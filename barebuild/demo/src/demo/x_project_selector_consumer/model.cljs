(ns demo.x-project-selector-consumer.model
  "The pure layer of the project selector — a consumer of the PROJECTS resource that drives
   the TASKS resource. `translate-project-gesture` is the intent it sends to \"tasks\";
   `project-options` projects the projects value into the selector's options.")

(def tag-name "x-project-selector-consumer")

(def observed-attributes #js [])

(def event-schema {})

(def method-api {})

;; The value of the \"All projects\" option and the clear sentinel. A blank project id
;; canonicalizes away, removing tasks.project from the URL — so selecting it unfilters.
(def all-projects-value "")

(defn translate-project-gesture
  "The intent patch that filters tasks by `project-id`. A blank id clears the filter.
   `:navigation` (vs the search field's `:refinement`) pushes history, so Back returns to
   the previously selected project."
  [project-id]
  {:query-patch {:project project-id} :gesture-class :navigation})

(defn project-options
  "The {:id :name} option list for the selector, projected from the projects value. Rows
   carry opaque string keys; only id and name are options — description and the rest are dropped."
  [accepted-response]
  (mapv (fn [p] {:id (get p "id") :name (get p "name")})
        (:value accepted-response)))
