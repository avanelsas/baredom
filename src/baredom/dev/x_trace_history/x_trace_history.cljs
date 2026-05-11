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
   [baredom.dev.x-debug-registry :as registry]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]
   [baredom.exports.x-button   :as x-button-export]
   [baredom.exports.x-checkbox :as x-checkbox-export]
   [baredom.exports.x-select   :as x-select-export]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private lane-h     20)   ; px per timeline lane
(def ^:private dot-r       3)   ; default dot radius
(def ^:private dot-r-sel  4.8)  ; dot radius when selected
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

(defn- tag-options-html
  "Build <option> tags for every known component tag, sorted alphabetically."
  []
  (->> (sort (keys registry/registry))
       (map (fn [t]
              (str "<option value='" (escape-html t) "'>"
                   (escape-html t)
                   "</option>")))
       (apply str)))

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
       "<span class='title'>x-trace-history</span>"
       "<x-button data-x-th-action='pause'  size='sm' variant='ghost'>Pause</x-button>"
       "<x-button data-x-th-action='record' size='sm' variant='ghost'>Record</x-button>"
       "<x-button data-x-th-action='clear'  size='sm' variant='ghost'>Clear</x-button>"
       "<x-button data-x-th-action='export' size='sm' variant='ghost' "
       "title='Download trace as .trace.json'>Export</x-button>"
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
       "<x-select data-x-th-tag size='sm'>"
       "<option value='all' selected>All tags</option>"
       (tag-options-html)
       "</x-select>"
       ;; x-checkbox has no default slot — text inside the host is
       ;; invisible. Wrap in <label> per docs/x-checkbox.md so the
       ;; visible label sits next to the checkbox AND clicking it
       ;; toggles the control (native label-association behavior).
       (cat-checkbox-html "events"    (contains? initial-categories :events))
       (cat-checkbox-html "state"     (contains? initial-categories :state))
       (cat-checkbox-html "dom"       (contains? initial-categories :dom))
       (cat-checkbox-html "lifecycle" (contains? initial-categories :lifecycle))
       "</div>"
       "<div class='timeline' data-x-th-timeline tabindex='0'>"
       "<div class='lanes' data-x-th-lanes></div>"
       "<div class='svg-pane' data-x-th-svg-pane></div>"
       "<div class='tooltip' data-x-th-tooltip hidden></div>"
       "</div>"
       "<div class='splitter' data-x-th-splitter hidden></div>"
       "<div class='detail' data-x-th-detail hidden></div>"
       "<div class='hint' data-x-th-hint></div>"
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

(defn- dot-html
  [^js r lane-y bounds plot-width selected-id]
  (let [cx   (model/time-x (.-t r) bounds plot-width)
        cy   (+ lane-y (/ lane-h 2))
        sel? (= (.-id r) selected-id)
        rad  (if sel? dot-r-sel dot-r)]
    (str "<circle class='dot' "
         "data-x-th-id='" (.-id r) "' "
         "cx='" cx "' cy='" cy "' r='" rad "' "
         "fill='" (model/dot-color r) "'"
         (when sel? " stroke='#fff' stroke-width='1.5'")
         " />")))

(defn- scrubber-html
  "Vertical drag-line drawn at the selected record's x. Returns the empty
   string when no record is selected."
  [^js sel-rec bounds plot-width svg-h]
  (if sel-rec
    (let [x (model/time-x (.-t sel-rec) bounds plot-width)]
      (str "<line class='scrubber' x1='" x "' y1='0' "
           "x2='" x "' y2='" svg-h "' />"))
    ""))

(defn- svg-html
  "Build the timeline SVG markup. Records are positioned by lane (Y) and
   time (X); selected dot is drawn last so its highlight stroke wins, and
   the scrubber line is drawn on top of everything."
  [filtered-records lanes bounds plot-width ^js sel-rec]
  (let [selected-id (when sel-rec (.-id sel-rec))
        lane-y      (into {} (map-indexed
                              (fn [i {:keys [lane-id]}] [lane-id (* i lane-h)])
                              lanes))
        h           (max lane-h (* lane-h (count lanes)))
        ;; Selected dot last so its highlight stroke wins z-order.
        ordered     (sort-by (fn [^js r] (if (= (.-id r) selected-id) 1 0))
                             filtered-records)]
    (str "<svg class='timeline-svg' width='" plot-width "' height='" h
         "' viewBox='0 0 " plot-width " " h "'>"
         (apply str
                (map (fn [^js r]
                       (dot-html r (get lane-y (model/lane-id-of r))
                                 bounds plot-width selected-id))
                     ordered))
         (scrubber-html sel-rec bounds plot-width h)
         "</svg>")))

(defn- render-timeline!
  "Repaint the lane-label column + SVG canvas. Caller passes the current
   filtered records, bounds, etc. (so render! computes them once)."
  [^js lanes-el ^js svg-pane-el filtered lanes bounds ^js sel-rec active-tag]
  (if (zero? (count filtered))
    (do
      (set! (.-innerHTML lanes-el) "")
      (set! (.-innerHTML svg-pane-el)
            "<div class='timeline-empty'>No records match the current filter.</div>"))
    (let [plot-w (max svg-min-w (.-clientWidth svg-pane-el))]
      (set! (.-innerHTML lanes-el)    (lanes-html lanes active-tag))
      (set! (.-innerHTML svg-pane-el) (svg-html filtered lanes bounds
                                                plot-w sel-rec)))))

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
  "One chip per session. data-x-th-session is `live` for the live view
   or the numeric session id otherwise. The active flag styles the
   currently-viewed chip; the recording flag adds the live-dot pulse
   to whichever session is currently being captured."
  [{:keys [chip-key active? recording? label]}]
  (str "<button class='session-chip" (when active? " active") "' "
       "type='button' data-x-th-session='" (escape-html chip-key) "'>"
       (when recording? "<span class='live-dot' aria-hidden='true'></span>")
       (escape-html label)
       "</button>"))

(defn- session-strip-html
  "Build the strip: a Live chip plus one chip per recorded session,
   sorted by session id ascending. Returns an empty string when no
   sessions exist (the strip is then hidden)."
  [^js sessions ^js view active-recording-id]
  (let [view-id (model/view-id view)
        live    {:chip-key   "live"
                 :active?    (model/live-view? view)
                 :recording? false
                 :label      "Live"}
        chips   (mapv (fn [^js s]
                        (let [id (.-id s)]
                          {:chip-key   (str id)
                           :active?    (= id view-id)
                           :recording? (= id active-recording-id)
                           :label      (str (.-label s)
                                            " · " (.-recordCount s))}))
                      (array-seq sessions))]
    (apply str (map session-chip-html (cons live chips)))))

(defn- refresh-sessions-strip!
  [^js sessions-el ^js sessions ^js view active-recording-id]
  (set! (.-innerHTML sessions-el)
        (session-strip-html sessions view active-recording-id))
  (if (zero? (.-length sessions))
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
   it as a JS-object key. Live → 'live'; session → 'session:N'."
  [view]
  (cond
    (model/live-view? view)    "live"
    (model/session-view? view) (str "session:" (model/view-id view))
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
   buffer; session N → the records inside that session's t-range. If a
   session has been deleted (or its id is stale) we fall back to live
   so the dock keeps rendering instead of going blank."
  [view]
  (cond
    (model/live-view? view)
    (recorder/records)

    (model/session-view? view)
    (let [recs (recorder/session-records (model/view-id view))]
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
        active-rec-id   (recorder/active-session-id)]
    (render-timeline! lanes-el svg-pane-el filtered lanes bounds sel-rec (:tag spec))
    (set! (.-textContent count-el) (str cnt-filtered))
    (set! (.-textContent hint-el)
          (model/timeline-hint cnt-filtered cnt-total bounds (count lanes)))
    (refresh-detail!         detail-el splitter-el recs sel-rec)
    (refresh-pause-btn!      pause-btn)
    (refresh-record-btn!     record-btn)
    (refresh-sessions-strip! sessions-el sessions view active-rec-id)))

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
      nil)))

(defn- on-session-chip-click!
  "Switch the dock's view when the user clicks a chip in the session
   strip. data-x-th-session is 'live' or a numeric session id; the
   chip's view becomes the active view and render! repaints from the
   new record source."
  [^js el ^js target]
  (when-let [^js btn (.closest target "[data-x-th-session]")]
    (let [k        (.getAttribute btn "data-x-th-session")
          new-view (if (= k "live")
                     :live
                     [:session (js/parseInt k 10)])]
      (gobj/set el model/k-view new-view)
      (render! el))))

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
    (on-action-click!       el target)
    (on-session-chip-click! el target)
    (on-lane-label-click!   el target)
    (on-detail-link-click!  el target)
    (on-dot-click!          el target)))

(defn- handle-tag-change!
  "Apply a new tag filter from a select-change detail value. The 'all'
   sentinel maps to nil :tag (matches every record)."
  [^js el value]
  (let [spec     (gobj/get el model/k-filter)
        new-spec (assoc spec :tag (when-not (= value "all") value))]
    (gobj/set el model/k-filter new-spec)
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
  "x-select fires `select-change` with detail = {value, label}."
  [^js el ^js e]
  (let [^js target (.-target e)]
    (when (some? (.getAttribute target "data-x-th-tag"))
      (handle-tag-change! el (.. e -detail -value)))))

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

(defn- select-nearest!
  "Apply a precomputed scrub context to a raw cursor x. Used by the
   scrub-start kick-off and by every pointermove that follows so the
   filter / records / bounds / plot-width snapshot is taken once at
   gesture start (a drag is a moment in time — capturing newly arriving
   records mid-drag would be more confusing than helpful)."
  [^js el filtered bounds plot-w x]
  (let [target-t    (model/time-from-x x bounds plot-w)
        ^js nearest (model/nearest-record filtered target-t)]
    (when nearest
      (set-selected! el (.-id nearest))
      (render! el))))

(defn- start-scrub!
  "Begin a pointer-driven scrub. The pointer is captured on the svg-pane,
   so the gesture survives the cursor leaving the SVG bounds. Filter
   snapshot, bounds, and plot width are computed once and reused for
   every pointermove until pointerup / pointercancel."
  [^js el ^js svg-pane ^js e]
  (.preventDefault e)
  (.setPointerCapture svg-pane (.-pointerId e))
  (let [pane-rect (.getBoundingClientRect svg-pane)
        plot-w    (max svg-min-w (.-clientWidth svg-pane))
        spec      (gobj/get el model/k-filter)
        ^js recs  (recorder/records)
        filtered  (model/filter-records recs spec)
        bounds    (model/time-bounds filtered)
        x-of      (fn [^js me] (- (.-clientX me) (.-left pane-rect)))]
    (select-nearest! el filtered bounds plot-w (x-of e))
    (let [on-move (fn [^js me]
                    (select-nearest! el filtered bounds plot-w (x-of me)))
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
  (gobj/set el model/k-sessions-el   (.querySelector shadow "[data-x-th-sessions]")))

(defn- build-listener-tuples
  "Allocate the dock's static event listeners as #js [target event
   handler] triples. Handlers are closures over `el`; each tuple
   captures a stable fn reference so the matching .removeEventListener
   call in unmount! cancels the same listener that was added.

   The encoding is the `listener-spec` named pattern from CLAUDE.md
   — one place lists every static listener, and add/remove iterate
   the same source of truth so they cannot drift."
  [^js el ^js shadow ^js timeline-el ^js svg-pane-el ^js splitter-el ^js detail-el]
  #js [#js [shadow      "click"             (fn [^js e] (handle-click!            el e))]
       #js [shadow      "select-change"     (fn [^js e] (handle-select-change!    el e))]
       #js [shadow      "x-checkbox-change" (fn [^js e] (handle-checkbox-change!  el e))]
       #js [timeline-el "pointermove"       (fn [^js e] (handle-pointermove!      el e))]
       #js [timeline-el "pointerleave"      (fn [^js e] (handle-pointerleave!     el e))]
       ;; Pointerdown anywhere in the timeline focuses it so the
       ;; keyboard outline shows the user their starting point.
       #js [timeline-el "pointerdown"       (fn [_e] (.focus timeline-el))]
       ;; Keydown is bound on the SHADOW ROOT (not the timeline) so the
       ;; shadow receives keydown from any focused descendant — arrow
       ;; keys keep working even when focus shifts to a control after
       ;; a click. The handler ignores form-control targets so we
       ;; don't fight native behaviour.
       #js [shadow      "keydown"           (fn [^js e] (handle-keydown!          el e))]
       #js [svg-pane-el "pointerdown"       (fn [^js e] (handle-svg-pointerdown!  el e))]
       #js [splitter-el "pointerdown"       (fn [^js e] (start-resize! detail-el splitter-el e))]])

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
  (let [^js timeline-el (gobj/get el model/k-timeline-el)
        ^js svg-pane-el (gobj/get el model/k-svg-pane-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js detail-el   (gobj/get el model/k-detail-el)
        tuples          (build-listener-tuples
                         el shadow timeline-el svg-pane-el splitter-el detail-el)]
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
  "Set up k-filter / k-view / k-view-selected on the first mount only.
   On re-mount these slots persist from the previous lifetime, so the
   user's filter and selected session survive a disconnect/reconnect.

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
    (gobj/set el model/k-view-selected (js-obj))))

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
   the listener-spec, unsubscribes from recorder updates, and clears
   the mounted flag. The shadow root and its skeleton stay in place
   — `attachShadow` is one-shot per host, so a re-mount must reuse
   the existing shadow. The cached refs (k-shadow, k-*-el) and the
   user's filter / view state persist across mount cycles so the
   dock comes back in the same shape it was in when disconnected."
  [^js el]
  (when-let [tok (gobj/get el model/k-sub-token)]
    (recorder/unsubscribe! tok))
  (unbind-listeners! el)
  (gobj/set el model/k-sub-token nil)
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
  (x-button-export/register!)
  (x-checkbox-export/register!)
  (x-select-export/register!)
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  (when (model/enabled? js/window)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!))))
