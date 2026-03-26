(ns app.components.x-file-download.model)

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
  {:click {:name       event-click
           :cancelable true
           :detail     #{:href :filename}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [href-raw filename-raw disabled-present? aria-label-raw]}]
  {:href       (or href-raw "")
   :filename   (or filename-raw "")
   :disabled?  (boolean disabled-present?)
   :aria-label aria-label-raw})
