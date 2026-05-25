import { watchEffect } from "vue";
import { registerPreset } from "@vanelsas/baredom/x-theme";

/**
 * Token map: CSS custom property names to CSS values.
 * e.g. `{ "--x-color-primary": "#3b82f6" }`
 */
export type TokenMap = Record<string, string>;

/**
 * Preset data with light and optional dark mode tokens.
 * Tokens not specified fall back to the `default` BareDOM preset.
 */
export interface PresetData {
  light: TokenMap;
  dark?: TokenMap;
}

/**
 * Register a custom BareDOM theme preset. The preset becomes available
 * to any `<XTheme preset="name">` in the tree.
 *
 * The registration runs on setup and re-runs reactively when `name`
 * or `data` change. Partial presets are supported — any tokens you
 * omit automatically fall back to the default preset.
 *
 * @example
 * ```ts
 * import { useRegisterPreset } from "@vanelsas/baredom-vue/composables";
 * import { XTheme } from "@vanelsas/baredom-vue/x-theme";
 *
 * const brandTokens = {
 *   light: {
 *     "--x-color-primary": "#e11d48",
 *     "--x-color-primary-hover": "#be123c",
 *   },
 *   dark: {
 *     "--x-color-primary": "#fb7185",
 *   },
 * };
 *
 * // inside setup()
 * useRegisterPreset("brand", brandTokens);
 * ```
 */
export function useRegisterPreset(name: string, data: PresetData): void {
  watchEffect(() => {
    registerPreset(name, data as any);
  });
}
