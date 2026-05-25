#!/usr/bin/env bb
;; generate_react.bb — Auto-generate React 19 wrapper components from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_react.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")
;; Load shared codegen helpers (resolve-event-name, prop-type->ts, event-detail->ts)
(load-file "scripts/codegen_shared.bb")
;; Load shared form-control metadata (single source of truth across all adapters)
(load-file "scripts/form-control-metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def react-src-dir "adapters/react/src")
(def react-pkg    "adapters/react/package.json")

;; Components that support controlled mode, derived from the shared
;; form-control metadata so adding a form control to BareDOM updates one
;; file and all four adapters pick it up.
;;
;; Mapping from form-controls schema to React's controlled shape:
;;   :control-prop          ← kebab->camel(:attr-name)
;;   :default-prop          ← "default" + capitalize(kebab->camel(:attr-name))
;;   :change-request-event  ← :change-request-event   (verbatim — names don't follow a derivable convention)
;;   :prop-type             ← :value-type
(def controlled-components
  (into {}
        (map (fn [[tag {:keys [attr-name change-request-event value-type]}]]
               (let [camel (kebab->camel attr-name)]
                 [tag {:control-prop         camel
                       :default-prop         (str "default" (str/capitalize camel))
                       :change-request-event change-request-event
                       :prop-type            value-type}])))
        form-controls))

;; ── React-specific helpers ──────────────────────────────────────────────────

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

;; ── Code generation ─────────────────────────────────────────────────────────

(defn generate-wrapper
  "Generate a complete .tsx wrapper file for a component."
  [{:keys [tag-name properties events methods string-defs]}]
  (let [interface-name (tag->interface-name tag-name)
        sdefs          (or string-defs {})
        ctrl           (get controlled-components tag-name)
        ctrl-prop      (when ctrl (:control-prop ctrl))
        default-prop   (when ctrl (:default-prop ctrl))
        ctrl-event     (when ctrl (:change-request-event ctrl))
        ctrl-type      (when ctrl (:prop-type ctrl))
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
                    ;; Default prop for controlled components
                    (when ctrl
                      [(str "  " default-prop "?: " ctrl-type ";")])
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
        ;; Props to destructure (events + controlled props + children)
        destructured (concat
                      (when ctrl [ctrl-prop default-prop])
                      (or event-prop-names [])
                      ["children"])
        ;; Find the change-request event entry (for controlled wrapping)
        change-request-entry (when ctrl
                               (some (fn [e] (when (= (:dom-name e) ctrl-event) e))
                                     event-entries))
        ;; useEffect event binding blocks — controlled change-request gets special handling
        effect-blocks (when event-entries
                        (map (fn [{:keys [dom-name react-prop] :as entry}]
                               (if (and ctrl (= entry change-request-entry))
                                 ;; Controlled mode: wrap the change-request handler to call preventDefault
                                 (str "      {\n"
                                      "        const controlled = " ctrl-prop " !== undefined;\n"
                                      "        const wrappedHandler = (e: Event) => {\n"
                                      "          if (controlled) e.preventDefault();\n"
                                      "          if (" react-prop ") (" react-prop " as EventListener)(e);\n"
                                      "        };\n"
                                      "        el.addEventListener(\"" dom-name "\", wrappedHandler);\n"
                                      "        cleanup.push(() => el.removeEventListener(\"" dom-name "\", wrappedHandler));\n"
                                      "      }")
                                 ;; Normal event binding
                                 (str "      if (" react-prop ") {\n"
                                      "        el.addEventListener(\"" dom-name "\", " react-prop " as EventListener);\n"
                                      "        cleanup.push(() => el.removeEventListener(\"" dom-name "\", " react-prop " as EventListener));\n"
                                      "      }")))
                             event-entries))
        ;; useEffect dependency array — add control prop for controlled components
        effect-dep-names (concat
                          (when ctrl [ctrl-prop])
                          (map :react-prop event-entries))
        effect-deps (if (seq effect-dep-names)
                      (str "[" (str/join ", " effect-dep-names) "]")
                      "[]")
        has-events  (boolean (seq event-entries))
        needs-effect (or has-events (some? ctrl))
        react-imports (if needs-effect
                        "forwardRef, useRef, useEffect"
                        "forwardRef, useRef")
        ;; Controlled mode: set initial value from defaultProp
        ctrl-init (when ctrl
                    (str "    // Set initial value from " default-prop " (uncontrolled mode)\n"
                         "    useEffect(() => {\n"
                         "      const el = innerRef.current;\n"
                         "      if (!el || " ctrl-prop " !== undefined || " default-prop " === undefined) return;\n"
                         (if (= ctrl-type "boolean")
                           (str "      if (" default-prop ") el.setAttribute(\"" ctrl-prop "\", \"\");\n"
                                "      else el.removeAttribute(\"" ctrl-prop "\");\n")
                           (str "      el.setAttribute(\"" ctrl-prop "\", String(" default-prop "));\n"))
                         "    // eslint-disable-next-line react-hooks/exhaustive-deps\n"
                         "    }, []);\n\n"))]
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
         ;; Controlled mode: set initial default value
         (or ctrl-init "")
         ;; Event binding (includes controlled change-request interception)
         (if has-events
           (str "    useEffect(() => {\n"
                "      const el = innerRef.current;\n"
                "      if (!el) return;\n"
                "      const cleanup: Array<() => void> = [];\n\n"
                (str/join "\n" effect-blocks) "\n\n"
                "      return () => cleanup.forEach(fn => fn());\n"
                "    }, " effect-deps ");\n\n")
           "")
         ;; For controlled components, pass the control prop explicitly since it's destructured out of rest
         (if ctrl
           (str "    return <" tag-name " ref={setRef} " ctrl-prop "={" ctrl-prop "} {...rest}>{children}</" tag-name ">;\n")
           (str "    return <" tag-name " ref={setRef} {...rest}>{children}</" tag-name ">;\n"))
         "  }\n"
         ");\n")))

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (str "// index.ts — auto-generated by generate_react.bb, do not edit\n\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (let [iface (tag->interface-name tag-name)]
                          (str "export { " iface ", type " iface "Props } from \"./" tag-name "\";")))
                      (sort-by :tag-name models)))
       "\n\n// Hooks (hand-written)\n"
       "export { useRegisterPreset, type PresetData, type TokenMap } from \"./hooks/useRegisterPreset\";\n"))

(defn generate-jsx-intrinsics
  "Generate a custom-elements.d.ts that registers all BareDOM tag names
   in JSX.IntrinsicElements so TypeScript allows <x-button> etc. in TSX."
  [models]
  (str "// custom-elements.d.ts — auto-generated by generate_react.bb, do not edit\n"
       "// Registers BareDOM custom element tag names as valid JSX intrinsic elements.\n\n"
       "import \"react\";\n\n"
       "declare module \"react\" {\n"
       "  namespace JSX {\n"
       "    interface IntrinsicElements {\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (str "      \"" tag-name "\": React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement> & Record<string, any>;"))
                      (sort-by :tag-name models)))
       "\n    }\n"
       "  }\n"
       "}\n"))

;; Exports that are hand-written (not auto-generated) and must be preserved
;; when the generator rewrites the package.json exports field.
(def manual-exports
  {"./hooks" {:import "./dist/hooks/index.js"
              :types  "./dist/hooks/index.d.ts"}})

(defn update-package-exports
  "Update the adapters/react/package.json exports field.
   Preserves manual-exports alongside the generated per-component entries."
  [models]
  (let [pkg     (json/parse-string (slurp react-pkg) true)
        exports (into (sorted-map)
                      (concat
                       [["." {:import "./dist/index.js"
                              :types  "./dist/index.d.ts"}]]
                       manual-exports
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

    ;; Generate JSX intrinsic elements declaration
    (spit (io/file react-src-dir "custom-elements.d.ts")
          (generate-jsx-intrinsics exported-models))

    ;; Update package.json exports
    (update-package-exports exported-models)

    (println (str "Generated " (count exported-models) " React wrappers + index.ts"))))

(-main)
