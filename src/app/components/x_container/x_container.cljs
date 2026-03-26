(ns app.components.x-container.x-container
  (:require [app.components.x-container.model :as model]))

(def state-key "__xContainerState")

(defn get-prop [obj k] (aget obj k))
(defn set-prop! [obj k v] (aset obj k v))

(defn has-attr? [el attr]
  (.hasAttribute el attr))

(defn get-attr [el attr]
  (.getAttribute el attr))

(defn set-bool-attr! [el attr value]
  (if value
    (.setAttribute el attr "")
    (.removeAttribute el attr)))

(defn get-el-state [el]
  (get-prop el state-key))

(defn set-el-state! [el state]
  (set-prop! el state-key state))

(defn get-default-true-bool
  [el attr-name]
  (not= "false" (.getAttribute el attr-name)))

(defn read-public-state [el]
  (model/public-state
   {:as (get-attr el model/attr-as)
    :size (get-attr el model/attr-size)
    :padding (get-attr el model/attr-padding)
    :center (get-default-true-bool el model/attr-center)
    :fluid (has-attr? el model/attr-fluid)
    :label (get-attr el model/attr-label)}))

(defn define-bool-prop!
  [proto prop attr]
  (.defineProperty
   js/Object
   proto
   prop
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (has-attr? this attr)))
        :set (fn [v]
               (this-as this
                        (set-bool-attr! this attr (boolean v))))}))

(defn define-default-true-bool-prop!
  [proto prop attr]
  (.defineProperty
   js/Object
   proto
   prop
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (get-default-true-bool this attr)))
        :set (fn [v]
               (this-as this
                        (if (boolean v)
                          (.removeAttribute this attr)
                          (.setAttribute this attr "false"))))}))

(def style-text
  "
  :host{
  display:block;
  color-scheme:light dark;

  --x-container-max-width-xs:480px;
  --x-container-max-width-sm:640px;
  --x-container-max-width-md:768px;
  --x-container-max-width-lg:1024px;
  --x-container-max-width-xl:1280px;

  --x-container-padding-sm:0.5rem;
  --x-container-padding-md:1rem;
  --x-container-padding-lg:1.5rem;

  --x-container-padding-block:0;
  --x-container-margin-inline:auto;

  --x-container-bg:transparent;
  --x-container-color:#0f172a;
  --x-container-border:transparent;
  --x-container-radius:0;
  --x-container-shadow:none;
  }

  @media (prefers-color-scheme: dark){
  :host{
  --x-container-bg:transparent;
  --x-container-color:#e5e7eb;
  --x-container-border:transparent;
  --x-container-radius:0;
  --x-container-shadow:none;
  }
  }

  [part='base']{
  display:block;
  box-sizing:border-box;
  width:100%;
  max-width:var(--x-container-max-width-lg);
  padding-inline:var(--x-container-padding-md);
  padding-block:var(--x-container-padding-block);
  margin-inline:0;
  background:var(--x-container-bg);
  color:var(--x-container-color);
  border:1px solid var(--x-container-border);
  border-radius:var(--x-container-radius);
  box-shadow:var(--x-container-shadow);
  }

  [part='base'][data-size='xs']{max-width:var(--x-container-max-width-xs);}
  [part='base'][data-size='sm']{max-width:var(--x-container-max-width-sm);}
  [part='base'][data-size='md']{max-width:var(--x-container-max-width-md);}
  [part='base'][data-size='lg']{max-width:var(--x-container-max-width-lg);}
  [part='base'][data-size='xl']{max-width:var(--x-container-max-width-xl);}
  [part='base'][data-size='full']{max-width:none;width:100%;}

  [part='base'][data-padding='none']{padding-inline:0;}
  [part='base'][data-padding='sm']{padding-inline:var(--x-container-padding-sm);}
  [part='base'][data-padding='md']{padding-inline:var(--x-container-padding-md);}
  [part='base'][data-padding='lg']{padding-inline:var(--x-container-padding-lg);}

  [part='base'][data-center='true']{
  margin-inline:var(--x-container-margin-inline);
  }

  [part='base'][data-center='false']{
  margin-inline:0;
  }

  [part='base'][data-fluid='true']{
  max-width:none;
  width:100%;
  }

  ")

(defn create-shadow! [el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        slot (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute base "part" "base")

    (.appendChild base slot)

    (.appendChild root style)
    (.appendChild root base)

    #js {:root root
         :base base
         :slot slot}))

(defn ensure-root-tag!
  [state tag]
  (let [base (aget state "base")
        current (.-tagName base)]
    (when (not= (.toLowerCase current) tag)
      (let [root (aget state "root")
            slot (aget state "slot")
            new-el (.createElement js/document tag)]
        (.setAttribute new-el "part" "base")
        (.appendChild new-el slot)
        (.replaceChild root new-el base)
        (aset state "base" new-el)))))

(defn render!
  [el state]
  (let [public (read-public-state el)]

    (ensure-root-tag! state (:as public))

    (let [base (aget state "base")]

      (.setAttribute base "data-size" (:size public))
      (.setAttribute base "data-padding" (:padding public))
      (.setAttribute base "data-center" (if (:center public) "true" "false"))
      (.setAttribute base "data-fluid" (if (:fluid public) "true" "false"))

      (if-let [label (:label public)]
        (.setAttribute base "aria-label" label)
        (.removeAttribute base "aria-label")))))

(defn connected! [el]
  (when-not (get-el-state el)
    (let [state (create-shadow! el)]
      (set-el-state! el state)))
  (render! el (get-el-state el)))

(defn attribute-changed! [el _ _ _]
  (when-let [state (get-el-state el)]
    (render! el state)))

(defn make-constructor []
  (let [ctor-ref (atom nil)
        ctor (fn []
               (js/Reflect.construct js/HTMLElement #js [] @ctor-ref))]
    (reset! ctor-ref ctor)
    ctor))

(defn define-element! []
  (when-not (.get js/customElements model/tag-name)
    (let [proto (js/Object.create (.-prototype js/HTMLElement))
          ctor (make-constructor)]

      (js/Object.setPrototypeOf ctor js/HTMLElement)
      (aset proto "constructor" ctor)

      (define-default-true-bool-prop! proto "center" model/attr-center)
      (define-bool-prop! proto "fluid" model/attr-fluid)

      (aset proto "connectedCallback"
            (fn []
              (this-as this
                       (connected! this))))

      (aset proto "attributeChangedCallback"
            (fn [name old new]
              (this-as this
                       (attribute-changed! this name old new))))

      (aset ctor "observedAttributes" model/observed-attributes)
      (aset ctor "prototype" proto)

      (.define js/customElements model/tag-name ctor))))

(defn init! []
  (define-element!))
