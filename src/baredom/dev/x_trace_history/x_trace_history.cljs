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
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]
   ;; Require the component implementations directly rather than the
   ;; baredom.exports.x-* wrappers: each exports ns is the entry of a
   ;; separate :lib module, and pulling them in from here would force
   ;; shadow-cljs to move those entries into :base — breaking the
   ;; per-module ESM split. The components themselves are plain
   ;; shareable code that shadow-cljs is happy to land in :base on its
   ;; own.
   [baredom.components.x-button.x-button     :as x-button-impl]
   [baredom.components.x-checkbox.x-checkbox :as x-checkbox-impl]
   [baredom.components.x-select.x-select     :as x-select-impl]))

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

;; ---------------------------------------------------------------------------
;; HTML escaping
;; ---------------------------------------------------------------------------

(defn- escape-html
  "Escape HTML metacharacters for safe interpolation into innerHTML."
  [s]
  (-> (str s)
      (.replace (js/RegExp. "&"  "g") "&amp;")
      (.replace (js/RegExp. "<"  "g") "&lt;")
      (.replace (js/RegExp. ">"  "g") "&gt;")
      (.replace (js/RegExp. "\"" "g") "&quot;")
      (.replace (js/RegExp. "'"  "g") "&#39;")))

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
       ;; x-checkbox has no default slot — text inside the host is
       ;; invisible. Wrap in <label> per docs/x-checkbox.md so the
       ;; visible label sits next to the checkbox AND clicking it
       ;; toggles the control (native label-association behavior).
       (cat-checkbox-html "events"    (contains? initial-categories :events))
       (cat-checkbox-html "state"     (contains? initial-categories :state))
       (cat-checkbox-html "dom"       (contains? initial-categories :dom))
       (cat-checkbox-html "lifecycle" (contains? initial-categories :lifecycle))
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
    :time  (model/time-x (.-t r) bounds plot-width)
    (model/index-x index n plot-width)))

(defn- dot-html
  [^js r cx lane-y selected-id]
  (let [cy   (+ lane-y (/ lane-h 2))
        sel? (= (.-id r) selected-id)
        rad  (if sel? dot-r-sel dot-r)]
    (str "<circle class='dot' "
         "data-x-th-id='" (.-id r) "' "
         "cx='" cx "' cy='" cy "' r='" rad "' "
         "fill='" (model/dot-color r) "'"
         (when sel? " stroke='#fff' stroke-width='1.5'")
         " />")))

(defn- scrubber-html
  "Vertical drag-line drawn at the selected record's x. Returns the
   empty string when no record is selected. `cx` is pre-computed by
   svg-html under whichever axis mode is active."
  [^js sel-rec cx svg-h]
  (if sel-rec
    (str "<line class='scrubber' x1='" cx "' y1='0' "
         "x2='" cx "' y2='" svg-h "' />")
    ""))

(defn- svg-html
  "Build the timeline SVG markup. Records are positioned by lane (Y)
   and by the active axis mode (X) — :order spreads records evenly by
   their index in the filtered list, :time maps them by their `t`
   value. Selected dot is drawn last so its highlight stroke wins
   z-order, and the scrubber line is drawn on top of everything."
  [axis-mode filtered-records lanes bounds plot-width ^js sel-rec]
  (let [selected-id (when sel-rec (.-id sel-rec))
        lane-y      (into {} (map-indexed
                              (fn [i {:keys [lane-id]}] [lane-id (* i lane-h)])
                              lanes))
        h           (max lane-h (* lane-h (count lanes)))
        n           (count filtered-records)
        ;; Capture each filtered record's index BEFORE the z-order
        ;; sort so the order-axis position survives the rearrangement.
        id->index   (into {} (map-indexed
                              (fn [i ^js r] [(.-id r) i]) filtered-records))
        cx-of       (fn [^js r]
                      (record-cx axis-mode r (get id->index (.-id r)) n
                                 bounds plot-width))
        ;; Selected dot last so its highlight stroke wins z-order.
        ordered     (sort-by (fn [^js r] (if (= (.-id r) selected-id) 1 0))
                             filtered-records)]
    (str "<svg class='timeline-svg' width='" plot-width "' height='" h
         "' viewBox='0 0 " plot-width " " h "'>"
         (apply str
                (map (fn [^js r]
                       (dot-html r (cx-of r)
                                 (get lane-y (model/lane-id-of r))
                                 selected-id))
                     ordered))
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
  "Write the selected record-id for the active view. Mirrors to the
   legacy k-selected-id slot when the view is Live so existing test
   assertions and any external readers keep working."
  [^js el id]
  (let [view  (gobj/get el model/k-view)
        ^js m (or (gobj/get el model/k-view-selected) (js-obj))
        k     (view-key view)]
    (if (nil? id)
      (gobj/remove m k)
      (gobj/set    m k id))
    (gobj/set el model/k-view-selected m)
    (when (model/live-view? view)
      (gobj/set el model/k-selected-id id))
    nil))

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

      (model/record-matches? rec spec)
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

(defn- render!
  "Repaint timeline, count, hint, detail pane, pause-button, record-button
   and session strip from current recorder + filter + view + selection state."
  [^js el]
  (let [^js lanes-el    (gobj/get el model/k-lanes-el)
        ^js svg-pane-el (gobj/get el model/k-svg-pane-el)
        ^js count-el    (gobj/get el model/k-count-el)
        ^js detail-el   (gobj/get el model/k-detail-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js hint-el     (gobj/get el model/k-hint-el)
        ^js pause-btn   (gobj/get el model/k-pause-btn)
        ^js record-btn  (gobj/get el model/k-record-btn)
        ^js sessions-el (gobj/get el model/k-sessions-el)
        ^js tag-sel-el  (gobj/get el model/k-tag-select-el)
        spec            (gobj/get el model/k-filter)
        view            (gobj/get el model/k-view)
        ^js recs        (view-records view)
        filtered        (model/filter-records recs spec)
        cnt-filtered    (count filtered)
        cnt-total       (.-length recs)
        bounds          (model/time-bounds filtered)
        lanes           (model/active-lanes filtered)
        sel-rec         (effective-selection! el recs spec)
        ^js sessions    (recorder/sessions)
        ^js imports     (recorder/imports)
        active-rec-id   (recorder/active-session-id)
        axis-mode       (or (gobj/get el model/k-axis-mode)
                            model/default-axis-mode)]
    (sync-tag-options!       tag-sel-el)
    (render-timeline! lanes-el svg-pane-el axis-mode filtered lanes bounds
                      sel-rec (:tag spec))
    (set! (.-textContent count-el) (str cnt-filtered))
    (set! (.-textContent hint-el)
          (model/timeline-hint cnt-filtered cnt-total bounds (count lanes)))
    (refresh-detail!         detail-el splitter-el recs sel-rec)
    (refresh-pause-btn!      pause-btn)
    (refresh-record-btn!     record-btn)
    (refresh-sessions-strip! sessions-el sessions imports view active-rec-id)))

;; ---------------------------------------------------------------------------
;; Click + tooltip + filter handlers
;; ---------------------------------------------------------------------------

(defn- circle-target?
  [^js target]
  (and target (= "circle" (.. target -tagName toLowerCase))))

(defn- read-dot-id
  [^js circle]
  (when-let [s (.getAttribute circle "data-x-th-id")]
    (js/parseInt s 10)))

(defn- on-dot-click!
  [^js el ^js target]
  (when (circle-target? target)
    (let [id     (read-dot-id target)
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
                   (gobj/set el model/k-selected-id nil)
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
  "x-select fires `select-change` with detail = {value, label}. Two
   selects live in the dock (tag filter + axis mode); the data
   attribute disambiguates."
  [^js el ^js e]
  (let [^js target (.-target e)
        value      (.. e -detail -value)]
    (cond
      (some? (.getAttribute target "data-x-th-tag"))
      (handle-tag-change! el value)

      (some? (.getAttribute target "data-x-th-axis"))
      (handle-axis-change! el value))))

(defn- handle-checkbox-change!
  "x-checkbox fires `x-checkbox-change` with detail = {value, checked}."
  [^js el ^js e]
  (let [^js target (.-target e)
        cat        (.getAttribute target "data-x-th-cat")]
    (when (some? cat)
      (handle-cat-change! el cat (.. e -detail -checked)))))

(def ^:private tooltip-cursor-offset 12)
(def ^:private tooltip-max-width    280)  ; mirrors CSS max-width

(defn- show-tooltip!
  "Place the tooltip near the cursor inside the timeline pane. Anchored to
   the right of the cursor by default; flips to the left when staying right
   would push the tooltip past the timeline's right edge. Uses the CSS
   max-width as the worst-case tooltip width to avoid a measure pass."
  [^js tooltip-el ^js timeline-el ^js e ^js record]
  (set! (.-textContent tooltip-el) (model/tooltip-text record))
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

(defn- hide-tooltip!
  [^js tooltip-el]
  (.setAttribute tooltip-el "hidden" ""))

(defn- handle-pointermove!
  [^js el ^js e]
  (let [^js target (.-target e)]
    (if (circle-target? target)
      (let [^js tooltip  (gobj/get el model/k-tooltip-el)
            ^js timeline (gobj/get el model/k-timeline-el)
            id           (read-dot-id target)
            ^js recs     (recorder/records)
            r            (model/find-record-by-id recs id)]
        (when r
          (show-tooltip! tooltip timeline e r)))
      (hide-tooltip! ^js (gobj/get el model/k-tooltip-el)))))

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
    (model/nearest-record filtered (model/time-from-x x bounds plot-w))

    (when-let [i (model/index-from-x x (count filtered) plot-w)]
      (nth filtered i nil))))

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
   reused for every pointermove until pointerup / pointercancel."
  [^js el ^js svg-pane ^js e]
  (.preventDefault e)
  (.setPointerCapture svg-pane (.-pointerId e))
  (let [pane-rect (.getBoundingClientRect svg-pane)
        plot-w    (max svg-min-w (.-clientWidth svg-pane))
        spec      (gobj/get el model/k-filter)
        axis-mode (or (gobj/get el model/k-axis-mode) model/default-axis-mode)
        ^js recs  (recorder/records)
        filtered  (model/filter-records recs spec)
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
        filtered   (model/filter-records recs spec)
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
        prev-tok    (gobj/get el model/k-import-error)]
    (when (number? prev-tok)
      (js/clearTimeout prev-tok))
    (set! (.-textContent hint-el) msg)
    (.add (.-classList hint-el) "error")
    (let [tok (js/setTimeout
               (fn []
                 (.remove (.-classList hint-el) "error")
                 (gobj/set el model/k-import-error nil)
                 (render! el))
               import-error-display-ms)]
      (gobj/set el model/k-import-error tok))))

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
   Counter-based (k-drag-depth) so passing over a child element
   doesn't flicker the overlay."
  [^js el]
  (let [^js overlay (gobj/get el model/k-drop-overlay)
        d           (or (gobj/get el model/k-drag-depth) 0)]
    (gobj/set el model/k-drag-depth (inc d))
    (.removeAttribute overlay "hidden")))

(defn- on-drag-overlay-hide!
  "Hide the drop-overlay when the dragleave count returns to 0 or
   after a successful drop."
  [^js el]
  (let [^js overlay (gobj/get el model/k-drop-overlay)
        d           (max 0 (dec (or (gobj/get el model/k-drag-depth) 0)))]
    (gobj/set el model/k-drag-depth d)
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
    (gobj/set el model/k-drag-depth 0)
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
            (str "<style>" model/dock-css "</style>"
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
  (gobj/set el model/k-tag-select-el (.querySelector shadow "[data-x-th-tag]"))
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
   ^js import-input]
  #js [#js [shadow       "click"             (fn [^js e] (handle-click!                 el e))]
       #js [shadow       "select-change"     (fn [^js e] (handle-select-change!         el e))]
       #js [shadow       "x-checkbox-change" (fn [^js e] (handle-checkbox-change!       el e))]
       #js [timeline-el  "pointermove"       (fn [^js e] (handle-pointermove!           el e))]
       #js [timeline-el  "pointerleave"      (fn [^js e] (handle-pointerleave!          el e))]
       ;; Pointerdown anywhere in the timeline focuses it so the
       ;; keyboard outline shows the user their starting point.
       #js [timeline-el  "pointerdown"       (fn [_e] (.focus timeline-el))]
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
        tuples            (build-listener-tuples
                           el shadow timeline-el svg-pane-el splitter-el detail-el
                           import-input)]
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
    (gobj/set el model/k-filter {:tag nil :categories cats}))
  (when-not (gobj/get el model/k-view)
    (gobj/set el model/k-view :live))
  (when-not (gobj/get el model/k-view-selected)
    (gobj/set el model/k-view-selected (js-obj)))
  (when-not (gobj/get el model/k-axis-mode)
    (gobj/set el model/k-axis-mode model/default-axis-mode))
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
    ;; events from inside the shadow (e.g. nested components registering)
    ;; are already gated by the recorder's internal-host boundary.
    (recorder/mark-internal! el)
    ;; Wrap the entire setup in with-suppressed-recording! so child
    ;; component constructors that mutate freshly-created (not-yet-
    ;; attached) internal elements via du/set-attr! / du/setv! don't
    ;; leak records into the trace. The boundary alone catches events
    ;; from elements ATTACHED inside the marked shadow; this scope
    ;; catches events from elements that haven't been attached yet.
    ;;
    ;; INVARIANT for future maintainers: this scope only covers the
    ;; INITIAL mount. If you add dynamic creation of x-checkbox /
    ;; x-select / any component whose constructor calls du/set-attr!
    ;; or du/setv! on detached internals (current x-button does NOT
    ;; — it uses native setAttribute) — wrap that render call in
    ;; recorder/with-suppressed-recording! too. Otherwise records
    ;; will leak on every re-render that creates such instances.
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
          (let [tok (recorder/subscribe! (fn [] (render! el)))]
            (gobj/set el model/k-sub-token tok))
          (render! el)
          (gobj/set el model/k-mounted true))))))

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
  (when-let [err-tok (gobj/get el model/k-import-error)]
    (when (number? err-tok)
      (js/clearTimeout err-tok)))
  (unbind-listeners! el)
  (gobj/set el model/k-sub-token nil)
  (gobj/set el model/k-import-error nil)
  (gobj/set el model/k-mounted nil))

;; ---------------------------------------------------------------------------
;; Element class
;; ---------------------------------------------------------------------------

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (mount!   this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (unmount! this))))
    klass))

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(defn- activate!
  "Append a single <x-trace-history> element to <body> if none is present."
  []
  (when-not (.querySelector js/document model/tag-name)
    (let [^js el (.createElement js/document model/tag-name)]
      (.appendChild (.-body js/document) el))))

(defn register!
  "Single entry point: install recorder hooks (idempotent), register the
   library components used inside the dock (x-button, x-checkbox,
   x-select), define the custom element (idempotent), and auto-mount the
   dock if activation is on. Defers mounting to DOMContentLoaded if
   document.body is not yet available."
  []
  (recorder/register!)
  (x-button-impl/init!)
  (x-checkbox-impl/init!)
  (x-select-impl/init!)
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  (when (model/enabled? js/window)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!))))
