import { createSignal } from "solid-js";
import { XTheme } from "@vanelsas/baredom-solid/x-theme";
import { XButton } from "@vanelsas/baredom-solid/x-button";
import { XCheckbox } from "@vanelsas/baredom-solid/x-checkbox";
import { XSlider } from "@vanelsas/baredom-solid/x-slider";
import { XSelect } from "@vanelsas/baredom-solid/x-select";
import { XModal } from "@vanelsas/baredom-solid/x-modal";

export function App() {
  // Event-only path
  const [pressCount, setPressCount] = createSignal(0);

  // Controlled boolean (XCheckbox)
  const [checked, setChecked] = createSignal(false);

  // Controlled string (XSlider)
  const [sliderValue, setSliderValue] = createSignal("50");

  // Imperative path (XModal): hold a ref and toggle the `open` attribute
  let modalEl!: HTMLElement;
  const [modalOpen, setModalOpen] = createSignal(false);

  return (
    <XTheme>
      <h1>BareDOM Solid Adapter — Smoke Test</h1>

      <section>
        <h2>1 · Event-only — XButton onPress</h2>
        <XButton onPress={() => setPressCount((n) => n + 1)}>
          Click me ({pressCount()})
        </XButton>
      </section>

      <section>
        <h2>2 · Controlled boolean — XCheckbox checked + onChangeRequest</h2>
        <XCheckbox
          checked={checked()}
          onChangeRequest={(e) => setChecked(e.detail.nextChecked)}
        >
          Subscribe (signal: {String(checked())})
        </XCheckbox>
      </section>

      <section>
        <h2>3 · Controlled string — XSlider value + onChangeRequest</h2>
        <XSlider
          value={sliderValue()}
          min="0"
          max="100"
          onChangeRequest={(e) => setSliderValue(e.detail.value)}
        />
        <pre>value: {sliderValue()}</pre>
      </section>

      <section>
        <h2>4 · Uncontrolled — XSelect defaultValue</h2>
        <XSelect defaultValue="middle">
          <option value="top">Top</option>
          <option value="middle">Middle</option>
          <option value="bottom">Bottom</option>
        </XSelect>
      </section>

      <section>
        <h2>5 · Imperative path — XModal via ref + open attribute</h2>
        <XButton onPress={() => setModalOpen(true)}>Open modal</XButton>
        <XModal
          ref={modalEl}
          open={modalOpen()}
          onClose={() => setModalOpen(false)}
        >
          <p>This is a controlled modal driven by a signal.</p>
          <XButton onPress={() => setModalOpen(false)}>Close</XButton>
        </XModal>
      </section>
    </XTheme>
  );
}
