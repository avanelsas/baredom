(ns barebuild.consumer-resource
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]))

(def ^:private k-child     "__xConsumerChild")
(def ^:private k-last-view "__xConsumerLastView")
(def ^:private k-ctx       "__xConsumerResourceCtx")

(defn submit-intent!
  "Send an intent patch from a gesture handler back to a server-resource. With no `target-id` the
   patch drives the consumer's own resource, with one it drives the named sibling through its URL
   projection."
  ([^js consumer patch]
   ((:submit-intent! (du/getv consumer k-ctx)) patch))
  ([^js consumer patch target-id]
   ((:submit-intent! (du/getv consumer k-ctx)) patch target-id)))

(defn submit-write!
  "Send a write from a gesture handler back to the server-resource."
  [^js consumer payload]
  ((:submit-write! (du/getv consumer k-ctx)) payload))

(defn- install-ctx!
  "Cache the ctx this consumer submits through. A server-resource holds one for its whole life,
   so this writes only when a different one arrives, which is what a consumer moved under another
   server-resource is handed."
  [^js this ctx]
  (when-not (identical? ctx (du/getv this k-ctx))
    (du/setv! this k-ctx ctx)))

(defn- first-apply-or-moved?
  "True on the first apply whatever `slice` holds, and after that only when it moved. One rule for
   every hook, so a consumer is initialised exactly once per hook it registers rather than only
   when its slice happens to start at something other than its resting value."
  [slice view last-view]
  (or (nil? last-view)
      (not= (slice view) (slice last-view))))

(defn- notify-on-change!
  "Invoke `callback` with `slice` of `view`. No-op when `callback` is nil."
  [^js this callback slice view last-view]
  (when (and callback (first-apply-or-moved? slice view last-view))
    (callback (du/getv this k-child) (slice view) this)))

(defn- render-when-changed!
  "Invoke `render` with the whole `view`. No-op when `render` is nil."
  [^js this render render-key view last-view]
  (when (and render (first-apply-or-moved? render-key view last-view))
    (render (du/getv this k-child) view this)))

(defn- apply-resource!
  "One projected view applied to a consumer. The hook order is part of the contract: on-failure,
   then render, then on-pending and on-writing. The data is painted before the flags describing
   the transition that produced it.

   `view` is recorded before the hooks run, not after, so a hook that submits and re-enters here
   compares against it rather than against the apply before it."
  [^js this {:keys [render render-key on-failure on-pending on-writing]
             :or   {render-key :accepted}} view ctx]
  (let [last-view (du/getv this k-last-view)]
    (install-ctx! this ctx)
    (du/setv! this k-last-view view)
    (notify-on-change!    this on-failure :failure  view last-view)
    (render-when-changed! this render render-key    view last-view)
    (notify-on-change!    this on-pending :pending? view last-view)
    (notify-on-change!    this on-writing :writing? view last-view)))

(defn- install-apply-resource!
  "Install the applyResource method a <server-resource> calls with each projected view. The render
   and hook slots are read straight out of `register!`'s own config map."
  [^js proto config]
  (.defineProperty js/Object proto "applyResource"
                   #js {:value        (fn apply-resource [view ctx]
                                        (this-as ^js this (apply-resource! this config view ctx)))
                        :writable     true
                        :configurable true}))

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

  Every hook fires once on the first apply, whatever its slice holds, and after that only when
  that slice moves. A consumer drives its child from the view alone and never from an attribute,
  so a consumer element observes none."
  [{:keys [tag child-tag on-connect] :as config}]
  (component/register!
   tag
   {:observed-attributes  #js []
    :connected-fn         (fn [^js el]
                            (du/setv! el k-child (.querySelector el child-tag))
                            (when on-connect (on-connect el)))
    :attribute-changed-fn (fn [_el _name _old _new] nil)
    :setup-prototype-fn   (fn [^js proto]
                            (install-apply-resource! proto config))}))
