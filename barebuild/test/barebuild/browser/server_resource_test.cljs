(ns barebuild.browser.server-resource-test
  "Element/integration tests for <server-resource> in a real browser (headless Chrome).
   Exercises the executor edge the pure step tests cannot: registration, connect, fetch,
   Conversion 1, notify fan-out, and consumer render."
  (:require
   [barebuild.browser.support :as support]
   [cljs.test :refer-macros [deftest is use-fixtures async]]))

(use-fixtures :each
  {:before (fn [] (support/reset-state!) (support/capture-errors!))
   :after  (fn [] (support/restore-fetch!) (support/restore-error!)
             (support/reset-url!) (support/unmount-all!))})

(deftest server-resource-boots-fetches-and-renders
  (async done
    (support/stub-accepted!
     {"outcome" "accepted"
      "query"   {}
      "value"   [{"id" 1 "owner" "Alice"}]
      "shape"   {"idKey" "id" "fields" [{"key" "owner" "type" "string"}]}})
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

(defn- error-logged? [needle]
  (some (fn [args] (some #(and (string? %) (re-find needle %)) args)) @support/error-calls))

(deftest a-throwing-consumer-does-not-starve-a-later-consumer
  (async done
    (support/stub-accepted!
     {"outcome" "accepted"
      "query"   {}
      "value"   [{"id" 1 "owner" "Alice"}]
      "shape"   {"idKey" "id" "fields" [{"key" "owner" "type" "string"}]}})
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

(deftest a-server-resource-with-no-consumers-logs-a-loud-error
  (async done
    (support/stub-accepted! {"outcome" "accepted" "query" {} "value" []
                             "shape" {"idKey" "id" "fields" []}})
    (support/mount-consumers! "tasks" "/api/tasks" [])
    (-> (support/settle #(error-logged? #"has no consumers"))
        (.then (fn [] (is true) (done)))
        (.catch (fn [e] (is false (str "no loud error for an empty registry: " e)) (done))))))
