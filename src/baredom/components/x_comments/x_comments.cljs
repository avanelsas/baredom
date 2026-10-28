(ns baredom.components.x-comments.x-comments
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-button.model :as button-model]
            [baredom.components.x-comment.x-comment :as x-comment]
            [baredom.components.x-comment.model :as comment-model]
            [baredom.components.x-form-field.x-form-field :as x-form-field]
            [baredom.components.x-form-field.model :as field-model]
            [baredom.components.x-text-area.x-text-area :as x-text-area]
            [baredom.components.x-text-area.model :as area-model]
            [baredom.components.x-comments.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xCommentsRefs")
(def ^:private k-model "__xCommentsModel")

;; ── String constants ──────────────────────────────────────────────────────
(def ^:private attr-part        "part")
(def ^:private attr-id          "id")
(def ^:private attr-name        "name")
(def ^:private attr-hidden      "hidden")
(def ^:private attr-role        "role")
(def ^:private attr-labelledby  "aria-labelledby")
(def ^:private attr-aria-hidden "aria-hidden")
(def ^:private attr-novalidate  "novalidate")
(def ^:private attr-tabindex    "tabindex")
(def ^:private attr-autocomplete "autocomplete")
(def ^:private attr-required    "required")

(def ^:private heading-id     "heading")
(def ^:private form-title-id  "form-title")
(def ^:private field-name     "name")
(def ^:private field-text     "text")
(def ^:private trap-name      "x-comments-aux")
(def ^:private role-status    "status")
(def ^:private ev-slotchange  "slotchange")
(def ^:private ev-submit      "submit")
(def ^:private reduced-motion "(prefers-reduced-motion: reduce)")
(def ^:private fade-ms        150)

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private hide-visually
  "position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;")

(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "--x-comments-gap:var(--x-space-md,0.75rem);"
   "--x-comments-form-bg:var(--x-color-surface,#ffffff);"
   "--x-comments-form-border:var(--x-color-border,#e5e7eb);"
   "--x-comments-form-radius:var(--x-radius-md,8px);"
   "--x-comments-heading-font-size:var(--x-font-size-lg,1.125rem);"
   "color:var(--x-color-text,#111827);"
   "font-family:var(--x-font-family,inherit);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-comments-form-bg:var(--x-color-surface,#1f2937);"
   "--x-comments-form-border:var(--x-color-border,#374151);"
   "color:var(--x-color-text,#f9fafb);}}"
   du/control-font-rule
   du/hidden-rule

   "[part=thread]{display:flex;flex-direction:column;gap:var(--x-comments-gap);}"

   "[part=heading]{margin:0;font-size:var(--x-comments-heading-font-size);"
   "font-weight:var(--x-font-weight-semibold,600);}"

   "slot{display:contents;}"

   "[part=form]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-space-sm,0.5rem);"
   "padding:var(--x-space-md,0.75rem);"
   "background:var(--x-comments-form-bg);"
   "border:var(--x-border-width,1px) solid var(--x-comments-form-border);"
   "border-radius:var(--x-comments-form-radius);}"

   "[part=form-title]{margin:0;font-weight:var(--x-font-weight-medium,500);}"

   "[part=actions]{display:flex;flex-wrap:wrap;justify-content:flex-end;"
   "gap:var(--x-space-sm,0.5rem);}"

   "[part=trap],[part=status]{" hide-visually "}"

   "[hidden]{display:none;}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- make-part! [tag part]
  (let [node (.createElement js/document tag)]
    (du/set-attr! node attr-part part)
    node))

(defn- make-slot! [slot-name]
  (let [slot (.createElement js/document "slot")]
    (when slot-name (du/set-attr! slot attr-name slot-name))
    slot))

(defn- make-field! [tag part field]
  (let [node (make-part! tag part)]
    (du/set-attr! node attr-name field)
    (du/set-attr! node attr-required "")
    node))

(defn- make-trap! []
  (let [trap (make-part! "input" "trap")]
    (du/set-attr! trap attr-name trap-name)
    (du/set-attr! trap attr-tabindex "-1")
    (du/set-attr! trap attr-autocomplete "off")
    (du/set-attr! trap attr-aria-hidden "true")
    trap))

(defn- make-actions! []
  (let [actions (make-part! "div" "actions")
        cancel  (make-part! button-model/tag-name "cancel")
        send    (make-part! button-model/tag-name "send")]
    (du/set-attr! cancel button-model/attr-variant "ghost")
    (du/set-attr! send button-model/attr-type "submit")
    (.append actions cancel send)
    {:actions actions :cancel cancel :send send}))

(defn- make-form-section! []
  (let [wrap   (make-part! "div" "form-wrap")
        form   (make-part! "form" "form")
        title  (make-part! "p" "form-title")
        nm     (make-field! field-model/tag-name "name" field-name)
        text   (make-field! area-model/tag-name "text" field-text)
        trap   (make-trap!)
        status (make-part! "p" "status")
        {:keys [actions] :as action-refs} (make-actions!)]
    (du/set-attr! form attr-novalidate "")
    (du/set-attr! form attr-labelledby form-title-id)
    (du/set-attr! title attr-id form-title-id)
    (du/set-attr! nm attr-autocomplete "name")
    (du/set-attr! status attr-role role-status)
    (.append form title nm text trap (make-slot! model/slot-notice) actions status)
    (.appendChild wrap form)
    (assoc action-refs :wrap wrap :form form :title title :name-field nm :text-field text
           :trap trap :status status)))

(declare install-listeners!)

(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        section (make-part! "section" "thread")
        heading (make-part! "h2" "heading")
        before  (make-slot! model/slot-before)
        after   (make-slot! model/slot-after)
        unplaced (make-slot! nil)
        {:keys [wrap] :as form-refs} (make-form-section!)
        refs    (assoc form-refs :heading heading :before before :after after :unplaced unplaced)]
    (set! (.-textContent style) style-text)
    (du/set-attr! section attr-labelledby heading-id)
    (du/set-attr! heading attr-id heading-id)
    (.append section heading before wrap after unplaced)
    (.append root style section)
    (install-listeners! el refs)
    (du/setv! el k-refs refs)))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Reading ───────────────────────────────────────────────────────────────
(defn- comment-nodes [^js el]
  (filterv (fn comment? [^js node] (= comment-model/tag-name (.-localName node)))
           (array-seq (.-children el))))

(defn- read-comment [^js node]
  {:id   (du/get-attr node comment-model/attr-comment-id)
   :name (du/get-attr node comment-model/attr-name)})

(defn- read-model [^js el]
  (model/normalize
   {:closed?            (du/has-attr? el model/attr-closed)
    :reply-to-raw       (du/get-attr el model/attr-reply-to)
    :commenter-name-raw (du/get-attr el model/attr-commenter-name)
    :name-max-raw       (du/get-attr el model/attr-name-max)
    :text-max-raw       (du/get-attr el model/attr-text-max)
    :labels-raw         (update-vals model/label-attributes (partial du/get-attr el))
    :comments           (mapv read-comment (comment-nodes el))}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- show! [^js node visible?]
  (du/set-bool-attr-to! node attr-hidden (not visible?)))

(defn- apply-comments! [^js el {:keys [slots closed?]}]
  (doseq [[^js node slot] (map vector (comment-nodes el) slots)]
    (du/set-attr-to! node model/attr-slot slot)
    (du/set-bool-attr-to! node comment-model/attr-closed closed?)))

(defn- apply-labels! [refs {:keys [labels form-title]}]
  (du/set-text-to! (:heading refs) (:heading labels))
  (du/set-text-to! (:title refs) form-title)
  (du/set-attr-to! (:name-field refs) field-model/attr-label (:name labels))
  (du/set-attr-to! (:text-field refs) area-model/attr-label (:text labels))
  (du/set-text-to! (:send refs) (:send labels))
  (du/set-text-to! (:cancel refs) (:cancel labels)))

(defn- apply-limits! [refs {:keys [name-max text-max]}]
  (du/set-attr-to! (:name-field refs) field-model/attr-maxlength (str name-max))
  (du/set-attr-to! (:text-field refs) area-model/attr-maxlength (str text-max)))

(defn- apply-form! [refs {:keys [closed? reply-to]}]
  (show! (:wrap refs) (not closed?))
  (show! (:cancel refs) (some? reply-to)))

(defn- apply-commenter-name! [refs old-m {:keys [commenter-name]}]
  (when (not= commenter-name (:commenter-name old-m))
    (set! (.-value ^js (:name-field refs)) commenter-name)))

(defn- fade-in! [^js node]
  (when-not (.-matches (js/matchMedia reduced-motion))
    (.animate node #js [#js {:opacity 0} #js {:opacity 1}] fade-ms)))

(defn- apply-motion! [refs old-m {:keys [reply-to]}]
  (when (and old-m (not= reply-to (:reply-to old-m)))
    (fade-in! (:wrap refs))))

(defn- apply-model! [^js el old-m m]
  (let [refs (ensure-refs! el)]
    (apply-comments!       el m)
    (apply-labels!         refs m)
    (apply-limits!         refs m)
    (apply-form!           refs m)
    (apply-commenter-name! refs old-m m)
    (apply-motion!         refs old-m m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el old-m new-m))))

;; ── Requests ──────────────────────────────────────────────────────────────
(defn- request-reply-to!
  "Asks to move the form to `reply-to`, and moves it unless the request is refused."
  [^js el reply-to]
  (let [previous (:reply-to (du/getv el k-model))
        allowed? (du/dispatch-cancelable! el model/event-reply-request
                                          (clj->js (model/reply-request-detail reply-to previous)))]
    (when allowed?
      (if (seq reply-to)
        (du/set-attr! el model/attr-reply-to reply-to)
        (du/remove-attr! el model/attr-reply-to)))
    allowed?))

(defn- comment-node [^js el id]
  (some (fn same-id [^js node] (when (= id (du/get-attr node comment-model/attr-comment-id)) node))
        (comment-nodes el)))

(defn- announce!
  "Has a screen reader speak `text`, also when it equals the last one."
  [refs text]
  (let [^js status (:status refs)]
    (du/set-text-to! status "")
    (js/requestAnimationFrame (fn speak [] (du/set-text-to! status text)))))

;; ── Events ────────────────────────────────────────────────────────────────
(defn- on-slotchange! [^js el _e]
  (update-from-attrs! el))

(defn- on-reply! [^js el ^js e]
  (let [refs (du/getv el k-refs)]
    (when (request-reply-to! el (.. e -detail -commentId))
      (.focus ^js (:text-field refs)))))

(defn- on-cancel! [^js el _e]
  (let [answered (:reply-to (du/getv el k-model))]
    (when (request-reply-to! el "")
      (some-> (comment-node el answered) .focus))))

(defn- first-invalid [refs]
  (some (fn invalid? [^js field] (when-not (.checkValidity field) field))
        [(:name-field refs) (:text-field refs)]))

(defn- entry [refs m]
  {:name     (.-value ^js (:name-field refs))
   :text     (.-value ^js (:text-field refs))
   :reply-to (:reply-to m)
   :trap     (.-value ^js (:trap refs))})

(defn- sent! [^js el refs]
  (set! (.-value ^js (:text-field refs)) "")
  (when (:reply-to (du/getv el k-model))
    (request-reply-to! el ""))
  (announce! refs (get-in (du/getv el k-model) [:labels :sent]))
  (.focus ^js (:text-field refs)))

(defn- on-submit! [^js el ^js e]
  (.preventDefault e)
  (let [refs (du/getv el k-refs)]
    (if-let [^js invalid (first-invalid refs)]
      (.reportValidity invalid)
      (when (du/dispatch-cancelable! el model/event-submit
                                     (clj->js (model/submit-detail (entry refs (du/getv el k-model)))))
        (sent! el refs)))))

;; ── Listeners ─────────────────────────────────────────────────────────────
(def ^:private listener-spec
  [[:before  ev-slotchange            on-slotchange!]
   [:after   ev-slotchange            on-slotchange!]
   [:unplaced ev-slotchange           on-slotchange!]
   [:form    ev-submit                on-submit!]
   [:cancel  button-model/event-press on-cancel!]])

(defn- install-listeners! [^js el refs]
  (doseq [[k event handler] listener-spec]
    (.addEventListener ^js (get refs k) event (fn on-event [e] (handler el e))))
  (.addEventListener el comment-model/event-reply (fn on-comment-reply [e] (on-reply! el e))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

(defn- focus-form! [^js el]
  (let [refs (ensure-refs! el)
        ^js nm (:name-field refs)]
    (.focus ^js (if (seq (.-value nm)) (:text-field refs) nm))))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (.defineProperty js/Object proto "focus"
                   #js {:value        (fn xcomments-focus [] (this-as ^js this (focus-form! this)))
                        :writable     true
                        :configurable true}))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (x-button/init!)
  (x-comment/init!)
  (x-form-field/init!)
  (x-text-area/init!)
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
