(ns demo.x-board-consumer.x-board-consumer
  "The kanban board consumer driving three x-drop-zones"
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-board-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-pending-zone "__xBoardPendingZone")

;; --- card building ------------------------------------------------------------

(defn- ensure-child! [^js panel selector tag decorate!]
  (or (.querySelector panel selector)
      (let [child (.createElement js/document tag)]
        (decorate! child)
        (.appendChild panel child)
        child)))

(defn- set-card-content! [^js panel row]
  (let [{:keys [title assignee project initial hue]} (model/card-vm row)
        head   (ensure-child! panel "[slot=header]" "span" #(set! (.-slot %) "header"))
        body   (ensure-child! panel ".board-card-body" "div" #(set! (.-className %) "board-card-body"))
        avatar (ensure-child! body ".board-card-avatar" "span" #(set! (.-className %) "board-card-avatar"))
        meta   (ensure-child! body ".board-card-meta" "span" #(set! (.-className %) "board-card-meta"))]
    (du/set-attr! panel "label" title)
    (set! (.-textContent head) title)
    (set! (.-textContent avatar) initial)
    (.setProperty (.-style avatar) "background-color" (str "hsl(" hue " 55% 48%)"))
    (set! (.-textContent meta) (str assignee " · " project))))

(defn- make-card! [row]
  (let [panel (.createElement js/document "x-drag-panel")]
    (du/set-attr! panel "kind" "task")
    (du/set-attr! panel "value" (str (get row "id")))
    (du/set-attr! panel "grab" "surface")
    (set-card-content! panel row)
    panel))

;; --- render: apply the board plan to the zones --------------------------------

(defn- zones [^js this]
  (array-seq (.querySelectorAll this "x-drop-zone")))

(defn- panel-pool
  "Every card the board is showing now, by the id it carries."
  [^js this]
  (into {} (map (fn [^js p] [(du/get-attr p "value") p])
                (array-seq (.querySelectorAll this "x-drag-panel")))))

(defn- panel? [^js node]
  (and (some? node)
       (= "x-drag-panel" (.. node -tagName toLowerCase))))

(defn- panel-from
  "The first card at or after `node`, skipping whatever else a zone holds. Nil when there is none."
  [^js node]
  (loop [^js n node]
    (cond
      (nil? n)   nil
      (panel? n) n
      :else      (recur (.-nextElementSibling n)))))

(defn- first-panel [^js zone] (panel-from (.-firstElementChild zone)))
(defn- next-panel [^js node] (panel-from (.-nextElementSibling node)))

(defn- rows-by-id
  "The rows the plan's ids name, by the same string id the plan uses."
  [cols]
  (into {} (map (fn [row] [(str (get row "id")) row])) (mapcat val cols)))

(defn- card-for!
  "The card showing `id`, reused from the pool or built, and refreshed from its row either way."
  [pool rows id]
  (let [row (get rows id)]
    (doto ^js (or (get pool id) (make-card! row))
      (set-card-content! row))))

(defn- place-in-order!
  "Insert `panels` into `zone` in the order given, skipping any already sitting where it belongs.
  Only a card that actually moved is touched, so the zone animates the moves that happened rather
  than every card on every render."
  [^js zone panels]
  (loop [remaining  panels
         ^js anchor (first-panel zone)]
    (when-let [^js panel (first remaining)]
      (if (identical? panel anchor)
        (recur (rest remaining) (next-panel anchor))
        (do (.insertBefore zone panel anchor)
            (recur (rest remaining) anchor))))))

(defn- apply-board-plan!
  "Perform `plan`: drop the cards no column claims, then order each zone. Dropping first means the
  ordering pass only ever sees cards that belong somewhere."
  [^js this {:keys [order remove]} pool rows]
  (doseq [id remove]
    (when-let [^js panel (get pool id)]
      (.remove panel)))
  (doseq [^js zone (zones this)]
    (place-in-order! zone
                     (mapv (fn [id] (card-for! pool rows id))
                           (get order (du/get-attr zone "value") [])))))

(defn- place-cards! [^js this cols]
  (let [pool (panel-pool this)]
    (apply-board-plan! this
                       (model/board-plan (keys pool) cols)
                       pool
                       (rows-by-id cols))))

(defn- project-selected?
  "Read from the projected intent."
  [intent]
  (some? (:project intent)))

(def ^:private empty-columns
  (into {} (map (fn [s] [s []]) model/statuses)))

(defn- columns-for
  "The three ordered columns `view` paints, empty until a project is selected."
  [{:keys [accepted intent]}]
  (if (and (project-selected? intent) accepted)
    (model/columns accepted)
    empty-columns))

;; --- drop: reserve -> write -> release ----------------------------------------

(defn- zone-for [^js this status]
  (some (fn [^js z] (when (= status (du/get-attr z "value")) z)) (zones this)))

(defn- reserved-zone
  "The zone holding a gap open for a dropped card the server has not confirmed, or nil."
  [^js this]
  (du/getv this k-pending-zone))

(defn- on-drop! [^js this ^js e]
  (let [^js detail (.-detail e)
        view       (consumer-resource/view this)
        row        (get (rows-by-id (columns-for view)) (.-value detail))
        ^js zone   (zone-for this (.-to detail))]
    (when (and row zone (not (:writing? view)) (not (reserved-zone this)))
      (.reserve zone (.-panel detail) (.-index detail))
      (du/setv! this k-pending-zone zone)
      (consumer-resource/submit-write!
       this (model/translate-drop-gesture row (.-to detail) (.-index detail))))))

(defn- on-writing!
  "Release the zone this board reserved once its own drop has settled. Another consumer's write
  moves `:writing?` too, and releasing on that would drop the card back early."
  [_child {:keys [writing?] :as view} ^js this]
  (when-let [^js zone (and (not writing?)
                           (consumer-resource/own-write? this view)
                           (reserved-zone this))]
    (.release zone)
    (du/setv! this k-pending-zone nil)
    ;; the ack's render ran while the drop was still reserved, so it deferred placing to here
    (place-cards! this (columns-for view))))

(defn- on-connect! [_child ^js el]
  (.addEventListener el "x-drop-zone-drop" (fn [e] (on-drop! el e))))

(defn- render!
  "The board paints from the accepted rows and from whether a project is selected, so its
  render-key names both. Either one changing is a repaint."
  [_child {:keys [intent] :as view} ^js this]
  (du/set-attr! this "data-empty" (if (project-selected? intent) "false" "true"))
  (when-not (reserved-zone this)
    (place-cards! this (columns-for view))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-drop-zone"
    :render-key (fn [{:keys [accepted intent]}]
                  [(:value accepted) (project-selected? intent)])
    :render     render!
    :on-writing on-writing!
    :on-connect on-connect!}))
