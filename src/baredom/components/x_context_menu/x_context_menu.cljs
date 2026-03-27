(ns baredom.components.x-context-menu.x-context-menu
  (:require [baredom.components.x-context-menu.model :as model]
            [goog.object :as gobj]))

;; ---- Instance field keys ----

(def ^:private k-refs     "__xContextMenuRefs")
(def ^:private k-handlers "__xContextMenuHandlers")
(def ^:private k-layer    "__xContextMenuLayer")

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
   "--x-context-menu-bg:rgba(255,255,255,0.97);"
   "--x-context-menu-border:rgba(0,0,0,0.1);"
   "--x-context-menu-shadow:0 8px 32px rgba(0,0,0,0.14),0 2px 8px rgba(0,0,0,0.08);"
   "--x-context-menu-radius:10px;"
   "--x-context-menu-item-hover:rgba(0,0,0,0.05);"
   "--x-context-menu-item-active:rgba(0,0,0,0.09);"
   "--x-context-menu-item-fg:rgba(0,0,0,0.87);"
   "--x-context-menu-separator:rgba(0,0,0,0.1);"
   "}"
   "@media(prefers-color-scheme:dark){"
   ":host{"
   "--x-context-menu-bg:rgba(30,30,35,0.97);"
   "--x-context-menu-border:rgba(255,255,255,0.1);"
   "--x-context-menu-shadow:0 8px 32px rgba(0,0,0,0.55),0 2px 8px rgba(0,0,0,0.35);"
   "--x-context-menu-item-hover:rgba(255,255,255,0.07);"
   "--x-context-menu-item-active:rgba(255,255,255,0.12);"
   "--x-context-menu-item-fg:rgba(255,255,255,0.87);"
   "--x-context-menu-separator:rgba(255,255,255,0.1);"
   "}"
   "}"
   "[part=panel]{"
   "position:absolute;"
   "min-width:160px;"
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
   "}"))

;; ---- Read model ----

(defn- has-attr? [^js el attr] (.hasAttribute el attr))
(defn- get-attr  [^js el attr] (.getAttribute  el attr))

(defn- read-model [^js el]
  (model/normalize
   {:open-present?     (when (has-attr? el model/attr-open)
                         (get-attr el model/attr-open))
    :disabled-present? (when (has-attr? el model/attr-disabled)
                         (get-attr el model/attr-disabled))
    :placement-raw     (get-attr el model/attr-placement)
    :offset-raw        (get-attr el model/attr-offset)
    :z-index-raw       (get-attr el model/attr-z-index)}))

;; ---- Event dispatch ----

(defn- dispatch! [^js el event-name cancelable? detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail    detail
                     :bubbles   true
                     :composed  true
                     :cancelable cancelable?})]
    (.dispatchEvent el ev)
    (not (.-defaultPrevented ev))))

;; ---- Overlay root ----

(def ^:private overlay-root-id "__xOverlayRoot")

(defn- ensure-overlay-root! []
  (or (.getElementById js/document overlay-root-id)
      (let [^js div (make-el "div")]
        (.setAttribute div "id" overlay-root-id)
        (set! (.. div -style -position) "fixed")
        (set! (.. div -style -inset) "0")
        (set! (.. div -style -pointerEvents) "none")
        (set! (.. div -style -zIndex) "0")
        (.appendChild (.-body js/document) div)
        div)))

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
  (let [^js overlay (ensure-overlay-root!)
        ^js layer   (make-el "div")
        ^js shadow  (.attachShadow layer #js {:mode "open"})
        ^js style   (make-el "style")
        ^js panel   (make-el "div")]

    (set! (.. layer -style -position) "fixed")
    (set! (.. layer -style -inset) "0")
    (set! (.. layer -style -pointerEvents) "none")
    (set! (.. layer -style -zIndex) (str z-index))

    (set! (.-textContent style) panel-style-text)
    (.setAttribute panel "part"     "panel")
    (.setAttribute panel "role"     "menu")
    (.setAttribute panel "tabindex" "-1")

    (.appendChild shadow style)
    (.appendChild shadow panel)
    (.appendChild overlay layer)
    layer))

(defn- get-panel [^js layer]
  (when layer
    (.querySelector (.-shadowRoot layer) "[part=panel]")))

(defn- position-layer! [^js layer x y {:keys [z-index max-height]}]
  (set! (.. layer -style -zIndex) (str z-index))
  (let [^js panel (get-panel layer)]
    (when panel
      (set! (.. panel -style -left)      (str x "px"))
      (set! (.. panel -style -top)       (str y "px"))
      (set! (.. panel -style -maxHeight) (str max-height "px")))))

(defn- clone-children-to-panel! [^js el ^js panel]
  (doseq [^js child (array-seq (.-children el))]
    (.appendChild panel (.cloneNode child true))))

(defn- add-layer-listeners! [^js el ^js layer]
  (let [^js panel (get-panel layer)

        on-key
        (fn [^js ev]
          (let [key (.-key ev)
                ^js focused (.-activeElement (.-shadowRoot layer))]
            (cond
              (= key "Escape")
              (do (.preventDefault ev)
                  (.stopPropagation ev)
                  (close! el "keyboard"))

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

        on-click-backdrop
        (fn [^js ev]
          ;; Click outside panel closes
          (let [^js panel (get-panel layer)]
            (when (and panel
                       (not (.contains panel (.-target ev))))
              (close! el "backdrop"))))

        on-item-click
        (fn [^js ev]
          (.stopPropagation ev)
          (let [^js target (.-target ev)
                ^js item   (.closest target "[role=menuitem]")]
            (when item
              (dispatch! el model/event-select false #js {:item item})
              (close! el "select"))))]

    (.addEventListener layer "keydown" on-key true)
    (.addEventListener layer "click"   on-click-backdrop)
    (when panel
      (.addEventListener panel "click" on-item-click))

    ;; store for cleanup
    (gobj/set layer "__onKey"             on-key)
    (gobj/set layer "__onClickBackdrop"   on-click-backdrop)
    (gobj/set layer "__onItemClick"       on-item-click)))

(defn- remove-layer! [^js layer]
  (when layer
    (let [on-key           (gobj/get layer "__onKey")
          on-click-backdrop (gobj/get layer "__onClickBackdrop")
          ^js panel        (get-panel layer)]
      (when on-key           (.removeEventListener layer "keydown" on-key true))
      (when on-click-backdrop (.removeEventListener layer "click" on-click-backdrop))
      (when (.-parentNode layer)
        (.removeChild (.-parentNode layer) layer)))))

;; ---- close! ----

(defn- close! [^js el reason]
  (when (.hasAttribute el model/attr-open)
    (let [proceed? (dispatch! el model/event-close-request true #js {:reason reason})]
      (when proceed?
        (.removeAttribute el model/attr-open)
        (let [^js layer (gobj/get el k-layer)]
          (remove-layer! layer)
          (gobj/set el k-layer nil))
        (dispatch! el model/event-close false #js {:reason reason})))))

;; ---- open! ----

(defn- open-at-coords! [^js el x y reason]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [proceed? (dispatch! el model/event-open-request true #js {:reason reason})]
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

          (clone-children-to-panel! el (get-panel layer))
          (add-layer-listeners! el layer)
          (position-layer! layer (:x pos) (:y pos) (assoc pos :z-index z-index))

          (.setAttribute el model/attr-open "")
          (gobj/set el k-layer layer)

          ;; Re-position after actual panel dimensions are known
          (js/requestAnimationFrame
           (fn []
             (let [^js panel (get-panel layer)]
               (when panel
                 (let [pw  (.-offsetWidth panel)
                       ph  (.-offsetHeight panel)
                       pos2 (model/compute-position
                              placement offset anchor-rect
                              {:width pw :height ph}
                              {:width vw :height vh} 8)]
                   (position-layer! layer (:x pos2) (:y pos2) (assoc pos2 :z-index z-index)))
                 (focus-first! panel)))))

          (dispatch! el model/event-open false #js {:reason reason}))))))

(defn- open-for-element! [^js el ^js anchor-el reason]
  (let [^js rect (.getBoundingClientRect anchor-el)
        m        (read-model el)
        {:keys [placement offset z-index]} m
        vw       (.-innerWidth js/window)
        vh       (.-innerHeight js/window)
        anchor   {:x (.-left rect) :y (.-top rect)
                  :width (.-width rect) :height (.-height rect)}
        proceed? (dispatch! el model/event-open-request true #js {:reason reason})]
    (when proceed?
      (let [panel-est {:width 200 :height 300}
            pos       (model/compute-position
                       placement offset anchor panel-est {:width vw :height vh} 8)
            ^js layer (make-layer! el z-index)]

        (clone-children-to-panel! el (get-panel layer))
        (add-layer-listeners! el layer)
        (position-layer! layer (:x pos) (:y pos) (assoc pos :z-index z-index))

        (.setAttribute el model/attr-open "")
        (gobj/set el k-layer layer)

        (js/requestAnimationFrame
         (fn []
           (let [^js panel (get-panel layer)]
             (when panel
               (let [pw  (.-offsetWidth panel)
                     ph  (.-offsetHeight panel)
                     pos2 (model/compute-position
                            placement offset anchor
                            {:width pw :height ph}
                            {:width vw :height vh} 8)]
                 (position-layer! layer (:x pos2) (:y pos2) (assoc pos2 :z-index z-index)))
               (focus-first! panel)))))

        (dispatch! el model/event-open false #js {:reason reason})))))

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

(defn- make-constructor []
  (let [ctor-ref (atom nil)
        ctor     (fn [] (js/Reflect.construct js/HTMLElement #js [] @ctor-ref))]
    (reset! ctor-ref ctor)
    ctor))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (let [proto (js/Object.create (.-prototype js/HTMLElement))
          ctor  (make-constructor)]
      (js/Object.setPrototypeOf ctor js/HTMLElement)
      (aset proto "constructor" ctor)

      (define-bool-prop! proto "open"      model/attr-open)
      (define-bool-prop! proto "disabled"  model/attr-disabled)
      (define-str-prop!  proto "placement" model/attr-placement)
      (define-methods!   proto)

      (aset proto "connectedCallback"
            (fn [] (this-as ^js this (connected! this))))
      (aset proto "disconnectedCallback"
            (fn [] (this-as ^js this (disconnected! this))))
      (aset proto "attributeChangedCallback"
            (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

      (aset ctor "observedAttributes" model/observed-attributes)
      (aset ctor "prototype" proto)

      (.define js/customElements model/tag-name ctor))))
