(ns demo.x-table-consumer.x-table-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.x-table-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-data-field-key "data-field-key")

(declare x-table-cell-sort-event)

(defn- failure-message [last-failure]
  (case (:failure last-failure)
    :rejected (get-in last-failure [:response :error :message])
    :network  "Couldn't reach the server — please try again."
    :protocol "The server sent an unexpected response."
    :contract "The server's data didn't match the expected format."
    "Something went wrong."))

(defn- create-x-alert!
  [{:keys [text type dismissible timeout-ms]}]
  (let [alert (.createElement js/document "x-alert")]
    (du/set-attr! alert "type" type)
    (du/set-attr! alert "text" text)
    (du/set-attr! alert "dismissible" dismissible)
    (when timeout-ms
      (du/set-attr! alert "timeout-ms" timeout-ms))
    alert))

(defn- delete-x-alert! [^js parent]
  (when-let [existing (.querySelector parent "x-alert")]
    (.remove existing)))

(defn- x-pagination-page-request
  [^js e]
  (let [new-page (.. e -detail -page)
        gesture  (model/translate-pagination-gesture (str new-page))
        consumer (.closest (.-currentTarget e) "x-table-consumer")]
    (consumer-resource/submit-intent! consumer gesture)))

(defn- create-x-pagination!
  [{:keys [page total-pages size]}]
  (let [pagination (.createElement js/document "x-pagination")]
    (du/set-attr! pagination "page" page)
    (du/set-attr! pagination "total-pages" total-pages)
    (du/set-attr! pagination "size" size)
    (.addEventListener pagination "page-change" x-pagination-page-request)
    pagination))

(defn- maybe-delete-x-pagination!
  [^js parent]
  (when-let [existing (.querySelector parent "x-pagination")]
    (.remove existing)))

(defn- maybe-set-x-pagination!
  [{:keys [page total-pages]} ^js parent]
  (if (or (nil? total-pages)
          (<= total-pages 1))
    (maybe-delete-x-pagination! parent)
    (let [existing-pagination (.querySelector parent "x-pagination")]
      (if existing-pagination
        (du/set-attr! existing-pagination "page" page)
        (let [new-pagination (create-x-pagination! {:page        page
                                                    :total-pages total-pages
                                                    :size        "md"})]
          (.appendChild parent new-pagination)
          (du/set-attr! new-pagination "page" page))))))

(defn- x-table-cell-sort-event
  [^js e]
  (let [k         (du/get-attr (.-currentTarget e) k-data-field-key)
        direction (:direction (js->clj (.-detail e) :keywordize-keys true))
        consumer  (.closest (.-currentTarget e) "x-table-consumer")
        gesture   (model/translate-gesture k direction)]
    (consumer-resource/submit-intent! consumer gesture)))

(defn- delete-row-request! [^js e]
  (let [id       (du/get-attr (.-currentTarget e) "data-row-id")
        consumer (.closest (.-currentTarget e) "x-table-consumer")]
    (consumer-resource/submit-write! consumer {:op :delete :id id})))

(defn- edit-row-request! [^js e]
  (let [id       (du/get-attr (.-currentTarget e) "data-row-id")]
    ;; the create/edit form consumer handles this event
    (.dispatchEvent (.closest (.-currentTarget e) "server-resource")
                    (js/CustomEvent. "x-task-edit-request"
                                     #js {:detail #js {:id id} :bubbles true :composed true}))))

(defn- create-table-cell!
  [s is-header sort-direction]
  (let [cell (.createElement js/document "x-table-cell")]
    (when is-header
      (du/set-attr! cell "type" "header")
      (du/set-attr! cell "scope" "col")
      (du/set-attr! cell "sortable" "")
      (du/set-attr! cell "sort-direction" sort-direction)
      (.addEventListener cell "x-table-cell-sort" x-table-cell-sort-event))
    (set! (.-textContent cell) (str s))
    cell))

(defn- create-actions-table-cell!
  [is-header id]
  (let [cell (.createElement js/document "x-table-cell")]
    (if is-header
      (do
        (du/set-attr! cell "type" "header")
        (set! (.-textContent cell) "Actions"))
      (let [delete-button (.createElement js/document "x-button")
            edit-button   (.createElement js/document "x-button")]
        ; edit
        (du/set-attr! edit-button "variant" "primary")
        (du/set-attr! edit-button "size" "sm")
        (du/set-attr! edit-button "data-row-id" (str id))
        (set! (.-textContent edit-button) "Edit")
        (.addEventListener edit-button "press" edit-row-request!)
        ; delete
        (du/set-attr! delete-button "variant" "danger")
        (du/set-attr! delete-button "size" "sm")
        (du/set-attr! delete-button "data-row-id" (str id))
        (set! (.-textContent delete-button) "Delete")
        (.addEventListener delete-button "press" delete-row-request!)

        (.appendChild cell edit-button)
        (.appendChild cell delete-button)))
    cell))

(defn- create-header-row!
  [columns]
  (let [row (.createElement js/document "x-table-row")]
    (doseq [{k :key label :label sort-direction :sort-direction} columns]
      (let [cell (create-table-cell! label true sort-direction)]
        (du/set-attr! cell k-data-field-key k)
        (.appendChild row cell)))
    (.appendChild row (create-actions-table-cell! true nil))
    row))

(defn- create-body-row!
  [values id]
  (let [row (.createElement js/document "x-table-row")]
    (du/set-attr! row "data-row-id" (str id))
    (doseq [v values]
      (let [cell (create-table-cell! v false nil)]
        (.appendChild row cell)))
    (.appendChild row (create-actions-table-cell! false id))
    row))

(defn- current-row-ids [^js table]
  (->> (.querySelectorAll table "x-table-row[data-row-id]")
    array-seq
    (mapv #(du/get-attr % "data-row-id"))))

(defn- find-row [^js table id]
  (.querySelector table (str "x-table-row[data-row-id='" id "']")))

(defn- ensure-header! [^js table columns]
  (if-let [header (.querySelector table "x-table-row:not([data-row-id])")]
    (doseq [{:keys [key sort-direction]} columns]
      (when-let [cell (.querySelector header (str "x-table-cell[" k-data-field-key "='" key "']"))]
        (du/set-attr! cell "sort-direction" sort-direction)))
    (do
      (du/set-attr! table "columns" (str (inc (count columns))))
      (.appendChild table (create-header-row! columns)))))

(defn- update-cells! [^js row cells columns]
  (doseq [[i {:keys [key]}] (map-indexed vector columns)]
    (let [cell (aget (.-children row) i)
          text (str (get cells key))]
      (when (not= (.-textContent cell) text)
        (set! (.-textContent cell) text)))))

(defn- row-element [^js table columns {:keys [id cells new?]}]
  (if new?
    (create-body-row! (map #(get cells (:key %)) columns) id)
    (doto (find-row table (str id))
      (update-cells! cells columns))))

(defn- apply-plan!
  [^js table {:keys [remove order]} columns]
  (ensure-header! table columns)
  (doseq [id remove]
    (when-let [row (find-row table id)] (.remove row)))
  (reduce (fn [anchor row-vm]
            (let [el (row-element table columns row-vm)]
              (.insertBefore table el anchor)
              el))
          nil
          (reverse order)))

(defn- render-table!
  [{:keys [columns rows]} ^js table]
  (when table
    (apply-plan! table (model/reconcile-plan (current-row-ids table) rows) columns)))

(defn- render! [^js table accepted ^js this]
  (render-table! (model/accepted-response->view-model accepted) table)
  (maybe-set-x-pagination! (:page-info accepted) this))

(defn- on-failure! [_child failure ^js this]
  (delete-x-alert! this)
  (when failure
    (.appendChild this (create-x-alert! {:type        "error"
                                         :dismissible true
                                         :text        (failure-message failure)}))))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-table"
    :observed-attributes model/observed-attributes
    :render              render!
    :on-failure          on-failure!}))
