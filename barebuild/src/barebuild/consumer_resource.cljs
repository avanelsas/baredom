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

(defn submit-write!
  "Send a write from a gesture handler back to the server-resource."
  [^js consumer payload]
  (submit! consumer :submit-write! [payload]))

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
   describing the transition that produced it. A row names what it calls, the slice whose movement
   fires it, and what it is handed."
  [{:keys [render render-key on-failure on-pending on-writing]
    :or   {render-key :accepted}}]
  (remove (comp nil? :callback)
          [{:callback on-failure :slice :failure   :payload :failure}
           {:callback render     :slice render-key :payload identity}
           {:callback on-pending :slice :pending?  :payload :pending?}
           {:callback on-writing :slice :writing?  :payload :writing?}]))

(defn- fire-hook!
  "Invoke one hook with the driven child, its payload and the consumer, when its slice moved."
  [^js this {:keys [callback slice payload]} view last-view]
  (when (first-apply-or-moved? slice view last-view)
    (callback (du/getv this k-child) (payload view) this)))

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
  :on-failure (fn [child failure this]), optional, called when :failure changes, with failure nil
              on recovery so the component can clear its UI
  :on-pending (fn [child pending this]), optional, called when :pending? changes
  :on-writing (fn [child writing this]), optional, called when :writing? changes
  :on-connect (fn [child this]), optional extra wiring

  `view` is the whole of what a consumer may read: {:accepted :failure :intent :pending? :writing?}.
  Every hook fires once on the first apply, whatever its slice holds, and after that only when that
  slice moves. A consumer drives its child from the view alone and never from an attribute, so a
  consumer element observes none."
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
