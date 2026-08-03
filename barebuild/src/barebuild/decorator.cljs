(ns barebuild.decorator)

;; The host app's per-request hook. The executor calls it just before a request goes out, to
;; attach headers a value cannot hold, a rotating bearer token above all: a token changes
;; independently of any step event, so it lives at the edge like the AbortController rather than
;; in the resource value. Apps install it through barebuild.core/init.
(defonce ^:private hook (atom nil))

(defn set-request-decorator!
  "Install `f`, called with each request value and returning the headers to merge into it, or a
  promise of them. Nil clears it."
  [f]
  (reset! hook f))

(defn current
  "The registered decorator, or nil when the app registered none."
  []
  @hook)
