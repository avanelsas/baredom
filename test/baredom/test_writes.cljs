(ns baredom.test-writes
  "Counts of the attribute writes that a test makes in the document."
  (:require [clojure.string :as str]
            [baredom.utils.dom :as du]))

(defn count-attr-writes!
  "An atom that counts the writes of `attrs` inside `node` from now on."
  [^js node attrs]
  (let [writes (atom 0)]
    (.observe (js/MutationObserver. (fn count-records [records] (swap! writes + (.-length records))))
              node #js {:subtree true :attributeFilter (clj->js attrs)})
    writes))

(defn count-child-changes!
  "An atom that counts the changes to the children of `node` from now on."
  [^js node]
  (let [changes (atom 0)]
    (.observe (js/MutationObserver. (fn count-records [records] (swap! changes + (.-length records))))
              node #js {:childList true})
    changes))

(def ^:private attr-writes #{:dom/attribute-set :dom/attribute-removed})

(defn- trace-into!
  "Installs a trace hook that adds each record to the atom `seen`. Returns the hook it replaces."
  [seen]
  (let [hook @du/trace-hook]
    (reset! du/trace-hook (partial swap! seen conj))
    hook))

(defn- traced!
  "The trace records of `work`."
  [work]
  (let [seen (atom [])
        hook (trace-into! seen)]
    (try
      (work)
      (finally
        (reset! du/trace-hook hook)))
    @seen))

(defn- foreign?
  "True when `node` is in the light DOM and is not `host`. A shadow root belongs to its host."
  [^js host ^js node]
  (and (not (identical? host node))
       (not (instance? js/ShadowRoot (.getRootNode node)))))

(defn- foreign-pairs
  "The [tag attribute] pairs of the attribute writes in `records` that `host` made at depth 0 on
   light-DOM nodes other than itself."
  [^js host records]
  (into #{}
        (comp (filter (fn host-write? [record]
                        (and (contains? attr-writes (:type record)) (zero? (:depth record)))))
              (filter (fn foreign-write? [{:keys [el]}] (foreign? host el)))
              (map (fn pair [{:keys [^js el attribute]}]
                     [(str/lower-case (.-tagName el)) attribute])))
        records))

(defn foreign-writes!
  "The [tag attribute] pairs that `host` writes on nodes it does not own while `work` runs.
   `work` changes `host` with a plain DOM call, so the writes of `host` run at depth 0."
  [^js host work]
  (foreign-pairs host (traced! work)))

(defn foreign-writes-later!
  "Calls `done` with the pairs of `foreign-writes!` for `work` and the `ms` milliseconds after it.
   For a host that answers a change of its children in `slotchange`."
  [^js host work ms done]
  (let [seen (atom [])
        hook (trace-into! seen)]
    (try
      (work)
      (catch :default e
        (reset! du/trace-hook hook)
        (throw e)))
    (js/setTimeout (fn finish []
                     (reset! du/trace-hook hook)
                     (done (foreign-pairs host @seen)))
                   ms)))

(defn- covers?
  "True when the declared write `w` covers the pair of `tag` and `attr`."
  [[tag attr] {:keys [target attributes]}]
  (and (contains? #{tag :any} target)
       (or (= :any attributes) (contains? attributes attr))))

(defn- undeclared
  "The pairs in `writes` that no entry of `foreign-writes` covers."
  [foreign-writes writes]
  (set (remove (fn declared? [pair] (some (partial covers? pair) foreign-writes)) writes)))

(defn- unexercised
  "The [target attribute] pairs that `foreign-writes` declares and none of `writes` shows.
   An entry with `:attributes :any` names no attribute to show."
  [foreign-writes writes]
  (set (for [{:keys [target attributes] :as w} foreign-writes
             :when (set? attributes)
             attr  attributes
             :when (not-any? (fn shows? [[_ written :as pair]]
                               (and (= attr written) (covers? pair w)))
                             writes)]
         [target attr])))

(defn audit
  "The `writes` that `foreign-writes` does not declare, and the declared writes that `writes`
   does not show."
  [foreign-writes writes]
  {:undeclared  (undeclared foreign-writes writes)
   :unexercised (unexercised foreign-writes writes)})
