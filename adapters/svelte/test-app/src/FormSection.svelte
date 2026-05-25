<script lang="ts">
  import { XCheckbox } from "@vanelsas/baredom-svelte/x-checkbox";
  import { XSlider } from "@vanelsas/baredom-svelte/x-slider";
  import { XSelect } from "@vanelsas/baredom-svelte/x-select";
  import { XPagination } from "@vanelsas/baredom-svelte/x-pagination";

  let agreed = $state(false);
  let volume = $state("42");
  let choice = $state("a");
  let page = $state(1);

  function blockOddPages(e: CustomEvent<{ page: number }>) {
    if (e.detail.page % 2 === 1) {
      e.preventDefault();
      console.log("[form] blocked odd page", e.detail.page);
    }
  }
</script>

<section>
  <h2>Forms — `$bindable()` two-way binding</h2>

  <p>
    <XCheckbox bind:checked={agreed}>I agree</XCheckbox>
    <code>agreed = {agreed}</code>
  </p>

  <p>
    <XSlider bind:value={volume} min="0" max="100" />
    <code>volume = {volume}</code>
  </p>

  <p>
    <XSelect bind:value={choice}>
      <option value="a">Alpha</option>
      <option value="b">Bravo</option>
      <option value="c">Charlie</option>
    </XSelect>
    <code>choice = {choice}</code>
  </p>

  <p>
    <XPagination bind:page totalPages={5} onpagechangerequest={blockOddPages} />
    <code>page = {page}</code>
  </p>
</section>
