(ns baredom.utils.overlay
  "Shared overlay root management for portal-based components.
   Provides document-level fixed layers that escape CSS stacking contexts."
  (:require [goog.object :as gobj]))

(def ^:private overlay-root-id "__xOverlayRoot")
(def ^:private k-listeners     "__xOverlayListeners")

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

(defn attach-listener!
  "Attach `handler` to `target` for `event-name` and remember the binding
   on `layer` so `remove-layer!` will detach it. `target` is usually the
   layer itself, but may be any node reachable from it (panel, close
   button, etc.). `capture` is the `addEventListener` capture flag."
  [^js layer ^js target event-name handler capture]
  (.addEventListener target event-name handler capture)
  (let [^js xs (or (gobj/get layer k-listeners) #js [])]
    (.push xs #js [target event-name handler capture])
    (gobj/set layer k-listeners xs)))

(defn- detach-listeners! [^js layer]
  (when-let [^js xs (gobj/get layer k-listeners)]
    (dotimes [i (.-length xs)]
      (let [^js entry (aget xs i)]
        (.removeEventListener (aget entry 0)
                              (aget entry 1)
                              (aget entry 2)
                              (aget entry 3))))
    (gobj/set layer k-listeners nil)))

(defn remove-layer!
  "Detach every listener attached via `attach-listener!` and remove the
   layer from the DOM."
  [^js layer]
  (when layer
    (detach-listeners! layer)
    (when (.-parentNode layer)
      (.removeChild (.-parentNode layer) layer))))
