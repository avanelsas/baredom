(ns baredom.components.x-tooltip.model)

(def tag-name "x-tooltip")

;; Attribute name constants
(def attr-text      "text")
(def attr-placement "placement")
(def attr-delay     "delay")
(def attr-disabled  "disabled")
(def attr-open      "open")

;; Event name constants
(def event-show "x-tooltip-show")
(def event-hide "x-tooltip-hide")

;; Placement constants
(def allowed-placements #{"top" "bottom" "left" "right"})
(def default-placement "top")
(def default-delay 400)
(def max-delay 5000)

(def observed-attributes
  #js [attr-text attr-placement attr-delay attr-disabled attr-open])

(def property-api
  {:text      {:type 'string  :reflects-attribute attr-text}
   :placement {:type 'string  :reflects-attribute attr-placement}
   :delay     {:type 'number  :reflects-attribute attr-delay}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :open      {:type 'boolean :reflects-attribute attr-open}})

(def event-schema
  {event-show {:cancelable false :detail {}}
   event-hide {:cancelable false :detail {}}})

(defn normalize-placement
  "Normalizes raw placement string. Falls back to default-placement if invalid or nil."
  [s]
  (if (contains? allowed-placements s) s default-placement))

(defn parse-delay
  "Parses delay string to int, clamped to [0, 5000]. Falls back to default-delay."
  [s]
  (if (some? s)
    (let [n (js/parseInt s 10)]
      (if (js/isNaN n)
        default-delay
        (-> n (max 0) (min max-delay))))
    default-delay))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [text-raw placement-raw delay-raw disabled-present? open-present?]}]
  {:text      (or text-raw "")
   :placement (normalize-placement placement-raw)
   :delay     (parse-delay delay-raw)
   :disabled? (boolean disabled-present?)
   :open?     (boolean open-present?)})

(def method-api nil)
