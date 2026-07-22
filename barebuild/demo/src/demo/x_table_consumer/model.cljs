(ns demo.x-table-consumer.model)

(def tag-name "x-table-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

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
        field-keys (map :key fields)
        rows (mapv
              (fn [row]
                {:id (get row id-key)
                 :cells (select-keys row field-keys)})
              (:value accepted-response))]
    {:columns columns
     :rows rows}))

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
