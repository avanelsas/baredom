(ns barebuild.elements.server-resource.model-test
  "The server-resource element's pure decisions: how it resolves its id, and when an intent is
   cross-resource coordination vs a self-drive."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.elements.server-resource.model :as model]))

(deftest resolve-resource-id-nil-when-unnamed
  (testing "an explicit id is kept"
    (is (= "projects" (model/resolve-resource-id "projects"))))
  (testing "absent or blank means unnamed: nil, so the resource owns the URL's root namespace"
    (is (nil? (model/resolve-resource-id nil)))
    (is (nil? (model/resolve-resource-id "")))
    (is (nil? (model/resolve-resource-id "   ")))))

(deftest targets-sibling?-with-an-unnamed-resource
  (testing "an unnamed resource driving itself is not a sibling hop"
    (is (false? (model/targets-sibling? nil nil))))
  (testing "an unnamed resource naming a sibling still coordinates"
    (is (true? (model/targets-sibling? nil "tasks")))))

(deftest targets-sibling?-only-when-naming-another-resource
  (testing "no target -> drive self"
    (is (false? (model/targets-sibling? "tasks" nil))))
  (testing "target equal to own id -> drive self, not a sibling hop"
    (is (false? (model/targets-sibling? "tasks" "tasks"))))
  (testing "a different named target -> cross-resource coordination"
    (is (true? (model/targets-sibling? "projects" "tasks")))))
