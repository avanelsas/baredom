(ns barebuild.consumer-resource
  (:require
   [barebuild.resource :as resource]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]))

(def ^:private k-child         "__xConsumerChild")
(def ^:private k-last-rendered "__xConsumerLastRendered")
(def ^:private k-last-failure  "__xConsumerLastFailure")
(def ^:private k-pending       "__xConsumerPending")
(def ^:private k-submit-intent "__xConsumerSubmitIntent")
(def ^:private k-writing       "__xConsumerWriting")
(def ^:private k-submit-write  "__xConsumerSubmitWrite")

(defn submit-intent!
  "Send an intent patch from a gesture handler back to the server-resource."
  [^js consumer patch]
  ((du/getv consumer k-submit-intent) patch))

(defn submit-write!
  "Send a write from a gesture handler back to the server-resource."
  [^js consumer payload]
  ((du/getv consumer k-submit-write) payload))

(defn- install-apply-resource! [^js proto render on-failure on-pending on-writing]
  (.defineProperty js/Object proto "applyResource"
                   #js {:value
                        (fn apply-resource [resource-value ctx]
                          (this-as
                           ^js this
                           (du/setv! this k-submit-intent (:submit-intent! ctx))
                           (du/setv! this k-submit-write (:submit-write! ctx))
                           (let [{:keys [last-accepted last-failure]} resource-value
                                 pending (resource/pending? resource-value)
                                 writing (resource/writing? resource-value)]
                             (when (and on-failure (not= last-failure (du/getv this k-last-failure)))
                               (on-failure (du/getv this k-child) last-failure this)
                               (du/setv! this k-last-failure last-failure))
                             (when (and last-accepted (not= last-accepted (du/getv this k-last-rendered)))
                               (render (du/getv this k-child) last-accepted this)
                               (du/setv! this k-last-rendered last-accepted))
                             (when (and on-pending (not= pending (du/getv this k-pending)))
                               (on-pending (du/getv this k-child) pending this)
                               (du/setv! this k-pending pending))
                             (when (and on-writing (not= writing (du/getv this k-writing)))
                               (on-writing (du/getv this k-child) writing this)
                               (du/setv! this k-writing writing)))))
                        :writable true :configurable true}))

(defn register!
  "Register a resource-consumer custom element from a config:
  :tag        element tag name
  :child-tag  the driven child element, cached on connect
  :render     (fn [child accepted this]) — called when :last-accepted changes
  :on-failure (fn [child failure this]) — optional; called when :last-failure changes,
  with failure nil on recovery so the component can clear its UI
  :on-pending (fn [child pending this]) — optional; called when :pending? changes
  :on-writing (fn [child writing this]) — optional; called when :writing? changes
  :on-connect (fn [this]) — optional extra wiring
  :observed-attributes — optional, defaults to #js []"
  [{:keys [tag child-tag render on-failure on-pending on-connect on-writing observed-attributes]}]
  (component/register!
   tag
   {:observed-attributes  (or observed-attributes #js [])
    :connected-fn         (fn [^js el]
                            (du/setv! el k-child (.querySelector el child-tag))
                            (when on-connect (on-connect el)))
    :attribute-changed-fn (fn [_el _name _old _new] nil)
    :setup-prototype-fn   (fn [^js proto] (install-apply-resource! proto render on-failure on-pending on-writing))}))
