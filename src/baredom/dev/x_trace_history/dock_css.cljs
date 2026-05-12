(ns baredom.dev.x-trace-history.dock-css
  "Stylesheet string for the x-trace-history dock. Lives in its own
   namespace so `model.cljs` stays focused on data transforms and the
   dock layer can require this one string when attaching its shadow.")

(def dock-css
  ":host {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: min(420px, calc(100vw - 1rem));
  z-index: 1999999;
  font: 11px/1.4 ui-monospace, 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  color: #cdd6f4;
  pointer-events: auto;
  /* The width transition makes the collapse / expand toggle feel
     responsive without being slow. The dock is otherwise position:
     fixed so transitioning width doesn't reflow the host page. */
  transition: width 160ms ease;
}
/* Collapsed mode: shrink the host to a thin vertical strip so the
   user can reach components the full dock would otherwise cover.
   The only interactive element left is the collapse-toggle button,
   which expands the dock back to full width when clicked. */
:host(.collapsed) {
  width: 32px;
}
:host(.collapsed) .dock > *:not(.header) { display: none; }
:host(.collapsed) .header {
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
  gap: 4px;
}
:host(.collapsed) .header > *:not([data-x-th-action='collapse']) {
  display: none;
}
/* Causality mode: hide the axis-mode select (Order / Time only
   affects the timeline pane, so it's noise in causality view). */
:host(.causality-mode) [data-x-th-axis] {
  display: none;
}
@media (prefers-reduced-motion: reduce) {
  :host { transition: none; }
}
.dock {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #11111b;
  border-left: 1px solid rgba(59,130,246,0.6);
  box-shadow: -4px 0 20px rgba(0,0,0,0.4);
  /* Anchor for the absolutely-positioned drop-overlay added in PR 11
     so its `inset: 0` reaches the dock edges. */
  position: relative;
}
.header {
  display: flex;
  /* Wrap so the count + action buttons survive narrow viewports
     (`min(420px, calc(100vw - 1rem))` reaches 304px at 320px screens,
     which is too tight for four action buttons + title + count on
     one row). */
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: rgba(59,130,246,0.15);
  border-bottom: 1px solid rgba(59,130,246,0.3);
  flex: 0 0 auto;
}
.title { font-weight: 700; color: #89b4fa; font-size: 12px; }
.count { color: #6c7086; margin-left: auto; }
.filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px 12px;
  padding: 6px 10px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  flex: 0 0 auto;
}
/* The tag dropdown is an <x-select>; its own shadow brings styling.
   We just constrain its width and font so it matches the dock's
   ui-monospace 11px tone. */
.filters x-select {
  font-size: 11px;
  max-width: 140px;
}
/* x-checkbox has no slot for its label, so the visible text sits
   next to the checkbox inside a <label> wrapper. The label uses
   inline-flex to keep the checkbox + text on one row, with a small
   gap. Cursor: pointer mirrors native checkbox-label affordance. */
.filters label.cat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #a6adc8;
  font-size: 10px;
  cursor: pointer;
  user-select: none;
}
/* PR 16 — search input. x-search-field brings its own theming via its
   shadow DOM; the dock just constrains the host width so it shares
   the filter row's monospace tone with the tag dropdown. */
.filters x-search-field {
  font-size: 11px;
  flex: 1 1 140px;
  min-width: 100px;
  max-width: 240px;
}
/* Session strip: thin row above the filter row listing the live view
   plus any captured session as a clickable chip. Hidden when the only
   chip is Live (no sessions yet) so the dock stays compact. Scrolls
   horizontally if the chip count overflows the dock width. */
.sessions {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(0,0,0,0.15);
  border-bottom: 1px solid rgba(255,255,255,0.04);
  flex: 0 0 auto;
  overflow-x: auto;
  white-space: nowrap;
}
.sessions[hidden] { display: none; }
.session-chip {
  flex: 0 0 auto;
  background: rgba(255,255,255,0.04);
  color: #a6adc8;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 3px;
  padding: 1px 6px;
  font: inherit;
  font-size: 10px;
  cursor: pointer;
  user-select: none;
}
.session-chip:hover { background: rgba(59,130,246,0.12); color: #cdd6f4; }
.session-chip.active {
  background: rgba(59,130,246,0.22);
  border-color: rgba(59,130,246,0.6);
  color: #89b4fa;
  font-weight: 600;
}
/* Imported traces are visually distinct from locally-captured
   sessions: amber border instead of blue, plus a small close button
   inside the chip so the user can drop the import without clearing
   live state. */
.session-chip.import {
  border-color: rgba(249,226,175,0.45);
  color: #f9e2af;
}
.session-chip.import.active {
  background: rgba(249,226,175,0.18);
  border-color: rgba(249,226,175,0.7);
  color: #f9e2af;
}
.session-chip.import .chip-close {
  background: transparent;
  border: none;
  color: inherit;
  cursor: pointer;
  font: inherit;
  font-size: 12px;
  line-height: 1;
  padding: 0 0 0 6px;
  margin: 0;
  opacity: 0.6;
}
.session-chip.import .chip-close:hover { opacity: 1; }
.session-chip .live-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f38ba8;
  margin-right: 4px;
  vertical-align: middle;
  animation: x-th-pulse 1s ease-in-out infinite;
}
@keyframes x-th-pulse {
  0%, 100% { opacity: 1; }
  50%      { opacity: 0.35; }
}
@media (prefers-reduced-motion: reduce) {
  .session-chip .live-dot { animation: none; }
}
.timeline {
  flex: 1 1 auto;
  overflow: auto;
  min-height: 0;
  position: relative;
  /* Transparent border reserves 2px so the focus indicator below doesn't
     overlap the leftmost pixels of lane labels or shift content layout. */
  border: 2px solid transparent;
  box-sizing: border-box;
}
.timeline-body {
  display: flex;
  align-items: stretch;
  min-height: 100%;
}
.lanes {
  flex: 0 0 110px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255,255,255,0.06);
  background: #11111b;
  position: sticky;
  left: 0;
  z-index: 1;
}
.lane-label {
  height: 20px;
  padding: 0 8px;
  display: flex;
  align-items: center;
  font-size: 11px;
  color: #a6adc8;
  border-bottom: 1px solid rgba(255,255,255,0.04);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  user-select: none;
}
.lane-label:hover { background: rgba(59,130,246,0.08); color: #cdd6f4; }
.lane-label.active {
  background: rgba(59,130,246,0.18);
  color: #89b4fa;
  font-weight: 600;
}
.lane-label .cid { color: #6c7086; margin-left: 4px; }
.svg-pane {
  flex: 1 1 auto;
  min-width: 0;
  display: block;
}
svg.timeline-svg {
  display: block;
  background:
    repeating-linear-gradient(
      to bottom,
      transparent 0,
      transparent 19px,
      rgba(255,255,255,0.03) 19px,
      rgba(255,255,255,0.03) 20px);
}
.dot { cursor: pointer; transition: r 80ms ease; }
.dot:hover { stroke: #fff; stroke-width: 1; }
/* Heatmap bins (PR 18). Each lane that exceeds the density threshold
   renders as a row of these rects rather than individual dots; the
   per-bin fill-opacity is set inline by the renderer (scales with
   the bin's record count relative to the lane's max). */
.density-bin {
  cursor: pointer;
  transition: stroke 80ms ease, fill-opacity 80ms ease;
}
.density-bin:hover {
  stroke: #fff;
  stroke-width: 0.5;
}
/* Thin pink bar drawn at the selected record's exact x inside its
   bin — keeps the scrubber-style highlight in dense lanes without
   obscuring the bin's category colour. */
line.density-selected {
  stroke: #f5c2e7;
  stroke-width: 2;
  pointer-events: none;
}
line.scrubber {
  stroke: #f5c2e7;
  stroke-width: 1.5;
  stroke-dasharray: 4 2;
  pointer-events: none;
}
.svg-pane { cursor: crosshair; }
.timeline:focus {
  outline: none;
  border-color: #89b4fa;
}
.tooltip {
  position: absolute;
  background: #1e1e2e;
  border: 1px solid rgba(59,130,246,0.6);
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 10px;
  white-space: pre-wrap;
  pointer-events: none;
  max-width: 280px;
  color: #cdd6f4;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0,0,0,0.5);
}
.timeline-empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
  width: 100%;
}
/* Causality DAG pane (PR 17). Shares the flexible vertical slot with
   .timeline — render! toggles which one is hidden by dock-mode. The
   scrollable container holds an inner <svg> sized to the tree's
   bounding box so the scrollbars correctly reflect off-screen depth
   and breadth. */
.causality {
  flex: 1 1 auto;
  overflow: auto;
  min-height: 0;
  position: relative;
  background: #11111b;
  border: 2px solid transparent;
  box-sizing: border-box;
}
.causality:focus { outline: none; border-color: #89b4fa; }
.causality[hidden] { display: none; }
.timeline[hidden]  { display: none; }
.causality-empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
  width: 100%;
}
/* Leaf-hint banner rendered ABOVE the SVG when the tree has only
   one node. Pinned via sticky positioning so it stays visible even
   when the user scrolls the pane (the lone node sits at the
   padded origin and could be obscured by the banner otherwise). */
.causality-leaf-hint {
  position: sticky;
  top: 0;
  left: 0;
  z-index: 1;
  background: rgba(249,226,175,0.10);
  border-bottom: 1px solid rgba(249,226,175,0.35);
  color: #f9e2af;
  font-size: 10px;
  padding: 6px 10px;
  line-height: 1.4;
}
svg.causality-svg { display: block; }
svg.causality-svg .edge {
  stroke: rgba(255,255,255,0.18);
  stroke-width: 1;
  fill: none;
}
svg.causality-svg .node-rect {
  fill: rgba(255,255,255,0.04);
  stroke: rgba(255,255,255,0.12);
  stroke-width: 1;
  cursor: pointer;
  rx: 4;
  ry: 4;
  transition: fill 80ms ease, stroke 80ms ease;
}
svg.causality-svg .node-rect:hover {
  fill: rgba(59,130,246,0.16);
  stroke: rgba(59,130,246,0.6);
}
svg.causality-svg .node-rect.selected {
  fill: rgba(245,194,231,0.18);
  stroke: #f5c2e7;
  stroke-width: 1.5;
}
svg.causality-svg .node-dot {
  pointer-events: none;
}
svg.causality-svg .node-text {
  fill: #cdd6f4;
  font: 10px ui-monospace, 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  pointer-events: none;
  dominant-baseline: middle;
}
svg.causality-svg .node-text.id {
  fill: #a6adc8;
  font-size: 9px;
}
.splitter {
  flex: 0 0 auto;
  height: 4px;
  background: rgba(255,255,255,0.04);
  cursor: ns-resize;
  touch-action: none;
  transition: background 120ms ease;
}
.splitter:hover, .splitter.dragging { background: rgba(59,130,246,0.6); }
.detail {
  background: #181825;
  border-top: 1px solid rgba(59,130,246,0.2);
  padding: 8px 10px;
  font-size: 10px;
  color: #cdd6f4;
  flex: 0 0 auto;
  height: 35vh;
  min-height: 60px;
  max-height: 80%;
  overflow-y: auto;
}
.detail-json {
  margin: 0 0 8px 0;
  font: inherit;
  white-space: pre-wrap;
  word-break: break-all;
  color: #a6e3a1;
}
.detail-section { margin-top: 6px; }
.detail-label {
  color: #89b4fa;
  font-weight: 600;
  margin-bottom: 2px;
  font-size: 10px;
  letter-spacing: 0.02em;
}
/* The host is an <x-button>; its own shadow brings background, hover,
   border-radius, etc. We keep block-layout + width on the host so each
   link fills its row, and reach into the button's `label` part to add
   ellipsis truncation (the summary string can exceed the dock width). */
.detail-link {
  display: block;
  width: 100%;
  margin: 2px 0;
}
.detail-link::part(label) {
  display: block;
  min-width: 0;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}
.detail-link::part(button) {
  width: 100%;
  justify-content: flex-start;
}
.detail-empty { color: #6c7086; font-style: italic; font-size: 10px; }
.empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
}
.hint { color: #6c7086; font-size: 10px; padding: 4px 10px; flex: 0 0 auto; }
.hint.error { color: #f38ba8; }
/* Drop overlay — covers the whole dock while a file is being hovered.
   pointer-events:none keeps drag events flowing through to the dock root
   so the dragleave/drop bookkeeping doesn't fight the overlay layer. */
.drop-overlay {
  position: absolute;
  inset: 0;
  background: rgba(249,226,175,0.10);
  border: 2px dashed rgba(249,226,175,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f9e2af;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
  z-index: 100;
  pointer-events: none;
}
.drop-overlay[hidden] { display: none; }
/* The hidden file input is kept off-screen — clicked programmatically
   by the Import button — but we mark it `display:none` to keep it out
   of layout entirely. */
.import-input { display: none; }")
