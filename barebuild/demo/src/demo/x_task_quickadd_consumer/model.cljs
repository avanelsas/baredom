(ns demo.x-task-quickadd-consumer.model)

(def tag-name "x-task-quickadd-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(defn new-task-record
  "The create record for a quick-add."
  [form-values project-id today]
  (merge form-values
         {"status"    "todo"
          "projectId" project-id
          "start"     today}))
