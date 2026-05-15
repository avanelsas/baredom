(ns baredom.components.x-otp-input.x-otp-input
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-otp-input.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xOtpInputRefs")
(def ^:private k-internals "__xOtpInputInternals")
(def ^:private k-handlers  "__xOtpInputHandlers")
(def ^:private k-model     "__xOtpInputModel")
(def ^:private k-focus-val "__xOtpInputFocusValue")

;; ---------------------------------------------------------------------------
;; DOM constants
;; ---------------------------------------------------------------------------
(def ^:private slot-tag-name-uppercase "INPUT")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "--x-otp-input-slot-size:2.75rem;"
   "--x-otp-input-gap:var(--x-space-sm,0.5rem);"
   "--x-otp-input-bg:var(--x-color-surface,#ffffff);"
   "--x-otp-input-color:var(--x-color-text,#111827);"
   "--x-otp-input-border:1px solid var(--x-color-border,#d1d5db);"
   "--x-otp-input-border-radius:var(--x-radius-md,6px);"
   "--x-otp-input-focus-ring-color:var(--x-color-primary,#2563eb);"
   "--x-otp-input-error-color:var(--x-color-danger,#dc2626);"
   "--x-otp-input-disabled-opacity:var(--x-opacity-disabled,0.45);"
   "--x-otp-input-font-family:var(--x-font-family-mono,ui-monospace,SFMono-Regular,Menlo,monospace);"
   "--x-otp-input-font-size:1.25rem;"
   "--x-otp-input-font-weight:var(--x-font-weight-semibold,600);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-otp-input-bg:var(--x-color-surface,#1f2937);"
   "--x-otp-input-color:var(--x-color-text,#f9fafb);"
   "--x-otp-input-border:1px solid var(--x-color-border,#4b5563);"
   "}"
   "}"
   "[part=root]{"
   "display:flex;"
   "flex-wrap:wrap;"
   "align-items:center;"
   "gap:var(--x-otp-input-gap);"
   "max-width:calc(100vw - 2rem);"
   "}"
   "[part=slot]{"
   "width:var(--x-otp-input-slot-size);"
   "height:var(--x-otp-input-slot-size);"
   "text-align:center;"
   "font-family:var(--x-otp-input-font-family);"
   "font-size:var(--x-otp-input-font-size);"
   "font-weight:var(--x-otp-input-font-weight);"
   "background:var(--x-otp-input-bg);"
   "color:var(--x-otp-input-color);"
   "border:var(--x-otp-input-border);"
   "border-radius:var(--x-otp-input-border-radius);"
   "outline:none;"
   "padding:0;"
   "caret-color:var(--x-otp-input-focus-ring-color);"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "box-sizing:border-box;"
   "}"
   "[part=slot]:focus{"
   "border-color:var(--x-otp-input-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-otp-input-focus-ring-color) 25%,transparent);"
   "}"
   ":host([error]) [part=slot],"
   ":host([error]) [part=slot]:focus{"
   "border-color:var(--x-otp-input-error-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-otp-input-error-color) 20%,transparent);"
   "}"
   ":host([disabled]) [part=root]{"
   "opacity:var(--x-otp-input-disabled-opacity);"
   "cursor:not-allowed;"
   "}"
   ":host([disabled]) [part=slot]{"
   "cursor:not-allowed;"
   "}"
   "@media (pointer:coarse){"
   "[part=slot]{"
   "min-width:44px;"
   "min-height:44px;"
   "}"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=slot]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------

(defn- slot-input-type [mask?]
  (if mask? "password" "text"))

;; ---------------------------------------------------------------------------
;; Slot construction (rebuild only when length/type/mask changes)
;; ---------------------------------------------------------------------------
(defn- make-slot! [index length type-name first? mask?]
  (let [^js input (.createElement js/document "input")]
    (du/set-attr! input "part"           "slot")
    (du/set-attr! input "data-index"     (str index))
    (du/set-attr! input "maxlength"      "1")
    (du/set-attr! input "inputmode"      (model/slot-inputmode type-name))
    (du/set-attr! input "autocomplete"   (if first? "one-time-code" "off"))
    (du/set-attr! input "autocapitalize" "off")
    (du/set-attr! input "autocorrect"    "off")
    (du/set-attr! input "aria-label"     (model/slot-aria-label type-name index length))
    (set! (.-type input)       (slot-input-type mask?))
    (set! (.-spellcheck input) false)
    input))

(defn- rebuild-slots! [^js root-el length type-name mask?]
  (set! (.-innerHTML root-el) "")
  (dotimes [i length]
    (.appendChild root-el (make-slot! i length type-name (zero? i) mask?))))

;; ---------------------------------------------------------------------------
;; Slot reads / writes
;; ---------------------------------------------------------------------------
(defn- slot-list [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js root (gobj/get refs "root")]
      (.querySelectorAll root "input"))))

(defn- collect-value [^js el]
  (let [slots (slot-list el)
        parts (js/Array.)]
    (when slots
      (dotimes [i (.-length slots)]
        (let [^js slot (aget slots i)]
          (.push parts (or (.-value slot) "")))))
    (.join parts "")))

(defn- write-slots!
  "Write each character of `value` into the matching slot input.
  Skips no-op writes to avoid spurious cursor jumps in browsers that reset
  selection on `.value =`."
  [^js el value]
  (when-let [slots (slot-list el)]
    (let [n (.-length slots)]
      (dotimes [i n]
        (let [^js slot (aget slots i)
              ch       (if (< i (count value))
                         (.charAt value i)
                         "")]
          (when (not= (.-value slot) ch)
            (set! (.-value slot) ch)))))))

(defn- focus-slot! [^js el index]
  (when-let [slots (slot-list el)]
    (when (and (>= index 0) (< index (.-length slots)))
      (let [^js slot (aget slots index)]
        (.focus slot)
        ;; Select content so typing replaces it cleanly.
        (when (.-select slot) (.select slot))))))

(defn- focus-first-empty! [^js el]
  (when-let [slots (slot-list el)]
    (let [n (.-length slots)
          target (loop [i 0]
                   (cond
                     (>= i n)                              (dec n)
                     (= "" (.-value (aget slots i)))       i
                     :else                                 (recur (inc i))))]
      (focus-slot! el target))))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- first-slot [^js el]
  (when-let [slots (slot-list el)]
    (when (pos? (.-length slots))
      (aget slots 0))))

(defn- sync-validity! [^js el ^js internals m]
  (let [anchor (or (first-slot el) el)
        value  (:value m)
        length (:length m)]
    (cond
      (:has-error? m)
      (.setValidity internals #js {:customError true} (:error m) anchor)

      (and (:required? m) (= "" value))
      (.setValidity internals #js {:valueMissing true}
                    "Please fill in this field." anchor)

      (and (:required? m) (< (count value) length))
      (.setValidity internals #js {:tooShort true}
                    (str "Please enter all " length " characters.") anchor)

      :else
      (.setValidity internals #js {} "" anchor))))

;; ---------------------------------------------------------------------------
;; Read model from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:name-raw           (du/get-attr el model/attr-name)
    :value-raw          (du/get-attr el model/attr-value)
    :length-raw         (du/get-attr el model/attr-length)
    :type-raw           (du/get-attr el model/attr-type)
    :mask-present?      (du/has-attr? el model/attr-mask)
    :disabled-present?  (du/has-attr? el model/attr-disabled)
    :readonly-present?  (du/has-attr? el model/attr-readonly)
    :required-present?  (du/has-attr? el model/attr-required)
    :autofocus-present? (du/has-attr? el model/attr-autofocus)
    :label-raw          (du/get-attr el model/attr-label)
    :placeholder-raw    (du/get-attr el model/attr-placeholder)
    :error-raw          (du/get-attr el model/attr-error)}))

;; ---------------------------------------------------------------------------
;; Apply model → DOM
;; ---------------------------------------------------------------------------
(defn- structure-changed? [old-m new-m]
  (or (nil? old-m)
      (not= (:length old-m) (:length new-m))
      (not= (:type old-m)   (:type new-m))
      (not= (:mask? old-m)  (:mask? new-m))))

(defn- apply-slot-attrs! [^js el m]
  (when-let [slots (slot-list el)]
    (let [n          (.-length slots)
          disabled?  (:disabled? m)
          readonly?  (:readonly? m)
          required?  (:required? m)
          has-error? (:has-error? m)
          ph         (:placeholder m)]
      (dotimes [i n]
        (let [^js slot (aget slots i)]
          (set! (.-disabled slot) disabled?)
          (set! (.-readOnly slot) readonly?)
          (set! (.-placeholder slot) ph)
          (du/set-attr! slot "aria-invalid" (if has-error? "true" "false"))
          (if (and (zero? i) required?)
            (du/set-attr! slot "aria-required" "true")
            (du/remove-attr! slot "aria-required")))))))

(defn- apply-host-aria! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js root (gobj/get refs "root")
          label    (:label m)]
      (if (and (string? label) (not= "" label))
        (du/set-attr! root "aria-label" label)
        (du/remove-attr! root "aria-label")))))

(defn- sync-host-value-attr! [^js el normalized-value]
  (let [raw (or (du/get-attr el model/attr-value) "")]
    (when (not= raw normalized-value)
      (du/set-attr! el model/attr-value normalized-value))))

(defn- apply-model! [^js el new-m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js root (gobj/get refs "root")
          old-m    (du/getv el k-model)]
      (when (structure-changed? old-m new-m)
        (rebuild-slots! root (:length new-m) (:type new-m) (:mask? new-m)))
      (apply-slot-attrs! el new-m)
      (apply-host-aria!  el new-m)
      (write-slots! el (:value new-m))
      (when-let [^js internals (du/getv el k-internals)]
        (.setFormValue internals (:value new-m))
        (sync-validity! el internals new-m))
      (du/setv! el k-model new-m)
      ;; After caching the new model, reflect the normalized value back to the
      ;; host attribute. This re-fires attributeChangedCallback, but the cached
      ;; model already matches → the change-guard skips a second apply.
      (sync-host-value-attr! el (:value new-m)))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ---------------------------------------------------------------------------
;; Event helpers
;; ---------------------------------------------------------------------------
(defn- slot-target? [^js evt]
  (let [^js target (.-target evt)]
    (and target (= slot-tag-name-uppercase (.-tagName target)))))

(defn- slot-index [^js target]
  (js/parseInt (.getAttribute target "data-index") 10))

(defn- name-for-event [^js el]
  (or (du/get-attr el model/attr-name) ""))

(defn- dispatch-input-event! [^js el value length]
  (du/dispatch! el model/event-input
                #js {:name     (name-for-event el)
                     :value    value
                     :complete (model/complete? value length)}))

(defn- maybe-dispatch-complete! [^js el prev-value new-value length]
  (let [was-complete? (model/complete? prev-value length)
        now-complete? (model/complete? new-value length)]
    (when (and (not was-complete?) now-complete?)
      (du/dispatch! el model/event-complete
                    #js {:name (name-for-event el) :value new-value}))))

(defn- commit-value! [^js el new-value]
  (let [old-value (or (du/get-attr el model/attr-value) "")]
    (when (not= old-value new-value)
      (du/set-attr! el model/attr-value new-value))
    old-value))

;; ---------------------------------------------------------------------------
;; Multi-char distribution — shared by paste, SMS autofill, IME multi-char
;; ---------------------------------------------------------------------------
(defn- distribute-from-slot!
  "Spread `filtered` characters across slots starting at `start-idx`. Preserves
   the head before the cursor and the tail past the inserted run, then commits
   and emits events. Used for paste, SMS autofill, and IME multi-char input."
  [^js el start-idx filtered length]
  (let [available  (max 0 (- length start-idx))
        fragment   (.substring filtered 0 available)
        existing   (or (du/get-attr el model/attr-value) "")
        head       (.substring existing 0 (min start-idx (count existing)))
        tail-start (+ start-idx (count fragment))
        tail       (if (< tail-start (count existing))
                     (.substring existing tail-start)
                     "")
        new-value  (model/truncate (str head fragment tail) length)
        old-value  (commit-value! el new-value)
        next-idx   (min (dec length) (+ start-idx (count fragment)))]
    (focus-slot! el next-idx)
    (when (not= old-value new-value)
      (dispatch-input-event! el new-value length)
      (maybe-dispatch-complete! el old-value new-value length))))

;; ---------------------------------------------------------------------------
;; Input handler — single-char typing path + multi-char dispatch
;; ---------------------------------------------------------------------------
(defn- handle-single-char-input! [^js el ^js target ^js evt start-idx filtered length]
  (let [first-ch (if (pos? (count filtered)) (.substring filtered 0 1) "")
        raw      (.-value target)]
    (when (not= raw first-ch)
      (set! (.-value target) first-ch))
    (let [new-value (collect-value el)
          old-value (commit-value! el new-value)]
      (when (not= old-value new-value)
        (dispatch-input-event! el new-value length)
        (maybe-dispatch-complete! el old-value new-value length))
      (when (and (pos? (count first-ch))
                 (not (.-isComposing evt))
                 (< start-idx (dec length)))
        (focus-slot! el (inc start-idx))))))

(defn- handle-input! [^js el ^js evt]
  (when (slot-target? evt)
    (let [^js target (.-target evt)
          m         (du/getv el k-model)
          length    (:length m)
          type-name (:type m)
          start-idx (slot-index target)
          raw       (.-value target)
          filtered  (model/filter-chars type-name raw)]
      (if (> (count filtered) 1)
        ;; Multi-char input — SMS autofill or IME composition delivered as one event.
        (distribute-from-slot! el start-idx filtered length)
        (handle-single-char-input! el target evt start-idx filtered length)))))

;; ---------------------------------------------------------------------------
;; Keydown handler — backspace / arrows / home / end
;; ---------------------------------------------------------------------------
(defn- clear-and-focus-prev! [^js el index]
  (focus-slot! el (dec index))
  (when-let [slots (slot-list el)]
    (let [^js prev (aget slots (dec index))]
      (set! (.-value prev) ""))))

(defn- handle-backspace! [^js el ^js evt index slot-val length]
  (when (= "" slot-val)
    (.preventDefault evt)
    (when (pos? index)
      (clear-and-focus-prev! el index)
      (let [new-value (collect-value el)
            old-value (commit-value! el new-value)]
        (dispatch-input-event! el new-value length)
        (maybe-dispatch-complete! el old-value new-value length)))))

(defn- handle-keydown! [^js el ^js evt]
  (when (slot-target? evt)
    (let [^js target (.-target evt)
          key        (.-key evt)
          index      (slot-index target)
          slot-val   (.-value target)
          m          (du/getv el k-model)
          length     (:length m)]
      (cond
        (= key "Backspace")
        (handle-backspace! el evt index slot-val length)

        (= key "ArrowLeft")
        (when (pos? index)
          (.preventDefault evt)
          (focus-slot! el (dec index)))

        (= key "ArrowRight")
        (when (< index (dec length))
          (.preventDefault evt)
          (focus-slot! el (inc index)))

        (= key "Home")
        (do (.preventDefault evt) (focus-slot! el 0))

        (= key "End")
        (do (.preventDefault evt) (focus-slot! el (dec length)))))))

;; ---------------------------------------------------------------------------
;; Paste handler — distribute pasted text across slots
;; ---------------------------------------------------------------------------
(defn- handle-paste! [^js el ^js evt]
  (when (slot-target? evt)
    (.preventDefault evt)
    (let [^js target (.-target evt)
          start-idx  (slot-index target)
          ^js cd     (.-clipboardData evt)
          pasted     (when cd (.getData cd "text/plain"))
          m          (du/getv el k-model)
          length     (:length m)
          type-name  (:type m)
          filtered   (model/filter-chars type-name (or pasted ""))]
      (distribute-from-slot! el start-idx filtered length))))

;; ---------------------------------------------------------------------------
;; Focus / blur for change-event tracking
;; ---------------------------------------------------------------------------
(defn- handle-focusin! [^js el ^js _evt]
  (when (nil? (du/getv el k-focus-val))
    (du/setv! el k-focus-val (or (du/get-attr el model/attr-value) ""))))

(defn- emit-change-if-left! [^js el]
  (let [^js active (.-activeElement js/document)]
    (when (not= active el)
      (let [old-val (du/getv el k-focus-val)
            new-val (or (du/get-attr el model/attr-value) "")
            m       (du/getv el k-model)
            length  (:length m)]
        (when (and (string? old-val) (not= old-val new-val))
          (du/dispatch! el model/event-change
                        #js {:name     (name-for-event el)
                             :value    new-val
                             :complete (model/complete? new-val length)}))
        (du/setv! el k-focus-val nil)))))

(defn- handle-focusout! [^js el ^js _evt]
  (js/setTimeout (fn [] (emit-change-if-left! el)) 0))

;; ---------------------------------------------------------------------------
;; Listener management (delegation on the root container + host)
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js root (gobj/get refs "root")
          input-h  (fn [^js e] (handle-input!    el e))
          key-h    (fn [^js e] (handle-keydown!  el e))
          paste-h  (fn [^js e] (handle-paste!    el e))
          fin-h    (fn [^js e] (handle-focusin!  el e))
          fout-h   (fn [^js e] (handle-focusout! el e))]
      (.addEventListener root "input"    input-h)
      (.addEventListener root "keydown"  key-h)
      (.addEventListener root "paste"    paste-h)
      (.addEventListener root "focusin"  fin-h)
      (.addEventListener root "focusout" fout-h)
      (du/setv! el k-handlers
                #js {:input    input-h
                     :keydown  key-h
                     :paste    paste-h
                     :focusin  fin-h
                     :focusout fout-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js root (gobj/get refs "root")]
        (.removeEventListener root "input"    (gobj/get handlers "input"))
        (.removeEventListener root "keydown"  (gobj/get handlers "keydown"))
        (.removeEventListener root "paste"    (gobj/get handlers "paste"))
        (.removeEventListener root "focusin"  (gobj/get handlers "focusin"))
        (.removeEventListener root "focusout" (gobj/get handlers "focusout")))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (when-not (.-shadowRoot el)
    (let [root      (.attachShadow el #js {:mode "open"})
          style-el  (.createElement js/document "style")
          group-el  (.createElement js/document "div")]
      (set! (.-textContent style-el) style-text)
      (du/set-attr! group-el "part" "root")
      (du/set-attr! group-el "role" "group")
      (.appendChild root style-el)
      (.appendChild root group-el)
      (du/setv! el k-refs #js {:root group-el}))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  ;; set-bool-attr! triggers attributeChangedCallback → update-from-attrs!
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?)))

(defn- form-reset! [^js el]
  ;; remove-attr! triggers attributeChangedCallback → update-from-attrs!
  (du/remove-attr! el model/attr-value))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el)
  (when (du/has-attr? el model/attr-autofocus)
    (focus-first-empty! el)))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new-val]
  (update-from-attrs! el))

;; ---------------------------------------------------------------------------
;; Property accessors and methods
;; ---------------------------------------------------------------------------
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (aset proto "focus"
        (fn []
          (this-as ^js this (focus-first-empty! this))))
  (aset proto "clear"
        (fn []
          (this-as ^js this
                   ;; remove-attr! triggers attributeChangedCallback → update-from-attrs!
                   (du/remove-attr! this model/attr-value)
                   (focus-slot! this 0))))
  (aset proto "checkValidity"
        (fn []
          (this-as ^js this
                   (if-let [^js internals (du/getv this k-internals)]
                     (.checkValidity internals)
                     true))))
  (aset proto "reportValidity"
        (fn []
          (this-as ^js this
                   (if-let [^js internals (du/getv this k-internals)]
                     (.reportValidity internals)
                     true)))))

;; ---------------------------------------------------------------------------
;; Element registration
;; ---------------------------------------------------------------------------
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :form-associated?     true
     :form-disabled-fn     form-disabled!
     :form-reset-fn        form-reset!
     :setup-prototype-fn   install-property-accessors!}))
