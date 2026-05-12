import { Component, OnDestroy, OnInit, signal } from '@angular/core';

const POLL_INTERVAL_MS = 1000;

function readActive(): boolean {
  return typeof window !== 'undefined' && Boolean(window.BareDOM?.traceHistory);
}

function readRecordCount(): number {
  const api = window.BareDOM?.traceHistory;
  return api ? api.records().length : 0;
}

/**
 * Smoke panel for the trace-history dock when consumed from an
 * Angular app. Confirms the side-effect import in main.ts took
 * effect AFTER zone.js's own patches (recorder wraps on top of
 * zone's dispatchEvent patch, so the recorder still sees every
 * dispatch). The verifier button fires a CustomEvent so the user
 * can watch a record land in the dock without leaving the page.
 */
@Component({
  selector: 'trace-history-panel',
  standalone: true,
  template: `
    <section>
      <h2>9. Trace history — adapter smoke</h2>
      <p style="font-size: 13px; color: var(--x-color-text-muted, #888)">
        The trace-history dock is loaded via a side-effect import in
        <code>main.ts</code>. It only mounts when activated:
      </p>
      <ul style="font-size: 13px; color: var(--x-color-text-muted, #888)">
        <li>
          Open this page with <code>?baredom-trace-history</code> in
          the URL, OR
        </li>
        <li>
          Set <code>window.BAREDOM_TRACE_HISTORY = true</code> before
          the module loads.
        </li>
        <li>
          For full forensic recording (every record including state /
          attribute mutations, no rate-limit cap), use
          <code>?baredom-trace-history=raw</code>.
        </li>
      </ul>
      <div style="display: flex; gap: 12px; align-items: center;
                  flex-wrap: wrap; margin-top: 8px;">
        <strong>Dock status:</strong>
        <span [style.color]="active() ? 'var(--x-color-success, #2e7d32)'
                                       : 'var(--x-color-text-muted, #888)'"
              style="font-family: var(--x-font-family-mono, monospace);">
          {{ active()
             ? 'active'
             : 'inactive — add ?baredom-trace-history to the URL' }}
        </span>
      </div>
      @if (active()) {
        <div style="display: flex; gap: 12px; align-items: center;
                    flex-wrap: wrap; margin-top: 8px;">
          <strong>Live record count:</strong>
          <span style="font-family: var(--x-font-family-mono, monospace);">
            {{ count() }}
          </span>
          <button #verifier (click)="fireVerifier(verifier)">
            Fire verifier event
          </button>
          <span style="font-size: 12px;
                       color: var(--x-color-text-muted, #888);">
            Click — a record appears in the dock (and the count above
            increments within ~1 second).
          </span>
        </div>
      }
    </section>
  `,
})
export class TraceHistoryPanelComponent implements OnInit, OnDestroy {
  active  = signal(readActive());
  count   = signal(0);
  private timerId: number | null = null;

  ngOnInit(): void {
    this.active.set(readActive());
    if (!this.active()) return;

    this.count.set(readRecordCount());
    this.timerId = window.setInterval(
      () => this.count.set(readRecordCount()),
      POLL_INTERVAL_MS,
    );
  }

  ngOnDestroy(): void {
    if (this.timerId !== null) {
      window.clearInterval(this.timerId);
      this.timerId = null;
    }
  }

  fireVerifier(btn: HTMLButtonElement): void {
    // Intentionally a NATIVE <button>, not <x-button>: the smoke
    // test exists to confirm the recorder catches events from any
    // consumer-app HTML, not just from x-* components. Using
    // x-button here would dogfood the library too hard for this
    // particular test.
    //
    // bubbles + composed=true so the event survives shadow
    // boundaries; the recorder doesn't care, but it mirrors the
    // contract every BareDOM component honours.
    btn.dispatchEvent(
      new CustomEvent('angular-trace-history-verify', {
        bubbles:  true,
        composed: true,
        detail:   { source: 'angular-adapter-test-app' },
      }),
    );
  }
}
