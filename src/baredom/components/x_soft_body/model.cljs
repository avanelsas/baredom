(ns baredom.components.x-soft-body.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-soft-body")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-stiffness  "stiffness")
(def attr-damping    "damping")
(def attr-radius     "radius")
(def attr-intensity  "intensity")
(def attr-grab-radius "grab-radius")
(def attr-disabled   "disabled")

(def observed-attributes
  #js [attr-stiffness attr-damping attr-radius attr-intensity
       attr-grab-radius attr-disabled])

;; ── Event names ─────────────────────────────────────────────────────────────
(def event-grab    "x-soft-body-grab")
(def event-release "x-soft-body-release")

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-bg           "--x-soft-body-bg")
(def css-border       "--x-soft-body-border")
(def css-border-width "--x-soft-body-border-width")
(def css-shadow       "--x-soft-body-shadow")
(def css-padding      "--x-soft-body-padding")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:stiffness  {:type 'number}
   :damping    {:type 'number}
   :radius     {:type 'number}
   :intensity  {:type 'number}
   :grabRadius {:type 'number}
   :disabled   {:type 'boolean}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-grab    {:detail {} :cancelable false}
   event-release {:detail {} :cancelable false}})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn- parse-float-clamped
  "Parse a string to float, clamp to [lo, hi], return default on nil/NaN."
  [s default-val lo hi]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "")
        default-val
        (let [n (js/parseFloat v)]
          (if (js/isNaN n)
            default-val
            (js/Math.min hi (js/Math.max lo n))))))
    default-val))

(defn parse-stiffness [s] (parse-float-clamped s 180.0 10.0 1000.0))
(defn parse-damping   [s] (parse-float-clamped s 12.0 1.0 100.0))
(defn parse-radius    [s] (parse-float-clamped s 16.0 6.0 200.0))
(defn parse-intensity [s] (parse-float-clamped s 1.0 0.0 5.0))
(defn parse-grab-radius [s] (parse-float-clamped s 80.0 10.0 500.0))

(defn parse-disabled
  "Parse disabled attribute. Present (any value) → true, nil → false."
  [s]
  (some? s))

;; ── derive-state ────────────────────────────────────────────────────────────

(defn derive-state
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :stiffness-raw   string | nil
    :damping-raw     string | nil
    :radius-raw      string | nil
    :intensity-raw   string | nil
    :grab-radius-raw string | nil
    :disabled-attr   string | nil  (nil when absent)

  Output keys:
    :stiffness   number
    :damping     number
    :radius      number
    :intensity   number
    :grab-radius number
    :disabled?   boolean"
  [{:keys [stiffness-raw damping-raw radius-raw intensity-raw
           grab-radius-raw disabled-attr]}]
  {:stiffness   (parse-stiffness stiffness-raw)
   :damping     (parse-damping damping-raw)
   :radius      (parse-radius radius-raw)
   :intensity   (parse-intensity intensity-raw)
   :grab-radius (parse-grab-radius grab-radius-raw)
   :disabled?   (parse-disabled disabled-attr)})

;; ── Number of control points ────────────────────────────────────────────────
(def point-count
  "20 control points: per side 3 edge points + 2 corner arc points = 5 × 4."
  20)

;; ── Geometry: rest points ───────────────────────────────────────────────────

(defn generate-rest-points
  "Compute 20 rest-position control points around a rounded rect of size w×h
   with corner radius r. Returns two JS arrays [xs, ys] each of length 20.

   Each side contributes 5 points: 3 along the straight edge, then 2 on the
   following quarter-circle corner arc (at 30° and 60° of the arc).
   The arc entry/exit points coincide with the first/last edge points of
   adjacent sides, so the corner is smoothly bridged.

   Layout (clockwise from top-left arc exit):
     0-2:   top edge       (left→right)
     3-4:   top-right arc  (30° and 60°)
     5-7:   right edge     (top→bottom)
     8-9:   bottom-right arc
     10-12: bottom edge    (right→left)
     13-14: bottom-left arc
     15-17: left edge      (bottom→top)
     18-19: top-left arc"
  [w h r]
  (let [r   (js/Math.max 6.0 (js/Math.min r (/ (js/Math.min w h) 2.0)))
        ;; Arc points at 30° and 60° from each corner's entry tangent
        c30 (js/Math.cos (/ js/Math.PI 6.0))   ;; cos 30° ≈ 0.866
        s30 (js/Math.sin (/ js/Math.PI 6.0))   ;; sin 30° = 0.5
        c60 (js/Math.cos (/ js/Math.PI 3.0))   ;; cos 60° = 0.5
        s60 (js/Math.sin (/ js/Math.PI 3.0))   ;; sin 60° ≈ 0.866
        xs  (js/Float64Array. point-count)
        ys  (js/Float64Array. point-count)]

    ;; ── Top edge (y=0, left→right) ──
    (aset xs 0  r)            (aset ys 0  0.0)
    (aset xs 1  (* w 0.5))    (aset ys 1  0.0)
    (aset xs 2  (- w r))      (aset ys 2  0.0)

    ;; ── Top-right corner arc (centre = (w-r, r)) ──
    ;; Arc runs from (w-r, 0) → (w, r), angles -90° → 0°
    (aset xs 3  (+ (- w r) (* r s30)))   (aset ys 3  (- r (* r c30)))   ;; 30°
    (aset xs 4  (+ (- w r) (* r s60)))   (aset ys 4  (- r (* r c60)))   ;; 60°

    ;; ── Right edge (x=w, top→bottom) ──
    (aset xs 5  w)            (aset ys 5  r)
    (aset xs 6  w)            (aset ys 6  (* h 0.5))
    (aset xs 7  w)            (aset ys 7  (- h r))

    ;; ── Bottom-right corner arc (centre = (w-r, h-r)) ──
    ;; Arc runs from (w, h-r) → (w-r, h), angles 0° → 90°
    (aset xs 8  (+ (- w r) (* r c30)))   (aset ys 8  (+ (- h r) (* r s30)))  ;; 30°
    (aset xs 9  (+ (- w r) (* r c60)))   (aset ys 9  (+ (- h r) (* r s60)))  ;; 60°

    ;; ── Bottom edge (y=h, right→left) ──
    (aset xs 10 (- w r))      (aset ys 10 h)
    (aset xs 11 (* w 0.5))    (aset ys 11 h)
    (aset xs 12 r)            (aset ys 12 h)

    ;; ── Bottom-left corner arc (centre = (r, h-r)) ──
    ;; Arc runs from (r, h) → (0, h-r), angles 90° → 180°
    (aset xs 13 (- r (* r s30)))         (aset ys 13 (+ (- h r) (* r c30)))  ;; 30°
    (aset xs 14 (- r (* r s60)))         (aset ys 14 (+ (- h r) (* r c60)))  ;; 60°

    ;; ── Left edge (x=0, bottom→top) ──
    (aset xs 15 0.0)          (aset ys 15 (- h r))
    (aset xs 16 0.0)          (aset ys 16 (* h 0.5))
    (aset xs 17 0.0)          (aset ys 17 r)

    ;; ── Top-left corner arc (centre = (r, r)) ──
    ;; Arc runs from (0, r) → (r, 0), angles 180° → 270°
    (aset xs 18 (- r (* r c30)))         (aset ys 18 (- r (* r s30)))  ;; 30°
    (aset xs 19 (- r (* r c60)))         (aset ys 19 (- r (* r s60)))  ;; 60°

    #js [xs ys]))

;; ── SVG path generation ─────────────────────────────────────────────────────

(defn points->path-d
  "Build a smooth closed SVG path through n control points using
   Catmull-Rom → cubic bezier conversion with handle-length clamping
   to prevent overshoot at corners.
   xs, ys are Float64Arrays of length n."
  [^js xs ^js ys n]
  (let [sb #js []]
    (.push sb (str "M" (.toFixed (aget xs 0) 2) "," (.toFixed (aget ys 0) 2)))
    (dotimes [i n]
      (let [i0 (mod (+ i (- n 1)) n)
            i1 i
            i2 (mod (+ i 1) n)
            i3 (mod (+ i 2) n)
            ;; Raw Catmull-Rom tangent vectors (divided by 6)
            t1x (/ (- (aget xs i2) (aget xs i0)) 6.0)
            t1y (/ (- (aget ys i2) (aget ys i0)) 6.0)
            t2x (/ (- (aget xs i3) (aget xs i1)) 6.0)
            t2y (/ (- (aget ys i3) (aget ys i1)) 6.0)
            ;; Segment chord length — clamp handles to 40% of this
            seg-dx (- (aget xs i2) (aget xs i1))
            seg-dy (- (aget ys i2) (aget ys i1))
            seg-len (js/Math.sqrt (+ (* seg-dx seg-dx) (* seg-dy seg-dy)))
            max-handle (* seg-len 0.4)
            ;; Clamp handle 1
            h1-len (js/Math.sqrt (+ (* t1x t1x) (* t1y t1y)))
            s1     (if (> h1-len max-handle) (/ max-handle (+ h1-len 0.001)) 1.0)
            cp1x   (+ (aget xs i1) (* t1x s1))
            cp1y   (+ (aget ys i1) (* t1y s1))
            ;; Clamp handle 2
            h2-len (js/Math.sqrt (+ (* t2x t2x) (* t2y t2y)))
            s2     (if (> h2-len max-handle) (/ max-handle (+ h2-len 0.001)) 1.0)
            cp2x   (- (aget xs i2) (* t2x s2))
            cp2y   (- (aget ys i2) (* t2y s2))]
        (.push sb (str "C" (.toFixed cp1x 2) "," (.toFixed cp1y 2)
                        " " (.toFixed cp2x 2) "," (.toFixed cp2y 2)
                        " " (.toFixed (aget xs i2) 2) "," (.toFixed (aget ys i2) 2)))))
    (.push sb "Z")
    (.join sb "")))

;; ── Static rounded-rect path ────────────────────────────────────────────────

(def ^:private kappa
  "Bezier approximation constant for quarter-circle arcs."
  0.5522847498)

(defn static-rounded-rect-d
  "Return an SVG path d-string for a simple rounded rectangle."
  [w h r]
  (let [r  (js/Math.max 6.0 (js/Math.min r (/ (js/Math.min w h) 2.0)))
        kr (* kappa r)]
    (str "M" (.toFixed r 2) ",0"
         "L" (.toFixed (- w r) 2) ",0"
         "C" (.toFixed (+ (- w r) kr) 2) ",0"
         " " (.toFixed w 2) "," (.toFixed (- r kr) 2)
         " " (.toFixed w 2) "," (.toFixed r 2)
         "L" (.toFixed w 2) "," (.toFixed (- h r) 2)
         "C" (.toFixed w 2) "," (.toFixed (+ (- h r) kr) 2)
         " " (.toFixed (+ (- w r) kr) 2) "," (.toFixed h 2)
         " " (.toFixed (- w r) 2) "," (.toFixed h 2)
         "L" (.toFixed r 2) "," (.toFixed h 2)
         "C" (.toFixed (- r kr) 2) "," (.toFixed h 2)
         " 0," (.toFixed (+ (- h r) kr) 2)
         " 0," (.toFixed (- h r) 2)
         "L0," (.toFixed r 2)
         "C0," (.toFixed (- r kr) 2)
         " " (.toFixed (- r kr) 2) ",0"
         " " (.toFixed r 2) ",0"
         "Z")))

;; ── Spring physics ──────────────────────────────────────────────────────────

(defn spring-step
  "Single step of damped spring physics (1D).
   Returns #js [new-position new-velocity]."
  [current target velocity dt stiffness damping]
  (let [force   (- (* stiffness (- target current)) (* damping velocity))
        new-vel (+ velocity (* force dt))
        new-pos (+ current (* new-vel dt))]
    #js [new-pos new-vel]))

;; ── Pointer offset computation ──────────────────────────────────────────────

(def ^:private hover-strength 20.0)
(def ^:private grab-strength  50.0)

(defn compute-pointer-offset
  "Compute the displacement offset for a single control point based on
   pointer position. Returns #js [offset-x offset-y].
   When the pointer is outside grab-radius, returns [0, 0]."
  [rest-x rest-y pointer-x pointer-y grab-radius intensity grabbed?]
  (let [dx (- pointer-x rest-x)
        dy (- pointer-y rest-y)
        dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))]
    (if (or (<= dist 0.001) (>= dist grab-radius))
      #js [0.0 0.0]
      (let [influence (- 1.0 (/ dist grab-radius))
            influence (* influence influence)  ;; quadratic falloff
            strength  (if grabbed? grab-strength hover-strength)
            mag       (* influence strength intensity)
            ;; Push in direction from rest point toward pointer
            nx        (/ dx dist)
            ny        (/ dy dist)]
        #js [(* nx mag) (* ny mag)]))))
