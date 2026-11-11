(ns baredom.components.x-tree.x-tree-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-tree.x-tree :as x-tree]
            [baredom.components.x-tree.model :as model]))

(defn- cleanup! []
  (doseq [^js node (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove node)))

(use-fixtures :each
  {:before (fn [] (x-tree/init!))
   :after  cleanup!})

;; ── Helpers ───────────────────────────────────────────────────────────────
(def ^:private files
  #js [#js {:id "src" :label "src"
            :children #js [#js {:id "main" :label "main.cljs" :meta "4 KB"}
                           #js {:id "util" :label "util.cljs"}]}
       #js {:id "readme" :label "README.md"}])

(defn- mount!
  "An x-tree in the body with `attrs` and the `files` items."
  [attrs]
  (let [^js el (.createElement js/document model/tag-name)]
    (doseq [[k v] attrs] (.setAttribute el k v))
    (set! (.-items el) files)
    (.appendChild (.-body js/document) el)
    el))

(defn- item [^js el id]
  (.querySelector (.-shadowRoot el) (str "[data-x-key=\"" id "\"]")))

(defn- row [^js el id]
  (.querySelector (item el id) ":scope > [part~='row']"))

(defn- visible-ids [^js el]
  (mapv (fn key-of [^js li] (.getAttribute li "data-x-key"))
        (array-seq (.querySelectorAll (.-shadowRoot el) "[part~='item']"))))

(defn- click! [^js node]
  (.click node))

(defn- press! [^js el key]
  (let [^js focused (or (.. el -shadowRoot -activeElement)
                        (.querySelector (.-shadowRoot el) "[tabindex='0']"))]
    (.dispatchEvent focused (js/KeyboardEvent. "keydown" #js {:key key :bubbles true
                                                              :composed true :cancelable true}))))

(defn- record! [^js el event]
  (let [log (atom [])]
    (.addEventListener el event (fn on-event [^js e] (swap! log conj (js->clj (.-detail e)))))
    log))

;; ── Rendering ─────────────────────────────────────────────────────────────
(deftest renders-the-top-level-and-leaves-closed-children-out
  (let [el (mount! {"label" "Files"})]
    (is (= ["src" "readme"] (visible-ids el)))
    (is (= "tree" (.getAttribute (.querySelector (.-shadowRoot el) "[part~='tree']") "role")))
    (is (= "Files" (.getAttribute (.querySelector (.-shadowRoot el) "[part~='tree']")
                                  "aria-label")))
    (is (= "false" (.getAttribute (item el "src") "aria-expanded")))
    (is (not (.hasAttribute (item el "readme") "aria-expanded")))))

(deftest renders-the-children-of-an-open-branch-in-its-group
  (let [el (mount! {"expanded" "[\"src\"]"})]
    (is (= ["src" "main" "util" "readme"] (visible-ids el)))
    (is (= "2" (.getAttribute (item el "main") "aria-level")))
    (is (= "4 KB" (.-textContent (.querySelector (row el "main") "[part~='meta']"))))))

(deftest gives-one-tab-stop
  (let [el (mount! {})]
    (is (= "0" (.getAttribute (item el "src") "tabindex")))
    (is (= "-1" (.getAttribute (item el "readme") "tabindex")))))

(deftest marks-the-host-empty-with-no-valid-item
  (let [^js el (mount! {})]
    (set! (.-items el) #js [])
    (is (.hasAttribute el "data-empty"))))

;; ── Pointer ───────────────────────────────────────────────────────────────
(deftest a-click-selects-and-sends-the-request-then-the-change
  (let [el      (mount! {})
        selects (record! el "x-tree-select")
        changes (record! el "x-tree-change")]
    (click! (row el "readme"))
    (is (= "[\"readme\"]" (.getAttribute el "selected")))
    (is (= [{"id" "readme" "changed" ["readme"] "nextSelected" "[\"readme\"]"
             "source" "pointer"}]
           @selects))
    (is (= [{"attribute" "selected"}] @changes))
    (is (= "true" (.getAttribute (item el "readme") "aria-selected")))))

(deftest a-canceled-request-writes-nothing-and-sends-no-change
  (let [el      (mount! {})
        changes (record! el "x-tree-change")]
    (.addEventListener el "x-tree-select" (fn cancel [^js e] (.preventDefault e)))
    (click! (row el "readme"))
    (is (not (.hasAttribute el "selected")))
    (is (= [] @changes))
    (testing "the focus still moves"
      (is (= "0" (.getAttribute (item el "readme") "tabindex"))))))

(deftest a-click-on-the-chevron-opens-the-branch
  (let [el (mount! {})]
    (click! (.querySelector (row el "src") "[part~='chevron']"))
    (is (= "[\"src\"]" (.getAttribute el "expanded")))
    (is (= ["src" "main" "util" "readme"] (visible-ids el)))))

;; ── Keyboard ──────────────────────────────────────────────────────────────
(deftest arrows-move-the-focus-and-open-a-branch
  (let [el (mount! {})]
    (press! el "ArrowDown")
    (is (= "0" (.getAttribute (item el "readme") "tabindex")))
    (press! el "ArrowUp")
    (press! el "ArrowRight")
    (is (= "[\"src\"]" (.getAttribute el "expanded")))
    (press! el "ArrowRight")
    (is (= "0" (.getAttribute (item el "main") "tabindex")))))

;; ── Lazy branches ─────────────────────────────────────────────────────────
(deftest opening-a-lazy-branch-shows-the-loading-row-and-asks-for-children
  (let [^js el (mount! {"loading-text" "Wait"})
        loads  (record! el "x-tree-lazy-load")]
    (set! (.-items el) #js [#js {:id "remote" :label "remote" :lazy true}])
    (click! (.querySelector (row el "remote") "[part~='chevron']"))
    (is (= [{"id" "remote"}] @loads))
    (is (= "true" (.getAttribute (item el "remote") "aria-busy")))
    (is (= "Wait" (.-textContent (.querySelector (item el "remote") "[part~='loading']"))))))

;; ── Properties ────────────────────────────────────────────────────────────
(deftest array-properties-read-and-write-json
  (let [^js el (mount! {})]
    (set! (.-expanded el) #js ["src"])
    (is (= "[\"src\"]" (.getAttribute el "expanded")))
    (is (= ["src"] (js->clj (.-expanded el))))
    (set! (.-selected el) "[\"main\"]")
    (is (= "[\"main\"]" (.getAttribute el "selected")))
    (set! (.-selected el) "not json")
    (is (not (.hasAttribute el "selected")))
    (is (nil? (.-selected el)))))
