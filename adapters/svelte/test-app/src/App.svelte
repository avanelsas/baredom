<script lang="ts">
  import { XTheme } from "@vanelsas/baredom-svelte/x-theme";
  import IconsSection from "./IconsSection.svelte";
  import FormSection from "./FormSection.svelte";
  import EventsSection from "./EventsSection.svelte";
  import RefSection from "./RefSection.svelte";
  import SlotsSection from "./SlotsSection.svelte";
</script>

<XTheme preset="brand">
  <main>
    <h1>BareDOM Svelte Adapter Test</h1>
    <IconsSection />
    <FormSection />
    <EventsSection />
    <RefSection />
    <SlotsSection />
  </main>
</XTheme>
