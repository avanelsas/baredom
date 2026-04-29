(ns baredom.dev.x-debug.model
  "Constants and CSS for the dev-only x-debug visual debugging overlay.")

(def tag-name "x-debug")

;; --- Marker keys (Closure-safe string keys for gobj/get|set) -----------

(def k-instrumented "__xDebugInstrumented")
(def k-observer     "__xDebugObserver")
(def k-panel-target "__xDebugPanelTarget")
(def k-panel-el     "__xDebugPanelEl")
(def k-cleanup-fns  "__xDebugCleanupFns")

(def proto-wrapped  "__xDebugWrapped")
(def proto-orig-attr-changed  "__xDebugOrigAttrChanged")
(def proto-orig-connected     "__xDebugOrigConnected")
(def proto-orig-disconnected  "__xDebugOrigDisconnected")

;; --- Debug overlay CSS (injected into each component's shadow root) ----

(def overlay-css
  ":host { outline: 1px dashed rgba(59,130,246,0.5) !important; outline-offset: -1px; position: relative; }
[data-x-debug-label] {
  position: absolute;
  top: 2px;
  right: 2px;
  font: 600 9px/1 system-ui, sans-serif;
  color: #fff;
  background: rgba(59,130,246,0.85);
  padding: 2px 5px;
  border-radius: 3px;
  pointer-events: auto;
  cursor: pointer;
  opacity: 0;
  transition: opacity 120ms ease;
  z-index: 999999;
  white-space: nowrap;
}
:host(:hover) > [data-x-debug-label] { opacity: 1; }")

;; --- Floating panel CSS (used inside the panel's own shadow root) ------

(def panel-css
  ":host {
  position: fixed;
  z-index: 2000000;
  pointer-events: auto;
  font: 12px/1.4 ui-monospace, 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
}
.panel {
  background: #1e1e2e;
  color: #cdd6f4;
  border: 1px solid rgba(59,130,246,0.6);
  border-radius: 8px;
  padding: 0;
  min-width: 260px;
  max-width: min(400px, calc(100vw - 2rem));
  max-height: min(500px, calc(100vh - 2rem));
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: rgba(59,130,246,0.15);
  border-bottom: 1px solid rgba(59,130,246,0.3);
  font-weight: 700;
  font-size: 13px;
  color: #89b4fa;
  border-radius: 8px 8px 0 0;
}
.close-btn {
  background: none;
  border: none;
  color: #6c7086;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  padding: 0 2px;
}
.close-btn:hover { color: #cdd6f4; }
.section { padding: 8px 12px; border-bottom: 1px solid rgba(255,255,255,0.06); }
.section:last-child { border-bottom: none; }
.section-title {
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #6c7086;
  margin-bottom: 4px;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 2px 0;
}
.key { color: #a6adc8; flex-shrink: 0; }
.val { color: #a6e3a1; text-align: right; word-break: break-all; }
.val.null { color: #6c7086; font-style: italic; }
.val.readonly { color: #6c7086; }
.empty { color: #6c7086; font-style: italic; font-size: 11px; padding: 2px 0; }
.edit-input {
  background: #313244;
  color: #a6e3a1;
  border: 1px solid #45475a;
  border-radius: 3px;
  padding: 1px 4px;
  font: inherit;
  font-size: 11px;
  width: 120px;
  text-align: right;
  outline: none;
}
.edit-input:focus { border-color: #89b4fa; }
.bool-toggle {
  appearance: none;
  width: 28px;
  height: 14px;
  background: #45475a;
  border-radius: 7px;
  position: relative;
  cursor: pointer;
  flex-shrink: 0;
  transition: background 120ms ease;
}
.bool-toggle::after {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 10px;
  height: 10px;
  background: #6c7086;
  border-radius: 50%;
  transition: transform 120ms ease, background 120ms ease;
}
.bool-toggle:checked { background: rgba(59,130,246,0.5); }
.bool-toggle:checked::after { transform: translateX(14px); background: #89b4fa; }")
