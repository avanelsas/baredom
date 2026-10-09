const numbered = count => Array.from({ length: count }, (_, index) =>
  ({ id: String(index + 1), text: `Task ${index + 1}`, done: false }));

export const initial = {
  tasks: [
    { id: '1', text: 'Read the thread', done: false },
    { id: '2', text: 'Write the task list', done: false },
    { id: '3', text: 'Ship it', done: false },
  ],
  nextId: 4,
  draft: '',
  refusing: false,
};

const toggled = (id, task) => (task.id === id ? { ...task, done: !task.done } : task);

function added(state) {
  const text = state.draft.trim();
  if (text === '') return state;
  const task = { id: String(state.nextId), text, done: false };
  return { ...state, draft: '', nextId: state.nextId + 1, tasks: [...state.tasks, task] };
}

export function step(state, [meaning, arg]) {
  const { tasks, refusing } = state;
  switch (meaning) {
    case 'draft':    return { ...state, draft: arg };
    case 'add':      return added(state);
    case 'toggle':   return refusing ? state : { ...state, tasks: tasks.map(task => toggled(arg, task)) };
    case 'remove':   return { ...state, tasks: tasks.filter(task => task.id !== arg) };
    case 'rotate':   return { ...state, tasks: [...tasks.slice(1), ...tasks.slice(0, 1)] };
    case 'refusing': return { ...state, refusing: arg };
    case 'resize':   return { ...state, tasks: numbered(arg), nextId: arg + 1 };
    default:         return state;
  }
}

export function refusal(state, [meaning, arg]) {
  const task = meaning === 'toggle' && state.refusing && state.tasks.find(task => task.id === arg);
  return task ? `The server refused the change to "${task.text}".` : '';
}

const ids = tasks => tasks.map(task => task.id);

const shown = task => ({
  ...task,
  status: task.done ? 'Done' : 'To do',
  tone: task.done ? 'success' : 'neutral',
});

export function view(state) {
  return {
    draft: state.draft,
    refusing: state.refusing,
    places: {
      tasks: ids(state.tasks.filter(task => !task.done)),
      done: ids(state.tasks.filter(task => task.done)),
    },
    rows: Object.fromEntries(state.tasks.map(task => [task.id, shown(task)])),
  };
}
