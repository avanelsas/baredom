<script setup lang="ts">
import { ref, useTemplateRef, computed } from "vue";
import { XTheme } from "@vanelsas/baredom-vue/x-theme";
import { XButton, type XButtonExposed } from "@vanelsas/baredom-vue/x-button";
import { XAlert } from "@vanelsas/baredom-vue/x-alert";
import { XSwitch } from "@vanelsas/baredom-vue/x-switch";
import { XCheckbox } from "@vanelsas/baredom-vue/x-checkbox";
import { XSlider } from "@vanelsas/baredom-vue/x-slider";
import { XBadge } from "@vanelsas/baredom-vue/x-badge";
import { XSpinner } from "@vanelsas/baredom-vue/x-spinner";
import { XDivider } from "@vanelsas/baredom-vue/x-divider";
import { useRegisterPreset, type PresetData } from "@vanelsas/baredom-vue/composables";
import TraceHistoryPanel from "./components/TraceHistoryPanel.vue";

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

useRegisterPreset("candy", candyPreset);

const log = ref<string[]>([]);
const disabled = ref(false);
const loading = ref(false);
const alertVisible = ref(true);
const checked = ref(false);
const switchOn = ref(false);
const sliderVal = ref("25");
const preset = ref<string | undefined>(undefined);
const buttonInstance = useTemplateRef<XButtonExposed>("buttonInstance");

const presetLabel = computed(() => preset.value ?? "default");

function addLog(msg: string): void {
  log.value = [...log.value.slice(-19), `${new Date().toLocaleTimeString()} — ${msg}`];
}

function toggleDisabled(): void {
  disabled.value = !disabled.value;
}

function toggleLoading(): void {
  loading.value = !loading.value;
}

function readRef(): void {
  addLog(`Ref tagName: ${buttonInstance.value?.el?.tagName}`);
}
</script>

<template>
  <XTheme :preset="preset">
    <div :style="{ maxWidth: '640px', margin: '2rem auto', fontFamily: 'var(--x-font-family, system-ui)', color: 'var(--x-color-text, inherit)' }">
      <h1>BareDOM Vue Adapter Test</h1>

      <!-- Test 1: Button with events and props -->
      <section>
        <h2>1. XButton — props, events, ref</h2>
        <div :style="{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap' }">
          <XButton
            ref="buttonInstance"
            :disabled="disabled"
            :loading="loading"
            @press="(e) => addLog(`Button pressed (source: ${e.detail.source})`)"
            @hover-start="() => addLog('Button hover start')"
            @hover-end="() => addLog('Button hover end')"
          >
            Click me
          </XButton>

          <button @click="toggleDisabled">
            Toggle disabled ({{ disabled ? "on" : "off" }})
          </button>
          <button @click="toggleLoading">
            Toggle loading ({{ loading ? "on" : "off" }})
          </button>
          <button @click="readRef">Read ref</button>
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 2: Alert with dismiss event -->
      <section>
        <h2>2. XAlert — dismiss event, conditional render</h2>
        <XAlert
          v-if="alertVisible"
          type="info"
          text="This is a dismissible alert from Vue"
          :dismissible="true"
          @dismiss="(e) => { addLog(`Alert dismissed (reason: ${e.detail.reason})`); alertVisible = false; }"
        />
        <div v-else>
          <em>Alert dismissed.</em>{{ " " }}
          <button @click="alertVisible = true">Show again</button>
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 3: Controlled checkbox via v-model -->
      <section>
        <h2>3. XCheckbox — v-model</h2>
        <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
          <XCheckbox v-model="checked" />
          <span>v-model: {{ checked ? "checked" : "unchecked" }}</span>
          <button @click="checked = !checked">Toggle from Vue</button>
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 4: Controlled switch via v-model -->
      <section>
        <h2>4. XSwitch — v-model</h2>
        <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
          <XSwitch v-model="switchOn" />
          <span>v-model: {{ switchOn ? "on" : "off" }}</span>
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 5: Controlled slider via v-model -->
      <section>
        <h2>5. XSlider — v-model</h2>
        <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
          <XSlider
            v-model="sliderVal"
            min="0"
            max="100"
            :show-value="true"
            :style="{ flex: 1 }"
          />
          <span :style="{ minWidth: '40px' }">{{ sliderVal }}</span>
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 6: Uncontrolled switch (no v-model) -->
      <section>
        <h2>6. XSwitch — uncontrolled (default behavior)</h2>
        <XSwitch
          :checked="true"
          @change="(e) => addLog(`Uncontrolled switch changed: checked=${e.detail.checked}`)"
        />
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 7: Static components -->
      <section>
        <h2>7. Static components — badge, spinner</h2>
        <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
          <XBadge text="Vue" variant="primary" />
          <XBadge text="Adapter" variant="secondary" />
          <XSpinner size="sm" />
        </div>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 8: useRegisterPreset composable -->
      <section>
        <h2>8. useRegisterPreset — custom theme</h2>
        <div :style="{ display: 'flex', gap: '8px', alignItems: 'center', flexWrap: 'wrap' }">
          <button @click="() => { preset = undefined; addLog('Preset: default'); }">Default</button>
          <button @click="() => { preset = 'candy'; addLog('Preset: candy'); }">Candy</button>
          <button @click="() => { preset = 'aurora'; addLog('Preset: aurora'); }">Aurora (built-in)</button>
          <span :style="{ fontSize: '14px', color: 'var(--x-color-text-muted, #888)' }">
            Active: {{ presetLabel }}
          </span>
        </div>
        <p :style="{ fontSize: '13px', color: 'var(--x-color-text-muted, #888)', marginTop: '8px' }">
          Switch presets to see all components above re-theme. "Candy" is registered via useRegisterPreset.
        </p>
      </section>

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Test 9: Trace history dock smoke -->
      <TraceHistoryPanel />

      <XDivider :style="{ margin: '1.5rem 0' }" />

      <!-- Event log -->
      <section>
        <h2>Event Log</h2>
        <div
          :style="{
            background: 'var(--x-color-surface, #1e1e2e)',
            color: 'var(--x-color-success, #a6e3a1)',
            fontFamily: 'var(--x-font-family-mono, monospace)',
            fontSize: '12px',
            padding: '12px',
            borderRadius: 'var(--x-radius-md, 8px)',
            border: '1px solid var(--x-color-border, transparent)',
            minHeight: '120px',
            maxHeight: '300px',
            overflowY: 'auto',
          }"
        >
          <span v-if="log.length === 0" :style="{ color: 'var(--x-color-text-muted, #6c7086)' }">
            Interact with components above...
          </span>
          <div v-for="(entry, i) in log" :key="i">{{ entry }}</div>
        </div>
      </section>
    </div>
  </XTheme>
</template>
