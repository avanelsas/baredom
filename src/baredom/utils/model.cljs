(ns baredom.utils.model
  (:require [clojure.string :as str]))

(defn- false-text? [s]
  (= "false" (.toLowerCase (.trim (str s)))))

(defn parse-bool-present
  "An attribute that is off by default, read as HTML does: true when present, whatever its text.
   Pass the value of `getAttribute`."
  [s]
  (some? s))

(defn parse-bool-default-true
  "An attribute that is on by default: true when absent, false only for the text `\"false\"`
   in any case. Pass the value of `getAttribute`."
  [s]
  (or (nil? s) (not (false-text? s))))

(defn parse-bool-attr
  "An attribute that says true or false with no default of its own: false when absent or
   `\"false\"` in any case. For an attribute that is off by default use `parse-bool-present`."
  [s]
  (and (some? s) (not (false-text? s))))

;; ── Slotted labels ────────────────────────────────────────────────────────────
(def label-id
  "The id of the label span in the shadow root of a form control with a slotted label."
  "label")

(defn control-labelledby
  "The aria-labelledby value of a control with a slotted label, or nil.
   A host aria-labelledby wins, then a host aria-label, then the slotted label."
  [{:keys [aria-labelledby aria-label labelled?]}]
  (cond
    aria-labelledby aria-labelledby
    aria-label      nil
    labelled?       label-id
    :else           nil))

;; The attributes `control-aria` sets on a control.
(def ^:private attr-aria-checked     "aria-checked")
(def ^:private attr-aria-disabled    "aria-disabled")
(def ^:private attr-aria-required    "aria-required")
(def ^:private attr-aria-readonly    "aria-readonly")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-aria-labelledby  "aria-labelledby")
(def ^:private attr-aria-describedby "aria-describedby")

(defn- bool-text [b] (if b "true" "false"))

(defn control-aria
  "The ARIA attributes of a checkable control, as a map from name to text, or nil to remove it.
   Takes the model of an x-checkbox, an x-switch or an x-radio."
  [{:keys [aria-checked disabled? required? readonly?
           aria-label control-labelledby aria-describedby]}]
  {attr-aria-checked     aria-checked
   attr-aria-disabled    (bool-text disabled?)
   attr-aria-required    (bool-text required?)
   attr-aria-readonly    (bool-text readonly?)
   attr-aria-label       aria-label
   attr-aria-labelledby  control-labelledby
   attr-aria-describedby aria-describedby})

(defn non-empty-string?
  "True when value is a string with at least one character."
  [value]
  (and (string? value) (not= "" value)))

(defn merge-texts
  "The map `defaults`, with each non-empty text of `texts` in place of its default."
  [defaults texts]
  (merge defaults (into {} (filter (comp non-empty-string? val)) texts)))

(defn parse-positive-int
  "The positive integer an attribute says, or nil. Pass the value of `getAttribute`.
   It reads a leading integer, as HTML does."
  [s]
  (when (string? s)
    (let [n (js/parseInt s 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        n))))

;; ── Security sanitizers ──────────────────────────────────────────────────

(def ^:private svg-path-re
  "Whitelist regex for valid SVG path d-attribute characters."
  #"[^MmLlHhVvCcSsQqTtAaZz0-9\s,.\-+eE]")

(defn sanitize-svg-path-d
  "Strips characters that are not valid in an SVG path d-attribute.
   Returns nil if the result is blank."
  [s]
  (when (string? s)
    (let [clean (.trim (str/replace s svg-path-re ""))]
      (when (not= clean "") clean))))

(def ^:private allowed-protocols
  #{"http" "https" "data" "blob" "mailto" "tel"})

(def ^:private control-char-re
  "Matches ASCII control characters (tabs, newlines, etc.)."
  #"[\x00-\x1f\x7f]")

(defn safe-url?
  "Returns true when url uses an allowed protocol or is a relative URL."
  [url]
  (when (string? url)
    (let [s (.trim (str/replace url control-char-re ""))
          lower (.toLowerCase s)
          colon (.indexOf lower ":")
          slash (.indexOf lower "/")
          question (.indexOf lower "?")]
      (cond
        ;; No colon at all → relative
        (neg? colon) true
        ;; Colon appears after first / or ? → relative (e.g. /path?q=a:b)
        (and (not (neg? slash)) (< slash colon)) true
        (and (not (neg? question)) (< question colon)) true
        ;; Check protocol against whitelist
        :else (contains? allowed-protocols (.substring lower 0 colon))))))

(defn sanitize-url
  "Returns the url unchanged when `safe-url?` accepts it; otherwise the
   empty string. Note: nil and the empty string both round-trip to `\"\"`,
   so callers cannot use the return value to distinguish \"missing url\"
   from \"unsafe url\" — guard with `safe-url?` directly when that
   distinction matters."
  [url]
  (if (safe-url? url) (or url "") ""))

(defn- count-identity [counts identity]
  (update counts identity (fnil inc 0)))

(defn- key-of [identity seen]
  (str identity "|" (get seen identity 0)))

(defn unique-keys
  "A key for each of `identities`, in order: the identity as text, and how many equal ones come
   before it. Equal identities so get different keys."
  [identities]
  (mapv key-of identities (reductions count-identity {} identities)))

(defn- with-key [item item-key]
  (assoc item :key item-key))

(defn with-unique-keys
  "The maps `items`, each with a `:key` made of what `identity-of` gives for it."
  [identity-of items]
  (mapv with-key items (unique-keys (map identity-of items))))

