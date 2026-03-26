(ns app.components.x-timeline-item.model)

(def tag-name "x-timeline-item")

;; ── Attribute name constants ──────────────────────────────────────────────────
(def attr-label     "label")
(def attr-title     "title")
(def attr-status    "status")
(def attr-icon      "icon")
(def attr-connector "connector")
(def attr-position  "position")
(def attr-disabled  "disabled")

;; Parent-managed data attributes (observed so the item reacts when parent sets them)
(def data-attr-last     "data-last")
(def data-attr-index    "data-index")
(def data-attr-position "data-position")
(def data-attr-striped  "data-striped")

(def observed-attributes
  #js ["label" "title" "status" "icon" "connector" "position" "disabled"
       "data-last" "data-index" "data-position" "data-striped"])

;; ── Event name constants ──────────────────────────────────────────────────────
(def event-connected    "x-timeline-item-connected")
(def event-disconnected "x-timeline-item-disconnected")
(def event-click        "x-timeline-item-click")

;; ── Public API metadata ───────────────────────────────────────────────────────
(def property-api
  {:label     {:type 'string}
   :title     {:type 'string}
   :status    {:type 'string}
   :icon      {:type 'string}
   :connector {:type 'string}
   :position  {:type 'string}
   :disabled  {:type 'boolean}})

(def event-schema
  {event-connected    {:detail     {:status 'string :label 'string :position 'string :disabled 'boolean}
                       :cancelable false}
   event-disconnected {:detail     {:status 'string :label 'string}
                       :cancelable false}
   event-click        {:detail     {:status 'string :label 'string}
                       :cancelable true}})

;; ── Private lookup tables ─────────────────────────────────────────────────────
(def ^:private status->kw
  {"pending" :pending "active" :active "complete" :complete
   "error"   :error   "warning" :warning})

(def ^:private kw->status
  {:pending "pending" :active "active" :complete "complete"
   :error   "error"   :warning "warning"})

(def ^:private status->icon
  {:pending "○" :active "●" :complete "✓" :error "✕" :warning "!"})

(def ^:private connector->kw
  {"solid" :solid "dashed" :dashed "none" :none})

(def ^:private position->kw
  {"start" :start "end" :end})

;; ── Parse helpers ─────────────────────────────────────────────────────────────
(defn parse-status
  "Normalise a raw status attribute string to a keyword. Defaults to :pending."
  [s]
  (or (get status->kw (when (string? s) (.toLowerCase s)))
      :pending))

(defn status->attr
  "Convert an internal status keyword back to its attribute string."
  [k]
  (or (get kw->status k) "pending"))

(defn parse-connector
  "Normalise a raw connector attribute string. Defaults to :solid."
  [s]
  (or (get connector->kw (when (string? s) (.toLowerCase s)))
      :solid))

(defn parse-position
  "Normalise a raw position attribute string to :start or :end. Defaults to :start."
  [s]
  (or (get position->kw (when (string? s) (.toLowerCase s)))
      :start))

(defn normalize-icon
  "Normalise icon attribute: absent/nil/empty/\"none\" become nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (and (not= v "") (not= (.toLowerCase v) "none"))
        v))))

(defn parse-data-index
  "Parse data-index attribute to a non-negative integer, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (number? n) (not (js/isNaN n)) (>= n 0))
        n))))

(defn effective-position
  "Returns the effective position keyword for rendering.
  Reads data-position first (parent override, accepts only start/end).
  Falls back to position attribute. Defaults to :start."
  [data-pos-raw pos-raw]
  (or (get position->kw (when (string? data-pos-raw) (.toLowerCase data-pos-raw)))
      (parse-position pos-raw)))

(defn icon-for-status
  "Return the default icon glyph for a status keyword."
  [k]
  (or (get status->icon k) (get status->icon :pending)))

(defn marker-aria-label
  "Build the ARIA label string for the marker element."
  [status label data-index]
  (let [status-str (status->attr status)
        index-str  (when (some? data-index) (str "Step " (inc data-index) ": "))]
    (str index-str
         (when (and (string? label) (not= label "")) (str label " "))
         "(" status-str ")")))

;; ── Normalize ─────────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :label-raw        string | nil
    :title-raw        string | nil
    :status-raw       string | nil
    :icon-present?    boolean
    :icon-raw         string | nil
    :connector-raw    string | nil
    :position-raw     string | nil
    :disabled?        boolean
    :data-last?       boolean
    :data-index-raw   string | nil
    :data-position-raw string | nil
    :data-striped?    boolean

  Output keys:
    :label             string
    :title             string
    :status            keyword  — :pending | :active | :complete | :error | :warning
    :icon-mode         keyword  — :default | :custom | :hidden
    :icon              string | nil
    :connector         keyword  — :solid | :dashed | :none
    :effective-position keyword — :start | :end
    :disabled?         boolean
    :data-last?        boolean
    :data-index        int | nil
    :data-striped?     boolean
    :marker-icon       string | nil
    :marker-aria       string"
  [{:keys [label-raw title-raw status-raw icon-present? icon-raw
           connector-raw position-raw disabled? data-last? data-index-raw
           data-position-raw data-striped?]}]
  (let [status    (parse-status status-raw)
        icon*     (normalize-icon icon-raw)
        icon-mode (cond
                    (and icon-present? (nil? icon*)) :hidden
                    (some? icon*)                    :custom
                    :else                            :default)
        marker-icon (case icon-mode
                      :default (icon-for-status status)
                      :custom  icon*
                      :hidden  nil)
        data-index  (parse-data-index data-index-raw)
        eff-pos     (effective-position data-position-raw position-raw)
        label       (or label-raw "")
        title       (or title-raw "")]
    {:label              label
     :title              title
     :status             status
     :icon-mode          icon-mode
     :icon               (when (= icon-mode :custom) icon*)
     :connector          (parse-connector connector-raw)
     :effective-position eff-pos
     :disabled?          (boolean disabled?)
     :data-last?         (boolean data-last?)
     :data-index         data-index
     :data-striped?      (boolean data-striped?)
     :marker-icon        marker-icon
     :marker-aria        (marker-aria-label status label data-index)}))

;; ── Predicates ────────────────────────────────────────────────────────────────
(defn interactive-eligible?
  "Return true when the item can receive click/keyboard interactions."
  [{:keys [disabled?]}]
  (not disabled?))

;; ── Event detail builders ─────────────────────────────────────────────────────
(defn connected-detail
  "Build the event detail map for an x-timeline-item-connected event."
  [{:keys [status label effective-position disabled?]}]
  {:status   (status->attr status)
   :label    label
   :position (name effective-position)
   :disabled (boolean disabled?)})

(defn disconnected-detail
  "Build the event detail map for an x-timeline-item-disconnected event."
  [{:keys [status label]}]
  {:status (status->attr status)
   :label  label})

(defn click-detail
  "Build the event detail map for an x-timeline-item-click event."
  [{:keys [status label]}]
  {:status (status->attr status)
   :label  label})
