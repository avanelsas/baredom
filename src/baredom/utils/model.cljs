(ns baredom.utils.model
  (:require [clojure.string :as str]))

(defn parse-bool-attr
  "HTML boolean-attribute semantics: an attribute is true when it is
   present with any value other than the literal string `\"false\"`.
   The empty string and `\"0\"` both count as true. Pass the value of
   `getAttribute` (a string or `nil`); never the result of
   `hasAttribute`. For strict presence semantics use `parse-bool-present`."
  [s]
  (and (some? s) (not= s "false")))

(defn parse-bool-present
  "Strict-presence variant of `parse-bool-attr`: true when the attribute
   exists at all, regardless of value. Pass the value of `getAttribute`
   (a string or `nil`)."
  [s]
  (some? s))

(defn non-empty-string?
  "True when value is a string with at least one character."
  [value]
  (and (string? value) (not= "" value)))

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
