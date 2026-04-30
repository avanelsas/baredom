# Development

This guide covers setting up the development environment, using the component demo, debug mode, and building from source.

---

## Building from Source

BareDOM is authored in ClojureScript and compiled with [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html).

```bash
# Install dependencies
npm install

# Start development server with hot reload (http://localhost:8000)
npx shadow-cljs watch app

# Run browser-based tests (http://localhost:8021)
npx shadow-cljs watch test

# Build production ESM library to dist/
npm run build
```

---

## Component Demo

BareDOM ships with a built-in demo that lets you browse and interact with every component in isolation. It is intended for developer convenience when working on the library itself.

```bash
npm install
npx shadow-cljs watch app
```

Then open `http://localhost:8000`. The dev server serves `public/index.html` and hot-reloads on every source change. Each component is demonstrated in its own section with controls for toggling attributes, properties, and variants.

---

## Debug Mode

BareDOM includes a visual debug overlay for development. It highlights every component on the page, shows tag names on hover, and lets you inspect and edit live state — no browser extension required.

**Activate** by adding `?baredom-debug` to any dev URL:

```
http://localhost:8000?baredom-debug
```

Or toggle from the browser console at any time:

```js
window.BAREDOM_DEBUG = true
```

When active, debug mode provides:

- **Dashed blue outlines** around every BareDOM component
- **Tag labels** that appear on hover (top-right corner)
- **Inspection panel** — click a label to see current attributes, properties, and computed state
- **Inline editing** — toggle boolean attributes with a switch, edit string/number values and press Enter to apply
- **Console logging** — structured `console.group` output on every attribute change, lifecycle events at `console.debug` level

Changes made through the debug panel are live but ephemeral — they modify the DOM directly and do not survive a page refresh.

Debug mode is dev-only and is excluded from the production ESM build (`npm run build`).

---

## Linting

```bash
clj-kondo --lint src test
```

Code must have zero lint warnings and errors before pushing.

---

## Project Structure

```
src/baredom/
  components/<name>/     Component source (model.cljs, <name>.cljs)
  exports/<name>.cljs    ESM entry point per component
  utils/                 Shared utilities (dom.cljs, model.cljs)
  dev/                   Dev-only code (hot_reload.cljs, x_debug/)
  core.cljs              Dev build entry point
docs/                    Per-component documentation
demo/                    Demo HTML pages
test/                    Browser-based tests
scripts/                 Build and code generation scripts
```

See [CLAUDE.md](../CLAUDE.md) for the full architecture reference, coding conventions, and contribution guidelines.
