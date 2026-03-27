(ns baredom.components.x-form.model)

(def tag-name "x-form")

;; Attribute name constants
(def attr-loading      "loading")
(def attr-novalidate   "novalidate")
(def attr-autocomplete "autocomplete")

;; Event name constants
(def event-submit "x-form-submit")
(def event-reset  "x-form-reset")

(def observed-attributes
  #js [attr-loading
       attr-novalidate
       attr-autocomplete])

(def allowed-autocomplete #{"on" "off"})

(defn normalize-autocomplete [raw]
  (if (contains? allowed-autocomplete raw) raw "on"))

(defn bool-attr?
  "Returns true when attribute is present and not literally \"false\"."
  [v]
  (and (some? v) (not= v "false")))

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [loading-raw novalidate-raw autocomplete-raw]}]
  {:loading?     (bool-attr? loading-raw)
   :novalidate?  (some? novalidate-raw)
   :autocomplete (normalize-autocomplete autocomplete-raw)})

(def property-api
  {:loading      {:type 'boolean :reflects-attribute attr-loading}
   :novalidate   {:type 'boolean :reflects-attribute attr-novalidate}
   :autocomplete {:type 'string  :reflects-attribute attr-autocomplete}})

(def event-schema
  {:submit {:name event-submit :cancelable true  :detail #{:values}}
   :reset  {:name event-reset  :cancelable false :detail #{}}})
