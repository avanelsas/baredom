(ns baredom.components.x-theme.x-theme
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-theme.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs  "__xThemeRefs")
(def ^:private k-model "__xThemeModel")

;; ── Attribute reader ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  {:preset (du/get-attr el model/attr-preset)})

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (.appendChild root style)
    (.appendChild root slot)
    (du/setv! el k-refs #js {:style style})))

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [preset] :as m}]
  (let [^js refs (du/getv el k-refs)]
    (when refs
      (let [^js style-el (.-style refs)]
        (set! (.-textContent style-el) (model/preset->css preset))
        (du/setv! el k-model m)))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (init-dom! el))
  (update-from-attrs! el))

(defn- disconnected! [^js _el])

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessor ───────────────────────────────────────────────────────
(defn- install-preset-property! [^js proto]
  (du/define-string-prop! proto "preset" model/attr-preset))

;; ── Element class ───────────────────────────────────────────────────────────
;; ── Registration ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-preset-property!
     }))
