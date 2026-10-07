(ns baredom.properties-test
  (:require [baredom.components.x-bento-grid.model :as bento-grid-model]
            [baredom.components.x-bento-grid.x-bento-grid :as x-bento-grid]
            [baredom.components.x-bento-item.model :as bento-item-model]
            [baredom.components.x-bento-item.x-bento-item :as x-bento-item]
            [baredom.components.x-card.model :as card-model]
            [baredom.components.x-card.x-card :as x-card]
            [baredom.components.x-container.model :as container-model]
            [baredom.components.x-container.x-container :as x-container]
            [baredom.components.x-grid.model :as grid-model]
            [baredom.components.x-grid.x-grid :as x-grid]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(def ^:private cases
  "One component for each row: how to register it, its tag and the properties its model declares."
  [{:init! x-bento-grid/init! :tag bento-grid-model/tag-name :api bento-grid-model/property-api}
   {:init! x-bento-item/init! :tag bento-item-model/tag-name :api bento-item-model/property-api}
   {:init! x-card/init!       :tag card-model/tag-name       :api card-model/property-api}
   {:init! x-container/init!  :tag container-model/tag-name  :api container-model/property-api}
   {:init! x-grid/init!       :tag grid-model/tag-name       :api grid-model/property-api}])

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
    {:prop (name k) :attr reflects-attribute :values (get samples type)}))

(deftest every-reflected-property-writes-its-attribute-and-reads-it-back
  (doseq [{:keys [init! tag api]} cases]
    (init!)
    (doseq [{:keys [prop attr values]} (reflected api)]
      (testing (str tag " " prop)
        (when (is (some? values) "the type of the property has sample values")
          (let [el      (element! tag)
                results (mapv #(write! el prop attr %) values)]
            (is (= values (mapv :read results)) "each value reads back")
            (is (apply not= (map :attribute results)) "each value changes the attribute")))))))
