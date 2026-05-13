#!/usr/bin/env bb
;; Lint each .cljs file's `(:require ...)` block for consistent indentation
;; of top-level entries. The T1 drift class caught in batch-FINAL was
;; require blocks whose entries (the [namespace :as alias] vectors) drift
;; across many different columns. Two accepted layouts:
;;
;;   A. opening (:require on its own line, every entry on a fresh line:
;;          (:require
;;           [a]
;;           [b])
;;
;;   B. first entry on the same line as (:require, subsequent entries
;;      aligned with the first entry's column:
;;          (:require [a]
;;                    [b])
;;
;; This checker uses rewrite-clj to find the *top-level* entry vectors
;; in each require block (skipping nested `:refer [...]` brackets) and
;; verifies they share the same start column.
;;
;; Usage:  bb scripts/check_require_style.bb
;; Exit: 0 if clean, 1 if any drift detected. Designed for CI.

(ns check-require-style
  (:require [clojure.string :as str]
            [babashka.fs :as fs]
            [rewrite-clj.parser :as p]
            [rewrite-clj.node :as n]))

(defn- find-cljs-files []
  (->> (concat (fs/glob "src" "**/*.cljs")
               (fs/glob "test" "**/*.cljs"))
       (map str)
       sort))

(defn- iter-children
  "Recursively walk a rewrite-clj node tree, calling `visit-fn` on every
   list/forms-node. visit-fn receives the node itself."
  [node visit-fn]
  (when (n/inner? node)
    (visit-fn node)
    (doseq [c (n/children node)]
      (iter-children c visit-fn))))

(defn- require-blocks
  "Return a seq of require-block nodes (`(:require ...)` lists) in
   `root`. We recognise them by being a list whose first child is the
   keyword `:require`."
  [root]
  (let [out (atom [])]
    (iter-children
     root
     (fn [node]
       (when (= :list (n/tag node))
         (let [kids (n/children node)
               first-kw (some #(when (= :token (n/tag %))
                                 (let [s (n/sexpr %)]
                                   (when (keyword? s) s)))
                              (take 2 kids))]
           (when (= first-kw :require)
             (swap! out conj node))))))
    @out))

(defn- entry-columns
  "Return the {:row :col} for every top-level vector inside the require
   block — these are the `[namespace ...]` entries. Skips nested
   vectors inside `:refer`, `:as-alias`, etc."
  [require-node]
  (let [children (n/children require-node)]
    (->> children
         (filter #(= :vector (n/tag %)))
         (map (fn [v]
                (let [m (meta v)]
                  {:row (:row m) :col (:col m)})))
         (filter :col))))

(defn- check-file [path]
  (let [src   (slurp path)
        ;; Wrap in a forms node so we can walk all top-level forms
        forms (p/parse-string-all src)
        blocks (require-blocks forms)
        issues (atom [])]
    (doseq [block blocks]
      (let [cols (mapv :col (entry-columns block))]
        (when (and (> (count cols) 1)
                   (apply not= cols))
          (let [first-row (or (:row (first (entry-columns block))) 1)]
            (swap! issues conj
                   (str path ":" first-row ": :require entries at "
                        "different columns: " cols))))))
    @issues))

(defn -main [& _]
  (let [files (find-cljs-files)
        all   (mapcat check-file files)]
    (println "Checked" (count files) "cljs files for :require indent drift")
    (when (seq all)
      (println)
      (doseq [m all] (println "✗" m)))
    (println)
    (if (seq all)
      (do (println "FAIL —" (count all) ":require block(s) with indent drift")
          (System/exit 1))
      (println "OK — no :require indent drift"))))

(apply -main *command-line-args*)
