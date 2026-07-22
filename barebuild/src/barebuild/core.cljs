(ns barebuild.core
  (:require
   [barebuild.elements.server-resource.server-resource :as server-resource]))

;; Registers BareBuild's runtime element(s). Consumers are host-app code — the demo
;; registers its own (see demo.app); a real adopter authors theirs with
;; barebuild.consumer-resource/register!.
(defn ^:export init []
  (server-resource/init!))
