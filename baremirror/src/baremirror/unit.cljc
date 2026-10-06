(ns baremirror.unit
  "The pure functions on shells. A shell is a vector of a tag, an optional map of attributes,
   and children that are shells or text.")

(defn pieces
  "The tag, the attributes and the children of `shell`."
  [[tag & more]]
  (if (map? (first more))
    [tag (first more) (next more)]
    [tag nil (seq more)]))
