(ns baredom.components.x-command-palette.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-command-palette.model :as model]))

(deftest parse-bool-attr-test
  (testing "parse-bool-attr"
    (is (= true  (model/parse-bool-attr "")))
    (is (= true  (model/parse-bool-attr "true")))
    (is (= false (model/parse-bool-attr "false")))
    (is (= false (model/parse-bool-attr nil)))))

(deftest parse-bool-default-true-test
  (testing "parse-bool-default-true"
    (is (= true  (model/parse-bool-default-true nil)))
    (is (= true  (model/parse-bool-default-true "")))
    (is (= true  (model/parse-bool-default-true "true")))
    (is (= false (model/parse-bool-default-true "false")))))

(deftest normalize-str-test
  (testing "normalize-str"
    (is (= "hello" (model/normalize-str "  hello  ")))
    (is (= nil     (model/normalize-str "")))
    (is (= nil     (model/normalize-str "   ")))
    (is (= nil     (model/normalize-str nil)))))

(deftest normalize-items-test
  (testing "empty and nil inputs"
    (is (= [] (model/normalize-items nil)))
    (is (= [] (model/normalize-items #js []))))

  (testing "basic item normalization"
    (let [items (model/normalize-items
                 #js [#js {:id "1" :label "Open file" :group "Files"}
                      #js {:id "2" :label "Save" :disabled true}])]
      (is (= 2 (count items)))
      (is (= "1" (:id (first items))))
      (is (= "Open file" (:label (first items))))
      (is (= "Files" (:group (first items))))
      (is (= false (:disabled? (first items))))
      (is (= true (:disabled? (second items))))
      (is (string? (:search-str (first items))))))

  (testing "search-str is lowercase and includes label"
    (let [items (model/normalize-items
                 #js [#js {:id "1" :label "GitHub Actions" :keywords #js ["ci" "cd"]}])]
      (is (clojure.string/includes? (:search-str (first items)) "github actions"))
      (is (clojure.string/includes? (:search-str (first items)) "ci")))))

(deftest filter-items-test
  (let [items (model/normalize-items
               #js [#js {:id "1" :label "Open file"}
                    #js {:id "2" :label "Save document"}
                    #js {:id "3" :label "Close tab"}])]
    (testing "empty query returns all"
      (is (= 3 (count (:visible (model/filter-items items ""))))))

    (testing "single token filter"
      (let [{:keys [visible]} (model/filter-items items "open")]
        (is (= 1 (count visible)))
        (is (= "1" (:id (first visible))))))

    (testing "multi-token filter (AND)"
      (let [{:keys [visible]} (model/filter-items items "save doc")]
        (is (= 1 (count visible)))
        (is (= "2" (:id (first visible))))))

    (testing "no match returns empty"
      (let [{:keys [visible]} (model/filter-items items "zzznomatch")]
        (is (= 0 (count visible)))))))

(deftest next-prev-active-idx-test
  (let [items (model/normalize-items
               #js [#js {:id "a" :label "Alpha"}
                    #js {:id "b" :label "Beta" :disabled true}
                    #js {:id "c" :label "Gamma"}])]
    (testing "next skips disabled"
      (is (= 2 (model/next-active-idx items 0))))

    (testing "prev skips disabled"
      (is (= 0 (model/prev-active-idx items 2))))

    (testing "wraps around"
      (is (= 0 (model/next-active-idx items 2))))))

(deftest normalize-test
  (testing "defaults"
    (let [m (model/normalize {})]
      (is (= false (:open? m)))
      (is (= true (:modal? m)))
      (is (= true (:dismissible? m)))
      (is (= false (:disabled? m)))
      (is (= true (:scrim? m)))
      (is (= true (:close-on-escape? m)))
      (is (= model/default-placeholder (:placeholder m)))
      (is (= model/default-empty-text (:empty-text m)))))

  (testing "open-present? reflects as open?"
    (let [m (model/normalize {:open-present? true})]
      (is (= true (:open? m)))))

  (testing "no-scrim suppresses scrim"
    (let [m (model/normalize {:no-scrim-raw ""})]
      (is (= false (:scrim? m)))))

  (testing "close-on-escape can be disabled"
    (let [m (model/normalize {:close-on-escape-raw "false"})]
      (is (= false (:close-on-escape? m))))))
