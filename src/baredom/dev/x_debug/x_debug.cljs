(ns baredom.dev.x-debug.x-debug
  "Dev-only visual debug overlay for BareDOM components.
   Activates via ?baredom-debug URL param or window.BAREDOM_DEBUG = true.
   Injects dashed outlines and tag labels into component shadow roots,
   wraps prototype callbacks for console logging, and provides a floating
   inspection panel."
  (:require
   [goog.object :as gobj]
   [baredom.dev.x-debug.model :as model]
   [baredom.dev.x-debug-registry :as registry]))

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(defn- debug-enabled? []
  (or (some-> (.. js/window -location -search) (.includes "baredom-debug"))
      (true? (gobj/get js/window "BAREDOM_DEBUG"))))

(defn- known-tag? [tag-name]
  (contains? registry/registry tag-name))

;; Set of wrapped prototypes, for clean unwrap on shutdown
(defonce ^:private wrapped-protos (atom #{}))

;; ---------------------------------------------------------------------------
;; HTML escaping
;; ---------------------------------------------------------------------------

(defn- escape-html
  "Escapes HTML metacharacters for safe interpolation into innerHTML."
  [s]
  (-> (str s)
      (.replace (js/RegExp. "&" "g")  "&amp;")
      (.replace (js/RegExp. "<" "g")  "&lt;")
      (.replace (js/RegExp. ">" "g")  "&gt;")
      (.replace (js/RegExp. "\"" "g") "&quot;")
      (.replace (js/RegExp. "'" "g")  "&#39;")))

;; ---------------------------------------------------------------------------
;; Reading component state (via public API)
;; ---------------------------------------------------------------------------

(defn- read-attributes
  "Returns a vector of [name value] pairs from the element's attributes."
  [^js el]
  (let [attrs (.-attributes el)
        n     (.-length attrs)]
    (loop [i 0, acc (transient [])]
      (if (< i n)
        (let [^js attr (.item attrs i)]
          (recur (inc i) (conj! acc [(.-name attr) (.-value attr)])))
        (persistent! acc)))))

(defn- read-properties
  "Returns a vector of [name value readonly?] triples by calling each public getter."
  [^js el tag-name]
  (when-let [api (get registry/registry tag-name)]
    (let [props (:properties api)]
      (when props
        (reduce-kv
         (fn [acc prop-key prop-meta]
           (let [js-name (name prop-key)]
             (conj acc [js-name (gobj/get el js-name) (boolean (:readonly prop-meta))])))
         []
         props)))))

(defn- bool-attributes
  "Returns a set of attribute names that are boolean type, derived from property-api."
  [tag-name]
  (when-let [api (get registry/registry tag-name)]
    (let [props (:properties api)]
      (when props
        (reduce-kv
         (fn [acc _prop-key prop-meta]
           (if (and (= 'boolean (:type prop-meta))
                    (:reflects-attribute prop-meta))
             (conj acc (:reflects-attribute prop-meta))
             acc))
         #{}
         props)))))

;; ---------------------------------------------------------------------------
;; Floating panel
;; ---------------------------------------------------------------------------

(defn- format-value [v]
  (cond
    (nil? v)       "null"
    (boolean? v)   (str v)
    (string? v)    (if (= v "") "\"\"" (str "\"" (escape-html v) "\""))
    :else          (escape-html (str v))))

(defn- value-class [v]
  (if (nil? v) "val null" "val"))

(defn- render-prop-rows [props]
  (if (seq props)
    (->> props
         (map (fn [[k v readonly?]]
                (str "<div class=\"row\">"
                     "<span class=\"key\">" (escape-html k) "</span>"
                     "<span class=\"" (if readonly? "val readonly" (value-class v)) "\">"
                     (format-value v)
                     "</span>"
                     "</div>")))
         (apply str))
    "<div class=\"empty\">(none)</div>"))

(defn- render-attr-rows
  "Renders attribute rows with editable controls.
   Boolean attrs get a toggle checkbox, others get a text input."
  [attrs bool-attr-set]
  (if (seq attrs)
    (->> attrs
         (map (fn [[attr-name attr-val]]
                (let [safe-name (escape-html attr-name)]
                  (if (contains? bool-attr-set attr-name)
                    ;; Boolean attribute — toggle checkbox
                    (str "<div class=\"row\">"
                         "<span class=\"key\">" safe-name "</span>"
                         "<input type=\"checkbox\" class=\"bool-toggle\""
                         " data-x-debug-attr=\"" safe-name "\""
                         " data-x-debug-type=\"bool\""
                         (when (some? attr-val) " checked")
                         " />"
                         "</div>")
                    ;; String/number attribute — text input
                    (str "<div class=\"row\">"
                         "<span class=\"key\">" safe-name "</span>"
                         "<input type=\"text\" class=\"edit-input\""
                         " data-x-debug-attr=\"" safe-name "\""
                         " data-x-debug-type=\"text\""
                         " value=\"" (escape-html (str attr-val)) "\""
                         " />"
                         "</div>")))))
         (apply str))
    "<div class=\"empty\">(none)</div>"))

(defn- build-panel-html [^js el tag-name]
  (let [attrs     (read-attributes el)
        props     (read-properties el tag-name)
        bool-set  (or (bool-attributes tag-name) #{})]
    (str "<div class=\"panel\">"
         "<div class=\"header\">"
         "<span>" (escape-html tag-name) "</span>"
         "<button class=\"close-btn\" data-x-debug-close>&times;</button>"
         "</div>"
         "<div class=\"section\">"
         "<div class=\"section-title\">Attributes</div>"
         (render-attr-rows attrs bool-set)
         "</div>"
         "<div class=\"section\">"
         "<div class=\"section-title\">Properties</div>"
         (render-prop-rows props)
         "</div>"
         "</div>")))

(defn- position-panel!
  "Positions the panel element next to the target element."
  [^js panel-host ^js target-el]
  (let [rect   (.getBoundingClientRect target-el)
        vw     (.-innerWidth js/window)
        vh     (.-innerHeight js/window)
        right-space (- vw (.-right rect))
        left   (if (> right-space 280)
                 (+ (.-right rect) 8)
                 (max 8 (- (.-left rect) 288)))
        top    (min (.-top rect) (- vh 520))]
    (set! (.. panel-host -style -left) (str (max 8 left) "px"))
    (set! (.. panel-host -style -top)  (str (max 8 top) "px"))))

(defn- create-panel-host []
  (let [^js host (.createElement js/document "div")
        ^js shadow (.attachShadow host #js {:mode "open"})
        ^js style  (.createElement js/document "style")]
    (set! (.-textContent style) model/panel-css)
    (.appendChild shadow style)
    (set! (.. host -style -position) "fixed")
    (set! (.. host -style -zIndex) "2000000")
    (set! (.. host -style -pointerEvents) "none")
    host))

(defn- close-panel!
  "Removes the floating panel if open."
  [^js debug-el]
  (when-let [^js host (gobj/get debug-el model/k-panel-el)]
    ;; Disconnect attribute observer
    (when-let [^js obs (gobj/get host "__xDebugAttrObserver")]
      (.disconnect obs))
    ;; Remove escape handler
    (when-let [handler (gobj/get host "__xDebugEscHandler")]
      (.removeEventListener js/document "keydown" handler))
    ;; Remove from DOM
    (when (.-parentNode host)
      (.removeChild (.-parentNode host) host))
    (gobj/set debug-el model/k-panel-el nil)
    (gobj/set debug-el model/k-panel-target nil)))

(defn- bind-edit-handlers!
  "Binds change/keydown handlers on editable inputs inside the panel shadow."
  [^js shadow ^js target-el ^js debug-el]
  ;; Close button
  (let [^js close-btn (.querySelector shadow "[data-x-debug-close]")]
    (when close-btn
      (.addEventListener close-btn "click"
                         (fn [_e] (close-panel! debug-el)))))
  ;; Boolean toggle checkboxes
  (let [^js toggles (.querySelectorAll shadow "[data-x-debug-type=\"bool\"]")]
    (.forEach toggles
              (fn [^js input]
                (.addEventListener input "change"
                                   (fn [_e]
                                     (let [attr-name (.getAttribute input "data-x-debug-attr")]
                                       (if (.-checked input)
                                         (.setAttribute target-el attr-name "")
                                         (.removeAttribute target-el attr-name))))))))
  ;; Text inputs — commit on Enter, revert on Escape
  (let [^js inputs (.querySelectorAll shadow "[data-x-debug-type=\"text\"]")]
    (.forEach inputs
              (fn [^js input]
                (.addEventListener input "keydown"
                                   (fn [^js e]
                                     (let [attr-name (.getAttribute input "data-x-debug-attr")]
                                       (when (= (.-key e) "Enter")
                                         (.preventDefault e)
                                         (.setAttribute target-el attr-name (.-value input))
                                         (.blur input))
                                       (when (= (.-key e) "Escape")
                                         (.stopPropagation e)
                                         (set! (.-value input) (.getAttribute target-el attr-name))
                                         (.blur input)))))))))

(defn- refresh-panel!
  "Re-renders panel content and re-binds handlers. Skips if an input is focused."
  [^js shadow ^js target-el tag-name ^js debug-el]
  ;; Skip re-render when user is editing
  (let [^js active (.-activeElement shadow)]
    (when-not (and active (or (= "INPUT" (.-tagName active))
                              (= "TEXTAREA" (.-tagName active))))
      (let [^js content (.querySelector shadow ".panel")]
        (when content
          (let [^js parent (.-parentNode content)]
            (set! (.-innerHTML parent) (build-panel-html target-el tag-name))
            (bind-edit-handlers! shadow target-el debug-el)))))))

(defn- show-panel!
  "Shows the floating inspection panel for the given element."
  [^js debug-el ^js target-el]
  (let [tag-name (.. target-el -tagName toLowerCase)]
    ;; Remove existing panel
    (close-panel! debug-el)
    ;; Create new panel
    (let [^js host    (create-panel-host)
          ^js shadow  (.-shadowRoot host)
          ^js content (.createElement js/document "div")]
      (set! (.-innerHTML content) (build-panel-html target-el tag-name))
      (set! (.. content -style -pointerEvents) "auto")
      (.appendChild shadow content)
      (.appendChild (.-body js/document) host)
      (position-panel! host target-el)
      ;; Bind edit handlers on inputs
      (bind-edit-handlers! shadow target-el debug-el)
      ;; Escape key handler (for closing panel — only when no input focused)
      (let [esc-handler (fn [^js e]
                          (when (= (.-key e) "Escape")
                            (let [^js active (.-activeElement shadow)]
                              (when-not (and active (= "INPUT" (.-tagName active)))
                                (close-panel! debug-el)))))]
        (.addEventListener js/document "keydown" esc-handler)
        (gobj/set host "__xDebugEscHandler" esc-handler))
      ;; Store refs for live update and cleanup
      (gobj/set debug-el model/k-panel-el host)
      (gobj/set debug-el model/k-panel-target target-el)
      ;; Set up attribute observer for live updates
      (let [^js attr-observer
            (js/MutationObserver.
             (fn [_mutations]
               (refresh-panel! shadow target-el tag-name debug-el)))]
        (.observe attr-observer target-el #js {:attributes true})
        (gobj/set host "__xDebugAttrObserver" attr-observer)))))

;; ---------------------------------------------------------------------------
;; Prototype wrapping for console logging
;; ---------------------------------------------------------------------------

(defn- wrap-prototype!
  "Wraps lifecycle callbacks on the element's prototype for console logging.
   Only wraps once per prototype (guarded by a flag)."
  [^js el]
  (let [^js proto (js/Object.getPrototypeOf el)
        tag-name  (.. el -tagName toLowerCase)]
    (when (and proto (not (gobj/get proto model/proto-wrapped)))
      ;; Wrap attributeChangedCallback
      (let [original (.-attributeChangedCallback proto)]
        (when original
          (gobj/set proto model/proto-orig-attr-changed original)
          (set! (.-attributeChangedCallback proto)
                (fn [attr-name old-val new-val]
                  (this-as ^js this
                    (js/console.group
                     (str "%c" tag-name "%c " attr-name " changed")
                     "color:#3b82f6;font-weight:bold"
                     "color:inherit")
                    (js/console.log "old:" old-val)
                    (js/console.log "new:" new-val)
                    (let [props (read-properties this tag-name)]
                      (when (seq props)
                        (js/console.log "properties:" (clj->js (into {} props)))))
                    (js/console.groupEnd)
                    (.call original this attr-name old-val new-val))))))
      ;; Wrap connectedCallback
      (let [original (.-connectedCallback proto)]
        (when original
          (gobj/set proto model/proto-orig-connected original)
          (set! (.-connectedCallback proto)
                (fn []
                  (this-as ^js this
                    (js/console.debug (str "[BareDOM] " tag-name " connected") this)
                    (.call original this))))))
      ;; Wrap disconnectedCallback
      (let [original (.-disconnectedCallback proto)]
        (when original
          (gobj/set proto model/proto-orig-disconnected original)
          (set! (.-disconnectedCallback proto)
                (fn []
                  (this-as ^js this
                    (js/console.debug (str "[BareDOM] " tag-name " disconnected") this)
                    (.call original this))))))
      (swap! wrapped-protos conj proto)
      (gobj/set proto model/proto-wrapped true))))

(defn- unwrap-prototype!
  "Restores original lifecycle callbacks on a prototype."
  [^js proto]
  (when (gobj/get proto model/proto-wrapped)
    (when-let [orig (gobj/get proto model/proto-orig-attr-changed)]
      (set! (.-attributeChangedCallback proto) orig))
    (when-let [orig (gobj/get proto model/proto-orig-connected)]
      (set! (.-connectedCallback proto) orig))
    (when-let [orig (gobj/get proto model/proto-orig-disconnected)]
      (set! (.-disconnectedCallback proto) orig))
    (gobj/set proto model/proto-wrapped nil)))

(defn- unwrap-all-prototypes!
  "Restores all wrapped prototypes to their originals."
  []
  (doseq [^js proto @wrapped-protos]
    (unwrap-prototype! proto))
  (reset! wrapped-protos #{}))

;; ---------------------------------------------------------------------------
;; Element instrumentation
;; ---------------------------------------------------------------------------

(defn- instrument-element!
  "Injects debug overlay into a BareDOM element's shadow root."
  [^js debug-el ^js el]
  (when (and (.-shadowRoot el)
             (not (gobj/get el model/k-instrumented)))
    (let [^js sr      (.-shadowRoot el)
          tag-name    (.. el -tagName toLowerCase)
          ;; Inject debug style
          ^js style   (.createElement js/document "style")
          _           (do (set! (.-textContent style) model/overlay-css)
                          (.setAttribute style "data-x-debug" "")
                          (.appendChild sr style))
          ;; Inject tag label
          ^js label   (.createElement js/document "span")
          _           (do (set! (.-textContent label) tag-name)
                          (.setAttribute label "data-x-debug-label" "")
                          (.insertBefore sr label (.-firstChild sr)))
          ;; Click handler on label
          click-fn    (fn [^js e]
                        (.stopPropagation e)
                        (.preventDefault e)
                        (show-panel! debug-el el))]
      (.addEventListener label "click" click-fn)
      ;; Wrap prototype for logging (once per class)
      (wrap-prototype! el)
      ;; Store cleanup function
      (let [cleanup (fn []
                      (.removeEventListener label "click" click-fn)
                      (when (.contains sr style) (.removeChild sr style))
                      (when (.contains sr label) (.removeChild sr label)))]
        (gobj/set el model/k-cleanup-fns cleanup))
      (gobj/set el model/k-instrumented true))))

(defn- uninstrument-element!
  "Removes debug overlay from a BareDOM element."
  [^js el]
  (when (gobj/get el model/k-instrumented)
    (when-let [cleanup (gobj/get el model/k-cleanup-fns)]
      (cleanup))
    (gobj/set el model/k-instrumented nil)
    (gobj/set el model/k-cleanup-fns nil)))

;; ---------------------------------------------------------------------------
;; MutationObserver for element discovery
;; ---------------------------------------------------------------------------

(defn- scan-and-instrument!
  "Finds all BareDOM elements in the document and instruments them."
  [^js debug-el]
  (let [^js els (.querySelectorAll js/document registry/selector)]
    (.forEach els (fn [^js el] (instrument-element! debug-el el)))))

(defn- handle-mutations!
  "MutationObserver callback — instruments newly added BareDOM elements."
  [^js debug-el ^js mutations]
  (doseq [^js mutation mutations]
    ;; Added nodes
    (let [^js added (.-addedNodes mutation)]
      (dotimes [i (.-length added)]
        (let [^js node (.item added i)]
          (when (= (.-nodeType node) 1)
            (let [tag (.. node -tagName toLowerCase)]
              (when (known-tag? tag)
                (instrument-element! debug-el node)))
            ;; Also check descendants
            (when (.-querySelectorAll node)
              (let [^js nested (.querySelectorAll node registry/selector)]
                (.forEach nested
                          (fn [^js el] (instrument-element! debug-el el)))))))))
    ;; Removed nodes
    (let [^js removed (.-removedNodes mutation)]
      (dotimes [i (.-length removed)]
        (let [^js node (.item removed i)]
          (when (= (.-nodeType node) 1)
            (uninstrument-element! node)
            (when (.-querySelectorAll node)
              (let [^js nested (.querySelectorAll node registry/selector)]
                (.forEach nested uninstrument-element!)))))))))

(defn- start-observing!
  "Starts a MutationObserver on document.body to discover BareDOM elements."
  [^js debug-el]
  (let [^js observer (js/MutationObserver.
                      (fn [^js mutations]
                        (handle-mutations! debug-el mutations)))]
    (.observe observer (.-body js/document) #js {:childList true :subtree true})
    (gobj/set debug-el model/k-observer observer)))

(defn- stop-observing!
  "Stops the MutationObserver and uninstruments all elements."
  [^js debug-el]
  (when-let [^js observer (gobj/get debug-el model/k-observer)]
    (.disconnect observer)
    (gobj/set debug-el model/k-observer nil))
  ;; Uninstrument all currently instrumented elements
  (let [^js els (.querySelectorAll js/document registry/selector)]
    (.forEach els uninstrument-element!))
  ;; Restore all wrapped prototypes
  (unwrap-all-prototypes!)
  ;; Close panel if open
  (close-panel! debug-el))

;; ---------------------------------------------------------------------------
;; Element class definition
;; ---------------------------------------------------------------------------

(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
              (when (debug-enabled?)
                (when-not (.-shadowRoot this)
                  (.attachShadow this #js {:mode "open"}))
                (scan-and-instrument! this)
                (start-observing! this)
                (js/console.log
                 "%c[BareDOM Debug]%c Active \u2014 hover components to see labels, click to inspect"
                 "color:#3b82f6;font-weight:bold"
                 "color:inherit")))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
              (stop-observing! this)
              (js/console.log
               "%c[BareDOM Debug]%c Deactivated"
               "color:#3b82f6;font-weight:bold"
               "color:inherit"))))
    klass))

;; ---------------------------------------------------------------------------
;; Registration & auto-activation
;; ---------------------------------------------------------------------------

(defn- activate! []
  (when-not (.querySelector js/document model/tag-name)
    (let [^js el (.createElement js/document model/tag-name)]
      (.appendChild (.-body js/document) el))))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  ;; Auto-create the element when debug is enabled.
  ;; Defer to DOMContentLoaded if body doesn't exist yet (script in <head>).
  (when (debug-enabled?)
    (if (.-body js/document)
      (activate!)
      (.addEventListener js/document "DOMContentLoaded" activate!))))
