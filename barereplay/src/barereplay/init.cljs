(ns barereplay.init
  (:require [barebuild.recorder :as recorder]
            [barereplay.store :as store]))

(defn init!
  "Start recording BareBuild events into the replay store."
  []
  (recorder/set-recorder! store/record!))
