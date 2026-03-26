(ns app.components.x-stat.x-stat-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [app.components.x-stat.x-stat :as x]
   [app.components.x-stat.model :as model]))

(x/init!)

(defn cleanup! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures
 :each
 {:before cleanup!
  :after cleanup!})

(defn make-el []
  (.createElement js/document model/tag-name))

(defn append! [el]
  (.appendChild (.-body js/document) el)
  el)

(defn shadow [el sel]
  (.querySelector (.-shadowRoot el) sel))

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest base-exists-test
  (let [el (append! (make-el))
        base (shadow el "[part='base']")]
    (is (some? base))))
