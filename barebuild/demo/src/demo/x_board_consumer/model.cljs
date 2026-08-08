(ns demo.x-board-consumer.model)

(def tag-name "x-board-consumer")

(def statuses ["todo" "doing" "done"])

(defn columns
  "Tasks grouped by status into the three columns, each ordered by rank."
  [accepted-response]
  (let [by-status (group-by #(get % "status") (:value accepted-response))]
    (into {}
          (map (fn [s] [s (vec (sort-by #(or (get % "rank") 0) (get by-status s)))]))
          statuses)))

(def empty-columns
  "The three columns, empty."
  (zipmap statuses (repeat [])))

(defn project-selected?
  "True when the intent names a project."
  [intent]
  (some? (:project intent)))

(defn columns-for
  "The columns `view` paints, empty until a project is selected."
  [{:keys [accepted intent]}]
  (if (and (project-selected? intent) accepted)
    (columns accepted)
    empty-columns))

(defn render-key
  "The slice the board repaints on: the rows, and whether a project is selected."
  [{:keys [accepted intent]}]
  [(:value accepted) (project-selected? intent)])

(defn rows-by-id
  "The rows of `cols`, keyed by the string id `board-plan` uses."
  [cols]
  (into {} (map (fn [row] [(str (get row "id")) row])) (mapcat val cols)))

(defn board-plan
  "The DOM plan: `:order` is each status's card ids in order, `:remove` the ids no column claims.
   Removal is decided board-wide, so a card that only changed column is moved, not rebuilt."
  [present-ids cols]
  (let [order (into {}
                    (map (fn [[status rows]]
                           [status (mapv (fn [row] (str (get row "id"))) rows)]))
                    cols)
        kept  (into #{} cat (vals order))]
    {:order  order
     :remove (vec (remove kept present-ids))}))

(defn- name-hue
  "A stable hue (0-359) per name, so an assignee keeps one avatar colour. The modulo folds into
   every step rather than landing at the end, where a long name would already have run past the
   precision a double carries."
  [name]
  (reduce (fn [h ch] (mod (+ (* h 31) (.charCodeAt ch 0)) 360)) 7 (str name)))

(defn card-vm
  "What a card renders."
  [row]
  (let [assignee (str (or (get row "assigneeName") (get row "owner")))]
    {:value    (str (get row "id"))
     :title    (get row "title")
     :assignee assignee
     :project  (str (get row "projectName"))
     :initial  (if (seq assignee) (subs assignee 0 1) "?")
     :hue      (name-hue assignee)}))

(defn translate-drop-gesture
  "A drop as a move: a positional command carrying only the destination, not a record edit."
  [row to-status index]
  {:op     :move
   :id     (get row "id")
   :record {"status" to-status "index" index}})
