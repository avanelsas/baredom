(ns baredom.utils.dom
  (:require [goog.object :as gobj]))

(defn getv [el k] (gobj/get el k))

(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el key]
  (true? (getv el key)))

(defn mark-initialized! [el key]
  (setv! el key true))

(defn has-attr?
  [^js el attr-name]
  (.hasAttribute el attr-name))

(defn get-attr
  [^js el attr-name]
  (.getAttribute el attr-name))

(defn set-bool-attr!
  [^js el attr-name value]
  (if value
    (.setAttribute el attr-name "")
    (.removeAttribute el attr-name)))
