(ns baredom.components.x-typography.x-typography
  (:require
[baredom.utils.component :as component]
               [goog.object :as gobj]
   [baredom.components.x-typography.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs  "__xTypographyRefs")
(def ^:private k-model "__xTypographyModel")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  ;; Shorthand for the default font stacks — used in var() fallbacks
  (let [sans  "system-ui,-apple-system,sans-serif"
        mono  "ui-monospace,\"SFMono-Regular\",\"SF Mono\",Menlo,Consolas,monospace"
        ;; Light-mode defaults for variant-specific tokens
        code-bg-light   "rgba(0,0,0,0.06)"
        code-bg-dark    "rgba(255,255,255,0.1)"
        kbd-bg-light    "rgba(0,0,0,0.06)"
        kbd-bg-dark     "rgba(255,255,255,0.1)"
        kbd-bd-light    "rgba(0,0,0,0.15)"
        kbd-bd-dark     "rgba(255,255,255,0.2)"
        bq-bd-light     "rgba(0,0,0,0.2)"
        bq-bd-dark      "rgba(255,255,255,0.25)"]
    (str
     ;; ── Host: no custom-property definitions — consumer sets them,
     ;;    we only read via var(--prop, fallback). This ensures inline
     ;;    style overrides always win. ──
     ":host{"
     "display:block;"
     "color-scheme:light dark;"
     "font-family:var(--x-typography-font-family," sans ");"
     "color:var(--x-typography-color,var(--x-color-text,inherit));}"

     ;; ── Container: box-model reset ──
     "[part=container]{margin:0;padding:0;}"

     ;; ── Variant styles on :host (inherited props reach slotted content) ──
     ;; Default / body1
     ":host(:not([data-variant])),"
     ":host([data-variant='body1']){"
     "font-size:1rem;font-weight:400;line-height:1.5;letter-spacing:normal;}"

     ":host([data-variant='h1']){"
     "font-size:2.5rem;font-weight:700;line-height:1.2;letter-spacing:-0.02em;}"

     ":host([data-variant='h2']){"
     "font-size:2rem;font-weight:700;line-height:1.25;letter-spacing:-0.015em;}"

     ":host([data-variant='h3']){"
     "font-size:1.75rem;font-weight:600;line-height:1.3;letter-spacing:-0.01em;}"

     ":host([data-variant='h4']){"
     "font-size:1.5rem;font-weight:600;line-height:1.35;letter-spacing:-0.005em;}"

     ":host([data-variant='h5']){"
     "font-size:1.25rem;font-weight:600;line-height:1.4;letter-spacing:normal;}"

     ":host([data-variant='h6']){"
     "font-size:1.125rem;font-weight:600;line-height:1.4;letter-spacing:normal;}"

     ":host([data-variant='subtitle1']){"
     "font-size:1.125rem;font-weight:500;line-height:1.4;letter-spacing:0.005em;}"

     ":host([data-variant='subtitle2']){"
     "font-size:0.875rem;font-weight:500;line-height:1.4;letter-spacing:0.01em;}"

     ":host([data-variant='body2']){"
     "font-size:0.875rem;font-weight:400;line-height:1.5;letter-spacing:normal;}"

     ":host([data-variant='caption']){"
     "font-size:0.75rem;font-weight:400;line-height:1.4;letter-spacing:0.02em;}"

     ":host([data-variant='overline']){"
     "font-size:0.625rem;font-weight:600;line-height:1.5;letter-spacing:0.1em;"
     "text-transform:uppercase;}"

     ":host([data-variant='small']){"
     "font-size:0.8125rem;font-weight:400;line-height:1.4;letter-spacing:normal;}"

     ;; Blockquote: inherited props on :host, box props on container
     ":host([data-variant='blockquote']){"
     "font-size:1.125rem;font-weight:400;line-height:1.6;letter-spacing:normal;"
     "font-style:italic;}"
     ":host([data-variant='blockquote']) [part=container]{"
     "border-left:3px solid var(--x-typography-blockquote-border-color,var(--x-color-border," bq-bd-light "));"
     "padding:var(--x-typography-blockquote-padding,0 0 0 1em);}"
     "@media (prefers-color-scheme:dark){"
     ":host([data-variant='blockquote']) [part=container]{"
     "border-left-color:var(--x-typography-blockquote-border-color,var(--x-color-border," bq-bd-dark "));}}"

     ;; Code: inherited props on :host, box props on container
     ":host([data-variant='code']){"
     "font-family:var(--x-typography-mono-font-family," mono ");"
     "font-size:0.875rem;font-weight:400;line-height:1.5;}"
     ":host([data-variant='code']) [part=container]{"
     "background:var(--x-typography-code-bg,var(--x-color-surface," code-bg-light "));"
     "border-radius:var(--x-typography-code-radius,var(--x-radius-sm,4px));"
     "padding:var(--x-typography-code-padding,0.15em 0.35em);}"
     "@media (prefers-color-scheme:dark){"
     ":host([data-variant='code']) [part=container]{"
     "background:var(--x-typography-code-bg,var(--x-color-surface," code-bg-dark "));}}"

     ;; Kbd: inherited props on :host, box props on container
     ":host([data-variant='kbd']){"
     "font-family:var(--x-typography-mono-font-family," mono ");"
     "font-size:0.875rem;font-weight:400;line-height:1.5;}"
     ":host([data-variant='kbd']) [part=container]{"
     "background:var(--x-typography-kbd-bg,var(--x-color-surface," kbd-bg-light "));"
     "border:1px solid var(--x-typography-kbd-border,var(--x-color-border," kbd-bd-light "));"
     "border-radius:var(--x-typography-kbd-radius,var(--x-radius-sm,4px));"
     "padding:var(--x-typography-kbd-padding,0.15em 0.4em);}"
     "@media (prefers-color-scheme:dark){"
     ":host([data-variant='kbd']) [part=container]{"
     "background:var(--x-typography-kbd-bg,var(--x-color-surface," kbd-bg-dark "));"
     "border-color:var(--x-typography-kbd-border,var(--x-color-border," kbd-bd-dark "));}}"

     ;; ── Alignment on :host (text-align inherits to slotted content) ──
     ":host([data-align='left']){text-align:left;}"
     ":host([data-align='center']){text-align:center;}"
     ":host([data-align='right']){text-align:right;}"
     ":host([data-align='justify']){text-align:justify;}"

     ;; ── Truncation on container (overflow is not inherited) ──
     "[part=container][data-truncate]{"
     "overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}")))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        slot      (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (.setAttribute container "part" "container")
    (.appendChild container slot)
    (.appendChild root style)
    (.appendChild root container)
    (gobj/set el k-refs {:root root :container container}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:variant-raw    (.getAttribute el model/attr-variant)
    :align-raw      (.getAttribute el model/attr-align)
    :truncate-raw   (.getAttribute el model/attr-truncate)
    :line-clamp-raw (.getAttribute el model/attr-line-clamp)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [variant align truncate? line-clamp] :as m}]
  (let [{:keys [container]} (ensure-refs! el)
        ^js container container]

    ;; Set data-variant and data-align on host for CSS selectors
    (.setAttribute el "data-variant" variant)
    (.setAttribute el "data-align" align)

    ;; Truncation: data-truncate on container
    (if truncate?
      (.setAttribute container "data-truncate" "")
      (.removeAttribute container "data-truncate"))

    ;; Line-clamp: inline styles on container
    ;; truncate takes precedence over line-clamp
    (if (and (some? line-clamp) (not truncate?))
      (let [^js s (.-style container)]
        (set! (.-display s) "-webkit-box")
        (.setProperty s "-webkit-box-orient" "vertical")
        (set! (.-overflow s) "hidden")
        (.setProperty s "-webkit-line-clamp" (str line-clamp)))
      (let [^js s (.-style container)]
        (.removeProperty s "display")
        (.removeProperty s "-webkit-box-orient")
        (.removeProperty s "overflow")
        (.removeProperty s "-webkit-line-clamp")))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-variant
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-variant) "body1")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-variant (str v))
                                          (.removeAttribute this model/attr-variant))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-align
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-align) "left")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-align (str v))
                                          (.removeAttribute this model/attr-align))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-truncate
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-truncate)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-truncate "")
                                          (.removeAttribute this model/attr-truncate))))
                        :enumerable true :configurable true})

  ;; camelCase JS property mapping to kebab-case attribute
  (.defineProperty js/Object proto "lineClamp"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-line-clamp
                                         (.getAttribute this model/attr-line-clamp))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-line-clamp)
                                          (.setAttribute this model/attr-line-clamp (str (int v))))))
                        :enumerable true :configurable true}))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (update-from-attrs! el)
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
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
