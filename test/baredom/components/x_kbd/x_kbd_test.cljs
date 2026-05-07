(ns baredom.components.x-kbd.x-kbd-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures]]
            [baredom.components.x-kbd.x-kbd :as x]
            [baredom.components.x-kbd.model :as model]))

(x/init!)

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

(use-fixtures :each {:before cleanup-dom! :after cleanup-dom!})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-root [^js el] (.-shadowRoot el))

(defn ^js shadow-q [^js el selector]
  (.querySelector (shadow-root el) selector))

(defn shadow-q-all [^js el selector]
  (array-seq (.querySelectorAll (shadow-root el) selector)))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow structure ─────────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (shadow-root el)))
    (is (some? (shadow-q el "[part=base]")))))

;; ── Combo mode ───────────────────────────────────────────────────────────────
(deftest combo-renders-keys-and-separators-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Ctrl+C")
    (let [keys (shadow-q-all el "[part=key]")
          seps (shadow-q-all el "[part=separator]")]
      (is (= 2 (count keys)))
      (is (= 1 (count seps)))
      (is (= "Ctrl" (.-textContent ^js (first keys))))
      (is (= "C"    (.-textContent ^js (second keys))))
      (is (= "+"    (.-textContent ^js (first seps)))))
    (testing "each cap is a real <kbd> element"
      (doseq [^js k (shadow-q-all el "[part=key]")]
        (is (= "kbd" (.toLowerCase (.-tagName k))))))))

(deftest combo-renders-three-tokens-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Ctrl+Shift+P")
    (is (= 3 (count (shadow-q-all el "[part=key]"))))
    (is (= 2 (count (shadow-q-all el "[part=separator]"))))))

;; ── Platform-aware Mod ───────────────────────────────────────────────────────
(deftest mod-on-mac-renders-command-glyph-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "mac")
    (.setAttribute el model/attr-keys     "Mod+K")
    (let [first-key (first (shadow-q-all el "[part=key]"))]
      (is (= "⌘" (.-textContent ^js first-key))))
    (is (= "Command plus K" (.getAttribute el "aria-label")))))

(deftest mod-on-win-renders-ctrl-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Mod+K")
    (let [first-key (first (shadow-q-all el "[part=key]"))]
      (is (= "Ctrl" (.-textContent ^js first-key))))
    (is (= "Control plus K" (.getAttribute el "aria-label")))))

(deftest mod-on-linux-renders-ctrl-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "linux")
    (.setAttribute el model/attr-keys     "Mod+K")
    (is (= "Ctrl" (.-textContent ^js (first (shadow-q-all el "[part=key]")))))
    (is (= "Control plus K" (.getAttribute el "aria-label")))))

(deftest platform-auto-resolves-to-detected-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "auto")
    (.setAttribute el model/attr-keys     "Mod+K")
    (let [visible (.-textContent ^js (first (shadow-q-all el "[part=key]")))]
      (is (contains? #{"⌘" "Ctrl"} visible)))))

(deftest platform-attribute-changes-live-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "mac")
    (.setAttribute el model/attr-keys     "Mod+K")
    (is (= "⌘" (.-textContent ^js (first (shadow-q-all el "[part=key]")))))
    (.setAttribute el model/attr-platform "win")
    (is (= "Ctrl" (.-textContent ^js (first (shadow-q-all el "[part=key]")))))))

;; ── Slot mode ────────────────────────────────────────────────────────────────
(deftest slot-mode-renders-single-cap-with-slot-test
  (let [^js el (append! (make-el))]
    (set! (.-textContent el) "Esc")
    (let [keys (shadow-q-all el "[part=key]")
          slot (shadow-q el "slot")]
      (is (= 1 (count keys)))
      (is (some? slot))
      (testing "slot is inside the kbd cap"
        (is (= "kbd" (.toLowerCase (.-tagName ^js (.-parentElement slot))))))
      (testing "slotted text is distributed"
        (let [assigned (.assignedNodes slot)]
          (is (pos? (.-length assigned))))))))

(deftest slot-mode-host-has-no-aria-label-test
  (let [^js el (append! (make-el))]
    (set! (.-textContent el) "Esc")
    (is (nil? (.getAttribute el "aria-label")))))

(deftest slot-mode-explicit-label-still-applies-test
  (let [^js el (append! (make-el))]
    (set! (.-textContent el) "Esc")
    (is (nil? (.getAttribute el "aria-label")))
    (.setAttribute el model/attr-label "Press to exit")
    (is (= "Press to exit" (.getAttribute el "aria-label")))))

(deftest empty-input-renders-no-caps-test
  (let [^js el (append! (make-el))]
    ;; Slot mode with no slot content → renders one cap containing the slot
    ;; (the slot itself shows nothing). No combo caps either.
    (is (some? (shadow-q el "[part=base]")))
    (is (= 1 (count (shadow-q-all el "[part=key]"))))
    (is (= 0 (count (shadow-q-all el "[part=separator]"))))
    (is (some? (shadow-q el "slot")))))

;; ── Combo → slot transition ──────────────────────────────────────────────────
(deftest combo-to-slot-rebuild-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Ctrl+C")
    (is (= 2 (count (shadow-q-all el "[part=key]"))))
    (is (= "Control plus C" (.getAttribute el "aria-label")))
    (.removeAttribute el model/attr-keys)
    (let [keys (shadow-q-all el "[part=key]")]
      (is (= 1 (count keys)))
      (is (some? (shadow-q el "slot")))
      (is (nil? (.getAttribute el "aria-label"))))))

;; ── Slot → combo transition ──────────────────────────────────────────────────
(deftest slot-to-combo-rebuild-test
  (let [^js el (append! (make-el))]
    (set! (.-textContent el) "Esc")
    (is (= 1 (count (shadow-q-all el "[part=key]"))))
    (is (some? (shadow-q el "slot")))
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Ctrl+C")
    (is (= 2 (count (shadow-q-all el "[part=key]"))))
    (is (= 1 (count (shadow-q-all el "[part=separator]"))))
    (is (nil? (shadow-q el "slot")))
    (is (= "Control plus C" (.getAttribute el "aria-label")))))

;; ── Size variant ─────────────────────────────────────────────────────────────
(deftest size-attribute-applies-to-host-data-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "sm")
    (is (= "sm" (.getAttribute el "data-size"))))
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-size "lg")
    (is (= "lg" (.getAttribute el "data-size"))))
  (testing "default md applied even when attribute absent"
    (let [^js el (append! (make-el))]
      (is (= "md" (.getAttribute el "data-size"))))))

;; ── Custom separator ─────────────────────────────────────────────────────────
(deftest custom-separator-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform  "mac")
    (.setAttribute el model/attr-separator "-")
    (.setAttribute el model/attr-keys      "Cmd-K")
    (is (= 2 (count (shadow-q-all el "[part=key]"))))
    (is (= "-" (.-textContent ^js (first (shadow-q-all el "[part=separator]"))))))
  (testing "default separator + when separator attr is unsupported value"
    (let [^js el (append! (make-el))]
      (.setAttribute el model/attr-platform  "win")
      (.setAttribute el model/attr-separator "and")  ;; multi-char → ignored
      (.setAttribute el model/attr-keys      "Ctrl+C")
      (is (= "+" (.-textContent ^js (first (shadow-q-all el "[part=separator]"))))))))

;; ── Author-supplied label overrides derived ──────────────────────────────────
(deftest author-label-wins-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "mac")
    (.setAttribute el model/attr-keys     "Mod+K")
    (.setAttribute el model/attr-label    "open palette")
    (is (= "open palette" (.getAttribute el "aria-label")))))

(deftest label-removal-reverts-to-derived-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "mac")
    (.setAttribute el model/attr-keys     "Mod+K")
    (.setAttribute el model/attr-label    "open palette")
    (is (= "open palette" (.getAttribute el "aria-label")))
    (.removeAttribute el model/attr-label)
    (is (= "Command plus K" (.getAttribute el "aria-label")))))

;; ── Live attribute updates ───────────────────────────────────────────────────
(deftest keys-attribute-changes-live-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-platform "win")
    (.setAttribute el model/attr-keys     "Ctrl+C")
    (is (= 2 (count (shadow-q-all el "[part=key]"))))
    (.setAttribute el model/attr-keys "Ctrl+Shift+P")
    (is (= 3 (count (shadow-q-all el "[part=key]"))))
    (is (= "P" (.-textContent ^js (last (shadow-q-all el "[part=key]")))))))

;; ── Property accessors (Tier 0 reflection) ───────────────────────────────────
(deftest keys-property-test
  (let [^js el (append! (make-el))]
    (set! (.-keys el) "Ctrl+C")
    (is (= "Ctrl+C" (.getAttribute el model/attr-keys)))
    (is (= "Ctrl+C" (.-keys el)))
    (set! (.-keys el) nil)
    (is (not (.hasAttribute el model/attr-keys)))))

(deftest platform-property-test
  (let [^js el (append! (make-el))]
    (set! (.-platform el) "mac")
    (is (= "mac" (.getAttribute el model/attr-platform)))
    (is (= "mac" (.-platform el)))))

(deftest size-property-test
  (let [^js el (append! (make-el))]
    (set! (.-size el) "lg")
    (is (= "lg" (.getAttribute el model/attr-size)))
    (is (= "lg" (.-size el)))))
