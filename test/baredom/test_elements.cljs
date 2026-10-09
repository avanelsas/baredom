(ns baredom.test-elements
  "Elements that a test puts in the document, and their removal.")

(def ^:private mark "data-test-element")

(defn element!
  "A new `tag` element with `attrs`, which it puts in `parent`."
  [^js parent tag attrs]
  (let [el (.createElement js/document tag)]
    (run! (fn [[k v]] (.setAttribute el k v)) (assoc attrs mark ""))
    (.appendChild parent el)
    el))

(defn in-body!
  "A new `tag` element with `attrs`, which it puts in the document."
  [tag attrs]
  (element! (.-body js/document) tag attrs))

(defn in-fieldset!
  "A new `tag` element with `attrs` in a new fieldset in the document. Returns both."
  [tag attrs]
  (let [fieldset (in-body! "fieldset" {})]
    [fieldset (element! fieldset tag attrs)]))

(defn remove-all!
  "Removes every element that this namespace made."
  []
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str "[" mark "]"))))
