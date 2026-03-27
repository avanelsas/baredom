(ns release
  (:require [babashka.process :refer [shell sh]]
            [clojure.string :as str]
            [cheshire.core :as json]))

;; ── ANSI output helpers ───────────────────────────────────────────────────────

(def ^:private R    "\u001b[31m")
(def ^:private G    "\u001b[32m")
(def ^:private Y    "\u001b[33m")
(def ^:private B    "\u001b[34m")
(def ^:private BOLD "\u001b[1m")
(def ^:private NC   "\u001b[0m")

(defn- info    [& args] (println (str B  "[release]" NC " " (apply str args))))
(defn- success [& args] (println (str G  "[release]" NC " " (apply str args))))
(defn- warn    [& args] (println (str Y  "[release]" NC " " (apply str args))))
(defn- die     [& args]
  (binding [*out* *err*]
    (println (str R "[release] ERROR:" NC " " (apply str args))))
  (System/exit 1))

;; ── Version logic ─────────────────────────────────────────────────────────────

(defn- parse-version
  "Splits 'X.Y.Z-suffix' into {:major X :minor Y :patch Z :suffix suffix-or-nil}.
   e.g. '0.1.0-alpha' → {:major 0 :minor 1 :patch 0 :suffix 'alpha'}"
  [v]
  (let [[base suffix] (str/split v #"-" 2)
        [major minor patch] (mapv #(Long/parseLong %) (str/split base #"\."))]
    {:major major :minor minor :patch patch :suffix suffix}))

(defn- format-version [{:keys [major minor patch suffix]}]
  (cond-> (str major "." minor "." patch)
    suffix (str "-" suffix)))

(defn- compute-new-version
  "Bumps current-version according to bump-arg:
     :patch  — increment patch, keep pre-release suffix  (0.1.0-alpha → 0.1.1-alpha)
     :minor  — increment minor, reset patch, keep suffix  (0.1.0-alpha → 0.2.0-alpha)
     :major  — increment major, reset minor+patch, DROP suffix (0.1.0-alpha → 1.0.0)
   An explicit version string like '0.2.0-beta.1' is validated and returned as-is."
  [current-version bump-arg]
  (let [{:keys [major minor patch suffix]} (parse-version current-version)]
    (case bump-arg
      ":patch" (format-version {:major major :minor minor :patch (inc patch) :suffix suffix})
      ":minor" (format-version {:major major :minor (inc minor) :patch 0      :suffix suffix})
      ":major" (format-version {:major (inc major) :minor 0     :patch 0      :suffix nil})
      (if (re-matches #"\d+\.\d+\.\d+(-[a-zA-Z0-9][a-zA-Z0-9.\-]*)?" bump-arg)
        bump-arg
        (die "Invalid argument: '" bump-arg "'.\n"
             "  Usage: bb release :patch|:minor|:major\n"
             "     or: bb release \"X.Y.Z\" or \"X.Y.Z-suffix\"")))))

;; ── File patching ─────────────────────────────────────────────────────────────

(defn- update-package-json! [new-version]
  (let [path    "package.json"
        pkg     (json/parse-string (slurp path) true)
        updated (assoc pkg :version new-version)]
    (spit path (str (json/generate-string updated {:pretty true}) "\n"))))

(defn- patch-file!
  "Replaces the first match of pattern-str in path with replacement.
   Calls die if no match is found."
  [path pattern-str replacement description]
  (let [content     (slurp path)
        new-content (str/replace-first content (re-pattern pattern-str) replacement)]
    (when (= content new-content)
      (die "Could not update " description " in " path " — pattern not found:\n  " pattern-str))
    (spit path new-content)))

;; ── Git helpers ───────────────────────────────────────────────────────────────

(defn- git-clean? []
  (and (zero? (:exit (sh "git" "diff" "--quiet")))
       (zero? (:exit (sh "git" "diff" "--cached" "--quiet")))))

(defn- tag-exists? [tag]
  (not (str/blank? (:out (sh "git" "tag" "--list" tag)))))

;; ── Main ─────────────────────────────────────────────────────────────────────

(defn -main [args]
  (when (empty? args)
    (die "Usage: bb release :patch|:minor|:major"))

  (let [bump-arg        (first args)
        current-version (-> (slurp "package.json")
                            (json/parse-string true)
                            :version)
        new-version     (compute-new-version current-version bump-arg)]

    (info "Current version: " BOLD current-version NC)
    (info "New version:     " BOLD new-version NC)

    ;; ── Pre-flight checks ────────────────────────────────────────────────────

    (when (= current-version new-version)
      (die "Version is already " current-version " — nothing to do."))

    (when (tag-exists? (str "v" new-version))
      (die "Tag v" new-version " already exists. Aborting."))

    (when-not (git-clean?)
      (die "Working tree is dirty. Commit or stash your changes first."))

    ;; ── CHANGELOG check ──────────────────────────────────────────────────────

    (if (str/includes? (slurp "CHANGELOG.md") (str "[" new-version "]"))
      (success "CHANGELOG.md has entry for [" new-version "].")
      (do
        (warn "No CHANGELOG.md entry found for [" new-version "].")
        (print (str Y "Have you updated CHANGELOG.md? [y/N] " NC))
        (flush)
        (when-not (contains? #{"y" "Y"} (str/trim (or (read-line) "")))
          (die "Aborted. Update CHANGELOG.md first, then re-run."))))

    ;; ── Patch version in tracked files ───────────────────────────────────────

    (info "Updating package.json ...")
    (update-package-json! new-version)

    (info "Updating build.clj ...")
    ;; Matches exactly: (def version   "0.1.0-alpha")  — note the 3 spaces
    (patch-file! "build.clj"
                 "\\(def version   \"[^\"]+\"\\)"
                 (str "(def version   \"" new-version "\")")
                 "version def")

    (info "Updating deps.edn ...")
    ;; Matches: baredom-<version>.jar  inside the artifact path string
    (patch-file! "deps.edn"
                 "baredom-[^\"]+\\.jar"
                 (str "baredom-" new-version ".jar")
                 "artifact path")

    ;; ── Verify all three are consistent ──────────────────────────────────────

    (let [pkg-ver   (-> (slurp "package.json") (json/parse-string true) :version)
          build-ver (second (re-find #"\(def version   \"([^\"]+)\"\)" (slurp "build.clj")))
          deps-ver  (second (re-find #"baredom-([^\"]+)\.jar" (slurp "deps.edn")))]
      (doseq [[label v] [["package.json" pkg-ver]
                         ["build.clj"    build-ver]
                         ["deps.edn"     deps-ver]]]
        (when (not= v new-version)
          (die label " shows '" v "' but expected '" new-version "'")))
      (success "All files consistent at " new-version "."))

    ;; ── Build + test locally ─────────────────────────────────────────────────

    (info "Building ESM library ...")
    (shell "npm" "run" "build")
    (success "npm build passed.")

    (info "Building Clojars JAR ...")
    (shell "clojure" "-T:build" "jar")
    (success "JAR build passed.")

    (if (= "1" (System/getenv "SKIP_TESTS"))
      (warn "Skipping tests (SKIP_TESTS=1).")
      (do
        (info "Running tests ...")
        (shell "npm" "test")
        (success "Tests passed.")))

    ;; ── Commit, tag, push ────────────────────────────────────────────────────

    (shell "git" "add" "package.json" "build.clj" "deps.edn" "CHANGELOG.md")
    (shell "git" "commit" "-m" (str "chore: release v" new-version))

    (let [tag (str "v" new-version)]
      (try
        (shell "git" "tag" "-s" tag "-m" (str "Release " tag))
        (catch Exception _
          (shell "git" "tag" tag "-m" (str "Release " tag))))
      (shell "git" "push" "origin" "main")
      (shell "git" "push" "origin" tag))

    (success "────────────────────────────────────────────────")
    (success " Released v" new-version)
    (success " GitHub Actions will publish to NPM and Clojars.")
    (success " https://github.com/vanelsas/baredom/actions")
    (success "────────────────────────────────────────────────")))
