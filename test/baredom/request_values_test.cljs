(ns baredom.request-values-test
  (:require [baredom.components-table :as table]
            [baredom.requests :as requests]
            [baredom.test-elements :as elements]
            [baremirror.alpha.parts :as parts]
            [cljs.test :refer-macros [async deftest is testing use-fixtures]]))

(def ^:private known-misses
  "What each request event shows today when its request is handled one way, by the row's
   `:name` or event, and the way. A pair that is not named shows what it should."
  {})

(def ^:private mount-ms "The wait after a mount and after `:prepare`." 30)
(def ^:private settle-ms "The wait after a gesture, unless a row gives `:settle`." 100)
(def ^:private blur-ms "The wait after the focus leaves the component." 100)

(run! (fn [[register!]] (register!)) table/components)

(use-fixtures :each {:after elements/remove-all!})

(defn- wait [ms]
  (js/Promise. (fn [resolve] (js/setTimeout resolve ms))))

(defn- part-of [^js el selector]
  (.querySelector (.-shadowRoot el) selector))

(defn- click-part [selector]
  (fn [^js el] (.click (part-of el selector))))

(defn- key-on-part [selector k]
  (fn [^js el]
    (.dispatchEvent (part-of el selector)
                    (js/KeyboardEvent. "keydown" #js {:key k :bubbles true :cancelable true}))))

(defn- type-into [selector text]
  (fn [^js el]
    (let [^js input (part-of el selector)]
      (.focus input)
      (set! (.-value input) text)
      (.dispatchEvent input (js/Event. "input" #js {:bubbles true})))))

(defn- choose-in-select [selector value]
  (fn [^js el]
    (let [^js select (part-of el selector)]
      (set! (.-value select) value)
      (.dispatchEvent select (js/Event. "change" #js {:bubbles true})))))

(defn- call [method & args]
  (fn [^js el] (.apply (unchecked-get el method) el (into-array args))))

(defn- click-in-panel [selector]
  (fn [^js el] (.click (.querySelector (part-of el "[part=panel]") selector))))

(defn- click-day [^js el]
  (.click (part-of el "[part=day][data-iso=\"2026-03-12\"]")))

(defn- click-step [^js el]
  (.click (.querySelector (.item (.querySelectorAll (.-shadowRoot el) "[part=step]") 2)
                          "[part=step-indicator]")))

(defn- click-chip-remove [value]
  (fn [^js el]
    (.click (part-of (part-of el (str "x-chip[data-x-key=\"" value "\"]")) "[part=remove]"))))

(defn- click-child [selector]
  (fn [^js el] (.click (.querySelector el selector))))

(def ^:private options
  "<option value=\"us\">United States</option><option value=\"uk\">United Kingdom</option>
   <option value=\"nl\">Netherlands</option><option value=\"de\">Germany</option>")

(def ^:private fruits
  "<option value=\"apple\">Apple</option><option value=\"banana\">Banana</option>
   <option value=\"cherry\">Cherry</option>")

(def ^:private letters
  "<option value=\"a\">A</option><option value=\"b\">B</option><option value=\"c\">C</option>")

(def ^:private tabs
  "<x-tab value=\"a\">A</x-tab><x-tab value=\"b\">B</x-tab><x-tab value=\"c\">C</x-tab>")

(def ^:private context-menu "<x-context-menu><x-menu-item>A</x-menu-item></x-context-menu>")

(def ^:private slides "<div>One</div><div>Two</div><div>Three</div>")

(def ^:private tour-steps
  (str "<x-welcome-tour-step title=\"One\"></x-welcome-tour-step>"
       "<x-welcome-tour-step title=\"Two\"></x-welcome-tour-step>"
       "<x-welcome-tour-step title=\"Three\"></x-welcome-tour-step>"))

(def ^:private rows
  "Each request event: the markup of its component, the gesture that sends the request, and
   values other than the asked ones. `:prepare` runs before the gesture, `:settle` is how long
   the component takes to come to rest. `:watch` names the one part that is compared, and
   `:none` compares no part. `:name` tells two rows of one event apart."
  [{:event "x-cancel-dialogue-cancel-request" :html "<x-cancel-dialogue open></x-cancel-dialogue>"
    :gesture (click-part "[part=cancel-btn]")}
   {:event "x-cancel-dialogue-confirm-request" :html "<x-cancel-dialogue open></x-cancel-dialogue>"
    :gesture (click-part "[part=confirm-btn]")}
   {:event "x-carousel-change" :html (str "<x-carousel current=\"0\">" slides "</x-carousel>")
    :gesture (click-part "[part=next-btn]") :other {"current" "2"}}
   {:event "x-checkbox-change-request" :html "<x-checkbox></x-checkbox>"
    :gesture (click-part "[part=control]")}
   {:event "x-collapse-toggle" :html "<x-collapse></x-collapse>" :gesture (call "toggle")
    :settle 400}
   {:event "x-combobox-change-request" :html (str "<x-combobox value=\"uk\">" options "</x-combobox>")
    :watch "[part=input]" :prepare (call "show") :gesture (click-in-panel "[data-value=nl]")
    :other {"value" "de"}}
   {:event "x-combobox-toggle" :html (str "<x-combobox>" options "</x-combobox>")
    :watch "[part=input]" :gesture (call "show")}
   {:event "x-command-palette-open-request" :html "<x-command-palette></x-command-palette>"
    :gesture (call "show")}
   {:event "x-command-palette-close-request" :html "<x-command-palette open></x-command-palette>"
    :gesture (call "close")}
   {:event "x-context-menu-open-request" :html context-menu
    :watch :none :gesture (call "openAt" 100 100)}
   {:event "x-context-menu-close-request" :html context-menu
    :watch :none :prepare (call "openAt" 100 100) :gesture (call "close")}
   {:event "x-currency-field-change-request" :html "<x-currency-field value=\"10\"></x-currency-field>"
    :watch "[part=input]" :gesture (type-into "[part=input]" "99") :other {"value" "50"}}
   {:event "x-date-picker-change-request" :html "<x-date-picker open value=\"2026-03-10\"></x-date-picker>"
    :gesture click-day :other {"value" "2026-03-20" "start" nil "end" nil}}
   {:event "x-dropdown-toggle" :html "<x-dropdown></x-dropdown>" :gesture (call "toggle")}
   {:event "x-floating-panel-dismiss-request" :html "<x-floating-panel closable></x-floating-panel>"
    :prepare (call "show") :gesture (click-part "[part=close]")}
   {:event "x-form-field-change-request" :html "<x-form-field value=\"keep\"></x-form-field>"
    :watch "[part=input]" :gesture (type-into "[part=input]" "typed") :other {"value" "other"}}
   {:event "x-multi-combobox-change-request" :watch "[part=chip-area]"
    :html (str "<x-multi-combobox value=\"apple,banana\">" fruits "</x-multi-combobox>")
    :gesture (key-on-part "[part=input]" "Backspace") :other {"value" "cherry"}}
   {:event "x-multi-combobox-change-request" :name "x-multi-combobox-change-request, a chip clicked"
    :watch "[part=chip-area]"
    :html (str "<x-multi-combobox value=\"apple,banana\">" fruits "</x-multi-combobox>")
    :gesture (click-chip-remove "banana") :other {"value" "cherry"}}
   {:event "x-multi-combobox-change-request" :name "x-multi-combobox-change-request, an option added"
    :html (str "<x-multi-combobox value=\"apple\">" fruits "</x-multi-combobox>")
    :prepare (call "show") :gesture (click-in-panel "[data-value=cherry]") :other {"value" "banana"}}
   {:event "x-multi-combobox-toggle" :html (str "<x-multi-combobox>" fruits "</x-multi-combobox>")
    :gesture (call "show")}
   {:event "page-change-request" :html "<x-pagination page=\"1\" total-pages=\"5\"></x-pagination>"
    :gesture (click-part "[part~=button-next]") :other {"page" "4"}}
   {:event "x-popover-toggle" :html "<x-popover></x-popover>" :gesture (call "show")}
   {:event "x-radio-change-request" :html "<x-radio value=\"a\"></x-radio>"
    :gesture (click-part "[part=control]")}
   {:event "x-range-slider-change-request" :html "<x-range-slider start=\"20\" end=\"80\"></x-range-slider>"
    :gesture (key-on-part "[part=thumb-start]" "ArrowRight") :other {"start" "30" "end" "80"}}
   {:event "x-rating-change-request" :html "<x-rating value=\"2\"></x-rating>"
    :gesture (key-on-part "[part=stars]" "ArrowRight") :other {"value" "4"}}
   {:event "x-search-field-change-request" :html "<x-search-field value=\"keep\"></x-search-field>"
    :watch "[part=input]" :gesture (type-into "[part=input]" "typed") :other {"value" "other"}}
   {:event "x-select-change-request" :html (str "<x-select value=\"a\">" letters "</x-select>")
    :watch "[part=select]" :gesture (choose-in-select "[part=select]" "b") :other {"value" "c"}}
   {:event "x-select-change-request" :name "x-select-change-request, after a pick"
    :html (str "<x-select value=\"a\">" letters "</x-select>")
    :watch "[part=select]" :prepare (choose-in-select "[part=select]" "b")
    :gesture (choose-in-select "[part=select]" "c") :other {"value" "a"}}
   {:event "x-slider-change-request" :html "<x-slider value=\"0\"></x-slider>"
    :watch "[part=input]" :gesture (type-into "[part=input]" "75") :other {"value" "30"}}
   {:event "x-stepper-change" :html "<x-stepper steps=\"3\" current=\"0\"></x-stepper>"
    :gesture click-step :other {"current" "1"}}
   {:event "x-switch-change-request" :html "<x-switch></x-switch>"
    :gesture (click-part "[part=control]")}
   {:event "value-change-request" :html (str "<x-tabs value=\"a\">" tabs "</x-tabs>")
    :gesture (click-child "x-tab[value=b]") :other {"value" "c"}}
   {:event "x-text-area-change-request" :html "<x-text-area value=\"keep\"></x-text-area>"
    :watch "textarea" :gesture (type-into "textarea" "typed") :other {"value" "other"}}
   {:event "x-welcome-tour-step-change" :html (str "<x-welcome-tour>" tour-steps "</x-welcome-tour>")
    :prepare (call "start") :gesture (call "next") :other {"step" "2"}}])

(defn- event-requests
  "The requests of each request event, by event, from the event schema of every component."
  []
  (into {}
        (comp (mapcat (comp :events second))
              (keep (fn [[event {:keys [requests]}]] (when requests [event requests]))))
        table/components))

(deftest every-request-event-has-requests-and-a-row
  (is (= (set (keys requests/requests))
         (set (keys (event-requests)))
         (set (map :event rows)))))

(defn- asked
  "The values that the event `e` asks for, by attribute, as `by-attr` names their sources."
  [by-attr ^js e]
  (into {}
        (map (fn [[attr source]]
               [attr (if (keyword? source) (unchecked-get (.-detail e) (name source)) source)]))
        by-attr))

(defn- attrs-of [^js el names]
  (into {} (map (juxt identity (fn [attr] (.getAttribute el attr)))) names))

(defn- written
  "The attribute text that `parts/set-attrs!` writes for `attrs`, by attribute."
  [attrs]
  (let [el (.createElement js/document "div")]
    (parts/set-attrs! el attrs)
    (attrs-of el (keys attrs))))

(defn- all-attrs [^js el]
  (into {}
        (map (juxt (fn [^js a] (.-name a)) (fn [^js a] (.-value a))))
        (array-seq (.-attributes el))))

(def ^:private ignored-attrs
  #{"id" "for" "aria-labelledby" "aria-describedby" "aria-controls" "aria-activedescendant"
    "aria-owns"})

(declare shape)

(defn- children-shape [^js node]
  (into [] (keep shape) (array-seq (.-childNodes node))))

(defn- shown-attr? [[k v]]
  (not (or (ignored-attrs k) (and (= "style" k) (= "" v)))))

(defn- element-shape [^js el]
  (cond-> [(.-localName el)
           (into (sorted-map) (filter shown-attr?) (all-attrs el))
           (children-shape el)]
    (some? (.-shadowRoot el))
    (conj (children-shape (.-shadowRoot el)))

    (#{"input" "textarea" "select"} (.-localName el))
    (conj [(.-value el) (.-checked el)])))

(defn- shape
  "What `node` shows, as data. Style elements, comments and ids are left out."
  [^js node]
  (case (.-nodeType node)
    1 (when-not (= "style" (.-localName node)) (element-shape node))
    3 (let [text (.trim (.-data node))] (when (seq text) [:text text]))
    nil))

(defn- watched-shape
  "What the part of `el` that `row` watches shows: the whole of `el` when it names none."
  [{:keys [watch]} ^js el]
  (cond
    (= :none watch) nil
    (some? watch)   (shape (.querySelector (.-shadowRoot el) watch))
    :else           (shape el)))

(defn- blur-inside! [^js el]
  (some-> (.-shadowRoot el) .-activeElement .blur))

(defn- parsed
  "The first element of `html`, not yet in the document."
  [html]
  (let [holder (.createElement js/document "template")]
    (set! (.-innerHTML holder) html)
    (.. holder -content -firstElementChild)))

(defn- replace-attrs!
  "Gives `el` exactly the attributes of the map `attrs`."
  [^js el attrs]
  (run! (fn [attr] (.removeAttribute el attr)) (keys (all-attrs el)))
  (run! (fn [[k v]] (.setAttribute el k v)) attrs)
  el)

(defn- answer
  "The writes with which one way answers a request: the asked values, none, or `other`."
  [way by-attr other ^js e]
  (case way
    :accept (asked by-attr e)
    :refuse nil
    :other  other))

(defn- answering
  "A listener that answers each request the way `way` and adds its writes to the atom
   `answered`."
  [way by-attr other ^js el answered]
  (fn [^js e]
    (let [writes (answer way by-attr other e)]
      (swap! answered conj writes)
      (parts/set-attrs! el writes)
      (when (not= :accept way)
        (.preventDefault e)))))

(defn- errors-while!
  "Calls `f`, which gives a promise, and resolves to `[value errors]`: what the promise gives,
   and how many errors reached the window until then."
  [f]
  (let [previous (.-onerror js/window)
        errors   (atom 0)
        restore! (fn [] (set! (.-onerror js/window) previous))]
    (set! (.-onerror js/window) (fn [& _] (swap! errors inc) true))
    (-> (js/Promise.resolve)
        (.then f)
        (.then (fn [value] (restore!) [value @errors])
               (fn [error] (restore!) (throw error))))))

(defn- gesture!
  "Runs the gesture of `row` on `el`. True when the gesture itself threw."
  [{:keys [gesture]} ^js el]
  (try (gesture el) false (catch :default _ true)))

(def ^:private field-name "field")

(defn- form-associated? [^js el]
  (some-> (.get js/customElements (.-localName el)) .-formAssociated))

(defn- in-form!
  "Puts `el` in a new form in the document, named when it takes part in forms. Returns `el`."
  [^js el]
  (let [form (.createElement js/document "form")]
    (when (and (form-associated? el) (not (.hasAttribute el "name")))
      (.setAttribute el "name" field-name))
    (.append form el)
    (elements/node-in-body! form)
    el))

(defn- form-data
  "The entries that the form of `in-form!` around `el` submits, in order."
  [^js el]
  (mapv vec (es6-iterator-seq (.entries (js/FormData. (.-parentNode el))))))

(defn- start!
  "Puts the component of `row` in a form in the document and runs `:prepare`."
  [{:keys [html prepare]}]
  (let [el (in-form! (parsed html))]
    (-> (wait mount-ms)
        (.then (fn [] (when prepare (prepare el)) (wait mount-ms)))
        (.then (fn [] {:el el :before (all-attrs el)})))))

(defn- request!
  "Answers the request of `row` the way `way` while its gesture runs, and adds what the page
   then shows."
  [by-attr {:keys [event other settle] :or {settle settle-ms} :as row} way
   {:keys [^js el] :as run}]
  (let [answered (atom [])]
    (.addEventListener el event (answering way by-attr other el answered))
    (-> (errors-while!
         (fn []
           (let [threw? (gesture! row el)]
             (-> (wait settle)
                 (.then (fn [] (blur-inside! el) (wait blur-ms)))
                 (.then (constantly threw?))))))
        (.then (fn [[threw? errors]]
                 (assoc run
                        :threw?   threw?
                        :errors   errors
                        :answered @answered
                        :attrs    (attrs-of el (keys by-attr))
                        :final    (all-attrs el)
                        :shown    [(watched-shape row el) (form-data el)]))))))

(defn- fresh!
  "Removes the component, and adds what a new one with its final attributes shows and submits."
  [{:keys [html settle] :or {settle settle-ms} :as row} {:keys [^js el final] :as run}]
  (.remove (.-parentNode el))
  (let [fresh (in-form! (replace-attrs! (parsed html) final))]
    (-> (wait (+ settle blur-ms))
        (.then (fn []
                 (let [fresh-shape [(watched-shape row fresh) (form-data fresh)]]
                   (.remove (.-parentNode fresh))
                   (assoc run :fresh fresh-shape)))))))

(defn- expected
  "The request attributes after the answers: those before the gesture, with the answers written.
   An attribute that is absent is nil."
  [by-attr {:keys [before answered]}]
  (let [names (keys by-attr)]
    (merge (zipmap names (repeat nil))
           (select-keys before names)
           (written (select-keys (apply merge answered) names)))))

(defn- misses
  "The misses of a run: a gesture or listener that throws, no request, other attributes than the
   answers leave, or a component that shows other than a new one with the same attributes."
  [by-attr {:keys [threw? errors answered attrs shown fresh] :as run}]
  (cond-> #{}
    threw?                                                   (conj :gesture-throws)
    (pos? errors)                                            (conj :throws)
    (empty? answered)                                        (conj :not-sent)
    (and (seq answered) (not= (expected by-attr run) attrs)) (conj :attrs)
    (not= fresh shown)                                       (conj :shown)))

(defn- run-case!
  "Mounts the component of `row`, answers its request the way `way`, and gives the misses."
  [by-attr row way]
  (-> (start! row)
      (.then (partial request! by-attr row way))
      (.then (partial fresh! row))
      (.then (partial misses by-attr))))

(defn- cases
  "The rows that can be answered the way `way`, each with `way`."
  [way]
  (for [{:keys [other] :as row} rows
        :when                   (or (not= :other way) other)]
    [row way]))

(defn- report! [case-name way found]
  (testing (str case-name ", answered " (name way) ", shows the answer")
    (is (= (get known-misses [case-name way] #{}) found))))

(defn- check-case! [by-event p [{:keys [event] :as row} way]]
  (.then p (fn []
             (.then (run-case! (by-event event) row way)
                    (partial report! (or (:name row) event) way)))))

(defn- check-all!
  "Checks every case of `way` in turn, and calls `done` after the last."
  [way done]
  (.then (reduce (partial check-case! (event-requests)) (js/Promise.resolve) (cases way))
         (fn [] (done))))

(deftest an-accepted-request-shows-the-asked-value
  (async done (check-all! :accept done)))

(deftest a-refused-request-shows-the-value-before
  (async done (check-all! :refuse done)))

(deftest a-request-answered-with-another-value-shows-that-value
  (async done (check-all! :other done)))
