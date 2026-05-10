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
   [baredom.exports.x-button :as x-button-export]))

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

(defn- skeleton-html
  "Static dock skeleton. The timeline body is populated dynamically by
   render!; the tooltip starts hidden and is shown on dot hover.

   The Pause and Clear buttons are <x-button> instances. Click events
   still bubble natively from the host, so the existing click delegate
   on the dock root works unchanged. The recorder's internal-host
   boundary (PR-A) suppresses x-button's own `press`/`hover` events
   so they never appear in the trace."
  []
  (str "<div class='dock'>"
       "<div class='header'>"
       "<span class='title'>x-trace-history</span>"
       "<x-button data-x-th-action='pause' size='sm' variant='ghost'>Pause</x-button>"
       "<x-button data-x-th-action='clear' size='sm' variant='ghost'>Clear</x-button>"
       "<span class='count' data-x-th-count>0</span>"
       "</div>"
       "<div class='filters'>"
       "<select data-x-th-tag>"
       "<option value='all'>All tags</option>"
       (tag-options-html)
       "</select>"
       "<label><input type='checkbox' data-x-th-cat='events' checked> events</label>"
       "<label><input type='checkbox' data-x-th-cat='state' checked> state</label>"
       "<label><input type='checkbox' data-x-th-cat='dom' checked> dom</label>"
       "<label><input type='checkbox' data-x-th-cat='lifecycle' checked> lifecycle</label>"
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

(defn- effective-selection!
  "Return the currently-selected record IFF it still passes the active
   filter; otherwise clear the dock's selected-id and return nil. Keeps
   the detail pane in sync with filter changes — selecting a dot, then
   filtering it out, drops the detail rather than leaving an orphan."
  [^js el ^js recs spec]
  (let [sel-id (gobj/get el model/k-selected-id)
        rec    (model/find-record-by-id recs sel-id)]
    (cond
      (nil? rec)
      nil

      (model/record-matches? rec spec)
      rec

      :else
      (do (gobj/set el model/k-selected-id nil) nil))))

;; ---------------------------------------------------------------------------
;; Render orchestrator
;; ---------------------------------------------------------------------------

(defn- render!
  "Repaint timeline, count, hint, detail pane, and pause-button state from
   current recorder + filter + selection state."
  [^js el]
  (let [^js lanes-el    (gobj/get el model/k-lanes-el)
        ^js svg-pane-el (gobj/get el model/k-svg-pane-el)
        ^js count-el    (gobj/get el model/k-count-el)
        ^js detail-el   (gobj/get el model/k-detail-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js hint-el     (gobj/get el model/k-hint-el)
        ^js pause-btn   (gobj/get el model/k-pause-btn)
        spec            (gobj/get el model/k-filter)
        ^js recs        (recorder/records)
        filtered        (model/filter-records recs spec)
        cnt-filtered    (count filtered)
        cnt-total       (.-length recs)
        bounds          (model/time-bounds filtered)
        lanes           (model/active-lanes filtered)
        sel-rec         (effective-selection! el recs spec)]
    (render-timeline! lanes-el svg-pane-el filtered lanes bounds sel-rec (:tag spec))
    (set! (.-textContent count-el) (str cnt-filtered))
    (set! (.-textContent hint-el)
          (model/timeline-hint cnt-filtered cnt-total bounds (count lanes)))
    (refresh-detail! detail-el splitter-el recs sel-rec)
    (refresh-pause-btn! pause-btn)))

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
          curr   (gobj/get el model/k-selected-id)
          new-id (if (= id curr) nil id)]
      (gobj/set el model/k-selected-id new-id)
      (render! el))))

(defn- on-action-click!
  [^js el ^js target]
  (when-let [^js btn (.closest target "[data-x-th-action]")]
    (case (.getAttribute btn "data-x-th-action")
      "pause" (if (recorder/paused?) (recorder/resume!) (recorder/pause!))
      "clear" (do (gobj/set el model/k-selected-id nil) (recorder/clear!))
      nil)))

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
          (gobj/set el model/k-selected-id id)
          (render! el))))))

(defn- handle-click!
  [^js el ^js e]
  (let [^js target (.-target e)]
    (on-action-click!      el target)
    (on-lane-label-click!  el target)
    (on-detail-link-click! el target)
    (on-dot-click!         el target)))

(defn- handle-tag-change!
  [^js el ^js target]
  (let [tag      (.-value target)
        spec     (gobj/get el model/k-filter)
        new-spec (assoc spec :tag (when-not (= tag "all") tag))]
    (gobj/set el model/k-filter new-spec)
    (render! el)))

(defn- handle-cat-change!
  [^js el ^js target]
  (let [cat      (.getAttribute target "data-x-th-cat")
        spec     (gobj/get el model/k-filter)
        cats     (or (:categories spec) (set model/all-categories))
        cat-kw   (keyword cat)
        new-cats (if (.-checked target) (conj cats cat-kw) (disj cats cat-kw))
        new-spec (assoc spec :categories new-cats)]
    (gobj/set el model/k-filter new-spec)
    (render! el)))

(defn- handle-change!
  [^js el ^js e]
  (let [^js target (.-target e)]
    (cond
      (some? (.getAttribute target "data-x-th-tag"))
      (handle-tag-change! el target)

      (some? (.getAttribute target "data-x-th-cat"))
      (handle-cat-change! el target))))

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
      (gobj/set el model/k-selected-id (.-id nearest))
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
  (let [spec        (gobj/get el model/k-filter)
        ^js recs    (recorder/records)
        filtered    (model/filter-records recs spec)
        curr-id     (gobj/get el model/k-selected-id)
        ^js next-r  (model/step-record filtered curr-id dir)]
    (when next-r
      (gobj/set el model/k-selected-id (.-id next-r))
      (render! el))))

(def ^:private form-control-tags #{"input" "select" "textarea"})

(defn- in-form-control?
  "Skip arrow handling when focus is somewhere arrow keys have native
   semantics — text inputs (cursor movement), selects (option stepping),
   textareas, and contenteditable regions. Buttons are NOT included
   because arrows do nothing native on a button, and excluding them
   means the user can click Pause / Clear and immediately use arrows
   without re-clicking the timeline."
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
  "Build shadow root, inject skeleton + style, return the shadow root.

   Sets innerHTML directly on the shadow root (rather than parsing
   into a detached temp div first) so any custom-element children —
   currently the Pause/Clear x-buttons — are upgraded INSIDE the
   dock's marked shadow tree. Their attributeChangedCallback fires
   immediately inside the boundary (PR-A), so initial size/variant
   attribute changes are correctly suppressed. dock-css is verified
   to contain no `</style>` substring so embedding it inside a
   <style> tag is safe."
  [^js el]
  (let [^js shadow (.attachShadow el #js {:mode "open"})]
    (set! (.-innerHTML shadow)
          (str "<style>" model/dock-css "</style>" (skeleton-html)))
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
  (gobj/set el model/k-pause-btn     (.querySelector shadow "[data-x-th-action='pause']")))

(defn- bind-listeners!
  [^js el ^js shadow]
  (let [^js dock        (.querySelector shadow ".dock")
        ^js timeline-el (gobj/get el model/k-timeline-el)
        ^js svg-pane-el (gobj/get el model/k-svg-pane-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js detail-el   (gobj/get el model/k-detail-el)]
    (.addEventListener dock "click"  (fn [^js e] (handle-click!  el e)))
    (.addEventListener dock "change" (fn [^js e] (handle-change! el e)))
    (.addEventListener timeline-el "pointermove"
                       (fn [^js e] (handle-pointermove! el e)))
    (.addEventListener timeline-el "pointerleave"
                       (fn [^js e] (handle-pointerleave! el e)))
    ;; Pointerdown anywhere in the timeline focuses it so the keyboard
    ;; outline (the inset box-shadow on .timeline:focus) shows the user
    ;; their starting point.
    (.addEventListener timeline-el "pointerdown"
                       (fn [_e] (.focus timeline-el)))
    ;; Bind keydown on the SHADOW ROOT, not the timeline. The shadow
    ;; receives keydown events from any focused descendant — so arrow
    ;; keys keep working even when focus shifts to a control after a
    ;; click. The handler ignores form-control targets so we don't
    ;; fight native behaviour. The fn reference is cached so unmount!
    ;; can detach it cleanly (the shadow root outlives many of its
    ;; descendant listeners' element-bound counterparts).
    (let [keydown-fn (fn [^js e] (handle-keydown! el e))]
      (gobj/set el model/k-keydown-fn keydown-fn)
      (.addEventListener shadow "keydown" keydown-fn))
    (.addEventListener svg-pane-el "pointerdown"
                       (fn [^js e] (handle-svg-pointerdown! el e)))
    (.addEventListener splitter-el "pointerdown"
                       (fn [^js e] (start-resize! detail-el splitter-el e)))))

(defn- mount!
  [^js el]
  (when-not (gobj/get el model/k-mounted)
    ;; Mark BEFORE attaching the shadow so any synchronous lifecycle
    ;; events from inside the shadow (e.g. nested components registering)
    ;; are already gated by the recorder's internal-host boundary.
    (recorder/mark-internal! el)
    (let [^js shadow (attach-skeleton! el)]
      (cache-refs! el shadow)
      (gobj/set el model/k-filter
                {:tag nil :categories (set model/all-categories)})
      (gobj/set el model/k-selected-id nil)
      (bind-listeners! el shadow)
      (let [tok (recorder/subscribe! (fn [] (render! el)))]
        (gobj/set el model/k-sub-token tok))
      (render! el)
      (gobj/set el model/k-mounted true))))

(defn- unmount!
  [^js el]
  (when-let [tok (gobj/get el model/k-sub-token)]
    (recorder/unsubscribe! tok))
  (let [^js shadow     (gobj/get el model/k-shadow)
        ^js keydown-fn (gobj/get el model/k-keydown-fn)]
    (when (and shadow keydown-fn)
      (.removeEventListener shadow "keydown" keydown-fn)))
  (gobj/set el model/k-keydown-fn nil)
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
   library components used inside the dock (x-button), define the
   custom element (idempotent), and auto-mount the dock if activation is on.
   Defers mounting to DOMContentLoaded if document.body is not yet available."
  []
  (recorder/register!)
  (x-button-export/register!)
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  (when (model/enabled? js/window)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!))))
