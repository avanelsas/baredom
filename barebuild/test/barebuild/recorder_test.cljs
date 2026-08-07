(ns barebuild.recorder-test
  "The dev-only recording seam: what record! does with an entry, and with a recorder that throws.
   The install rule it shares with the decorator is covered in hook-test. What the element puts in
   an entry is covered in the browser suite."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.console-capture :refer [errors-while]]
            [barebuild.recorder :as recorder]))

(use-fixtures :each (fn [t] (recorder/install! nil) (t) (recorder/install! nil)))

(deftest record-is-a-noop-with-no-recorder-set
  (testing "with no recorder, record! does nothing and returns nil"
    (is (nil? (recorder/record! {:event [:connected {}]})))))

(deftest record-forwards-each-entry-to-the-recorder
  (let [seen (atom [])]
    (is (nil? (recorder/install! (fn [entry] (swap! seen conj entry))))
        "installing answers nothing, it is done for the effect")
    (recorder/record! {:event [:a]})
    (recorder/record! {:event [:b]})
    (testing "every entry reaches the installed recorder, in order"
      (is (= [{:event [:a]} {:event [:b]}] @seen)))))

(deftest a-throwing-recorder-is-reported-not-propagated
  (let [returned (atom :unset)
        errors   (errors-while
                  (fn []
                    (recorder/install! (fn [_] (throw (js/Error. "boom"))))
                    (reset! returned (recorder/record! {:event [:a]}))))]
    (testing "a buggy recorder cannot break the runtime, record! isolates it and returns nil"
      (is (nil? @returned)))
    (testing "and says so rather than failing in silence"
      (is (= 1 (count errors))))))

(deftest this-seam-applies-the-shared-install-rule
  (testing "pins that the rule reaches the public entry point, its edge cases living in hook-test"
    (let [seen   (atom [])
          errors (errors-while (fn []
                                 (recorder/install! (fn [entry] (swap! seen conj entry)))
                                 (recorder/install! "a recorder")))]
      (is (= 1 (count errors)))
      (recorder/record! {:event [:a]})
      (is (= [{:event [:a]}] @seen) "the working recorder is left in place"))))
