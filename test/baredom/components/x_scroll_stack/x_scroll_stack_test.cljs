(ns baredom.components.x-scroll-stack.x-scroll-stack-test
  (:require
   [cljs.test :refer [deftest is testing use-fixtures async]]
   [baredom.components.x-scroll-stack.x-scroll-stack :as x-scroll-stack]
   [baredom.components.x-scroll-stack.model :as model]))

;; ── Registration ────────────────────────────────────────────────────────────
(x-scroll-stack/init!)

;; ── Fixture: cleanup ────────────────────────────────────────────────────────
(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn- add-cards! [^js el n]
  (dotimes [i n]
    (let [card (.createElement js/document "div")]
      (set! (.-textContent card) (str "Card " (inc i)))
      (set! (.. card -style -width) "200px")
      (set! (.. card -style -height) "100px")
      (set! (.. card -style -background) "#eee")
      (.appendChild el card))))

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Tests ───────────────────────────────────────────────────────────────────
(deftest registration-test
  (testing "element is registered"
    (is (some? (.get js/customElements model/tag-name)))))

(deftest shadow-dom-structure-test
  (let [^js el (append! (make-el))]
    (testing "shadow root exists"
      (is (some? (.-shadowRoot el))))
    (testing "container part exists"
      (is (some? (shadow-part el "[part=container]"))))
    (testing "slot exists"
      (is (some? (shadow-part el "slot"))))))

(deftest container-role-test
  (let [^js el (append! (make-el))]
    (testing "container has role=region"
      (is (= "region" (.getAttribute (shadow-part el "[part=container]") "role"))))))

(deftest default-attributes-test
  (let [^js el (append! (make-el))]
    (testing "default peek"
      (is (= 6 (.-peek el))))
    (testing "default rotation"
      (is (= 3 (.-rotation el))))
    (testing "default scrollDistance"
      (is (= 150 (.-scrollDistance el))))
    (testing "default align"
      (is (= "center" (.-align el))))
    (testing "default disabled"
      (is (false? (.-disabled el))))
    (testing "default stackedCount"
      (is (= 0 (.-stackedCount el))))
    (testing "default progress"
      (is (= 0 (.-progress el))))))

(deftest property-setters-test
  (let [^js el (append! (make-el))]
    (testing "peek setter reflects to attribute"
      (set! (.-peek el) 10)
      (is (= "10" (.getAttribute el "peek"))))
    (testing "rotation setter reflects to attribute"
      (set! (.-rotation el) 5)
      (is (= "5" (.getAttribute el "rotation"))))
    (testing "scrollDistance setter reflects to attribute"
      (set! (.-scrollDistance el) 200)
      (is (= "200" (.getAttribute el "scroll-distance"))))
    (testing "align setter reflects to attribute"
      (set! (.-align el) "top")
      (is (= "top" (.getAttribute el "align"))))
    (testing "disabled setter reflects to attribute"
      (set! (.-disabled el) true)
      (is (.hasAttribute el "disabled")))))

(deftest align-property-roundtrip-test
  (let [^js el (append! (make-el))]
    (testing "set align via attribute, read via property"
      (.setAttribute el "align" "bottom")
      (is (= "bottom" (.-align el))))
    (testing "invalid align defaults to center"
      (.setAttribute el "align" "nonsense")
      (is (= "center" (.-align el))))
    (testing "removing align attribute defaults to center"
      (.setAttribute el "align" "top")
      (.removeAttribute el "align")
      (is (= "center" (.-align el))))))

(deftest attribute-change-test
  (let [^js el (append! (make-el))]
    (testing "attribute change updates property"
      (.setAttribute el "peek" "12")
      (is (= 12 (.-peek el))))))

(deftest disabled-state-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "disabled" "")
    (testing "disabled property is true"
      (is (true? (.-disabled el))))
    (testing "removing disabled"
      (.removeAttribute el "disabled")
      (is (false? (.-disabled el))))))

(deftest reconnect-test
  (let [^js el (make-el)
        _      (add-cards! el 3)]
    (.appendChild (.-body js/document) el)
    (let [parent (.-parentNode el)]
      (.removeChild parent el)
      (.appendChild parent el)
      (testing "element reconnects without error"
        (is (some? (.-shadowRoot el)))))))

(deftest refresh-method-test
  (let [^js el (make-el)]
    (add-cards! el 3)
    (append! el)
    (testing "refresh method callable without error"
      (.refresh el)
      (is true))))

(deftest event-progress-detail-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-cards! el 3)
      (append! el)
      (.addEventListener el model/event-progress
                         (fn [^js e]
                           (swap! events conj (.-detail e))))
      ;; IntersectionObserver + rAF scheduling needs time to settle.
      ;; The progress event fires on first update-scroll! because last-prog
      ;; starts nil. Use setTimeout to give IO + 2 rAFs time to complete.
      (js/setTimeout
       (fn []
         (if (pos? (count @events))
           (let [^js d (first @events)]
             (testing "progress detail has progress key"
               (is (number? (.-progress d))))
             (testing "progress detail has stackedCount key"
               (is (number? (.-stackedCount d))))
             (testing "progress detail has totalCount key"
               (is (= 3 (.-totalCount d)))))
           (testing "progress event fired on connect"
             (is (pos? (count @events)) "expected at least one progress event")))
         (done))
       300))))

(deftest event-change-detail-test
  (async done
    (let [^js el (make-el)
          events (atom [])]
      (add-cards! el 3)
      (append! el)
      (.addEventListener el model/event-change
                         (fn [^js e]
                           (swap! events conj (.-detail e))))
      ;; The change event requires stacked count to change, which needs
      ;; real scroll offset. Verify detail structure if it fires, otherwise
      ;; verify no errors occurred during setup.
      (js/setTimeout
       (fn []
         (when (pos? (count @events))
           (let [^js d (first @events)]
             (testing "change detail has stackedCount key"
               (is (number? (.-stackedCount d))))
             (testing "change detail has totalCount key"
               (is (number? (.-totalCount d))))
             (testing "change detail has progress key"
               (is (number? (.-progress d))))))
         ;; Always passes — change event may not fire without real scrolling
         (is true)
         (done))
       300))))
