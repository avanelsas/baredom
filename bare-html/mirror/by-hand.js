const template = document.createElement('template');
template.innerHTML = `
  <li>
    <x-checkbox></x-checkbox>
    <span class="text"></span>
    <input class="note" placeholder="A note, not saved" aria-label="Note">
    <x-badge></x-badge>
    <x-button size="sm" variant="danger">Remove</x-button>
  </li>`;

function create(id) {
  const li = template.content.firstElementChild.cloneNode(true);
  li.dataset.id = id;
  return { li, box: li.querySelector('x-checkbox'), text: li.querySelector('.text'),
           badge: li.querySelector('x-badge') };
}

function update({ box, text, badge }, task) {
  if (box.hasAttribute('checked') !== task.done) box.toggleAttribute('checked', task.done);
  if (box.getAttribute('aria-label') !== task.text) box.setAttribute('aria-label', task.text);
  if (text.textContent !== task.text) text.textContent = task.text;
  if (badge.getAttribute('variant') !== task.tone) badge.setAttribute('variant', task.tone);
  if (badge.getAttribute('text') !== task.status) badge.setAttribute('text', task.status);
}

function place(list, node, before) {
  if (list.moveBefore && node.isConnected) list.moveBefore(node, before);
  else list.insertBefore(node, before);
}

function arrange(list, nodes) {
  const wanted = new Set(nodes);
  const following = node => {
    let next = node.nextElementSibling;
    while (next && !wanted.has(next)) next = next.nextElementSibling;
    return next;
  };
  nodes.reduceRight((next, node) => {
    if (node.parentNode !== list || following(node) !== next) place(list, node, next);
    return node;
  }, null);
}

const idOf = event => event.target.closest('li').dataset.id;

function listen(list, dispatch) {
  list.addEventListener('x-checkbox-change-request', event => {
    event.preventDefault();
    dispatch(['toggle', idOf(event)]);
  });
  list.addEventListener('press', event => dispatch(['remove', idOf(event)]));
}

export function mount(root, dispatch) {
  const lists = {
    tasks: root.querySelector('[data-x-part="tasks"]'),
    done: root.querySelector('[data-x-part="done"]'),
  };
  const rows = new Map();
  const rowOf = id => {
    if (!rows.has(id)) rows.set(id, create(id));
    return rows.get(id);
  };
  Object.values(lists).forEach(list => listen(list, dispatch));
  return vm => {
    rows.forEach((row, id) => {
      if (!vm.rows[id]) { row.li.remove(); rows.delete(id); }
    });
    Object.values(vm.rows).forEach(task => update(rowOf(task.id), task));
    Object.entries(vm.places).forEach(([name, ids]) =>
      arrange(lists[name], ids.map(id => rows.get(id).li)));
  };
}
