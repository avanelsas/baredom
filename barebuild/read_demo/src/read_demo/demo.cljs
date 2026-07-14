(ns read-demo.demo
  (:require
   [baredom.components.x-alert.x-alert :as x-alert]
   [baredom.components.x-pagination.x-pagination :as x-pagination]
   [baredom.components.x-progress.x-progress :as x-progress]
   [baredom.components.x-search-field.x-search-field :as x-search-field]
   [baredom.components.x-spacer.x-spacer :as x-spacer]
   [baredom.components.x-stat.x-stat :as x-stat]
   [baredom.components.x-table.x-table :as x-table]
   [baredom.components.x-table-row.x-table-row :as x-table-row]
   [baredom.components.x-table-cell.x-table-cell :as x-table-cell]
   [read-demo.x-progress-consumer.x-progress-consumer :as x-progress-consumer]
   [read-demo.x-search-field-consumer.x-search-field-consumer :as x-search-field-consumer]
   [read-demo.x-stat-consumer.x-stat-consumer :as x-stat-consumer]
   [read-demo.x-table-consumer.x-table-consumer :as x-table-consumer]
   [barebuild.core :as core]))

;; Stands in for a host app: registers the BareDOM components it drives, the demo's own
;; consumers, and then the BareBuild runtime (server-resource) via barebuild.core.
(defn ^:export init []
  ;; driven BareDOM components
  (x-alert/init!)
  (x-pagination/init!)
  (x-progress/init!)
  (x-search-field/init!)
  (x-spacer/init!)
  (x-stat/init!)
  (x-table/init!)
  (x-table-row/init!)
  (x-table-cell/init!)
  ;; the demo's consumers (host-app code, not part of the BareBuild product)
  (x-progress-consumer/init!)
  (x-search-field-consumer/init!)
  (x-stat-consumer/init!)
  (x-table-consumer/init!)
  ;; the BareBuild runtime element
  (core/init))
