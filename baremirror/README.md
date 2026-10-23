# BareMirror

BareMirror is a small set of functions that bring a page to what your application wants to
show. You keep the state. The functions place the nodes, write the values and turn events into
messages. A node that stays is never made again.

**It is an alpha.** It ships inside the [BareDOM](https://github.com/avanelsas/baredom) package,
and a name may still change. See [What alpha means](#what-alpha-means).

Use it from JavaScript, TypeScript or ClojureScript. In JavaScript it is a plain ES module with
type declarations, and you never need ClojureScript. In ClojureScript you require its namespaces,
with keywords, maps and atoms. It works on any HTML, with BareDOM components or without them.

## Design principles

- **The page is the only truth.** BareMirror keeps no copy of the screen. What it remembers, it
  keeps on the nodes themselves. Each render reads the places from the page, so another script,
  a drag-and-drop library or HTML from a server can change the page, and the next render starts
  from what is there.
- **A change is a value first.** What must change is a plan and a set of writes. You can look
  at them and test them before they reach the page.
- **Templates are data.** A template is an array: a tag, attributes and children. A hole is a
  function of an item that says where a value goes. A template has no loop and no condition.
  A list is a container, and your view gives its keys.

## A first look

[`first-look.html`](https://github.com/avanelsas/baredom/blob/main/bare-html/first-look.html)
is a list of tasks with a checkbox each, in plain HTML and about 30 lines of JavaScript.
[See it run](https://avanelsas.github.io/baredom/bare-html/first-look.html).

A click on a checkbox sends the message `['toggle', '1']`. The argument is the key of the row.
`step` makes the next state, `view` says what the page shows, and `render` brings the page there.
`writeItem` skips a row whose task is the same as last time.

In ClojureScript, the same list is
[`main.cljs`](https://github.com/avanelsas/baredom/blob/main/bare-html/first-look-cljs/src/first_look/main.cljs),
with keyword holes and an atom. There the message is `[:toggle "1"]`, and `write-item!` writes a
row. [See it run](https://avanelsas.github.io/baredom/bare-html/first-look-cljs.html).

The [guide](https://github.com/avanelsas/baredom/blob/main/docs/baremirror.md) explains each
function, with a larger example that uses BareDOM components.

## Install

From npm:

```
npm install @vanelsas/baredom
```

```js
import { splitTemplate, syncPlaces, writeItem } from '@vanelsas/baredom/baremirror-alpha';
```

From a CDN, with no build step:

```js
import { splitTemplate, syncPlaces, writeItem }
  from 'https://cdn.jsdelivr.net/npm/@vanelsas/baredom@4.15.0/dist/baremirror-alpha.js';
```

**What it costs.** The module shares `base.js` with the components of BareDOM. On a page that
loads a BareDOM component, BareMirror adds about 7 KB, gzipped. On a page with no component, it
loads `base.js` too, and costs about 78 KB.

## With BareDOM components

BareDOM components ask before they change: `x-checkbox-change-request` can be cancelled. Pass
`requestedValues` from `@vanelsas/baredom/requests` to `listen`. Your `step` then decides, and a
component shows only what your state holds. The
[guide](https://github.com/avanelsas/baredom/blob/main/docs/baremirror.md#requests) shows how.

## Browser support

The current versions of Chrome, Edge, Firefox and Safari, as for
[BareDOM](https://github.com/avanelsas/baredom#browser-support). Where the browser offers
`moveBefore`, a node keeps its focus and state when it moves. Safari 26.5 does not offer it, so
there a row that moves loses its focus.

## TypeScript

The types ship in `baremirror-alpha.d.ts`. They catch most misuse at compile time, such as an
item of another type, or a hole where a fixed template is needed.

## In ClojureScript

BareMirror is written in ClojureScript. The same functions are in the namespaces
`baremirror.alpha.plan`, `places`, `template`, `parts`, `events` and `element`, in
`com.github.avanelsas/baredom` on Clojars:

```clojure
{:deps {com.github.avanelsas/baredom {:mvn/version "4.15.0"}}}
```

Start with [the first look in ClojureScript](https://github.com/avanelsas/baredom/blob/main/bare-html/first-look-cljs/src/first_look/main.cljs).
The [guide](https://github.com/avanelsas/baredom/blob/main/docs/baremirror.md#in-clojurescript)
has a larger example.

## What alpha means

BareMirror has the version of BareDOM. Until it leaves the alpha, any release of BareDOM may
change it, and the [changelog](https://github.com/avanelsas/baredom/blob/main/CHANGELOG.md) names
BareMirror in each change to it. The word `alpha` in the import path marks it. A stable release
will drop it.

Questions and feedback are welcome as
[issues](https://github.com/avanelsas/baredom/issues).

## See it run

- [A first look](https://avanelsas.github.io/baredom/bare-html/first-look.html): the page above,
  also [in ClojureScript](https://avanelsas.github.io/baredom/bare-html/first-look-cljs.html).
- [One task list, rendered three ways](https://avanelsas.github.io/baredom/bare-html/mirror.html):
  a rebuild, code by hand and BareMirror, side by side.
- [A task list](https://avanelsas.github.io/baredom/bare-html/tasks.html) with BareDOM components.
- [A tree](https://avanelsas.github.io/baredom/bare-html/tree.html), synced in one call.
- [Holes](https://avanelsas.github.io/baredom/bare-html/holes.html): a table from a template in
  ClojureScript.

## License

MIT, as [BareDOM](https://github.com/avanelsas/baredom/blob/main/LICENSE).
