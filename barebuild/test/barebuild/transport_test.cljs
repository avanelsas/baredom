(ns barebuild.transport-test
  "The request pipeline: what ends a request early, and how a rejection is classified.
   fetch is stubbed on globalThis, so nothing here reaches a network. Every test is async,
   which is why the whole namespace uses map-style fixtures."
  (:require [cljs.test :refer-macros [deftest is testing async use-fixtures]]
            [barebuild.decorator :as decorator]
            [barebuild.transport :as transport]))

(def ^:private real-fetch (.-fetch js/globalThis))

(use-fixtures :each
  {:after (fn []
            (decorator/install! nil)
            (set! (.-fetch js/globalThis) real-fetch))})

(defn- stub-fetch! [f]
  (set! (.-fetch js/globalThis) f))

(defn- never []
  (js/Promise. (fn [_resolve _reject])))

(defn- abort-error [] (js/DOMException. "gone" "AbortError"))

(def ^:private a-request {:url "/api/tasks" :method "GET"})

;; --- classifying a rejection -----------------------------------------------

(deftest transport-error-tells-a-spent-budget-from-a-dead-connection
  (testing "only the budget's own error is a timeout, and it carries what the budget was"
    (is (= {:kind :timeout :after 5000}
           (transport/transport-error (js/DOMException. "x" "TimeoutError") 5000))))
  (testing "anything else is the connection, which is all the client can honestly say"
    (is (= {:kind :offline} (transport/transport-error (js/Error. "boom") 5000)))
    (is (= {:kind :offline} (transport/transport-error (abort-error) 5000)))))

(deftest abort-error?-names-only-a-deliberate-abort
  (is (true? (transport/abort-error? (abort-error))))
  (is (false? (transport/abort-error? (js/DOMException. "x" "TimeoutError"))))
  (is (false? (transport/abort-error? (js/Error. "boom")))))

;; --- bounded: the operation against what ends it early ---------------------

(deftest bounded-resolves-when-the-operation-settles-first
  (async done
    (-> (transport/bounded (js/Promise.resolve :ok) 1000 (js/AbortController.))
      (.then (fn [v] (is (= :ok v)) (done))))))

(deftest bounded-without-a-budget-waits-for-the-operation
  (async done
    (-> (transport/bounded (js/Promise.resolve :ok) nil (js/AbortController.))
      (.then (fn [v] (is (= :ok v)) (done))))))

(deftest bounded-rejects-with-the-signals-own-reason-when-aborted
  (async done
    (let [controller (js/AbortController.)
          reason     (abort-error)]
      (js/setTimeout (fn [] (.abort controller reason)) 0)
      (-> (transport/bounded (never) 1000 controller)
        (.then (fn [_] (is false "an aborted operation must not resolve") (done))
               (fn [e]
                 (is (identical? reason e) "the signal's own reason travels out unchanged")
                 (done)))))))

(deftest bounded-rejects-at-once-on-an-already-aborted-controller
  (testing "the controller is stashed before the decorator is awaited, so an abort landing in
            that window has no fetch to reject and must be caught by the signal's state"
    (async done
      (let [controller (js/AbortController.)]
        (.abort controller (abort-error))
        (-> (transport/bounded (never) 1000 controller)
          (.then (fn [_] (is false "an already-aborted operation must not resolve") (done))
                 (fn [e] (is (true? (transport/abort-error? e))) (done))))))))

(deftest bounded-rejects-and-releases-the-socket-when-the-budget-runs-out
  (async done
    (let [controller (js/AbortController.)]
      (-> (transport/bounded (never) 1 controller)
        (.then (fn [_] (is false "a spent budget must not resolve") (done))
               (fn [^js e]
                 (is (= {:kind :timeout :after 1} (transport/transport-error e 1)))
                 (is (true? (.-aborted (.-signal controller)))
                     "the controller is aborted on the way out, so the socket is released")
                 (done)))))))

(deftest bounded-cancels-the-budget-when-the-operation-wins
  (testing "an operation that finishes inside its budget leaves no timer behind to fire later"
    (async done
      (let [controller (js/AbortController.)]
        (-> (transport/bounded (js/Promise.resolve :ok) 1 controller)
          (.then (fn [_]
                   (js/setTimeout
                    (fn []
                      (is (false? (.-aborted (.-signal controller)))
                          "the budget never fired, so it never aborted a finished request")
                      (done))
                    20))))))))

;; --- perform!: decorate, send, classify ------------------------------------

(deftest perform-parses-an-ok-response-into-an-envelope
  (async done
    (stub-fetch!
     (fn [_url _init]
       (js/Promise.resolve
        #js {:ok   true
             :text (fn [] (js/Promise.resolve
                           (js/JSON.stringify
                            #js {:outcome "accepted"
                                 :value   #js []
                                 :shape   #js {:idKey "id" :fields #js []}})))})))
    (-> (transport/perform! a-request (js/AbortController.))
      (.then (fn [result] (is (= :accepted (:outcome result))) (done))))))

(deftest perform-classifies-a-non-ok-response-by-its-status
  (async done
    (stub-fetch! (fn [_url _init] (js/Promise.resolve #js {:ok false :status 503})))
    (-> (transport/perform! a-request (js/AbortController.))
      (.then (fn [result]
               (is (= {:network-failure {:kind :http-status :status 503}} result))
               (done))))))

(deftest perform-turns-an-unreadable-body-into-a-protocol-failure
  (async done
    (stub-fetch! (fn [_url _init]
                   (js/Promise.resolve
                    #js {:ok true :text (fn [] (js/Promise.resolve "not json"))})))
    (-> (transport/perform! a-request (js/AbortController.))
      (.then (fn [result]
               (is (= :malformed-json (get-in result [:protocol-failure :reason])))
               (done))))))

(deftest perform-attaches-the-decorators-headers-to-the-request
  (async done
    (let [seen (atom nil)]
      (decorator/install! (fn [_m] #js {"Authorization" "Bearer t"}))
      (stub-fetch! (fn [_url ^js init]
                     (reset! seen (js->clj (.-headers init)))
                     (js/Promise.resolve #js {:ok false :status 204})))
      (-> (transport/perform! a-request (js/AbortController.))
        (.then (fn [_]
                 (is (= {"authorization" "Bearer t"} @seen)
                     "a decorator's headers are normalized and ride the request")
                 (done)))))))

(deftest perform-classifies-a-failed-decorator-without-sending
  (testing "headers that could not be built are a network failure like any other, so a consumer
            branches on it the same way. The request never leaves"
    (async done
      (let [sent (atom false)]
        (decorator/install! (fn [_m] (throw (js/Error. "no token"))))
        (stub-fetch! (fn [_url _init]
                       (reset! sent true)
                       (js/Promise.resolve #js {:ok false :status 204})))
        (-> (transport/perform! a-request (js/AbortController.))
          (.then (fn [result]
                   (is (= {:network-failure {:kind :decorator}} result))
                   (is (false? @sent))
                   (done))))))))

(deftest perform-carries-the-abort-signal-onto-the-request
  (async done
    (let [controller (js/AbortController.)
          seen       (atom nil)]
      (stub-fetch! (fn [_url ^js init]
                     (reset! seen (.-signal init))
                     (js/Promise.resolve #js {:ok false :status 204})))
      (-> (transport/perform! a-request controller)
        (.then (fn [_]
                 (is (identical? (.-signal controller) @seen)
                     "the fetch is cancellable by the controller the caller holds")
                 (done)))))))
