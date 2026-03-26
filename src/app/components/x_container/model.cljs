(ns app.components.x-container.model)

(def tag-name "x-container")

(def attr-as "as")
(def attr-size "size")
(def attr-padding "padding")
(def attr-center "center")
(def attr-fluid "fluid")
(def attr-label "label")

(def default-as "div")
(def default-size "lg")
(def default-padding "md")

(def allowed-as
  #{"div" "section" "article" "main" "aside" "header" "footer" "nav"})

(def allowed-sizes
  #{"xs" "sm" "md" "lg" "xl" "full"})

(def allowed-padding
  #{"none" "sm" "md" "lg"})

(def observed-attributes
  #js [attr-as attr-size attr-padding attr-center attr-fluid attr-label])

(def property-api
  {:center {:type 'boolean
            :reflects-attribute attr-center}
   :fluid {:type 'boolean
           :reflects-attribute attr-fluid}})

(def event-schema {})

(defn normalize-enum
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value))
    value
    default-value))

(defn normalize-as
  [value]
  (normalize-enum value allowed-as default-as))

(defn normalize-size
  [value]
  (normalize-enum value allowed-sizes default-size))

(defn normalize-padding
  [value]
  (normalize-enum value allowed-padding default-padding))

(defn non-empty-string?
  [value]
  (and (string? value) (not= value "")))

(defn public-state
  [{:keys [as size padding center fluid label]}]
  {:as (normalize-as as)
   :size (normalize-size size)
   :padding (normalize-padding padding)
   :center (boolean center)
   :fluid (boolean fluid)
   :label (when (non-empty-string? label) label)})
