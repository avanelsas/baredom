#!/usr/bin/env bb
;; generate_svelte.bb — Auto-generate Svelte 5 wrapper components from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_svelte.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")
;; Load shared codegen helpers (resolve-event-name, prop-type->ts, event-detail->ts)
(load-file "scripts/codegen_shared.bb")
;; Load shared form-control metadata (Vue v-model / Angular CVA / Svelte $bindable)
(load-file "scripts/form-control-metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def svelte-src-dir "adapters/svelte/src")
(def svelte-pkg     "adapters/svelte/package.json")

;; Manually-curated exports preserved when the generator rewrites the
;; package.json exports field. The composables entry is hand-written
;; (useRegisterPreset).
(def manual-exports
  {"./composables" {:types  "./dist/composables/index.d.ts"
                    :import "./dist/composables/index.js"}})

;; ── Svelte-specific helpers ─────────────────────────────────────────────────

(defn event->svelte-prop
  "Convert a DOM event name to a Svelte 5 `on<event>` prop name.
   Strips the tag-name prefix if present, then strips remaining hyphens
   and lowercases — Svelte's native DOM-event convention (`onclick`,
   `onpointerdown`).
   e.g. 'press' (tag 'x-button') -> 'onpress'
        'x-alert-dismiss' (tag 'x-alert') -> 'ondismiss'
        'press-start' -> 'onpressstart'
        'value-change' -> 'onvaluechange'
        'x-checkbox-change-request' -> 'onchangerequest'"
  [event-str tag-name]
  (let [prefix     (str tag-name "-")
        unprefixed (if (str/starts-with? event-str prefix)
                     (subs event-str (count prefix))
                     event-str)]
    (str "on" (str/lower-case (str/replace unprefixed "-" "")))))

;; ── Field classification ────────────────────────────────────────────────────

;; JS reserved words that cannot be used as variable names in strict mode
;; (Svelte 5 `<script>` is implicit module/strict scope). If a property's
;; camelCased name is one of these, the wrapper aliases it in the
;; destructure: `let { static: staticAttr } = $props();` and writes the
;; attribute imperatively via `static={staticAttr}` in the template.
;; The interface keeps the original name so consumers still write
;; `<XSpotlightCard static />` (the prop name in the public API is unchanged).
(def js-reserved-prop-names
  #{"static" "default" "function" "this" "new" "delete" "return" "var"
    "let" "const" "import" "export" "for" "while" "do" "switch" "case"
    "break" "continue" "throw" "catch" "finally" "try" "typeof"
    "instanceof" "in" "of" "void" "null" "true" "false" "super"
    "if" "else" "yield" "await" "enum" "implements" "interface"
    "package" "private" "protected" "public" "arguments" "eval"})

(defn reserved-alias
  "Rename a reserved-word prop to `<name>Attr`."
  [prop-name]
  (str prop-name "Attr"))

(defn writable-props
  "Filter readonly props out of property-api. Returns a vector of normalized
   entries: {:kw :meta :orig-name :var-name :is-aliased :ts-type}.
   `orig-name` is the camelCased identifier consumers use (matches what
   they'd write in their template). `var-name` is the destructure target
   inside the Svelte component — equal to orig-name unless the original
   clashes with a JS reserved word (then suffixed `Attr`)."
  [properties]
  (when properties
    (->> properties
         (remove (fn [[_ m]] (:readonly m)))
         (remove (fn [[_ m]] (:read-only m)))
         (mapv (fn [[k m]]
                 (let [orig (kebab->camel (name k))
                       aliased (contains? js-reserved-prop-names orig)]
                   {:kw         k
                    :meta       m
                    :orig-name  orig
                    :var-name   (if aliased (reserved-alias orig) orig)
                    :is-aliased aliased
                    :ts-type    (prop-type->ts m)}))))))

(defn bindable-prop-entry
  "If a writable entry matches the form-control's bindable detail-field,
   return it. nil otherwise."
  [writables form-ctl-cfg]
  (when form-ctl-cfg
    (let [target (:detail-field form-ctl-cfg)]
      (some (fn [{:keys [orig-name] :as entry}]
              (when (= orig-name target) entry))
            writables))))

(defn non-bindable-writable-props
  "writables minus the bindable form prop (if any)."
  [writables bindable-entry]
  (if bindable-entry
    (remove #(= (:kw %) (:kw bindable-entry)) writables)
    writables))

(defn build-event-entries
  "Resolve events to {:dom-name :prop-name :detail-ts :is-form-change}."
  [events string-defs tag-name form-ctl-cfg]
  (when (and events (seq events))
    (mapv (fn [[event-key event-info]]
            (let [dom-name  (resolve-event-name event-key event-info string-defs)
                  prop-name (event->svelte-prop dom-name tag-name)
                  detail    (:detail event-info)]
              {:dom-name       dom-name
               :prop-name      prop-name
               :detail-ts      (event-detail->ts detail)
               :is-form-change (boolean (and form-ctl-cfg
                                             (= dom-name (:change-event form-ctl-cfg))))}))
          events)))

;; Svelte's a11y linter flags `autofocus` and `scope` as suspicious on what it
;; treats as plain HTML, but these are forwarded props on a custom element
;; (the linter has no notion of BareDOM's intent). Suppress the warning at
;; generation time — narrowly, only for components that actually expose the
;; offending prop, so a NEW genuine a11y issue on a different attribute still
;; surfaces in CI (and the CI now runs `svelte-check --fail-on-warnings`).
(def a11y-prop->svelte-ignore
  {"autofocus" "a11y_autofocus"
   "scope"     "a11y_misplaced_scope"})

(defn collect-a11y-suppressions
  "Return the sequence of svelte-ignore codes needed for this component's
   writable props (deduplicated, in declaration order)."
  [writables]
  (->> writables
       (keep (fn [{:keys [orig-name]}]
               (get a11y-prop->svelte-ignore orig-name)))
       distinct
       vec))

;; ── Section builders ────────────────────────────────────────────────────────

(defn build-prop-interface-lines
  "Body lines for the XFooProps interface (no surrounding braces)."
  [non-bindable-writables bindable-entry event-entries form-ctl-cfg interface-name]
  (let [writable-lines
        (when (seq non-bindable-writables)
          (map (fn [{:keys [orig-name ts-type]}]
                 (str "    " orig-name "?: " ts-type ";"))
               non-bindable-writables))
        bindable-lines
        (when bindable-entry
          (let [orig-name (:orig-name bindable-entry)
                ts-type (:value-type form-ctl-cfg)]
            [(str "    /** Two-way bindable form value — `bind:" orig-name "={...}`. */")
             (str "    " orig-name "?: " ts-type ";")]))
        event-lines
        (when (seq event-entries)
          (map (fn [{:keys [prop-name detail-ts]}]
                 (str "    " prop-name "?: (e: CustomEvent<" detail-ts ">) => void;"))
               event-entries))
        common-lines
        [(str "    el?: " interface-name "Element | null;")
         "    children?: import(\"svelte\").Snippet;"
         "    class?: string;"
         "    id?: string;"
         ;; `slot` is declared in the type surface but intentionally NOT
         ;; destructured below — it must flow through `...rest` because
         ;; Svelte 5 rejects explicit `slot={...}` on a wrapper's root
         ;; element (no statically-known parent at compile time). At the
         ;; consumer's site `<XFoo slot="end">` is analyzed in their
         ;; template context, where the parent (a component or custom-
         ;; element descendant) IS known.
         "    slot?: string;"]]
    (concat writable-lines bindable-lines event-lines common-lines)))

(defn destructure-field
  "Build one destructure field. Aliased reserved-name props become
   `orig: varname`; everything else is just `varname`."
  [{:keys [orig-name var-name is-aliased]}]
  (if is-aliased
    (str orig-name ": " var-name)
    var-name))

(defn build-destructure-fields
  "Fields list for the `let { ... } = $props();` block."
  [non-bindable-writables bindable-entry event-entries]
  (let [writable-fields
        (when (seq non-bindable-writables)
          (map destructure-field non-bindable-writables))
        bindable-field
        (when bindable-entry
          (let [{:keys [orig-name var-name is-aliased]} bindable-entry
                lhs (if is-aliased (str orig-name ": " var-name) var-name)]
            [(str lhs " = $bindable(undefined)")]))
        event-fields
        (when (seq event-entries)
          (map :prop-name event-entries))
        common-fields
        ["el = $bindable(null)"
         "children"
         "class: className"
         "id"]]
    (concat writable-fields bindable-field event-fields common-fields)))

(defn build-attr-mirror-effect
  "Emit the $effect that writes the bindable value back to the DOM."
  [bindable-entry form-ctl-cfg]
  (when bindable-entry
    (let [var-name  (:var-name bindable-entry)
          attr-name (:attr-name form-ctl-cfg)
          body      (if (= (:write-mode form-ctl-cfg) :boolean-attr)
                      (str "    if (" var-name ") node.setAttribute(\"" attr-name "\", \"\");\n"
                           "    else node.removeAttribute(\"" attr-name "\");")
                      (str "    if (" var-name " != null) node.setAttribute(\""
                           attr-name "\", String(" var-name "));\n"
                           "    else node.removeAttribute(\"" attr-name "\");"))]
      (str "  $effect(() => {\n"
           "    const node = el;\n"
           "    if (!node || " var-name " === undefined) return;\n"
           body "\n"
           "  });"))))

(defn build-event-effect
  "Emit the single $effect that wires all listeners and returns cleanup."
  [event-entries bindable-entry form-ctl-cfg]
  (when (seq event-entries)
    (let [bindable-name (when bindable-entry (:var-name bindable-entry))
          coerce        (when bindable-entry
                          (case (:value-type form-ctl-cfg)
                            "string"  "String"
                            "number"  "Number"
                            "boolean" "Boolean"))
          listener-blocks
          (map (fn [{:keys [dom-name prop-name detail-ts is-form-change]}]
                 (if is-form-change
                   (str "    const " prop-name "Handler = (e: Event) => {\n"
                        "      const detail = (e as CustomEvent<" detail-ts ">).detail;\n"
                        "      " bindable-name " = " coerce "(detail."
                        (:detail-field form-ctl-cfg) ");\n"
                        "      " prop-name "?.(e as CustomEvent<" detail-ts ">);\n"
                        "    };\n"
                        "    node.addEventListener(\"" dom-name "\", " prop-name "Handler);\n"
                        "    cleanups.push(() => node.removeEventListener(\""
                        dom-name "\", " prop-name "Handler));")
                   (str "    const " prop-name "Handler = (e: Event) => "
                        prop-name "?.(e as CustomEvent<" detail-ts ">);\n"
                        "    node.addEventListener(\"" dom-name "\", " prop-name "Handler);\n"
                        "    cleanups.push(() => node.removeEventListener(\""
                        dom-name "\", " prop-name "Handler));")))
               event-entries)]
      (str "  $effect(() => {\n"
           "    const node = el;\n"
           "    if (!node) return;\n"
           "    const cleanups: Array<() => void> = [];\n"
           (str/join "\n" listener-blocks) "\n"
           "    return () => cleanups.forEach((fn) => fn());\n"
           "  });"))))

(defn build-template-attrs
  "Attribute lines for the inner <x-foo> element. Bindable form prop is
   omitted (the $effect is the writer); event props are omitted (consumed
   by the wrapper, not forwarded twice). Reserved-aliased props use
   explicit `orig={var}` form since shorthand `{var}` would produce the
   wrong attribute name."
  [non-bindable-writables]
  (let [writable-attrs
        (when (seq non-bindable-writables)
          (map (fn [{:keys [orig-name var-name is-aliased]}]
                 (if is-aliased
                   (str "  " orig-name "={" var-name "}")
                   (str "  {" var-name "}")))
               non-bindable-writables))
        common-attrs
        ["  class={className}"
         "  {id}"
         "  {...rest}"]]
    (concat ["  bind:this={el}"] writable-attrs common-attrs)))

;; ── Whole-file orchestrator ─────────────────────────────────────────────────

(defn generate-component
  "Generate the .svelte wrapper file content for a component."
  [{:keys [tag-name properties events string-defs]}]
  (let [interface-name      (tag->interface-name tag-name)
        sdefs               (or string-defs {})
        form-ctl-cfg        (get form-controls tag-name)
        writables           (writable-props properties)
        bindable-entry      (bindable-prop-entry writables form-ctl-cfg)
        non-bindable        (non-bindable-writable-props writables bindable-entry)
        event-entries       (build-event-entries events sdefs tag-name form-ctl-cfg)
        interface-lines     (build-prop-interface-lines
                              non-bindable bindable-entry event-entries
                              form-ctl-cfg interface-name)
        destructure-fields  (build-destructure-fields
                              non-bindable bindable-entry event-entries)
        attr-mirror-effect  (build-attr-mirror-effect bindable-entry form-ctl-cfg)
        event-effect        (build-event-effect event-entries bindable-entry form-ctl-cfg)
        template-attrs      (build-template-attrs non-bindable)
        a11y-suppressions   (collect-a11y-suppressions non-bindable)]
    (str
      "<!-- " tag-name ".svelte — auto-generated by generate_svelte.bb, do not edit -->\n"
      "<script lang=\"ts\" module>\n"
      "  import type { " interface-name " as " interface-name "Element }"
      " from \"@vanelsas/baredom/" tag-name "\";\n"
      "  import { init } from \"@vanelsas/baredom/" tag-name "\";\n"
      "  init();\n"
      "  export type { " interface-name "Element };\n"
      "  export interface " interface-name "Props {\n"
      (str/join "\n" interface-lines) "\n"
      "  }\n"
      "</script>\n\n"
      "<script lang=\"ts\">\n"
      "  let {\n"
      (str/join ",\n" (map #(str "    " %) destructure-fields))
      ",\n"
      "    ...rest\n"
      "  }: " interface-name "Props = $props();\n"
      (when attr-mirror-effect (str "\n" attr-mirror-effect "\n"))
      (when event-effect (str "\n" event-effect "\n"))
      "</script>\n\n"
      (when (seq a11y-suppressions)
        (str (str/join "\n" (map #(str "<!-- svelte-ignore " % " -->") a11y-suppressions))
             "\n"))
      "<" tag-name "\n"
      (str/join "\n" template-attrs) "\n"
      ">\n"
      "  {@render children?.()}\n"
      "</" tag-name ">\n")))

;; ── Index, CEM types, package.json ──────────────────────────────────────────

(defn generate-component-shim
  "Generate the per-component .ts shim. Re-exports the .svelte default
   as a named export plus the prop and element types. Lets consumers
   write `import { XIcon } from '@vanelsas/baredom-svelte/x-icon'` —
   matching the Vue/React adapter UX. Without this, per-component
   imports would have to be default-only because Svelte components
   export themselves as the module default."
  [{:keys [tag-name]}]
  (let [iface (tag->interface-name tag-name)]
    (str "// " tag-name ".ts — auto-generated by generate_svelte.bb, do not edit\n"
         "export { default as " iface
         ", type " iface "Props"
         ", type " iface "Element"
         " } from \"./" tag-name ".svelte\";\n")))

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (str "// index.ts — auto-generated by generate_svelte.bb, do not edit\n\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (let [iface (tag->interface-name tag-name)]
                          (str "export { default as " iface
                               ", type " iface "Props"
                               ", type " iface "Element"
                               " } from \"./" tag-name ".svelte\";")))
                      (sort-by :tag-name models)))
       "\n\n// Composables (hand-written)\n"
       "export { useRegisterPreset, type PresetData, type TokenMap } from \"./composables/index.js\";\n"))

(defn generate-custom-elements-dts
  "Generate the svelteHTML.IntrinsicElements augmentation so consumers can
   use raw `<x-foo>` tags in .svelte files (in addition to the wrappers)
   without TS errors."
  [models]
  (str "// custom-elements.d.ts — auto-generated by generate_svelte.bb, do not edit\n"
       "// Declares BareDOM custom elements for Svelte's TS checker so that\n"
       "// raw `<x-foo>` usage in templates type-checks. The auto-generated\n"
       "// wrappers in this package are the recommended consumer surface.\n\n"
       "declare namespace svelteHTML {\n"
       "  interface IntrinsicElements {\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (str "    \"" tag-name "\": Record<string, any>;"))
                      (sort-by :tag-name models)))
       "\n"
       "  }\n"
       "}\n"
       "\n"
       "export {};\n"))

(defn update-package-exports
  [models]
  (let [pkg     (json/parse-string (slurp svelte-pkg) true)
        exports (into (sorted-map)
                      (concat
                       [["." {:types  "./dist/index.d.ts"
                              :svelte "./dist/index.js"
                              :import "./dist/index.js"}]]
                       manual-exports
                       ;; Per-component subpath exports resolve to the
                       ;; generated `.ts` shim (compiled to `.js`) which
                       ;; re-exports the default of `<tag>.svelte` as a
                       ;; NAMED export. We deliberately omit the `svelte`
                       ;; condition here so bundlers don't pick the bare
                       ;; `.svelte` file (which has only a default export
                       ;; and would break `import { XIcon }` calls).
                       (map (fn [{:keys [tag-name]}]
                              [(str "./" tag-name)
                               {:types  (str "./dist/" tag-name ".d.ts")
                                :import (str "./dist/" tag-name ".js")}])
                            (sort-by :tag-name models))))
        updated (assoc pkg :exports exports)]
    (spit svelte-pkg (json/generate-string updated {:pretty true}))))

;; ── Main ────────────────────────────────────────────────────────────────────

(defn -main []
  (println "Generating Svelte 5 wrapper components...")

  (.mkdirs (io/file svelte-src-dir))

  (let [all-models      (discover-models)
        exported-names  (load-package-exports)
        exported-models (filter #(contains? exported-names (:tag-name %)) all-models)]

    ;; Generate per-component .svelte + .ts shim
    (doseq [model exported-models]
      (let [svelte-content (generate-component model)
            svelte-file    (io/file svelte-src-dir (str (:tag-name model) ".svelte"))
            ts-content     (generate-component-shim model)
            ts-file        (io/file svelte-src-dir (str (:tag-name model) ".ts"))]
        (spit svelte-file svelte-content)
        (spit ts-file ts-content)))

    ;; Generate barrel index.ts
    (spit (io/file svelte-src-dir "index.ts")
          (generate-index exported-models))

    ;; Generate custom-elements.d.ts
    (spit (io/file svelte-src-dir "custom-elements.d.ts")
          (generate-custom-elements-dts exported-models))

    ;; Update package.json exports
    (update-package-exports exported-models)

    (println (str "Generated " (count exported-models) " Svelte wrappers + index.ts + custom-elements.d.ts"))))

(-main)
