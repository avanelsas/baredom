(ns baredom.components.x-i18n-provider.model
  (:require [clojure.string :as str]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-i18n-provider")

;; ── Attribute constants ─────────────────────────────────────────────────────
(def attr-src             "src")
(def attr-locale          "locale")
(def attr-fallback-locale "fallback-locale")

;; ── Observed attributes ─────────────────────────────────────────────────────
(def observed-attributes #js [attr-src attr-locale attr-fallback-locale])

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:src            {:type 'string :reflects-attribute attr-src}
   :locale         {:type 'string :reflects-attribute attr-locale}
   :fallbackLocale {:type 'string :reflects-attribute attr-fallback-locale}})

;; ── Event constants ─────────────────────────────────────────────────────────
(def event-loading "x-i18n-loading")
(def event-change  "x-i18n-change")
(def event-error   "x-i18n-error")

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-loading {:cancelable false :detail {:locale 'string}}
   event-change  {:cancelable false :detail {:locale 'string}}
   event-error   {:cancelable false :detail {:locale 'string :message 'string}}})

;; ── Method API ──────────────────────────────────────────────────────────────
(def method-api nil)

;; ── Default locale ──────────────────────────────────────────────────────────
(def default-locale "en")

;; ── Pure functions ──────────────────────────────────────────────────────────

(defn resolve-url
  "Replace `{locale}` in the src pattern with the given locale.
   Returns nil if src or locale is blank."
  [src locale]
  (when (and (string? src) (not= src "")
             (string? locale) (not= locale ""))
    (str/replace src "{locale}" locale)))

(defn normalize
  "Normalize raw attribute values into a model map."
  [src locale fallback-locale]
  {:src             (or src "")
   :locale          (if (and (string? locale) (not= locale ""))
                      locale
                      default-locale)
   :fallback-locale (or fallback-locale "")})

(defn needs-fetch?
  "Returns true when the locale or src changed between old and new models,
   indicating a new fetch is required."
  [old-m new-m]
  (or (not= (:src old-m) (:src new-m))
      (not= (:locale old-m) (:locale new-m))
      (not= (:fallback-locale old-m) (:fallback-locale new-m))))
