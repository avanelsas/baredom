(ns bare-reagent-demo.state
  (:require [reagent.core :as r]))

(defonce app
  (r/atom {:sidebar-open false
           :modal-open   false
           :active-nav   "home"}))
