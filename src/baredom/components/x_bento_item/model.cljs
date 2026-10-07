(ns baredom.components.x-bento-item.model)

(def tag-name "x-bento-item")

(def attr-col-span "col-span")
(def attr-row-span "row-span")
(def attr-order "order")

(def observed-attributes
  #js [attr-col-span
       attr-row-span
       attr-order])

(def default-span 1)

(def property-api
  {:colSpan {:type 'number :reflects-attribute attr-col-span :default default-span}
   :rowSpan {:type 'number :reflects-attribute attr-row-span :default default-span}
   :order   {:type 'number :reflects-attribute attr-order    :default 0}})
(def event-schema {})

(defn normalize-span
  "Parse span attribute to integer clamped 1–6, default 1."
  [v]
  (if (and (string? v) (not= v ""))
    (let [n (js/parseInt v 10)]
      (if (and (number? n) (not (js/isNaN n)))
        (-> n (max 1) (min 6))
        default-span))
    default-span))

(defn normalize-order
  "Parse order attribute to integer, or nil if absent/invalid."
  [v]
  (when (and (string? v) (not= v ""))
    (let [n (js/parseInt v 10)]
      (when (and (number? n) (not (js/isNaN n)))
        n))))

(defn normalize
  [{:keys [col-span row-span order]}]
  (let [cs (normalize-span col-span)
        rs (normalize-span row-span)
        ord (normalize-order order)]
    {:col-span     cs
     :row-span     rs
     :col-span-css (str "span " cs)
     :row-span-css (str "span " rs)
     :order        ord}))

(def method-api {})
