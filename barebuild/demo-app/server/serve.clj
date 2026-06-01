(ns serve
  "BareBuild Tasks demo backend (Phase 4) — Babashka + http-kit.

  Serves the JSON read/write API and the built app (public/) on a SINGLE origin,
  so the Playwright e2e run needs no CORS and deep-links resolve via an SPA
  fallback. The GET endpoints back the read-side <barebuild-data> brokers; the
  write endpoints (POST/PUT/PATCH/DELETE) exist so a participant's hand-rolled
  create/update/delete actually round-trips — V1 ships no write-side elements, so
  observing how they wire those is the whole point of Phase 4.

  State is two in-memory atoms seeded from data/*.edn, ephemeral per process
  (resets on restart) — this keeps the e2e run reproducible and matches the
  'server state IS the state' stance (no client store, the server holds truth).

  Run from barebuild/demo-app/:  `bb serve [port]`  (default port 3000).

  Boundary note: nothing here uses a parent-relative path; all paths resolve from
  (fs/cwd), which is the demo-app dir when launched by `bb serve`. The barebuild
  boundary check forbids cross-boundary relative references in barebuild/ sources."
  (:require [org.httpkit.server :as http]
            [cheshire.core :as json]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [clojure.string :as str]))

;; ── In-memory state (seeded in -main) ────────────────────────────────────────
(defonce ^:private tasks    (atom []))
(defonce ^:private settings (atom {}))
;; next-id is the max :id in `tasks` (re-derived from it on every (re)seed; see
;; -main), held as its own atom on purpose: a single (swap! next-id inc) hands out
;; a unique id per concurrent POST without locking the whole task list. The two
;; atoms are not transactionally coupled — acceptable for this ephemeral demo.
(defonce ^:private next-id  (atom 0))

(defn- read-edn
  "Read an EDN file under the demo-app working dir (e.g. \"data/tasks.edn\")."
  [rel]
  (edn/read-string (slurp (str (fs/path (fs/cwd) rel)))))

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

;; ── API routing ──────────────────────────────────────────────────────────────
(defn- api-handler [method uri req]
  (cond
    (= uri "/api/tasks")
    (case method
      :get  (json-response 200 @tasks)
      :post (let [body (parse-body req)
                  id   (swap! next-id inc)
                  task (merge {:status "todo"} body {:id id})]
              (swap! tasks conj task)
              (json-response 201 task))
      (json-response 405 {:error "method not allowed"}))

    (= uri "/api/settings")
    (case method
      :get (json-response 200 @settings)
      :put (do (reset! settings (merge @settings (parse-body req)))
               (json-response 200 @settings))
      (json-response 405 {:error "method not allowed"}))

    (task-id-from uri)
    (let [id (task-id-from uri)]
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
        (json-response 405 {:error "method not allowed"})))

    :else (json-response 404 {:error "unknown endpoint"})))

;; ── Static files + SPA fallback ──────────────────────────────────────────────
(defn- content-type [path]
  (cond
    (str/ends-with? path ".js")   "text/javascript; charset=utf-8"
    (str/ends-with? path ".css")  "text/css; charset=utf-8"
    (str/ends-with? path ".html") "text/html; charset=utf-8"
    (str/ends-with? path ".json") "application/json; charset=utf-8"
    (str/ends-with? path ".map")  "application/json; charset=utf-8"
    (str/ends-with? path ".svg")  "image/svg+xml"
    :else                         "text/plain; charset=utf-8"))

(defn- index-html []
  (slurp (str (fs/path (fs/cwd) "public" "index.html"))))

(defn- asset-request?
  "True when the URI names a file (its last path segment has an extension), as
  opposed to a client route. A missing asset (e.g. a skipped /js/main.js build)
  must 404 — never masquerade as index.html with a 200, which would make the app
  silently fail to boot and turn e2e failures into opaque selector timeouts. Only
  extension-less client routes (/tasks, /tasks/1, /settings) fall back to the SPA."
  [uri]
  (let [last-seg (last (str/split uri #"/"))]
    (boolean (and last-seg (str/includes? last-seg ".")))))

(defn- serve-static
  "Serve a file from public/. A missing *asset* 404s; a missing *client route*
  falls back to index.html (SPA: /tasks/1 must load the app, not 404)."
  [uri]
  (let [rel (if (= uri "/") "index.html" (subs uri 1))
        f   (fs/path (fs/cwd) "public" rel)]
    (cond
      (and (fs/exists? f) (fs/regular-file? f))
      {:status 200 :headers {"Content-Type" (content-type (str f))} :body (slurp (str f))}

      (asset-request? uri)
      {:status 404 :headers {"Content-Type" "text/plain; charset=utf-8"}
       :body (str "Not found: " uri)}

      :else
      {:status 200 :headers {"Content-Type" "text/html; charset=utf-8"} :body (index-html)})))

(defn- handler [req]
  (let [uri    (:uri req)
        method (:request-method req)]
    (cond
      ;; reject path traversal (a literal two-dot segment) defensively
      (str/includes? uri "..")        {:status 403 :body "forbidden"}
      (str/starts-with? uri "/api/")  (api-handler method uri req)
      :else                           (serve-static uri))))

;; ── Entry point ──────────────────────────────────────────────────────────────
(defn -main [& args]
  (let [port (or (some-> (first args) parse-long)
                 (some-> (System/getenv "PORT") parse-long)
                 3000)]
    (reset! tasks    (vec (read-edn "data/tasks.edn")))
    (reset! settings (read-edn "data/settings.edn"))
    (reset! next-id  (apply max 0 (map :id @tasks)))
    (http/run-server #'handler {:port port})
    (println (str "BareBuild Tasks demo backend → http://localhost:" port))
    (println "  read:  GET /api/tasks   GET /api/tasks/:id   GET /api/settings")
    (println "  write: POST /api/tasks  PUT|PATCH|DELETE /api/tasks/:id  PUT /api/settings")
    (println "  app:   GET /  (static public/ + SPA fallback)")
    @(promise)))
