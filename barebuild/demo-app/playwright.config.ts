import { defineConfig } from '@playwright/test';

// Playwright config for the BareBuild Tasks demo read-path smoke.
//
// The webServer boots `bb serve` (build the app + serve public/ + the JSON API on
// a single origin, port 3000) so the spec drives the real stack with no CORS and
// deep-links resolve via the server's SPA fallback. Bundled chromium is used (what
// `bb e2e` provisions via `npx playwright install chromium`, and what CI installs).
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
  webServer: {
    command: 'bb serve',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
    // `bb serve` installs deps + does a release build before serving, so give the
    // cold path generous headroom.
    timeout: 180_000,
  },
});
