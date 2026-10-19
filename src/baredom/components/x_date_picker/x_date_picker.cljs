(ns baredom.components.x-date-picker.x-date-picker
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.utils.dates :as dates]
            [baredom.utils.forms :as forms]
            [baremirror.alpha.places :as places]
            [goog.object :as gobj]
            [baredom.components.x-date-picker.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance-field keys (gobj-safe)
;; ---------------------------------------------------------------------------

(def ^:private k-refs     "__xDatePickerRefs")
(def ^:private k-state    "__xDatePickerState")
(def ^:private k-model    "__xDatePickerModel")
(def ^:private k-focused  "__xDatePickerFocused")
(def ^:private k-written  "__xDatePickerWritten")
(def ^:private k-wd-done  "__xDatePickerWeekdaysDone")
(def ^:private k-handlers "__xDatePickerHandlers")
(def ^:private k-grid-focus "__xDatePickerGridFocus")
(def ^:private k-month      "__xDatePickerMonth")
(def ^:private k-internals  "__xDatePickerInternals")

(def ^:private msg-value-missing "Please enter a date.")

;; ---------------------------------------------------------------------------
;; Read state from element attrs
;; ---------------------------------------------------------------------------

(defn- read-state!
  "Read all observed attrs, canonicalize, cache result on element."
  [^js el]
  (let [mode-raw  (du/get-attr el model/attr-mode)
        canon     (model/canonicalize
                   {:mode       mode-raw
                    :value      (du/get-attr el model/attr-value)
                    :start      (du/get-attr el model/attr-start)
                    :end        (du/get-attr el model/attr-end)
                    :min        (du/get-attr el model/attr-min)
                    :max        (du/get-attr el model/attr-max)
                    :format     (du/get-attr el model/attr-format)
                    :locale     (du/get-attr el model/attr-locale)
                    :separator  (du/get-attr el model/attr-separator)
                    :auto-swap? (du/has-attr? el model/attr-auto-swap)
                    :range-allow-same-day?
                    (du/has-attr? el model/attr-range-allow-same)})
        cfg       {:format (:format canon) :locale (:locale canon)}
        disp      (model/display-value canon cfg)
        existing  (du/getv el k-state)
        month     (model/shown-month (when existing (gobj/get existing "canon"))
                                     canon
                                     (du/getv el k-month)
                                     (js/Date.))
        state     #js {:canon  canon
                       :cfg    cfg
                       :disp   disp}]
    (when (not= month (du/getv el k-month))
      (du/setv! el k-month month))
    (du/setv! el k-state state)
    state))

;; ---------------------------------------------------------------------------
;; Grid rendering
;; ---------------------------------------------------------------------------

(defn- make-day-cell!
  "The node of a day cell: its fixed structure. `apply-day-cell!` gives it its values."
  [_key]
  (let [^js btn (.createElement js/document "button")]
    (du/set-attr! btn "part" "day")
    (du/set-attr! btn "type" "button")
    (du/set-attr! btn "role" "gridcell")
    btn))

(def ^:private day-attrs
  "The attributes of a day cell, each with the key of what the model's day shows for it."
  [["data-iso"        :iso]
   ["data-outside"    :outside]
   ["data-disabled"   :disabled]
   ["data-selected"   :selected]
   ["data-in-range"   :in-range]
   ["data-range-edge" :range-edge]
   ["aria-selected"   :selected]
   ["aria-disabled"   :disabled]
   ["tabindex"        :tabindex]])

(defn- apply-day-attr! [^js btn day [attr-name shown-key]]
  (du/set-attr-to! btn attr-name (get day shown-key)))

(defn- apply-day-cell!
  "Writes what `day` shows onto its node in `nodes`."
  [nodes {day-key :key :keys [text] :as day}]
  (let [^js btn (nodes day-key)]
    (du/set-text-to! btn text)
    (run! (partial apply-day-attr! btn day) day-attrs)))

(defn- render-grid!
  "Brings the day grid to the month shown. A day that stays in view keeps its node."
  [^js el ^js grid canon]
  (let [^js month (du/getv el k-month)
        days      (model/shown-days month canon (du/getv el k-grid-focus))
        nodes     (places/sync! {:days {:parent grid}} {:days (mapv :key days)} make-day-cell!)]
    (run! (partial apply-day-cell! nodes) days)))

(defn- focus-grid-date!
  "Set the focused date and move DOM focus to that cell."
  [^js el iso]
  (du/setv! el k-grid-focus iso)
  (let [refs (du/getv el k-refs)
        ^js grid (when refs (gobj/get refs "grid"))]
    (when grid
      ;; Remove previous tabindex=0
      (when-let [^js prev (.querySelector grid "[tabindex=\"0\"]")]
        (du/set-attr! prev "tabindex" "-1"))
      (when-let [^js btn (.querySelector grid (str "[data-iso=\"" iso "\"]"))]
        (du/set-attr! btn "tabindex" "0")
        (.focus btn)))))

;; ---------------------------------------------------------------------------
;; Weekday header
;; ---------------------------------------------------------------------------

(defn- render-weekdays!
  [^js el ^js weekdays-el]
  (when-not (du/getv el k-wd-done)
    (du/setv! el k-wd-done true)
    (let [labels #js ["Su" "Mo" "Tu" "We" "Th" "Fr" "Sa"]]
      (dotimes [i 7]
        (let [^js div (.createElement js/document "div")]
          (du/set-attr! div "part" "weekday")
          (du/set-attr! div "aria-hidden" "true")
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
  (let [refs      (du/getv el k-refs)]
    (when refs
      (let [^js month-label (gobj/get refs "month-label")
            ^js grid        (gobj/get refs "grid")
            ^js weekdays-el (gobj/get refs "weekdays")
            state           (du/getv el k-state)
            canon           (when state (gobj/get state "canon"))
            ^js month       (du/getv el k-month)
            cfg             (when state (gobj/get state "cfg"))
            locale          (when cfg (:locale cfg))]
        (when month-label
          (set! (.-textContent month-label) (month-name month locale)))
        (render-weekdays! el weekdays-el)
        (when (and grid canon)
          (render-grid! el grid canon))))))

;; ---------------------------------------------------------------------------
;; Input display
;; ---------------------------------------------------------------------------

(defn- sync-input-display!
  "Writes the shown text into the input when `model/write-display?` allows. Typed text
  survives a render that does not change the shown text."
  [^js el]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        state   (du/getv el k-state)
        text    (or (when state (gobj/get state "disp")) "")]
    (when (and inp (model/write-display? (du/getv el k-focused) text (du/getv el k-written)))
      (set! (.-value inp) text)
      (du/setv! el k-written text))))

(defn- input-edited?
  "True when the input holds text other than the text last written."
  [^js el]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs "input"))]
    (boolean (and inp (not= (.-value inp) (du/getv el k-written))))))

;; ---------------------------------------------------------------------------
;; Full render
;; ---------------------------------------------------------------------------

(defn- apply-error!
  "Render the inline error message and invalid markers from the `error` attr,
  merging any author-supplied aria-describedby with the inline error id."
  [^js el ^js inp]
  (when-let [refs (du/getv el k-refs)]
    (let [^js err    (gobj/get refs "error")
          error      (du/get-attr el model/attr-error)
          has-error? (and (string? error) (not= error ""))]
      (forms/apply-error-display!
       el inp err {:error error :has-error? has-error?}
       (forms/error-describedby has-error? (du/get-attr el model/attr-aria-describedby))))))

;; ---------------------------------------------------------------------------
;; Form association — ElementInternals value + constraint validation
;; ---------------------------------------------------------------------------
(defn- form-value-of
  "The string submitted with the form: the ISO value for single mode, or
  \"start/end\" once a range is complete (empty until then)."
  [canon]
  (if (= (:mode canon) :single)
    (or (:value-str canon) "")
    (if (:complete? canon)
      (str (:start-str canon) "/" (:end-str canon))
      "")))

(defn- sync-form-state!
  "Push the current value into the form and refresh validity via the shared
  policy. No-op when the element is not form-associated (older browsers)."
  [^js el]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        state   (du/getv el k-state)
        canon   (when state (gobj/get state "canon"))
        fval    (if canon (form-value-of canon) "")]
    (forms/sync! (du/getv el k-internals) inp fval
                 {:has-error?      (du/has-attr? el model/attr-error)
                  :error           (du/get-attr el model/attr-error)
                  :required?       (du/has-attr? el model/attr-required)
                  :empty?          (= fval "")
                  :missing-message msg-value-missing})))

(defn- render!
  [^js el]
  (let [refs (du/getv el k-refs)]
    (when refs
      (let [^js inp     (gobj/get refs "input")
            ^js btn     (gobj/get refs "btn")
            ^js _sr     (gobj/get refs "sr")
            disabled?   (forms/disabled? el)
            readonly?   (du/has-attr? el model/attr-readonly)
            placeholder (du/get-attr el model/attr-placeholder)
            aria-label  (du/get-attr el model/attr-aria-label)
            open?       (du/has-attr? el "open")]

        (when btn
          (if disabled?
            (du/set-attr! btn "disabled" "")
            (du/remove-attr! btn "disabled")))

        (when inp
          (du/set-attr! inp "aria-expanded" (if open? "true" "false"))
          (if disabled?
            (do (du/set-attr! inp "disabled" "")
                (du/set-attr! inp "aria-disabled" "true"))
            (do (du/remove-attr! inp "disabled")
                (du/remove-attr! inp "aria-disabled")))
          (when readonly?  (du/set-attr! inp "readonly" ""))
          (when-not readonly? (du/remove-attr! inp "readonly"))
          (when placeholder (du/set-attr! inp "placeholder" placeholder))
          (when aria-label  (du/set-attr! inp "aria-label" aria-label)))

        (apply-error! el inp)
        (sync-form-state! el)
        (sync-input-display! el)
        (render-calendar! el)))))

;; ---------------------------------------------------------------------------
;; Date selection
;; ---------------------------------------------------------------------------

(defn- change-detail
  "The detail a change or change-request carries.
   Every key is present, null where the current mode has nothing to say. `date` is the day of a
   range click."
  [{:keys [value date start end]} mode-s reason]
  #js {:value  value
       :date   date
       :start  start
       :end    end
       :mode   mode-s
       :reason reason})

(defn- set-single-value!
  [^js el ^js d]
  (if d
    (du/set-attr! el model/attr-value (dates/date->iso d))
    (du/remove-attr! el model/attr-value)))

(defn- close-on-select!
  "Removes `open` when the element has `close-on-select`."
  [^js el]
  (when (du/has-attr? el model/attr-close-on-select)
    (du/remove-attr! el "open")))

(defn- set-range!
  "Writes `start` and `end` on `el`, and removes `end` when it is nil."
  [^js el {:keys [start end]}]
  (du/set-attr! el model/attr-start start)
  (if end
    (du/set-attr! el model/attr-end end)
    (du/remove-attr! el model/attr-end)))

(defn- select-single-date!
  "Asks to set the value to day `d`, and sets it when the request is not refused."
  [^js el ^js d reason]
  (let [detail (change-detail {:value (dates/date->iso d)} model/mode-single reason)]
    (when (du/dispatch-cancelable! el model/event-change-request detail)
      (set-single-value! el d)
      (read-state! el)
      (render! el)
      (du/dispatch! el model/event-change detail)
      (close-on-select! el))))

(defn- select-range-date!
  "Asks for the range that a click on day `d` gives, and sets it when the request is not refused."
  [^js el canon ^js d reason]
  (let [picked (model/range-pick canon d)]
    (when (du/dispatch-cancelable! el model/event-change-request
                                   (change-detail (assoc picked :date (dates/date->iso d))
                                                  model/mode-range reason))
      (set-range! el picked)
      (read-state! el)
      (render! el)
      (du/dispatch! el model/event-change (change-detail picked model/mode-range reason))
      (when (:end picked)
        (close-on-select! el)))))

(defn- do-select-date!
  "Selects day `d`: sets the value, or starts or ends the range."
  [^js el ^js d reason]
  (when-not (du/has-attr? el model/attr-readonly)
    (let [state  (du/getv el k-state)
          canon  (when state (gobj/get state "canon"))]
      (if (= (:mode canon) :range)
        (select-range-date! el canon d reason)
        (select-single-date! el d reason)))))

;; ---------------------------------------------------------------------------
;; Input commit
;; ---------------------------------------------------------------------------

(declare xdp-clear!)

(defn- clear-detail
  "The detail of a clear: it asks for no date."
  [mode-s reason]
  (change-detail {} mode-s reason))

(defn- commit-display!
  "Commits the text of the input. Returns `:date` or `:cleared` when it commits, else nil."
  [^js el reason]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        ;; Empty rather than nil when there is no input yet, which `commit()` can
        ;; reach on an element that was never connected. A text field with no text
        ;; has the value "", and null on this event would mean the field does not
        ;; apply — which is what it means on a change, and is not true here.
        val     (or (when inp (.-value inp)) "")
        state   (du/getv el k-state)
        canon   (when state (gobj/get state "canon"))
        mode    (:mode canon)
        mode-s  (model/mode-name canon)]
    (du/dispatch! el model/event-input #js {:value val :mode mode-s})
    (cond
      ;; A blank input should clear
      (and (nil? (model/normalize-str val))
           (model/committed-value? canon))
      (let [detail (clear-detail mode-s reason)]
        (when (du/dispatch-cancelable! el model/event-change-request detail)
          (xdp-clear! el)
          (du/dispatch! el model/event-change detail)
          :cleared))

      (= mode :single)
      (let [{:keys [ok? date]} (model/parse-display->single val)]
        (when ok?
          (let [iso (dates/date->iso date)]
            ;; du/dispatch-cancelable! returns a boolean (true = proceed,
            ;; false = preventDefault'd). The previous code bound the return
            ;; to ^js ev and read (.-defaultPrevented ev), which is undefined
            ;; on a boolean — so the cancel path never blocked the commit.
            (when (du/dispatch-cancelable! el model/event-change-request
                                           (change-detail {:value iso} mode-s reason))
              (set-single-value! el date)
              (read-state! el)
              (render! el)
              (du/dispatch! el model/event-change
                          (change-detail {:value iso} mode-s reason))
              :date))))

      :else
      (let [{:keys [ok? start end]} (model/parse-display->range val {:separator (:separator canon)})]
        (when ok?
          (let [s-iso (dates/date->iso start)
                e-iso (dates/date->iso end)]
            (when (du/dispatch-cancelable! el model/event-change-request
                                           (change-detail {:start s-iso :end e-iso}
                                                          mode-s reason))
              (du/set-attr! el model/attr-start s-iso)
              (du/set-attr! el model/attr-end   e-iso)
              (read-state! el)
              (render! el)
              (du/dispatch! el model/event-change
                          (change-detail {:start s-iso :end e-iso} mode-s reason))
              :date)))))))

;; ---------------------------------------------------------------------------
;; Month navigation
;; ---------------------------------------------------------------------------

(defn- navigate-month!
  [^js el direction]
  (when-let [^js cur (du/getv el k-month)]
    (let [y       (.getUTCFullYear cur)
          mo      (.getUTCMonth cur)
          next-mo (if (= direction :next)
                    (js/Date. (js/Date.UTC y (inc mo) 1))
                    (js/Date. (js/Date.UTC y (dec mo) 1)))]
      (du/setv! el k-month next-mo)
      (render-calendar! el))))

;; ---------------------------------------------------------------------------
;; Popover open/close
;; ---------------------------------------------------------------------------

(defn- open-popover!
  "Opens the calendar on the month of the selected date, or of today, and focuses that day."
  [^js el]
  (when-not (du/has-attr? el "open")
    (let [state  (du/getv el k-state)
          anchor (or (model/anchor (when state (gobj/get state "canon"))) (js/Date.))]
      (du/setv! el k-month (dates/start-of-month anchor))
      (du/set-attr! el "open" "")
      (focus-grid-date! el (dates/date->iso anchor)))))

(defn- close-popover!
  [^js el]
  (when (du/has-attr? el "open")
    (du/remove-attr! el "open")))

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
   "--x-date-picker-popover-bg:var(--x-color-bg,#fff);"
   "--x-date-picker-popover-radius:var(--x-radius-lg,12px);"
   "--x-date-picker-nav-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-date-picker-weekday:var(--x-color-text-muted,#94a3b8);"
   "--x-date-picker-day-text:inherit;"
   "--x-date-picker-day-hover:var(--x-color-surface-active,#f1f5f9);"
   "--x-date-picker-selected-bg:var(--x-color-primary,#2563eb);"
   "--x-date-picker-selected-text:#fff;"
   "--x-date-picker-range-bg:var(--x-color-surface-active,#dbeafe);"
   "--x-date-picker-range-text:var(--x-color-primary-active,#1e40af);"
   "--x-date-picker-error-color:var(--x-color-danger,#dc2626);"
   "}"
   du/control-font-rule
   du/hidden-rule
   "[part=container]{position:relative;display:flex;align-items:stretch;gap:8px;width:100%;}"
   "[part=input]{"
   "flex:1;min-width:0;appearance:none;box-sizing:border-box;"
   "border:var(--x-border-width,1px) solid var(--x-date-picker-border,#cbd5e1);"
   "border-radius:var(--x-date-picker-radius);"
   "padding:8px 12px;font-size:var(--x-font-size-sm,0.9375rem);"
   "background:var(--x-date-picker-input-bg,#fff);"
   "color:var(--x-date-picker-text,inherit);"
   "outline:none;"
   "}"
   "[part=input]:focus{border-color:var(--x-date-picker-focus,#60a5fa);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-date-picker-focus) 25%,transparent);}"
   "[part=input][disabled]{opacity:0.5;cursor:default;}"
   "[part=btn][disabled]{opacity:0.5;cursor:default;pointer-events:none;}"
   "[part=input]::placeholder{color:var(--x-date-picker-placeholder,#94a3b8);}"
   "[part=btn]{"
   "all:unset;cursor:pointer;flex:0 0 auto;width:42px;height:42px;"
   "display:inline-flex;align-items:center;justify-content:center;"
   "border:var(--x-border-width,1px) solid var(--x-date-picker-border,#cbd5e1);"
   "border-radius:var(--x-date-picker-radius);"
   "background:var(--x-date-picker-btn-bg,#f8fafc);"
   "color:var(--x-date-picker-text,inherit);"
   "box-sizing:border-box;"
   "}"
   "[part=btn]:hover{background:var(--x-date-picker-btn-hover,#f1f5f9);}"
   "[part=btn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=popover]{"
   "display:none;position:absolute;z-index:var(--x-z-dropdown,1000);"
   "top:calc(100% + 8px);left:0;"
   "background:var(--x-date-picker-popover-bg,#fff);"
   "border:var(--x-border-width,1px) solid var(--x-date-picker-border,#e2e8f0);"
   "border-radius:var(--x-date-picker-popover-radius);"
   "box-shadow:var(--x-shadow-md,0 8px 32px rgba(0,0,0,0.12),0 2px 8px rgba(0,0,0,0.06));"
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
   "width:32px;height:32px;border-radius:var(--x-radius-md,6px);"
   "color:var(--x-date-picker-text,inherit);"
   "}"
   "[part=navbtn]:hover{background:var(--x-date-picker-nav-hover,#f1f5f9);}"
   "[part=navbtn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=monthlabel]{font-weight:var(--x-font-weight-semibold,600);font-size:var(--x-font-size-sm,0.9375rem);}"
   "[part=weekdays]{"
   "display:grid;grid-template-columns:repeat(7,1fr);gap:4px;margin-bottom:4px;"
   "}"
   "[part=weekday]{"
   "text-align:center;font-size:var(--x-font-size-xs,0.75rem);font-weight:var(--x-font-weight-semibold,600);"
   "color:var(--x-date-picker-weekday,#94a3b8);padding:4px 0;"
   "}"
   "[part=grid]{display:grid;grid-template-columns:repeat(7,1fr);gap:4px;}"
   "[part=day]{"
   "all:unset;cursor:pointer;box-sizing:border-box;"
   "display:flex;align-items:center;justify-content:center;"
   "height:36px;border-radius:var(--x-radius-md,6px);"
   "font-size:var(--x-font-size-sm,0.875rem);"
   "color:var(--x-date-picker-day-text,inherit);"
   "}"
   "[part=day]:hover:not([disabled]){background:var(--x-date-picker-day-hover,#f1f5f9);}"
   "[part=day]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"
   "[part=day][data-outside=true]{opacity:0.4;}"
   "[part=day][data-disabled=true]{opacity:0.3;cursor:default;pointer-events:none;}"
   "[part=day][data-selected=true]{"
   "background:var(--x-date-picker-selected-bg,#2563eb);"
   "color:var(--x-date-picker-selected-text,#fff);"
   "font-weight:var(--x-font-weight-semibold,600);"
   "}"
   "[part=day][data-in-range=true]:not([data-range-edge=true]){"
   "background:var(--x-date-picker-range-bg,#dbeafe);"
   "color:var(--x-date-picker-range-text,#1e40af);"
   "border-radius:0;"
   "}"
   "[part=day][data-range-edge=true]{"
   "background:var(--x-date-picker-selected-bg,#2563eb);"
   "color:var(--x-date-picker-selected-text,#fff);"
   "font-weight:var(--x-font-weight-semibold,600);"
   "}"
   "[part=sr-status]{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-date-picker-border:var(--x-color-border,#334155);"
   "--x-date-picker-input-bg:var(--x-color-surface,#0f172a);"
   "--x-date-picker-btn-bg:var(--x-color-surface-hover,#1e293b);"
   "--x-date-picker-btn-hover:var(--x-color-surface-active,#334155);"
   "--x-date-picker-popover-bg:var(--x-color-bg,#1e293b);"
   "--x-date-picker-day-hover:var(--x-color-surface-active,#334155);"
   "--x-date-picker-selected-bg:var(--x-color-primary,#3b82f6);"
   "--x-date-picker-range-bg:var(--x-color-surface-active,#1e3a8a);"
   "--x-date-picker-range-text:var(--x-color-focus-ring,#93c5fd);"
   "--x-date-picker-nav-hover:var(--x-color-surface-active,#334155);"
   "--x-date-picker-error-color:var(--x-color-danger,#f87171);"
   "}"
   "}"
   "[part=error]{"
   "display:block;margin-top:0.25rem;"
   "font-size:var(--x-font-size-sm,0.8125rem);line-height:1.4;"
   "color:var(--x-date-picker-error-color,#dc2626);"
   "}"
   ".error-hidden{display:none;}"
   ":host([data-invalid]) [part=input]{border-color:var(--x-date-picker-error-color,#dc2626);}"
   ":host([data-invalid]) [part=input]:focus{"
   "border-color:var(--x-date-picker-error-color,#dc2626);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-date-picker-error-color,#dc2626) 25%,transparent);"
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
        ^js style     (.createElement js/document "style")
        ^js container (.createElement js/document "div")
        ^js inp       (.createElement js/document "input")
        ^js btn       (.createElement js/document "button")
        ^js popover   (.createElement js/document "div")
        ^js nav       (.createElement js/document "div")
        ^js nav-prev  (.createElement js/document "button")
        ^js month-lbl (.createElement js/document "div")
        ^js nav-next  (.createElement js/document "button")
        ^js weekdays  (.createElement js/document "div")
        ^js grid      (.createElement js/document "div")
        ^js sr        (.createElement js/document "div")
        ^js error     (.createElement js/document "span")]

    (set! (.-textContent style) style-text)

    (du/set-attr! container "part" "container")

    ;; Inline error message below the field. Mirrors x-form-field: an assertive
    ;; live region with role=alert, hidden until an `error` attribute is set.
    (du/set-attr! error "part" "error")
    (du/set-attr! error "id" "error")
    (du/set-attr! error "role" "alert")
    (du/set-attr! error "aria-live" "assertive")
    (.add (.-classList error) "error-hidden")

    (du/set-attr! inp "part" "input")
    (du/set-attr! inp "type" "text")
    (du/set-attr! inp "inputmode" "none")
    (du/set-attr! inp "autocomplete" "off")
    (du/set-attr! inp "role" "combobox")
    (du/set-attr! inp "aria-haspopup" "dialog")
    (du/set-attr! inp "aria-expanded" "false")

    (du/set-attr! btn "part" "btn")
    (du/set-attr! btn "type" "button")
    (du/set-attr! btn "aria-label" "Open calendar")
    (set! (.-textContent btn) "\uD83D\uDCC5")

    (du/set-attr! popover "part" "popover")
    (du/set-attr! popover "role" "dialog")
    (du/set-attr! popover "aria-modal" "true")
    (du/set-attr! popover "aria-label" "Calendar")

    (du/set-attr! nav "part" "nav")

    (du/set-attr! nav-prev "part" "navbtn")
    (du/set-attr! nav-prev "data-nav" "prev")
    (du/set-attr! nav-prev "type" "button")
    (du/set-attr! nav-prev "aria-label" "Previous month")
    (set! (.-textContent nav-prev) "\u2039")

    (du/set-attr! month-lbl "part" "monthlabel")
    (du/set-attr! month-lbl "aria-live" "polite")

    (du/set-attr! nav-next "part" "navbtn")
    (du/set-attr! nav-next "data-nav" "next")
    (du/set-attr! nav-next "type" "button")
    (du/set-attr! nav-next "aria-label" "Next month")
    (set! (.-textContent nav-next) "\u203A")

    (du/set-attr! weekdays "part" "weekdays")
    (du/set-attr! weekdays "aria-hidden" "true")

    (du/set-attr! grid "part" "grid")
    (du/set-attr! grid "role" "grid")
    (du/set-attr! grid "aria-label" "Calendar grid")

    (du/set-attr! sr "part" "sr-status")
    (du/set-attr! sr "aria-live" "polite")
    (du/set-attr! sr "aria-atomic" "true")

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
    (.appendChild root error)

    (let [refs #js {:input      inp
                    :btn        btn
                    :popover    popover
                    :nav-prev   nav-prev
                    :month-label month-lbl
                    :nav-next   nav-next
                    :weekdays   weekdays
                    :grid       grid
                    :sr         sr
                    :error      error}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Event listeners
;; ---------------------------------------------------------------------------

(defn- on-input-event!
  [^js el ^js _e]
  (let [refs    (du/getv el k-refs)
        ^js inp (when refs (gobj/get refs "input"))
        state   (du/getv el k-state)
        canon   (when state (gobj/get state "canon"))
        mode-s  (model/mode-name canon)]
    ;; The listener only fires from the input it is attached to, so `inp` is
    ;; there; the fallback is so every site honours the declared `string` rather
    ;; than one of them resting on that being true.
    (du/dispatch! el model/event-input
               #js {:value (or (when inp (.-value inp)) "") :mode mode-s})))

(defn- on-input-keydown!
  [^js el ^js e]
  (when-not (forms/disabled? el)
    (let [key (.-key e)]
      (cond
        (= key "Enter")
        (do (.preventDefault e)
            (when (= :date (commit-display! el "keyboard"))
              (close-on-select! el)))

        (= key "Escape")
        (close-popover! el)))))

(defn- on-input-focus!
  [^js el ^js _e]
  (du/setv! el k-focused true))

(defn- on-input-blur!
  [^js el ^js _e]
  (du/setv! el k-focused false)
  (when (and (not (forms/disabled? el)) (input-edited? el))
    (commit-display! el "blur"))
  (sync-input-display! el))

(defn- on-btn-click!
  [^js el ^js _e]
  (when-not (or (forms/disabled? el)
                (du/has-attr? el model/attr-readonly))
    (if (du/has-attr? el "open")
      (close-popover! el)
      (open-popover! el))))

(defn- on-nav-click!
  [^js el ^js e]
  (when-not (forms/disabled? el)
    (let [^js target (.-currentTarget e)
          dir        (.getAttribute target "data-nav")]
      (if (= dir "prev")
        (navigate-month! el :prev)
        (navigate-month! el :next)))))

(defn- on-grid-click!
  [^js el ^js e]
  (when-not (or (forms/disabled? el)
                (du/has-attr? el model/attr-readonly))
    (let [^js target (.-target e)
          ^js btn    (.closest target "button[data-iso]")]
      (when (and btn (not= "true" (.getAttribute btn "data-disabled")))
        (let [iso (. btn getAttribute "data-iso")
              ^js d (dates/iso->date iso)]
          (when d (do-select-date! el d "click")))))))

(defn- event-day-iso
  "The iso date of the day cell that `e` happened on, or nil."
  [^js e]
  (some-> ^js (.closest (.-target e) "button[data-iso]") (.getAttribute "data-iso")))

(defn- on-grid-keydown!
  [^js el ^js e]
  (when-not (forms/disabled? el)
    (let [key     (.-key e)
          cur-iso (or (event-day-iso e) (du/getv el k-grid-focus))
          ^js cur (when cur-iso (dates/iso->date cur-iso))
          state   (du/getv el k-state)
          canon   (when state (gobj/get state "canon"))
          ^js month (du/getv el k-month)
          nav-date
          (when cur
            (case key
              "ArrowLeft"  (dates/add-days cur -1)
              "ArrowRight" (dates/add-days cur 1)
              "ArrowUp"    (dates/add-days cur -7)
              "ArrowDown"  (dates/add-days cur 7)
              "PageUp"     (dates/add-months cur -1)
              "PageDown"   (dates/add-months cur 1)
              "Home"       (dates/start-of-week cur)
              "End"        (dates/end-of-week cur)
              nil))]
      (cond
        ;; Arrow / page / home / end navigation
        (some? nav-date)
        (do
          (.preventDefault e)
          (let [clamped (dates/clamp-to-range nav-date (:min-d canon) (:max-d canon))
                nav-iso (dates/date->iso clamped)]
            ;; If navigated outside current month, shift month view
            (when (and month (not= (.getUTCMonth clamped) (.getUTCMonth month)))
              (du/setv! el k-month (dates/start-of-month clamped))
              (render-calendar! el))
            (focus-grid-date! el nav-iso)))

        ;; Enter / Space selects the focused date
        (and (or (= key "Enter") (= key " "))
             cur
             (not (du/has-attr? el model/attr-readonly)))
        (do
          (.preventDefault e)
          (when-not (model/day-out-of-range? cur canon)
            (do-select-date! el cur "keyboard")))

        ;; Escape closes the calendar
        (= key "Escape")
        (do
          (.preventDefault e)
          (close-popover! el)
          ;; Return focus to input
          (let [refs (du/getv el k-refs)
                ^js inp (when refs (gobj/get refs "input"))]
            (when inp (.focus inp))))))))

(defn- on-popover-mousedown!
  [^js _el ^js e]
  ;; Prevent input blur when clicking inside popover
  (.preventDefault e))

(defn- add-listeners!
  [^js el]
  (let [refs      (du/getv el k-refs)
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
                      (when (du/has-attr? el "open")
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

    (du/setv! el k-handlers
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
  (let [refs     (du/getv el k-refs)
        handlers (du/getv el k-handlers)]
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

(defn- read-inputs
  "The raw value of every observed attribute of `el`, with its disabled state."
  [^js el]
  (into {:disabled? (forms/disabled? el)}
        (map (juxt identity (partial du/get-attr el)))
        (array-seq model/observed-attributes)))

(defn- render-from-attrs!
  "Reads the state of `el` from its attributes and renders it. Caches `inputs`."
  [^js el inputs]
  (read-state! el)
  (render! el)
  (du/setv! el k-model inputs))

(defn- connected!
  [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (render-from-attrs! el (read-inputs el)))

(defn- disconnected!
  [^js el]
  (remove-listeners! el))

(defn- attribute-changed!
  [^js el _n old-val new-val]
  (when (not= old-val new-val)
    ;; Force the popover closed when disabled is set while open. Done here, not
    ;; inside render!, so the render path doesn't write observed attrs.
    (when (and (forms/disabled? el)
               (du/has-attr? el "open"))
      (du/remove-attr! el "open"))
    (let [inputs (read-inputs el)]
      (when (not= inputs (du/getv el k-model))
        (render-from-attrs! el inputs)))))

;; ---------------------------------------------------------------------------
;; Property definitions
;; ---------------------------------------------------------------------------

(defn- xdp-focus!
  "Body of the focus() prototype method — exposed so the .defineProperty
  :value descriptor stays a tight thunk."
  [^js this]
  (let [refs (du/getv this k-refs)
        ^js inp (when refs (gobj/get refs "input"))]
    (when inp (.focus inp))))

(defn- xdp-clear!
  "Body of the clear() prototype method."
  [^js this]
  (let [state (du/getv this k-state)
        canon (when state (gobj/get state "canon"))]
    (if (= (:mode canon) :single)
      (du/remove-attr! this model/attr-value)
      (do (du/remove-attr! this model/attr-start)
          (du/remove-attr! this model/attr-end)))
    (read-state! this)
    (render! this)))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
;; Reset clears the committed value(s) — mirroring x-form-field, which resets to
;; empty. render! then re-syncs the (now empty) form value + validity.
(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-error)
  (xdp-clear! el))

(defn- define-methods!
  [^js proto]
  ;; focus / commit / clear — .defineProperty per the Tier-2 idiom (bare aset
  ;; on the prototype was audited out in PR #155).
  (.defineProperty js/Object proto "focus"
                   #js {:value (fn xdp-focus [] (this-as ^js this (xdp-focus! this)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "commit"
                   #js {:value (fn xdp-commit []
                                 (this-as ^js this
                                   (commit-display! this "programmatic")
                                   js/undefined))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "clear"
                   #js {:value (fn xdp-clear [] (this-as ^js this (xdp-clear! this)))
                        :writable true :configurable true}))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  (du/install-properties! proto model/property-api)
  (define-methods! proto))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
