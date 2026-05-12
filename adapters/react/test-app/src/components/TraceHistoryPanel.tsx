import { useEffect, useRef, useState } from "react";

// Read the live record count once per second so the panel doesn't
// burn CPU polling — the dock itself reflects activity in real time;
// the panel is just a smoke indicator that the integration works.
const POLL_INTERVAL_MS = 1000;

function readActive(): boolean {
  // The dock installs `window.BareDOM.traceHistory` only when the
  // recorder activated (URL flag or window flag at module load).
  return typeof window !== "undefined"
    && Boolean(window.BareDOM?.traceHistory);
}

function readRecordCount(): number {
  const api = window.BareDOM?.traceHistory;
  return api ? api.records().length : 0;
}

/**
 * Smoke panel for the trace-history dock when consumed from a React
 * app. Confirms the side-effect import in main.tsx took effect and
 * the dock auto-mounted when the activation flag is on. Fires a
 * verifier CustomEvent on demand so the user can see a record land
 * in the dock without leaving the page.
 */
export function TraceHistoryPanel() {
  const [active, setActive] = useState(readActive());
  const [count, setCount] = useState(0);
  const verifierRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    setActive(readActive());
    if (!readActive()) return;

    setCount(readRecordCount());
    const id = window.setInterval(() => setCount(readRecordCount()),
                                  POLL_INTERVAL_MS);
    return () => window.clearInterval(id);
  }, []);

  const fireVerifier = () => {
    // Intentionally a NATIVE <button>, not <x-button>: the smoke
    // test exists to confirm the recorder catches events from any
    // consumer-app HTML, not just from x-* components. Using
    // x-button here would dogfood the library too hard for this
    // particular test.
    //
    // bubbles + composed=true so the event survives shadow
    // boundaries; the recorder doesn't care, but it mirrors the
    // contract every BareDOM component honours.
    verifierRef.current?.dispatchEvent(
      new CustomEvent("react-trace-history-verify", {
        bubbles:  true,
        composed: true,
        detail:   { source: "react-adapter-test-app" },
      }),
    );
  };

  return (
    <section>
      <h2>9. Trace history — adapter smoke</h2>
      <p style={{ fontSize: 13, color: "var(--x-color-text-muted, #888)" }}>
        The trace-history dock is loaded via a side-effect import in{" "}
        <code>main.tsx</code>. It only mounts when activated:
      </p>
      <ul style={{ fontSize: 13, color: "var(--x-color-text-muted, #888)" }}>
        <li>
          Open this page with{" "}
          <code>?baredom-trace-history</code> in the URL, OR
        </li>
        <li>
          Set <code>window.BAREDOM_TRACE_HISTORY = true</code> before
          the module loads.
        </li>
        <li>
          For full forensic recording (every record including state /
          attribute mutations, no rate-limit cap), use{" "}
          <code>?baredom-trace-history=raw</code>.
        </li>
      </ul>
      <div style={{ display: "flex", gap: 12, alignItems: "center",
                    flexWrap: "wrap", marginTop: 8 }}>
        <strong>Dock status:</strong>
        <span style={{
          color: active ? "var(--x-color-success, #2e7d32)"
                        : "var(--x-color-text-muted, #888)",
          fontFamily: "var(--x-font-family-mono, monospace)",
        }}>
          {active ? "active" : "inactive — add ?baredom-trace-history to the URL"}
        </span>
      </div>
      {active && (
        <div style={{ display: "flex", gap: 12, alignItems: "center",
                      flexWrap: "wrap", marginTop: 8 }}>
          <strong>Live record count:</strong>
          <span style={{
            fontFamily: "var(--x-font-family-mono, monospace)",
          }}>
            {count}
          </span>
          <button ref={verifierRef} onClick={fireVerifier}>
            Fire verifier event
          </button>
          <span style={{ fontSize: 12,
                         color: "var(--x-color-text-muted, #888)" }}>
            Click — a record appears in the dock (and the count above
            increments within ~1 second).
          </span>
        </div>
      )}
    </section>
  );
}
