(ns baredom.components.x-drop-zone.x-drop-zone
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-drop-zone.model :as model]
   [baredom.components.x-drag-panel.model :as panel-model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-initialized? "__xDropZoneInitialized")
(def ^:private k-refs         "__xDropZoneRefs")
(def ^:private k-model        "__xDropZoneModel")
(def ^:private k-handlers     "__xDropZoneHandlers")
(def ^:private k-hover        "__xDropZoneHover")
(def ^:private k-last-drop    "__xDropZoneLastDrop")
(def ^:private k-reservation  "__xDropZoneReservation")
(def ^:private k-rects        "__xDropZoneRects")
(def ^:private k-observer     "__xDropZoneObserver")

;; ── Refs keys ────────────────────────────────────────────────────────────────
(def ^:private rk-zone  "zone")
(def ^:private rk-caret "caret")
(def ^:private rk-empty "empty")
(def ^:private rk-slot  "slot")
(def ^:private rk-live  "live")

;; ── Handler keys ─────────────────────────────────────────────────────────────
(def ^:private hk-slot-change "slotChange")

;; ── String-literal constants (no duplication; Closure-Advanced safe) ─────────
(def ^:private ev-slotchange "slotchange")

(def ^:private attr-part        "part")
(def ^:private attr-class       "class")
(def ^:private attr-name        "name")
(def ^:private attr-hidden      "hidden")
(def ^:private attr-role        "role")
(def ^:private attr-aria-label  "aria-label")
(def ^:private attr-aria-busy   "aria-busy")
(def ^:private attr-aria-live   "aria-live")

(def ^:private tag-div  "div")
(def ^:private tag-span "span")
(def ^:private tag-slot "slot")

(def ^:private slot-empty "empty")

(def ^:private role-group    "group")
(def ^:private value-true    "true")
(def ^:private value-polite  "polite")
(def ^:private class-sr-only "sr-only")

(def ^:private style-top       "top")
(def ^:private style-left      "left")
(def ^:private style-right     "right")
(def ^:private style-bottom    "bottom")
(def ^:private style-width     "width")
(def ^:private style-height    "height")
(def ^:private px "px")

(def ^:private flex-row "row")

(def ^:private selector-child-panel (str ":scope > " panel-model/tag-name))
(def ^:private media-reduced-motion "(prefers-reduced-motion: reduce)")

;; Duration of the animate-moves FLIP. A model constant rather than a CSS custom
;; property: the animation runs through the Web Animations API, which never
;; touches inline styles, and reading a duration back out of the cascade to feed
;; it would be a worse trade than one documented number.
(def ^:private move-duration-ms 180)

;; ── CSS custom property names ────────────────────────────────────────────────
(def ^:private css-bg             "--x-drop-zone-bg")
(def ^:private css-border         "--x-drop-zone-border")
(def ^:private css-radius         "--x-drop-zone-radius")
(def ^:private css-padding        "--x-drop-zone-padding")
(def ^:private css-gap            "--x-drop-zone-gap")
(def ^:private css-min-height     "--x-drop-zone-min-height")
(def ^:private css-over-bg        "--x-drop-zone-over-bg")
(def ^:private css-over-border    "--x-drop-zone-over-border")
(def ^:private css-reject-bg      "--x-drop-zone-reject-bg")
(def ^:private css-reject-border  "--x-drop-zone-reject-border")
(def ^:private css-caret-color    "--x-drop-zone-caret-color")
(def ^:private css-caret-size     "--x-drop-zone-caret-size")
(def ^:private css-busy-bg        "--x-drop-zone-busy-bg")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:block;box-sizing:border-box;}"
   ":host([hidden]){display:none;}"

   ".sr-only{"
   "position:absolute;width:1px;height:1px;padding:0;margin:-1px;"
   "overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap;border:0;}"

   "[part=zone]{"
   "position:relative;"
   "box-sizing:border-box;"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(" css-gap ",.5rem);"
   "min-height:var(" css-min-height ",4rem);"
   "padding:var(" css-padding ",.5rem);"
   "background:var(" css-bg ",var(--x-color-surface,#fafafa));"
   "border:var(" css-border ",1px solid var(--x-color-border,#e2e2e2));"
   "border-radius:var(" css-radius ",var(--x-radius-md,8px));"
   "transition:background .16s ease,border-color .16s ease;}"

   ":host([data-drag-state=over]) [part=zone]{"
   "background:var(" css-over-bg ",var(--x-color-accent-soft,#eef4ff));"
   "border-color:var(" css-over-border ",var(--x-color-accent,#3b82f6));}"

   ":host([data-drag-state=reject]) [part=zone]{"
   "background:var(" css-reject-bg ",var(--x-color-danger-soft,#fdeeee));"
   "border-color:var(" css-reject-border ",var(--x-color-danger,#dc2626));}"

   ":host([pending]) [part=zone]{"
   "background:var(" css-busy-bg ",var(--x-color-accent-soft,#eef4ff));}"

   "[part=caret]{"
   "position:absolute;"
   "border-radius:999px;"
   "background:var(" css-caret-color ",var(--x-color-accent,#3b82f6));"
   "pointer-events:none;}"

   "[part=caret][hidden]{display:none;}"

   ":host([pending]) [part=caret]{animation:x-drop-zone-pulse 1.1s ease-in-out infinite;}"

   "@keyframes x-drop-zone-pulse{"
   "0%,100%{opacity:1;}"
   "50%{opacity:.35;}}"

   "[part=empty]{"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "flex:1;"
   "color:var(--x-color-text-muted,#8a8a8a);"
   "font-size:.875rem;"
   "pointer-events:none;}"

   "[part=empty][hidden]{display:none;}"

   ":host([disabled]) [part=zone]{opacity:.6;}"

   "@media (prefers-reduced-motion: reduce){"
   "[part=zone]{transition:none !important;}"
   ":host([pending]) [part=caret]{animation:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- make-empty-section! []
  (let [wrap (.createElement js/document tag-div)
        slot (.createElement js/document tag-slot)]
    (du/set-attr! wrap attr-part model/part-empty)
    (du/set-attr! slot attr-name slot-empty)
    (.appendChild wrap slot)
    wrap))

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        zone  (.createElement js/document tag-div)
        caret (.createElement js/document tag-div)
        slot  (.createElement js/document tag-slot)
        empty-section (make-empty-section!)
        live  (.createElement js/document tag-span)]
    (set! (.-textContent style) style-text)
    (du/set-attr! zone attr-part model/part-zone)
    (du/set-attr! caret attr-part model/part-caret)
    (du/set-attr! caret attr-hidden "")
    (du/set-attr! live attr-aria-live value-polite)
    (du/set-attr! live attr-class class-sr-only)

    (.appendChild zone caret)
    (.appendChild zone slot)
    (.appendChild zone empty-section)
    (.appendChild root style)
    (.appendChild root zone)
    (.appendChild root live)

    (du/setv! el k-refs #js {"zone"  zone
                             "caret" caret
                             "empty" empty-section
                             "slot"  slot
                             "live"  live})
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

(defn- ref-of [^js el k]
  (when-let [^js refs (du/getv el k-refs)]
    (gobj/get refs k)))

;; ── Children and measurement ─────────────────────────────────────────────────
(defn- panel-children [^js el]
  (vec (array-seq (.querySelectorAll el selector-child-panel))))

(defn- without-panel [children ^js panel]
  (into [] (remove (fn same-panel? [^js c] (identical? c panel))) children))

(defn- flow-vertical?
  "True when the zone lays its panels out on the vertical axis.

  Read from the computed flex-direction rather than assumed, so an author who
  restyles the zone into a row gets a caret on the matching axis."
  [^js el]
  (let [^js zone (ref-of el rk-zone)]
    (if zone
      (not (.startsWith (.-flexDirection (js/getComputedStyle zone)) flex-row))
      true)))

(defn- child-spans [children vertical?]
  (mapv (fn span-of [^js c]
          (let [^js r (.getBoundingClientRect c)]
            (if vertical?
              {:start (.-top r)  :end (.-bottom r)}
              {:start (.-left r) :end (.-right r)})))
        children))

(defn- content-bounds [^js el vertical?]
  (let [^js zone (ref-of el rk-zone)
        ^js r    (.getBoundingClientRect zone)]
    (if vertical?
      {:start (.-top r)  :end (.-bottom r)}
      {:start (.-left r) :end (.-right r)})))

;; ── Live region ──────────────────────────────────────────────────────────────
(defn- announce! [^js el text]
  (when-let [^js live (ref-of el rk-live)]
    (set! (.-textContent live) text)))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:value-raw              (du/get-attr el model/attr-value)
    :accepts-raw            (du/get-attr el model/attr-accepts)
    :max-raw                (du/get-attr el model/attr-max)
    :label-raw              (du/get-attr el model/attr-label)
    :disabled-present?      (du/has-attr? el model/attr-disabled)
    :pending-present?       (du/has-attr? el model/attr-pending)
    :pending-index-raw      (du/get-attr el model/attr-pending-index)
    :animate-moves-present? (du/has-attr? el model/attr-animate-moves)}))

(defn- current-model [^js el]
  (or (du/getv el k-model) (read-model el)))

;; ── Caret ────────────────────────────────────────────────────────────────────
(defn- hide-caret! [^js el]
  (when-let [^js caret (ref-of el rk-caret)]
    (du/set-attr! caret attr-hidden "")))

(defn- show-caret-at! [^js el index ^js exclude-panel]
  (let [^js caret  (ref-of el rk-caret)
        vertical?  (flow-vertical? el)
        children   (without-panel (panel-children el) exclude-panel)
        spans      (child-spans children vertical?)
        bounds     (content-bounds el vertical?)
        boundaries (model/boundary-offsets spans (:start bounds) (:end bounds))
        offset     (model/caret-offset boundaries index)]
    (when (and caret offset)
      (let [local (- offset (:start bounds))
            ^js s (.-style caret)]
        (if vertical?
          (do (.setProperty s style-left "0")
              (.setProperty s style-right "0")
              (.setProperty s style-height (str "var(" css-caret-size ",3px)"))
              (.removeProperty s style-width)
              (.removeProperty s style-bottom)
              (.setProperty s style-top (str local px)))
          (do (.setProperty s style-top "0")
              (.setProperty s style-bottom "0")
              (.setProperty s style-width (str "var(" css-caret-size ",3px)"))
              (.removeProperty s style-height)
              (.removeProperty s style-right)
              (.setProperty s style-left (str local px))))
        (du/remove-attr! caret attr-hidden)))))

(defn- refresh-caret!
  "The caret has two independent sources — a live drag hovering the zone, and an
  author-set pending reservation. Both write the same element, so the decision
  lives in one place. A live hover wins: it is the thing the user is doing now."
  [^js el]
  (let [hover (du/getv el k-hover)
        m     (current-model el)]
    (cond
      (and hover (:accepted? hover))
      (show-caret-at! el (:index hover) (:panel hover))

      (and (:pending? m) (:pending-index m))
      (show-caret-at! el (:pending-index m) nil)

      :else
      (hide-caret! el))))

;; ── Empty state ──────────────────────────────────────────────────────────────
(defn- refresh-empty! [^js el]
  (when-let [^js wrap (ref-of el rk-empty)]
    (if (seq (panel-children el))
      (du/set-attr! wrap attr-hidden "")
      (du/remove-attr! wrap attr-hidden))))

;; ── animate-moves (FLIP) ─────────────────────────────────────────────────────
(defn- capture-rects! [^js el]
  (let [m (js/Map.)]
    (doseq [^js child (panel-children el)]
      (.set m child (.getBoundingClientRect child)))
    ;; Bookkeeping: geometry snapshot with no diagnostic display value.
    (du/setv-untraced! el k-rects m)))

(defn- reduced-motion? []
  (.-matches (js/matchMedia media-reduced-motion)))

(defn- animate-child! [^js child dx dy]
  ;; Web Animations rather than inline styles: a FLIP driven through the style
  ;; attribute would leave the component writing to the author's light DOM.
  (.animate child
            #js [#js {"transform" (str "translate(" dx px "," dy px ")")}
                 #js {"transform" "none"}]
            #js {"duration" move-duration-ms "easing" "ease"}))

(defn- flip! [^js el]
  (when-let [^js prev (du/getv el k-rects)]
    (doseq [^js child (panel-children el)]
      (when-let [^js old-rect (.get prev child)]
        (let [^js new-rect (.getBoundingClientRect child)
              dx           (- (.-left old-rect) (.-left new-rect))
              dy           (- (.-top old-rect) (.-top new-rect))]
          (when (or (not= 0 dx) (not= 0 dy))
            (animate-child! child dx dy))))))
  (capture-rects! el))

(defn- stop-observer! [^js el]
  (when-let [^js obs (du/getv el k-observer)]
    (.disconnect obs))
  (du/setv! el k-observer nil))

(defn- start-observer! [^js el]
  (when-not (du/getv el k-observer)
    (let [obs (js/MutationObserver.
               (fn on-children-changed [_records]
                 (when-not (reduced-motion?)
                   (flip! el))
                 (refresh-empty! el)))]
      (.observe obs el #js {"childList" true})
      (du/setv! el k-observer obs)
      (capture-rects! el))))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-host-aria! [^js el {:keys [label pending?]}]
  (du/set-attr! el attr-role role-group)
  (du/set-attr! el attr-aria-label label)
  (if pending?
    (du/set-attr! el attr-aria-busy value-true)
    (du/remove-attr! el attr-aria-busy)))

(defn- apply-observer! [^js el {:keys [animate-moves?]}]
  (if animate-moves?
    (start-observer! el)
    (stop-observer! el)))

(defn- announce-resolution!
  "Narrate a pending window closing. Whether the move succeeded is read from the
  DOM — the dropped panel is either a child of this zone now or it is not —
  rather than assumed, so a server that refused is reported honestly."
  [^js el]
  (let [m         (current-model el)
        last-drop (du/getv el k-last-drop)
        ^js panel (:panel last-drop)
        landed?   (and panel
                       (some (fn is-panel? [^js c] (identical? c panel))
                             (panel-children el)))
        what      (or (:label last-drop) "Item")]
    (announce! el (if landed?
                    (str what " moved to " (:label m))
                    (str "Move of " what " failed")))
    (du/setv! el k-last-drop nil)))

(defn- announce-pending-change! [^js el old-m new-m]
  (let [was? (boolean (:pending? old-m))
        now? (boolean (:pending? new-m))]
    (cond
      (and (not was?) now?)
      (let [what (or (:label (du/getv el k-last-drop)) "Item")]
        (announce! el (str "Moving " what " to " (:label new-m))))

      (and was? (not now?))
      (announce-resolution! el)

      :else nil)))

(defn- apply-model! [^js el old-m new-m]
  (ensure-shadow!  el)
  (apply-host-aria! el new-m)
  (apply-observer!  el new-m)
  (du/setv! el k-model new-m)
  (refresh-caret! el)
  (refresh-empty! el)
  (when old-m (announce-pending-change! el old-m new-m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el old-m new-m))))

;; ── Hover state ──────────────────────────────────────────────────────────────
(defn- apply-drag-state! [^js el accepted?]
  (du/set-attr! el model/attr-drag-state
                (if accepted? model/state-over model/state-reject)))

(defn- clear-drag-state! [^js el]
  (du/remove-attr! el model/attr-drag-state))

(defn- panel-kind [^js panel]
  (or (du/get-attr panel panel-model/attr-kind) ""))

(defn- panel-value [^js panel]
  (or (du/get-attr panel panel-model/attr-value) ""))

(defn- panel-label [^js panel]
  (panel-model/normalize-label (du/get-attr panel panel-model/attr-label)))

;; ── Public API used by x-drag-panel ──────────────────────────────────────────
(defn accepts-panel?
  "True when this zone would accept `panel`.

  A panel already living in this zone is excluded from the capacity count —
  otherwise a full zone would refuse to let its own panels be reordered."
  [^js el ^js panel]
  (ensure-shadow! el)
  (let [m (current-model el)]
    (model/accepts? (assoc m
                           :kind        (panel-kind panel)
                           :child-count (count (without-panel (panel-children el)
                                                              panel))))))

(defn hover!
  "Called by a dragged panel entering this zone. Fires x-drop-zone-enter once,
  for accepted and rejected panels alike, so an app can tell *hovered but
  refused* from *never hovered*."
  [^js el ^js panel]
  (ensure-shadow! el)
  (let [accepted? (accepts-panel? el panel)]
    (du/setv! el k-hover {:panel panel :accepted? accepted? :index 0})
    (apply-drag-state! el accepted?)
    (du/dispatch! el model/event-enter
                  (model/hover-detail (panel-kind panel) (panel-value panel)))
    (refresh-caret! el)))

(defn unhover!
  "Called by a dragged panel leaving this zone, including when it is dropped
  elsewhere."
  [^js el ^js panel]
  (when (du/getv el k-hover)
    (du/setv! el k-hover nil)
    (clear-drag-state! el)
    (du/dispatch! el model/event-leave
                  (model/hover-detail (panel-kind panel) (panel-value panel)))
    (refresh-caret! el)))

(defn index-at
  "Insertion index for a pointer at viewport `(x, y)`, with `panel` excluded
  from the measurement when it already lives here."
  [^js el ^js panel x y]
  (let [vertical? (flow-vertical? el)
        children  (without-panel (panel-children el) panel)
        spans     (child-spans children vertical?)]
    (model/insert-index (model/span-midpoints spans) (if vertical? y x))))

(defn preview!
  "Per-frame update of the caret while a panel hovers. Cheap when nothing moved:
  the index is recomputed but the caret is only repositioned through the same
  single code path as every other caret update."
  [^js el ^js panel x y]
  (when-let [hover (du/getv el k-hover)]
    (when (:accepted? hover)
      (let [index (index-at el panel x y)]
        (when (not= index (:index hover))
          ;; Hot path: rAF-driven — the hovered index is re-derived every frame.
          (du/setv-untraced! el k-hover (assoc hover :index index)))
        (refresh-caret! el)))))

(defn- zone-value
  "The `value` of a zone element, or nil when there is no zone.

  nil and \"\" are different answers: nil means the panel came from outside any
  zone, \"\" means it came from a zone the author gave no identity."
  [^js zone]
  (when zone
    (model/normalize-value (du/get-attr zone model/attr-value))))

(defn- fire-drop! [^js el ^js panel ^js from-zone index]
  (du/setv! el k-last-drop {:panel panel :label (panel-label panel)})
  (du/dispatch! el model/event-drop
                (model/drop-detail (panel-kind panel)
                                   (panel-value panel)
                                   (zone-value from-zone)
                                   (:value (current-model el))
                                   index
                                   panel)))

(defn commit-drop!
  "Announce an accepted pointer drop. The zone never moves the panel — the app
  owns the landing — so this is purely the report plus caret teardown."
  [^js el ^js panel ^js from-zone x y]
  (let [index (index-at el panel x y)]
    (du/setv! el k-hover nil)
    (clear-drag-state! el)
    (fire-drop! el panel from-zone index)
    (refresh-caret! el)))

(defn commit-keyboard-drop!
  "Announce an accepted keyboard drop. A keyboard drop appends: there is no
  pointer position to derive an insertion point from, and cycling positions as
  well as zones would double the key vocabulary."
  [^js el ^js panel ^js from-zone]
  (let [index (count (without-panel (panel-children el) panel))]
    (du/setv! el k-hover nil)
    (clear-drag-state! el)
    (fire-drop! el panel from-zone index)
    (refresh-caret! el)))

;; ── Reservation ──────────────────────────────────────────────────────────────
;; Sugar over the `pending` / `pending-index` attributes, which stay fully
;; author-settable. This does not weaken the rule that matters: the component
;; still never *infers* that a drop was sent. It reserves only when the app
;; calls reserve!, having decided to send it.

(defn- clear-reserved-panel! [^js el]
  (when-let [reservation (du/getv el k-reservation)]
    (when-let [^js panel (:panel reservation)]
      (du/remove-attr! panel panel-model/attr-pending))))

(defn reserve!
  "Mark an in-flight move of `panel` into this zone at `index`: the panel
  renders its footprint, the zone renders the reserved caret and busy tint.

  Replacing a live reservation clears the previous panel first, so a second drop
  cannot strand the first panel in a permanent pending state. Omitting `index`
  gives busy-only, the right rendering for an unordered bucket."
  [^js el ^js panel index]
  (ensure-shadow! el)
  (clear-reserved-panel! el)
  (du/setv! el k-reservation {:panel panel})
  (when panel
    (du/set-attr! panel panel-model/attr-pending ""))
  (if (number? index)
    (du/set-attr! el model/attr-pending-index (str index))
    (du/remove-attr! el model/attr-pending-index))
  (du/set-attr! el model/attr-pending ""))

(defn release!
  "Clear the reservation made by `reserve!`.

  Idempotent, and harmless when the reserved panel was destroyed by a
  confirmation re-render — the replacement never carried `pending` to begin
  with, so there is nothing stale to clear."
  [^js el]
  (clear-reserved-panel! el)
  (du/setv! el k-reservation nil)
  (du/remove-attr! el model/attr-pending)
  (du/remove-attr! el model/attr-pending-index))

(defn announce-candidate!
  "Narrate this zone becoming the keyboard candidate."
  [^js el ^js panel]
  (let [m     (current-model el)
        zones (.querySelectorAll js/document model/tag-name)
        total (.-length zones)
        pos   (inc (.indexOf (js/Array.from zones) el))]
    (announce! el (str (panel-label panel) " over " (:label m)
                       ", zone " pos " of " total))))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-slot-change! [^js el _event]
  (refresh-empty! el)
  (refresh-caret! el))

(def ^:private listener-spec
  [[rk-slot ev-slotchange hk-slot-change false]])

(defn- make-handlers [^js el]
  (let [handlers #js {}]
    (gobj/set handlers hk-slot-change
              (fn handle-slot-change [^js e] (on-slot-change! el e)))
    handlers))

(defn- iter-listeners! [^js el add?]
  (let [^js refs     (du/getv el k-refs)
        ^js handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (doseq [[refs-key event-name handler-key capture?] listener-spec]
        (let [^js target  (gobj/get refs refs-key)
              ^js handler (gobj/get handlers handler-key)]
          (when (and target handler)
            (if add?
              (.addEventListener target event-name handler capture?)
              (.removeEventListener target event-name handler capture?))))))))

(defn- add-listeners! [^js el]
  (du/setv! el k-handlers (make-handlers el))
  (iter-listeners! el true))

(defn- remove-listeners! [^js el]
  (iter-listeners! el false)
  (du/setv! el k-handlers nil))

;; ── Property accessors ───────────────────────────────────────────────────────
;; Tier 1: `max` and `pendingIndex` must read as *absent* when the attribute is
;; missing, and install-properties! coerces an omitted or nil number default to
;; 0 — which would silently close a zone and pin a caret to the top.
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto "value"        model/attr-value "")
  (du/define-string-prop! proto "accepts"      model/attr-accepts "")
  (du/define-number-prop! proto "max"          model/attr-max nil)
  (du/define-string-prop! proto "label"        model/attr-label model/default-label)
  (du/define-bool-prop!   proto "disabled"     model/attr-disabled)
  (du/define-bool-prop!   proto "pending"      model/attr-pending)
  (du/define-number-prop! proto "pendingIndex" model/attr-pending-index nil)
  (du/define-bool-prop!   proto "animateMoves" model/attr-animate-moves)

  (.defineProperty js/Object proto "reserve"
                   #js {:value        (fn [^js panel index]
                                        (this-as ^js this (reserve! this panel index)))
                        :enumerable   true
                        :configurable true
                        :writable     true})

  (.defineProperty js/Object proto "release"
                   #js {:value        (fn [] (this-as ^js this (release! this)))
                        :enumerable   true
                        :configurable true
                        :writable     true}))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (update-from-attrs! el)
  (add-listeners! el)
  (refresh-empty! el))

(defn- disconnected! [^js el]
  (stop-observer! el)
  (remove-listeners! el)
  (du/setv! el k-hover nil))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
