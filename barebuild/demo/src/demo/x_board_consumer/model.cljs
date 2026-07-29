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

(defn- name-hue
  "A stable hue (0-359) derived from a name, so each assignee keeps one avatar colour."
  [name]
  (let [s (str name)]
    (mod (reduce (fn [h i] (+ (* h 31) (.charCodeAt s i))) 7 (range (count s))) 360)))

(defn card-vm
  "What a card renders: its opaque value (task id as a string), title, assignee and project,
   and a stable avatar initial + hue keyed on the assignee."
  [row]
  (let [assignee (str (get row "assigneeName"))]
    {:value    (str (get row "id"))
     :title    (get row "title")
     :assignee assignee
     :project  (str (get row "projectName"))
     :initial  (if (seq assignee) (subs assignee 0 1) "?")
     :hue      (name-hue assignee)}))

(defn translate-drop-gesture
  "The :move for dropping `row` at `to-status`/`index`. A move is a positional command, not an
   edit: it carries only the destination — the server owns rank and keeps the rest of the row."
  [row to-status index]
  {:op     :move
   :id     (get row "id")
   :record {"status" to-status "index" index}})
