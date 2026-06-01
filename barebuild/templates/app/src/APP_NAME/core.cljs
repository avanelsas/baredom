(ns APP_NAME.core
  (:require
   [goog.object :as gobj]
   ["@vanelsas/baredom/barebuild-router" :as barebuild-router]
   ["@vanelsas/baredom/barebuild-route"  :as barebuild-route]
   ["@vanelsas/baredom/barebuild-data"   :as barebuild-data]
   ["@vanelsas/baredom/x-table"          :as x-table]
   ["@vanelsas/baredom/x-table-row"      :as x-table-row]
   ["@vanelsas/baredom/x-table-cell"     :as x-table-cell]))

(defn- register-components!
  "Each imported module's init() calls customElements.define for its tag. Order
  among them does not matter — the router defers its initial route-change push to a
  microtask, so every element is upgraded before any cross-component cascade fires.
  What matters is that init! wires the listeners BEFORE calling this, so a deep load
  still delivers that initial push."
  []
  (.init barebuild-router)
  (.init barebuild-route)
  (.init barebuild-data)
  (.init x-table)
  (.init x-table-row)
  (.init x-table-cell))

;; ── Render the fetched value into the table — DOM = f(server data). ──────────
;; The rows are a projection of the server value, rebuilt from it; the <x-table>
;; element itself persists (the route never destroys its slotted body).
(def ^:private columns ["id" "name" "role"])
(def ^:private headers ["ID" "Name" "Role"])

(defn- cell [text header?]
  (let [c (.createElement js/document "x-table-cell")]
    (when header? (.setAttribute c "type" "header"))
    (set! (.-textContent c) (str text))
    c))

(defn- append-row! [^js table cells]
  (let [r (.createElement js/document "x-table-row")]
    (doseq [c cells] (.appendChild r c))
    (.appendChild table r)))

(defn- render-users! [^js table users]
  (set! (.-innerHTML table) "")
  (append-row! table (map #(cell % true) headers))
  (doseq [u users]
    (append-row! table (map #(cell (gobj/get u %) false) columns))))

;; ── Read-side wiring (see https://github.com/... barebuild/docs/read-side.md). ──
;; Handles are captured once; identity preservation keeps them valid across
;; navigation. This hand-wiring is intentional V1 friction — the declarative
;; wiring elements (<barebuild-bind>) are V1.1, designed from what users build here.
(defn init! []
  (let [route (.querySelector js/document "barebuild-route[path='/users']")
        data  (.querySelector route "barebuild-data")
        table (.querySelector route "x-table")]
    ;; WHEN to read is an explicit value-in-time decision: set src when /users is
    ;; the active match (re-reads on each return). route-change is pushed to every
    ;; route on every resolution, so gate on the path.
    (.addEventListener route "barebuild-route-change"
                       (fn [^js e]
                         (when (= "/users" (.. e -detail -path))
                           ;; Points at the static stub file served by dev-http
                           ;; (public/api/users.json). For a real backend, change
                           ;; this to your endpoint, e.g. "/api/users".
                           (set! (.-src data) "/api/users.json"))))
    ;; Read the delivered value from event.detail.state — a plain JS object
    ;; { phase, data, error, httpStatus } (so any consumer, including this
    ;; separately-compiled app, can read it). Render the rows when phase is "loaded".
    (.addEventListener route "barebuild-data-state"
                       (fn [^js e]
                         (let [^js state (.. e -detail -state)]
                           (when (= "loaded" (.-phase state))
                             (render-users! table (.-data state))))))
    ;; Register AFTER wiring so a deep-load straight to /users still delivers the
    ;; initial route-change our listener depends on.
    (register-components!)))

(defn reload! []
  ;; Hot-reload hook (shadow :after-load). Elements are already registered and the
  ;; listeners live on the persistent route element, so there is nothing to redo.
  nil)
