# Installation

BareDOM can be consumed three ways: as a **ClojureScript source dependency** (Clojars), as **standalone ES module files** (no build tool required), or as an **npm package**.

---

## ClojureScript via Clojars

Add BareDOM to your `deps.edn`:

```clojure
{:deps {com.github.avanelsas/baredom {:mvn/version "2.5.0"}}}
```

Or in your `shadow-cljs.edn` dependencies:

```clojure
:dependencies [[com.github.avanelsas/baredom "2.5.0"]]
```

Then require component namespaces directly and call their `init!` function once at startup:

```clojure
(ns my-app.core
  (:require
   [baredom.exports.x-button  :as x-button]
   [baredom.exports.x-alert   :as x-alert]
   [baredom.exports.x-toaster :as x-toaster]
   [baredom.exports.x-toast   :as x-toast]))

(defn- register-components! []
  (x-button/init)
  (x-alert/init)
  (x-toaster/init)
  (x-toast/init))
```

Call `register-components!` once in your `init!` entry point. Registration is idempotent — calling `init` on an already-registered element is a no-op.

---

## Vanilla HTML/JS via ES modules

No build tool, no npm, no ClojureScript required. Copy the `dist/` folder (from a release or after running `npm run build`) to your web server and load components directly with `<script type="module">`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>BareDOM Example</title>
</head>
<body>
  <x-button variant="primary">Click me</x-button>
  <x-alert type="success" text="It works!"></x-alert>

  <script type="module">
    import { init as initButton } from './dist/x-button.js';
    import { init as initAlert }  from './dist/x-alert.js';

    initButton();
    initAlert();
  </script>
</body>
</html>
```

Each component is a separate ES module. Import only the components you use — the browser loads only those files plus the shared `base.js` runtime.

---

## npm

Add the npm package to your `package.json`:

```json
{
  "dependencies": {
    "@vanelsas/baredom": "^2.5.0"
  }
}
```

Then `npm install`. shadow-cljs resolves npm packages automatically via `node_modules`. From ClojureScript:

```clojure
(ns my-app.core
  (:require
   ["@vanelsas/baredom/x-button"  :as x-button]
   ["@vanelsas/baredom/x-alert"   :as x-alert]
   ["@vanelsas/baredom/x-toaster" :as x-toaster]
   ["@vanelsas/baredom/x-toast"   :as x-toast]))

(defn- register-components! []
  (.init x-button)
  (.init x-alert)
  (.init x-toaster)
  (.init x-toast))
```

Call `register-components!` once in your `init!` entry point. Registration is idempotent — calling `.init` on an already-registered element is a no-op.

---

For framework-specific setup, see:
- [JavaScript Developer Guide](./javascript-guide.md)
- [ClojureScript Guide](./clojurescript-guide.md)
