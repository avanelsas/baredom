(ns demo.x-task-form-consumer.model)

(def tag-name "x-task-form-consumer")

(def ^:private status-field-key "status")

(defn row-by-id
  "The accepted row `id` names, or nil. Compared as strings, the id arriving from the DOM."
  [accepted id]
  (let [id-key (get-in accepted [:shape :id-key])]
    (first (filter #(= (str (get % id-key)) id) (:value accepted)))))

(defn status-choices
  "What the status select offers: the field's :options, or its :enum with each value as its own
   label."
  [accepted]
  (let [field (first (filter #(= status-field-key (:key %))
                             (get-in accepted [:shape :fields])))]
    (or (:options field)
        (mapv (fn [v] {:value v :label v}) (:enum field)))))
