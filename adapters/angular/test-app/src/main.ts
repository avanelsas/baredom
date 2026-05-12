import 'zone.js';
// Side-effect import: loads + auto-registers the trace-history dock.
// The dock only mounts when activated via `?baredom-trace-history` in
// the URL or `window.BAREDOM_TRACE_HISTORY = true`. Otherwise the
// import is a no-op beyond the small module cost — safe to ship in
// production builds with the flag gated to dev only.
//
// Order matters: zone.js patches DOM APIs (including dispatchEvent)
// during its own import. By importing trace-history AFTER zone.js,
// the recorder's dispatchEvent wrapper installs on top of zone's
// patch — events still flow through both, and the recorder sees
// every dispatch the app fires.
import '@vanelsas/baredom/x-trace-history';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent).catch(err => console.error(err));
