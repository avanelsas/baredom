(ns barebuild.utils
  (:require [clojure.string :as str]))

(defn url-prefix
  "The URL key prefix for a resource id: `<id>.` for a named resource, or \"\" for the unnamed
  root resource, whose keys are bare."
  [resource-id]
  (if (str/blank? resource-id) "" (str resource-id ".")))

(defn owned-url-keys
  "The keys in `all-keys` a resource owns: its `<id>.`-prefixed keys when named, or the bare
  undotted keys when unnamed (blank id). Bare and namespaced keys never overlap, so an unnamed
  root resource and named siblings can share one URL without colliding."
  [resource-id all-keys]
  (if (str/blank? resource-id)
    (filterv #(not (str/includes? % ".")) all-keys)
    (let [prefix (str resource-id ".")]
      (filterv #(str/starts-with? % prefix) all-keys))))

(defn build-scoped-url
  "Build the url from the resource's params, scoped by its resource-id. A named resource owns
  `<id>.`-prefixed keys (e.g. tasks.sort=start); an unnamed (blank id) resource owns the bare
  keys and writes them without a prefix (sort=start)."
  [search pathname resource-id new-params]
  (let [params (js/URLSearchParams. search)
        prefix (url-prefix resource-id)]
    (doseq [k (owned-url-keys resource-id (js/Array.from (.keys params)))]
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
        (keep (fn [[k v]]
                (let [s (str v)]
                  (when-not (= "" s) [(keyword k) s]))))
        q))

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
                          (str "/" (js/encodeURIComponent segment)))
                        "?requestId=" request-id (when (seq query) (str "&" (map->query-params query))))}
    body (assoc :body body
                :headers {"content-type" "application/json"})))
