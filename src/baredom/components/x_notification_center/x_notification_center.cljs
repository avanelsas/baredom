(ns baredom.components.x-notification-center.x-notification-center
  (:require
   [goog.object :as gobj]
   [baredom.components.x-notification-center.model :as model]
   [baredom.components.x-alert.model :as alert-model]))

;; ── Instance-field key constants ─────────────────────────────────────────────
(def ^:private k-refs     "__xNcRefs")
(def ^:private k-handlers "__xNcHandlers")
(def ^:private k-max      "__xNcMax")

;; ── Module-level ID counter (no atom) ────────────────────────────────────────
(def ^:private next-id-counter #js {"v" 0})

(defn- next-id! []
  (let [id (aget next-id-counter "v")]
    (aset next-id-counter "v" (inc id))
    (str "xnc-" id)))

;; ── Styles ────────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:fixed;"
   "z-index:var(--x-notification-center-z-index,9999);"
   "--x-notification-center-width:360px;"
   "--x-notification-center-gap:8px;"
   "--x-notification-center-offset-x:16px;"
   "--x-notification-center-offset-y:16px;"
   "--x-notification-center-z-index:9999;"
   "pointer-events:none;"
   "}"

   ":host([data-position='top-right']){"
   "top:var(--x-notification-center-offset-y);"
   "right:var(--x-notification-center-offset-x);"
   "}"

   ":host([data-position='top-left']){"
   "top:var(--x-notification-center-offset-y);"
   "left:var(--x-notification-center-offset-x);"
   "}"

   ":host([data-position='bottom-right']){"
   "bottom:var(--x-notification-center-offset-y);"
   "right:var(--x-notification-center-offset-x);"
   "}"

   ":host([data-position='bottom-left']){"
   "bottom:var(--x-notification-center-offset-y);"
   "left:var(--x-notification-center-offset-x);"
   "}"

   ":host([data-position='top-center']){"
   "top:var(--x-notification-center-offset-y);"
   "left:50%;"
   "transform:translateX(-50%);"
   "}"

   ":host([data-position='bottom-center']){"
   "bottom:var(--x-notification-center-offset-y);"
   "left:50%;"
   "transform:translateX(-50%);"
   "}"

   "[part=container]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-notification-center-gap);"
   "width:var(--x-notification-center-width);"
   "pointer-events:auto;"
   "}"

   ":host([data-position='bottom-right']) [part=container],"
   ":host([data-position='bottom-left']) [part=container],"
   ":host([data-position='bottom-center']) [part=container]{"
   "flex-direction:column-reverse;"
   "}"))

;; ── DOM initialisation ────────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")]
    (set! (.-textContent style) style-text)
    (.setAttribute container "part" "container")
    (.setAttribute container "role" "log")
    (.setAttribute container "aria-live" "polite")
    (.appendChild root style)
    (.appendChild root container)
    (gobj/set el k-refs #js {"root" root "container" container}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Position ──────────────────────────────────────────────────────────────────
(defn- apply-position! [^js el]
  (let [raw (.getAttribute el model/attr-position)
        pos (model/parse-position raw)]
    (.setAttribute el model/data-position pos))
  nil)

;; ── Event dispatch helpers ────────────────────────────────────────────────────
(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent el
                  (js/CustomEvent. event-name
                                   #js {:detail   (clj->js detail)
                                        :bubbles  true
                                        :composed true
                                        :cancelable false}))
  nil)

;; ── Alert dismiss handler ─────────────────────────────────────────────────────
(defn- on-alert-dismiss [^js el ^js e]
  (let [refs      (ensure-refs! el)
        ^js container (gobj/get refs "container")
        ^js alert (.-target e)
        id        (.getAttribute alert model/data-notification-id)
        ;; Alert is still in the DOM during the dismiss event; dec to get post-removal count
        new-count (dec (.-length (.querySelectorAll container model/alert-tag)))
        type-val  (.getAttribute alert "type")
        reason    (.. e -detail -reason)
        text-val  (.. e -detail -text)]
    (dispatch! el model/event-dismiss
               {:id     id
                :type   (or type-val "info")
                :reason (or reason "")
                :text   (or text-val "")
                :count  new-count})
    (when (zero? new-count)
      (dispatch! el model/event-empty {})))
  nil)

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs  (ensure-refs! el)
        ^js root (gobj/get refs "root")
        dismiss-h (fn [e] (on-alert-dismiss el e))]
    (.addEventListener root alert-model/event-dismiss dismiss-h #js {:capture false})
    (gobj/set el k-handlers #js {"dismiss" dismiss-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js root     (gobj/get refs "root")
            dismiss-h (gobj/get hs "dismiss")]
        (when dismiss-h
          (.removeEventListener root alert-model/event-dismiss dismiss-h #js {:capture false})))))
  (gobj/set el k-handlers nil)
  nil)

;; ── push! ────────────────────────────────────────────────────────────────────
(defn- push! [^js el ^js opts]
  (let [refs      (ensure-refs! el)
        ^js container (gobj/get refs "container")
        current-max (or (gobj/get el k-max) model/default-max)
        current-count (.-length (.querySelectorAll container model/alert-tag))]
    (when (< current-count current-max)
      (let [^js alert (.createElement js/document model/alert-tag)
            id        (or (gobj/get opts "id") (next-id!))
            type-val  (gobj/get opts "type")
            text-val  (gobj/get opts "text")
            icon-val  (gobj/get opts "icon")
            timeout-val (gobj/get opts "timeoutMs")
            dismissible-val (gobj/get opts "dismissible")]
        (.setAttribute alert model/data-notification-id id)
        (when type-val    (.setAttribute alert "type" type-val))
        (when text-val    (.setAttribute alert "text" text-val))
        (when icon-val    (.setAttribute alert "icon" icon-val))
        (when timeout-val (.setAttribute alert "timeout-ms" (str (int timeout-val))))
        ;; Only set dismissible=false when explicitly false; default (true) is handled by x-alert
        (when (false? dismissible-val) (.setAttribute alert "dismissible" "false"))
        (.appendChild container alert)
        (let [new-count (.-length (.querySelectorAll container model/alert-tag))]
          (dispatch! el model/event-push {:id id :count new-count}))
        id))))

;; ── clear! ───────────────────────────────────────────────────────────────────
(defn- clear! [^js el]
  (let [refs      (ensure-refs! el)
        ^js container (gobj/get refs "container")
        alerts    (.querySelectorAll container model/alert-tag)]
    (doseq [^js alert (array-seq alerts)]
      (.remove alert)))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-position
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-position (.getAttribute this model/attr-position))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-position (str v))
                                          (.removeAttribute this model/attr-position))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-max
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-max (.getAttribute this model/attr-max))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-max (str (int v)))
                                          (.removeAttribute this model/attr-max))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "count"
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [refs (gobj/get this k-refs)]
                                          (if refs
                                            (let [^js container (gobj/get refs "container")]
                                              (.-length (.querySelectorAll container model/alert-tag)))
                                            0))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (apply-position! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n _old-val _new-val]
            (this-as ^js this
                     (cond
                       (= n model/attr-position) (apply-position! this)
                       (= n model/attr-max)      (gobj/set this k-max (model/parse-max (.getAttribute this model/attr-max))))
                     nil)))

    (install-property-accessors! (.-prototype klass))

    (set! (.-push (.-prototype klass))
          (fn [opts]
            (this-as ^js this (push! this opts))))

    (set! (.-clear (.-prototype klass))
          (fn []
            (this-as ^js this (clear! this))))

    klass))

;; ── Public API ────────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
