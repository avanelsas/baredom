(ns read-demo.x-table-consumer-model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [read-demo.x-table-consumer.model :as model]))

(def accepted
  {:outcome    :accepted
   :request/id "req-1"
   :revision   "tasks:v1"
   :query      {:sort "owner" :direction "asc"}
   :value      [{"id" 1 "owner" "Alice" "start" "2026-01-05" "status" "doing" "secret" "x"}
                {"id" 2 "owner" "Bob"   "start" nil          "status" "todo"  "secret" "y"}]
   :shape      {:id-key "id"
                :fields [{:key "owner"  :type :string}
                         {:key "start"  :type :date}
                         {:key "status" :type :string}]}})

(deftest columns-from-shape-in-declared-order
  (let [{:keys [columns]} (model/accepted-response->view-model accepted)]
    (is (= [{:key "owner"  :label "owner"  :type :string  :sort-direction "asc"}
            {:key "start"  :label "start"  :type :date    :sort-direction "none"}
            {:key "status" :label "status" :type :string  :sort-direction "none"}]
           columns)
        "columns from shape.fields in order; label falls back to key; the sorted column
         carries the echoed direction, others \"none\"")))

(deftest rows-lift-id-and-project-declared-cells
  (let [{:keys [rows]} (model/accepted-response->view-model accepted)]
    (testing "id is lifted from each row via shape :id-key"
      (is (= [1 2] (mapv :id rows))))
    (testing "cells contain only declared fields, as a map of raw values"
      (is (= {"owner" "Alice" "start" "2026-01-05" "status" "doing"}
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
      (is (= ["none" "desc" "none"] (mapv :sort-direction columns)))))
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
