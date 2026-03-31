(ns bare-node-demo.state)

(defonce app
  (atom {:sidebar-open false
         :modal-open   false
         :active-nav   "home"}))
