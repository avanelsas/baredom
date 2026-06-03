(ns serve-api
  "Independent BareBuild demo backend — an API-ONLY babashka + http-kit server that
  reimplements the demo's JSON contract (see server/API-CONTRACT.md) FROM SCRATCH, on
  a separate port (default 3001) with permissive CORS.

  It deliberately shares NO code with serve.clj. That is the whole point: the BareBuild
  orchestration elements are vanilla `fetch` + CustomEvents that hold no authoritative
  client state (DOM = f(server value); every read re-fetches, every write re-reads). So
  they work against ANY server that (a) honours the contract and (b) holds the state —
  not just the original babashka one. A second, INDEPENDENT implementation demonstrates
  that; a thin wrapper around serve.clj's handler would only show 'same server, new port'.

  Run from barebuild/demo-app/:  `bb serve-api [port]`  (default 3001).
  Pair it with a page served on a DIFFERENT origin (e.g. `bb serve` on 3000) loaded as
  `http://localhost:3000/?api=http://localhost:3001` to drive it cross-origin over CORS.

  State is two in-memory atoms seeded from data/*.edn, ephemeral per process (resets on
  restart) — matching serve.clj's 'the server holds truth' stance. Boundary note: like
  serve.clj, every path resolves from (fs/cwd) (the demo-app dir under `bb`), never a
  parent-relative path."
  (:require [org.httpkit.server :as http]
            [cheshire.core :as json]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.string :as str]))

;; ── In-memory state (seeded in -main; ephemeral per process) ─────────────────
(defonce ^:private tasks    (atom []))
(defonce ^:private settings (atom {}))
;; A standalone next-id atom (re-derived from the seed in -main) hands out a unique
;; id per POST without locking the task list — same rationale as serve.clj.
(defonce ^:private next-id  (atom 0))

(defn- read-edn
  "Read an EDN file under the demo-app working dir (e.g. \"data/tasks.edn\")."
  [rel]
  (edn/read-string (slurp (str (fs/path (fs/cwd) rel)))))

;; ── CORS ─────────────────────────────────────────────────────────────────────
;; <barebuild-data>/<barebuild-action> send no credentials (plain fetch), so `*` is
;; correct. A PUT/PATCH/DELETE with an application/json body triggers a preflight, so
;; OPTIONS must be answered and Content-Type must be in Allow-Headers.
(def ^:private cors-headers
  {"Access-Control-Allow-Origin"  "*"
   "Access-Control-Allow-Methods" "GET, POST, PUT, PATCH, DELETE, OPTIONS"
   "Access-Control-Allow-Headers" "Content-Type"
   ;; Cache the preflight so each cross-origin PUT/PATCH/DELETE doesn't re-OPTIONS.
   "Access-Control-Max-Age"       "600"})

(defn- with-cors [resp]
  (update resp :headers merge cors-headers))

;; ── HTTP helpers ─────────────────────────────────────────────────────────────
(defn- json-response [status body]
  {:status  status
   :headers {"Content-Type" "application/json"}
   :body    (json/generate-string body)})

(defn- parse-body
  "Parse a JSON request body into a map with keyword keys (nil if absent/blank)."
  [req]
  (when-let [b (:body req)]
    (let [s (slurp b)]
      (when-not (str/blank? s)
        (json/parse-string s true)))))

(defn- task-id-from
  "Parse the numeric id out of /api/tasks/<id>, or nil."
  [uri]
  (some-> (re-matches #"/api/tasks/(\d+)" uri) second parse-long))

(defn- find-task [id]
  (first (filter #(= id (:id %)) @tasks)))

(defn- update-task!
  "Merge fields into the task with id; return the updated task or nil if missing."
  [id fields]
  (when (find-task id)
    (swap! tasks (fn [ts] (mapv #(if (= id (:id %)) (merge % fields {:id id}) %) ts)))
    (find-task id)))

;; ── API routing (the contract; see server/API-CONTRACT.md) ───────────────────
(defn- api-handler [method uri req]
  (let [id (task-id-from uri)]                     ; the /api/tasks/<id> id, or nil — computed once
    (cond
      (= uri "/api/tasks")
      (case method
        :get  (json-response 200 @tasks)
        :post (let [body   (parse-body req)
                    new-id (swap! next-id inc)
                    task   (merge {:status "todo"} body {:id new-id})]
                (swap! tasks conj task)
                (json-response 201 task))
        (json-response 405 {:error "method not allowed"}))

      (= uri "/api/settings")
      (case method
        :get (json-response 200 @settings)
        ;; swap! is the atomic read-modify-write (a reset! of (merge @settings …) races
        ;; under http-kit's worker pool); it also returns the just-committed value.
        :put (json-response 200 (swap! settings merge (parse-body req)))
        (json-response 405 {:error "method not allowed"}))

      id
      (case method
        :get          (if-let [t (find-task id)]
                        (json-response 200 t)
                        (json-response 404 {:error "task not found"}))
        (:put :patch) (if-let [t (update-task! id (parse-body req))]
                        (json-response 200 t)
                        (json-response 404 {:error "task not found"}))
        :delete       (if (find-task id)
                        (do (swap! tasks (fn [ts] (vec (remove #(= id (:id %)) ts))))
                            {:status 204})
                        (json-response 404 {:error "task not found"}))
        (json-response 405 {:error "method not allowed"}))

      :else (json-response 404 {:error "unknown endpoint"}))))

;; ── Top-level handler: CORS preflight + API-only (no static serving) ─────────
(defn- handler [req]
  (let [uri    (:uri req)
        method (:request-method req)]
    (with-cors
      (cond
        ;; CORS preflight — answer before routing so any /api/* path is reachable.
        (= method :options)            {:status 204}
        (str/starts-with? uri "/api/") (api-handler method uri req)
        ;; API-only: the page is served from ANOTHER origin, not here.
        :else (json-response 404 {:error "api-only server; load the app from its own origin"})))))

;; ── Entry point ──────────────────────────────────────────────────────────────
(defn -main [& args]
  (let [port (or (some-> (first args) parse-long)
                 (some-> (System/getenv "PORT") parse-long)
                 3001)]
    (reset! tasks    (vec (read-edn "data/tasks.edn")))
    (reset! settings (read-edn "data/settings.edn"))
    (reset! next-id  (apply max 0 (map :id @tasks)))
    (http/run-server #'handler {:port port})
    (println (str "BareBuild demo — INDEPENDENT API-only backend (CORS) → http://localhost:" port))
    (println "  read:  GET /api/tasks   GET /api/tasks/:id   GET /api/settings")
    (println "  write: POST /api/tasks  PUT|PATCH|DELETE /api/tasks/:id  PUT /api/settings")
    (println (str "  CORS:  * (no static). Pair with a page on another origin via "
                  "?api=http://localhost:" port))
    @(promise)))
