(ns baredom.components.x-file-upload.x-file-upload
  (:require [goog.object :as gobj]
            [baredom.components.x-file-upload.model :as model]
            [baredom.utils.dom :as du]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xFileUploadRefs")
(def ^:private k-handlers  "__xFileUploadHandlers")
(def ^:private k-files     "__xFileUploadFiles")
(def ^:private k-internals "__xFileUploadInternals")
(def ^:private k-drag-ctr  "__xFileUploadDragCtr")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-file-upload-bg:var(--x-color-surface,#ffffff);"
   "--x-file-upload-fg:var(--x-color-text,#0f172a);"
   "--x-file-upload-muted:var(--x-color-text-muted,#64748b);"
   "--x-file-upload-border:2px dashed var(--x-color-border,#cbd5e1);"
   "--x-file-upload-border-hover:2px dashed var(--x-color-primary,#3b82f6);"
   "--x-file-upload-drag-bg:var(--x-color-primary,rgba(59,130,246,0.05));"
   "--x-file-upload-radius:var(--x-radius-md,8px);"
   "--x-file-upload-padding:var(--x-space-lg,24px);"
   "--x-file-upload-font-size:var(--x-font-size-sm,0.9375rem);"
   "--x-file-upload-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-file-upload-disabled-opacity:var(--x-opacity-disabled,0.55);"
   "--x-file-upload-transition-duration:var(--x-transition-duration,150ms);"
   "--x-file-upload-item-bg:var(--x-color-surface,#f8fafc);"
   "--x-file-upload-item-border:1px solid var(--x-color-border,#e2e8f0);"
   "--x-file-upload-thumb-size:48px;"
   "--x-file-upload-remove-color:var(--x-color-text-muted,#64748b);"
   "--x-file-upload-remove-hover:var(--x-color-danger,#ef4444);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-file-upload-bg:var(--x-color-surface,#1e293b);"
   "--x-file-upload-fg:var(--x-color-text,#e2e8f0);"
   "--x-file-upload-muted:var(--x-color-text-muted,#94a3b8);"
   "--x-file-upload-border:2px dashed var(--x-color-border,#334155);"
   "--x-file-upload-border-hover:2px dashed var(--x-color-primary,#60a5fa);"
   "--x-file-upload-drag-bg:rgba(96,165,250,0.08);"
   "--x-file-upload-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-file-upload-item-bg:var(--x-color-surface,#1e293b);"
   "--x-file-upload-item-border:1px solid var(--x-color-border,#334155);"
   "--x-file-upload-remove-color:var(--x-color-text-muted,#94a3b8);"
   "}"
   "}"
   ;; Drop zone
   "[part=drop-zone]{"
   "position:relative;"
   "display:flex;"
   "flex-direction:column;"
   "align-items:center;"
   "justify-content:center;"
   "min-height:120px;"
   "padding:var(--x-file-upload-padding);"
   "background:var(--x-file-upload-bg);"
   "border:var(--x-file-upload-border);"
   "border-radius:var(--x-file-upload-radius);"
   "cursor:pointer;"
   "text-align:center;"
   "color:var(--x-file-upload-muted);"
   "font-size:var(--x-file-upload-font-size);"
   "font-family:inherit;"
   "transition:border-color var(--x-file-upload-transition-duration) ease,"
   "background var(--x-file-upload-transition-duration) ease;"
   "}"
   "[part=drop-zone]:hover{"
   "border:var(--x-file-upload-border-hover);"
   "}"
   "[part=drop-zone]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 2px var(--x-file-upload-focus-ring);"
   "}"
   ;; Drag-over state
   ":host([data-drag-over]) [part=drop-zone]{"
   "border:var(--x-file-upload-border-hover);"
   "background:var(--x-file-upload-drag-bg);"
   "}"
   ":host([data-drag-over]) [part=content]{"
   "visibility:hidden;"
   "}"
   ":host([data-drag-over]) [part=drag-overlay]{"
   "display:flex;"
   "}"
   ;; Drag overlay
   "[part=drag-overlay]{"
   "display:none;"
   "position:absolute;"
   "inset:0;"
   "align-items:center;"
   "justify-content:center;"
   "border-radius:var(--x-file-upload-radius);"
   "font-weight:600;"
   "color:var(--x-color-primary,#3b82f6);"
   "pointer-events:none;"
   "}"
   ;; Disabled
   ":host([disabled]){"
   "pointer-events:none;"
   "cursor:default;"
   "}"
   ":host([disabled]) [part=drop-zone]{"
   "opacity:var(--x-file-upload-disabled-opacity);"
   "}"
   ;; File list
   "[part=file-list]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:8px;"
   "margin-top:12px;"
   "}"
   "[part=file-list]:empty{display:none;}"
   ;; File item
   "[part=file-item]{"
   "display:flex;"
   "align-items:center;"
   "gap:12px;"
   "padding:8px 12px;"
   "background:var(--x-file-upload-item-bg);"
   "border:var(--x-file-upload-item-border);"
   "border-radius:6px;"
   "font-size:var(--x-file-upload-font-size);"
   "font-family:inherit;"
   "color:var(--x-file-upload-fg);"
   "}"
   ;; Thumbnail
   "[part=thumbnail]{"
   "width:var(--x-file-upload-thumb-size);"
   "height:var(--x-file-upload-thumb-size);"
   "object-fit:cover;"
   "border-radius:4px;"
   "flex-shrink:0;"
   "}"
   ;; File name
   "[part=file-name]{"
   "flex:1;"
   "min-width:0;"
   "overflow:hidden;"
   "text-overflow:ellipsis;"
   "white-space:nowrap;"
   "}"
   ;; File size
   "[part=file-size]{"
   "color:var(--x-file-upload-muted);"
   "font-size:0.8125rem;"
   "flex-shrink:0;"
   "}"
   ;; Remove button
   "[part=remove]{"
   "all:unset;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:1.5rem;"
   "height:1.5rem;"
   "font-size:1rem;"
   "color:var(--x-file-upload-remove-color);"
   "cursor:pointer;"
   "border-radius:4px;"
   "flex-shrink:0;"
   "}"
   "[part=remove]:hover{"
   "color:var(--x-file-upload-remove-hover);"
   "}"
   "[part=remove]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 2px var(--x-file-upload-focus-ring);"
   "}"
   ;; Live region (sr-only)
   "[part=live-region]{"
   "position:absolute;width:1px;height:1px;"
   "overflow:hidden;clip:rect(0,0,0,0);"
   "}"
   ;; Hidden input
   "input[type=file]{display:none;}"
   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=drop-zone]{transition:none !important;}"
   "}"
   ;; Coarse pointer: bigger touch targets
   "@media (pointer:coarse){"
   "[part=drop-zone]{min-height:140px;}"
   "[part=remove]{width:2.75rem;height:2.75rem;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style-el     (make-el "style")
        drop-zone    (make-el "div")
        file-input   (make-el "input")
        content-el   (make-el "div")
        slot-el      (make-el "slot")
        drag-overlay (make-el "div")
        file-list    (make-el "div")
        live-region  (make-el "div")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! drop-zone "part"     "drop-zone")
    (du/set-attr! drop-zone "role"     "button")
    (du/set-attr! drop-zone "tabindex" "0")

    (du/set-attr! file-input "type"        "file")
    (du/set-attr! file-input "aria-hidden" "true")
    (du/set-attr! file-input "tabindex"    "-1")

    (du/set-attr! content-el "part" "content")
    (.appendChild content-el slot-el)

    (du/set-attr! drag-overlay "part"   "drag-overlay")
    (du/set-attr! drag-overlay "hidden" "")
    (set! (.-textContent drag-overlay) model/msg-drop-here)

    (du/set-attr! file-list "part" "file-list")
    (du/set-attr! file-list "role" "list")

    (du/set-attr! live-region "part"        "live-region")
    (du/set-attr! live-region "aria-live"   "polite")
    (du/set-attr! live-region "aria-atomic" "true")

    (.appendChild drop-zone file-input)
    (.appendChild drop-zone content-el)
    (.appendChild drop-zone drag-overlay)

    (.appendChild root style-el)
    (.appendChild root drop-zone)
    (.appendChild root file-list)
    (.appendChild root live-region)

    (du/setv! el k-files   #js [])
    (du/setv! el k-drag-ctr 0)

    (let [refs #js {:dropZone    drop-zone
                    :fileInput   file-input
                    :content     content-el
                    :dragOverlay drag-overlay
                    :fileList    file-list
                    :liveRegion  live-region}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Model reading
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:accept-raw        (du/get-attr el model/attr-accept)
    :multiple-present? (du/has-attr? el model/attr-multiple)
    :max-size-raw      (du/get-attr el model/attr-max-size)
    :max-files-raw     (du/get-attr el model/attr-max-files)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :required-present? (du/has-attr? el model/attr-required)
    :name-raw          (du/get-attr el model/attr-name)}))

;; ---------------------------------------------------------------------------
;; Render (attribute sync)
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [{:keys [accept multiple? disabled?]} (read-model el)
          ^js file-input (gobj/get refs "fileInput")
          ^js drop-zone  (gobj/get refs "dropZone")]
      (if (= accept "")
        (.removeAttribute file-input "accept")
        (du/set-attr! file-input "accept" accept))
      (if multiple?
        (du/set-attr! file-input "multiple" "")
        (.removeAttribute file-input "multiple"))
      (du/set-attr! drop-zone "tabindex" (if disabled? "-1" "0"))
      (du/set-attr! drop-zone "aria-disabled" (str disabled?)))))

;; ---------------------------------------------------------------------------
;; Blob URL management
;; ---------------------------------------------------------------------------
(defn- revoke-thumbnails! [^js file-list-el]
  (let [^js thumbs (.querySelectorAll file-list-el "[part=thumbnail]")]
    (doseq [^js img (array-seq thumbs)]
      (js/URL.revokeObjectURL (.-src img)))))

;; ---------------------------------------------------------------------------
;; Forward declarations
;; ---------------------------------------------------------------------------
(declare remove-file!)

;; ---------------------------------------------------------------------------
;; File list rendering
;; ---------------------------------------------------------------------------
(defn- render-file-list! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js file-list  (gobj/get refs "fileList")
          ^js live-region (gobj/get refs "liveRegion")
          ^js files      (du/getv el k-files)
          n              (.-length files)]

      ;; Clean up old blob URLs
      (revoke-thumbnails! file-list)
      (set! (.-textContent file-list) "")

      ;; Render each file
      (dotimes [i n]
        (let [^js file (aget files i)
              ^js item (make-el "div")
              ^js name-el (make-el "span")
              ^js size-el (make-el "span")
              ^js remove-el (make-el "button")
              fname (.-name file)]

          (du/set-attr! item "part" "file-item")
          (du/set-attr! item "role" "listitem")

          ;; Thumbnail for images
          (when (model/file-is-image? file)
            (let [^js img (make-el "img")]
              (du/set-attr! img "part" "thumbnail")
              (set! (.-src img) (js/URL.createObjectURL file))
              (du/set-attr! img "alt" fname)
              (.appendChild item img)))

          (du/set-attr! name-el "part" "file-name")
          (set! (.-textContent name-el) fname)

          (du/set-attr! size-el "part" "file-size")
          (set! (.-textContent size-el) (model/format-file-size (.-size file)))

          (du/set-attr! remove-el "part"       "remove")
          (du/set-attr! remove-el "type"       "button")
          (du/set-attr! remove-el "aria-label" (str "Remove " fname))
          (du/set-attr! remove-el "data-index" (str i))
          (set! (.-textContent remove-el) "\u00d7")

          (.appendChild item name-el)
          (.appendChild item size-el)
          (.appendChild item remove-el)
          (.appendChild file-list item)))

      ;; Live region announcement
      (set! (.-textContent live-region)
            (if (zero? n) ""
                (str n " file" (when (not= n 1) "s") " selected"))))))

;; ---------------------------------------------------------------------------
;; Form integration
;; ---------------------------------------------------------------------------
(defn- sync-form-value! [^js el]
  (when-let [^js internals (du/getv el k-internals)]
    (let [^js files (du/getv el k-files)
          name-attr (or (du/get-attr el model/attr-name) "file")]
      (if (zero? (.-length files))
        (.setFormValue internals nil)
        (let [^js fd (js/FormData.)]
          (dotimes [i (.-length files)]
            (.append fd name-attr (aget files i)))
          (.setFormValue internals fd))))))

(defn- sync-validity! [^js el]
  (when-let [^js internals (du/getv el k-internals)]
    (when-let [refs (du/getv el k-refs)]
      (let [required? (du/has-attr? el model/attr-required)
            ^js files (du/getv el k-files)
            ^js drop-zone (gobj/get refs "dropZone")]
        (if (and required? (zero? (.-length files)))
          (.setValidity internals #js {:valueMissing true}
                        "Please select a file." drop-zone)
          (.setValidity internals #js {} ""))))))

;; ---------------------------------------------------------------------------
;; Dispatch helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; File management
;; ---------------------------------------------------------------------------
(defn- add-files! [^js el ^js new-files]
  (let [{:keys [accept multiple? max-size max-files]} (read-model el)
        ^js current-files (du/getv el k-files)
        current-count     (.-length current-files)
        {:keys [accepted rejected]}
        (model/validate-files (array-seq new-files) accept max-size max-files current-count)]
    (if multiple?
      (doseq [^js f accepted]
        (.push current-files f))
      (do
        ;; Single mode: replace
        (set! (.-length current-files) 0)
        (when (seq accepted)
          (.push current-files (first accepted)))))
    (render-file-list! el)
    (sync-form-value! el)
    (sync-validity! el)
    (du/dispatch! el model/event-select
               #js {:files    (to-array accepted)
                    :rejected (to-array (map (fn [{:keys [file reason]}]
                                               #js {:file file :reason reason})
                                             rejected))})))

(defn- remove-file! [^js el idx]
  (let [^js files (du/getv el k-files)
        ^js file  (aget files idx)]
    (.splice files idx 1)
    (render-file-list! el)
    (sync-form-value! el)
    (sync-validity! el)
    (du/dispatch! el model/event-remove
               #js {:file file :remaining (.slice files)})))

;; ---------------------------------------------------------------------------
;; Handler construction
;; ---------------------------------------------------------------------------
(defn- make-handlers [^js el]
  (let [on-zone-click
        (fn [^js _e]
          (when-not (du/has-attr? el model/attr-disabled)
            (when-let [refs (du/getv el k-refs)]
              (.click (gobj/get refs "fileInput")))))

        on-zone-keydown
        (fn [^js e]
          (when (and (or (= (.-key e) "Enter") (= (.-key e) " "))
                     (not (du/has-attr? el model/attr-disabled)))
            (.preventDefault e)
            (when-let [refs (du/getv el k-refs)]
              (.click (gobj/get refs "fileInput")))))

        on-file-change
        (fn [^js e]
          (let [^js input (.-target e)
                ^js files (.-files input)]
            (when (pos? (.-length files))
              (add-files! el files))
            ;; Reset so same file can be re-selected
            (set! (.-value input) "")))

        on-dragenter
        (fn [^js e]
          (.preventDefault e)
          (when-not (du/has-attr? el model/attr-disabled)
            (let [ctr (inc (du/getv el k-drag-ctr))]
              (du/setv! el k-drag-ctr ctr)
              (when (= ctr 1)
                (du/set-attr! el "data-drag-over" "")))))

        on-dragover
        (fn [^js e]
          (.preventDefault e)
          (set! (.. e -dataTransfer -dropEffect) "copy"))

        on-dragleave
        (fn [^js _e]
          (let [ctr (dec (du/getv el k-drag-ctr))]
            (du/setv! el k-drag-ctr ctr)
            (when (<= ctr 0)
              (du/setv! el k-drag-ctr 0)
              (.removeAttribute el "data-drag-over"))))

        on-drop
        (fn [^js e]
          (.preventDefault e)
          (du/setv! el k-drag-ctr 0)
          (.removeAttribute el "data-drag-over")
          (when-not (du/has-attr? el model/attr-disabled)
            (let [^js files (.. e -dataTransfer -files)]
              (when (pos? (.-length files))
                (add-files! el files)))))

        on-file-list-click
        (fn [^js e]
          (let [^js target (.-target e)
                ^js btn    (if (.hasAttribute target "data-index")
                             target
                             (.closest target "[data-index]"))]
            (when btn
              (let [idx (js/parseInt (.getAttribute btn "data-index") 10)]
                (when-not (js/isNaN idx)
                  (remove-file! el idx))))))]

    #js {:zoneClick      on-zone-click
         :zoneKeydown    on-zone-keydown
         :fileChange     on-file-change
         :dragenter      on-dragenter
         :dragover       on-dragover
         :dragleave      on-dragleave
         :drop           on-drop
         :fileListClick  on-file-list-click}))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js drop-zone  (gobj/get refs "dropZone")
            ^js file-input (gobj/get refs "fileInput")
            ^js file-list  (gobj/get refs "fileList")]
        (.addEventListener drop-zone "click"     (gobj/get handlers "zoneClick"))
        (.addEventListener drop-zone "keydown"   (gobj/get handlers "zoneKeydown"))
        (.addEventListener drop-zone "dragenter" (gobj/get handlers "dragenter"))
        (.addEventListener drop-zone "dragover"  (gobj/get handlers "dragover"))
        (.addEventListener drop-zone "dragleave" (gobj/get handlers "dragleave"))
        (.addEventListener drop-zone "drop"      (gobj/get handlers "drop"))
        (.addEventListener file-input "change"   (gobj/get handlers "fileChange"))
        (.addEventListener file-list  "click"    (gobj/get handlers "fileListClick"))))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js drop-zone  (gobj/get refs "dropZone")
            ^js file-input (gobj/get refs "fileInput")
            ^js file-list  (gobj/get refs "fileList")]
        (.removeEventListener drop-zone "click"     (gobj/get handlers "zoneClick"))
        (.removeEventListener drop-zone "keydown"   (gobj/get handlers "zoneKeydown"))
        (.removeEventListener drop-zone "dragenter" (gobj/get handlers "dragenter"))
        (.removeEventListener drop-zone "dragover"  (gobj/get handlers "dragover"))
        (.removeEventListener drop-zone "dragleave" (gobj/get handlers "dragleave"))
        (.removeEventListener drop-zone "drop"      (gobj/get handlers "drop"))
        (.removeEventListener file-input "change"   (gobj/get handlers "fileChange"))
        (.removeEventListener file-list  "click"    (gobj/get handlers "fileListClick"))))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (when (and (.-attachInternals el) (not (du/getv el k-internals)))
    (du/setv! el k-internals (.attachInternals el)))
  (remove-listeners! el)
  (du/setv! el k-handlers (make-handlers el))
  (add-listeners! el)
  (render! el)
  (sync-validity! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  ;; Revoke any blob URLs
  (when-let [refs (du/getv el k-refs)]
    (revoke-thumbnails! (gobj/get refs "fileList"))))

(defn- attribute-changed! [^js el _name _old _new]
  (when (du/getv el k-refs)
    (render! el)))

;; ---------------------------------------------------------------------------
;; Form callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled disabled?)
  (render! el))

(defn- form-reset! [^js el]
  (let [^js files (du/getv el k-files)]
    (set! (.-length files) 0))
  (when-let [refs (du/getv el k-refs)]
    (revoke-thumbnails! (gobj/get refs "fileList")))
  (render-file-list! el)
  (sync-form-value! el)
  (sync-validity! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (du/has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this
                              (du/set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (du/get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (.setAttribute this attr-name (str v))
                                (.removeAttribute this attr-name))))}))

(defn- define-int-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn []
               (this-as ^js this
                        (let [v (du/get-attr this attr-name)]
                          (when (some? v)
                            (let [n (js/parseInt v 10)]
                              (when-not (js/isNaN n) n))))))
        :set (fn [v]
               (this-as ^js this
                        (if (and (some? v) (not= v js/undefined))
                          (.setAttribute this attr-name (str v))
                          (.removeAttribute this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (set! (.-formAssociated cls) true)

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Properties
    (define-string-prop! proto "accept"   model/attr-accept)
    (define-string-prop! proto "name"     model/attr-name)
    (define-bool-prop!   proto "multiple" model/attr-multiple)
    (define-bool-prop!   proto "disabled" model/attr-disabled)
    (define-bool-prop!   proto "required" model/attr-required)
    (define-int-prop!    proto "maxSize"  model/attr-max-size)
    (define-int-prop!    proto "maxFiles" model/attr-max-files)

    ;; Read-only files property
    (.defineProperty
     js/Object proto "files"
     #js {:configurable true
          :enumerable   true
          :get (fn [] (this-as ^js this
                               (.slice (or (du/getv this k-files) #js []))))})

    ;; Form delegates
    (aset proto "checkValidity"
          (fn [] (this-as ^js this
                          (when-let [^js i (du/getv this k-internals)]
                            (.checkValidity i)))))

    (aset proto "reportValidity"
          (fn [] (this-as ^js this
                          (when-let [^js i (du/getv this k-internals)]
                            (.reportValidity i)))))

    ;; Form-associated callbacks
    (aset proto "formDisabledCallback"
          (fn [d] (this-as ^js this (form-disabled! this d))))

    (aset proto "formResetCallback"
          (fn [] (this-as ^js this (form-reset! this))))

    ;; Lifecycle callbacks
    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))

    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))

    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
