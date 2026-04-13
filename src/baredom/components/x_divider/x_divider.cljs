(ns baredom.components.x-divider.x-divider
  (:require
   [goog.object :as gobj]
   [baredom.components.x-divider.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xDividerRefs")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
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
(defn- set-attr! [^js el k v] (.setAttribute el k v))

;; ── Shadow DOM creation ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        ^js style     (make-el "style")
        ^js container (make-el "div")]
    (set! (.-textContent style) style-text)
    (set-attr! container "part" "container")

    ;; Build the no-label subtree: single line div
    (let [^js line (make-el "div")]
      (set-attr! line "part" "line")
      (.appendChild container line))

    (.appendChild root style)
    (.appendChild root container)

    (gobj/set el k-refs
              #js {:root      root
                   :container container
                   :mode      "no-label"}))
  nil)

(defn- remove-all-children! [^js parent]
  (loop []
    (when-let [^js child (.-firstChild parent)]
      (.removeChild parent child)
      (recur))))

(defn- ensure-label-dom!
  "Switches the container to label mode if not already in that mode."
  [^js el]
  (let [refs      (gobj/get el k-refs)
        cur-mode  (gobj/get refs "mode")]
    (when (= cur-mode "no-label")
      (let [^js container (gobj/get refs "container")]
        ;; Clear existing children
        (remove-all-children! container)
        ;; Rebuild with label structure
        (let [^js line-left  (make-el "div")
              ^js label-wrap (make-el "span")
              ^js slot-el    (make-el "slot")
              ^js label-text (make-el "span")
              ^js line-right (make-el "div")]
          (set-attr! line-left  "part" "line-left")
          (set-attr! label-wrap "part" "label")
          (set-attr! slot-el    "name" "label")
          (set-attr! label-text "part" "label-text")
          (set-attr! line-right "part" "line-right")
          (.appendChild label-wrap slot-el)
          (.appendChild label-wrap label-text)
          (.appendChild container line-left)
          (.appendChild container label-wrap)
          (.appendChild container line-right)
          (gobj/set refs "line-left"  line-left)
          (gobj/set refs "label-wrap" label-wrap)
          (gobj/set refs "label-text" label-text)
          (gobj/set refs "line-right" line-right)
          (gobj/set refs "mode"       "label"))))
    nil))

(defn- ensure-no-label-dom!
  "Switches the container to no-label mode if not already in that mode."
  [^js el]
  (let [refs     (gobj/get el k-refs)
        cur-mode (gobj/get refs "mode")]
    (when (= cur-mode "label")
      (let [^js container (gobj/get refs "container")]
        (remove-all-children! container)
        (let [^js line (make-el "div")]
          (set-attr! line "part" "line")
          (.appendChild container line)
          (gobj/set refs "line-left"  nil)
          (gobj/set refs "label-wrap" nil)
          (gobj/set refs "label-text" nil)
          (gobj/set refs "line-right" nil)
          (gobj/set refs "mode"       "no-label"))))
    nil))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [m (model/normalize
           {:orientation-raw (.getAttribute el model/attr-orientation)
            :variant-raw     (.getAttribute el model/attr-variant)
            :thickness-raw   (.getAttribute el model/attr-thickness)
            :color-raw       (.getAttribute el model/attr-color)
            :inset-raw       (.getAttribute el model/attr-inset)
            :length-raw      (.getAttribute el model/attr-length)
            :label-raw       (.getAttribute el model/attr-label)
            :align-raw       (.getAttribute el model/attr-align)
            :role-raw        (.getAttribute el model/attr-role)
            :aria-label-raw  (.getAttribute el model/attr-aria-label)})
        {:keys [orientation variant align label aria-label
                thickness color inset length]} m
        has-lbl (model/has-label? label)
        ^js style (.-style el)]

    ;; Data attributes for CSS
    (.setAttribute el "data-orientation" orientation)
    (.setAttribute el "data-variant"     variant)
    (.setAttribute el "data-align"       align)

    ;; CSS custom properties — always set thickness/inset/length with defaults
    ;; so the values are always present on the host's inline style (avoids
    ;; relying on CSS custom-property inheritance from :host rule)
    (.setProperty style "--x-divider-thickness" (or thickness "1px"))
    (.setProperty style "--x-divider-inset"     (or inset "0px"))
    (.setProperty style "--x-divider-length"    (or length "auto"))
    ;; Color: only set explicitly when provided; let the :host CSS rule and
    ;; the dark-mode @media query supply the correct default
    (if color
      (.setProperty style "--x-divider-color" color)
      (.removeProperty style "--x-divider-color"))

    ;; ARIA role on host
    (if (model/separator-role? (:role m))
      (.setAttribute el "role" "separator")
      (.setAttribute el "role" "presentation"))

    ;; aria-orientation on host (only meaningful for separator role)
    (if (= orientation "vertical")
      (.setAttribute el "aria-orientation" "vertical")
      (.removeAttribute el "aria-orientation"))

    ;; aria-label on host
    (if (and aria-label (pos? (.-length aria-label)))
      (.setAttribute el "aria-label" aria-label)
      (.removeAttribute el "aria-label"))

    ;; Switch DOM structure based on label presence
    (if has-lbl
      (do
        (ensure-label-dom! el)
        (let [refs (gobj/get el k-refs)
              ^js label-text (gobj/get refs "label-text")]
          (when label-text
            (set! (.-textContent label-text) label))))
      (ensure-no-label-dom! el)))
  nil)

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js _el]
  ;; x-divider is purely visual — no event listeners needed
  nil)

(defn- remove-listeners! [^js _el]
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

(defn- def-string-prop-default! [^js proto attr default-val]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) default-val)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-string-prop-default! proto model/attr-orientation model/default-orientation)
  (def-string-prop-default! proto model/attr-variant     model/default-variant)
  (def-string-prop-default! proto model/attr-align       model/default-align)
  (def-string-prop! proto model/attr-label)
  (def-string-prop! proto model/attr-thickness)
  (def-string-prop! proto model/attr-color)
  (def-string-prop! proto model/attr-inset)
  (def-string-prop! proto model/attr-length))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this (connected! this))))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this (disconnected! this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v]
            (this-as ^js this (attribute-changed! this n o v))))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
