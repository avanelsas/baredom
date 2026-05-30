(ns baredom.components.barebuild-route.model
  (:require [baredom.components.barebuild-router.model :as router-model]))

;; barebuild-route is the passive child of barebuild-router. It holds a single
;; path pattern (parsed once into a value) and owns its own visibility. The
;; routing *calculation* (parse-path-pattern / match-path) is not duplicated
;; here — it is the router model's, and this namespace requires it. The router
;; and route are a tight pair, like x-tab / x-tabs.

(def tag-name "barebuild-route")

(def attr-path "path")

(def observed-attributes #js [attr-path])

;; Event names are single-sourced from the router model so the two components
;; can never drift on the strings they exchange.
(def event-route-change    router-model/event-route-change)
(def event-route-mounted   router-model/event-route-mounted)
(def event-route-unmounted router-model/event-route-unmounted)

(def property-api
  {:path {:type 'string :reflects-attribute attr-path :default ""}})

(def event-schema
  {event-route-mounted   {:detail {}}
   event-route-unmounted {:detail {}}})

(def method-api {})
