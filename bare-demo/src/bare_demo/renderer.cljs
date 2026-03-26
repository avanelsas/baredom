(ns bare-demo.renderer
  (:require [clojure.string :as str]))

;;; ── Prop helpers ──────────────────────────────────────────────────────────

(defn- on-key? [k]
  (str/starts-with? (name k) "on-"))

(defn- event-name [k]
  ;; :on-click → "click"   :on-x-modal-dismiss → "x-modal-dismiss"
  (subs (name k) 3))

(defn- set-prop! [el k v]
  (let [attr (name k)]
    (cond
      (on-key? k)  (.addEventListener el (event-name k) v)
      (nil? v)     (.removeAttribute el attr)
      (true? v)    (.setAttribute el attr "")
      (false? v)   (.removeAttribute el attr)
      :else        (.setAttribute el attr (str v)))))

;;; ── DOM creation ──────────────────────────────────────────────────────────

(declare create-nodes)

(defn- create-element [[tag & args]]
  (let [has-props? (and (seq args) (map? (first args)))
        props      (when has-props? (first args))
        children   (if has-props? (rest args) args)
        el         (.createElement js/document (name tag))]
    (doseq [[k v] props]
      (set-prop! el k v))
    (doseq [node (mapcat create-nodes children)]
      (.appendChild el node))
    el))

(defn create-nodes [x]
  (cond
    (nil? x)    []
    (false? x)  []
    (string? x) [(.createTextNode js/document x)]
    (number? x) [(.createTextNode js/document (str x))]
    (vector? x) [(create-element x)]
    (seq? x)    (mapcat create-nodes x)
    :else       []))

;;; ── Mount ─────────────────────────────────────────────────────────────────

(defn render! [container view-fn]
  (set! (.-innerHTML container) "")
  (doseq [node (create-nodes (view-fn))]
    (.appendChild container node)))

(defn mount! [container view-fn state-atom]
  (render! container view-fn)
  (add-watch state-atom ::render
             (fn [_ _ _ _]
               (render! container view-fn))))
