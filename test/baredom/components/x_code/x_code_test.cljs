(ns baredom.components.x-code.x-code-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [clojure.string :as str]
            [baredom.components.x-code.x-code :as x]
            [baredom.components.x-code.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el [] (.createElement js/document model/tag-name))
(defn ^js append! [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js part [^js el sel] (.querySelector (.-shadowRoot el) sel))
(defn ^js parts [^js el sel] (.querySelectorAll (.-shadowRoot el) sel))

(defn ^js with-text [^js el s]
  (set! (.-textContent el) s)
  el)

;; ── Registration & structure ─────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-code should be registered")
  (is (some? (.get js/customElements "x-copy"))
      "x-code registers its composed x-copy dependency"))

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))            "shadow root should exist")
    (is (some? (part el "[part=container]")) "container should exist")
    (is (some? (part el "[part=header]"))    "header should exist")
    (is (some? (part el "[part=scroll]"))    "scroll region should exist")
    (is (some? (part el "[part=code]"))      "code element should exist")
    (is (some? (part el "[part=expander]"))  "expander should exist")
    (is (some? (part el "[part=copy]"))      "composed x-copy should exist")))

(deftest scroll-region-is-labelled-test
  (let [el     (append! (with-text (make-el) "code"))
        scroll (part el "[part=scroll]")]
    (is (= "region" (.getAttribute scroll "role")))
    (is (= "0"      (.getAttribute scroll "tabindex")))
    (is (some?      (.getAttribute scroll "aria-label")))))

;; ── textContent rendering ────────────────────────────────────────────────────
(deftest text-content-rendering-test
  (let [el (append! (with-text (make-el) "const x = 1;"))]
    (is (= 1 (alength (parts el "[part=line]")))
        "one line of code renders one line element")
    (is (= "const x = 1;"
           (.-textContent (part el "[part=line-content]"))))))

(deftest multi-line-rendering-test
  (let [el (append! (with-text (make-el) "line one\nline two\nline three"))]
    (is (= 3 (alength (parts el "[part=line]"))))))

(deftest escaping-test
  (let [el      (append! (with-text (make-el) "<div> & </div>"))
        content (part el "[part=line-content]")
        code    (part el "[part=code]")]
    (is (= "<div> & </div>" (.-textContent content))
        "the visible text round-trips the raw characters")
    (is (re-find #"&lt;"  (.-innerHTML code))
        "angle brackets are HTML-escaped in the markup")
    (is (re-find #"&amp;" (.-innerHTML code))
        "ampersands are HTML-escaped in the markup")))

(deftest dedent-on-render-test
  (let [el (append! (with-text (make-el) "\n    const x = 1;\n    const y = 2;\n"))]
    (is (= "const x = 1;" (.-textContent (part el "[part=line-content]")))
        "leading blank line is dropped and common indentation stripped")))

;; ── code property / attribute ────────────────────────────────────────────────
(deftest code-property-overrides-text-content-test
  (let [el (append! (with-text (make-el) "from-text-content"))]
    (set! (.-code el) "from-property")
    (is (= "from-property" (.-textContent (part el "[part=line-content]"))))
    (is (= "from-property" (.-code el)) "the code getter reports the override")))

(deftest code-attribute-rendering-test
  (let [el (append! (make-el))]
    (.setAttribute el model/attr-code "attr-code-value")
    (is (= "attr-code-value" (.-textContent (part el "[part=line-content]"))))))

(deftest textcontent-mutation-rerenders-test
  (async done
    (let [el (append! (make-el))]
      (set! (.-textContent el) "mutated content")
      ;; The MutationObserver re-renders on a microtask — a macrotask wait
      ;; guarantees it has run.
      (js/setTimeout
       (fn []
         (is (= "mutated content"
                (.-textContent (part el "[part=line-content]")))
             "changing light-DOM text re-renders the shadow")
         (done))
       0))))

;; ── Line numbers ─────────────────────────────────────────────────────────────
(deftest line-numbers-gutter-test
  (let [el     (append! (with-text (make-el) "a\nb\nc"))
        gutter (part el "[part=gutter]")]
    (is (= "1" (.-textContent gutter)) "the first gutter cell numbers from 1")
    (is (= "none" (.-display (js/getComputedStyle gutter)))
        "the gutter is hidden without the line-numbers attribute")
    (.setAttribute el model/attr-line-numbers "")
    (is (not= "none" (.-display (js/getComputedStyle (part el "[part=gutter]"))))
        "the gutter shows once line-numbers is set")))

(defn- gutter-digits [^js el]
  (.trim (.getPropertyValue (js/getComputedStyle el) "--x-code-gutter-digits")))

(deftest gutter-width-tracks-line-count-test
  (let [el (append! (with-text (make-el) (str/join "\n" (repeat 7 "x"))))]
    (is (= "2" (gutter-digits el))
        "short blocks floor the gutter at 2 digits"))
  (let [el (append! (with-text (make-el) (str/join "\n" (repeat 120 "x"))))]
    (is (= "3" (gutter-digits el))
        "a 120-line block widens every gutter to 3 digits uniformly")))

;; ── Soft-wrap ────────────────────────────────────────────────────────────────
(deftest wrap-toggle-test
  (let [el (append! (with-text (make-el) "a long line of code"))]
    (is (= "pre" (.-whiteSpace (js/getComputedStyle (part el "[part=line-content]"))))
        "lines do not wrap by default")
    (.setAttribute el model/attr-wrap "")
    (is (= "pre-wrap"
           (.-whiteSpace (js/getComputedStyle (part el "[part=line-content]"))))
        "the wrap attribute enables soft-wrapping")))

;; ── Collapsible ──────────────────────────────────────────────────────────────
(deftest collapsible-state-test
  (let [el (make-el)]
    (with-text el "l1\nl2\nl3\nl4\nl5\nl6")
    (.setAttribute el model/attr-max-lines "2")
    (append! el)
    (is (.hasAttribute el "data-collapsible") "6 lines over a 2-line cap is collapsible")
    (is (.hasAttribute el "data-collapsed")   "a collapsible block starts collapsed")
    (is (re-find #"more" (.-textContent (part el "[part=expander]"))))))

(deftest not-collapsible-when-short-test
  (let [el (make-el)]
    (with-text el "l1\nl2")
    (.setAttribute el model/attr-max-lines "5")
    (append! el)
    (is (not (.hasAttribute el "data-collapsible")))
    (is (not (.hasAttribute el "data-collapsed")))))

(deftest expander-toggles-expanded-test
  (let [el   (make-el)
        seen (atom nil)]
    (with-text el "l1\nl2\nl3\nl4")
    (.setAttribute el model/attr-max-lines "2")
    (append! el)
    (.addEventListener el model/event-toggle
                       (fn [^js ev] (reset! seen (.. ev -detail -expanded))))
    (.click (part el "[part=expander]"))
    (is (.hasAttribute el model/attr-expanded)   "clicking the expander expands the block")
    (is (not (.hasAttribute el "data-collapsed")) "an expanded block is no longer collapsed")
    (is (true? @seen) "x-code-toggle fires with the new expanded state")
    (.click (part el "[part=expander]"))
    (is (not (.hasAttribute el model/attr-expanded)) "clicking again collapses it")
    (is (false? @seen))))

(deftest expand-collapse-methods-test
  (let [el (append! (with-text (make-el) "a\nb\nc"))]
    (.expand el)
    (is (.hasAttribute el model/attr-expanded))
    (.collapse el)
    (is (not (.hasAttribute el model/attr-expanded)))))

;; ── Copy button ──────────────────────────────────────────────────────────────
(deftest copy-button-carries-code-test
  (let [el   (make-el)]
    (with-text el "copy me")
    (.setAttribute el model/attr-show-copy "")
    (append! el)
    (let [copy (part el "[part=copy]")]
      (is (some? copy) "the copy button exists")
      (is (= "copy me" (.getAttribute copy "text"))
          "the composed x-copy receives the prepared code"))))

(deftest copy-success-reemits-x-code-copy-test
  (let [el   (make-el)
        seen (atom nil)]
    (with-text el "payload")
    (.setAttribute el model/attr-show-copy "")
    (append! el)
    (.addEventListener el model/event-copy
                       (fn [^js ev] (reset! seen (.. ev -detail -code))))
    ;; Simulate the composed x-copy reporting a successful copy.
    (.dispatchEvent (part el "[part=copy]")
                    (js/CustomEvent. "x-copy-success"
                                     #js {:bubbles true :composed true
                                          :detail  #js {:text "payload"}}))
    (is (= "payload" @seen)
        "x-code re-emits x-code-copy when the inner x-copy succeeds")))

;; ── Tokenization ─────────────────────────────────────────────────────────────
(deftest js-tokenization-test
  (let [el (make-el)]
    (.setAttribute el model/attr-language "js")
    (with-text el "const total = 42;")
    (append! el)
    (is (some? (part el ".tok-keyword")) "js keywords get a token class")
    (is (some? (part el ".tok-number"))  "js numbers get a token class")))

(deftest plain-text-has-no-tokens-test
  (let [el (append! (with-text (make-el) "const total = 42;"))]
    (is (nil? (part el ".tok-keyword"))
        "without a language attribute nothing is tokenized")))

;; ── Properties ───────────────────────────────────────────────────────────────
(deftest property-reflection-test
  (let [el (append! (make-el))]
    (set! (.-language el) "css")
    (is (= "css" (.getAttribute el model/attr-language)))
    (set! (.-lineNumbers el) true)
    (is (.hasAttribute el model/attr-line-numbers))
    (set! (.-maxLines el) 8)
    (is (= "8" (.getAttribute el model/attr-max-lines)))
    (is (= 8 (.-maxLines el)))
    (set! (.-wrap el) true)
    (is (.hasAttribute el model/attr-wrap))))

;; ── Reconnect stability ──────────────────────────────────────────────────────
(deftest reconnect-stability-test
  (let [el   (with-text (make-el) "const stable = true;")
        body (.-body js/document)]
    (.appendChild body el)
    (.remove el)
    (.appendChild body el)
    (is (= "const stable = true;"
           (.-textContent (part el "[part=line-content]")))
        "code stays rendered after a disconnect/reconnect cycle")))
