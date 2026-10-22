(ns first-look.main
  "A list of tasks with a checkbox each, in plain HTML."
  (:require [baremirror.alpha.events :as events]
            [baremirror.alpha.parts :as parts]
            [baremirror.alpha.places :as places]
            [baremirror.alpha.template :as template]))

;; 1. The state, and what a message does to it.

(def initial
  {:tasks [{:id "1" :text "Read the guide" :done? false}
           {:id "2" :text "Try BareMirror" :done? true}]})

(defn- toggled [id task]
  (cond-> task (= id (:id task)) (update :done? not)))

(defn- toggle [tasks id]
  (mapv (partial toggled id) tasks))

(defn step [state [meaning id]]
  (case meaning
    :toggle (update state :tasks toggle id)
    state))

;; 2. The template of a row, and what the page shows for a state.

;; A checkbox stops following its checked attribute once clicked, so checked is a property.
(def row
  (template/split :row [:li
                        [:input {:type "checkbox" :props {:checked :done?} :on {"change" :toggle}}]
                        [:span :text]]))

(defn view [{:keys [tasks]}]
  {:order (mapv :id tasks)
   :rows  (zipmap (map :id tasks) tasks)})

;; 3. The one effect.

(def copy-row!
  (parts/node-maker (:fixed row)))

(defn- write-row! [nodes rows k]
  (parts/write-item! (nodes k) row (rows k)))

(defn render! [^js ul {:keys [order rows]}]
  (let [nodes (places/sync! {:tasks {:parent ul}} {:tasks order} copy-row!)]
    (run! (partial write-row! nodes rows) order)))

;; The wiring.

(defn start! []
  (let [ul    (.getElementById js/document "tasks")
        state (atom initial)]
    (events/listen! ul {:dispatch! (events/dispatcher state step view (partial render! ul))
                        :events    (:events row)})
    (render! ul (view @state))))
