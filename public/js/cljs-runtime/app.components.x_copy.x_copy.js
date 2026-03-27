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
var root_22495 = el.attachShadow(({"mode": "open"}));
var style_22496 = app.components.x_copy.x_copy.make_el(document,"style");
var wrap_22497 = app.components.x_copy.x_copy.make_el(document,"span");
var trigger_22498 = app.components.x_copy.x_copy.make_el(document,"button");
var default_slot_22499 = app.components.x_copy.x_copy.make_el(document,"slot");
var tooltip_22500 = app.components.x_copy.x_copy.make_el(document,"span");
var tooltip_slot_22501 = app.components.x_copy.x_copy.make_el(document,"slot");
var tooltip_text_22502 = app.components.x_copy.x_copy.make_el(document,"span");
(style_22496.textContent = app.components.x_copy.x_copy.style_text);

wrap_22497.setAttribute("part","wrap");

trigger_22498.setAttribute("part","trigger");

trigger_22498.setAttribute("type","button");

trigger_22498.appendChild(default_slot_22499);

tooltip_22500.setAttribute("part","tooltip");

tooltip_22500.setAttribute("role","status");

tooltip_22500.setAttribute("aria-live","polite");

tooltip_slot_22501.setAttribute("name","tooltip");

tooltip_text_22502.setAttribute("part","tooltip-text");

tooltip_22500.appendChild(tooltip_slot_22501);

tooltip_22500.appendChild(tooltip_text_22502);

wrap_22497.appendChild(trigger_22498);

wrap_22497.appendChild(tooltip_22500);

root_22495.appendChild(style_22496);

root_22495.appendChild(wrap_22497);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_refs,({"root": root_22495, "wrap": wrap_22497, "trigger": trigger_22498, "tooltip": tooltip_22500, "tooltipSlot": tooltip_slot_22501, "tooltipText": tooltip_text_22502}));

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
}catch (e22438){if((e22438 instanceof Error)){
var e = e22438;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22438;

}
}}
} else {
if(cljs.core.truth_(navigator.clipboard)){
return navigator.clipboard.writeText(text).then((function (){
return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
})).catch((function (e){
try{document.execCommand("copy");

return (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(text) : resolve.call(null,text));
}catch (e22439){if((e22439 instanceof Error)){
var _ = e22439;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22439;

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
}catch (e22440){if((e22440 instanceof Error)){
var e = e22440;
return (reject.cljs$core$IFn$_invoke$arity$1 ? reject.cljs$core$IFn$_invoke$arity$1(e) : reject.call(null,e));
} else {
throw e22440;

}
}}

}
})));
});
app.components.x_copy.x_copy.hide_tooltip_BANG_ = (function app$components$x_copy$x_copy$hide_tooltip_BANG_(el){
el.removeAttribute("data-tooltip-open");

el.removeAttribute("data-tooltip-kind");

var refs_22511 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_(refs_22511)){
(app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22511,"tooltipText").textContent = "");
} else {
}

return null;
});
app.components.x_copy.x_copy.show_tooltip_BANG_ = (function app$components$x_copy$x_copy$show_tooltip_BANG_(el,kind,message,tooltip_ms){
var refs_22512 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_(refs_22512)){
var temp__5823__auto___22513 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_tid);
if(cljs.core.truth_(temp__5823__auto___22513)){
var tid_22514 = temp__5823__auto___22513;
clearTimeout(tid_22514);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,null);
} else {
}

el.setAttribute("data-tooltip-open","");

el.setAttribute("data-tooltip-kind",kind);

(app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22512,"tooltipText").textContent = message);

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
var m_22515 = app.components.x_copy.x_copy.read_model(el);
var req_detail_22516 = app.components.x_copy.model.request_detail(m_22515);
var req_ev_22517 = app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_request,req_detail_22516,true);
if(cljs.core.truth_(req_ev_22517.defaultPrevented)){
} else {
app.components.x_copy.x_copy.do_copy_BANG_(el).then((function (text){
app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_success,app.components.x_copy.model.success_detail(text),false);

if(cljs.core.truth_(new cljs.core.Keyword(null,"show-tooltip?","show-tooltip?",-1214081087).cljs$core$IFn$_invoke$arity$1(m_22515))){
return app.components.x_copy.x_copy.show_tooltip_BANG_(el,"success",new cljs.core.Keyword(null,"success-message","success-message",-96541601).cljs$core$IFn$_invoke$arity$1(m_22515),new cljs.core.Keyword(null,"tooltip-ms","tooltip-ms",1062450417).cljs$core$IFn$_invoke$arity$1(m_22515));
} else {
return null;
}
})).catch((function (err){
app.components.x_copy.x_copy.dispatch_event_BANG_(el,app.components.x_copy.model.event_copy_error,app.components.x_copy.model.error_detail(err),false);

if(cljs.core.truth_(new cljs.core.Keyword(null,"show-tooltip?","show-tooltip?",-1214081087).cljs$core$IFn$_invoke$arity$1(m_22515))){
return app.components.x_copy.x_copy.show_tooltip_BANG_(el,"error",new cljs.core.Keyword(null,"error-message","error-message",1756021561).cljs$core$IFn$_invoke$arity$1(m_22515),new cljs.core.Keyword(null,"tooltip-ms","tooltip-ms",1062450417).cljs$core$IFn$_invoke$arity$1(m_22515));
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
var need_ctrl = cljs.core.boolean$(cljs.core.some((function (p1__22441_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22441_SHARP_,"ctrl");
}),parts));
var need_shift = cljs.core.boolean$(cljs.core.some((function (p1__22442_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22442_SHARP_,"shift");
}),parts));
var need_alt = cljs.core.boolean$(cljs.core.some((function (p1__22443_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22443_SHARP_,"alt");
}),parts));
var need_meta = cljs.core.boolean$(cljs.core.some((function (p1__22444_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__22444_SHARP_,"meta");
}),parts));
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.key.toLowerCase(),key_part)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_ctrl,e.ctrlKey)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_shift,e.shiftKey)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_alt,e.altKey)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(need_meta,e.metaKey)))))))));
} else {
return null;
}
});
app.components.x_copy.x_copy.add_listeners_BANG_ = (function app$components$x_copy$x_copy$add_listeners_BANG_(el){
var refs_22518 = app.components.x_copy.x_copy.ensure_refs_BANG_(el);
var trigger_22519 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22518,"trigger");
var click_h_22520 = (function (_){
return app.components.x_copy.x_copy.copy_BANG_(el);
});
var key_h_22521 = (function (e){
var hotkey = el.getAttribute(app.components.x_copy.model.attr_hotkey);
if(cljs.core.truth_(app.components.x_copy.x_copy.matches_hotkey_QMARK_(e,hotkey))){
e.preventDefault();

return app.components.x_copy.x_copy.copy_BANG_(el);
} else {
return null;
}
});
trigger_22519.addEventListener("click",click_h_22520);

document.addEventListener("keydown",key_h_22521);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_handlers,({"click": click_h_22520, "keydown": key_h_22521}));

return null;
});
app.components.x_copy.x_copy.remove_listeners_BANG_ = (function app$components$x_copy$x_copy$remove_listeners_BANG_(el){
var temp__5823__auto___22522 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_tid);
if(cljs.core.truth_(temp__5823__auto___22522)){
var tid_22523 = temp__5823__auto___22522;
clearTimeout(tid_22523);

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_tid,null);
} else {
}

var hs_22524 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_handlers);
var refs_22525 = app.components.x_copy.x_copy.goog$module$goog$object.get(el,app.components.x_copy.x_copy.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22524;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22525;
} else {
return and__5140__auto__;
}
})())){
var trigger_22526 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22525,"trigger");
var click_h_22527 = app.components.x_copy.x_copy.goog$module$goog$object.get(hs_22524,"click");
var key_h_22528 = app.components.x_copy.x_copy.goog$module$goog$object.get(hs_22524,"keydown");
if(cljs.core.truth_(click_h_22527)){
trigger_22526.removeEventListener("click",click_h_22527);
} else {
}

if(cljs.core.truth_(key_h_22528)){
document.removeEventListener("keydown",key_h_22528);
} else {
}
} else {
}

app.components.x_copy.x_copy.goog$module$goog$object.set(el,app.components.x_copy.x_copy.k_handlers,null);

return null;
});
app.components.x_copy.x_copy.render_BANG_ = (function app$components$x_copy$x_copy$render_BANG_(el){
var refs_22529 = app.components.x_copy.x_copy.ensure_refs_BANG_(el);
var trigger_22530 = app.components.x_copy.x_copy.goog$module$goog$object.get(refs_22529,"trigger");
var disabled_QMARK__22531 = el.hasAttribute(app.components.x_copy.model.attr_disabled);
(trigger_22530.disabled = cljs.core.boolean$(disabled_QMARK__22531));

if(cljs.core.truth_(disabled_QMARK__22531)){
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
var seq__22450_22532 = cljs.core.seq(new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["text",app.components.x_copy.model.attr_text], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["from",app.components.x_copy.model.attr_from], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["fromAttr",app.components.x_copy.model.attr_from_attr], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["mode",app.components.x_copy.model.attr_mode], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["successMessage",app.components.x_copy.model.attr_success_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["errorMessage",app.components.x_copy.model.attr_error_message], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["hotkey",app.components.x_copy.model.attr_hotkey], null)], null));
var chunk__22451_22533 = null;
var count__22452_22534 = (0);
var i__22453_22535 = (0);
while(true){
if((i__22453_22535 < count__22452_22534)){
var vec__22460_22536 = chunk__22451_22533.cljs$core$IIndexed$_nth$arity$2(null,i__22453_22535);
var js_prop_22537 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22460_22536,(0),null);
var attr_name_22538 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22460_22536,(1),null);
Object.defineProperty(proto,js_prop_22537,({"get": ((function (seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22460_22536,js_prop_22537,attr_name_22538){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22538);
});})(seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22460_22536,js_prop_22537,attr_name_22538))
, "set": ((function (seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22460_22536,js_prop_22537,attr_name_22538){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22538);
} else {
return this$.setAttribute(attr_name_22538,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22460_22536,js_prop_22537,attr_name_22538))
, "enumerable": true, "configurable": true}));


var G__22539 = seq__22450_22532;
var G__22540 = chunk__22451_22533;
var G__22541 = count__22452_22534;
var G__22542 = (i__22453_22535 + (1));
seq__22450_22532 = G__22539;
chunk__22451_22533 = G__22540;
count__22452_22534 = G__22541;
i__22453_22535 = G__22542;
continue;
} else {
var temp__5823__auto___22543 = cljs.core.seq(seq__22450_22532);
if(temp__5823__auto___22543){
var seq__22450_22544__$1 = temp__5823__auto___22543;
if(cljs.core.chunked_seq_QMARK_(seq__22450_22544__$1)){
var c__5673__auto___22545 = cljs.core.chunk_first(seq__22450_22544__$1);
var G__22546 = cljs.core.chunk_rest(seq__22450_22544__$1);
var G__22547 = c__5673__auto___22545;
var G__22548 = cljs.core.count(c__5673__auto___22545);
var G__22549 = (0);
seq__22450_22532 = G__22546;
chunk__22451_22533 = G__22547;
count__22452_22534 = G__22548;
i__22453_22535 = G__22549;
continue;
} else {
var vec__22465_22550 = cljs.core.first(seq__22450_22544__$1);
var js_prop_22551 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22465_22550,(0),null);
var attr_name_22552 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22465_22550,(1),null);
Object.defineProperty(proto,js_prop_22551,({"get": ((function (seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22465_22550,js_prop_22551,attr_name_22552,seq__22450_22544__$1,temp__5823__auto___22543){
return (function (){
var this$ = this;
return this$.getAttribute(attr_name_22552);
});})(seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22465_22550,js_prop_22551,attr_name_22552,seq__22450_22544__$1,temp__5823__auto___22543))
, "set": ((function (seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22465_22550,js_prop_22551,attr_name_22552,seq__22450_22544__$1,temp__5823__auto___22543){
return (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(attr_name_22552);
} else {
return this$.setAttribute(attr_name_22552,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
});})(seq__22450_22532,chunk__22451_22533,count__22452_22534,i__22453_22535,vec__22465_22550,js_prop_22551,attr_name_22552,seq__22450_22544__$1,temp__5823__auto___22543))
, "enumerable": true, "configurable": true}));


var G__22553 = cljs.core.next(seq__22450_22544__$1);
var G__22554 = null;
var G__22555 = (0);
var G__22556 = (0);
seq__22450_22532 = G__22553;
chunk__22451_22533 = G__22554;
count__22452_22534 = G__22555;
i__22453_22535 = G__22556;
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
var proto_22557 = Object.create(HTMLElement.prototype);
var ctor_22558 = app.components.x_copy.x_copy.make_constructor();
Object.setPrototypeOf(ctor_22558,HTMLElement);

(proto_22557["constructor"] = ctor_22558);

app.components.x_copy.x_copy.install_properties_BANG_(proto_22557);

(proto_22557["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_copy.x_copy.connected_BANG_(this$);
}));

(proto_22557["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_copy.x_copy.disconnected_BANG_(this$);
}));

(proto_22557["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_copy.x_copy.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor_22558["observedAttributes"] = app.components.x_copy.model.observed_attributes);

(ctor_22558["prototype"] = proto_22557);

customElements.define(app.components.x_copy.model.tag_name,ctor_22558);
}

return null;
});
app.components.x_copy.x_copy.init_BANG_ = (function app$components$x_copy$x_copy$init_BANG_(){
return app.components.x_copy.x_copy.register_BANG_();
});

//# sourceMappingURL=app.components.x_copy.x_copy.js.map
