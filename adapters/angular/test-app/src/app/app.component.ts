import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule, FormControl } from '@angular/forms';
import {
  BaredomButton,
  BaredomAlert,
  BaredomCheckbox,
  BaredomCheckboxCva,
  BaredomSwitch,
  BaredomSwitchCva,
  BaredomSlider,
  BaredomSliderCva,
  BaredomBadge,
  BaredomSpinner,
  BaredomDivider,
  BaredomTheme,
} from '@vanelsas/baredom-angular';

@Component({
  selector: 'app-root',
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    ReactiveFormsModule,
    FormsModule,
    BaredomButton,
    BaredomAlert,
    BaredomCheckbox,
    BaredomCheckboxCva,
    BaredomSwitch,
    BaredomSwitchCva,
    BaredomSlider,
    BaredomSliderCva,
    BaredomBadge,
    BaredomSpinner,
    BaredomDivider,
    BaredomTheme,
  ],
  template: `
    <x-theme [attr.preset]="preset">
      <div style="max-width: 640px; margin: 2rem auto; font-family: var(--x-font-family, system-ui); color: var(--x-color-text, inherit)">
        <h1>BareDOM Angular Adapter Test</h1>

        <!-- Test 1: Button with events and props -->
        <section>
          <h2>1. BaredomButton &mdash; props, events</h2>
          <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap">
            <x-button
              [disabled]="disabled"
              [loading]="loading"
              (press)="addLog('Button pressed (source: ' + $any($event).detail.source + ')')"
              (hoverStart)="addLog('Button hover start')"
              (hoverEnd)="addLog('Button hover end')"
            >
              Click me
            </x-button>
            <button (click)="disabled = !disabled">Toggle disabled ({{ disabled ? 'on' : 'off' }})</button>
            <button (click)="loading = !loading">Toggle loading ({{ loading ? 'on' : 'off' }})</button>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 2: Alert with dismiss event -->
        <section>
          <h2>2. BaredomAlert &mdash; dismiss event</h2>
          @if (alertVisible) {
            <x-alert
              type="info"
              text="This is a dismissible alert from Angular"
              [dismissible]="true"
              (dismiss)="addLog('Alert dismissed (reason: ' + $any($event).detail.reason + ')'); alertVisible = false"
            ></x-alert>
          } @else {
            <div><em>Alert dismissed.</em> <button (click)="alertVisible = true">Show again</button></div>
          }
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 3: Controlled checkbox with reactive forms -->
        <section>
          <h2>3. BaredomCheckbox &mdash; reactive forms (CVA)</h2>
          <div style="display: flex; gap: 12px; align-items: center">
            <x-checkbox [formControl]="checkboxControl">I agree</x-checkbox>
            <span>FormControl value: {{ checkboxControl.value }}</span>
            <button (click)="checkboxControl.setValue(!checkboxControl.value)">Toggle from Angular</button>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 4: Controlled switch with reactive forms -->
        <section>
          <h2>4. BaredomSwitch &mdash; reactive forms (CVA)</h2>
          <div style="display: flex; gap: 12px; align-items: center">
            <x-switch [formControl]="switchControl"></x-switch>
            <span>FormControl value: {{ switchControl.value }}</span>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 5: Controlled slider with reactive forms -->
        <section>
          <h2>5. BaredomSlider &mdash; reactive forms (CVA)</h2>
          <div style="display: flex; gap: 12px; align-items: center">
            <x-slider [formControl]="sliderControl" min="0" max="100" [showValue]="true" style="flex: 1"></x-slider>
            <span style="min-width: 40px">{{ sliderControl.value }}</span>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 6: Template-driven switch (ngModel) -->
        <section>
          <h2>6. BaredomSwitch &mdash; template-driven (ngModel)</h2>
          <div style="display: flex; gap: 12px; align-items: center">
            <x-switch [(ngModel)]="uncontrolledSwitch"></x-switch>
            <span>ngModel value: {{ uncontrolledSwitch }}</span>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 7: Static components -->
        <section>
          <h2>7. Static components &mdash; badge, spinner</h2>
          <div style="display: flex; gap: 12px; align-items: center">
            <x-badge text="Angular" variant="primary"></x-badge>
            <x-badge text="Adapter" variant="secondary"></x-badge>
            <x-spinner size="sm"></x-spinner>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Test 8: Theme switching -->
        <section>
          <h2>8. Theme switching</h2>
          <div style="display: flex; gap: 8px; align-items: center; flex-wrap: wrap">
            <button (click)="preset = undefined; addLog('Preset: default')">Default</button>
            <button (click)="preset = 'aurora'; addLog('Preset: aurora')">Aurora (built-in)</button>
            <span style="font-size: 14px; color: var(--x-color-text-muted, #888)">
              Active: {{ preset ?? 'default' }}
            </span>
          </div>
        </section>

        <x-divider style="margin: 1.5rem 0"></x-divider>

        <!-- Event log -->
        <section>
          <h2>Event Log</h2>
          <div style="background: var(--x-color-surface, #1e1e2e); color: var(--x-color-success, #a6e3a1); font-family: var(--x-font-family-mono, monospace); font-size: 12px; padding: 12px; border-radius: var(--x-radius-md, 8px); border: 1px solid var(--x-color-border, transparent); min-height: 120px; max-height: 300px; overflow-y: auto">
            @if (log.length === 0) {
              <span style="color: var(--x-color-text-muted, #6c7086)">Interact with components above...</span>
            } @else {
              @for (entry of log; track $index) {
                <div>{{ entry }}</div>
              }
            }
          </div>
        </section>
      </div>
    </x-theme>
  `,
})
export class AppComponent {
  log: string[] = [];
  disabled = false;
  loading = false;
  alertVisible = true;
  preset: string | undefined = undefined;

  // Reactive forms
  checkboxControl = new FormControl(false);
  switchControl = new FormControl(false);
  sliderControl = new FormControl('25');

  // Template-driven
  uncontrolledSwitch = true;

  addLog(msg: string) {
    this.log = [
      ...this.log.slice(-19),
      `${new Date().toLocaleTimeString()} — ${msg}`,
    ];
  }
}
