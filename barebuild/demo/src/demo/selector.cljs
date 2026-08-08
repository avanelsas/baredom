(ns demo.selector
  "Building a CSS selector around server data. A quote or a backslash in a server-supplied value
   closes the selector string early and querySelector throws.")

(defn attr=
  "The fragment matching elements whose `attr` equals `value`, escaped."
  [attr value]
  (str "[" attr "=\"" (js/CSS.escape (str value)) "\"]"))
