#!/usr/bin/env bb
;; A component that sets a display on its host must also hide the host under `hidden`. An author
;; rule such as `:host{display:block}` wins over the browser's own rule for the attribute, so
;; without `du/hidden-rule` the attribute has no effect. The rule must come after every other
;; rule of the form `:host(...)` that sets a display, since those have the same weight.
;;
;; Usage:  bb scripts/check_hidden_rule.bb
;; Exit: 0 when every component has the rule in its place, 1 otherwise. Designed for CI.

(ns check-hidden-rule
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def ^:private hidden-rule "du/hidden-rule")
(def ^:private host-rule #":host\s*\{([^}]*)\}")
(def ^:private shown #"display\s*:\s*(?!none)[a-z-]+")
(def ^:private shown-host-with-condition #":host\([^)]+\)\s*\{[^}]*display\s*:\s*(?!none)[a-z-]+")

(defn- style-text
  "The source of `file` with adjacent string literals joined, so a rule split over lines is whole."
  [file]
  (str/replace (slurp file) #"\"\s*\n\s*\"" ""))

(defn- shown-host?
  "True when `text` gives the host a display other than none."
  [text]
  (boolean (some (comp (partial re-find shown) second) (re-seq host-rule text))))

(defn- fault
  "What is wrong with the style in `text`, or nil."
  [text]
  (let [at (str/index-of text hidden-rule)]
    (cond
      (not (shown-host? text))                              nil
      (nil? at)                                             "has no du/hidden-rule"
      (re-find shown-host-with-condition (subs text at))    "sets a display on the host after du/hidden-rule"
      :else                                                 nil)))

(defn- file-fault [file]
  (when-some [found (fault (style-text file))]
    [file found]))

(let [files  (sort (map str (fs/glob "src/baredom/components" "*/x_*.cljs")))
      faults (keep file-fault files)]
  (if (seq faults)
    (do (println "These components do not hide under hidden:")
        (run! (fn [[file found]] (println "   " file found)) faults)
        (System/exit 1))
    (println (str "Every component that sets a display on its host hides under hidden ("
                  (count files) " files)."))))
