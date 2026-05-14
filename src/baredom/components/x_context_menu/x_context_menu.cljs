(ns baredom.components.x-context-menu.x-context-menu
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-context-menu.model :as model]
            [baredom.utils.overlay :as overlay]
            [goog.object :as gobj]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs         "__xContextMenuRefs")
(def ^:private k-layer        "__xContextMenuLayer")
(def ^:private k-doc-handlers "__xContextMenuDocH")
(def ^:private k-anchor       "__xContextMenuAnchor")
(def ^:private k-model        "__xContextMenuModel")

;; ── Forward declarations ──────────────────────────────────────────────────
(declare close!)

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part        "part")
(def ^:private attr-role        "role")
(def ^:private attr-tabindex    "tabindex")
(def ^:private part-panel       "panel")
(def ^:private role-menu "menu")
(def ^:private sel-menuitem
  "[role=menuitem]:not([disabled]):not([aria-disabled=true])")
(def ^:private sel-menuitem-any "[role=menuitem]")

(def ^:private reason-keyboard     "keyboard")
(def ^:private reason-backdrop     "backdrop")
(def ^:private reason-select       "select")
(def ^:private reason-programmatic "programmatic")
(def ^:private reason-toggle       "toggle")

(def ^:private margin-px 8)
(def ^:private panel-est-w 200)
(def ^:private panel-est-h 300)

;; ── DOM helpers ───────────────────────────────────────────────────────────

;; ── Styles ────────────────────────────────────────────────────────────────
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

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open-present?     (when (du/has-attr? el model/attr-open)
                         (du/get-attr el model/attr-open))
    :disabled-present? (when (du/has-attr? el model/attr-disabled)
                         (du/get-attr el model/attr-disabled))
    :placement-raw     (du/get-attr el model/attr-placement)
    :offset-raw        (du/get-attr el model/attr-offset)
    :z-index-raw       (du/get-attr el model/attr-z-index)}))

;; ── Keyboard navigation in panel ─────────────────────────────────────────
(defn- menu-items [^js panel]
  (array-seq (.querySelectorAll panel sel-menuitem)))

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

;; ── Layer management ──────────────────────────────────────────────────────
(defn- make-layer! [^js el z-index]
  (let [^js layer (overlay/make-layer! el panel-style-text z-index)
        ^js panel (.createElement js/document "div")]
    (du/set-attr! panel attr-part     part-panel)
    (du/set-attr! panel attr-role     role-menu)
    (du/set-attr! panel attr-tabindex "-1")
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

(defn- viewport-size []
  {:width (.-innerWidth js/window) :height (.-innerHeight js/window)})

(defn- compute-and-place! [^js layer anchor-rect placement offset panel-size z-index]
  (let [pos (model/compute-position placement offset anchor-rect panel-size
                                    (viewport-size) margin-px)]
    (position-layer! layer (:x pos) (:y pos) (assoc pos :z-index z-index))))

(defn- reposition!
  "Reposition the open layer using stored anchor + latest attribute model."
  [^js el]
  (when-let [^js layer (du/getv el k-layer)]
    (when-let [anchor (du/getv el k-anchor)]
      (let [{:keys [placement offset z-index]} (read-model el)
            ^js panel (overlay/get-panel layer)
            panel-size (if panel
                         {:width (.-offsetWidth panel) :height (.-offsetHeight panel)}
                         {:width panel-est-w :height panel-est-h})]
        (compute-and-place! layer anchor placement offset panel-size z-index)))))

(defn- add-layer-listeners! [^js el ^js layer]
  (let [^js panel (overlay/get-panel layer)

        on-key
        (fn on-layer-keydown [^js ev]
          (let [key (.-key ev)
                ^js focused (.-activeElement (.-shadowRoot layer))]
            (cond
              (= key "Tab")
              (close! el reason-keyboard)

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
        (fn on-panel-click [^js ev]
          (.stopPropagation ev)
          (let [^js target (.-target ev)
                ^js item   (.closest target sel-menuitem-any)]
            (when item
              (du/dispatch! el model/event-select #js {:item item})
              (close! el reason-select))))]

    (overlay/attach-listener! layer layer "keydown" on-key true)
    (when panel
      (overlay/attach-listener! layer panel "click" on-item-click false))))

;; ── Document-level listeners (Escape + click-outside) ────────────────────
(defn- add-doc-listeners! [^js el]
  (let [on-doc-keydown
        (fn handle-doc-keydown [^js ev]
          (when (= (.-key ev) "Escape")
            (.preventDefault ev)
            (close! el reason-keyboard)))

        on-doc-click
        (fn handle-doc-click [^js ev]
          (when-let [^js lyr (du/getv el k-layer)]
            (let [^js panel (overlay/get-panel lyr)
                  path      (.composedPath ev)
                  inside?   (some #(identical? % panel) (array-seq path))]
              (when (and panel (not inside?))
                (close! el reason-backdrop)))))]

    ;; Delay by one tick so the opening click/contextmenu does not immediately close
    (js/setTimeout
     (fn delayed-add-doc-listeners []
       (when (du/has-attr? el model/attr-open)
         (.addEventListener js/document "keydown" on-doc-keydown)
         (.addEventListener js/document "click"   on-doc-click)
         (du/setv! el k-doc-handlers
                   #js {:keydown on-doc-keydown :click on-doc-click})))
     0)))

(defn- remove-doc-listeners! [^js el]
  (when-let [handlers (du/getv el k-doc-handlers)]
    (.removeEventListener js/document "keydown" (gobj/get handlers "keydown"))
    (.removeEventListener js/document "click"   (gobj/get handlers "click"))
    (du/setv! el k-doc-handlers nil)))

(defn- remove-layer! [^js layer]
  (overlay/remove-layer! layer))

;; ── close! ────────────────────────────────────────────────────────────────
(defn- close! [^js el reason]
  (when (du/has-attr? el model/attr-open)
    (let [proceed? (du/dispatch-cancelable! el model/event-close-request #js {:reason reason})]
      (when proceed?
        (remove-doc-listeners! el)
        (du/remove-attr! el model/attr-open)
        (let [^js layer (du/getv el k-layer)]
          (remove-layer! layer)
          (du/setv! el k-layer nil))
        (du/setv! el k-anchor nil)
        (du/setv! el k-model nil)
        (du/dispatch! el model/event-close #js {:reason reason})))))

;; ── open! ─────────────────────────────────────────────────────────────────
(defn- open-with-anchor-rect!
  "Shared open path: place a layer for the given anchor rect, install listeners,
   and dispatch open-request / open events."
  [^js el anchor-rect reason]
  (when-not (du/has-attr? el model/attr-disabled)
    (let [proceed? (du/dispatch-cancelable! el model/event-open-request #js {:reason reason})]
      (when proceed?
        (let [{:keys [placement offset z-index] :as m} (read-model el)
              ^js layer (make-layer! el z-index)]

          (clone-children-to-panel! el (overlay/get-panel layer))
          (add-layer-listeners! el layer)
          (compute-and-place! layer anchor-rect placement offset
                              {:width panel-est-w :height panel-est-h} z-index)

          (du/set-attr! el model/attr-open "")
          (du/setv! el k-layer  layer)
          (du/setv! el k-anchor anchor-rect)
          (du/setv! el k-model  m)
          (add-doc-listeners! el)

          ;; Re-position after actual panel dimensions are known
          (js/requestAnimationFrame
           (fn refine-position []
             (let [^js panel (overlay/get-panel layer)]
               (when panel
                 (let [panel-size {:width (.-offsetWidth panel) :height (.-offsetHeight panel)}]
                   (compute-and-place! layer anchor-rect placement offset panel-size z-index)))
               (when panel (focus-first! panel)))))

          (du/dispatch! el model/event-open #js {:reason reason}))))))

(defn- open-at-coords! [^js el x y reason]
  (open-with-anchor-rect! el {:x x :y y :width 1 :height 1} reason))

(defn- open-for-element! [^js el ^js anchor-el reason]
  (let [^js rect (.getBoundingClientRect anchor-el)]
    (open-with-anchor-rect!
     el
     {:x (.-left rect) :y (.-top rect)
      :width (.-width rect) :height (.-height rect)}
     reason)))

;; ── Shadow DOM creation ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [^js root  (.attachShadow el #js {:mode "open"})
        ^js style (.createElement js/document "style")
        ^js slot  (.createElement js/document "slot")
        refs      #js {}]

    (set! (.-textContent style) host-style-text)
    (.appendChild root style)
    (.appendChild root slot)

    (gobj/set refs "root" root)
    (gobj/set refs "slot" slot)
    (du/setv! el k-refs refs)))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  ;; If open attr was already set (e.g. SSR), the layer was not created yet.
  ;; Clear the attribute so subsequent open() calls work correctly.
  (when (du/has-attr? el model/attr-open)
    (du/remove-attr! el model/attr-open)))

(defn- disconnected! [^js el]
  (let [^js layer (du/getv el k-layer)]
    (when layer
      (remove-layer! layer)
      (du/setv! el k-layer nil)
      (du/setv! el k-anchor nil)
      (du/setv! el k-model nil))))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (cond
      ;; open attribute removed externally → tear down layer
      (and (= attr-name model/attr-open)
           (not (du/has-attr? el model/attr-open)))
      (let [^js layer (du/getv el k-layer)]
        (when layer
          (remove-doc-listeners! el)
          (remove-layer! layer)
          (du/setv! el k-layer nil)
          (du/setv! el k-anchor nil)
          (du/setv! el k-model nil)))

      ;; placement / offset / z-index changed while open → reposition
      (and (du/getv el k-layer)
           (or (= attr-name model/attr-placement)
               (= attr-name model/attr-offset)
               (= attr-name model/attr-z-index)))
      (let [new-m (read-model el)]
        (when (not= new-m (du/getv el k-model))
          (reposition! el)
          (du/setv! el k-model new-m))))))

;; ── Public methods (Tier-2 .defineProperty migration) ────────────────────
(defn- define-methods! [^js proto]
  (.defineProperty js/Object proto "openAt"
    #js {:value (fn xcm-open-at [x y]
                  (this-as ^js this
                    (open-at-coords! this x y reason-programmatic)))
         :writable true :configurable true})

  (.defineProperty js/Object proto "toggleAt"
    #js {:value (fn xcm-toggle-at [x y]
                  (this-as ^js this
                    (if (.hasAttribute this model/attr-open)
                      (close! this reason-toggle)
                      (open-at-coords! this x y reason-toggle))))
         :writable true :configurable true})

  (.defineProperty js/Object proto "openForElement"
    #js {:value (fn xcm-open-for-element [^js anchor-el]
                  (this-as ^js this
                    (open-for-element! this anchor-el reason-programmatic)))
         :writable true :configurable true})

  (.defineProperty js/Object proto "close"
    #js {:value (fn xcm-close []
                  (this-as ^js this
                    (close! this reason-programmatic)))
         :writable true :configurable true}))

;; ── Element class + registration ──────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (define-methods!        proto))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
