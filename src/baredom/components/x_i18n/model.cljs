(ns baredom.components.x-i18n.model
  (:require [goog.object :as gobj]
            [baredom.components.x-i18n-provider.model :as provider-model]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-i18n")

;; ── Provider tag name (re-exported for .closest lookup) ─────────────────────
(def provider-tag-name provider-model/tag-name)

;; ── Provider event constant ─────────────────────────────────────────────────
(def provider-event-change provider-model/event-change)

;; ── Attribute constants ─────────────────────────────────────────────────────
(def attr-key    "key")
(def attr-params "params")

;; ── Observed attributes ─────────────────────────────────────────────────────
(def observed-attributes #js [attr-key attr-params])

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:key    {:type 'string :reflects-attribute attr-key}
   :params {:type 'string :reflects-attribute attr-params}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema {})

;; ── Method API ──────────────────────────────────────────────────────────────
(def method-api nil)

;; ── Pure functions ──────────────────────────────────────────────────────────

(defn parse-params
  "Parse a JSON string into a ClojureScript map.
   Returns {} on nil, empty, or invalid JSON."
  [s]
  (if (and (string? s) (not= s ""))
    (try
      (let [obj (js/JSON.parse s)]
        (if (object? obj)
          (into {} (map (fn [k] [k (gobj/get obj k)])) (js/Object.keys obj))
          {}))
      (catch :default _e {}))
    {}))

(defn resolve-key
  "Walk a dot-notation key path through a JS object.
   Returns nil if any segment is missing.
   E.g. (resolve-key obj \"greeting.hello\") -> (gobj/get (gobj/get obj \"greeting\") \"hello\")"
  [^js translations key-str]
  (when (and translations (string? key-str) (not= key-str ""))
    (let [parts (.split key-str ".")]
      (loop [i 0
             ^js current translations]
        (if (or (nil? current) (>= i (.-length parts)))
          (when (and (some? current) (= i (.-length parts))) current)
          (if (object? current)
            (recur (inc i) (gobj/get current (aget parts i)))
            nil))))))

(defn interpolate
  "Replace {placeholder} patterns in a template string with values from params map.
   Missing keys are left as-is."
  [template params]
  (if (and (string? template) (seq params))
    (.replace template
              (js/RegExp. "\\{([^}]+)\\}" "g")
              (fn [match key]
                (let [v (get params key)]
                  (if (some? v) (str v) match))))
    (or template "")))

(defn resolve-translation
  "Full resolution pipeline: look up key in current translations,
   fall back to fallback translations, interpolate with params,
   fall back to the raw key string."
  [^js translations ^js fallback-translations key-str params]
  (let [raw (or (resolve-key translations key-str)
                (resolve-key fallback-translations key-str))]
    (if (string? raw)
      (interpolate raw params)
      (or key-str ""))))

(defn normalize
  "Normalize raw attribute values into a model map."
  [key-val params-raw]
  {:key    (or key-val "")
   :params (parse-params params-raw)})
