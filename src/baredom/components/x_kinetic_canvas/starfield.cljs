(ns baredom.components.x-kinetic-canvas.starfield)

;; ── Constants ───────────────────────────────────────────────────────────────
(def ^:private two-pi 6.283185307179586)

;; ── Entity creation ─────────────────────────────────────────────────────────

(defn create-entities
  "Create an array of star entities."
  [width height count _variant & _rest]
  (let [stars #js []]
    (dotimes [_ count]
      (let [s #js {}]
        (aset s "x"  (* (js/Math.random) width))
        (aset s "y"  (* (js/Math.random) height))
        (aset s "z"  (+ 0.1 (* (js/Math.random) 0.9)))
        (aset s "sz" (+ 0.5 (* (js/Math.random) 2.5)))
        (aset s "br" (+ 0.3 (* (js/Math.random) 0.7)))
        (aset s "ph" (* (js/Math.random) two-pi))
        (.push stars s)))
    stars))

;; ── Motion variant update ───────────────────────────────────────────────────

(defn- update-motion! [^js stars dt speed width height]
  (let [len   (.-length stars)
        drift (* 30.0 speed dt)
        cx    (* width 0.5)
        cy    (* height 0.5)]
    (dotimes [i len]
      (let [s    (aget stars i)
            z    (aget s "z")
            sx   (aget s "x")
            sy   (aget s "y")
            dx   (- sx cx)
            dy   (- sy cy)
            move (* drift z z)
            nx   (+ sx (* dx move 0.02))
            ny   (+ sy (* dy move 0.02))]
        (aset s "x" nx)
        (aset s "y" ny)
        ;; Wrap: respawn near center
        (when (or (< nx -10) (> nx (+ width 10))
                  (< ny -10) (> ny (+ height 10)))
          (aset s "x" (+ cx (* (- (js/Math.random) 0.5) 40)))
          (aset s "y" (+ cy (* (- (js/Math.random) 0.5) 40)))
          (aset s "z" (+ 0.1 (* (js/Math.random) 0.9)))
          (aset s "br" (+ 0.3 (* (js/Math.random) 0.7))))))))

;; ── Twinkle variant update ──────────────────────────────────────────────────

(defn- update-twinkle! [^js stars dt speed _width _height]
  (let [len         (.-length stars)
        phase-speed (* 2.0 speed dt)]
    (dotimes [i len]
      (let [s (aget stars i)]
        (aset s "ph" (+ (aget s "ph") phase-speed))))))

;; ── Public update ───────────────────────────────────────────────────────────

(defn update-entities!
  "Update star positions/brightness each frame."
  [stars dt speed-multiplier width height variant]
  (case variant
    "twinkle" (update-twinkle! stars dt speed-multiplier width height)
    (update-motion! stars dt speed-multiplier width height)))

;; ── Draw ────────────────────────────────────────────────────────────────────

(defn draw!
  "Render stars to a 2D canvas context."
  [^js ctx stars _width _height colors time]
  (let [len (.-length stars)
        c1  (aget colors 0)
        c2  (aget colors 1)]
    (dotimes [i len]
      (let [s       (aget stars i)
            z       (aget s "z")
            size    (* (aget s "sz") z)
            twinkle (+ 0.5 (* 0.5 (js/Math.sin (+ (* time 2.0) (aget s "ph")))))
            alpha   (* (aget s "br") twinkle z)
            color   (if (zero? (mod i 3)) c2 c1)]
        (set! (.-globalAlpha ctx) (js/Math.min 1.0 alpha))
        (set! (.-fillStyle ctx) color)
        (.beginPath ctx)
        (.arc ctx (aget s "x") (aget s "y") (js/Math.max 0.5 size) 0 two-pi)
        (.fill ctx))))
  (set! (.-globalAlpha ctx) 1.0))

;; ── Draw static ─────────────────────────────────────────────────────────────

(defn draw-static!
  "Render a single static frame (prefers-reduced-motion)."
  [^js ctx stars _width _height colors]
  (let [len (.-length stars)
        c1  (aget colors 0)]
    (set! (.-fillStyle ctx) c1)
    (dotimes [i len]
      (let [s    (aget stars i)
            z    (aget s "z")
            size (* (aget s "sz") z)]
        (set! (.-globalAlpha ctx) (* (aget s "br") z))
        (.beginPath ctx)
        (.arc ctx (aget s "x") (aget s "y") (js/Math.max 0.5 size) 0 two-pi)
        (.fill ctx))))
  (set! (.-globalAlpha ctx) 1.0))
