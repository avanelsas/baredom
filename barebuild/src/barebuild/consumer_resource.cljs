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

(defn- default-render-key
  "The slice a consumer paints from when it names none: the accepted envelope minus the
   per-request ids, so a refetch that returns unchanged data does not re-render."
  [view]
  (resource/render-key (:accepted view)))

(defn- install-apply-resource!
  "Install the applyResource method a <server-resource> calls with each projected view.

   The hook order is part of the contract: on-failure, then render, then on-pending and
   on-writing. The data is painted before the flags describing the transition that produced it,
   so when a consumer learns a write finished, the value that write returned is already on
   screen. A component that defers work while writing relies on this to resume."
  [^js proto render-key render on-failure on-pending on-writing]
  (.defineProperty js/Object proto "applyResource"
                   #js {:value
                        (fn apply-resource [view ctx]
                          (this-as
                           ^js this
                           (du/setv! this k-submit-intent (:submit-intent! ctx))
                           (du/setv! this k-submit-write (:submit-write! ctx))
                           (diff-notify! this on-failure k-last-failure (:failure view))
                           (when render
                             (let [rkey [(render-key view)]]
                               (when (not= rkey (du/getv this k-last-rendered))
                                 (render (du/getv this k-child) view this)
                                 (du/setv! this k-last-rendered rkey))))
                           (diff-notify! this on-pending k-pending (:pending? view))
                           (diff-notify! this on-writing k-writing (:writing? view))))
                        :writable true :configurable true}))

(defn register!
  "Register a resource-consumer custom element from a config:
  :tag        element tag name
  :child-tag  the driven child element, cached on connect
  :render     (fn [child view this]), optional, called when the render-key slice changes.
  `view` is the whole of what a consumer may read: {:accepted :failure :intent
  :pending? :writing?}.
  :on-failure (fn [child failure this]), optional, called when :failure changes,
  with failure nil on recovery so the component can clear its UI
  :on-pending (fn [child pending this]), optional, called when :pending? changes
  :on-writing (fn [child writing this]), optional, called when :writing? changes
  :on-connect (fn [this]), optional extra wiring
  :render-key (fn [view]) -> the slice render draws, so render only fires when it changes.
  Defaults to the accepted value minus the per-request ids
  :observed-attributes — optional, defaults to #js []"
  [{:keys [tag child-tag render on-failure on-pending on-connect on-writing observed-attributes render-key]}]
  (component/register!
   tag
   {:observed-attributes  (or observed-attributes #js [])
    :connected-fn         (fn [^js el]
                            (du/setv! el k-child (.querySelector el child-tag))
                            (when on-connect (on-connect el)))
    :attribute-changed-fn (fn [_el _name _old _new] nil)
    :setup-prototype-fn   (fn [^js proto]
                            (install-apply-resource! proto (or render-key default-render-key)
                                                     render on-failure on-pending on-writing))}))
