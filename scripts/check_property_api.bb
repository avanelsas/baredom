#!/usr/bin/env bb
;; Compare each component's imperative install-property-accessors! body
;; against its model/property-api map. Reports mismatches that would cause
;; behavior drift if we replaced the imperative install with
;; (du/install-properties! proto model/property-api).
;;
;; Usage: bb scripts/check_property_api.bb [component-name ...]
;; If no args, runs against the PB.1 candidate list.

(ns check-property-api
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:private pb1-candidates
  ["x_checkbox" "x_collapse" "x_color_picker" "x_combobox" "x_context_menu"
   "x_date_picker" "x_dropdown" "x_fieldset" "x_file_download" "x_form"
   "x_gaussian_blur" "x_navbar" "x_particle_button" "x_popover" "x_radio"
   "x_select" "x_sidebar" "x_skeleton_group" "x_slider" "x_spinner"
   "x_switch" "x_tooltip"])

;; ── Read & extract install calls ─────────────────────────────────────────────

(defn- read-file [path]
  (when (.exists (io/file path))
    (slurp path)))

(defn- extract-install-fn-body
  "Return the textual body of (defn- install-property-accessors! ...) or nil."
  [source]
  (when-let [start (str/index-of source "install-property-accessors!")]
    (let [tail (subs source start)
          ;; balanced-paren walk to find end of the defining form
          start-defn (str/last-index-of source "(defn" start)]
      (when start-defn
        (loop [i start-defn depth 0 in-str? false]
          (if (>= i (count source))
            nil
            (let [c (.charAt source i)]
              (cond
                in-str? (if (= c \") (recur (inc i) depth false)
                            (recur (inc i) depth true))
                (= c \") (recur (inc i) depth true)
                (= c \() (recur (inc i) (inc depth) false)
                (= c \)) (if (= depth 1)
                           (subs source start-defn (inc i))
                           (recur (inc i) (dec depth) false))
                :else (recur (inc i) depth false)))))))))

(defn- extract-define-prop-calls
  "Walk install-fn-body string and pull out each
   (du/define-X-prop! proto Y Z [W]) call as a parsed map."
  [body]
  (when body
    (let [pattern #"\(du/define-(bool|string|number)-prop!\s+proto\s+([^\s)]+)\s+([^\s)]+)(?:\s+([^)]+))?\)"
          matches (re-seq pattern body)]
      (mapv (fn [[_ helper-type prop-name attr-name default]]
              {:helper-type   helper-type
               :prop-name     (str/trim prop-name)
               :attr-name     (str/trim attr-name)
               :default       (some-> default str/trim)})
            matches))))

;; ── Parse model/property-api ─────────────────────────────────────────────────

(defn- extract-property-api-map
  "Return the literal map text for (def property-api {...}) or nil."
  [source]
  (when-let [start (str/index-of source "(def property-api")]
    (let [open-brace (str/index-of source \{ start)]
      (when open-brace
        (loop [i open-brace depth 0 in-str? false]
          (if (>= i (count source))
            nil
            (let [c (.charAt source i)]
              (cond
                in-str? (if (= c \") (recur (inc i) depth false)
                            (recur (inc i) depth true))
                (= c \") (recur (inc i) depth true)
                (= c \{) (recur (inc i) (inc depth) false)
                (= c \}) (if (= depth 1)
                           (subs source open-brace (inc i))
                           (recur (inc i) (dec depth) false))
                :else (recur (inc i) depth false)))))))))

(defn- parse-property-api
  "Parse the property-api map text into a Clojure data structure.
   Quoted symbols (`'string`) become symbols; bare attr-name refs stay symbols."
  [map-text]
  (when map-text
    (try
      (edn/read-string {:default (fn [_tag value] value)} map-text)
      (catch Exception e
        (println "  WARN: could not parse property-api:" (ex-message e))
        nil))))

;; ── Compare ──────────────────────────────────────────────────────────────────

(defn- helper-type->api-type
  "Map du-helper suffix to property-api :type symbol."
  [helper-type]
  (case helper-type
    "bool"   'boolean
    "string" 'string
    "number" 'number))

(defn- prop-key-from-name
  "Convert imperative prop-name string to keyword for property-api lookup.
   Imperative uses string like \"variant\" or model/attr-variant. We need to
   match on the keyword (`:variant`) used in property-api."
  [prop-name]
  (-> prop-name
      (str/replace #"^model/attr-" "")
      (str/replace #"^\"" "")
      (str/replace #"\"$" "")
      keyword))

(defn- norm-symbol-name
  "Strip a leading apostrophe from a symbol's name so EDN-read 'boolean
   compares equal to the literal symbol boolean."
  [sym]
  (let [s (str sym)]
    (if (str/starts-with? s "'") (subs s 1) s)))

(defn- norm-attr-name
  "Strip the model/attr- prefix and surrounding quotes so we can compare
   imperative attr arguments (model/attr-foo or \"foo\") against property-api
   :reflects-attribute values (which are symbols like attr-foo)."
  [s]
  (-> (str s)
      (str/replace #"^model/" "")
      (str/replace #"^\"" "")
      (str/replace #"\"$" "")))

(defn- compare-component
  "Compare imperative install vs property-api. Returns a vector of mismatches."
  [_comp-name install-calls property-api]
  (let [issues (atom [])
        api-keys (set (keys (or property-api {})))
        called-keys (set (mapv (comp prop-key-from-name :prop-name) install-calls))]
    ;; Calls without matching property-api entries
    (doseq [{:keys [helper-type prop-name attr-name default]} install-calls]
      (let [k (prop-key-from-name prop-name)]
        (if-let [spec (get property-api k)]
          (let [api-type-raw (:type spec)
                api-type      (norm-symbol-name api-type-raw)
                api-attr      (:reflects-attribute spec)
                api-attr-norm (norm-attr-name api-attr)
                imp-attr-norm (norm-attr-name attr-name)
                api-default   (:default spec)
                expected-type (str (helper-type->api-type helper-type))]
            (when (not= api-type expected-type)
              (swap! issues conj
                     (str "  " (name k) ": :type is " api-type-raw
                          ", helper installs " expected-type)))
            (when (or (not api-attr) (not= api-attr-norm imp-attr-norm))
              (swap! issues conj
                     (str "  " (name k) ": :reflects-attribute is "
                          (or api-attr "MISSING")
                          ", helper uses " attr-name)))
            ;; Default mismatch — only relevant for string/number
            (when (and default (#{"string" "number"} helper-type))
              (let [api-def-str (when (some? api-default) (pr-str api-default))]
                (when (not= default api-def-str)
                  (swap! issues conj
                         (str "  " (name k) ": helper passes default " default
                              ", property-api has " api-def-str))))))
          (swap! issues conj
                 (str "  " (name k) ": helper installs but no property-api entry")))))
    ;; Property-api entries with no matching imperative call
    (doseq [k (clojure.set/difference api-keys called-keys)]
      (when-let [spec (get property-api k)]
        (when (:reflects-attribute spec)
          (swap! issues conj
                 (str "  " (name k) ": property-api entry but no imperative install "
                      "(install-properties! WOULD install this — extra prop after conversion)")))))
    @issues))

;; ── Main ─────────────────────────────────────────────────────────────────────

(defn- check-component [comp-name]
  (let [comp-file  (str "src/baredom/components/" comp-name "/"
                        (str/replace comp-name #"_" "_") ".cljs")
        ;; Component file uses the same name with one segment
        comp-path  (str "src/baredom/components/" comp-name "/" comp-name ".cljs")
        model-path (str "src/baredom/components/" comp-name "/model.cljs")
        comp-source  (read-file comp-path)
        model-source (read-file model-path)]
    (cond
      (nil? comp-source)
      (println comp-name "  [MISSING component file:" comp-path "]")

      (nil? model-source)
      (println comp-name "  [MISSING model file:" model-path "]")

      :else
      (let [install-body  (extract-install-fn-body comp-source)
            install-calls (extract-define-prop-calls install-body)
            api-text      (extract-property-api-map model-source)
            api-map       (parse-property-api api-text)
            issues        (compare-component comp-name install-calls api-map)]
        (println "==" comp-name)
        (println "  imperative calls:" (count install-calls))
        (println "  property-api entries:" (count (or api-map {})))
        (if (seq issues)
          (do (println "  ⚠ MISMATCHES:")
              (doseq [i issues] (println i)))
          (println "  ✓ ready for Tier-0 conversion"))
        (println)))))

(defn -main [& args]
  (let [components (if (seq args) args pb1-candidates)]
    (println "Checking" (count components) "component(s) for Tier-0 readiness")
    (println "")
    (doseq [c components] (check-component c))))

(apply -main *command-line-args*)
