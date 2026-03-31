(ns baredom.components.x-chart.x-chart-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-chart.x-chart :as x]
            [baredom.components.x-chart.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el sel]
  (.querySelector (.-shadowRoot el) sel))

;; ---- Registration ----

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-chart should be registered in the custom element registry"))

;; ---- Shadow DOM structure ----

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))
        "element should have a shadow root")
    (is (some? (shadow-part el "[part=container]"))
        "shadow should contain a container part")
    (is (some? (shadow-part el "[part=svg]"))
        "shadow should contain an svg part")
    (is (some? (shadow-part el "[part=sr-only]"))
        "shadow should contain an sr-only part")))

(deftest container-focusable-test
  (let [el (append! (make-el))
        ^js container (shadow-part el "[part=container]")]
    (is (= "0" (.getAttribute container "tabindex"))
        "container should be focusable with tabindex=0")))

(deftest svg-aria-hidden-test
  (let [el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (= "true" (.getAttribute svg "aria-hidden"))
        "SVG should be aria-hidden")))

;; ---- Default attribute values ----

(deftest default-type-test
  (let [el (append! (make-el))]
    (is (nil? (.getAttribute el model/attr-type))
        "type attribute should not be set by default")))

(deftest default-height-test
  (let [el (append! (make-el))]
    (is (nil? (.getAttribute el model/attr-height))
        "height attribute should not be set by default")))

;; ---- Loading state ----

(deftest loading-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-loading "")
    (is (.hasAttribute el model/attr-loading)
        "loading attribute should be present after setting")))

(deftest loading-boolean-property-test
  (let [el (append! (make-el))]
    (is (false? (.-loading el)) "loading should default to false")
    (set! (.-loading el) true)
    (is (true? (.hasAttribute el model/attr-loading))
        "setting loading=true should reflect to attribute")
    (set! (.-loading el) false)
    (is (false? (.hasAttribute el model/attr-loading))
        "setting loading=false should remove attribute")))

;; ---- Type attribute ----

(deftest type-property-reflects-attr-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-type "bar")
    (is (= "bar" (.-type el))
        "type property should reflect attr")))

(deftest type-property-setter-test
  (let [el (append! (make-el))]
    (set! (.-type el) "area")
    (is (= "area" (.getAttribute el model/attr-type))
        "setting type property should set attr")))

;; ---- Disabled state ----

(deftest disabled-property-test
  (let [el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (true? (.-disabled el)))
    (is (.hasAttribute el model/attr-disabled))))

;; ---- Data attribute parses ----

(deftest data-attr-parses-test
  (let [el (append! (make-el))
        json (js/JSON.stringify
              (clj->js [{:id "a" :label "A" :data [{:x 1 :y 5}]}]))]
    (.setAttribute el model/attr-data json)
    (is (some? (shadow-part el "[part=svg]"))
        "SVG should still be present after data is set")))

;; ---- Tooltip part ----

(deftest tooltip-always-present-in-shadow-test
  (let [el (append! (make-el))]
    ;; The tooltip element is always built into the shadow DOM
    (is (some? (shadow-part el "[part=tooltip]"))
        "tooltip part should always be present in shadow DOM")))

(deftest tooltip-present-when-attr-set-test
  ;; Create element with tooltip attr set before connecting
  (let [el (make-el)]
    (.setAttribute el model/attr-tooltip "")
    (append! el)
    (is (some? (shadow-part el "[part=tooltip]"))
        "tooltip part should be present when tooltip attr is set at connect time")))

;; ---- Events ----

(deftest select-event-test
  (async done
    (let [el (append! (make-el))
          json (js/JSON.stringify
                (clj->js [{:id "s0" :label "Series" :data [{:x 0 :y 10} {:x 1 :y 20}]}]))
          seen (atom nil)]
      (.setAttribute el model/attr-data json)
      (.addEventListener
       el model/event-select
       (fn [^js ev]
         (reset! seen (.-detail ev))
         (done)))
      ;; Dispatch a synthetic click on the hit rect in SVG
      (let [^js svg  (shadow-part el "[part=svg]")
            ^js hit  (when svg (.querySelector svg "rect[style]"))]
        (when hit
          (.dispatchEvent hit (js/MouseEvent. "click"
                                              #js {:bubbles   true
                                                   :cancelable true
                                                   :clientX   200
                                                   :clientY   90})))
        ;; If no hit rect found (e.g. empty data rendered), just finish
        (when-not hit (done))))))

;; ---- Rich tooltip structure ----

(deftest tooltip-has-header-and-body-test
  (let [el (make-el)]
    (.setAttribute el model/attr-tooltip "")
    (append! el)
    (is (some? (shadow-part el "[part=tooltip-header]"))
        "tooltip should have a header part")
    (is (some? (shadow-part el "[part=tooltip-body]"))
        "tooltip should have a body part")))

(deftest tooltip-has-prebuilt-rows-test
  (let [el (make-el)]
    (.setAttribute el model/attr-tooltip "")
    (append! el)
    (let [rows (.querySelectorAll (.-shadowRoot el) "[part=tooltip-row]")]
      (is (= model/max-tooltip-rows (.-length rows))
          "tooltip should have max-tooltip-rows pre-built row elements"))))

(deftest tooltip-rows-hidden-by-default-test
  (let [el (make-el)]
    (.setAttribute el model/attr-tooltip "")
    (append! el)
    (let [rows (.querySelectorAll (.-shadowRoot el) "[part=tooltip-row]")]
      (is (every? #(= "true" (.getAttribute % "data-hidden"))
                  (array-seq rows))
          "all rows should be hidden before hover"))))

(deftest tooltip-hidden-by-default-visibility-test
  (let [el (make-el)]
    (.setAttribute el model/attr-tooltip "")
    (append! el)
    (let [^js t (shadow-part el "[part=tooltip]")]
      (is (= "false" (.getAttribute t "data-visible"))
          "tooltip data-visible should be false by default"))))

;; ---- cursor=none guard ----

(deftest cursor-none-no-tooltip-test
  (async done
    (let [el (make-el)
          json (js/JSON.stringify
                (clj->js [{:id "s0" :label "S" :data [{:x 0 :y 10} {:x 1 :y 20}]}]))]
      (.setAttribute el model/attr-tooltip "")
      (.setAttribute el model/attr-cursor "none")
      (.setAttribute el model/attr-data json)
      (append! el)
      ;; mousemove on hit rect should NOT show tooltip
      (let [^js svg (shadow-part el "[part=svg]")
            ^js hit (when svg (.querySelector svg "rect[style]"))]
        (when hit
          (.dispatchEvent hit (js/MouseEvent. "mousemove"
                                              #js {:bubbles true
                                                   :clientX 200
                                                   :clientY 90})))
        (let [^js t (shadow-part el "[part=tooltip]")]
          (is (= "false" (.getAttribute t "data-visible"))
              "tooltip should stay hidden when cursor=none"))
        (done)))))

;; ---- grid / axes bool properties ----

(deftest grid-property-test
  (let [el (append! (make-el))]
    (set! (.-grid el) true)
    (is (.hasAttribute el model/attr-grid)
        "setting grid=true should set the attr")
    (set! (.-grid el) false)
    (is (not (.hasAttribute el model/attr-grid))
        "setting grid=false should remove the attr")))

(deftest axes-property-test
  (let [el (append! (make-el))]
    (set! (.-axes el) true)
    (is (.hasAttribute el model/attr-axes)
        "setting axes=true should set the attr")
    (set! (.-axes el) false)
    (is (not (.hasAttribute el model/attr-axes))
        "setting axes=false should remove the attr")))

;; ---- height / padding numeric properties ----

(deftest height-property-default-test
  (let [el (append! (make-el))]
    (is (= model/default-height (.-height el))
        "height should return default when attr is absent")))

(deftest height-property-setter-test
  (let [el (append! (make-el))]
    (set! (.-height el) 300)
    (is (= "300" (.getAttribute el model/attr-height))
        "setting height property should set the attr")
    (is (= 300 (.-height el))
        "height getter should return a number")))

(deftest padding-property-default-test
  (let [el (append! (make-el))]
    (is (= model/default-padding (.-padding el))
        "padding should return default when attr is absent")))

(deftest padding-property-setter-test
  (let [el (append! (make-el))]
    (set! (.-padding el) 20)
    (is (= "20" (.getAttribute el model/attr-padding))
        "setting padding property should set the attr")
    (is (= 20 (.-padding el))
        "padding getter should return a number")))

;; ---- keyboard navigation ----

(deftest keyboard-arrowright-advances-selection-test
  (async done
    (let [el   (make-el)
          json (js/JSON.stringify
                (clj->js [{:id "s0" :label "A" :data [{:x 0 :y 1} {:x 1 :y 2} {:x 2 :y 3}]}]))]
      (.setAttribute el model/attr-data json)
      (append! el)
      (let [^js container (shadow-part el "[part=container]")]
        (.dispatchEvent container
                        (js/KeyboardEvent. "keydown"
                                           #js {:key "ArrowRight" :bubbles true}))
        (js/requestAnimationFrame
         (fn []
           (is (= "s0:0" (.getAttribute el model/attr-selected))
               "ArrowRight with no prior selection should select s0:0")
           (done)))))))

(deftest keyboard-enter-fires-select-event-test
  (async done
    (let [el   (make-el)
          json (js/JSON.stringify
                (clj->js [{:id "k0" :label "K" :data [{:x 0 :y 5}]}]))]
      (.setAttribute el model/attr-data json)
      (.setAttribute el model/attr-selected "k0:0")
      (append! el)
      (.addEventListener el model/event-select
                         (fn [^js ev]
                           (is (= "k0" (.-seriesId (.-detail ev))))
                           (is (= 0   (.-index (.-detail ev))))
                           (done)))
      (let [^js container (shadow-part el "[part=container]")]
        (.dispatchEvent container
                        (js/KeyboardEvent. "keydown"
                                           #js {:key "Enter" :bubbles true}))))))
