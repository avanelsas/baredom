(ns smoke-build
  "End-to-end release smoke: prove a scaffolded app actually COMPILES against the
   real, published-shape barebuild-* ESM — the one thing the fast `bb test` and
   the dev demos don't exercise. Run from barebuild/: `bb smoke-build`.

   Flow: build dist (release lib) → npm pack the repo → scaffold an app to a temp
   dir → npm install the local tarball OVER the registry pin (no published version
   has barebuild-* yet) → shadow-cljs release app → assert public/js/main.js."
  (:require [babashka.process :refer [shell]]
            [babashka.fs :as fs]
            [cheshire.core :as json]
            [barebuild-new :as scaffold]))

(defn- repo-root
  "The repo root = the parent of the barebuild/ cwd. fs/parent (not a literal
   parent-relative path, which the boundary check forbids in barebuild/ code)."
  []
  (str (fs/parent (fs/cwd))))

(defn -main []
  (let [root    (repo-root)
        tmp     (str (fs/create-temp-dir {:prefix "bb-smoke-"}))
        app-dir (str (fs/path tmp "smoke-app"))
        tgz-abs (atom nil)]
    (try
      (println "→ build dist (release lib)")
      (shell {:dir root} "npx" "shadow-cljs" "release" "lib")

      (println "→ npm pack")
      (let [out (:out (shell {:dir root :out :string} "npm" "pack" "--json" "--ignore-scripts"))
            tgz (-> (json/parse-string out true) first :filename)]
        (reset! tgz-abs (str (fs/path root tgz)))
        (println "  packed" @tgz-abs))

      (println "→ scaffold smoke-app")
      (scaffold/scaffold! {:app-name "smoke-app" :template-dir "templates/app" :target-dir app-dir})

      (println "→ npm install (local tarball over the ^3.4.0 pin) + shadow build")
      (shell {:dir app-dir} "npm" "install" @tgz-abs "shadow-cljs@^3.4.7")
      (shell {:dir app-dir} "npx" "shadow-cljs" "release" "app")

      (let [main-js (fs/path app-dir "public" "js" "main.js")]
        (if (fs/exists? main-js)
          (println (str "✓ smoke-build PASS — scaffolded app compiled against barebuild-* ("
                        (fs/size main-js) " bytes main.js)"))
          (do (println "✗ smoke-build FAIL — no public/js/main.js emitted")
              (System/exit 1))))
      (finally
        (fs/delete-tree tmp)
        (when @tgz-abs (fs/delete-if-exists @tgz-abs))))))
