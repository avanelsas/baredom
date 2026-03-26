(ns app.components.x-cancel-dialogue.x-cancel-dialogue-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures async]]
            [app.components.x-cancel-dialogue.x-cancel-dialogue :as x]
            [app.components.x-cancel-dialogue.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [^js node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el s]
  (.querySelector (.-shadowRoot el) s))

;; ── Registration ──────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ─────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part=backdrop]")))
    (is (some? (shadow-part el "[part=dialog]")))
    (is (some? (shadow-part el "[part=panel]")))
    (is (some? (shadow-part el "[part=header]")))
    (is (some? (shadow-part el "[part=headline]")))
    (is (some? (shadow-part el "[part=body]")))
    (is (some? (shadow-part el "[part=message]")))
    (is (some? (shadow-part el "[part=actions]")))
    (is (some? (shadow-part el "[part=cancel-btn]")))
    (is (some? (shadow-part el "[part=confirm-btn]")))))

;; ── Default text content ──────────────────────────────────────────────────────
(deftest default-texts-test
  (let [^js el (append! (make-el))]
    (is (= model/default-headline
           (.-textContent (shadow-part el "[part=headline]"))))
    (is (= model/default-confirm-text
           (.-textContent (shadow-part el "[part=confirm-btn]"))))
    (is (= model/default-cancel-text
           (.-textContent (shadow-part el "[part=cancel-btn]")))))  )

;; ── Dialog closed by default ──────────────────────────────────────────────────
(deftest dialog-hidden-by-default-test
  (let [^js el (append! (make-el))]
    (is (not (.hasAttribute el model/attr-open)))))

;; ── Open attribute shows dialog ───────────────────────────────────────────────
(deftest open-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-open "")
    (is (.hasAttribute el model/attr-open))))

;; ── headline attribute ────────────────────────────────────────────────────────
(deftest headline-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-headline "Discard draft?")
    (is (= "Discard draft?"
           (.-textContent (shadow-part el "[part=headline]"))))))

;; ── message attribute ─────────────────────────────────────────────────────────
(deftest message-shown-when-set-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-message "All changes will be lost.")
    (let [^js msg (shadow-part el "[part=message]")]
      (is (= "All changes will be lost." (.-textContent msg)))
      (is (= "block" (.. msg -style -display))))))

(deftest message-hidden-when-not-set-test
  (let [^js el  (append! (make-el))
        ^js msg (shadow-part el "[part=message]")]
    (is (= "none" (.. msg -style -display)))))

;; ── confirm-text and cancel-text attributes ───────────────────────────────────
(deftest custom-button-texts-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-confirm-text "Delete")
    (.setAttribute el model/attr-cancel-text "Never mind")
    (is (= "Delete"
           (.-textContent (shadow-part el "[part=confirm-btn]"))))
    (is (= "Never mind"
           (.-textContent (shadow-part el "[part=cancel-btn]"))))))

;; ── danger attribute ──────────────────────────────────────────────────────────
(deftest danger-attribute-test
  (let [^js el      (append! (make-el))
        ^js confirm (shadow-part el "[part=confirm-btn]")]
    (.setAttribute el model/attr-danger "")
    (is (.hasAttribute confirm "data-danger"))))

(deftest no-danger-by-default-test
  (let [^js el      (append! (make-el))
        ^js confirm (shadow-part el "[part=confirm-btn]")]
    (is (not (.hasAttribute confirm "data-danger")))))

;; ── Cancel button click ───────────────────────────────────────────────────────
(deftest cancel-button-fires-cancel-request-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.addEventListener el model/event-cancel-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=cancel-btn]"))
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (.-cancelable ev))
      (is (= "cancel-button" (.-reason (.-detail ev)))))))

(deftest cancel-button-fires-cancel-and-closes-test
  (let [^js el   (append! (make-el))
        cancels  (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.addEventListener el model/event-cancel
                                    (fn [^js e] (swap! cancels conj e)))]
    (.click (shadow-part el "[part=cancel-btn]"))
    (is (= 1 (count @cancels)))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest cancel-request-prevent-default-stops-close-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-open "")
        _      (.addEventListener el model/event-cancel-request
                                  (fn [^js e] (.preventDefault e)))]
    (.click (shadow-part el "[part=cancel-btn]"))
    ;; dialog should still be open
    (is (.hasAttribute el model/attr-open))))

;; ── Confirm button click ──────────────────────────────────────────────────────
(deftest confirm-button-fires-confirm-request-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.addEventListener el model/event-confirm-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=confirm-btn]"))
    (is (= 1 (count @events)))
    (let [^js ev (first @events)]
      (is (.-cancelable ev)))))

(deftest confirm-button-fires-confirm-and-closes-test
  (let [^js el    (append! (make-el))
        confirms  (atom [])
        _         (.setAttribute el model/attr-open "")
        _         (.addEventListener el model/event-confirm
                                     (fn [^js e] (swap! confirms conj e)))]
    (.click (shadow-part el "[part=confirm-btn]"))
    (is (= 1 (count @confirms)))
    (is (not (.hasAttribute el model/attr-open)))))

(deftest confirm-request-prevent-default-stops-close-test
  (let [^js el (append! (make-el))
        _      (.setAttribute el model/attr-open "")
        _      (.addEventListener el model/event-confirm-request
                                  (fn [^js e] (.preventDefault e)))]
    (.click (shadow-part el "[part=confirm-btn]"))
    (is (.hasAttribute el model/attr-open))))

;; ── Backdrop click ────────────────────────────────────────────────────────────
(deftest backdrop-click-fires-cancel-with-backdrop-reason-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.addEventListener el model/event-cancel-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=backdrop]"))
    (is (= 1 (count @events)))
    (is (= "backdrop" (.-reason (.-detail (first @events)))))))

;; ── Disabled prevents events ──────────────────────────────────────────────────
(deftest disabled-prevents-cancel-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.setAttribute el model/attr-disabled "")
        _        (.addEventListener el model/event-cancel-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=cancel-btn]"))
    (is (= 0 (count @events)))))

(deftest disabled-prevents-confirm-test
  (let [^js el   (append! (make-el))
        events   (atom [])
        _        (.setAttribute el model/attr-open "")
        _        (.setAttribute el model/attr-disabled "")
        _        (.addEventListener el model/event-confirm-request
                                    (fn [^js e] (swap! events conj e)))]
    (.click (shadow-part el "[part=confirm-btn]"))
    (is (= 0 (count @events)))))

;; ── Property reflection ───────────────────────────────────────────────────────
(deftest open-property-test
  (let [^js el (append! (make-el))]
    (is (= false (.-open el)))
    (set! (.-open el) true)
    (is (.hasAttribute el model/attr-open))
    (is (= true (.-open el)))
    ;; Setting open=false closes the dialog
    (set! (.-open el) false)
    (is (not (.hasAttribute el model/attr-open)))
    (is (= false (.-open el)))))

(deftest disabled-property-test
  (let [^js el (append! (make-el))]
    (set! (.-disabled el) true)
    (is (.hasAttribute el model/attr-disabled))
    (set! (.-disabled el) false)
    (is (not (.hasAttribute el model/attr-disabled)))))

(deftest danger-property-test
  (let [^js el (append! (make-el))]
    (is (= false (.-danger el)))
    (set! (.-danger el) true)
    (is (.hasAttribute el model/attr-danger))))

(deftest headline-property-test
  (let [^js el (append! (make-el))]
    (set! (.-headline el) "Sure?")
    (is (= "Sure?" (.getAttribute el model/attr-headline)))))

(deftest confirm-text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-confirmText el) "Yes, delete")
    (is (= "Yes, delete" (.getAttribute el model/attr-confirm-text)))))

(deftest cancel-text-property-test
  (let [^js el (append! (make-el))]
    (set! (.-cancelText el) "Go back")
    (is (= "Go back" (.getAttribute el model/attr-cancel-text)))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-single-cancel-event-test
  (let [^js el  (make-el)
        events  (atom [])
        _       (.addEventListener el model/event-cancel-request
                                   (fn [^js e] (swap! events conj e)))]
    (.appendChild (.-body js/document) el)
    (.setAttribute el model/attr-open "")
    (.remove el)
    (.appendChild (.-body js/document) el)
    (.setAttribute el model/attr-open "")
    (.click (shadow-part el "[part=cancel-btn]"))
    (is (= 1 (count @events)))))

;; ── aria attributes ───────────────────────────────────────────────────────────
(deftest dialog-role-and-aria-modal-test
  (let [^js el     (append! (make-el))
        ^js dialog (shadow-part el "[part=dialog]")]
    (is (= "dialog" (.getAttribute dialog "role")))
    (is (= "true" (.getAttribute dialog "aria-modal")))))

(deftest dialog-aria-labelledby-links-to-headline-test
  (let [^js el       (append! (make-el))
        ^js dialog   (shadow-part el "[part=dialog]")
        ^js headline (shadow-part el "[part=headline]")
        lid          (.getAttribute dialog "aria-labelledby")]
    (is (string? lid))
    (is (not= "" lid))
    (is (= lid (.getAttribute headline "id")))))
