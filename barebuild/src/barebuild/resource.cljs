(ns barebuild.resource
  (:require [barebuild.utils.query :as query]
            [barebuild.utils.request :as request]
            [barebuild.validation :as validation]))

;; READ functionality ----------------------------------------------------------
(defn- resolve-history-mode
  "Push for navigations, replace otherwise (default)."
  [resource gesture-class]
  (get (:history-policy resource) gesture-class :replace))

;; contract validation

(defn- validate-shape [shape]
  (cond-> []
    (not (:id-key shape)) (conj (validation/err [:shape :id-key] :missing-id-key "shape is missing :id-key"))
    (not (:fields shape)) (conj (validation/err [:shape :fields] :missing-fields "shape is missing :fields"))))

(defn- validate-value [v]
  (cond
    (not (sequential? v)) [(validation/err [:value] :not-a-list "value is not a list")]
    (not (every? map? v)) [(validation/err [:value] :not-maps "value is not a list of maps")]
    :else []))

(defn- validate-ids [id-key value]
  (let [ids (map #(get % id-key) value)]
    (cond-> []
      (some nil? ids)
      (conj (validation/err [:value] :missing-id (str "some rows are missing \"" id-key "\"")))
      (not= (count ids) (count (distinct ids)))
      (conj (validation/err [:value] :duplicate-id "row ids are not unique")))))

(defn- validate-row [row-idx row fields]
  (mapcat (fn [{:keys [key type]}]
            (cond
              (not (contains? row key))
              [(validation/err [:value row-idx key] :missing-field
                               (str "row " row-idx " is missing field \"" key "\""))]

              (not (validation/validate-value-type (get row key) type))
              [(validation/err [:value row-idx key] :wrong-type
                               (str "row " row-idx " field \"" key "\" is not a " (name type)))]

              :else []))
          fields))

(defn- validate-rows [fields value]
  (into [] (comp (map-indexed (fn [idx row] (validate-row idx row fields))) cat) value))

(defn- validate-contract
  "verifies if an accepted payload contains the right shape"
  [payload]
  (let [{:keys [shape value]} payload
        ;; use this to skip checks inside the value. No use to look further if this is firing
        value-errors (validate-value value)]
    (into (vec (validate-shape shape))
          (if (seq value-errors)
            value-errors
            (concat (validate-ids (:id-key shape) value)
                    (validate-rows (:fields shape) value))))))

;; read-write-data — one in-flight vocabulary for reads and writes
;; The two kinds of requests as data: where each one's in-flight record lives, where its counter
;; lives, and how its ids are named. A read and a write differ in those three places. Using this
;; mapa llows us to reuse the same read/write functions for both operations.
(def ^:private read-write-data
  {:read  {:slot :active-request :counter :request-count :id-prefix ""}
   :write {:slot :active-write   :counter :write-count   :id-prefix "w"}})

(defn- in-flight
  "The in-flight record for the `kind` read-write-data in `r`, or nil when none is."
  [r kind]
  (get r (:slot (read-write-data kind))))

(defn- clear-in-flight
  "Drop the `kind` read-write-data from `r`, it is no longer in flight."
  [r kind]
  (assoc r (:slot (read-write-data kind)) nil))

(defn- read-request [r]
  (let [{:keys [query] rid :request/id} (in-flight r :read)]
    (assoc (request/request {:endpoint    (:endpoint r)
                           :method      "GET"
                           :query       query
                           :credentials (:credentials r)
                           :headers     (:headers r)
                           :timeout     (:timeout r)
                           :request-id  rid})
           :request/id rid)))

(defn- notify-fx
  "The :notify-consumers effect for resource value `r`."
  [r]
  [:notify-consumers {:resource r}])

(defn- fetch-fx
  "The :fetch effect for r's in-flight read request."
  [r]
  [:fetch (read-request r)])

(defn- url-write-fx
  "The :url-write effect projecting `params` onto the resource's URL scope, in `mode`."
  [resource params mode]
  [:url-write {:resource/id (:resource/id resource) :params params :mode mode}])

(defn- start
  "Open an in-flight request of `kind` in `r`, numbered from that kind's counter and named from
  the resource id. `extra` is whatever that kind's record carries beyond its id and query, a
  write's payload above all."
  ([r kind query] (start r kind query nil))
  ([r kind query extra]
   (let [{:keys [slot counter id-prefix]} (read-write-data kind)
         n                                (inc (or (counter r) 0))]
     (assoc r
            counter n
            slot    (merge {:request/id (str (:resource/id r) ":" id-prefix n)
                            :query      query}
                           extra)))))

(defn- requested-query
  [r]
  (:query (in-flight r :read)))

(defn- fresh?
  "True when `request-id` names the read-write-data of `kind` that is currently in flight."
  [r kind request-id]
  (= request-id (:request/id (in-flight r kind))))

;; Public for test purposes only
(defn installable?
  "True when `response` answers the in-flight read request, matched by request id alone."
  [r response]
  (fresh? r :read (:request/id response)))

(defn- drifted?
  "True when the intent moved on while the `kind` read-write-data was still in flight, so what comes
  back answers a question the URL has already left behind."
  [r kind]
  (not= (:url-intent r) (:query (in-flight r kind))))

(defn- read-failure-query
  "The query a *read* failure concerns. A write failure answers nothing about whether the current
  intent has been fetched, so it does not count towards pending?.

  A failure records the query its request was *issued for*, never the server's echo of it, as the
  server is free to normalise a query it doesn't honor. The echo sent back would then never equal the
  current intent."
  [f]
  (when (= :read (:for f)) (:query f)))

;; Public for test purposes only
(defn pending? [r]
  (or (some? (in-flight r :read))
      (let [intent (:url-intent r)]
        (and (not= intent (get-in r [:last-accepted :query]))
             (not= intent (read-failure-query (:last-failure r)))))))

(defn- with-trailing-fetch
  "If there is a current intent, still not answered, fire it again if a transition had cleared
  the active-request. Do nothing if the active-request is still set"
  [{:keys [resource effects] :as result}]
  (if (and (nil? (in-flight resource :read)) (pending? resource))
    (let [r* (start resource :read (:url-intent resource))]
      {:resource r*
       :effects  (conj (vec effects) (fetch-fx r*))})
    result))

(defn- with-reconciling-fetch
  "Follow a write whose outcome the client cannot know with a re-read. The request may have
  reached the server and committed before the failure, so observing the server is the only way to
  learn whether it did, and rendering the old view as if nothing happened would make the user's
  retry a duplicate. Skipped while a read is already in flight, since one is on its way."
  [{:keys [resource effects] :as result}]
  (if (in-flight resource :read)
    result
    (let [r* (start resource :read (:url-intent resource))]
      {:resource r*
       :effects  (conj (vec effects) (fetch-fx r*))})))

;; WRITE functionality ----------------------------------------------------------

;; The write vocabulary as data: each op maps to its method, whether it addresses the
;; collection or a member, and whether it carries a body.
;; :move is a positional command, not a partial merge of the record: it repositions a member
;; (server-owned rank) and carries only the destination, so full-replace :update stays the only
;; way to edit record fields.
(def ^:private write-ops
  {:create {:method "POST"   :target :collection :body? true}
   :update {:method "PUT"    :target :member     :body? true}
   :delete {:method "DELETE" :target :member     :body? false}
   :move   {:method "PATCH"  :target :member     :body? true}})

;; Public for test purposes only
(defn write-request
  "The :write effect value for a write payload, or nil when none can be built. Reads the endpoint
  and the request config off the resource"
  [resource write-id {:keys [op id record]} query]
  (when-let [{:keys [method target body?]} (get write-ops op)]
    (let [member? (= target :member)]
      (when (or (not member?) (seq (str id)))
        (assoc (request/request {:endpoint    (:endpoint resource)
                               :segment     (when member? id)
                               :method      method
                               :query       query
                               :body        (when body? record)
                               :credentials (:credentials resource)
                               :headers     (:headers resource)
                               :timeout     (:timeout resource)
                               :request-id  write-id})
               :request/id write-id)))))

;; Public for test purposes only
(defn writing? [r]
  (some? (in-flight r :write)))

(defn- install-accepted
  "Install an accepted envelope: set last-accepted and, when adopt?, adopt the query echo
  as intent. Returns the resource and whether the URL needs a corrective write."
  [resource payload adopt?]
  (let [echo      (:query payload)
        installed (assoc resource :last-accepted payload :last-failure nil)]
    {:resource (if adopt?
                 (assoc installed :url-intent echo)
                 installed)
     :correct? (and adopt?
                    (not= echo (:url-intent resource)))}))

(defn- accepted-effects [resource echo correct?]
  (if correct?
    [(url-write-fx resource echo :replace) (notify-fx resource)]
    [(notify-fx resource)]))

(defn- diagnostic
  "A diagnostic effect value, optionally carrying `detail` the executor prints beside the code.
  The executor only console.debugs it, it drives no state."
  ([code] [:diagnostic {:code code}])
  ([code detail] [:diagnostic {:code code :detail detail}]))

(defn- record-failure
  "Clear the in-flight `kind` read-write-data, stash the failure, notify."
  [resource kind failure]
  (let [resource* (assoc (clear-in-flight resource kind) :last-failure failure)]
    {:resource resource* :effects [(notify-fx resource*)]}))

(defn- install-envelope
  "Install an accepted envelope that answered the `kind` read-write-data. Since a write returns the full
  post-mutation state, an accepted ack is the same envelope a read returns, so both install the
  same way: check the contract, record a violation as a failure, and otherwise install, adopting
  the query echo unless the intent drifted while the read-write-data was in flight."
  [resource kind payload]
  (let [errors (validate-contract payload)]
    (if (seq errors)
      (record-failure resource kind
                      {:failure :contract :for kind :response payload :errors errors
                       :query   (:query (in-flight resource kind))})
      (let [adopt?                      (not (drifted? resource kind))
            {:keys [resource correct?]} (install-accepted (clear-in-flight resource kind)
                                                          payload adopt?)]
        {:resource resource
         :effects  (accepted-effects resource (:query payload) correct?)}))))

;; Projection  ------ What a consumer sees
(defn project
  "The fields a consumer may depend on, everything else is internal resource bookkeeping.
  The accepted envelope loses its :request/id on the way out. That id names the read-write-data
  that fetched the value, not the value, so two identical refetches project equal views and
  a consumer comparing them does not repaint."
  [resource]
  {:accepted  (dissoc (:last-accepted resource) :request/id)
   :failure   (:last-failure resource)
   :intent    (:url-intent resource)
   :pending?  (pending? resource)
   :writing?  (writing? resource)})

;; CONNECT / SSR boot embed (§7.4) ----------------------------------------------

(defn- boot-fetch
  "The initial connect fetch for the current intent, with an optional leading diagnostic for an
  embed that was ignored (broken, or a stale rejection)."
  [resource diag-code]
  (let [r* (start resource :read (:url-intent resource))]
    {:resource r*
     :effects  (cond-> []
                 diag-code (conj (diagnostic diag-code))
                 :always   (conj (fetch-fx r*) (notify-fx r*)))}))

(defn- connect-accepted-embed
  "Install an accepted boot embed exactly as a network response: validate the contract, a broken
  payload adjudicates as a contract failure, a valid one installs and fetches only when the URL
  has moved past what was embedded."
  [resource embed]
  (let [errors (validate-contract embed)]
    (if (seq errors)
      (with-trailing-fetch
        (record-failure resource :read
                        {:failure :contract :for :read :response embed :errors errors :query (:query embed)}))
      ;; An embed never adopts its query echo and never corrects the URL: there is no in-flight
      ;; request for the intent to have drifted from, so a mismatch is answered by fetching.
      (let [installed (:resource (install-accepted resource embed false))]
        (if (pending? installed)
          (let [r* (start installed :read (:url-intent installed))]
            {:resource r* :effects [(notify-fx r*) (fetch-fx r*)]})
          {:resource installed :effects [(notify-fx installed)]})))))

(defn- connect-rejected-embed
  "A rejected boot embed the server already adjudicated. When its echo matches intent it installs
  as the failure and answers the intent, so no boot fetch. A stale rejection (the URL moved) is
  diagnostics only, then a normal fetch."
  [resource embed]
  (if (= (:query embed) (:url-intent resource))
    (let [r* (assoc resource :last-failure {:failure :rejected :for :read :response embed :query (:query embed)})]
      {:resource r* :effects [(notify-fx r*)]})
    (boot-fetch resource :stale-rejected-embed)))

(defn- connect
  "The :connected transition. An SSR boot embed, if present and usable, installs first; otherwise
  a plain first fetch for the current intent."
  [resource embed]
  (cond
    (:protocol-failure embed)      (boot-fetch resource :broken-embed)
    (= :accepted (:outcome embed)) (connect-accepted-embed resource embed)
    (= :rejected (:outcome embed)) (connect-rejected-embed resource embed)
    :else                          (boot-fetch resource nil)))

(defn- targets-sibling?
  "True when an intent names a resource other than the one it was submitted to, cross-resource
  coordination. A nil target, or one equal to the resource's own id, drives the resource itself."
  [resource target-id]
  (boolean (and target-id (not= target-id (:resource/id resource)))))

(defn- install-rejection
  "Record a read the server declined. When the rejection concerns the query the URL currently
  holds, and there is a good value behind it, the intent reverts to the query that value answered
  and the URL is corrected to match, so the user is not left looking at an address that names a
  view the server refuses to serve."
  [resource payload]
  (let [accepted-query (get-in resource [:last-accepted :query])
        revert?        (and (= (:query payload) (:url-intent resource))
                            (some? (:last-accepted resource)))
        cleared        (assoc (clear-in-flight resource :read)
                              :last-failure {:failure :rejected :for :read :response payload
                                             :query (requested-query resource)})
        resource*      (if revert? (assoc cleared :url-intent accepted-query) cleared)]
    {:resource resource*
     :effects  (if revert?
                 [(url-write-fx resource* accepted-query :replace) (notify-fx resource*)]
                 [(notify-fx resource*)])}))

(defn- apply-intent-patch
  "Merge a patch into the resource's own intent. The URL is written whenever the intent moved,
  and a fetch follows only when nothing is in flight and the new intent is unanswered."
  [resource payload]
  (let [new-intent (query/canonicalize-query (merge (:url-intent resource) (:query-patch payload)))
        merged     (assoc resource :url-intent new-intent)
        mode       (resolve-history-mode resource (:gesture-class payload))
        moved?     (not= new-intent (:url-intent resource))
        fetch?     (and (nil? (in-flight resource :read))
                        (pending? merged))
        r*         (if fetch? (start merged :read new-intent) merged)]
    {:resource r*
     :effects  (cond-> []
                 moved?  (conj (url-write-fx r* new-intent mode))
                 fetch?  (conj (fetch-fx r*))
                 :always (conj (notify-fx r*)))}))

(defn- route-intent
  "Hand a patch that names another resource to the executor, which resolves the name to an
  element. This resource's own value does not move."
  [resource payload]
  {:resource resource
   :effects  [[:route-intent {:resource/id (:target-id payload)
                              :patch       (dissoc payload :target-id)}]]})

;; Public for test purposes only
(def effect-tags
  "Every effect `step` can return. The executor performs these and nothing else, so an executor
  that covers a different set has drifted from the vocabulary rather than merely lagged it."
  #{:fetch :write :abort :url-write :route-intent :notify-consumers :diagnostic})

(defn step
  "Takes a resource and event and returns (a possibly updated) resource
  and the effects that need to be called. Each step gets a unique resource/id"
  [resource event]
  (let [[event-k payload] event]
    (case event-k
      ;; reads
      :connected
      (connect resource (:embed payload))

      :response
      (if-not (installable? resource payload)
        {:resource resource :effects [(diagnostic :stale-response)]}
        (case (:outcome payload)
          :accepted (with-trailing-fetch (install-envelope resource :read payload))
          :rejected (with-trailing-fetch (install-rejection resource payload))
          {:resource resource :effects []}))

      ;; An intent naming a sibling is not this resource's to apply. step says where it goes and
      ;; the executor only resolves that name to an element, so the routing is in the effect value.
      :intent-patch
      (if (targets-sibling? resource (:target-id payload))
        (route-intent resource payload)
        (apply-intent-patch resource payload))

      ;; The executor could not resolve the target of a :route-intent. The gesture is lost either
      ;; way, so we register it here.
      :intent-unroutable
      {:resource resource :effects [(diagnostic :unroutable-intent payload)]}

      :url-changed
      (let [replaced (assoc resource :url-intent payload)
            fetch?   (and (nil? (in-flight resource :read))
                          (pending? replaced))
            r*       (if fetch? (start replaced :read payload) replaced)]
        {:resource r*
         :effects  (cond-> []
                     fetch? (conj (fetch-fx r*))
                     :always (conj (notify-fx r*)))})

      :protocol-failed
      (if-not (fresh? resource :read (:request/id payload))
        {:resource resource :effects [(diagnostic :stale-failure)]}
        (with-trailing-fetch
          (record-failure resource :read
                          {:failure :protocol
                           :for     :read
                           :detail  (:protocol-failure payload)
                           :query   (requested-query resource)})))

      :network-failed
      (if-not (fresh? resource :read (:request/id payload))
        {:resource resource :effects [(diagnostic :stale-failure)]}
        (with-trailing-fetch
          (record-failure resource :read
                          {:failure :network
                           :for   :read
                           :error (:error payload)
                           :query (requested-query resource)})))

      :disconnected
      (if-let [id (:request/id (in-flight resource :read))]
        {:resource (clear-in-flight resource :read)
         :effects  [[:abort {:request/id id}]]}
        {:resource resource :effects []})

      ;; writes
      :submit-write
      (if-not (writing? resource)
        (let [resource* (start resource :write (:url-intent resource) {:payload payload})
              id        (:request/id (in-flight resource* :write))
              write-req (write-request resource* id payload (:url-intent resource))]
          (if write-req
            {:resource resource*
             :effects  [(notify-fx resource*)
                        [:write write-req]]}
            {:resource resource
             :effects  [(diagnostic :unsupported-write)]}))
        {:resource resource
         :effects  [(diagnostic :stale-write)]})

      :write-ack
      (if (fresh? resource :write (:request/id payload))
        (if (= :accepted (:outcome payload))
          (with-trailing-fetch (install-envelope resource :write payload))
          (record-failure resource :write
                          {:failure :rejected :for :write :response payload
                           :query (:query (in-flight resource :write))}))
        {:resource resource
         :effects  [(diagnostic :stale-write)]})

      ;; Every failure reaching here left the write's outcome unknown: the request may have
      ;; committed before the connection dropped, the budget ran out, or the body came back
      ;; unreadable. Only an explicit :rejected ack, handled above, is the server saying no.
      :write-failed
      (if (fresh? resource :write (:request/id payload))
        (let [write   (in-flight resource :write)
              failure (if-let [detail (:protocol-failure payload)]
                        {:failure :protocol :for :write :detail detail :write write :query (:query write)}
                        {:failure :network :for :write :error (:error payload) :write write
                         :query (:query write)})]
          (with-reconciling-fetch (record-failure resource :write failure)))
        {:resource resource
         :effects [(diagnostic :stale-write)]})

      ;; An event this vocabulary does not contain changes nothing, and says so.
      {:resource resource :effects [(diagnostic :unknown-event {:event event-k})]})))
