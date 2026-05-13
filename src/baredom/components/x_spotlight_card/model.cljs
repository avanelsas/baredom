(ns baredom.components.x-spotlight-card.model)

(def tag-name "x-spotlight-card")

(def attr-variant   "variant")
(def attr-radius    "radius")
(def attr-padding   "padding")
(def attr-color     "color")
(def attr-intensity "intensity")
(def attr-size      "size")
(def attr-static    "static")

(def observed-attributes
  #js [attr-variant
       attr-radius
       attr-padding
       attr-color
       attr-intensity
       attr-size
       attr-static])

(def variant-values   #{"elevated" "bordered"})
(def radius-values    #{"none" "sm" "md" "lg" "xl"})
(def padding-values   #{"none" "sm" "md" "lg"})
(def color-values     #{"primary" "secondary" "success" "warning"
                        "danger" "info" "accent" "neutral"})
(def intensity-values #{"soft" "medium" "strong"})
(def size-values      #{"sm" "md" "lg" "xl"})

(def default-variant   "elevated")
(def default-radius    "lg")
(def default-padding   "md")
(def default-color     "primary")
(def default-intensity "medium")
(def default-size      "md")

(def ^:private intensity->opacity
  {"soft"   "0.10"
   "medium" "0.18"
   "strong" "0.28"})

(def ^:private size->px
  {"sm" "140px"
   "md" "200px"
   "lg" "280px"
   "xl" "360px"})

(def ^:private color->css
  {"primary"   "var(--x-color-primary, #2563eb)"
   "secondary" "var(--x-color-secondary, #64748b)"
   "success"   "var(--x-color-success, #16a34a)"
   "warning"   "var(--x-color-warning, #f59e0b)"
   "danger"    "var(--x-color-danger, #dc2626)"
   "info"      "var(--x-color-info, #0ea5e9)"
   "accent"    "var(--x-color-accent, #8b5cf6)"
   "neutral"   "var(--x-color-text, #111827)"})

(def property-api
  {:variant   {:type 'string  :reflects-attribute attr-variant   :default default-variant}
   :radius    {:type 'string  :reflects-attribute attr-radius    :default default-radius}
   :padding   {:type 'string  :reflects-attribute attr-padding   :default default-padding}
   :color     {:type 'string  :reflects-attribute attr-color     :default default-color}
   :intensity {:type 'string  :reflects-attribute attr-intensity :default default-intensity}
   :size      {:type 'string  :reflects-attribute attr-size      :default default-size}
   :static    {:type 'boolean :reflects-attribute attr-static}})

(def event-schema {})

(def method-api {})

(defn valid-enum
  [value allowed fallback]
  (if (contains? allowed value) value fallback))

(defn normalize-variant   [v] (valid-enum v variant-values   default-variant))
(defn normalize-radius    [v] (valid-enum v radius-values    default-radius))
(defn normalize-padding   [v] (valid-enum v padding-values   default-padding))
(defn normalize-color     [v] (valid-enum v color-values     default-color))
(defn normalize-intensity [v] (valid-enum v intensity-values default-intensity))
(defn normalize-size      [v] (valid-enum v size-values      default-size))

(defn color-css-value
  "Resolve a normalized color token to a `var(--x-color-…, fallback)` CSS string."
  [color]
  (get color->css color (get color->css default-color)))

(defn intensity-css-value
  "Resolve a normalized intensity token to a numeric opacity string."
  [intensity]
  (get intensity->opacity intensity (get intensity->opacity default-intensity)))

(defn size-css-value
  "Resolve a normalized size token to a px length string."
  [size]
  (get size->px size (get size->px default-size)))

(defn derive-state
  "Pure transform from raw attribute inputs to a complete view-model.
   `static?` collapses the explicit `static` attribute and reduced-motion preference
   so the DOM layer does not need to repeat that logic."
  [{:keys [variant radius padding color intensity size static? motion-ok?]}]
  (let [variant*   (normalize-variant   variant)
        radius*    (normalize-radius    radius)
        padding*   (normalize-padding   padding)
        color*     (normalize-color     color)
        intensity* (normalize-intensity intensity)
        size*      (normalize-size      size)
        forced-static? (or (boolean static?) (not (boolean motion-ok?)))]
    {:variant       variant*
     :radius        radius*
     :padding       padding*
     :color         color*
     :intensity     intensity*
     :size          size*
     :static?       forced-static?
     :color-css     (color-css-value color*)
     :intensity-css (intensity-css-value intensity*)
     :size-css      (size-css-value size*)}))
