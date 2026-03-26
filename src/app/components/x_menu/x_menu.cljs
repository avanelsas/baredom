(ns app.components.x-menu.x-menu
  (:require
   [goog.object :as gobj]
   [app.components.x-menu.model :as model]))

(def key-refs "__xMenuRefs")
(def key-handlers "__xMenuHandlers")
(def key-init "__xMenuInit")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-init)))

(defn mark-initialized! [el]
  (setv! el key-init true))

(def style-text
  (str
   ":host{"
   "display:inline-block;position:relative;color-scheme:light dark;"
   "--x-menu-bg:#ffffff;"
   "--x-menu-border:1px solid #e5e7eb;"
   "--x-menu-border-radius:8px;"
   "--x-menu-shadow:0 4px 16px rgba(0,0,0,0.12);"
   "--x-menu-min-width:160px;"
   "--x-menu-padding:4px;"
   "--x-menu-z-index:1000;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-menu-bg:#1f2937;"
   "--x-menu-border:1px solid #374151;"
   "--x-menu-shadow:0 4px 16px rgba(0,0,0,0.4);}}"
   ".base{position:relative;display:inline-block;}"
   ".popup{"
   "display:none;position:absolute;"
   "background:var(--x-menu-bg);"
   "border:var(--x-menu-border);"
   "border-radius:var(--x-menu-border-radius);"
   "box-shadow:var(--x-menu-shadow);"
   "min-width:var(--x-menu-min-width);"
   "padding:var(--x-menu-padding);"
   "z-index:var(--x-menu-z-index);"
   "box-sizing:border-box;}"
   ":host([open]) .popup{display:block;}"
   ".popup[data-placement='bottom-start']{top:100%;left:0;margin-top:4px;}"
   ".popup[data-placement='bottom-end']{top:100%;right:0;margin-top:4px;}"
   ".popup[data-placement='top-start']{bottom:100%;left:0;margin-bottom:4px;}"
   ".popup[data-placement='top-end']{bottom:100%;right:0;margin-bottom:4px;}"))

(defn read-inputs [^js el]
  {:open (.hasAttribute el model/attr-open)
   :placement (.getAttribute el model/attr-placement)
   :label (.getAttribute el model/attr-label)})

(defn get-focusable-items [^js el]
  (let [all (.querySelectorAll el "x-menu-item")
        result #js []]
    (dotimes [i (alength all)]
      (let [item (aget all i)]
        (when (and (not (.hasAttribute item "disabled"))
                   (not= (.getAttribute item "type") "divider"))
          (.push result item))))
    result))

(defn focus-item! [^js item]
  (.focus item))

(defn get-trigger [^js el]
  (let [refs (getv el key-refs)
        trigger-slot (when refs (gobj/get refs "trigger-slot"))
        assigned (when trigger-slot (.assignedElements trigger-slot))]
    (when (and assigned (> (alength assigned) 0))
      (aget assigned 0))))

(defn dispatch-event! [^js el event-name ^js detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail detail
         :bubbles true
         :composed true})))

(defn open-menu! [^js el focus-target]
  (when-not (.hasAttribute el model/attr-open)
    (.setAttribute el model/attr-open "")
    (dispatch-event! el model/event-open #js {}))
  (when focus-target
    (let [items (get-focusable-items el)
          n (alength items)]
      (when (> n 0)
        (cond
          (= focus-target :first) (focus-item! (aget items 0))
          (= focus-target :last)  (focus-item! (aget items (dec n))))))))

(defn close-menu! [^js el return-focus?]
  (when (.hasAttribute el model/attr-open)
    (.removeAttribute el model/attr-open)
    (dispatch-event! el model/event-close #js {}))
  (when return-focus?
    (when-let [trigger (get-trigger el)]
      (.focus trigger))))

(defn trigger-clicked? [^js trigger-slot ^js evt]
  (let [path (.composedPath evt)
        assigned (array-seq (.assignedElements trigger-slot))]
    (boolean (some (fn [node] (> (.indexOf path node) -1)) assigned))))

(defn handle-el-click! [^js el ^js evt]
  (let [refs (getv el key-refs)
        trigger-slot (when refs (gobj/get refs "trigger-slot"))]
    (when (and trigger-slot (trigger-clicked? trigger-slot evt))
      (if (.hasAttribute el model/attr-open)
        (close-menu! el true)
        (open-menu! el nil)))))

(defn handle-doc-click! [^js el ^js evt]
  (when (.hasAttribute el model/attr-open)
    (let [path (.composedPath evt)]
      (when (= -1 (.indexOf path el))
        (close-menu! el false)))))

(defn handle-keydown! [^js el ^js evt]
  (let [k (.-key evt)
        is-open? (.hasAttribute el model/attr-open)]
    (cond
      (= k "Escape")
      (when is-open?
        (.preventDefault evt)
        (close-menu! el true))

      (= k "Tab")
      (when is-open?
        (close-menu! el false))

      (or (= k "ArrowDown") (= k "ArrowUp"))
      (let [items (get-focusable-items el)
            n (alength items)]
        (when (> n 0)
          (.preventDefault evt)
          (if is-open?
            (let [active (.-activeElement js/document)
                  idx (.indexOf items active)
                  next-idx (if (= k "ArrowDown")
                             (if (>= idx (dec n)) 0 (if (< idx 0) 0 (inc idx)))
                             (if (<= idx 0) (dec n) (dec idx)))]
              (focus-item! (aget items next-idx)))
            (do
              (open-menu! el nil)
              (focus-item! (aget items (if (= k "ArrowDown") 0 (dec n))))))))

      (= k "Home")
      (when is-open?
        (let [items (get-focusable-items el)]
          (when (> (alength items) 0)
            (.preventDefault evt)
            (focus-item! (aget items 0)))))

      (= k "End")
      (when is-open?
        (let [items (get-focusable-items el)]
          (when (> (alength items) 0)
            (.preventDefault evt)
            (focus-item! (aget items (dec (alength items))))))))))

(defn handle-item-select! [^js el ^js evt]
  (.stopPropagation evt)
  (let [value (.. evt -detail -value)]
    (close-menu! el false)
    (dispatch-event! el model/event-select #js {:value (or value "")})))

(defn render! [^js el]
  (let [refs (getv el key-refs)
        popup (when refs (gobj/get refs "popup"))
        state (model/derive-state (read-inputs el))]
    (when popup
      (.setAttribute popup "data-placement" (:placement state))
      (let [label (:label state)]
        (if (and label (not= label ""))
          (.setAttribute popup "aria-label" label)
          (.removeAttribute popup "aria-label"))))))

(defn init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        trigger-slot (.createElement js/document "slot")
        popup (.createElement js/document "div")
        item-slot (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.setAttribute base "part" "base")
    (set! (.-className base) "base")
    (.setAttribute trigger-slot "name" "trigger")
    (.setAttribute popup "part" "popup")
    (set! (.-className popup) "popup")
    (.setAttribute popup "role" "menu")
    (.appendChild popup item-slot)
    (.appendChild base trigger-slot)
    (.appendChild base popup)
    (.appendChild root style)
    (.appendChild root base)
    (let [refs #js {}]
      (gobj/set refs "base" base)
      (gobj/set refs "popup" popup)
      (gobj/set refs "trigger-slot" trigger-slot)
      (gobj/set refs "item-slot" item-slot)
      (setv! el key-refs refs))))

(defn install-listeners! [^js el]
  (let [on-click (fn [^js e] (handle-el-click! el e))
        on-doc-click (fn [^js e] (handle-doc-click! el e))
        on-keydown (fn [^js e] (handle-keydown! el e))
        on-item-select (fn [^js e] (handle-item-select! el e))
        handlers #js {}]
    (.addEventListener el "click" on-click)
    (.addEventListener js/document "click" on-doc-click true)
    (.addEventListener el "keydown" on-keydown)
    (.addEventListener el model/event-item-select on-item-select)
    (gobj/set handlers "click" on-click)
    (gobj/set handlers "doc-click" on-doc-click)
    (gobj/set handlers "keydown" on-keydown)
    (gobj/set handlers "item-select" on-item-select)
    (setv! el key-handlers handlers)))

(defn remove-listeners! [^js el]
  (let [handlers (getv el key-handlers)]
    (when handlers
      (let [on-click (gobj/get handlers "click")
            on-doc-click (gobj/get handlers "doc-click")
            on-keydown (gobj/get handlers "keydown")
            on-item-select (gobj/get handlers "item-select")]
        (.removeEventListener el "click" on-click)
        (.removeEventListener js/document "click" on-doc-click true)
        (.removeEventListener el "keydown" on-keydown)
        (.removeEventListener el model/event-item-select on-item-select)))))

(defn init-element! [^js el]
  (when-not (initialized? el)
    (init-dom! el)
    (install-listeners! el)
    (mark-initialized! el))
  (render! el)
  el)

(defn connected-callback [^js el]
  (if (initialized? el)
    (do (install-listeners! el) (render! el))
    (init-element! el)))

(defn disconnected-callback [^js el]
  (remove-listeners! el))

(defn attribute-changed-callback [^js el _name _old _new]
  (when (initialized? el)
    (render! el)))

(defn install-property-accessors! [klass]
  (.defineProperty
   js/Object (.-prototype klass) "open"
   #js {:get (fn [] (this-as this (.hasAttribute this model/attr-open)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-open "") (.removeAttribute this model/attr-open))))
        :configurable true :enumerable true})
  (.defineProperty
   js/Object (.-prototype klass) "placement"
   #js {:get (fn [] (this-as this (.getAttribute this model/attr-placement)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-placement v) (.removeAttribute this model/attr-placement))))
        :configurable true :enumerable true})
  (.defineProperty
   js/Object (.-prototype klass) "label"
   #js {:get (fn [] (this-as this (.getAttribute this model/attr-label)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-label v) (.removeAttribute this model/attr-label))))
        :configurable true :enumerable true}))

(defn element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)
    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as this (connected-callback this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as this (disconnected-callback this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as this (attribute-changed-callback this n o v))))
    (install-property-accessors! klass)
    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
