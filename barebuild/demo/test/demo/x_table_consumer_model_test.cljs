(ns demo.x-table-consumer-model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-table-consumer.model :as model]))

(def accepted
  {:outcome    :accepted
   :request/id "req-1"
   :revision   "tasks:v1"
   :query      {:sort "owner" :direction "asc"}
   :value      [{"id" 1 "title" "Refactor parser core" "owner" "Alice" "start" "2026-01-05" "status" "doing" "secret" "x"}
                {"id" 2 "title" "Trim bundle size"     "owner" "Bob"   "start" nil          "status" "todo"  "secret" "y"}]
   :shape      {:id-key "id"
                :fields [{:key "title"  :type :string}
                         {:key "owner"  :type :string}
                         {:key "start"  :type :date}
                         {:key "status" :type :string}]}})

(deftest columns-from-shape-in-declared-order
  (let [{:keys [columns]} (model/accepted-response->view-model accepted)]
    (is (= [{:key "title"  :label "title"  :type :string  :sort-direction "none"}
            {:key "owner"  :label "owner"  :type :string  :sort-direction "asc"}
            {:key "start"  :label "start"  :type :date    :sort-direction "none"}
            {:key "status" :label "status" :type :string  :sort-direction "none"}]
           columns)
        "columns from shape.fields in order; the header label is the field key; the sorted
         column carries the echoed direction, others \"none\"")))

(deftest rows-lift-id-and-project-declared-cells
  (let [{:keys [rows]} (model/accepted-response->view-model accepted)]
    (testing "id is lifted from each row via shape :id-key"
      (is (= [1 2] (mapv :id rows))))
    (testing "cells contain only declared fields, as a map of raw values"
      (is (= {"title" "Refactor parser core" "owner" "Alice" "start" "2026-01-05" "status" "doing"}
             (:cells (first rows))))
      (is (not (contains? (:cells (first rows)) "secret")) "undeclared key dropped (§5.2)")
      (is (not (contains? (:cells (first rows)) "id")) "id is lifted, not a cell"))
    (testing "explicit nil cell is preserved (key present, value nil)"
      (is (contains? (:cells (second rows)) "start"))
      (is (nil? (get-in (second rows) [:cells "start"]))))))

(deftest empty-shape-and-value
  (testing "no fields / no rows -> empty columns and rows, no crash"
    (is (= {:columns [] :rows []}
           (model/accepted-response->view-model
            {:shape {:id-key "id" :fields []} :value []})))))

(deftest sort-direction-annotation
  (testing "the sorted column gets the echoed direction, all others get \"none\""
    (let [{:keys [columns]} (model/accepted-response->view-model
                             (assoc accepted :query {:sort "start" :direction "desc"}))]
      (is (= ["none" "none" "desc" "none"] (mapv :sort-direction columns)))))
  (testing "no sort in the query -> every column \"none\""
    (let [{:keys [columns]} (model/accepted-response->view-model
                             (assoc accepted :query {}))]
      (is (every? #(= "none" %) (map :sort-direction columns))))))

(deftest translate-gesture-builds-intent-patch
  (is (= {:query-patch {:sort "owner" :direction "asc"} :gesture-class :refinement}
         (model/translate-gesture "owner" "asc"))
      "sort gesture -> a query-patch classified :refinement (replaces history)"))

(deftest translate-gesture-none-clears-sort
  (is (= {:query-patch {:sort nil :direction nil} :gesture-class :refinement}
         (model/translate-gesture "owner" "none"))
      "a \"none\" direction nils the sort keys so the canonicalized intent carries no sort"))

(deftest translate-pagination-gesture-builds-navigation-patch
  (is (= {:query-patch {:page "3"} :gesture-class :navigation}
         (model/translate-pagination-gesture "3"))
      "page gesture -> a :page query-patch classified :navigation (pushes history)"))

(deftest reconcile-plan-reorders-removes-and-flags-new
  (let [plan (model/reconcile-plan ["1" "2" "3"]
                                   [{:id 3 :cells {}} {:id 1 :cells {}} {:id 4 :cells {}}])]
    (is (= ["2"] (:remove plan)) "an id no longer present is removed")
    (is (= [3 1 4] (map :id (:order plan))) "order follows the desired rows")
    (is (= [false false true] (map :new? (:order plan))) "only the unseen id is flagged new")))

(deftest reconcile-plan-matches-numeric-ids-to-string-dom-ids
  (let [plan (model/reconcile-plan ["1"] [{:id 1 :cells {}}])]
    (is (empty? (:remove plan)) "numeric row id matches the string data-row-id")
    (is (= [false] (map :new? (:order plan))) "so the row counts as existing, not new")))

(deftest reconcile-plan-handles-empty-sides
  (is (= {:remove [] :order []} (model/reconcile-plan [] [])))
  (is (= [true] (map :new? (:order (model/reconcile-plan [] [{:id 1 :cells {}}]))))
      "into an empty table every row is new")
  (is (= ["1" "2"] (:remove (model/reconcile-plan ["1" "2"] [])))
      "clearing the rows removes them all"))
