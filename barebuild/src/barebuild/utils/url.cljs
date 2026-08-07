(ns barebuild.utils.url
  "The URL projection: how a resource reads its query out of the address bar and writes it back,
  scoped so several resources can share one URL without colliding."
  (:require [barebuild.utils.query :as query]
            [clojure.string :as str]))

(defn url-prefix
  "The URL key prefix for a resource id: `<id>.` for a named resource, or \"\" for the unnamed
  root resource, whose keys are bare."
  [resource-id]
  (if (str/blank? resource-id) "" (str resource-id ".")))

(defn owned-url-keys
  "The keys in `all-keys` a resource owns: its `<id>.`-prefixed keys when named, or the bare
  undotted keys when unnamed (blank id). The two never overlap, so a root resource and named
  siblings share one URL without colliding."
  [resource-id all-keys]
  (if (str/blank? resource-id)
    (vec (remove #(str/includes? % ".") all-keys))
    (let [prefix (str resource-id ".")]
      (filterv #(str/starts-with? % prefix) all-keys))))

(defn- scope-params!
  "Replace the keys `resource-id` owns in `params` with `new-params`, each prefixed by the
  resource scope. Mutates and returns `params`."
  [^js params resource-id new-params]
  (doseq [k (owned-url-keys resource-id (js/Array.from (.keys params)))]
    (.delete params k))
  (let [prefix (url-prefix resource-id)]
    (doseq [[k v] new-params]
      (.set params (str prefix (name k)) (str v))))
  params)

(defn- params->url
  "Render `params` onto `pathname`, dropping the ? when the query is empty."
  [^js params pathname]
  (let [qs (.toString params)]
    (if (str/blank? qs) pathname (str pathname "?" qs))))

(defn build-scoped-url
  "The url built from each resource's params in `params-by-id`, scoped by its resource-id. A named
  resource owns `<id>.`-prefixed keys (e.g. tasks.sort=start), an unnamed (blank id) resource owns
  the bare keys and writes them without a prefix (sort=start). Key order follows the url it starts
  from, unlike `query/->query-string`, which sorts."
  [search pathname params-by-id]
  (-> (reduce-kv scope-params! (js/URLSearchParams. search) params-by-id)
      (params->url pathname)))

(defn parse-scoped-query
  "The query a resource owns in `search`: its `<id>.`-prefixed keys with the prefix stripped, or
  the bare undotted keys when unnamed, in the query's normal form. It reads a search string, so it
  inverts the query `build-scoped-url` writes rather than that function's return value, which
  carries the pathname too."
  [search resource-id]
  (let [params (js/URLSearchParams. search)
        prefix (url-prefix resource-id)]
    (query/canonicalize-query
     (into {}
           (map (fn [k] [(subs k (count prefix)) (.get params k)]))
           (owned-url-keys resource-id (js/Array.from (.keys params)))))))
