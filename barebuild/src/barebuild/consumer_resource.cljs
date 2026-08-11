(ns barebuild.consumer-resource
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]))

(def ^:private k-child     "__xConsumerChild")
(def ^:private k-last-view "__xConsumerLastView")
(def ^:private k-ctx       "__xConsumerResourceCtx")

(defn- submit!
  "Hand `args` to the ctx closure `k`. The ctx lands with the first apply, so a gesture fired while
   the host is still waiting on its custom elements is reported rather than thrown."
  [^js consumer k args]
  (if-let [ctx (du/getv consumer k-ctx)]
    (apply (get ctx k) args)
    (js/console.error "[consumer-resource]" (.-tagName consumer)
                      "submitted before its resource booted, ignoring it")))

(defn submit-intent!
  "Send an intent patch from a gesture handler back to a server-resource. With no `target-id` the
   patch drives the consumer's own resource, with one it drives the named sibling through its URL
   projection."
  ([^js consumer patch]           (submit! consumer :submit-intent! [patch]))
  ([^js consumer patch target-id] (submit! consumer :submit-intent! [patch target-id])))

(defn submit-refresh!
  "Ask the server-resource to read the current intent again. For a gesture that wants the same
   question answered afresh rather than a different question asked: the intent does not move, the
   URL is not written, and what the resource holds stands until an answer replaces it. A read
   already in flight already answers this, so one is not opened on top of it."
  [^js consumer]
  (submit! consumer :submit-refresh! []))

(defn- consumer-tag [^js consumer]
  (.. consumer -tagName toLowerCase))

(defn submit-write!
  "Send a write from a gesture handler back to the server-resource. The payload is stamped with the
   consumer's tag as its `:submitter` unless it already names one. The stamp never reaches the
   server: a request body is built from `:record` alone."
  [^js consumer payload]
  (submit! consumer :submit-write! [(merge {:submitter (consumer-tag consumer)} payload)]))

(defn own-write?
  "True when the write `view` reports is the one this consumer submitted. A write moves `:writing?`
   for every consumer the resource drives, so one acting on that movement asks this first."
  [^js consumer view]
  (= (consumer-tag consumer) (get-in view [:write :payload :submitter])))

(defn view
  "The view this consumer was last applied, or nil before the first apply. What a gesture handler
   reads, since it fires from the DOM and is handed no view of its own."
  [^js consumer]
  (du/getv consumer k-last-view))

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

(defn- hook-plan
  "The hooks `config` registered, in the order they fire: the data is painted before the flags
   describing the transition that produced it. A row names what it calls and the slice whose
   movement fires it."
  [{:keys [render render-key on-failure on-pending on-writing]
    :or   {render-key :accepted}}]
  (remove (comp nil? :callback)
          [{:callback on-failure :slice :failure}
           {:callback render     :slice render-key}
           {:callback on-pending :slice :pending?}
           {:callback on-writing :slice :writing?}]))

(defn- fire-hook!
  "Invoke one hook with the driven child, the whole view and the consumer, when its slice moved. A
   slice decides whether a hook fires, never what it may read."
  [^js this {:keys [callback slice]} view last-view]
  (when (first-apply-or-moved? slice view last-view)
    (callback (du/getv this k-child) view this)))

(defn- apply-resource!
  "One projected view applied to a consumer. `view` is recorded before the hooks run, not after, so
   a hook that submits and re-enters here compares against it rather than against the apply before
   it."
  [^js this hooks view ctx]
  (let [last-view (du/getv this k-last-view)]
    (install-ctx! this ctx)
    (du/setv! this k-last-view view)
    (doseq [hook hooks]
      (fire-hook! this hook view last-view))))

(defn- install-apply-resource!
  "Install the applyResource method a <server-resource> calls with each projected view."
  [^js proto hooks]
  (aset proto "applyResource"
        (fn [view ctx]
          (this-as ^js this (apply-resource! this hooks view ctx)))))

(defn register!
  "Register a resource-consumer custom element from a config:
  :tag        element tag name
  :child-tag  the driven child element, cached on connect and handed to every hook
  :render     (fn [child view this]), optional, called when the render-key slice changes
  :render-key (fn [view]) -> the slice render draws. Defaults to the accepted envelope
  :on-failure (fn [child view this]), optional, called when :failure changes, with :failure nil
              on recovery so the component can clear its UI
  :on-pending (fn [child view this]), optional, called when :pending? changes
  :on-writing (fn [child view this]), optional, called when :writing? changes
  :on-connect (fn [child this]), optional extra wiring

  `view` is the whole of what a consumer may read:
  {:accepted :failure :intent :pending? :writing? :write}. Every hook is handed all of it, its slice
  deciding only when it fires: once on the first apply, then only when that slice moves. A consumer
  drives its child from the view alone and never from an attribute, so it observes none."
  [{:keys [tag child-tag on-connect] :as config}]
  (let [hooks (hook-plan config)]
    (component/register!
     tag
     {:observed-attributes  #js []
      :connected-fn         (fn [^js el]
                              (let [child (.querySelector el child-tag)]
                                (du/setv! el k-child child)
                                (when on-connect (on-connect child el))))
      :attribute-changed-fn (fn [_el _name _old _new] nil)
      :setup-prototype-fn   (fn [^js proto] (install-apply-resource! proto hooks))})))
