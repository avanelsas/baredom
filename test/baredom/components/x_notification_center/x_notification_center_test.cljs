(ns baredom.components.x-notification-center.x-notification-center-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [baredom.components.x-alert.x-alert :as xa]
            [baredom.components.x-notification-center.x-notification-center :as nc]
            [baredom.components.x-notification-center.model :as model]
            [baredom.components.x-alert.model :as alert-model]))

;; Register both elements at top level
(xa/init!)
(nc/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-nc []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn alerts-in [^js nc]
  (.querySelectorAll (shadow-part nc "[part=container]") model/alert-tag))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js nc (append! (make-nc))]
    (is (some? (.-shadowRoot nc)))
    (is (some? (shadow-part nc "[part=container]")))))

;; ── Container a11y attributes ─────────────────────────────────────────────────
(deftest container-a11y-attributes-test
  (let [^js nc        (append! (make-nc))
        ^js container (shadow-part nc "[part=container]")]
    (is (= "log" (.getAttribute container "role")))
    (is (= "polite" (.getAttribute container "aria-live")))))

;; ── Default position ──────────────────────────────────────────────────────────
(deftest default-data-position-test
  (let [^js nc (append! (make-nc))]
    (is (= "top-right" (.getAttribute nc "data-position")))))

;; ── position property ─────────────────────────────────────────────────────────
(deftest position-property-get-set-test
  (let [^js nc (append! (make-nc))]
    (is (= "top-right" (.-position nc)))
    (set! (.-position nc) "bottom-left")
    (is (= "bottom-left" (.getAttribute nc model/attr-position)))
    (is (= "bottom-left" (.-position nc)))
    (is (= "bottom-left" (.getAttribute nc "data-position")))))

;; ── max property ─────────────────────────────────────────────────────────────
(deftest max-property-get-set-test
  (let [^js nc (append! (make-nc))]
    (is (= 5 (.-max nc)))
    (set! (.-max nc) 3)
    (is (= "3" (.getAttribute nc model/attr-max)))
    (is (= 3 (.-max nc)))))

;; ── count is 0 initially ─────────────────────────────────────────────────────
(deftest count-zero-initially-test
  (let [^js nc (append! (make-nc))]
    (is (= 0 (.-count nc)))))

;; ── push appends an x-alert ──────────────────────────────────────────────────
(deftest push-appends-alert-test
  (let [^js nc (append! (make-nc))]
    (.push nc #js {:type "info" :text "Hello"})
    (is (= 1 (.-count nc)))
    (is (= 1 (.-length (alerts-in nc))))))

;; ── push sets correct attributes on alert ────────────────────────────────────
(deftest push-alert-attributes-test
  (let [^js nc    (append! (make-nc))
        _         (.push nc #js {:type "warning" :text "Watch out" :icon "!" :dismissible false})
        ^js alert (aget (alerts-in nc) 0)]
    (is (= "warning" (.getAttribute alert "type")))
    (is (= "Watch out" (.getAttribute alert "text")))
    (is (= "!" (.getAttribute alert "icon")))
    (is (= "false" (.getAttribute alert "dismissible")))
    (is (some? (.getAttribute alert model/data-notification-id)))))

;; ── push sets data-notification-id ───────────────────────────────────────────
(deftest push-sets-notification-id-test
  (let [^js nc    (append! (make-nc))
        _         (.push nc #js {})
        ^js alert (aget (alerts-in nc) 0)]
    (is (some? (.getAttribute alert model/data-notification-id)))))

;; ── push with explicit id uses that id ───────────────────────────────────────
(deftest push-explicit-id-test
  (let [^js nc    (append! (make-nc))
        _         (.push nc #js {:id "my-id" :text "Test"})
        ^js alert (aget (alerts-in nc) 0)]
    (is (= "my-id" (.getAttribute alert model/data-notification-id)))))

;; ── push sets timeout-ms on alert ─────────────────────────────────────────────
(deftest push-timeout-ms-attribute-test
  (let [^js nc    (append! (make-nc))
        _         (.push nc #js {:text "Timeout" :timeoutMs 3000})
        ^js alert (aget (alerts-in nc) 0)]
    (is (= "3000" (.getAttribute alert "timeout-ms")))))

;; ── push returns the notification id ─────────────────────────────────────────
(deftest push-returns-id-test
  (let [^js nc (append! (make-nc))]
    (is (string? (.push nc #js {:text "Hello"})))
    (is (= "custom-id" (.push nc #js {:text "Hi" :id "custom-id"})))))

;; ── push returns nil when at max ─────────────────────────────────────────────
(deftest push-returns-nil-at-max-test
  (let [^js nc (append! (make-nc))]
    (set! (.-max nc) 1)
    (.push nc #js {:text "First"})
    (is (nil? (.push nc #js {:text "Second"})))))

;; ── push fires x-notification-center-push ────────────────────────────────────
(deftest push-fires-event-test
  (let [^js nc   (append! (make-nc))
        events   (atom [])
        _        (.addEventListener nc model/event-push
                                    (fn [^js e] (swap! events conj (.-detail e))))]
    (.push nc #js {:text "Hello"})
    (is (= 1 (count @events)))
    (let [^js d (first @events)]
      (is (some? (.-id d)))
      (is (= 1 (.-count d))))))

;; ── push respects max ─────────────────────────────────────────────────────────
(deftest push-respects-max-test
  (let [^js nc (append! (make-nc))]
    (set! (.-max nc) 2)
    (.push nc #js {:text "1"})
    (.push nc #js {:text "2"})
    (.push nc #js {:text "3"})
    (is (= 2 (.-count nc)))))

;; ── clear removes all alerts ──────────────────────────────────────────────────
(deftest clear-removes-all-test
  (let [^js nc (append! (make-nc))]
    (.push nc #js {:text "A"})
    (.push nc #js {:text "B"})
    (is (= 2 (.-count nc)))
    (.clear nc)
    (is (= 0 (.-count nc)))))

;; ── dismiss fires x-notification-center-dismiss ───────────────────────────────
(deftest dismiss-fires-nc-dismiss-test
  (async done
    (let [^js nc   (append! (make-nc))
          events   (atom [])
          _        (.addEventListener nc model/event-dismiss
                                      (fn [^js e] (swap! events conj (.-detail e))))
          _        (.push nc #js {:text "Hi" :type "success"})
          ^js container (shadow-part nc "[part=container]")
          ^js alert     (aget (.querySelectorAll container model/alert-tag) 0)
          ^js dismiss-btn (.querySelector (.-shadowRoot alert) "[part=dismiss]")]
      (.click dismiss-btn)
      (js/setTimeout
       (fn []
         (is (= 1 (count @events)))
         (let [^js d (first @events)]
           (is (some? (.-id d)))
           (is (= "button" (.-reason d))))
         (done))
       50))))

;; ── x-notification-center-empty fires when last alert is dismissed ────────────
(deftest empty-event-fires-on-last-dismiss-test
  (async done
    (let [^js nc   (append! (make-nc))
          empties  (atom [])
          _        (.addEventListener nc model/event-empty
                                      (fn [^js e] (swap! empties conj e)))
          _        (.push nc #js {:text "Only one"})
          ^js container (shadow-part nc "[part=container]")
          ^js alert     (aget (.querySelectorAll container model/alert-tag) 0)
          ^js dismiss-btn (.querySelector (.-shadowRoot alert) "[part=dismiss]")]
      (.click dismiss-btn)
      (js/setTimeout
       (fn []
         (is (= 1 (count @empties)))
         (done))
       50))))

;; ── x-notification-center-empty does NOT fire when alerts remain ──────────────
(deftest empty-event-not-fired-when-alerts-remain-test
  (async done
    (let [^js nc   (append! (make-nc))
          empties  (atom [])
          _        (.addEventListener nc model/event-empty
                                      (fn [^js e] (swap! empties conj e)))
          _        (.push nc #js {:text "First"})
          _        (.push nc #js {:text "Second"})
          ^js container (shadow-part nc "[part=container]")
          ^js alert     (aget (.querySelectorAll container model/alert-tag) 0)
          ^js dismiss-btn (.querySelector (.-shadowRoot alert) "[part=dismiss]")]
      (.click dismiss-btn)
      (js/setTimeout
       (fn []
         (is (= 0 (count @empties)))
         (done))
       50))))

;; ── No listener doubling after reconnect ─────────────────────────────────────
(deftest no-listener-doubling-after-reconnect-test
  (let [^js nc   (make-nc)
        events   (atom [])]
    (.addEventListener nc model/event-push (fn [^js e] (swap! events conj e)))
    ;; First connect
    (.appendChild (.-body js/document) nc)
    ;; Disconnect and reconnect
    (.remove nc)
    (.appendChild (.-body js/document) nc)
    ;; Push once
    (.push nc #js {:text "Test"})
    ;; Should have fired exactly once despite reconnect
    (is (= 1 (count @events)))))
