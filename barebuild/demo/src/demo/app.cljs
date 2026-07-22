(ns demo.app
  (:require
   [baredom.components.x-alert.x-alert :as x-alert]
   [baredom.components.x-button.x-button :as x-button]
   [baredom.components.x-date-picker.x-date-picker :as x-date-picker]
   [baredom.components.x-form.x-form :as x-form]
   [baredom.components.x-form-field.x-form-field :as x-form-field]
   [baredom.components.x-modal.x-modal :as x-modal]
   [baredom.components.x-pagination.x-pagination :as x-pagination]
   [baredom.components.x-progress.x-progress :as x-progress]
   [baredom.components.x-search-field.x-search-field :as x-search-field]
   [baredom.components.x-select.x-select :as x-select]
   [baredom.components.x-spacer.x-spacer :as x-spacer]
   [baredom.components.x-stat.x-stat :as x-stat]
   [baredom.components.x-table.x-table :as x-table]
   [baredom.components.x-table-row.x-table-row :as x-table-row]
   [baredom.components.x-table-cell.x-table-cell :as x-table-cell]
   [baredom.components.x-typography.x-typography :as x-typography]
   [demo.x-progress-consumer.x-progress-consumer :as x-progress-consumer]
   [demo.x-search-field-consumer.x-search-field-consumer :as x-search-field-consumer]
   [demo.x-stat-consumer.x-stat-consumer :as x-stat-consumer]
   [demo.x-table-consumer.x-table-consumer :as x-table-consumer]
   [demo.x-task-form-consumer.x-task-form-consumer :as x-task-form-consumer]
   [barebuild.core :as core]))

;; Stands in for a host app: registers the BareDOM components it drives, the demo's own
;; consumers, and then the BareBuild runtime (server-resource) via barebuild.core.
(defn ^:export init []
  ;; driven BareDOM components
  (x-alert/init!)
  (x-button/init!)
  (x-date-picker/init!)
  (x-form/init!)
  (x-form-field/init!)
  (x-modal/init!)
  (x-pagination/init!)
  (x-progress/init!)
  (x-search-field/init!)
  (x-select/init!)
  (x-spacer/init!)
  (x-stat/init!)
  (x-table/init!)
  (x-table-row/init!)
  (x-table-cell/init!)
  (x-typography/init!)

  ;; the demo's consumers (host-app code, not part of the BareBuild product)
  (x-progress-consumer/init!)
  (x-search-field-consumer/init!)
  (x-stat-consumer/init!)
  (x-table-consumer/init!)
  (x-task-form-consumer/init!)

  ;; the BareBuild runtime element
  (core/init))
