#!/usr/bin/env bb
;; audit_tokens.bb — Say, for every declaration in every component's CSS, whether
;; its value follows a design token of x-theme.
;;
;; A component writes its CSS as text in ClojureScript, joined from literals and
;; constants. This evaluates those joins, so a declaration is read as the browser
;; gets it. A value that only exists at run time stays unread, and is counted.
;;
;; Usage: bb scripts/audit_tokens.bb summary    the kinds of value, per family
;;        bb scripts/audit_tokens.bb literals   every literal value, by family
;;        bb scripts/audit_tokens.bb doc        write docs/TOKEN-COVERAGE.md

(load-file "scripts/metadata.bb")
(require '[edamame.core :as edamame])

;; ── the CSS text of a component ─────────────────────────────────────────────

(def ^:private unread "⁇")

(defn- forms-of [^java.io.File file]
  (edamame/parse-string-all (slurp file) {:all true :readers {'js identity} :auto-resolve name}))

(defn- text-of
  "The text `form` stands for, with `unread` where a part is only known at run time."
  [env form]
  (cond
    (string? form) form
    (number? form) (str form)
    (symbol? form) (or (get env form) (get env (symbol (name form))) unread)
    (vector? form) (str/join (map #(text-of env %) form))
    (seq? form)    (let [[head & args] form]
                     (case head
                       str       (str/join (map #(text-of env %) args))
                       str/join  (if (= 2 (count args))
                                   (str/join (text-of env (first args))
                                             (map #(text-of env %) (second args)))
                                   (text-of env (first args)))
                       let       (text-of (reduce (fn [env [sym value]]
                                                    (assoc env sym (text-of env value)))
                                                  env
                                                  (partition 2 (first args)))
                                          (last args))
                       unread))
    :else unread))

(defn- defined
  "The name and the value form of a `def`, or of a function that takes no argument, or nil."
  [form]
  (when (seq? form)
    (let [[head sym & more] form]
      (cond
        (and (= 'def head) (symbol? sym))                          [sym (last more)]
        (and (#{'defn 'defn-} head) (some #{[]} (take 2 more)))    [sym (last more)]))))

(defn- definitions
  "A map from each name a file defines to its text, built in the order of the file."
  [env forms]
  (reduce (fn [env form]
            (if-let [[sym value] (defined form)]
              (assoc env sym (text-of env value))
              env))
          env
          forms))

(def ^:private shared
  "The definitions of the shared DOM utilities, which a style text may name."
  (definitions {} (forms-of (io/file "src/baredom/utils/dom.cljs"))))

(def ^:private rule #"\{[^{}]*:[^{}]*\}")

(defn- model-file? [^java.io.File file]
  (= "model.cljs" (.getName file)))

(defn- styles
  "The CSS of a component: the texts it defines that hold a rule, and the names of those that
   were not read in full."
  [dir-name]
  (let [files (filter #(str/ends-with? (.getName ^java.io.File %) ".cljs")
                      (.listFiles (io/file components-dir dir-name)))
        model (definitions shared (mapcat forms-of (filter model-file? files)))
        own   (apply dissoc
                     (definitions model (mapcat forms-of (remove model-file? files)))
                     (keys shared))]
    {:texts  (filter #(re-find rule %) (distinct (vals own)))
     :unread (sort (for [[sym text] own
                         :when (and (re-find rule text) (str/includes? text unread))]
                     (name sym)))}))

;; ── declarations ────────────────────────────────────────────────────────────

(defn- declarations
  "Every `property: value` of a CSS text, as pairs."
  [css]
  (for [[_ body] (re-seq #"\{([^{}]*)\}" (str/replace css #"/\*.*?\*/" ""))
        decl     (str/split body #";")
        :let     [[prop value] (map str/trim (str/split decl #":" 2))]
        :when    (and (seq prop) (seq value))]
    [prop value]))

(def ^:private families
  "The token families of x-theme and the CSS properties each one answers for."
  (array-map
   "color"        (re-pattern (str "^(color|background|background-color|border-color"
                                   "|outline-color|fill|stroke|caret-color)$"))
   "font-family"  #"^font-family$"
   "font-size"    #"^font-size$"
   "font-weight"  #"^font-weight$"
   "line-height"  #"^line-height$"
   "radius"       #"^border(-[a-z]+-[a-z]+)?-radius$"
   "shadow"       #"^box-shadow$"
   "transition"   #"^(transition|animation)(-duration|-timing-function)?$"
   "space"        #"^(padding|margin|gap|row-gap|column-gap)(-[a-z]+)*$"
   "z"            #"^z-index$"
   "border-width" #"^border(-[a-z]+)?-width$"))

(def ^:private structural
  "Values that state structure and no design default, so no theme answers for them."
  (re-pattern (str "(?i)^(0|0px|0ms|0s|none|inherit|initial|unset|auto|normal|transparent"
                   "|currentcolor|100%|50%|1|-1|[0-9]|infinite)\\s*(!important)?$")))

(defn- family-of [prop]
  (some (fn [[family re]] (when (re-find re prop) family)) families))

(def ^:private named-families
  "What the name of a component's own property says about its family, most specific first."
  [[#"border-width"                        "border-width"]
   [#"radius"                              "radius"]
   [#"shadow"                              "shadow"]
   [#"font-family"                         "font-family"]
   [#"font-size"                           "font-size"]
   [#"font-weight|weight"                  "font-weight"]
   [#"line-height"                         "line-height"]
   [#"duration|easing|transition"          "transition"]
   [#"padding|margin|gap|spacing"          "space"]
   [#"z-index"                             "z"]
   [#"color|background|bg$|-bg-|border|fg$|fill|stroke|ring|outline" "color"]])

(defn- family-by-name [prop]
  (some (fn [[re family]] (when (re-find re prop) family)) named-families))

(def ^:private shorthand #"^(border|outline)(-(top|right|bottom|left|block|inline))?$")

(defn- parts
  "The parts of a value, split at the spaces that are outside parentheses."
  [value]
  (re-seq #"(?:[^\s()]+|\((?:[^()]|\([^()]*\))*\))+" value))

(def ^:private width-part #"^(\d|\.\d|thin|medium|thick|var\(--x-[a-z0-9-]*width)")
(def ^:private colour-part #"(?i)^(#|rgb|hsl|var\(|currentcolor|transparent)")

(defn- longhand
  "The declarations a border or an outline shorthand stands for: its width and its colour."
  [[prop value]]
  (if (re-find shorthand prop)
    (let [ps     (parts value)
          width  (first (filter #(re-find width-part %) ps))
          colour (first (filter #(re-find colour-part %) (remove #{width} ps)))]
      (cond-> []
        (and width (str/starts-with? prop "border")) (conj ["border-width" width])
        colour                                       (conj ["border-color" colour])
        (and (not width) (not colour))               (conj ["border-width" value])))
    [[prop value]]))

;; ── the tokens ──────────────────────────────────────────────────────────────

(def ^:private catalogue
  (set (map second (re-seq #"\(def tk-[a-z0-9-]+\s+\"(--x-[a-z0-9-]+)\"\)"
                           (slurp (io/file components-dir "x_theme" "model.cljs"))))))

(defn- names-token? [value]
  (some catalogue (re-seq #"--x-[a-z0-9-]+" value)))

(defn- own? [prop]
  (and (str/starts-with? prop "--x-") (not (catalogue prop))))

(defn- own-in
  "The own properties that `value` names."
  [value]
  (filter own? (re-seq #"--x-[a-z0-9-]+" value)))

(defn- depth-after
  "The depth of parentheses after the character `c`, from `depth` before it."
  [depth c]
  (case c \( (inc depth) \) (dec depth) depth))

(defn- closing
  "The index of the parenthesis that closes the one opened just before `from` in `text`."
  [text from]
  (some->> (rest (reductions depth-after 1 (subs text from)))
           (keep-indexed (fn [at depth] (when (zero? depth) at)))
           first
           (+ from)))

(defn- fallbacks
  "The fallback of each own property that `value` reads with one, as a map. A property that
   `value` reads twice has the fallback of its first reading."
  [value]
  (into {}
        (for [[opening prop] (re-seq #"var\(\s*(--x-[a-z0-9-]+)\s*," value)
              :let  [from (+ (str/index-of value opening) (count opening))
                     end  (closing value from)]
              :when (and end (own? prop))]
          [prop (str/trim (subs value from end))])))

(defn- structural?
  "True when `value` states structure and no design default of `family`. A font size in `em` is
   relative to the text around it."
  [family value]
  (or (re-find structural value)
      (and (= "font-size" family) (re-find #"^[\d.]+em$" value))))

(defn- value-kind
  "How a value of `family` that names no own property relates to the tokens of x-theme."
  [family value]
  (cond
    (str/includes? value unread) :unread
    (names-token? value)         :token
    (structural? family value)   :structural
    :else                        :literal))

(defn- own-kind
  "The kind of an own property of `family` whose default is `value`."
  [family value]
  (let [kind (value-kind family value)]
    (get {:token :own-token :literal :own-literal} kind kind)))

(defn- outside-calc
  "`value` without its `calc()` expressions."
  [value]
  (if-let [at (str/index-of value "calc(")]
    (let [end (or (closing value (+ at 5)) (dec (count value)))]
      (str (subs value 0 at) (outside-calc (subs value (inc end)))))
    value))

(defn- family-by-use
  "A map from each own property to its family: what its name says, or else the family of a
   declaration that reads it. A property that is an operand in `calc()` is not read as a
   value of that declaration."
  [decls]
  (into {}
        (for [[prop value] decls
              :let  [family (family-of prop)]
              used  (own-in (outside-calc value))
              :let  [found (or (family-by-name used) family)]
              :when found]
          [used found])))

(defn- declared-rows
  "One row for each own property a component declares, in the family that uses it."
  [tag decls]
  (let [family (family-by-use decls)]
    (for [[prop value] decls
          :when (and (own? prop) (family prop) (empty? (own-in value)))]
      {:tag tag :family (family prop) :prop prop :value value
       :kind (own-kind (family prop) value)})))

(defn- used-rows
  "One row for each declaration of a family: its own value, or the fallback of each own
   property it reads, in the family that the name of that property says. An own property read
   with no fallback is left to its declaration."
  [tag decls]
  (for [[prop value] decls
        :let  [family (family-of prop)]
        :when family
        row   (if (seq (own-in value))
                (for [[own fallback] (fallbacks value)
                      :when (empty? (own-in fallback))
                      :let  [named (or (family-by-name own) family)]]
                  {:family named :value fallback :kind (own-kind named fallback)})
                [{:value value :kind (value-kind family value)}])]
    (merge {:tag tag :family family :prop prop} row)))

(def ^:private by-design
  "The values that follow no token on purpose: pairs of a reason and its values, each value a
   component, a property and the value."
  (edn/read-string (slurp "scripts/tokens_by_design.edn")))

(def ^:private exempt
  "Every value that is not themed by design."
  (set (mapcat second by-design)))

(defn- marked
  "`row` with the kind `:by-design` when it is not themed on purpose."
  [{:keys [tag prop value] :as row}]
  (cond-> row (exempt [tag prop value]) (assoc :kind :by-design)))

(defn- audited
  "A component with every value of its CSS that a token family answers for."
  [{:keys [tag-name dir-name]}]
  (let [{:keys [texts unread]} (styles dir-name)
        decls                  (mapcat longhand (declarations (str/join "\n" texts)))]
    {:tag    tag-name
     :read?  (boolean (seq texts))
     :unread unread
     :rows   (map marked (concat (declared-rows tag-name decls) (used-rows tag-name decls)))}))

;; ── the report ──────────────────────────────────────────────────────────────

(def ^:private kinds [:token :own-token :own-literal :literal :by-design :structural :unread])

(def ^:private themed
  "The kinds of value that follow the theme."
  #{:token :own-token})

(def ^:private open
  "The kinds of value that do not follow the theme and could."
  #{:own-literal :literal})

(defn- in-family [family rows]
  (filter #(= family (:family %)) rows))

(defn- summary [components]
  (let [rows (mapcat :rows components)]
    (println (format "%-13s %6s %9s %11s %7s %9s %10s %6s   %s"
                     "family" "token" "own-token" "own-literal" "literal" "by-design" "structural"
                     "unread" "components with an open value"))
    (doseq [family (keys families)
            :let [found  (in-family family rows)
                  counts (frequencies (map :kind found))
                  tags   (distinct (map :tag (filter (comp open :kind) found)))]]
      (println (apply format "%-13s %6d %9d %11d %7d %9d %10d %6d   %d"
                      family (concat (map #(get counts % 0) kinds) [(count tags)]))))))

(defn- literals [components]
  (doseq [[family found] (group-by :family (filter (comp open :kind) (mapcat :rows components)))]
    (println (str "\n" family))
    (doseq [[value n] (sort-by (comp - val) (frequencies (map :value found)))]
      (println (format "  %4d  %s" n value)))))

(def ^:private read-name #"var\(\s*(--x-[a-z0-9-]*[a-z0-9])\s*[,)]")

(defn- unknown-names
  "The `--x-` names a component reads that are neither a token of x-theme nor a property of a
   component, as a message for each."
  [tags {:keys [tag-name dir-name]}]
  (let [source (component-source (io/file components-dir dir-name))]
    (for [prop (sort (distinct (map second (re-seq read-name source))))
          :when (not (or (catalogue prop)
                         (some #(str/starts-with? prop (str "--" % "-")) tags)))]
      (str tag-name " reads " prop ", which x-theme does not define"))))

(defn- stale
  "The values listed as not themed by design that no component has, as a message for each."
  [rows]
  (let [found (set (map (juxt :tag :prop :value) rows))]
    (for [[tag prop value :as listed] (sort exempt)
          :when (not (found listed))]
      (str tag " is listed as not themed by design for " prop ": " value
           ", which it does not have"))))

(def ^:private closed
  "The families in which every value follows the theme or is listed as not themed by design."
  #{"font-family" "font-weight" "radius" "shadow" "transition"})

(defn- reopened
  "The values of a closed family that follow no token, as a message for each."
  [rows]
  (for [{:keys [tag family prop value kind]} rows
        :when (and (closed family) (open kind))]
    (str tag " has " prop ": " value ", which follows no token of the closed family " family)))

(defn- not-read
  "The components whose CSS was not read, or not in full, with the definitions concerned."
  [components]
  (for [{:keys [tag read? unread]} components
        :when (or (not read?) (seq unread))]
    (str tag (when (seq unread) (str " (" (str/join ", " unread) ")")))))

(def ^:private doc-file "docs/TOKEN-COVERAGE.md")

(def ^:private intro
  ["# Token coverage"
   ""
   "Generated by `bb scripts/audit_tokens.bb doc`. Do not edit."
   ""
   "Each cell says how many values of that family follow a token of `x-theme`, out of all the"
   "values of that family in the component's CSS. A value follows the theme when it is a token,"
   "or the component's own property whose default is a token. Structural values such as `0`,"
   "`none` and `inherit` are not counted, and neither are the values listed at the end as not"
   "themed by design. An empty cell means the component has no value of that family."
   ""
   (str "Closed families, in which CI allows no value that follows no token: "
        (str/join ", " (sort closed)) ".")
   ""])

(def ^:private by-design-lines
  "The values that are not themed by design, as lines of Markdown."
  (concat
   ["## Not themed by design" ""]
   (mapcat (fn [[reason values]]
             (concat [reason ""]
                     (for [[tag prop value] values]
                       (str "- `" tag "`: `" prop ": " value "`"))
                     [""]))
           by-design)))

(defn- cell
  "How many of `rows` follow the theme, as `themed/all`, or nothing when there are none."
  [rows]
  (let [found (filter (some-fn themed open) (map :kind rows))]
    (if (seq found)
      (str (count (filter themed found)) "/" (count found))
      "")))

(defn- table-row [label rows]
  (str "| " (str/join " | " (cons label (map #(cell (in-family % rows)) (keys families)))) " |"))

(defn- coverage-doc
  "The coverage of every component as a Markdown document."
  [components]
  (let [missing (not-read components)]
    (str/join
     "\n"
     (concat
      intro
      [(str "| " (str/join " | " (cons "Component" (keys families))) " |")
       (str "| " (str/join " | " (repeat (inc (count families)) "---")) " |")]
      (for [{:keys [tag rows]} (sort-by :tag components)]
        (table-row (str "`" tag "`") rows))
      [(table-row "**All**" (mapcat :rows components))
       ""
       (str "CSS not read, or not in full: " (if (seq missing) (str/join ", " missing) "none") ".")
       ""]
      by-design-lines))))

(defn- write-doc! [components]
  (spit doc-file (coverage-doc components))
  (println "Wrote" doc-file))

(let [models     (remove #(= "x-theme" (:tag-name %)) (discover-models))
      components (map audited models)
      unknown    (mapcat (partial unknown-names (all-tag-names)) models)
      rows       (mapcat :rows components)
      missing    (not-read components)
      report     (get {"summary" summary "literals" literals "doc" write-doc!}
                      (first *command-line-args*))]
  (when-not report
    (println "Usage: bb scripts/audit_tokens.bb summary|literals|doc")
    (System/exit 1))
  (when-let [faults (seq (concat unknown (stale rows) (reopened rows)))]
    (run! println faults)
    (System/exit 1))
  (report components)
  (println (format "\n%d values in %d components. Not read, or not in full: %s"
                   (count rows) (count (distinct (map :tag rows)))
                   (if (seq missing) (str/join ", " missing) "none"))))
