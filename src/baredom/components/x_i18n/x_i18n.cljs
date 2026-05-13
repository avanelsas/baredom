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

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-part   "part")
(def ^:private part-text   "text")
(def ^:private tag-span    "span")
(def ^:private rk-span     "span")
(def ^:private tk-current  "current")
(def ^:private tk-fallback "fallback")
(def ^:private hk-change   "change")

;; ── CSS ─────────────────────────────────────────────────────────────────────
(def ^:private styles
  (str ":host{display:inline}"
       "[part=text]{font:var(--x-i18n-font,inherit);"
       "color:var(--x-i18n-color,inherit)}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        span  (.createElement js/document tag-span)
        refs  #js {}]
    (set! (.-textContent style) styles)
    (.setAttribute span attr-part part-text)
    (.appendChild root style)
    (.appendChild root span)
    (gobj/set refs rk-span span)
    (du/setv! el k-refs refs)))

;; ── Read model from attributes ──────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   (du/get-attr el model/attr-key)
   (du/get-attr el model/attr-params)))

;; ── Apply model (writes to DOM, caches at tail) ─────────────────────────────
(defn- apply-model! [^js el m]
  (let [^js refs     (du/getv el k-refs)
        ^js prov     (du/getv el k-provider)
        ^js t        (when prov (provider/get-translations prov))
        ^js current  (when t (gobj/get t tk-current))
        ^js fallback (when t (gobj/get t tk-fallback))
        text         (model/resolve-translation current fallback
                                                 (:key m) (:params m))]
    (when refs
      (set! (.-textContent (gobj/get refs rk-span)) text))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
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
      (let [handler (fn handle-provider-change [_e] (on-provider-change! el))
            handlers #js {}]
        (gobj/set handlers hk-change handler)
        (du/setv! el k-handlers handlers)
        (.addEventListener prov model/provider-event-change handler)))))

(defn- detach-provider! [^js el]
  (when-let [^js prov (du/getv el k-provider)]
    (when-let [^js handlers (du/getv el k-handlers)]
      (let [handler (gobj/get handlers hk-change)]
        (when handler
          (.removeEventListener prov model/provider-event-change handler)))))
  (du/setv! el k-handlers nil)
  (du/setv! el k-provider nil))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (init-dom! el))
  (attach-provider! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (detach-provider! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-refs)
      (update-from-attrs! el))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; Read-only value property
  (js/Object.defineProperty
   proto "value"
   #js {:get (fn xi-get-value []
               (this-as ^js this
                 (when-let [^js refs (du/getv this k-refs)]
                   (.-textContent (gobj/get refs rk-span)))))
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
