(ns barebuild.validation
  "What a payload must satisfy. Two checkers over the same declared shape: `validate-contract`
  reads an accepted envelope the server sent, `validate-payload` reads a record the client is
  about to send."
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

(defn- valid-datestr? [date-str]
  ;; Intentionally lenient. js/Date.parse accepts more than the server's strict ISO
  ;; LocalDate/parse, so the client may pass a date the server rejects. That is ok,
  ;; the server is the authority. This local check is only for a faster UX.
  (not (js/isNaN (.parse js/Date date-str))))

(defn- validate-value-type
  "True when `v` satisfies the declared `type`. A field that declares no type declares no type
  constraint."
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
  "The declaration errors in `shape`. A nil :fields is a shape that carried no field list at all,
  so there is nothing to check the rows against and the payload cannot be trusted. An empty list
  is a shape that declares nothing to check, which is a different claim and a valid one."
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
  "verifies if an accepted payload contains the right shape"
  [payload]
  (let [{:keys [shape value]} payload
        ;; use this to skip checks inside the value. No use to look further if this is firing
        value-errors (validate-value value)]
    (into (vec (validate-shape shape))
          (if (seq value-errors)
            value-errors
            (concat (validate-ids (:id-key shape) value)
                    (validate-rows (:fields shape) value))))))

(defn- field-err
  "A write-side validation error for the field named `key`."
  [key code message]
  {:field key :code code :message message})

; notice that error mapping is intentionally different for write (vs read)
;; Public for test purposes only, `conform-payload` below is the write-side entry point
(defn validate-payload
  "The write-side errors in `payload` against `shape`, as a vector, matching `validate-contract`."
  [payload shape]
  (into []
        (keep (fn [{:keys [key type required enum]}]
                (let [v     (get payload key)
                      given (not (str/blank? v))]
                  (cond
                    (and required (str/blank? v))
                    (field-err key :missing-required
                               (str "Required field " key " is missing."))

                    (and given (not (validate-value-type v type)))
                    (field-err key :wrong-type
                               (str "The field " key " has the wrong type. Should be " type))

                    (and enum given (not (some #(= v %) enum)))
                    (field-err key :not-in-enum
                               (str "The field " key " is not in the enum " enum))))))
        (:fields shape)))

;; Reading a record as its shape declares it ----------------------------------
;; A read arrives as JSON, so a field the shape calls a number is already one. A write starts in
;; a form, where every field is a string because that is what the DOM holds. The declared type is
;; the same on both sides, so the two are reconciled here rather than by every consumer that
;; builds a record.

(defn- coerce-value
  "`v` read as the declared `type`. A value that cannot be read as its type is returned exactly as
  it came, so `validate-payload` reports it as the wrong type instead of a NaN reaching the wire."
  [v type]
  (if (and (= :number type) (string? v) (not (str/blank? v)))
    (or (parse-double v) v)
    v))

(defn- coerce-record
  "`record` with every field the shape declares read as its declared type. A field the record does
  not carry stays absent rather than appearing as nil."
  [record shape]
  (reduce (fn [m {:keys [key type]}]
            (if (contains? m key)
              (update m key coerce-value type)
              m))
          record
          (:fields shape)))

(defn conform-payload
  "The write-side entry point: `record` read as `shape` declares it, with whatever still does not
  fit. Returns `{:record <conformed> :errors [...]}`, the first being what to send and the second
  what to show. Validating before coercing would report every number field as the wrong type, and
  coercing in each consumer would have each of them rediscover the types the shape already
  declares, so the order is settled here once."
  [record shape]
  (let [conformed (coerce-record record shape)]
    {:record conformed
     :errors (validate-payload conformed shape)}))
