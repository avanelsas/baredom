(ns baredom.components.x-kinetic-canvas.bubbles)

;; ── Constants ───────────────────────────────────────────────────────────────
(def ^:private two-pi 6.283185307179586)

;; ── Entity creation ─────────────────────────────────────────────────────────

(defn create-entities
  "Create an array of bubble entities."
  [width height count _variant & _rest]
  (let [bubbles #js []]
    (dotimes [i count]
      (let [b #js {}]
        (aset b "x"  (* (js/Math.random) width))
        (aset b "y"  (* (js/Math.random) height))
        (aset b "r"  (+ 8 (* (js/Math.random) 40)))
        (aset b "vx" (* (- (js/Math.random) 0.5) 20))
        (aset b "vy" (- (+ 10 (* (js/Math.random) 30))))
        (aset b "op" (+ 0.4 (* (js/Math.random) 0.5)))
        (aset b "ci" (mod i 3))
        (.push bubbles b)))
    bubbles))

;; ── Update ──────────────────────────────────────────────────────────────────

(defn update-entities!
  "Update bubble positions each frame. Bubbles float upward and wrap around."
  [^js bubbles dt speed-multiplier width height _variant]
  (let [len (.-length bubbles)
        spd (* speed-multiplier dt)]
    (dotimes [i len]
      (let [b   (aget bubbles i)
            r   (aget b "r")
            x   (+ (aget b "x") (* (aget b "vx") spd))
            y   (+ (aget b "y") (* (aget b "vy") spd))
            vx  (+ (aget b "vx") (* (- (js/Math.random) 0.5) 2.0 dt))]
        (aset b "x" x)
        (aset b "y" y)
        (aset b "vx" (if (> (js/Math.abs vx) 30)
                       (* (js/Math.sign vx) 30)
                       vx))
        ;; Wrap: respawn at bottom when off top
        (when (< y (- 0 r 10))
          (aset b "y" (+ height r 10))
          (aset b "x" (* (js/Math.random) width))
          (aset b "r" (+ 8 (* (js/Math.random) 40)))
          (aset b "op" (+ 0.4 (* (js/Math.random) 0.5))))
        ;; Wrap horizontal
        (when (< x (- 0 r))
          (aset b "x" (+ width r)))
        (when (> x (+ width r))
          (aset b "x" (- 0 r)))))))

;; ── Draw ────────────────────────────────────────────────────────────────────

(defn draw!
  "Render bubbles to a 2D canvas context."
  [^js ctx ^js bubbles _width _height colors _time]
  (let [len (.-length bubbles)]
    (dotimes [i len]
      (let [b       (aget bubbles i)
            ci      (aget b "ci")
            color   (aget colors ci)
            opacity (aget b "op")
            bx      (aget b "x")
            by      (aget b "y")
            br      (aget b "r")]
        (set! (.-globalAlpha ctx) opacity)
        (set! (.-fillStyle ctx) color)
        (.beginPath ctx)
        (.arc ctx bx by br 0 two-pi)
        (.fill ctx)
        ;; Subtle highlight on upper-left
        (set! (.-globalAlpha ctx) (* opacity 0.3))
        (set! (.-fillStyle ctx) "rgba(255,255,255,0.5)")
        (.beginPath ctx)
        (.arc ctx
              (- bx (* br 0.25))
              (- by (* br 0.25))
              (* br 0.3) 0 two-pi)
        (.fill ctx))))
  (set! (.-globalAlpha ctx) 1.0))

;; ── Draw static ─────────────────────────────────────────────────────────────

(defn draw-static!
  "Render a single static frame (prefers-reduced-motion)."
  [^js ctx ^js bubbles width height colors]
  (draw! ctx bubbles width height colors 0))
