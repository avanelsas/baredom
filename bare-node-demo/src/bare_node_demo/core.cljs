(ns bare-node-demo.core
  (:require
   ["@vanelsas/baredom/x-navbar"    :as x-navbar]
   ["@vanelsas/baredom/x-sidebar"   :as x-sidebar]
   ["@vanelsas/baredom/x-button"    :as x-button]
   ["@vanelsas/baredom/x-modal"     :as x-modal]
   ["@vanelsas/baredom/x-container" :as x-container]
   ["@vanelsas/baredom/x-card"      :as x-card]
   [clojure.string :as str]
   [bare-node-demo.renderer :as renderer]
   [bare-node-demo.state    :as state]
   [bare-node-demo.views.app :as app-view]))

(defn- register-components! []
  (.init x-navbar)
  (.init x-sidebar)
  (.init x-button)
  (.init x-modal)
  (.init x-container)
  (.init x-card))

;;; ── Event log ─────────────────────────────────────────────────────────────

(defn- pad2 [n]
  (let [s (str n)]
    (if (< (count s) 2) (str "0" s) s)))

(defn- pad3 [n]
  (let [s (str n)]
    (cond (< (count s) 2) (str "00" s)
          (< (count s) 3) (str "0" s)
          :else s)))

(defn- format-time [^js d]
  (str (pad2 (.getHours d)) ":"
       (pad2 (.getMinutes d)) ":"
       (pad2 (.getSeconds d)) "."
       (pad3 (.getMilliseconds d))))

(defn- setup-event-log! []
  (let [container  (.getElementById js/document "event-log")
        card       (.createElement js/document "x-card")
        header     (.createElement js/document "div")
        title      (.createElement js/document "span")
        clear-btn  (.createElement js/document "x-button")
        log-list   (.createElement js/document "div")
        empty-msg  (.createElement js/document "p")]

    ;; x-card
    (.setAttribute card "variant" "elevated")
    (.setAttribute card "padding" "sm")

    ;; header
    (.setAttribute header "class" "event-log-header")
    (.setAttribute title "class" "event-log-title")
    (set! (.-textContent title) "Event Log")
    (.setAttribute clear-btn "variant" "ghost")
    (.setAttribute clear-btn "size" "sm")
    (set! (.-textContent clear-btn) "Clear")

    ;; log list
    (.setAttribute log-list "class" "event-log-list")
    (.setAttribute log-list "id" "event-log-list")

    ;; empty placeholder
    (.setAttribute empty-msg "class" "event-log-empty")
    (.setAttribute empty-msg "id" "event-log-empty")
    (set! (.-textContent empty-msg) "No events yet")

    ;; assemble
    (.appendChild header title)
    (.appendChild header clear-btn)
    (.appendChild card header)
    (.appendChild card log-list)
    (.appendChild card empty-msg)
    (.appendChild container card)

    ;; clear button handler
    (.addEventListener clear-btn "click"
      (fn [_]
        (set! (.-innerHTML log-list) "")
        (set! (.-style.display empty-msg) "")))

    ;; log-event! closes over log-list and empty-msg
    (letfn [(log-event! [^js e]
              (let [now        (js/Date.)
                    tag        (str/lower-case (.. e -target -tagName))
                    event-type (.-type e)
                    time-el    (.createElement js/document "span")
                    type-el    (.createElement js/document "span")
                    src-el     (.createElement js/document "span")
                    entry      (.createElement js/document "div")]
                (.setAttribute time-el "class" "event-log-time")
                (set! (.-textContent time-el) (format-time now))
                (.setAttribute type-el "class" "event-log-type")
                (set! (.-textContent type-el) event-type)
                (.setAttribute src-el "class" "event-log-source")
                (set! (.-textContent src-el) tag)
                (.setAttribute entry "class" "event-log-entry")
                (.appendChild entry time-el)
                (.appendChild entry type-el)
                (.appendChild entry src-el)
                ;; prepend — newest on top
                (.insertBefore log-list entry (.-firstChild log-list))
                ;; hide empty placeholder
                (set! (.-style.display empty-msg) "none")
                ;; cap at 50 entries
                (when (> (.-childElementCount log-list) 50)
                  (.removeChild log-list (.-lastChild log-list)))))]

      ;; custom events from web components (bubbles + composed)
      (doseq [ev-name ["press" "toggle" "x-modal-dismiss"]]
        (.addEventListener js/document ev-name log-event!))

      ;; native click events filtered to x- host elements
      (.addEventListener js/document "click"
        (fn [^js e]
          (when (str/starts-with? (str/lower-case (.. e -target -tagName)) "x-")
            (log-event! e)))))))

;;; ── App ───────────────────────────────────────────────────────────────────

(defn- view []
  (app-view/app @state/app))

(defn reload! []
  (renderer/render! (.getElementById js/document "app") view))

(defn init! []
  (register-components!)
  (renderer/mount! (.getElementById js/document "app") view state/app)
  (setup-event-log!))
