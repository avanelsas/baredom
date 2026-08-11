# Configuring requests

Every request a `<server-resource>` issues, reads and writes alike, is built from the element's
own attributes. Three of them configure how the request reaches your server rather than what it
asks for: `credentials`, `headers` and `timeout`.

```html
<server-resource src="/api/tasks"
                 credentials="include"
                 headers='{"X-Api-Key": "public-key"}'
                 timeout="15000">
  ...
</server-resource>
```

All three are optional. `credentials` and `headers` do nothing unless you set them. `timeout`
is the one attribute with a default, for the reason given below.

Everything on this page is running in the credentials demo (`demo/auth.html`): a static header
on the element, a rotating bearer token attached by a decorator, a 401 reaching a consumer, and
the trace showing the static header but not the token.

## `credentials`

Sets the fetch credentials mode, one of `same-origin`, `include` or `omit`.

| Value | When |
|---|---|
| *(absent)* | Same-origin API. The browser default already sends cookies to your own origin. |
| `include` | Cross-origin API using **cookie auth**. The cookie rides the request, and your server must answer with `Access-Control-Allow-Credentials: true` and a concrete `Access-Control-Allow-Origin`. |
| `omit` | Never send cookies, even same-origin. |

Cookie auth needs nothing else from BareBuild: the browser attaches and refreshes the session,
and this one attribute is the whole integration.

An unrecognised value is reported on the console and ignored, leaving the browser default in
place. Handing fetch a mode it does not know would otherwise throw on every request.

## `headers`

A JSON object of static headers added to every request:

```html
<server-resource src="/api/tasks" headers='{"X-Api-Key": "k", "Accept-Language": "nl"}'>
```

Header names are lowercased when read, so `X-Api-Key` and `x-api-key` are the same header and
can never both be sent. Values are used exactly as written. Entries with a blank name or an
empty value are dropped.

**`content-type` on a write is BareBuild's.** A create, update or move sends its record as JSON,
so `content-type: application/json` is part of the write contract and overrides whatever you
configure for those requests. Every other header is yours and passes through untouched. On a
bodiless read your `content-type`, if you set one, is left alone.

An attribute that is not parseable as a JSON object is reported on the console and ignored. The
resource still connects and still fetches, just without the headers. Configuration failing soft
is deliberate: a typo in an attribute should not take a page's data down.

### Static means static

`headers` is read **once, when the element connects**, exactly like `src`. Changing the
attribute afterwards does not affect requests already in flight or requests still to come. The
value the pure `step` builds its requests from cannot shift underneath it.

That makes this attribute the right home for an API key, a tenant id, or a language preference,
and the wrong home for a **rotating bearer token**. A token that refreshes is not a static value
the resource holds, and modelling it as one gives you a stale copy. Dynamic credentials go
through the request decorator instead.

## `timeout`

How long a request may run, in milliseconds, before BareBuild abandons it.

```html
<server-resource src="/api/reports" timeout="120000">
```

| Value | Meaning |
|---|---|
| *(absent)* | **60000.** Every resource has a budget whether or not you ask for one. |
| a positive number | That budget instead. |
| `0` | No budget. The request runs until the browser gives up on it. |
| anything else | Reported on the console, and the default is kept. |

Note the last row. Unlike `credentials` and `headers`, an unusable value here does **not**
degrade to "unconfigured", because that would silently remove the budget at exactly the moment
someone was trying to set one.

### Why there is a default at all

BareBuild runs one request at a time per resource, and the slot is cleared only by a response or
a classified failure. A request that never settles never produces either, so the resource stays
pending forever: later gestures still write the URL and still notify consumers, but no fetch is
ever issued, and the user sees stale data under a URL that says otherwise. On the write side it
is worse. A hung write holds the single-flight write slot, so every later create, update, delete
or move is refused for the life of the element.

Browsers do eventually abandon a dead connection, but a socket that is open and simply silent
can stall far longer than any user will wait. The budget turns that indefinite wedge into an
ordinary failure your consumer can render and the user can retry.

That is also the thing to weigh before writing `timeout="0"`. It is the right answer for an
endpoint that genuinely takes minutes, a large export or a cold-start function, and it is the
wrong answer for making a flaky endpoint stop complaining.

### What it covers

The budget covers the whole operation, not just the network call. If a
[request decorator](#a-request-decorator) is registered and its promise never settles, the
request is abandoned on the same budget, because a request still waiting for a credential wedges
a resource exactly as a request waiting for a response does.

When the budget runs out the in-flight request is aborted, so the socket is released rather than
left to the browser, and your consumer's `on-failure` receives a `:network` failure with
`:error {:kind :timeout :after 60000}`, carrying the budget it outlived.

A **write** that runs out of budget is followed by a re-read, because giving up on the response
says nothing about whether the server committed. See
[Writes](./authoring-a-consumer.md#writes).

## A request decorator

For a header that changes between requests, register one function at boot. BareBuild calls it
just before each request goes out and merges the headers it returns:

```clojure
(defn- auth-headers [_request]
  {"authorization" (str "Bearer " (current-token))})

(barebuild/init {:request-decorator auth-headers})
```

It may return the headers directly, or a **promise** of them, which is what lets a near-expiry
token be refreshed before the request fires rather than after a 401 comes back:

```clojure
(defn- auth-headers [_request]
  (-> (fresh-token!)                                  ; returns a promise
      (.then (fn [t] {"authorization" (str "Bearer " t)}))))
```

The function receives the **request value** it is about to decorate, including `:method` and
`:url`, so one decorator can serve several endpoints and attach a credential only to the ones
that need it. Returning `nil` or an empty map attaches nothing. A `#js` object works as well as
a map, and returning anything else is reported on the console rather than quietly attaching
nothing.

Precedence runs last-most-specific: the decorator's headers override the resource's static
`headers`, and the protocol's `content-type` on a bodied write still overrides both.

### When a decorator fails

If it throws or its promise rejects, **the request is not sent**. An uncredentialed request
would only come back a 401, so BareBuild surfaces the failure directly instead: your consumer's
`on-failure` receives a `:network` failure with `:error {:kind :decorator}`. That is its own
kind precisely so an app can tell "I could not produce a credential" from "the user is offline",
which call for different handling.

### One decorator, registered once

There is one hook per page, established at `init`. It is not per resource, because the function
can already discriminate on the request it is handed, and because a credential that could change
mid-flight between a request being built and being sent is a source of confusion rather than
flexibility. The function is fixed, the token inside it is free to vary.

Naming `:request-decorator` sets the hook to whatever the key holds, so passing `nil` clears it.
Leaving the key out says nothing about the hook and leaves whatever is installed in place, which
is what keeps a second `init` from silently dropping the decorator an earlier one established.

Whatever you register has to be callable. A value that is not is refused and reported when you
register it, rather than accepted and then failing every request the page goes on to make.

## What BareBuild does and does not do about auth

BareBuild **attaches** the credential you configure and **surfaces** what the server says back.
It owns nothing else. Login UI, token storage, refresh, logout, and deciding what a 401 means
are your application's, exactly as they are with any other data layer.

There is no automatic retry. A 401 arrives at your consumer's `on-failure` as a `:network`
failure with `:error {:kind :http-status :status 401}`, and your app decides whether that means
redirecting to a login page or re-authenticating and re-submitting the intent. See
[Rendering failures](./authoring-a-consumer.md#rendering-failures) for how to branch on it.

A decorator that refreshes a token pre-emptively narrows how often that happens. It does not
replace it, and it is not meant to: the app still owns what a 401 means.

## Where the config lives

The three attributes resolve, once at connect, into a `:transport` entry on the resource value.
`step` merges it into the `:fetch` and `:write` effects it returns, so the request value handed
to the executor already describes the whole call, headers and credentials included. The edge
translates that value into a `fetch` init object and decides nothing, which is the same property
every other part of the runtime holds.

The decorator is the deliberate exception, and sits outside the value layer for the same reason
the `AbortController` does: a credential that changes on its own schedule is not a fact about
the resource at any moment in time. It runs after `step`, on the request `step` produced. One
consequence worth knowing when reading a [BareReplay](../../barereplay/README.md) trace: a
recorded request shows the static config but not the decorator's headers, so a token never
enters a trace.
