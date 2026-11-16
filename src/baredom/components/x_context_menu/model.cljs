(ns baredom.components.x-context-menu.model)

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

;; Property keys are the JS property names. `zIndex` (camelCase) maps to the
;; kebab-case `z-index` attribute. `offset` is intentionally a string in the
;; JS API — consumers pass values like "12" or "1rem".
(def property-api
  {:open      {:type 'boolean :reflects-attribute attr-open}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :placement {:type 'string  :reflects-attribute attr-placement :enum valid-placements}
   :offset    {:type 'string  :reflects-attribute attr-offset}
   :zIndex    {:type 'string  :reflects-attribute attr-z-index}})

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
  {:open?     (boolean open-present?)
   :disabled? (boolean disabled-present?)
   :placement (parse-placement placement-raw)
   :offset    (parse-int-pos offset-raw default-offset)
   :z-index   (parse-int-pos z-index-raw default-z-index)})

(def event-schema
  {event-open-request  {:cancelable true
                        :requests   {attr-open true}
                        :detail     {:reason 'string}}
   event-open          {:cancelable false :detail {:reason 'string}}
   event-close-request {:cancelable true
                        :requests   {attr-open false}
                        :detail     {:reason 'string}}
   event-close         {:cancelable false :detail {:reason 'string}}
   event-select        {:cancelable false :detail {:item 'object}}})

(def method-api
  {:openAt         {:args [{:name "x" :type 'number} {:name "y" :type 'number}]
                    :returns 'void}
   :toggleAt       {:args [{:name "x" :type 'number} {:name "y" :type 'number}]
                    :returns 'void}
   :openForElement {:args [{:name "anchor" :type 'object}]
                    :returns 'void}
   :close          {:args [] :returns 'void}})
