(ns baredom.components.x-menu.x-menu
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-menu.model :as model]))

(def ^:private key-refs "__xMenuRefs")
(def ^:private key-handlers "__xMenuHandlers")
(def ^:private key-init "__xMenuInit")
(def ^:private key-model "__xMenuModel")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private rk-base         "base")
(def ^:private rk-popup        "popup")
(def ^:private rk-trigger-slot "trigger-slot")
(def ^:private rk-item-slot    "item-slot")
(def ^:private hk-click        "click")
(def ^:private hk-doc-click    "doc-click")
(def ^:private hk-keydown      "keydown")
(def ^:private hk-item-select  "item-select")
(def ^:private attr-part           "part")
(def ^:private attr-name           "name")
(def ^:private attr-role           "role")
(def ^:private attr-data-placement "data-placement")
(def ^:private attr-aria-label     "aria-label")
(def ^:private part-base       "base")
(def ^:private part-popup      "popup")
(def ^:private slot-trigger    "trigger")
(def ^:private role-menu       "menu")
(def ^:private ev-click        "click")
(def ^:private ev-keydown      "keydown")

(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;position:relative;color-scheme:light dark;"
   "--x-menu-bg:var(--x-color-bg,#ffffff);"
   "--x-menu-border:1px solid var(--x-color-border,#e5e7eb);"
   "--x-menu-border-radius:var(--x-radius-md,8px);"
   "--x-menu-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.12));"
   "--x-menu-min-width:160px;"
   "--x-menu-padding:4px;"
   "--x-menu-z-index:1000;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-menu-bg:var(--x-color-bg,#1f2937);"
   "--x-menu-border:1px solid var(--x-color-border,#374151);"
   "--x-menu-shadow:var(--x-shadow-md,0 4px 16px rgba(0,0,0,0.4));}}"
   ".base{position:relative;display:inline-block;}"
   ".popup{"
   "display:none;position:absolute;"
   "background:var(--x-menu-bg);"
   "border:var(--x-menu-border);"
   "border-radius:var(--x-menu-border-radius);"
   "box-shadow:var(--x-menu-shadow);"
   "min-width:var(--x-menu-min-width);"
   "max-width:calc(100vw - 1rem);"
   "padding:var(--x-menu-padding);"
   "z-index:var(--x-menu-z-index);"
   "box-sizing:border-box;}"
   ":host([open]) .popup{display:block;}"
   ".popup[data-placement='bottom-start']{top:100%;left:0;margin-top:4px;}"
   ".popup[data-placement='bottom-end']{top:100%;right:0;margin-top:4px;}"
   ".popup[data-placement='top-start']{bottom:100%;left:0;margin-bottom:4px;}"
   ".popup[data-placement='top-end']{bottom:100%;right:0;margin-bottom:4px;}"))

(defn- read-model [^js el]
  (model/derive-state
   {:open (du/has-attr? el model/attr-open)
    :placement (du/get-attr el model/attr-placement)
    :label (du/get-attr el model/attr-label)}))

(defn- get-focusable-items [^js el]
  (let [all (.querySelectorAll el "x-menu-item")
        result #js []]
    (dotimes [i (alength all)]
      (let [item (aget all i)]
        (when (and (not (du/has-attr? item "disabled"))
                   (not= (du/get-attr item "type") "divider"))
          (.push result item))))
    result))

(defn- focus-item! [^js item]
  (.focus item))

(defn- get-trigger [^js el]
  (let [refs (du/getv el key-refs)
        trigger-slot (when refs (gobj/get refs rk-trigger-slot))
        assigned (when trigger-slot (.assignedElements trigger-slot))]
    (when (and assigned (> (alength assigned) 0))
      (aget assigned 0))))

(defn- open-menu! [^js el focus-target]
  (when-not (du/has-attr? el model/attr-open)
    (du/set-attr! el model/attr-open "")
    (du/dispatch! el model/event-open #js {}))
  (when focus-target
    (let [items (get-focusable-items el)
          n (alength items)]
      (when (> n 0)
        (cond
          (= focus-target :first) (focus-item! (aget items 0))
          (= focus-target :last)  (focus-item! (aget items (dec n))))))))

(defn- close-menu! [^js el return-focus?]
  (when (du/has-attr? el model/attr-open)
    (du/remove-attr! el model/attr-open)
    (du/dispatch! el model/event-close #js {}))
  (when return-focus?
    (when-let [trigger (get-trigger el)]
      (.focus trigger))))

(defn- trigger-clicked? [^js trigger-slot ^js evt]
  (let [path (.composedPath evt)
        assigned (array-seq (.assignedElements trigger-slot))]
    (boolean (some (fn [node] (> (.indexOf path node) -1)) assigned))))

(defn- handle-el-click! [^js el ^js evt]
  (let [refs (du/getv el key-refs)
        trigger-slot (when refs (gobj/get refs rk-trigger-slot))]
    (when (and trigger-slot (trigger-clicked? trigger-slot evt))
      (if (du/has-attr? el model/attr-open)
        (close-menu! el true)
        (open-menu! el nil)))))

(defn- handle-doc-click! [^js el ^js evt]
  (when (du/has-attr? el model/attr-open)
    (let [path (.composedPath evt)]
      (when (= -1 (.indexOf path el))
        (close-menu! el false)))))

(defn- handle-keydown! [^js el ^js evt]
  (let [k (.-key evt)
        is-open? (du/has-attr? el model/attr-open)]
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

(defn- handle-item-select! [^js el ^js evt]
  (.stopPropagation evt)
  (let [value (.. evt -detail -value)]
    (close-menu! el false)
    (du/dispatch! el model/event-select #js {:value (or value "")})))

(defn- apply-model! [^js el m]
  (let [refs (du/getv el key-refs)
        ^js popup (when refs (gobj/get refs rk-popup))]
    (when popup
      (.setAttribute popup attr-data-placement (:placement m))
      (let [label (:label m)]
        (if (and label (not= label ""))
          (.setAttribute popup attr-aria-label label)
          (.removeAttribute popup attr-aria-label))))
    (du/setv! el key-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el key-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(defn- init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        trigger-slot (.createElement js/document "slot")
        popup (.createElement js/document "div")
        item-slot (.createElement js/document "slot")
        refs #js {}]
    (set! (.-textContent style) style-text)
    (.setAttribute base attr-part part-base)
    (set! (.-className base) part-base)
    (.setAttribute trigger-slot attr-name slot-trigger)
    (.setAttribute popup attr-part part-popup)
    (set! (.-className popup) part-popup)
    (.setAttribute popup attr-role role-menu)
    (.appendChild popup item-slot)
    (.appendChild base trigger-slot)
    (.appendChild base popup)
    (.appendChild root style)
    (.appendChild root base)
    (gobj/set refs rk-base base)
    (gobj/set refs rk-popup popup)
    (gobj/set refs rk-trigger-slot trigger-slot)
    (gobj/set refs rk-item-slot item-slot)
    (du/setv! el key-refs refs)))

(defn- install-listeners! [^js el]
  (let [on-click       (fn handle-host-click [^js e] (handle-el-click! el e))
        on-doc-click   (fn handle-doc-click* [^js e] (handle-doc-click! el e))
        on-keydown     (fn handle-host-keydown [^js e] (handle-keydown! el e))
        on-item-select (fn handle-item-select* [^js e] (handle-item-select! el e))
        handlers #js {}]
    (.addEventListener el ev-click on-click)
    (.addEventListener js/document ev-click on-doc-click true)
    (.addEventListener el ev-keydown on-keydown)
    (.addEventListener el model/event-item-select on-item-select)
    (gobj/set handlers hk-click on-click)
    (gobj/set handlers hk-doc-click on-doc-click)
    (gobj/set handlers hk-keydown on-keydown)
    (gobj/set handlers hk-item-select on-item-select)
    (du/setv! el key-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [handlers (du/getv el key-handlers)]
    (when handlers
      (let [on-click       (gobj/get handlers hk-click)
            on-doc-click   (gobj/get handlers hk-doc-click)
            on-keydown     (gobj/get handlers hk-keydown)
            on-item-select (gobj/get handlers hk-item-select)]
        (.removeEventListener el ev-click on-click)
        (.removeEventListener js/document ev-click on-doc-click true)
        (.removeEventListener el ev-keydown on-keydown)
        (.removeEventListener el model/event-item-select on-item-select)))))

(defn- init-element! [^js el]
  (when-not (du/initialized? el key-init)
    (init-dom! el)
    (install-listeners! el)
    (du/mark-initialized! el key-init))
  (update-from-attrs! el)
  el)

(defn- connected! [^js el]
  (if (du/initialized? el key-init)
    (do (install-listeners! el) (update-from-attrs! el))
    (init-element! el)))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val) (du/initialized? el key-init))
    (update-from-attrs! el)))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
