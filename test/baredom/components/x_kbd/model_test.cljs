(ns baredom.components.x-kbd.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-kbd.model :as model]))

;; ── parse-size ───────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (is (= "sm" (model/parse-size "sm")))
  (is (= "md" (model/parse-size "md")))
  (is (= "lg" (model/parse-size "lg")))
  (is (= "lg" (model/parse-size "LG")))
  (is (= "md" (model/parse-size "  md  "))))

(deftest parse-size-invalid-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "")))
  (is (= "md" (model/parse-size "huge")))
  (is (= "md" (model/parse-size 42))))

;; ── parse-platform ───────────────────────────────────────────────────────────
(deftest parse-platform-valid-test
  (is (= "auto"  (model/parse-platform "auto")))
  (is (= "mac"   (model/parse-platform "mac")))
  (is (= "win"   (model/parse-platform "win")))
  (is (= "linux" (model/parse-platform "linux")))
  (is (= "mac"   (model/parse-platform "MAC"))))

(deftest parse-platform-invalid-test
  (is (= "auto" (model/parse-platform nil)))
  (is (= "auto" (model/parse-platform "")))
  (is (= "auto" (model/parse-platform "windows"))))

;; ── parse-separator ──────────────────────────────────────────────────────────
(deftest parse-separator-test
  (testing "single character accepted"
    (is (= "+" (model/parse-separator "+")))
    (is (= "-" (model/parse-separator "-")))
    (is (= " " (model/parse-separator " "))))
  (testing "non-string or wrong length falls back to default"
    (is (= "+" (model/parse-separator nil)))
    (is (= "+" (model/parse-separator "")))
    (is (= "+" (model/parse-separator "++")))
    (is (= "+" (model/parse-separator "and")))))

;; ── resolve-platform ─────────────────────────────────────────────────────────
(deftest resolve-platform-explicit-test
  (is (= "mac"   (model/resolve-platform "mac"   "linux")))
  (is (= "win"   (model/resolve-platform "win"   "mac")))
  (is (= "linux" (model/resolve-platform "linux" "mac"))))

(deftest resolve-platform-auto-uses-detected-test
  (is (= "mac"   (model/resolve-platform "auto" "mac")))
  (is (= "win"   (model/resolve-platform "auto" "win")))
  (is (= "linux" (model/resolve-platform "auto" "linux"))))

(deftest resolve-platform-nil-attr-uses-detected-test
  (is (= "mac" (model/resolve-platform nil "mac")))
  (is (= "win" (model/resolve-platform ""  "win"))))

(deftest resolve-platform-bogus-detected-falls-back-test
  (is (= "linux" (model/resolve-platform "auto" nil)))
  (is (= "linux" (model/resolve-platform "auto" "ios"))))

;; ── split-keys ───────────────────────────────────────────────────────────────
(deftest split-keys-basic-test
  (is (= ["Ctrl" "C"]          (model/split-keys "Ctrl+C" "+")))
  (is (= ["Ctrl" "Shift" "P"]  (model/split-keys "Ctrl+Shift+P" "+")))
  (is (= ["Cmd" "K"]           (model/split-keys "Cmd-K" "-"))))

(deftest split-keys-trim-and-drop-empty-test
  (is (= ["Ctrl" "C"] (model/split-keys " Ctrl + C " "+")))
  (is (= ["Ctrl" "C"] (model/split-keys "Ctrl++C" "+")))
  (is (= ["A"]        (model/split-keys "+A+" "+"))))

(deftest split-keys-empty-or-nil-test
  (is (= [] (model/split-keys nil "+")))
  (is (= [] (model/split-keys ""  "+")))
  (is (= [] (model/split-keys "   " "+"))))

;; ── resolve-token ────────────────────────────────────────────────────────────
(deftest resolve-token-mod-test
  (testing "Mod becomes ⌘ on mac"
    (let [t (model/resolve-token "mac" "Mod")]
      (is (= "⌘" (:visible t)))
      (is (= "Command"  (:aria t)))))
  (testing "Mod becomes Ctrl on win"
    (let [t (model/resolve-token "win" "Mod")]
      (is (= "Ctrl"    (:visible t)))
      (is (= "Control" (:aria t)))))
  (testing "Mod becomes Ctrl on linux"
    (let [t (model/resolve-token "linux" "Mod")]
      (is (= "Ctrl"    (:visible t)))
      (is (= "Control" (:aria t))))))

(deftest resolve-token-cmd-meta-test
  (testing "Cmd/Meta on mac → ⌘ Command"
    (doseq [tok ["Cmd" "Meta" "Command"]]
      (let [t (model/resolve-token "mac" tok)]
        (is (= "⌘" (:visible t)))
        (is (= "Command"  (:aria t))))))
  (testing "Cmd on win → Win"
    (let [t (model/resolve-token "win" "Cmd")]
      (is (= "Win"     (:visible t)))
      (is (= "Windows" (:aria t)))))
  (testing "Cmd on linux → Super"
    (let [t (model/resolve-token "linux" "Cmd")]
      (is (= "Super" (:visible t)))
      (is (= "Super" (:aria t))))))

(deftest resolve-token-mac-glyphs-test
  (is (= "⌃" (:visible (model/resolve-token "mac" "Ctrl"))))
  (is (= "⌥" (:visible (model/resolve-token "mac" "Alt"))))
  (is (= "⌥" (:visible (model/resolve-token "mac" "Option"))))
  (is (= "⇧" (:visible (model/resolve-token "mac" "Shift"))))
  (is (= "Return" (:visible (model/resolve-token "mac" "Enter"))))
  (is (= "Return" (:visible (model/resolve-token "mac" "Return")))))

(deftest resolve-token-non-mac-keeps-text-test
  (is (= "Ctrl"  (:visible (model/resolve-token "win"   "Ctrl"))))
  (is (= "Alt"   (:visible (model/resolve-token "win"   "Alt"))))
  (is (= "Shift" (:visible (model/resolve-token "linux" "Shift"))))
  (is (= "Enter" (:visible (model/resolve-token "win"   "Enter")))))

(deftest resolve-token-case-insensitive-test
  (is (= "⌘" (:visible (model/resolve-token "mac" "MOD"))))
  (is (= "⌘" (:visible (model/resolve-token "mac" "cmd"))))
  (is (= "Ctrl" (:visible (model/resolve-token "win" "CTRL")))))

(deftest resolve-token-passthrough-test
  (testing "unknown letters/symbols pass through verbatim"
    (let [t (model/resolve-token "mac" "K")]
      (is (= "K" (:visible t)))
      (is (= "K" (:aria t))))
    (let [t (model/resolve-token "win" "F1")]
      (is (= "F1" (:visible t)))
      (is (= "F1" (:aria t))))
    (let [t (model/resolve-token "mac" "↑")]
      (is (= "↑" (:visible t)))
      (is (= "↑" (:aria t)))))
  (testing "trims whitespace"
    (is (= "K" (:visible (model/resolve-token "mac" "  K  "))))))

;; ── derive-aria-label ────────────────────────────────────────────────────────
(deftest derive-aria-label-explicit-wins-test
  (let [tokens [{:visible "⌘" :aria "Command"} {:visible "K" :aria "K"}]]
    (is (= "open palette"
           (model/derive-aria-label tokens "open palette")))
    (is (= "open palette"
           (model/derive-aria-label tokens "  open palette  ")))))

(deftest derive-aria-label-derived-test
  (let [tokens [{:visible "⌘" :aria "Command"}
                {:visible "⇧" :aria "Shift"}
                {:visible "P" :aria "P"}]]
    (is (= "Command plus Shift plus P"
           (model/derive-aria-label tokens nil)))
    (is (= "Command plus Shift plus P"
           (model/derive-aria-label tokens ""))))
  (testing "uses :aria names, never glyphs"
    (let [tokens [{:visible "⌘" :aria "Command"} {:visible "K" :aria "K"}]
          out    (model/derive-aria-label tokens nil)]
      (is (not (re-find #"⌘" out))))))

(deftest derive-aria-label-empty-test
  (is (= "" (model/derive-aria-label [] nil)))
  (is (= "" (model/derive-aria-label [] ""))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= []    (:tokens m)))
    (is (= "+"   (:separator m)))
    (is (= "md"  (:size m)))
    (is (= ""    (:label m)))
    (is (true?   (:slot-mode? m)))))

(deftest normalize-mac-combo-test
  (let [m (model/normalize {:keys-raw "Mod+K" :platform-raw "mac"})]
    (is (= 2 (count (:tokens m))))
    (is (= "⌘" (:visible (first (:tokens m)))))
    (is (= "K"  (:visible (second (:tokens m)))))
    (is (= "Command plus K" (:label m)))
    (is (false? (:slot-mode? m)))
    (is (= "mac" (:platform m)))))

(deftest normalize-win-combo-test
  (let [m (model/normalize {:keys-raw "Mod+K" :platform-raw "win"})]
    (is (= "Ctrl" (:visible (first (:tokens m)))))
    (is (= "K"    (:visible (second (:tokens m)))))
    (is (= "Control plus K" (:label m)))))

(deftest normalize-custom-separator-test
  (let [m (model/normalize {:keys-raw "Cmd-K"
                            :separator-raw "-"
                            :platform-raw "mac"})]
    (is (= "-" (:separator m)))
    (is (= 2  (count (:tokens m))))
    (is (= "⌘" (:visible (first (:tokens m)))))))

(deftest normalize-explicit-label-test
  (let [m (model/normalize {:keys-raw "Mod+K"
                            :platform-raw "mac"
                            :label-raw "open command palette"})]
    (is (= "open command palette" (:label m)))))

(deftest normalize-whitespace-tolerated-test
  (let [m (model/normalize {:keys-raw "  Ctrl + Shift + P  "
                            :platform-raw "win"})]
    (is (= 3 (count (:tokens m))))
    (is (= ["Ctrl" "Shift" "P"]
           (mapv :visible (:tokens m))))))

(deftest normalize-empty-keys-is-slot-mode-test
  (let [m (model/normalize {:keys-raw ""})]
    (is (true? (:slot-mode? m)))
    (is (= [] (:tokens m)))))

(deftest normalize-platform-passthrough-test
  (testing "model/normalize uses provided platform when valid"
    (is (= "win" (:platform (model/normalize {:platform-raw "win"})))))
  (testing "auto/invalid falls back inside normalize (linux)"
    (is (= "linux" (:platform (model/normalize {:platform-raw "auto"}))))
    (is (= "linux" (:platform (model/normalize {:platform-raw nil}))))))
