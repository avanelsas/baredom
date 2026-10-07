#!/usr/bin/env bb
;; check_attribute_api.bb — Check the attributes the manifest publishes against
;; the model sources they were read from.
;;
;; metadata.bb reads observed-attributes carefully, resolving each `attr-*`
;; symbol through the model's own string defs. This reads it crudely and asserts
;; the two agree.
;;
;; The crude reading must not share an assumption with the careful one. The
;; assumption that matters here is symbol resolution: `observed-attributes` names
;; symbols, and a symbol the careful reader cannot resolve falls through as its
;; own name. So this checks the published names against the string literals the
;; model actually declares, which is the one thing a resolver cannot confirm
;; about itself.
;;
;; A published name is also checked for shape. `attr-columns` is a symbol that
;; escaped resolution, and it looks exactly like an attribute until you read it.
;;
;; The obvious tightening is to check each name against the model's own
;; `(def attr-... "...")` constants rather than against every string it declares.
;; That convention does not hold: `x-date-picker` observes `open` and
;; `x-timeline-item` observes four `data-*` attributes, none declared that way. The
;; tightening was tried and produces five false positives.
;;
;; It also checks each property that reflects an attribute against the component.
;; The model says which attribute a property reflects, and the manifest takes the
;; attribute's type from that. The component installs the property somewhere else:
;; from the model's data, with a call that names the property and the attribute, or
;; in a hand-written block. A link that no install agrees with is reported.
;;
;; Usage: bb scripts/check_attribute_api.bb

(load-file "scripts/metadata.bb")

(defn- declared-strings
  "Every string a model declares, however it declares it. A crude scan,
   deliberately not the extractor's."
  [text]
  (into #{} (map second) (re-seq #"\"([^\"]+)\"" text)))

(defn- model-text [dir-name]
  (slurp (io/file components-dir dir-name "model.cljs")))

(defn- problems
  "Where the attributes a component publishes contradict its model."
  [{:keys [tag-name dir-name attributes]}]
  (let [text     (model-text dir-name)
        declared (declared-strings text)]
    (concat
     ;; Extraction that fails returns nothing rather than failing loudly, and a
     ;; component observing nothing is the shape that would take.
     (when (empty? attributes)
       [(str tag-name " published no attribute, so extraction read nothing")])
     (for [a attributes
           :when (not (re-matches #"[a-z][a-z0-9-]*" a))]
       (str tag-name " published a malformed attribute name " (pr-str a)))
     (for [a attributes
           :when (not (contains? declared a))]
       (str tag-name " publishes " (pr-str a) ", which its model never declares")))))

(require '[edamame.core :as edamame])

(defn- component-forms [dir-name]
  (mapcat (fn [^java.io.File f]
            (edamame/parse-string-all (slurp f) {:all true :readers {'js identity} :auto-resolve name}))
          (filter (fn [^java.io.File f]
                    (and (str/ends-with? (.getName f) ".cljs") (not= "model.cljs" (.getName f))))
                  (.listFiles (io/file components-dir dir-name)))))

(defn- text-of
  "The text a literal or a `model/` symbol stands for, or nil."
  [x string-defs]
  (cond
    (string? x) x
    (symbol? x) (get string-defs (symbol (name x)))))

(defn- subforms [forms]
  (filter seq? (tree-seq coll? seq forms)))

(defn- install-pair
  "The two texts an install call names, as a set, or nil. An install call takes `proto` and
   then a property and an attribute, in either order."
  [[head proto a b] string-defs]
  (let [texts (keep #(text-of % string-defs) [a b])]
    (when (and (symbol? head) (= 'proto proto) (= 2 (count texts)))
      (set texts))))

(defn- defines?
  "True when `form` defines the property `prop` and holds a symbol that stands for `attr`."
  [form prop attr string-defs]
  (let [[head _ _ defined] form]
    (and (= '.defineProperty head)
         (= prop (text-of defined string-defs))
         (some #(and (symbol? %) (= attr (text-of % string-defs)))
               (tree-seq coll? seq (drop 4 form))))))

(def ^:private installed-by-data
  "The property types that `du/install-properties!` installs."
  #{"boolean" "string" "number"})

(defn- links
  "Each reflected property of a component with how its install was found: `:call`, `:data` or
   `:block` when it agrees, `:wrong` with the pairs that differ, or `:missing`."
  [{:keys [tag-name dir-name properties string-defs]}]
  (let [forms (subforms (component-forms dir-name))
        pairs (into #{} (keep #(install-pair % string-defs)) forms)
        data? (some #{'(du/install-properties! proto model/property-api)} forms)]
    (for [[k {:keys [type reflects-attribute readonly]}] properties
          :when (and reflects-attribute (not readonly))
          :let  [prop   (name k)
                 attr   (resolve-sym reflects-attribute string-defs)
                 link   (set [prop attr])
                 others (filter #(some link %) pairs)]]
      {:tag tag-name :prop prop :attr attr :others others
       :how (cond
              (pairs link)                                              :call
              (and data? (installed-by-data (normalize-type-sym type))) :data
              (some #(defines? % prop attr string-defs) forms)          :block
              (seq others)                                              :wrong
              :else                                                     :missing)})))

(defn- link-problem [{:keys [tag prop attr others how]}]
  (case how
    :wrong   (str tag " declares that " prop " reflects " (pr-str attr)
                  ", and an install call names " (pr-str (mapv (comp vec sort) others)))
    :missing (str tag " declares that " prop " reflects " (pr-str attr)
                  ", and does not install it")
    nil))

(defn- untyped
  "The published attributes of a component that no property reflects."
  [{:keys [attributes properties string-defs]}]
  (let [reflected (into #{}
                        (keep #(some-> (:reflects-attribute %) (resolve-sym string-defs)))
                        (vals properties))]
    (remove reflected attributes)))

(let [models (discover-models)
      links  (mapcat links models)
      found  (concat (mapcat problems models) (keep link-problem links))
      total  (reduce + (map (comp count :attributes) models))
      how    (frequencies (map :how links))]
  (if (seq found)
    (do (doseq [p found] (println "  " p))
        (println (count found) "problems")
        (System/exit 1))
    (do (println (format "%d attributes across %d components agree with their model"
                         total (count models)))
        (println (format "%d reflect a property: %d by data, %d by an install call, %d in a hand-written block"
                         (count links) (:data how 0) (:call how 0) (:block how 0)))
        (println (format "%d attributes have no property that reflects them"
                         (count (mapcat untyped models)))))))
