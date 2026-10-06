(ns baremirror.unit-test
  (:require [baremirror.unit :as unit]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def ^:private row
  [:li
   [:x-checkbox {:checked :done? :on {"x-checkbox-change-request" :toggle}}]
   [:span :text]
   [:x-button {:on {"press" :remove}} "Remove"]])

(def ^:private gen-fixed
  (gen/one-of [gen/string-alphanumeric gen/small-integer gen/boolean]))

(def ^:private gen-hole
  (gen/elements [:a :b :c]))

(def ^:private gen-attrs
  (gen/let [values (gen/map (gen/elements [:x :y :z]) (gen/one-of [gen-fixed gen-hole]))
            on     (gen/elements [nil {"press" :go}])]
    (cond-> values on (assoc :on on))))

(defn- gen-element
  "A generator of a shell whose children come from `gen-child`, or are one text hole."
  [gen-child]
  (gen/let [tag      (gen/elements [:div :span :li])
            attrs    gen-attrs
            children (gen/one-of [(gen/fmap vector gen-hole) (gen/vector gen-child 0 3)])]
    (into [tag attrs] children)))

(def ^:private gen-shell
  (gen-element (gen/recursive-gen gen-element gen/string-alphanumeric)))

(defn- child-shells [shell]
  (filter vector? (nth (unit/pieces shell) 2)))

(defn- elements
  "Every shell in `shell`, itself included."
  [shell]
  (tree-seq (constantly true) child-shells shell))

(defn- part-of [shell]
  (get (second (unit/pieces shell)) (keyword unit/attr-part)))

(defn- part-names [shell]
  (into #{} (keep part-of) (elements shell)))

(deftest pieces-of-a-shell
  (testing "a shell with attributes"
    (is (= [:li {:class "row"} ["text" [:b]]]
           (unit/pieces [:li {:class "row"} "text" [:b]]))))
  (testing "a shell with no attributes"
    (is (= [:li nil ["text" [:b]]]
           (unit/pieces [:li "text" [:b]]))))
  (testing "a shell with attributes and no children"
    (is (= [:li {:class "row"} nil] (unit/pieces [:li {:class "row"}]))))
  (testing "a shell of a tag alone"
    (is (= [:li nil nil] (unit/pieces [:li])))))

(deftest fixed-shells
  (testing "text, a number, a boolean and nil are fixed attribute values"
    (is (true? (unit/fixed? [:x-button {:variant "primary" :disabled true :tabindex 0 :title nil}]))))
  (testing "a shell, text and a number are fixed children"
    (is (true? (unit/fixed? [:li "Tasks: " 3 [:b "open"]]))))
  (testing "a keyword as an attribute value is not fixed"
    (is (false? (unit/fixed? [:x-button {:variant :primary}]))))
  (testing "a function as an attribute value is not fixed"
    (is (false? (unit/fixed? [:x-button {:variant identity}]))))
  (testing "a keyword as a child is not fixed"
    (is (false? (unit/fixed? [:span :text]))))
  (testing "a sequence as a child is not fixed"
    (is (false? (unit/fixed? [:ul (map (partial vector :li) ["a" "b"])]))))
  (testing "a shell with an :on entry is not fixed"
    (is (false? (unit/fixed? [:x-button {:on {"press" :remove}}]))))
  (testing "a hole in a child shell leaves the shell around it fixed"
    (is (true? (unit/fixed? [:li [:span :text]])))))

(deftest split-of-a-row
  (is (= {:shell  [:li
                   [:x-checkbox {:data-x-part "row.0"}]
                   [:span {:data-x-part "row.1"}]
                   [:x-button {:data-x-part "row.2"} "Remove"]]
          :holes  [{:part "row.0" :attrs {:checked :done?}}
                   {:part "row.1" :text :text}]
          :events {["x-checkbox-change-request" "row.0"] :toggle
                   ["press" "row.2"]                     :remove}}
         (unit/split :row row))))

(deftest split-of-small-cases
  (testing "a shell with no hole is its own fixed shell"
    (is (= {:shell [:li {:class "row"} "text" [:b "bold"]] :holes [] :events {}}
           (unit/split :row [:li {:class "row"} "text" [:b "bold"]]))))
  (testing "a part name the developer gave is kept, and the count does not move"
    (is (= {:shell  [:li [:b {:data-x-part "title"}] [:span {:data-x-part "row.0"}]]
            :holes  [{:part "title" :text :title} {:part "row.0" :text :text}]
            :events {}}
           (unit/split :row [:li [:b {:data-x-part "title"} :title] [:span :text]]))))
  (testing "an element is named before the elements in it"
    (is (= [:li {:data-x-part "row.0"} [:span {:data-x-part "row.1"}]]
           (:shell (unit/split :row [:li {:class :kind} [:span :text]])))))
  (testing "a fixed attribute stays beside a hole"
    (is (= [:x-button {:size "sm" :data-x-part "row.0"}]
           (:shell (unit/split :row [:x-button {:size "sm" :variant :kind}])))))
  (testing "a name of a unit may be a string"
    (is (= [:span {:data-x-part "row.0"}] (:shell (unit/split "row" [:span :text]))))))

(deftest split-refuses-a-text-hole-beside-other-children
  (is (thrown? #?(:clj clojure.lang.ExceptionInfo :cljs ExceptionInfo)
               (unit/split :row [:span "Task: " :text]))))

(deftest split-refuses-a-child-that-is-no-shell-text-number-or-hole
  (is (thrown? #?(:clj clojure.lang.ExceptionInfo :cljs ExceptionInfo)
               (unit/split :row [:ul (map (partial vector :li) ["a" "b"])]))))

(deftest writes-of-an-item
  (let [unit (unit/split :row row)]
    (testing "each hole gives the value of the item"
      (is (= {"row.0" {:attrs {:checked true}}
              "row.1" {:text "Buy milk"}}
             (unit/writes unit {:done? true :text "Buy milk"}))))
    (testing "a hole that is a function is called with the item"
      (is (= {"row.0" {:text 2}}
             (unit/writes (unit/split :row [:span count]) [:a :b]))))
    (testing "a unit with no hole has no writes"
      (is (= {} (unit/writes (unit/split :row [:li "text"]) {}))))))

(defspec the-shell-of-a-unit-is-fixed 300
  (prop/for-all [shell gen-shell]
    (every? unit/fixed? (elements (:shell (unit/split :u shell))))))

(defspec the-shell-of-a-unit-keeps-the-tags-of-the-shell-given 300
  (prop/for-all [shell gen-shell]
    (= (map first (elements shell))
       (map first (elements (:shell (unit/split :u shell)))))))

(defspec the-holes-and-events-of-a-unit-name-parts-of-its-shell 300
  (prop/for-all [shell gen-shell]
    (let [{fixed :shell :keys [holes events]} (unit/split :u shell)
          hole-parts                           (map :part holes)]
      (and (every? (part-names fixed) (concat hole-parts (map second (keys events))))
           (= (count hole-parts) (count (set hole-parts)))))))

(defspec the-writes-of-a-unit-name-the-parts-of-its-holes 300
  (prop/for-all [shell gen-shell]
    (let [unit (unit/split :u shell)]
      (= (set (map :part (:holes unit)))
         (set (keys (unit/writes unit {:a 1 :b 2 :c 3})))))))
