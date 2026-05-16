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

;; Matches `(gobj/get el …)`, `(gobj/set this …)`, `(gobj/set instance …)`
;; and similar — any gobj read/write whose first argument is one of the
;; three conventional names for the host element. The previous narrow
;; form (`\(gobj/(set|get) el k-`) missed:
;;   • `gobj/get this k-X` in property accessors (host binding is `this`)
;;   • `gobj/get el "X"` and `gobj/get el model/k-X` — non-`k-` keys
;;   • `gobj/get this field-key` — helper-function parameter
;;   • `gobj/get instance "_X"` — components that pass the host as
;;     `instance` (x_sidebar, etc.); the regex now covers this too.
;; Each form bypasses the trace-recorder hook. Lines tagged with
;; `;; allow-gobj:` (same line or any of the two lines above) are
;; exempt — reserved for the recorder's own bootstrap, where routing
;; through `du/setv!` would either recurse on the hook or fire it before
;; the internal-host filter is in place.
(def ^:private gobj-pattern  #"\(gobj/(?:set|get) (?:el|this|instance)\b")
(def ^:private allow-marker  "allow-gobj:")
(def ^:private shim-pattern  #"\(defn-? make-el \[(?:\^js )?tag\]")

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
      (when (and (re-find gobj-pattern line)
                 (not (str/includes? line allow-marker)))
        (swap! hits conj
               (str path ":" lineno
                    ": forbidden gobj on host element — use (du/setv! el k-X v) "
                    "or (du/getv el k-X), or tag the same line with "
                    "`;; allow-gobj: <reason>` for recorder-internal bootstrap. "
                    (str/trim line))))
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
