(ns baredom.dev.x-trace-history.x-trace-history
  "Dev-only floating dock for x-trace-history.

   When activated (?baredom-trace-history or window.BAREDOM_TRACE_HISTORY),
   register! installs the recorder hooks (via recorder/register!), defines a
   `<x-trace-history>` custom element, and auto-mounts one to <body>. The
   dock subscribes to recorder updates and renders an SVG timeline: one
   lane per component instance (Y), event time on X, dots coloured by
   category. Hover for tooltip, click to open the JSON detail pane."
  (:require
   [goog.object :as gobj]
   [baredom.dev.x-debug-registry :as registry]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]))

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
   render!; the tooltip starts hidden and is shown on dot hover."
  []
  (str "<div class='dock'>"
       "<div class='header'>"
       "<span class='title'>x-trace-history</span>"
       "<button class='btn' data-x-th-action='pause' type='button'>Pause</button>"
       "<button class='btn' data-x-th-action='clear' type='button'>Clear</button>"
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
       "<div class='timeline' data-x-th-timeline>"
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

(defn- find-record-by-id
  "Return the record with id `id`, or nil if not found / id non-numeric.
   Delegates to Array.prototype.find for short-circuiting linear search."
  [^js records id]
  (when (number? id)
    (.find records (fn [^js r] (= id (.-id r))))))

(defn- lane-label-html
  [{:keys [lane-id tag] :as lane}]
  (let [text (model/lane-label lane)]
    (str "<div class='lane-label' title='" (escape-html text) "'>"
         (escape-html (if (= model/document-lane lane-id) "document" tag))
         (when (not= model/document-lane lane-id)
           (str " <span class='cid'>#" lane-id "</span>"))
         "</div>")))

(defn- lanes-html
  [lanes]
  (apply str (map lane-label-html lanes)))

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

(defn- svg-html
  "Build the timeline SVG markup. Records are positioned by lane (Y) and
   time (X); selected dot is drawn last so its highlight stroke wins."
  [filtered-records lanes bounds plot-width selected-id]
  (let [lane-y  (into {} (map-indexed
                          (fn [i {:keys [lane-id]}] [lane-id (* i lane-h)])
                          lanes))
        h       (max lane-h (* lane-h (count lanes)))
        ;; Selected dot last so its highlight stroke wins z-order.
        ordered (sort-by (fn [^js r] (if (= (.-id r) selected-id) 1 0))
                         filtered-records)]
    (str "<svg class='timeline-svg' width='" plot-width "' height='" h
         "' viewBox='0 0 " plot-width " " h "'>"
         (apply str
                (map (fn [^js r]
                       (dot-html r (get lane-y (model/lane-id-of r))
                                 bounds plot-width selected-id))
                     ordered))
         "</svg>")))

(defn- render-timeline!
  "Repaint the lane-label column + SVG canvas. Caller passes the current
   filtered records, bounds, etc. (so render! computes them once)."
  [^js lanes-el ^js svg-pane-el filtered lanes bounds selected-id]
  (if (zero? (count filtered))
    (do
      (set! (.-innerHTML lanes-el) "")
      (set! (.-innerHTML svg-pane-el)
            "<div class='timeline-empty'>No records match the current filter.</div>"))
    (let [plot-w (max svg-min-w (.-clientWidth svg-pane-el))]
      (set! (.-innerHTML lanes-el)    (lanes-html lanes))
      (set! (.-innerHTML svg-pane-el) (svg-html filtered lanes bounds
                                                plot-w selected-id)))))

;; ---------------------------------------------------------------------------
;; Pause button + detail pane updates
;; ---------------------------------------------------------------------------

(defn- refresh-pause-btn!
  [^js btn]
  (let [paused? (recorder/paused?)]
    (set! (.-textContent btn) (if paused? "Resume" "Pause"))
    (if paused?
      (.add    (.-classList btn) "paused")
      (.remove (.-classList btn) "paused"))))

(defn- refresh-detail!
  [^js detail-el ^js splitter-el ^js record]
  (if record
    (do
      (set! (.-textContent detail-el) (js/JSON.stringify record nil 2))
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
        rec    (find-record-by-id recs sel-id)]
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
        sel-rec         (effective-selection! el recs spec)
        sel-id          (when sel-rec (.-id sel-rec))]
    (render-timeline! lanes-el svg-pane-el filtered lanes bounds sel-id)
    (set! (.-textContent count-el) (str cnt-filtered))
    (set! (.-textContent hint-el)
          (model/timeline-hint cnt-filtered cnt-total bounds (count lanes)))
    (refresh-detail! detail-el splitter-el sel-rec)
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

(defn- handle-click!
  [^js el ^js e]
  (let [^js target (.-target e)]
    (on-action-click! el target)
    (on-dot-click!    el target)))

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

(defn- show-tooltip!
  [^js tooltip-el ^js timeline-el ^js circle ^js e ^js record]
  (set! (.-textContent tooltip-el) (model/tooltip-text record))
  (.removeAttribute tooltip-el "hidden")
  (let [tl-rect (.getBoundingClientRect timeline-el)
        x       (- (.-clientX e) (.-left tl-rect))
        y       (- (.-clientY e) (.-top  tl-rect))
        scroll-top  (.-scrollTop  timeline-el)
        scroll-left (.-scrollLeft timeline-el)]
    ;; Add scroll offsets so the tooltip stays anchored to the dot when
    ;; the timeline is scrolled. Position offsets keep the cursor uncovered.
    (set! (.. tooltip-el -style -left) (str (+ x scroll-left 12) "px"))
    (set! (.. tooltip-el -style -top)  (str (+ y scroll-top 12)  "px"))
    ;; circle is referenced so the linter doesn't complain; future PRs may
    ;; pin tooltip to the dot's SVG coords rather than the cursor.
    (when circle nil)))

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
            r            (find-record-by-id recs id)]
        (when r
          (show-tooltip! tooltip timeline target e r)))
      (hide-tooltip! ^js (gobj/get el model/k-tooltip-el)))))

(defn- handle-pointerleave!
  [^js el _e]
  (hide-tooltip! ^js (gobj/get el model/k-tooltip-el)))

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
  "Build shadow root, inject skeleton + style, return the shadow root."
  [^js el]
  (let [^js shadow (.attachShadow el #js {:mode "open"})
        ^js style  (.createElement js/document "style")]
    (set! (.-textContent style) model/dock-css)
    (.appendChild shadow style)
    (let [^js wrap (.createElement js/document "div")]
      (set! (.-innerHTML wrap) (skeleton-html))
      (while (.-firstChild wrap)
        (.appendChild shadow (.-firstChild wrap))))
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
  (gobj/set el model/k-hint-el     (.querySelector shadow "[data-x-th-hint]"))
  (gobj/set el model/k-pause-btn   (.querySelector shadow "[data-x-th-action='pause']")))

(defn- bind-listeners!
  [^js el ^js shadow]
  (let [^js dock        (.querySelector shadow ".dock")
        ^js timeline-el (gobj/get el model/k-timeline-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js detail-el   (gobj/get el model/k-detail-el)]
    (.addEventListener dock "click"  (fn [^js e] (handle-click!  el e)))
    (.addEventListener dock "change" (fn [^js e] (handle-change! el e)))
    (.addEventListener timeline-el "pointermove"
                       (fn [^js e] (handle-pointermove! el e)))
    (.addEventListener timeline-el "pointerleave"
                       (fn [^js e] (handle-pointerleave! el e)))
    (.addEventListener splitter-el "pointerdown"
                       (fn [^js e] (start-resize! detail-el splitter-el e)))))

(defn- mount!
  [^js el]
  (when-not (gobj/get el model/k-mounted)
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
  "Single entry point: install recorder hooks (idempotent), define the
   custom element (idempotent), and auto-mount the dock if activation is on.
   Defers mounting to DOMContentLoaded if document.body is not yet available."
  []
  (recorder/register!)
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  (when (model/enabled? js/window)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!))))
