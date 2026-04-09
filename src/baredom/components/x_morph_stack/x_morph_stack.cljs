(ns baredom.components.x-morph-stack.x-morph-stack
  (:require
   [goog.object :as gobj]
   [baredom.components.x-morph-stack.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs          "__xMorphStackRefs")
(def ^:private k-model         "__xMorphStackModel")
(def ^:private k-handlers      "__xMorphStackHandlers")
(def ^:private k-current-state "__xMorphStackCurrent")
(def ^:private k-token         "__xMorphStackToken")
(def ^:private k-raf           "__xMorphStackRaf")
(def ^:private k-entries       "__xMorphStackEntries")
(def ^:private k-last-time     "__xMorphStackLast")
(def ^:private k-filter-id     "__xMorphStackFilterId")
(def ^:private k-from          "__xMorphStackFrom")
(def ^:private k-to            "__xMorphStackTo")

(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; Module-level monotonic counter for unique SVG filter ids (not component state).
(def ^:private uid-counter #js [0])
(defn- next-uid []
  (let [n (aget uid-counter 0)]
    (aset uid-counter 0 (inc n))
    n))

;; ── Styles ──────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-morph-stack-spring-stiffness:170;"
   "--x-morph-stack-spring-damping:26;"
   "--x-morph-stack-spring-mass:1;"
   "--x-morph-stack-fade-ms:160ms;"
   "--x-morph-stack-goo-blur:10;"
   "--x-morph-stack-goo-threshold:18;"
   "--x-morph-stack-bg:transparent;}"

   "[part=viewport]{"
   "position:relative;"
   "display:block;"
   "background:var(--x-morph-stack-bg);}"

   ":host([disabled]){opacity:0.7;}"))

;; ── DOM init ────────────────────────────────────────────────────────────────
;; The ghost-layer and goo SVG live in LIGHT DOM (appended to document.body).
;; They MUST be in light DOM so author CSS classes on cloned ghosts apply —
;; shadow DOM encapsulation would otherwise strip all class-based styling.
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (.createElement js/document "style")
        viewport (.createElement js/document "div")
        slot-el  (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute viewport "part" "viewport")
    (.setAttribute slot-el "name" model/slot-state)
    (.appendChild viewport slot-el)

    (.appendChild root style)
    (.appendChild root viewport)

    (gobj/set el k-refs
              {:root        root
               :viewport    viewport
               :slot        slot-el}))
  nil)

(defn- ensure-light-layer!
  "Lazily creates the per-element ghost-layer and goo SVG in document.body
  (light DOM). Returns a JS object with :layer and :svg refs.
  Stored in the same k-refs map used for shadow refs."
  [^js el]
  (let [refs (gobj/get el k-refs)
        existing-layer (when refs (:ghost-layer refs))]
    (if (and existing-layer (.-isConnected existing-layer))
      refs
      (let [layer (.createElement js/document "div")
            svg   (.createElementNS js/document svg-ns "svg")]
        ;; Inline-style the layer (no style sheet because it's in light DOM).
        (set! (.. layer -style -position)      "fixed")
        (set! (.. layer -style -top)           "0")
        (set! (.. layer -style -left)          "0")
        (set! (.. layer -style -width)         "100vw")
        (set! (.. layer -style -height)        "100vh")
        (set! (.. layer -style -pointerEvents) "none")
        (set! (.. layer -style -overflow)      "visible")
        (set! (.. layer -style -zIndex)        "2147483646")
        (.setAttribute layer "aria-hidden" "true")
        (.setAttribute layer "data-x-morph-stack-ghost-layer" "")
        ;; Hidden SVG for goo filter definitions.
        (set! (.. svg -style -position) "absolute")
        (set! (.. svg -style -width)    "0")
        (set! (.. svg -style -height)   "0")
        (set! (.. svg -style -overflow) "hidden")
        (.setAttribute svg "aria-hidden" "true")
        (.appendChild layer svg)
        (.appendChild (.-body js/document) layer)
        (let [new-refs (assoc refs :ghost-layer layer :svg svg)]
          (gobj/set el k-refs new-refs)
          new-refs)))))

(defn- destroy-light-layer! [^js el]
  (let [refs (gobj/get el k-refs)
        ^js layer (when refs (:ghost-layer refs))]
    (when (and layer (.-parentNode layer))
      (.removeChild (.-parentNode layer) layer))
    (when refs
      (gobj/set el k-refs (-> refs (dissoc :ghost-layer) (dissoc :svg)))))
  (gobj/set el k-filter-id nil)
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:active-state-raw  (.getAttribute el model/attr-active-state)
    :active-index-raw  (.getAttribute el model/attr-active-index)
    :stiffness-raw     (.getAttribute el model/attr-stiffness)
    :damping-raw       (.getAttribute el model/attr-damping)
    :mass-raw          (.getAttribute el model/attr-mass)
    :goo-present?      (.hasAttribute el model/attr-goo)
    :disabled-present? (.hasAttribute el model/attr-disabled)}))

;; ── State root collection ───────────────────────────────────────────────────
(defn- collect-state-roots
  "Returns a JS array of slotted elements carrying data-state."
  [^js el]
  (let [{:keys [^js slot]} (ensure-refs! el)
        assigned (.assignedElements slot)
        out #js []]
    (dotimes [i (.-length assigned)]
      (let [^js node (aget assigned i)]
        (when (and (= 1 (.-nodeType node))
                   (.hasAttribute node model/attr-data-state))
          (.push out node))))
    out))

(defn- state-names [^js el]
  (let [roots (collect-state-roots el)
        out #js []]
    (dotimes [i (.-length roots)]
      (.push out (.getAttribute (aget roots i) model/attr-data-state)))
    out))

(defn- root-for-name [^js el ^String name]
  (let [roots (collect-state-roots el)
        len (.-length roots)]
    (loop [i 0]
      (when (< i len)
        (let [^js r (aget roots i)]
          (if (= name (.getAttribute r model/attr-data-state))
            r
            (recur (inc i))))))))

;; ── Active visibility ───────────────────────────────────────────────────────
(defn- apply-active-display!
  "Toggles display on each state root: active gets '', others 'none'.
  Mirrors data-active-state on the host. Does NOT animate."
  [^js el ^String active-name]
  (let [roots (collect-state-roots el)]
    (dotimes [i (.-length roots)]
      (let [^js r (aget roots i)
            n (.getAttribute r model/attr-data-state)]
        (if (= n active-name)
          (set! (.. r -style -display) "")
          (set! (.. r -style -display) "none")))))
  (if active-name
    (.setAttribute el model/attr-data-active-state active-name)
    (.removeAttribute el model/attr-data-active-state))
  (gobj/set el k-current-state active-name)
  nil)

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── Morph snapshot ──────────────────────────────────────────────────────────
(defn- snapshot-morph-data
  "Walks [data-morph-id] descendants of root, returns #js {} keyed by id with
  shape #js {:rect ... :br ... :bg ... :color ... :clip ... :text ... :node ...}"
  [^js root]
  (let [out #js {}]
    (when root
      (let [nodes (.querySelectorAll root (str "[" model/attr-data-morph-id "]"))]
        (dotimes [i (.-length nodes)]
          (let [^js node (aget nodes i)
                id (.getAttribute node model/attr-data-morph-id)
                rect (.getBoundingClientRect node)
                cs (.getComputedStyle js/window node)]
            (gobj/set out id
                      #js {:rect  #js {:x (.-left rect)
                                       :y (.-top rect)
                                       :w (.-width rect)
                                       :h (.-height rect)}
                           :br    (.-borderRadius cs)
                           :bg    (.-backgroundColor cs)
                           :color (.-color cs)
                           :clip  (.-clipPath cs)
                           :text  (.-textContent node)
                           :node  node})))))
    out))

;; ── Goo filter ──────────────────────────────────────────────────────────────
(defn- ensure-goo-filter! [^js el]
  (let [{:keys [^js svg]} (ensure-light-layer! el)]
    (or (gobj/get el k-filter-id)
        (let [uid (next-uid)
              filter-id (str "x-morph-stack-goo-" uid)
              defs   (.createElementNS js/document svg-ns "defs")
              filt   (.createElementNS js/document svg-ns "filter")
              blur   (.createElementNS js/document svg-ns "feGaussianBlur")
              matrix (.createElementNS js/document svg-ns "feColorMatrix")]
          (.setAttribute filt "id" filter-id)
          (.setAttribute filt "x" "-20%")
          (.setAttribute filt "y" "-20%")
          (.setAttribute filt "width" "140%")
          (.setAttribute filt "height" "140%")
          (.setAttribute blur "in" "SourceGraphic")
          (.setAttribute blur "stdDeviation" (str model/default-goo-blur))
          (.setAttribute blur "result" "blur")
          (.setAttribute matrix "in" "blur")
          (.setAttribute matrix "type" "matrix")
          (.setAttribute matrix "values" model/default-goo-threshold)
          (.appendChild filt blur)
          (.appendChild filt matrix)
          (.appendChild defs filt)
          (.appendChild svg defs)
          (gobj/set el k-filter-id filter-id)
          filter-id))))

(defn- start-goo! [^js el]
  (let [filter-id (ensure-goo-filter! el)
        {:keys [^js ghost-layer]} (ensure-light-layer! el)]
    (set! (.. ghost-layer -style -filter) (str "url(#" filter-id ")"))
    (set! (.. ghost-layer -style -webkitFilter) (str "url(#" filter-id ")")))
  nil)

(defn- stop-goo! [^js el]
  (let [refs (gobj/get el k-refs)
        ^js layer (when refs (:ghost-layer refs))]
    (when layer
      (set! (.. layer -style -filter) "")
      (set! (.. layer -style -webkitFilter) "")))
  nil)

;; ── Ghost building ──────────────────────────────────────────────────────────
(defn- prep-ghost-base-style!
  "Apply the base inline styles every ghost needs (position, transform origin,
  box-sizing, margin reset). These used to come from a shadow-DOM CSS rule;
  now ghosts live in light DOM so we set them inline."
  [^js ghost]
  (set! (.. ghost -style -position)        "absolute")
  (set! (.. ghost -style -margin)          "0")
  (set! (.. ghost -style -boxSizing)       "border-box")
  (set! (.. ghost -style -transformOrigin) "top left")
  (set! (.. ghost -style -pointerEvents)   "none"))

(defn- abs-place!
  "Position a ghost element absolutely inside ghost-layer at viewport rect.
  ghost-layer is position:fixed at (0,0), so layer-rect is effectively (0,0)
  and viewport coords map directly to layer coords."
  [^js ghost ^js rect ^js layer-rect]
  (let [x (- (.-x rect) (.-left layer-rect))
        y (- (.-y rect) (.-top layer-rect))]
    (set! (.. ghost -style -left)   (str x "px"))
    (set! (.. ghost -style -top)    (str y "px"))
    (set! (.. ghost -style -width)  (str (.-w rect) "px"))
    (set! (.. ghost -style -height) (str (.-h rect) "px"))))

(defn- make-matched-entry
  "Create a ghost-layer clone of the new node, animating from old → new.
  The ghost holds its natural cloned content (with new text in normal flow),
  so its final visual layout matches the real element exactly — no snap on
  finalize. When old and new text differ, callers should pair this with a
  `make-leaving-entry` over the old node to crossfade the prior text out."
  [^js layer ^js layer-rect old-data new-data]
  (let [^js new-node (gobj/get new-data "node")
        ^js new-rect (gobj/get new-data "rect")
        ^js old-rect (gobj/get old-data "rect")
        old-br    (gobj/get old-data "br")
        new-br    (gobj/get new-data "br")
        old-bg    (gobj/get old-data "bg")
        new-bg    (gobj/get new-data "bg")
        old-color (gobj/get old-data "color")
        new-color (gobj/get new-data "color")
        old-text  (gobj/get old-data "text")
        new-text  (gobj/get new-data "text")
        text-diff? (not= old-text new-text)
        ghost     (.cloneNode new-node true)
        nx (.-x new-rect) ny (.-y new-rect)
        nw (.-w new-rect) nh (.-h new-rect)
        ox (.-x old-rect) oy (.-y old-rect)
        ow (.-w old-rect) oh (.-h old-rect)
        sx0 (if (pos? nw) (/ ow nw) 1.0)
        sy0 (if (pos? nh) (/ oh nh) 1.0)
        dx0 (- ox nx)
        dy0 (- oy ny)]
    (prep-ghost-base-style! ghost)
    (abs-place! ghost new-rect layer-rect)
    (.appendChild layer ghost)
    (set! (.. new-node -style -visibility) "hidden")
    (let [update (fn [t]
                   (let [tt (max 0.0 (min 1.0 t))
                         dx (model/lerp dx0 0.0 tt)
                         dy (model/lerp dy0 0.0 tt)
                         sx (model/lerp sx0 1.0 tt)
                         sy (model/lerp sy0 1.0 tt)]
                     (set! (.. ghost -style -transform)
                           (str "translate(" dx "px," dy "px) scale(" sx "," sy ")"))
                     (set! (.. ghost -style -borderRadius)
                           (model/lerp-radius-list old-br new-br tt))
                     (when (and old-bg new-bg)
                       (set! (.. ghost -style -backgroundColor)
                             (model/lerp-color old-bg new-bg tt)))
                     (when (and old-color new-color)
                       (set! (.. ghost -style -color)
                             (model/lerp-color old-color new-color tt)))
                     ;; When text differs, fade the new text in along with the
                     ;; spring; the paired leaving ghost (with the old text)
                     ;; fades out simultaneously. Same `t`, so they crossfade.
                     (when text-diff?
                       (set! (.. ghost -style -opacity)
                             (str (max 0.0 (min 1.0 tt)))))
                     nil))
          finalize (fn []
                     (set! (.. new-node -style -visibility) "")
                     (when (.-parentNode ghost)
                       (.removeChild (.-parentNode ghost) ghost)))]
      #js {:t 0 :v 0 :update update :finalize finalize})))

(defn- make-leaving-entry
  "Clone the old node anchored at its old rect; fade opacity 1→0."
  [^js layer ^js layer-rect old-data]
  (let [^js old-node (gobj/get old-data "node")
        ^js old-rect (gobj/get old-data "rect")
        ghost (.cloneNode old-node true)]
    (prep-ghost-base-style! ghost)
    (abs-place! ghost old-rect layer-rect)
    (.appendChild layer ghost)
    (let [update (fn [t]
                   (set! (.. ghost -style -opacity)
                         (str (max 0.0 (- 1.0 t))))
                   nil)
          finalize (fn []
                     (when (.-parentNode ghost)
                       (.removeChild (.-parentNode ghost) ghost)))]
      #js {:t 0 :v 0 :update update :finalize finalize})))

(defn- make-entering-entry
  "Real new element fades opacity 0→1 (no ghost)."
  [new-data]
  (let [^js new-node (gobj/get new-data "node")]
    (set! (.. new-node -style -opacity) "0")
    (let [update (fn [t]
                   (set! (.. new-node -style -opacity)
                         (str (max 0.0 (min 1.0 t))))
                   nil)
          finalize (fn []
                     (set! (.. new-node -style -opacity) ""))]
      #js {:t 0 :v 0 :update update :finalize finalize})))

(defn- build-entries!
  "Builds the JS array of animation entries for the transition."
  [^js el ^js old-snap ^js new-snap]
  (let [{:keys [^js ghost-layer]} (ensure-light-layer! el)
        layer-rect (.getBoundingClientRect ghost-layer)
        old-keys (.keys js/Object old-snap)
        new-keys (.keys js/Object new-snap)
        diff (model/diff-morph-ids (vec old-keys) (vec new-keys))
        matched  (:matched diff)
        leaving  (:leaving diff)
        entering (:entering diff)
        out #js []]
    (doseq [id matched]
      (let [old-data (gobj/get old-snap id)
            new-data (gobj/get new-snap id)
            old-text (gobj/get old-data "text")
            new-text (gobj/get new-data "text")]
        (.push out (make-matched-entry ghost-layer layer-rect old-data new-data))
        ;; When the text differs, layer a leaving ghost (clone of the OLD node
        ;; at the OLD rect) over it that fades out as the matched ghost fades
        ;; in. Both are driven by the same spring, producing a smooth crossfade
        ;; with no final-position snap (the matched ghost's natural new-text
        ;; layout matches the real revealed element).
        (when (not= old-text new-text)
          (.push out (make-leaving-entry ghost-layer layer-rect old-data)))))
    (doseq [id leaving]
      (.push out (make-leaving-entry ghost-layer layer-rect
                                     (gobj/get old-snap id))))
    (doseq [id entering]
      (.push out (make-entering-entry (gobj/get new-snap id))))
    out))

;; ── Spring tick ─────────────────────────────────────────────────────────────
(declare finalize-transition!)

(defn- tick! [^js el now]
  (let [token (gobj/get el k-token)
        entries ^js (gobj/get el k-entries)
        last-time (gobj/get el k-last-time)
        m (or (gobj/get el k-model) (read-model el))
        mass (:mass m)
        tension (:stiffness m)
        friction (:damping m)
        dt (max 0.0 (min (/ 1.0 30.0) (if last-time (/ (- now last-time) 1000.0) (/ 1.0 60.0))))]
    (gobj/set el k-last-time now)
    (when (and entries (.-length entries))
      (let [len (.-length entries)
            ;; Use a 1-slot JS array for the all-settled flag (no volatile!).
            flag #js [true]]
        (dotimes [i len]
          (let [^js entry (aget entries i)
                t (.-t entry)
                v (.-v entry)
                step (model/spring-step t 1.0 v dt mass tension friction)
                t' (aget step 0)
                v' (aget step 1)]
            (set! (.-t entry) t')
            (set! (.-v entry) v')
            ((.-update entry) t')
            (when-not (model/spring-settled? (- t' 1.0) v')
              (aset flag 0 false))))
        (if (aget flag 0)
          (finalize-transition! el token true)
          (let [raf (.requestAnimationFrame js/window
                                            (fn [now2]
                                              ;; Token may have changed mid-flight; check before continuing.
                                              (when (= token (gobj/get el k-token))
                                                (tick! el now2))))]
            (gobj/set el k-raf raf))))))
  nil)

;; ── Finalize ────────────────────────────────────────────────────────────────
(defn- run-finalizers! [^js entries]
  (when entries
    (dotimes [i (.-length entries)]
      (let [^js entry (aget entries i)
            f (.-finalize entry)]
        (when f (f))))))

(defn- finalize-transition! [^js el token settle?]
  (when (= token (gobj/get el k-token))
    (let [entries ^js (gobj/get el k-entries)]
      (when-let [raf (gobj/get el k-raf)]
        (.cancelAnimationFrame js/window raf)
        (gobj/set el k-raf nil))
      (run-finalizers! entries)
      (gobj/set el k-entries nil)
      (gobj/set el k-last-time nil)
      (stop-goo! el)
      (when settle?
        (let [from (gobj/get el k-from)
              to   (gobj/get el k-to)
              detail (clj->js (model/changed-detail from to))
              ^js ev (js/CustomEvent.
                      model/event-changed
                      #js {:detail detail :bubbles true :composed true :cancelable false})]
          (.dispatchEvent el ev)
          (gobj/set el k-from nil)
          (gobj/set el k-to nil)))))
  nil)

(defn- cancel-current!
  "Stop any in-flight transition without firing the changed event."
  [^js el]
  (when-let [raf (gobj/get el k-raf)]
    (.cancelAnimationFrame js/window raf)
    (gobj/set el k-raf nil))
  (run-finalizers! ^js (gobj/get el k-entries))
  (gobj/set el k-entries nil)
  (gobj/set el k-last-time nil)
  (stop-goo! el)
  nil)

;; ── Transition orchestrator ─────────────────────────────────────────────────
(defn- start-transition!
  "Drive a transition from the currently visible state to `to-name`.
  `reason` is one of \"attribute\" or \"method\".
  Returns true when the transition was started (or instantly applied),
  false when cancelled by event preventDefault."
  [^js el ^String to-name ^String reason]
  (let [from-name (gobj/get el k-current-state)
        m (or (gobj/get el k-model) (read-model el))
        names (state-names el)]
    (cond
      ;; No states or unknown target → no-op.
      (or (zero? (.-length names))
          (nil? to-name)
          (not (some #(= % to-name) names)))
      false

      ;; Already visible → no-op.
      (= from-name to-name)
      true

      :else
      (let [from-root (when from-name (root-for-name el from-name))
            to-root   (root-for-name el to-name)
            detail    (clj->js (model/change-detail from-name to-name reason))
            ^js change-ev (js/CustomEvent.
                           model/event-change
                           #js {:detail detail :bubbles true :composed true :cancelable true})
            ok? (.dispatchEvent el change-ev)]
        (if-not ok?
          false
          (do
            ;; Cancel any in-flight transition first (no `changed` event for it).
            (cancel-current! el)
            (let [reduced? (or (prefers-reduced-motion?) (:disabled? m))]
              (cond
                reduced?
                (do
                  (apply-active-display! el to-name)
                  ;; Fire `changed` next microtask.
                  (js/queueMicrotask
                   (fn []
                     (let [d (clj->js (model/changed-detail from-name to-name))
                           ^js ev (js/CustomEvent.
                                   model/event-changed
                                   #js {:detail d :bubbles true :composed true})]
                       (.dispatchEvent el ev))))
                  true)

                :else
                (let [old-snap (snapshot-morph-data from-root)
                      _        (apply-active-display! el to-name)
                      ;; Force layout
                      _        (.-offsetWidth ^js (:viewport (ensure-refs! el)))
                      new-snap (snapshot-morph-data to-root)
                      entries  (build-entries! el old-snap new-snap)
                      token    (inc (or (gobj/get el k-token) 0))]
                  (gobj/set el k-token token)
                  (gobj/set el k-entries entries)
                  (gobj/set el k-last-time nil)
                  (gobj/set el k-from from-name)
                  (gobj/set el k-to   to-name)
                  (when (:goo? m) (start-goo! el))
                  (if (zero? (.-length entries))
                    ;; Nothing to animate (no morph-ids on either side); finalize immediately.
                    (finalize-transition! el token true)
                    (let [raf (.requestAnimationFrame js/window
                                                      (fn [now]
                                                        (when (= token (gobj/get el k-token))
                                                          (tick! el now))))]
                      (gobj/set el k-raf raf)))
                  true)))))))))

;; ── Public methods ──────────────────────────────────────────────────────────
(defn- go-to! [^js el ^String name reason]
  (start-transition! el name reason))

(defn- next-state! [^js el reason]
  (let [names (state-names el)
        len (.-length names)
        cur (gobj/get el k-current-state)]
    (when (pos? len)
      (let [idx (loop [i 0]
                  (if (>= i len) -1
                      (if (= (aget names i) cur) i (recur (inc i)))))
            nxt (aget names (mod (inc idx) len))]
        (start-transition! el nxt reason)))))

(defn- prev-state! [^js el reason]
  (let [names (state-names el)
        len (.-length names)
        cur (gobj/get el k-current-state)]
    (when (pos? len)
      (let [idx (loop [i 0]
                  (if (>= i len) 0
                      (if (= (aget names i) cur) i (recur (inc i)))))
            prv (aget names (mod (+ (dec idx) len) len))]
        (start-transition! el prv reason)))))

;; ── Update from attributes ──────────────────────────────────────────────────
(defn- update-from-attrs! [^js el]
  (ensure-refs! el)
  (let [new-m (read-model el)]
    (gobj/set el k-model new-m)
    (let [names (state-names el)
          target (model/resolve-active (vec (array-seq names)) new-m)
          current (gobj/get el k-current-state)]
      (cond
        ;; First-time mount: no current state → instantly show target, no events.
        (nil? current)
        (when target (apply-active-display! el target))

        ;; Active changed → animate.
        (and target (not= target current))
        (start-transition! el target "attribute")

        ;; No target (states removed entirely) → clear.
        (and (nil? target) current)
        (do (cancel-current! el)
            (apply-active-display! el nil)))))
  nil)

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slot-change [^js el]
  (let [names (state-names el)
        m (or (gobj/get el k-model) (read-model el))
        target (model/resolve-active (vec (array-seq names)) m)
        current (gobj/get el k-current-state)]
    (cond
      (nil? target)
      (do (cancel-current! el)
          (apply-active-display! el nil))

      ;; Current vanished → fall back without animation.
      (and current (nil? (root-for-name el current)))
      (do (cancel-current! el)
          (apply-active-display! el target))

      ;; First-time population.
      (nil? current)
      (apply-active-display! el target)

      ;; Otherwise just re-apply display in case styles drifted.
      :else
      (apply-active-display! el current)))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [^js slot]} (ensure-refs! el)
        slot-h (fn [_e] (on-slot-change el))]
    (.addEventListener slot "slotchange" slot-h)
    (gobj/set el k-handlers #js {:slot slot-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js slot (:slot refs)
            slot-h (gobj/get hs "slot")]
        (when slot-h (.removeEventListener slot "slotchange" slot-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors & methods ────────────────────────────────────────────
(defn- bool-attr-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr "")
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- number-attr-prop! [^js proto js-name attr]
  (.defineProperty js/Object proto js-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [s (.getAttribute this attr)]
                                          (if (nil? s) nil (js/parseFloat s)))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this attr)
                                          (.setAttribute this attr (str v)))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  ;; Camel-case JS properties mapping to kebab-case attrs.
  (.defineProperty js/Object proto "activeState"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-active-state)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-active-state)
                                          (.setAttribute this model/attr-active-state (str v)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "activeIndex"
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [s (.getAttribute this model/attr-active-index)]
                                          (if (nil? s) nil (js/parseInt s 10)))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-active-index)
                                          (.setAttribute this model/attr-active-index (str (int v))))))
                        :enumerable true :configurable true})

  (number-attr-prop! proto "stiffness" model/attr-stiffness)
  (number-attr-prop! proto "damping"   model/attr-damping)
  (number-attr-prop! proto "mass"      model/attr-mass)

  (bool-attr-prop! proto model/attr-goo)
  (bool-attr-prop! proto model/attr-disabled)

  ;; Methods
  (set! (.-goTo proto)
        (fn [name]
          (this-as ^js this
                   (let [n (when (some? name) (str name))]
                     (start-transition! this n "method")))))

  (set! (.-next proto)
        (fn []
          (this-as ^js this
                   (next-state! this "method"))))

  (set! (.-prev proto)
        (fn []
          (this-as ^js this
                   (prev-state! this "method"))))

  (set! (.-states proto)
        (fn []
          (this-as ^js this
                   (state-names this)))))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (update-from-attrs! this)
                     ;; Initial slot may have already populated before connect — ensure visibility.
                     (when (nil? (gobj/get this k-current-state))
                       (on-slot-change this))
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (cancel-current! this)
                     (destroy-light-layer! this)
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public registration ─────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
