#!/usr/bin/env bb
;; A browser gives a native control a font of its own. It does not inherit the font of the
;; page, so a component that creates a button, an input, a select or a textarea must say so.
;; `du/control-font-rule` is that rule, and a component that creates such a control has it in
;; its style. `overlay/make-layer!` puts the rule in every layer it makes, so a component whose
;; controls are in a layer has it from there.
;;
;; A component that has controls both in its own shadow root and in a layer passes when the
;; layer has the rule. The check cannot see in which of the two a control ends up.
;;
;; Usage:  bb scripts/check_control_font.bb
;; Exit: 0 when every such component has the rule, 1 otherwise. Designed for CI.

(ns check-control-font
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(def ^:private rule #"du/control-font-rule|overlay/make-layer!")

(def ^:private creates-control
  "A call that creates a native control, or a template that names one."
  #"createElement\s+js/document\s+\"(button|input|select|textarea)\"|\[:(button|input|select|textarea)\b")

(defn- source
  "The source of every file of the component in `dir`."
  [dir]
  (str/join "\n" (map (comp slurp str) (fs/glob dir "*.cljs"))))

(defn- fault? [dir]
  (let [text (source dir)]
    (and (re-find creates-control text) (not (re-find rule text)))))

(let [dirs   (sort (map str (fs/list-dir "src/baredom/components" fs/directory?)))
      faults (filter fault? dirs)]
  (if (seq faults)
    (do (println "These components create a native control and have no du/control-font-rule:")
        (run! #(println "   " %) faults)
        (System/exit 1))
    (println (str "Every component that creates a native control gives it the font of the page ("
                  (count (filter #(re-find creates-control (source %)) dirs)) " components)."))))
