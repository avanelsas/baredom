(ns barebuild.wire
  (:require
   [barebuild.utils.query :as query]
   [goog.object :as gobj]
   [clojure.string :as str]))

;; Conversion 1 reads every member the server sent through one of these guards. A member of the
;; wrong kind yields nothing to read rather than throwing, since the edge classifies a rejected
;; fetch as a transport failure and would report a server that answered as one that could not be
;; reached.
(defn- object->map
  "`x` as a CLJS map when it is a JSON object, nil for anything else."
  [x]
  (when (object? x) (js->clj x)))

(defn- array->vec
  "`x` mapped through `f` when it is a JSON array, nil for anything else. An empty array maps to
  an empty vector, distinct from a list not declared at all."
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

(defn- accepted-defect
  "The reason an accepted envelope cannot be read, or nil when it can. What a readable shape then
  declares is the contract's business, not the envelope's."
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
  "One selectable value for a field. `value` stays an opaque string, `label` is what a control
  shows for it."
  [^js o]
  {:value (gobj/get o "value") :label (gobj/get o "label")})

(defn- ->field
  "A js field descriptor as a CLJS field map. Options that are not a list read as no options at
  all, leaving the field bare exactly as an absent list does."
  [^js f]
  (let [options (array->vec ->option (gobj/get f "options"))]
    (cond-> {:key (gobj/get f "key") :type (keyword (gobj/get f "type"))}
      (some? (gobj/get f "required")) (assoc :required (gobj/get f "required"))
      (some? (gobj/get f "enum"))     (assoc :enum (js->clj (gobj/get f "enum")))
      options                         (assoc :options options))))

(defn- envelope-head
  "The members every envelope carries whatever its outcome."
  [js-obj outcome]
  {:outcome    outcome
   :request/id (gobj/get js-obj "requestId")
   :revision   (gobj/get js-obj "revision")
   :query      (query/canonicalize-query (object->map (gobj/get js-obj "query")))})

(defn- ->accepted
  "A js accepted envelope as a CLJS map. A shape declaring no field list yields a nil :fields,
  which the contract check reads as the missing declaration it is. An empty list stays an empty
  list. Page info follows the same rule."
  [js-obj]
  (let [shape (gobj/get js-obj "shape")]
    (assoc (envelope-head js-obj :accepted)
           :value     (js->clj (gobj/get js-obj "value"))
           :page-info (some-> (object->map (gobj/get js-obj "pageInfo"))
                        (update-keys camel->kebab-keyword))
           :shape     {:id-key (gobj/get shape "idKey")
                       :fields (array->vec ->field (gobj/get shape "fields"))})))

(defn- ->rejected
  "A js rejected envelope as a CLJS map."
  [js-obj]
  (let [error (gobj/get js-obj "error")]
    (assoc (envelope-head js-obj :rejected)
           :error {:code    (keyword (gobj/get error "code"))
                   :message (gobj/get error "message")
                   :details (js->clj (gobj/get error "details"))})))

(defn- protocol-failure
  "A protocol-failure marker naming `reason`."
  [reason extra]
  {:protocol-failure (merge {:reason reason} extra)})

;; The envelope vocabulary as data: each outcome the protocol defines, what makes one unreadable,
;; and how a readable one is read. An outcome not in the table is one this client does not speak.
(def ^:private envelope-kinds
  {"accepted" {:defect accepted-defect :parse ->accepted}
   "rejected" {:defect rejected-defect :parse ->rejected}})

(defn parse-envelope
  "A server JS object as an accepted or rejected envelope, or a protocol-failure marker when it
  cannot be read. Anything that is not a JSON object carries no outcome to read and so is not an
  envelope at all, rather than one whose outcome happens to be missing."
  [js-obj]
  (if-not (object? js-obj)
    (protocol-failure :malformed-envelope {})
    (let [outcome (gobj/get js-obj "outcome")]
      (if-let [{:keys [defect parse]} (envelope-kinds outcome)]
        (if-let [reason (defect js-obj)]
          (protocol-failure reason {:outcome outcome})
          (parse js-obj))
        (protocol-failure :unknown-outcome {:outcome outcome})))))

(defn parse-body
  "Response or embed text as an envelope. The whole of the JSON-to-CLJS edge, shared by the network
  edge and the boot embed so both read a body the same way. A body that is not there, one that is
  not JSON, and one that is JSON but not an envelope are three different mistakes and are reported
  as three."
  [text]
  (if (str/blank? text)
    (protocol-failure :empty-body {})
    (if-let [parsed (try (js/JSON.parse text) (catch :default _ nil))]
      (parse-envelope parsed)
      (protocol-failure :malformed-json {}))))
