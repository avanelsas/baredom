goog.provide('app.components.x_navbar.x_navbar');
app.components.x_navbar.x_navbar.state_key = "__xNavbarState";
app.components.x_navbar.x_navbar.focus_visible_key = "__xNavbarFocusVisible";
app.components.x_navbar.x_navbar.get_prop = (function app$components$x_navbar$x_navbar$get_prop(obj,k){
return (obj[k]);
});
app.components.x_navbar.x_navbar.set_prop_BANG_ = (function app$components$x_navbar$x_navbar$set_prop_BANG_(obj,k,v){
return (obj[k] = v);
});
app.components.x_navbar.x_navbar.has_attr_QMARK_ = (function app$components$x_navbar$x_navbar$has_attr_QMARK_(el,attr_name){
return el.hasAttribute(attr_name);
});
app.components.x_navbar.x_navbar.get_attr = (function app$components$x_navbar$x_navbar$get_attr(el,attr_name){
return el.getAttribute(attr_name);
});
app.components.x_navbar.x_navbar.set_bool_attr_BANG_ = (function app$components$x_navbar$x_navbar$set_bool_attr_BANG_(el,attr_name,value){
if(cljs.core.truth_(value)){
return el.setAttribute(attr_name,"");
} else {
return el.removeAttribute(attr_name);
}
});
app.components.x_navbar.x_navbar.get_el_state = (function app$components$x_navbar$x_navbar$get_el_state(el){
return app.components.x_navbar.x_navbar.get_prop(el,app.components.x_navbar.x_navbar.state_key);
});
app.components.x_navbar.x_navbar.set_el_state_BANG_ = (function app$components$x_navbar$x_navbar$set_el_state_BANG_(el,state){
return app.components.x_navbar.x_navbar.set_prop_BANG_(el,app.components.x_navbar.x_navbar.state_key,state);
});
app.components.x_navbar.x_navbar.get_focus_visible = (function app$components$x_navbar$x_navbar$get_focus_visible(el){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(true,app.components.x_navbar.x_navbar.get_prop(el,app.components.x_navbar.x_navbar.focus_visible_key));
});
app.components.x_navbar.x_navbar.set_focus_visible_BANG_ = (function app$components$x_navbar$x_navbar$set_focus_visible_BANG_(el,value){
return app.components.x_navbar.x_navbar.set_prop_BANG_(el,app.components.x_navbar.x_navbar.focus_visible_key,cljs.core.boolean$(value));
});
app.components.x_navbar.x_navbar.read_public_state = (function app$components$x_navbar$x_navbar$read_public_state(el){
return app.components.x_navbar.model.public_state(new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_navbar.x_navbar.get_attr(el,app.components.x_navbar.model.attr_label),new cljs.core.Keyword(null,"orientation","orientation",623557579),app.components.x_navbar.x_navbar.get_attr(el,app.components.x_navbar.model.attr_orientation),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_navbar.x_navbar.get_attr(el,app.components.x_navbar.model.attr_variant),new cljs.core.Keyword(null,"sticky","sticky",-2121213869),app.components.x_navbar.x_navbar.has_attr_QMARK_(el,app.components.x_navbar.model.attr_sticky),new cljs.core.Keyword(null,"elevated","elevated",-7323953),app.components.x_navbar.x_navbar.has_attr_QMARK_(el,app.components.x_navbar.model.attr_elevated),new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440),app.components.x_navbar.x_navbar.get_attr(el,app.components.x_navbar.model.attr_breakpoint),new cljs.core.Keyword(null,"alignment","alignment",1040093386),app.components.x_navbar.x_navbar.get_attr(el,app.components.x_navbar.model.attr_alignment)], null));
});
app.components.x_navbar.x_navbar.dispatch_BANG_ = (function app$components$x_navbar$x_navbar$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_navbar.x_navbar.assigned_nodes = (function app$components$x_navbar$x_navbar$assigned_nodes(slot_el){
return slot_el.assignedNodes(({"flatten": true}));
});
app.components.x_navbar.x_navbar.slot_has_content_QMARK_ = (function app$components$x_navbar$x_navbar$slot_has_content_QMARK_(slot_el){
return (app.components.x_navbar.x_navbar.assigned_nodes(slot_el).length > (0));
});
app.components.x_navbar.x_navbar.source_from_event = (function app$components$x_navbar$x_navbar$source_from_event(event){
if((event instanceof PointerEvent)){
return "pointer";
} else {
if((event instanceof MouseEvent)){
return "pointer";
} else {
if((event instanceof KeyboardEvent)){
return "keyboard";
} else {
return "programmatic";

}
}
}
});
app.components.x_navbar.x_navbar.closest_anchor = (function app$components$x_navbar$x_navbar$closest_anchor(node){
if(cljs.core.truth_(node)){
return node.closest("a");
} else {
return null;
}
});
app.components.x_navbar.x_navbar.node_in_assigned_nodes_QMARK_ = (function app$components$x_navbar$x_navbar$node_in_assigned_nodes_QMARK_(target,nodes){
var idx = (0);
while(true){
if((idx < nodes.length)){
var node = (nodes[idx]);
if(cljs.core.truth_((function (){var or__5142__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node,target);
if(or__5142__auto__){
return or__5142__auto__;
} else {
var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node.nodeType,Node.ELEMENT_NODE);
if(and__5140__auto__){
return node.contains(target);
} else {
return and__5140__auto__;
}
}
})())){
return true;
} else {
var G__20982 = (idx + (1));
idx = G__20982;
continue;
}
} else {
return false;
}
break;
}
});
app.components.x_navbar.x_navbar.target_in_brand_slot_QMARK_ = (function app$components$x_navbar$x_navbar$target_in_brand_slot_QMARK_(state,target){
return app.components.x_navbar.x_navbar.node_in_assigned_nodes_QMARK_(target,app.components.x_navbar.x_navbar.assigned_nodes((state["brand-slot"])));
});
app.components.x_navbar.x_navbar.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-navbar-height:4rem;"+"--x-navbar-padding-inline:1rem;"+"--x-navbar-gap:0.75rem;"+"--x-navbar-bg:rgba(255,255,255,0.88);"+"--x-navbar-color:#0f172a;"+"--x-navbar-border:rgba(148,163,184,0.22);"+"--x-navbar-divider:rgba(148,163,184,0.18);"+"--x-navbar-shadow:0 8px 24px rgba(15,23,42,0.08);"+"--x-navbar-focus-ring:#60a5fa;"+"--x-navbar-z-index:40;"+"--x-navbar-radius:1rem;"+"--x-navbar-transition-duration:180ms;"+"--x-navbar-transition-easing:cubic-bezier(0.2,0,0,1);"+"--x-navbar-align-items:center;"+"}"+"@media (prefers-color-scheme: dark){"+":host{"+"--x-navbar-bg:rgba(15,23,42,0.84);"+"--x-navbar-color:#e5e7eb;"+"--x-navbar-border:rgba(51,65,85,0.9);"+"--x-navbar-divider:rgba(51,65,85,0.8);"+"--x-navbar-shadow:0 14px 36px rgba(0,0,0,0.35);"+"--x-navbar-focus-ring:#93c5fd;"+"}"+"}"+":host([hidden]){display:none;}"+"[part='base']{"+"display:block;"+"color:var(--x-navbar-color);"+"background:var(--x-navbar-bg);"+"border:1px solid var(--x-navbar-border);"+"border-radius:var(--x-navbar-radius);"+"box-shadow:none;"+"backdrop-filter:blur(14px);"+"-webkit-backdrop-filter:blur(14px);"+"transition:"+"background var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"+"border-color var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"+"box-shadow var(--x-navbar-transition-duration) var(--x-navbar-transition-easing);"+"}"+":host([sticky]) [part='base']{"+"position:sticky;"+"top:0;"+"z-index:var(--x-navbar-z-index);"+"}"+":host([elevated]) [part='base']{"+"box-shadow:var(--x-navbar-shadow);"+"}"+"[part='base'][data-variant='subtle']{"+"background:transparent;"+"backdrop-filter:none;"+"-webkit-backdrop-filter:none;"+"box-shadow:none;"+"}"+"[part='base'][data-variant='inverted']{"+"background:#0f172a;"+"color:#f8fafc;"+"border-color:#1e293b;"+"}"+"@media (prefers-color-scheme: dark){"+"[part='base'][data-variant='inverted']{"+"background:#020617;"+"color:#f8fafc;"+"border-color:#172033;"+"}"+"}"+"[part='base'][data-variant='transparent']{"+"background:transparent;"+"border-color:transparent;"+"box-shadow:none;"+"backdrop-filter:none;"+"-webkit-backdrop-filter:none;"+"}"+"[part='bar']{"+"display:grid;"+"grid-template-columns:auto auto 1fr auto auto auto;"+"align-items:var(--x-navbar-align-items, center);"+"gap:var(--x-navbar-gap);"+"min-block-size:var(--x-navbar-height);"+"padding-inline:var(--x-navbar-padding-inline);"+"}"+"[part='bar'][data-alignment='start']{justify-items:start;}"+"[part='bar'][data-alignment='center']{justify-items:center;}"+"[part='bar'][data-alignment='space-between']{justify-items:stretch;}"+"[part='base'][data-orientation='vertical'] [part='bar']{"+"grid-template-columns:1fr;"+"grid-auto-rows:auto;"+"align-items:var(--x-navbar-align-items, center);"+"padding-block:0.75rem;"+"}"+"[part='brand'],[part='start'],[part='nav'],[part='actions'],[part='toggle'],[part='end']{"+"display:flex;"+"align-items:inherit;"+"min-inline-size:0;"+"}"+"[part='nav']{gap:var(--x-navbar-gap);}"+"[part='actions'],[part='end'],[part='toggle']{justify-self:end;}"+"[part='base'][data-orientation='vertical'] [part='actions'],"+"[part='base'][data-orientation='vertical'] [part='end'],"+"[part='base'][data-orientation='vertical'] [part='toggle']{justify-self:start;}"+"[part='brand'][data-has-brand='false'],"+"[part='start'][data-has-start='false'],"+"[part='actions'][data-has-actions='false'],"+"[part='toggle'][data-has-toggle='false'],"+"[part='end'][data-has-end='false']{display:none;}"+"slot[name='brand'],slot[name='start'],slot[name='actions'],slot[name='toggle'],slot[name='end']{display:contents;}"+"slot{display:contents;}"+":host(:focus-within) [part='base']{box-shadow:var(--x-navbar-shadow);}"+":host([data-focus-visible-within='true']) [part='base']{"+"box-shadow:0 0 0 2px var(--x-navbar-focus-ring), var(--x-navbar-shadow);"+"}"+"@media (prefers-reduced-motion: reduce){"+"[part='base']{transition:none;}"+"}");
app.components.x_navbar.x_navbar.create_el = (function app$components$x_navbar$x_navbar$create_el(tag){
return document.createElement(tag);
});
app.components.x_navbar.x_navbar.create_shadow_BANG_ = (function app$components$x_navbar$x_navbar$create_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_navbar.x_navbar.create_el("style");
var nav_el = app.components.x_navbar.x_navbar.create_el("nav");
var bar_el = app.components.x_navbar.x_navbar.create_el("div");
var brand_el = app.components.x_navbar.x_navbar.create_el("div");
var start_el = app.components.x_navbar.x_navbar.create_el("div");
var nav_slot_wrap_el = app.components.x_navbar.x_navbar.create_el("div");
var actions_el = app.components.x_navbar.x_navbar.create_el("div");
var toggle_el = app.components.x_navbar.x_navbar.create_el("div");
var end_el = app.components.x_navbar.x_navbar.create_el("div");
var brand_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
var start_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
var default_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
var actions_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
var toggle_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
var end_slot_el = app.components.x_navbar.x_navbar.create_el("slot");
(style_el.textContent = app.components.x_navbar.x_navbar.style_text);

nav_el.setAttribute("part","base");

bar_el.setAttribute("part","bar");

brand_el.setAttribute("part","brand");

start_el.setAttribute("part","start");

nav_slot_wrap_el.setAttribute("part","nav");

actions_el.setAttribute("part","actions");

toggle_el.setAttribute("part","toggle");

end_el.setAttribute("part","end");

brand_slot_el.setAttribute("name",app.components.x_navbar.model.slot_brand);

start_slot_el.setAttribute("name",app.components.x_navbar.model.slot_start);

actions_slot_el.setAttribute("name",app.components.x_navbar.model.slot_actions);

toggle_slot_el.setAttribute("name",app.components.x_navbar.model.slot_toggle);

end_slot_el.setAttribute("name",app.components.x_navbar.model.slot_end);

brand_el.appendChild(brand_slot_el);

start_el.appendChild(start_slot_el);

nav_slot_wrap_el.appendChild(default_slot_el);

actions_el.appendChild(actions_slot_el);

toggle_el.appendChild(toggle_slot_el);

end_el.appendChild(end_slot_el);

bar_el.appendChild(brand_el);

bar_el.appendChild(start_el);

bar_el.appendChild(nav_slot_wrap_el);

bar_el.appendChild(actions_el);

bar_el.appendChild(toggle_el);

bar_el.appendChild(end_el);

nav_el.appendChild(bar_el);

root.appendChild(style_el);

root.appendChild(nav_el);

return ({"end-slot": end_slot_el, "bar": bar_el, "start": start_el, "toggle": toggle_el, "actions": actions_el, "brand": brand_el, "default-slot": default_slot_el, "root": root, "brand-slot": brand_slot_el, "start-slot": start_slot_el, "actions-slot": actions_slot_el, "toggle-slot": toggle_slot_el, "end": end_el, "base": nav_el, "nav": nav_slot_wrap_el});
});
app.components.x_navbar.x_navbar.render_BANG_ = (function app$components$x_navbar$x_navbar$render_BANG_(el,state){
var base_el = (state["base"]);
var bar_el = (state["bar"]);
var brand_el = (state["brand"]);
var start_el = (state["start"]);
var nav_el = (state["nav"]);
var actions_el = (state["actions"]);
var toggle_el = (state["toggle"]);
var end_el = (state["end"]);
var brand_slot_el = (state["brand-slot"]);
var start_slot_el = (state["start-slot"]);
var actions_slot_el = (state["actions-slot"]);
var toggle_slot_el = (state["toggle-slot"]);
var end_slot_el = (state["end-slot"]);
var public_state = app.components.x_navbar.x_navbar.read_public_state(el);
var has_brand_QMARK_ = app.components.x_navbar.x_navbar.slot_has_content_QMARK_(brand_slot_el);
var has_start_QMARK_ = app.components.x_navbar.x_navbar.slot_has_content_QMARK_(start_slot_el);
var has_actions_QMARK_ = app.components.x_navbar.x_navbar.slot_has_content_QMARK_(actions_slot_el);
var has_toggle_QMARK_ = app.components.x_navbar.x_navbar.slot_has_content_QMARK_(toggle_slot_el);
var has_end_QMARK_ = app.components.x_navbar.x_navbar.slot_has_content_QMARK_(end_slot_el);
var label = app.components.x_navbar.model.landmark_label(public_state);
if(cljs.core.truth_(label)){
base_el.setAttribute("aria-label",label);
} else {
base_el.removeAttribute("aria-label");
}

base_el.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(public_state));

base_el.setAttribute("data-orientation",new cljs.core.Keyword(null,"orientation","orientation",623557579).cljs$core$IFn$_invoke$arity$1(public_state));

base_el.setAttribute("data-breakpoint",new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440).cljs$core$IFn$_invoke$arity$1(public_state));

bar_el.setAttribute("data-alignment",new cljs.core.Keyword(null,"alignment","alignment",1040093386).cljs$core$IFn$_invoke$arity$1(public_state));

brand_el.setAttribute("data-has-brand",((has_brand_QMARK_)?"true":"false"));

start_el.setAttribute("data-has-start",((has_start_QMARK_)?"true":"false"));

actions_el.setAttribute("data-has-actions",((has_actions_QMARK_)?"true":"false"));

toggle_el.setAttribute("data-has-toggle",((has_toggle_QMARK_)?"true":"false"));

return end_el.setAttribute("data-has-end",((has_end_QMARK_)?"true":"false"));
});
app.components.x_navbar.x_navbar.setup_delegated_events_BANG_ = (function app$components$x_navbar$x_navbar$setup_delegated_events_BANG_(el,base_el){
base_el.addEventListener("click",(function (event){
var state = app.components.x_navbar.x_navbar.get_el_state(el);
var target = event.target;
var anchor = app.components.x_navbar.x_navbar.closest_anchor(target);
var source = app.components.x_navbar.x_navbar.source_from_event(event);
if(cljs.core.truth_(anchor)){
app.components.x_navbar.x_navbar.dispatch_BANG_(el,app.components.x_navbar.model.event_navigate,({"href": (function (){var or__5142__auto__ = anchor.getAttribute("href");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(), "source": source}));
} else {
}

if(app.components.x_navbar.x_navbar.target_in_brand_slot_QMARK_(state,target)){
return app.components.x_navbar.x_navbar.dispatch_BANG_(el,app.components.x_navbar.model.event_brand_activate,({"source": source}));
} else {
return null;
}
}));

base_el.addEventListener("focusin",(function (event){
var target = event.target;
if(cljs.core.truth_((function (){var and__5140__auto__ = target;
if(cljs.core.truth_(and__5140__auto__)){
return target.matches(":focus-visible");
} else {
return and__5140__auto__;
}
})())){
if(app.components.x_navbar.x_navbar.get_focus_visible(el)){
return null;
} else {
app.components.x_navbar.x_navbar.set_focus_visible_BANG_(el,true);

el.setAttribute("data-focus-visible-within","true");

return app.components.x_navbar.x_navbar.dispatch_BANG_(el,app.components.x_navbar.model.event_focus_visible,({}));
}
} else {
return null;
}
}));

return base_el.addEventListener("focusout",(function (event){
var related = event.relatedTarget;
if(cljs.core.truth_((function (){var and__5140__auto__ = related;
if(cljs.core.truth_(and__5140__auto__)){
return el.contains(related);
} else {
return and__5140__auto__;
}
})())){
return null;
} else {
app.components.x_navbar.x_navbar.set_focus_visible_BANG_(el,false);

return el.removeAttribute("data-focus-visible-within");
}
}));
});
app.components.x_navbar.x_navbar.setup_slots_BANG_ = (function app$components$x_navbar$x_navbar$setup_slots_BANG_(el,state){
var rerender = (function (_){
return app.components.x_navbar.x_navbar.render_BANG_(el,state);
});
(state["brand-slot"]).addEventListener("slotchange",rerender);

(state["start-slot"]).addEventListener("slotchange",rerender);

(state["default-slot"]).addEventListener("slotchange",rerender);

(state["actions-slot"]).addEventListener("slotchange",rerender);

(state["toggle-slot"]).addEventListener("slotchange",rerender);

return (state["end-slot"]).addEventListener("slotchange",rerender);
});
app.components.x_navbar.x_navbar.connected_BANG_ = (function app$components$x_navbar$x_navbar$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_navbar.x_navbar.get_el_state(el))){
} else {
var state_21048 = app.components.x_navbar.x_navbar.create_shadow_BANG_(el);
var base_el_21049 = (state_21048["base"]);
app.components.x_navbar.x_navbar.set_focus_visible_BANG_(el,false);

app.components.x_navbar.x_navbar.setup_delegated_events_BANG_(el,base_el_21049);

app.components.x_navbar.x_navbar.setup_slots_BANG_(el,state_21048);

app.components.x_navbar.x_navbar.set_el_state_BANG_(el,state_21048);
}

return app.components.x_navbar.x_navbar.render_BANG_(el,app.components.x_navbar.x_navbar.get_el_state(el));
});
app.components.x_navbar.x_navbar.disconnected_BANG_ = (function app$components$x_navbar$x_navbar$disconnected_BANG_(el){
return app.components.x_navbar.x_navbar.set_focus_visible_BANG_(el,false);
});
app.components.x_navbar.x_navbar.attribute_changed_BANG_ = (function app$components$x_navbar$x_navbar$attribute_changed_BANG_(el,_name,_old_value,_new_value){
var temp__5823__auto__ = app.components.x_navbar.x_navbar.get_el_state(el);
if(cljs.core.truth_(temp__5823__auto__)){
var state = temp__5823__auto__;
return app.components.x_navbar.x_navbar.render_BANG_(el,state);
} else {
return null;
}
});
app.components.x_navbar.x_navbar.define_bool_prop_BANG_ = (function app$components$x_navbar$x_navbar$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_navbar.x_navbar.has_attr_QMARK_(this$,attr_name);
}), "set": (function (value){
var this$ = this;
return app.components.x_navbar.x_navbar.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(value));
})}));
});
app.components.x_navbar.x_navbar.make_constructor = (function app$components$x_navbar$x_navbar$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_navbar.x_navbar.define_element_BANG_ = (function app$components$x_navbar$x_navbar$define_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_navbar.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_navbar.x_navbar.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_navbar.x_navbar.define_bool_prop_BANG_(proto,"sticky",app.components.x_navbar.model.attr_sticky);

app.components.x_navbar.x_navbar.define_bool_prop_BANG_(proto,"elevated",app.components.x_navbar.model.attr_elevated);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_navbar.x_navbar.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_navbar.x_navbar.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_navbar.x_navbar.attribute_changed_BANG_(this$,name,old_value,new_value);
}));

(ctor["observedAttributes"] = app.components.x_navbar.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_navbar.model.tag_name,ctor);
}
});
app.components.x_navbar.x_navbar.init_BANG_ = (function app$components$x_navbar$x_navbar$init_BANG_(){
return app.components.x_navbar.x_navbar.define_element_BANG_();
});

//# sourceMappingURL=app.components.x_navbar.x_navbar.js.map
