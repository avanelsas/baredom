(ns barebuild.recorder
  "The dev-only seam a debugger installs to watch every step, at the edge like the request
  decorator."
  (:require [barebuild.hook :as hook]))

(defonce ^:private slot (atom nil))

(defn install!
  "Register `f`, the one recorder for the page."
  [f]
  (hook/install! slot "recorder" f))

(defn record!
  "Hand one step over as `{:resource/id :el :event :before :after :effects}`, the resource either
  side of the event that drove it. `:el` is a live element rather than a value because replay is
  in-page only."
  [entry]
  (when-some [f @slot]
    (try (f entry)
         (catch :default e
           (js/console.error "[barebuild] the recorder threw:" e))))
  nil)
