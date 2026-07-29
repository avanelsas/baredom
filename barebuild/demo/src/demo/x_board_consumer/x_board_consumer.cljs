(ns demo.x-board-consumer.x-board-consumer
  "The kanban board — one consumer driving three x-drop-zones (Option B, hand-rolled multi-zone).
   Render reconciles each zone's x-drag-panel children from the tasks value (keyed by id, so a
   moved card is the SAME element and animate-moves animates it). A drop reserves the slot,
   submits the move as a write, and releases when the server confirms — no optimism: the card
   relocates only when the refetched truth re-renders it."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-board-consumer.model :as model]
   [baredom.utils.dom :as du]))

;; id (string) -> row, so the drop handler can build a full-replace record; and the zone whose
;; reservation is awaiting the write's answer.
(def ^:private k-rows         "__xBoardRows")
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
    (du/set-attr! panel "grab" "surface")   ; drag the whole card, not just the handle bar
    (set-card-content! panel row)
    panel))

;; --- render: reconcile cards into their zones ---------------------------------

(defn- panel-pool [^js this]
  (into {} (map (fn [^js p] [(du/get-attr p "value") p])
                (array-seq (.querySelectorAll this "x-drag-panel")))))

(defn- panels-in [^js zone]
  (vec (array-seq (.querySelectorAll zone "x-drag-panel"))))

(defn- reconcile-zone!
  "Make `zone`'s cards match `rows` in order, moving only the panels that are out of place — so
   an unchanged column produces NO child-list mutation, and animate-moves leaves it alone.
   Content is always refreshed (a cheap in-panel update that animate-moves ignores)."
  [^js zone rows pool]
  (let [desired (mapv (fn [row]
                        (let [^js panel (or (get pool (str (get row "id"))) (make-card! row))]
                          (set-card-content! panel row)
                          panel))
                      rows)]
    (doseq [[i ^js panel] (map-indexed vector desired)]
      (let [^js current (nth (panels-in zone) i nil)]
        (when-not (identical? current panel)
          (if current
            (.insertBefore zone panel current)
            (.appendChild zone panel)))))
    (doseq [^js extra (drop (count desired) (panels-in zone))]
      (.remove extra))))

(defn- place-cards! [^js this cols]
  (let [pool (panel-pool this)]
    (doseq [^js zone (array-seq (.querySelectorAll this "x-drop-zone"))]
      (reconcile-zone! zone (get cols (du/get-attr zone "value") []) pool))))

(defn- project-selected? []
  (some? (.get (js/URLSearchParams. (.-search js/location)) "tasks.project")))

(defn- render! [_child accepted ^js this]
  (du/setv! this k-rows
            (into {} (map (fn [r] [(str (get r "id")) r]) (:value accepted))))
  (du/set-attr! this "data-empty" (if (project-selected?) "false" "true"))
  (place-cards! this (model/columns accepted)))

;; --- drop: reserve -> write -> release ----------------------------------------

(defn- zone-for [^js this status]
  (some (fn [^js z] (when (= status (du/get-attr z "value")) z))
        (array-seq (.querySelectorAll this "x-drop-zone"))))

(defn- on-drop! [^js this ^js e]
  (let [^js detail (.-detail e)
        row        (get (du/getv this k-rows) (.-value detail))
        ^js zone   (zone-for this (.-to detail))]
    (when (and row zone)
      (.reserve zone (.-panel detail) (.-index detail))
      (du/setv! this k-pending-zone zone)
      (consumer-resource/submit-write!
       this (model/translate-drop-gesture row (.-to detail) (.-index detail))))))

(defn- on-writing! [_child writing ^js this]
  (when-let [^js zone (and (not writing) (du/getv this k-pending-zone))]
    (.release zone)
    (du/setv! this k-pending-zone nil)))

(defn- on-connect! [^js el]
  (.addEventListener el "x-drop-zone-drop" (fn [e] (on-drop! el e))))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-drop-zone"
    :observed-attributes model/observed-attributes
    :render-key          model/columns
    :render              render!
    :on-writing          on-writing!
    :on-connect          on-connect!}))
