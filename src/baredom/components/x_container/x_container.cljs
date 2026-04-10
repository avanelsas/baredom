(ns baredom.components.x-container.x-container
  (:require [baredom.components.x-container.model :as model]))

(def state-key "__xContainerState")

(defn get-prop [^js obj k] (aget obj k))
(defn set-prop! [^js obj k v] (aset obj k v))

(defn has-attr? [^js el attr]
  (.hasAttribute el attr))

(defn get-attr [^js el attr]
  (.getAttribute el attr))

(defn set-bool-attr! [^js el attr value]
  (if value
    (.setAttribute el attr "")
    (.removeAttribute el attr)))

(defn get-el-state [^js el]
  (get-prop el state-key))

(defn set-el-state! [^js el state]
  (set-prop! el state-key state))

(defn get-default-true-bool
  [^js el attr-name]
  (not= "false" (.getAttribute el attr-name)))

(defn read-public-state [^js el]
  (model/public-state
   {:as (get-attr el model/attr-as)
    :size (get-attr el model/attr-size)
    :padding (get-attr el model/attr-padding)
    :center (get-default-true-bool el model/attr-center)
    :fluid (has-attr? el model/attr-fluid)
    :label (get-attr el model/attr-label)}))

(defn define-bool-prop!
  [^js proto prop attr]
  (.defineProperty
   js/Object
   proto
   prop
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as ^js this
                        (has-attr? this attr)))
        :set (fn [v]
               (this-as ^js this
                        (set-bool-attr! this attr (boolean v))))}))

(defn define-default-true-bool-prop!
  [^js proto prop attr]
  (.defineProperty
   js/Object
   proto
   prop
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as ^js this
                        (get-default-true-bool this attr)))
        :set (fn [v]
               (this-as ^js this
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
  --x-container-color:var(--x-color-text, #0f172a);
  --x-container-border:transparent;
  --x-container-radius:0;
  --x-container-shadow:none;
  }

  @media (prefers-color-scheme: dark){
  :host{
  --x-container-bg:transparent;
  --x-container-color:var(--x-color-text, #e5e7eb);
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

(defn create-shadow! [^js el]
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
  [^js state tag]
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
  [^js el ^js state]
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

(defn connected! [^js el]
  (when-not (get-el-state el)
    (let [state (create-shadow! el)]
      (set-el-state! el state)))
  (render! el (get-el-state el)))

(defn attribute-changed! [^js el _ _ _]
  (when-let [state (get-el-state el)]
    (render! el state)))

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] nil))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (define-default-true-bool-prop! (.-prototype klass) "center" model/attr-center)
    (define-bool-prop! (.-prototype klass) "fluid" model/attr-fluid)

    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
