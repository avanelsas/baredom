(ns barebuild.resource
  (:require [barebuild.effect :as effect]
            [barebuild.utils.query :as query]
            [barebuild.utils.request :as request]
            [barebuild.validation :as validation]))

;; The resource value ----------------------------------------------------------

;; Which gesture classes push a history entry rather than replace one.
(def ^:private default-history-policy {:navigation :push})

(defn initial
  "The resource a fresh connection starts from, and the one place its shape is written down, so a
  test fixture and what the element boots with cannot drift apart. `request-config` is whatever the
  host declared as attributes, `carried` what `carry-over` selected out of the connection before
  this one. Both may name keys this does not, and both win over the defaults."
  [{:keys [resource/id endpoint url-intent history-policy request-config carried]}]
  (merge {:resource/id    id
          :endpoint       endpoint
          :last-accepted  nil
          :url-intent     url-intent
          :history-policy (or history-policy default-history-policy)}
         request-config
         carried))

;; The step outcome ------------------------------------------------------------
;; Every transition returns this shape: the resource the event left behind, and the effects the
;; executor must perform. Built here and nowhere else, so :effects is always a vector and a
;; transition appending to it can `conj`.
(defn- result
  ([resource] (result resource []))
  ([resource effects] {:resource resource :effects effects}))

(defn- ignored
  "An outcome that moves nothing, carrying a diagnostic naming `code`."
  ([resource code] (result resource [(effect/diagnostic code)]))
  ([resource code detail] (result resource [(effect/diagnostic code detail)])))

;; The failure value -----------------------------------------------------------
;; What `project` hands a consumer as :failure, and the only shape it takes.
;;
;;   :cause    what went wrong, one of
;;             :rejected  the server said no                        (carries :response)
;;             :contract  accepted, but the payload broke the shape (carries :response, :errors)
;;             :protocol  the answer could not be read              (carries :detail)
;;             :network   no answer arrived                         (carries :error)
;;   :for      the kind of request that failed, :read or :write. The discriminator a consumer
;;             branches on, never the presence of one of the members above.
;;   :query    the query the failed request was issued for, never the server's echo of it.
;;   :write    the in-flight write record, present only on a :write failure.
(defn- failure
  "A failure value of the shape above. The three named members are merged last, so `extra` cannot
  redefine them."
  ([cause kind query] (failure cause kind query nil))
  ([cause kind query extra]
   (merge extra {:cause cause :for kind :query query})))

;; Requests in flight, read and write alike ------------------------------------

;; The two kinds of request as data: where each one's in-flight record lives, where its counter
;; lives, and how its ids are named. One set of functions serves both, told apart only by the kind
;; they are handed.
(def ^:private request-kinds
  {:read  {:slot :active-request :counter :request-count :id-prefix ""}
   :write {:slot :active-write   :counter :write-count   :id-prefix "w"}})

(defn carry-over
  "The part of `resource` the next connection inherits, and the only part that survives a boot. An
  in-flight write's slot has to still be there when its ack lands, and its counter has to keep
  counting so the next write cannot mint an id the orphan already answers to. Reads need neither, a
  disconnect aborts the in-flight one. Read off the table so a renamed slot moves both halves."
  [resource]
  (let [{:keys [slot counter]} (request-kinds :write)]
    (select-keys resource [slot counter])))

(defn- in-flight
  "The in-flight request of `kind` in `r`, or nil."
  [r kind]
  (get r (:slot (request-kinds kind))))

(defn- clear-in-flight
  "`r` with its in-flight request of `kind` dropped."
  [r kind]
  (assoc r (:slot (request-kinds kind)) nil))

(defn- in-flight-query
  "The query the in-flight request of `kind` was issued for, or nil when none is."
  [r kind]
  (:query (in-flight r kind)))

(defn- transport-fields
  "The fields every request off `r` carries whatever it asks for: :endpoint, :credentials,
  :headers, :timeout."
  [r]
  (select-keys r [:endpoint :credentials :headers :timeout]))

(defn- start
  "`r` with an in-flight request of `kind` opened for `query`, numbered from that kind's counter
  and named from the resource id. `extra` is whatever else that kind's record carries, a write's
  payload above all."
  ([r kind query] (start r kind query nil))
  ([r kind query extra]
   (let [{:keys [slot counter id-prefix]} (request-kinds kind)
         n                                (inc (or (counter r) 0))]
     (assoc r
            counter n
            slot    (merge {:request/id (str (:resource/id r) ":" id-prefix n)
                            :query      query}
                           extra)))))

(defn- answers-in-flight?
  "True when `request-id` names the in-flight request of `kind`."
  [r kind request-id]
  (= request-id (:request/id (in-flight r kind))))

;; Public for test purposes only
(defn answers-in-flight-read?
  "`answers-in-flight?` for a read, matching `response` to it by request id alone."
  [r response]
  (answers-in-flight? r :read (:request/id response)))

(defn- drifted?
  "True when the intent moved on while the request of `kind` was in flight."
  [r kind]
  (not= (:url-intent r) (in-flight-query r kind)))

;; The read, and when one is wanted --------------------------------------------

(defn- read-request [r]
  (let [{:keys [query] rid :request/id} (in-flight r :read)]
    (request/request (assoc (transport-fields r)
                            :method     "GET"
                            :query      query
                            :request-id rid))))

(defn- fetch-fx
  "The :fetch effect for r's in-flight read request."
  [r]
  (effect/fetch (read-request r)))

(defn- read-failure-query
  "The query a read failure concerns, nil for a write failure. A write answers nothing about
  whether the current intent has been fetched."
  [f]
  (when (= :read (:for f)) (:query f)))

(defn- answered?
  "True when `r` already holds an answer for the current intent, accepted or refused. The answer
  has to exist, so a resource that has fetched nothing answers nothing."
  [r]
  (let [intent (:url-intent r)]
    (or (and (some? (:last-accepted r))
             (= intent (get-in r [:last-accepted :query])))
        (and (some? (:last-failure r))
             (= intent (read-failure-query (:last-failure r)))))))

;; Public for test purposes only
(defn pending?
  "True while a read is in flight, or the current intent has no answer yet."
  [r]
  (or (some? (in-flight r :read))
      (not (answered? r))))

(defn- with-read
  "`outcome` with a :fetch for the current intent appended, when `wanted?` and no read is already
  in flight. Every read a transition opens goes through here.

  The :fetch lands after effects the transition already built, so a :notify-consumers among them
  carries the value from before the read opened. Safe because a read only opens when `pending?` is
  already true, and opening one cannot turn it false."
  [{:keys [resource effects] :as outcome} wanted?]
  (if (and wanted? (nil? (in-flight resource :read)))
    (let [r* (start resource :read (:url-intent resource))]
      (result r* (conj effects (fetch-fx r*))))
    outcome))

(defn- with-trailing-fetch
  "`outcome` followed by a read for the current intent, when the value does not already answer it."
  [{:keys [resource] :as outcome}]
  (with-read outcome (pending? resource)))

(defn- with-reconciling-fetch
  "`outcome` followed by a re-read, always. A write whose outcome is unknown may have committed
  before it failed, so only the server can say whether it did."
  [outcome]
  (with-read outcome true))

;; The URL projection ----------------------------------------------------------

(defn- resolve-history-mode
  "The history mode for `gesture-class`: :push for navigations, :replace otherwise."
  [resource gesture-class]
  (get (:history-policy resource) gesture-class :replace))

(defn- url-write-fx
  "The :url-write effect projecting `params` onto `resource`'s own URL scope, in `mode`."
  [resource params mode]
  (effect/url-write (:resource/id resource) params mode))

;; Writes ----------------------------------------------------------------------

;; The write vocabulary as data: each op's method, whether it addresses the collection or a
;; member, and whether it carries a body. :move repositions a member and carries only the
;; destination, so full-replace :update stays the only way to edit record fields.
(def ^:private write-ops
  {:create {:method "POST"   :target :collection :body? true}
   :update {:method "PUT"    :target :member     :body? true}
   :delete {:method "DELETE" :target :member     :body? false}
   :move   {:method "PATCH"  :target :member     :body? true}})

;; Public for test purposes only
(defn write-request
  "What a write payload resolves to: {:request <the request value>} when the op vocabulary can
  express it, else {:defect <why it cannot>}. An op this client does not speak and a member op
  arriving without the member to address are different mistakes, so they get different defects."
  [resource write-id {:keys [op id record]} query]
  (if-let [{:keys [method target body?]} (get write-ops op)]
    (let [member? (= target :member)]
      (if (and member? (empty? (str id)))
        {:defect :member-write-without-id}
        {:request (request/request (assoc (transport-fields resource)
                                          :segment    (when member? id)
                                          :method     method
                                          :query      query
                                          :body       (when body? record)
                                          :request-id write-id))}))
    {:defect :unsupported-write}))

;; Public for test purposes only
(defn writing?
  "True while a write is in flight."
  [r]
  (some? (in-flight r :write)))

;; What a consumer sees --------------------------------------------------------

(defn project
  "The fields a consumer may depend on, everything else being internal bookkeeping. The accepted
  envelope loses its :request/id on the way out, so two identical refetches project equal views."
  [resource]
  {:accepted  (dissoc (:last-accepted resource) :request/id)
   :failure   (:last-failure resource)
   :intent    (:url-intent resource)
   :pending?  (pending? resource)
   :writing?  (writing? resource)})

(defn- notify-fx
  "The :notify-consumers effect for `r`. It carries the projection rather than the resource, so
  what a consumer is handed is decided here and no internal bookkeeping rides the effect."
  [r]
  (effect/notify-consumers (:resource/id r) (project r)))

;; Installing an answer, read or write -----------------------------------------

(defn- accept
  "`resource` holding `payload` as the value it answers with, retiring whatever failure preceded
  it."
  [resource payload]
  (assoc resource :last-accepted payload :last-failure nil))

(defn- with-failure
  "`resource` with the in-flight request of `kind` dropped and `failure` stashed as what it now
  answers with."
  [resource kind failure]
  (assoc (clear-in-flight resource kind) :last-failure failure))

(defn- record-failure
  "`with-failure`, notified. What every transition that only records a failure wants. One that
  also moves the intent builds its own outcome off `with-failure` instead."
  [resource kind failure]
  (let [resource* (with-failure resource kind failure)]
    (result resource* [(notify-fx resource*)])))

(defn- transport-members
  "The members a `cause` that produced no readable answer carries: :detail for :protocol, :error
  for :network."
  [cause payload]
  (case cause
    :protocol {:detail (:protocol-failure payload)}
    :network  {:error  (:error payload)}))

(defn- install-envelope
  "Install an accepted envelope that answered the request of `kind`. A write returns the full
  post-mutation state, so an accepted ack is the same envelope a read returns and both install
  the same way. A contract violation records as a failure. Otherwise the envelope installs,
  adopting the query echo unless the intent drifted while the request was in flight, and
  correcting the URL when the adopted echo is not what it already holds."
  [resource kind payload]
  (let [errors (validation/validate-contract payload)]
    (if (seq errors)
      (record-failure resource kind
                      (failure :contract kind (in-flight-query resource kind)
                               {:response payload :errors errors}))
      (let [echo     (:query payload)
            adopt?   (not (drifted? resource kind))
            correct? (and adopt? (not= echo (:url-intent resource)))
            r*       (cond-> (accept (clear-in-flight resource kind) payload)
                       adopt? (assoc :url-intent echo))]
        (result r* (cond-> []
                     correct? (conj (url-write-fx r* echo :replace))
                     :always  (conj (notify-fx r*))))))))

;; Transition helpers ----------------------------------------------------------
;; The first three are the SSR boot embed (§7.4).

(defn- boot
  "The initial connect for the current intent, with an optional leading diagnostic for an ignored
  embed. Nothing is accepted yet, so the trailing read always opens."
  [resource diag-code]
  (with-trailing-fetch
    (result resource (cond-> []
                       diag-code (conj (effect/diagnostic diag-code))
                       :always   (conj (notify-fx resource))))))

(defn- install-accepted-embed
  "Install an accepted boot embed exactly as a network response installs: a broken payload
  adjudicates as a contract failure, a valid one installs and fetches only when the URL has moved
  past what was embedded."
  [resource embed]
  (let [errors (validation/validate-contract embed)]
    (if (seq errors)
      (with-trailing-fetch
        (record-failure resource :read
                        (failure :contract :read (:query embed) {:response embed :errors errors})))
      ;; An embed never adopts its query echo and never corrects the URL. There is no in-flight
      ;; request for the intent to have drifted from, so a mismatch is answered by fetching.
      (let [installed (accept resource embed)]
        (with-trailing-fetch (result installed [(notify-fx installed)]))))))

(defn- install-rejected-embed
  "Install a rejected boot embed. An echo matching the intent installs as the failure and answers
  it, so no boot fetch. A stale rejection is diagnostics only, then a normal fetch."
  [resource embed]
  (if (= (:query embed) (:url-intent resource))
    (let [r* (assoc resource :last-failure
                    (failure :rejected :read (:query embed) {:response embed}))]
      (result r* [(notify-fx r*)]))
    (boot resource :stale-rejected-embed)))

(defn- targets-sibling?
  "True when `target-id` names a resource other than `resource`'s own. A nil target, or its own
  id, drives the resource itself."
  [resource target-id]
  (boolean (and target-id (not= target-id (:resource/id resource)))))

(defn- install-rejection
  "Record a read the server declined. A rejection of the query the URL currently holds, with a
  good value behind it, reverts the intent to the query that value answered and corrects the URL
  to match."
  [resource payload]
  (let [accepted-query (get-in resource [:last-accepted :query])
        revert?        (and (= (:query payload) (:url-intent resource))
                            (some? (:last-accepted resource)))
        cleared        (with-failure resource :read
                                     (failure :rejected :read (in-flight-query resource :read)
                                              {:response payload}))
        r*             (cond-> cleared
                         revert? (assoc :url-intent accepted-query))]
    (result r* (cond-> []
                 revert? (conj (url-write-fx r* accepted-query :replace))
                 :always (conj (notify-fx r*))))))

(defn- apply-intent-patch
  "Merge a patch into the resource's own intent. The URL is written whenever the intent moved, and
  a fetch follows only when nothing is in flight and the new intent is unanswered."
  [resource payload]
  (let [new-intent (query/canonicalize-query (merge (:url-intent resource) (:query-patch payload)))
        merged     (assoc resource :url-intent new-intent)
        mode       (resolve-history-mode resource (:gesture-class payload))
        moved?     (not= new-intent (:url-intent resource))]
    (with-trailing-fetch
      (result merged (cond-> []
                       moved?  (conj (url-write-fx merged new-intent mode))
                       :always (conj (notify-fx merged)))))))

(defn- hand-to-sibling
  "Hand a patch naming another resource to the executor, which resolves the name to an element.
  This resource's own value does not move."
  [resource payload]
  (result resource [(effect/route-intent (:target-id payload) (dissoc payload :target-id))]))

;; Transitions, one per event, each named after it, each returning a `result` -------

(defn- on-connected
  "A usable SSR boot embed installs first, otherwise a plain first fetch for the current intent."
  [resource embed]
  (cond
    (:protocol-failure embed)      (boot resource :broken-embed)
    (= :accepted (:outcome embed)) (install-accepted-embed resource embed)
    (= :rejected (:outcome embed)) (install-rejected-embed resource embed)
    :else                          (boot resource nil)))

(defn- on-url-changed
  "The address bar has already moved, so the intent is replaced outright rather than merged, and
  never written back."
  [resource intent]
  (let [replaced (assoc resource :url-intent intent)]
    (with-trailing-fetch (result replaced [(notify-fx replaced)]))))

(defn- on-response
  "A response for a request no longer in flight is ignored."
  [resource payload]
  (if-not (answers-in-flight-read? resource payload)
    (ignored resource :stale-response)
    (case (:outcome payload)
      :accepted (with-trailing-fetch (install-envelope resource :read payload))
      :rejected (with-trailing-fetch (install-rejection resource payload))
      ;; `wire/parse-envelope` yields one of the two above or a protocol-failure marker, which
      ;; arrives as :protocol-failed instead, so nothing should reach here. Said out loud rather
      ;; than passed over in silence.
      (ignored resource :unknown-outcome {:outcome (:outcome payload)}))))

(defn- on-intent-patch
  "A patch naming a sibling is routed rather than applied, and the routing is in the effect value
  so the executor only resolves the name to an element."
  [resource payload]
  (if (targets-sibling? resource (:target-id payload))
    (hand-to-sibling resource payload)
    (apply-intent-patch resource payload)))

(defn- on-read-failed
  "The :protocol-failed and :network-failed transitions, which differ only in `cause`. The read
  produced no answer, so the intent is still unanswered and a fresh read follows."
  [resource payload cause]
  (if-not (answers-in-flight? resource :read (:request/id payload))
    (ignored resource :stale-failure)
    (with-trailing-fetch
      (record-failure resource :read
                      (failure cause :read (in-flight-query resource :read)
                               (transport-members cause payload))))))

(defn- on-disconnected
  "An in-flight read is abandoned. An in-flight write is left alone, and the element carries its
  slot and counter across the boot so the ack still lands. Until it does the resource is still
  writing, so the single-flight rule refuses a second write."
  [resource]
  (if-let [id (:request/id (in-flight resource :read))]
    (result (clear-in-flight resource :read) [(effect/abort id)])
    (result resource)))

(defn- on-submit-write
  "One write at a time, so a submit landing while another is in flight is ignored, and a payload
  the op vocabulary cannot express is reported rather than sent."
  [resource payload]
  (if (writing? resource)
    (ignored resource :stale-write)
    (let [resource*                (start resource :write (:url-intent resource) {:payload payload})
          write-id                 (:request/id (in-flight resource* :write))
          {:keys [request defect]} (write-request resource* write-id payload (:url-intent resource))]
      (if request
        (result resource* [(notify-fx resource*) (effect/write request)])
        (ignored resource defect)))))

(defn- on-write-ack
  "An accepted ack carries the full post-mutation state, so it installs exactly as a read's
  envelope does. A rejection stands."
  [resource payload]
  (if-not (answers-in-flight? resource :write (:request/id payload))
    (ignored resource :stale-write)
    (if (= :accepted (:outcome payload))
      (with-trailing-fetch (install-envelope resource :write payload))
      (record-failure resource :write
                      (failure :rejected :write (in-flight-query resource :write)
                               {:response payload})))))

(defn- on-write-failed
  "Every failure reaching here left the write's outcome unknown. Only an explicit :rejected ack,
  which `on-write-ack` handles, is the server saying no."
  [resource payload]
  (if-not (answers-in-flight? resource :write (:request/id payload))
    (ignored resource :stale-write)
    (let [write (in-flight resource :write)
          cause (if (:protocol-failure payload) :protocol :network)]
      (with-reconciling-fetch
        (record-failure resource :write
                        (failure cause :write (:query write)
                                 (assoc (transport-members cause payload) :write write)))))))

(defn step
  "Takes a resource and an event, returns {:resource <the resource the event left behind>
  :effects [<what the executor must perform>]}."
  [resource event]
  (let [[event-k payload] event]
    (case event-k
      ;; reads
      :connected         (on-connected resource (:embed payload))
      :response          (on-response resource payload)
      :intent-patch      (on-intent-patch resource payload)
      ;; The executor could not resolve the target of a :route-intent. The gesture is lost either
      ;; way, so we register it here.
      :intent-unroutable (ignored resource :unroutable-intent payload)
      :url-changed       (on-url-changed resource payload)
      :protocol-failed   (on-read-failed resource payload :protocol)
      :network-failed    (on-read-failed resource payload :network)
      :disconnected      (on-disconnected resource)

      ;; writes
      :submit-write      (on-submit-write resource payload)
      :write-ack         (on-write-ack resource payload)
      :write-failed      (on-write-failed resource payload)

      ;; An event this vocabulary does not contain changes nothing, and says so.
      (ignored resource :unknown-event {:event event-k}))))
