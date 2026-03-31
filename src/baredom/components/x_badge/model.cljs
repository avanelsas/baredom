(ns baredom.components.x-badge.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name              "x-badge")
(def attr-variant          "variant")
(def attr-size             "size")
(def attr-pill             "pill")
(def attr-dot              "dot")
(def attr-count            "count")
(def attr-max              "max")
(def attr-text             "text")
(def attr-aria-label       "aria-label")
(def attr-aria-describedby "aria-describedby")

(def observed-attributes
  #js [attr-variant attr-size attr-pill attr-dot attr-count attr-max attr-text
       attr-aria-label attr-aria-describedby])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def ^:private valid-variants #{"neutral" "info" "success" "warning" "error"})
(def ^:private valid-sizes    #{"sm" "md"})

(def default-variant "neutral")
(def default-size    "md")
(def default-max     99)

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-variant [s] (parse-enum valid-variants default-variant s))
(defn parse-size    [s] (parse-enum valid-sizes    default-size    s))

(defn parse-int-attr
  "Parse string to non-negative integer, returning `fallback` on failure."
  [s fallback]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (js/isFinite n) (>= n 0)) n fallback))
    fallback))

(defn parse-bool-attr [s]
  (and (some? s) (not= s "false")))

;; ── Display mode ──────────────────────────────────────────────────────────
(defn compute-mode
  "Determine badge display mode.
   :slot  — custom slotted content (overrides everything)
   :count — numeric count display
   :text  — string text display
   :dot   — dot-only (no text)
   :empty — hidden / no content"
  [{:keys [has-slot? count text dot]}]
  (cond
    has-slot?     :slot
    (some? count) :count
    (some? text)  :text
    dot           :dot
    :else         :empty))

(defn display-text
  "Return the string to render in [part=label], or nil."
  [{:keys [count max text] :as m}]
  (case (compute-mode m)
    :count (if (> count max) (str max "+") (str count))
    :text  text
    nil))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [variant-raw size-raw pill-raw dot-raw count-raw max-raw
           text-raw aria-label-raw aria-describedby-raw has-slot?]}]
  (let [max-val   (parse-int-attr max-raw default-max)
        count-val (when count-raw
                    (let [n (parse-int-attr count-raw nil)]
                      (when (some? n) n)))]
    {:variant          (parse-variant variant-raw)
     :size             (parse-size size-raw)
     :pill             (parse-bool-attr pill-raw)
     :dot              (parse-bool-attr dot-raw)
     :count            count-val
     :max              max-val
     :text             (when (string? text-raw)
                         (let [v (.trim text-raw)]
                           (when-not (= v "") v)))
     :aria-label       aria-label-raw
     :aria-describedby aria-describedby-raw
     :has-slot?        (boolean has-slot?)}))

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:variant {:type 'string}
   :size    {:type 'string}
   :pill    {:type 'boolean}
   :dot     {:type 'boolean}
   :count   {:type 'number}
   :max     {:type 'number}
   :text    {:type 'string}})
