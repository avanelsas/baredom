import { test, expect, type Page } from '@playwright/test';

// Read-path E2E smoke for the BareBuild Tasks demo. Drives the real stack
// (built app + `bb serve` JSON API on one origin) and asserts the DOM is a
// projection of server state, that filtering never refetches, and that the
// route's slotted body survives navigation (the identity-preservation invariant).
//
// The WRITE paths are deliberately NOT asserted — they ship unwired (the Phase-4
// telemetry seams in src/demo_app/write_side.cljs), so the default demo is
// read-only-correct and these assertions stay deterministic.

type Task = { id: number; title: string; status: string; assignee: string; due: string };

const rowTitles = (page: Page) =>
  page.$$eval('#tasks-table x-table-row', (rows) =>
    rows.slice(1).map((r) => r.querySelector('x-table-cell:nth-child(2)')?.textContent ?? ''));

test('home route is visible at /', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('barebuild-route[path="/"] h1')).toBeVisible();
});

test('board renders rows that match GET /api/tasks', async ({ page, request }) => {
  const serverTasks: Task[] = await (await request.get('/api/tasks')).json();

  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');

  // 1 header row + one row per task.
  await expect(page.locator('#tasks-table x-table-row')).toHaveCount(serverTasks.length + 1);
  expect(await rowTitles(page)).toEqual(serverTasks.map((t) => t.title));
});

test('stats reflect status counts', async ({ page, request }) => {
  const serverTasks: Task[] = await (await request.get('/api/tasks')).json();
  const count = (s: string) => String(serverTasks.filter((t) => t.status === s).length);

  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');

  for (const status of ['todo', 'doing', 'done']) {
    await expect(page.locator(`x-stat[data-status="${status}"]`)).toHaveAttribute('value', count(status));
  }
});

test('filtering is client-side and never refetches', async ({ page }) => {
  let tasksRequests = 0;
  page.on('request', (r) => {
    if (new URL(r.url()).pathname === '/api/tasks') tasksRequests += 1;
  });

  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');
  const afterLoad = tasksRequests;

  await page.fill('#task-search input', 'board');
  await expect(page.locator('#tasks-table x-table-row')).toHaveCount(2); // header + 1 match
  await page.fill('#task-search input', '');
  await page.waitForTimeout(150);

  // The board re-rendered from the broker's retained .state — no new GET.
  expect(tasksRequests).toBe(afterLoad);
});

test('a row navigates to the param-driven detail route', async ({ page, request }) => {
  await page.goto('/tasks');
  await page.click('#tasks-table x-table-row:nth-child(2) a[data-barebuild-route]');

  await expect(page).toHaveURL(/\/tasks\/\d+$/);
  const id = Number(new URL(page.url()).pathname.split('/').pop());
  const task: Task = await (await request.get(`/api/tasks/${id}`)).json();
  await expect(page.locator('#detail-title')).toContainText(`Task #${id}`);
  await expect(page.locator('#detail-title')).toContainText(task.title);
});

test('the board <x-table> handle survives navigation (identity preservation)', async ({ page }) => {
  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');

  // Tag the live element, leave to a detail route and come back.
  await page.evaluate(() => ((document.querySelector('#tasks-table') as any).__probe = 'KEEP-ME'));
  await page.click('#tasks-table x-table-row:nth-child(2) a[data-barebuild-route]');
  await expect(page).toHaveURL(/\/tasks\/\d+$/);
  // Scope to the navbar: the detail route also renders a "/tasks" back-link, so a
  // bare a[href="/tasks"] matches two live anchors (page.click takes the first;
  // a strict locator would throw). The navbar link is the unambiguous "go to board".
  await page.click('x-navbar a[href="/tasks"][data-barebuild-route]');
  await expect(page).toHaveURL(/\/tasks$/);

  // Same element instance → the route toggled display, never rebuilt the table.
  const probe = await page.evaluate(() => (document.querySelector('#tasks-table') as any).__probe);
  expect(probe).toBe('KEEP-ME');
  // ...and the rows re-rendered on reactivation.
  await expect(page.locator('#tasks-table x-table-row').first()).toBeVisible();
});

test('deep-link straight to a detail URL renders (SPA fallback + register-after-wiring)', async ({ page, request }) => {
  const task: Task = await (await request.get('/api/tasks/2')).json();
  await page.goto('/tasks/2');
  await expect(page.locator('#detail-title')).toContainText(`Task #2`);
  await expect(page.locator('#detail-title')).toContainText(task.title);
});

test('deep-link to /settings populates the form from GET /api/settings', async ({ page }) => {
  await page.goto('/settings');
  await expect
    .poll(() => page.evaluate(() => (document.querySelector('#settings-form [name="theme"]') as any)?.value))
    .toBe('system');
});

test('a missing task surfaces the error state (404 → error phase)', async ({ page }) => {
  await page.goto('/tasks/9999');
  await expect(page.locator('#detail-error')).toBeVisible();
});
