import { mount } from "svelte";
import { useRegisterPreset } from "@vanelsas/baredom-svelte/composables";
import App from "./App.svelte";

// Dogfood the hand-written composable. Must run before <XTheme preset="brand">
// mounts so the preset lookup resolves.
useRegisterPreset("brand", {
  light: {
    "--x-color-primary": "#e11d48",
    "--x-color-primary-hover": "#be123c",
  },
  dark: {
    "--x-color-primary": "#fb7185",
    "--x-color-primary-hover": "#f43f5e",
  },
});

const target = document.getElementById("app");
if (!target) throw new Error("missing #app target");

mount(App, { target });
