(ns demo.url
  "The one place the demo reads another resource's URL scope, used only by the project selector.
   It belongs to PROJECTS and routes its selection to TASKS as a targeted intent, which `step`
   answers with no notify, so the address bar is its only route to what it picked. A consumer of
   TASKS reads the same selection off its own `:intent`.

   The replay dock rewinds this URL before it projects, so a selection read from here does rewind,
   but a render-key cannot notice: a key names facts its view carries, and the projects view a
   scrub hands back is the same at every position. What reads the URL therefore also listens for
   `barereplay.dock/url-changed-event`, which the dock fires whenever it moves the address bar.")

(def ^:private tasks-project-param "tasks.project")

(defn tasks-project-id
  "The project TASKS is filtered by, or nil."
  []
  (.get (js/URLSearchParams. (.-search js/location)) tasks-project-param))
