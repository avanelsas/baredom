(ns bare-node-demo.renderer
  (:require [clojure.string :as str]))

;;; ── Prop helpers ──────────────────────────────────────────────────────────

(defn- on-key? [k]
  (str/starts-with? (name k) "on-"))

(defn- event-name [k]
  ;; :on-click → "click"   :on-x-modal-dismiss → "x-modal-dismiss"
  (subs (name k) 3))

(def ^:private listeners-key "__bd_listeners")
(def ^:private props-key     "__bd_props")

(defn- set-listener! [^js el k handler]
  (let [ename (event-name k)
        store (or (aget el listeners-key) #js {})]
    (when-let [old (aget store ename)]
      (.removeEventListener el ename old))
    (.addEventListener el ename handler)
    (aset store ename handler)
    (aset el listeners-key store)))

(defn- remove-listener! [^js el k]
  (let [ename (event-name k)
        store (aget el listeners-key)]
    (when-let [old (and store (aget store ename))]
      (.removeEventListener el ename old)
      (js-delete store ename))))

(defn- set-prop! [^js el k v]
  (cond
    (on-key? k)  (set-listener! el k v)
    (nil? v)     (.removeAttribute el (name k))
    (true? v)    (.setAttribute el (name k) "")
    (false? v)   (.removeAttribute el (name k))
    :else        (.setAttribute el (name k) (str v))))

(defn- store-props! [^js el props]
  (aset el props-key props))

(defn- get-stored-props [^js el]
  (or (aget el props-key) {}))

;;; ── Hiccup parsing ───────────────────────────────────────────────────────

(defn- parse-vnode [v]
  (let [[tag & args] v
        has-props? (and (seq args) (map? (first args)))
        props      (if has-props? (first args) {})
        children   (if has-props? (rest args) args)]
    [tag props children]))

(defn- flatten-children [children]
  (persistent!
   (reduce (fn [acc x]
             (cond
               (nil? x)    acc
               (false? x)  acc
               (string? x) (conj! acc x)
               (number? x) (conj! acc (str x))
               (vector? x) (conj! acc x)
               (seq? x)    (reduce conj! acc (flatten-children x))
               :else       acc))
           (transient [])
           children)))

;;; ── DOM creation ──────────────────────────────────────────────────────────

(declare create-node)

(defn- create-element [v]
  (let [[tag props children] (parse-vnode v)
        ^js el (.createElement js/document (name tag))]
    (doseq [[k val] props]
      (set-prop! el k val))
    (store-props! el props)
    (doseq [child (flatten-children children)]
      (.appendChild el (create-node child)))
    el))

(defn- create-node [x]
  (cond
    (string? x) (.createTextNode js/document x)
    (vector? x) (create-element x)
    :else       (.createTextNode js/document "")))

;;; ── Reconciler ────────────────────────────────────────────────────────────

(defn- patch-props! [^js el old-props new-props]
  ;; Remove props that no longer exist
  (doseq [[k _] old-props]
    (when-not (contains? new-props k)
      (if (on-key? k)
        (remove-listener! el k)
        (.removeAttribute el (name k)))))
  ;; Add / update changed props
  (doseq [[k v] new-props]
    (let [old-v (get old-props k)]
      (when (not= v old-v)
        (set-prop! el k v)))))

(declare patch-children!)

(defn- same-tag? [^js dom-node vnode]
  (and (= 1 (.-nodeType dom-node))
       (vector? vnode)
       (= (str/upper-case (name (first vnode)))
          (.-tagName dom-node))))

(defn- text-node? [^js node]
  (= 3 (.-nodeType node)))

(defn- patch-node! [^js parent ^js old-node new-vnode]
  (cond
    ;; Text → text: update in place
    (and (string? new-vnode) (text-node? old-node))
    (when (not= new-vnode (.-nodeValue old-node))
      (set! (.-nodeValue old-node) new-vnode))

    ;; Element with same tag: patch attributes + recurse children
    (same-tag? old-node new-vnode)
    (let [[_ new-props new-children] (parse-vnode new-vnode)
          old-props (get-stored-props old-node)]
      (patch-props! old-node old-props new-props)
      (store-props! old-node new-props)
      (patch-children! old-node (flatten-children new-children)))

    ;; Type or tag mismatch: replace
    :else
    (.replaceChild parent (create-node new-vnode) old-node)))

(defn- patch-children! [^js parent new-vnodes]
  (let [old-nodes (array-seq (.-childNodes parent))
        old-count (count old-nodes)
        new-count (count new-vnodes)]
    ;; Patch positions that exist in both old and new
    (dotimes [i (min old-count new-count)]
      (patch-node! parent (nth old-nodes i) (nth new-vnodes i)))
    ;; Append new children
    (when (> new-count old-count)
      (doseq [vnode (subvec new-vnodes old-count)]
        (.appendChild parent (create-node vnode))))
    ;; Remove excess old children
    (when (> old-count new-count)
      (dotimes [_ (- old-count new-count)]
        (.removeChild parent (.-lastChild parent))))))

;;; ── Public API ────────────────────────────────────────────────────────────

(defn render! [^js container view-fn]
  (let [vnodes (flatten-children [(view-fn)])]
    (if (zero? (.-childNodes.length container))
      ;; First render: create from scratch
      (doseq [vnode vnodes]
        (.appendChild container (create-node vnode)))
      ;; Subsequent renders: reconcile
      (patch-children! container vnodes))))

(defn mount! [^js container view-fn state-atom]
  (render! container view-fn)
  (add-watch state-atom ::render
             (fn [_ _ _ _]
               (render! container view-fn))))
