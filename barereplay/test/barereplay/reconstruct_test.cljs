(ns barereplay.reconstruct-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.resource :as resource]
            [barereplay.reconstruct :as rc]))

(def seed
  {:resource/id "tasks" :endpoint "/api/tasks" :last-accepted nil})

(defn- record-log [seed events]
  (:log (reduce (fn [{:keys [r log]} e]
                  (let [after (:resource (resource/step r e))]
                    {:r after :log (conj log {:event e :before r :after after})}))
                {:r seed :log []}
                events)))

(def events
  [[:connected {}]
   [:intent-patch {:query-patch {:sort "owner"} :gesture-class :refinement}]
   [:url-changed {:page "2"}]])

(deftest resource-at-folds-from-the-seed
  (let [log (record-log seed events)]
    (testing "n=0 is the seed"
      (is (= seed (rc/resource-at log 0))))
    (testing "n=count is the final recorded state"
      (is (= (:after (last log)) (rc/resource-at log (count log)))))
    (testing "every step reproduces the recording — replay is faithful"
      (doseq [k (range 1 (inc (count log)))]
        (is (= (:after (nth log (dec k))) (rc/resource-at log k))
            (str "step " k))))))

(deftest resource-at-of-an-empty-log-is-nil
  (is (nil? (rc/resource-at [] 3))))
