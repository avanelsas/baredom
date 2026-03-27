# BareDOM HTML/JS Demo

A demo that renders BareDOM web components using nothing but plain HTML and JavaScript — no ClojureScript, no build step, no bundler, no framework.

This demo is visually identical to `bare-demo/` but consumes the pre-built ES modules from the `dist/` folder directly via `<script type="module">`. It shows the minimal amount of code needed to drive BareDOM components from vanilla JS.

## Running the demo

The demo must be served over HTTP — browsers block ES module imports on `file://` URLs. Run a static server from the **project root** (not from inside `bare-html/`) so that the relative `../dist/` imports resolve correctly:

```bash
python3 -m http.server 8000
```

Then open [http://localhost:8000/bare-html/demo.html](http://localhost:8000/bare-html/demo.html).

Any static file server works — `npx serve .`, `npx http-server .`, etc.

## How it works

### Registering components

Each component is a separate ES module. Import the `init` function and call it once to register the custom element with the browser:

```html
<script type="module">
  import { init as initNavbar }    from '../dist/x-navbar.js';
  import { init as initSidebar }   from '../dist/x-sidebar.js';
  import { init as initButton }    from '../dist/x-button.js';
  import { init as initModal }     from '../dist/x-modal.js';
  import { init as initContainer } from '../dist/x-container.js';
  import { init as initCard }      from '../dist/x-card.js';

  initNavbar();
  initSidebar();
  initButton();
  initModal();
  initContainer();
  initCard();
</script>
```

Registration is idempotent — calling `init()` on an already-registered element is a no-op. Only the components you import are loaded.

### Declarative HTML markup

Once registered, components are used as standard HTML elements. Attributes are set directly in the source:

```html
<x-navbar label="BareDOM Demo" elevated>
  <picture slot="brand">
    <img src="../bare-demo/public/assets/baredom_lightmode.svg" height="32" alt="BareDOM">
  </picture>
  <div slot="actions">
    <x-button id="menu-btn" variant="ghost" size="sm">Menu</x-button>
  </div>
</x-navbar>

<x-sidebar id="sidebar" placement="left" variant="modal" label="Navigation">
  <!-- nav items -->
</x-sidebar>

<x-modal id="about-modal" label="About BareDOM" size="md">
  <span slot="header">About BareDOM</span>
  <!-- body and footer slots -->
</x-modal>
```

Boolean attributes follow the HTML convention: presence means `true`, absence means `false`.

### State and rendering

All UI state lives in a plain JS object. A single `render()` function reads the state and reconciles attributes:

```js
let sidebarOpen = false;
let modalOpen   = false;
let activeNav   = 'home';

function render() {
  if (sidebarOpen) { sidebar.setAttribute('open', ''); }
  else             { sidebar.removeAttribute('open'); }

  if (modalOpen) { modal.setAttribute('open', ''); }
  else           { modal.removeAttribute('open'); }

  navItems.forEach(btn => {
    const active = btn.dataset.nav === activeNav;
    btn.className = active ? 'sidebar-item sidebar-item--active' : 'sidebar-item';
  });
}
```

`render()` is called once after initial setup and again whenever state changes. There is no diffing or reactivity system — just direct attribute manipulation.

## Handling events

### x-button — `press`

`x-button` fires a `press` CustomEvent when activated (pointer or keyboard). Use `press` rather than `click` to correctly reflect the component's interaction model:

```js
menuBtn.addEventListener('press', () => {
  sidebarOpen = !sidebarOpen;
  render();
});
```

### x-sidebar — `toggle` and `dismiss`

`x-sidebar` fires `toggle` whenever its effective open state changes. The `detail.open` boolean reflects the new state. In modal variant, `dismiss` fires when the user presses Escape or clicks the backdrop:

```js
sidebar.addEventListener('toggle', e => {
  sidebarOpen = e.detail.open;
  render();
});
```

### x-modal — `x-modal-dismiss`

`x-modal` fires `x-modal-dismiss` when the user closes the modal via Escape or a backdrop click:

```js
modal.addEventListener('x-modal-dismiss', () => {
  modalOpen = false;
  render();
});
```

### Plain `<button>` nav items

The sidebar navigation items are standard HTML `<button>` elements (not BareDOM components), so they use native `click`:

```js
navItems.forEach(btn => {
  btn.addEventListener('click', () => {
    activeNav   = btn.dataset.nav;
    sidebarOpen = false;
    render();
  });
});
```

## Event log

The bottom-right panel is an `x-card` built imperatively with `document.createElement`. It listens for `press`, `toggle`, and `x-modal-dismiss` on the `document` (these events bubble and are composed), and also captures native `click` events on any element whose tag name starts with `x-`:

```js
['press', 'toggle', 'x-modal-dismiss'].forEach(name => {
  document.addEventListener(name, logEvent);
});

document.addEventListener('click', e => {
  if (e.target.tagName.toLowerCase().startsWith('x-')) {
    logEvent(e);
  }
});
```

Each log entry records a timestamp, event type, and source tag name. Entries are prepended (newest on top) and capped at 50.

## Comparison: bare-demo vs bare-html

| Concern | bare-demo | bare-html |
|---|---|---|
| Language | ClojureScript | JavaScript |
| Renderer | Custom ~55-line hiccup→DOM | Declarative HTML + `setAttribute` |
| State | `cljs.core/atom` | Plain JS `let` variables |
| Re-render trigger | `add-watch` → full rebuild | Manual `render()` call after each event |
| Button events | `:on-click` → `addEventListener("click", …)` | `addEventListener("press", …)` |
| Custom events | Same `addEventListener` path | Same `addEventListener` path |
| Build step | `npx shadow-cljs watch bare-demo` | None — serve static files |
| Dependencies | shadow-cljs, ClojureScript | None |
| Port / URL | `http://localhost:8001` | `http://localhost:8000/bare-html/demo.html` |

## Project structure

```
bare-html/
└── demo.html    # Everything in one file: CSS, HTML markup, JS wiring, event log
```

All CSS, HTML, and JavaScript live in a single self-contained file. There is no `package.json`, no `node_modules`, and no build output to manage.
