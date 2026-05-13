#!/usr/bin/env bb
;; Diff each component's installed prototype methods against its
;; model/method-api declaration. Flags two drift classes:
;;
;;   1. method-api is nil/empty but methods ARE installed
;;      (R1/R2 from batch FINAL: x_toast :dismiss, x_tooltip :show/:hide)
;;
;;   2. method-api lists a method that isn't installed, or installs a
;;      method that isn't listed.
;;
;; "Installed" means the component's <name>.cljs prototype-setup
;; function uses one of:
;;
;;     (set! (.-NAME proto) ...)
;;     (aset proto "NAME" ...)
;;     (.defineProperty js/Object proto "NAME" #js {:value ...})
;;
;; Usage:  bb scripts/check_method_api.bb
;; Exit: 0 if clean, 1 if any drift detected. Designed for CI.

(ns check-method-api
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private components-root "src/baredom/components")

(defn- read-file [path]
  (when (.exists (io/file path))
    (slurp path)))

(defn- list-components []
  (->> (.listFiles (io/file components-root))
       (filter #(.isDirectory ^java.io.File %))
       (map #(.getName ^java.io.File %))
       (filter #(str/starts-with? % "x_"))
       sort))

;; ── Extract method-api from model.cljs ───────────────────────────────────────

(defn- extract-method-api-text
  "Return the textual form (def method-api X) value, or nil. Handles
   both `(def method-api nil)` and `(def method-api {...})`."
  [source]
  (when-let [start (str/index-of source "(def method-api")]
    (let [;; Find what follows "method-api"
          tail-start (+ start (count "(def method-api"))]
      (loop [i tail-start depth 0 in-str? false start-val nil]
        (if (>= i (count source))
          nil
          (let [c (.charAt source i)]
            (cond
              in-str? (if (= c \") (recur (inc i) depth false start-val)
                                   (recur (inc i) depth true start-val))
              (= c \") (recur (inc i) depth true (or start-val i))
              (= c \space) (if (nil? start-val)
                             (recur (inc i) depth false nil)
                             (recur (inc i) depth false start-val))
              (= c \newline) (if (nil? start-val)
                               (recur (inc i) depth false nil)
                               (recur (inc i) depth false start-val))
              (= c \() (recur (inc i) (inc depth) false (or start-val i))
              (= c \{) (recur (inc i) (inc depth) false (or start-val i))
              (= c \[) (recur (inc i) (inc depth) false (or start-val i))
              (= c \)) (cond
                         (zero? depth) (when start-val
                                         (subs source start-val i))
                         (= depth 1) (when start-val
                                       (subs source start-val (inc i)))
                         :else (recur (inc i) (dec depth) false start-val))
              (= c \}) (if (= depth 1)
                         (when start-val
                           (subs source start-val (inc i)))
                         (recur (inc i) (dec depth) false start-val))
              (= c \]) (if (= depth 1)
                         (when start-val
                           (subs source start-val (inc i)))
                         (recur (inc i) (dec depth) false start-val))
              :else (recur (inc i) depth false (or start-val i)))))))))

(defn- parse-method-api [text]
  (when (and text (not (str/blank? text)))
    (try
      (edn/read-string {:default (fn [_tag v] v)} text)
      (catch Exception _ ::parse-error))))

(defn- method-api-method-names
  "Return set of method names (as strings) declared in method-api."
  [api]
  (cond
    (nil? api)         #{}
    (= api ::parse-error) ::parse-error
    (map? api)         (set (map name (keys api)))
    :else              #{}))

;; ── Extract installed methods from <name>.cljs ───────────────────────────────

(def ^:private method-install-patterns
  ;; Each pattern's match group 1 is the method name.
  [#"\(set!\s+\(\.-([A-Za-z][A-Za-z0-9_]*)\s+proto\)"
   #"\(aset\s+proto\s+\"([A-Za-z][A-Za-z0-9_]*)\""
   ;; .defineProperty js/Object proto "NAME" #js {:value ...}
   ;; The :value key only appears in METHOD installs (not get/set accessors).
   #"\.defineProperty\s+js/Object\s+proto\s+\"([A-Za-z][A-Za-z0-9_]*)\"\s*\n?\s*#js\s*\{[^}]*:value"])

(defn- strip-line-comments
  "Remove `;`-prefixed line comments so the install-pattern regexes
   don't match install-looking syntax that appears inside an
   explanatory comment. Preserves `;` characters inside string
   literals."
  [source]
  (let [sb (StringBuilder.)
        len (count source)]
    (loop [i 0 in-str? false]
      (if (>= i len)
        (str sb)
        (let [c (.charAt source i)]
          (cond
            in-str?
            (do (.append sb c)
                (cond
                  (= c \\) (when (< (inc i) len)
                             (.append sb (.charAt source (inc i))))
                  :else nil)
                (recur (if (and (= c \\) (< (inc i) len))
                         (+ i 2) (inc i))
                       (if (= c \") false true)))

            (= c \")
            (do (.append sb c) (recur (inc i) true))

            (= c \;)
            (let [nl (.indexOf source "\n" i)]
              (if (neg? nl)
                (str sb)
                (recur nl false)))

            :else
            (do (.append sb c) (recur (inc i) false))))))))

(defn- extract-installed-methods [source]
  (when source
    (let [code (strip-line-comments source)]
      (->> method-install-patterns
           (mapcat (fn [p] (map second (re-seq p code))))
           set))))

;; ── Compare ──────────────────────────────────────────────────────────────────

(defn- check-component [comp-name]
  (let [comp-path  (str components-root "/" comp-name "/" comp-name ".cljs")
        model-path (str components-root "/" comp-name "/model.cljs")
        comp-src   (read-file comp-path)
        model-src  (read-file model-path)]
    (cond
      (nil? comp-src)  {:status :skip :reason "no component file"}
      (nil? model-src) {:status :skip :reason "no model file"}
      :else
      (let [api-text  (extract-method-api-text model-src)
            api       (parse-method-api api-text)
            declared  (method-api-method-names api)
            installed (extract-installed-methods comp-src)]
        (cond
          (= declared ::parse-error)
          {:status :error
           :messages [(str "method-api in " model-path " could not be parsed")]}

          (and (empty? declared) (seq installed))
          {:status :error
           :messages [(str "model.cljs declares method-api nil/empty, but "
                           "component installs: " (sort installed))
                      "  Add the methods to model.cljs `method-api` so the "
                      "d.ts generator and adapters see them."]}

          :else
          (let [missing-decl (clojure.set/difference installed declared)
                missing-impl (clojure.set/difference declared installed)
                msgs (cond-> []
                       (seq missing-decl)
                       (conj (str "installed but not declared in method-api: "
                                  (sort missing-decl)))
                       (seq missing-impl)
                       (conj (str "declared in method-api but not installed: "
                                  (sort missing-impl))))]
            (if (seq msgs)
              {:status :error :messages msgs}
              {:status :ok})))))))

;; ── Main ─────────────────────────────────────────────────────────────────────

(defn -main [& _]
  (let [components (list-components)
        results    (mapv (fn [c] [c (check-component c)]) components)
        errors     (filter (fn [[_ r]] (= :error (:status r))) results)]
    (println "Checked" (count results) "components for method-api drift")
    (doseq [[c r] errors]
      (println)
      (println "✗" c)
      (doseq [m (:messages r)]
        (println " " m)))
    (println)
    (if (seq errors)
      (do (println "FAIL —" (count errors) "component(s) with method-api drift")
          (System/exit 1))
      (println "OK — no method-api drift"))))

(apply -main *command-line-args*)
