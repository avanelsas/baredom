(ns baredom.components.x-comments.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-comments.model :as model]))

(def ^:private thread
  [{:id "a" :name "Ann"} {:id "b" :name "Bo"} {:id "c" :name "Cy"}])

;; ── Placement ─────────────────────────────────────────────────────────────
(deftest target-index-finds-the-comment-reply-to-names-test
  (is (= 1 (model/target-index thread "b")))
  (is (nil? (model/target-index thread "z")))
  (is (nil? (model/target-index thread "")))
  (is (nil? (model/target-index thread nil))))

(deftest with-no-target-every-comment-is-before-the-form-test
  (is (= ["before" "before" "before"] (model/slots thread nil))))

(deftest the-form-stands-right-after-its-target-test
  (is (= ["before" "after" "after"] (model/slots thread 0)))
  (is (= ["before" "before" "after"] (model/slots thread 1))))

;; ── Normalize ─────────────────────────────────────────────────────────────
(deftest normalize-of-no-attributes-is-an-open-form-at-the-bottom-test
  (let [m (model/normalize {})]
    (is (false? (:closed? m)))
    (is (= "" (:commenter-name m)))
    (is (= 80 (:name-max m)))
    (is (= 4000 (:text-max m)))
    (is (= [] (:slots m)))
    (is (nil? (:reply-to m)))
    (is (= "Leave a comment" (:form-title m)))
    (is (= "Comments" (get-in m [:labels :heading])))))

(deftest a-reply-titles-the-form-with-the-name-it-answers-test
  (let [m (model/normalize {:comments thread :reply-to-raw "b"})]
    (is (= "b" (:reply-to m)))
    (is (= "Reply to Bo" (:form-title m)))
    (is (= ["before" "before" "after"] (:slots m)))))

(deftest a-reply-to-no-comment-places-the-form-at-the-bottom-test
  (let [m (model/normalize {:comments thread :reply-to-raw "z"})]
    (is (nil? (:reply-to m)))
    (is (= "Leave a comment" (:form-title m)))
    (is (= ["before" "before" "before"] (:slots m)))))

(deftest limits-read-positive-integers-and-default-the-rest-test
  (let [m (model/normalize {:name-max-raw "40" :text-max-raw "0"})]
    (is (= 40 (:name-max m)))
    (is (= 4000 (:text-max m)))))

(deftest labels-replace-their-defaults-test
  (let [m (model/normalize {:comments thread :reply-to-raw "a"
                            :labels-raw {:reply-form "Antwoord op" :send "Verstuur"}})]
    (is (= "Antwoord op Ann" (:form-title m)))
    (is (= "Verstuur" (get-in m [:labels :send])))))

(deftest every-label-has-an-attribute-and-a-default-test
  (is (= (set (keys model/default-labels)) (set (keys model/label-attributes)))))

;; ── Event details ─────────────────────────────────────────────────────────
(deftest reply-request-detail-gives-text-for-an-absent-target-test
  (is (= {:replyTo "b" :previousReplyTo ""} (model/reply-request-detail "b" nil)))
  (is (= {:replyTo "" :previousReplyTo "b"} (model/reply-request-detail "" "b"))))

(deftest submit-detail-holds-the-entry-and-its-target-test
  (is (= {:name "Ann" :text "Hi" :replyTo "" :trap ""}
         (model/submit-detail {:name "Ann" :text "Hi" :reply-to nil :trap ""}))))
