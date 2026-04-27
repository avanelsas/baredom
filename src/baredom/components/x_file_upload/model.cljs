(ns baredom.components.x-file-upload.model
  (:require [clojure.string :as str]
            [baredom.utils.model :as mu]))

(def tag-name "x-file-upload")

;; Attribute name constants
(def attr-accept    "accept")
(def attr-multiple  "multiple")
(def attr-max-size  "max-size")
(def attr-max-files "max-files")
(def attr-disabled  "disabled")
(def attr-required  "required")
(def attr-name      "name")

;; Event name constants
(def event-select "x-file-upload-select")
(def event-remove "x-file-upload-remove")

;; UI text
(def msg-drop-here "Drop files here")

(def observed-attributes
  #js [attr-accept attr-multiple attr-max-size attr-max-files
       attr-disabled attr-required attr-name])

(def property-api
  {:accept   {:type 'string  :reflects-attribute attr-accept}
   :multiple {:type 'boolean :reflects-attribute attr-multiple}
   :maxSize  {:type 'number  :reflects-attribute attr-max-size}
   :maxFiles {:type 'number  :reflects-attribute attr-max-files}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :required {:type 'boolean :reflects-attribute attr-required}
   :name     {:type 'string  :reflects-attribute attr-name}
   :files    {:type 'array   :read-only true}})

(def event-schema
  {event-select {:cancelable false
                 :detail     {:files 'array :rejected 'array}}
   event-remove {:cancelable false
                 :detail     {:file 'object :remaining 'array}}})

;; ---------------------------------------------------------------------------
;; Normalization
;; ---------------------------------------------------------------------------
(defn parse-positive-int
  "Parses string to positive integer, returns nil on failure."
  [s]
  (when (some? s)
    (let [n (js/parseInt s 10)]
      (when (and (not (js/isNaN n)) (pos? n)) n))))

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [accept-raw multiple-present? max-size-raw max-files-raw
           disabled-present? required-present? name-raw]}]
  {:accept    (or accept-raw "")
   :multiple? (boolean multiple-present?)
   :max-size  (parse-positive-int max-size-raw)
   :max-files (parse-positive-int max-files-raw)
   :disabled? (boolean disabled-present?)
   :required? (boolean required-present?)
   :name      (or name-raw "")})

;; ---------------------------------------------------------------------------
;; File type matching
;; ---------------------------------------------------------------------------
(defn file-is-image?
  "Returns true if the file's MIME type starts with image/."
  [^js file]
  (let [t (.-type file)]
    (and (string? t) (.startsWith t "image/"))))

(defn- get-extension
  "Extracts lowercase file extension from a filename, e.g. 'photo.JPG' → '.jpg'."
  [filename]
  (let [idx (.lastIndexOf filename ".")]
    (when (pos? idx)
      (str/lower-case (.substring filename idx)))))

(defn matches-accept?
  "Returns true if file matches the accept filter string.
   Accept format: comma-separated MIME types, wildcards (image/*), or extensions (.pdf).
   Returns true when accept is nil or empty (no filter)."
  [^js file accept-str]
  (if (or (nil? accept-str) (= accept-str ""))
    true
    (let [file-type (str/lower-case (or (.-type file) ""))
          file-ext  (get-extension (.-name file))
          tokens    (map str/trim (str/split accept-str #","))]
      (boolean (some (fn [token]
              (let [t (str/lower-case token)]
                (cond
                  ;; Extension match: .pdf, .jpg etc
                  (.startsWith t ".")
                  (= t file-ext)

                  ;; Wildcard MIME: image/*, video/* etc
                  (.endsWith t "/*")
                  (let [prefix (.substring t 0 (.indexOf t "/"))]
                    (.startsWith file-type (str prefix "/")))

                  ;; Exact MIME match
                  :else
                  (= t file-type))))
            tokens)))))

;; ---------------------------------------------------------------------------
;; File validation
;; ---------------------------------------------------------------------------
(defn validate-file
  "Validates a single file. Returns {:valid? bool :reason string-or-nil}."
  [^js file accept-str max-size]
  (cond
    (and (mu/non-empty-string? accept-str) (not (matches-accept? file accept-str)))
    {:valid? false :reason "File type not accepted"}

    (and (some? max-size) (> (.-size file) max-size))
    {:valid? false :reason "File too large"}

    :else
    {:valid? true :reason nil}))

(defn validate-files
  "Validates a batch of files. Returns {:accepted [...] :rejected [{:file :reason}]}.
   Enforces max-files limit based on current-count."
  [files accept-str max-size max-files current-count]
  (loop [remaining (seq files)
         accepted  []
         rejected  []
         count     current-count]
    (if-not remaining
      {:accepted accepted :rejected rejected}
      (let [^js file (first remaining)
            result   (validate-file file accept-str max-size)]
        (if-not (:valid? result)
          (recur (next remaining) accepted
                 (conj rejected {:file file :reason (:reason result)}) count)
          (if (and (some? max-files) (>= count max-files))
            (recur (next remaining) accepted
                   (conj rejected {:file file :reason "Maximum files reached"}) count)
            (recur (next remaining) (conj accepted file) rejected (inc count))))))))

;; ---------------------------------------------------------------------------
;; Formatting
;; ---------------------------------------------------------------------------
(defn format-file-size
  "Formats bytes into human-readable string."
  [bytes]
  (cond
    (< bytes 1024)       (str bytes " B")
    (< bytes 1048576)    (str (.toFixed (/ bytes 1024) 1) " KB")
    (< bytes 1073741824) (str (.toFixed (/ bytes 1048576) 1) " MB")
    :else                (str (.toFixed (/ bytes 1073741824) 1) " GB")))
