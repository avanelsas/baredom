#!/usr/bin/env bb
;; form-control-metadata.bb — Shared metadata for form-aware components.
;; Drives the form bindings of the five adapters: Vue v-model, Svelte $bindable(), the Angular
;; ControlValueAccessor and the controlled mode of React and Solid. Each binding follows the
;; committed change event, so a text field updates its model on commit, not while typing.
;;
;; Schema per component, as written here:
;;   :value-type    TypeScript type for the form value ("boolean", "string", "number", "string[]")
;;   :change-event  DOM event fired on a committed value change
;;   :detail-field  field in the detail of :change-event that carries the new value
;;   :attr-name     the value attribute
;;
;; Derived from the models by `form-controls-of`:
;;   :change-request-event  the request event whose :requests names :attr-name
;;   :write-mode            :boolean-attr for a boolean, :property for "string[]" (the value setter of
;;                          x-multi-combobox writes an array in its value format), else :string-attr
;;
;; `form-controls-of` uses the functions of scripts/metadata.bb.

(def ^:private form-control-rows
  {"x-checkbox"       {:value-type "boolean"  :change-event "x-checkbox-change"       :detail-field "checked" :attr-name "checked"}
   "x-switch"         {:value-type "boolean"  :change-event "x-switch-change"         :detail-field "checked" :attr-name "checked"}
   "x-radio"          {:value-type "boolean"  :change-event "x-radio-change"          :detail-field "checked" :attr-name "checked"}
   "x-slider"         {:value-type "string"   :change-event "x-slider-change"         :detail-field "value"   :attr-name "value"}
   "x-text-area"      {:value-type "string"   :change-event "x-text-area-change"      :detail-field "value"   :attr-name "value"}
   "x-form-field"     {:value-type "string"   :change-event "x-form-field-change"     :detail-field "value"   :attr-name "value"}
   "x-search-field"   {:value-type "string"   :change-event "x-search-field-change"   :detail-field "value"   :attr-name "value"}
   "x-select"         {:value-type "string"   :change-event "select-change"           :detail-field "value"   :attr-name "value"}
   "x-combobox"       {:value-type "string"   :change-event "x-combobox-change"       :detail-field "value"   :attr-name "value"}
   "x-multi-combobox" {:value-type "string[]" :change-event "x-multi-combobox-change" :detail-field "value"   :attr-name "value"}
   "x-date-picker"    {:value-type "string"   :change-event "x-date-picker-change"    :detail-field "value"   :attr-name "value"}
   "x-currency-field" {:value-type "string"   :change-event "x-currency-field-change" :detail-field "value"   :attr-name "value"}
   "x-rating"         {:value-type "number"   :change-event "x-rating-change"         :detail-field "value"   :attr-name "value"}
   "x-tabs"           {:value-type "string"   :change-event "value-change"            :detail-field "value"   :attr-name "value"}
   "x-pagination"     {:value-type "number"   :change-event "page-change"             :detail-field "page"    :attr-name "page"}})

(defn- request-events
  "The events of `model` whose :requests name the attribute `attr`."
  [{:keys [events string-defs]} attr]
  (for [[event {:keys [requests]}] events
        :when (contains? (set (keys (event-requests requests string-defs))) attr)]
    (resolve-sym event string-defs)))

(defn- write-mode [value-type]
  (case value-type
    "boolean"  :boolean-attr
    "string[]" :property
    :string-attr))

(defn- event-named
  "The schema of the event of `model` named `event-name`, or nil."
  [{:keys [events string-defs]} event-name]
  (some (fn named [[event schema]] (when (= event-name (resolve-sym event string-defs)) schema))
        events))

(defn- row-problem
  "Why the row of `tag` does not match `model`, or nil."
  [tag model {:keys [attr-name change-event detail-field]} request-events]
  (let [change (event-named model change-event)]
    (cond
      (not= 1 (count request-events))
      (str tag " needs one request event for " attr-name ", found " (vec request-events))
      (nil? change)
      (str tag " has no event " change-event)
      (not (contains? (:detail change) (keyword detail-field)))
      (str change-event " of " tag " has no detail field " detail-field))))

(defn- with-derived
  "The row of `tag` with its derived fields. Throws when the row does not match the model."
  [models-by-tag [tag {:keys [attr-name value-type] :as row}]]
  (let [model  (models-by-tag tag)
        events (request-events model attr-name)]
    (when-some [problem (row-problem tag model row events)]
      (throw (ex-info problem {:tag tag})))
    [tag (assoc row :change-request-event (first events) :write-mode (write-mode value-type))]))

(defn form-controls-of
  "The form controls with their derived fields, from `models`, the result of `discover-models`."
  [models]
  (let [models-by-tag (into {} (map (juxt :tag-name identity)) models)]
    (into {} (map (partial with-derived models-by-tag)) form-control-rows)))
