(ns demo.x-search-field-consumer-model-test
  "translate-search-gesture / project-search-value: the refinement gesture out, and the
   echoed term read back. Query keys are keywords — wire/parse-envelope runs the echo through
   canonicalize-query, and the merge in resource/step canonicalizes intent-patches too."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-search-field-consumer.model :as model]))

(deftest translate-produces-a-refinement-query-patch
  (testing "a term becomes a :search query-patch classed as a refinement"
    (is (= {:query-patch {:search "alice"} :gesture-class :refinement}
           (model/translate-search-gesture "alice"))))
  (testing "an empty term is carried as-is (canonicalize-query drops it downstream, so the
            clear gesture removes the key and restores the full set)"
    (is (= {:query-patch {:search ""} :gesture-class :refinement}
           (model/translate-search-gesture "")))))

(deftest project-reads-the-echoed-term
  (testing "the term comes from the accepted response's canonicalized (keyword-keyed) :query"
    (is (= "alice"
           (model/project-search-value {:query {:search "alice"} :value []}))))
  (testing "no search echoed -> nil (the field reflects an empty filter)"
    (is (nil? (model/project-search-value {:query {} :value []})))))
