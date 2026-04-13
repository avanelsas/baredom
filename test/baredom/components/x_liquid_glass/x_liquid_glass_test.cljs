(ns baredom.components.x-liquid-glass.x-liquid-glass-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.x-liquid-glass.x-liquid-glass :as x-liquid-glass]
   [baredom.components.x-liquid-glass.model :as model]))

;; Register element once
(x-liquid-glass/init!)

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
    (is (some? (shadow-part el "[part=glass]")))
    (is (some? (shadow-part el "[part=gradient]")))
    (is (some? (shadow-part el "[part=specular]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "slot")))))

;; ── SVG has aria-hidden ─────────────────────────────────────────────────────

(deftest svg-aria-hidden-test
  (let [^js el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (= "true" (.getAttribute svg "aria-hidden")))))

;; ── SVG has goo filter ──────────────────────────────────────────────────────

(deftest svg-has-goo-filter-test
  (let [^js el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (some? (.querySelector svg "filter")))
    (is (some? (.querySelector svg "feGaussianBlur")))
    (is (some? (.querySelector svg "feColorMatrix")))))

;; ── Core ellipse exists ─────────────────────────────────────────────────────

(deftest core-ellipse-exists-test
  (let [^js el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (some? (.querySelector svg "ellipse.blob-core")))))

;; ── Property reflection ─────────────────────────────────────────────────────

(deftest blobs-property-test
  (let [^js el (append! (make-el))]
    (set! (.-blobs el) 7)
    (is (= "7" (.getAttribute el model/attr-blobs)))
    (is (= 7 (.-blobs el)))))

(deftest speed-property-test
  (let [^js el (append! (make-el))]
    (set! (.-speed el) 1.0)
    (is (= "1" (.getAttribute el model/attr-speed)))
    (is (= 1.0 (.-speed el)))))

(deftest amplitude-property-test
  (let [^js el (append! (make-el))]
    (set! (.-amplitude el) 0.25)
    (is (= "0.25" (.getAttribute el model/attr-amplitude)))
    (is (= 0.25 (.-amplitude el)))))

(deftest blur-property-test
  (let [^js el (append! (make-el))]
    (set! (.-blur el) 20)
    (is (= "20" (.getAttribute el model/attr-blur)))
    (is (= 20.0 (.-blur el)))))

(deftest goo-property-test
  (let [^js el (append! (make-el))]
    (set! (.-goo el) 15)
    (is (= "15" (.getAttribute el model/attr-goo)))
    (is (= 15.0 (.-goo el)))))

(deftest tint-property-test
  (let [^js el (append! (make-el))]
    (set! (.-tint el) "rgba(255,0,0,0.2)")
    (is (= "rgba(255,0,0,0.2)" (.getAttribute el model/attr-tint)))
    (is (= "rgba(255,0,0,0.2)" (.-tint el)))))

(deftest specular-property-test
  (let [^js el (append! (make-el))]
    ;; Default is false
    (is (false? (.-specular el)))
    ;; Set to true
    (set! (.-specular el) true)
    (is (= "" (.getAttribute el model/attr-specular)))
    (is (true? (.-specular el)))
    ;; Set back to false
    (set! (.-specular el) false)
    (is (nil? (.getAttribute el model/attr-specular)))
    (is (false? (.-specular el)))))

(deftest specular-size-property-test
  (let [^js el (append! (make-el))]
    (set! (.-specularSize el) 0.6)
    (is (= "0.6" (.getAttribute el model/attr-specular-size)))
    (is (= 0.6 (.-specularSize el)))))

(deftest specular-intensity-property-test
  (let [^js el (append! (make-el))]
    (set! (.-specularIntensity el) 0.8)
    (is (= "0.8" (.getAttribute el model/attr-specular-intensity)))
    (is (= 0.8 (.-specularIntensity el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    ;; Default is false
    (is (false? (.-disabled el)))
    ;; Set to true
    (set! (.-disabled el) true)
    (is (= "" (.getAttribute el model/attr-disabled)))
    (is (true? (.-disabled el)))
    ;; Set back to false
    (set! (.-disabled el) false)
    (is (nil? (.getAttribute el model/attr-disabled)))
    (is (false? (.-disabled el)))))

(deftest mode-property-test
  (let [^js el (append! (make-el))]
    (set! (.-mode el) "submerged")
    (is (= "submerged" (.getAttribute el model/attr-mode)))
    (is (= "submerged" (.-mode el)))))

(deftest frost-property-test
  (let [^js el (append! (make-el))]
    (set! (.-frost el) 0.8)
    (is (= "0.8" (.getAttribute el model/attr-frost)))
    (is (= 0.8 (.-frost el)))))

(deftest color1-property-test
  (let [^js el (append! (make-el))]
    (set! (.-color1 el) "red")
    (is (= "red" (.getAttribute el model/attr-color-1)))
    (is (= "red" (.-color1 el)))))

(deftest color2-property-test
  (let [^js el (append! (make-el))]
    (set! (.-color2 el) "blue")
    (is (= "blue" (.getAttribute el model/attr-color-2)))
    (is (= "blue" (.-color2 el)))))

;; ── Default property values ─────────────────────────────────────────────────

(deftest default-property-values-test
  (let [^js el (append! (make-el))]
    (is (= 5 (.-blobs el)))
    (is (= 0.3 (.-speed el)))
    (is (= 0.15 (.-amplitude el)))
    (is (= 12.0 (.-blur el)))
    (is (= 10.0 (.-goo el)))
    (is (nil? (.-tint el)))
    (is (false? (.-specular el)))
    (is (= 0.4 (.-specularSize el)))
    (is (= 0.3 (.-specularIntensity el)))
    (is (false? (.-disabled el)))
    (is (= "surface" (.-mode el)))
    (is (= 0.5 (.-frost el)))
    (is (nil? (.-color1 el)))
    (is (nil? (.-color2 el)))))

;; ── Null property clearing removes attribute ────────────────────────────────

(deftest null-clears-blobs-test
  (let [^js el (append! (make-el))]
    (set! (.-blobs el) 7)
    (is (= "7" (.getAttribute el model/attr-blobs)))
    (set! (.-blobs el) nil)
    (is (nil? (.getAttribute el model/attr-blobs)))
    (is (= 5 (.-blobs el)) "getter returns default after removal")))

(deftest null-clears-tint-test
  (let [^js el (append! (make-el))]
    (set! (.-tint el) "red")
    (set! (.-tint el) nil)
    (is (nil? (.getAttribute el model/attr-tint)))))

;; ── Satellites created on connect ──────────────────────────────────────────

(deftest satellites-created-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (append! el)
      ;; Wait for ResizeObserver callback
      (js/setTimeout
       (fn []
         (let [^js svg (shadow-part el "[part=svg]")
               sats (.querySelectorAll svg "ellipse[class^='blob-sat-']")]
           (is (= 5 (.-length sats)) "default 5 satellites"))
         (done))
       200))))

;; ── Blobs attribute changes satellite count ─────────────────────────────────

(deftest blobs-attribute-changes-count-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-blobs "7")
      (set! (.. el -style -width) "300px")
      (set! (.. el -style -height) "200px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js svg (shadow-part el "[part=svg]")
               sats (.querySelectorAll svg "ellipse[class^='blob-sat-']")]
           (is (= 7 (.-length sats)) "should have 7 satellites"))
         (done))
       200))))

;; ── Resize updates SVG viewBox ──────────────────────────────────────────────

(deftest resize-updates-viewbox-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js svg (shadow-part el "[part=svg]")]
           (set! (.. el -style -width) "400px")
           (set! (.. el -style -height) "300px")
           (js/setTimeout
            (fn []
              (let [vb (.getAttribute svg "viewBox")]
                (is (some? vb))
                (is (.includes vb "400")))
              (done))
            200)))
       200))))
