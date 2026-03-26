goog.provide('app.components.x_popover.x_popover');
goog.scope(function(){
  app.components.x_popover.x_popover.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_popover.x_popover.k_refs = "__xPopoverRefs";
app.components.x_popover.x_popover.k_handlers = "__xPopoverHandlers";
app.components.x_popover.x_popover.style_text = (""+":host{"+"display:inline-block;"+"position:relative;"+"color-scheme:light dark;"+"--x-popover-panel-bg:#ffffff;"+"--x-popover-panel-border:1px solid #e2e8f0;"+"--x-popover-panel-radius:8px;"+"--x-popover-panel-shadow:0 4px 16px rgba(0,0,0,0.12);"+"--x-popover-panel-min-width:12rem;"+"--x-popover-panel-max-width:24rem;"+"--x-popover-panel-max-height:24rem;"+"--x-popover-panel-offset:4px;"+"--x-popover-panel-z:1000;"+"--x-popover-header-padding:0.625rem 0.75rem 0.625rem 0.875rem;"+"--x-popover-header-border:1px solid #e2e8f0;"+"--x-popover-heading-color:#0f172a;"+"--x-popover-heading-font-size:0.9375rem;"+"--x-popover-heading-font-weight:600;"+"--x-popover-close-bg:transparent;"+"--x-popover-close-bg-hover:#f1f5f9;"+"--x-popover-close-color:#64748b;"+"--x-popover-close-color-hover:#0f172a;"+"--x-popover-close-radius:4px;"+"--x-popover-close-size:1.5rem;"+"--x-popover-focus-ring:#60a5fa;"+"--x-popover-body-padding:0.875rem;"+"--x-popover-body-color:#334155;"+"--x-popover-body-font-size:0.9375rem;"+"--x-popover-footer-padding:0.625rem 0.875rem;"+"--x-popover-footer-border:1px solid #e2e8f0;"+"--x-popover-arrow-size:8px;"+"--x-popover-arrow-bg:#ffffff;"+"--x-popover-arrow-border:#e2e8f0;"+"--x-popover-transition-duration:150ms;"+"--x-popover-transition-easing:ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-popover-panel-bg:#1e293b;"+"--x-popover-panel-border:1px solid #334155;"+"--x-popover-panel-shadow:0 4px 24px rgba(0,0,0,0.4);"+"--x-popover-header-border:1px solid #334155;"+"--x-popover-heading-color:#e2e8f0;"+"--x-popover-close-bg-hover:#334155;"+"--x-popover-close-color:#94a3b8;"+"--x-popover-close-color-hover:#e2e8f0;"+"--x-popover-focus-ring:#93c5fd;"+"--x-popover-body-color:#cbd5e1;"+"--x-popover-footer-border:1px solid #334155;"+"--x-popover-arrow-bg:#1e293b;"+"--x-popover-arrow-border:#334155;"+"}"+"}"+"[part=trigger]{"+"display:inline-block;"+"}"+"[part=panel]{"+"position:absolute;"+"z-index:var(--x-popover-panel-z);"+"box-sizing:border-box;"+"background:var(--x-popover-panel-bg);"+"border:var(--x-popover-panel-border);"+"border-radius:var(--x-popover-panel-radius);"+"box-shadow:var(--x-popover-panel-shadow);"+"min-width:var(--x-popover-panel-min-width);"+"max-width:var(--x-popover-panel-max-width);"+"max-height:var(--x-popover-panel-max-height);"+"overflow-y:auto;"+"visibility:hidden;"+"pointer-events:none;"+"opacity:0;"+"transform:scale(0.96) translateY(-4px);"+"transition:"+"opacity var(--x-popover-transition-duration) var(--x-popover-transition-easing),"+"transform var(--x-popover-transition-duration) var(--x-popover-transition-easing),"+"visibility 0s var(--x-popover-transition-duration);"+"}"+":host([open]) [part=panel]{"+"visibility:visible;"+"pointer-events:auto;"+"opacity:1;"+"transform:scale(1) translateY(0);"+"transition:"+"opacity var(--x-popover-transition-duration) var(--x-popover-transition-easing),"+"transform var(--x-popover-transition-duration) var(--x-popover-transition-easing),"+"visibility 0s 0s;"+"}"+"[part=panel][data-placement=bottom-start]{"+"top:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"+"left:0;"+"transform-origin:top left;"+"}"+"[part=panel][data-placement=bottom-end]{"+"top:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"+"right:0;"+"transform-origin:top right;"+"}"+"[part=panel][data-placement=top-start]{"+"bottom:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"+"left:0;"+"transform-origin:bottom left;"+"}"+"[part=panel][data-placement=top-end]{"+"bottom:calc(100% + var(--x-popover-arrow-size) + var(--x-popover-panel-offset));"+"right:0;"+"transform-origin:bottom right;"+"}"+"[part=arrow]{"+"position:absolute;"+"width:var(--x-popover-arrow-size);"+"height:var(--x-popover-arrow-size);"+"background:var(--x-popover-arrow-bg);"+"transform:rotate(45deg);"+"pointer-events:none;"+"}"+"[part=panel][data-placement=bottom-start] [part=arrow],"+"[part=panel][data-placement=bottom-end] [part=arrow]{"+"top:calc(var(--x-popover-arrow-size) / -2 - 1px);"+"border-left:1px solid var(--x-popover-arrow-border);"+"border-top:1px solid var(--x-popover-arrow-border);"+"}"+"[part=panel][data-placement=bottom-start] [part=arrow]{"+"left:1rem;"+"}"+"[part=panel][data-placement=bottom-end] [part=arrow]{"+"right:1rem;"+"}"+"[part=panel][data-placement=top-start] [part=arrow],"+"[part=panel][data-placement=top-end] [part=arrow]{"+"bottom:calc(var(--x-popover-arrow-size) / -2 - 1px);"+"border-right:1px solid var(--x-popover-arrow-border);"+"border-bottom:1px solid var(--x-popover-arrow-border);"+"}"+"[part=panel][data-placement=top-start] [part=arrow]{"+"left:1rem;"+"}"+"[part=panel][data-placement=top-end] [part=arrow]{"+"right:1rem;"+"}"+"[part=header]{"+"display:flex;"+"align-items:center;"+"justify-content:space-between;"+"padding:var(--x-popover-header-padding);"+"border-bottom:var(--x-popover-header-border);"+"gap:0.5rem;"+"}"+"[part=heading]{"+"flex:1;"+"font-size:var(--x-popover-heading-font-size);"+"font-weight:var(--x-popover-heading-font-weight);"+"color:var(--x-popover-heading-color);"+"font-family:inherit;"+"}"+"[part=close-button]{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-popover-close-size);"+"height:var(--x-popover-close-size);"+"background:var(--x-popover-close-bg);"+"color:var(--x-popover-close-color);"+"border-radius:var(--x-popover-close-radius);"+"cursor:pointer;"+"flex-shrink:0;"+"transition:background 100ms ease,color 100ms ease;"+"}"+"[part=close-button]:hover{"+"background:var(--x-popover-close-bg-hover);"+"color:var(--x-popover-close-color-hover);"+"}"+"[part=close-button]:focus-visible{"+"outline:none;"+"box-shadow:0 0 0 2px var(--x-popover-focus-ring);"+"}"+"[part=close-button][hidden]{display:none;}"+"[part=body]{"+"padding:var(--x-popover-body-padding);"+"color:var(--x-popover-body-color);"+"font-size:var(--x-popover-body-font-size);"+"font-family:inherit;"+"}"+"[part=footer]{"+"padding:var(--x-popover-footer-padding);"+"border-top:var(--x-popover-footer-border);"+"}"+"[part=footer]:not(:has(slot[name=footer] > *)){"+"display:none;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=panel]{transition:none !important;}"+"[part=close-button]{transition:none !important;}"+"}");
app.components.x_popover.x_popover.make_el = (function app$components$x_popover$x_popover$make_el(tag){
return document.createElement(tag);
});
app.components.x_popover.x_popover.set_attr_BANG_ = (function app$components$x_popover$x_popover$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_popover.x_popover.remove_attr_BANG_ = (function app$components$x_popover$x_popover$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_popover.x_popover.has_attr_QMARK_ = (function app$components$x_popover$x_popover$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_popover.x_popover.get_attr = (function app$components$x_popover$x_popover$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_popover.x_popover.set_bool_attr_BANG_ = (function app$components$x_popover$x_popover$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_popover.x_popover.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_popover.x_popover.remove_attr_BANG_(el,attr);
}
});
app.components.x_popover.x_popover.make_shadow_BANG_ = (function app$components$x_popover$x_popover$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_popover.x_popover.make_el("style");
var trigger_el = app.components.x_popover.x_popover.make_el("span");
var trigger_slot = app.components.x_popover.x_popover.make_el("slot");
var panel_el = app.components.x_popover.x_popover.make_el("div");
var arrow_el = app.components.x_popover.x_popover.make_el("div");
var header_el = app.components.x_popover.x_popover.make_el("div");
var heading_el = app.components.x_popover.x_popover.make_el("span");
var close_btn = app.components.x_popover.x_popover.make_el("button");
var body_el = app.components.x_popover.x_popover.make_el("div");
var body_slot = app.components.x_popover.x_popover.make_el("slot");
var footer_el = app.components.x_popover.x_popover.make_el("div");
var footer_slot = app.components.x_popover.x_popover.make_el("slot");
(style_el.textContent = app.components.x_popover.x_popover.style_text);

app.components.x_popover.x_popover.set_attr_BANG_(trigger_el,"part","trigger");

app.components.x_popover.x_popover.set_attr_BANG_(trigger_slot,"name","trigger");

app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"part","panel");

app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"role","dialog");

app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"inert","");

app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"data-placement",app.components.x_popover.model.default_placement);

app.components.x_popover.x_popover.set_attr_BANG_(arrow_el,"part","arrow");

app.components.x_popover.x_popover.set_attr_BANG_(arrow_el,"aria-hidden","true");

app.components.x_popover.x_popover.set_attr_BANG_(header_el,"part","header");

app.components.x_popover.x_popover.set_attr_BANG_(heading_el,"part","heading");

app.components.x_popover.x_popover.set_attr_BANG_(heading_el,"id","popover-heading");

app.components.x_popover.x_popover.set_attr_BANG_(close_btn,"part","close-button");

app.components.x_popover.x_popover.set_attr_BANG_(close_btn,"type","button");

app.components.x_popover.x_popover.set_attr_BANG_(close_btn,"aria-label",app.components.x_popover.model.default_close_label);

(close_btn.innerHTML = (""+"<svg width=\"14\" height=\"14\" viewBox=\"0 0 14 14\" fill=\"none\""+" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\">"+"<path d=\"M1 1L13 13M13 1L1 13\" stroke=\"currentColor\""+" stroke-width=\"2\" stroke-linecap=\"round\"/></svg>"));

app.components.x_popover.x_popover.set_attr_BANG_(body_el,"part","body");

app.components.x_popover.x_popover.set_attr_BANG_(footer_el,"part","footer");

app.components.x_popover.x_popover.set_attr_BANG_(footer_slot,"name","footer");

trigger_el.appendChild(trigger_slot);

header_el.appendChild(heading_el);

header_el.appendChild(close_btn);

body_el.appendChild(body_slot);

footer_el.appendChild(footer_slot);

panel_el.appendChild(arrow_el);

panel_el.appendChild(header_el);

panel_el.appendChild(body_el);

panel_el.appendChild(footer_el);

root.appendChild(style_el);

root.appendChild(trigger_el);

root.appendChild(panel_el);

var refs = ({"trigger": trigger_el, "panel": panel_el, "heading": heading_el, "closeBtn": close_btn, "body": body_el, "footer": footer_el});
app.components.x_popover.x_popover.goog$module$goog$object.set(el,app.components.x_popover.x_popover.k_refs,refs);

return refs;
});
app.components.x_popover.x_popover.render_BANG_ = (function app$components$x_popover$x_popover$render_BANG_(el){
var temp__5823__auto__ = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var panel_el = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"panel");
var heading_el = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"heading");
var close_btn = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"closeBtn");
var open_QMARK_ = app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open);
var no_close_QMARK_ = app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_no_close);
var heading = (function (){var or__5142__auto__ = app.components.x_popover.x_popover.get_attr(el,app.components.x_popover.model.attr_heading);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var close_label = (function (){var or__5142__auto__ = app.components.x_popover.x_popover.get_attr(el,app.components.x_popover.model.attr_close_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_popover.model.default_close_label;
}
})();
var placement = app.components.x_popover.model.normalize_placement(app.components.x_popover.x_popover.get_attr(el,app.components.x_popover.model.attr_placement));
app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"data-placement",placement);

app.components.x_popover.x_popover.set_bool_attr_BANG_(panel_el,"inert",cljs.core.not(open_QMARK_));

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(heading,"")){
app.components.x_popover.x_popover.set_attr_BANG_(panel_el,"aria-labelledby","popover-heading");
} else {
app.components.x_popover.x_popover.remove_attr_BANG_(panel_el,"aria-labelledby");
}

(heading_el.textContent = heading);

app.components.x_popover.x_popover.set_attr_BANG_(close_btn,"aria-label",close_label);

return app.components.x_popover.x_popover.set_bool_attr_BANG_(close_btn,"hidden",no_close_QMARK_);
} else {
return null;
}
});
app.components.x_popover.x_popover.dispatch_cancelable_BANG_ = (function app$components$x_popover$x_popover$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_popover.x_popover.dispatch_BANG_ = (function app$components$x_popover$x_popover$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_popover.x_popover.do_open_BANG_ = (function app$components$x_popover$x_popover$do_open_BANG_(el,source){
if(((cljs.core.not(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_disabled))) && (cljs.core.not(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))))){
var detail = app.components.x_popover.model.toggle_detail(true,source);
var allowed_QMARK_ = app.components.x_popover.x_popover.dispatch_cancelable_BANG_(el,app.components.x_popover.model.event_toggle,detail);
if(allowed_QMARK_){
app.components.x_popover.x_popover.set_attr_BANG_(el,app.components.x_popover.model.attr_open,"");

return app.components.x_popover.x_popover.dispatch_BANG_(el,app.components.x_popover.model.event_change,({"open": true}));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_popover.x_popover.do_close_BANG_ = (function app$components$x_popover$x_popover$do_close_BANG_(el,source){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_disabled));
if(and__5140__auto__){
return app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open);
} else {
return and__5140__auto__;
}
})())){
var detail = app.components.x_popover.model.toggle_detail(false,source);
var allowed_QMARK_ = app.components.x_popover.x_popover.dispatch_cancelable_BANG_(el,app.components.x_popover.model.event_toggle,detail);
if(allowed_QMARK_){
app.components.x_popover.x_popover.remove_attr_BANG_(el,app.components.x_popover.model.attr_open);

return app.components.x_popover.x_popover.dispatch_BANG_(el,app.components.x_popover.model.event_change,({"open": false}));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_popover.x_popover.toggle_BANG_ = (function app$components$x_popover$x_popover$toggle_BANG_(el,source){
if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
return app.components.x_popover.x_popover.do_close_BANG_(el,source);
} else {
return app.components.x_popover.x_popover.do_open_BANG_(el,source);
}
});
app.components.x_popover.x_popover.add_doc_listeners_BANG_ = (function app$components$x_popover$x_popover$add_doc_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_handlers);
if(cljs.core.truth_(temp__5823__auto__)){
var handlers = temp__5823__auto__;
var doc_click_h = app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"docClick");
var doc_keydown_h = app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"docKeydown");
return setTimeout((function (){
if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
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
app.components.x_popover.x_popover.remove_doc_listeners_BANG_ = (function app$components$x_popover$x_popover$remove_doc_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_handlers);
if(cljs.core.truth_(temp__5823__auto__)){
var handlers = temp__5823__auto__;
var doc_click_h = app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"docClick");
var doc_keydown_h = app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"docKeydown");
document.removeEventListener("click",doc_click_h);

return document.removeEventListener("keydown",doc_keydown_h);
} else {
return null;
}
});
app.components.x_popover.x_popover.make_handlers = (function app$components$x_popover$x_popover$make_handlers(el){
var trigger_click_h = (function (_evt){
return app.components.x_popover.x_popover.toggle_BANG_(el,"pointer");
});
var close_btn_click_h = (function (_evt){
return app.components.x_popover.x_popover.do_close_BANG_(el,"close-button");
});
var host_focusout_h = (function (evt){
if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
var related = evt.relatedTarget;
if((((related == null)) || (cljs.core.not(el.contains(related))))){
return app.components.x_popover.x_popover.do_close_BANG_(el,"focusout");
} else {
return null;
}
} else {
return null;
}
});
var doc_click_h = (function (evt){
if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
var path = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(evt.composedPath());
var inside_QMARK_ = cljs.core.some((function (p1__22921_SHARP_){
return (p1__22921_SHARP_ === el);
}),path);
if(cljs.core.truth_(inside_QMARK_)){
return null;
} else {
return app.components.x_popover.x_popover.do_close_BANG_(el,"outside-click");
}
} else {
return null;
}
});
var doc_keydown_h = (function (evt){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(evt.key,"Escape");
if(and__5140__auto__){
return app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open);
} else {
return and__5140__auto__;
}
})())){
return app.components.x_popover.x_popover.do_close_BANG_(el,"escape");
} else {
return null;
}
});
return ({"triggerClick": trigger_click_h, "closeBtnClick": close_btn_click_h, "hostFocusout": host_focusout_h, "docClick": doc_click_h, "docKeydown": doc_keydown_h});
});
app.components.x_popover.x_popover.add_static_listeners_BANG_ = (function app$components$x_popover$x_popover$add_static_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var trigger_el = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"trigger");
var close_btn = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"closeBtn");
trigger_el.addEventListener("click",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"triggerClick"));

close_btn.addEventListener("click",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"closeBtnClick"));

return el.addEventListener("focusout",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"hostFocusout"));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_popover.x_popover.remove_static_listeners_BANG_ = (function app$components$x_popover$x_popover$remove_static_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var trigger_el = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"trigger");
var close_btn = app.components.x_popover.x_popover.goog$module$goog$object.get(refs,"closeBtn");
trigger_el.removeEventListener("click",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"triggerClick"));

close_btn.removeEventListener("click",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"closeBtnClick"));

return el.removeEventListener("focusout",app.components.x_popover.x_popover.goog$module$goog$object.get(handlers,"hostFocusout"));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_popover.x_popover.connected_BANG_ = (function app$components$x_popover$x_popover$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_refs))){
} else {
app.components.x_popover.x_popover.make_shadow_BANG_(el);
}

app.components.x_popover.x_popover.remove_static_listeners_BANG_(el);

app.components.x_popover.x_popover.remove_doc_listeners_BANG_(el);

app.components.x_popover.x_popover.goog$module$goog$object.set(el,app.components.x_popover.x_popover.k_handlers,app.components.x_popover.x_popover.make_handlers(el));

app.components.x_popover.x_popover.add_static_listeners_BANG_(el);

if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
app.components.x_popover.x_popover.add_doc_listeners_BANG_(el);
} else {
}

return app.components.x_popover.x_popover.render_BANG_(el);
});
app.components.x_popover.x_popover.disconnected_BANG_ = (function app$components$x_popover$x_popover$disconnected_BANG_(el){
app.components.x_popover.x_popover.remove_static_listeners_BANG_(el);

return app.components.x_popover.x_popover.remove_doc_listeners_BANG_(el);
});
app.components.x_popover.x_popover.attribute_changed_BANG_ = (function app$components$x_popover$x_popover$attribute_changed_BANG_(el,name,_old,_new){
if(cljs.core.truth_(app.components.x_popover.x_popover.goog$module$goog$object.get(el,app.components.x_popover.x_popover.k_refs))){
app.components.x_popover.x_popover.render_BANG_(el);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_popover.model.attr_open)){
if(cljs.core.truth_(app.components.x_popover.x_popover.has_attr_QMARK_(el,app.components.x_popover.model.attr_open))){
return app.components.x_popover.x_popover.add_doc_listeners_BANG_(el);
} else {
return app.components.x_popover.x_popover.remove_doc_listeners_BANG_(el);
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_popover.x_popover.define_bool_prop_BANG_ = (function app$components$x_popover$x_popover$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_popover.x_popover.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_popover.x_popover.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_popover.x_popover.define_string_prop_BANG_ = (function app$components$x_popover$x_popover$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_popover.x_popover.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_popover.x_popover.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_popover.x_popover.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_popover.x_popover.element_class = (function app$components$x_popover$x_popover$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_popover.model.observed_attributes;
})}));

app.components.x_popover.x_popover.define_bool_prop_BANG_(proto,"open",app.components.x_popover.model.attr_open);

app.components.x_popover.x_popover.define_bool_prop_BANG_(proto,"disabled",app.components.x_popover.model.attr_disabled);

app.components.x_popover.x_popover.define_bool_prop_BANG_(proto,"noClose",app.components.x_popover.model.attr_no_close);

app.components.x_popover.x_popover.define_string_prop_BANG_(proto,"placement",app.components.x_popover.model.attr_placement);

app.components.x_popover.x_popover.define_string_prop_BANG_(proto,"heading",app.components.x_popover.model.attr_heading);

app.components.x_popover.x_popover.define_string_prop_BANG_(proto,"closeLabel",app.components.x_popover.model.attr_close_label);

(proto["show"] = (function (){
var this$ = this;
return app.components.x_popover.x_popover.do_open_BANG_(this$,"programmatic");
}));

(proto["hide"] = (function (){
var this$ = this;
return app.components.x_popover.x_popover.do_close_BANG_(this$,"programmatic");
}));

(proto["toggle"] = (function (){
var this$ = this;
return app.components.x_popover.x_popover.toggle_BANG_(this$,"programmatic");
}));

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_popover.x_popover.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_popover.x_popover.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_popover.x_popover.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_popover.x_popover.init_BANG_ = (function app$components$x_popover$x_popover$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_popover.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_popover.model.tag_name,app.components.x_popover.x_popover.element_class());
}
});

//# sourceMappingURL=app.components.x_popover.x_popover.js.map
