(ns bare-reagent-demo.views.app
  (:require
   ["react" :as react]
   [reagent.core :as r]
   [bare-reagent-demo.state :as state]))

;;; ── Navbar ────────────────────────────────────────────────────────────────

(defn- navbar []
  [:x-navbar {:label "BareDOM Demo" :elevated true}
   [:picture {:slot "brand"}
    [:source {:srcset "/assets/baredom_darkmode.svg"
              :media  "(prefers-color-scheme: dark)"}]
    [:img {:src "/assets/baredom_lightmode.svg" :height "32" :alt "BareDOM"}]]
   [:div {:slot "actions"}
    [:x-button {:variant  "ghost"
                :size     "sm"
                :on-click (fn [_]
                            (swap! state/app update :sidebar-open not))}
     "Menu"]]])

;;; ── Sidebar ───────────────────────────────────────────────────────────────
;;
;; x-sidebar fires a native "toggle" custom event with detail.open.
;; React/Reagent does not forward unknown custom events, so we attach the
;; listener imperatively via create-class lifecycle hooks.

(defn- sidebar []
  (let [el-ref    (react/createRef)
        on-toggle (fn [^js e]
                    (swap! state/app assoc :sidebar-open (.. e -detail -open)))]
    (r/create-class
     {:display-name "sidebar"

      :component-did-mount
      (fn [_]
        (.addEventListener (.-current el-ref) "toggle" on-toggle))

      :component-will-unmount
      (fn [_]
        (.removeEventListener (.-current el-ref) "toggle" on-toggle))

      :reagent-render
      (fn []
        (let [s      @state/app
              active (:active-nav s)
              nav!   (fn [item _]
                       (swap! state/app assoc :sidebar-open false :active-nav item))
              item-class (fn [key]
                           (str "sidebar-item"
                                (when (= active key) " sidebar-item--active")))]
          [:x-sidebar {:ref       el-ref
                       :open      (when (:sidebar-open s) "")
                       :placement "left"
                       :variant   "modal"
                       :label     "Navigation"}
           [:div {:class "sidebar-inner"}
            [:div {:class "sidebar-header"}
             [:picture
              [:source {:srcset "/assets/baredom_darkmode.svg"
                        :media  "(prefers-color-scheme: dark)"}]
              [:img {:src "/assets/baredom_lightmode.svg" :height "28" :alt "BareDOM"}]]]
            [:div {:class "sidebar-content"}
             [:p {:class "sidebar-heading"} "Menu"]
             [:button {:class (item-class "home")          :on-click (partial nav! "home")}          "Home"]
             [:button {:class (item-class "components")    :on-click (partial nav! "components")}    "Components"]
             [:button {:class (item-class "documentation") :on-click (partial nav! "documentation")} "Documentation"]
             [:button {:class (item-class "github")        :on-click (partial nav! "github")}        "GitHub"]]]]))})))

;;; ── Main content ──────────────────────────────────────────────────────────

(defn- main-content []
  [:x-container {:size "md" :padding "lg"}
   [:div {:class "welcome"}
    [:h1 "Welcome to BareDOM"]
    [:p "Native web components. Zero runtime. No framework required. "
     "Set an attribute — the DOM updates. Remove it — the DOM updates back."]
    [:x-button {:variant  "primary"
                :on-click (fn [_]
                            (swap! state/app assoc :modal-open true))}
     "Learn more"]]])

;;; ── Modal ─────────────────────────────────────────────────────────────────
;;
;; x-modal fires "x-modal-dismiss" — an unknown event to React — when the
;; user presses Escape or clicks the backdrop. We wire it imperatively.

(defn- modal []
  (let [el-ref     (react/createRef)
        on-dismiss (fn [_]
                     (swap! state/app assoc :modal-open false))]
    (r/create-class
     {:display-name "modal"

      :component-did-mount
      (fn [_]
        (.addEventListener (.-current el-ref) "x-modal-dismiss" on-dismiss))

      :component-will-unmount
      (fn [_]
        (.removeEventListener (.-current el-ref) "x-modal-dismiss" on-dismiss))

      :reagent-render
      (fn []
        [:x-modal {:ref   el-ref
                   :open  (when (:modal-open @state/app) "")
                   :label "About BareDOM"
                   :size  "md"}
         [:span {:slot "header"} "About BareDOM"]
         [:div {:class "modal-body"}
          [:p {:class "modal-intro"}
           "BareDOM is a library of 56 native UI components built entirely on web standards — "
           "Custom Elements v1, Shadow DOM, and ES modules. No framework. No runtime. No virtual DOM."]
          [:div {:class "modal-formula"}
           [:span {:class "modal-formula-label"} "Core rendering model"]
           [:pre {:class "modal-formula-code"} "DOM = f(attributes, properties)"]]
          [:div {:class "modal-features"}
           [:div {:class "modal-feature"}
            [:span {:class "modal-feature-value"} "56"]
            [:span {:class "modal-feature-label"} "Components"]]
           [:div {:class "modal-feature"}
            [:span {:class "modal-feature-value"} "0"]
            [:span {:class "modal-feature-label"} "Runtime deps"]]
           [:div {:class "modal-feature"}
            [:span {:class "modal-feature-value"} "100%"]
            [:span {:class "modal-feature-label"} "Stateless"]]]
          [:p {:class "modal-note"}
           "Every component compiles to a tree-shakeable ES module via Google Closure's "
           "advanced compilation pass. Import only what you use."]]
         [:div {:slot "footer" :style {:display "flex" :justify-content "flex-end" :gap "8px"}}
          [:x-button {:variant  "ghost"
                      :on-click (fn [_]
                                  (swap! state/app assoc :modal-open false))}
           "Close"]
          [:x-button {:variant  "primary"
                      :on-click (fn [_]
                                  (swap! state/app assoc :modal-open false))}
           "Get started"]]])})))

;;; ── Root ──────────────────────────────────────────────────────────────────

(defn app []
  [:div {:class "app-shell"}
   [navbar]
   [sidebar]
   [:div {:class "app-main"}
    [main-content]]
   [modal]])
