(ns baredom.control-font-test
  (:require
            [baredom.components.x-alert.x-alert :as x-alert]
            [baredom.components.x-breadcrumbs.x-breadcrumbs :as x-breadcrumbs]
            [baredom.components.x-button.x-button :as x-button]
            [baredom.components.x-cancel-dialogue.x-cancel-dialogue :as x-cancel-dialogue]
            [baredom.components.x-carousel.x-carousel :as x-carousel]
            [baredom.components.x-checkbox.x-checkbox :as x-checkbox]
            [baredom.components.x-chip.x-chip :as x-chip]
            [baredom.components.x-code.x-code :as x-code]
            [baredom.components.x-collapse.x-collapse :as x-collapse]
            [baredom.components.x-color-picker.x-color-picker :as x-color-picker]
            [baredom.components.x-combobox.x-combobox :as x-combobox]
            [baredom.components.x-command-palette.x-command-palette :as x-command-palette]
            [baredom.components.x-currency-field.x-currency-field :as x-currency-field]
            [baredom.components.x-date-picker.x-date-picker :as x-date-picker]
            [baredom.components.x-dropdown.x-dropdown :as x-dropdown]
            [baredom.components.x-file-upload.x-file-upload :as x-file-upload]
            [baredom.components.x-form-field.x-form-field :as x-form-field]
            [baredom.components.x-multi-combobox.x-multi-combobox :as x-multi-combobox]
            [baredom.components.x-otp-input.x-otp-input :as x-otp-input]
            [baredom.components.x-pagination.x-pagination :as x-pagination]
            [baredom.components.x-popover.x-popover :as x-popover]
            [baredom.components.x-radio.x-radio :as x-radio]
            [baredom.components.x-scroll.x-scroll :as x-scroll]
            [baredom.components.x-search-field.x-search-field :as x-search-field]
            [baredom.components.x-select.x-select :as x-select]
            [baredom.components.x-slider.x-slider :as x-slider]
            [baredom.components.x-stepper.x-stepper :as x-stepper]
            [baredom.components.x-switch.x-switch :as x-switch]
            [baredom.components.x-table-cell.x-table-cell :as x-table-cell]
            [baredom.components.x-text-area.x-text-area :as x-text-area]
            [baredom.components.x-toast.x-toast :as x-toast]
            [baredom.components.x-welcome-tour.x-welcome-tour :as x-welcome-tour]
            [cljs.test :refer-macros [deftest is testing use-fixtures]]))

(def ^:private cases
  "Every component that creates a native control: how to register it, and its tag."
  [[x-alert/init!           "x-alert"]
   [x-breadcrumbs/init!     "x-breadcrumbs"]
   [x-button/init!          "x-button"]
   [x-cancel-dialogue/init! "x-cancel-dialogue"]
   [x-carousel/init!        "x-carousel"]
   [x-checkbox/init!        "x-checkbox"]
   [x-chip/init!            "x-chip"]
   [x-code/init!            "x-code"]
   [x-collapse/init!        "x-collapse"]
   [x-color-picker/init!    "x-color-picker"]
   [x-combobox/init!        "x-combobox"]
   [x-command-palette/init! "x-command-palette"]
   [x-currency-field/init!  "x-currency-field"]
   [x-date-picker/init!     "x-date-picker"]
   [x-dropdown/init!        "x-dropdown"]
   [x-file-upload/init!     "x-file-upload"]
   [x-form-field/init!      "x-form-field"]
   [x-multi-combobox/init!  "x-multi-combobox"]
   [x-otp-input/init!       "x-otp-input"]
   [x-pagination/init!      "x-pagination"]
   [x-popover/init!         "x-popover"]
   [x-radio/init!           "x-radio"]
   [x-scroll/init!          "x-scroll"]
   [x-search-field/init!    "x-search-field"]
   [x-select/init!          "x-select"]
   [x-slider/init!          "x-slider"]
   [x-stepper/init!         "x-stepper"]
   [x-switch/init!          "x-switch"]
   [x-table-cell/init!      "x-table-cell"]
   [x-text-area/init!       "x-text-area"]
   [x-toast/init!           "x-toast"]
   [x-welcome-tour/init!    "x-welcome-tour"]])

(def ^:private own-font
  "The components whose controls have a font of their own on purpose."
  #{"x-code" "x-otp-input"})

(def ^:private no-control-when-empty
  "The components that create a control only for content or a state this test does not give."
  #{"x-breadcrumbs" "x-stepper" "x-welcome-tour"})

(def ^:private page-font "ControlFontTest")

(def ^:private mark "data-control-font-test")

(defn- remove-marked! []
  (run! (fn [^js node] (.remove node))
        (.querySelectorAll js/document (str "[" mark "]"))))

(use-fixtures :each {:after remove-marked!})

(defn- page!
  "An element in the document whose font family is `page-font`."
  []
  (let [page (.createElement js/document "div")]
    (.setAttribute page mark "")
    (set! (.. page -style -fontFamily) (str page-font ", serif"))
    (.appendChild (.-body js/document) page)))

(defn- element!
  "A `tag` element in `page`, with some text and the attributes that make most components show
   their parts."
  [^js page tag]
  (let [el (.createElement js/document tag)]
    (run! (fn [[k v]] (.setAttribute el k v))
          {"label" "L" "text" "T" "heading" "H" "message" "M" "placeholder" "P" "value" "V"})
    (set! (.-textContent el) "content")
    (.appendChild page el)))

(defn- within
  "Every element in the shadow tree of `el`, and in the shadow trees inside it."
  [^js el]
  (when-let [^js root (.-shadowRoot el)]
    (mapcat (fn [^js node] (cons node (within node)))
            (array-seq (.querySelectorAll root "*")))))

(defn- control? [^js node]
  (contains? #{"BUTTON" "INPUT" "SELECT" "TEXTAREA"} (.-tagName node)))

(defn- font-of [^js node]
  (.-fontFamily (js/getComputedStyle node)))

(deftest a-native-control-takes-the-font-family-of-the-page
  (doseq [[init! tag] cases
          :when (not (own-font tag))]
    (init!)
    (testing tag
      (let [controls (filter control? (within (element! (page!) tag)))]
        (is (= (contains? no-control-when-empty tag) (empty? controls)) "whether it shows a control agrees with the list")
        (is (every? #(.includes (font-of %) page-font) controls))))))
