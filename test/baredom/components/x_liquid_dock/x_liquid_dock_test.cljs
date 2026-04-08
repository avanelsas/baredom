(ns baredom.components.x-liquid-dock.x-liquid-dock-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-liquid-dock.x-liquid-dock :as x]
            [baredom.components.x-liquid-dock.model         :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn ^js shadow-all [^js el selector]
  (.querySelectorAll (.-shadowRoot el) selector))

(defn ^js make-dock-with-items
  "Create a dock with n button children."
  [n]
  (let [el (make-el)]
    (dotimes [i n]
      (let [btn (.createElement js/document "button")]
        (set! (.-textContent btn) (str "Item " i))
        (.appendChild el btn)))
    el))

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=dock]")))
    (is (some? (shadow-part el "[part=filter-svg]")))
    (is (some? (shadow-part el "[part=liquid-layer]")))
    (is (some? (shadow-part el "[part=items]")))
    (is (some? (shadow-part el "slot")))))

;; ── SVG filter structure ────────────────────────────────────────────────────
(deftest filter-structure-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "[part=filter-svg]")
        ^js blur (.. svg (querySelector "feGaussianBlur"))
        ^js mat  (.. svg (querySelector "feColorMatrix"))
        ^js turb (.. svg (querySelector "feTurbulence"))
        ^js disp (.. svg (querySelector "feDisplacementMap"))]
    (is (some? blur) "feGaussianBlur should exist")
    (is (some? mat)  "feColorMatrix should exist")
    (is (some? turb) "feTurbulence should exist")
    (is (some? disp) "feDisplacementMap should exist")))

(deftest filter-default-values-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "[part=filter-svg]")
        ^js blur (.. svg (querySelector "feGaussianBlur"))
        ^js disp (.. svg (querySelector "feDisplacementMap"))]
    (is (= "10" (.getAttribute blur "stdDeviation")))
    (is (= "8" (.getAttribute disp "scale")))))

;; ── Navigation semantics ────────────────────────────────────────────────────
(deftest nav-role-test
  (let [^js el   (append! (make-el))
        ^js dock (shadow-part el "[part=dock]")]
    (is (= "navigation" (.getAttribute dock "role")))
    (is (= "Navigation dock" (.getAttribute dock "aria-label")))))

(deftest svg-a11y-test
  (let [^js el  (append! (make-el))
        ^js svg (shadow-part el "[part=filter-svg]")]
    (is (= "true" (.getAttribute svg "aria-hidden")))
    (is (= "presentation" (.getAttribute svg "role")))))

;; ── Blob reconciliation with slotted items (dual blobs) ─────────────────────
(deftest blob-count-matches-items-test
  (async done
    (let [^js el (append! (make-dock-with-items 4))]
      ;; Blobs are created after rAF + slotchange (2 per item: rest + phantom)
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [blobs (shadow-all el ".blob")]
              (is (= 8 (.-length blobs))
                  "Should have two blobs per slotted item (rest + phantom)"))
            (done))))))))

(deftest blob-rest-class-test
  (async done
    (let [^js el (append! (make-dock-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [rest-blobs (shadow-all el ".blob-rest")]
              (is (= 3 (.-length rest-blobs))
                  "Should have one rest blob per item"))
            (done))))))))

(deftest blob-phantom-class-test
  (async done
    (let [^js el (append! (make-dock-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [phantom-blobs (shadow-all el ".blob-phantom")]
              (is (= 3 (.-length phantom-blobs))
                  "Should have one phantom blob per item"))
            (done))))))))

;; ── Attribute binding ───────────────────────────────────────────────────────
(deftest blur-attribute-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-blur "20")
    (append! el)
    (let [^js svg  (shadow-part el "[part=filter-svg]")
          ^js blur (.. svg (querySelector "feGaussianBlur"))]
      (is (= "20" (.getAttribute blur "stdDeviation"))))))

(deftest ripple-scale-attribute-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-ripple-scale "15")
    (append! el)
    (let [^js svg  (shadow-part el "[part=filter-svg]")
          ^js disp (.. svg (querySelector "feDisplacementMap"))]
      (is (= "15" (.getAttribute disp "scale"))))))

(deftest attribute-change-updates-filter-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "[part=filter-svg]")
        ^js blur (.. svg (querySelector "feGaussianBlur"))]
    (is (= "10" (.getAttribute blur "stdDeviation")))
    (.setAttribute el model/attr-blur "25")
    (is (= "25" (.getAttribute blur "stdDeviation")))))

;; ── Position attribute ──────────────────────────────────────────────────────
(deftest position-default-test
  (let [^js el (append! (make-el))]
    ;; Default is bottom — no position attribute set
    (is (nil? (.getAttribute el model/attr-position)))))

(deftest position-vertical-layout-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-position "left")
    (append! el)
    (let [^js dock (shadow-part el "[part=dock]")
          style    (.getComputedStyle js/window dock)]
      (is (= "column" (.getPropertyValue style "flex-direction"))))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest position-property-test
  (let [^js el (append! (make-el))]
    (is (= "bottom" (.-position el)))
    (set! (.-position el) "top")
    (is (= "top" (.getAttribute el model/attr-position)))
    (is (= "top" (.-position el)))))

(deftest gap-property-test
  (let [^js el (append! (make-el))]
    (is (= 8 (.-gap el)))
    (set! (.-gap el) 16)
    (is (= "16" (.getAttribute el model/attr-gap)))
    (is (= 16 (.-gap el)))))

(deftest blur-property-test
  (let [^js el (append! (make-el))]
    (is (= 10 (.-blur el)))
    (set! (.-blur el) 20)
    (is (= "20" (.getAttribute el model/attr-blur)))
    (is (= 20 (.-blur el)))))

(deftest color-property-test
  (let [^js el (append! (make-el))]
    (is (= "#6366f1" (.-color el)))
    (set! (.-color el) "hotpink")
    (is (= "hotpink" (.getAttribute el model/attr-color)))
    (is (= "hotpink" (.-color el)))))

(deftest ripple-scale-property-test
  (let [^js el (append! (make-el))]
    (is (= 8 (.-rippleScale el)))
    (set! (.-rippleScale el) 15)
    (is (= "15" (.getAttribute el model/attr-ripple-scale)))
    (is (= 15 (.-rippleScale el)))))

(deftest magnet-radius-property-test
  (let [^js el (append! (make-el))]
    (is (= 150 (.-magnetRadius el)))
    (set! (.-magnetRadius el) 200)
    (is (= "200" (.getAttribute el model/attr-magnet-radius)))
    (is (= 200 (.-magnetRadius el)))))

(deftest bob-intensity-property-test
  (let [^js el (append! (make-el))]
    (is (= 1.0 (.-bobIntensity el)))
    (set! (.-bobIntensity el) 0.5)
    (is (= "0.5" (.getAttribute el model/attr-bob-intensity)))
    (is (= 0.5 (.-bobIntensity el)))))

(deftest threshold-property-test
  (let [^js el (append! (make-el))]
    (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7" (.-threshold el)))
    (set! (.-threshold el) "1 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 25 -10")
    (is (= "1 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 25 -10"
           (.getAttribute el model/attr-threshold)))
    (is (= "1 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 25 -10" (.-threshold el)))))

(deftest ripple-speed-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.03 (.-rippleSpeed el)))
    (set! (.-rippleSpeed el) 0.08)
    (is (= "0.08" (.getAttribute el model/attr-ripple-speed)))
    (is (= 0.08 (.-rippleSpeed el)))))

(deftest magnet-strength-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.6 (.-magnetStrength el)))
    (set! (.-magnetStrength el) 1.2)
    (is (= "1.2" (.getAttribute el model/attr-magnet-strength)))
    (is (= 1.2 (.-magnetStrength el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

;; ── Disabled prevents events ────────────────────────────────────────────────
(deftest disabled-prevents-select-test
  (async done
    (let [^js el       (make-el)
          _            (.setAttribute el model/attr-disabled "")
          ^js el       (append! (make-dock-with-items 3))
          _            (.setAttribute el model/attr-disabled "")
          event-fired? (volatile! false)]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [_] (vreset! event-fired? true)))
            (let [^js items (.-children el)
                  ^js btn   (aget items 0)]
              (.click btn))
            (js/setTimeout
             (fn []
               (is (not @event-fired?)
                   "x-liquid-dock-select should NOT fire when disabled")
               (done))
             50))))))))

;; ── Keyboard navigation ─────────────────────────────────────────────────────
(deftest keyboard-arrow-navigation-test
  (async done
    (let [^js el (append! (make-dock-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (let [^js items (.-children el)]
              ;; First ArrowRight: focus-idx goes from -1 to 0
              (.dispatchEvent el
                (js/KeyboardEvent. "keydown"
                  #js {:key "ArrowRight" :bubbles true}))
              ;; Second ArrowRight: focus-idx goes from 0 to 1
              (.dispatchEvent el
                (js/KeyboardEvent. "keydown"
                  #js {:key "ArrowRight" :bubbles true}))
              (js/setTimeout
               (fn []
                 (is (= (aget items 1) (.-activeElement js/document))
                     "Two ArrowRight presses should focus second item")
                 (done))
               50)))))))))

(deftest keyboard-enter-select-test
  (async done
    (let [^js el       (append! (make-dock-with-items 3))
          event-fired? (volatile! false)]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [^js e]
                                 (vreset! event-fired? true)
                                 (is (= "keyboard" (.. e -detail -source)))))
            (let [^js items (.-children el)
                  ^js first-item (aget items 0)]
              ;; Focus first item then press ArrowRight to set focus-idx
              (.focus first-item)
              (.dispatchEvent el
                (js/KeyboardEvent. "keydown"
                  #js {:key "ArrowRight" :bubbles true}))
              ;; Then press Enter
              (js/setTimeout
               (fn []
                 (.dispatchEvent el
                   (js/KeyboardEvent. "keydown"
                     #js {:key "Enter" :bubbles true}))
                 (js/setTimeout
                  (fn []
                    (is @event-fired?
                        "Enter on focused item should dispatch select event")
                    (done))
                  50))
               50)))))))))

;; ── Select event ────────────────────────────────────────────────────────────
(deftest select-event-on-click-test
  (async done
    (let [^js el       (append! (make-dock-with-items 3))
          event-fired? (volatile! false)]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            (.addEventListener el model/event-select
                               (fn [^js e]
                                 (vreset! event-fired? true)
                                 (is (= 1 (.. e -detail -index)))
                                 (is (= "pointer" (.. e -detail -source)))))
            (let [^js items (.-children el)
                  ^js btn   (aget items 1)]
              (.click btn))
            (js/setTimeout
             (fn []
               (is @event-fired? "x-liquid-dock-select should have fired")
               (done))
             50))))))))

;; ── Keyboard navigation ─────────────────────────────────────────────────────
(deftest keyboard-focus-test
  (async done
    (let [^js el (append! (make-dock-with-items 3))]
      (js/requestAnimationFrame
       (fn [_]
         (js/requestAnimationFrame
          (fn [_]
            ;; Items should be focusable
            (let [^js items (.-children el)]
              (is (= "0" (.getAttribute (aget items 0) "tabindex")))
              (is (= "0" (.getAttribute (aget items 1) "tabindex")))
              (is (= "0" (.getAttribute (aget items 2) "tabindex"))))
            (done))))))))

;; ── Reconnect ───────────────────────────────────────────────────────────────
(deftest reconnect-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (some? (.-shadowRoot el)))))
