(ns baredom.components.x-kbd.x-kbd
  (:require
   [clojure.string :as str]
   [goog.object :as gobj]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-kbd.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs              "__xKbdRefs")
(def ^:private k-model             "__xKbdModel")
(def ^:private k-detected-platform "__xKbdDetectedPlatform")

;; ── String-literal constants ─────────────────────────────────────────────────
(def ^:private attr-part       "part")
(def ^:private attr-aria-label "aria-label")
(def ^:private attr-data-size  "data-size")
(def ^:private part-base       "base")
(def ^:private part-key        "key")
(def ^:private part-separator  "separator")
(def ^:private tag-kbd         "kbd")
(def ^:private tag-span        "span")
(def ^:private tag-slot        "slot")
(def ^:private rk-base         "base")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:baseline;"
   "vertical-align:baseline;"
   "gap:var(--x-kbd-gap,0.25rem);"
   "color-scheme:light dark;"
   "--x-kbd-bg:var(--x-typography-kbd-bg,rgba(0,0,0,0.06));"
   "--x-kbd-color:var(--x-color-text,inherit);"
   "--x-kbd-border-color:var(--x-typography-kbd-border,rgba(0,0,0,0.15));"
   "--x-kbd-border-radius:var(--x-typography-kbd-radius,0.25rem);"
   "--x-kbd-padding:var(--x-typography-kbd-padding,0.1em 0.4em);"
   "--x-kbd-font-family:ui-monospace,\"SF Mono\",\"SFMono-Regular\",Menlo,Consolas,monospace;"
   "--x-kbd-font-size:0.875em;"
   "--x-kbd-shadow:inset 0 -1px 0 rgba(0,0,0,0.08);"
   "--x-kbd-separator-color:var(--x-color-text-muted,currentColor);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-kbd-bg:var(--x-typography-kbd-bg,rgba(255,255,255,0.1));"
   "--x-kbd-border-color:var(--x-typography-kbd-border,rgba(255,255,255,0.2));"
   "--x-kbd-shadow:inset 0 -1px 0 rgba(0,0,0,0.4);}}"

   ;; Size variants
   ":host([data-size='sm']){"
   "--x-kbd-font-size:0.75em;"
   "--x-kbd-padding:0.05em 0.3em;}"

   ":host([data-size='lg']){"
   "--x-kbd-font-size:1em;"
   "--x-kbd-padding:0.15em 0.5em;}"

   ;; Container
   "[part=base]{"
   "display:inline-flex;"
   "align-items:baseline;"
   "gap:var(--x-kbd-gap,0.25rem);"
   "flex-wrap:wrap;"
   "color:var(--x-kbd-color);}"

   ;; Each cap is a real <kbd>
   "[part=key]{"
   "display:inline-block;"
   "min-width:1.25em;"
   "padding:var(--x-kbd-padding);"
   "border:1px solid var(--x-kbd-border-color);"
   "border-radius:var(--x-kbd-border-radius);"
   "background:var(--x-kbd-bg);"
   "color:inherit;"
   "font-family:var(--x-kbd-font-family);"
   "font-size:var(--x-kbd-font-size);"
   "font-weight:600;"
   "line-height:1.2;"
   "text-align:center;"
   "white-space:nowrap;"
   "box-shadow:var(--x-kbd-shadow);"
   "box-sizing:border-box;}"

   "[part=separator]{"
   "display:inline-block;"
   "color:var(--x-kbd-separator-color);"
   "font-size:var(--x-kbd-font-size);"
   "user-select:none;}"

   "::slotted(*){font-family:var(--x-kbd-font-family);}"))

;; ── Platform detection (effects only) ────────────────────────────────────────
(defn- detect-platform! []
  (let [^js nav  js/navigator
        ua-data  (gobj/get nav "userAgentData")
        ua-pf    (when ua-data (gobj/get ua-data "platform"))
        legacy   (gobj/get nav "platform")
        s        (-> (or ua-pf legacy "") str .toLowerCase)]
    (cond
      (or (str/includes? s "mac")
          (str/includes? s "iphone")
          (str/includes? s "ipad"))      "mac"
      (str/includes? s "win")            "win"
      :else                              "linux")))

(defn- ensure-detected-platform! [^js el]
  (or (du/getv el k-detected-platform)
      (let [p (detect-platform!)]
        (du/setv! el k-detected-platform p)
        p)))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (.createElement js/document tag-span)
        refs  #js {}]
    (set! (.-textContent style) style-text)
    (.setAttribute base attr-part part-base)
    (.appendChild root style)
    (.appendChild root base)
    (gobj/set refs rk-base base)
    (du/setv! el k-refs refs)))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (let [detected (ensure-detected-platform! el)
        resolved (model/resolve-platform
                  (du/get-attr el model/attr-platform)
                  detected)]
    (model/normalize
     {:keys-raw      (du/get-attr el model/attr-keys)
      :separator-raw (du/get-attr el model/attr-separator)
      :size-raw      (du/get-attr el model/attr-size)
      :platform-raw  resolved
      :label-raw     (du/get-attr el model/attr-label)})))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- clear-children! [^js node]
  (while (.-firstChild node)
    (.removeChild node (.-firstChild node))))

(defn- build-key-cap! [visible]
  (let [k (.createElement js/document tag-kbd)]
    (.setAttribute k attr-part part-key)
    (set! (.-textContent k) visible)
    k))

(defn- build-separator! [sep]
  (let [s (.createElement js/document tag-span)]
    (.setAttribute s attr-part part-separator)
    (set! (.-textContent s) sep)
    s))

(defn- build-slot-cap! []
  (let [k    (.createElement js/document tag-kbd)
        slot (.createElement js/document tag-slot)]
    (.setAttribute k attr-part part-key)
    (.appendChild k slot)
    k))

(defn- render-base! [^js base {:keys [tokens separator slot-mode?]}]
  (clear-children! base)
  (if slot-mode?
    (.appendChild base (build-slot-cap!))
    (let [n (count tokens)]
      (dotimes [i n]
        (let [tok (nth tokens i)]
          (when (pos? i)
            (.appendChild base (build-separator! separator)))
          (.appendChild base (build-key-cap! (:visible tok))))))))

(defn- apply-host-aria! [^js el label]
  (if (= "" label)
    (du/remove-attr! el attr-aria-label)
    (du/set-attr! el attr-aria-label label)))

(defn- apply-model! [^js el {:keys [size label] :as m}]
  (let [refs (ensure-refs! el)
        ^js base (gobj/get refs rk-base)]
    (du/set-attr! el attr-data-size size)
    (apply-host-aria! el label)
    (render-base! base m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (ensure-detected-platform! el)
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
