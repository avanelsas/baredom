(ns baredom.components.x-welcome-tour-step.x-welcome-tour-step
  "Passive child element for x-welcome-tour.
   Holds step configuration as attributes and provides a default slot
   for rich content. The parent orchestrator reads these attributes
   and renders the tour UI — this element never renders visibly itself."
  (:require
   [baredom.components.x-welcome-tour-step.model :as model]
   [baredom.utils.dom :as du]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs  "__xWelcomeTourStepRefs")
(def ^:private k-model "__xWelcomeTourStepModel")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  ":host{display:none;}")

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.appendChild root style)
    (.appendChild root slot)
    (du/setv! el k-refs {:root root :slot slot}))
  nil)

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:target-raw         (du/get-attr el model/attr-target)
    :title-raw          (du/get-attr el model/attr-title)
    :placement-raw      (du/get-attr el model/attr-placement)
    :connector-raw      (du/get-attr el model/attr-connector)
    :cutout-padding-raw (du/get-attr el model/attr-cutout-padding)
    :cutout-radius-raw  (du/get-attr el model/attr-cutout-radius)
    :scroll-to-raw      (if (du/has-attr? el model/attr-scroll-to)
                           (du/get-attr el model/attr-scroll-to)
                           nil)}))

(defn- update-model! [^js el]
  (let [new-m (read-model el)]
    (du/setv! el k-model new-m))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto "target"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-target) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-target (str v))
                                          (.removeAttribute this model/attr-target))))
                        :enumerable true :configurable true})

  ;; Override HTMLElement.title (tooltip) — intentional, this component
  ;; uses "title" for its step heading text
  (.defineProperty js/Object proto "title"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-title) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-title (str v))
                                          (.removeAttribute this model/attr-title))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "placement"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/get-attr this model/attr-placement) "bottom")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-placement (str v))
                                          (.removeAttribute this model/attr-placement))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "connector"
                   #js {:get (fn []
                               (this-as ^js this
                                        (du/get-attr this model/attr-connector)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-connector (str v))
                                          (.removeAttribute this model/attr-connector))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "cutoutPadding"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-cutout-padding
                                         (du/get-attr this model/attr-cutout-padding))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-cutout-padding (str v))
                                          (.removeAttribute this model/attr-cutout-padding))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "cutoutRadius"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-cutout-radius
                                         (du/get-attr this model/attr-cutout-radius))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-cutout-radius (str v))
                                          (.removeAttribute this model/attr-cutout-radius))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "scrollTo"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-scroll-to
                                         (if (du/has-attr? this model/attr-scroll-to)
                                           (du/get-attr this model/attr-scroll-to)
                                           nil))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-scroll-to "")
                                          (.setAttribute this model/attr-scroll-to "false"))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (update-model! this)
                     (.dispatchEvent this
                                    (js/CustomEvent.
                                     model/event-connected
                                     #js {:bubbles    true
                                          :composed   true
                                          :cancelable false}))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js _this
                     (.dispatchEvent js/document
                                    (js/CustomEvent.
                                     model/event-disconnected
                                     #js {:bubbles    false
                                          :composed   false
                                          :cancelable false}))
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-model! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
