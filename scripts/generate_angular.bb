#!/usr/bin/env bb
;; generate_angular.bb — Auto-generate Angular 17+ standalone directives from
;; BareDOM model.cljs metadata.
;;
;; Usage: bb scripts/generate_angular.bb

;; Load shared metadata utilities
(load-file "scripts/metadata.bb")

;; ── Configuration ───────────────────────────────────────────────────────────
(def angular-src-dir      "adapters/angular/src")
(def angular-directives-dir "adapters/angular/src/directives")
(def angular-cva-dir      "adapters/angular/src/cva")
(def angular-pkg          "adapters/angular/package.json")

;; Components that need ControlValueAccessor directives for Angular forms.
;; :value-type    — TypeScript type for the form control value
;; :change-event  — DOM event fired on committed value change
;; :detail-field  — field in event.detail that carries the new value
;; :write-mode    — :boolean-attr (set/remove attribute) or :string-attr (setAttribute)
;; :attr-name     — attribute name for writeValue (only for :string-attr)
(def cva-components
  {"x-checkbox"       {:value-type "boolean" :change-event "x-checkbox-change"       :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-switch"         {:value-type "boolean" :change-event "x-switch-change"         :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-radio"          {:value-type "boolean" :change-event "x-radio-change"          :detail-field "checked" :write-mode :boolean-attr :attr-name "checked"}
   "x-slider"         {:value-type "string"  :change-event "x-slider-change"         :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-text-area"      {:value-type "string"  :change-event "x-text-area-change"      :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-select"         {:value-type "string"  :change-event "x-select-change"         :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-combobox"       {:value-type "string"  :change-event "x-combobox-change"       :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-currency-field" {:value-type "string"  :change-event "x-currency-field-change" :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-tabs"           {:value-type "string"  :change-event "value-change"            :detail-field "value"   :write-mode :string-attr  :attr-name "value"}
   "x-pagination"     {:value-type "number"  :change-event "page-change"             :detail-field "page"    :write-mode :string-attr  :attr-name "page"}})

;; ── Angular-specific helpers ────────────────────────────────────────────────

(defn resolve-event-name
  "Resolve the actual DOM event name from an event-schema entry."
  [event-key event-info string-defs]
  (cond
    (symbol? event-key) (resolve-sym event-key (or string-defs {}))
    (keyword? event-key) (or (:event-name event-info) (name event-key))
    :else (str event-key)))

(defn event->angular-output
  "Convert a DOM event name to an Angular @Output() name.
   Strips the tag-name prefix if present, then camelCases.
   e.g. 'press' -> 'press'
        'x-alert-dismiss' (tag 'x-alert') -> 'dismiss'
        'press-start' -> 'pressStart'"
  [event-str tag-name]
  (let [prefix   (str tag-name "-")
        stripped (if (str/starts-with? event-str prefix)
                  (subs event-str (count prefix))
                  event-str)]
    (kebab->camel stripped)))

(defn prop-type->ts
  "Convert a property metadata type to TypeScript type."
  [prop-meta]
  (let [t (normalize-type-sym (:type prop-meta))]
    (case t
      "boolean" "boolean"
      "string"  "string"
      "number"  "number"
      "object"  "Record<string, any>"
      "any")))

(defn event-detail->ts
  "Generate TypeScript type for a CustomEvent detail."
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

(defn tag->directive-class
  "Convert 'x-button' to 'BaredomButton'.
   Strips the 'X' prefix and adds 'Baredom'."
  [tag-name]
  (let [iface (tag->interface-name tag-name)
        ;; Remove leading 'X' if present
        without-x (if (str/starts-with? iface "X")
                    (subs iface 1)
                    iface)]
    (str "Baredom" without-x)))

;; ── Directive generation ────────────────────────────────────────────────────

(defn generate-directive
  "Generate a standalone Angular directive .ts file for a component."
  [{:keys [tag-name properties events methods string-defs]}]
  (let [interface-name (tag->interface-name tag-name)
        directive-class (tag->directive-class tag-name)
        sdefs          (or string-defs {})

        ;; Properties: filter out readonly, build inputs
        writable-props (when properties
                         (->> properties
                              (remove (fn [[_ m]] (:readonly m)))
                              (remove (fn [[_ m]] (:read-only m)))))

        ;; Collect input names for collision detection
        input-names (set (map (fn [[k _]] (kebab->camel (name k))) writable-props))

        ;; Events: resolve names and build output mappings
        ;; Suffix output names that collide with input names
        event-entries  (when (and events (seq events))
                         (mapv (fn [[event-key event-info]]
                                 (let [dom-name    (resolve-event-name event-key event-info sdefs)
                                       base-name   (event->angular-output dom-name tag-name)
                                       output-name (if (contains? input-names base-name)
                                                     (str base-name "Event")
                                                     base-name)
                                       detail      (:detail event-info)]
                                   {:dom-name    dom-name
                                    :output-name output-name
                                    :detail-ts   (event-detail->ts detail)}))
                               events))

        has-events  (boolean (seq event-entries))
        has-inputs  (boolean (seq writable-props))

        ;; Collect output names for collision detection
        output-names (set (map :output-name event-entries))

        ;; Methods: public method wrappers (suffix with "Method" if name collides with an output)
        method-entries (when (and methods (seq methods))
                         (mapv (fn [[method-name method-info]]
                                 (let [base-name (kebab->camel (name method-name))
                                       safe-name (if (contains? output-names base-name)
                                                   (str base-name "Method")
                                                   base-name)]
                                   {:name safe-name
                                    :el-name (kebab->camel (name method-name))
                                    :args (:args method-info)
                                    :returns (cljs-type->ts (or (:returns method-info) 'void))}))
                               methods))

        ;; Angular imports
        ng-core-imports (cond-> ["Directive" "ElementRef"]
                          has-inputs  (conj "Input")
                          has-events  (into ["Output" "EventEmitter"])
                          has-events  (conj "OnInit")
                          has-events  (conj "OnDestroy")
                          has-events  (conj "NgZone"))

        ;; Build @Input() setter lines
        input-lines (when writable-props
                      (map (fn [[k m]]
                             (let [prop-name (kebab->camel (name k))
                                   ts-type   (prop-type->ts m)]
                               (str "  @Input() set " prop-name "(v: " ts-type ") { this.el." prop-name " = v as any; }")))
                           writable-props))

        ;; Build @Output() lines
        output-lines (when event-entries
                       (map (fn [{:keys [output-name detail-ts]}]
                              (str "  @Output() " output-name " = new EventEmitter<CustomEvent<" detail-ts ">>();"))
                            event-entries))

        ;; Build ngOnInit listen calls
        listen-calls (when event-entries
                       (map (fn [{:keys [dom-name output-name]}]
                              (str "    this.listen('" dom-name "', this." output-name ");"))
                            event-entries))

        ;; Build method wrappers
        method-lines (when method-entries
                       (map (fn [{:keys [name el-name args returns]}]
                              (let [ts-args (when (seq args)
                                              (map (fn [a]
                                                     (str (:name a) ": " (cljs-type->ts (:type a))))
                                                   args))
                                    args-str (if ts-args (str/join ", " ts-args) "")
                                    call-args (if (seq args)
                                                (str/join ", " (map :name args))
                                                "")]
                                (str "  " name "(" args-str "): " returns " { return this.el." el-name "(" call-args "); }")))
                            method-entries))

        ;; Interfaces to implement
        implements (concat
                     (when has-events ["OnInit"])
                     (when has-events ["OnDestroy"]))
        implements-str (if (seq implements)
                         (str " implements " (str/join ", " implements))
                         "")]

    (str "// " tag-name ".directive.ts — auto-generated by generate_angular.bb, do not edit\n\n"
         "import { " (str/join ", " ng-core-imports) " } from '@angular/core';\n"
         "import type { " interface-name " as " interface-name "Element } from '@vanelsas/baredom/" tag-name "';\n"
         "import { init } from '@vanelsas/baredom/" tag-name "';\n\n"
         "init();\n\n"
         "@Directive({ selector: '" tag-name "', standalone: true })\n"
         "export class " directive-class implements-str " {\n"
         "  private el: " interface-name "Element;\n"
         (when has-events "  private zone: NgZone;\n")
         (when has-events "  private listeners: Array<() => void> = [];\n")
         "\n"
         (if has-events
           (str "  constructor(elRef: ElementRef<" interface-name "Element>, zone: NgZone) {\n"
                "    this.el = elRef.nativeElement;\n"
                "    this.zone = zone;\n"
                "  }\n\n")
           (str "  constructor(elRef: ElementRef<" interface-name "Element>) {\n"
                "    this.el = elRef.nativeElement;\n"
                "  }\n\n"))
         ;; Inputs
         (when input-lines
           (str (str/join "\n" input-lines) "\n\n"))
         ;; Outputs
         (when output-lines
           (str (str/join "\n" output-lines) "\n\n"))
         ;; ngOnInit
         (when has-events
           (str "  ngOnInit(): void {\n"
                (when listen-calls
                  (str (str/join "\n" listen-calls) "\n"))
                "  }\n\n"))
         ;; ngOnDestroy
         (when has-events
           (str "  ngOnDestroy(): void {\n"
                "    this.listeners.forEach(fn => fn());\n"
                "  }\n\n"))
         ;; listen helper
         (when has-events
           (str "  private listen(event: string, emitter: EventEmitter<any>): void {\n"
                "    const handler = (e: Event) => this.zone.run(() => emitter.emit(e));\n"
                "    this.el.addEventListener(event, handler);\n"
                "    this.listeners.push(() => this.el.removeEventListener(event, handler));\n"
                "  }\n\n"))
         ;; Public method wrappers
         (when method-lines
           (str (str/join "\n" method-lines) "\n"))
         "}\n")))

;; ── CVA directive generation ────────────────────────────────────────────────

(defn generate-cva-directive
  "Generate a ControlValueAccessor directive .ts file for a form component."
  [{:keys [tag-name]} cva-config]
  (let [interface-name  (tag->interface-name tag-name)
        directive-class (str (tag->directive-class tag-name) "Cva")
        {:keys [value-type change-event detail-field write-mode attr-name]} cva-config
        selector (str tag-name "[formControlName]," tag-name "[formControl]," tag-name "[ngModel]")

        write-value-body
        (if (= write-mode :boolean-attr)
          (str "    if (v) this.el.setAttribute('" attr-name "', '');\n"
               "    else this.el.removeAttribute('" attr-name "');")
          (str "    if (v != null) this.el.setAttribute('" attr-name "', String(v));\n"
               "    else this.el.removeAttribute('" attr-name "');"))]

    (str "// " tag-name "-cva.directive.ts — auto-generated by generate_angular.bb, do not edit\n\n"
         "import { Directive, ElementRef, forwardRef, OnInit, OnDestroy, NgZone } from '@angular/core';\n"
         "import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';\n"
         "import type { " interface-name " as " interface-name "Element } from '@vanelsas/baredom/" tag-name "';\n"
         "import { init } from '@vanelsas/baredom/" tag-name "';\n\n"
         "init();\n\n"
         "@Directive({\n"
         "  selector: '" selector "',\n"
         "  standalone: true,\n"
         "  providers: [{\n"
         "    provide: NG_VALUE_ACCESSOR,\n"
         "    useExisting: forwardRef(() => " directive-class "),\n"
         "    multi: true,\n"
         "  }],\n"
         "})\n"
         "export class " directive-class " implements ControlValueAccessor, OnInit, OnDestroy {\n"
         "  private el: " interface-name "Element;\n"
         "  private zone: NgZone;\n"
         "  private listeners: Array<() => void> = [];\n"
         "\n"
         "  constructor(elRef: ElementRef<" interface-name "Element>, zone: NgZone) {\n"
         "    this.el = elRef.nativeElement;\n"
         "    this.zone = zone;\n"
         "  }\n"
         "\n"
         "  private onChange: (value: " value-type ") => void = () => {};\n"
         "  private onTouched: () => void = () => {};\n\n"
         "  ngOnInit(): void {\n"
         "    const handler = (e: Event) => {\n"
         "      const detail = (e as CustomEvent).detail;\n"
         "      this.zone.run(() => this.onChange(detail." detail-field "));\n"
         "    };\n"
         "    this.el.addEventListener('" change-event "', handler);\n"
         "    this.listeners.push(() => this.el.removeEventListener('" change-event "', handler));\n\n"
         "    const blur = () => this.zone.run(() => this.onTouched());\n"
         "    this.el.addEventListener('focusout', blur);\n"
         "    this.listeners.push(() => this.el.removeEventListener('focusout', blur));\n"
         "  }\n\n"
         "  ngOnDestroy(): void {\n"
         "    this.listeners.forEach(fn => fn());\n"
         "  }\n\n"
         "  writeValue(v: " value-type "): void {\n"
         write-value-body "\n"
         "  }\n\n"
         "  registerOnChange(fn: (v: " value-type ") => void): void {\n"
         "    this.onChange = fn;\n"
         "  }\n\n"
         "  registerOnTouched(fn: () => void): void {\n"
         "    this.onTouched = fn;\n"
         "  }\n\n"
         "  setDisabledState(d: boolean): void {\n"
         "    if (d) this.el.setAttribute('disabled', '');\n"
         "    else this.el.removeAttribute('disabled');\n"
         "  }\n"
         "}\n")))

;; ── Index generation ────────────────────────────────────────────────────────

(defn generate-index
  "Generate the barrel index.ts file."
  [models]
  (let [directive-exports
        (map (fn [{:keys [tag-name]}]
               (let [cls (tag->directive-class tag-name)]
                 (str "export { " cls " } from './directives/" tag-name ".directive';")))
             (sort-by :tag-name models))

        cva-exports
        (->> models
             (filter #(contains? cva-components (:tag-name %)))
             (sort-by :tag-name)
             (map (fn [{:keys [tag-name]}]
                    (let [cls (str (tag->directive-class tag-name) "Cva")]
                      (str "export { " cls " } from './cva/" tag-name "-cva.directive';")))))]

    (str "// index.ts — auto-generated by generate_angular.bb, do not edit\n\n"
         "// Directives\n"
         (str/join "\n" directive-exports)
         "\n\n// ControlValueAccessor directives\n"
         (str/join "\n" cva-exports)
         "\n\n// Types\n"
         "export type { BaredomEvent } from './types';\n")))

;; ── Package.json exports ────────────────────────────────────────────────────

(defn update-package-exports
  "Update the adapters/angular/package.json exports field."
  [models]
  (let [pkg     (json/parse-string (slurp angular-pkg) true)
        exports (into (sorted-map)
                      (concat
                       [["." {:import "./dist/index.js"
                              :types  "./dist/index.d.ts"}]]
                       (map (fn [{:keys [tag-name]}]
                              [(str "./" tag-name)
                               {:import (str "./dist/directives/" tag-name ".directive.js")
                                :types  (str "./dist/directives/" tag-name ".directive.d.ts")}])
                            (sort-by :tag-name models))
                       (->> models
                            (filter #(contains? cva-components (:tag-name %)))
                            (sort-by :tag-name)
                            (map (fn [{:keys [tag-name]}]
                                   [(str "./" tag-name "-cva")
                                    {:import (str "./dist/cva/" tag-name "-cva.directive.js")
                                     :types  (str "./dist/cva/" tag-name "-cva.directive.d.ts")}])))))
        updated (assoc pkg :exports exports)]
    (spit angular-pkg (json/generate-string updated {:pretty true}))))

;; ── Main ────────────────────────────────────────────────────────────────────
(defn -main []
  (println "Generating Angular 17+ standalone directives...")

  (.mkdirs (io/file angular-directives-dir))
  (.mkdirs (io/file angular-cva-dir))

  (let [all-models      (discover-models)
        exported-names  (load-package-exports)
        exported-models (filter #(contains? exported-names (:tag-name %)) all-models)]

    ;; Generate per-component directive .ts
    (doseq [model exported-models]
      (let [ts-content (generate-directive model)
            ts-file    (io/file angular-directives-dir (str (:tag-name model) ".directive.ts"))]
        (spit ts-file ts-content)))

    ;; Generate CVA directives for form components
    (doseq [model exported-models
            :let [cva-config (get cva-components (:tag-name model))]
            :when cva-config]
      (let [ts-content (generate-cva-directive model cva-config)
            ts-file    (io/file angular-cva-dir (str (:tag-name model) "-cva.directive.ts"))]
        (spit ts-file ts-content)))

    ;; Generate barrel index.ts
    (spit (io/file angular-src-dir "index.ts")
          (generate-index exported-models))

    ;; Update package.json exports
    (update-package-exports exported-models)

    (let [cva-count (count (filter #(contains? cva-components (:tag-name %)) exported-models))]
      (println (str "Generated " (count exported-models) " directives + "
                    cva-count " CVA directives + index.ts")))))

(-main)
