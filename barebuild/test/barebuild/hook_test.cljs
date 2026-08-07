(ns barebuild.hook-test
  "The install rule both seams share. Tested once here rather than once per seam, where two copies
   could drift into two different rules without either test noticing."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.console-capture :refer [errors-while]]
            [barebuild.hook :as hook]))

(deftest installing-answers-nothing
  (testing "it is done for the effect, so a caller cannot mistake the return for the hook"
    (is (nil? (hook/install! (atom nil) "recorder" (fn []))))))

(deftest a-callable-replaces-what-was-there-and-nil-clears-it
  (let [slot (atom nil)
        f    (fn [])]
    (hook/install! slot "recorder" f)
    (is (identical? f @slot) "the fn itself, not a wrapper")
    (hook/install! slot "recorder" nil)
    (is (nil? @slot))))

(deftest what-cannot-be-called-is-refused-at-the-install-site
  (let [slot   (atom nil)
        f      (fn [])
        errors (errors-while (fn []
                               (hook/install! slot "recorder" f)
                               (hook/install! slot "recorder" "not a fn")))]
    (testing "the mistake is reported where it was made, not once per call forever after"
      (is (= 1 (count errors))))
    (testing "and the working hook is left in place rather than replaced by a broken one"
      (is (identical? f @slot)))))

(deftest false-is-refused-rather-than-silently-meaning-no-hook
  (let [slot   (atom nil)
        errors (errors-while (fn [] (hook/install! slot "recorder" false)))]
    (is (= 1 (count errors)))
    (is (nil? @slot) "nothing callable was ever installed")))

(deftest the-kind-names-the-seam-in-the-complaint
  (testing "one rule serves both seams, so the message has to say which one was misused"
    (let [errors (errors-while (fn [] (hook/install! (atom nil) "request decorator" 42)))]
      (is (re-find #"request decorator" (first (first errors)))))))
