(ns baredom.components.x-i18n-provider.x-i18n-provider
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-i18n-provider.model :as model]
            [baredom.components.x-i18n.model :as i18n-model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs         "__xI18nProviderRefs")
(def ^:private k-model        "__xI18nProviderModel")
(def ^:private k-translations "__xI18nProviderTranslations")
(def ^:private k-abort        "__xI18nProviderAbort")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private tk-current      "current")
(def ^:private tk-fallback     "fallback")
(def ^:private err-abort       "AbortError")
(def ^:private sel-all         "*")
(def ^:private data-i18n-prefix     "data-i18n-")
(def ^:private data-i18n-prefix-len (count data-i18n-prefix))
(def ^:private data-i18n-params     "data-i18n-params")

;; ── Public field key (accessed by x-i18n for translation lookup) ────────────
(defn get-translations
  "Returns the translations object stored on the provider element.
   Used by x-i18n children to resolve translation keys."
  [^js el]
  (du/getv el k-translations))

(defn set-translations!
  "Set translations on the provider element.
   Used by tests and for programmatic locale injection."
  [^js el ^js translations]
  (du/setv! el k-translations translations))

;; ── CSS ─────────────────────────────────────────────────────────────────────
(def ^:private styles ":host{display:contents}")

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) styles)
    (.appendChild root style)
    (.appendChild root slot)
    (du/setv! el k-refs #js {:style style :slot slot})))

;; ── Read model from attributes ──────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   (du/get-attr el model/attr-src)
   (du/get-attr el model/attr-locale)
   (du/get-attr el model/attr-fallback-locale)))

;; ── Abort in-flight fetch ───────────────────────────────────────────────────
(defn- abort-fetch! [^js el]
  (when-let [^js ctrl (du/getv el k-abort)]
    (.abort ctrl)
    (du/setv! el k-abort nil)))

;; ── Fetch single JSON URL ───────────────────────────────────────────────────
(defn- fetch-json [url ^js signal]
  (-> (js/fetch url #js {:signal signal})
      (.then (fn handle-fetch-response [^js resp]
               (if (.-ok resp)
                 (.json resp)
                 (throw (js/Error. (str "HTTP " (.-status resp)))))))
      (.then (fn pass-through-data [^js data] data))))

;; ── Subtree scanning (data-i18n-* attribute translation) ────────────────────

(defn- translate-element! [^js child ^js current ^js fallback]
  (let [^js attrs  (.-attributes child)
        params-raw (du/get-attr child data-i18n-params)
        params     (when (some? params-raw)
                     (i18n-model/parse-params params-raw))
        len        (.-length attrs)]
    ;; Iterate backwards — setting attributes may mutate the NamedNodeMap
    (loop [j (dec len)]
      (when (>= j 0)
        (let [^js a  (aget attrs j)
              a-name (.-name a)]
          (when (and (.startsWith a-name data-i18n-prefix)
                     (not= a-name data-i18n-params))
            (let [target-attr (.substring a-name data-i18n-prefix-len)
                  key-str     (.-value a)
                  raw         (or (i18n-model/resolve-key current key-str)
                                  (i18n-model/resolve-key fallback key-str))
                  resolved    (if (string? raw)
                                (if params
                                  (i18n-model/interpolate raw params)
                                  raw)
                                key-str)]
              (du/set-attr! child target-attr resolved))))
        (recur (dec j))))))

(defn translate-subtree!
  "Scan all descendants for data-i18n-{attr} attributes and set the
   corresponding real attribute from the current translations.
   Skips elements owned by nested x-i18n-provider elements."
  [^js el]
  (let [^js t (du/getv el k-translations)]
    (when t
      (let [^js current  (gobj/get t tk-current)
            ^js fallback (gobj/get t tk-fallback)
            ^js all      (.querySelectorAll el sel-all)]
        (dotimes [i (.-length all)]
          (let [^js child (aget all i)]
            ;; Only translate elements owned by this provider, not nested ones
            (when (identical? (.closest child model/tag-name) el)
              (translate-element! child current fallback))))))))

;; ── Fetch helpers (decomposed from fetch-translations!) ────────────────────
(defn- build-fetch-promises [^js signal src locale fallback-locale]
  (let [url    (model/resolve-url src locale)
        main-p (fetch-json url signal)
        fb-url (when (and (not= fallback-locale "")
                          (not= fallback-locale locale))
                 (model/resolve-url src fallback-locale))
        fb-p   (if fb-url
                 (-> (fetch-json fb-url signal)
                     (.catch (fn swallow-fb-error [_] nil)))
                 (js/Promise.resolve nil))]
    #js [main-p fb-p]))

(defn- dispatch-translations-ready! [^js el locale ^js results]
  (let [^js main-data (aget results 0)
        ^js fb-data   (aget results 1)]
    (du/setv! el k-translations
              #js {:current  main-data
                   :fallback fb-data})
    (du/setv! el k-abort nil)
    (du/dispatch! el model/event-change #js {:locale locale})
    (translate-subtree! el)))

(defn- handle-fetch-error! [^js el locale ^js err]
  (when-not (= err-abort (.-name err))
    (du/setv! el k-abort nil)
    (du/dispatch! el model/event-error
                  #js {:locale  locale
                       :message (.-message err)})))

(defn- fetch-translations! [^js el m]
  (abort-fetch! el)
  (let [{:keys [src locale fallback-locale]} m
        url (model/resolve-url src locale)]
    (when url
      (let [^js ctrl   (js/AbortController.)
            ^js signal (.-signal ctrl)
            promises   (build-fetch-promises signal src locale fallback-locale)]
        (du/setv! el k-abort ctrl)
        (du/dispatch! el model/event-loading #js {:locale locale})
        (-> (js/Promise.all promises)
            (.then  (fn on-translations-ready [^js results]
                      (dispatch-translations-ready! el locale results)))
            (.catch (fn on-fetch-error [^js err]
                      (handle-fetch-error! el locale err))))))))

;; ── Apply model + render-pipeline ──────────────────────────────────────────
(defn- apply-model! [^js el new-m old-m]
  (let [needs-fetch? (or (nil? old-m) (model/needs-fetch? old-m new-m))]
    (when needs-fetch?
      (fetch-translations! el new-m))
    (du/setv! el k-model new-m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m old-m))))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (init-dom! el))
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (abort-fetch! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-refs)
      (update-from-attrs! el))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; Read-only translations property
  (js/Object.defineProperty
   proto "translations"
   #js {:get (fn xip-get-translations []
               (this-as ^js this
                 (when-let [^js t (du/getv this k-translations)]
                   (gobj/get t tk-current))))
        :enumerable   true
        :configurable true})
  ;; translateSubtree() — re-scan subtree for data-i18n-* attributes
  (.defineProperty js/Object proto "translateSubtree"
                   #js {:value (fn xip-translate-subtree []
                                 (this-as ^js this
                                   (translate-subtree! this)))
                        :writable true :configurable true}))

;; ── Registration ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
