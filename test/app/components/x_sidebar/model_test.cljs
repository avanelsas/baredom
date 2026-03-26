(ns app.components.x-sidebar.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-sidebar.model :as model]))

(deftest normalize-placement-test
  (testing "placement normalization"
    (is (= model/placement-left
           (model/normalize-placement model/placement-left)))
    (is (= model/placement-right
           (model/normalize-placement model/placement-right)))
    (is (= model/placement-left
           (model/normalize-placement "bogus")))))

(deftest normalize-variant-test
  (testing "variant normalization"
    (is (= model/variant-docked
           (model/normalize-variant model/variant-docked)))
    (is (= model/variant-overlay
           (model/normalize-variant model/variant-overlay)))
    (is (= model/variant-modal
           (model/normalize-variant model/variant-modal)))
    (is (= model/variant-docked
           (model/normalize-variant "bogus")))))

(deftest normalize-breakpoint-test
  (testing "breakpoint normalization"
    (is (= 768 (model/normalize-breakpoint nil)))
    (is (= 900 (model/normalize-breakpoint "900")))
    (is (= 768 (model/normalize-breakpoint "not-a-number")))
    (is (= 768 (model/normalize-breakpoint "")))
    (is (= 768 (model/normalize-breakpoint "   ")))))

(deftest normalize-label-test
  (testing "label normalization"
    (is (= "Sidebar" (model/normalize-label nil)))
    (is (= "Sidebar" (model/normalize-label "   ")))
    (is (= "Main navigation" (model/normalize-label " Main navigation ")))))

(deftest compute-state-desktop-docked-test
  (testing "desktop docked state"
    (let [state (model/compute-state
                 {:open-attr true
                  :collapsed-attr false
                  :placement-attr "left"
                  :variant-attr "docked"
                  :breakpoint-attr "768"
                  :label-attr "Sidebar"
                  :viewport-width 1200
                  :prefers-reduced-motion false})]
      (is (= "left" (:placement state)))
      (is (= "docked" (:declared-variant state)))
      (is (= "docked" (:effective-variant state)))
      (is (= true (:open state)))
      (is (= false (:collapsed-applied state)))
      (is (= false (:show-backdrop state))))))

(deftest compute-state-mobile-modal-test
  (testing "small viewport forces modal"
    (let [state (model/compute-state
                 {:open-attr true
                  :collapsed-attr true
                  :placement-attr "right"
                  :variant-attr "overlay"
                  :breakpoint-attr "768"
                  :label-attr "Sidebar"
                  :viewport-width 375
                  :prefers-reduced-motion true})]
      (is (= "modal" (:effective-variant state)))
      (is (= true (:is-modal state)))
      (is (= false (:collapsed-applied state)))
      (is (= true (:show-backdrop state)))
      (is (= true (:reduced-motion state))))))

(deftest toggle-should-fire-test
  (testing "toggle event transition detection"
    (is (false? (model/toggle-should-fire? {:open true} {:open true})))
    (is (true? (model/toggle-should-fire? {:open true} {:open false})))
    (is (true? (model/toggle-should-fire? {} {:open true})))))


(deftest modal-transition-detection-test
  (testing "modal open entry and exit detection"
    (is (true? (model/entered-modal-open?
                {:is-modal false :open true}
                {:is-modal true :open true})))
    (is (true? (model/left-modal-open?
                {:is-modal true :open true}
                {:is-modal true :open false})))
    (is (false? (model/left-modal-open?
                 {:is-modal false :open false}
                 {:is-modal false :open false})))))
