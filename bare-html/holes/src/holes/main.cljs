(ns holes.main
  "A table written from a template, and a hole that a component also writes."
  (:require [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-switch.x-switch :as x-switch]
            [baredom.components.x-table-cell.x-table-cell :as x-table-cell]
            [baredom.components.x-table-row.x-table-row :as x-table-row]
            [baredom.components.x-table.x-table :as x-table]
            [baredom.foreign-writes :as foreign-writes]
            [baredom.requests :as requests]
            [baremirror.alpha.events :as events]
            [baremirror.alpha.parts :as parts]
            [baremirror.alpha.places :as places]
            [baremirror.alpha.template :as template]
            [clojure.string :as string]))

;; 1. The state, and what a message does to it.

(def initial
  {:people       [{:id "1" :name "Ada" :role "Engineer"}
                  {:id "2" :name "Grace" :role "Admiral"}
                  {:id "3" :name "Edsger" :role "Professor"}]
   :next-id      4
   :selected     "1"
   :selection-in :rows})

(defn- added [{:keys [next-id] :as state}]
  (let [id (str next-id)]
    (-> state
        (update :people conj {:id id :name (str "Person " id) :role "New"})
        (assoc :next-id (inc next-id)))))

(def ^:private other-place {:rows :table :table :rows})

(defn step [state [meaning arg]]
  (case meaning
    :add    (added state)
    :select (assoc state :selected arg)
    :move   (update state :selection-in other-place)
    state))

;; 2. The templates of a row, by where the selection is written.

(def templates
  (let [rows [:x-table-row {:interactive true :value :id :selected :selected?}
              [:x-table-cell :name]
              [:x-table-cell :role]]]
    {:rows  rows
     :table (update rows 1 dissoc :selected)}))

(def splits
  (update-vals templates (partial template/split :row)))

(def on-this-page
  "The writes of the components on this page on nodes they do not own."
  (select-keys foreign-writes/foreign-writes
               ["x-button" "x-switch" "x-table" "x-table-cell" "x-table-row"]))

;; 3. The explanation, as data: a list of pieces of text, each with a kind or none.

(defn- attr-kinds
  "The kind of each attribute hole of the row template for `selection-in`: :shared when a
   component on this page also writes it, else :hole."
  [selection-in]
  (let [shared (into #{} (map :attr) (template/shared-holes (splits selection-in) on-this-page))]
    (into {} (for [[k v] (second (templates selection-in)) :when (keyword? v)]
               [(name k) (if (shared (name k)) :shared :hole)]))))

(defn- piece
  "The piece of the template value `v`, marked with `kind` when it is a hole."
  [kind v]
  [(when (keyword? v) kind) (pr-str v)])

(defn- attr-pieces [kinds [k v]]
  [[nil (str (pr-str k) " ")] (piece (kinds (name k)) v)])

(defn- attrs-pieces [kinds attrs]
  (concat [[nil " {"]]
          (apply concat (interpose [[nil " "]] (map (partial attr-pieces kinds) attrs)))
          [[nil "}"]]))

(declare element-pieces)

(defn- child-pieces [kinds indent child]
  (if (vector? child)
    (cons [nil (str "\n" indent)] (element-pieces kinds (str indent " ") child))
    [[nil " "] (piece :hole child)]))

(defn- element-pieces
  "The pieces of the template `element`, with each attribute hole marked with its kind in `kinds`."
  [kinds indent [tag & more]]
  (let [attrs (when (map? (first more)) (first more))]
    (concat [[nil (str "[" (pr-str tag))]]
            (when attrs (attrs-pieces kinds attrs))
            (mapcat (partial child-pieces kinds indent) (if attrs (rest more) more))
            [[nil "]"]])))

(defn- tag-attr-pieces [kinds [attr value]]
  [[nil " "] [(kinds attr) (str attr "=\"" value "\"")]])

(defn- tag-pieces
  "The pieces of the opening tag of a row with the attribute pairs `attrs`, each marked with its
   kind in `kinds`."
  [kinds attrs]
  (concat [[nil "<x-table-row"]] (mapcat (partial tag-attr-pieces kinds) attrs) [[nil ">"]]))

(defn- sentence [{:keys [writer attr tag]}]
  (str writer " also writes " attr " on " tag "."))

(defn- answer [split]
  (if-some [entries (seq (template/shared-holes split on-this-page))]
    (string/join " " (map sentence entries))
    "No hole has a second writer."))

;; 4. What the screen shows, as a value.

(defn- item [{:keys [selected selection-in]} {:keys [id] :as person}]
  (cond-> person
    (= :rows selection-in) (assoc :selected? (= id selected))))

(defn view [{:keys [people selected selection-in] :as state}]
  (let [items (into {} (map (juxt :id (partial item state))) people)
        kinds (attr-kinds selection-in)]
    {:fixed    {"table" {:attrs (if (= :table selection-in) {:value selected} {})}
                "item"  {:text (pr-str (items selected))}
                "check" {:text (answer (splits selection-in))}
                "move"  {:attrs {:checked (= :table selection-in)}}}
     :pieces   (element-pieces kinds " " (templates selection-in))
     :kinds    kinds
     :selected selected
     :order    (mapv :id people)
     :items    items
     :row      (splits selection-in)}))

;; 5. The effect.

(def copy-row!
  (parts/node-maker (:fixed (splits :rows))))

(defn- made! [split items k]
  (doto (copy-row!) (parts/write-item! split (items k))))

(defn- written! [split nodes items k]
  (parts/write-item! (nodes k) split (items k)))

(defn- piece-node [[kind text]]
  (if kind
    (doto (.createElement js/document "mark") (.setAttribute "class" (name kind)) (.append text))
    text))

(defn- show-pieces! [^js node pieces]
  (.replaceChildren node)
  (run! (fn append! [piece] (.append node (piece-node piece))) pieces))

(defn- render-rows! [page-parts {:keys [order items row]}]
  (let [nodes (places/sync! {:rows {:parent (page-parts "table")}} {:rows order}
                            (partial made! row items))]
    (run! (partial written! row nodes items) order)))

(defn- render-fixed! [page-parts {:keys [fixed pieces]}]
  (parts/write-parts! page-parts fixed)
  (show-pieces! (page-parts "template") pieces))

;; 6. The render, watched: who writes `selected`, and the selected row as it is in the page.

(defn- row-name [^js row]
  (.-textContent (.querySelector row "x-table-cell")))

(defn- write-line [writer ^js record]
  (str writer (if (nil? (.-oldValue record)) " added selected to " " removed selected from ")
       (row-name (.-target record)) "."))

(defn- log! [^js log writer records]
  (doseq [record records]
    (.append log (doto (.createElement js/document "li") (.append (write-line writer record)))))
  (while (< 6 (.-childElementCount log))
    (.remove (.-firstElementChild log))))

(def ^:private shown-attrs ["interactive" "value" "selected"])

(defn- attr-of [^js row attr]
  (when (.hasAttribute row attr)
    [attr (.getAttribute row attr)]))

(defn- row-attrs [^js row]
  (into [] (keep (partial attr-of row)) shown-attrs))

(defn- show-row! [^js page {:keys [selected kinds]}]
  (when-some [row (.querySelector page (str "x-table-row[data-x-key=\"" selected "\"]"))]
    (show-pieces! (.getElementById js/document "row") (tag-pieces kinds (row-attrs row)))))

(defn- watched-render!
  "A function of a view that renders it, and logs the writes of `selected`: those of the rows
   are the template's, and all others are those of x-table."
  [^js page state]
  (let [log      (.getElementById js/document "log")
        observer (js/MutationObserver.
                  (fn on-writes [records]
                    (log! log "x-table" records)
                    (show-row! page (view @state))))]
    (.observe observer (.querySelector page "x-table")
              #js {:attributes true :attributeOldValue true :subtree true
                   :attributeFilter #js ["selected"]})
    (fn render! [vm]
      (let [page-parts (parts/read-parts page)]
        (render-rows! page-parts vm)
        (log! log "Your template" (.takeRecords observer))
        (render-fixed! page-parts vm)
        (log! log "x-table" (.takeRecords observer))
        (show-row! page vm)))))

;; The wiring.

(defn- detail-value [_origin ^js e]
  (.. e -detail -value))

(def page-events
  {["x-table-row-select" "table"]     [:select detail-value]
   ["press" "add"]                    :add
   ["x-switch-change-request" "move"] :move})

(defn start! []
  (doseq [init! [x-button/init! x-switch/init! x-table/init! x-table-row/init! x-table-cell/init!]]
    (init!))
  (let [page    (.getElementById js/document "app")
        state   (atom initial)
        render! (watched-render! page state)]
    (events/listen! page {:dispatch!        (events/dispatcher state step view render!)
                          :requested-values requests/requested-values
                          :events           page-events})
    (render! (view @state))))
