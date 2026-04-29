#!/usr/bin/env bb
;; generate_react.bb — Auto-generate React 19 wrapper components from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_react.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def react-src-dir "adapters/react/src")
(def react-pkg    "adapters/react/package.json")

;; ── React-specific helpers ──────────────────────────────────────────────────

(defn kebab->camel
  "Convert kebab-case to camelCase.
   e.g. 'max-items' -> 'maxItems', 'timeout-ms' -> 'timeoutMs'"
  [s]
  (let [parts (str/split s #"-")]
    (str (first parts)
         (str/join (map str/capitalize (rest parts))))))

(defn resolve-event-name
  "Resolve the actual DOM event name from an event-schema entry.
   Handles both patterns:
   - Symbol key: resolved via string-defs (e.g. event-press -> 'press')
   - Keyword key with :event-name: uses :event-name field
   - Keyword key without :event-name: uses (name key)"
  [event-key event-info string-defs]
  (cond
    (symbol? event-key) (resolve-sym event-key (or string-defs {}))
    (keyword? event-key) (or (:event-name event-info) (name event-key))
    :else (str event-key)))

(defn event->react-prop
  "Convert a DOM event name to a React callback prop name.
   Strips the tag-name prefix if present, then camelCases.
   e.g. 'press' -> 'onPress'
        'x-alert-dismiss' (tag 'x-alert') -> 'onDismiss'
        'value-change' -> 'onValueChange'"
  [event-str tag-name]
  (let [prefix  (str tag-name "-")
        stripped (if (str/starts-with? event-str prefix)
                  (subs event-str (count prefix))
                  event-str)
        parts   (str/split stripped #"-")]
    (str "on" (str/join (map str/capitalize parts)))))

(defn prop-type->ts
  "Convert a property metadata type to TypeScript prop type."
  [prop-meta]
  (let [t (normalize-type-sym (:type prop-meta))]
    (case t
      "boolean" "boolean"
      "string"  "string"
      "number"  "number"
      "object"  "Record<string, any>"
      "any")))

(defn event-detail->ts
  "Generate TypeScript type for a CustomEvent detail.
   Converts kebab-case keys to camelCase for valid TypeScript."
  [detail]
  (cond
    (or (nil? detail) (and (coll? detail) (empty? detail)))
    "{}"

    (set? detail)
    (let [fields (map #(str (kebab->camel (name %)) ": string") (sort detail))]
      (str "{ " (str/join "; " fields) " }"))

    (map? detail)
    (let [fields (map (fn [[k v]]
                        (str (kebab->camel (name k)) ": " (cljs-type->ts v)))
                      detail)]
      (str "{ " (str/join "; " fields) " }"))

    :else "{}"))

;; ── Code generation ─────────────────────────────────────────────────────────

(defn generate-wrapper
  "Generate a complete .tsx wrapper file for a component."
  [{:keys [tag-name properties events methods string-defs]}]
  (let [interface-name (tag->interface-name tag-name)
        sdefs          (or string-defs {})
        ;; Properties: filter out readonly, build props
        writable-props (when properties
                         (->> properties
                              (remove (fn [[_ m]] (:readonly m)))
                              (remove (fn [[_ m]] (:read-only m)))))
        ;; Events: resolve names and build React prop mappings
        event-entries  (when (and events (seq events))
                         (mapv (fn [[event-key event-info]]
                                 (let [dom-name   (resolve-event-name event-key event-info sdefs)
                                       react-prop (event->react-prop dom-name tag-name)
                                       detail     (:detail event-info)]
                                   {:dom-name   dom-name
                                    :react-prop react-prop
                                    :detail-ts  (event-detail->ts detail)}))
                               events))
        ;; Build prop interface lines
        prop-lines (concat
                    ;; Properties from property-api
                    (when writable-props
                      (map (fn [[k m]]
                             (str "  " (kebab->camel (name k)) "?: " (prop-type->ts m) ";"))
                           writable-props))
                    ;; Event handler props
                    (when event-entries
                      (map (fn [{:keys [react-prop detail-ts]}]
                             (str "  " react-prop "?: (e: CustomEvent<" detail-ts ">) => void;"))
                           event-entries))
                    ;; Standard React props
                    ["  children?: React.ReactNode;"
                     "  className?: string;"
                     "  style?: React.CSSProperties;"
                     "  id?: string;"
                     "  slot?: string;"])
        ;; Event prop names for destructuring
        event-prop-names (when event-entries
                           (map :react-prop event-entries))
        ;; All prop names to destructure (events + children)
        destructured (concat (or event-prop-names []) ["children"])
        ;; useEffect event binding blocks
        effect-blocks (when event-entries
                        (map (fn [{:keys [dom-name react-prop]}]
                               (str "      if (" react-prop ") {\n"
                                    "        el.addEventListener(\"" dom-name "\", " react-prop " as EventListener);\n"
                                    "        cleanup.push(() => el.removeEventListener(\"" dom-name "\", " react-prop " as EventListener));\n"
                                    "      }"))
                             event-entries))
        ;; useEffect dependency array
        effect-deps (if event-entries
                      (str "[" (str/join ", " (map :react-prop event-entries)) "]")
                      "[]")]
    (let [has-events (boolean (seq event-entries))
          react-imports (if has-events
                          "forwardRef, useRef, useEffect"
                          "forwardRef, useRef")]
    (str "\"use client\";\n"
         "// " tag-name ".tsx — auto-generated by generate_react.bb, do not edit\n\n"
         "import React, { " react-imports " } from \"react\";\n"
         "import type { " interface-name " as " interface-name "Element } from \"@vanelsas/baredom/" tag-name "\";\n"
         "import { init } from \"@vanelsas/baredom/" tag-name "\";\n\n"
         "init();\n\n"
         "export interface " interface-name "Props {\n"
         (str/join "\n" prop-lines) "\n"
         "}\n\n"
         "export const " interface-name " = forwardRef<" interface-name "Element, " interface-name "Props>(\n"
         "  function " interface-name "(props, forwardedRef) {\n"
         "    const { " (str/join ", " destructured) ", ...rest } = props;\n"
         "    const innerRef = useRef<" interface-name "Element>(null);\n\n"
         "    const setRef = (el: " interface-name "Element | null) => {\n"
         "      innerRef.current = el;\n"
         "      if (typeof forwardedRef === \"function\") forwardedRef(el);\n"
         "      else if (forwardedRef) forwardedRef.current = el;\n"
         "    };\n\n"
         (if event-entries
           (str "    useEffect(() => {\n"
                "      const el = innerRef.current;\n"
                "      if (!el) return;\n"
                "      const cleanup: Array<() => void> = [];\n\n"
                (str/join "\n" effect-blocks) "\n\n"
                "      return () => cleanup.forEach(fn => fn());\n"
                "    }, " effect-deps ");\n\n")
           "")
         "    return <" tag-name " ref={setRef} {...rest}>{children}</" tag-name ">;\n"
         "  }\n"
         ");\n"))))

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (str "// index.ts — auto-generated by generate_react.bb, do not edit\n\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (let [iface (tag->interface-name tag-name)]
                          (str "export { " iface ", type " iface "Props } from \"./" tag-name "\";")))
                      (sort-by :tag-name models)))
       "\n"))

(defn update-package-exports
  "Update the adapters/react/package.json exports field."
  [models]
  (let [pkg     (json/parse-string (slurp react-pkg) true)
        exports (into (sorted-map)
                      (concat
                       [["." {:import "./dist/index.js"
                              :types  "./dist/index.d.ts"}]]
                       (map (fn [{:keys [tag-name]}]
                              [(str "./" tag-name)
                               {:import (str "./dist/" tag-name ".js")
                                :types  (str "./dist/" tag-name ".d.ts")}])
                            (sort-by :tag-name models))))
        updated (assoc pkg :exports exports)]
    (spit react-pkg (json/generate-string updated {:pretty true}))))

;; ── Main ────────────────────────────────────────────────────────────────────
(defn -main []
  (println "Generating React 19 wrapper components...")

  (.mkdirs (io/file react-src-dir))

  (let [all-models     (discover-models)
        exported-names (load-package-exports)
        exported-models (filter #(contains? exported-names (:tag-name %)) all-models)]

    ;; Generate per-component .tsx
    (doseq [model exported-models]
      (let [tsx-content (generate-wrapper model)
            tsx-file    (io/file react-src-dir (str (:tag-name model) ".tsx"))]
        (spit tsx-file tsx-content)))

    ;; Generate barrel index.ts
    (spit (io/file react-src-dir "index.ts")
          (generate-index exported-models))

    ;; Update package.json exports
    (update-package-exports exported-models)

    (println (str "Generated " (count exported-models) " React wrappers + index.ts"))))

(-main)
