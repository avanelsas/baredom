goog.provide('app.components.x_alert.x_alert');
goog.scope(function(){
  app.components.x_alert.x_alert.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_alert.x_alert.k_refs = "__xAlertRefs";
app.components.x_alert.x_alert.k_model = "__xAlertModel";
app.components.x_alert.x_alert.k_handlers = "__xAlertHandlers";
app.components.x_alert.x_alert.k_timeout = "__xAlertTimeout";
app.components.x_alert.x_alert.k_exit_timer = "__xAlertExitTimer";
app.components.x_alert.x_alert.k_exiting = "__xAlertExiting";
app.components.x_alert.x_alert.k_entered = "__xAlertEntered";
app.components.x_alert.x_alert.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-alert-radius:10px;"+"--x-alert-padding-y:10px;"+"--x-alert-padding-x:12px;"+"--x-alert-gap:10px;"+"--x-alert-font-size:0.875rem;"+"--x-alert-motion-fast:120ms;"+"--x-alert-motion-ease:cubic-bezier(0.2,0,0,1);"+"--x-alert-enter-duration:140ms;"+"--x-alert-exit-duration:160ms;"+"--x-alert-press-scale:0.98;"+"--x-alert-disabled-opacity:0.55;"+"--x-alert-focus-ring:rgba(0,0,0,0.6);"+"--x-alert-dismiss-color:rgba(0,0,0,0.62);"+"--x-alert-dismiss-hover-bg:rgba(0,0,0,0.06);"+"--x-alert-info-bg:rgba(0,102,204,0.08);"+"--x-alert-info-border:rgba(0,102,204,0.35);"+"--x-alert-info-color:rgba(0,60,120,0.95);"+"--x-alert-success-bg:rgba(16,140,72,0.10);"+"--x-alert-success-border:rgba(16,140,72,0.35);"+"--x-alert-success-color:rgba(10,90,46,0.95);"+"--x-alert-warning-bg:rgba(204,120,0,0.12);"+"--x-alert-warning-border:rgba(204,120,0,0.45);"+"--x-alert-warning-color:rgba(120,70,0,0.95);"+"--x-alert-error-bg:rgba(190,20,40,0.10);"+"--x-alert-error-border:rgba(190,20,40,0.45);"+"--x-alert-error-color:rgba(120,10,20,0.95);"+"--x-alert-bg:transparent;"+"--x-alert-border-color:transparent;"+"--x-alert-color:inherit;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-alert-focus-ring:rgba(255,255,255,0.75);"+"--x-alert-dismiss-color:rgba(255,255,255,0.75);"+"--x-alert-dismiss-hover-bg:rgba(255,255,255,0.14);"+"--x-alert-info-bg:rgba(80,160,255,0.18);"+"--x-alert-info-border:rgba(80,160,255,0.42);"+"--x-alert-info-color:rgba(230,245,255,0.92);"+"--x-alert-success-bg:rgba(60,210,120,0.18);"+"--x-alert-success-border:rgba(60,210,120,0.42);"+"--x-alert-success-color:rgba(235,255,245,0.92);"+"--x-alert-warning-bg:rgba(255,190,90,0.20);"+"--x-alert-warning-border:rgba(255,190,90,0.46);"+"--x-alert-warning-color:rgba(255,248,235,0.92);"+"--x-alert-error-bg:rgba(255,90,110,0.18);"+"--x-alert-error-border:rgba(255,90,110,0.46);"+"--x-alert-error-color:rgba(255,235,238,0.92);}}"+":host([data-type='info']){"+"--x-alert-bg:var(--x-alert-info-bg);"+"--x-alert-border-color:var(--x-alert-info-border);"+"--x-alert-color:var(--x-alert-info-color);}"+":host([data-type='success']){"+"--x-alert-bg:var(--x-alert-success-bg);"+"--x-alert-border-color:var(--x-alert-success-border);"+"--x-alert-color:var(--x-alert-success-color);}"+":host([data-type='warning']){"+"--x-alert-bg:var(--x-alert-warning-bg);"+"--x-alert-border-color:var(--x-alert-warning-border);"+"--x-alert-color:var(--x-alert-warning-color);}"+":host([data-type='error']){"+"--x-alert-bg:var(--x-alert-error-bg);"+"--x-alert-border-color:var(--x-alert-error-border);"+"--x-alert-color:var(--x-alert-error-color);}"+"[part=container]{"+"display:flex;align-items:flex-start;gap:var(--x-alert-gap);"+"padding:var(--x-alert-padding-y) var(--x-alert-padding-x);"+"border:1px solid var(--x-alert-border-color);"+"border-radius:var(--x-alert-radius);"+"background:var(--x-alert-bg);"+"color:var(--x-alert-color);"+"font-size:var(--x-alert-font-size);"+"transition:"+"background var(--x-alert-motion-fast) var(--x-alert-motion-ease),"+"border-color var(--x-alert-motion-fast) var(--x-alert-motion-ease),"+"color var(--x-alert-motion-fast) var(--x-alert-motion-ease),"+"opacity var(--x-alert-motion-fast) var(--x-alert-motion-ease),"+"transform var(--x-alert-motion-fast) var(--x-alert-motion-ease);}"+"[part=icon]{flex:0 0 auto;line-height:1;margin-top:0.05em;}"+"slot[name=icon]::slotted(*){display:block;}"+"[part=text]{flex:1 1 auto;line-height:1.25;min-width:0;overflow-wrap:anywhere;}"+"[part=dismiss]{"+"flex:0 0 auto;appearance:none;border:0;background:transparent;"+"color:var(--x-alert-dismiss-color);"+"width:1.5em;height:1.5em;padding:0;margin:0;"+"border-radius:999px;cursor:pointer;"+"display:inline-flex;align-items:center;justify-content:center;"+"transition:"+"background var(--x-alert-motion-fast) var(--x-alert-motion-ease),"+"transform var(--x-alert-motion-fast) var(--x-alert-motion-ease);}"+":host(:not([disabled])) [part=dismiss]:hover{background:var(--x-alert-dismiss-hover-bg);}"+":host(:not([disabled])) [part=dismiss]:active{transform:scale(var(--x-alert-press-scale));}"+"[part=dismiss]:focus{outline:none;}"+":host(:not([disabled])) [part=dismiss]:focus-visible{"+"outline:2px solid var(--x-alert-focus-ring);outline-offset:2px;}"+":host([disabled]){opacity:var(--x-alert-disabled-opacity);}"+":host([disabled]) [part=dismiss]{cursor:default;pointer-events:none;}"+":host([data-entering]) [part=container]{"+"animation:x-alert-enter var(--x-alert-enter-duration) var(--x-alert-motion-ease) both;}"+"@keyframes x-alert-enter{"+"from{opacity:0;transform:translateY(4px);}"+"to{opacity:1;transform:translateY(0);}}"+":host([data-exiting]) [part=container]{"+"animation:x-alert-exit var(--x-alert-exit-duration) var(--x-alert-motion-ease) forwards;}"+"@keyframes x-alert-exit{"+"from{opacity:1;transform:translateY(0);}"+"to{opacity:0;transform:translateY(4px);}}"+"@media (prefers-reduced-motion:reduce){"+"[part=container],[part=dismiss]{transition:none !important;}"+":host([data-entering]) [part=container],:host([data-exiting]) [part=container]{"+"animation:none !important;}}");
app.components.x_alert.x_alert.init_dom_BANG_ = (function app$components$x_alert$x_alert$init_dom_BANG_(el){
var root_21726 = el.attachShadow(({"mode": "open"}));
var style_21727 = document.createElement("style");
var container_21728 = document.createElement("div");
var icon_wrap_21729 = document.createElement("span");
var icon_slot_21730 = document.createElement("slot");
var default_icon_21731 = document.createElement("span");
var text_el_21732 = document.createElement("span");
var dismiss_btn_21733 = document.createElement("button");
var dismiss_x_21734 = document.createElement("span");
(style_21727.textContent = app.components.x_alert.x_alert.style_text);

container_21728.setAttribute("part","container");

icon_wrap_21729.setAttribute("part","icon");

icon_wrap_21729.setAttribute("aria-hidden","true");

icon_slot_21730.setAttribute("name","icon");

default_icon_21731.setAttribute("part","default-icon");

icon_slot_21730.appendChild(default_icon_21731);

icon_wrap_21729.appendChild(icon_slot_21730);

text_el_21732.setAttribute("part","text");

dismiss_btn_21733.setAttribute("part","dismiss");

dismiss_btn_21733.setAttribute("type","button");

dismiss_x_21734.setAttribute("aria-hidden","true");

(dismiss_x_21734.textContent = "\u00D7");

dismiss_btn_21733.appendChild(dismiss_x_21734);

container_21728.appendChild(icon_wrap_21729);

container_21728.appendChild(text_el_21732);

container_21728.appendChild(dismiss_btn_21733);

root_21726.appendChild(style_21727);

root_21726.appendChild(container_21728);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_refs,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"root","root",-448657453),root_21726,new cljs.core.Keyword(null,"container","container",-1736937707),container_21728,new cljs.core.Keyword(null,"icon-wrap","icon-wrap",-438741928),icon_wrap_21729,new cljs.core.Keyword(null,"icon-slot","icon-slot",1874301182),icon_slot_21730,new cljs.core.Keyword(null,"default-icon","default-icon",491415788),default_icon_21731,new cljs.core.Keyword(null,"text-el","text-el",-1615414066),text_el_21732,new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928),dismiss_btn_21733], null));

return null;
});
app.components.x_alert.x_alert.ensure_refs_BANG_ = (function app$components$x_alert$x_alert$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_alert.x_alert.init_dom_BANG_(el);

return app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_refs);
}
});
app.components.x_alert.x_alert.read_model = (function app$components$x_alert$x_alert$read_model(el){
return app.components.x_alert.model.normalize(new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"type-raw","type-raw",-967209994),el.getAttribute(app.components.x_alert.model.attr_type),new cljs.core.Keyword(null,"text","text",-1790561697),el.getAttribute(app.components.x_alert.model.attr_text),new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778),el.hasAttribute(app.components.x_alert.model.attr_icon),new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214),el.getAttribute(app.components.x_alert.model.attr_icon),new cljs.core.Keyword(null,"dismissible-attr","dismissible-attr",-2012325753),el.getAttribute(app.components.x_alert.model.attr_dismissible),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_alert.model.attr_disabled),new cljs.core.Keyword(null,"timeout-ms-raw","timeout-ms-raw",-1969949623),el.getAttribute(app.components.x_alert.model.attr_timeout_ms)], null));
});
app.components.x_alert.x_alert.slot_has_content_QMARK_ = (function app$components$x_alert$x_alert$slot_has_content_QMARK_(slot_el){
if(cljs.core.truth_(slot_el)){
return (slot_el.assignedNodes().length > (0));
} else {
return null;
}
});
app.components.x_alert.x_alert.set_host_a11y_BANG_ = (function app$components$x_alert$x_alert$set_host_a11y_BANG_(el,dismissible_QMARK_,disabled_QMARK_){
var interactive_QMARK_ = (function (){var and__5140__auto__ = dismissible_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto__;
}
})();
(el.tabIndex = (cljs.core.truth_(interactive_QMARK_)?(0):(-1)));

if(cljs.core.truth_(disabled_QMARK_)){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

if(cljs.core.truth_(interactive_QMARK_)){
return el.setAttribute("aria-keyshortcuts","Escape");
} else {
return el.removeAttribute("aria-keyshortcuts");
}
});
app.components.x_alert.x_alert.apply_model_BANG_ = (function app$components$x_alert$x_alert$apply_model_BANG_(el,p__21440){
var map__21445 = p__21440;
var map__21445__$1 = cljs.core.__destructure_map(map__21445);
var m = map__21445__$1;
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
var icon_mode = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"icon-mode","icon-mode",-2015012071));
var icon = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"icon","icon",1679606541));
var dismissible_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21445__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var map__21451_21735 = app.components.x_alert.x_alert.ensure_refs_BANG_(el);
var map__21451_21736__$1 = cljs.core.__destructure_map(map__21451_21735);
var container_21737 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var icon_wrap_21738 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"icon-wrap","icon-wrap",-438741928));
var icon_slot_21739 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"icon-slot","icon-slot",1874301182));
var default_icon_21740 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"default-icon","default-icon",491415788));
var text_el_21741 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"text-el","text-el",-1615414066));
var dismiss_btn_21742 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21451_21736__$1,new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928));
var container_21743__$1 = container_21737;
var icon_wrap_21744__$1 = icon_wrap_21738;
var icon_slot_21745__$1 = icon_slot_21739;
var default_icon_21746__$1 = default_icon_21740;
var text_el_21747__$1 = text_el_21741;
var dismiss_btn_21748__$1 = dismiss_btn_21742;
var has_slot_QMARK__21749 = app.components.x_alert.x_alert.slot_has_content_QMARK_(icon_slot_21745__$1);
var fallback_21750 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"custom","custom",340151948)))?icon:app.components.x_alert.model.default_icon_for_type(type));
var hide_icon_QMARK__21751 = ((cljs.core.not(has_slot_QMARK__21749)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"hidden","hidden",-312506092))));
el.setAttribute("data-type",app.components.x_alert.model.type__GT_attr(type));

container_21743__$1.setAttribute("role",app.components.x_alert.model.role_for_type(type));

(text_el_21747__$1.textContent = text);

if(hide_icon_QMARK__21751){
(default_icon_21746__$1.textContent = "");

(icon_wrap_21744__$1.style.display = "none");
} else {
(default_icon_21746__$1.textContent = fallback_21750);

(icon_wrap_21744__$1.style.display = "inline");
}

if(cljs.core.truth_(dismissible_QMARK_)){
(dismiss_btn_21748__$1.disabled = cljs.core.boolean$(disabled_QMARK_));

(dismiss_btn_21748__$1.style.display = "inline-flex");
} else {
(dismiss_btn_21748__$1.disabled = true);

(dismiss_btn_21748__$1.style.display = "none");
}

dismiss_btn_21748__$1.setAttribute("aria-label","Dismiss alert");

app.components.x_alert.x_alert.set_host_a11y_BANG_(el,dismissible_QMARK_,disabled_QMARK_);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_model,m);

return null;
});
app.components.x_alert.x_alert.update_from_attrs_BANG_ = (function app$components$x_alert$x_alert$update_from_attrs_BANG_(el){
var new_m_21752 = app.components.x_alert.x_alert.read_model(el);
var old_m_21753 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21753,new_m_21752)){
app.components.x_alert.x_alert.apply_model_BANG_(el,new_m_21752);
} else {
}

return null;
});
app.components.x_alert.x_alert.prefers_reduced_motion_QMARK_ = (function app$components$x_alert$x_alert$prefers_reduced_motion_QMARK_(){
return cljs.core.boolean$(window.matchMedia("(prefers-reduced-motion:reduce)").matches);
});
app.components.x_alert.x_alert.parse_duration_ms = (function app$components$x_alert$x_alert$parse_duration_ms(s){
var s__$1 = ((typeof s === 'string')?s.trim():"");
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s__$1,"")) || (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s__$1,"0")) || (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s__$1,"0ms")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s__$1,"0s")))))))){
return (0);
} else {
if(cljs.core.truth_(s__$1.endsWith("ms"))){
var n = parseFloat(s__$1.slice((0),(s__$1.length - (2))));
if(cljs.core.truth_(isNaN(n))){
return (0);
} else {
return Math.max((0),Math.round(n));
}
} else {
if(cljs.core.truth_(s__$1.endsWith("s"))){
var n = parseFloat(s__$1.slice((0),(s__$1.length - (1))));
if(cljs.core.truth_(isNaN(n))){
return (0);
} else {
return Math.max((0),Math.round(((1000) * n)));
}
} else {
return (0);

}
}
}
});
app.components.x_alert.x_alert.exit_duration_ms = (function app$components$x_alert$x_alert$exit_duration_ms(el){
var cs = window.getComputedStyle(el);
var v1 = app.components.x_alert.x_alert.parse_duration_ms(cs.getPropertyValue("--x-alert-exit-duration"));
var v2 = app.components.x_alert.x_alert.parse_duration_ms(cs.getPropertyValue("--x-motion-exit-duration"));
if((v1 > (0))){
return v1;
} else {
if((v2 > (0))){
return v2;
} else {
return (160);

}
}
});
app.components.x_alert.x_alert.clear_timeout_BANG_ = (function app$components$x_alert$x_alert$clear_timeout_BANG_(el){
var temp__5823__auto___21754 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_timeout);
if(cljs.core.truth_(temp__5823__auto___21754)){
var tid_21755 = temp__5823__auto___21754;
clearTimeout(tid_21755);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_timeout,null);
} else {
}

return null;
});
app.components.x_alert.x_alert.schedule_timeout_BANG_ = (function app$components$x_alert$x_alert$schedule_timeout_BANG_(el){
app.components.x_alert.x_alert.clear_timeout_BANG_(el);

var m_21756 = (function (){var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.x_alert.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_alert.model.dismiss_eligible_QMARK_(m_21756);
if(cljs.core.truth_(and__5140__auto__)){
return ((typeof new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(m_21756) === 'number') && (cljs.core.not(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting))));
} else {
return and__5140__auto__;
}
})())){
app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_timeout,setTimeout((function (){
app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_timeout,null);

if(cljs.core.truth_((function (){var and__5140__auto__ = el.isConnected;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting));
} else {
return and__5140__auto__;
}
})())){
var m2 = (function (){var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.x_alert.read_model(el);
}
})();
if(cljs.core.truth_(app.components.x_alert.model.dismiss_eligible_QMARK_(m2))){
var detail = cljs.core.clj__GT_js(app.components.x_alert.model.dismiss_detail(m2,"timeout"));
var ev = (new CustomEvent(app.components.x_alert.model.event_dismiss,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
var ok_QMARK_ = el.dispatchEvent(ev);
if(cljs.core.truth_(ok_QMARK_)){
return (app.components.x_alert.x_alert.start_exit_and_remove_BANG_.cljs$core$IFn$_invoke$arity$1 ? app.components.x_alert.x_alert.start_exit_and_remove_BANG_.cljs$core$IFn$_invoke$arity$1(el) : app.components.x_alert.x_alert.start_exit_and_remove_BANG_.call(null,el));
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
}),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(m_21756)));
} else {
}

return null;
});
app.components.x_alert.x_alert.start_enter_BANG_ = (function app$components$x_alert$x_alert$start_enter_BANG_(el){
if(cljs.core.truth_(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_entered))){
} else {
app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_entered,true);

el.setAttribute("data-entering","");

var map__21607_21763 = app.components.x_alert.x_alert.ensure_refs_BANG_(el);
var map__21607_21764__$1 = cljs.core.__destructure_map(map__21607_21763);
var container_21765 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21607_21764__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_21766__$1 = container_21765;
var on_end = (function app$components$x_alert$x_alert$start_enter_BANG__$_on_end(e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.target,container_21766__$1)){
container_21766__$1.removeEventListener("animationend",app$components$x_alert$x_alert$start_enter_BANG__$_on_end);

return el.removeAttribute("data-entering");
} else {
return null;
}
});
container_21766__$1.addEventListener("animationend",on_end);
}

return null;
});
app.components.x_alert.x_alert.start_exit_and_remove_BANG_ = (function app$components$x_alert$x_alert$start_exit_and_remove_BANG_(el){
if(cljs.core.truth_(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting))){
} else {
app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_exiting,true);

app.components.x_alert.x_alert.clear_timeout_BANG_(el);

el.removeAttribute("data-entering");

var dur_21771 = app.components.x_alert.x_alert.exit_duration_ms(el);
if((((dur_21771 === (0))) || (app.components.x_alert.x_alert.prefers_reduced_motion_QMARK_()))){
el.remove();
} else {
var map__21654_21775 = app.components.x_alert.x_alert.ensure_refs_BANG_(el);
var map__21654_21776__$1 = cljs.core.__destructure_map(map__21654_21775);
var container_21777 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21654_21776__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_21778__$1 = container_21777;
var on_end = (function app$components$x_alert$x_alert$start_exit_and_remove_BANG__$_on_end(e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.target,container_21778__$1)){
container_21778__$1.removeEventListener("animationend",app$components$x_alert$x_alert$start_exit_and_remove_BANG__$_on_end);

var temp__5823__auto___21779 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exit_timer);
if(cljs.core.truth_(temp__5823__auto___21779)){
var tid_21780 = temp__5823__auto___21779;
clearTimeout(tid_21780);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_exit_timer,null);
} else {
}

if(cljs.core.truth_(el.isConnected)){
return el.remove();
} else {
return null;
}
} else {
return null;
}
});
el.setAttribute("data-exiting","");

container_21778__$1.addEventListener("animationend",on_end);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_exit_timer,setTimeout((function (){
if(cljs.core.truth_((function (){var and__5140__auto__ = el.isConnected;
if(cljs.core.truth_(and__5140__auto__)){
return app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting);
} else {
return and__5140__auto__;
}
})())){
container_21778__$1.removeEventListener("animationend",on_end);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_exit_timer,null);

return el.remove();
} else {
return null;
}
}),(dur_21771 + (60))));
}
}

return null;
});
app.components.x_alert.x_alert.dispatch_dismiss_BANG_ = (function app$components$x_alert$x_alert$dispatch_dismiss_BANG_(el,reason){
var m = (function (){var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.x_alert.read_model(el);
}
})();
var detail = cljs.core.clj__GT_js(app.components.x_alert.model.dismiss_detail(m,reason));
var ev = (new CustomEvent(app.components.x_alert.model.event_dismiss,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
var ok_QMARK_ = el.dispatchEvent(ev);
if(cljs.core.truth_(ok_QMARK_)){
app.components.x_alert.x_alert.start_exit_and_remove_BANG_(el);
} else {
}

return cljs.core.boolean$(ok_QMARK_);
});
app.components.x_alert.x_alert.on_dismiss_click = (function app$components$x_alert$x_alert$on_dismiss_click(el,e){
var m_21784 = (function (){var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.x_alert.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_alert.model.dismiss_eligible_QMARK_(m_21784);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting));
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

app.components.x_alert.x_alert.dispatch_dismiss_BANG_(el,"button");
} else {
}

return null;
});
app.components.x_alert.x_alert.on_keydown = (function app$components$x_alert$x_alert$on_keydown(el,e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.key,"Escape")){
var m_21788 = (function (){var or__5142__auto__ = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.x_alert.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_alert.model.dismiss_eligible_QMARK_(m_21788);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exiting));
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

app.components.x_alert.x_alert.dispatch_dismiss_BANG_(el,"keyboard");
} else {
}
} else {
}

return null;
});
app.components.x_alert.x_alert.add_listeners_BANG_ = (function app$components$x_alert$x_alert$add_listeners_BANG_(el){
var map__21717_21789 = app.components.x_alert.x_alert.ensure_refs_BANG_(el);
var map__21717_21790__$1 = cljs.core.__destructure_map(map__21717_21789);
var dismiss_btn_21791 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21717_21790__$1,new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928));
var dismiss_btn_21792__$1 = dismiss_btn_21791;
var click_h_21793 = (function (e){
return app.components.x_alert.x_alert.on_dismiss_click(el,e);
});
var key_h_21794 = (function (e){
return app.components.x_alert.x_alert.on_keydown(el,e);
});
dismiss_btn_21792__$1.addEventListener("click",click_h_21793);

el.addEventListener("keydown",key_h_21794);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_handlers,({"click": click_h_21793, "keydown": key_h_21794}));

return null;
});
app.components.x_alert.x_alert.remove_listeners_BANG_ = (function app$components$x_alert$x_alert$remove_listeners_BANG_(el){
app.components.x_alert.x_alert.clear_timeout_BANG_(el);

var temp__5823__auto___21798 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_exit_timer);
if(cljs.core.truth_(temp__5823__auto___21798)){
var tid_21799 = temp__5823__auto___21798;
clearTimeout(tid_21799);

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_exit_timer,null);
} else {
}

var hs_21800 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_handlers);
var refs_21801 = app.components.x_alert.x_alert.goog$module$goog$object.get(el,app.components.x_alert.x_alert.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_21800;
if(cljs.core.truth_(and__5140__auto__)){
return refs_21801;
} else {
return and__5140__auto__;
}
})())){
var btn_21802 = new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928).cljs$core$IFn$_invoke$arity$1(refs_21801);
var click_h_21803 = app.components.x_alert.x_alert.goog$module$goog$object.get(hs_21800,"click");
var key_h_21804 = app.components.x_alert.x_alert.goog$module$goog$object.get(hs_21800,"keydown");
if(cljs.core.truth_(click_h_21803)){
btn_21802.removeEventListener("click",click_h_21803);
} else {
}

if(cljs.core.truth_(key_h_21804)){
el.removeEventListener("keydown",key_h_21804);
} else {
}
} else {
}

app.components.x_alert.x_alert.goog$module$goog$object.set(el,app.components.x_alert.x_alert.k_handlers,null);

return null;
});
app.components.x_alert.x_alert.install_property_accessors_BANG_ = (function app$components$x_alert$x_alert$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_alert.model.attr_type,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_alert.model.attr_type);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_alert.model.attr_type,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_alert.model.attr_type);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_alert.model.attr_text,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_alert.model.attr_text);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_alert.model.attr_text,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_alert.model.attr_text);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_alert.model.attr_icon,({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_alert.model.attr_icon);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_alert.model.attr_icon,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_alert.model.attr_icon);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_alert.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_alert.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_alert.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_alert.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_alert.model.attr_dismissible,({"get": (function (){
var this$ = this;
return app.components.x_alert.model.parse_bool_default_true(this$.getAttribute(app.components.x_alert.model.attr_dismissible));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_alert.model.attr_dismissible,"");
} else {
return this$.setAttribute(app.components.x_alert.model.attr_dismissible,"false");
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"timeoutMs",({"get": (function (){
var this$ = this;
return app.components.x_alert.model.parse_timeout_ms(this$.getAttribute(app.components.x_alert.model.attr_timeout_ms));
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_alert.model.attr_timeout_ms);
} else {
return this$.setAttribute(app.components.x_alert.model.attr_timeout_ms,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v | 0))));
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_alert.x_alert.element_class = (function app$components$x_alert$x_alert$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_alert.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_alert.x_alert.ensure_refs_BANG_(this$);

app.components.x_alert.x_alert.remove_listeners_BANG_(this$);

app.components.x_alert.x_alert.add_listeners_BANG_(this$);

app.components.x_alert.x_alert.update_from_attrs_BANG_(this$);

app.components.x_alert.x_alert.start_enter_BANG_(this$);

app.components.x_alert.x_alert.schedule_timeout_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_alert.x_alert.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_alert.x_alert.update_from_attrs_BANG_(this$);

if(cljs.core.truth_(this$.isConnected)){
app.components.x_alert.x_alert.schedule_timeout_BANG_(this$);
} else {
}
} else {
}

return null;
}));

app.components.x_alert.x_alert.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_alert.x_alert.register_BANG_ = (function app$components$x_alert$x_alert$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_alert.model.tag_name))){
} else {
customElements.define(app.components.x_alert.model.tag_name,app.components.x_alert.x_alert.element_class());
}

return null;
});
app.components.x_alert.x_alert.init_BANG_ = (function app$components$x_alert$x_alert$init_BANG_(){
return app.components.x_alert.x_alert.register_BANG_();
});

//# sourceMappingURL=app.components.x_alert.x_alert.js.map
