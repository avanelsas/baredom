import { test, expect } from '@playwright/test';

// Write-path E2E for the ALPHA declarative write side (barebuild-action +
// barebuild-invalidate-on). Drives the real stack and mutates server state, so
// each assertion compares against the live server rather than hardcoded counts.

test('create: barebuild-action POSTs and invalidate-on refetches the board', async ({ page, request }) => {
  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');
  const before = (await (await request.get('/api/tasks')).json()).length;

  // Open the modal (in-app: x-button → press → modal.show; call show() directly for a
  // deterministic headless run), then fill + submit.
  await page.evaluate(() => (document.querySelector('#new-task-modal') as { show(): void }).show());
  const title = page.locator('#new-task-form x-form-field[name="title"] input');
  await title.waitFor({ state: 'visible' });
  await title.fill('E2E declarative task');
  await page.click('#new-task-form x-button[type="submit"]');

  // No hand-written fetch in write_side: <barebuild-action> POSTed and its child
  // <barebuild-invalidate-on> refetched /api/tasks, so the new row appears.
  await expect(page.locator('#tasks-table')).toContainText('E2E declarative task');

  const after = await (await request.get('/api/tasks')).json();
  const created = after.find((t: { title: string }) => t.title === 'E2E declarative task');
  expect(created).toBeTruthy();
  expect(after.length).toBe(before + 1);
  // Payload hygiene restored: write_side installs a `valuesTransform` (without-blanks)
  // on #create-action, so an unset status no longer POSTs "" and the server default
  // {status:"todo"} applies. (Earlier alpha shipped this as a gap; the transform seam
  // on barebuild-action closed it — see write-side-design-notes.md.)
  expect(created.status).toBe('todo');
});

test('update: barebuild-action PUTs the edit form (dynamic /api/tasks/:id)', async ({ page, request }) => {
  await page.goto('/tasks/2');
  await page.waitForSelector('#detail-title');
  // The edit action's URL was set to /api/tasks/2 by detail/on-route-change.
  await page.fill('#edit-task-form x-form-field[name="assignee"] input', 'Ported Dev');
  await page.click('#edit-task-form x-button[type="submit"]');
  await expect
    .poll(async () => (await (await request.get('/api/tasks/2')).json()).assignee)
    .toBe('Ported Dev');
});

test('settings: barebuild-action PUTs and invalidate-on refetches', async ({ page, request }) => {
  await page.goto('/settings');
  await expect
    .poll(() => page.evaluate(() => (document.querySelector('#settings-form [name="theme"]') as { value?: string })?.value))
    .toBe('system');
  await page.evaluate(() => { (document.querySelector('#settings-form [name="theme"]') as { value: string }).value = 'dark'; });
  await page.fill('#settings-form x-form-field[name="page-size"] input', '25');
  await page.click('#settings-form x-button[type="submit"]');
  await expect
    .poll(async () => (await (await request.get('/api/settings')).json()).theme)
    .toBe('dark');
  // Payload hygiene restored: #settings-action's valuesTransform (with-number) coerces
  // page-size, so the merge endpoint stores a NUMBER, not the string the control reports.
  const settings = await (await request.get('/api/settings')).json();
  expect(settings['page-size']).toBe(25);
  expect(typeof settings['page-size']).toBe('number');
});

test('delete (row): hand-wired DELETE + barebuild-invalidate refetches the board', async ({ page, request }) => {
  await page.goto('/tasks');
  await page.waitForSelector('#tasks-table x-table-row');
  const before = await (await request.get('/api/tasks')).json();
  const victim = before[before.length - 1].id;
  await page.click(`#tasks-table x-button[data-delete-id="${victim}"]`);
  // The delegated handler DELETEs, then dispatches barebuild-invalidate {/api/tasks};
  // the board self-matches and refetches — the same protocol the declarative flows use.
  await expect
    .poll(async () => (await (await request.get('/api/tasks')).json()).length)
    .toBe(before.length - 1);
});
