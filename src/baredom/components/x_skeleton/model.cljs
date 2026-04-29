(ns baredom.components.x-skeleton.model)

(def tag-name "x-skeleton")

(def attr-variant   "variant")
(def attr-animation "animation")
(def attr-width     "width")
(def attr-height    "height")

(def default-variant   "rect")
(def default-animation "pulse")

(def allowed-variants   #{"rect" "text" "circle"})
(def allowed-animations #{"pulse" "wave" "none"})

(def observed-attributes
  #js [attr-variant attr-animation attr-width attr-height])

(def property-api
  {:variant   {:type 'string :reflects-attribute attr-variant}
   :animation {:type 'string :reflects-attribute attr-animation}
   :width     {:type 'string :reflects-attribute attr-width}
   :height    {:type 'string :reflects-attribute attr-height}})

(defn normalize-variant [v]
  (if (and (string? v) (contains? allowed-variants v))
    v
    default-variant))

(defn normalize-animation [v]
  (if (and (string? v) (contains? allowed-animations v))
    v
    default-animation))

(defn normalize-css-value [v]
  (when (and (string? v) (not= "" v)) v))

(defn derive-state [{:keys [variant animation width height]}]
  {:variant   (normalize-variant variant)
   :animation (normalize-animation animation)
   :width     (normalize-css-value width)
   :height    (normalize-css-value height)})

(def method-api nil)
