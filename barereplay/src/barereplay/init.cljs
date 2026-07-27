(ns barereplay.init
  (:require
   [baredom.components.x-button.x-button :as x-button]
   [baredom.components.x-slider.x-slider :as x-slider]
   [baredom.components.x-timeline.x-timeline :as x-timeline]
   [baredom.components.x-timeline-item.x-timeline-item :as x-timeline-item]
   [baredom.components.x-typography.x-typography :as x-typography]
   [barereplay.dock :as dock]
   [barebuild.recorder :as recorder]
   [barereplay.store :as store]))

;; Ensure a Web component used is inited if needed
(defn- ensure! [tag init!]
  (when-not (js/customElements.get tag) (init!)))

(defn init!
  "Start recording BareBuild events into the replay store."
  []
  (recorder/set-recorder! store/record!)
  (ensure! "x-button" x-button/init!)
  (ensure! "x-slider" x-slider/init!)
  (ensure! "x-typography" x-typography/init!)
  (ensure! "x-timeline" x-timeline/init!)
  (ensure! "x-timeline-item" x-timeline-item/init!)
  (dock/register!)
  (dock/mount!))
