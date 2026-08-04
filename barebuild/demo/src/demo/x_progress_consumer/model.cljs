(ns demo.x-progress-consumer.model)

(def tag-name "x-progress-consumer")

(def observed-attributes #js [])

(defn project-progress [{:keys [page total-pages]}]
  {:value page :max total-pages})
