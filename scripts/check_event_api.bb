#!/usr/bin/env bb
;; check_event_api.bb — Check the event details the manifest publishes against
;; the dispatch sites they describe.
;;
;; metadata.bb reads `event-schema`, a declaration. This reads what a component
;; actually sends, which is a different thing in a different file, and asserts
;; the two agree.
;;
;; The declaration is what every consumer is typed from — the .d.ts, the
;; manifest and five framework adapters all come from it — so a schema that
;; drifts from the dispatch is worse than no schema: it is wrong with authority.
;;
;; The crude reading must not share an assumption with the careful one. This
;; reads the dispatch calls out of the implementation; the extractor reads a
;; `def` out of the model. Neither confirms the other by restating it.
;;
;; It reads forms rather than text. A first version scanned for `#js {...}` at
;; the call site, which missed every detail built anywhere else — and then a
;; refactor moved x-date-picker's details into a helper and the check went from
;; reading five sites to reading none, while still reporting agreement. A regex
;; cannot tell "nothing to see" from "cannot see".
;;
;; What it cannot resolve it says so about, and what it did not check it does not
;; claim. A check that overstates its coverage is the failure it exists to catch.
;;
;; Usage: bb scripts/check_event_api.bb

(load-file "scripts/metadata.bb")
(require '[cheshire.core :as json]
         '[clojure.set :as set]
         '[clojure.walk :as walk]
         '[edamame.core :as edamame])

(def ^:private unresolved ::unresolved)

(def ^:private parse-opts
  ;; `#js` is the only reader this needs to understand, and it understands it as
  ;; "the collection that follows", which is all a key set asks of it.
  {:all true :readers {'js identity} :auto-resolve name})

;; ── what a component sends ──────────────────────────────────────────────────

(def ^:private dispatch-fns
  "Where the detail sits in each call. `dispatch-document!` takes no element, so
   its detail is one argument earlier — a difference a positional scan gets
   silently wrong."
  {'du/dispatch!            2
   'du/dispatch-cancelable! 2
   'du/dispatch-document!   1})

(defn- bindings-in
  "Every symbol a file binds to an expression: `let` locals and `defn` bodies
   alike, because a detail is reached the same way through either.

   A name maps to every expression bound to it, not the last one seen. `detail`
   is bound in half a dozen scopes in one file, and a flat map let one scope's
   binding answer for another's — which read as the component sending a key it
   never sends."
  [forms]
  (let [acc (atom {})]
    (walk/postwalk
     (fn [form]
       (when (seq? form)
         (let [[head & more] form]
           (cond
             (and (#{'defn 'defn-} head) (symbol? (first more)))
             (swap! acc update (first more) (fnil conj #{}) (last form))

             (and (#{'let 'let* 'when-let 'if-let} head) (vector? (first more)))
             (doseq [[sym expr] (partition 2 (first more))
                     :when (symbol? sym)]
               (swap! acc update sym (fnil conj #{}) expr)))))
       form)
     forms)
    @acc))

(defn- lookup
  "Every expression bound to `sym`, reached through an alias if it carries one. A
   component's own files are the only ones read, so `model/toggle-detail` and the
   `toggle-detail` its model defines are the same function."
  [env sym]
  (or (get env sym)
      (when (namespace sym) (get env (symbol (name sym))))))

(defn- one-of
  "The keys every candidate agrees on, or unresolved when they disagree. A name
   bound in two scopes cannot be told apart here, so it is not guessed at."
  [ks]
  (cond
    (empty? ks)            unresolved
    (some #{unresolved} ks) unresolved
    (apply = ks)           (first ks)
    :else                  unresolved))

(declare detail-keys)

(defn- branch-keys
  "The union over branches, unresolved if any branch is."
  [env exprs depth]
  (let [ks (map #(detail-keys env % depth) exprs)]
    (if (some #{unresolved} ks) unresolved (reduce into #{} ks))))

(defn- detail-keys
  "The keys `expr` names, or `unresolved`. Follows a symbol to what bound it and
   a call to what the callee returns, which is how a detail built in a helper is
   read at all."
  [env expr depth]
  (cond
    (> depth 8)    unresolved
    ;; Both spellings. A map that goes through `clj->js` may be keyed by strings,
    ;; and dropping those silently read as a detail with no keys at all rather
    ;; than one this could not understand.
    (map? expr)    (into #{} (comp (filter (some-fn keyword? string?))
                                   (map name))
                         (keys expr))
    (symbol? expr) (one-of (map #(detail-keys env % (inc depth)) (lookup env expr)))
    (seq? expr)
    (let [[head & args] expr]
      (cond
        (= 'if head)              (branch-keys env (rest args) (inc depth))
        (#{'cond} head)           (branch-keys env (take-nth 2 (rest args)) (inc depth))
        (#{'do 'when 'when-not} head) (detail-keys env (last args) (inc depth))
        ;; Transparent: it changes the representation, never the keys.
        (= 'clj->js head)         (detail-keys env (first args) (inc depth))
        (lookup env head)         (one-of (map #(detail-keys env % (inc depth))
                                              (lookup env head)))
        :else                     unresolved))
    :else unresolved))

(defn- sent
  "What each event carries, gathered across every site that sends it, plus the
   sites that could not be read.

   The union rather than each site alone, because a detail may vary by mode and
   no one call sends every key."
  [forms env]
  (let [acc (atom {}) blind (atom [])]
    (walk/postwalk
     (fn [form]
       (when (seq? form)
         (when-let [at (dispatch-fns (first form))]
           (let [args  (vec (rest form))
                 event (get args (dec at))
                 ks    (if (< at (count args))
                         (detail-keys env (get args at) 0)
                         #{})]                      ; the no-detail arity sends none
             (when (symbol? event)
               (let [nm (str/replace (str event) "model/" "")]
                 (if (= unresolved ks)
                   (swap! blind conj nm)
                   (swap! acc update nm (fnil into #{}) ks)))))))
       form)
     forms)
    {:sent @acc :blind (set @blind)}))

;; ── the comparison ──────────────────────────────────────────────────────────

(defn- read-component [dir-name]
  (let [dir (io/file components-dir dir-name)]
    (mapcat (fn [^java.io.File f]
              (edamame/parse-string-all (slurp f) parse-opts))
            (filter #(str/ends-with? (.getName ^java.io.File %) ".cljs")
                    (.listFiles dir)))))

(defn- checked
  "One component's declared events against what it sends."
  [{:keys [tag-name dir-name events string-defs]}]
  (let [forms          (read-component dir-name)
        env            (bindings-in forms)
        {:keys [sent blind]} (sent forms env)
        resolve-name   (fn [s] (resolve-sym (symbol s) string-defs))
        sent-by-name   (into {} (map (fn [[s ks]] [(resolve-name s) ks])) sent)
        blind-by-name  (into #{} (map resolve-name) blind)
        declared       (into {} (map (fn [[k v]] [(resolve-sym k string-defs)
                                                  (set (keys (:detail v)))]))
                             events)
        declared       (update-vals declared #(set (map name %)))]
    {:tag      tag-name
     ;; An event with a site this could not read is not compared at all: a
     ;; partial union would report the keys it missed as undeclared.
     :compared (set (remove blind-by-name (keys sent-by-name)))
     :unread   (set/union blind-by-name
                          (set (remove (some-fn sent-by-name blind-by-name) (keys declared))))
     :problems
     (concat
      (for [[event _] sent-by-name
            :when (and (not (contains? declared event))
                       (not (blind-by-name event)))]
        (str tag-name " sends " event ", which its event-schema does not declare"))
      (for [[event ks] sent-by-name
            :let  [d (get declared event)]
            :when (and d (not (blind-by-name event)) (seq (set/difference ks d)))]
        (str tag-name " sends " event " with "
             (pr-str (vec (sort (set/difference ks d))))
             ", which it does not declare"))
      (for [[event d] declared
            :let  [ks (get sent-by-name event)]
            :when (and ks (not (blind-by-name event)) (seq (set/difference d ks)))]
        (str tag-name " declares " event " with "
             (pr-str (vec (sort (set/difference d ks))))
             ", which no dispatch site sends")))}))

(defn- published
  "Where what the manifest publishes differs from what the model declares.

   The two checks above read the model on both sides, so anything the emitter does
   between them is invisible to them: camel-casing a detail key published `pressX`
   for a burst that dispatches `press-x`, and nothing noticed.

   Field types as well as names, and over the union of the two sides. Reading only
   the manifest would miss the ordinary way this goes stale, which is a schema
   edited and the generator not run: the event is absent from the manifest, so
   there is nothing to iterate and nothing to report."
  [{:keys [tag-name events string-defs]} manifest]
  ;; Spelled out rather than borrowed from `event-detail-fields`. That is the
  ;; emitter's own function, and using it here made both sides agree by
  ;; construction: with the camel-casing put back this reported nothing, which is
  ;; the one bug it was written to catch. A check may not share the step it checks.
  (let [fields    (fn [detail]
                    (into (sorted-map)
                          (map (fn [[k v]] [(name k) (cljs-type->ts v)]))
                          (when (map? detail) detail)))
        declared  (into {} (map (fn [[k v]] [(resolve-sym k string-defs)
                                             (fields (:detail v))]))
                        events)
        ;; The manifest is read with keywordised keys, so a detail field arrives as
        ;; `:press-x` and is named back.
        emitted   (into {} (map (fn [e] [(:name e)
                                         (into (sorted-map)
                                               (map (juxt (comp name key) val))
                                               (:detail e))]))
                        (get manifest tag-name))]
    (for [event (sort (set/union (set (keys declared)) (set (keys emitted))))
          :let  [d (get declared event) e (get emitted event)]
          :when (not= d e)]
      (cond
        (nil? e) (str tag-name " declares " event ", which the manifest does not publish")
        (nil? d) (str tag-name " does not declare " event ", which the manifest publishes")
        :else    (str tag-name " declares " event " as " (pr-str d)
                      " and the manifest publishes " (pr-str e))))))

(defn- manifest-events
  "Each tag's published events, from the manifest as it stands on disk."
  []
  (into {}
        (for [m (:modules (json/parse-string (slurp "custom-elements.json") true))
              d (:declarations m)
              :when (:tagName d)]
          [(:tagName d) (:events d)])))

(let [models  (filter (comp seq :events) (discover-models))
      manifest (manifest-events)
      results (map checked models)
      found   (concat (mapcat :problems results)
                      (mapcat #(published % manifest) models))
      n-cmp   (reduce + (map (comp count :compared) results))
      n-unr   (reduce + (map (comp count :unread) results))]
  (doseq [p found] (println "  " p))
  (println (format "%d events compared against the sites that dispatch them, %d not read"
                   n-cmp n-unr))
  (when (pos? n-unr)
    (doseq [{:keys [tag unread]} results
            :when (seq unread)]
      (println "    unread:" tag (pr-str (vec (sort unread))))))
  (when (seq found)
    (println (count found) "problems")
    (System/exit 1)))
