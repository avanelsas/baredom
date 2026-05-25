<script lang="ts">
  import { XModal, type XModalElement } from "@vanelsas/baredom-svelte/x-modal";

  let modalRef = $state<XModalElement | null>(null);

  function openModal() {
    modalRef?.show?.();
  }
</script>

<section>
  <h2>Refs — `bind:el` for imperative method calls</h2>

  <button onclick={openModal}>Open modal (imperative)</button>

  <XModal bind:el={modalRef}>
    <h3>Hello from a BareDOM modal</h3>
    <p>The Svelte wrapper exposes `el` via $bindable for method calls.</p>
  </XModal>
</section>
