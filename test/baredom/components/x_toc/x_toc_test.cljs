(ns baredom.components.x-toc.x-toc-test
  (:require [cljs.test :refer-macros [deftest is async use-fixtures]]
            [baredom.components.x-toc.x-toc :as x-toc]
            [baredom.components.x-toc.model :as model]
            [baredom.test-writes :as writes]))

(def ^:private fixture-class "x-toc-test-fixture")

(defn- cleanup! []
  (doseq [^js node (array-seq (.querySelectorAll js/document (str model/tag-name ",." fixture-class)))]
    (.remove node)))

(use-fixtures :each
  {:before (fn [] (x-toc/init!))
   :after  cleanup!})

;; ── Helpers ───────────────────────────────────────────────────────────────
(defn- make-heading [[tag text id]]
  (let [^js h (.createElement js/document tag)]
    (set! (.-textContent h) text)
    (when id (.setAttribute h "id" id))
    h))

(defn- make-spacer []
  (let [^js div (.createElement js/document "div")]
    (set! (.. div -style -height) "600px")
    div))

(defn- make-article
  "An article `id` with a tall spacer after each of `headings`, a vector of [tag text id?]."
  [id headings]
  (let [^js article (.createElement js/document "article")]
    (.setAttribute article "id" id)
    (doseq [h headings]
      (.appendChild article (make-heading h))
      (.appendChild article (make-spacer)))
    article))

(defn- mount-scroller!
  "A 300px tall scrolling box holding `article`, in the body."
  [^js article]
  (let [^js box (.createElement js/document "div")]
    (.add (.-classList box) fixture-class)
    (set! (.. box -style -height) "300px")
    (set! (.. box -style -overflowY) "auto")
    (.appendChild box article)
    (.appendChild (.-body js/document) box)
    box))

(defn- mount-toc!
  ([] (mount-toc! {}))
  ([attrs]
   (let [^js el (.createElement js/document model/tag-name)]
     (doseq [[k v] attrs] (.setAttribute el k v))
     (.appendChild (.-body js/document) el)
     el)))

(def ^:private three-headings
  [["h2" "Intro"] ["h3" "Details" "details"] ["h2" "Source code"]])

(defn- setup!
  "A scrolling article with `headings` and an x-toc for it."
  ([] (setup! three-headings {}))
  ([headings attrs]
   (let [box (mount-scroller! (make-article "post" headings))
         el  (mount-toc! (merge {"for" "post"} attrs))]
     {:el el :box box})))

(defn- items [^js el]
  (array-seq (.querySelectorAll (.-shadowRoot el) "[part~='item']")))

(defn- links [^js el]
  (array-seq (.querySelectorAll (.-shadowRoot el) "[part~='link']")))

(defn- hrefs [^js el]
  (mapv (fn href [^js a] (.getAttribute a "href")) (links el)))

(defn- current-href [^js el]
  (some-> (.querySelector (.-shadowRoot el) "[aria-current='location']") (.getAttribute "href")))

(defn- part [^js el name]
  (.querySelector (.-shadowRoot el) (str "[part~='" name "']")))

(defn- later [ms f]
  (js/setTimeout f ms))

;; ── Registration and structure ────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

(deftest shadow-structure-test
  (let [{:keys [^js el]} (setup!)]
    (is (= "NAV" (.-tagName (part el "nav"))))
    (is (= "OL" (.-tagName (part el "list"))))
    (is (= "true" (.getAttribute (part el "indicator") "aria-hidden")))))

;; ── Entries ───────────────────────────────────────────────────────────────
(deftest entries-link-to-the-headings-test
  (let [{:keys [^js el]} (setup!)]
    (is (= ["#intro" "#details" "#source-code"] (hrefs el)))
    (is (= ["Intro" "Details" "Source code"]
           (mapv (fn text [^js a] (.-textContent a)) (links el))))
    (is (= ["1" "2" "1"] (mapv (fn level [^js li] (.getAttribute li "data-level")) (items el))))))

(deftest missing-ids-are-written-and-own-ids-kept-test
  (setup!)
  (let [hs (array-seq (.querySelectorAll js/document "#post h2, #post h3"))]
    (is (= ["intro" "details" "source-code"] (mapv (fn id [^js h] (.-id h)) hs)))))

(deftest selector-chooses-the-entries-test
  (let [{:keys [^js el]} (setup! three-headings {"selector" "h2"})]
    (is (= ["#intro" "#source-code"] (hrefs el)))))

(deftest invalid-selector-gives-an-empty-list-test
  (let [{:keys [^js el]} (setup! three-headings {"selector" "h2["})]
    (is (empty? (links el)))
    (is (.hasAttribute el "data-empty"))))

(deftest missing-container-gives-an-empty-list-test
  (let [^js el (mount-toc! {"for" "nowhere"})]
    (is (empty? (links el)))
    (is (.hasAttribute el "data-empty"))
    (is (nil? (.-activeId el)))))

(deftest new-heading-is-added-and-items-keep-their-nodes-test
  (async done
    (let [{:keys [el]} (setup!)
          ^js first-li (first (items el))]
      (.appendChild (.getElementById js/document "post") (make-heading ["h2" "Further reading"]))
      (later 50
             (fn check []
               (is (= ["#intro" "#details" "#source-code" "#further-reading"] (hrefs el)))
               (is (identical? first-li (first (items el))))
               (done))))))

(deftest changing-the-text-of-a-heading-updates-its-link-test
  (async done
    (let [{:keys [^js el]} (setup!)]
      (set! (.-textContent (.getElementById js/document "details")) "More details")
      (later 50
             (fn check []
               (is (= "More details" (.-textContent (second (links el)))))
               (done))))))

(deftest container-added-later-is-found-test
  (async done
    (let [^js el (mount-toc! {"for" "post"})]
      (is (empty? (links el)))
      (mount-scroller! (make-article "post" three-headings))
      (later 50
             (fn check []
               (is (= 3 (count (links el))))
               (is (not (.hasAttribute el "data-empty")))
               (done))))))

;; ── Label ─────────────────────────────────────────────────────────────────
(deftest without-label-the-nav-has-a-default-name-test
  (let [{:keys [^js el]} (setup!)]
    (is (= model/default-label (.getAttribute (part el "nav") "aria-label")))
    (is (.hasAttribute (part el "label") "hidden"))))

(deftest label-names-the-nav-test
  (let [{:keys [^js el]} (setup! three-headings {"label" "On this page"})
        nav (part el "nav")]
    (is (= "On this page" (.-textContent (part el "label"))))
    (is (= "label" (.getAttribute nav "aria-labelledby")))
    (is (not (.hasAttribute nav "aria-label")))
    (is (not (.hasAttribute (part el "label") "hidden")))))

;; ── Active entry ──────────────────────────────────────────────────────────
(deftest first-entry-is-active-at-the-top-test
  (let [{:keys [^js el]} (setup!)]
    (is (= "intro" (.-activeId el)))
    (is (= "#intro" (current-href el)))))

(deftest scrolling-moves-the-active-entry-test
  (async done
    (let [{:keys [^js el ^js box]} (setup!)
          events (atom [])]
      (.addEventListener el model/event-change (fn record [^js e] (swap! events conj (.-detail e))))
      (set! (.-scrollTop box) 650)
      (later 100
             (fn check []
               (is (= "details" (.-activeId el)))
               (is (= "#details" (current-href el)))
               (is (= 1 (count @events)))
               (let [^js d (first @events)]
                 (is (= "details" (.-id d)))
                 (is (= "intro" (.-previousId d)))
                 (is (= 2 (.-level d)))
                 (is (= 1 (.-index d))))
               (done))))))

(deftest bottom-of-the-scroll-root-activates-the-last-entry-test
  (async done
    (let [{:keys [^js el ^js box]} (setup!)]
      (set! (.-scrollTop box) (.-scrollHeight box))
      (later 100
             (fn check []
               (is (= "source-code" (.-activeId el)))
               (done))))))

(deftest offset-moves-the-activation-line-test
  (async done
    (let [{:keys [^js el ^js box]} (setup! three-headings {"offset" "0px"})]
      (set! (.-scrollTop box) 560)
      (later 100
             (fn check []
               (is (= "intro" (.-activeId el)))
               (done))))))

;; ── Navigation ────────────────────────────────────────────────────────────
(deftest click-navigates-to-the-heading-test
  (let [{:keys [^js el]} (setup!)
        navigated (atom nil)
        ^js h     (.getElementById js/document "source-code")]
    (.addEventListener el model/event-navigate (fn record [^js e] (reset! navigated (.-detail e))))
    (.click ^js (last (links el)))
    (is (= "source-code" (.-id ^js @navigated)))
    (is (= "source-code" (.-activeId el)))
    (is (identical? h (.-activeElement js/document)))
    (is (= "-1" (.getAttribute h "tabindex")))))

(deftest canceled-navigate-changes-nothing-test
  (let [{:keys [^js el]} (setup!)
        ^js h (.getElementById js/document "source-code")]
    (.addEventListener el model/event-navigate (fn cancel [^js e] (.preventDefault e)))
    (.click ^js (last (links el)))
    (is (= "intro" (.-activeId el)))
    (is (not (.hasAttribute h "tabindex")))))

(deftest modifier-click-is-left-to-the-browser-test
  (let [{:keys [^js el]} (setup!)
        navigated (atom false)
        block     (fn block [^js e] (.preventDefault e))]
    (.addEventListener el model/event-navigate (fn record [_] (reset! navigated true)))
    (.addEventListener js/document "click" block)
    (.dispatchEvent ^js (last (links el))
                    (js/MouseEvent. "click" #js {:bubbles true :composed true :cancelable true
                                                 :ctrlKey true}))
    (.removeEventListener js/document "click" block)
    (is (false? @navigated))
    (is (= "intro" (.-activeId el)))))

(deftest update-hash-replaces-the-hash-test
  (let [{:keys [^js el]} (setup! three-headings {"update-hash" ""})
        before (.-href js/location)
        length (.-length js/history)]
    (.click ^js (second (links el)))
    (is (= "#details" (.-hash js/location)))
    (is (= length (.-length js/history)))
    (.replaceState js/history nil "" before)))

(deftest without-update-hash-the-hash-stays-test
  (let [{:keys [^js el]} (setup!)
        before (.-hash js/location)]
    (.click ^js (second (links el)))
    (is (= before (.-hash js/location)))))

;; ── Properties and sticky ─────────────────────────────────────────────────
(deftest properties-reflect-attributes-test
  (let [^js el (mount-toc!)]
    (is (= model/default-selector (.-selector el)))
    (is (= model/default-offset (.-offset el)))
    (set! (.-for el) "post")
    (set! (.-updateHash el) true)
    (set! (.-sticky el) true)
    (is (= "post" (.getAttribute el "for")))
    (is (.hasAttribute el "update-hash"))
    (is (.hasAttribute el "sticky"))))

(deftest active-id-is-read-only-test
  (let [{:keys [^js el]} (setup!)]
    (set! (.-activeId el) "details")
    (is (= "intro" (.-activeId el)))))

(deftest sticky-makes-the-host-sticky-test
  (let [{:keys [^js el]} (setup! three-headings {"sticky" ""})]
    (is (= "sticky" (.-position (js/getComputedStyle el))))))

(deftest hidden-hides-the-host-test
  (let [{:keys [^js el]} (setup! three-headings {"hidden" ""})]
    (is (= "none" (.-display (js/getComputedStyle el))))))

;; ── Writes on other nodes ─────────────────────────────────────────────────
(deftest writes-only-what-it-declares-on-other-nodes-test
  (mount-scroller! (make-article "post" three-headings))
  (let [^js el (.createElement js/document model/tag-name)]
    (.setAttribute el "for" "post")
    (let [written (writes/foreign-writes!
                   el
                   (fn mount-and-click []
                     (.appendChild (.-body js/document) el)
                     (.click ^js (last (links el)))))]
      (is (= {:undeclared #{} :unexercised #{}} (writes/audit model/foreign-writes written))))))

;; ── Indicator ─────────────────────────────────────────────────────────────
(defn- indicator-top [^js el]
  (js/parseFloat (second (re-find #"translateY\(([-\d.]+)px\)"
                                  (.. (part el "indicator") -style -transform)))))

(defn- current-item-top [^js el]
  (.-offsetTop (.-parentElement (.querySelector (.-shadowRoot el) "[aria-current='location']"))))

(deftest indicator-follows-the-active-item-after-a-reflow-test
  (async done
    (let [{:keys [^js el]} (setup! [["h2" "Intro"] ["h2" "A rather long heading that wraps"]
                                    ["h2" "Last"]]
                                   {})]
      (.click ^js (last (links el)))
      (set! (.. el -style -width) "90px")
      (later 100
             (fn check []
               (is (= (current-item-top el) (indicator-top el)))
               (done))))))

;; ── Upgrade ───────────────────────────────────────────────────────────────
(deftest parsed-element-builds-its-list-on-upgrade-test
  (mount-scroller! (make-article "post" three-headings))
  (let [^js holder (.createElement js/document "div")]
    (.add (.-classList holder) fixture-class)
    (.appendChild (.-body js/document) holder)
    (set! (.-innerHTML holder) "<x-toc for=\"post\" label=\"On this page\" sticky></x-toc>")
    (let [^js el (.querySelector holder model/tag-name)]
      (is (= ["#intro" "#details" "#source-code"] (hrefs el)))
      (is (= "intro" (.-activeId el))))))

;; ── Container lifecycle ───────────────────────────────────────────────────
(deftest empty-heading-gets-no-id-test
  (setup! [["h2" "Intro"] ["h2" "  "]] {})
  (let [^js empty-h (second (array-seq (.querySelectorAll js/document "#post h2")))]
    (is (not (.hasAttribute empty-h "id")))))

(deftest removed-container-empties-the-list-test
  (async done
    (let [{:keys [^js el ^js box]} (setup!)]
      (.remove (.getElementById js/document "post"))
      (later 50
             (fn check []
               (is (empty? (links el)))
               (is (nil? (.-activeId el)))
               (.appendChild box (make-article "post" [["h2" "Swapped in"]]))
               (later 50
                      (fn check-swap []
                        (is (= ["#swapped-in"] (hrefs el)))
                        (done))))))))

(deftest changing-for-binds-the-new-container-test
  (let [{:keys [^js el]} (setup!)]
    (mount-scroller! (make-article "other" [["h2" "Elsewhere"]]))
    (.setAttribute el "for" "other")
    (is (= ["#elsewhere"] (hrefs el)))))

(deftest reconnected-element-tracks-scrolling-again-test
  (async done
    (let [{:keys [^js el ^js box]} (setup!)]
      (.remove el)
      (.appendChild (.-body js/document) el)
      (set! (.-scrollTop box) 650)
      (later 100
             (fn check []
               (is (= "details" (.-activeId el)))
               (done))))))

(deftest reflow-above-a-heading-moves-the-active-entry-test
  (async done
    (let [{:keys [^js el ^js box]} (setup!)]
      (set! (.-scrollTop box) 650)
      (later 100
             (fn grow []
               (is (= "details" (.-activeId el)))
               (set! (.. (.querySelector box "article > div") -style -height) "1200px")
               (later 100
                      (fn check []
                        (is (= "intro" (.-activeId el)))
                        (done))))))))

(deftest click-in-a-gap-of-the-list-is-left-alone-test
  (mount-scroller! (make-article "post" three-headings))
  (let [^js page-li (.createElement js/document "li")
        ^js el      (.createElement js/document model/tag-name)
        ev          (js/MouseEvent. "click" #js {:bubbles true :composed true :cancelable true})]
    (.add (.-classList page-li) fixture-class)
    (.setAttribute el "for" "post")
    (.appendChild page-li el)
    (.appendChild (.-body js/document) page-li)
    (.dispatchEvent (part el "list") ev)
    (is (false? (.-defaultPrevented ev)))))

;; ── Lookup and the sticky list ────────────────────────────────────────────
(deftest heading-is-found-in-the-container-when-its-id-is-also-outside-test
  (let [^js decoy (.createElement js/document "div")]
    (.add (.-classList decoy) fixture-class)
    (.setAttribute decoy "id" "intro")
    (.appendChild (.-body js/document) decoy)
    (let [{:keys [^js el]} (setup! [["h2" "Intro" "intro"] ["h2" "Later"]] {})
          ^js h (.querySelector js/document "#post h2")]
      (.click ^js (first (links el)))
      (is (identical? h (.-activeElement js/document))))))

(deftest sticky-list-keeps-the-scroll-of-the-reader-test
  (async done
    (let [headings (mapv (fn heading [i] ["h2" (str "Section " i)]) (range 30))
          {:keys [^js el ^js box]} (setup! headings {"sticky" ""})
          ^js body (part el "body")]
      (.setProperty (.-style el) "--x-toc-sticky-top" "calc(50dvh - 60px)")
      (later 100
             (fn scroll-list []
               (set! (.-scrollTop body) 40)
               (set! (.-scrollTop box) 5)
               (later 100
                      (fn check []
                        (is (= "section-0" (.-activeId el)))
                        (is (= 40 (.-scrollTop body)))
                        (done))))))))
