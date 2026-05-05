(ns baredom.components.x-i18n.x-i18n
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-i18n.model :as model]
            [baredom.components.x-i18n-provider.x-i18n-provider :as provider]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs     "__xI18nRefs")
(def ^:private k-model    "__xI18nModel")
(def ^:private k-handlers "__xI18nHandlers")
(def ^:private k-provider "__xI18nProvider")

;; ── CSS ─────────────────────────────────────────────────────────────────────
(def ^:private styles
  (str ":host{display:inline}"
       "[part=text]{font:var(--x-i18n-font,inherit);"
       "color:var(--x-i18n-color,inherit)}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        span  (.createElement js/document "span")]
    (set! (.-textContent style) styles)
    (.setAttribute span "part" "text")
    (.appendChild root style)
    (.appendChild root span)
    (du/setv! el k-refs #js {:span span})))

;; ── Read model from attributes ──────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   (du/get-attr el model/attr-key)
   (du/get-attr el model/attr-params)))

;; ── Apply model (write to DOM) ──────────────────────────────────────────────
(defn- apply-model! [^js el m]
  (let [^js refs     (du/getv el k-refs)
        ^js prov     (du/getv el k-provider)
        ^js t        (when prov (provider/get-translations prov))
        ^js current  (when t (gobj/get t "current"))
        ^js fallback (when t (gobj/get t "fallback"))
        text         (model/resolve-translation current fallback
                                                 (:key m) (:params m))]
    (when refs
      (set! (.-textContent (gobj/get refs "span")) text))))

;; ── Render (guarded) ────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (du/setv! el k-model new-m)
      (apply-model! el new-m))))

;; ── Provider change handler (unguarded — translations changed) ──────────────
(defn- on-provider-change! [^js el]
  (let [m (or (du/getv el k-model) (read-model el))]
    (apply-model! el m)))

;; ── Provider attachment ─────────────────────────────────────────────────────
(defn- attach-provider! [^js el]
  (let [^js prov (.closest el model/provider-tag-name)]
    (du/setv! el k-provider prov)
    (when prov
      (let [handler (fn [_e] (on-provider-change! el))]
        (du/setv! el k-handlers #js {:change handler})
        (.addEventListener prov model/provider-event-change handler)))))

(defn- detach-provider! [^js el]
  (when-let [^js prov (du/getv el k-provider)]
    (when-let [^js handlers (du/getv el k-handlers)]
      (let [handler (gobj/get handlers "change")]
        (when handler
          (.removeEventListener prov model/provider-event-change handler)))))
  (du/setv! el k-handlers nil)
  (du/setv! el k-provider nil))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (init-dom! el))
  (attach-provider! el)
  (render! el))

(defn- disconnected! [^js el]
  (detach-provider! el))

(defn- attribute-changed! [^js el _name _old-val _new-val]
  (when (du/getv el k-refs)
    (render! el)))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; Read-only value property
  (js/Object.defineProperty
   proto "value"
   #js {:get (fn []
               (this-as ^js this
                 (when-let [^js refs (du/getv this k-refs)]
                   (.-textContent (gobj/get refs "span")))))
        :enumerable   true
        :configurable true}))

;; ── Registration ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
