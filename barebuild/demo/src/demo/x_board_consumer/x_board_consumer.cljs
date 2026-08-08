(ns demo.x-board-consumer.x-board-consumer
  "The kanban board consumer, driving three x-drop-zones."
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
        head     (ensure-child! panel "[slot=header]" "span" #(set! (.-slot %) "header"))
        body     (ensure-child! panel ".board-card-body" "div" #(set! (.-className %) "board-card-body"))
        avatar   (ensure-child! body ".board-card-avatar" "span" #(set! (.-className %) "board-card-avatar"))
        subtitle (ensure-child! body ".board-card-meta" "span" #(set! (.-className %) "board-card-meta"))]
    (du/set-attr! panel "label" title)
    (set! (.-textContent head) title)
    (set! (.-textContent avatar) initial)
    (.setProperty (.-style avatar) "background-color" (str "hsl(" hue " 55% 48%)"))
    (set! (.-textContent subtitle) (str assignee " · " project))))

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
  "The cards on the board, by id."
  [^js this]
  (into {} (map (fn [^js p] [(du/get-attr p "value") p])
                (array-seq (.querySelectorAll this "x-drag-panel")))))

(defn- panel? [^js node]
  (and (some? node)
       (= "x-drag-panel" (.. node -tagName toLowerCase))))

(defn- panel-from
  "The first card at or after `node`, skipping whatever else a zone holds, or nil."
  [^js node]
  (loop [^js n node]
    (cond
      (nil? n)   nil
      (panel? n) n
      :else      (recur (.-nextElementSibling n)))))

(defn- first-panel [^js zone] (panel-from (.-firstElementChild zone)))
(defn- next-panel [^js node] (panel-from (.-nextElementSibling node)))

(defn- card-for!
  "The card for `id`, reused from the pool or built, refreshed from its row either way."
  [pool rows id]
  (let [row (get rows id)]
    (doto ^js (or (get pool id) (make-card! row))
      (set-card-content! row))))

(defn- place-in-order!
  "Insert `panels` into `zone` in order, skipping any already in place, so the zone animates only
  the cards that moved."
  [^js zone panels]
  (loop [remaining  panels
         ^js anchor (first-panel zone)]
    (when-let [^js panel (first remaining)]
      (if (identical? panel anchor)
        (recur (rest remaining) (next-panel anchor))
        (do (.insertBefore zone panel anchor)
            (recur (rest remaining) anchor))))))

(defn- apply-board-plan!
  "Perform `plan`: drop what no column claims, then order each zone."
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
                       (model/rows-by-id cols))))

;; --- drop: reserve -> write -> release ----------------------------------------

(defn- zone-for [^js this status]
  (some (fn [^js z] (when (= status (du/get-attr z "value")) z)) (zones this)))

(defn- reserved-zone
  "The zone holding a gap open for an unconfirmed drop, or nil."
  [^js this]
  (du/getv this k-pending-zone))

(defn- on-drop! [^js this ^js e]
  (let [^js detail (.-detail e)
        view       (consumer-resource/view this)
        row        (get (model/rows-by-id (model/columns-for view)) (.-value detail))
        ^js zone   (zone-for this (.-to detail))]
    (when (and row zone (not (:writing? view)) (not (reserved-zone this)))
      (.reserve zone (.-panel detail) (.-index detail))
      (du/setv! this k-pending-zone zone)
      (consumer-resource/submit-write!
       this (model/translate-drop-gesture row (.-to detail) (.-index detail))))))

(defn- on-writing!
  "Release the reserved zone once this board's own drop settles. Another consumer's write moves
  `:writing?` too, and releasing on that drops the card back early."
  [_child {:keys [writing?] :as view} ^js this]
  (when-let [^js zone (and (not writing?)
                           (consumer-resource/own-write? this view)
                           (reserved-zone this))]
    (.release zone)
    (du/setv! this k-pending-zone nil)
    ;; render deferred placing while the drop was reserved
    (place-cards! this (model/columns-for view))))

(defn- on-connect! [_child ^js el]
  (.addEventListener el "x-drop-zone-drop" (fn [e] (on-drop! el e))))

(defn- render!
  [_child {:keys [intent] :as view} ^js this]
  (du/set-attr! this "data-empty" (if (model/project-selected? intent) "false" "true"))
  (when-not (reserved-zone this)
    (place-cards! this (model/columns-for view))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-drop-zone"
    :render-key model/render-key
    :render     render!
    :on-writing on-writing!
    :on-connect on-connect!}))
