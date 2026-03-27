goog.provide('app.components.x_toast.x_toast');
goog.scope(function(){
  app.components.x_toast.x_toast.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_toast.x_toast.k_refs = "__xToastRefs";
app.components.x_toast.x_toast.k_model = "__xToastModel";
app.components.x_toast.x_toast.k_handlers = "__xToastHandlers";
app.components.x_toast.x_toast.k_timeout = "__xToastTimeout";
app.components.x_toast.x_toast.k_exit_timer = "__xToastExitTimer";
app.components.x_toast.x_toast.k_exiting = "__xToastExiting";
app.components.x_toast.x_toast.k_entered = "__xToastEntered";
app.components.x_toast.x_toast.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-toast-radius:12px;"+"--x-toast-padding-y:14px;"+"--x-toast-padding-x:16px;"+"--x-toast-gap:12px;"+"--x-toast-font-size:0.875rem;"+"--x-toast-heading-font-size:0.9375rem;"+"--x-toast-heading-weight:600;"+"--x-toast-min-width:280px;"+"--x-toast-max-width:480px;"+"--x-toast-border-width:1px;"+"--x-toast-shadow:0 4px 16px rgba(0,0,0,0.12),0 1px 4px rgba(0,0,0,0.08);"+"--x-toast-motion-fast:120ms;"+"--x-toast-motion-ease:cubic-bezier(0.2,0,0,1);"+"--x-toast-enter-duration:200ms;"+"--x-toast-exit-duration:180ms;"+"--x-toast-press-scale:0.97;"+"--x-toast-disabled-opacity:0.55;"+"--x-toast-focus-ring:rgba(0,0,0,0.6);"+"--x-toast-dismiss-color:rgba(0,0,0,0.50);"+"--x-toast-dismiss-hover-bg:rgba(0,0,0,0.06);"+"--x-toast-progress-height:3px;"+"--x-toast-progress-bg:rgba(0,0,0,0.08);"+"--x-toast-info-bg:#ffffff;"+"--x-toast-info-border:rgba(0,102,204,0.30);"+"--x-toast-info-color:rgba(15,23,42,0.92);"+"--x-toast-info-icon-color:rgba(0,102,204,0.85);"+"--x-toast-info-progress-fill:rgba(0,102,204,0.70);"+"--x-toast-success-bg:#ffffff;"+"--x-toast-success-border:rgba(16,140,72,0.30);"+"--x-toast-success-color:rgba(15,23,42,0.92);"+"--x-toast-success-icon-color:rgba(16,140,72,0.85);"+"--x-toast-success-progress-fill:rgba(16,140,72,0.70);"+"--x-toast-warning-bg:#ffffff;"+"--x-toast-warning-border:rgba(204,120,0,0.40);"+"--x-toast-warning-color:rgba(15,23,42,0.92);"+"--x-toast-warning-icon-color:rgba(180,100,0,0.85);"+"--x-toast-warning-progress-fill:rgba(204,120,0,0.70);"+"--x-toast-error-bg:#ffffff;"+"--x-toast-error-border:rgba(190,20,40,0.40);"+"--x-toast-error-color:rgba(15,23,42,0.92);"+"--x-toast-error-icon-color:rgba(190,20,40,0.85);"+"--x-toast-error-progress-fill:rgba(190,20,40,0.70);"+"--x-toast-bg:transparent;"+"--x-toast-border-color:transparent;"+"--x-toast-color:inherit;"+"--x-toast-icon-color:inherit;"+"--x-toast-progress-fill:currentColor;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-toast-shadow:0 4px 20px rgba(0,0,0,0.40),0 1px 6px rgba(0,0,0,0.30);"+"--x-toast-focus-ring:rgba(255,255,255,0.75);"+"--x-toast-dismiss-color:rgba(255,255,255,0.65);"+"--x-toast-dismiss-hover-bg:rgba(255,255,255,0.12);"+"--x-toast-progress-bg:rgba(255,255,255,0.10);"+"--x-toast-info-bg:#1e293b;"+"--x-toast-info-border:rgba(80,160,255,0.35);"+"--x-toast-info-color:rgba(248,250,252,0.92);"+"--x-toast-info-icon-color:rgba(96,165,250,0.90);"+"--x-toast-info-progress-fill:rgba(96,165,250,0.75);"+"--x-toast-success-bg:#1e293b;"+"--x-toast-success-border:rgba(60,210,120,0.35);"+"--x-toast-success-color:rgba(248,250,252,0.92);"+"--x-toast-success-icon-color:rgba(52,211,153,0.90);"+"--x-toast-success-progress-fill:rgba(52,211,153,0.75);"+"--x-toast-warning-bg:#1e293b;"+"--x-toast-warning-border:rgba(255,190,90,0.40);"+"--x-toast-warning-color:rgba(248,250,252,0.92);"+"--x-toast-warning-icon-color:rgba(251,191,36,0.90);"+"--x-toast-warning-progress-fill:rgba(251,191,36,0.75);"+"--x-toast-error-bg:#1e293b;"+"--x-toast-error-border:rgba(255,90,110,0.40);"+"--x-toast-error-color:rgba(248,250,252,0.92);"+"--x-toast-error-icon-color:rgba(248,113,113,0.90);"+"--x-toast-error-progress-fill:rgba(248,113,113,0.75);}}"+":host([data-type='info']){"+"--x-toast-bg:var(--x-toast-info-bg);"+"--x-toast-border-color:var(--x-toast-info-border);"+"--x-toast-color:var(--x-toast-info-color);"+"--x-toast-icon-color:var(--x-toast-info-icon-color);"+"--x-toast-progress-fill:var(--x-toast-info-progress-fill);}"+":host([data-type='success']){"+"--x-toast-bg:var(--x-toast-success-bg);"+"--x-toast-border-color:var(--x-toast-success-border);"+"--x-toast-color:var(--x-toast-success-color);"+"--x-toast-icon-color:var(--x-toast-success-icon-color);"+"--x-toast-progress-fill:var(--x-toast-success-progress-fill);}"+":host([data-type='warning']){"+"--x-toast-bg:var(--x-toast-warning-bg);"+"--x-toast-border-color:var(--x-toast-warning-border);"+"--x-toast-color:var(--x-toast-warning-color);"+"--x-toast-icon-color:var(--x-toast-warning-icon-color);"+"--x-toast-progress-fill:var(--x-toast-warning-progress-fill);}"+":host([data-type='error']){"+"--x-toast-bg:var(--x-toast-error-bg);"+"--x-toast-border-color:var(--x-toast-error-border);"+"--x-toast-color:var(--x-toast-error-color);"+"--x-toast-icon-color:var(--x-toast-error-icon-color);"+"--x-toast-progress-fill:var(--x-toast-error-progress-fill);}"+"[part=container]{"+"display:flex;flex-direction:column;"+"background:var(--x-toast-bg);"+"border:var(--x-toast-border-width) solid var(--x-toast-border-color);"+"border-radius:var(--x-toast-radius);"+"box-shadow:var(--x-toast-shadow);"+"color:var(--x-toast-color);"+"min-width:var(--x-toast-min-width);"+"max-width:var(--x-toast-max-width);"+"overflow:hidden;"+"transition:"+"background var(--x-toast-motion-fast) var(--x-toast-motion-ease),"+"border-color var(--x-toast-motion-fast) var(--x-toast-motion-ease),"+"opacity var(--x-toast-motion-fast) var(--x-toast-motion-ease),"+"transform var(--x-toast-motion-fast) var(--x-toast-motion-ease);}"+"[part=inner]{"+"display:flex;align-items:flex-start;gap:var(--x-toast-gap);"+"padding:var(--x-toast-padding-y) var(--x-toast-padding-x);}"+"[part=icon]{"+"flex:0 0 auto;"+"color:var(--x-toast-icon-color);"+"line-height:1;margin-top:0.15em;}"+"slot[name=icon]::slotted(*){display:block;}"+"[part=body]{"+"flex:1 1 auto;display:flex;flex-direction:column;gap:3px;min-width:0;}"+"[part=heading]{"+"font-size:var(--x-toast-heading-font-size);"+"font-weight:var(--x-toast-heading-weight);"+"line-height:1.2;overflow-wrap:anywhere;}"+"[part=message]{"+"font-size:var(--x-toast-font-size);"+"line-height:1.35;overflow-wrap:anywhere;opacity:0.82;}"+"[part=dismiss]{"+"flex:0 0 auto;appearance:none;border:0;background:transparent;"+"color:var(--x-toast-dismiss-color);"+"width:1.5em;height:1.5em;padding:0;margin:0;"+"border-radius:999px;cursor:pointer;"+"display:inline-flex;align-items:center;justify-content:center;"+"transition:"+"background var(--x-toast-motion-fast) var(--x-toast-motion-ease),"+"transform var(--x-toast-motion-fast) var(--x-toast-motion-ease);}"+":host(:not([disabled])) [part=dismiss]:hover{background:var(--x-toast-dismiss-hover-bg);}"+":host(:not([disabled])) [part=dismiss]:active{transform:scale(var(--x-toast-press-scale));}"+"[part=dismiss]:focus{outline:none;}"+":host(:not([disabled])) [part=dismiss]:focus-visible{"+"outline:2px solid var(--x-toast-focus-ring);outline-offset:2px;}"+":host([disabled]){opacity:var(--x-toast-disabled-opacity);}"+":host([disabled]) [part=dismiss]{cursor:default;pointer-events:none;}"+"[part=progress]{"+"height:var(--x-toast-progress-height);"+"background:var(--x-toast-progress-bg);"+"overflow:hidden;display:none;}"+"[part=progress-bar]{"+"height:100%;width:100%;"+"background:var(--x-toast-progress-fill);"+"animation:x-toast-progress var(--x-toast-timeout,0ms) linear forwards;"+"animation-play-state:paused;}"+":host([data-progress-active]) [part=progress-bar]{"+"animation-play-state:running;}"+"@keyframes x-toast-progress{"+"from{width:100%;}"+"to{width:0%;}}"+":host([data-entering]) [part=container]{"+"animation:x-toast-enter var(--x-toast-enter-duration) var(--x-toast-motion-ease) both;}"+"@keyframes x-toast-enter{"+"from{opacity:0;transform:translateY(-8px);}"+"to{opacity:1;transform:translateY(0);}}"+":host([data-exiting]) [part=container]{"+"animation:x-toast-exit var(--x-toast-exit-duration) var(--x-toast-motion-ease) forwards;}"+"@keyframes x-toast-exit{"+"from{opacity:1;transform:translateY(0);}"+"to{opacity:0;transform:translateY(-8px);}}"+"@media (prefers-reduced-motion:reduce){"+"[part=container],[part=dismiss]{transition:none !important;}"+":host([data-entering]) [part=container],:host([data-exiting]) [part=container]{"+"animation:none !important;}"+":host([data-progress-active]) [part=progress-bar]{"+"animation:none !important;}}");
app.components.x_toast.x_toast.init_dom_BANG_ = (function app$components$x_toast$x_toast$init_dom_BANG_(el){
var root_23517 = el.attachShadow(({"mode": "open"}));
var style_23518 = document.createElement("style");
var container_23519 = document.createElement("div");
var inner_23520 = document.createElement("div");
var icon_wrap_23521 = document.createElement("span");
var icon_slot_23522 = document.createElement("slot");
var default_icon_23523 = document.createElement("span");
var body_el_23524 = document.createElement("div");
var heading_el_23525 = document.createElement("span");
var message_el_23526 = document.createElement("span");
var dismiss_btn_23527 = document.createElement("button");
var dismiss_x_23528 = document.createElement("span");
var progress_el_23529 = document.createElement("div");
var progress_bar_23530 = document.createElement("div");
(style_23518.textContent = app.components.x_toast.x_toast.style_text);

container_23519.setAttribute("part","container");

inner_23520.setAttribute("part","inner");

icon_wrap_23521.setAttribute("part","icon");

icon_wrap_23521.setAttribute("aria-hidden","true");

icon_slot_23522.setAttribute("name","icon");

default_icon_23523.setAttribute("part","default-icon");

icon_slot_23522.appendChild(default_icon_23523);

icon_wrap_23521.appendChild(icon_slot_23522);

body_el_23524.setAttribute("part","body");

heading_el_23525.setAttribute("part","heading");

message_el_23526.setAttribute("part","message");

body_el_23524.appendChild(heading_el_23525);

body_el_23524.appendChild(message_el_23526);

dismiss_btn_23527.setAttribute("part","dismiss");

dismiss_btn_23527.setAttribute("type","button");

dismiss_btn_23527.setAttribute("aria-label","Dismiss toast");

dismiss_x_23528.setAttribute("aria-hidden","true");

(dismiss_x_23528.textContent = "\u00D7");

dismiss_btn_23527.appendChild(dismiss_x_23528);

inner_23520.appendChild(icon_wrap_23521);

inner_23520.appendChild(body_el_23524);

inner_23520.appendChild(dismiss_btn_23527);

progress_el_23529.setAttribute("part","progress");

progress_bar_23530.setAttribute("part","progress-bar");

progress_el_23529.appendChild(progress_bar_23530);

container_23519.appendChild(inner_23520);

container_23519.appendChild(progress_el_23529);

root_23517.appendChild(style_23518);

root_23517.appendChild(container_23519);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_refs,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"progress-bar","progress-bar",-123877022),new cljs.core.Keyword(null,"default-icon","default-icon",491415788),new cljs.core.Keyword(null,"message-el","message-el",-683533234),new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928),new cljs.core.Keyword(null,"body-el","body-el",-277974608),new cljs.core.Keyword(null,"inner","inner",-1383171215),new cljs.core.Keyword(null,"root","root",-448657453),new cljs.core.Keyword(null,"container","container",-1736937707),new cljs.core.Keyword(null,"progress-el","progress-el",-184081515),new cljs.core.Keyword(null,"icon-wrap","icon-wrap",-438741928),new cljs.core.Keyword(null,"icon-slot","icon-slot",1874301182),new cljs.core.Keyword(null,"heading-el","heading-el",-1277272161)],[progress_bar_23530,default_icon_23523,message_el_23526,dismiss_btn_23527,body_el_23524,inner_23520,root_23517,container_23519,progress_el_23529,icon_wrap_23521,icon_slot_23522,heading_el_23525]));

return null;
});
app.components.x_toast.x_toast.ensure_refs_BANG_ = (function app$components$x_toast$x_toast$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_toast.x_toast.init_dom_BANG_(el);

return app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_refs);
}
});
app.components.x_toast.x_toast.read_model = (function app$components$x_toast$x_toast$read_model(el){
return app.components.x_toast.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"dismissible-attr","dismissible-attr",-2012325753),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"timeout-ms-raw","timeout-ms-raw",-1969949623),new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778),new cljs.core.Keyword(null,"show-progress-attr","show-progress-attr",1680262066),new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214),new cljs.core.Keyword(null,"type-raw","type-raw",-967209994),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.Keyword(null,"heading","heading",-1312171873)],[el.getAttribute(app.components.x_toast.model.attr_dismissible),el.hasAttribute(app.components.x_toast.model.attr_disabled),el.getAttribute(app.components.x_toast.model.attr_timeout_ms),el.hasAttribute(app.components.x_toast.model.attr_icon),el.getAttribute(app.components.x_toast.model.attr_show_progress),el.getAttribute(app.components.x_toast.model.attr_icon),el.getAttribute(app.components.x_toast.model.attr_type),el.getAttribute(app.components.x_toast.model.attr_message),el.getAttribute(app.components.x_toast.model.attr_heading)]));
});
app.components.x_toast.x_toast.slot_has_content_QMARK_ = (function app$components$x_toast$x_toast$slot_has_content_QMARK_(slot_el){
if(cljs.core.truth_(slot_el)){
return (slot_el.assignedNodes().length > (0));
} else {
return null;
}
});
app.components.x_toast.x_toast.set_host_a11y_BANG_ = (function app$components$x_toast$x_toast$set_host_a11y_BANG_(el,dismissible_QMARK_,disabled_QMARK_){
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
app.components.x_toast.x_toast.apply_model_BANG_ = (function app$components$x_toast$x_toast$apply_model_BANG_(el,p__23487){
var map__23488 = p__23487;
var map__23488__$1 = cljs.core.__destructure_map(map__23488);
var m = map__23488__$1;
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var heading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"heading","heading",-1312171873));
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var icon_mode = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"icon-mode","icon-mode",-2015012071));
var icon = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"icon","icon",1679606541));
var dismissible_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var timeout_ms = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23488__$1,new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406));
var map__23489_23531 = app.components.x_toast.x_toast.ensure_refs_BANG_(el);
var map__23489_23532__$1 = cljs.core.__destructure_map(map__23489_23531);
var icon_slot_23533 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"icon-slot","icon-slot",1874301182));
var heading_el_23534 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"heading-el","heading-el",-1277272161));
var progress_bar_23535 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"progress-bar","progress-bar",-123877022));
var default_icon_23536 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"default-icon","default-icon",491415788));
var message_el_23537 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"message-el","message-el",-683533234));
var dismiss_btn_23538 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928));
var container_23539 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var progress_el_23540 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"progress-el","progress-el",-184081515));
var icon_wrap_23541 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23489_23532__$1,new cljs.core.Keyword(null,"icon-wrap","icon-wrap",-438741928));
var container_23542__$1 = container_23539;
var icon_wrap_23543__$1 = icon_wrap_23541;
var icon_slot_23544__$1 = icon_slot_23533;
var default_icon_23545__$1 = default_icon_23536;
var heading_el_23546__$1 = heading_el_23534;
var message_el_23547__$1 = message_el_23537;
var dismiss_btn_23548__$1 = dismiss_btn_23538;
var progress_el_23549__$1 = progress_el_23540;
var progress_bar_23550__$1 = progress_bar_23535;
var has_slot_QMARK__23551 = app.components.x_toast.x_toast.slot_has_content_QMARK_(icon_slot_23544__$1);
var fallback_23552 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"custom","custom",340151948)))?icon:app.components.x_toast.model.default_icon_for_type(type));
var hide_icon_QMARK__23553 = ((cljs.core.not(has_slot_QMARK__23551)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"hidden","hidden",-312506092))));
var show_prog_QMARK__23554 = app.components.x_toast.model.progress_eligible_QMARK_(m);
el.setAttribute("data-type",app.components.x_toast.model.type__GT_attr(type));

container_23542__$1.setAttribute("role",app.components.x_toast.model.role_for_type(type));

(heading_el_23546__$1.textContent = heading);

(heading_el_23546__$1.style.display = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(heading,""))?"none":""));

(message_el_23547__$1.textContent = message);

if(hide_icon_QMARK__23553){
(default_icon_23545__$1.textContent = "");

(icon_wrap_23543__$1.style.display = "none");
} else {
(default_icon_23545__$1.textContent = fallback_23552);

(icon_wrap_23543__$1.style.display = "inline");
}

if(cljs.core.truth_(dismissible_QMARK_)){
(dismiss_btn_23548__$1.disabled = cljs.core.boolean$(disabled_QMARK_));

(dismiss_btn_23548__$1.style.display = "inline-flex");
} else {
(dismiss_btn_23548__$1.disabled = true);

(dismiss_btn_23548__$1.style.display = "none");
}

if(show_prog_QMARK__23554){
(progress_el_23549__$1.style.display = "block");

progress_bar_23550__$1.style.setProperty("--x-toast-timeout",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(timeout_ms)+"ms"));
} else {
(progress_el_23549__$1.style.display = "none");
}

app.components.x_toast.x_toast.set_host_a11y_BANG_(el,dismissible_QMARK_,disabled_QMARK_);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_model,m);

return null;
});
app.components.x_toast.x_toast.update_from_attrs_BANG_ = (function app$components$x_toast$x_toast$update_from_attrs_BANG_(el){
var new_m_23555 = app.components.x_toast.x_toast.read_model(el);
var old_m_23556 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23556,new_m_23555)){
app.components.x_toast.x_toast.apply_model_BANG_(el,new_m_23555);
} else {
}

return null;
});
app.components.x_toast.x_toast.prefers_reduced_motion_QMARK_ = (function app$components$x_toast$x_toast$prefers_reduced_motion_QMARK_(){
return cljs.core.boolean$(window.matchMedia("(prefers-reduced-motion:reduce)").matches);
});
app.components.x_toast.x_toast.parse_duration_ms = (function app$components$x_toast$x_toast$parse_duration_ms(s){
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
app.components.x_toast.x_toast.exit_duration_ms = (function app$components$x_toast$x_toast$exit_duration_ms(el){
var cs = window.getComputedStyle(el);
var v1 = app.components.x_toast.x_toast.parse_duration_ms(cs.getPropertyValue("--x-toast-exit-duration"));
var v2 = app.components.x_toast.x_toast.parse_duration_ms(cs.getPropertyValue("--x-motion-exit-duration"));
if((v1 > (0))){
return v1;
} else {
if((v2 > (0))){
return v2;
} else {
return (180);

}
}
});
app.components.x_toast.x_toast.clear_timeout_BANG_ = (function app$components$x_toast$x_toast$clear_timeout_BANG_(el){
var temp__5823__auto___23557 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_timeout);
if(cljs.core.truth_(temp__5823__auto___23557)){
var tid_23558 = temp__5823__auto___23557;
clearTimeout(tid_23558);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_timeout,null);
} else {
}

return null;
});
app.components.x_toast.x_toast.schedule_timeout_BANG_ = (function app$components$x_toast$x_toast$schedule_timeout_BANG_(el){
app.components.x_toast.x_toast.clear_timeout_BANG_(el);

var m_23559 = (function (){var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.x_toast.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_toast.model.dismiss_eligible_QMARK_(m_23559);
if(cljs.core.truth_(and__5140__auto__)){
return ((typeof new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(m_23559) === 'number') && (cljs.core.not(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting))));
} else {
return and__5140__auto__;
}
})())){
app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_timeout,setTimeout((function (){
app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_timeout,null);

if(cljs.core.truth_((function (){var and__5140__auto__ = el.isConnected;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting));
} else {
return and__5140__auto__;
}
})())){
var m2 = (function (){var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.x_toast.read_model(el);
}
})();
if(cljs.core.truth_(app.components.x_toast.model.dismiss_eligible_QMARK_(m2))){
var detail = cljs.core.clj__GT_js(app.components.x_toast.model.dismiss_detail(m2,"timeout"));
var ev = (new CustomEvent(app.components.x_toast.model.event_dismiss,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
var ok_QMARK_ = el.dispatchEvent(ev);
if(cljs.core.truth_(ok_QMARK_)){
return (app.components.x_toast.x_toast.start_exit_and_remove_BANG_.cljs$core$IFn$_invoke$arity$1 ? app.components.x_toast.x_toast.start_exit_and_remove_BANG_.cljs$core$IFn$_invoke$arity$1(el) : app.components.x_toast.x_toast.start_exit_and_remove_BANG_.call(null,el));
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
}),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406).cljs$core$IFn$_invoke$arity$1(m_23559)));
} else {
}

return null;
});
app.components.x_toast.x_toast.start_enter_BANG_ = (function app$components$x_toast$x_toast$start_enter_BANG_(el){
if(cljs.core.truth_(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_entered))){
} else {
app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_entered,true);

el.setAttribute("data-entering","");

var map__23492_23560 = app.components.x_toast.x_toast.ensure_refs_BANG_(el);
var map__23492_23561__$1 = cljs.core.__destructure_map(map__23492_23560);
var container_23562 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23492_23561__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_23563__$1 = container_23562;
var on_end = (function app$components$x_toast$x_toast$start_enter_BANG__$_on_end(e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.target,container_23563__$1)){
container_23563__$1.removeEventListener("animationend",app$components$x_toast$x_toast$start_enter_BANG__$_on_end);

el.removeAttribute("data-entering");

var m = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_((function (){var and__5140__auto__ = m;
if(cljs.core.truth_(and__5140__auto__)){
return app.components.x_toast.model.progress_eligible_QMARK_(m);
} else {
return and__5140__auto__;
}
})())){
return el.setAttribute("data-progress-active","");
} else {
return null;
}
} else {
return null;
}
});
container_23563__$1.addEventListener("animationend",on_end);
}

return null;
});
app.components.x_toast.x_toast.start_exit_and_remove_BANG_ = (function app$components$x_toast$x_toast$start_exit_and_remove_BANG_(el){
if(cljs.core.truth_(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting))){
} else {
app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_exiting,true);

app.components.x_toast.x_toast.clear_timeout_BANG_(el);

el.removeAttribute("data-entering");

el.removeAttribute("data-progress-active");

var dur_23564 = app.components.x_toast.x_toast.exit_duration_ms(el);
if((((dur_23564 === (0))) || (app.components.x_toast.x_toast.prefers_reduced_motion_QMARK_()))){
el.remove();
} else {
var map__23493_23565 = app.components.x_toast.x_toast.ensure_refs_BANG_(el);
var map__23493_23566__$1 = cljs.core.__destructure_map(map__23493_23565);
var container_23567 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23493_23566__$1,new cljs.core.Keyword(null,"container","container",-1736937707));
var container_23568__$1 = container_23567;
var on_end = (function app$components$x_toast$x_toast$start_exit_and_remove_BANG__$_on_end(e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.target,container_23568__$1)){
container_23568__$1.removeEventListener("animationend",app$components$x_toast$x_toast$start_exit_and_remove_BANG__$_on_end);

var temp__5823__auto___23569 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exit_timer);
if(cljs.core.truth_(temp__5823__auto___23569)){
var tid_23570 = temp__5823__auto___23569;
clearTimeout(tid_23570);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_exit_timer,null);
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

container_23568__$1.addEventListener("animationend",on_end);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_exit_timer,setTimeout((function (){
if(cljs.core.truth_((function (){var and__5140__auto__ = el.isConnected;
if(cljs.core.truth_(and__5140__auto__)){
return app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting);
} else {
return and__5140__auto__;
}
})())){
container_23568__$1.removeEventListener("animationend",on_end);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_exit_timer,null);

return el.remove();
} else {
return null;
}
}),(dur_23564 + (60))));
}
}

return null;
});
app.components.x_toast.x_toast.dispatch_dismiss_BANG_ = (function app$components$x_toast$x_toast$dispatch_dismiss_BANG_(el,reason){
var m = (function (){var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.x_toast.read_model(el);
}
})();
var detail = cljs.core.clj__GT_js(app.components.x_toast.model.dismiss_detail(m,reason));
var ev = (new CustomEvent(app.components.x_toast.model.event_dismiss,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
var ok_QMARK_ = el.dispatchEvent(ev);
if(cljs.core.truth_(ok_QMARK_)){
app.components.x_toast.x_toast.start_exit_and_remove_BANG_(el);
} else {
}

return cljs.core.boolean$(ok_QMARK_);
});
app.components.x_toast.x_toast.on_dismiss_click = (function app$components$x_toast$x_toast$on_dismiss_click(el,e){
var m_23571 = (function (){var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.x_toast.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_toast.model.dismiss_eligible_QMARK_(m_23571);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting));
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

app.components.x_toast.x_toast.dispatch_dismiss_BANG_(el,"button");
} else {
}

return null;
});
app.components.x_toast.x_toast.on_keydown = (function app$components$x_toast$x_toast$on_keydown(el,e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.key,"Escape")){
var m_23572 = (function (){var or__5142__auto__ = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.x_toast.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_toast.model.dismiss_eligible_QMARK_(m_23572);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exiting));
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

app.components.x_toast.x_toast.dispatch_dismiss_BANG_(el,"keyboard");
} else {
}
} else {
}

return null;
});
app.components.x_toast.x_toast.add_listeners_BANG_ = (function app$components$x_toast$x_toast$add_listeners_BANG_(el){
var map__23497_23573 = app.components.x_toast.x_toast.ensure_refs_BANG_(el);
var map__23497_23574__$1 = cljs.core.__destructure_map(map__23497_23573);
var dismiss_btn_23575 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23497_23574__$1,new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928));
var dismiss_btn_23576__$1 = dismiss_btn_23575;
var click_h_23577 = (function (e){
return app.components.x_toast.x_toast.on_dismiss_click(el,e);
});
var key_h_23578 = (function (e){
return app.components.x_toast.x_toast.on_keydown(el,e);
});
dismiss_btn_23576__$1.addEventListener("click",click_h_23577);

el.addEventListener("keydown",key_h_23578);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_handlers,({"click": click_h_23577, "keydown": key_h_23578}));

return null;
});
app.components.x_toast.x_toast.remove_listeners_BANG_ = (function app$components$x_toast$x_toast$remove_listeners_BANG_(el){
app.components.x_toast.x_toast.clear_timeout_BANG_(el);

var temp__5823__auto___23579 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_exit_timer);
if(cljs.core.truth_(temp__5823__auto___23579)){
var tid_23580 = temp__5823__auto___23579;
clearTimeout(tid_23580);

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_exit_timer,null);
} else {
}

var hs_23581 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_handlers);
var refs_23582 = app.components.x_toast.x_toast.goog$module$goog$object.get(el,app.components.x_toast.x_toast.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_23581;
if(cljs.core.truth_(and__5140__auto__)){
return refs_23582;
} else {
return and__5140__auto__;
}
})())){
var btn_23583 = new cljs.core.Keyword(null,"dismiss-btn","dismiss-btn",-346928).cljs$core$IFn$_invoke$arity$1(refs_23582);
var click_h_23584 = app.components.x_toast.x_toast.goog$module$goog$object.get(hs_23581,"click");
var key_h_23585 = app.components.x_toast.x_toast.goog$module$goog$object.get(hs_23581,"keydown");
if(cljs.core.truth_(click_h_23584)){
btn_23583.removeEventListener("click",click_h_23584);
} else {
}

if(cljs.core.truth_(key_h_23585)){
el.removeEventListener("keydown",key_h_23585);
} else {
}
} else {
}

app.components.x_toast.x_toast.goog$module$goog$object.set(el,app.components.x_toast.x_toast.k_handlers,null);

return null;
});
app.components.x_toast.x_toast.install_property_accessors_BANG_ = (function app$components$x_toast$x_toast$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_toast.model.attr_type,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_toast.model.attr_type);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_type,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_type);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toast.model.attr_heading,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_toast.model.attr_heading);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_heading,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_heading);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toast.model.attr_message,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_toast.model.attr_message);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_message,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_message);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toast.model.attr_icon,({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_toast.model.attr_icon);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_icon,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_icon);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toast.model.attr_dismissible,({"get": (function (){
var this$ = this;
return app.components.x_toast.model.parse_bool_default_true(this$.getAttribute(app.components.x_toast.model.attr_dismissible));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_dismissible,"");
} else {
return this$.setAttribute(app.components.x_toast.model.attr_dismissible,"false");
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toast.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_toast.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"timeoutMs",({"get": (function (){
var this$ = this;
return app.components.x_toast.model.parse_timeout_ms(this$.getAttribute(app.components.x_toast.model.attr_timeout_ms));
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_toast.model.attr_timeout_ms);
} else {
return this$.setAttribute(app.components.x_toast.model.attr_timeout_ms,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v | 0))));
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"showProgress",({"get": (function (){
var this$ = this;
return app.components.x_toast.model.parse_bool_default_false(this$.getAttribute(app.components.x_toast.model.attr_show_progress));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toast.model.attr_show_progress,"");
} else {
return this$.removeAttribute(app.components.x_toast.model.attr_show_progress);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_toast.x_toast.install_dismiss_method_BANG_ = (function app$components$x_toast$x_toast$install_dismiss_method_BANG_(proto){
return (proto.dismiss = (function (reason){
var this$ = this;
var r_23586 = ((typeof reason === 'string')?reason:"api");
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not(app.components.x_toast.x_toast.goog$module$goog$object.get(this$,app.components.x_toast.x_toast.k_exiting));
if(and__5140__auto__){
return this$.isConnected;
} else {
return and__5140__auto__;
}
})())){
app.components.x_toast.x_toast.dispatch_dismiss_BANG_(this$,r_23586);
} else {
}

return null;
}));
});
app.components.x_toast.x_toast.element_class = (function app$components$x_toast$x_toast$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_toast.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_toast.x_toast.ensure_refs_BANG_(this$);

app.components.x_toast.x_toast.remove_listeners_BANG_(this$);

app.components.x_toast.x_toast.add_listeners_BANG_(this$);

app.components.x_toast.x_toast.update_from_attrs_BANG_(this$);

app.components.x_toast.x_toast.start_enter_BANG_(this$);

app.components.x_toast.x_toast.schedule_timeout_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_toast.x_toast.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_toast.x_toast.update_from_attrs_BANG_(this$);

if(cljs.core.truth_(this$.isConnected)){
app.components.x_toast.x_toast.schedule_timeout_BANG_(this$);
} else {
}
} else {
}

return null;
}));

app.components.x_toast.x_toast.install_property_accessors_BANG_(klass.prototype);

app.components.x_toast.x_toast.install_dismiss_method_BANG_(klass.prototype);

return klass;
});
app.components.x_toast.x_toast.register_BANG_ = (function app$components$x_toast$x_toast$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_toast.model.tag_name))){
} else {
customElements.define(app.components.x_toast.model.tag_name,app.components.x_toast.x_toast.element_class());
}

return null;
});
app.components.x_toast.x_toast.init_BANG_ = (function app$components$x_toast$x_toast$init_BANG_(){
return app.components.x_toast.x_toast.register_BANG_();
});

//# sourceMappingURL=app.components.x_toast.x_toast.js.map
