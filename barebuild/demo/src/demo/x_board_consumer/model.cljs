(ns demo.x-board-consumer.model
  "The pure layer of the kanban board consumer: group the tasks value into status columns
   ordered by rank, project a card's view-model, and translate a drop into the full-replace
   write that moves the task. Rows carry opaque string keys.")

(def tag-name "x-board-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(def statuses ["todo" "doing" "done"])

(defn columns
  "Tasks grouped by status into the three ordered columns, each sorted by rank. Also the
   render-key: the board re-renders exactly when the grouped, ordered set changes."
  [accepted-response]
  (let [by-status (group-by #(get % "status") (:value accepted-response))]
    (into {}
          (map (fn [s]
                 [s (vec (sort-by #(or (get % "rank") 0) (get by-status s [])))])
               statuses))))

(defn card-vm
  "What a card renders: its opaque value (task id as a string), title, and a subtitle."
  [row]
  {:value    (str (get row "id"))
   :title    (get row "title")
   :subtitle (str (get row "assigneeName") " · " (get row "projectName"))})

(defn translate-drop-gesture
  "The full-replace update for moving `row` to `to-status` at `index`. The denormalized names
   are dropped — the server recomputes them; status and rank carry the move."
  [row to-status index]
  {:op     :update
   :id     (get row "id")
   :record (-> row
               (assoc "status" to-status "rank" index)
               (dissoc "assigneeName" "projectName"))})
