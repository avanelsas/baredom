(ns read-demo.x-progress-consumer.model)

(def tag-name "x-progress-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(defn project-progress [{:keys [page total-pages]}]
  {:value page :max total-pages})
