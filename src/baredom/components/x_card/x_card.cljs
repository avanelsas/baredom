(ns baredom.components.x-card.x-card
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-card.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xCardRefs")
(def ^:private k-model "__xCardModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part         "part")
(def ^:private attr-role         "role")
(def ^:private attr-tabindex     "tabindex")
(def ^:private attr-aria-label   "aria-label")
(def ^:private attr-aria-disabled "aria-disabled")
(def ^:private attr-data-variant "data-variant")
(def ^:private attr-data-padding "data-padding")
(def ^:private attr-data-radius  "data-radius")
(def ^:private attr-data-interactive "data-interactive")
(def ^:private attr-data-disabled    "data-disabled")

(def ^:private part-base "base")
(def ^:private cls-base  "base")
(def ^:private val-true  "true")

(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private key-enter    "Enter")
(def ^:private key-space    " ")
(def ^:private key-spacebar "Spacebar")
(def ^:private activation-keys #{key-space key-spacebar})

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  "
  :host {
  display: block;
  color-scheme: light dark;

  --x-card-background: var(--x-color-surface, rgba(255, 255, 255, 0.92));
  --x-card-color: var(--x-color-text, #111827);
  --x-card-border-color: var(--x-color-border, rgba(17, 24, 39, 0.12));
  --x-card-filled-background: rgba(241, 245, 249, 0.96);
  --x-card-ghost-background: transparent;
  --x-card-hover-background: rgba(15, 23, 42, 0.04);
  --x-card-press-background: rgba(15, 23, 42, 0.08);
  --x-card-shadow: var(--x-shadow-md, 0 10px 24px rgba(15, 23, 42, 0.10));
  --x-card-focus-ring: var(--x-color-focus-ring, rgba(59, 130, 246, 0.55));
  --x-card-disabled-opacity: 0.6;

  --x-card-padding-none: 0;
  --x-card-padding-sm: 0.5rem;
  --x-card-padding-md: 1rem;
  --x-card-padding-lg: 1.5rem;

  --x-card-radius-none: 0;
  --x-card-radius-sm: var(--x-radius-sm, 0.375rem);
  --x-card-radius-md: var(--x-radius-md, 0.75rem);
  --x-card-radius-lg: var(--x-radius-lg, 1rem);
  --x-card-radius-xl: 1.5rem;

  --x-card-transition-duration: var(--x-transition-duration, 140ms);
  --x-card-transition-timing: ease;

  outline: none;
  }

  @media (prefers-color-scheme: dark) {
  :host {
  --x-card-background: var(--x-color-surface, rgba(15, 23, 42, 0.88));
  --x-card-color: var(--x-color-text, #e5e7eb);
  --x-card-border-color: var(--x-color-border, rgba(148, 163, 184, 0.24));
  --x-card-filled-background: rgba(30, 41, 59, 0.96);
  --x-card-hover-background: rgba(148, 163, 184, 0.10);
  --x-card-press-background: rgba(148, 163, 184, 0.16);
  --x-card-shadow: var(--x-shadow-md, 0 16px 32px rgba(0, 0, 0, 0.32));
  --x-card-focus-ring: var(--x-color-focus-ring, rgba(96, 165, 250, 0.6));
  }
  }

  .base {
  box-sizing: border-box;
  display: block;
  min-width: 0;
  background: var(--x-card-background);
  color: var(--x-card-color);
  border: 1px solid transparent;
  border-radius: var(--x-card-radius-lg);
  transition:
  background var(--x-card-transition-duration) var(--x-card-transition-timing),
  box-shadow var(--x-card-transition-duration) var(--x-card-transition-timing),
  border-color var(--x-card-transition-duration) var(--x-card-transition-timing),
  transform var(--x-card-transition-duration) var(--x-card-transition-timing);
  }

  .base[data-variant='elevated'] {
  background: var(--x-card-background);
  box-shadow: var(--x-card-shadow);
  }

  .base[data-variant='outlined'] {
  background: var(--x-card-background);
  border-color: var(--x-card-border-color);
  box-shadow: none;
  }

  .base[data-variant='filled'] {
  background: var(--x-card-filled-background);
  box-shadow: none;
  }

  .base[data-variant='ghost'] {
  background: var(--x-card-ghost-background);
  box-shadow: none;
  }

  .base[data-padding='none'] { padding: var(--x-card-padding-none); }
  .base[data-padding='sm']   { padding: var(--x-card-padding-sm); }
  .base[data-padding='md']   { padding: var(--x-card-padding-md); }
  .base[data-padding='lg']   { padding: var(--x-card-padding-lg); }

  .base[data-radius='none'] { border-radius: var(--x-card-radius-none); }
  .base[data-radius='sm']   { border-radius: var(--x-card-radius-sm); }
  .base[data-radius='md']   { border-radius: var(--x-card-radius-md); }
  .base[data-radius='lg']   { border-radius: var(--x-card-radius-lg); }
  .base[data-radius='xl']   { border-radius: var(--x-card-radius-xl); }

  .base[data-interactive='true']:not([data-disabled='true']) {
  cursor: pointer;
  }

  .base[data-interactive='true']:not([data-disabled='true']):hover {
  background: var(--x-card-hover-background);
  }

  .base[data-interactive='true']:not([data-disabled='true']):active {
  background: var(--x-card-press-background);
  transform: translateY(1px);
  }

  .base[data-disabled='true'] {
  opacity: var(--x-card-disabled-opacity);
  cursor: default;
  transform: none;
  }

  :host(:focus-visible) .base[data-interactive='true']:not([data-disabled='true']) {
  box-shadow:
  0 0 0 3px var(--x-card-focus-ring),
  var(--x-card-shadow);
  }

  @media (prefers-reduced-motion: reduce) {
  .base {
  transition: none;
  }

  .base[data-interactive='true']:not([data-disabled='true']):active {
  transform: none;
  }
  }
  ")

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (.createElement js/document "div")
        slot  (.createElement js/document "slot")
        refs  {:root root :base base}]

    (set! (.-textContent style) style-text)

    (du/set-attr! base attr-part part-base)
    (set! (.-className base) cls-base)
    (.appendChild base slot)

    (.appendChild root style)
    (.appendChild root base)

    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:variant     (du/get-attr el model/attr-variant)
    :padding     (du/get-attr el model/attr-padding)
    :radius      (du/get-attr el model/attr-radius)
    :interactive (du/has-attr? el model/attr-interactive)
    :disabled    (du/has-attr? el model/attr-disabled)
    :label       (du/get-attr el model/attr-label)}))

(defn- current-model
  "Return the cached model, falling back to a fresh read.
  Used by event handlers that must reflect the latest attribute state
  even before the next attribute-changed callback has cached it."
  [^js el]
  (or (du/getv el k-model) (read-model el)))

(defn- interactive-active? [m]
  (and (:interactive m) (not (:disabled m))))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- set-or-remove-attr! [^js el name value]
  (if (some? value)
    (du/set-attr! el name value)
    (du/remove-attr! el name)))

(defn- apply-host-a11y! [^js el m]
  (set-or-remove-attr! el attr-role          (:role m))
  (set-or-remove-attr! el attr-tabindex      (:tabindex m))
  (set-or-remove-attr! el attr-aria-label    (:aria-label m))
  (set-or-remove-attr! el attr-aria-disabled (:aria-disabled m)))

(defn- apply-base-state! [^js base m]
  (du/set-attr! base attr-data-variant (:variant m))
  (du/set-attr! base attr-data-padding (:padding m))
  (du/set-attr! base attr-data-radius  (:radius m))
  (set-or-remove-attr! base attr-data-interactive (when (:interactive m) val-true))
  (set-or-remove-attr! base attr-data-disabled    (when (:disabled m)    val-true)))

(defn- apply-model! [^js el m]
  (let [{:keys [base]} (ensure-refs! el)]
    (apply-host-a11y! el m)
    (apply-base-state! base m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Event handlers ────────────────────────────────────────────────────────
(defn- dispatch-press! [^js el]
  (du/dispatch! el model/event-press #js {}))

(defn- on-click [^js el ^js _event]
  (when (interactive-active? (current-model el))
    (dispatch-press! el)))

(defn- on-keydown [^js el ^js event]
  (when (interactive-active? (current-model el))
    (let [k (.-key event)]
      (cond
        (= k key-enter)
        (dispatch-press! el)

        (contains? activation-keys k)
        (do (.preventDefault event)
            (dispatch-press! el))))))

(defn- install-listeners! [^js el]
  (.addEventListener el ev-click   (fn handle-click   [event] (on-click   el event)))
  (.addEventListener el ev-keydown (fn handle-keydown [event] (on-keydown el event))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (ensure-refs! el)
    (install-listeners! el))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
