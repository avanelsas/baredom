(ns demo.x-table-consumer.model)

(def tag-name "x-table-consumer")

(defn- network-message
  "The message for a read that got no usable answer. A status the server did send is named."
  [error]
  (case (:kind error)
    :http-status (case (:status error)
                   (401 403) "Your session has expired. Please sign in again."
                   404       "That resource was not found."
                   "The server returned an error. Please try again.")
    "Couldn't reach the server. Please try again."))

(defn failure-message
  "The alert text for a failure. A rejection shows the server's own message, every other cause
   this client's reading of the answer."
  [failure]
  (case (:cause failure)
    :rejected (get-in failure [:response :error :message])
    :network  (network-message (:error failure))
    :protocol "The server sent an unexpected response."
    :contract "The server's data didn't match the expected format."
    "Something went wrong."))

(defn- labeller
  "A value -> display text fn for one field, from its declared `options`. An unlisted value
   shows as itself."
  [{:keys [options]}]
  (if (seq options)
    (let [by-value (into {} (map (juxt :value :label)) options)]
      (fn [v] (get by-value v v)))
    identity))

(defn- labellers
  "One labeller per declared field, built once for the whole table rather than per cell."
  [fields]
  (into {} (map (juxt :key labeller)) fields))

(defn- row-cells
  "The displayed cell text per declared field."
  [fs row]
  (into {} (map (fn [[k label]] [k (label (get row k))])) fs))

(defn- column
  "One header column: the field key, and the direction it is sorted in right now."
  [{sorted-key :sort :keys [direction]} {:keys [key]}]
  {:key            key
   :sort-direction (if (= sorted-key key) direction "none")})

(defn accepted-response->view-model
  [accepted-response]
  (let [{:keys [query shape]}   accepted-response
        {:keys [id-key fields]} shape
        fs                      (labellers fields)]
    {:columns (mapv (partial column query) fields)
     :rows    (mapv (fn [row] {:id (get row id-key) :cells (row-cells fs row)})
                    (:value accepted-response))}))

(defn grid-template
  "The table's `columns` template: the fields split the width evenly, the actions column takes
   `max-content` so it cannot shrink below its buttons."
  [columns]
  (if (seq columns)
    (str "repeat(" (count columns) ",minmax(0,1fr)) max-content")
    "max-content"))

(defn reconcile-plan
  "The ids to remove, and the desired order flagged new or existing."
  [old-ids new-rows]
  (let [old-set (set old-ids)
        new-set (set (map (comp str :id) new-rows))]
    {:remove (vec (remove new-set old-ids))
     :order  (mapv #(assoc % :new? (not (old-set (str (:id %))))) new-rows)}))

(defn translate-gesture
  [field-key direction]
  {:query-patch (if (= direction "none")
                  {:sort nil :direction nil}
                  {:sort field-key :direction direction})
   :gesture-class :refinement})

(defn translate-pagination-gesture
  [page]
  {:query-patch
   {:page (str page)}
   :gesture-class :navigation})
