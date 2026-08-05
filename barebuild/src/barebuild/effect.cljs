(ns barebuild.effect
  "The effect vocabulary: every value `step` may hand back for the executor to perform, and
  nothing else. Each constructor takes the parts of its effect already computed, so this
  namespace knows what an effect *is* while `resource` knows when to emit one and what to put in
  it. The executor in `elements/server-resource` performs exactly `tags` and decides nothing.")

;; Public for test purposes only
(def tags
  "Every effect a transition can return. An executor covering a different set has drifted from
  the vocabulary rather than merely lagged it, which is what pins these two together."
  #{:fetch :write :abort :url-write :route-intent :notify-consumers :diagnostic})

(defn diagnostic
  "Say something happened without changing anything, optionally carrying `detail` the executor
  prints beside the code. The executor only console.debugs it, it drives no state."
  ([code] [:diagnostic {:code code}])
  ([code detail] [:diagnostic {:code code :detail detail}]))

(defn notify-consumers
  "Project `resource` at the consumers attached to this element."
  [resource]
  [:notify-consumers {:resource resource}])

(defn fetch
  "Send the built read `request`."
  [request]
  [:fetch request])

(defn write
  "Send the built write `request`."
  [request]
  [:write request])

(defn abort
  "End the in-flight request named by `request-id`."
  [request-id]
  [:abort {:request/id request-id}])

(defn url-write
  "Project `params` onto the address bar under `resource-id`'s scope, in `mode` (:push or
  :replace)."
  [resource-id params mode]
  [:url-write {:resource/id resource-id :params params :mode mode}])

(defn route-intent
  "Hand `patch` to the resource named `target-id`, which the executor resolves to an element."
  [target-id patch]
  [:route-intent {:resource/id target-id :patch patch}])
