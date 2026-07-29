(ns demo.x-board-consumer.model)

(def tag-name "x-board-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(def statuses ["todo" "doing" "done"])

(defn columns
  "Tasks grouped by status into the three ordered columns, each sorted by rank.
  The board re-renders exactly when the grouped, ordered set changes."
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
  "Elements that render in a card."
  [row]
  (let [assignee (str (get row "assigneeName"))]
    {:value    (str (get row "id"))
     :title    (get row "title")
     :assignee assignee
     :project  (str (get row "projectName"))
     :initial  (if (seq assignee) (subs assignee 0 1) "?")
     :hue      (name-hue assignee)}))

(defn translate-drop-gesture
  "A move is a positional command, used when a user drops a card. Not an
   edit, it carries only the destination"
  [row to-status index]
  {:op     :move
   :id     (get row "id")
   :record {"status" to-status "index" index}})
