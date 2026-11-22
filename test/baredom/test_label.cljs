(ns baredom.test-label
  "The checks of a form control with a slotted label: x-checkbox, x-switch and x-radio.
   Each control is unchecked at first and checked by one activation."
  (:require [cljs.test :refer-macros [is testing]]
            [baredom.utils.model :as mu]))

(def ^:private label-text "Notify me")
(def ^:private for-id "test-label-for")

(defn- ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn- ^js control-of [^js el] (shadow-part el "[part=control]"))
(defn- ^js label-of   [^js el] (shadow-part el "[part=label]"))

(defn- ^js mount! [^js node]
  (.appendChild (.-body js/document) node)
  node)

(defn- ^js make-el
  "An unmounted control of `tag` with `text` as its light DOM."
  [tag text]
  (let [el (.createElement js/document tag)]
    (set! (.-textContent el) text)
    el))

(defn- ^js make-link-el
  "An unmounted control of `tag` whose label is a link that keeps its own navigation."
  [tag]
  (let [el   (.createElement js/document tag)
        link (.createElement js/document "a")]
    (.setAttribute link "href" "#terms")
    (set! (.-textContent link) "terms")
    (.addEventListener link "click" (fn prevent-navigation [^js ev] (.preventDefault ev)))
    (.appendChild el link)
    el))

(defn- display-of [^js node]
  (.-display (js/getComputedStyle node)))

(defn- check-labelled! [tag]
  (testing "slotted text marks the control labelled and names the control"
    (let [el (mount! (make-el tag label-text))]
      (is (.hasAttribute el "data-labelled"))
      (is (= mu/label-id (.getAttribute (control-of el) "aria-labelledby")))
      (is (not= "none" (display-of (label-of el))))
      (.remove el)))
  (testing "no slotted content hides the label part"
    (let [el (mount! (make-el tag ""))]
      (is (not (.hasAttribute el "data-labelled")))
      (is (= "none" (display-of (label-of el))))
      (is (not (.hasAttribute (control-of el) "aria-labelledby")))
      (.remove el)))
  (testing "white space only is no label"
    (let [el (mount! (make-el tag "  "))]
      (is (not (.hasAttribute el "data-labelled")))
      (.remove el))))

(defn- check-name-priority! [tag]
  (testing "a host aria-label wins over the slotted label"
    (let [el (make-el tag label-text)]
      (.setAttribute el "aria-label" "Notifications")
      (mount! el)
      (is (= "Notifications" (.getAttribute (control-of el) "aria-label")))
      (is (not (.hasAttribute (control-of el) "aria-labelledby")))
      (.remove el)))
  (testing "a host aria-labelledby wins over the slotted label"
    (let [el (make-el tag label-text)]
      (.setAttribute el "aria-labelledby" "ext")
      (mount! el)
      (is (= "ext" (.getAttribute (control-of el) "aria-labelledby")))
      (.remove el))))

(defn- check-clicks! [tag]
  (testing "a click on the control checks it once"
    (let [el (mount! (make-el tag ""))]
      (.click (control-of el))
      (is (true? (.-checked el)))
      (.remove el)))
  (testing "a click on the label part checks the control and focuses it"
    (let [el (mount! (make-el tag label-text))]
      (.click (label-of el))
      (is (true? (.-checked el)))
      (is (identical? el (.-activeElement js/document)))
      (is (identical? (control-of el) (.-activeElement (.-shadowRoot el))))
      (.blur (control-of el))
      (.remove el)))
  (testing "a click on a slotted element checks the control"
    (let [el   (.createElement js/document tag)
          span (.createElement js/document "span")]
      (set! (.-textContent span) label-text)
      (.appendChild el span)
      (mount! el)
      (.click span)
      (is (true? (.-checked el)))
      (.remove el)))
  (testing "a click on a slotted link neither checks nor focuses the control"
    (let [el (mount! (make-link-el tag))]
      (.click (.querySelector el "a"))
      (is (false? (.-checked el)))
      (is (nil? (.-activeElement (.-shadowRoot el))))
      (.remove el)))
  (testing "a click on the label of a disabled control does not check it"
    (let [el (make-el tag label-text)]
      (.setAttribute el "disabled" "")
      (mount! el)
      (.click (label-of el))
      (is (false? (.-checked el)))
      (.remove el))))

(defn- check-native-labels! [tag]
  (testing "a click on a wrapping label checks the control"
    (let [lbl  (.createElement js/document "label")
          el   (.createElement js/document tag)
          text (.createElement js/document "span")]
      (set! (.-textContent text) label-text)
      (.appendChild lbl el)
      (.appendChild lbl text)
      (mount! lbl)
      (.click text)
      (is (true? (.-checked el)))
      (.remove lbl)))
  (testing "a click on a label for the control checks it and leaves the label id alone"
    (let [lbl (.createElement js/document "label")
          el  (.createElement js/document tag)]
      (.setAttribute lbl "for" for-id)
      (set! (.-textContent lbl) label-text)
      (.setAttribute el "id" for-id)
      (mount! lbl)
      (mount! el)
      (.click lbl)
      (is (true? (.-checked el)))
      (is (not (.hasAttribute lbl "id")))
      (.remove lbl)
      (.remove el))))

(defn check-label!
  "Runs the synchronous slotted-label checks on a control of `tag`."
  [tag]
  (check-labelled! tag)
  (check-name-priority! tag)
  (check-clicks! tag)
  (check-native-labels! tag))

(defn check-label-changes!
  "Runs the slotted-label checks that wait for slotchange, then calls `done`."
  [tag done]
  (let [added   (mount! (make-el tag ""))
        removed (mount! (make-el tag label-text))]
    (set! (.-textContent added) label-text)
    (set! (.-textContent removed) "")
    (js/setTimeout
     (fn check-after-slotchange []
       (testing "added slotted text marks the control labelled"
         (is (.hasAttribute added "data-labelled")))
       (testing "removed slotted text clears the label and the name"
         (is (not (.hasAttribute removed "data-labelled")))
         (is (not (.hasAttribute (control-of removed) "aria-labelledby"))))
       (.remove added)
       (.remove removed)
       (done))
     0)))
