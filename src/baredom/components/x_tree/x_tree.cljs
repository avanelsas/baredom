(ns baredom.components.x-tree.x-tree
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baremirror.alpha.places :as places]
            [baredom.components.x-tree.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs    "__xTreeRefs")
(def ^:private k-model   "__xTreeModel")
(def ^:private k-focused "__xTreeFocused")
(def ^:private k-rendered "__xTreeRendered")

;; ── String constants ──────────────────────────────────────────────────────
(def ^:private attr-part          "part")
(def ^:private attr-role          "role")
(def ^:private attr-aria-label    "aria-label")
(def ^:private attr-aria-multi    "aria-multiselectable")
(def ^:private attr-aria-hidden   "aria-hidden")
(def ^:private attr-empty         "data-empty")
(def ^:private attr-mirror-part   "data-x-part")

(def ^:private part-tree     "tree")
(def ^:private part-item     "item")
(def ^:private part-row      "row")
(def ^:private part-chevron  "chevron")
(def ^:private part-checkbox "checkbox")
(def ^:private part-label    "label")
(def ^:private part-meta     "meta")
(def ^:private part-group    "group")
(def ^:private part-loading  "loading")

(defn- part-selector [part]
  (str "[part~='" part "']"))

(def ^:private sel-row     (part-selector part-row))
(def ^:private sel-chevron (part-selector part-chevron))
(def ^:private sel-loading (str ":scope > " (part-selector part-loading)))

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "--x-tree-font-family:var(--x-font-family,inherit);"
   "--x-tree-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-tree-color:var(--x-color-text,#1f2937);"
   "--x-tree-meta-color:var(--x-color-text-muted,#6b7280);"
   "--x-tree-row-height:2rem;"
   "--x-tree-row-padding:0 var(--x-space-sm,0.5rem);"
   "--x-tree-row-radius:var(--x-radius-sm,4px);"
   "--x-tree-indent:1.25rem;"
   "--x-tree-gap:0.375rem;"
   "--x-tree-hover-bg:color-mix(in srgb,var(--x-color-text,#000) 6%,transparent);"
   "--x-tree-selected-bg:color-mix(in srgb,var(--x-color-primary,#2563eb) 12%,transparent);"
   "--x-tree-selected-color:var(--x-tree-color);"
   "--x-tree-focus-ring:var(--x-color-focus-ring,#2563eb);"
   "--x-tree-disabled-opacity:0.5;"
   "--x-tree-chevron-color:var(--x-color-text-muted,#6b7280);"
   "--x-tree-chevron-size:1rem;"
   "--x-tree-checkbox-color:var(--x-color-primary,#2563eb);"
   "--x-tree-checkbox-size:1rem;"
   "--x-tree-guide-width:var(--x-border-width,1px);"
   "--x-tree-guide-color:var(--x-color-border,#e5e7eb);"
   "--x-tree-guide-style:solid;"
   "--x-tree-guide-active-color:var(--x-tree-guide-color);"
   "--x-tree-transition-duration:var(--x-transition-duration,150ms);"
   "font-family:var(--x-tree-font-family);"
   "font-size:var(--x-tree-font-size);"
   "color:var(--x-tree-color);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-tree-color:var(--x-color-text,#e5e7eb);"
   "--x-tree-meta-color:var(--x-color-text-muted,#9ca3af);"
   "--x-tree-hover-bg:color-mix(in srgb,var(--x-color-text,#fff) 6%,transparent);"
   "--x-tree-selected-bg:color-mix(in srgb,var(--x-color-primary,#60a5fa) 20%,transparent);"
   "--x-tree-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-tree-chevron-color:var(--x-color-text-muted,#9ca3af);"
   "--x-tree-checkbox-color:var(--x-color-primary,#60a5fa);"
   "--x-tree-guide-color:var(--x-color-border,#374151);}}"
   du/hidden-rule

   "ul{list-style:none;margin:0;padding:0;}"

   ;; The guide sits under the chevron of the branch: the row padding plus half a chevron.
   "[part~='group']{"
   "--_guide-offset:calc(var(--x-space-sm,0.5rem) + var(--x-tree-chevron-size) / 2);"
   "margin-inline-start:var(--_guide-offset);"
   "padding-inline-start:calc(var(--x-tree-indent) - var(--_guide-offset));"
   "border-inline-start-width:var(--x-tree-guide-width);"
   "border-inline-start-style:solid;"
   "border-inline-start-color:transparent;}"

   ":host([guides]) [part~='group']{"
   "border-inline-start-style:var(--x-tree-guide-style);"
   "border-inline-start-color:var(--x-tree-guide-color);}"

   ":host([guides]) [part~='group'][data-active]{"
   "border-inline-start-color:var(--x-tree-guide-active-color);}"

   "[part~='item']{outline:none;}"

   "[part~='row']{"
   "display:flex;"
   "align-items:center;"
   "gap:var(--x-tree-gap);"
   "min-height:var(--x-tree-row-height);"
   "padding:var(--x-tree-row-padding);"
   "border-radius:var(--x-tree-row-radius);"
   "box-sizing:border-box;"
   "cursor:pointer;"
   "user-select:none;"
   "transition:background-color var(--x-tree-transition-duration) ease,"
   "color var(--x-tree-transition-duration) ease;}"

   "[part~='row']:hover{background:var(--x-tree-hover-bg);}"

   "[part~='item'][data-selected] > [part~='row']{"
   "background:var(--x-tree-selected-bg);"
   "color:var(--x-tree-selected-color);}"

   "[part~='item']:focus-visible > [part~='row']{"
   "outline:2px solid var(--x-tree-focus-ring);"
   "outline-offset:-2px;}"

   "[part~='item'][data-disabled] > [part~='row']{"
   "opacity:var(--x-tree-disabled-opacity);"
   "cursor:default;}"

   "[part~='chevron']{"
   "flex:none;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-tree-chevron-size);"
   "height:var(--x-tree-chevron-size);"
   "color:var(--x-tree-chevron-color);"
   "transition:transform var(--x-tree-transition-duration) ease;}"

   ;; Physical borders: the chevron and the check keep their shape in both directions.
   "[part~='chevron']::before{"
   "content:'';"
   "width:0.35em;"
   "height:0.35em;"
   "border-right:1.5px solid currentColor;"
   "border-bottom:1.5px solid currentColor;"
   "transform:rotate(-45deg);}"

   ":host(:dir(rtl)) [part~='chevron']::before{transform:rotate(135deg);}"

   "[part~='item']:not([data-branch]) > [part~='row'] > [part~='chevron']{visibility:hidden;}"

   "[part~='item'][data-expanded] > [part~='row'] > [part~='chevron']{transform:rotate(90deg);}"

   ":host(:dir(rtl)) [part~='item'][data-expanded] > [part~='row'] > [part~='chevron']{"
   "transform:rotate(-90deg);}"

   "[part~='checkbox']{"
   "flex:none;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-tree-checkbox-size);"
   "height:var(--x-tree-checkbox-size);"
   "border:1.5px solid var(--x-tree-chevron-color);"
   "border-radius:3px;"
   "box-sizing:border-box;"
   "color:var(--x-color-on-primary,#fff);}"

   "[part~='checkbox']:not([data-state]){display:none;}"

   "[part~='checkbox'][data-state='checked'],[part~='checkbox'][data-state='mixed']{"
   "background:var(--x-tree-checkbox-color);"
   "border-color:var(--x-tree-checkbox-color);}"

   "[part~='checkbox'][data-state='checked']::after{"
   "content:'';"
   "width:0.3em;"
   "height:0.55em;"
   "margin-block-start:-0.1em;"
   "border-right:2px solid currentColor;"
   "border-bottom:2px solid currentColor;"
   "transform:rotate(45deg);}"

   "[part~='checkbox'][data-state='mixed']::after{"
   "content:'';"
   "width:0.5em;"
   "border-block-start:2px solid currentColor;}"

   ;; The label and meta are the consumer's text: each takes the direction of its own script,
   ;; and aligns by the direction of the tree.
   "[part~='label']{"
   "flex:1 1 auto;"
   "min-width:0;"
   "overflow-wrap:anywhere;"
   "unicode-bidi:plaintext;"
   "text-align:left;}"

   ":host(:dir(rtl)) [part~='label']{text-align:right;}"

   "[part~='meta']{"
   "flex:none;"
   "margin-inline-start:auto;"
   "color:var(--x-tree-meta-color);"
   "white-space:nowrap;"
   "unicode-bidi:plaintext;}"

   "[part~='meta']:empty{display:none;}"

   "[part~='loading']{"
   "display:flex;"
   "align-items:center;"
   "min-height:var(--x-tree-row-height);"
   "padding:var(--x-tree-row-padding);"
   "color:var(--x-tree-meta-color);"
   "font-style:italic;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part~='row'],[part~='chevron']{transition-duration:0ms;}}"

   "@media (pointer:coarse){"
   "[part~='row'],[part~='loading']{min-height:2.75rem;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- element! [tag part]
  (let [node (.createElement js/document tag)]
    (du/set-attr! node attr-part part)
    node))

(defn- make-row! []
  (let [row      (element! "div" part-row)
        chevron  (element! "span" part-chevron)
        checkbox (element! "span" part-checkbox)]
    (du/set-attr! chevron attr-aria-hidden "true")
    (du/set-attr! checkbox attr-aria-hidden "true")
    (doto row
      (.appendChild chevron)
      (.appendChild checkbox)
      (.appendChild (element! "span" part-label))
      (.appendChild (element! "span" part-meta)))))

(defn- make-item!
  "A new tree item: its row, then its group."
  [_key]
  (let [li    (element! "li" part-item)
        group (element! "ul" part-group)]
    (du/set-attr! li attr-role "treeitem")
    (du/set-attr! group attr-role "group")
    (du/set-attr! group attr-mirror-part part-group)
    (doto li
      (.appendChild (make-row!))
      (.appendChild group))))

(defn- item-parts
  "The parts of the item `li` that `make-item!` built, by name."
  [^js li]
  (let [^js row                        (.-firstElementChild li)
        [_chevron checkbox label meta] (array-seq (.-children row))]
    {:checkbox checkbox
     :label    label
     :meta     meta
     :group    (.-lastElementChild li)}))

(declare install-listeners!)

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        tree  (element! "ul" part-tree)]
    (set! (.-textContent style) style-text)
    (du/set-attr! tree attr-role "tree")
    (.appendChild root style)
    (.appendChild root tree)
    (install-listeners! el tree)
    (du/setv! el k-refs {:tree tree})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Reading ──────────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:items-raw     (du/get-attr el model/attr-items)
    :expanded-raw  (du/get-attr el model/attr-expanded)
    :selected-raw  (du/get-attr el model/attr-selected)
    :selection-raw (du/get-attr el model/attr-selection)
    :label         (du/get-attr el model/attr-label)
    :loading-text  (du/get-attr el model/attr-loading-text)
    :guides        (du/has-attr? el model/attr-guides)
    :focused       (du/getv el k-focused)}
   (du/getv el k-model)))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- set-attrs-to! [^js node attrs]
  (doseq [[attr value] attrs]
    (du/set-attr-to! node attr value)))

(defn- make-loading! [text]
  (let [li (element! "li" part-loading)]
    (du/set-text-to! li text)
    li))

(defn- apply-loading!
  "Shows the loading row in `group` while `loading?`, with `text`."
  [^js group loading? text]
  (let [loading (.querySelector group sel-loading)]
    (cond
      (and loading? loading) (du/set-text-to! loading text)
      loading?               (.appendChild group (make-loading! text))
      loading                (.remove loading))))

(defn- item-view
  "What the node of `id` shows in `m`: its row, its item and the loading text."
  [m id]
  {:row          (model/row m id)
   :item         (get-in m [:item id])
   :loading-text (:loading-text m)})

(defn- apply-item!
  "Brings the node of `id` in `nodes` to its view in `views`."
  [nodes views id]
  (let [^js li                              (nodes id)
        {:keys [checkbox label meta group]} (item-parts li)
        {:keys [row item loading-text]}     (views id)
        attrs                               (model/row->attrs row)]
    (set-attrs-to! li (:item attrs))
    (set-attrs-to! checkbox (:checkbox attrs))
    (set-attrs-to! group (:group attrs))
    (du/set-text-to! label (:label item))
    (du/set-text-to! meta (str (:meta item "")))
    (apply-loading! group (:loading? row) loading-text)))

(defn- stale?
  "True when the node of `id` is new, or its view differs from the one `rendered` holds."
  [rendered nodes views id]
  (not (and (identical? (nodes id) (get-in rendered [:nodes id]))
            (= (views id) (get-in rendered [:views id])))))

(defn- synced-nodes
  "The node of each key of the places `pl`. Syncs the tree only when `pl` differs from the
   places that `rendered` holds."
  [^js tree rendered pl]
  (if (= pl (:places rendered))
    (:nodes rendered)
    (places/sync! {:tree   {:parent tree}
                   :groups {:part part-group}}
                  pl
                  make-item!)))

(defn- render-items!
  "Brings each visible node to its view. A node whose view did not change is not touched."
  [^js el ^js tree m]
  (let [rendered (du/getv el k-rendered)
        pl       (model/places m)
        nodes    (synced-nodes tree rendered pl)
        visible  (model/places->visible pl)
        views    (into {} (map (juxt identity (partial item-view m))) visible)]
    (run! (partial apply-item! nodes views)
          (filter (partial stale? rendered nodes views) visible))
    ;; Render cache: the places, nodes and views of this render. It has no display value.
    (du/setv-untraced! el k-rendered {:places pl :nodes nodes :views views})))

(defn- apply-tree! [^js el ^js tree m]
  (du/set-attr-to! tree attr-aria-label (:label m))
  (du/set-attr-to! tree attr-aria-multi (when (= :multiple (:selection m)) "true"))
  (du/set-bool-attr-to! el attr-empty (empty? (:order m))))

(defn- warn-dropped!
  "Warns once for each item the tree drops, when the dropped items change."
  [old-m m]
  (when (not= (:dropped old-m) (:dropped m))
    (doseq [{:keys [id reason]} (:dropped m)]
      (js/console.warn (str "[x-tree] Dropped item " (pr-str id) ": " (name reason))))))

(defn- apply-model! [^js el {:keys [tree]} old-m m]
  (apply-tree! el tree m)
  (render-items! el tree m)
  (warn-dropped! old-m m)
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [refs  (ensure-refs! el)
        new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el refs old-m new-m))))

;; ── Gestures ──────────────────────────────────────────────────────────────
(defn- write-request!
  "Writes each attribute that `requests` names, from `detail`."
  [^js el requests detail]
  (doseq [[attr k] requests]
    (du/set-attr! el attr (get detail k))))

(defn- send!
  "Sends one event. Writes what a request asks when nobody cancels it. False when canceled."
  [^js el [event detail]]
  (let [{:keys [cancelable requests]} (get model/event-schema event)
        js-detail                     (clj->js detail)]
    (if cancelable
      (when (du/dispatch-cancelable! el event js-detail)
        (write-request! el requests detail)
        true)
      (do (du/dispatch! el event js-detail)
          true))))

(defn- send-all!
  "Sends `events` in order, and stops at the first one canceled."
  [^js el events]
  (loop [[event & more] events]
    (when (and event (send! el event))
      (recur more))))

(defn- focus-item!
  "Moves DOM focus to the item of the focused id."
  [^js el]
  (let [id (:focused (du/getv el k-model))]
    (some-> ^js (get-in (du/getv el k-rendered) [:nodes id])
            (.focus))))

(defn- run-action!
  "Applies `action`: stores the focus, sends the events, renders and focuses the item."
  [^js el action]
  (let [m  (du/getv el k-model)
        m' (model/step m action)]
    (du/setv! el k-focused (:focused m'))
    (send-all! el (model/events m action m'))
    (update-from-attrs! el)
    (focus-item! el)))

(defn- clicked
  "The id and the part (`:row` or `:chevron`) that `ev` hit, or nil outside a row."
  [^js ev]
  (when-some [^js row (.closest (.-target ev) sel-row)]
    [(.getAttribute (.-parentElement row) places/attr-key)
     (if (.closest (.-target ev) sel-chevron) :chevron :row)]))

(defn- on-click! [^js el ^js ev]
  (when-some [[id part] (clicked ev)]
    (some->> (model/click->action (du/getv el k-model) id part)
             (run-action! el))))

(defn- modified? [^js ev]
  (or (.-ctrlKey ev) (.-metaKey ev) (.-altKey ev)))

(defn- on-keydown! [^js el ^js ev]
  (when-not (modified? ev)
    (when-some [action (model/key->action (du/getv el k-model) (.-key ev))]
      (.preventDefault ev)
      (run-action! el action))))

(def ^:private listener-spec
  [["click"   on-click!]
   ["keydown" on-keydown!]])

(defn- install-listeners!
  "Binds the gestures of `el` to `tree`. The tree lives as long as the shadow root."
  [^js el ^js tree]
  (doseq [[event handler] listener-spec]
    (.addEventListener tree event (fn on-tree-event [ev] (handler el ev)))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-json-prop!   proto "items"       model/attr-items    model/array-of)
  (du/define-json-prop!   proto "expanded"    model/attr-expanded model/array-of)
  (du/define-json-prop!   proto "selected"    model/attr-selected model/array-of)
  (du/define-string-prop! proto "selection"   model/attr-selection)
  (du/define-string-prop! proto "label"       model/attr-label)
  (du/define-string-prop! proto "loadingText" model/attr-loading-text)
  (du/define-bool-prop!   proto "guides"      model/attr-guides))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
