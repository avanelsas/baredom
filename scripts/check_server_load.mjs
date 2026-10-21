#!/usr/bin/env node
// Loads each module of dist in Node, where there is no DOM, and makes the calls a page makes
// when it loads: init for a component, and what docs/baremirror.md calls at module level for
// BareMirror. A server renderer does this when a page imports them. No module may fail.
//
// Usage:  node scripts/check_server_load.mjs [dist]
// Exit:   0 if each module loads, 1 if one fails. Designed for CI.

import { readdirSync } from 'node:fs';
import { resolve } from 'node:path';
import { pathToFileURL } from 'node:url';

const dist = resolve(process.argv[2] ?? 'dist');
const modules = readdirSync(dist).filter((f) => f.endsWith('.js') && f !== 'base.js');

// The calls a module may make when it loads, by file. Every other module calls its init.
const atLoad = {
  'baremirror-alpha.js': (m) => {
    const row = m.splitTemplate('row', ['li', ['span', (item) => item.text]]);
    m.nodeMaker(row.fixed);
    m.defineElement('bm-server-load-check', { attrs: [], template: ['span'] });
  },
};

const callInit = (m) => m.init?.();

async function failure(file) {
  try {
    const loaded = await import(pathToFileURL(resolve(dist, file)).href);
    (atLoad[file] ?? callInit)(loaded);
    return null;
  } catch (e) {
    return `${file}: ${e.name}: ${e.message}`;
  }
}

const failures = (await Promise.all(modules.map(failure))).filter(Boolean);

failures.forEach((line) => console.log(line));
if (failures.length > 0) {
  console.log(`${failures.length} of ${modules.length} modules fail where there is no DOM.`);
  process.exit(1);
}
console.log(`Each of ${modules.length} modules loads where there is no DOM.`);
