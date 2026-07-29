(ns demo.x-board-consumer.x-board-consumer
  "The kanban board, one consumer driving three x-drop-zones.
   Render reconciles each zone's x-drag-panel children from the tasks value (keyed by id, so a
   moved card is the SAME element and animate-moves animates it). A drop reserves a slot,
   submits the move as a write, and releases when the server confirms."
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-board-consumer.model :as model]
   [baredom.utils.dom :as du]))

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
    (du/set-attr! panel "grab" "surface")
    (set-card-content! panel row)
    panel))

;; --- render: reconcile cards into their zones ---------------------------------

(defn- panel-pool [^js this]
  (into {} (map (fn [^js p] [(du/get-attr p "value") p])
                (array-seq (.querySelectorAll this "x-drag-panel")))))

(defn- panel? [^js node]
  (and (some? node) (= "x-drag-panel" (.. node -tagName toLowerCase))))

(defn- first-panel [^js zone]
  (loop [^js n (.-firstElementChild zone)]
    (cond (nil? n) nil, (panel? n) n, :else (recur (.-nextElementSibling n)))))

(defn- next-panel [^js node]
  (loop [^js n (.-nextElementSibling node)]
    (cond (nil? n) nil, (panel? n) n, :else (recur (.-nextElementSibling n)))))

(defn- reconcile-zone!
  "Reconciles card movements in a zone. Server decides the order, here we try to materialise
them by reusing the current content (pool) and refreshing it with the server state received. Only
cards that are out of place will mutate and animate-move."
  [^js zone rows pool]
  (let [wanted (mapv (fn [row]
                       (let [^js panel (or (get pool (str (get row "id"))) (make-card! row))]
                         (set-card-content! panel row)
                         panel))
                     rows)]
    (loop [ws wanted, ^js anchor (first-panel zone)]
      (if-let [^js panel (first ws)]
        (if (identical? panel anchor)
          (recur (rest ws) (next-panel anchor))
          (do (.insertBefore zone panel anchor)
              (recur (rest ws) anchor)))
        (loop [^js n anchor]
          (when n
            (let [^js nxt (next-panel n)]
              (.remove n)
              (recur nxt))))))))

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

(defn- write-pending? [^js this]
  (some? (du/getv this k-pending-zone)))

(defn- on-drop! [^js this ^js e]
  (let [^js detail (.-detail e)
        row        (get (du/getv this k-rows) (.-value detail))
        ^js zone   (zone-for this (.-to detail))]
    (when (and row zone (not (write-pending? this)))
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
