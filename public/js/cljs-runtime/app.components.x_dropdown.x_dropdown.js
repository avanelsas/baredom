goog.provide('app.components.x_dropdown.x_dropdown');
goog.scope(function(){
  app.components.x_dropdown.x_dropdown.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_dropdown.x_dropdown.k_refs = "__xDropdownRefs";
app.components.x_dropdown.x_dropdown.k_handlers = "__xDropdownHandlers";
app.components.x_dropdown.x_dropdown.style_text = (""+":host{"+"display:inline-block;"+"position:relative;"+"color-scheme:light dark;"+"--x-dropdown-trigger-bg:#f8fafc;"+"--x-dropdown-trigger-bg-hover:#f1f5f9;"+"--x-dropdown-trigger-bg-active:#e2e8f0;"+"--x-dropdown-trigger-color:#0f172a;"+"--x-dropdown-trigger-border:1px solid #e2e8f0;"+"--x-dropdown-trigger-radius:6px;"+"--x-dropdown-trigger-padding:0 0.75rem;"+"--x-dropdown-trigger-height:2.25rem;"+"--x-dropdown-trigger-font-size:0.9375rem;"+"--x-dropdown-trigger-font-weight:500;"+"--x-dropdown-chevron-color:#64748b;"+"--x-dropdown-focus-ring:#60a5fa;"+"--x-dropdown-panel-bg:#ffffff;"+"--x-dropdown-panel-border:1px solid #e2e8f0;"+"--x-dropdown-panel-radius:8px;"+"--x-dropdown-panel-shadow:0 4px 16px rgba(0,0,0,0.12);"+"--x-dropdown-panel-padding:0.25rem;"+"--x-dropdown-panel-min-width:10rem;"+"--x-dropdown-panel-max-height:20rem;"+"--x-dropdown-panel-offset:4px;"+"--x-dropdown-transition-duration:150ms;"+"--x-dropdown-transition-easing:ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-dropdown-trigger-bg:#1e293b;"+"--x-dropdown-trigger-bg-hover:#334155;"+"--x-dropdown-trigger-bg-active:#475569;"+"--x-dropdown-trigger-color:#e2e8f0;"+"--x-dropdown-trigger-border:1px solid #334155;"+"--x-dropdown-chevron-color:#94a3b8;"+"--x-dropdown-focus-ring:#93c5fd;"+"--x-dropdown-panel-bg:#1e293b;"+"--x-dropdown-panel-border:1px solid #334155;"+"--x-dropdown-panel-shadow:0 4px 24px rgba(0,0,0,0.4);"+"}"+"}"+"[part=trigger]{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"gap:0.375rem;"+"height:var(--x-dropdown-trigger-height);"+"padding:var(--x-dropdown-trigger-padding);"+"background:var(--x-dropdown-trigger-bg);"+"color:var(--x-dropdown-trigger-color);"+"border:var(--x-dropdown-trigger-border);"+"border-radius:var(--x-dropdown-trigger-radius);"+"font-size:var(--x-dropdown-trigger-font-size);"+"font-weight:var(--x-dropdown-trigger-font-weight);"+"font-family:inherit;"+"cursor:pointer;"+"user-select:none;"+"white-space:nowrap;"+"}"+"[part=trigger]:hover:not(:disabled){"+"background:var(--x-dropdown-trigger-bg-hover);"+"}"+"[part=trigger]:active:not(:disabled){"+"background:var(--x-dropdown-trigger-bg-active);"+"}"+"[part=trigger]:disabled{"+"cursor:default;"+"opacity:0.55;"+"}"+"[part=trigger]:focus-visible{"+"outline:none;"+"box-shadow:0 0 0 2px var(--x-dropdown-focus-ring);"+"}"+"[part=trigger-label]{"+"flex:1;"+"}"+"[part=chevron]{"+"display:inline-block;"+"color:var(--x-dropdown-chevron-color);"+"font-style:normal;"+"flex-shrink:0;"+"transition:transform 200ms ease;"+"}"+":host([open]) [part=chevron]{"+"transform:rotate(180deg);"+"}"+"[part=panel]{"+"position:absolute;"+"z-index:1000;"+"box-sizing:border-box;"+"background:var(--x-dropdown-panel-bg);"+"border:var(--x-dropdown-panel-border);"+"border-radius:var(--x-dropdown-panel-radius);"+"box-shadow:var(--x-dropdown-panel-shadow);"+"padding:var(--x-dropdown-panel-padding);"+"min-width:var(--x-dropdown-panel-min-width);"+"max-height:var(--x-dropdown-panel-max-height);"+"overflow-y:auto;"+"visibility:hidden;"+"pointer-events:none;"+"opacity:0;"+"transform:scaleY(0.95);"+"transition:"+"opacity var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"+"transform var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"+"visibility 0s var(--x-dropdown-transition-duration);"+"}"+":host([open]) [part=panel]{"+"visibility:visible;"+"pointer-events:auto;"+"opacity:1;"+"transform:scaleY(1);"+"transition:"+"opacity var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"+"transform var(--x-dropdown-transition-duration) var(--x-dropdown-transition-easing),"+"visibility 0s 0s;"+"}"+"[part=panel][data-placement=bottom-start]{"+"top:calc(100% + var(--x-dropdown-panel-offset));"+"left:0;"+"transform-origin:top left;"+"}"+"[part=panel][data-placement=bottom-end]{"+"top:calc(100% + var(--x-dropdown-panel-offset));"+"right:0;"+"transform-origin:top right;"+"}"+"[part=panel][data-placement=top-start]{"+"bottom:calc(100% + var(--x-dropdown-panel-offset));"+"left:0;"+"transform-origin:bottom left;"+"}"+"[part=panel][data-placement=top-end]{"+"bottom:calc(100% + var(--x-dropdown-panel-offset));"+"right:0;"+"transform-origin:bottom right;"+"}"+"[part=panel-inner]{"+"display:contents;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=panel]{transition:none !important;}"+"[part=chevron]{transition:none !important;}"+"}");
app.components.x_dropdown.x_dropdown.make_el = (function app$components$x_dropdown$x_dropdown$make_el(tag){
return document.createElement(tag);
});
app.components.x_dropdown.x_dropdown.set_attr_BANG_ = (function app$components$x_dropdown$x_dropdown$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_dropdown.x_dropdown.remove_attr_BANG_ = (function app$components$x_dropdown$x_dropdown$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_dropdown.x_dropdown.has_attr_QMARK_ = (function app$components$x_dropdown$x_dropdown$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_dropdown.x_dropdown.get_attr = (function app$components$x_dropdown$x_dropdown$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_dropdown.x_dropdown.set_bool_attr_BANG_ = (function app$components$x_dropdown$x_dropdown$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_dropdown.x_dropdown.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_dropdown.x_dropdown.remove_attr_BANG_(el,attr);
}
});
app.components.x_dropdown.x_dropdown.make_shadow_BANG_ = (function app$components$x_dropdown$x_dropdown$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_dropdown.x_dropdown.make_el("style");
var trigger_el = app.components.x_dropdown.x_dropdown.make_el("button");
var label_el = app.components.x_dropdown.x_dropdown.make_el("span");
var chevron_el = app.components.x_dropdown.x_dropdown.make_el("span");
var panel_el = app.components.x_dropdown.x_dropdown.make_el("div");
var inner_el = app.components.x_dropdown.x_dropdown.make_el("div");
var slot_el = app.components.x_dropdown.x_dropdown.make_el("slot");
(style_el.textContent = app.components.x_dropdown.x_dropdown.style_text);

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"part","trigger");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"type","button");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"aria-haspopup","true");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"aria-expanded","false");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"aria-controls","panel");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(label_el,"part","trigger-label");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(chevron_el,"part","chevron");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(chevron_el,"aria-hidden","true");

(chevron_el.textContent = "\u25BE");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(panel_el,"part","panel");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(panel_el,"id","panel");

app.components.x_dropdown.x_dropdown.set_attr_BANG_(panel_el,"data-placement",app.components.x_dropdown.model.default_placement);

app.components.x_dropdown.x_dropdown.set_attr_BANG_(inner_el,"part","panel-inner");

trigger_el.appendChild(label_el);

trigger_el.appendChild(chevron_el);

inner_el.appendChild(slot_el);

panel_el.appendChild(inner_el);

root.appendChild(style_el);

root.appendChild(trigger_el);

root.appendChild(panel_el);

var refs = ({"trigger": trigger_el, "label": label_el, "chevron": chevron_el, "panel": panel_el});
app.components.x_dropdown.x_dropdown.goog$module$goog$object.set(el,app.components.x_dropdown.x_dropdown.k_refs,refs);

return refs;
});
app.components.x_dropdown.x_dropdown.render_BANG_ = (function app$components$x_dropdown$x_dropdown$render_BANG_(el){
var temp__5823__auto__ = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var trigger_el = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(refs,"trigger");
var label_el = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(refs,"label");
var panel_el = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(refs,"panel");
var open_QMARK_ = app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open);
var disabled_QMARK_ = app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_disabled);
var label = (function (){var or__5142__auto__ = app.components.x_dropdown.x_dropdown.get_attr(el,app.components.x_dropdown.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var placement = app.components.x_dropdown.model.normalize_placement(app.components.x_dropdown.x_dropdown.get_attr(el,app.components.x_dropdown.model.attr_placement));
(label_el.textContent = label);

app.components.x_dropdown.x_dropdown.set_attr_BANG_(trigger_el,"aria-expanded",(cljs.core.truth_(open_QMARK_)?"true":"false"));

app.components.x_dropdown.x_dropdown.set_bool_attr_BANG_(trigger_el,"disabled",disabled_QMARK_);

return app.components.x_dropdown.x_dropdown.set_attr_BANG_(panel_el,"data-placement",placement);
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.dispatch_cancelable_BANG_ = (function app$components$x_dropdown$x_dropdown$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_dropdown.x_dropdown.dispatch_BANG_ = (function app$components$x_dropdown$x_dropdown$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_dropdown.x_dropdown.toggle_BANG_ = (function app$components$x_dropdown$x_dropdown$toggle_BANG_(el,source){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_disabled))){
return null;
} else {
var currently_open_QMARK_ = app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open);
var next_open_QMARK_ = cljs.core.not(currently_open_QMARK_);
var detail = app.components.x_dropdown.model.toggle_detail(next_open_QMARK_,source);
var allowed_QMARK_ = app.components.x_dropdown.x_dropdown.dispatch_cancelable_BANG_(el,app.components.x_dropdown.model.event_toggle,detail);
if(allowed_QMARK_){
if(next_open_QMARK_){
app.components.x_dropdown.x_dropdown.set_attr_BANG_(el,app.components.x_dropdown.model.attr_open,"");
} else {
app.components.x_dropdown.x_dropdown.remove_attr_BANG_(el,app.components.x_dropdown.model.attr_open);
}

return app.components.x_dropdown.x_dropdown.dispatch_BANG_(el,app.components.x_dropdown.model.event_change,({"open": next_open_QMARK_}));
} else {
return null;
}
}
});
app.components.x_dropdown.x_dropdown.add_doc_listeners_BANG_ = (function app$components$x_dropdown$x_dropdown$add_doc_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_handlers);
if(cljs.core.truth_(temp__5823__auto__)){
var handlers = temp__5823__auto__;
var doc_click_h = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"docClick");
var doc_keydown_h = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"docKeydown");
return setTimeout((function (){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open))){
document.addEventListener("click",doc_click_h);

return document.addEventListener("keydown",doc_keydown_h);
} else {
return null;
}
}),(0));
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.remove_doc_listeners_BANG_ = (function app$components$x_dropdown$x_dropdown$remove_doc_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_handlers);
if(cljs.core.truth_(temp__5823__auto__)){
var handlers = temp__5823__auto__;
var doc_click_h = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"docClick");
var doc_keydown_h = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"docKeydown");
document.removeEventListener("click",doc_click_h);

return document.removeEventListener("keydown",doc_keydown_h);
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.make_handlers = (function app$components$x_dropdown$x_dropdown$make_handlers(el){
var trigger_click_h = (function (_evt){
return app.components.x_dropdown.x_dropdown.toggle_BANG_(el,"pointer");
});
var trigger_keydown_h = (function (evt){
var key = evt.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")))){
evt.preventDefault();

return app.components.x_dropdown.x_dropdown.toggle_BANG_(el,"keyboard");
} else {
return null;
}
});
var host_focusout_h = (function (evt){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open))){
var related = evt.relatedTarget;
if((((related == null)) || (cljs.core.not(el.contains(related))))){
return app.components.x_dropdown.x_dropdown.toggle_BANG_(el,"focusout");
} else {
return null;
}
} else {
return null;
}
});
var doc_click_h = (function (evt){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open))){
var path = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(evt.composedPath());
var inside_QMARK_ = cljs.core.some((function (p1__22633_SHARP_){
return (p1__22633_SHARP_ === el);
}),path);
if(cljs.core.truth_(inside_QMARK_)){
return null;
} else {
return app.components.x_dropdown.x_dropdown.toggle_BANG_(el,"outside-click");
}
} else {
return null;
}
});
var doc_keydown_h = (function (evt){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(evt.key,"Escape");
if(and__5140__auto__){
return app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open);
} else {
return and__5140__auto__;
}
})())){
return app.components.x_dropdown.x_dropdown.toggle_BANG_(el,"escape");
} else {
return null;
}
});
return ({"triggerClick": trigger_click_h, "triggerKeydown": trigger_keydown_h, "hostFocusout": host_focusout_h, "docClick": doc_click_h, "docKeydown": doc_keydown_h});
});
app.components.x_dropdown.x_dropdown.add_static_listeners_BANG_ = (function app$components$x_dropdown$x_dropdown$add_static_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var trigger_el = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(refs,"trigger");
trigger_el.addEventListener("click",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"triggerClick"));

trigger_el.addEventListener("keydown",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"triggerKeydown"));

return el.addEventListener("focusout",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"hostFocusout"));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.remove_static_listeners_BANG_ = (function app$components$x_dropdown$x_dropdown$remove_static_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var trigger_el = app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(refs,"trigger");
trigger_el.removeEventListener("click",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"triggerClick"));

trigger_el.removeEventListener("keydown",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"triggerKeydown"));

return el.removeEventListener("focusout",app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(handlers,"hostFocusout"));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.connected_BANG_ = (function app$components$x_dropdown$x_dropdown$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_refs))){
} else {
app.components.x_dropdown.x_dropdown.make_shadow_BANG_(el);
}

app.components.x_dropdown.x_dropdown.remove_static_listeners_BANG_(el);

app.components.x_dropdown.x_dropdown.remove_doc_listeners_BANG_(el);

app.components.x_dropdown.x_dropdown.goog$module$goog$object.set(el,app.components.x_dropdown.x_dropdown.k_handlers,app.components.x_dropdown.x_dropdown.make_handlers(el));

app.components.x_dropdown.x_dropdown.add_static_listeners_BANG_(el);

if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open))){
app.components.x_dropdown.x_dropdown.add_doc_listeners_BANG_(el);
} else {
}

return app.components.x_dropdown.x_dropdown.render_BANG_(el);
});
app.components.x_dropdown.x_dropdown.disconnected_BANG_ = (function app$components$x_dropdown$x_dropdown$disconnected_BANG_(el){
app.components.x_dropdown.x_dropdown.remove_static_listeners_BANG_(el);

return app.components.x_dropdown.x_dropdown.remove_doc_listeners_BANG_(el);
});
app.components.x_dropdown.x_dropdown.attribute_changed_BANG_ = (function app$components$x_dropdown$x_dropdown$attribute_changed_BANG_(el,name,_old,_new){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.goog$module$goog$object.get(el,app.components.x_dropdown.x_dropdown.k_refs))){
app.components.x_dropdown.x_dropdown.render_BANG_(el);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_dropdown.model.attr_open)){
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(el,app.components.x_dropdown.model.attr_open))){
return app.components.x_dropdown.x_dropdown.add_doc_listeners_BANG_(el);
} else {
return app.components.x_dropdown.x_dropdown.remove_doc_listeners_BANG_(el);
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_dropdown.x_dropdown.define_bool_prop_BANG_ = (function app$components$x_dropdown$x_dropdown$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_dropdown.x_dropdown.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_dropdown.x_dropdown.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_dropdown.x_dropdown.define_string_prop_BANG_ = (function app$components$x_dropdown$x_dropdown$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_dropdown.x_dropdown.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_dropdown.x_dropdown.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_dropdown.x_dropdown.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_dropdown.x_dropdown.element_class = (function app$components$x_dropdown$x_dropdown$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_dropdown.model.observed_attributes;
})}));

app.components.x_dropdown.x_dropdown.define_bool_prop_BANG_(proto,"open",app.components.x_dropdown.model.attr_open);

app.components.x_dropdown.x_dropdown.define_bool_prop_BANG_(proto,"disabled",app.components.x_dropdown.model.attr_disabled);

app.components.x_dropdown.x_dropdown.define_string_prop_BANG_(proto,"label",app.components.x_dropdown.model.attr_label);

app.components.x_dropdown.x_dropdown.define_string_prop_BANG_(proto,"placement",app.components.x_dropdown.model.attr_placement);

(proto["show"] = (function (){
var this$ = this;
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(this$,app.components.x_dropdown.model.attr_open))){
return null;
} else {
return app.components.x_dropdown.x_dropdown.toggle_BANG_(this$,"programmatic");
}
}));

(proto["hide"] = (function (){
var this$ = this;
if(cljs.core.truth_(app.components.x_dropdown.x_dropdown.has_attr_QMARK_(this$,app.components.x_dropdown.model.attr_open))){
return app.components.x_dropdown.x_dropdown.toggle_BANG_(this$,"programmatic");
} else {
return null;
}
}));

(proto["toggle"] = (function (){
var this$ = this;
return app.components.x_dropdown.x_dropdown.toggle_BANG_(this$,"programmatic");
}));

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_dropdown.x_dropdown.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_dropdown.x_dropdown.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_dropdown.x_dropdown.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_dropdown.x_dropdown.init_BANG_ = (function app$components$x_dropdown$x_dropdown$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_dropdown.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_dropdown.model.tag_name,app.components.x_dropdown.x_dropdown.element_class());
}
});

//# sourceMappingURL=app.components.x_dropdown.x_dropdown.js.map
