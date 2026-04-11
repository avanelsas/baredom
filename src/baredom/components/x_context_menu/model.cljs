(ns baredom.components.x-context-menu.model
  (:require [baredom.utils.model :as mu]))

(def tag-name "x-context-menu")

(def attr-open      "open")
(def attr-disabled  "disabled")
(def attr-placement "placement")
(def attr-offset    "offset")
(def attr-z-index   "z-index")

(def observed-attributes
  #js ["open" "disabled" "placement" "offset" "z-index"])

(def valid-placements
  #{"bottom-start" "bottom-end" "top-start" "top-end" "right-start" "left-start"})

(def default-placement "bottom-start")
(def default-offset    8)
(def default-z-index   1000)

(def event-open-request  "x-context-menu-open-request")
(def event-open          "x-context-menu-open")
(def event-close-request "x-context-menu-close-request")
(def event-close         "x-context-menu-close")
(def event-select        "x-context-menu-select")

(def property-api
  {:open      {:type 'boolean :reflects-attribute attr-open}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :placement {:type 'string  :reflects-attribute attr-placement}
   :offset    {:type 'number  :reflects-attribute attr-offset}
   :z-index   {:type 'number  :reflects-attribute attr-z-index}})

;; ---- Pure parsing ----

(defn parse-placement [s]
  (if (and (string? s) (contains? valid-placements s))
    s
    default-placement))

(defn parse-int-pos [s default-val]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (not (js/isNaN n)) (pos? n)) n default-val))
    default-val))

(defn normalize
  [{:keys [open-present? disabled-present? placement-raw offset-raw z-index-raw]}]
  {:open?     (mu/parse-bool-attr open-present?)
   :disabled? (mu/parse-bool-attr disabled-present?)
   :placement (parse-placement placement-raw)
   :offset    (parse-int-pos offset-raw default-offset)
   :z-index   (parse-int-pos z-index-raw default-z-index)})

;; ---- Pure position calculation ----

(defn- fits? [edge size limit margin]
  (and (>= edge margin) (<= (+ edge size) (- limit margin))))

(defn- opposite-placement [placement]
  (cond
    (= placement "bottom-start") "top-start"
    (= placement "bottom-end")   "top-end"
    (= placement "top-start")    "bottom-start"
    (= placement "top-end")      "bottom-end"
    (= placement "right-start")  "left-start"
    (= placement "left-start")   "right-start"
    :else placement))

(defn- candidate-xy
  [placement offset anchor-rect panel-size]
  (let [{ax :x ay :y aw :width ah :height} anchor-rect
        {pw :width ph :height} panel-size]
    (case placement
      "bottom-start" {:x ax       :y (+ ay ah offset)}
      "bottom-end"   {:x (- (+ ax aw) pw) :y (+ ay ah offset)}
      "top-start"    {:x ax       :y (- ay ph offset)}
      "top-end"      {:x (- (+ ax aw) pw) :y (- ay ph offset)}
      "right-start"  {:x (+ ax aw offset) :y ay}
      "left-start"   {:x (- ax pw offset) :y ay}
      {:x ax :y (+ ay ah offset)})))

(defn compute-position
  [placement offset anchor-rect panel-size viewport-size margin]
  (let [{vw :width vh :height} viewport-size
        {pw :width ph :height} panel-size
        try-placement
        (fn [p]
          (let [{cx :x cy :y} (candidate-xy p offset anchor-rect panel-size)
                fits-x (fits? cx pw vw margin)
                fits-y (fits? cy ph vh margin)
                ;; clamp within viewport
                x (-> cx (max margin) (min (- vw pw margin)))
                y (-> cy (max margin) (min (- vh ph margin)))
                max-h (- vh y margin)]
            {:x x :y y
             :final-placement p
             :fits (and fits-x fits-y)
             :max-height (max 40 max-h)}))
        primary   (try-placement placement)
        flipped   (try-placement (opposite-placement placement))]
    (if (or (:fits primary) (not (:fits flipped)))
      primary
      flipped)))
