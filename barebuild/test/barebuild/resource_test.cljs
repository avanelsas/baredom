(ns barebuild.resource-test
  "step tests: (step resource event) -> {:resource :effects}.
   Pure, =-asserted per event branch."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.resource :as resource]))

;; Built through the same constructor the element boots with, so a fixture cannot claim a shape
;; the runtime never produces.
(def base
  (resource/initial {:resource/id "tasks"
                     :endpoint    "/api/tasks"
                     :url-intent  {}}))

(def accepted
  {:outcome    :accepted
   :request/id "req-1"
   :revision   "tasks:v1"
   :query      {}
   :value      [{"id" 1 "owner" "Alice"}]
   :shape      {:id-key "id" :fields [{:key "owner" :type :string}]}})

(defn- expecting
  "Put r in flight for `response`: a live request whose id and query the response answers,
   so (answers-in-flight-read? r response) holds."
  [r response]
  (assoc r :active-request {:request/id (:request/id response)
                            :query      (:query response)}))

(defn- effect-value
  "The value of the first `k` effect in `effects`, or nil when step emitted none."
  [effects k]
  (some (fn [[fx m]] (when (= k fx) m)) effects))

;; --- the accepted envelope a consumer compares -----------------------------

(defn- projected-accepted
  "The accepted envelope as a consumer sees it: the :accepted of the view projected from a
   resource holding `envelope`. This is the slice the default render-key compares."
  [envelope]
  (:accepted (resource/project (assoc base :last-accepted envelope))))

(deftest projected-accepted-drops-the-exchange-id
  (testing "the id names the request that fetched the value, not the value, so it is gone from
            the view rather than merely ignored by the consumer that compares it"
    (is (nil? (:request/id (projected-accepted accepted)))))
  (testing "two responses identical but for :request/id project equal, so a refetch that
            returns unchanged data does not re-render"
    (is (= (projected-accepted (assoc accepted :request/id "tasks:1"))
           (projected-accepted (assoc accepted :request/id "tasks:2")))))
  (testing "a write install's :request/id is an exchange id too, so an identical read after a
            write does not re-render"
    (is (= (projected-accepted (assoc accepted :request/id "tasks:w1"))
           (projected-accepted accepted)))))

(deftest projected-accepted-keeps-every-drawn-field
  (let [k (projected-accepted accepted)]
    (testing "a change in any field a consumer draws yields a different value"
      (is (not= k (projected-accepted (assoc accepted :value [{"id" 2 "owner" "Bob"}]))))
      (is (not= k (projected-accepted (assoc accepted :query {:sort "start"}))))
      (is (not= k (projected-accepted (assoc accepted :page-info {:page 2}))))
      (is (not= k (projected-accepted (assoc accepted :shape {:id-key "id" :fields []})))))
    (testing "revision stays, it is the server's claim about the content rather than a name for
              the exchange, and a consumer may draw it"
      (is (not= k (projected-accepted (assoc accepted :revision "tasks:v2")))))))

(deftest connected-emits-fetch
  (let [r* (assoc base :request-count 1
                       :active-request {:request/id "tasks:1" :query {}})]
    (is (= {:resource r*
            :effects  [[:notify-consumers {:resource base}]
                       [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1" :request/id "tasks:1"}]]}
           (resource/step base [:connected {}]))
        "connect notifies, then fetches the endpoint, recording a fresh live request")))

(deftest connected-notifies-a-loading-view-before-the-read-opens
  (testing "boot opens its read through the same combinator every other transition uses, so the
            notify carries the pre-start value. A resource that has fetched nothing answers
            nothing, so that value already reports pending? and the first paint says loading"
    (let [{:keys [resource effects]} (resource/step base [:connected {}])
          notified                   (get-in (first effects) [1 :resource])]
      (is (true? (resource/pending? base)))
      (is (true? (resource/pending? notified)))
      (is (= (resource/project resource) (resource/project notified))
          "the read is invisible to a consumer, so notifying either side of it is the same view"))))

(deftest connected-carries-url-intent
  (let [r (assoc base :url-intent {:sort "start" :direction "desc"})
        {:keys [resource effects]} (resource/step r [:connected {}])]
    (is (= [[:notify-consumers {:resource r}]
            [:fetch {:method     "GET"
                     :url        "/api/tasks?requestId=tasks:1&direction=desc&sort=start"
                     :request/id "tasks:1"}]]
           effects)
        "a resource booted from a sorted URL fetches that query on connect")
    (is (= {:request/id "tasks:1" :query {:sort "start" :direction "desc"}}
           (:active-request resource)))))

;; --- 5b-4: SSR boot embed (T1) ---------------------------------------------

(deftest connected-with-matching-embed-installs-and-skips-fetch
  (let [r     (assoc base :url-intent {})          ; bare boot URL -> empty intent
        embed (assoc accepted :query {})           ; embed answers the empty query
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "the embed installs as :last-accepted (first paint from the page)"
      (is (= embed (:last-accepted resource))))
    (testing "not pending -> no fetch, no live request recorded"
      (is (nil? (:active-request resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest connected-with-mismatched-embed-installs-and-fetches
  (let [r     (assoc base :url-intent {:sort "owner"})   ; URL moved past what was embedded
        embed (assoc accepted :query {})
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "the embed still installs (it is genuinely the last accepted value)"
      (is (= embed (:last-accepted resource))))
    (testing "notifies AND fetches the current intent under a fresh id"
      (is (= [[:notify-consumers {:resource (dissoc resource :request-count :active-request)}]
              [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&sort=owner" :request/id "tasks:1"}]]
             effects)))
    (testing "the notify runs before the read opens, so it carries the installed value rather
              than the one holding the request. An installed embed answers something, so pending?
              already reads true and the consumer sees the same view either way"
      (is (true? (resource/pending? (get-in (first effects) [1 :resource]))))
      (is (= (resource/project resource)
             (resource/project (get-in (first effects) [1 :resource])))))))

(deftest connected-with-broken-embed-fetches
  (let [marker {:protocol-failure {:reason :unknown-outcome}}
        {:keys [resource effects]} (resource/step base [:connected {:embed marker}])]
    (is (nil? (:last-accepted resource)) "a broken embed is not installed")
    (is (= [[:diagnostic {:code :broken-embed}]
            [:notify-consumers {:resource base}]
            [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1" :request/id "tasks:1"}]] effects)
        "diagnoses the broken embed, then falls back to a normal fetch")))

(deftest connected-with-rejected-embed-installs-failure-and-skips-fetch
  (let [r     (assoc base :url-intent {:sort "nope"})
        embed {:outcome :rejected :request/id "req-1" :query {:sort "nope"}
               :error   {:code :invalid-query :message "no"}}
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "the server already adjudicated this query at render time"
      (is (= {:failure :rejected :for :read :response embed :query (:query embed)} (:last-failure resource)))
      (is (nil? (:last-accepted resource))))
    (testing "the failure adjudicates the intent, so there is no boot fetch"
      (is (nil? (:active-request resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest connected-with-stale-rejected-embed-diagnoses-and-fetches
  (let [r     (assoc base :url-intent {:sort "owner"})   ; the URL moved since the server rendered
        embed {:outcome :rejected :request/id "req-1" :query {:sort "stale"}
               :error   {:code :invalid-query :message "no"}}
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "a stale rejection is not installed"
      (is (nil? (:last-failure resource)))
      (is (nil? (:last-accepted resource)))
      (is (= {:request/id "tasks:1" :query {:sort "owner"}} (:active-request resource))))
    (is (= [[:diagnostic {:code :stale-rejected-embed}]
            [:notify-consumers {:resource r}]
            [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&sort=owner" :request/id "tasks:1"}]]
           effects)
        "diagnoses the stale rejection, then fetches the current intent")))

(deftest connected-with-invalid-accepted-embed-records-contract-failure
  (let [r     (assoc base :url-intent {})
        embed (assoc accepted :query {} :value [{"id" 1}])   ; declared field "owner" missing from the row
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "a contract-broken accepted embed adjudicates as a failure, not installed data"
      (is (= :contract (get-in resource [:last-failure :failure])))
      (is (nil? (:last-accepted resource))))
    (testing "the failure adjudicates the intent (echo = intent), so no boot fetch"
      (is (nil? (:active-request resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest intent-patch-merges-writes-url-and-fetches
  (let [r     (assoc base :url-intent {:sort "owner" :direction "asc"})
        patch {:query-patch {:direction "desc"} :gesture-class nil}
        {:keys [resource effects]} (resource/step r [:intent-patch patch])]
    (testing "the patch merges into :url-intent (sort preserved, direction updated)"
      (is (= {:sort "owner" :direction "desc"} (:url-intent resource))))
    (testing "emits a scoped :replace url-write, notifies, then fetches the new intent. The
              notify names the resource as the gesture left it, before the read opened: an intent
              the value does not answer already reads as pending, so opening the read does not
              change what a consumer sees"
      (is (= [[:url-write {:resource/id "tasks"
                           :params      {:sort "owner" :direction "desc"}
                           :mode        :replace}]
              [:notify-consumers {:resource (assoc base :url-intent {:sort      "owner"
                                                                     :direction "desc"})}]
              [:fetch {:method     "GET"
                       :url        "/api/tasks?requestId=tasks:1&direction=desc&sort=owner"
                       :request/id "tasks:1"}]]
             effects)))))

(deftest intent-patch-from-empty-intent
  (let [patch {:query-patch {:sort "owner" :direction "asc"} :gesture-class nil}
        {:keys [resource]} (resource/step base [:intent-patch patch])]
    (is (= {:sort "owner" :direction "asc"} (:url-intent resource))
        "merging a patch into a nil url-intent yields just the patch")))

(deftest intent-patch-history-mode-from-gesture-class
  (let [r base]
    (testing ":navigation resolves to :push via the default :history-policy every boot carries"
      (let [{:keys [effects]} (resource/step r [:intent-patch {:query-patch {:page "2"}
                                                               :gesture-class :navigation}])
            [_ params] (first effects)]
        (is (= :push (:mode params)))))
    (testing ":refinement is not in the policy -> defaults to :replace"
      (let [{:keys [effects]} (resource/step r [:intent-patch {:query-patch {:sort "owner"}
                                                               :gesture-class :refinement}])
            [_ params] (first effects)]
        (is (= :replace (:mode params)))))))

(deftest intent-patch-in-flight-does-not-fetch
  (let [r     (assoc base :url-intent {:sort "owner"}
                          :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        patch {:query-patch {:direction "desc"} :gesture-class nil}
        {:keys [resource effects]} (resource/step r [:intent-patch patch])]
    (testing "a gesture during a live request writes the URL but launches no second fetch (C2->C3 seam)"
      (is (= {:sort "owner" :direction "desc"} (:url-intent resource)))
      (is (= {:request/id "tasks:1" :query {:sort "owner"}} (:active-request resource)))
      (is (= [[:url-write {:resource/id "tasks"
                           :params      {:sort "owner" :direction "desc"}
                           :mode        :replace}]
              [:notify-consumers {:resource resource}]]
             effects)))))

(deftest intent-patch-clearing-sort-canonicalizes-to-empty
  (let [r     (assoc base :url-intent {:sort "owner" :direction "desc"})
        patch {:query-patch {:sort nil :direction nil} :gesture-class :refinement}
        {:keys [resource effects]} (resource/step r [:intent-patch patch])]
    (testing "a nil-valued patch clears the keys: the merged intent canonicalizes to {}"
      (is (= {} (:url-intent resource))))
    (testing "it fetches the now-empty query (matches a server echoing no sort keys)"
      (is (some (fn [[fx m]] (and (= :fetch fx)
                                  (= "/api/tasks?requestId=tasks:1" (:url m))))
                effects)))))

;; --- routing a cross-resource intent ---------------------------------------

(deftest intent-patch-naming-a-sibling-routes-instead-of-applying
  (testing "a targeted intent is not this resource's to apply. step names the target in an effect
            and leaves its own value untouched, so the routing is visible rather than decided in
            a closure at the edge"
    (let [r (assoc base :url-intent {:sort "owner"})
          {:keys [resource effects]} (resource/step r [:intent-patch
                                                       {:query-patch {:project "p-1"}
                                                        :target-id   "projects"}])]
      (is (= r resource) "the sending resource does not move")
      (is (= [[:route-intent {:resource/id "projects"
                              :patch       {:query-patch {:project "p-1"}}}]]
             effects)
          "the target is dropped from the patch that travels, it addressed the hop not the query"))))

(deftest intent-patch-targeting-self-is-applied-not-routed
  (testing "no target, or a target equal to the resource's own id, drives the resource itself"
    (doseq [payload [{:query-patch {:sort "start"}}
                     {:query-patch {:sort "start"} :target-id "tasks"}]]
      (let [{:keys [resource effects]} (resource/step (assoc base :url-intent {}) [:intent-patch payload])]
        (is (= {:sort "start"} (:url-intent resource)))
        (is (not-any? (fn [[fx _]] (= :route-intent fx)) effects))))))

(deftest an-unnamed-resource-can-still-name-a-sibling
  (testing "the root resource has a nil id, so any named target is a hop rather than a self-drive"
    (let [{:keys [effects]} (resource/step (assoc base :resource/id nil :url-intent {})
                                           [:intent-patch {:query-patch {:sort "start"}
                                                           :target-id   "tasks"}])]
      (is (= :route-intent (ffirst effects))))))

(deftest intent-unroutable-diagnoses-rather-than-vanishing
  (testing "the executor could not resolve the target. The gesture is lost either way, but it
            reaches the recorder as an event instead of evaporating in a closure"
    (let [{:keys [resource effects]} (resource/step base [:intent-unroutable {:resource/id "ghost"}])]
      (is (= base resource))
      (is (= [[:diagnostic {:code :unroutable-intent :detail {:resource/id "ghost"}}]] effects)))))

(deftest url-changed-replaces-intent-and-fetches-without-writing
  (let [r (assoc base :url-intent {:sort "owner"})
        {:keys [resource effects]} (resource/step r [:url-changed {:page "2"}])]
    (testing "the URL-derived intent replaces :url-intent (not merged)"
      (is (= {:page "2"} (:url-intent resource))))
    (testing "notifies, then fetches the new intent, and does NOT write the URL (browser already
              moved). The notify names the resource as the address bar left it, before the read
              opened, which a consumer cannot tell apart: the new intent already reads as pending"
      (is (= [[:notify-consumers {:resource (assoc base :url-intent {:page "2"})}]
              [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&page=2" :request/id "tasks:1"}]]
             effects)))))

(deftest opening-a-read-does-not-change-the-projected-view
  (testing "every transition that trails a fetch notifies before opening the read, so the notify
            names the resource without it. That is safe because an intent the value does not
            answer already projects as pending, which is what a consumer reads"
    (let [before (assoc base :url-intent {:page "2"})
          after  (:resource (resource/step before [:url-changed {:page "2"}]))]
      (is (some? (:active-request after)) "the read did open")
      (is (= (resource/project before) (resource/project after))))))

(deftest accepted-response-installs-and-notifies
  (let [r (expecting base accepted)
        {:keys [resource effects]} (resource/step r [:response accepted])]
    (testing "response installed into :last-accepted, rest of the resource preserved"
      (is (= accepted (:last-accepted resource)))
      (is (= "tasks" (:resource/id resource)))
      (is (= "/api/tasks" (:endpoint resource))))
    (testing ":active-request is cleared so the next gesture can fetch (anti-wedge)"
      (is (nil? (:active-request resource))))
    (testing "notify-consumers carries the same updated resource value"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest installable-gates-on-request-id-only
  (let [r (assoc base :url-intent {:sort "owner"}
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})]
    (testing "a matching id installs even when the server normalized the echo past intent and the
              query as sent, so echo adoption (T5) can still take it"
      (is (resource/answers-in-flight-read? r {:request/id "tasks:1" :query {:sort "owner" :direction "asc"}})))
    (testing "a non-matching id never installs"
      (is (not (resource/answers-in-flight-read? r {:request/id "tasks:9" :query {:sort "owner"}}))))))

(deftest stale-response-is-dropped
  (let [r (assoc base :active-request {:request/id "tasks:9" :query nil}
                      :last-accepted accepted)
        {:keys [resource effects]} (resource/step r [:response accepted])]  ; accepted answers "req-1", not "tasks:9"
    (testing "a response that doesn't answer the live request never replaces visible truth (T4)"
      (is (= accepted (:last-accepted resource)))
      (is (= {:request/id "tasks:9" :query nil} (:active-request resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-response}]] effects)))))

(deftest second-gesture-fetches-after-a-response
  (let [after-connect (:resource (resource/step base [:connected {}]))          ; live request tasks:1
        response      (assoc accepted :request/id "tasks:1" :query nil)          ; answers tasks:1
        after-resp    (:resource (resource/step after-connect [:response response]))
        {:keys [resource effects]} (resource/step after-resp
                                                 [:intent-patch {:query-patch {:sort "owner"}
                                                                 :gesture-class nil}])]
    (testing "the installable response cleared the first request"
      (is (nil? (:active-request after-resp))))
    (testing "the next gesture is free to fetch again under a fresh id"
      (is (= {:request/id "tasks:2" :query {:sort "owner"}} (:active-request resource)))
      (is (some (fn [[fx m]] (and (= :fetch fx) (= "tasks:2" (:request/id m)))) effects)))))

(def rejected
  {:outcome    :rejected
   :request/id "req-2"
   :query      {:sort "bogus" :direction "asc"}
   :error      {:code :invalid-query :message "Sorting by \"bogus\" is not supported."}})

(deftest rejected-response-records-failure-and-notifies
  ;; the intent is what the read was issued for, as the element always holds it. Without it the
  ;; rejection answers a question nobody asked and a trailing fetch follows, correctly.
  (let [r (assoc (expecting base rejected) :url-intent (:query rejected))
        {:keys [resource effects]} (resource/step r [:response rejected])]
    (testing "the rejection is recorded as a :rejected failure wrapping the response"
      (is (= {:failure :rejected :for :read :response rejected :query (:query rejected)} (:last-failure resource))))
    (testing ":active-request is cleared; :last-accepted is left untouched"
      (is (nil? (:active-request resource)))
      (is (nil? (:last-accepted resource))))
    (testing "consumers are notified with the updated resource"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest accepted-response-clears-a-prior-failure
  (let [failed (expecting (assoc base :last-failure {:failure :rejected :response rejected}) accepted)
        {:keys [resource]} (resource/step failed [:response accepted])]
    (is (= accepted (:last-accepted resource)) "the good value installs")
    (is (nil? (:last-failure resource))
        "a successful response clears the prior failure (T5)")))

;; --- 5b-3: contract validation (T7) ----------------------------------------

(def valid-accepted
  {:outcome    :accepted
   :request/id "req-3"
   :revision   "tasks:v1"
   :query      {}
   :value      [{"id" 1 "owner" "Alice" "status" "todo"}
                {"id" 2 "owner" "Bob"   "status" "done"}]
   :shape      {:id-key "id"
                :fields [{:key "owner"  :type :string}
                         {:key "status" :type :string}]}})

(deftest accepted-contract-valid-payload-installs
  (let [r (expecting base valid-accepted)
        {:keys [resource effects]} (resource/step r [:response valid-accepted])]
    (is (= valid-accepted (:last-accepted resource)) "a contract-valid payload installs")
    (is (nil? (:last-failure resource)))
    (is (nil? (:active-request resource)))
    (is (= [[:notify-consumers {:resource resource}]] effects))))

(deftest accepted-missing-field-is-a-contract-failure
  (let [bad (update-in valid-accepted [:value 0] dissoc "status")   ; row 0 loses a declared field
        {:keys [resource effects]} (resource/step (expecting base bad) [:response bad])]
    (testing "records a :contract failure, clears the request, does NOT install (keep-stale)"
      (is (= :contract (get-in resource [:last-failure :failure])))
      (is (nil? (:last-accepted resource)))
      (is (nil? (:active-request resource))))
    (testing "the failure carries the errors that adjudicated it (which rule fired for which
              payload is asserted directly in barebuild.validation-test)"
      (is (seq (get-in resource [:last-failure :errors]))))
    (testing "consumers are notified"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest a-shape-without-a-field-list-does-not-install
  (testing "a shape carrying no field list declares nothing to check the rows against, so the
            payload cannot be trusted and is not installed"
    (let [bad (assoc valid-accepted :shape {:id-key "id"})
          {:keys [resource]} (resource/step (expecting base bad) [:response bad])]
      (is (= :contract (get-in resource [:last-failure :failure])))
      (is (nil? (:last-accepted resource)))))
  (testing "a shape declaring an EMPTY field list makes the opposite claim, that there is
            nothing to check, and installs"
    (let [ok (assoc valid-accepted :shape {:id-key "id" :fields []})
          {:keys [resource]} (resource/step (expecting base ok) [:response ok])]
      (is (= ok (:last-accepted resource)))
      (is (nil? (:last-failure resource))))))

(deftest network-failed-records-network-failure-and-notifies
  (let [prior (assoc base :last-accepted accepted
                          :url-intent {:sort "owner"}
                          :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step prior [:network-failed {:request/id "tasks:1"
                                                                          :error {:kind :offline}}])]
    (testing "records a :network failure carrying the error kind and the adjudicated query"
      (is (= {:failure :network :for :read :error {:kind :offline} :query {:sort "owner"}} (:last-failure resource))))
    (testing ":active-request cleared; :last-accepted left untouched (keep-stale)"
      (is (nil? (:active-request resource)))
      (is (= accepted (:last-accepted resource))))
    (testing "consumers are notified with the updated resource"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest stale-network-failure-is-dropped
  (let [prior (assoc base :last-accepted accepted
                          :active-request {:request/id "tasks:2" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step prior [:network-failed {:request/id "tasks:1"
                                                                          :error {:kind :offline}}])]
    (testing "a failure for a superseded request does not touch state (T12)"
      (is (= {:request/id "tasks:2" :query {:sort "owner"}} (:active-request resource)))
      (is (nil? (:last-failure resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-failure}]] effects)))))

(deftest protocol-failed-records-protocol-failure-and-notifies
  (let [prior  (assoc base :last-accepted accepted
                           :url-intent {:sort "owner"}
                           :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        marker {:protocol-failure {:reason :unknown-outcome :outcome "banana"}
                :request/id "tasks:1"}
        {:keys [resource effects]} (resource/step prior [:protocol-failed marker])]
    (testing "records a :protocol failure carrying the parse-failure detail and adjudicated query"
      (is (= {:failure :protocol :for :read :detail {:reason :unknown-outcome :outcome "banana"} :query {:sort "owner"}}
             (:last-failure resource))))
    (testing ":active-request cleared; :last-accepted left untouched (keep-stale)"
      (is (nil? (:active-request resource)))
      (is (= accepted (:last-accepted resource))))
    (testing "consumers are notified with the updated resource"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest stale-protocol-failure-is-dropped
  (let [prior  (assoc base :last-accepted accepted
                           :active-request {:request/id "tasks:2" :query {:sort "owner"}})
        marker {:protocol-failure {:reason :unknown-outcome} :request/id "tasks:1"}
        {:keys [resource effects]} (resource/step prior [:protocol-failed marker])]
    (testing "a protocol failure for a superseded request does not touch state (T14)"
      (is (= {:request/id "tasks:2" :query {:sort "owner"}} (:active-request resource)))
      (is (nil? (:last-failure resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-failure}]] effects)))))

;; --- C3: echo-adoption (T5/T6) + trailing-fetch + failure adjudication ------

(def echoed
  {:outcome    :accepted
   :request/id "tasks:1"
   :query      {:sort "owner" :direction "asc"}   ; server normalized: it added :direction
   :value      [{"id" 1 "owner" "Alice"}]
   :shape      {:id-key "id" :fields [{:key "owner" :type :string}]}})

(deftest accepted-adopts-normalized-echo-and-corrects-url
  (let [r (assoc base :url-intent {:sort "owner"}          ; sent without direction
                      :request-count 1
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step r [:response echoed])]
    (testing "not drifted -> the normalized echo is adopted into :url-intent (T5)"
      (is (= {:sort "owner" :direction "asc"} (:url-intent resource))))
    (testing "a corrective :replace url-write reflects the adopted echo"
      (is (some (fn [[fx m]] (and (= :url-write fx)
                                  (= {:sort "owner" :direction "asc"} (:params m))
                                  (= :replace (:mode m))))
                effects)))
    (testing "the value installs, the request clears, and nothing trails (echo answered the intent)"
      (is (= echoed (:last-accepted resource)))
      (is (nil? (:active-request resource)))
      (is (not-any? (fn [[fx _]] (= :fetch fx)) effects)))))

(deftest accepted-drifted-keeps-intent-and-trails-fetch
  (let [r (assoc base :url-intent {:sort "start"}          ; user moved on during the flight
                      :request-count 1
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step r [:response echoed])]
    (testing "drifted -> the stale echo is NOT adopted; :url-intent untouched, no url-write (T6)"
      (is (= {:sort "start"} (:url-intent resource)))
      (is (not-any? (fn [[fx _]] (= :url-write fx)) effects)))
    (testing "a trailing fetch fires once for the NEW intent under a fresh id"
      (is (= {:request/id "tasks:2" :query {:sort "start"}} (:active-request resource)))
      (is (some (fn [[fx m]] (and (= :fetch fx)
                                  (= "/api/tasks?requestId=tasks:2&sort=start" (:url m))
                                  (= "tasks:2" (:request/id m))))
                effects)))))

(deftest network-failure-adjudicates-its-query-no-auto-retry
  (let [r (assoc base :url-intent {:sort "owner"}
                      :request-count 1
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step r [:network-failed {:request/id "tasks:1"
                                                                      :error {:kind :offline}}])]
    (testing "the failure records its query, so pending? sees the failed intent as adjudicated"
      (is (= {:sort "owner"} (get-in resource [:last-failure :query]))))
    (testing "no trailing fetch — a failed request is not auto-retried (A.1)"
      (is (nil? (:active-request resource)))
      (is (not-any? (fn [[fx _]] (= :fetch fx)) effects)))))

(deftest network-failure-with-drift-trails-the-new-intent
  (let [r (assoc base :url-intent {:sort "start"}          ; intent drifted during the flight
                      :request-count 1
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step r [:network-failed {:request/id "tasks:1"
                                                                      :error {:kind :offline}}])]
    (testing "the failed query is adjudicated, but intent moved -> fetch the new intent once"
      (is (= {:request/id "tasks:2" :query {:sort "start"}} (:active-request resource)))
      (is (some (fn [[fx m]] (and (= :fetch fx)
                                  (= "/api/tasks?requestId=tasks:2&sort=start" (:url m))))
                effects)))))

;; --- C4: rejection revert (T8/T9/T10) --------------------------------------

(deftest rejected-about-current-intent-reverts-to-last-accepted
  (let [good     {:outcome :accepted :request/id "tasks:1"
                  :query   {:sort "owner" :direction "asc"}
                  :value   [{"id" 1 "owner" "Alice"}]
                  :shape   {:id-key "id" :fields [{:key "owner" :type :string}]}}
        bad-q    {:sort "bogus" :direction "asc"}
        r        (assoc base :last-accepted good
                             :url-intent bad-q                         ; user sorted to a bad field
                             :request-count 2
                             :active-request {:request/id "tasks:2" :query bad-q})
        rejected {:outcome :rejected :request/id "tasks:2" :query bad-q
                  :error   {:code :invalid-query :message "nope"}}
        {:keys [resource effects]} (resource/step r [:response rejected])]
    (testing "the rejection is recorded and the request cleared"
      (is (= {:failure :rejected :for :read :response rejected :query (:query rejected)} (:last-failure resource)))
      (is (nil? (:active-request resource))))
    (testing "url-intent reverts to the last accepted query (T8)"
      (is (= {:sort "owner" :direction "asc"} (:url-intent resource))))
    (testing "a corrective :replace url-write restores the URL and lands before the notify, so a
              consumer is handed the reverted value with the address bar already matching it.
              Nothing trails, and the good value is kept"
      (is (= [[:url-write {:resource/id "tasks"
                           :params      {:sort "owner" :direction "asc"}
                           :mode        :replace}]
              [:notify-consumers {:resource resource}]]
             effects))
      (is (= good (:last-accepted resource))))))

(deftest rejected-first-load-has-no-revert
  (let [bad-q    {:sort "bogus"}
        r        (assoc base :url-intent bad-q                         ; booted straight into a bad sort
                             :request-count 1
                             :active-request {:request/id "tasks:1" :query bad-q})
        rejected {:outcome :rejected :request/id "tasks:1" :query bad-q
                  :error   {:code :invalid-query :message "nope"}}
        {:keys [resource effects]} (resource/step r [:response rejected])]
    (testing "no accepted query exists -> intent left as-is; the failure adjudicates it (T9)"
      (is (= bad-q (:url-intent resource)))
      (is (nil? (:last-accepted resource))))
    (testing "no url-write, no trailing fetch, request cleared"
      (is (not-any? (fn [[fx _]] (#{:url-write :fetch} fx)) effects))
      (is (nil? (:active-request resource))))))

(deftest rejected-stale-yields-to-newer-intent
  (let [good     {:outcome :accepted :request/id "tasks:1"
                  :query {} :value [] :shape {:id-key "id" :fields []}}
        r        (assoc base :last-accepted good
                             :url-intent {:sort "start"}               ; user has already moved on
                             :request-count 2
                             :active-request {:request/id "tasks:2" :query {:sort "bogus"}})
        rejected {:outcome :rejected :request/id "tasks:2" :query {:sort "bogus"}
                  :error   {:code :invalid-query :message "nope"}}
        {:keys [resource effects]} (resource/step r [:response rejected])]
    (testing "an older rejection has no authority over newer intent: no revert (T10)"
      (is (= {:sort "start"} (:url-intent resource)))
      (is (not-any? (fn [[fx _]] (= :url-write fx)) effects)))
    (testing "the newer intent is unanswered -> a trailing fetch fires for it"
      (is (= {:request/id "tasks:3" :query {:sort "start"}} (:active-request resource)))
      (is (some (fn [[fx m]] (and (= :fetch fx)
                                  (= "/api/tasks?requestId=tasks:3&sort=start" (:url m))))
                effects)))))

;; --- T15: disconnect aborts the in-flight request --------------------------

(deftest disconnected-aborts-active-request
  (let [r (assoc base :request-count 1
                      :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step r [:disconnected {}])]
    (testing "the in-flight request is aborted by id and cleared from the value"
      (is (= [[:abort {:request/id "tasks:1"}]] effects))
      (is (nil? (:active-request resource))))))

(deftest disconnected-with-no-request-is-a-noop
  (is (= {:resource base :effects []}
         (resource/step base [:disconnected {}]))
      "nothing in flight -> disconnect is a clean no-op"))

;; --- W1b: writes — submit-write / write-ack / write-failed -----------------

(deftest submit-write-starts-write-and-emits-write-effect
  (let [{:keys [resource effects]} (resource/step base [:submit-write {:op :delete :id 7}])]
    (testing "an active write is recorded under a namespaced id, carrying the payload"
      (is (= {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {}} (:active-write resource)))
      (is (true? (resource/writing? resource))))
    (testing "notify first (so the button disables), then the :write effect — which carries the
              request step already decided, not the payload for the executor to interpret"
      (is (= [[:notify-consumers {:resource resource}]
              [:write {:request/id "tasks:w1"
                       :method   "DELETE"
                       :url      "/api/tasks/7?requestId=tasks:w1"}]]
             effects)))))

(deftest submit-write-decides-the-request-in-step
  (testing "a create rides the same spine and step — not the executor — resolves it to a
            POST with a body; the payload stays on :active-write, never in the effect"
    (let [record  {"owner" "Zoe" "start" "2026-03-01" "end" "2026-03-10" "status" "todo"}
          payload {:op :create :record record}
          {:keys [resource effects]} (resource/step base [:submit-write payload])]
      (is (= {:request/id "tasks:w1" :payload payload :query {}} (:active-write resource)))
      (is (= [[:notify-consumers {:resource resource}]
              [:write {:request/id "tasks:w1"
                       :method   "POST"
                       :url      "/api/tasks?requestId=tasks:w1"
                       :body     record
                       :headers  {"content-type" "application/json"}}]]
             effects)))))

(deftest submit-write-update-addresses-the-member-with-a-body
  (testing "update is one row in the op table: PUT, member-addressed, body-carrying"
    (let [record  {"title" "Ship it" "status" "done"}
          {:keys [effects]} (resource/step base [:submit-write {:op :update :id 7 :record record}])]
      (is (= [:write {:request/id "tasks:w1"
                      :method   "PUT"
                      :url      "/api/tasks/7?requestId=tasks:w1"
                      :body     record
                      :headers  {"content-type" "application/json"}}]
             (second effects))))))

(deftest submit-write-of-an-unsupported-op-leaves-the-resource-untouched
  (let [{:keys [resource effects]} (resource/step base [:submit-write {:op :frobnicate :id 7}])]
    (testing "an op the table can't build never starts a write — no :active-write, no id burned.
              Starting one would set writing? with no request to answer it, and the single-flight
              guard would then reject every later write for the life of the element"
      (is (= base resource))
      (is (false? (resource/writing? resource))))
    (testing "it surfaces as its own diagnostic, distinct from the double-click case"
      (is (= [[:diagnostic {:code :unsupported-write}]] effects)))))

(deftest submit-write-without-a-member-id-leaves-the-resource-untouched
  (let [{:keys [resource effects]} (resource/step base [:submit-write {:op :delete}])]
    (testing "an unbuildable member op is refused before start-write, exactly like an
              unknown op — no :active-write, so the single-flight slot stays free"
      (is (= base resource))
      (is (false? (resource/writing? resource))))
    (is (= [[:diagnostic {:code :unsupported-write}]] effects))))

;; --- U1b: write-request — the op table as data -----------------------------

(deftest write-request-resolves-each-op
  (let [record {"title" "Ship it"}]
    (testing "create: collection-addressed POST carrying the record"
      (is (= {:request/id "tasks:w1"
              :method   "POST"
              :url      "/api/tasks?requestId=tasks:w1"
              :body     record
              :headers  {"content-type" "application/json"}}
             (resource/write-request base "tasks:w1" {:op :create :record record} nil))))
    (testing "update: member-addressed PUT carrying the record"
      (is (= {:request/id "tasks:w1"
              :method   "PUT"
              :url      "/api/tasks/7?requestId=tasks:w1"
              :body     record
              :headers  {"content-type" "application/json"}}
             (resource/write-request base "tasks:w1" {:op :update :id 7 :record record} nil))))
    (testing "delete: member-addressed DELETE, no body and so no content-type"
      (is (= {:request/id "tasks:w1"
              :method   "DELETE"
              :url      "/api/tasks/7?requestId=tasks:w1"}
             (resource/write-request base "tasks:w1" {:op :delete :id 7} nil))))))

(deftest write-request-carries-the-current-query
  (testing "the write is issued for the current view, so its query rides the URL like a read's"
    (is (= "/api/tasks?requestId=tasks:w1&direction=desc&sort=owner"
           (:url (resource/write-request base "tasks:w1"
                                         {:op :create :record {"title" "x"}}
                                         {:sort "owner" :direction "desc"}))))))

(deftest write-request-of-an-unknown-op-is-nil
  (testing "the table is the whole write vocabulary — nil is how step learns it can't build one"
    (is (nil? (resource/write-request base "tasks:w1" {:op :frobnicate :id 7} nil)))
    (is (nil? (resource/write-request base "tasks:w1" {:id 7} nil)))))

(deftest write-request-of-a-member-op-without-an-id-is-nil
  (testing "no id would address the collection instead — a DELETE that reads as
            'delete everything' to any server implementing collection-level delete"
    (is (nil? (resource/write-request base "tasks:w1" {:op :delete} nil)))
    (is (nil? (resource/write-request base "tasks:w1" {:op :update :record {"title" "x"}} nil)))
    (is (nil? (resource/write-request base "tasks:w1" {:op :delete :id ""} nil))))
  (testing "a collection op needs no id"
    (is (some? (resource/write-request base "tasks:w1" {:op :create :record {"title" "x"}} nil))))
  (testing "0 is a legitimate id, not a missing one"
    (is (= "/api/tasks/0?requestId=tasks:w1"
           (:url (resource/write-request base "tasks:w1" {:op :delete :id 0} nil))))))

(deftest write-request-passes-the-record-through-unconverted
  (testing "the body stays a CLJS value — JSON serialization is the executor's job, and an
            =-comparable effect is what makes the write spine testable at all"
    (let [record {"title" "Ship it" "status" "todo"}
          req    (resource/write-request base "tasks:w1" {:op :create :record record} nil)]
      (is (map? (:body req)))
      (is (= record (:body req))))))

;; --- request config: step emits complete requests --------------------------

(def ^:private configured
  (assoc base :url-intent {}
              :credentials "include"
              :headers {"x-api-key" "k"}
              :timeout 5000))

(deftest request-config-rides-every-request
  (testing "a read: the :fetch value step returns already describes the whole call, so the
            executor translates it and decides nothing"
    (let [{:keys [effects]} (resource/step configured [:connected {}])
          req               (effect-value effects :fetch)]
      (is (= "include" (:credentials req)))
      (is (= {"x-api-key" "k"} (:headers req)))))
  (testing "a write: the same config, with the protocol's content-type merged on top"
    (let [{:keys [effects]} (resource/step configured [:submit-write {:op :create
                                                                     :record {"title" "x"}}])
          req               (effect-value effects :write)]
      (is (= "include" (:credentials req)))
      (is (= {"x-api-key" "k" "content-type" "application/json"} (:headers req)))))
  (testing "the budget is on both, so a hung write cannot block every later write either"
    (let [read-req  (effect-value (:effects (resource/step configured [:connected {}])) :fetch)
          write-req (effect-value (:effects (resource/step configured [:submit-write
                                                                      {:op :delete :id 7}]))
                                  :write)]
      (is (= 5000 (:timeout read-req)))
      (is (= 5000 (:timeout write-req))))))

(deftest an-unconfigured-resource-emits-the-requests-it-always-did
  (testing "no config -> no :credentials and no :headers on a read, so adding config
            changed nothing for a resource that declares none"
    (let [{:keys [effects]} (resource/step (assoc base :url-intent {}) [:connected {}])
          req               (effect-value effects :fetch)]
      (is (nil? (:credentials req)))
      (is (nil? (:headers req))))))

(deftest submit-write-while-writing-is-a-noop
  (let [r (assoc base :write-count 1
                      :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:submit-write {:op :delete :id 9}])]
    (testing "a second write while one is in flight does not start another (guards the double-click)"
      (is (= r resource))
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-ack-accepted-installs-the-returned-state
  (let [r       (assoc base :url-intent {:sort "owner"} :write-count 1
                            :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        payload (assoc accepted :query {:sort "owner"} :request/id "tasks:w1")
        {:keys [resource effects]} (resource/step r [:write-ack payload])]
    (testing "the write clears -> writing? false"
      (is (nil? (:active-write resource)))
      (is (false? (resource/writing? resource))))
    (testing "the returned envelope is installed directly — no refetch, just notify"
      (is (= payload (:last-accepted resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-accepted-adopts-a-clamped-echo
  (let [r       (assoc base :url-intent {:page "4"} :write-count 1
                            :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:page "4"}})
        payload (assoc accepted :query {:page "3"} :request/id "tasks:w1")
        {:keys [resource effects]} (resource/step r [:write-ack payload])]
    (testing "not drifted, so the clamped echo is adopted and written to the URL"
      (is (= {:page "3"} (:url-intent resource)))
      (is (some (fn [[fx m]] (and (= :url-write fx) (= {:page "3"} (:params m)))) effects))
      (is (= payload (:last-accepted resource))))))

(deftest write-ack-accepted-does-not-adopt-a-drifted-echo
  (let [r       (assoc base :url-intent {:sort "start"} :request-count 3 :write-count 1
                            :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        payload (assoc accepted :query {:sort "owner"} :request/id "tasks:w1")
        {:keys [resource effects]} (resource/step r [:write-ack payload])]
    (testing "the user moved during the write, so its old-query echo is not adopted"
      (is (= {:sort "start"} (:url-intent resource)))
      (is (not-any? (fn [[fx _]] (= :url-write fx)) effects)))
    (testing "a trailing fetch fires for the new intent under a fresh id"
      (is (= {:request/id "tasks:4" :query {:sort "start"}} (:active-request resource)))
      (is (some (fn [[fx m]] (and (= :fetch fx)
                                  (= "/api/tasks?requestId=tasks:4&sort=start" (:url m))))
                effects)))))

(deftest write-ack-accepted-with-broken-contract-keeps-stale
  (let [r      (assoc base :url-intent {:sort "owner"} :last-accepted accepted :write-count 1
                           :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        broken (assoc accepted :query {:sort "owner"} :request/id "tasks:w1" :value [{"owner" "Alice"}])
        {:keys [resource effects]} (resource/step r [:write-ack broken])]
    (testing "the returned envelope fails the shape, so it is not installed"
      (is (= accepted (:last-accepted resource)))
      (is (= :contract (get-in resource [:last-failure :failure]))))
    (testing "the write clears and the stale view is kept"
      (is (nil? (:active-write resource))))
    (testing "a re-read follows: the write landed, but the envelope describing the new state was
              unusable, so the current view is not backed by valid data. It cannot loop, since a
              read that also fails the contract records a :read failure that answers the intent"
      (is (= :notify-consumers (ffirst effects)))
      (is (= :fetch (first (second effects)))))))

(deftest failure-of-a-normalized-query-answers-the-intent-and-does-not-spin
  (testing "a server may honor less of a query than was asked and echo only what it honored. The
            failure records what was *asked*, so the intent reads as answered and no trailing
            fetch reissues the same doomed request. Recording the echo here spun forever"
    (let [asked  {:fixture "contract"}
          r      (assoc base :url-intent asked :request-count 1
                             :active-request {:request/id "tasks:1" :query asked})
          broken (assoc accepted :request/id "tasks:1" :query {} :value [{"id" 1}])
          {:keys [resource effects]} (resource/step r [:response broken])]
      (is (= :contract (get-in resource [:last-failure :failure])))
      (is (= asked (get-in resource [:last-failure :query]))
          "the failure names the query the request was issued for, not the server's echo")
      (is (false? (resource/pending? resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)
          "no trailing fetch: the intent has been answered, badly")))
  (testing "the same holds for a rejection whose echo came back narrower than the intent"
    (let [asked {:sort "bogus" :extra "dropped"}
          r     (assoc base :url-intent asked :request-count 1
                            :active-request {:request/id "tasks:1" :query asked})
          {:keys [resource effects]} (resource/step r [:response {:outcome :rejected
                                                                  :request/id "tasks:1"
                                                                  :query {:sort "bogus"}
                                                                  :error {:code :invalid-query}}])]
      (is (= asked (get-in resource [:last-failure :query])))
      (is (false? (resource/pending? resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-rejected-records-failure-keeps-stale-and-clears-writing
  (let [r   (assoc base :last-accepted accepted
                        :write-count 1
                        :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7}
                                       :query {:sort "owner"}})
        ack {:outcome :rejected :request/id "tasks:w1" :query {:sort "owner"}
             :error   {:code :conflict :message "nope"}}
        {:keys [resource effects]} (resource/step r [:write-ack ack])]
    (testing "the rejection is recorded as a :rejected failure wrapping the ack, with the query
              it concerns lifted to the top level like every other failure"
      (is (= {:failure :rejected :for :write :response ack :query {:sort "owner"}} (:last-failure resource))))
    (testing "writing? clears so the button re-enables (the regression that bit twice)"
      (is (nil? (:active-write resource)))
      (is (false? (resource/writing? resource))))
    (testing "pessimistic keep-stale: last-accepted intact, no refetch"
      (is (= accepted (:last-accepted resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-for-superseded-write-is-dropped
  (let [r (assoc base :write-count 2
                      :active-write {:request/id "tasks:w2" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:write-ack {:outcome :accepted :request/id "tasks:w1"}])]
    (testing "an ack that doesn't answer the in-flight write never touches state"
      (is (= {:request/id "tasks:w2" :payload {:op :delete :id 7}} (:active-write resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-failed-records-failure-keeps-stale-and-clears-writing
  (let [aw {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}}
        r  (assoc base :last-accepted accepted :write-count 1 :active-write aw)
        {:keys [resource effects]} (resource/step r [:write-failed {:request/id "tasks:w1"
                                                                    :error    {:kind :offline}}])]
    (testing "records a :network failure carrying the error kind and the in-flight write"
      (is (= {:failure :network :for :write :error {:kind :offline} :write aw :query {:sort "owner"}}
             (:last-failure resource))))
    (testing "writing? clears; last-accepted kept (pessimistic -> nothing to roll back)"
      (is (nil? (:active-write resource)))
      (is (= accepted (:last-accepted resource))))
    (testing "consumers are notified, and a re-read follows to find out whether the write landed"
      (is (= [:notify-consumers {:resource (dissoc resource :request-count :active-request)}]
             (first effects)))
      (is (= :fetch (first (second effects)))))))

;; --- reconciling a write whose outcome is unknown --------------------------

(defn- write-failure-effects
  "The effects of `failure-payload` landing on a resource with a live write and a good last view."
  [failure-payload]
  (:effects (resource/step (assoc base :last-accepted accepted
                                       :url-intent (:query accepted)
                                       :write-count 1
                                       :active-write {:request/id "tasks:w1"
                                                      :payload {:op :create :record {"t" "x"}}})
                           [:write-failed (merge {:request/id "tasks:w1"} failure-payload)])))

(deftest an-unknown-outcome-write-is-reconciled-by-a-re-read
  (testing "a timed-out write may well have committed before the client gave up. Leaving the old
            view on screen would hide a row the server has, and the retry the failure invites
            would then write it twice"
    (is (some? (effect-value (write-failure-effects {:error {:kind :timeout :after 60000}}) :fetch))))
  (testing "the same holds for a dropped connection and for an unreadable body: the server may
            have committed either way, so both observe rather than assume"
    (is (some? (effect-value (write-failure-effects {:error {:kind :offline}}) :fetch)))
    (is (some? (effect-value (write-failure-effects {:protocol-failure {:reason :empty-body}})
                             :fetch)))))

(deftest a-rejected-write-is-not-reconciled
  (testing "an explicit rejection is the server saying no, the one write failure whose outcome is
            known, so re-reading would only cost a round trip"
    (let [{:keys [effects]} (resource/step (assoc base :last-accepted accepted
                                                       :url-intent (:query accepted)
                                                       :write-count 1
                                                       :active-write {:request/id "tasks:w1"
                                                                      :payload {:op :delete :id 7}})
                                           [:write-ack {:request/id "tasks:w1"
                                                        :outcome  :rejected
                                                        :error    {:code :conflict}}])]
      (is (nil? (effect-value effects :fetch))))))

(deftest reconciling-does-not-open-a-second-request
  (testing "a read already in flight is the observation we need, so single-flight holds"
    (let [{:keys [effects]}
          (resource/step (assoc base :last-accepted accepted
                                     :write-count 1
                                     :active-write {:request/id "tasks:w1" :payload {:op :delete :id 7}}
                                     :request-count 1
                                     :active-request {:request/id "tasks:1" :query {}})
                         [:write-failed {:request/id "tasks:w1" :error {:kind :offline}}])]
      (is (nil? (effect-value effects :fetch))))))

(deftest write-failed-for-superseded-write-is-dropped
  (let [r (assoc base :active-write {:request/id "tasks:w2" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:write-failed {:request/id "tasks:w1"
                                                                    :error    {:kind :offline}}])]
    (testing "a failure for a superseded write does not touch state"
      (is (= {:request/id "tasks:w2" :payload {:op :delete :id 7}} (:active-write resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-failed-protocol-is-labelled-protocol-not-network
  (let [aw {:request/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}}
        r  (assoc base :last-accepted accepted :write-count 1 :active-write aw)
        {:keys [resource]} (resource/step r [:write-failed {:request/id "tasks:w1"
                                                            :protocol-failure {:reason :empty-body}}])]
    (testing "a broken ack envelope is a :protocol failure carrying its detail, not a nil-error :network one"
      (is (= {:failure :protocol :for :write :detail {:reason :empty-body} :write aw :query {:sort "owner"}}
             (:last-failure resource))))))

(deftest unknown-event-leaves-the-resource-untouched
  (is (= base (:resource (resource/step base [:some-future-event {}])))
      "events step doesn't handle leave the resource untouched")
  (is (= [[:diagnostic {:code :unknown-event :detail {:event :some-future-event}}]]
         (:effects (resource/step base [:some-future-event {}])))
      "and are reported rather than silently swallowed"))

;; --- every failure names the query it concerns -----------------------------

(defn- failure-of
  "The :last-failure a resource lands in after `event`, starting from `r`."
  [r event]
  (:last-failure (:resource (resource/step r event))))

(deftest every-failure-carries-its-query-at-the-top-level
  (testing "the query used to hide under :response for rejected and contract, at :query for
            network and protocol, and nowhere at all for writes. One place means a reader never
            has to know the tag to find it"
    (let [q        {:sort "owner"}
          in-fligh (assoc base :url-intent q :request-count 1
                               :active-request {:request/id "tasks:1" :query q})
          writing  (assoc base :url-intent q :last-accepted (assoc accepted :query q)
                               :write-count 1
                               :active-write {:request/id "tasks:w1" :query q
                                              :payload {:op :delete :id 7}})]
      (testing "read: rejected"
        (is (= q (:query (failure-of in-fligh [:response {:outcome :rejected :request/id "tasks:1"
                                                          :query q :error {:code :bad}}])))))
      (testing "read: contract"
        (is (= q (:query (failure-of in-fligh [:response (assoc accepted :request/id "tasks:1"
                                                                :query q :value [{"id" 1}])])))))
      (testing "read: network"
        (is (= q (:query (failure-of in-fligh [:network-failed {:request/id "tasks:1"
                                                                :error {:kind :offline}}])))))
      (testing "read: protocol"
        (is (= q (:query (failure-of in-fligh [:protocol-failed {:request/id "tasks:1"
                                                                 :protocol-failure {:reason :empty-body}}])))))
      (testing "write: rejected ack"
        (is (= q (:query (failure-of writing [:write-ack {:request/id "tasks:w1" :outcome :rejected
                                                          :query q :error {:code :conflict}}])))))
      (testing "write: network"
        (is (= q (:query (failure-of writing [:write-failed {:request/id "tasks:w1"
                                                             :error {:kind :timeout :after 60000}}])))))
      (testing "write: protocol"
        (is (= q (:query (failure-of writing [:write-failed {:request/id "tasks:w1"
                                                             :protocol-failure {:reason :empty-body}}]))))))))

(deftest a-write-failure-does-not-answer-a-read-intent
  (testing "a write failure names the query it was issued for, but that says nothing about
            whether the current intent has been fetched, so pending? must ignore it. Counting it
            would leave an unfetched view looking answered"
    (let [r (assoc base :url-intent {:sort "owner"}
                        :last-failure {:failure :network :for :write :error {:kind :offline}
                                       :query {:sort "owner"}})]
      (is (true? (resource/pending? r))))
    (testing "the same failure tagged :read does answer it. The tag is what decides, not whether
              some other key happens to be present"
      (let [r (assoc base :url-intent {:sort "owner"}
                          :last-failure {:failure :network :for :read :error {:kind :offline}
                                         :query {:sort "owner"}})]
        (is (false? (resource/pending? r)))))))

(deftest a-value-that-has-fetched-nothing-is-pending-whatever-its-intent
  (testing "answering is holding an answer, not merely agreeing with a query nobody asked. A
            fresh resource has neither an accepted envelope nor a failure, so it is pending
            however its intent is spelled, and boot needs no special case to report loading"
    (doseq [intent [nil {} {:sort "owner"}]]
      (is (true? (resource/pending? (assoc base :url-intent intent)))
          (str "unfetched with intent " (pr-str intent)))))
  (testing "an answer that matches settles it, whichever kind of answer it is"
    (is (false? (resource/pending? (assoc base :url-intent {} :last-accepted (assoc accepted :query {})))))
    (is (false? (resource/pending? (assoc base :url-intent {}
                                               :last-failure {:failure :rejected :for :read :query {}}))))))

;; --- project: what a consumer is allowed to see ----------------------------

(def ^:private view-keys #{:accepted :failure :intent :pending? :writing?})

(deftest project-always-has-the-same-shape
  (testing "absent values arrive as nil or false rather than as missing keys, so a consumer can
            destructure without checking first and two views compare meaningfully"
    (is (= view-keys (set (keys (resource/project {})))))
    (is (= view-keys (set (keys (resource/project (assoc base :last-accepted accepted
                                                              :url-intent {:sort "owner"}))))))))

(deftest project-hides-the-machine
  (testing "the resource's own bookkeeping is not part of the contract. Handing it to consumers
            makes it a promise, and 1.0 would freeze internals the runtime needs to keep changing"
    (let [view (resource/project (assoc base
                                        :url-intent     {}
                                        :credentials    "include"
                                        :headers        {"authorization" "Bearer t"}
                                        :timeout        5000
                                        :history-policy {:navigation :push}
                                        :request-count  3
                                        :write-count    1
                                        :active-request {:request/id "tasks:3" :query {}}
                                        :active-write   {:request/id "tasks:w1"}))]
      (doseq [k [:endpoint :credentials :headers :timeout :history-policy :request-count
                 :write-count :active-request :active-write :resource/id]]
        (is (not (contains? view k)) (str k " leaked into the consumer's view"))))))

(deftest project-carries-what-a-consumer-renders-from
  (let [failure {:failure :network :error {:kind :offline} :query {}}
        view    (resource/project (assoc base :last-accepted accepted
                                              :last-failure  failure
                                              :url-intent    {:sort "owner"}))]
    (is (= (dissoc accepted :request/id) (:accepted view))
        "the accepted envelope passes through, minus the id of the exchange that fetched it")
    (is (= failure (:failure view)) "so does the failure")
    (testing "intent passes through untouched, nil included: a resource that has not booted and
              one booted with an empty query are different states, and flattening them would
              hide the difference from a consumer that gates on the URL"
      (is (= {:sort "owner"} (:intent view)))
      (is (nil? (:intent (resource/project {})))))))

(deftest project-derives-the-two-flags-as-booleans
  (testing "answered intent and no write in flight -> both false, checked strictly because a
            truthy non-boolean would pass a consumer's `if` and fail its `false?`"
    (let [idle (resource/project (assoc base :last-accepted accepted
                                             :url-intent (:query accepted)))]
      (is (false? (:pending? idle)))
      (is (false? (:writing? idle)))))
  (testing "a read and a write both in flight -> both true"
    (let [busy (resource/project (assoc base :active-request {:request/id "tasks:1" :query {}}
                                             :active-write   {:request/id "tasks:w1"}))]
      (is (true? (:pending? busy)))
      (is (true? (:writing? busy))))))

(deftest project-survives-a-resource-that-has-not-booted
  (testing "the element can project before any event has run, so this must not throw"
    (is (= view-keys (set (keys (resource/project {})))))))
