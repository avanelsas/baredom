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

(defn- traced!
  "The trace records of `work`."
  [work]
  (let [hook @du/trace-hook
        seen (atom [])]
    (reset! du/trace-hook (partial swap! seen conj))
    (try
      (work)
      (finally
        (reset! du/trace-hook hook)))
    @seen))

(defn- own?
  "True when `node` is `host` or lies in its shadow root."
  [^js host ^js node]
  (or (identical? host node)
      (identical? (.getRootNode node) (.-shadowRoot host))))

(defn foreign-writes!
  "The [tag attribute] pairs that `host` writes on nodes it does not own while `work` runs.
   `work` changes `host` with a plain DOM call, so the writes of `host` run at depth 0."
  [^js host work]
  (into #{}
        (comp (filter (fn host-write? [record]
                        (and (contains? attr-writes (:type record)) (zero? (:depth record)))))
              (remove (fn own-write? [{:keys [el]}] (own? host el)))
              (map (fn pair [{:keys [^js el attribute]}]
                     [(str/lower-case (.-tagName el)) attribute])))
        (traced! work)))

(defn- covers?
  "True when the declared write `w` covers the pair of `tag` and `attr`."
  [[tag attr] {:keys [target attributes]}]
  (and (contains? #{tag :any} target)
       (contains? attributes attr)))

(defn- undeclared
  "The pairs in `writes` that no entry of `foreign-writes` covers."
  [foreign-writes writes]
  (set (remove (fn declared? [pair] (some (partial covers? pair) foreign-writes)) writes)))

(defn- unexercised
  "The [target attribute] pairs that `foreign-writes` declares and none of `writes` shows."
  [foreign-writes writes]
  (set (for [{:keys [target attributes] :as w} foreign-writes
             attr attributes
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
