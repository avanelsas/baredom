(ns baredom.components.x-file-download.x-file-download
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-file-download.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (always use gobj/get, gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xFileDownloadRefs")
(def ^:private k-model    "__xFileDownloadModel")
(def ^:private k-handlers "__xFileDownloadHandlers")
;; Untraced: an opaque in-flight AbortController. data-busy and the outcome events trace the save.
(def ^:private k-abort    "__xFileDownloadAbort")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part          "part")
(def ^:private attr-download      "download")
(def ^:private attr-aria-label    "aria-label")
(def ^:private attr-aria-disabled "aria-disabled")
(def ^:private attr-aria-busy     "aria-busy")
(def ^:private attr-data-disabled "data-disabled")
(def ^:private attr-data-busy     "data-busy")

(def ^:private part-anchor  "anchor")
(def ^:private part-icon    "icon")
(def ^:private part-content "content")

(def ^:private val-true "true")

(def ^:private rk-anchor  "anchor-el")
(def ^:private rk-icon    "icon-el")
(def ^:private rk-content "content-el")

(def ^:private ev-click "click")
(def ^:private hk-click "click")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "--x-file-download-bg:var(--x-color-primary,#2563eb);"
   "--x-file-download-color:var(--x-color-surface,#ffffff);"
   "--x-file-download-hover-bg:var(--x-color-primary-hover,#1d4ed8);"
   "--x-file-download-active-bg:var(--x-color-primary-active,#1e40af);"
   "--x-file-download-border-radius:var(--x-radius-md,6px);"
   "--x-file-download-padding:0.5rem 1rem;"
   "--x-file-download-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-file-download-font-weight:500;"
   "--x-file-download-gap:0.375rem;"
   "--x-file-download-icon-size:1em;"
   "--x-file-download-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-file-download-disabled-opacity:0.45;"
   "--x-file-download-transition:background var(--x-transition-duration,120ms) ease;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-file-download-bg:var(--x-color-primary,#3b82f6);"
   "--x-file-download-hover-bg:var(--x-color-primary-hover,#2563eb);"
   "--x-file-download-active-bg:var(--x-color-primary-active,#1d4ed8);"
   "--x-file-download-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "[part=anchor]{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-file-download-gap);"
   "padding:var(--x-file-download-padding);"
   "background:var(--x-file-download-bg);"
   "color:var(--x-file-download-color);"
   "border-radius:var(--x-file-download-border-radius);"
   "font-size:var(--x-file-download-font-size);"
   "font-weight:var(--x-file-download-font-weight);"
   "text-decoration:none;"
   "cursor:pointer;"
   "box-sizing:border-box;"
   "transition:var(--x-file-download-transition);"
   "outline:none;"
   "user-select:none;"
   "-webkit-user-select:none;"
   "}"
   "[part=anchor]:hover{"
   "background:var(--x-file-download-hover-bg);"
   "}"
   "[part=anchor]:active{"
   "background:var(--x-file-download-active-bg);"
   "}"
   "[part=anchor]:focus-visible{"
   "outline:2px solid var(--x-file-download-focus-ring);"
   "outline-offset:2px;"
   "}"
   "[part=icon]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-file-download-icon-size);"
   "height:var(--x-file-download-icon-size);"
   "flex-shrink:0;"
   "}"
   "[part=content]{"
   "display:inline;"
   "}"
   ":host([data-disabled]) [part=anchor]{"
   "opacity:var(--x-file-download-disabled-opacity);"
   "pointer-events:none;"
   "cursor:default;"
   "}"
   ":host([data-busy]) [part=anchor]{"
   "cursor:progress;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=anchor]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Download icon SVG
;; ---------------------------------------------------------------------------
(def ^:private download-icon-svg
  "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 16 16\" fill=\"currentColor\" width=\"1em\" height=\"1em\" aria-hidden=\"true\"><path d=\"M8 1a.75.75 0 0 1 .75.75v6.19l1.97-1.97a.75.75 0 1 1 1.06 1.06l-3.25 3.25a.75.75 0 0 1-1.06 0L4.22 7.03a.75.75 0 0 1 1.06-1.06L7.25 7.94V1.75A.75.75 0 0 1 8 1ZM2.5 13.25a.75.75 0 0 1 .75-.75h9.5a.75.75 0 0 1 0 1.5h-9.5a.75.75 0 0 1-.75-.75Z\"/></svg>")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        anchor-el  (.createElement js/document "a")
        icon-el    (.createElement js/document "span")
        content-el (.createElement js/document "span")
        slot-el    (.createElement js/document "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! anchor-el  attr-part part-anchor)
    (du/set-attr! icon-el    attr-part part-icon)
    (du/set-attr! content-el attr-part part-content)

    (set! (.-innerHTML icon-el) download-icon-svg)

    (.appendChild content-el slot-el)
    (.appendChild anchor-el icon-el)
    (.appendChild anchor-el content-el)
    (.appendChild root style-el)
    (.appendChild root anchor-el)

    (let [refs #js {}]
      (gobj/set refs rk-anchor  anchor-el)
      (gobj/set refs rk-icon    icon-el)
      (gobj/set refs rk-content content-el)
      (du/setv! el k-refs refs)
      refs)))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (make-shadow! el)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:href-raw          (du/get-attr el model/attr-href)
    :filename-raw      (du/get-attr el model/attr-filename)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :aria-label-raw    (du/get-attr el model/attr-aria-label)
    :picker-present?   (du/has-attr? el model/attr-picker)}))

;; ---------------------------------------------------------------------------
;; DOM patching (render-orchestrator: phase list of named helpers)
;; ---------------------------------------------------------------------------
(defn- apply-href! [^js anchor-el {:keys [href]}]
  (set! (.-href anchor-el) href))

(defn- apply-download-attr! [^js anchor-el m]
  (if-some [download (model/download-value m)]
    (du/set-attr!    anchor-el attr-download download)
    (du/remove-attr! anchor-el attr-download)))

(defn- apply-anchor-aria! [^js anchor-el {:keys [disabled? aria-label]}]
  (if disabled?
    (du/set-attr!    anchor-el attr-aria-disabled val-true)
    (du/remove-attr! anchor-el attr-aria-disabled))
  (if aria-label
    (du/set-attr!    anchor-el attr-aria-label aria-label)
    (du/remove-attr! anchor-el attr-aria-label)))

(defn- apply-host-data! [^js el {:keys [disabled?]}]
  (du/set-bool-attr! el attr-data-disabled disabled?))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js anchor-el (gobj/get refs rk-anchor)]
      (apply-href!          anchor-el m)
      (apply-download-attr! anchor-el m)
      (apply-anchor-aria!   anchor-el m)
      (apply-host-data!     el        m)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ---------------------------------------------------------------------------
;; Save through the native save dialog
;; ---------------------------------------------------------------------------
(defn- saving? [^js el]
  (some? (du/getv el k-abort)))

(defn- apply-busy! [^js el busy?]
  (let [^js anchor-el (gobj/get (du/getv el k-refs) rk-anchor)]
    (du/set-bool-attr! el attr-data-busy busy?)
    (if busy?
      (du/set-attr!    anchor-el attr-aria-busy val-true)
      (du/remove-attr! anchor-el attr-aria-busy))))

(defn- set-saving! [^js el controller]
  (du/setv-untraced! el k-abort controller)
  (apply-busy! el (some? controller)))

(defn- top-reachable? []
  (try
    (some? (.. js/window -top -location -href))
    (catch :default _ false)))

(defn- picker-usable? [{:keys [picker? href]}]
  (and picker?
       (not= href "")
       (fn? (.-showSaveFilePicker js/window))
       (top-reachable?)))

(defn- start-fallback-download! [m]
  (.click (doto (.createElement js/document "a")
            (apply-href! m)
            (apply-download-attr! m))))

(defn- failure-in [phase]
  (fn tag-failure [err]
    (js/Promise.reject
     (if (map? err)
       err
       {:phase phase :error (model/error-name err)}))))

(defn- empty-body []
  (.-body (js/Response. "")))

(defn- open-picker! [m]
  (js/Promise.
   (fn call-picker [resolve]
     (resolve (.showSaveFilePicker js/window (clj->js (model/picker-options m)))))))

(defn- fetch-ok! [^js signal href]
  (.then (js/fetch href #js {:signal signal})
         (fn response-received [^js response]
           (if (.-ok response)
             response
             (js/Promise.reject {:phase model/phase-fetch
                                 :error (model/http-error (.-status response))})))))

(defn- write-response! [^js signal ^js handle ^js response]
  (.then (.createWritable handle)
         (fn writable-created [^js writable]
           (let [^js body (or (.-body response) (empty-body))]
             (.pipeTo body writable #js {:signal signal})))))

(defn- save-to-handle! [^js signal href ^js handle]
  (-> (fetch-ok! signal href)
      (.catch (failure-in model/phase-fetch))
      (.then (fn fetched [^js response]
               (.catch (write-response! signal handle response)
                       (failure-in model/phase-write))))
      (.then (fn written [_]
               (.-name handle)))))

(defn- on-saved! [^js el filename]
  (set-saving! el nil)
  (du/dispatch! el model/event-success (model/success-detail filename)))

(defn- on-failed! [^js el m {:keys [phase error] :as failure}]
  (set-saving! el nil)
  (case (model/failure-outcome failure)
    :cancel   (du/dispatch! el model/event-cancel (model/cancel-detail))
    :fallback (start-fallback-download! m)
    :error    (du/dispatch! el model/event-error (model/error-detail error phase))))

(defn- start-save! [^js el m]
  (let [^js controller (js/AbortController.)
        signal         (.-signal controller)]
    (set-saving! el controller)
    (-> (open-picker! m)
        (.catch (failure-in model/phase-pick))
        (.then (fn picked [^js handle]
                 (save-to-handle! signal (:href m) handle)))
        (.then (fn saved [filename]
                 (on-saved! el filename))
               (fn failed [failure]
                 (on-failed! el m failure))))))

(defn- abort-save! [^js el]
  (when-some [^js controller (du/getv el k-abort)]
    (.abort controller)))

;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- on-anchor-click [^js el ^js evt]
  (let [m (du/getv el k-model)]
    (cond
      (or (:disabled? m) (saving? el))
      (.preventDefault evt)

      (not (du/dispatch-cancelable! el model/event-click (model/click-detail m)))
      (.preventDefault evt)

      (picker-usable? m)
      (do (.preventDefault evt)
          (start-save! el m)))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (let [refs       (du/getv el k-refs)
        ^js anchor (gobj/get refs rk-anchor)
        click-h    (fn handle-anchor-click [evt] (on-anchor-click el evt))
        handlers   #js {}]
    (.addEventListener anchor ev-click click-h)
    (gobj/set handlers hk-click click-h)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js anchor (gobj/get refs rk-anchor)
            click-h    (gobj/get hs hk-click)]
        (when click-h (.removeEventListener anchor ev-click click-h)))))
  (du/setv! el k-handlers nil))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (abort-save! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
