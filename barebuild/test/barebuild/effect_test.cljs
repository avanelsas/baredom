(ns barebuild.effect-test
  "The effect vocabulary: what each constructor builds, that `tags` names exactly the constructors
   this namespace has, and that the executor performs exactly `tags`."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.effect :as effect]
            [barebuild.elements.server-resource.executor :as executor]))

(deftest tags-names-every-constructor-this-namespace-has
  (testing "a constructor left out of `tags` builds an effect no executor performs, which the
            handler test cannot catch because it compares `tags` against the handlers alone"
    (is (= effect/tags
           (into #{}
                 (comp (remove #{'tags}) (map (comp keyword name)))
                 (keys (ns-publics 'barebuild.effect)))))))

(deftest the-executor-performs-exactly-the-vocabulary
  (testing "step decides and the executor performs, so the two vocabularies are one. An effect step
            learns to emit with no performer would be silently unperformed, and a performer for an
            effect step never emits is dead weight that reads as supported"
    (is (= effect/tags (set (keys (executor/performers (fn [_el _event]))))))))

(deftest each-constructor-builds-its-effect
  (testing "a read and a write carry the built request as their payload, unwrapped"
    (is (= [:fetch {:request/id "tasks:1"}] (effect/fetch {:request/id "tasks:1"})))
    (is (= [:write {:request/id "tasks:w1"}] (effect/write {:request/id "tasks:w1"}))))
  (testing "and everything naming a request names it the same way, so a performer reads one key"
    (is (= [:abort {:request/id "tasks:1"}] (effect/abort "tasks:1"))))
  (testing "the url projection carries the scope, the params and the history mode"
    (is (= [:url-write {:resource/id "tasks" :params {:sort "owner"} :mode :replace}]
           (effect/url-write "tasks" {:sort "owner"} :replace))))
  (testing "a routed intent names its target and carries the patch"
    (is (= [:route-intent {:resource/id "projects" :patch {:query-patch {:sort "owner"}}}]
           (effect/route-intent "projects" {:query-patch {:sort "owner"}}))))
  (testing "a notification carries the projection and the resource it was projected from"
    (is (= [:notify-consumers {:resource/id "tasks" :view {:pending? false}}]
           (effect/notify-consumers "tasks" {:pending? false})))))

(deftest a-diagnostic-carries-detail-only-when-there-is-detail
  (testing "absent and nil differ here, the performer branching on the key being there at all"
    (is (= [:diagnostic {:code :stale-response}] (effect/diagnostic :stale-response)))
    (is (= [:diagnostic {:code :unroutable-intent :detail {:resource/id "ghost"}}]
           (effect/diagnostic :unroutable-intent {:resource/id "ghost"})))))
