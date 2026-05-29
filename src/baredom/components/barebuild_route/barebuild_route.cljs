(ns baredom.components.barebuild-route.barebuild-route
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.barebuild-route.model :as model]
   [baredom.components.barebuild-router.model :as router-model]))

;; Effect shell for barebuild-route. Passive child: it registers with its
;; ancestor router by a bubbling event, is subscribed by being dispatched *at*,
;; and owns ITS OWN visibility. It never walks up the DOM to find the router and
;; holds no reference to it — registration flows up by event, the match flows
;; down by targeted dispatch. The slotted body is built once at connect and is
;; never destroyed across activation transitions (identity preservation): the
;; route only toggles its own `display`.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized?   "__barebuildRouteInit")
(def ^:private k-pattern        "__barebuildRoutePattern")        ; parsed path pattern (value)
(def ^:private k-change-handler "__barebuildRouteChangeHandler")  ; stashed self-listener

;; ── String-literal constants ───────────────────────────────────────────────────
(def ^:private styles          ":host{display:contents}")
(def ^:private display-none    "none")
(def ^:private display-default "")   ; clears the inline override → reverts to :host{display:contents}

;; ── Shadow DOM ──────────────────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) styles)
    (.appendChild root style)
    (.appendChild root slot)
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

;; ── Visibility (the route's own effect, gated by a received value) ─────────────
(defn- set-visible! [^js el visible?]
  (set! (.. el -style -display) (if visible? display-default display-none)))

(defn- parse-own-pattern! [^js el]
  (du/setv! el k-pattern (router-model/parse-path-pattern (du/get-attr el model/attr-path))))

(defn- on-route-change [^js el ^js e]
  (let [path    (.. e -detail -path)
        pattern (du/getv el k-pattern)]
    (set-visible! el (some? (router-model/match-path pattern path)))))

;; ── Self-listener (GC'd with the element; nothing foreign to tear down) ─────────
(defn- add-self-listener! [^js el]
  (let [h (fn route-on-change [e] (on-route-change el e))]
    (du/setv! el k-change-handler h)
    (.addEventListener el model/event-route-change h)))

(defn- remove-self-listener! [^js el]
  (when-let [h (du/getv el k-change-handler)]
    (.removeEventListener el model/event-route-change h)
    (du/setv! el k-change-handler nil)))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (parse-own-pattern! el)
  ;; Start hidden; the router reveals the matching route via the match it pushes
  ;; on registration. No all-routes-visible flash before the first resolution.
  (set-visible! el false)
  (when-not (du/getv el k-change-handler)
    (add-self-listener! el))
  (du/dispatch! el model/event-route-mounted #js {}))

(defn- disconnected! [^js el]
  (du/dispatch! el model/event-route-unmounted #js {})
  (remove-self-listener! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val) (du/initialized? el k-initialized?))
    (parse-own-pattern! el)
    ;; Re-register: the router's mounted handler is idempotent and re-resolves,
    ;; pushing the current match back so visibility reflects the new pattern.
    (du/dispatch! el model/event-route-mounted #js {})))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
