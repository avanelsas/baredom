(ns barebuild.elements.server-resource.config
  "The request configuration the host declares as attributes, read into the keys the resource value
  carries. Knows nothing of resources, effects or the engine."
  (:require
   [barebuild.elements.server-resource.model :as model]
   [barebuild.utils.request :as request]
   [baredom.utils.dom :as du]
   [clojure.string :as str]))

(defn- json-object
  "`text` parsed as a JSON object, or nil when it does not parse or is not an object."
  [text]
  (let [parsed (try (js/JSON.parse text) (catch :default _ nil))]
    (when (object? parsed) parsed)))

(defn- parse-headers
  "The static headers the `headers` attribute declares, and whether it was a JSON object."
  [text]
  (let [obj (json-object text)]
    {:value (request/normalize-headers (js->clj obj)) :usable? (some? obj)}))

(defn- parse-credentials
  "The fetch credentials mode the `credentials` attribute declares. An unrecognised mode is not
  usable and is never passed to fetch."
  [text]
  (let [mode (model/resolve-credentials text)]
    {:value mode :usable? (some? mode)}))

(defn- parse-timeout
  "The request budget the `timeout` attribute declares. An unusable value keeps the default."
  [text]
  (let [{:keys [ms valid?]} (model/parse-timeout text)]
    {:value ms :usable? valid?}))

;; Which attribute carries each knob, which key it becomes in the resource value, how to read it,
;; and what to say when the attribute is there but cannot be read. A new knob is a row here.
(def ^:private rows
  [{:key :credentials :attr model/attr-credentials :parse parse-credentials
    :complaint "is not a fetch credentials mode, ignoring it:"}
   {:key :headers :attr model/attr-headers :parse parse-headers
    :complaint "is not a JSON object, ignoring it:"}
   {:key :timeout :attr model/attr-timeout :parse parse-timeout
    :complaint "is not a number of milliseconds, keeping the default:"}])

(defn- read-attr!
  "One configured attribute as the value it declares, nil when it declares none. A present one that
  cannot be read is reported, the row's parse deciding what it falls back to."
  [^js el {:keys [attr parse complaint]}]
  (let [text                    (du/get-attr el attr)
        {:keys [value usable?]} (parse text)]
    (when-not (or (str/blank? text) usable?)
      (js/console.error "[server-resource]" attr complaint text))
    value))

(defn read-request-config!
  "The request configuration the host declared, as the keys the resource value carries. A key is
  present only when its attribute yielded a value. `timeout` is the one whose absence means
  something: a budget of 0 drops the key, and no key is no budget."
  [^js el]
  (into {}
        (keep (fn [row]
                (when-let [value (read-attr! el row)]
                  [(:key row) value])))
        rows))
