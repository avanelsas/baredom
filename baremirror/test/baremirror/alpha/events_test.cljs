(ns baremirror.alpha.events-test
  (:require [baremirror.alpha.events :as mirror-events]
            [baremirror.alpha.parts :as mirror-parts]
            [baremirror.alpha.test-stage :refer [element! remove-stages! stage!]]
            [clojure.test :refer [deftest is testing use-fixtures]]))

(use-fixtures :each {:after remove-stages!})

(defn- staged!
  "The node of the fixed template `fixed`, in the document."
  [fixed]
  (let [node (mirror-parts/make-node! fixed)]
    (.append (stage!) node)
    node))

(defn- origin-of!
  "The origin of an event of `event-type` sent from `target`, as a listener on `root` reads it."
  [^js root ^js target event-type]
  (let [origin (atom nil)]
    (.addEventListener root event-type (comp (partial reset! origin) mirror-events/read-origin)
                       #js {:once true})
    (.dispatchEvent target (js/Event. event-type #js {:bubbles true :composed true}))
    @origin))

(defn- find-one [^js root selector]
  (.querySelector root selector))

(def ^:private page
  [:div {:data-x-part "app"}
   [:ul {:data-x-part "list"}
    [:li {:data-x-key "t1"}
     [:span "Buy milk"]
     [:button {:data-x-part "remove"} [:i "x"]]
     [:ul
      [:li {:data-x-key "s1" :data-x-part "step"} [:b {:data-x-part "name"}]]]]]
   [:button {:data-x-part "add"}]
   [:p "No part here"]])

(deftest read-origin-gives-the-keys-and-the-nearest-part
  (let [root (staged! page)]
    (testing "an event from inside a part of a keyed node"
      (is (= {:key-path ["t1"] :part "remove" :node (find-one root "[data-x-part=remove]")}
             (origin-of! root (find-one root "i") "press"))))
    (testing "the keys of nested keyed nodes come outermost first"
      (is (= {:key-path ["t1" "s1"] :part "name" :node (find-one root "b")}
             (origin-of! root (find-one root "b") "press"))))
    (testing "a keyed node that is a part is the nearest part of its own event"
      (is (= {:key-path ["t1" "s1"] :part "step" :node (find-one root "[data-x-key=s1]")}
             (origin-of! root (find-one root "[data-x-key=s1]") "press"))))
    (testing "an event from a part outside every keyed node has no keys"
      (is (= {:key-path [] :part "add" :node (find-one root "[data-x-part=add]")}
             (origin-of! root (find-one root "[data-x-part=add]") "press"))))))

(deftest read-origin-does-not-look-for-a-part-beyond-the-nearest-keyed-node
  (let [root (staged! page)]
    (is (= {:key-path ["t1"] :part nil :node nil}
           (origin-of! root (find-one root "span") "press")))))

(deftest read-origin-takes-the-node-that-listens-as-a-part
  (let [root (staged! page)]
    (is (= {:key-path [] :part "app" :node root}
           (origin-of! root (find-one root "p") "press")))))

(deftest read-origin-takes-the-key-of-the-node-that-listens
  (let [root (staged! [:li {:data-x-key "t1"} [:ul [:li {:data-x-key "s1"} [:b]]]])]
    (is (= ["t1" "s1"] (:key-path (origin-of! root (find-one root "b") "press"))))))

(deftest read-origin-reads-the-path-through-a-shadow-root
  (let [root   (staged! [:ul [:li {:data-x-key "t1"} [:div {:data-x-part "host"}]]])
        host   (find-one root "div")
        inside (element! "button")]
    (.append (.attachShadow host #js {:mode "open"}) inside)
    (is (= {:key-path ["t1"] :part "host" :node host}
           (origin-of! root inside "press")))))

(deftest read-origin-of-an-event-that-is-no-longer-handled-is-empty
  (let [root  (staged! page)
        event (atom nil)]
    (.addEventListener root "press" (partial reset! event))
    (.dispatchEvent (find-one root "i") (js/Event. "press" #js {:bubbles true}))
    (is (= {:key-path [] :part nil :node nil} (mirror-events/read-origin @event)))))

(defn- view-of [n]
  {:shown n})

(deftest dispatcher-steps-the-state-and-renders-its-view
  (let [state     (atom 1)
        rendered  (atom [])
        dispatch! (mirror-events/dispatcher state + view-of (partial swap! rendered conj))]
    (testing "it returns the new state"
      (is (= 3 (dispatch! 2))))
    (testing "the state holder has the new state"
      (is (= 3 @state)))
    (testing "the view of the new state is rendered once"
      (is (= [{:shown 3}] @rendered)))))

(deftest dispatcher-renders-when-the-step-leaves-the-state-as-it-was
  (let [rendered  (atom [])
        dispatch! (mirror-events/dispatcher (atom 1) (fn [state _event] state) view-of
                                     (partial swap! rendered conj))]
    (dispatch! :refused)
    (is (= [{:shown 1}] @rendered))))

(defn- interrupted-screen!
  "The state and the screen after a dispatch of 1.
   `answers` maps a shown state to the event that its render dispatches before it finishes."
  [answers]
  (let [state     (atom 0)
        screen    (atom nil)
        dispatch! (atom nil)
        render!   (fn [{:keys [shown] :as vm}]
                    (when-some [event (answers shown)]
                      (@dispatch! event))
                    (reset! screen vm))]
    (reset! dispatch! (mirror-events/dispatcher state + view-of render!))
    (@dispatch! 1)
    {:state @state :screen @screen}))

(deftest dispatcher-ends-on-the-current-state-when-a-render-dispatches
  (testing "one dispatch during the render"
    (is (= {:state 11 :screen {:shown 11}}
           (interrupted-screen! {1 10}))))
  (testing "a dispatch during the render that follows it"
    (is (= {:state 111 :screen {:shown 111}}
           (interrupted-screen! {1 10 11 100})))))

(deftest dispatcher-renders-no-more-when-a-dispatch-during-the-render-leaves-the-state
  (let [renders   (atom 0)
        dispatch! (atom nil)
        render!   (fn [_vm]
                    (when (= 1 (swap! renders inc))
                      (@dispatch! 0)))]
    (reset! dispatch! (mirror-events/dispatcher (atom 0) + view-of render!))
    (@dispatch! 1)
    (is (= 2 @renders))))

(def ^:private task-page
  [:div
   [:ul
    [:li {:data-x-key "t1"}
     [:span {:data-x-part "check" :aria-checked "false"}]
     [:span {:data-x-part "note"}]
     [:button {:data-x-part "remove"} [:i "x"]]]]
   [:input {:data-x-part "draft"}]
   [:button {:data-x-part "add"}]])

(defn- send!
  "Sends a cancelable event of `event-type` from `target`, and returns it."
  [^js target event-type detail]
  (let [event (js/CustomEvent. event-type
                               #js {:bubbles true :composed true :cancelable true :detail detail})]
    (.dispatchEvent target event)
    event))

(defn- detail-value [_origin ^js e]
  (.. e -detail -value))

(defn- messages-of!
  "The messages that `listen!` dispatches for `events` when `send-events!` runs on a page."
  [events send-events!]
  (let [root     (staged! task-page)
        messages (atom [])]
    (mirror-events/listen! root {:dispatch! (partial swap! messages conj) :events events})
    (send-events! root)
    @messages))

(deftest listen-dispatches-the-meaning-of-an-event
  (testing "the argument is the nearest key"
    (is (= [[:remove "t1"]]
           (messages-of! {["press" "remove"] :remove}
                         (fn [root] (send! (find-one root "i") "press" nil))))))
  (testing "an event outside every keyed node has no argument"
    (is (= [[:add]]
           (messages-of! {["press" "add"] :add}
                         (fn [root] (send! (find-one root "[data-x-part=add]") "press" nil))))))
  (testing "a function in the entry gives the argument"
    (is (= [[:draft "milk"]]
           (messages-of! {["input" "draft"] [:draft detail-value]}
                         (fn [root] (send! (find-one root "input") "input" #js {:value "milk"})))))))

(deftest listen-does-nothing-for-an-event-with-no-entry
  (testing "another part"
    (is (= [] (messages-of! {["press" "remove"] :remove}
                            (fn [root] (send! (find-one root "[data-x-part=add]") "press" nil))))))
  (testing "another event type"
    (is (= [] (messages-of! {["press" "remove"] :remove}
                            (fn [root] (send! (find-one root "i") "click" nil)))))))

(deftest listen-adds-one-listener-for-an-event-type
  (is (= [[:remove "t1"]]
         (messages-of! {["press" "remove"] :remove ["press" "add"] :add}
                       (fn [root] (send! (find-one root "i") "press" nil))))))

(defn- answer-with!
  "A dispatch function that brings the part named `part-name` of `root` to `attrs`."
  [^js root part-name attrs]
  (fn [_message]
    (mirror-parts/set-attrs! (find-one root (str "[data-x-part=" part-name "]")) attrs)))

(defn- cancelled?
  "True when `listen!` cancels a `toggle` from the check of a page whose dispatch is `answer!`."
  [requests answer!]
  (let [root (staged! task-page)]
    (mirror-events/listen! root {:dispatch! (answer! root)
                          :requests  requests
                          :events    {["toggle" "check"] :toggle}})
    (.-defaultPrevented (send! (find-one root "[data-x-part=check]") "toggle" nil))))

(defn- leave-as-it-is [_root]
  (constantly nil))

(def ^:private toggle-request
  {"toggle" ["aria-checked"]})

(deftest listen-cancels-a-request-that-did-not-change-the-attributes-it-asks-for
  (testing "the dispatch changes nothing"
    (is (true? (cancelled? toggle-request leave-as-it-is))))
  (testing "the dispatch changes another part"
    (is (true? (cancelled? toggle-request
                           (fn [root] (answer-with! root "note" {:data-error "refused"}))))))
  (testing "the dispatch changes another attribute of the same part"
    (is (true? (cancelled? toggle-request
                           (fn [root] (answer-with! root "check" {:data-error "refused"})))))))

(deftest listen-does-not-cancel-a-request-that-changed-an-attribute-it-asks-for
  (is (false? (cancelled? toggle-request
                          (fn [root] (answer-with! root "check" {:aria-checked "true"}))))))

(deftest listen-does-not-cancel-an-event-that-is-not-named-as-a-request
  (is (false? (cancelled? {} leave-as-it-is))))
