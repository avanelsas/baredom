(ns baredom.components.x-toc.x-toc
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baremirror.alpha.places :as places]
            [baredom.components.x-toc.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs    "__xTocRefs")
(def ^:private k-model   "__xTocModel")
(def ^:private k-active  "__xTocActive")
(def ^:private k-binding "__xTocBinding")
(def ^:private k-pin     "__xTocPin")
(def ^:private k-raf     "__xTocRaf")
(def ^:private k-settle  "__xTocSettle")
(def ^:private k-update  "__xTocUpdate")
(def ^:private k-nodes   "__xTocNodes")

;; ── String constants ──────────────────────────────────────────────────────
(def ^:private attr-part         "part")
(def ^:private attr-href         "href")
(def ^:private attr-hidden       "hidden")
(def ^:private attr-level        "data-level")
(def ^:private attr-empty        "data-empty")
(def ^:private attr-aria-current "aria-current")
(def ^:private attr-aria-label   "aria-label")
(def ^:private attr-labelledby   "aria-labelledby")
(def ^:private attr-aria-hidden  "aria-hidden")

(def ^:private label-id         "label")
(def ^:private current-location "location")
(def ^:private reduced-motion   "(prefers-reduced-motion: reduce)")
(def ^:private settle-ms        150)

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "--x-toc-font-family:var(--x-font-family,inherit);"
   "--x-toc-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-toc-color:var(--x-color-text,#1f2937);"
   "--x-toc-hover-color:var(--x-color-primary-hover,#1d4ed8);"
   "--x-toc-active-color:var(--x-color-primary,#2563eb);"
   "--x-toc-label-color:var(--x-color-text-muted,#6b7280);"
   "--x-toc-label-font-family:var(--x-font-family-mono,ui-monospace,monospace);"
   "--x-toc-label-font-size:var(--x-font-size-xs,0.75rem);"
   "--x-toc-label-letter-spacing:0.12em;"
   "--x-toc-label-transform:uppercase;"
   "--x-toc-rail-color:var(--x-color-border,#e5e7eb);"
   "--x-toc-rail-width:var(--x-border-width,1px);"
   "--x-toc-indicator-color:var(--x-toc-active-color);"
   "--x-toc-indicator-width:3px;"
   "--x-toc-indicator-radius:var(--x-radius-full,9999px);"
   "--x-toc-gap:var(--x-space-sm,0.5rem);"
   "--x-toc-indent:0.75rem;"
   "--x-toc-sticky-top:1rem;"
   "--x-toc-transition-duration:var(--x-transition-duration,200ms);"
   "font-family:var(--x-toc-font-family);"
   "font-size:var(--x-toc-font-size);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-toc-color:var(--x-color-text,#e5e7eb);"
   "--x-toc-hover-color:var(--x-color-primary-hover,#93c5fd);"
   "--x-toc-active-color:var(--x-color-primary,#60a5fa);"
   "--x-toc-label-color:var(--x-color-text-muted,#9ca3af);"
   "--x-toc-rail-color:var(--x-color-border,#374151);}}"

   ":host([sticky]){"
   "position:sticky;"
   "top:var(--x-toc-sticky-top);"
   "align-self:start;}"
   du/hidden-rule

   "[part~='nav']{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-space-md,0.75rem);"
   "border-inline-start:var(--x-toc-rail-width) solid var(--x-toc-rail-color);"
   "padding-inline-start:0.75rem;"
   "box-sizing:border-box;}"

   ":host([sticky]) [part~='nav']{"
   "max-height:calc(100dvh - 2 * var(--x-toc-sticky-top));}"

   "[part~='label']{"
   "color:var(--x-toc-label-color);"
   "font-family:var(--x-toc-label-font-family);"
   "font-size:var(--x-toc-label-font-size);"
   "letter-spacing:var(--x-toc-label-letter-spacing);"
   "text-transform:var(--x-toc-label-transform);"
   "padding-inline-start:calc(var(--x-toc-indicator-width) + 0.75rem);}"

   "[part~='body']{"
   "position:relative;"
   "flex:1 1 auto;"
   "min-height:0;}"

   ":host([sticky]) [part~='body']{"
   "overflow-y:auto;"
   "scrollbar-gutter:stable;"
   "scrollbar-width:thin;"
   "overscroll-behavior:contain;}"

   "[part~='indicator']{"
   "position:absolute;"
   "top:0;"
   "inset-inline-start:0;"
   "width:var(--x-toc-indicator-width);"
   "height:0;"
   "border-radius:var(--x-toc-indicator-radius);"
   "background:var(--x-toc-indicator-color);"
   "transition:transform var(--x-toc-transition-duration) var(--x-transition-easing,ease);}"

   ":host([data-empty]) [part~='indicator']{display:none;}"

   "[part~='list']{"
   "list-style:none;"
   "margin:0;"
   "padding:0;"
   "padding-inline-start:calc(var(--x-toc-indicator-width) + 0.75rem);"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-toc-gap);}"

   "[part~='item']{margin:0;}"
   "[part~='item'][data-level='2']{padding-inline-start:calc(var(--x-toc-indent) * 1);}"
   "[part~='item'][data-level='3']{padding-inline-start:calc(var(--x-toc-indent) * 2);}"
   "[part~='item'][data-level='4']{padding-inline-start:calc(var(--x-toc-indent) * 3);}"
   "[part~='item'][data-level='5']{padding-inline-start:calc(var(--x-toc-indent) * 4);}"
   "[part~='item'][data-level='6']{padding-inline-start:calc(var(--x-toc-indent) * 5);}"

   "[part~='link']{"
   "display:block;"
   "color:var(--x-toc-color);"
   "text-decoration:none;"
   "line-height:var(--x-line-height-normal,1.5);"
   "overflow-wrap:anywhere;"
   "border-radius:var(--x-radius-sm,0.25rem);"
   "transition:color var(--x-toc-transition-duration) var(--x-transition-easing,ease);}"

   "[part~='link']:hover{color:var(--x-toc-hover-color);}"

   "[part~='link'][aria-current='location']{color:var(--x-toc-active-color);}"

   "[part~='link']:focus-visible{"
   "outline:2px solid var(--x-color-focus-ring,#2563eb);"
   "outline-offset:2px;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part~='indicator'],[part~='link']{transition:none;}}"

   "@media (pointer:coarse){"
   "[part~='link']{display:flex;align-items:center;min-height:2.75rem;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- make-label! []
  (let [label (.createElement js/document "div")]
    (du/set-attr! label attr-part "label")
    (du/set-attr! label model/attr-id label-id)
    label))

(defn- make-body! []
  (let [body      (.createElement js/document "div")
        indicator (.createElement js/document "span")
        ol        (.createElement js/document "ol")]
    (du/set-attr! body attr-part "body")
    (du/set-attr! indicator attr-part "indicator")
    (du/set-attr! indicator attr-aria-hidden "true")
    (du/set-attr! ol attr-part "list")
    (.appendChild body indicator)
    (.appendChild body ol)
    {:body body :indicator indicator :ol ol}))

(declare on-click!)

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        nav   (.createElement js/document "nav")
        label (make-label!)
        {:keys [body ol] :as parts} (make-body!)]
    (set! (.-textContent style) style-text)
    (du/set-attr! nav attr-part "nav")
    (.appendChild nav label)
    (.appendChild nav body)
    (.appendChild root style)
    (.appendChild root nav)
    ;; The list lives as long as the shadow tree, so its listener needs no remove path.
    (.addEventListener ol "click" (fn on-list-click [ev] (on-click! el ev)))
    (du/setv! el k-refs (assoc parts :nav nav :label label))))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Reading ──────────────────────────────────────────────────────────────
(defn- read-attrs [^js el]
  (model/normalize
   {:for-raw      (du/get-attr el model/attr-for)
    :selector-raw (du/get-attr el model/attr-selector)
    :label-raw    (du/get-attr el model/attr-label)
    :offset-raw   (du/get-attr el model/attr-offset)
    :update-hash? (du/has-attr? el model/attr-update-hash)}))

(defn- find-container
  "The element with the id `id` in the root of `el`, or nil."
  [^js el id]
  (when-not (= "" id)
    (.getElementById (.getRootNode el) id)))

(defn- find-headings
  "The nodes in `container` that match `selector`. None for an invalid selector."
  [^js container selector]
  (if container
    (try
      (vec (array-seq (.querySelectorAll container selector)))
      (catch :default _ []))
    []))

(defn- read-heading [^js node]
  {:id   (not-empty (.getAttribute node model/attr-id))
   :text (.-textContent node)
   :tag  (.-tagName node)})

(defn- taken-ids
  "The ids in the root of `el`, when one of `headings` has no id."
  [^js el headings]
  (if (some (comp nil? :id) headings)
    (into #{} (map (fn id-of [^js node] (.-id node)))
          (array-seq (.querySelectorAll (.getRootNode el) "[id]")))
    #{}))

(defn- identify
  "The headings in `nodes` with their ids, as `model/identify` gives them."
  [^js el nodes]
  (let [headings (mapv read-heading nodes)]
    (model/identify headings (taken-ids el headings))))

(defn- node-by-id
  "A map from each id of `identified` to its node in `nodes`. The first node of an id wins."
  [nodes identified]
  (into {} (map (fn pair [{:keys [id at]}] [id (nth nodes at)])) (rseq identified)))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- make-item!
  "A new list item with a link."
  []
  (let [li   (.createElement js/document "li")
        link (.createElement js/document "a")]
    (du/set-attr! li attr-part "item")
    (du/set-attr! link attr-part "link")
    (.appendChild li link)
    li))

(defn- apply-item! [^js li {:keys [id text level]}]
  (let [link (.-firstElementChild li)]
    (du/set-attr-to! li attr-level (str level))
    (du/set-attr-to! link attr-href (str "#" id))
    (du/set-text-to! link text)))

(defn- render-items!
  "Brings the items of the list in step with `entries`. An item that stays keeps its node."
  [^js ol entries]
  (let [nodes (places/sync! {:items {:parent ol}}
                            {:items (mapv :id entries)}
                            (fn new-item [_] (make-item!)))]
    (run! (fn apply-entry! [{:keys [id] :as entry}] (apply-item! (nodes id) entry)) entries)))

(defn- write-ids!
  "Writes each `[at id]` pair of `writes` as the id of the node at `at` in `nodes`."
  [nodes writes]
  (doseq [[at id] writes]
    (du/set-attr-to! (nth nodes at) model/attr-id id)))

(defn- apply-label! [{:keys [nav label]} m]
  (du/set-text-to! label (:label m))
  (du/set-bool-attr-to! label attr-hidden (not (:labelled? m)))
  (du/set-attr-to! nav attr-labelledby (when (:labelled? m) label-id))
  (du/set-attr-to! nav attr-aria-label (when-not (:labelled? m) model/default-label)))

(defn- apply-model! [^js el refs m]
  (apply-label! refs m)
  (render-items! (:ol refs) (:entries m))
  (du/set-bool-attr-to! el attr-empty (empty? (:entries m)))
  (du/setv! el k-model m))

;; ── Scroll root ───────────────────────────────────────────────────────────
(def ^:private scrolling-overflow #{"auto" "scroll" "overlay"})

(defn- scrolls? [^js node]
  (contains? scrolling-overflow (.-overflowY (js/getComputedStyle node))))

(defn- parent-of [^js node]
  (or (.-parentElement node)
      (.-host (.getRootNode node))))

(defn- page-root? [^js node]
  (or (identical? node (.-body js/document))
      (identical? node (.-documentElement js/document))))

(defn- scroll-root
  "The nearest element from `container` up that scrolls, or nil for the viewport."
  [^js container]
  (loop [^js node container]
    (cond
      (or (nil? node) (page-root? node)) nil
      (scrolls? node)                    node
      :else                              (recur (parent-of node)))))

(defn- root-box
  "The top and height of the visible area of the scroll root `root`."
  [^js root]
  (if root
    {:top    (+ (.-top (.getBoundingClientRect root)) (.-clientTop root))
     :height (.-clientHeight root)}
    {:top 0 :height (.-innerHeight js/window)}))

(defn- at-bottom?
  "True when the scroll root `root` can scroll and is at its end."
  [^js root]
  (let [^js s (or root (.-scrollingElement js/document))]
    (and (> (.-scrollHeight s) (inc (.-clientHeight s)))
         (>= (+ (.-scrollTop s) (.-clientHeight s)) (dec (.-scrollHeight s))))))

;; ── Active entry ──────────────────────────────────────────────────────────
(defn- heading-node
  "The heading node of the entry `id`, or nil."
  [^js el id]
  (get (du/getv el k-nodes) id))

(defn- top-of [^js el box-top {:keys [id]}]
  (if-let [^js node (heading-node el id)]
    (- (.-top (.getBoundingClientRect node)) box-top)
    ##Inf))

(defn- measured-id
  "The id of the entry the reader is at, from the layout."
  [^js el {:keys [root]} {:keys [entries offset]}]
  (let [{:keys [top height]} (root-box root)
        tops (mapv (partial top-of el top) entries)
        i    (model/active-index tops (model/offset-px offset height) (at-bottom? root))]
    (when i (:id (nth entries i)))))

(defn- entry-by-id [{:keys [entries]} id]
  (first (filter (comp #{id} :id) entries)))

(defn- item-nodes [^js ol]
  (array-seq (.-children ol)))

(defn- item-key [^js li]
  (.getAttribute li places/attr-key))

(defn- keep-in-view!
  "Scrolls the list body so that `li` shows, when the body scrolls."
  [^js body ^js li]
  (let [top    (.-offsetTop li)
        bottom (+ top (.-offsetHeight li))
        view   (.-scrollTop body)
        height (.-clientHeight body)]
    (when (> (.-scrollHeight body) height)
      (cond
        (< top view)               (set! (.-scrollTop body) top)
        (> bottom (+ view height)) (set! (.-scrollTop body) (- bottom height))))))

(defn- place-indicator! [^js indicator ^js li]
  (du/set-style-to! indicator "transform" (str "translateY(" (.-offsetTop li) "px)"))
  (du/set-style-to! indicator "height" (str (.-offsetHeight li) "px")))

(defn- active-item [^js ol id]
  (first (filter (comp #{id} item-key) (item-nodes ol))))

(defn- apply-active!
  "Marks the link of the entry `id` as current and moves the indicator to it."
  [{:keys [ol indicator]} id]
  (doseq [^js li (item-nodes ol)]
    (du/set-attr-to! (.-firstElementChild li) attr-aria-current
                     (when (= id (item-key li)) current-location)))
  (some->> (active-item ol id) (place-indicator! indicator)))

(defn- set-active!
  "Makes the entry `id` active, and announces a move from one entry to another."
  [^js el m id]
  (let [old  (du/getv el k-active)
        refs (du/getv el k-refs)]
    (apply-active! refs id)
    (when (not= old id)
      (du/setv! el k-active id)
      (some->> (active-item (:ol refs) id) (keep-in-view! (:body refs)))
      (when (and old id)
        (du/dispatch! el model/event-change
                      (clj->js (model/change-detail (entry-by-id m id) old)))))))

(defn- pinned-id
  "The id of the entry a click pinned, while it is in the list."
  [^js el m]
  (let [id (:id (du/getv el k-pin))]
    (when (entry-by-id m id) id)))

(defn- refresh-active! [^js el]
  (when-let [m (du/getv el k-model)]
    (set-active! el m (or (pinned-id el m) (measured-id el (du/getv el k-binding) m)))))

;; ── Scheduling ────────────────────────────────────────────────────────────
(declare update!)

(defn- once-a-frame!
  "Calls `f` in the next frame, once however often it is asked for. `k` holds the frame id."
  [^js el k f]
  (when-not (du/getv el k)
    ;; Animation bookkeeping: the frame id has no display value.
    (du/setv-untraced! el k (js/requestAnimationFrame
                             (fn frame []
                               (du/setv-untraced! el k nil)
                               (f el))))))

(defn- schedule-frame! [^js el]
  (once-a-frame! el k-raf refresh-active!))

(defn- schedule-update!
  "Reads the container again in the next frame."
  [^js el]
  (once-a-frame! el k-update update!))

(defn- set-pin!
  "Moves the pin by `event` and returns the new pin."
  [^js el event]
  (let [old (du/getv el k-pin)
        pin (model/next-pin old event)]
    (when (not= old pin) (du/setv! el k-pin pin))
    pin))

(defn- schedule-settle!
  "Settles the pin once no scroll has come for a moment."
  [^js el]
  (js/clearTimeout (du/getv el k-settle))
  ;; Transient handle: the timer id has no display value.
  (du/setv-untraced! el k-settle (js/setTimeout (fn settle [] (set-pin! el [:settle])) settle-ms)))

(defn- on-scroll! [^js el]
  (if (set-pin! el [:scroll])
    (schedule-settle! el)
    (schedule-frame! el)))

;; ── Navigation ────────────────────────────────────────────────────────────
(defn- reduced-motion? []
  (.-matches (js/matchMedia reduced-motion)))

(defn- scroll-to-heading!
  "Scrolls the heading `node` to the activation line."
  [{:keys [root scroll-target]} {:keys [offset]} ^js node]
  (let [{:keys [top height]} (root-box root)
        delta (- (.-top (.getBoundingClientRect node)) top (model/offset-px offset height))]
    (.scrollBy ^js scroll-target
               #js {:top delta :behavior (if (reduced-motion?) "auto" "smooth")})))

(defn- focus-heading! [^js node]
  (when (and (neg? (.-tabIndex node)) (not (.hasAttribute node model/attr-tabindex)))
    (du/set-attr! node model/attr-tabindex "-1"))
  (.focus node #js {:preventScroll true}))

(defn- replace-hash! [id]
  (.replaceState js/history (.-state js/history) "" (str "#" id)))

(defn- navigate! [^js el m id ^js node]
  (set-pin! el [:click id])
  (schedule-settle! el)
  (set-active! el m id)
  (scroll-to-heading! (du/getv el k-binding) m node)
  (focus-heading! node)
  (when (:update-hash? m) (replace-hash! id)))

(defn- plain-click? [^js ev]
  (and (zero? (.-button ev))
       (not (or (.-ctrlKey ev) (.-metaKey ev) (.-shiftKey ev) (.-altKey ev)))))

(defn- clicked-entry
  "The entry of the item that `ev` hit, or nil."
  [m ^js ev]
  (some->> (.closest (.-target ev) "[part~='item']") item-key (entry-by-id m)))

(defn- on-click! [^js el ^js ev]
  (let [m      (du/getv el k-model)
        entry  (when (plain-click? ev) (clicked-entry m ev))
        ^js node (some->> entry :id (heading-node el))]
    (when node
      (.preventDefault ev)
      (when (du/dispatch-cancelable! el model/event-navigate
                                     (clj->js (model/navigate-detail entry)))
        (navigate! el m (:id entry) node)))))

;; ── Binding ───────────────────────────────────────────────────────────────
(defn- watch! [^js node options on-change]
  (doto (js/MutationObserver. on-change)
    (.observe node options)))

(defn- watch-sizes! [^js el nodes]
  (let [observer (js/ResizeObserver. (fn on-size [] (schedule-frame! el)))]
    (run! (fn observe [^js node] (.observe observer node)) nodes)
    observer))

(defn- container-moved?
  "True when `for` names another node than the bound container."
  [^js el]
  (not (identical? (:container (du/getv el k-binding))
                   (find-container el (model/clean-text (du/get-attr el model/attr-for))))))

(defn- watch-root!
  "Watches the root of `el` for the container that `for` names to appear, leave or change."
  [^js el]
  (watch! (.getRootNode el) #js {:childList true :subtree true}
          (fn on-root-change [] (when (container-moved? el) (schedule-update! el)))))

(defn- bind-container! [^js el refs ^js container]
  (let [root      (scroll-root container)
        target    (or root js/window)
        on-scroll (fn on-scroll [] (on-scroll! el))
        on-resize (fn on-resize [] (schedule-frame! el))]
    (.addEventListener target "scroll" on-scroll #js {:passive true})
    (.addEventListener js/window "resize" on-resize)
    {:root          root
     :scroll-target target
     :on-scroll     on-scroll
     :on-resize     on-resize
     :sizer         (watch-sizes! el [(:ol refs) container])
     :content       (watch! container
                            #js {:childList true :subtree true :characterData true
                                 :attributeFilter #js [model/attr-id]}
                            (fn on-content-change [] (schedule-update! el)))}))

(defn- unbind! [^js el]
  (when-let [{:keys [^js watcher ^js content ^js sizer ^js scroll-target on-scroll on-resize]}
             (du/getv el k-binding)]
    (.disconnect watcher)
    (some-> content .disconnect)
    (some-> sizer .disconnect)
    (when scroll-target
      (.removeEventListener scroll-target "scroll" on-scroll #js {:passive true})
      (.removeEventListener js/window "resize" on-resize)))
  (js/cancelAnimationFrame (du/getv el k-raf))
  (js/cancelAnimationFrame (du/getv el k-update))
  (js/clearTimeout (du/getv el k-settle))
  (du/setv-untraced! el k-raf nil)
  (du/setv-untraced! el k-update nil)
  (du/setv! el k-binding nil)
  (du/setv! el k-pin nil)
  (du/setv! el k-active nil))

(defn- bind!
  "Binds `el` to `container`, or to nothing, unless it is bound to it already."
  [^js el refs ^js container]
  (let [binding (du/getv el k-binding)]
    (when-not (and binding (identical? container (:container binding)))
      (unbind! el)
      (du/setv! el k-binding (assoc (when container (bind-container! el refs container))
                                    :container container
                                    :watcher   (watch-root! el))))))

(defn- update! [^js el]
  (when (.-isConnected el)
    (let [refs       (ensure-refs! el)
          attrs      (read-attrs el)
          container  (find-container el (:for attrs))
          nodes      (find-headings container (:selector attrs))
          identified (identify el nodes)
          new-m      (assoc attrs :entries (model/entries identified))]
      (bind! el refs container)
      (write-ids! nodes (model/id-writes identified))
      (du/setv! el k-nodes (node-by-id nodes identified))
      (when (not= new-m (du/getv el k-model))
        (apply-model! el refs new-m))
      (refresh-active! el))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- active-id
  "The id of the active entry, or null where the field was never set."
  [^js el]
  (or (du/getv el k-active) nil))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (du/define-readonly-prop! proto "activeId" active-id))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (update! el))

(defn- disconnected! [^js el]
  (unbind! el))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update! el)))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
