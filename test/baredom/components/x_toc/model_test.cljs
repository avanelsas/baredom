(ns baredom.components.x-toc.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-toc.model :as model]))

(deftest parse-offset-test
  (testing "a percentage gives a percent offset"
    (is (= {:unit :percent :value 40} (model/parse-offset "40%"))))
  (testing "a number with or without px gives a px offset"
    (is (= {:unit :px :value 80} (model/parse-offset "80px")))
    (is (= {:unit :px :value 80} (model/parse-offset " 80 "))))
  (testing "a decimal is kept"
    (is (= {:unit :px :value 12.5} (model/parse-offset "12.5px"))))
  (testing "invalid values give the default 25%"
    (is (= {:unit :percent :value 25} (model/parse-offset nil)))
    (is (= {:unit :percent :value 25} (model/parse-offset "")))
    (is (= {:unit :percent :value 25} (model/parse-offset "2rem")))
    (is (= {:unit :percent :value 25} (model/parse-offset "-10px")))
    (is (= {:unit :percent :value 25} (model/parse-offset "120%")))))

(deftest offset-px-test
  (is (= 200 (model/offset-px {:unit :percent :value 25} 800)))
  (is (= 80 (model/offset-px {:unit :px :value 80} 800))))

(deftest slug-test
  (testing "the slug is lower case words joined by a dash"
    (is (= "source-code" (model/slug "Source code")))
    (is (= "what-is-a-web-component" (model/slug "What is a web-component?"))))
  (testing "letters outside ASCII stay"
    (is (= "café-über" (model/slug "Café Über"))))
  (testing "text with no letters or digits gives section"
    (is (= "section" (model/slug "?!")))
    (is (= "section" (model/slug nil)))))

(deftest heading-level-test
  (is (= 1 (model/heading-level "H1")))
  (is (= 6 (model/heading-level "h6")))
  (is (nil? (model/heading-level "DIV")))
  (is (nil? (model/heading-level nil))))

(defn- ids [headings taken]
  (mapv (juxt :id :new-id?) (model/identify headings taken)))

(defn- entries [headings]
  (model/entries (model/identify headings #{})))

(deftest entries-ids-test
  (testing "an own id stays and needs no write"
    (is (= [["intro" false]] (ids [{:id "intro" :text "Hello" :tag "H2"}] #{"intro"}))))
  (testing "a missing id becomes a slug of the text"
    (is (= [["source-code" true]] (ids [{:text "Source code" :tag "H2"}] #{}))))
  (testing "a slug in use gets a number"
    (is (= [["faq-2" true]] (ids [{:text "FAQ" :tag "H2"}] #{"faq"}))))
  (testing "equal texts get distinct ids"
    (is (= ["faq" "faq-2" "faq-3"]
           (map first (ids (repeat 3 {:text "FAQ" :tag "H2"}) #{})))))
  (testing "a heading with empty text gets no id"
    (is (= [["b" true]] (ids [{:text "  " :tag "H2"} {:text "B" :tag "H2"}] #{})))))

(deftest id-writes-test
  (testing "only headings without an id are written, at their place in the headings"
    (is (= [[1 "b"]]
           (model/id-writes (model/identify [{:id "a" :text "A" :tag "H2"} {:text "B" :tag "H2"}]
                                            #{"a"}))))))

(deftest entries-test
  (testing "an entry holds its id, text, level and index"
    (is (= [{:id "a" :text "A" :level 1 :index 0}
            {:id "b" :text "B" :level 2 :index 1}]
           (entries [{:id "a" :text "A" :tag "H2"} {:text "" :tag "H2"}
                     {:id "b" :text "B" :tag "H3"}]))))
  (testing "a list of h1 only is at level 1"
    (is (= [1 1] (map :level (entries [{:text "A" :tag "H1"} {:text "B" :tag "H1"}])))))
  (testing "an element that is not a heading is level 1"
    (is (= [1 1 2] (map :level (entries [{:text "A" :tag "P"} {:text "B" :tag "H2"}
                                         {:text "C" :tag "H3"}])))))
  (testing "text is trimmed and its whitespace collapsed"
    (is (= "Source code" (:text (first (entries [{:text "  Source \n code " :tag "H2"}]))))))
  (testing "an entry whose id an earlier entry has is left out"
    (is (= [["a" 0] ["b" 1]]
           (map (juxt :id :index)
                (entries [{:id "a" :text "A" :tag "H2"} {:id "a" :text "A2" :tag "H2"}
                          {:id "b" :text "B" :tag "H2"}]))))))

(deftest active-index-test
  (testing "no entries give nil"
    (is (nil? (model/active-index [] 100 false))))
  (testing "the last entry past the line is active"
    (is (= 1 (model/active-index [-300 50 400] 100 false))))
  (testing "an entry at the line is active"
    (is (= 2 (model/active-index [-300 50 100] 100 false))))
  (testing "before any entry reaches the line the first is active"
    (is (= 0 (model/active-index [300 600] 100 false))))
  (testing "at the bottom the last entry is active"
    (is (= 2 (model/active-index [-300 50 400] 100 true)))))

(deftest next-pin-test
  (let [pinned {:id "a" :settled? false}
        settled {:id "a" :settled? true}]
    (testing "a click pins its entry"
      (is (= pinned (model/next-pin nil [:click "a"]))))
    (testing "a scroll keeps a pin that has not settled"
      (is (= pinned (model/next-pin pinned [:scroll]))))
    (testing "the pin settles"
      (is (= settled (model/next-pin pinned [:settle]))))
    (testing "a scroll after the pin settled drops it"
      (is (nil? (model/next-pin settled [:scroll]))))
    (testing "without a pin, scroll and settle give no pin"
      (is (nil? (model/next-pin nil [:scroll])))
      (is (nil? (model/next-pin nil [:settle]))))))

(deftest normalize-test
  (let [m (model/normalize {})]
    (testing "an element with no attributes has the default values"
      (is (= {:for "" :selector model/default-selector :label "" :labelled? false
              :offset {:unit :percent :value 25} :update-hash? false}
             m))))
  (testing "a label names the list"
    (is (true? (:labelled? (model/normalize {:label-raw "On this page"})))))
  (testing "a blank selector gives the default"
    (is (= model/default-selector (:selector (model/normalize {:selector-raw " "}))))))

(deftest details-test
  (let [entry {:id "b" :text "B" :level 2 :index 1 :at 4 :new-id? true}]
    (testing "the change detail holds the entry and the previous id"
      (is (= {:id "b" :text "B" :level 2 :index 1 :previousId "a"}
             (model/change-detail entry "a"))))
    (testing "the navigate detail holds the entry"
      (is (= {:id "b" :text "B" :level 2 :index 1} (model/navigate-detail entry))))))

(deftest metadata-test
  (is (= "x-toc" model/tag-name))
  (is (true? (get-in model/property-api [:activeId :readonly])))
  (is (true? (get-in model/event-schema [model/event-navigate :cancelable])))
  (is (false? (get-in model/event-schema [model/event-change :cancelable]))))
