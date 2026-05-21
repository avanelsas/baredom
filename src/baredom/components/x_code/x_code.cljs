(ns baredom.components.x-code.x-code
  "DOM + lifecycle layer for x-code — a code-display component.

   The component is stateless: DOM = f(attributes, properties). Source code is
   read from the element's light-DOM textContent (or the `code` property /
   attribute), tokenized by the pure model layer, and rendered escaped into
   the shadow `<code>`. A MutationObserver re-renders when the light-DOM text
   changes. The expand/collapse state lives in the observed `expanded`
   attribute, so it too is a value, never a mutable field.

   Instance fields hold only the refs map, the cached model, the
   MutationObserver, and the `code` property override."
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-code.model :as model]
   [baredom.components.x-copy.x-copy :as x-copy]
   [baredom.components.x-copy.model :as copy-model]))

;; ── Instance-field keys (host storage via du/setv! / du/getv) ────────────────
(def ^:private k-refs     "__xCodeRefs")
(def ^:private k-model    "__xCodeModel")
(def ^:private k-observer "__xCodeObserver")
(def ^:private k-code-val "__xCodeCodeValue")

;; ── Refs-object keys (set on the JS refs map) ────────────────────────────────
(def ^:private rk-filename  "filename")
(def ^:private rk-lang      "lang")
(def ^:private rk-cwrap     "cwrap")
(def ^:private rk-copy      "copy")
(def ^:private rk-scroll    "scroll")
(def ^:private rk-code      "code")
(def ^:private rk-expander  "expander")

;; ── String-literal constants ─────────────────────────────────────────────────
(def ^:private attr-part          "part")
(def ^:private attr-type          "type")
(def ^:private attr-role          "role")
(def ^:private attr-tabindex      "tabindex")
(def ^:private attr-aria-hidden   "aria-hidden")
(def ^:private attr-aria-label    "aria-label")
(def ^:private attr-aria-expanded "aria-expanded")
(def ^:private attr-data-header      "data-header")
(def ^:private attr-data-collapsed   "data-collapsed")
(def ^:private attr-data-collapsible "data-collapsible")

(def ^:private part-container    "container")
(def ^:private part-header       "header")
(def ^:private part-filename     "filename")
(def ^:private part-language     "language")
(def ^:private part-copy-wrap    "copy-wrap")
(def ^:private part-copy         "copy")
(def ^:private part-body         "body")
(def ^:private part-scroll       "scroll")
(def ^:private part-pre          "pre")
(def ^:private part-code         "code")
(def ^:private part-line         "line")
(def ^:private part-gutter       "gutter")
(def ^:private part-line-content "line-content")
(def ^:private part-fade         "fade")
(def ^:private part-expander     "expander")

(def ^:private css-var-max-lines     "--x-code-max-lines")
(def ^:private css-var-gutter-digits "--x-code-gutter-digits")

(def ^:private ev-click "click")

(def ^:private copy-label "Copy")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-code-bg:var(--x-color-surface,#f6f8fa);"
   "--x-code-fg:var(--x-color-text,#1f2328);"
   "--x-code-border:var(--x-color-border,#d0d7de);"
   "--x-code-muted:var(--x-color-text-muted,#656d76);"
   "--x-code-radius:var(--x-radius-md,8px);"
   "--x-code-font:var(--x-font-family-mono,ui-monospace,SFMono-Regular,Menlo,Consolas,monospace);"
   "--x-code-font-size:0.8125rem;"
   "--x-code-line-height:1.6;"
   "--x-code-tab-size:2;"
   "--x-code-accent:var(--x-color-primary,#0969da);"
   "--x-code-token-comment:#6a737d;"
   "--x-code-token-string:#0a7d33;"
   "--x-code-token-template:#0a7d33;"
   "--x-code-token-keyword:#cf222e;"
   "--x-code-token-number:#0550ae;"
   "--x-code-token-punct:#57606a;"
   "--x-code-token-key:#0550ae;"
   "--x-code-token-atrule:#cf222e;"
   "--x-code-token-property:#0550ae;"
   "--x-code-token-tag:#116329;"
   "--x-code-token-attr-value:#0a3069;"
   "--x-code-token-entity:#0550ae;"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-code-bg:var(--x-color-surface,#161b22);"
   "--x-code-fg:var(--x-color-text,#e6edf3);"
   "--x-code-border:var(--x-color-border,#30363d);"
   "--x-code-muted:var(--x-color-text-muted,#8b949e);"
   "--x-code-token-comment:#8b949e;"
   "--x-code-token-string:#a5d6ff;"
   "--x-code-token-template:#a5d6ff;"
   "--x-code-token-keyword:#ff7b72;"
   "--x-code-token-number:#79c0ff;"
   "--x-code-token-punct:#c9d1d9;"
   "--x-code-token-key:#79c0ff;"
   "--x-code-token-atrule:#ff7b72;"
   "--x-code-token-property:#79c0ff;"
   "--x-code-token-tag:#7ee787;"
   "--x-code-token-attr-value:#a5d6ff;"
   "--x-code-token-entity:#79c0ff;"
   "}}"

   "[part=container]{"
   "display:flex;"
   "flex-direction:column;"
   "max-inline-size:100%;"
   "box-sizing:border-box;"
   "border:1px solid var(--x-code-border);"
   "border-radius:var(--x-code-radius);"
   "overflow:hidden;"
   "background:var(--x-code-bg);"
   "color:var(--x-code-fg);"
   "font-family:var(--x-code-font);"
   "font-size:var(--x-code-font-size);"
   "}"

   ;; Header — hidden unless a filename or copy button is present.
   "[part=header]{"
   "display:none;"
   "align-items:center;"
   "gap:0.5rem;"
   "box-sizing:border-box;"
   "padding:0.4rem 0.75rem;"
   "border-bottom:1px solid var(--x-code-border);"
   "font-size:0.75rem;"
   "}"
   ":host([data-header]) [part=header]{display:flex;}"
   "[part=filename]{"
   "font-weight:600;"
   "white-space:nowrap;"
   "overflow:hidden;"
   "text-overflow:ellipsis;"
   "}"
   "[part=language]{"
   "color:var(--x-code-muted);"
   "text-transform:uppercase;"
   "letter-spacing:0.06em;"
   "}"
   "[part=copy-wrap]{margin-inline-start:auto;}"
   "[part=copy]::part(trigger){"
   "font:inherit;"
   "font-size:0.7rem;"
   "padding:0.2rem 0.5rem;"
   "border-radius:var(--x-radius-sm,4px);"
   "border:1px solid var(--x-code-border);"
   "background:var(--x-code-bg);"
   "color:var(--x-code-muted);"
   "cursor:pointer;"
   "}"
   "[part=copy]::part(trigger):hover{color:var(--x-code-fg);}"

   ;; Body — relative so the collapse fade can overlay the scroll area.
   "[part=body]{position:relative;}"
   "[part=scroll]{overflow:auto;}"
   "[part=scroll]:focus-visible{"
   "outline:2px solid var(--x-code-accent);"
   "outline-offset:-2px;"
   "}"
   "[part=pre]{margin:0;}"
   "[part=code]{"
   "display:inline-block;"
   "min-width:100%;"
   "box-sizing:border-box;"
   "padding-block:0.75rem;"
   "line-height:var(--x-code-line-height);"
   "tab-size:var(--x-code-tab-size);"
   "-moz-tab-size:var(--x-code-tab-size);"
   "}"
   "[part=line]{"
   "display:flex;"
   "min-width:100%;"
   "width:max-content;"
   "min-height:calc(1em * var(--x-code-line-height));"
   "}"
   ;; Width is derived from the line count's digit count (set as
   ;; --x-code-gutter-digits) so every line's gutter is identical — a
   ;; fixed min-width would leave 100+ line files with a ragged left edge.
   "[part=gutter]{"
   "position:sticky;"
   "left:0;"
   "flex:0 0 auto;"
   "box-sizing:border-box;"
   "width:calc(var(--x-code-gutter-digits,2) * 1ch + 1.5em);"
   "padding:0 0.75em;"
   "text-align:right;"
   "color:var(--x-code-muted);"
   "background:var(--x-code-bg);"
   "user-select:none;"
   "-webkit-user-select:none;"
   "}"
   ":host(:not([line-numbers])) [part=gutter]{display:none;}"
   "[part=line-content]{"
   "flex:0 0 auto;"
   "white-space:pre;"
   "padding-right:0.75em;"
   "}"
   ":host(:not([line-numbers])) [part=line-content]{padding-left:0.75em;}"

   ;; Soft-wrap — long lines fold instead of scrolling horizontally.
   ":host([wrap]) [part=scroll]{overflow-x:hidden;}"
   ":host([wrap]) [part=code]{display:block;}"
   ":host([wrap]) [part=line]{width:auto;}"
   ":host([wrap]) [part=line-content]{"
   "flex:1 1 auto;"
   "min-width:0;"
   "white-space:pre-wrap;"
   "overflow-wrap:anywhere;"
   "}"

   ;; Token colours.
   ".tok-comment{color:var(--x-code-token-comment);font-style:italic;}"
   ".tok-string{color:var(--x-code-token-string);}"
   ".tok-template{color:var(--x-code-token-template);}"
   ".tok-keyword{color:var(--x-code-token-keyword);}"
   ".tok-number{color:var(--x-code-token-number);}"
   ".tok-punct{color:var(--x-code-token-punct);}"
   ".tok-key{color:var(--x-code-token-key);}"
   ".tok-atrule{color:var(--x-code-token-atrule);}"
   ".tok-property{color:var(--x-code-token-property);}"
   ".tok-tag{color:var(--x-code-token-tag);}"
   ".tok-attr-value{color:var(--x-code-token-attr-value);}"
   ".tok-entity{color:var(--x-code-token-entity);}"

   ;; Collapse — capped height with a fade and a real <button> expander.
   ":host([data-collapsed]) [part=scroll]{"
   "max-height:calc(var(--x-code-font-size) * var(--x-code-line-height) "
   "* var(--x-code-max-lines,10) + 1.5rem);"
   "overflow-y:hidden;"
   "}"
   "[part=fade]{"
   "display:none;"
   "position:absolute;"
   "left:0;right:0;bottom:0;"
   "height:3.5em;"
   "pointer-events:none;"
   "background:linear-gradient(to bottom,transparent,var(--x-code-bg));"
   "}"
   ":host([data-collapsed]) [part=fade]{display:block;}"
   "[part=expander]{"
   "display:none;"
   "width:100%;"
   "box-sizing:border-box;"
   "border:0;"
   "border-top:1px solid var(--x-code-border);"
   "padding:0.45rem;"
   "background:var(--x-code-bg);"
   "color:var(--x-code-accent);"
   "font:inherit;"
   "font-size:0.75rem;"
   "cursor:pointer;"
   "text-align:center;"
   "transition:background 120ms ease;"
   "}"
   ":host([data-collapsible]) [part=expander]{display:block;}"
   "[part=expander]:hover{text-decoration:underline;}"
   "[part=expander]:focus-visible{"
   "outline:2px solid var(--x-code-accent);"
   "outline-offset:-2px;"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=expander]{transition:none;}"
   "}"))

;; ── Shadow DOM builders (shadow-builders named pattern) ──────────────────────
(defn- make-header! []
  (let [header   (.createElement js/document "div")
        filename (.createElement js/document "span")
        lang     (.createElement js/document "span")
        cwrap    (.createElement js/document "span")
        copy     (.createElement js/document copy-model/tag-name)]
    (du/set-attr! header   attr-part part-header)
    (du/set-attr! filename attr-part part-filename)
    (du/set-attr! lang     attr-part part-language)
    (du/set-attr! cwrap    attr-part part-copy-wrap)
    (du/set-attr! copy     attr-part part-copy)
    ;; The composed x-copy supplies the copy button (its `trigger` part);
    ;; its tooltip is disabled so it cannot be clipped by the container's
    ;; overflow — copy feedback is the `x-code-copy` event instead.
    (du/set-attr! copy copy-model/attr-show-tooltip "false")
    (.appendChild copy (.createTextNode js/document copy-label))
    (.appendChild cwrap copy)
    (.appendChild header filename)
    (.appendChild header lang)
    (.appendChild header cwrap)
    {:header header :filename filename :lang lang :cwrap cwrap :copy copy}))

(defn- make-body! []
  (let [body   (.createElement js/document "div")
        scroll (.createElement js/document "div")
        pre    (.createElement js/document "pre")
        code   (.createElement js/document "code")
        fade   (.createElement js/document "div")]
    (du/set-attr! body   attr-part part-body)
    (du/set-attr! scroll attr-part part-scroll)
    (du/set-attr! scroll attr-role "region")
    (du/set-attr! scroll attr-tabindex "0")
    (du/set-attr! pre    attr-part part-pre)
    (du/set-attr! code   attr-part part-code)
    (du/set-attr! fade   attr-part part-fade)
    (du/set-attr! fade   attr-aria-hidden "true")
    (.appendChild pre code)
    (.appendChild scroll pre)
    (.appendChild body scroll)
    (.appendChild body fade)
    {:body body :scroll scroll :pre pre :code code :fade fade}))

(defn- make-expander! []
  (let [btn (.createElement js/document "button")]
    (du/set-attr! btn attr-part part-expander)
    (du/set-attr! btn attr-type "button")
    btn))

(defn- make-shadow! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        header    (make-header!)
        body      (make-body!)
        expander  (make-expander!)]
    (set! (.-textContent style) style-text)
    (du/set-attr! container attr-part part-container)
    (.appendChild container (:header header))
    (.appendChild container (:body body))
    (.appendChild container expander)
    (.appendChild root style)
    (.appendChild root container)
    (du/setv! el k-refs
              #js {:filename (:filename header)
                   :lang     (:lang header)
                   :cwrap    (:cwrap header)
                   :copy     (:copy header)
                   :scroll   (:scroll body)
                   :code     (:code body)
                   :expander expander})))

;; ── Code resolution ──────────────────────────────────────────────────────────
(defn- resolve-code
  "Resolve the raw code string. The `code` property override wins, then the
   `code` attribute, then the element's light-DOM textContent."
  [^js el]
  (let [cv (du/getv el k-code-val)]
    (cond
      (string? cv)                      cv
      (du/has-attr? el model/attr-code) (or (du/get-attr el model/attr-code) "")
      :else                             (or (.-textContent el) ""))))

;; ── Attribute reader ─────────────────────────────────────────────────────────
(defn- read-model
  "Build the view-model from the element's attributes and code source.

   Tokenizing is the expensive step, so when the resolved code and language
   match the cached model the previous :lines are reused — an attribute-only
   change (wrap, line-numbers, expanded, max-lines, filename, show-copy)
   never re-runs the tokenizer. This is a pure read: it only inspects el and
   the cached model."
  [^js el]
  (let [raw-code (resolve-code el)
        raw-lang (du/get-attr el model/attr-language)
        old      (du/getv el k-model)
        reuse?   (and old
                      (= (model/prepare-code raw-code)   (:code old))
                      (= (model/parse-language raw-lang) (:language old)))]
    (model/normalize
     {:code-raw      raw-code
      :language-raw  raw-lang
      :lines         (when reuse? (:lines old))
      :filename-raw  (du/get-attr el model/attr-filename)
      :show-copy?    (du/has-attr? el model/attr-show-copy)
      :line-numbers? (du/has-attr? el model/attr-line-numbers)
      :wrap?         (du/has-attr? el model/attr-wrap)
      :max-lines-raw (du/get-attr el model/attr-max-lines)
      :expanded?     (du/has-attr? el model/attr-expanded)})))

;; ── Code-body rendering ──────────────────────────────────────────────────────
(defn- token-node
  "Build the DOM node for one [type text] token. :plain tokens become bare
   text nodes; everything else a classed <span>. Either way the text is set
   via textContent, which escapes <, > and & by construction."
  [[ttype text]]
  (if (= :plain ttype)
    (.createTextNode js/document text)
    (let [span (.createElement js/document "span")]
      (set! (.-className span) (str "tok-" (name ttype)))
      (set! (.-textContent span) text)
      span)))

(defn- make-line! [line idx]
  (let [line-el (.createElement js/document "span")
        gutter  (.createElement js/document "span")
        content (.createElement js/document "span")]
    (du/set-attr! line-el attr-part part-line)
    (du/set-attr! gutter  attr-part part-gutter)
    (du/set-attr! gutter  attr-aria-hidden "true")
    (set! (.-textContent gutter) (str (inc idx)))
    (du/set-attr! content attr-part part-line-content)
    (doseq [tok line]
      (when (not= "" (second tok))
        (.appendChild content (token-node tok))))
    (.appendChild line-el gutter)
    (.appendChild line-el content)
    line-el))

(defn- rebuild-code-dom! [^js el m]
  (let [^js refs (du/getv el k-refs)
        ^js code (gobj/get refs rk-code)
        frag     (.createDocumentFragment js/document)]
    (doseq [[idx line] (map-indexed vector (:lines m))]
      (.appendChild frag (make-line! line idx)))
    (.replaceChildren code)
    (.appendChild code frag)))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────────
(defn- apply-host-css-vars! [^js el m]
  (let [^js style (.-style el)]
    (.setProperty style css-var-max-lines (str (:max-lines m)))
    ;; Gutter width tracks the widest line number so all gutters align.
    (.setProperty style css-var-gutter-digits
                  (str (max 2 (count (str (:line-count m))))))))

(defn- apply-header! [^js el m]
  (let [^js refs  (du/getv el k-refs)
        ^js fname (gobj/get refs rk-filename)
        ^js lang  (gobj/get refs rk-lang)
        ^js cwrap (gobj/get refs rk-cwrap)
        ^js copy  (gobj/get refs rk-copy)
        file?     (not= "" (:filename m))
        label?    (and (not file?) (not= "" (:language-label m)))]
    (du/set-bool-attr! el attr-data-header (:header? m))
    (set! (.-textContent fname) (:filename m))
    (set! (.. fname -style -display) (if file? "block" "none"))
    (set! (.-textContent lang) (:language-label m))
    (set! (.. lang -style -display) (if label? "block" "none"))
    (set! (.. cwrap -style -display) (if (:show-copy? m) "inline-flex" "none"))
    (du/set-attr! copy copy-model/attr-text (:code m))))

(defn- apply-code-body!
  "Rebuild the tokenized <code> DOM only when the code or language changed.
   The cached model still holds the previous value here — `apply-model!`
   writes the new cache last."
  [^js el m]
  (let [old (du/getv el k-model)]
    (when (not= (select-keys m   [:code :language])
                (select-keys old [:code :language]))
      (rebuild-code-dom! el m))))

(defn- expander-label [{:keys [line-count max-lines]}]
  (let [hidden (max 0 (- line-count max-lines))]
    (str "Show " hidden " more " (if (= 1 hidden) "line" "lines"))))

(defn- apply-collapse! [^js el m]
  (let [^js refs     (du/getv el k-refs)
        ^js expander (gobj/get refs rk-expander)
        collapsible? (:collapsible? m)
        collapsed?   (and collapsible? (not (:expanded? m)))]
    (du/set-bool-attr! el attr-data-collapsible collapsible?)
    (du/set-bool-attr! el attr-data-collapsed collapsed?)
    (set! (.-textContent expander)
          (if collapsed? (expander-label m) "Show less"))
    (du/set-attr! expander attr-aria-expanded (if collapsed? "false" "true"))))

(defn- apply-aria! [^js el m]
  (let [^js refs   (du/getv el k-refs)
        ^js scroll (gobj/get refs rk-scroll)
        label      (cond
                     (not= "" (:filename m))       (:filename m)
                     (not= "" (:language-label m)) (str (:language-label m) " code")
                     :else                         "Code")]
    (du/set-attr! scroll attr-aria-label label)))

(defn- apply-model! [^js el m]
  (apply-host-css-vars! el m)
  (apply-header!        el m)
  (apply-code-body!     el m)
  (apply-collapse!      el m)
  (apply-aria!          el m)
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= new-m old-m)
        (apply-model! el new-m)))))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-expander-click! [^js el _evt]
  (let [m             (du/getv el k-model)
        now-expanded? (not (:expanded? m))]
    (if now-expanded?
      (du/set-attr!    el model/attr-expanded "")
      (du/remove-attr! el model/attr-expanded))
    (du/dispatch! el model/event-toggle #js {:expanded now-expanded?})))

(defn- on-copy-success! [^js el _evt]
  (du/dispatch! el model/event-copy
                #js {:code (:code (du/getv el k-model))}))

;; Each entry: [refs-key event-name handler-fn]. Listeners bind to shadow
;; children that live with the shadow DOM, so no explicit remove path is
;; needed across disconnect/reconnect cycles.
(def ^:private listener-spec
  [[rk-expander ev-click                       on-expander-click!]
   [rk-copy     copy-model/event-copy-success  on-copy-success!]])

(defn- install-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)]
    (doseq [[refs-key event-name handler] listener-spec]
      (let [^js target (gobj/get refs refs-key)]
        (.addEventListener target event-name
                           (fn [^js event] (handler el event)))))))

;; ── Light-DOM mutation observer ──────────────────────────────────────────────
;; Observed attributes do not cover slotted/child text. This observer watches
;; the light DOM and re-renders on any change. No feedback loop: it never
;; watches attributes, and apply-model! only writes the shadow root.
(defn- install-observer! [^js el]
  (let [^js obs (or (du/getv el k-observer)
                    (let [o (js/MutationObserver.
                             (fn on-code-mutation [_records _observer]
                               (update-from-attrs! el)))]
                      (du/setv! el k-observer o)
                      o))]
    (.observe obs el #js {:childList     true
                          :characterData true
                          :subtree       true})))

(defn- disconnect-observer! [^js el]
  (when-let [^js obs (du/getv el k-observer)]
    (.disconnect obs)))

;; ── Property accessors (Tier 1) ──────────────────────────────────────────────
(defn- install-properties! [^js proto]
  (du/define-string-prop! proto "language"    model/attr-language    "")
  (du/define-string-prop! proto "filename"    model/attr-filename    "")
  (du/define-bool-prop!   proto "showCopy"    model/attr-show-copy)
  (du/define-bool-prop!   proto "lineNumbers" model/attr-line-numbers)
  (du/define-bool-prop!   proto "wrap"        model/attr-wrap)
  (du/define-bool-prop!   proto "expanded"    model/attr-expanded)
  (du/define-number-prop! proto "maxLines"    model/attr-max-lines   0)

  ;; code — property-only override stored in an instance field. The getter
  ;; reports the effective code (override / attribute / textContent); the
  ;; setter triggers a re-render.
  (.defineProperty
   js/Object proto "code"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (resolve-code this)))
        :set (fn [v]
               (this-as ^js this
                 (if (string? v)
                   (du/setv! this k-code-val v)
                   (du/setv! this k-code-val js/undefined))
                 (update-from-attrs! this)))})

  ;; expand() / collapse() — drive the observed `expanded` attribute, the
  ;; same path the expander button click takes.
  (.defineProperty
   js/Object proto "expand"
   #js {:configurable true :enumerable true :writable true
        :value (fn [] (this-as ^js this (du/set-attr! this model/attr-expanded "")))})
  (.defineProperty
   js/Object proto "collapse"
   #js {:configurable true :enumerable true :writable true
        :value (fn [] (this-as ^js this (du/remove-attr! this model/attr-expanded)))}))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (install-listeners! el))
  (install-observer! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (disconnect-observer! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn init! []
  ;; The copy button composes x-copy — register it first. component/register!
  ;; is idempotent, so this is safe even when x-copy is already defined.
  (x-copy/init!)
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-properties!}))
