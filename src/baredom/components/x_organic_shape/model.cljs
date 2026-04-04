(ns baredom.components.x-organic-shape.model)

(def tag-name "x-organic-shape")

(def attr-shape   "shape")
(def attr-path    "path")
(def attr-animation "animation")
(def attr-ratio   "ratio")
(def attr-width   "width")
(def attr-height  "height")

(def observed-attributes
  #js [attr-shape attr-path attr-animation attr-ratio attr-width attr-height])

(def property-api
  {:shape   {:type 'string  :reflects-attribute attr-shape}
   :path    {:type 'string  :reflects-attribute attr-path}
   :animation {:type 'string :reflects-attribute attr-animation}
   :ratio   {:type 'string  :reflects-attribute attr-ratio}
   :width   {:type 'string  :reflects-attribute attr-width}
   :height  {:type 'string  :reflects-attribute attr-height}})

(def event-schema {})

(def default-shape     "blob-1")
(def default-ratio     "1/1")
(def default-animation "none")

(def allowed-animations #{"none" "morph" "pulse" "float" "spin"})

(def allowed-shapes
  #{"blob-1" "blob-2" "blob-3" "pebble" "leaf" "droplet" "cloud" "wave"})

;; Each preset has a :clip (static) and :clip-alt (morph target for animation).
;; Paths use percentage-based polygon() or path() values so they scale with the element.
(def shape-presets
  {"blob-1"  {:clip     "polygon(30% 0%, 70% 0%, 100% 30%, 95% 70%, 70% 100%, 30% 95%, 0% 70%, 5% 30%)"
              :clip-alt "polygon(25% 5%, 75% 0%, 95% 25%, 100% 65%, 75% 95%, 25% 100%, 0% 65%, 5% 25%)"}

   "blob-2"  {:clip     "polygon(50% 0%, 80% 10%, 100% 40%, 95% 75%, 70% 100%, 30% 100%, 5% 75%, 0% 40%, 20% 10%)"
              :clip-alt "polygon(50% 5%, 85% 15%, 100% 45%, 90% 80%, 65% 95%, 35% 95%, 10% 80%, 0% 45%, 15% 15%)"}

   "blob-3"  {:clip     "polygon(20% 0%, 80% 0%, 100% 20%, 100% 50%, 90% 80%, 70% 100%, 30% 100%, 10% 80%, 0% 50%, 0% 20%)"
              :clip-alt "polygon(25% 5%, 75% 5%, 95% 25%, 100% 55%, 85% 85%, 65% 95%, 35% 95%, 15% 85%, 0% 55%, 5% 25%)"}

   "pebble"  {:clip     "polygon(50% 2%, 78% 8%, 96% 30%, 98% 60%, 85% 88%, 55% 98%, 25% 92%, 4% 65%, 2% 35%, 22% 8%)"
              :clip-alt "polygon(50% 5%, 75% 12%, 93% 32%, 96% 58%, 82% 85%, 52% 96%, 28% 90%, 8% 62%, 5% 38%, 25% 12%)"}

   "leaf"    {:clip     "polygon(50% 0%, 80% 15%, 98% 45%, 90% 75%, 65% 98%, 50% 100%, 35% 98%, 10% 75%, 2% 45%, 20% 15%)"
              :clip-alt "polygon(50% 3%, 78% 18%, 95% 42%, 88% 72%, 62% 95%, 50% 97%, 38% 95%, 12% 72%, 5% 42%, 22% 18%)"}

   "droplet" {:clip     "polygon(50% 0%, 72% 20%, 90% 50%, 95% 72%, 80% 92%, 50% 100%, 20% 92%, 5% 72%, 10% 50%, 28% 20%)"
              :clip-alt "polygon(50% 5%, 70% 22%, 88% 48%, 92% 70%, 78% 90%, 50% 97%, 22% 90%, 8% 70%, 12% 48%, 30% 22%)"}

   "cloud"   {:clip     "polygon(10% 60%, 0% 45%, 5% 30%, 18% 20%, 32% 15%, 45% 8%, 58% 12%, 72% 8%, 85% 18%, 95% 32%, 100% 50%, 95% 65%, 80% 75%, 55% 80%, 30% 78%, 15% 72%)"
              :clip-alt "polygon(12% 58%, 2% 43%, 8% 28%, 20% 18%, 35% 12%, 48% 10%, 60% 14%, 75% 10%, 88% 20%, 97% 35%, 98% 52%, 92% 68%, 78% 78%, 52% 82%, 28% 80%, 13% 70%)"}

   "wave"    {:clip     "polygon(0% 35%, 15% 20%, 30% 30%, 50% 15%, 70% 28%, 85% 18%, 100% 30%, 100% 75%, 85% 85%, 70% 72%, 50% 85%, 30% 75%, 15% 82%, 0% 70%)"
              :clip-alt "polygon(0% 30%, 15% 25%, 30% 35%, 50% 20%, 70% 32%, 85% 22%, 100% 35%, 100% 70%, 85% 80%, 70% 68%, 50% 80%, 30% 70%, 15% 78%, 0% 65%)"}})

(defn normalize-shape [s]
  (if (and (string? s) (contains? allowed-shapes s))
    s
    default-shape))

(defn normalize-ratio [s]
  (if (and (string? s) (not= "" (.trim s)))
    (.trim s)
    default-ratio))

(defn normalize-path [s]
  (when (and (string? s) (not= "" (.trim s)))
    (.trim s)))

(defn normalize-css-value [v]
  (when (and (string? v) (not= "" v)) v))

(defn normalize-animation [s]
  (if (and (string? s) (contains? allowed-animations s))
    s
    default-animation))

(defn resolve-clip-path
  "Returns the effective clip-path value. Custom path takes precedence over preset."
  [path shape]
  (if (some? path)
    path
    (get-in shape-presets [(normalize-shape shape) :clip])))

(defn resolve-clip-path-alt
  "Returns the morph-target clip-path for animation. nil when using custom path."
  [path shape]
  (when (nil? path)
    (get-in shape-presets [(normalize-shape shape) :clip-alt])))

(defn derive-state [{:keys [shape path animation ratio width height]}]
  (let [norm-path      (normalize-path path)
        norm-shape     (normalize-shape shape)
        norm-animation (normalize-animation animation)
        clip           (resolve-clip-path norm-path norm-shape)
        clip-alt       (resolve-clip-path-alt norm-path norm-shape)
        ;; morph requires clip-alt and no custom path
        effective-anim (if (and (= norm-animation "morph") (nil? clip-alt))
                         "none"
                         norm-animation)]
    {:shape     norm-shape
     :path      norm-path
     :clip      clip
     :clip-alt  clip-alt
     :animation effective-anim
     :ratio     (normalize-ratio ratio)
     :width     (normalize-css-value width)
     :height    (normalize-css-value height)}))
