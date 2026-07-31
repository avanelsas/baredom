(ns barebuild.resource
  (:require [barebuild.utils :as utils]
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

(defn- read-request [r]
  (let [{:keys [query] rid :request/id} (:active-request r)]
    (assoc (utils/request {:endpoint   (:endpoint r)
                           :method     "GET"
                           :query      query
                           :request-id rid})
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

(defn- start-request
  "Save an active request in r. The ID is generated elsewhere."
  [r query]
  (let [n (inc (or (:request-count r) 0))]
    (assoc r
           :request-count  n
           :active-request {:request/id (str (:resource/id r) ":" n)
                            :query      query})))

(defn- requested-query
  [r]
  (get-in r [:active-request :query]))

(defn- fresh-request?
  "True when `request-id` matches the id of the in-flight read request."
  [r request-id]
  (= request-id (get-in r [:active-request :request/id])))

(defn- fresh-write?
  "True when `write-id` matches the id of the in-flight write request."
  [r write-id]
  (= write-id (get-in r [:active-write :write/id])))

(defn installable?
  "Ensure that the live request matches the in-flight request id."
  [r response]
  (fresh-request? r (:request/id response)))

(defn drifted?
  "If a new intent appeared while an old one was still in flight, signal it"
  [r]
  (not= (:url-intent r) (requested-query r)))

(defn- failure-query [f]
  (case (:failure f)
    (:rejected :contract) (get-in f [:response :query])
    (:network :protocol)  (:query f)
    nil))

(defn pending? [r]
  (or (some? (:active-request r))
      (let [intent (:url-intent r)]
        (and (not= intent (get-in r [:last-accepted :query]))
             (not= intent (failure-query (:last-failure r)))))))

(defn- with-trailing-fetch
  "If there is a current intent, still not answered, fire it again if a transition had cleared
  the active-request. Do nothing if the active-request is still set"
  [{:keys [resource effects] :as result}]
  (if (and (nil? (:active-request resource)) (pending? resource))
    (let [r* (start-request resource (:url-intent resource))]
      {:resource r*
       :effects  (conj (vec effects) (fetch-fx r*))})
    result))

(defn render-key
  "In order to check if an accepted envelope, remove the added request/id
  so that it can be compared to the saved value"
  [accepted]
  (dissoc accepted :request/id :write/id))

;; WRITE functionality ----------------------------------------------------------

;; The write vocabulary as data: each op maps to its method, whether it addresses the
;; collection or a member, and whether it carries a body. step resolves this into the
;; :write effect so the executor decides nothing.
;; :move is a positional command, not a partial merge of the record: it repositions a member
;; (server-owned rank) and carries only the destination, so full-replace :update stays the only
;; way to edit record fields. Adding it is one row here — the executor performs it and decides
;; nothing.
(def ^:private write-ops
  {:create {:method "POST"   :target :collection :body? true}
   :update {:method "PUT"    :target :member     :body? true}
   :delete {:method "DELETE" :target :member     :body? false}
   :move   {:method "PATCH"  :target :member     :body? true}})

(defn write-request
  "The :write effect value for a write payload, or nil when none can be built"
  [endpoint write-id {:keys [op id record]} query]
  (when-let [{:keys [method target body?]} (get write-ops op)]
    (let [member? (= target :member)]
      (when (or (not member?) (seq (str id)))
        (assoc (utils/request {:endpoint endpoint
                               :segment (when member? id)
                               :method method
                               :query query
                               :body (when body? record)
                               :request-id write-id})
               :write/id write-id)))))

(defn- start-write
  "Save an active write request in r. The ID is generated elsewhere."
  [r payload query]
  (let [n (inc (or (:write-count r) 0))]
    (assoc r
           :write-count  n
           :active-write {:write/id (str (:resource/id r) ":w" n)
                          :payload  payload
                          :query query})))

(defn writing? [r]
  (some? (:active-write r)))

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

(defn- write-drifted? [r]
  (not= (:url-intent r) (get-in r [:active-write :query])))

(defn- accepted-effects [resource echo correct?]
  (if correct?
    [(url-write-fx resource echo :replace) (notify-fx resource)]
    [(notify-fx resource)]))

(defn- diagnostic
  "A diagnostic effect value. The executor only console.debugs it — it drives no state."
  [code]
  [:diagnostic {:code code}])

(defn- record-failure
  "Clear the in-flight slot (:active-request or :active-write), stash the failure, notify."
  [resource slot-key failure]
  (let [resource* (assoc resource :last-failure failure slot-key nil)]
    {:resource resource* :effects [(notify-fx resource*)]}))

;; CONNECT / SSR boot embed (§7.4) ----------------------------------------------

(defn- boot-fetch
  "The initial connect fetch for the current intent, with an optional leading diagnostic for an
  embed that was ignored (broken, or a stale rejection)."
  [resource diag-code]
  (let [r* (start-request resource (:url-intent resource))]
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
        (record-failure resource :active-request
                        {:failure :contract :response embed :errors errors}))
      (let [installed (assoc resource :last-accepted embed :last-failure nil)]
        (if (pending? installed)
          (let [r* (start-request installed (:url-intent installed))]
            {:resource r* :effects [(notify-fx r*) (fetch-fx r*)]})
          {:resource installed :effects [(notify-fx installed)]})))))

(defn- connect-rejected-embed
  "A rejected boot embed the server already adjudicated. When its echo matches intent it installs
  as the failure and answers the intent, so no boot fetch. A stale rejection (the URL moved) is
  diagnostics only, then a normal fetch."
  [resource embed]
  (if (= (:query embed) (:url-intent resource))
    (let [r* (assoc resource :last-failure {:failure :rejected :response embed})]
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
          :accepted
          (let [errors (validate-contract payload)]
            (if (seq errors)
              (with-trailing-fetch
                (record-failure resource :active-request
                                {:failure :contract :response payload :errors errors}))
              (let [adopt? (not (drifted? resource))
                    {:keys [resource correct?]} (install-accepted (assoc resource :active-request nil)
                                                                  payload adopt?)]
                (with-trailing-fetch
                  {:resource resource
                   :effects  (accepted-effects resource (:query payload) correct?)}))))

          :rejected
          (let [accepted-query (get-in resource [:last-accepted :query])
                revert?        (and (= (:query payload) (:url-intent resource))
                                    (some? (:last-accepted resource)))
                cleared        (assoc resource
                                      :last-failure {:failure :rejected :response payload}
                                      :active-request nil)
                resource*      (if revert? (assoc cleared :url-intent accepted-query) cleared)]
            (with-trailing-fetch
              {:resource resource*
               :effects  (if revert?
                           [(url-write-fx resource* accepted-query :replace) (notify-fx resource*)]
                           [(notify-fx resource*)])}))

          {:resource resource :effects []}))

      :intent-patch
      (let [new-intent (utils/canonicalize-query (merge (:url-intent resource) (:query-patch payload)))
            merged     (assoc resource :url-intent new-intent)
            mode       (resolve-history-mode resource (:gesture-class payload))
            moved?     (not= new-intent (:url-intent resource)) ; owned intent changed?
            fetch?     (and (nil? (:active-request resource))   ; no request in flight
                            (pending? merged))                  ; and the new intent is unanswered
            r*         (if fetch? (start-request merged new-intent) merged)]
        {:resource r*
         :effects  (cond-> []
                     moved?  (conj (url-write-fx r* new-intent mode))
                     fetch?  (conj (fetch-fx r*))
                     :always (conj (notify-fx r*)))})

      :url-changed
      (let [replaced (assoc resource :url-intent payload)
            fetch?   (and (nil? (:active-request resource))
                          (pending? replaced))
            r*       (if fetch? (start-request replaced payload) replaced)]
        {:resource r*
         :effects  (cond-> []
                     fetch? (conj (fetch-fx r*))
                     :always (conj (notify-fx r*)))})

      :protocol-failed
      (if-not (fresh-request? resource (:request/id payload))
        {:resource resource :effects [(diagnostic :stale-failure)]}
        (with-trailing-fetch
          (record-failure resource :active-request
                          {:failure :protocol
                           :detail  (:protocol-failure payload)
                           :query   (requested-query resource)})))

      :network-failed
      (if-not (fresh-request? resource (:request/id payload))
        {:resource resource :effects [(diagnostic :stale-failure)]}
        (with-trailing-fetch
          (record-failure resource :active-request
                          {:failure :network
                           :error (:error payload)
                           :query (requested-query resource)})))

      :disconnected
      (if-let [id (get-in resource [:active-request :request/id])]
        {:resource (assoc resource :active-request nil)
         :effects  [[:abort {:request/id id}]]}
        {:resource resource :effects []})

      ;; writes
      :submit-write
      (if-not (writing? resource)
        (let [resource* (start-write resource payload (:url-intent resource))
              id        (get-in resource* [:active-write :write/id])
              write-req (write-request (:endpoint resource*) id payload (:url-intent resource))]
          (if write-req
            {:resource resource*
             :effects  [(notify-fx resource*)
                        [:write write-req]]}
            {:resource resource
             :effects  [(diagnostic :unsupported-write)]}))
        {:resource resource
         :effects  [(diagnostic :stale-write)]})

      :write-ack
      (if (fresh-write? resource (:write/id payload))
        (if (= :accepted (:outcome payload))
          (let [errors (validate-contract payload)]
            (if (seq errors)
              (with-trailing-fetch
                (record-failure resource :active-write
                                {:failure :contract :response payload :errors errors}))
              (let [adopt? (not (write-drifted? resource))
                    {:keys [resource correct?]} (install-accepted (assoc resource :active-write nil)
                                                                  payload adopt?)]
                (with-trailing-fetch
                  {:resource resource
                   :effects  (accepted-effects resource (:query payload) correct?)}))))
          (record-failure resource :active-write {:failure :rejected :response payload}))
        {:resource resource
         :effects  [(diagnostic :stale-write)]})

      :write-failed
      (if (fresh-write? resource (:write/id payload))
        (let [failure (if-let [detail (:protocol-failure payload)]
                        {:failure :protocol :detail detail :write (:active-write resource)}
                        {:failure :network :error (:error payload) :write (:active-write resource)})]
          (record-failure resource :active-write failure))
        {:resource resource
         :effects [(diagnostic :stale-write)]})

      {:resource resource :effects []})))
