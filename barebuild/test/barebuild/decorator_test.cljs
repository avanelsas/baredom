(ns barebuild.decorator-test
  "The per-request hook seam: nil by default, and reading back what was installed. The install rule
   it shares with the recorder is covered in hook-test. Installing it through barebuild.core/init is
   covered in the browser suite."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.console-capture :refer [errors-while]]
            [barebuild.decorator :as decorator]))

(use-fixtures :each (fn [t] (decorator/install! nil) (t)
                      (decorator/install! nil)))

(deftest no-decorator-is-registered-by-default
  (testing "nothing is installed until an app asks for it, so the executor skips the hook"
    (is (nil? (decorator/current)))))

(deftest the-registered-decorator-is-what-the-executor-reads
  (let [f (fn [_request] {"authorization" "Bearer t"})]
    (is (nil? (decorator/install! f)) "installing answers nothing, it is done for the effect")
    (is (identical? f (decorator/current)) "the fn itself, not a wrapper")
    (testing "nil clears it"
      (decorator/install! nil)
      (is (nil? (decorator/current))))))

(deftest this-seam-applies-the-shared-install-rule
  (testing "pins that the rule reaches the public entry point, its edge cases living in hook-test"
    (let [f      (fn [_request] {"authorization" "Bearer t"})
          errors (errors-while (fn []
                                 (decorator/install! f)
                                 (decorator/install! "Bearer t")))]
      (is (= 1 (count errors)))
      (is (identical? f (decorator/current)) "the working decorator is left in place"))))
