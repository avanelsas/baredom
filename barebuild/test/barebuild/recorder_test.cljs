(ns barebuild.recorder-test
  "The dev-only recording seam: nil hook by default, set via set-recorder!,
   and a hook that throws must never propagate into the runtime."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.recorder :as recorder]))

(use-fixtures :each (fn [t] (recorder/set-recorder! nil) (t) (recorder/set-recorder! nil)))

(deftest record-is-a-noop-with-no-recorder-set
  (testing "with no recorder, record! does nothing and returns nil"
    (is (nil? (recorder/record! {:event [:connected {}]})))))

(deftest record-forwards-each-entry-to-the-recorder
  (let [seen (atom [])]
    (recorder/set-recorder! (fn [entry] (swap! seen conj entry)))
    (recorder/record! {:event [:a]})
    (recorder/record! {:event [:b]})
    (testing "every entry reaches the set recorder, in order"
      (is (= [{:event [:a]} {:event [:b]}] @seen)))))

(deftest a-throwing-recorder-is-swallowed
  (recorder/set-recorder! (fn [_] (throw (js/Error. "boom"))))
  (testing "a buggy recorder cannot break the runtime — record! swallows it and returns nil"
    (is (nil? (recorder/record! {:event [:a]})))))
