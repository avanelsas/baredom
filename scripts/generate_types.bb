#!/usr/bin/env bb
;; generate_types.bb — Auto-generate .d.ts type declarations and
;; custom-elements.json from BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_types.bb

(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[cheshire.core :as json])

;; ── Configuration ───────────────────────────────────────────────────────────
(def components-dir "src/baredom/components")
(def exports-dir    "src/baredom/exports")
(def dist-dir       "dist")
(def package-json   "package.json")

;; ── Type mapping ────────────────────────────────────────────────────────────
(defn normalize-type-sym
  "Normalize a type value from EDN. Handles:
   - symbol 'boolean (quote prefix as part of name)
   - list (quote boolean)
   - bare symbol boolean"
  [v]
  (cond
    (and (symbol? v) (str/starts-with? (str v) "'"))
    (subs (str v) 1)

    (and (list? v) (= (first v) 'quote))
    (str (second v))

    (symbol? v)
    (str v)

    (string? v)
    v

    :else (str v)))

(defn cljs-type->ts [v]
  (case (normalize-type-sym v)
    "string"  "string"
    "boolean" "boolean"
    "number"  "number"
    "object"  "Record<string, any>"
    "void"    "void"
    "any"))

;; ── Naming conventions ──────────────────────────────────────────────────────
(defn tag->interface-name
  "Convert 'x-button' to 'XButton'."
  [tag-name]
  (->> (str/split tag-name #"-")
       (map str/capitalize)
       (str/join)))

(defn tag->module-name
  "Convert 'x-button' to 'x-button' (identity — used for file names)."
  [tag-name]
  tag-name)

;; ── EDN parsing helpers ─────────────────────────────────────────────────────
(defn preprocess-cljs
  "Strip ClojureScript-specific syntax for EDN parsing."
  [s]
  (-> s
      (str/replace #"#js\s*\[" "[")
      (str/replace #"#js\s*\{" "{")
      (str/replace #"\^:private\s+" "")
      (str/replace #"\^:export\s+" "")))

(defn extract-def
  "Extract the value of a (def name ...) form from file text.
   Returns the raw string of the value, or nil if not found."
  [text def-name]
  (let [;; Match (def name <value>) handling multi-line forms
        pattern (re-pattern (str "\\(def\\s+" (java.util.regex.Pattern/quote def-name) "\\s+"))
        matcher (re-matcher pattern text)]
    (when (.find matcher)
      (let [start (.end matcher)
            ;; Find the balanced closing paren
            chars (seq (subs text start))
            result (loop [cs chars depth 0 acc [] in-string false escape false]
                     (if (empty? cs)
                       (str/join acc)
                       (let [c (first cs)]
                         (cond
                           escape
                           (recur (rest cs) depth (conj acc c) in-string false)

                           (= c \\)
                           (recur (rest cs) depth (conj acc c) in-string true)

                           (and (= c \") (not escape))
                           (recur (rest cs) depth (conj acc c) (not in-string) false)

                           in-string
                           (recur (rest cs) depth (conj acc c) in-string false)

                           (or (= c \() (= c \[) (= c \{))
                           (recur (rest cs) (inc depth) (conj acc c) in-string false)

                           (or (= c \)) (= c \]) (= c \}))
                           (if (zero? depth)
                             (str/join acc) ;; found the closing paren of the def
                             (recur (rest cs) (dec depth) (conj acc c) in-string false))

                           :else
                           (recur (rest cs) depth (conj acc c) in-string false)))))]
        (str/trim result)))))

(defn parse-def-value
  "Extract and parse a def value from file text as EDN."
  [text def-name]
  (when-let [raw (extract-def text def-name)]
    (try
      (edn/read-string (preprocess-cljs raw))
      (catch Exception _e
        nil))))

(defn extract-tag-name
  "Extract tag-name string from model file text."
  [text]
  (second (re-find #"\(def\s+tag-name\s+\"([^\"]+)\"" text)))

(defn extract-observed-attributes
  "Extract observed-attributes as a vector of strings."
  [text]
  (when-let [raw (extract-def text "observed-attributes")]
    (let [processed (preprocess-cljs raw)]
      ;; The array contains variable references like attr-type — extract string
      ;; literals that are resolved from the def constants
      ;; Strategy: find all (def attr-* "...") in the file and resolve
      (let [attr-defs (re-seq #"\(def\s+(?:\^:private\s+)?(\S+)\s+\"([^\"]+)\"\)" text)
            attr-map  (into {} (map (fn [[_ k v]] [(symbol k) v]) attr-defs))
            ;; Parse the array form
            syms (try (edn/read-string processed) (catch Exception _ nil))]
        (if (sequential? syms)
          (mapv (fn [s]
                  (if (string? s) s
                      (get attr-map s (str s))))
                syms)
          ;; Fallback: extract strings directly
          (vec (map second (re-seq #"\"([^\"]+)\"" raw))))))))

(defn extract-slots
  "Extract slot name constants (def slot-* \"...\") from model text."
  [text]
  (mapv second (re-seq #"\(def\s+slot-\S+\s+\"([^\"]+)\"\)" text)))

(defn extract-string-defs
  "Build a map of symbol -> string for all (def name \"value\") forms."
  [text]
  (into {} (map (fn [[_ k v]] [(symbol k) v])
                (re-seq #"\(def\s+(?:\^:private\s+)?(\S+)\s+\"([^\"]+)\"\)" text))))

;; ── Model discovery ─────────────────────────────────────────────────────────
(defn discover-models
  "Find all model.cljs files and extract metadata."
  []
  (let [comps-dir (io/file components-dir)]
    (->> (.listFiles comps-dir)
         (filter #(.isDirectory %))
         (map (fn [dir]
                (let [model-file (io/file dir "model.cljs")]
                  (when (.exists model-file)
                    (let [text (slurp model-file)
                          tag  (extract-tag-name text)]
                      (when tag
                        {:tag-name    tag
                         :dir-name    (.getName dir)
                         :properties  (parse-def-value text "property-api")
                         :events      (parse-def-value text "event-schema")
                         :methods     (parse-def-value text "method-api")
                         :attributes  (extract-observed-attributes text)
                         :slots       (extract-slots text)
                         :string-defs (extract-string-defs text)}))))))
         (remove nil?)
         (vec))))

(defn load-package-exports
  "Read package.json and return the set of exported component names (e.g. #{\"x-button\" ...})."
  []
  (let [pkg (json/parse-string (slurp package-json) true)]
    (->> (:exports pkg)
         keys
         (map name)  ;; :./x-button -> "x-button" (Clojure treats ./ as namespace)
         (filter #(str/starts-with? % "x-"))
         set)))

;; ── Symbol resolution ────────────────────────────────────────────────────────
(defn resolve-sym
  "Resolve a symbol to its string value using the string-defs map.
   Falls back to (str sym) if not found."
  [sym string-defs]
  (if (symbol? sym)
    (or (get string-defs sym) (str sym))
    (str sym)))

;; ── .d.ts generation ────────────────────────────────────────────────────────
(defn generate-event-detail-type
  "Generate TypeScript type for an event detail.
   Handles both map format {:key 'type} and set format #{:key1 :key2}."
  [detail]
  (cond
    (or (nil? detail) (and (coll? detail) (empty? detail)))
    "{}"

    (set? detail)
    ;; Set of keywords — treat each as string-typed field
    (let [fields (map #(str (name %) ": string") (sort detail))]
      (str "{ " (str/join "; " fields) " }"))

    (map? detail)
    (let [fields (map (fn [[k v]]
                        (str (name k) ": " (cljs-type->ts v)))
                      detail)]
      (str "{ " (str/join "; " fields) " }"))

    :else "{}"))

(defn generate-event-map
  "Generate the EventMap interface."
  [interface-name events string-defs]
  (when (and events (seq events))
    (let [map-name (str interface-name "EventMap")
          entries  (map (fn [[event-key event-info]]
                          (let [;; Resolve symbol key to its string value
                                event-str (resolve-sym event-key (or string-defs {}))
                                detail    (:detail event-info)]
                            (str "  \"" event-str "\": CustomEvent<" (generate-event-detail-type detail) ">;")))
                        events)]
      (str "export interface " map-name " {\n"
           (str/join "\n" entries)
           "\n}\n"))))

;; Properties that conflict with HTMLElement built-in methods/properties.
;; These are skipped in .d.ts output to avoid type incompatibilities.
(def ^:private html-element-conflicts
  #{"scrollTo" "scroll" "click" "focus" "blur" "animate"})

(defn generate-interface
  "Generate the element interface."
  [interface-name properties methods events]
  (let [has-events   (and events (seq events))
        event-map    (str interface-name "EventMap")
        prop-lines   (when properties
                       (->> properties
                            (remove (fn [[k _]] (contains? html-element-conflicts (name k))))
                            (map (fn [[k {:keys [type readonly read-only]}]]
                                   (let [ro (or readonly read-only)]
                                     (str "  " (when ro "readonly ") (name k) ": " (cljs-type->ts type) ";"))))))
        method-lines (when methods
                       (map (fn [[k {:keys [args returns]}]]
                              (let [params (when (seq args)
                                             (str/join ", " (map (fn [{:keys [name type]}]
                                                                   (str name ": " (cljs-type->ts type)))
                                                                 args)))
                                    ret    (cljs-type->ts (or returns 'void))]
                                (str "  " (clojure.core/name k) "(" (or params "") "): " ret ";")))
                            methods))
        listener-overloads
        (when has-events
          [(str "\n  addEventListener<K extends keyof " event-map ">(")
           (str "    type: K,")
           (str "    listener: (this: " interface-name ", ev: " event-map "[K]) => any,")
           (str "    options?: boolean | AddEventListenerOptions")
           (str "  ): void;")
           (str "  addEventListener(")
           (str "    type: string,")
           (str "    listener: EventListenerOrEventListenerObject,")
           (str "    options?: boolean | AddEventListenerOptions")
           (str "  ): void;")
           (str "  removeEventListener<K extends keyof " event-map ">(")
           (str "    type: K,")
           (str "    listener: (this: " interface-name ", ev: " event-map "[K]) => any,")
           (str "    options?: boolean | EventListenerOptions")
           (str "  ): void;")
           (str "  removeEventListener(")
           (str "    type: string,")
           (str "    listener: EventListenerOrEventListenerObject,")
           (str "    options?: boolean | EventListenerOptions")
           (str "  ): void;")])]
    (str "export interface " interface-name " extends HTMLElement {\n"
         (when (seq prop-lines) (str (str/join "\n" prop-lines) "\n"))
         (when (seq method-lines) (str (str/join "\n" method-lines) "\n"))
         (when (seq listener-overloads) (str (str/join "\n" listener-overloads) "\n"))
         "}\n")))

(defn generate-dts
  "Generate complete .d.ts content for a component."
  [model & {:keys [extra-exports child-models]}]
  (let [{:keys [tag-name properties events methods string-defs]} model
        interface-name (tag->interface-name tag-name)
        lines          [(str "// " tag-name ".d.ts — auto-generated by generate_types.bb, do not edit")
                        ""
                        "export function init(): void;"]]
    (str (str/join "\n" lines)
         "\n"
         ;; Extra exports (e.g., x-theme registerPreset)
         (when extra-exports
           (str "\n" (str/join "\n" extra-exports) "\n"))
         "\n"
         ;; Event map
         (when (and events (seq events))
           (str (generate-event-map interface-name events string-defs) "\n"))
         ;; Interface
         (generate-interface interface-name properties methods events)
         "\n"
         ;; Child component interfaces (compound components)
         (when child-models
           (str/join "\n"
                     (map (fn [child]
                            (let [child-iface (tag->interface-name (:tag-name child))]
                              (str (when (and (:events child) (seq (:events child)))
                                     (str (generate-event-map child-iface (:events child) (:string-defs child)) "\n"))
                                   (generate-interface child-iface (:properties child) (:methods child) (:events child))
                                   "\n")))
                          child-models)))
         ;; Global augmentation
         "declare global {\n"
         "  interface HTMLElementTagNameMap {\n"
         "    \"" tag-name "\": " interface-name ";\n"
         (when child-models
           (str/join ""
                     (map (fn [child]
                            (str "    \"" (:tag-name child) "\": " (tag->interface-name (:tag-name child)) ";\n"))
                          child-models)))
         "  }\n"
         "}\n")))

;; ── Custom Elements Manifest generation ─────────────────────────────────────
(defn generate-cem-module
  "Generate a CEM module entry for a component."
  [{:keys [tag-name properties events methods slots string-defs]}]
  (let [interface-name (tag->interface-name tag-name)
        sdefs (or string-defs {})
        members (vec (concat
                      (when properties
                        (map (fn [[k {:keys [type readonly read-only]}]]
                               (cond-> {:kind "field"
                                        :name (name k)
                                        :type {:text (cljs-type->ts type)}}
                                 (or readonly read-only) (assoc :readonly true)))
                             properties))
                      (when methods
                        (map (fn [[k {:keys [args returns]}]]
                               (cond-> {:kind   "method"
                                        :name   (name k)
                                        :return {:type {:text (cljs-type->ts (or returns 'void))}}}
                                 (seq args)
                                 (assoc :parameters
                                        (mapv (fn [{:keys [name type]}]
                                                {:name name :type {:text (cljs-type->ts type)}})
                                              args))))
                             methods))))
        cem-events (when events
                     (mapv (fn [[event-key _event-info]]
                             (let [en (resolve-sym event-key sdefs)]
                               {:name en
                                :type {:text "CustomEvent"}}))
                           events))
        cem-slots (if (seq slots)
                    (mapv (fn [s] (if (= s "default") {:name ""} {:name s})) slots)
                    [{:name ""}])]
    {:kind         "javascript-module"
     :path         (str "dist/" tag-name ".js")
     :declarations [{:kind       "class"
                     :name       interface-name
                     :tagName    tag-name
                     :superclass {:name "HTMLElement"}
                     :members    members
                     :events     (or cem-events [])
                     :slots      cem-slots}]
     :exports      [{:kind        "js"
                     :name        "init"
                     :declaration {:name "init"}}
                    {:kind        "custom-element-definition"
                     :name        tag-name
                     :declaration {:name interface-name}}]}))

;; ── Compound component detection ────────────────────────────────────────────
(def compound-children
  "Map of parent tag-name to child dir-names."
  {"x-welcome-tour"  ["x_welcome_tour_step"]
   "x-tabs"          ["x_tab"]
   "x-timeline"      ["x_timeline_item"]
   "x-table"         ["x_table_row" "x_table_cell"]})

(defn find-child-models
  "Find child component models for a compound parent."
  [parent-tag all-models]
  (when-let [child-dirs (get compound-children parent-tag)]
    (->> all-models
         (filter #(some #{(:dir-name %)} child-dirs))
         vec)))

;; ── Extra exports per component ─────────────────────────────────────────────
(def extra-export-signatures
  {"x-theme" ["export function registerPreset(presetName: string, data: Record<string, string>): void;"]})

;; ── Main ────────────────────────────────────────────────────────────────────
(defn -main []
  (println "Generating TypeScript declarations and Custom Elements Manifest...")

  ;; Ensure dist dir exists
  (.mkdirs (io/file dist-dir))

  (let [all-models      (discover-models)
        exported-names   (load-package-exports)
        ;; Components with their own package.json export
        exported-models  (filter #(contains? exported-names (:tag-name %)) all-models)
        ;; Components that are children of compound components (no own export)
        child-tag-names  (set (mapcat val compound-children))
        cem-modules      (atom [])
        global-entries   (atom [])]

    ;; Generate per-component .d.ts
    (doseq [model exported-models]
      (let [tag         (:tag-name model)
            children    (find-child-models tag all-models)
            extra       (get extra-export-signatures tag)
            dts-content (generate-dts model :extra-exports extra :child-models children)
            dts-file    (io/file dist-dir (str tag ".d.ts"))]
        (spit dts-file dts-content)
        ;; Collect for global .d.ts
        (swap! global-entries conj {:tag tag :interface (tag->interface-name tag)})
        (when children
          (doseq [child children]
            (swap! global-entries conj {:tag (:tag-name child) :interface (tag->interface-name (:tag-name child))})))
        ;; Collect for CEM
        (swap! cem-modules conj (generate-cem-module model))
        (when children
          (doseq [child children]
            (swap! cem-modules conj (generate-cem-module child))))))

    ;; Generate global baredom.d.ts
    (let [global-content
          (str "// baredom.d.ts — auto-generated by generate_types.bb, do not edit\n\n"
               ;; Re-exports from each component file
               (str/join "\n"
                         (map (fn [{:keys [tag interface]}]
                                ;; Only re-export from files that exist (exported components)
                                (when (contains? exported-names tag)
                                  (str "export { " interface " } from \"./" tag "\";")))
                              @global-entries))
               "\n")]
      (spit (io/file dist-dir "baredom.d.ts") global-content))

    ;; Generate custom-elements.json
    (let [cem {:schemaVersion "1.0.0"
               :readme        "README.md"
               :modules       @cem-modules}]
      (spit "custom-elements.json"
            (json/generate-string cem {:pretty true})))

    (println (str "Generated " (count exported-models) " .d.ts files, "
                  "baredom.d.ts, and custom-elements.json"))))

(-main)
