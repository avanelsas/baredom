goog.provide('app.components.x_menu.x_menu');
goog.scope(function(){
  app.components.x_menu.x_menu.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_menu.x_menu.key_refs = "__xMenuRefs";
app.components.x_menu.x_menu.key_handlers = "__xMenuHandlers";
app.components.x_menu.x_menu.key_init = "__xMenuInit";
app.components.x_menu.x_menu.getv = (function app$components$x_menu$x_menu$getv(el,k){
return app.components.x_menu.x_menu.goog$module$goog$object.get(el,k);
});
app.components.x_menu.x_menu.setv_BANG_ = (function app$components$x_menu$x_menu$setv_BANG_(el,k,v){
return app.components.x_menu.x_menu.goog$module$goog$object.set(el,k,v);
});
app.components.x_menu.x_menu.initialized_QMARK_ = (function app$components$x_menu$x_menu$initialized_QMARK_(el){
return app.components.x_menu.x_menu.getv(el,app.components.x_menu.x_menu.key_init) === true;
});
app.components.x_menu.x_menu.mark_initialized_BANG_ = (function app$components$x_menu$x_menu$mark_initialized_BANG_(el){
return app.components.x_menu.x_menu.setv_BANG_(el,app.components.x_menu.x_menu.key_init,true);
});
app.components.x_menu.x_menu.style_text = (""+":host{"+"display:inline-block;position:relative;color-scheme:light dark;"+"--x-menu-bg:#ffffff;"+"--x-menu-border:1px solid #e5e7eb;"+"--x-menu-border-radius:8px;"+"--x-menu-shadow:0 4px 16px rgba(0,0,0,0.12);"+"--x-menu-min-width:160px;"+"--x-menu-padding:4px;"+"--x-menu-z-index:1000;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-menu-bg:#1f2937;"+"--x-menu-border:1px solid #374151;"+"--x-menu-shadow:0 4px 16px rgba(0,0,0,0.4);}}"+".base{position:relative;display:inline-block;}"+".popup{"+"display:none;position:absolute;"+"background:var(--x-menu-bg);"+"border:var(--x-menu-border);"+"border-radius:var(--x-menu-border-radius);"+"box-shadow:var(--x-menu-shadow);"+"min-width:var(--x-menu-min-width);"+"padding:var(--x-menu-padding);"+"z-index:var(--x-menu-z-index);"+"box-sizing:border-box;}"+":host([open]) .popup{display:block;}"+".popup[data-placement='bottom-start']{top:100%;left:0;margin-top:4px;}"+".popup[data-placement='bottom-end']{top:100%;right:0;margin-top:4px;}"+".popup[data-placement='top-start']{bottom:100%;left:0;margin-bottom:4px;}"+".popup[data-placement='top-end']{bottom:100%;right:0;margin-bottom:4px;}");
app.components.x_menu.x_menu.read_inputs = (function app$components$x_menu$x_menu$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open","open",-1763596448),el.hasAttribute(app.components.x_menu.model.attr_open),new cljs.core.Keyword(null,"placement","placement",768366651),el.getAttribute(app.components.x_menu.model.attr_placement),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_menu.model.attr_label)], null);
});
app.components.x_menu.x_menu.get_focusable_items = (function app$components$x_menu$x_menu$get_focusable_items(el){
var all = el.querySelectorAll("x-menu-item");
var result = [];
var n__5741__auto___22796 = all.length;
var i_22797 = (0);
while(true){
if((i_22797 < n__5741__auto___22796)){
var item_22798 = (all[i_22797]);
if(((cljs.core.not(item_22798.hasAttribute("disabled"))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(item_22798.getAttribute("type"),"divider")))){
result.push(item_22798);
} else {
}

var G__22799 = (i_22797 + (1));
i_22797 = G__22799;
continue;
} else {
}
break;
}

return result;
});
app.components.x_menu.x_menu.focus_item_BANG_ = (function app$components$x_menu$x_menu$focus_item_BANG_(item){
return item.focus();
});
app.components.x_menu.x_menu.get_trigger = (function app$components$x_menu$x_menu$get_trigger(el){
var refs = app.components.x_menu.x_menu.getv(el,app.components.x_menu.x_menu.key_refs);
var trigger_slot = (cljs.core.truth_(refs)?app.components.x_menu.x_menu.goog$module$goog$object.get(refs,"trigger-slot"):null);
var assigned = (cljs.core.truth_(trigger_slot)?trigger_slot.assignedElements():null);
if(cljs.core.truth_((function (){var and__5140__auto__ = assigned;
if(cljs.core.truth_(and__5140__auto__)){
return (assigned.length > (0));
} else {
return and__5140__auto__;
}
})())){
return (assigned[(0)]);
} else {
return null;
}
});
app.components.x_menu.x_menu.dispatch_event_BANG_ = (function app$components$x_menu$x_menu$dispatch_event_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true}))));
});
app.components.x_menu.x_menu.open_menu_BANG_ = (function app$components$x_menu$x_menu$open_menu_BANG_(el,focus_target){
if(cljs.core.truth_(el.hasAttribute(app.components.x_menu.model.attr_open))){
} else {
el.setAttribute(app.components.x_menu.model.attr_open,"");

app.components.x_menu.x_menu.dispatch_event_BANG_(el,app.components.x_menu.model.event_open,({}));
}

if(cljs.core.truth_(focus_target)){
var items = app.components.x_menu.x_menu.get_focusable_items(el);
var n = items.length;
if((n > (0))){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(focus_target,new cljs.core.Keyword(null,"first","first",-644103046))){
return app.components.x_menu.x_menu.focus_item_BANG_((items[(0)]));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(focus_target,new cljs.core.Keyword(null,"last","last",1105735132))){
return app.components.x_menu.x_menu.focus_item_BANG_((items[(n - (1))]));
} else {
return null;
}
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_menu.x_menu.close_menu_BANG_ = (function app$components$x_menu$x_menu$close_menu_BANG_(el,return_focus_QMARK_){
if(cljs.core.truth_(el.hasAttribute(app.components.x_menu.model.attr_open))){
el.removeAttribute(app.components.x_menu.model.attr_open);

app.components.x_menu.x_menu.dispatch_event_BANG_(el,app.components.x_menu.model.event_close,({}));
} else {
}

if(cljs.core.truth_(return_focus_QMARK_)){
var temp__5823__auto__ = app.components.x_menu.x_menu.get_trigger(el);
if(cljs.core.truth_(temp__5823__auto__)){
var trigger = temp__5823__auto__;
return trigger.focus();
} else {
return null;
}
} else {
return null;
}
});
app.components.x_menu.x_menu.trigger_clicked_QMARK_ = (function app$components$x_menu$x_menu$trigger_clicked_QMARK_(trigger_slot,evt){
var path = evt.composedPath();
var assigned = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(trigger_slot.assignedElements());
return cljs.core.boolean$(cljs.core.some((function (node){
return (path.indexOf(node) > (-1));
}),assigned));
});
app.components.x_menu.x_menu.handle_el_click_BANG_ = (function app$components$x_menu$x_menu$handle_el_click_BANG_(el,evt){
var refs = app.components.x_menu.x_menu.getv(el,app.components.x_menu.x_menu.key_refs);
var trigger_slot = (cljs.core.truth_(refs)?app.components.x_menu.x_menu.goog$module$goog$object.get(refs,"trigger-slot"):null);
if(cljs.core.truth_((function (){var and__5140__auto__ = trigger_slot;
if(cljs.core.truth_(and__5140__auto__)){
return app.components.x_menu.x_menu.trigger_clicked_QMARK_(trigger_slot,evt);
} else {
return and__5140__auto__;
}
})())){
if(cljs.core.truth_(el.hasAttribute(app.components.x_menu.model.attr_open))){
return app.components.x_menu.x_menu.close_menu_BANG_(el,true);
} else {
return app.components.x_menu.x_menu.open_menu_BANG_(el,null);
}
} else {
return null;
}
});
app.components.x_menu.x_menu.handle_doc_click_BANG_ = (function app$components$x_menu$x_menu$handle_doc_click_BANG_(el,evt){
if(cljs.core.truth_(el.hasAttribute(app.components.x_menu.model.attr_open))){
var path = evt.composedPath();
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((-1),path.indexOf(el))){
return app.components.x_menu.x_menu.close_menu_BANG_(el,false);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_menu.x_menu.handle_keydown_BANG_ = (function app$components$x_menu$x_menu$handle_keydown_BANG_(el,evt){
var k = evt.key;
var is_open_QMARK_ = el.hasAttribute(app.components.x_menu.model.attr_open);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"Escape")){
if(cljs.core.truth_(is_open_QMARK_)){
evt.preventDefault();

return app.components.x_menu.x_menu.close_menu_BANG_(el,true);
} else {
return null;
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"Tab")){
if(cljs.core.truth_(is_open_QMARK_)){
return app.components.x_menu.x_menu.close_menu_BANG_(el,false);
} else {
return null;
}
} else {
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"ArrowDown")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"ArrowUp")))){
var items = app.components.x_menu.x_menu.get_focusable_items(el);
var n = items.length;
if((n > (0))){
evt.preventDefault();

if(cljs.core.truth_(is_open_QMARK_)){
var active = document.activeElement;
var idx = items.indexOf(active);
var next_idx = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"ArrowDown"))?(((idx >= (n - (1))))?(0):(((idx < (0)))?(0):(idx + (1)))):(((idx <= (0)))?(n - (1)):(idx - (1))));
return app.components.x_menu.x_menu.focus_item_BANG_((items[next_idx]));
} else {
app.components.x_menu.x_menu.open_menu_BANG_(el,null);

return app.components.x_menu.x_menu.focus_item_BANG_((items[((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"ArrowDown"))?(0):(n - (1)))]));
}
} else {
return null;
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"Home")){
if(cljs.core.truth_(is_open_QMARK_)){
var items = app.components.x_menu.x_menu.get_focusable_items(el);
if((items.length > (0))){
evt.preventDefault();

return app.components.x_menu.x_menu.focus_item_BANG_((items[(0)]));
} else {
return null;
}
} else {
return null;
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"End")){
if(cljs.core.truth_(is_open_QMARK_)){
var items = app.components.x_menu.x_menu.get_focusable_items(el);
if((items.length > (0))){
evt.preventDefault();

return app.components.x_menu.x_menu.focus_item_BANG_((items[(items.length - (1))]));
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
}
}
}
}
});
app.components.x_menu.x_menu.handle_item_select_BANG_ = (function app$components$x_menu$x_menu$handle_item_select_BANG_(el,evt){
evt.stopPropagation();

var value = evt.detail.value;
app.components.x_menu.x_menu.close_menu_BANG_(el,false);

return app.components.x_menu.x_menu.dispatch_event_BANG_(el,app.components.x_menu.model.event_select,({"value": (function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()}));
});
app.components.x_menu.x_menu.render_BANG_ = (function app$components$x_menu$x_menu$render_BANG_(el){
var refs = app.components.x_menu.x_menu.getv(el,app.components.x_menu.x_menu.key_refs);
var popup = (cljs.core.truth_(refs)?app.components.x_menu.x_menu.goog$module$goog$object.get(refs,"popup"):null);
var state = app.components.x_menu.model.derive_state(app.components.x_menu.x_menu.read_inputs(el));
if(cljs.core.truth_(popup)){
popup.setAttribute("data-placement",new cljs.core.Keyword(null,"placement","placement",768366651).cljs$core$IFn$_invoke$arity$1(state));

var label = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_((function (){var and__5140__auto__ = label;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,"");
} else {
return and__5140__auto__;
}
})())){
return popup.setAttribute("aria-label",label);
} else {
return popup.removeAttribute("aria-label");
}
} else {
return null;
}
});
app.components.x_menu.x_menu.init_dom_BANG_ = (function app$components$x_menu$x_menu$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var trigger_slot = document.createElement("slot");
var popup = document.createElement("div");
var item_slot = document.createElement("slot");
(style.textContent = app.components.x_menu.x_menu.style_text);

base.setAttribute("part","base");

(base.className = "base");

trigger_slot.setAttribute("name","trigger");

popup.setAttribute("part","popup");

(popup.className = "popup");

popup.setAttribute("role","menu");

popup.appendChild(item_slot);

base.appendChild(trigger_slot);

base.appendChild(popup);

root.appendChild(style);

root.appendChild(base);

var refs = ({});
app.components.x_menu.x_menu.goog$module$goog$object.set(refs,"base",base);

app.components.x_menu.x_menu.goog$module$goog$object.set(refs,"popup",popup);

app.components.x_menu.x_menu.goog$module$goog$object.set(refs,"trigger-slot",trigger_slot);

app.components.x_menu.x_menu.goog$module$goog$object.set(refs,"item-slot",item_slot);

return app.components.x_menu.x_menu.setv_BANG_(el,app.components.x_menu.x_menu.key_refs,refs);
});
app.components.x_menu.x_menu.install_listeners_BANG_ = (function app$components$x_menu$x_menu$install_listeners_BANG_(el){
var on_click = (function (e){
return app.components.x_menu.x_menu.handle_el_click_BANG_(el,e);
});
var on_doc_click = (function (e){
return app.components.x_menu.x_menu.handle_doc_click_BANG_(el,e);
});
var on_keydown = (function (e){
return app.components.x_menu.x_menu.handle_keydown_BANG_(el,e);
});
var on_item_select = (function (e){
return app.components.x_menu.x_menu.handle_item_select_BANG_(el,e);
});
var handlers = ({});
el.addEventListener("click",on_click);

document.addEventListener("click",on_doc_click,true);

el.addEventListener("keydown",on_keydown);

el.addEventListener(app.components.x_menu.model.event_item_select,on_item_select);

app.components.x_menu.x_menu.goog$module$goog$object.set(handlers,"click",on_click);

app.components.x_menu.x_menu.goog$module$goog$object.set(handlers,"doc-click",on_doc_click);

app.components.x_menu.x_menu.goog$module$goog$object.set(handlers,"keydown",on_keydown);

app.components.x_menu.x_menu.goog$module$goog$object.set(handlers,"item-select",on_item_select);

return app.components.x_menu.x_menu.setv_BANG_(el,app.components.x_menu.x_menu.key_handlers,handlers);
});
app.components.x_menu.x_menu.remove_listeners_BANG_ = (function app$components$x_menu$x_menu$remove_listeners_BANG_(el){
var handlers = app.components.x_menu.x_menu.getv(el,app.components.x_menu.x_menu.key_handlers);
if(cljs.core.truth_(handlers)){
var on_click = app.components.x_menu.x_menu.goog$module$goog$object.get(handlers,"click");
var on_doc_click = app.components.x_menu.x_menu.goog$module$goog$object.get(handlers,"doc-click");
var on_keydown = app.components.x_menu.x_menu.goog$module$goog$object.get(handlers,"keydown");
var on_item_select = app.components.x_menu.x_menu.goog$module$goog$object.get(handlers,"item-select");
el.removeEventListener("click",on_click);

document.removeEventListener("click",on_doc_click,true);

el.removeEventListener("keydown",on_keydown);

return el.removeEventListener(app.components.x_menu.model.event_item_select,on_item_select);
} else {
return null;
}
});
app.components.x_menu.x_menu.init_element_BANG_ = (function app$components$x_menu$x_menu$init_element_BANG_(el){
if(app.components.x_menu.x_menu.initialized_QMARK_(el)){
} else {
app.components.x_menu.x_menu.init_dom_BANG_(el);

app.components.x_menu.x_menu.install_listeners_BANG_(el);

app.components.x_menu.x_menu.mark_initialized_BANG_(el);
}

app.components.x_menu.x_menu.render_BANG_(el);

return el;
});
app.components.x_menu.x_menu.connected_callback = (function app$components$x_menu$x_menu$connected_callback(el){
if(app.components.x_menu.x_menu.initialized_QMARK_(el)){
app.components.x_menu.x_menu.install_listeners_BANG_(el);

return app.components.x_menu.x_menu.render_BANG_(el);
} else {
return app.components.x_menu.x_menu.init_element_BANG_(el);
}
});
app.components.x_menu.x_menu.disconnected_callback = (function app$components$x_menu$x_menu$disconnected_callback(el){
return app.components.x_menu.x_menu.remove_listeners_BANG_(el);
});
app.components.x_menu.x_menu.attribute_changed_callback = (function app$components$x_menu$x_menu$attribute_changed_callback(el,_name,_old,_new){
if(app.components.x_menu.x_menu.initialized_QMARK_(el)){
return app.components.x_menu.x_menu.render_BANG_(el);
} else {
return null;
}
});
app.components.x_menu.x_menu.install_property_accessors_BANG_ = (function app$components$x_menu$x_menu$install_property_accessors_BANG_(klass){
Object.defineProperty(klass.prototype,"open",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_menu.model.attr_open);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu.model.attr_open,"");
} else {
return this$.removeAttribute(app.components.x_menu.model.attr_open);
}
}), "configurable": true, "enumerable": true}));

Object.defineProperty(klass.prototype,"placement",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_menu.model.attr_placement);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu.model.attr_placement,v);
} else {
return this$.removeAttribute(app.components.x_menu.model.attr_placement);
}
}), "configurable": true, "enumerable": true}));

return Object.defineProperty(klass.prototype,"label",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_menu.model.attr_label);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_menu.model.attr_label,v);
} else {
return this$.removeAttribute(app.components.x_menu.model.attr_label);
}
}), "configurable": true, "enumerable": true}));
});
app.components.x_menu.x_menu.element_class = (function app$components$x_menu$x_menu$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_menu.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_menu.x_menu.connected_callback(this$);
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_menu.x_menu.disconnected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
return app.components.x_menu.x_menu.attribute_changed_callback(this$,n,o,v);
}));

app.components.x_menu.x_menu.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_menu.x_menu.register_BANG_ = (function app$components$x_menu$x_menu$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_menu.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_menu.model.tag_name,app.components.x_menu.x_menu.element_class());
}
});
app.components.x_menu.x_menu.init_BANG_ = (function app$components$x_menu$x_menu$init_BANG_(){
return app.components.x_menu.x_menu.register_BANG_();
});

//# sourceMappingURL=app.components.x_menu.x_menu.js.map
