(ns baredom.components.x-skeleton-group.x-skeleton-group-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-skeleton-group.x-skeleton-group :as x]
            [baredom.components.x-skeleton-group.model            :as model]
            [baredom.components.x-skeleton.model                  :as skeleton-model]
            [baredom.test-writes                                  :as writes]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
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
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=container]")))
    (is (some? (shadow-part el "[part=preset-container]")))
    (is (some? (shadow-part el "slot")))))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-preset)))
    (is (= "true" (.getAttribute el "aria-hidden")))))

;; ── Preset: card renders skeleton children ───────────────────────────────────
(deftest preset-card-renders-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "card")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (>= (.-length skeletons) 4)))))

;; ── Preset: paragraph renders 4 text skeletons ──────────────────────────────
(deftest preset-paragraph-renders-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "paragraph")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (= 4 (.-length skeletons))))))

;; ── Preset: profile renders circle + 2 text ─────────────────────────────────
(deftest preset-profile-renders-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "profile")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (= 3 (.-length skeletons)))
      (is (= "circle" (.getAttribute (aget skeletons 0) "variant"))))))

;; ── Count attribute repeats preset ───────────────────────────────────────────
(deftest count-repeats-preset-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "paragraph")
    (.setAttribute el model/attr-count "3")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      ;; 4 items per paragraph * 3 repetitions = 12
      (is (= 12 (.-length skeletons))))))

;; ── Animation propagated to preset children ──────────────────────────────────
(deftest animation-propagated-to-preset-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "card")
    (.setAttribute el model/attr-animation "wave")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js first-skel (.querySelector preset-el "x-skeleton")]
      (is (= "wave" (.getAttribute first-skel "animation"))))))

;; ── Animation propagated to slotted children ─────────────────────────────────
(deftest animation-propagated-to-slotted-test
  (async done
    (let [^js el   (make-el)
          ^js skel (.createElement js/document "x-skeleton")]
      (.setAttribute el model/attr-animation "wave")
      (.appendChild el skel)
      (append! el)
      (js/setTimeout
       (fn []
         (is (= "wave" (.getAttribute skel skeleton-model/group-attr-animation)))
         (done))
       50))))

;; ── Preset: list-item renders circle + 2 text ────────────────────────────────
(deftest preset-list-item-renders-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "list-item")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (= 3 (.-length skeletons)))
      (is (= "circle" (.getAttribute (aget skeletons 0) "variant"))))))

;; ── Preset: table-row renders 4 cells ────────────────────────────────────────
(deftest preset-table-row-renders-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "table-row")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (= 4 (.-length skeletons))))))

;; ── Dynamic preset change ────────────────────────────────────────────────────
(deftest dynamic-preset-change-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "card")
    (let [^js preset-el (shadow-part el "[part=preset-container]")]
      (is (>= (.-length (.querySelectorAll preset-el "x-skeleton")) 4))
      ;; Change to profile
      (.setAttribute el model/attr-preset "profile")
      (let [^js skeletons (.querySelectorAll preset-el "x-skeleton")]
        (is (= 3 (.-length skeletons)))
        (is (= "circle" (.getAttribute (aget skeletons 0) "variant")))))))

;; ── Animation "none" propagated to preset children ───────────────────────────
(deftest animation-none-propagated-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "paragraph")
    (.setAttribute el model/attr-animation "none")
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js first-skel (.querySelector preset-el "x-skeleton")]
      (is (= "none" (.getAttribute first-skel "animation"))))))

;; ── Removing preset clears generated children ────────────────────────────────
(deftest removing-preset-clears-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "card")
    (let [^js preset-el (shadow-part el "[part=preset-container]")]
      (is (pos? (.-childElementCount preset-el)))
      (.removeAttribute el model/attr-preset)
      (is (zero? (.-childElementCount preset-el))))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest preset-property-test
  (let [^js el (append! (make-el))]
    (set! (.-preset el) "card")
    (is (= "card" (.getAttribute el model/attr-preset)))
    (is (= "card" (.-preset el)))))

(deftest animation-property-test
  (let [^js el (append! (make-el))]
    (set! (.-animation el) "wave")
    (is (= "wave" (.getAttribute el model/attr-animation)))))

(deftest count-property-test
  (let [^js el (append! (make-el))]
    (set! (.-count el) 5)
    (is (= "5" (.getAttribute el model/attr-count)))
    (is (= 5 (.-count el)))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-renders-once-test
  (let [^js el (make-el)]
    (.setAttribute el model/attr-preset "paragraph")
    (.appendChild (.-body js/document) el)
    (.remove el)
    (.appendChild (.-body js/document) el)
    (let [^js preset-el (shadow-part el "[part=preset-container]")
          ^js skeletons (.querySelectorAll preset-el "x-skeleton")]
      (is (= 4 (.-length skeletons))))))

;; ── Invalid preset renders nothing ───────────────────────────────────────────
(deftest invalid-preset-renders-nothing-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-preset "unknown")
    (let [^js preset-el (shadow-part el "[part=preset-container]")]
      (is (zero? (.-childElementCount preset-el))))))

;; ── A child change ──────────────────────────────────────────────────────────
(defn- delay-of [^js node]
  (.getPropertyValue (.-style (shadow-part node "[part=base]")) "--_x-skeleton-delay"))

(deftest a-new-skeleton-keeps-the-others-in-phase-test
  (async done
    (let [^js el    (make-el)
          ^js skeleton (.createElement js/document "x-skeleton")]
      (.appendChild el skeleton)
      (append! el)
      (js/setTimeout
       (fn []
         (let [before    (delay-of skeleton)
               n         (writes/count-attr-writes! skeleton [skeleton-model/group-attr-animation])
               ^js added (.createElement js/document "x-skeleton")]
           (.appendChild el added)
           (js/setTimeout
            (fn []
              (is (= before (delay-of skeleton)) "the first skeleton keeps its delay")
              (is (= 0 @n) "the first skeleton is not written")
              (is (not= "" (delay-of added)) "the new skeleton gets a delay of its own")
              (done))
            50)))
       50))))

(deftest a-new-animation-puts-the-slotted-skeletons-in-phase-again-test
  (async done
    (let [^js el       (make-el)
          ^js skeleton (.createElement js/document "x-skeleton")]
      (.appendChild el skeleton)
      (append! el)
      (js/setTimeout
       (fn []
         (let [before (delay-of skeleton)]
           (.setAttribute el model/attr-animation "wave")
           (is (= "wave" (.getAttribute skeleton skeleton-model/group-attr-animation)))
           (is (not= before (delay-of skeleton)) "the skeleton gets the delay of its new animation")
           (done)))
       50))))

;; ── Writes on other nodes ───────────────────────────────────────────────────
(deftest x-skeleton-group-writes-only-what-it-declares-on-other-nodes-test
  (let [^js el (make-el)]
    (.appendChild el (.createElement js/document "x-skeleton"))
    (append! el)
    (let [written (writes/foreign-writes! el #(.setAttribute el model/attr-animation "wave"))]
      (is (= {:undeclared #{} :unexercised #{}} (writes/audit model/foreign-writes written))))))

;; ── The skeleton's own animation ────────────────────────────────────────────
(deftest a-skeleton-takes-its-own-animation-and-else-the-groups-test
  (async done
    (let [^js el    (make-el)
          ^js own   (.createElement js/document skeleton-model/tag-name)
          ^js plain (.createElement js/document skeleton-model/tag-name)]
      (.setAttribute own skeleton-model/attr-animation "none")
      (.setAttribute el model/attr-animation "wave")
      (.appendChild el own)
      (.appendChild el plain)
      (append! el)
      (js/setTimeout
       (fn []
         (is (= "none" (.getAttribute (shadow-part own "[part=base]") "data-animation"))
             "the skeleton with an animation keeps it")
         (is (= "wave" (.getAttribute (shadow-part plain "[part=base]") "data-animation"))
             "the skeleton without one takes the group's")
         (is (= "none" (.getAttribute own skeleton-model/attr-animation))
             "the group leaves the skeleton's animation")
         (done))
       50))))

(deftest a-new-group-animation-keeps-the-phase-of-a-skeleton-with-its-own-test
  (async done
    (let [^js el  (make-el)
          ^js own (.createElement js/document skeleton-model/tag-name)]
      (.setAttribute own skeleton-model/attr-animation "none")
      (.appendChild el own)
      (append! el)
      (js/setTimeout
       (fn []
         (let [before (delay-of own)]
           (.setAttribute el model/attr-animation "wave")
           (is (= before (delay-of own)))
           (done)))
       50))))
