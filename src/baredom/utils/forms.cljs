(ns baredom.utils.forms
  "Shared form-control policy for form-associated components: ElementInternals
   validity + form value, and the inline error-display recipe.

   The two decisions here — constraint `validity` and the `error-describedby`
   token — are pure functions, separated from their effects (`set-validity!` /
   `sync!` / `apply-error-display!`), so the policy that maps a component's
   `error` / `required` / emptiness onto native validity and invalid markers
   lives in one place and is unit-testable.

   A component supplies only its genuine per-control variation — what counts as
   *empty*, which element is the validity *anchor*, its *value* projection, any
   non-default *missing message*, and (for describedby) any author-supplied id —
   and composes this policy."
  (:require [baredom.utils.dom :as du]
            [baredom.utils.model :as mu]))

(def default-value-missing "Please fill in this field.")

(defn validity
  "Pure: the standard BareDOM constraint-validation projection.

   Returns a map `{:flags <#js validity-state> :message <string>}` ready for
   `ElementInternals.setValidity`. Precedence: a present `error` with a message wins as a
   `customError`; otherwise a `required?` control that is `empty?` reports
   `valueMissing`; otherwise the field is valid (empty flags + empty message)."
  [{:keys [has-error? error required? empty? missing-message]}]
  (cond
    (and has-error? (seq error))
    {:flags #js {:customError true}  :message error}
    (and required? empty?)
    {:flags #js {:valueMissing true} :message (or missing-message default-value-missing)}
    :else
    {:flags #js {} :message ""}))

(defn set-validity!
  "Effect: apply the standard `validity` projection to `internals`, anchored at
   `anchor`. No-op when `internals` is nil (older browsers / attachInternals
   absent). `inputs` is the map accepted by `validity`."
  [^js internals ^js anchor inputs]
  (when internals
    (let [{:keys [flags message]} (validity inputs)]
      (.setValidity internals flags message anchor))))

(defn sync!
  "Effect: push `value` into the form and apply the standard validity in one
   call. No-op when `internals` is nil. `inputs` is the map accepted by
   `validity`."
  [^js internals ^js anchor value inputs]
  (when internals
    (.setFormValue internals value)
    (set-validity! internals anchor inputs)))

(def ^:private selector-disabled ":disabled")

(defn disabled?
  "True when `el` is disabled by its own attribute or by a fieldset."
  [^js el]
  (.matches el selector-disabled))

;; ── Native-like validation API ───────────────────────────────────────────────
(defn- define-getter!
  [^js proto prop-name getter-fn]
  (.defineProperty js/Object proto prop-name
                   #js {:get getter-fn :configurable true}))

(defn- define-method!
  [^js proto method-name method-fn]
  (.defineProperty js/Object proto method-name
                   #js {:value method-fn :writable true :configurable true}))

(defn install-validity-api!
  "Install the native form-control validation surface on `proto`, delegating to
   the ElementInternals each instance stashes under `k-internals`: `validity`,
   `validationMessage`, `willValidate`, `form` and `labels` as read-only
   getters, plus `checkValidity()` and `reportValidity()`.

   ElementInternals never mirrors any of this onto the host element — a
   form-associated custom element has to expose it itself, or consumers can only
   observe validity through a submit attempt. Where `attachInternals` is
   unavailable the members fall back to the shape of a control that never blocks
   submission, so feature-detecting callers see a consistent surface."
  [^js proto k-internals]
  (define-getter! proto "validity"
    (fn validity-getter []
      (this-as ^js this
        (when-let [^js internals (du/getv this k-internals)]
          (.-validity internals)))))
  (define-getter! proto "validationMessage"
    (fn validation-message-getter []
      (this-as ^js this
        (if-let [^js internals (du/getv this k-internals)]
          (.-validationMessage internals)
          ""))))
  (define-getter! proto "willValidate"
    (fn will-validate-getter []
      (this-as ^js this
        (if-let [^js internals (du/getv this k-internals)]
          (.-willValidate internals)
          false))))
  (define-getter! proto "form"
    (fn form-getter []
      (this-as ^js this
        (when-let [^js internals (du/getv this k-internals)]
          (.-form internals)))))
  (define-getter! proto "labels"
    (fn labels-getter []
      (this-as ^js this
        (when-let [^js internals (du/getv this k-internals)]
          (.-labels internals)))))
  (define-method! proto "checkValidity"
    (fn check-validity []
      (this-as ^js this
        (if-let [^js internals (du/getv this k-internals)]
          (.checkValidity internals)
          true))))
  (define-method! proto "reportValidity"
    (fn report-validity []
      (this-as ^js this
        (if-let [^js internals (du/getv this k-internals)]
          (.reportValidity internals)
          true)))))

;; ── Inline error display ─────────────────────────────────────────────────────
(def ^:private error-id             "error")
(def ^:private cls-error-hidden     "error-hidden")
(def ^:private attr-data-invalid    "data-invalid")
(def ^:private attr-aria-invalid    "aria-invalid")
(def ^:private attr-aria-describedby "aria-describedby")

(defn error-describedby
  "Pure: the `aria-describedby` value for a control's interactive element — the
   inline-error id when an error is present, appended after an optional
   author-supplied id list. Returns nil when there is neither an error nor an
   author value (the caller then removes the attribute)."
  [has-error? author]
  (let [author (when (and (string? author) (not= author "")) author)]
    (cond
      (and author has-error?) (str author " " error-id)
      has-error?              error-id
      author                  author
      :else                   nil)))

(defn apply-error-display!
  "Effect: render a control's inline validation error and its invalid markers,
   from `{:error :has-error?}`:
     - set the `[part=error]` span text and toggle its `error-hidden` class,
     - set/remove `data-invalid` on the host `el`,
     - set `aria-invalid` on the `anchor`,
     - set `aria-describedby` on the `anchor` to `describedby`, or remove it when
       `describedby` is nil.
   `describedby` is passed in (see `error-describedby`) because the token set —
   error only, or author-supplied ids plus error — is the caller's to decide."
  [^js el ^js anchor ^js error-el {:keys [error has-error?]} describedby]
  (set! (.-textContent error-el) (or error ""))
  (if has-error?
    (.remove (.-classList error-el) cls-error-hidden)
    (.add    (.-classList error-el) cls-error-hidden))
  (du/set-bool-attr! el attr-data-invalid has-error?)
  (du/set-attr! anchor attr-aria-invalid (if has-error? "true" "false"))
  (if describedby
    (du/set-attr!    anchor attr-aria-describedby describedby)
    (du/remove-attr! anchor attr-aria-describedby)))

;; ── Slotted labels ───────────────────────────────────────────────────────────
;; Elements in a slotted label that keep their own click.
(def ^:private interactive-selector
  "a[href],button,input,select,textarea,summary,[contenteditable='true'],[contenteditable='']")

(def ^:private ev-slotchange "slotchange")
(def ^:private attr-part     "part")
(def ^:private attr-id       "id")

(defn label-rules
  "The CSS of a slotted label in part `part`, dimmed by `opacity-var` when disabled.
   The host sets `data-labelled` and `data-disabled`."
  [part opacity-var]
  (str
   "[part=" part "]{"
   "cursor:pointer;"
   "}"
   ":host(:not([data-labelled])) [part=" part "]{"
   "display:none;"
   "}"
   ":host([data-disabled]) [part=" part "]{"
   "cursor:default;"
   "opacity:var(" opacity-var ");"
   "}"))

(defn make-label-section!
  "Creates the label span of part `part` with a default slot in it.
   Returns `{:label span :slot slot}`. The caller appends the span to its shadow root."
  [part]
  (let [label-el (.createElement js/document "span")
        slot-el  (.createElement js/document "slot")]
    (du/set-attr! label-el attr-part part)
    (du/set-attr! label-el attr-id   mu/label-id)
    (.appendChild label-el slot-el)
    {:label label-el :slot slot-el}))

(defn watch-label-slot!
  "Calls `on-change` when the content of `slot-el` changes. Call once per slot."
  [^js slot-el on-change]
  (.addEventListener slot-el ev-slotchange (fn handle-slotchange [_evt] (on-change))))

(defn- content-node?
  "True for an element, or for a text node with visible text."
  [^js node]
  (let [t (.-nodeType node)]
    (or (= t js/Node.ELEMENT_NODE)
        (and (= t js/Node.TEXT_NODE)
             (not= "" (.trim (.-textContent node)))))))

(defn slot-has-content?
  "True when the light DOM assigns content to `slot-el`."
  [^js slot-el]
  (boolean (some content-node? (array-seq (.assignedNodes slot-el)))))

(defn- path-inside-host
  "The nodes of the event path that come before the host."
  [^js el ^js evt]
  (take-while (fn before-host? [node] (not (identical? node el)))
              (array-seq (.composedPath evt))))

(defn- interactive-node?
  "True for an interactive element other than the control."
  [^js control ^js node]
  (and (= (.-nodeType node) js/Node.ELEMENT_NODE)
       (not (identical? node control))
       (.matches node interactive-selector)))

(defn label-activation?
  "True when a click on the host `el` acts on `control`, as a click on a native label does.
   A click from a link, a button or a form control in the label does not."
  [^js el ^js control ^js evt]
  (not (some (partial interactive-node? control) (path-inside-host el evt))))

(defn focus-quietly!
  "Focuses `control` without a focus ring."
  [^js control]
  (.focus control #js {:focusVisible false}))
