(ns baredom.components.barebuild-data.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [goog.object :as gobj]
            [baredom.components.barebuild-data.model :as model]))

;; State values are plain JS objects (cross-cljs.core-runtime readable), so the
;; tests read them with JS interop, and phases are strings.

(deftest phases-closed-set-test
  (is (= #{"idle" "loading" "loaded" "error"} model/phases)))

(deftest idle-state-test
  (is (= "idle" (.-phase (model/idle-state)))))

(deftest loading-state-test
  (is (= "loading" (.-phase (model/loading-state)))))

(deftest loaded-state-test
  (testing "carries data and httpStatus"
    (let [data #js {}
          s    (model/loaded-state data 200)]
      (is (= "loaded" (.-phase s)))
      (is (identical? data (.-data s)))
      (is (= 200 (.-httpStatus s)))))
  (testing "httpStatus omitted when nil (absent → undefined, not null-valued)"
    (let [s (model/loaded-state #js {} nil)]
      (is (not (gobj/containsKey s "httpStatus"))))))

(deftest error-state-test
  (testing "carries message and httpStatus"
    (let [s (model/error-state "HTTP 500" 500)]
      (is (= "error" (.-phase s)))
      (is (= "HTTP 500" (.-error s)))
      (is (= 500 (.-httpStatus s)))))
  (testing "httpStatus omitted for transport-level failure"
    (let [s (model/error-state "boom" nil)]
      (is (= "boom" (.-error s)))
      (is (not (gobj/containsKey s "httpStatus")))))
  (testing "no data key on error"
    (is (not (gobj/containsKey (model/error-state "x" 404) "data")))))

;; Every state wrapper is frozen so the cached `.state` value a consumer reads is
;; read-only (see model.cljs). This guards that contract: drop a freeze from any
;; constructor and this test goes red rather than the regression slipping through.
(deftest state-values-are-frozen-test
  (testing "all four constructors return a frozen wrapper"
    (is (js/Object.isFrozen (model/idle-state)))
    (is (js/Object.isFrozen (model/loading-state)))
    (is (js/Object.isFrozen (model/loaded-state #js {} 200)))
    (is (js/Object.isFrozen (model/error-state "boom" 500))))
  (testing "frozen with or without an optional httpStatus key"
    (is (js/Object.isFrozen (model/loaded-state #js {} nil)))
    (is (js/Object.isFrozen (model/error-state "boom" nil)))))
