(ns app.components.x-timeline.x-timeline-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [goog.object :as gobj]
            [app.components.x-timeline.x-timeline      :as x]
            [app.components.x-timeline.model           :as model]
            [app.components.x-timeline-item.x-timeline-item :as x-item]
            [app.components.x-timeline-item.model      :as item-model]))

(x/init!)
(x-item/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-timeline
  ([] (.createElement js/document model/tag-name))
  ([attrs]
   (let [^js el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs]
       (if (true? v)
         (.setAttribute el (name k) "")
         (.setAttribute el (name k) (str v))))
     el)))

(defn ^js make-item
  ([] (.createElement js/document item-model/tag-name))
  ([attrs]
   (let [^js el (.createElement js/document item-model/tag-name)]
     (doseq [[k v] attrs]
       (if (true? v)
         (.setAttribute el (name k) "")
         (.setAttribute el (name k) (str v))))
     el)))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Registration ──────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ──────────────────────────────────────────────────────

(deftest shadow-structure-test
  (let [^js el (append! (make-timeline))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=label]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ─────────────────────────────────────────────────────────────

(deftest default-state-test
  (let [^js el (append! (make-timeline))]
    (is (= "list" (.getAttribute el "role")))
    (is (nil? (.getAttribute el "aria-label")))
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (.hasAttribute lbl "hidden")))))

;; ── label attribute ───────────────────────────────────────────────────────────

(deftest label-shows-when-set-test
  (let [^js el (append! (make-timeline {:label "My Timeline"}))]
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (not (.hasAttribute lbl "hidden")))
      (is (= "My Timeline" (.-textContent lbl))))
    (is (= "My Timeline" (.getAttribute el "aria-label")))))

(deftest label-hidden-when-absent-test
  (let [^js el (append! (make-timeline))]
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (.hasAttribute lbl "hidden")))
    (is (nil? (.getAttribute el "aria-label")))))

(deftest label-update-test
  (let [^js el (append! (make-timeline))]
    (.setAttribute el model/attr-label "Updated")
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (not (.hasAttribute lbl "hidden")))
      (is (= "Updated" (.-textContent lbl))))
    (is (= "Updated" (.getAttribute el "aria-label")))))

(deftest label-cleared-test
  (let [^js el (append! (make-timeline {:label "Temp"}))]
    (.removeAttribute el model/attr-label)
    (let [^js lbl (shadow-part el "[part=label]")]
      (is (.hasAttribute lbl "hidden")))
    (is (nil? (.getAttribute el "aria-label")))))

;; ── position attribute ────────────────────────────────────────────────────────

(deftest position-default-test
  (let [^js el (append! (make-timeline))]
    (is (= "start" (.-position el)))))

(deftest position-end-test
  (let [^js el (append! (make-timeline {:position "end"}))]
    (is (= "end" (.-position el)))))

(deftest position-alternating-test
  (let [^js el (append! (make-timeline {:position "alternating"}))]
    (is (= "alternating" (.-position el)))))

(deftest position-unknown-falls-back-test
  (let [^js el (append! (make-timeline {:position "center"}))]
    (is (= "start" (.-position el)))))

;; ── striped property ──────────────────────────────────────────────────────────

(deftest striped-default-false-test
  (let [^js el (append! (make-timeline))]
    (is (= false (.-striped el)))))

(deftest striped-true-test
  (let [^js el (append! (make-timeline {:striped true}))]
    (is (= true (.-striped el)))))

(deftest striped-setter-test
  (let [^js el (append! (make-timeline))]
    (set! (.-striped el) true)
    (is (.hasAttribute el model/attr-striped))
    (is (= true (.-striped el)))
    (set! (.-striped el) false)
    (is (not (.hasAttribute el model/attr-striped)))
    (is (= false (.-striped el)))))

;; ── Child indexing ────────────────────────────────────────────────────────────

(deftest child-indexing-test
  (testing "two items get correct data-index and data-last"
    (let [^js tl    (make-timeline)
          ^js item0 (make-item {:status "complete"})
          ^js item1 (make-item {:status "active"})]
      (.appendChild tl item0)
      (.appendChild tl item1)
      (append! tl)
      (is (= "0" (.getAttribute item0 "data-index")))
      (is (= "1" (.getAttribute item1 "data-index")))
      (is (not (.hasAttribute item0 "data-last")))
      (is (.hasAttribute item1 "data-last")))))

(deftest single-item-last-test
  (testing "single item gets data-last"
    (let [^js tl   (make-timeline)
          ^js item (make-item)]
      (.appendChild tl item)
      (append! tl)
      (is (= "0" (.getAttribute item "data-index")))
      (is (.hasAttribute item "data-last")))))

;; ── Position propagation ──────────────────────────────────────────────────────

(deftest position-start-propagation-test
  (testing "position=start: all items get data-position=start"
    (let [^js tl    (make-timeline {:position "start"})
          ^js item0 (make-item)
          ^js item1 (make-item)
          ^js item2 (make-item)]
      (doseq [^js i [item0 item1 item2]] (.appendChild tl i))
      (append! tl)
      (is (= "start" (.getAttribute item0 "data-position")))
      (is (= "start" (.getAttribute item1 "data-position")))
      (is (= "start" (.getAttribute item2 "data-position"))))))

(deftest position-end-propagation-test
  (testing "position=end: all items get data-position=end"
    (let [^js tl    (make-timeline {:position "end"})
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (doseq [^js i [item0 item1]] (.appendChild tl i))
      (append! tl)
      (is (= "end" (.getAttribute item0 "data-position")))
      (is (= "end" (.getAttribute item1 "data-position"))))))

(deftest position-alternating-propagation-test
  (testing "position=alternating: even→start, odd→end"
    (let [^js tl    (make-timeline {:position "alternating"})
          ^js item0 (make-item)
          ^js item1 (make-item)
          ^js item2 (make-item)]
      (doseq [^js i [item0 item1 item2]] (.appendChild tl i))
      (append! tl)
      (is (= "start" (.getAttribute item0 "data-position")))
      (is (= "end"   (.getAttribute item1 "data-position")))
      (is (= "start" (.getAttribute item2 "data-position"))))))

;; ── Striped propagation ───────────────────────────────────────────────────────

(deftest striped-propagation-test
  (testing "striped attribute: all items get data-striped"
    (let [^js tl    (make-timeline {:striped true})
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (doseq [^js i [item0 item1]] (.appendChild tl i))
      (append! tl)
      (is (.hasAttribute item0 "data-striped"))
      (is (.hasAttribute item1 "data-striped")))))

(deftest striped-not-set-test
  (testing "no striped attribute: items do not get data-striped"
    (let [^js tl    (make-timeline)
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (doseq [^js i [item0 item1]] (.appendChild tl i))
      (append! tl)
      (is (not (.hasAttribute item0 "data-striped")))
      (is (not (.hasAttribute item1 "data-striped"))))))

;; ── Dynamic child addition ────────────────────────────────────────────────────

(deftest dynamic-add-reindexes-test
  (testing "adding a third item re-indexes and updates data-last"
    (let [^js tl    (make-timeline)
          ^js item0 (make-item)
          ^js item1 (make-item)
          ^js item2 (make-item)]
      (.appendChild tl item0)
      (.appendChild tl item1)
      (append! tl)
      ;; item1 is last before adding item2
      (is (.hasAttribute item1 "data-last"))
      ;; Add third item — its connectedCallback fires x-timeline-item-connected
      ;; which bubbles to x-timeline, triggering update-items!
      (.appendChild tl item2)
      (is (= "0" (.getAttribute item0 "data-index")))
      (is (= "1" (.getAttribute item1 "data-index")))
      (is (= "2" (.getAttribute item2 "data-index")))
      (is (not (.hasAttribute item0 "data-last")))
      (is (not (.hasAttribute item1 "data-last")))
      (is (.hasAttribute item2 "data-last")))))

;; ── Dynamic child removal ─────────────────────────────────────────────────────

(deftest dynamic-remove-reindexes-test
  (testing "removing last item makes previous item the new last"
    (let [^js tl    (make-timeline)
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (.appendChild tl item0)
      (.appendChild tl item1)
      (append! tl)
      (is (.hasAttribute item1 "data-last"))
      ;; Remove item1 — its disconnectedCallback fires x-timeline-item-disconnected
      ;; on document, x-timeline's document listener calls update-items!
      (.remove item1)
      (is (= "0" (.getAttribute item0 "data-index")))
      (is (.hasAttribute item0 "data-last")))))

;; ── attributeChangedCallback propagation ─────────────────────────────────────

(deftest attribute-changed-position-propagates-test
  (testing "changing position attribute updates all child data-position attrs"
    (let [^js tl    (make-timeline {:position "start"})
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (doseq [^js i [item0 item1]] (.appendChild tl i))
      (append! tl)
      (is (= "start" (.getAttribute item0 "data-position")))
      (is (= "start" (.getAttribute item1 "data-position")))
      ;; Change position to alternating
      (.setAttribute tl model/attr-position "alternating")
      (is (= "start" (.getAttribute item0 "data-position")))
      (is (= "end"   (.getAttribute item1 "data-position"))))))

(deftest attribute-changed-striped-propagates-test
  (testing "adding striped attribute sets data-striped on all children"
    (let [^js tl    (make-timeline)
          ^js item0 (make-item)
          ^js item1 (make-item)]
      (doseq [^js i [item0 item1]] (.appendChild tl i))
      (append! tl)
      (is (not (.hasAttribute item0 "data-striped")))
      (.setAttribute tl model/attr-striped "")
      (is (.hasAttribute item0 "data-striped"))
      (is (.hasAttribute item1 "data-striped"))
      (.removeAttribute tl model/attr-striped)
      (is (not (.hasAttribute item0 "data-striped")))
      (is (not (.hasAttribute item1 "data-striped"))))))

;; ── x-timeline-select event ───────────────────────────────────────────────────

(deftest select-event-fires-test
  (testing "x-timeline-item-click on a child fires x-timeline-select on parent"
    (let [^js tl   (make-timeline)
          ^js item (make-item {:status "active" :label "Launch"})
          received (atom nil)]
      (.appendChild tl item)
      (append! tl)
      (.addEventListener tl model/event-select
                         (fn [^js e] (reset! received e)))
      ;; Simulate click event from item (as if user clicked)
      (.dispatchEvent item
                      (js/CustomEvent. model/child-event-click
                                       #js {:detail     #js {"status" "active" "label" "Launch"}
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true}))
      (is (some? @received))
      (let [^js e @received
            ^js d (.-detail e)]
        (is (= 0        (gobj/get d "index")))
        (is (= "active" (gobj/get d "status")))
        (is (= "Launch" (gobj/get d "label")))))))

(deftest select-event-stops-propagation-test
  (testing "x-timeline-item-click does not propagate above x-timeline"
    (let [^js tl       (make-timeline)
          ^js item     (make-item {:status "active"})
          outer-fired? (atom false)]
      (.appendChild tl item)
      (append! tl)
      (.addEventListener (.-body js/document) model/child-event-click
                         (fn [_e] (reset! outer-fired? true)))
      (.dispatchEvent item
                      (js/CustomEvent. model/child-event-click
                                       #js {:detail     #js {"status" "active" "label" ""}
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true}))
      (is (= false @outer-fired?)))))

;; ── JS property API ───────────────────────────────────────────────────────────

(deftest property-label-getter-test
  (let [^js el (append! (make-timeline {:label "Test"}))]
    (is (= "Test" (.-label el)))))

(deftest property-label-setter-test
  (let [^js el (append! (make-timeline))]
    (set! (.-label el) "Set Label")
    (is (= "Set Label" (.getAttribute el model/attr-label)))))

(deftest property-position-default-test
  (let [^js el (append! (make-timeline))]
    (is (= "start" (.-position el)))))

(deftest property-position-setter-test
  (let [^js el (append! (make-timeline))]
    (set! (.-position el) "end")
    (is (= "end" (.getAttribute el model/attr-position)))))

;; ── Reconnect safety ──────────────────────────────────────────────────────────

(deftest reconnect-no-double-listener-test
  (testing "after remove and re-append, select fires exactly once"
    (let [^js tl    (make-timeline)
          ^js item  (make-item {:status "pending"})
          fire-count (atom 0)]
      (.appendChild tl item)
      (append! tl)
      (.addEventListener tl model/event-select (fn [_e] (swap! fire-count inc)))
      ;; Remove and re-attach timeline
      (.remove tl)
      (append! tl)
      ;; Fire click on item
      (.dispatchEvent item
                      (js/CustomEvent. model/child-event-click
                                       #js {:detail     #js {"status" "pending" "label" ""}
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true}))
      (is (= 1 @fire-count)))))
