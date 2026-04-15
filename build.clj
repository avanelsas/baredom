(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib       'com.github.avanelsas/baredom)
(def version   "2.2.0")
(def class-dir "target/classes")
(def jar-file  (format "target/%s-%s.jar" (name lib) version))
(def pom-file  (format "target/classes/META-INF/maven/%s/%s/pom.xml"
                       (namespace lib) (name lib)))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn jar [_]
  (println "Building" jar-file "...")
  (b/delete {:path class-dir})
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     @basis
                :src-dirs  ["src"]
                :scm       {:url                "https://github.com/avanelsas/baredom"
                            :connection          "scm:git:git://github.com/avanelsas/baredom.git"
                            :developerConnection "scm:git:ssh://git@github.com/avanelsas/baredom.git"
                            :tag                (str "v" version)}
                :pom-data  [[:description "Native Web Components with centralised theming — stateless, framework-free, zero runtime. Built with ClojureScript, compiled to tree-shakeable ESM."]
                            [:url "https://github.com/avanelsas/baredom"]
                            [:licenses
                             [:license
                              [:name "MIT License"]
                              [:url "https://opensource.org/licenses/MIT"]]]
                            [:developers
                             [:developer
                              [:name "Alexander van Elsas"]]]]})
  (b/copy-dir {:src-dirs   ["src"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file})
  (b/copy-file {:src    pom-file
                :target "pom.xml"})
  (println "Done:" jar-file))

(defn deploy [_]
  (jar nil)
  (println "Deploying to Clojars...")
  (b/process {:command-args ["clojure" "-X:deploy"]}))
