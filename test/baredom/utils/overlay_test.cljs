(ns baredom.utils.overlay-test
  (:require [baredom.utils.overlay :as overlay]
            [cljs.test :refer-macros [deftest is]]))

(def ^:private page-font "OverlayFontTest")

(defn- button-font-in-a-layer!
  "The font family of a button in a new layer whose overlay root has `page-font`. Removes the
   layer."
  []
  (let [^js layer  (overlay/make-layer! (.-body js/document) "" 1)
        ^js button (.createElement js/document "button")]
    (set! (.. layer -parentNode -style -fontFamily) (str page-font ", serif"))
    (.appendChild (.-shadowRoot layer) button)
    (let [font (.-fontFamily (js/getComputedStyle button))]
      (set! (.. layer -parentNode -style -fontFamily) "")
      (overlay/remove-layer! layer)
      font)))

(deftest a-button-in-a-layer-takes-the-font-family-of-the-page
  (is (.includes (button-font-in-a-layer!) page-font)))
