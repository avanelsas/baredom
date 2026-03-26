(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib     'io.github.vanelsas/baredom)
(def version "0.1.0-alpha")
(def class-dir "target/classes")
(def jar-file  (format "target/%s-%s.jar" (name lib) version))

(defn jar [_]
  (println "Building" jar-file "...")
  (b/delete {:path class-dir})
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :src-dirs  ["src"]
                :scm       {:url                 "https://github.com/vanelsas/baredom"
                            :connection           "scm:git:git://github.com/vanelsas/baredom.git"
                            :developerConnection  "scm:git:ssh://git@github.com/vanelsas/baredom.git"
                            :tag                 (str "v" version)}})
  (b/copy-dir {:src-dirs   ["src"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file})
  (println "Done:" jar-file))

(defn deploy [_]
  (jar nil)
  (println "Deploying to Clojars...")
  (b/process {:command-args ["clojure" "-X:deploy"]}))
