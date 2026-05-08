(ns baredom.components.x-chart.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-chart.model :as model]
            [baredom.utils.model :as mu]))

;; ---- parse-type ----

(deftest parse-type-test
  (testing "known types pass through"
    (is (= "line" (model/parse-type "line")))
    (is (= "bar"  (model/parse-type "bar")))
    (is (= "area" (model/parse-type "area"))))
  (testing "unknown values fall back to line"
    (is (= "line" (model/parse-type "pie")))
    (is (= "line" (model/parse-type nil)))
    (is (= "line" (model/parse-type "")))))

;; ---- parse-cursor ----

(deftest parse-cursor-test
  (testing "known cursors pass through"
    (is (= "nearest" (model/parse-cursor "nearest")))
    (is (= "x"       (model/parse-cursor "x")))
    (is (= "none"    (model/parse-cursor "none"))))
  (testing "unknown cursor falls back to nearest"
    (is (= "nearest" (model/parse-cursor nil)))
    (is (= "nearest" (model/parse-cursor "hover")))))

;; ---- parse-bool-attr ----

(deftest parse-bool-attr-test
  (is (true?  (mu/parse-bool-attr "")))
  (is (true?  (mu/parse-bool-attr "true")))
  (is (false? (mu/parse-bool-attr "false")))
  (is (false? (mu/parse-bool-attr nil))))

;; ---- parse-int-pos ----

(deftest parse-int-pos-test
  (is (= 200  (model/parse-int-pos "200" 180)))
  (is (= 180  (model/parse-int-pos "0"   180)))
  (is (= 180  (model/parse-int-pos "-5"  180)))
  (is (= 180  (model/parse-int-pos "abc" 180)))
  (is (= 180  (model/parse-int-pos nil   180))))

;; ---- parse-series-data ----

(deftest parse-series-data-empty-test
  (is (= [] (model/parse-series-data nil)))
  (is (= [] (model/parse-series-data "")))
  (is (= [] (model/parse-series-data "not-json"))))

(deftest parse-series-data-valid-test
  (let [json (js/JSON.stringify
              (clj->js [{:id "s0" :label "Alpha"
                         :data [{:x 1 :y 10} {:x 2 :y 20}]}]))
        result (model/parse-series-data json)]
    (is (= 1 (count result)))
    (is (= "s0" (:id (first result))))
    (is (= "Alpha" (:label (first result))))
    (is (= 2 (count (:data (first result)))))
    (is (= {:x 1 :y 10} (first (:data (first result)))))))

(deftest parse-series-data-auto-id-test
  (let [json (js/JSON.stringify
              (clj->js [{:data [{:x 0 :y 5}]}
                        {:data [{:x 0 :y 3}]}]))
        result (model/parse-series-data json)]
    (is (= 2 (count result)))
    (is (= "s0" (:id (first result))))
    (is (= "s1" (:id (second result))))))

;; ---- domain-y ----

(deftest domain-y-test
  (testing "no series returns [0 1]"
    (is (= [0 1] (model/domain-y []))))
  (testing "computes min/max across series"
    (let [series [{:data [{:x 0 :y -5} {:x 1 :y 10}]}
                  {:data [{:x 0 :y 3}  {:x 1 :y 20}]}]]
      (is (= [-5 20] (model/domain-y series))))))

;; ---- x-kind ----

(deftest x-kind-test
  (is (= "category" (model/x-kind [{:data [{:x "Jan" :y 1}]}])))
  (is (= "numeric"  (model/x-kind [{:data [{:x 0    :y 1}]}])))
  (is (= "numeric"  (model/x-kind []))))

;; ---- ticks-y ----

(deftest ticks-y-test
  (testing "returns vector of numbers"
    (let [ts (model/ticks-y 0 100 5)]
      (is (vector? ts))
      (is (>= (count ts) 1))
      (is (every? number? ts))))
  (testing "same mn and mx"
    (is (= [5] (model/ticks-y 5 5 5))))
  (testing "ticks within range"
    (let [ts (model/ticks-y 0 100 5)]
      (is (>= (first ts) 0))
      (is (<= (last ts) 100)))))

;; ---- parse-selected ----

(deftest parse-selected-test
  (is (= {:series-id "s0" :index 3} (model/parse-selected "s0:3")))
  (is (nil? (model/parse-selected nil)))
  (is (nil? (model/parse-selected "malformed")))
  (is (nil? (model/parse-selected "s0:abc"))))

;; ---- format-value ----

(deftest format-value-nil-fmt-test
  (is (= "1K"   (model/format-value 1000  nil)))
  (is (= "1M"   (model/format-value 1000000 nil)))
  (is (= "42"   (model/format-value 42    nil)))
  (is (= "3.5"  (model/format-value 3.5   nil))))

(deftest format-value-int-test
  (is (= "42"  (model/format-value 42.4 "int")))
  (is (= "43"  (model/format-value 42.7 "int"))))

(deftest format-value-pct-test
  (is (= "50%"  (model/format-value 50 "pct"))))

(deftest format-value-abbr-test
  (is (= "1.5K" (model/format-value 1500  "abbr")))
  (is (= "2.0M" (model/format-value 2000000 "abbr"))))

(deftest format-value-raw-test
  (is (= "3.14" (model/format-value 3.14 "raw"))))

(deftest format-value-fixed-test
  (is (= "3.14" (model/format-value 3.14159 "fixed:2"))))

;; ---- tooltip-position ----

(deftest tooltip-position-right-test
  ;; cursor at left — tooltip goes right
  (let [{:keys [left top]} (model/tooltip-position 100 50 80 30 400 200 8 12)]
    (is (= 112 left))
    (is (= 46  top))))

(deftest tooltip-position-flip-left-test
  ;; cursor at right edge — tooltip flips left
  (let [{:keys [left]} (model/tooltip-position 360 50 80 30 400 200 8 12)]
    (is (< left 360))))

(deftest tooltip-position-clamped-test
  ;; tooltip never goes below edge-pad from right
  (let [{:keys [left]} (model/tooltip-position 390 50 80 30 400 200 8 12)]
    (is (<= left (- 400 80 8)))))

;; ---- find-pts-at-x ----

(deftest find-pts-at-x-test
  (let [pts [{:id "s0" :px 10 :py 50 :x 1 :y 10}
             {:id "s0" :px 50 :py 40 :x 2 :y 20}
             {:id "s1" :px 10 :py 60 :x 1 :y 30}
             {:id "s1" :px 50 :py 70 :x 2 :y 40}]
        result (model/find-pts-at-x pts 12)]
    (is (= 2 (count result)))
    (is (every? #(= 10 (:px %)) result))))

;; ---- normalize ----

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "line" (:type m)))
    (is (= model/default-height (:height m)))
    (is (= model/default-padding (:padding m)))
    (is (true? (:grid? m)))
    (is (true? (:axes? m)))
    (is (false? (:tooltip? m)))
    (is (= "nearest" (:cursor m)))
    (is (false? (:disabled? m)))
    (is (false? (:loading? m)))
    (is (= [] (:series m)))
    (is (nil? (:selected m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize
           {:type-raw         "bar"
            :height-raw       "250"
            :padding-raw      "20"
            :grid-present?    "false"
            :axes-raw         "false"
            :tooltip-present? ""
            :cursor-raw       "x"
            :disabled-present? ""
            :loading-present?  ""
            :selected-raw      "s1:2"})]
    (is (= "bar"  (:type m)))
    (is (= 250    (:height m)))
    (is (= 20     (:padding m)))
    (is (false?   (:grid? m)))
    (is (false?   (:axes? m)))
    (is (true?    (:tooltip? m)))
    (is (= "x"    (:cursor m)))
    (is (true?    (:disabled? m)))
    (is (true?    (:loading? m)))
    (is (= {:series-id "s1" :index 2} (:selected m)))))

;; ── SVG coordinate math ─────────────────────────────────────────────────────
(defn- approx=
  [a b tol]
  (< (js/Math.abs (- a b)) tol))

(deftest scale-y-test
  (testing "y-axis is inverted: max-y maps to top edge, min-y to bottom edge"
    (is (approx= (model/scale-y 100 [0 100] 10 110)  10 0.001))
    (is (approx= (model/scale-y   0 [0 100] 10 110) 110 0.001))
    (is (approx= (model/scale-y  50 [0 100] 10 110)  60 0.001)))
  (testing "zero-span domain returns the rect midpoint"
    (is (approx= (model/scale-y 5 [5 5] 10 110) 60 0.001))))

(deftest scale-x-numeric-test
  (testing "linear interpolation between domain min and max"
    (is (approx= (model/scale-x-numeric  0 [0 10] 0 100)   0 0.001))
    (is (approx= (model/scale-x-numeric 10 [0 10] 0 100) 100 0.001))
    (is (approx= (model/scale-x-numeric  5 [0 10] 0 100)  50 0.001)))
  (testing "non-zero rect origin is honoured"
    (is (approx= (model/scale-x-numeric 5 [0 10] 100 200) 150 0.001)))
  (testing "zero-span domain returns the rect midpoint"
    (is (approx= (model/scale-x-numeric 7 [7 7] 0 100) 50 0.001))))

(deftest scale-x-category-test
  (testing "i in [0, n-1] spans x0 → x1 evenly: divisor is (dec n)"
    (is (approx= (model/scale-x-category 0 4 0 100)   0    0.01))
    (is (approx= (model/scale-x-category 1 4 0 100)  33.33 0.01))
    (is (approx= (model/scale-x-category 2 4 0 100)  66.67 0.01))
    (is (approx= (model/scale-x-category 3 4 0 100) 100    0.01)))
  (testing "n <= 1 collapses to the rect midpoint"
    (is (approx= (model/scale-x-category 0 1 0 100) 50 0.001))
    (is (approx= (model/scale-x-category 0 0 0 100) 50 0.001))))

(deftest compute-series-pts-numeric-test
  (let [s {:id "s1" :data [{:x 0 :y 0} {:x 5 :y 50} {:x 10 :y 100}]}
        bounds {:x0 0 :y0 0 :x1 100 :y1 100}
        pts (model/compute-series-pts s 2 "numeric" [0 10] bounds [0 100])]
    (testing "returns one annotated point per data point"
      (is (= 3 (count pts))))
    (testing "annotations carry series and point indices"
      (is (= [2 2 2] (map :si pts)))
      (is (= [0 1 2] (map :i  pts))))
    (testing "px/py are linearly scaled across the rect"
      (let [[p0 p1 p2] pts]
        (is (approx= (:px p0)   0 0.001))
        (is (approx= (:py p0) 100 0.001))     ;; y=0 maps to bottom
        (is (approx= (:px p1)  50 0.001))
        (is (approx= (:py p1)  50 0.001))
        (is (approx= (:px p2) 100 0.001))
        (is (approx= (:py p2)   0 0.001))))   ;; y=100 maps to top
    (testing "original :x and :y are preserved alongside the annotations"
      (is (= 0   (:x (first pts))))
      (is (= 100 (:y (last  pts)))))))
