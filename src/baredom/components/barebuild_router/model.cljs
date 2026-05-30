(ns baredom.components.barebuild-router.model
  (:require [clojure.string :as str]))

;; barebuild-router owns the URL-as-data. This namespace is the *pure* routing
;; calculation — `(patterns, url) -> match` — with no DOM and no side effects.
;; The effect shell (barebuild_router.cljs) holds caches/projections only; every
;; decision below is a value-returning function, callable and testable on its own.
;; `parse-path-pattern` and `match-path` are shared with barebuild-route, which
;; requires this namespace rather than duplicating the calculation.

(def tag-name "barebuild-router")

(def attr-base "base")

(def observed-attributes #js [attr-base])

;; ── Events ───────────────────────────────────────────────────────────────────
(def event-route-change    "barebuild-route-change")
(def event-navigate        "barebuild-navigate")
(def event-route-mounted   "barebuild-route-mounted")
(def event-route-unmounted "barebuild-route-unmounted")

(def property-api
  {:base   {:type 'string :reflects-attribute attr-base :default ""}
   :path   {:type 'string :readonly true}
   :params {:type 'object :readonly true}})

(def event-schema
  {event-route-change {:detail {:path 'string :params 'object}}
   ;; The public programmatic-navigation event: dispatch it AT a router (it bubbles
   ;; + is composed) to drive a pushState navigation without a click. Declared here
   ;; so adapters / the CEM / .d.ts surface it as part of the router's API.
   event-navigate     {:detail {:path 'string}}})

(def method-api {})

;; ── Path segmentation ──────────────────────────────────────────────────────────

(defn split-segments
  "Split a URL path into non-empty segments. Leading/trailing slashes and
  repeated slashes collapse away, so `/`, `` and `//` all yield `[]`.

  Always returns a vector — segmentation is one fold, not a validity check
  braided into a calculation. Non-string input (the boundary's job to avoid)
  yields `[]`, so callers never have to repair the return.

  `\"/users/42\"` => `[\"users\" \"42\"]`."
  [path]
  (if (string? path)
    (vec (remove str/blank? (str/split path #"/")))
    []))

(defn parse-path-pattern
  "Parse a route `path` pattern string into ordered segment data — the runtime
  contract is this parsed value, not the string. Parsed once per route at connect.

  `:name` segments become params; everything else is a literal.

    `\"/\"`            => `[]`
    `\"/users\"`       => `[{:kind :literal :v \"users\"}]`
    `\"/users/:id\"`   => `[{:kind :literal :v \"users\"} {:kind :param :name \"id\"}]`"
  [pattern]
  (mapv (fn segment->data [seg]
          (if (str/starts-with? seg ":")
            {:kind :param :name (subs seg 1)}
            {:kind :literal :v seg}))
        (split-segments pattern)))

(defn match-segments
  "Match a parsed `pattern` (from `parse-path-pattern`) against pre-split path
  `segs` (from `split-segments`). Returns `{:params {…}}` on a full match (params
  empty when the pattern has no `:name` segments), or `nil` when it does not
  match. Takes segments rather than a path string so a caller matching many
  patterns against one URL segments it ONCE rather than per pattern.

  Decomplected into two folds over the zipped segments — `every?` decides whether
  the pattern matches, `for`/`into` projects the param bindings — so neither
  question is smuggled through the other."
  [pattern segs]
  (let [pairs (map vector pattern segs)]
    (when (and (= (count pattern) (count segs))
               (every? (fn [[{:keys [kind v]} seg]] (or (= kind :param) (= v seg)))
                       pairs))
      {:params (into {} (for [[{:keys [kind] pname :name} seg] pairs
                              :when (= kind :param)]
                          [pname seg]))})))

(defn match-path
  "Match a parsed `pattern` against a stripped `path` string — a convenience
  wrapper that splits `path` and delegates to `match-segments`. Pure projection:
  the same `(pattern, path)` always yields the same value."
  [pattern path]
  (match-segments pattern (split-segments path)))

(defn strip-base
  "Strip a router `base` prefix from a `url-path` before matching.

  `(strip-base \"/app\" \"/app/users/42\")` => `\"/users/42\"`. The prefix only
  strips on a path-segment boundary, so `base \"/app\"` does not strip
  `\"/application\"`. An empty base (the default) returns the path unchanged.
  A path equal to the base returns `\"/\"`."
  [base url-path]
  (let [base     (or base "")
        url-path (or url-path "")
        prefix   (if (and (not= base "") (str/ends-with? base "/"))
                   (subs base 0 (dec (count base)))
                   base)]
    (if (and (not= prefix "")
             (or (= url-path prefix)
                 (str/starts-with? url-path (str prefix "/"))))
      (let [tail (subs url-path (count prefix))]
        (if (= "" tail) "/" tail))
      url-path)))

(defn should-intercept?
  "Pure predicate deciding whether the router SPA-navigates for an anchor click.
  The effect shell extracts these anchor facts from the event (`composedPath`
  walk + this router's identity); the decision is a plain conjunction so each
  branch is independently testable. The click-gesture gate (primary button, no
  modifier, not already defaultPrevented) lives in the shell's `candidate-click?`
  — it needs no composedPath walk and is checked before facts are built.

  Intercept only when: the click path contains an `<a data-barebuild-route>`
  ancestor, *this* router is that anchor's nearest `<barebuild-router>` ancestor
  (no other router lies between them — nearest router wins for nested/sibling
  routers), the anchor targets the same origin, navigates THIS frame (no
  `target=\"_blank\"`/named target), is not a `download`, and is not a pure
  in-page hash link (`hash-only?` — same pathname+query, only the `#fragment`
  differs). Intercepting an in-page hash link would pushState a redundant history
  entry and `preventDefault` the browser's native scroll-to-anchor; letting it
  through preserves both."
  [{:keys [anchor-present? nearest-router-is-this?
           same-origin? same-frame? download? hash-only?]}]
  (every? identity
          [anchor-present?
           nearest-router-is-this?
           same-origin?
           same-frame?
           (not download?)
           (not hash-only?)]))
