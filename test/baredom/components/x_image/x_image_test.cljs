(ns baredom.components.x-image.x-image-test
  (:require [cljs.test :refer-macros [deftest is use-fixtures async]]
            [baredom.components.x-image.x-image :as x]
            [baredom.components.x-image.model   :as model]))

(x/init!)

;; 1x1 transparent PNG as data URL — loads synchronously in Chrome.
(def valid-png-data-url
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+ip1sAAAAASUVORK5CYII=")

;; Syntactically-valid data URL whose base64 payload decodes to bytes
;; that are not a PNG — the network stack accepts the URL, the image
;; decoder rejects the content, and the <img> fires `error`. We
;; deliberately avoid a syntactically-malformed URL (e.g. !!!not-valid!!!)
;; because Chrome's URL parser would reject that with a
;; `net::ERR_INVALID_URL` console error before the image element ever
;; sees it — extra noise that obscures real failures during a test run.
(def invalid-img-url
  "data:image/png;base64,SGVsbG8=")

(defn cleanup-dom! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node)))

;; Suppress the x-image missing-alt console.warn during tests.
(def ^:private orig-warn js/console.warn)

(defn- silence-alt-warn! []
  (set! js/console.warn
        (fn [& args]
          (when-not (and (string? (first args))
                         (.includes ^js (first args) "[x-image] Missing alt"))
            (.apply orig-warn js/console (to-array args))))))

(defn- restore-warn! []
  (set! js/console.warn orig-warn))

(use-fixtures :each
  {:before (fn [] (silence-alt-warn!) (cleanup-dom!))
   :after  (fn [] (cleanup-dom!) (restore-warn!))})

(defn ^js make-el []
  (.createElement js/document model/tag-name))

(defn ^js append! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn ^js shadow-part [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

;; ── Registration ─────────────────────────────────────────────────────────────
(deftest registration-test
  (is (some? (.get js/customElements model/tag-name))))

;; ── Shadow DOM structure ─────────────────────────────────────────────────────
(deftest shadow-structure-test
  (let [^js el (append! (make-el))]
    (is (some? (.-shadowRoot el)))
    (is (some? (shadow-part el "[part=frame]")))
    (is (some? (shadow-part el "[part=shimmer]")))
    (is (some? (shadow-part el "[part=image]")))
    (is (some? (shadow-part el "[part=error]")))
    (is (some? (shadow-part el "slot[name=error]")))
    (is (some? (shadow-part el "[part=error-default]")))))

;; ── Default rendered state ───────────────────────────────────────────────────
(deftest default-state-test
  (let [^js el (append! (make-el))]
    (is (= "loading" (.getAttribute el "data-state")))
    (is (= "loading" (.-state el)))
    (is (= "img" (.getAttribute el "role")))))

;; ── alt attribute ────────────────────────────────────────────────────────────
(deftest alt-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-alt "A red square")
    (let [^js img (shadow-part el "[part=image]")]
      (is (= "A red square" (.-alt img)))
      (is (= "img" (.getAttribute el "role")))
      (is (not (.hasAttribute el "aria-hidden"))))))

;; ── decorative ───────────────────────────────────────────────────────────────
(deftest decorative-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-decorative "")
    (.setAttribute el model/attr-alt "ignored")
    (let [^js img (shadow-part el "[part=image]")]
      (is (= "" (.-alt img)))
      (is (= "true" (.getAttribute el "aria-hidden")))
      (is (not (.hasAttribute el "role"))))))

;; ── ratio ────────────────────────────────────────────────────────────────────
(deftest ratio-valid-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-ratio "16:9")
    (let [^js frame (shadow-part el "[part=frame]")]
      (is (= "16 / 9" (.. frame -style -aspectRatio))))))

(deftest ratio-auto-clears-test
  (let [^js el    (append! (make-el))
        ^js frame (shadow-part el "[part=frame]")]
    (.setAttribute el model/attr-ratio "16:9")
    (is (= "16 / 9" (.. frame -style -aspectRatio)))
    (.setAttribute el model/attr-ratio "auto")
    (is (= "" (.. frame -style -aspectRatio)))))

(deftest ratio-invalid-warns-and-clears-test
  (let [warned (atom [])
        orig   js/console.warn]
    (set! js/console.warn (fn [& args] (swap! warned conj (apply str args))))
    (try
      (let [^js el (append! (make-el))]
        (.setAttribute el model/attr-ratio "nonsense")
        (let [^js frame (shadow-part el "[part=frame]")]
          (is (= "" (.. frame -style -aspectRatio)))
          (is (pos? (count @warned)))))
      (finally
        (set! js/console.warn orig)))))

;; ── fit & position ───────────────────────────────────────────────────────────
(deftest fit-and-position-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-fit "contain")
    (.setAttribute el model/attr-position "top right")
    (let [^js img (shadow-part el "[part=image]")]
      (is (= "contain"   (.. img -style -objectFit)))
      (is (= "right top" (.. img -style -objectPosition))))))

;; ── loading attribute ────────────────────────────────────────────────────────
(deftest loading-attribute-test
  (let [^js el (append! (make-el))]
    (.setAttribute el model/attr-loading "eager")
    (let [^js img (shadow-part el "[part=image]")]
      (is (= "eager" (.-loading img))))))

;; ── x-image-load event ───────────────────────────────────────────────────────
(deftest load-event-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.addEventListener el model/event-load
                         (fn [^js e] (swap! events conj (.-detail e))))
      (.setAttribute el model/attr-alt "Test")
      (.setAttribute el model/attr-src valid-png-data-url)
      ;; Data URLs may load async in some browsers — poll briefly.
      (let [start (.now js/Date)]
        (letfn [(check []
                  (cond
                    (pos? (count @events))
                    (let [^js d (first @events)]
                      (is (= valid-png-data-url (.-src d)))
                      (is (pos? (.-naturalWidth d)))
                      (is (pos? (.-naturalHeight d)))
                      (is (= "loaded" (.getAttribute el "data-state")))
                      (is (= "loaded" (.-state el)))
                      (done))

                    (> (- (.now js/Date) start) 2000)
                    (do (is false "timeout waiting for load event") (done))

                    :else
                    (js/setTimeout check 20)))]
          (check))))))

;; ── An image that the document has already ───────────────────────────────────

;; A picture that the document showed before is there at once for a new element, as it is
;; when a prerendered page is taken over. The data URL below is of its own, so that no
;; other test has loaded it.
(def ^:private red-png-data-url
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg==")

(deftest image-already-there-test
  (async done
    (let [^js held (js/Image.)]
      (set! (.-onload held)
            (fn []
              (.appendChild (.-body js/document) held)
              (let [^js el (make-el)
                    events (atom [])]
                (.addEventListener el model/event-load (fn [^js e] (swap! events conj (.-detail e))))
                (.setAttribute el model/attr-alt "Test")
                (.setAttribute el model/attr-src red-png-data-url)
                (append! el)
                ;; Loaded in the same turn: no frame shows the shimmer.
                (is (= "loaded" (.getAttribute el "data-state")))
                (is (= 1 (count @events)))
                ;; The load event of the inner image comes later, and says nothing twice.
                (js/setTimeout
                 (fn []
                   (is (= "loaded" (.-state el)))
                   (is (= 1 (count @events)))
                   (.remove held)
                   (done))
                 100))))
      (set! (.-onerror held) (fn [] (is false "the test image did not load") (done)))
      (set! (.-src held) red-png-data-url))))

;; ── x-image-error event ──────────────────────────────────────────────────────
(deftest error-event-test
  (async done
    (let [^js el (append! (make-el))
          events (atom [])]
      (.addEventListener el model/event-error
                         (fn [^js e] (swap! events conj (.-detail e))))
      (.setAttribute el model/attr-alt "Missing")
      (.setAttribute el model/attr-src invalid-img-url)
      (let [start (.now js/Date)]
        (letfn [(check []
                  (cond
                    (pos? (count @events))
                    (let [^js d (first @events)]
                      (is (= invalid-img-url (.-src d)))
                      (is (= "error" (.getAttribute el "data-state")))
                      (is (= "error" (.-state el)))
                      (done))

                    (> (- (.now js/Date) start) 2000)
                    (do (is false "timeout waiting for error event") (done))

                    :else
                    (js/setTimeout check 20)))]
          (check))))))

;; ── src change reloads ───────────────────────────────────────────────────────
(deftest src-change-reloads-test
  (async done
    (let [^js el (append! (make-el))
          loads  (atom [])]
      (.addEventListener el model/event-load
                         (fn [^js e] (swap! loads conj (.-src (.-detail e)))))
      (.setAttribute el model/attr-alt "First")
      (.setAttribute el model/attr-src valid-png-data-url)
      (let [start (.now js/Date)]
        (letfn [(wait-first []
                  (cond
                    (pos? (count @loads))
                    ;; First loaded — now change src and expect a second load.
                    (let [second-url (str valid-png-data-url "#2")]
                      (.setAttribute el model/attr-src second-url)
                      (let [t0 (.now js/Date)]
                        (letfn [(wait-second []
                                  (cond
                                    (>= (count @loads) 2)
                                    (do
                                      (is (= valid-png-data-url (first @loads)))
                                      (is (= (str valid-png-data-url "#2") (second @loads)))
                                      (done))

                                    (> (- (.now js/Date) t0) 2000)
                                    (do (is false "timeout waiting for second load") (done))

                                    :else
                                    (js/setTimeout wait-second 20)))]
                          (wait-second))))

                    (> (- (.now js/Date) start) 2000)
                    (do (is false "timeout waiting for first load") (done))

                    :else
                    (js/setTimeout wait-first 20)))]
          (wait-first))))))

;; ── Reconnect: no listener doubling ──────────────────────────────────────────
(deftest reconnect-no-double-fire-test
  (async done
    (let [^js el (make-el)
          loads  (atom 0)]
      (.addEventListener el model/event-load
                         (fn [_e] (swap! loads inc)))
      (.setAttribute el model/attr-alt "X")
      ;; First connect + load
      (.appendChild (.-body js/document) el)
      (.setAttribute el model/attr-src valid-png-data-url)
      (let [start (.now js/Date)]
        (letfn [(after-first []
                  (cond
                    (pos? @loads)
                    (do
                      ;; Remove + re-append; change src to trigger another load.
                      (.remove el)
                      (.appendChild (.-body js/document) el)
                      (.setAttribute el model/attr-src (str valid-png-data-url "#r"))
                      (let [t0 (.now js/Date)]
                        (letfn [(after-second []
                                  (cond
                                    (= 2 @loads) (done)
                                    (> @loads 2)
                                    (do (is false
                                            (str "expected exactly 2 load events, got " @loads))
                                        (done))
                                    (> (- (.now js/Date) t0) 2000)
                                    (do (is false "timeout after reconnect") (done))
                                    :else
                                    (js/setTimeout after-second 20)))]
                          (after-second))))

                    (> (- (.now js/Date) start) 2000)
                    (do (is false "timeout before reconnect") (done))

                    :else
                    (js/setTimeout after-first 20)))]
          (after-first))))))

;; ── Property accessors ───────────────────────────────────────────────────────
(deftest src-property-test
  (let [^js el (append! (make-el))]
    (is (= "" (.-src el)))
    (set! (.-src el) "/a.png")
    (is (= "/a.png" (.getAttribute el model/attr-src)))
    (is (= "/a.png" (.-src el)))
    (set! (.-src el) "")
    (is (not (.hasAttribute el model/attr-src)))))

(deftest alt-property-test
  (let [^js el (append! (make-el))]
    (set! (.-alt el) "hello")
    (is (= "hello" (.getAttribute el model/attr-alt)))
    (is (= "hello" (.-alt el)))))

(deftest decorative-property-test
  (let [^js el (append! (make-el))]
    (is (false? (.-decorative el)))
    (set! (.-decorative el) true)
    (is (.hasAttribute el model/attr-decorative))
    (is (true? (.-decorative el)))
    (set! (.-decorative el) false)
    (is (not (.hasAttribute el model/attr-decorative)))))

(deftest ratio-property-test
  (let [^js el (append! (make-el))]
    (set! (.-ratio el) "4:3")
    (is (= "4:3" (.getAttribute el model/attr-ratio)))
    (let [^js frame (shadow-part el "[part=frame]")]
      (is (= "4 / 3" (.. frame -style -aspectRatio))))))

(deftest fit-property-test
  (let [^js el (append! (make-el))]
    (set! (.-fit el) "contain")
    (is (= "contain" (.getAttribute el model/attr-fit)))))

(deftest state-property-test
  (let [^js el (append! (make-el))]
    (is (= "loading" (.-state el)))))

;; ── naturalWidth / naturalHeight (pre-load) ──────────────────────────────────
(deftest natural-dimensions-pre-load-test
  (let [^js el (append! (make-el))]
    (is (= 0 (.-naturalWidth el)))
    (is (= 0 (.-naturalHeight el)))))
