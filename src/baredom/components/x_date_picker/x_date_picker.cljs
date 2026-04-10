(ns baredom.components.x-date-picker.x-date-picker
  (:require [goog.object :as gobj]
            [baredom.components.x-date-picker.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance-field keys (gobj-safe)
;; ---------------------------------------------------------------------------

(def ^:private k-refs     "__xDatePickerRefs")
(def ^:private k-state    "__xDatePickerState")
(def ^:private k-focused  "__xDatePickerFocused")
(def ^:private k-display  "__xDatePickerDisplay")
(def ^:private k-pending  "__xDatePickerPendingDisplay")
(def ^:private k-wd-done  "__xDatePickerWeekdaysDone")
(def ^:private k-handlers "__xDatePickerHandlers")
(def ^:private k-range-step "__xDatePickerRangeStep")
(def ^:private k-grid-focus "__xDatePickerGridFocus")

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- make-el
  [tag]
  (.createElement js/document tag))

(defn- get-ref
  [^js el k]
  (gobj/get (gobj/get el k-refs) (name k)))

(defn- dispatch!
  [^js el event-name cancelable detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable (boolean cancelable)})]
    (.dispatchEvent el ev)
    ev))

;; ---------------------------------------------------------------------------
;; Read state from element attrs
;; ---------------------------------------------------------------------------

(defn- read-state!
  "Read all observed attrs, canonicalize, cache result on element."
  [^js el]
  (let [mode-raw  (.getAttribute el model/attr-mode)
        mode      (model/parse-mode mode-raw)
        canon     (model/canonicalize
                   {:mode       mode-raw
                    :value      (.getAttribute el model/attr-value)
                    :start      (.getAttribute el model/attr-start)
                    :end        (.getAttribute el model/attr-end)
                    :min        (.getAttribute el model/attr-min)
                    :max        (.getAttribute el model/attr-max)
                    :format     (.getAttribute el model/attr-format)
                    :locale     (.getAttribute el model/attr-locale)
                    :separator  (.getAttribute el model/attr-separator)
                    :auto-swap? (model/parse-bool-attr
                                 (.getAttribute el model/attr-auto-swap))
                    :range-allow-same-day?
                    (model/parse-bool-attr
                     (.getAttribute el model/attr-range-allow-same))})
        cfg       {:format (:format canon) :locale (:locale canon)}
        disp      (model/display-value canon cfg)
        ;; stored month: default to value-d / start-d / today
        existing  (gobj/get el k-state)
        cur-month (when existing (gobj/get existing "month"))
        anchor    (or (:value-d canon) (:start-d canon)
                      (js/Date. (js/Date.now)))
        month     (or cur-month (model/start-of-month anchor))
        state     #js {:canon  canon
                       :cfg    cfg
                       :disp   disp
                       :month  month}]
    (gobj/set el k-state state)
    state))

;; ---------------------------------------------------------------------------
;; Day flag helpers
;; ---------------------------------------------------------------------------

(defn- day-out-of-range?
  [^js d canon]
  (or (and (:min-d canon) (neg? (model/compare-date d (:min-d canon))))
      (and (:max-d canon) (pos? (model/compare-date d (:max-d canon))))))

(defn- compute-flags
  [^js d canon]
  (if (= (:mode canon) :single)
    {:selected? (and (:value-d canon)
                     (= 0 (model/compare-date d (:value-d canon))))
     :in-range? false
     :edge?     false}
    (let [start (:start-d canon)
          end   (:end-d canon)
          sel?  (or (and start (= 0 (model/compare-date d start)))
                    (and end   (= 0 (model/compare-date d end))))
          in-r? (and start end (model/in-range? d start end))
          edge? (or (and start (= 0 (model/compare-date d start)))
                    (and end   (= 0 (model/compare-date d end))))]
      {:selected? sel? :in-range? in-r? :edge? edge?})))

;; ---------------------------------------------------------------------------
;; Grid rendering
;; ---------------------------------------------------------------------------

(defn- render-grid!
  [^js el ^js grid canon]
  (set! (.-textContent grid) "")
  (let [state (gobj/get el k-state)
        ^js month (when state (gobj/get state "month"))
        items (model/month-grid month)]
    (doseq [{:keys [date in-month?]} items]
      (let [^js btn  (make-el "button")
            iso      (model/date->iso date)
            day      (.getUTCDate date)
            disabled? (day-out-of-range? date canon)
            {:keys [selected? in-range? edge?]} (compute-flags date canon)]
        (.setAttribute btn "part" "day")
        (.setAttribute btn "type" "button")
        (.setAttribute btn "role" "gridcell")
        (.setAttribute btn "data-iso" iso)
        (.setAttribute btn "data-outside" (if in-month? "false" "true"))
        (.setAttribute btn "data-disabled" (if disabled? "true" "false"))
        (.setAttribute btn "data-selected" (if selected? "true" "false"))
        (.setAttribute btn "data-in-range" (if in-range? "true" "false"))
        (.setAttribute btn "data-range-edge" (if edge? "true" "false"))
        (.setAttribute btn "aria-selected" (if selected? "true" "false"))
        (.setAttribute btn "aria-disabled" (if disabled? "true" "false"))
        (.setAttribute btn "tabindex" "-1")
        (set! (.-textContent btn) (str day))
        (.appendChild grid btn)))))

(defn- apply-grid-focus!
  "Set tabindex=0 on the focused day cell (from k-grid-focus), -1 on all others."
  [^js el]
  (let [iso  (gobj/get el k-grid-focus)
        refs (gobj/get el k-refs)
        ^js grid (when refs (gobj/get refs "grid"))]
    (when (and grid iso)
      (when-let [^js btn (.querySelector grid (str "[data-iso=\"" iso "\"]"))]
        (.setAttribute btn "tabindex" "0")))))

(defn- focus-grid-date!
  "Set the focused date and move DOM focus to that cell."
  [^js el iso]
  (gobj/set el k-grid-focus iso)
  (let [refs (gobj/get el k-refs)
        ^js grid (when refs (gobj/get refs "grid"))]
    (when grid
      ;; Remove previous tabindex=0
      (when-let [^js prev (.querySelector grid "[tabindex=\"0\"]")]
        (.setAttribute prev "tabindex" "-1"))
      (when-let [^js btn (.querySelector grid (str "[data-iso=\"" iso "\"]"))]
        (.setAttribute btn "tabindex" "0")
        (.focus btn)))))

;; ---------------------------------------------------------------------------
;; Weekday header
;; ---------------------------------------------------------------------------

(defn- render-weekdays!
  [^js el ^js weekdays-el]
  (when-not (gobj/get el k-wd-done)
    (gobj/set el k-wd-done true)
    (let [labels #js ["Su" "Mo" "Tu" "We" "Th" "Fr" "Sa"]]
      (dotimes [i 7]
        (let [^js div (make-el "div")]
          (.setAttribute div "part" "weekday")
          (.setAttribute div "aria-hidden" "true")
          (set! (.-textContent div) (aget labels i))
          (.appendChild weekdays-el div))))))

;; ---------------------------------------------------------------------------
;; Calendar render
;; ---------------------------------------------------------------------------

(defn- month-name
  [^js month-date locale]
  (let [opts #js {:year "numeric" :month "long" :timeZone "UTC"}
        loc  (or locale "default")]
    (.format (js/Intl.DateTimeFormat. loc opts) month-date)))

(defn- render-calendar!
  [^js el]
  (let [refs      (gobj/get el k-refs)]
    (when refs
      (let [^js month-label (gobj/get refs "month-label")
            ^js grid        (gobj/get refs "grid")
            ^js weekdays-el (gobj/get refs "weekdays")
            state           (gobj/get el k-state)
            canon           (when state (gobj/get state "canon"))
            ^js month       (when state (gobj/get state "month"))
            cfg             (when state (gobj/get state "cfg"))
            locale          (when cfg (:locale cfg))]
        (when month-label
          (set! (.-textContent month-label) (month-name month locale)))
        (render-weekdays! el weekdays-el)
        (when (and grid canon)
          (render-grid! el grid canon)
          (apply-grid-focus! el))))))

;; ---------------------------------------------------------------------------
;; Input display
;; ---------------------------------------------------------------------------

(defn- sync-input-display!
  [^js el]
  (let [refs    (gobj/get el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        state   (gobj/get el k-state)
        disp    (when state (gobj/get state "disp"))
        focused (gobj/get el k-focused)]
    (when (and inp (not focused))
      (set! (.-value inp) (or disp "")))))

;; ---------------------------------------------------------------------------
;; Full render
;; ---------------------------------------------------------------------------

(defn- render!
  [^js el]
  (let [refs (gobj/get el k-refs)]
    (when refs
      (let [^js inp     (gobj/get refs "input")
            ^js btn     (gobj/get refs "btn")
            ^js sr      (gobj/get refs "sr")
            disabled?   (.hasAttribute el model/attr-disabled)
            readonly?   (.hasAttribute el model/attr-readonly)
            placeholder (.getAttribute el model/attr-placeholder)
            aria-label  (.getAttribute el model/attr-aria-label)
            aria-desc   (.getAttribute el model/attr-aria-describedby)
            open?       (.hasAttribute el "open")]

        (when btn
          (if disabled?
            (.setAttribute btn "disabled" "")
            (.removeAttribute btn "disabled")))

        (when (and disabled? open?)
          (.removeAttribute el "open"))

        (when inp
          (.setAttribute inp "aria-expanded" (if open? "true" "false"))
          (if disabled?
            (do (.setAttribute inp "disabled" "")
                (.setAttribute inp "aria-disabled" "true"))
            (do (.removeAttribute inp "disabled")
                (.removeAttribute inp "aria-disabled")))
          (when readonly?  (.setAttribute inp "readonly" ""))
          (when-not readonly? (.removeAttribute inp "readonly"))
          (when placeholder (.setAttribute inp "placeholder" placeholder))
          (when aria-label  (.setAttribute inp "aria-label" aria-label))
          (when aria-desc   (.setAttribute inp "aria-describedby" aria-desc)))

        (sync-input-display! el)
        (render-calendar! el)))))

;; ---------------------------------------------------------------------------
;; Date selection
;; ---------------------------------------------------------------------------

(defn- set-single-value!
  [^js el ^js d]
  (if d
    (.setAttribute el model/attr-value (model/date->iso d))
    (.removeAttribute el model/attr-value)))

(defn- do-select-date!
  "Apply date selection. For single: set value. For range: smart first/second click."
  [^js el ^js d reason]
  (when-not (.hasAttribute el model/attr-readonly)
    (let [state  (gobj/get el k-state)
          canon  (when state (gobj/get state "canon"))
          mode   (when canon (:mode canon))
          mode-s (if (= mode :range) "range" "single")
          req-detail (if (= mode :single)
                       #js {:value (model/date->iso d) :mode mode-s :reason reason}
                       #js {:date (model/date->iso d) :mode mode-s :reason reason})
          ^js ev (dispatch! el model/event-change-request true req-detail)]
      (when-not (.-defaultPrevented ev)
        (if (= mode :single)
          (let [iso (model/date->iso d)]
            (set-single-value! el d)
            (read-state! el)
            (render! el)
            (dispatch! el model/event-change false
                       #js {:value iso :mode mode-s :reason reason})
            ;; Auto-close if close-on-select
            (when (.hasAttribute el model/attr-close-on-select)
              (.removeAttribute el "open")
              (render! el)))
          ;; Range mode
          (let [step (or (gobj/get el k-range-step) 0)
                cur-start (:start-d canon)
                cur-end   (:end-d canon)]
            (cond
              ;; Step 0: no selection yet — set start
              (= step 0)
              (do
                (.setAttribute el model/attr-start (model/date->iso d))
                (.removeAttribute el model/attr-end)
                (gobj/set el k-range-step 1))

              ;; Step 1: start selected, pick end
              (= step 1)
              (let [allow-same? (:allow-same-day? canon)
                    cmp         (model/compare-date d cur-start)
                    valid?      (or (pos? cmp)
                                    (and allow-same? (zero? cmp)))]
                (if valid?
                  (let [final-start (if (:auto-swap? canon)
                                     (model/min-date cur-start d)
                                     cur-start)
                        final-end   (if (:auto-swap? canon)
                                     (model/max-date cur-start d)
                                     d)]
                    (.setAttribute el model/attr-start (model/date->iso final-start))
                    (.setAttribute el model/attr-end (model/date->iso final-end))
                    (gobj/set el k-range-step 0)
                    (when (and (.hasAttribute el model/attr-close-on-select)
                               (some? final-start) (some? final-end))
                      (.removeAttribute el "open")))
                  ;; Restart: treat as new start
                  (do
                    (.setAttribute el model/attr-start (model/date->iso d))
                    (.removeAttribute el model/attr-end)
                    (gobj/set el k-range-step 1))))

              ;; Step 2+: reset and start fresh
              :else
              (do
                (.setAttribute el model/attr-start (model/date->iso d))
                (.removeAttribute el model/attr-end)
                (gobj/set el k-range-step 1)))
            (read-state! el)
            (render! el)
            (let [new-state (gobj/get el k-state)
                  new-canon (when new-state (gobj/get new-state "canon"))
                  s-iso     (when (:start-d new-canon) (model/date->iso (:start-d new-canon)))
                  e-iso     (when (:end-d new-canon) (model/date->iso (:end-d new-canon)))
                  chg-detail (cond-> #js {:mode mode-s :reason reason}
                               s-iso (doto (gobj/set "start" s-iso))
                               e-iso (doto (gobj/set "end" e-iso)))]
              (dispatch! el model/event-change false chg-detail))))))))

;; ---------------------------------------------------------------------------
;; Input commit
;; ---------------------------------------------------------------------------

(defn- commit-display!
  [^js el reason]
  (let [refs    (gobj/get el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        val     (when inp (.-value inp))
        state   (gobj/get el k-state)
        canon   (when state (gobj/get state "canon"))
        mode    (:mode canon)
        mode-s  (if (= mode :range) "range" "single")]
    (dispatch! el model/event-input false #js {:value val :mode mode-s})
    (if (= mode :single)
      (let [{:keys [ok? date]} (model/parse-display->single val)]
        (when ok?
          (let [iso    (model/date->iso date)
                ^js ev (dispatch! el model/event-change-request true
                                  #js {:value iso :mode mode-s :reason reason})]
            (when-not (.-defaultPrevented ev)
              (set-single-value! el date)
              (read-state! el)
              (render! el)
              (dispatch! el model/event-change false
                         #js {:value iso :mode mode-s :reason reason})))))
      (let [{:keys [ok? start end]} (model/parse-display->range val {:separator (:separator canon)})]
        (when ok?
          (let [s-iso  (model/date->iso start)
                e-iso  (model/date->iso end)
                ^js ev (dispatch! el model/event-change-request true
                                  #js {:start s-iso :end e-iso
                                       :mode mode-s :reason reason})]
            (when-not (.-defaultPrevented ev)
              (.setAttribute el model/attr-start s-iso)
              (.setAttribute el model/attr-end   e-iso)
              (gobj/set el k-range-step 0)
              (read-state! el)
              (render! el)
              (dispatch! el model/event-change false
                         #js {:start s-iso :end e-iso
                              :mode mode-s :reason reason}))))))))

;; ---------------------------------------------------------------------------
;; Month navigation
;; ---------------------------------------------------------------------------

(defn- navigate-month!
  [^js el direction]
  (let [state   (gobj/get el k-state)
        ^js cur (when state (gobj/get state "month"))]
    (when cur
      (let [y    (.getUTCFullYear cur)
            mo   (.getUTCMonth cur)
            next-mo (if (= direction :next)
                      (js/Date. (js/Date.UTC y (inc mo) 1))
                      (js/Date. (js/Date.UTC y (dec mo) 1)))]
        (gobj/set state "month" next-mo)
        (render-calendar! el)))))

;; ---------------------------------------------------------------------------
;; Popover open/close
;; ---------------------------------------------------------------------------

(defn- open-popover!
  [^js el]
  (when-not (.hasAttribute el "open")
    (.setAttribute el "open" "")
    ;; attributeChangedCallback already triggers render; set grid focus
    (let [state  (gobj/get el k-state)
          canon  (when state (gobj/get state "canon"))
          anchor (or (:value-d canon) (:start-d canon) (js/Date. (js/Date.now)))
          iso    (model/date->iso anchor)]
      (focus-grid-date! el iso))))

(defn- close-popover!
  [^js el]
  (when (.hasAttribute el "open")
    (.removeAttribute el "open")))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------

(def ^:private style-text
  (str
   ":host{"
   "display:block;color-scheme:light dark;"
   "--x-date-picker-border:var(--x-color-border,#cbd5e1);"
   "--x-date-picker-radius:var(--x-radius-md,8px);"
   "--x-date-picker-input-bg:var(--x-color-surface,#fff);"
   "--x-date-picker-text:inherit;"
   "--x-date-picker-focus:var(--x-color-focus-ring,#60a5fa);"
   "--x-date-picker-placeholder:var(--x-color-text-muted,#94a3b8);"
   "--x-date-picker-btn-bg:var(--x-color-surface-hover,#f8fafc);"
   "--x-date-picker-btn-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-date-picker-popover-bg:var(--x-color-surface,#fff);"
   "--x-date-picker-popover-radius:var(--x-radius-lg,12px);"
   "--x-date-picker-nav-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-date-picker-weekday:var(--x-color-text-muted,#94a3b8);"
   "--x-date-picker-day-text:inherit;"
   "--x-date-picker-day-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-date-picker-selected-bg:var(--x-color-primary,#2563eb);"
   "--x-date-picker-selected-text:#fff;"
   "--x-date-picker-range-bg:#dbeafe;"
   "--x-date-picker-range-text:var(--x-color-primary-active,#1e40af);"
   "}"
   "[part=container]{position:relative;display:flex;align-items:stretch;gap:8px;width:100%;}"
   "[part=input]{"
   "flex:1;min-width:0;appearance:none;box-sizing:border-box;"
   "border:1px solid var(--x-date-picker-border,#cbd5e1);"
   "border-radius:var(--x-date-picker-radius,8px);"
   "padding:8px 12px;font-size:0.9375rem;"
   "background:var(--x-date-picker-input-bg,#fff);"
   "color:var(--x-date-picker-text,inherit);"
   "outline:none;"
   "}"
   "[part=input]:focus{border-color:var(--x-date-picker-focus,#60a5fa);box-shadow:0 0 0 3px rgba(96,165,250,0.25);}"
   "[part=input][disabled]{opacity:0.5;cursor:default;}"
   "[part=btn][disabled]{opacity:0.5;cursor:default;pointer-events:none;}"
   "[part=input]::placeholder{color:var(--x-date-picker-placeholder,#94a3b8);}"
   "[part=btn]{"
   "all:unset;cursor:pointer;flex:0 0 auto;width:42px;height:42px;"
   "display:inline-flex;align-items:center;justify-content:center;"
   "border:1px solid var(--x-date-picker-border,#cbd5e1);"
   "border-radius:var(--x-date-picker-radius,8px);"
   "background:var(--x-date-picker-btn-bg,#f8fafc);"
   "color:var(--x-date-picker-text,inherit);"
   "box-sizing:border-box;"
   "}"
   "[part=btn]:hover{background:var(--x-date-picker-btn-hover,#f1f5f9);}"
   "[part=btn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=popover]{"
   "display:none;position:absolute;z-index:1000;"
   "top:calc(100% + 8px);left:0;"
   "background:var(--x-date-picker-popover-bg,#fff);"
   "border:1px solid var(--x-date-picker-border,#e2e8f0);"
   "border-radius:var(--x-date-picker-popover-radius,12px);"
   "box-shadow:0 8px 32px rgba(0,0,0,0.12),0 2px 8px rgba(0,0,0,0.06);"
   "padding:16px;"
   "width:var(--x-date-picker-popover-width,304px);"
   "max-width:calc(100vw - 1rem);"
   "box-sizing:border-box;"
   "}"
   ":host([open]) [part=popover]{display:block;}"
   "[part=nav]{"
   "display:flex;align-items:center;justify-content:space-between;"
   "margin-bottom:12px;"
   "}"
   "[part=navbtn]{"
   "all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"
   "width:32px;height:32px;border-radius:6px;"
   "color:var(--x-date-picker-text,inherit);"
   "}"
   "[part=navbtn]:hover{background:var(--x-date-picker-nav-hover,#f1f5f9);}"
   "[part=navbtn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=monthlabel]{font-weight:600;font-size:0.9375rem;}"
   "[part=weekdays]{"
   "display:grid;grid-template-columns:repeat(7,1fr);gap:4px;margin-bottom:4px;"
   "}"
   "[part=weekday]{"
   "text-align:center;font-size:0.75rem;font-weight:600;"
   "color:var(--x-date-picker-weekday,#94a3b8);padding:4px 0;"
   "}"
   "[part=grid]{display:grid;grid-template-columns:repeat(7,1fr);gap:4px;}"
   "[part=day]{"
   "all:unset;cursor:pointer;box-sizing:border-box;"
   "display:flex;align-items:center;justify-content:center;"
   "height:36px;border-radius:6px;"
   "font-size:0.875rem;"
   "color:var(--x-date-picker-day-text,inherit);"
   "}"
   "[part=day]:hover:not([disabled]){background:var(--x-date-picker-day-hover,#f1f5f9);}"
   "[part=day]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=day][data-outside=true]{opacity:0.4;}"
   "[part=day][data-disabled=true]{opacity:0.3;cursor:default;pointer-events:none;}"
   "[part=day][data-selected=true]{"
   "background:var(--x-date-picker-selected-bg,#2563eb);"
   "color:var(--x-date-picker-selected-text,#fff);"
   "font-weight:600;"
   "}"
   "[part=day][data-in-range=true]:not([data-range-edge=true]){"
   "background:var(--x-date-picker-range-bg,#dbeafe);"
   "color:var(--x-date-picker-range-text,#1e40af);"
   "border-radius:0;"
   "}"
   "[part=day][data-range-edge=true]{"
   "background:var(--x-date-picker-selected-bg,#2563eb);"
   "color:var(--x-date-picker-selected-text,#fff);"
   "font-weight:600;"
   "}"
   "[part=sr-status]{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-date-picker-border:var(--x-color-border,#334155);"
   "--x-date-picker-input-bg:var(--x-color-surface,#0f172a);"
   "--x-date-picker-btn-bg:var(--x-color-surface-hover,#1e293b);"
   "--x-date-picker-btn-hover:var(--x-color-surface-active,#334155);"
   "--x-date-picker-popover-bg:var(--x-color-surface-hover,#1e293b);"
   "--x-date-picker-day-hover:var(--x-color-surface-active,#334155);"
   "--x-date-picker-selected-bg:var(--x-color-primary,#3b82f6);"
   "--x-date-picker-range-bg:#1e3a8a;"
   "--x-date-picker-range-text:var(--x-color-focus-ring,#93c5fd);"
   "--x-date-picker-nav-hover:var(--x-color-surface-active,#334155);"
   "}"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=popover]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------

(defn- make-shadow!
  [^js el]
  (let [^js root      (.attachShadow el #js {:mode "open"})
        ^js style     (make-el "style")
        ^js container (make-el "div")
        ^js inp       (make-el "input")
        ^js btn       (make-el "button")
        ^js popover   (make-el "div")
        ^js nav       (make-el "div")
        ^js nav-prev  (make-el "button")
        ^js month-lbl (make-el "div")
        ^js nav-next  (make-el "button")
        ^js weekdays  (make-el "div")
        ^js grid      (make-el "div")
        ^js sr        (make-el "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute container "part" "container")

    (.setAttribute inp "part" "input")
    (.setAttribute inp "type" "text")
    (.setAttribute inp "inputmode" "none")
    (.setAttribute inp "autocomplete" "off")
    (.setAttribute inp "role" "combobox")
    (.setAttribute inp "aria-haspopup" "dialog")
    (.setAttribute inp "aria-expanded" "false")

    (.setAttribute btn "part" "btn")
    (.setAttribute btn "type" "button")
    (.setAttribute btn "aria-label" "Open calendar")
    (set! (.-textContent btn) "\uD83D\uDCC5")

    (.setAttribute popover "part" "popover")
    (.setAttribute popover "role" "dialog")
    (.setAttribute popover "aria-modal" "true")
    (.setAttribute popover "aria-label" "Calendar")

    (.setAttribute nav "part" "nav")

    (.setAttribute nav-prev "part" "navbtn")
    (.setAttribute nav-prev "data-nav" "prev")
    (.setAttribute nav-prev "type" "button")
    (.setAttribute nav-prev "aria-label" "Previous month")
    (set! (.-textContent nav-prev) "\u2039")

    (.setAttribute month-lbl "part" "monthlabel")
    (.setAttribute month-lbl "aria-live" "polite")

    (.setAttribute nav-next "part" "navbtn")
    (.setAttribute nav-next "data-nav" "next")
    (.setAttribute nav-next "type" "button")
    (.setAttribute nav-next "aria-label" "Next month")
    (set! (.-textContent nav-next) "\u203A")

    (.setAttribute weekdays "part" "weekdays")
    (.setAttribute weekdays "aria-hidden" "true")

    (.setAttribute grid "part" "grid")
    (.setAttribute grid "role" "grid")
    (.setAttribute grid "aria-label" "Calendar grid")

    (.setAttribute sr "part" "sr-status")
    (.setAttribute sr "aria-live" "polite")
    (.setAttribute sr "aria-atomic" "true")

    (.appendChild nav nav-prev)
    (.appendChild nav month-lbl)
    (.appendChild nav nav-next)

    (.appendChild popover nav)
    (.appendChild popover weekdays)
    (.appendChild popover grid)
    (.appendChild popover sr)

    (.appendChild container inp)
    (.appendChild container btn)
    (.appendChild container popover)

    (.appendChild root style)
    (.appendChild root container)

    (let [refs #js {:input      inp
                    :btn        btn
                    :popover    popover
                    :nav-prev   nav-prev
                    :month-label month-lbl
                    :nav-next   nav-next
                    :weekdays   weekdays
                    :grid       grid
                    :sr         sr}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Event listeners
;; ---------------------------------------------------------------------------

(defn- on-input-event!
  [^js el ^js _e]
  (let [refs    (gobj/get el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        state   (gobj/get el k-state)
        canon   (when state (gobj/get state "canon"))
        mode-s  (if (= (:mode canon) :range) "range" "single")]
    (gobj/set el k-display (when inp (.-value inp)))
    (dispatch! el model/event-input false
               #js {:value (when inp (.-value inp)) :mode mode-s})))

(defn- on-input-keydown!
  [^js el ^js e]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [key (.-key e)]
      (cond
        (= key "Enter")
        (do (.preventDefault e)
            (commit-display! el "keyboard"))

        (= key "Escape")
        (close-popover! el)))))

(defn- on-input-focus!
  [^js el ^js _e]
  (gobj/set el k-focused true))

(defn- on-input-blur!
  [^js el ^js _e]
  (gobj/set el k-focused false)
  (when-not (.hasAttribute el model/attr-disabled)
    (commit-display! el "blur"))
  (sync-input-display! el))

(defn- on-btn-click!
  [^js el ^js _e]
  (when-not (.hasAttribute el model/attr-disabled)
    (if (.hasAttribute el "open")
      (close-popover! el)
      (open-popover! el))))

(defn- on-nav-click!
  [^js el ^js e]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [^js target (.-currentTarget e)
          dir        (.getAttribute target "data-nav")]
      (if (= dir "prev")
        (navigate-month! el :prev)
        (navigate-month! el :next)))))

(defn- on-grid-click!
  [^js el ^js e]
  (when-not (or (.hasAttribute el model/attr-disabled)
                (.hasAttribute el model/attr-readonly))
    (let [^js target (.-target e)
          ^js btn    (.closest target "button[data-iso]")]
      (when (and btn (not= "true" (.getAttribute btn "data-disabled")))
        (let [iso (. btn getAttribute "data-iso")
              ^js d (model/iso->date iso)]
          (when d (do-select-date! el d "click")))))))

(defn- on-grid-keydown!
  [^js el ^js e]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [key     (.-key e)
          cur-iso (gobj/get el k-grid-focus)
          ^js cur (when cur-iso (model/iso->date cur-iso))
          state   (gobj/get el k-state)
          canon   (when state (gobj/get state "canon"))
          ^js month (when state (gobj/get state "month"))
          nav-date
          (when cur
            (case key
              "ArrowLeft"  (model/add-days cur -1)
              "ArrowRight" (model/add-days cur 1)
              "ArrowUp"    (model/add-days cur -7)
              "ArrowDown"  (model/add-days cur 7)
              "PageUp"     (model/add-months cur -1)
              "PageDown"   (model/add-months cur 1)
              "Home"       (model/start-of-week cur)
              "End"        (model/end-of-week cur)
              nil))]
      (cond
        ;; Arrow / page / home / end navigation
        (some? nav-date)
        (do
          (.preventDefault e)
          (let [clamped (model/clamp-to-range nav-date (:min-d canon) (:max-d canon))
                nav-iso (model/date->iso clamped)]
            ;; If navigated outside current month, shift month view
            (when (and month (not= (.getUTCMonth clamped) (.getUTCMonth month)))
              (gobj/set state "month" (model/start-of-month clamped))
              (render-calendar! el))
            (focus-grid-date! el nav-iso)))

        ;; Enter / Space selects the focused date
        (and (or (= key "Enter") (= key " "))
             cur
             (not (.hasAttribute el model/attr-readonly)))
        (do
          (.preventDefault e)
          (when-not (day-out-of-range? cur canon)
            (do-select-date! el cur "keyboard")))

        ;; Escape closes the calendar
        (= key "Escape")
        (do
          (.preventDefault e)
          (close-popover! el)
          ;; Return focus to input
          (let [refs (gobj/get el k-refs)
                ^js inp (when refs (gobj/get refs "input"))]
            (when inp (.focus inp))))))))

(defn- on-popover-mousedown!
  [^js _el ^js e]
  ;; Prevent input blur when clicking inside popover
  (.preventDefault e))

(defn- add-listeners!
  [^js el]
  (let [refs      (gobj/get el k-refs)
        ^js inp   (gobj/get refs "input")
        ^js btn   (gobj/get refs "btn")
        ^js prev  (gobj/get refs "nav-prev")
        ^js next  (gobj/get refs "nav-next")
        ^js grid  (gobj/get refs "grid")
        ^js pop   (gobj/get refs "popover")

        h-input    (fn [^js e] (on-input-event! el e))
        h-keydown  (fn [^js e] (on-input-keydown! el e))
        h-focus    (fn [^js e] (on-input-focus! el e))
        h-blur     (fn [^js e] (on-input-blur! el e))
        h-btn      (fn [^js e] (on-btn-click! el e))
        h-prev     (fn [^js e] (on-nav-click! el e))
        h-next     (fn [^js e] (on-nav-click! el e))
        h-grid     (fn [^js e] (on-grid-click! el e))
        h-grid-kd  (fn [^js e] (on-grid-keydown! el e))
        h-pop-md   (fn [^js e] (on-popover-mousedown! el e))
        h-doc-click (fn [^js e]
                      (when (.hasAttribute el "open")
                        (when-not (.some (.composedPath e)
                                         (fn [^js n] (identical? n el)))
                          (close-popover! el))))]

    (.addEventListener inp "input"    h-input)
    (.addEventListener inp "keydown"  h-keydown)
    (.addEventListener inp "focus"    h-focus)
    (.addEventListener inp "blur"     h-blur)
    (.addEventListener btn "click"    h-btn)
    (.addEventListener prev "click"   h-prev)
    (.addEventListener next "click"   h-next)
    (.addEventListener grid "click"   h-grid)
    (.addEventListener grid "keydown" h-grid-kd)
    (.addEventListener pop "pointerdown" h-pop-md)
    (.addEventListener js/document "pointerdown" h-doc-click true)

    (gobj/set el k-handlers
              #js {:input     h-input
                   :keydown   h-keydown
                   :focus     h-focus
                   :blur      h-blur
                   :btn       h-btn
                   :prev      h-prev
                   :next      h-next
                   :grid      h-grid
                   :grid-kd   h-grid-kd
                   :pop-md    h-pop-md
                   :doc-click h-doc-click})))

(defn- remove-listeners!
  [^js el]
  (let [refs     (gobj/get el k-refs)
        handlers (gobj/get el k-handlers)]
    (when (and refs handlers)
      (let [^js inp  (gobj/get refs "input")
            ^js btn  (gobj/get refs "btn")
            ^js prev (gobj/get refs "nav-prev")
            ^js next (gobj/get refs "nav-next")
            ^js grid (gobj/get refs "grid")
            ^js pop  (gobj/get refs "popover")]
        (.removeEventListener inp  "input"     (gobj/get handlers "input"))
        (.removeEventListener inp  "keydown"   (gobj/get handlers "keydown"))
        (.removeEventListener inp  "focus"     (gobj/get handlers "focus"))
        (.removeEventListener inp  "blur"      (gobj/get handlers "blur"))
        (.removeEventListener btn  "click"     (gobj/get handlers "btn"))
        (.removeEventListener prev "click"     (gobj/get handlers "prev"))
        (.removeEventListener next "click"     (gobj/get handlers "next"))
        (.removeEventListener grid "click"     (gobj/get handlers "grid"))
        (.removeEventListener grid "keydown"  (gobj/get handlers "grid-kd"))
        (.removeEventListener pop  "pointerdown" (gobj/get handlers "pop-md"))
        (.removeEventListener js/document "pointerdown" (gobj/get handlers "doc-click") true)))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------

(defn- connected!
  [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (read-state! el)
  (render! el))

(defn- disconnected!
  [^js el]
  (remove-listeners! el))

(defn- attribute-changed!
  [^js el _n _o _v]
  (read-state! el)
  (render! el))

;; ---------------------------------------------------------------------------
;; Property definitions
;; ---------------------------------------------------------------------------

(defn- define-string-prop!
  [^js proto prop-name attr-name]
  (.defineProperty js/Object proto prop-name
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr-name) nil)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= v ""))
                                          (.setAttribute this attr-name v)
                                          (.removeAttribute this attr-name))))}))

(defn- define-bool-prop!
  [^js proto prop-name attr-name]
  (.defineProperty js/Object proto prop-name
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                        (.hasAttribute this attr-name)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr-name "")
                                          (.removeAttribute this attr-name))))}))

(defn- define-methods!
  [^js proto]
  (aset proto "focus"
        (fn []
          (this-as ^js this
                   (let [refs (gobj/get this k-refs)
                         ^js inp (when refs (gobj/get refs "input"))]
                     (when inp (.focus inp))))))
  (aset proto "commit"
        (fn []
          (this-as ^js this
                   (commit-display! this "programmatic"))))
  (aset proto "clear"
        (fn []
          (this-as ^js this
                   (let [state (gobj/get this k-state)
                         canon (when state (gobj/get state "canon"))]
                     (if (= (:mode canon) :single)
                       (.removeAttribute this model/attr-value)
                       (do (.removeAttribute this model/attr-start)
                           (.removeAttribute this model/attr-end)))
                     (read-state! this)
                     (render! this))))))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(defn- element-class
  []
  (let [klass (js* "(class extends HTMLElement {})")
        proto (.-prototype klass)]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback proto)
          (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback proto)
          (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback proto)
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (define-string-prop! proto "mode"      model/attr-mode)
    (define-string-prop! proto "value"     model/attr-value)
    (define-string-prop! proto "start"     model/attr-start)
    (define-string-prop! proto "end"       model/attr-end)
    (define-bool-prop!   proto "open"      "open")
    (define-bool-prop!   proto "disabled"  model/attr-disabled)
    (define-bool-prop!   proto "readOnly"  model/attr-readonly)
    (define-bool-prop!   proto "required"  model/attr-required)
    (define-methods! proto)

    klass))

(defn init!
  []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
