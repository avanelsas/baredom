(ns barereplay.dock
  (:require
   [baredom.utils.component :as comp]
   [baredom.utils.dom :as du]
   [barereplay.label :as label]
   [barereplay.reconstruct :as reconstruct]
   [barereplay.store :as store]))

(def ^:private styles
  "
  :host {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 2147483647;
  }

  .panel {
  background: var(--x-color-bg, #fff);
  color: var(--x-color-text, #111);
  border: 1px solid var(--x-color-border, #ccc);
  border-radius: 0.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  padding: 0.5rem;
  min-width: 260px;
  }

  .handle {
  cursor: move;
  user-select: none;
  font-weight: 600;
  padding-bottom: 0.35rem;
  }

  @media (prefers-color-scheme: dark) {
  .panel {
  background: var(--x-color-bg, #1e1e1e);
  color: var(--x-color-text, #e6e6e6);
  border-color: var(--x-color-border, #444);
  }
  }
  ")


(def ^:private tag "barereplay-dock")

(defn mount!
  "Mounts the replay dock"
  []
  (when-not (.querySelector js/document tag)
    (let [add! (fn [] (.appendChild (.-body js/document)
                                    (.createElement js/document tag)))]
      (if (.-body js/document)
        (add!)
        (.addEventListener js/document "DOMContentLoaded" add! #js {:once true})))))

(defn unmount!
  "unmounts the replay dock"
  []
  (when-let [^js el (.querySelector js/document tag)]
    (.remove el)))

(defn- project! [n]
  (when-let [^js sr (.querySelector js/document "server-resource")]
    (.projectResource sr (reconstruct/resource-at (store/entries) n))))

(defn- disconnected! [^js _el]
  nil)

(defn- wire-drag! [^js el ^js handle]
  (.addEventListener handle "pointermove"
                     (fn [^js e]
                       (when-some [dx (du/getv el "__dx")]
                         (set! (.. el -style -left)  (str (- (.-clientX e) dx) "px"))
                         (set! (.. el -style -top)   (str (- (.-clientY e) (du/getv el "__dy")) "px"))
                         (set! (.. el -style -right) "auto"))))       ;; release the initial right-pin
  (.addEventListener handle "pointerup"
                     (fn [^js _] (du/setv-untraced! el "__dx" nil)))
  (.addEventListener handle "pointerdown"
                     (fn [^js e]
                       (let [r (.getBoundingClientRect el)]
                         (du/setv-untraced! el "__dx" (- (.-clientX e) (.-left r)))
                         (du/setv-untraced! el "__dy" (- (.-clientY e) (.-top r)))
                         (.setPointerCapture handle (.-pointerId e))))))

(defn- build-slider [root]
  (let [slider       (.querySelector root "x-slider")
        count-el     (.querySelector root ".count")
        refresh-max! (fn [] (du/set-attr! slider "max" (str (count (store/entries)))))
        set-max!     (fn []                                   ;; initial only
                       (refresh-max!)
                       (du/set-attr! slider "value" (str (count (store/entries))))
                       (set! (.-textContent count-el)
                         (label/readout (store/entries) (count (store/entries)))))]
    (.addEventListener slider "x-slider-input"
                       (fn [^js e]
                         (let [n (js/Math.round (.. e -detail -value))]
                           (project! n)
                           (set! (.-textContent count-el) (label/readout (store/entries) n)))))
    (.addEventListener slider "pointerdown" (fn [_] (refresh-max!)))))

(defn- build-handle [root el]
  (let [handle (.querySelector root ".handle")]
    (wire-drag! el handle)))

(defn- connected! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})]
    (set! (.-innerHTML root)
      (str "<style>" styles "</style>"
           "<div class='panel'>"
           "  <div class='handle'>BareReplay</div>"
           "  <x-slider min='0' step='1' show-value label='Replay'></x-slider>"
           "  <x-typography variant='caption' class='count'></x-typography>"
           "</div>"))
    (build-slider root)
    (build-handle root el)))


(def ^:private element-opts
  "Declarative class options passed to component/register!. `:internal? true`
  keeps the dev-tool lifecycle hook from firing on the dock's own
  connect/disconnect — otherwise those records would pollute every trace
  the dock records. There are no observed attributes; the no-op
  attribute-changed-fn is a formality (the callback can never fire)."
  {:internal?            true
   :observed-attributes  #js []
   :connected-fn         connected!
   :disconnected-fn      disconnected!
   :attribute-changed-fn (fn [_ _ _ _] nil)})

(defn register! []
  (comp/register! "barereplay-dock"
                  element-opts))
