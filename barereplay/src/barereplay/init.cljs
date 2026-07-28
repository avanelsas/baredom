(ns barereplay.init
  (:require
   [baredom.components.x-button.x-button :as x-button]
   [baredom.components.x-code.x-code :as x-code]
   [baredom.components.x-floating-panel.x-floating-panel :as x-floating-panel]
   [baredom.components.x-slider.x-slider :as x-slider]
   [baredom.components.x-timeline.x-timeline :as x-timeline]
   [baredom.components.x-timeline-item.x-timeline-item :as x-timeline-item]
   [barereplay.dock :as dock]
   [barebuild.recorder :as recorder]
   [barereplay.store :as store]))

;; Ensure a Web component used is inited if needed
(defn- ensure! [tag init!]
  (when-not (js/customElements.get tag) (init!)))

(defn init!
  "Record BareBuild events into the replay store."
  []
  (recorder/set-recorder! store/record!)
  (ensure! "x-floating-panel" x-floating-panel/init!)
  (ensure! "x-button" x-button/init!)
  (ensure! "x-code" x-code/init!)
  (ensure! "x-slider" x-slider/init!)
  (ensure! "x-timeline" x-timeline/init!)
  (ensure! "x-timeline-item" x-timeline-item/init!)
  (dock/register!)
  (dock/mount!))
