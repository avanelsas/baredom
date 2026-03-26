goog.provide('app.components.x_cancel_dialogue.x_cancel_dialogue');
goog.scope(function(){
  app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs = "__xCancelDialogueRefs";
app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers = "__xCancelDialogueHandlers";
app.components.x_cancel_dialogue.x_cancel_dialogue.id_state = ({"n": (0)});
app.components.x_cancel_dialogue.x_cancel_dialogue.next_id_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$next_id_BANG_(){
var n = (app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(app.components.x_cancel_dialogue.x_cancel_dialogue.id_state,"n") + (1));
app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(app.components.x_cancel_dialogue.x_cancel_dialogue.id_state,"n",n);

return (""+"x-cd-headline-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n));
});
app.components.x_cancel_dialogue.x_cancel_dialogue.style_text = (""+":host{"+"display:contents;"+"color-scheme:light dark;"+"--x-cancel-dialogue-z-base:1000;"+"--x-cancel-dialogue-backdrop-bg:rgba(0,0,0,0.45);"+"--x-cancel-dialogue-bg:#ffffff;"+"--x-cancel-dialogue-fg:#0f172a;"+"--x-cancel-dialogue-radius:12px;"+"--x-cancel-dialogue-shadow:0 20px 60px rgba(0,0,0,0.20),0 4px 12px rgba(0,0,0,0.10);"+"--x-cancel-dialogue-padding:24px;"+"--x-cancel-dialogue-min-width:320px;"+"--x-cancel-dialogue-max-width:480px;"+"--x-cancel-dialogue-headline-size:1.125rem;"+"--x-cancel-dialogue-headline-weight:700;"+"--x-cancel-dialogue-message-size:0.9375rem;"+"--x-cancel-dialogue-cancel-bg:#f1f5f9;"+"--x-cancel-dialogue-cancel-fg:#0f172a;"+"--x-cancel-dialogue-cancel-bg-hover:#e2e8f0;"+"--x-cancel-dialogue-confirm-bg:#2563eb;"+"--x-cancel-dialogue-confirm-fg:#ffffff;"+"--x-cancel-dialogue-confirm-bg-hover:#1d4ed8;"+"--x-cancel-dialogue-danger-bg:#dc2626;"+"--x-cancel-dialogue-danger-fg:#ffffff;"+"--x-cancel-dialogue-danger-bg-hover:#b91c1c;"+"--x-cancel-dialogue-btn-radius:8px;"+"--x-cancel-dialogue-btn-height:2.5rem;"+"--x-cancel-dialogue-btn-font-size:0.9375rem;"+"--x-cancel-dialogue-btn-font-weight:600;"+"--x-cancel-dialogue-focus-ring:#60a5fa;"+"--x-cancel-dialogue-enter-duration:0ms;"+"--x-cancel-dialogue-exit-duration:0ms;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-cancel-dialogue-bg:#1e293b;"+"--x-cancel-dialogue-fg:#f1f5f9;"+"--x-cancel-dialogue-backdrop-bg:rgba(0,0,0,0.60);"+"--x-cancel-dialogue-shadow:0 20px 60px rgba(0,0,0,0.55),0 4px 12px rgba(0,0,0,0.30);"+"--x-cancel-dialogue-cancel-bg:#334155;"+"--x-cancel-dialogue-cancel-fg:#f1f5f9;"+"--x-cancel-dialogue-cancel-bg-hover:#475569;"+"--x-cancel-dialogue-confirm-bg:#3b82f6;"+"--x-cancel-dialogue-confirm-bg-hover:#2563eb;"+"--x-cancel-dialogue-focus-ring:#93c5fd;"+"}}"+"[part=backdrop]{"+"display:none;"+"position:fixed;"+"inset:0;"+"background:var(--x-cancel-dialogue-backdrop-bg);"+"z-index:var(--x-cancel-dialogue-z-base,1000);"+"}"+"[part=dialog]{"+"display:none;"+"position:fixed;"+"inset:0;"+"align-items:center;"+"justify-content:center;"+"z-index:calc(var(--x-cancel-dialogue-z-base,1000) + 1);"+"padding:16px;"+"box-sizing:border-box;"+"}"+":host([open]) [part=backdrop]{display:block;}"+":host([open]) [part=dialog]{display:flex;}"+"[part=panel]{"+"background:var(--x-cancel-dialogue-bg);"+"color:var(--x-cancel-dialogue-fg);"+"border-radius:var(--x-cancel-dialogue-radius);"+"box-shadow:var(--x-cancel-dialogue-shadow);"+"padding:var(--x-cancel-dialogue-padding);"+"min-width:var(--x-cancel-dialogue-min-width);"+"max-width:var(--x-cancel-dialogue-max-width);"+"width:100%;"+"box-sizing:border-box;"+"display:flex;"+"flex-direction:column;"+"gap:16px;"+"}"+"[part=header]{display:flex;align-items:flex-start;}"+"[part=headline]{"+"margin:0;"+"font-size:var(--x-cancel-dialogue-headline-size);"+"font-weight:var(--x-cancel-dialogue-headline-weight);"+"line-height:1.3;"+"}"+"[part=body]{display:flex;flex-direction:column;gap:8px;}"+"[part=message]{"+"font-size:var(--x-cancel-dialogue-message-size);"+"line-height:1.5;"+"margin:0;"+"opacity:0.8;"+"}"+"[part=actions]{"+"display:flex;"+"gap:8px;"+"justify-content:flex-end;"+"flex-wrap:wrap;"+"}"+"[part=cancel-btn],[part=confirm-btn]{"+"all:unset;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"box-sizing:border-box;"+"padding:0 20px;"+"min-height:var(--x-cancel-dialogue-btn-height);"+"border-radius:var(--x-cancel-dialogue-btn-radius);"+"font-size:var(--x-cancel-dialogue-btn-font-size);"+"font-weight:var(--x-cancel-dialogue-btn-font-weight);"+"cursor:pointer;"+"user-select:none;"+"white-space:nowrap;"+"}"+"[part=cancel-btn]{"+"background:var(--x-cancel-dialogue-cancel-bg);"+"color:var(--x-cancel-dialogue-cancel-fg);"+"}"+"[part=cancel-btn]:hover:not(:disabled){"+"background:var(--x-cancel-dialogue-cancel-bg-hover);"+"}"+"[part=confirm-btn]{"+"background:var(--x-cancel-dialogue-confirm-bg);"+"color:var(--x-cancel-dialogue-confirm-fg);"+"}"+"[part=confirm-btn]:hover:not(:disabled){"+"background:var(--x-cancel-dialogue-confirm-bg-hover);"+"}"+"[part=confirm-btn][data-danger]{"+"background:var(--x-cancel-dialogue-danger-bg);"+"color:var(--x-cancel-dialogue-danger-fg);"+"}"+"[part=confirm-btn][data-danger]:hover:not(:disabled){"+"background:var(--x-cancel-dialogue-danger-bg-hover);"+"}"+"[part=cancel-btn]:focus-visible,[part=confirm-btn]:focus-visible{"+"outline:2px solid var(--x-cancel-dialogue-focus-ring);"+"outline-offset:2px;"+"}"+":host([disabled]) [part=cancel-btn],:host([disabled]) [part=confirm-btn]{"+"opacity:0.55;"+"pointer-events:none;"+"cursor:default;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=backdrop],[part=dialog],[part=panel]{transition:none;animation:none;}"+"}");
app.components.x_cancel_dialogue.x_cancel_dialogue.make_el = (function app$components$x_cancel_dialogue$x_cancel_dialogue$make_el(tag){
return document.createElement(tag);
});
app.components.x_cancel_dialogue.x_cancel_dialogue.init_dom_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$init_dom_BANG_(el){
var headline_id_22084 = app.components.x_cancel_dialogue.x_cancel_dialogue.next_id_BANG_();
var root_22085 = el.attachShadow(({"mode": "open"}));
var style_22086 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("style");
var backdrop_22087 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var dialog_22088 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var panel_22089 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var header_22090 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var headline_22091 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("h2");
var body_22092 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var body_slot_22093 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("slot");
var message_el_22094 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("p");
var actions_22095 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var cancel_btn_22096 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("button");
var confirm_btn_22097 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("button");
(style_22086.textContent = app.components.x_cancel_dialogue.x_cancel_dialogue.style_text);

backdrop_22087.setAttribute("part","backdrop");

dialog_22088.setAttribute("part","dialog");

dialog_22088.setAttribute("role","dialog");

dialog_22088.setAttribute("aria-modal","true");

dialog_22088.setAttribute("aria-labelledby",headline_id_22084);

panel_22089.setAttribute("part","panel");

header_22090.setAttribute("part","header");

headline_22091.setAttribute("part","headline");

headline_22091.setAttribute("id",headline_id_22084);

body_22092.setAttribute("part","body");

body_slot_22093.setAttribute("name","body");

message_el_22094.setAttribute("part","message");

actions_22095.setAttribute("part","actions");

cancel_btn_22096.setAttribute("part","cancel-btn");

cancel_btn_22096.setAttribute("type","button");

confirm_btn_22097.setAttribute("part","confirm-btn");

confirm_btn_22097.setAttribute("type","button");

header_22090.appendChild(headline_22091);

body_22092.appendChild(body_slot_22093);

body_22092.appendChild(message_el_22094);

actions_22095.appendChild(cancel_btn_22096);

actions_22095.appendChild(confirm_btn_22097);

panel_22089.appendChild(header_22090);

panel_22089.appendChild(body_22092);

panel_22089.appendChild(actions_22095);

dialog_22088.appendChild(panel_22089);

root_22085.appendChild(style_22086);

root_22085.appendChild(backdrop_22087);

root_22085.appendChild(dialog_22088);

app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs,({"headline": headline_22091, "confirmBtn": confirm_btn_22097, "panel": panel_22089, "bodySlot": body_slot_22093, "root": root_22085, "dialog": dialog_22088, "backdrop": backdrop_22087, "cancelBtn": cancel_btn_22096, "message": message_el_22094}));

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_cancel_dialogue.x_cancel_dialogue.init_dom_BANG_(el);

return app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
}
});
app.components.x_cancel_dialogue.x_cancel_dialogue.read_model = (function app$components$x_cancel_dialogue$x_cancel_dialogue$read_model(el){
return app.components.x_cancel_dialogue.model.normalize(new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"open-present?","open-present?",965047899),el.hasAttribute(app.components.x_cancel_dialogue.model.attr_open),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_cancel_dialogue.model.attr_disabled),new cljs.core.Keyword(null,"headline-raw","headline-raw",1141142757),el.getAttribute(app.components.x_cancel_dialogue.model.attr_headline),new cljs.core.Keyword(null,"message-raw","message-raw",952235685),el.getAttribute(app.components.x_cancel_dialogue.model.attr_message),new cljs.core.Keyword(null,"confirm-text-raw","confirm-text-raw",-1317418708),el.getAttribute(app.components.x_cancel_dialogue.model.attr_confirm_text),new cljs.core.Keyword(null,"cancel-text-raw","cancel-text-raw",615622099),el.getAttribute(app.components.x_cancel_dialogue.model.attr_cancel_text),new cljs.core.Keyword(null,"danger-present?","danger-present?",-439648505),el.hasAttribute(app.components.x_cancel_dialogue.model.attr_danger),new cljs.core.Keyword(null,"portal-raw","portal-raw",2293766),el.getAttribute(app.components.x_cancel_dialogue.model.attr_portal)], null));
});
app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$dispatch_BANG_(el,event_name,detail,cancelable_QMARK_){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cancelable_QMARK_})));
el.dispatchEvent(ev);

return ev;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.do_open_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$do_open_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_cancel_dialogue.model.attr_open))){
} else {
el.setAttribute(app.components.x_cancel_dialogue.model.attr_open,"");
}

var refs_22103 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var confirm_22104 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22103,"confirmBtn");
setTimeout((function (){
return confirm_22104.focus();
}),(0));

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$do_close_BANG_(el){
el.removeAttribute(app.components.x_cancel_dialogue.model.attr_open);

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$do_cancel_BANG_(el,reason){
if(cljs.core.truth_(el.hasAttribute(app.components.x_cancel_dialogue.model.attr_disabled))){
} else {
var req_ev_22105 = app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_cancel_request,app.components.x_cancel_dialogue.model.cancel_request_detail(reason),true);
if(cljs.core.truth_(req_ev_22105.defaultPrevented)){
} else {
app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_cancel,({}),false);
}
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.do_confirm_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$do_confirm_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_cancel_dialogue.model.attr_disabled))){
} else {
var req_ev_22106 = app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_confirm_request,app.components.x_cancel_dialogue.model.confirm_request_detail(),true);
if(cljs.core.truth_(req_ev_22106.defaultPrevented)){
} else {
app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_confirm,({}),false);
}
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.on_keydown = (function app$components$x_cancel_dialogue$x_cancel_dialogue$on_keydown(el,e){
var key_22107 = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22107,"Escape")){
e.preventDefault();

app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"escape");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22107,"Tab")){
var refs_22108 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var cancel_22109 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22108,"cancelBtn");
var confirm_22110 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22108,"confirmBtn");
var shift_QMARK__22111 = e.shiftKey;
var active_22112 = el.shadowRoot.activeElement;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK__22111;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22112,cancel_22109);
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

confirm_22110.focus();
} else {
if(((cljs.core.not(shift_QMARK__22111)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22112,confirm_22110)))){
e.preventDefault();

cancel_22109.focus();
} else {
}
}
} else {
}
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.render_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$render_BANG_(el){
var refs_22113 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var headline_22114 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22113,"headline");
var message_22115 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22113,"message");
var cancel_22116 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22113,"cancelBtn");
var confirm_22117 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22113,"confirmBtn");
var m_22118 = app.components.x_cancel_dialogue.x_cancel_dialogue.read_model(el);
(headline_22114.textContent = new cljs.core.Keyword(null,"headline","headline",-157157727).cljs$core$IFn$_invoke$arity$1(m_22118));

(cancel_22116.textContent = new cljs.core.Keyword(null,"cancel-text","cancel-text",1885137831).cljs$core$IFn$_invoke$arity$1(m_22118));

(confirm_22117.textContent = new cljs.core.Keyword(null,"confirm-text","confirm-text",-1839494031).cljs$core$IFn$_invoke$arity$1(m_22118));

if(cljs.core.truth_(new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(m_22118))){
(message_22115.textContent = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(m_22118));

(message_22115.style.display = "block");
} else {
(message_22115.textContent = "");

(message_22115.style.display = "none");
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"danger?","danger?",181682216).cljs$core$IFn$_invoke$arity$1(m_22118))){
confirm_22117.setAttribute("data-danger","");
} else {
confirm_22117.removeAttribute("data-danger");
}

(cancel_22116.disabled = cljs.core.boolean$(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m_22118)));

(confirm_22117.disabled = cljs.core.boolean$(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m_22118)));

var dialog_22119 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22113,"dialog");
dialog_22119.setAttribute("aria-modal","true");

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.add_listeners_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$add_listeners_BANG_(el){
var refs_22120 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var backdrop_22121 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22120,"backdrop");
var cancel_22122 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22120,"cancelBtn");
var confirm_22123 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22120,"confirmBtn");
var dialog_22124 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22120,"dialog");
var backdrop_h_22125 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"backdrop");
});
var cancel_h_22126 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"cancel-button");
});
var confirm_h_22127 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_confirm_BANG_(el);
});
var keydown_h_22128 = (function (e){
return app.components.x_cancel_dialogue.x_cancel_dialogue.on_keydown(el,e);
});
backdrop_22121.addEventListener("click",backdrop_h_22125);

cancel_22122.addEventListener("click",cancel_h_22126);

confirm_22123.addEventListener("click",confirm_h_22127);

dialog_22124.addEventListener("keydown",keydown_h_22128);

app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers,({"backdrop": backdrop_h_22125, "cancel": cancel_h_22126, "confirm": confirm_h_22127, "keydown": keydown_h_22128}));

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.remove_listeners_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$remove_listeners_BANG_(el){
var hs_22129 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers);
var refs_22130 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22129;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22130;
} else {
return and__5140__auto__;
}
})())){
var backdrop_22131 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22130,"backdrop");
var cancel_22132 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22130,"cancelBtn");
var confirm_22133 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22130,"confirmBtn");
var dialog_22134 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22130,"dialog");
var backdrop_h_22135 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22129,"backdrop");
var cancel_h_22136 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22129,"cancel");
var confirm_h_22137 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22129,"confirm");
var keydown_h_22138 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22129,"keydown");
if(cljs.core.truth_(backdrop_h_22135)){
backdrop_22131.removeEventListener("click",backdrop_h_22135);
} else {
}

if(cljs.core.truth_(cancel_h_22136)){
cancel_22132.removeEventListener("click",cancel_h_22136);
} else {
}

if(cljs.core.truth_(confirm_h_22137)){
confirm_22133.removeEventListener("click",confirm_h_22137);
} else {
}

if(cljs.core.truth_(keydown_h_22138)){
dialog_22134.removeEventListener("keydown",keydown_h_22138);
} else {
}
} else {
}

app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers,null);

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.connected_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$connected_BANG_(el){
app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.remove_listeners_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.add_listeners_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.render_BANG_(el);

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.disconnected_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$disconnected_BANG_(el){
app.components.x_cancel_dialogue.x_cancel_dialogue.remove_listeners_BANG_(el);

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.attribute_changed_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$attribute_changed_BANG_(el,attr_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_cancel_dialogue.x_cancel_dialogue.render_BANG_(el);

if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(attr_name,app.components.x_cancel_dialogue.model.attr_open)) && ((((!((new_val == null)))) && ((old_val == null)))))){
var refs_22139 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
var confirm_22140 = (cljs.core.truth_(refs_22139)?app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22139,"confirmBtn"):null);
if(cljs.core.truth_(confirm_22140)){
setTimeout((function (){
return confirm_22140.focus();
}),(0));
} else {
}
} else {
}
} else {
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.install_properties_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$install_properties_BANG_(proto){
Object.defineProperty(proto,"open",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_cancel_dialogue.model.attr_open);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_open_BANG_(this$);
} else {
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_(this$);
}
}), "enumerable": true, "configurable": true}));

var seq__22048_22141 = cljs.core.seq(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["disabled",app.components.x_cancel_dialogue.model.attr_disabled], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["danger",app.components.x_cancel_dialogue.model.attr_danger], null)], null));
var chunk__22049_22142 = null;
var count__22050_22143 = (0);
var i__22051_22144 = (0);
while(true){
if((i__22051_22144 < count__22050_22143)){
var vec__22059_22149 = chunk__22049_22142.cljs$core$IIndexed$_nth$arity$2(null,i__22051_22144);
var js_prop_22150 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22059_22149,(0),null);
var attr_name_22151 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22059_22149,(1),null);
Object.defineProperty(proto,js_prop_22150,({"get": ((function (seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22059_22149,js_prop_22150,attr_name_22151){
return (function (){
var this$ = this;
return this$.hasAttribute(attr_name_22151);
});})(seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22059_22149,js_prop_22150,attr_name_22151))
, "set": ((function (seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22059_22149,js_prop_22150,attr_name_22151){
return (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name_22151,"");
} else {
return this$.removeAttribute(attr_name_22151);
}
});})(seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22059_22149,js_prop_22150,attr_name_22151))
, "enumerable": true, "configurable": true}));


var G__22152 = seq__22048_22141;
var G__22153 = chunk__22049_22142;
var G__22154 = count__22050_22143;
var G__22155 = (i__22051_22144 + (1));
seq__22048_22141 = G__22152;
chunk__22049_22142 = G__22153;
count__22050_22143 = G__22154;
i__22051_22144 = G__22155;
continue;
} else {
var temp__5823__auto___22156 = cljs.core.seq(seq__22048_22141);
if(temp__5823__auto___22156){
var seq__22048_22157__$1 = temp__5823__auto___22156;
if(cljs.core.chunked_seq_QMARK_(seq__22048_22157__$1)){
var c__5673__auto___22158 = cljs.core.chunk_first(seq__22048_22157__$1);
var G__22159 = cljs.core.chunk_rest(seq__22048_22157__$1);
var G__22160 = c__5673__auto___22158;
var G__22161 = cljs.core.count(c__5673__auto___22158);
var G__22162 = (0);
seq__22048_22141 = G__22159;
chunk__22049_22142 = G__22160;
count__22050_22143 = G__22161;
i__22051_22144 = G__22162;
continue;
} else {
var vec__22062_22163 = cljs.core.first(seq__22048_22157__$1);
var js_prop_22164 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22062_22163,(0),null);
var attr_name_22165 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22062_22163,(1),null);
Object.defineProperty(proto,js_prop_22164,({"get": ((function (seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22062_22163,js_prop_22164,attr_name_22165,seq__22048_22157__$1,temp__5823__auto___22156){
return (function (){
var this$ = this;
return this$.hasAttribute(attr_name_22165);
});})(seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22062_22163,js_prop_22164,attr_name_22165,seq__22048_22157__$1,temp__5823__auto___22156))
, "set": ((function (seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22062_22163,js_prop_22164,attr_name_22165,seq__22048_22157__$1,temp__5823__auto___22156){
return (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name_22165,"");
} else {
return this$.removeAttribute(attr_name_22165);
}
});})(seq__22048_22141,chunk__22049_22142,count__22050_22143,i__22051_22144,vec__22062_22163,js_prop_22164,attr_name_22165,seq__22048_22157__$1,temp__5823__auto___22156))
, "enumerable": true, "configurable": true}));


var G__22166 = cljs.core.next(seq__22048_22157__$1);
var G__22167 = null;
var G__22168 = (0);
var G__22169 = (0);
seq__22048_22141 = G__22166;
chunk__22049_22142 = G__22167;
count__22050_22143 = G__22168;
i__22051_22144 = G__22169;
continue;
}
} else {
}
}
break;
}

var seq__22065_22170 = cljs.core.seq(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["headline",app.components.x_cancel_dialogue.model.attr_headline], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["message",app.components.x_cancel_dialogue.model.attr_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["confirmText",app.components.x_cancel_dialogue.model.attr_confirm_text], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["cancelText",app.components.x_cancel_dialogue.model.attr_cancel_text], null)], null));
var chunk__22066_22171 = null;
var count__22067_22172 = (0);
var i__22068_22173 = (0);
while(true){
if((i__22068_22173 < count__22067_22172)){
var vec__22075_22174 = chunk__22066_22171.cljs$core$IIndexed$_nth$arity$2(null,i__22068_22173);
var js_prop_22175 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22075_22174,(0),null);
var attr_name_22176 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22075_22174,(1),null);
Object.defineProperty(proto,js_prop_22175,({"get": ((function (seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22075_22174,js_prop_22175,attr_name_22176){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22176);
});})(seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22075_22174,js_prop_22175,attr_name_22176))
, "set": ((function (seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22075_22174,js_prop_22175,attr_name_22176){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22176);
} else {
return this$.setAttribute(attr_name_22176,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22075_22174,js_prop_22175,attr_name_22176))
, "enumerable": true, "configurable": true}));


var G__22177 = seq__22065_22170;
var G__22178 = chunk__22066_22171;
var G__22179 = count__22067_22172;
var G__22180 = (i__22068_22173 + (1));
seq__22065_22170 = G__22177;
chunk__22066_22171 = G__22178;
count__22067_22172 = G__22179;
i__22068_22173 = G__22180;
continue;
} else {
var temp__5823__auto___22181 = cljs.core.seq(seq__22065_22170);
if(temp__5823__auto___22181){
var seq__22065_22182__$1 = temp__5823__auto___22181;
if(cljs.core.chunked_seq_QMARK_(seq__22065_22182__$1)){
var c__5673__auto___22183 = cljs.core.chunk_first(seq__22065_22182__$1);
var G__22184 = cljs.core.chunk_rest(seq__22065_22182__$1);
var G__22185 = c__5673__auto___22183;
var G__22186 = cljs.core.count(c__5673__auto___22183);
var G__22187 = (0);
seq__22065_22170 = G__22184;
chunk__22066_22171 = G__22185;
count__22067_22172 = G__22186;
i__22068_22173 = G__22187;
continue;
} else {
var vec__22078_22188 = cljs.core.first(seq__22065_22182__$1);
var js_prop_22189 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22078_22188,(0),null);
var attr_name_22190 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22078_22188,(1),null);
Object.defineProperty(proto,js_prop_22189,({"get": ((function (seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22078_22188,js_prop_22189,attr_name_22190,seq__22065_22182__$1,temp__5823__auto___22181){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22190);
});})(seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22078_22188,js_prop_22189,attr_name_22190,seq__22065_22182__$1,temp__5823__auto___22181))
, "set": ((function (seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22078_22188,js_prop_22189,attr_name_22190,seq__22065_22182__$1,temp__5823__auto___22181){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22190);
} else {
return this$.setAttribute(attr_name_22190,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22065_22170,chunk__22066_22171,count__22067_22172,i__22068_22173,vec__22078_22188,js_prop_22189,attr_name_22190,seq__22065_22182__$1,temp__5823__auto___22181))
, "enumerable": true, "configurable": true}));


var G__22191 = cljs.core.next(seq__22065_22182__$1);
var G__22192 = null;
var G__22193 = (0);
var G__22194 = (0);
seq__22065_22170 = G__22191;
chunk__22066_22171 = G__22192;
count__22067_22172 = G__22193;
i__22068_22173 = G__22194;
continue;
}
} else {
}
}
break;
}

Object.defineProperty(proto,"close",({"value": (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

Object.defineProperty(proto,"confirm",({"value": (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_confirm_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

return Object.defineProperty(proto,"cancel",({"value": (function (reason){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(this$,(function (){var or__5142__auto__ = reason;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "cancel-button";
}
})());
}), "enumerable": true, "configurable": true, "writable": true}));
});
app.components.x_cancel_dialogue.x_cancel_dialogue.make_constructor = (function app$components$x_cancel_dialogue$x_cancel_dialogue$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.register_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_cancel_dialogue.model.tag_name))){
} else {
var proto_22203 = Object.create(HTMLElement.prototype);
var ctor_22204 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_constructor();
Object.setPrototypeOf(ctor_22204,HTMLElement);

(proto_22203["constructor"] = ctor_22204);

app.components.x_cancel_dialogue.x_cancel_dialogue.install_properties_BANG_(proto_22203);

(proto_22203["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.connected_BANG_(this$);
}));

(proto_22203["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.disconnected_BANG_(this$);
}));

(proto_22203["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor_22204["observedAttributes"] = app.components.x_cancel_dialogue.model.observed_attributes);

(ctor_22204["prototype"] = proto_22203);

customElements.define(app.components.x_cancel_dialogue.model.tag_name,ctor_22204);
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.init_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$init_BANG_(){
return app.components.x_cancel_dialogue.x_cancel_dialogue.register_BANG_();
});

//# sourceMappingURL=app.components.x_cancel_dialogue.x_cancel_dialogue.js.map
