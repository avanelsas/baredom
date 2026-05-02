(ns baredom.components.x-welcome-tour-step.x-welcome-tour-step-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-welcome-tour-step.x-welcome-tour-step :as step]
            [baredom.components.x-welcome-tour-step.model :as model]))

;; ── Setup ───────────────────────────────────────────────────────────────────
(step/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

;; ── Registration & structure ────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-dom-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))))

(deftest host-hidden-test
  (testing "Step element has display:none (invisible in DOM)"
    (let [^js el (append! (make-el))
          style  (js/getComputedStyle el)]
      (is (= "none" (.-display style))))))

;; ── Property getters (attribute → property) ─────────────────────────────────
(deftest target-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "target" "#my-el")
    (is (= "#my-el" (.-target el)))))

(deftest target-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-target el) "#other")
    (is (= "#other" (.getAttribute el "target")))))

(deftest target-property-remove-test
  (let [^js el (append! (make-el))]
    (set! (.-target el) "#foo")
    (set! (.-target el) "")
    (is (nil? (.getAttribute el "target")))))

(deftest title-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "title" "Step One")
    (is (= "Step One" (.-title el)))))

(deftest title-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-title el) "Hello")
    (is (= "Hello" (.getAttribute el "title")))))

(deftest title-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-title el)))))

(deftest placement-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "placement" "right")
    (is (= "right" (.-placement el)))))

(deftest placement-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-placement el) "left")
    (is (= "left" (.getAttribute el "placement")))))

(deftest placement-property-default-test
  (let [^js el (append! (make-el))]
    (is (= "bottom" (.-placement el)))))

(deftest connector-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "connector" "curve")
    (is (= "curve" (.-connector el)))))

(deftest connector-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-connector el) "line")
    (is (= "line" (.getAttribute el "connector")))))

(deftest connector-property-nil-when-absent-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-connector el)))))

(deftest cutout-padding-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "cutout-padding" "16")
    (is (= 16 (.-cutoutPadding el)))))

(deftest cutout-padding-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-cutoutPadding el) 20)
    (is (= "20" (.getAttribute el "cutout-padding")))))

(deftest cutout-padding-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 8 (.-cutoutPadding el)))))

(deftest cutout-radius-property-get-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "cutout-radius" "12")
    (is (= 12 (.-cutoutRadius el)))))

(deftest cutout-radius-property-set-test
  (let [^js el (append! (make-el))]
    (set! (.-cutoutRadius el) 10)
    (is (= "10" (.getAttribute el "cutout-radius")))))

(deftest cutout-radius-property-default-test
  (let [^js el (append! (make-el))]
    (is (= 4 (.-cutoutRadius el)))))

(deftest scroll-to-property-default-true-test
  (let [^js el (append! (make-el))]
    (is (true? (.-scrollTo el)))))

(deftest scroll-to-property-set-false-test
  (let [^js el (append! (make-el))]
    (set! (.-scrollTo el) false)
    (is (= "false" (.getAttribute el "scroll-to")))))

(deftest scroll-to-property-set-true-test
  (let [^js el (append! (make-el))]
    (set! (.-scrollTo el) false)
    (set! (.-scrollTo el) true)
    (is (= "" (.getAttribute el "scroll-to")))))

;; ── Events ──────────────────────────────────────────────────────────────────
(deftest connected-event-fires-test
  (let [events (atom [])
        ^js el (make-el)]
    (.addEventListener (.-body js/document) model/event-connected
                       (fn [^js _e] (swap! events conj true)))
    (append! el)
    (is (= 1 (count @events)))))

(deftest disconnected-event-fires-test
  (let [events (atom [])
        ^js el (append! (make-el))]
    (.addEventListener js/document model/event-disconnected
                       (fn [^js _e] (swap! events conj true)))
    (.remove el)
    (is (= 1 (count @events)))))

;; ── Slot content ────────────────────────────────────────────────────────────
(deftest slot-accepts-child-content-test
  (let [^js el (make-el)
        ^js p  (.createElement js/document "p")]
    (set! (.-textContent p) "Step content")
    (.appendChild el p)
    (append! el)
    (is (= 1 (.-childElementCount el)))))
