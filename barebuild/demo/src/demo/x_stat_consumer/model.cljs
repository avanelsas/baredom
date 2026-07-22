(ns demo.x-stat-consumer.model)

(def tag-name "x-stat-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(defn project-stat
  [accepted-response]
  (str (get-in accepted-response [:page-info :total-count])))
