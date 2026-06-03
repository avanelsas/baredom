(ns baredom.components.barebuild.listeners
  (:require
   [baredom.utils.dom :as du]))

;; Shared source-listener management for the BareBuild family — barebuild-action
;; (a host-bound submit listener) and barebuild-invalidate-on (a parentNode-bound
;; source listener). Both stash a stable handler, the node it was bound to, and the
;; event name, so a rebind (attribute change) or a disconnect removes the EXACT
;; (node, event, handler) triple it added. The mechanics are identical; only the
;; target node and the event-name resolution differ, so those stay in the caller.
;; This is kept separate from barebuild/lifecycle (the fetch + state-transition
;; concern) so neither namespace mixes two responsibilities. `cfg` keys:
;;   :k-handler     instance-field key holding the stable handler closure
;;   :k-bound-event instance-field key holding the event name currently bound
;;   :k-node        OPTIONAL key holding the node attached to (defaults to `el`)

(defn detach-listener!
  "Remove a previously-bound source listener (no-op when nothing is bound). With
  :k-node configured the node is read from that field; without it the listener is
  assumed to sit on `el` itself."
  [^js el {:keys [k-handler k-node k-bound-event]}]
  (let [^js h    (du/getv el k-handler)
        ^js node (if k-node (du/getv el k-node) el)
        bound    (du/getv el k-bound-event)]
    (when (and h node bound)
      (.removeEventListener node bound h)
      (when k-node (du/setv! el k-node nil))
      (du/setv! el k-bound-event nil))))

(defn bind-listener!
  "Attach `handler` to `node` for `event-name`, stashing the bound-event (and the
  node, when :k-node is configured) so detach-listener! can later remove the exact
  pair. The caller resolves `node` + `event-name` and supplies the stable handler."
  [^js el ^js node event-name handler {:keys [k-node k-bound-event]}]
  (.addEventListener node event-name handler)
  (when k-node (du/setv! el k-node node))
  (du/setv! el k-bound-event event-name))
