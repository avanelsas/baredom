(ns baremirror.alpha.template-test
  (:require [baremirror.alpha.template :as template]
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
  "A generator of a template whose children come from `gen-child`, or are one text hole."
  [gen-child]
  (gen/let [tag      (gen/elements [:div :span :li])
            attrs    gen-attrs
            children (gen/one-of [(gen/fmap vector gen-hole) (gen/vector gen-child 0 3)])]
    (into [tag attrs] children)))

(def ^:private gen-template
  (gen-element (gen/recursive-gen gen-element gen/string-alphanumeric)))

(defn- child-templates [given]
  (filter vector? (nth (template/pieces given) 2)))

(defn- elements
  "Every template in `given`, itself included."
  [given]
  (tree-seq (constantly true) child-templates given))

(defn- part-of [given]
  (get (second (template/pieces given)) (keyword template/attr-part)))

(defn- part-names [given]
  (into #{} (keep part-of) (elements given)))

(deftest pieces-of-a-template
  (testing "a template with attributes"
    (is (= [:li {:class "row"} ["text" [:b]]]
           (template/pieces [:li {:class "row"} "text" [:b]]))))
  (testing "a template with no attributes"
    (is (= [:li nil ["text" [:b]]]
           (template/pieces [:li "text" [:b]]))))
  (testing "a template with attributes and no children"
    (is (= [:li {:class "row"} nil] (template/pieces [:li {:class "row"}]))))
  (testing "a template of a tag alone"
    (is (= [:li nil nil] (template/pieces [:li])))))

(deftest fixed-templates
  (testing "text, a number, a boolean and nil are fixed attribute values"
    (is (true? (template/fixed? [:x-button {:variant "primary" :disabled true :tabindex 0
                                            :title nil}]))))
  (testing "a template, text and a number are fixed children"
    (is (true? (template/fixed? [:li "Tasks: " 3 [:b "open"]]))))
  (testing "a keyword as an attribute value is not fixed"
    (is (false? (template/fixed? [:x-button {:variant :primary}]))))
  (testing "a function as an attribute value is not fixed"
    (is (false? (template/fixed? [:x-button {:variant identity}]))))
  (testing "a keyword as a child is not fixed"
    (is (false? (template/fixed? [:span :text]))))
  (testing "a sequence as a child is not fixed"
    (is (false? (template/fixed? [:ul (map (partial vector :li) ["a" "b"])]))))
  (testing "a template with an :on entry is not fixed"
    (is (false? (template/fixed? [:x-button {:on {"press" :remove}}]))))
  (testing "a template with a :props entry is not fixed"
    (is (false? (template/fixed? [:x-command-palette {:props {:items :items}}]))))
  (testing "a hole in a child template leaves the template around it fixed"
    (is (true? (template/fixed? [:li [:span :text]])))))

(deftest split-of-a-row
  (is (= {:fixed  [:li
                   [:x-checkbox {:data-x-part "row.0"}]
                   [:span {:data-x-part "row.1"}]
                   [:x-button {:data-x-part "row.2"} "Remove"]]
          :holes  [{:part "row.0" :attrs {:checked :done?}}
                   {:part "row.1" :text :text}]
          :events {["x-checkbox-change-request" "row.0"] :toggle
                   ["press" "row.2"]                     :remove}}
         (template/split :row row))))

(deftest split-of-small-cases
  (testing "a template with no hole is its own fixed template"
    (is (= {:fixed [:li {:class "row"} "text" [:b "bold"]] :holes [] :events {}}
           (template/split :row [:li {:class "row"} "text" [:b "bold"]]))))
  (testing "a part name the developer gave is kept, and the count does not move"
    (is (= {:fixed  [:li [:b {:data-x-part "title"}] [:span {:data-x-part "row.0"}]]
            :holes  [{:part "title" :text :title} {:part "row.0" :text :text}]
            :events {}}
           (template/split :row [:li [:b {:data-x-part "title"} :title] [:span :text]]))))
  (testing "an element is named before the elements in it"
    (is (= [:li {:data-x-part "row.0"} [:span {:data-x-part "row.1"}]]
           (:fixed (template/split :row [:li {:class :kind} [:span :text]])))))
  (testing "a fixed attribute stays beside a hole"
    (is (= [:x-button {:size "sm" :data-x-part "row.0"}]
           (:fixed (template/split :row [:x-button {:size "sm" :variant :kind}])))))
  (testing "the name may be a string"
    (is (= [:span {:data-x-part "row.0"}] (:fixed (template/split "row" [:span :text]))))))

(deftest split-of-properties
  (is (= {:fixed  [:li [:x-command-palette {:open true :data-x-part "row.0"}]]
          :holes  [{:part "row.0" :props {:items :items}}]
          :events {}}
         (template/split :row [:li [:x-command-palette {:open true :props {:items :items}}]]))))

(deftest split-refuses-a-property-that-is-not-a-hole
  (is (thrown-with-msg? #?(:clj clojure.lang.ExceptionInfo :cljs ExceptionInfo) #"is a hole"
                        (template/split :row [:x-command-palette {:props {:items []}}]))))

(deftest split-refuses-a-text-hole-beside-other-children
  (is (thrown? #?(:clj clojure.lang.ExceptionInfo :cljs ExceptionInfo)
               (template/split :row [:span "Task: " :text]))))

(deftest split-refuses-a-child-that-is-no-template-text-number-or-hole
  (is (thrown? #?(:clj clojure.lang.ExceptionInfo :cljs ExceptionInfo)
               (template/split :row [:ul (map (partial vector :li) ["a" "b"])]))))

(deftest writes-of-an-item
  (let [split-row (template/split :row row)]
    (testing "each hole gives the value of the item"
      (is (= {"row.0" {:attrs {:checked true}}
              "row.1" {:text "Buy milk"}}
             (template/writes split-row {:done? true :text "Buy milk"}))))
    (testing "a hole that is a function is called with the item"
      (is (= {"row.0" {:text 2}}
             (template/writes (template/split :row [:span count]) [:a :b]))))
    (testing "a property hole gives the value of the item"
      (is (= {"row.0" {:props {:items ["a"]}}}
             (template/writes (template/split :row [:x-command-palette {:props {:items :items}}])
                              {:items ["a"]}))))
    (testing "a template with no hole has no writes"
      (is (= {} (template/writes (template/split :row [:li "text"]) {}))))))

(defspec split-gives-a-fixed-template 300
  (prop/for-all [given gen-template]
    (every? template/fixed? (elements (:fixed (template/split :u given))))))

(defspec split-keeps-the-tags-of-the-template 300
  (prop/for-all [given gen-template]
    (= (map first (elements given))
       (map first (elements (:fixed (template/split :u given)))))))

(defspec the-holes-and-events-name-parts-of-the-fixed-template 300
  (prop/for-all [given gen-template]
    (let [{:keys [fixed holes events]} (template/split :u given)
          hole-parts                   (map :part holes)]
      (and (every? (part-names fixed) (concat hole-parts (map second (keys events))))
           (= (count hole-parts) (count (set hole-parts)))))))

(defspec the-writes-name-the-parts-of-the-holes 300
  (prop/for-all [given gen-template]
    (let [split-given (template/split :u given)]
      (= (set (map :part (:holes split-given)))
         (set (keys (template/writes split-given {:a 1 :b 2 :c 3})))))))

(def ^:private tab-row
  [:li
   [:x-tab {:data-x-part "tab" :selected :on? :value :id}]
   [:x-field {:data-x-part "field" :error :problem :label :name}]
   [:span {:data-x-part "note" :hidden :quiet?} :text]])

(def ^:private writes
  {"x-tabs" [{:target "x-tab" :attributes ["selected"]} {:target "any" :attributes ["hidden"]}]
   "x-form" [{:target "any" :attributes ["error"]}]})

(deftest shared-holes-names-each-hole-a-writer-may-write
  (is (= [{:part "tab" :tag "x-tab" :attr "selected" :writer "x-tabs" :target "x-tab"}
          {:part "field" :tag "x-field" :attr "error" :writer "x-form" :target "any"}
          {:part "note" :tag "span" :attr "hidden" :writer "x-tabs" :target "any"}]
         (template/shared-holes (template/split :row tab-row) writes))))

(deftest shared-holes-skips-another-tag-another-attribute-and-text
  (is (= [] (template/shared-holes (template/split :row tab-row)
                                   {"x-radio" [{:target "x-radio" :attributes ["checked" "value"]}]
                                    "x-table" [{:target "any" :attributes ["text" "title"]}]}))))

(deftest shared-holes-takes-any-attributes-as-every-attribute
  (is (= #{["tab" "selected"] ["tab" "value"] ["field" "error"] ["field" "label"] ["note" "hidden"]}
         (into #{} (map (juxt :part :attr))
               (template/shared-holes (template/split :row tab-row)
                                      {"x-i18n-provider" [{:target "any" :attributes "any"}]})))))

(deftest shared-holes-gives-an-entry-for-each-writer-in-writer-order
  (is (= ["x-form" "x-other"]
         (map :writer (template/shared-holes
                       (template/split :row [:x-field {:error :problem}])
                       {"x-other" [{:target "x-field" :attributes ["error"]}]
                        "x-form"  [{:target "any" :attributes ["error"]}]})))))

(deftest shared-holes-gives-one-entry-for-a-writer-with-the-named-target-first
  (is (= [{:part "tab" :tag "x-tab" :attr "hidden" :writer "x-tabs" :target "x-tab"}]
         (template/shared-holes (template/split :row [:x-tab {:data-x-part "tab" :hidden :off?}])
                                {"x-tabs" [{:target "any" :attributes ["hidden"]}
                                           {:target "x-tab" :attributes ["hidden"]}]}))))

(deftest shared-holes-takes-the-keyword-any-as-the-text-any
  (is (= [{:part "f" :tag "x-field" :attr "error" :writer "x-form" :target "any"}]
         (template/shared-holes (template/split :row [:x-field {:data-x-part "f" :error :problem}])
                                {"x-form" [{:target :any :attributes :any}]}))))

(deftest shared-holes-compares-tags-and-attributes-in-lower-case
  (is (= [["x-tab" "selected"]]
         (map (juxt :tag :attr)
              (template/shared-holes (template/split :row ["X-TAB" {"data-x-part" "t" "Selected" :on?}])
                                     {"x-tabs" [{:target "x-tab" :attributes ["selected"]}]})))))
