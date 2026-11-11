(ns baredom.components.x-tree.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-tree.model :as model]))

(def ^:private files
  [{:id "src" :label "src"
    :children [{:id "main" :label "main.cljs" :icon "file" :meta "4 KB"}
               {:id "util" :label "util.cljs"}]}
   {:id "readme" :label "README.md"}])

(deftest index-keys-the-items-by-id-in-tree-order
  (let [ix (model/index files)]
    (testing "each item is held without its children"
      (is (= {"src"    {:id "src" :label "src"}
              "main"   {:id "main" :label "main.cljs" :icon "file" :meta "4 KB"}
              "util"   {:id "util" :label "util.cljs"}
              "readme" {:id "readme" :label "README.md"}}
             (:item ix))))
    (testing "children holds the ids under each id, with nil for the top level"
      (is (= {nil ["src" "readme"] "src" ["main" "util"]} (:children ix))))
    (testing "parent holds the parent of each item below the top level"
      (is (= {"main" "src" "util" "src"} (:parent ix))))
    (testing "order holds every id in tree order"
      (is (= ["src" "main" "util" "readme"] (:order ix))))
    (testing "nothing is dropped"
      (is (= [] (:dropped ix))))))

(deftest index-of-no-items-has-an-empty-top-level
  (doseq [items [nil [] "not a list"]]
    (is (= {:item {} :children {nil []} :parent {} :position {} :order [] :dropped []}
           (model/index items)))))

(deftest index-gives-each-item-its-position-among-its-siblings
  (is (= {"src" 0 "main" 0 "util" 1 "readme" 1}
         (:position (model/index files)))))

(deftest index-drops-an-item-with-no-id-or-label-with-its-children
  (let [ix (model/index [{:label "no id" :children [{:id "a" :label "a"}]}
                         {:id "" :label "empty id"}
                         {:id 7 :label "number id"}
                         {:id "b"}
                         {:id "c" :label ""}
                         {:id "d" :label "d"}])]
    (testing "only the valid item stays"
      (is (= ["d"] (:order ix))))
    (testing "each dropped item is listed with its reason, its children are not"
      (is (= [{:id nil :reason :missing-id}
              {:id "" :reason :missing-id}
              {:id 7 :reason :missing-id}
              {:id "b" :reason :missing-label}
              {:id "c" :reason :missing-label}]
             (:dropped ix))))))

(deftest index-drops-an-item-whose-id-an-earlier-item-has
  (let [ix (model/index [{:id "a" :label "first"
                          :children [{:id "b" :label "child"}]}
                         {:id "b" :label "second b" :children [{:id "c" :label "c"}]}
                         {:id "a" :label "second a"}])]
    (testing "the earlier item stays, in tree order across levels"
      (is (= ["a" "b"] (:order ix)))
      (is (= "first" (get-in ix [:item "a" :label])))
      (is (= "child" (get-in ix [:item "b" :label]))))
    (testing "the later item is dropped with its children"
      (is (not (contains? (:item ix) "c")))
      (is (= [{:id "b" :reason :duplicate} {:id "a" :reason :duplicate}] (:dropped ix))))))

(deftest index-does-not-count-a-dropped-item-as-earlier
  (let [ix (model/index [{:id "a"} {:id "a" :label "a"}])]
    (is (= ["a"] (:order ix)))
    (is (= [{:id "a" :reason :missing-label}] (:dropped ix)))))

(deftest index-gives-no-children-entry-to-an-item-whose-children-are-all-dropped
  (let [ix (model/index [{:id "a" :label "a" :children [{:id "x"}]}
                         {:id "b" :label "b" :children []}
                         {:id "c" :label "c" :children "not a list"}])]
    (is (= {nil ["a" "b" "c"]} (:children ix)))))

;; ── Kinds of item ─────────────────────────────────────────────────────────
(def ^:private kinds
  (model/index [{:id "dir" :label "dir" :children [{:id "file" :label "file"}]}
                {:id "lazy" :label "lazy" :lazy true}
                {:id "loaded" :label "loaded" :lazy true :children [{:id "x" :label "x"}]}
                {:id "empty" :label "empty" :children []}
                {:id "odd" :label "odd" :lazy "yes"}]))

(deftest branch-is-an-item-with-children-or-lazy
  (is (true? (model/branch? kinds "dir")))
  (is (true? (model/branch? kinds "lazy")))
  (is (true? (model/branch? kinds "loaded")))
  (is (false? (model/branch? kinds "file")))
  (is (false? (model/branch? kinds "empty")))
  (testing "lazy counts only when it is true"
    (is (false? (model/branch? kinds "odd")))))

(deftest end-item-is-an-item-with-no-children
  (is (true? (model/end-item? kinds "file")))
  (is (true? (model/end-item? kinds "lazy")))
  (is (false? (model/end-item? kinds "dir")))
  (is (false? (model/end-item? kinds "loaded"))))

;; ── Counted ids ───────────────────────────────────────────────────────────
(def ^:private tree
  (model/index [{:id "a" :label "a"
                 :children [{:id "a1" :label "a1"}
                            {:id "a2" :label "a2" :lazy true}]}
                {:id "b" :label "b"}]))

(defn- counted-selected [selection selected]
  (:selected (model/counted tree {:selection selection :selected selected})))

(deftest counted-expanded-keeps-the-branches
  (is (= #{"a" "a2"}
         (:expanded (model/counted tree {:expanded ["b" "a2" "nope" "a" "a1"]})))))

(deftest counted-selected-depends-on-selection
  (testing "none counts no id"
    (is (= #{} (counted-selected :none ["a1" "b"]))))
  (testing "single counts the first id in tree order"
    (is (= #{"a"} (counted-selected :single ["b" "nope" "a"]))))
  (testing "leaf counts the first leaf id in tree order"
    (is (= #{"a1"} (counted-selected :leaf ["b" "a" "a1"]))))
  (testing "multiple counts every end item id"
    (is (= #{"a1" "a2" "b"} (counted-selected :multiple ["a" "b" "a2" "a1" "nope"])))))

(deftest counted-keeps-the-ids-that-name-no-item-in-their-order
  (is (= {:expanded ["z" "y"] :selected ["nope" 7]}
         (:unknown (model/counted tree {:selection :single
                                        :expanded  ["z" "a" "y" "z"]
                                        :selected  ["nope" "a1" 7 "nope"]})))))

(deftest counted-takes-missing-or-invalid-lists-as-empty
  (doseq [ids [nil "a" {:id "a"}]]
    (is (= {:expanded #{} :selected #{} :unknown {:expanded [] :selected []}}
           (model/counted tree {:selection :multiple :expanded ids :selected ids})))))

;; ── Places and visible ids ────────────────────────────────────────────────
(def ^:private deep
  (model/index [{:id "a" :label "a"
                 :children [{:id "a1" :label "a1"
                             :children [{:id "a1x" :label "a1x"}]}
                            {:id "a2" :label "a2" :lazy true}]}
                {:id "b" :label "b"}]))

(defn- opened [ix expanded]
  (merge ix (model/counted ix {:expanded expanded})))

(deftest places-gives-each-visible-id-its-shown-children
  (testing "an open branch has its children, a closed one and a leaf have none"
    (is (= {:tree ["a" "b"] "a" ["a1" "a2"] "a1" [] "a2" [] "b" []}
           (model/places (opened deep ["a"])))))
  (testing "an open lazy branch with no children has none"
    (is (= [] (get (model/places (opened deep ["a" "a2"])) "a2"))))
  (testing "an open branch under a closed one is not visible"
    (is (= {:tree ["a" "b"] "a" [] "b" []}
           (model/places (opened deep ["a1"])))))
  (testing "no items gives an empty top level"
    (is (= {:tree []} (model/places (opened (model/index []) []))))))

(deftest visible-gives-the-visible-ids-in-tree-order
  (is (= ["a" "b"] (model/visible (opened deep []))))
  (is (= ["a" "a1" "a1x" "a2" "b"] (model/visible (opened deep ["a" "a1"]))))
  (is (= ["a" "b"] (model/visible (opened deep ["a1"])))))

;; ── Focus ─────────────────────────────────────────────────────────────────
(defn- state [expanded selected]
  (merge deep (model/counted deep {:selection :multiple :expanded expanded :selected selected})))

(deftest resolve-focus-keeps-a-visible-focused-id
  (is (= "a1x" (model/resolve-focus (state ["a" "a1"] ["b"]) "a1x"))))

(deftest resolve-focus-lifts-a-hidden-focused-id-to-its-nearest-visible-ancestor
  (is (= "a1" (model/resolve-focus (state ["a"] ["b"]) "a1x")))
  (is (= "a" (model/resolve-focus (state [] ["b"]) "a1x"))))

(deftest resolve-focus-falls-back-to-the-first-selected-id
  (testing "a removed focused id gives the first selected id in tree order"
    (is (= "a1x" (model/resolve-focus (state ["a" "a1"] ["b" "a1x"]) "gone"))))
  (testing "a hidden selected id gives its nearest visible ancestor"
    (is (= "a" (model/resolve-focus (state [] ["a1x"]) "gone")))))

(deftest resolve-focus-falls-back-to-the-first-visible-id
  (is (= "a" (model/resolve-focus (state [] []) nil)))
  (is (= "a" (model/resolve-focus (state [] []) "gone"))))

(deftest resolve-focus-of-no-items-is-nil
  (let [ix (model/index [])]
    (is (nil? (model/resolve-focus (merge ix (model/counted ix {})) "a")))))

;; ── Normalize ─────────────────────────────────────────────────────────────
(deftest parse-selection-gives-the-mode-or-single
  (is (= :none (model/parse-selection "none")))
  (is (= :leaf (model/parse-selection "leaf")))
  (is (= :multiple (model/parse-selection "multiple")))
  (is (= :single (model/parse-selection nil)))
  (is (= :single (model/parse-selection "MULTIPLE"))))

(deftest parse-list-gives-the-array-of-json-text-or-nil
  (testing "an array is a vector with keyword keys"
    (is (= [{:id "a" :label "a"}] (model/parse-list "[{\"id\":\"a\",\"label\":\"a\"}]")))
    (is (= ["a" "b"] (model/parse-list "[\"a\",\"b\"]"))))
  (testing "anything else is nil"
    (doseq [s [nil "" "not json" "{\"id\":\"a\"}" "\"a\"" "7"]]
      (is (nil? (model/parse-list s))))))

(def ^:private raw
  {:items-raw     "[{\"id\":\"src\",\"label\":\"src\",\"children\":[{\"id\":\"main\",\"label\":\"main.cljs\"}]},{\"id\":\"readme\",\"label\":\"README.md\"}]"
   :expanded-raw  "[\"src\",\"later\"]"
   :selected-raw  "[\"main\"]"
   :selection-raw "multiple"
   :label         "Files"
   :focused       "main"})

(deftest normalize-gives-the-model-of-the-attributes
  (let [m (model/normalize raw)]
    (testing "the index of the items"
      (is (= ["src" "main" "readme"] (:order m)))
      (is (= {nil ["src" "readme"] "src" ["main"]} (:children m))))
    (testing "the counted ids and the unknown ones"
      (is (= #{"src"} (:expanded m)))
      (is (= #{"main"} (:selected m)))
      (is (= {:expanded ["later"] :selected []} (:unknown m))))
    (testing "the other attributes"
      (is (= :multiple (:selection m)))
      (is (= "Files" (:label m)))
      (is (= "Loading…" (:loading-text m)))
      (is (false? (:guides m))))
    (testing "the resolved focus"
      (is (= "main" (:focused m))))))

(deftest normalize-resolves-a-hidden-focused-id
  (is (= "src" (:focused (model/normalize (assoc raw :expanded-raw "[]"))))))

(deftest normalize-of-invalid-attributes-gives-an-empty-tree
  (let [m (model/normalize {:items-raw "{" :selection-raw "bogus"
                                    :loading-text "Wait" :guides true})]
    (is (= [] (:order m)))
    (is (= :single (:selection m)))
    (is (= "Wait" (:loading-text m)))
    (is (true? (:guides m)))
    (is (nil? (:focused m)))))

(deftest normalize-gives-equal-models-for-equal-attributes
  (is (= (model/normalize raw) (model/normalize raw))))

;; ── Row ───────────────────────────────────────────────────────────────────
(def ^:private perms
  (model/index [{:id "all" :label "all"
                 :children [{:id "read" :label "read"}
                            {:id "write" :label "write"}
                            {:id "root" :label "root" :disabled true}]}
                {:id "locked" :label "locked"
                 :children [{:id "l1" :label "l1" :disabled true}
                            {:id "l2" :label "l2" :disabled true}]}
                {:id "later" :label "later" :lazy true}]))

(defn- model-of [selection expanded selected focused]
  (let [m (merge perms (model/counted perms {:selection selection
                                             :expanded  expanded
                                             :selected  selected}))]
    (assoc m :selection selection :focused focused)))

(deftest row-gives-the-place-of-an-item
  (let [r (model/row (model-of :single ["all"] [] "write") "write")]
    (is (= {:id "write" :level 2 :setsize 3 :posinset 2}
           (select-keys r [:id :level :setsize :posinset])))
    (is (true? (:focused? r)))
    (is (false? (:branch? r)))))

(deftest row-gives-expanded-to-branches-only
  (let [m (model-of :none ["all"] [] "all")]
    (is (= :open (:expanded (model/row m "all"))))
    (is (= :closed (:expanded (model/row m "locked"))))
    (is (= :closed (:expanded (model/row m "later"))))
    (is (not (contains? (model/row m "read") :expanded)))))

(deftest row-gives-select-to-selectable-items-only
  (testing "single selects every item, a disabled one too"
    (let [m (model-of :single ["all"] ["root"] "all")]
      (is (= :selected (:select (model/row m "root"))))
      (is (= :unselected (:select (model/row m "all"))))))
  (testing "leaf selects leaves only"
    (let [m (model-of :leaf ["all"] ["read"] "all")]
      (is (= :selected (:select (model/row m "read"))))
      (is (not (contains? (model/row m "all") :select)))))
  (testing "none and multiple select nothing"
    (is (not (contains? (model/row (model-of :none [] [] "all") "all") :select)))
    (is (not (contains? (model/row (model-of :multiple [] [] "all") "all") :select)))))

(deftest row-gives-check-in-multiple-only
  (let [check (fn [selected id] (:check (model/row (model-of :multiple ["all"] selected "all") id)))]
    (testing "an end item is checked when it is selected"
      (is (= :checked (check ["read"] "read")))
      (is (= :unchecked (check [] "read")))
      (is (= :checked (check ["later"] "later"))))
    (testing "a branch counts its enabled end items"
      (is (= :checked (check ["read" "write"] "all")))
      (is (= :mixed (check ["read"] "all")))
      (is (= :unchecked (check ["root"] "all"))))
    (testing "a branch with no enabled end item counts all of them"
      (is (= :checked (check ["l1" "l2"] "locked")))
      (is (= :mixed (check ["l1"] "locked")))
      (is (= :unchecked (check [] "locked")))))
  (is (not (contains? (model/row (model-of :single [] [] "all") "all") :check))))

(deftest row-gives-loading-to-an-open-lazy-branch-with-no-children
  (is (true? (:loading? (model/row (model-of :none ["later"] [] "all") "later"))))
  (is (false? (:loading? (model/row (model-of :none [] [] "all") "later")))))

(deftest row-gives-guide-active-to-the-parent-of-the-focused-item
  (let [m (model-of :none ["all"] [] "read")]
    (is (true? (:guide-active? (model/row m "all"))))
    (is (false? (:guide-active? (model/row m "locked"))))))

(deftest row->attrs-writes-a-leaf
  (let [attrs (model/row->attrs {:id "read" :level 2 :setsize 3 :posinset 1 :branch? false
                                 :disabled? false :focused? false :loading? false
                                 :guide-active? false :select :unselected})]
    (is (= {"aria-level" "2" "aria-setsize" "3" "aria-posinset" "1"
            "aria-expanded" nil "aria-selected" "false" "aria-checked" nil
            "aria-disabled" nil "aria-busy" nil "tabindex" "-1"
            "data-level" "2" "data-branch" nil "data-expanded" nil
            "data-selected" nil "data-disabled" nil}
           (:item attrs)))
    (is (= {"data-state" nil} (:checkbox attrs)))
    (is (= {"hidden" "" "data-active" nil} (:group attrs)))))

(deftest row->attrs-writes-an-open-mixed-focused-branch
  (let [attrs (model/row->attrs {:id "all" :level 1 :setsize 3 :posinset 1 :branch? true
                                 :disabled? true :focused? true :loading? true
                                 :guide-active? true :expanded :open :check :mixed})]
    (is (= {"aria-expanded" "true" "aria-checked" "mixed" "aria-disabled" "true"
            "aria-busy" "true" "tabindex" "0" "data-branch" "" "data-expanded" ""
            "data-selected" nil "data-disabled" ""}
           (select-keys (:item attrs) ["aria-expanded" "aria-checked" "aria-disabled"
                                       "aria-busy" "tabindex" "data-branch" "data-expanded"
                                       "data-selected" "data-disabled"])))
    (is (= {"data-state" "mixed"} (:checkbox attrs)))
    (is (= {"hidden" nil "data-active" ""} (:group attrs)))))

(deftest row->attrs-marks-a-selected-or-checked-item
  (is (= "" (get-in (model/row->attrs {:select :selected}) [:item "data-selected"])))
  (is (= "" (get-in (model/row->attrs {:check :checked}) [:item "data-selected"])))
  (is (= "true" (get-in (model/row->attrs {:select :selected}) [:item "aria-selected"])))
  (is (= "false" (get-in (model/row->attrs {:expanded :closed}) [:item "aria-expanded"]))))

;; ── Step ──────────────────────────────────────────────────────────────────
(defn- stepped [m op id]
  (model/step m {:op op :id id :source :keyboard}))

(deftest step-focuses-the-id-of-every-action
  (let [m (model-of :multiple [] [] "all")]
    (doseq [op [:focus :toggle :select :check :expand-siblings :unknown]]
      (is (= "locked" (:focused (stepped m op "locked")))))))

(deftest step-focus-changes-nothing-else
  (let [m (model-of :multiple ["all"] ["read"] "all")]
    (is (= (assoc m :focused "read") (stepped m :focus "read")))))

(deftest step-toggle-opens-and-closes-a-branch
  (let [m (model-of :none ["all"] [] "all")]
    (is (= #{} (:expanded (stepped m :toggle "all"))))
    (is (= #{"all" "later"} (:expanded (stepped m :toggle "later"))))
    (testing "a leaf does not open"
      (is (= #{"all"} (:expanded (stepped m :toggle "read")))))))

(deftest step-select-makes-the-id-the-selection
  (let [m (model-of :single ["all"] ["read"] "all")]
    (is (= #{"write"} (:selected (stepped m :select "write"))))
    (testing "a disabled id changes nothing"
      (is (= #{"read"} (:selected (stepped m :select "root")))))))

(deftest step-check-adds-or-removes-the-enabled-end-items
  (let [checked (fn [selected id] (:selected (stepped (model-of :multiple ["all"] selected "all") :check id)))]
    (testing "an end item toggles"
      (is (= #{"read"} (checked [] "read")))
      (is (= #{} (checked ["read"] "read"))))
    (testing "an unchecked or mixed branch adds its enabled end items"
      (is (= #{"read" "write"} (checked [] "all")))
      (is (= #{"read" "write"} (checked ["read"] "all"))))
    (testing "a checked branch removes them, a disabled one keeps its state"
      (is (= #{"root"} (checked ["read" "write" "root"] "all"))))
    (testing "a disabled id or a branch with no enabled end item changes nothing"
      (is (= #{} (checked [] "root")))
      (is (= #{"l1"} (checked ["l1"] "locked"))))
    (testing "an unloaded lazy branch checks itself"
      (is (= #{"later"} (checked [] "later"))))))

(deftest step-expand-siblings-opens-every-branch-under-the-same-parent
  (testing "the branches at the top level, its own included"
    (is (= #{"all" "locked" "later"}
           (:expanded (stepped (model-of :none [] [] "all") :expand-siblings "all")))))
  (testing "leaves under the same parent open nothing"
    (is (= #{"all"}
           (:expanded (stepped (model-of :none ["all"] [] "read") :expand-siblings "read"))))))

;; ── Events ────────────────────────────────────────────────────────────────
(defn- sent [m action]
  (model/events m action (model/step m action)))

(defn- with-unknown [m unknown]
  (assoc m :unknown unknown))

(deftest events-of-a-toggle-are-the-request-then-the-change
  (is (= [["x-tree-toggle" {:id "all" :nextExpanded "[]" :source "pointer"}]
          ["x-tree-change" {:attribute "expanded"}]]
         (sent (model-of :none ["all"] [] "all") {:op :toggle :id "all" :source :pointer}))))

(deftest events-of-a-select-list-the-changed-ids
  (is (= [["x-tree-select" {:id "write" :changed ["read" "write"]
                            :nextSelected "[\"write\"]" :source "keyboard"}]
          ["x-tree-change" {:attribute "selected"}]]
         (sent (model-of :single ["all"] ["read"] "read")
               {:op :select :id "write" :source :keyboard}))))

(deftest events-write-the-ids-in-tree-order-then-the-unknown-ones
  (let [m (with-unknown (model-of :multiple ["all"] ["later"] "all")
            {:expanded ["deep"] :selected ["gone" "old"]})]
    (testing "a check keeps the unknown ids of selected"
      (is (= "[\"read\",\"write\",\"later\",\"gone\",\"old\"]"
             (get-in (sent m {:op :check :id "all" :source :pointer}) [0 1 :nextSelected]))))
    (testing "a toggle keeps the unknown ids of expanded"
      (is (= "[\"deep\"]"
             (get-in (sent m {:op :toggle :id "all" :source :pointer}) [0 1 :nextExpanded])))
      (is (= "[\"all\",\"locked\",\"deep\"]"
             (get-in (sent m {:op :toggle :id "locked" :source :pointer}) [0 1 :nextExpanded]))))
    (testing "a select writes the selected id only"
      (is (= "[\"write\"]"
             (get-in (sent (-> (model-of :single ["all"] ["read"] "all")
                               (with-unknown {:expanded [] :selected ["gone"]}))
                           {:op :select :id "write" :source :pointer})
                     [0 1 :nextSelected]))))))

(deftest events-end-with-a-lazy-load-for-each-unloaded-branch-opened
  (let [lazy (model/index [{:id "p" :label "p" :lazy true}
                           {:id "q" :label "q" :lazy true :children [{:id "q1" :label "q1"}]}
                           {:id "r" :label "r" :lazy true}])
        m    (merge lazy (model/counted lazy {:selection :none}) {:selection :none :focused "p"})]
    (is (= [["x-tree-toggle" {:id "p" :nextExpanded "[\"p\",\"q\",\"r\"]" :source "keyboard"}]
            ["x-tree-change" {:attribute "expanded"}]
            ["x-tree-lazy-load" {:id "p"}]
            ["x-tree-lazy-load" {:id "r"}]]
           (sent m {:op :expand-siblings :id "p" :source :keyboard})))
    (testing "closing a lazy branch loads nothing"
      (is (= 2 (count (sent (assoc m :expanded #{"p"}) {:op :toggle :id "p" :source :keyboard})))))))

(deftest events-of-an-action-that-changes-nothing-are-none
  (let [m (model-of :single ["all"] ["read"] "all")]
    (is (= [] (sent m {:op :focus :id "write" :source :keyboard})))
    (is (= [] (sent m {:op :select :id "read" :source :keyboard})))
    (is (= [] (sent m {:op :select :id "root" :source :keyboard})))
    (is (= [] (sent m {:op :toggle :id "read" :source :keyboard})))))

;; ── Gestures ──────────────────────────────────────────────────────────────
(deftest target-reaches-the-item-of-each-move
  (let [m (model-of :none ["all"] [] "read")]
    (is (= "write" (model/target m "read" :next)))
    (is (= "all" (model/target m "read" :prev)))
    (is (= "all" (model/target m "read" :first)))
    (is (= "later" (model/target m "read" :last)))
    (is (= "all" (model/target m "read" :parent)))
    (is (= "read" (model/target m "all" :first-child)))
    (testing "a move past an end or out of the tree reaches nothing"
      (is (nil? (model/target m "all" :prev)))
      (is (nil? (model/target m "later" :next)))
      (is (nil? (model/target m "all" :parent)))
      (is (nil? (model/target m "locked" :first-child)))
      (is (nil? (model/target m "read" :bogus))))))

(defn- pressed [m key]
  (model/key->action m key))

(deftest key->action-moves-focus
  (let [m (model-of :none ["all"] [] "read")]
    (is (= {:op :focus :id "write" :source :keyboard} (pressed m "ArrowDown")))
    (is (= {:op :focus :id "all" :source :keyboard} (pressed m "ArrowUp")))
    (is (= {:op :focus :id "all" :source :keyboard} (pressed m "Home")))
    (is (= {:op :focus :id "later" :source :keyboard} (pressed m "End")))
    (is (= {:op :focus :id "all" :source :keyboard} (pressed m "ArrowLeft")))))

(deftest key->action-follows-the-kind-of-the-focused-item
  (testing "right opens a closed branch and enters an open one"
    (is (= {:op :toggle :id "locked" :source :keyboard}
           (pressed (model-of :none [] [] "locked") "ArrowRight")))
    (is (= {:op :focus :id "read" :source :keyboard}
           (pressed (model-of :none ["all"] [] "all") "ArrowRight"))))
  (testing "left closes an open branch"
    (is (= {:op :toggle :id "all" :source :keyboard}
           (pressed (model-of :none ["all"] [] "all") "ArrowLeft"))))
  (testing "right on a leaf and left at the top level do nothing"
    (is (nil? (pressed (model-of :none ["all"] [] "read") "ArrowRight")))
    (is (nil? (pressed (model-of :none [] [] "locked") "ArrowLeft")))))

(deftest key->action-activates-with-enter-and-space
  (doseq [key ["Enter" " "]]
    (is (= {:op :select :id "read" :source :keyboard}
           (pressed (model-of :single ["all"] [] "read") key)))
    (is (= {:op :check :id "all" :source :keyboard}
           (pressed (model-of :multiple [] [] "all") key)))
    (is (= {:op :toggle :id "all" :source :keyboard}
           (pressed (model-of :leaf [] [] "all") key)))
    (testing "activating a leaf in none does nothing"
      (is (nil? (pressed (model-of :none ["all"] [] "read") key))))))

(deftest key->action-opens-the-siblings-with-star
  (is (= {:op :expand-siblings :id "read" :source :keyboard}
         (pressed (model-of :none ["all"] [] "read") "*"))))

(deftest key->action-types-ahead
  (let [m (model-of :none ["all"] [] "all")]
    (testing "the next visible label that starts with the character, in any case"
      (is (= "write" (:id (pressed m "W"))))
      (is (= "locked" (:id (pressed m "l")))))
    (testing "it wraps"
      (is (= "all" (:id (pressed (assoc m :focused "later") "a")))))
    (testing "no match and other keys do nothing"
      (is (nil? (pressed m "z")))
      (is (nil? (pressed m "Tab")))
      (is (nil? (pressed m nil))))))

(deftest key->action-with-no-focused-item-is-nil
  (is (nil? (pressed (model-of :none [] [] nil) "ArrowDown"))))

(deftest click->action-activates-a-row-and-toggles-a-chevron
  (let [clicked (fn [selection id part]
                  (model/click->action (model-of selection ["all"] [] "all") id part))]
    (is (= {:op :select :id "read" :source :pointer} (clicked :single "read" :row)))
    (is (= {:op :check :id "all" :source :pointer} (clicked :multiple "all" :row)))
    (is (= {:op :toggle :id "all" :source :pointer} (clicked :multiple "all" :chevron)))
    (testing "a row that activates nothing still takes focus"
      (is (= {:op :focus :id "read" :source :pointer} (clicked :none "read" :row))))
    (testing "a chevron on a leaf, an unknown part or id do nothing"
      (is (nil? (clicked :none "read" :chevron)))
      (is (nil? (clicked :none "read" :label)))
      (is (nil? (clicked :none "gone" :row))))))

;; ── API metadata ──────────────────────────────────────────────────────────
(deftest every-observed-attribute-has-a-property-that-reflects-it
  (is (= (set (array-seq model/observed-attributes))
         (set (keep :reflects-attribute (vals model/property-api))))))

(deftest every-selection-of-the-enum-parses-to-its-mode
  (doseq [s model/allowed-selections]
    (is (= (keyword s) (model/parse-selection s)))))

(deftest each-request-names-a-detail-key-of-its-event
  (doseq [[_ {:keys [requests detail]}] model/event-schema
          [_ k] requests]
    (is (contains? detail k))))

(deftest every-event-sends-the-detail-keys-its-schema-declares
  (let [lazy-m (let [ix (model/index [{:id "p" :label "p" :lazy true}])]
                 (merge ix (model/counted ix {:selection :none}) {:selection :none :focused "p"}))
        sent   (concat (sent (model-of :none ["all"] [] "all") {:op :toggle :id "all" :source :pointer})
                       (sent (model-of :single [] [] "all") {:op :select :id "all" :source :pointer})
                       (sent lazy-m {:op :toggle :id "p" :source :keyboard}))]
    (is (= (set (keys model/event-schema)) (set (map first sent))))
    (doseq [[event detail] sent]
      (is (= (set (keys (get-in model/event-schema [event :detail]))) (set (keys detail)))
          event))))

(deftest normalize-keeps-the-index-of-the-model-before-while-items-is-the-same-text
  (let [prev (model/normalize raw)]
    (testing "the same text keeps the same index"
      (let [m (model/normalize (assoc raw :focused "src") prev)]
        (is (identical? (:item prev) (:item m)))
        (is (identical? (:order prev) (:order m)))
        (is (= "src" (:focused m)))))
    (testing "another text builds the index again"
      (let [m (model/normalize (assoc raw :items-raw "[{\"id\":\"x\",\"label\":\"x\"}]") prev)]
        (is (= ["x"] (:order m)))))
    (testing "the model is the one a fresh normalize gives"
      (is (= (model/normalize (assoc raw :selected-raw "[]"))
             (model/normalize (assoc raw :selected-raw "[]") prev))))))
