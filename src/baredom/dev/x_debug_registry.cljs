(ns baredom.dev.x-debug-registry
  "Dev-only registry mapping tag names to public-api metadata for all
   BareDOM components. Used by x-debug to introspect component state."
  (:require
   [clojure.string :as str]
   [baredom.exports.x-alert            :as x-alert]
   [baredom.exports.x-avatar           :as x-avatar]
   [baredom.exports.x-avatar-group     :as x-avatar-group]
   [baredom.exports.x-badge            :as x-badge]
   [baredom.exports.x-bento-grid       :as x-bento-grid]
   [baredom.exports.x-bento-item       :as x-bento-item]
   [baredom.exports.x-breadcrumbs      :as x-breadcrumbs]
   [baredom.exports.x-button           :as x-button]
   [baredom.exports.x-cancel-dialogue  :as x-cancel-dialogue]
   [baredom.exports.x-card             :as x-card]
   [baredom.exports.x-carousel         :as x-carousel]
   [baredom.exports.x-chart            :as x-chart]
   [baredom.exports.x-checkbox         :as x-checkbox]
   [baredom.exports.x-chip             :as x-chip]
   [baredom.exports.x-collapse         :as x-collapse]
   [baredom.exports.x-color-picker     :as x-color-picker]
   [baredom.exports.x-combobox         :as x-combobox]
   [baredom.exports.x-command-palette  :as x-command-palette]
   [baredom.exports.x-container        :as x-container]
   [baredom.exports.x-context-menu     :as x-context-menu]
   [baredom.exports.x-copy             :as x-copy]
   [baredom.exports.x-currency-field   :as x-currency-field]
   [baredom.exports.x-date-picker      :as x-date-picker]
   [baredom.exports.x-divider          :as x-divider]
   [baredom.exports.x-drawer           :as x-drawer]
   [baredom.exports.x-dropdown         :as x-dropdown]
   [baredom.exports.x-fieldset         :as x-fieldset]
   [baredom.exports.x-file-download    :as x-file-download]
   [baredom.exports.x-file-upload      :as x-file-upload]
   [baredom.exports.x-form             :as x-form]
   [baredom.exports.x-form-field       :as x-form-field]
   [baredom.exports.x-gaussian-blur    :as x-gaussian-blur]
   [baredom.exports.x-grid             :as x-grid]
   [baredom.exports.x-icon             :as x-icon]
   [baredom.exports.x-image            :as x-image]
   [baredom.exports.x-kinetic-font     :as x-kinetic-font]
   [baredom.exports.x-kinetic-typography :as x-kinetic-typography]
   [baredom.exports.x-liquid-dock      :as x-liquid-dock]
   [baredom.exports.x-liquid-fill      :as x-liquid-fill]
   [baredom.exports.x-liquid-glass     :as x-liquid-glass]
   [baredom.exports.x-menu             :as x-menu]
   [baredom.exports.x-menu-item        :as x-menu-item]
   [baredom.exports.x-metaball-cursor  :as x-metaball-cursor]
   [baredom.exports.x-modal            :as x-modal]
   [baredom.exports.x-morph-stack      :as x-morph-stack]
   [baredom.exports.x-navbar           :as x-navbar]
   [baredom.exports.x-neural-glow      :as x-neural-glow]
   [baredom.exports.x-notification-center :as x-notification-center]
   [baredom.exports.x-organic-divider  :as x-organic-divider]
   [baredom.exports.x-organic-progress :as x-organic-progress]
   [baredom.exports.x-organic-shape    :as x-organic-shape]
   [baredom.exports.x-pagination       :as x-pagination]
   [baredom.exports.x-particle-button  :as x-particle-button]
   [baredom.exports.x-popover          :as x-popover]
   [baredom.exports.x-progress         :as x-progress]
   [baredom.exports.x-progress-circle  :as x-progress-circle]
   [baredom.exports.x-radio            :as x-radio]
   [baredom.exports.x-ripple-effect    :as x-ripple-effect]
   [baredom.exports.x-scroll           :as x-scroll]
   [baredom.exports.x-scroll-parallax  :as x-scroll-parallax]
   [baredom.exports.x-scroll-stack     :as x-scroll-stack]
   [baredom.exports.x-scroll-story     :as x-scroll-story]
   [baredom.exports.x-scroll-timeline  :as x-scroll-timeline]
   [baredom.exports.x-search-field     :as x-search-field]
   [baredom.exports.x-select           :as x-select]
   [baredom.exports.x-sidebar          :as x-sidebar]
   [baredom.exports.x-skeleton         :as x-skeleton]
   [baredom.exports.x-skeleton-group   :as x-skeleton-group]
   [baredom.exports.x-slider           :as x-slider]
   [baredom.exports.x-soft-body        :as x-soft-body]
   [baredom.exports.x-spacer           :as x-spacer]
   [baredom.exports.x-spinner          :as x-spinner]
   [baredom.exports.x-splash           :as x-splash]
   [baredom.exports.x-stat             :as x-stat]
   [baredom.exports.x-stepper          :as x-stepper]
   [baredom.exports.x-switch           :as x-switch]
   [baredom.exports.x-tab              :as x-tab]
   [baredom.exports.x-table            :as x-table]
   [baredom.exports.x-table-cell       :as x-table-cell]
   [baredom.exports.x-table-row        :as x-table-row]
   [baredom.exports.x-tabs             :as x-tabs]
   [baredom.exports.x-text-area        :as x-textarea]
   [baredom.exports.x-theme            :as x-theme]
   [baredom.exports.x-timeline         :as x-timeline]
   [baredom.exports.x-timeline-item    :as x-timeline-item]
   [baredom.exports.x-toast            :as x-toast]
   [baredom.exports.x-toaster          :as x-toaster]
   [baredom.exports.x-tooltip          :as x-tooltip]
   [baredom.exports.x-typography       :as x-typography]
   [baredom.exports.x-welcome-tour     :as x-welcome-tour]))

(defn- build-registry
  "Builds a map of tag-name string -> public-api map from a sequence of
   public-api defs."
  [apis]
  (reduce (fn [acc api]
            (assoc acc (:tag-name api) api))
          {}
          apis))

(def registry
  "Map of tag-name string to public-api metadata for every BareDOM component."
  (build-registry
   [x-alert/public-api
    x-avatar/public-api
    x-avatar-group/public-api
    x-badge/public-api
    x-bento-grid/public-api
    x-bento-item/public-api
    x-breadcrumbs/public-api
    x-button/public-api
    x-cancel-dialogue/public-api
    x-card/public-api
    x-carousel/public-api
    x-chart/public-api
    x-checkbox/public-api
    x-chip/public-api
    x-collapse/public-api
    x-color-picker/public-api
    x-combobox/public-api
    x-command-palette/public-api
    x-container/public-api
    x-context-menu/public-api
    x-copy/public-api
    x-currency-field/public-api
    x-date-picker/public-api
    x-divider/public-api
    x-drawer/public-api
    x-dropdown/public-api
    x-fieldset/public-api
    x-file-download/public-api
    x-file-upload/public-api
    x-form/public-api
    x-form-field/public-api
    x-gaussian-blur/public-api
    x-grid/public-api
    x-icon/public-api
    x-image/public-api
    x-kinetic-font/public-api
    x-kinetic-typography/public-api
    x-liquid-dock/public-api
    x-liquid-fill/public-api
    x-liquid-glass/public-api
    x-menu/public-api
    x-menu-item/public-api
    x-metaball-cursor/public-api
    x-modal/public-api
    x-morph-stack/public-api
    x-navbar/public-api
    x-neural-glow/public-api
    x-notification-center/public-api
    x-organic-divider/public-api
    x-organic-progress/public-api
    x-organic-shape/public-api
    x-pagination/public-api
    x-particle-button/public-api
    x-popover/public-api
    x-progress/public-api
    x-progress-circle/public-api
    x-radio/public-api
    x-ripple-effect/public-api
    x-scroll/public-api
    x-scroll-parallax/public-api
    x-scroll-stack/public-api
    x-scroll-story/public-api
    x-scroll-timeline/public-api
    x-search-field/public-api
    x-select/public-api
    x-sidebar/public-api
    x-skeleton/public-api
    x-skeleton-group/public-api
    x-slider/public-api
    x-soft-body/public-api
    x-spacer/public-api
    x-spinner/public-api
    x-splash/public-api
    x-stat/public-api
    x-stepper/public-api
    x-switch/public-api
    x-tab/public-api
    x-table/public-api
    x-table-cell/public-api
    x-table-row/public-api
    x-tabs/public-api
    x-textarea/public-api
    x-theme/public-api
    x-timeline/public-api
    x-timeline-item/public-api
    x-toast/public-api
    x-toaster/public-api
    x-tooltip/public-api
    x-typography/public-api
    x-welcome-tour/public-api]))

(def selector
  "Pre-built CSS selector string matching all known BareDOM tag names."
  (->> (keys registry)
       (str/join ",")))
