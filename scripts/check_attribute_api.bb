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

(let [models (discover-models)
      found  (mapcat problems models)
      total  (reduce + (map (comp count :attributes) models))]
  (if (seq found)
    (do (doseq [p found] (println "  " p))
        (println (count found) "problems")
        (System/exit 1))
    (println (format "%d attributes across %d components agree with their model"
                     total (count models)))))
