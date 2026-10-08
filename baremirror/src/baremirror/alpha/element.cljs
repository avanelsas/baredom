(ns baremirror.alpha.element
  "An element with no state, made from a template."
  (:require [baremirror.alpha.parts :as parts]
            [baremirror.alpha.template :as template]))

(defn- attribute-value [^js node attr]
  (.getAttribute node attr))

(defn- attribute-item
  "The item of `el`: the values of its attributes `attrs`, by keyword."
  [attrs ^js el]
  (into {} (map (juxt keyword (partial attribute-value el))) attrs))

(defn- shadow-root!
  "The shadow root of `el`. A new one holds a style element with `css` and the node of `fixed`."
  [^js el css fixed]
  (or (.-shadowRoot el)
      (let [style (.createElement js/document "style")]
        (set! (.-textContent style) (str css))
        (doto (.attachShadow el #js {:mode "open"})
          (.append style (parts/make-node! fixed))))))

(defn- show!
  "Brings the shadow tree of `el` to what its attributes say."
  [{:keys [attrs css split]} ^js el]
  (let [^js root (shadow-root! el css (:fixed split))]
    (parts/write-parts! (parts/read-parts (.-lastElementChild root))
                        (template/writes split (attribute-item attrs el)))))

(defn- element-class
  "A custom element class that calls `show-element!` with the element when it connects and
   when one of its attributes `attrs` changes."
  [attrs show-element!]
  (let [^js klass (js* "(class extends HTMLElement {})")
        ^js proto (.-prototype klass)]
    (set! (.-observedAttributes klass) (into-array attrs))
    (set! (.-connectedCallback proto)
          (fn [] (this-as this (show-element! this))))
    (set! (.-attributeChangedCallback proto)
          (fn [_name _old _new] (this-as this (show-element! this))))
    klass))

(defn- element-split
  "The split template of the element `tag`. Throws when the template has an `:on` entry."
  [tag element-template]
  (let [split (template/split tag element-template)]
    (if (empty? (:events split))
      split
      (throw (ex-info "define-element!: the template of an element takes no :on entry."
                      {:tag tag})))))

(defn define-element!
  "Registers `tag` as an element with no state: its shadow tree is `:template`, with each hole
   filled from the attributes named in `:attrs`, by keyword, and styled by `:css`.
   A tag that is already registered is left as it is."
  [tag {:keys [attrs css] element-template :template}]
  (let [options {:attrs attrs :css css :split (element-split tag element-template)}]
    (when-not (.get js/customElements tag)
      (.define js/customElements tag (element-class attrs (partial show! options))))))
