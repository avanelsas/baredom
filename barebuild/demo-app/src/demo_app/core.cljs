(ns demo-app.core
  "BareBuild Tasks demo — Phase 4 read-only E2E demo + write-side telemetry.

  Orchestrator only: register every dogfooded component, then run each route's
  read-side wiring (board / detail / settings). Registration happens AFTER wiring
  so a deep-load straight to a route still delivers the initial route-change the
  listeners depend on. The actual rendering and read logic lives in the per-route
  namespaces; the write surfaces those namespaces build are wired by hand in a
  later step (the Phase-4 telemetry seam)."
  (:require
   [demo-app.board      :as board]
   [demo-app.detail     :as detail]
   [demo-app.settings   :as settings]
   [demo-app.write-side :as write-side]
   ;; Orchestration elements
   ["@vanelsas/baredom/barebuild-router" :as barebuild-router]
   ["@vanelsas/baredom/barebuild-route"  :as barebuild-route]
   ["@vanelsas/baredom/barebuild-data"   :as barebuild-data]
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
  (.init barebuild-router)
  (.init barebuild-route)
  (.init barebuild-data)
  (.init x-navbar)
  (.init x-table)
  (.init x-table-row)
  (.init x-table-cell)
  (.init x-stat)
  (.init x-badge)
  (.init x-button)
  (.init x-search-field)
  (.init x-select)
  (.init x-modal)
  (.init x-form)
  (.init x-form-field)
  (.init x-text-area)
  (.init x-date-picker)
  (.init x-card)
  (.init x-alert)
  (.init x-skeleton)
  (.init x-cancel-dialogue)
  (.init x-toast)
  (.init x-toaster))

(defn init! []
  (board/init-board!)
  (detail/init-detail!)
  (settings/init-settings!)
  ;; Inert write-side seams — the Phase-4 telemetry surface (see write_side.cljs).
  (write-side/attach-stubs!)
  (register-components!))

(defn reload! []
  ;; Hot-reload hook (shadow :after-load). Elements are already registered and the
  ;; listeners live on the persistent route elements, so there is nothing to redo.
  nil)
