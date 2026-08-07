(ns barebuild.console-capture
  "Capturing console.error under Node, for the seams that report a mistake rather than throwing.
   Not a -test namespace, so the runner does not pick it up as a suite.")

(defn errors-while
  "The console.error calls `f` made, real console restored afterwards."
  [f]
  (let [errors     (atom [])
        real-error (.-error js/console)]
    (set! (.-error js/console) (fn [& args] (swap! errors conj (vec args))))
    (try (f)
         (finally (set! (.-error js/console) real-error)))
    @errors))
