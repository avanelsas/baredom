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
var headline_id_22146 = app.components.x_cancel_dialogue.x_cancel_dialogue.next_id_BANG_();
var root_22147 = el.attachShadow(({"mode": "open"}));
var style_22148 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("style");
var backdrop_22149 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var dialog_22150 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var panel_22151 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var header_22152 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var headline_22153 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("h2");
var body_22154 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var body_slot_22155 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("slot");
var message_el_22156 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("p");
var actions_22157 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("div");
var cancel_btn_22158 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("button");
var confirm_btn_22159 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_el("button");
(style_22148.textContent = app.components.x_cancel_dialogue.x_cancel_dialogue.style_text);

backdrop_22149.setAttribute("part","backdrop");

dialog_22150.setAttribute("part","dialog");

dialog_22150.setAttribute("role","dialog");

dialog_22150.setAttribute("aria-modal","true");

dialog_22150.setAttribute("aria-labelledby",headline_id_22146);

panel_22151.setAttribute("part","panel");

header_22152.setAttribute("part","header");

headline_22153.setAttribute("part","headline");

headline_22153.setAttribute("id",headline_id_22146);

body_22154.setAttribute("part","body");

body_slot_22155.setAttribute("name","body");

message_el_22156.setAttribute("part","message");

actions_22157.setAttribute("part","actions");

cancel_btn_22158.setAttribute("part","cancel-btn");

cancel_btn_22158.setAttribute("type","button");

confirm_btn_22159.setAttribute("part","confirm-btn");

confirm_btn_22159.setAttribute("type","button");

header_22152.appendChild(headline_22153);

body_22154.appendChild(body_slot_22155);

body_22154.appendChild(message_el_22156);

actions_22157.appendChild(cancel_btn_22158);

actions_22157.appendChild(confirm_btn_22159);

panel_22151.appendChild(header_22152);

panel_22151.appendChild(body_22154);

panel_22151.appendChild(actions_22157);

dialog_22150.appendChild(panel_22151);

root_22147.appendChild(style_22148);

root_22147.appendChild(backdrop_22149);

root_22147.appendChild(dialog_22150);

app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs,({"headline": headline_22153, "confirmBtn": confirm_btn_22159, "panel": panel_22151, "bodySlot": body_slot_22155, "root": root_22147, "dialog": dialog_22150, "backdrop": backdrop_22149, "cancelBtn": cancel_btn_22158, "message": message_el_22156}));

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

var refs_22160 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var confirm_22161 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22160,"confirmBtn");
setTimeout((function (){
return confirm_22161.focus();
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
var req_ev_22162 = app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_cancel_request,app.components.x_cancel_dialogue.model.cancel_request_detail(reason),true);
if(cljs.core.truth_(req_ev_22162.defaultPrevented)){
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
var req_ev_22164 = app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_confirm_request,app.components.x_cancel_dialogue.model.confirm_request_detail(),true);
if(cljs.core.truth_(req_ev_22164.defaultPrevented)){
} else {
app.components.x_cancel_dialogue.x_cancel_dialogue.do_close_BANG_(el);

app.components.x_cancel_dialogue.x_cancel_dialogue.dispatch_BANG_(el,app.components.x_cancel_dialogue.model.event_confirm,({}),false);
}
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.on_keydown = (function app$components$x_cancel_dialogue$x_cancel_dialogue$on_keydown(el,e){
var key_22165 = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22165,"Escape")){
e.preventDefault();

app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"escape");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22165,"Tab")){
var refs_22193 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var cancel_22194 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22193,"cancelBtn");
var confirm_22195 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22193,"confirmBtn");
var shift_QMARK__22196 = e.shiftKey;
var active_22197 = el.shadowRoot.activeElement;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK__22196;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22197,cancel_22194);
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

confirm_22195.focus();
} else {
if(((cljs.core.not(shift_QMARK__22196)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22197,confirm_22195)))){
e.preventDefault();

cancel_22194.focus();
} else {
}
}
} else {
}
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.render_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$render_BANG_(el){
var refs_22198 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var headline_22199 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22198,"headline");
var message_22200 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22198,"message");
var cancel_22201 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22198,"cancelBtn");
var confirm_22202 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22198,"confirmBtn");
var m_22203 = app.components.x_cancel_dialogue.x_cancel_dialogue.read_model(el);
(headline_22199.textContent = new cljs.core.Keyword(null,"headline","headline",-157157727).cljs$core$IFn$_invoke$arity$1(m_22203));

(cancel_22201.textContent = new cljs.core.Keyword(null,"cancel-text","cancel-text",1885137831).cljs$core$IFn$_invoke$arity$1(m_22203));

(confirm_22202.textContent = new cljs.core.Keyword(null,"confirm-text","confirm-text",-1839494031).cljs$core$IFn$_invoke$arity$1(m_22203));

if(cljs.core.truth_(new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(m_22203))){
(message_22200.textContent = new cljs.core.Keyword(null,"message","message",-406056002).cljs$core$IFn$_invoke$arity$1(m_22203));

(message_22200.style.display = "block");
} else {
(message_22200.textContent = "");

(message_22200.style.display = "none");
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"danger?","danger?",181682216).cljs$core$IFn$_invoke$arity$1(m_22203))){
confirm_22202.setAttribute("data-danger","");
} else {
confirm_22202.removeAttribute("data-danger");
}

(cancel_22201.disabled = cljs.core.boolean$(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m_22203)));

(confirm_22202.disabled = cljs.core.boolean$(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m_22203)));

var dialog_22204 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22198,"dialog");
dialog_22204.setAttribute("aria-modal","true");

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.add_listeners_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$add_listeners_BANG_(el){
var refs_22206 = app.components.x_cancel_dialogue.x_cancel_dialogue.ensure_refs_BANG_(el);
var backdrop_22207 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22206,"backdrop");
var cancel_22208 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22206,"cancelBtn");
var confirm_22209 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22206,"confirmBtn");
var dialog_22210 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22206,"dialog");
var backdrop_h_22211 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"backdrop");
});
var cancel_h_22212 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_cancel_BANG_(el,"cancel-button");
});
var confirm_h_22213 = (function (_){
return app.components.x_cancel_dialogue.x_cancel_dialogue.do_confirm_BANG_(el);
});
var keydown_h_22214 = (function (e){
return app.components.x_cancel_dialogue.x_cancel_dialogue.on_keydown(el,e);
});
backdrop_22207.addEventListener("click",backdrop_h_22211);

cancel_22208.addEventListener("click",cancel_h_22212);

confirm_22209.addEventListener("click",confirm_h_22213);

dialog_22210.addEventListener("keydown",keydown_h_22214);

app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.set(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers,({"backdrop": backdrop_h_22211, "cancel": cancel_h_22212, "confirm": confirm_h_22213, "keydown": keydown_h_22214}));

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.remove_listeners_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$remove_listeners_BANG_(el){
var hs_22215 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_handlers);
var refs_22216 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22215;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22216;
} else {
return and__5140__auto__;
}
})())){
var backdrop_22217 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22216,"backdrop");
var cancel_22218 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22216,"cancelBtn");
var confirm_22219 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22216,"confirmBtn");
var dialog_22220 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22216,"dialog");
var backdrop_h_22221 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22215,"backdrop");
var cancel_h_22222 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22215,"cancel");
var confirm_h_22223 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22215,"confirm");
var keydown_h_22224 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(hs_22215,"keydown");
if(cljs.core.truth_(backdrop_h_22221)){
backdrop_22217.removeEventListener("click",backdrop_h_22221);
} else {
}

if(cljs.core.truth_(cancel_h_22222)){
cancel_22218.removeEventListener("click",cancel_h_22222);
} else {
}

if(cljs.core.truth_(confirm_h_22223)){
confirm_22219.removeEventListener("click",confirm_h_22223);
} else {
}

if(cljs.core.truth_(keydown_h_22224)){
dialog_22220.removeEventListener("keydown",keydown_h_22224);
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
var refs_22227 = app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(el,app.components.x_cancel_dialogue.x_cancel_dialogue.k_refs);
var confirm_22228 = (cljs.core.truth_(refs_22227)?app.components.x_cancel_dialogue.x_cancel_dialogue.goog$module$goog$object.get(refs_22227,"confirmBtn"):null);
if(cljs.core.truth_(confirm_22228)){
setTimeout((function (){
return confirm_22228.focus();
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

var seq__22014_22229 = cljs.core.seq(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["disabled",app.components.x_cancel_dialogue.model.attr_disabled], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["danger",app.components.x_cancel_dialogue.model.attr_danger], null)], null));
var chunk__22015_22230 = null;
var count__22016_22231 = (0);
var i__22017_22232 = (0);
while(true){
if((i__22017_22232 < count__22016_22231)){
var vec__22071_22233 = chunk__22015_22230.cljs$core$IIndexed$_nth$arity$2(null,i__22017_22232);
var js_prop_22234 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22071_22233,(0),null);
var attr_name_22235 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22071_22233,(1),null);
Object.defineProperty(proto,js_prop_22234,({"get": ((function (seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22071_22233,js_prop_22234,attr_name_22235){
return (function (){
var this$ = this;
return this$.hasAttribute(attr_name_22235);
});})(seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22071_22233,js_prop_22234,attr_name_22235))
, "set": ((function (seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22071_22233,js_prop_22234,attr_name_22235){
return (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name_22235,"");
} else {
return this$.removeAttribute(attr_name_22235);
}
});})(seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22071_22233,js_prop_22234,attr_name_22235))
, "enumerable": true, "configurable": true}));


var G__22236 = seq__22014_22229;
var G__22237 = chunk__22015_22230;
var G__22238 = count__22016_22231;
var G__22239 = (i__22017_22232 + (1));
seq__22014_22229 = G__22236;
chunk__22015_22230 = G__22237;
count__22016_22231 = G__22238;
i__22017_22232 = G__22239;
continue;
} else {
var temp__5823__auto___22240 = cljs.core.seq(seq__22014_22229);
if(temp__5823__auto___22240){
var seq__22014_22241__$1 = temp__5823__auto___22240;
if(cljs.core.chunked_seq_QMARK_(seq__22014_22241__$1)){
var c__5673__auto___22242 = cljs.core.chunk_first(seq__22014_22241__$1);
var G__22243 = cljs.core.chunk_rest(seq__22014_22241__$1);
var G__22244 = c__5673__auto___22242;
var G__22245 = cljs.core.count(c__5673__auto___22242);
var G__22246 = (0);
seq__22014_22229 = G__22243;
chunk__22015_22230 = G__22244;
count__22016_22231 = G__22245;
i__22017_22232 = G__22246;
continue;
} else {
var vec__22074_22247 = cljs.core.first(seq__22014_22241__$1);
var js_prop_22248 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22074_22247,(0),null);
var attr_name_22249 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22074_22247,(1),null);
Object.defineProperty(proto,js_prop_22248,({"get": ((function (seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22074_22247,js_prop_22248,attr_name_22249,seq__22014_22241__$1,temp__5823__auto___22240){
return (function (){
var this$ = this;
return this$.hasAttribute(attr_name_22249);
});})(seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22074_22247,js_prop_22248,attr_name_22249,seq__22014_22241__$1,temp__5823__auto___22240))
, "set": ((function (seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22074_22247,js_prop_22248,attr_name_22249,seq__22014_22241__$1,temp__5823__auto___22240){
return (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name_22249,"");
} else {
return this$.removeAttribute(attr_name_22249);
}
});})(seq__22014_22229,chunk__22015_22230,count__22016_22231,i__22017_22232,vec__22074_22247,js_prop_22248,attr_name_22249,seq__22014_22241__$1,temp__5823__auto___22240))
, "enumerable": true, "configurable": true}));


var G__22251 = cljs.core.next(seq__22014_22241__$1);
var G__22252 = null;
var G__22253 = (0);
var G__22254 = (0);
seq__22014_22229 = G__22251;
chunk__22015_22230 = G__22252;
count__22016_22231 = G__22253;
i__22017_22232 = G__22254;
continue;
}
} else {
}
}
break;
}

var seq__22077_22255 = cljs.core.seq(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["headline",app.components.x_cancel_dialogue.model.attr_headline], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["message",app.components.x_cancel_dialogue.model.attr_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["confirmText",app.components.x_cancel_dialogue.model.attr_confirm_text], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["cancelText",app.components.x_cancel_dialogue.model.attr_cancel_text], null)], null));
var chunk__22078_22256 = null;
var count__22079_22257 = (0);
var i__22080_22258 = (0);
while(true){
if((i__22080_22258 < count__22079_22257)){
var vec__22087_22259 = chunk__22078_22256.cljs$core$IIndexed$_nth$arity$2(null,i__22080_22258);
var js_prop_22260 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22087_22259,(0),null);
var attr_name_22261 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22087_22259,(1),null);
Object.defineProperty(proto,js_prop_22260,({"get": ((function (seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22087_22259,js_prop_22260,attr_name_22261){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22261);
});})(seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22087_22259,js_prop_22260,attr_name_22261))
, "set": ((function (seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22087_22259,js_prop_22260,attr_name_22261){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22261);
} else {
return this$.setAttribute(attr_name_22261,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22087_22259,js_prop_22260,attr_name_22261))
, "enumerable": true, "configurable": true}));


var G__22262 = seq__22077_22255;
var G__22263 = chunk__22078_22256;
var G__22264 = count__22079_22257;
var G__22265 = (i__22080_22258 + (1));
seq__22077_22255 = G__22262;
chunk__22078_22256 = G__22263;
count__22079_22257 = G__22264;
i__22080_22258 = G__22265;
continue;
} else {
var temp__5823__auto___22266 = cljs.core.seq(seq__22077_22255);
if(temp__5823__auto___22266){
var seq__22077_22267__$1 = temp__5823__auto___22266;
if(cljs.core.chunked_seq_QMARK_(seq__22077_22267__$1)){
var c__5673__auto___22268 = cljs.core.chunk_first(seq__22077_22267__$1);
var G__22269 = cljs.core.chunk_rest(seq__22077_22267__$1);
var G__22271 = c__5673__auto___22268;
var G__22272 = cljs.core.count(c__5673__auto___22268);
var G__22273 = (0);
seq__22077_22255 = G__22269;
chunk__22078_22256 = G__22271;
count__22079_22257 = G__22272;
i__22080_22258 = G__22273;
continue;
} else {
var vec__22092_22274 = cljs.core.first(seq__22077_22267__$1);
var js_prop_22275 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22092_22274,(0),null);
var attr_name_22276 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22092_22274,(1),null);
Object.defineProperty(proto,js_prop_22275,({"get": ((function (seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22092_22274,js_prop_22275,attr_name_22276,seq__22077_22267__$1,temp__5823__auto___22266){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22276);
});})(seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22092_22274,js_prop_22275,attr_name_22276,seq__22077_22267__$1,temp__5823__auto___22266))
, "set": ((function (seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22092_22274,js_prop_22275,attr_name_22276,seq__22077_22267__$1,temp__5823__auto___22266){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22276);
} else {
return this$.setAttribute(attr_name_22276,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22077_22255,chunk__22078_22256,count__22079_22257,i__22080_22258,vec__22092_22274,js_prop_22275,attr_name_22276,seq__22077_22267__$1,temp__5823__auto___22266))
, "enumerable": true, "configurable": true}));


var G__22278 = cljs.core.next(seq__22077_22267__$1);
var G__22279 = null;
var G__22280 = (0);
var G__22281 = (0);
seq__22077_22255 = G__22278;
chunk__22078_22256 = G__22279;
count__22079_22257 = G__22280;
i__22080_22258 = G__22281;
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
var proto_22282 = Object.create(HTMLElement.prototype);
var ctor_22283 = app.components.x_cancel_dialogue.x_cancel_dialogue.make_constructor();
Object.setPrototypeOf(ctor_22283,HTMLElement);

(proto_22282["constructor"] = ctor_22283);

app.components.x_cancel_dialogue.x_cancel_dialogue.install_properties_BANG_(proto_22282);

(proto_22282["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.connected_BANG_(this$);
}));

(proto_22282["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.disconnected_BANG_(this$);
}));

(proto_22282["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_cancel_dialogue.x_cancel_dialogue.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor_22283["observedAttributes"] = app.components.x_cancel_dialogue.model.observed_attributes);

(ctor_22283["prototype"] = proto_22282);

customElements.define(app.components.x_cancel_dialogue.model.tag_name,ctor_22283);
}

return null;
});
app.components.x_cancel_dialogue.x_cancel_dialogue.init_BANG_ = (function app$components$x_cancel_dialogue$x_cancel_dialogue$init_BANG_(){
return app.components.x_cancel_dialogue.x_cancel_dialogue.register_BANG_();
});

//# sourceMappingURL=app.components.x_cancel_dialogue.x_cancel_dialogue.js.map
