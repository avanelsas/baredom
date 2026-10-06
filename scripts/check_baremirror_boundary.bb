#!/usr/bin/env bb
;; BareMirror is a package of its own in this repository. Its source and its
;; tests name no `baredom.*` namespace, so that it works without BareDOM.
;;
;; Usage:  bb scripts/check_baremirror_boundary.bb
;; Exit:   0 if clean, 1 if a file names a baredom namespace. Designed for CI.

(ns check-baremirror-boundary
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def ^:private root "baremirror")

(def ^:private baredom-namespace #"\bbaredom\.[a-z]")

(defn- source-files []
  (sort (map str (fs/glob root "**/*.{clj,cljs,cljc}"))))

(defn- violation
  "The numbered `line` of `path` as a violation, when it names a baredom namespace."
  [path index line]
  (when (re-find baredom-namespace line)
    {:file path :line (inc index) :text (str/trim line)}))

(defn- violations
  "The lines of `path` that name a baredom namespace."
  [path]
  (keep-indexed (partial violation path) (str/split-lines (slurp path))))

(defn- report! [{:keys [file line text]}]
  (println (str file ":" line ": " text)))

(let [files (source-files)
      found (mapcat violations files)]
  (if (seq found)
    (do (println "BareMirror must not name a baredom namespace:")
        (run! report! found)
        (System/exit 1))
    (println (str "BareMirror names no baredom namespace (" (count files) " files)."))))
