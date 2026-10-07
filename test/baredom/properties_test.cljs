(ns baredom.properties-test
  (:require [baredom.components.x-bento-grid.model :as bento-grid-model]
            [baredom.components.x-bento-grid.x-bento-grid :as x-bento-grid]
            [baredom.components.x-bento-item.model :as bento-item-model]
            [baredom.components.x-bento-item.x-bento-item :as x-bento-item]
            [baredom.components.x-button.model :as button-model]
            [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-card.model :as card-model]
            [baredom.components.x-card.x-card :as x-card]
            [baredom.components.x-container.model :as container-model]
            [baredom.components.x-container.x-container :as x-container]
            [baredom.components.x-grid.model :as grid-model]
            [baredom.components.x-grid.x-grid :as x-grid]
            [baredom.components.x-particle-button.model :as particle-button-model]
            [baredom.components.x-particle-button.x-particle-button :as x-particle-button]
            [baredom.components.x-sidebar.model :as sidebar-model]
            [baredom.components.x-sidebar.x-sidebar :as x-sidebar]
            [baredom.components.x-tab.model :as tab-model]
            [baredom.components.x-tab.x-tab :as x-tab]
            [baredom.components.x-tabs.model :as tabs-model]
            [baredom.components.x-tabs.x-tabs :as x-tabs]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(def ^:private cases
  "One component for each row: how to register it, its tag and the properties its model declares."
  [[x-bento-grid/init!      bento-grid-model/tag-name      bento-grid-model/property-api]
   [x-bento-item/init!      bento-item-model/tag-name      bento-item-model/property-api]
   [x-button/init!          button-model/tag-name          button-model/property-api]
   [x-card/init!            card-model/tag-name            card-model/property-api]
   [x-container/init!       container-model/tag-name       container-model/property-api]
   [x-grid/init!            grid-model/tag-name            grid-model/property-api]
   [x-particle-button/init! particle-button-model/tag-name particle-button-model/property-api]
   [x-sidebar/init!         sidebar-model/tag-name         sidebar-model/property-api]
   [x-tab/init!             tab-model/tag-name             tab-model/property-api]
   [x-tabs/init!            tabs-model/tag-name            tabs-model/property-api]])

(def ^:private samples
  "For each type of property, two values that differ."
  {'boolean [true false]
   'string  ["first" "second"]
   'number  [3 5]})

(def ^:private mark "data-properties-test")

(defn- remove-marked! []
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str "[" mark "]"))))

(use-fixtures :each {:after remove-marked!})

(defn- element!
  "A `tag` element in the document."
  [tag]
  (let [el (.createElement js/document tag)]
    (.setAttribute el mark "")
    (.appendChild (.-body js/document) el)))

(defn- write!
  "Writes `value` to the property `prop` of `el`. Returns what the property reads back and what
   its attribute `attr` holds."
  [^js el prop attr value]
  (unchecked-set el prop value)
  {:read (unchecked-get el prop) :attribute (.getAttribute el attr)})

(defn- reflected
  "The declared properties of a component that reflect an attribute and can be written."
  [api]
  (for [[k {:keys [type reflects-attribute readonly]}] api
        :when (and reflects-attribute (not readonly))]
    {:prop (name k) :attr reflects-attribute :type type :values (get samples type)}))

(deftest every-reflected-property-writes-its-attribute-and-reads-it-back
  (doseq [[init! tag api] cases]
    (init!)
    (doseq [{:keys [prop attr type values]} (reflected api)]
      (testing (str tag " " prop)
        (when (is (some? values) "the type of the property has sample values")
          (let [el      (element! tag)
                results (mapv #(write! el prop attr %) values)]
            (is (= values (mapv :read results)) "each value reads back")
            (is (apply not= (map :attribute results)) "each value changes the attribute")
            (when (= 'number type)
              (is (= "7" (:attribute (write! el prop attr "7")))
                  "numeric text reaches the attribute"))))))))
