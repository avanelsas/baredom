(ns baredom.utils.forms
  "Shared ElementInternals form-participation policy for form-associated
   components.

   The constraint-validation *decision* (`validity`) is a pure function,
   separated from the *effect* (`set-validity!` / `sync!`), so the policy that
   maps a component's `error` / `required` / emptiness onto native validity
   lives in exactly one place and is unit-testable on its own — the browser
   test harness cannot exercise the ElementInternals effect, but it can test
   this projection.

   A component supplies only its genuine per-control variation — what counts as
   *empty*, which element is the validity *anchor*, its *value* projection, and
   (when non-default) its *missing message* — and composes this policy.")

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
