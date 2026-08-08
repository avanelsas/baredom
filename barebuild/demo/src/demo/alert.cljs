(ns demo.alert
  "The demo's error banner. A host shows at most one, replaced rather than stacked."
  (:require
   [baredom.utils.dom :as du]))

(def ^:private tag "x-alert")

(defn clear!
  "Drop `host`'s alert, if it has one."
  [^js host]
  (when-let [^js existing (.querySelector host tag)]
    (.remove existing)))

(defn show!
  "Replace `host`'s alert with one reading `message`, placed before `before`. A nil `before`
   appends it, which is what insertBefore already does."
  ([^js host message] (show! host message nil))
  ([^js host message ^js before]
   (clear! host)
   (let [alert (.createElement js/document tag)]
     (du/set-attr! alert "type" "error")
     (du/set-attr! alert "text" message)
     (du/set-attr! alert "dismissible" "")
     (.insertBefore host alert before))))
