(ns baredom.components.x-organic-progress.x-organic-progress-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.x-organic-progress.x-organic-progress :as x-organic-progress]
   [baredom.components.x-organic-progress.model :as model]))

;; Register element once
(x-organic-progress/init!)

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
    (is (some? (shadow-part el "[part=branches]")))
    (is (some? (shadow-part el "[part=blooms]")))))

;; ── ARIA defaults (indeterminate) ───────────────────────────────────────────

(deftest aria-defaults-test
  (let [^js el (append! (make-el))]
    (is (= "progressbar" (.getAttribute el "role")))
    (is (= "0" (.getAttribute el "aria-valuemin")))
    (is (= "100" (.getAttribute el "aria-valuemax")))
    (is (= "true" (.getAttribute el "aria-busy")))
    (is (nil? (.getAttribute el "aria-valuenow")))
    (is (= "Loading..." (.getAttribute el "aria-valuetext")))))

;; ── ARIA with progress ──────────────────────────────────────────────────────

(deftest aria-with-progress-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-progress "60")
    (is (= "60" (.getAttribute el "aria-valuenow")))
    (is (= "60%" (.getAttribute el "aria-valuetext")))
    (is (nil? (.getAttribute el "aria-busy")))))

;; ── ARIA label ──────────────────────────────────────────────────────────────

(deftest aria-label-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-label "Uploading file")
    (is (= "Uploading file" (.getAttribute el "aria-label")))))

;; ── Property reflection ─────────────────────────────────────────────────────

(deftest progress-property-test
  (let [^js el (append! (make-el))]
    (set! (.-progress el) "50")
    (is (= "50" (.getAttribute el model/attr-progress)))
    (is (= 50.0 (.-progress el)))))

(deftest variant-property-test
  (let [^js el (append! (make-el))]
    (set! (.-variant el) "honeycomb")
    (is (= "honeycomb" (.getAttribute el model/attr-variant)))
    (is (= "honeycomb" (.-variant el)))))

(deftest bloom-property-test
  (let [^js el (append! (make-el))]
    ;; Default is true (no attribute)
    (is (true? (.-bloom el)))
    ;; Set to false
    (set! (.-bloom el) false)
    (is (= "false" (.getAttribute el model/attr-bloom)))
    (is (false? (.-bloom el)))))

(deftest density-property-test
  (let [^js el (append! (make-el))]
    (set! (.-density el) "dense")
    (is (= "dense" (.getAttribute el model/attr-density)))
    (is (= "dense" (.-density el)))))

(deftest seed-property-test
  (let [^js el (append! (make-el))]
    (set! (.-seed el) 99)
    (is (= "99" (.getAttribute el model/attr-seed)))
    (is (= 99 (.-seed el)))))

(deftest label-property-test
  (let [^js el (append! (make-el))]
    (set! (.-label el) "Processing")
    (is (= "Processing" (.getAttribute el model/attr-label)))
    (is (= "Processing" (.-label el)))))

;; ── Complete event ──────────────────────────────────────────────────────────

(deftest complete-aria-at-100-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-progress "100")
    (is (= "100" (.getAttribute el "aria-valuenow"))
        "aria-valuenow should reflect 100")
    (is (= "100%" (.getAttribute el "aria-valuetext"))
        "aria-valuetext should show 100%")))

;; ── Bloom attribute ─────────────────────────────────────────────────────────

(deftest bloom-false-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-bloom "false")
    (is (= "false" (.getAttribute el model/attr-bloom)))))

;; ── Variant reflects in structure ───────────────────────────────────────────

(deftest variant-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-variant "honeycomb")
    (is (= "honeycomb" (.getAttribute el model/attr-variant)))))

;; ── SVG has line elements when connected ────────────────────────────────────

(deftest svg-has-lines-test
  (let [^js el       (append! (make-el))
        ^js branches (shadow-part el "[part=branches]")]
    (is (pos? (.-childElementCount branches)))))

;; ── Nodes part exists ──────────────────────────────────────────────────────

(deftest nodes-part-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-part el "[part=nodes]")))))

;; ── Complete event fires ───────────────────────────────────────────────────

(deftest complete-event-fires-test
  (async done
    (let [^js el (append! (make-el))
          seen   (atom nil)]
      (.addEventListener el model/event-complete
                         (fn [^js e]
                           (reset! seen (.-detail e))))
      (.setAttribute el model/attr-progress "100")
      ;; Event fires from requestAnimationFrame — wait for it
      (js/setTimeout
       (fn []
         (is (some? @seen) "x-organic-progress-complete should have fired")
         (when @seen
           (is (= 100 (.-progress ^js @seen))))
         (done))
       200))))

;; ── Bloom-end event fires ──────────────────────────────────────────────────

(deftest bloom-end-event-fires-test
  (async done
    (let [^js el (append! (make-el))
          seen   (atom nil)]
      (.addEventListener el model/event-bloom-end
                         (fn [^js e]
                           (reset! seen (.-detail e))))
      (.setAttribute el model/attr-progress "100")
      ;; Bloom animation takes ~1.5s
      (js/setTimeout
       (fn []
         (is (some? @seen) "x-organic-progress-bloom-end should have fired")
         (when @seen
           (is (= 100 (.-progress ^js @seen))))
         (done))
       2500))))
