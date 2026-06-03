import { defineConfig } from '@playwright/test';

// Playwright config for the BareBuild Tasks demo E2E.
//
// Two web servers boot, demonstrating BareBuild is server-agnostic:
//  - `bb serve`        — single origin :3000 (app + JSON API via serve.clj). The
//                        read/write specs drive this with no CORS; SPA deep-links
//                        resolve via the server's fallback.
//  - `bb serve-api 3001` — the INDEPENDENT API-only + CORS server (serve_api.clj) on
//                        a separate origin. xorigin.spec.ts drives it via ?api= to
//                        prove the elements work cross-origin against a different
//                        backend (see server/API-CONTRACT.md).
// Bundled chromium is used (what `bb e2e` provisions via `npx playwright install
// chromium`, and what CI installs).
export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: process.env.CI ? 'github' : 'list',
  timeout: 30_000,
  use: {
    baseURL: 'http://localhost:3000',
    browserName: 'chromium',
    trace: 'on-first-retry',
    // GitHub runners need --no-sandbox for headless chromium (mirrors the repo's
    // karma CHROMIUM_FLAGS); a no-op locally.
    launchOptions: { args: process.env.CI ? ['--no-sandbox'] : [] },
  },
  webServer: [
    {
      command: 'bb serve',
      url: 'http://localhost:3000',
      reuseExistingServer: !process.env.CI,
      // `bb serve` installs deps + does a release build before serving, so give the
      // cold path generous headroom.
      timeout: 180_000,
    },
    {
      // The independent cross-origin backend for xorigin.spec.ts. API-only, no build,
      // so it starts fast; readiness probed on a real endpoint.
      command: 'bb serve-api 3001',
      url: 'http://localhost:3001/api/tasks',
      reuseExistingServer: !process.env.CI,
      timeout: 60_000,
    },
  ],
});
