<script lang="ts">
  // Side-effect imports register the custom elements; we use the raw tags
  // below to verify BareDOM's shadow-DOM named-slot path works under Svelte
  // 5's compilation. The Svelte wrappers DON'T destructure `slot` (Svelte 5
  // rejects explicit `slot={...}` on a wrapper's root), so `slot="..."` on
  // a child of `<XButton>` flows through the wrapper's `...rest` to the
  // inner `<x-button>` — exactly the same code path exercised by writing
  // `<x-button>` directly here. Svelte 5's TS checker rejects
  // `<XButton><elem slot="..."/></XButton>` because it can't statically
  // validate named slots on a Svelte component (a Svelte 4-era $$Slots
  // mechanism); the raw-tag approach sidesteps that.
  import "@vanelsas/baredom-svelte/x-button";
  import "@vanelsas/baredom-svelte/x-navbar";
  import { XButton } from "@vanelsas/baredom-svelte/x-button";
</script>

<section>
  <h2>Slots — named-slot forwarding</h2>

  <p>
    <x-button>
      <span slot="icon-start" aria-hidden="true">✓</span>
      With icon-start slot
    </x-button>
  </p>

  <p>
    <x-button>
      With icon-end slot
      <span slot="icon-end" aria-hidden="true">→</span>
    </x-button>
  </p>

  <p>
    <x-navbar>
      <strong slot="brand">BareDOM</strong>
      <a slot="start" href="#docs">Docs</a>
      <a slot="end" href="#about">About</a>
    </x-navbar>
  </p>

  <!--
    Exercises the wrapper's `...rest` flow-through path: a Svelte wrapper
    `<XButton slot="end">` placed inside a raw custom element `<x-navbar>`.
    `slot="end"` is consumed by the wrapper, flows through `...rest`, and
    lands on the inner `<x-button>` element where BareDOM's shadow-DOM slot
    matcher picks it up. svelte-check accepts this because `<XButton>` is a
    descendant of a custom element at the consumer's call site.
  -->
  <p>
    <x-navbar>
      <strong slot="brand">Wrapper-as-slot</strong>
      <XButton slot="end" onpress={() => console.log("[slots] wrapper-in-slot pressed")}>
        Sign in
      </XButton>
    </x-navbar>
  </p>
</section>
