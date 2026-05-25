<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, useTemplateRef } from "vue";

// Read the live record count once per second so the panel doesn't
// burn CPU polling — the dock itself reflects activity in real time;
// the panel is just a smoke indicator that the integration works.
const POLL_INTERVAL_MS = 1000;

function readActive(): boolean {
  return typeof window !== "undefined"
    && Boolean((window as any).BareDOM?.traceHistory);
}

function readRecordCount(): number {
  const api = (window as any).BareDOM?.traceHistory;
  return api ? api.records().length : 0;
}

const active = ref(readActive());
const count = ref(0);
const verifierBtn = useTemplateRef<HTMLButtonElement>("verifierBtn");
let timerId: number | undefined;

onMounted(() => {
  active.value = readActive();
  if (!active.value) return;
  count.value = readRecordCount();
  timerId = window.setInterval(() => {
    count.value = readRecordCount();
  }, POLL_INTERVAL_MS);
});

onBeforeUnmount(() => {
  if (timerId !== undefined) window.clearInterval(timerId);
});

function fireVerifier(): void {
  // Intentionally a NATIVE <button>, not <XButton>: the smoke test
  // exists to confirm the recorder catches events from any consumer-app
  // HTML, not just from x-* components. Using XButton here would
  // dogfood the library too hard for this particular test.
  //
  // bubbles + composed=true so the event survives shadow boundaries.
  verifierBtn.value?.dispatchEvent(
    new CustomEvent("vue-trace-history-verify", {
      bubbles: true,
      composed: true,
      detail: { source: "vue-adapter-test-app" },
    }),
  );
}
</script>

<template>
  <section>
    <h2>9. Trace history — adapter smoke</h2>
    <p :style="{ fontSize: '13px', color: 'var(--x-color-text-muted, #888)' }">
      The trace-history dock is loaded via a side-effect import in
      <code>main.ts</code>. It only mounts when activated:
    </p>
    <ul :style="{ fontSize: '13px', color: 'var(--x-color-text-muted, #888)' }">
      <li>Open this page with <code>?baredom-trace-history</code> in the URL, OR</li>
      <li>Set <code>window.BAREDOM_TRACE_HISTORY = true</code> before the module loads.</li>
      <li>
        For full forensic recording (every record including state /
        attribute mutations, no rate-limit cap), use
        <code>?baredom-trace-history=raw</code>.
      </li>
    </ul>
    <div :style="{ display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap', marginTop: '8px' }">
      <strong>Dock status:</strong>
      <span :style="{
        color: active ? 'var(--x-color-success, #2e7d32)' : 'var(--x-color-text-muted, #888)',
        fontFamily: 'var(--x-font-family-mono, monospace)',
      }">
        {{ active ? "active" : "inactive — add ?baredom-trace-history to the URL" }}
      </span>
    </div>
    <div
      v-if="active"
      :style="{ display: 'flex', gap: '12px', alignItems: 'center', flexWrap: 'wrap', marginTop: '8px' }"
    >
      <strong>Live record count:</strong>
      <span :style="{ fontFamily: 'var(--x-font-family-mono, monospace)' }">{{ count }}</span>
      <button ref="verifierBtn" @click="fireVerifier">Fire verifier event</button>
      <span :style="{ fontSize: '12px', color: 'var(--x-color-text-muted, #888)' }">
        Click — a record appears in the dock (and the count above increments within ~1 second).
      </span>
    </div>
  </section>
</template>
