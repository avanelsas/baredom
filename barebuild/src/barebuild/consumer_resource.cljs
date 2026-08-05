(ns barebuild.consumer-resource
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]))

(def ^:private k-child         "__xConsumerChild")
(def ^:private k-last-view     "__xConsumerLastView")
(def ^:private k-submit-intent "__xConsumerSubmitIntent")
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

(defn- notify-on-change!
  "Invoke `callback` with `slice` of the new view when it differs from the same slice of the view
   this consumer was last applied with. Before the first apply there is no such view, and `slice`
   of nil is nil, which is what an unread cache always amounted to. No-op when `callback` is nil."
  [^js this callback slice view last-view]
  (when (and callback (not= (slice view) (slice last-view)))
    (callback (du/getv this k-child) (slice view) this)))

(defn- install-apply-resource!
  "Install the applyResource method a <server-resource> calls with each projected view. Reads the
   render and hook slots straight out of `register!`'s own config map, so adding a hook is a key
   here rather than a seventh positional argument.

   The hook order is part of the contract: on-failure, then render, then on-pending and
   on-writing. The data is painted before the flags describing the transition that produced it,
   so when a consumer learns a write finished, the value that write returned is already on
   screen. A component that defers work while writing relies on this to resume."
  [^js proto {:keys [render render-key on-failure on-pending on-writing]
              :or   {render-key :accepted}}]
  (.defineProperty js/Object proto "applyResource"
                   #js {:value
                        (fn apply-resource [view ctx]
                          (this-as
                           ^js this
                           (let [last-view (du/getv this k-last-view)]
                             (du/setv! this k-submit-intent (:submit-intent! ctx))
                             (du/setv! this k-submit-write (:submit-write! ctx))
                             (notify-on-change! this on-failure :failure view last-view)
                             (when (and render
                                        (or (nil? last-view)   ; nothing applied yet, so paint
                                            (not= (render-key view) (render-key last-view))))
                               (render (du/getv this k-child) view this))
                             (notify-on-change! this on-pending :pending? view last-view)
                             (notify-on-change! this on-writing :writing? view last-view)
                             (du/setv! this k-last-view view))))
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
  Defaults to the accepted envelope
  :observed-attributes — optional, defaults to #js []"
  [{:keys [tag child-tag on-connect observed-attributes] :as config}]
  (component/register!
   tag
   {:observed-attributes  (or observed-attributes #js [])
    :connected-fn         (fn [^js el]
                            (du/setv! el k-child (.querySelector el child-tag))
                            (when on-connect (on-connect el)))
    :attribute-changed-fn (fn [_el _name _old _new] nil)
    :setup-prototype-fn   (fn [^js proto]
                            (install-apply-resource! proto config))}))
