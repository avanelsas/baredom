(ns baredom.components.x-toast.model)

(def tag-name "x-toast")

(def attr-type          "type")
(def attr-heading       "heading")
(def attr-message       "message")
(def attr-icon          "icon")
(def attr-dismissible   "dismissible")
(def attr-disabled      "disabled")
(def attr-timeout-ms    "timeout-ms")
(def attr-show-progress "show-progress")

(def observed-attributes
  #js [attr-type attr-heading attr-message attr-icon
       attr-dismissible attr-disabled attr-timeout-ms attr-show-progress])

(def event-dismiss "x-toast-dismiss")

(def property-api
  {:type         {:type 'string}
   :heading      {:type 'string}
   :message      {:type 'string}
   :icon         {:type 'string}
   :dismissible  {:type 'boolean}
   :disabled     {:type 'boolean}
   :timeoutMs    {:type 'number}
   :showProgress {:type 'boolean}})

(def event-schema
  {event-dismiss {:detail     {:type 'string :reason 'string
                               :heading 'string :message 'string}
                  :cancelable true}})

(def ^:private default-type :info)

(def ^:private type->kw
  {"info" :info "success" :success "warning" :warning "error" :error})

(def ^:private kw->type
  {:info "info" :success "success" :warning "warning" :error "error"})

(def ^:private type->icon
  {:info "ℹ" :success "✓" :warning "!" :error "⨯"})

(defn parse-type
  "Normalise a raw type attribute string to a keyword.
  Unknown / nil values fall back to :info."
  [s]
  (or (get type->kw (when (string? s) (.toLowerCase s)))
      default-type))

(defn type->attr
  "Convert an internal type keyword back to its attribute string."
  [t]
  (or (get kw->type t) "info"))

(defn parse-bool-default-true
  "Parse an attribute that is true when absent or empty, false only when \"false\"."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-bool-default-false
  "Parse an attribute that is false when absent, true when present (any value except \"false\")."
  [s]
  (if (nil? s)
    false
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-timeout-ms
  "Parse timeout-ms attribute to a positive integer, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (number? n) (not (js/isNaN n)) (pos? n))
        n))))

(defn normalize-icon
  "Normalise icon attribute: empty strings and \"none\" become nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (and (not= v "") (not= (.toLowerCase v) "none"))
        v))))

(defn role-for-type
  "Return the ARIA live-region role for a given toast type.
  warning/error use role=alert (assertive); info/success use role=status (polite)."
  [t]
  (if (or (= t :warning) (= t :error)) "alert" "status"))

(defn default-icon-for-type
  "Return the default icon glyph for a type keyword."
  [t]
  (or (get type->icon t) (get type->icon default-type)))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :type-raw            string | nil
    :heading             string | nil
    :message             string | nil
    :icon-present?       boolean
    :icon-raw            string | nil
    :dismissible-attr    string | nil
    :disabled-present?   boolean
    :timeout-ms-raw      string | nil
    :show-progress-attr  string | nil

  Output keys:
    :type           keyword  — :info | :success | :warning | :error
    :heading        string   — trimmed, may be \"\"
    :message        string   — trimmed, may be \"\"
    :icon-mode      keyword  — :default | :custom | :hidden
    :icon           string | nil  (only meaningful when :custom)
    :dismissible?   boolean
    :disabled?      boolean
    :timeout-ms     int | nil
    :show-progress? boolean"
  [{:keys [type-raw heading message icon-present? icon-raw
           dismissible-attr disabled-present? timeout-ms-raw show-progress-attr]}]
  (let [t     (parse-type type-raw)
        icon* (normalize-icon icon-raw)
        icon-mode (cond
                    (and icon-present? (nil? icon*)) :hidden
                    (some? icon*)                    :custom
                    :else                            :default)]
    {:type          t
     :heading       (or (when (string? heading) (.trim heading)) "")
     :message       (or (when (string? message) (.trim message)) "")
     :icon-mode     icon-mode
     :icon          (when (= icon-mode :custom) icon*)
     :dismissible?  (parse-bool-default-true dismissible-attr)
     :disabled?     (boolean disabled-present?)
     :timeout-ms    (parse-timeout-ms timeout-ms-raw)
     :show-progress? (parse-bool-default-false show-progress-attr)}))

(defn dismiss-eligible?
  "Return true when the toast can currently be dismissed (dismissible and not disabled)."
  [{:keys [dismissible? disabled?]}]
  (and dismissible? (not disabled?)))

(defn dismiss-detail
  "Build the event detail map for an x-toast-dismiss event."
  [{:keys [type heading message]} reason]
  {:type    (type->attr type)
   :reason  reason
   :heading heading
   :message message})

(defn progress-eligible?
  "Return true when both show-progress is set and timeout-ms is a valid positive integer."
  [{:keys [show-progress? timeout-ms]}]
  (boolean (and show-progress? (some? timeout-ms) (pos? timeout-ms))))

(def method-api nil)
