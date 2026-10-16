(ns baredom.test-writes
  "Counts of the attribute writes that a test makes in the document.")

(defn count-attr-writes!
  "An atom that counts the writes of `attrs` inside `node` from now on."
  [^js node attrs]
  (let [writes (atom 0)]
    (.observe (js/MutationObserver. (fn count-records [records] (swap! writes + (.-length records))))
              node #js {:subtree true :attributeFilter (clj->js attrs)})
    writes))

(defn count-child-changes!
  "An atom that counts the changes to the children of `node` from now on."
  [^js node]
  (let [changes (atom 0)]
    (.observe (js/MutationObserver. (fn count-records [records] (swap! changes + (.-length records))))
              node #js {:childList true})
    changes))
