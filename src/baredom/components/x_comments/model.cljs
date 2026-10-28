(ns baredom.components.x-comments.model
  (:require [baredom.utils.model :as mu]
            [baredom.components.x-comment.model :as comment-model]))

;; ── Tag & attribute constants ──────────────────────────────────────────────
(def tag-name "x-comments")

(def attr-closed           "closed")
(def attr-reply-to         "reply-to")
(def attr-commenter-name   "commenter-name")
(def attr-name-max         "name-max")
(def attr-text-max         "text-max")
(def attr-heading-label    "heading-label")
(def attr-form-label       "form-label")
(def attr-reply-form-label "reply-form-label")
(def attr-name-label       "name-label")
(def attr-text-label       "text-label")
(def attr-send-label       "send-label")
(def attr-cancel-label     "cancel-label")
(def attr-sent-label       "sent-label")

(def observed-attributes
  #js [attr-closed attr-reply-to attr-commenter-name attr-name-max attr-text-max
       attr-heading-label attr-form-label attr-reply-form-label attr-name-label attr-text-label
       attr-send-label attr-cancel-label attr-sent-label])

;; ── Attributes written on the comments ────────────────────────────────────
(def attr-slot "slot")

(def foreign-writes
  "The attributes x-comments writes on each of its comments."
  [{:target comment-model/tag-name :attributes #{attr-slot comment-model/attr-closed}}])

;; ── Slots ─────────────────────────────────────────────────────────────────
(def slot-before "before")
(def slot-after  "after")
(def slot-notice "notice")

;; ── Event constants ───────────────────────────────────────────────────────
(def event-reply-request "x-comments-reply-request")
(def event-submit        "x-comments-submit")

;; ── Defaults ──────────────────────────────────────────────────────────────
(def default-name-max 80)
(def default-text-max 4000)

(def label-attributes
  {:heading    attr-heading-label
   :form       attr-form-label
   :reply-form attr-reply-form-label
   :name       attr-name-label
   :text       attr-text-label
   :send       attr-send-label
   :cancel     attr-cancel-label
   :sent       attr-sent-label})

(def default-labels
  {:heading    "Comments"
   :form       "Leave a comment"
   :reply-form "Reply to"
   :name       "Name"
   :text       "Comment"
   :send       "Send"
   :cancel     "Cancel"
   :sent       "Your comment was sent."})

;; ── Placement ─────────────────────────────────────────────────────────────
(defn target-index
  "The place of the comment `reply-to` names in `comments`, or nil."
  [comments reply-to]
  (when (mu/non-empty-string? reply-to)
    (first (keep-indexed (fn same-id [i {:keys [id]}] (when (= id reply-to) i)) comments))))

(defn slots
  "The slot of each of the `comments`: `before` up to and including the place `target`, `after`
   from there. With no target, every comment is `before`."
  [comments target]
  (into [] (map-indexed (fn slot-of [i _] (if (and target (> i target)) slot-after slot-before)))
        comments))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  "The view model of the thread from its raw attributes and its comments `{:id :name}`."
  [{:keys [closed? reply-to-raw commenter-name-raw name-max-raw text-max-raw labels-raw
           comments]}]
  (let [labels   (mu/merge-texts default-labels labels-raw)
        target   (target-index comments reply-to-raw)
        answered (some->> target (nth comments))]
    {:closed?        (boolean closed?)
     :commenter-name (or commenter-name-raw "")
     :name-max       (or (mu/parse-positive-int name-max-raw) default-name-max)
     :text-max       (or (mu/parse-positive-int text-max-raw) default-text-max)
     :labels         labels
     :slots          (slots comments target)
     :reply-to       (:id answered)
     :form-title     (if answered
                       (str (:reply-form labels) " " (:name answered))
                       (:form labels))}))

(defn reply-request-detail
  "The detail of `x-comments-reply-request` for a move of the form to `reply-to`."
  [reply-to previous]
  {:replyTo (or reply-to "") :previousReplyTo (or previous "")})

(defn submit-detail
  "The detail of `x-comments-submit` for the entered `name` and `text`."
  [{:keys [name text reply-to trap]}]
  {:name name :text text :replyTo (or reply-to "") :trap trap})

;; ── Metadata ──────────────────────────────────────────────────────────────
(def property-api
  {:closed         {:type 'boolean :reflects-attribute attr-closed}
   :replyTo        {:type 'string  :reflects-attribute attr-reply-to :default ""}
   :commenterName  {:type 'string  :reflects-attribute attr-commenter-name :default ""}
   :nameMax        {:type 'number  :reflects-attribute attr-name-max :default default-name-max}
   :textMax        {:type 'number  :reflects-attribute attr-text-max :default default-text-max}
   :headingLabel   {:type 'string  :reflects-attribute attr-heading-label
                    :default (:heading default-labels)}
   :formLabel      {:type 'string  :reflects-attribute attr-form-label
                    :default (:form default-labels)}
   :replyFormLabel {:type 'string  :reflects-attribute attr-reply-form-label
                    :default (:reply-form default-labels)}
   :nameLabel      {:type 'string  :reflects-attribute attr-name-label
                    :default (:name default-labels)}
   :textLabel      {:type 'string  :reflects-attribute attr-text-label
                    :default (:text default-labels)}
   :sendLabel      {:type 'string  :reflects-attribute attr-send-label
                    :default (:send default-labels)}
   :cancelLabel    {:type 'string  :reflects-attribute attr-cancel-label
                    :default (:cancel default-labels)}
   :sentLabel      {:type 'string  :reflects-attribute attr-sent-label
                    :default (:sent default-labels)}})

(def event-schema
  {event-reply-request {:cancelable true
                        :requests   {attr-reply-to :replyTo}
                        :detail     {:replyTo 'string :previousReplyTo 'string}}
   event-submit        {:cancelable true
                        :detail     {:name 'string :text 'string :replyTo 'string :trap 'string}}})

(def method-api
  {:focus {:args [] :returns 'void}})
