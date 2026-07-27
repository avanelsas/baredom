(ns barereplay.store-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [barereplay.store :as store]))

(use-fixtures :each (fn [t] (store/unsubscribe!) (store/clear!) (t)
                      (store/unsubscribe!) (store/clear!)))

(deftest subscribe-sees-each-record-in-order
  (let [seen (atom [])]
    (store/subscribe! (fn [entries] (swap! seen conj entries)))
    (store/record! {:event [:a]})
    (store/record! {:event [:b]})
    (testing "the watcher fires once per record with the growing log"
      (is (= [[{:event [:a]}]
              [{:event [:a]} {:event [:b]}]]
             @seen)))))

(deftest unsubscribe-stops-further-callbacks
  (let [seen (atom [])]
    (store/subscribe! (fn [entries] (swap! seen conj entries)))
    (store/record! {:event [:a]})
    (store/unsubscribe!)
    (store/record! {:event [:b]})
    (testing "after unsubscribe! no further records reach the watcher"
      (is (= [[{:event [:a]}]] @seen)))))
