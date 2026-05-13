(ns baredom.components.x-avatar.x-avatar
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-avatar.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xAvatarRefs")
(def ^:private k-model    "__xAvatarModel")
(def ^:private k-handlers "__xAvatarHandlers")
(def ^:private k-img-ok   "__xAvatarImgOk")
(def ^:private k-last-src "__xAvatarLastSrc")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part         "part")
(def ^:private attr-alt          "alt")
(def ^:private attr-name         "name")
(def ^:private attr-role         "role")
(def ^:private attr-aria-label   "aria-label")
(def ^:private attr-aria-hidden  "aria-hidden")
(def ^:private attr-aria-disabled "aria-disabled")
(def ^:private attr-data-size    "data-size")
(def ^:private attr-data-shape   "data-shape")
(def ^:private attr-data-variant "data-variant")

(def ^:private part-root     "root")
(def ^:private part-image    "image")
(def ^:private part-initials "initials")
(def ^:private part-fallback "fallback")
(def ^:private part-status   "status")
(def ^:private part-badge    "badge")

(def ^:private role-img         "img")
(def ^:private val-true         "true")
(def ^:private val-fallback     "?")
(def ^:private slot-name-badge  "badge")

(def ^:private css-var-status-color "--x-avatar-status-color")

(def ^:private ev-load       "load")
(def ^:private ev-error      "error")
(def ^:private ev-slotchange "slotchange")

(def ^:private hk-load  "load")
(def ^:private hk-error "error")
(def ^:private hk-slot  "slot")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-avatar-size-xs:20px;"
   "--x-avatar-size-sm:24px;"
   "--x-avatar-size-md:32px;"
   "--x-avatar-size-lg:40px;"
   "--x-avatar-size-xl:48px;"
   "--x-avatar-radius:10px;"
   "--x-avatar-disabled-opacity:0.55;"
   "--x-avatar-bg:var(--x-color-surface, rgba(0,0,0,0.06));"
   "--x-avatar-border:var(--x-color-border, rgba(0,0,0,0.14));"
   "--x-avatar-color:var(--x-color-text, rgba(0,0,0,0.86));"
   "--x-avatar-ring:var(--x-color-bg, #ffffff);"
   "--x-avatar-status-online:rgba(16,140,72,0.95);"
   "--x-avatar-status-offline:rgba(0,0,0,0.45);"
   "--x-avatar-status-busy:rgba(190,20,40,0.95);"
   "--x-avatar-status-away:rgba(204,120,0,0.95);"
   "--x-avatar-status-color:transparent;"
   "--x-avatar-focus-ring:var(--x-color-focus-ring, rgba(59,130,246,0.55));"
   "--x-avatar-font-size:var(--x-font-size-sm, 0.875rem);"
   "--x-avatar-size:var(--x-avatar-size-md);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-avatar-bg:var(--x-color-surface, rgba(255,255,255,0.10));"
   "--x-avatar-border:var(--x-color-border, rgba(255,255,255,0.18));"
   "--x-avatar-color:var(--x-color-text, rgba(255,255,255,0.88));"
   "--x-avatar-ring:var(--x-color-bg, #0b0c0f);"
   "--x-avatar-status-online:rgba(60,210,120,0.95);"
   "--x-avatar-status-offline:rgba(255,255,255,0.45);"
   "--x-avatar-status-busy:rgba(255,90,110,0.95);"
   "--x-avatar-status-away:rgba(255,190,90,0.95);}}"

   ":host([data-size='xs']){--x-avatar-size:var(--x-avatar-size-xs);--x-avatar-font-size:0.625rem;}"
   ":host([data-size='sm']){--x-avatar-size:var(--x-avatar-size-sm);--x-avatar-font-size:0.75rem;}"
   ":host([data-size='md']){--x-avatar-size:var(--x-avatar-size-md);}"
   ":host([data-size='lg']){--x-avatar-size:var(--x-avatar-size-lg);--x-avatar-font-size:1rem;}"
   ":host([data-size='xl']){--x-avatar-size:var(--x-avatar-size-xl);--x-avatar-font-size:1.125rem;}"

   ":host([data-variant='brand']){"
   "--x-avatar-bg:rgba(0,102,204,0.10);"
   "--x-avatar-border:rgba(0,102,204,0.30);"
   "--x-avatar-color:rgba(0,60,120,0.90);}"

   ":host([data-variant='subtle']){"
   "--x-avatar-bg:rgba(0,0,0,0.03);"
   "--x-avatar-border:rgba(0,0,0,0.08);"
   "--x-avatar-color:rgba(0,0,0,0.55);}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-variant='brand']){"
   "--x-avatar-bg:rgba(80,160,255,0.18);"
   "--x-avatar-border:rgba(80,160,255,0.40);"
   "--x-avatar-color:rgba(210,235,255,0.92);}"
   ":host([data-variant='subtle']){"
   "--x-avatar-bg:rgba(255,255,255,0.04);"
   "--x-avatar-border:rgba(255,255,255,0.08);"
   "--x-avatar-color:rgba(255,255,255,0.50);}}"

   "[part=root]{"
   "width:var(--x-avatar-size);"
   "height:var(--x-avatar-size);"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "overflow:hidden;"
   "background:var(--x-avatar-bg);"
   "color:var(--x-avatar-color);"
   "border:1px solid var(--x-avatar-border);"
   "box-shadow:0 0 0 2px var(--x-avatar-ring);"
   "font-size:var(--x-avatar-font-size);"
   "font-weight:600;"
   "line-height:1;"
   "user-select:none;"
   "border-radius:999px;}"

   ":host([data-shape='square']) [part=root]{border-radius:0;}"
   ":host([data-shape='rounded']) [part=root]{border-radius:var(--x-avatar-radius);}"

   ":host([disabled]){opacity:var(--x-avatar-disabled-opacity);}"

   "[part=image]{width:100%;height:100%;object-fit:cover;display:none;}"

   "[part=initials],[part=fallback]{display:none;padding:0 0.25em;}"

   "[part=status]{"
   "position:absolute;"
   "right:-1px;"
   "bottom:-1px;"
   "width:0.34em;"
   "height:0.34em;"
   "min-width:10px;"
   "min-height:10px;"
   "border-radius:999px;"
   "border:2px solid var(--x-avatar-ring);"
   "background:var(--x-avatar-status-color);"
   "display:none;}"

   "[part=badge]{"
   "position:absolute;"
   "top:-4px;"
   "right:-4px;"
   "display:none;}"

   "slot[name=badge]::slotted(*){display:block;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=root]{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style      (.createElement js/document "style")
        root-el    (.createElement js/document "div")
        img        (.createElement js/document "img")
        initials   (.createElement js/document "span")
        fallback   (.createElement js/document "span")
        status     (.createElement js/document "span")
        badge      (.createElement js/document "span")
        badge-slot (.createElement js/document "slot")
        refs       {:root-el     root-el
                    :img         img
                    :initials-el initials
                    :fallback-el fallback
                    :status-el   status
                    :badge-el    badge
                    :badge-slot  badge-slot}]

    (set! (.-textContent style) style-text)

    (.setAttribute root-el  attr-part part-root)
    (.setAttribute img      attr-part part-image)
    (.setAttribute img      attr-alt  "")
    (.setAttribute initials attr-part part-initials)
    (.setAttribute fallback attr-part part-fallback)
    (set! (.-textContent fallback) val-fallback)
    (.setAttribute status   attr-part        part-status)
    (.setAttribute status   attr-aria-hidden val-true)
    (.setAttribute badge      attr-part part-badge)
    (.setAttribute badge-slot attr-name slot-name-badge)
    (.appendChild badge badge-slot)

    (.appendChild root-el img)
    (.appendChild root-el initials)
    (.appendChild root-el fallback)

    (.appendChild root style)
    (.appendChild root root-el)
    (.appendChild root status)
    (.appendChild root badge)

    (gobj/set el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs) (init-dom! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:src-raw           (du/get-attr el model/attr-src)
    :alt-raw           (du/get-attr el model/attr-alt)
    :name-raw          (du/get-attr el model/attr-name)
    :initials-raw      (du/get-attr el model/attr-initials)
    :size-raw          (du/get-attr el model/attr-size)
    :shape-raw         (du/get-attr el model/attr-shape)
    :variant-raw       (du/get-attr el model/attr-variant)
    :status-raw        (du/get-attr el model/attr-status)
    :disabled-present? (du/has-attr? el model/attr-disabled)}))

;; ── Slot probing ──────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- set-display! [^js node v]
  (set! (.. node -style -display) v))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ───────
(defn- apply-host-data! [^js el {:keys [size shape variant]}]
  (du/set-attr! el attr-data-size    size)
  (du/set-attr! el attr-data-shape   shape)
  (du/set-attr! el attr-data-variant variant))

(defn- apply-host-aria! [^js el m]
  (let [lbl (model/label m)]
    (if lbl
      (do (du/set-attr! el attr-role       role-img)
          (du/set-attr! el attr-aria-label lbl)
          (du/remove-attr! el attr-aria-hidden))
      (do (du/remove-attr! el attr-role)
          (du/remove-attr! el attr-aria-label)
          (du/set-attr! el attr-aria-hidden val-true))))
  (if (:disabled m)
    (du/set-attr! el attr-aria-disabled val-true)
    (du/remove-attr! el attr-aria-disabled)))

(defn- apply-image! [^js el ^js img {:keys [src]} show-img?]
  (let [next (or src "")
        prev (or (gobj/get el k-last-src) "")]
    (when (not= prev next)
      (gobj/set el k-last-src next)
      (set! (.-src img) next)))
  (set-display! img (if show-img? "block" "none")))

(defn- apply-text-layers!
  [^js initials-el ^js fallback-el text show-initials? show-fallback?]
  (when show-initials?
    (set! (.-textContent initials-el) text))
  (set-display! initials-el (if show-initials? "inline" "none"))
  (set-display! fallback-el (if show-fallback? "inline" "none")))

(defn- status-css-value [status]
  (case status
    "online" "var(--x-avatar-status-online)"
    "busy"   "var(--x-avatar-status-busy)"
    "away"   "var(--x-avatar-status-away)"
    "var(--x-avatar-status-offline)"))

(defn- apply-status! [^js el ^js status-el {:keys [status]}]
  (if status
    (do (.setProperty (.-style el) css-var-status-color (status-css-value status))
        (set-display! status-el "block"))
    (set-display! status-el "none")))

(defn- apply-badge! [^js badge-el has-badge?]
  (set-display! badge-el (if has-badge? "block" "none")))

(defn- apply-model! [^js el m]
  (let [{:keys [img initials-el fallback-el status-el badge-el badge-slot]}
        (ensure-refs! el)
        ^js img         img
        ^js initials-el initials-el
        ^js fallback-el fallback-el
        ^js status-el   status-el
        ^js badge-el    badge-el
        ^js badge-slot  badge-slot
        img-ok?         (boolean (gobj/get el k-img-ok))
        has-src?        (some? (:src m))
        text            (model/display-text m)
        show-img?       (and has-src? img-ok?)
        show-initials?  (and (not show-img?) (some? text))
        show-fallback?  (and (not show-img?) (not show-initials?))
        has-badge?      (slot-has-content? badge-slot)]
    (apply-host-data!    el m)
    (apply-host-aria!    el m)
    (apply-image!        el img m show-img?)
    (apply-text-layers!  initials-el fallback-el text show-initials? show-fallback?)
    (apply-status!       el status-el m)
    (apply-badge!        badge-el has-badge?)
    (gobj/set el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [img badge-slot]} (ensure-refs! el)
        ^js img        img
        ^js badge-slot badge-slot
        on-load  (fn handle-img-load  [_] (gobj/set el k-img-ok true)  (update-from-attrs! el))
        on-error (fn handle-img-error [_] (gobj/set el k-img-ok false) (update-from-attrs! el))
        on-slot  (fn handle-slotchange [_] (update-from-attrs! el))
        handlers #js {}]
    (.addEventListener img ev-load  on-load)
    (.addEventListener img ev-error on-error)
    (when badge-slot (.addEventListener badge-slot ev-slotchange on-slot))
    (gobj/set handlers hk-load  on-load)
    (gobj/set handlers hk-error on-error)
    (gobj/set handlers hk-slot  on-slot)
    (gobj/set el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js img        (:img refs)
            ^js badge-slot (:badge-slot refs)
            on-load        (gobj/get hs hk-load)
            on-error       (gobj/get hs hk-error)
            on-slot        (gobj/get hs hk-slot)]
        (when on-load  (.removeEventListener img ev-load  on-load))
        (when on-error (.removeEventListener img ev-error on-error))
        (when (and badge-slot on-slot)
          (.removeEventListener badge-slot ev-slotchange on-slot)))))
  (gobj/set el k-handlers nil))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (when (= attr-name model/attr-src)
      (gobj/set el k-img-ok false))
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
