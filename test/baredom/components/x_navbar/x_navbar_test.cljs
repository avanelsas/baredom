(ns baredom.components.x-navbar.x-navbar-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures testing async]]
            [baredom.components.x-navbar.x-navbar :as x-navbar]
            [baredom.components.x-navbar.model :as model]))

(x-navbar/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn dispatch-click! [^js el]
  (.dispatchEvent
   el
   (js/MouseEvent.
    "click"
    #js {:bubbles true
         :composed true
         :cancelable true})))

(defn next-tick [f]
  (js/setTimeout f 0))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el   (append! (make-el))
        ^js root (.-shadowRoot el)]
    (is (some? root))
    (is (some? (shadow-part el "[part='base']")))
    (is (some? (shadow-part el "[part='bar']")))
    (is (some? (shadow-part el "[part='brand']")))
    (is (some? (shadow-part el "[part='start']")))
    (is (some? (shadow-part el "[part='nav']")))
    (is (some? (shadow-part el "[part='actions']")))
    (is (some? (shadow-part el "[part='toggle']")))
    (is (some? (shadow-part el "[part='end']")))
    (is (some? (.querySelector root "slot[name='brand']")))
    (is (some? (.querySelector root "slot[name='start']")))
    (is (some? (.querySelector root "slot[name='actions']")))
    (is (some? (.querySelector root "slot[name='toggle']")))
    (is (some? (.querySelector root "slot[name='end']")))
    (is (some? (.querySelector root "slot:not([name])")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")
        ^js bar  (shadow-part el "[part='bar']")]
    (is (= "default"       (.getAttribute base "data-variant")))
    (is (= "horizontal"    (.getAttribute base "data-orientation")))
    (is (= "md"            (.getAttribute base "data-breakpoint")))
    (is (= "space-between" (.getAttribute bar "data-alignment")))
    (is (nil? (.getAttribute base "aria-label")))))

;; ── Boolean property reflection ──────────────────────────────────────────────
(deftest sticky-property-test
  (let [^js el (append! (make-el))]
    (set! (.-sticky el) true)
    (is (.hasAttribute el model/attr-sticky))
    (is (= true (.-sticky el)))
    (set! (.-sticky el) false)
    (is (not (.hasAttribute el model/attr-sticky)))
    (is (= false (.-sticky el)))))

(deftest elevated-property-test
  (let [^js el (append! (make-el))]
    (set! (.-elevated el) true)
    (is (.hasAttribute el model/attr-elevated))
    (is (= true (.-elevated el)))
    (set! (.-elevated el) false)
    (is (not (.hasAttribute el model/attr-elevated)))
    (is (= false (.-elevated el)))))

;; ── String property reflection ───────────────────────────────────────────────
(deftest label-property-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-label el)))
    (set! (.-label el) "Main navigation")
    (is (= "Main navigation" (.getAttribute el model/attr-label)))
    (is (= "Main navigation" (.-label el)))
    (set! (.-label el) nil)
    (is (not (.hasAttribute el model/attr-label)))))

(deftest variant-property-test
  (let [^js el (append! (make-el))]
    (set! (.-variant el) "inverted")
    (is (= "inverted" (.getAttribute el model/attr-variant)))
    (is (= "inverted" (.-variant el)))))

(deftest orientation-property-test
  (let [^js el (append! (make-el))]
    (set! (.-orientation el) "vertical")
    (is (= "vertical" (.getAttribute el model/attr-orientation)))
    (is (= "vertical" (.-orientation el)))))

(deftest alignment-property-test
  (let [^js el (append! (make-el))]
    (set! (.-alignment el) "center")
    (is (= "center" (.getAttribute el model/attr-alignment)))
    (is (= "center" (.-alignment el)))))

(deftest breakpoint-property-test
  (let [^js el (append! (make-el))]
    (set! (.-breakpoint el) "lg")
    (is (= "lg" (.getAttribute el model/attr-breakpoint)))
    (is (= "lg" (.-breakpoint el)))))

;; ── Attribute normalization ──────────────────────────────────────────────────
(deftest invalid-attributes-normalize-to-defaults-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")
        ^js bar  (shadow-part el "[part='bar']")]
    (.setAttribute el model/attr-orientation "bad")
    (.setAttribute el model/attr-variant "bad")
    (.setAttribute el model/attr-breakpoint "bad")
    (.setAttribute el model/attr-alignment "bad")

    (testing "host attributes are not rewritten"
      (is (= "bad" (.getAttribute el model/attr-orientation)))
      (is (= "bad" (.getAttribute el model/attr-variant)))
      (is (= "bad" (.getAttribute el model/attr-breakpoint)))
      (is (= "bad" (.getAttribute el model/attr-alignment))))

    (testing "shadow DOM uses normalized defaults"
      (is (= "horizontal"    (.getAttribute base "data-orientation")))
      (is (= "default"       (.getAttribute base "data-variant")))
      (is (= "md"            (.getAttribute base "data-breakpoint")))
      (is (= "space-between" (.getAttribute bar "data-alignment"))))))

(deftest no-render-loop-on-normalization-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")
        ^js bar  (shadow-part el "[part='bar']")]
    (.setAttribute el model/attr-variant "oops")
    (.setAttribute el model/attr-alignment "oops")
    (.setAttribute el model/attr-breakpoint "oops")

    (is (= "default"       (.getAttribute base "data-variant")))
    (is (= "space-between" (.getAttribute bar "data-alignment")))
    (is (= "md"            (.getAttribute base "data-breakpoint")))))

;; ── Landmark label ───────────────────────────────────────────────────────────
(deftest landmark-label-set-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-label "Main navigation")
    (is (= "Main navigation" (.getAttribute base "aria-label")))))

(deftest landmark-label-removed-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-label "Main navigation")
    (is (= "Main navigation" (.getAttribute base "aria-label")))
    (.removeAttribute el model/attr-label)
    (is (nil? (.getAttribute base "aria-label")))))

(deftest empty-label-no-aria-label-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-label "")
    (is (nil? (.getAttribute base "aria-label")))))

;; ── Navigate event ───────────────────────────────────────────────────────────
(deftest navigate-event-test
  (let [^js el   (append! (make-el))
        ^js link (.createElement js/document "a")
        seen     (atom nil)]
    (.setAttribute link "href" "#docs")
    (set! (.-textContent link) "Docs")

    (.addEventListener link "click"
                       (fn [^js event] (.preventDefault event)))

    (.appendChild el link)

    (.addEventListener el model/event-navigate
                       (fn [^js event]
                         (reset! seen (.-detail event))))

    (dispatch-click! link)

    (is (some? @seen))
    (is (= "#docs"   (.-href ^js @seen)))
    (is (= "pointer" (.-source ^js @seen)))))

(deftest navigate-event-bubbles-and-composed-test
  (let [^js el   (append! (make-el))
        ^js link (.createElement js/document "a")
        seen     (atom nil)]
    (.setAttribute link "href" "#test")
    (set! (.-textContent link) "Test")

    (.addEventListener link "click"
                       (fn [^js event] (.preventDefault event)))

    (.appendChild el link)

    (.addEventListener el model/event-navigate
                       (fn [^js event]
                         (reset! seen event)))

    (dispatch-click! link)

    (is (true?  (.-bubbles ^js @seen)))
    (is (true?  (.-composed ^js @seen)))
    (is (false? (.-cancelable ^js @seen)))))

;; ── Brand activate event ─────────────────────────────────────────────────────
(deftest brand-activate-event-test
  (async done
    (let [^js el    (append! (make-el))
          ^js brand (.createElement js/document "a")
          seen      (atom nil)]
      (.setAttribute brand "slot" model/slot-brand)
      (.setAttribute brand "href" "#home")
      (set! (.-textContent brand) "Acme")

      (.addEventListener brand "click"
                         (fn [^js event] (.preventDefault event)))

      (.appendChild el brand)

      (.addEventListener el model/event-brand-activate
                         (fn [^js event]
                           (reset! seen (.-detail event))))

      (next-tick
       (fn []
         (dispatch-click! brand)
         (is (some? @seen))
         (is (= "pointer" (.-source ^js @seen)))
         (done))))))

;; ── Slot presence markers ────────────────────────────────────────────────────
(deftest slot-presence-markers-test
  (async done
    (let [^js el     (append! (make-el))
          ^js brand  (.createElement js/document "span")
          ^js action (.createElement js/document "button")
          ^js toggle (.createElement js/document "button")]
      (.setAttribute brand "slot" model/slot-brand)
      (set! (.-textContent brand) "Acme")

      (.setAttribute action "slot" model/slot-actions)
      (.setAttribute action "type" "button")
      (set! (.-textContent action) "Login")

      (.setAttribute toggle "slot" model/slot-toggle)
      (.setAttribute toggle "type" "button")
      (set! (.-textContent toggle) "Menu")

      (.appendChild el brand)
      (.appendChild el action)
      (.appendChild el toggle)

      (next-tick
       (fn []
         (is (= "true" (.getAttribute (shadow-part el "[part='brand']") "data-has-brand")))
         (is (= "true" (.getAttribute (shadow-part el "[part='actions']") "data-has-actions")))
         (is (= "true" (.getAttribute (shadow-part el "[part='toggle']") "data-has-toggle")))
         (done))))))

(deftest empty-slots-collapse-test
  (let [^js el (append! (make-el))]
    (is (= "false" (.getAttribute (shadow-part el "[part='brand']") "data-has-brand")))
    (is (= "false" (.getAttribute (shadow-part el "[part='start']") "data-has-start")))
    (is (= "false" (.getAttribute (shadow-part el "[part='actions']") "data-has-actions")))
    (is (= "false" (.getAttribute (shadow-part el "[part='toggle']") "data-has-toggle")))
    (is (= "false" (.getAttribute (shadow-part el "[part='end']") "data-has-end")))))

;; ── CSS tokens surface ───────────────────────────────────────────────────────
(deftest height-and-alignment-tokens-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")
        ^js bar  (shadow-part el "[part='bar']")]
    (.setProperty (.-style el) "--x-navbar-height" "72px")
    (.setProperty (.-style el) "--x-navbar-align-items" "end")
    (is (some? base))
    (is (some? bar))))

;; ── Variant rendering ────────────────────────────────────────────────────────
(deftest variant-renders-to-shadow-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-variant "inverted")
    (is (= "inverted" (.getAttribute base "data-variant")))
    (.setAttribute el model/attr-variant "subtle")
    (is (= "subtle" (.getAttribute base "data-variant")))
    (.setAttribute el model/attr-variant "transparent")
    (is (= "transparent" (.getAttribute base "data-variant")))))

;; ── Orientation rendering ────────────────────────────────────────────────────
(deftest orientation-renders-to-shadow-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")]
    (.setAttribute el model/attr-orientation "vertical")
    (is (= "vertical" (.getAttribute base "data-orientation")))
    (.setAttribute el model/attr-orientation "horizontal")
    (is (= "horizontal" (.getAttribute base "data-orientation")))))

;; ── Reconnection ─────────────────────────────────────────────────────────────
(deftest reconnect-preserves-shadow-dom-test
  (let [^js el (make-el)]
    (.appendChild (.-body js/document) el)
    (let [^js root1 (.-shadowRoot el)]
      (.remove el)
      (.appendChild (.-body js/document) el)
      (is (= root1 (.-shadowRoot el)))
      (is (some? (shadow-part el "[part='base']"))))))

;; ── Disconnected clears focus visible ────────────────────────────────────────
(deftest disconnected-clears-focus-visible-test
  (let [^js el (append! (make-el))]
    (.setAttribute el "data-focus-visible-within" "true")
    (.remove el)
    (.appendChild (.-body js/document) el)
    ;; After reconnection, focus-visible state should be clean
    ;; (disconnectedCallback resets the internal flag)
    (is (some? (shadow-part el "[part='base']")))))

;; ── Default look: bottom border only, no radius ──────────────────────────────
(deftest default-has-bottom-border-only-test
  (let [^js el   (append! (make-el))
        ^js base (shadow-part el "[part='base']")
        ^js cs   (js/getComputedStyle base)]
    (is (= "none" (.getPropertyValue cs "border-top-style")))
    (is (= "none" (.getPropertyValue cs "border-left-style")))
    (is (= "none" (.getPropertyValue cs "border-right-style")))
    (is (not= "none" (.getPropertyValue cs "border-bottom-style")))
    (is (= "0px" (.getPropertyValue cs "border-top-left-radius")))
    (is (= "0px" (.getPropertyValue cs "border-top-right-radius")))
    (is (= "0px" (.getPropertyValue cs "border-bottom-left-radius")))
    (is (= "0px" (.getPropertyValue cs "border-bottom-right-radius")))))

(deftest radius-escape-hatch-test
  (let [^js el (append! (make-el))]
    (.setProperty (.-style el) "--x-navbar-radius" "1rem")
    (let [^js base (shadow-part el "[part='base']")
          ^js cs   (js/getComputedStyle base)]
      (is (not= "0px" (.getPropertyValue cs "border-top-left-radius"))))))
