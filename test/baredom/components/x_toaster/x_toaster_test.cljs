(ns baredom.components.x-toaster.x-toaster-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [goog.object :as gobj]
            [baredom.components.x-toaster.x-toaster :as x]
            [baredom.components.x-toaster.model     :as model]
            [baredom.components.x-toast.x-toast     :as x-toast]
            [baredom.components.x-toast.model       :as toast-model]))

;; Register both elements before any tests run
(x/init!)
(x-toast/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-toaster []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-toaster))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "slot")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-toaster))]
    (is (= "region"        (.getAttribute el "role")))
    (is (= "Notifications" (.getAttribute el "aria-label")))
    (is (= "top-end"       (.getAttribute el "data-position")))))

;; ── position attribute ───────────────────────────────────────────────────────
(deftest position-attribute-test
  (testing "valid positions applied as data-position"
    (doseq [pos ["top-start" "top-center" "top-end"
                 "bottom-start" "bottom-center" "bottom-end"]]
      (let [^js el (append! (make-toaster))]
        (.setAttribute el model/attr-position pos)
        (is (= pos (.getAttribute el "data-position"))))))

  (testing "invalid position ignored - stays at top-end"
    (let [^js el (append! (make-toaster))]
      (.setAttribute el model/attr-position "middle")
      (is (= "top-end" (.getAttribute el "data-position"))))))

;; ── label attribute ──────────────────────────────────────────────────────────
(deftest label-attribute-test
  (let [^js el (append! (make-toaster))]
    (.setAttribute el model/attr-label "My Alerts")
    (is (= "My Alerts" (.getAttribute el "aria-label")))))

;; ── max-toasts attribute ─────────────────────────────────────────────────────
(deftest max-toasts-property-test
  (let [^js el (append! (make-toaster))]
    (is (= 5 (.-maxToasts el)))
    (.setAttribute el model/attr-max-toasts "3")
    (is (= 3 (.-maxToasts el)))))

;; ── toast() API ──────────────────────────────────────────────────────────────
(deftest toast-method-creates-element-test
  (let [^js el (append! (make-toaster))]
    (let [^js t (.toast el #js {:type "success" :message "Hello"})]
      (is (some? t))
      (is (= "X-TOAST" (.-tagName t)))
      (is (= "success" (.getAttribute t "type")))
      (is (= "Hello"   (.getAttribute t "message"))))))

(deftest toast-method-sets-heading-test
  (let [^js el (append! (make-toaster))]
    (let [^js t (.toast el #js {:heading "Title" :message "Body"})]
      (is (= "Title" (.getAttribute t "heading")))
      (is (= "Body"  (.getAttribute t "message"))))))

(deftest toast-method-sets-timeout-test
  (let [^js el (append! (make-toaster))]
    (let [^js t (.toast el #js {:timeoutMs 3000})]
      (is (= "3000" (.getAttribute t "timeout-ms"))))))

(deftest toast-method-sets-show-progress-test
  (let [^js el (append! (make-toaster))]
    (let [^js t (.toast el #js {:timeoutMs 3000 :showProgress true})]
      (is (.hasAttribute t "show-progress")))))

(deftest toast-method-sets-dismissible-false-test
  (let [^js el (append! (make-toaster))]
    (let [^js t (.toast el #js {:dismissible false})]
      (is (= "false" (.getAttribute t "dismissible"))))))

(deftest toast-method-appends-to-toaster-test
  (let [^js el (append! (make-toaster))]
    (.toast el #js {:message "A"})
    (.toast el #js {:message "B"})
    (let [^js toasts (.querySelectorAll el toast-model/tag-name)]
      (is (= 2 (.-length toasts))))))

(deftest toast-method-returns-element-test
  (let [^js el (append! (make-toaster))]
    (let [result (.toast el #js {})]
      (is (instance? js/HTMLElement result)))))

;; ── max-toasts eviction ──────────────────────────────────────────────────────
(deftest max-toasts-eviction-test
  (async done
    (let [^js el (append! (make-toaster))]
      (.setAttribute el model/attr-max-toasts "2")
      ;; Fill to capacity
      (.toast el #js {:message "First"})
      (.toast el #js {:message "Second"})
      ;; Add one more — oldest (First) should be dismissed
      (.toast el #js {:message "Third"})
      ;; Allow dismiss animation to complete (x-toast exits in ~180ms)
      (js/setTimeout
       (fn []
         (let [^js remaining (.querySelectorAll el toast-model/tag-name)]
           ;; After eviction completes there should be ≤ max-toasts
           (is (<= (.-length remaining) 2))
           (done)))
       400))))

;; ── dismiss coordination ─────────────────────────────────────────────────────
(deftest dismiss-coordination-test
  (async done
    (let [^js el    (append! (make-toaster))
          ^js toast (.toast el #js {:message "Test" :type "info"})]
      ;; Listen for x-toaster-dismiss
      (let [received (atom nil)]
        (.addEventListener el model/event-dismiss
                           (fn [^js e]
                             (reset! received (.-detail e))))
        ;; Programmatically dismiss the toast via toaster-remove so
        ;; the toaster doesn't intercept it (it's already a toaster-remove reason)
        ;; Instead, call dismiss with a non-toaster reason to trigger full flow
        (.dismiss toast "test-reason")
        (js/setTimeout
         (fn []
           (let [d @received]
             (is (some? d))
             (is (= "test-reason" (gobj/get d "reason")))
             (is (= "info"        (gobj/get d "type")))
             (done))
         50))))))

(deftest dismiss-preventDefault-keeps-toast-test
  (async done
    (let [^js el    (append! (make-toaster))
          ^js toast (.toast el #js {:message "Keep me"})]
      ;; Prevent the toaster-level dismiss
      (.addEventListener el model/event-dismiss
                         (fn [^js e] (.preventDefault e)))
      (.dismiss toast "keyboard")
      (js/setTimeout
       (fn []
         ;; Toast should still be in DOM since we prevented
         (is (.-isConnected toast))
         (done))
       200))))
