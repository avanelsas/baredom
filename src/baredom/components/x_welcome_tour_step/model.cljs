(ns baredom.components.x-welcome-tour-step.model
  "Pure functions for the x-welcome-tour-step child element.
   This component is a passive data holder — the parent orchestrator
   reads its attributes and renders the tour UI.")

(def tag-name "x-welcome-tour-step")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-target         "target")
(def attr-title          "title")
(def attr-placement      "placement")
(def attr-connector      "connector")
(def attr-cutout-padding "cutout-padding")
(def attr-cutout-radius  "cutout-radius")
(def attr-scroll-to      "scroll-to")

(def observed-attributes
  #js [attr-target attr-title attr-placement attr-connector
       attr-cutout-padding attr-cutout-radius attr-scroll-to])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-connected    "x-welcome-tour-step-connected")
(def event-disconnected "x-welcome-tour-step-disconnected")

;; ── Public API metadata ─────────────────────────────────────────────────────
(def property-api
  {:target        {:type 'string}
   :title         {:type 'string}
   :placement     {:type 'string}
   :connector     {:type 'string}
   :cutoutPadding {:type 'number}
   :cutoutRadius  {:type 'number}
   :scrollTo      {:type 'boolean}})

(def event-schema
  {event-connected    {:detail {} :cancelable false}
   event-disconnected {:detail {} :cancelable false}})

;; ── Enums ───────────────────────────────────────────────────────────────────
(def allowed-placements
  #{"top" "bottom" "left" "right"
    "top-start" "top-end" "bottom-start" "bottom-end"})

(def default-placement "bottom")

(def allowed-connectors #{"arrow" "line" "curve" "none"})

;; ── Parse helpers ───────────────────────────────────────────────────────────
(defn parse-placement
  "Normalise a raw placement string. Falls back to default-placement if invalid."
  [s]
  (if (and (string? s) (contains? allowed-placements (.toLowerCase s)))
    (.toLowerCase s)
    default-placement))

(defn parse-connector
  "Normalise a raw connector string. Returns nil when absent (inherit from parent)."
  [s]
  (when (and (string? s) (contains? allowed-connectors (.toLowerCase s)))
    (.toLowerCase s)))

(defn parse-cutout-padding
  "Parse cutout-padding to a non-negative number. Defaults to 8."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (>= n 0)) n 8))
    8))

(defn parse-cutout-radius
  "Parse cutout-radius to a non-negative number. Defaults to 4."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (>= n 0)) n 4))
    4))

(defn parse-scroll-to
  "Parse scroll-to attribute. Defaults to true when absent (presence-based but
   default-on; only false when explicitly set to \"false\")."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

;; ── Normalize ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :target-raw          string | nil
    :title-raw           string | nil
    :placement-raw       string | nil
    :connector-raw       string | nil
    :cutout-padding-raw  string | nil
    :cutout-radius-raw   string | nil
    :scroll-to-raw       string | nil   (nil = attribute absent)

  Output keys:
    :target          string | nil
    :title           string
    :placement       string
    :connector       string | nil  (nil = inherit from parent)
    :cutout-padding  number
    :cutout-radius   number
    :scroll-to?      boolean"
  [{:keys [target-raw title-raw placement-raw connector-raw
           cutout-padding-raw cutout-radius-raw scroll-to-raw]}]
  {:target         (when (and (string? target-raw) (not= (.trim target-raw) ""))
                     (.trim target-raw))
   :title          (or title-raw "")
   :placement      (parse-placement placement-raw)
   :connector      (parse-connector connector-raw)
   :cutout-padding (parse-cutout-padding cutout-padding-raw)
   :cutout-radius  (parse-cutout-radius cutout-radius-raw)
   :scroll-to?     (parse-scroll-to scroll-to-raw)})
