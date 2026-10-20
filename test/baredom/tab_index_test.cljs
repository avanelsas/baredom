(ns baredom.tab-index-test
  (:require [baredom.components-table :as table]
            [baredom.test-elements :as elements]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(use-fixtures :each {:after elements/remove-all!})

(defn- set-attr [attr value]
  (fn [^js el] (.setAttribute el attr value)))

(defn- reconnect! [^js el]
  (.append (.-parentNode el) el))

(def ^:private keeping-changes
  "For each component that sets a tabindex, a change that renders it and keeps that tabindex."
  {"x-alert"         (set-attr "text" "a")
   "x-checkbox"      (set-attr "aria-label" "a")
   "x-chip"          (set-attr "label" "a")
   "x-radio"         reconnect!
   "x-scroll"        (set-attr "label" "a")
   "x-switch"        (set-attr "aria-label" "a")
   "x-table-cell"    (set-attr "align" "end")
   "x-table-row"     (set-attr "selected" "")
   "x-timeline-item" (set-attr "label" "a")
   "x-toast"         (set-attr "message" "a")})

(defn- observe! [^js observer ^js node]
  (.observe observer node #js {:attributes true :attributeFilter #js ["tabindex"] :subtree true}))

(defn- tab-index-writes!
  "The number of tabindex writes in `el` and its shadow tree while `f` runs."
  [^js el f]
  (let [observer (js/MutationObserver. (fn [_ _]))]
    (observe! observer el)
    (some->> (.-shadowRoot el) (observe! observer))
    (f)
    (let [n (.-length (.takeRecords observer))]
      (.disconnect observer)
      n)))

(deftest a-render-that-keeps-the-tabindex-writes-no-tabindex
  (doseq [[register! {:keys [tag-name]}] table/components
          :let  [change! (keeping-changes tag-name)]
          :when change!]
    (register!)
    (testing tag-name
      (let [^js el (elements/in-body! tag-name {})]
        (is (or (.hasAttribute el "tabindex") (some? (.querySelector (.-shadowRoot el) "[tabindex]"))))
        (is (= 0 (tab-index-writes! el (partial change! el))))))))
