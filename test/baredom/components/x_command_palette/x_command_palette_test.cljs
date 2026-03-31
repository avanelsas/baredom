(ns baredom.components.x-command-palette.x-command-palette-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-command-palette.x-command-palette :as x]
            [baredom.components.x-command-palette.model :as model]))

(x/init!)

(defn cleanup-dom!
  []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el
  []
  (.createElement js/document model/tag-name))

(defn ^js append!
  [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part
  [^js el s]
  (.querySelector (.-shadowRoot el) s))

;; ---------------------------------------------------------------------------

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "Custom element should be registered"))

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el)) "Shadow root should be attached")
    (is (some? (shadow-part el "[part=overlay]"))   "overlay part should exist")
    (is (some? (shadow-part el "[part=panel]"))      "panel part should exist")
    (is (some? (shadow-part el "[part=input]"))      "input part should exist")
    (is (some? (shadow-part el "[part=list]"))       "list part should exist")
    (is (some? (shadow-part el "[part=empty]"))      "empty part should exist")
    (is (some? (shadow-part el "[part=clear-btn]"))  "clear-btn part should exist")))

(deftest panel-roles-test
  (let [el    (append! (make-el))
        ^js p (shadow-part el "[part=panel]")]
    (is (= "dialog" (.getAttribute p "role")))
    (is (= "true"   (.getAttribute p "aria-modal")))))

(deftest items-property-test
  (let [el (append! (make-el))]
    (is (array? (.-items el)) "items getter returns JS array")
    (set! (.-items el) #js [#js {:id "1" :label "Item one"}
                             #js {:id "2" :label "Item two"}])
    (is (= 2 (alength (.-items el))) "setter stores items")
    ;; After setting items, list should contain rendered item elements
    (let [^js list-el (shadow-part el "[part=list]")
          ^js items   (.querySelectorAll list-el "[part=item]")]
      (is (= 2 (alength items)) "rendered item count matches"))))

(deftest open-close-attribute-test
  (let [el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-open)) "initially closed")
    (.setAttribute el model/attr-open "")
    (is (.hasAttribute el model/attr-open) "open attr set")
    (.removeAttribute el model/attr-open)
    (is (not (.hasAttribute el model/attr-open)) "closed after remove")))

(deftest open-method-test
  (let [el (append! (make-el))]
    (.open el)
    (is (.hasAttribute el model/attr-open) "open() sets open attr")))

(deftest close-method-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (.close el)
    (is (not (.hasAttribute el model/attr-open)) "close() removes open attr")))

(deftest toggle-method-test
  (let [el (append! (make-el))]
    (.toggle el)
    (is (.hasAttribute el model/attr-open) "toggle opens when closed")
    (.toggle el)
    (is (not (.hasAttribute el model/attr-open)) "toggle closes when open")))

(deftest open-event-test
  (async done
    (let [el     (append! (make-el))
          fired  (atom false)]
      (.addEventListener el model/event-open
                         (fn [_] (reset! fired true)))
      (.open el)
      (js/setTimeout
       (fn []
         (is (= true @fired) "x-command-palette-open event fired")
         (done))
       0))))

(deftest open-request-cancelable-test
  (let [el (append! (make-el))]
    (.addEventListener el model/event-open-request
                       (fn [^js e] (.preventDefault e)))
    (.open el)
    (is (not (.hasAttribute el model/attr-open))
        "open should be prevented when open-request is cancelled")))

(deftest close-event-test
  (async done
    (let [el    (append! (make-el))
          fired (atom false)]
      (.setAttribute el model/attr-open "")
      (.addEventListener el model/event-close
                         (fn [_] (reset! fired true)))
      (.close el)
      (js/setTimeout
       (fn []
         (is (= true @fired) "x-command-palette-close event fired")
         (done))
       0))))

(deftest keyboard-navigation-test
  (let [el    (append! (make-el))
        items #js [#js {:id "1" :label "Alpha"}
                   #js {:id "2" :label "Beta"}
                   #js {:id "3" :label "Gamma"}]]
    (.setAttribute el model/attr-open "")
    (set! (.-items el) items)
    (let [^js input (shadow-part el "[part=input]")
          ^js list-el (shadow-part el "[part=list]")]
      ;; Simulate ArrowDown
      (.dispatchEvent input
                      (js/KeyboardEvent. "keydown"
                                         #js {:key "ArrowDown" :bubbles true}))
      (let [^js selected (.querySelector list-el "[aria-selected=true]")]
        (is (some? selected) "ArrowDown should activate an item")))))

(deftest filter-items-integration-test
  (let [el    (append! (make-el))
        items #js [#js {:id "1" :label "Open file"}
                   #js {:id "2" :label "Save document"}]]
    (set! (.-items el) items)
    (let [^js input (shadow-part el "[part=input]")]
      (set! (.-value input) "save")
      (.dispatchEvent input (js/Event. "input" #js {:bubbles true}))
      (let [^js list-el (shadow-part el "[part=list]")
            ^js rendered (.querySelectorAll list-el "[part=item]")]
        (is (= 1 (alength rendered))
            "filtering by 'save' should show only 1 item")))))

(deftest select-event-test
  (async done
    (let [el     (append! (make-el))
          fired  (atom nil)]
      (.setAttribute el model/attr-open "")
      (set! (.-items el) #js [#js {:id "x1" :label "Run tests"}])
      (.addEventListener el model/event-select
                         (fn [^js e] (reset! fired (.-detail e))))
      (let [^js list-el (shadow-part el "[part=list]")
            ^js item    (.querySelector list-el "[part=item]")]
        (when item (.click item)))
      (js/setTimeout
       (fn []
         (is (some? @fired) "x-command-palette-select event fired")
         (done))
       0))))

(deftest disabled-items-not-selectable-test
  (let [el    (append! (make-el))
        fired (atom false)]
    (.setAttribute el model/attr-open "")
    (set! (.-items el) #js [#js {:id "d1" :label "Disabled item" :disabled true}])
    (.addEventListener el model/event-select (fn [_] (reset! fired true)))
    (let [^js list-el (shadow-part el "[part=list]")
          ^js item    (.querySelector list-el "[part=item]")]
      (when item (.click item)))
    (is (= false @fired) "disabled items should not fire select")))

(deftest group-headers-test
  (let [el (append! (make-el))]
    (set! (.-items el)
          #js [#js {:id "1" :label "New file"   :group "File"}
               #js {:id "2" :label "Open file"  :group "File"}
               #js {:id "3" :label "Preferences" :group "App"}])
    (let [^js list-el (shadow-part el "[part=list]")
          ^js headers (.querySelectorAll list-el "[part=group-header]")]
      (is (= 2 (alength headers)) "two distinct groups produce two group headers")
      (is (= "File" (.-textContent (aget headers 0))) "first header is 'File'")
      (is (= "App"  (.-textContent (aget headers 1))) "second header is 'App'"))))

(deftest query-change-event-test
  (async done
    (let [el    (append! (make-el))
          fired (atom nil)]
      (.addEventListener el model/event-query-change
                         (fn [^js e] (reset! fired (.-query (.-detail e)))))
      (let [^js input (shadow-part el "[part=input]")]
        (set! (.-value input) "hello")
        (.dispatchEvent input (js/Event. "input" #js {:bubbles true})))
      (js/setTimeout
       (fn []
         (is (= "hello" @fired) "query-change event carries query string")
         (done))
       0))))
