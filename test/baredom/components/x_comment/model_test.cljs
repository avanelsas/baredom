(ns baredom.components.x-comment.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-comment.model :as model]))

;; ── Defaults ──────────────────────────────────────────────────────────────
(deftest normalize-of-no-attributes-is-a-published-top-level-comment-test
  (let [m (model/normalize {})]
    (is (= "published" (:status m)))
    (is (= "" (:comment-id m)))
    (is (= "" (:name m)))
    (is (true? (:identity? m)))
    (is (nil? (:datetime m)))
    (is (= 0 (:level m)))
    (is (nil? (:context m)))
    (is (true? (:body? m)))
    (is (false? (:muted? m)))
    (is (false? (:author? m)))
    (is (nil? (:badge m)))
    (is (nil? (:notice m)))
    (is (true? (:reply? m)))
    (is (= "Reply" (:reply-text m)))))

;; ── Status ────────────────────────────────────────────────────────────────
(deftest parse-status-keeps-a-known-status-and-defaults-the-rest-test
  (is (= "pending" (model/parse-status "pending")))
  (is (= "published" (model/parse-status "draft")))
  (is (= "published" (model/parse-status nil))))

(deftest a-pending-comment-is-muted-badged-and-not-replyable-test
  (let [m (model/normalize {:status-raw "pending" :name-raw "Ann"})]
    (is (true? (:identity? m)))
    (is (true? (:muted? m)))
    (is (= "Awaiting review" (:badge m)))
    (is (false? (:reply? m)))))

(deftest a-removed-comment-shows-only-its-notice-test
  (let [m (model/normalize {:status-raw "removed" :datetime-raw "2026-10-01"})]
    (is (false? (:identity? m)))
    (is (nil? (:datetime m)))
    (is (false? (:body? m)))
    (is (= "This comment was removed." (:notice m)))
    (is (false? (:reply? m)))))

(deftest an-expired-comment-shows-its-date-and-notice-test
  (let [m (model/normalize {:status-raw "expired" :datetime-raw "2026-10-01"})]
    (is (false? (:identity? m)))
    (is (= "2026-10-01" (:datetime m)))
    (is (false? (:body? m)))
    (is (= "Your comment was not published." (:notice m)))
    (is (false? (:reply? m)))))

;; ── Author ────────────────────────────────────────────────────────────────
(deftest an-author-reply-is-badged-and-highlighted-when-published-test
  (let [m (model/normalize {:author? true})]
    (is (true? (:author? m)))
    (is (= "Author" (:badge m)))))

(deftest an-author-mark-on-a-removed-comment-has-no-effect-test
  (let [m (model/normalize {:author? true :status-raw "removed"})]
    (is (false? (:author? m)))
    (is (nil? (:badge m)))))

;; ── Depth ─────────────────────────────────────────────────────────────────
(deftest parse-depth-reads-a-depth-and-defaults-to-zero-test
  (is (= 3 (model/parse-depth "3")))
  (is (= 0 (model/parse-depth "0")))
  (is (= 0 (model/parse-depth nil))))

(deftest the-level-is-the-depth-capped-at-max-depth-test
  (is (= 1 (:level (model/normalize {:depth-raw "1"}))))
  (is (= 2 (:level (model/normalize {:depth-raw "5"}))))
  (is (= 3 (:level (model/normalize {:depth-raw "5" :max-depth-raw "3"})))))

(deftest an-invalid-max-depth-gives-the-default-test
  (is (= 2 (model/parse-max-depth "0")))
  (is (= 2 (model/parse-max-depth "abc"))))

;; ── Reply context ─────────────────────────────────────────────────────────
(deftest a-reply-within-the-cap-has-a-quiet-context-test
  (is (= {:text "Reply to Ann" :quiet? true}
         (:context (model/normalize {:depth-raw "1" :reply-to-name-raw "Ann"})))))

(deftest a-reply-past-the-cap-shows-its-context-test
  (is (= {:text "Reply to Ann" :quiet? false}
         (:context (model/normalize {:depth-raw "3" :reply-to-name-raw "Ann"})))))

(deftest a-comment-with-no-reply-to-name-has-no-context-test
  (is (nil? (:context (model/normalize {:depth-raw "3" :reply-to-name-raw ""})))))

;; ── Date ──────────────────────────────────────────────────────────────────
(deftest parse-datetime-keeps-a-readable-date-only-test
  (is (= "2026-10-28" (model/parse-datetime "2026-10-28")))
  (is (= "2026-10-28T09:30:00Z" (model/parse-datetime "2026-10-28T09:30:00Z")))
  (is (nil? (model/parse-datetime "yesterday")))
  (is (nil? (model/parse-datetime "")))
  (is (nil? (model/parse-datetime nil))))

;; ── Closed ────────────────────────────────────────────────────────────────
(deftest a-closed-comment-has-no-reply-test
  (is (false? (:reply? (model/normalize {:closed? true})))))

;; ── Labels ────────────────────────────────────────────────────────────────
(deftest a-given-label-replaces-its-default-test
  (is (= "Antwoord" (:reply-text (model/normalize {:labels-raw {:reply "Antwoord"}})))))

(deftest the-context-text-uses-the-reply-context-label-test
  (is (= "Antwoord op Ann"
         (get-in (model/normalize {:depth-raw "1" :reply-to-name-raw "Ann"
                        :labels-raw {:reply-context "Antwoord op"}})
                 [:context :text]))))

;; ── Events ────────────────────────────────────────────────────────────────
(deftest reply-detail-holds-the-comment-id-test
  (is (= {:commentId "c1"} (model/reply-detail {:comment-id "c1"}))))

(deftest every-label-has-an-attribute-and-a-default-test
  (is (= (set (keys model/default-labels)) (set (keys model/label-attributes)))))
