(ns barebuild.resource
  (:require [barebuild.utils :as utils]
            [baredom.utils.model :as mu]))

(defn- resolve-history-mode
  "Push for navigations, replace otherwise (default)."
  [resource gesture-class]
  (get (:history-policy resource) gesture-class :replace))

;; contract validation
(defn- err [path code message]
  {:path path :code code :message message})

(defn- validate-shape [shape]
  (cond-> []
    (not (:id-key shape)) (conj (err [:shape :id-key] :missing-id-key "shape is missing :id-key"))
    (not (:fields shape)) (conj (err [:shape :fields] :missing-fields "shape is missing :fields"))))

(defn- validate-value [v]
  (cond
    (not (sequential? v)) [(err [:value] :not-a-list "value is not a list")]
    (not (every? map? v)) [(err [:value] :not-maps "value is not a list of maps")]
    :else []))

(defn- validate-ids [id-key value]
  (let [ids (map #(get % id-key) value)]
    (cond-> []
      (some nil? ids)
      (conj (err [:value] :missing-id (str "some rows are missing \"" id-key "\"")))
      (not= (count ids) (count (distinct ids)))
      (conj (err [:value] :duplicate-id "row ids are not unique")))))

(defn- valid-datestr? [date-str]
  (not (js/isNaN (.parse js/Date date-str))))

(defn- validate-value-type [v type]
  (or (nil? v)
      (case type
        :string (string? v)
        :date   (valid-datestr? v)
        :number (number? v)
        :url    (and (string? v) (mu/safe-url? v))
        false)))

(defn- validate-row [row-idx row fields]
  (mapcat (fn [{:keys [key type]}]
            (cond
              (not (contains? row key))
              [(err [:value row-idx key] :missing-field
                    (str "row " row-idx " is missing field \"" key "\""))]

              (not (validate-value-type (get row key) type))
              [(err [:value row-idx key] :wrong-type
                    (str "row " row-idx " field \"" key "\" is not a " (name type)))]

              :else []))
          fields))

(defn- validate-rows [fields value]
  (mapcat (fn [[idx row]] (validate-row idx row fields))
          (map-indexed vector value)))

(defn- validate-contract
  "verifies if an accepted payload contains the right shape"
  [payload]
  (let [{:keys [shape value]} payload
        ;; use this to skip checks inside the value. No use to look further if this is firing
        value-errors (validate-value value)]

    (vec (concat (validate-shape shape)
                 value-errors
                 (when-not (seq value-errors)
                   (validate-ids (:id-key shape) value))
                 (when-not (seq value-errors)
                   (validate-rows (:fields shape) value))))))

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

(defn installable?
  "Ensure that the live request matches the in-flight request id."
  [r response]
  (= (:request/id response) (get-in r [:active-request :request/id])))

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
  the active-request. DO nothing if the active-request is still set"
  [{:keys [resource effects] :as result}]
  (if (and (nil? (:active-request resource)) (pending? resource))
    (let [r* (start-request resource (:url-intent resource))
          id (get-in r* [:active-request :request/id])]
      {:resource r*
       :effects  (conj (vec effects) [:fetch {:endpoint   (:endpoint r*)
                                              :query      (:url-intent r*)
                                              :request/id id}])})
    result))

(defn step
  "Takes a resource and event and returns (a possibly updated) resource
  and the effects that need to be called. Each step gets a unique resource/id"
  [resource event]
  (let [[event-k payload] event]
    (case event-k
      :connected
      (let [embed  (:embed payload)
            intent (:url-intent resource)]
        (if (= :accepted (:outcome embed))
          (let [installed (assoc resource :last-accepted embed :last-failure nil)]
            (if (pending? installed)
              (let [r* (start-request installed intent)
                    id (get-in r* [:active-request :request/id])]
                {:resource r*
                 :effects  [[:notify-consumers {:resource r*}]
                            [:fetch {:endpoint (:endpoint r*) :query intent :request/id id}]]})
              {:resource installed
               :effects  [[:notify-consumers {:resource installed}]]}))
          ;; no usable embed → last-accepted nil → always pending → always fetch
          (let [r* (start-request resource intent)
                id (get-in r* [:active-request :request/id])]
            {:resource r*
             :effects  [[:fetch {:endpoint (:endpoint r*) :query intent :request/id id}]
                        [:notify-consumers {:resource r*}]]})))

      :response
      (if-not (installable? resource payload)
        {:resource resource :effects [[:diagnostic :stale-response]]}
        (case (:outcome payload)
          :accepted
          (let [errors (validate-contract payload)]
            (if (seq errors)
              (with-trailing-fetch
                (let [resource* (assoc resource
                                       :last-failure {:failure :contract
                                                      :response payload
                                                      :errors   errors}
                                       :active-request nil)]
                  {:resource resource* :effects [[:notify-consumers {:resource resource*}]]}))
              (let [echo      (:query payload)
                    adopt?    (not (drifted? resource))
                    correct?  (and adopt? (not= echo (:url-intent resource)))
                    installed (assoc resource
                                     :last-accepted payload
                                     :last-failure nil
                                     :active-request nil)
                    resource* (if adopt? (assoc installed :url-intent echo) installed)]
                (with-trailing-fetch
                  {:resource resource*
                   :effects  (if correct?
                               [[:url-write {:resource/id (:resource/id resource*)
                                             :params      echo
                                             :mode        :replace}]
                                [:notify-consumers {:resource resource*}]]
                               [[:notify-consumers {:resource resource*}]])}))))

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
                           [[:url-write {:resource/id (:resource/id resource*)
                                         :params      accepted-query
                                         :mode        :replace}]
                            [:notify-consumers {:resource resource*}]]
                           [[:notify-consumers {:resource resource*}]])}))

          {:resource resource :effects []}))

      :intent-patch
      (let [new-intent (utils/canonicalize-query (merge (:url-intent resource) (:query-patch payload)))
            merged     (assoc resource :url-intent new-intent)
            mode       (resolve-history-mode resource (:gesture-class payload))
            moved?     (not= new-intent (:url-intent resource)) ; owned intent changed?
            fetch?     (and (nil? (:active-request resource))   ; no request in flight …
                            (pending? merged))                  ; … and the new intent is unanswered
            r*         (if fetch? (start-request merged new-intent) merged)
            id         (get-in r* [:active-request :request/id])]
        {:resource r*
         :effects  (cond-> []
                     moved? (conj [:url-write {:resource/id (:resource/id r*)
                                               :params      new-intent
                                               :mode        mode}])
                     fetch? (conj [:fetch {:endpoint   (:endpoint r*)
                                           :query      new-intent
                                           :request/id id}])
                     :always (conj [:notify-consumers {:resource r*}]))})

      :url-changed
      (let [replaced (assoc resource :url-intent payload)
            fetch?   (and (nil? (:active-request resource))
                          (pending? replaced))
            r*       (if fetch? (start-request replaced payload) replaced)
            id       (get-in r* [:active-request :request/id])]
        {:resource r*
         :effects  (cond-> []
                     fetch? (conj [:fetch {:endpoint   (:endpoint r*)
                                           :query      payload
                                           :request/id id}])
                     :always (conj [:notify-consumers {:resource r*}]))})

      :protocol-failed
      (if-not (= (:request/id payload) (get-in resource [:active-request :request/id]))
        {:resource resource :effects [[:diagnostic :stale-failure]]}
        (with-trailing-fetch
          (let [resource* (assoc resource
                                 :last-failure {:failure :protocol
                                                :detail  (:protocol-failure payload)
                                                :query   (requested-query resource)}
                                 :active-request nil)]
            {:resource resource* :effects [[:notify-consumers {:resource resource*}]]})))

      :network-failed
      (if-not (= (:request/id payload) (get-in resource [:active-request :request/id]))
        {:resource resource :effects [[:diagnostic :stale-failure]]}
        (with-trailing-fetch
          (let [resource* (assoc resource
                                 :last-failure {:failure :network
                                                :error (:error payload)
                                                :query (requested-query resource)}
                                 :active-request nil)]
            {:resource resource* :effects [[:notify-consumers {:resource resource*}]]})))

      :disconnected
      (if-let [id (get-in resource [:active-request :request/id])]
        {:resource (assoc resource :active-request nil)
         :effects  [[:abort {:request/id id}]]}
        {:resource resource :effects []})

      {:resource resource
       :effects    []})))
