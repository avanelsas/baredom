(ns barebuild.wire
  "Conversion 1, the JSON-to-CLJS edge. A server JS object read as an envelope value, or as a
  marker saying why it could not be read. Only the protocol's own closed vocabulary becomes
  keywords, opaque domain keys stay strings, and what leaves here is CLJS."
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

(defn- map-array
  "`x` mapped through `f` when it is a JSON array, nil for anything else. An empty array maps to
  an empty vector, distinct from a list not declared at all."
  [f x]
  (when (array? x) (mapv f x)))

(defn- readable-object?
  "True when a member is absent, so there is nothing to read, or is a JSON object, so it can be."
  [x]
  (or (nil? x) (object? x)))

(defn- assoc-present
  "`m` with `k` set to `v`, or `m` unchanged when the server sent nothing to set. A member that
  did not arrive stays absent rather than present and nil."
  [m k v]
  (cond-> m (some? v) (assoc k v)))

;; What makes an envelope unreadable ------------------------------------------

(defn- head-defect
  "The reason the members every envelope carries cannot be read, or nil when they can."
  [^js js-obj]
  (when-not (readable-object? (gobj/get js-obj "query")) :malformed-query))

(defn- accepted-defect
  "The reason an accepted envelope cannot be read, or nil when it can. What a readable shape then
  declares is the contract's business, not the envelope's."
  [^js js-obj]
  (let [shape (gobj/get js-obj "shape")]
    (cond
      (nil? (gobj/get js-obj "value")) :missing-value
      (nil? shape)                     :missing-shape
      (not (object? shape))            :malformed-shape)))

(defn- rejected-defect
  "The reason a rejected envelope cannot be read, or nil when it can."
  [^js js-obj]
  (when (nil? (gobj/get js-obj "error")) :missing-error))

;; Reading a readable envelope ------------------------------------------------

(defn- ->option
  "One selectable value for a field. `value` stays an opaque string, `label` is what a control
  shows for it."
  [^js option]
  {:value (gobj/get option "value") :label (gobj/get option "label")})

(defn- ->field
  "A js field descriptor as a CLJS field map. Options that are not a list read as no options at
  all, leaving the field bare exactly as an absent list does."
  [^js field]
  (-> {:key (gobj/get field "key") :type (keyword (gobj/get field "type"))}
    (assoc-present :required (gobj/get field "required"))
    (assoc-present :enum (js->clj (gobj/get field "enum")))
    (assoc-present :options (map-array ->option (gobj/get field "options")))))

(def ^:private page-info-members
  "The page-info members the protocol declares, by the name each arrives under. Read by name like
  every other protocol member, so what a consumer may expect is written down here."
  {"page"       :page
   "pageSize"   :page-size
   "totalPages" :total-pages
   "totalCount" :total-count})

(defn- ->page-info
  "A js page-info member as a CLJS map, carrying each declared member the server sent. Anything
  that is not an object is no page info at all."
  [^js page-info]
  (when (object? page-info)
    (reduce-kv (fn [m member k] (assoc-present m k (gobj/get page-info member)))
               {}
               page-info-members)))

(defn- envelope-head
  "The members every envelope carries whatever its outcome."
  [^js js-obj]
  {:request/id (gobj/get js-obj "requestId")
   :revision   (gobj/get js-obj "revision")
   :query      (query/canonicalize-query (object->map (gobj/get js-obj "query")))})

(defn- ->accepted
  "A js accepted envelope as a CLJS map. A shape declaring no field list yields a nil :fields,
  which the contract check reads as the missing declaration it is. An empty list stays an empty
  list."
  [^js js-obj]
  (let [shape (gobj/get js-obj "shape")]
    (assoc (envelope-head js-obj)
           :value     (js->clj (gobj/get js-obj "value"))
           :page-info (->page-info (gobj/get js-obj "pageInfo"))
           :shape     {:id-key (gobj/get shape "idKey")
                       :fields (map-array ->field (gobj/get shape "fields"))})))

(defn- ->rejected
  "A js rejected envelope as a CLJS map."
  [^js js-obj]
  (let [error (gobj/get js-obj "error")]
    (assoc (envelope-head js-obj)
           :error {:code    (keyword (gobj/get error "code"))
                   :message (gobj/get error "message")
                   :details (js->clj (gobj/get error "details"))})))

;; The envelope vocabulary ----------------------------------------------------

(defn- protocol-failure
  "A protocol-failure marker naming `reason`. The reason is merged last, so `extra` cannot
  redefine it."
  ([reason] (protocol-failure reason nil))
  ([reason extra] {:protocol-failure (merge extra {:reason reason})}))

(def ^:private envelope-kinds
  "Each outcome the protocol defines, as data: the keyword it reads as, what makes one unreadable,
  and how a readable one is read."
  {"accepted" {:reads-as :accepted :defect accepted-defect :parse ->accepted}
   "rejected" {:reads-as :rejected :defect rejected-defect :parse ->rejected}})

(defn parse-envelope
  "A server JS object as an accepted or rejected envelope, or a protocol-failure marker when it
  cannot be read."
  [js-obj]
  (if-not (object? js-obj)
    (protocol-failure :malformed-envelope)
    (let [outcome (gobj/get js-obj "outcome")]
      (if-let [{:keys [reads-as defect parse]} (envelope-kinds outcome)]
        (if-let [reason (or (head-defect js-obj) (defect js-obj))]
          (protocol-failure reason {:outcome outcome})
          (assoc (parse js-obj) :outcome reads-as))
        (protocol-failure :unknown-outcome {:outcome outcome})))))

(defn parse-body
  "Response or embed text as an envelope. The whole of the JSON-to-CLJS edge, shared by the network
  edge and the boot embed so both read a body the same way."
  [text]
  (if (str/blank? text)
    (protocol-failure :empty-body)
    (let [parsed (try (js/JSON.parse text) (catch :default _ ::unreadable))]
      (if (= ::unreadable parsed)
        (protocol-failure :malformed-json)
        (parse-envelope parsed)))))
