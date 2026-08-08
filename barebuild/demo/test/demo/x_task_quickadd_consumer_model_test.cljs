(ns demo.x-task-quickadd-consumer-model-test
  "The quick-add's pure layer: two form fields into a full create record."
  (:require [cljs.test :refer-macros [deftest is]]
            [demo.x-task-quickadd-consumer.model :as model]))

(deftest new-task-record-completes-the-write
  (is (= {"title"     "Ship it"
          "owner"     "Wendy"
          "status"    "todo"
          "projectId" "p-1"
          "start"     "2026-07-30"}
         (model/new-task-record {"title" "Ship it" "owner" "Wendy"} "p-1" "2026-07-30"))
      "the typed title and name join the server-required fields, scoped to the viewed project"))
