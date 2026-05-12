import { createRoot } from "react-dom/client";
import { App } from "./App";

// Side-effect import: loads + auto-registers the trace-history dock.
// The dock only mounts when activated via `?baredom-trace-history` in
// the URL or `window.BAREDOM_TRACE_HISTORY = true`. Otherwise the
// import is a no-op beyond the small module cost — safe to ship in
// production builds with the flag gated to dev only.
import "@vanelsas/baredom/x-trace-history";

createRoot(document.getElementById("root")!).render(<App />);
