(ns baredom.components.x-theme.model
  (:require [goog.object :as gobj]))

(def tag-name "x-theme")

(def attr-preset "preset")

(def observed-attributes #js [attr-preset])

(def default-preset "default")

(def property-api
  {:preset {:type 'string}})

(def event-schema {})

;; ── Token name constants ────────────────────────────────────────────────────

(def tk-color-primary          "--x-color-primary")
(def tk-color-primary-hover    "--x-color-primary-hover")
(def tk-color-primary-active   "--x-color-primary-active")
(def tk-color-secondary        "--x-color-secondary")
(def tk-color-secondary-hover  "--x-color-secondary-hover")
(def tk-color-secondary-active "--x-color-secondary-active")
(def tk-color-tertiary         "--x-color-tertiary")
(def tk-color-tertiary-hover   "--x-color-tertiary-hover")
(def tk-color-tertiary-active  "--x-color-tertiary-active")
(def tk-color-surface          "--x-color-surface")
(def tk-color-surface-hover    "--x-color-surface-hover")
(def tk-color-surface-active   "--x-color-surface-active")
(def tk-color-bg               "--x-color-bg")
(def tk-color-text             "--x-color-text")
(def tk-color-text-muted       "--x-color-text-muted")
(def tk-color-border           "--x-color-border")
(def tk-color-focus-ring       "--x-color-focus-ring")
(def tk-color-danger           "--x-color-danger")
(def tk-color-success          "--x-color-success")
(def tk-color-warning          "--x-color-warning")

(def tk-font-family            "--x-font-family")
(def tk-font-family-mono       "--x-font-family-mono")
(def tk-font-size-sm           "--x-font-size-sm")
(def tk-font-size-base         "--x-font-size-base")

(def tk-radius-sm              "--x-radius-sm")
(def tk-radius-md              "--x-radius-md")
(def tk-radius-lg              "--x-radius-lg")
(def tk-radius-full            "--x-radius-full")

(def tk-shadow-sm              "--x-shadow-sm")
(def tk-shadow-md              "--x-shadow-md")
(def tk-shadow-lg              "--x-shadow-lg")

(def tk-transition-duration    "--x-transition-duration")
(def tk-transition-easing      "--x-transition-easing")

;; ── Shared non-color defaults ───────────────────────────────────────────────

(def ^:private base-typography
  {tk-font-family      "system-ui,-apple-system,sans-serif"
   tk-font-family-mono "ui-monospace,'SF Mono',monospace"
   tk-font-size-sm     "0.875rem"
   tk-font-size-base   "1rem"})

(def ^:private base-shape
  {tk-radius-sm   "0.375rem"
   tk-radius-md   "0.75rem"
   tk-radius-lg   "1rem"
   tk-radius-full "9999px"})

(def ^:private base-motion
  {tk-transition-duration "140ms"
   tk-transition-easing   "cubic-bezier(0.2,0,0,1)"})

(def ^:private base-shadow-light
  {tk-shadow-sm "0 1px 2px rgba(15,23,42,0.08)"
   tk-shadow-md "0 4px 10px rgba(15,23,42,0.10),0 2px 4px rgba(15,23,42,0.06)"
   tk-shadow-lg "0 10px 25px rgba(15,23,42,0.12),0 4px 10px rgba(15,23,42,0.08)"})

(def ^:private base-shadow-dark
  {tk-shadow-sm "0 1px 2px rgba(0,0,0,0.35)"
   tk-shadow-md "0 6px 14px rgba(0,0,0,0.35),0 2px 6px rgba(0,0,0,0.24)"
   tk-shadow-lg "0 12px 30px rgba(0,0,0,0.40),0 4px 12px rgba(0,0,0,0.30)"})

;; ── Built-in presets ────────────────────────────────────────────────────────

(def ^:private preset-default
  {:light (merge base-typography base-shape base-motion base-shadow-light
                 {tk-color-primary          "#2563eb"
                  tk-color-primary-hover    "#1d4ed8"
                  tk-color-primary-active   "#1e40af"
                  tk-color-secondary        "#64748b"
                  tk-color-secondary-hover  "#475569"
                  tk-color-secondary-active "#334155"
                  tk-color-tertiary         "#94a3b8"
                  tk-color-tertiary-hover   "#64748b"
                  tk-color-tertiary-active  "#475569"
                  tk-color-surface          "#ffffff"
                  tk-color-surface-hover    "#f8fafc"
                  tk-color-surface-active   "#f1f5f9"
                  tk-color-bg               "#f8fafc"
                  tk-color-text             "#0f172a"
                  tk-color-text-muted       "#64748b"
                  tk-color-border           "#cbd5e1"
                  tk-color-focus-ring       "#60a5fa"
                  tk-color-danger           "#dc2626"
                  tk-color-success          "#16a34a"
                  tk-color-warning          "#d97706"})
   :dark  (merge base-shadow-dark
                 {tk-color-primary          "#3b82f6"
                  tk-color-primary-hover    "#2563eb"
                  tk-color-primary-active   "#1d4ed8"
                  tk-color-secondary        "#94a3b8"
                  tk-color-secondary-hover  "#64748b"
                  tk-color-secondary-active "#475569"
                  tk-color-tertiary         "#64748b"
                  tk-color-tertiary-hover   "#94a3b8"
                  tk-color-tertiary-active  "#64748b"
                  tk-color-surface          "#111827"
                  tk-color-surface-hover    "#1f2937"
                  tk-color-surface-active   "#273449"
                  tk-color-bg               "#0f172a"
                  tk-color-text             "#f1f5f9"
                  tk-color-text-muted       "#94a3b8"
                  tk-color-border           "#374151"
                  tk-color-focus-ring       "#93c5fd"
                  tk-color-danger           "#ef4444"
                  tk-color-success          "#22c55e"
                  tk-color-warning          "#f59e0b"})})

(def ^:private preset-ocean
  {:light (merge base-typography base-shape base-motion base-shadow-light
                 {tk-color-primary          "#0891b2"
                  tk-color-primary-hover    "#0e7490"
                  tk-color-primary-active   "#155e75"
                  tk-color-secondary        "#0d9488"
                  tk-color-secondary-hover  "#0f766e"
                  tk-color-secondary-active "#115e59"
                  tk-color-tertiary         "#5eead4"
                  tk-color-tertiary-hover   "#2dd4bf"
                  tk-color-tertiary-active  "#14b8a6"
                  tk-color-surface          "#ffffff"
                  tk-color-surface-hover    "#f0fdfa"
                  tk-color-surface-active   "#ccfbf1"
                  tk-color-bg               "#f0fdfa"
                  tk-color-text             "#134e4a"
                  tk-color-text-muted       "#5f7a7a"
                  tk-color-border           "#99f6e4"
                  tk-color-focus-ring       "#22d3ee"
                  tk-color-danger           "#e11d48"
                  tk-color-success          "#059669"
                  tk-color-warning          "#d97706"})
   :dark  (merge base-shadow-dark
                 {tk-color-primary          "#06b6d4"
                  tk-color-primary-hover    "#0891b2"
                  tk-color-primary-active   "#0e7490"
                  tk-color-secondary        "#14b8a6"
                  tk-color-secondary-hover  "#0d9488"
                  tk-color-secondary-active "#0f766e"
                  tk-color-tertiary         "#2dd4bf"
                  tk-color-tertiary-hover   "#5eead4"
                  tk-color-tertiary-active  "#2dd4bf"
                  tk-color-surface          "#0c1a1f"
                  tk-color-surface-hover    "#133e4a"
                  tk-color-surface-active   "#164e5e"
                  tk-color-bg               "#061216"
                  tk-color-text             "#ecfdf5"
                  tk-color-text-muted       "#5eead4"
                  tk-color-border           "#164e63"
                  tk-color-focus-ring       "#67e8f9"
                  tk-color-danger           "#fb7185"
                  tk-color-success          "#34d399"
                  tk-color-warning          "#fbbf24"})})

(def ^:private preset-forest
  {:light (merge base-typography base-shape base-motion base-shadow-light
                 {tk-color-primary          "#15803d"
                  tk-color-primary-hover    "#166534"
                  tk-color-primary-active   "#14532d"
                  tk-color-secondary        "#65a30d"
                  tk-color-secondary-hover  "#4d7c0f"
                  tk-color-secondary-active "#3f6212"
                  tk-color-tertiary         "#a3e635"
                  tk-color-tertiary-hover   "#84cc16"
                  tk-color-tertiary-active  "#65a30d"
                  tk-color-surface          "#fffbeb"
                  tk-color-surface-hover    "#fef9c3"
                  tk-color-surface-active   "#fef3c7"
                  tk-color-bg               "#fefce8"
                  tk-color-text             "#1a2e05"
                  tk-color-text-muted       "#65753a"
                  tk-color-border           "#d9d2b0"
                  tk-color-focus-ring       "#4ade80"
                  tk-color-danger           "#dc2626"
                  tk-color-success          "#15803d"
                  tk-color-warning          "#ca8a04"})
   :dark  (merge base-shadow-dark
                 {tk-color-primary          "#22c55e"
                  tk-color-primary-hover    "#16a34a"
                  tk-color-primary-active   "#15803d"
                  tk-color-secondary        "#84cc16"
                  tk-color-secondary-hover  "#65a30d"
                  tk-color-secondary-active "#4d7c0f"
                  tk-color-tertiary         "#a3e635"
                  tk-color-tertiary-hover   "#bef264"
                  tk-color-tertiary-active  "#a3e635"
                  tk-color-surface          "#1a1f12"
                  tk-color-surface-hover    "#2a3318"
                  tk-color-surface-active   "#354020"
                  tk-color-bg               "#111408"
                  tk-color-text             "#f0fdf4"
                  tk-color-text-muted       "#86efac"
                  tk-color-border           "#3a4a24"
                  tk-color-focus-ring       "#86efac"
                  tk-color-danger           "#f87171"
                  tk-color-success          "#4ade80"
                  tk-color-warning          "#fbbf24"})})

(def ^:private preset-sunset
  {:light (merge base-typography base-shape base-motion base-shadow-light
                 {tk-color-primary          "#ea580c"
                  tk-color-primary-hover    "#c2410c"
                  tk-color-primary-active   "#9a3412"
                  tk-color-secondary        "#e11d48"
                  tk-color-secondary-hover  "#be123c"
                  tk-color-secondary-active "#9f1239"
                  tk-color-tertiary         "#fb923c"
                  tk-color-tertiary-hover   "#f97316"
                  tk-color-tertiary-active  "#ea580c"
                  tk-color-surface          "#ffffff"
                  tk-color-surface-hover    "#fff7ed"
                  tk-color-surface-active   "#ffedd5"
                  tk-color-bg               "#fffbf5"
                  tk-color-text             "#431407"
                  tk-color-text-muted       "#9a7b6b"
                  tk-color-border           "#fed7aa"
                  tk-color-focus-ring       "#fb923c"
                  tk-color-danger           "#be123c"
                  tk-color-success          "#16a34a"
                  tk-color-warning          "#ca8a04"})
   :dark  (merge base-shadow-dark
                 {tk-color-primary          "#f97316"
                  tk-color-primary-hover    "#ea580c"
                  tk-color-primary-active   "#c2410c"
                  tk-color-secondary        "#fb7185"
                  tk-color-secondary-hover  "#f43f5e"
                  tk-color-secondary-active "#e11d48"
                  tk-color-tertiary         "#fdba74"
                  tk-color-tertiary-hover   "#fb923c"
                  tk-color-tertiary-active  "#f97316"
                  tk-color-surface          "#1c1210"
                  tk-color-surface-hover    "#2a1c17"
                  tk-color-surface-active   "#3d261d"
                  tk-color-bg               "#140d0a"
                  tk-color-text             "#fff7ed"
                  tk-color-text-muted       "#fdba74"
                  tk-color-border           "#4a2d1f"
                  tk-color-focus-ring       "#fdba74"
                  tk-color-danger           "#fb7185"
                  tk-color-success          "#4ade80"
                  tk-color-warning          "#fbbf24"})})

(def ^:private preset-neo-brutalist
  {:light (merge base-typography base-motion
                 {tk-color-primary          "#000000"
                  tk-color-primary-hover    "#1a1a1a"
                  tk-color-primary-active   "#333333"
                  tk-color-secondary        "#ff3366"
                  tk-color-secondary-hover  "#e6004c"
                  tk-color-secondary-active "#cc0044"
                  tk-color-tertiary         "#555555"
                  tk-color-tertiary-hover   "#333333"
                  tk-color-tertiary-active  "#1a1a1a"
                  tk-color-surface          "#ffffff"
                  tk-color-surface-hover    "#f5f5f5"
                  tk-color-surface-active   "#eeeeee"
                  tk-color-bg               "#ffffff"
                  tk-color-text             "#000000"
                  tk-color-text-muted       "#555555"
                  tk-color-border           "#000000"
                  tk-color-focus-ring       "#ff3366"
                  tk-color-danger           "#ff0000"
                  tk-color-success          "#00cc66"
                  tk-color-warning          "#ffcc00"
                  tk-radius-sm              "0"
                  tk-radius-md              "0"
                  tk-radius-lg              "0"
                  tk-radius-full            "0"
                  tk-shadow-sm              "3px 3px 0 #000000"
                  tk-shadow-md              "5px 5px 0 #000000"
                  tk-shadow-lg              "8px 8px 0 #000000"
                  tk-font-family            "'Space Grotesk',system-ui,sans-serif"})
   :dark  {tk-color-primary          "#ff3366"
           tk-color-primary-hover    "#e6004c"
           tk-color-primary-active   "#cc0044"
           tk-color-secondary        "#ffffff"
           tk-color-secondary-hover  "#e5e5e5"
           tk-color-secondary-active "#cccccc"
           tk-color-tertiary         "#aaaaaa"
           tk-color-tertiary-hover   "#888888"
           tk-color-tertiary-active  "#666666"
           tk-color-surface          "#111111"
           tk-color-surface-hover    "#1a1a1a"
           tk-color-surface-active   "#222222"
           tk-color-bg               "#000000"
           tk-color-text             "#ffffff"
           tk-color-text-muted       "#aaaaaa"
           tk-color-border           "#ffffff"
           tk-color-focus-ring       "#ff3366"
           tk-color-danger           "#ff4444"
           tk-color-success          "#44ff88"
           tk-color-warning          "#ffdd44"
           tk-shadow-sm              "3px 3px 0 #ffffff"
           tk-shadow-md              "5px 5px 0 #ffffff"
           tk-shadow-lg              "8px 8px 0 #ffffff"}})

(def ^:private preset-aurora
  {:light (merge base-typography base-shape base-motion
                 {tk-color-primary          "#7c3aed"
                  tk-color-primary-hover    "#6d28d9"
                  tk-color-primary-active   "#5b21b6"
                  tk-color-secondary        "#ec4899"
                  tk-color-secondary-hover  "#db2777"
                  tk-color-secondary-active "#be185d"
                  tk-color-tertiary         "#06b6d4"
                  tk-color-tertiary-hover   "#0891b2"
                  tk-color-tertiary-active  "#0e7490"
                  tk-color-surface          "rgba(255,255,255,0.72)"
                  tk-color-surface-hover    "rgba(248,245,255,0.80)"
                  tk-color-surface-active   "rgba(243,232,255,0.85)"
                  tk-color-bg               "#faf5ff"
                  tk-color-text             "#1e1b4b"
                  tk-color-text-muted       "#7c7aad"
                  tk-color-border           "rgba(196,181,253,0.5)"
                  tk-color-focus-ring       "#a78bfa"
                  tk-color-danger           "#e11d48"
                  tk-color-success          "#059669"
                  tk-color-warning          "#d97706"
                  tk-shadow-sm              "0 1px 3px rgba(139,92,246,0.10)"
                  tk-shadow-md              "0 4px 15px rgba(139,92,246,0.12),0 2px 4px rgba(139,92,246,0.08)"
                  tk-shadow-lg              "0 10px 30px rgba(139,92,246,0.15),0 4px 10px rgba(139,92,246,0.10)"})
   :dark  {tk-color-primary          "#a78bfa"
           tk-color-primary-hover    "#8b5cf6"
           tk-color-primary-active   "#7c3aed"
           tk-color-secondary        "#f472b6"
           tk-color-secondary-hover  "#ec4899"
           tk-color-secondary-active "#db2777"
           tk-color-tertiary         "#22d3ee"
           tk-color-tertiary-hover   "#06b6d4"
           tk-color-tertiary-active  "#0891b2"
           tk-color-surface          "rgba(30,20,60,0.72)"
           tk-color-surface-hover    "rgba(45,30,80,0.78)"
           tk-color-surface-active   "rgba(55,35,95,0.85)"
           tk-color-bg               "#0f0720"
           tk-color-text             "#ede9fe"
           tk-color-text-muted       "#a5b4fc"
           tk-color-border           "rgba(139,92,246,0.3)"
           tk-color-focus-ring       "#c4b5fd"
           tk-color-danger           "#fb7185"
           tk-color-success          "#34d399"
           tk-color-warning          "#fbbf24"
           tk-shadow-sm              "0 1px 3px rgba(139,92,246,0.20)"
           tk-shadow-md              "0 4px 15px rgba(139,92,246,0.25),0 2px 4px rgba(139,92,246,0.15)"
           tk-shadow-lg              "0 10px 30px rgba(139,92,246,0.30),0 4px 10px rgba(139,92,246,0.18)"}})

(def ^:private preset-mono-ai
  {:light (merge base-shape base-motion
                 {tk-color-primary          "#18181b"
                  tk-color-primary-hover    "#27272a"
                  tk-color-primary-active   "#3f3f46"
                  tk-color-secondary        "#39ff14"
                  tk-color-secondary-hover  "#2ee00f"
                  tk-color-secondary-active "#24c00a"
                  tk-color-tertiary         "#71717a"
                  tk-color-tertiary-hover   "#52525b"
                  tk-color-tertiary-active  "#3f3f46"
                  tk-color-surface          "#fafafa"
                  tk-color-surface-hover    "#f4f4f5"
                  tk-color-surface-active   "#e4e4e7"
                  tk-color-bg               "#ffffff"
                  tk-color-text             "#18181b"
                  tk-color-text-muted       "#71717a"
                  tk-color-border           "#d4d4d8"
                  tk-color-focus-ring       "#39ff14"
                  tk-color-danger           "#ef4444"
                  tk-color-success          "#22c55e"
                  tk-color-warning          "#eab308"
                  tk-font-family            "'JetBrains Mono',ui-monospace,monospace"
                  tk-font-family-mono       "'JetBrains Mono',ui-monospace,monospace"
                  tk-shadow-sm              "0 1px 2px rgba(0,0,0,0.06)"
                  tk-shadow-md              "0 4px 10px rgba(0,0,0,0.08),0 2px 4px rgba(0,0,0,0.04)"
                  tk-shadow-lg              "0 10px 25px rgba(0,0,0,0.10),0 4px 10px rgba(0,0,0,0.06)"})
   :dark  {tk-color-primary          "#15803d"
           tk-color-primary-hover    "#166534"
           tk-color-primary-active   "#14532d"
           tk-color-secondary        "#e4e4e7"
           tk-color-secondary-hover  "#d4d4d8"
           tk-color-secondary-active "#a1a1aa"
           tk-color-tertiary         "#52525b"
           tk-color-tertiary-hover   "#71717a"
           tk-color-tertiary-active  "#52525b"
           tk-color-surface          "#09090b"
           tk-color-surface-hover    "#18181b"
           tk-color-surface-active   "#27272a"
           tk-color-bg               "#000000"
           tk-color-text             "#e4e4e7"
           tk-color-text-muted       "#71717a"
           tk-color-border           "#27272a"
           tk-color-focus-ring       "#39ff14"
           tk-color-danger           "#f87171"
           tk-color-success          "#4ade80"
           tk-color-warning          "#facc15"
           tk-shadow-sm              "0 1px 2px rgba(0,0,0,0.40)"
           tk-shadow-md              "0 4px 10px rgba(0,0,0,0.45),0 2px 4px rgba(0,0,0,0.30)"
           tk-shadow-lg              "0 10px 25px rgba(0,0,0,0.50),0 4px 10px rgba(0,0,0,0.35)"}})

(def ^:private preset-warm-mineral
  {:light (merge base-typography base-shape base-motion base-shadow-light
                 {tk-color-primary          "#b45309"
                  tk-color-primary-hover    "#92400e"
                  tk-color-primary-active   "#78350f"
                  tk-color-secondary        "#a16207"
                  tk-color-secondary-hover  "#854d0e"
                  tk-color-secondary-active "#713f12"
                  tk-color-tertiary         "#d6a56a"
                  tk-color-tertiary-hover   "#c49250"
                  tk-color-tertiary-active  "#b08040"
                  tk-color-surface          "#fefdfb"
                  tk-color-surface-hover    "#fdf8f0"
                  tk-color-surface-active   "#f5ebe0"
                  tk-color-bg               "#fdf8f0"
                  tk-color-text             "#3b2417"
                  tk-color-text-muted       "#8b7355"
                  tk-color-border           "#d6c4a8"
                  tk-color-focus-ring       "#d97706"
                  tk-color-danger           "#dc2626"
                  tk-color-success          "#4d7c0f"
                  tk-color-warning          "#ca8a04"})
   :dark  (merge base-shadow-dark
                 {tk-color-primary          "#c2785a"
                  tk-color-primary-hover    "#a8604a"
                  tk-color-primary-active   "#8e4a38"
                  tk-color-secondary        "#b8976e"
                  tk-color-secondary-hover  "#a18360"
                  tk-color-secondary-active "#8a7052"
                  tk-color-tertiary         "#7a7068"
                  tk-color-tertiary-hover   "#8e847c"
                  tk-color-tertiary-active  "#7a7068"
                  tk-color-surface          "#1e1c18"
                  tk-color-surface-hover    "#2a2822"
                  tk-color-surface-active   "#38352e"
                  tk-color-bg               "#14130f"
                  tk-color-text             "#e8e0d4"
                  tk-color-text-muted       "#9c9488"
                  tk-color-border           "#44403a"
                  tk-color-focus-ring       "#d4a574"
                  tk-color-danger           "#f87171"
                  tk-color-success          "#84cc16"
                  tk-color-warning          "#fbbf24"})})

;; ── Preset registry ─────────────────────────────────────────────────────────

(def ^:private built-in-presets
  {"default"       preset-default
   "ocean"         preset-ocean
   "forest"        preset-forest
   "sunset"        preset-sunset
   "neo-brutalist"  preset-neo-brutalist
   "aurora"        preset-aurora
   "mono-ai"       preset-mono-ai
   "warm-mineral"  preset-warm-mineral})

(def ^:private custom-presets (js/Object.create nil))

(defn register-preset!
  "Register a custom preset. The `data` argument is a JS object with
  `light` and optional `dark` keys, each mapping token names to values.
  Missing tokens fall back to the `default` built-in preset."
  [preset-name ^js data]
  (let [default-light (:light preset-default)
        default-dark  (:dark  preset-default)
        light-obj     (.-light data)
        dark-obj      (.-dark data)
        light-map     (merge default-light
                             (when light-obj
                               (into {} (for [k (js/Object.keys light-obj)]
                                          [k (gobj/get light-obj k)]))))
        dark-map      (merge default-dark
                             (when dark-obj
                               (into {} (for [k (js/Object.keys dark-obj)]
                                          [k (gobj/get dark-obj k)]))))]
    (gobj/set custom-presets preset-name
              {:light light-map :dark dark-map})))

(def allowed-presets
  #{"default" "ocean" "forest" "sunset" "neo-brutalist" "aurora" "mono-ai" "warm-mineral"})

(defn normalize-preset
  "Normalise a raw preset attribute value. Returns default-preset for
  unknown or nil values. Accepts both built-in and custom preset names."
  [s]
  (if (and (string? s)
           (or (contains? allowed-presets s)
               (some? (gobj/get custom-presets s))))
    s
    default-preset))

(defn- get-preset-data
  "Look up preset data. Custom presets take precedence over built-in."
  [preset-name]
  (or (gobj/get custom-presets preset-name)
      (get built-in-presets preset-name)
      preset-default))

(defn- tokens->css
  "Build a CSS declaration block string from a token map."
  [m]
  (let [sb (array)]
    (doseq [[k v] m]
      (.push sb (str k ":" v ";")))
    (.join sb "")))

(defn preset->css
  "Generate a complete CSS string for a given preset name."
  [preset-name]
  (let [data         (get-preset-data (normalize-preset preset-name))
        light-tokens (:light data)
        dark-tokens  (:dark data)]
    (str ":host{display:contents;"
         (tokens->css light-tokens)
         "}"
         "@media(prefers-color-scheme:dark){"
         ":host{"
         (tokens->css dark-tokens)
         "}}")))
