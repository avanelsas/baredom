goog.provide('app.components.x_copy.x_copy');
goog.scope(function(){
  app.components.x_copy.x_copy.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_copy.x_copy.k_refs = "__xCopyRefs";
app.components.x_copy.x_copy.k_handlers = "__xCopyHandlers";
app.components.x_copy.x_copy.k_tid = "__xCopyTid";
app.components.x_copy.x_copy.k_text_val = "__xCopyTextValue";
app.components.x_copy.x_copy.style_text = (""+":host{"+"display:inline-block;"+"position:relative;"+"color-scheme:light dark;"+"--x-copy-tooltip-bg:#1e293b;"+"--x-copy-tooltip-fg:#f8fafc;"+"--x-copy-tooltip-success-bg:#15803d;"+"--x-copy-tooltip-success-fg:#f0fdf4;"+"--x-copy-tooltip-error-bg:#b91c1c;"+"--x-copy-tooltip-error-fg:#fef2f2;"+"--x-copy-tooltip-radius:6px;"+"--x-copy-tooltip-padding:4px 8px;"+"--x-copy-tooltip-font-size:0.8125rem;"+"--x-copy-tooltip-z:100;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-copy-tooltip-bg:#0f172a;"+"--x-copy-tooltip-fg:#e2e8f0;"+"--x-copy-tooltip-success-bg:#166534;"+"--x-copy-tooltip-success-fg:#dcfce7;"+"--x-copy-tooltip-error-bg:#991b1b;"+"--x-copy-tooltip-error-fg:#fee2e2;"+"}}"+"[part=wrap]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"}"+"[part=trigger]{"+"all:unset;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"cursor:pointer;"+"box-sizing:border-box;"+"}"+"[part=trigger]:focus-visible{"+"outline:2px solid currentColor;"+"outline-offset:2px;"+"}"+":host([disabled]) [part=trigger]{"+"opacity:0.55;"+"pointer-events:none;"+"cursor:default;"+"}"+"[part=tooltip]{"+"display:none;"+"position:absolute;"+"bottom:calc(100% + 6px);"+"left:50%;"+"transform:translateX(-50%);"+"white-space:nowrap;"+"background:var(--x-copy-tooltip-bg);"+"color:var(--x-copy-tooltip-fg);"+"border-radius:var(--x-copy-tooltip-radius);"+"padding:var(--x-copy-tooltip-padding);"+"font-size:var(--x-copy-tooltip-font-size);"+"z-index:var(--x-copy-tooltip-z);"+"pointer-events:none;"+"}"+":host([data-tooltip-open]) [part=tooltip]{display:block;}"+":host([data-tooltip-kind=success]) [part=tooltip]{"+"background:var(--x-copy-tooltip-success-bg);"+"color:var(--x-copy-tooltip-success-fg);"+"}"+":host([data-tooltip-kind=error]) [part=tooltip]{"+"background:var(--x-copy-tooltip-error-bg);"+"color:var(--x-copy-tooltip-error-fg);"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=trigger]{transition:none;}"+"}");
app.components.x_copy.x_copy.make_el = (function app$components$x_copy$x_copy$make_el(doc,tag){
return doc.createElement(tag);
});
app.components.x_copy.x_copy.init_dom_BANG_ = (function app$components$x_copy$x_copy$init_dom_BANG_(el){
var root_22505 = el.attachShadow(({"mode": "open"}));
var style_22506 = app.components.x_copy.x_copy.make_el(document,"style");
var wrap_22507 = app.components.x_copy.x_copy.make_el(document,"span");
var trigger_22508 = app.components.x_copy.x_copy.make_el(document,"button");
var default_slot_22509 = app.components.x_copy.x_copy.make_el(document,"slot");
var tooltip_22510 = app.components.x_copy.x_copy.make_el(document,"span");
var tooltip_slot_22511 = app.components.x_copy.x_copy.make_el(document,"slot");
var tooltip_text_22512 = app.components.x_copy.x_copy.make_el(document,"span");
(style_22506.textContent = app.components.x_copy.x_copy.style_text);

wrap_22507.setAttribute("part","wrap");

trigger_22508.setAttribute("part","trigger");

trigger_22508.setAttribute("type","button");

trigger_22508.appendChild(default_slot_22509);

tooltip_22510.setAttribute("part","tooltip");

tooltip_22510.setAttribute("role","status");

tooltip_22510.setAttribute("aria-live","polite");

tooltip_slot_22511.setAttribute("name","tooltip");

tooltip_text_22512.setAttribute("part","tooltip-text");

tooltip_22510.appendChild(tooltip_slot_22511);

tooltip_22510.appendChild(tooltip_text_22512);

wrap_22507.appendChild(trigger_22508);

wrap_22507.appendChild(tooltip_22510);

root_22505.appendChild(style_22506);

root_22505.appendChild(wrap_22507);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_refs,({"root": root_22505, "wrap": wrap_22507, "trigger": trigger_22508, "tooltip": tooltip_22510, "tooltipSlot": tooltip_slot_22511, "tooltipText": tooltip_text_22512}));

return null;
});
app.components.x_copy.x_copy.ensure_refs_BANG_ = (function app$components$x_copy$x_copy$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_copy.x_copy.init_dom_BANG_(el);

return app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
}
});
app.components.x_copy.x_copy.read_model = (function app$components$x_copy$x_copy$read_model(el){
return app.components.x_copy.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"from-raw","from-raw",-44462428),new cljs.core.Keyword(null,"text-raw","text-raw",-124751068),new cljs.core.Keyword(null,"mode-raw","mode-raw",-112711707),new cljs.core.Keyword(null,"error-message-raw","error-message-raw",1910536231),new cljs.core.Keyword(null,"from-attr-raw","from-attr-raw",761657416),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"success-message-raw","success-message-raw",388691116),new cljs.core.Keyword(null,"show-tooltip-raw","show-tooltip-raw",453600717),new cljs.core.Keyword(null,"hotkey-raw","hotkey-raw",1972404222),new cljs.core.Keyword(null,"tooltip-ms-raw","tooltip-ms-raw",-1973326401)],[el.getAttribute(app.components.x_copy.model.attr_from),el.getAttribute(app.components.x_copy.model.attr_text),el.getAttribute(app.components.x_copy.model.attr_mode),el.getAttribute(app.components.x_copy.model.attr_error_message),el.getAttribute(app.components.x_copy.model.attr_from_attr),el.hasAttribute(app.components.x_copy.model.attr_disabled),el.getAttribute(app.components.x_copy.model.attr_success_message),el.getAttribute(app.components.x_copy.model.attr_show_tooltip),el.getAttribute(app.components.x_copy.model.attr_hotkey),el.getAttribute(app.components.x_copy.model.attr_tooltip_ms)]));
});
app.components.x_copy.x_copy.resolve_text = (function app$components$x_copy$x_copy$resolve_text(el){
var tv = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_text_val);
if(typeof tv === 'string'){
return tv;
} else {
var text_attr = el.getAttribute(app.components.x_copy.model.attr_text);
if(((typeof text_attr === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(text_attr,"")))){
return text_attr;
} else {
var from_sel = el.getAttribute(app.components.x_copy.model.attr_from);
if(((typeof from_sel === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(from_sel,"")))){
var target = document.querySelector(from_sel);
if(cljs.core.truth_(target)){
var from_attr = el.getAttribute(app.components.x_copy.model.attr_from_attr);
if(((typeof from_attr === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(from_attr,"")))){
var or__5142__auto__ = target.getAttribute(from_attr);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
} else {
var or__5142__auto__ = target.textContent;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}
} else {
return "";
}
} else {
return "";
}
}
}
});
app.components.x_copy.x_copy.do_copy_BANG_ = (function app$components$x_copy$x_copy$do_copy_BANG_(el){
var text = app.components.x_copy.x_copy.resolve_text(el);
var mode = (function (){var or__5142__auto__ = el.getAttribute(app.components.x_copy.model.attr_mode);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "text";
}
})();
return (new Promise((function (resolve,reject){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mode,"html")){
if(cljs.core.truth_((function (){var and__5140__auto__ = navigator.clipboard;
if(cljs.core.truth_(and__5140__auto__)){
return window.ClipboardItem;
} else {
return and__5140__auto__;
}
})())){
return navigator.clipboard.write(cljs.core.clj__GT_js(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new ClipboardItem(({"text/html": (new Blob([text],({"type": "text/html"}))), "text/plain": (new Blob([text],({"type": "text/plain"})))})))], null))).then((function (){
return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
})).catch((function (e){
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
}));
} else {
try{var ta = app.components.x_copy.x_copy.make_el(document,"textarea");
(ta.value = text);

document.body.appendChild(ta);

ta.select();

document.execCommand("copy");

ta.remove();

return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
}catch (e22463){if((e22463 instanceof Error)){
var e = e22463;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22463;

}
}}
} else {
if(cljs.core.truth_(navigator.clipboard)){
return navigator.clipboard.writeText(text).then((function (){
return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
})).catch((function (e){
try{document.execCommand("copy");

return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
}catch (e22464){if((e22464 instanceof Error)){
var _ = e22464;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22464;

}
}}));
} else {
try{var ta = app.components.x_copy.x_copy.make_el(document,"textarea");
(ta.value = text);

(ta.style.position = "fixed");

(ta.style.opacity = "0");

document.body.appendChild(ta);

ta.select();

document.execCommand("copy");

ta.remove();

return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
}catch (e22465){if((e22465 instanceof Error)){
var e = e22465;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22465;

}
}}

}
})));
});
app.components.x_copy.x_copy.hide_tooltip_BANG_ = (function app$components$x_copy$x_copy$hide_tooltip_BANG_(el){
el.removeAttribute("data-tooltip-open");

el.removeAttribute("data-tooltip-kind");

var refs_22516 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_(refs_22516)){
(app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22516,"tooltipText").textContent = "");
} else {
}

return null;
});
app.components.x_copy.x_copy.show_tooltip_BANG_ = (function app$components$x_copy$x_copy$show_tooltip_BANG_(el,kind,message,tooltip_ms){
var refs_22517 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_(refs_22517)){
var temp__5823__auto___22518 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_tid);
if(cljs.core.truth_(temp__5823__auto___22518)){
var tid_22519 = temp__5823__auto___22518;
clearTimeout(tid_22519);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,null);
} else {
}

el.setAttribute("data-tooltip-open","");

el.setAttribute("data-tooltip-kind",kind);

(app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22517,"tooltipText").textContent = message);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,setTimeout((function (){
app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,null);

return app.components.x_copy.x_copy.hide_tooltip_BANG_(el);
}),tooltip_ms));
} else {
}

return null;
});
app.components.x_copy.x_copy.dispatch_event_BANG_ = (function app$components$x_copy$x_copy$dispatch_event_BANG_(el,event_name,detail,cancelable_QMARK_){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cancelable_QMARK_})));
el.dispatchEvent(ev);

return ev;
});
app.components.x_copy.x_copy.copy_BANG_ = (function app$components$x_copy$x_copy$copy_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_copy.model.attr_disabled))){
} else {
var m_22520 = app.components.x_copy.x_copy.read_model(el);
var req_detail_22521 = app.components.x_copy.model.request_detail(m_22520);
var req_ev_22522 = app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_request,req_detail_22521,true);
if(cljs.core.truth_(req_ev_22522.defaultPrevented)){
} else {
app.components.x_copy.x_copy.do_copy_BANG_(el).then((function (text){
app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_success,app.components.x_copy.model.success_detail(text),false);

if(cljs.core.truth_(new cljs.core.Keyword(null,"show-tooltip?","show-tooltip?",-1214081087).cljs$core$IFn$_invoke$arity$1(m_22520))){
return app.components.x_copy.x_copy.show_tooltip_BANG_(el,"success",new cljs.core.Keyword(null,"success-message","success-message",-96541601).cljs$core$IFn$_invoke$arity$1(m_22520),new cljs.core.Keyword(null,"tooltip-ms","tooltip-ms",1062450417).cljs$core$IFn$_invoke$arity$1(m_22520));
} else {
return null;
}
})).catch((function (err){
app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_error,app.components.x_copy.model.error_detail(err),false);

if(cljs.core.truth_(new cljs.core.Keyword(null,"show-tooltip?","show-tooltip?",-1214081087).cljs$core$IFn$_invoke$arity$1(m_22520))){
return app.components.x_copy.x_copy.show_tooltip_BANG_(el,"error",new cljs.core.Keyword(null,"error-message","error-message",1756021561).cljs$core$IFn$_invoke$arity$1(m_22520),new cljs.core.Keyword(null,"tooltip-ms","tooltip-ms",1062450417).cljs$core$IFn$_invoke$arity$1(m_22520));
} else {
return null;
}
}));
}
}

return null;
});
app.components.x_copy.x_copy.matches_hotkey_QMARK_ = (function app$components$x_copy$x_copy$matches_hotkey_QMARK_(e,hotkey){
if(((typeof hotkey === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(hotkey,"")))){
var parts = hotkey.toLowerCase().split("+");
var key_part = cljs.core.last(parts);
var need_ctrl = cljs.core.boolean$(cljs.core.some((function (p1__22469_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22469_SHARP_,"ctrl");
}),parts));
var need_shift = cljs.core.boolean$(cljs.core.some((function (p1__22470_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22470_SHARP_,"shift");
}),parts));
var need_alt = cljs.core.boolean$(cljs.core.some((function (p1__22471_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22471_SHARP_,"alt");
}),parts));
var need_meta = cljs.core.boolean$(cljs.core.some((function (p1__22472_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22472_SHARP_,"meta");
}),parts));
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.key.toLowerCase(),key_part)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_ctrl,e.ctrlKey)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_shift,e.shiftKey)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_alt,e.altKey)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_meta,e.metaKey)))))))));
} else {
return null;
}
});
app.components.x_copy.x_copy.add_listeners_BANG_ = (function app$components$x_copy$x_copy$add_listeners_BANG_(el){
var refs_22528 = app.components.x_copy.x_copy.ensure_refs_BANG_(el);
var trigger_22529 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22528,"trigger");
var click_h_22530 = (function (_){
return app.components.x_copy.x_copy.copy_BANG_(el);
});
var key_h_22531 = (function (e){
var hotkey = el.getAttribute(app.components.x_copy.model.attr_hotkey);
if(cljs.core.truth_(app.components.x_copy.x_copy.matches_hotkey_QMARK_(e,hotkey))){
e.preventDefault();

return app.components.x_copy.x_copy.copy_BANG_(el);
} else {
return null;
}
});
trigger_22529.addEventListener("click",click_h_22530);

document.addEventListener("keydown",key_h_22531);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_handlers,({"click": click_h_22530, "keydown": key_h_22531}));

return null;
});
app.components.x_copy.x_copy.remove_listeners_BANG_ = (function app$components$x_copy$x_copy$remove_listeners_BANG_(el){
var temp__5823__auto___22533 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_tid);
if(cljs.core.truth_(temp__5823__auto___22533)){
var tid_22534 = temp__5823__auto___22533;
clearTimeout(tid_22534);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,null);
} else {
}

var hs_22535 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_handlers);
var refs_22536 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22535;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22536;
} else {
return and__5140__auto__;
}
})())){
var trigger_22537 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22536,"trigger");
var click_h_22538 = app.components.x_copy.x_copy.goog$module$goog$object.get(hs_22535,"click");
var key_h_22539 = app.components.x_copy.x_copy.goog$module$goog$object.get(hs_22535,"keydown");
if(cljs.core.truth_(click_h_22538)){
trigger_22537.removeEventListener("click",click_h_22538);
} else {
}

if(cljs.core.truth_(key_h_22539)){
document.removeEventListener("keydown",key_h_22539);
} else {
}
} else {
}

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_handlers,null);

return null;
});
app.components.x_copy.x_copy.render_BANG_ = (function app$components$x_copy$x_copy$render_BANG_(el){
var refs_22540 = app.components.x_copy.x_copy.ensure_refs_BANG_(el);
var trigger_22541 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22540,"trigger");
var disabled_QMARK__22542 = el.hasAttribute(app.components.x_copy.model.attr_disabled);
(trigger_22541.disabled = cljs.core.boolean$(disabled_QMARK__22542));

if(cljs.core.truth_(disabled_QMARK__22542)){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

return null;
});
app.components.x_copy.x_copy.connected_BANG_ = (function app$components$x_copy$x_copy$connected_BANG_(el){
app.components.x_copy.x_copy.ensure_refs_BANG_(el);

app.components.x_copy.x_copy.remove_listeners_BANG_(el);

app.components.x_copy.x_copy.add_listeners_BANG_(el);

app.components.x_copy.x_copy.render_BANG_(el);

return null;
});
app.components.x_copy.x_copy.disconnected_BANG_ = (function app$components$x_copy$x_copy$disconnected_BANG_(el){
app.components.x_copy.x_copy.remove_listeners_BANG_(el);

return null;
});
app.components.x_copy.x_copy.attribute_changed_BANG_ = (function app$components$x_copy$x_copy$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_copy.x_copy.render_BANG_(el);
} else {
}

return null;
});
app.components.x_copy.x_copy.install_properties_BANG_ = (function app$components$x_copy$x_copy$install_properties_BANG_(proto){
var seq__22485_22544 = cljs.core.seq(new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["text",app.components.x_copy.model.attr_text], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["from",app.components.x_copy.model.attr_from], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["fromAttr",app.components.x_copy.model.attr_from_attr], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["mode",app.components.x_copy.model.attr_mode], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["successMessage",app.components.x_copy.model.attr_success_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["errorMessage",app.components.x_copy.model.attr_error_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["hotkey",app.components.x_copy.model.attr_hotkey], null)], null));
var chunk__22486_22545 = null;
var count__22487_22546 = (0);
var i__22488_22547 = (0);
while(true){
if((i__22488_22547 < count__22487_22546)){
var vec__22495_22553 = chunk__22486_22545.cljs$core$IIndexed$_nth$arity$2(null,i__22488_22547);
var js_prop_22554 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22495_22553,(0),null);
var attr_name_22555 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22495_22553,(1),null);
Object.defineProperty(proto,js_prop_22554,({"get": ((function (seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22495_22553,js_prop_22554,attr_name_22555){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22555);
});})(seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22495_22553,js_prop_22554,attr_name_22555))
, "set": ((function (seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22495_22553,js_prop_22554,attr_name_22555){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22555);
} else {
return this$.setAttribute(attr_name_22555,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22495_22553,js_prop_22554,attr_name_22555))
, "enumerable": true, "configurable": true}));


var G__22556 = seq__22485_22544;
var G__22557 = chunk__22486_22545;
var G__22558 = count__22487_22546;
var G__22559 = (i__22488_22547 + (1));
seq__22485_22544 = G__22556;
chunk__22486_22545 = G__22557;
count__22487_22546 = G__22558;
i__22488_22547 = G__22559;
continue;
} else {
var temp__5823__auto___22560 = cljs.core.seq(seq__22485_22544);
if(temp__5823__auto___22560){
var seq__22485_22561__$1 = temp__5823__auto___22560;
if(cljs.core.chunked_seq_QMARK_(seq__22485_22561__$1)){
var c__5673__auto___22562 = cljs.core.chunk_first(seq__22485_22561__$1);
var G__22563 = cljs.core.chunk_rest(seq__22485_22561__$1);
var G__22564 = c__5673__auto___22562;
var G__22565 = cljs.core.count(c__5673__auto___22562);
var G__22566 = (0);
seq__22485_22544 = G__22563;
chunk__22486_22545 = G__22564;
count__22487_22546 = G__22565;
i__22488_22547 = G__22566;
continue;
} else {
var vec__22498_22567 = cljs.core.first(seq__22485_22561__$1);
var js_prop_22568 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22498_22567,(0),null);
var attr_name_22569 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22498_22567,(1),null);
Object.defineProperty(proto,js_prop_22568,({"get": ((function (seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22498_22567,js_prop_22568,attr_name_22569,seq__22485_22561__$1,temp__5823__auto___22560){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22569);
});})(seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22498_22567,js_prop_22568,attr_name_22569,seq__22485_22561__$1,temp__5823__auto___22560))
, "set": ((function (seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22498_22567,js_prop_22568,attr_name_22569,seq__22485_22561__$1,temp__5823__auto___22560){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22569);
} else {
return this$.setAttribute(attr_name_22569,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22485_22544,chunk__22486_22545,count__22487_22546,i__22488_22547,vec__22498_22567,js_prop_22568,attr_name_22569,seq__22485_22561__$1,temp__5823__auto___22560))
, "enumerable": true, "configurable": true}));


var G__22570 = cljs.core.next(seq__22485_22561__$1);
var G__22571 = null;
var G__22572 = (0);
var G__22573 = (0);
seq__22485_22544 = G__22570;
chunk__22486_22545 = G__22571;
count__22487_22546 = G__22572;
i__22488_22547 = G__22573;
continue;
}
} else {
}
}
break;
}

Object.defineProperty(proto,"disabled",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_copy.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_copy.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_copy.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"showTooltip",({"get": (function (){
var this$ = this;
return app.components.x_copy.model.parse_bool_default_true(this$.getAttribute(app.components.x_copy.model.attr_show_tooltip));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_copy.model.attr_show_tooltip,"true");
} else {
return this$.setAttribute(app.components.x_copy.model.attr_show_tooltip,"false");
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"tooltipMs",({"get": (function (){
var this$ = this;
return app.components.x_copy.model.parse_int_pos(this$.getAttribute(app.components.x_copy.model.attr_tooltip_ms),(1200));
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_copy.model.attr_tooltip_ms);
} else {
return this$.setAttribute(app.components.x_copy.model.attr_tooltip_ms,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v | 0))));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"textValue",({"get": (function (){
var this$ = this;
return app.components.x_copy.x_copy.goog$module$goog$object.get(this$,app.components.x_copy.x_copy.k_text_val);
}), "set": (function (v){
var this$ = this;
if(typeof v === 'string'){
return app.components.x_copy.x_copy.goog$module$goog$object.set(this$,app.components.x_copy.x_copy.k_text_val,v);
} else {
return app.components.x_copy.x_copy.goog$module$goog$object.set(this$,app.components.x_copy.x_copy.k_text_val,undefined);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"copy",({"value": (function (){
var this$ = this;
return app.components.x_copy.x_copy.do_copy_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));
});
app.components.x_copy.x_copy.make_constructor = (function app$components$x_copy$x_copy$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_copy.x_copy.register_BANG_ = (function app$components$x_copy$x_copy$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_copy.model.tag_name))){
} else {
var proto_22622 = Object.create(HTMLElement.prototype);
var ctor_22623 = app.components.x_copy.x_copy.make_constructor();
Object.setPrototypeOf(ctor_22623,HTMLElement);

(proto_22622["constructor"] = ctor_22623);

app.components.x_copy.x_copy.install_properties_BANG_(proto_22622);

(proto_22622["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_copy.x_copy.connected_BANG_(this$);
}));

(proto_22622["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_copy.x_copy.disconnected_BANG_(this$);
}));

(proto_22622["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_copy.x_copy.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor_22623["observedAttributes"] = app.components.x_copy.model.observed_attributes);

(ctor_22623["prototype"] = proto_22622);

customElements.define(app.components.x_copy.model.tag_name,ctor_22623);
}

return null;
});
app.components.x_copy.x_copy.init_BANG_ = (function app$components$x_copy$x_copy$init_BANG_(){
return app.components.x_copy.x_copy.register_BANG_();
});

//# sourceMappingURL=app.components.x_copy.x_copy.js.map
