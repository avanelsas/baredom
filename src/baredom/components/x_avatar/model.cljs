(ns baredom.components.x-avatar.model
  (:require [baredom.utils.model :as utils]))

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name        "x-avatar")
(def attr-src        "src")
(def attr-alt        "alt")
(def attr-name       "name")
(def attr-initials   "initials")
(def attr-size       "size")
(def attr-shape      "shape")
(def attr-variant    "variant")
(def attr-status     "status")
(def attr-disabled   "disabled")

(def observed-attributes
  #js [attr-src attr-alt attr-name attr-initials
       attr-size attr-shape attr-variant attr-status attr-disabled])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def ^:private valid-sizes    #{"xs" "sm" "md" "lg" "xl"})
(def ^:private valid-shapes   #{"circle" "square" "rounded"})
(def ^:private valid-variants #{"neutral" "brand" "subtle"})
(def ^:private valid-statuses #{"online" "offline" "busy" "away"})

(def default-size    "md")
(def default-shape   "circle")
(def default-variant "neutral")

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-size    [s] (parse-enum valid-sizes    default-size    s))
(defn parse-shape   [s] (parse-enum valid-shapes   default-shape   s))
(defn parse-variant [s] (parse-enum valid-variants default-variant s))

(defn parse-status [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (when (contains? valid-statuses v) v)))

;; ── Text helpers ──────────────────────────────────────────────────────────
(defn normalize-text [s]
  (when (string? s)
    (let [v (.trim s)]
      (when-not (= v "") v))))

(defn normalize-initials [s]
  (when-let [v (normalize-text s)]
    (if (> (.-length v) 3) (.slice v 0 3) v)))

(defn derive-initials [name-str]
  (when-let [n (normalize-text name-str)]
    (let [parts (vec (remove #(= % "") (.split n #"\s+")))]
      (when (seq parts)
        (let [a (aget (first parts) 0)
              b (when (>= (count parts) 2) (aget (second parts) 0))
              s (str a (or b ""))]
          (when-not (= s "") (.toUpperCase s)))))))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize [{:keys [src-raw alt-raw name-raw initials-raw
                         size-raw shape-raw variant-raw status-raw
                         disabled-present?]}]
  (let [src      (when-let [s (normalize-text src-raw)]
                   (let [safe (utils/sanitize-url s)]
                     (when (not= safe "") safe)))
        alt      (normalize-text alt-raw)
        nm       (normalize-text name-raw)
        initials (normalize-initials initials-raw)
        derived  (when (nil? initials) (derive-initials nm))]
    {:src      src
     :alt      alt
     :name     nm
     :initials initials
     :derived  derived
     :size     (parse-size size-raw)
     :shape    (parse-shape shape-raw)
     :variant  (parse-variant variant-raw)
     :status   (parse-status status-raw)
     :disabled (boolean disabled-present?)}))

;; ── View-model helpers ────────────────────────────────────────────────────
(defn display-text
  "Return the string to render in [part=initials], or nil (show fallback)."
  [{:keys [initials derived]}]
  (or initials derived))

(defn label
  "Return the accessible label string, or nil (mark as aria-hidden)."
  [{:keys [alt name]}]
  (or alt name))

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:src      {:type 'string}
   :alt      {:type 'string}
   :name     {:type 'string}
   :initials {:type 'string}
   :size     {:type 'string}
   :shape    {:type 'string}
   :variant  {:type 'string}
   :status   {:type 'string}
   :disabled {:type 'boolean}})

(def method-api nil)
