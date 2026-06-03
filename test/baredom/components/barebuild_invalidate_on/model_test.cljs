(ns baredom.components.barebuild-invalidate-on.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.barebuild.protocol :as protocol]
            [baredom.components.barebuild-invalidate-on.model :as model]))

(deftest tag-name-test
  (is (= "barebuild-invalidate-on" model/tag-name)))

;; The cross-component handshake names must come from the shared protocol ns, not
;; local literals — otherwise a rename on one side drifts silently at runtime.
(deftest event-names-from-protocol-test
  (is (= protocol/event-invalidate model/event-invalidate)
      "the dispatched invalidate event is the shared protocol name")
  (is (= protocol/event-action-state model/default-source-event)
      "the default source event is the shared protocol action-state name"))

(deftest observed-attributes-test
  (is (= #{"event" "when-phase" "when-name" "src"}
         (set (array-seq model/observed-attributes)))))
