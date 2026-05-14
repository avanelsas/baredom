(ns baredom.components.x-welcome-tour-step.x-welcome-tour-step
  "Passive child element for x-welcome-tour.
   Holds step configuration as attributes and provides a default slot
   for rich content. The parent orchestrator reads these attributes
   and renders the tour UI — this element never renders visibly itself."
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-welcome-tour-step.model :as model]))

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
    (du/setv! el k-refs {:root root :slot slot})))

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
    :scroll-to-raw      (when (du/has-attr? el model/attr-scroll-to)
                           (du/get-attr el model/attr-scroll-to))}))

(defn- update-model! [^js el]
  (let [new-m (read-model el)]
    (du/setv! el k-model new-m)))

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

  (du/define-string-prop! proto "placement" model/attr-placement "bottom")
  (du/define-string-prop! proto "connector" model/attr-connector)

  (du/define-parsed-prop! proto "cutoutPadding" model/attr-cutout-padding
                          model/parse-cutout-padding)
  (du/define-parsed-prop! proto "cutoutRadius"  model/attr-cutout-radius
                          model/parse-cutout-radius)

  (.defineProperty js/Object proto "scrollTo"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-scroll-to
                                         (when (du/has-attr? this model/attr-scroll-to)
                                           (du/get-attr this model/attr-scroll-to)))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-scroll-to "")
                                          (.setAttribute this model/attr-scroll-to "false"))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (update-model! el)
  (du/dispatch! el model/event-connected #js {}))

(defn- disconnected! [^js _el]
  (du/dispatch-document! model/event-disconnected))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-model! el)))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
