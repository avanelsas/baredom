(ns barebuild.decorator-test
  "The per-request hook seam: nil by default, refusing what it cannot call, and free of requires
   so it exercises without a DOM. Installing it through barebuild.core/init is covered in the
   browser suite."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.decorator :as decorator]))

(use-fixtures :each (fn [t] (decorator/install! nil) (t)
                      (decorator/install! nil)))

(defn- errors-while
  "The console.error calls `f` made, real console restored afterwards."
  [f]
  (let [errors     (atom [])
        real-error (.-error js/console)]
    (set! (.-error js/console) (fn [& args] (swap! errors conj (vec args))))
    (try (f)
         (finally (set! (.-error js/console) real-error)))
    @errors))

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

(deftest what-cannot-be-called-is-refused-at-the-install-site
  (let [f      (fn [_request] {"authorization" "Bearer t"})
        errors (errors-while (fn []
                               (decorator/install! f)
                               (decorator/install! "Bearer t")))]
    (testing "the mistake is reported where it was made, not once per request forever after"
      (is (= 1 (count errors))))
    (testing "and the working decorator is left in place rather than replaced by a broken one"
      (is (identical? f (decorator/current))))))

(deftest false-is-refused-rather-than-silently-meaning-no-decorator
  (let [errors (errors-while (fn [] (decorator/install! false)))]
    (is (= 1 (count errors)))
    (is (nil? (decorator/current)) "nothing callable was ever installed")))
