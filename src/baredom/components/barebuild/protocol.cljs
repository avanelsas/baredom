(ns baredom.components.barebuild.protocol)

;; Single source of truth for the cross-component HANDSHAKE of the BareBuild
;; server-state family — the event names AND the shared `.state` field key. These
;; strings are the wire between independently shipped ESM modules: barebuild-action
;; emits `event-action-state` and barebuild-data emits `event-data-state`;
;; barebuild-invalidate-on emits `event-invalidate` which barebuild-data listens
;; for. Each module ships separately, so a rename has to land on every side at once
;; — declaring the strings here once makes that handshake compile-coupled instead of
;; hand-kept copies that drift silently (build stays green, invalidation just stops
;; working at runtime). Each model.cljs re-exports the names it uses so component
;; code keeps reading `model/…`.

(def event-data-state   "barebuild-data-state")
(def event-action-state "barebuild-action-state")
(def event-invalidate   "barebuild-invalidate")

;; The `.state` object's shared field key. Both barebuild-data (loaded/error) and
;; barebuild-action (success/error) stamp `httpStatus` onto their state via gobj/set
;; (a string key — `.state` is a plain JS object); declaring it here keeps the two
;; model constructors from re-spelling the literal independently.
(def field-http-status "httpStatus")
