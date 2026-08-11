(ns barebuild.resource
  (:require [barebuild.effect :as effect]
            [barebuild.utils.query :as query]
            [barebuild.utils.request :as request]
            [barebuild.validation :as validation]))

;; The resource value ----------------------------------------------------------

;; Which gesture classes push a history entry rather than replace one.
(def ^:private default-history-policy {:navigation :push})

;; The two kinds of request as data: where each one's in-flight record lives, where its counter
;; lives, and how its ids are named. One set of functions serves both, told apart only by the kind
;; they are handed.
(def ^:private request-kinds
  {:read  {:slot :active-request :counter :request-count :id-prefix ""}
   :write {:slot :active-write   :counter :write-count   :id-prefix "w"}})

(def ^:private no-requests
  "Both request kinds at rest: nothing in flight, nothing counted yet."
  {:active-request nil :request-count 0
   :active-write   nil :write-count   0})

(defn initial
  "The resource a fresh connection starts from, and the one place its shape is written down.
  `request-config` is whatever the host declared as attributes, `carried` what `carry-over`
  selected out of the connection before this one. Both win over the defaults."
  [{:keys [resource/id endpoint url-intent history-policy request-config carried]}]
  (merge no-requests
         {:resource/id    id
          :endpoint       endpoint
          :last-accepted  nil
          :last-failure   nil
          :last-write     nil
          :url-intent     url-intent
          :history-policy (or history-policy default-history-policy)}
         request-config
         carried))

;; The step result --------------------------------------------------------------
;; The resource the event left behind, and the effects the executor must perform. Built here and
;; nowhere else, so :effects is always a vector a transition can `conj` onto.
(defn- result
  ([resource] (result resource []))
  ([resource effects] {:resource resource :effects effects}))

(defn- ignored
  "A result that moves nothing, carrying a diagnostic naming `code`."
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
;;
;; :response is the server's answer minus the id this client minted, so two identical refusals
;; compare equal.
(defn- failure
  "A failure value of the shape above. The three named members are merged last, so `extra` cannot
  redefine them."
  ([cause kind query] (failure cause kind query nil))
  ([cause kind query extra]
   (merge (cond-> extra
            (:response extra) (update :response dissoc :request/id))
          {:cause cause :for kind :query query})))

;; Requests in flight, read and write alike ------------------------------------

(defn carry-over
  "The part of `resource` the next connection inherits, and the only part that survives a boot. An
  in-flight write's slot has to still be there when its ack lands, its counter has to keep counting
  so the next write cannot mint an id the orphan already answers to, and `:last-write` has to be
  there for that ack to settle. Reads need none of it. Slot and counter come off the table so a
  renamed slot moves both halves."
  [resource]
  (let [{:keys [slot counter]} (request-kinds :write)]
    (select-keys resource [slot counter :last-write])))

(defn- in-flight
  "The in-flight request of `kind` in `resource`, or nil."
  [resource kind]
  (get resource (:slot (request-kinds kind))))

(defn- clear-in-flight
  "`resource` with its in-flight request of `kind` dropped."
  [resource kind]
  (assoc resource (:slot (request-kinds kind)) nil))

(defn- in-flight-query
  "The query the in-flight request of `kind` was issued for, or nil when none is."
  [resource kind]
  (:query (in-flight resource kind)))

(defn- transport-fields
  "The fields every request off `resource` carries whatever it asks for: where the endpoint is, and
  how the executor is to make the call."
  [resource]
  (select-keys resource [:endpoint :credentials :headers :timeout]))

(defn- start
  "`resource` with an in-flight request of `kind` opened for `query`, numbered from that kind's
  counter and named from the resource id. `extra` is whatever else that kind's record carries, a
  write's payload above all."
  ([resource kind query] (start resource kind query nil))
  ([resource kind query extra]
   (let [{:keys [slot counter id-prefix]} (request-kinds kind)
         n                                (inc (counter resource))]
     (assoc resource
            counter n
            slot    (merge {:request/id (str (:resource/id resource) ":" id-prefix n)
                            :query      query}
                           extra)))))

(defn- answers-in-flight?
  "True when `request-id` names the in-flight request of `kind`."
  [resource kind request-id]
  (= request-id (:request/id (in-flight resource kind))))

;; Which kind of request each answering event answers. One naming a request no longer in flight is
;; dropped by `step` before dispatching.
(def ^:private answered-kind
  {:response        :read
   :protocol-failed :read
   :network-failed  :read
   :write-ack       :write
   :write-failed    :write})

(defn- stale-answer
  "The kind `event` answers when the request it names is no longer in flight, nil otherwise."
  [resource [event-k payload]]
  (when-let [kind (answered-kind event-k)]
    (when-not (answers-in-flight? resource kind (:request/id payload))
      kind)))

(defn- drifted?
  "True when the intent moved on while the request of `kind` was in flight."
  [resource kind]
  (not= (:url-intent resource) (in-flight-query resource kind)))

;; The read, and when one is wanted --------------------------------------------

(defn- read-request [resource]
  (let [{:keys [query] rid :request/id} (in-flight resource :read)]
    (request/request (assoc (transport-fields resource)
                            :method     "GET"
                            :query      query
                            :request-id rid))))

(defn- fetch-fx
  "The :fetch effect for the in-flight read request."
  [resource]
  (effect/fetch (read-request resource)))

(defn- read-failure-query
  "The query a read failure concerns, nil for a write failure. A write answers nothing about
  whether the current intent has been fetched."
  [f]
  (when (= :read (:for f)) (:query f)))

(defn- answered?
  "True when `resource` already holds an answer for the current intent, accepted or refused. The
  answer has to exist, so a resource that has fetched nothing answers nothing."
  [resource]
  (let [intent (:url-intent resource)]
    (or (and (some? (:last-accepted resource))
             (= intent (get-in resource [:last-accepted :query])))
        (and (some? (:last-failure resource))
             (= intent (read-failure-query (:last-failure resource)))))))

;; Public for test purposes only
(defn pending?
  "True while a read is in flight, or the current intent has no answer yet."
  [resource]
  (or (some? (in-flight resource :read))
      (not (answered? resource))))

(defn- open-read
  "`built` with a read for the current intent opened and its :fetch appended, unless one already is
  in flight, so the single-flight rule lives here rather than at the two callers."
  [{:keys [resource effects] :as built}]
  (if (in-flight resource :read)
    built
    (let [started (start resource :read (:url-intent resource))]
      (result started (conj effects (fetch-fx started))))))

(defn- abandon-read
  "`built` with any in-flight read dropped and an :abort for it appended. The answer to a read
  nobody wants any more is ended rather than waited on."
  [{:keys [resource effects] :as built}]
  (if-let [request-id (:request/id (in-flight resource :read))]
    (result (clear-in-flight resource :read) (conj effects (effect/abort request-id)))
    built))

(defn- with-trailing-fetch
  "`built` followed by a read for the current intent, when the value does not already answer it."
  [{:keys [resource] :as built}]
  (cond-> built (pending? resource) open-read))

(defn- with-reconciling-fetch
  "`built` followed by a read issued after the write was, always. A write whose outcome is unknown
  may have committed before it failed, so only the server can say whether it did. A read already in
  flight may have been served before that commit, so it is abandoned rather than counted as the
  observation."
  [built]
  (open-read (abandon-read built)))

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

(defn- write-request
  "What the in-flight write resolves to: {:request <the request value>} when the op vocabulary can
  express it, else {:defect <why it cannot>}. An op this client does not speak and a member op
  arriving without the member to address are different mistakes, so they get different defects."
  [resource]
  (let [{:keys [query payload] rid :request/id} (in-flight resource :write)
        {:keys [op id record]}                  payload]
    (if-let [{:keys [method target body?]} (get write-ops op)]
      (let [member? (= target :member)]
        (if (and member? (empty? (str id)))
          {:defect :member-write-without-id}
          {:request (request/request (assoc (transport-fields resource)
                                            :segment    (when member? id)
                                            :method     method
                                            :query      query
                                            :body       (when body? record)
                                            :request-id rid))}))
      {:defect :unsupported-write})))

;; Public for test purposes only
(defn writing?
  "True while a write is in flight."
  [resource]
  (some? (in-flight resource :write)))

;; The write the resource last submitted, and how it ended. A sibling of :last-accepted and
;; :last-failure, and like them not derivable. :active-write stays the single-flight slot and still
;; clears on settlement, this is the history a consumer reads.

(defn- opened-write
  "`resource` with the write it just opened recorded as the one it last submitted."
  [resource]
  (assoc resource :last-write (assoc (in-flight resource :write) :status :in-flight)))

(defn- settled-write
  "`resource` with the write it last submitted recorded as having ended in `status`. Single-flight
  and the staleness guard mean that is always the write now settling."
  [resource status]
  (assoc-in resource [:last-write :status] status))

;; What a consumer sees --------------------------------------------------------

(defn- write-view
  "The write the resource last submitted as a consumer sees it. Nil until one has been."
  [resource]
  (when-let [w (:last-write resource)]
    (select-keys w [:payload :status])))

(defn project
  "The fields a consumer may depend on, everything else being internal bookkeeping. Nothing here
  keeps the id of the exchange that produced it, so two identical refetches, refusals or writes
  project equal views."
  [resource]
  {:accepted  (dissoc (:last-accepted resource) :request/id)
   :failure   (:last-failure resource)
   :intent    (:url-intent resource)
   :pending?  (pending? resource)
   :writing?  (writing? resource)
   :write     (write-view resource)})

(defn- notify-fx
  "The :notify-consumers effect for `resource`. It carries the projection rather than the resource,
  so what a consumer is handed is decided here and no internal bookkeeping rides the effect."
  [resource]
  (effect/notify-consumers (:resource/id resource) (project resource)))

(defn- announced
  "`built` with a :notify-consumers appended when the view moved from `before`, or when `force?`
  overrides that."
  [before {:keys [resource effects]} force?]
  (if (or force? (not= (project before) (project resource)))
    (result resource (conj effects (notify-fx resource)))
    (result resource effects)))

;; Installing an answer, read or write -----------------------------------------

(defn- retires-failure?
  "True when an answer of `kind` settles the failure `resource` is holding. A write ack carries the
  full post-mutation state, so it answers both the write and the read that state belongs to. A read
  answers only the read, leaving an unconfirmed write still reported."
  [resource kind]
  (or (= :write kind)
      (= :read (:for (:last-failure resource)))))

(defn- accept
  "`resource` holding `payload` as the value it answers with, retiring the failure before it when
  that failure has been answered."
  [resource kind payload]
  (cond-> (assoc resource :last-accepted payload)
    (retires-failure? resource kind) (assoc :last-failure nil)))

(defn- fail
  "`resource` with the in-flight request of `kind` dropped and `failure` stashed as what it now
  answers with. The sibling of `accept`, and like it a resource rather than a result."
  [resource kind failure]
  (assoc (clear-in-flight resource kind) :last-failure failure))

(defn- record-failure
  "A result holding `failure` as what the resource now answers with."
  [resource kind failure]
  (result (fail resource kind failure)))

(defn- transport-members
  "The members a `cause` that produced no readable answer carries: :detail for :protocol, :error
  for :network."
  [cause payload]
  (case cause
    :protocol {:detail (:protocol-failure payload)}
    :network  {:error  (:error payload)}))

(defn- install-envelope
  "Install an accepted envelope that answered the request of `kind`, a write's ack being the same
  envelope a read returns. A contract violation records as a failure. Otherwise it installs,
  adopting the query echo unless the intent drifted while the request was in flight, and correcting
  the URL when the adopted echo is not what it already holds."
  [resource kind payload]
  (let [errors (validation/validate-contract payload)]
    (if (seq errors)
      (record-failure resource kind
                      (failure :contract kind (in-flight-query resource kind)
                               {:response payload :errors errors}))
      (let [echo      (:query payload)
            adopt?    (not (drifted? resource kind))
            correct?  (and adopt? (not= echo (:url-intent resource)))
            installed (cond-> (accept (clear-in-flight resource kind) kind payload)
                        adopt? (assoc :url-intent echo))]
        (result installed (cond-> []
                              correct? (conj (url-write-fx installed echo :replace))))))))

;; Transition helpers ----------------------------------------------------------
;; The first three are the SSR boot embed (§7.4).

(defn- boot
  "The initial connect for the current intent, with an optional leading diagnostic for an ignored
  embed. Nothing is accepted yet, so the trailing read always opens."
  [resource diag-code]
  (with-trailing-fetch
    (result resource (cond-> []
                         diag-code (conj (effect/diagnostic diag-code))))))

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
      (with-trailing-fetch (result (accept resource :read embed))))))

(defn- install-rejected-embed
  "Install a rejected boot embed. An echo matching the intent installs as the failure and answers
  it, so no boot fetch. A stale rejection is diagnostics only, then a normal fetch."
  [resource embed]
  (if (= (:query embed) (:url-intent resource))
    (result (assoc resource :last-failure
                     (failure :rejected :read (:query embed) {:response embed})))
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
        cleared        (fail resource :read
                                     (failure :rejected :read (in-flight-query resource :read)
                                              {:response payload}))
        reverted       (cond-> cleared
                         revert? (assoc :url-intent accepted-query))]
    (result reverted (cond-> []
                         revert? (conj (url-write-fx reverted accepted-query :replace))))))

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
                         moved? (conj (url-write-fx merged new-intent mode)))))))

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
  (with-trailing-fetch (result (assoc resource :url-intent intent))))

(defn- on-response
  [resource payload]
  (case (:outcome payload)
    :accepted (with-trailing-fetch (install-envelope resource :read payload))
    :rejected (with-trailing-fetch (install-rejection resource payload))
    ;; `wire/parse-envelope` yields one of the two above or a protocol-failure marker, which
    ;; arrives as :protocol-failed instead, so nothing should reach here. Said out loud rather
    ;; than passed over in silence.
    (ignored resource :unknown-outcome {:outcome (:outcome payload)})))

(defn- on-intent-patch
  "A patch naming a sibling is routed rather than applied, and the routing is in the effect value
  so the executor only resolves the name to an element."
  [resource payload]
  (if (targets-sibling? resource (:target-id payload))
    (hand-to-sibling resource payload)
    (apply-intent-patch resource payload)))

(defn- on-refresh
  "Read the current intent again. Nothing about what the read is *about* changes, which is the whole
  of the difference from an intent patch: no merge, no URL write, and the value the resource already
  holds is left standing until an answer replaces it. A read already in flight is the answer to this
  request, so `open-read` declining is the correct outcome rather than a missed refresh."
  [resource]
  (open-read (result resource)))

(defn- on-read-failed
  "The :protocol-failed and :network-failed transitions, which differ only in `cause`. The read
  produced no answer, so the intent is still unanswered and a fresh read follows."
  [resource payload cause]
  (with-trailing-fetch
    (record-failure resource :read
                    (failure cause :read (in-flight-query resource :read)
                             (transport-members cause payload)))))

(defn- on-disconnected
  "An in-flight read is abandoned. An in-flight write is left alone, and the element carries its
  slot and counter across the boot so the ack still lands. Until it does the resource is still
  writing, so the single-flight rule refuses a second write."
  [resource]
  (abandon-read (result resource)))

(defn- on-submit-write
  "One write at a time, so a submit landing while another is in flight is refused, and a payload
  the op vocabulary cannot express is reported rather than sent."
  [resource payload]
  (if (writing? resource)
    (ignored resource :write-in-flight)
    (let [started                  (start resource :write (:url-intent resource) {:payload payload})
          {:keys [request defect]} (write-request started)]
      (if request
        (let [opened (opened-write started)]
          (result opened [(effect/write request)]))
        (ignored resource defect)))))

(defn- on-write-ack
  "An accepted ack carries the full post-mutation state, so it installs exactly as a read's
  envelope does. A rejection stands. Either way the write it answers is recorded as settled."
  [resource payload]
  (if (= :accepted (:outcome payload))
    (with-trailing-fetch (install-envelope (settled-write resource :accepted) :write payload))
    (record-failure (settled-write resource :rejected) :write
                    (failure :rejected :write (in-flight-query resource :write)
                             {:response payload}))))

(defn- on-write-failed
  "Every failure reaching here left the write's outcome unknown. Only an explicit :rejected ack,
  which `on-write-ack` handles, is the server saying no. Consumers are told after the reconciling
  read opens, so the failure arrives with `:pending?` set rather than beside an idle-looking value."
  [resource payload]
  (let [write (in-flight resource :write)
        cause (if (:protocol-failure payload) :protocol :network)]
    (with-reconciling-fetch
     (result (fail (settled-write resource :failed) :write
                   (failure cause :write (:query write)
                            (assoc (transport-members cause payload) :write write)))))))

(defn- transition
  "The result `event` leaves behind, before anything is announced."
  [resource [event-k payload]]
  (case event-k
    ;; reads
    :connected         (on-connected resource (:embed payload))
    :response          (on-response resource payload)
    :intent-patch      (on-intent-patch resource payload)
    :refresh           (on-refresh resource)
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
    (ignored resource :unknown-event {:event event-k})))

;; The two events the moved-view rule cannot decide. :connected has nothing projected before it, so
;; its view never reads as moved. :disconnected leaves no consumer to hand a view to.
(def ^:private announce-rule {:connected :always :disconnected :never})

(defn step
  "Takes a resource and an event, returns {:resource <the resource the event left behind>
  :effects [<what the executor must perform>]}. The :notify-consumers is appended here, once, when
  the view moved, so no transition places one."
  [resource event]
  (if-let [kind (stale-answer resource event)]
    (ignored resource :stale-answer {:for kind :event (first event)})
    (let [built (transition resource event)]
      (case (announce-rule (first event))
        :never  built
        :always (announced resource built true)
        (announced resource built false)))))
