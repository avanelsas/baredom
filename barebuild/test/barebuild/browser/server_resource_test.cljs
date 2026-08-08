(ns barebuild.browser.server-resource-test
  "Element/integration tests for <server-resource> in a real browser (headless Chrome).
   Exercises the executor edge the pure step tests cannot: registration, connect, fetch,
   Conversion 1, notify fan-out, consumer render, writes, drift, popstate, and disconnect."
  (:require
   [barebuild.browser.support :as support]
   [barebuild.core :as core]
   [barebuild.decorator :as decorator]
   [baredom.utils.component :as component]
   [cljs.test :refer-macros [deftest is testing use-fixtures async]]))

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
    (-> (support/settle #(some (comp :accepted) @support/spy-calls))
        (.then (fn []
                 (is (= 1 (count @support/fetch-calls))
                     "exactly one fetch is issued on connect")
                 (is (re-find #"/api/tasks\?requestId=tasks:1" (first @support/fetch-calls))
                     "the fetch carries the endpoint and the minted request id")
                 (is (nil? (:accepted (first @support/spy-calls)))
                     "the consumer is projected at connect, before any response, so it can paint
                      an empty state from the view rather than waiting on data")
                 (is (= [{"id" 1 "owner" "Alice"}] (:value (:accepted (last @support/spy-calls))))
                     "and again once the parsed server payload is installed")
                 (done)))
        (.catch (fn [e] (is false (str "pipeline failed to settle: " e)) (done))))))

(deftest an-undefined-element-inside-a-nested-resource-does-not-block-the-boot
  (async done
    (testing "the boot waits for the custom elements inside the host to be defined, because a
              consumer cannot be recognised before it is. It waits only for the ones it owns: an
              element belonging to a nested <server-resource> is that resource's business, and
              blocking on it would leave this one issuing no request at all"
      (support/stub-accepted! [] empty-shape)
      (let [host     (js/document.createElement "server-resource")
            consumer (js/document.createElement "x-spy-consumer")
            nested   (js/document.createElement "server-resource")]
        (.setAttribute host "resource-id" "outer")
        (.setAttribute host "src" "/api/outer")
        (.appendChild consumer (js/document.createElement "div"))
        (.appendChild host consumer)
        (.setAttribute nested "resource-id" "inner")
        (.setAttribute nested "src" "/api/inner")
        (.appendChild nested (js/document.createElement "x-never-defined"))
        (.appendChild host nested)
        (.appendChild js/document.body host)
        (-> (support/settle #(seq @support/fetch-calls))
            (.then (fn []
                     (is (= 1 (count @support/fetch-calls))
                         "the outer resource booted and fetched")
                     (is (re-find #"/api/outer" (first @support/fetch-calls))
                         "and it is the outer resource that did, the inner one still waiting on
                          the element it does own")
                     (done)))
            (.catch (fn [e] (is false (str "the boot never fetched: " e)) (done))))))))

(deftest a-garbled-request-id-echo-still-installs
  (async done
    (testing "the client matches a response to its request by the id it minted, not by the
              server's echo. A server that garbles the echo used to leave every response looking
              stale, and since a stale response clears nothing, the read stayed in flight and the
              element hung pending for good"
      (support/respond-with!
       (fn [url _method _body]
         (assoc (support/accepted url [{"id" 1 "owner" "Alice"}] owner-shape)
                "requestId" "not-the-id-we-minted")))
      (support/mount! "tasks" "/api/tasks")
      (-> (support/settle #(some (comp :accepted) @support/spy-calls))
          (.then (fn []
                   (let [view (last @support/spy-calls)]
                     (is (= [{"id" 1 "owner" "Alice"}] (:value (:accepted view)))
                         "the response installs")
                     (is (false? (:pending? view))
                         "and the resource has stopped pending, rather than waiting on a read
                          that can never be answered"))
                   (done)))
          (.catch (fn [e] (is false (str "pipeline failed to settle: " e)) (done)))))))

(deftest a-notification-that-changes-nothing-does-not-re-render
  (async done
    (support/stub-accepted! [{"id" 1 "owner" "Alice"}] owner-shape)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(some (comp :accepted) @support/spy-calls))
        (.then (fn []
                 (let [renders (count @support/spy-calls)]
                   ;; three navigations to the url the resource already holds. Each one notifies
                   ;; consumers and none of them changes the value, so the guard has to hold.
                   ;; This runs synchronously: popstate to step to notify to applyResource.
                   (dotimes [_ 3] (support/pop-to! ""))
                   (is (= renders (count @support/spy-calls))
                       "render fires when the slice it draws changes, not on every notification")
                   (is (= 1 (count @support/fetch-calls))
                       "and an already-answered intent issues no new request")
                   (done))))
        (.catch (fn [e] (is false (str "guard never settled: " e)) (done))))))

(deftest a-throwing-consumer-does-not-starve-a-later-consumer
  (async done
    (support/stub-accepted! [{"id" 1 "owner" "Alice"}] owner-shape)
    ;; throwing consumer is first in document order, spy consumer second
    (support/mount-consumers! "tasks" "/api/tasks" ["x-throwing-consumer" "x-spy-consumer"])
    (-> (support/settle #(some (comp :accepted) @support/spy-calls))
        (.then (fn []
                 (is (= [{"id" 1 "owner" "Alice"}] (:value (:accepted (last @support/spy-calls))))
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

(deftest a-targeted-intent-drives-the-named-sibling-not-the-sender
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [projects (support/mount-consumers! "projects" "/api/projects" ["x-spy-consumer"])]
      (support/mount-consumers! "tasks" "/api/tasks" ["x-spy-consumer"])
      (-> (support/settle #(= 2 (count @support/fetch-calls)))
          (.then (fn []
                   ;; a gesture in the projects resource selecting a project for the tasks one
                   (support/submit-intent! projects {:query-patch {:project "p-1"}} "tasks")
                   (support/settle #(= 3 (count @support/fetch-calls)))))
          (.then (fn []
                   (let [refetch (last @support/fetch-calls)]
                     (is (re-find #"/api/tasks" refetch)
                         "the sibling named in the patch is the one that refetched")
                     (is (re-find #"[?&]project=p-1" refetch)
                         "carrying the routed patch as its own query"))
                   (is (re-find #"[?&]tasks\.project=p-1" (.-search js/location))
                       "and the url is written in the target's scope, not the sender's")
                   (done)))
          (.catch (fn [e] (is false (str "the intent did not reach the sibling: " e)) (done)))))))

(deftest a-targeted-intent-reaches-an-id-that-would-break-a-selector
  (testing "the target is resolved by an attribute selector built from the id it was named by, so
            an id carrying a quote has to be escaped into it. Unescaped, the quote closes the
            selector's string early and querySelector throws instead of routing"
    (async done
      (support/stub-accepted! [] empty-shape)
      (let [odd-id "od\"d"
            sender (support/mount-consumers! "tasks" "/api/tasks" ["x-spy-consumer"])]
        (support/mount-consumers! odd-id "/api/odd" ["x-spy-consumer"])
        (-> (support/settle #(= 2 (count @support/fetch-calls)))
            (.then (fn []
                     (support/submit-intent! sender {:query-patch {:sort "owner"}} odd-id)
                     (support/settle #(= 3 (count @support/fetch-calls)))))
            (.then (fn []
                     (is (re-find #"/api/odd" (last @support/fetch-calls))
                         "the sibling named by the awkward id is the one that refetched")
                     (done)))
            (.catch (fn [e]
                      (is false (str "the intent did not reach the odd id: " e))
                      (done))))))))

(deftest a-consumer-moved-under-another-resource-submits-through-that-one
  (testing "a consumer caches the ctx it is applied with, so the cache has to follow the ctx it is
            handed. Cached write-once, a consumer that changes hands keeps calling back into the
            resource it left and its gestures drive the wrong element"
    (async done
      (support/stub-accepted! [] empty-shape)
      (let [alpha (support/mount-consumers! "alpha" "/api/alpha" ["x-spy-consumer"])
            beta  (js/document.createElement "server-resource")]
        (.setAttribute beta "resource-id" "beta")
        (.setAttribute beta "src" "/api/beta")
        (-> (support/settle #(some (comp :accepted) @support/spy-calls))
            (.then (fn []
                     ;; the move. beta collects the consumer at its own boot and applies its ctx,
                     ;; over the one alpha left behind
                     (.appendChild beta (.querySelector alpha "x-spy-consumer"))
                     (.appendChild js/document.body beta)
                     (support/settle #(= 2 (count @support/fetch-calls)))))
            (.then (fn []
                     (support/submit-intent! beta {:query-patch {:sort "owner"}})
                     (support/settle #(= 3 (count @support/fetch-calls)))))
            (.then (fn []
                     (let [refetch (last @support/fetch-calls)]
                       (is (re-find #"/api/beta" refetch)
                           "the gesture drives the resource the consumer now belongs to")
                       (is (not (re-find #"/api/alpha" refetch))
                           "and not the one that applied it first"))
                     (is (re-find #"[?&]beta\.sort=owner" (.-search js/location))
                         "with the url written in the new resource's scope")
                     (done)))
            (.catch (fn [e]
                      (is false (str "the moved consumer did not repoint: " e))
                      (done))))))))

(deftest every-record-names-the-resource-that-stepped
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/capture-records!)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/records))
        (.then (fn []
                 (is (= #{"tasks"} (set (map :resource/id @support/records)))
                     "so a trace can be grouped by resource without reaching into the value")
                 (done)))
        (.catch (fn [e] (is false (str "nothing reached the recorder: " e)) (done))))))

(deftest a-resource-id-a-query-cannot-carry-is-reported-and-dropped
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "a&b" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-calls))
        (.then (fn []
                 (is (error-logged? #"cannot go in a request query")
                     "the author is told where the mistake is")
                 (is (re-find #"requestId=:1" (first @support/fetch-calls))
                     "and the id never reaches the query, the element booting unnamed instead")
                 (done)))
        (.catch (fn [e] (is false (str "the bad id was not reported: " e)) (done))))))

(deftest an-intent-naming-a-resource-that-is-not-there-does-not-vanish-silently
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [host (support/mount! "tasks" "/api/tasks")]
      (-> (support/settle #(= 1 (count @support/fetch-calls)))
          (.then (fn []
                   ;; routing is synchronous: submit, then read what it did
                   (support/capture-records!)
                   (support/submit-intent! host {:query-patch {:sort "owner"}} "ghost")
                   (let [events (support/recorded-events)]
                     (is (some (fn [[k _]] (= :intent-unroutable k)) events)
                         "the undeliverable gesture reaches the trace as an event rather than
                          evaporating in a closure at the edge")
                     (is (= {:resource/id "ghost"}
                            (some (fn [[k m]] (when (= :intent-unroutable k) m)) events))
                         "naming the target that could not be resolved"))
                   (is (= 1 (count @support/fetch-calls))
                       "an unroutable intent issues no request")
                   (is (= "" (.-search js/location))
                       "and never writes the sender's url with a patch meant for someone else")
                   (done)))
          (.catch (fn [e] (is false (str "unroutable intent misbehaved: " e)) (done)))))))

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
    (let [host      (support/mount! "tasks" "/api/tasks")
          ;; settle on the rows rendered, not on a call count: the count now passes through the
          ;; empty connect projection too, and polling for an exact count can step over it
          rows-shown (fn [n] #(some (fn [v] (= n (count (:value (:accepted v)))))
                                    @support/spy-calls))]
      (-> (support/settle (rows-shown 1))
          (.then (fn []
                   (support/submit-write! host {:op :create :record {"title" "b"}})
                   (support/settle (rows-shown 2))))
          (.then (fn []
                   (is (= 2 (count (:value (:accepted (last @support/spy-calls)))))
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

(deftest a-disconnect-during-the-notify-still-aborts-the-read-that-step-opened
  (async done
    (support/stub-controlled!)
    ;; The consumer removes the host from inside its render, and removal fires
    ;; disconnectedCallback synchronously, so :disconnected is stepped while the connect's own
    ;; effects are still being performed.
    (support/mount-consumers! "tasks" "/api/tasks" ["x-self-removing-consumer"])
    (-> (support/settle #(seq @support/aborted-calls))
        (.then (fn []
                 (is (= 1 (count @support/fetch-calls))
                     "the read step chose is still issued, the nested step does not cancel it")
                 (is (re-find #"/api/tasks" (first @support/aborted-calls))
                     "and the abort reaches it rather than finding nothing to abort")
                 (done)))
        (.catch (fn [e]
                  (is false (str "the read outlived the disconnect that ordered its abort: " e))
                  (done))))))

;; --- boot is tied to the connection that asked for it ----------------------
;;
;; connectedCallback defers boot until every custom element inside the host is defined, so a
;; disconnect can land in that window. These mount a host containing a tag that is deliberately
;; undefined at mount time, so the boot is still pending when the test moves the element, and
;; define it afterwards to release the boot.

(defonce ^:private late-tag-counter (atom 0))

(defn- define-late!
  "Define `tag` as a do-nothing custom element, releasing any boot awaiting it."
  [tag]
  (component/register! tag {:observed-attributes  #js []
                            :connected-fn         (fn [_el] nil)
                            :attribute-changed-fn (fn [_el _n _o _v] nil)}))

(defn- mount-awaiting!
  "A <server-resource> holding a spy consumer plus a child whose tag is not defined yet, so its
   boot stays pending. Returns [host tag]."
  []
  (let [tag  (str "x-late-" (swap! late-tag-counter inc))
        host (support/mount! "tasks" "/api/tasks")]
    (.appendChild host (js/document.createElement tag))
    [host tag]))

(deftest a-boot-that-resolves-after-removal-does-not-run
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [[host tag] (mount-awaiting!)]
      (is (zero? (count @support/fetch-calls)) "boot is pending on the undefined child")
      (.remove host)
      (define-late! tag)
      (-> (support/settle #(true? true))
          (.then (fn []
                   (is (zero? (count @support/fetch-calls))
                       "a boot whose connection already ended issues no request")
                   (support/pop-to! "?tasks.sort=owner")
                   (support/settle #(true? true))))
          (.then (fn []
                   (is (zero? (count @support/fetch-calls))
                       "and left no popstate listener behind on a detached element")
                   (done)))
          (.catch (fn [e] (is false (str "detached boot misbehaved: " e)) (done)))))))

(deftest a-reattached-element-boots-once-and-listens-once
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [[host tag] (mount-awaiting!)]
      (.remove host)
      (.appendChild js/document.body host)          ; re-attached before the boot is released
      (define-late! tag)
      (-> (support/settle #(= 1 (count @support/fetch-calls)))
          (.then (fn []
                   (is (= 1 (count @support/fetch-calls))
                       "only the live connection boots, the superseded one is dropped")
                   (support/pop-to! "?tasks.sort=owner")
                   (support/settle #(= 2 (count @support/fetch-calls)))))
          (.then (fn []
                   (is (= 2 (count @support/fetch-calls))
                       "one popstate listener, so a navigation refetches once, not twice")
                   (done)))
          (.catch (fn [e] (is false (str "re-attach left duplicate listeners: " e)) (done)))))))

(deftest a-write-in-flight-survives-a-reconnect-and-its-ack-still-lands
  (testing "disconnect leaves an in-flight write running because its outcome still matters, so
            the boot carries the slot and counter naming it. Without that the rebuilt value knows
            of no write, the restarted counter mints the orphan's own id for the next one, and
            that ack installs as though it answered a write the user made after reconnecting"
    (async done
      (support/stub-controlled!)
      (let [shape  {"idKey" "id" "fields" [{"key" "title" "type" "string"}]}
            view   (fn [] (last @support/spy-calls))
            rows   (fn [] (:value (:accepted (view))))
            titles (fn [] (mapv #(get % "title") (rows)))
            host   (support/mount! "tasks" "/api/tasks")]
        (-> (support/settle #(= 1 (count @support/fetch-calls)))
            (.then (fn []
                     (support/resolve-nth! 0 [{"id" 1 "title" "a"}] shape)      ; the boot read
                     (support/settle #(= 1 (count (rows))))))
            (.then (fn []
                     (support/submit-write! host {:op :create :record {"title" "orphan"}})
                     (support/settle #(= 2 (count @support/fetch-calls)))))
            (.then (fn []
                     (.remove host)                          ; the write is still in flight
                     (.appendChild js/document.body host)    ; and outlives the connection
                     (support/settle #(= 3 (count @support/fetch-calls)))))
            (.then (fn []
                     (support/resolve-nth! 2 [{"id" 1 "title" "a"}] shape)      ; the reboot read
                     (support/settle #(= 1 (count (rows))))))
            (.then (fn []
                     (is (true? (:writing? (view)))
                         "the reconnected element still reports the write it never heard back on")
                     ;; single flight spans the reconnect too, so this one is refused rather than
                     ;; sent under an id the orphan would answer to
                     (support/submit-write! host {:op :create :record {"title" "refused"}})
                     (support/resolve-nth! 1 [{"id" 1 "title" "a"} {"id" 2 "title" "ORPHAN"}] shape)
                     (support/settle #(= 2 (count (rows))))))
            (.then (fn []
                     (is (= ["a" "ORPHAN"] (titles))
                         "the ack the user was waiting on installs instead of being dropped")
                     (is (= 3 (count @support/fetch-calls))
                         "the second write never went out, one write is in flight at a time")
                     (is (false? (:writing? (view))) "and the slot is free again")
                     (support/submit-write! host {:op :create :record {"title" "next"}})
                     (support/settle #(= 4 (count @support/fetch-calls)))))
            (.then (fn []
                     (is (re-find #"requestId=tasks:w2$" (nth @support/fetch-calls 3))
                         "the counter kept counting across the boot, so the next write cannot
                          mint an id an earlier one already answers to")
                     (done)))
            (.catch (fn [e]
                      (is false (str "reconnect write test did not settle: " e))
                      (done))))))))

(defn- first-failure []
  (first (remove nil? @support/failure-calls)))

(deftest an-http-error-surfaces-with-its-status
  (async done
    (support/stub-status! 401)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (let [f (first-failure)]
                   (is (= :network (:cause f)) "an http error is a network failure")
                   (is (= :http-status (get-in f [:error :kind])) "carrying the http-status kind")
                   (is (= 401 (get-in f [:error :status])) "and the status code, so a consumer can react"))
                 (done)))
        (.catch (fn [e] (is false (str "http error did not surface: " e)) (done))))))

(deftest a-transport-rejection-surfaces-as-offline
  (async done
    (support/stub-reject!)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (is (= :offline (get-in (first-failure) [:error :kind]))
                     "a genuine transport rejection is offline, distinct from an http status")
                 (done)))
        (.catch (fn [e] (is false (str "transport rejection did not surface: " e)) (done))))))

(deftest an-unreadable-body-surfaces-as-a-protocol-failure
  (async done
    (testing "an ok response whose body is not an envelope is classified at the edge, before step
              sees it. Handed in as a plain response instead, it reaches on-response carrying no
              outcome and is diagnosed :unknown-outcome, which tells the consumer the server said
              something unexpected rather than that the answer could not be read at all"
      (support/stub-body! "not json at all")
      (support/mount! "tasks" "/api/tasks")
      (-> (support/settle #(seq (remove nil? @support/failure-calls)))
          (.then (fn []
                   (let [f (first-failure)]
                     (is (= :protocol (:cause f))
                         "an unreadable answer is a protocol failure, not the server saying no")
                     (is (= :read (:for f))
                         "and the read is what failed, the discriminator a consumer branches on")
                     (is (= :malformed-json (get-in f [:detail :reason]))
                         "carrying what made it unreadable, so an author can tell a truncated
                          response from a proxy's html error page"))
                   (done)))
          (.catch (fn [e] (is false (str "an unreadable body never surfaced: " e)) (done)))))))

(deftest a-write-renders-before-it-reports-that-writing-finished
  (async done
    (support/respond-with!
     (fn [url method _body]
       (support/accepted url
                         (if (= method "GET") [{"id" 1 "title" "a"}] [{"id" 1 "title" "a"}
                                                                      {"id" 2 "title" "b"}])
                         {"idKey" "id" "fields" [{"key" "title" "type" "string"}]})))
    (let [host        (support/mount-consumers! "tasks" "/api/tasks"
                                                ["x-spy-consumer" "x-order-consumer"])
          writings    (fn [] (count (filter #{:on-writing} @support/order-calls)))
          last-idx-of (fn [k] (last (keep-indexed (fn [i c] (when (= k c) i))
                                                  @support/order-calls)))]
      (-> (support/settle #(some :accepted @support/spy-calls))
          (.then (fn []
                   (let [before (writings)]
                     (support/submit-write! host {:op :create :record {"title" "b"}})
                     ;; the write adds two of its own: true when it is submitted, false again on
                     ;; the ack. Counted from whatever the boot apply already reported, so this
                     ;; waits for the ack rather than stopping on the submit
                     (support/settle #(= (+ before 2) (writings)) 200))))
          (.then (fn []
                   ;; nil sorts below any number in CLJS, so prove both hooks actually ran
                   (is (some? (last-idx-of :render)) "the order consumer rendered")
                   (is (some? (last-idx-of :on-writing)) "and was told about the write")
                   (is (< (last-idx-of :render) (last-idx-of :on-writing))
                       "the post-write data is painted before a consumer is told the write
                        finished, so a component that defers work while a write is in flight can
                        resume on the true->false edge and find the new value already rendered")
                   (done)))
          (.catch (fn [e] (is false (str "write ordering never settled: " e)) (done)))))))

(defn- settled-writings
  "What each writing consumer made of the write, the boot apply (no :write yet) dropped."
  []
  (remove (comp nil? :status) @support/writer-calls))

(deftest a-settled-write-is-only-its-own-submitters
  (async done
    (testing "a write moves :writing? for every consumer the resource drives, so that edge alone
              says only that some write finished. :write names the submitter, so own-write? tells
              them apart without each keeping a flag"
      (support/respond-with!
       (fn [url _method _body]
         (support/accepted url [{"id" 1 "title" "a"}]
                           {"idKey" "id" "fields" [{"key" "title" "type" "string"}]})))
      (let [host (support/mount-consumers! "tasks" "/api/tasks"
                                           ["x-spy-consumer" "x-writer-a-consumer"
                                            "x-writer-b-consumer"])]
        (-> (support/settle #(some :accepted @support/spy-calls))
            (.then (fn []
                     (support/submit-write-from! host "x-writer-a-consumer"
                                                 {:op :create :record {"title" "b"}})
                     ;; both consumers see the write open and settle, so four in total
                     (support/settle #(= 4 (count (settled-writings))) 200)))
            (.then (fn []
                     (let [by-tag (group-by :tag (settled-writings))]
                       (is (= [{:tag "x-writer-a-consumer" :own? true :status :in-flight}
                               {:tag "x-writer-a-consumer" :own? true :status :accepted}]
                              (get by-tag "x-writer-a-consumer"))
                           "the submitter recognises the write as its own, in flight and accepted")
                       (is (= [{:tag "x-writer-b-consumer" :own? false :status :in-flight}
                               {:tag "x-writer-b-consumer" :own? false :status :accepted}]
                              (get by-tag "x-writer-b-consumer"))
                           "the other is told about the same write and can see it is not its own"))
                     (done)))
            (.catch (fn [e] (is false (str "the write never settled: " e)) (done))))))))

(deftest a-submitter-stamp-never-reaches-the-server
  (async done
    (testing "the stamp is how consumers tell their writes apart, not something the server was
              asked about. A request body is built from the payload's :record alone"
      (support/respond-with!
       (fn [url _method _body]
         (support/accepted url [] {"idKey" "id" "fields" []})))
      (let [host  (support/mount-consumers! "tasks" "/api/tasks"
                                            ["x-spy-consumer" "x-writer-a-consumer"])
            posts (fn [] (filter #(= "POST" (.-method ^js %)) @support/fetch-inits))]
        (-> (support/settle #(some :accepted @support/spy-calls))
            (.then (fn []
                     (support/submit-write-from! host "x-writer-a-consumer"
                                                 {:op :create :record {"title" "b"}})
                     (support/settle #(seq (posts)) 200)))
            (.then (fn []
                     (let [body (.-body ^js (first (posts)))]
                       (is (= {"title" "b"} (js->clj (js/JSON.parse body)))
                           "the record alone, no :submitter and no :op alongside it"))
                     (done)))
            (.catch (fn [e] (is false (str "the write never went out: " e)) (done))))))))

(deftest every-hook-is-initialised-on-the-first-apply
  (async done
    (testing "one rule for every hook: it fires once on the first apply whatever its slice holds,
              and after that only when that slice moves. Without it a hook whose slice starts at
              its resting value (writing? false, failure nil) would never be called at all, and a
              consumer could not tell being told nothing from being told false"
      (support/stub-controlled!)
      (support/mount-consumers! "tasks" "/api/tasks" ["x-order-consumer"])
      (-> (support/settle #(= 4 (count @support/order-calls)))
          (.then (fn []
                   (is (= [:on-failure :render :on-pending :on-writing] @support/order-calls)
                       "all four fire, in the contract order, from the boot apply alone. The read
                        is still in flight, so nothing but that first apply has happened")
                   (done)))
          (.catch (fn [e] (is false (str "the boot apply never initialised every hook: " e))
                    (done)))))))

(deftest on-connect-is-handed-the-driven-child
  (testing "on-connect is handed what every other hook is handed, the child the consumer declared,
            so wiring a gesture never re-queries an element the config already names"
    (support/stub-accepted! [] empty-shape)
    (let [host          (support/mount-consumers! "tasks" "/api/tasks" ["x-connect-consumer"])
          ^js consumer  (.querySelector host "x-connect-consumer")
          [[child this]] @support/connect-calls]
      (is (identical? (.querySelector consumer "div") child)
          "the declared child, already cached when on-connect runs")
      (is (identical? consumer this) "alongside the consumer itself"))))

(deftest a-gesture-before-the-resource-boots-is-reported
  (async done
    (testing "a consumer's listeners are live the moment it connects, while its host is still
              waiting on the custom elements inside it to be defined. A gesture in that window has
              no resource to submit through, so it is reported like every other misuse rather than
              thrown out of the handler"
      (support/stub-accepted! [] empty-shape)
      (let [host (support/mount! "tasks" "/api/tasks")]
        ;; boot waits on whenDefined, so this runs before the first apply installs the ctx
        (support/submit-intent! host {:query-patch {:sort "owner"}})
        (is (error-logged? #"before its resource booted") "the gesture is reported")
        (-> (support/settle #(= 1 (count @support/fetch-calls)))
            (.then (fn []
                     (is (not (re-find #"sort=owner" (first @support/fetch-calls)))
                         "and it does not reach the resource once that boots")
                     (done)))
            (.catch (fn [e] (is false (str "the boot fetch never settled: " e)) (done))))))))

;; --- transport config: attributes reach fetch ------------------------------

(defn- first-init [] (first @support/fetch-inits))

(deftest configured-credentials-and-headers-reach-fetch
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks" {"credentials" "include"
                                          "headers"     "{\"X-Api-Key\": \"k\"}"})
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (let [^js init (first-init)]
                   (is (= "include" (.-credentials init))
                       "the credentials mode is set on the request, so a cookie can cross origins")
                   (is (= {"x-api-key" "k"} (js->clj (.-headers init)))
                       "the static headers ride the request, lowercased"))
                 (done)))
        (.catch (fn [e] (is false (str "configured request never fired: " e)) (done))))))

(deftest configured-headers-ride-a-write-beside-the-content-type
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [host (support/mount! "tasks" "/api/tasks" {"headers" "{\"x-api-key\": \"k\"}"})]
      (-> (support/settle #(seq @support/spy-calls))
          (.then (fn []
                   (support/submit-write! host {:op :create :record {"title" "x"}})
                   (support/settle #(= 2 (count @support/fetch-inits)))))
          (.then (fn []
                   (is (= {"x-api-key" "k" "content-type" "application/json"}
                          (js->clj (.-headers ^js (second @support/fetch-inits))))
                       "a write carries the author's headers and the protocol content-type")
                   (done)))
          (.catch (fn [e] (is false (str "write never fired: " e)) (done)))))))

(deftest an-unconfigured-resource-sends-no-transport-keys
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (let [^js init (first-init)]
                   (is (undefined? (.-credentials init))
                       "no credentials key, so the browser default applies untouched")
                   (is (undefined? (.-headers init))
                       "and no empty headers object on a bodiless read"))
                 (done)))
        (.catch (fn [e] (is false (str "unconfigured request never fired: " e)) (done))))))

(deftest a-malformed-headers-attribute-is-reported-and-ignored
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks" {"headers" "not json"})
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (undefined? (.-headers ^js (first-init)))
                     "the request still fires, without headers, config is not a hard failure")
                 (is (error-logged? #"is not a JSON object")
                     "and the author is told which attribute was ignored")
                 (done)))
        (.catch (fn [e] (is false (str "malformed config blocked the request: " e)) (done))))))

;; --- the request decorator: dynamic headers at the edge --------------------

(defn- headers-of [^js init] (js->clj (.-headers init)))

(defn- methods-sent
  "The HTTP method of every request the stub received, in order."
  []
  (mapv (fn [^js init] (.-method init)) @support/fetch-inits))

(deftest an-async-decorator-is-awaited-before-the-request-goes-out
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install!
     (fn [_request] (js/Promise.resolve {"Authorization" "Bearer fresh"})))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (= {"authorization" "Bearer fresh"} (headers-of (first-init)))
                     "the request waits for the token and carries it, lowercased like any header")
                 (done)))
        (.catch (fn [e] (is false (str "decorated request never fired: " e)) (done))))))

(deftest a-decorator-may-return-headers-directly
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] {"authorization" "Bearer plain"}))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (= {"authorization" "Bearer plain"} (headers-of (first-init)))
                     "a plain map works too, so a hook that needs no refresh stays synchronous")
                 (done)))
        (.catch (fn [e] (is false (str "decorated request never fired: " e)) (done))))))

(deftest a-decorator-may-return-a-js-object
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] #js {"Authorization" "Bearer js"}))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (= {"authorization" "Bearer js"} (headers-of (first-init)))
                     "a #js literal is as natural to write at the edge as a map, and dropping it
                      silently would surface only as an unexplained 401")
                 (done)))
        (.catch (fn [e] (is false (str "js-object headers never arrived: " e)) (done))))))

(deftest a-decorator-returning-something-that-is-not-headers-is-reported
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] "authorization: nope"))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (undefined? (.-headers ^js (first-init)))
                     "the request still goes out, without headers")
                 (is (error-logged? #"returned no map of headers")
                     "and the author is told, rather than debugging a silent 401")
                 (done)))
        (.catch (fn [e] (is false (str "a bad decorator return blocked the request: " e)) (done))))))

(deftest a-decorator-sees-the-request-it-is-decorating
  (async done
    (support/stub-accepted! [] empty-shape)
    (let [seen (atom nil)]
      (decorator/install! (fn [request] (reset! seen request) nil))
      (support/mount! "tasks" "/api/tasks")
      (-> (support/settle #(some? @seen))
          (.then (fn []
                   (is (= "GET" (:method @seen)) "the method, so one hook can serve several APIs")
                   (is (re-find #"/api/tasks" (:url @seen)) "and the url it is about to be sent to")
                   (done)))
          (.catch (fn [e] (is false (str "decorator never ran: " e)) (done)))))))

(deftest a-decorator-outranks-the-static-header-but-not-the-content-type
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install!
     (fn [_request] {"x-api-key" "dynamic" "content-type" "text/plain"}))
    (let [host (support/mount! "tasks" "/api/tasks" {"headers" "{\"x-api-key\": \"static\"}"})]
      (-> (support/settle #(seq @support/spy-calls))
          (.then (fn []
                   (support/submit-write! host {:op :create :record {"title" "x"}})
                   (support/settle #(= 2 (count @support/fetch-inits)))))
          (.then (fn []
                   (is (= {"x-api-key" "dynamic" "content-type" "application/json"}
                          (headers-of (second @support/fetch-inits)))
                       "the dynamic header wins over the static one, the write contract wins over both")
                   (done)))
          (.catch (fn [e] (is false (str "decorated write never fired: " e)) (done)))))))

(deftest a-failing-decorator-fails-the-request-instead-of-sending-it
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] (js/Promise.reject (js/Error. "no token"))))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (let [f (first-failure)]
                   (is (= :network (:cause f)) "it reaches the consumer as a network failure")
                   (is (= :decorator (get-in f [:error :kind]))
                       "with its own kind, so an app can tell it from being offline"))
                 (is (empty? @support/fetch-calls)
                     "and nothing was sent, since an uncredentialed request would only 401")
                 (done)))
        (.catch (fn [e] (is false (str "decorator failure never surfaced: " e)) (done))))))

(deftest a-throwing-decorator-fails-the-same-way-as-a-rejecting-one
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] (throw (js/Error. "boom"))))
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (is (= :decorator (get-in (first-failure) [:error :kind]))
                     "a synchronous throw cannot escape into the lifecycle callback that ran it")
                 (done)))
        (.catch (fn [e] (is false (str "throwing decorator was not contained: " e)) (done))))))

(deftest disconnecting-while-the-decorator-is-awaited-reaches-the-request
  (async done
    (support/stub-controlled!)
    (let [release (atom nil)]
      (decorator/install!
       (fn [_request] (js/Promise. (fn [resolve _] (reset! release #(resolve nil))))))
      (let [host (support/mount! "tasks" "/api/tasks")]
        (-> (support/settle #(some? @release))
            (.then (fn []
                     (.remove host)          ; disconnect while the token is still being fetched
                     (@release)
                     (support/settle #(seq @support/aborted-calls))))
            (.then (fn []
                     (is (= 1 (count @support/aborted-calls))
                         "the controller is stashed before the decorator is awaited, so a
                          disconnect landing in that window still reaches the fetch behind it")
                     (done)))
            (.catch (fn [e] (is false (str "abort during decoration was mishandled: " e)) (done))))))))

;; --- the request budget ----------------------------------------------------

(deftest a-read-that-never-answers-gives-up-on-its-budget
  (async done
    (support/stub-controlled!)
    (support/mount! "tasks" "/api/tasks" {"timeout" "30"})
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (let [f (first-failure)]
                   (is (= :network (:cause f)) "a spent budget reaches the consumer as a failure")
                   (is (= :timeout (get-in f [:error :kind]))
                       "with its own kind, distinct from being offline")
                   (is (= 30 (get-in f [:error :after])) "carrying the budget it outlived"))
                 (is (seq @support/aborted-calls) "and the socket is released, not left hanging")
                 (done)))
        (.catch (fn [e] (is false (str "a hung read never gave up: " e)) (done))))))

(deftest a-spent-budget-unwedges-the-resource
  (async done
    (support/stub-controlled!)
    (let [host (support/mount! "tasks" "/api/tasks" {"timeout" "30"})]
      (-> (support/settle #(seq (remove nil? @support/failure-calls)))
          (.then (fn []
                   (support/submit-intent! host {:query-patch {:sort "owner"}})
                   (support/settle #(= 2 (count @support/fetch-calls)))))
          (.then (fn []
                   (is (re-find #"sort=owner" (second @support/fetch-calls))
                       "a gesture after the timeout fetches again, which a request still counted
                        as in flight would have blocked for the life of the element")
                   (done)))
          (.catch (fn [e] (is false (str "the resource stayed wedged: " e)) (done)))))))

(deftest a-write-that-never-answers-gives-up-on-its-budget
  (async done
    (support/respond-with! (fn [url method _body]
                             (if (= "POST" method)
                               (js/Promise. (fn [_resolve _reject] nil))   ; never answers
                               (support/accepted url [] empty-shape))))
    ;; the budget has to be wide enough that the boot *read* comfortably beats it on a loaded
    ;; machine, since only the write is meant to run out of time here
    (let [host (support/mount! "tasks" "/api/tasks" {"timeout" "100"})]
      (-> (support/settle #(seq @support/spy-calls))
          (.then (fn []
                   (support/submit-write! host {:op :create :record {"title" "x"}})
                   (support/settle #(seq (remove nil? @support/failure-calls)) 200)))
          (.then (fn []
                   (is (= :timeout (get-in (first-failure) [:error :kind]))
                       "a hung write gives up too, releasing the single-flight write slot that
                        would otherwise refuse every later write")
                   (support/settle #(= 3 (count @support/fetch-inits)) 200)))
          (.then (fn []
                   (is (= "GET" (last (methods-sent)))
                       "and the client re-reads rather than assuming the write did not land,
                        since a slow write may have committed before the budget ran out")
                   (support/submit-write! host {:op :create :record {"title" "y"}})
                   (support/settle #(= 2 (count (filter #{"POST"} (methods-sent)))) 200)))
          (.then (fn [] (is true "a later write is accepted again") (done)))
          (.catch (fn [e] (is false (str "a hung write wedged the resource: " e)) (done)))))))

(deftest a-decorator-that-never-answers-gives-up-on-the-budget
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] (js/Promise. (fn [_resolve _reject] nil))))
    (support/mount! "tasks" "/api/tasks" {"timeout" "30"})
    (-> (support/settle #(seq (remove nil? @support/failure-calls)))
        (.then (fn []
                 (is (= :timeout (get-in (first-failure) [:error :kind]))
                     "the budget covers the whole operation: there is no fetch to abort here,
                      so only racing the timer against it can end the request")
                 (is (empty? @support/fetch-calls) "and nothing was ever sent")
                 (done)))
        (.catch (fn [e] (is false (str "a hung decorator was not bounded: " e)) (done))))))

(deftest an-unusable-timeout-attribute-is-reported-and-keeps-the-default
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks" {"timeout" "soon"})
    (-> (support/settle #(seq @support/spy-calls))
        (.then (fn []
                 (is (error-logged? #"is not a number of milliseconds")
                     "the typo is reported")
                 (is (empty? (remove nil? @support/failure-calls))
                     "and the request runs on the default budget rather than losing it")
                 (done)))
        (.catch (fn [e] (is false (str "a bad budget broke the request: " e)) (done))))))

(deftest init-installs-the-decorator-from-its-options
  (async done
    (support/stub-accepted! [] empty-shape)
    (core/init {:request-decorator (fn [_request] {"authorization" "Bearer from-init"})})
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (= {"authorization" "Bearer from-init"} (headers-of (first-init)))
                     "an app names the hook once, through the entry point it already calls")
                 (done)))
        (.catch (fn [e] (is false (str "init did not install the decorator: " e)) (done))))))

(deftest init-without-the-key-leaves-the-installed-decorator-alone
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] {"authorization" "Bearer standing"}))
    (core/init)
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (= {"authorization" "Bearer standing"} (headers-of (first-init)))
                     "a second init that says nothing about the hook does not drop it")
                 (done)))
        (.catch (fn [e] (is false (str "init dropped the decorator: " e)) (done))))))

(deftest init-clears-the-decorator-when-the-key-holds-nil
  (async done
    (support/stub-accepted! [] empty-shape)
    (decorator/install! (fn [_request] {"authorization" "Bearer standing"}))
    (core/init {:request-decorator nil})
    (support/mount! "tasks" "/api/tasks")
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (undefined? (.-headers ^js (first-init)))
                     "naming the key sets the hook to what it holds, nil included")
                 (done)))
        (.catch (fn [e] (is false (str "init did not clear the decorator: " e)) (done))))))

(deftest an-unknown-credentials-mode-is-reported-and-ignored
  (async done
    (support/stub-accepted! [] empty-shape)
    (support/mount! "tasks" "/api/tasks" {"credentials" "always"})
    (-> (support/settle #(seq @support/fetch-inits))
        (.then (fn []
                 (is (undefined? (.-credentials ^js (first-init)))
                     "an unknown mode is dropped rather than handed to fetch, which would throw")
                 (is (error-logged? #"is not a fetch credentials mode")
                     "and the typo is reported")
                 (done)))
        (.catch (fn [e] (is false (str "bad credentials mode blocked the request: " e)) (done))))))
