// Tests of the prerender script: run with `node --test scripts/prerender.test.mjs`. They need
// Chrome, as the script does.

import { test } from 'node:test';
import assert from 'node:assert/strict';
import { execFile } from 'node:child_process';
import { createServer } from 'node:http';
import { mkdtemp, readdir, readFile, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';

const script = new URL('./prerender.mjs', import.meta.url).pathname;

// A page that says its number once its script has run. One whose frames throw, which the script
// refuses at once.
const page = n => `<!doctype html><html data-loading><body><p>page ${n}</p>
<script>setTimeout(() => { document.querySelector('p').textContent = 'drawn ${n}';
document.documentElement.removeAttribute('data-loading'); }, 20);</script></body></html>`;
const broken = `<!doctype html><html><body>
<script>window.requestAnimationFrame = () => { throw new Error('No frames here'); };</script></body></html>`;

// A server of these pages that holds each answer for `delayMs`, and counts how many pages it
// answers at once at most. A browser asks for an icon as well, which is no page.
const withServer = async (check, delayMs = 0) => {
  let open = 0;
  let most = 0;
  const server = createServer(async (request, response) => {
    if (request.url === '/favicon.ico') return response.writeHead(404).end();
    open += 1;
    most = Math.max(most, open);
    await new Promise(done => setTimeout(done, delayMs));
    response.setHeader('Content-Type', 'text/html');
    response.end(request.url === '/broken' ? broken : page(request.url.slice(1)));
    open -= 1;
  });
  await new Promise(resolve => server.listen(0, '127.0.0.1', resolve));
  const dir = await mkdtemp(join(tmpdir(), 'prerender-test-'));
  try {
    return await check(`http://127.0.0.1:${server.address().port}`, dir, () => most);
  } finally {
    server.close();
    await rm(dir, { recursive: true, force: true });
  }
};

const run = args =>
  new Promise(resolve =>
    execFile('node', [script, ...args], (error, stdout, stderr) =>
      resolve({ code: error ? error.code : 0, stdout, stderr })));

const pairs = (origin, dir, names) => names.flatMap(name => [`${origin}/${name}`, join(dir, `${name}.html`)]);

const numbers = n => Array.from({ length: n }, (_, i) => String(i + 1));

test('every page is written, each from its own url', () =>
  withServer(async (origin, dir) => {
    const names = numbers(7);
    const { code, stdout } = await run(['--tabs', '3', ...pairs(origin, dir, names)]);
    assert.equal(code, 0);
    assert.equal(stdout.trim().split('\n').length, names.length);
    for (const name of names) {
      assert.match(await readFile(join(dir, `${name}.html`), 'utf8'), new RegExp(`drawn ${name}<`));
    }
  }));

test('as many pages are open at once as there are tabs, and no more', async () => {
  const most = tabs => withServer(async (origin, dir, mostOpen) => {
    const { code } = await run(['--tabs', String(tabs), ...pairs(origin, dir, numbers(6))]);
    assert.equal(code, 0);
    return mostOpen();
  }, 300);
  assert.equal(await most(1), 1);
  assert.equal(await most(3), 3);
});

test('a page that fails is named by its url, and is not written', () =>
  withServer(async (origin, dir) => {
    const { code, stderr } = await run(pairs(origin, dir, ['broken']));
    assert.equal(code, 1);
    assert.match(stderr, new RegExp(`${origin}/broken: Error: No frames here`));
    assert.deepEqual(await readdir(dir), []);
  }));

// A url that nothing answers fails at once, while the other tab is at its first page, which
// the server holds back for a while.
test('a page that fails stops the run: no tab takes another page', () =>
  withServer(async (origin, dir) => {
    const { code, stderr } = await run(['--tabs', '2', 'http://127.0.0.1:1/', join(dir, 'gone.html'),
                                        ...pairs(origin, dir, numbers(8))]);
    assert.equal(code, 1);
    assert.match(stderr, /Could not open http:\/\/127\.0\.0\.1:1\//);
    assert.deepEqual(await readdir(dir), ['1.html']);
  }, 300));

test('pages that fail together are counted', () =>
  withServer(async (origin, dir) => {
    const { code, stderr } = await run(['--tabs', '2', ...pairs(origin, dir, ['broken', 'broken'])]);
    assert.equal(code, 1);
    assert.match(stderr, /No frames here\n1 more of the pages that were open failed too\./);
  }));

test('the number of tabs is a whole number of 1 or more', async () => {
  const { code, stderr } = await run(['--tabs', '0', 'http://127.0.0.1:1/', 'a.html']);
  assert.equal(code, 1);
  assert.match(stderr, /--tabs takes a whole number of 1 or more/);
});
