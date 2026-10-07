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
    {:texts  (filter #(re-find #"\{[^{}]*:[^{}]*\}" %) (distinct (vals own)))
     :unread (sort (for [[sym text] own
                         :when (and (re-find #"\{[^{}]*:[^{}]*\}" text) (str/includes? text unread))]
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
   "color"        #"^(color|background|background-color|border-color|outline-color|fill|stroke|caret-color)$"
   "font-family"  #"^font-family$"
   "font-size"    #"^font-size$"
   "font-weight"  #"^font-weight$"
   "line-height"  #"^line-height$"
   "radius"       #"^border(-[a-z]+-[a-z]+)?-radius$"
   "shadow"       #"^box-shadow$"
   "transition"   #"^(transition|transition-duration|transition-timing-function)$"
   "space"        #"^(padding|margin|gap|row-gap|column-gap)(-[a-z]+)*$"
   "z"            #"^z-index$"
   "border-width" #"^border(-[a-z]+)?-width$"))

(def ^:private structural
  "Values that state structure and no design default, so no theme answers for them."
  #"(?i)^(0|0px|none|inherit|initial|unset|auto|normal|transparent|currentcolor|100%|50%|1|-1|[0-9]|infinite)\s*(!important)?$")

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

(defn- longhand
  "The declarations a border or an outline shorthand stands for: its width and its colour."
  [[prop value]]
  (if (re-find shorthand prop)
    (let [ps     (parts value)
          width  (first (filter #(re-find #"^(\d|\.\d|thin|medium|thick|var\(--x-[a-z0-9-]*width)" %) ps))
          colour (first (filter #(re-find #"^(#|rgb|hsl|var\(|currentcolor|transparent)" (str/lower-case %))
                                (remove #{width} ps)))]
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

(defn- fallbacks
  "The fallback of each own property that `value` reads with one, as a map."
  [value]
  (into {}
        (keep (fn [[_ prop fallback]] (when (own? prop) [prop fallback])))
        (re-seq #"var\(\s*(--x-[a-z0-9-]+)\s*,\s*((?:[^()]|\([^()]*\))*)\)" value)))

(defn- value-kind
  "How a value that names no own property relates to the tokens of x-theme."
  [value]
  (cond
    (str/includes? value unread) :unread
    (names-token? value)         :token
    (re-find structural value)   :structural
    :else                        :literal))

(defn- own-kind
  "The kind of an own property whose default is `value`."
  [value]
  (let [kind (value-kind value)]
    (get {:token :own-token :literal :own-literal} kind kind)))

(defn- family-by-use
  "A map from each own property to its family: what its name says, or else the family of a
   declaration that reads it."
  [decls]
  (into {}
        (for [[prop value] decls
              :let  [family (family-of prop)]
              used  (own-in value)
              :let  [found (or (family-by-name used) family)]
              :when found]
          [used found])))

(defn- declared-rows
  "One row for each own property a component declares, in the family that uses it."
  [tag decls]
  (let [family (family-by-use decls)]
    (for [[prop value] decls
          :when (and (own? prop) (family prop) (empty? (own-in value)))]
      {:tag tag :family (family prop) :prop prop :value value :kind (own-kind value)})))

(defn- used-rows
  "One row for each declaration of a family: its own value, or the fallback of each own
   property it reads. An own property read with no fallback is left to its declaration."
  [tag decls]
  (for [[prop value] decls
        :let  [family (family-of prop)]
        :when family
        row   (if (seq (own-in value))
                (for [[_ fallback] (fallbacks value)
                      :when (empty? (own-in fallback))]
                  {:value fallback :kind (own-kind fallback)})
                [{:value value :kind (value-kind value)}])]
    (merge {:tag tag :family family :prop prop} row)))

(defn- audited
  "A component with every value of its CSS that a token family answers for."
  [{:keys [tag-name dir-name]}]
  (let [{:keys [texts unread]} (styles dir-name)
        decls                  (mapcat longhand (declarations (str/join "\n" texts)))]
    {:tag    tag-name
     :read?  (boolean (seq texts))
     :unread unread
     :rows   (concat (declared-rows tag-name decls) (used-rows tag-name decls))}))

;; ── the report ──────────────────────────────────────────────────────────────

(def ^:private kinds [:token :own-token :own-literal :literal :structural :unread])

(defn- summary [rows]
  (println (format "%-13s %6s %9s %11s %7s %10s %6s   %s"
                   "family" "token" "own-token" "own-literal" "literal" "structural" "unread"
                   "components with a literal"))
  (doseq [family (keys families)
          :let [in-family (filter #(= family (:family %)) rows)
                counts    (frequencies (map :kind in-family))
                open      (count (distinct (map :tag (filter #(#{:literal :own-literal} (:kind %))
                                                             in-family))))]]
    (println (apply format "%-13s %6d %9d %11d %7d %10d %6d   %d"
                    family (concat (map #(get counts % 0) kinds) [open])))))

(defn- literals [rows]
  (doseq [[family in-family] (group-by :family (filter #(#{:literal :own-literal} (:kind %)) rows))]
    (println (str "\n" family))
    (doseq [[value n] (sort-by (comp - val) (frequencies (map :value in-family)))]
      (println (format "  %4d  %s" n value)))))

(defn- not-read
  "The components whose CSS was not read, or not in full, with the definitions concerned."
  [components]
  (for [{:keys [tag read? unread]} components
        :when (or (not read?) (seq unread))]
    (str tag (when (seq unread) (str " (" (str/join ", " unread) ")")))))

(let [components (map audited (remove #(= "x-theme" (:tag-name %)) (discover-models)))
      rows       (mapcat :rows components)
      report     (get {"summary" summary "literals" literals} (first *command-line-args*))]
  (when-not report
    (println "Usage: bb scripts/audit_tokens.bb summary|literals")
    (System/exit 1))
  (report rows)
  (println (format "\n%d values in %d components. Not read, or not in full: %s"
                   (count rows) (count (distinct (map :tag rows)))
                   (if-let [names (seq (not-read components))] (str/join ", " names) "none"))))
