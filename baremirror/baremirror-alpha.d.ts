// baremirror-alpha.d.ts — the types of @vanelsas/baredom/baremirror-alpha. Written by hand.
// generate_types.bb copies this file into dist. test/types/baremirror.ts checks it.

import type { ForeignWrite } from "./foreign-writes";

// ── Templates ───────────────────────────────────────────────────────────────

/** What an attribute takes: text, a number or a boolean. `false`, `null` and `undefined` remove it. */
export type AttrValue = string | number | boolean | null | undefined;

/** A function of an item. It says where a value goes. */
export type Hole<Item> = (item: Item) => unknown;

/** A hole of an attribute. It gives what an attribute takes. */
export type AttrHole<Item> = (item: Item) => AttrValue;

/** Holes by name. */
export type Holes<Item> = Readonly<Record<string, Hole<Item>>>;

/** Attribute holes by name. */
export type AttrHoles<Item> = Readonly<Record<string, AttrHole<Item>>>;

/** A meaning, or a meaning and a function that gives the argument of the message. */
export type Meaning = string | readonly [meaning: string, argOf: (origin: Origin, e: Event) => unknown];

/** The attributes of an element of a template. */
export interface Attrs<Item> {
  /** The namespace of the element and of the elements below it. Never a hole. */
  xmlns?: string;
  /** An event type to its meaning. */
  on?: Readonly<Record<string, Meaning>>;
  /** A property name to its hole. */
  props?: Holes<Item>;
  [attr: string]: AttrValue | AttrHole<Item> | Readonly<Record<string, unknown>>;
}

/** A tag, optional attributes, and children: templates, text, numbers, or one hole as the only child. */
export type Template<Item> =
  readonly [tag: string, ...rest: (Attrs<Item> | Template<Item> | string | number | Hole<Item>)[]];

/** The attributes of a fixed template: no hole, no `on` and no `props`. */
export interface FixedAttrs {
  on?: never;
  props?: never;
  [attr: string]: AttrValue;
}

/** A template with no hole and no `on` or `props` entry. */
export type FixedTemplate = readonly [tag: string, ...rest: (FixedAttrs | FixedTemplate | string | number)[]];

// ── Parts ───────────────────────────────────────────────────────────────────

/** The write of one part. */
export interface Write {
  text?: unknown;
  attrs?: Readonly<Record<string, AttrValue>>;
  props?: Readonly<Record<string, unknown>>;
}

/** Writes by part name. */
export type Writes = Readonly<Record<string, Write>>;

/** Nodes by part name. */
export type Parts = Readonly<Record<string, Element>>;

/** The hole entry of one part. */
export interface HoleEntry<Item> {
  readonly part: string;
  readonly attrs?: AttrHoles<Item>;
  readonly text?: Hole<Item>;
  readonly props?: Holes<Item>;
}

/** An event type and a part name, with a meaning. */
export type EventEntry = readonly [readonly [eventType: string, part: string], Meaning];

/** What `splitTemplate` returns. Frozen. A copy of it is not a split template. */
export declare class SplitTemplate<Item> {
  private constructor();
  /** Ties the split template to the type of its item. */
  private readonly item: Item;
  readonly fixed: FixedTemplate;
  readonly holes: readonly HoleEntry<Item>[];
  readonly events: readonly EventEntry[];
  /** The writes of `item`, by part name. */
  writes(item: Item): Writes;
}

/** A hole of a split template that a component may also write. */
export interface SharedHole {
  readonly part: string;
  readonly tag: string;
  readonly attr: string;
  readonly writer: string;
  readonly target: string;
}

export function splitTemplate<Item>(name: string, template: Template<Item>): SplitTemplate<Item>;
export function sharedHoles<Item>(
  split: SplitTemplate<Item>,
  foreignWrites: Readonly<Record<string, readonly ForeignWrite[]>>,
): SharedHole[];
export function makeNode(fixed: FixedTemplate): Element;
export function nodeMaker(fixed: FixedTemplate): (...ignored: unknown[]) => Element;
export function readParts(node: Element): Parts;
export function writeParts(parts: Parts, writes: Writes): void;
export function writeItem<Item>(node: Element, split: SplitTemplate<Item>, item: Item): void;
export function setAttrs(el: Element, attrs: Readonly<Record<string, AttrValue>>): void;
export function setText(el: Element, text: unknown): void;
export function withOneRender<E extends Element>(el: E, work: (el: E) => unknown): void;

// ── Places ──────────────────────────────────────────────────────────────────

/** A parent with an optional anchor, the key of a node with an optional part, or a part alone. */
export type Container =
  | { readonly parent: Element; readonly before?: Node | null }
  | { readonly key: string; readonly part?: string }
  | { readonly part: string };

/** Containers by name. */
export type Containers = Readonly<Record<string, Container>>;

/** The keys of each container, in order. */
export type Places = Readonly<Record<string, readonly string[]>>;

/** Nodes by key. */
export type Nodes = Readonly<Record<string, Element>>;

/** What `readPlaces` returns. Frozen. */
export interface Reading {
  readonly containers: Containers;
  readonly places: Places;
  readonly nodes: Nodes;
}

/** One placement of a plan: a key, its container, and the key it goes before, or null. */
export interface Placement {
  readonly key: string;
  readonly in: string;
  readonly before: string | null;
}

/** What `plan` returns. Frozen. */
export interface Plan {
  readonly remove: readonly string[];
  readonly place: readonly Placement[];
}

/** Makes the node of a new key. */
export type Make = (key: string) => Element;

export function plan(current: Places, wanted: Places): Plan;
export function readPlaces(containers: Containers): Reading;
export function perform(reading: Reading, plan: Plan, make: Make): Nodes;
export function syncPlaces(containers: Containers, wanted: Places, make: Make): Nodes;
export function release(node: Element): void;

// ── Events ──────────────────────────────────────────────────────────────────

/** The keys on the path of an event, outermost first, and its nearest part. */
export interface Origin {
  readonly keyPath: readonly string[];
  readonly part: string | null;
  readonly node: Element | null;
}

/** What the application receives: a meaning and an optional argument. */
export type Message = readonly [meaning: string, arg?: unknown];

export interface ListenOptions {
  dispatch: (message: Message) => unknown;
  events: readonly EventEntry[];
  requestedValues?: typeof import("./requests").requestedValues;
}

/** The state holder of `dispatcher`. */
export interface Store<State> {
  value: State;
}

export function readOrigin(e: Event): Origin;
/** Returns a function that removes the listeners. */
export function listen(root: Node, options: ListenOptions): () => void;
export function dispatcher<State, View>(
  store: Store<State>,
  step: (state: State, message: Message) => State,
  view: (state: State) => View,
  render: (view: View) => unknown,
): (message: Message) => State;

// ── Elements ────────────────────────────────────────────────────────────────

export interface ElementOptions<A extends string> {
  /** The attributes the element shows. A hole gets them as an item, `null` when absent. */
  attrs: readonly A[];
  template: Template<Readonly<Record<A, string | null>>>;
  css?: string;
}

export function defineElement<A extends string>(tag: string, options: ElementOptions<A>): void;
