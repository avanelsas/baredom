(ns baredom.components.x-container.x-container
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-container.model :as model]))

;; ── Instance-field keys ────────────────────────────────────────────────────
(def ^:private k-refs  "__xContainerRefs")
(def ^:private k-model "__xContainerModel")

;; ── Refs-object keys ───────────────────────────────────────────────────────
(def ^:private rk-root "root")
(def ^:private rk-base "base")
(def ^:private rk-slot "slot")

;; ── String-literal constants ───────────────────────────────────────────────
(def ^:private attr-part         "part")
(def ^:private attr-aria-label   "aria-label")
(def ^:private attr-data-size    "data-size")
(def ^:private attr-data-padding "data-padding")
(def ^:private attr-data-center  "data-center")
(def ^:private attr-data-fluid   "data-fluid")

(def ^:private part-base "base")
(def ^:private val-true  "true")
(def ^:private val-false "false")

;; ── Helpers ────────────────────────────────────────────────────────────────
(defn- get-default-true-bool [^js el attr-name]
  (not= val-false (du/get-attr el attr-name)))

(defn- read-model [^js el]
  (model/public-state
   {:as      (du/get-attr el model/attr-as)
    :size    (du/get-attr el model/attr-size)
    :padding (du/get-attr el model/attr-padding)
    :center  (get-default-true-bool el model/attr-center)
    :fluid   (du/has-attr? el model/attr-fluid)
    :label   (du/get-attr el model/attr-label)}))

;; ── Styles ─────────────────────────────────────────────────────────────────
(def ^:private style-text
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

;; ── DOM initialisation ─────────────────────────────────────────────────────
(defn- create-base! [tag]
  (let [base (.createElement js/document tag)]
    (.setAttribute base attr-part part-base)
    base))

(defn- init-dom! [^js el initial-tag]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (create-base! initial-tag)
        slot  (.createElement js/document "slot")
        refs  #js {}]
    (set! (.-textContent style) style-text)
    (.appendChild base slot)
    (.appendChild root style)
    (.appendChild root base)
    (gobj/set refs rk-root root)
    (gobj/set refs rk-base base)
    (gobj/set refs rk-slot slot)
    (du/setv! el k-refs refs)
    refs))

;; ── DOM patching ───────────────────────────────────────────────────────────
(defn- swap-base!
  "Replace the [part=base] element with a fresh one of `tag`. Re-parents
  the persistent slot into the new base and updates refs.base. Used when
  the host's `:as` attribute changes the desired root tag."
  [^js refs tag]
  (let [^js root     (gobj/get refs rk-root)
        ^js old-base (gobj/get refs rk-base)
        ^js slot     (gobj/get refs rk-slot)
        ^js new-base (create-base! tag)]
    (.appendChild new-base slot)
    (.replaceChild root new-base old-base)
    (gobj/set refs rk-base new-base)))

(defn- apply-tag!
  "Swap the base element when the cached tag differs from the model's :as."
  [^js refs {:keys [as]}]
  (let [^js base    (gobj/get refs rk-base)
        current-tag (.toLowerCase (.-tagName base))]
    (when (not= current-tag as)
      (swap-base! refs as))))

(defn- apply-base-data! [^js base {:keys [size padding center fluid]}]
  (.setAttribute base attr-data-size    size)
  (.setAttribute base attr-data-padding padding)
  (.setAttribute base attr-data-center  (if center val-true val-false))
  (.setAttribute base attr-data-fluid   (if fluid  val-true val-false)))

(defn- apply-base-aria! [^js base {:keys [label]}]
  (if label
    (.setAttribute base attr-aria-label label)
    (.removeAttribute base attr-aria-label)))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (apply-tag! refs m)
    ;; Re-read base after apply-tag! — it may have been swapped.
    (let [^js base (gobj/get refs rk-base)]
      (apply-base-data! base m)
      (apply-base-aria! base m))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Property accessors ─────────────────────────────────────────────────────
(defn- define-default-true-bool-prop! [^js proto prop attr]
  ;; Tier 2: the absent attribute resolves to `true`, so du/define-bool-prop!
  ;; (which treats absence as `false`) would invert the default. Setting v=true
  ;; removes the attribute; setting v=false writes the literal "false" string.
  (.defineProperty js/Object proto prop
                   #js {:configurable true
                        :enumerable   true
                        :get (fn []
                               (this-as ^js this
                                 (get-default-true-bool this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                 (if (boolean v)
                                   (.removeAttribute this attr)
                                   (.setAttribute this attr val-false))))}))

(defn- install-property-accessors! [^js proto]
  (define-default-true-bool-prop! proto "center" model/attr-center)
  (du/define-bool-prop! proto "fluid" model/attr-fluid))

;; ── Lifecycle ──────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    ;; Initial tag comes from a fresh read so the shadow's [part=base] is
    ;; created with the right element type from the start.
    (init-dom! el (:as (read-model el))))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ─────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
