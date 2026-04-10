(ns baredom.components.x-avatar.x-avatar
  (:require
   [goog.object :as gobj]
   [baredom.components.x-avatar.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xAvatarRefs")
(def ^:private k-model    "__xAvatarModel")
(def ^:private k-handlers "__xAvatarHandlers")
(def ^:private k-img-ok   "__xAvatarImgOk")
(def ^:private k-last-src "__xAvatarLastSrc")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "position:relative;"           ; positioning context for status & badge
   "color-scheme:light dark;"
   "--x-avatar-size-xs:20px;"
   "--x-avatar-size-sm:24px;"
   "--x-avatar-size-md:32px;"
   "--x-avatar-size-lg:40px;"
   "--x-avatar-size-xl:48px;"
   "--x-avatar-radius:10px;"
   "--x-avatar-disabled-opacity:0.55;"
   "--x-avatar-bg:rgba(0,0,0,0.06);"
   "--x-avatar-border:rgba(0,0,0,0.14);"
   "--x-avatar-color:rgba(0,0,0,0.86);"
   "--x-avatar-ring:var(--x-color-surface, #ffffff);"
   "--x-avatar-status-online:rgba(16,140,72,0.95);"
   "--x-avatar-status-offline:rgba(0,0,0,0.45);"
   "--x-avatar-status-busy:rgba(190,20,40,0.95);"
   "--x-avatar-status-away:rgba(204,120,0,0.95);"
   "--x-avatar-status-color:transparent;"
   "--x-avatar-font-size:var(--x-font-size-sm, 0.875rem);"
   "--x-avatar-size:var(--x-avatar-size-md);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-avatar-bg:rgba(255,255,255,0.10);"
   "--x-avatar-border:rgba(255,255,255,0.18);"
   "--x-avatar-color:rgba(255,255,255,0.88);"
   "--x-avatar-ring:#0b0c0f;"
   "--x-avatar-status-online:rgba(60,210,120,0.95);"
   "--x-avatar-status-offline:rgba(255,255,255,0.45);"
   "--x-avatar-status-busy:rgba(255,90,110,0.95);"
   "--x-avatar-status-away:rgba(255,190,90,0.95);}}"

   ;; Size tokens
   ":host([data-size='xs']){--x-avatar-size:var(--x-avatar-size-xs);--x-avatar-font-size:0.625rem;}"
   ":host([data-size='sm']){--x-avatar-size:var(--x-avatar-size-sm);--x-avatar-font-size:0.75rem;}"
   ":host([data-size='md']){--x-avatar-size:var(--x-avatar-size-md);}"
   ":host([data-size='lg']){--x-avatar-size:var(--x-avatar-size-lg);--x-avatar-font-size:1rem;}"
   ":host([data-size='xl']){--x-avatar-size:var(--x-avatar-size-xl);--x-avatar-font-size:1.125rem;}"

   ;; Variant overrides — neutral is the :host default
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

   ;; Avatar circle — overflow:hidden clips the image; status & badge are siblings
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
   "border-radius:999px;}"   ; default: circle

   ":host([data-shape='square']) [part=root]{border-radius:0;}"
   ":host([data-shape='rounded']) [part=root]{border-radius:var(--x-avatar-radius);}"

   ":host([disabled]){opacity:var(--x-avatar-disabled-opacity);}"

   "[part=image]{width:100%;height:100%;object-fit:cover;display:none;}"

   "[part=initials],[part=fallback]{display:none;padding:0 0.25em;}"

   ;; Status dot — absolute on host (outside [part=root]'s overflow:hidden)
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

   ;; Badge — absolute top-right corner of host
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
        badge-slot (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute root-el "part" "root")

    (.setAttribute img "part" "image")
    (.setAttribute img "alt" "")          ; host carries the accessible label

    (.setAttribute initials "part" "initials")

    (.setAttribute fallback "part" "fallback")
    (set! (.-textContent fallback) "?")

    (.setAttribute status "part" "status")
    (.setAttribute status "aria-hidden" "true")

    (.setAttribute badge "part" "badge")
    (.setAttribute badge-slot "name" "badge")
    (.appendChild badge badge-slot)

    ;; [part=root] contains image / initials / fallback — all clipped
    (.appendChild root-el img)
    (.appendChild root-el initials)
    (.appendChild root-el fallback)

    ;; status and badge are siblings of root-el, positioned on the host
    (.appendChild root style)
    (.appendChild root root-el)
    (.appendChild root status)
    (.appendChild root badge)

    (gobj/set el k-refs
              {:root-el    root-el
               :img        img
               :initials-el initials
               :fallback-el fallback
               :status-el   status
               :badge-el    badge
               :badge-slot  badge-slot}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:src-raw           (.getAttribute el model/attr-src)
    :alt-raw           (.getAttribute el model/attr-alt)
    :name-raw          (.getAttribute el model/attr-name)
    :initials-raw      (.getAttribute el model/attr-initials)
    :size-raw          (.getAttribute el model/attr-size)
    :shape-raw         (.getAttribute el model/attr-shape)
    :variant-raw       (.getAttribute el model/attr-variant)
    :status-raw        (.getAttribute el model/attr-status)
    :disabled-present? (.hasAttribute el model/attr-disabled)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  ;; No flatten — we only want externally slotted nodes, not fallback content
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- set-display! [^js node v]
  (set! (.. node -style -display) v)
  nil)

(defn- apply-model! [^js el {:keys [src size shape variant status disabled] :as m}]
  (let [{:keys [img initials-el fallback-el status-el badge-el badge-slot]}
        (ensure-refs! el)
        ^js img          img
        ^js initials-el  initials-el
        ^js fallback-el  fallback-el
        ^js status-el    status-el
        ^js badge-el     badge-el
        ^js badge-slot   badge-slot
        img-ok?       (boolean (gobj/get el k-img-ok))
        has-src?      (some? src)
        text          (model/display-text m)
        lbl           (model/label m)
        show-img?      (and has-src? img-ok?)
        show-initials? (and (not show-img?) (some? text))
        show-fallback? (and (not show-img?) (not show-initials?))
        has-badge?    (slot-has-content? badge-slot)]

    ;; Data attributes drive CSS selectors
    (.setAttribute el "data-size"    size)
    (.setAttribute el "data-shape"   shape)
    (.setAttribute el "data-variant" variant)

    ;; ARIA on host
    (if lbl
      (do (.setAttribute el "role" "img")
          (.setAttribute el "aria-label" lbl)
          (.removeAttribute el "aria-hidden"))
      (do (.removeAttribute el "role")
          (.removeAttribute el "aria-label")
          (.setAttribute el "aria-hidden" "true")))

    (if disabled
      (.setAttribute el "aria-disabled" "true")
      (.removeAttribute el "aria-disabled"))

    ;; Image src — compare against last-assigned to avoid re-triggering load
    (let [next (or src "")
          prev (or (gobj/get el k-last-src) "")]
      (when (not= prev next)
        (gobj/set el k-last-src next)
        (set! (.-src img) next)))

    (set-display! img (if show-img? "block" "none"))

    ;; Initials / fallback
    (when show-initials? (set! (.-textContent initials-el) text))
    (set-display! initials-el (if show-initials? "inline" "none"))
    (set-display! fallback-el (if show-fallback? "inline" "none"))

    ;; Status dot — set CSS var then show/hide
    (if status
      (do (.setProperty (.-style el) "--x-avatar-status-color"
                        (case status
                          "online"  "var(--x-avatar-status-online)"
                          "busy"    "var(--x-avatar-status-busy)"
                          "away"    "var(--x-avatar-status-away)"
                          "var(--x-avatar-status-offline)"))
          (set-display! status-el "block"))
      (set-display! status-el "none"))

    ;; Badge wrapper
    (set-display! badge-el (if has-badge? "block" "none"))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [img badge-slot]} (ensure-refs! el)
        ^js img        img
        ^js badge-slot badge-slot
        on-load  (fn [_] (gobj/set el k-img-ok true)  (update-from-attrs! el))
        on-error (fn [_] (gobj/set el k-img-ok false) (update-from-attrs! el))
        on-slot  (fn [_] (update-from-attrs! el))]
    (.addEventListener img "load"  on-load)
    (.addEventListener img "error" on-error)
    (when badge-slot (.addEventListener badge-slot "slotchange" on-slot))
    (gobj/set el k-handlers #js {:load on-load :error on-error :slot on-slot}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [hs (gobj/get el k-handlers)]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js img        (:img refs)
            ^js badge-slot (:badge-slot refs)
            on-load  (gobj/get hs "load")
            on-error (gobj/get hs "error")
            on-slot  (gobj/get hs "slot")]
        (when on-load  (.removeEventListener img "load"  on-load))
        (when on-error (.removeEventListener img "error" on-error))
        (when (and badge-slot on-slot)
          (.removeEventListener badge-slot "slotchange" on-slot)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- def-string-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-string-prop-default! [^js proto attr default]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (def-string-prop! proto model/attr-src)
  (def-string-prop! proto model/attr-alt)
  (def-string-prop! proto model/attr-name)
  (def-string-prop! proto model/attr-initials)
  (def-string-prop-default! proto model/attr-size    model/default-size)
  (def-string-prop-default! proto model/attr-shape   model/default-shape)
  (def-string-prop-default! proto model/attr-variant model/default-variant)
  (def-string-prop! proto model/attr-status)
  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)   ; reconnect guard — prevents listener doubling
                     (add-listeners! this)
                     (update-from-attrs! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [attr-name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       ;; Reset image-ok only when src changes
                       (when (= attr-name model/attr-src)
                         (gobj/set this k-img-ok false))
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
