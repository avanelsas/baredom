(ns barebuild.decorator
  "The host app's per-request hook, for headers a value cannot hold, a rotating bearer token
  above all. It lives at the edge like the AbortController, changing independently of any step
  event. This namespace holds the seam, barebuild.transport calls it.")

(defonce ^:private hook (atom nil))

(defn install!
  "Register `f`, the one decorator for the page. It takes the request and returns the headers to
  merge into it, or a promise of them. Throwing means the request is not sent at all. Nil clears
  the hook, and what is not callable is refused here rather than failing every request later.
  Full contract in docs/request-configuration.md."
  [f]
  (if (or (nil? f) (ifn? f))
    (reset! hook f)
    (js/console.error "[barebuild] a request decorator must be a function, ignoring:" f))
  nil)

(defn current
  "The registered decorator, or nil when there is none."
  []
  @hook)
