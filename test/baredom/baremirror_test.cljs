(ns baredom.baremirror-test
  (:require [baredom.components.x-checkbox.model :as checkbox-model]
            [baredom.components.x-checkbox.x-checkbox :as x-checkbox]
            [baredom.requests :as requests]
            [baremirror.alpha.events :as events]
            [baremirror.alpha.parts :as parts]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(x-checkbox/init!)

(def ^:private page-attr "data-baremirror-page")

(defn- remove-pages! []
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str "[" page-attr "]"))))

(use-fixtures :each {:after remove-pages!})

(defn- page!
  "A page in the document with one x-checkbox as the part `check`."
  []
  (let [page (parts/make-node! [:div {page-attr ""} [:x-checkbox {:data-x-part "check"}]])]
    (.append (.-body js/document) page)
    page))

(defn- toggle [state _message]
  (update state :checked? not))

(defn- refuse [state _message]
  (assoc state :refused? true))

(defn- render-check! [^js checkbox {:keys [checked? refused?]}]
  (parts/set-attrs! checkbox {:checked checked? :data-refused refused?}))

(defn- after-a-click
  "What the x-checkbox of a page shows after a click, with `step` as the application."
  [step]
  (let [page     (page!)
        checkbox (.-firstElementChild page)
        state    (atom {:checked? false})]
    (events/listen! page {:dispatch! (events/dispatcher state step identity
                                                        (partial render-check! checkbox))
                          :requests  requests/requests
                          :events    {[checkbox-model/event-change-request "check"] :toggle}})
    (.click (.querySelector (.-shadowRoot checkbox) "[part=control]"))
    {:checked? (.hasAttribute checkbox checkbox-model/attr-checked)
     :marked?  (.hasAttribute checkbox "data-refused")}))

(deftest the-application-decides-what-an-x-checkbox-shows
  (testing "a request that the application accepts changes the checkbox"
    (is (= {:checked? true :marked? false} (after-a-click toggle))))
  (testing "a request that the application refuses and marks leaves the checkbox unchecked"
    (is (= {:checked? false :marked? true} (after-a-click refuse)))))
