#!/usr/bin/env bb
;; form-control-metadata.bb — Shared metadata for form-aware components.
;; Drives Vue v-model bridging, Angular ControlValueAccessor, and Svelte 5
;; $bindable() form props. Pure data; no behavior.
;;
;; Schema per component:
;;   :value-type    — TypeScript type for the form value ("boolean", "string", "number")
;;   :change-event  — DOM event fired on committed value change
;;   :detail-field  — field in event.detail that carries the new value
;;   :write-mode    — :boolean-attr (set/remove attribute) or :string-attr (setAttribute)
;;   :attr-name     — attribute name to write/remove

(def form-controls
  {"x-checkbox"       {:value-type "boolean" :change-event "x-checkbox-change"       :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-switch"         {:value-type "boolean" :change-event "x-switch-change"         :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-radio"          {:value-type "boolean" :change-event "x-radio-change"          :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-slider"         {:value-type "string"  :change-event "x-slider-change"         :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-text-area"      {:value-type "string"  :change-event "x-text-area-change"      :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-select"         {:value-type "string"  :change-event "select-change"           :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-combobox"       {:value-type "string"  :change-event "x-combobox-change"       :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-currency-field" {:value-type "string"  :change-event "x-currency-field-change" :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-tabs"           {:value-type "string"  :change-event "value-change"            :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-pagination"     {:value-type "number"  :change-event "page-change"             :detail-field "page"    :write-mode :string-attr  :attr-name "page"}})
