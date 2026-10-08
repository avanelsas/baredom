(ns baremirror.alpha.test-stage
  "What the tests of the document share: a stage in the page and elements to put on it.")

(def ^:private stage-attr "data-baremirror-test")

(defn- detach! [^js node]
  (.remove node))

(defn remove-stages! []
  (run! detach! (array-seq (.querySelectorAll js/document (str "[" stage-attr "]")))))

(defn element! [tag]
  (.createElement js/document tag))

(defn stage! []
  (let [stage (doto (element! "div") (.setAttribute stage-attr ""))]
    (.append (.-body js/document) stage)
    stage))

(defn element-with!
  "An element of `tag` with the attributes `attrs`."
  [tag attrs]
  (let [el (element! tag)]
    (run! (fn [[k v]] (.setAttribute el k v)) attrs)
    el))

(defn records-of!
  "The mutation records of `el` and its descendants that `f` causes."
  [^js el f]
  (let [observer (js/MutationObserver. identity)]
    (.observe observer el #js {:attributes true :characterData true :childList true :subtree true})
    (f)
    (let [records (vec (array-seq (.takeRecords observer)))]
      (.disconnect observer)
      records)))

(defn record-type [^js record]
  (.-type record))
