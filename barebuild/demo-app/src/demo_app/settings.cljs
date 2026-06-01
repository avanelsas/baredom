(ns demo-app.settings
  "Read-side wiring for the /settings route — a non-list read that fills a form
  from GET /api/settings. The Save submit is the Phase-4 stub seam."
  (:require [goog.object :as gobj]
            [demo-app.dom :as dom]
            [demo-app.wiring :as w]))

(def ^:private fields ["theme" "page-size" "default-status"])

(defn- fill-form! [^js form ^js s]
  (doseq [field fields]
    (when-let [^js f (.querySelector form (str "[name='" field "']"))]
      (set! (.-value f) (str (or (gobj/get s field) ""))))))

;; ── Handlers ─────────────────────────────────────────────────────────────────
;; Named, event-only handlers (resolve handles from `currentTarget`), so
;; `init-settings!` reads as a wiring list — matching write_side.cljs.

(defn- on-route-change [^js e]
  (when (= w/path-settings (.. e -detail -path))
    (let [^js route (.-currentTarget e)]
      (set! (.-src (.querySelector route "#settings-data")) "/api/settings"))))

(defn- on-data-state [^js e]
  (let [^js route (.-currentTarget e)
        ^js state (.. e -detail -state)
        phase     (.-phase state)
        ^js err   (.querySelector route "#settings-error")
        ^js form  (.querySelector route w/id-settings-form)]
    (dom/show! err (= "error" phase))
    (when (= "error" phase)
      (.setAttribute err "text" (str "Couldn't load settings (" (.-httpStatus state) ").")))
    (when (= "loaded" phase)
      (fill-form! form (.-data state)))))

(defn init-settings! []
  (let [^js route (.querySelector js/document (w/route-selector w/path-settings))]
    (.addEventListener route w/ev-route-change on-route-change)
    (.addEventListener route w/ev-data-state   on-data-state)))
