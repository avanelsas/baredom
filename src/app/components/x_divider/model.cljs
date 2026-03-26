(ns app.components.x-divider.model)

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name        "x-divider")
(def attr-orientation "orientation")
(def attr-variant     "variant")
(def attr-thickness   "thickness")
(def attr-color       "color")
(def attr-inset       "inset")
(def attr-length      "length")
(def attr-label       "label")
(def attr-align       "align")
(def attr-role        "role")
(def attr-aria-label  "aria-label")

(def observed-attributes
  #js [attr-orientation attr-variant attr-thickness attr-color
       attr-inset attr-length attr-label attr-align attr-role attr-aria-label])

;; ── Valid enum sets & defaults ────────────────────────────────────────────
(def ^:private valid-orientations #{"horizontal" "vertical"})
(def ^:private valid-variants     #{"solid" "dashed" "dotted"})
(def ^:private valid-aligns       #{"center" "start" "end"})

(def default-orientation "horizontal")
(def default-variant     "solid")
(def default-align       "center")

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-orientation [s] (parse-enum valid-orientations default-orientation s))
(defn parse-variant     [s] (parse-enum valid-variants     default-variant     s))
(defn parse-align       [s] (parse-enum valid-aligns       default-align       s))

(defn separator-role?
  "Returns true when the divider should carry role=separator.
   False when role is 'presentation' or 'none'."
  [role]
  (not (contains? #{"presentation" "none"} role)))

(defn has-label?
  "Returns true when label is a non-empty string."
  [label]
  (and (string? label) (pos? (.-length (.trim label)))))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [orientation-raw variant-raw thickness-raw color-raw inset-raw
           length-raw label-raw align-raw role-raw aria-label-raw]}]
  {:orientation (parse-orientation orientation-raw)
   :variant     (parse-variant variant-raw)
   :align       (parse-align align-raw)
   :thickness   thickness-raw
   :color       color-raw
   :inset       inset-raw
   :length      length-raw
   :label       label-raw
   :role        role-raw
   :aria-label  aria-label-raw})

;; ── Property API metadata ─────────────────────────────────────────────────
(def property-api
  {:orientation {:type 'string}
   :variant     {:type 'string}
   :align       {:type 'string}
   :label       {:type 'string}
   :thickness   {:type 'string}
   :color       {:type 'string}
   :inset       {:type 'string}
   :length      {:type 'string}})
