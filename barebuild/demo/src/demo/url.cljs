(ns demo.url
  "The one place the demo reads another resource's URL scope, used only by the project selector.
   It belongs to PROJECTS and routes its selection to TASKS as a targeted intent, which `step`
   answers with no notify, so the address bar is its only route to what it picked. A consumer of
   TASKS reads the same selection off its own `:intent`. What reads the URL does not rewind under
   time-travel replay.")

(def ^:private tasks-project-param "tasks.project")

(defn tasks-project-id
  "The project TASKS is filtered by, or nil."
  []
  (.get (js/URLSearchParams. (.-search js/location)) tasks-project-param))
