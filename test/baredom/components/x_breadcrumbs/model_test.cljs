(ns baredom.components.x-breadcrumbs.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-breadcrumbs.model :as model]))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (doseq [s ["sm" "md" "lg"]]
    (is (= s (model/parse-size s)))))

(deftest parse-size-default-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "xl"))))

;; ── parse-variant ─────────────────────────────────────────────────────────
(deftest parse-variant-valid-test
  (doseq [v ["default" "subtle" "text"]]
    (is (= v (model/parse-variant v)))))

(deftest parse-variant-default-test
  (is (= "default" (model/parse-variant nil)))
  (is (= "default" (model/parse-variant "fancy"))))

;; ── parse-pos-int ─────────────────────────────────────────────────────────
(deftest parse-pos-int-valid-test
  (is (= 1  (model/parse-pos-int "1"  99)))
  (is (= 5  (model/parse-pos-int "5"  99))))

(deftest parse-pos-int-invalid-uses-fallback-test
  (is (= 99 (model/parse-pos-int nil  99)))
  (is (= 99 (model/parse-pos-int ""   99)))
  (is (= 99 (model/parse-pos-int "0"  99)))
  (is (= 99 (model/parse-pos-int "-1" 99))))

;; ── parse-pos-int-or-nil ──────────────────────────────────────────────────
(deftest parse-pos-int-or-nil-test
  (is (= 3  (model/parse-pos-int-or-nil "3")))
  (is (nil? (model/parse-pos-int-or-nil nil)))
  (is (nil? (model/parse-pos-int-or-nil "0"))))

;; ── normalize-separator ───────────────────────────────────────────────────
(deftest normalize-separator-test
  (is (= "/"  (model/normalize-separator nil)))
  (is (= "/"  (model/normalize-separator "/")))
  (is (= ">"  (model/normalize-separator ">")))
  (is (= "→"  (model/normalize-separator "→"))))

;; ── build-plan — no collapse ──────────────────────────────────────────────
(deftest build-plan-no-max-test
  (let [p (model/build-plan 5 nil 1 2)]
    (is (= [0 1 2 3 4] (:visible p)))
    (is (= -1           (:ellipsis-at p)))))

(deftest build-plan-under-max-test
  (let [p (model/build-plan 3 10 1 2)]
    (is (= [0 1 2] (:visible p)))
    (is (= -1       (:ellipsis-at p)))))

(deftest build-plan-at-max-test
  (let [p (model/build-plan 5 5 1 2)]
    (is (= [0 1 2 3 4] (:visible p)))
    (is (= -1           (:ellipsis-at p)))))

;; ── build-plan — with collapse ────────────────────────────────────────────
(deftest build-plan-collapses-middle-test
  ;; 6 items, max 4, before 1, after 2 → show [0] + ellipsis + [4 5]
  ;; Wait: before=1, after=2, total=6. With max=4: show 1 before + 2 after + ellipsis
  ;; That's 3 items plus an ellipsis slot, totalling max-items display
  (let [p (model/build-plan 6 4 1 2)]
    ;; visible should have 1 before + 2 after = 3 items
    (is (= [0 4 5] (:visible p)))
    (is (= 1        (:ellipsis-at p)))))

(deftest build-plan-collapses-with-more-before-test
  ;; 8 items, max 5, before 2, after 2
  (let [p (model/build-plan 8 5 2 2)]
    (is (= [0 1 6 7] (:visible p)))
    (is (= 2          (:ellipsis-at p)))))

(deftest build-plan-total-equals-plan-test
  (let [p (model/build-plan 5 3 1 1)]
    ;; before=1, after=1 → [0] ellipsis [4]
    (is (= [0 4] (:visible p)))
    (is (= 1      (:ellipsis-at p)))))

(deftest build-plan-at-max-clamps-before-after-test
  ;; total=4, max=3, before=2, after=2: before+after would cover all items,
  ;; but clamping means after gets reduced; result covers all 4 with ellipsis placeholder.
  ;; This edge case documents current algorithm behaviour.
  (let [p (model/build-plan 4 3 2 2)]
    (is (= [0 1 2 3] (:visible p)))
    (is (= 2          (:ellipsis-at p)))))

(deftest build-plan-single-item-test
  (let [p (model/build-plan 1 nil 1 2)]
    (is (= [0] (:visible p)))
    (is (= -1   (:ellipsis-at p)))))

(deftest build-plan-empty-test
  (let [p (model/build-plan 0 nil 1 2)]
    (is (= [] (:visible p)))
    (is (= -1  (:ellipsis-at p)))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "/" (model/normalize-separator nil)))
    (is (= "/" (:separator m)))
    (is (= "md"      (:size m)))
    (is (= "default" (:variant m)))
    (is (= 1         (:items-before m)))
    (is (= 2         (:items-after m)))
    (is (nil?        (:max-items m)))
    (is (false?      (:disabled m)))
    (is (false?      (:preserve-aria-current m)))))

(deftest normalize-all-attrs-test
  (let [m (model/normalize {:separator-raw    ">"
                             :size-raw         "lg"
                             :variant-raw      "subtle"
                             :max-items-raw    "5"
                             :items-before-raw "2"
                             :items-after-raw  "3"
                             :disabled-present?               true
                             :preserve-aria-current-present?  true
                             :aria-label-raw   "Navigation"
                             :aria-describedby-raw "desc-id"})]
    (is (= ">"          (:separator m)))
    (is (= "lg"         (:size m)))
    (is (= "subtle"     (:variant m)))
    (is (= 5            (:max-items m)))
    (is (= 2            (:items-before m)))
    (is (= 3            (:items-after m)))
    (is (true?          (:disabled m)))
    (is (true?          (:preserve-aria-current m)))
    (is (= "Navigation" (:aria-label m)))
    (is (= "desc-id"    (:aria-describedby m)))))
