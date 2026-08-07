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

(def attr-timeout "timeout")

;; Public for test purposes only
(def default-timeout-ms 60000)

(defn parse-timeout
  "The request budget the `timeout` attribute declares. `:ms` is the budget in milliseconds, nil
  meaning no budget at all, and `:valid?` says whether a budget could be read from the attribute.
  A positive value sets the budget, 0 removes it, and an absent or unusable attribute leaves the
  default in place."
  [attr-value]
  (let [text (str/trim (str attr-value))
        ms   (when-not (str/blank? text) (parse-long text))]
    (cond
      (str/blank? text) {:ms default-timeout-ms :valid? true}
      (nil? ms)         {:ms default-timeout-ms :valid? false}
      (neg? ms)         {:ms default-timeout-ms :valid? false}
      (zero? ms)        {:ms nil                :valid? true}
      :else             {:ms ms                 :valid? true})))

(def ^:private url-unsafe-in-id #"[&=?#+%\s]")

(defn resolve-resource-id
  "The element's resource id from its `resource-id` attribute, or nil when absent, blank, or
  carrying a character that would break the query its request ids are written into."
  [attr-id]
  (when-not (or (str/blank? attr-id) (re-find url-unsafe-in-id attr-id))
    attr-id))

(defn label
  "How a resource is named in a console message, an unnamed one included."
  [resource-id]
  (or resource-id "(unnamed)"))

(def observed-attributes #js [])
