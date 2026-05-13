(ns baredom.components.x-kbd.model
  (:require [clojure.string :as str]))

;; ── Tag & attribute constants ─────────────────────────────────────────────
(def tag-name "x-kbd")

(def attr-keys      "keys")
(def attr-separator "separator")
(def attr-size      "size")
(def attr-platform  "platform")
(def attr-label     "label")

(def observed-attributes
  #js [attr-keys attr-separator attr-size attr-platform attr-label])

;; ── Defaults & enums ──────────────────────────────────────────────────────
(def default-separator "+")
(def default-size      "md")
(def default-platform  "auto")
(def detected-fallback "linux")

(def ^:private valid-sizes     #{"sm" "md" "lg"})
(def ^:private valid-platforms #{"auto" "mac" "win" "linux"})
(def ^:private resolved-platforms #{"mac" "win" "linux"})

;; ── Token map ─────────────────────────────────────────────────────────────
;; [platform token-lower] → {:visible "..." :aria "..."}
;; Tokens not in the map pass through verbatim (visible = aria = trimmed input).
(def ^:private token-map
  {["mac"   "mod"]     {:visible "⌘" :aria "Command"}
   ["win"   "mod"]     {:visible "Ctrl"   :aria "Control"}
   ["linux" "mod"]     {:visible "Ctrl"   :aria "Control"}

   ["mac"   "cmd"]     {:visible "⌘" :aria "Command"}
   ["mac"   "command"] {:visible "⌘" :aria "Command"}
   ["mac"   "meta"]    {:visible "⌘" :aria "Command"}
   ["mac"   "super"]   {:visible "⌘" :aria "Command"}
   ["win"   "cmd"]     {:visible "Win"    :aria "Windows"}
   ["win"   "command"] {:visible "Win"    :aria "Windows"}
   ["win"   "meta"]    {:visible "Win"    :aria "Windows"}
   ["win"   "super"]   {:visible "Win"    :aria "Windows"}
   ["linux" "cmd"]     {:visible "Super"  :aria "Super"}
   ["linux" "command"] {:visible "Super"  :aria "Super"}
   ["linux" "meta"]    {:visible "Super"  :aria "Super"}
   ["linux" "super"]   {:visible "Super"  :aria "Super"}

   ["mac"   "ctrl"]    {:visible "⌃" :aria "Control"}
   ["mac"   "control"] {:visible "⌃" :aria "Control"}
   ["win"   "ctrl"]    {:visible "Ctrl" :aria "Control"}
   ["win"   "control"] {:visible "Ctrl" :aria "Control"}
   ["linux" "ctrl"]    {:visible "Ctrl" :aria "Control"}
   ["linux" "control"] {:visible "Ctrl" :aria "Control"}

   ["mac"   "alt"]     {:visible "⌥" :aria "Option"}
   ["mac"   "option"]  {:visible "⌥" :aria "Option"}
   ["win"   "alt"]     {:visible "Alt" :aria "Alt"}
   ["win"   "option"]  {:visible "Alt" :aria "Alt"}
   ["linux" "alt"]     {:visible "Alt" :aria "Alt"}
   ["linux" "option"]  {:visible "Alt" :aria "Alt"}

   ["mac"   "shift"]   {:visible "⇧" :aria "Shift"}
   ["win"   "shift"]   {:visible "Shift" :aria "Shift"}
   ["linux" "shift"]   {:visible "Shift" :aria "Shift"}

   ["mac"   "enter"]   {:visible "Return" :aria "Return"}
   ["mac"   "return"]  {:visible "Return" :aria "Return"}
   ["win"   "enter"]   {:visible "Enter" :aria "Enter"}
   ["win"   "return"]  {:visible "Enter" :aria "Enter"}
   ["linux" "enter"]   {:visible "Enter" :aria "Enter"}
   ["linux" "return"]  {:visible "Enter" :aria "Enter"}})

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-size     [s] (parse-enum valid-sizes default-size s))
(defn parse-platform [s] (parse-enum valid-platforms default-platform s))

(defn parse-separator
  "Single non-empty character; otherwise default."
  [s]
  (if (and (string? s) (= 1 (.-length ^js s))) s default-separator))

(defn resolve-platform
  "Map (possibly \"auto\") platform attr value plus a UA-detected fallback to a
  concrete platform string (\"mac\"|\"win\"|\"linux\")."
  [platform-attr detected]
  (let [p (parse-platform platform-attr)]
    (if (contains? resolved-platforms p)
      p
      (if (contains? resolved-platforms detected)
        detected
        detected-fallback))))

(defn split-keys
  "Split a combo string on `separator` into trimmed, non-empty tokens."
  [keys-raw separator]
  (if (and (string? keys-raw) (not= "" (.trim ^js keys-raw)))
    (->> (.split ^js keys-raw separator)
         (map (fn [^js s] (.trim s)))
         (remove #(= "" %))
         vec)
    []))

(defn resolve-token
  "Map a raw input token to {:visible :aria} for the given resolved platform.
  Unknown tokens pass through (visible = aria = trimmed input)."
  [platform raw-token]
  (let [trimmed (.trim ^js raw-token)
        lower   (.toLowerCase trimmed)]
    (or (get token-map [platform lower])
        {:visible trimmed :aria trimmed})))

(defn derive-aria-label
  "Author-supplied `label-raw` wins verbatim. Otherwise join `:aria` names with
  \" plus \". Empty when no tokens and no label."
  [resolved-tokens label-raw]
  (cond
    (and (string? label-raw)
         (not= "" (.trim ^js label-raw)))
    (.trim ^js label-raw)

    (seq resolved-tokens)
    (str/join " plus " (map :aria resolved-tokens))

    :else ""))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "Inputs:
     :keys-raw       string | nil
     :separator-raw  string | nil
     :size-raw       string | nil
     :platform-raw   string | nil  (\"mac\"|\"win\"|\"linux\"; \"auto\" is resolved
                                    by the caller before reaching this fn)
     :label-raw      string | nil
   Output:
     :tokens     vector of {:visible :aria} maps (empty in slot mode)
     :separator  string (single char)
     :size       string  (\"sm\"|\"md\"|\"lg\")
     :platform   string  (\"mac\"|\"win\"|\"linux\")
     :label      string  (derived or author-supplied)
     :slot-mode? boolean (true when no tokens parsed)"
  [{:keys [keys-raw separator-raw size-raw platform-raw label-raw]}]
  (let [sep      (parse-separator separator-raw)
        platform (let [p (parse-platform platform-raw)]
                   (if (contains? resolved-platforms p) p detected-fallback))
        raw      (split-keys keys-raw sep)
        tokens   (mapv #(resolve-token platform %) raw)]
    {:tokens     tokens
     :separator  sep
     :size       (parse-size size-raw)
     :platform   platform
     :label      (derive-aria-label tokens label-raw)
     :slot-mode? (empty? tokens)}))

;; ── Public metadata ───────────────────────────────────────────────────────
(def property-api
  {:keys      {:type 'string :reflects-attribute attr-keys      :default ""}
   :separator {:type 'string :reflects-attribute attr-separator :default default-separator}
   :size      {:type 'string :reflects-attribute attr-size      :default default-size}
   :platform  {:type 'string :reflects-attribute attr-platform  :default default-platform}
   :label     {:type 'string :reflects-attribute attr-label     :default ""}})

(def event-schema {})

(def method-api {})
