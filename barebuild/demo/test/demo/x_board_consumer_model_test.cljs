(ns demo.x-board-consumer-model-test
  "The board's pure layer: the columns, the repaint key, and the move a drop becomes."
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

(def ^:private accepted {:value rows})

(deftest columns-for-paints-nothing-until-a-project-is-picked
  (testing "rows are not shown before a project names them"
    (is (= model/empty-columns (model/columns-for {:accepted accepted :intent {}})))
    (is (= #{"todo" "doing" "done"} (set (keys model/empty-columns))))
    (is (every? empty? (vals model/empty-columns))))
  (testing "a selected project paints the grouped columns"
    (is (= (model/columns accepted)
           (model/columns-for {:accepted accepted :intent {:project "7"}}))))
  (testing "a selection before the first response has nothing to paint"
    (is (= model/empty-columns (model/columns-for {:accepted nil :intent {:project "7"}})))))

(deftest render-key-moves-with-the-rows-and-with-the-selection
  (let [selected {:accepted accepted :intent {:project "7"}}]
    (testing "same rows, same selection, same key"
      (is (= (model/render-key selected) (model/render-key selected))))
    (testing "clearing the selection repaints, the rows unmoved"
      (is (not= (model/render-key selected)
                (model/render-key (assoc selected :intent {})))))
    (testing "new rows under the same selection repaint"
      (is (not= (model/render-key selected)
                (model/render-key (assoc selected :accepted {:value []})))))))

(deftest rows-by-id-keys-rows-the-way-board-plan-names-them
  (let [cols (model/columns accepted)]
    (testing "a string id finds its row, the server's id being a number"
      (is (= "A" (get-in (model/rows-by-id cols) ["1" "title"]))))
    (testing "every card the plan orders can be looked up"
      (is (= (set (mapcat val (:order (model/board-plan [] cols))))
             (set (keys (model/rows-by-id cols))))))))

(deftest board-plan-orders-each-column-and-drops-only-what-left-the-board
  (let [cols (model/columns {:value rows})]
    (testing "cards as the string ids the DOM carries, in column order"
      (is (= {"todo" ["2" "1"] "doing" [] "done" ["3"]} (:order (model/board-plan [] cols)))))
    (testing "a card no column claims is dropped"
      (is (= ["9"] (:remove (model/board-plan ["1" "2" "3" "9"] cols)))))
    (testing "a card that only changed column is moved, not rebuilt, a rebuilt card having
              nowhere to animate from"
      (let [moved (model/columns {:value (mapv #(if (= 1 (get % "id"))
                                                  (assoc % "status" "done")
                                                  %)
                                               rows)})]
        (is (= ["3" "1"] (get-in (model/board-plan ["1" "2" "3"] moved) [:order "done"]))
            "the card is claimed by its new column, in rank order")
        (is (= [] (:remove (model/board-plan ["1" "2" "3"] moved)))
            "and so nothing is removed")))
    (testing "an empty board drops everything"
      (is (= ["1" "2"] (:remove (model/board-plan ["1" "2"] {})))))))

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
      "no resolved user, so the owner shows")
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
        "only the destination, no record fields and no rank")))
