(ns baredom.components.x-file-download.model
  (:require [baredom.utils.model :as utils]))

(def tag-name "x-file-download")

;; Attribute name constants
(def attr-href       "href")
(def attr-filename   "filename")
(def attr-disabled   "disabled")
(def attr-aria-label "aria-label")

;; Event name constants
(def event-click "x-file-download-click")

(def observed-attributes
  #js [attr-href
       attr-filename
       attr-disabled
       attr-aria-label])

(def property-api
  {:href     {:type 'string  :reflects-attribute attr-href}
   :filename {:type 'string  :reflects-attribute attr-filename}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}})

(def event-schema
  {event-click {:cancelable true
                :detail     {:href 'string :filename 'string}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [href-raw filename-raw disabled-present? aria-label-raw]}]
  {:href       (utils/sanitize-url href-raw)
   :filename   (or filename-raw "")
   :disabled?  (boolean disabled-present?)
   :aria-label aria-label-raw})

(defn data-url? [href]
  (and (string? href) (.startsWith ^string href "data:")))

(def method-api nil)
