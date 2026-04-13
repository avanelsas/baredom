(ns baredom.components.x-metaball-cursor.x-metaball-cursor-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-metaball-cursor.x-metaball-cursor :as x]
            [baredom.components.x-metaball-cursor.model             :as model]))

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

;; ── Registration ────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=viewport]")))
    (is (some? (shadow-part el "svg")))))

(deftest default-blob-count-test
  (let [^js el (append! (make-el))
        blobs  (shadow-all el ".blob")]
    (is (= 5 (.-length blobs)))))

;; ── SVG filter structure ────────────────────────────────────────────────────
(deftest filter-structure-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "svg")
        ^js blur (.. svg (querySelector "feGaussianBlur"))
        ^js mat  (.. svg (querySelector "feColorMatrix"))
        ^js turb (.. svg (querySelector "feTurbulence"))
        ^js disp (.. svg (querySelector "feDisplacementMap"))]
    (is (some? blur) "feGaussianBlur should exist")
    (is (some? mat)  "feColorMatrix should exist")
    (is (some? turb) "feTurbulence should exist")
    (is (some? disp) "feDisplacementMap should exist")))

;; ── Blob count attribute ────────────────────────────────────────────────────
(deftest blob-count-attribute-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-blob-count "3")
    (append! el)
    (is (= 3 (.-length (shadow-all el ".blob"))))))

(deftest blob-count-change-test
  (let [^js el (append! (make-el))]
    (is (= 5 (.-length (shadow-all el ".blob"))))
    (.setAttribute el model/attr-blob-count "3")
    (is (= 3 (.-length (shadow-all el ".blob"))))
    (.setAttribute el model/attr-blob-count "7")
    (is (= 7 (.-length (shadow-all el ".blob"))))))

;; ── Noise attribute ─────────────────────────────────────────────────────────
(deftest noise-disabled-by-default-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "svg")
        ^js disp (.. svg (querySelector "feDisplacementMap"))]
    (is (= "0" (.getAttribute disp "scale")))))

(deftest noise-enabled-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-noise "")
    (append! el)
    (let [^js svg  (shadow-part el "svg")
          ^js disp (.. svg (querySelector "feDisplacementMap"))]
      (is (not= "0" (.getAttribute disp "scale"))))))

(deftest noise-toggle-test
  (let [^js el   (append! (make-el))
        ^js svg  (shadow-part el "svg")
        ^js disp (.. svg (querySelector "feDisplacementMap"))]
    (is (= "0" (.getAttribute disp "scale")))
    (.setAttribute el model/attr-noise "")
    (is (not= "0" (.getAttribute disp "scale")))
    (.removeAttribute el model/attr-noise)
    (is (= "0" (.getAttribute disp "scale")))))

;; ── Property accessors ──────────────────────────────────────────────────────
(deftest blob-count-property-test
  (let [^js el (append! (make-el))]
    (is (= 5 (.-blobCount el)))
    (set! (.-blobCount el) 8)
    (is (= "8" (.getAttribute el model/attr-blob-count)))
    (is (= 8 (.-blobCount el)))))

(deftest blob-size-property-test
  (let [^js el (append! (make-el))]
    (is (= 40 (.-blobSize el)))
    (set! (.-blobSize el) 60)
    (is (= "60" (.getAttribute el model/attr-blob-size)))
    (is (= 60 (.-blobSize el)))))

(deftest color-property-test
  (let [^js el (append! (make-el))]
    (is (= "#6366f1" (.-color el)))
    (set! (.-color el) "hotpink")
    (is (= "hotpink" (.getAttribute el model/attr-color)))
    (is (= "hotpink" (.-color el)))))

(deftest noise-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-noise el)))
    (set! (.-noise el) true)
    (is (.hasAttribute el model/attr-noise))
    (set! (.-noise el) false)
    (is (not (.hasAttribute el model/attr-noise)))))

(deftest blur-property-test
  (let [^js el (append! (make-el))]
    (is (= 12 (.-blur el)))
    (set! (.-blur el) 20)
    (is (= "20" (.getAttribute el model/attr-blur)))
    (is (= 20 (.-blur el)))))

;; ── Palette attribute ────────────────────────────────────────────────────────
(deftest palette-preset-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-palette "ocean")
    (append! el)
    (let [blobs (shadow-all el ".blob")
          ^js b0 (.item blobs 0)
          ^js b1 (.item blobs 1)]
      (is (not= (.. b0 -style -background) (.. b1 -style -background))
          "Ocean palette blobs should have different colors"))))

(deftest palette-custom-cycling-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-blob-count "4")
    (.setAttribute el model/attr-palette "red, blue")
    (append! el)
    (let [blobs (shadow-all el ".blob")
          ^js b0 (.item blobs 0)
          ^js b2 (.item blobs 2)]
      (is (= (.. b0 -style -background) (.. b2 -style -background))
          "Colors should cycle: blob 0 and blob 2 should match"))))

(deftest palette-property-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-palette el)))
    (set! (.-palette el) "neon")
    (is (= "neon" (.getAttribute el model/attr-palette)))
    (set! (.-palette el) "")
    (is (not (.hasAttribute el model/attr-palette)))))

;; ── Accessibility ───────────────────────────────────────────────────────────
(deftest a11y-test
  (let [^js el (append! (make-el))]
    (is (= "true" (.getAttribute el "aria-hidden")))
    (is (= "presentation" (.getAttribute el "role")))))

;; ── Pointer-events none ─────────────────────────────────────────────────────
(deftest pointer-events-none-test
  (let [^js el (append! (make-el))
        style  (.getComputedStyle js/window el)]
    (is (= "none" (.getPropertyValue style "pointer-events")))))

;; ── Reconnect: no duplicate blobs ───────────────────────────────────────────
(deftest reconnect-no-duplicates-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (is (= 5 (.-length (shadow-all el ".blob"))))
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (= 5 (.-length (shadow-all el ".blob"))))))
