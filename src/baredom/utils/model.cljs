(ns baredom.utils.model)

(defn parse-bool-attr
  [s]
  (and (some? s) (not= s "false")))

(defn parse-bool-present
  [s]
  (some? s))

(defn non-empty-string?
  [value]
  (and (string? value) (not= "" value)))
