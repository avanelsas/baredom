(ns demo.selector
  "Building a CSS selector around server data. Row ids, shape field keys and the field an error
   names all come from the server, and a quote or a backslash in any of them closes the selector's
   string early, so querySelector throws a SyntaxError rather than matching nothing.")

(defn attr=
  "The selector fragment matching elements whose `attr` equals `value`, with the value escaped
   into it."
  [attr value]
  (str "[" attr "=\"" (js/CSS.escape (str value)) "\"]"))
