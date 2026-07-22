(ns barebuild.wire
  (:require
   [barebuild.utils :as utils]
   [goog.object :as gobj]
   [clojure.string :as str]))

(def outcome-accepted "accepted")
(def outcome-rejected "rejected")

(defn- camel->kebab-keyword [s]
  (-> s
    (str/replace #"([a-z0-9])([A-Z])" "$1-$2")
    str/lower-case
    keyword))

(defn- replace-js-keys
  "Change js map keys (strings) to kebab-keywords"
  [m]
  (into {} (for [[k v] m] [(camel->kebab-keyword k) v])))

(defn- accepted-structure-correct?
  "Check if outcome has a value and a shape"
  [js-obj]
  (and (some? (gobj/get js-obj "value"))
       (some? (gobj/get js-obj "shape"))))

(defn- rejected-structure-correct?
  "Check to see if error has a value"
  [js-obj]
  (some? (gobj/get js-obj "error")))

(defn- ->accepted
  "Transforms js object to accepted CLJS map"
  [js-obj]
  (let [shape (gobj/get js-obj "shape")]
    {:outcome    :accepted
     :request/id (gobj/get js-obj "requestId")
     :revision   (gobj/get js-obj "revision")
     :query      (utils/canonicalize-query (js->clj (gobj/get js-obj "query")))
     :value      (js->clj (gobj/get js-obj "value"))
     :page-info  (replace-js-keys (js->clj (gobj/get js-obj "pageInfo")))
     :shape      {:id-key (gobj/get shape "idKey")
                  :fields (mapv (fn [f]
                                  (cond-> {:key (gobj/get f "key") :type (keyword (gobj/get f "type"))}
                                    (some? (gobj/get f "required")) (assoc :required (gobj/get f "required"))
                                    (some? (gobj/get f "enum"))     (assoc :enum (js->clj (gobj/get f "enum")))))
                                (gobj/get shape "fields"))}}))

(defn- ->rejected
  "Transforms js object to error CLJS map"
  [js-obj]
  (let [error (gobj/get js-obj "error")]
    {:outcome    :rejected
     :request/id (gobj/get js-obj "requestId")
     :revision   (gobj/get js-obj "revision")
     :query      (utils/canonicalize-query (js->clj (gobj/get js-obj "query")))
     :error      {:code    (keyword (gobj/get error "code"))
                  :message (gobj/get error "message")
                  :details (js->clj (gobj/get error "details"))}}))

(defn- protocol-failure
  "Return protocol failure map"
  [reason extra]
  {:protocol-failure (merge {:reason reason} extra)})

(defn parse-envelope
  "Parses a server JS object to a CLJS map
  Returns protocol failures if something is amiss"
  [js-obj]
  (if (nil? js-obj)
    (protocol-failure :empty-body {})
    (let [outcome (gobj/get js-obj "outcome")]
      (cond
        (= outcome outcome-accepted)
        (if (accepted-structure-correct? js-obj)
          (->accepted js-obj)
          (protocol-failure :missing-accepted-members {:outcome outcome}))

        (= outcome outcome-rejected)
        (if (rejected-structure-correct? js-obj)
          (->rejected js-obj)
          (protocol-failure :missing-rejected-members {:outcome outcome}))

        :else
        (protocol-failure :unknown-outcome {:outcome outcome})))))

;; Write

(defn- ->write-accepted
  "Transforms write-ack js object to accepted CLJS map"
  [js-obj]
  {:outcome    :accepted
   :request/id (gobj/get js-obj "requestId")
   :revision   (gobj/get js-obj "revision")})

(defn- accepted-write-structure-correct?
  "Check if outcome has a revision and requestId"
  [js-obj]
  (and (some? (gobj/get js-obj "revision"))
       (some? (gobj/get js-obj "requestId"))))

(defn parse-ack
  "Parses a server JS acknowledgement object to a CLJS map
  Returns protocol failures if something is amiss"
  [js-obj]
  (if (nil? js-obj)
    (protocol-failure :empty-body {})
    (let [outcome (gobj/get js-obj "outcome")]
      (cond
        (= outcome outcome-accepted)
        (if (accepted-write-structure-correct? js-obj)
          (->write-accepted js-obj)
          (protocol-failure :missing-accepted-members {:outcome outcome}))

        (= outcome outcome-rejected)
        (if (rejected-structure-correct? js-obj)
          (->rejected js-obj) ; same structure as read
          (protocol-failure :missing-rejected-members {:outcome outcome}))

        :else
        (protocol-failure :unknown-outcome {:outcome outcome})))))
