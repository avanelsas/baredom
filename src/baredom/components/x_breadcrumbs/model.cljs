(ns baredom.components.x-breadcrumbs.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name                 "x-breadcrumbs")
(def attr-separator           "separator")
(def attr-size                "size")
(def attr-variant             "variant")
(def attr-wrap                "wrap")
(def attr-max-items           "max-items")
(def attr-items-before        "items-before")
(def attr-items-after         "items-after")
(def attr-disabled            "disabled")
(def attr-preserve-aria-current "preserve-aria-current")
(def attr-aria-label          "aria-label")
(def attr-aria-describedby    "aria-describedby")

(def observed-attributes
  #js [attr-separator attr-size attr-variant attr-wrap
       attr-max-items attr-items-before attr-items-after
       attr-disabled attr-preserve-aria-current
       attr-aria-label attr-aria-describedby])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def ^:private valid-sizes    #{"sm" "md" "lg"})
(def ^:private valid-variants #{"default" "subtle" "text"})

(def default-size          "md")
(def default-variant       "default")
(def default-separator     "/")
(def default-items-before  1)
(def default-items-after   2)

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-size    [s] (parse-enum valid-sizes    default-size    s))
(defn parse-variant [s] (parse-enum valid-variants default-variant s))

(defn parse-bool-attr [s]
  (and (some? s) (not= s "false")))

(defn parse-pos-int
  "Parse string to positive integer ≥ 1, returning `fallback` on failure."
  [s fallback]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (js/isFinite n) (pos? n)) n fallback))
    fallback))

(defn parse-pos-int-or-nil
  "Parse string to positive integer ≥ 1, returning nil on failure."
  [s]
  (when (string? s)
    (let [n (js/parseInt s 10)]
      (when (and (js/isFinite n) (pos? n)) n))))

(defn normalize-separator [s]
  (if (string? s) s default-separator))

;; ── Build plan ────────────────────────────────────────────────────────────
(defn build-plan
  "Return a map describing which items to display and where the ellipsis goes.

   Returns:
   {:visible     [0 1 4 5]  ; indices of visible items in order
    :ellipsis-at 2          ; insert ellipsis before this position in :visible (-1 = none)
    :total       6}"
  [total max-items items-before items-after]
  (if (or (nil? max-items) (<= total max-items))
    {:visible     (vec (range total))
     :ellipsis-at -1
     :total       total}
    (let [before  (min items-before (max 0 (- total items-after)))
          after   (min items-after  (max 0 (- total before)))
          ;; Clamp again after mutually constraining
          before  (min before (- total after))
          before  (max 0 before)
          before-idxs (vec (range before))
          after-idxs  (vec (range (- total after) total))]
      {:visible     (into before-idxs after-idxs)
       :ellipsis-at (count before-idxs)
       :total       total})))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [separator-raw size-raw variant-raw wrap-raw max-items-raw
           items-before-raw items-after-raw disabled-present?
           preserve-aria-current-present? aria-label-raw aria-describedby-raw]}]
  {:separator             (normalize-separator separator-raw)
   :size                  (parse-size    size-raw)
   :variant               (parse-variant variant-raw)
   :wrap                  (parse-bool-attr wrap-raw)
   :max-items             (parse-pos-int-or-nil max-items-raw)
   :items-before          (parse-pos-int items-before-raw default-items-before)
   :items-after           (parse-pos-int items-after-raw  default-items-after)
   :disabled              (boolean disabled-present?)
   :preserve-aria-current (boolean preserve-aria-current-present?)
   :aria-label            aria-label-raw
   :aria-describedby      aria-describedby-raw})

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:separator    {:type 'string}
   :size         {:type 'string}
   :variant      {:type 'string}
   :wrap         {:type 'boolean}
   :max-items    {:type 'number}
   :items-before {:type 'number}
   :items-after  {:type 'number}
   :disabled     {:type 'boolean}})
