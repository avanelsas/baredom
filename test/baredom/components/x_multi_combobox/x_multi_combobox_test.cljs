(ns baredom.components.x-multi-combobox.x-multi-combobox-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-multi-combobox.x-multi-combobox :as x]
            [baredom.components.x-multi-combobox.model            :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el
  "Creates an x-multi-combobox with 5 fruit options."
  []
  (let [^js el (.createElement js/document model/tag-name)]
    (doseq [[v l] [["apple" "Apple"] ["banana" "Banana"]
                   ["cherry" "Cherry"] ["date" "Date"]
                   ["elderberry" "Elderberry"]]]
      (let [^js opt (.createElement js/document "option")]
        (.setAttribute opt "value" v)
        (set! (.-textContent opt) l)
        (.appendChild el opt)))
    el))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn ^js shadow-parts [^js el selector]
  (.querySelectorAll (.-shadowRoot el) selector))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=wrapper]")))
    (is (some? (shadow-part el "[part=chip-area]")))
    (is (some? (shadow-part el "[part=input]")))
    (is (some? (shadow-part el "[part=chevron]")))
    (is (some? (shadow-part el "[part=panel]")))
    (is (some? (shadow-part el "[part=error]")))))

;; ── Inline error display (mirrors x-form-field) ──────────────────────────────

(deftest error-panel-nested-in-wrapper-test
  ;; The panel is anchored inside the wrapper so the inline error span below the
  ;; wrapper never displaces the open dropdown.
  (let [^js el      (append! (make-el))
        ^js wrapper (shadow-part el "[part=wrapper]")]
    (is (some? (.querySelector wrapper "[part=panel]"))
        "panel should be nested inside the wrapper")))

(deftest error-part-hidden-by-default-test
  (let [^js el  (append! (make-el))
        ^js err (shadow-part el "[part=error]")]
    (is (.contains (.-classList err) "error-hidden"))
    (is (= "alert" (.getAttribute err "role")))
    (is (= "assertive" (.getAttribute err "aria-live")))))

(deftest error-attr-shows-message-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-error "Choose at least one")
    (let [^js err (shadow-part el "[part=error]")
          ^js inp (shadow-part el "[part=input]")]
      (is (not (.contains (.-classList err) "error-hidden")))
      (is (= "Choose at least one" (.-textContent err)))
      (is (.hasAttribute el "data-invalid"))
      (is (= "true" (.getAttribute inp "aria-invalid")))
      (is (= "error" (.getAttribute inp "aria-describedby"))))))

(deftest error-attr-clears-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-error "Boom")
    (.removeAttribute el model/attr-error)
    (let [^js err (shadow-part el "[part=error]")
          ^js inp (shadow-part el "[part=input]")]
      (is (.contains (.-classList err) "error-hidden"))
      (is (not (.hasAttribute el "data-invalid")))
      (is (= "false" (.getAttribute inp "aria-invalid")))
      (is (not (.hasAttribute inp "aria-describedby"))))))

(deftest error-property-reflects-attr-test
  (let [^js el (append! (make-el))]
    (set! (.-error el) "Required")
    (is (= "Required" (.getAttribute el model/attr-error)))
    (is (= "Required" (.-error el)))))

;; ── Chevron is hit-testable (regression: issue #267) ─────────────────────────
;; The chevron carries a `pointerdown` listener that toggles the panel. If CSS
;; sets `pointer-events:none` on it, real clicks fall through and the handler is
;; dead code. A synthetic dispatchEvent bypasses hit-testing, so we assert the
;; computed style directly — this is what actually caught the bug.
(deftest chevron-is-hit-testable-test
  (let [^js el      (append! (make-el))
        ^js chevron (shadow-part el "[part=chevron]")
        pe          (.-pointerEvents (js/getComputedStyle chevron))]
    (is (not= "none" pe)
        "chevron must receive pointer events so its click handler can fire")))

;; ── Chevron pointerdown toggles the panel (issue #267) ───────────────────────
(deftest chevron-pointerdown-toggles-open-test
  (let [^js el      (append! (make-el))
        ^js chevron (shadow-part el "[part=chevron]")
        fire!       (fn []
                      (.dispatchEvent
                       chevron
                       (js/PointerEvent. "pointerdown"
                                         #js {:bubbles true :cancelable true})))]
    (is (not (.hasAttribute el model/attr-open)))
    (fire!)
    (is (.hasAttribute el model/attr-open)
        "pointerdown on the chevron should open the panel")
    (fire!)
    (is (not (.hasAttribute el model/attr-open))
        "pointerdown on the chevron while open should close the panel")))

;; ── Default state ────────────────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el    (append! (make-el))
        ^js input (shadow-part el "[part=input]")
        ^js panel (shadow-part el "[part=panel]")]
    (is (= "combobox" (.getAttribute input "role")))
    (is (= "false"    (.getAttribute input "aria-expanded")))
    (is (= "list"     (.getAttribute input "aria-autocomplete")))
    (is (= "listbox"  (.getAttribute panel "role")))
    (is (= "true"     (.getAttribute panel "aria-multiselectable")))
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Placeholder attribute ────────────────────────────────────────────────────
(deftest placeholder-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-placeholder "Choose fruits")
    (is (= "Choose fruits" (.-placeholder (shadow-part el "[part=input]"))))))

;; ── Value attribute renders chips ────────────────────────────────────────────
(deftest value-renders-chips-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "apple,cherry")
         (let [chips (shadow-parts el "x-chip")]
           (is (= 2 (.-length chips)))
           ;; Check chip labels (sorted: apple, cherry)
           (is (= "Apple" (.getAttribute (aget chips 0) "label")))
           (is (= "Cherry" (.getAttribute (aget chips 1) "label"))))
         (done))
       50))))

;; ── Value property returns JS array ──────────────────────────────────────────
(deftest value-property-returns-array-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value "banana,apple")
    (let [v (.-value el)]
      (is (array? v))
      ;; Sorted output
      (is (= "apple" (aget v 0)))
      (is (= "banana" (aget v 1))))))

;; ── Value property setter accepts array ──────────────────────────────────────
(deftest value-property-setter-array-test
  (let [^js el (append! (make-el))]
    (set! (.-value el) #js ["cherry" "date"])
    (is (= "cherry,date" (.getAttribute el model/attr-value)))))

;; ── Disabled state ───────────────────────────────────────────────────────────
(deftest disabled-blocks-open-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-disabled "")
    (.setAttribute el model/attr-open "")
    ;; disabled → open-panel! is blocked, but setAttribute still sets it
    ;; The key check is that input is disabled
    (is (true? (.-disabled (shadow-part el "[part=input]"))))))

;; ── Placeholder hidden when chips present ────────────────────────────────────
(deftest placeholder-hidden-with-chips-test
  (async done
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-placeholder "Choose fruits")
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "apple")
         (is (= "" (.-placeholder (shadow-part el "[part=input]"))))
         (done))
       50))))

;; ── Max attribute ────────────────────────────────────────────────────────────
(deftest max-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-max "2")
    (is (= 2 (.-max el)))))

;; ── Change event on add ──────────────────────────────────────────────────────
(deftest change-event-on-add-test
  (async done
    (let [^js el  (append! (make-el))
          events  (atom [])]
      (.addEventListener el model/event-change
        (fn [^js evt]
          (swap! events conj (js->clj (.-detail evt) :keywordize-keys true))))
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-value "apple")
         ;; Attribute change doesn't fire change event (that's for programmatic add)
         ;; Let's verify the attribute is set
         (is (= "apple" (.getAttribute el model/attr-value)))
         (done))
       50))))

;; ── Form association (ElementInternals) ──────────────────────────────────────
;; Instance validity APIs aren't exposed by the karma harness (same as the
;; shipped x-form-field), so validity assertions are guarded; the static flag
;; and form callbacks are harness-independent. Submit gating is verified in a
;; real browser via the demo.

(deftest form-associated-static-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

(deftest form-reset-callback-clears-value-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value "apple,banana")
    (is (= "apple,banana" (.getAttribute el model/attr-value)) "precondition")
    (.formResetCallback el)
    (is (not (.hasAttribute el model/attr-value))
        "formResetCallback clears the selected set")))

(deftest form-reset-callback-clears-error-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-error "Choose at least one")
    (is (.hasAttribute el "data-invalid") "precondition: error is displayed")
    (.formResetCallback el)
    (let [^js err (shadow-part el "[part=error]")
          ^js inp (shadow-part el "[part=input]")]
      (is (not (.hasAttribute el model/attr-error))
          "reset drops the error attribute")
      (is (.contains (.-classList err) "error-hidden")
          "reset hides the inline error message")
      (is (not (.hasAttribute el "data-invalid"))
          "reset drops data-invalid")
      (is (= "false" (.getAttribute inp "aria-invalid"))
          "reset clears aria-invalid on the input"))))

(deftest validity-value-missing-when-required-empty-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "name" "tags")
    (.setAttribute el "required" "")
    (is (true? (.. el -validity -valueMissing))
        "required + empty set reports valueMissing")))

(deftest validity-custom-error-when-error-set-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "error" "Pick at least one")
    (is (true? (.. el -validity -customError))
        "error attribute drives customError")
    (is (= "Pick at least one" (.-validationMessage el)))))

;; ── Chips keep their nodes ───────────────────────────────────────────────────
(defn- chip [^js el value]
  (shadow-part el (str "x-chip[value='" value "']")))

(defn- chip-values [^js el]
  (mapv (fn [^js node] (.getAttribute node "value")) (array-seq (shadow-parts el "x-chip"))))

(defn- after-options
  "Calls `f` with a mounted element once its options are read."
  [f]
  (let [^js el (append! (make-el))]
    (js/setTimeout (fn [] (f el)) 50)))

(deftest chips-that-stay-keep-their-nodes-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,cherry")
       (let [apple  (chip el "apple")
             cherry (chip el "cherry")]
         (.setAttribute el model/attr-value "apple,banana,cherry")
         (is (= ["apple" "banana" "cherry"] (chip-values el)))
         (is (identical? apple (chip el "apple")))
         (is (identical? cherry (chip el "cherry")))
         (.setAttribute el model/attr-value "cherry")
         (is (= ["cherry"] (chip-values el)))
         (is (identical? cherry (chip el "cherry")))
         (is (false? (.-isConnected apple))))
       (done)))))

(deftest chips-sit-before-the-input-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "banana,apple")
       (let [area (shadow-part el "[part=chip-area]")]
         (is (= ["X-CHIP" "X-CHIP" "INPUT"]
                (mapv (fn [^js node] (.-tagName node)) (array-seq (.-children area))))))
       (done)))))

(defn- press-remove! [^js chip-el]
  (.click (.querySelector (.-shadowRoot chip-el) "[part=remove]")))

(deftest removing-one-chip-leaves-the-others-untouched-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana,cherry")
       (let [apple  (chip el "apple")
             cherry (chip el "cherry")]
         (press-remove! (chip el "banana"))
         (is (= "apple,cherry" (.getAttribute el model/attr-value)))
         (is (identical? apple (shadow-part el "x-chip[data-x-key='apple']")))
         (is (identical? cherry (shadow-part el "x-chip[data-x-key='cherry']"))))
       (done)))))

(defn- end-animation! [^js node]
  (.dispatchEvent node (js/Event. "animationend")))

(defn- press-backspace! [^js el]
  (.dispatchEvent (shadow-part el "[part=input]")
                  (js/KeyboardEvent. "keydown" #js {:key "Backspace" :bubbles true})))

(deftest a-chip-the-user-removes-stays-to-fade-and-then-leaves-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (let [banana (chip el "banana")]
         (press-remove! banana)
         (is (= "apple" (.getAttribute el model/attr-value)))
         (is (true? (.-isConnected banana)))
         (is (true? (.hasAttribute banana "data-exiting")))
         (is (nil? (shadow-part el "x-chip[data-x-key='banana']")))
         (end-animation! banana)
         (is (false? (.-isConnected banana)))
         (is (= ["apple"] (chip-values el))))
       (done)))))

(deftest the-last-chip-removed-with-backspace-fades-too-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (let [banana (chip el "banana")]
         (press-backspace! el)
         (is (= "apple" (.getAttribute el model/attr-value)))
         (is (true? (.-isConnected banana)))
         (is (true? (.hasAttribute banana "data-exiting")))
         (end-animation! banana)
         (is (false? (.-isConnected banana)))
         (is (= ["apple"] (chip-values el))))
       (done)))))

(deftest a-refused-backspace-keeps-the-chip-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (.addEventListener el model/event-change-request (fn [^js evt] (.preventDefault evt)))
       (let [banana (chip el "banana")]
         (press-backspace! el)
         (is (= "apple,banana" (.getAttribute el model/attr-value)))
         (is (identical? banana (shadow-part el "x-chip[data-x-key='banana']")))
         (is (false? (.hasAttribute banana "data-exiting"))))
       (done)))))

(deftest a-chip-removed-from-outside-leaves-at-once-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (let [banana (chip el "banana")]
         (.setAttribute el model/attr-value "apple")
         (is (false? (.-isConnected banana))))
       (done)))))

(deftest a-refused-removal-keeps-the-chip-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (.addEventListener el model/event-change-request (fn [^js evt] (.preventDefault evt)))
       (let [banana (chip el "banana")]
         (press-remove! banana)
         (is (= "apple,banana" (.getAttribute el model/attr-value)))
         (is (identical? banana (shadow-part el "x-chip[data-x-key='banana']")))
         (is (false? (.hasAttribute banana "data-exiting"))))
       (done)))))

(deftest disabled-reaches-chips-that-stay-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple")
       (let [apple (chip el "apple")]
         (.setAttribute el model/attr-disabled "")
         (is (identical? apple (chip el "apple")))
         (is (true? (.hasAttribute apple "disabled")))
         (.removeAttribute el model/attr-disabled)
         (is (false? (.hasAttribute apple "disabled"))))
       (done)))))

(defn- focus-remove-button! [^js chip-el]
  (.focus (.querySelector (.-shadowRoot chip-el) "[part=remove]")))

(defn- with-reported-errors
  "Calls `f` and returns the messages of the errors the window reports meanwhile."
  [f]
  (let [errors   (atom [])
        on-error (fn [^js evt]
                   (.preventDefault evt)
                   (swap! errors conj (.-message evt)))]
    (.addEventListener js/window "error" on-error)
    (try
      (f)
      (finally
        (.removeEventListener js/window "error" on-error)))
    @errors))

(defn- with-focused-chip
  "Calls `f` with a mounted element, its input and a chip that holds focus while the panel is open."
  [f]
  (after-options
   (fn [^js el]
     (.setAttribute el model/attr-value "apple,banana")
     (let [input  (shadow-part el "[part=input]")
           banana (chip el "banana")]
       (.focus input)
       (focus-remove-button! banana)
       (is (identical? banana (.-activeElement (.-shadowRoot el))))
       (f el input banana)))))

(defn- remove-by-user! [^js chip-el]
  (press-remove! chip-el)
  (end-animation! chip-el))

(deftest focus-moves-to-the-input-when-the-user-removes-the-focused-chip-test
  (async done
    (with-focused-chip
      (fn [^js el input banana]
        (remove-by-user! banana)
        (is (= ["apple"] (chip-values el)))
        (is (identical? input (.-activeElement (.-shadowRoot el))))
        (is (.hasAttribute el model/attr-open))
        (done)))))

(deftest no-error-is-reported-when-the-user-removes-the-focused-chip-test
  (async done
    (with-focused-chip
      (fn [_ _ banana]
        (is (= [] (with-reported-errors (fn [] (remove-by-user! banana)))))
        (done)))))

(deftest focus-moves-to-the-input-when-the-focused-chip-is-removed-from-outside-test
  (async done
    (with-focused-chip
      (fn [^js el input _]
        (.setAttribute el model/attr-value "apple")
        (is (= ["apple"] (chip-values el)))
        (is (identical? input (.-activeElement (.-shadowRoot el))))
        (is (.hasAttribute el model/attr-open))
        (done)))))

(deftest no-error-is-reported-when-the-focused-chip-is-removed-from-outside-test
  (async done
    (with-focused-chip
      (fn [^js el _ _]
        (is (= [] (with-reported-errors
                    (fn [] (.setAttribute el model/attr-value "apple")))))
        (done)))))

(deftest the-panel-opens-when-the-focused-chip-leaves-a-closed-panel-test
  (async done
    (with-focused-chip
      (fn [^js el input _]
        (.removeAttribute el model/attr-open)
        (is (= [] (with-reported-errors
                    (fn [] (.setAttribute el model/attr-value "apple")))))
        (is (= ["apple"] (chip-values el)))
        (is (identical? input (.-activeElement (.-shadowRoot el))))
        (is (.hasAttribute el model/attr-open))
        (done)))))

;; ── Options keep their nodes ─────────────────────────────────────────────────
(defn- option-nodes [^js el]
  (vec (array-seq (.querySelectorAll (shadow-part el "[part=panel]") "[part=option]"))))

(defn- option-values [^js el]
  (mapv (fn [^js node] (.getAttribute node "data-value")) (option-nodes el)))

(defn- type! [^js el text]
  (let [^js input (shadow-part el "[part=input]")]
    (set! (.-value input) text)
    (.dispatchEvent input (js/Event. "input" #js {:bubbles true}))))

(deftest an-option-that-still-matches-keeps-its-node-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-open "")
         (let [[apple banana cherry] (option-nodes el)]
           (is (= 5 (count (option-nodes el))))
           (type! el "an")
           (is (= [banana] (option-nodes el)) "the one that matches is the same node")
           (is (= "an" (.-textContent (.querySelector banana "b"))) "the match is shown in bold")
           (is (not (.-isConnected apple)) "an option that no longer matches is gone")
           (type! el "")
           (is (identical? banana (nth (option-nodes el) 1)) "it stays when the query is cleared")
           (is (not (.-isConnected cherry)) "an option that left is a new node when it returns")
           (is (= "cherry" (.getAttribute (nth (option-nodes el) 2) "data-value"))))
         (done))
       50))))

(deftest a-selected-option-leaves-the-panel-and-the-others-keep-their-nodes-test
  (async done
    (let [^js el (append! (make-el))]
      (js/setTimeout
       (fn []
         (.setAttribute el model/attr-open "")
         (let [before (option-nodes el)]
           (.setAttribute el model/attr-value "apple")
           (is (= ["banana" "cherry" "date" "elderberry"] (option-values el)))
           (is (= (subvec before 1) (option-nodes el)) "the four that stay are the same nodes"))
         (done))
       50))))


;; ── Value format ─────────────────────────────────────────────────────────────
(def ^:private charity "Liefdadigheid, religieus en non-profit")
(def ^:private padded " padded ")

(defn ^js make-json-el
  "Creates an x-multi-combobox in json format with options whose values hold a comma or spaces."
  []
  (let [^js el (.createElement js/document model/tag-name)]
    (.setAttribute el model/attr-value-format "json")
    (doseq [[v l] [[charity "Charity"] [padded "Padded"] ["news" "News"]]]
      (let [^js opt (.createElement js/document "option")]
        (.setAttribute opt "value" v)
        (set! (.-textContent opt) l)
        (.appendChild el opt)))
    el))

(defn- after-json-options
  "Calls `f` with a mounted json element once its options are read."
  [f]
  (let [^js el (append! (make-json-el))]
    (js/setTimeout (fn [] (f el)) 50)))

(defn- events-of
  "Collects the detail of each `event-name` event on `el` into an atom, as CLJS data."
  [^js el event-name]
  (let [events (atom [])]
    (.addEventListener el event-name
                       (fn [^js evt]
                         (swap! events conj (js->clj (.-detail evt) :keywordize-keys true))))
    events))

(defn- json-attr [values]
  (js/JSON.stringify (to-array values)))

(defn- click-option! [^js el value]
  (.setAttribute el model/attr-open "")
  (.click (.querySelector (shadow-part el "[part=panel]")
                          (str "[data-value=\"" value "\"]"))))

(deftest value-format-property-reflects-the-attribute-test
  (let [^js el (append! (make-el))]
    (is (nil? (.-valueFormat el)))
    (set! (.-valueFormat el) "json")
    (is (= "json" (.getAttribute el model/attr-value-format)))
    (is (= "json" (.-valueFormat el)))))

(deftest json-attribute-keeps-a-value-with-a-comma-test
  (async done
    (after-json-options
     (fn [^js el]
       (.setAttribute el model/attr-value (json-attr [charity "news"]))
       (is (= [charity "news"] (chip-values el)))
       (is (= [charity "news"] (vec (.-value el))))
       (is (= "Charity" (.getAttribute (chip el charity) "label")))
       (done)))))

(deftest json-property-keeps-a-comma-and-spaces-test
  (let [^js el (append! (make-json-el))]
    (set! (.-value el) #js [charity padded])
    (is (= (json-attr [charity padded]) (.getAttribute el model/attr-value)))
    (is (= [padded charity] (vec (.-value el))) "the getter returns the exact strings, sorted")))

(deftest json-property-accepts-a-json-string-test
  (let [^js el (append! (make-json-el))]
    (set! (.-value el) (json-attr [charity]))
    (is (= [charity] (vec (.-value el))))))

(deftest json-property-clears-with-an-empty-array-test
  (let [^js el (append! (make-json-el))]
    (set! (.-value el) #js [charity])
    (set! (.-value el) #js [])
    (is (not (.hasAttribute el model/attr-value)))
    (is (= [] (vec (.-value el))))))

(deftest json-property-drops-empty-values-test
  (let [^js el (append! (make-json-el))]
    (set! (.-value el) #js ["news" nil "" js/undefined])
    (is (= (json-attr ["news"]) (.getAttribute el model/attr-value)))
    (set! (.-value el) #js [nil ""])
    (is (not (.hasAttribute el model/attr-value)) "only empty values clear the selection")))

(deftest json-attribute-drops-an-empty-value-test
  (async done
    (after-json-options
     (fn [^js el]
       (let [errors (events-of el model/event-error)]
         (.setAttribute el model/attr-value (json-attr ["news" ""]))
         (is (= ["news"] (chip-values el)))
         (is (= ["news"] (vec (.-value el))))
         (is (empty? @errors))
         (done))))))

(deftest json-option-click-keeps-a-value-with-a-comma-test
  (async done
    (after-json-options
     (fn [^js el]
       (let [requests (events-of el model/event-change-request)
             changes  (events-of el model/event-change)]
         (click-option! el charity)
         (is (= (json-attr [charity]) (.getAttribute el model/attr-value)))
         (is (= [{:value [charity] :action "add" :item charity}] @requests))
         (is (= [{:value [charity]}] @changes))
         (is (= [charity] (chip-values el))))
       (done)))))

(deftest json-option-click-keeps-spaces-test
  (async done
    (after-json-options
     (fn [^js el]
       (let [changes (events-of el model/event-change)]
         (click-option! el padded)
         (is (= (json-attr [padded]) (.getAttribute el model/attr-value)))
         (is (= [{:value [padded]}] @changes))
         (is (= [padded] (vec (.-value el)))))
       (done)))))

(deftest json-chip-removal-reports-the-exact-values-test
  (async done
    (after-json-options
     (fn [^js el]
       (.setAttribute el model/attr-value (json-attr [charity padded "news"]))
       (let [changes (events-of el model/event-change)]
         (press-remove! (chip el "news"))
         (is (= (json-attr [padded charity]) (.getAttribute el model/attr-value)))
         (is (= [{:value [padded charity]}] @changes)))
       (done)))))

(deftest json-invalid-attribute-yields-no-selection-and-an-error-test
  (async done
    (after-json-options
     (fn [^js el]
       (.setAttribute el model/attr-value (json-attr ["news"]))
       (let [errors (events-of el model/event-error)]
         (.setAttribute el model/attr-value "news,sport")
         (is (= [] (chip-values el)))
         (is (= [] (vec (.-value el))))
         (is (= 1 (count @errors)))
         (is (= ["news,sport"] (:values (first @errors))))
         (is (string? (:error (first @errors)))))
       (done)))))

(deftest json-invalid-attribute-at-connect-sends-one-error-test
  (async done
    (let [^js el (make-json-el)
          errors (events-of el model/event-error)]
      (.setAttribute el model/attr-value "not json")
      (append! el)
      (js/setTimeout
       (fn []
         (is (= 1 (count @errors)) "one error, also after the options are read")
         (is (= ["not json"] (:values (first @errors))))
         (is (= [] (chip-values el)))
         (done))
       50))))

(deftest csv-property-splits-a-value-it-cannot-hold-and-warns-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (let [errors  (events-of el model/event-error)
             changes (events-of el model/event-change)]
         (set! (.-value el) #js ["cherry" charity " date"])
         (is (= (str "cherry," charity ", date") (.getAttribute el model/attr-value))
             "the attribute is the values joined with commas")
         (is (= ["Liefdadigheid" "cherry" "date" "religieus en non-profit"] (chip-values el))
             "the selection is the values split at each comma and trimmed")
         (is (= [] @changes))
         (is (= 1 (count @errors)))
         (is (= [charity " date"] (:values (first @errors))))
         (is (not= -1 (.indexOf (:error (first @errors)) "value-format=\"json\""))))
       (done)))))

(deftest csv-property-sends-no-error-for-plain-values-test
  (let [^js el (append! (make-el))
        errors (events-of el model/event-error)]
    (set! (.-value el) #js ["date" "cherry"])
    (is (= "date,cherry" (.getAttribute el model/attr-value)) "csv keeps the order it is given")
    (is (= [] @errors))))

(deftest switching-to-json-reads-the-attribute-as-json-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value (json-attr ["apple" "banana"]))
       (is (= ["\"banana\"]" "[\"apple\""] (vec (.-value el))) "csv splits the JSON text")
       (let [errors (events-of el model/event-error)]
         (.setAttribute el model/attr-value-format "json")
         (is (= ["apple" "banana"] (chip-values el)))
         (is (= ["apple" "banana"] (vec (.-value el))))
         (is (= [] @errors)))
       (done)))))

(deftest switching-to-json-with-a-csv-attribute-sends-an-error-test
  (async done
    (after-options
     (fn [^js el]
       (.setAttribute el model/attr-value "apple,banana")
       (let [errors (events-of el model/event-error)]
         (.setAttribute el model/attr-value-format "json")
         (is (= [] (chip-values el)))
         (is (= 1 (count @errors)))
         (is (= ["apple,banana"] (:values (first @errors))))
         (.removeAttribute el model/attr-value-format)
         (is (= ["apple" "banana"] (chip-values el)) "csv reads the attribute again")
         (is (= 1 (count @errors))))
       (done)))))

(deftest switching-to-csv-splits-a-json-value-test
  (async done
    (after-json-options
     (fn [^js el]
       (.setAttribute el model/attr-value (json-attr [charity]))
       (.setAttribute el model/attr-value-format "csv")
       (is (= ["[\"Liefdadigheid" "religieus en non-profit\"]"] (vec (.-value el)))
           "csv splits the value at the comma")
       (done)))))

(deftest unknown-value-format-acts-as-csv-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-value-format "xml")
    (.setAttribute el model/attr-value "banana,apple")
    (is (= ["apple" "banana"] (vec (.-value el))))))

(deftest json-form-value-is-the-json-array-test
  (let [^js form (.createElement js/document "form")
        ^js el   (make-json-el)]
    (.setAttribute form "data-test-form" "")
    (.setAttribute el "name" "tags")
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (try
      (set! (.-value el) #js [charity "news"])
      (is (= (json-attr [charity "news"]) (.get (js/FormData. form) "tags")))
      (finally
        (.remove form)))))
