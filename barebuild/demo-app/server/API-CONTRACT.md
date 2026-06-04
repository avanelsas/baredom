# BareBuild demo — backend API contract

This is the **whole interface** between the BareBuild demo app and its backend. Implement these
endpoints, in **any language or stack**, and the demo works against your server unchanged.

That is the point: the BareBuild orchestration elements (`<barebuild-router>`, `<barebuild-route>`,
`<barebuild-data>`, `<barebuild-action>`, `<barebuild-invalidate-on>`) are vanilla `fetch` +
`CustomEvent`s. They hold **no authoritative client state** — `DOM = f(server value)`: every read
re-fetches, every write re-reads (via invalidation/refresh). So a backend is valid iff it:

1. **honours this contract**, and
2. **is the single source of truth** (server state IS the state — no client store).

Two backends in this repo satisfy it, independently:
- `server/serve.clj` — babashka, single-origin (serves the API **and** the static app on `:3000`).
- `server/serve_api.clj` — babashka, **API-only + CORS** on `:3001` (a separate origin), written
  from scratch. Run with `bb serve-api`.

A third, different-stack server (Node/Python/Go/…) implementing the table below would be just as
valid — and is the most convincing demonstration of all.

## Endpoints

All request/response bodies are JSON (`Content-Type: application/json`). State is **in-memory and
ephemeral** (reseeded on process start) — do not persist writes to disk.

| Method | Path | Request body | Success | Response body |
|--------|------|--------------|---------|---------------|
| GET | `/api/tasks` | — | 200 | array of task objects |
| GET | `/api/tasks/:id` | — | 200 | the task (404 `{error:"task not found"}` if absent) |
| POST | `/api/tasks` | partial task | **201** | the created task, with assigned `id` |
| PUT \| PATCH | `/api/tasks/:id` | fields to change | 200 | the **merged** task (404 if absent) |
| DELETE | `/api/tasks/:id` | — | **204** (empty) | — (404 if absent) |
| GET | `/api/settings` | — | 200 | the settings object |
| PUT | `/api/settings` | fields to change | 200 | the **merged** settings object |

Other behaviours:

- **POST defaults:** the new task is `merge({status: "todo"}, body, {id})` — i.e. `status` defaults
  to `"todo"` but an explicit `status` in the body wins; `id` is server-assigned and never
  overridable. The id is a **monotonic counter**: seeded to `max(existing id)` at startup, then
  `+1` per POST — so ids strictly increase and are **never reused, even after a delete**. (It is
  NOT recomputed as `max(existing ids) + 1` on each POST; after deleting the highest task the next
  id is still counter+1, not the freed value.)
- **PUT/PATCH/PUT-settings are MERGES**, not replacements: only supplied fields change; the rest are
  preserved. (A blank string is a deliberate "clear this field" — do not strip it.)
- **Unknown `/api/*`** → 404 `{error:"unknown endpoint"}`. **Wrong method** on a known path → 405
  `{error:"method not allowed"}`.
- **Empty/204 bodies are fine:** the client reads the body as text and treats an empty body as
  `nil`, so DELETE may return no body.

## Data shapes

**Task** (`data/tasks.edn` seeds four of these):

```json
{ "id": 1, "title": "…", "description": "…", "status": "todo|doing|done", "assignee": "…", "due": "YYYY-MM-DD" }
```

`id` is a number; the rest are strings. `description` is optional — the create/edit form sends it
and the reference servers preserve it via plain `merge`, so a backend should store it too rather
than validate it away.

**Settings** (`data/settings.edn`):

```json
{ "theme": "system", "page-size": 25, "default-status": "todo" }
```

`page-size` is a number.

## CORS (only needed for a separate-origin backend)

If the API runs on a **different origin** than the page (as `serve_api.clj` does — API on `:3001`,
page on `:3000`), respond to every request with:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

and answer the `OPTIONS` preflight (which a `PUT`/`PATCH`/`DELETE` + JSON body triggers) with `204`.
No credentials are sent, so `*` is correct.

**URL-match note:** `<barebuild-data>` decides whether a `barebuild-invalidate` applies by exact
**origin + pathname + query** equality. So when the page drives a cross-origin API, the data
broker's `src` **and** the matching `<barebuild-invalidate-on src>` must both carry the **same
absolute base** (the demo routes every API URL through `demo_app.wiring/api`, selected via the
navbar backend picker). A relative `src` resolves to the page origin and would not match an
absolute cross-origin invalidate.
