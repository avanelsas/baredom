(ns baredom.early-properties-test
  (:require [baredom.components-table :as table]
            [baredom.components.x-command-palette.x-command-palette :as x-command-palette]
            [baredom.test-elements :as elements]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(use-fixtures :each {:after elements/remove-all!})

(def ^:private samples
  "A value for each type of property."
  {'boolean true
   'string  "early"
   'number  3})

(defn- inert-document
  "A document where no element is defined, as the one that `nodeMaker` makes its nodes in."
  []
  (.-ownerDocument (.-content (.createElement js/document "template"))))

(defn- writable
  "The name and a sample value of each property of `properties` that a page may write."
  [properties]
  (for [[k {:keys [type readonly read-only]}] properties
        :when (and (not (or readonly read-only)) (contains? samples type))]
    [(name k) (samples type)]))

(deftest a-property-set-before-the-upgrade-reads-as-one-set-after-it
  (doseq [[register!] table/components]
    (register!))
  (doseq [[_ {:keys [tag-name properties]}] table/components
          [prop value] (writable properties)]
    (testing (str tag-name " " prop)
      (let [late  (doto (elements/in-body! tag-name {}) (unchecked-set prop value))
            early (doto (.createElement (inert-document) tag-name)
                    (unchecked-set prop value)
                    (elements/node-in-body!))]
        (is (= (js->clj (unchecked-get late prop)) (js->clj (unchecked-get early prop))))
        (is (not (js/Object.hasOwn early prop)))))))

(deftest a-command-palette-shows-items-set-before-the-upgrade
  (let [^js el (.createElement (inert-document) "x-command-palette")]
    (x-command-palette/init!)
    (set! (.-items el) #js [#js {:id "a" :label "Alpha"}])
    (elements/node-in-body! el)
    (is (= 1 (.-length (.-items el))))
    (is (re-find #"Alpha" (.. el -shadowRoot -textContent)))))
