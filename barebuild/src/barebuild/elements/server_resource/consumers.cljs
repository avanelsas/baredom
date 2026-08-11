(ns barebuild.elements.server-resource.consumers
  "The consumers one <server-resource> drives: which elements they are, what they may call back
  into, and handing each a projected view."
  (:require
   [barebuild.elements.server-resource.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-consumers "__xConsumers")
(def ^:private k-ctx       "__xConsumerCtx")

(defn- apply-one!
  "Notify one consumer, isolating a throwing applyResource so later consumers still run."
  [^js c view ctx own-id]
  (try
    (.applyResource c view ctx)
    (catch :default e
      (js/console.error "[server-resource]" (model/label own-id)
                        "consumer applyResource threw:" e))))

(defn apply!
  "Hand `view` to every consumer this element drives."
  [^js el view own-id]
  (let [ctx (du/getv el k-ctx)]
    (doseq [^js c (du/getv el k-consumers)]
      (apply-one! c view ctx own-id))))

(defn- submit-intent!
  "Hand a consumer's intent patch back in as an event. A `target-id` names a sibling resource,
  which `step` routes."
  ([emit! ^js el patch]           (emit! el [:intent-patch patch]))
  ([emit! ^js el patch target-id] (submit-intent! emit! el (assoc patch :target-id target-id))))

(defn- ctx
  "What a consumer may call back into. Built once at boot, the same three closures for the element's
  whole life."
  [emit! ^js el]
  {:submit-intent!  (partial submit-intent! emit! el)
   :submit-refresh! (fn [] (emit! el [:refresh]))
   :submit-write!   (fn [payload] (emit! el [:submit-write payload]))})

(defn install!
  "Cache the consumers among `nodes` and the ctx they submit through, reporting a host that drives
  none. `nodes` are the elements the host owns, never a nested <server-resource>'s."
  [^js el resource-id nodes emit!]
  (let [consumers (filterv (fn [^js c] (some? (.-applyResource c))) nodes)]
    (when (empty? consumers)
      (js/console.error "[server-resource]" (model/label resource-id) "has no consumers"))
    (du/setv! el k-consumers consumers)
    (du/setv! el k-ctx (ctx emit! el))))
