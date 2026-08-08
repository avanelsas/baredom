(ns demo.x-project-selector-consumer.model)

(def tag-name "x-project-selector-consumer")

(def all-projects-value "")

(defn translate-project-gesture
  "The intent patch that filters tasks by `project-id`. A blank id clears the filter."
  [project-id]
  {:query-patch {:project project-id} :gesture-class :navigation})

(defn project-options
  "The {:id :name} options, from the projects value."
  [accepted-response]
  (mapv (fn [p] {:id (get p "id") :name (get p "name")})
        (:value accepted-response)))
