(ns barebuild.utils.request
  "The request value `step` hands the executor on a :fetch or a :write effect. Everything past
  :url is absent rather than nil when it does not apply, so two requests that mean the same
  compare equal."
  (:require [barebuild.utils.query :as query]
            [clojure.string :as str]))

(defn- normalize-header
  "One header entry as a lowercase-keyed string pair, or nil when the key or the value is blank."
  [[k v]]
  (let [header-name (-> (str k) str/trim str/lower-case)
        value       (str v)]
    (when-not (or (str/blank? header-name) (str/blank? value))
      [header-name value])))

(defn normalize-headers
  "`m` as wire-ready headers: names lowercased and trimmed, values stringified, blank entries
  dropped. Nil when `m` is not a map or nothing survives, so absent headers are absent rather
  than empty."
  [m]
  (when (map? m)
    (let [headers (into {} (keep normalize-header) m)]
      (when (seq headers) headers))))

(defn- request-headers
  [static-headers body?]
  (let [headers (cond-> (or static-headers {})
                  body? (assoc "content-type" "application/json"))]
    (when (seq headers) headers)))

(defn request
  "The request value for the executor, e.g.
  {:request/id \"tasks:1\" :method \"GET\" :url \"/api/tasks?requestId=tasks:1&sort=owner\"}.
  `credentials` and `headers` say what it carries on the wire, `timeout` how long the executor
  may wait. The id rides the value as well as the URL, the executor naming every outcome by it."
  [{:keys [endpoint segment method query body request-id credentials headers timeout]}]
  (let [headers (request-headers headers (some? body))]
    (cond-> {:request/id request-id
             :method     method
             :url        (str endpoint
                              (when segment
                                (str "/" (js/encodeURIComponent segment)))
                              "?requestId=" request-id
                              (when (seq query) (str "&" (query/->query-string query))))}
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
