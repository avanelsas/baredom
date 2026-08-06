(ns demo.x-progress-consumer.model)

(def tag-name "x-progress-consumer")

(defn project-progress [{:keys [page total-pages]}]
  {:value page :max total-pages})
