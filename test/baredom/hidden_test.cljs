(ns baredom.hidden-test
  (:require [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-card.x-card :as x-card]
            [baredom.components.x-chip.x-chip :as x-chip]
            [baredom.components.x-context-menu.x-context-menu :as x-context-menu]
            [baredom.components.x-grid.x-grid :as x-grid]
            [baredom.components.x-i18n.x-i18n :as x-i18n]
            [baredom.components.x-table.x-table :as x-table]
            [baredom.components.x-timeline.x-timeline :as x-timeline]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(def ^:private cases
  "One component for each display a host can have: how to register it, its tag, the attributes it
   is given, and the display its own style gives it."
  [{:init! x-card/init!         :tag "x-card"         :attrs {}            :display "block"}
   {:init! x-button/init!       :tag "x-button"       :attrs {}            :display "inline-block"}
   {:init! x-chip/init!         :tag "x-chip"         :attrs {}            :display "inline-flex"}
   {:init! x-context-menu/init! :tag "x-context-menu" :attrs {}            :display "contents"}
   {:init! x-timeline/init!     :tag "x-timeline"     :attrs {}            :display "flex"}
   {:init! x-table/init!        :tag "x-table"        :attrs {}            :display "grid"}
   {:init! x-i18n/init!         :tag "x-i18n"         :attrs {}            :display "inline"}
   {:init! x-grid/init!         :tag "x-grid"         :attrs {"inline" ""} :display "inline-block"}])

(def ^:private mark "data-hidden-test")

(defn- remove-marked! []
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str "[" mark "]"))))

(use-fixtures :each {:after remove-marked!})

(defn- display-of!
  "The computed display of a `tag` element with `attrs`, which it puts in the document."
  [tag attrs]
  (let [el (.createElement js/document tag)]
    (run! (fn [[k v]] (.setAttribute el k v)) (assoc attrs mark ""))
    (.appendChild (.-body js/document) el)
    (.-display (js/getComputedStyle el))))

(deftest a-component-with-hidden-is-not-displayed
  (doseq [{:keys [init! tag attrs display]} cases]
    (init!)
    (testing tag
      (is (some? (.get js/customElements tag)) "the component is registered")
      (is (= display (display-of! tag attrs)) "without hidden it has the display of its style")
      (is (= "none" (display-of! tag (assoc attrs "hidden" ""))) "with hidden it is not displayed"))))
