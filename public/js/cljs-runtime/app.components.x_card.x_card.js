goog.provide('app.components.x_card.x_card');
goog.scope(function(){
  app.components.x_card.x_card.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_card.x_card.key_root = "__x_card_root";
app.components.x_card.x_card.key_style = "__x_card_style";
app.components.x_card.x_card.key_base = "__x_card_base";
app.components.x_card.x_card.key_slot = "__x_card_slot";
app.components.x_card.x_card.key_initialized = "__x_card_initialized";
app.components.x_card.x_card.get_instance_value = (function app$components$x_card$x_card$get_instance_value(el,key){
return app.components.x_card.x_card.goog$module$goog$object.get(el,key);
});
app.components.x_card.x_card.set_instance_value_BANG_ = (function app$components$x_card$x_card$set_instance_value_BANG_(el,key,value){
return app.components.x_card.x_card.goog$module$goog$object.set(el,key,value);
});
app.components.x_card.x_card.initialized_QMARK_ = (function app$components$x_card$x_card$initialized_QMARK_(el){
return app.components.x_card.x_card.get_instance_value(el,app.components.x_card.x_card.key_initialized) === true;
});
app.components.x_card.x_card.mark_initialized_BANG_ = (function app$components$x_card$x_card$mark_initialized_BANG_(el){
return app.components.x_card.x_card.set_instance_value_BANG_(el,app.components.x_card.x_card.key_initialized,true);
});
app.components.x_card.x_card.shadow_root_of = (function app$components$x_card$x_card$shadow_root_of(el){
return app.components.x_card.x_card.get_instance_value(el,app.components.x_card.x_card.key_root);
});
app.components.x_card.x_card.base_node_of = (function app$components$x_card$x_card$base_node_of(el){
return app.components.x_card.x_card.get_instance_value(el,app.components.x_card.x_card.key_base);
});
app.components.x_card.x_card.read_inputs = (function app$components$x_card$x_card$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_card.model.attr_variant),new cljs.core.Keyword(null,"padding","padding",1660304693),el.getAttribute(app.components.x_card.model.attr_padding),new cljs.core.Keyword(null,"radius","radius",-2073122258),el.getAttribute(app.components.x_card.model.attr_radius),new cljs.core.Keyword(null,"interactive","interactive",-2024078362),el.hasAttribute(app.components.x_card.model.attr_interactive),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),el.hasAttribute(app.components.x_card.model.attr_disabled),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_card.model.attr_label)], null);
});
app.components.x_card.x_card.interactive_active_QMARK_ = (function app$components$x_card$x_card$interactive_active_QMARK_(state){
var and__5140__auto__ = new cljs.core.Keyword(null,"interactive","interactive",-2024078362).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state));
} else {
return and__5140__auto__;
}
});
app.components.x_card.x_card.dispatch_press_BANG_ = (function app$components$x_card$x_card$dispatch_press_BANG_(el){
return el.dispatchEvent((new CustomEvent(app.components.x_card.model.event_press,({"detail": ({}), "bubbles": true, "composed": true}))));
});
app.components.x_card.x_card.set_or_remove_attr_BANG_ = (function app$components$x_card$x_card$set_or_remove_attr_BANG_(el,name,value){
if((!((value == null)))){
return el.setAttribute(name,value);
} else {
return el.removeAttribute(name);
}
});
app.components.x_card.x_card.reflect_host_a11y_BANG_ = (function app$components$x_card$x_card$reflect_host_a11y_BANG_(el,state){
app.components.x_card.x_card.set_or_remove_attr_BANG_(el,"role",new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(state));

app.components.x_card.x_card.set_or_remove_attr_BANG_(el,"tabindex",new cljs.core.Keyword(null,"tabindex","tabindex",338877510).cljs$core$IFn$_invoke$arity$1(state));

app.components.x_card.x_card.set_or_remove_attr_BANG_(el,"aria-label",new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(state));

return app.components.x_card.x_card.set_or_remove_attr_BANG_(el,"aria-disabled",new cljs.core.Keyword(null,"aria-disabled","aria-disabled",-667357160).cljs$core$IFn$_invoke$arity$1(state));
});
app.components.x_card.x_card.reflect_base_state_BANG_ = (function app$components$x_card$x_card$reflect_base_state_BANG_(base,state){
base.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-padding",new cljs.core.Keyword(null,"padding","padding",1660304693).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-radius",new cljs.core.Keyword(null,"radius","radius",-2073122258).cljs$core$IFn$_invoke$arity$1(state));

app.components.x_card.x_card.set_or_remove_attr_BANG_(base,"data-interactive",(cljs.core.truth_(new cljs.core.Keyword(null,"interactive","interactive",-2024078362).cljs$core$IFn$_invoke$arity$1(state))?"true":null));

return app.components.x_card.x_card.set_or_remove_attr_BANG_(base,"data-disabled",(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))?"true":null));
});
app.components.x_card.x_card.style_text = (function app$components$x_card$x_card$style_text(){
return "\n  :host {\n  display: block;\n  color-scheme: light dark;\n\n  --x-card-background: rgba(255, 255, 255, 0.92);\n  --x-card-color: #111827;\n  --x-card-border-color: rgba(17, 24, 39, 0.12);\n  --x-card-filled-background: rgba(241, 245, 249, 0.96);\n  --x-card-ghost-background: transparent;\n  --x-card-hover-background: rgba(15, 23, 42, 0.04);\n  --x-card-press-background: rgba(15, 23, 42, 0.08);\n  --x-card-shadow: 0 10px 24px rgba(15, 23, 42, 0.10);\n  --x-card-focus-ring: rgba(59, 130, 246, 0.55);\n  --x-card-disabled-opacity: 0.6;\n\n  --x-card-padding-none: 0;\n  --x-card-padding-sm: 0.5rem;\n  --x-card-padding-md: 1rem;\n  --x-card-padding-lg: 1.5rem;\n\n  --x-card-radius-none: 0;\n  --x-card-radius-sm: 0.375rem;\n  --x-card-radius-md: 0.75rem;\n  --x-card-radius-lg: 1rem;\n  --x-card-radius-xl: 1.5rem;\n\n  --x-card-transition-duration: 140ms;\n  --x-card-transition-timing: ease;\n\n  outline: none;\n  }\n\n  @media (prefers-color-scheme: dark) {\n  :host {\n  --x-card-background: rgba(15, 23, 42, 0.88);\n  --x-card-color: #e5e7eb;\n  --x-card-border-color: rgba(148, 163, 184, 0.24);\n  --x-card-filled-background: rgba(30, 41, 59, 0.96);\n  --x-card-hover-background: rgba(148, 163, 184, 0.10);\n  --x-card-press-background: rgba(148, 163, 184, 0.16);\n  --x-card-shadow: 0 16px 32px rgba(0, 0, 0, 0.32);\n  --x-card-focus-ring: rgba(96, 165, 250, 0.6);\n  }\n  }\n\n  .base {\n  box-sizing: border-box;\n  display: block;\n  min-width: 0;\n  background: var(--x-card-background);\n  color: var(--x-card-color);\n  border: 1px solid transparent;\n  border-radius: var(--x-card-radius-lg);\n  transition:\n  background var(--x-card-transition-duration) var(--x-card-transition-timing),\n  box-shadow var(--x-card-transition-duration) var(--x-card-transition-timing),\n  border-color var(--x-card-transition-duration) var(--x-card-transition-timing),\n  transform var(--x-card-transition-duration) var(--x-card-transition-timing);\n  }\n\n  .base[data-variant='elevated'] {\n  background: var(--x-card-background);\n  box-shadow: var(--x-card-shadow);\n  }\n\n  .base[data-variant='outlined'] {\n  background: var(--x-card-background);\n  border-color: var(--x-card-border-color);\n  box-shadow: none;\n  }\n\n  .base[data-variant='filled'] {\n  background: var(--x-card-filled-background);\n  box-shadow: none;\n  }\n\n  .base[data-variant='ghost'] {\n  background: var(--x-card-ghost-background);\n  box-shadow: none;\n  }\n\n  .base[data-padding='none'] { padding: var(--x-card-padding-none); }\n  .base[data-padding='sm']   { padding: var(--x-card-padding-sm); }\n  .base[data-padding='md']   { padding: var(--x-card-padding-md); }\n  .base[data-padding='lg']   { padding: var(--x-card-padding-lg); }\n\n  .base[data-radius='none'] { border-radius: var(--x-card-radius-none); }\n  .base[data-radius='sm']   { border-radius: var(--x-card-radius-sm); }\n  .base[data-radius='md']   { border-radius: var(--x-card-radius-md); }\n  .base[data-radius='lg']   { border-radius: var(--x-card-radius-lg); }\n  .base[data-radius='xl']   { border-radius: var(--x-card-radius-xl); }\n\n  .base[data-interactive='true']:not([data-disabled='true']) {\n  cursor: pointer;\n  }\n\n  .base[data-interactive='true']:not([data-disabled='true']):hover {\n  background: var(--x-card-hover-background);\n  }\n\n  .base[data-interactive='true']:not([data-disabled='true']):active {\n  background: var(--x-card-press-background);\n  transform: translateY(1px);\n  }\n\n  .base[data-disabled='true'] {\n  opacity: var(--x-card-disabled-opacity);\n  cursor: default;\n  transform: none;\n  }\n\n  :host(:focus-visible) .base[data-interactive='true']:not([data-disabled='true']) {\n  box-shadow:\n  0 0 0 3px var(--x-card-focus-ring),\n  var(--x-card-shadow);\n  }\n\n  @media (prefers-reduced-motion: reduce) {\n  .base {\n  transition: none;\n  }\n\n  .base[data-interactive='true']:not([data-disabled='true']):active {\n  transform: none;\n  }\n  }\n  ";
});
app.components.x_card.x_card.create_style_node = (function app$components$x_card$x_card$create_style_node(){
var node = document.createElement("style");
(node.textContent = app.components.x_card.x_card.style_text());

return node;
});
app.components.x_card.x_card.create_base_node = (function app$components$x_card$x_card$create_base_node(){
var node = document.createElement("div");
node.setAttribute("part","base");

(node.className = "base");

return node;
});
app.components.x_card.x_card.create_slot_node = (function app$components$x_card$x_card$create_slot_node(){
return document.createElement("slot");
});
app.components.x_card.x_card.init_shadow_dom_BANG_ = (function app$components$x_card$x_card$init_shadow_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_node = app.components.x_card.x_card.create_style_node();
var base_node = app.components.x_card.x_card.create_base_node();
var slot_node = app.components.x_card.x_card.create_slot_node();
base_node.appendChild(slot_node);

root.appendChild(style_node);

root.appendChild(base_node);

app.components.x_card.x_card.set_instance_value_BANG_(el,app.components.x_card.x_card.key_root,root);

app.components.x_card.x_card.set_instance_value_BANG_(el,app.components.x_card.x_card.key_style,style_node);

app.components.x_card.x_card.set_instance_value_BANG_(el,app.components.x_card.x_card.key_base,base_node);

app.components.x_card.x_card.set_instance_value_BANG_(el,app.components.x_card.x_card.key_slot,slot_node);

return root;
});
app.components.x_card.x_card.render_BANG_ = (function app$components$x_card$x_card$render_BANG_(el){
var state = app.components.x_card.model.derive_state(app.components.x_card.x_card.read_inputs(el));
var base = app.components.x_card.x_card.base_node_of(el);
app.components.x_card.x_card.reflect_host_a11y_BANG_(el,state);

return app.components.x_card.x_card.reflect_base_state_BANG_(base,state);
});
app.components.x_card.x_card.on_click = (function app$components$x_card$x_card$on_click(el,_event){
var state = app.components.x_card.model.derive_state(app.components.x_card.x_card.read_inputs(el));
if(cljs.core.truth_(app.components.x_card.x_card.interactive_active_QMARK_(state))){
return app.components.x_card.x_card.dispatch_press_BANG_(el);
} else {
return null;
}
});
app.components.x_card.x_card.on_keydown = (function app$components$x_card$x_card$on_keydown(el,event){
var state = app.components.x_card.model.derive_state(app.components.x_card.x_card.read_inputs(el));
var key = event.key;
if(cljs.core.truth_(app.components.x_card.x_card.interactive_active_QMARK_(state))){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")){
return app.components.x_card.x_card.dispatch_press_BANG_(el);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")){
event.preventDefault();

return app.components.x_card.x_card.dispatch_press_BANG_(el);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Spacebar")){
event.preventDefault();

return app.components.x_card.x_card.dispatch_press_BANG_(el);
} else {
return null;

}
}
}
} else {
return null;
}
});
app.components.x_card.x_card.install_listeners_BANG_ = (function app$components$x_card$x_card$install_listeners_BANG_(el){
el.addEventListener("click",(function (event){
return app.components.x_card.x_card.on_click(el,event);
}));

return el.addEventListener("keydown",(function (event){
return app.components.x_card.x_card.on_keydown(el,event);
}));
});
app.components.x_card.x_card.init_element_BANG_ = (function app$components$x_card$x_card$init_element_BANG_(el){
if(app.components.x_card.x_card.initialized_QMARK_(el)){
} else {
app.components.x_card.x_card.init_shadow_dom_BANG_(el);

app.components.x_card.x_card.install_listeners_BANG_(el);

app.components.x_card.x_card.mark_initialized_BANG_(el);
}

app.components.x_card.x_card.render_BANG_(el);

return el;
});
app.components.x_card.x_card.connected_callback = (function app$components$x_card$x_card$connected_callback(el){
return app.components.x_card.x_card.init_element_BANG_(el);
});
app.components.x_card.x_card.disconnected_callback = (function app$components$x_card$x_card$disconnected_callback(_el){
return null;
});
app.components.x_card.x_card.attribute_changed_callback = (function app$components$x_card$x_card$attribute_changed_callback(el,_name,_old_value,_new_value){
if(app.components.x_card.x_card.initialized_QMARK_(el)){
return app.components.x_card.x_card.render_BANG_(el);
} else {
return null;
}
});
app.components.x_card.x_card.install_property_accessors_BANG_ = (function app$components$x_card$x_card$install_property_accessors_BANG_(klass){
Object.defineProperty(klass.prototype,"interactive",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_card.model.attr_interactive);
}), "set": (function (value){
var this$ = this;
if(cljs.core.truth_(value)){
return this$.setAttribute(app.components.x_card.model.attr_interactive,"");
} else {
return this$.removeAttribute(app.components.x_card.model.attr_interactive);
}
})}));

return Object.defineProperty(klass.prototype,"disabled",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_card.model.attr_disabled);
}), "set": (function (value){
var this$ = this;
if(cljs.core.truth_(value)){
return this$.setAttribute(app.components.x_card.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_card.model.attr_disabled);
}
})}));
});
app.components.x_card.x_card.element_class = (function app$components$x_card$x_card$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_card.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_card.x_card.connected_callback(this$);
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_card.x_card.disconnected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_card.x_card.attribute_changed_callback(this$,name,old_value,new_value);
}));

app.components.x_card.x_card.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_card.x_card.register_BANG_ = (function app$components$x_card$x_card$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_card.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_card.model.tag_name,app.components.x_card.x_card.element_class());
}
});
app.components.x_card.x_card.init_BANG_ = (function app$components$x_card$x_card$init_BANG_(){
return app.components.x_card.x_card.register_BANG_();
});

//# sourceMappingURL=app.components.x_card.x_card.js.map
