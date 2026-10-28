(ns baredom.components.x-comment.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag & attribute constants ──────────────────────────────────────────────
(def tag-name "x-comment")

(def attr-comment-id          "comment-id")
(def attr-name                "name")
(def attr-datetime            "datetime")
(def attr-depth               "depth")
(def attr-max-depth           "max-depth")
(def attr-reply-to-name       "reply-to-name")
(def attr-author              "author")
(def attr-status              "status")
(def attr-reply-label         "reply-label")
(def attr-reply-context-label "reply-context-label")
(def attr-author-label        "author-label")
(def attr-pending-label       "pending-label")
(def attr-removed-label       "removed-label")
(def attr-expired-label       "expired-label")

;; ── Attributes a parent writes ────────────────────────────────────────────
(def attr-closed "data-comments-closed")

(def internal-attributes
  "The attributes that the parent x-comments writes. They are not part of the public API."
  #{attr-closed})

(def observed-attributes
  #js [attr-comment-id attr-name attr-datetime attr-depth attr-max-depth attr-reply-to-name
       attr-author attr-status attr-reply-label attr-reply-context-label attr-author-label
       attr-pending-label attr-removed-label attr-expired-label attr-closed])

;; ── Event constants ───────────────────────────────────────────────────────
(def event-reply "x-comment-reply")

;; ── Defaults ──────────────────────────────────────────────────────────────
(def default-status    "published")
(def default-max-depth 2)

(def label-attributes
  {:reply         attr-reply-label
   :reply-context attr-reply-context-label
   :author        attr-author-label
   :pending       attr-pending-label
   :removed       attr-removed-label
   :expired       attr-expired-label})

(def default-labels
  {:reply         "Reply"
   :reply-context "Reply to"
   :author        "Author"
   :pending       "Awaiting review"
   :removed       "This comment was removed."
   :expired       "Your comment was not published."})

;; ── Status ────────────────────────────────────────────────────────────────
(def ^:private status-views
  {"published" {:identity? true  :date? true  :body? true  :muted? false :author? true  :badge nil      :notice nil      :reply? true}
   "pending"   {:identity? true  :date? true  :body? true  :muted? true  :author? false :badge :pending :notice nil      :reply? false}
   "removed"   {:identity? false :date? false :body? false :muted? false :author? false :badge nil      :notice :removed :reply? false}
   "expired"   {:identity? false :date? true  :body? false :muted? false :author? false :badge nil      :notice :expired :reply? false}})

(def statuses (set (keys status-views)))

(defn parse-status
  "The status `s` names, or the default for an unknown one."
  [s]
  (if (contains? statuses s) s default-status))

;; ── Facts ─────────────────────────────────────────────────────────────────
(defn parse-depth
  "The depth `s` states, 0 when it states none."
  [s]
  (or (mu/parse-positive-int s) 0))

(defn parse-max-depth
  "The deepest indent level `s` states, or the default."
  [s]
  (or (mu/parse-positive-int s) default-max-depth))

(defn parse-datetime
  "The date text `s` when a date can be read from it, else nil."
  [s]
  (when (and (mu/non-empty-string? s) (not (js/isNaN (js/Date.parse s))))
    s))

(defn- context-view [reply-to-name depth max-depth labels]
  (when (mu/non-empty-string? reply-to-name)
    {:text     (str (:reply-context labels) " " reply-to-name)
     :quiet?   (<= depth max-depth)}))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "The view model of a comment from its raw attributes."
  [{:keys [comment-id-raw name-raw datetime-raw depth-raw max-depth-raw reply-to-name-raw
           author? status-raw closed? labels-raw]}]
  (let [labels    (mu/merge-texts default-labels labels-raw)
        depth     (parse-depth depth-raw)
        max-depth (parse-max-depth max-depth-raw)
        status    (parse-status status-raw)
        view      (get status-views status)
        authored? (boolean (and author? (:author? view)))]
    {:comment-id (or comment-id-raw "")
     :name       (or name-raw "")
     :identity?  (:identity? view)
     :datetime   (when (:date? view) (parse-datetime datetime-raw))
     :level      (min depth max-depth)
     :context    (context-view reply-to-name-raw depth max-depth labels)
     :status     status
     :body?      (:body? view)
     :muted?     (:muted? view)
     :author?    authored?
     :badge      (or (get labels (:badge view)) (when authored? (:author labels)))
     :notice     (get labels (:notice view))
     :reply?     (and (:reply? view) (not closed?))
     :reply-text (:reply labels)}))

(defn reply-detail
  "The detail of `x-comment-reply` for the comment `m`."
  [{:keys [comment-id]}]
  {:commentId comment-id})

;; ── Metadata ──────────────────────────────────────────────────────────────
(def property-api
  {:commentId         {:type 'string  :reflects-attribute attr-comment-id :default ""}
   :name              {:type 'string  :reflects-attribute attr-name :default ""}
   :datetime          {:type 'string  :reflects-attribute attr-datetime :default ""}
   :depth             {:type 'number  :reflects-attribute attr-depth :default 0}
   :maxDepth          {:type 'number  :reflects-attribute attr-max-depth :default default-max-depth}
   :replyToName       {:type 'string  :reflects-attribute attr-reply-to-name :default ""}
   :author            {:type 'boolean :reflects-attribute attr-author}
   :status            {:type 'string  :reflects-attribute attr-status :default default-status
                       :enum statuses}
   :replyLabel        {:type 'string  :reflects-attribute attr-reply-label
                       :default (:reply default-labels)}
   :replyContextLabel {:type 'string  :reflects-attribute attr-reply-context-label
                       :default (:reply-context default-labels)}
   :authorLabel       {:type 'string  :reflects-attribute attr-author-label
                       :default (:author default-labels)}
   :pendingLabel      {:type 'string  :reflects-attribute attr-pending-label
                       :default (:pending default-labels)}
   :removedLabel      {:type 'string  :reflects-attribute attr-removed-label
                       :default (:removed default-labels)}
   :expiredLabel      {:type 'string  :reflects-attribute attr-expired-label
                       :default (:expired default-labels)}})

(def event-schema
  {event-reply {:cancelable false :detail {:commentId 'string}}})

(def method-api {})
