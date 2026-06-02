(ns baredom.components.barebuild-invalidate-on.barebuild-invalidate-on
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
   [baredom.components.barebuild-invalidate-on.model :as model]))

;; Effect shell for barebuild-invalidate-on. Holds only its attributes. Listens to
;; its parentNode (the source, by containment — no selectors) for the configured
;; `event`; on a when-phase/when-name match it dispatches a document-level bubbling
;; `barebuild-invalidate {:src}`. A matching <barebuild-data> self-matches by URL.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized? "__barebuildInvalidateOnInit")
(def ^:private k-handler      "__barebuildInvalidateOnHandler")    ; stable source listener
(def ^:private k-source-node  "__barebuildInvalidateOnSource")     ; node we attached to (for removal)
(def ^:private k-bound-event  "__barebuildInvalidateOnBoundEvent") ; event name currently bound

(def ^:private styles ":host{display:none}")

(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? false))

;; ── Matching ────────────────────────────────────────────────────────────────────
(defn- matches?
  "True when the configured matchers all fire on `ev`. when-phase compares
  detail.state.phase; when-name compares detail.name. At least one matcher must be
  set (both unset → log + no-match); set matchers are AND-composed."
  [^js el ^js ev]
  (let [when-phase  (du/get-attr el model/attr-when-phase)
        when-name   (du/get-attr el model/attr-when-name)
        phase-set?  (mu/non-empty-string? when-phase)
        name-set?   (mu/non-empty-string? when-name)]
    (if-not (or phase-set? name-set?)
      (do (js/console.error "barebuild-invalidate-on: needs at least one of when-phase / when-name; no-op")
          false)
      (let [^js detail (.-detail ev)
            phase-ok   (or (not phase-set?) (= when-phase (some-> detail .-state .-phase)))
            name-ok    (or (not name-set?)  (= when-name  (some-> detail .-name)))]
        (and phase-ok name-ok)))))

(defn- do-invalidate! [^js el]
  (du/dispatch! el model/event-invalidate #js {:src (du/get-attr el model/attr-src)}))

(defn- on-source-event [^js el ^js ev]
  (when (matches? el ev)
    (do-invalidate! el)))

;; ── Source wiring (containment; no selectors) ────────────────────────────────────
(defn- ensure-handler! [^js el]
  (or (du/getv el k-handler)
      (let [h (fn invalidate-on-source [^js ev] (on-source-event el ev))]
        (du/setv! el k-handler h)
        h)))

(defn- detach! [^js el]
  (let [^js h    (du/getv el k-handler)
        ^js node (du/getv el k-source-node)
        bound    (du/getv el k-bound-event)]
    (when (and h node (mu/non-empty-string? bound))
      (.removeEventListener node bound h)
      (du/setv! el k-source-node nil)
      (du/setv! el k-bound-event nil))))

(defn- bind-source!
  "(Re)bind the listener to parentNode for the current `event` name. An orphan
  (no parentNode) logs once and no-ops — the element is a child by construction."
  [^js el]
  (detach! el)
  (let [^js parent (.-parentNode el)
        evt        (let [e (du/get-attr el model/attr-event)]
                     (if (mu/non-empty-string? e) e model/default-source-event))]
    (if (nil? parent)
      (js/console.warn "barebuild-invalidate-on: no parentNode source; no-op")
      (do (.addEventListener parent evt (ensure-handler! el))
          (du/setv! el k-source-node parent)
          (du/setv! el k-bound-event evt)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (bind-source! el))

(defn- disconnected! [^js el]
  (detach! el))

(defn- attribute-changed! [^js el name _old-val _new-val]
  (when (and (du/initialized? el k-initialized?) (.-isConnected el)
             (= name model/attr-event))
    (bind-source! el)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; Manual trigger: dispatch unconditionally, bypassing the matchers + listener.
  (aset proto "invalidate" (fn bio-invalidate [] (this-as ^js el (do-invalidate! el)))))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
