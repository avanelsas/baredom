(ns app.components.x-notification-center.model)

(def tag-name        "x-notification-center")
(def attr-position   "position")
(def attr-max        "max")
(def data-position   "data-position")

(def event-push      "x-notification-center-push")
(def event-dismiss   "x-notification-center-dismiss")
(def event-empty     "x-notification-center-empty")

(def alert-tag       "x-alert")

(def data-notification-id "data-notification-id")

(def observed-attributes #js [attr-position attr-max])

;; CSS custom property names
(def css-width    "--x-notification-center-width")
(def css-gap      "--x-notification-center-gap")
(def css-offset-x "--x-notification-center-offset-x")
(def css-offset-y "--x-notification-center-offset-y")
(def css-z-index  "--x-notification-center-z-index")

(def valid-positions
  #{"top-right" "top-left" "bottom-right" "bottom-left" "top-center" "bottom-center"})

(def default-position "top-right")
(def default-max      5)

(defn parse-position
  "Normalise a position attribute string. Falls back to top-right for unknown values."
  [s]
  (if (and (string? s) (contains? valid-positions (.toLowerCase s)))
    (.toLowerCase s)
    default-position))

(defn parse-max
  "Parse max attribute to a positive integer. Falls back to 5 for nil/invalid/non-positive."
  [s]
  (if (nil? s)
    default-max
    (let [n (js/parseInt (str s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (pos? n))
        n
        default-max))))

(defn bottom-position?
  "Returns true for bottom-* position strings."
  [pos]
  (and (string? pos) (.startsWith pos "bottom")))

(def property-api
  {:position {:type 'string}
   :max      {:type 'number}
   :count    {:type 'number :readonly true}})

(def event-schema
  {event-push    {:detail {:id 'string :count 'number} :cancelable false}
   event-dismiss {:detail {:id 'string :type 'string :reason 'string :text 'string :count 'number} :cancelable false}
   event-empty   {:detail {} :cancelable false}})
