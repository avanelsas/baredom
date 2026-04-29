(ns baredom.components.x-chip.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name      "x-chip")
(def attr-label    "label")
(def attr-value    "value")
(def attr-removable "removable")
(def attr-disabled  "disabled")

(def observed-attributes
  #js [attr-label attr-value attr-removable attr-disabled])

;; ── Event names ───────────────────────────────────────────────────────────
(def event-remove "x-chip-remove")

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn parse-bool-default-true
  "nil (attribute absent) → true.
   'false' or '0' → false.
   Anything else → true."
  [s]
  (if (nil? s)
    true
    (not (or (= s "false") (= s "0")))))

(defn effective-value
  "Returns value-raw when it is a non-nil, non-empty string; else falls back to label."
  [label value-raw]
  (if (and (string? value-raw) (pos? (.-length value-raw)))
    value-raw
    label))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "Converts raw attribute strings to a clean view model.
   disabled-present? should be boolean: (.hasAttribute el attr-disabled)"
  [{:keys [label-raw value-raw removable-raw disabled-present?]}]
  (let [label      (or label-raw "")
        removable? (parse-bool-default-true removable-raw)
        disabled?  (boolean disabled-present?)]
    {:label      label
     :value      (effective-value label value-raw)
     :removable? removable?
     :disabled?  disabled?}))

;; ── Derived helpers ───────────────────────────────────────────────────────
(defn removal-eligible?
  "True when the chip can be removed by the user: removable and not disabled."
  [{:keys [removable? disabled?]}]
  (and removable? (not disabled?)))

(defn remove-detail
  "Builds the JS detail object for x-chip-remove events."
  [{:keys [value label]}]
  #js {:value value :label label})

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:label     {:type 'string}
   :value     {:type 'string}
   :removable {:type 'boolean}
   :disabled  {:type 'boolean}})

;; ── Event schema ──────────────────────────────────────────────────────────
(def event-schema
  {event-remove {:detail     {:value 'string :label 'string}
                 :cancelable true}})

(def method-api nil)
