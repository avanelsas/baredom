(ns app.components.x-spinner.model)

(def tag-name "x-spinner")

(def attr-size    "size")
(def attr-variant "variant")
(def attr-label   "label")

(def observed-attributes
  #js [attr-size attr-variant attr-label])

(def property-api
  {:size    {:type 'string :reflects-attribute attr-size}
   :variant {:type 'string :reflects-attribute attr-variant}
   :label   {:type 'string :reflects-attribute attr-label}})

;; ── Allowed values ────────────────────────────────────────────────────────
(def allowed-sizes    #{"xs" "sm" "md" "lg" "xl"})
(def allowed-variants #{"default" "primary" "success" "warning" "danger"})

(def default-size    "md")
(def default-variant "default")
(def default-label   "Loading")

;; ── Normalisation ─────────────────────────────────────────────────────────
(defn normalize-size
  "Return size string if valid, otherwise default-size."
  [s]
  (if (and (string? s) (contains? allowed-sizes s))
    s
    default-size))

(defn normalize-variant
  "Return variant string if valid, otherwise default-variant."
  [s]
  (if (and (string? s) (contains? allowed-variants s))
    s
    default-variant))

(defn normalize-label
  "Return trimmed label string or default-label when absent/blank."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (.trim s)
    default-label))

;; ── Derived state ─────────────────────────────────────────────────────────
(defn derive-state
  "Produce a stable view-model map from raw attribute strings.

  Input keys:
    :size    string | nil
    :variant string | nil
    :label   string | nil

  Output keys:
    :size    string  — valid size enum value
    :variant string  — valid variant enum value
    :label   string  — accessible label text"
  [{:keys [size variant label]}]
  {:size    (normalize-size size)
   :variant (normalize-variant variant)
   :label   (normalize-label label)})
