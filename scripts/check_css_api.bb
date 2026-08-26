#!/usr/bin/env bb
;; check_css_api.bb — Check the cssProperties and cssParts the manifest
;; publishes against the component sources they were read from.
;;
;; metadata.bb reads a component's CSS carefully. This reads it crudely and
;; asserts the two agree.
;;
;; The crude reading must not share an assumption with the careful one, or the
;; two are wrong together. Ownership is the assumption that matters: a tag can
;; prefix another tag, so --x-table-cell-padding starts with --x-table- and a
;; prefix scan cannot say whose it is. `claimed-twice` checks instead that every
;; property is published exactly once, which is what the extractor's
;; longest-prefix rule is for and what a prefix scan cannot confirm about itself.
;;
;; A property mentioned once and never declared looks like a misspelling and is
;; not: it is one the page supplies, which is most of them. That heuristic was
;; tried, and produced 149 false positives and no true ones.
;;
;; Usage: bb scripts/check_css_api.bb

(load-file "scripts/metadata.bb")

(defn- source-of [dir-name]
  (component-source (io/file components-dir dir-name)))

(defn- mentions-own-property?
  "Does the source name any custom property namespaced to `tag`? A crude scan,
   deliberately not the extractor's."
  [tag source]
  (str/includes? source (str "--" tag "-")))

(defn- stamps-a-part? [source]
  (boolean (re-find #"\"part\"" source)))

(defn- malformed
  "What is wrong with the names and defaults a component published."
  [{:keys [tag-name css-properties css-parts]}]
  (concat
   (for [{:keys [name]} css-properties
         :when (not (re-matches #"--x-[a-z0-9-]+" name))]
     (str tag-name " published a malformed property name " (pr-str name)))
   (for [{:keys [name default]} css-properties
         :when (and (some? default) (re-find #"[\"{}]" default))]
     (str tag-name " published a malformed default for " name ": " (pr-str default)))
   (for [{:keys [name]} css-parts
         :when (not (re-matches #"[a-z0-9-]+" name))]
     (str tag-name " published a malformed part name " (pr-str name)))))

(defn- problems
  "Where the manifest and the source disagree about `model`."
  [{:keys [tag-name dir-name css-properties css-parts] :as model}]
  (let [source (source-of dir-name)]
    (concat
     (when (and (mentions-own-property? tag-name source) (empty? css-properties))
       [(str tag-name " writes --" tag-name "- properties that were not extracted")])
     (when (and (stamps-a-part? source) (empty? css-parts))
       [(str tag-name " stamps a part that was not extracted")])
     (malformed model))))

(defn- claimed-twice
  "Properties more than one component publishes. Ownership is decided by the
   longest tag that prefixes a property, so exactly one component should own
   each. This is the part a prefix scan cannot check about itself."
  [models]
  (->> (for [{:keys [tag-name css-properties]} models
             {:keys [name]} css-properties]
         [name tag-name])
       (reduce (fn [acc [n t]] (update acc n (fnil conj #{}) t)) {})
       (keep (fn [[n ts]] (when (< 1 (count ts))
                            (str n " is published by " (pr-str (vec (sort ts)))))))))

(let [models (discover-models)
      found  (concat (mapcat problems models) (claimed-twice models))
      props  (reduce + (map (comp count :css-properties) models))
      parts  (reduce + (map (comp count :css-parts) models))]
  (if (seq found)
    (do (doseq [p found] (println "  " p))
        (println (count found) "problems")
        (System/exit 1))
    (println (format "%d css properties and %d parts across %d components agree with their source"
                     props parts (count models)))))
