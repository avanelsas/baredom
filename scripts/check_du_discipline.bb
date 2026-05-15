#!/usr/bin/env bb
;; Lint each src/baredom/components/ and src/baredom/dev/ .cljs file for
;; two BareDOM-specific conventions that were swept across the codebase
;; in PRs #200–#212 and that all future components must follow:
;;
;;   1. Instance fields on the host element MUST use du/setv! / du/getv,
;;      not gobj/set / gobj/get. Routing through du/ makes every
;;      mutation visible to the x-trace-history recorder; gobj/set on
;;      the host element bypasses that hook silently.
;;
;;   2. Components MUST NOT define a local `(defn- make-el [tag] ...)`
;;      shim. It conflicts with the test-side `make-el` (which
;;      constructs a fully populated component element) and is a thin
;;      wrapper that should be inlined as `.createElement js/document`
;;      directly. Exception: `x_copy` retains
;;      `(defn- make-el [^js doc tag] ...)` because its signature is a
;;      real abstraction over a custom document object — not a shim.
;;
;; Usage:  bb scripts/check_du_discipline.bb
;; Exit:   0 if clean, 1 if any violation found. Designed for CI.

(ns check-du-discipline
  (:require [clojure.string :as str]
            [babashka.fs :as fs]))

;; NOTE: this pattern is intentionally narrow. A broadened form
;; (`\(gobj/(?:set|get) (?:el|this)\b`) catches more leak vectors —
;; `(gobj/get this k-X)` in property accessors, `(gobj/set el model/k-X …)`
;; — but surfaces ~50 pre-existing violations across ~20 components and
;; the `x-trace-history` dev tool. A separate sweep PR will broaden the
;; pattern alongside the cleanup. See the audit plan at
;; `.claude/plans/you-are-an-experienced-wondrous-peach.md` (cross-cutting
;; pattern #1 — "gobj/get this k-X via helpers").
(def ^:private gobj-pattern #"\(gobj/(?:set|get) el k-")
(def ^:private shim-pattern #"\(defn-? make-el \[(?:\^js )?tag\]")

(defn- find-cljs-files []
  (->> (concat (fs/glob "src/baredom/components" "**/*.cljs")
               (fs/glob "src/baredom/dev"        "**/*.cljs"))
       (map str)
       sort))

(defn- comment-line? [line]
  (str/starts-with? (str/triml line) ";"))

(defn- check-file [path]
  (let [src     (slurp path)
        lines   (->> (str/split-lines src)
                     (map-indexed (fn [idx l] [(inc idx) l]))
                     (remove (fn [[_ l]] (comment-line? l))))
        hits    (atom [])]
    (doseq [[lineno line] lines]
      (when (re-find gobj-pattern line)
        (swap! hits conj
               (str path ":" lineno
                    ": forbidden gobj on host element — use (du/setv! el k-X v) "
                    "or (du/getv el k-X). " (str/trim line))))
      (when (re-find shim-pattern line)
        (swap! hits conj
               (str path ":" lineno
                    ": forbidden `make-el [tag]` shim — inline as "
                    "(.createElement js/document \"...\"). " (str/trim line)))))
    @hits))

(defn -main [& _]
  (let [files (find-cljs-files)
        all   (mapcat check-file files)]
    (println "Checked" (count files) "cljs files for du discipline")
    (when (seq all)
      (println)
      (doseq [m all] (println "✗" m)))
    (println)
    (if (seq all)
      (do (println "FAIL —" (count all) "convention violation(s)")
          (System/exit 1))
      (println "OK — du discipline clean"))))

(apply -main *command-line-args*)
