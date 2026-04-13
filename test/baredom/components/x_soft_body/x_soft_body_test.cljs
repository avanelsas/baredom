(ns baredom.components.x-soft-body.x-soft-body-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures async]]
   [baredom.components.x-soft-body.x-soft-body :as x-soft-body]
   [baredom.components.x-soft-body.model :as model]))

;; Register element once
(x-soft-body/init!)

;; ── Helpers ─────────────────────────────────────────────────────────────────

(defn- make-el [] (.createElement js/document model/tag-name))
(defn- append! [^js el] (.appendChild (.-body js/document) el) el)
(defn- shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Fixtures ────────────────────────────────────────────────────────────────

(defn- cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each
  {:before cleanup-dom!
   :after  cleanup-dom!})

;; ── Registration ────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ────────────────────────────────────────────────────

(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=svg]")))
    (is (some? (shadow-part el "[part=shape]")))
    (is (some? (shadow-part el "[part=content]")))
    (is (some? (shadow-part el "slot")))))

;; ── SVG has aria-hidden ─────────────────────────────────────────────────────

(deftest svg-aria-hidden-test
  (let [^js el (append! (make-el))
        ^js svg (shadow-part el "[part=svg]")]
    (is (= "true" (.getAttribute svg "aria-hidden")))))

;; ── Property reflection ─────────────────────────────────────────────────────

(deftest stiffness-property-test
  (let [^js el (append! (make-el))]
    (set! (.-stiffness el) 300)
    (is (= "300" (.getAttribute el model/attr-stiffness)))
    (is (= 300.0 (.-stiffness el)))))

(deftest damping-property-test
  (let [^js el (append! (make-el))]
    (set! (.-damping el) 20)
    (is (= "20" (.getAttribute el model/attr-damping)))
    (is (= 20.0 (.-damping el)))))

(deftest radius-property-test
  (let [^js el (append! (make-el))]
    (set! (.-radius el) 24)
    (is (= "24" (.getAttribute el model/attr-radius)))
    (is (= 24.0 (.-radius el)))))

(deftest intensity-property-test
  (let [^js el (append! (make-el))]
    (set! (.-intensity el) 2.5)
    (is (= "2.5" (.getAttribute el model/attr-intensity)))
    (is (= 2.5 (.-intensity el)))))

(deftest grab-radius-property-test
  (let [^js el (append! (make-el))]
    (set! (.-grabRadius el) 120)
    (is (= "120" (.getAttribute el model/attr-grab-radius)))
    (is (= 120.0 (.-grabRadius el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    ;; Default is false
    (is (false? (.-disabled el)))
    ;; Set to true
    (set! (.-disabled el) true)
    (is (= "" (.getAttribute el model/attr-disabled)))
    (is (true? (.-disabled el)))
    ;; Set back to false
    (set! (.-disabled el) false)
    (is (nil? (.getAttribute el model/attr-disabled)))
    (is (false? (.-disabled el)))))

;; ── SVG path has d attribute after connection ───────────────────────────────

(deftest path-has-d-attribute-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      ;; Wait for ResizeObserver callback + RAF
      (js/setTimeout
       (fn []
         (let [^js path (shadow-part el "[part=shape]")
               d (.getAttribute path "d")]
           (is (some? d) "path should have a d attribute")
           (when d
             (is (.startsWith d "M") "path d should start with M")))
         (done))
       200))))

;; ── Disabled renders static path ────────────────────────────────────────────

(deftest disabled-renders-static-test
  (async done
    (let [^js el (make-el)]
      (.setAttribute el model/attr-disabled "")
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      (js/setTimeout
       (fn []
         (let [^js path (shadow-part el "[part=shape]")
               d (.getAttribute path "d")]
           (is (some? d) "disabled path should have d attribute")
           (when d
             (is (.includes d "L") "static path uses L commands")))
         (done))
       200))))

;; ── Grab event fires on pointerdown ─────────────────────────────────────────

(deftest grab-event-fires-test
  (async done
    (let [^js el (append! (make-el))
          seen   (atom nil)]
      (.addEventListener el model/event-grab
                         (fn [^js e] (reset! seen true)))
      (.dispatchEvent el (js/PointerEvent. "pointerdown"
                                           #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (true? @seen) "x-soft-body-grab should have fired")
         (done))
       50))))

;; ── Release event fires on pointerup ────────────────────────────────────────

(deftest release-event-fires-test
  (async done
    (let [^js el (append! (make-el))
          seen   (atom nil)]
      (.addEventListener el model/event-release
                         (fn [^js e] (reset! seen true)))
      (.dispatchEvent el (js/PointerEvent. "pointerup"
                                           #js {:bubbles true}))
      (js/setTimeout
       (fn []
         (is (true? @seen) "x-soft-body-release should have fired")
         (done))
       50))))

;; ── Default property values ─────────────────────────────────────────────────

(deftest default-property-values-test
  (let [^js el (append! (make-el))]
    (is (= 180.0 (.-stiffness el)))
    (is (= 12.0 (.-damping el)))
    (is (= 16.0 (.-radius el)))
    (is (= 1.0 (.-intensity el)))
    (is (= 80.0 (.-grabRadius el)))
    (is (false? (.-disabled el)))))

;; ── Null property clearing removes attribute ────────────────────────────────

(deftest null-clears-stiffness-test
  (let [^js el (append! (make-el))]
    (set! (.-stiffness el) 300)
    (is (= "300" (.getAttribute el model/attr-stiffness)))
    (set! (.-stiffness el) nil)
    (is (nil? (.getAttribute el model/attr-stiffness)))
    (is (= 180.0 (.-stiffness el)) "getter returns default after removal")))

(deftest null-clears-damping-test
  (let [^js el (append! (make-el))]
    (set! (.-damping el) 25)
    (set! (.-damping el) nil)
    (is (nil? (.getAttribute el model/attr-damping)))
    (is (= 12.0 (.-damping el)))))

(deftest null-clears-grab-radius-test
  (let [^js el (append! (make-el))]
    (set! (.-grabRadius el) 200)
    (set! (.-grabRadius el) nil)
    (is (nil? (.getAttribute el model/attr-grab-radius)))
    (is (= 80.0 (.-grabRadius el)))))

;; ── Resize reinitialises physics ────────────────────────────────────────────

(deftest resize-updates-path-test
  (async done
    (let [^js el (make-el)]
      (set! (.. el -style -width) "200px")
      (set! (.. el -style -height) "150px")
      (append! el)
      ;; Wait for initial ResizeObserver
      (js/setTimeout
       (fn []
         (let [^js path (shadow-part el "[part=shape]")
               d1 (.getAttribute path "d")]
           (is (some? d1) "path should have d after first resize")
           ;; Change size
           (set! (.. el -style -width) "400px")
           (set! (.. el -style -height) "300px")
           ;; Wait for second ResizeObserver
           (js/setTimeout
            (fn []
              (let [d2 (.getAttribute path "d")]
                (is (some? d2) "path should have d after second resize")
                (is (not= d1 d2) "path d should change after resize"))
              (done))
            200)))
       200))))
