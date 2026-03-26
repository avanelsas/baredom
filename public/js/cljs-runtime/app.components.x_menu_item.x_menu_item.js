goog.provide('app.components.x_menu_item.x_menu_item');
goog.scope(function(){
  app.components.x_menu_item.x_menu_item.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_menu_item.x_menu_item.key_refs = "__xMenuItemRefs";
app.components.x_menu_item.x_menu_item.key_handlers = "__xMenuItemHandlers";
app.components.x_menu_item.x_menu_item.key_init = "__xMenuItemInit";
app.components.x_menu_item.x_menu_item.getv = (function app$components$x_menu_item$x_menu_item$getv(el,k){
return app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(el,k);
});
app.components.x_menu_item.x_menu_item.setv_BANG_ = (function app$components$x_menu_item$x_menu_item$setv_BANG_(el,k,v){
return app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(el,k,v);
});
app.components.x_menu_item.x_menu_item.initialized_QMARK_ = (function app$components$x_menu_item$x_menu_item$initialized_QMARK_(el){
return app.components.x_menu_item.x_menu_item.getv(el,app.components.x_menu_item.x_menu_item.key_init) === true;
});
app.components.x_menu_item.x_menu_item.mark_initialized_BANG_ = (function app$components$x_menu_item$x_menu_item$mark_initialized_BANG_(el){
return app.components.x_menu_item.x_menu_item.setv_BANG_(el,app.components.x_menu_item.x_menu_item.key_init,true);
});
app.components.x_menu_item.x_menu_item.style_text = (""+":host{"+"display:block;outline:none;color-scheme:light dark;"+"--x-menu-item-color:#111827;"+"--x-menu-item-hover-bg:#f3f4f6;"+"--x-menu-item-focus-bg:#eff6ff;"+"--x-menu-item-focus-color:#1d4ed8;"+"--x-menu-item-disabled-opacity:0.45;"+"--x-menu-item-danger-color:#dc2626;"+"--x-menu-item-danger-hover-bg:#fef2f2;"+"--x-menu-item-padding:8px 12px;"+"--x-menu-item-border-radius:4px;"+"--x-menu-item-font-size:0.9375rem;"+"--x-menu-item-icon-gap:8px;"+"--x-menu-item-divider-color:#e5e7eb;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-menu-item-color:#f9fafb;"+"--x-menu-item-hover-bg:#1f2937;"+"--x-menu-item-focus-bg:#1e3a5f;"+"--x-menu-item-focus-color:#60a5fa;"+"--x-menu-item-danger-color:#f87171;"+"--x-menu-item-danger-hover-bg:#2d1515;"+"--x-menu-item-divider-color:#374151;}}"+".base{"+"display:flex;align-items:center;gap:var(--x-menu-item-icon-gap);"+"padding:var(--x-menu-item-padding);"+"border-radius:var(--x-menu-item-border-radius);"+"font-size:var(--x-menu-item-font-size);"+"color:var(--x-menu-item-color);"+"cursor:pointer;user-select:none;"+"transition:background 100ms ease,color 100ms ease;}"+".base:hover{background:var(--x-menu-item-hover-bg);}"+":host(:focus) .base{background:var(--x-menu-item-focus-bg);color:var(--x-menu-item-focus-color);}"+".base[data-variant='danger']{color:var(--x-menu-item-danger-color);}"+".base[data-variant='danger']:hover{background:var(--x-menu-item-danger-hover-bg);}"+":host(:focus) .base[data-variant='danger']{background:var(--x-menu-item-danger-hover-bg);}"+".base[data-disabled='true']{opacity:var(--x-menu-item-disabled-opacity);cursor:default;pointer-events:none;}"+".base[data-type='divider']{padding:0;cursor:default;pointer-events:none;}"+".icon-span{display:none;flex-shrink:0;align-items:center;}"+":host([has-icon]) .icon-span{display:flex;}"+".divider-hr{display:none;}"+".base[data-type='divider'] .icon-span,.base[data-type='divider'] .label-span{display:none;}"+".base[data-type='divider'] .divider-hr{"+"display:block;width:100%;border:none;"+"border-top:1px solid var(--x-menu-item-divider-color);margin:4px 0;}"+"@media (prefers-reduced-motion:reduce){.base{transition:none;}}");
app.components.x_menu_item.x_menu_item.read_inputs = (function app$components$x_menu_item$x_menu_item$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),el.getAttribute(app.components.x_menu_item.model.attr_value),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),el.hasAttribute(app.components.x_menu_item.model.attr_disabled),new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_menu_item.model.attr_variant),new cljs.core.Keyword(null,"type","type",1174270348),el.getAttribute(app.components.x_menu_item.model.attr_type)], null);
});
app.components.x_menu_item.x_menu_item.dispatch_item_select_BANG_ = (function app$components$x_menu_item$x_menu_item$dispatch_item_select_BANG_(el){
var value = el.getAttribute(app.components.x_menu_item.model.attr_value);
return el.dispatchEvent((new CustomEvent(app.components.x_menu_item.model.event_item_select,({"detail": ({"value": (function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()}), "bubbles": true, "composed": true}))));
});
app.components.x_menu_item.x_menu_item.handle_click_BANG_ = (function app$components$x_menu_item$x_menu_item$handle_click_BANG_(el,evt){
var state = app.components.x_menu_item.model.derive_state(app.components.x_menu_item.x_menu_item.read_inputs(el));
if(cljs.core.truth_((function (){var or__5142__auto__ = new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(state),"divider");
}
})())){
evt.preventDefault();
} else {
}

if(((cljs.core.not(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(state),"divider")))){
return app.components.x_menu_item.x_menu_item.dispatch_item_select_BANG_(el);
} else {
return null;
}
});
app.components.x_menu_item.x_menu_item.handle_keydown_BANG_ = (function app$components$x_menu_item$x_menu_item$handle_keydown_BANG_(el,evt){
var k = evt.key;
var state = app.components.x_menu_item.model.derive_state(app.components.x_menu_item.x_menu_item.read_inputs(el));
if(((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k," ")))) && (((cljs.core.not(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(state),"divider")))))){
evt.preventDefault();

return app.components.x_menu_item.x_menu_item.dispatch_item_select_BANG_(el);
} else {
return null;
}
});
app.components.x_menu_item.x_menu_item.handle_icon_slotchange_BANG_ = (function app$components$x_menu_item$x_menu_item$handle_icon_slotchange_BANG_(el,slot){
var nodes = slot.assignedNodes();
if((nodes.length > (0))){
return el.setAttribute("has-icon","");
} else {
return el.removeAttribute("has-icon");
}
});
app.components.x_menu_item.x_menu_item.render_BANG_ = (function app$components$x_menu_item$x_menu_item$render_BANG_(el){
var refs = app.components.x_menu_item.x_menu_item.getv(el,app.components.x_menu_item.x_menu_item.key_refs);
var base = (cljs.core.truth_(refs)?app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(refs,"base"):null);
var state = app.components.x_menu_item.model.derive_state(app.components.x_menu_item.x_menu_item.read_inputs(el));
if(cljs.core.truth_(base)){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(state),"divider")){
el.setAttribute("role","separator");

el.removeAttribute("tabindex");

el.removeAttribute("aria-disabled");

base.setAttribute("data-type","divider");

base.setAttribute("data-variant","");

return base.setAttribute("data-disabled","false");
} else {
el.setAttribute("role","menuitem");

el.setAttribute("tabindex","-1");

base.setAttribute("data-type","");

base.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-disabled",(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))?"true":"false"));

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))){
return el.setAttribute("aria-disabled","true");
} else {
return el.removeAttribute("aria-disabled");
}
}
} else {
return null;
}
});
app.components.x_menu_item.x_menu_item.init_dom_BANG_ = (function app$components$x_menu_item$x_menu_item$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var icon_span = document.createElement("span");
var icon_slot = document.createElement("slot");
var label_span = document.createElement("span");
var label_slot = document.createElement("slot");
var divider_hr = document.createElement("hr");
(style.textContent = app.components.x_menu_item.x_menu_item.style_text);

base.setAttribute("part","base");

(base.className = "base");

icon_span.setAttribute("part","icon");

(icon_span.className = "icon-span");

icon_slot.setAttribute("name","icon");

icon_span.appendChild(icon_slot);

(label_span.className = "label-span");

label_span.appendChild(label_slot);

(divider_hr.className = "divider-hr");

base.appendChild(icon_span);

base.appendChild(label_span);

base.appendChild(divider_hr);

root.appendChild(style);

root.appendChild(base);

var refs = ({});
app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(refs,"base",base);

app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(refs,"icon-span",icon_span);

app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(refs,"icon-slot",icon_slot);

return app.components.x_menu_item.x_menu_item.setv_BANG_(el,app.components.x_menu_item.x_menu_item.key_refs,refs);
});
app.components.x_menu_item.x_menu_item.install_listeners_BANG_ = (function app$components$x_menu_item$x_menu_item$install_listeners_BANG_(el){
var refs = app.components.x_menu_item.x_menu_item.getv(el,app.components.x_menu_item.x_menu_item.key_refs);
var icon_slot = app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(refs,"icon-slot");
var on_click = (function (e){
return app.components.x_menu_item.x_menu_item.handle_click_BANG_(el,e);
});
var on_keydown = (function (e){
return app.components.x_menu_item.x_menu_item.handle_keydown_BANG_(el,e);
});
var on_slotchange = (function (_){
return app.components.x_menu_item.x_menu_item.handle_icon_slotchange_BANG_(el,icon_slot);
});
var handlers = ({});
el.addEventListener("click",on_click);

el.addEventListener("keydown",on_keydown);

icon_slot.addEventListener("slotchange",on_slotchange);

app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(handlers,"click",on_click);

app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(handlers,"keydown",on_keydown);

app.components.x_menu_item.x_menu_item.goog$module$goog$object.set(handlers,"icon-slotchange",on_slotchange);

return app.components.x_menu_item.x_menu_item.setv_BANG_(el,app.components.x_menu_item.x_menu_item.key_handlers,handlers);
});
app.components.x_menu_item.x_menu_item.remove_listeners_BANG_ = (function app$components$x_menu_item$x_menu_item$remove_listeners_BANG_(el){
var handlers = app.components.x_menu_item.x_menu_item.getv(el,app.components.x_menu_item.x_menu_item.key_handlers);
var refs = app.components.x_menu_item.x_menu_item.getv(el,app.components.x_menu_item.x_menu_item.key_refs);
if(cljs.core.truth_(handlers)){
var on_click = app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(handlers,"click");
var on_keydown = app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(handlers,"keydown");
var on_slotchange = app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(handlers,"icon-slotchange");
var icon_slot = (cljs.core.truth_(refs)?app.components.x_menu_item.x_menu_item.goog$module$goog$object.get(refs,"icon-slot"):null);
el.removeEventListener("click",on_click);

el.removeEventListener("keydown",on_keydown);

if(cljs.core.truth_(icon_slot)){
return icon_slot.removeEventListener("slotchange",on_slotchange);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_menu_item.x_menu_item.init_element_BANG_ = (function app$components$x_menu_item$x_menu_item$init_element_BANG_(el){
if(app.components.x_menu_item.x_menu_item.initialized_QMARK_(el)){
} else {
app.components.x_menu_item.x_menu_item.init_dom_BANG_(el);

app.components.x_menu_item.x_menu_item.install_listeners_BANG_(el);

app.components.x_menu_item.x_menu_item.mark_initialized_BANG_(el);
}

app.components.x_menu_item.x_menu_item.render_BANG_(el);

return el;
});
app.components.x_menu_item.x_menu_item.connected_callback = (function app$components$x_menu_item$x_menu_item$connected_callback(el){
if(app.components.x_menu_item.x_menu_item.initialized_QMARK_(el)){
app.components.x_menu_item.x_menu_item.install_listeners_BANG_(el);

return app.components.x_menu_item.x_menu_item.render_BANG_(el);
} else {
return app.components.x_menu_item.x_menu_item.init_element_BANG_(el);
}
});
app.components.x_menu_item.x_menu_item.disconnected_callback = (function app$components$x_menu_item$x_menu_item$disconnected_callback(el){
return app.components.x_menu_item.x_menu_item.remove_listeners_BANG_(el);
});
app.components.x_menu_item.x_menu_item.attribute_changed_callback = (function app$components$x_menu_item$x_menu_item$attribute_changed_callback(el,_name,_old,_new){
if(app.components.x_menu_item.x_menu_item.initialized_QMARK_(el)){
return app.components.x_menu_item.x_menu_item.render_BANG_(el);
} else {
return null;
}
});
app.components.x_menu_item.x_menu_item.install_property_accessors_BANG_ = (function app$components$x_menu_item$x_menu_item$install_property_accessors_BANG_(klass){
Object.defineProperty(klass.prototype,"value",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_menu_item.model.attr_value);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu_item.model.attr_value,v);
} else {
return this$.removeAttribute(app.components.x_menu_item.model.attr_value);
}
}), "configurable": true, "enumerable": true}));

Object.defineProperty(klass.prototype,"disabled",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_menu_item.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu_item.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_menu_item.model.attr_disabled);
}
}), "configurable": true, "enumerable": true}));

Object.defineProperty(klass.prototype,"variant",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_menu_item.model.attr_variant);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu_item.model.attr_variant,v);
} else {
return this$.removeAttribute(app.components.x_menu_item.model.attr_variant);
}
}), "configurable": true, "enumerable": true}));

return Object.defineProperty(klass.prototype,"type",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_menu_item.model.attr_type);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu_item.model.attr_type,v);
} else {
return this$.removeAttribute(app.components.x_menu_item.model.attr_type);
}
}), "configurable": true, "enumerable": true}));
});
app.components.x_menu_item.x_menu_item.element_class = (function app$components$x_menu_item$x_menu_item$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_menu_item.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_menu_item.x_menu_item.connected_callback(this$);
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_menu_item.x_menu_item.disconnected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
return app.components.x_menu_item.x_menu_item.attribute_changed_callback(this$,n,o,v);
}));

app.components.x_menu_item.x_menu_item.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_menu_item.x_menu_item.register_BANG_ = (function app$components$x_menu_item$x_menu_item$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_menu_item.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_menu_item.model.tag_name,app.components.x_menu_item.x_menu_item.element_class());
}
});
app.components.x_menu_item.x_menu_item.init_BANG_ = (function app$components$x_menu_item$x_menu_item$init_BANG_(){
return app.components.x_menu_item.x_menu_item.register_BANG_();
});

//# sourceMappingURL=app.components.x_menu_item.x_menu_item.js.map
