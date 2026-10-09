(ns baredom.components.x-grid.model)

(def tag-name "x-grid")

(def attr-columns "columns")
(def attr-min-column-size "min-column-size")
(def attr-gap "gap")
(def attr-row-gap "row-gap")
(def attr-column-gap "column-gap")
(def attr-align-items "align-items")
(def attr-justify-items "justify-items")
(def attr-auto-flow "auto-flow")
(def attr-inline "inline")

(def observed-attributes
  #js [attr-columns
       attr-min-column-size
       attr-gap
       attr-row-gap
       attr-column-gap
       attr-align-items
       attr-justify-items
       attr-auto-flow
       attr-inline])

(def gap-values #{"none" "xs" "sm" "md" "lg" "xl"})
(def align-values #{"start" "center" "end" "stretch"})
(def flow-values #{"row" "column" "dense" "row-dense" "column-dense"})

(def default-gap "md")
(def default-min-column-size "16rem")
(def default-align "stretch")
(def default-flow "row")

(def property-api
  {:columns       {:type 'string  :reflects-attribute attr-columns         :default ""}
   :minColumnSize {:type 'string  :reflects-attribute attr-min-column-size
                   :default default-min-column-size}
   :gap           {:type 'string  :reflects-attribute attr-gap             :default default-gap :enum gap-values}
   :rowGap        {:type 'string  :reflects-attribute attr-row-gap         :default "" :enum gap-values}
   :columnGap     {:type 'string  :reflects-attribute attr-column-gap      :default "" :enum gap-values}
   :alignItems    {:type 'string  :reflects-attribute attr-align-items     :default default-align :enum align-values}
   :justifyItems  {:type 'string  :reflects-attribute attr-justify-items   :default default-align :enum align-values}
   :autoFlow      {:type 'string  :reflects-attribute attr-auto-flow       :default default-flow :enum flow-values}
   :inline        {:type 'boolean :reflects-attribute attr-inline}})
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

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-gap [v] (valid-enum v gap-values default-gap))
(defn normalize-row-gap [v] (when (contains? gap-values v) v))
(defn normalize-column-gap [v] (when (contains? gap-values v) v))

(defn normalize-align [v]
  (valid-enum v align-values default-align))

(defn normalize-flow [v]
  (valid-enum v flow-values default-flow))

(defn flow->css [v]
  (case v
    "row-dense" "row dense"
    "column-dense" "column dense"
    v))

(defn normalize-columns [v]
  (when (and v (not= "" v)) v))

(defn normalize-min-size [v]
  (if (and v (not= "" v)) v default-min-column-size))

(defn normalize
  [{:keys [columns min-column-size gap row-gap column-gap
           align-items justify-items auto-flow inline]}]

  (let [columns* (normalize-columns columns)
        min* (normalize-min-size min-column-size)
        gap* (normalize-gap gap)
        row-gap* (or (normalize-row-gap row-gap) gap*)
        col-gap* (or (normalize-column-gap column-gap) gap*)
        align* (normalize-align align-items)
        justify* (normalize-align justify-items)
        flow* (flow->css (normalize-flow auto-flow))
        template (if columns*
                   columns*
                   (str "repeat(auto-fit,minmax(" min* ",1fr))"))]

    {:columns template
     :gap gap*
     :row-gap-token row-gap*
     :column-gap-token col-gap*
     :row-gap (gap->css row-gap*)
     :column-gap (gap->css col-gap*)
     :align-items align*
     :justify-items justify*
     :auto-flow flow*
     :inline (boolean inline)}))

(def method-api {})
