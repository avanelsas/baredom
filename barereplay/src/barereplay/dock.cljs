(ns barereplay.dock
  (:require
   [barebuild.utils :as utils]
   [baredom.utils.component :as comp]
   [baredom.utils.dom :as du]
   [barereplay.label :as label]
   [barereplay.reconstruct :as reconstruct]
   [barereplay.store :as store]
   [clojure.string :as str]))

(def ^:private styles
  "
  :host { display: contents; }

  .panel::part(body) { scrollbar-gutter: stable; }

  .body {
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  }

  .status { display: flex; align-items: baseline; gap: 0.5rem; }

  .badge {
  font-weight: 700;
  font-size: 0.72rem;
  letter-spacing: 0.04em;
  padding: 0.12rem 0.45rem;
  border-radius: 0.35rem;
  color: #fff;
  background: var(--x-color-warning, #b45309);
  }

  .badge[data-live] { background: var(--x-color-success, #108c48); }

  .count {
  color: var(--x-color-text-muted, #64748b);
  font-variant-numeric: tabular-nums;
  }

  .event {
  color: var(--x-color-text-muted, #64748b);
  font-size: 0.7rem;
  line-height: 1.3;
  height: 2.6em;
  overflow: hidden;
  overflow-wrap: anywhere;
  }

  .history {
  font-size: 0.68rem;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: var(--x-color-text-muted, #64748b);
  }

  .transport { display: flex; align-items: center; gap: 0.2rem; }
  .transport x-slider { flex: 1 1 auto; display: block; }
  .nav { flex: 0 0 auto; }
  .transport .nav {
  --x-button-padding-inline: 0.25rem;
  --x-button-height-md: 1.9rem;
  --x-button-ghost-bg-hover: rgba(127, 127, 127, 0.2);
  --x-button-ghost-bg-active: rgba(127, 127, 127, 0.32);
  }
  .nav svg { display: block; width: 15px; height: 15px; }

  .log {
  display: block;
  max-height: 40vh;
  overflow-y: auto;
  scrollbar-gutter: stable;
  }

  .log x-timeline-item {
  --x-timeline-item-title-font-size: 0.75rem;
  --x-timeline-item-label-width: 0;
  }

  .data summary {
  cursor: pointer;
  user-select: none;
  color: var(--x-color-text-muted, #64748b);
  font-size: 0.68rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  }

  .data[open] summary { margin-bottom: 0.4rem; }
  .data-code { display: block; }

  @media (prefers-color-scheme: dark) {
  .count, .event, .history { color: var(--x-color-text-muted, #94a3b8); }
  }
  ")

(def ^:private tag "barereplay-dock")
(def ^:private k-state "__brDockState")
(def ^:private k-view "__brDockView")
(def ^:private k-live-url "__brDockLiveUrl")

(def ^:private svg-start "M6 6h2v12H6zm3.5 6l8.5 6V6z")
(def ^:private svg-prev  "M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6z")
(def ^:private svg-next  "M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6z")
(def ^:private svg-end   "M6 18l8.5-6L6 6v12zM16 6v12h2V6z")

(defn- data-json [entries n]
  (if-let [d (label/detail-at entries n)]
    (js/JSON.stringify (clj->js d) nil 2)
    "No request/response at this step."))

(defn- project! [entries n]
  (doseq [[_ m] (reconstruct/resources-at entries n)]
    (let [^js el (:el m)
          value  (:value m)]
      (when (and el value)
        (.projectResource el value)))))

(defn- set-disabled! [^js btn disabled?]
  (if disabled?
    (du/set-attr! btn "disabled" "")
    (du/remove-attr! btn "disabled")))

(defn- make-item! [entries i]
  (let [^js item (.createElement js/document "x-timeline-item")]
    (du/set-attr! item "title" (label/event->label (:event (nth entries i))))
    item))

(defn- grow-log! [^js timeline entries from to]
  (doseq [i (range from to)]
    (.appendChild timeline (make-item! entries i))))

(defn- paint-statuses! [^js timeline n]
  (let [items (.-children timeline)]
    (dotimes [i (.-length items)]
      (du/set-attr! (.item items i) "status" (label/item-status (inc i) n)))))

(defn- scroll-active! [^js timeline n]
  (when (pos? n)
    (when-let [^js item (.item (.-children timeline) (dec n))]
      (.scrollIntoView item #js {:block "nearest"}))))

(defn- render-status! [refs entries n]
  (let [^js badge    (:badge refs)
        ^js count-el (:count refs)
        ^js event-el (:event refs)
        {:keys [live? total event]} (label/status entries n)]
    (set! (.-textContent badge) (if live? "LIVE" "REPLAYING"))
    (if live?
      (du/set-attr! badge "data-live" "")
      (du/remove-attr! badge "data-live"))
    (set! (.-textContent count-el) (str n " / " total))
    (set! (.-textContent event-el) (if event (label/event->label event) ""))))

(defn- render-log! [refs entries n]
  (let [^js timeline (:timeline refs)
        total        (count entries)
        have         (.-length (.-children timeline))]
    (cond
      (> have total) (do (set! (.-innerHTML timeline) "")
                         (grow-log! timeline entries 0 total))
      (< have total) (grow-log! timeline entries have total))
    (paint-statuses! timeline n)
    (scroll-active! timeline n)))

(defn- render-data! [refs entries n]
  (let [^js data-code (:data refs)]
    (set! (.-code data-code) (data-json entries n))))

(defn- render-nav! [refs n total]
  (set-disabled! (:start refs) (<= n 0))
  (set-disabled! (:prev refs) (<= n 0))
  (set-disabled! (:next refs) (>= n total))
  (set-disabled! (:end refs) (>= n total)))

(defn- render-slider! [refs n total]
  (let [^js slider (:slider refs)]
    (du/set-attr! slider "max" (str total))
    (du/set-attr! slider "value" (str n))))

(defn- reconstructed-url
  "Reconstruct the current replay url from its pathname and params"
  [entries n]
  (let [params   (js/URLSearchParams. (.-search js/location))
        pathname (.-pathname js/location)]
    (doseq [[rid {:keys [value]}] (reconstruct/resources-at entries n)]
      (doseq [k (utils/owned-url-keys rid (js/Array.from (.keys params)))]
        (.delete params k))
      (let [prefix (utils/url-prefix rid)]
        (doseq [[k v] (:url-intent value)]
          (.set params (str prefix (name k)) (str v)))))
    (let [qs (.toString params)]
      (if (str/blank? qs) pathname (str pathname "?" qs)))))

(defn- sync-url!
 "Update the URL during a replay. If n>=total we are back at the live url."
  [^js el entries n total]
  (if (>= n total)
    (when-let [saved (du/getv el k-live-url)]
      (.replaceState js/history nil "" saved)
      (du/setv! el k-live-url nil))
    (do
      (when-not (du/getv el k-live-url)
        (du/setv! el k-live-url (str (.-pathname js/location) (.-search js/location))))
      (.replaceState js/history nil "" (reconstructed-url entries n)))))

(defn- render! [^js el refs entries n]
  (let [total (count entries)
        view  {:n n :total total}
        prev  (du/getv el k-view)]
    (when (not= view prev)
      (when (and (not= (:n prev) n)
                 (not (and (= (:n prev) (:total prev)) (= n total))))
        (sync-url! el entries n total)
        (project! entries n))
      (du/setv! el k-view view)
      (render-status! refs entries n)
      (render-log! refs entries n)
      (render-data! refs entries n)
      (render-nav! refs n total)
      (render-slider! refs n total))))

(defn- apply-at! [^js el refs entries n pinned?]
  (du/setv! el k-state {:n n :pinned? pinned?})
  (render! el refs entries n))

(defn- go! [^js el refs n]
  (let [entries (store/entries)
        total   (count entries)
        n'      (label/clamp n 0 total)]
    (apply-at! el refs entries n' (= n' total))))

(defn- icon-svg [path]
  (str "<svg viewBox='0 0 24 24' fill='currentColor' aria-hidden='true'><path d='"
       path "'/></svg>"))

(defn- nav-btn [cls aria path]
  (str "<x-button class='nav " cls "' variant='ghost' aria-label='" aria "'>"
       (icon-svg path)
       "</x-button>"))

(defn- markup []
  (str "<style>" styles "</style>"
       "<x-floating-panel class='panel' open label='BareReplay'>"
       "<span slot='header'>BareReplay</span>"
       "<div class='body'>"
       "<div class='status'><span class='badge'></span><span class='count'></span></div>"
       "<div class='event'></div>"
       "<div class='history'>History</div>"
       "<div class='transport'>"
       (nav-btn "nav-start" "Jump to start" svg-start)
       (nav-btn "nav-prev" "Step back" svg-prev)
       "<x-slider class='slider' min='0' step='1' aria-label='History'></x-slider>"
       (nav-btn "nav-next" "Step forward" svg-next)
       (nav-btn "nav-end" "Jump to live" svg-end)
       "</div>"
       "<x-timeline class='log' position='end'></x-timeline>"
       "<details class='data'>"
       "<summary>Data</summary>"
       "<x-code class='data-code' language='json' show-copy wrap max-lines='12'></x-code>"
       "</details>"
       "</div>"
       "</x-floating-panel>"))

(defn- build-refs [^js root]
  {:panel    (.querySelector root "x-floating-panel")
   :badge    (.querySelector root ".badge")
   :count    (.querySelector root ".count")
   :event    (.querySelector root ".event")
   :slider   (.querySelector root "x-slider")
   :timeline (.querySelector root "x-timeline")
   :data     (.querySelector root ".data-code")
   :start    (.querySelector root ".nav-start")
   :prev     (.querySelector root ".nav-prev")
   :next     (.querySelector root ".nav-next")
   :end      (.querySelector root ".nav-end")})

(defn- position-top-right! [^js panel]
  (js/requestAnimationFrame
   (fn []
     (let [margin   8
           ^js part (some-> (.-shadowRoot panel) (.querySelector "[part=panel]"))
           w        (if part (.-width (.getBoundingClientRect part)) 352)
           x        (max margin (- (.-innerWidth js/window) w margin))]
       (du/set-attr! panel "x" (str x))
       (du/set-attr! panel "y" (str margin))))))

(defn mount!
  "Mounts the replay dock"
  []
  (when-not (.querySelector js/document tag)
    (let [add! (fn [] (.appendChild (.-body js/document)
                                    (.createElement js/document tag)))]
      (if (.-body js/document)
        (add!)
        (.addEventListener js/document "DOMContentLoaded" add! #js {:once true})))))

(defn unmount!
  "unmounts the replay dock"
  []
  (when-let [^js el (.querySelector js/document tag)]
    (.remove el)))

(defn- current-n [^js el]
  (:n (du/getv el k-state)))

(defn- wire! [^js el ^js root]
  (let [refs (build-refs root)]
    (.addEventListener (:slider refs) "x-slider-input"
                       (fn [^js e]
                         (let [entries (store/entries)
                               total   (count entries)
                               n       (label/clamp (js/Math.round (.. e -detail -value)) 0 total)]
                           (apply-at! el refs entries n (= n total)))))
    (.addEventListener (:timeline refs) "x-timeline-select"
                       (fn [^js e]
                         (let [i (.. e -detail -index)]
                           (when (number? i)
                             (go! el refs (inc i))))))
    (.addEventListener (:start refs) "press" (fn [_] (go! el refs 0)))
    (.addEventListener (:prev refs) "press" (fn [_] (go! el refs (dec (current-n el)))))
    (.addEventListener (:next refs) "press" (fn [_] (go! el refs (inc (current-n el)))))
    (.addEventListener (:end refs) "press" (fn [_] (go! el refs (count (store/entries)))))
    (position-top-right! (:panel refs))
    (go! el refs (count (store/entries)))
    (store/subscribe!
     (fn [entries]
       (let [{:keys [n pinned?]} (du/getv el k-state)]
         (if pinned?
           (go! el refs (count entries))
           (render! el refs entries n)))))))

(defn- connected! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})]
    (set! (.-innerHTML root) (markup))
    (wire! el root)))

(defn- disconnected! [^js el]
  (when-let [saved (du/getv el k-live-url)]
    (.replaceState js/history nil "" saved)
    (du/setv! el k-live-url nil))
  (store/unsubscribe!))

(def ^:private element-opts
  "Declarative class options passed to component/register!. `:internal? true`
  keeps the dev-tool lifecycle hook from firing on the dock's own
  connect/disconnect. This prevents those records from polluting every trace.
  There are no observed attributes. The no-op attribute-changed-fn is mandatory but does not do anything
  (the callback will never fire)."
  {:internal?            true
   :observed-attributes  #js []
   :connected-fn         connected!
   :disconnected-fn      disconnected!
   :attribute-changed-fn (fn [_ _ _ _] nil)})

(defn register! []
  (comp/register! "barereplay-dock"
                  element-opts))
