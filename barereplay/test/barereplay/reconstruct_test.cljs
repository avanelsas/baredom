(ns barereplay.reconstruct-test
  (:require [cljs.test :refer-macros [deftest is]]
            [barebuild.resource :as resource]
            [barereplay.reconstruct :as rc]))

(defn- record-log [el seed events]
  (:log (reduce (fn [{:keys [r log]} e]
                  (let [after (:resource (resource/step r e))]
                    {:r after :log (conj log {:el el :event e :before r :after after})}))
                {:r seed :log []}
                events)))

(def tasks-seed
  {:resource/id "tasks" :endpoint "/api/tasks" :last-accepted nil})

(def projects-seed
  {:resource/id "projects" :endpoint "/api/projects" :last-accepted nil})

(def tasks-events
  [[:connected {}]
   [:intent-patch {:query-patch {:sort "owner"} :gesture-class :refinement}]])

(def projects-events
  [[:connected {}]])

(deftest resources-at-empty-log
  (is (= {} (rc/resources-at [] 3))))

(deftest resources-at-single-resource
  (let [log (record-log :tasks-el tasks-seed tasks-events)]
    (is (= tasks-seed (get-in (rc/resources-at log 0) ["tasks" :value])))
    (is (= (:after (last log)) (get-in (rc/resources-at log (count log)) ["tasks" :value])))
    (is (= :tasks-el (get-in (rc/resources-at log 1) ["tasks" :el])))
    (doseq [k (range 1 (inc (count log)))]
      (is (= (:after (nth log (dec k))) (get-in (rc/resources-at log k) ["tasks" :value]))))))

(deftest resources-at-folds-each-resource-independently
  (let [t       (record-log :tasks-el tasks-seed tasks-events)
        p       (record-log :projects-el projects-seed projects-events)
        log     [(nth t 0) (nth p 0) (nth t 1)]
        full    (rc/resources-at log (count log))
        partial (rc/resources-at log 2)]
    (is (= (:after (last t)) (get-in full ["tasks" :value])))
    (is (= (:after (last p)) (get-in full ["projects" :value])))
    (is (= :tasks-el (get-in full ["tasks" :el])))
    (is (= :projects-el (get-in full ["projects" :el])))
    (is (= (:after (nth t 0)) (get-in partial ["tasks" :value])))
    (is (= (:after (nth p 0)) (get-in partial ["projects" :value])))
    (is (not= (get-in full ["tasks" :value]) (get-in full ["projects" :value])))))
