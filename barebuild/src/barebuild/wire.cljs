(ns barebuild.wire
  (:require
   [barebuild.utils.query :as query]
   [goog.object :as gobj]
   [clojure.string :as str]))

(def ^:private outcome-accepted "accepted")
(def ^:private outcome-rejected "rejected")

;; Conversion 1 reads every member the server sent, so every member is read through one of these
;; guards. A member of the wrong kind yields nothing to read rather than throwing: a throw here
;; rejects the fetch promise, and the edge classifies a rejected fetch as a transport failure,
;; which would tell the user the server could not be reached when it answered.
(defn- object->map
  "`x` as a CLJS map when it is a JSON object, nil for anything else."
  [x]
  (when (object? x) (js->clj x)))

(defn- array->vec
  "`x` mapped through `f` when it is a JSON array, nil for anything else. An empty array maps to
  an empty vector, so a list declared empty stays distinct from one not declared at all."
  [f x]
  (when (array? x) (mapv f x)))

(defn- readable-object?
  "True when a member is absent, so there is nothing to read, or is a JSON object, so it can be."
  [x]
  (or (nil? x) (object? x)))

(defn- camel->kebab-keyword [s]
  (-> s
    (str/replace #"([a-z0-9])([A-Z])" "$1-$2")
    str/lower-case
    keyword))

(defn- replace-js-keys
  "Change js map keys (strings) to kebab-keywords"
  [m]
  (into {} (for [[k v] m] [(camel->kebab-keyword k) v])))

(defn- accepted-defect
  "The reason an accepted envelope cannot be read, or nil when it can. The query echo has to be
  an object because an unreadable one coerced to the empty query would adopt as intent and
  rewrite the address bar on the strength of a broken response. What a readable shape then
  declares is the contract's business rather than the envelope's."
  [js-obj]
  (let [shape (gobj/get js-obj "shape")]
    (cond
      (or (nil? (gobj/get js-obj "value"))
          (nil? shape))                                  :missing-accepted-members
      (not (object? shape))                              :malformed-shape
      (not (readable-object? (gobj/get js-obj "query"))) :malformed-query)))

(defn- rejected-defect
  "The reason a rejected envelope cannot be read, or nil when it can."
  [js-obj]
  (cond
    (nil? (gobj/get js-obj "error"))                   :missing-rejected-members
    (not (readable-object? (gobj/get js-obj "query"))) :malformed-query))

(defn- ->option
  "One selectable value for a field. `value` is opaque domain data and stays a string, `label` is
  what a control shows for it."
  [^js o]
  {:value (gobj/get o "value") :label (gobj/get o "label")})

(defn- ->field
  "Transforms a js field descriptor to a CLJS field map. Options that are not a list are read as
  no options at all, leaving the field bare exactly as an absent list does."
  [^js f]
  (let [options (array->vec ->option (gobj/get f "options"))]
    (cond-> {:key (gobj/get f "key") :type (keyword (gobj/get f "type"))}
      (some? (gobj/get f "required")) (assoc :required (gobj/get f "required"))
      (some? (gobj/get f "enum"))     (assoc :enum (js->clj (gobj/get f "enum")))
      options                         (assoc :options options))))

(defn- ->accepted
  "Transforms js object to accepted CLJS map. A shape that declares no list of fields yields a
  nil :fields, which the contract check reads as the missing declaration it is. An empty list
  stays an empty list, a shape that genuinely declares nothing to check."
  [js-obj]
  (let [shape (gobj/get js-obj "shape")]
    {:outcome    :accepted
     :request/id (gobj/get js-obj "requestId")
     :revision   (gobj/get js-obj "revision")
     :query      (query/canonicalize-query (object->map (gobj/get js-obj "query")))
     :value      (js->clj (gobj/get js-obj "value"))
     :page-info  (replace-js-keys (object->map (gobj/get js-obj "pageInfo")))
     :shape      {:id-key (gobj/get shape "idKey")
                  :fields (array->vec ->field (gobj/get shape "fields"))}}))

(defn- ->rejected
  "Transforms js object to error CLJS map"
  [js-obj]
  (let [error (gobj/get js-obj "error")]
    {:outcome    :rejected
     :request/id (gobj/get js-obj "requestId")
     :revision   (gobj/get js-obj "revision")
     :query      (query/canonicalize-query (object->map (gobj/get js-obj "query")))
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
        (if-let [defect (accepted-defect js-obj)]
          (protocol-failure defect {:outcome outcome})
          (->accepted js-obj))

        (= outcome outcome-rejected)
        (if-let [defect (rejected-defect js-obj)]
          (protocol-failure defect {:outcome outcome})
          (->rejected js-obj))

        :else
        (protocol-failure :unknown-outcome {:outcome outcome})))))
