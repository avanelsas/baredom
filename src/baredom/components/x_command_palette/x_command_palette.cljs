(ns baredom.components.x-command-palette.x-command-palette
  (:require [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.components.x-command-palette.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance-field keys (gobj-safe)
;; ---------------------------------------------------------------------------

(def ^:private k-refs     "__xCommandPaletteRefs")
(def ^:private k-handlers "__xCommandPaletteHandlers")
(def ^:private k-items    "__xCommandPaletteItems")
(def ^:private k-query    "__xCommandPaletteQuery")
(def ^:private k-active-idx "__xCommandPaletteActiveIdx")

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- make-el
  [^js tag]
  (.createElement js/document tag))

(defn- get-ref
  [^js el k]
  (gobj/get (gobj/get el k-refs) (name k)))

(defn- read-model
  "Read all observed attrs from the element and normalize."
  [^js el]
  (model/normalize
   {:open-present?       (du/has-attr? el model/attr-open)
    :modal-raw           (du/get-attr el model/attr-modal)
    :dismissible-raw     (du/get-attr el model/attr-dismissible)
    :disabled-raw        (du/get-attr el model/attr-disabled)
    :no-scrim-raw        (du/get-attr el model/attr-no-scrim)
    :close-on-scrim-raw  (du/get-attr el model/attr-close-on-scrim)
    :close-on-escape-raw (du/get-attr el model/attr-close-on-escape)
    :label-raw           (du/get-attr el model/attr-label)
    :placeholder-raw     (du/get-attr el model/attr-placeholder)
    :empty-text-raw      (du/get-attr el model/attr-empty-text)}))

(defn- dispatch!
  [^js el event-name cancelable detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail   detail
                     :bubbles  true
                     :composed true
                     :cancelable (boolean cancelable)})]
    (.dispatchEvent el ev)
    ev))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------

(def ^:private style-text
  (str
   ":host{display:contents;}"
   "[part=overlay]{"
   "display:none;position:fixed;inset:0;"
   "background:var(--x-command-palette-backdrop,rgba(0,0,0,0.45));"
   "z-index:var(--x-command-palette-z,800);"
   "}"
   "[part=panel]{"
   "display:none;position:fixed;"
   "top:20vh;left:50%;transform:translateX(-50%);"
   "width:var(--x-command-palette-width,560px);"
   "max-width:calc(100vw - 2rem);"
   "max-height:60dvh;"
   "z-index:calc(var(--x-command-palette-z,800) + 1);"
   "flex-direction:column;"
   "border-radius:var(--x-command-palette-radius,var(--x-radius-lg,12px));"
   "background:var(--x-command-palette-bg,var(--x-color-bg,#fff));"
   "box-shadow:var(--x-command-palette-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.25),0 4px 16px rgba(0,0,0,0.12)));"
   "overflow:hidden;"
   "}"
   ":host([open]) [part=overlay]{display:block;}"
   ":host([open]) [part=panel]{display:flex;}"
   "[part=search-wrap]{"
   "display:flex;align-items:center;gap:8px;"
   "padding:12px 16px;"
   "border-bottom:1px solid var(--x-command-palette-divider,rgba(0,0,0,0.08));"
   "}"
   "[part=search-icon]{"
   "flex:none;display:inline-flex;align-items:center;justify-content:center;"
   "width:18px;height:18px;"
   "color:var(--x-command-palette-icon-color,var(--x-color-text-muted,#64748b));"
   "}"
   "[part=input]{"
   "flex:1;border:none;outline:none;background:transparent;"
   "font-size:1rem;line-height:1.5;"
   "color:var(--x-command-palette-text,inherit);"
   "}"
   "[part=input]::placeholder{"
   "color:var(--x-command-palette-placeholder,var(--x-color-text-muted,#94a3b8));"
   "}"
   "[part=clear-btn]{"
   "all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"
   "width:24px;height:24px;border-radius:50%;"
   "color:var(--x-command-palette-icon-color,var(--x-color-text-muted,#64748b));"
   "}"
   "[part=clear-btn]:focus-visible{outline:2px solid var(--x-command-palette-focus-ring,var(--x-color-focus-ring,#60a5fa));}"
   "[part=clear-btn][hidden]{display:none;}"
   "[part=list-wrap]{"
   "flex:1;overflow-y:auto;overscroll-behavior:contain;"
   "padding:8px 0;"
   "}"
   "[part=list]{display:flex;flex-direction:column;}"
   "[part=item]{"
   "padding:10px 16px;cursor:default;"
   "font-size:var(--x-font-size-sm,0.9375rem);line-height:1.4;"
   "color:var(--x-command-palette-item-text,inherit);"
   "border-radius:0;"
   "outline:none;"
   "}"
   "[part=item]:hover,[part=item]:focus{"
   "background:var(--x-command-palette-item-hover,var(--x-color-surface-active,#f1f5f9));"
   "}"
   "[part=item][aria-selected=true]{"
   "background:var(--x-command-palette-item-active,#e0e7ff);"
   "color:var(--x-command-palette-item-active-text,#3730a3);"
   "}"
   "[part=item][aria-disabled=true]{opacity:0.45;cursor:default;pointer-events:none;}"
   "[part=group-header]{"
   "padding:8px 16px 4px;"
   "font-size:0.75rem;font-weight:600;letter-spacing:0.05em;text-transform:uppercase;"
   "color:var(--x-command-palette-group-text,var(--x-color-text-muted,#94a3b8));"
   "}"
   "[part=empty]{"
   "padding:32px 16px;text-align:center;"
   "color:var(--x-command-palette-empty-text,var(--x-color-text-muted,#94a3b8));"
   "font-size:0.9375rem;"
   "}"
   "[part=empty][hidden]{display:none;}"
   "@media (prefers-color-scheme:dark){"
   "[part=panel]{background:var(--x-command-palette-bg,var(--x-color-bg,#1e293b));box-shadow:var(--x-command-palette-shadow,var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.6),0 4px 16px rgba(0,0,0,0.4)));}"
   "[part=search-wrap]{border-bottom-color:var(--x-command-palette-divider,rgba(255,255,255,0.08));}"
   "[part=item]:hover,[part=item]:focus{background:var(--x-command-palette-item-hover,var(--x-color-text-muted,#334155));}"
   "[part=item][aria-selected=true]{background:var(--x-command-palette-item-active,#312e81);color:var(--x-command-palette-item-active-text,#c7d2fe);}"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel],[part=overlay]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------

(defn- make-shadow!
  "Attach shadow root, build DOM, store refs. Returns refs map."
  [^js el]
  (let [^js root  (.attachShadow el #js {:mode "open"})
        ^js style (make-el "style")

        ;; Overlay / scrim
        ^js overlay (make-el "div")

        ;; Panel (dialog)
        ^js panel (make-el "div")

        ;; Search area
        ^js search-wrap  (make-el "div")
        ^js search-icon  (make-el "span")
        ^js input-el     (make-el "input")
        ^js clear-btn    (make-el "button")

        ;; List
        ^js list-wrap (make-el "div")
        ^js list-el   (make-el "div")
        ^js empty-el  (make-el "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute overlay "part" "overlay")

    (.setAttribute panel "part" "panel")
    (.setAttribute panel "role" "dialog")
    (.setAttribute panel "aria-modal" "true")

    (.setAttribute search-wrap "part" "search-wrap")

    (.setAttribute search-icon "part" "search-icon")
    (.setAttribute search-icon "aria-hidden" "true")
    (set! (.-textContent search-icon) "\uD83D\uDD0D")

    (.setAttribute input-el "part" "input")
    (.setAttribute input-el "type" "search")
    (.setAttribute input-el "role" "combobox")
    (.setAttribute input-el "autocomplete" "off")
    (.setAttribute input-el "autocorrect" "off")
    (.setAttribute input-el "spellcheck" "false")
    (.setAttribute input-el "aria-autocomplete" "list")
    (.setAttribute input-el "aria-expanded" "false")
    (.setAttribute input-el "aria-controls" "x-cp-list")

    (.setAttribute clear-btn "part" "clear-btn")
    (.setAttribute clear-btn "type" "button")
    (.setAttribute clear-btn "aria-label" "Clear")
    (.setAttribute clear-btn "hidden" "")

    (.setAttribute list-wrap "part" "list-wrap")

    (.setAttribute list-el "part" "list")
    (.setAttribute list-el "id" "x-cp-list")
    (.setAttribute list-el "role" "listbox")
    (.setAttribute list-el "aria-label" "Results")

    (.setAttribute empty-el "part" "empty")
    (.setAttribute empty-el "hidden" "")

    (.appendChild search-wrap search-icon)
    (.appendChild search-wrap input-el)
    (.appendChild search-wrap clear-btn)

    (.appendChild list-wrap list-el)
    (.appendChild list-wrap empty-el)

    (.appendChild panel search-wrap)
    (.appendChild panel list-wrap)

    (.appendChild root style)
    (.appendChild root overlay)
    (.appendChild root panel)

    (let [refs #js {:overlay    overlay
                    :panel      panel
                    :input      input-el
                    :clear-btn  clear-btn
                    :list       list-el
                    :empty      empty-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Item rendering
;; ---------------------------------------------------------------------------

(defn- render-items!
  "Clear list and re-render items filtered by current query."
  [^js el]
  (let [refs      (gobj/get el k-refs)]
    (when refs
      (let [^js list-el  (gobj/get refs "list")
            ^js empty-el (gobj/get refs "empty")
            ^js input-el (gobj/get refs "input")
            items-js     (gobj/get el k-items)
            query        (or (gobj/get el k-query) "")
            items        (model/normalize-items items-js)
            {:keys [visible]} (model/filter-items items query)
            active-idx   (or (gobj/get el k-active-idx) 0)]

        (set! (.-textContent list-el) "")

        (if (empty? visible)
          (do
            (.removeAttribute empty-el "hidden")
            (when input-el (.removeAttribute input-el "aria-activedescendant")))
          (do
            (.setAttribute empty-el "hidden" "")
            (loop [remaining (map-indexed vector visible)
                   last-group nil]
              (when-let [[idx item] (first remaining)]
                (let [g (:group item)]
                  (when (and g (not= g last-group))
                    (let [^js header (make-el "div")]
                      (.setAttribute header "part" "group-header")
                      (.setAttribute header "aria-hidden" "true")
                      (set! (.-textContent header) g)
                      (.appendChild list-el header)))
                  (let [^js div (make-el "div")]
                    (.setAttribute div "part" "item")
                    (.setAttribute div "role" "option")
                    (.setAttribute div "id" (str "x-cp-item-" idx))
                    (.setAttribute div "data-id" (str (:id item)))
                    (.setAttribute div "data-idx" (str idx))
                    (.setAttribute div "tabindex" "-1")
                    (when (:disabled? item)
                      (.setAttribute div "aria-disabled" "true"))
                    (set! (.-textContent div) (:label item))
                    (when (= idx active-idx)
                      (.setAttribute div "aria-selected" "true"))
                    (.appendChild list-el div))
                  (recur (rest remaining) (or g last-group)))))
            (when input-el
              (.setAttribute input-el "aria-activedescendant"
                             (str "x-cp-item-" active-idx)))))))))

;; ---------------------------------------------------------------------------
;; Full render (attrs + items)
;; ---------------------------------------------------------------------------

(defn- render!
  "Sync all shadow DOM parts with current element state."
  [^js el]
  (let [refs (gobj/get el k-refs)]
    (when refs
      (let [m           (read-model el)
            ^js panel   (gobj/get refs "panel")
            ^js input   (gobj/get refs "input")
            ^js overlay (gobj/get refs "overlay")
            ^js empty-el (gobj/get refs "empty")
            query       (or (gobj/get el k-query) "")
            label       (:label m)
            placeholder (:placeholder m)
            empty-text  (:empty-text m)]

        ;; Panel aria-label
        (if label
          (.setAttribute panel "aria-label" label)
          (.setAttribute panel "aria-label" "Command palette"))

        ;; Scrim pointer-events
        (if (:scrim? m)
          (.removeAttribute overlay "hidden")
          (.setAttribute overlay "hidden" ""))

        ;; Input sync
        (.setAttribute input "placeholder" placeholder)
        (.setAttribute input "aria-expanded" (if (:open? m) "true" "false"))
        (when (:disabled? m)
          (.setAttribute input "disabled" "")
          (.setAttribute input "aria-disabled" "true"))
        (when-not (:disabled? m)
          (.removeAttribute input "disabled")
          (.removeAttribute input "aria-disabled"))

        ;; Empty text
        (set! (.-textContent empty-el) empty-text)

        (render-items! el)))))

;; ---------------------------------------------------------------------------
;; Open / close
;; ---------------------------------------------------------------------------

(defn- do-open!
  "Actually open: set attr, focus input, dispatch open event."
  [^js el]
  (.setAttribute el model/attr-open "")
  (let [refs  (gobj/get el k-refs)
        ^js input (when refs (gobj/get refs "input"))]
    (when input (.focus input)))
  (render! el)
  (dispatch! el model/event-open false #js {}))

(defn- do-close!
  "Actually close: remove attr, dispatch close event."
  [^js el]
  (.removeAttribute el model/attr-open)
  (let [refs (gobj/get el k-refs)
        ^js input (when refs (gobj/get refs "input"))]
    (when input (.removeAttribute input "aria-activedescendant")))
  (render! el)
  (dispatch! el model/event-close false #js {}))

(defn- request-open!
  [^js el]
  (when-not (.hasAttribute el model/attr-open)
    (let [^js ev (dispatch! el model/event-open-request true #js {})]
      (when-not (.-defaultPrevented ev)
        (do-open! el)))))

(defn- request-close!
  [^js el]
  (when (.hasAttribute el model/attr-open)
    (let [^js ev (dispatch! el model/event-close-request true #js {})]
      (when-not (.-defaultPrevented ev)
        (do-close! el)))))

;; ---------------------------------------------------------------------------
;; Keyboard navigation
;; ---------------------------------------------------------------------------

(defn- active-visible
  [^js el]
  (let [items-js (gobj/get el k-items)
        query    (or (gobj/get el k-query) "")
        items    (model/normalize-items items-js)
        {:keys [visible]} (model/filter-items items query)]
    visible))

(defn- move-active!
  [^js el direction]
  (let [visible   (active-visible el)
        cur-idx   (or (gobj/get el k-active-idx) 0)
        new-idx   (if (= direction :down)
                    (model/next-active-idx visible cur-idx)
                    (model/prev-active-idx visible cur-idx))]
    (gobj/set el k-active-idx new-idx)
    (render-items! el)
    ;; Scroll active item into view
    (let [refs    (gobj/get el k-refs)
          ^js list-el (when refs (gobj/get refs "list"))
          ^js item-el (when list-el
                        (.querySelector list-el (str "[data-idx='" new-idx "']")))]
      (when item-el
        (.scrollIntoView item-el #js {:block "nearest"})))))

(defn- select-active!
  [^js el]
  (let [visible  (active-visible el)
        cur-idx  (or (gobj/get el k-active-idx) 0)
        item     (nth visible cur-idx nil)]
    (when (and item (not (:disabled? item)))
      (let [^js ev (dispatch! el model/event-select-request true
                              #js {:item (clj->js item)})]
        (when-not (.-defaultPrevented ev)
          (dispatch! el model/event-select false #js {:item (clj->js item)})
          (request-close! el))))))

;; ---------------------------------------------------------------------------
;; Event listeners
;; ---------------------------------------------------------------------------

(defn- on-input-input!
  [^js el ^js _e]
  (let [refs    (gobj/get el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        ^js clr (when refs (gobj/get refs "clear-btn"))
        q       (when inp (.-value inp))]
    (gobj/set el k-query (or q ""))
    (gobj/set el k-active-idx 0)
    (when clr
      (if (and q (not= q ""))
        (.removeAttribute clr "hidden")
        (.setAttribute clr "hidden" "")))
    (dispatch! el model/event-query-change false #js {:query (or q "")})
    (render-items! el)))

(defn- on-input-keydown!
  [^js el ^js e]
  (let [key (.-key e)]
    (cond
      (= key "ArrowDown")
      (do (.preventDefault e) (move-active! el :down))

      (= key "ArrowUp")
      (do (.preventDefault e) (move-active! el :up))

      (= key "Enter")
      (do (.preventDefault e) (select-active! el))

      (= key "Escape")
      (let [m (read-model el)]
        (when (:close-on-escape? m)
          (request-close! el))))))

(defn- on-scrim-click!
  [^js el ^js _e]
  (let [m (read-model el)]
    (when (:close-on-scrim? m)
      (request-close! el))))

(defn- on-list-click!
  [^js el ^js e]
  (let [^js target (.-target e)
        ^js item-el (.closest target "[part=item]")]
    (when item-el
      (let [idx (js/parseInt (du/get-attr item-el "data-idx") 10)
            visible (active-visible el)
            item (nth visible idx nil)]
        (when (and item (not (:disabled? item)))
          (let [^js ev (dispatch! el model/event-select-request true
                                  #js {:item (clj->js item)})]
            (when-not (.-defaultPrevented ev)
              (dispatch! el model/event-select false #js {:item (clj->js item)})
              (request-close! el))))))))

(defn- on-clear-click!
  [^js el ^js _e]
  (let [refs    (gobj/get el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        ^js clr (when refs (gobj/get refs "clear-btn"))]
    (when inp (set! (.-value inp) ""))
    (when clr (.setAttribute clr "hidden" ""))
    (gobj/set el k-query "")
    (gobj/set el k-active-idx 0)
    (dispatch! el model/event-query-change false #js {:query ""})
    (render-items! el)
    (when inp (.focus inp))))

(defn- add-listeners!
  [^js el]
  (let [refs       (gobj/get el k-refs)
        ^js input  (gobj/get refs "input")
        ^js overlay (gobj/get refs "overlay")
        ^js list-el (gobj/get refs "list")
        ^js clr    (gobj/get refs "clear-btn")

        h-input    (fn [^js e] (on-input-input! el e))
        h-keydown  (fn [^js e] (on-input-keydown! el e))
        h-scrim    (fn [^js e] (on-scrim-click! el e))
        h-list     (fn [^js e] (on-list-click! el e))
        h-clear    (fn [^js e] (on-clear-click! el e))]

    (.addEventListener input "input" h-input)
    (.addEventListener input "keydown" h-keydown)
    (.addEventListener overlay "click" h-scrim)
    (.addEventListener list-el "click" h-list)
    (.addEventListener clr "click" h-clear)

    (gobj/set el k-handlers
              #js {:input   h-input
                   :keydown h-keydown
                   :scrim   h-scrim
                   :list    h-list
                   :clear   h-clear})))

(defn- remove-listeners!
  [^js el]
  (let [refs     (gobj/get el k-refs)
        handlers (gobj/get el k-handlers)]
    (when (and refs handlers)
      (let [^js input   (gobj/get refs "input")
            ^js overlay (gobj/get refs "overlay")
            ^js list-el (gobj/get refs "list")
            ^js clr     (gobj/get refs "clear-btn")]
        (.removeEventListener input "input" (gobj/get handlers "input"))
        (.removeEventListener input "keydown" (gobj/get handlers "keydown"))
        (.removeEventListener overlay "click" (gobj/get handlers "scrim"))
        (.removeEventListener list-el "click" (gobj/get handlers "list"))
        (.removeEventListener clr "click" (gobj/get handlers "clear"))))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------

(defn- connected!
  [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected!
  [^js el]
  (remove-listeners! el))

(defn- attribute-changed!
  [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Public element API helpers
;; ---------------------------------------------------------------------------

(defn- define-bool-prop!
  [^js proto prop-name attr-name]
  (.defineProperty js/Object proto prop-name
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                        (.hasAttribute this attr-name)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr-name "")
                                          (.removeAttribute this attr-name))))}))

(defn- define-items-prop!
  [^js proto]
  (.defineProperty js/Object proto "items"
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-items) #js [])))
                        :set (fn [v]
                               (this-as ^js this
                                        (gobj/set this k-items v)
                                        (gobj/set this k-active-idx 0)
                                        (render-items! this)))}))

(defn- define-methods!
  [^js proto]
  (.defineProperty js/Object proto "open"
                   #js {:configurable true
                        :writable     true
                        :value        (fn [] (this-as ^js this (request-open! this)))})
  (.defineProperty js/Object proto "close"
                   #js {:configurable true
                        :writable     true
                        :value        (fn [] (this-as ^js this (request-close! this)))})
  (.defineProperty js/Object proto "toggle"
                   #js {:configurable true
                        :writable     true
                        :value        (fn []
                                        (this-as ^js this
                                                 (if (.hasAttribute this model/attr-open)
                                                   (request-close! this)
                                                   (request-open! this))))}))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)
    (let [proto (.-prototype klass)]
      (define-bool-prop!  proto "open"     model/attr-open)
      (define-bool-prop!  proto "disabled" model/attr-disabled)
      (define-items-prop! proto)
      (define-methods!    proto)
      (set! (.-connectedCallback proto)
            (fn [] (this-as ^js this (connected! this))))
      (set! (.-disconnectedCallback proto)
            (fn [] (this-as ^js this (disconnected! this))))
      (set! (.-attributeChangedCallback proto)
            (fn [n o v] (this-as ^js this (attribute-changed! this n o v)))))
    klass))

(defn init!
  []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
