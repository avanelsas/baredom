(ns barebuild-new-test
  "Fast platform tests for the `barebuild new` scaffolder (no shadow compile — the
   end-to-end build is the separate `bb smoke-build` task). Run: `cd barebuild && bb test`."
  (:require [clojure.test :refer [deftest is testing run-tests]]
            [babashka.fs :as fs]
            [clojure.string :as str]
            [barebuild-new :as sut]))

(def ^:private template-dir "templates/app")   ; relative to the barebuild/ cwd

(deftest valid-name?-test
  (testing "accepts npm-/ns-safe names"
    (doseq [n ["app" "my-app" "a1" "foo-bar-baz" "x2y"]]
      (is (sut/valid-name? n) n)))
  (testing "rejects bad names (nil, empty, upper, leading digit/hyphen, trailing/doubled hyphen, underscores, dots)"
    (doseq [n [nil "" "My-App" "1app" "-app" "app-" "my--app" "app_x" "app.x" "AB"]]
      (is (not (sut/valid-name? n)) (pr-str n)))))

(deftest scaffold!-test
  (let [tmp    (str (fs/create-temp-dir))
        target (str (fs/path tmp "my-app"))
        n      (sut/scaffold! {:app-name "my-app" :template-dir template-dir :target-dir target})
        slurp* (fn [rel] (slurp (str (fs/path target rel))))]
    (try
      (testing "writes the expected files"
        (is (>= n 8) "at least the 8 template files")
        (doseq [f ["package.json" "shadow-cljs.edn" "bb.edn" "README.md" ".gitignore"
                   "public/index.html" "public/api/users.json" "src/my_app/core.cljs"]]
          (is (fs/exists? (fs/path target f)) f)))

      (testing "the APP_NAME token is fully substituted (none survives)"
        (doseq [f ["package.json" "shadow-cljs.edn" "src/my_app/core.cljs"
                   "README.md" "public/index.html" "bb.edn"]]
          (is (not (str/includes? (slurp* f) "APP_NAME")) f)))

      (testing "the src dir is munged ('-' → '_'); the hyphen form is absent"
        (is (fs/exists? (fs/path target "src" "my_app")))
        (is (not (fs/exists? (fs/path target "src" "my-app")))))

      (testing "substitution uses the hyphen name in contents"
        (is (str/includes? (slurp* "src/my_app/core.cljs") "(ns my-app.core"))
        (is (str/includes? (slurp* "package.json") "\"name\": \"my-app\""))
        (is (str/includes? (slurp* "shadow-cljs.edn") "my-app.core/init!")))

      (finally (fs/delete-tree tmp)))))

(defn -main []
  (let [{:keys [fail error]} (run-tests 'barebuild-new-test)]
    (System/exit (if (pos? (+ (or fail 0) (or error 0))) 1 0))))
