(ns baredom.components.x-comment.x-comment
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-avatar.x-avatar :as x-avatar]
            [baredom.components.x-avatar.model :as avatar-model]
            [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-button.model :as button-model]
            [baredom.components.x-comment.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xCommentRefs")
(def ^:private k-model "__xCommentModel")

;; ── String constants ──────────────────────────────────────────────────────
(def ^:private attr-part        "part")
(def ^:private attr-id          "id")
(def ^:private attr-hidden      "hidden")
(def ^:private attr-datetime    "datetime")
(def ^:private attr-labelledby  "aria-labelledby")
(def ^:private attr-aria-hidden "aria-hidden")
(def ^:private attr-author      "data-author")
(def ^:private attr-status      "data-status")
(def ^:private attr-muted       "data-muted")
(def ^:private attr-quiet       "data-quiet")

(def ^:private meta-id         "meta")
(def ^:private css-var-level   "--x-comment-level")
(def ^:private avatar-size     "sm")
(def ^:private button-variant  "ghost")
(def ^:private button-size     "sm")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "--x-comment-indent:var(--x-space-xl,2rem);"
   "--x-comment-bg:var(--x-color-surface,#ffffff);"
   "--x-comment-border:var(--x-color-border,#e5e7eb);"
   "--x-comment-radius:var(--x-radius-md,8px);"
   "--x-comment-author-bg:color-mix(in srgb,var(--x-color-primary,#2563eb) 8%,var(--x-comment-bg));"
   "--x-comment-pending-opacity:var(--x-opacity-placeholder,0.6);"
   "--x-comment-meta-color:var(--x-color-text-muted,#6b7280);"
   "margin-inline-start:calc(var(" css-var-level ",0) * var(--x-comment-indent));"
   "color:var(--x-color-text,#111827);"
   "font-family:var(--x-font-family,inherit);}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-comment-bg:var(--x-color-surface,#1f2937);"
   "--x-comment-border:var(--x-color-border,#374151);"
   "--x-comment-author-bg:color-mix(in srgb,var(--x-color-primary,#60a5fa) 12%,var(--x-comment-bg));"
   "--x-comment-meta-color:var(--x-color-text-muted,#9ca3af);"
   "color:var(--x-color-text,#f9fafb);}}"

   "@media (max-width:30rem){:host{--x-comment-indent:1rem;}}"
   du/hidden-rule

   "[part=comment]{"
   "display:flex;"
   "gap:var(--x-space-sm,0.5rem);"
   "padding:var(--x-space-md,0.75rem);"
   "background:var(--x-comment-bg);"
   "border:var(--x-border-width,1px) solid var(--x-comment-border);"
   "border-radius:var(--x-comment-radius);}"

   "[part=comment][data-author]{background:var(--x-comment-author-bg);}"

   "[part=main]{flex:1 1 auto;min-width:0;}"

   "[part=meta]{"
   "display:flex;"
   "flex-wrap:wrap;"
   "align-items:baseline;"
   "gap:var(--x-space-xs,0.25rem) var(--x-space-sm,0.5rem);"
   "color:var(--x-comment-meta-color);"
   "font-size:var(--x-font-size-sm,0.875rem);}"

   "[part=name]{color:var(--x-color-text,inherit);font-weight:var(--x-font-weight-semibold,600);}"

   "[part=badge]{"
   "padding:0 var(--x-space-xs,0.25rem);"
   "border-radius:var(--x-radius-sm,4px);"
   "font-size:var(--x-font-size-xs,0.75rem);"
   "background:color-mix(in srgb,var(--x-color-primary,#2563eb) 12%,transparent);}"

   "[part=comment][data-status=pending] [part=badge]{"
   "background:color-mix(in srgb,var(--x-color-warning,#d97706) 18%,transparent);}"

   "[part=context]{margin:var(--x-space-xs,0.25rem) 0 0;color:var(--x-comment-meta-color);"
   "font-size:var(--x-font-size-sm,0.875rem);}"

   "[part=context][data-quiet]{"
   "position:absolute;width:1px;height:1px;overflow:hidden;"
   "clip:rect(0,0,0,0);white-space:nowrap;}"

   "[part=body]{margin-top:var(--x-space-xs,0.25rem);overflow-wrap:anywhere;}"

   "[part=body][data-muted]{opacity:var(--x-comment-pending-opacity);}"

   "[part=notice]{margin:var(--x-space-xs,0.25rem) 0 0;color:var(--x-comment-meta-color);"
   "font-style:italic;}"

   "[part=reply]{margin-top:var(--x-space-xs,0.25rem);}"

   "[hidden]{display:none;}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- make-part! [tag part]
  (let [node (.createElement js/document tag)]
    (du/set-attr! node attr-part part)
    node))

(defn- make-meta-section! []
  (let [header  (make-part! "header" "meta")
        name-el (make-part! "span" "name")
        badge   (make-part! "span" "badge")
        date    (make-part! "time" "date")]
    (du/set-attr! header attr-id meta-id)
    (.append header name-el badge date)
    {:header header :name-el name-el :badge badge :date date}))

(defn- make-reply-button! []
  (let [button (make-part! button-model/tag-name "reply")]
    (du/set-attr! button button-model/attr-variant button-variant)
    (du/set-attr! button button-model/attr-size button-size)
    button))

(defn- make-main-section! []
  (let [main    (make-part! "div" "main")
        context (make-part! "p" "context")
        body    (make-part! "div" "body")
        notice  (make-part! "p" "notice")
        reply   (make-reply-button!)
        {:keys [header] :as meta-refs} (make-meta-section!)]
    (.appendChild body (.createElement js/document "slot"))
    (.append main header context body notice reply)
    (assoc meta-refs :main main :context context :body body :notice notice :reply reply)))

(declare on-reply-press!)

(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open" :delegatesFocus true})
        style   (.createElement js/document "style")
        article (make-part! "article" "comment")
        avatar  (make-part! avatar-model/tag-name "avatar")
        {:keys [main reply] :as refs} (make-main-section!)]
    (set! (.-textContent style) style-text)
    (du/set-attr! article attr-labelledby meta-id)
    (du/set-attr! avatar avatar-model/attr-size avatar-size)
    (du/set-attr! avatar attr-aria-hidden "true")
    (.append article avatar main)
    (.append root style article)
    (.addEventListener reply button-model/event-press (fn on-press [_] (on-reply-press! el)))
    (du/setv! el k-refs (assoc refs :article article :avatar avatar))))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Reading ───────────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:comment-id-raw    (du/get-attr el model/attr-comment-id)
    :name-raw          (du/get-attr el model/attr-name)
    :datetime-raw      (du/get-attr el model/attr-datetime)
    :depth-raw         (du/get-attr el model/attr-depth)
    :max-depth-raw     (du/get-attr el model/attr-max-depth)
    :reply-to-name-raw (du/get-attr el model/attr-reply-to-name)
    :author?           (du/has-attr? el model/attr-author)
    :status-raw        (du/get-attr el model/attr-status)
    :closed?           (du/has-attr? el model/attr-closed)
    :labels-raw        (update-vals model/label-attributes (partial du/get-attr el))}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(def ^:private date-format
  (delay (js/Intl.DateTimeFormat. js/undefined #js {:dateStyle "medium"})))

(defn- format-date [iso]
  (.format ^js @date-format (js/Date. iso)))

(defn- show! [^js node visible?]
  (du/set-bool-attr-to! node attr-hidden (not visible?)))

(defn- apply-text! [^js node text]
  (du/set-text-to! node (or text ""))
  (show! node (some? text)))

(defn- apply-level! [^js el {:keys [level]}]
  (du/set-style-to! el css-var-level (str level)))

(defn- apply-identity! [{:keys [avatar name-el]} m]
  (du/set-attr-to! avatar avatar-model/attr-name (:name m))
  (show! avatar (:identity? m))
  (du/set-text-to! name-el (:name m))
  (show! name-el (:identity? m)))

(defn- apply-badge! [{:keys [badge]} m]
  (apply-text! badge (:badge m)))

(defn- apply-date! [{:keys [date]} {:keys [datetime]}]
  (du/set-attr-to! date attr-datetime datetime)
  (apply-text! date (some-> datetime format-date)))

(defn- apply-context! [{:keys [context]} m]
  (let [{:keys [text quiet?]} (:context m)]
    (apply-text! context text)
    (du/set-bool-attr-to! context attr-quiet quiet?)))

(defn- apply-body! [{:keys [body]} m]
  (show! body (:body? m))
  (du/set-bool-attr-to! body attr-muted (:muted? m)))

(defn- apply-notice! [{:keys [notice]} m]
  (apply-text! notice (:notice m)))

(defn- apply-reply! [{:keys [reply]} m]
  (du/set-text-to! reply (:reply-text m))
  (show! reply (:reply? m)))

(defn- apply-article! [{:keys [article]} m]
  (du/set-attr-to! article attr-status (:status m))
  (du/set-bool-attr-to! article attr-author (:author? m)))

(defn- apply-model! [^js el m]
  (let [refs (ensure-refs! el)]
    (apply-level!    el m)
    (apply-identity! refs m)
    (apply-badge!    refs m)
    (apply-date!     refs m)
    (apply-context!  refs m)
    (apply-body!     refs m)
    (apply-notice!   refs m)
    (apply-reply!    refs m)
    (apply-article!  refs m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Events ────────────────────────────────────────────────────────────────
(defn- on-reply-press! [^js el]
  (when-let [m (du/getv el k-model)]
    (du/dispatch! el model/event-reply (clj->js (model/reply-detail m)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (x-avatar/init!)
  (x-button/init!)
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
