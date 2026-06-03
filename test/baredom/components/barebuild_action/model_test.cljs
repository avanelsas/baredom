(ns baredom.components.barebuild-action.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [goog.object :as gobj]
            [baredom.components.barebuild.protocol :as protocol]
            [baredom.components.barebuild-action.model :as model]))

;; State values are plain JS objects (cross-cljs.core-runtime readable), so the
;; tests read them with JS interop, and phases are strings.

(deftest phases-closed-set-test
  (is (= #{"idle" "submitting" "success" "error"} model/phases)))

(deftest event-name-from-protocol-test
  (is (= protocol/event-action-state model/event-action-state)
      "the *-state event name is sourced from the shared protocol ns, not a local literal"))

(deftest idle-state-test
  (is (= "idle" (.-phase (model/idle-state)))))

(deftest submitting-state-test
  (is (= "submitting" (.-phase (model/submitting-state)))))

(deftest success-state-test
  (testing "carries response and httpStatus"
    (let [resp #js {:id 7}
          s    (model/success-state resp 201)]
      (is (= "success" (.-phase s)))
      (is (identical? resp (.-response s)))
      (is (= 201 (.-httpStatus s)))))
  (testing "response may be nil (204/empty body) and is still a success"
    (let [s (model/success-state nil 204)]
      (is (= "success" (.-phase s)))
      (is (nil? (.-response s)))
      (is (= 204 (.-httpStatus s)))))
  (testing "httpStatus omitted when nil (absent → undefined, not null-valued)"
    (is (not (gobj/containsKey (model/success-state #js {} nil) "httpStatus")))))

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
  (testing "no response key on error"
    (is (not (gobj/containsKey (model/error-state "x" 404) "response")))))

;; Every state wrapper is frozen so the cached `.state` value a consumer reads is
;; read-only (see model.cljs). Drop a freeze from any constructor and this goes red.
(deftest state-values-are-frozen-test
  (testing "all four constructors return a frozen wrapper"
    (is (js/Object.isFrozen (model/idle-state)))
    (is (js/Object.isFrozen (model/submitting-state)))
    (is (js/Object.isFrozen (model/success-state #js {} 201)))
    (is (js/Object.isFrozen (model/error-state "boom" 500))))
  (testing "frozen with or without an optional httpStatus key"
    (is (js/Object.isFrozen (model/success-state nil nil)))
    (is (js/Object.isFrozen (model/error-state "boom" nil)))))
