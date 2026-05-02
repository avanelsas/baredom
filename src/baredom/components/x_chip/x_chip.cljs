(ns baredom.components.x-chip.x-chip
  (:require
[baredom.utils.component :as component]
            [baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-chip.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xChipRefs")
(def ^:private k-handlers "__xChipHandlers")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
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
    (du/set-attr! container  "part" "container")
    (du/set-attr! label-el   "part" "label")
    (du/set-attr! remove-btn "part" "remove")
    (du/set-attr! remove-btn "type" "button")
    (du/set-attr! remove-btn "aria-label" "Remove")
    (du/set-attr! remove-x   "aria-hidden" "true")
    (set! (.-textContent remove-x) "\u00D7")
    (.appendChild remove-btn remove-x)
    (.appendChild container label-el)
    (.appendChild container remove-btn)
    (.appendChild root style)
    (.appendChild root container)
    (gobj/set el k-refs
              {:root       root
               :container  container
               :label-el   label-el
               :remove-btn remove-btn}))
  nil)

;; ── Removal logic ─────────────────────────────────────────────────────────
(defn- dispatch-remove!
  "Dispatches x-chip-remove. Returns true if NOT prevented (proceed), false if cancelled."
  [^js el m]
  (let [detail (model/remove-detail m)
        ev     (js/CustomEvent. model/event-remove
                                #js {:detail    detail
                                     :bubbles   true
                                     :composed  true
                                     :cancelable true})]
    (.dispatchEvent el ev)
    (not (.-defaultPrevented ev))))

(defn- do-exit!
  "Starts exit animation. Removes element after animationend or 400ms fallback."
  [^js el]
  (let [timeout-id (js/setTimeout
                    (fn [] (.remove el))
                    400)
        on-end     (fn on-end []
                     (js/clearTimeout timeout-id)
                     (.removeEventListener el "animationend" on-end)
                     (.remove el))]
    (.addEventListener el "animationend" on-end)
    (.setAttribute el "data-exiting" "")))

(defn- try-remove!
  "Checks eligibility, fires event, starts exit if not cancelled."
  [^js el]
  (let [m (model/normalize
           {:label-raw       (.getAttribute el model/attr-label)
            :value-raw       (.getAttribute el model/attr-value)
            :removable-raw   (.getAttribute el model/attr-removable)
            :disabled-present? (.hasAttribute el model/attr-disabled)})]
    (when (model/removal-eligible? m)
      (when (dispatch-remove! el m)
        (do-exit! el)))))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [m (model/normalize
           {:label-raw         (.getAttribute el model/attr-label)
            :value-raw         (.getAttribute el model/attr-value)
            :removable-raw     (.getAttribute el model/attr-removable)
            :disabled-present? (.hasAttribute el model/attr-disabled)})
        {:keys [label removable? disabled?]} m
        refs       (gobj/get el k-refs)
        ^js label-el   (:label-el refs)
        ^js remove-btn (:remove-btn refs)
        eligible       (model/removal-eligible? m)]

    ;; Label text
    (when label-el
      (set! (.-textContent label-el) label))

    ;; data-removable drives CSS visibility of remove button
    (if removable?
      (.setAttribute el "data-removable" "")
      (.removeAttribute el "data-removable"))

    ;; tabIndex — focusable only when removable and not disabled
    (set! (.-tabIndex el) (if eligible 0 -1))

    ;; aria-disabled
    (if disabled?
      (.setAttribute el "aria-disabled" "true")
      (.removeAttribute el "aria-disabled"))

    ;; aria-keyshortcuts
    (if eligible
      (.setAttribute el "aria-keyshortcuts" "Backspace Delete")
      (.removeAttribute el "aria-keyshortcuts"))

    ;; Disable remove button when disabled
    (when remove-btn
      (if disabled?
        (.setAttribute remove-btn "disabled" "")
        (.removeAttribute remove-btn "disabled"))))
  nil)

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs       (gobj/get el k-refs)
        ^js remove-btn (:remove-btn refs)
        on-click   (fn [^js _ev] (try-remove! el))
        on-keydown (fn [^js ev]
                     (let [key (.-key ev)]
                       (when (or (= key "Backspace") (= key "Delete"))
                         (.preventDefault ev)
                         (try-remove! el))))]
    (when remove-btn
      (.addEventListener remove-btn "click" on-click))
    (.addEventListener el "keydown" on-keydown)
    (gobj/set el k-handlers
              #js {:click   on-click
                   :keydown on-keydown}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (let [refs       (gobj/get el k-refs)
          on-click   (gobj/get hs "click")
          on-keydown (gobj/get hs "keydown")]
      (when (and refs on-click)
        (let [^js remove-btn (:remove-btn refs)]
          (when remove-btn
            (.removeEventListener remove-btn "click" on-click))))
      (when on-keydown
        (.removeEventListener el "keydown" on-keydown))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs) (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-refs)
      (render! el))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- def-string-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-bool-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr "")
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-string-prop! proto model/attr-label)
  (def-string-prop! proto model/attr-value)
  (def-bool-prop!   proto model/attr-removable)
  (def-bool-prop!   proto model/attr-disabled))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
