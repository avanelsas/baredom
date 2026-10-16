(ns baredom.components.x-breadcrumbs.model
  (:require [baredom.utils.model :as mu]))

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
(def valid-sizes    #{"sm" "md" "lg"})
(def valid-variants #{"default" "subtle" "text"})

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

;; ── List items ────────────────────────────────────────────────────────────
(def ^:private ellipsis-item {:kind :ellipsis :key "ellipsis"})

(defn- crumb-item [k index current?]
  {:kind :crumb :key (str "crumb " k) :index index :current? current?})

(defn- separator-item [k]
  {:kind :separator :key (str "separator " k)})

(defn- items-at
  "The items that show the visible child `index` at `pos`, with what comes before it."
  [crumb-keys ellipsis-at current-pos pos index]
  (let [k     (nth crumb-keys index)
        crumb (crumb-item k index (= pos current-pos))]
    (cond
      (= pos ellipsis-at) [ellipsis-item (separator-item k) crumb]
      (pos? pos)          [(separator-item k) crumb]
      :else               [crumb])))

(defn list-items
  "Return the items of the list in order: crumbs, separators and the ellipsis, each with a `:key`.
   A crumb has the `:index` of its child and `:current?`."
  [{:keys [visible ellipsis-at]} crumb-keys preserve-aria-current?]
  (let [current-pos (when-not preserve-aria-current? (dec (count visible)))]
    (cond-> (vec (mapcat (partial items-at crumb-keys ellipsis-at current-pos) (range) visible))
      (= ellipsis-at (count visible)) (conj ellipsis-item))))

(defn- items-of
  "The list items of the parsed model `m` for the children with `crumb-keys`."
  [{:keys [max-items items-before items-after preserve-aria-current]} crumb-keys]
  (list-items (build-plan (count crumb-keys) max-items items-before items-after)
              crumb-keys preserve-aria-current))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [separator-raw size-raw variant-raw wrap-raw max-items-raw
           items-before-raw items-after-raw disabled-present?
           preserve-aria-current-present? aria-label-raw aria-describedby-raw
           crumb-htmls]}]
  (let [m {:separator             (normalize-separator separator-raw)
           :size                  (parse-size    size-raw)
           :variant               (parse-variant variant-raw)
           :wrap                  (mu/parse-bool-attr wrap-raw)
           :max-items             (parse-pos-int-or-nil max-items-raw)
           :items-before          (parse-pos-int items-before-raw default-items-before)
           :items-after           (parse-pos-int items-after-raw  default-items-after)
           :disabled              (boolean disabled-present?)
           :preserve-aria-current (boolean preserve-aria-current-present?)
           :aria-label            aria-label-raw
           :aria-describedby      aria-describedby-raw}]
    (assoc m :items (items-of m (mu/unique-keys crumb-htmls)))))

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:separator           {:type 'string  :reflects-attribute attr-separator}
   :size                {:type 'string  :reflects-attribute attr-size :enum valid-sizes}
   :variant             {:type 'string  :reflects-attribute attr-variant :enum valid-variants}
   :wrap                {:type 'boolean :reflects-attribute attr-wrap}
   :maxItems            {:type 'number  :reflects-attribute attr-max-items}
   :itemsBefore         {:type 'number  :reflects-attribute attr-items-before}
   :itemsAfter          {:type 'number  :reflects-attribute attr-items-after}
   :disabled            {:type 'boolean :reflects-attribute attr-disabled}
   :preserveAriaCurrent {:type 'boolean :reflects-attribute attr-preserve-aria-current}})

(def event-schema {})

(def method-api {})
