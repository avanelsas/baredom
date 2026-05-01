(ns baredom.components.x-context-menu.x-context-menu
  (:require [baredom.utils.dom :as du]
            [baredom.components.x-context-menu.model :as model]
            [baredom.utils.overlay :as overlay]
            [goog.object :as gobj]))

;; ---- Instance field keys ----

(def ^:private k-refs     "__xContextMenuRefs")
(def ^:private k-layer    "__xContextMenuLayer")
(def ^:private k-doc-handlers "__xContextMenuDocH")

;; ---- Forward declarations ----

(declare close!)

;; ---- DOM helpers ----

(defn- make-el [tag]
  (.createElement js/document tag))

;; ---- Styles ----

(def ^:private host-style-text
  ":host{display:contents;}slot{display:none;}")

(def ^:private panel-style-text
  (str
   ":host{color-scheme:light dark;"
   "--x-context-menu-bg:var(--x-color-bg,rgba(255,255,255,0.97));"
   "--x-context-menu-border:var(--x-color-border,rgba(0,0,0,0.1));"
   "--x-context-menu-shadow:var(--x-shadow-lg,0 8px 32px rgba(0,0,0,0.14),0 2px 8px rgba(0,0,0,0.08));"
   "--x-context-menu-radius:var(--x-radius-md,10px);"
   "--x-context-menu-item-hover:rgba(0,0,0,0.05);"
   "--x-context-menu-item-active:rgba(0,0,0,0.09);"
   "--x-context-menu-item-fg:var(--x-color-text,rgba(0,0,0,0.87));"
   "--x-context-menu-separator:var(--x-color-border,rgba(0,0,0,0.1));"
   "}"
   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-context-menu-bg:var(--x-color-bg,#1e293b);"
   "--x-context-menu-border:var(--x-color-border,rgba(255,255,255,0.12));"
   "--x-context-menu-shadow:var(--x-shadow-lg,0 8px 32px rgba(0,0,0,0.7),0 2px 8px rgba(0,0,0,0.4));"
   "--x-context-menu-item-hover:rgba(255,255,255,0.08);"
   "--x-context-menu-item-active:rgba(255,255,255,0.14);"
   "--x-context-menu-item-fg:var(--x-color-text,rgba(255,255,255,0.9));"
   "--x-context-menu-separator:var(--x-color-border,rgba(255,255,255,0.12));"
   "}"
   "}"
   "[part=panel]{"
   "position:absolute;"
   "min-width:160px;"
   "max-width:calc(100vw - 1rem);"
   "border-radius:var(--x-context-menu-radius);"
   "background:var(--x-context-menu-bg);"
   "border:1px solid var(--x-context-menu-border);"
   "box-shadow:var(--x-context-menu-shadow);"
   "padding:4px 0;"
   "overflow:auto;"
   "pointer-events:auto;"
   "outline:none;"
   "@media(prefers-reduced-motion:no-preference){"
   "transition:opacity 120ms ease,transform 120ms ease;"
   "}"
   "}"
   "[role=menuitem]{color:var(--x-context-menu-item-fg);}"
   "[role=menuitem]:hover{background:var(--x-context-menu-item-hover);}"))

;; ---- Read model ----

(defn- read-model [^js el]
  (model/normalize
   {:open-present?     (when (du/has-attr? el model/attr-open)
                         (du/get-attr el model/attr-open))
    :disabled-present? (when (du/has-attr? el model/attr-disabled)
                         (du/get-attr el model/attr-disabled))
    :placement-raw     (du/get-attr el model/attr-placement)
    :offset-raw        (du/get-attr el model/attr-offset)
    :z-index-raw       (du/get-attr el model/attr-z-index)}))

;; ---- Event dispatch ----

;; ---- Overlay root (via shared utility) ----

;; ---- Keyboard navigation in panel ----

(defn- menu-items [^js panel]
  (array-seq (.querySelectorAll panel "[role=menuitem]:not([disabled]):not([aria-disabled=true])")))

(defn- focus-item! [^js item]
  (when item (.focus item)))

(defn- focus-next! [^js panel ^js current]
  (let [items (menu-items panel)
        idx   (.indexOf (to-array items) current)
        next  (nth items (mod (inc idx) (count items)) nil)]
    (focus-item! next)))

(defn- focus-prev! [^js panel ^js current]
  (let [items (menu-items panel)
        n     (count items)
        idx   (.indexOf (to-array items) current)
        prev  (nth items (mod (+ idx (dec n)) n) nil)]
    (focus-item! prev)))

(defn- focus-first! [^js panel]
  (focus-item! (first (menu-items panel))))

;; ---- Layer management ----

(defn- make-layer! [^js el z-index]
  (let [^js layer (overlay/make-layer! el panel-style-text z-index)
        ^js panel (make-el "div")]
    (.setAttribute panel "part"     "panel")
    (.setAttribute panel "role"     "menu")
    (.setAttribute panel "tabindex" "-1")
    (.appendChild (.-shadowRoot layer) panel)
    layer))

(defn- position-layer! [^js layer x y {:keys [z-index max-height]}]
  (set! (.. layer -style -zIndex) (str z-index))
  (let [^js panel (overlay/get-panel layer)]
    (when panel
      (set! (.. panel -style -left)      (str x "px"))
      (set! (.. panel -style -top)       (str y "px"))
      (set! (.. panel -style -maxHeight) (str max-height "px")))))

(defn- clone-children-to-panel! [^js el ^js panel]
  (doseq [^js child (array-seq (.-children el))]
    (.appendChild panel (.cloneNode child true))))

(defn- add-layer-listeners! [^js el ^js layer]
  (let [^js panel (overlay/get-panel layer)

        on-key
        (fn [^js ev]
          (let [key (.-key ev)
                ^js focused (.-activeElement (.-shadowRoot layer))]
            (cond
              (= key "Tab")
              (close! el "keyboard")

              (= key "ArrowDown")
              (do (.preventDefault ev)
                  (if focused
                    (focus-next! panel focused)
                    (focus-first! panel)))

              (= key "ArrowUp")
              (do (.preventDefault ev)
                  (if focused
                    (focus-prev! panel focused)
                    (focus-first! panel)))

              (or (= key "Enter") (= key " "))
              (when focused
                (.preventDefault ev)
                (.click focused)))))

        on-item-click
        (fn [^js ev]
          (.stopPropagation ev)
          (let [^js target (.-target ev)
                ^js item   (.closest target "[role=menuitem]")]
            (when item
              (du/dispatch! el model/event-select #js {:item item})
              (close! el "select"))))]

    (.addEventListener layer "keydown" on-key true)
    (when panel
      (.addEventListener panel "click" on-item-click))

    ;; store for cleanup
    (gobj/set layer "__onKey"             on-key)
    (gobj/set layer "__onItemClick"       on-item-click)))

;; ---- Document-level listeners (Escape + click-outside) ----

(defn- add-doc-listeners! [^js el]
  (let [on-doc-keydown
        (fn [^js ev]
          (when (= (.-key ev) "Escape")
            (.preventDefault ev)
            (close! el "keyboard")))

        on-doc-click
        (fn [^js ev]
          (when-let [^js lyr (gobj/get el k-layer)]
            (let [^js panel (overlay/get-panel lyr)]
              (when (and panel
                         (not (.contains panel (.-target ev))))
                (close! el "backdrop")))))]

    ;; Delay by one tick so the opening click/contextmenu does not immediately close
    (js/setTimeout
     (fn []
       (when (.hasAttribute el model/attr-open)
         (.addEventListener js/document "keydown" on-doc-keydown)
         (.addEventListener js/document "click"   on-doc-click)
         (gobj/set el k-doc-handlers
                   #js {:keydown on-doc-keydown :click on-doc-click})))
     0)))

(defn- remove-doc-listeners! [^js el]
  (when-let [handlers (gobj/get el k-doc-handlers)]
    (.removeEventListener js/document "keydown" (gobj/get handlers "keydown"))
    (.removeEventListener js/document "click"   (gobj/get handlers "click"))
    (gobj/set el k-doc-handlers nil)))

(defn- remove-layer! [^js layer]
  (overlay/remove-layer! layer))

;; ---- close! ----

(defn- close! [^js el reason]
  (when (.hasAttribute el model/attr-open)
    (let [proceed? (du/dispatch-cancelable! el model/event-close-request #js {:reason reason})]
      (when proceed?
        (remove-doc-listeners! el)
        (.removeAttribute el model/attr-open)
        (let [^js layer (gobj/get el k-layer)]
          (remove-layer! layer)
          (gobj/set el k-layer nil))
        (du/dispatch! el model/event-close #js {:reason reason})))))

;; ---- open! ----

(defn- open-at-coords! [^js el x y reason]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [proceed? (du/dispatch-cancelable! el model/event-open-request #js {:reason reason})]
      (when proceed?
        (let [m           (read-model el)
              {:keys [placement offset z-index]} m
              vw          (.-innerWidth js/window)
              vh          (.-innerHeight js/window)
              ;; Use a 1x1 anchor at the coordinate
              anchor-rect {:x x :y y :width 1 :height 1}
              ;; Estimate panel size (will be corrected after render)
              panel-est   {:width 200 :height 300}
              pos         (model/compute-position
                           placement offset anchor-rect panel-est
                           {:width vw :height vh} 8)
              ^js layer   (make-layer! el z-index)]

          (clone-children-to-panel! el (overlay/get-panel layer))
          (add-layer-listeners! el layer)
          (position-layer! layer (:x pos) (:y pos) (assoc pos :z-index z-index))

          (.setAttribute el model/attr-open "")
          (gobj/set el k-layer layer)
          (add-doc-listeners! el)

          ;; Re-position after actual panel dimensions are known
          (js/requestAnimationFrame
           (fn []
             (let [^js panel (overlay/get-panel layer)]
               (when panel
                 (let [pw  (.-offsetWidth panel)
                       ph  (.-offsetHeight panel)
                       pos2 (model/compute-position
                              placement offset anchor-rect
                              {:width pw :height ph}
                              {:width vw :height vh} 8)]
                   (position-layer! layer (:x pos2) (:y pos2) (assoc pos2 :z-index z-index)))
                 (focus-first! panel)))))

          (du/dispatch! el model/event-open #js {:reason reason}))))))

(defn- open-for-element! [^js el ^js anchor-el reason]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [^js rect (.getBoundingClientRect anchor-el)
          m        (read-model el)
          {:keys [placement offset z-index]} m
          vw       (.-innerWidth js/window)
          vh       (.-innerHeight js/window)
          anchor   {:x (.-left rect) :y (.-top rect)
                    :width (.-width rect) :height (.-height rect)}
          proceed? (du/dispatch-cancelable! el model/event-open-request #js {:reason reason})]
      (when proceed?
        (let [panel-est {:width 200 :height 300}
              pos       (model/compute-position
                         placement offset anchor panel-est {:width vw :height vh} 8)
              ^js layer (make-layer! el z-index)]

          (clone-children-to-panel! el (overlay/get-panel layer))
          (add-layer-listeners! el layer)
          (position-layer! layer (:x pos) (:y pos) (assoc pos :z-index z-index))

          (.setAttribute el model/attr-open "")
          (gobj/set el k-layer layer)
          (add-doc-listeners! el)

          (js/requestAnimationFrame
           (fn []
             (let [^js panel (overlay/get-panel layer)]
               (when panel
                 (let [pw  (.-offsetWidth panel)
                       ph  (.-offsetHeight panel)
                       pos2 (model/compute-position
                              placement offset anchor
                              {:width pw :height ph}
                              {:width vw :height vh} 8)]
                   (position-layer! layer (:x pos2) (:y pos2) (assoc pos2 :z-index z-index)))
                 (focus-first! panel)))))

          (du/dispatch! el model/event-open #js {:reason reason}))))))

;; ---- Shadow DOM creation ----

(defn- make-shadow! [^js el]
  (let [^js root    (.attachShadow el #js {:mode "open"})
        ^js style   (make-el "style")
        ^js slot    (make-el "slot")]

    (set! (.-textContent style) host-style-text)
    (.appendChild root style)
    (.appendChild root slot)

    (let [refs #js {}]
      (gobj/set refs "root" root)
      (gobj/set refs "slot" slot)
      (gobj/set el k-refs refs))))

;; ---- Lifecycle ----

(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  ;; If open attr was already set (e.g. SSR), sync visual state
  (when (.hasAttribute el model/attr-open)
    ;; Re-opening: the layer was not created yet (first connect), clear attr
    (.removeAttribute el model/attr-open)))

(defn- disconnected! [^js el]
  (let [^js layer (gobj/get el k-layer)]
    (when layer
      (remove-layer! layer)
      (gobj/set el k-layer nil))))

(defn- attribute-changed! [^js el name _old _new]
  ;; If open attr removed externally, close the layer
  (when (= name model/attr-open)
    (when-not (.hasAttribute el model/attr-open)
      (let [^js layer (gobj/get el k-layer)]
        (when layer
          (remove-layer! layer)
          (gobj/set el k-layer nil))))))

;; ---- Property descriptors ----

(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (.hasAttribute this attr-name)))
        :set (fn [v]
               (this-as ^js this
                        (if v
                          (.setAttribute this attr-name "")
                          (.removeAttribute this attr-name))))}))

(defn- define-str-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (.getAttribute this attr-name)))
        :set (fn [v]
               (this-as ^js this
                        (if (some? v)
                          (.setAttribute this attr-name (str v))
                          (.removeAttribute this attr-name))))}))

;; ---- Public methods ----

(defn- define-methods! [^js proto]
  (aset proto "openAt"
        (fn [x y]
          (this-as ^js this
                   (open-at-coords! this x y "programmatic"))))

  (aset proto "toggleAt"
        (fn [x y]
          (this-as ^js this
                   (if (.hasAttribute this model/attr-open)
                     (close! this "toggle")
                     (open-at-coords! this x y "toggle")))))

  (aset proto "openForElement"
        (fn [^js anchor-el]
          (this-as ^js this
                   (open-for-element! this anchor-el "programmatic"))))

  (aset proto "close"
        (fn []
          (this-as ^js this
                   (close! this "programmatic")))))

;; ---- Element class + registration ----

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (define-bool-prop! (.-prototype klass) "open"      model/attr-open)
    (define-bool-prop! (.-prototype klass) "disabled"  model/attr-disabled)
    (define-str-prop!  (.-prototype klass) "placement" model/attr-placement)
    (define-str-prop!  (.-prototype klass) "offset"    model/attr-offset)
    (define-str-prop!  (.-prototype klass) "zIndex"    model/attr-z-index)
    (define-methods!   (.-prototype klass))

    klass))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
