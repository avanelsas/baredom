(ns baredom.exports.x-trace-history
  "ESM entry point for x-trace-history, BareDOM's dev-only debugger.

   Unlike component exports, this module exposes no public-api map: the
   dock is not a consumer-facing component, has no model.cljs under
   components/, and contributes nothing to custom-elements.json. Its
   surface is the JS API at window.BareDOM.traceHistory.* (typed in
   dist/x-trace-history.d.ts).

   register! is idempotent and gates on model/enabled? inside
   recorder/register!, so loading this module in a consumer app costs a
   single boolean check when ?baredom-trace-history is absent.

   Auto-invoked on module load: components (x-button, x-card, etc.)
   ship a per-component React/Angular wrapper that calls init() during
   its own load, so the framework's import statement implicitly
   activates the component. The trace-history dock has no wrapper
   (it's a dev tool, not a consumer-facing component), so a plain
   `import \"@vanelsas/baredom/x-trace-history\"` side-effect import
   has to do the activation itself. Calling register! at the
   namespace level achieves that: the dock self-activates on the
   first import, idempotent on repeat imports."
  (:require [baredom.dev.x-trace-history.x-trace-history :as x-trace-history]))

(defn register! []
  (x-trace-history/register!))

(defn ^:export init []
  (register!))

;; Self-activate on module load — see the namespace docstring for why
;; this differs from component exports. register! is idempotent + gated
;; by model/enabled? so the cost when the URL flag is absent is one
;; boolean check.
(register!)
