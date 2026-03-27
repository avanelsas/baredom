(ns baredom.components.x-fieldset.model)

(def tag-name "x-fieldset")

;; Attribute name constants
(def attr-legend           "legend")
(def attr-disabled         "disabled")
(def attr-aria-label       "aria-label")
(def attr-aria-describedby "aria-describedby")

(def observed-attributes
  #js [attr-legend
       attr-disabled
       attr-aria-label
       attr-aria-describedby])

(def property-api
  {:legend   {:type 'string  :reflects-attribute attr-legend}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}})

(defn parse-bool-attr
  "Returns true if attribute is present and not equal to \"false\"."
  [s]
  (and (some? s) (not= s "false")))

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [legend-raw
           disabled-present?
           aria-label-raw
           aria-describedby-raw]}]
  (let [legend    (or legend-raw "")
        disabled? (boolean disabled-present?)]
    {:legend           legend
     :legend-visible?  (not= legend "")
     :disabled?        disabled?
     :aria-label       aria-label-raw
     :aria-describedby aria-describedby-raw}))
