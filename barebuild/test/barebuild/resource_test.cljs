(ns barebuild.resource-test
  "step tests: (step resource event) -> {:resource :effects}.
   Pure, =-asserted per event branch."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.resource :as resource]))

(def base
  {:resource/id   "tasks"
   :endpoint      "/api/tasks"
   :last-accepted nil})

(def accepted
  {:outcome    :accepted
   :request/id "req-1"
   :revision   "tasks:v1"
   :value      [{"id" 1 "owner" "Alice"}]
   :shape      {:id-key "id" :fields [{:key "owner" :type :string}]}})

(defn- expecting
  "Put r in flight for `response`: a live request whose id and query the response answers,
   so (installable? r response) holds."
  [r response]
  (assoc r :active-request {:request/id (:request/id response)
                            :query      (:query response)}))

(defn- effect-value
  "The value of the first `k` effect in `effects`, or nil when step emitted none."
  [effects k]
  (some (fn [[fx m]] (when (= k fx) m)) effects))

;; --- render-key: the change-guard projection -------------------------------

(deftest render-key-ignores-transport-ids
  (testing "two responses identical but for :request/id compare equal, so a refetch that
            returns unchanged data does not re-render"
    (is (= (resource/render-key (assoc accepted :request/id "tasks:1"))
           (resource/render-key (assoc accepted :request/id "tasks:2")))))
  (testing "a write install's :write/id is transport too — an identical read after a write
            does not re-render"
    (is (= (resource/render-key (assoc accepted :write/id "tasks:w1"))
           (resource/render-key accepted)))))

(deftest render-key-tracks-every-drawn-field
  (let [k (resource/render-key accepted)]
    (testing "a change in any field a consumer draws yields a different key"
      (is (not= k (resource/render-key (assoc accepted :value [{"id" 2 "owner" "Bob"}]))))
      (is (not= k (resource/render-key (assoc accepted :query {:sort "start"}))))
      (is (not= k (resource/render-key (assoc accepted :page-info {:page 2}))))
      (is (not= k (resource/render-key (assoc accepted :shape {:id-key "id" :fields []})))))
    (testing "revision stays in the key — a consumer may draw it, so it is not excluded"
      (is (not= k (resource/render-key (assoc accepted :revision "tasks:v2")))))))

(deftest connected-emits-fetch
  (let [r* (assoc base :request-count 1
                       :active-request {:request/id "tasks:1" :query nil})]
    (is (= {:resource r*
            :effects  [[:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1" :request/id "tasks:1"}]
                       [:notify-consumers {:resource r*}]]}
           (resource/step base [:connected {}]))
        "connect fetches the endpoint, records a fresh live request, carries the empty intent")))

(deftest connected-carries-url-intent
  (let [r (assoc base :url-intent {:sort "start" :direction "desc"})
        {:keys [resource effects]} (resource/step r [:connected {}])]
    (is (= [[:fetch {:method     "GET"
                     :url        "/api/tasks?requestId=tasks:1&direction=desc&sort=start"
                     :request/id "tasks:1"}]
            [:notify-consumers {:resource resource}]]
           effects)
        "a resource booted from a sorted URL fetches that query on connect")))

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
      (is (= [[:notify-consumers {:resource resource}]
              [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&sort=owner" :request/id "tasks:1"}]]
             effects)))))

(deftest connected-with-broken-embed-fetches
  (let [marker {:protocol-failure {:reason :unknown-outcome}}
        {:keys [resource effects]} (resource/step base [:connected {:embed marker}])]
    (is (nil? (:last-accepted resource)) "a broken embed is not installed")
    (is (= [[:diagnostic {:code :broken-embed}]
            [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1" :request/id "tasks:1"}]
            [:notify-consumers {:resource resource}]] effects)
        "diagnoses the broken embed, then falls back to a normal fetch")))

(deftest connected-with-rejected-embed-installs-failure-and-skips-fetch
  (let [r     (assoc base :url-intent {:sort "nope"})
        embed {:outcome :rejected :request/id "req-1" :query {:sort "nope"}
               :error   {:code :invalid-query :message "no"}}
        {:keys [resource effects]} (resource/step r [:connected {:embed embed}])]
    (testing "the server already adjudicated this query at render time"
      (is (= {:failure :rejected :response embed} (:last-failure resource)))
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
            [:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&sort=owner" :request/id "tasks:1"}]
            [:notify-consumers {:resource resource}]]
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
    (testing "emits a scoped :replace url-write then a fetch for the new intent"
      (is (= [[:url-write {:resource/id "tasks"
                           :params      {:sort "owner" :direction "desc"}
                           :mode        :replace}]
              [:fetch {:method     "GET"
                       :url        "/api/tasks?requestId=tasks:1&direction=desc&sort=owner"
                       :request/id "tasks:1"}]
              [:notify-consumers {:resource resource}]]
             effects)))))

(deftest intent-patch-from-empty-intent
  (let [patch {:query-patch {:sort "owner" :direction "asc"} :gesture-class nil}
        {:keys [resource]} (resource/step base [:intent-patch patch])]
    (is (= {:sort "owner" :direction "asc"} (:url-intent resource))
        "merging a patch into a nil url-intent yields just the patch")))

(deftest intent-patch-history-mode-from-gesture-class
  (let [r (assoc base :history-policy {:navigation :push})]
    (testing ":navigation resolves to :push via the resource's :history-policy"
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

(deftest url-changed-replaces-intent-and-fetches-without-writing
  (let [r (assoc base :url-intent {:sort "owner"})
        {:keys [resource effects]} (resource/step r [:url-changed {:page "2"}])]
    (testing "the URL-derived intent replaces :url-intent (not merged)"
      (is (= {:page "2"} (:url-intent resource))))
    (testing "fetches the new intent and does NOT write the URL (browser already moved)"
      (is (= [[:fetch {:method "GET" :url "/api/tasks?requestId=tasks:1&page=2" :request/id "tasks:1"}]
              [:notify-consumers {:resource resource}]] effects)))))

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
      (is (resource/installable? r {:request/id "tasks:1" :query {:sort "owner" :direction "asc"}})))
    (testing "a non-matching id never installs"
      (is (not (resource/installable? r {:request/id "tasks:9" :query {:sort "owner"}}))))))

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
  (let [r (expecting base rejected)
        {:keys [resource effects]} (resource/step r [:response rejected])]
    (testing "the rejection is recorded as a :rejected failure wrapping the response"
      (is (= {:failure :rejected :response rejected} (:last-failure resource))))
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
    (testing "the failure carries a structured :missing-field error with its path"
      (let [errs (get-in resource [:last-failure :errors])]
        (is (some #(and (= :missing-field (:code %))
                        (= [:value 0 "status"] (:path %)))
                  errs))))
    (testing "consumers are notified"
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest accepted-wrong-type-is-a-contract-failure
  (let [bad (assoc-in valid-accepted [:value 0 "owner"] 42)   ; owner should be a string
        {:keys [resource]} (resource/step (expecting base bad) [:response bad])]
    (is (= :contract (get-in resource [:last-failure :failure])))
    (is (some #(= :wrong-type (:code %)) (get-in resource [:last-failure :errors])))))

(deftest accepted-duplicate-id-is-a-contract-failure
  (let [bad (assoc-in valid-accepted [:value 1 "id"] 1)   ; both rows now id 1
        {:keys [resource]} (resource/step (expecting base bad) [:response bad])]
    (is (= :contract (get-in resource [:last-failure :failure])))
    (is (some #(= :duplicate-id (:code %)) (get-in resource [:last-failure :errors])))))

(deftest network-failed-records-network-failure-and-notifies
  (let [prior (assoc base :last-accepted accepted
                          :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        {:keys [resource effects]} (resource/step prior [:network-failed {:request/id "tasks:1"
                                                                          :error {:kind :offline}}])]
    (testing "records a :network failure carrying the error kind and the adjudicated query"
      (is (= {:failure :network :error {:kind :offline} :query {:sort "owner"}} (:last-failure resource))))
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
                           :active-request {:request/id "tasks:1" :query {:sort "owner"}})
        marker {:protocol-failure {:reason :unknown-outcome :outcome "banana"}
                :request/id "tasks:1"}
        {:keys [resource effects]} (resource/step prior [:protocol-failed marker])]
    (testing "records a :protocol failure carrying the parse-failure detail and adjudicated query"
      (is (= {:failure :protocol :detail {:reason :unknown-outcome :outcome "banana"} :query {:sort "owner"}}
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
      (is (= {:failure :rejected :response rejected} (:last-failure resource)))
      (is (nil? (:active-request resource))))
    (testing "url-intent reverts to the last accepted query (T8)"
      (is (= {:sort "owner" :direction "asc"} (:url-intent resource))))
    (testing "a corrective :replace url-write restores the URL; nothing trails; good value kept"
      (is (some (fn [[fx m]] (and (= :url-write fx)
                                  (= {:sort "owner" :direction "asc"} (:params m))
                                  (= :replace (:mode m))))
                effects))
      (is (not-any? (fn [[fx _]] (= :fetch fx)) effects))
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
      (is (= {:write/id "tasks:w1" :payload {:op :delete :id 7} :query nil} (:active-write resource)))
      (is (true? (resource/writing? resource))))
    (testing "notify first (so the button disables), then the :write effect — which carries the
              request step already decided, not the payload for the executor to interpret"
      (is (= [[:notify-consumers {:resource resource}]
              [:write {:write/id "tasks:w1"
                       :method   "DELETE"
                       :url      "/api/tasks/7?requestId=tasks:w1"}]]
             effects)))))

(deftest submit-write-decides-the-request-in-step
  (testing "a create rides the same spine and step — not the executor — resolves it to a
            POST with a body; the payload stays on :active-write, never in the effect"
    (let [record  {"owner" "Zoe" "start" "2026-03-01" "end" "2026-03-10" "status" "todo"}
          payload {:op :create :record record}
          {:keys [resource effects]} (resource/step base [:submit-write payload])]
      (is (= {:write/id "tasks:w1" :payload payload :query nil} (:active-write resource)))
      (is (= [[:notify-consumers {:resource resource}]
              [:write {:write/id "tasks:w1"
                       :method   "POST"
                       :url      "/api/tasks?requestId=tasks:w1"
                       :body     record
                       :headers  {"content-type" "application/json"}}]]
             effects)))))

(deftest submit-write-update-addresses-the-member-with-a-body
  (testing "update is one row in the op table: PUT, member-addressed, body-carrying"
    (let [record  {"title" "Ship it" "status" "done"}
          {:keys [effects]} (resource/step base [:submit-write {:op :update :id 7 :record record}])]
      (is (= [:write {:write/id "tasks:w1"
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
      (is (= {:write/id "tasks:w1"
              :method   "POST"
              :url      "/api/tasks?requestId=tasks:w1"
              :body     record
              :headers  {"content-type" "application/json"}}
             (resource/write-request base "tasks:w1" {:op :create :record record} nil))))
    (testing "update: member-addressed PUT carrying the record"
      (is (= {:write/id "tasks:w1"
              :method   "PUT"
              :url      "/api/tasks/7?requestId=tasks:w1"
              :body     record
              :headers  {"content-type" "application/json"}}
             (resource/write-request base "tasks:w1" {:op :update :id 7 :record record} nil))))
    (testing "delete: member-addressed DELETE, no body and so no content-type"
      (is (= {:write/id "tasks:w1"
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

;; --- transport config: step emits complete requests ------------------------

(def ^:private configured
  (assoc base :url-intent {}
              :transport {:credentials "include" :headers {"x-api-key" "k"} :timeout 5000}))

(deftest transport-config-rides-every-request
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
  (testing "no transport -> no :credentials and no :headers on a read, so adding config
            changed nothing for a resource that declares none"
    (let [{:keys [effects]} (resource/step (assoc base :url-intent {}) [:connected {}])
          req               (effect-value effects :fetch)]
      (is (nil? (:credentials req)))
      (is (nil? (:headers req))))))

(deftest submit-write-while-writing-is-a-noop
  (let [r (assoc base :write-count 1
                      :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:submit-write {:op :delete :id 9}])]
    (testing "a second write while one is in flight does not start another (guards the double-click)"
      (is (= r resource))
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-ack-accepted-installs-the-returned-state
  (let [r       (assoc base :url-intent {:sort "owner"} :write-count 1
                            :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        payload (assoc accepted :query {:sort "owner"} :write/id "tasks:w1")
        {:keys [resource effects]} (resource/step r [:write-ack payload])]
    (testing "the write clears -> writing? false"
      (is (nil? (:active-write resource)))
      (is (false? (resource/writing? resource))))
    (testing "the returned envelope is installed directly — no refetch, just notify"
      (is (= payload (:last-accepted resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-accepted-adopts-a-clamped-echo
  (let [r       (assoc base :url-intent {:page "4"} :write-count 1
                            :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7} :query {:page "4"}})
        payload (assoc accepted :query {:page "3"} :write/id "tasks:w1")
        {:keys [resource effects]} (resource/step r [:write-ack payload])]
    (testing "not drifted, so the clamped echo is adopted and written to the URL"
      (is (= {:page "3"} (:url-intent resource)))
      (is (some (fn [[fx m]] (and (= :url-write fx) (= {:page "3"} (:params m)))) effects))
      (is (= payload (:last-accepted resource))))))

(deftest write-ack-accepted-does-not-adopt-a-drifted-echo
  (let [r       (assoc base :url-intent {:sort "start"} :request-count 3 :write-count 1
                            :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        payload (assoc accepted :query {:sort "owner"} :write/id "tasks:w1")
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
                           :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7} :query {:sort "owner"}})
        broken (assoc accepted :query {:sort "owner"} :write/id "tasks:w1" :value [{"owner" "Alice"}])
        {:keys [resource effects]} (resource/step r [:write-ack broken])]
    (testing "the returned envelope fails the shape, so it is not installed"
      (is (= accepted (:last-accepted resource)))
      (is (= :contract (get-in resource [:last-failure :failure]))))
    (testing "the write clears and the stale view is kept"
      (is (nil? (:active-write resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-rejected-records-failure-keeps-stale-and-clears-writing
  (let [r   (assoc base :last-accepted accepted
                        :write-count 1
                        :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7}})
        ack {:outcome :rejected :write/id "tasks:w1"
             :error   {:code :conflict :message "nope"}}
        {:keys [resource effects]} (resource/step r [:write-ack ack])]
    (testing "the rejection is recorded as a :rejected failure wrapping the ack"
      (is (= {:failure :rejected :response ack} (:last-failure resource))))
    (testing "writing? clears so the button re-enables (the regression that bit twice)"
      (is (nil? (:active-write resource)))
      (is (false? (resource/writing? resource))))
    (testing "pessimistic keep-stale: last-accepted intact, no refetch"
      (is (= accepted (:last-accepted resource)))
      (is (= [[:notify-consumers {:resource resource}]] effects)))))

(deftest write-ack-for-superseded-write-is-dropped
  (let [r (assoc base :write-count 2
                      :active-write {:write/id "tasks:w2" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:write-ack {:outcome :accepted :write/id "tasks:w1"}])]
    (testing "an ack that doesn't answer the in-flight write never touches state"
      (is (= {:write/id "tasks:w2" :payload {:op :delete :id 7}} (:active-write resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-failed-records-failure-keeps-stale-and-clears-writing
  (let [aw {:write/id "tasks:w1" :payload {:op :delete :id 7}}
        r  (assoc base :last-accepted accepted :write-count 1 :active-write aw)
        {:keys [resource effects]} (resource/step r [:write-failed {:write/id "tasks:w1"
                                                                    :error    {:kind :offline}}])]
    (testing "records a :network failure carrying the error kind and the in-flight write"
      (is (= {:failure :network :error {:kind :offline} :write aw} (:last-failure resource))))
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
                                       :active-write {:write/id "tasks:w1"
                                                      :payload {:op :create :record {"t" "x"}}})
                           [:write-failed (merge {:write/id "tasks:w1"} failure-payload)])))

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
                                                       :active-write {:write/id "tasks:w1"
                                                                      :payload {:op :delete :id 7}})
                                           [:write-ack {:write/id "tasks:w1"
                                                        :outcome  :rejected
                                                        :error    {:code :conflict}}])]
      (is (nil? (effect-value effects :fetch))))))

(deftest reconciling-does-not-open-a-second-request
  (testing "a read already in flight is the observation we need, so single-flight holds"
    (let [{:keys [effects]}
          (resource/step (assoc base :last-accepted accepted
                                     :write-count 1
                                     :active-write {:write/id "tasks:w1" :payload {:op :delete :id 7}}
                                     :request-count 1
                                     :active-request {:request/id "tasks:1" :query {}})
                         [:write-failed {:write/id "tasks:w1" :error {:kind :offline}}])]
      (is (nil? (effect-value effects :fetch))))))

(deftest write-failed-for-superseded-write-is-dropped
  (let [r (assoc base :active-write {:write/id "tasks:w2" :payload {:op :delete :id 7}})
        {:keys [resource effects]} (resource/step r [:write-failed {:write/id "tasks:w1"
                                                                    :error    {:kind :offline}}])]
    (testing "a failure for a superseded write does not touch state"
      (is (= {:write/id "tasks:w2" :payload {:op :delete :id 7}} (:active-write resource))))
    (testing "it surfaces only as a diagnostic"
      (is (= [[:diagnostic {:code :stale-write}]] effects)))))

(deftest write-failed-protocol-is-labelled-protocol-not-network
  (let [aw {:write/id "tasks:w1" :payload {:op :delete :id 7}}
        r  (assoc base :last-accepted accepted :write-count 1 :active-write aw)
        {:keys [resource]} (resource/step r [:write-failed {:write/id "tasks:w1"
                                                            :protocol-failure {:reason :empty-body}}])]
    (testing "a broken ack envelope is a :protocol failure carrying its detail, not a nil-error :network one"
      (is (= {:failure :protocol :detail {:reason :empty-body} :write aw} (:last-failure resource))))))

(deftest unknown-event-is-a-noop
  (is (= {:resource base :effects []}
         (resource/step base [:some-future-event {}]))
      "events step doesn't handle leave the resource untouched with no effects"))
