(ns baredom.dev.x-trace-history.x-trace-history
  "Dev-only floating dock for x-trace-history.

   When activated (?baredom-trace-history or window.BAREDOM_TRACE_HISTORY),
   register! installs the recorder hooks (via recorder/register!), defines a
   `<x-trace-history>` custom element, and auto-mounts one to <body>. The
   dock subscribes to recorder updates and renders an SVG timeline: one
   lane per component instance (Y), event time on X, dots coloured by
   category. Hover for tooltip, click to open the JSON detail pane."
  (:require
   [clojure.string :as str]
   [goog.object :as gobj]
   [baredom.utils.component :as comp]
   [baredom.dev.x-trace-history.dock-css :as dock-css]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]
   ;; Require the component implementations directly rather than the
   ;; baredom.exports.x-* wrappers: each exports ns is the entry of a
   ;; separate :lib module, and pulling them in from here would force
   ;; shadow-cljs to move those entries into :base — breaking the
   ;; per-module ESM split. The components themselves are plain
   ;; shareable code that shadow-cljs is happy to land in :base on its
   ;; own. Unaliased + fully-qualified call sites match the existing
   ;; convention in baredom.exports.x_*.cljs.
   [baredom.components.x-button.x-button]
   [baredom.components.x-checkbox.x-checkbox]
   [baredom.components.x-search-field.x-search-field]
   [baredom.components.x-select.x-select]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private lane-h      20)  ; px per timeline lane
;; Dot radii sized for clickable hit targets at typical dock width.
;; A 4.5px radius reads as a clear 9px-diameter circle on screen and
;; pairs with model/plot-padding-x (8px) to keep edge dots fully
;; visible without overlapping into the lane-label column. Selected
;; dots get a 6.5px radius so the highlight reads at a glance.
(def ^:private dot-r      4.5)  ; default dot radius
(def ^:private dot-r-sel  6.5)  ; dot radius when selected
(def ^:private svg-min-w 200)   ; floor for plot width when pane has no size yet

;; PR 17 — causality node dot radius (the colour indicator on the
;; leading edge of each tree node). Small enough not to crowd the
;; label, big enough to read at dock zoom.
(def ^:private causality-dot-r 4)

;; ---------------------------------------------------------------------------
;; HTML escaping
;; ---------------------------------------------------------------------------
;; Each render passes hundreds of strings through escape-html (every
;; label, every tooltip, every detail JSON). The five patterns are
;; module-level constants so we don't allocate fresh RegExp instances
;; on every call.

(def ^:private re-amp   (js/RegExp. "&"  "g"))
(def ^:private re-lt    (js/RegExp. "<"  "g"))
(def ^:private re-gt    (js/RegExp. ">"  "g"))
(def ^:private re-quot  (js/RegExp. "\"" "g"))
(def ^:private re-apos  (js/RegExp. "'"  "g"))

(defn- escape-html
  "Escape HTML metacharacters for safe interpolation into innerHTML."
  [s]
  (-> (str s)
      (.replace re-amp  "&amp;")
      (.replace re-lt   "&lt;")
      (.replace re-gt   "&gt;")
      (.replace re-quot "&quot;")
      (.replace re-apos "&#39;")))

;; ---------------------------------------------------------------------------
;; Static skeleton
;; ---------------------------------------------------------------------------

(defn- tag-option-html
  "One <option> tag for the tag-filter dropdown, with the tag name as
   both value and visible label. Used by sync-tag-options! when a new
   tag arrives — the static skeleton ships only the 'All tags' default,
   and observed tags get appended at runtime so the dock has no static
   dependency on the component registry."
  [tag]
  (str "<option value='" (escape-html tag) "'>" (escape-html tag) "</option>"))

(defn- cat-checkbox-html
  "One labeled x-checkbox for a category. `checked?` reflects the
   initial filter spec — in normal mode :state is unchecked so the
   skeleton + the spec stay in sync from frame zero (no flicker, no
   visual lie about what's currently filtered)."
  [cat checked?]
  (str "<label class='cat'>"
       "<x-checkbox data-x-th-cat='" cat "' "
       (when checked? "checked ")
       "aria-label='" cat "'></x-checkbox>"
       cat
       "</label>"))

(defn- skeleton-html
  "Dock skeleton, parameterized by initial-categories so the rendered
   <x-checkbox> elements agree with the dock's filter spec. Pause /
   Record / Clear are <x-button>s; click events bubble natively so the
   click delegate on the dock root works unchanged. The recorder's
   internal-host boundary (PR-A) suppresses x-button's own press / hover
   events so they never appear in the trace."
  [initial-categories]
  (str "<div class='dock'>"
       "<div class='header'>"
       ;; Collapse-toggle button. Placed first so it stays in the same
       ;; spot whether the dock is expanded (leftmost in the header
       ;; row) or collapsed (top of the thin vertical strip — the
       ;; only visible control). Default content is `«` meaning
       ;; 'push me to the right'; the toggle swaps it to `»` when
       ;; collapsed. Keyboard shortcut documented in the title.
       "<x-button data-x-th-action='collapse' size='sm' variant='ghost' "
       "aria-label='Collapse panel' "
       "title='Collapse / expand panel (Ctrl/Cmd + \\\\)'>«</x-button>"
       "<span class='title'>x-trace-history</span>"
       "<x-button data-x-th-action='pause'  size='sm' variant='ghost'>Pause</x-button>"
       "<x-button data-x-th-action='record' size='sm' variant='ghost'>Record</x-button>"
       "<x-button data-x-th-action='clear'  size='sm' variant='ghost'>Clear</x-button>"
       "<x-button data-x-th-action='export' size='sm' variant='ghost' "
       "title='Download trace as .trace.json'>Export</x-button>"
       "<x-button data-x-th-action='import' size='sm' variant='ghost' "
       "title='Load a .trace.json file (or drag-drop one onto the dock)'>Import</x-button>"
       "<span class='count' data-x-th-count>0</span>"
       "</div>"
       ;; Session strip: hidden by default; render! shows it once the
       ;; user has captured at least one session. Populated dynamically.
       "<div class='sessions' data-x-th-sessions hidden></div>"
       "<div class='filters'>"
       ;; <option selected> (rather than value='all' on the host)
       ;; survives x-select's sync-options! clone path. value-on-host
       ;; gets applied during the initial render, but the inner native
       ;; <select> is still empty at that point — its slotted options
       ;; are cloned in asynchronously on slotchange, so the value
       ;; never lands. The HTML `selected` attribute is preserved by
       ;; cloneNode(true) and selects the option after the clone.
       ;; Tag dropdown starts with only the 'All tags' default option;
       ;; sync-tag-options! appends one <option> per observed tag on
       ;; every render. We avoid statically enumerating the component
       ;; registry here because that would transitively pull every
       ;; BareDOM component into the x-trace-history ESM bundle and
       ;; defeat per-module distribution.
       "<x-select data-x-th-tag size='sm'>"
       "<option value='all' selected>All tags</option>"
       "</x-select>"
       ;; PR 16 — full-text search across record JSON. Uses x-search-
       ;; field so the dock's controls dogfood the library uniformly
       ;; (x-button, x-checkbox, x-select, x-search-field). The search
       ;; haystack is lazily indexed on the record itself (see
       ;; model/searchable-haystack) so the recording hot path pays
       ;; nothing; the first search per record allocates the haystack,
       ;; subsequent searches reuse it.
       "<x-search-field data-x-th-search name='search' "
       "placeholder='Search records…' autocomplete='off'></x-search-field>"
       ;; x-checkbox has no default slot — text inside the host is
       ;; invisible. Wrap in <label> per docs/x-checkbox.md so the
       ;; visible label sits next to the checkbox AND clicking it
       ;; toggles the control (native label-association behavior).
       (cat-checkbox-html "events"    (contains? initial-categories :events))
       (cat-checkbox-html "state"     (contains? initial-categories :state))
       (cat-checkbox-html "dom"       (contains? initial-categories :dom))
       (cat-checkbox-html "lifecycle" (contains? initial-categories :lifecycle))
       ;; PR 17 — view-mode toggle. :timeline shows the SVG lane view
       ;; (the default since PR 5); :causality shows the cause→effect
       ;; tree of the currently-selected record. The two panes share
       ;; the flexible vertical slot above the detail; render! hides
       ;; whichever isn't active.
       "<x-select data-x-th-mode size='sm'>"
       "<option value='timeline' selected>Timeline</option>"
       "<option value='causality'>Causality</option>"
       "</x-select>"
       ;; Axis-mode toggle. Default option is rendered with `selected`
       ;; so x-select picks it up after its slotchange clone, same
       ;; trick as the tag filter above. Order is the default — see
       ;; the design rationale in model/axis-modes.
       "<x-select data-x-th-axis size='sm'>"
       "<option value='order' selected>Order</option>"
       "<option value='time'>Time</option>"
       "</x-select>"
       "</div>"
       "<div class='timeline' data-x-th-timeline tabindex='0'>"
       "<div class='lanes' data-x-th-lanes></div>"
       "<div class='svg-pane' data-x-th-svg-pane></div>"
       "<div class='tooltip' data-x-th-tooltip hidden></div>"
       "</div>"
       ;; Causality DAG pane — shares the flexible slot with .timeline.
       ;; Hidden by default; render! flips the hidden attributes based
       ;; on the active dock-mode. tabindex makes it focusable so the
       ;; keyboard arrow stepping works the same way the timeline does.
       "<div class='causality' data-x-th-causality tabindex='0' hidden></div>"
       "<div class='splitter' data-x-th-splitter hidden></div>"
       "<div class='detail' data-x-th-detail hidden></div>"
       "<div class='hint' data-x-th-hint></div>"
       ;; Drop overlay sits inside .dock (which is position:relative) so
       ;; `inset: 0` covers the dock. pointer-events:none on the overlay
       ;; (CSS) lets drag events pass through to .dock so the dragenter
       ;; / dragleave counter stays accurate.
       "<div class='drop-overlay' data-x-th-drop-overlay hidden>"
       "Drop .trace.json to import</div>"
       ;; Hidden <input type='file'> that the Import button clicks to
       ;; open the OS picker. accept gates by extension; the change
       ;; handler still validates the contents.
       "<input class='import-input' data-x-th-import-input "
       "type='file' accept='.json,.trace.json,application/json' />"
       "</div>"))

;; ---------------------------------------------------------------------------
;; Timeline rendering
;; ---------------------------------------------------------------------------

(defn- lane-label-html
  [{:keys [lane-id tag] :as lane} active-tag]
  (let [text     (model/lane-label lane)
        ;; The tag attribute we filter on. Document-lane records carry
        ;; tag="document" on the wire, so this matches.
        tag-attr (if (= model/document-lane lane-id) "document" tag)
        active?  (= active-tag tag-attr)]
    (str "<div class='lane-label" (when active? " active") "' "
         "data-x-th-lane-tag='" (escape-html tag-attr) "' "
         "title='" (escape-html text) "'>"
         (escape-html (if (= model/document-lane lane-id) "document" tag))
         (when (not= model/document-lane lane-id)
           (str " <span class='cid'>#" lane-id "</span>"))
         "</div>")))

(defn- lanes-html
  [lanes active-tag]
  (apply str (map #(lane-label-html % active-tag) lanes)))

(defn- record-cx
  "Compute the x-coordinate for a single record under the active
   axis mode. :order uses index-x against the record's position in
   the filtered list; :time uses the linear time-x mapping. Pulled
   out of dot-html so the scrubber can share it without duplicating
   the dispatch."
  [axis-mode ^js r index n bounds plot-width]
  (case axis-mode
    :order (model/index-x index n plot-width)
    :time  (model/time-x (.-t r) bounds plot-width)))

(defn- dot-html
  [^js r cx lane-y selected-id]
  (let [cy   (+ lane-y (/ lane-h 2))
        sel? (= (.-id r) selected-id)
        rad  (if sel? dot-r-sel dot-r)]
    (str "<circle class='dot" (when sel? " selected") "' "
         "data-x-th-id='" (.-id r) "' "
         "cx='" cx "' cy='" cy "' r='" rad "' "
         "fill='" (model/dot-color r) "' />")))

(defn- scrubber-html
  "Vertical drag-line drawn at the selected record's x. Returns the
   empty string when no record is selected. `cx` is pre-computed by
   svg-html under whichever axis mode is active."
  [^js sel-rec cx svg-h]
  (if sel-rec
    (str "<line class='scrubber' x1='" cx "' y1='0' "
         "x2='" cx "' y2='" svg-h "' />")
    ""))

(defn- dots-html
  "Emit one <circle> per record in a lane. Selected dot drawn last so
   its highlight stroke wins z-order."
  [lane-recs lane-y cx-of selected-id]
  (let [ordered (sort-by (fn [^js r] (if (= (.-id r) selected-id) 1 0))
                         lane-recs)]
    (apply str
           (map (fn [^js r] (dot-html r (cx-of r) lane-y selected-id))
                ordered))))

(defn- density-bin-rect-html
  "One <rect> for a single heatmap bin. data-x-th-bin-record-id
   carries the id of the bin's first record so the click delegate
   (on-dot-click!) routes selection to a known record.
   data-x-th-bin-count + data-x-th-bin-cat are surfaced so the
   tooltip handler can build the bin tooltip without re-binning the
   lane at hover time. role='button' + aria-label give screen-reader
   users a summary of what the bin represents (count + category)
   since a heatmap rectangle has no native semantics."
  [{:keys [records dominant-cat x-start]} lane-y max-count]
  (let [^js r0       (first records)
        cat-color    (get model/category-colors (or dominant-cat :other))
        opacity      (model/bin-opacity (count records) max-count)
        cat-name     (escape-html (name (or dominant-cat :other)))
        n            (count records)
        aria-label   (str n " " (if (= 1 n) "record" "records")
                          " · " (name (or dominant-cat :other)))]
    (str "<rect class='density-bin' role='button' "
         "aria-label='" (escape-html aria-label) "' "
         "data-x-th-bin-record-id='" (.-id r0) "' "
         "data-x-th-bin-count='" n "' "
         "data-x-th-bin-cat='" cat-name "' "
         "x='" x-start "' y='" lane-y "' "
         "width='" model/density-bin-width "' height='" lane-h "' "
         "fill='" cat-color "' fill-opacity='" opacity "' />")))

(defn- selected-in-band-html
  "Vertical 2px bar drawn at the selected record's exact x inside its
   bin — marks the precise position without obscuring the band's
   colour. Returns empty string when no record is selected OR when
   the lane's bins don't contain it. The nil-id short-circuit skips
   the O(N) walk over bins on every render-without-selection."
  [bins lane-y selected-id cx-of]
  (if (nil? selected-id)
    ""
    (let [^js sel-rec
          (some (fn [{:keys [records]}]
                  (some (fn [^js r] (when (= (.-id r) selected-id) r))
                        records))
                bins)]
      (if sel-rec
        (let [x (cx-of sel-rec)]
          (str "<line class='density-selected' "
               "x1='" x "' y1='" lane-y "' "
               "x2='" x "' y2='" (+ lane-y lane-h) "' />"))
        ""))))

(defn- density-band-html
  "Emit one <rect> per bin for a dense lane, plus a thin vertical bar
   at the selected record's x if it lies in this lane."
  [bins lane-y selected-id cx-of]
  (let [max-count (model/lane-max-bin-count bins)]
    (str (apply str
                (map (fn [b] (density-bin-rect-html b lane-y max-count))
                     bins))
         (selected-in-band-html bins lane-y selected-id cx-of))))

(defn- lane-svg-html
  "Render one lane: either a heatmap band (if :time axis AND the lane
   crosses the density threshold) or individual dots (everything
   else). :order axis skips the density check — uniform-by-index
   spacing means no overlap to compress."
  [axis-mode lane-recs lane-y cx-of selected-id]
  (if (= :time axis-mode)
    (let [bins (model/bin-records-by-x lane-recs cx-of)]
      (if (model/lane-is-dense? bins)
        (density-band-html bins lane-y selected-id cx-of)
        (dots-html lane-recs lane-y cx-of selected-id)))
    (dots-html lane-recs lane-y cx-of selected-id)))

(defn- svg-html
  "Build the timeline SVG markup. Records are grouped by lane (Y) and
   positioned by the active axis mode (X) — :order spreads records
   evenly by index, :time maps them by `t`. Each lane decides
   independently whether to render as dots or as a heatmap band
   (PR 18). The scrubber line is drawn last so it overlays
   everything."
  [axis-mode filtered-records lanes bounds plot-width ^js sel-rec]
  (let [selected-id      (when sel-rec (.-id sel-rec))
        lane-y           (into {} (map-indexed
                                   (fn [i {:keys [lane-id]}] [lane-id (* i lane-h)])
                                   lanes))
        h                (max lane-h (* lane-h (count lanes)))
        n                (count filtered-records)
        ;; Capture each filtered record's index BEFORE we partition
        ;; by lane so the order-axis position survives the grouping.
        id->index        (into {} (map-indexed
                                   (fn [i ^js r] [(.-id r) i]) filtered-records))
        cx-of            (fn [^js r]
                           (record-cx axis-mode r (get id->index (.-id r)) n
                                      bounds plot-width))
        records-by-lane  (group-by model/lane-id-of filtered-records)]
    (str "<svg class='timeline-svg' width='" plot-width "' height='" h
         "' viewBox='0 0 " plot-width " " h "'>"
         (apply str
                (map (fn [{:keys [lane-id]}]
                       (lane-svg-html axis-mode
                                      (get records-by-lane lane-id [])
                                      (get lane-y lane-id)
                                      cx-of
                                      selected-id))
                     lanes))
         (scrubber-html sel-rec (when sel-rec (cx-of sel-rec)) h)
         "</svg>")))

(defn- render-timeline!
  "Repaint the lane-label column + SVG canvas. Caller passes the current
   filtered records, bounds, axis-mode, etc. (so render! computes them
   once)."
  [^js lanes-el ^js svg-pane-el axis-mode filtered lanes bounds ^js sel-rec active-tag]
  (if (zero? (count filtered))
    (do
      (set! (.-innerHTML lanes-el) "")
      (set! (.-innerHTML svg-pane-el)
            "<div class='timeline-empty'>No records match the current filter.</div>"))
    (let [plot-w (max svg-min-w (.-clientWidth svg-pane-el))]
      (set! (.-innerHTML lanes-el)    (lanes-html lanes active-tag))
      (set! (.-innerHTML svg-pane-el) (svg-html axis-mode filtered lanes bounds
                                                plot-w sel-rec)))))

;; ---------------------------------------------------------------------------
;; Causality DAG rendering (PR 17)
;;
;; The causality pane shares the flexible vertical slot with the
;; timeline pane. render! decides which one is visible by toggling
;; their hidden attributes from the active dock-mode. Inside the
;; causality pane the renderer builds a tree rooted at the highest
;; ancestor of the currently-selected record, lays it out, and emits
;; one <rect>+<text>+<circle> group per node plus one <line> per edge.
;;
;; Selection / scrubbing stays in sync with the timeline: clicking a
;; node updates k-view-selected exactly like the timeline's dot click,
;; so toggling back to :timeline keeps the user oriented.
;; ---------------------------------------------------------------------------

(defn- label-of-node
  "Compact label rendered inside a tree-node rect: 'tag · type'. The
   leading '#id' goes into a secondary smaller label on the same row
   so the primary text doesn't repeat what the rect's hover-title
   already says."
  [^js r]
  (str (.-tag r) " · " (.-type r)))

(defn- short-label
  "Truncate a label string to fit inside a node rect. The node is 140
   px wide; at the dock's 10 px monospace font ~22 chars fit before
   the text would clip the right border."
  [s]
  (let [s (str s)]
    (if (<= (count s) 22)
      s
      (str (subs s 0 21) "…"))))

(defn- causality-node-html
  "One <g> per node: a coloured dot on the left, a rect that hosts the
   click target, an `#id` minor label, and the primary tag · type
   label. data-x-th-link-id makes the rect routable through the
   existing on-detail-link-click! delegate that already handles
   numeric ids — so causality clicks reuse the same selection plumbing
   the cause/effect detail-pane links use.

   Nodes carry role='button' (for screen-reader announcement on
   click) but no tabindex — the parent pane is the single tab stop,
   and arrow keys step the selection from there. A per-node tabindex
   would create one tab stop per tree node, which is unmanageable
   for trees near the 200-node cap."
  [{:keys [id ^js record cx cy]} selected-id]
  (let [^js r       record
        left-x      (- cx (/ model/causality-node-w 2))
        top-y       (- cy (/ model/causality-node-h 2))
        sel?        (= id selected-id)
        dot-cx      (+ left-x 12)
        id-x        (+ left-x 22)
        label-x     (+ left-x 22)
        label-y     (+ top-y 18)
        id-y        (+ top-y 11)
        title-text  (escape-html (str "#" id " " (label-of-node r)
                                      " · " (model/payload-preview r)))]
    (str "<g data-x-th-link-id='" id "' role='button' "
         "aria-label='" title-text "'>"
         "<title>" title-text "</title>"
         "<rect class='node-rect" (when sel? " selected") "' "
         "x='" left-x "' y='" top-y "' "
         "width='" model/causality-node-w "' height='" model/causality-node-h "' "
         "/>"
         "<circle class='node-dot' cx='" dot-cx "' cy='" cy "' "
         "r='" causality-dot-r "' fill='" (model/dot-color r) "' />"
         "<text class='node-text id' x='" id-x "' y='" id-y "'>#" id "</text>"
         "<text class='node-text' x='" label-x "' y='" label-y "'>"
         (escape-html (short-label (label-of-node r))) "</text>"
         "</g>")))

(defn- causality-edge-html
  "One <line> per parent→child edge. Drawn before nodes so they sit
   underneath rects in the SVG z-order."
  [{:keys [from-cx from-cy to-cx to-cy]}]
  (let [;; Connect bottom of parent to top of child so the line doesn't
        ;; pierce the node rects.
        y1 (+ from-cy (/ model/causality-node-h 2))
        y2 (- to-cy   (/ model/causality-node-h 2))]
    (str "<line class='edge' "
         "x1='" from-cx "' y1='" y1 "' "
         "x2='" to-cx   "' y2='" y2 "' />")))

(defn- causality-svg-html
  "Build the inner SVG markup for a laid-out tree. Edges first (z-order
   underneath), then nodes. width / height come from layout-tree so
   the SVG's intrinsic size matches the tree's bounding box, which
   makes the scrolling container's scrollbars accurate."
  [layout selected-id]
  (let [{:keys [nodes edges width height]} layout]
    (str "<svg class='causality-svg' "
         "width='" width "' height='" height "' "
         "viewBox='0 0 " width " " height "'>"
         (apply str (map causality-edge-html edges))
         (apply str (map #(causality-node-html % selected-id) nodes))
         "</svg>")))

(defn- leaf-hint-html
  "Banner above a single-node tree explaining there's no chain to draw.
   Common case: the selected record was emitted outside any dispatch
   frame (constructor / lifecycle path), so its causeId is null AND
   nothing else has it as a cause. Without this banner the pane looks
   indistinguishable from a broken view."
  []
  (str "<div class='causality-leaf-hint'>"
       (escape-html (model/causality-leaf-message))
       "</div>"))

(defn- render-causality!
  "Repaint the causality pane. Branches on selection / size so the
   user always sees a useful message instead of a blank pane.
     - no selection         → empty-state hint
     - tree > cap           → over-cap hint
     - tree = 1 node        → SVG tree + leaf-hint banner
     - else                 → SVG tree
   Stashes the layout map on the host so the post-render fit-to-view
   step can read node coordinates without rebuilding the tree."
  [^js el ^js causality-el ^js recs ^js sel-rec]
  (cond
    (nil? sel-rec)
    (do
      (model/set-ui-state! el :causality-layout nil)
      (set! (.-innerHTML causality-el)
            (str "<div class='causality-empty'>"
                 (escape-html (model/causality-empty-message))
                 "</div>")))

    :else
    (let [root   (model/causality-root recs sel-rec)
          tree   (model/causality-tree recs root)
          stats  (model/tree-size-stats tree)]
      (cond
        (:capped? tree)
        (do
          (model/set-ui-state! el :causality-layout nil)
          (set! (.-innerHTML causality-el)
                (str "<div class='causality-empty'>"
                     (escape-html (model/causality-over-cap-message stats))
                     "</div>")))

        :else
        (let [layout (model/layout-tree tree)
              leaf?  (= 1 (:node-count stats))]
          (model/set-ui-state! el :causality-layout layout)
          (set! (.-innerHTML causality-el)
                (str (when leaf? (leaf-hint-html))
                     (causality-svg-html layout (.-id sel-rec)))))))))

;; Forward decl — get-selected is defined in the "View / selection
;; helpers" section further down. apply-causality-fit! reads the
;; current selection to drive the scroll, but the causality renderer
;; itself takes the selected record as a parameter, so only the
;; post-render fit step needs the helper.
(declare get-selected)

(defn- apply-causality-fit!
  "If the causality pane is on AND the needs-fit flag is set AND a
   layout is cached AND a record is selected, scroll the pane so the
   selected node sits at the centre of the viewport. Clears the flag
   on a successful fit. Safe to call when the pane is hidden (the
   container has zero clientWidth/Height — fit-to-view-scroll then
   returns {0, 0} which is a no-op against scroll positions already
   at the start)."
  [^js el]
  (when (model/ui-state el :causality-needs-fit)
    (let [^js container (gobj/get el model/k-causality-el)
          layout        (model/ui-state el :causality-layout)
          sel-id        (get-selected el)
          node          (when layout (model/find-laid-node layout sel-id))
          vw            (some-> container .-clientWidth)
          vh            (some-> container .-clientHeight)]
      (when (and container layout node
                 (number? vw) (number? vh)
                 (pos? vw)    (pos? vh))
        (let [{:keys [width height]} layout
              {sl :scroll-left st :scroll-top}
              (model/fit-to-view-scroll (:cx node) (:cy node) vw vh width height)]
          (set! (.-scrollLeft container) sl)
          (set! (.-scrollTop  container) st)
          (model/set-ui-state! el :causality-needs-fit false))))))

;; ---------------------------------------------------------------------------
;; Tag dropdown — populated at runtime from observed tags
;; ---------------------------------------------------------------------------

(defn- existing-tag-options-set
  "Return a CLJS set of every option value already present in the tag
   select (excluding the static 'all' default)."
  [^js select-el]
  (let [^js opts (.querySelectorAll select-el "option")
        n       (.-length opts)]
    (loop [i 0 acc (transient #{})]
      (if (>= i n)
        (persistent! acc)
        (let [^js opt (aget opts i)
              v       (.-value opt)]
          (recur (inc i)
                 (if (= v "all") acc (conj! acc v))))))))

(defn- sync-tag-options!
  "Append one <option> per newly-observed tag to the tag-filter
   <x-select>. Append-only so the user's current selection survives
   re-renders, and so x-select's slotchange-driven option clone path
   picks up additions without us having to rebuild its inner native
   <select>. No-op when no new tags have appeared since the last call."
  [^js select-el]
  (let [observed (recorder/observed-tags)
        existing (existing-tag-options-set select-el)
        missing  (remove existing observed)]
    (when (seq missing)
      (let [html (apply str (map tag-option-html missing))]
        (.insertAdjacentHTML select-el "beforeend" html)))))

;; ---------------------------------------------------------------------------
;; Auto-switch to a fresh import — PR 15 (Phase 7)
;;
;; When a new import lands AND the dock is on :live AND the live buffer
;; is empty, switch the view to that import. Covers two callsites:
;;   - viewer.html: live is always empty, so dropping/loading a trace
;;     puts the user on it immediately.
;;   - regular dock: drag-drop or console import on an idle page does
;;     the same thing, saving a chip click.
;;
;; Stays off when the user has live records. We compare imports.length
;; against a per-host counter so the switch only fires on *transitions*
;; (a new import arriving), not on every render after the user clicks
;; back to :live.
;; ---------------------------------------------------------------------------

(defn- maybe-auto-switch-import!
  "If a new import has appeared since the last subscriber tick and the
   dock is sitting on an empty live view, switch view to the newest
   import. Otherwise, just keep the import counter in sync so a later
   addition is detected as a transition. No-op once the user has any
   live records or has manually navigated off :live."
  [^js el]
  (let [view       (gobj/get el model/k-view)
        ^js imps   (recorder/imports)
        cur-count  (.-length imps)
        prev       (or (model/ui-state el :auto-switch-import) 0)]
    (cond
      ;; New import landed. Consider switching.
      (> cur-count prev)
      (do
        (model/set-ui-state! el :auto-switch-import cur-count)
        (when (and (model/live-view? view)
                   (zero? (.-length ^js (recorder/records))))
          (let [^js latest (aget imps (dec cur-count))
                id         (.-id latest)]
            (gobj/set el model/k-view [:import id]))))

      ;; Imports shrank (the user dropped one while we weren't
      ;; mounted, or some test path called remove-import!). Rebaseline
      ;; the counter so a later addition still registers as a
      ;; new-import transition.
      (< cur-count prev)
      (model/set-ui-state! el :auto-switch-import cur-count)

      ;; cur-count == prev: nothing changed, nothing to do.
      :else nil)))

;; ---------------------------------------------------------------------------
;; Pause button + detail pane updates
;; ---------------------------------------------------------------------------

(defn- refresh-pause-btn!
  [^js btn]
  (let [paused? (recorder/paused?)]
    (set! (.-textContent btn) (if paused? "Resume" "Pause"))
    ;; x-button reflects the `pressed` boolean property to the
    ;; `pressed` attribute and reads either as the source of truth.
    ;; Use the property setter (gobj/set won't reach the accessor).
    (set! (.-pressed btn) paused?)))

(defn- refresh-record-btn!
  "Pause/Resume-style toggle for the Record button. While a session is
   active the label reads 'Stop' and the button shows pressed."
  [^js btn]
  (let [active? (some? (recorder/active-session-id))]
    (set! (.-textContent btn) (if active? "Stop" "Record"))
    (set! (.-pressed btn) active?)))

;; ---------------------------------------------------------------------------
;; Session strip rendering
;; ---------------------------------------------------------------------------

(defn- session-chip-html
  "One chip per record source. `chip-key` is the wire token the click
   delegate parses: `live`, a stringified session id, or
   `import:N` for an imported trace. `import?` toggles the styling
   modifier and the inline close (×) button — clicking the close
   button drops the import without changing the view."
  [{:keys [chip-key active? recording? import? label import-id]}]
  (str "<button class='session-chip"
       (when active? " active")
       (when import? " import")
       "' type='button' data-x-th-session='" (escape-html chip-key) "'>"
       (when recording? "<span class='live-dot' aria-hidden='true'></span>")
       (escape-html label)
       (when import?
         (str "<span class='chip-close' role='button' "
              "aria-label='Remove import' "
              "data-x-th-import-close='" import-id "'>×</span>"))
       "</button>"))

(defn- session-strip-html
  "Build the strip: a Live chip, one chip per recorded session, and one
   per imported trace. Returns an empty string when no sessions and no
   imports exist (the strip is then hidden)."
  [^js sessions ^js imports ^js view active-recording-id]
  (let [live      {:chip-key   "live"
                   :active?    (model/live-view? view)
                   :recording? false
                   :import?    false
                   :label      "Live"}
        session-id (when (model/session-view? view) (model/view-id view))
        import-id  (when (model/import-view? view)  (model/view-id view))
        sess-chips (mapv (fn [^js s]
                           (let [id (.-id s)]
                             {:chip-key   (str id)
                              :active?    (= id session-id)
                              :recording? (= id active-recording-id)
                              :import?    false
                              :label      (str (.-label s)
                                               " · " (.-recordCount s))}))
                         (array-seq sessions))
        imp-chips  (mapv (fn [^js imp]
                           (let [id (.-id imp)]
                             {:chip-key   (str "import:" id)
                              :active?    (= id import-id)
                              :recording? false
                              :import?    true
                              :import-id  id
                              :label      (str (.-label imp)
                                               " · " (.-recordCount imp))}))
                         (array-seq imports))]
    (apply str (map session-chip-html
                    (concat [live] sess-chips imp-chips)))))

(defn- refresh-sessions-strip!
  [^js sessions-el ^js sessions ^js imports ^js view active-recording-id]
  (set! (.-innerHTML sessions-el)
        (session-strip-html sessions imports view active-recording-id))
  (if (and (zero? (.-length sessions)) (zero? (.-length imports)))
    (.setAttribute sessions-el "hidden" "")
    (.removeAttribute sessions-el "hidden")))

(defn- record-link-summary
  "Compact one-line text for a cause/effect link button: '#id tag · type · preview'.
   payload-preview already handles the per-type rendering."
  [^js r]
  (let [preview (model/payload-preview r)]
    (str "#" (.-id r) " " (.-tag r) " · " (.-type r)
         (when (and preview (not (str/blank? preview)))
           (str " · " preview)))))

(defn- detail-link-html
  "One <x-button> for the cause/effect rows. data-x-th-link-id is read
   by on-detail-link-click! to set selection. The host element bubbles
   native click; the dock's internal-host boundary suppresses x-button's
   own press/hover events from the trace."
  [^js r]
  (let [text (record-link-summary r)]
    (str "<x-button class='detail-link' size='sm' variant='ghost' "
         "data-x-th-link-id='" (.-id r) "' "
         "title='" (escape-html text) "'>"
         (escape-html text)
         "</x-button>")))

(defn- cause-section-html
  [^js cause-rec]
  (when cause-rec
    (str "<div class='detail-section' data-x-th-detail-cause>"
         "<div class='detail-label'>Caused by</div>"
         (detail-link-html cause-rec)
         "</div>")))

(defn- effects-section-html
  [{:keys [total records]}]
  (when (pos? total)
    (let [shown (count records)
          label (if (= shown total)
                  (str "Effects (" total ")")
                  (str "Effects (" shown " of " total ")"))]
      (str "<div class='detail-section' data-x-th-detail-effects>"
           "<div class='detail-label'>" label "</div>"
           (apply str (map detail-link-html records))
           "</div>"))))

(defn- detail-html
  "Render the full detail pane for a selected record: JSON dump, the
   cause link (if any), and the effects list (if any)."
  [^js record ^js all-records]
  (let [json-str (js/JSON.stringify record nil 2)
        cause    (model/cause-of   all-records record)
        effects  (model/effects-of all-records record)]
    (str "<pre class='detail-json'>" (escape-html json-str) "</pre>"
         (cause-section-html   cause)
         (effects-section-html effects))))

(defn- refresh-detail!
  [^js detail-el ^js splitter-el ^js all-records ^js record]
  (if record
    (do
      (set! (.-innerHTML detail-el) (detail-html record all-records))
      (.removeAttribute detail-el   "hidden")
      (.removeAttribute splitter-el "hidden"))
    (do
      (.setAttribute detail-el   "hidden" "")
      (.setAttribute splitter-el "hidden" ""))))

;; ---------------------------------------------------------------------------
;; Search haystack cache
;;
;; Records are immutable per the recorder's contract, so a WeakMap keyed
;; by record reference is the natural place for memoised search
;; haystacks: JSON.stringify is paid once per record, the entry is
;; collected automatically when the record is evicted from the ring
;; buffer, and the model namespace stays pure (it no longer mutates
;; records via gobj/set to cache the haystack on them).
;; ---------------------------------------------------------------------------

(defonce ^:private haystack-cache (js/WeakMap.))

(defn- cached-haystack [^js r]
  (or (.get haystack-cache r)
      (let [s (model/searchable-haystack r)]
        (.set haystack-cache r s)
        s)))

(defn- filter-recs
  "Dock wrapper around `model/filter-records` that injects the
   WeakMap-memoised haystack-fn so per-keystroke search across the
   whole ring buffer pays JSON.stringify once per record."
  [^js recs spec]
  (model/filter-records recs (assoc spec :haystack-fn cached-haystack)))

(defn- matches?
  "Dock wrapper around `model/record-matches?` with the cached
   haystack-fn injected."
  [^js r spec]
  (model/record-matches? r (assoc spec :haystack-fn cached-haystack)))

;; ---------------------------------------------------------------------------
;; View / selection helpers
;; ---------------------------------------------------------------------------

(defn- view-key
  "Stable string key for a view, so the per-view selection map can use
   it as a JS-object key. Live → 'live'; session → 'session:N';
   import → 'import:N'."
  [view]
  (cond
    (model/live-view? view)    "live"
    (model/session-view? view) (str "session:" (model/view-id view))
    (model/import-view? view)  (str "import:"  (model/view-id view))
    :else                      "live"))

(defn- get-selected
  "Read the selected record-id for the active view from k-view-selected."
  [^js el]
  (let [view (gobj/get el model/k-view)
        ^js m (gobj/get el model/k-view-selected)]
    (when m (gobj/get m (view-key view)))))

(defn- set-selected!
  "Write the selected record-id for the active view.

   When the dock is in :causality mode AND a non-nil id is being
   selected, mark :causality-needs-fit in ui-state so the next render scrolls
   the freshly-selected node to the centre of the causality pane.
   We do NOT set the flag on a nil-id clear (which `effective-
   selection!` triggers when a filter change drops the selection)
   because apply-causality-fit! has nothing to scroll to AND the
   stale flag would silently scroll a LATER unrelated render when
   the user reselects."
  [^js el id]
  (let [view  (gobj/get el model/k-view)
        ^js m (or (gobj/get el model/k-view-selected) (js-obj))
        k     (view-key view)]
    (if (nil? id)
      (gobj/remove m k)
      (gobj/set    m k id))
    (gobj/set el model/k-view-selected m)
    (when (and (some? id)
               (= :causality (gobj/get el model/k-dock-mode)))
      (model/set-ui-state! el :causality-needs-fit true))))

(defn- effective-selection!
  "Return the currently-selected record IFF it still passes the active
   filter; otherwise clear the dock's selected-id and return nil. Keeps
   the detail pane in sync with filter changes — selecting a dot, then
   filtering it out, drops the detail rather than leaving an orphan."
  [^js el ^js recs spec]
  (let [sel-id (get-selected el)
        rec    (model/find-record-by-id recs sel-id)]
    (cond
      (nil? rec)
      nil

      (matches? rec spec)
      rec

      :else
      (do (set-selected! el nil) nil))))

;; ---------------------------------------------------------------------------
;; Render orchestrator
;; ---------------------------------------------------------------------------

(defn- view-records
  "Pick the record source for the active view. Live → the full ring
   buffer; session N → the records inside that session's t-range;
   import N → the records inside the dropped/picked envelope. If a
   session or import has been deleted (or its id is stale) we fall
   back to live so the dock keeps rendering instead of going blank."
  [view]
  (cond
    (model/live-view? view)
    (recorder/records)

    (model/session-view? view)
    (let [recs (recorder/session-records (model/view-id view))]
      (if (zero? (.-length recs)) (recorder/records) recs))

    (model/import-view? view)
    (let [recs (recorder/import-records (model/view-id view))]
      (if (zero? (.-length recs)) (recorder/records) recs))

    :else
    (recorder/records)))

(defn- apply-pane-visibility!
  "Toggle the hidden attribute on the timeline / causality panes so
   only the active dock-mode is visible. Both panes share the
   flex: 1 1 auto slot above the detail; only one renders at a time.

   Also mirrors the dock-mode onto a host CSS class so the filter row
   can conditionally hide controls that don't apply to the active
   pane (e.g. the axis-mode select, which only affects the timeline)."
  [^js host ^js timeline-el ^js causality-el dock-mode]
  (case dock-mode
    :causality
    (do (.setAttribute    timeline-el  "hidden" "")
        (.removeAttribute causality-el "hidden")
        (.add    (.-classList host) "causality-mode"))
    (do (.removeAttribute timeline-el  "hidden")
        (.setAttribute    causality-el "hidden" "")
        (.remove (.-classList host) "causality-mode"))))

(defn- render!
  "Repaint timeline, count, hint, detail pane, pause-button, record-button
   and session strip from current recorder + filter + view + selection state."
  [^js el]
  (let [^js timeline-el  (gobj/get el model/k-timeline-el)
        ^js causality-el (gobj/get el model/k-causality-el)
        ^js lanes-el     (gobj/get el model/k-lanes-el)
        ^js svg-pane-el  (gobj/get el model/k-svg-pane-el)
        ^js count-el     (gobj/get el model/k-count-el)
        ^js detail-el    (gobj/get el model/k-detail-el)
        ^js splitter-el  (gobj/get el model/k-splitter-el)
        ^js hint-el      (gobj/get el model/k-hint-el)
        ^js pause-btn    (gobj/get el model/k-pause-btn)
        ^js record-btn   (gobj/get el model/k-record-btn)
        ^js sessions-el  (gobj/get el model/k-sessions-el)
        ^js tag-sel-el   (gobj/get el model/k-tag-select-el)
        spec             (gobj/get el model/k-filter)
        view             (gobj/get el model/k-view)
        ^js recs         (view-records view)
        filtered         (filter-recs recs spec)
        cnt-filtered     (count filtered)
        cnt-total        (.-length recs)
        bounds           (model/time-bounds filtered)
        lanes            (model/active-lanes filtered)
        sel-rec          (effective-selection! el recs spec)
        ^js sessions     (recorder/sessions)
        ^js imports      (recorder/imports)
        active-rec-id    (recorder/active-session-id)
        axis-mode        (or (gobj/get el model/k-axis-mode)
                             model/default-axis-mode)
        dock-mode        (or (gobj/get el model/k-dock-mode)
                             model/default-dock-mode)]
    (sync-tag-options!       tag-sel-el)
    (apply-pane-visibility! el timeline-el causality-el dock-mode)
    (case dock-mode
      :causality (render-causality! el causality-el recs sel-rec)
      (render-timeline! lanes-el svg-pane-el axis-mode filtered lanes bounds
                        sel-rec (:tag spec)))
    (set! (.-textContent count-el) (str cnt-filtered))
    (set! (.-textContent hint-el)
          (model/timeline-hint cnt-filtered cnt-total bounds (count lanes)))
    (refresh-detail!         detail-el splitter-el recs sel-rec)
    (refresh-pause-btn!      pause-btn)
    (refresh-record-btn!     record-btn)
    (refresh-sessions-strip! sessions-el sessions imports view active-rec-id)
    ;; Fit-to-view runs LAST so the causality pane has its final
    ;; visibility / SVG contents in place when we read its
    ;; clientWidth/Height. apply-causality-fit! is a no-op unless the
    ;; pane is visible, the needs-fit flag is set, and a selection
    ;; resolves to a laid-out node.
    (apply-causality-fit! el)))

;; ---------------------------------------------------------------------------
;; Click + tooltip + filter handlers
;; ---------------------------------------------------------------------------

(defn- circle-target?
  [^js target]
  (and target (= "circle" (.. target -tagName toLowerCase))))

(defn- density-bin-target?
  "True for the heatmap bin <rect>s added in PR 18. A density-bin rect
   carries data-x-th-bin-record-id (the bin's first record), so click
   + hover routing can read a single id from either a dot or a bin."
  [^js target]
  (and target
       (= "rect" (.. target -tagName toLowerCase))
       (some? (.getAttribute target "data-x-th-bin-record-id"))))

(defn- record-target?
  "Either a dot or a density-bin rect — both resolve to a record id."
  [^js target]
  (or (circle-target?      target)
      (density-bin-target? target)))

(defn- read-dot-id
  [^js circle]
  (when-let [s (.getAttribute circle "data-x-th-id")]
    (js/parseInt s 10)))

(defn- read-record-id-from-target
  "Extract the record id a target represents:
     - circle.dot              → data-x-th-id
     - rect.density-bin        → data-x-th-bin-record-id (the bin's first record)
   Returns nil for any other target."
  [^js target]
  (cond
    (circle-target? target)
    (read-dot-id target)

    (density-bin-target? target)
    (when-let [s (.getAttribute target "data-x-th-bin-record-id")]
      (js/parseInt s 10))))

(defn- on-dot-click!
  "Click delegate for record-targets: a dot in a sparse lane, OR a
   heatmap bin in a dense lane. Selecting a bin selects its first
   record (the chronologically-earliest), which keeps the existing
   detail-pane + arrow-step flow working unchanged — a bin is just
   an alternate rendering of the same records."
  [^js el ^js target]
  (when (record-target? target)
    (let [id     (read-record-id-from-target target)
          curr   (get-selected el)
          new-id (if (= id curr) nil id)]
      (set-selected! el new-id)
      (render! el))))

(def ^:private collapse-icon-open  "«")
(def ^:private collapse-icon-closed "»")

(defn- apply-collapsed-state!
  "Apply the collapsed boolean to the host element: toggle the
   `collapsed` class so the :host(.collapsed) CSS rules apply, and
   swap the toggle button's icon so the user can tell which way it'll
   move next. Pure visual — does NOT touch any other dock state."
  [^js el collapsed?]
  (if collapsed?
    (.add    (.-classList el) "collapsed")
    (.remove (.-classList el) "collapsed"))
  (when-let [^js btn (gobj/get el model/k-collapse-btn)]
    (set! (.-textContent btn)
          (if collapsed? collapse-icon-closed collapse-icon-open))
    ;; aria-expanded so screen readers track the disclosure state.
    (.setAttribute btn "aria-expanded" (if collapsed? "false" "true"))))

(defn- toggle-collapsed!
  "Flip the dock's collapsed state and apply the visual class. Used
   by both the click handler and the document-level keyboard
   shortcut so the two entry points stay in sync."
  [^js el]
  (let [next? (not (boolean (gobj/get el model/k-collapsed)))]
    (gobj/set el model/k-collapsed next?)
    (apply-collapsed-state! el next?)))

(defn- on-action-click!
  [^js el ^js target]
  (when-let [^js btn (.closest target "[data-x-th-action]")]
    (case (.getAttribute btn "data-x-th-action")
      "pause"  (if (recorder/paused?) (recorder/resume!) (recorder/pause!))
      "record" (if (some? (recorder/active-session-id))
                 (recorder/stop-session!)
                 (recorder/start-session!))
      ;; Clear empties the buffer AND drops sessions; reset per-view
      ;; selections so stale ids don't linger, AND switch back to the
      ;; Live view so the session strip's active chip doesn't dangle
      ;; on a now-deleted session id.
      "clear"  (do (gobj/set el model/k-view-selected (js-obj))
                   (gobj/set el model/k-view :live)
                   (recorder/clear!))
      ;; Export is read-only — produce a .trace.json download from the
      ;; current recorder snapshot. Does not change view, selection,
      ;; filter, or recording state.
      "export" (recorder/download-trace!)
      ;; Import delegates to the hidden <input type='file'>. Drag-drop
      ;; is the other entry point; both routes converge on
      ;; import-file! below.
      "import" (when-let [^js input (gobj/get el model/k-import-input)]
                 (set! (.-value input) "")  ; allow re-picking the same file
                 (.click input))
      ;; Collapse toggle — flips the dock between full-width and a
      ;; thin strip so components behind it become reachable. The
      ;; document-level keyboard shortcut (Ctrl/Cmd + \) also routes
      ;; through `toggle-collapsed!`.
      "collapse" (toggle-collapsed! el)
      nil)))

(defn- parse-chip-key
  "Map a chip's data-x-th-session value to a dock view value. 'live'
   → :live; 'import:N' → [:import N]; everything else is parsed as a
   numeric session id and yields [:session N]."
  [k]
  (cond
    (= k "live")
    :live

    (str/starts-with? k "import:")
    [:import (js/parseInt (subs k (count "import:")) 10)]

    :else
    [:session (js/parseInt k 10)]))

(defn- on-session-chip-click!
  "Switch the dock's view when the user clicks a chip in the session
   strip. data-x-th-session is 'live', a numeric session id, or
   'import:N' for an imported trace. Clicking the inline × close
   button on an import chip is handled separately (see
   `on-import-close-click!`) — we short-circuit when the actual
   click target is the close button so the parent-chip click doesn't
   also fire."
  [^js el ^js target]
  (when-not (.closest target "[data-x-th-import-close]")
    (when-let [^js btn (.closest target "[data-x-th-session]")]
      (let [k        (.getAttribute btn "data-x-th-session")
            new-view (parse-chip-key k)]
        (gobj/set el model/k-view new-view)
        (render! el)))))

(defn- on-import-close-click!
  "Drop the import whose × close button was clicked. If the dock is
   currently viewing that import, switch back to :live before removing
   it so render! doesn't briefly render a dangling [:import N] view."
  [^js el ^js target]
  (when-let [^js close-btn (.closest target "[data-x-th-import-close]")]
    (let [id   (js/parseInt (.getAttribute close-btn "data-x-th-import-close") 10)
          view (gobj/get el model/k-view)]
      (when (and (model/import-view? view)
                 (= id (model/view-id view)))
        (gobj/set el model/k-view :live))
      (recorder/remove-import! id))))

(defn- on-lane-label-click!
  "Toggle the tag filter for the clicked lane. Clicking the same lane label
   again clears the filter (back to All tags). The tag-select dropdown is
   kept in sync so its visible value matches the active filter."
  [^js el ^js target]
  (when-let [^js label (.closest target "[data-x-th-lane-tag]")]
    (let [lane-tag (.getAttribute label "data-x-th-lane-tag")
          spec     (gobj/get el model/k-filter)
          curr-tag (:tag spec)
          new-tag  (when-not (= lane-tag curr-tag) lane-tag)
          new-spec (assoc spec :tag new-tag)]
      (gobj/set el model/k-filter new-spec)
      (when-let [^js sel (gobj/get el model/k-tag-select-el)]
        (set! (.-value sel) (or new-tag "all")))
      (render! el))))

(defn- on-detail-link-click!
  "Click on a cause/effect link in the detail pane: jump selection to
   that record. The click is also bubbled through .closest so wrapping
   markup inside the button (e.g. <span class='cid'>) routes correctly."
  [^js el ^js target]
  (when-let [^js btn (.closest target "[data-x-th-link-id]")]
    (when-let [s (.getAttribute btn "data-x-th-link-id")]
      (let [id (js/parseInt s 10)]
        (when-not (js/isNaN id)
          (set-selected! el id)
          (render! el))))))

(defn- handle-click!
  [^js el ^js e]
  (let [^js target (.-target e)]
    (on-action-click!        el target)
    (on-import-close-click!  el target)
    (on-session-chip-click!  el target)
    (on-lane-label-click!    el target)
    (on-detail-link-click!   el target)
    (on-dot-click!           el target)))

(defn- handle-tag-change!
  "Apply a new tag filter from a select-change detail value. The 'all'
   sentinel maps to nil :tag (matches every record)."
  [^js el value]
  (let [spec     (gobj/get el model/k-filter)
        new-spec (assoc spec :tag (when-not (= value "all") value))]
    (gobj/set el model/k-filter new-spec)
    (render! el)))

(defn- handle-axis-change!
  "Switch the dock's axis layout. `value` is either 'order' (the
   default) or 'time' as emitted by the <x-select data-x-th-axis>.
   Unknown values fall back to the default so a malformed external
   write doesn't put the dock into a broken state."
  [^js el value]
  (let [mode (case value
               "order" :order
               "time"  :time
               model/default-axis-mode)]
    (gobj/set el model/k-axis-mode mode)
    (render! el)))

(defn- handle-mode-change!
  "Switch the dock's view between :timeline and :causality. Toggling
   TO causality always marks needs-fit so the next render scrolls
   the selected node into the centre of the new pane (the user's
   intent on switching is 'show me the chain for what I've got
   selected'). Toggling back to :timeline clears the flag so a
   later return to causality doesn't fight a stale request."
  [^js el value]
  (let [mode (case value
               "timeline"  :timeline
               "causality" :causality
               model/default-dock-mode)]
    (gobj/set el model/k-dock-mode mode)
    (if (= :causality mode)
      (model/set-ui-state! el :causality-needs-fit true)
      (model/set-ui-state! el :causality-needs-fit false))
    (render! el)))

(defn- handle-cat-change!
  "Toggle a category in the filter from an x-checkbox-change detail
   payload. `cat` is the data-x-th-cat string (e.g. \"events\");
   `checked?` is the new boolean."
  [^js el cat checked?]
  (let [spec     (gobj/get el model/k-filter)
        cats     (or (:categories spec) (set model/all-categories))
        cat-kw   (keyword cat)
        new-cats (if checked? (conj cats cat-kw) (disj cats cat-kw))
        new-spec (assoc spec :categories new-cats)]
    (gobj/set el model/k-filter new-spec)
    (render! el)))

(defn- handle-select-change!
  "x-select fires `select-change` with detail = {value, label}. Three
   selects live in the dock (tag filter + view mode + axis mode); the
   data attribute disambiguates."
  [^js el ^js e]
  (let [^js target (.-target e)
        value      (.. e -detail -value)]
    (cond
      (some? (.getAttribute target "data-x-th-tag"))
      (handle-tag-change! el value)

      (some? (.getAttribute target "data-x-th-mode"))
      (handle-mode-change! el value)

      (some? (.getAttribute target "data-x-th-axis"))
      (handle-axis-change! el value))))

(defn- handle-search-input!
  "x-search-field fires `x-search-field-input` on every keystroke with
   detail = {name, value}. We lowercase the value once here so
   model/matches-search? compares cheap lowercase-vs-lowercase without
   re-lowercasing each haystack call. Empty value disables the filter.
   Gated on data-x-th-search so a future second x-search-field in the
   dock cannot stomp this spec through the same shadow delegate."
  [^js el ^js e]
  (let [^js target (.-target e)]
    (when (and target (.hasAttribute target "data-x-th-search"))
      (let [raw      (or (.. e -detail -value) "")
            q        (.toLowerCase raw)
            spec     (gobj/get el model/k-filter)
            new-spec (assoc spec :search (when-not (= "" q) q))]
        (gobj/set el model/k-filter new-spec)
        (render! el)))))

(defn- handle-checkbox-change!
  "x-checkbox fires `x-checkbox-change` with detail = {value, checked}."
  [^js el ^js e]
  (let [^js target (.-target e)
        cat        (.getAttribute target "data-x-th-cat")]
    (when (some? cat)
      (handle-cat-change! el cat (.. e -detail -checked)))))

(def ^:private tooltip-cursor-offset 12)
(def ^:private tooltip-max-width    280)  ; mirrors CSS max-width

(defn- place-tooltip!
  "Position-only logic: write `text` into the tooltip and place it
   near the cursor. Anchored to the right of the cursor by default;
   flips to the left when staying right would push the tooltip past
   the timeline's right edge. Uses the CSS max-width as the worst-
   case tooltip width to avoid a measure pass. Split from
   show-tooltip! / show-bin-tooltip! so each caller writes its own
   text exactly once (avoids a wasted double-write per hover)."
  [^js tooltip-el ^js timeline-el ^js e text]
  (set! (.-textContent tooltip-el) text)
  (.removeAttribute tooltip-el "hidden")
  (let [tl-rect     (.getBoundingClientRect timeline-el)
        cursor-x    (- (.-clientX e) (.-left tl-rect))
        cursor-y    (- (.-clientY e) (.-top  tl-rect))
        scroll-top  (.-scrollTop  timeline-el)
        scroll-left (.-scrollLeft timeline-el)
        tl-width    (.-clientWidth timeline-el)
        flip?       (> (+ cursor-x tooltip-cursor-offset tooltip-max-width)
                       tl-width)
        left-px     (max 0 (if flip?
                             (- cursor-x tooltip-cursor-offset tooltip-max-width)
                             (+ cursor-x tooltip-cursor-offset)))]
    (set! (.. tooltip-el -style -left)
          (str (+ left-px scroll-left) "px"))
    (set! (.. tooltip-el -style -top)
          (str (+ cursor-y scroll-top tooltip-cursor-offset) "px"))))

(defn- show-tooltip!
  "Show the single-record dot tooltip near the cursor."
  [^js tooltip-el ^js timeline-el ^js e ^js record]
  (place-tooltip! tooltip-el timeline-el e (model/tooltip-text record)))

(defn- hide-tooltip!
  [^js tooltip-el]
  (.setAttribute tooltip-el "hidden" ""))

(defn- show-bin-tooltip!
  "Show the heatmap-bin tooltip near the cursor. Uses
   density-bin-tooltip-text built from the bin's first record + the
   count + dominant category data attributes."
  [^js tooltip-el ^js timeline-el ^js e ^js first-record bin-count dominant-cat]
  (place-tooltip! tooltip-el timeline-el e
                  (model/density-bin-tooltip-text first-record bin-count dominant-cat)))

(defn- read-bin-meta
  "Read the bin metadata the renderer stamped onto the rect: the
   first record's id (resolved via the recorder), the bin's count,
   and the bin's dominant-category keyword. Returns nil for a
   malformed rect (missing attributes) so the caller can short-
   circuit cleanly."
  [^js rect]
  (let [id-s    (.getAttribute rect "data-x-th-bin-record-id")
        count-s (.getAttribute rect "data-x-th-bin-count")
        cat-s   (.getAttribute rect "data-x-th-bin-cat")]
    (when (and id-s count-s cat-s)
      {:record-id   (js/parseInt id-s 10)
       :bin-count   (js/parseInt count-s 10)
       :dominant-cat (keyword cat-s)})))

(defn- handle-pointermove!
  [^js el ^js e]
  (let [^js target   (.-target e)
        ^js tooltip  (gobj/get el model/k-tooltip-el)
        ^js timeline (gobj/get el model/k-timeline-el)
        ;; Resolve hovered records from the active view's source
        ;; (live / session / import). Reading the live ring buffer
        ;; here would lose the tooltip whenever the dock is viewing
        ;; an import or session whose records aren't in :live.
        view         (gobj/get el model/k-view)
        ^js recs     (view-records view)]
    (cond
      (circle-target? target)
      (let [id (read-dot-id target)
            r  (model/find-record-by-id recs id)]
        (when r
          (show-tooltip! tooltip timeline e r)))

      (density-bin-target? target)
      (when-let [{:keys [record-id bin-count dominant-cat]} (read-bin-meta target)]
        (when-let [r0 (model/find-record-by-id recs record-id)]
          (show-bin-tooltip! tooltip timeline e r0 bin-count dominant-cat)))

      :else
      (hide-tooltip! tooltip))))

(defn- handle-pointerleave!
  [^js el _e]
  (hide-tooltip! ^js (gobj/get el model/k-tooltip-el)))

;; ---------------------------------------------------------------------------
;; Scrubber — pointer-drag on the SVG to step selection by nearest record
;; ---------------------------------------------------------------------------

(defn- record-at-cursor
  "Pick the filtered record closest to cursor x under the active
   axis mode. :order maps x to a list index and picks `filtered[i]`;
   :time picks the record with the nearest `t` value. Returns nil
   for empty input so the scrubber can no-op cleanly."
  [axis-mode filtered bounds plot-w x]
  (case axis-mode
    :order
    (when-let [i (model/index-from-x x (count filtered) plot-w)]
      (nth filtered i nil))

    :time
    (model/nearest-record filtered (model/time-from-x x bounds plot-w))))

(defn- select-nearest!
  "Apply a precomputed scrub context to a raw cursor x. Used by the
   scrub-start kick-off and by every pointermove that follows so the
   filter / records / bounds / plot-width / axis-mode snapshot is
   taken once at gesture start (a drag is a moment in time — capturing
   newly arriving records mid-drag would be more confusing than
   helpful)."
  [^js el axis-mode filtered bounds plot-w x]
  (when-let [^js nearest (record-at-cursor axis-mode filtered bounds plot-w x)]
    (set-selected! el (.-id nearest))
    (render! el)))

(defn- start-scrub!
  "Begin a pointer-driven scrub. The pointer is captured on the svg-pane,
   so the gesture survives the cursor leaving the SVG bounds. Filter
   snapshot, bounds, plot width, and axis-mode are computed once and
   reused for every pointermove until pointerup / pointercancel.

   Reads records from the active view (live / session / import) so
   scrubbing inside a captured-or-imported trace lands on records
   that actually exist there — not the live buffer."
  [^js el ^js svg-pane ^js e]
  (.preventDefault e)
  (.setPointerCapture svg-pane (.-pointerId e))
  (let [pane-rect (.getBoundingClientRect svg-pane)
        plot-w    (max svg-min-w (.-clientWidth svg-pane))
        spec      (gobj/get el model/k-filter)
        axis-mode (or (gobj/get el model/k-axis-mode) model/default-axis-mode)
        view      (gobj/get el model/k-view)
        ^js recs  (view-records view)
        filtered  (filter-recs recs spec)
        bounds    (model/time-bounds filtered)
        x-of      (fn [^js me] (- (.-clientX me) (.-left pane-rect)))]
    (select-nearest! el axis-mode filtered bounds plot-w (x-of e))
    (let [on-move (fn [^js me]
                    (select-nearest! el axis-mode filtered bounds plot-w (x-of me)))
          on-end  (fn end-fn [_]
                    (.removeEventListener svg-pane "pointermove"   on-move)
                    (.removeEventListener svg-pane "pointerup"     end-fn)
                    (.removeEventListener svg-pane "pointercancel" end-fn))]
      (.addEventListener svg-pane "pointermove"   on-move)
      (.addEventListener svg-pane "pointerup"     on-end)
      (.addEventListener svg-pane "pointercancel" on-end))))

(defn- handle-svg-pointerdown!
  [^js el ^js e]
  (let [^js svg-pane (gobj/get el model/k-svg-pane-el)]
    (start-scrub! el svg-pane e)))

;; ---------------------------------------------------------------------------
;; Keyboard navigation — Left/Right arrows step the selection
;; ---------------------------------------------------------------------------

(defn- step-selection!
  [^js el dir]
  (let [spec       (gobj/get el model/k-filter)
        view       (gobj/get el model/k-view)
        ^js recs   (view-records view)
        filtered   (filter-recs recs spec)
        curr-id    (get-selected el)
        ^js next-r (model/step-record filtered curr-id dir)]
    (when next-r
      (set-selected! el (.-id next-r))
      (render! el))))

(def ^:private form-control-tags
  ;; Native form controls plus the BareDOM equivalents the dock now
  ;; uses (x-select for the tag filter, x-checkbox for category
  ;; filters). Arrow keys have meaningful semantics inside these
  ;; — option stepping in selects, focus traversal between
  ;; checkboxes — that we should not preempt.
  #{"input" "select" "textarea" "x-select" "x-checkbox"})

(defn- in-form-control?
  "Skip arrow handling when focus is somewhere arrow keys have native
   semantics. Buttons (native or x-button) are NOT included because
   arrows do nothing native on a button, and excluding them means the
   user can click Pause / Clear and immediately use arrows without
   re-clicking the timeline."
  [^js target]
  (or (boolean (some-> target .-isContentEditable))
      (let [tag (some-> target .-tagName .toLowerCase)]
        (contains? form-control-tags tag))))

(defn- handle-keydown!
  [^js el ^js e]
  (when-not (in-form-control? (.-target e))
    (case (.-key e)
      "ArrowRight" (do (.preventDefault e) (step-selection! el :next))
      "ArrowLeft"  (do (.preventDefault e) (step-selection! el :prev))
      nil)))

(defn- handle-document-keydown!
  "Listen on `document` (not just the shadow root) so the
   collapse-toggle keyboard shortcut works from anywhere on the page.

   The shortcut is `Ctrl + \\` (Linux/Windows) or `Cmd + \\` (Mac):
   uncommon enough not to collide with browser, OS, or app
   shortcuts, two-key combo so it doesn't trigger while typing
   regular content. Suppressed when target is a form control or
   contenteditable so users typing literal `\\` characters into an
   editor aren't interrupted."
  [^js el ^js e]
  (when (and (= "\\" (.-key e))
             (or (.-metaKey e) (.-ctrlKey e))
             (not (.-altKey e))
             (not (.-shiftKey e))
             (not (in-form-control? (.-target e))))
    (.preventDefault e)
    (toggle-collapsed! el)))

;; ---------------------------------------------------------------------------
;; Splitter — drag the divider above the detail pane to resize it
;; ---------------------------------------------------------------------------

(def ^:private min-detail-px 60)

(defn- start-resize!
  "Begin a pointer-driven resize of the detail pane. Drag the splitter UP
   to grow the detail; DOWN to shrink. Uses pointer capture so the gesture
   completes correctly even when the cursor leaves the splitter."
  [^js detail-el ^js splitter-el ^js e]
  (.preventDefault e)
  (.setPointerCapture splitter-el (.-pointerId e))
  (.add (.-classList splitter-el) "dragging")
  (let [start-y (.-clientY e)
        start-h (.-offsetHeight detail-el)
        on-move (fn [^js me]
                  (let [delta (- start-y (.-clientY me))
                        new-h (max min-detail-px (+ start-h delta))]
                    (set! (.. detail-el -style -height) (str new-h "px"))))
        on-end  (fn end-fn [_]
                  (.removeEventListener splitter-el "pointermove" on-move)
                  (.removeEventListener splitter-el "pointerup"   end-fn)
                  (.removeEventListener splitter-el "pointercancel" end-fn)
                  (.remove (.-classList splitter-el) "dragging"))]
    (.addEventListener splitter-el "pointermove"   on-move)
    (.addEventListener splitter-el "pointerup"     on-end)
    (.addEventListener splitter-el "pointercancel" on-end)))

;; ---------------------------------------------------------------------------
;; Import flow — file → JSON → envelope → recorder
;;
;; Two entry points converge here: the Import button (hidden <input
;; type='file'>) and drag-drop on the dock root. Both end up handing
;; us a File; we read it as text, pass the JSON to recorder/import-trace!,
;; and switch the dock view to the new import on success. Validation
;; failure surfaces a brief red message in the hint area (`.hint.error`)
;; that the next non-error render replaces.
;; ---------------------------------------------------------------------------

(def ^:private import-error-display-ms
  "How long a validation-error message stays visible in the hint area
   before being replaced by the next normal render. Long enough to
   read, short enough not to feel sticky."
  4000)

(defn- label-from-filename
  "Strip the `.trace.json` (or `.json`) extension off a filename so it
   reads cleanly on a chip. Falls back to the original string when no
   recognised extension is present, and to nil when the input is
   nil/blank."
  [name]
  (cond
    (or (nil? name) (= "" name))
    nil

    (str/ends-with? name ".trace.json")
    (subs name 0 (- (count name) (count ".trace.json")))

    (str/ends-with? name ".json")
    (subs name 0 (- (count name) (count ".json")))

    :else
    name))

(defn- show-import-error!
  "Push a transient error message into the hint area + add the
   `.error` class. A setTimeout clears the override after
   `import-error-display-ms` and re-renders, so the next regular
   hint string lands. Storing the timeout id on the host lets us
   replace it when a second error fires before the first cleared."
  [^js el msg]
  (let [^js hint-el (gobj/get el model/k-hint-el)
        prev-tok    (model/ui-state el :import-error)]
    (when (number? prev-tok)
      (js/clearTimeout prev-tok))
    (set! (.-textContent hint-el) msg)
    (.add (.-classList hint-el) "error")
    (let [tok (js/setTimeout
               (fn []
                 (.remove (.-classList hint-el) "error")
                 (model/set-ui-state! el :import-error nil)
                 (render! el))
               import-error-display-ms)]
      (model/set-ui-state! el :import-error tok))))

(defn- on-import-success!
  "Switch the dock view to the freshly-imported trace so the user
   immediately sees what they just loaded. Also clears any prior
   error so the regular hint message returns on the next render."
  [^js el import-id]
  (let [^js hint-el (gobj/get el model/k-hint-el)]
    (.remove (.-classList hint-el) "error"))
  (gobj/set el model/k-view [:import import-id])
  (render! el))

(defn- import-text!
  "Hand the file's text contents to recorder/import-trace!, branching
   on success vs. validation failure. The text path is the same for
   both Import-button picks and drag-drop drops."
  [^js el text label]
  (let [id (recorder/import-trace! text label)]
    (if (some? id)
      (on-import-success! el id)
      (show-import-error! el "Import failed — see console for details."))))

(defn- import-file!
  "Read a File object as text, then route through import-text!. Wraps
   the FileReader callback so caller doesn't need to know about it."
  [^js el ^js file]
  (let [^js reader (js/FileReader.)
        label      (label-from-filename (.-name file))]
    (set! (.-onload reader)
          (fn [^js _e]
            (import-text! el (.-result reader) label)))
    (set! (.-onerror reader)
          (fn [^js _e]
            (show-import-error! el "Could not read file.")))
    (.readAsText reader file)))

(defn- handle-import-input-change!
  "Change handler for the hidden <input type='file'>. Routes the first
   selected file through import-file!. Multiple-file selection is not
   supported in v1 — only the first file is loaded."
  [^js el ^js e]
  (let [^js input  (.-target e)
        ^js files  (.-files input)]
    (when (pos? (.-length files))
      (import-file! el (aget files 0)))))

(defn- on-drag-overlay-show!
  "Show the drop-overlay when a dragenter crosses into the dock.
   Counter-based (:drag-depth in ui-state) so passing over a child
   element doesn't flicker the overlay."
  [^js el]
  (let [^js overlay (gobj/get el model/k-drop-overlay)
        d           (or (model/ui-state el :drag-depth) 0)]
    (model/set-ui-state! el :drag-depth (inc d))
    (.removeAttribute overlay "hidden")))

(defn- on-drag-overlay-hide!
  "Hide the drop-overlay when the dragleave count returns to 0 or
   after a successful drop."
  [^js el]
  (let [^js overlay (gobj/get el model/k-drop-overlay)
        d           (max 0 (dec (or (model/ui-state el :drag-depth) 0)))]
    (model/set-ui-state! el :drag-depth d)
    (when (zero? d)
      (.setAttribute overlay "hidden" ""))))

(defn- drag-contains-file?
  "True when the event's dataTransfer carries at least one File item.
   Filters out drags of text selections, page elements, etc. — the
   overlay only appears for actual file drags."
  [^js e]
  (when-let [^js dt (.-dataTransfer e)]
    (let [^js types (.-types dt)]
      (.includes types "Files"))))

(defn- handle-dragenter!
  [^js el ^js e]
  (when (drag-contains-file? e)
    (.preventDefault e)
    (on-drag-overlay-show! el)))

(defn- handle-dragover!
  [^js _el ^js e]
  (when (drag-contains-file? e)
    (.preventDefault e)
    ;; Mark the drop effect explicitly so the cursor reads as 'copy'
    ;; rather than the OS default 'move'.
    (when-let [^js dt (.-dataTransfer e)]
      (set! (.-dropEffect dt) "copy"))))

(defn- handle-dragleave!
  [^js el ^js e]
  (when (drag-contains-file? e)
    (on-drag-overlay-hide! el)))

(defn- handle-drop!
  "On drop: hide the overlay and route the first dropped file through
   import-file!. preventDefault stops the browser's default behaviour
   of opening the file in a new tab when the user misses the dock."
  [^js el ^js e]
  (when (drag-contains-file? e)
    (.preventDefault e)
    ;; Reset the drag counter unconditionally — a drop fires WITHOUT a
    ;; final dragleave on every browser we care about.
    (model/set-ui-state! el :drag-depth 0)
    (let [^js overlay (gobj/get el model/k-drop-overlay)]
      (.setAttribute overlay "hidden" ""))
    (let [^js dt    (.-dataTransfer e)
          ^js files (when dt (.-files dt))]
      (when (and files (pos? (.-length files)))
        (import-file! el (aget files 0))))))

;; ---------------------------------------------------------------------------
;; Mount / unmount
;; ---------------------------------------------------------------------------

(defn- attach-skeleton!
  "Get-or-attach the dock's shadow root, injecting the skeleton + style
   only on the first attach.

   Sets innerHTML directly on the shadow root (rather than parsing
   into a detached temp div first) so any custom-element children —
   currently the Pause/Clear x-buttons — are upgraded INSIDE the
   dock's marked shadow tree. Their attributeChangedCallback fires
   immediately inside the boundary (PR-A), so initial size/variant
   attribute changes are correctly suppressed. dock-css is verified
   to contain no `</style>` substring so embedding it inside a
   <style> tag is safe.

   Idempotent on re-mount: if the host already carries a shadow root
   (because the element was previously connected, then disconnected,
   then re-appended), reuse it. `attachShadow` can only be called
   once per host — a fresh call would throw `InvalidStateError`."
  [^js el initial-categories]
  (let [^js existing (.-shadowRoot el)
        ^js shadow   (or existing (.attachShadow el #js {:mode "open"}))]
    (when (nil? existing)
      (set! (.-innerHTML shadow)
            (str "<style>" dock-css/dock-css "</style>"
                 (skeleton-html initial-categories))))
    shadow))

(defn- cache-refs!
  "Resolve and cache every shadow-DOM ref the dock reads in the hot path
   so render! doesn't repeat querySelector calls."
  [^js el ^js shadow]
  (gobj/set el model/k-shadow      shadow)
  (gobj/set el model/k-timeline-el (.querySelector shadow "[data-x-th-timeline]"))
  (gobj/set el model/k-lanes-el    (.querySelector shadow "[data-x-th-lanes]"))
  (gobj/set el model/k-svg-pane-el (.querySelector shadow "[data-x-th-svg-pane]"))
  (gobj/set el model/k-tooltip-el  (.querySelector shadow "[data-x-th-tooltip]"))
  (gobj/set el model/k-count-el    (.querySelector shadow "[data-x-th-count]"))
  (gobj/set el model/k-detail-el   (.querySelector shadow "[data-x-th-detail]"))
  (gobj/set el model/k-splitter-el (.querySelector shadow "[data-x-th-splitter]"))
  (gobj/set el model/k-hint-el       (.querySelector shadow "[data-x-th-hint]"))
  (gobj/set el model/k-tag-select-el   (.querySelector shadow "[data-x-th-tag]"))
  (gobj/set el model/k-causality-el    (.querySelector shadow "[data-x-th-causality]"))
  (gobj/set el model/k-pause-btn     (.querySelector shadow "[data-x-th-action='pause']"))
  (gobj/set el model/k-record-btn    (.querySelector shadow "[data-x-th-action='record']"))
  (gobj/set el model/k-sessions-el   (.querySelector shadow "[data-x-th-sessions]"))
  (gobj/set el model/k-import-input  (.querySelector shadow "[data-x-th-import-input]"))
  (gobj/set el model/k-drop-overlay  (.querySelector shadow "[data-x-th-drop-overlay]"))
  (gobj/set el model/k-collapse-btn  (.querySelector shadow "[data-x-th-action='collapse']")))

(defn- build-listener-tuples
  "Allocate the dock's static event listeners as #js [target event
   handler] triples. Handlers are closures over `el`; each tuple
   captures a stable fn reference so the matching .removeEventListener
   call in unmount! cancels the same listener that was added.

   The encoding is the `listener-spec` named pattern from CLAUDE.md
   — one place lists every static listener, and add/remove iterate
   the same source of truth so they cannot drift."
  [^js el ^js shadow ^js timeline-el ^js svg-pane-el ^js splitter-el ^js detail-el
   ^js import-input ^js causality-el]
  #js [#js [shadow       "click"             (fn [^js e] (handle-click!                 el e))]
       #js [shadow       "select-change"     (fn [^js e] (handle-select-change!         el e))]
       #js [shadow       "x-checkbox-change" (fn [^js e] (handle-checkbox-change!       el e))]
       ;; PR 16 — x-search-field fires `x-search-field-input` on every
       ;; keystroke with detail = {name, value}. The handler gates on
       ;; data-x-th-search so a future second search field in the dock
       ;; wouldn't pollute the search spec through this same shadow-
       ;; root listener.
       #js [shadow       "x-search-field-input" (fn [^js e] (handle-search-input!      el e))]
       #js [timeline-el  "pointermove"       (fn [^js e] (handle-pointermove!           el e))]
       #js [timeline-el  "pointerleave"      (fn [^js e] (handle-pointerleave!          el e))]
       ;; Pointerdown anywhere in the timeline focuses it so the
       ;; keyboard outline shows the user their starting point.
       #js [timeline-el  "pointerdown"       (fn [_e] (.focus timeline-el))]
       ;; Causality pane mirrors the timeline's focus pattern so arrow
       ;; keys step the selection when the user is browsing the tree.
       ;; Pointerdown bubbles from any SVG descendant (rect / text /
       ;; circle) so a single listener on the pane covers all node
       ;; clicks. The pane is focusable via tabindex='0'.
       #js [causality-el "pointerdown"       (fn [_e] (.focus causality-el))]
       ;; Keydown is bound on the SHADOW ROOT (not the timeline) so the
       ;; shadow receives keydown from any focused descendant — arrow
       ;; keys keep working even when focus shifts to a control after
       ;; a click. The handler ignores form-control targets so we
       ;; don't fight native behaviour.
       #js [shadow       "keydown"           (fn [^js e] (handle-keydown!               el e))]
       #js [svg-pane-el  "pointerdown"       (fn [^js e] (handle-svg-pointerdown!       el e))]
       #js [splitter-el  "pointerdown"       (fn [^js e] (start-resize! detail-el splitter-el e))]
       ;; PR 11 import flow: file-picker change + dock-root drag/drop.
       ;; Drag listeners attach to the shadow root so any descendant
       ;; participates in the drop target; the overlay's CSS
       ;; pointer-events:none keeps the drag counter accurate as the
       ;; cursor crosses children.
       #js [import-input "change"            (fn [^js e] (handle-import-input-change!   el e))]
       #js [shadow       "dragenter"         (fn [^js e] (handle-dragenter!             el e))]
       #js [shadow       "dragover"          (fn [^js e] (handle-dragover!              el e))]
       #js [shadow       "dragleave"         (fn [^js e] (handle-dragleave!             el e))]
       #js [shadow       "drop"              (fn [^js e] (handle-drop!                  el e))]
       ;; Collapse-toggle keyboard shortcut: bound on document so it
       ;; fires regardless of focus. Symmetric add/remove keeps it
       ;; from leaking across mount/unmount cycles.
       #js [js/document  "keydown"           (fn [^js e] (handle-document-keydown!      el e))]])

(defn- add-listeners!
  "Attach every entry in the listener-tuples JS array."
  [^js tuples]
  (dotimes [i (.-length tuples)]
    (let [^js t (aget tuples i)]
      (.addEventListener (aget t 0) (aget t 1) (aget t 2)))))

(defn- remove-listeners!
  "Detach every entry in a listener-tuples JS array. Symmetric with
   add-listeners! — same fn references go through to removeEventListener
   so each listener is actually cancelled."
  [^js tuples]
  (dotimes [i (.-length tuples)]
    (let [^js t (aget tuples i)]
      (.removeEventListener (aget t 0) (aget t 1) (aget t 2)))))

(defn- bind-listeners!
  "Build the tuples, stash them on the host so unmount! can remove
   them, and attach. Called on every (re)mount — never produces
   duplicate listeners because unmount! always cancels first."
  [^js el ^js shadow]
  (let [^js timeline-el   (gobj/get el model/k-timeline-el)
        ^js svg-pane-el   (gobj/get el model/k-svg-pane-el)
        ^js splitter-el   (gobj/get el model/k-splitter-el)
        ^js detail-el     (gobj/get el model/k-detail-el)
        ^js import-input  (gobj/get el model/k-import-input)
        ^js causality-el  (gobj/get el model/k-causality-el)
        tuples            (build-listener-tuples
                           el shadow timeline-el svg-pane-el splitter-el detail-el
                           import-input causality-el)]
    (gobj/set el model/k-listeners tuples)
    (add-listeners! tuples)))

(defn- unbind-listeners!
  "Cancel every static listener attached by bind-listeners! and clear
   the slot. Idempotent — a second call with no listeners is a no-op."
  [^js el]
  (when-let [^js tuples (gobj/get el model/k-listeners)]
    (remove-listeners! tuples)
    (gobj/set el model/k-listeners nil)))

(defn- initialize-filter-state!
  "Set up k-filter / k-view / k-view-selected / k-axis-mode on the
   first mount only. On re-mount these slots persist from the
   previous lifetime, so the user's filter, selected session, and
   axis preference survive a disconnect/reconnect.

   The skeleton's <x-checkbox checked> markup was rendered against
   the saved categories on the first mount and is preserved in the
   reused shadow tree, so the checkboxes stay in sync with the
   filter spec across remount."
  [^js el cats]
  (when-not (gobj/get el model/k-filter)
    (gobj/set el model/k-filter {:tag nil :categories cats :search nil}))
  (when-not (gobj/get el model/k-view)
    (gobj/set el model/k-view :live))
  (when-not (gobj/get el model/k-view-selected)
    (gobj/set el model/k-view-selected (js-obj)))
  (when-not (gobj/get el model/k-axis-mode)
    (gobj/set el model/k-axis-mode model/default-axis-mode))
  ;; Dock-mode persists across remount, same as axis-mode: a user
  ;; flipped to :causality before disconnect should land back in
  ;; causality on reconnect rather than getting reset to timeline.
  (when-not (gobj/get el model/k-dock-mode)
    (gobj/set el model/k-dock-mode model/default-dock-mode))
  ;; Default is expanded (collapsed = false). After this slot is set
  ;; once it survives remount, so apply-collapsed-state! below re-
  ;; applies the user's prior choice when the dock reconnects.
  (when-not (some? (gobj/get el model/k-collapsed))
    (gobj/set el model/k-collapsed false))
  (apply-collapsed-state! el (boolean (gobj/get el model/k-collapsed))))

(defn- mount!
  "Connect the dock: attach (or reuse) the shadow root, cache refs,
   bind listeners, and subscribe to recorder updates. Idempotent —
   a second call while already mounted is a no-op.

   Re-mountable: when the host is disconnected and re-appended,
   `attach-skeleton!` reuses the existing shadow root (it can only
   be attached once per host) and `bind-listeners!` installs a
   fresh set of listeners after `unmount!`'s symmetric tear-down."
  [^js el]
  (when-not (gobj/get el model/k-mounted)
    ;; Mark BEFORE attaching the shadow so any synchronous lifecycle
    ;; events from inside the shadow (e.g. nested components
    ;; registering) are already gated by the recorder's internal-host
    ;; boundary. Wrap the setup in with-suppressed-recording! to
    ;; catch records from elements that haven't been attached yet
    ;; (child constructors calling du/set-attr! / du/setv! on
    ;; detached internals).
    (recorder/mark-internal! el)
    (let [records-before (.-length (recorder/records))]
      (recorder/with-suppressed-recording!
        (fn []
          ;; Default the :state checkbox off in normal mode (instance-
          ;; field writes are the noisiest type and tend to drown out
          ;; user-relevant events in the timeline). Forensic mode
          ;; defaults all categories ON so users capturing edge-case
          ;; mutation chains see everything.
          (let [cats       (model/default-categories (recorder/forensic-mode?))
                ^js shadow (attach-skeleton! el cats)]
            (cache-refs! el shadow)
            (initialize-filter-state! el cats)
            (bind-listeners! el shadow)
            (let [tok (recorder/subscribe!
                       (fn []
                         (maybe-auto-switch-import! el)
                         (render! el)))]
              (gobj/set el model/k-sub-token tok))
            (render! el)
            (gobj/set el model/k-mounted true))))
      ;; INVARIANT (runtime-checked): every record emitted during
      ;; mount must be caught by either the suppression scope above
      ;; (for detached internals) or the internal-host marker (for
      ;; elements already attached inside the marked shadow). A leak
      ;; here means a future dynamic component constructor needs its
      ;; own with-suppressed-recording! wrapper around the render
      ;; call that creates it.
      (when (not= records-before (.-length (recorder/records)))
        (js/console.warn
         "[x-trace-history] dock mount produced records — internal-host suppression is leaking. "
         "Wrap dynamic child-component constructors in recorder/with-suppressed-recording!.")))))

(defn- unmount!
  "Symmetric tear-down for mount!. Removes every static listener via
   the listener-spec, unsubscribes from recorder updates, clears any
   pending import-error timeout (PR 11), and resets the mounted flag.
   The shadow root and its skeleton stay in place — `attachShadow` is
   one-shot per host, so a re-mount must reuse the existing shadow.
   The cached refs (k-shadow, k-*-el) and the user's filter / view
   state persist across mount cycles so the dock comes back in the
   same shape it was in when disconnected."
  [^js el]
  (when-let [tok (gobj/get el model/k-sub-token)]
    (recorder/unsubscribe! tok))
  (when-let [err-tok (model/ui-state el :import-error)]
    (when (number? err-tok)
      (js/clearTimeout err-tok)))
  (unbind-listeners! el)
  (gobj/set el model/k-sub-token nil)
  (model/set-ui-state! el :import-error nil)
  (gobj/set el model/k-mounted nil))

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(defn- activate!
  "Append a single <x-trace-history> element to <body> if none is present."
  []
  (when-not (.querySelector js/document model/tag-name)
    (let [^js el (.createElement js/document model/tag-name)]
      (.appendChild (.-body js/document) el))))

(def ^:private element-opts
  "Declarative class options passed to component/register!. `:internal? true`
   keeps the dev-tool lifecycle hook from firing on the dock's own
   connect/disconnect — otherwise those records would pollute every trace
   the dock records. There are no observed attributes; the no-op
   attribute-changed-fn is a formality (the callback can never fire)."
  {:internal?            true
   :observed-attributes  #js []
   :connected-fn         (fn [^js el] (mount!   el))
   :disconnected-fn      (fn [^js el] (unmount! el))
   :attribute-changed-fn (fn [_ _ _ _] nil)})

(defn register!
  "Single entry point: install recorder hooks (idempotent), register the
   library components used inside the dock (x-button, x-checkbox,
   x-search-field, x-select), register the <x-trace-history> custom
   element (idempotent), and auto-mount the dock if activation is on.
   Defers mounting to DOMContentLoaded if document.body is not yet
   available."
  []
  (recorder/register!)
  (baredom.components.x-button.x-button/init!)
  (baredom.components.x-checkbox.x-checkbox/init!)
  (baredom.components.x-search-field.x-search-field/init!)
  (baredom.components.x-select.x-select/init!)
  (comp/register! model/tag-name element-opts)
  (when (model/enabled? js/window)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!
                         #js {:once true}))))
