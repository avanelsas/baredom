(ns barebuild.elements.server-resource.model
  (:require [clojure.string :as str]))

(def tag-name "server-resource")

(def attr-src "src")

(def attr-resource-id "resource-id")

(def attr-credentials "credentials")

(def attr-headers "headers")

(def ^:private credentials-modes #{"same-origin" "include" "omit"})

(defn resolve-credentials
  [attr-value]
  (credentials-modes (str/trim (str attr-value))))

(defn- normalize-header
  "One header entry as a lowercase-keyed string pair, or nil when the key or the value is blank."
  [[k v]]
  (let [header-name (-> (str k) str/trim str/lower-case)
        value       (str v)]
    (when-not (or (str/blank? header-name) (str/blank? value))
      [header-name value])))

(defn normalize-headers
  [m]
  (when (map? m)
    (let [headers (into {} (keep normalize-header) m)]
      (when (seq headers) headers))))

(def attr-timeout "timeout")

;; Public for test purposes only
(def default-timeout-ms 60000)

(defn- timeout-ms
  [attr-value]
  (let [text (str/trim (str attr-value))]
    (when-not (str/blank? text)
      (parse-long text))))

(defn valid-timeout?
  [attr-value]
  (let [text (str/trim (str attr-value))]
    (or (str/blank? text)
        (boolean (some-> (timeout-ms text) (>= 0))))))

(defn resolve-timeout
  "The budget in milliseconds a request gets before it is abandoned. A positive attribute value
  sets it, 0 removes it, and an absent or unusable one leaves the default in place,
  so a typo cannot silently drop the budget."
  [attr-value]
  (let [ms (timeout-ms attr-value)]
    (cond
      (nil? ms)  default-timeout-ms
      (zero? ms) nil
      (pos? ms)  ms
      :else      default-timeout-ms)))

(defn resolve-resource-id
  "The element's resource id from its `resource-id` attribute, or nil when absent or blank."
  [attr-id]
  (when-not (str/blank? attr-id) attr-id))

(def observed-attributes #js [])
