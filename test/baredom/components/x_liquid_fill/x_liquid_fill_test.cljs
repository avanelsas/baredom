(ns baredom.components.x-liquid-fill.x-liquid-fill-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.x-liquid-fill.x-liquid-fill :as x-liquid-fill]
   [baredom.components.x-liquid-fill.model :as model]))

;; Register element once
(x-liquid-fill/init!)

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- make-el [] (.createElement js/document model/tag-name))
(defn- append! [^js el] (.appendChild (.-body js/document) el) el)
(defn- shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Fixtures ────────────────────────────────────────────────────────────────

(defn- cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each
  {:before cleanup-dom!
   :after  cleanup-dom!})

;; ── Registration ────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────

(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=svg]")))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "slot")))))

;; ── SVG has aria-hidden ─────────────────────────────────────────────────────

(deftest svg-aria-hidden-test
  (let [^js el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (= "true" (.getAttribute svg "aria-hidden")))))

;; ── SVG has wave path elements ──────────────────────────────────────────────

(deftest svg-has-wave-paths-test
  (let [^js el (append! (make-el))
        ^js root (.-shadowRoot el)
        paths (.querySelectorAll root ".wave-layer")]
    ;; Default 3 layers active, but 5 path elements exist
    (is (= 5 (.-length paths)))))

;; ── SVG has gradient and filter ─────────────────────────────────────────────

(deftest svg-has-gradient-test
  (let [^js el (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? (.querySelector root "radialGradient")))))

(deftest svg-has-filter-test
  (let [^js el (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? (.querySelector root "filter")))))

;; ── Property reflection: orientation ────────────────────────────────────────

(deftest orientation-property-test
  (let [^js el (append! (make-el))]
    ;; Default
    (is (= "vertical" (.-orientation el)))
    ;; Set
    (set! (.-orientation el) "horizontal")
    (is (= "horizontal" (.getAttribute el model/attr-orientation)))
    (is (= "horizontal" (.-orientation el)))))

;; ── Property reflection: mode ───────────────────────────────────────────────

(deftest mode-property-test
  (let [^js el (append! (make-el))]
    (is (= "fill" (.-mode el)))
    (set! (.-mode el) "bar")
    (is (= "bar" (.getAttribute el model/attr-mode)))
    (is (= "bar" (.-mode el)))))

;; ── Property reflection: theme ──────────────────────────────────────────────

(deftest theme-property-test
  (let [^js el (append! (make-el))]
    (is (= "gold" (.-theme el)))
    (set! (.-theme el) "water")
    (is (= "water" (.getAttribute el model/attr-theme)))
    (is (= "water" (.-theme el)))))

;; ── Property reflection: waveIntensity ──────────────────────────────────────

(deftest wave-intensity-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.5 (.-waveIntensity el)))
    (set! (.-waveIntensity el) 0.8)
    (is (= "0.8" (.getAttribute el model/attr-wave-intensity)))
    (is (= 0.8 (.-waveIntensity el)))))

;; ── Property reflection: splashIntensity ────────────────────────────────────

(deftest splash-intensity-property-test
  (let [^js el (append! (make-el))]
    (is (= 0.7 (.-splashIntensity el)))
    (set! (.-splashIntensity el) 0.3)
    (is (= "0.3" (.getAttribute el model/attr-splash-intensity)))
    (is (= 0.3 (.-splashIntensity el)))))

;; ── Property reflection: layers ─────────────────────────────────────────────

(deftest layers-property-test
  (let [^js el (append! (make-el))]
    (is (= 3 (.-layers el)))
    (set! (.-layers el) 5)
    (is (= "5" (.getAttribute el model/attr-layers)))
    (is (= 5 (.-layers el)))))

;; ── Property reflection: disabled ───────────────────────────────────────────

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-disabled el)))
    (set! (.-disabled el) true)
    (is (= "" (.getAttribute el model/attr-disabled)))
    (is (true? (.-disabled el)))
    (set! (.-disabled el) false)
    (is (nil? (.getAttribute el model/attr-disabled)))
    (is (false? (.-disabled el)))))

;; ── Property: progress is read-only ─────────────────────────────────────────

(deftest progress-read-only-test
  (let [^js el (append! (make-el))]
    (is (= 0.0 (.-progress el)))))

;; ── Null clears attributes ──────────────────────────────────────────────────

(deftest null-clears-wave-intensity-test
  (let [^js el (append! (make-el))]
    (set! (.-waveIntensity el) 0.9)
    (is (= "0.9" (.getAttribute el model/attr-wave-intensity)))
    (set! (.-waveIntensity el) nil)
    (is (nil? (.getAttribute el model/attr-wave-intensity)))
    (is (= 0.5 (.-waveIntensity el)) "getter returns default after removal")))

;; ── Wave paths get d attribute after resize ─────────────────────────────────

(deftest paths-have-d-after-resize-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      ;; Wait for ResizeObserver + RAF
      (js/setTimeout
       (fn []
         (let [^js root (.-shadowRoot el)
               ^js path (.querySelector root "[data-layer=\"2\"]")
               d (.getAttribute path "d")]
           (is (some? d) "front wave path should have d attribute")
           (when d
             (is (.startsWith d "M") "path d should start with M")))
         (done))
       300))))

;; ── Disabled renders flat paths ─────────────────────────────────────────────

(deftest disabled-renders-flat-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-disabled "")
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js root (.-shadowRoot el)
               ^js path (.querySelector root "[data-layer=\"2\"]")
               d (.getAttribute path "d")]
           (is (some? d) "disabled path should have d attribute")
           (when d
             ;; Static path uses L commands, no Q curves
             (is (.includes d "L") "static path uses L commands")))
         (done))
       300))))

;; ── Layers attribute changes visible path count ─────────────────────────────

(deftest layers-changes-visible-paths-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-layers "2")
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js root (.-shadowRoot el)
               paths (.querySelectorAll root ".wave-layer")]
           ;; layer 0 and 1 visible, 2-4 hidden
           (is (not= "none" (.. (aget paths 0) -style -display)))
           (is (not= "none" (.. (aget paths 1) -style -display)))
           (is (= "none" (.. (aget paths 2) -style -display)))
           (is (= "none" (.. (aget paths 3) -style -display)))
           (is (= "none" (.. (aget paths 4) -style -display))))
         (done))
       300))))

;; ── Progress event fires with correct detail ────────────────────────────────

(deftest progress-event-detail-test
  (async done
    (let [;; Scrollable container
          ^js box (.createElement js/document "div")
          _       (set! (.. box -style -cssText)
                        "overflow:auto;height:100px;width:200px;")
          ^js inner (.createElement js/document "div")
          _       (set! (.. inner -style -height) "500px")
          _       (.appendChild box inner)
          _       (set! (.-id box) "xlf-test-scroll")
          _       (.appendChild (.-body js/document) box)
          ;; Element targeting that container. Enabled so the rAF-driven
          ;; animation loop dispatches progress events as it lerps toward
          ;; the new scroll target.
          ^js el  (make-el)]
      (.setAttribute el "target" "#xlf-test-scroll")
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "100px")
      (.appendChild (.-body js/document) el)
      ;; Wait for connectedCallback + target resolution
      (js/setTimeout
       (fn []
         (let [events (atom [])]
           (.addEventListener el model/event-progress
                              (fn [^js e] (swap! events conj (.-detail e))))
           ;; Scroll the container
           (set! (.-scrollTop box) 100)
           (.dispatchEvent box (js/Event. "scroll"))
           ;; Give the rAF loop a few frames to dispatch
           (js/setTimeout
            (fn []
              (is (pos? (count @events)) "progress event should fire")
              (when (pos? (count @events))
                (let [^js d (first @events)]
                  (is (number? (.-progress d)) "detail has progress")
                  (is (number? (.-velocity d)) "detail has velocity")))
              (.remove box)
              (done))
            300)))
       500))))

;; ── Reconnect without listener doubling ─────────────────────────────────────

(deftest reconnect-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      (js/setTimeout
       (fn []
         ;; Disconnect and reconnect
         (.remove el)
         (append! el)
         (js/setTimeout
          (fn []
            ;; Should still have shadow DOM intact
            (is (some? (.-shadowRoot el)))
            (is (some? (shadow-part el "[part=svg]")))
            (done))
          300))
       200))))
