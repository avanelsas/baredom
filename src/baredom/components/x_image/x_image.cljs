(ns baredom.components.x-image.x-image
  (:require
[baredom.utils.component :as component]
               [baredom.components.x-image.model :as model]
   [baredom.utils.dom :as du]))

;; ── Instance-field keys (gobj/get, gobj/set via du) ──────────────────────────
(def ^:private k-refs        "__xImageRefs")
(def ^:private k-model       "__xImageModel")
(def ^:private k-state       "__xImageState")
(def ^:private k-handlers    "__xImageHandlers")
(def ^:private k-current-src "__xImageCurrentSrc")
(def ^:private k-warned-alt  "__xImageWarnedAlt")
(def ^:private k-warned-rat  "__xImageWarnedRatio")

(def ^:private data-state-attr "data-state")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "max-width:100%;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-image-radius:var(--x-radius-md,8px);"
   "--x-image-bg:var(--x-color-surface,#f3f4f6);"
   "--x-image-border:0;"
   "--x-image-shimmer-color:var(--x-color-border,rgba(0,0,0,0.08));"
   "--x-image-shimmer-highlight:rgba(255,255,255,0.65);"
   "--x-image-shimmer-duration:1.5s;"
   "--x-image-fade-duration:var(--x-transition-duration,200ms);"
   "--x-image-fade-easing:var(--x-transition-easing,ease);"
   "--x-image-text:var(--x-color-text-muted,rgba(0,0,0,0.55));"
   "--x-image-error-gap:var(--x-space-xs,6px);"
   "--x-image-font-family:var(--x-font-family,system-ui,sans-serif);"
   "--x-image-font-size:var(--x-font-size-sm,0.875rem);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-image-bg:var(--x-color-surface,#1f2937);"
   "--x-image-shimmer-color:var(--x-color-border,rgba(255,255,255,0.08));"
   "--x-image-shimmer-highlight:rgba(255,255,255,0.10);"
   "--x-image-text:var(--x-color-text-muted,rgba(255,255,255,0.65));}}"

   "[part=frame]{"
   "position:relative;"
   "width:100%;"
   "height:100%;"
   "min-height:1em;"
   "background:var(--x-image-bg);"
   "border-radius:var(--x-image-radius);"
   "border:var(--x-image-border);"
   "overflow:hidden;}"

   "[part=image]{"
   "display:block;"
   "width:100%;"
   "height:100%;"
   "opacity:0;"
   "transition:opacity var(--x-image-fade-duration) var(--x-image-fade-easing);}"

   ":host([data-state=loaded]) [part=image]{opacity:1;}"

   "[part=shimmer]{"
   "position:absolute;"
   "inset:0;"
   "background-color:var(--x-image-shimmer-color);"
   "background-image:linear-gradient(90deg,transparent 0%,var(--x-image-shimmer-highlight) 50%,transparent 100%);"
   "background-size:200% 100%;"
   "background-repeat:no-repeat;"
   "background-position:-100% 0;"
   "animation:x-image-shimmer var(--x-image-shimmer-duration) linear infinite;}"

   "@keyframes x-image-shimmer{"
   "0%{background-position:-100% 0;}"
   "100%{background-position:100% 0;}}"

   ":host([data-state=loaded]) [part=shimmer],"
   ":host([data-state=error]) [part=shimmer]{display:none;}"

   "[part=error]{"
   "position:absolute;"
   "inset:0;"
   "display:none;"
   "flex-direction:column;"
   "align-items:center;"
   "justify-content:center;"
   "gap:var(--x-image-error-gap);"
   "padding:8px;"
   "text-align:center;"
   "color:var(--x-image-text);"
   "font-family:var(--x-image-font-family);"
   "font-size:var(--x-image-font-size);}"

   ":host([data-state=error]) [part=error]{display:flex;}"
   ":host([data-state=error]) [part=image]{visibility:hidden;}"

   "[part=error-glyph]{font-size:1.5em;line-height:1;}"
   "[part=error-text]{line-height:1.25;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=shimmer]{animation:none;}"
   "[part=image]{transition:none;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style        (.createElement js/document "style")
        frame        (.createElement js/document "div")
        shimmer      (.createElement js/document "div")
        img          (.createElement js/document "img")
        error-box    (.createElement js/document "div")
        error-slot   (.createElement js/document "slot")
        error-default (.createElement js/document "div")
        error-glyph  (.createElement js/document "span")
        error-text   (.createElement js/document "span")]

    (set! (.-textContent style) style-text)

    (.setAttribute frame "part" "frame")

    (.setAttribute shimmer "part" "shimmer")
    (.setAttribute shimmer "aria-hidden" "true")

    (.setAttribute img "part" "image")
    (set! (.-alt img) "")
    (set! (.-decoding img) "async")
    (set! (.-loading img) model/default-loading)

    (.setAttribute error-box "part" "error")
    (.setAttribute error-box "role" "img")
    (.setAttribute error-box "aria-label" "Image failed to load")

    (.setAttribute error-slot "name" "error")

    (.setAttribute error-default "part" "error-default")
    (.setAttribute error-glyph "part" "error-glyph")
    (.setAttribute error-glyph "aria-hidden" "true")
    (set! (.-textContent error-glyph) "⚠")
    (.setAttribute error-text "part" "error-text")
    (set! (.-textContent error-text) "Image unavailable")

    (.appendChild error-default error-glyph)
    (.appendChild error-default error-text)
    (.appendChild error-slot error-default)
    (.appendChild error-box error-slot)

    (.appendChild frame shimmer)
    (.appendChild frame img)
    (.appendChild frame error-box)

    (.appendChild root style)
    (.appendChild root frame)

    (du/setv! el k-refs
              {:root       root
               :frame      frame
               :shimmer    shimmer
               :img        img
               :error-box  error-box
               :error-slot error-slot}))
  nil)

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Host state helpers ───────────────────────────────────────────────────────
(defn- set-host-state! [^js el state-str]
  (du/setv! el k-state state-str)
  (.setAttribute el data-state-attr state-str)
  nil)

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:src-raw             (du/get-attr el model/attr-src)
    :alt-raw             (du/get-attr el model/attr-alt)
    :alt-present?        (du/has-attr? el model/attr-alt)
    :decorative-present? (du/has-attr? el model/attr-decorative)
    :ratio-raw           (du/get-attr el model/attr-ratio)
    :fit-raw             (du/get-attr el model/attr-fit)
    :position-raw        (du/get-attr el model/attr-position)
    :loading-raw         (du/get-attr el model/attr-loading)}))

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-load! [^js el src natural-w natural-h]
  (let [detail (clj->js (model/load-detail src natural-w natural-h))
        ^js ev (js/CustomEvent.
                model/event-load
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable false})]
    (.dispatchEvent el ev))
  nil)

(defn- dispatch-error! [^js el src]
  (let [detail (clj->js (model/error-detail src))
        ^js ev (js/CustomEvent.
                model/event-error
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable false})]
    (.dispatchEvent el ev))
  nil)

;; ── Image load / error handling ──────────────────────────────────────────────
(defn- on-img-load [^js el]
  (let [{:keys [img]} (du/getv el k-refs)
        ^js img img
        expected (du/getv el k-current-src)]
    (when (and img (string? expected) (not= "" expected))
      ;; Only act if this load corresponds to the src we most recently set.
      ;; Compare against the attribute we set (not the resolved IDL), since
      ;; relative URLs resolve differently.
      (let [attr-src (.getAttribute img "src")]
        (when (= attr-src expected)
          (set-host-state! el model/state-loaded)
          (dispatch-load! el expected (.-naturalWidth img) (.-naturalHeight img))))))
  nil)

(defn- on-img-error [^js el]
  (let [{:keys [img]} (du/getv el k-refs)
        ^js img img
        expected (du/getv el k-current-src)]
    (when (and img (string? expected) (not= "" expected))
      (let [attr-src (.getAttribute img "src")]
        (when (= attr-src expected)
          (set-host-state! el model/state-error)
          (dispatch-error! el expected)))))
  nil)

(defn- set-img-src! [^js el new-src]
  (let [{:keys [img]} (du/getv el k-refs)
        ^js img img
        prev    (du/getv el k-current-src)]
    (when (not= prev new-src)
      (du/setv! el k-current-src new-src)
      (if (and (string? new-src) (not= "" new-src))
        (do
          ;; Reset to loading state BEFORE assignment so sync load events
          ;; (data URLs) see the correct pending state.
          (set-host-state! el model/state-loading)
          (.setAttribute img "src" new-src))
        (do
          (.removeAttribute img "src")
          (set-host-state! el model/state-loading)))))
  nil)

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- warn-once! [^js el key msg]
  (when-not (du/getv el key)
    (du/setv! el key true)
    (js/console.warn msg))
  nil)

(defn- apply-model! [^js el m]
  (let [{:keys [frame img error-box]} (ensure-refs! el)
        ^js frame     frame
        ^js img       img
        ^js error-box error-box
        prev-model    (du/getv el k-model)
        prev-src      (when prev-model (:src prev-model))
        src-changed?  (or (nil? prev-model) (not= prev-src (:src m)))]

    ;; Aspect ratio
    (if (:ratio-css m)
      (set! (.. frame -style -aspectRatio) (:ratio-css m))
      (set! (.. frame -style -aspectRatio) ""))
    (when-not (:ratio-valid? m)
      (warn-once! el k-warned-rat
                  "[x-image] Invalid ratio attribute; expected \"W:H\" (e.g. \"16:9\")."))

    ;; Image styling
    (set! (.. img -style -objectFit) (:fit m))
    (set! (.. img -style -objectPosition) (:position m))
    (set! (.-loading img) (:loading m))
    (set! (.-decoding img) "async")

    ;; Accessibility
    (if (:decorative? m)
      (do
        (set! (.-alt img) "")
        (.setAttribute el "aria-hidden" "true")
        (.removeAttribute el "role"))
      (do
        (set! (.-alt img) (:alt m))
        (.removeAttribute el "aria-hidden")
        (.setAttribute el "role" "img")
        (when (:warn-alt? m)
          (warn-once! el k-warned-alt
                      "[x-image] Missing alt attribute. Set alt=\"…\" or add the `decorative` attribute."))))

    (.setAttribute error-box "aria-label"
                   (if (and (not (:decorative? m)) (not= "" (:alt m)))
                     (:alt m)
                     "Image failed to load"))

    ;; Src (reload if changed)
    (when src-changed?
      (set-img-src! el (:src m)))

    (du/setv! el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [img]} (ensure-refs! el)
        ^js img img
        load-h  (fn [_e] (on-img-load el))
        err-h   (fn [_e] (on-img-error el))]
    (.addEventListener img "load" load-h)
    (.addEventListener img "error" err-h)
    (du/setv! el k-handlers {:load load-h :error err-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js img (:img refs)
            load-h  (:load hs)
            err-h   (:error hs)]
        (when load-h (.removeEventListener img "load" load-h))
        (when err-h  (.removeEventListener img "error" err-h)))))
  (du/setv! el k-handlers nil)
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- define-string-attr-prop! [^js proto prop-name attr-name]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr-name) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (or (nil? v) (= v ""))
                                          (.removeAttribute this attr-name)
                                          (.setAttribute this attr-name (str v)))))
                        :enumerable  true
                        :configurable true}))

(defn- install-property-accessors! [^js proto]
  (define-string-attr-prop! proto "src"      model/attr-src)
  (define-string-attr-prop! proto "alt"      model/attr-alt)
  (define-string-attr-prop! proto "ratio"    model/attr-ratio)
  (define-string-attr-prop! proto "fit"      model/attr-fit)
  (define-string-attr-prop! proto "position" model/attr-position)
  (define-string-attr-prop! proto "loading"  model/attr-loading)

  (.defineProperty js/Object proto "decorative"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-decorative)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-decorative "")
                                          (.removeAttribute this model/attr-decorative))))
                        :enumerable  true
                        :configurable true})

  (.defineProperty js/Object proto "naturalWidth"
                   #js {:get (fn []
                               (this-as ^js this
                                        (if-let [refs (du/getv this k-refs)]
                                          (.-naturalWidth ^js (:img refs))
                                          0)))
                        :enumerable  true
                        :configurable true})

  (.defineProperty js/Object proto "naturalHeight"
                   #js {:get (fn []
                               (this-as ^js this
                                        (if-let [refs (du/getv this k-refs)]
                                          (.-naturalHeight ^js (:img refs))
                                          0)))
                        :enumerable  true
                        :configurable true})

  (.defineProperty js/Object proto "state"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (du/getv this k-state) model/state-loading)))
                        :enumerable  true
                        :configurable true}))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (when (nil? (du/getv el k-state))
  (set-host-state! el model/state-loading))
  (update-from-attrs! el)
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
  (update-from-attrs! el))
  nil)

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
