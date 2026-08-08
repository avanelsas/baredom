(ns demo.x-task-form-consumer-model-test
  "The task form's pure layer: the row an edit names, and the status choices."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-task-form-consumer.model :as model]))

(def ^:private accepted
  {:value [{"id" 1 "title" "A"} {"id" 2 "title" "B"}]
   :shape {:id-key "id"
           :fields [{:key "title" :type :string}
                    {:key "status" :type :string
                     :enum ["todo" "doing"]
                     :options [{:value "todo" :label "To do"} {:value "doing" :label "Doing"}]}]}})

(deftest row-by-id-matches-a-dom-string-against-a-server-number
  (testing "the id comes off a data attribute as a string, the server sends it as a number"
    (is (= "B" (get (model/row-by-id accepted "2") "title"))))
  (testing "an id no row carries finds nothing"
    (is (nil? (model/row-by-id accepted "9"))))
  (testing "no answer yet, no row"
    (is (nil? (model/row-by-id nil "1")))))

(deftest unshown-fields-carry-the-references-the-form-never-offers
  (let [row {"id" 1 "title" "A" "projectId" "p-1" "assigneeId" "u-2" "status" "todo"}]
    (testing "an update is a full replace, so the references travel with it"
      (is (= {"projectId" "p-1" "assigneeId" "u-2"} (model/unshown-fields row))))
    (testing "nothing the form does collect is carried twice"
      (is (empty? (select-keys (model/unshown-fields row) ["title" "status" "id"]))))
    (testing "a row without them yields nothing to carry"
      (is (= {} (model/unshown-fields {"id" 1 "title" "A"}))))))

(deftest status-choices-prefers-the-labels-the-shape-declares
  (testing "options carry the value to submit and the label to show"
    (is (= [{:value "todo" :label "To do"} {:value "doing" :label "Doing"}]
           (model/status-choices accepted)))))

(deftest status-choices-falls-back-to-the-bare-enum
  (testing "no labels declared, so each value is its own label"
    (let [bare (assoc-in accepted [:shape :fields]
                         [{:key "status" :enum ["todo" "done"]}])]
      (is (= [{:value "todo" :label "todo"} {:value "done" :label "done"}]
             (model/status-choices bare)))))
  (testing "no status field offers nothing rather than throwing"
    (is (empty? (model/status-choices (assoc-in accepted [:shape :fields]
                                                [{:key "title" :type :string}]))))
    (is (empty? (model/status-choices nil)))))
