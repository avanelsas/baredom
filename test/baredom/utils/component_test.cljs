(ns baredom.utils.component-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-progress.model :as progress-model]
            [baredom.components.x-progress.x-progress :as x-progress]
            [baredom.utils.component :as component]
            [baremirror.core :as mirror]))

(def ^:private probe-tag "x-hold-probe")

(def ^:private calls (atom []))

(defn- record-call! [^js el n _old _new]
  (swap! calls conj [n (.getAttribute el "a") (.getAttribute el "b")]))

(component/register! probe-tag
                     {:observed-attributes  #js ["a" "b"]
                      :connected-fn         identity
                      :attribute-changed-fn record-call!})

(def ^:private failing-tag "x-hold-failing-probe")

(defn- record-call-and-fail! [^js el n old new]
  (record-call! el n old new)
  (throw (js/Error. n)))

(component/register! failing-tag
                     {:observed-attributes  #js ["a" "b"]
                      :connected-fn         identity
                      :attribute-changed-fn record-call-and-fail!})

(x-progress/init!)

(defn- cleanup! []
  (reset! calls [])
  (reset! component/lifecycle-hook nil)
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str probe-tag "," progress-model/tag-name))))

(use-fixtures :each {:before cleanup! :after cleanup!})

(defn- make-probe []
  (.createElement js/document probe-tag))

(defn- hold! [^js el f]
  (.call (aget el component/hold-key) el f))

(defn- write-both! [^js el]
  (.setAttribute el "a" "1")
  (.setAttribute el "b" "2"))

(defn- throw-after-write! [^js el]
  (.setAttribute el "a" "1")
  (throw (js/Error. "stop")))

(defn- reported-errors!
  "The messages of the errors reported to the window while `f` runs."
  [f]
  (let [reported (atom [])
        handler  (.-onerror js/window)]
    (set! (.-onerror js/window) (fn [message] (swap! reported conj message) true))
    (try
      (f)
      @reported
      (finally
        (set! (.-onerror js/window) handler)))))

(defn- make-progress []
  (let [el (.createElement js/document progress-model/tag-name)]
    (.setAttribute el "value" "50")
    (.setAttribute el "max" "200")
    (.appendChild (.-body js/document) el)
    el))

(defn- count-completes! [^js el]
  (let [seen (atom 0)]
    (.addEventListener el progress-model/event-complete (fn [_] (swap! seen inc)))
    seen))

(defn- lower-max-then-value! [^js el]
  (.setAttribute el "max" "50")
  (.setAttribute el "value" "10"))

(deftest an-element-that-is-not-held-receives-each-change-at-once
  (let [el (make-probe)]
    (.setAttribute el "a" "1")
    (is (= [["a" "1" nil]] @calls))))

(deftest a-held-element-receives-no-change-until-the-work-returns
  (let [el     (make-probe)
        during (hold! el (fn [] (write-both! el) @calls))]
    (testing "no change arrives while the work runs"
      (is (= [] during)))
    (testing "each change arrives in order and sees the final attributes"
      (is (= [["a" "1" "2"] ["b" "1" "2"]] @calls)))))

(deftest the-hold-returns-what-the-work-returns
  (is (= :done (hold! (make-probe) (constantly :done)))))

(deftest the-hold-ends-when-the-work-throws
  (let [el (make-probe)]
    (is (thrown? js/Error (hold! el (partial throw-after-write! el))))
    (testing "the change written before the throw arrives"
      (is (= [["a" "1" nil]] @calls)))
    (testing "a later change arrives at once"
      (.setAttribute el "b" "2")
      (is (= [["a" "1" nil] ["b" "1" "2"]] @calls)))))

(deftest a-hold-inside-a-hold-ends-with-the-outer-one
  (let [el    (make-probe)
        inner (hold! el (fn []
                          (hold! el (partial write-both! el))
                          @calls))]
    (is (= [] inner))
    (is (= 2 (count @calls)))))

(deftest an-error-from-the-element-is-reported-and-the-later-changes-arrive
  (let [el       (.createElement js/document failing-tag)
        reported (reported-errors! (fn [] (hold! el (partial write-both! el))))]
    (testing "both changes arrive"
      (is (= [["a" "1" "2"] ["b" "1" "2"]] @calls)))
    (testing "each error is reported"
      (is (= 2 (count reported))))))

(deftest the-lifecycle-hook-fires-at-the-write-and-not-at-the-release
  (let [el     (make-probe)
        fired  (atom [])
        _      (reset! component/lifecycle-hook (comp (partial swap! fired conj) :attribute))
        during (hold! el (fn [] (write-both! el) @fired))]
    (is (= ["a" "b"] during))
    (is (= ["a" "b"] @fired))))

(deftest x-progress-held-sends-no-event-from-the-state-in-between
  (let [one-by-one (make-progress)
        held       (make-progress)
        kept       (count-completes! held)]
    (lower-max-then-value! one-by-one)
    (hold! held (partial lower-max-then-value! held))
    (testing "the held writes send no event"
      (is (= 0 @kept)))
    (testing "the held element ends with the shadow tree of writes one by one"
      (is (= (.-innerHTML (.-shadowRoot one-by-one))
             (.-innerHTML (.-shadowRoot held)))))))

(deftest baremirror-holds-the-changes-of-a-baredom-element
  (let [el     (make-probe)
        during (atom nil)]
    (mirror/with-one-render! el (fn [^js held]
                                  (write-both! held)
                                  (reset! during @calls)))
    (testing "no change arrives while the work runs"
      (is (= [] @during)))
    (testing "each change arrives after the work"
      (is (= [["a" "1" "2"] ["b" "1" "2"]] @calls)))))
