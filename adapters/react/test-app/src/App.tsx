import { useState, useRef, useMemo } from "react";
import { XTheme } from "@vanelsas/baredom-react/x-theme";
import { XButton } from "@vanelsas/baredom-react/x-button";
import { XAlert } from "@vanelsas/baredom-react/x-alert";
import { XSwitch } from "@vanelsas/baredom-react/x-switch";
import { XCheckbox } from "@vanelsas/baredom-react/x-checkbox";
import { XSlider } from "@vanelsas/baredom-react/x-slider";
import { XBadge } from "@vanelsas/baredom-react/x-badge";
import { XSpinner } from "@vanelsas/baredom-react/x-spinner";
import { XDivider } from "@vanelsas/baredom-react/x-divider";
import { useRegisterPreset } from "@vanelsas/baredom-react/hooks";
import type { PresetData } from "@vanelsas/baredom-react/hooks";
import type { XButton as XButtonElement } from "@vanelsas/baredom/x-button";

const candyPreset: PresetData = {
  light: {
    "--x-color-primary": "#e11d48",
    "--x-color-primary-hover": "#be123c",
    "--x-color-primary-active": "#9f1239",
    "--x-color-surface": "#fff1f2",
    "--x-color-bg": "#ffffff",
    "--x-color-text": "#1c1917",
    "--x-color-border": "#fecdd3",
    "--x-radius-md": "16px",
    "--x-radius-sm": "12px",
  },
  dark: {
    "--x-color-primary": "#fb7185",
    "--x-color-primary-hover": "#f43f5e",
    "--x-color-primary-active": "#e11d48",
    "--x-color-surface": "#1c1017",
    "--x-color-bg": "#0c0a09",
    "--x-color-text": "#fef2f2",
    "--x-color-border": "#4c0519",
    "--x-radius-md": "16px",
    "--x-radius-sm": "12px",
  },
};

export function App() {
  const [log, setLog] = useState<string[]>([]);
  const [disabled, setDisabled] = useState(false);
  const [loading, setLoading] = useState(false);
  const [alertVisible, setAlertVisible] = useState(true);
  const [checked, setChecked] = useState(false);
  const [switchOn, setSwitchOn] = useState(false);
  const [sliderVal, setSliderVal] = useState("25");
  const [preset, setPreset] = useState<string | undefined>(undefined);
  const buttonRef = useRef<XButtonElement>(null);

  const addLog = (msg: string) => {
    setLog((prev) => [...prev.slice(-19), `${new Date().toLocaleTimeString()} — ${msg}`]);
  };

  // Memoize so the object identity is stable across renders
  const stableCandy = useMemo(() => candyPreset, []);
  useRegisterPreset("candy", stableCandy);

  return (
    <XTheme preset={preset}>
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

        {/* --- Test 3: Controlled checkbox --- */}
        <section>
          <h2>3. XCheckbox — controlled</h2>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <XCheckbox
              checked={checked}
              onChangeRequest={(e) => {
                addLog(`Checkbox change-request: ${e.detail.previousChecked} → ${e.detail.nextChecked}`);
                setChecked(e.detail.nextChecked);
              }}
            />
            <span>Controlled: {checked ? "checked" : "unchecked"}</span>
            <button onClick={() => setChecked((c) => !c)}>Toggle from React</button>
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 4: Controlled switch --- */}
        <section>
          <h2>4. XSwitch — controlled</h2>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <XSwitch
              checked={switchOn}
              onChangeRequest={(e) => {
                addLog(`Switch change-request: ${e.detail.previousChecked} → ${e.detail.nextChecked}`);
                setSwitchOn(e.detail.nextChecked);
              }}
            />
            <span>Controlled: {switchOn ? "on" : "off"}</span>
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 5: Controlled slider --- */}
        <section>
          <h2>5. XSlider — controlled</h2>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <XSlider
              value={sliderVal}
              min="0"
              max="100"
              showValue
              onChangeRequest={(e) => {
                addLog(`Slider change-request: ${e.detail.previousValue} → ${e.detail.value}`);
                setSliderVal(String(e.detail.value));
              }}
              style={{ flex: 1 }}
            />
            <span style={{ minWidth: 40 }}>{sliderVal}</span>
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 6: Uncontrolled switch (defaultChecked) --- */}
        <section>
          <h2>6. XSwitch — uncontrolled (defaultChecked)</h2>
          <XSwitch
            defaultChecked={true}
            onChange={(e) => addLog(`Uncontrolled switch changed: checked=${e.detail.checked}`)}
          />
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 7: Static components --- */}
        <section>
          <h2>7. Static components — badge, spinner</h2>
          <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
            <XBadge text="React" variant="primary" />
            <XBadge text="Adapter" variant="secondary" />
            <XSpinner size="sm" />
          </div>
        </section>

        <XDivider style={{ margin: "1.5rem 0" }} />

        {/* --- Test 8: useRegisterPreset hook --- */}
        <section>
          <h2>8. useRegisterPreset — custom theme</h2>
          <div style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
            <button onClick={() => { setPreset(undefined); addLog("Preset: default"); }}>
              Default
            </button>
            <button onClick={() => { setPreset("candy"); addLog("Preset: candy"); }}>
              Candy
            </button>
            <button onClick={() => { setPreset("aurora"); addLog("Preset: aurora"); }}>
              Aurora (built-in)
            </button>
            <span style={{ fontSize: 14, color: "var(--x-color-text-muted, #888)" }}>
              Active: {preset ?? "default"}
            </span>
          </div>
          <p style={{ fontSize: 13, color: "var(--x-color-text-muted, #888)", marginTop: 8 }}>
            Switch presets to see all components above re-theme. "Candy" is registered via useRegisterPreset.
          </p>
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
