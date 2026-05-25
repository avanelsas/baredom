<script lang="ts">
  import { XButton } from "@vanelsas/baredom-svelte/x-button";
  import { XAlert } from "@vanelsas/baredom-svelte/x-alert";

  let pressCount = $state(0);
  let lastDismissed = $state<string | null>(null);

  function onPress(e: CustomEvent<{ source: string }>) {
    pressCount += 1;
    console.log("[events] press from", e.detail.source);
  }
</script>

<section>
  <h2>Events — typed callback props</h2>

  <p>
    <XButton onpress={onPress}>Click me</XButton>
    <code>pressed = {pressCount}</code>
  </p>

  <p>
    <XAlert
      type="success"
      text="Operation completed"
      dismissible
      ondismiss={() => { lastDismissed = new Date().toISOString(); }}
    />
    <code>lastDismissed = {lastDismissed ?? "—"}</code>
  </p>
</section>
