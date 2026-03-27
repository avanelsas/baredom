(ns baredom.components.x-text-area.x-text-area-test
  (:require [cljs.test :refer [deftest is testing use-fixtures async]]
            [baredom.components.x-text-area.model :as model]
            [baredom.components.x-text-area.x-text-area :as sut]))

;; ---------------------------------------------------------------------------
;; Test helpers
;; ---------------------------------------------------------------------------
(defn- cleanup-dom! []
  (doseq [^js el (array-seq (.querySelectorAll js/document model/tag-name))]
    (.remove el)))

(use-fixtures :each
  {:after cleanup-dom!})

(defn- mount!
  ([]
   (let [^js el (.createElement js/document model/tag-name)]
     (.appendChild js/document.body el)
     el))
  ([attrs]
   (let [^js el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs]
       (.setAttribute el k v))
     (.appendChild js/document.body el)
     el)))

(defn- shadow-q [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn- get-textarea [^js el]
  (shadow-q el "[part=textarea]"))

;; ---------------------------------------------------------------------------
;; Registration
;; ---------------------------------------------------------------------------
(deftest registration-test
  (testing "element is registered before tests run"
    (sut/init!)
    (is (some? (.get js/customElements model/tag-name)))))

;; ---------------------------------------------------------------------------
;; Shadow DOM structure
;; ---------------------------------------------------------------------------
(deftest shadow-structure-test
  (testing "all parts exist after connection"
    (sut/init!)
    (let [^js el (mount!)]
      (is (some? (.-shadowRoot el)))
      (is (some? (shadow-q el "[part=field]")))
      (is (some? (shadow-q el "[part=label]")))
      (is (some? (shadow-q el "[part=textarea-wrapper]")))
      (is (some? (shadow-q el "[part=textarea]")))
      (is (some? (shadow-q el "[part=hint]")))
      (is (some? (shadow-q el "[part=error]"))))))

;; ---------------------------------------------------------------------------
;; Default state
;; ---------------------------------------------------------------------------
(deftest default-state-test
  (testing "textarea has expected defaults"
    (sut/init!)
    (let [^js el       (mount!)
          ^js textarea (get-textarea el)]
      (is (= ""  (.-name textarea)))
      (is (= ""  (.-placeholder textarea)))
      (is (false? (.-disabled textarea)))
      (is (false? (.-readOnly textarea)))
      (is (false? (.-required textarea)))
      (is (= 3   (.-rows textarea)))))
  (testing "label, hint, error are hidden by default"
    (sut/init!)
    (let [^js el      (mount!)
          ^js label   (shadow-q el "[part=label]")
          ^js hint    (shadow-q el "[part=hint]")
          ^js error   (shadow-q el "[part=error]")]
      (is (.contains (.-classList label) "label-hidden"))
      (is (.contains (.-classList hint)  "hint-hidden"))
      (is (.contains (.-classList error) "error-hidden")))))

;; ---------------------------------------------------------------------------
;; Attribute → DOM
;; ---------------------------------------------------------------------------
(deftest label-attribute-test
  (testing "label attribute renders label text"
    (sut/init!)
    (let [^js el    (mount! {"label" "Message"})
          ^js label (shadow-q el "[part=label]")]
      (is (= "Message" (.-textContent label)))
      (is (not (.contains (.-classList label) "label-hidden"))))))

(deftest hint-attribute-test
  (testing "hint attribute shows hint text"
    (sut/init!)
    (let [^js el   (mount! {"hint" "Max 500 chars"})
          ^js hint (shadow-q el "[part=hint]")]
      (is (= "Max 500 chars" (.-textContent hint)))
      (is (not (.contains (.-classList hint) "hint-hidden"))))))

(deftest error-attribute-test
  (testing "error attribute shows error and sets data-invalid"
    (sut/init!)
    (let [^js el    (mount! {"error" "Required"})
          ^js error (shadow-q el "[part=error]")
          ^js ta    (get-textarea el)]
      (is (= "Required" (.-textContent error)))
      (is (not (.contains (.-classList error) "error-hidden")))
      (is (.hasAttribute el "data-invalid"))
      (is (= "true" (.getAttribute ta "aria-invalid"))))))

(deftest error-cleared-test
  (testing "clearing error removes data-invalid"
    (sut/init!)
    (let [^js el (mount! {"error" "Bad"})]
      (is (.hasAttribute el "data-invalid"))
      (.removeAttribute el "error")
      (is (not (.hasAttribute el "data-invalid"))))))

(deftest rows-attribute-test
  (testing "rows attribute sets textarea rows"
    (sut/init!)
    (let [^js el (mount! {"rows" "6"})
          ^js ta (get-textarea el)]
      (is (= 6 (.-rows ta))))))

(deftest maxlength-attribute-test
  (testing "maxlength attribute sets textarea maxlength"
    (sut/init!)
    (let [^js el (mount! {"maxlength" "200"})
          ^js ta (get-textarea el)]
      (is (= "200" (.getAttribute ta "maxlength"))))))

(deftest resize-attribute-test
  (testing "resize attribute sets CSS custom property on host"
    (sut/init!)
    (let [^js el (mount! {"resize" "none"})]
      (is (= "none" (.getPropertyValue (.-style el) "--x-text-area-resize"))))))

;; ---------------------------------------------------------------------------
;; Properties
;; ---------------------------------------------------------------------------
(deftest value-property-test
  (testing "value getter returns textarea.value"
    (sut/init!)
    (let [^js el (mount!)]
      (is (= "" (.-value el)))))
  (testing "value setter updates textarea and attribute"
    (sut/init!)
    (let [^js el (mount!)
          ^js ta (get-textarea el)]
      (set! (.-value el) "Hello")
      (is (= "Hello" (.-value ta)))
      (is (= "Hello" (.getAttribute el "value"))))))

(deftest disabled-property-test
  (testing "disabled getter reflects attribute presence"
    (sut/init!)
    (let [^js el (mount!)]
      (is (false? (.-disabled el)))
      (set! (.-disabled el) true)
      (is (true? (.-disabled el)))
      (is (.hasAttribute el "disabled")))))

(deftest rows-property-test
  (testing "rows property reflects attribute"
    (sut/init!)
    (let [^js el (mount!)]
      (is (= 3 (.-rows el)))
      (set! (.-rows el) 8)
      (is (= 8 (.-rows el)))
      (is (= "8" (.getAttribute el "rows"))))))

(deftest readonly-property-test
  (testing "readOnly property reflects attribute"
    (sut/init!)
    (let [^js el (mount!)]
      (is (false? (.-readOnly el)))
      (set! (.-readOnly el) true)
      (is (true? (.-readOnly el))))))

;; ---------------------------------------------------------------------------
;; ARIA
;; ---------------------------------------------------------------------------
(deftest aria-required-test
  (testing "aria-required set when required present"
    (sut/init!)
    (let [^js el (mount! {"required" ""})
          ^js ta (get-textarea el)]
      (is (= "true" (.getAttribute ta "aria-required"))))))

(deftest aria-describedby-test
  (testing "aria-describedby includes error id when error present"
    (sut/init!)
    (let [^js el (mount! {"error" "Bad"})
          ^js ta (get-textarea el)]
      (is (= "error" (.getAttribute ta "aria-describedby")))))
  (testing "aria-describedby includes both ids when hint and error present"
    (sut/init!)
    (let [^js el (mount! {"hint" "Help" "error" "Bad"})
          ^js ta (get-textarea el)]
      (is (= "hint error" (.getAttribute ta "aria-describedby")))))
  (testing "aria-describedby absent when no hint or error"
    (sut/init!)
    (let [^js el (mount!)
          ^js ta (get-textarea el)]
      (is (nil? (.getAttribute ta "aria-describedby"))))))

;; ---------------------------------------------------------------------------
;; Events
;; ---------------------------------------------------------------------------
(deftest input-event-test
  (testing "x-text-area-input fires on input with name and value"
    (sut/init!)
    (async done
      (let [^js el  (mount! {"name" "bio"})
            ^js ta  (get-textarea el)
            received (atom nil)]
        (.addEventListener el model/event-input
                           (fn [^js e] (reset! received (.-detail e))))
        (set! (.-value ta) "typed")
        (.dispatchEvent ta (js/Event. "input" #js {:bubbles true}))
        (js/setTimeout
         (fn []
           (is (some? @received))
           (is (= "bio"   (.-name @received)))
           (is (= "typed" (.-value @received)))
           (done))
         0)))))

(deftest change-event-test
  (testing "x-text-area-change fires on change with name and value"
    (sut/init!)
    (async done
      (let [^js el  (mount! {"name" "notes"})
            ^js ta  (get-textarea el)
            received (atom nil)]
        (.addEventListener el model/event-change
                           (fn [^js e] (reset! received (.-detail e))))
        (set! (.-value ta) "final text")
        (.dispatchEvent ta (js/Event. "change" #js {:bubbles true}))
        (js/setTimeout
         (fn []
           (is (some? @received))
           (is (= "notes"      (.-name @received)))
           (is (= "final text" (.-value @received)))
           (done))
         0)))))

;; ---------------------------------------------------------------------------
;; Disabled / readonly
;; ---------------------------------------------------------------------------
(deftest disabled-state-test
  (testing "disabled attribute disables textarea"
    (sut/init!)
    (let [^js el (mount! {"disabled" ""})
          ^js ta (get-textarea el)]
      (is (true? (.-disabled ta))))))

(deftest readonly-state-test
  (testing "readonly attribute makes textarea readonly"
    (sut/init!)
    (let [^js el (mount! {"readonly" ""})
          ^js ta (get-textarea el)]
      (is (true? (.-readOnly ta))))))

;; ---------------------------------------------------------------------------
;; Form reset
;; ---------------------------------------------------------------------------
(deftest form-reset-test
  (testing "formResetCallback clears textarea value"
    (sut/init!)
    (let [^js el (mount! {"value" "preset"})
          ^js ta (get-textarea el)]
      (is (= "preset" (.-value ta)))
      ;; Simulate form reset by calling formResetCallback directly
      (.formResetCallback el)
      (is (= "" (.-value ta))))))

;; ---------------------------------------------------------------------------
;; Reconnect — no listener doubling
;; ---------------------------------------------------------------------------
(deftest reconnect-no-listener-doubling-test
  (testing "events fire once after reconnect"
    (sut/init!)
    (async done
      (let [^js el   (mount!)
            ^js ta   (get-textarea el)
            counter  (atom 0)]
        (.addEventListener el model/event-input (fn [_] (swap! counter inc)))
        ;; Disconnect then reconnect
        (.remove el)
        (.appendChild js/document.body el)
        ;; Get fresh textarea reference after reconnect
        (let [^js ta2 (get-textarea el)]
          (set! (.-value ta2) "x")
          (.dispatchEvent ta2 (js/Event. "input" #js {:bubbles true}))
          (js/setTimeout
           (fn []
             (is (= 1 @counter))
             (done))
           0))))))
