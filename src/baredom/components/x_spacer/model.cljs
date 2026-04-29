(ns baredom.components.x-spacer.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name  "x-spacer")
(def attr-size "size")
(def attr-axis "axis")
(def attr-grow "grow")

(def observed-attributes
  #js [attr-size attr-axis attr-grow])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def ^:private valid-axes #{"vertical" "horizontal"})

(def default-axis "vertical")
(def default-size "1rem")

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn parse-axis
  "Normalise axis attribute. Unknown / nil values fall back to 'vertical'."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid-axes v) v default-axis)))

(defn parse-grow
  "Returns true when the grow attribute is present and not explicitly 'false'.
  grow is false when absent (nil), true when present (empty string or any value
  other than 'false')."
  [s]
  (and (some? s) (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-size
  "Returns the size value, or the default when nil/blank."
  [s]
  (if (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)
    default-size))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :size-raw   string | nil
    :axis-raw   string | nil
    :grow-raw   string | nil

  Output keys:
    :size   string   — CSS length value
    :axis   string   — 'vertical' | 'horizontal'
    :grow?  boolean  — true when grow attribute is present"
  [{:keys [size-raw axis-raw grow-raw]}]
  {:size  (parse-size size-raw)
   :axis  (parse-axis axis-raw)
   :grow? (parse-grow grow-raw)})

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:size {:type 'string}
   :axis {:type 'string}
   :grow {:type 'boolean}})

(def method-api nil)
