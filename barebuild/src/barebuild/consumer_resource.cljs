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
  "Send an intent patch from a gesture handler back to a server-resource. With no `target-id`
   the patch drives the consumer's own resource. With one, it drives the named sibling resource
   through its URL projection. This is explicit, URL-mediated cross-resource coordination."
  ([^js consumer patch]
   ((du/getv consumer k-submit-intent) patch))
  ([^js consumer patch target-id]
   ((du/getv consumer k-submit-intent) patch target-id)))

(defn submit-write!
  "Send a write from a gesture handler back to the server-resource."
  [^js consumer payload]
  ((du/getv consumer k-submit-write) payload))

(defn- diff-notify!
  "Change-guard for when `new-value` differs from the value last
   cached under `field-key`. If so, invoke `callback` with the driven child and cache it. No-op when
   `callback` is nil."
  [^js this callback field-key new-value]
  (when (and callback (not= new-value (du/getv this field-key)))
    (callback (du/getv this k-child) new-value this)
    (du/setv! this field-key new-value)))

(defn- install-apply-resource! [^js proto render-key render on-failure on-pending on-writing on-apply]
  (.defineProperty js/Object proto "applyResource"
                   #js {:value
                        (fn apply-resource [resource-value ctx]
                          (this-as
                           ^js this
                           (du/setv! this k-submit-intent (:submit-intent! ctx))
                           (du/setv! this k-submit-write (:submit-write! ctx))
                           (let [{:keys [last-accepted]} resource-value]
                             (diff-notify! this on-failure k-last-failure (:last-failure resource-value))
                             (when (and render last-accepted)
                               (let [rkey [(render-key last-accepted)]]
                                 (when (not= rkey (du/getv this k-last-rendered))
                                   (render (du/getv this k-child) last-accepted this)
                                   (du/setv! this k-last-rendered rkey))))
                             (diff-notify! this on-pending k-pending (resource/pending? resource-value))
                             (diff-notify! this on-writing k-writing (resource/writing? resource-value))
                             (when on-apply
                               (on-apply (du/getv this k-child) resource-value this)))))
                        :writable true :configurable true}))

(defn register!
  "Register a resource-consumer custom element from a config:
  :tag        element tag name
  :child-tag  the driven child element, cached on connect
  :render     (fn [child accepted this]), optional, called when the render-key slice changes
  :on-failure (fn [child failure this]), optional, called when :last-failure changes,
  with failure nil on recovery so the component can clear its UI
  :on-pending (fn [child pending this]), optional, called when :pending? changes
  :on-writing (fn [child writing this]), optional, called when :writing? changes
  :on-apply   (fn [child resource-value this]), optional, called on every projection, so a consumer
  can re-derive state that does not depend on :last-accepted being
  present (e.g. an empty gate driven by the URL).
  :on-connect (fn [this]), optional extra wiring
  :render-key (fn [accepted]) -> the slice render draws, so render only fires when it changes.
  Defaults to the whole accepted value minus the per-request id
  :observed-attributes — optional, defaults to #js []"
  [{:keys [tag child-tag render on-failure on-pending on-connect on-writing on-apply observed-attributes render-key]}]
  (component/register!
   tag
   {:observed-attributes  (or observed-attributes #js [])
    :connected-fn         (fn [^js el]
                            (du/setv! el k-child (.querySelector el child-tag))
                            (when on-connect (on-connect el)))
    :attribute-changed-fn (fn [_el _name _old _new] nil)
    :setup-prototype-fn   (fn [^js proto]
                            (install-apply-resource! proto (or render-key resource/render-key)
                                                     render on-failure on-pending on-writing on-apply))}))
