(ns baredom.core
  (:require
   [baredom.exports.x-button :as x-button]
   [baredom.exports.x-card :as x-card]
   [baredom.exports.x-carousel :as x-carousel]
   [baredom.exports.x-container :as x-container]
   [baredom.exports.x-grid :as x-grid]
   [baredom.exports.x-navbar :as x-navbar]
   [baredom.exports.x-sidebar :as x-sidebar]
   [baredom.exports.x-stat :as x-stat]
   [baredom.exports.x-tab :as x-tab]
   [baredom.exports.x-tabs :as x-tabs]
   [baredom.exports.x-alert        :as x-alert]
   [baredom.exports.x-avatar       :as x-avatar]
   [baredom.exports.x-avatar-group :as x-avatar-group]
   [baredom.exports.x-badge        :as x-badge]
   [baredom.exports.x-breadcrumbs  :as x-breadcrumbs]
   [baredom.exports.x-cancel-dialogue  :as x-cancel-dialogue]
   [baredom.exports.x-chart            :as x-chart]
   [baredom.exports.x-checkbox         :as x-checkbox]
   [baredom.exports.x-chip             :as x-chip]
   [baredom.exports.x-collapse         :as x-collapse]
   [baredom.exports.x-color-picker     :as x-color-picker]
   [baredom.exports.x-combobox         :as x-combobox]
   [baredom.exports.x-command-palette  :as x-command-palette]
   [baredom.exports.x-context-menu     :as x-context-menu]
   [baredom.exports.x-copy             :as x-copy]
   [baredom.exports.x-date-picker      :as x-date-picker]
   [baredom.exports.x-divider          :as x-divider]
   [baredom.exports.x-drawer           :as x-drawer]
   [baredom.exports.x-dropdown         :as x-dropdown]
   [baredom.exports.x-fieldset         :as x-fieldset]
   [baredom.exports.x-file-download    :as x-file-download]
   [baredom.exports.x-file-upload     :as x-file-upload]
   [baredom.exports.x-form-field       :as x-form-field]
   [baredom.exports.x-form             :as x-form]
   [baredom.exports.x-search-field     :as x-search-field]
   [baredom.exports.x-currency-field   :as x-currency-field]
   [baredom.exports.x-menu             :as x-menu]
   [baredom.exports.x-menu-item        :as x-menu-item]
   [baredom.exports.x-modal            :as x-modal]
   [baredom.exports.x-notification-center :as x-notification-center]
   [baredom.exports.x-pagination          :as x-pagination]
   [baredom.exports.x-popover             :as x-popover]
   [baredom.exports.x-progress            :as x-progress]
   [baredom.exports.x-progress-circle     :as x-progress-circle]
   [baredom.exports.x-radio               :as x-radio]
   [baredom.exports.x-select              :as x-select]
   [baredom.exports.x-skeleton            :as x-skeleton]
   [baredom.exports.x-skeleton-group     :as x-skeleton-group]
   [baredom.exports.x-slider              :as x-slider]
   [baredom.exports.x-spacer              :as x-spacer]
   [baredom.exports.x-spinner             :as x-spinner]
   [baredom.exports.x-stepper             :as x-stepper]
   [baredom.exports.x-switch              :as x-switch]
   [baredom.exports.x-table-cell          :as x-table-cell]
   [baredom.exports.x-table-row           :as x-table-row]
   [baredom.exports.x-table               :as x-table]
   [baredom.exports.x-text-area           :as x-textarea]
   [baredom.exports.x-timeline-item       :as x-timeline-item]
   [baredom.exports.x-timeline            :as x-timeline]
   [baredom.exports.x-toast               :as x-toast]
   [baredom.exports.x-toaster             :as x-toaster]
   [baredom.exports.x-tooltip             :as x-tooltip]
   [baredom.exports.x-welcome-tour       :as x-welcome-tour]
   [baredom.exports.x-organic-shape       :as x-organic-shape]
   [baredom.exports.x-typography          :as x-typography]
   [baredom.exports.x-kinetic-typography :as x-kinetic-typography]
   [baredom.exports.x-kinetic-font      :as x-kinetic-font]
   [baredom.exports.x-organic-divider   :as x-organic-divider]
   [baredom.exports.x-organic-progress   :as x-organic-progress]
   [baredom.exports.x-ripple-effect    :as x-ripple-effect]
   [baredom.exports.x-gaussian-blur   :as x-gaussian-blur]
   [baredom.exports.x-scroll          :as x-scroll]
   [baredom.exports.x-scroll-parallax :as x-scroll-parallax]
   [baredom.exports.x-scroll-story    :as x-scroll-story]
   [baredom.exports.x-scroll-timeline :as x-scroll-timeline]
   [baredom.exports.x-scroll-stack    :as x-scroll-stack]
   [baredom.exports.x-splash          :as x-splash]
   [baredom.exports.x-metaball-cursor :as x-metaball-cursor]
   [baredom.exports.x-neural-glow    :as x-neural-glow]
   [baredom.exports.x-bento-grid     :as x-bento-grid]
   [baredom.exports.x-bento-item     :as x-bento-item]
   [baredom.exports.x-soft-body      :as x-soft-body]
   [baredom.exports.x-liquid-glass   :as x-liquid-glass]
   [baredom.exports.x-liquid-dock    :as x-liquid-dock]
   [baredom.exports.x-liquid-fill    :as x-liquid-fill]
   [baredom.exports.x-morph-stack    :as x-morph-stack]
   [baredom.exports.x-particle-button :as x-particle-button]
   [baredom.exports.x-image          :as x-image]
   [baredom.exports.x-icon           :as x-icon]
   [baredom.exports.x-theme          :as x-theme]
   [baredom.dev.hot-reload             :as hot-reload]
   [baredom.dev.x-debug.x-debug        :as x-debug]))

(defn ^:dev/after-load start!
  []
  (x-alert/register!)
  (x-avatar/register!)
  (x-avatar-group/register!)
  (x-badge/register!)
  (x-breadcrumbs/register!)
  (x-button/register!)
  (x-card/register!)
  (x-carousel/register!)
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
  (x-color-picker/register!)
  (x-combobox/register!)
  (x-command-palette/register!)
  (x-context-menu/register!)
  (x-copy/register!)
  (x-date-picker/register!)
  (x-divider/register!)
  (x-drawer/register!)
  (x-dropdown/register!)
  (x-fieldset/register!)
  (x-file-download/register!)
  (x-file-upload/register!)
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
  (x-skeleton-group/register!)
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
  (x-toaster/register!)
  (x-tooltip/register!)
  (x-welcome-tour/register!)
  (x-organic-shape/register!)
  (x-typography/register!)
  (x-kinetic-typography/register!)
  (x-kinetic-font/register!)
  (x-organic-divider/register!)
  (x-organic-progress/register!)
  (x-ripple-effect/register!)
  (x-gaussian-blur/register!)
  (x-scroll/register!)
  (x-scroll-parallax/register!)
  (x-scroll-story/register!)
  (x-scroll-timeline/register!)
  (x-scroll-stack/register!)
  (x-splash/register!)
  (x-metaball-cursor/register!)
  (x-neural-glow/register!)
  (x-bento-grid/register!)
  (x-bento-item/register!)
  (x-soft-body/register!)
  (x-liquid-glass/register!)
  (x-liquid-dock/register!)
  (x-liquid-fill/register!)
  (x-morph-stack/register!)
  (x-particle-button/register!)
  (x-image/register!)
  (x-icon/register!)
  (x-theme/register!)
  (x-debug/register!)
  (hot-reload/refresh-styles!))

(defn init!
  []
  (start!))
