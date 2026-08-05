(ns barebuild.resource
  (:require [barebuild.effect :as effect]
            [barebuild.utils.query :as query]
            [barebuild.utils.request :as request]
            [barebuild.validation :as validation]))

;; The step outcome ------------------------------------------------------------
;; Every transition returns this one shape: the resource the event left behind and the effects
;; the executor must perform. Built here and nowhere else, so the two keys have one spelling.
(defn- result
  ([resource] (result resource []))
  ([resource effects] {:resource resource :effects effects}))

(defn- ignored
  "The event moved nothing, `code` names why. Every event this resource declines to act on is
  answered this way rather than in silence, so a gesture that went nowhere still reaches the log."
  ([resource code] (result resource [(effect/diagnostic code)]))
  ([resource code detail] (result resource [(effect/diagnostic code detail)])))

;; The failure value -----------------------------------------------------------
;; What `project` hands a consumer as :failure, and the only shape it takes. Every failure is
;; built by `failure` below, so a consumer reads this one place rather than the seven transitions
;; that can produce one.
;;
;;   :failure  what went wrong, one of
;;             :rejected  the server adjudicated the request and said no  (carries :response)
;;             :contract  it accepted, but the payload broke the shape    (carries :response, :errors)
;;             :protocol  the answer could not be read                    (carries :detail)
;;             :network   no answer arrived                               (carries :error)
;;   :for      the kind of request that failed, :read or :write. This is the discriminator a
;;             consumer branches on, never the presence of one of the members above.
;;   :query    the query the failed request was issued for, never the server's echo of it. A
;;             server is free to normalise a query it does not honor, and an echo would then never
;;             equal the current intent.
;;   :write    the in-flight write record, present only on a :write failure whose outcome the
;;             client never learned.
(defn- failure
  "A failure value. `cause` is what went wrong, `kind` the kind of request it happened to, `query`
  the query that request was issued for, and `extra` the members that cause carries. The three
  named members are merged last, so the shape written down above is what a failure has and a
  cause's own members cannot quietly redefine the key consumers branch on."
  ([cause kind query] (failure cause kind query nil))
  ([cause kind query extra]
   (merge extra {:failure cause :for kind :query query})))

;; READ functionality ----------------------------------------------------------
(defn- resolve-history-mode
  "Push for navigations, replace otherwise (default)."
  [resource gesture-class]
  (get (:history-policy resource) gesture-class :replace))

;; The two kinds of request as data: where each one's in-flight record lives, where its counter
;; lives, and how its ids are named. A read and a write differ in those three places and nowhere
;; else, so one set of functions serves both, told apart only by the kind they are handed.
(def ^:private request-kinds
  {:read  {:slot :active-request :counter :request-count :id-prefix ""}
   :write {:slot :active-write   :counter :write-count   :id-prefix "w"}})

(defn- in-flight
  "The in-flight request of `kind` in `r`, or nil when none is."
  [r kind]
  (get r (:slot (request-kinds kind))))

(defn- clear-in-flight
  "Drop r's in-flight request of `kind`, it is no longer in flight."
  [r kind]
  (assoc r (:slot (request-kinds kind)) nil))

(defn- transport-fields
  "What every request off this resource carries whatever it is asking for: where to send it, what
  it goes on the wire with, and how long the executor may wait. A new one of these is a key here
  rather than a line in each builder below."
  [r]
  (select-keys r [:endpoint :credentials :headers :timeout]))

(defn- read-request [r]
  (let [{:keys [query] rid :request/id} (in-flight r :read)]
    (request/request (assoc (transport-fields r)
                            :method     "GET"
                            :query      query
                            :request-id rid))))

(defn- fetch-fx
  "The :fetch effect for r's in-flight read request. The one effect whose payload is computed
  rather than passed in, since only this namespace knows which fields of a resource make a read."
  [r]
  (effect/fetch (read-request r)))

(defn- url-write-fx
  "The :url-write effect projecting `params` onto `resource`'s own URL scope, in `mode`."
  [resource params mode]
  (effect/url-write (:resource/id resource) params mode))

(defn- start
  "Open an in-flight request of `kind` in `r`, numbered from that kind's counter and named from
  the resource id. `extra` is whatever that kind's record carries beyond its id and query, a
  write's payload above all."
  ([r kind query] (start r kind query nil))
  ([r kind query extra]
   (let [{:keys [slot counter id-prefix]} (request-kinds kind)
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
  "True when `request-id` names the request of `kind` that is currently in flight."
  [r kind request-id]
  (= request-id (:request/id (in-flight r kind))))

;; Public for test purposes only
(defn answers-in-flight-read?
  "True when `response` answers the in-flight read request, matched by request id alone."
  [r response]
  (fresh? r :read (:request/id response)))

(defn- drifted?
  "True when the intent moved on while the request of `kind` was still in flight, so what comes
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

(defn- answered?
  "True when the value already holds an answer for the current intent, accepted or refused. An
  answer has to exist to count: a resource that has fetched nothing answers nothing, whatever its
  intent is, so this stays total rather than resting on the element always seeding :url-intent
  with a map."
  [r]
  (let [intent (:url-intent r)]
    (or (and (some? (:last-accepted r))
             (= intent (get-in r [:last-accepted :query])))
        (and (some? (:last-failure r))
             (= intent (read-failure-query (:last-failure r)))))))

;; Public for test purposes only
(defn pending? [r]
  (or (some? (in-flight r :read))
      (not (answered? r))))

(defn- with-read
  "Open a read for the current intent when `wanted?`, appending its :fetch to `outcome`. Skipped
  while a read is already in flight, since one is on its way. Every read a transition decides to
  open goes through here.

  The :fetch lands after effects the transition already built, so a :notify-consumers among them
  carries the value from *before* the read opened. That is safe, and it is the invariant holding
  it up: a read only opens when `pending?` is already true, and opening one cannot turn it false,
  so both values project the same view. Nothing else `start` touches is projected at all."
  [{:keys [resource effects] :as outcome} wanted?]
  (if (and wanted? (nil? (in-flight resource :read)))
    (let [r* (start resource :read (:url-intent resource))]
      (result r* (conj (vec effects) (fetch-fx r*))))
    outcome))

(defn- with-trailing-fetch
  "Follow a transition that cleared the active request with a read for the current intent, when
  the value does not already answer it."
  [{:keys [resource] :as outcome}]
  (with-read outcome (pending? resource)))

(defn- with-reconciling-fetch
  "Follow a write whose outcome the client cannot know with a re-read. The request may have
  reached the server and committed before the failure, so observing the server is the only way to
  learn whether it did, and rendering the old view as if nothing happened would make the user's
  retry a duplicate. Wanted whatever the value claims to answer, since what it claims is exactly
  what the failed write may have invalidated."
  [outcome]
  (with-read outcome true))

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
        (request/request (assoc (transport-fields resource)
                                :segment    (when member? id)
                                :method     method
                                :query      query
                                :body       (when body? record)
                                :request-id write-id))))))

;; Public for test purposes only
(defn writing? [r]
  (some? (in-flight r :write)))

(defn- accept
  "Hold `payload` as the value this resource answers with. A good value retires whatever failure
  preceded it, so a consumer that drew an error clears it."
  [resource payload]
  (assoc resource :last-accepted payload :last-failure nil))

(defn- record-failure
  "Clear the in-flight request of `kind`, stash the failure, notify."
  [resource kind failure]
  (let [resource* (assoc (clear-in-flight resource kind) :last-failure failure)]
    (result resource* [(effect/notify-consumers resource*)])))

(defn- transport-members
  "What a `cause` that produced no readable answer carries: the unreadable answer itself
  (:protocol), or the reason none arrived (:network). Reads and writes classify the same two ways,
  so both build it here."
  [cause payload]
  (case cause
    :protocol {:detail (:protocol-failure payload)}
    :network  {:error  (:error payload)}))

(defn- install-envelope
  "Install an accepted envelope that answered the request of `kind`. Since a write returns the full
  post-mutation state, an accepted ack is the same envelope a read returns, so both install the
  same way: check the contract, record a violation as a failure, and otherwise install, adopting
  the query echo unless the intent drifted while the request was in flight. Adopting an echo the
  URL does not already hold is what makes the URL wrong, so that is exactly when it is corrected."
  [resource kind payload]
  (let [errors (validation/validate-contract payload)]
    (if (seq errors)
      (record-failure resource kind
                      (failure :contract kind (:query (in-flight resource kind))
                               {:response payload :errors errors}))
      (let [echo     (:query payload)
            adopt?   (not (drifted? resource kind))
            correct? (and adopt? (not= echo (:url-intent resource)))
            r*       (cond-> (accept (clear-in-flight resource kind) payload)
                       adopt? (assoc :url-intent echo))]
        (result r* (cond-> []
                     correct? (conj (url-write-fx r* echo :replace))
                     :always  (conj (effect/notify-consumers r*))))))))

;; Projection  ------ What a consumer sees
(defn project
  "The fields a consumer may depend on, everything else is internal resource bookkeeping.
  The accepted envelope loses its :request/id on the way out. That id names the request that
  fetched the value, not the value, so two identical refetches project equal views and a consumer
  comparing them does not repaint."
  [resource]
  {:accepted  (dissoc (:last-accepted resource) :request/id)
   :failure   (:last-failure resource)
   :intent    (:url-intent resource)
   :pending?  (pending? resource)
   :writing?  (writing? resource)})

;; CONNECT / SSR boot embed (§7.4) ----------------------------------------------

(defn- boot
  "The initial connect for the current intent, with an optional leading diagnostic for an embed
  that was ignored (broken, or a stale rejection). Nothing is accepted yet, so the intent is
  unanswered and the trailing read always opens."
  [resource diag-code]
  (with-trailing-fetch
    (result resource (cond-> []
                       diag-code (conj (effect/diagnostic diag-code))
                       :always   (conj (effect/notify-consumers resource))))))

(defn- connect-accepted-embed
  "Install an accepted boot embed exactly as a network response: validate the contract, a broken
  payload adjudicates as a contract failure, a valid one installs and fetches only when the URL
  has moved past what was embedded."
  [resource embed]
  (let [errors (validation/validate-contract embed)]
    (if (seq errors)
      (with-trailing-fetch
        (record-failure resource :read
                        (failure :contract :read (:query embed) {:response embed :errors errors})))
      ;; An embed never adopts its query echo and never corrects the URL: there is no in-flight
      ;; request for the intent to have drifted from, so a mismatch is answered by fetching.
      (let [installed (accept resource embed)]
        (with-trailing-fetch (result installed [(effect/notify-consumers installed)]))))))

(defn- connect-rejected-embed
  "A rejected boot embed the server already adjudicated. When its echo matches intent it installs
  as the failure and answers the intent, so no boot fetch. A stale rejection (the URL moved) is
  diagnostics only, then a normal fetch."
  [resource embed]
  (if (= (:query embed) (:url-intent resource))
    (let [r* (assoc resource :last-failure
                    (failure :rejected :read (:query embed) {:response embed}))]
      (result r* [(effect/notify-consumers r*)]))
    (boot resource :stale-rejected-embed)))

(defn- connect
  "The :connected transition. An SSR boot embed, if present and usable, installs first; otherwise
  a plain first fetch for the current intent."
  [resource embed]
  (cond
    (:protocol-failure embed)      (boot resource :broken-embed)
    (= :accepted (:outcome embed)) (connect-accepted-embed resource embed)
    (= :rejected (:outcome embed)) (connect-rejected-embed resource embed)
    :else                          (boot resource nil)))

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
                              :last-failure (failure :rejected :read (requested-query resource)
                                                     {:response payload}))
        resource*      (if revert? (assoc cleared :url-intent accepted-query) cleared)]
    (result resource*
            (if revert?
              [(url-write-fx resource* accepted-query :replace) (effect/notify-consumers resource*)]
              [(effect/notify-consumers resource*)]))))

(defn- apply-intent-patch
  "Merge a patch into the resource's own intent. The URL is written whenever the intent moved,
  and a fetch follows only when nothing is in flight and the new intent is unanswered."
  [resource payload]
  (let [new-intent (query/canonicalize-query (merge (:url-intent resource) (:query-patch payload)))
        merged     (assoc resource :url-intent new-intent)
        mode       (resolve-history-mode resource (:gesture-class payload))
        moved?     (not= new-intent (:url-intent resource))]
    (with-trailing-fetch
      (result merged (cond-> []
                       moved?  (conj (url-write-fx merged new-intent mode))
                       :always (conj (effect/notify-consumers merged)))))))

(defn- replace-intent
  "The :url-changed transition. The address bar has already moved, so the intent is replaced
  outright rather than merged, and never written back."
  [resource intent]
  (let [replaced (assoc resource :url-intent intent)]
    (with-trailing-fetch (result replaced [(effect/notify-consumers replaced)]))))

(defn- route-intent
  "Hand a patch that names another resource to the executor, which resolves the name to an
  element. This resource's own value does not move."
  [resource payload]
  (result resource [(effect/route-intent (:target-id payload) (dissoc payload :target-id))]))

;; Transitions — one per event, each named, each returning a `result` --------------

(defn- install-response
  "The :response transition. A response for a request no longer in flight answers a question the
  resource has already left behind, so it is ignored."
  [resource payload]
  (if-not (answers-in-flight-read? resource payload)
    (ignored resource :stale-response)
    (case (:outcome payload)
      :accepted (with-trailing-fetch (install-envelope resource :read payload))
      :rejected (with-trailing-fetch (install-rejection resource payload))
      ;; `wire/parse-envelope` yields one of the two above or a protocol-failure marker, which
      ;; arrives as :protocol-failed instead, so nothing should reach here. Said out loud rather
      ;; than passed over, since a response that changes nothing in silence is the one gesture
      ;; that would not reach the log.
      (ignored resource :unknown-outcome {:outcome (:outcome payload)}))))

(defn- patch-intent
  "The :intent-patch transition. An intent naming a sibling is not this resource's to apply. `step`
  says where it goes and the executor only resolves that name to an element, so the routing is in
  the effect value."
  [resource payload]
  (if (targets-sibling? resource (:target-id payload))
    (route-intent resource payload)
    (apply-intent-patch resource payload)))

(defn- fail-read
  "The :protocol-failed and :network-failed transitions, which differ only in `cause`. Either way
  the read produced no answer, so the intent is still unanswered and a fresh read follows."
  [resource payload cause]
  (if-not (fresh? resource :read (:request/id payload))
    (ignored resource :stale-failure)
    (with-trailing-fetch
      (record-failure resource :read
                      (failure cause :read (requested-query resource)
                               (transport-members cause payload))))))

(defn- disconnect
  "The :disconnected transition. An in-flight read is abandoned, since nothing is left to render
  it into. An in-flight write is left alone: its outcome still matters to whoever reconnects, and
  the element carries the slot and counter naming it across the boot so the ack still lands. Until
  it does the resource is still writing, so the single-flight rule refuses a second write exactly
  as it would have without the reconnect."
  [resource]
  (if-let [id (:request/id (in-flight resource :read))]
    (result (clear-in-flight resource :read) [(effect/abort id)])
    (result resource)))

(defn- submit-write
  "The :submit-write transition. One write at a time, so a submit landing while another is in
  flight is ignored, and a payload the op vocabulary cannot express is reported rather than sent."
  [resource payload]
  (if (writing? resource)
    (ignored resource :stale-write)
    (let [resource* (start resource :write (:url-intent resource) {:payload payload})
          write-id  (:request/id (in-flight resource* :write))]
      (if-let [request (write-request resource* write-id payload (:url-intent resource))]
        (result resource* [(effect/notify-consumers resource*) (effect/write request)])
        (ignored resource :unsupported-write)))))

(defn- install-ack
  "The :write-ack transition. An accepted ack carries the full post-mutation state, so it installs
  exactly as a read's envelope does. A rejection is the server saying no, and stands."
  [resource payload]
  (if-not (fresh? resource :write (:request/id payload))
    (ignored resource :stale-write)
    (if (= :accepted (:outcome payload))
      (with-trailing-fetch (install-envelope resource :write payload))
      (record-failure resource :write
                      (failure :rejected :write (:query (in-flight resource :write))
                               {:response payload})))))

(defn- fail-write
  "The :write-failed transition. Every failure reaching here left the write's outcome unknown: the
  request may have committed before the connection dropped, the budget ran out, or the body came
  back unreadable. Only an explicit :rejected ack, which `install-ack` handles, is the server
  saying no."
  [resource payload]
  (if-not (fresh? resource :write (:request/id payload))
    (ignored resource :stale-write)
    (let [write (in-flight resource :write)
          cause (if (:protocol-failure payload) :protocol :network)]
      (with-reconciling-fetch
        (record-failure resource :write
                        (failure cause :write (:query write)
                                 (assoc (transport-members cause payload) :write write)))))))

(defn step
  "Takes a resource and event and returns (a possibly updated) resource
  and the effects that need to be called. Each step gets a unique resource/id"
  [resource event]
  (let [[event-k payload] event]
    (case event-k
      ;; reads
      :connected         (connect resource (:embed payload))
      :response          (install-response resource payload)
      :intent-patch      (patch-intent resource payload)
      ;; The executor could not resolve the target of a :route-intent. The gesture is lost either
      ;; way, so we register it here.
      :intent-unroutable (ignored resource :unroutable-intent payload)
      :url-changed       (replace-intent resource payload)
      :protocol-failed   (fail-read resource payload :protocol)
      :network-failed    (fail-read resource payload :network)
      :disconnected      (disconnect resource)

      ;; writes
      :submit-write      (submit-write resource payload)
      :write-ack         (install-ack resource payload)
      :write-failed      (fail-write resource payload)

      ;; An event this vocabulary does not contain changes nothing, and says so.
      (ignored resource :unknown-event {:event event-k}))))
