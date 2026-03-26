(ns app.exports.all
  (:require
   [app.exports.x-button :as x-button]
   [app.exports.x-card :as x-card]
   [app.exports.x-container :as x-container]
   [app.exports.x-grid :as x-grid]
   [app.exports.x-navbar :as x-navbar]
   [app.exports.x-sidebar :as x-sidebar]
   [app.exports.x-stat :as x-stat]
   [app.exports.x-tab :as x-tab]
   [app.exports.x-tabs :as x-tabs]
   [app.exports.x-alert        :as x-alert]
   [app.exports.x-avatar       :as x-avatar]
   [app.exports.x-avatar-group :as x-avatar-group]
   [app.exports.x-badge        :as x-badge]
   [app.exports.x-breadcrumbs  :as x-breadcrumbs]
   [app.exports.x-cancel-dialogue  :as x-cancel-dialogue]
   [app.exports.x-chart            :as x-chart]
   [app.exports.x-checkbox         :as x-checkbox]
   [app.exports.x-chip             :as x-chip]
   [app.exports.x-collapse         :as x-collapse]
   [app.exports.x-command-palette  :as x-command-palette]
   [app.exports.x-context-menu     :as x-context-menu]
   [app.exports.x-copy             :as x-copy]
   [app.exports.x-date-picker      :as x-date-picker]
   [app.exports.x-divider          :as x-divider]
   [app.exports.x-drawer           :as x-drawer]
   [app.exports.x-dropdown         :as x-dropdown]
   [app.exports.x-fieldset         :as x-fieldset]
   [app.exports.x-file-download    :as x-file-download]
   [app.exports.x-form-field       :as x-form-field]
   [app.exports.x-form             :as x-form]
   [app.exports.x-search-field     :as x-search-field]
   [app.exports.x-currency-field   :as x-currency-field]
   [app.exports.x-menu             :as x-menu]
   [app.exports.x-menu-item        :as x-menu-item]
   [app.exports.x-modal            :as x-modal]
   [app.exports.x-notification-center :as x-notification-center]
   [app.exports.x-pagination          :as x-pagination]
   [app.exports.x-popover             :as x-popover]
   [app.exports.x-progress            :as x-progress]
   [app.exports.x-progress-circle     :as x-progress-circle]
   [app.exports.x-radio               :as x-radio]
   [app.exports.x-select              :as x-select]
   [app.exports.x-skeleton            :as x-skeleton]
   [app.exports.x-slider              :as x-slider]
   [app.exports.x-spacer              :as x-spacer]
   [app.exports.x-spinner             :as x-spinner]
   [app.exports.x-stepper             :as x-stepper]
   [app.exports.x-switch              :as x-switch]
   [app.exports.x-table-cell          :as x-table-cell]
   [app.exports.x-table-row           :as x-table-row]
   [app.exports.x-table               :as x-table]
   [app.exports.x-text-area           :as x-textarea]
   [app.exports.x-timeline-item       :as x-timeline-item]
   [app.exports.x-timeline            :as x-timeline]
   [app.exports.x-toast               :as x-toast]
   [app.exports.x-toaster             :as x-toaster]))

(defn register!
  []
  (x-alert/register!)
  (x-avatar/register!)
  (x-avatar-group/register!)
  (x-badge/register!)
  (x-breadcrumbs/register!)
  (x-button/register!)
  (x-card/register!)
  (x-container/register!)
  (x-grid/register!)
  (x-navbar/register!)
  (x-sidebar/register!)
  (x-stat/register!)
  (x-tab/register!)
  (x-tabs/register!)
  (x-cancel-dialogue/register!)
  (x-chart/register!)
  (x-checkbox/register!)
  (x-chip/register!)
  (x-collapse/register!)
  (x-command-palette/register!)
  (x-context-menu/register!)
  (x-copy/register!)
  (x-date-picker/register!)
  (x-divider/register!)
  (x-drawer/register!)
  (x-dropdown/register!)
  (x-fieldset/register!)
  (x-file-download/register!)
  (x-form-field/register!)
  (x-form/register!)
  (x-search-field/register!)
  (x-currency-field/register!)
  (x-menu/register!)
  (x-menu-item/register!)
  (x-modal/register!)
  (x-notification-center/register!)
  (x-pagination/register!)
  (x-popover/register!)
  (x-progress/register!)
  (x-progress-circle/register!)
  (x-radio/register!)
  (x-select/register!)
  (x-skeleton/register!)
  (x-slider/register!)
  (x-spacer/register!)
  (x-spinner/register!)
  (x-stepper/register!)
  (x-switch/register!)
  (x-table-cell/register!)
  (x-table-row/register!)
  (x-table/register!)
  (x-textarea/register!)
  (x-timeline-item/register!)
  (x-timeline/register!)
  (x-toast/register!)
  (x-toaster/register!))

(defn ^:export init
  []
  (register!))
