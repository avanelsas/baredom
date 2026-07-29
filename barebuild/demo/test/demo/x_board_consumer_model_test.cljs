(ns demo.x-board-consumer-model-test
  "The board's pure layer: grouping tasks into rank-ordered columns and translating a drop into
   the full-replace move write."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-board-consumer.model :as model]))

(def ^:private rows
  [{"id" 1 "title" "A" "status" "todo"  "rank" 1 "assigneeName" "Alice" "projectName" "Web"}
   {"id" 2 "title" "B" "status" "todo"  "rank" 0 "assigneeName" "Bob"   "projectName" "Web"}
   {"id" 3 "title" "C" "status" "done"  "rank" 0 "assigneeName" "Carmen" "projectName" "Web"}])

(deftest columns-groups-by-status-ordered-by-rank
  (let [cols (model/columns {:value rows})]
    (testing "each status is a column, even when empty"
      (is (= #{"todo" "doing" "done"} (set (keys cols))))
      (is (= [] (get cols "doing"))))
    (testing "a column is ordered by rank, not input order"
      (is (= [2 1] (mapv #(get % "id") (get cols "todo"))))
      (is (= [3] (mapv #(get % "id") (get cols "done")))))))

(deftest card-vm-projects-title-and-subtitle
  (is (= {:value "1" :title "A" :subtitle "Alice · Web"}
         (model/card-vm (first rows)))
      "value is the id as a string; subtitle joins the denormalized names"))

(deftest translate-drop-gesture-is-a-full-replace-move
  (let [g (model/translate-drop-gesture (first rows) "done" 2)]
    (is (= :update (:op g)))
    (is (= 1 (:id g)))
    (testing "status and rank carry the move; denormalized names are dropped"
      (is (= "done" (get-in g [:record "status"])))
      (is (= 2 (get-in g [:record "rank"])))
      (is (not (contains? (:record g) "assigneeName")))
      (is (not (contains? (:record g) "projectName")))
      (is (= "A" (get-in g [:record "title"])) "the rest of the record is preserved"))))
