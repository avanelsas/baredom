goog.provide('app.components.x_collapse.x_collapse');
goog.scope(function(){
  app.components.x_collapse.x_collapse.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_collapse.x_collapse.k_refs = "__xCollapseRefs";
app.components.x_collapse.x_collapse.k_handlers = "__xCollapseHandlers";
app.components.x_collapse.x_collapse.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-collapse-border-radius:8px;"+"--x-collapse-border:1px solid #e2e8f0;"+"--x-collapse-bg:#ffffff;"+"--x-collapse-trigger-bg:#f8fafc;"+"--x-collapse-trigger-bg-hover:#f1f5f9;"+"--x-collapse-trigger-color:#0f172a;"+"--x-collapse-trigger-padding:0.75rem 1rem;"+"--x-collapse-content-padding:1rem;"+"--x-collapse-font-size:0.9375rem;"+"--x-collapse-font-weight:600;"+"--x-collapse-chevron-color:#64748b;"+"--x-collapse-focus-ring:#60a5fa;"+"--x-collapse-transition-easing:ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-collapse-border:1px solid #334155;"+"--x-collapse-bg:#1e293b;"+"--x-collapse-trigger-bg:#0f172a;"+"--x-collapse-trigger-bg-hover:#1e293b;"+"--x-collapse-trigger-color:#e2e8f0;"+"--x-collapse-chevron-color:#94a3b8;"+"--x-collapse-focus-ring:#93c5fd;"+"}"+"}"+"[part=container]{"+"box-sizing:border-box;"+"border:var(--x-collapse-border);"+"border-radius:var(--x-collapse-border-radius);"+"background:var(--x-collapse-bg);"+"overflow:hidden;"+"}"+"[part=trigger]{"+"all:unset;"+"box-sizing:border-box;"+"display:flex;"+"align-items:center;"+"justify-content:space-between;"+"width:100%;"+"padding:var(--x-collapse-trigger-padding);"+"background:var(--x-collapse-trigger-bg);"+"color:var(--x-collapse-trigger-color);"+"font-size:var(--x-collapse-font-size);"+"font-weight:var(--x-collapse-font-weight);"+"cursor:pointer;"+"user-select:none;"+"}"+"[part=trigger][disabled]{"+"cursor:default;"+"opacity:0.55;"+"}"+"[part=trigger]:hover:not([disabled]){"+"background:var(--x-collapse-trigger-bg-hover);"+"}"+"[part=trigger]:focus-visible{"+"outline:none;"+"box-shadow:inset 0 0 0 2px var(--x-collapse-focus-ring);"+"}"+"[part=header-text]{"+"flex:1;"+"text-align:start;"+"}"+"[part=chevron]{"+"display:inline-block;"+"margin-inline-start:0.5rem;"+"color:var(--x-collapse-chevron-color);"+"transition:transform 200ms ease;"+"flex-shrink:0;"+"}"+":host([open]) [part=chevron]{"+"transform:rotate(180deg);"+"}"+"[part=content]{"+"overflow:hidden;"+"height:0;"+"}"+":host([open]) [part=content]{"+"height:auto;"+"}"+"[part=content-inner]{"+"padding:var(--x-collapse-content-padding);"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=content]{transition:none !important;}"+"[part=chevron]{transition:none !important;}"+"}");
app.components.x_collapse.x_collapse.make_el = (function app$components$x_collapse$x_collapse$make_el(tag){
return document.createElement(tag);
});
app.components.x_collapse.x_collapse.set_attr_BANG_ = (function app$components$x_collapse$x_collapse$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_collapse.x_collapse.remove_attr_BANG_ = (function app$components$x_collapse$x_collapse$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_collapse.x_collapse.has_attr_QMARK_ = (function app$components$x_collapse$x_collapse$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_collapse.x_collapse.get_attr = (function app$components$x_collapse$x_collapse$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_collapse.x_collapse.set_bool_attr_BANG_ = (function app$components$x_collapse$x_collapse$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_collapse.x_collapse.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_collapse.x_collapse.remove_attr_BANG_(el,attr);
}
});
app.components.x_collapse.x_collapse.make_shadow_BANG_ = (function app$components$x_collapse$x_collapse$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_collapse.x_collapse.make_el("style");
var container_el = app.components.x_collapse.x_collapse.make_el("div");
var trigger_el = app.components.x_collapse.x_collapse.make_el("button");
var header_text_el = app.components.x_collapse.x_collapse.make_el("span");
var chevron_el = app.components.x_collapse.x_collapse.make_el("span");
var content_el = app.components.x_collapse.x_collapse.make_el("div");
var content_inner_el = app.components.x_collapse.x_collapse.make_el("div");
var slot_el = app.components.x_collapse.x_collapse.make_el("slot");
(style_el.textContent = app.components.x_collapse.x_collapse.style_text);

app.components.x_collapse.x_collapse.set_attr_BANG_(container_el,"part","container");

app.components.x_collapse.x_collapse.set_attr_BANG_(trigger_el,"part","trigger");

app.components.x_collapse.x_collapse.set_attr_BANG_(trigger_el,"type","button");

app.components.x_collapse.x_collapse.set_attr_BANG_(header_text_el,"part","header-text");

app.components.x_collapse.x_collapse.set_attr_BANG_(chevron_el,"part","chevron");

app.components.x_collapse.x_collapse.set_attr_BANG_(chevron_el,"aria-hidden","true");

app.components.x_collapse.x_collapse.set_attr_BANG_(content_el,"part","content");

app.components.x_collapse.x_collapse.set_attr_BANG_(content_inner_el,"part","content-inner");

app.components.x_collapse.x_collapse.set_attr_BANG_(slot_el,"name",app.components.x_collapse.model.slot_content);

(chevron_el.textContent = "\u25BC");

trigger_el.appendChild(header_text_el);

trigger_el.appendChild(chevron_el);

content_inner_el.appendChild(slot_el);

content_el.appendChild(content_inner_el);

container_el.appendChild(trigger_el);

container_el.appendChild(content_el);

root.appendChild(style_el);

root.appendChild(container_el);

var refs = ({"root": root, "trigger": trigger_el, "header-text": header_text_el, "content": content_el});
app.components.x_collapse.x_collapse.goog$module$goog$object.set(el,app.components.x_collapse.x_collapse.k_refs,refs);

return refs;
});
app.components.x_collapse.x_collapse.prefers_reduced_motion_QMARK_ = (function app$components$x_collapse$x_collapse$prefers_reduced_motion_QMARK_(){
var and__5140__auto__ = window.matchMedia;
if(cljs.core.truth_(and__5140__auto__)){
return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
} else {
return and__5140__auto__;
}
});
app.components.x_collapse.x_collapse.get_duration_ms = (function app$components$x_collapse$x_collapse$get_duration_ms(el){
var d = app.components.x_collapse.x_collapse.get_attr(el,app.components.x_collapse.model.attr_duration_ms);
if(cljs.core.truth_(d)){
var n = parseInt(d,(10));
if(cljs.core.truth_(isNaN(n))){
return app.components.x_collapse.model.default_duration_ms;
} else {
return cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),cljs.core.min.cljs$core$IFn$_invoke$arity$2((2000),n));
}
} else {
return app.components.x_collapse.model.default_duration_ms;
}
});
app.components.x_collapse.x_collapse.start_open_BANG_ = (function app$components$x_collapse$x_collapse$start_open_BANG_(el,content){
if(cljs.core.truth_(app.components.x_collapse.x_collapse.prefers_reduced_motion_QMARK_())){
(content.style.height = "");

return (content.style.transition = "");
} else {
var dur = app.components.x_collapse.x_collapse.get_duration_ms(el);
(content.style.height = "0px");

content.offsetHeight;

var target_h = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(content.scrollHeight)+"px");
(content.style.transition = (""+"height "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(dur)+"ms "+"ease"));

(content.style.height = target_h);

var tid = setTimeout((function (){
(content.style.height = "");

return (content.style.transition = "");
}),(dur + (80)));
var handler_ref = ({"fn": null});
var handler = (function (){
clearTimeout(tid);

(content.style.height = "");

(content.style.transition = "");

return content.removeEventListener("transitionend",app.components.x_collapse.x_collapse.goog$module$goog$object.get(handler_ref,"fn"));
});
app.components.x_collapse.x_collapse.goog$module$goog$object.set(handler_ref,"fn",handler);

return content.addEventListener("transitionend",handler);
}
});
app.components.x_collapse.x_collapse.start_close_BANG_ = (function app$components$x_collapse$x_collapse$start_close_BANG_(el,content){
if(cljs.core.truth_(app.components.x_collapse.x_collapse.prefers_reduced_motion_QMARK_())){
(content.style.height = "0px");

return (content.style.transition = "");
} else {
var current_h = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(content.offsetHeight)+"px");
var dur = app.components.x_collapse.x_collapse.get_duration_ms(el);
(content.style.height = current_h);

content.offsetHeight;

(content.style.transition = (""+"height "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(dur)+"ms "+"ease"));

(content.style.height = "0px");

var tid = setTimeout((function (){
(content.style.height = "");

return (content.style.transition = "");
}),(dur + (80)));
var handler_ref = ({"fn": null});
var handler = (function (){
clearTimeout(tid);

(content.style.height = "");

(content.style.transition = "");

return content.removeEventListener("transitionend",app.components.x_collapse.x_collapse.goog$module$goog$object.get(handler_ref,"fn"));
});
app.components.x_collapse.x_collapse.goog$module$goog$object.set(handler_ref,"fn",handler);

return content.addEventListener("transitionend",handler);
}
});
app.components.x_collapse.x_collapse.render_BANG_ = (function app$components$x_collapse$x_collapse$render_BANG_(el){
var temp__5823__auto__ = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var trigger_el = app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"trigger");
var header_text_el = app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"header-text");
var open_QMARK_ = app.components.x_collapse.x_collapse.has_attr_QMARK_(el,app.components.x_collapse.model.attr_open);
var disabled_QMARK_ = app.components.x_collapse.x_collapse.has_attr_QMARK_(el,app.components.x_collapse.model.attr_disabled);
var header = (function (){var or__5142__auto__ = app.components.x_collapse.x_collapse.get_attr(el,app.components.x_collapse.model.attr_header);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
(header_text_el.textContent = header);

app.components.x_collapse.x_collapse.set_attr_BANG_(trigger_el,"aria-expanded",(cljs.core.truth_(open_QMARK_)?"true":"false"));

if(cljs.core.truth_(disabled_QMARK_)){
return app.components.x_collapse.x_collapse.set_attr_BANG_(trigger_el,"disabled","");
} else {
return app.components.x_collapse.x_collapse.remove_attr_BANG_(trigger_el,"disabled");
}
} else {
return null;
}
});
app.components.x_collapse.x_collapse.dispatch_cancelable_BANG_ = (function app$components$x_collapse$x_collapse$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_collapse.x_collapse.dispatch_BANG_ = (function app$components$x_collapse$x_collapse$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_collapse.x_collapse.toggle_BANG_ = (function app$components$x_collapse$x_collapse$toggle_BANG_(el,source){
if(cljs.core.truth_(app.components.x_collapse.x_collapse.has_attr_QMARK_(el,app.components.x_collapse.model.attr_disabled))){
return null;
} else {
var currently_open_QMARK_ = app.components.x_collapse.x_collapse.has_attr_QMARK_(el,app.components.x_collapse.model.attr_open);
var next_open_QMARK_ = cljs.core.not(currently_open_QMARK_);
var detail = app.components.x_collapse.model.toggle_detail(next_open_QMARK_,source);
var allowed_QMARK_ = app.components.x_collapse.x_collapse.dispatch_cancelable_BANG_(el,app.components.x_collapse.model.event_toggle,detail);
if(allowed_QMARK_){
var refs = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs);
var content_el = (cljs.core.truth_(refs)?app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"content"):null);
if(next_open_QMARK_){
app.components.x_collapse.x_collapse.set_attr_BANG_(el,app.components.x_collapse.model.attr_open,"");
} else {
app.components.x_collapse.x_collapse.remove_attr_BANG_(el,app.components.x_collapse.model.attr_open);
}

app.components.x_collapse.x_collapse.render_BANG_(el);

if(cljs.core.truth_(content_el)){
if(next_open_QMARK_){
app.components.x_collapse.x_collapse.start_open_BANG_(el,content_el);
} else {
app.components.x_collapse.x_collapse.start_close_BANG_(el,content_el);
}
} else {
}

return app.components.x_collapse.x_collapse.dispatch_BANG_(el,app.components.x_collapse.model.event_change,({"open": next_open_QMARK_}));
} else {
return null;
}
}
});
app.components.x_collapse.x_collapse.make_trigger_handler = (function app$components$x_collapse$x_collapse$make_trigger_handler(el){
return (function (_evt){
return app.components.x_collapse.x_collapse.toggle_BANG_(el,"pointer");
});
});
app.components.x_collapse.x_collapse.make_keydown_handler = (function app$components$x_collapse$x_collapse$make_keydown_handler(el){
return (function (evt){
var key = evt.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")))){
evt.preventDefault();

return app.components.x_collapse.x_collapse.toggle_BANG_(el,"keyboard");
} else {
return null;
}
});
});
app.components.x_collapse.x_collapse.add_listeners_BANG_ = (function app$components$x_collapse$x_collapse$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var trigger_el = app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"trigger");
var click_h = app.components.x_collapse.x_collapse.make_trigger_handler(el);
var keydown_h = app.components.x_collapse.x_collapse.make_keydown_handler(el);
var handlers = ({"click": click_h, "keydown": keydown_h});
trigger_el.addEventListener("click",click_h);

trigger_el.addEventListener("keydown",keydown_h);

return app.components.x_collapse.x_collapse.goog$module$goog$object.set(el,app.components.x_collapse.x_collapse.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_collapse.x_collapse.remove_listeners_BANG_ = (function app$components$x_collapse$x_collapse$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var trigger_el_22288 = app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"trigger");
trigger_el_22288.removeEventListener("click",app.components.x_collapse.x_collapse.goog$module$goog$object.get(handlers,"click"));

trigger_el_22288.removeEventListener("keydown",app.components.x_collapse.x_collapse.goog$module$goog$object.get(handlers,"keydown"));

return app.components.x_collapse.x_collapse.goog$module$goog$object.set(el,app.components.x_collapse.x_collapse.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_collapse.x_collapse.connected_BANG_ = (function app$components$x_collapse$x_collapse$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs))){
} else {
app.components.x_collapse.x_collapse.make_shadow_BANG_(el);
}

app.components.x_collapse.x_collapse.remove_listeners_BANG_(el);

app.components.x_collapse.x_collapse.add_listeners_BANG_(el);

return app.components.x_collapse.x_collapse.render_BANG_(el);
});
app.components.x_collapse.x_collapse.disconnected_BANG_ = (function app$components$x_collapse$x_collapse$disconnected_BANG_(el){
return app.components.x_collapse.x_collapse.remove_listeners_BANG_(el);
});
app.components.x_collapse.x_collapse.attribute_changed_BANG_ = (function app$components$x_collapse$x_collapse$attribute_changed_BANG_(el,name,_old,_new){
if(cljs.core.truth_(app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs))){
app.components.x_collapse.x_collapse.render_BANG_(el);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_collapse.model.attr_open)){
var refs = app.components.x_collapse.x_collapse.goog$module$goog$object.get(el,app.components.x_collapse.x_collapse.k_refs);
var content_el = (cljs.core.truth_(refs)?app.components.x_collapse.x_collapse.goog$module$goog$object.get(refs,"content"):null);
var open_QMARK_ = app.components.x_collapse.x_collapse.has_attr_QMARK_(el,app.components.x_collapse.model.attr_open);
if(cljs.core.truth_(content_el)){
if(cljs.core.truth_(open_QMARK_)){
return app.components.x_collapse.x_collapse.start_open_BANG_(el,content_el);
} else {
return app.components.x_collapse.x_collapse.start_close_BANG_(el,content_el);
}
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_collapse.x_collapse.define_bool_prop_BANG_ = (function app$components$x_collapse$x_collapse$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_collapse.x_collapse.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_collapse.x_collapse.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_collapse.x_collapse.define_string_prop_BANG_ = (function app$components$x_collapse$x_collapse$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_collapse.x_collapse.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_collapse.x_collapse.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_collapse.x_collapse.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_collapse.x_collapse.define_number_prop_BANG_ = (function app$components$x_collapse$x_collapse$define_number_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var raw = app.components.x_collapse.x_collapse.get_attr(this$,attr_name);
if(cljs.core.truth_(raw)){
var n = parseInt(raw,(10));
if(cljs.core.truth_(isNaN(n))){
return app.components.x_collapse.model.default_duration_ms;
} else {
return n;
}
} else {
return app.components.x_collapse.model.default_duration_ms;
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'number') && (cljs.core.not(isNaN(v))))){
return app.components.x_collapse.x_collapse.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round(v))));
} else {
return app.components.x_collapse.x_collapse.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_collapse.x_collapse.element_class = (function app$components$x_collapse$x_collapse$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_collapse.model.observed_attributes;
})}));

app.components.x_collapse.x_collapse.define_bool_prop_BANG_(proto,"open",app.components.x_collapse.model.attr_open);

app.components.x_collapse.x_collapse.define_bool_prop_BANG_(proto,"disabled",app.components.x_collapse.model.attr_disabled);

app.components.x_collapse.x_collapse.define_string_prop_BANG_(proto,"header",app.components.x_collapse.model.attr_header);

app.components.x_collapse.x_collapse.define_number_prop_BANG_(proto,"durationMs",app.components.x_collapse.model.attr_duration_ms);

(proto["toggle"] = (function (){
var this$ = this;
return app.components.x_collapse.x_collapse.toggle_BANG_(this$,"programmatic");
}));

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_collapse.x_collapse.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_collapse.x_collapse.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_collapse.x_collapse.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_collapse.x_collapse.init_BANG_ = (function app$components$x_collapse$x_collapse$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_collapse.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_collapse.model.tag_name,app.components.x_collapse.x_collapse.element_class());
}
});

//# sourceMappingURL=app.components.x_collapse.x_collapse.js.map
