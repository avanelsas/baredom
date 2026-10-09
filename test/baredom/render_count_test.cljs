(ns baredom.render-count-test
  (:require [baredom.components-table :as table]
            [baredom.test-elements :as elements]
            [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [clojure.string :as str]))

(def ^:private known-defects
  "The defects each component has today. A component that is not named has none."
  {"x-carousel"            #{:change-renders-more-than-once}
   "x-date-picker"         #{:no-change-writes}
   "x-divider"             #{:change-renders-more-than-once}
   "x-notification-center" #{:no-change-writes}
   "x-search-field"        #{:no-change-writes}
   "x-sidebar"             #{:no-change-writes}})

(def ^:private known-gaps
  "What this test cannot measure in a component today: its renders, or its renders in a hold."
  {"x-context-menu"        #{:renders :hold}
   "x-date-picker"         #{:renders :hold}
   "x-i18n"                #{:hold}
   "x-menu-item"           #{:hold}
   "x-notification-center" #{:renders :hold}
   "x-ripple-effect"       #{:renders :hold}
   "x-scroll-parallax"     #{:hold}
   "x-search-field"        #{:renders :hold}
   "x-sidebar"             #{:renders :hold}
   "x-skeleton-group"      #{:hold}
   "x-spinner"             #{:hold}
   "x-spotlight-card"      #{:hold}
   "x-stepper"             #{:hold}
   "x-theme"               #{:hold}})

(def ^:private attr-disabled "disabled")

(def ^:private values
  "The values an attribute takes in turn. nil is an absent attribute."
  [nil "" "a" "b" "1" "2" nil])

(def ^:private disabling {:attr attr-disabled :from nil :to ""})

(use-fixtures :each {:after elements/remove-all!})

(defn- mount!
  "A new `tag` element with a size, which it puts in the document."
  [tag]
  (elements/in-body! tag {"style" "width:300px;height:200px"}))

(defn- write! [^js el {:keys [attr]} value]
  (if (nil? value)
    (.removeAttribute el attr)
    (.setAttribute el attr value)))

(defn- write-from! [^js el change]
  (write! el change (:from change)))

(defn- write-to! [^js el change]
  (write! el change (:to change)))

(defn- write-held!
  "Writes every change of `changes` to `el` inside one hold."
  [^js el changes]
  (.call (aget el component/hold-key) el (partial run! (partial write-to! el) changes)))

(defn- model-write? [^js el payload]
  (and (= :state/instance-field-set (:type payload))
       (identical? el (:el payload))
       (str/ends-with? (:field payload) "Model")))

(def ^:private dom-writes #{:dom/attribute-set :dom/attribute-removed})

(defn- write?
  "True when `payload` is a write of the cached model of `el`, or a write to the DOM."
  [^js el payload]
  (or (model-write? el payload)
      (contains? dom-writes (:type payload))))

(defn- count-when! [counts? seen payload]
  (when (counts? payload)
    (swap! seen inc)))

(defn- counted!
  "The number of trace payloads that `counts?` accepts while `work` runs."
  [counts? work]
  (let [hook @du/trace-hook
        seen (atom 0)]
    (reset! du/trace-hook (partial count-when! counts? seen))
    (try
      (work)
      (finally
        (reset! du/trace-hook hook)))
    @seen))

(defn- renders!
  "The number of times `el` writes its cached model while `work` runs."
  [^js el work]
  (counted! (partial model-write? el) work))

(defn- measured!
  "`change` with what it costs a new `tag` element: the renders of the change, and the renders
   of the same write once more."
  [tag change]
  (let [el      (mount! tag)
        _       (write-from! el change)
        renders (renders! el (partial write-to! el change))
        again   (renders! el (partial write-to! el change))]
    (.remove el)
    (assoc change :renders renders :again again)))

(defn- held-renders!
  "The renders of a new `tag` element when all of `changes` are written inside one hold."
  [tag changes]
  (let [el (mount! tag)
        _  (run! (partial write-from! el) changes)
        n  (renders! el (partial write-held! el changes))]
    (.remove el)
    n))

(defn- call-back!
  "Calls the attribute callback of `el` for `attr`, with no attribute changed."
  [^js el attr]
  (.attributeChangedCallback el attr "old" "new"))

(defn- call-back-all! [^js el attrs]
  (run! (partial call-back! el) attrs))

(defn- idle-writes!
  "The writes of a new `tag` element when each of `attrs` calls back with no attribute changed.
   A first round of callbacks is not counted."
  [tag attrs]
  (let [el (mount! tag)
        _  (call-back-all! el attrs)
        n  (counted! (partial write? el) (partial call-back-all! el attrs))]
    (.remove el)
    n))

(defn- change [attr [from to]]
  {:attr attr :from from :to to})

(defn- changes-of [attr]
  (mapv (partial change attr) (partition 2 1 values)))

(defn- live? [{:keys [attr renders]}]
  (and (= 1 renders) (not= attr-disabled attr)))

(defn- live
  "The first change of each attribute that renders once, with `disabled` left out."
  [changes]
  (->> changes
       (filter live?)
       (partition-by :attr)
       (mapv first)))

(defn- renders-of!
  "What each change of `attrs` costs `tag`: one by one, in pairs inside a hold, with `disabled`
   inside a hold, and with no attribute changed."
  [tag attrs]
  (let [changes (mapv (partial measured! tag) (mapcat changes-of attrs))
        lives   (live changes)]
    {:changes       changes
     :held          (mapv (partial held-renders! tag) (partition 2 1 lives))
     :disabled-held (when (and (some #{attr-disabled} attrs) (seq lives))
                      (held-renders! tag [disabling (first lives)]))
     :idle          (idle-writes! tag attrs)}))

(defn- measurement!
  "The renders of `tag`, with the number of errors its callbacks threw."
  [tag attrs]
  (let [handler (.-onerror js/window)
        thrown  (atom 0)]
    (set! (.-onerror js/window) (fn [& _] (swap! thrown inc) true))
    (try
      (assoc (renders-of! tag attrs) :thrown @thrown)
      (finally
        (set! (.-onerror js/window) handler)))))

(defn- rendered? [{:keys [renders]}]
  (pos? renders))

(defn- rendered-more-than-once? [{:keys [renders]}]
  (< 1 renders))

(defn- rendered-again? [{:keys [again]}]
  (pos? again))

(defn- more-than-one? [n]
  (< 1 n))

(defn- defects
  "The defects that `measurement` shows, as a set of keywords."
  [{:keys [changes held disabled-held idle thrown]}]
  (cond-> #{}
    (some rendered-more-than-once? changes) (conj :change-renders-more-than-once)
    (some rendered-again? changes)          (conj :same-value-renders)
    (pos? idle)                             (conj :no-change-writes)
    (some more-than-one? held)              (conj :hold-renders-more-than-once)
    (some-> disabled-held more-than-one?)   (conj :disabled-in-hold-renders-more-than-once)
    (pos? thrown)                           (conj :attribute-value-throws)))

(defn- gaps
  "What `measurement` could not show, as a set of keywords."
  [{:keys [changes held]}]
  (cond-> #{}
    (not-any? rendered? changes) (conj :renders)
    (empty? held)                (conj :hold)))

(def ^:private clean
  "The measurement of a component with two attributes and no defect."
  {:changes       [{:attr "a" :renders 1 :again 0} {:attr "b" :renders 1 :again 0}]
    :held          [1]
    :disabled-held 1
    :idle          0
    :thrown        0})

(deftest defects-names-each-defect-of-a-measurement
  (is (= #{} (defects clean)))
  (is (= #{:change-renders-more-than-once} (defects (assoc-in clean [:changes 0 :renders] 2))))
  (is (= #{:same-value-renders} (defects (assoc-in clean [:changes 0 :again] 1))))
  (is (= #{:no-change-writes} (defects (assoc clean :idle 3))))
  (is (= #{:hold-renders-more-than-once} (defects (assoc clean :held [1 2]))))
  (is (= #{:disabled-in-hold-renders-more-than-once} (defects (assoc clean :disabled-held 2))))
  (is (= #{:attribute-value-throws} (defects (assoc clean :thrown 1))))
  (is (= #{} (defects (assoc clean :disabled-held nil)))))

(deftest gaps-names-what-a-measurement-could-not-show
  (is (= #{} (gaps clean)))
  (is (= #{:hold} (gaps (assoc clean :held []))))
  (is (= #{:renders :hold} (gaps {:changes [{:attr "a" :renders 0 :again 0}] :held []}))))

(deftest each-component-has-its-known-defects-and-gaps
  (doseq [[register! {:keys [tag-name observed-attributes]}] table/components]
    (register!)
    (testing tag-name
      (let [measurement (measurement! tag-name (vec observed-attributes))]
        (is (= (get known-defects tag-name #{}) (defects measurement)))
        (is (= (get known-gaps tag-name #{}) (gaps measurement)))))))
