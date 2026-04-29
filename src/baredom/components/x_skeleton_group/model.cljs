(ns baredom.components.x-skeleton-group.model)

(def tag-name "x-skeleton-group")

;; Attribute name constants
(def attr-preset    "preset")
(def attr-animation "animation")
(def attr-count     "count")

;; Enum sets
(def allowed-presets    #{"card" "list-item" "paragraph" "table-row" "profile"})
(def allowed-animations #{"pulse" "wave" "none"})
(def default-animation  "pulse")
(def default-count      1)
(def max-count          20)

(def observed-attributes
  #js [attr-preset attr-animation attr-count])

(def property-api
  {:preset    {:type 'string  :reflects-attribute attr-preset}
   :animation {:type 'string  :reflects-attribute attr-animation}
   :count     {:type 'number  :reflects-attribute attr-count}})

;; ---------------------------------------------------------------------------
;; Normalization
;; ---------------------------------------------------------------------------
(defn normalize-preset
  "Returns preset name if valid, nil otherwise."
  [s]
  (when (contains? allowed-presets s) s))

(defn normalize-animation
  "Returns animation name if valid, falls back to default."
  [s]
  (if (contains? allowed-animations s) s default-animation))

(defn parse-count
  "Parses count string to int, clamped to [1, 20]. Falls back to default."
  [s]
  (if (some? s)
    (let [n (js/parseInt s 10)]
      (if (js/isNaN n)
        default-count
        (-> n (max 1) (min max-count))))
    default-count))

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [preset-raw animation-raw count-raw]}]
  {:preset    (normalize-preset preset-raw)
   :animation (normalize-animation animation-raw)
   :count     (parse-count count-raw)})

;; ---------------------------------------------------------------------------
;; Preset definitions
;; ---------------------------------------------------------------------------
(def presets
  {"card"
   {:layout :vertical
    :gap    "12px"
    :items  [{:variant "rect"  :width "100%" :height "140px"}
             {:variant "text"  :width "80%"}
             {:variant "text"  :width "60%"}
             {:variant "text"  :width "40%"}]}

   "list-item"
   {:layout :horizontal
    :gap    "12px"
    :items  [{:variant "circle" :width "40px" :height "40px"}
             {:layout :vertical
              :gap    "8px"
              :flex   "1"
              :items  [{:variant "text" :width "70%"}
                       {:variant "text" :width "50%"}]}]}

   "paragraph"
   {:layout :vertical
    :gap    "8px"
    :items  [{:variant "text" :width "100%"}
             {:variant "text" :width "100%"}
             {:variant "text" :width "100%"}
             {:variant "text" :width "60%"}]}

   "table-row"
   {:layout :horizontal
    :gap    "16px"
    :items  [{:variant "text" :flex "1"}
             {:variant "text" :flex "1"}
             {:variant "text" :flex "1"}
             {:variant "text" :flex "1"}]}

   "profile"
   {:layout :horizontal
    :gap    "12px"
    :items  [{:variant "circle" :width "48px" :height "48px"}
             {:layout :vertical
              :gap    "8px"
              :flex   "1"
              :items  [{:variant "text" :width "120px"}
                       {:variant "text" :width "80px"}]}]}})

(def method-api nil)
