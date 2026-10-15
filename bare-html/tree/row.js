import { splitTemplate, nodeMaker, writeItem } from '../../dist/baremirror-alpha.js';

export const row = splitTemplate('row',
  ['li', { 'data-selected': item => item.selected },
    ['div', { class: 'line' },
      ['span', { class: 'label' }, item => item.label],
      ['x-badge', { size: 'sm', text: item => item.inside, hidden: item => item.inside === '' }],
      ['input', { class: 'note', placeholder: 'Type a note', on: { focusin: 'select' },
                  'aria-label': item => `Note for ${item.label}` }]],
    ['ul', { class: 'rows', 'data-x-part': 'children' }]]);

const makeRow = nodeMaker(row.fixed);

export function made(item) {
  const node = makeRow();
  writeItem(node, row, item);
  return node;
}
