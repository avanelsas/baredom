(ns barebuild.browser.server-resource-test
  "Element/integration tests for <server-resource> in a real browser (headless Chrome).
   Exercises the executor edge the pure step tests cannot: registration, connect, fetch,
   Conversion 1, notify fan-out, consumer render, writes, drift, popstate, and disconnect."
  (:require
   [barebuild.browser.support :as support]
   [cljs.test :refer-macros [deftest is use-fixtures async]]))

(use-fixtures :each
  {:before (fn [] (support/reset-state!) (support/capture-errors!))
   :after  (fn [] (support/restore-fetch!) (support/restore-error!)
             (support/reset-url!) (support/unmount-all!))})

(def ^:private owner-shape {"idKey" "id" "fields" [{"key" "owner" "type" "string"}]})
(def ^:private empty-shape {"idKey" "id" "fields" []})

(defn- error-logged? [needle]
  (some (fn [args] (some #(and (string? %) (re-find needle %)) args)) @support/error-calls))

(deftest server-resource-boots-fetches-and-renders
  (async done
    (support/stub-accepted! [{"id" 1 "owner" "Alice"}] owner-shape)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/spy-calls))
        (.then (fn []
                 (is (= 1 (count @support/fetch-calls))
                     "exactly one fetch is issued on connect")
                 (is (re-find #"/api/tasks\?requestId=tasks:1" (first @support/fetch-calls))
                     "the fetch carries the endpoint and the minted request id")
                 (is (= 1 (count @support/spy-calls))
                     "the consumer renders once, from the accepted response")
                 (is (= [{"id" 1 "owner" "Alice"}] (:value (first @support/spy-calls)))
                     "the rendered value is the parsed server payload")
                 (done)))
        (.catch (fn [e] (is false (str "pipeline failed to settle: " e)) (done))))))

(deftest a-throwing-consumer-does-not-starve-a-later-consumer
  (async done
    (support/stub-accepted! [{"id" 1 "owner" "Alice"}] owner-shape)
    ;; throwing consumer is first in document order, spy consumer second
    (support/mount-consumers! "tasks" "/api/tasks" ["x-throwing-consumer" "x-spy-consumer"])
    (-> (support/settle #(seq @support/spy-calls))
        (.then (fn []
                 (is (= 1 (count @support/spy-calls))
                     "the later consumer still renders despite the earlier one throwing")
                 (is (error-logged? #"applyResource threw")
                     "the throwing consumer is reported as a diagnostic")
                 (done)))
        (.catch (fn [e] (is false (str "later consumer was starved: " e)) (done))))))

(deftest a-server-resource-with-no-consumers-logs-an-error
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount-consumers! "tasks" "/api/tasks" [])
    (-> (support/settle #(error-logged? #"has no consumers"))
        (.then (fn [] (is true) (done)))
        (.catch (fn [e] (is false (str "no error for an empty registry: " e)) (done))))))

(deftest multiple-resources-fetch-only-their-own-scoped-query
  (async done
    (support/set-url! "?tasks.sort=owner&projects.sort=name")
    (support/stub-accepted! [] empty-shape)
    (support/mount-consumers! "tasks" "/api/tasks" ["x-spy-consumer"])
    (support/mount-consumers! "projects" "/api/projects" ["x-spy-consumer"])
    (-> (support/settle #(= 2 (count @support/fetch-calls)))
        (.then (fn []
                 (let [urls      @support/fetch-calls
                       tasks-url (some #(when (re-find #"/api/tasks" %) %) urls)
                       proj-url  (some #(when (re-find #"/api/projects" %) %) urls)]
                   (is (re-find #"[?&]sort=owner" tasks-url) "tasks fetched its own scoped query")
                   (is (not (re-find #"name" tasks-url)) "tasks did not leak the projects param")
                   (is (re-find #"[?&]sort=name" proj-url) "projects fetched its own scoped query")
                   (is (not (re-find #"owner" proj-url)) "projects did not leak the tasks param"))
                 (done)))
        (.catch (fn [e] (is false (str "resources did not both fetch: " e)) (done))))))

(deftest back-navigation-refetches-the-restored-intent
  (async done
    (support/set-url! "?tasks.sort=owner")
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(= 1 (count @support/fetch-calls)))
        (.then (fn []
                 (is (re-find #"[?&]sort=owner" (first @support/fetch-calls))
                     "the initial fetch reflects the booted url intent")
                 (support/pop-to! "?tasks.sort=start")
                 (support/settle #(= 2 (count @support/fetch-calls)))))
        (.then (fn []
                 (is (re-find #"[?&]sort=start" (last @support/fetch-calls))
                     "back navigation refetches the restored intent")
                 (done)))
        (.catch (fn [e] (is false (str "back navigation did not refetch: " e)) (done))))))

(deftest a-write-installs-the-returned-collection-state
  (async done
    (support/respond-with!
     (fn [url method _body]
       (support/accepted url
                         (if (= method "GET")
                           [{"id" 1 "title" "a"}]
                           [{"id" 1 "title" "a"} {"id" 2 "title" "b"}])
                         {"idKey" "id" "fields" [{"key" "title" "type" "string"}]})))
    (let [host (support/mount! "tasks" "/api/tasks")]
      (-> (support/settle #(= 1 (count @support/spy-calls)))
          (.then (fn []
                   (support/submit-write! host {:op :create :record {"title" "b"}})
                   (support/settle #(= 2 (count @support/spy-calls)))))
          (.then (fn []
                   (is (= 2 (count (:value (last @support/spy-calls))))
                       "the consumer re-renders the post-write collection the ack returned")
                   (done)))
          (.catch (fn [e] (is false (str "write did not install: " e)) (done)))))))

(deftest an-intent-change-mid-flight-trails-a-single-fetch
  (async done
    (support/set-url! "?tasks.sort=owner")
    (support/stub-controlled!)
    (let [host (support/mount! "tasks" "/api/tasks")]
      (-> (support/settle #(= 1 (count @support/fetch-calls)))     ; first GET is in flight
          (.then (fn []
                   ;; a new intent arrives before the first response
                   (support/submit-intent! host {:query-patch {:sort "start"} :gesture-class :navigation})
                   ;; the first response now returns, for the superseded intent
                   (support/resolve-nth! 0 [] empty-shape)
                   (support/settle #(= 2 (count @support/fetch-calls)))))
          (.then (fn []
                   (is (re-find #"[?&]sort=start" (last @support/fetch-calls))
                       "exactly one trailing fetch fires, for the newest intent")
                   (done)))
          (.catch (fn [e] (is false (str "supersede did not trail a fetch: " e)) (done)))))))

(deftest disconnect-aborts-an-in-flight-request
  (async done
    (support/stub-controlled!)
    (let [host (support/mount! "tasks" "/api/tasks")]
      (-> (support/settle #(= 1 (count @support/fetch-calls)))
          (.then (fn []
                   (.remove host)                    ; disconnect while the GET is in flight
                   (support/settle #(seq @support/aborted-calls))))
          (.then (fn []
                   (is (re-find #"/api/tasks" (first @support/aborted-calls))
                       "disconnecting aborts the pending request")
                   (done)))
          (.catch (fn [e] (is false (str "in-flight request was not aborted: " e)) (done)))))))
