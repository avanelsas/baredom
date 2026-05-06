(ns baredom.components.x-tabs.x-tabs
  (:require
[baredom.utils.component :as component]
               [baredom.utils.dom :as du]
   [baredom.components.x-tabs.model :as model]))

(def key-root "__xTabsRoot")
(def key-base "__xTabsBase")
(def key-slot "__xTabsSlot")
(def key-init "__xTabsInitialized")
(def key-on-tab-select "__xTabsOnTabSelect")
(def key-on-keydown "__xTabsOnKeydown")
(def key-observer "__xTabsObserver")

(def style-text
  (str
   ":host{display:block;color-scheme:light dark;}"
   ".base{display:flex;flex-direction:row;}"
   ".base[aria-orientation='vertical']{flex-direction:column;}"
   "::slotted(x-tab){margin-inline-end:var(--x-tabs-gap,8px);}"
   ":host([orientation='vertical']) ::slotted(x-tab){margin-inline-end:0;margin-block-end:var(--x-tabs-gap,8px);}"))

(defn read-inputs [^js el]
  {:value (du/get-attr el model/attr-value)
   :orientation (du/get-attr el model/attr-orientation)
   :activation (du/get-attr el model/attr-activation)
   :label (du/get-attr el model/attr-label)
   :loop (du/has-attr? el model/attr-loop)})

(defn direct-children [^js el]
  (array-seq (.-children el)))

(defn get-tabs [^js el]
  (->> (direct-children el)
       (filter #(= "X-TAB" (.-tagName %)))
       vec))

(defn enabled-tab? [^js tab]
  (not (.hasAttribute tab "disabled")))

(defn tab-value [^js tab]
  (.getAttribute tab "value"))

(defn first-enabled-tab [tabs]
  (some (fn [tab]
          (when (enabled-tab? tab)
            tab))
        tabs))

(defn derive-initial-selection [tabs]
  (when-let [tab (first-enabled-tab tabs)]
    (tab-value tab)))

(defn set-host-value-if-needed! [^js el value]
  (let [current (du/get-attr el model/attr-value)]
    (cond
      (and value (not= current value))
      (du/set-attr! el model/attr-value value)

      (and (or (nil? value) (= value "")) current)
      (du/remove-attr! el model/attr-value)

      :else nil)))

(defn set-tab-selected-if-needed! [^js tab selected?]
  (let [has-selected? (.hasAttribute tab "selected")]
    (cond
      (and selected? (not has-selected?))
      (.setAttribute tab "selected" "")

      (and (not selected?) has-selected?)
      (.removeAttribute tab "selected")

      :else nil)))

(defn set-tab-tabindex-if-needed! [^js tab tabindex]
  (let [current (.getAttribute tab "tabindex")]
    (when (not= current tabindex)
      (.setAttribute tab "tabindex" tabindex))))

(defn select-tab! [tabs value]
  (doseq [tab tabs]
    (let [selected? (= (tab-value tab) value)]
      (set-tab-selected-if-needed! tab selected?)
      (if (and selected? (enabled-tab? tab))
        (set-tab-tabindex-if-needed! tab "0")
        (set-tab-tabindex-if-needed! tab "-1")))))

(defn hide-panel! [^js tab visible?]
  (let [id (.getAttribute tab "controls")]
    (when (and id (not= id ""))
      (when-let [panel (.getElementById js/document id)]
        (if visible?
          (.removeAttribute panel "hidden")
          (.setAttribute panel "hidden" ""))))))

(defn update-panels! [tabs selected-value]
  (doseq [tab tabs]
    (hide-panel! tab (= (tab-value tab) selected-value))))

(defn effective-selected-value [tabs host-value]
  (let [values (set (keep tab-value tabs))]
    (cond
      (and host-value (contains? values host-value)) host-value
      :else (derive-initial-selection tabs))))

(defn dispatch-value-change! [^js el value]
  (du/dispatch! el model/event-value-change #js {:value (or value "")}))

(defn coordinate-tabs! [^js el]
  (let [tabs (get-tabs el)
        host-value (du/get-attr el model/attr-value)
        selected-value (effective-selected-value tabs host-value)]
    (when selected-value
      (select-tab! tabs selected-value)
      (update-panels! tabs selected-value)
      (set-host-value-if-needed! el selected-value))
    selected-value))

(defn activate-tab-by-value! [^js el value]
  (let [current (du/get-attr el model/attr-value)]
    (when (and value (not= current value))
      (let [allowed? (du/dispatch-cancelable!
                      el model/event-change-request
                      #js {:value value :previousValue (or current "")})]
        (when allowed?
          (set-host-value-if-needed! el value)
          (coordinate-tabs! el)
          (dispatch-value-change! el value))))))

(defn on-tab-select [^js el ^js e]
  (.stopPropagation e)
  (let [value (.. e -detail -value)]
    (activate-tab-by-value! el value)))

(defn focus-tab! [tabs idx]
  (when-let [^js tab (nth tabs idx nil)]
    (.focus tab)
    tab))

(defn next-index [idx tabs loop?]
  (let [last-idx (dec (count tabs))
        candidate (inc idx)]
    (cond
      (<= candidate last-idx) candidate
      loop? 0
      :else idx)))

(defn prev-index [idx tabs loop?]
  (let [candidate (dec idx)
        last-idx (dec (count tabs))]
    (cond
      (>= candidate 0) candidate
      loop? last-idx
      :else idx)))

(defn handle-arrow [^js el ^js e]
  (let [tabs (vec (filter enabled-tab? (get-tabs el)))
        state (model/derive-state (read-inputs el))
        orientation (:orientation state)
        activation (:activation state)
        loop? (:loop state)
        key (.-key e)
        active (.-activeElement js/document)
        idx (.indexOf tabs active)]
    (when (>= idx 0)
      (let [target-idx
            (cond
              (and (= orientation "horizontal") (= key "ArrowRight"))
              (next-index idx tabs loop?)

              (and (= orientation "horizontal") (= key "ArrowLeft"))
              (prev-index idx tabs loop?)

              (and (= orientation "vertical") (= key "ArrowDown"))
              (next-index idx tabs loop?)

              (and (= orientation "vertical") (= key "ArrowUp"))
              (prev-index idx tabs loop?)

              (= key "Home")
              0

              (= key "End")
              (dec (count tabs))

              :else nil)]
        (when (some? target-idx)
          (.preventDefault e)
          (when-let [tab (focus-tab! tabs target-idx)]
            (when (= activation "auto")
              (activate-tab-by-value! el (tab-value tab)))))))))

(defn render! [^js el]
  (let [base (du/getv el key-base)
        state (model/derive-state (read-inputs el))]
    (when base
      (.setAttribute base "role" "tablist")
      (.setAttribute base "aria-orientation" (:orientation state))
      (let [label (:label state)]
        (if (and label (not= label ""))
          (.setAttribute base "aria-label" label)
          (.removeAttribute base "aria-label"))))
    (coordinate-tabs! el)))

(defn install-mutation-observer! [^js el]
  (let [observer (js/MutationObserver.
                  (fn [_records _observer]
                    (render! el)))]
    (.observe observer el #js {:childList true
                               :subtree true
                               :attributes true
                               :attributeFilter #js ["value" "selected" "disabled" "controls"]})
    (du/setv! el key-observer observer)))

(defn disconnect-mutation-observer! [^js el]
  (when-let [observer (du/getv el key-observer)]
    (.disconnect observer)))

(defn install-listeners! [^js el]
  (let [on-tab-select* (fn [e] (on-tab-select el e))
        on-keydown* (fn [e] (handle-arrow el e))]
    (.addEventListener el "tab-select" on-tab-select*)
    (.addEventListener el "keydown" on-keydown*)
    (du/setv! el key-on-tab-select on-tab-select*)
    (du/setv! el key-on-keydown on-keydown*)
    (install-mutation-observer! el)))

(defn remove-listeners! [^js el]
  (when-let [on-tab-select* (du/getv el key-on-tab-select)]
    (.removeEventListener el "tab-select" on-tab-select*))
  (when-let [on-keydown* (du/getv el key-on-keydown)]
    (.removeEventListener el "keydown" on-keydown*))
  (disconnect-mutation-observer! el))

(defn init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        slot (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.setAttribute base "part" "base")
    (set! (.-className base) "base")
    (.appendChild base slot)
    (.appendChild root style)
    (.appendChild root base)
    (du/setv! el key-root root)
    (du/setv! el key-base base)
    (du/setv! el key-slot slot)))

(defn init-element! [^js el]
  (when-not (du/initialized? el key-init)
    (init-dom! el)
    (install-listeners! el)
    (du/mark-initialized! el key-init))
  (render! el)
  el)

(defn- connected! [^js el]
  (if (du/initialized? el key-init)
    (do (install-listeners! el) (render! el))
    (init-element! el)))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (when (du/initialized? el key-init)
    (render! el)))

(defn install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
