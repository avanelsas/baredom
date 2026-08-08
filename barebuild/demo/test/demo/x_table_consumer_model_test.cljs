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
    (is (= [{:key "title"  :sort-direction "none"}
            {:key "owner"  :sort-direction "asc"}
            {:key "start"  :sort-direction "none"}
            {:key "status" :sort-direction "none"}]
           columns)
        "columns from shape.fields in order, the sorted one carrying the echoed direction")))

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

(deftest cells-show-the-label-a-field-declares-for-its-value
  (let [labelled (assoc-in accepted [:shape :fields 3 :options]
                           [{:value "todo"  :label "To do"}
                            {:value "doing" :label "In progress"}])
        {:keys [rows]} (model/accepted-response->view-model labelled)]
    (testing "a cell reads the same as the form control that offers the value"
      (is (= "In progress" (get-in (first rows) [:cells "status"])))
      (is (= "To do" (get-in (second rows) [:cells "status"]))))
    (testing "fields without options are untouched"
      (is (= "Refactor parser core" (get-in (first rows) [:cells "title"]))))))

(deftest a-value-missing-from-the-options-shows-as-itself
  (let [labelled (assoc-in accepted [:shape :fields 3 :options]
                           [{:value "todo" :label "To do"}])
        {:keys [rows]} (model/accepted-response->view-model labelled)]
    (testing "an unlisted value shows raw rather than blank"
      (is (= "doing" (get-in (first rows) [:cells "status"]))))))

(deftest failure-message-shows-the-server-its-own-words-and-speaks-for-every-other-cause
  (testing "the server's own message is passed through"
    (is (= "That project is closed."
           (model/failure-message {:cause    :rejected
                                   :for      :read
                                   :response {:error {:message "That project is closed."}}}))))
  (testing "the client's own readings speak for themselves"
    (is (= "The server sent an unexpected response."
           (model/failure-message {:cause :protocol :detail {:reason :empty-body}})))
    (is (= "The server's data didn't match the expected format."
           (model/failure-message {:cause :contract :errors [{:code :missing-id-key}]}))))
  (testing "an unknown cause still says something"
    (is (= "Something went wrong." (model/failure-message {})))))

(deftest failure-message-names-the-status-a-server-that-answered-sent
  (testing "auth statuses are told apart"
    (is (= "Your session has expired. Please sign in again."
           (model/failure-message {:cause :network :error {:kind :http-status :status 401}})))
    (is (= "Your session has expired. Please sign in again."
           (model/failure-message {:cause :network :error {:kind :http-status :status 403}})))
    (is (= "That resource was not found."
           (model/failure-message {:cause :network :error {:kind :http-status :status 404}}))))
  (testing "any other status is still the server answering"
    (is (= "The server returned an error. Please try again."
           (model/failure-message {:cause :network :error {:kind :http-status :status 500}}))))
  (testing "no answer at all reads as unreachable"
    (is (= "Couldn't reach the server. Please try again."
           (model/failure-message {:cause :network :error {:kind :offline}})))
    (is (= "Couldn't reach the server. Please try again."
           (model/failure-message {:cause :network :error {:kind :timeout :after 60000}})))))

(deftest grid-template-keeps-the-actions-column-off-the-even-split
  (testing "the fields split the width, the actions column takes what its buttons need"
    (is (= "repeat(6,minmax(0,1fr)) max-content"
           (model/grid-template [{:key "title"} {:key "owner"} {:key "start"}
                                 {:key "end"} {:key "est"} {:key "status"}])))
    (is (= "repeat(1,minmax(0,1fr)) max-content" (model/grid-template [{:key "title"}]))))
  (testing "no fields still leaves the actions column a track, not a repeat(0,...) the browser
            discards"
    (is (= "max-content" (model/grid-template [])))
    (is (= "max-content" (model/grid-template nil)))))
