(ns baredom.components.x-toaster.model)

;; ── Tag name ──────────────────────────────────────────────────────────────────
(def tag-name "x-toaster")

;; ── Attribute name constants ──────────────────────────────────────────────────
(def attr-position   "position")
(def attr-max-toasts "max-toasts")
(def attr-label      "label")

(def observed-attributes
  #js [attr-position attr-max-toasts attr-label])

;; ── Child component constants ─────────────────────────────────────────────────
(def child-tag                    "x-toast")
(def child-event-dismiss          "x-toast-dismiss")
(def child-dismiss-reason-toaster "toaster-remove")

;; ── Event name constants ──────────────────────────────────────────────────────
(def event-dismiss "x-toaster-dismiss")

;; ── Public API metadata ───────────────────────────────────────────────────────
(def property-api
  {:position  {:type 'string}
   :maxToasts {:type 'number}
   :label     {:type 'string}})

(def event-schema
  {event-dismiss
   {:detail     {:type 'string :reason 'string :heading 'string :message 'string}
    :cancelable true}})

;; ── Valid values ──────────────────────────────────────────────────────────────
(def ^:private valid-positions
  #{"top-start" "top-center" "top-end"
    "bottom-start" "bottom-center" "bottom-end"})

;; ── Parse helpers ─────────────────────────────────────────────────────────────
(defn parse-position
  "Normalise position attribute to a valid enum value; fallback to \"top-end\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid-positions v) v "top-end")))

(defn parse-max-toasts
  "Parse max-toasts to a positive integer; fallback to 5."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        (js/Math.floor n)))))

;; ── Normalisation ─────────────────────────────────────────────────────────────
(defn normalize
  "Produce a stable view-model map from raw attribute inputs.

  Input keys:
    :position-raw    string | nil
    :max-toasts-raw  string | nil
    :label-raw       string | nil

  Output keys:
    :position   string — one of the six valid position values
    :max-toasts number — positive integer (default 5)
    :label      string — aria-label for the region (default \"Notifications\")"
  [{:keys [position-raw max-toasts-raw label-raw]}]
  {:position   (parse-position position-raw)
   :max-toasts (or (parse-max-toasts max-toasts-raw) 5)
   :label      (if (and (string? label-raw) (not= (.trim label-raw) ""))
                 label-raw
                 "Notifications")})

;; ── Event detail builders ─────────────────────────────────────────────────────
(defn dismiss-detail
  "Build event detail map for x-toaster-dismiss from the child x-toast-dismiss detail."
  [type reason heading message]
  {:type    (or type "info")
   :reason  (or reason "")
   :heading (or heading "")
   :message (or message "")})

;; ── Derived helpers ───────────────────────────────────────────────────────────
(defn bottom-position?
  "Returns true when position places the toaster at the bottom of the viewport."
  [position]
  (or (= position "bottom-start")
      (= position "bottom-center")
      (= position "bottom-end")))

(defn center-position?
  "Returns true when position uses horizontal centering."
  [position]
  (or (= position "top-center")
      (= position "bottom-center")))

;; ── Method API metadata ──────────────────────────────────────────────────────
(def method-api
  {:toast {:args [{:name "options" :type 'object}] :returns 'void}})
