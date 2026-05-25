#!/usr/bin/env bb
;; generate_solid.bb — Auto-generate Solid 1.x wrapper components from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_solid.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")
;; Load shared codegen helpers (resolve-event-name, prop-type->ts, event-detail->ts)
(load-file "scripts/codegen_shared.bb")
;; Load shared form-control metadata (single source of truth across all adapters)
(load-file "scripts/form-control-metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def solid-src-dir "adapters/solid/src")
(def solid-pkg     "adapters/solid/package.json")

;; Components that support controlled mode, derived from the shared
;; form-control metadata. Identical derivation to generate_react.bb so
;; both adapters expose the same controlled-mode prop names.
(def controlled-components
  (into {}
        (map (fn [[tag {:keys [attr-name change-request-event value-type]}]]
               (let [camel (kebab->camel attr-name)]
                 [tag {:control-prop         camel
                       :default-prop         (str "default" (str/capitalize camel))
                       :change-request-event change-request-event
                       :prop-type            value-type}])))
        form-controls))

;; ── Solid-specific helpers ──────────────────────────────────────────────────

(defn event->solid-prop
  "Convert a DOM event name to a Solid callback prop name.
   Strips the tag-name prefix if present, then camelCases with `on` prefix.
   Matches React's convention so users porting between adapters have the
   same prop names.
   e.g. 'press' -> 'onPress'
        'x-alert-dismiss' (tag 'x-alert') -> 'onDismiss'
        'value-change' -> 'onValueChange'"
  [event-str tag-name]
  (let [prefix   (str tag-name "-")
        stripped (if (str/starts-with? event-str prefix)
                   (subs event-str (count prefix))
                   event-str)
        parts    (str/split stripped #"-")]
    (str "on" (str/join (map str/capitalize parts)))))

;; ── Code generation ─────────────────────────────────────────────────────────

(defn generate-wrapper
  "Generate a complete .tsx wrapper file for a component."
  [{:keys [tag-name properties events string-defs]}]
  (let [interface-name (tag->interface-name tag-name)
        sdefs          (or string-defs {})
        ctrl           (get controlled-components tag-name)
        ctrl-prop      (when ctrl (:control-prop ctrl))
        default-prop   (when ctrl (:default-prop ctrl))
        ctrl-event     (when ctrl (:change-request-event ctrl))
        ctrl-type      (when ctrl (:prop-type ctrl))
        writable-props (when properties
                         (->> properties
                              (remove (fn [[_ m]] (:readonly m)))
                              (remove (fn [[_ m]] (:read-only m)))))
        event-entries  (when (and events (seq events))
                         (mapv (fn [[event-key event-info]]
                                 (let [dom-name   (resolve-event-name event-key event-info sdefs)
                                       solid-prop (event->solid-prop dom-name tag-name)
                                       detail     (:detail event-info)]
                                   {:dom-name   dom-name
                                    :solid-prop solid-prop
                                    :detail-ts  (event-detail->ts detail)}))
                               events))
        ;; Prop interface lines
        prop-lines (concat
                    (when writable-props
                      (map (fn [[k m]]
                             (str "  " (kebab->camel (name k)) "?: " (prop-type->ts m) ";"))
                           writable-props))
                    (when ctrl
                      [(str "  " default-prop "?: " ctrl-type ";")])
                    (when event-entries
                      (map (fn [{:keys [solid-prop detail-ts]}]
                             (str "  " solid-prop "?: (e: CustomEvent<" detail-ts ">) => void;"))
                           event-entries))
                    [(str "  ref?: " interface-name "Element | ((el: " interface-name "Element) => void);")
                     "  children?: JSX.Element;"
                     "  class?: string;"
                     "  style?: JSX.CSSProperties | string;"
                     "  id?: string;"
                     "  slot?: string;"])
        ;; Props that splitProps takes out of the spread
        split-prop-names (concat
                          (when ctrl [(str "\"" ctrl-prop "\"") (str "\"" default-prop "\"")])
                          (map #(str "\"" (:solid-prop %) "\"") event-entries)
                          ["\"ref\"" "\"children\""])
        ;; Change-request entry (for controlled-mode wrapping)
        change-request-entry (when ctrl
                               (some (fn [e] (when (= (:dom-name e) ctrl-event) e))
                                     event-entries))
        ;; addEventListener blocks inside onMount
        event-blocks (when event-entries
                       (map (fn [{:keys [dom-name solid-prop detail-ts] :as entry}]
                              (if (and ctrl (= entry change-request-entry))
                                ;; Controlled: preventDefault when control prop supplied
                                (str "    {\n"
                                     "      const handler = (e: Event) => {\n"
                                     "        if (local." ctrl-prop " !== undefined) e.preventDefault();\n"
                                     "        local." solid-prop "?.(e as CustomEvent<" detail-ts ">);\n"
                                     "      };\n"
                                     "      el.addEventListener(\"" dom-name "\", handler);\n"
                                     "      onCleanup(() => el.removeEventListener(\"" dom-name "\", handler));\n"
                                     "    }")
                                ;; Plain forwarding handler — captures `local` so latest prop value is used
                                (str "    {\n"
                                     "      const handler = (e: Event) => local." solid-prop "?.(e as CustomEvent<" detail-ts ">);\n"
                                     "      el.addEventListener(\"" dom-name "\", handler);\n"
                                     "      onCleanup(() => el.removeEventListener(\"" dom-name "\", handler));\n"
                                     "    }")))
                            event-entries))
        has-events  (boolean (seq event-entries))
        ;; Imports
        solid-imports (cond-> ["splitProps"]
                        has-events       (conj "onMount" "onCleanup")
                        (some? ctrl)     (conj (if has-events "createEffect" "onMount") "createEffect"))
        ;; De-dup imports
        solid-imports (distinct solid-imports)
        ;; Build the controlled-mode write-effect (drives DOM attribute from control prop)
        ctrl-write-effect
        (when ctrl
          (str "  createEffect(() => {\n"
               "    if (!el) return;\n"
               "    const v = local." ctrl-prop ";\n"
               "    if (v === undefined) return;\n"
               (if (= ctrl-type "boolean")
                 (str "    if (v) el.setAttribute(\"" ctrl-prop "\", \"\");\n"
                      "    else el.removeAttribute(\"" ctrl-prop "\");\n")
                 (str "    el.setAttribute(\"" ctrl-prop "\", String(v));\n"))
               "  });\n\n"))
        ;; Build the uncontrolled default-init (runs once)
        ctrl-default-init
        (when ctrl
          (str "  onMount(() => {\n"
               "    if (!el || local." ctrl-prop " !== undefined || local." default-prop " === undefined) return;\n"
               (if (= ctrl-type "boolean")
                 (str "    if (local." default-prop ") el.setAttribute(\"" ctrl-prop "\", \"\");\n"
                      "    else el.removeAttribute(\"" ctrl-prop "\");\n")
                 (str "    el.setAttribute(\"" ctrl-prop "\", String(local." default-prop "));\n"))
               "  });\n\n"))
        ;; Build the event-binding block (onMount with all events)
        event-binding
        (when has-events
          (str "  onMount(() => {\n"
               "    if (!el) return;\n"
               (str/join "\n" event-blocks) "\n"
               "  });\n\n"))]
    (str "// " tag-name ".tsx — auto-generated by generate_solid.bb, do not edit\n"
         "import { " (str/join ", " solid-imports) " } from \"solid-js\";\n"
         "import type { JSX } from \"solid-js\";\n"
         "import type { " interface-name " as " interface-name "Element } from \"@vanelsas/baredom/" tag-name "\";\n"
         "import { init } from \"@vanelsas/baredom/" tag-name "\";\n\n"
         "init();\n\n"
         "export interface " interface-name "Props {\n"
         (str/join "\n" prop-lines) "\n"
         "}\n\n"
         "export function " interface-name "(props: " interface-name "Props): JSX.Element {\n"
         "  const [local, others] = splitProps(props, [" (str/join ", " split-prop-names) "]);\n"
         "  let el!: " interface-name "Element;\n\n"
         "  const setRef = (r: " interface-name "Element) => {\n"
         "    el = r;\n"
         "    if (typeof local.ref === \"function\") local.ref(r);\n"
         "  };\n\n"
         (or ctrl-default-init "")
         (or ctrl-write-effect "")
         (or event-binding "")
         "  return (\n"
         "    <" tag-name " ref={setRef} {...others}>\n"
         "      {local.children}\n"
         "    </" tag-name ">\n"
         "  );\n"
         "}\n")))

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (str "// index.ts — auto-generated by generate_solid.bb, do not edit\n\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (let [iface (tag->interface-name tag-name)]
                          (str "export { " iface ", type " iface "Props } from \"./" tag-name "\";")))
                      (sort-by :tag-name models)))
       "\n\n// Composables (hand-written)\n"
       "export { useRegisterPreset, type PresetData, type TokenMap } from \"./composables\";\n"))

(defn generate-jsx-intrinsics
  "Generate a custom-elements.d.ts that registers all BareDOM tag names
   in Solid's JSX.IntrinsicElements so TypeScript allows <x-button>
   etc. inside the wrapper bodies. Each tag is parameterised by its
   specific element type so the `ref` callback gets the right type."
  [models]
  (let [sorted (sort-by :tag-name models)
        imports (str/join "\n"
                          (map (fn [{:keys [tag-name]}]
                                 (let [iface (tag->interface-name tag-name)]
                                   (str "import type { " iface " } from \"@vanelsas/baredom/" tag-name "\";")))
                               sorted))
        intrinsic-lines (str/join "\n"
                                  (map (fn [{:keys [tag-name]}]
                                         (let [iface (tag->interface-name tag-name)]
                                           (str "      \"" tag-name "\": JSX.HTMLAttributes<" iface "> & Record<string, any>;")))
                                       sorted))]
    (str "// custom-elements.d.ts — auto-generated by generate_solid.bb, do not edit\n"
         "// Registers BareDOM custom element tag names as valid JSX intrinsic elements.\n\n"
         "import \"solid-js\";\n"
         imports "\n\n"
         "declare module \"solid-js\" {\n"
         "  namespace JSX {\n"
         "    interface IntrinsicElements {\n"
         intrinsic-lines "\n"
         "    }\n"
         "  }\n"
         "}\n")))

;; Exports preserved when the generator rewrites package.json.
(def manual-exports
  {"./composables" {:import "./dist/composables/index.js"
                    :types  "./dist/composables/index.d.ts"}})

(defn update-package-exports
  "Update the adapters/solid/package.json exports field.
   Per-component entries point to .jsx (tsc with jsx:preserve emits .jsx);
   the barrel and composables are .ts sources so they compile to .js."
  [models]
  (let [pkg     (json/parse-string (slurp solid-pkg) true)
        exports (into (sorted-map)
                      (concat
                       [["." {:import "./dist/index.js"
                              :types  "./dist/index.d.ts"}]]
                       manual-exports
                       (map (fn [{:keys [tag-name]}]
                              [(str "./" tag-name)
                               {:import (str "./dist/" tag-name ".jsx")
                                :types  (str "./dist/" tag-name ".d.ts")}])
                            (sort-by :tag-name models))))
        updated (assoc pkg :exports exports)]
    (spit solid-pkg (json/generate-string updated {:pretty true}))))

;; ── Main ────────────────────────────────────────────────────────────────────
(defn -main []
  (println "Generating Solid 1.x wrapper components...")

  (.mkdirs (io/file solid-src-dir))

  (let [all-models      (discover-models)
        exported-names  (load-package-exports)
        exported-models (filter #(contains? exported-names (:tag-name %)) all-models)]

    (doseq [model exported-models]
      (let [tsx-content (generate-wrapper model)
            tsx-file    (io/file solid-src-dir (str (:tag-name model) ".tsx"))]
        (spit tsx-file tsx-content)))

    (spit (io/file solid-src-dir "index.ts")
          (generate-index exported-models))

    (spit (io/file solid-src-dir "custom-elements.d.ts")
          (generate-jsx-intrinsics exported-models))

    (update-package-exports exported-models)

    (println (str "Generated " (count exported-models) " Solid wrappers + index.ts + custom-elements.d.ts"))))

(-main)
