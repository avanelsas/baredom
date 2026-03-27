(ns baredom.components.x-file-download.x-file-download-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-file-download.x-file-download :as x]
            [baredom.components.x-file-download.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el selector] (.querySelector (.-shadowRoot el) selector))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-file-download should be registered"))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                     "shadow root should exist")
    (is (some? (shadow-part el "[part=anchor]"))       "anchor should exist")
    (is (some? (shadow-part el "[part=icon]"))         "icon span should exist")
    (is (some? (shadow-part el "[part=content]"))      "content span should exist")
    (is (some? (shadow-part el "slot"))                "default slot should exist")))

(deftest anchor-is-a-element-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (is (= "A" (.-tagName anchor-el))
        "anchor part should be an <a> element")))

;; ---------------------------------------------------------------------------
;; href attribute
;; ---------------------------------------------------------------------------

(deftest href-sets-anchor-href-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-href "https://example.com/file.pdf")
    (is (= "https://example.com/file.pdf" (.getAttribute anchor-el "href"))
        "href attribute should set href on anchor")))

;; ---------------------------------------------------------------------------
;; filename attribute
;; ---------------------------------------------------------------------------

(deftest filename-sets-download-attr-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-filename "report.pdf")
    (is (= "report.pdf" (.getAttribute anchor-el "download"))
        "filename attribute should set download attr on anchor")))

(deftest empty-filename-removes-download-attr-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-filename "report.pdf")
    (.setAttribute el model/attr-filename "")
    (is (not (.hasAttribute anchor-el "download"))
        "empty filename should remove download attr from anchor")))

;; ---------------------------------------------------------------------------
;; disabled attribute
;; ---------------------------------------------------------------------------

(deftest disabled-sets-data-disabled-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (is (.hasAttribute el "data-disabled")
        "data-disabled should be set on host when disabled")))

(deftest disabled-sets-aria-disabled-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute anchor-el "aria-disabled"))
        "aria-disabled=true should be set on anchor when disabled")))

(deftest not-disabled-clears-data-disabled-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.removeAttribute el model/attr-disabled)
    (is (not (.hasAttribute el "data-disabled"))
        "data-disabled should be removed when disabled attr removed")))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------

(deftest href-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-href el) "https://example.com/data.csv")
    (is (= "https://example.com/data.csv" (.getAttribute el model/attr-href))
        "setting href property should reflect to attribute")))

(deftest href-property-read-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-href "https://example.com/data.csv")
    (is (= "https://example.com/data.csv" (.-href el))
        "href property should read from attribute")))

(deftest filename-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-filename el) "data.csv")
    (is (= "data.csv" (.getAttribute el model/attr-filename))
        "setting filename property should reflect to attribute")))

(deftest filename-property-read-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-filename "data.csv")
    (is (= "data.csv" (.-filename el))
        "filename property should read from attribute")))

(deftest disabled-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled)
        "setting disabled=true should set attribute")
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled))
        "setting disabled=false should remove attribute")))

;; ---------------------------------------------------------------------------
;; aria-label forwarding
;; ---------------------------------------------------------------------------

(deftest aria-label-forwarded-to-anchor-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-aria-label "Download report")
    (is (= "Download report" (.getAttribute anchor-el "aria-label"))
        "aria-label should be forwarded to anchor")))

(deftest no-aria-label-removes-from-anchor-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-aria-label "Download report")
    (.removeAttribute el model/attr-aria-label)
    (is (nil? (.getAttribute anchor-el "aria-label"))
        "removing aria-label should clear it from anchor")))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------

(deftest click-dispatches-event-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")
        received  (atom nil)]
    (.setAttribute el model/attr-href "https://example.com/file.pdf")
    (.setAttribute el model/attr-filename "file.pdf")
    (.addEventListener el model/event-click
                       (fn [^js e]
                         (reset! received e)
                         (.preventDefault e)))
    (.click anchor-el)
    (is (some? @received)
        "x-file-download-click should be dispatched on click")
    (is (= "https://example.com/file.pdf" (.-href (.-detail @received)))
        "event detail should contain href")
    (is (= "file.pdf" (.-filename (.-detail @received)))
        "event detail should contain filename")))

(deftest disabled-click-does-not-dispatch-event-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")
        received  (atom nil)]
    (.setAttribute el model/attr-disabled "")
    (.addEventListener el model/event-click
                       (fn [^js e] (reset! received e)))
    (.click anchor-el)
    (is (nil? @received)
        "x-file-download-click should not be dispatched when disabled")))

(deftest cancelled-event-prevents-default-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")
        click-prevented (atom false)]
    (.setAttribute el model/attr-href "https://example.com/file.pdf")
    ;; Cancel the custom event
    (.addEventListener el model/event-click
                       (fn [^js e] (.preventDefault e)))
    ;; Listen on the anchor click bubble phase AFTER the component listener
    ;; so defaultPrevented reflects what the component handler did
    (.addEventListener anchor-el "click"
                       (fn [^js e] (reset! click-prevented (.-defaultPrevented e))))
    (.click anchor-el)
    (is (= true @click-prevented)
        "cancelled x-file-download-click should prevent native anchor click")))
