#!/usr/bin/env bb
;; The plan and the templates of BareMirror are pure. This runs their tests
;; with babashka, so that "tested with no browser" is checked.
;; A test file that ends in .cljc runs here. One that ends in .cljs needs a browser.
;;
;; Usage:  bb scripts/test_baremirror_pure.bb
;; Exit:   0 if every test passes, 1 otherwise. Designed for CI.

(ns test-baremirror-pure
  (:require [babashka.classpath :as classpath]
            [babashka.deps :as deps]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :as test]))

(def ^:private source-root "baremirror/src")
(def ^:private test-root "baremirror/test")
(def ^:private test-check 'org.clojure/test.check)

(defn- test-check-version
  "The version of test.check that the browser tests use."
  []
  (->> (:dependencies (edn/read-string (slurp "shadow-cljs.edn")))
       (filter (comp #{test-check} first))
       first
       second))

(defn- namespace-of
  "The namespace that the test file `path` holds."
  [path]
  (-> (str (fs/strip-ext (fs/relativize test-root path)))
      (str/replace fs/file-separator ".")
      (str/replace "_" "-")
      symbol))

(defn- test-namespaces
  "The test namespaces that run with no browser."
  []
  (sort (map namespace-of (fs/glob test-root "**/*_test.cljc"))))

(defn- run-tests!
  "Loads and runs `namespaces`. Returns the number of tests that did not pass."
  [namespaces]
  (apply require namespaces)
  (let [{:keys [fail error]} (apply test/run-tests namespaces)]
    (+ fail error)))

(deps/add-deps {:deps {test-check {:mvn/version (test-check-version)}}})
(run! classpath/add-classpath [source-root test-root])
(System/exit (if (zero? (run-tests! (test-namespaces))) 0 1))
