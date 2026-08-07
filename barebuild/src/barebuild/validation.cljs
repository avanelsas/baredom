(ns barebuild.validation
  "What a payload must satisfy, and how a record is read into its declared types. Two checkers
  over the same declared shape: `validate-contract` for an accepted envelope the server sent,
  `validate-payload` for a record the client is about to send."
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

(defn- valid-datestr? [date-str]
  ;; Intentionally lenient. js/Date.parse accepts more than the server's strict ISO
  ;; LocalDate/parse, so the client may pass a date the server rejects. The server is the
  ;; authority, this check is only for a faster UX.
  (not (js/isNaN (.parse js/Date date-str))))

(defn- validate-value-type
  "True when `v` satisfies the declared `type`. A field that declares no type declares no type
  constraint, and a declared field is nullable, so a null passes every type."
  [v type]
  (or (nil? v)
      (nil? type)
      (case type
        :string (string? v)
        :date   (valid-datestr? v)
        :number (number? v)
        :url    (and (string? v) (mu/safe-url? v))
        false)))

;; contract validation

(defn- path-err
  "A read-side contract error, located by its `path` into the payload."
  [path code message]
  {:path path :code code :message message})

(defn- validate-shape
  "The declaration errors in `shape`. A nil :fields carried no field list at all, so there is
  nothing to check the rows against. An empty list declares nothing to check, a different claim
  and a valid one."
  [shape]
  (cond-> []
    (not (:id-key shape)) (conj (path-err [:shape :id-key] :missing-id-key "shape is missing :id-key"))
    (not (:fields shape)) (conj (path-err [:shape :fields] :missing-fields "shape is missing :fields"))))

(defn- validate-value [v]
  (cond
    (not (sequential? v)) [(path-err [:value] :not-a-list "value is not a list")]
    (not (every? map? v)) [(path-err [:value] :not-maps "value is not a list of maps")]
    :else []))

(defn- validate-ids [id-key value]
  (let [ids (map #(get % id-key) value)]
    (cond-> []
      (some nil? ids)
      (conj (path-err [:value] :missing-id (str "some rows are missing \"" id-key "\"")))
      (not= (count ids) (count (distinct ids)))
      (conj (path-err [:value] :duplicate-id "row ids are not unique")))))

(defn- validate-row [row-idx row fields]
  (keep (fn [{:keys [key type]}]
          (cond
            (not (contains? row key))
            (path-err [:value row-idx key] :missing-field
                      (str "row " row-idx " is missing field \"" key "\""))

            (not (validate-value-type (get row key) type))
            (path-err [:value row-idx key] :wrong-type
                      (str "row " row-idx " field \"" key "\" is not a " (name type)))))
        fields))

(defn- validate-rows [fields value]
  (into [] (comp (map-indexed (fn [idx row] (validate-row idx row fields))) cat) value))

(defn validate-contract
  "The contract errors in an accepted `payload`, as a vector."
  [payload]
  (let [{:keys [shape value]} payload
        ;; a broken value stops the row checks, there is nothing further to look into
        value-errors (validate-value value)]
    (into (validate-shape shape)
          (if (seq value-errors)
            value-errors
            (concat (validate-ids (:id-key shape) value)
                    (validate-rows (:fields shape) value))))))

(defn- field-err
  "A write-side validation error for the field named `key`."
  [key code message]
  {:field key :code code :message message})

(defn- given?
  "True when the author supplied a value. An empty form field reaches here as a blank string, and
  anything else is a value, a coerced zero included."
  [v]
  (if (string? v)
    (not (str/blank? v))
    (some? v)))

;; Read errors are diagnostics located by a path into the payload, write errors are user-facing
;; and name a form field.
;; Public for test purposes only, `conform-payload` below is the write-side entry point
(defn validate-payload
  "The write-side errors in `payload` against `shape`, as a vector, matching `validate-contract`.
  Runs after `coerce-record`, so a value here is already of its declared type."
  [payload shape]
  (into []
        (keep (fn [{:keys [key type required enum]}]
                (let [v     (get payload key)
                      given (given? v)]
                  (cond
                    (and required (not given))
                    (field-err key :missing-required
                               (str "Required field " key " is missing."))

                    (and given (not (validate-value-type v type)))
                    (field-err key :wrong-type
                               (str "The field " key " has the wrong type. Should be "
                                    (name type)))

                    (and enum given (not (contains? (set enum) v)))
                    (field-err key :not-in-enum
                               (str "The field " key " is not in the enum " enum))))))
        (:fields shape)))

;; Reading a record as its shape declares it ----------------------------------
;; A read arrives as JSON with its types intact. A write starts in a form, where every field is a
;; string because that is what the DOM holds. Reconciled here rather than in every consumer that
;; builds a record.

(defn- coerce-value
  "`v` read as the declared `type`. A value that cannot be read as its type comes back exactly as
  it came, for `validate-payload` to report as the wrong type."
  [v type]
  (if (and (= :number type) (string? v) (not (str/blank? v)))
    (or (parse-double v) v)
    v))

(defn- coerce-record
  "`record` with every field the shape declares read as its declared type. A field the record does
  not carry stays absent."
  [record shape]
  (reduce (fn [m {:keys [key type]}]
            (if (contains? m key)
              (update m key coerce-value type)
              m))
          record
          (:fields shape)))

(defn conform-payload
  "The write-side entry point. Returns {:record <`record` read as `shape` declares it> :errors
  [...]}, the first being what to send and the second what to show. Coercing precedes validating,
  so a number field arriving from a form as a string is not reported as the wrong type."
  [record shape]
  (let [conformed (coerce-record record shape)]
    {:record conformed
     :errors (validate-payload conformed shape)}))
