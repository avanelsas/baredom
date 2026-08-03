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

(defn scope-params!
  "Replace the keys `resource-id` owns in `params` with `new-params`, each prefixed by the
  resource scope. Mutates and returns `params`."
  [^js params resource-id new-params]
  (doseq [k (owned-url-keys resource-id (js/Array.from (.keys params)))]
    (.delete params k))
  (let [prefix (url-prefix resource-id)]
    (doseq [[k v] new-params]
      (.set params (str prefix (name k)) (str v))))
  params)

(defn params->url
  "Render `params` onto `pathname`, dropping the ? when the query is empty."
  [^js params pathname]
  (let [qs (.toString params)]
    (if (str/blank? qs) pathname (str pathname "?" qs))))

(defn build-scoped-url
  "Build the url from the resource's params, scoped by its resource-id. A named resource owns
  `<id>.`-prefixed keys (e.g. tasks.sort=start); an unnamed (blank id) resource owns the bare
  keys and writes them without a prefix (sort=start)."
  [search pathname resource-id new-params]
  (-> (js/URLSearchParams. search)
      (scope-params! resource-id new-params)
      (params->url pathname)))

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

(defn- request-headers
  [transport-headers body?]
  (let [headers (cond-> (or transport-headers {})
                  body? (assoc "content-type" "application/json"))]
    (when (seq headers) headers)))

(defn request
  "build a request value for the executor from the parts provided
  .e.g. -> {:method \"GET\" :url \"/api/tasks?requestId=tasks:1&sort=owner&direction=asc\"}
  `transport` is the resource's static config."
  [{:keys [endpoint segment method query body request-id transport]}]
  (let [headers     (request-headers (:headers transport) (some? body))
        credentials (:credentials transport)
        timeout     (:timeout transport)]
    (cond-> {:method method
             :url    (str endpoint
                          (when segment
                            (str "/" (js/encodeURIComponent segment)))
                          "?requestId=" request-id (when (seq query) (str "&" (map->query-params query))))}
      body        (assoc :body body)
      headers     (assoc :headers headers)
      credentials (assoc :credentials credentials)
      timeout     (assoc :timeout timeout))))

(defn merge-request-headers
  "Merge `extra` headers into a built request, more specific headers winning over the ones already
  there, and the protocol's content-type still winning over both on a bodied request."
  [request extra]
  (let [headers (request-headers (merge (:headers request) extra) (some? (:body request)))]
    (cond-> request
      headers (assoc :headers headers))))
