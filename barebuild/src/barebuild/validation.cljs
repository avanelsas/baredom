(ns barebuild.validation
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

(defn- valid-datestr? [date-str]
  ;; Intentionally lenient. js/Date.parse accepts more than the server's strict ISO
  ;; LocalDate/parse, so the client may pass a date the server rejects. That is ok,
  ;; the server is the authority. This local check is only for a faster UX.
  (not (js/isNaN (.parse js/Date date-str))))

(defn validate-value-type [v type]
  (or (nil? v)
      (case type
        :string (string? v)
        :date   (valid-datestr? v)
        :number (number? v)
        :url    (and (string? v) (mu/safe-url? v))
        false)))

(defn err [path code message]
  {:path path :code code :message message})


(defn- blank-or-nil? [s]
  (or (nil? s)
      (str/blank? s)))

; notice that error mapping is intentionally different for write (vs read)
(defn validate-payload [payload shape]
  (mapcat (fn [{:keys [key] :as field}]
            (let [v (get payload key)]
              (cond
                (and (:required field)
                     (blank-or-nil? v))
                [{:field (:key field)
                  :code :missing-required
                  :message (str "Required field " key " is missing.")}]

                (and (not (blank-or-nil? v))
                     (not (validate-value-type v (:type field))))
                [{:field (:key field)
                  :code :wrong-type
                  :message (str "The field " key " has the wrong type. Should be " (:type field))}]

                (and (:enum field)
                     (not (blank-or-nil? v))
                     (not (some #(= v %) (:enum field))))
                [{:field (:key field)
                  :code :not-in-enum
                  :message (str "The field " key " is not in the enum " (:enum field))}]

                :else [])))
          (:fields shape)))
