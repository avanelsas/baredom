(ns demo-app.dom
  "Tiny DOM-construction helpers for the demo app's render functions. App code,
  not library code — it legitimately builds its own markup, so plain
  .createElement / .setAttribute interop is fine here."
  (:require [goog.object :as gobj]))

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

(defn fill-form!
  "Set each named control's `.value` from `obj` (string-coerced; a missing key
  fills \"\"). Shared by the detail and settings read sides — one projection of
  a record into a form, so the two cannot drift."
  [^js form ^js obj fields]
  (doseq [field fields]
    (when-let [^js f (.querySelector form (str "[name='" field "']"))]
      (set! (.-value f) (str (gobj/get obj field))))))

(defn fill-options!
  "Replace `select`'s <option> children from `options` (each {:value :label}),
  optionally prepending a leading empty-value option labelled `blank-label` (the
  board filter's \"All statuses\"). Lets the status taxonomy live once in
  demo-app.view instead of being re-spelled as static <option>s per <x-select>.
  x-select re-syncs on slotchange, so adding options after upgrade is fine."
  [^js select options blank-label]
  (clear! select)
  (when blank-label
    (.appendChild select (doto (text-el! "option" blank-label) (.setAttribute "value" ""))))
  (doseq [{:keys [value label]} options]
    (.appendChild select (doto (text-el! "option" label) (.setAttribute "value" value)))))
