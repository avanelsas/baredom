# TypeScript

BareDOM includes TypeScript declarations for every component. Types are auto-generated from component metadata and ship alongside the ESM files — no additional setup required.

## What you get

- **Typed element interfaces** extending `HTMLElement` with all properties and methods
- **Typed custom events** with full `detail` payload types
- **`HTMLElementTagNameMap` augmentation** so `document.querySelector('x-button')` returns `XButton`
- **[Custom Elements Manifest](https://github.com/webcomponents/custom-elements-manifest)** (`custom-elements.json`) for IDE tooling and HTML intellisense

## Usage

```typescript
import '@vanelsas/baredom/x-button';
import '@vanelsas/baredom/x-alert';

// querySelector returns typed XButton
const btn = document.querySelector('x-button')!;
btn.disabled = true;    // type-checked
btn.loading = true;     // autocomplete works

// Event listeners have typed detail payloads
btn.addEventListener('press', (e) => {
  console.log(e.detail.source);  // string — fully typed
});

// Custom events on other components
const alert = document.querySelector('x-alert')!;
alert.addEventListener('x-alert-dismiss', (e) => {
  console.log(e.detail.reason);  // string
  console.log(e.detail.type);    // string
});

// Components with methods
import '@vanelsas/baredom/x-modal';
const modal = document.querySelector('x-modal')!;
modal.show();   // typed method
modal.hide();   // typed method
```

## IDE support

The `custom-elements.json` manifest enables HTML intellisense in editors that support it. For VS Code, install the [Lit Plugin](https://marketplace.visualstudio.com/items?itemName=nicktomlin.vscode-lit-html) or the [Custom Elements Language Server](https://marketplace.visualstudio.com/items?itemName=nicktomlin.vscode-lit-html) to get attribute autocomplete and validation in HTML templates.
