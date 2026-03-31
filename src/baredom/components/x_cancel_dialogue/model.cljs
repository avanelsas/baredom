(ns baredom.components.x-cancel-dialogue.model)

(def tag-name "x-cancel-dialogue")

;; Attribute name constants
(def attr-open         "open")
(def attr-disabled     "disabled")
(def attr-headline     "headline")
(def attr-message      "message")
(def attr-confirm-text "confirm-text")
(def attr-cancel-text  "cancel-text")
(def attr-danger       "danger")

;; Event name constants
(def event-cancel-request  "x-cancel-dialogue-cancel-request")
(def event-cancel          "x-cancel-dialogue-cancel")
(def event-confirm-request "x-cancel-dialogue-confirm-request")
(def event-confirm         "x-cancel-dialogue-confirm")

;; Default text constants
(def default-headline      "Discard changes?")
(def default-confirm-text  "Discard")
(def default-cancel-text   "Keep editing")

(def observed-attributes
  #js ["open" "disabled" "headline" "message" "confirm-text" "cancel-text" "danger"])

(defn parse-bool-attr
  "Returns true if the attr value indicates presence (not nil), false otherwise."
  [s]
  (some? s))

(defn normalize
  "Produce a normalized view-model map from raw attribute values."
  [{:keys [open-present? disabled-present? headline-raw message-raw
           confirm-text-raw cancel-text-raw danger-present?]}]
  {:open?        (boolean open-present?)
   :disabled?    (boolean disabled-present?)
   :headline     (or (and (string? headline-raw) (not= headline-raw "") headline-raw)
                     default-headline)
   :message      (when (and (string? message-raw) (not= message-raw ""))
                   message-raw)
   :confirm-text (or (and (string? confirm-text-raw) (not= confirm-text-raw "") confirm-text-raw)
                     default-confirm-text)
   :cancel-text  (or (and (string? cancel-text-raw) (not= cancel-text-raw "") cancel-text-raw)
                     default-cancel-text)
   :danger?      (boolean danger-present?)})

(defn cancel-request-detail
  "Build the x-cancel-dialogue-cancel-request CustomEvent detail."
  [reason]
  #js {:reason reason})

(defn confirm-request-detail
  "Build the x-cancel-dialogue-confirm-request CustomEvent detail."
  []
  #js {})

(def property-api
  {:open        {:type 'boolean :reflects-attribute attr-open}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :headline    {:type 'string  :reflects-attribute attr-headline}
   :message     {:type 'string  :reflects-attribute attr-message}
   :confirmText {:type 'string  :reflects-attribute attr-confirm-text}
   :cancelText  {:type 'string  :reflects-attribute attr-cancel-text}
   :danger      {:type 'boolean :reflects-attribute attr-danger}})

(def event-schema
  {event-cancel-request  {:cancelable true  :detail {:reason 'string}}
   event-cancel          {:cancelable false :detail {}}
   event-confirm-request {:cancelable true  :detail {}}
   event-confirm         {:cancelable false :detail {}}})
