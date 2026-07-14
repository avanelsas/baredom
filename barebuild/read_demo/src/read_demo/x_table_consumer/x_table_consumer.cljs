(ns read-demo.x-table-consumer.x-table-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [read-demo.x-table-consumer.model :as model]
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

(defn- create-header-row!
  [columns]
  (let [row (.createElement js/document "x-table-row")]
    (doseq [{k :key label :label sort-direction :sort-direction} columns]
      (let [cell (create-table-cell! label true sort-direction)]
        (du/set-attr! cell k-data-field-key k)
        (.appendChild row cell)))
    row))

(defn- create-body-row!
  [values]
  (let [row (.createElement js/document "x-table-row")]
    (doseq [v values]
      (let [cell (create-table-cell! v false nil)]
        (.appendChild row cell)))
    row))

(defn- render-table!
  [{:keys [columns rows]} table]
  (when table
    (set! (.-innerHTML table) "")
    (du/set-attr! table "columns" (str (count columns)))
    (.appendChild table (create-header-row! columns))
    (doseq [r rows]
      (let [values (map (fn [col] (get (:cells r) (:key col))) columns)
            row    (create-body-row! values)]
        (.appendChild table row)))))

(defn- render! [^js table accepted ^js this]
  (render-table! (model/accepted-response->view-model accepted) table)
  (maybe-set-x-pagination! (:page-info accepted) this))

(defn- on-failure! [_child failure ^js this]
  (delete-x-alert! this)
  (when failure
    (.appendChild this (create-x-alert! {:type        "error"
                                         :dismissible true
                                         :text        (failure-message failure)}))))

(defn- on-pending! [^js table pending _this]
  (if pending
    (do (du/set-attr! table "aria-busy" "true")
      (set! (.. table -style -opacity) "0.6"))
    (do (du/remove-attr! table "aria-busy")
      (set! (.. table -style -opacity) ""))))

(defn init! []
  (consumer-resource/register!
   {:tag                 model/tag-name
    :child-tag           "x-table"
    :observed-attributes model/observed-attributes
    :render              render!
    :on-failure          on-failure!
    :on-pending          on-pending!}))
