(ns barebuild.recorder-test
  "The dev-only recording seam, free of requires so it exercises under Node. What the element
   puts in an entry is covered in the browser suite."
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barebuild.recorder :as recorder]))

(use-fixtures :each (fn [t] (recorder/install! nil) (t) (recorder/install! nil)))

(defn- errors-while
  "The console.error calls `f` made, real console restored afterwards."
  [f]
  (let [errors     (atom [])
        real-error (.-error js/console)]
    (set! (.-error js/console) (fn [& args] (swap! errors conj (vec args))))
    (try (f)
         (finally (set! (.-error js/console) real-error)))
    @errors))

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

(deftest what-cannot-be-called-is-refused-at-the-install-site
  (let [seen   (atom [])
        errors (errors-while
                (fn []
                  (recorder/install! (fn [entry] (swap! seen conj entry)))
                  (recorder/install! "a recorder")))]
    (testing "the mistake is reported where it was made, not once per event forever after"
      (is (= 1 (count errors))))
    (recorder/record! {:event [:a]})
    (testing "and the working recorder is left in place rather than replaced by a broken one"
      (is (= [{:event [:a]}] @seen)))))

(deftest false-is-refused-rather-than-silently-meaning-no-recorder
  (let [errors (errors-while (fn [] (recorder/install! false)))]
    (is (= 1 (count errors)))
    (is (nil? (recorder/record! {:event [:a]})) "nothing callable was ever installed")))
