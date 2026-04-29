(ns baredom.components.x-collapse.model)

(def tag-name "x-collapse")

;; Attribute name constants
(def attr-open        "open")
(def attr-disabled    "disabled")
(def attr-header      "header")
(def attr-duration-ms "duration-ms")

;; Event name constants
(def event-toggle "x-collapse-toggle")
(def event-change "x-collapse-change")

;; Slot name constants
(def slot-content "content")

(def default-duration-ms 300)

(def observed-attributes
  #js [attr-open attr-disabled attr-header attr-duration-ms])

(def property-api
  {:open       {:type 'boolean :reflects-attribute attr-open}
   :disabled   {:type 'boolean :reflects-attribute attr-disabled}
   :header     {:type 'string  :reflects-attribute attr-header}
   :durationMs {:type 'number  :reflects-attribute attr-duration-ms}})

(def event-schema
  {event-toggle {:cancelable true
                 :detail     {:open   'boolean
                              :source 'string}}
   event-change {:cancelable false
                 :detail     {:open 'boolean}}})

(defn parse-int-pos
  "Parses s as a positive integer. Returns nil if s is nil, not a number, or <= 0."
  [s]
  (when s
    (let [n (js/parseInt s 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        n))))

(defn clamp
  "Clamps n to the range [lo hi]."
  [n lo hi]
  (max lo (min hi n)))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [open-present? disabled-present? header-raw duration-ms-raw]}]
  (let [open?     (boolean open-present?)
        disabled? (boolean disabled-present?)
        header    (or header-raw "")
        dur-raw   (parse-int-pos duration-ms-raw)
        duration  (if dur-raw (clamp dur-raw 0 2000) default-duration-ms)]
    {:open?       open?
     :disabled?   disabled?
     :header      header
     :duration-ms duration}))

(defn toggle-detail
  "Produces the CustomEvent detail object for toggle/change events."
  [open? source]
  #js {:open open? :source source})

(def method-api nil)
