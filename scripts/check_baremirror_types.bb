#!/usr/bin/env bb
;; The types of BareMirror are written by hand. This script checks that they name each function
;; that the :baremirror-alpha module exports, and no other.
;;
;; Usage:  bb scripts/check_baremirror_types.bb
;; Exit:   0 if the names match, 1 if not. Designed for CI.

(ns check-baremirror-types
  (:require [clojure.edn :as edn]
            [clojure.set :as set]))

(def ^:private shadow-file "shadow-cljs.edn")

(def ^:private dts-file "baremirror/baremirror-alpha.d.ts")

(defn- exported
  "The names that the :baremirror-alpha module exports, in the text `shadow-text`."
  [shadow-text]
  (let [config (edn/read-string {:default tagged-literal} shadow-text)]
    (into (sorted-set) (map name)
          (keys (get-in config [:builds :lib :modules :baremirror-alpha :exports])))))

(defn- declared
  "The names of the functions that the types in the text `dts-text` declare."
  [dts-text]
  (into (sorted-set) (map second) (re-seq #"(?m)^export function (\w+)" dts-text)))

(defn- report! [says names]
  (when (seq names)
    (println says (vec names))))

(let [exports   (exported (slurp shadow-file))
      functions (declared (slurp dts-file))
      untyped   (set/difference exports functions)
      unknown   (set/difference functions exports)]
  (report! (str "Exported with no type in " dts-file ":") untyped)
  (report! (str "Typed in " dts-file " and not exported:") unknown)
  (if (= exports functions)
    (println "The types of BareMirror name each export.")
    (System/exit 1)))
