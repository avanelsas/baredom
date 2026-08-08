(ns demo.x-table-consumer.x-table-consumer
  (:require
   [barebuild.consumer-resource :as consumer-resource]
   [demo.alert :as alert]
   [demo.selector :as selector]
   [demo.task-edit :as task-edit]
   [demo.x-table-consumer.model :as model]
   [baredom.utils.dom :as du]))

(def ^:private k-field-key "data-field-key")
(def ^:private k-row-id "data-row-id")

(defn- consumer-of [^js e]
  (.closest (.-currentTarget e) model/tag-name))

(defn- request-page!
  [^js e]
  (consumer-resource/submit-intent!
   (consumer-of e)
   (model/translate-pagination-gesture (.. e -detail -page))))

(defn- create-x-pagination!
  [page total-pages]
  (let [pagination (.createElement js/document "x-pagination")]
    (du/set-attr! pagination "page" page)
    (du/set-attr! pagination "total-pages" total-pages)
    (du/set-attr! pagination "size" "md")
    (.addEventListener pagination "page-change" request-page!)
    pagination))

(defn- apply-pagination!
  "One page bar while there is more than one page to reach, none otherwise."
  [{:keys [page total-pages]} ^js parent]
  (let [existing (.querySelector parent "x-pagination")]
    (cond
      (or (nil? total-pages) (<= total-pages 1)) (when existing (.remove existing))
      existing                                   (du/set-attr! existing "page" page)
      :else                                      (.appendChild parent (create-x-pagination! page total-pages)))))

(defn- request-sort!
  [^js e]
  (consumer-resource/submit-intent!
   (consumer-of e)
   (model/translate-gesture (du/get-attr (.-currentTarget e) k-field-key)
                            (.. e -detail -direction))))

(defn- delete-row-request! [^js e]
  (consumer-resource/submit-write!
   (consumer-of e)
   {:op :delete :id (du/get-attr (.-currentTarget e) k-row-id)}))

(defn- edit-row-request! [^js e]
  (task-edit/request! (.-currentTarget e) (du/get-attr (.-currentTarget e) k-row-id)))

(defn- create-body-cell!
  "A cell showing `text`, tagged with its field key so re-render can find it."
  [text field-key]
  (let [cell (.createElement js/document "x-table-cell")]
    (du/set-attr! cell k-field-key field-key)
    (set! (.-textContent cell) (str text))
    cell))

(defn- create-header-cell!
  "A body cell that also announces its sort and asks for another."
  [field-key sort-direction]
  (let [cell (create-body-cell! field-key field-key)]
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
    (du/set-attr! button k-row-id (str id))
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
    (doseq [{:keys [key sort-direction]} columns]
      (.appendChild row (create-header-cell! key sort-direction)))
    (.appendChild row (create-header-actions-cell!))
    row))

(defn- create-body-row!
  [cells columns id]
  (let [row (.createElement js/document "x-table-row")]
    (du/set-attr! row k-row-id (str id))
    (doseq [{k :key} columns]
      (.appendChild row (create-body-cell! (get cells k) k)))
    (.appendChild row (create-body-actions-cell! id))
    row))

(defn- current-row-ids [^js table]
  (->> (.querySelectorAll table (str "x-table-row[" k-row-id "]"))
    array-seq
    (mapv #(du/get-attr % k-row-id))))

(defn- find-row [^js table id]
  (.querySelector table (str "x-table-row" (selector/attr= k-row-id id))))

(defn- find-cell [^js row key]
  (.querySelector row (str "x-table-cell" (selector/attr= k-field-key key))))

(defn- ensure-header! [^js table columns]
  ;; written every render, not only when the header is built. x-table's change guard makes the
  ;; repeats free.
  (du/set-attr! table "columns" (model/grid-template columns))
  (if-let [header (.querySelector table (str "x-table-row:not([" k-row-id "])"))]
    (doseq [{:keys [key sort-direction]} columns]
      (when-let [cell (find-cell header key)]
        (du/set-attr! cell "sort-direction" sort-direction)))
    (.appendChild table (create-header-row! columns))))

(defn- update-cells! [^js row cells columns]
  (doseq [{:keys [key]} columns]
    (when-let [^js cell (find-cell row key)]
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
  (if failure
    (alert/show! this (model/failure-message failure))
    (alert/clear! this)))

(defn init! []
  (consumer-resource/register!
   {:tag        model/tag-name
    :child-tag  "x-table"
    :render     render!
    :on-failure on-failure!}))
