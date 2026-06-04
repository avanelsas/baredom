(ns demo-app.core
  "BareBuild Tasks demo — Phase 4 read-only E2E demo + write-side telemetry.

  Orchestrator only: register every dogfooded component, then run each route's
  read-side wiring (board / detail / settings). Registration happens AFTER wiring
  so a deep-load straight to a route still delivers the initial route-change the
  listeners depend on. The actual rendering and read logic lives in the per-route
  namespaces; the write surfaces those namespaces build are wired by hand in
  write-side (the Phase-4 telemetry seam)."
  (:require
   [demo-app.wiring     :as w]
   [demo-app.dom        :as dom]
   [demo-app.board      :as board]
   [demo-app.detail     :as detail]
   [demo-app.settings   :as settings]
   [demo-app.write-side :as write-side]
   ;; Orchestration elements
   ["@vanelsas/baredom/barebuild-router" :as barebuild-router]
   ["@vanelsas/baredom/barebuild-route"  :as barebuild-route]
   ["@vanelsas/baredom/barebuild-data"   :as barebuild-data]
   ;; Write-side ALPHA (4.0.0-alpha) — declarative submit→fetch + invalidation
   ["@vanelsas/baredom/barebuild-action"        :as barebuild-action]
   ["@vanelsas/baredom/barebuild-invalidate-on" :as barebuild-invalidate-on]
   ;; UI components (dogfood)
   ["@vanelsas/baredom/x-navbar"          :as x-navbar]
   ["@vanelsas/baredom/x-table"           :as x-table]
   ["@vanelsas/baredom/x-table-row"       :as x-table-row]
   ["@vanelsas/baredom/x-table-cell"      :as x-table-cell]
   ["@vanelsas/baredom/x-stat"            :as x-stat]
   ["@vanelsas/baredom/x-badge"           :as x-badge]
   ["@vanelsas/baredom/x-button"          :as x-button]
   ["@vanelsas/baredom/x-search-field"    :as x-search-field]
   ["@vanelsas/baredom/x-select"          :as x-select]
   ["@vanelsas/baredom/x-modal"           :as x-modal]
   ["@vanelsas/baredom/x-form"            :as x-form]
   ["@vanelsas/baredom/x-form-field"      :as x-form-field]
   ["@vanelsas/baredom/x-text-area"       :as x-text-area]
   ["@vanelsas/baredom/x-date-picker"     :as x-date-picker]
   ["@vanelsas/baredom/x-card"            :as x-card]
   ["@vanelsas/baredom/x-alert"           :as x-alert]
   ["@vanelsas/baredom/x-skeleton"        :as x-skeleton]
   ["@vanelsas/baredom/x-cancel-dialogue" :as x-cancel-dialogue]
   ["@vanelsas/baredom/x-toast"           :as x-toast]
   ["@vanelsas/baredom/x-toaster"         :as x-toaster]))

(defn- register-components!
  "Each imported module's init() calls customElements.define for its tag. Order
  among them does not matter: the router defers its initial route-change push to a
  microtask, so every element is upgraded before any cross-component cascade fires
  (see barebuild-router's on-route-mounted). What matters is only that init! wires
  the listeners BEFORE calling this, so a deep-load still delivers that push."
  []
  (doseq [^js m [barebuild-router barebuild-route barebuild-data
                 barebuild-action barebuild-invalidate-on
                 x-navbar x-table x-table-row x-table-cell x-stat x-badge
                 x-button x-search-field x-select x-modal x-form x-form-field
                 x-text-area x-date-picker x-card x-alert x-skeleton
                 x-cancel-dialogue x-toast x-toaster]]
    (.init m)))

(defn- init-backend-select!
  "Wire the navbar backend picker (#backend-select): populate it from the data-driven
  w/backends, reflect the active choice, and on a change persist + reload — the brokers
  re-fetch from the new backend cleanly on load. This is the demo's interactive proof of
  server-agnosticism (swap the server, the same app works against it). The change guard
  ignores the upgrade's echo of the current value, so only a real switch reloads.

  Set before component registration (the picker is parsed but not upgraded): options +
  the value ATTRIBUTE are read by x-select on upgrade, and the listener is already live."
  []
  (when-let [^js sel (.querySelector js/document "#backend-select")]
    (dom/fill-options! sel w/backends nil)
    (.setAttribute sel "value" (:value w/active-backend))
    (.addEventListener sel w/ev-select-change
                       (fn on-backend-change [^js e]
                         (let [v (.. e -detail -value)]
                           (when (not= v (:value w/active-backend))
                             (w/select-backend! v)
                             (.reload (.. js/window -location))))))))

(defn init! []
  (board/init-board!)
  (detail/init-detail!)
  (settings/init-settings!)
  ;; Live write-side handlers — create / update / delete / settings (see write_side.cljs).
  (write-side/attach-write-handlers!)
  (w/capture-backend!)        ; persist a valid ?backend deep-link so the choice sticks
  (init-backend-select!)
  (register-components!))

(defn reload! []
  ;; Hot-reload hook (shadow :after-load). Elements are already registered and the
  ;; listeners live on the persistent route elements, so there is nothing to redo.
  nil)
