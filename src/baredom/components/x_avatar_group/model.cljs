(ns baredom.components.x-avatar-group.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name       "x-avatar-group")
(def attr-size      "size")
(def attr-overlap   "overlap")
(def attr-max       "max")
(def attr-direction "direction")
(def attr-disabled  "disabled")
(def attr-label     "label")

(def observed-attributes
  #js [attr-size attr-overlap attr-max attr-direction attr-disabled attr-label])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def valid-sizes      #{"xs" "sm" "md" "lg" "xl"})
(def valid-overlaps   #{"none" "sm" "md" "lg"})
(def valid-directions #{"ltr" "rtl"})

(def default-size      "md")
(def default-overlap   "md")
(def default-direction "ltr")

;; Overlap margin values (negative margin-inline-start applied to non-first children)
(def overlap-margin
  {"none" "0px"
   "sm"   "-4px"
   "md"   "-8px"
   "lg"   "-12px"})

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-size      [s] (parse-enum valid-sizes      default-size      s))
(defn parse-overlap   [s] (parse-enum valid-overlaps   default-overlap   s))
(defn parse-direction [s] (parse-enum valid-directions default-direction s))

(defn parse-max [s]
  (when (string? s)
    (let [n (js/parseInt s 10)]
      (when (and (js/isFinite n) (pos? n)) n))))

(defn normalize-label [s]
  (when (string? s)
    (let [v (.trim s)]
      (when-not (= v "") v))))

;; ── View-model helpers ────────────────────────────────────────────────────
(defn compute-visible-hidden
  "Given total item count and optional max, return [visible-count hidden-count]."
  [total max]
  (if (and max (> total max))
    [max (- total max)]
    [total 0]))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [size-raw overlap-raw max-raw direction-raw disabled-present? label-raw]}]
  {:size      (parse-size      size-raw)
   :overlap   (parse-overlap   overlap-raw)
   :max       (parse-max       max-raw)
   :direction (parse-direction direction-raw)
   :disabled  (boolean         disabled-present?)
   :label     (normalize-label label-raw)})

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:size      {:type 'string  :reflects-attribute attr-size :enum valid-sizes}
   :overlap   {:type 'string  :reflects-attribute attr-overlap :enum valid-overlaps}
   :max       {:type 'number  :reflects-attribute attr-max}
   :direction {:type 'string  :reflects-attribute attr-direction :enum valid-directions}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :label     {:type 'string  :reflects-attribute attr-label}})

(def event-schema {})

(def method-api {})
