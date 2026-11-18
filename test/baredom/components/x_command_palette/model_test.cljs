(ns baredom.components.x-command-palette.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [clojure.string :as str]
            [baredom.components.x-command-palette.model :as model]))

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
      (is (str/includes? (:search-str (first items)) "github actions"))
      (is (str/includes? (:search-str (first items)) "ci")))))

(deftest filter-items-test
  (let [items (model/normalize-items
               #js [#js {:id "1" :label "Open file"}
                    #js {:id "2" :label "Save document"}
                    #js {:id "3" :label "Close tab"}])]
    (testing "empty query returns all"
      (is (= 3 (count (model/filter-items items "" model/filter-default)))))

    (testing "single token filter"
      (let [visible (model/filter-items items "open" model/filter-default)]
        (is (= 1 (count visible)))
        (is (= "1" (:id (first visible))))))

    (testing "multi-token filter (AND)"
      (let [visible (model/filter-items items "save doc" model/filter-default)]
        (is (= 1 (count visible)))
        (is (= "2" (:id (first visible))))))

    (testing "no match returns empty"
      (let [visible (model/filter-items items "zzznomatch" model/filter-default)]
        (is (= 0 (count visible)))))

    (testing "filter mode none returns all items in order"
      (let [visible (model/filter-items items "zzznomatch" model/filter-none)]
        (is (= ["1" "2" "3"] (mapv :id visible)))))))

(deftest normalize-filter-test
  (is (= "none" (model/normalize-filter "none")))
  (is (= "default" (model/normalize-filter "default")))
  (is (= "default" (model/normalize-filter "fuzzy")))
  (is (= "default" (model/normalize-filter nil))))

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
      (is (= model/default-empty-text (:empty-text m)))
      (is (= model/filter-default (:filter m)))))

  (testing "open-present? reflects as open?"
    (let [m (model/normalize {:open-present? true})]
      (is (= true (:open? m)))))

  (testing "no-scrim suppresses scrim"
    (let [m (model/normalize {:no-scrim-raw ""})]
      (is (= false (:scrim? m)))))

  (testing "close-on-escape can be disabled"
    (let [m (model/normalize {:close-on-escape-raw "false"})]
      (is (= false (:close-on-escape? m))))))

;; ── shown-list ──────────────────────────────────────────────────────────────
(def ^:private commands
  (model/normalize-items
   #js [#js {:id "new"  :label "New file"  :group "File"}
        #js {:id "open" :label "Open file" :group "File"}
        #js {:id "copy" :label "Copy"      :group "Edit" :disabled true}
        #js {:id "help" :label "Help"}]))

(deftest shown-list-gives-the-headers-and-the-items-in-order-test
  (let [{:keys [entries active-id]} (model/shown-list commands {:query "" :active-idx 1 :filter-mode model/filter-default})]
    (is (= ["group-new|0" "item-new|0" "item-open|0" "group-copy|0" "item-copy|0" "item-help|0"]
           (mapv :key entries)))
    (is (= {:key "group-new|0" :text "File"} (first entries)))
    (is (= {:key "item-open|0" :id "x-cp-item-1" :data-id "open" :position "1" :text "Open file"
            :shortcut nil :shortcut-id nil :aria-disabled nil :aria-selected "true"}
           (nth entries 2)))
    (is (= "true" (:aria-disabled (nth entries 4))))
    (is (= "x-cp-item-1" active-id))))

(deftest shown-list-keeps-the-key-of-an-item-while-the-query-changes-test
  (let [{:keys [entries]} (model/shown-list commands {:query "copy" :active-idx 0 :filter-mode model/filter-default})]
    (is (= ["group-copy|0" "item-copy|0"] (mapv :key entries)) "the key is made of the id")
    (is (= "0" (:position (second entries))) "the position is among the visible ones")))

(deftest shown-list-of-no-match-is-empty-test
  (is (= {:entries [] :active-id nil} (model/shown-list commands {:query "zzz" :active-idx 0 :filter-mode model/filter-default}))))


(deftest filter-items-uses-the-default-mode-for-nil-test
  (let [items (model/normalize-items #js [#js {:id "1" :label "Open"} #js {:id "2" :label "Save"}])]
    (is (= ["1"] (mapv :id (model/filter-items items "open" nil))))))

;; ── shortcut ────────────────────────────────────────────────────────────────

(deftest an-item-keeps-its-shortcut-as-text-test
  (let [[save other blank] (model/normalize-items
                            #js [#js {:id "save" :label "Save" :shortcut " Mod+S "}
                                 #js {:id "x" :label "X" :shortcut "abracadabra"}
                                 #js {:id "y" :label "Y" :shortcut "  "}])]
    (is (= "Mod+S" (:shortcut save)))
    (is (= "abracadabra" (:shortcut other)))
    (is (nil? (:shortcut blank)))))

(deftest the-shortcut-is-not-searched-test
  (let [items (model/normalize-items #js [#js {:id "save" :label "Save" :shortcut "Mod+S"}])]
    (is (empty? (model/filter-items items "mod" model/filter-default)))))

(deftest a-shown-item-names-the-id-of-its-shortcut-test
  (let [items   (model/normalize-items #js [#js {:id "save" :label "Save" :shortcut "Mod+S"}
                                            #js {:id "open" :label "Open"}])
        entries (:entries (model/shown-list items {:query "" :active-idx 0
                                                   :filter-mode model/filter-default}))]
    (is (= ["Mod+S" "x-cp-item-0-shortcut"] ((juxt :shortcut :shortcut-id) (first entries))))
    (is (= [nil nil] ((juxt :shortcut :shortcut-id) (second entries))))))
