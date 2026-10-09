#!/usr/bin/env bb
;; A form control is disabled by its own attribute or by a fieldset. It reads that state with
;; `forms/disabled?`. A read of the attribute misses the fieldset, a write of the attribute on the
;; host keeps the control disabled after the fieldset is enabled, and a style that selects the
;; attribute does not show the fieldset.
;;
;; Usage:  bb scripts/check_form_disabled.bb
;; Exit: 0 when no form control reads, writes or styles the attribute on its host, 1 otherwise.
;; Designed for CI.

(ns check-form-disabled
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def ^:private form-associated ":form-associated?")

(def ^:private faults
  "What a form control must not do with the disabled attribute of its host."
  {"reads the disabled attribute of its host"
   #"(has-attr\?|get-attr)\s+el\s+(model/)?attr-disabled"
   "writes the disabled attribute of its host"
   #"(set-attr!|set-bool-attr!|set-attr-to!|remove-attr!)\s+el\s+(model/)?attr-disabled"
   "styles its host by the disabled attribute"
   #":host\(\[disabled\]"})

(defn- found
  "The faults of the source `text`."
  [text]
  (keep (fn [[fault pattern]] (when (re-find pattern text) fault)) faults))

(defn- file-faults [file]
  (let [text (slurp file)]
    (when (str/includes? text form-associated)
      (map (partial vector file) (found text)))))

(let [files (sort (map str (fs/glob "src/baredom/components" "*/x_*.cljs")))
      all   (mapcat file-faults files)]
  (if (seq all)
    (do (println "These form controls do not read their disabled state with forms/disabled?:")
        (run! (fn [[file fault]] (println "   " file fault)) all)
        (System/exit 1))
    (println "Every form control reads its disabled state with forms/disabled?.")))
