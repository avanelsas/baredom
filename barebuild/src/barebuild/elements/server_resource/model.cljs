(ns barebuild.elements.server-resource.model
  (:require [clojure.string :as str]))

(def tag-name "server-resource")

(def attr-src "src")

(def attr-resource-id "resource-id")

(defn resolve-resource-id
  "The element's resource id: its `resource-id` attribute value, or \"tasks\" when absent or blank."
  [attr-id]
  (if (str/blank? attr-id) "tasks" attr-id))

(defn targets-sibling?
  "True when an intent names a resource other than its own — cross-resource coordination. A nil
   target (or one equal to the own id) drives the resource itself."
  [own-id target-id]
  (boolean (and target-id (not= target-id own-id))))

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})
