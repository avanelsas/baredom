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

(defn- map->query-params
  "Take a map of k v and turn it into a url query parameter string, key-sorted so the
  same query always renders the same URL regardless of how the map was built.
  e.g. {tasks.sort \"end\"} -> \"tasks.sort=end\""
  [m]
  (let [params (js/URLSearchParams.)]
    (doseq [[k v] (sort-by (comp name first) m)]
      (.append params (name k) (str v)))
    (.toString params)))

(defn request
  "build a request value for the executor from the parts provided
  .e.g. -> {:method \"GET\" :url \"/api/tasks?requestId=tasks:1&sort=owner&direction=asc\"}"
  [{:keys [endpoint segment method query body request-id]}]
  (cond-> {:method method
           :url    (str endpoint
                        (when segment
                          (str "/" segment))
                        "?requestId=" request-id (when (seq query) (str "&" (map->query-params query))))}
    body (assoc :body body
                :headers {"content-type" "application/json"})))
