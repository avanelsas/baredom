"use client";

import { useEffect } from "react";
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
 * The registration runs once on mount (or when `name` / `data` change).
 * Partial presets are supported — any tokens you omit automatically
 * fall back to the default preset.
 *
 * @example
 * ```tsx
 * import { useRegisterPreset } from "@vanelsas/baredom-react/hooks";
 * import { XTheme } from "@vanelsas/baredom-react/x-theme";
 *
 * const brandTokens = {
 *   light: {
 *     "--x-color-primary": "#e11d48",
 *     "--x-color-primary-hover": "#be123c",
 *     "--x-font-family": "'Inter', sans-serif",
 *   },
 *   dark: {
 *     "--x-color-primary": "#fb7185",
 *     "--x-color-primary-hover": "#f43f5e",
 *   },
 * };
 *
 * function App() {
 *   useRegisterPreset("brand", brandTokens);
 *   return <XTheme preset="brand">...</XTheme>;
 * }
 * ```
 */
export function useRegisterPreset(name: string, data: PresetData): void {
  useEffect(() => {
    registerPreset(name, data as any);
  }, [name, data]);
}
