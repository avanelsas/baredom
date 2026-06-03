import { test, expect, request as pwrequest } from '@playwright/test';

// Cross-origin E2E — the point of the second backend: BareBuild's orchestration
// elements are vanilla fetch + CustomEvents that hold no client state, so they work
// against ANY server honouring the contract (server/API-CONTRACT.md). Here the page
// is served on :3000 (serve.clj) but ?api= points every API call at the INDEPENDENT
// :3001 server (serve_api.clj, API-only + CORS). Both web servers are booted by
// playwright.config.ts. The trailing slash on ?api also exercises base normalization.

const API = 'http://localhost:3001';

test('create round-trips to the independent :3001 backend and the board refetches (CORS)', async ({ page }) => {
  const api = await pwrequest.newContext();
  const before = (await (await api.get(`${API}/api/tasks`)).json()).length;

  // Trailing slash on purpose — proves wiring/api-base normalizes (no //api/tasks).
  await page.goto('/tasks?api=http://localhost:3001/');
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

test('settings PUT round-trips to :3001 and refetches (CORS preflight path)', async ({ page }) => {
  const api = await pwrequest.newContext();

  await page.goto('/settings?api=http://localhost:3001');
  await expect
    .poll(() => page.evaluate(() => (document.querySelector('#settings-form [name="theme"]') as { value?: string })?.value))
    .toBe('system');

  await page.evaluate(() => { (document.querySelector('#settings-form [name="theme"]') as { value: string }).value = 'dark'; });
  await page.click('#settings-form x-button[type="submit"]');

  // The PUT is a cross-origin mutation → triggers a CORS preflight serve_api answers.
  await expect
    .poll(async () => (await (await api.get(`${API}/api/settings`)).json()).theme)
    .toBe('dark');
});
