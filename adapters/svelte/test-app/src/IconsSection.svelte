<script lang="ts">
  import { XIcon, type XIconElement } from "@vanelsas/baredom-svelte/x-icon";

  let firstIcon = $state<XIconElement | null>(null);
  $effect(() => {
    if (firstIcon) console.log("[icons] first icon element:", firstIcon);
  });
</script>

<section>
  <h2>Icons — no events, no bindable</h2>
  <XIcon bind:el={firstIcon} label="check" />
  <XIcon label="arrow-right" />
  <XIcon label="x" />
  <XIcon label="search" />
  <XIcon label="moon" />
  <XIcon label="sun" class="hero" />
</section>
