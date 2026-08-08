(ns demo.x-table-consumer.x-table-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.selector :as selector]
   [demo.x-table-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-data-field-key "data-field-key")

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

(defn- request-page!
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
    (.addEventListener pagination "page-change" request-page!)
    pagination))

(defn- delete-x-pagination!
  [^js parent]
  (when-let [existing (.querySelector parent "x-pagination")]
    (.remove existing)))

(defn- apply-pagination!
  [{:keys [page total-pages]} ^js parent]
  (if (or (nil? total-pages)
          (<= total-pages 1))
    (delete-x-pagination! parent)
    (let [existing-pagination (.querySelector parent "x-pagination")]
      (if existing-pagination
        (du/set-attr! existing-pagination "page" page)
        (let [new-pagination (create-x-pagination! {:page        page
                                                    :total-pages total-pages
                                                    :size        "md"})]
          (.appendChild parent new-pagination)
          (du/set-attr! new-pagination "page" page))))))

(defn- request-sort!
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

(defn- create-body-cell!
  "A cell showing `text`, tagged with its field key so re-render can find it."
  [text field-key]
  (let [cell (.createElement js/document "x-table-cell")]
    (du/set-attr! cell k-data-field-key field-key)
    (set! (.-textContent cell) (str text))
    cell))

(defn- create-header-cell!
  "A body cell that also announces its sort and asks for another."
  [label field-key sort-direction]
  (let [cell (create-body-cell! label field-key)]
    (du/set-attr! cell "type" "header")
    (du/set-attr! cell "scope" "col")
    (du/set-attr! cell "sortable" "")
    (du/set-attr! cell "sort-direction" sort-direction)
    (.addEventListener cell "x-table-cell-sort" request-sort!)
    cell))

;; What a row offers, in order. A third action is a row here.
(def ^:private row-actions
  [{:label "Edit"   :variant "primary" :on-press edit-row-request!}
   {:label "Delete" :variant "danger"  :on-press delete-row-request!}])

(defn- create-action-button!
  [{:keys [label variant on-press]} id]
  (let [button (.createElement js/document "x-button")]
    (du/set-attr! button "variant" variant)
    (du/set-attr! button "size" "sm")
    (du/set-attr! button "data-row-id" (str id))
    (set! (.-textContent button) label)
    (.addEventListener button "press" on-press)
    button))

(defn- create-actions!
  "The row's buttons, side by side."
  [id]
  (let [actions (.createElement js/document "span")]
    (.setProperty (.-style actions) "display" "inline-flex")
    (.setProperty (.-style actions) "gap" "0.2rem")
    (doseq [action row-actions]
      (.appendChild actions (create-action-button! action id)))
    actions))

(defn- create-header-actions-cell! []
  (let [cell (.createElement js/document "x-table-cell")]
    (du/set-attr! cell "type" "header")
    (set! (.-textContent cell) "Actions")
    cell))

(defn- create-body-actions-cell! [id]
  (doto (.createElement js/document "x-table-cell")
    (.appendChild (create-actions! id))))

(defn- create-header-row!
  [columns]
  (let [row (.createElement js/document "x-table-row")]
    (doseq [{k :key label :label sort-direction :sort-direction} columns]
      (.appendChild row (create-header-cell! label k sort-direction)))
    (.appendChild row (create-header-actions-cell!))
    row))

(defn- create-body-row!
  [cells columns id]
  (let [row (.createElement js/document "x-table-row")]
    (du/set-attr! row "data-row-id" (str id))
    (doseq [{k :key} columns]
      (.appendChild row (create-body-cell! (get cells k) k)))
    (.appendChild row (create-body-actions-cell! id))
    row))

(defn- current-row-ids [^js table]
  (->> (.querySelectorAll table "x-table-row[data-row-id]")
    array-seq
    (mapv #(du/get-attr % "data-row-id"))))

(defn- find-row [^js table id]
  (.querySelector table (str "x-table-row" (selector/attr= "data-row-id" id))))

(defn- ensure-header! [^js table columns]
  ;; written every render, not only when the header is built. x-table's change guard makes the
  ;; repeats free.
  (du/set-attr! table "columns" (model/grid-template columns))
  (if-let [header (.querySelector table "x-table-row:not([data-row-id])")]
    (doseq [{:keys [key sort-direction]} columns]
      (when-let [cell (.querySelector header (str "x-table-cell" (selector/attr= k-data-field-key key)))]
        (du/set-attr! cell "sort-direction" sort-direction)))
    (.appendChild table (create-header-row! columns))))

(defn- update-cells! [^js row cells columns]
  (doseq [{:keys [key]} columns]
    (when-let [^js cell (.querySelector row (str "x-table-cell" (selector/attr= k-data-field-key key)))]
      (let [text (str (get cells key))]
        (when (not= (.-textContent cell) text)
          (set! (.-textContent cell) text))))))

(defn- row-element [^js table columns {:keys [id cells new?]}]
  (if new?
    (create-body-row! cells columns id)
    (doto (find-row table (str id))
      (update-cells! cells columns))))

(defn- apply-plan!
  [^js table {:keys [remove order]} columns]
  (ensure-header! table columns)
  (doseq [id remove]
    (when-let [row (find-row table id)] (.remove row)))
  (loop [[row-vm & rest-vms] (reverse order)
         ^js anchor          nil]
    (when row-vm
      (let [^js el (row-element table columns row-vm)]
        (.insertBefore table el anchor)
        (recur rest-vms el)))))

(defn- render-table!
  [{:keys [columns rows]} ^js table]
  (when table
    (apply-plan! table (model/reconcile-plan (current-row-ids table) rows) columns)))

(defn- render! [^js table {accepted :accepted} ^js this]
  ;; a header built from an empty column list would be fixed at one column for good
  (when accepted
    (render-table! (model/accepted-response->view-model accepted) table)
    (apply-pagination! (:page-info accepted) this)))

(defn- on-failure! [_child {:keys [failure]} ^js this]
  (delete-x-alert! this)
  (when failure
    (.appendChild this (create-x-alert! {:type        "error"
                                         :dismissible true
                                         :text        (model/failure-message failure)}))))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-table"
    :render     render!
    :on-failure on-failure!}))
