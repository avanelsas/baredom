// Type-check smoke test — verifies generated .d.ts declarations compile.
// Run: npm run test:types

import type { XButton } from "@vanelsas/baredom/x-button";
import type { XAlert } from "@vanelsas/baredom/x-alert";
import type { XModal } from "@vanelsas/baredom/x-modal";
import type { XWelcomeTour, XWelcomeTourStep } from "@vanelsas/baredom/x-welcome-tour";
import type { XTheme } from "@vanelsas/baredom/x-theme";
import type { XSlider } from "@vanelsas/baredom/x-slider";
import type {
  XTraceHistory,
  TraceRecord,
  TraceEnvelope,
  TraceImport,
  TraceSession,
} from "@vanelsas/baredom/x-trace-history";

// ── HTMLElementTagNameMap augmentation ───────────────────────────────────────
const btn: XButton = document.createElement("x-button");
const alert: XAlert = document.createElement("x-alert");

// ── Property types ──────────────────────────────────────────────────────────
const _disabled: boolean = btn.disabled;
const _loading: boolean = btn.loading;
const _alertText: string = alert.text;
const _alertType: string = alert.type;

// ── Readonly property ───────────────────────────────────────────────────────
const tour = document.createElement("x-welcome-tour") as unknown as XWelcomeTour;
const _total: number = tour.totalSteps;

// ── Methods ─────────────────────────────────────────────────────────────────
const modal = document.createElement("x-modal") as unknown as XModal;
modal.show();
modal.hide();
modal.toggle();

tour.start();
tour.next();
tour.prev();
tour.goTo(2);
tour.complete();
tour.skip();

// ── Typed events ────────────────────────────────────────────────────────────
alert.addEventListener("x-alert-dismiss", (e) => {
  const type: string = e.detail.type;
  const reason: string = e.detail.reason;
  const text: string = e.detail.text;
  void [type, reason, text];
});

btn.addEventListener("press", (e) => {
  const source: string = e.detail.source;
  void source;
});

// ── querySelector type narrowing ────────────────────────────────────────────
declare const el1: HTMLElementTagNameMap["x-button"];
const _b: boolean = el1.disabled;

declare const el2: HTMLElementTagNameMap["x-welcome-tour-step"];
const _target: string = el2.target;

// ── x-trace-history: dock element + JS API surface ─────────────────────────
const dock: XTraceHistory = document.createElement("x-trace-history");
void dock;

const _api = window.BareDOM?.traceHistory;
if (_api) {
  const records: TraceRecord[] = _api.records();
  const envelope: TraceEnvelope = _api.export();
  const sessions: TraceSession[] = _api.sessions();
  const imports: TraceImport[] = _api.imports();
  _api.pause();
  _api.resume();
  _api.clear();
  const sid: number = _api.startSession();
  _api.stopSession();
  const importId: number | null = _api.import(envelope, "test");

  // Discriminated union: switch on `type` for exhaustive handling.
  for (const r of records) {
    switch (r.type) {
      case "event/dispatch":
      case "event/dispatch-cancelable":
      case "event/dispatch-document": {
        const en: string = r.eventName;
        const cancelable: boolean = r.cancelable;
        void [en, cancelable];
        break;
      }
      case "state/instance-field-set": {
        const f: string = r.field;
        void f;
        break;
      }
      case "dom/attribute-set": {
        const a: string = r.attribute;
        void a;
        break;
      }
      case "dom/attribute-removed": {
        const a: string = r.attribute;
        void a;
        break;
      }
      case "lifecycle/connected":
      case "lifecycle/disconnected":
        break;
      case "lifecycle/attribute-changed": {
        const ov: string | null = r.oldValue;
        const nv: string | null = r.newValue;
        void [ov, nv];
        break;
      }
    }
  }
  void [envelope, sessions, imports, sid, importId];
}

// ── querySelector type narrowing for the dock ───────────────────────────────
declare const dockEl: HTMLElementTagNameMap["x-trace-history"];
void dockEl;

// ── Suppress unused warnings ────────────────────────────────────────────────
void [_disabled, _loading, _alertText, _alertType, _total, _b, _target];
