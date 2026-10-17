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

(def group-attr-animation "data-group-animation")

(def internal-attributes
  "The attributes that the parent x-skeleton-group writes. They are not part of the public API."
  #{group-attr-animation})

(def observed-attributes
  #js [attr-variant attr-animation attr-width attr-height group-attr-animation])

(def property-api
  {:variant   {:type 'string :reflects-attribute attr-variant :enum allowed-variants}
   :animation {:type 'string :reflects-attribute attr-animation :enum allowed-animations}
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

(defn resolve-animation
  "The animation from `own`, or else from `group`, or else the default."
  [own group]
  (if (contains? allowed-animations own) own (normalize-animation group)))

(def cycle-ms
  "The length of one cycle at the default `--x-skeleton-duration`."
  1500)

(defn phase-delay
  "The animation delay that puts an animation started at `now` in phase with the page clock."
  [now]
  (str (- (mod now cycle-ms)) "ms"))

(defn normalize-css-value [v]
  (when (and (string? v) (not= "" v)) v))

(defn normalize [{:keys [variant animation group-animation width height]}]
  {:variant   (normalize-variant variant)
   :animation (resolve-animation animation group-animation)
   :width     (normalize-css-value width)
   :height    (normalize-css-value height)})

(def event-schema {})

(def method-api {})
