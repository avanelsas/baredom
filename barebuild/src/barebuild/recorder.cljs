(ns barebuild.recorder
  "The dev-only seam a debugger installs to watch every step, at the edge like the request
  decorator.")

(defonce ^:private hook (atom nil))

(defn install!
  "Register `f`, the one recorder for the page. Nil clears the hook, and what is not callable is
  refused rather than failing on every event."
  [f]
  (if (or (nil? f) (ifn? f))
    (reset! hook f)
    (js/console.error "[barebuild] a recorder must be a function, ignoring:" f))
  nil)

(defn record!
  "Hand one step over as `{:resource/id :el :event :before :after :effects}`, the resource either
  side of the event that drove it. `:el` is a live element rather than a value because replay is
  in-page only."
  [entry]
  (when-some [f @hook]
    (try (f entry)
         (catch :default e
           (js/console.error "[barebuild] the recorder threw:" e))))
  nil)
