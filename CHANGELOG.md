# Changelog

All notable changes to BareDOM will be documented in this file.

## [Unreleased]

### Changed

- **Small updates in BareMirror are cheaper.** The `nodes` of a reading from `readPlaces` are
  made when they are first read, so a render that plans and performs never makes them.
  `writeItem` compares the item before it checks the split template, so an item it has written
  already costs less than half what it did.

## [4.12.0] - 2026-10-21

BareDOM 4.12 can be imported on a server, so a page that uses its components renders with a
server renderer such as Next.js. BareMirror's `listen` returns a function that stops it, a
template can make SVG, and the module ships TypeScript types. The adapters are not released
with it: they work with `@vanelsas/baredom` 4.0.0 or later, and a page that uses them renders
on a server with 4.12.0 or later.

### Added

- **`listen` in BareMirror returns a function that removes its listeners.** `events/listen!`
  returns the same function.
- **SVG in a BareMirror template.** An `svg` and the elements below it are made in the SVG
  namespace, and the children of a `foreignObject` are HTML again. An element with an `xmlns`
  attribute is in that namespace, so a template for a `circle` can make rows for an `svg` on
  the page. `splitTemplate` refuses an `xmlns` hole.
- **Types for BareMirror.** `@vanelsas/baredom/baremirror-alpha` ships `baremirror-alpha.d.ts`.
  The item of a split template follows from its holes, so `writeItem` refuses an item of
  another type and a copy of a split template, and `makeNode` refuses a template with holes.

### Fixed

- **`x-particle-button` reads a translucent colour the same each time, and `x-neural-glow` reads
  every colour syntax.** `x-particle-button` painted a colour over the one it read before, so
  `rgba(255, 0, 0, 0.5)` took on the colour read before it. `x-neural-glow` drew a colour in
  `oklch`, `lab`, `color()` or `color-mix` as black. Both now read a cleared pixel.
- **BareDOM loads on a server.** A component module and its `init()` do nothing where there is
  no DOM, so a page that uses the adapters renders on a server, as in a Next.js build.
  `x-neural-glow`, `x-particle-button` and `x-trace-history` make their canvas or media query
  on first use. CI loads each module in Node.
- **`nodeMaker` and `defineElement` in BareMirror can be called where there is no DOM.**
  `nodeMaker` checks its template at once and makes its first node on its first call, and
  `defineElement` defines nothing where there is no `customElements`. A module that calls them
  when it loads can be imported on a server.
- **`listen` in BareMirror hears an event that does not bubble.** `focus`, `blur`,
  `pointerenter`, `scroll` and a custom event with no `bubbles` reached no listener. They now
  reach `listen` on their way down. An event that bubbles is handled as before.

### Changed

- **The README states the bundle size and the runtime as they are.** `base.js` is about 72 KB
  gzipped and holds the ClojureScript core and the shared components, and BareDOM is no longer
  called zero runtime. The npm description says no dependencies instead.

## [4.11.0] - 2026-10-20

BareDOM 4.11 lets a BareMirror template write the properties of a component, and a component
takes over a property that was set before it was defined. The adapters are not released with
it: they work with `@vanelsas/baredom` 4.0.0 or later.

### Added

- **Property holes in BareMirror.** A template names them in `props`, next to `on`:
  `['x-command-palette', { props: { items: item => item.commands } }]`. A write sets each
  property after the text and the attributes, and only when its value differs from the value
  last written to that node. JavaScript arrays and plain objects compare by their content.
- **The task list demo** finds a task with an `x-command-palette`, whose `items` it writes
  through `props`.

### Fixed

- **A component takes over a property set before it was defined.** Such a value hid the
  accessor of the component, so a property written to a node from `nodeMaker`, or before the
  module loaded, never reached it. The component now sets the value again through its
  accessor when it connects.

## [4.10.1] - 2026-10-20

BareDOM 4.10.1 stops a focused control from recalculating the styles of the page on every
render, and makes BareMirror faster at many rows. All five adapters are released with it:
`-react` 3.3.0, `-angular` 3.3.0, `-vue` 0.9.0, `-svelte` 0.9.0 and `-solid` 0.9.0. They bind
`x-form-field`, `x-search-field` and `x-rating` to a form value. Each still works with
`@vanelsas/baredom` 4.0.0 or later. The binding of `x-search-field` follows a clear with 4.10.1.

### Added

- **Form bindings for `x-form-field`, `x-search-field` and `x-rating`** in the five adapters:
  `v-model` in Vue, `bind:value` in Svelte, a control value accessor in Angular, and a
  controlled mode in React and Solid. A binding follows the committed change event, so a text
  field updates its value on blur or Enter, not while typing.
- **Two demos of BareMirror.** [Holes](https://avanelsas.github.io/baredom/bare-html/holes.html)
  is a table written from a template in ClojureScript, with a hole that `x-table` also writes
  and the check that finds it. [The tree](https://avanelsas.github.io/baredom/bare-html/tree.html)
  is now on the demo site too.

### Changed

- **BareMirror reads and plans a move in 1,000 rows in about half the time.** Moving the first
  row to the end through `syncPlaces` takes 0.50 ms, from 0.92 ms.
- **The task list demo** makes its counter with `defineElement`, and a row that leaves fades
  out after `release`.

### Fixed

- **A component writes `tabindex` only when it changes.** `x-checkbox`, `x-switch`, `x-radio`,
  `x-chip`, `x-alert`, `x-toast`, `x-table-row`, `x-table-cell`, `x-timeline-item` and
  `x-scroll` wrote it on every render. On a focused control that made the browser recalculate
  the styles of the page each time.
- **`x-search-field` sends `x-search-field-change` with an empty value when it is cleared.**
- **`writeParts` reads only the own fields of a write.** A field inherited from a prototype is
  no field.
- **An element from `defineElement` stays as it is when it moves** with `moveBefore`.
- **In the task list demo, Enter during IME composition no longer adds a task.**

## [4.10.0] - 2026-10-19

BareDOM 4.10 lets each component write its own selection or value only after an accepted
request, and reads a boolean attribute as HTML does. All five adapters are released with it:
`-react` 3.2.0, `-angular` 3.2.0, `-vue` 0.8.0, `-svelte` 0.8.0 and `-solid` 0.8.0. Each still
works with `@vanelsas/baredom` 4.0.0 or later. The `value` props of `x-table` and
`x-table-row` need 4.10.0.

### Added

- **`value` and `value-format` on `x-table`,** with their properties. `value` is the list of
  selected row values, comma-separated or a JSON array. `x-table` sets `selected` on the rows
  it names.
- **`value` on `x-table-row`.** A row's value is its `value`, else its `row-index`.
- **`x-table-row-select` is a request for the `value` of `x-table`,** and its detail gains
  `value`. It is in `requestedValues`.
- **`x-table-cell-sort` is a request for `sort-direction`.** It is in `requestedValues`, and it
  bubbles on past `x-table`, so a page can answer it.

### Changed

- **A boolean attribute that is off by default is on when present, whatever its text,** as in
  HTML. `x-checkbox`, `x-date-picker`, `x-command-palette`, `x-chart`, `x-badge`,
  `x-breadcrumbs`, `x-color-picker`, `x-context-menu`, `x-form` and `x-spacer` read the text
  `"false"` as off before. An attribute that is on by default still turns off with `"false"`,
  but no longer with `"0"` (`removable` of `x-chip`, `show-tooltip` of `x-copy`).
- **`x-table` holds its selection in `value`.** A click on a row asks for a new `value`. A row
  with neither `value` nor `row-index` is no longer selected by a click, and `selected` written
  on a row of a table with a `value` is replaced.
- **`x-tabs` writes `value` only after an accepted `value-change-request`.** Without a `value`
  it shows the first enabled tab marked `selected`, else the first enabled tab, and
  `tabs.value` is `null`. A `value` that names no tab is no longer replaced.
- **`x-select`, `x-form-field`, `x-text-area` and `x-search-field` write `value` after an
  accepted change.** A later refused change keeps the accepted value, and `previousValue`
  reports it.
- **An `x-table-cell` header writes its own `sort-direction`** when its request is not
  refused. A cancelled `x-table-sort` keeps the cell as it was.
- **Six events are no longer cancelable,** because their components never acted on a cancel:
  `x-form-submit`, `x-search-field-search`, `x-liquid-dock-select`,
  `x-proximity-list-select`, `x-timeline-item-click` and `x-color-picker-change`.
- **The `data` property of `x-chart` reflects its attribute.** Writing chart data, or JSON text
  that holds it, writes its JSON. Reading parses the attribute into a new value.

### Fixed

- **A refused change no longer loses an earlier accepted value** in the four form controls
  above.
- **`x-select` with no `value` restores the option the markup marks `selected`** after a
  refused change, keeps an option with an empty value, and gives its form the same value as its
  `value` getter.
- **The calendar of `x-date-picker` moves to the month of a date written after it connects,**
  and keeps a month the user browsed to otherwise. Opening shows the month of the date again,
  one day cell is always reachable by Tab, and Enter selects the cell it is pressed on.
- **Cancelling `x-table-row-click` on the row stops the selection,** as the docs said.
- **`x-tabs` hides the panel of a removed tab.**
- **The `data` property of `x-chart` no longer disagrees with the chart** after an attribute
  write, and no longer returns ClojureScript data.

## [4.9.0] - 2026-10-19

BareDOM 4.9 lets each request event show what its handler leaves. BareMirror cancels a request
when the page does not show the value it asked for, and names the holes that a component may
also write. No adapter is released with it.

### Added

- **`requestedValues` in `@vanelsas/baredom/requests`,** and `requested-values` in
  `baredom.requests`. It maps each request event to what it asks of each attribute: the name of
  a detail field, or a fixed boolean.
- **`@vanelsas/baredom/foreign-writes`,** and `baredom.foreign-writes`. It maps each component
  to the attributes it writes on nodes it does not own.
- **`sharedHoles` in BareMirror,** and `template/shared-holes`. It names each hole of a split
  template that a component may also write. It only reports.

### Changed

- **BareMirror `listen` takes `requestedValues`.** It cancels a request when its part does not
  show the asked value after the dispatch, so a `step` that accepts another value refuses the
  request. `listen` no longer takes `requests`. The `requests` map is still published.
- **The BareMirror functions for JavaScript read only the own fields of an object.**
- **A range click on `x-date-picker` asks for the range it gives.** The `start` and `end` of
  `x-date-picker-change-request` hold that range, and an accepted request writes exactly it. A
  picker given only `start` ends the range on the next later click.
- **A clear of `x-date-picker` holds `null` in `value`, `start` and `end`,** where it held `""`.
  The component removes those attributes.

### Fixed

- **A refused change shows and submits the `value` attribute that the handler left,** in
  `x-form-field`, `x-text-area`, `x-select` and `x-currency-field`.
- **A written `value` reaches the form** in `x-text-area`, `x-currency-field` and
  `x-form-field`.
- **`x-multi-combobox` no longer throws when a handler writes its value during a removal
  request,** and its panel shows every option left after an add that the handler wrote.
- **The input event of `x-text-area` reports what the textarea shows.**
- **`auto-swap` on `x-date-picker` swaps.** A click before the start gives the range from that
  day to the start. Before, it started a new range.

## [4.8.0] - 2026-10-17

BareDOM 4.8 lets `x-multi-combobox` take values that hold a comma, and fixes two `x-date-picker`
input bugs. All five adapters are released with it: `-react` 3.1.0, `-angular` 3.1.0, `-vue`
0.7.0, `-svelte` 0.7.0 and `-solid` 0.7.0. Each still works with `@vanelsas/baredom` 4.0.0 or
later. `value-format` needs 4.8.0.

### Added

- **`value-format` on `x-multi-combobox`,** and its property `valueFormat`. With `json`, the
  `value` attribute is a JSON array of strings, so a value can hold a comma or leading and
  trailing spaces. `csv` is the default. An invalid JSON value gives no selection and sends
  `x-multi-combobox-error`.
- **`x-multi-combobox-error`.** `x-multi-combobox` sends it when a `json` value is not a JSON
  array of strings, and when the `csv` setter warns about an array.
- **In `csv`, the `value` setter of `x-multi-combobox` warns about a value that a
  comma-separated list cannot hold:** a value with a comma, or with leading or trailing
  whitespace. It writes the array as before, so it splits such a value at the comma or trims it,
  and it sends `x-multi-combobox-error`.

### Changed

- **The adapters write the value of `x-multi-combobox` through its `value` property.** This is
  Vue `v-model`, Svelte `bind:value`, the Angular ControlValueAccessor, the React `defaultValue`,
  and the Solid `value` and `defaultValue`. Before, they wrote `String(value)` to the attribute,
  which is always comma-separated. Now the array round-trips in both value formats. In `csv`, a
  binding gives the same attribute and chips as before. The React adapter passes `value` after
  the other props, so `valueFormat` is set first.

### Fixed

- **A focused `x-date-picker` text box shows a new `start`, `end` or `value`,** and blur commits
  only text that the user edited. Before, the text box kept the old text, and blur committed it
  again.
- **Enter closes the `x-date-picker` panel when the element has `close-on-select`,** as a pick
  in the calendar does.

## [4.7.0] - 2026-10-17

BareDOM 4.7 lets a child's own attribute win over its group's, and keeps every skeleton in phase
on its own. The adapters are not released again.

### Changed

- **An avatar's own `size` wins over the `size` of its `x-avatar-group`,** and an avatar is
  disabled when it or the group is. The group no longer writes `size` and `disabled` on its
  avatars. It writes `data-group-size` and `data-group-disabled`, which are not public API.
- **A skeleton's own `animation` wins over the `animation` of its `x-skeleton-group`.** The
  group no longer writes `animation` on its skeletons. It writes `data-group-animation`, which
  is not public API.
- **`x-tabs` takes its first selection from a tab marked `selected`** when it has no `value`.
  Before, the mark was removed and the first enabled tab was selected. An enabled tab with no
  `value` is skipped.
- **Every `x-skeleton` on a page pulses in phase,** also outside a group. A skeleton aligns its
  animation to a clock of the page when it connects and when its animation changes.
- **`--x-skeleton-delay` is no longer a custom property of `x-skeleton`.** It was not
  documented, and a value set on the host no longer takes effect.

### Fixed

- **A skeleton that is removed and inserted again stays in phase,** and so do the skeletons of a
  group that is moved.
- **The `x-carousel` doc states that `current` keeps its value** when there are fewer slides.
  The last slide is shown.

## [4.6.0] - 2026-10-17

BareDOM 4.6 keeps the state of a moved component, lets one BareMirror sync bring a whole tree,
and makes a parent do only what a change of its children requires. The adapters are not
released again.

### Added

- **`@vanelsas/baredom/all`** registers every component with one `init()`.
- **A moved component keeps its state.** Every component has `connectedMoveCallback`. A move
  with `moveBefore` calls neither `connected-fn` nor `disconnected-fn`, and calls the optional
  `:moved-fn`. Seven components give one: `x-i18n` shows the text of its new provider, `x-radio`
  syncs the tab order of its new group, `x-liquid-fill` finds its scroll target again, and
  `x-table-row`, `x-table-cell`, `x-timeline-item` and `x-welcome-tour-step` still reach their
  parent.
- **One `sync!` brings a whole tree.** A container can name its parent by a key and a part, or
  name a part alone for the node of every key. A node that moves to another branch keeps its
  node and its focus. `bare-html/tree.html` shows it. See [`docs/baremirror.md`](docs/baremirror.md).
- **`foreignWrites` in `custom-elements.json`** names the attributes a component writes on
  nodes it does not own, such as its children. Fourteen components state it, and each doc has a
  section "Writes on other nodes".

### Changed

- **`perform!` places nodes before it removes others,** and places a container after the
  containers that hold it. It refuses a key placed inside its own node.
- **`x-table` and `x-timeline` follow their children through `slotchange`.** The stripes and the
  `data-*` attributes of timeline items are written in the microtask after a change, not during
  it. This is before paint. The rows, cells and items still send their events.
- **`x-tabs` no longer writes `tabindex` on its tabs.** Each `x-tab` sets its own.
- **`docs/PRERENDER.md` and `docs/baremirror.md` state Firefox 157 and Safari 26.5.** Safari
  has no `moveBefore`, so a moved row loses its focus there.

### Fixed

- **A choice in `x-select` survives a new or changed option.** It was reset to the marked option.
- **The `x-carousel` autoplay keeps running** when a slide is added or removed.
- **`x-scroll-story` and `x-scroll-timeline` no longer fire enter again on a resize,** and an
  entry that only moved fires no change event.
- **`x-welcome-tour` keeps the text typed in the open step** when its steps change.
- **A striped `x-table` restripes after a row is removed.** The rows below it kept their old
  stripes.
- **Fifty items appended to an `x-timeline` cost 50 writes, not 1275,** and a nested timeline
  keeps its own items.
- **Four components work again after a remove and an insert:** `x-chart` and
  `x-particle-button` follow a resize again, `x-kinetic-typography` draws its animation again,
  and `x-i18n-provider` fetches again when a disconnect cut its fetch short. A removed open
  `x-combobox` no longer leaks its document click listener.

## [4.5.0] - 2026-10-14

BareDOM 4.5 makes the prerender script open several pages at once. No component changes, and
the adapters are not released again.

### Changed

- **The prerender script opens four pages at once,** each in a tab of its own, or fewer on a
  machine with fewer processors. `--tabs` says how many. 351 pages were written in half the
  time. The pages are written in the order in which they are done. A page that fails
  still stops the run: no tab takes another page, the pages that are open are written, and the
  error says how many more failed. See [`docs/PRERENDER.md`](docs/PRERENDER.md).
- **The prerender script tells Chrome to draw every tab as the one in front:** it turns off
  the slowing down of background tabs, and tells each tab that it has the focus. A page is
  so drawn as if its window has the focus, also with one tab.

### Added

- **Tests of the prerender script,** run with `node --test scripts/prerender.test.mjs`, and in
  CI. They need Chrome.

## [4.4.0] - 2026-10-12

BareDOM 4.4 makes a prerendered page hold its pictures, and ships the script that prerenders.
`x-code` colours Clojure. The adapters are not released again.

### Added

- **`x-code` colours Clojure.** The language is `clojure`, and `clj`, `cljs`, `cljc`, `edn` and
  `bb` are aliases of it. The special forms and the macros that define or branch are keywords,
  a `:keyword` is a key, and a regex and a character are strings. A symbol with a special form
  in it, such as `my-def`, is plain. See [`docs/x-code.md`](docs/x-code.md).
- **The prerender script is in the npm package**, as the command `baredom-prerender`.
- **The prerender script writes many pages in one run.** Give it more pairs of a URL and a
  file. One browser opens them all, so four pages take about the time that two took. See
  [`docs/PRERENDER.md`](docs/PRERENDER.md).
- **The prerender script makes the directory of an output file** when it is not there.

### Changed

- **The prerender script waits for every image**, in the page and in every shadow tree. An
  image that the browser loads lazily is loaded for this, and is lazy again in what is written.
  An `x-image` is so written as loaded, and the same page gives the same file each time. The
  script stops and names an image that never finishes.
- **A page that fails names its URL in the error.**

### Fixed

- **`x-image` no longer fades in a second time on a prerendered page.** An image that the
  document has already is loaded in the turn that `src` is set, with no frame of the shimmer.
  `x-image-load` fires once.
- **A docked `x-sidebar` is no longer `aria-hidden` when it is not `open`.** It is in the page
  whether it is open or not. An overlay or modal sidebar is still `aria-hidden` while closed.

## [4.3.0] - 2026-10-11

BareDOM 4.3 makes BareMirror faster at many rows, and adds a function that makes a node by
copying one. No component changed. The adapters are not released again.

The times are for the task list of the demo with 1,000 rows, as medians in Chrome, for one whole
render through the JavaScript functions.

| Render | Before | After |
|---|---|---|
| The first one | 345 ms | 241 ms |
| Nothing changed | 5.2 ms | 1.0 ms |
| One row changed and moved to the other list | 6.3 ms | 2.5 ms |
| The first row moved to the end | 13.0 ms | 2.9 ms |

### Added

- **`parts/node-maker`, and `nodeMaker` for JavaScript**, in the BareMirror alpha. It takes a
  fixed template and returns a function that makes one detached element each time, by copying
  one node. A custom element in a copy is a plain element until the copy is in the page, so the
  first writes to it cost what an attribute costs. See [`docs/baremirror.md`](docs/baremirror.md).

### Changed

- **`plan/plan` is faster.** It finds the keys that stay with one pass and a binary search, and
  in a browser it looks a key up in a map of the browser. The plan is the same.
- **`readPlaces`, `plan`, `perform` and `syncPlaces` are faster in JavaScript.** A reading and a
  plan hold the value they were made from, so `perform` does not read them again.
- **In JavaScript, a reading and a plan are frozen.** `perform` still takes a plain object of
  the same shape.
- **The error for a template that is not fixed no longer names `make-node!`**, since
  `node-maker` gives it too.

### Fixed

- **In JavaScript, a copy of a split template is no longer taken as the template it was copied
  from.** `{ ...row, holes: other }` kept the first template inside it, and `writeItem` wrote
  with that. `writeItem` now refuses a copy.
- **`writeItem` compares two items faster.** The result is the same.
- **The demo of BareMirror, the task list and the examples of the doc make their rows with
  `nodeMaker` and `node-maker`.**

## [4.2.0] - 2026-10-11

BareDOM 4.2 lets BareMirror skip a row whose item did not change, and adds a demo that shows the
functions beside plain DOM code. No component changed. The adapters are not released again.

### Added

- **`parts/write-item!`, and `writeItem` for JavaScript**, in the BareMirror alpha. It brings a
  node to an item, and writes nothing when the item is the same as the one it last wrote to that
  node. JavaScript compares the fields of two object literals, one level deep. See
  [`docs/baremirror.md`](docs/baremirror.md).
- **`template/split?`**, for ClojureScript. It is true for a split template.
- **A demo of BareMirror.** One task list, rendered with a rebuild, with code by hand and with
  the functions, side by side, with counts and times. It is
  [on the demo site](https://avanelsas.github.io/baredom/bare-html/mirror.html), with
  [a copy that shows with no script](https://avanelsas.github.io/baredom/bare-html/mirror.static.html).
- **`scripts/prerender.mjs` takes `--static`.** The output has no `script` element, and its
  `html` element has `data-static`. The script also waits while the `html` element of the page
  has `data-loading`. See [`docs/PRERENDER.md`](docs/PRERENDER.md).

### Changed

- **`parts/read-parts` and `readParts` are faster.** One selector finds the parts of a node. The
  parts of 1,000 rows, read one by one, take 0.9 ms where they took 5.1 ms. The result is the
  same.

### Fixed

- **A prerendered page keeps its `html` tag.** `scripts/prerender.mjs` left the tag out, so the
  page lost its `lang` attribute.

## [4.1.0] - 2026-10-09

BareDOM 4.1 corrects how a form control follows its `fieldset`, and makes every component render
once for a change. The adapters are not released again. Each works with this version as it is.

### Fixed

- **A control in a disabled `fieldset` works again when the fieldset is enabled.** All 17 form
  controls stayed disabled, because they wrote a `disabled` attribute on themselves. A control no
  longer gets that attribute from its fieldset. A page that styled such a control by the attribute
  must select `:disabled`.
- **`x-button` and `x-particle-button` obey a disabled `fieldset`.** Both could still be pressed.
- **`x-form` leaves out the fields of a disabled `fieldset`**, from its values and from its
  validity check.
- **`disabled="false"` disables `x-checkbox` and `x-color-picker`**, as it does every other
  control and every native one. Both treated the text `false` as an absent attribute.
- **An `error` attribute with no text is no error.** It was reported as a custom error with an
  empty message, which a browser refuses.
- **Two attribute values no longer throw.** A `locale` of `x-date-picker` that is no language tag
  and a `target` of `x-liquid-fill` that is no selector are ignored.
- **A component renders once for a change, and not at all when nothing changed.** `x-range-slider`,
  `x-switch`, `x-text-area`, `x-slider`, `x-rating`, `x-radio`, `x-date-picker` and
  `x-search-field` rendered twice for a change of `disabled`. `x-metaball-cursor`,
  `x-welcome-tour`, `x-organic-shape`, `x-notification-center` and `x-sidebar` rendered when no
  attribute had changed. A hold of the render now also holds a change of `disabled`.

### Changed

- **`x-divider` and `x-carousel` write a default only where the host has none.** `x-divider`
  writes `role="separator"` and `x-carousel` its `aria-label` when the author gave no value, and
  neither touches a value of the author. `role="none"` on `x-divider` is no longer rewritten to
  `presentation`. `role` and `aria-label` leave the attribute list of `x-divider` in the manifest,
  and `aria-label` that of `x-carousel`.
- **The trace records a change of a fieldset**, as a change of the `disabled` attribute of the
  control.
- **For ClojureScript.** `component/register!` no longer takes `:form-disabled-fn`. Every
  form-associated element receives the callback as a change of its `disabled` attribute.
  `blob-colors` of the `x-metaball-cursor` model returns a vector. `separator-role?` left the
  `x-divider` model.

### Added

- **`forms/disabled?`**, for ClojureScript. It is true for a control that its own attribute or a
  `fieldset` disables.
- **The canonical values of a property, as data.** 150 properties of 75 components name their
  set of values as `:enum` in `property-api`. The generated types do not read it yet.

## [4.0.0] - 2026-10-08

BareDOM 4 makes every component follow the theme, gives every attribute a typed property, and
brings BareMirror as an alpha. A page looks different after this upgrade, with and without
`x-theme`, which is why it is a major version.

All five adapters are released with it: `-react` 3.0.0, `-angular` 3.0.0, `-vue` 0.6.0, `-svelte`
0.6.0 and `-solid` 0.6.0. Each now requires `@vanelsas/baredom` 4.0.0 or later.

### Breaking

- **The look under `x-theme`.** Ten families of values follow the theme where they were fixed
  before. See "Changed" below for what moves.
- **The font, also without a theme.** Buttons, inputs, selects and text areas inside components
  take the font of the page. `x-typography` and `x-kinetic-typography` show the page font, where
  they showed the system font.
- **`hidden` hides a component.** 101 components ignored the attribute. A page that set `hidden`
  on one of them and relied on it showing must remove the attribute.
- **Seven properties lost their dashed names.** `x-pagination` (`totalPages`, `siblingCount`,
  `boundaryCount`) and `x-breadcrumbs` (`maxItems`, `itemsBefore`, `itemsAfter`,
  `preserveAriaCurrent`) installed these under the names of their attributes, such as
  `el["total-pages"]`. They are now installed under the camel-case names that the docs and the
  types always gave. The attributes are unchanged.
- **`x-form-field` in the adapters: `readonly` is `readOnly`.** The adapters now use the name
  the element installs.
- **A boolean property that is on by default reads the text `"false"` as false.** A framework
  that writes `dismissible="false"` to `x-alert` through the property now turns it off.
- **The `code` property of `x-code` writes the attribute.** The component no longer keeps the
  value in a field of its own, so an attribute written later wins.
- **`--x-z-toast` is 9000, was 1200.** Under a theme, dialogs are at 1100 (were 1000),
  `x-command-palette` at 1100 (was 800), the tooltip of `x-copy` at 1000 (was 100) and
  `x-notification-center` at 9000 (was 9999).
- **The supported browsers are the current ones.** BareDOM is tested in Chrome and Edge 154,
  Firefox 157 and Safari 26.5. The floor of Chrome 67, Firefox 63 and Safari 14 is dropped.
- **Four `data-*` attributes of `x-timeline-item` left the manifest.** The parent writes them,
  and they are not public API.

### Added

- **BareMirror, as an alpha.** A small set of functions for an application on top of the
  components. They place keyed nodes, write values into the parts of a template and turn events
  into messages, with no comparing of old and new. `@vanelsas/baredom/baremirror-alpha` for
  JavaScript, and the namespaces `baremirror.alpha.*` for ClojureScript. A name there may still
  change. See [`docs/baremirror.md`](docs/baremirror.md).
- **The requests map.** `@vanelsas/baredom/requests` and `baredom.requests` say, for each
  cancelable request event, which attributes it asks to change. The manifest gives every event
  its cancelable flag and every request its attributes.
- **A typed property for every public attribute.** 79 new properties in 16 components, and
  `counter` and `dots` on `x-welcome-tour`. The manifest types every attribute, and a number
  property accepts numeric text, as a native one does.
- **A change request on two text fields.** `x-form-field-change-request` and
  `x-search-field-change-request` fire before a typed value takes effect. A cancelled request
  puts the field back to its `value` attribute.
- **Eight theme tokens.** `--x-radius-xl`, `--x-font-weight-bold`, and a `-hover` and an
  `-active` shade for `--x-color-danger`, `--x-color-success` and `--x-color-warning`. The shades
  are mixed from their colour, so a preset that names only the colour gets matching shades.
- **A hold of the render.** Every element offers a way to hold its attribute changes and to
  deliver them in order, for code that must write several attributes as one step.
- **A script that prerenders a page**, `scripts/prerender.mjs`. See
  [`docs/PRERENDER.md`](docs/PRERENDER.md).
- **Two documents.** [`docs/styling.md`](docs/styling.md) says at which level to style and where
  each value must be set to work. [`docs/TOKEN-COVERAGE.md`](docs/TOKEN-COVERAGE.md) counts, for
  each component, how many values follow the theme.

### Changed

Under `x-theme`, these values now follow the theme:

- **Colour.** A custom danger, success, warning or primary colour reaches alerts, badges,
  toasts, selected tabs and rows. Hover fills and tints are mixed from the theme's colours. A
  disabled `x-button` is a pale surface with muted text. The hover and pressed colour of a
  danger, success or warning button shifts slightly.
- **Corners.** 27 components take the theme's scale.
- **Motion.** Every triggered transition takes the theme's duration and easing.
- **Shadows.** They follow the theme, in dark mode too.
- **Type.** The font family, the weights and the body line height follow the theme. Text of
  13px and 15px is 14px, and the sizes of buttons and badges follow the scale.
- **Borders.** Edges, inputs and divider lines follow `--x-border-width`.
- **Layers.** Popups, dialogs and toasts sit on the three layers of the theme.

Also changed:

- A removed chip of `x-multi-combobox` fades out.
- The default cells in the component docs are generated from the manifest.

### Fixed

- **Lists keep their nodes.** Ten lists no longer rebuild their items on every change, so a
  node that stays keeps its focus: the pages of `x-pagination`, the chips and options of
  `x-multi-combobox`, the options of `x-combobox`, the steps of `x-stepper`, the dots of
  `x-scroll`, the day cells of `x-calendar` and `x-date-picker`, the items of
  `x-command-palette` and the rows of `x-file-upload`.
- **`x-table` honours a cancelled row select.** It changed the selection in every case.
- **`x-form-field` and `x-search-field` keep typed text** on a write of the value they already
  have.
- **`x-progress` and `x-progress-circle` drop `aria-valuemax`** when they become indeterminate.
- **`x-form`, `x-otp-input` and `x-search-field` set up on a prerendered page.** They took a
  shadow root that was already there as a sign that they were set up.
- **Three documents no longer show a component's own property set on `:root`**, where it has no
  effect.

This release contains the two fixes of 3.12.1.

## [3.12.1] - 2026-10-08

### Fixed

- **`x-multi-combobox` reports no `NotFoundError` when a chip is removed with the pointer.** A
  click on the remove button of a chip, while the panel was open, moved focus into that chip.
  Removing the chip then closed the panel from inside the render, and the browser reported
  `Failed to execute 'removeChild' on 'Node'`. The selection was correct, but an ordinary gesture
  left an error in the console. Focus now moves to the input before the chip leaves, so the panel
  stays open and focus stays in the control. (#376)

- **`x-currency-field` keeps `value` current while the user types.** The `value` attribute was
  written on change only, so the property was stale during an edit, and with it the values that
  `x-form` collects on a submit with Enter. The attribute now follows each keystroke. (#264)

  Three things follow from that:

  - The inline message for a bad number or a value out of range waits while the user types, and
    shows on change or blur. Validity itself follows each keystroke, as before.
  - The display is formatted on every blur, also when the value did not change.
  - On a change, `previousValue` of `x-currency-field-change-request` is the typed text. It was
    the value before the edit.

## [3.12.0] - 2026-09-15

### Added

- **`x-file-download` saves through the native save dialog.** A new `picker` attribute makes a
  click open `showSaveFilePicker`, so the user chooses the folder and the name. The component
  then fetches `href` and streams it into the chosen file, and reports the outcome as
  `x-file-download-success`, `x-file-download-cancel` or `x-file-download-error` with the failing
  phase. Browsers without the API, such as Firefox, Safari and Brave, keep the plain download, as
  do cross-origin frames and a dialog that cannot open. `picker` is off by default, so existing
  pages are unchanged.

  All five adapters gain the `picker` prop and the three events: `-react` 2.5.0, `-angular`
  2.11.0, `-vue` 0.5.0, `-svelte` 0.5.0, `-solid` 0.5.0.

## [3.11.0] - 2026-08-28

### Fixed

- **Event detail keys are published as the component dispatches them.** `x-particle-button`
  dispatches `#js {:press-x … :press-y …}`, so those are the keys on the event, but the generated
  types camel-cased them and promised `pressX` and `pressY`. Every typed caller read a field that
  is not there. The `.d.ts`, `custom-elements.json` and all five adapters now say `"press-x"`,
  quoted because a hyphenated key is not a bare identifier in a TypeScript type.

  This supersedes the detail half of the 3.3.0 fix "TypeScript declarations", which corrected the
  same invalid identifiers by renaming them. That made the types compile and made them wrong.
  Property names are unaffected and still camel-case, because a property reflects an attribute and
  genuinely changes spelling.

  Breaking for anyone writing `e.detail.pressX` — which never resolved to anything. Only
  `x-particle-button` is affected; no other component has a kebab-case detail key. Every adapter
  published the wrong key, so all five are released with it: `-react` 2.4.1, `-angular` 2.10.1,
  `-vue` 0.4.2, `-svelte` 0.4.2, `-solid` 0.4.1.

### Added

- **`detail` on every event in `custom-elements.json`** — what an event carries, as data, beside
  the TypeScript in `type.text`. A reader no longer parses a type expression to learn the shape.
  Both come from one function, so they cannot disagree.

### Changed

- **`scripts/check_event_api.bb` also compares the manifest with the model.** It read the model on
  both sides, so anything the generator did between them was invisible — which is how the
  camel-casing above survived it. It now compares names and types over the union of both sides, so
  a schema edited without regenerating is caught too.

## [3.10.0] - 2026-08-28

### Changed

- **`x-date-picker` change details are one shape in both modes.** `x-date-picker-change` and
  `x-date-picker-change-request` carried a different key set per mode — `value` in single mode,
  `start` and `end` in range mode, and `date` on a range click — while `event-schema` declared only
  the first. Every key is now always present and `null` where the mode has nothing to say, so the
  declaration is true and a handler null-checks instead of testing for a key that was never there.

  This widens the published types from `string` to `string | null` for `value`, `date`, `start` and
  `end` on those two events, in `custom-elements.json`, the `.d.ts` files and all five framework
  adapters. A consumer writing `const v: string = e.detail.value` will need a null check. Nothing
  about when the events fire has changed.

### Fixed

- **The Svelte and Vue adapters no longer write `"null"` into a bound value.** Released as
  `@vanelsas/baredom-svelte` 0.4.1 and `@vanelsas/baredom-vue` 0.4.1. Both coerced the
  change detail unconditionally — `String(detail.value)`, `Number(detail.page)` — so a detail field
  that did not apply arrived as the literal string `"null"` in `bind:value` / `v-model` and was
  written back onto the element as an attribute. They now write only when the field is present.
  `false`, `0` and `""` are values and still write; only `null` and `undefined` are skipped.
  Affects the twelve form controls in each adapter. React, Solid and Angular do not write back and
  were never affected.

### Added

- **`scripts/check_event_api.bb`** — a CI check that reads each component's dispatch calls and
  asserts the manifest's event details agree with them. It reads forms rather than text, follows a
  detail through a `let`, a helper, a namespace alias and `clj->js`, and reports what it could not
  read rather than counting it as agreement. 198 of 201 events are compared; the three it cannot
  resolve are named. It is what found the `x-date-picker` mismatch above.

## [3.9.0] - 2026-08-26

The manifest declares `schemaVersion 1.0.0` and omitted three fields the schema defines. It now
publishes all three, so an editor reading `custom-elements.json` completes attributes and custom
properties that it previously knew nothing about.

### Added

- **`attributes`** — every attribute a component observes. An attribute that a `property-api`
  entry reflects also carries that property's type and field name. The rest carry a name alone,
  because the component states no type for them. 809 attributes across 108 components, 480 of
  them typed. `observed-attributes` names symbols rather than strings, so each one is resolved
  through the model's own constants: a symbol that escaped resolution would publish `attr-columns`
  as if it were an attribute name.

- **`cssProperties` and `cssParts`** — the custom properties a component reads and the parts it
  exposes, read from the implementation source that writes them rather than from a second
  declaration. A property is owned by the longest tag that prefixes it, so `--x-table-cell-padding`
  belongs to `x-table-cell` and not to `x-table`. 1351 properties and 489 parts.

- **`scripts/check_css_api.bb` and `scripts/check_attribute_api.bb`** — CI checks that read each
  component crudely and assert the manifest agrees. Each is written to avoid the assumption its
  extractor makes, because a check that shares one agrees with the bug.

### Notes

- **`src/` is unchanged**, so the ESM bundles, the framework adapters and the compiled namespaces
  in the jar are identical to 3.8.0. Only the manifest grew.

## [3.8.0] - 2026-08-14

### Added

- **`baredom.dev.pick`** — a dev-only namespace for pointing at elements from a REPL, in both directions. `pick!` puts the page into click-to-select and answers with a stable `tag#n` id for whatever is clicked, `element-for` resolves that id back to an element, and `show!` outlines where it is. The id is minted here rather than reused from `x-trace-history`'s component id, which is an integer assigned inside the trace hook and only exists while the recorder is installed, because picking has to work with tracing off. Ids are numbered per tag in document order, so `x-table-cell#7` is the seventh cell on the page rather than the seventh one clicked, and an id survives a re-projection replacing the node.

### Notes

- **Nothing here is reachable from a component**, and no custom element is registered, so the ESM bundle and the framework adapters are unchanged. The namespace ships in the Clojars jar alongside the other `baredom.dev` namespaces and is not exported to npm.

## [3.7.1] - 2026-08-12

### Fixed

- **`x-drop-zone`** no longer animates a panel in from the top-left corner. `animate-moves` measured panels with `getBoundingClientRect`, which returns `(0,0)` inside a `display: none` subtree, so a panel captured while hidden flew in from the viewport origin once shown. The FLIP decision is now a pure `flip-delta` that returns nil when either box was measured with zero size, and nil when the panel did not move.

## [3.7.0] - 2026-07-30

Two new components that work as a pair: a panel the user can pick up and a region that accepts it. Together they cover kanban boards, sortable lists, and any interface where something is moved from one place to another.

### Added

- **`x-drag-panel`** — a surface the user picks up with a pointer or the keyboard, with a grip handle over a body slot. Built on pointer events rather than the HTML5 drag-and-drop API, which never fires from touch input on iOS or Android, so it works from 320px up. `grab` chooses whether the whole card is a drag target or only the handle bar; in surface mode a touch drag arms after a short press and cancels if the finger travels first, so swiping over a card still scrolls the board. Dragging toward the edge of a scrollable ancestor auto-scrolls it, resolving the vertical and horizontal scrollers independently so a board that scrolls sideways with columns that scroll down works in both axes at once. Emits `x-drag-panel-drag-start` and `x-drag-panel-drag-cancel`.

- **`x-drop-zone`** — a region that senses panels passing over it, renders an insertion caret where one would land, and reports the drop. Acceptance is declarative: `accepts` matches the panel's `kind` and `max` caps capacity, so a full or incompatible zone shows a reject cue during the hover rather than silently ignoring the drop. `animate-moves` animates children into place whenever the child list changes, which covers drop confirmations, another client's change arriving over a websocket, and filter toggles with one mechanism. Emits `x-drop-zone-enter`, `x-drop-zone-leave`, and `x-drop-zone-drop`.

### Notes

- **Neither component moves a node.** A drop is a request, not a fact — an app may validate it locally and refuse, queue it offline, or send it to a server that reorders differently. The components sense and report; the application owns the landing. `x-drop-zone-drop` carries `{ kind, value, from, to, index, panel }`, where `value` identifies the panel and `from` / `to` are the two zones' `value`s, so a server-backed handler works entirely in opaque strings and never holds an element across a round trip. `from` is `null` when the panel came from outside any zone, distinct from `""` for a zone with no `value`, and is typed `string | null` so the check is not optional.

- **For moves that need confirming**, `zone.reserve(panel, index)` marks the in-flight window and `zone.release()` clears it. The panel renders an empty dashed footprint that keeps its box and the zone renders a reserved caret, so nothing in the layout moves until the answer arrives — every real reflow happens once, when the truth does. Nothing times out: a stalled move stays visible and keyboard-reachable rather than becoming a lost card. The underlying `pending` and `pending-index` attributes remain author-settable for apps that prefer to drive them directly.

### Accessibility

- The full interaction is keyboard-operable, which WCAG 2.2 SC 2.5.7 (Dragging Movements) requires of any drag: <kbd>Space</kbd> picks a panel up from its handle, arrows cycle the accepting zones in document order and scroll each into view, <kbd>Enter</kbd> drops, <kbd>Esc</kbd> cancels. Pointer and keyboard produce the same `x-drop-zone-drop` detail, so a consumer handles one path rather than two.

- Focus moves to the destination zone on commit rather than riding the panel, because a confirmation re-render will often replace the panel element and focus would fall to `<body>`. Each component narrates the events it owns into a visually hidden live region; the panel's is created lazily at pickup so a fifty-card board carries no idle `aria-live` nodes. Whether a move succeeded is read back from the DOM rather than assumed, so a server that refused is announced honestly.

- Auto-scroll is deliberately **not** disabled under `prefers-reduced-motion` — it is function rather than decoration, and a board deeper than the viewport would otherwise be unusable for the people relying on that setting. The lift transition, caret pulse, and `animate-moves` all are.

- The framework adapters gain generated `x-drag-panel` and `x-drop-zone` wrappers. They version on their own cadence — see [`docs/RELEASING.md`](docs/RELEASING.md) — so releasing them is a separate step.

## [3.6.0] - 2026-07-27

One new component: a floating, draggable container for inspectors, tool palettes, mini-players, and any persistent surface the user should be able to move out of the way.

### Added

- **`x-floating-panel`** — a non-modal container that floats above page content at a viewport-fixed position and is repositioned by dragging its handle bar or, when the handle has focus, with the arrow keys. Position lives in the `x` / `y` attributes, so a panel renders where you declared it on first paint with no measure-then-place flash, and clamping keeps the handle reachable whenever the panel opens, connects, moves, or the viewport resizes — a panel parked off-screen is pulled back rather than stranded. Optional `closable` (close button plus `Escape`), `resizable` (native CSS resize grip on the body), `focus-on-open` (off by default, so an ambient panel never steals focus from what the user is typing in), and `step` for arrow-key distance. Emits `x-floating-panel-toggle`, a cancelable `x-floating-panel-dismiss-request`, and `x-floating-panel-move` on drag end and keyboard nudge — never per animation frame.

### Accessibility

- The handle bar is keyboard-operable, which WCAG 2.2 SC 2.5.7 (Dragging Movements) requires of any drag interaction. It is focusable and carries `aria-roledescription="Drag handle"` plus `aria-keyshortcuts`, because the handle is moved rather than activated and a bare `role="button"` would promise an activation that never happens. The panel is a non-modal `role="dialog"` with no focus trap, always accessibly named from the `header` slot or the `label` attribute.

### Notes

- Interactive content in the `header` slot can opt out of the drag surface with `data-no-drag`; the built-in close button is excluded automatically.
- The framework adapters gain a generated `x-floating-panel` wrapper. They version on their own cadence — see [`docs/RELEASING.md`](docs/RELEASING.md) — so releasing them is a separate step.

## [3.5.0] - 2026-07-24

Form-associated components gain the instance-level validation API they were missing, and two form-control bugs are fixed: emptying an `x-date-picker` input now clears the committed date, and a form reset now clears validation errors.

### Added

- **The native validation API on all 17 form-associated components** — read-only `validity`, `validationMessage`, `willValidate`, `form` and `labels`, plus `checkValidity()` and `reportValidity()`. `ElementInternals` never mirrors these onto the host element, so only `x-search-field`, `x-currency-field`, `x-otp-input` and `x-file-upload` exposed any of it (the two methods), and every other control could be validated only by attempting a submit. `x-checkbox`, `x-color-picker`, `x-combobox`, `x-date-picker`, `x-form-field`, `x-multi-combobox`, `x-radio`, `x-range-slider`, `x-rating`, `x-select`, `x-slider`, `x-switch` and `x-text-area` now expose the full surface, and the four above are extended to match.

### Fixed

- **`x-date-picker` ignored a cleared input.** Deleting the text and committing it (blur, `Enter`, or `commit()`) left the previous date committed and repainted the old text — there was no way to clear the field from the keyboard. It now clears the value through the same cancelable `x-date-picker-change-request` path a normal commit uses, reports empty strings in the change detail (`{ value: "" }`, or `{ start: "", end: "" }` in range mode), and resets the range step so a half-picked range cannot complete against a start that no longer exists. Committing a blank input when nothing is committed stays a no-op.
- **`form.reset()` left stale validation errors.** Reset cleared the value but not the `error` attribute, so the inline message, `data-invalid`, `aria-invalid` and the `customError` flag survived it on `x-date-picker`, `x-select`, `x-combobox`, `x-multi-combobox`, `x-form-field` and `x-text-area`. All six now drop the attribute on reset.

### Changed — internal

- **`forms/install-validity-api!`** (new, in `baredom.utils.forms`) is the single implementation of the validation surface; the four components that hand-rolled `checkValidity` / `reportValidity` are migrated onto it. The five read-only members are declared in each `property-api` as `:readonly true` — the framework adapters skip them and the generated declarations mark them `readonly` — and the two methods in `method-api`.
- Twelve validity assertions across five test suites sat behind a `(when (.-validity el) …)` guard that was never true, and an input-commit regression test dispatched an event the component never listens for. All of them now genuinely run, alongside a new cross-component suite covering the surface on all 17 tags.

### Notes

- No adapter release accompanies this one: the new members are read-only, so the generated wrappers filter them out and the adapter packages are functionally unchanged.

## [3.4.0] - 2026-07-22

The four popup-selection form controls — `x-select`, `x-date-picker`, `x-combobox`, and `x-multi-combobox` — become **real form controls**: they display inline validation errors and participate in native `<form>` submission and constraint validation via `ElementInternals`. Two internal refactors extract the resulting form-control policy into one shared, unit-tested place.

### Added

- **Inline validation errors** on `x-select`, `x-date-picker`, `x-combobox`, `x-multi-combobox` — a new `error` attribute (and matching `error` property) renders a message in an `error` CSS part (an assertive `role="alert"` live region), marks the host `data-invalid`, and sets `aria-invalid` / `aria-describedby` on the control. Inside `x-form` this is driven for you by `form.setFieldError(name, message)`.
- **Form association** for those four controls — they now attach `ElementInternals`, submit their value under `name` (appearing in `FormData` with no hidden input), honour `required`, expose the `error` attribute as a `customError`, and respond to `form.reset()` and a disabling `<fieldset>`.
- **Framework adapter form bindings** for the newly form-associated controls: `x-date-picker` gains Vue `v-model` / Angular `ControlValueAccessor` / Svelte `bind:value` / React & Solid controlled `value`; `x-multi-combobox` gains the same with an array (`string[]`) value via new array-aware adapter codegen.

### Changed — behavior changes (please read)

These are bug fixes — the controls advertised `name` / `required` but did not honour them — but they change runtime behavior for anyone already using these four inside a `<form>`:

- **Native form participation.** Placed in a plain `<form>`, these controls now submit their value under `name` and appear in `FormData`. Previously they did not (unless manually wired); a hidden-input workaround for the same `name` will now double-submit.
- **`required` and `error` now block submission**, and `form.reset()` now clears these fields. Previously `required` was inert and reset skipped them — a form that used to submit with an empty required select/combobox/date-picker will now be gated by `reportValidity()`.

No attributes, properties, or events were removed or renamed; this is otherwise additive.

### Changed — internal

- **`baredom.utils.forms`** (new) — the ElementInternals constraint-validation policy and the inline error-display recipe, previously hand-implemented in each form control, are extracted into one namespace. Both the validity decision and the `aria-describedby` projection are pure functions split from their effects, and are now unit-tested (coverage the effecting versions never had). All six form-associated components (the four above plus `x-form-field`, `x-text-area`) compose the shared validity policy; the four popup controls also compose the shared error display.
- **`custom-elements.json`** — regenerated to pick up the `error` fields and `x-search-field`'s `debounce` field that had drifted out of the committed manifest.

### Notes

- Adapter packages are versioned independently. This release ships alongside `react@2.2.0`, `angular@2.8.0`, `vue@0.2.0`, `svelte@0.2.0`, `solid@0.2.0` — each a minor bump carrying the new form bindings. See [`docs/RELEASING.md`](docs/RELEASING.md).

## [3.3.0] - 2026-05-26

Three new framework adapters ship alongside the existing React and Angular adapters: **Vue 3**, **Svelte 5**, and **SolidJS** are now officially supported. BareDOM users of every major JS framework can now install a typed wrapper package that adds framework-idiomatic props, events, and ref handling on top of the same underlying web components.

The core library itself is functionally unchanged from 3.2.0 — `dist/` is byte-identical. This release ships an updated `README.md` (and an additional `CHANGELOG.md` entry) so the npm-registry view reflects the now-complete five-adapter ecosystem.

### Added — framework adapters

- **`@vanelsas/baredom-vue`** — Vue 3.4+ wrapper components. v-model bridging via the shared `form-control-metadata` single-source-of-truth, typed events, ref forwarding through `expose()`. Install: `npm install @vanelsas/baredom-vue`.
- **`@vanelsas/baredom-svelte`** — Svelte 5+ wrapper components. `$bindable()` for form controls, `$effect()` for event binding, per-component `.svelte` + `.ts` shim files for named exports. Install: `npm install @vanelsas/baredom-svelte`.
- **`@vanelsas/baredom-solid`** — Solid 1.9+ wrapper components. Function components with `splitProps`, `onMount`, `onCleanup`, `createEffect`. JSX is preserved for the consumer's Solid bundler. Install: `npm install @vanelsas/baredom-solid`.

All three adapters auto-generate one typed component per BareDOM custom element (103 components total). Per-component bundles, controlled-input support via Vue v-model / Svelte `$bindable()` / Solid signals, typed `CustomEvent` payloads — all driven by the same Custom Elements Manifest that already powered the React and Angular adapters.

### Changed

- **`README.md`** — Restructured for the five-adapter era. New "Framework adapters" comparison table. "Why web components?" and "Design Principles" sections merged into a single "Why BareDOM?". Theming section trimmed (full guide lives in [`docs/x-theme.md`](docs/x-theme.md)). Quick-start replaced with a working HTML example. New top-level "Time-Travel Debugger" section surfacing [`x-trace-history`](docs/x-trace-history.md) — previously buried inside a value-prop bullet.
- **[`docs/components.md`](docs/components.md)** — Full per-component catalogue (103 components across 11 categories) split out from the README. The previous README listed only 58 of the 103 components; the kinetics, organic, effects, and scroll clusters are now properly catalogued.
- **`scripts/codegen_shared.bb`** (new) — Shared codegen module housing `resolve-event-name`, `prop-type->ts`, and `event-detail->ts`, previously copy-pasted across all four adapter generators (~270 lines of duplication eliminated). React's `controlled-components` map now derives from `scripts/form-control-metadata.bb`, closing a silent drift trap for new form controls.
- **`CLAUDE.md`** — Registration checklist for new components now requires updating both `docs/components.md` and the `README.md` Components overview.
- **CI** — New `.github/workflows/release-solid.yml` matching the per-adapter release pattern. [`docs/RELEASING.md`](docs/RELEASING.md) extended to cover the per-adapter release process and first-publish `NPM_TOKEN` permission considerations.

### Fixed — adapter bugs

- **`@vanelsas/baredom-angular`** — `x-select` CVA used the wrong change-event name. Shipped via PR #242.
- **`@vanelsas/baredom-vue`** — Vue-reserved prop names (`key`, `ref`, `is`) were silently dropped by Vue. Reserved props are now renamed in the generated wrapper (e.g. `keyAttr`) and the underlying attribute is written imperatively. Shipped via PR #243.

### Notes

- Adapter packages are versioned independently and live at `@vanelsas/baredom-{react,angular,vue,svelte,solid}`. See [`docs/RELEASING.md`](docs/RELEASING.md) for the per-adapter release process.
- The five published adapter versions at the time of this release: `react@2.1.0`, `angular@2.7.0`, `vue@0.1.0`, `svelte@0.1.0`, `solid@0.1.0`.

## [3.2.0] - 2026-05-22

Five new components, an element-highlight feature for the `x-trace-history` dev tool, two notable bug fixes, and an internal naming-consistency cleanup.

### Added

- **x-split-pane** — Resizable two-panel layout. A draggable divider re-proportions a `start` and an `end` panel along a horizontal or vertical axis. The divider position is a percentage held in the `position` attribute — the single source of truth — so layouts are fully serialisable and the component stays stateless. Nest panes for three or more panels.
- **x-code** — Code-display component. Reads source code from its own light-DOM text content, tokenizes it with a built-in syntax highlighter, and renders a themable code block with optional line numbers, soft-wrapping, a header bar with a copy button, and a collapsible long-snippet mode.
- **x-calendar** — Standalone, always-visible inline month calendar — a companion to `x-date-picker`. Supports single-date and date-range selection, localized weekday/month names, configurable first-day-of-week, a month/year quick-jump header, and an optional ISO week-number column. Dates are ISO 8601 strings.
- **x-range-slider** — Dual-handle, form-associated, accessible range slider. Two draggable thumbs select a numeric `[start, end]` sub-range within `[min, max]`.
- **x-rating** — Discrete, form-associated, accessible star-rating component. A row of star (or heart) icons selects a numeric rating, optionally in half-star increments; `readonly` mode displays an average rating such as `3.5`.
- **`x-trace-history` element highlight** — Selecting a record in the dock now outlines the live web component that emitted it with a magenta accent overlay. The outline tracks the target through scroll/resize, smoothly scrolls offscreen targets into view (respecting `prefers-reduced-motion`), and clears when selection clears, when the dock unmounts, or when the originating element has been disconnected since the record was emitted. `document`-tag records produce no outline. The highlight machinery emits zero trace records, and the dock remains opt-in via `?baredom-trace-history` — never shipped in production.

### Fixed

- **x-card** — Interactive cards declared in HTML with an observed attribute set at upgrade time silently never installed their listeners, so `<x-card interactive>` never responded to clicks. `connected!`'s install guard conflated refs-init with listener-install; the install epoch now lives on its own `k-installed` field, gated independently of refs init.
- **x-modal** — `x-modal-toggle` was dispatched on every connect because the open-transition epoch started at `nil` and always differed from the initial open state. The toggle decision is now folded into the main render-pipeline epoch and gated on `(some? old-m)`.

### Changed

- **Shared date utilities** — Date math extracted into a shared `baredom.utils.dates` namespace, consumed by `x-calendar` and `x-date-picker`.
- **Internal naming consistency** — The top-level model transformer was renamed from `derive-state` to `normalize` across 23 component model layers and their tests, so every component uses the same name. No behaviour change.
- **Dead-code removal** — Write-only instance fields (set but never read) removed from x-combobox, x-multi-combobox, x-welcome-tour, x-spotlight-card, x-stat, x-tab, and x-tabs.
- **`x-trace-history` highlight refactor** — `apply-highlight!` decomposed into a phase-list dispatcher per the `render-orchestrator` pattern; `reposition!` now clears the stale cached component id when the target has been disconnected; the reduced-motion probe caches the live `MediaQueryList` instead of re-invoking `matchMedia` on every show.
- **Angular adapter** — `@vanelsas/baredom-angular` dev dependencies bumped from Angular 17 to 21; `custom-elements.json` refreshed.
- **CI** — `check_method_api.bb` taught about the `define-method!` helper introduced by the x-modal refactor, so wrapped method installations are no longer flagged as "declared but not installed".

## [3.1.0] - 2026-05-16

A full-library audit cycle against the `x-icon` golden sample (PRs #222–#231). All 99 components now conform to the Hickey-level architecture standard documented in `CLAUDE.md`. No public API or behavioural changes.

### Changed

- **`x-trace-history` recorder noise reduction** — rAF hot paths in x-kinetic-canvas, x-ripple-effect, x-scroll-parallax, x-scroll-stack, and x-particle-button now consistently route per-frame writes (`k-raf`, `k-last-frame`, `k-time` animation bookkeeping; per-frame attribute updates inside `animate!` loops) through `du/setv-untraced!` / `du/set-attr-untraced!`. The recorder no longer drowns in 60×/sec writes that carry no diagnostic value.
- **`property-api` metadata consistency** — `:reflects-attribute` entries added across x-progress-circle, x-stepper, x-splash, x-table-cell, x-table-row, x-theme, x-timeline, x-timeline-item, and x-toaster. The React and Angular code generators (`generate_react.bb` / `generate_angular.bb`) and `metadata.bb` now see a complete property-to-attribute contract for every component.
- **Render-pipeline decomposition** — oversized `apply-model!` / `init-dom!` / `make-shadow!` / `create-shadow!` / `start-transition!` / `build-refs!` functions split into named phase helpers across 10 components (x-button, x-dropdown, x-navbar, x-toast, x-select, x-kinetic-font, x-kinetic-typography, x-morph-stack, x-avatar-group, x-combobox).
- **`du`-discipline sweep** — cleared 41 `gobj/get|set` calls on host elements in x-sidebar plus stray hits in x-radio. Broadened `scripts/check_du_discipline.bb` twice (to catch the `instance` host-variable name and the multi-line `(defn- make-el [tag] …)` shim) so the patterns can't regress in CI.
- **Pipeline canonicalisation** — x-select moved its change-guard into `update-from-attrs!` and adopted the canonical `[el m]` `apply-model!` signature. x-card's `event-schema` switched to the canonical `{event-symbol {:detail {…}}}` shape. x-radio decomposed `try-select!` into named phase helpers.

### Fixed

- **x-progress-circle** — `variant`, `size`, and `label` are now exposed as JS properties. They were observed attributes without corresponding property installations, so `el.variant = "primary"` was a silent no-op.
- **x-collapse** — `read-model` now routes through `model/normalize`. It was reconstructing the view-model inline and duplicating the duration-ms parsing/clamping logic the model layer already provides.
- **x-date-picker** — Removed a forbidden multi-line `(defn- make-el [tag] …)` shim that the CI regex was missing; 13 call sites inlined to `.createElement js/document` directly.

## [3.0.0] - 2026-05-12

### Added

- **x-trace-history** — A time-travel debugger for BareDOM components. Records every CustomEvent, attribute mutation, instance-field write, and lifecycle callback as plain JS records with a stable schema. Floating dock with cross-instance lanes, scrubber, synchronous cause→effect causality DAG view, heatmap density rendering for animation-heavy lanes, full-text search across recorded values, bounded recording sessions, and JSON export/import. Ships as a separate ESM module (`@vanelsas/baredom/x-trace-history`) activated by `?baredom-trace-history` URL param or `window.BAREDOM_TRACE_HISTORY = true` — zero overhead when off. TypeScript declarations (`TraceRecord` discriminated union + `window.BareDOM.traceHistory` console API) included. See [`docs/x-trace-history.md`](docs/x-trace-history.md) and [`docs/x-trace-history-schema.md`](docs/x-trace-history-schema.md).
- **Standalone trace viewer** — `public/viewer.html` deployed alongside the demo site for sharing recorded traces. Drag-drop a `.trace.json` file or pass `?trace=<base64>` to load a trace into the dock.
- **Adapter integration tests** — React (`adapters/react/test-app/`) and Angular (`adapters/angular/test-app/`) test-apps now exercise the trace-history dock end-to-end, with dev-tool guidance added to each adapter's README and main doc.
- **Dispatch + lifecycle hook points** — `baredom.utils.dom` and `baredom.utils.component` expose `*trace-hook*` / `*lifecycle-hook*` extension atoms gating instrumentation of every CustomEvent dispatch, attribute mutation, instance-field write, and lifecycle callback. One nil-check cost when no recorder is installed.

### Fixed

- **x-select** — Slotted `<option selected>` default no longer gets stomped on render when the host has no `value` attribute. An explicit `value` attribute still overrides the slotted default.

### Changed

- **Tooling** — Sibling demo build outputs (`bare-demo/public/js/`, `bare-html/js/`, etc.), the Angular CLI cache, and the clj-kondo cache are now correctly gitignored. Adapter test-app `package-lock.json` files are tracked for reproducible smoke-test installs.

## [2.9.0] - 2026-05-09

### Added

- **x-spotlight-card** — Card surface with a cursor-following spotlight highlight (motion-respecting, theme-aware).
- **x-confetti** — Imperative celebration burst component with canvas particles.
- **x-kbd** — Platform-aware keyboard key / shortcut display.
- **x-otp-input** — Form-associated OTP / verification-code input.
- **x-proximity-list** — Dock-effect list with proximity scaling.

### Changed

- **Centralized component registration** — `baredom.registry` is now the single source of truth for the full component set; `baredom.core/start!` and `baredom.exports.all/init` both consume the same vector.
- **Overlay listener registry** — Replaced overlay magic-string listener keys with `baredom.utils.overlay/attach-listener!`, eliminating drift between add/remove sites.
- **Pure model layer expanded** — Pointer-coord math moved out of `x-color-picker` into the model layer; coordinate math moved out of `x-chart` into the model layer; series-point computation in `x-chart` is now pure.
- **Render-pipeline decomposition** — `render!` (and adjacent monolithic functions) decomposed into named phase helpers in: `x-chart`, `x-button`, `x-combobox`, `x-color-picker`, `x-scroll-timeline`, `x-particle-button`. Each `render!` now reads as a phase list (`render-orchestrator` pattern); long shadow builders split per the `shadow-builders` pattern; `x-color-picker` listener wiring moved to a single `listener-spec` table.
- **`x-proximity-list` settling loop** — Replaced the JS-object settling flag with a `loop`/`recur` driver, removing mutable instance state.
- **`x-date-picker`** — Force-close moved out of `render!` (audit Phase 5).
- **Host attribute access** — Migrated to `du/*` wrappers across the remaining call sites (audit Wave 4c).
- **Utils contracts** — Documented `baredom.utils.*` contracts; `normalize-prop-val` extracted as a reusable helper.
- **shadow-cljs** — Bumped to 3.4.7.
- **CLAUDE.md** — Added audit-derived guidance (named patterns, accessor tiers, error-recovery entries, function-size rule with orchestrator exemption).

### Fixed

- **x-chart** — Category x-axis off-by-one on the rightmost label.
- **x-particle-button** — Palette no longer goes stale after a `variant` change; the cached palette is invalidated when variant attributes flip.

## [2.8.0] - 2026-05-06

### Added

- **x-i18n / x-i18n-provider** — Translation components. `x-i18n-provider` holds the dictionary and active locale; `x-i18n` renders a translated string for a given key with parameter interpolation.
- **x-kinetic-canvas** — Animated background component (canvas-based, motion-respecting).
- **x-multi-combobox** — Combobox variant with multi-select chips, keyboard navigation, and a cancelable `change-request` event.

### Changed

- **Internal architecture audit** — 18 PRs across the component library: `event-schema` defs added to every model, lifecycle callbacks normalized to `!`-suffix `defn-` form, `^js` hints filled in for Closure Advanced safety, event dispatch routed through `du/dispatch!` / `du/dispatch-cancelable!`, and property accessors migrated to `du/define-*-prop!` helpers. 36 components now register properties via the one-line data-driven `du/install-properties!` (Tier 0). Net several thousand lines removed. **No public API changes.**
- **Tier 0/1/2 property-accessor taxonomy** — Documented in `CLAUDE.md` and `docs/REGISTRATION.md` so contributors pick the simplest registration tier per component.
- **Pre-commit hook** — Now auto-regenerates the Angular adapter when `src/` changes.

### Fixed

- **x-combobox / x-multi-combobox** — Render pipeline now applies the model-change guard, preventing infinite `attributeChangedCallback` recursion.
- **x-kinetic-typography** — SVG path `d` attribute is now sanitized via `mu/sanitize-svg-path-d`.
- **x-i18n-provider** — Internal state encapsulated behind the public API.
- **x-kinetic-canvas docs** — CSS custom property defaults synced with implementation.
- **x-icon** — Property test now forwards `:default` in `install-properties!` for string props.

## [2.7.0] - 2026-05-02

### Changed

- **Component factory** — All 91 components now register via `component/register!`, eliminating manual `element-class` boilerplate. Shared utility modules (`baredom.utils.dom`, `baredom.utils.model`, `baredom.utils.component`) drive lifecycle wiring, property accessors, event dispatch, and attribute helpers. Net reduction of ~2 285 lines.

### Fixed

- **x-command-palette** — Search field now correctly receives focus when the palette opens.
- **x-package.json** — Fixed export ordering and regenerated type declarations.
- **Test runner** — Fixed `classList` TypeError caused by `dom_test` cleanup removing the `cljs-test-display` root element. Suppressed x-image missing-alt warnings in test fixtures.

## [2.6.0] - 2026-04-30

### Added

- **Cancelable change-request events** — Seven input components now fire a cancelable `change-request` event before applying user-initiated value changes. Call `preventDefault()` to block the update (enables controlled component patterns in framework adapters). Components: x-slider, x-text-area, x-select, x-combobox, x-currency-field, x-tabs, x-pagination.

### Fixed

- **x-context-menu** — Escape key and click-outside now correctly dismiss the menu. Handlers moved from the overlay layer (which has `pointer-events: none`) to document-level listeners.
- **x-button** — Fixed missing press events on mobile Safari. Added `touch-action: manipulation` and `-webkit-tap-highlight-color: transparent` to the internal button element (reset by `all: unset`).
- **x-carousel demo** — Control panel now uses correct BareDOM event names (`x-switch-change`, `x-form-field-input`, `select-change`, `press`) instead of native DOM events.
- **x-welcome-tour test** — Added missing `^js` type hint to fix Closure Advanced compilation warning.
- **x-combobox** — Fixed undefined category in demo gallery (changed from `"input"` to `"form"`).

## [2.5.0] - 2026-04-29

### Added

- **TypeScript support** — First-class `.d.ts` type declarations auto-generated from component model metadata. Per-component interfaces with typed properties, methods, and `addEventListener` overloads. Custom Elements Manifest (`custom-elements.json`) for IDE support.
- **Debug mode** — Dev-only visual debugging overlay for all BareDOM components. Activate via `?baredom-debug` URL param or `window.BAREDOM_DEBUG = true`. Shows dashed outlines, tag labels on hover, and a floating inspection panel with live attribute/property editing and structured console logging. Excluded from the production `:lib` build.

### Fixed

- **TypeScript declarations** — Fixed invalid kebab-case identifiers in generated `.d.ts` files. Property names like `max-items` and event detail keys like `press-x` are now correctly output as camelCase (`maxItems`, `pressX`). Affected components: x-breadcrumbs, x-context-menu, x-pagination, x-particle-button.

## [2.4.1] - 2026-04-28

### Fixed

- **x-welcome-tour** — Fixed console error caused by `feGaussianBlur` `stdDeviation` attribute receiving a CSS `var()` value instead of a number. SVG filter attributes don't support CSS custom properties.

## [2.4.0] - 2026-04-27

### Added

- **x-welcome-tour** — Guided product tour component with spotlight backdrop (SVG mask cutout + glow ring), anchored popover, and configurable connectors (`arrow`, `line`, `curve`, `none`). Compound pattern with `x-welcome-tour-step` child elements for declarative step definitions. 8-placement positioning with automatic flip, smooth step transitions, focus trap, keyboard navigation, and full x-theme integration.
- **x-file-upload** — Drag-and-drop file upload component with click-to-browse fallback, file type filtering via `accept` attribute, size limits, and multi-file support.
- **SRI integrity.json** — Build now generates `dist/integrity.json` containing Subresource Integrity hashes for all ESM output files.

### Fixed

- **x-file-upload** — `matches-accept?` now returns a boolean instead of nil for non-matching types.

## [2.3.2] - 2026-04-23

### Fixed

- **Demo gallery** — Fixed scroll issue on iOS Safari and Android landscape. Replaced app-shell layout (`height:100%` + `overflow:hidden` on body) with natural document scrolling using `min-height:100dvh`.

## [2.3.1] - 2026-04-23

### Added

- **x-popover** — New `portal` boolean attribute that renders the panel in a document-level overlay layer, escaping CSS stacking contexts (sticky navbars with `backdrop-filter`, modals, etc.). Content nodes are moved (not cloned) to preserve event listeners and returned to the host on close.
- **baredom.utils.overlay** — New shared utility module for document-level overlay root management (`ensure-overlay-root!`, `make-layer!`, `remove-layer!`).

### Changed

- **x-context-menu** — Refactored to use shared `baredom.utils.overlay` module (no behavior change).

## [2.3.0] - 2026-04-22

### Added

- **x-tooltip** — Lightweight non-interactive overlay showing supplementary text on hover/focus. Supports 4 placements (top, bottom, left, right), configurable show delay, rich content via named slot, and full keyboard/screen-reader accessibility (role=tooltip, aria-describedby, Escape to dismiss, instant show on focusin).
- **x-combobox** — Single-select combobox with type-ahead filtering over slot-based `<option>` children. Case-insensitive substring matching with highlighted results, keyboard navigation (Arrow Up/Down, Enter, Escape, Home, End), clear button, WAI-ARIA combobox pattern (role=combobox, aria-activedescendant, role=listbox/option).
- **x-skeleton-group** — Container that wraps `x-skeleton` children to synchronize animation timing and generate common skeleton layouts from a `preset` attribute (card, list-item, paragraph, table-row, profile). Uses inherited `--x-skeleton-delay` CSS custom property for phase alignment.

### Changed

- **x-skeleton** — Added `animation-delay: var(--x-skeleton-delay, 0s)` to pulse and wave CSS rules (non-breaking; enables synchronization when used inside `x-skeleton-group`).

## [2.2.0] - 2026-04-15

### Added

- **x-image** — Stateless image component that wraps a native `<img>` in shadow DOM with a skeleton shimmer placeholder, fade-in on load, optional `ratio` attribute to reserve space and prevent layout shift, a default or slotted error fallback, and enforced `alt` hygiene (decorative mode available). Attributes: `src`, `alt`, `decorative`, `ratio`, `fit`, `position`, `loading`. Events: `x-image-load`, `x-image-error`. Read-only properties: `naturalWidth`, `naturalHeight`, `state`.
- **x-icon** — Stateless slot-based wrapper for SVG icons. Handles sizing (`sm`/`md`/`lg`/`xl` tokens or a numeric pixel value), theme-aware colour via `currentColor` inheritance or the `color` attribute (`inherit`/`primary`/`secondary`/`tertiary`/`success`/`warning`/`danger`/`muted`), and accessibility (decorative by default; `label` attribute flips on `role="img"` + `aria-label`). Consumers provide the `<svg>` in the default slot using `fill="currentColor"` / `stroke="currentColor"` for colour propagation.

### Changed

- **x-navbar** default look is now a site header instead of a rounded card. `--x-navbar-radius` defaults to `0` (was `1rem`) and the full four-sided border has been replaced with a single 1px bottom separator. Variant overrides (`subtle`, `inverted`, `transparent`) updated to target `border-bottom-color` accordingly. The rounded card look remains reachable via `style="--x-navbar-radius: 1rem"` — no public API changes. Visual regression for consumers who relied on the previous default; the escape hatch is one line.

## [2.1.1] - 2026-04-13

### Fixed

- x-table-cell: cell `border-bottom` lines now connect cleanly across columns with uneven intrinsic content heights (e.g. a header cell with a sort button next to a plain label cell). Inner `[part=cell]` wrapper now fills the host's row-stretched height so the border sits at the row's bottom edge.

### Demos (no impact on published library)

- Demo gallery now dogfoods the library: every `<button>`, `<select>`, `<input type="text|number|...">`, `<input type="checkbox">`, status pill, tag chip, and `<hr>` in the ~80 demo pages has been swapped to the matching library component (`<x-button>`, `<x-select>`, `<x-form-field>`, `<x-switch>` / `<x-checkbox>`, `<x-badge>`, `<x-chip>`, `<x-divider>`).
- Section eyebrow `<h2>` and page title `<h1>` headings swapped to `<x-typography variant="overline"|"h1">`.
- Shared compatibility shims in `demo/demo-theme.js` re-dispatch `select-change` / `x-switch-change` / `x-checkbox-change` / `x-form-field-input` / `x-form-field-change` as native `change` / `input` events so existing demo handlers keep working.
- `demo/demo-responsive.css` carries shared sizing/layout overrides for the dogfooded controls so they match each demo's local visual rhythm.
- Several latent demo bugs surfaced and fixed during the sweep: x-soft-body unreadable text in dark mode, x-morph-stack `button[data-stack]` selector silently skipping x-button, x-progress-circle slider max not syncing with the max field, theme-picker bar z-index covering overlay components, and a handful of demos with leftover `style=""` / `class=""` from their native-element days.

## [2.1.0] - 2026-04-13

### Added

- **x-carousel** — Themeable, accessible carousel with swipe/drag navigation, arrow buttons, dot indicators, autoplay, keyboard interaction, slide/fade transitions, and horizontal/vertical orientation
- **x-color-picker** — Color picker with 2D saturation/brightness area, hue strip, optional alpha channel, preset swatches, eyedropper support, and clipboard copy; supports inline and popover display modes
- **x-particle-button** — Button that emits and reabsorbs visual fragments on interaction, combining a premium base, pointer-tracked specular highlight, and a canvas particle system (modes: subtle, spark, ember, disperse)
- x-theme: design tokens expanded from 33 to 50 (adds z-index, opacity, and additional colour/spacing/shape tokens for overlay stacking and disabled states)
- Shared DOM and model utilities (`baredom.utils.dom`, `baredom.utils.model`) so components no longer reimplement attribute parsing, instance-field access, and security sanitisers
- CI quality checks and bundle-size documentation

### Changed

- `public/index.html` component gallery now consumes x-theme design tokens

### Fixed

- CSS injection and XSS vulnerabilities in input sanitization
- x-liquid-fill: `disabled` attribute now correctly suspends animation and interaction
- x-divider: pattern rendering regression
- Mobile viewport units (`100dvh` / `100%`) applied where `100vh` / `100vw` were causing overflow
- Missing demo page for audit-flagged component
- Flaky async test timeouts
- Closure Advanced Compilation safety regression (bab76f9)

## [2.0.1] - 2026-04-10

### Fixed

- Release pipeline: version references in build.clj, deps.edn, and pom.xml now match package.json (were stuck at 1.4.0/1.2.0, causing Clojars deploy to fail for 2.0.0)
- CI workflow: npm publish step now uses continue-on-error to avoid blocking Clojars deploy

## [2.0.0] - 2026-04-10

### Added

- **x-theme** — Centralised theming component with 33 design tokens, 8 built-in presets (default, ocean, forest, sunset, neo-brutalist, aurora, mono-ai, warm-mineral), and `registerPreset()` JS API for custom themes with partial preset merging
- Theme preset picker on all 79 demo pages (desktop buttons + mobile dropdown, persists via localStorage)
- Theming section in README.md
- Theming guidelines in CLAUDE.md

### Changed

- All 80 components now consume x-theme design tokens via `var(--x-token, fallback)` pattern — fully backwards compatible, components render identically without an `<x-theme>` wrapper
- Overlay components (modal, drawer, menu, popover, dropdown, toast, command-palette, context-menu, cancel-dialogue) use opaque `--x-color-bg` for backgrounds
- `demo-responsive.css` re-maps demo variables to theme tokens when inside `<x-theme>`
- Demo pages use theme-aware CSS variables instead of hardcoded colours

### Fixed

- x-liquid-dock: `.color` property returns `null` when no `color` attribute is set, allowing the CSS theme token to take effect (visual default unchanged)

## [1.4.0] - 2026-04-09

### Added

Six new components:

- **Effects** — x-liquid-glass, x-soft-body, x-liquid-dock, x-liquid-fill
- **Typography** — x-kinetic-font
- **Layout** — x-morph-stack (with variant presets and multi-element goo merge demo)

### Changed

- Demo index: style family filtering
- Demo index components sorted alphabetically
- README updates

## [1.3.0] - 2026-04-07

### Added

Six new components:

- **Effects** — x-splash, x-metaball-cursor, x-neural-glow, x-organic-progress
- **Layout** — x-bento-grid, x-bento-item

### Changed

- **x-neural-glow** — automatic light/dark background adaptation via shader luminance detection

## [1.2.0] - 2026-04-05

### Added

- **Scroll** — x-scroll-stack (card-stack with scroll-driven align support)
- **Documentation** — JavaScript Developer Guide (`docs/javascript-guide.md`) for npm/ESM setup, event handling, theming, and framework integration

### Changed

- Cleaned up x-button.md for JavaScript developer readability
- Added JavaScript guide link to README Installation section

## [1.1.0] - 2026-04-05

### Added

Nine new components:

- **Effects** — x-ripple-effect, x-gaussian-blur
- **Scroll** — x-scroll-parallax, x-scroll-timeline, x-scroll-story
- **Typography** — x-typography, x-kinetic-typography
- **Decorative** — x-organic-divider, x-organic-shape

## [1.0.0] - 2026-04-04

Stable release — promoted from 1.0.0-rc.4 with no code changes.

### Summary

- All 54 components audited, tested, and stable
- Full API consistency, accessibility, theming, and Closure Advanced compilation safety
- Published to NPM, Clojars, and GitHub Releases

## [1.0.0-rc.4] - 2026-03-31

Release candidate — all components audited and stable.

### Changed

- All 54 components reviewed and updated for API consistency, accessibility, theming, Closure Advanced compilation safety, and test coverage
- Accompanying documentation and demo files updated for all components
- Namespace renamed from `app` to `baredom` to match published package name

### Components

- **Layout** — x-container, x-grid, x-spacer, x-divider, x-sidebar, x-navbar
- **Typography & Display** — x-badge, x-chip, x-stat, x-avatar, x-avatar-group, x-skeleton
- **Feedback** — x-alert, x-spinner, x-progress, x-progress-circle, x-toast, x-toaster
- **Overlays** — x-modal, x-drawer, x-popover, x-context-menu, x-command-palette, x-cancel-dialogue
- **Navigation** — x-breadcrumbs, x-pagination, x-tab, x-tabs, x-menu, x-menu-item, x-stepper
- **Forms** — x-button, x-checkbox, x-radio, x-switch, x-select, x-slider, x-text-area, x-form, x-form-field, x-fieldset, x-search-field, x-currency-field, x-date-picker
- **Content** — x-card, x-collapse, x-chart, x-copy, x-file-download, x-dropdown
- **Data** — x-table, x-table-row, x-table-cell
- **Timeline** — x-timeline, x-timeline-item
- **Notifications** — x-notification-center

## [0.1.0-alpha] - 2026-03-25

Initial alpha release of BareDome — 54 native Web Components built with ClojureScript and compiled to zero-dependency ESM modules.

### Components

- **Layout** — x-container, x-grid, x-spacer, x-divider, x-sidebar, x-navbar
- **Typography & Display** — x-badge, x-chip, x-stat, x-avatar, x-avatar-group, x-skeleton
- **Feedback** — x-alert, x-spinner, x-progress, x-progress-circle, x-toast, x-toaster
- **Overlays** — x-modal, x-drawer, x-popover, x-context-menu, x-command-palette, x-cancel-dialogue
- **Navigation** — x-breadcrumbs, x-pagination, x-tab, x-tabs, x-menu, x-menu-item, x-stepper
- **Forms** — x-button, x-checkbox, x-radio, x-switch, x-select, x-slider, x-text-area, x-form, x-form-field, x-fieldset, x-search-field, x-currency-field, x-date-picker
- **Content** — x-card, x-collapse, x-chart, x-copy, x-file-download, x-dropdown
- **Data** — x-table, x-table-row, x-table-cell
- **Timeline** — x-timeline, x-timeline-item

### Architecture

- Stateless rendering: `DOM = f(attributes, properties)`
- Zero framework runtime — no React, Lit, Vue, or virtual DOM
- Open shadow DOM with CSS custom properties for theming
- Light/dark mode via `@media (prefers-color-scheme: dark)`
- Reduced motion via `@media (prefers-reduced-motion: reduce)`
- Google Closure Advanced compiled — minified, dead-code-eliminated
- Per-component ESM modules — fully tree-shakeable
- Accessible by default (ARIA roles, keyboard navigation, focus management)
