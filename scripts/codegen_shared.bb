;; codegen_shared.bb — Framework-agnostic codegen helpers shared by every
;; adapter generator (generate_react.bb, generate_vue.bb, generate_angular.bb,
;; generate_svelte.bb).
;;
;; Distinct from metadata.bb: that one *reads* model.cljs; this one *emits*
;; TypeScript snippets that every adapter needs identically.
;;
;; Depends on metadata.bb (normalize-type-sym, kebab->camel, cljs-type->ts).
;; Load metadata.bb first.
;;
;; Usage: (load-file "scripts/codegen_shared.bb")

(require '[clojure.string :as str])

;; ── Event-schema → DOM event name ───────────────────────────────────────────

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

;; ── Property type → TypeScript ──────────────────────────────────────────────

(defn prop-type->ts
  "Convert a property metadata type to a TypeScript prop type."
  [prop-meta]
  (let [t (normalize-type-sym (:type prop-meta))]
    (case t
      "boolean" "boolean"
      "string"  "string"
      "number"  "number"
      "object"  "Record<string, any>"
      "any")))

;; ── CustomEvent detail → TypeScript object type ─────────────────────────────

(defn event-detail->ts
  "Generate a TypeScript type for a CustomEvent detail.
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
