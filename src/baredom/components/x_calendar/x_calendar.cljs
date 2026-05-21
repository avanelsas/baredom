(ns baredom.components.x-calendar.x-calendar
  "DOM + lifecycle layer for x-calendar — a standalone, always-visible
   inline month calendar. Pure date/model logic lives in
   baredom.components.x-calendar.model and baredom.utils.dates; this
   namespace only performs DOM side effects."
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.utils.dates :as dates]
            [baredom.components.x-calendar.model :as model]
            [goog.object :as gobj]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-initialized? "__xCalendarInitialized")
(def ^:private k-model        "__xCalendarModel")
(def ^:private k-refs         "__xCalendarRefs")
(def ^:private k-grid-focus   "__xCalendarGridFocus")
(def ^:private k-range-step   "__xCalendarRangeStep")
(def ^:private k-jump-open?   "__xCalendarJumpOpen")
(def ^:private k-jump-year    "__xCalendarJumpYear")
(def ^:private k-listeners?   "__xCalendarListeners")

;; ── String-literal constants (Closure-Advanced safe) ─────────────────────────
(def ^:private tag-button "button")
(def ^:private tag-div    "div")
(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private attr-part        "part")
(def ^:private attr-type        "type")
(def ^:private attr-role        "role")
(def ^:private attr-tabindex    "tabindex")
(def ^:private attr-hidden      "hidden")
(def ^:private attr-aria-label  "aria-label")
(def ^:private attr-aria-hidden "aria-hidden")
(def ^:private attr-aria-disabled "aria-disabled")
(def ^:private attr-aria-expanded "aria-expanded")
(def ^:private attr-aria-current  "aria-current")
(def ^:private attr-aria-selected "aria-selected")
(def ^:private attr-data-iso     "data-iso")
(def ^:private attr-data-dir     "data-dir")
(def ^:private attr-data-month   "data-month-index")
(def ^:private attr-data-current "data-current")

(def ^:private val-true  "true")
(def ^:private val-false "false")

;; ── Refs-object keys ─────────────────────────────────────────────────────────
(def ^:private rk-month-label    "month-label")
(def ^:private rk-jump           "jump")
(def ^:private rk-jump-year-lbl  "jump-year-label")
(def ^:private rk-jump-months    "jump-months")
(def ^:private rk-weekdays       "weekdays")
(def ^:private rk-grid           "grid")
(def ^:private rk-nav-prev       "nav-prev")
(def ^:private rk-nav-next       "nav-next")
(def ^:private rk-jump-year-prev "jump-year-prev")
(def ^:private rk-jump-year-next "jump-year-next")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;color-scheme:light dark;"
   "--x-calendar-width:18rem;"
   "--x-calendar-surface:var(--x-color-surface,#fff);"
   "--x-calendar-bg:var(--x-color-bg,#fff);"
   "--x-calendar-border:var(--x-color-border,#cbd5e1);"
   "--x-calendar-radius:var(--x-radius-lg,12px);"
   "--x-calendar-text:var(--x-color-text,inherit);"
   "--x-calendar-muted:var(--x-color-text-muted,#94a3b8);"
   "--x-calendar-focus:var(--x-color-focus-ring,#60a5fa);"
   "--x-calendar-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-calendar-selected-bg:var(--x-color-primary,#2563eb);"
   "--x-calendar-selected-text:#fff;"
   "--x-calendar-range-bg:var(--x-color-surface-active,#dbeafe);"
   "--x-calendar-range-text:var(--x-color-primary-active,#1e40af);"
   "--x-calendar-today-ring:var(--x-color-primary,#2563eb);"
   "}"
   "[part=calendar]{"
   "position:relative;box-sizing:border-box;"
   "width:var(--x-calendar-width,18rem);max-width:calc(100vw - 2rem);"
   "background:var(--x-calendar-surface,#fff);"
   "color:var(--x-calendar-text,inherit);"
   "border:1px solid var(--x-calendar-border,#cbd5e1);"
   "border-radius:var(--x-calendar-radius,12px);"
   "padding:12px;font-size:0.875rem;"
   "}"
   ":host([disabled]) [part=calendar]{opacity:var(--x-opacity-disabled,0.5);pointer-events:none;}"
   ;; header
   "[part=header]{display:flex;align-items:center;justify-content:space-between;margin-bottom:8px;}"
   "[part=navbtn]{"
   "all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"
   "width:32px;height:32px;border-radius:6px;color:inherit;"
   "}"
   "[part=navbtn]:hover{background:var(--x-calendar-hover,#f1f5f9);}"
   "[part=navbtn]:focus-visible{outline:2px solid var(--x-calendar-focus,#60a5fa);}"
   "[part=monthlabel]{"
   "all:unset;cursor:pointer;font-weight:600;font-size:0.9375rem;"
   "padding:4px 8px;border-radius:6px;color:inherit;"
   "}"
   "[part=monthlabel]:hover{background:var(--x-calendar-hover,#f1f5f9);}"
   "[part=monthlabel]:focus-visible{outline:2px solid var(--x-calendar-focus,#60a5fa);}"
   ;; weekday header + grid
   "[part=weekdays]{display:grid;grid-template-columns:repeat(7,1fr);gap:2px;margin-bottom:2px;}"
   ":host([show-week-numbers]) [part=weekdays]{grid-template-columns:1.75rem repeat(7,1fr);}"
   "[part=weekday]{text-align:center;font-size:0.75rem;font-weight:600;"
   "color:var(--x-calendar-muted,#94a3b8);padding:4px 0;}"
   "[part=grid]{display:grid;grid-template-columns:repeat(7,1fr);gap:2px;}"
   ":host([show-week-numbers]) [part=grid]{grid-template-columns:1.75rem repeat(7,1fr);}"
   "[part=weeknum]{display:flex;align-items:center;justify-content:center;"
   "font-size:0.6875rem;color:var(--x-calendar-muted,#94a3b8);}"
   "[part=day]{"
   "all:unset;cursor:pointer;box-sizing:border-box;"
   "display:flex;align-items:center;justify-content:center;"
   "aspect-ratio:1;min-width:1.75rem;border-radius:6px;"
   "transition:background-color 0.12s ease;"
   "}"
   "[part=day]:hover:not([data-disabled=true]){background:var(--x-calendar-hover,#f1f5f9);}"
   "[part=day]:focus-visible{outline:2px solid var(--x-calendar-focus,#60a5fa);}"
   "[part=day][data-outside=true]{opacity:0.4;}"
   "[part=day][data-disabled=true]{opacity:0.3;cursor:default;pointer-events:none;}"
   "[part=day][data-today=true]{box-shadow:inset 0 0 0 1px var(--x-calendar-today-ring,#2563eb);}"
   "[part=day][data-in-range=true]:not([data-range-edge=true]){"
   "background:var(--x-calendar-range-bg,#dbeafe);"
   "color:var(--x-calendar-range-text,#1e40af);border-radius:0;}"
   "[part=day][data-selected=true],[part=day][data-range-edge=true]{"
   "background:var(--x-calendar-selected-bg,#2563eb);"
   "color:var(--x-calendar-selected-text,#fff);font-weight:600;}"
   ;; quick-jump panel
   "[part=jump]{"
   "position:absolute;left:12px;right:12px;top:44px;z-index:1;"
   "display:flex;flex-direction:column;gap:8px;"
   "background:var(--x-calendar-bg,#fff);"
   "border:1px solid var(--x-calendar-border,#cbd5e1);"
   "border-radius:8px;padding:8px;"
   "box-shadow:var(--x-shadow-md,0 8px 24px rgba(0,0,0,0.12));"
   "}"
   "[part=jump][hidden]{display:none;}"
   "[part=jump-year]{display:flex;align-items:center;justify-content:space-between;}"
   "[part=jump-yearlabel]{font-weight:600;}"
   "[part=jump-yearbtn]{"
   "all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"
   "width:28px;height:28px;border-radius:6px;color:inherit;"
   "}"
   "[part=jump-yearbtn]:hover{background:var(--x-calendar-hover,#f1f5f9);}"
   "[part=jump-yearbtn]:focus-visible{outline:2px solid var(--x-calendar-focus,#60a5fa);}"
   "[part=jump-months]{display:grid;grid-template-columns:repeat(3,1fr);gap:4px;}"
   "[part=jump-month]{"
   "all:unset;cursor:pointer;text-align:center;padding:6px 4px;border-radius:6px;"
   "font-size:0.8125rem;color:inherit;"
   "}"
   "[part=jump-month]:hover{background:var(--x-calendar-hover,#f1f5f9);}"
   "[part=jump-month]:focus-visible{outline:2px solid var(--x-calendar-focus,#60a5fa);}"
   "[part=jump-month][data-current=true]{"
   "background:var(--x-calendar-selected-bg,#2563eb);color:var(--x-calendar-selected-text,#fff);}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-calendar-border:var(--x-color-border,#334155);"
   "--x-calendar-surface:var(--x-color-surface,#0f172a);"
   "--x-calendar-bg:var(--x-color-bg,#1e293b);"
   "--x-calendar-hover:var(--x-color-surface-active,#334155);"
   "--x-calendar-range-bg:var(--x-color-surface-active,#1e3a8a);"
   "--x-calendar-range-text:var(--x-color-focus-ring,#93c5fd);"
   "}}"
   "@media (prefers-reduced-motion:reduce){[part=day]{transition:none;}}"))

;; ── Shadow DOM construction ──────────────────────────────────────────────────

(defn- make-month-buttons! [^js container]
  (dotimes [i 12]
    (let [^js btn (.createElement js/document tag-button)]
      (du/set-attr! btn attr-type tag-button)
      (du/set-attr! btn attr-part "jump-month")
      (du/set-attr! btn attr-data-month (str i))
      (.appendChild container btn))))

(defn- init-dom! [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        ^js style     (.createElement js/document "style")
        ^js calendar  (.createElement js/document tag-div)
        ^js header    (.createElement js/document tag-div)
        ^js nav-prev  (.createElement js/document tag-button)
        ^js label     (.createElement js/document tag-button)
        ^js nav-next  (.createElement js/document tag-button)
        ^js jump      (.createElement js/document tag-div)
        ^js jump-year (.createElement js/document tag-div)
        ^js jy-prev   (.createElement js/document tag-button)
        ^js jy-label  (.createElement js/document tag-div)
        ^js jy-next   (.createElement js/document tag-button)
        ^js jump-mons (.createElement js/document tag-div)
        ^js weekdays  (.createElement js/document tag-div)
        ^js grid      (.createElement js/document tag-div)]
    (set! (.-textContent style) style-text)

    (du/set-attr! calendar attr-part "calendar")
    (du/set-attr! header   attr-part "header")

    (du/set-attr! nav-prev attr-part "navbtn")
    (du/set-attr! nav-prev attr-type tag-button)
    (du/set-attr! nav-prev attr-data-dir "prev")
    (du/set-attr! nav-prev attr-aria-label "Previous month")
    (set! (.-textContent nav-prev) "‹")

    (du/set-attr! label attr-part "monthlabel")
    (du/set-attr! label attr-type tag-button)
    (du/set-attr! label "aria-haspopup" val-true)
    (du/set-attr! label attr-aria-expanded val-false)
    (du/set-attr! label "aria-live" "polite")

    (du/set-attr! nav-next attr-part "navbtn")
    (du/set-attr! nav-next attr-type tag-button)
    (du/set-attr! nav-next attr-data-dir "next")
    (du/set-attr! nav-next attr-aria-label "Next month")
    (set! (.-textContent nav-next) "›")

    (du/set-attr! jump attr-part "jump")
    (du/set-attr! jump attr-hidden "")
    (du/set-attr! jump-year attr-part "jump-year")

    (du/set-attr! jy-prev attr-part "jump-yearbtn")
    (du/set-attr! jy-prev attr-type tag-button)
    (du/set-attr! jy-prev attr-data-dir "prev")
    (du/set-attr! jy-prev attr-aria-label "Previous year")
    (set! (.-textContent jy-prev) "‹")

    (du/set-attr! jy-label attr-part "jump-yearlabel")

    (du/set-attr! jy-next attr-part "jump-yearbtn")
    (du/set-attr! jy-next attr-type tag-button)
    (du/set-attr! jy-next attr-data-dir "next")
    (du/set-attr! jy-next attr-aria-label "Next year")
    (set! (.-textContent jy-next) "›")

    (du/set-attr! jump-mons attr-part "jump-months")
    (make-month-buttons! jump-mons)

    (du/set-attr! weekdays attr-part "weekdays")
    (du/set-attr! weekdays attr-aria-hidden val-true)

    (du/set-attr! grid attr-part "grid")
    (du/set-attr! grid attr-role "grid")
    (du/set-attr! grid attr-aria-label "Calendar")

    (.appendChild header nav-prev)
    (.appendChild header label)
    (.appendChild header nav-next)

    (.appendChild jump-year jy-prev)
    (.appendChild jump-year jy-label)
    (.appendChild jump-year jy-next)
    (.appendChild jump jump-year)
    (.appendChild jump jump-mons)

    (.appendChild calendar header)
    (.appendChild calendar jump)
    (.appendChild calendar weekdays)
    (.appendChild calendar grid)

    (.appendChild root style)
    (.appendChild root calendar)

    (du/setv! el k-refs
              #js {"calendar"        calendar
                   "nav-prev"        nav-prev
                   "month-label"     label
                   "nav-next"        nav-next
                   "jump"            jump
                   "jump-year-prev"  jy-prev
                   "jump-year-label" jy-label
                   "jump-year-next"  jy-next
                   "jump-months"     jump-mons
                   "weekdays"        weekdays
                   "grid"            grid})))

;; ── DOM patching ─────────────────────────────────────────────────────────────

(defn- read-model [^js el]
  (model/canonicalize
   {:mode                  (du/get-attr el model/attr-mode)
    :value                 (du/get-attr el model/attr-value)
    :start                 (du/get-attr el model/attr-start)
    :end                   (du/get-attr el model/attr-end)
    :min                   (du/get-attr el model/attr-min)
    :max                   (du/get-attr el model/attr-max)
    :disabled-dates        (du/get-attr el model/attr-disabled-dates)
    :first-day-of-week     (du/get-attr el model/attr-first-day-of-week)
    :locale                (du/get-attr el model/attr-locale)
    :month-raw             (du/get-attr el model/attr-month)
    :disabled?             (du/has-attr? el model/attr-disabled)
    :show-week-numbers?    (du/has-attr? el model/attr-show-week-numbers)
    :range-allow-same-day? (du/has-attr? el model/attr-range-allow-same)
    :auto-swap?            (du/has-attr? el model/attr-auto-swap)
    :today                 (js/Date. (js/Date.now))}))

(defn- apply-host-state! [^js el m]
  (if (:disabled? m)
    (du/set-attr! el attr-aria-disabled val-true)
    (du/remove-attr! el attr-aria-disabled)))

(defn- apply-header! [^js el m]
  (let [^js label (gobj/get (du/getv el k-refs) rk-month-label)
        view      (dates/iso->date (:view-iso m))]
    (set! (.-textContent label) (model/month-label view (:locale m)))))

(defn- apply-weekdays! [^js el m]
  (let [^js wd (gobj/get (du/getv el k-refs) rk-weekdays)]
    (set! (.-textContent wd) "")
    (when (:show-week-numbers? m)
      (let [^js spacer (.createElement js/document tag-div)]
        (du/set-attr! spacer attr-aria-hidden val-true)
        (.appendChild wd spacer)))
    (doseq [nm (model/weekday-names (:locale m) (:fdow m))]
      (let [^js cell (.createElement js/document tag-div)]
        (du/set-attr! cell attr-part "weekday")
        (set! (.-textContent cell) nm)
        (.appendChild wd cell)))))

(defn- make-weeknum-cell! [^js d]
  (let [^js cell (.createElement js/document tag-div)]
    (du/set-attr! cell attr-part "weeknum")
    (du/set-attr! cell attr-aria-hidden val-true)
    (set! (.-textContent cell) (str (dates/iso-week-number d)))
    cell))

(defn- bool-attr [v] (if v val-true val-false))

(defn- make-day-cell!
  [^js d {:keys [iso in-month? today? disabled? selected? in-range? range-edge?]}
   ^js day-fmt]
  (let [^js btn (.createElement js/document tag-button)]
    (du/set-attr! btn attr-type tag-button)
    (du/set-attr! btn attr-part "day")
    (du/set-attr! btn attr-role "gridcell")
    (du/set-attr! btn attr-data-iso iso)
    (du/set-attr! btn "data-outside"    (bool-attr (not in-month?)))
    (du/set-attr! btn "data-today"      (bool-attr today?))
    (du/set-attr! btn "data-disabled"   (bool-attr disabled?))
    (du/set-attr! btn "data-selected"   (bool-attr selected?))
    (du/set-attr! btn "data-in-range"   (bool-attr in-range?))
    (du/set-attr! btn "data-range-edge" (bool-attr range-edge?))
    (du/set-attr! btn attr-aria-selected (bool-attr selected?))
    (du/set-attr! btn attr-aria-disabled (bool-attr disabled?))
    (du/set-attr! btn attr-aria-label (.format day-fmt d))
    (when today? (du/set-attr! btn attr-aria-current "date"))
    (du/set-attr! btn attr-tabindex "-1")
    (set! (.-textContent btn) (str (.getUTCDate d)))
    btn))

(defn- resolve-grid-focus!
  "Pick the single cell that carries tabindex=0 (roving tabindex). Prefers the
   remembered focus cell, then the selection, then today, then the 1st."
  [^js el ^js grid m today-iso]
  (let [present? (fn [iso]
                   (when (and iso
                              (.querySelector grid
                                              (str "[" attr-data-iso "=\"" iso "\"]")))
                     iso))
        iso (or (present? (du/getv el k-grid-focus))
                (present? (:value m))
                (present? (:start m))
                (present? today-iso)
                (:view-iso m))]
    (du/setv! el k-grid-focus iso)
    (when-let [^js btn (.querySelector grid
                                       (str "[" attr-data-iso "=\"" iso "\"]"))]
      (du/set-attr! btn attr-tabindex "0"))))

(defn- apply-grid! [^js el m]
  (let [^js grid  (gobj/get (du/getv el k-refs) rk-grid)
        view      (dates/iso->date (:view-iso m))
        cells     (dates/month-grid view (:fdow m))
        today-iso (dates/date->iso (js/Date. (js/Date.now)))
        wk?       (:show-week-numbers? m)
        day-fmt   (js/Intl.DateTimeFormat.
                   (or (:locale m) "default")
                   #js {:weekday "long" :year "numeric" :month "long"
                        :day "numeric" :timeZone "UTC"})]
    (set! (.-textContent grid) "")
    (dotimes [row 6]
      (let [row-cells (subvec cells (* row 7) (+ (* row 7) 7))]
        (when wk?
          (.appendChild grid (make-weeknum-cell! (:date (first row-cells)))))
        (doseq [{:keys [^js date in-month?]} row-cells]
          (let [iso   (dates/date->iso date)
                flags (model/compute-cell-flags m iso in-month? today-iso)]
            (.appendChild grid (make-day-cell! date flags day-fmt))))))
    (resolve-grid-focus! el grid m today-iso)))

(defn- apply-jump-panel! [^js el m]
  (let [^js refs   (du/getv el k-refs)
        ^js label  (gobj/get refs rk-month-label)
        ^js jump   (gobj/get refs rk-jump)
        ^js ylabel (gobj/get refs rk-jump-year-lbl)
        ^js months (gobj/get refs rk-jump-months)
        open?      (boolean (du/getv el k-jump-open?))
        view       (dates/iso->date (:view-iso m))
        view-year  (.getUTCFullYear view)
        view-month (.getUTCMonth view)
        jump-year  (or (du/getv el k-jump-year) view-year)
        opts       (model/month-options (:locale m))]
    (du/set-attr! label attr-aria-expanded (bool-attr open?))
    (if open?
      (du/remove-attr! jump attr-hidden)
      (du/set-attr! jump attr-hidden ""))
    (set! (.-textContent ylabel) (str jump-year))
    (let [^js kids (.-children months)]
      (dotimes [i (.-length kids)]
        (let [^js btn (.item kids i)]
          (set! (.-textContent btn) (:label (nth opts i)))
          (du/set-attr! btn attr-data-current
                        (bool-attr (and (= jump-year view-year)
                                        (= i view-month)))))))))

(defn- apply-model! [^js el m]
  (apply-host-state! el m)
  (apply-header!     el m)
  (apply-weekdays!   el m)
  (apply-grid!       el m)
  (apply-jump-panel! el m)
  (du/setv! el k-model m))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)
    (du/mark-initialized! el k-initialized?)))

(defn- sync-focus-to-selection!
  "When the selection (value/start/end) changes, move the roving-tabindex
   focus onto it — so a programmatically set value is keyboard-reachable.
   A `month`-only change (e.g. keyboard navigation across months) leaves the
   focus where the interaction put it."
  [^js el old-m new-m]
  (let [anchor (or (:value new-m) (:end new-m) (:start new-m))]
    (when (and anchor
               (or (not= (:value old-m) (:value new-m))
                   (not= (:start old-m) (:start new-m))
                   (not= (:end old-m) (:end new-m))))
      (du/setv! el k-grid-focus anchor))))

(defn- update-from-attrs! [^js el]
  (ensure-shadow! el)
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (sync-focus-to-selection! el old-m new-m)
      (apply-model! el new-m))))

;; ── Selection ────────────────────────────────────────────────────────────────

(defn- dispatch-change! [^js el]
  (let [mode (du/get-attr el model/attr-mode)]
    (du/dispatch! el model/event-change
                  #js {:mode  (if (= "range" mode) "range" "single")
                       :value (or (du/get-attr el model/attr-value) "")
                       :start (or (du/get-attr el model/attr-start) "")
                       :end   (or (du/get-attr el model/attr-end) "")})))

(defn- select-range! [^js el iso m]
  (let [clicked (dates/iso->date iso)
        start-d (dates/iso->date (:start m))
        step    (or (du/getv el k-range-step) 0)]
    (if (and (= step 1) start-d)
      (let [cmp    (dates/compare-date clicked start-d)
            valid? (cond
                     (pos? cmp)  true
                     (zero? cmp) (:range-allow-same-day? m)
                     :else       (:auto-swap? m))]
        (if valid?
          (do (du/set-attr! el model/attr-start
                            (dates/date->iso (dates/min-date start-d clicked)))
              (du/set-attr! el model/attr-end
                            (dates/date->iso (dates/max-date start-d clicked)))
              (du/setv! el k-range-step 0))
          (do (du/set-attr! el model/attr-start iso)
              (du/remove-attr! el model/attr-end)
              (du/setv! el k-range-step 1))))
      (do (du/set-attr! el model/attr-start iso)
          (du/remove-attr! el model/attr-end)
          (du/setv! el k-range-step 1)))
    (dispatch-change! el)))

(defn- select-date! [^js el iso]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (du/setv! el k-grid-focus iso)
      (if (= :range (:mode m))
        (select-range! el iso m)
        (do (du/set-attr! el model/attr-value iso)
            (dispatch-change! el))))))

;; ── Navigation ───────────────────────────────────────────────────────────────

(defn- navigate-to-month! [^js el ^js month-date]
  (let [ym (model/date->year-month month-date)]
    (du/set-attr! el model/attr-month ym)
    (du/dispatch! el model/event-navigate #js {:month ym})))

(defn- focus-grid-date! [^js el iso]
  (du/setv! el k-grid-focus iso)
  (let [^js grid (gobj/get (du/getv el k-refs) rk-grid)]
    (when grid
      (when-let [^js prev (.querySelector grid "[tabindex=\"0\"]")]
        (du/set-attr! prev attr-tabindex "-1"))
      (when-let [^js btn (.querySelector grid
                                         (str "[" attr-data-iso "=\"" iso "\"]"))]
        (du/set-attr! btn attr-tabindex "0")
        (.focus btn)))))

(defn- cell-disabled? [^js el iso]
  (let [^js grid (gobj/get (du/getv el k-refs) rk-grid)
        ^js btn  (when (and grid iso)
                   (.querySelector grid
                                   (str "[" attr-data-iso "=\"" iso "\"]")))]
    (and btn (= val-true (.getAttribute btn "data-disabled")))))

;; ── Keyboard navigation ──────────────────────────────────────────────────────

(defn- keyed-target
  "The date a navigation key should move the focus to, or nil."
  [^js cur key fdow]
  (when cur
    (case key
      "ArrowLeft"  (dates/add-days cur -1)
      "ArrowRight" (dates/add-days cur 1)
      "ArrowUp"    (dates/add-days cur -7)
      "ArrowDown"  (dates/add-days cur 7)
      "PageUp"     (dates/add-months cur -1)
      "PageDown"   (dates/add-months cur 1)
      "Home"       (dates/start-of-week cur fdow)
      "End"        (dates/end-of-week cur fdow)
      nil)))

(defn- move-focus! [^js el m ^js target]
  (let [clamped (dates/clamp-to-range target
                                      (dates/iso->date (:min m))
                                      (dates/iso->date (:max m)))
        iso     (dates/date->iso clamped)
        view    (dates/iso->date (:view-iso m))]
    (du/setv! el k-grid-focus iso)
    (when (not= (.getUTCMonth clamped) (.getUTCMonth view))
      (navigate-to-month! el (dates/start-of-month clamped)))
    (focus-grid-date! el iso)))

(defn- on-grid-keydown! [^js el ^js e]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (let [key     (.-key e)
            cur-iso (du/getv el k-grid-focus)
            ^js cur (when cur-iso (dates/iso->date cur-iso))
            target  (keyed-target cur key (:fdow m))]
        (cond
          (some? target)
          (do (.preventDefault e)
              (move-focus! el m target))

          (and cur (or (= key "Enter") (= key " ")))
          (do (.preventDefault e)
              (when-not (cell-disabled? el cur-iso)
                (select-date! el cur-iso))))))))

;; ── Event handlers ───────────────────────────────────────────────────────────

(defn- on-prev-click! [^js el ^js _e]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (navigate-to-month! el (dates/add-months (dates/iso->date (:view-iso m)) -1)))))

(defn- on-next-click! [^js el ^js _e]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (navigate-to-month! el (dates/add-months (dates/iso->date (:view-iso m)) 1)))))

(defn- on-label-click! [^js el ^js _e]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (let [open? (not (du/getv el k-jump-open?))]
        (du/setv! el k-jump-open? open?)
        (when open?
          (du/setv! el k-jump-year
                    (.getUTCFullYear (dates/iso->date (:view-iso m)))))
        (apply-jump-panel! el m)))))

(defn- step-jump-year! [^js el delta]
  (let [m    (du/getv el k-model)
        year (or (du/getv el k-jump-year)
                 (.getUTCFullYear (dates/iso->date (:view-iso m))))]
    (du/setv! el k-jump-year (+ year delta))
    (apply-jump-panel! el m)))

(defn- on-jump-year-prev! [^js el ^js _e] (step-jump-year! el -1))
(defn- on-jump-year-next! [^js el ^js _e] (step-jump-year! el 1))

(defn- on-jump-month-click! [^js el ^js e]
  (let [^js btn (.closest (.-target e) (str "[" attr-data-month "]"))]
    (when btn
      (let [idx  (js/parseInt (.getAttribute btn attr-data-month) 10)
            m    (du/getv el k-model)
            year (or (du/getv el k-jump-year)
                     (.getUTCFullYear (dates/iso->date (:view-iso m))))]
        (du/setv! el k-jump-open? false)
        (navigate-to-month! el (js/Date. (js/Date.UTC year idx 1)))))))

(defn- on-grid-click! [^js el ^js e]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (let [^js btn (.closest (.-target e) (str "[" attr-data-iso "]"))]
        (when (and btn (not= val-true (.getAttribute btn "data-disabled")))
          (select-date! el (.getAttribute btn attr-data-iso)))))))

;; ── Listener installation (listener-spec named pattern) ──────────────────────
;; Each entry: [refs-key event-name handler-fn]. Listeners bind to shadow-DOM
;; nodes that persist with the element, so no explicit remove path is needed.
(def ^:private listener-spec
  [[rk-nav-prev       ev-click   on-prev-click!]
   [rk-nav-next       ev-click   on-next-click!]
   [rk-month-label    ev-click   on-label-click!]
   [rk-jump-year-prev ev-click   on-jump-year-prev!]
   [rk-jump-year-next ev-click   on-jump-year-next!]
   [rk-jump-months    ev-click   on-jump-month-click!]
   [rk-grid           ev-click   on-grid-click!]
   [rk-grid           ev-keydown on-grid-keydown!]])

(defn- install-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)]
    (doseq [[refs-key event-name handler] listener-spec]
      (let [^js target (gobj/get refs refs-key)]
        (.addEventListener target event-name
                           (fn [^js event] (handler el event)))))))

;; ── Methods ──────────────────────────────────────────────────────────────────

(defn- xcal-focus! [^js this]
  (let [^js grid (gobj/get (du/getv this k-refs) rk-grid)
        iso      (du/getv this k-grid-focus)]
    (when (and grid iso)
      (when-let [^js btn (.querySelector grid
                                         (str "[" attr-data-iso "=\"" iso "\"]"))]
        (.focus btn)))))

(defn- xcal-go-to-month! [^js this ym]
  (when-let [^js d (model/parse-year-month ym)]
    (du/set-attr! this model/attr-month (model/date->year-month d))))

(defn- xcal-clear! [^js this]
  (du/remove-attr! this model/attr-value)
  (du/remove-attr! this model/attr-start)
  (du/remove-attr! this model/attr-end)
  (du/setv! this k-range-step 0))

(defn- define-methods! [^js proto]
  (.defineProperty js/Object proto "focus"
                   #js {:value (fn xcal-focus []
                                 (this-as ^js this (xcal-focus! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "goToMonth"
                   #js {:value (fn xcal-go-to-month [ym]
                                 (this-as ^js this (xcal-go-to-month! this ym)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "clear"
                   #js {:value (fn xcal-clear []
                                 (this-as ^js this (xcal-clear! this)))
                        :writable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (define-methods! proto))

;; ── Lifecycle ────────────────────────────────────────────────────────────────

(defn- connected! [^js el]
  (ensure-shadow! el)
  (when-not (du/initialized? el k-listeners?)
    (install-listeners! el)
    (du/mark-initialized! el k-listeners?))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
