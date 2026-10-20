(ns baredom.utils.component-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-progress.model :as progress-model]
            [baredom.components.x-progress.x-progress :as x-progress]
            [baredom.test-elements :as elements]
            [baredom.utils.component :as component]
            [baremirror.alpha.parts :as parts]))

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

(def ^:private control-tag "x-hold-control-probe")

(def ^:private disabled-changes (atom []))

(defn- record-disabled-change! [_el n old new]
  (swap! disabled-changes conj [n old new]))

(component/register! control-tag
                     {:observed-attributes  #js ["disabled"]
                      :connected-fn         identity
                      :attribute-changed-fn record-disabled-change!
                      :form-associated?     true})

(def ^:private move-tag "x-move-probe")

(def ^:private lifecycle (atom []))

(defn- note! [step _el]
  (swap! lifecycle conj step))

(component/register! move-tag
                     {:observed-attributes  #js []
                      :connected-fn         (partial note! :connected)
                      :disconnected-fn      (partial note! :disconnected)
                      :moved-fn             (partial note! :moved)
                      :attribute-changed-fn identity})

(def ^:private quiet-tag "x-quiet-move-probe")

(component/register! quiet-tag
                     {:observed-attributes  #js []
                      :connected-fn         (partial note! :connected)
                      :disconnected-fn      (partial note! :disconnected)
                      :attribute-changed-fn identity})

(x-progress/init!)

(defn- cleanup! []
  (reset! calls [])
  (reset! disabled-changes [])
  (reset! lifecycle [])
  (reset! component/lifecycle-hook nil)
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str probe-tag "," progress-model/tag-name)))
  (elements/remove-all!))

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

(deftest a-control-receives-its-fieldset-as-a-change-of-disabled
  (let [[^js fieldset ^js control] (elements/in-fieldset! control-tag {})]
    (set! (.-disabled fieldset) true)
    (set! (.-disabled fieldset) false)
    (testing "each way is one change"
      (is (= [["disabled" nil ""] ["disabled" "" nil]] @disabled-changes)))
    (testing "the control gets no attribute"
      (is (false? (.hasAttribute control "disabled"))))))

(deftest a-fieldset-leaves-a-record-of-a-change-of-disabled
  (let [records        (atom [])
        [^js fieldset] (elements/in-fieldset! control-tag {})]
    (reset! component/lifecycle-hook (partial swap! records conj))
    (set! (.-disabled fieldset) true)
    (is (= [[:lifecycle/attribute-changed "disabled" nil ""]]
           (mapv (juxt :type :attribute :old-value :new-value) @records)))))

(deftest a-held-control-receives-its-fieldset-when-the-work-returns
  (let [[^js fieldset ^js control] (elements/in-fieldset! control-tag {})
        during (hold! control (fn [] (set! (.-disabled fieldset) true) @disabled-changes))]
    (testing "no change arrives while the work runs"
      (is (= [] during)))
    (testing "the change arrives when the work returns"
      (is (= [["disabled" nil ""]] @disabled-changes)))))

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
    (parts/with-one-render! el (fn [^js held]
                                  (write-both! held)
                                  (reset! during @calls)))
    (testing "no change arrives while the work runs"
      (is (= [] @during)))
    (testing "each change arrives after the work"
      (is (= [["a" "1" "2"] ["b" "1" "2"]] @calls)))))

(defn- move!
  "Moves `el` to the end of `parent` with moveBefore, so it stays connected."
  [^js parent ^js el]
  (.moveBefore parent el nil))

(defn- reinsert! [^js parent ^js el]
  (.insertBefore parent el nil))

(defn- moved-steps
  "The lifecycle steps of a `tag` element when `put!` takes it to a second parent."
  [tag put!]
  (let [el     (elements/in-body! tag {})
        parent (elements/in-body! "div" {})]
    (reset! lifecycle [])
    (put! parent el)
    @lifecycle))

(deftest a-moved-element-runs-its-moved-fn-only
  (is (= [:moved] (moved-steps move-tag move!))))

(deftest a-moved-element-without-a-moved-fn-runs-nothing
  (is (= [] (moved-steps quiet-tag move!))))

(deftest an-element-taken-out-and-put-back-disconnects-and-connects
  (is (= [:disconnected :connected] (moved-steps move-tag reinsert!))))

(deftest a-move-leaves-a-record-of-the-move
  (let [records (atom [])
        el      (elements/in-body! quiet-tag {})
        parent  (elements/in-body! "div" {})]
    (reset! component/lifecycle-hook (partial swap! records conj))
    (move! parent el)
    (is (= [:lifecycle/moved] (mapv :type @records)))))

(def ^:private early-tag "x-early-probe")

(defn- install-early-props! [^js proto]
  (js/Object.defineProperty proto "label"
                            #js {:configurable true
                                 :get          (fn [] (this-as ^js this (.getAttribute this "label")))
                                 :set          (fn [v] (this-as ^js this (.setAttribute this "label" v)))})
  (js/Object.defineProperty proto "size"
                            #js {:configurable true
                                 :get          (fn [] 3)}))

(deftest a-property-set-before-the-element-is-defined-goes-through-its-setter
  (let [^js el (.createElement js/document early-tag)]
    (set! (.-label el) "early")
    (set! (.-size el) 9)
    (component/register! early-tag {:observed-attributes  #js []
                                    :connected-fn         identity
                                    :attribute-changed-fn identity
                                    :setup-prototype-fn   install-early-props!})
    (elements/node-in-body! el)
    (testing "the setter writes the value"
      (is (= "early" (.getAttribute el "label")))
      (is (= "early" (.-label el)))
      (is (not (js/Object.hasOwn el "label"))))
    (testing "a value set on a property with only a getter stays on the element"
      (is (= 9 (.-size el))))))
