(ns barebuild.browser.server-resource-test
  "Element/integration tests for <server-resource> in a real browser (headless Chrome).
   Exercises the executor edge the pure step tests cannot: registration, connect, fetch,
   Conversion 1, notify fan-out, and consumer render."
  (:require
   [barebuild.browser.support :as support]
   [cljs.test :refer-macros [deftest is use-fixtures async]]))

(use-fixtures :each
  {:before (fn [] (support/reset-state!))
   :after  (fn [] (support/restore-fetch!) (support/reset-url!) (support/unmount-all!))})

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
