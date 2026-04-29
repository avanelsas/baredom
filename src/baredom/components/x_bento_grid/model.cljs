(ns baredom.components.x-bento-grid.model)

(def tag-name "x-bento-grid")

(def attr-columns "columns")
(def attr-gap "gap")
(def attr-row-gap "row-gap")
(def attr-column-gap "column-gap")
(def attr-row-height "row-height")

(def observed-attributes
  #js [attr-columns
       attr-gap
       attr-row-gap
       attr-column-gap
       attr-row-height])

(def gap-values #{"none" "xs" "sm" "md" "lg" "xl"})

(def default-columns 4)
(def default-gap "md")
(def default-row-height "auto")

(def property-api {})
(def event-schema {})

(defn gap->css [v]
  (case v
    "none" "0"
    "xs" "4px"
    "sm" "8px"
    "md" "16px"
    "lg" "24px"
    "xl" "32px"
    "16px"))

(defn normalize-gap [v]
  (if (contains? gap-values v) v default-gap))

(defn normalize-row-gap [v]
  (when (contains? gap-values v) v))

(defn normalize-column-gap [v]
  (when (contains? gap-values v) v))

(defn normalize-columns
  "Parse columns attribute to integer clamped 1–12, default 4."
  [v]
  (if (and (string? v) (not= v ""))
    (let [n (js/parseInt v 10)]
      (if (and (number? n) (not (js/isNaN n)))
        (-> n (max 1) (min 12))
        default-columns))
    default-columns))

(defn normalize-row-height
  "Return row-height as-is if non-empty, else default."
  [v]
  (if (and (string? v) (not= v ""))
    v
    default-row-height))

(defn derive-state
  [{:keys [columns gap row-gap column-gap row-height]}]
  (let [cols    (normalize-columns columns)
        gap*    (normalize-gap gap)
        rg      (or (normalize-row-gap row-gap) gap*)
        cg      (or (normalize-column-gap column-gap) gap*)
        rh      (normalize-row-height row-height)]
    {:columns  cols
     :template (str "repeat(" cols ",minmax(0,1fr))")
     :gap      gap*
     :row-gap  (gap->css rg)
     :column-gap (gap->css cg)
     :row-height rh}))

(def method-api nil)
