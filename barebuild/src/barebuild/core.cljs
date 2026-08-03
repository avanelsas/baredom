(ns barebuild.core
  (:require
   [barebuild.decorator :as decorator]
   [barebuild.elements.server-resource.server-resource :as server-resource]))

;; Registers BareBuild's runtime element(s). Consumers are host-app code, the demo
;; registers its own (see demo.app)
;; `opts` may carry :request-decorator, a fn of the request value returning the headers to attach
;; to it (or a promise of them), for credentials that change independently of the resource value.
(defn ^:export init
  ([] (init nil))
  ([opts]
   (when-let [f (:request-decorator opts)]
     (decorator/set-request-decorator! f))
   (server-resource/init!)))
