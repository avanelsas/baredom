(ns baredom.components.x-file-upload.x-file-upload
  (:require [baredom.utils.component :as component]
            [baredom.utils.forms :as forms]
            [goog.object :as gobj]
            [baredom.components.x-file-upload.model :as model]
            [baredom.utils.dom :as du]
            [baremirror.core :as mirror]
            [baremirror.plan :as plan]))

;; ---------------------------------------------------------------------------
;; Instance field keys
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xFileUploadRefs")
(def ^:private k-model     "__xFileUploadModel")
(def ^:private k-handlers  "__xFileUploadHandlers")
(def ^:private k-files     "__xFileUploadFiles")
(def ^:private k-internals "__xFileUploadInternals")
(def ^:private k-drag-ctr  "__xFileUploadDragCtr")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------

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
   du/control-font-rule
   du/hidden-rule
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-file-upload-bg:var(--x-color-surface,#1e293b);"
   "--x-file-upload-fg:var(--x-color-text,#e2e8f0);"
   "--x-file-upload-muted:var(--x-color-text-muted,#94a3b8);"
   "--x-file-upload-border:2px dashed var(--x-color-border,#334155);"
   "--x-file-upload-border-hover:2px dashed var(--x-color-primary,#60a5fa);"
   "--x-file-upload-drag-bg:color-mix(in srgb,var(--x-color-primary,rgb(96,165,250)) 8%,transparent);"
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
   "font-weight:var(--x-font-weight-semibold,600);"
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
   "border-radius:var(--x-radius-md,6px);"
   "font-size:var(--x-file-upload-font-size);"
   "font-family:inherit;"
   "color:var(--x-file-upload-fg);"
   "}"
   ;; Thumbnail
   "[part=thumbnail]{"
   "width:var(--x-file-upload-thumb-size);"
   "height:var(--x-file-upload-thumb-size);"
   "object-fit:cover;"
   "border-radius:var(--x-radius-sm,4px);"
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
   "font-size:var(--x-font-size-sm,0.8125rem);"
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
   "border-radius:var(--x-radius-sm,4px);"
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
        style-el     (.createElement js/document "style")
        drop-zone    (.createElement js/document "div")
        file-input   (.createElement js/document "input")
        content-el   (.createElement js/document "div")
        slot-el      (.createElement js/document "slot")
        drag-overlay (.createElement js/document "div")
        file-list    (.createElement js/document "div")
        live-region  (.createElement js/document "div")]

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
(defn- apply-file-input! [^js file-input {:keys [accept multiple?]}]
  (if (= accept "")
    (du/remove-attr! file-input "accept")
    (du/set-attr! file-input "accept" accept))
  (if multiple?
    (du/set-attr! file-input "multiple" "")
    (du/remove-attr! file-input "multiple")))

(defn- apply-drop-zone-state! [^js drop-zone {:keys [disabled?]}]
  (du/set-attr! drop-zone "tabindex"      (if disabled? "-1" "0"))
  (du/set-attr! drop-zone "aria-disabled" (str disabled?)))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js file-input (gobj/get refs "fileInput")
          ^js drop-zone  (gobj/get refs "dropZone")]
      (apply-file-input!      file-input m)
      (apply-drop-zone-state! drop-zone  m)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ---------------------------------------------------------------------------
;; File list rendering
;; ---------------------------------------------------------------------------
(defn- revoke-thumbnail!
  "Gives back the blob URL of the thumbnail of the file row `item`, when it has one."
  [^js item]
  (when-some [^js img (.querySelector item "[part=thumbnail]")]
    (js/URL.revokeObjectURL (.-src img))))

(defn- make-thumbnail! [^js file]
  (let [^js img (.createElement js/document "img")]
    (du/set-attr! img "part" "thumbnail")
    (set! (.-src img) (js/URL.createObjectURL file))
    (du/set-attr! img "alt" (.-name file))
    img))

(defn- make-remove-button! []
  (let [^js remove-el (.createElement js/document "button")]
    (du/set-attr! remove-el "part" "remove")
    (du/set-attr! remove-el "type" "button")
    (set! (.-textContent remove-el) "\u00d7")
    remove-el))

(defn- make-file-text!
  "The spans of a file row for its name and its size."
  []
  (let [^js name-el (.createElement js/document "span")
        ^js size-el (.createElement js/document "span")]
    (du/set-attr! name-el "part" "file-name")
    (du/set-attr! size-el "part" "file-size")
    [name-el size-el]))

(defn- make-file-item!
  "The node of a file row: its fixed structure. An image file gets a thumbnail, whose blob URL
   is made here and lives as long as the node."
  [files-by-key file-key]
  (let [^js item          (.createElement js/document "div")
        ^js file          (files-by-key file-key)
        [name-el size-el] (make-file-text!)]
    (du/set-attr! item "part" "file-item")
    (du/set-attr! item "role" "listitem")
    (when (model/file-is-image? file)
      (.appendChild item (make-thumbnail! file)))
    (.append item name-el size-el (make-remove-button!))
    item))

(defn- file-part [^js item part]
  (.querySelector item (str "[part=" part "]")))

(defn- apply-file-item!
  "Writes what the file row `shown` shows onto its node in `nodes`."
  [nodes {file-key :key :keys [index name size remove-label]}]
  (let [^js item   (nodes file-key)
        ^js remove (file-part item "remove")]
    (du/set-text-to! (file-part item "file-name") name)
    (du/set-text-to! (file-part item "file-size") size)
    (du/set-attr-to! remove "aria-label" remove-label)
    (du/set-attr-to! remove "data-index" index)))

(defn- render-file-rows!
  "Brings the rows of `file-list` to what `shown` gives for `files`. A row that stays keeps its
   node and its thumbnail, and a row that leaves gives the blob URL of its thumbnail back."
  [^js file-list files shown]
  (let [reading (mirror/read-places {:files {:parent file-list}})
        steps   (plan/plan (:places reading) {:files (mapv :key shown)})
        make    (partial make-file-item! (zipmap (map :key shown) files))]
    (run! (comp revoke-thumbnail! (:nodes reading)) (:remove steps))
    (run! (partial apply-file-item! (mirror/perform! reading steps make)) shown)))

(defn- render-file-list! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [files (array-seq (du/getv el k-files))
          shown (model/shown-files files)]
      (render-file-rows! (gobj/get refs "fileList") files shown)
      (du/set-text-to! (gobj/get refs "liveRegion") (model/selection-message (count shown))))))

(defn- clear-file-rows!
  "Takes every file row out, so no blob URL outlives the element in the document."
  [^js el]
  (when-let [refs (du/getv el k-refs)]
    (render-file-rows! (gobj/get refs "fileList") [] [])))

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
              (du/remove-attr! el "data-drag-over"))))

        on-drop
        (fn [^js e]
          (.preventDefault e)
          (du/setv! el k-drag-ctr 0)
          (du/remove-attr! el "data-drag-over")
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
  (update-from-attrs! el)
  (render-file-list! el)
  (sync-validity! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (clear-file-rows! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Form callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled disabled?)
  (update-from-attrs! el))

(defn- form-reset! [^js el]
  (let [^js files (du/getv el k-files)]
    (set! (.-length files) 0))
  (render-file-list! el)
  (sync-form-value! el)
  (sync-validity! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  (du/define-string-prop! proto "accept"   model/attr-accept)
  (du/define-string-prop! proto "name"     model/attr-name)
  (du/define-bool-prop!   proto "multiple" model/attr-multiple)
  (du/define-bool-prop!   proto "disabled" model/attr-disabled)
  (du/define-bool-prop!   proto "required" model/attr-required)
  (du/define-number-prop! proto "maxSize" model/attr-max-size 0)
  (du/define-number-prop! proto "maxFiles" model/attr-max-files 0)
  ;; Read-only files property
  (.defineProperty
   js/Object proto "files"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (.slice (or (du/getv this k-files) #js []))))}))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-disabled-fn       form-disabled!
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
