# State recipes

When the [server-state-first canonical pattern](server-state-first.md) is not enough, these recipes show how to extend it without contradicting it.

Four recipes:

1. [Pure server state (the default)](#1-pure-server-state)
2. [Reading UI-local state from outside a component](#2-reading-ui-local-state-from-outside-a-component)
3. [Per-activation route-body teardown](#3-per-activation-route-body-teardown)
4. [Re-frame compatibility](#4-re-frame-compatibility)

---

## 1. Pure server state

This is the default. The server holds the truth; `<barebuild-data>` projects it; `<barebuild-bind>` wires the projection into a UI consumer; `<barebuild-action>` mutates; `<barebuild-invalidate-on>` keeps the projection fresh.

```html
<barebuild-data id="tasks" src="/api/tasks" trigger-on-connect></barebuild-data>
<barebuild-bind from="#tasks" prop="items" target="#tasks-table"></barebuild-bind>
<x-table id="tasks-table"></x-table>

<barebuild-action id="add" action="/api/tasks" method="POST">
  <x-form>
    <input name="title">
    <x-button type="submit" label="Add"></x-button>
  </x-form>
</barebuild-action>
<barebuild-invalidate-on from="#add" src="/api/tasks"></barebuild-invalidate-on>
```

**When this is enough:** any app whose primary content is server-owned (lists, detail views, dashboards, admin tools, search). Probably 80% of the apps BareBuild's audience builds.

**When this is not enough:** see recipes 2–4.

---

## 2. Reading UI-local state from outside a component

Components like `<x-checkbox>`, `<x-search-field>`, `<x-dropdown>` hold ambient interaction state: checked/unchecked, current text, open/closed. This state is *not* server-owned and *should not* be lifted into a global store. But sometimes another part of the page needs to read it.

The platform provides two channels per BareDOM component:

- **Event:** the component dispatches a CustomEvent (e.g. `x-checkbox-change`, `x-search-field-input`) on every change. Subscribe to it.
- **Property:** the current value is readable at any time via the component's property accessor (e.g. `el.checked`, `el.value`).

### Recipe

```html
<x-search-field id="q" placeholder="Search…"></x-search-field>
<x-button id="search-btn" label="Search"></x-button>
```

```js
const q = document.querySelector('#q');
const btn = document.querySelector('#search-btn');

// Pull the value when an action happens.
btn.addEventListener('x-button-click', () => {
  console.log('Searching for:', q.value);
});

// Or subscribe to changes as they happen.
q.addEventListener('x-search-field-input', (e) => {
  console.log('Now typing:', e.detail.value);
});
```

**No global store. No subscription registry. No middleware.** The component is the value's home; the channels are open.

### Wiring UI-local state into a data fetch

A common case: a search field's value should drive a fetch. Use `trigger-on-event`:

```html
<x-search-field id="q" placeholder="Search…"></x-search-field>
<barebuild-data id="results" src="/api/search" trigger-on-event="x-search-field-input"></barebuild-data>
```

Now every keystroke fires `x-search-field-input`, which the data broker is listening for, and refetches. For debouncing, wrap with a small custom emitter that re-fires `x-search-field-input` on a timer — or use a third-party debounce helper at the application layer (no platform support needed).

---

## 3. Per-activation route-body teardown

`<barebuild-route>` preserves the slotted body's state by contract (input values, scroll position, etc. survive deactivation). This is the right default for the V1 audience.

Sometimes you want the opposite: tear down the route body when the user navigates away, build a fresh one when they come back. Common cases: heavy components (charts, maps, video players) that shouldn't stay mounted; routes that consume server resources via a long-lived stream.

V1 ships no lazy-mount attribute. Instead, this recipe uses a `<template>` and the router's `barebuild-route-change` event.

### Recipe

```html
<barebuild-route path="/heavy" name="heavy-page">
  <template id="heavy-tpl">
    <x-chart id="heavy-chart"></x-chart>
  </template>
  <div id="heavy-mount-point"></div>
</barebuild-route>

<script>
  const router = document.querySelector('barebuild-router');
  const route = document.querySelector('barebuild-route[name="heavy-page"]');
  const tpl = document.querySelector('#heavy-tpl');
  const mountPoint = document.querySelector('#heavy-mount-point');

  router.addEventListener('barebuild-route-change', (e) => {
    if (e.detail.name === 'heavy-page') {
      // Activating: clone fresh.
      mountPoint.replaceChildren(tpl.content.cloneNode(true));
    } else {
      // Deactivating: clear.
      mountPoint.replaceChildren();
    }
  });
</script>
```

Every activation gets a fresh `<x-chart>` instance. Deactivation removes it.

V2 may ship `<barebuild-route-lazy>` if this recipe proves to be a real demand. Until then, this is two lines of explicit code.

---

## 4. Re-frame compatibility

Some apps need a global event/effect/state model: multi-step wizards, optimistic UIs with rollback, complex orchestration across concurrent flows, drag-and-drop reordering with intermediate visual feedback. Re-frame is a well-designed answer to that question, and BareBuild does not get in its way.

**The honest framing:** V1's server-state-first shape doesn't *ask* the question re-frame answers. We default to no client store because in our target shape there isn't enough client state to justify one. Apps that genuinely need re-frame are not "wrong" — they're a different shape.

### Recipe

Add re-frame to your `deps.edn` at the application layer (not as a BareBuild dependency):

```clojure
;; deps.edn
{:deps {re-frame/re-frame {:mvn/version "1.4.3"}}}
```

Use re-frame to manage your *client-local* state. Use BareBuild's six elements to coordinate server state. They don't fight; they serve different jobs.

Example: a multi-step task-creation wizard with three pages, draft-saving, validation:

```clojure
(ns my-app.wizard
  (:require [re-frame.core :as rf]))

;; Re-frame holds the wizard's intermediate state.
(rf/reg-event-db ::next-step
  (fn [db _] (update db ::step inc)))

(rf/reg-sub ::step (fn [db _] (::step db 0)))
(rf/reg-sub ::draft (fn [db _] (::draft db {})))

;; When the wizard completes, dispatch through <barebuild-action> as usual.
(defn on-finish! [draft]
  (let [action (.querySelector js/document "#submit-task")]
    (.submit! action draft)))
```

In the markup:

```html
<barebuild-action id="submit-task" action="/api/tasks" method="POST"></barebuild-action>
<barebuild-invalidate-on from="#submit-task" src="/api/tasks"></barebuild-invalidate-on>

<!-- The wizard UI itself is a re-frame component that calls on-finish! at the end. -->
<my-wizard></my-wizard>
```

Re-frame owns the wizard's local state (current step, draft fields, validation errors). BareBuild owns the server interaction (the final POST, the refetch).

**The boundary is clean:** re-frame doesn't see the network; BareBuild doesn't see the wizard's intermediate state.

---

## What none of these recipes do

- **No global "store of stores."** Each recipe scopes its mechanism to its own purpose: server state on the server, UI-local state in components, ephemeral wizard state in re-frame (if you want it).
- **No virtual DOM.** All four recipes use BareDOM's property-write idiom (`el.items = newRows`) for view updates, not Hiccup re-mount.
- **No magic.** Every effect that crosses an element boundary is named in markup or code you can grep.

---

## See also

- [`server-state-first.md`](server-state-first.md) — the canonical pattern these recipes extend.
- Per-component reference under [`../../docs/barebuild-*.md`](../../docs/).
- [`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md) — architectural rationale.
