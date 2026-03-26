(ns app.components.x-copy.model)

(def tag-name "x-copy")

;; Attribute name constants
(def attr-text           "text")
(def attr-from           "from")
(def attr-from-attr      "from-attr")
(def attr-mode           "mode")
(def attr-disabled       "disabled")
(def attr-show-tooltip   "show-tooltip")
(def attr-tooltip-ms     "tooltip-ms")
(def attr-success-message "success-message")
(def attr-error-message  "error-message")
(def attr-hotkey         "hotkey")

;; Event name constants
(def event-copy-request "x-copy-request")
(def event-copy-success "x-copy-success")
(def event-copy-error   "x-copy-error")

;; Slot name constants
(def slot-default "default")
(def slot-tooltip "tooltip")

;; CSS custom property prefix
(def css-prefix "--x-copy-")

(def observed-attributes
  #js ["text" "from" "from-attr" "mode" "disabled" "show-tooltip"
       "tooltip-ms" "success-message" "error-message" "hotkey"])

(defn parse-mode
  "Returns \"text\" or \"html\", defaulting to \"text\"."
  [s]
  (if (= s "html") "html" "text"))

(defn parse-bool-default-true
  "nil/absent → true, \"false\"/\"0\" → false, anything else → true."
  [s]
  (cond
    (nil? s)                true
    (= s "false")           false
    (= s "0")               false
    :else                   true))

(defn parse-int-pos
  "Parse a positive integer from s, returning default-v if not valid or not positive."
  [s default-v]
  (if (and (string? s) (not= s ""))
    (let [n (js/parseInt s 10)]
      (if (and (not (js/isNaN n)) (pos? n))
        n
        default-v))
    default-v))

(defn clamp
  "Clamp integer n to [lo, hi]."
  [n lo hi]
  (js/Math.min hi (js/Math.max lo n)))

(defn normalize
  "Produce a normalized view-model map from raw attribute values."
  [{:keys [text-raw from-raw from-attr-raw mode-raw disabled-present?
           show-tooltip-raw tooltip-ms-raw success-message-raw
           error-message-raw hotkey-raw]}]
  {:text            (when (and (string? text-raw) (not= (.trim text-raw) ""))
                      (.trim text-raw))
   :from            (when (and (string? from-raw) (not= (.trim from-raw) ""))
                      (.trim from-raw))
   :from-attr       (when (and (string? from-attr-raw) (not= (.trim from-attr-raw) ""))
                      (.trim from-attr-raw))
   :mode            (parse-mode mode-raw)
   :disabled?       (boolean disabled-present?)
   :show-tooltip?   (parse-bool-default-true show-tooltip-raw)
   :tooltip-ms      (clamp (parse-int-pos tooltip-ms-raw 1200) 100 10000)
   :success-message (or (and (string? success-message-raw)
                              (not= success-message-raw "")
                              success-message-raw)
                        "Copied")
   :error-message   (or (and (string? error-message-raw)
                              (not= error-message-raw "")
                              error-message-raw)
                        "Copy failed")
   :hotkey          (when (and (string? hotkey-raw) (not= (.trim hotkey-raw) ""))
                      (.trim hotkey-raw))})

(defn request-detail
  "Build the x-copy-request CustomEvent detail object."
  [{:keys [text from from-attr mode]}]
  #js {:text     (or text "")
       :mode     (or mode "text")
       :from     (or from nil)
       :fromAttr (or from-attr nil)})

(defn success-detail
  "Build the x-copy-success CustomEvent detail object."
  [text]
  #js {:text (or text "")})

(defn error-detail
  "Build the x-copy-error CustomEvent detail object."
  [err]
  #js {:error (str err)})

(def property-api
  {:text           {:type 'string  :reflects-attribute attr-text}
   :from           {:type 'string  :reflects-attribute attr-from}
   :fromAttr       {:type 'string  :reflects-attribute attr-from-attr}
   :mode           {:type 'string  :reflects-attribute attr-mode}
   :disabled       {:type 'boolean :reflects-attribute attr-disabled}
   :showTooltip    {:type 'boolean :reflects-attribute attr-show-tooltip}
   :tooltipMs      {:type 'number  :reflects-attribute attr-tooltip-ms}
   :successMessage {:type 'string  :reflects-attribute attr-success-message}
   :errorMessage   {:type 'string  :reflects-attribute attr-error-message}
   :hotkey         {:type 'string  :reflects-attribute attr-hotkey}
   :textValue      {:type 'string  :reflects-attribute nil :note "property-only override"}})

(def event-schema
  {event-copy-request {:cancelable true  :detail {:text 'string :mode 'string :from 'string :fromAttr 'string}}
   event-copy-success {:cancelable false :detail {:text 'string}}
   event-copy-error   {:cancelable false :detail {:error 'string}}})
