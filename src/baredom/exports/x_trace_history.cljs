(ns baredom.exports.x-trace-history
  "ESM entry point for x-trace-history, BareDOM's dev-only debugger.

   Unlike component exports, this module exposes no public-api map: the
   dock is not a consumer-facing component, has no model.cljs under
   components/, and contributes nothing to custom-elements.json. Its
   surface is the JS API at window.BareDOM.traceHistory.* (typed in
   dist/x-trace-history.d.ts).

   register! is idempotent and gates on model/enabled? inside
   recorder/register!, so loading this module in a consumer app costs a
   single boolean check when ?baredom-trace-history is absent."
  (:require [baredom.dev.x-trace-history.x-trace-history :as x-trace-history]))

(defn register! []
  (x-trace-history/register!))

(defn ^:export init []
  (register!))
