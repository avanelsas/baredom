(ns baredom.components.x-comments.x-comments-test
  (:require [cljs.test :refer-macros [deftest is async use-fixtures]]
            [baredom.test-writes :as writes]
            [baredom.components.x-comments.x-comments :as x]
            [baredom.components.x-comments.model :as model]
            [baredom.components.x-comment.model :as comment-model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-comment [id comment-name]
  (let [node (.createElement js/document comment-model/tag-name)]
    (.setAttribute node comment-model/attr-comment-id id)
    (.setAttribute node comment-model/attr-name comment-name)
    node))

(defn ^js make-el
  "An x-comments with the comments a, b and c, and the attributes `attrs`."
  [attrs]
  (let [el (.createElement js/document model/tag-name)]
    (doseq [[k v] attrs]
      (.setAttribute el k v))
    (.append el (make-comment "a" "Ann") (make-comment "b" "Bo") (make-comment "c" "Cy"))
    el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js part [^js el part-name]
  (.querySelector (.-shadowRoot el) (str "[part=" part-name "]")))

(defn- slots-of [^js el]
  (mapv (fn slot-of [^js node] (.getAttribute node "slot")) (array-seq (.-children el))))

(defn- shown? [^js node]
  (not (.hasAttribute node "hidden")))

(defn- listen-once! [event]
  (let [seen (atom nil)]
    (.addEventListener js/document event (fn on-event [e] (reset! seen e)) #js {:once true})
    seen))

(defn- fill! [^js el comment-name text]
  (set! (.-value (part el "name")) comment-name)
  (set! (.-value (part el "text")) text))

(defn- submit! [^js el]
  (.requestSubmit (part el "form")))

;; ── Registration and structure ────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [el (append! (make-el {}))]
    (doseq [p ["thread" "heading" "form-wrap" "form" "form-title" "name" "text" "trap" "actions"
               "cancel" "send" "status"]]
      (is (some? (part el p)) p))))

(deftest the-thread-is-labelled-by-its-heading-test
  (let [el (append! (make-el {}))]
    (is (= "Comments" (.-textContent (part el "heading"))))
    (is (= (.-id (part el "heading")) (.getAttribute (part el "thread") "aria-labelledby")))))

(deftest the-trap-is-a-hidden-field-a-person-skips-test
  (let [trap (part (append! (make-el {})) "trap")]
    (is (= "x-comments-aux" (.getAttribute trap "name")))
    (is (not (.hasAttribute trap "id")))
    (is (= "-1" (.getAttribute trap "tabindex")))
    (is (= "off" (.getAttribute trap "autocomplete")))
    (is (= "true" (.getAttribute trap "aria-hidden")))))

;; ── Placement ─────────────────────────────────────────────────────────────
(deftest with-no-reply-to-every-comment-is-before-the-form-test
  (let [el (append! (make-el {}))]
    (is (= ["before" "before" "before"] (slots-of el)))
    (is (not (shown? (part el "cancel"))))))

(deftest reply-to-places-the-form-right-after-its-target-test
  (let [el (append! (make-el {model/attr-reply-to "a"}))]
    (is (= ["before" "after" "after"] (slots-of el)))
    (is (= "Reply to Ann" (.-textContent (part el "form-title"))))
    (is (shown? (part el "cancel")))))

(deftest a-comment-added-later-gets-a-slot-test
  (async done
    (let [el (append! (make-el {model/attr-reply-to "a"}))]
      (.append el (make-comment "d" "Dee"))
      (js/setTimeout (fn check []
                       (is (= ["before" "after" "after" "after"] (slots-of el)))
                       (done))
                     0))))

(deftest the-writes-on-the-comments-are-declared-test
  (let [el      (append! (make-el {}))
        written (writes/foreign-writes! el (fn change-thread []
                                             (.setAttribute el model/attr-reply-to "a")
                                             (.setAttribute el model/attr-closed "")))]
    (is (= {:undeclared #{} :unexercised #{}} (writes/audit model/foreign-writes written)))))

;; ── Closed ────────────────────────────────────────────────────────────────
(deftest closed-hides-the-form-and-marks-each-comment-test
  (let [el (append! (make-el {model/attr-closed ""}))]
    (is (not (shown? (part el "form-wrap"))))
    (is (every? (fn closed? [^js node] (.hasAttribute node comment-model/attr-closed))
                (array-seq (.-children el))))
    (.removeAttribute el model/attr-closed)
    (is (shown? (part el "form-wrap")))
    (is (not-any? (fn closed? [^js node] (.hasAttribute node comment-model/attr-closed))
                  (array-seq (.-children el))))))

;; ── Fields ────────────────────────────────────────────────────────────────
(deftest limits-reach-the-fields-test
  (let [el (append! (make-el {model/attr-name-max "40" model/attr-text-max "500"}))]
    (is (= "40" (.getAttribute (part el "name") "maxlength")))
    (is (= "500" (.getAttribute (part el "text") "maxlength")))))

(deftest labels-reach-the-form-test
  (let [el (append! (make-el {model/attr-name-label "Naam" model/attr-send-label "Verstuur"}))]
    (is (= "Naam" (.getAttribute (part el "name") "label")))
    (is (= "Verstuur" (.-textContent (part el "send"))))))

(deftest commenter-name-fills-the-name-field-test
  (let [el (append! (make-el {model/attr-commenter-name "Ann"}))]
    (is (= "Ann" (.-value (part el "name"))))))

(deftest a-change-of-another-attribute-keeps-a-typed-name-test
  (let [el (append! (make-el {model/attr-commenter-name "Ann"}))]
    (set! (.-value (part el "name")) "Annie")
    (.setAttribute el model/attr-reply-to "b")
    (is (= "Annie" (.-value (part el "name"))))))

;; ── Reply ─────────────────────────────────────────────────────────────────
(defn- reply-from! [^js el index]
  (let [^js node (aget (.-children el) index)
        id       (.getAttribute node comment-model/attr-comment-id)]
    (.dispatchEvent node (js/CustomEvent. comment-model/event-reply
                                          #js {:bubbles true :composed true
                                               :detail  #js {:commentId id}}))))

(deftest a-reply-asks-to-move-the-form-and-moves-it-test
  (let [el   (append! (make-el {}))
        seen (listen-once! model/event-reply-request)]
    (reply-from! el 1)
    (is (= "b" (some-> ^js @seen .-detail .-replyTo)))
    (is (= "" (some-> ^js @seen .-detail .-previousReplyTo)))
    (is (= "b" (.getAttribute el model/attr-reply-to)))))

(deftest a-refused-reply-request-leaves-the-form-in-place-test
  (let [el (append! (make-el {}))]
    (.addEventListener el model/event-reply-request (fn refuse [^js e] (.preventDefault e)))
    (reply-from! el 1)
    (is (not (.hasAttribute el model/attr-reply-to)))))

(deftest cancel-asks-to-move-the-form-back-test
  (let [el   (append! (make-el {model/attr-reply-to "b"}))
        seen (listen-once! model/event-reply-request)]
    (.click (.querySelector (.-shadowRoot (part el "cancel")) "button"))
    (is (= "" (some-> ^js @seen .-detail .-replyTo)))
    (is (= "b" (some-> ^js @seen .-detail .-previousReplyTo)))
    (is (not (.hasAttribute el model/attr-reply-to)))))

;; ── Submit ────────────────────────────────────────────────────────────────
(deftest a-send-carries-the-entry-test
  (let [el   (append! (make-el {model/attr-reply-to "b"}))
        seen (listen-once! model/event-submit)]
    (fill! el "Ann" "Thanks")
    (submit! el)
    (is (= {:name "Ann" :text "Thanks" :replyTo "b" :trap ""}
           (js->clj (some-> ^js @seen .-detail) :keywordize-keys true)))))

(deftest an-accepted-send-clears-the-text-and-moves-the-form-back-test
  (let [el   (append! (make-el {model/attr-reply-to "b"}))
        seen (listen-once! model/event-reply-request)]
    (fill! el "Ann" "Thanks")
    (submit! el)
    (is (= "" (.-value (part el "text"))))
    (is (= "Ann" (.-value (part el "name"))))
    (is (= "" (some-> ^js @seen .-detail .-replyTo)))
    (is (not (.hasAttribute el model/attr-reply-to)))))

(deftest a-refused-move-back-leaves-the-form-under-its-target-test
  (let [el (append! (make-el {model/attr-reply-to "b"}))]
    (.addEventListener el model/event-reply-request (fn refuse [^js e] (.preventDefault e)))
    (fill! el "Ann" "Thanks")
    (submit! el)
    (is (= "b" (.getAttribute el model/attr-reply-to)))))

(deftest an-accepted-send-is-announced-test
  (async done
    (let [el (append! (make-el {}))]
      (fill! el "Ann" "Thanks")
      (submit! el)
      (js/requestAnimationFrame
       (fn check []
         (is (= "Your comment was sent." (.-textContent (part el "status"))))
         (done))))))

(deftest a-refused-send-keeps-the-form-test
  (let [el (append! (make-el {}))]
    (.addEventListener el model/event-submit (fn refuse [^js e] (.preventDefault e)))
    (fill! el "Ann" "Thanks")
    (submit! el)
    (is (= "Thanks" (.-value (part el "text"))))
    (is (= "" (.-textContent (part el "status"))))))

(deftest an-empty-field-sends-nothing-test
  (let [el   (append! (make-el {}))
        seen (listen-once! model/event-submit)]
    (fill! el "Ann" "")
    (submit! el)
    (is (nil? @seen))))

(deftest a-filled-trap-goes-in-the-detail-test
  (let [el   (append! (make-el {}))
        seen (listen-once! model/event-submit)]
    (fill! el "Bot" "Buy now")
    (set! (.-value (part el "trap")) "http://spam")
    (submit! el)
    (is (= "http://spam" (some-> ^js @seen .-detail .-trap)))))

;; ── Focus ─────────────────────────────────────────────────────────────────
(deftest focus-goes-to-the-first-empty-field-test
  (let [el (append! (make-el {}))]
    (.focus el)
    (is (identical? (part el "name") (.. el -shadowRoot -activeElement)))
    (set! (.-value (part el "name")) "Ann")
    (.focus el)
    (is (identical? (part el "text") (.. el -shadowRoot -activeElement)))))

;; ── Properties ────────────────────────────────────────────────────────────
(deftest properties-reflect-their-attributes-test
  (let [el (append! (make-el {}))]
    (set! (.-replyTo el) "c")
    (set! (.-closed el) true)
    (set! (.-nameMax el) 50)
    (is (= "c" (.getAttribute el model/attr-reply-to)))
    (is (.hasAttribute el model/attr-closed))
    (is (= "50" (.getAttribute el model/attr-name-max)))))
