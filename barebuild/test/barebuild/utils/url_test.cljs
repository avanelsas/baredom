(ns barebuild.utils.url-test
  "The URL projection: a resource's query scoped into the address bar, so several resources can
   share one URL without colliding."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.utils.url :as url]))

(deftest build-scoped-url-reflects-the-new-params
  (testing "the new params win, the URL reflects the mutation, not the stale value (regression)"
    (is (= "/t?tasks.sort=owner"
           (url/build-scoped-url "?tasks.sort=STALE" "/t" {"tasks" {:sort "owner"}}))))
  (testing "only this resource's prefixed params are replaced; others are preserved"
    (is (= "/tasks?other.x=1&tasks.sort=owner&tasks.direction=asc"
           (url/build-scoped-url "?other.x=1&tasks.sort=old" "/tasks"
                                 {"tasks" {:sort "owner" :direction "asc"}}))))
  (testing "clearing all owned params yields just the pathname (no dangling ?)"
    (is (= "/t" (url/build-scoped-url "?tasks.sort=owner" "/t" {"tasks" {}}))))
  (testing "empty starting search + new params"
    (is (= "/t?tasks.page=2" (url/build-scoped-url "" "/t" {"tasks" {:page "2"}})))))

(deftest build-scoped-url-unnamed-writes-bare-keys
  (testing "a blank id owns the root namespace: keys are written and replaced without a prefix"
    (is (= "/t?sort=owner"
           (url/build-scoped-url "?sort=STALE" "/t" {nil {:sort "owner"}})))
    (is (= "/t?sort=owner" (url/build-scoped-url "?sort=STALE" "/t" {"" {:sort "owner"}}))))
  (testing "an unnamed resource touches only bare keys, leaving a named sibling's keys intact"
    (is (= "/t?projects.name=x&sort=owner"
           (url/build-scoped-url "?projects.name=x&sort=old" "/t" {nil {:sort "owner"}})))))

(deftest build-scoped-url-writes-every-resource-it-is-handed
  (testing "a named resource and the unnamed root write side by side"
    (let [u (url/build-scoped-url "" "" {"tasks" {:sort "owner"} nil {:page "2"}})]
      (is (= {:sort "owner"} (url/parse-scoped-query u "tasks")))
      (is (= {:page "2"} (url/parse-scoped-query u nil)))))
  (testing "each replaces only its own stale keys, and a resource not handed in keeps its own"
    (let [u (url/build-scoped-url "?tasks.sort=STALE&projects.name=STALE&page=1" ""
                                  {"tasks" {:sort "owner"} "projects" {:name "x"}})]
      (is (= {:sort "owner"} (url/parse-scoped-query u "tasks")))
      (is (= {:name "x"} (url/parse-scoped-query u "projects")))
      (is (= {:page "1"} (url/parse-scoped-query u nil))))))

(deftest parse-scoped-query-reads-only-what-the-resource-owns
  (testing "a named resource reads its prefixed keys, with the prefix stripped"
    (is (= {:sort "owner" :direction "asc"}
           (url/parse-scoped-query "?tasks.sort=owner&tasks.direction=asc" "tasks"))))
  (testing "a sibling's keys and the bare root keys are not this resource's to read"
    (is (= {:sort "owner"}
           (url/parse-scoped-query "?tasks.sort=owner&projects.sort=name&page=2" "tasks"))))
  (testing "an unnamed resource owns the bare keys and leaves every namespaced one alone"
    (is (= {:sort "owner"} (url/parse-scoped-query "?sort=owner&tasks.sort=start" nil)))
    (is (= {:sort "owner"} (url/parse-scoped-query "?sort=owner&tasks.sort=start" ""))))
  (testing "an empty address bar, and one holding nothing this resource owns, are both empty"
    (is (= {} (url/parse-scoped-query "" "tasks")))
    (is (= {} (url/parse-scoped-query "?projects.sort=name" "tasks"))))
  (testing "the query's normal form: a key carrying no value is not an entry"
    (is (= {} (url/parse-scoped-query "?tasks.sort=" "tasks")))))

(deftest build-and-parse-are-inverses
  (testing "parsing what build-scoped-url wrote gives back the query it was handed"
    (doseq [id ["tasks" "" nil]
            q  [{} {:sort "owner"} {:sort "owner" :direction "asc" :page "2"}]]
      (is (= q (url/parse-scoped-query (url/build-scoped-url "" "" {id q}) id))
          (str "round trip for id " (pr-str id) " and query " (pr-str q))))))

(deftest url-prefix-and-owned-keys
  (testing "a named resource prefixes with `<id>.`, an unnamed one uses no prefix"
    (is (= "tasks." (url/url-prefix "tasks")))
    (is (= "" (url/url-prefix nil)))
    (is (= "" (url/url-prefix ""))))
  (testing "owned keys split bare from namespaced so root and named siblings never collide"
    (let [ks ["sort" "page" "projects.name" "tasks.sort"]]
      (is (= ["projects.name"] (url/owned-url-keys "projects" ks)))
      (is (= ["sort" "page"] (url/owned-url-keys nil ks))))))

