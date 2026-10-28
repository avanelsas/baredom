(ns baredom.components.x-comment.x-comment-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures]]
            [baredom.components.x-comment.x-comment :as x]
            [baredom.components.x-comment.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [attrs]
  (let [el (.createElement js/document model/tag-name)]
    (doseq [[k v] attrs]
      (.setAttribute el k v))
    el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js part [^js el part-name]
  (.querySelector (.-shadowRoot el) (str "[part=" part-name "]")))

(defn- shown? [^js node]
  (not (.hasAttribute node "hidden")))

(defn- text [^js node]
  (.-textContent node))

(defn- level [^js el]
  (.. el -style (getPropertyValue "--x-comment-level")))

;; ── Registration and structure ────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [el (append! (make-el {}))]
    (doseq [p ["comment" "avatar" "main" "meta" "name" "badge" "date" "context" "body"
               "notice" "reply"]]
      (is (some? (part el p)) p))
    (is (some? (.querySelector (part el "body") "slot")))))

(deftest the-article-is-labelled-by-its-meta-test
  (let [el (append! (make-el {}))]
    (is (= (.-id (part el "meta")) (.getAttribute (part el "comment") "aria-labelledby")))))

;; ── Identity and date ─────────────────────────────────────────────────────
(deftest name-reaches-the-name-and-the-avatar-test
  (let [el (append! (make-el {model/attr-name "Ann Lee"}))]
    (is (= "Ann Lee" (text (part el "name"))))
    (is (= "Ann Lee" (.getAttribute (part el "avatar") "name")))))

(deftest the-avatar-is-hidden-from-assistive-technology-test
  (let [el (append! (make-el {}))]
    (is (= "true" (.getAttribute (part el "avatar") "aria-hidden")))))

(deftest datetime-fills-the-time-element-test
  (let [el   (append! (make-el {model/attr-datetime "2026-10-28"}))
        date (part el "date")]
    (is (= "2026-10-28" (.getAttribute date "datetime")))
    (is (not= "" (text date)))
    (is (shown? date))))

(deftest an-unreadable-datetime-hides-the-date-test
  (let [el   (append! (make-el {model/attr-datetime "soon"}))
        date (part el "date")]
    (is (not (.hasAttribute date "datetime")))
    (is (not (shown? date)))))

;; ── Depth ─────────────────────────────────────────────────────────────────
(deftest depth-sets-the-indent-level-test
  (let [el (append! (make-el {model/attr-depth "1"}))]
    (is (= "1" (level el)))))

(deftest the-indent-level-stops-at-max-depth-test
  (let [el (append! (make-el {model/attr-depth "4"}))]
    (is (= "2" (level el)))
    (.setAttribute el model/attr-max-depth "3")
    (is (= "3" (level el)))))

(deftest a-reply-within-the-cap-has-quiet-context-text-test
  (let [el      (append! (make-el {model/attr-depth "1" model/attr-reply-to-name "Ann"}))
        context (part el "context")]
    (is (= "Reply to Ann" (text context)))
    (is (shown? context))
    (is (.hasAttribute context "data-quiet"))))

(deftest a-reply-past-the-cap-shows-its-context-text-test
  (let [el (append! (make-el {model/attr-depth "3" model/attr-reply-to-name "Ann"}))]
    (is (not (.hasAttribute (part el "context") "data-quiet")))))

(deftest a-top-level-comment-has-no-context-test
  (let [el (append! (make-el {}))]
    (is (not (shown? (part el "context"))))))

;; ── Status ────────────────────────────────────────────────────────────────
(deftest a-published-comment-shows-reply-and-no-badge-test
  (let [el (append! (make-el {}))]
    (is (shown? (part el "reply")))
    (is (= "Reply" (text (part el "reply"))))
    (is (not (shown? (part el "badge"))))
    (is (not (shown? (part el "notice"))))))

(deftest a-pending-comment-is-badged-muted-and-not-replyable-test
  (let [el (append! (make-el {model/attr-status "pending"}))]
    (is (= "Awaiting review" (text (part el "badge"))))
    (is (.hasAttribute (part el "body") "data-muted"))
    (is (not (shown? (part el "reply"))))))

(deftest a-removed-comment-shows-only-its-notice-test
  (let [el (append! (make-el {model/attr-status "removed" model/attr-name "Ann"}))]
    (is (not (shown? (part el "avatar"))))
    (is (not (shown? (part el "name"))))
    (is (not (shown? (part el "body"))))
    (is (= "This comment was removed." (text (part el "notice"))))
    (is (not (shown? (part el "reply"))))))

(deftest an-expired-comment-shows-its-date-and-notice-test
  (let [el (append! (make-el {model/attr-status "expired" model/attr-datetime "2026-09-28"}))]
    (is (shown? (part el "date")))
    (is (not (shown? (part el "body"))))
    (is (= "Your comment was not published." (text (part el "notice"))))))

(deftest the-article-carries-the-status-of-the-model-test
  (let [el (append! (make-el {model/attr-status "pending"}))]
    (is (= "pending" (.getAttribute (part el "comment") "data-status")))
    (.setAttribute el model/attr-status "unknown")
    (is (= "published" (.getAttribute (part el "comment") "data-status")))))

(deftest an-author-reply-is-badged-and-highlighted-test
  (let [el (append! (make-el {model/attr-author ""}))]
    (is (= "Author" (text (part el "badge"))))
    (is (.hasAttribute (part el "comment") "data-author"))))

(deftest the-closed-mark-hides-reply-test
  (let [el (append! (make-el {}))]
    (.setAttribute el model/attr-closed "")
    (is (not (shown? (part el "reply"))))
    (.removeAttribute el model/attr-closed)
    (is (shown? (part el "reply")))))

;; ── Labels ────────────────────────────────────────────────────────────────
(deftest labels-replace-the-default-text-test
  (let [el (append! (make-el {model/attr-reply-label   "Antwoord"
                              model/attr-removed-label "Verwijderd."
                              model/attr-status        "removed"}))]
    (is (= "Antwoord" (text (part el "reply"))))
    (is (= "Verwijderd." (text (part el "notice"))))))

;; ── Reply ─────────────────────────────────────────────────────────────────
(defn- press-reply! [^js el]
  (.click (.querySelector (.-shadowRoot (part el "reply")) "button")))

(deftest pressing-reply-sends-x-comment-reply-with-the-id-test
  (let [el   (append! (make-el {model/attr-comment-id "c1"}))
        seen (atom nil)]
    (.addEventListener js/document model/event-reply
                       (fn on-reply [^js e] (reset! seen e)) #js {:once true})
    (press-reply! el)
    (is (= "c1" (some-> ^js @seen .-detail .-commentId)))
    (is (true? (some-> ^js @seen .-composed)))))

(deftest focus-on-the-host-reaches-the-reply-button-test
  (let [el (append! (make-el {}))]
    (.focus el)
    (is (identical? (part el "reply") (.. el -shadowRoot -activeElement)))))

;; ── Properties ────────────────────────────────────────────────────────────
(deftest properties-reflect-their-attributes-test
  (let [el (append! (make-el {}))]
    (set! (.-commentId el) "c2")
    (set! (.-depth el) 2)
    (set! (.-status el) "pending")
    (set! (.-author el) true)
    (is (= "c2" (.getAttribute el model/attr-comment-id)))
    (is (= "2" (.getAttribute el model/attr-depth)))
    (is (= "pending" (.getAttribute el model/attr-status)))
    (is (.hasAttribute el model/attr-author))))

(deftest property-defaults-read-when-attributes-are-absent-test
  (let [el (append! (make-el {}))]
    (is (= 0 (.-depth el)))
    (is (= 2 (.-maxDepth el)))
    (is (= "published" (.-status el)))
    (is (= "Reply" (.-replyLabel el)))))
