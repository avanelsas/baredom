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

(defn- set-card-content! [^js panel row]
  (let [{:keys [title subtitle]} (model/card-vm row)
        head (or (.querySelector panel "[slot=header]")
                 (let [h (.createElement js/document "span")]
                   (set! (.-slot h) "header")
                   (.appendChild panel h)
                   h))
        body (or (.querySelector panel ".board-card-body")
                 (let [b (.createElement js/document "div")]
                   (set! (.-className b) "board-card-body")
                   (.appendChild panel b)
                   b))]
    (du/set-attr! panel "label" title)
    (set! (.-textContent head) title)
    (set! (.-textContent body) subtitle)))

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

(defn- place-cards! [^js this cols]
  (let [pool   (panel-pool this)
        wanted (set (map #(str (get % "id")) (mapcat val cols)))]
    (doseq [^js zone (array-seq (.querySelectorAll this "x-drop-zone"))]
      (doseq [row (get cols (du/get-attr zone "value") [])]
        (let [value (str (get row "id"))
              panel (or (get pool value) (make-card! row))]
          (set-card-content! panel row)
          (.appendChild zone panel))))                 ; appendChild MOVES an existing panel
    (doseq [^js p (array-seq (.querySelectorAll this "x-drag-panel"))]
      (when-not (contains? wanted (du/get-attr p "value"))
        (.remove p)))))

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
