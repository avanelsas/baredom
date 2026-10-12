#!/usr/bin/env node
// Writes a page back out with every shadow tree in its markup, as a declarative shadow root, so
// the page shows its components before any script has run.
//
// Usage:  node scripts/prerender.mjs [--static] <url> <output.html> [<url> <output.html> ...]
//         --static leaves out every script and noscript element and sets data-static on the
//         html element, so the page can style a copy that runs no script.
//         Each url is written to the output that follows it. One browser opens them all.
// Needs:  Node 22 or later, and Chrome. Set CHROME_PATH when Chrome is not found, and
//         CHROME_FLAGS for more flags, such as --no-sandbox in a container.
//
// A page must be served: the script opens its url in a headless Chrome. Write the output where
// the page's relative urls still resolve, which is beside the page it was made from.

import { spawn } from 'node:child_process';
import { existsSync } from 'node:fs';
import { mkdir, mkdtemp, rm, writeFile } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { basename, dirname, join } from 'node:path';

const STEP_LIMIT_MS = 10_000;
const PAGE_LIMIT_MS = 30_000;

const CHROME_PATHS = [
  process.env.CHROME_PATH,
  '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
  '/usr/bin/google-chrome',
  '/usr/bin/chromium',
  '/usr/bin/chromium-browser',
];

const findChrome = () => CHROME_PATHS.find(path => path && existsSync(path));

const extraFlags = () => (process.env.CHROME_FLAGS ?? '').split(/\s+/).filter(Boolean);

const within = (ms, what, promise) =>
  Promise.race([
    promise,
    new Promise((_, reject) => setTimeout(() => reject(new Error(`Timed out: ${what}`)), ms)),
  ]);

// This function runs in the page. It waits until every custom element is defined, in the
// document and in every shadow tree, until the html element has no data-loading, and until
// every image has loaded or failed. Then it waits for two frames and returns the page as markup.
// It stops, and says what it waited for, when one of these takes longer than the limit.
const pageAsMarkup = async (limitMs, isStatic) => {
  const shadowRoots = node =>
    [...node.querySelectorAll('*')]
      .filter(el => el.shadowRoot)
      .flatMap(el => [el.shadowRoot, ...shadowRoots(el.shadowRoot)]);
  const undefinedTags = () => [...new Set(
    [document, ...shadowRoots(document)]
      .flatMap(root => [...root.querySelectorAll(':not(:defined)')])
      .map(el => el.localName))];
  const allDefined = async () => {
    const tags = undefinedTags();
    if (tags.length === 0) return;
    await Promise.all(tags.map(tag => customElements.whenDefined(tag)));
    return allDefined();
  };
  const frame = () => new Promise(done => requestAnimationFrame(done));
  const loading = () => document.documentElement.hasAttribute('data-loading');
  const loaded = async () => {
    while (loading()) await frame();
  };
  const limit = () => new Promise(done => setTimeout(done, limitMs));
  // An image that the browser loads lazily, and that is outside the window, would never load
  // here. It is loaded now, and is lazy again in the markup that is written, also when the wait
  // is cut short.
  const images = () =>
    [document, ...shadowRoots(document)].flatMap(root => [...root.querySelectorAll('img')]);
  const settled = img => new Promise(done => {
    if (img.complete) return done();
    img.addEventListener('load', done, { once: true });
    img.addEventListener('error', done, { once: true });
  });
  const pictured = async () => {
    const lazy = images().filter(img => img.loading === 'lazy' && !img.complete);
    lazy.forEach(img => { img.loading = 'eager'; });
    try {
      await Promise.race([Promise.all(images().map(settled)), limit()]);
    } finally {
      lazy.forEach(img => { img.loading = 'lazy'; });
    }
    return images().filter(img => !img.complete).map(img => img.currentSrc || img.src);
  };
  const makeStatic = () => {
    document.querySelectorAll('script, noscript, link[rel="modulepreload"]').forEach(el => el.remove());
    document.documentElement.setAttribute('data-static', '');
  };
  await Promise.race([allDefined().then(loaded), limit()]);
  const missing = undefinedTags();
  if (missing.length > 0) throw new Error(`Never defined: ${missing.join(', ')}`);
  if (loading()) throw new Error('The page still has data-loading.');
  const unfinished = await pictured();
  if (unfinished.length > 0) throw new Error(`An image never finished loading: ${unfinished.join(', ')}`);
  await frame();
  await frame();
  if (isStatic) makeStatic();
  const htmlTag = document.documentElement.cloneNode(false).outerHTML.replace('</html>', '');
  const inside = document.documentElement.getHTML({ shadowRoots: shadowRoots(document) });
  return `<!doctype html>\n${htmlTag}${inside}</html>\n`;
};

const startChrome = (chromePath, profileDir) =>
  spawn(chromePath, [
    '--headless=new', '--remote-debugging-port=0', `--user-data-dir=${profileDir}`,
    '--no-first-run', '--disable-gpu', ...extraFlags(), 'about:blank',
  ]);

const chromeAddress = chrome =>
  new Promise((resolve, reject) => {
    chrome.stderr.on('data', chunk => {
      const found = /DevTools listening on (ws:\/\/\S+)/.exec(String(chunk));
      if (found) resolve(found[1]);
    });
    chrome.once('exit', () => reject(new Error('Chrome stopped before it could be reached.')));
  });

const stopChrome = async (chrome, profileDir) => {
  const running = chrome.exitCode === null && chrome.signalCode === null;
  const stopped = running ? new Promise(resolve => chrome.once('exit', resolve)) : Promise.resolve();
  chrome.kill();
  await stopped;
  await rm(profileDir, { recursive: true, force: true, maxRetries: 10, retryDelay: 100 });
};

// A connection to Chrome over its debugging protocol: `send` gives the answer to one command,
// and `next` the next event of one name.
const connect = async address => {
  const socket = new WebSocket(address);
  await new Promise((resolve, reject) => {
    socket.onopen = resolve;
    socket.onerror = () => reject(new Error('No connection to Chrome.'));
  });
  const answers = new Map();
  const events = new Map();
  let lastId = 0;
  const settle = (waiting, name, message) => {
    const resolve = waiting.get(name);
    waiting.delete(name);
    resolve?.(message);
  };
  const onAnswer = message => settle(answers, message.id, message);
  const onEvent = message => settle(events, message.method, message);
  socket.onmessage = ({ data }) => {
    const message = JSON.parse(data);
    (message.id ? onAnswer : onEvent)(message);
  };
  return {
    send: (method, params = {}, sessionId) =>
      new Promise((resolve, reject) => {
        lastId += 1;
        answers.set(lastId, ({ error, result }) =>
          error ? reject(new Error(`${method}: ${error.message}`)) : resolve(result));
        socket.send(JSON.stringify({ id: lastId, method, params, sessionId }));
      }),
    next: method => new Promise(resolve => events.set(method, resolve)),
    close: () => socket.close(),
  };
};

const prerender = async (browser, url, isStatic) => {
  const { targetId } = await browser.send('Target.createTarget', { url: 'about:blank' });
  try {
    const { sessionId } = await browser.send('Target.attachToTarget', { targetId, flatten: true });
    await browser.send('Page.enable', {}, sessionId);
    const loaded = browser.next('Page.loadEventFired');
    const { errorText } = await browser.send('Page.navigate', { url }, sessionId);
    if (errorText) throw new Error(`Could not open ${url}: ${errorText}`);
    await loaded;
    const { result, exceptionDetails } = await browser.send('Runtime.evaluate', {
      expression: `(${pageAsMarkup})(${STEP_LIMIT_MS}, ${isStatic})`, awaitPromise: true, returnByValue: true,
    }, sessionId);
    if (exceptionDetails) {
      const description = exceptionDetails.exception?.description ?? exceptionDetails.text;
      throw new Error(`${url}: ${description.split('\n')[0]}`);
    }
    return result.value;
  } finally {
    // A tab that cannot be closed must not hide why the page failed.
    await browser.send('Target.closeTarget', { targetId }).catch(() => {});
  }
};

const shadowRootCount = markup => markup.split('shadowrootmode').length - 1;

// Each page is opened in a tab of its own, one after the other, and written as soon as it is
// read. A page that fails stops the run, and the pages before it stay written.
const writePages = async (browser, pages, isStatic) => {
  for (const { url, output } of pages) {
    const markup = await within(PAGE_LIMIT_MS, `prerendering ${url}`, prerender(browser, url, isStatic));
    await mkdir(dirname(output), { recursive: true });
    await writeFile(output, markup);
    console.log(`${output}: ${markup.length} characters, ${shadowRootCount(markup)} shadow roots`);
  }
};

const STATIC_FLAG = '--static';

// The script is run by its file, or as the command of the package. It names itself as it was run.
const USAGE = `Usage: ${basename(process.argv[1] ?? 'prerender.mjs')} [--static] <url> <output.html> [<url> <output.html> ...]`;

// The arguments after the flag are pairs: a url, and the file it is written to.
const pagesOf = args => {
  if (args.length === 0 || args.length % 2 !== 0) throw new Error(USAGE);
  return Array.from({ length: args.length / 2 }, (_, i) => ({ url: args[2 * i], output: args[2 * i + 1] }));
};

const main = async args => {
  const isStatic = args.includes(STATIC_FLAG);
  const pages = pagesOf(args.filter(arg => arg !== STATIC_FLAG));
  const chromePath = findChrome();
  if (!chromePath) throw new Error('Chrome was not found. Set CHROME_PATH.');
  const profileDir = await mkdtemp(join(tmpdir(), 'prerender-'));
  const chrome = startChrome(chromePath, profileDir);
  try {
    const browser = await connect(await chromeAddress(chrome));
    try {
      await writePages(browser, pages, isStatic);
    } finally {
      browser.close();
    }
  } finally {
    await stopChrome(chrome, profileDir);
  }
};

main(process.argv.slice(2)).then(
  () => process.exit(0),
  error => {
    console.error(error.message);
    process.exit(1);
  });
