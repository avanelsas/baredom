import { test, expect, request as pwrequest } from '@playwright/test';

// Cross-origin E2E — the point of the second backend: BareBuild's orchestration elements
// are vanilla fetch + CustomEvents that hold no client state, so they work against ANY
// server honouring the contract (server/API-CONTRACT.md). The page is served on :3000
// (serve.clj); selecting the "bb-cors" backend points every API call at the INDEPENDENT
// :3001 server (serve_api.clj, API-only + CORS). The backend is data-driven
// (demo_app.wiring/backends) and chosen via the navbar picker; `?backend=<value>` is the
// deep-link that seeds it. Both web servers are booted by playwright.config.ts.

const API = 'http://localhost:3001';

test('create round-trips to the independent :3001 backend and the board refetches (CORS)', async ({ page }) => {
  const api = await pwrequest.newContext();
  const before = (await (await api.get(`${API}/api/tasks`)).json()).length;

  await page.goto('/tasks?backend=bb-cors');
  await page.waitForSelector('#tasks-table x-table-row');

  await page.evaluate(() => (document.querySelector('#new-task-modal') as { show(): void }).show());
  const title = page.locator('#new-task-form x-form-field[name="title"] input');
  await title.waitFor({ state: 'visible' });
  await title.fill('Cross-origin task');
  await page.click('#new-task-form x-button[type="submit"]');

  // <barebuild-action> POSTed cross-origin and the child <barebuild-invalidate-on>
  // refetched — so the board (its broker src + the invalidate src both carry the same
  // absolute base) shows the new row, all via the elements, no hand-written fetch.
  await expect(page.locator('#tasks-table')).toContainText('Cross-origin task');

  // It landed on :3001 (the independent server), not the :3000 babashka one.
  const after = await (await api.get(`${API}/api/tasks`)).json();
  expect(after.find((t: { title: string }) => t.title === 'Cross-origin task')).toBeTruthy();
  expect(after.length).toBe(before + 1);
  const on3000 = await (await api.get('http://localhost:3000/api/tasks')).json();
  expect(on3000.find((t: { title: string }) => t.title === 'Cross-origin task')).toBeFalsy();
});

test('in-app navigation drops ?backend from the URL but the backend stays :3001 (sticky)', async ({ page }) => {
  const api = await pwrequest.newContext();
  await page.goto('/tasks?backend=bb-cors');
  await page.waitForSelector('#tasks-table x-table-row');

  // Navigate via the in-app nav link. The router navigates using the anchor's href
  // (/settings, no query), so ?backend disappears from the address bar — the original report.
  await page.click('a.nav-link[href="/settings"]');
  await expect(page).toHaveURL(/\/settings$/);

  // The settings form still populated from GET :3001 (read is cross-origin post-nav)…
  await expect
    .poll(() => page.evaluate(() => (document.querySelector('#settings-form [name="theme"]') as { value?: string })?.value))
    .toBeTruthy();
  // …and a write still lands on :3001 even though the URL no longer shows ?backend — the
  // choice is sticky (sessionStorage), so navigation does not silently revert to :3000.
  await page.evaluate(() => { (document.querySelector('#settings-form [name="theme"]') as { value: string }).value = 'light'; });
  await page.click('#settings-form x-button[type="submit"]');
  await expect
    .poll(async () => (await (await api.get(`${API}/api/settings`)).json()).theme)
    .toBe('light');
});

test('settings PUT round-trips to :3001 and refetches (CORS preflight path)', async ({ page }) => {
  const api = await pwrequest.newContext();

  await page.goto('/settings?backend=bb-cors');
  // Wait for the form to populate from GET :3001 (proves the read is cross-origin). Don't
  // assert a specific initial value: serve_api state persists across the run, so a sibling
  // test may have already changed the theme — this test owns only its own set→assert.
  await expect
    .poll(() => page.evaluate(() => (document.querySelector('#settings-form [name="theme"]') as { value?: string })?.value))
    .toBeTruthy();

  await page.evaluate(() => { (document.querySelector('#settings-form [name="theme"]') as { value: string }).value = 'dark'; });
  await page.click('#settings-form x-button[type="submit"]');

  // The PUT is a cross-origin mutation → triggers a CORS preflight serve_api answers.
  await expect
    .poll(async () => (await (await api.get(`${API}/api/settings`)).json()).theme)
    .toBe('dark');
});

test('the navbar backend picker switches to :3001 and the choice survives the reload', async ({ page }) => {
  const api = await pwrequest.newContext();
  await page.goto('/tasks'); // default: same-origin (:3000)
  await page.waitForSelector('#tasks-table x-table-row');
  const before = (await (await api.get(`${API}/api/tasks`)).json()).length;

  // Pick the CORS backend through the navbar x-select; the handler persists + reloads.
  await Promise.all([
    page.waitForEvent('load'),
    page.evaluate(() => {
      document.querySelector('#backend-select')!.dispatchEvent(
        new CustomEvent('select-change', { detail: { value: 'bb-cors' }, bubbles: true }));
    }).catch(() => {}), // the reload destroys this execution context — expected
  ]);
  await page.waitForSelector('#tasks-table x-table-row');

  // After the reload the demo is on :3001 (the choice is sticky). A create lands there.
  await page.evaluate(() => (document.querySelector('#new-task-modal') as { show(): void }).show());
  const t = page.locator('#new-task-form x-form-field[name="title"] input');
  await t.waitFor({ state: 'visible' });
  await t.fill('Picker task');
  await page.click('#new-task-form x-button[type="submit"]');
  await expect(page.locator('#tasks-table')).toContainText('Picker task');

  const after = await (await api.get(`${API}/api/tasks`)).json();
  expect(after.find((x: { title: string }) => x.title === 'Picker task')).toBeTruthy();
  expect(after.length).toBe(before + 1);
});

test('a default load is upgrade-safe: the picker neither reloads nor switches', async ({ page }) => {
  // The select-change listener is attached before x-select upgrades. If x-select ever
  // echoed select-change on upgrade/programmatic value-set, the handler would spuriously
  // select-backend! + reload. Assert exactly ONE load and the picker resting on same-origin.
  let loads = 0;
  page.on('load', () => { loads += 1; });
  await page.goto('/tasks'); // no ?backend → default same-origin
  await page.waitForSelector('#tasks-table x-table-row');
  await page.waitForTimeout(400); // give any echo-fired reload a chance to happen
  const value = await page.evaluate(() => (document.querySelector('#backend-select') as { value?: string })?.value);
  expect(value).toBe('same-origin');
  expect(loads).toBe(1);
});
