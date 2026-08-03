(ns demo.x-table-consumer.model)

(def tag-name "x-table-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(defn- labeller
  "A value -> display text fn for one field. When the shape gives the field `options`, they name
   its values, so a cell shows the same text the form's control offers. An unlisted value shows
   as itself rather than blank."
  [{:keys [options]}]
  (if (seq options)
    (let [by-value (into {} (map (juxt :value :label)) options)]
      (fn [v] (get by-value v v)))
    identity))

(defn- row-cells
  "The displayed cell text per declared field."
  [fields row]
  (into {} (map (fn [{:keys [key] :as field}] [key ((labeller field) (get row key))])) fields))

(defn accepted-response->view-model
  [accepted-response]
  (let [{:keys [query shape]} accepted-response
        sort-column-name      (:sort query)
        sort-direction        (:direction query)
        {:keys [id-key fields]} shape
        columns (mapv
                 (fn [{:keys [key type]}]
                   {:key key
                    :label key
                    :type type
                    :sort-direction (if (= sort-column-name key)
                                      sort-direction
                                      "none")})
                 fields)
        rows (mapv
              (fn [row]
                {:id (get row id-key)
                 :cells (row-cells fields row)})
              (:value accepted-response))]
    {:columns columns
     :rows rows}))

(defn reconcile-plan
  "Diff current row ids against desired rows: ids to remove, and the desired order flagged new/existing."
  [old-ids new-rows]
  (let [old-set (set old-ids)
        new-set (set (map (comp str :id) new-rows))]
    {:remove (vec (remove new-set old-ids))
     :order  (mapv #(assoc % :new? (not (old-set (str (:id %))))) new-rows)}))

(defn translate-gesture
  [field-key direction]
  {:query-patch (if (= direction "none")
                  {:sort nil :direction nil}
                  {:sort field-key :direction direction})
   :gesture-class :refinement})

(defn translate-pagination-gesture
  [page]
  {:query-patch
   {:page (str page)}
   :gesture-class :navigation})
