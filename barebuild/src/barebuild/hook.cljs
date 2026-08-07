(ns barebuild.hook
  "One replaceable function the host app installs, called at the edge. The slot and the rule for
  filling it live here. What calling the hook means belongs to whoever owns the slot, and that is
  the whole of the difference between the two that exist.")

(defn install!
  "Put `f` in `slot`, the atom holding the page's one hook of `kind`. Nil clears it, and what is not
  callable is refused here rather than failing at every call site later."
  [slot kind f]
  (if (or (nil? f) (ifn? f))
    (reset! slot f)
    (js/console.error (str "[barebuild] a " kind " must be a function, ignoring:") f))
  nil)
