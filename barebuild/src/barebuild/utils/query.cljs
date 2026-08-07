(ns barebuild.utils.query
  "The query value. A map of keyword to non-blank string, what both the URL projection and the
  request are about. `canonicalize-query` is its normal form, so two queries that mean the same
  compare equal wherever they came from."
  (:require [clojure.string :as str]))

(defn canonicalize-query
  "`q` with every key a keyword and every value a non-blank string. Entries whose value is nil,
  empty or whitespace are dropped, so a key carrying nothing is absent rather than present and
  meaningless."
  [q]
  (into {}
        (keep (fn [[k v]]
                (let [s (str v)]
                  (when-not (str/blank? s) [(keyword k) s]))))
        q))

(defn ->query-string
  "A query as a url query string, key-sorted so the same query always renders the same way.
  e.g. {tasks.sort \"end\"} -> \"tasks.sort=end\""
  [m]
  (let [params (js/URLSearchParams.)]
    (doseq [[k v] (sort-by (comp name key) m)]
      (.append params (name k) (str v)))
    (.toString params)))
