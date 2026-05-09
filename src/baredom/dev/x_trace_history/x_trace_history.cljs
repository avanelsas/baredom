(ns baredom.dev.x-trace-history.x-trace-history
  "Dev-only floating dock for x-trace-history.

   When activated (?baredom-trace-history or window.BAREDOM_TRACE_HISTORY),
   register! installs the recorder hooks (via recorder/register!), defines a
   `<x-trace-history>` custom element, and auto-mounts one to <body>. The
   dock subscribes to recorder updates and renders a flat list of records
   (newest first) with click-to-expand JSON detail and tag/category filters."
  (:require
   [goog.object :as gobj]
   [baredom.dev.x-debug-registry :as registry]
   [baredom.dev.x-trace-history.model :as model]
   [baredom.dev.x-trace-history.recorder :as recorder]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private max-rows 200)

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
  "Static dock skeleton. The list is populated dynamically by render!."
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
       "<div class='list' data-x-th-list></div>"
       "<div class='splitter' data-x-th-splitter hidden></div>"
       "<div class='detail' data-x-th-detail hidden></div>"
       "<div class='hint' data-x-th-hint></div>"
       "</div>"))

;; ---------------------------------------------------------------------------
;; Row + list rendering
;; ---------------------------------------------------------------------------

(defn- row-html
  [^js r selected-id]
  (let [type-str  (.-type r)
        cat       (model/categorize-type type-str)
        cat-class (str "cat-" (name cat))
        sel-class (if (= (.-id r) selected-id) " selected" "")]
    (str "<div class='row " cat-class sel-class "' data-x-th-id='" (.-id r) "'>"
         "<div class='t'>"   (escape-html (model/format-timestamp (.-t r))) "</div>"
         "<div class='tag'>" (escape-html (.-tag r)) "</div>"
         "<div class='body'>"
         "<div class='type'>"    (escape-html type-str)                  "</div>"
         "<div class='preview'>" (escape-html (model/payload-preview r)) "</div>"
         "</div>"
         "</div>")))

(defn- rows-html
  [rows selected-id]
  (if (empty? rows)
    "<div class='empty'>No records yet — interact with components to start tracing.</div>"
    (apply str (map #(row-html % selected-id) rows))))

(defn- find-record-by-id
  "Return the record with id `id`, or nil if not found / id non-numeric.
   Delegates to Array.prototype.find for short-circuiting linear search."
  [^js records id]
  (when (number? id)
    (.find records (fn [^js r] (= id (.-id r))))))

;; ---------------------------------------------------------------------------
;; Pause button + detail pane + hint updates
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

(defn- format-hint-text
  [cnt]
  (cond
    (> cnt max-rows) (str "Showing " max-rows " of " cnt)
    (= cnt 1)        "1 record"
    :else            (str cnt " records")))

(defn- effective-selection!
  "Return the currently-selected record IFF it still passes the active
   filter; otherwise clear the dock's selected-id and return nil. Reading
   the selection through this helper is what keeps the detail pane in sync
   with filter changes — selecting a row, then filtering it out, drops the
   detail rather than leaving an orphan record visible."
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
  "Repaint the list, count, hint, detail pane, and pause-button state from
   current recorder + filter + selection state."
  [^js el]
  (let [^js list-el     (gobj/get el model/k-list-el)
        ^js count-el    (gobj/get el model/k-count-el)
        ^js detail-el   (gobj/get el model/k-detail-el)
        ^js splitter-el (gobj/get el model/k-splitter-el)
        ^js hint-el     (gobj/get el model/k-hint-el)
        ^js pause-btn   (gobj/get el model/k-pause-btn)
        spec            (gobj/get el model/k-filter)
        ^js recs        (recorder/records)
        filtered        (model/filter-records recs spec)
        cnt             (count filtered)
        start           (max 0 (- cnt max-rows))
        visible         (vec (rseq (subvec filtered start cnt)))
        sel-rec         (effective-selection! el recs spec)
        sel-id          (when sel-rec (.-id sel-rec))]
    (set! (.-innerHTML list-el) (rows-html visible sel-id))
    (set! (.-textContent count-el) (str cnt))
    (set! (.-textContent hint-el) (format-hint-text cnt))
    (refresh-detail! detail-el splitter-el sel-rec)
    (refresh-pause-btn! pause-btn)))

;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------

(defn- on-row-click!
  [^js el ^js target]
  (when-let [^js row (.closest target ".row")]
    (let [id-str (.getAttribute row "data-x-th-id")
          id     (when id-str (js/parseInt id-str 10))
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
    (on-row-click!    el target)))

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

(defn- mount!
  [^js el]
  (when-not (gobj/get el model/k-mounted)
    (let [^js shadow      (attach-skeleton! el)
          ^js list-el     (.querySelector shadow "[data-x-th-list]")
          ^js count-el    (.querySelector shadow "[data-x-th-count]")
          ^js detail-el   (.querySelector shadow "[data-x-th-detail]")
          ^js splitter-el (.querySelector shadow "[data-x-th-splitter]")
          ^js hint-el     (.querySelector shadow "[data-x-th-hint]")
          ^js pause-btn   (.querySelector shadow "[data-x-th-action='pause']")
          ^js dock        (.querySelector shadow ".dock")]
      (gobj/set el model/k-shadow shadow)
      (gobj/set el model/k-list-el list-el)
      (gobj/set el model/k-count-el count-el)
      (gobj/set el model/k-detail-el detail-el)
      (gobj/set el model/k-splitter-el splitter-el)
      (gobj/set el model/k-hint-el hint-el)
      (gobj/set el model/k-pause-btn pause-btn)
      (gobj/set el model/k-filter
                {:tag nil :categories (set model/all-categories)})
      (gobj/set el model/k-selected-id nil)
      (.addEventListener dock "click"  (fn [^js e] (handle-click!  el e)))
      (.addEventListener dock "change" (fn [^js e] (handle-change! el e)))
      (.addEventListener splitter-el "pointerdown"
                         (fn [^js e] (start-resize! detail-el splitter-el e)))
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
