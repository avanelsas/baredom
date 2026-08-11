(ns demo.x-auth-consumer.model
  "Pure reads over the view the auth demo's consumer projects. No DOM, no effects."
  (:require
   [clojure.string :as str]))

(def tag-name "x-auth-consumer")

(defn unauthorized?
  "Whether `failure` is the server refusing the credential. A 401 arrives as a network failure,
  never as a rejection, since an HTTP failure never reaches envelope parsing."
  [failure]
  (and (= :network (:cause failure))
       (= :http-status (get-in failure [:error :kind]))
       (= 401 (get-in failure [:error :status]))))

(defn- network-message
  "The message for a read with no usable answer. A status means the request did arrive, so only
  its absence reads as unreachable."
  [error]
  (if (= :http-status (:kind error))
    (str "The server answered with HTTP " (:status error) ".")
    "The request did not reach the server."))

(defn failure-message
  "The banner text for a failure that is not a 401. The refused case gets its own prompt."
  [failure]
  (case (:cause failure)
    :rejected (get-in failure [:response :error :message])
    :network  (network-message (:error failure))
    :protocol "The server sent an unexpected response."
    :contract "The server's data didn't match the expected format."
    "Something went wrong."))

(def columns
  [{:label "Title"  :field "title"}
   {:label "Owner"  :field "owner"}
   {:label "Status" :field "status"}])

(def grid-template
  (str "repeat(" (count columns) ",minmax(0,1fr))"))

(defn- cells
  "One record as its cell texts, in column order."
  [record]
  (mapv (fn [{:keys [field]}] (str (get record field))) columns))

(defn rows
  "The rows an accepted response carries, each as its cells in column order."
  [accepted]
  (mapv cells (:value accepted)))

(defn token-label
  "The line naming the credential the next request will carry, empty when there is none."
  [{:keys [token refused?]}]
  (if (str/blank? (str token))
    ""
    (str "Next request carries: " token (when refused? " (the server refuses this one)"))))

(defn row-count-label
  "How many rows a response carried, as the log words it."
  [n]
  (case n
    0 "no rows"
    1 "1 row"
    (str n " rows")))

(defn unauthorized-banner
  "The banner text for a refused credential, naming the credential and the status."
  [token]
  (str "The server refused credential " token " (HTTP 401). Re-authenticate to generate a fresh one."))

(defn- outcome-of
  "How an exchange ended, or nil when the view carries neither an answer nor a failure."
  [{:keys [accepted failure]}]
  (cond
    (unauthorized? failure) :refused
    failure                 :failed
    accepted                :accepted))

(defn- observed-status
  "The HTTP status this client actually saw, or nil. A success carries none: a non-2xx became a
  failure before the envelope was read, so success implies a 200 rather than recording one."
  [failure]
  (get-in failure [:error :status]))

(defn log-line
  "One line of the request log: the credential that was attached, and what came back. Nil until
  there is an exchange to report."
  [token {:keys [accepted failure] :as view}]
  (when-let [outcome (outcome-of view)]
    {:token   (or token "none")
     :status  (str (or (observed-status failure) "--"))
     :outcome (name outcome)
     :detail  (case outcome
                :refused  "the server would not accept this credential"
                :failed   (failure-message failure)
                :accepted (row-count-label (count (:value accepted))))}))
