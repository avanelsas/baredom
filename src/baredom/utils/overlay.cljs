(ns baredom.utils.overlay
  "Shared overlay root management for portal-based components.
   Provides document-level fixed layers that escape CSS stacking contexts."
  (:require [goog.object :as gobj]))

(def ^:private overlay-root-id "__xOverlayRoot")

(defn find-theme-host
  "Walk up from el to find the nearest x-theme ancestor, or fall back to body."
  [^js el]
  (or (when el (.closest el "x-theme"))
      (.-body js/document)))

(defn ensure-overlay-root!
  "Return (or create) the fixed overlay container. When an x-theme wrapper
   exists, the root is placed inside it so theme tokens cascade into panels."
  [^js trigger-el]
  (let [^js host     (find-theme-host trigger-el)
        ^js existing (.getElementById js/document overlay-root-id)]
    (if (and existing (.contains host existing))
      existing
      (let [^js div (.createElement js/document "div")]
        (.setAttribute div "id" overlay-root-id)
        (set! (.. div -style -position) "fixed")
        (set! (.. div -style -inset) "0")
        (set! (.. div -style -pointerEvents) "none")
        (set! (.. div -style -zIndex) "9999")
        (when existing (.remove existing))
        (.appendChild host div)
        div))))

(defn make-layer!
  "Create a fixed-position layer with its own shadow DOM inside the overlay root.
   style-text is the CSS for the layer's shadow root. Returns the layer element."
  [^js trigger-el style-text z-index]
  (let [^js overlay (ensure-overlay-root! trigger-el)
        ^js layer   (.createElement js/document "div")
        ^js shadow  (.attachShadow layer #js {:mode "open"})
        ^js style   (.createElement js/document "style")]

    (set! (.. layer -style -position) "fixed")
    (set! (.. layer -style -inset) "0")
    (set! (.. layer -style -pointerEvents) "none")
    (set! (.. layer -style -zIndex) (str z-index))

    (set! (.-textContent style) style-text)
    (.appendChild shadow style)

    (.appendChild overlay layer)
    layer))

(defn get-panel
  "Query the layer's shadow root for the panel element."
  [^js layer]
  (when layer
    (.querySelector (.-shadowRoot layer) "[part=panel]")))

(defn remove-layer!
  "Remove a layer from the DOM and clean up stored event listeners."
  [^js layer]
  (when layer
    (let [on-key            (gobj/get layer "__onKey")
          on-click-backdrop (gobj/get layer "__onClickBackdrop")
          on-item-click     (gobj/get layer "__onItemClick")
          on-close-btn      (gobj/get layer "__onCloseBtn")
          ^js close-btn-el  (gobj/get layer "__closeBtnEl")]
      (when on-key            (.removeEventListener layer "keydown" on-key true))
      (when on-click-backdrop (.removeEventListener layer "click" on-click-backdrop))
      (when on-item-click
        (when-let [^js panel (get-panel layer)]
          (.removeEventListener panel "click" on-item-click)))
      (when (and on-close-btn close-btn-el)
        (.removeEventListener close-btn-el "click" on-close-btn))
      (when (.-parentNode layer)
        (.removeChild (.-parentNode layer) layer)))))
