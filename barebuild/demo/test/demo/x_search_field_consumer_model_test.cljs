(ns demo.x-search-field-consumer-model-test
  "translate-search-gesture and project-search-value: the refinement out, the echoed term back.
   Query keys are keywords, canonicalized on both sides."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-search-field-consumer.model :as model]))

(deftest translate-produces-a-refinement-query-patch
  (testing "a term becomes a :search query-patch classed as a refinement"
    (is (= {:query-patch {:search "alice"} :gesture-class :refinement}
           (model/translate-search-gesture "alice"))))
  (testing "an empty term is carried as-is, canonicalize-query drops it downstream"
    (is (= {:query-patch {:search ""} :gesture-class :refinement}
           (model/translate-search-gesture "")))))

(deftest project-reads-the-echoed-term
  (testing "the term comes from the accepted response's :query"
    (is (= "alice"
           (model/project-search-value {:query {:search "alice"} :value []}))))
  (testing "no search echoed, no term"
    (is (nil? (model/project-search-value {:query {} :value []})))))
