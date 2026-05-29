(ns baredom.components.barebuild-router.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.barebuild-router.model :as model]))

;; ── split-segments ─────────────────────────────────────────────────────────────
(deftest split-segments-test
  (testing "root and empties collapse to []"
    (is (= [] (model/split-segments "/")))
    (is (= [] (model/split-segments "")))
    (is (= [] (model/split-segments "//"))))
  (testing "single + multi segment"
    (is (= ["users"] (model/split-segments "/users")))
    (is (= ["users" "42"] (model/split-segments "/users/42"))))
  (testing "trailing slash collapses"
    (is (= ["users"] (model/split-segments "/users/"))))
  (testing "non-string input yields [] (uniform return — boundary guards type)"
    (is (= [] (model/split-segments nil)))
    (is (= [] (model/split-segments 42)))))

;; ── parse-path-pattern ───────────────────────────────────────────────────────
(deftest parse-path-pattern-test
  (testing "root pattern has no segments"
    (is (= [] (model/parse-path-pattern "/"))))
  (testing "literal segment"
    (is (= [{:kind :literal :v "users"}]
           (model/parse-path-pattern "/users"))))
  (testing "single param"
    (is (= [{:kind :literal :v "users"} {:kind :param :name "id"}]
           (model/parse-path-pattern "/users/:id"))))
  (testing "multiple params"
    (is (= [{:kind :literal :v "posts"} {:kind :param :name "postId"}
            {:kind :literal :v "comments"} {:kind :param :name "commentId"}]
           (model/parse-path-pattern "/posts/:postId/comments/:commentId")))))

;; ── match-path ─────────────────────────────────────────────────────────────────
(deftest match-path-literal-test
  (testing "root matches root"
    (is (= {:params {}} (model/match-path (model/parse-path-pattern "/") "/"))))
  (testing "literal exact match"
    (is (= {:params {}} (model/match-path (model/parse-path-pattern "/users") "/users"))))
  (testing "literal mismatch is nil"
    (is (nil? (model/match-path (model/parse-path-pattern "/users") "/posts")))))

(deftest match-path-param-test
  (testing "single param captured"
    (is (= {:params {"id" "42"}}
           (model/match-path (model/parse-path-pattern "/users/:id") "/users/42"))))
  (testing "multiple params captured"
    (is (= {:params {"postId" "7" "commentId" "3"}}
           (model/match-path (model/parse-path-pattern "/posts/:postId/comments/:commentId")
                             "/posts/7/comments/3")))))

(deftest match-path-arity-mismatch-test
  (testing "too few path segments"
    (is (nil? (model/match-path (model/parse-path-pattern "/users/:id") "/users"))))
  (testing "too many path segments"
    (is (nil? (model/match-path (model/parse-path-pattern "/users") "/users/42"))))
  (testing "root pattern does not match a deeper path"
    (is (nil? (model/match-path (model/parse-path-pattern "/") "/users")))))

;; ── strip-base ─────────────────────────────────────────────────────────────────
(deftest strip-base-test
  (testing "empty base returns path unchanged"
    (is (= "/users/42" (model/strip-base "" "/users/42")))
    (is (= "/users/42" (model/strip-base nil "/users/42"))))
  (testing "base prefix stripped on a segment boundary"
    (is (= "/users/42" (model/strip-base "/app" "/app/users/42"))))
  (testing "path equal to base becomes root"
    (is (= "/" (model/strip-base "/app" "/app"))))
  (testing "trailing slash on base is tolerated"
    (is (= "/users" (model/strip-base "/app/" "/app/users"))))
  (testing "no false strip on a non-boundary prefix match"
    (is (= "/application" (model/strip-base "/app" "/application"))))
  (testing "path that does not start with base is unchanged"
    (is (= "/other" (model/strip-base "/app" "/other")))))

;; ── should-intercept? ──────────────────────────────────────────────────────────
(def ^:private happy
  {:anchor-present?         true
   :nearest-router-is-this? true
   :primary-button?         true
   :modifier?               false
   :default-prevented?      false})

(deftest should-intercept-happy-test
  (testing "all conditions met → intercept"
    (is (true? (model/should-intercept? happy)))))

(deftest should-intercept-no-anchor-test
  (testing "no data-barebuild-route anchor in path → no intercept"
    (is (false? (model/should-intercept? (assoc happy :anchor-present? false))))))

(deftest should-intercept-sibling-router-test
  (testing "anchor in a sibling router's subtree (this router not nearest) → no intercept"
    (is (false? (model/should-intercept? (assoc happy :nearest-router-is-this? false))))))

(deftest should-intercept-nested-inner-test
  (testing "nested: another router lies between anchor and this one → only the nearest intercepts"
    ;; For the OUTER router of a nested pair, nearest-router-is-this? is false.
    (is (false? (model/should-intercept? (assoc happy :nearest-router-is-this? false))))))

(deftest should-intercept-modifier-test
  (testing "modifier key held → no intercept (let the browser open a tab)"
    (is (false? (model/should-intercept? (assoc happy :modifier? true))))))

(deftest should-intercept-non-primary-button-test
  (testing "right/middle click → no intercept"
    (is (false? (model/should-intercept? (assoc happy :primary-button? false))))))

(deftest should-intercept-default-prevented-test
  (testing "preventDefault already called → no intercept"
    (is (false? (model/should-intercept? (assoc happy :default-prevented? true))))))
