# @vanelsas/baredom-angular

Angular 17+ standalone directives for [BareDOM](https://github.com/avanelsas/baredom) — auto-generated from component metadata.

Provides typed inputs, event outputs, and `ControlValueAccessor` integration for all 90+ BareDOM web components.

## Installation

```bash
npm install @vanelsas/baredom-angular @vanelsas/baredom
```

## Usage

Add `CUSTOM_ELEMENTS_SCHEMA` and import the directives you need:

```typescript
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BaredomButton, BaredomAlert, BaredomTheme } from '@vanelsas/baredom-angular';

@Component({
  selector: 'app-root',
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [BaredomButton, BaredomAlert, BaredomTheme],
  template: `
    <x-theme>
      <x-button
        [disabled]="false"
        (press)="onPress($event)"
      >
        Click me
      </x-button>

      <x-alert
        type="success"
        text="Operation completed"
        [dismissible]="true"
        (dismiss)="onDismiss($event)"
      ></x-alert>
    </x-theme>
  `,
})
export class AppComponent {
  onPress(e: CustomEvent<{ source: string }>) {
    console.log('Pressed!', e.detail.source);
  }
  onDismiss(e: CustomEvent) {
    console.log('Dismissed');
  }
}
```

## Features

- **Standalone directives** — import only what you need, no NgModule required
- **Typed inputs** — all component properties available as `@Input()` bindings
- **Event outputs** — custom events mapped to `@Output()` emitters (e.g. `(press)`, `(dismiss)`)
- **Angular forms** — `ControlValueAccessor` directives for reactive forms and `ngModel`
- **Tree-shakable** — import individual directives to keep bundle size small
- **Zone-aware** — all events trigger Angular change detection via `NgZone.run()`

## Event naming

BareDOM event names are automatically converted to Angular output names:

| DOM event | Angular output |
|-----------|---------------|
| `press` | `(press)` |
| `x-alert-dismiss` | `(dismiss)` |
| `x-switch-change` | `(change)` |
| `value-change` | `(valueChange)` |
| `hover-start` | `(hoverStart)` |

The component's tag-name prefix (e.g. `x-alert-`) is stripped, and the remainder is camelCased.

When an output name collides with an input name, the output is suffixed with `Event` (e.g. `(openEvent)`).

## Angular forms integration

10 form components include `ControlValueAccessor` directives that work with both reactive forms and template-driven forms.

```typescript
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import {
  BaredomCheckbox, BaredomCheckboxCva,
  BaredomSlider, BaredomSliderCva,
} from '@vanelsas/baredom-angular';

@Component({
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    ReactiveFormsModule,
    BaredomCheckbox, BaredomCheckboxCva,
    BaredomSlider, BaredomSliderCva,
  ],
  template: `
    <x-checkbox [formControl]="accepted">I agree</x-checkbox>
    <x-slider [formControl]="volume" min="0" max="100"></x-slider>
  `,
})
export class FormComponent {
  accepted = new FormControl(false);
  volume = new FormControl('50');
}
```

**CVA components:** BaredomCheckboxCva, BaredomSwitchCva, BaredomRadioCva (`boolean`), BaredomSliderCva, BaredomTextAreaCva, BaredomSelectCva, BaredomComboboxCva, BaredomCurrencyFieldCva, BaredomTabsCva (`string`), BaredomPaginationCva (`number`).

The CVA directive activates automatically when `formControlName`, `formControl`, or `ngModel` is present on the element.

## Methods

Components with public methods expose them on the directive. Access via `ViewChild`:

```typescript
import { Component, ViewChild, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BaredomModal } from '@vanelsas/baredom-angular';

@Component({
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [BaredomModal],
  template: `
    <button (click)="modal.show()">Open</button>
    <x-modal #modal (dismiss)="onDismiss()">
      Modal content
    </x-modal>
  `,
})
export class DialogComponent {
  @ViewChild(BaredomModal) modal!: BaredomModal;

  onDismiss() {
    console.log('Modal dismissed');
  }
}
```

## Theming

Wrap your app with `<x-theme>` to enable BareDOM's design tokens:

```html
<x-theme preset="aurora">
  <!-- All BareDOM components inherit theme tokens -->
</x-theme>
```

## Requirements

- Angular 17+
- @vanelsas/baredom 2.6.0+

## Auto-generated

The directives are auto-generated from BareDOM's component model metadata using `bb scripts/generate_angular.bb`. Adding a new component to BareDOM automatically produces its Angular directive.

## License

MIT
