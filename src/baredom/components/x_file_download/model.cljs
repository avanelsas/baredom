(ns baredom.components.x-file-download.model
  (:require [baredom.utils.model :as utils]))

(def tag-name "x-file-download")

;; Attribute name constants
(def attr-href       "href")
(def attr-filename   "filename")
(def attr-disabled   "disabled")
(def attr-aria-label "aria-label")
(def attr-picker     "picker")

;; Event name constants
(def event-click   "x-file-download-click")
(def event-success "x-file-download-success")
(def event-cancel  "x-file-download-cancel")
(def event-error   "x-file-download-error")

(def phase-pick  "pick")
(def phase-fetch "fetch")
(def phase-write "write")

(def error-abort    "AbortError")
(def error-security "SecurityError")

(def observed-attributes
  #js [attr-href
       attr-filename
       attr-disabled
       attr-aria-label
       attr-picker])

(def property-api
  {:href     {:type 'string  :reflects-attribute attr-href     :default ""}
   :filename {:type 'string  :reflects-attribute attr-filename :default ""}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :picker   {:type 'boolean :reflects-attribute attr-picker}})

(def event-schema
  {event-click   {:cancelable true
                  :detail     {:href 'string :filename 'string}}
   event-success {:cancelable false
                  :detail     {:filename 'string}}
   event-cancel  {:cancelable false
                  :detail     {}}
   event-error   {:cancelable false
                  :detail     {:error 'string :phase 'string}}})

(def ^:private url-origin-re #"^(?:[A-Za-z][A-Za-z0-9+.-]*:)?//[^/?#]*")

(def ^:private failure-outcomes
  {[phase-pick error-abort]    :cancel
   [phase-pick error-security] :fallback})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [href-raw filename-raw disabled-present? aria-label-raw picker-present?]}]
  {:href       (utils/sanitize-url href-raw)
   :filename   (or filename-raw "")
   :disabled?  (boolean disabled-present?)
   :aria-label aria-label-raw
   :picker?    (boolean picker-present?)})

(defn data-url? [href]
  (and (string? href) (.startsWith ^string href "data:")))

(defn- blob-url? [href]
  (and (string? href) (.startsWith ^string href "blob:")))

(defn download-value [{:keys [href filename]}]
  (cond
    (utils/non-empty-string? filename) filename
    (data-url? href)                   ""
    :else                              nil))

(defn- last-path-segment [href]
  (-> href
      (.split #"[?#]") first
      (.replace url-origin-re "")
      (.split "/") last))

(defn- decode-segment [segment]
  (try
    (js/decodeURIComponent segment)
    (catch :default _ segment)))

(defn suggested-name [{:keys [href filename]}]
  (cond
    (utils/non-empty-string? filename)
    filename

    (and (string? href) (not (data-url? href)) (not (blob-url? href)))
    (not-empty (decode-segment (last-path-segment href)))

    :else nil))

(defn picker-options [m]
  (if-some [suggested (suggested-name m)]
    {:suggestedName suggested}
    {}))

(defn failure-outcome [{:keys [phase error]}]
  (get failure-outcomes [phase error] :error))

(defn error-name [^js err]
  (let [n (when (some? err) (.-name err))]
    (if (utils/non-empty-string? n) n "Error")))

(defn http-error [status]
  (str "HTTP " status))

(defn click-detail [{:keys [href filename]}]
  #js {:href href :filename filename})

(defn success-detail [filename]
  #js {:filename filename})

(defn cancel-detail []
  #js {})

(defn error-detail [error phase]
  #js {:error error :phase phase})

(def method-api {})
