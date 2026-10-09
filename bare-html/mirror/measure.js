export function rowWatcher(root) {
  let seen = new Set();
  return () => {
    const rows = [...root.querySelectorAll('li')];
    const created = rows.filter(row => !seen.has(row));
    seen = new Set(rows);
    return created;
  };
}

const isRender = record =>
  record.type === 'state/instance-field-set' && record.field.endsWith('Model');

const trace = () => window.BareDOM.traceHistory;

export function setCounting(on) {
  if (on) trace().resume();
  else trace().pause();
}

export function measure(work, counting) {
  if (counting) trace().clear();
  const started = performance.now();
  const result = work();
  const time = performance.now() - started;
  return { result, time, renders: counting ? trace().records().filter(isRender).length : null };
}

const outline = [
  { outline: '2px solid var(--flash)', outlineOffset: '2px' },
  { outline: '2px solid transparent', outlineOffset: '2px' },
];

const calm = matchMedia('(prefers-reduced-motion: reduce)');

export function flash(rows) {
  const easing = calm.matches ? 'steps(1, end)' : 'ease-out';
  rows.forEach(row => row.animate(outline, { duration: 900, easing }));
}
