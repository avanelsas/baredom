(ns baredom.components.x-tree.model
  "The pure model of x-tree."
  (:require [clojure.set :as set]
            [clojure.string :as str]))

;; ── Tag & attribute constants ──────────────────────────────────────────────
(def tag-name "x-tree")

(def attr-items        "items")
(def attr-expanded     "expanded")
(def attr-selected     "selected")
(def attr-selection    "selection")
(def attr-label        "label")
(def attr-loading-text "loading-text")
(def attr-guides       "guides")

(def observed-attributes
  #js [attr-items attr-expanded attr-selected attr-selection attr-label attr-loading-text
       attr-guides])

(def allowed-selections #{"none" "single" "leaf" "multiple"})

(def default-loading-text "Loading…")

;; ── Event constants ───────────────────────────────────────────────────────
(def event-toggle    "x-tree-toggle")
(def event-select    "x-tree-select")
(def event-change    "x-tree-change")
(def event-lazy-load "x-tree-lazy-load")

;; ── Accessors ─────────────────────────────────────────────────────────────
(defn- children-of [m id]
  (get-in m [:children id]))

(defn- parent-of [m id]
  (get-in m [:parent id]))

(defn- siblings
  "The ids under the parent of `id`, `id` included."
  [m id]
  (children-of m (parent-of m id)))

(defn- lineage
  "`id` and its ancestors, nearest first."
  [m id]
  (take-while some? (iterate (partial parent-of m) id)))

(defn- known? [m id]
  (contains? (:item m) id))

(defn- open? [m id]
  (contains? (:expanded m) id))

(defn- lazy? [m id]
  (true? (get-in m [:item id :lazy])))

(defn- disabled? [m id]
  (true? (get-in m [:item id :disabled])))

(defn- in-tree-order
  "The ids of `ids` that name an item of `m`, in tree order."
  [m ids]
  (filterv (set ids) (:order m)))

;; ── Index ─────────────────────────────────────────────────────────────────
(def ^:private empty-index
  {:item {} :children {nil []} :parent {} :position {} :order [] :dropped []})

(defn- text? [v]
  (and (string? v) (not= "" v)))

(defn- problem
  "Why `item` cannot join `acc`, or nil when it can."
  [acc {:keys [id label]}]
  (cond
    (not (text? id))    :missing-id
    (not (text? label)) :missing-label
    (known? acc id)     :duplicate))

(declare add-items)

(defn- add-item
  "`acc` with `item` and its children under `parent`, or with `item` in `:dropped`."
  [parent acc {:keys [id children] :as item}]
  (if-some [reason (problem acc item)]
    (update acc :dropped conj {:id id :reason reason})
    (-> acc
        (assoc-in [:item id] (dissoc item :children))
        (assoc-in [:position id] (count (children-of acc parent)))
        (update-in [:children parent] (fnil conj []) id)
        (cond-> (some? parent) (assoc-in [:parent id] parent))
        (update :order conj id)
        (add-items id children))))

(defn- add-items [acc parent items]
  (reduce (partial add-item parent) acc (when (sequential? items) items)))

(defn index
  "The items of `items` keyed by id: `:item`, `:children` (nil for the top level), `:parent`,
   `:position` among its siblings, and `:order`, in tree order. An item with no id or label, or
   with the id of an earlier item, is left out with its children and listed in `:dropped`."
  [items]
  (add-items empty-index nil items))

;; ── Kinds of item ─────────────────────────────────────────────────────────
(defn branch?
  "True when the item `id` of `m` has children or is lazy."
  [m id]
  (boolean (or (seq (children-of m id)) (lazy? m id))))

(defn end-item?
  "True when the item `id` of `m` has no children."
  [m id]
  (empty? (children-of m id)))

;; ── Counted ids ───────────────────────────────────────────────────────────
(defn- ids-of [ids]
  (when (sequential? ids) ids))

(defn- unknown-ids
  "The ids of `ids` that name no item of `m`, once each, in their order."
  [m ids]
  (into [] (comp (remove (partial known? m)) (distinct)) ids))

(defn- counted-selected [m selection ids]
  (let [known (in-tree-order m ids)]
    (case selection
      :single   (take 1 known)
      :leaf     (take 1 (remove (partial branch? m) known))
      :multiple (filter (partial end-item? m) known)
      nil)))

(defn counted
  "The ids of `expanded` and `selected` that `m` counts under `selection`, as sets, and in
   `:unknown` the ids of each that name no item."
  [m {:keys [selection expanded selected]}]
  (let [ex (ids-of expanded)
        se (ids-of selected)]
    {:expanded (set (filter (partial branch? m) (in-tree-order m ex)))
     :selected (set (counted-selected m selection se))
     :unknown  {:expanded (unknown-ids m ex)
                :selected (unknown-ids m se)}}))

;; ── Places and visible ids ────────────────────────────────────────────────
(defn- shown-children
  "The children of `id` when it is open in `m`, else none."
  [m id]
  (if (open? m id)
    (or (children-of m id) [])
    []))

(defn- add-place [m acc id]
  (let [ks (shown-children m id)]
    (reduce (partial add-place m) (assoc acc id ks) ks)))

(defn places
  "The wanted places of `m` for `places/sync!`: the top level under `:tree`, and each visible id
   with its children when it is open, else none."
  [m]
  (let [top (or (children-of m nil) [])]
    (reduce (partial add-place m) {:tree top} top)))

(defn- add-visible [pl acc id]
  (reduce (partial add-visible pl) (conj acc id) (get pl id)))

(defn places->visible
  "The visible ids of the places `pl`, in tree order."
  [pl]
  (reduce (partial add-visible pl) [] (:tree pl)))

(defn visible
  "The visible ids of `m`, in tree order."
  [m]
  (places->visible (places m)))

;; ── Focus ─────────────────────────────────────────────────────────────────
(defn- nearest-visible
  "`id` or its nearest ancestor in `shown`, or nil."
  [m shown id]
  (first (filter shown (lineage m id))))

(defn resolve-focus
  "The id that takes focus in `m`: `focused` or its nearest visible ancestor, else the first
   selected id or its nearest visible ancestor, else the first visible id. Nil with no items."
  [m focused]
  (let [vis  (visible m)
        lift (partial nearest-visible m (set vis))]
    (or (some-> focused lift)
        (some-> (first (in-tree-order m (:selected m))) lift)
        (first vis))))

;; ── Row ───────────────────────────────────────────────────────────────────
(defn- end-items-under
  "The end items below the branch `id` of `m`."
  [m id]
  (->> (tree-seq (complement (partial end-item? m)) (partial children-of m) id)
       (filter (partial end-item? m))))

(defn- share-state
  "`:checked` when every id of `ids` is in `selected`, `:mixed` when some are, else `:unchecked`."
  [selected ids]
  (let [n (count (filter selected ids))]
    (cond
      (= n (count ids)) :checked
      (zero? n)         :unchecked
      :else             :mixed)))

(defn- check-state
  "The check state of `id` in `m`. A branch counts its enabled end items, or all of them when
   none is enabled."
  [m id]
  (if (end-item? m id)
    (if (contains? (:selected m) id) :checked :unchecked)
    (let [ends    (end-items-under m id)
          enabled (remove (partial disabled? m) ends)]
      (share-state (:selected m) (if (seq enabled) enabled ends)))))

(defn- selectable? [m id]
  (case (:selection m)
    :single true
    :leaf   (not (branch? m id))
    false))

(defn row
  "The facts the tree writes for the item `id` of `m`."
  [m id]
  (let [branch (branch? m id)
        open   (open? m id)
        peers  (siblings m id)]
    (cond-> {:id            id
             :level         (count (lineage m id))
             :setsize       (count peers)
             :posinset      (inc (get-in m [:position id]))
             :branch?       branch
             :disabled?     (disabled? m id)
             :focused?      (= id (:focused m))
             :loading?      (and open (end-item? m id))
             :guide-active? (= id (parent-of m (:focused m)))}
      branch                       (assoc :expanded (if open :open :closed))
      (selectable? m id)           (assoc :select (if (contains? (:selected m) id)
                                                    :selected
                                                    :unselected))
      (= :multiple (:selection m)) (assoc :check (check-state m id)))))

(def ^:private aria-expanded {:open "true" :closed "false"})
(def ^:private aria-selected {:selected "true" :unselected "false"})
(def ^:private aria-checked  {:checked "true" :mixed "mixed" :unchecked "false"})

(defn- flag [on?]
  (when on? ""))

(defn- aria-flag [on?]
  (when on? "true"))

(defn row->attrs
  "The attributes of each part of the item of `row`. Nil removes an attribute."
  [{:keys [level setsize posinset branch? disabled? focused? loading? guide-active?
           expanded select check]}]
  {:item     {"aria-level"    (str level)
              "aria-setsize"  (str setsize)
              "aria-posinset" (str posinset)
              "aria-expanded" (aria-expanded expanded)
              "aria-selected" (aria-selected select)
              "aria-checked"  (aria-checked check)
              "aria-disabled" (aria-flag disabled?)
              "aria-busy"     (aria-flag loading?)
              "tabindex"      (if focused? "0" "-1")
              "data-level"    (str level)
              "data-branch"   (flag branch?)
              "data-expanded" (flag (= :open expanded))
              "data-selected" (flag (or (= :selected select) (= :checked check)))
              "data-disabled" (flag disabled?)}
   :checkbox {"data-state" (some-> check name)}
   :group    {"hidden"      (flag (not= :open expanded))
              "data-active" (flag guide-active?)}})

;; ── Step ──────────────────────────────────────────────────────────────────
(defn- unchanged [m _id]
  m)

(defn- toggle [m id]
  (if (branch? m id)
    (update m :expanded (if (open? m id) disj conj) id)
    m))

(defn- select-only [m id]
  (if (disabled? m id)
    m
    (assoc m :selected #{id})))

(defn- check-targets
  "The enabled end items that checking `id` adds or removes."
  [m id]
  (remove (partial disabled? m)
          (if (end-item? m id) [id] (end-items-under m id))))

(defn- check [m id]
  (if (disabled? m id)
    m
    (let [targets (check-targets m id)]
      (if (= :checked (check-state m id))
        (update m :selected (partial reduce disj) targets)
        (update m :selected into targets)))))

(defn- expand-siblings [m id]
  (update m :expanded into (filter (partial branch? m) (siblings m id))))

(def ^:private ops
  {:focus           unchanged
   :toggle          toggle
   :select          select-only
   :check           check
   :expand-siblings expand-siblings})

(defn step
  "The model after `action`. Every action focuses its id."
  [m {:keys [op id]}]
  (let [apply-op (get ops op unchanged)]
    (assoc (apply-op m id) :focused id)))

;; ── Gestures ──────────────────────────────────────────────────────────────
(def activation
  "The op that activating a branch or a leaf gives, by selection mode."
  {:none     {:branch :toggle :leaf nil}
   :single   {:branch :select :leaf :select}
   :leaf     {:branch :toggle :leaf :select}
   :multiple {:branch :check  :leaf :check}})

(def keymap
  "The step of each key, by the kind of the focused item: `:open`, `:closed`, `:leaf` or `:any`."
  {"ArrowDown"  {:any :next}
   "ArrowUp"    {:any :prev}
   "ArrowRight" {:closed :toggle :open :first-child}
   "ArrowLeft"  {:open :toggle :closed :parent :leaf :parent}
   "Home"       {:any :first}
   "End"        {:any :last}
   "Enter"      {:any :activate}
   " "          {:any :activate}
   "*"          {:any :expand-siblings}})

(defn- position
  "The index of `id` in `ids`, or nil."
  [ids id]
  (let [n (count (take-while (partial not= id) ids))]
    (when (< n (count ids)) n)))

(defn- neighbour [vis id offset]
  (some->> (position vis id) (+ offset) (get vis)))

(defn- next-visible [m id]
  (neighbour (visible m) id 1))

(defn- prev-visible [m id]
  (neighbour (visible m) id -1))

(defn- first-visible [m _id]
  (first (visible m)))

(defn- last-visible [m _id]
  (peek (visible m)))

(defn- first-child [m id]
  (when (open? m id)
    (first (children-of m id))))

(def ^:private moves
  {:next        next-visible
   :prev        prev-visible
   :first       first-visible
   :last        last-visible
   :parent      parent-of
   :first-child first-child})

(defn target
  "The id that `move` reaches from `id` in `m`, or nil."
  [m id move]
  (when-some [reach (get moves move)]
    (reach m id)))

(defn- kind [m id]
  (cond
    (not (branch? m id)) :leaf
    (open? m id)         :open
    :else                :closed))

(defn- activation-op [m id]
  (get-in activation [(:selection m) (if (branch? m id) :branch :leaf)]))

(defn- focus-action [id]
  {:op :focus :id id})

(defn- step-action
  "The action of the keymap step `st` on the focused `id`, or nil."
  [m id st]
  (case st
    ;; These keymap steps are op names.
    (:toggle :expand-siblings) {:op st :id id}
    :activate                  (when-some [op (activation-op m id)] {:op op :id id})
    (some-> (target m id st) focus-action)))

(defn- label-starts-with? [m ch id]
  (str/starts-with? (str/lower-case (get-in m [:item id :label])) ch))

(defn- typeahead
  "The next visible id after `id` whose label starts with `ch`, wrapping, or nil."
  [m id ch]
  (let [vis   (visible m)
        after (inc (or (position vis id) -1))]
    (->> (concat (subvec vis after) (subvec vis 0 after))
         (filter (partial label-starts-with? m (str/lower-case ch)))
         first)))

(defn- key-step [m id key]
  (let [steps (get keymap key)]
    (or (get steps (kind m id)) (get steps :any))))

(defn- typed? [key]
  (and (string? key) (= 1 (count key))))

(defn key->action
  "The action of the key `key` on the focused item of `m`, or nil when it does nothing."
  [m key]
  (when-some [id (:focused m)]
    (some-> (cond
              (contains? keymap key) (step-action m id (key-step m id key))
              (typed? key)           (some-> (typeahead m id key) focus-action))
            (assoc :source :keyboard))))

(defn click->action
  "The action of a click on the `part` (`:row` or `:chevron`) of the item `id`, or nil."
  [m id part]
  (when (known? m id)
    (some-> (case part
              :chevron (when (branch? m id) {:op :toggle :id id})
              :row     {:op (or (activation-op m id) :focus) :id id}
              nil)
            (assoc :source :pointer))))

;; ── Events ────────────────────────────────────────────────────────────────
(defn- next-text
  "The JSON text of the set `k` of `m'` in tree order, with its unknown ids after it when
   `keep-unknown?`."
  [m' k keep-unknown?]
  (-> (in-tree-order m' (k m'))
      (cond-> keep-unknown? (into (get-in m' [:unknown k])))
      clj->js
      js/JSON.stringify))

(defn- changed-ids [m m']
  (let [before (:selected m)
        after  (:selected m')]
    (in-tree-order m' (set/union (set/difference before after)
                                 (set/difference after before)))))

(defn- request
  "The request event of `action` and the change event after it, or nil when neither
   `expanded` nor `selected` changed. An action changes at most one of them."
  [m {:keys [op id source]} m']
  (let [src (some-> source name)]
    (cond
      (not= (:expanded m) (:expanded m'))
      [[event-toggle {:id id :nextExpanded (next-text m' :expanded true) :source src}]
       [event-change {:attribute attr-expanded}]]

      (not= (:selected m) (:selected m'))
      [[event-select {:id           id
                      :changed      (changed-ids m m')
                      :nextSelected (next-text m' :selected (not= :select op))
                      :source       src}]
       [event-change {:attribute attr-selected}]])))

(defn- lazy-unloaded? [m id]
  (and (lazy? m id) (end-item? m id)))

(defn- lazy-load-event [id]
  [event-lazy-load {:id id}])

(defn- lazy-loads
  "A lazy-load event for each lazy branch with no children that `m'` opens."
  [m m']
  (->> (set/difference (:expanded m') (:expanded m))
       (in-tree-order m')
       (filter (partial lazy-unloaded? m'))
       (mapv lazy-load-event)))

(defn events
  "The events that `action` sends, from `m` to `m'`, in order, as `[name detail]` pairs."
  [m action m']
  (if-some [sent (request m action m')]
    (into sent (lazy-loads m m'))
    []))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn parse-selection
  "The selection mode that `s` names, or `:single`."
  [s]
  (if (contains? allowed-selections s) (keyword s) :single))

(defn array-of
  "The JS array `v`, or the one that the JSON text `v` holds. Nil for anything else."
  [v]
  (let [a (if (string? v)
            (try (js/JSON.parse v) (catch :default _ nil))
            v)]
    (when (array? a) a)))

(defn parse-list
  "The array that the JSON text `s` holds, as a vector with keyword keys, or nil."
  [s]
  (when (string? s)
    (some-> (array-of s) (js->clj :keywordize-keys true))))

(defn- index-of
  "The index of the items text `items-raw`: the one of `prev` while it holds the same text."
  [items-raw prev]
  (if (and (some? prev) (= items-raw (:items-raw prev)))
    (select-keys prev (keys empty-index))
    (index (parse-list items-raw))))

(defn normalize
  "The model of the attribute texts in the `-raw` keys of `raw`, and the focused id in
   `:focused`. With `prev`, the model before, it keeps the index of `prev` while `items` is
   the same text."
  ([raw]
   (normalize raw nil))
  ([{:keys [items-raw expanded-raw selected-raw selection-raw label loading-text guides
            focused]}
    prev]
   (let [mode (parse-selection selection-raw)
         ix   (index-of items-raw prev)
         m    (merge ix
                     (counted ix {:selection mode
                                  :expanded  (parse-list expanded-raw)
                                  :selected  (parse-list selected-raw)})
                     {:items-raw    items-raw
                      :selection    mode
                      :label        label
                      :loading-text (or loading-text default-loading-text)
                      :guides       (boolean guides)})]
     (assoc m :focused (resolve-focus m focused)))))

;; ── API metadata ──────────────────────────────────────────────────────────
(def property-api
  {:items       {:type 'array   :reflects-attribute attr-items}
   :expanded    {:type 'array   :reflects-attribute attr-expanded}
   :selected    {:type 'array   :reflects-attribute attr-selected}
   :selection   {:type 'string  :reflects-attribute attr-selection :enum allowed-selections}
   :label       {:type 'string  :reflects-attribute attr-label}
   :loadingText {:type 'string  :reflects-attribute attr-loading-text}
   :guides      {:type 'boolean :reflects-attribute attr-guides}})

(def event-schema
  {event-toggle    {:cancelable true
                    :requests   {attr-expanded :nextExpanded}
                    :detail     {:id 'string :nextExpanded 'string :source 'string}}
   event-select    {:cancelable true
                    :requests   {attr-selected :nextSelected}
                    :detail     {:id 'string :changed 'array :nextSelected 'string
                                 :source 'string}}
   event-change    {:cancelable false
                    :detail     {:attribute 'string}}
   event-lazy-load {:cancelable false
                    :detail     {:id 'string}}})

(def method-api {})
