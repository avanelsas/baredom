import { useState, useRef } from "react";
import { XTheme } from "@vanelsas/baredom-react/x-theme";
import { XButton } from "@vanelsas/baredom-react/x-button";
import { XAlert } from "@vanelsas/baredom-react/x-alert";
import { XSwitch } from "@vanelsas/baredom-react/x-switch";
import { XBadge } from "@vanelsas/baredom-react/x-badge";
import { XSpinner } from "@vanelsas/baredom-react/x-spinner";
import { XDivider } from "@vanelsas/baredom-react/x-divider";
import type { XButton as XButtonElement } from "@vanelsas/baredom/x-button";

export function App() {
  const [log, setLog] = useState<string[]>([]);
  const [disabled, setDisabled] = useState(false);
  const [loading, setLoading] = useState(false);
  const [alertVisible, setAlertVisible] = useState(true);
  const buttonRef = useRef<XButtonElement>(null);

  const addLog = (msg: string) => {
    setLog((prev) => [...prev.slice(-19), `${new Date().toLocaleTimeString()} — ${msg}`]);
  };

  return (
    <XTheme>
      <div style={{ maxWidth: 640, margin: "2rem auto", fontFamily: "var(--x-font-family, system-ui)", color: "var(--x-color-text, inherit)" }}>
        <h1>BareDOM React Adapter Test</h1>

        {/* --- Test 1: Button with events and props --- */}
        <section>
          <h2>1. XButton — props, events, ref</h2>
          <div style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
            <XButton
              ref={buttonRef}
              disabled={disabled}
              loading={loading}
              onPress={(e) => addLog(`Button pressed (source: ${e.detail.source})`)}
              onHoverStart={() => addLog("Button hover start")}
              onHoverEnd={() => addLog("Button hover end")}
            >
              Click me
            </XButton>

            <button onClick={() => setDisabled((d) => !d)}>
              Toggle disabled ({disabled ? "on" : "off"})
            </button>
            <button onClick={() => setLoading((l) => !l)}>
              Toggle loading ({loading ? "on" : "off"})
            </button>
            <button onClick={() => addLog(`Ref tagName: ${buttonRef.current?.tagName}`)}>
              Read ref
            </button>
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 2: Alert with dismiss event --- */}
        <section>
          <h2>2. XAlert — dismiss event, conditional render</h2>
          {alertVisible ? (
            <XAlert
              type="info"
              text="This is a dismissible alert from React"
              dismissible={true}
              onDismiss={(e) => {
                addLog(`Alert dismissed (reason: ${e.detail.reason})`);
                setAlertVisible(false);
              }}
            />
          ) : (
            <div>
              <em>Alert dismissed.</em>{" "}
              <button onClick={() => setAlertVisible(true)}>Show again</button>
            </div>
          )}
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 3: Switch with toggle event --- */}
        <section>
          <h2>3. XSwitch — toggle event</h2>
          <XSwitch
            onChange={(e) => addLog(`Switch changed: checked=${e.detail.checked}`)}
          />
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 4: Components without events --- */}
        <section>
          <h2>4. Static components — badge, spinner</h2>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <XBadge text="React" variant="primary" />
            <XBadge text="Adapter" variant="secondary" />
            <XSpinner size="sm" />
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Event log --- */}
        <section>
          <h2>Event Log</h2>
          <div
            style={{
              background: "var(--x-color-surface, #1e1e2e)",
              color: "var(--x-color-success, #a6e3a1)",
              fontFamily: "var(--x-font-family-mono, monospace)",
              fontSize: 12,
              padding: 12,
              borderRadius: "var(--x-radius-md, 8px)",
              border: "1px solid var(--x-color-border, transparent)",
              minHeight: 120,
              maxHeight: 300,
              overflowY: "auto",
            }}
          >
            {log.length === 0 ? (
              <span style={{ color: "var(--x-color-text-muted, #6c7086)" }}>Interact with components above...</span>
            ) : (
              log.map((entry, i) => <div key={i}>{entry}</div>)
            )}
          </div>
        </section>
      </div>
    </XTheme>
  );
}
