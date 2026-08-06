(ns barebuild.core
  (:require
   [barebuild.decorator :as decorator]
   [barebuild.elements.server-resource.server-resource :as server-resource]))

;; Registers BareBuild's runtime element(s). Consumers are host-app code, the demo registers its
;; own (see demo.app). `opts` may carry :request-decorator, a fn of the request value returning
;; the headers to attach to it, or a promise of them. Naming the key sets the hook to whatever it
;; holds, nil included, and omitting it leaves whatever is installed alone, so calling init again
;; without opts does not silently drop a decorator.
(defn ^:export init
  ([] (init nil))
  ([opts]
   (when (contains? opts :request-decorator)
     (decorator/install! (:request-decorator opts)))
   (server-resource/init!)))
