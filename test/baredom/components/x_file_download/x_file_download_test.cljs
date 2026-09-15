(ns baredom.components.x-file-download.x-file-download-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [goog.object :as gobj]
            [baredom.components.x-file-download.x-file-download :as x]
            [baredom.components.x-file-download.model :as model]))

(x/init!)

(def ^:private original-picker (gobj/get js/window "showSaveFilePicker"))
(def ^:private original-fetch (gobj/get js/window "fetch"))
(def ^:private anchor-proto (.-prototype js/HTMLAnchorElement))
(def ^:private original-anchor-click (gobj/get anchor-proto "click"))

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(defn- restore-global! [^js obj k original]
  (if (undefined? original)
    (gobj/remove obj k)
    (gobj/set obj k original)))

(defn- restore-globals! []
  (restore-global! js/window "showSaveFilePicker" original-picker)
  (restore-global! js/window "fetch" original-fetch)
  (gobj/set anchor-proto "click" original-anchor-click))

(use-fixtures :each {:before cleanup-dom!
                     :after  (fn [] (cleanup-dom!) (restore-globals!))})

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
;; data: URL auto-download
;; ---------------------------------------------------------------------------

(deftest data-url-sets-download-attr-test
  (let [el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-href "data:text/plain,hello")
    (is (.hasAttribute anchor-el "download")
        "data: URL should force download attribute on anchor even without filename")))

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

(deftest picker-property-reflects-test
  (let [el (append! (make-el))]
    (set! (.-picker el) true)
    (is (.hasAttribute el model/attr-picker) "picker=true sets the attribute")
    (set! (.-picker el) false)
    (is (not (.hasAttribute el model/attr-picker)) "picker=false removes the attribute")))

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

;; ---------------------------------------------------------------------------
;; Picker path (stubbed showSaveFilePicker)
;; ---------------------------------------------------------------------------

(def ^:private outcome-events [model/event-success model/event-cancel model/event-error])

(defn- ^js make-picker-el [href filename]
  (let [el (make-el)]
    (.setAttribute el model/attr-picker "")
    (.setAttribute el model/attr-href href)
    (when filename (.setAttribute el model/attr-filename filename))
    (append! el)))

(defn- stub-picker! [f]
  (let [calls (atom [])]
    (gobj/set js/window "showSaveFilePicker"
              (fn [opts] (swap! calls conj opts) (f opts)))
    calls))

(defn- stub-fetch! [^js response]
  (gobj/set js/window "fetch" (fn [_ _] (js/Promise.resolve response))))

(defn- ^js opfs-handle [file-name]
  (.then (.. js/navigator -storage (getDirectory))
         (fn [^js dir] (.getFileHandle dir file-name #js {:create true}))))

(defn- ^js opfs-text [file-name]
  (-> (opfs-handle file-name)
      (.then (fn [^js handle] (.getFile handle)))
      (.then (fn [^js file] (.text file)))))

(defn- record-outcomes [^js el]
  (let [seen (atom [])]
    (doseq [event-name outcome-events]
      (.addEventListener el event-name (fn [^js e] (swap! seen conj (.-type e)))))
    seen))

(defn- ^js single-outcome [^js el]
  (let [seen (record-outcomes el)]
    (js/Promise.
     (fn [resolve]
       (doseq [event-name outcome-events]
         (.addEventListener el event-name
                            (fn [^js e] (js/setTimeout (fn [] (resolve [e @seen])) 0))
                            #js {:once true}))))))

(defn- click! [^js el]
  (.click (shadow-part el "[part=anchor]")))

(defn- rejection [name]
  (js/Promise.reject (js/DOMException. "stubbed" name)))

(defn- fail! [done]
  (fn [err]
    (is false (str "unexpected rejection: " err))
    (done)))

(deftest picker-writes-file-and-dispatches-success-test
  (async done
    (let [calls   (stub-picker! (fn [_] (opfs-handle "x-file-download-success.txt")))
          el      (make-picker-el "data:text/plain,hello" "hello.txt")
          outcome (single-outcome el)]
      (click! el)
      (is (.hasAttribute el "data-busy") "data-busy is set while saving")
      (is (= "true" (.getAttribute (shadow-part el "[part=anchor]") "aria-busy"))
          "aria-busy is set on the anchor while saving")
      (is (= "hello.txt" (.-suggestedName (first @calls)))
          "the picker receives filename as suggestedName")
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-success] seen) "only x-file-download-success fires")
                   (is (= "x-file-download-success.txt" (.. e -detail -filename))
                       "the detail carries the chosen file name")
                   (is (not (.hasAttribute el "data-busy")) "data-busy is cleared")
                   (is (not (.hasAttribute (shadow-part el "[part=anchor]") "aria-busy"))
                       "aria-busy is cleared")
                   (opfs-text "x-file-download-success.txt")))
          (.then (fn [text]
                   (is (= "hello" text) "the file holds the fetched content")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-writes-empty-file-for-empty-body-test
  (async done
    (stub-picker! (fn [_] (opfs-handle "x-file-download-empty.txt")))
    (stub-fetch! (js/Response. nil #js {:status 204}))
    (let [el      (make-picker-el "/empty" "empty.txt")
          outcome (single-outcome el)]
      (click! el)
      (-> outcome
          (.then (fn [[_ seen]]
                   (is (= [model/event-success] seen) "only x-file-download-success fires")
                   (opfs-text "x-file-download-empty.txt")))
          (.then (fn [text]
                   (is (= "" text) "the file is empty")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-abort-dispatches-cancel-test
  (async done
    (stub-picker! (fn [_] (rejection "AbortError")))
    (let [el      (make-picker-el "data:text/plain,hello" "hello.txt")
          outcome (single-outcome el)]
      (click! el)
      (-> outcome
          (.then (fn [[_ seen]]
                   (is (= [model/event-cancel] seen) "only x-file-download-cancel fires")
                   (is (not (.hasAttribute el "data-busy")) "data-busy is cleared")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-security-error-falls-back-to-download-test
  (async done
    (stub-picker! (fn [_] (rejection "SecurityError")))
    (let [el   (make-picker-el "data:text/plain,hello" "hello.txt")
          seen (record-outcomes el)]
      (click! el)
      (gobj/set anchor-proto "click"
                (fn []
                  (this-as ^js a
                    (is (= "data:text/plain,hello" (.-href a)) "the fallback anchor downloads href")
                    (is (= "hello.txt" (.getAttribute a "download")) "the fallback anchor carries download")
                    (js/setTimeout
                     (fn []
                       (is (= [] @seen) "no outcome event fires")
                       (is (not (.hasAttribute el "data-busy")) "data-busy is cleared")
                       (done))
                     0)))))))

(deftest picker-other-error-dispatches-pick-error-test
  (async done
    (stub-picker! (fn [_] (js/Promise.reject (js/TypeError. "bad options"))))
    (let [el      (make-picker-el "data:text/plain,hello" "hello.txt")
          outcome (single-outcome el)]
      (click! el)
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-error] seen) "only x-file-download-error fires")
                   (is (= "TypeError" (.. e -detail -error)) "the detail carries the error name")
                   (is (= "pick" (.. e -detail -phase)) "the phase is pick")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-sync-throw-dispatches-pick-error-test
  (async done
    (stub-picker! (fn [_] (throw (js/TypeError. "thrown"))))
    (let [el      (make-picker-el "data:text/plain,hello" "hello.txt")
          outcome (single-outcome el)]
      (click! el)
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-error] seen) "only x-file-download-error fires")
                   (is (= "TypeError" (.. e -detail -error)) "the detail carries the thrown error name")
                   (is (= "pick" (.. e -detail -phase)) "the phase is pick")
                   (is (not (.hasAttribute el "data-busy")) "data-busy is cleared")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-http-error-dispatches-fetch-error-test
  (async done
    (stub-picker! (fn [_] (js/Promise.resolve #js {})))
    (stub-fetch! (js/Response. "" #js {:status 404}))
    (let [el      (make-picker-el "/missing.txt" nil)
          outcome (single-outcome el)]
      (click! el)
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-error] seen) "only x-file-download-error fires")
                   (is (= "HTTP 404" (.. e -detail -error)) "the detail carries the HTTP status")
                   (is (= "fetch" (.. e -detail -phase)) "the phase is fetch")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-busy-blocks-second-save-test
  (let [calls (stub-picker! (fn [_] (js/Promise. (fn [_ _]))))
        el    (make-picker-el "data:text/plain,hello" "hello.txt")]
    (click! el)
    (click! el)
    (is (= 1 (count @calls)) "a click while busy does not open a second picker")
    (.removeAttribute el "data-busy")
    (click! el)
    (is (= 1 (count @calls)) "removing data-busy does not allow a second save")))

(deftest picker-disconnect-while-picking-dispatches-abort-error-test
  (async done
    (stub-picker! (fn [_] (js/Promise.resolve #js {})))
    (let [el      (make-picker-el "data:text/plain,hello" "hello.txt")
          outcome (single-outcome el)]
      (click! el)
      (.remove el)
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-error] seen) "only x-file-download-error fires")
                   (is (= "AbortError" (.. e -detail -error)) "the error is AbortError")
                   (is (= "fetch" (.. e -detail -phase)) "the phase is fetch")
                   (done)))
          (.catch (fail! done))))))

(deftest picker-disconnect-while-writing-dispatches-abort-error-test
  (async done
    (let [el      (make-picker-el "data:text/plain,hello" "hello.txt")
          body    (js/ReadableStream.
                   #js {:pull (fn [_] (.remove el) (js/Promise. (fn [_ _])))}
                   #js {:highWaterMark 0})
          outcome (single-outcome el)]
      (stub-picker! (fn [_] (opfs-handle "x-file-download-disconnect-write.txt")))
      (stub-fetch! (js/Response. body))
      (click! el)
      (-> outcome
          (.then (fn [[^js e seen]]
                   (is (= [model/event-error] seen) "only x-file-download-error fires")
                   (is (= "AbortError" (.. e -detail -error)) "the error is AbortError")
                   (is (= "write" (.. e -detail -phase)) "the phase is write")
                   (is (not (.hasAttribute el "data-busy")) "data-busy is cleared")
                   (done)))
          (.catch (fail! done))))))

(deftest no-picker-attribute-skips-picker-test
  (let [calls     (stub-picker! (fn [_] (rejection "AbortError")))
        el        (append! (make-el))
        anchor-el (shadow-part el "[part=anchor]")]
    (.setAttribute el model/attr-href "data:text/plain,hello")
    (.addEventListener anchor-el "click" (fn [^js e] (.preventDefault e)))
    (.click anchor-el)
    (is (= 0 (count @calls)) "the picker is not called without the picker attribute")))

(deftest cancelled-click-skips-picker-test
  (let [calls (stub-picker! (fn [_] (rejection "AbortError")))
        el    (make-picker-el "data:text/plain,hello" "hello.txt")]
    (.addEventListener el model/event-click (fn [^js e] (.preventDefault e)))
    (click! el)
    (is (= 0 (count @calls)) "a cancelled x-file-download-click does not open the picker")
    (is (not (.hasAttribute el "data-busy")) "data-busy is not set")))
