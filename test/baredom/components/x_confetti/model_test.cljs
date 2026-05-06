(ns baredom.components.x-confetti.model-test
  (:require
   [cljs.test :refer [deftest is testing]]
   [baredom.components.x-confetti.model :as model]))

(deftest mode-and-origin-test
  (testing "mode falls back to default for unknown / nil"
    (is (= "overlay" (model/parse-mode nil)))
    (is (= "overlay" (model/parse-mode "")))
    (is (= "overlay" (model/parse-mode "xyz")))
    (is (= "inline"  (model/parse-mode "inline")))
    (is (= "overlay" (model/parse-mode "overlay"))))

  (testing "origin falls back to default for unknown / nil"
    (is (= "top"    (model/parse-origin nil)))
    (is (= "top"    (model/parse-origin "diagonal")))
    (doseq [v ["top" "center" "bottom" "point"]]
      (is (= v (model/parse-origin v))))))

(deftest numeric-clamping-test
  (testing "count clamps to [count-min, count-max] and rounds"
    (is (= model/default-count (model/parse-count nil)))
    (is (= model/default-count (model/parse-count "abc")))
    (is (= model/count-min     (model/parse-count "0")))
    (is (= model/count-max     (model/parse-count "99999")))
    (is (= 50                  (model/parse-count "50")))
    (is (= 50                  (model/parse-count "49.6"))))

  (testing "spread clamps into [0,180]"
    (is (= model/default-spread (model/parse-spread nil)))
    (is (= 0                    (model/parse-spread "-10")))
    (is (= 180                  (model/parse-spread "999")))
    (is (= 90                   (model/parse-spread "90"))))

  (testing "velocity clamps into [1,60]"
    (is (= 1                      (model/parse-velocity "0")))
    (is (= 60                     (model/parse-velocity "9999")))
    (is (= 14                     (model/parse-velocity "14")))
    (is (= model/default-velocity (model/parse-velocity nil))))

  (testing "gravity clamps into [-2,2]"
    (is (= -2  (model/parse-gravity "-10")))
    (is (= 2   (model/parse-gravity "10")))
    (is (= 0.0 (model/parse-gravity "0"))))

  (testing "duration clamps into [200,20000]"
    (is (= 200   (model/parse-duration "0")))
    (is (= 20000 (model/parse-duration "999999")))
    (is (= 1000  (model/parse-duration "1000")))))

(deftest colors-parsing-test
  (testing "empty / missing returns the theme default palette"
    (is (= model/default-palette (model/parse-colors nil)))
    (is (= model/default-palette (model/parse-colors "")))
    (is (= model/default-palette (model/parse-colors "   "))))

  (testing "comma-separated values are split and trimmed"
    (is (= ["#ff0066" "#00ccaa" "gold"]
           (model/parse-colors "#ff0066, #00ccaa ,gold ")))))

(deftest shapes-parsing-test
  (testing "empty / missing returns default shapes"
    (is (= ["square" "ribbon"] (model/parse-shapes nil)))
    (is (= ["square" "ribbon"] (model/parse-shapes "")))
    (is (= ["square" "ribbon"] (model/parse-shapes "garbage"))))

  (testing "filters unknown values"
    (is (= ["square" "circle"]
           (model/parse-shapes "square,nope,circle"))))

  (testing "accepts the full shape vocabulary"
    (is (= ["square" "circle" "ribbon" "star"]
           (model/parse-shapes "square,circle,ribbon,star")))))

(deftest normalize-shape-test
  (testing "normalize returns a stable shape with all keys"
    (let [m (model/normalize {:mode-raw     "inline"
                              :origin-raw   "center"
                              :count-raw    "120"
                              :spread-raw   "45"
                              :velocity-raw "20"
                              :gravity-raw  "0.5"
                              :duration-raw "3000"
                              :colors-raw   "red,blue"
                              :shapes-raw   "circle,star"
                              :auto-fire?   true
                              :disabled?    false})]
      (is (= "inline" (:mode m)))
      (is (= "center" (:origin m)))
      (is (= 120 (:count m)))
      (is (= 45  (:spread m)))
      (is (= 20  (:velocity m)))
      (is (= 0.5 (:gravity m)))
      (is (= 3000 (:duration m)))
      (is (= ["red" "blue"]      (:colors m)))
      (is (= ["circle" "star"]   (:shapes m)))
      (is (true?  (:auto-fire? m)))
      (is (false? (:disabled? m)))))

  (testing "normalize falls back to defaults for nil inputs"
    (let [m (model/normalize {})]
      (is (= model/default-mode     (:mode m)))
      (is (= model/default-origin   (:origin m)))
      (is (= model/default-count    (:count m)))
      (is (= model/default-spread   (:spread m)))
      (is (= model/default-velocity (:velocity m)))
      (is (= model/default-gravity  (:gravity m)))
      (is (= model/default-duration (:duration m)))
      (is (= model/default-palette  (:colors m)))
      (is (= ["square" "ribbon"]    (:shapes m)))
      (is (false? (:auto-fire? m)))
      (is (false? (:disabled? m))))))

(deftest origin-point-test
  (testing "top → top-center"
    (is (= [50 0] (model/origin-point "top" 100 80 nil nil))))
  (testing "bottom → bottom-center"
    (is (= [50 80] (model/origin-point "bottom" 100 80 nil nil))))
  (testing "center → middle"
    (is (= [50 40] (model/origin-point "center" 100 80 nil nil))))
  (testing "point uses provided coordinates"
    (is (= [12 34] (model/origin-point "point" 100 80 12 34))))
  (testing "point falls back to center if coords missing"
    (is (= [50 40] (model/origin-point "point" 100 80 nil nil)))))

(deftest launch-angle-test
  (testing "top with zero spread always launches straight down"
    (is (< (js/Math.abs
             (- (model/launch-angle-rad "top" 0 0.5)
                (* 0.5 js/Math.PI)))
           1e-9))
    (is (< (js/Math.abs
             (- (model/launch-angle-rad "top" 0 0.0)
                (* 0.5 js/Math.PI)))
           1e-9)))

  (testing "bottom with zero spread always launches straight up"
    (is (< (js/Math.abs
             (- (model/launch-angle-rad "bottom" 0 0.7)
                (* -0.5 js/Math.PI)))
           1e-9)))

  (testing "top spread shifts the angle within ± spread radians"
    (let [spread-deg 60
          a-low  (model/launch-angle-rad "top" spread-deg 0.0)
          a-high (model/launch-angle-rad "top" spread-deg 1.0)
          base   (* 0.5 js/Math.PI)
          spread-rad (/ (* spread-deg js/Math.PI) 180)]
      (is (< (js/Math.abs (- a-low  (- base spread-rad))) 1e-9))
      (is (< (js/Math.abs (- a-high (+ base spread-rad))) 1e-9)))))
