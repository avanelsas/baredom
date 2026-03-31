(ns baredom.components.x-tabs.x-tabs
  (:require
   [goog.object :as gobj]
   [baredom.components.x-tabs.model :as model]))

(def key-root "__x_tabs_root")
(def key-base "__x_tabs_base")
(def key-slot "__x_tabs_slot")
(def key-init "__x_tabs_initialized")
(def key-on-tab-select "__x_tabs_on_tab_select")
(def key-on-keydown "__x_tabs_on_keydown")
(def key-observer "__x_tabs_observer")

(defn getv [el k]
  (gobj/get el k))

(defn setv! [el k v]
  (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-init)))

(defn mark-init! [el]
  (setv! el key-init true))

(def style-text
  (str
   ":host{display:block;color-scheme:light dark;}"
   ".base{display:flex;flex-direction:row;}"
   ".base[aria-orientation='vertical']{flex-direction:column;}"
   "::slotted(x-tab){margin-inline-end:var(--x-tabs-gap,8px);}"
   ":host([orientation='vertical']) ::slotted(x-tab){margin-inline-end:0;margin-block-end:var(--x-tabs-gap,8px);}"))

(defn read-inputs [^js el]
  {:value (.getAttribute el model/attr-value)
   :orientation (.getAttribute el model/attr-orientation)
   :activation (.getAttribute el model/attr-activation)
   :label (.getAttribute el model/attr-label)
   :loop (.hasAttribute el model/attr-loop)})

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
  (let [current (.getAttribute el model/attr-value)]
    (cond
      (and value (not= current value))
      (.setAttribute el model/attr-value value)

      (and (or (nil? value) (= value "")) current)
      (.removeAttribute el model/attr-value)

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
  (.dispatchEvent
   el
   (js/CustomEvent.
    model/event-value-change
    #js {:detail #js {:value (or value "")}
         :bubbles true
         :composed true})))

(defn coordinate-tabs! [^js el]
  (let [tabs (get-tabs el)
        host-value (.getAttribute el model/attr-value)
        selected-value (effective-selected-value tabs host-value)]
    (when selected-value
      (select-tab! tabs selected-value)
      (update-panels! tabs selected-value)
      (set-host-value-if-needed! el selected-value))
    selected-value))

(defn activate-tab-by-value! [^js el value]
  (let [current (.getAttribute el model/attr-value)]
    (when (and value (not= current value))
      (set-host-value-if-needed! el value)
      (coordinate-tabs! el)
      (dispatch-value-change! el value))))

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
  (let [base (getv el key-base)
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
    (setv! el key-observer observer)))

(defn disconnect-mutation-observer! [^js el]
  (when-let [observer (getv el key-observer)]
    (.disconnect observer)))

(defn install-listeners! [^js el]
  (let [on-tab-select* (fn [e] (on-tab-select el e))
        on-keydown* (fn [e] (handle-arrow el e))]
    (.addEventListener el "tab-select" on-tab-select*)
    (.addEventListener el "keydown" on-keydown*)
    (setv! el key-on-tab-select on-tab-select*)
    (setv! el key-on-keydown on-keydown*)
    (install-mutation-observer! el)))

(defn remove-listeners! [^js el]
  (when-let [on-tab-select* (getv el key-on-tab-select)]
    (.removeEventListener el "tab-select" on-tab-select*))
  (when-let [on-keydown* (getv el key-on-keydown)]
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
    (setv! el key-root root)
    (setv! el key-base base)
    (setv! el key-slot slot)))

(defn init-element! [^js el]
  (when-not (initialized? el)
    (init-dom! el)
    (install-listeners! el)
    (mark-init! el))
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
   js/Object
   (.-prototype klass)
   "value"
   #js {:get (fn []
               (this-as this
                 (.getAttribute this model/attr-value)))
        :set (fn [v]
               (this-as this
                 (if v
                   (.setAttribute this model/attr-value v)
                   (.removeAttribute this model/attr-value))))
        :configurable true
        :enumerable true}))

(defn element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass)
          model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as this
              (connected-callback this))))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as this
              (disconnected-callback this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [name old new]
            (this-as this
              (attribute-changed-callback this name old new))))

    (install-property-accessors! klass)
    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
