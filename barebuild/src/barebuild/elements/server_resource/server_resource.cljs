(ns barebuild.elements.server-resource.server-resource
  (:require
   [barebuild.wire :as wire]
   [barebuild.resource :as resource]
   [barebuild.elements.server-resource.model :as model]
   [barebuild.utils :as utils]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [clojure.string :as str]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-resource  "__xServerResource")
(def ^:private k-consumers "__xConsumers")
(def ^:private k-popstate  "__xPopstate")
(def ^:private k-abort     "__xAbort")
(declare handle-event!)

(defn- construct-url-intent [resource-id]
  (let [current-url-params (js/URLSearchParams. (.-search js/location))
        prefix             (str resource-id ".")
        resource-id-keys   (filterv #(str/starts-with? % prefix)
                                    (js/Array.from (.keys current-url-params)))]
    (utils/canonicalize-query
     (into {}
           (for [k resource-id-keys]
             [(keyword (subs k (count prefix)))
              (.get current-url-params k)])))))

(defn- handle-popstate [^js el resource-id]
  (handle-event! el [:url-changed (construct-url-intent resource-id)]))

;; ── The engine ───────────────────────────────────────────────────────────────

(defn- map->query-params
  "Take a map of k v and turn it into a url query parameter string
  e.g. {tasks.sort \"end\"} -> \"tasks.sort=end\""
  [m]
  (let [params (js/URLSearchParams.)]
    (doseq [[k v] m]
      (.append params (name k) (str v)))
    (.toString params)))

(defn- execute-fetch! [^js el url request-id]
  ;; Transient transport handle: the AbortController lives outside every value (§6.3);
  ;; the lifecycle it tracks is traced through :active-request + the dispatched events.
  (let [controller (js/AbortController.)]
    (du/setv-untraced! el k-abort controller)
    (-> (js/fetch url #js {:signal (.-signal controller)})
      (.then (fn [^js resp]
               (if (.-ok resp)
                 (.text resp) ; text, not .json
                 (throw (js/Error. (str "HTTP " (.-status resp)))))))
      (.then (fn [^js body]
               (let [obj    (try (js/JSON.parse body) (catch :default _ nil))
                     result (wire/parse-envelope obj)] ; obj nil -> empty-body marker
                 (if (:protocol-failure result)
                   (handle-event! el [:protocol-failed (assoc result :request/id request-id)])
                   (handle-event! el [:response result])))))
      (.catch (fn [^js e]
                ;; an aborted fetch is intentional (disconnect / supersede), not a failure
                (when-not (= "AbortError" (.-name e))
                  (handle-event! el [:network-failed {:request/id request-id :error {:kind :offline}}])))))))

(defn- execute-write! [^js el url init request-id]
  (-> (js/fetch url init)
    (.then (fn [^js resp]
             (if (.-ok resp)
               (.text resp) ; text, not .json
               (throw (js/Error. (str "HTTP " (.-status resp)))))))
    (.then (fn [^js body]
             (let [obj     (try (js/JSON.parse body) (catch :default _ nil))
                   result  (wire/parse-ack obj)
                   result* (assoc result :write/id request-id)] ; obj nil -> empty-body marker
               (if (:protocol-failure result*)
                 (handle-event! el [:write-failed result*])
                 (handle-event! el [:write-ack result*])))))
    (.catch (fn [^js _e]
              (handle-event! el [:write-failed {:write/id request-id :error {:kind :offline}}])))))

(defn- run-effects!
  [^js el effects]
  (doseq [[fx m] effects]
    (case fx
      :fetch
      (let [request-id   (:request/id m)
            query        (:query m)
            ;; turn the map of k-v pairs into a query param
            query-params (map->query-params query)
            url         (str
                         (:endpoint m)
                         "?requestId="
                         request-id
                         (when-not (str/blank? query-params)
                           (str "&" query-params)))]
        (execute-fetch! el url request-id))

      :notify-consumers
      (let [r         (:resource m)
            consumers (du/getv el k-consumers)
            ctx       {:submit-intent! (fn [patch] (handle-event! el [:intent-patch patch]))
                       :submit-write!  (fn [payload] (handle-event! el [:submit-write payload]))}]
        (doseq [^js c consumers]
          (.applyResource c r ctx)))

      :url-write
      (let [new-url (utils/build-scoped-url (.-search js/location)
                                            (.-pathname js/location)
                                            (:resource/id m)
                                            (:params m))]
        (if (= (:mode m) :push)
          (.pushState js/history nil "" new-url)
          (.replaceState js/history nil "" new-url)))

      :abort
      (when-let [^js controller (du/getv el k-abort)]
        (.abort controller)
        (du/setv-untraced! el k-abort nil))

      :write
      (let [write-id (:write/id m)
            {:keys [op id record]} (:payload m)
            base-url (:endpoint m)
            url (if (= op :delete)
                  (str base-url  "/" id "?requestId=" write-id)
                  (str base-url "?requestId=" write-id))
            init (if (= op :create)
                   #js {:method "POST"
                        :headers #js {"content-type" "application/json"}
                        :body (js/JSON.stringify (clj->js record))}
                   #js {:method "DELETE"})]

        (execute-write! el url init write-id))

      nil)))

(defn- handle-event!
  [^js el event]
  (let [r                          (du/getv el k-resource)
        {:keys [resource effects]} (resource/step r event)]
    (du/setv! el k-resource resource)
    (run-effects! el effects)))

(defn- read-boot-embed
  "Read the <script type=\"application/json\"> embed inside the host and
  run it through the same parse path as a network response. Returns the parsed response
  value (or a protocol-failure marker for a broken embed), or nil when there is no embed."
  [^js el]
  (when-let [^js script (.querySelector el "script[type=\"application/json\"]")]
    (let [text (.-textContent script)
          obj  (try (js/JSON.parse text) (catch :default _ nil))]
      (wire/parse-envelope obj))))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- collect-consumers [^js el]
  (->> (array-seq (.-children el))
    (filterv
     (fn [^js c]
       (some? (.-applyResource c))))))

(defn- boot!
  "When reloading, read the current url and see if there are url parameters that
  have to be processed to get the element in the right state (e.g. table sorting).
  If there is an embedderd version, load that first."
  [^js el]
  (let [resource-id    "tasks"
        history-policy {:navigation :push}
        on-popstate    (fn [_e] (handle-popstate el resource-id))
        embed          (read-boot-embed el)]
    (du/setv! el k-resource {:resource/id    resource-id
                             :endpoint       (du/get-attr el model/attr-src)
                             :last-accepted  nil
                             :url-intent     (construct-url-intent resource-id)
                             :history-policy history-policy})
    ;; add a popstate event listener
    (du/setv! el k-popstate on-popstate)
    (.addEventListener js/window "popstate" on-popstate)
    (du/setv! el k-consumers (collect-consumers el))
    (handle-event! el [:connected {:embed embed}])))

(defn- disconnected! [^js el]
  (handle-event! el [:disconnected {}])
  (.removeEventListener js/window "popstate" (du/getv el k-popstate))
  (du/setv! el k-popstate nil))

(defn- connected! [^js el]
  (let [tags (->> (array-seq (.-children el))
               (map #(.. % -tagName toLowerCase))
               distinct)]
    (-> (js/Promise.all (clj->js (map #(js/customElements.whenDefined %) tags)))
      (.then (fn [] (boot! el))))))

;; register! always installs attributeChangedCallback and calls this — so it must exist.
(defn- attribute-changed! [_el _name _old _new] nil)

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!}))
