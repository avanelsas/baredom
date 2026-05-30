(ns barebuild-new
  "Scaffold a BareBuild read-side app from templates/app.

   Run from the barebuild/ directory: `bb new <app-name> [parent-dir]`.
   Babashka built-ins only (no external deps), modelled on scripts/release.clj.

   NB: the ns is barebuild-rooted (not `barebuild.cli.new`) on purpose — bb.edn
   lives in barebuild/ and its :paths are relative to it, so a `barebuild.*` ns
   would need `:paths [\"..\"]`, and the boundary check forbids any `..` here."
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private token "APP_NAME")
;; Relative to the barebuild/ cwd (bb runs tasks from the bb.edn dir). Avoids any
;; parent-relative path (boundary-check rule) and fragile *file* resolution.
(def ^:private default-template-dir "templates/app")

(defn- die [& msg]
  (binding [*out* *err*]
    (println (str "barebuild new: " (apply str msg))))
  (System/exit 1))

(defn valid-name?
  "An app name usable as both an npm package name and a CLJS ns segment:
   hyphen-separated runs of lowercase letters / digits, starting with a letter.
   Hyphens must be internal separators — no leading, trailing, or doubled hyphen
   (those make an invalid npm name and an ugly munged dir like 'my_app_')."
  [s]
  (boolean (and (string? s) (re-matches #"[a-z][a-z0-9]*(-[a-z0-9]+)*" s))))

(defn- munge-dir
  "ClojureScript munges '-' to '_' in file paths; the ns itself keeps the hyphen."
  [app-name]
  (str/replace app-name "-" "_"))

(defn scaffold!
  "Copy template-dir → target-dir, substituting the APP_NAME token in file
   CONTENTS (hyphen name, e.g. my-app) and in PATH segments (munged dir name,
   e.g. my_app). Returns the number of files written."
  [{:keys [app-name template-dir target-dir]}]
  (let [munged (munge-dir app-name)
        files  (filter #(.isFile ^java.io.File %) (file-seq (io/file template-dir)))]
    ;; Template files are UTF-8 text by contract — this slurp/replace/spit round-trip
    ;; would corrupt a binary asset. None exist today; add a byte-copy branch here if
    ;; one ever does (e.g. a favicon).
    (doseq [^java.io.File f files]
      (let [rel      (str (fs/relativize template-dir (.getPath f)))
            out-file (fs/path target-dir (str/replace rel token munged))
            content  (str/replace (slurp f) token app-name)]
        (fs/create-dirs (fs/parent out-file))
        (spit (fs/file out-file) content)))
    (count files)))

(defn -main [& args]
  (let [[app-name parent-dir] args]
    (when-not (valid-name? app-name)
      (die "invalid app name " (pr-str app-name)
           " — hyphen-separated lowercase letters/digits, starting with a letter"
           " (e.g. my-app). No leading, trailing, or doubled hyphen.\n"
           "  usage: bb new <app-name> [parent-dir]"))
    (when-not (fs/exists? default-template-dir)
      (die "template dir not found: " default-template-dir
           " (run this from the barebuild/ directory)"))
    (let [target (str (fs/path (or parent-dir (str (fs/cwd))) app-name))]
      (when (fs/exists? target)
        (die "target already exists: " target))
      (let [n (scaffold! {:app-name     app-name
                          :template-dir default-template-dir
                          :target-dir   target})]
        (println (str "✓ created '" app-name "' (" n " files) → " target))
        (println "  next:")
        (println (str "    cd " target))
        (println "    bb dev        # installs deps + serves http://localhost:8000")))))
