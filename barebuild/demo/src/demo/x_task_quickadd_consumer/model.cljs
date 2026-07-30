(ns demo.x-task-quickadd-consumer.model)

(def tag-name "x-task-quickadd-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

(defn new-task-record
  "The complete create record for a quick-add. The typed title and name join the fields the
  small form does not surface: the name is the task's owner (shown as the card's person),
  status is always todo since new cards start there, projectId scopes the card to the board
  being viewed, and start defaults to today."
  [form-values project-id today]
  (merge form-values
         {"status"    "todo"
          "projectId" project-id
          "start"     today}))
