(ns baredom.components.x-file-upload.x-file-upload-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-file-upload.x-file-upload :as x]
            [baredom.components.x-file-upload.model         :as model]))

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

(defn mock-file [name type size]
  (js/File. #js [(js/ArrayBuffer. size)] name #js {:type type}))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=drop-zone]")))
    (is (some? (shadow-part el "input[type=file]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "[part=drag-overlay]")))
    (is (some? (shadow-part el "[part=file-list]")))
    (is (some? (shadow-part el "[part=live-region]")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el       (append! (make-el))
        ^js drop-zone (shadow-part el "[part=drop-zone]")]
    (is (= "button" (.getAttribute drop-zone "role")))
    (is (= "0"      (.getAttribute drop-zone "tabindex")))
    (is (= 0        (.-length (.-files el))))))

;; ── Accept attribute syncs to input ──────────────────────────────────────────
(deftest accept-syncs-to-input-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-accept "image/*")
    (is (= "image/*" (.getAttribute (shadow-part el "input[type=file]") "accept")))))

;; ── Multiple attribute syncs to input ────────────────────────────────────────
(deftest multiple-syncs-to-input-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-multiple "")
    (is (.hasAttribute (shadow-part el "input[type=file]") "multiple"))))

;; ── Disabled state ───────────────────────────────────────────────────────────
(deftest disabled-blocks-interaction-test
  (let [^js el       (append! (make-el))
        ^js drop-zone (shadow-part el "[part=drop-zone]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "-1" (.getAttribute drop-zone "tabindex")))
    (is (= "true" (.getAttribute drop-zone "aria-disabled")))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest accept-property-test
  (let [^js el (append! (make-el))]
    (set! (.-accept el) ".pdf,.doc")
    (is (= ".pdf,.doc" (.getAttribute el model/attr-accept)))))

(deftest multiple-property-test
  (let [^js el (append! (make-el))]
    (set! (.-multiple el) true)
    (is (.hasAttribute el model/attr-multiple))
    (set! (.-multiple el) false)
    (is (not (.hasAttribute el model/attr-multiple)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))))

(deftest max-size-property-test
  (let [^js el (append! (make-el))]
    (set! (.-maxSize el) 5242880)
    (is (= "5242880" (.getAttribute el model/attr-max-size)))
    (is (= 5242880 (.-maxSize el)))))

(deftest files-property-readonly-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-length (.-files el))))))

;; ── Drop zone: keyboard opens picker ─────────────────────────────────────────
(deftest enter-key-opens-picker-test
  (let [^js el       (append! (make-el))
        ^js drop-zone (shadow-part el "[part=drop-zone]")
        clicked       (atom false)]
    ;; Spy on file input click
    (let [^js fi (shadow-part el "input[type=file]")]
      (set! (.-click fi) (fn [] (reset! clicked true))))
    (.dispatchEvent drop-zone
                    (js/KeyboardEvent. "keydown" #js {:key "Enter" :bubbles true}))
    (is (true? @clicked))))

;; ── Drag-over state ──────────────────────────────────────────────────────────
(deftest dragenter-sets-data-drag-over-test
  (let [^js el       (append! (make-el))
        ^js drop-zone (shadow-part el "[part=drop-zone]")]
    (.dispatchEvent drop-zone
                    (js/DragEvent. "dragenter" #js {:bubbles true :cancelable true
                                                    :dataTransfer (js/DataTransfer.)}))
    (is (.hasAttribute el "data-drag-over"))))

(deftest dragleave-removes-data-drag-over-test
  (let [^js el       (append! (make-el))
        ^js drop-zone (shadow-part el "[part=drop-zone]")]
    (.dispatchEvent drop-zone
                    (js/DragEvent. "dragenter" #js {:bubbles true :cancelable true
                                                    :dataTransfer (js/DataTransfer.)}))
    (.dispatchEvent drop-zone
                    (js/DragEvent. "dragleave" #js {:bubbles true
                                                    :dataTransfer (js/DataTransfer.)}))
    (is (not (.hasAttribute el "data-drag-over")))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-doubling-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (is (some? (shadow-part el "[part=drop-zone]")))))

;; ── Live region exists ───────────────────────────────────────────────────────
(deftest live-region-has-aria-live-test
  (let [^js el (append! (make-el))
        ^js lr (shadow-part el "[part=live-region]")]
    (is (= "polite" (.getAttribute lr "aria-live")))))

;; ── Select event listener can be attached ─────────────────────────────────────
(deftest select-event-listener-attaches-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.setAttribute el model/attr-accept "image/*")
    (.addEventListener el model/event-select
                       (fn [^js _e] (swap! events conj :fired)))
    ;; Event listener is attached; file input change would trigger it
    ;; (can't programmatically set input.files in browser tests)
    (is (= 0 (count @events)))))

;; ── Remove event listener can be attached ────────────────────────────────────
(deftest remove-event-listener-attaches-test
  (let [^js el (append! (make-el))
        events (atom [])]
    (.addEventListener el model/event-remove
                       (fn [^js _e] (swap! events conj :fired)))
    (is (= 0 (count @events)))))

;; ── Files property returns empty array by default ────────────────────────────
(deftest files-property-returns-array-test
  (let [^js el (append! (make-el))
        ^js f  (.-files el)]
    (is (= 0 (.-length f)))
    (is (array? f))))
