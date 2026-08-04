(ns barebuild.utils.url-test
  "The URL projection: a resource's query scoped into the address bar, so several resources can
   share one URL without colliding."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.utils.url :as url]))

(deftest build-scoped-url-reflects-the-new-params
  (testing "the new params win — the URL reflects the mutation, not the stale value (regression)"
    (is (= "/t?tasks.sort=owner"
           (url/build-scoped-url "?tasks.sort=STALE" "/t" "tasks" {:sort "owner"}))))
  (testing "only this resource's prefixed params are replaced; others are preserved"
    (is (= "/tasks?other.x=1&tasks.sort=owner&tasks.direction=asc"
           (url/build-scoped-url "?other.x=1&tasks.sort=old" "/tasks" "tasks"
                                   {:sort "owner" :direction "asc"}))))
  (testing "clearing all owned params yields just the pathname (no dangling ?)"
    (is (= "/t" (url/build-scoped-url "?tasks.sort=owner" "/t" "tasks" {}))))
  (testing "empty starting search + new params"
    (is (= "/t?tasks.page=2" (url/build-scoped-url "" "/t" "tasks" {:page "2"})))))

(deftest build-scoped-url-unnamed-writes-bare-keys
  (testing "a blank id owns the root namespace: keys are written and replaced without a prefix"
    (is (= "/t?sort=owner"
           (url/build-scoped-url "?sort=STALE" "/t" nil {:sort "owner"})))
    (is (= "/t?sort=owner" (url/build-scoped-url "?sort=STALE" "/t" "" {:sort "owner"}))))
  (testing "an unnamed resource touches only bare keys, leaving a named sibling's keys intact"
    (is (= "/t?projects.name=x&sort=owner"
           (url/build-scoped-url "?projects.name=x&sort=old" "/t" nil {:sort "owner"})))))

(deftest url-prefix-and-owned-keys
  (testing "a named resource prefixes with `<id>.`, an unnamed one uses no prefix"
    (is (= "tasks." (url/url-prefix "tasks")))
    (is (= "" (url/url-prefix nil)))
    (is (= "" (url/url-prefix ""))))
  (testing "owned keys split bare from namespaced so root and named siblings never collide"
    (let [ks ["sort" "page" "projects.name" "tasks.sort"]]
      (is (= ["projects.name"] (url/owned-url-keys "projects" ks)))
      (is (= ["sort" "page"] (url/owned-url-keys nil ks))))))

