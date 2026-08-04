(ns barebuild.utils.query
  "The query value. A map of keyword to non-blank string, the thing both the URL projection and
  the request are about. `canonicalize-query` is its normal form, so two queries that mean the
  same compare equal wherever they came from, the address bar or a server's echo.")

(defn canonicalize-query
  "Ensure all query keys are keywords and values are non-blank strings.
  Entries whose value is nil or blank are dropped."
  [q]
  (into {}
        (keep (fn [[k v]]
                (let [s (str v)]
                  (when-not (= "" s) [(keyword k) s]))))
        q))

(defn ->query-string
  "Render a query as a url query string, key-sorted so the same query always renders the same
  way regardless of how the map was built. e.g. {tasks.sort \"end\"} -> \"tasks.sort=end\""
  [m]
  (let [params (js/URLSearchParams.)]
    (doseq [[k v] (sort-by (comp name first) m)]
      (.append params (name k) (str v)))
    (.toString params)))
