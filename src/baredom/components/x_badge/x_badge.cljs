(ns baredom.components.x-badge.x-badge
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-badge.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xBadgeRefs")
(def ^:private k-model    "__xBadgeModel")
(def ^:private k-handlers "__xBadgeHandlers")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part             "part")
(def ^:private attr-role             "role")
(def ^:private attr-data-variant     "data-variant")
(def ^:private attr-data-size        "data-size")
(def ^:private attr-data-mode        "data-mode")
(def ^:private attr-data-pill        "data-pill")
(def ^:private attr-data-dot         "data-dot")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-aria-describedby "aria-describedby")

(def ^:private part-base  "base")
(def ^:private part-label "label")

(def ^:private role-status "status")

(def ^:private ev-slotchange "slotchange")
(def ^:private hk-slot       "slot")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-flex;"
   "vertical-align:middle;"
   "align-items:center;"
   "justify-content:center;"
   "color-scheme:light dark;"
   "--x-badge-bg:rgba(0,0,0,0.08);"
   "--x-badge-color:var(--x-color-text,rgba(0,0,0,0.80));"
   "--x-badge-border:var(--x-color-border,rgba(0,0,0,0.12));"
   "--x-badge-font-size:var(--x-font-size-sm,0.75rem);"
   "--x-badge-height:1.25rem;"
   "--x-badge-padding:0 0.375rem;"
   "--x-badge-radius:var(--x-radius-sm,0.25rem);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-badge-bg:rgba(255,255,255,0.12);"
   "--x-badge-color:var(--x-color-text,rgba(255,255,255,0.88));"
   "--x-badge-border:var(--x-color-border,rgba(255,255,255,0.14));}}"

   ;; Variant overrides
   ":host([data-variant='info']){"
   "--x-badge-bg:rgba(0,102,204,0.12);"
   "--x-badge-color:rgba(0,60,140,0.90);"
   "--x-badge-border:rgba(0,102,204,0.25);}"

   ":host([data-variant='success']){"
   "--x-badge-bg:rgba(16,140,72,0.12);"
   "--x-badge-color:rgba(0,90,45,0.90);"
   "--x-badge-border:rgba(16,140,72,0.25);}"

   ":host([data-variant='warning']){"
   "--x-badge-bg:rgba(204,120,0,0.12);"
   "--x-badge-color:rgba(140,70,0,0.90);"
   "--x-badge-border:rgba(204,120,0,0.25);}"

   ":host([data-variant='error']){"
   "--x-badge-bg:rgba(190,20,40,0.12);"
   "--x-badge-color:rgba(140,0,20,0.90);"
   "--x-badge-border:rgba(190,20,40,0.25);}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-variant='info']){"
   "--x-badge-bg:rgba(80,160,255,0.18);"
   "--x-badge-color:rgba(210,235,255,0.95);"
   "--x-badge-border:rgba(80,160,255,0.35);}"
   ":host([data-variant='success']){"
   "--x-badge-bg:rgba(60,210,120,0.18);"
   "--x-badge-color:rgba(200,255,230,0.95);"
   "--x-badge-border:rgba(60,210,120,0.35);}"
   ":host([data-variant='warning']){"
   "--x-badge-bg:rgba(255,190,90,0.18);"
   "--x-badge-color:rgba(255,235,180,0.95);"
   "--x-badge-border:rgba(255,190,90,0.35);}"
   ":host([data-variant='error']){"
   "--x-badge-bg:rgba(255,90,110,0.18);"
   "--x-badge-color:rgba(255,210,215,0.95);"
   "--x-badge-border:rgba(255,90,110,0.35);}}"

   ;; Size — sm
   ":host([data-size='sm']){"
   "--x-badge-font-size:0.6875rem;"
   "--x-badge-height:1rem;"
   "--x-badge-padding:0 0.25rem;"
   "--x-badge-radius:0.1875rem;}"

   ;; Base element
   "[part=base]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "min-height:var(--x-badge-height);"
   "padding:var(--x-badge-padding);"
   "border-radius:var(--x-badge-radius);"
   "border:1px solid var(--x-badge-border);"
   "background:var(--x-badge-bg);"
   "color:var(--x-badge-color);"
   "font-size:var(--x-badge-font-size);"
   "font-weight:600;"
   "line-height:1;"
   "white-space:nowrap;"
   "user-select:none;"
   "box-sizing:border-box;}"

   ;; Pill modifier
   ":host([data-pill]) [part=base]{border-radius:999px;}"

   ;; Dot mode — equal width/height, no padding, no label
   ":host([data-dot]) [part=base]{"
   "min-width:var(--x-badge-height);"
   "padding:0;}"
   ":host([data-mode='dot']) [part=label],"
   ":host([data-mode='empty']) [part=base]{"
   "display:none;}"

   ;; Slot content — hidden by default; shown only in :slot mode
   "slot{display:none;}"
   ":host([data-mode='slot']) slot{display:contents;}"
   ":host([data-mode='slot']) [part=label]{display:none;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=base]{transition:none !important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (.createElement js/document "style")
        base     (.createElement js/document "div")
        slot-el  (.createElement js/document "slot")
        label-el (.createElement js/document "span")]
    (set! (.-textContent style) style-text)
    (du/set-attr! base     attr-part part-base)
    (du/set-attr! base     attr-role role-status)
    (du/set-attr! label-el attr-part part-label)
    (.appendChild base slot-el)
    (.appendChild base label-el)
    (.appendChild root style)
    (.appendChild root base)
    (du/setv! el k-refs {:base     base
                         :slot-el  slot-el
                         :label-el label-el})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  ;; No flatten — only user-slotted nodes, not fallback content
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- read-model [^js el]
  (let [{:keys [slot-el]} (ensure-refs! el)]
    (model/normalize
     {:variant-raw          (du/get-attr el model/attr-variant)
      :size-raw             (du/get-attr el model/attr-size)
      :pill-raw             (du/get-attr el model/attr-pill)
      :dot-raw              (du/get-attr el model/attr-dot)
      :count-raw            (du/get-attr el model/attr-count)
      :max-raw              (du/get-attr el model/attr-max)
      :text-raw             (du/get-attr el model/attr-text)
      :aria-label-raw       (du/get-attr el model/attr-aria-label)
      :aria-describedby-raw (du/get-attr el model/attr-aria-describedby)
      :has-slot?            (slot-has-content? slot-el)})))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [variant size pill dot aria-label aria-describedby] :as m}]
  (let [{:keys [base label-el]} (ensure-refs! el)
        ^js base     base
        ^js label-el label-el
        mode (model/compute-mode m)
        txt  (model/display-text m)]
    (du/set-attr! el attr-data-variant variant)
    (du/set-attr! el attr-data-size    size)
    (du/set-attr! el attr-data-mode    (name mode))
    (if pill (du/set-attr! el attr-data-pill "") (du/remove-attr! el attr-data-pill))
    (if dot  (du/set-attr! el attr-data-dot  "") (du/remove-attr! el attr-data-dot))
    (set! (.-textContent label-el) (or txt ""))
    (if aria-label
      (du/set-attr! base attr-aria-label aria-label)
      (du/remove-attr! base attr-aria-label))
    (if aria-describedby
      (du/set-attr! base attr-aria-describedby aria-describedby)
      (du/remove-attr! base attr-aria-describedby))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot-el]} (ensure-refs! el)
        ^js slot-el slot-el
        on-slot  (fn handle-slotchange [_] (update-from-attrs! el))
        handlers #js {}]
    (when slot-el (.addEventListener slot-el ev-slotchange on-slot))
    (gobj/set handlers hk-slot on-slot)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (when-let [hs (du/getv el k-handlers)]
    (when-let [refs (du/getv el k-refs)]
      (let [^js slot-el (:slot-el refs)
            on-slot     (gobj/get hs hk-slot)]
        (when (and slot-el on-slot)
          (.removeEventListener slot-el ev-slotchange on-slot)))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ────────────────────────────────────────────────────
;; count and max use model/parse-int-attr — keep as inline custom accessor
(defn- def-int-prop! [^js proto attr default]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-int-attr
                                         (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (du/set-attr! this attr (str v))
                                          (du/remove-attr! this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-variant            model/attr-variant            model/default-variant)
  (du/define-string-prop! proto model/attr-size               model/attr-size               model/default-size)
  (du/define-bool-prop!   proto model/attr-pill               model/attr-pill)
  (du/define-bool-prop!   proto model/attr-dot                model/attr-dot)
  (du/define-string-prop! proto model/attr-text               model/attr-text)
  (du/define-string-prop! proto model/attr-aria-label         model/attr-aria-label)
  (du/define-string-prop! proto model/attr-aria-describedby   model/attr-aria-describedby)
  (def-int-prop!          proto model/attr-count nil)
  (def-int-prop!          proto model/attr-max   model/default-max)
  ;; displayText — computed read-only property
  (.defineProperty js/Object proto "displayText"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/display-text (read-model this))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _attr old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
