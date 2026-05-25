#!/usr/bin/env bb
;; generate_vue.bb — Auto-generate Vue 3 wrapper components from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_vue.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def vue-src-dir "adapters/vue/src")
(def vue-pkg     "adapters/vue/package.json")

;; Components that have v-model bridging. Copy of cva-components from
;; generate_angular.bb — identical metadata serves both Vue v-model and
;; Angular ControlValueAccessor.
;; :value-type    — TypeScript type for the v-model value
;; :change-event  — DOM event fired on committed change
;; :detail-field  — field in event.detail that carries the new value
;; :write-mode    — :boolean-attr (set/remove attribute) or :string-attr (setAttribute)
;; :attr-name     — attribute name to write/remove
(def v-model-components
  {"x-checkbox"       {:value-type "boolean" :change-event "x-checkbox-change"       :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-switch"         {:value-type "boolean" :change-event "x-switch-change"         :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-radio"          {:value-type "boolean" :change-event "x-radio-change"          :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-slider"         {:value-type "string"  :change-event "x-slider-change"         :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-text-area"      {:value-type "string"  :change-event "x-text-area-change"      :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-select"         {:value-type "string"  :change-event "select-change"           :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-combobox"       {:value-type "string"  :change-event "x-combobox-change"       :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-currency-field" {:value-type "string"  :change-event "x-currency-field-change" :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-tabs"           {:value-type "string"  :change-event "value-change"            :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-pagination"     {:value-type "number"  :change-event "page-change"             :detail-field "page"    :write-mode :string-attr  :attr-name "page"}})

;; ── Vue-specific helpers ────────────────────────────────────────────────────

(defn resolve-event-name
  "Resolve the actual DOM event name from an event-schema entry."
  [event-key event-info string-defs]
  (cond
    (symbol? event-key) (resolve-sym event-key (or string-defs {}))
    (keyword? event-key) (or (:event-name event-info) (name event-key))
    :else (str event-key)))

(defn event->vue-emit
  "Strip tag-name prefix from a DOM event name; Vue uses kebab-case emit keys.
   e.g. 'press' -> 'press'
        'x-alert-dismiss' (tag 'x-alert') -> 'dismiss'
        'value-change' -> 'value-change'"
  [event-str tag-name]
  (let [prefix (str tag-name "-")]
    (if (str/starts-with? event-str prefix)
      (subs event-str (count prefix))
      event-str)))

(defn prop-type->ts
  "TypeScript type for a property metadata entry."
  [prop-meta]
  (let [t (normalize-type-sym (:type prop-meta))]
    (case t
      "boolean" "boolean"
      "string"  "string"
      "number"  "number"
      "object"  "Record<string, any>"
      "any")))

(defn prop-type->vue-runtime
  "Vue 3 runtime prop constructor for a property metadata entry."
  [prop-meta]
  (let [t (normalize-type-sym (:type prop-meta))]
    (case t
      "boolean" "Boolean"
      "string"  "String"
      "number"  "Number"
      "object"  "Object"
      "Object")))

(defn vue-runtime-for-type
  "Vue 3 runtime constructor for a bare TS type string."
  [type-str]
  (case type-str
    "boolean" "Boolean"
    "string"  "String"
    "number"  "Number"
    "Object"))

;; Vue 3 reserves these names as VNode-level identifiers. A prop with
;; one of these names is silently dropped by Vue ("Invalid prop name:
;; ... is a reserved property") and the value never reaches the element.
;; Affected props are renamed in the generated wrapper (suffix "Attr")
;; and the underlying attribute is written imperatively. Only x-i18n's
;; `key` (its translation key — the component's primary attribute)
;; currently hits this; the set guards against future collisions too.
(def vue-reserved-prop-names #{"key" "ref" "is"})

(defn build-reserved-prop-entries
  "For each writable prop whose camelCased name collides with a Vue
   reserved VNode identifier, return an entry describing the rename
   and the imperative-write strategy."
  [writable-props]
  (when writable-props
    (->> writable-props
         (keep (fn [[k m]]
                 (let [camel (kebab->camel (name k))]
                   (when (contains? vue-reserved-prop-names camel)
                     {:original-camel camel
                      :renamed        (str camel "Attr")
                      :ts-type        (prop-type->ts m)
                      :attr-name      (name k)
                      :runtime-ctor   (prop-type->vue-runtime m)}))))
         vec)))

(defn event-detail->ts
  "TypeScript type for a CustomEvent detail. kebab keys -> camelCase."
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

;; ── Component generation ────────────────────────────────────────────────────

(defn build-prop-decls
  "Generate the `props: { ... }` entries. Reserved-name props
   (key/ref/is) are emitted under their `Attr`-suffixed alias."
  [writable-props v-model-cfg]
  (concat
    (when writable-props
      (map (fn [[k m]]
             (let [camel (kebab->camel (name k))
                   prop-name (if (contains? vue-reserved-prop-names camel)
                               (str camel "Attr")
                               camel)]
               (str "    " prop-name
                    ": { type: " (prop-type->vue-runtime m)
                    " as PropType<" (prop-type->ts m) " | undefined>, default: undefined },")))
           writable-props))
    (when v-model-cfg
      [(str "    modelValue: { type: "
            (vue-runtime-for-type (:value-type v-model-cfg))
            " as PropType<" (:value-type v-model-cfg) " | undefined>, default: undefined },")])))

(defn build-reserved-write-helpers
  "TypeScript writeX helper functions for reserved-name props.
   These run imperatively because h() refuses to set reserved keys
   as element attributes."
  [reserved-entries]
  (when (seq reserved-entries)
    (map (fn [{:keys [renamed ts-type attr-name]}]
           (let [helper-name (str "write" (str/upper-case (subs renamed 0 1)) (subs renamed 1))
                 body (if (= ts-type "boolean")
                        (str "      if (v) el.setAttribute(\"" attr-name "\", \"\");\n"
                             "      else el.removeAttribute(\"" attr-name "\");\n")
                        (str "      if (v != null) el.setAttribute(\"" attr-name "\", String(v));\n"
                             "      else el.removeAttribute(\"" attr-name "\");\n"))]
             (str "    const " helper-name " = (v: " ts-type " | undefined) => {\n"
                  "      const el = elRef.value;\n"
                  "      if (!el) return;\n"
                  body
                  "    };")))
         reserved-entries)))

(defn reserved-helper-name
  "writeKeyAttr from a {:renamed \"keyAttr\"} entry."
  [{:keys [renamed]}]
  (str "write" (str/upper-case (subs renamed 0 1)) (subs renamed 1)))

(defn build-event-entries
  "Resolve events to {:dom-name :emit-name :detail-ts :is-model}."
  [events string-defs tag-name v-model-cfg]
  (when (and events (seq events))
    (mapv (fn [[event-key event-info]]
            (let [dom-name (resolve-event-name event-key event-info string-defs)
                  emit-name (event->vue-emit dom-name tag-name)
                  detail (:detail event-info)]
              {:dom-name dom-name
               :emit-name emit-name
               :detail-ts (event-detail->ts detail)
               :is-model (boolean (and v-model-cfg (= dom-name (:change-event v-model-cfg))))}))
          events)))

(defn build-emit-decls
  "Generate the `emits: { ... }` entries."
  [event-entries v-model-cfg]
  (concat
    (when v-model-cfg
      [(str "    \"update:modelValue\": (_v: " (:value-type v-model-cfg) ") => true,")])
    (when event-entries
      (map (fn [{:keys [emit-name detail-ts]}]
             (str "    \"" emit-name "\": (_e: CustomEvent<" detail-ts ">) => true,"))
           event-entries))))

(defn build-bind-blocks
  "Per-event addEventListener blocks for the onMounted body."
  [event-entries v-model-cfg]
  (when event-entries
    (map (fn [{:keys [dom-name emit-name is-model]}]
           (if is-model
             (let [coerce (case (:value-type v-model-cfg)
                            "string"  "String"
                            "number"  "Number"
                            "boolean" "Boolean")]
               (str "      {\n"
                    "        const handler = (e: Event) => {\n"
                    "          const detail = (e as CustomEvent).detail;\n"
                    "          emit(\"update:modelValue\", " coerce "(detail." (:detail-field v-model-cfg) "));\n"
                    "          emit(\"" emit-name "\", e as CustomEvent);\n"
                    "        };\n"
                    "        el.addEventListener(\"" dom-name "\", handler);\n"
                    "        cleanup.push(() => el.removeEventListener(\"" dom-name "\", handler));\n"
                    "      }"))
             (str "      {\n"
                  "        const handler = (e: Event) => emit(\"" emit-name "\", e as CustomEvent);\n"
                  "        el.addEventListener(\"" dom-name "\", handler);\n"
                  "        cleanup.push(() => el.removeEventListener(\"" dom-name "\", handler));\n"
                  "      }")))
         event-entries)))

(defn build-write-value-body
  "writeValue helper body for v-model components."
  [v-model-cfg]
  (if (= (:write-mode v-model-cfg) :boolean-attr)
    (str "      if (v) el.setAttribute(\"" (:attr-name v-model-cfg) "\", \"\");\n"
         "      else el.removeAttribute(\"" (:attr-name v-model-cfg) "\");\n")
    (str "      if (v != null) el.setAttribute(\"" (:attr-name v-model-cfg) "\", String(v));\n"
         "      else el.removeAttribute(\"" (:attr-name v-model-cfg) "\");\n")))

(defn generate-component
  "Generate the .ts wrapper file content for a component."
  [{:keys [tag-name properties events string-defs]}]
  (let [interface-name   (tag->interface-name tag-name)
        sdefs            (or string-defs {})
        v-model-cfg      (get v-model-components tag-name)
        has-v-model      (boolean v-model-cfg)
        writable-props   (when properties
                           (->> properties
                                (remove (fn [[_ m]] (:readonly m)))
                                (remove (fn [[_ m]] (:read-only m)))))
        reserved-entries (build-reserved-prop-entries writable-props)
        has-reserved     (boolean (seq reserved-entries))
        prop-decls       (build-prop-decls writable-props v-model-cfg)
        event-entries    (build-event-entries events sdefs tag-name v-model-cfg)
        emit-decls       (build-emit-decls event-entries v-model-cfg)
        bind-blocks      (build-bind-blocks event-entries v-model-cfg)
        write-value-body (when has-v-model (build-write-value-body v-model-cfg))
        reserved-helpers (build-reserved-write-helpers reserved-entries)
        has-events       (boolean (seq event-entries))
        has-props        (boolean (or (seq writable-props) has-v-model))
        needs-on-mounted (or has-events has-v-model has-reserved)
        vue-imports      (cond-> ["defineComponent" "h" "ref"]
                           has-events       (conj "onMounted" "onBeforeUnmount")
                           (and (or has-v-model has-reserved)
                                (not has-events)) (conj "onMounted")
                           (or has-v-model has-reserved) (conj "watch")
                           has-props        (conj "type PropType"))]

    (str "// " tag-name ".ts — auto-generated by generate_vue.bb, do not edit\n\n"
         "import { " (str/join ", " vue-imports) " } from \"vue\";\n"
         "import type { " interface-name " as " interface-name "Element } from \"@vanelsas/baredom/" tag-name "\";\n"
         "import { init } from \"@vanelsas/baredom/" tag-name "\";\n\n"
         "init();\n\n"
         "/** Shape exposed via `expose()` — use with `useTemplateRef<" interface-name "Exposed>(name)`. */\n"
         "export interface " interface-name "Exposed {\n"
         "  readonly el: " interface-name "Element | null;\n"
         "}\n\n"
         "export const " interface-name " = defineComponent({\n"
         "  name: \"" interface-name "\",\n"
         "  inheritAttrs: false,\n"
         (if has-props
           (str "  props: {\n"
                (str/join "\n" prop-decls) "\n"
                "  },\n")
           "  props: {},\n")
         (if (seq emit-decls)
           (str "  emits: {\n"
                (str/join "\n" emit-decls) "\n"
                "  },\n")
           "")
         "  setup(props, { emit, attrs, slots, expose }) {\n"
         "    const elRef = ref<" interface-name "Element | null>(null);\n"
         (when has-events "    const cleanup: Array<() => void> = [];\n")
         "\n"
         (when has-v-model
           (str "    const writeValue = (v: " (:value-type v-model-cfg) " | undefined) => {\n"
                "      const el = elRef.value;\n"
                "      if (!el || v === undefined) return;\n"
                write-value-body
                "    };\n\n"))
         (when has-reserved
           (str (str/join "\n\n" reserved-helpers) "\n\n"))
         (if needs-on-mounted
           (str "    onMounted(() => {\n"
                "      const el = elRef.value;\n"
                "      if (!el) return;\n"
                (when has-v-model
                  "      writeValue(props.modelValue);\n")
                (when has-reserved
                  (str/join (map (fn [entry]
                                   (str "      " (reserved-helper-name entry)
                                        "(props." (:renamed entry) ");\n"))
                                 reserved-entries)))
                (when has-events
                  (str "\n" (str/join "\n" bind-blocks) "\n"))
                "    });\n\n")
           "")
         (when has-events
           (str "    onBeforeUnmount(() => {\n"
                "      cleanup.forEach((fn) => fn());\n"
                "    });\n\n"))
         (when has-v-model
           "    watch(() => props.modelValue, writeValue);\n\n")
         (when has-reserved
           (str (str/join "\n" (map (fn [entry]
                                      (str "    watch(() => props." (:renamed entry)
                                           ", " (reserved-helper-name entry) ");"))
                                    reserved-entries))
                "\n\n"))
         "    expose({ el: elRef });\n\n"
         "    return () => {\n"
         (let [excluded (concat (when has-v-model ["modelValue"])
                                (map :renamed reserved-entries))]
           (if (seq excluded)
             (str "      const { "
                  (str/join ", " (map #(str % ": _" %) excluded))
                  ", ...elProps } = props;\n"
                  (str/join (map #(str "      void _" % ";\n") excluded))
                  "      return h(\"" tag-name "\" as any, { ...attrs, ref: elRef, ...elProps }, slots.default?.());\n")
             (str "      return h(\"" tag-name "\" as any, { ...attrs, ref: elRef, ...props }, slots.default?.());\n")))
         "    };\n"
         "  },\n"
         "});\n\n"
         "export type " interface-name "Instance = InstanceType<typeof " interface-name ">;\n")))

;; ── Index generation ────────────────────────────────────────────────────────

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (str "// index.ts — auto-generated by generate_vue.bb, do not edit\n\n"
       (str/join "\n"
                 (map (fn [{:keys [tag-name]}]
                        (let [iface (tag->interface-name tag-name)]
                          (str "export { " iface
                               ", type " iface "Instance"
                               ", type " iface "Exposed"
                               " } from \"./" tag-name "\";")))
                      (sort-by :tag-name models)))
       "\n\n// Composables (hand-written)\n"
       "export { useRegisterPreset, type PresetData, type TokenMap } from \"./composables/index\";\n"))

;; ── Package.json exports ────────────────────────────────────────────────────

;; Manually-curated exports preserved when the generator rewrites the
;; package.json exports field.
(def manual-exports
  {"./composables" {:import "./dist/composables/index.js"
                    :types  "./dist/composables/index.d.ts"}})

(defn update-package-exports
  [models]
  (let [pkg     (json/parse-string (slurp vue-pkg) true)
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
    (spit vue-pkg (json/generate-string updated {:pretty true}))))

;; ── Main ────────────────────────────────────────────────────────────────────

(defn -main []
  (println "Generating Vue 3 wrapper components...")

  (.mkdirs (io/file vue-src-dir))

  (let [all-models      (discover-models)
        exported-names  (load-package-exports)
        exported-models (filter #(contains? exported-names (:tag-name %)) all-models)]

    ;; Generate per-component .ts
    (doseq [model exported-models]
      (let [content (generate-component model)
            file    (io/file vue-src-dir (str (:tag-name model) ".ts"))]
        (spit file content)))

    ;; Generate barrel index.ts
    (spit (io/file vue-src-dir "index.ts")
          (generate-index exported-models))

    ;; Update package.json exports
    (update-package-exports exported-models)

    (println (str "Generated " (count exported-models) " Vue wrappers + index.ts"))))

(-main)
