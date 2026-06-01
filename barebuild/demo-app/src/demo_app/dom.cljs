(ns demo-app.dom
  "Tiny DOM-construction helpers for the demo app's render functions. App code,
  not library code — it legitimately builds its own markup, so plain
  .createElement / .setAttribute interop is fine here.")

(defn el!
  "Create `tag`, apply `attrs` (a map of string name -> value; `true` sets a
  presence attribute, `nil`/`false` skips it, anything else stringifies), and
  append `children` (nils skipped). Returns the element."
  ([tag] (el! tag nil nil))
  ([tag attrs] (el! tag attrs nil))
  ([tag attrs children]
   (let [^js e (.createElement js/document tag)]
     (doseq [[k v] attrs :when (and (some? v) (not (false? v)))]
       (.setAttribute e k (if (true? v) "" (str v))))
     (doseq [c children :when (some? c)] (.appendChild e c))
     e)))

(defn text-el!
  "Create `tag` with `s` as its textContent."
  [tag s]
  (let [^js e (.createElement js/document tag)]
    (set! (.-textContent e) (str s))
    e))

(defn clear! [^js node]
  (set! (.-innerHTML node) "")
  node)

(defn show! [^js node show?]
  (set! (.. node -style -display) (if show? "" "none"))
  node)
