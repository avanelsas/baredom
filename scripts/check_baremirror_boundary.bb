#!/usr/bin/env bb
;; BareMirror is a package of its own in this repository. Its source and its
;; tests name no `baredom.*` namespace, so that it works without BareDOM.
;; BareDOM's source names only the places and the plan of BareMirror, so that
;; nothing else of it lands in the shared base module.
;;
;; Usage:  bb scripts/check_baremirror_boundary.bb
;; Exit:   0 if clean, 1 if a file crosses the boundary. Designed for CI.

(ns check-baremirror-boundary
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def ^:private rules
  [{:root    "baremirror"
    :refuses #"\bbaredom\.[a-z]"
    :says    "BareMirror must not name a baredom namespace:"}
   {:root    "src/baredom"
    :refuses #"\bbaremirror\.(?!alpha\.(places|plan)\b)[a-z]"
    :says    "BareDOM's source may name only baremirror.alpha.places and baremirror.alpha.plan:"}])

(defn- source-files [root]
  (sort (map str (fs/glob root "**/*.{clj,cljs,cljc}"))))

(defn- violation
  "The numbered `line` of `path` as a violation, when `refuses` finds a match in it."
  [refuses path index line]
  (when (re-find refuses line)
    {:file path :line (inc index) :text (str/trim line)}))

(defn- violations
  "The lines of `path` that `refuses` matches."
  [refuses path]
  (keep-indexed (partial violation refuses path) (str/split-lines (slurp path))))

(defn- broken
  "The rule with the violations found under its root, or nil when there are none."
  [{:keys [root refuses] :as rule}]
  (when-some [found (seq (mapcat (partial violations refuses) (source-files root)))]
    (assoc rule :found found)))

(defn- report-line! [{:keys [file line text]}]
  (println (str file ":" line ": " text)))

(defn- report! [{:keys [says found]}]
  (println says)
  (run! report-line! found))

(let [found (keep broken rules)]
  (if (seq found)
    (do (run! report! found)
        (System/exit 1))
    (println "BareMirror and BareDOM keep the boundary.")))
