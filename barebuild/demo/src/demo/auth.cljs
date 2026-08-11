(ns demo.auth
  "The demo's credential edge: the rotating bearer token and the decorator that attaches it. A
  token that refreshes on its own schedule is not a fact about a resource, so it lives here rather
  than in a `headers` attribute."
  (:require
   [barebuild.decorator :as decorator]))

(def header-name "authorization")

(def ^:private expired-token
  "The one token the dev-server refuses, so a 401 needs no second endpoint."
  "demo-expired")

(defonce ^:private minted (atom {:serial 1 :expired? false}))

(defn- token-of
  "The bearer token a mint state stands for."
  [{:keys [serial expired?]}]
  (if expired? expired-token (str "demo-" serial)))

(defn current
  "The credential the next request will carry: the token, and whether it is the one the server
  refuses. One value, so nothing reads that fact back out of the rendered token."
  []
  (let [m @minted]
    {:token (token-of m) :refused? (:expired? m)}))

(defn expire!
  "Make the next request carry the token the server refuses, as a lapsed session would."
  []
  (swap! minted assoc :expired? true)
  nil)

(defn reauthenticate!
  "Mint the next token, as a refresh would."
  []
  (swap! minted #(-> % (update :serial inc) (assoc :expired? false)))
  nil)

(defonce ^:private attached (atom nil))

(defn last-attached
  "The credential the decorator last put on the wire, or nil. Recorded rather than derived, so
  the log reports what was attached and not what would be attached now."
  []
  @attached)

(defn install!
  "Register the page's one request decorator. Idempotent: installing a hook is a `reset!`, so a
  reconnecting element re-registers rather than stacking."
  []
  (decorator/install!
   (fn [_request]
     (let [{:keys [token]} (current)]
       (reset! attached token)
       {header-name (str "Bearer " token)}))))
