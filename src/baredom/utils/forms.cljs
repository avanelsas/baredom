(ns baredom.utils.forms
  "Shared form-control policy for form-associated components: ElementInternals
   validity + form value, and the inline error-display recipe.

   The two decisions here — constraint `validity` and the `error-describedby`
   token — are pure functions, separated from their effects (`set-validity!` /
   `sync!` / `apply-error-display!`), so the policy that maps a component's
   `error` / `required` / emptiness onto native validity and invalid markers
   lives in one place and is unit-testable — the browser test harness cannot
   exercise the ElementInternals / DOM effects, but it can test the projections.

   A component supplies only its genuine per-control variation — what counts as
   *empty*, which element is the validity *anchor*, its *value* projection, any
   non-default *missing message*, and (for describedby) any author-supplied id —
   and composes this policy."
  (:require [baredom.utils.dom :as du]))

(def default-value-missing "Please fill in this field.")

(defn validity
  "Pure: the standard BareDOM constraint-validation projection.

   Returns a map `{:flags <#js validity-state> :message <string>}` ready for
   `ElementInternals.setValidity`. Precedence: a present `error` wins as a
   `customError`; otherwise a `required?` control that is `empty?` reports
   `valueMissing`; otherwise the field is valid (empty flags + empty message)."
  [{:keys [has-error? error required? empty? missing-message]}]
  (cond
    has-error?
    {:flags #js {:customError true}  :message (or error "")}
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
