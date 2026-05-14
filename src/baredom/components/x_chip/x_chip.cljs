(ns baredom.components.x-chip.x-chip
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-chip.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xChipRefs")
(def ^:private k-model    "__xChipModel")
(def ^:private k-handlers "__xChipHandlers")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part              "part")
(def ^:private attr-type              "type")
(def ^:private attr-disabled          "disabled")
(def ^:private attr-aria-label        "aria-label")
(def ^:private attr-aria-hidden       "aria-hidden")
(def ^:private attr-aria-disabled     "aria-disabled")
(def ^:private attr-aria-keyshortcuts "aria-keyshortcuts")
(def ^:private attr-data-removable    "data-removable")
(def ^:private attr-data-exiting      "data-exiting")

(def ^:private part-container "container")
(def ^:private part-label     "label")
(def ^:private part-remove    "remove")

(def ^:private val-true              "true")
(def ^:private val-button            "button")
(def ^:private val-remove-label      "Remove")
(def ^:private val-remove-glyph      "×")
(def ^:private val-remove-shortcuts  "Backspace Delete")

(def ^:private ev-click        "click")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-animationend "animationend")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

(def ^:private key-backspace "Backspace")
(def ^:private key-delete    "Delete")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "--x-chip-bg:var(--x-color-surface,rgba(0,0,0,0.08));"
   "--x-chip-color:var(--x-color-text,rgba(0,0,0,0.80));"
   "--x-chip-border:var(--x-color-border,rgba(0,0,0,0.12));"
   "--x-chip-radius:var(--x-radius-full, 9999px);"
   "--x-chip-font-size:var(--x-font-size-sm, 0.875rem);"
   "--x-chip-padding-x:0.625rem;"
   "--x-chip-padding-y:0.25rem;"
   "--x-chip-remove-size:1rem;"
   "--x-chip-exit-duration:300ms;"
   "display:inline-flex;"
   "align-items:center;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "font-family:inherit;"
   "font-size:var(--x-chip-font-size);"
   "line-height:1;"
   "border-radius:var(--x-chip-radius);"
   "background:var(--x-chip-bg);"
   "border:1px solid var(--x-chip-border);"
   "color:var(--x-chip-color);"
   "padding:var(--x-chip-padding-y) var(--x-chip-padding-x);"
   "gap:0.25rem;"
   "box-sizing:border-box;"
   "cursor:default;"
   "user-select:none;"
   "outline:none;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-chip-bg:var(--x-color-surface,rgba(255,255,255,0.10));"
   "--x-chip-border:var(--x-color-border,rgba(255,255,255,0.16));"
   "--x-chip-color:var(--x-color-text,rgba(255,255,255,0.88));}}"

   ":host(:focus-visible){"
   "outline:2px solid var(--x-color-focus-ring,currentColor);"
   "outline-offset:2px;}"

   ":host([disabled]){"
   "opacity:0.55;"
   "pointer-events:none;}"

   "[part=container]{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:0.25rem;}"

   "[part=label]{"
   "display:inline;"
   "font-size:inherit;"
   "color:inherit;}"

   "[part=remove]{"
   "display:none;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-chip-remove-size);"
   "height:var(--x-chip-remove-size);"
   "border:none;"
   "background:none;"
   "color:inherit;"
   "cursor:pointer;"
   "padding:0;"
   "border-radius:50%;"
   "font-size:1rem;"
   "line-height:1;"
   "opacity:0.7;"
   "flex-shrink:0;}"

   "[part=remove]:hover{opacity:1;}"
   "[part=remove]:focus-visible{"
   "outline:2px solid currentColor;"
   "outline-offset:1px;}"

   ":host([data-removable]) [part=remove]{"
   "display:inline-flex;}"

   "@keyframes x-chip-exit{"
   "to{opacity:0;transform:scale(0.85);}}"

   ":host([data-exiting]){"
   "animation:x-chip-exit var(--x-chip-exit-duration) forwards;}"

   "@media (prefers-reduced-motion:reduce){"
   ":host([data-exiting]){"
   "animation-duration:0ms;}}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────
(defn- make-el [tag] (.createElement js/document tag))
;; ── Shadow DOM creation ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [^js root       (.attachShadow el #js {:mode "open"})
        ^js style      (make-el "style")
        ^js container  (make-el "span")
        ^js label-el   (make-el "span")
        ^js remove-btn (make-el "button")
        ^js remove-x   (make-el "span")]
    (set! (.-textContent style) style-text)
    (du/set-attr! container  attr-part        part-container)
    (du/set-attr! label-el   attr-part        part-label)
    (du/set-attr! remove-btn attr-part        part-remove)
    (du/set-attr! remove-btn attr-type        val-button)
    (du/set-attr! remove-btn attr-aria-label  val-remove-label)
    (du/set-attr! remove-x   attr-aria-hidden val-true)
    (set! (.-textContent remove-x) val-remove-glyph)
    (.appendChild remove-btn remove-x)
    (.appendChild container label-el)
    (.appendChild container remove-btn)
    (.appendChild root style)
    (.appendChild root container)
    (du/setv! el k-refs
              {:root       root
               :container  container
               :label-el   label-el
               :remove-btn remove-btn})))

;; ── Removal logic ─────────────────────────────────────────────────────────
(defn- dispatch-remove!
  "Dispatches x-chip-remove. Returns true if NOT prevented (proceed), false if cancelled."
  [^js el m]
  (du/dispatch-cancelable! el model/event-remove (model/remove-detail m)))

(defn- do-exit!
  "Starts exit animation. Removes element after animationend or 400ms fallback."
  [^js el]
  (let [timeout-id (js/setTimeout
                    (fn on-exit-fallback [] (.remove el))
                    400)]
    (letfn [(on-end []
              (js/clearTimeout timeout-id)
              (.removeEventListener el ev-animationend on-end)
              (.remove el))]
      (.addEventListener el ev-animationend on-end)
      (du/set-attr! el attr-data-exiting ""))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:label-raw         (du/get-attr el model/attr-label)
    :value-raw         (du/get-attr el model/attr-value)
    :removable-raw     (du/get-attr el model/attr-removable)
    :disabled-present? (du/has-attr? el model/attr-disabled)}))

(defn- current-model [^js el]
  (or (du/getv el k-model) (read-model el)))

(defn- try-remove!
  "Checks eligibility, fires event, starts exit if not cancelled."
  [^js el]
  (let [m (current-model el)]
    (when (model/removal-eligible? m)
      (when (dispatch-remove! el m)
        (do-exit! el)))))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el m]
  (let [{:keys [label removable? disabled?]} m
        refs           (du/getv el k-refs)
        ^js label-el   (:label-el refs)
        ^js remove-btn (:remove-btn refs)
        eligible       (model/removal-eligible? m)]
    (when label-el
      (set! (.-textContent label-el) label))
    (if removable?
      (du/set-attr! el attr-data-removable "")
      (du/remove-attr! el attr-data-removable))
    (set! (.-tabIndex el) (if eligible 0 -1))
    (if disabled?
      (du/set-attr! el attr-aria-disabled val-true)
      (du/remove-attr! el attr-aria-disabled))
    (if eligible
      (du/set-attr! el attr-aria-keyshortcuts val-remove-shortcuts)
      (du/remove-attr! el attr-aria-keyshortcuts))
    (when remove-btn
      (if disabled?
        (.setAttribute remove-btn attr-disabled "")
        (.removeAttribute remove-btn attr-disabled)))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- on-remove-click [^js el ^js _ev]
  (try-remove! el))

(defn- on-host-keydown [^js el ^js ev]
  (let [k (.-key ev)]
    (when (or (= k key-backspace) (= k key-delete))
      (.preventDefault ev)
      (try-remove! el))))

(defn- add-listeners! [^js el]
  (let [refs           (du/getv el k-refs)
        ^js remove-btn (:remove-btn refs)
        on-click       (fn handle-remove-click [ev] (on-remove-click el ev))
        on-keydown     (fn handle-host-keydown [ev] (on-host-keydown el ev))
        handlers       #js {}]
    (when remove-btn
      (.addEventListener remove-btn ev-click on-click))
    (.addEventListener el ev-keydown on-keydown)
    (gobj/set handlers hk-click   on-click)
    (gobj/set handlers hk-keydown on-keydown)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (let [refs       (du/getv el k-refs)
          on-click   (gobj/get hs hk-click)
          on-keydown (gobj/get hs hk-keydown)]
      (when (and refs on-click)
        (let [^js remove-btn (:remove-btn refs)]
          (when remove-btn
            (.removeEventListener remove-btn ev-click on-click))))
      (when on-keydown
        (.removeEventListener el ev-keydown on-keydown))))
  (du/setv! el k-handlers nil))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs) (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
