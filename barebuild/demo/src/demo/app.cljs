(ns demo.app
  (:require
   [baredom.components.x-alert.x-alert :as x-alert]
   [baredom.components.x-button.x-button :as x-button]
   [baredom.components.x-date-picker.x-date-picker :as x-date-picker]
   [baredom.components.x-drag-panel.x-drag-panel :as x-drag-panel]
   [baredom.components.x-drop-zone.x-drop-zone :as x-drop-zone]
   [baredom.components.x-form.x-form :as x-form]
   [baredom.components.x-form-field.x-form-field :as x-form-field]
   [baredom.components.x-grid.x-grid :as x-grid]
   [baredom.components.x-modal.x-modal :as x-modal]
   [baredom.components.x-pagination.x-pagination :as x-pagination]
   [baredom.components.x-progress.x-progress :as x-progress]
   [baredom.components.x-search-field.x-search-field :as x-search-field]
   [baredom.components.x-select.x-select :as x-select]
   [baredom.components.x-spacer.x-spacer :as x-spacer]
   [baredom.components.x-spinner.x-spinner :as x-spinner]
   [baredom.components.x-stat.x-stat :as x-stat]
   [baredom.components.x-table.x-table :as x-table]
   [baredom.components.x-table-row.x-table-row :as x-table-row]
   [baredom.components.x-table-cell.x-table-cell :as x-table-cell]
   [baredom.components.x-typography.x-typography :as x-typography]
   [demo.x-auth-consumer.x-auth-consumer :as x-auth-consumer]
   [demo.x-board-consumer.x-board-consumer :as x-board-consumer]
   [demo.x-project-form-consumer.x-project-form-consumer :as x-project-form-consumer]
   [demo.x-project-selector-consumer.x-project-selector-consumer :as x-project-selector-consumer]
   [demo.x-progress-consumer.x-progress-consumer :as x-progress-consumer]
   [demo.x-search-field-consumer.x-search-field-consumer :as x-search-field-consumer]
   [demo.x-spinner-consumer.x-spinner-consumer :as x-spinner-consumer]
   [demo.x-stat-consumer.x-stat-consumer :as x-stat-consumer]
   [demo.x-table-consumer.x-table-consumer :as x-table-consumer]
   [demo.x-task-form-consumer.x-task-form-consumer :as x-task-form-consumer]
   [demo.x-task-quickadd-consumer.x-task-quickadd-consumer :as x-task-quickadd-consumer]
   [barebuild.core :as core]
   [barereplay.init :as barereplay]))

(def ^:private registrations
  "Every element the demo page needs defined: the BareDOM components it drives, then the demo's
   own consumers, which are host-app code."
  [x-alert/init!
   x-button/init!
   x-date-picker/init!
   x-drag-panel/init!
   x-drop-zone/init!
   x-form/init!
   x-form-field/init!
   x-grid/init!
   x-modal/init!
   x-pagination/init!
   x-progress/init!
   x-search-field/init!
   x-select/init!
   x-spacer/init!
   x-spinner/init!
   x-stat/init!
   x-table/init!
   x-table-row/init!
   x-table-cell/init!
   x-typography/init!

   x-auth-consumer/init!
   x-board-consumer/init!
   x-project-form-consumer/init!
   x-project-selector-consumer/init!
   x-progress-consumer/init!
   x-search-field-consumer/init!
   x-spinner-consumer/init!
   x-stat-consumer/init!
   x-table-consumer/init!
   x-task-form-consumer/init!
   x-task-quickadd-consumer/init!])

(defn ^:export init []
  (doseq [register! registrations]
    (register!))
  (barereplay/init!)
  (core/init))
