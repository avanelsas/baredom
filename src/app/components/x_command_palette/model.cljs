(ns app.components.x-command-palette.model
  (:require [clojure.string :as str]))

(def tag-name "x-command-palette")

(def attr-open "open")
(def attr-modal "modal")
(def attr-dismissible "dismissible")
(def attr-disabled "disabled")
(def attr-no-scrim "no-scrim")
(def attr-close-on-scrim "close-on-scrim")
(def attr-close-on-escape "close-on-escape")
(def attr-label "label")
(def attr-placeholder "placeholder")
(def attr-empty-text "empty-text")
(def attr-portal "portal")

(def event-open-request "x-command-palette-open-request")
(def event-open "x-command-palette-open")
(def event-close-request "x-command-palette-close-request")
(def event-close "x-command-palette-close")
(def event-select-request "x-command-palette-select-request")
(def event-select "x-command-palette-select")
(def event-query-change "x-command-palette-query-change")

(def default-placeholder "Search\u2026")
(def default-empty-text "No results")

(def observed-attributes
  #js ["open" "modal" "dismissible" "disabled" "no-scrim" "close-on-scrim"
       "close-on-escape" "label" "placeholder" "empty-text" "portal"])

(def property-api
  {:items {:type 'array
           :description "Array of command items with shape {id, label, keywords?, group?, value?, icon?, disabled?}"}
   :open {:type 'boolean
          :reflects-attribute attr-open}
   :disabled {:type 'boolean
              :reflects-attribute attr-disabled}})

(def event-schema
  {event-open-request {:cancelable true :detail {}}
   event-open {:cancelable false :detail {}}
   event-close-request {:cancelable true :detail {}}
   event-close {:cancelable false :detail {}}
   event-select-request {:cancelable true :detail {:item 'object}}
   event-select {:cancelable false :detail {:item 'object}}
   event-query-change {:cancelable false :detail {:query 'string}}})

(defn parse-bool-attr
  "Returns true if attr string is non-nil and not \"false\"."
  [s]
  (and (some? s) (not= s "false")))

(defn parse-bool-default-true
  "nil → true, \"false\" → false, anything else → true."
  [s]
  (if (nil? s)
    true
    (not= s "false")))

(defn normalize-str
  "Trim and return nil if empty."
  [s]
  (when (string? s)
    (let [t (.trim s)]
      (when (not= t "") t))))

(defn normalize-items
  "Takes a JS array of item objects and returns a ClojureScript vector of
  normalized maps. Each item receives: :id :label :group :value :icon
  :disabled? :search-str."
  [^js items]
  (if (or (nil? items) (not (array? items)))
    []
    (let [len (alength items)]
      (loop [i 0 acc (transient [])]
        (if (= i len)
          (persistent! acc)
          (let [^js raw (aget items i)
                id (str (or (.-id raw) i))
                label (str (or (.-label raw) ""))
                group (normalize-str (.-group raw))
                value (.-value raw)
                icon (.-icon raw)
                disabled? (= true (.-disabled raw))
                keywords (.-keywords raw)
                kw-str (if (array? keywords)
                         (.join keywords " ")
                         (if (string? keywords) keywords ""))
                group-str (or group "")
                value-str (if (string? value) value "")
                search-str (.toLowerCase
                            (str label " " kw-str " " group-str " " value-str))]
            (recur (inc i)
                   (conj! acc {:id id
                                :label label
                                :group group
                                :value value
                                :icon icon
                                :disabled? disabled?
                                :search-str search-str}))))))))

(defn filter-items
  "Filter items by query string. Each space-separated token must appear
  in the item's search-str. Returns {:visible [...] :groups #{...}}."
  [items query]
  (let [q (if (string? query) (.trim query) "")
        tokens (when (not= q "")
                 (keep normalize-str (str/split q #"\s+")))]
    (if (empty? tokens)
      {:visible items
       :groups (into #{} (keep :group items))}
      (let [visible (filterv
                     (fn [item]
                       (every? (fn [tok]
                                 (not= -1 (.indexOf (:search-str item) (.toLowerCase tok))))
                               tokens))
                     items)]
        {:visible visible
         :groups (into #{} (keep :group visible))}))))

(defn- find-next-enabled
  "Walk forward (or backward) from start-idx in visible, wrapping,
  returning the index of the next non-disabled item. Returns nil if all disabled."
  [visible start-idx direction]
  (let [n (count visible)]
    (when (pos? n)
      (loop [i 1]
        (when (<= i n)
          (let [idx (mod (+ start-idx (* i direction)) n)
                item (nth visible idx nil)]
            (if (and item (not (:disabled? item)))
              idx
              (recur (inc i)))))))))

(defn next-active-idx
  "Advance active-idx forward, wrapping, skipping disabled items."
  [visible active-idx]
  (or (find-next-enabled visible (or active-idx -1) 1) 0))

(defn prev-active-idx
  "Advance active-idx backward, wrapping, skipping disabled items."
  [visible active-idx]
  (let [n (count visible)]
    (or (find-next-enabled visible (or active-idx n) -1) 0)))

(defn normalize
  "Canonicalize raw attr strings to a stable model map."
  [{:keys [open-present? modal-raw dismissible-raw disabled-raw
           no-scrim-raw close-on-scrim-raw close-on-escape-raw
           label-raw placeholder-raw empty-text-raw portal-raw]}]
  (let [modal?          (parse-bool-default-true modal-raw)
        dismissible?    (parse-bool-default-true dismissible-raw)
        no-scrim?       (parse-bool-attr no-scrim-raw)
        scrim?          (and modal? (not no-scrim?))
        close-on-scrim? (if (some? close-on-scrim-raw)
                          (parse-bool-attr close-on-scrim-raw)
                          scrim?)
        close-on-escape? (parse-bool-default-true close-on-escape-raw)]
    {:open?            (boolean open-present?)
     :modal?           modal?
     :dismissible?     dismissible?
     :disabled?        (parse-bool-attr disabled-raw)
     :scrim?           scrim?
     :close-on-scrim?  close-on-scrim?
     :close-on-escape? close-on-escape?
     :label            (normalize-str label-raw)
     :placeholder      (or (normalize-str placeholder-raw) default-placeholder)
     :empty-text       (or (normalize-str empty-text-raw) default-empty-text)
     :portal           (normalize-str portal-raw)}))
