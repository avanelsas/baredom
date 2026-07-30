(ns baredom.components.x-drop-zone.model
  (:require [clojure.string :as str]))

(def tag-name "x-drop-zone")

;; ── Attribute name constants ─────────────────────────────────────────────────
(def attr-value         "value")
(def attr-accepts       "accepts")
(def attr-max           "max")
(def attr-label         "label")
(def attr-disabled      "disabled")
(def attr-pending       "pending")
(def attr-pending-index "pending-index")
(def attr-animate-moves "animate-moves")

(def observed-attributes
  #js [attr-value attr-accepts attr-max attr-label attr-disabled attr-pending
       attr-pending-index attr-animate-moves])

;; ── Event name constants ─────────────────────────────────────────────────────
(def event-enter "x-drop-zone-enter")
(def event-leave "x-drop-zone-leave")
(def event-drop  "x-drop-zone-drop")

;; ── Part name constants ──────────────────────────────────────────────────────
(def part-zone  "zone")
(def part-caret "caret")
(def part-empty "empty")

;; ── State attribute ──────────────────────────────────────────────────────────
;; Unobserved: set by the component for styling only, so it stays outside the
;; change guard. One valued attribute rather than two booleans, because the two
;; states are mutually exclusive and two booleans would make an impossible
;; both-set combination representable.
(def attr-drag-state "data-drag-state")
(def state-over      "over")
(def state-reject    "reject")

;; ── Default values ───────────────────────────────────────────────────────────
(def default-label "Drop zone")

;; ── Normalisation ────────────────────────────────────────────────────────────
(defn normalize-label
  "Normalize the raw label attribute. Falls back to default-label."
  [raw]
  (if (and (string? raw) (not= raw ""))
    raw
    default-label))

(defn parse-accepts
  "Parse the raw `accepts` attribute into a set of kinds, or nil.

  nil means *accept anything* — deliberately distinct from the empty set, which
  would mean accept nothing. An author who writes `accepts=\"\"` means they have
  not constrained the zone, not that they have closed it."
  [raw]
  (when (string? raw)
    (let [kinds (into #{} (remove str/blank?) (str/split (str/trim raw) #"\s+"))]
      (when (seq kinds) kinds))))

(defn parse-count
  "Parse a raw attribute into a non-negative integer, or nil.

  Absent, blank, non-numeric and negative all collapse to nil: a capacity or an
  index that cannot be read is better treated as unset than coerced to zero,
  which would silently close a zone or pin a caret to the top."
  [raw]
  (when (string? raw)
    (let [t (str/trim raw)]
      (when (not= "" t)
        (let [n (js/parseInt t 10)]
          (when (and (js/isFinite n) (>= n 0)) n))))))

(defn normalize-value
  "Normalize the raw `value` attribute — the zone's opaque identity key.

  Absent collapses to the empty string, matching the panel's `value`. Callers
  that need to distinguish *this zone has no id* from *there was no zone at all*
  do so at the boundary, where nil means the latter."
  [raw]
  (if (string? raw) raw ""))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :value-raw              string | nil
    :accepts-raw            string | nil
    :max-raw                string | nil
    :label-raw              string | nil
    :disabled-present?      boolean
    :pending-present?       boolean
    :pending-index-raw      string | nil
    :animate-moves-present? boolean

  Output keys:
    :value          string
    :accept-kinds   set | nil  (nil → accepts anything)
    :capacity       number | nil
    :label          string
    :disabled?      boolean
    :pending?       boolean
    :pending-index  number | nil
    :animate-moves? boolean"
  [{:keys [value-raw accepts-raw max-raw label-raw disabled-present?
           pending-present? pending-index-raw animate-moves-present?]}]
  {:value          (normalize-value value-raw)
   :accept-kinds   (parse-accepts accepts-raw)
   :capacity       (parse-count max-raw)
   :label          (normalize-label label-raw)
   :disabled?      (boolean disabled-present?)
   :pending?       (boolean pending-present?)
   :pending-index  (parse-count pending-index-raw)
   :animate-moves? (boolean animate-moves-present?)})

;; ── Acceptance ───────────────────────────────────────────────────────────────
(defn accepts?
  "True when a zone in state `m` would accept a panel of `kind`.

  `child-count` is the zone's current panel count, read from the DOM rather than
  accumulated, so capacity stays a function of what is actually there. A pending
  reservation is deliberately not counted: the panel has not arrived, and
  counting it would reject a second drop the server may well accept.

  Input keys:
    :disabled?    boolean
    :accept-kinds set | nil
    :capacity     number | nil
    :child-count  number
    :kind         string"
  [{:keys [disabled? accept-kinds capacity child-count kind]}]
  (boolean
   (and (not disabled?)
        (or (nil? accept-kinds) (contains? accept-kinds kind))
        (or (nil? capacity) (< (or child-count 0) capacity)))))

(defn drag-state
  "The `data-drag-state` value for a hovering panel, or nil when none hovers."
  [{:keys [hovering? accepted?]}]
  (when hovering?
    (if accepted? state-over state-reject)))

;; ── Insertion geometry ───────────────────────────────────────────────────────
;; All three take measurements supplied by the caller and never touch the DOM.
;; Spans are `{:start :end}` along the zone's flow axis, in DOM order. A zone is
;; assumed to lay its panels out on a single axis; a wrapping layout would need
;; two-dimensional hit-testing, which is out of scope.

(defn span-midpoints
  "Midpoint of each child span along the flow axis."
  [spans]
  (mapv (fn midpoint-of [{:keys [start end]}]
          (/ (+ (or start 0) (or end 0)) 2))
        spans))

(defn insert-index
  "Index a panel would occupy if dropped at `pointer` along the flow axis.

  The count of children whose midpoint precedes the pointer. The dragged panel
  is excluded from `midpoints` by the caller when it already lives in this zone,
  so dropping a panel back where it started yields its original index and the
  app can cheaply no-op."
  [midpoints pointer]
  (count (filter (fn before-pointer? [m] (< m (or pointer 0))) midpoints)))

(defn boundary-offsets
  "Caret offsets for every insertion index, given child spans and the zone's
  content bounds. Always one longer than `spans`.

  An empty zone yields a single centred offset, so the caret reads as *here*
  rather than as a stray line pinned to the top edge."
  [spans content-start content-end]
  (let [spans (vec spans)
        n     (count spans)
        start (or content-start 0)
        end   (or content-end 0)]
    (if (zero? n)
      [(/ (+ start end) 2)]
      (mapv (fn boundary-at [i]
              (cond
                (zero? i) (:start (nth spans 0))
                (= i n)   (:end (nth spans (dec n)))
                :else     (/ (+ (:end (nth spans (dec i)))
                                (:start (nth spans i)))
                             2)))
            (range (inc n))))))

(defn caret-offset
  "Offset for the caret at `index`, clamped into the available boundaries.

  Returns nil when there is no index to render. Clamping rather than erroring
  matters because `pending-index` is author-set and may outlive the child list
  it was computed against — a stale index should park the caret at the end, not
  throw during a render."
  [boundaries index]
  (when (and (seq boundaries) index)
    (nth boundaries (max 0 (min index (dec (count boundaries)))))))

;; ── animate-moves (FLIP) ─────────────────────────────────────────────────────
(defn flip-delta
  "The FLIP translation to animate a panel from its previous box to its current one, both given
  as {:left :top :width :height}, or nil when there is nothing to animate. Returns nil when
  either box was measured while not laid out (zero size, e.g. inside a display:none subtree), so
  a panel never animates in from a stale (0,0) origin, and nil when the panel did not move."
  [old-box new-box]
  (let [laid-out? (fn [{:keys [width height]}] (and (pos? (or width 0)) (pos? (or height 0))))]
    (when (and (laid-out? old-box) (laid-out? new-box))
      (let [dx (- (:left old-box) (:left new-box))
            dy (- (:top old-box) (:top new-box))]
        (when (or (not= 0 dx) (not= 0 dy))
          {:dx dx :dy dy})))))

;; ── Event details ────────────────────────────────────────────────────────────
(defn hover-detail
  "Build the x-drop-zone-enter / x-drop-zone-leave CustomEvent detail."
  [kind value]
  #js {:kind kind :value value})

(defn drop-detail
  "Build the x-drop-zone-drop CustomEvent detail.

  Both endpoints are opaque strings rather than element references: a
  confirmation re-render can replace the panel and its source zone, so an app
  that held elements across the round trip would be writing to detached nodes.
  `panel` stays because the synchronous path needs a node to move and it cannot
  be derived; `from` is nil when the panel was dragged from outside any zone,
  which is distinct from a source zone that simply has no `value`."
  [kind value from to index panel]
  #js {:kind  kind
       :value value
       :from  from
       :to    to
       :index index
       :panel panel})

;; Eight entries — exactly at the array-map threshold. A ninth flips this to a
;; hash-map and reshuffles every generated adapter file, so anything further
;; has to displace an existing entry rather than join them.
(def property-api
  {:value        {:type 'string  :reflects-attribute attr-value   :default ""}
   :accepts      {:type 'string  :reflects-attribute attr-accepts :default ""}
   :max          {:type 'number  :reflects-attribute attr-max     :default nil}
   :label        {:type 'string  :reflects-attribute attr-label   :default default-label}
   :disabled     {:type 'boolean :reflects-attribute attr-disabled}
   :pending      {:type 'boolean :reflects-attribute attr-pending}
   :pendingIndex {:type 'number  :reflects-attribute attr-pending-index :default nil}
   :animateMoves {:type 'boolean :reflects-attribute attr-animate-moves}})

(def event-schema
  {event-enter {:cancelable false :detail {:kind 'string :value 'string}}
   event-leave {:cancelable false :detail {:kind 'string :value 'string}}
   event-drop  {:cancelable false :detail {:kind  'string
                                           :value 'string
                                           :from  'NullableString
                                           :to    'string
                                           :index 'number
                                           :panel 'HTMLElement}}})

(def method-api
  {:reserve {:args    [{:name "panel" :type 'HTMLElement}
                       {:name "index" :type 'number}]
             :returns 'void}
   :release {:args [] :returns 'void}})
