(ns baredom.components.x-color-picker.model
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

;; ---------------------------------------------------------------------------
;; Tag name
;; ---------------------------------------------------------------------------
(def tag-name "x-color-picker")

;; ---------------------------------------------------------------------------
;; Attribute name constants
;; ---------------------------------------------------------------------------
(def attr-value    "value")
(def attr-alpha    "alpha")
(def attr-swatches "swatches")
(def attr-disabled "disabled")
(def attr-readonly "readonly")
(def attr-name     "name")
(def attr-mode     "mode")
(def attr-open     "open")
(def attr-label    "label")

;; ---------------------------------------------------------------------------
;; Event name constants
;; ---------------------------------------------------------------------------
(def event-input  "x-color-picker-input")
(def event-change "x-color-picker-change")

;; ---------------------------------------------------------------------------
;; Defaults and allowed values
;; ---------------------------------------------------------------------------
(def default-value "#000000")
(def default-mode  "inline")

(def allowed-modes #{"inline" "popover"})

;; ---------------------------------------------------------------------------
;; Observed attributes
;; ---------------------------------------------------------------------------
(def observed-attributes
  #js [attr-value
       attr-alpha
       attr-swatches
       attr-disabled
       attr-readonly
       attr-name
       attr-mode
       attr-open
       attr-label])

;; ---------------------------------------------------------------------------
;; API metadata
;; ---------------------------------------------------------------------------
(def property-api
  {:value    {:type 'string  :reflects-attribute attr-value}
   :alpha    {:type 'boolean :reflects-attribute attr-alpha}
   :swatches {:type 'string  :reflects-attribute attr-swatches}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly {:type 'boolean :reflects-attribute attr-readonly}
   :name     {:type 'string  :reflects-attribute attr-name}
   :mode     {:type 'string  :reflects-attribute attr-mode}
   :open     {:type 'boolean :reflects-attribute attr-open}
   :label    {:type 'string  :reflects-attribute attr-label}})

(def event-schema
  {event-input  {:detail {:value 'string :h 'number :s 'number :l 'number :a 'number}
                 :cancelable false}
   event-change {:detail {:value 'string :h 'number :s 'number :l 'number :a 'number}
                 :cancelable true}})

;; ---------------------------------------------------------------------------
;; Color math — clamping
;; ---------------------------------------------------------------------------
(defn clamp
  "Clamp v to [lo, hi]."
  [v lo hi]
  (-> v (max lo) (min hi)))

(defn clamp-hue
  "Wrap hue to [0, 360)."
  [h]
  (mod h 360))

(defn clamp-percent
  "Clamp to [0, 100]."
  [v]
  (clamp v 0 100))

(defn clamp-alpha
  "Clamp to [0, 1]."
  [a]
  (clamp a 0.0 1.0))

;; ---------------------------------------------------------------------------
;; Color math — hex parsing
;; ---------------------------------------------------------------------------
(def ^:private hex-re
  "Regex matching #rgb, #rrggbb, or #rrggbbaa (case-insensitive)."
  #"^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$")

(defn valid-hex?
  "Returns true when s is a valid hex colour string."
  [s]
  (boolean (and (string? s) (re-matches hex-re (.trim s)))))

(defn parse-hex
  "Parse hex string (#rgb, #rrggbb, #rrggbbaa) into {:r :g :b :a} or nil.
   r/g/b are 0-255, a is 0-1."
  [s]
  (when (string? s)
    (let [s (.trim s)]
      (when-let [[_ hex] (re-matches hex-re s)]
        (case (count hex)
          3 (let [r (js/parseInt (str (nth hex 0) (nth hex 0)) 16)
                  g (js/parseInt (str (nth hex 1) (nth hex 1)) 16)
                  b (js/parseInt (str (nth hex 2) (nth hex 2)) 16)]
              {:r r :g g :b b :a 1.0})
          6 (let [r (js/parseInt (.substring hex 0 2) 16)
                  g (js/parseInt (.substring hex 2 4) 16)
                  b (js/parseInt (.substring hex 4 6) 16)]
              {:r r :g g :b b :a 1.0})
          8 (let [r (js/parseInt (.substring hex 0 2) 16)
                  g (js/parseInt (.substring hex 2 4) 16)
                  b (js/parseInt (.substring hex 4 6) 16)
                  a (js/parseInt (.substring hex 6 8) 16)]
              {:r r :g g :b b :a (/ a 255.0)})
          nil)))))

;; ---------------------------------------------------------------------------
;; Color math — conversions
;; ---------------------------------------------------------------------------
(defn rgb->hsl
  "Convert RGB (0-255) to HSL. Returns {:h 0-360 :s 0-100 :l 0-100}."
  [r g b]
  (let [r (/ r 255.0)
        g (/ g 255.0)
        b (/ b 255.0)
        mx (max r g b)
        mn (min r g b)
        l  (/ (+ mx mn) 2.0)
        d  (- mx mn)]
    (if (zero? d)
      {:h 0 :s 0 :l (* l 100)}
      (let [s (if (> l 0.5)
                (/ d (- 2.0 mx mn))
                (/ d (+ mx mn)))
            h (cond
                (== mx r) (/ (+ (/ (- g b) d) (if (< g b) 6 0)) 6.0)
                (== mx g) (/ (+ (/ (- b r) d) 2.0) 6.0)
                :else     (/ (+ (/ (- r g) d) 4.0) 6.0))]
        {:h (* h 360)
         :s (* s 100)
         :l (* l 100)}))))

(defn- hue->rgb
  "Helper for hsl->rgb. Convert hue segment to RGB channel value."
  [p q t]
  (let [t (cond (< t 0) (+ t 1) (> t 1) (- t 1) :else t)]
    (cond
      (< t (/ 1.0 6)) (+ p (* (- q p) 6.0 t))
      (< t 0.5)       q
      (< t (/ 2.0 3)) (+ p (* (- q p) (- (/ 2.0 3) t) 6.0))
      :else            p)))

(defn hsl->rgb
  "Convert HSL to RGB. H is 0-360, S and L are 0-100.
   Returns {:r :g :b} with values 0-255."
  [h s l]
  (let [h (/ (clamp-hue h) 360.0)
        s (/ (clamp-percent s) 100.0)
        l (/ (clamp-percent l) 100.0)]
    (if (zero? s)
      (let [v (js/Math.round (* l 255))]
        {:r v :g v :b v})
      (let [q (if (< l 0.5)
                (* l (+ 1.0 s))
                (- (+ l s) (* l s)))
            p (- (* 2.0 l) q)]
        {:r (js/Math.round (* (hue->rgb p q (+ h (/ 1.0 3))) 255))
         :g (js/Math.round (* (hue->rgb p q h) 255))
         :b (js/Math.round (* (hue->rgb p q (- h (/ 1.0 3))) 255))}))))

(defn rgb->hex
  "Convert RGB (0-255) to #rrggbb hex string."
  [r g b]
  (let [to-hex (fn [n]
                 (let [s (.toString (clamp (js/Math.round n) 0 255) 16)]
                   (if (< (count s) 2) (str "0" s) s)))]
    (str "#" (to-hex r) (to-hex g) (to-hex b))))

(defn rgba->hex8
  "Convert RGBA to #rrggbbaa hex string. Alpha is 0-1."
  [r g b a]
  (let [to-hex (fn [n]
                 (let [s (.toString (clamp (js/Math.round n) 0 255) 16)]
                   (if (< (count s) 2) (str "0" s) s)))]
    (str "#" (to-hex r) (to-hex g) (to-hex b)
         (to-hex (* (clamp-alpha a) 255)))))

;; ---------------------------------------------------------------------------
;; Color math — HSL <-> HSV conversion
;; ---------------------------------------------------------------------------
(defn hsl->hsv
  "Convert HSL to HSV. All inputs/outputs use H 0-360, S/L/V 0-100."
  [h s l]
  (let [s (/ (clamp-percent s) 100.0)
        l (/ (clamp-percent l) 100.0)
        v (+ l (* s (min l (- 1.0 l))))
        sv (if (zero? v) 0.0 (* 2.0 (- 1.0 (/ l v))))]
    {:h (clamp-hue h)
     :s (* sv 100)
     :v (* v 100)}))

(defn hsv->hsl
  "Convert HSV to HSL. All inputs/outputs use H 0-360, S/V/L 0-100."
  [h s v]
  (let [s (/ (clamp-percent s) 100.0)
        v (/ (clamp-percent v) 100.0)
        l (* v (- 1.0 (/ s 2.0)))
        sl (if (or (zero? l) (== l 1.0))
             0.0
             (/ (- v l) (min l (- 1.0 l))))]
    {:h (clamp-hue h)
     :s (* sl 100)
     :l (* l 100)}))

;; ---------------------------------------------------------------------------
;; Coordinate math — area thumb position
;; ---------------------------------------------------------------------------
(defn sat-val->xy-pct
  "Convert HSV saturation (0-100) and value (0-100) to area thumb
   percentage positions {:x 0-100 :y 0-100}.
   x = saturation (left=0, right=100)
   y = inverted value (top=0 means V=100, bottom=100 means V=0)."
  [sat val]
  {:x (clamp-percent sat)
   :y (- 100 (clamp-percent val))})

(defn xy-pct->sat-val
  "Convert area thumb percentage positions to HSV saturation and value.
   Returns {:sat 0-100 :val 0-100}."
  [x-pct y-pct]
  {:sat (clamp-percent x-pct)
   :val (- 100 (clamp-percent y-pct))})

(defn hue->pct
  "Convert hue (0-360) to strip percentage (0-100)."
  [h]
  (* (/ (clamp-hue h) 360.0) 100.0))

(defn pct->hue
  "Convert strip percentage (0-100) to hue (0-360)."
  [pct]
  (* (/ (clamp-percent pct) 100.0) 360.0))

(defn alpha->pct
  "Convert alpha (0-1) to strip percentage (0-100)."
  [a]
  (* (clamp-alpha a) 100.0))

(defn pct->alpha
  "Convert strip percentage (0-100) to alpha (0-1)."
  [pct]
  (/ (clamp-percent pct) 100.0))

;; ---------------------------------------------------------------------------
;; Normalization helpers
;; ---------------------------------------------------------------------------
(defn normalize-value
  "Parse and normalize a hex value string. Returns valid #rrggbb or default."
  [s]
  (if (and (string? s) (valid-hex? s))
    (let [parsed (parse-hex s)]
      (when parsed
        (rgb->hex (:r parsed) (:g parsed) (:b parsed))))
    default-value))

(defn normalize-mode
  "Normalize mode attribute to 'inline' or 'popover'."
  [s]
  (if (and (string? s) (contains? allowed-modes (.toLowerCase (.trim s))))
    (.toLowerCase (.trim s))
    default-mode))

(defn parse-swatches
  "Parse comma-separated hex colour strings. Returns vector of valid hex strings."
  [s]
  (if (and (string? s) (mu/non-empty-string? s))
    (into []
          (comp
           (map str/trim)
           (filter valid-hex?)
           (map (fn [hex]
                  (let [{:keys [r g b]} (parse-hex hex)]
                    (rgb->hex r g b)))))
          (str/split s #","))
    []))

;; ---------------------------------------------------------------------------
;; Derive state — central normalization
;; ---------------------------------------------------------------------------
(defn derive-state
  "Transform raw attribute values into a normalized state map."
  [{:keys [value alpha disabled readonly name mode open label swatches]}]
  (let [hex       (normalize-value value)
        {:keys [r g b a]} (or (parse-hex value) (parse-hex default-value))
        ;; When the original value has alpha info (8-digit hex), preserve it
        has-alpha-val (and (string? value)
                          (let [v (.trim value)]
                            (and (re-matches hex-re v)
                                 (== 8 (count (second (re-matches hex-re v)))))))
        alpha-val (if has-alpha-val a 1.0)
        {:keys [h s l]} (rgb->hsl r g b)
        disabled? (mu/parse-bool-attr disabled)
        readonly? (mu/parse-bool-attr readonly)
        alpha?    (mu/parse-bool-present alpha)
        open?     (mu/parse-bool-present open)
        norm-mode (normalize-mode mode)]
    {:h         h
     :s         s
     :l         l
     :a         alpha-val
     :hex       hex
     :hex-full  (if (and alpha? (< alpha-val 1.0))
                  (rgba->hex8 r g b alpha-val)
                  hex)
     :mode      norm-mode
     :disabled? disabled?
     :readonly? readonly?
     :alpha?    alpha?
     :open?     (and (= norm-mode "popover") open?)
     :name      (when (mu/non-empty-string? name) name)
     :label     (when (mu/non-empty-string? label) label)
     :swatches  (parse-swatches swatches)}))

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------
(defn color-value-text
  "Produce a human-readable ARIA value text for the current colour."
  [h s l a]
  (let [base (str "Hue " (js/Math.round h)
                  ", Saturation " (js/Math.round s) "%"
                  ", Lightness " (js/Math.round l) "%")]
    (if (< a 1.0)
      (str base ", Opacity " (js/Math.round (* a 100)) "%")
      base)))

(defn make-detail
  "Create an event detail map for the current colour state."
  [hex h s l a]
  {:value hex :h h :s s :l l :a a})

(defn interactable?
  "Returns true when the element can accept user input."
  [{:keys [disabled? readonly?]}]
  (and (not disabled?) (not readonly?)))
