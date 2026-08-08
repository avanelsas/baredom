(ns demo.task-edit
  "The one message the demo's consumers send each other: the table asking the task form to open on
   a row. It rides a DOM event on the shared <server-resource>, the nearest element both sides can
   name. Spelled once here so neither side can misspell it."
  (:require
   [baredom.utils.dom :as du]))

(def ^:private event-name "x-task-edit-request")
(def ^:private resource-tag "server-resource")

(defn- resource-of [^js el]
  (.closest el resource-tag))

(defn request!
  "Ask for the row `id` to be edited."
  [^js from id]
  (du/dispatch! (resource-of from) event-name #js {:id id}))

(defn on-request!
  "Call `handler` with the requested row id."
  [^js el handler]
  (.addEventListener (resource-of el) event-name
                     (fn [^js e] (handler (.. e -detail -id)))))
