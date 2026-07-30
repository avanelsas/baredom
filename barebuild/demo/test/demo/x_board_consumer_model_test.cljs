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

(deftest card-vm-projects-card-fields
  (let [vm (model/card-vm (first rows))]
    (is (= "1" (:value vm)) "value is the id as a string")
    (is (= "A" (:title vm)))
    (is (= "Alice" (:assignee vm)))
    (is (= "Web" (:project vm)))
    (is (= "A" (:initial vm)) "the avatar initial is the assignee's first letter")))

(deftest card-vm-falls-back-to-owner-when-no-user
  (is (= "Wendy"
         (:assignee (model/card-vm {"id" 9 "title" "T" "owner" "Wendy" "projectName" "Web"})))
      "a quick-added card with no resolved user shows its owner")
  (is (= "Alice"
         (:assignee (model/card-vm {"id" 9 "title" "T" "assigneeName" "Alice" "owner" "Wendy"})))
      "a resolved assignee name wins over the owner"))

(deftest card-vm-avatar-hue-is-stable-and-bounded
  (let [h1 (:hue (model/card-vm {"assigneeName" "Alice"}))
        h2 (:hue (model/card-vm {"assigneeName" "Alice"}))]
    (is (= h1 h2) "same assignee always gets the same hue")
    (is (<= 0 h1 359) "hue is a valid degree"))
  (is (= "?" (:initial (model/card-vm {}))) "a missing assignee falls back to a placeholder"))

(deftest translate-drop-gesture-is-a-move-command
  (let [g (model/translate-drop-gesture (first rows) "done" 2)]
    (is (= :move (:op g)) "a drop is a positional move, not a record edit")
    (is (= 1 (:id g)))
    (is (= {"status" "done" "index" 2} (:record g))
        "the move carries only the destination — no record fields, no server-owned rank")))
