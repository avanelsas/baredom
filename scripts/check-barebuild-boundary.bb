#!/usr/bin/env bb
;; check-barebuild-boundary.bb — the single canonical enforcement of the
;; BareDOM <-> BareBuild boundary. CI invokes this one script; it runs four
;; independent checks and exits non-zero if any fails.
;;
;;   1. No cross-boundary relative file references. Nothing inside the
;;      barebuild/ platform dir reaches outside via a relative path, and no
;;      source/script outside references the barebuild/ tree by path. (The
;;      barebuild-* COMPONENTS live under src/baredom/components/ and are
;;      required by namespace — this check is about the barebuild/ platform dir.)
;;   2. Adapters exclude orchestration. No barebuild-* tag leaks into the x-
;;      package-export set, and no generated framework-adapter source mentions
;;      barebuild. (Adapter exclusion is by the x- prefix convention; this makes
;;      it tested, not merely conventional.)
;;   3. The published tarball excludes barebuild/. `npm pack --dry-run` must not
;;      list any file under barebuild/.
;;   4. No querySelector in barebuild-* component sources (wiring is by
;;      containment + events, never by selector).
;;
;; Usage:  bb scripts/check-barebuild-boundary.bb
;; Exit:   0 if clean, 1 if any violation found. Designed for CI.

(ns check-barebuild-boundary
  (:require [clojure.string :as str]
            [babashka.fs :as fs]
            [babashka.process :as p]
            [cheshire.core :as json]))

(def ^:private errors (atom []))
(defn- fail! [msg] (swap! errors conj msg))

(defn- slurp-glob [root pattern]
  (map (fn [f] [f (slurp (str f))]) (fs/glob root pattern)))

;; ── Check 1: cross-boundary relative references ─────────────────────────────────
(defn- check-relative-boundary! []
  (doseq [ext ["clj" "cljs" "cljc" "bb" "edn"]
          [f content] (slurp-glob "barebuild" (str "**/*." ext))]
    (when (re-find #"\.\./" content)
      (fail! (str "barebuild/ file reaches outside via a relative path: " f))))
  (doseq [dir ["src" "scripts"]
          ext ["clj" "cljs" "cljc" "bb"]
          [f content] (slurp-glob dir (str "**/*." ext))]
    (when (re-find #"\"barebuild/" content)
      (fail! (str "file outside barebuild/ references the barebuild/ path: " f)))))

;; ── Check 2: adapters exclude orchestration ─────────────────────────────────────
(defn- check-adapters-exclude-barebuild! []
  (let [pkg       (json/parse-string (slurp "package.json") true)
        x-exports (->> (:exports pkg) keys (map name) (filter #(str/starts-with? % "x-")))]
    (when (some #(str/includes? % "barebuild") x-exports)
      (fail! "a barebuild-* tag leaked into the x- package-export set (adapters would wrap it)")))
  ;; Two patterns per extension: flat files directly in */src and nested ones.
  ;; babashka's `**` requires >=1 intermediate dir, so it alone misses the flat
  ;; per-component wrappers (e.g. adapters/react/src/x-alert.tsx).
  (doseq [ext ["ts" "tsx" "jsx"]
          pat [(str "*/src/*." ext) (str "*/src/**/*." ext)]
          [f content] (slurp-glob "adapters" pat)]
    (when (str/includes? content "barebuild")
      (fail! (str "generated adapter source references barebuild: " f)))))

;; ── Check 3: tarball excludes barebuild/ ────────────────────────────────────────
(defn- check-pack-excludes-barebuild! []
  (let [{:keys [out exit]} (p/shell {:out :string :err :string :continue true}
                                    "npm pack --dry-run --json --ignore-scripts")]
    (if (not= 0 exit)
      (fail! "npm pack --dry-run failed; cannot verify tarball contents")
      (let [files (->> (json/parse-string out true) first :files (map :path))]
        (when (some #(str/starts-with? % "barebuild/") files)
          (fail! "barebuild/ is included in the npm tarball (should be excluded by the files allowlist)"))))))

;; ── Check 4: no querySelector in barebuild component sources ────────────────────
(defn- check-no-queryselector! []
  (doseq [[f content] (slurp-glob "src/baredom/components" "barebuild_*/*.cljs")]
    (when (re-find #"querySelector" content)
      (fail! (str "querySelector in barebuild component source: " f)))))

;; ── Run ───────────────────────────────────────────────────────────────────────
(check-relative-boundary!)
(check-adapters-exclude-barebuild!)
(check-pack-excludes-barebuild!)
(check-no-queryselector!)

(if (seq @errors)
  (do (println "✗ barebuild boundary check FAILED:")
      (doseq [e @errors] (println "  -" e))
      (System/exit 1))
  (do (println "✓ barebuild boundary check passed (4 checks)")
      (System/exit 0)))
