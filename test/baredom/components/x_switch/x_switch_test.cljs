(ns baredom.components.x-switch.x-switch-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-switch.x-switch :as x]
            [baredom.components.x-switch.model    :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [^js node (.querySelectorAll js/document "form, fieldset")]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el    [] (.createElement js/document model/tag-name))
(defn ^js append!    [^js el] (.appendChild (.-body js/document) el) el)
(defn ^js shadow-part [^js el sel] (.querySelector (.-shadowRoot el) sel))

;; ── Registration ─────────────────────────────────────────────────────────────

(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))
      "x-switch should be registered"))

(deftest form-associated-test
  (let [^js cls (.get js/customElements model/tag-name)]
    (is (= true (.-formAssociated cls))
        "formAssociated static property must be true")))

;; ── Shadow DOM structure ──────────────────────────────────────────────────────

(deftest shadow-structure-test
  (let [el (append! (make-el))]
    (is (some? (.-shadowRoot el))                  "shadow root should exist")
    (is (some? (shadow-part el "[part=control]"))   "control button should exist")
    (is (some? (shadow-part el "[part=track]"))     "track span should exist")
    (is (some? (shadow-part el "[part=thumb]"))     "thumb span should exist")))

(deftest control-role-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (is (= "switch" (.getAttribute control "role"))
        "control must have role=switch")))

;; ── Default attribute state ───────────────────────────────────────────────────

(deftest default-aria-checked-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (is (= "false" (.getAttribute control "aria-checked"))
        "aria-checked should default to \"false\"")))

(deftest default-tabindex-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (is (= 0 (.-tabIndex control))
        "tabIndex should default to 0")))

;; ── Attribute → DOM updates ───────────────────────────────────────────────────

(deftest checked-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-checked "")
    (is (= "true" (.getAttribute control "aria-checked")))
    (is (.hasAttribute el "data-checked"))))

(deftest disabled-attr-updates-control-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-disabled "")
    (is (= "true" (.getAttribute control "aria-disabled")))
    (is (.hasAttribute control "disabled"))
    (is (= -1 (.-tabIndex control)))
    (is (.hasAttribute el "data-disabled"))))

(deftest readonly-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-readonly "")
    (is (= "true" (.getAttribute control "aria-readonly")))))

(deftest required-attr-updates-aria-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-required "")
    (is (= "true" (.getAttribute control "aria-required")))))

(deftest aria-label-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-label "Enable notifications")
    (is (= "Enable notifications" (.getAttribute control "aria-label")))))

(deftest aria-describedby-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-describedby "hint-3")
    (is (= "hint-3" (.getAttribute control "aria-describedby")))))

;; ── Properties ───────────────────────────────────────────────────────────────

(deftest boolean-property-checked-test
  (let [el (append! (make-el))]
    (set! (.-checked el) true)
    (is (.hasAttribute el model/attr-checked))
    (is (= true (.-checked el)))
    (set! (.-checked el) false)
    (is (not (.hasAttribute el model/attr-checked)))))

(deftest boolean-property-disabled-test
  (let [el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest boolean-property-readonly-test
  (let [el (append! (make-el))]
    (set! (.-readOnly el) true)
    (is (.hasAttribute el model/attr-readonly))
    (set! (.-readOnly el) false)
    (is (not (.hasAttribute el model/attr-readonly)))))

(deftest string-property-name-test
  (let [el (append! (make-el))]
    (set! (.-name el) "dark-mode")
    (is (= "dark-mode" (.getAttribute el model/attr-name)))))

(deftest string-property-value-test
  (let [el (append! (make-el))]
    (set! (.-value el) "enabled")
    (is (= "enabled" (.getAttribute el model/attr-value)))))

;; ── Toggle events (async) ─────────────────────────────────────────────────────

(deftest toggle-dispatches-change-request-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener
       el model/event-change-request
       (fn [^js ev]
         (reset! seen {:next-checked (.-nextChecked (.-detail ev))
                       :prev-checked (.-previousChecked (.-detail ev))
                       :value        (.-value (.-detail ev))})))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "change-request event should fire")
         (is (= false (:prev-checked @seen)))
         (is (= true  (:next-checked @seen)))
         (is (= "on"  (:value @seen)))
         (done))
       0))))

(deftest toggle-dispatches-change-after-request-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen {:checked (.-checked (.-detail ev))
                       :value   (.-value (.-detail ev))})))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (some? @seen) "change event should fire")
         (is (= true (:checked @seen)))
         (is (= "on" (:value @seen)))
         (done))
       0))))

(deftest toggle-can-be-cancelled-test
  (async done
    (let [el (append! (make-el))]
      (.addEventListener
       el model/event-change-request
       (fn [^js ev] (.preventDefault ev)))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "checked attr should NOT be set when change-request is cancelled")
         (done))
       0))))

(deftest disabled-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "disabled switch should not toggle")
         (done))
       0))))

(deftest readonly-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-readonly "")
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "readonly switch should not toggle")
         (done))
       0))))

(deftest toggle-off-when-checked-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.setAttribute el model/attr-checked "")
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen (.-checked (.-detail ev)))))
      (.click (shadow-part el "[part=control]"))
      (js/setTimeout
       (fn []
         (is (= false @seen) "checked switch should toggle off")
         (is (not (.hasAttribute el model/attr-checked)))
         (done))
       0))))

;; ── Keyboard toggle ──────────────────────────────────────────────────────────

(defn- dispatch-key! [^js target key]
  (.dispatchEvent target
                  (js/KeyboardEvent. "keydown"
                                     #js {:key key :bubbles true :cancelable true})))

(deftest keyboard-space-toggles-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen (.-checked (.-detail ev)))))
      (dispatch-key! (shadow-part el "[part=control]") " ")
      (js/setTimeout
       (fn []
         (is (= true @seen) "Space should toggle switch on")
         (is (.hasAttribute el model/attr-checked))
         (done))
       0))))

(deftest keyboard-enter-toggles-test
  (async done
    (let [el   (append! (make-el))
          seen (atom nil)]
      (.addEventListener
       el model/event-change
       (fn [^js ev]
         (reset! seen (.-checked (.-detail ev)))))
      (dispatch-key! (shadow-part el "[part=control]") "Enter")
      (js/setTimeout
       (fn []
         (is (= true @seen) "Enter should toggle switch on")
         (is (.hasAttribute el model/attr-checked))
         (done))
       0))))

(deftest keyboard-disabled-blocks-toggle-test
  (async done
    (let [el (append! (make-el))]
      (.setAttribute el model/attr-disabled "")
      (dispatch-key! (shadow-part el "[part=control]") " ")
      (js/setTimeout
       (fn []
         (is (not (.hasAttribute el model/attr-checked))
             "Space on disabled switch should not toggle")
         (done))
       0))))

;; ── Attribute forwarding (aria-labelledby) ───────────────────────────────────

(deftest aria-labelledby-forwarded-test
  (let [el      (append! (make-el))
        control (shadow-part el "[part=control]")]
    (.setAttribute el model/attr-aria-labelledby "lbl-7")
    (is (= "lbl-7" (.getAttribute control "aria-labelledby")))))

;; ── Form-associated callbacks ────────────────────────────────────────────────

(deftest form-reset-clears-checked-test
  (let [^js form (js/document.createElement "form")
        el       (make-el)]
    (.appendChild form el)
    (.appendChild (.-body js/document) form)
    (.setAttribute el model/attr-checked "")
    (is (.hasAttribute el model/attr-checked) "precondition: switch is checked")
    (.reset form)
    (is (not (.hasAttribute el model/attr-checked))
        "formReset should remove checked attribute")))

(deftest form-disabled-callback-test
  (let [^js fieldset (js/document.createElement "fieldset")
        el           (make-el)]
    (.appendChild fieldset el)
    (.appendChild (.-body js/document) fieldset)
    (is (not (.hasAttribute el model/attr-disabled)) "precondition: switch is enabled")
    (.setAttribute fieldset "disabled" "")
    (let [^js cls   (.get js/customElements model/tag-name)
          ^js proto (.-prototype cls)]
      (when-let [cb (aget proto "formDisabledCallback")]
        (.call cb el true)
        (is (.hasAttribute el model/attr-disabled)
            "formDisabledCallback(true) should set disabled attribute")
        (.call cb el false)
        (is (not (.hasAttribute el model/attr-disabled))
            "formDisabledCallback(false) should remove disabled attribute")))))

;; ── Reconnect stability ───────────────────────────────────────────────────────

(deftest reconnect-stability-test
  (let [el   (make-el)
        body (.-body js/document)]
    (.appendChild body el)
    (.setAttribute el model/attr-checked "")
    (.remove el)
    (.appendChild body el)
    (let [control (shadow-part el "[part=control]")]
      (is (= "true" (.getAttribute control "aria-checked"))
          "state should be preserved after reconnect"))))
