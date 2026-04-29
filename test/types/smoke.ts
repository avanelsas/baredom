// Type-check smoke test — verifies generated .d.ts declarations compile.
// Run: npm run test:types

import type { XButton } from "@vanelsas/baredom/x-button";
import type { XAlert } from "@vanelsas/baredom/x-alert";
import type { XModal } from "@vanelsas/baredom/x-modal";
import type { XWelcomeTour, XWelcomeTourStep } from "@vanelsas/baredom/x-welcome-tour";
import type { XTheme } from "@vanelsas/baredom/x-theme";
import type { XSlider } from "@vanelsas/baredom/x-slider";

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

// ── Suppress unused warnings ────────────────────────────────────────────────
void [_disabled, _loading, _alertText, _alertType, _total, _b, _target];
