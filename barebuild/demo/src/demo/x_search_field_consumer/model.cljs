(ns demo.x-search-field-consumer.model)

(def tag-name "x-search-field-consumer")

(defn translate-search-gesture
  [value]
  {:query-patch {:search value} :gesture-class :refinement})

(defn project-search-value
  [accepted-response]
  (get-in accepted-response [:query :search]))
