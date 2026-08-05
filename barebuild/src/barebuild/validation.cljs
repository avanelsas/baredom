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
