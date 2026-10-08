#!/usr/bin/env bb
;; sync_doc_defaults_test.bb — Tests for sync_doc_defaults.bb.
;;
;; Usage: bb scripts/sync_doc_defaults_test.bb

(load-file "scripts/sync_doc_defaults.bb")
(require '[clojure.test :refer [deftest is run-tests]])

(def ^:private defaults
  {"--x-card-radius" "var(--x-radius-md,10px)"
   "--x-card-bg"     "#fff"})

(defn- table [& rows]
  (str/join "\n" (concat ["| Property | Default | Description |" "|---|---|---|"] rows)))

(deftest a-default-cell-takes-the-default-of-its-property
  (is (= (table "| `--x-card-radius` | `var(--x-radius-md,10px)` | Corner |")
         (synced defaults (table "| `--x-card-radius` | `10px` | Corner |")))))

(deftest a-row-that-is-current-stays-as-it-is
  (let [doc (table "| `--x-card-bg` | `#fff` | Fill |")]
    (is (= doc (synced defaults doc)))))

(deftest a-shorter-default-keeps-the-width-of-its-cell
  (is (= (table "| `--x-card-bg` | `#fff`          | Fill |")
         (synced defaults (table "| `--x-card-bg` | `rgba(0,0,0,1)` | Fill |")))))

(deftest text-after-the-value-stays
  (is (= (table "| `--x-card-bg` | `#fff` / dark adjusted | Fill |")
         (synced defaults (table "| `--x-card-bg` | `#eee` / dark adjusted | Fill |")))))

(deftest a-default-in-words-becomes-the-value
  (is (= (table "| `--x-card-bg` | `#fff` | Fill |")
         (synced defaults (table "| `--x-card-bg` | white | Fill |")))))

(deftest only-the-first-default-column-is-written
  (let [heading "| Property | Default (light) | Default (dark) |\n|---|---|---|\n"]
    (is (= (str heading "| `--x-card-bg` | `#fff` | `#000` |")
           (synced defaults (str heading "| `--x-card-bg` | `#eee` | `#000` |"))))))

(deftest a-table-with-no-default-column-is-left-alone
  (let [doc "| Property | Purpose |\n|---|---|\n| `--x-card-bg` | The fill |"]
    (is (= doc (synced defaults doc)))))

(deftest a-property-with-no-default-is-left-alone
  (let [doc (table "| `--x-card-shadow` | `none` | Shadow |")]
    (is (= doc (synced defaults doc)))))

(deftest text-outside-a-table-is-left-alone
  (let [doc "# Card\n\nSet `--x-card-bg` to change the fill.\n"]
    (is (= doc (synced defaults doc)))))

(deftest only-a-doc-whose-text-changes-is-outdated
  (is (= {"card.md" (table "| `--x-card-bg` | `#fff` | Fill |")}
         (outdated defaults {"card.md" (table "| `--x-card-bg` | `#eee` | Fill |")
                             "icon.md" (table "| `--x-card-bg` | `#fff` | Fill |")
                             "note.md" "No table here."}))))

(deftest properties-reads-every-default-of-the-manifest
  (is (= {"--x-card-bg" "#fff" "--x-card-gap" nil}
         (properties {:modules [{:declarations [{:cssProperties [{:name "--x-card-bg" :default "#fff"}
                                                                 {:name "--x-card-gap"}]}]}]}))))

(let [{:keys [fail error]} (run-tests)]
  (System/exit (if (zero? (+ fail error)) 0 1)))
