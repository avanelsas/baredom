(ns barebuild.decorator-test
  "The per-request hook seam: nil by default, and read by the executor from a namespace with no
   requires, so it can be exercised without a DOM. Installing it through barebuild.core/init is
   covered in the browser suite, where custom-element registration can run."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.decorator :as decorator]))

(use-fixtures :each (fn [t] (decorator/set-request-decorator! nil) (t)
                      (decorator/set-request-decorator! nil)))

(deftest no-decorator-is-registered-by-default
  (testing "nothing is installed until an app asks for it, so the executor skips the hook"
    (is (nil? (decorator/current)))))

(deftest the-registered-decorator-is-what-the-executor-reads
  (let [f (fn [_request] {"authorization" "Bearer t"})]
    (decorator/set-request-decorator! f)
    (is (identical? f (decorator/current)) "the fn itself, not a wrapper")
    (testing "nil clears it"
      (decorator/set-request-decorator! nil)
      (is (nil? (decorator/current))))))
