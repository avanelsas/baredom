(ns baredom.components.x-skeleton-group.x-skeleton-group
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [clojure.string :as str]
            [baredom.components.x-skeleton-group.model :as model]
            [baredom.components.x-skeleton.model :as skeleton-model]
            [baredom.components.x-skeleton.x-skeleton :as x-skeleton]
            [baredom.utils.dom :as du]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xSkeletonGroupRefs")
(def ^:private k-model    "__xSkeletonGroupModel")
(def ^:private k-handlers "__xSkeletonGroupHandlers")

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------
(def ^:private sync-duration-ms 1500)
(def ^:private skeleton-tag-upper (str/upper-case skeleton-model/tag-name))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "}"
   "[part=container]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-skeleton-group-gap,12px);"
   "}"
   "[part=preset-container]:empty{"
   "display:none;"
   "}"
   ":host([preset]) slot{"
   "display:none;"
   "}"
   ;; Preset layout helpers
   ".sg-row{"
   "display:flex;"
   "flex-direction:row;"
   "align-items:center;"
   "}"
   ".sg-col{"
   "display:flex;"
   "flex-direction:column;"
   "}"
   ;; Reduced motion handled by x-skeleton children
   ))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style-el     (make-el "style")
        container-el (make-el "div")
        preset-el    (make-el "div")
        slot-el      (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! container-el "part" "container")
    (du/set-attr! preset-el    "part" "preset-container")

    (.appendChild container-el preset-el)
    (.appendChild container-el slot-el)

    (.appendChild root style-el)
    (.appendChild root container-el)

    (let [refs #js {:container container-el
                    :preset    preset-el
                    :slot      slot-el}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Model reading
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:preset-raw    (du/get-attr el model/attr-preset)
    :animation-raw (du/get-attr el model/attr-animation)
    :count-raw     (du/get-attr el model/attr-count)}))

;; ---------------------------------------------------------------------------
;; Preset DOM building
;; ---------------------------------------------------------------------------
(defn- build-item!
  "Builds a single preset item (x-skeleton element or nested layout div).
   Returns the created element."
  [^js item-def animation]
  (if (:layout item-def)
    ;; Nested layout container
    (let [^js div (make-el "div")
          cls     (if (= :horizontal (:layout item-def)) "sg-row" "sg-col")]
      (set! (.-className div) cls)
      (set! (.. div -style -gap) (:gap item-def))
      (when-let [flex (:flex item-def)]
        (set! (.. div -style -flex) flex))
      (doseq [child-def (:items item-def)]
        (.appendChild div (build-item! child-def animation)))
      div)
    ;; Leaf: x-skeleton element
    (let [^js skel (make-el "x-skeleton")]
      (when-let [v (:variant item-def)]
        (du/set-attr! skel "variant" v))
      (when-let [w (:width item-def)]
        (du/set-attr! skel "width" w))
      (when-let [h (:height item-def)]
        (du/set-attr! skel "height" h))
      (when-let [flex (:flex item-def)]
        (set! (.. skel -style -flex) flex))
      (du/set-attr! skel "animation" animation)
      skel)))

(defn- build-preset! [^js preset-el preset-name animation cnt]
  (set! (.-textContent preset-el) "")
  (when-let [preset-def (get model/presets preset-name)]
    (let [layout-cls (if (= :horizontal (:layout preset-def)) "sg-row" "sg-col")]
      (dotimes [_ cnt]
        (let [^js wrap (make-el "div")]
          (set! (.-className wrap) layout-cls)
          (set! (.. wrap -style -gap) (:gap preset-def))
          (doseq [item-def (:items preset-def)]
            (.appendChild wrap (build-item! item-def animation)))
          (.appendChild preset-el wrap))))))

(defn- clear-preset! [^js preset-el]
  (set! (.-textContent preset-el) ""))

;; ---------------------------------------------------------------------------
;; Animation sync
;; ---------------------------------------------------------------------------
(defn- sync-animations! [^js el]
  (let [now      (js/performance.now)
        delay-ms (- (mod now sync-duration-ms))]
    (.setProperty (.-style el) "--x-skeleton-delay" (str delay-ms "ms"))))

;; ---------------------------------------------------------------------------
;; Animation propagation to slotted children
;; ---------------------------------------------------------------------------
(defn- propagate-animation! [^js el animation]
  (when-let [refs (du/getv el k-refs)]
    (let [^js slot-el (gobj/get refs "slot")
          ^js nodes   (.assignedElements slot-el #js {:flatten true})]
      (doseq [^js node (array-seq nodes)]
        (when (= skeleton-tag-upper (.-tagName node))
          (du/set-attr! node "animation" animation))))))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [m         (read-model el)
          old-m     (du/getv el k-model)
          ^js preset-el (gobj/get refs "preset")]

      (when (not= m old-m)
        (du/setv! el k-model m)

        (let [{:keys [preset animation count]} m]
          ;; Build or clear preset
          (if preset
            (build-preset! preset-el preset animation count)
            (clear-preset! preset-el))

          ;; Propagate animation to slotted children
          (propagate-animation! el animation))

        ;; Sync animation timing
        (sync-animations! el)

        ;; Accessibility
        (when-not (du/has-attr? el "aria-hidden")
          (du/set-attr! el "aria-hidden" "true"))))))

;; ---------------------------------------------------------------------------
;; Handlers
;; ---------------------------------------------------------------------------
(defn- make-handlers [^js el]
  (let [on-slotchange
        (fn [^js _evt]
          (let [{:keys [animation]} (read-model el)]
            (propagate-animation! el animation)
            (sync-animations! el)))]
    #js {:slotchange on-slotchange}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (.addEventListener (gobj/get refs "slot") "slotchange"
                         (gobj/get handlers "slotchange")))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (.removeEventListener (gobj/get refs "slot") "slotchange"
                            (gobj/get handlers "slotchange")))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (du/setv! el k-handlers (make-handlers el))
  (add-listeners! el)
  (du/setv! el k-model nil)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (when (du/getv el k-refs)
    (render! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto "preset"    model/attr-preset "")
  (du/define-string-prop! proto "animation" model/attr-animation "")
  (du/define-number-prop! proto "count" model/attr-count model/default-count))

(defn init! []
  (x-skeleton/init!)
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
