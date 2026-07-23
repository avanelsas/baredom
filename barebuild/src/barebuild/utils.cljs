(ns barebuild.utils
  (:require [clojure.string :as str]))

(defn build-scoped-url
  "Build the url from parameter set in the resource using the resource-id.
  e.g. {:sort \"start\" :direction \"asc\" -> tasks.sort=start&tasks.direction=asc"
  [search pathname resource-id new-params]
  (let [params (js/URLSearchParams. search)
        prefix (str resource-id ".")
        owned  (filterv #(str/starts-with? % prefix)
                        (js/Array.from (.keys params)))]
    (doseq [k owned]
      (.delete params k))
    (doseq [[k v] new-params]
      (.set params (str prefix (name k)) (str v)))
    (let [qs (.toString params)]
      (if (str/blank? qs) pathname (str pathname "?" qs)))))

(defn canonicalize-query
  "Public function that ensures all query keys are keywords and values are non-blank strings.
  Entries whose value is nil or blank are dropped"
  [q]
  (into {}
        (for [[k v] q
              :let  [s (str v)]
              :when (and (some? v) (not= "" s))]
          [(keyword k) s])))
