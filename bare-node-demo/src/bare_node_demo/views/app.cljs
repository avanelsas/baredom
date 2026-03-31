(ns bare-node-demo.views.app
  (:require [bare-node-demo.state :as state]))

(defn- navbar [_s]
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

(defn- sidebar [s]
  (let [active (:active-nav s)
        nav!   (fn [item _]
                 (swap! state/app assoc :sidebar-open false :active-nav item))
        item-class (fn [key]
                     (str "sidebar-item" (when (= active key) " sidebar-item--active")))]
    [:x-sidebar {:open      (:sidebar-open s)
                 :placement "left"
                 :variant   "modal"
                 :label     "Navigation"
                 :on-toggle (fn [e]
                              (swap! state/app assoc
                                     :sidebar-open (.. e -detail -open)))}
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
       [:button {:class (item-class "github")        :on-click (partial nav! "github")}        "GitHub"]]]]))

(defn- main-content [s]
  [:x-container {:size "md" :padding "lg"}
   [:div {:class "welcome"}
    [:h1 "Welcome to BareDOM"]
    [:p "Native web components. Zero runtime. No framework required. "
     "Set an attribute — the DOM updates. Remove it — the DOM updates back."]
    [:x-button {:variant  "primary"
                :on-click (fn [_]
                            (swap! state/app assoc :modal-open true))}
     "Learn more"]]])

(defn- modal [s]
  [:x-modal {:open      (:modal-open s)
             :label     "About BareDOM"
             :size      "md"
             :on-x-modal-dismiss (fn [_]
                                   (swap! state/app assoc :modal-open false))}
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
   [:div {:slot "footer" :style "display:flex;justify-content:flex-end;gap:8px"}
    [:x-button {:variant  "ghost"
                :on-click (fn [_]
                            (swap! state/app assoc :modal-open false))}
     "Close"]
    [:x-button {:variant  "primary"
                :on-click (fn [_]
                            (swap! state/app assoc :modal-open false))}
     "Get started"]]])

(defn app [s]
  [:div {:class "app-shell"}
   (navbar s)
   (sidebar s)
   [:div {:class "app-main"}
    (main-content s)]
   (modal s)])
