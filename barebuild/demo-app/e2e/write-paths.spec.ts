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
  // NOTE: created.status comes through "" — the action JSON-encodes the form values
  // as-is and cannot blank-strip (the hand-wired version used without-blanks). A
  // documented payload-cleanliness gap; see write-side-design-notes.md.
});
