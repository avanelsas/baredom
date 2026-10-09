const most = 5;

function shortened(list) {
  return list.length <= most ? list : [...list.slice(0, most), `and ${list.length - most} more`];
}

export function stateText(state) {
  return JSON.stringify({ ...state, tasks: shortened(state.tasks) }, null, 2);
}

export function planText(steps) {
  return JSON.stringify({ remove: shortened(steps.remove), place: shortened(steps.place) }, null, 2);
}

async function gzipped(bytes) {
  const stream = new Blob([bytes]).stream().pipeThrough(new CompressionStream('gzip'));
  return (await new Response(stream).arrayBuffer()).byteLength;
}

export async function sizeOf(url) {
  const bytes = await (await fetch(url)).arrayBuffer();
  return { raw: bytes.byteLength, gzip: await gzipped(bytes) };
}

export const kilobytes = bytes => `${(bytes / 1000).toFixed(1)} kB`;

export async function sourceOf(url) {
  const text = await (await fetch(url)).text();
  return { text, lines: text.trimEnd().split('\n').length };
}

export function sourceBlock(name, { text, lines }, open) {
  const collapse = document.createElement('x-collapse');
  const code = document.createElement('x-code');
  collapse.setAttribute('header', `${name}, ${lines} lines`);
  code.setAttribute('slot', 'content');
  code.setAttribute('language', 'javascript');
  code.toggleAttribute('line-numbers', true);
  code.toggleAttribute('expanded', open);
  collapse.toggleAttribute('open', open);
  code.setAttribute('code', text);
  collapse.append(code);
  return collapse;
}
