(ns baredom.components.x-divider.x-divider
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-divider.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xDividerRefs")
(def ^:private k-model "__xDividerModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part             "part")
(def ^:private attr-name             "name")
(def ^:private attr-role             "role")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-aria-orientation "aria-orientation")
(def ^:private attr-data-orientation "data-orientation")
(def ^:private attr-data-variant     "data-variant")
(def ^:private attr-data-align       "data-align")

(def ^:private part-container "container")
(def ^:private part-line       "line")
(def ^:private part-line-left  "line-left")
(def ^:private part-line-right "line-right")
(def ^:private part-label      "label")
(def ^:private part-label-text "label-text")
(def ^:private slot-name-label "label")

(def ^:private val-separator    "separator")
(def ^:private val-presentation "presentation")
(def ^:private val-vertical     "vertical")
(def ^:private val-default-thickness "1px")
(def ^:private val-default-inset     "0px")
(def ^:private val-default-length    "auto")

(def ^:private css-var-color     "--x-divider-color")
(def ^:private css-var-thickness "--x-divider-thickness")
(def ^:private css-var-inset     "--x-divider-inset")
(def ^:private css-var-length    "--x-divider-length")

(def ^:private rk-container  "container")
(def ^:private rk-mode       "mode")
(def ^:private rk-line-left  "line-left")
(def ^:private rk-label-wrap "label-wrap")
(def ^:private rk-label-text "label-text")
(def ^:private rk-line-right "line-right")

(def ^:private mode-label    "label")
(def ^:private mode-no-label "no-label")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-divider-color:rgba(0,0,0,0.12);"
   "--x-divider-thickness:1px;"
   "--x-divider-inset:0px;"
   "--x-divider-length:auto;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-divider-color:rgba(255,255,255,0.15);}}"

   "[part=container]{"
   "display:flex;"
   "align-items:center;"
   "width:var(--x-divider-length,auto);"
   "box-sizing:border-box;}"

   ":host([data-orientation='vertical']) [part=container]{"
   "flex-direction:column;"
   "height:var(--x-divider-length,auto);"
   "width:auto;}"

   "[part=line],"
   "[part=line-left],"
   "[part=line-right]{"
   "flex:1;"
   "border:none;"
   "background:var(--x-divider-color,rgba(0,0,0,0.12));"
   "height:var(--x-divider-thickness,1px);"
   "margin:var(--x-divider-inset,0px) 0;}"

   ":host([data-orientation='vertical']) [part=line],"
   ":host([data-orientation='vertical']) [part=line-left],"
   ":host([data-orientation='vertical']) [part=line-right]{"
   "width:var(--x-divider-thickness,1px);"
   "height:auto;"
   "margin:0 var(--x-divider-inset,0px);}"

   ":host([data-variant='dashed']) [part=line],"
   ":host([data-variant='dashed']) [part=line-left],"
   ":host([data-variant='dashed']) [part=line-right]{"
   "background:none;"
   "height:0;"
   "border-top:var(--x-divider-thickness,1px) dashed var(--x-divider-color,rgba(0,0,0,0.12));}"

   ":host([data-orientation='vertical'][data-variant='dashed']) [part=line],"
   ":host([data-orientation='vertical'][data-variant='dashed']) [part=line-left],"
   ":host([data-orientation='vertical'][data-variant='dashed']) [part=line-right]{"
   "border-top:none;"
   "border-left:var(--x-divider-thickness,1px) dashed var(--x-divider-color,rgba(0,0,0,0.12));"
   "width:0;"
   "height:auto;}"

   ":host([data-variant='dotted']) [part=line],"
   ":host([data-variant='dotted']) [part=line-left],"
   ":host([data-variant='dotted']) [part=line-right]{"
   "background:none;"
   "height:0;"
   "border-top:var(--x-divider-thickness,1px) dotted var(--x-divider-color,rgba(0,0,0,0.12));}"

   ":host([data-orientation='vertical'][data-variant='dotted']) [part=line],"
   ":host([data-orientation='vertical'][data-variant='dotted']) [part=line-left],"
   ":host([data-orientation='vertical'][data-variant='dotted']) [part=line-right]{"
   "border-top:none;"
   "border-left:var(--x-divider-thickness,1px) dotted var(--x-divider-color,rgba(0,0,0,0.12));"
   "width:0;"
   "height:auto;}"

   "[part=label]{"
   "display:flex;"
   "align-items:center;"
   "flex-shrink:0;"
   "padding:0 0.75em;"
   "font-size:0.75rem;"
   "font-weight:500;"
   "color:var(--x-divider-color);"
   "white-space:nowrap;}"

   ":host([data-orientation='vertical']) [part=label]{"
   "padding:0.75em 0;"
   "writing-mode:horizontal-tb;}"

   "[part=label-text]{"
   "display:inline;}"

   ":host([data-align='start']) [part=line-left]{"
   "flex:0 0 1rem;}"

   ":host([data-align='end']) [part=line-right]{"
   "flex:0 0 1rem;}"

   "@media (prefers-reduced-motion:reduce){"
   "*{transition:none !important;animation:none !important;}}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────
(defn- make-el [tag] (.createElement js/document tag))

(defn- remove-all-children! [^js parent]
  (loop []
    (when-let [^js child (.-firstChild parent)]
      (.removeChild parent child)
      (recur))))

;; ── Shadow DOM creation ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        ^js style     (make-el "style")
        ^js container (make-el "div")
        refs          #js {}]
    (set! (.-textContent style) style-text)
    (du/set-attr! container attr-part part-container)

    ;; Start in no-label mode: single line div.
    (let [^js line (make-el "div")]
      (du/set-attr! line attr-part part-line)
      (.appendChild container line))

    (.appendChild root style)
    (.appendChild root container)

    (gobj/set refs "root"      root)
    (gobj/set refs rk-container container)
    (gobj/set refs rk-mode      mode-no-label)
    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (make-shadow! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (let [m (model/normalize
           {:orientation-raw (du/get-attr el model/attr-orientation)
            :variant-raw     (du/get-attr el model/attr-variant)
            :thickness-raw   (du/get-attr el model/attr-thickness)
            :color-raw       (du/get-attr el model/attr-color)
            :inset-raw       (du/get-attr el model/attr-inset)
            :length-raw      (du/get-attr el model/attr-length)
            :label-raw       (du/get-attr el model/attr-label)
            :align-raw       (du/get-attr el model/attr-align)
            :role-raw        (du/get-attr el model/attr-role)
            :aria-label-raw  (du/get-attr el model/attr-aria-label)})]
    (assoc m :has-label? (model/has-label? (:label m)))))

;; ── Label-mode DOM swapping ───────────────────────────────────────────────
(defn- build-label-children! [^js container ^js refs]
  (let [^js line-left  (make-el "div")
        ^js label-wrap (make-el "span")
        ^js slot-el    (make-el "slot")
        ^js label-text (make-el "span")
        ^js line-right (make-el "div")]
    (du/set-attr! line-left  attr-part part-line-left)
    (du/set-attr! label-wrap attr-part part-label)
    (du/set-attr! slot-el    attr-name slot-name-label)
    (du/set-attr! label-text attr-part part-label-text)
    (du/set-attr! line-right attr-part part-line-right)
    (.appendChild label-wrap slot-el)
    (.appendChild label-wrap label-text)
    (.appendChild container line-left)
    (.appendChild container label-wrap)
    (.appendChild container line-right)
    (gobj/set refs rk-line-left  line-left)
    (gobj/set refs rk-label-wrap label-wrap)
    (gobj/set refs rk-label-text label-text)
    (gobj/set refs rk-line-right line-right)
    (gobj/set refs rk-mode       mode-label)))

(defn- build-no-label-children! [^js container ^js refs]
  (let [^js line (make-el "div")]
    (du/set-attr! line attr-part part-line)
    (.appendChild container line)
    (gobj/set refs rk-line-left  nil)
    (gobj/set refs rk-label-wrap nil)
    (gobj/set refs rk-label-text nil)
    (gobj/set refs rk-line-right nil)
    (gobj/set refs rk-mode       mode-no-label)))

(defn- ensure-mode! [^js refs target-mode]
  (when (not= (gobj/get refs rk-mode) target-mode)
    (let [^js container (gobj/get refs rk-container)]
      (remove-all-children! container)
      (if (= target-mode mode-label)
        (build-label-children!    container refs)
        (build-no-label-children! container refs)))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────
(defn- apply-host-data! [^js el {:keys [orientation variant align]}]
  (du/set-attr! el attr-data-orientation orientation)
  (du/set-attr! el attr-data-variant     variant)
  (du/set-attr! el attr-data-align       align))

(defn- apply-css-vars! [^js el {:keys [thickness color inset length]}]
  (let [^js style (.-style el)]
    ;; thickness/inset/length always set with defaults so they're present on
    ;; the host's inline style (don't rely on :host inheritance).
    (.setProperty style css-var-thickness (or thickness val-default-thickness))
    (.setProperty style css-var-inset     (or inset     val-default-inset))
    (.setProperty style css-var-length    (or length    val-default-length))
    ;; color: only set when provided; let the :host rule + dark-mode @media
    ;; supply the default.
    (if color
      (.setProperty    style css-var-color color)
      (.removeProperty style css-var-color))))

(defn- apply-host-aria! [^js el {:keys [orientation aria-label role]}]
  (if (model/separator-role? role)
    (du/set-attr! el attr-role val-separator)
    (du/set-attr! el attr-role val-presentation))
  ;; aria-orientation only meaningful for separator role; reflect for both
  ;; orientations rather than removing, so AT can read explicit value.
  (if (= orientation val-vertical)
    (du/set-attr!    el attr-aria-orientation val-vertical)
    (du/remove-attr! el attr-aria-orientation))
  (if (and aria-label (pos? (.-length aria-label)))
    (du/set-attr!    el attr-aria-label aria-label)
    (du/remove-attr! el attr-aria-label)))

(defn- apply-label-structure! [^js refs {:keys [has-label? label]}]
  (ensure-mode! refs (if has-label? mode-label mode-no-label))
  (when has-label?
    (let [^js label-text (gobj/get refs rk-label-text)]
      (when label-text
        (set! (.-textContent label-text) label)))))

(defn- apply-model! [^js el m]
  (let [refs (ensure-refs! el)]
    (apply-host-data!       el m)
    (apply-css-vars!        el m)
    (apply-host-aria!       el m)
    (apply-label-structure! refs m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
;; x-divider is purely visual — no event listeners.
(defn- connected! [^js el]
  (ensure-refs! el)
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
