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
  "The request budget the `timeout` attribute declares, read once. `:ms` is the budget in
  milliseconds, nil meaning no budget at all, and `:valid?` says whether the attribute was
  something a budget could be read from, which is what decides whether to report it. A positive
  value sets the budget, 0 removes it, and an absent or unusable attribute leaves the default in
  place, so a typo cannot silently drop it."
  [attr-value]
  (let [text (str/trim (str attr-value))
        ms   (when-not (str/blank? text) (parse-long text))]
    (cond
      (str/blank? text) {:ms default-timeout-ms :valid? true}
      (nil? ms)         {:ms default-timeout-ms :valid? false}
      (neg? ms)         {:ms default-timeout-ms :valid? false}
      (zero? ms)        {:ms nil                :valid? true}
      :else             {:ms ms                 :valid? true})))

(defn resolve-resource-id
  "The element's resource id from its `resource-id` attribute, or nil when absent or blank."
  [attr-id]
  (when-not (str/blank? attr-id) attr-id))

(def observed-attributes #js [])
