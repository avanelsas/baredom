(ns baredom.components.x-toc.model
  (:require [clojure.string :as str]))

;; ── Tag & attribute constants ──────────────────────────────────────────────
(def tag-name "x-toc")

(def attr-for         "for")
(def attr-selector    "selector")
(def attr-label       "label")
(def attr-offset      "offset")
(def attr-sticky      "sticky")
(def attr-update-hash "update-hash")

(def observed-attributes
  #js [attr-for attr-selector attr-label attr-offset attr-sticky attr-update-hash])

;; ── Attributes written on the entries ─────────────────────────────────────
(def attr-id       "id")
(def attr-tabindex "tabindex")

(def foreign-writes
  "The attributes x-toc writes on the entries of its container."
  [{:target :any :attributes #{attr-id attr-tabindex}}])

;; ── Event constants ───────────────────────────────────────────────────────
(def event-change   "x-toc-change")
(def event-navigate "x-toc-navigate")

;; ── Defaults ──────────────────────────────────────────────────────────────
(def default-selector "h2, h3")
(def default-offset   "25%")
(def default-label    "Table of contents")
(def fallback-slug    "section")

;; ── Offset ────────────────────────────────────────────────────────────────
(def ^:private offset-re #"^(\d+(?:\.\d+)?)(px|%)?$")

(defn- offset-unit [suffix]
  (if (= "%" suffix) :percent :px))

(defn- read-offset
  "The offset that `s` states, or nil."
  [s]
  (when (string? s)
    (when-let [[_ n suffix] (re-matches offset-re (str/trim s))]
      (let [unit  (offset-unit suffix)
            value (js/parseFloat n)]
        (when (or (= :px unit) (<= value 100))
          {:unit unit :value value})))))

(defn parse-offset
  "The offset `{:unit :px|:percent :value n}` that `s` states, or the default 25%."
  [s]
  (or (read-offset s) (read-offset default-offset)))

(defn offset-px
  "The distance in px of the activation line from the top of a scroll root `height` px tall."
  [{:keys [unit value]} height]
  (if (= :percent unit)
    (* height (/ value 100))
    value))

;; ── Entries ───────────────────────────────────────────────────────────────
(defn clean-text
  "The text `s` trimmed, with each run of whitespace made one space."
  [s]
  (str/trim (str/replace (or s "") #"\s+" " ")))

(def ^:private word-re (js/RegExp. "[\\p{L}\\p{N}]+" "u"))

(defn slug
  "An id made from the text `s`: lower case, letters and digits joined by `-`."
  [s]
  (let [words (re-seq word-re (str/lower-case (or s "")))]
    (if (seq words) (str/join "-" words) fallback-slug)))

(defn- free-id
  "The first of `base`, `base-2`, `base-3` and so on that is not in `taken`."
  [taken base]
  (first (remove taken (cons base (map (partial str base "-") (iterate inc 2))))))

(defn- step-id [[taken out] {:keys [id text] :as heading}]
  (let [chosen (or id (free-id taken (slug text)))]
    [(conj taken chosen) (conj out (assoc heading :id chosen :new-id? (nil? id)))]))

(defn- with-ids
  "The `headings`, each with its own id or a free slug of its text."
  [taken headings]
  (second (reduce step-id [(set taken) []] headings)))

(defn heading-level
  "The level of the tag `tag-name-text`: 1 to 6 for `H1` to `H6`, else nil."
  [tag-name-text]
  (when-let [[_ n] (re-matches #"(?i)h([1-6])" (or tag-name-text ""))]
    (js/parseInt n 10)))

(defn- base-level [levels]
  (if (seq levels) (apply min levels) 1))

(defn- with-level [base {:keys [tag] :as heading}]
  (let [level (heading-level tag)]
    (-> heading
        (assoc :level (if level (inc (- level base)) 1))
        (dissoc :tag))))

(defn- keep-first [[seen kept] {:keys [id] :as entry}]
  (if (contains? seen id)
    [seen kept]
    [(conj seen id) (conj kept entry)]))

(defn- first-of-each-id
  "The `entries` without those whose id an earlier entry has."
  [entries]
  (second (reduce keep-first [#{} []] entries)))

(defn- read-text [at heading]
  (assoc heading :at at :text (clean-text (:text heading))))

(defn identify
  "The `headings` with text, each with its id, its place `:at` in `headings`, and `:new-id?`.
   A heading is `{:id :text :tag}`, and `taken` holds the ids already in the root."
  [headings taken]
  (with-ids taken (remove (comp str/blank? :text) (map-indexed read-text headings))))

(defn id-writes
  "The `[at id]` pairs of the `identified` headings that need their id written."
  [identified]
  (into [] (keep (fn write [{:keys [at id new-id?]}] (when new-id? [at id]))) identified))

(defn- entry [i heading]
  (-> (select-keys heading [:id :text :level])
      (assoc :index i)))

(defn entries
  "The entries `{:id :text :level :index}` of the `identified` headings. A heading whose id an
   earlier one has is left out."
  [identified]
  (let [base (base-level (keep (comp heading-level :tag) identified))]
    (into [] (map-indexed entry) (map (partial with-level base) (first-of-each-id identified)))))

;; ── Active entry ──────────────────────────────────────────────────────────
(def ^:private line-slack-px 1)

(defn active-index
  "The index of the active entry. `tops` holds the top of each entry from the top of the scroll
   root, `line` the activation line. Nil when there are no entries."
  [tops line at-bottom?]
  (let [n (count tops)]
    (cond
      (zero? n)  nil
      at-bottom? (dec n)
      :else      (or (last (keep-indexed (fn passed [i top]
                                           (when (<= top (+ line line-slack-px)) i))
                                         tops))
                     0))))

(defn next-pin
  "The pin after `event`: `[:click id]`, `[:scroll]` or `[:settle]`. A pin holds the clicked
   entry until its scroll settles and the reader scrolls again."
  [pin [kind id]]
  (case kind
    :click  {:id id :settled? false}
    :settle (some-> pin (assoc :settled? true))
    :scroll (when-not (:settled? pin) pin)))

(defn change-detail
  "The detail of `x-toc-change` for a move from the entry `previous-id` to `entry`."
  [{:keys [id text level index]} previous-id]
  {:id id :text text :level level :index index :previousId previous-id})

(defn navigate-detail
  "The detail of `x-toc-navigate` for `entry`."
  [{:keys [id text level index]}]
  {:id id :text text :level level :index index})

;; ── Selector ──────────────────────────────────────────────────────────────
(defn parse-selector
  "The selector `s`, or the default for a blank one."
  [s]
  (if (str/blank? s) default-selector s))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "The view model `{:for :selector :label :labelled? :offset :update-hash?}` from the raw
   attributes, without its entries."
  [{:keys [for-raw selector-raw label-raw offset-raw update-hash?]}]
  (let [label (clean-text label-raw)]
    {:for          (clean-text for-raw)
     :selector     (parse-selector selector-raw)
     :label        label
     :labelled?    (not= "" label)
     :offset       (parse-offset offset-raw)
     :update-hash? (boolean update-hash?)}))

;; ── Metadata ──────────────────────────────────────────────────────────────
(def property-api
  {:for        {:type 'string  :reflects-attribute attr-for :default ""}
   :selector   {:type 'string  :reflects-attribute attr-selector :default default-selector}
   :label      {:type 'string  :reflects-attribute attr-label :default ""}
   :offset     {:type 'string  :reflects-attribute attr-offset :default default-offset}
   :sticky     {:type 'boolean :reflects-attribute attr-sticky}
   :updateHash {:type 'boolean :reflects-attribute attr-update-hash}
   :activeId   {:type 'string  :readonly true}})

(def event-schema
  {event-change   {:cancelable false
                   :detail     {:id 'string :text 'string :level 'number :index 'number
                                :previousId 'string}}
   event-navigate {:cancelable true
                   :detail     {:id 'string :text 'string :level 'number :index 'number}}})

(def method-api {})
