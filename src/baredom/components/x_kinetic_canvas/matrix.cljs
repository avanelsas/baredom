(ns baredom.components.x-kinetic-canvas.matrix)

;; ── Constants ───────────────────────────────────────────────────────────────
(def default-char-size 14)
(def ^:private katakana-start 0x30A0)
(def ^:private katakana-range 96)
(def ^:private latin-start 33)
(def ^:private latin-range 94)

;; ── Random character ────────────────────────────────────────────────────────

(defn- random-char []
  (if (< (js/Math.random) 0.5)
    (.fromCharCode js/String (+ katakana-start (js/Math.floor (* (js/Math.random) katakana-range))))
    (.fromCharCode js/String (+ latin-start (js/Math.floor (* (js/Math.random) latin-range))))))

;; ── Entity creation ─────────────────────────────────────────────────────────

(defn create-entities
  "Create an array of column entities for matrix rain.
   `char-sz` sets the font size used for spacing calculations."
  [width height count _variant char-sz]
  (let [cs      (or char-sz default-char-size)
        columns #js []
        rows    (js/Math.ceil (/ height cs))]
    (dotimes [i count]
      (let [col-len (+ 5 (js/Math.floor (* (js/Math.random) (- rows 5))))
            chars   #js []]
        (dotimes [_ col-len]
          (.push chars (random-char)))
        (let [c #js {}]
          (aset c "x"  (* i (/ width count)))
          (aset c "y"  (* (- (js/Math.random) 0.5) height 2))
          (aset c "sp" (+ 40 (* (js/Math.random) 120)))
          (aset c "ln" col-len)
          (aset c "ch" chars)
          (aset c "ct" (* (js/Math.random) 0.2))
          (aset c "cs" cs)
          (.push columns c))))
    columns))

;; ── Update ──────────────────────────────────────────────────────────────────

(defn update-entities!
  "Update column positions and randomly change characters."
  [^js columns dt speed-multiplier _width height _variant]
  (let [len (.-length columns)]
    (dotimes [i len]
      (let [col  (aget columns i)
            spd  (* (aget col "sp") speed-multiplier dt)
            clen (aget col "ln")
            cs   (aget col "cs")]
        ;; Move down
        (aset col "y" (+ (aget col "y") spd))
        ;; Wrap when entire trail is past bottom
        (when (> (- (aget col "y") (* clen cs)) height)
          (aset col "y" (- 0 (* clen cs)))
          (aset col "sp" (+ 40 (* (js/Math.random) 120))))
        ;; Randomly change characters
        (aset col "ct" (+ (aget col "ct") dt))
        (when (> (aget col "ct") 0.08)
          (aset col "ct" 0)
          (let [chars (aget col "ch")
                idx   (js/Math.floor (* (js/Math.random) (.-length chars)))]
            (aset chars idx (random-char))))))))

;; ── Draw ────────────────────────────────────────────────────────────────────

(defn draw!
  "Render matrix rain columns to a 2D canvas context."
  [^js ctx ^js columns _width height colors _time]
  (let [len  (.-length columns)
        c1   (aget colors 0)
        c2   (aget colors 1)
        c3   (aget colors 2)]
    (dotimes [i len]
      (let [col   (aget columns i)
            x     (aget col "x")
            y     (aget col "y")
            chars (aget col "ch")
            clen  (aget col "ln")
            cs    (aget col "cs")]
        (set! (.-font ctx) (str cs "px monospace"))
        (set! (.-textBaseline ctx) "top")
        (dotimes [j clen]
          (let [cy   (- y (* (- clen 1 j) cs))
                fade (/ (+ j 1.0) clen)
                alpha (* fade fade)]
            (when (and (> cy (- cs)) (< cy (+ height cs)))
              (set! (.-globalAlpha ctx) (js/Math.min 1.0 alpha))
              (set! (.-fillStyle ctx)
                    (cond
                      (= j (dec clen)) c1
                      (> fade 0.5)     c2
                      :else            c3))
              (.fillText ctx (aget chars j) x cy)))))))
  (set! (.-globalAlpha ctx) 1.0))

;; ── Draw static ─────────────────────────────────────────────────────────────

(defn draw-static!
  "Render a single static frame (prefers-reduced-motion)."
  [^js ctx ^js columns width height colors]
  (draw! ctx columns width height colors 0))
