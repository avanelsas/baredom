goog.provide('app.components.x_modal.x_modal');
goog.scope(function(){
  app.components.x_modal.x_modal.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_modal.x_modal.k_refs = "__xModalRefs";
app.components.x_modal.x_modal.k_handlers = "__xModalHandlers";
app.components.x_modal.x_modal.k_prev_open = "__xModalPrevOpen";
app.components.x_modal.x_modal.k_restore = "__xModalRestore";
app.components.x_modal.x_modal.k_tabbables = "__xModalTabbables";
app.components.x_modal.x_modal.k_dialog_tab = "__xModalDialogTab";
app.components.x_modal.x_modal.style_text = (""+":host{"+"display:contents;"+"color-scheme:light dark;"+"--x-modal-bg:Canvas;"+"--x-modal-fg:CanvasText;"+"--x-modal-backdrop:rgb(0 0 0/0.45);"+"--x-modal-shadow:0 20px 60px rgb(0 0 0/0.25);"+"--x-modal-radius:0.75rem;"+"--x-modal-width-sm:22rem;"+"--x-modal-width-md:32rem;"+"--x-modal-width-lg:44rem;"+"--x-modal-width-xl:60rem;"+"--x-modal-max-height:90vh;"+"--x-modal-header-padding:1rem 1.25rem;"+"--x-modal-body-padding:1rem 1.25rem;"+"--x-modal-footer-padding:0.75rem 1.25rem;"+"--x-modal-border:color-mix(in srgb,currentColor 12%,transparent);"+"--x-modal-duration:180ms;"+"--x-modal-easing:ease;"+"--x-modal-z:1000;"+"}"+"[part=backdrop]{"+"position:fixed;"+"inset:0;"+"background:var(--x-modal-backdrop);"+"z-index:var(--x-modal-z);"+"opacity:0;"+"visibility:hidden;"+"pointer-events:none;"+"transition:"+"opacity var(--x-modal-duration) var(--x-modal-easing),"+"visibility 0s var(--x-modal-easing) var(--x-modal-duration);"+"}"+":host([data-open=true]) [part=backdrop]{"+"opacity:1;"+"visibility:visible;"+"pointer-events:auto;"+"transition:"+"opacity var(--x-modal-duration) var(--x-modal-easing),"+"visibility 0s;"+"}"+"[part=dialog]{"+"position:fixed;"+"top:50%;"+"left:50%;"+"transform:translate(-50%,-50%) scale(0.96);"+"opacity:0;"+"visibility:hidden;"+"pointer-events:none;"+"background:var(--x-modal-bg);"+"color:var(--x-modal-fg);"+"box-shadow:var(--x-modal-shadow);"+"border-radius:var(--x-modal-radius);"+"display:flex;"+"flex-direction:column;"+"overflow:hidden;"+"z-index:calc(var(--x-modal-z) + 1);"+"max-height:var(--x-modal-max-height);"+"transition:"+"opacity var(--x-modal-duration) var(--x-modal-easing),"+"transform var(--x-modal-duration) var(--x-modal-easing),"+"visibility 0s var(--x-modal-easing) var(--x-modal-duration);"+"}"+":host([data-size=sm]) [part=dialog]{width:var(--x-modal-width-sm);}"+":host([data-size=md]) [part=dialog]{width:var(--x-modal-width-md);}"+":host([data-size=lg]) [part=dialog]{width:var(--x-modal-width-lg);}"+":host([data-size=xl]) [part=dialog]{width:var(--x-modal-width-xl);}"+":host([data-size=full]) [part=dialog]{"+"inset:0;"+"top:0;"+"left:0;"+"width:100vw;"+"height:100dvh;"+"border-radius:0;"+"max-width:100vw;"+"max-height:100dvh;"+"transform:scale(0.98);"+"}"+":host([data-open=true]) [part=dialog]{"+"opacity:1;"+"transform:translate(-50%,-50%) scale(1);"+"visibility:visible;"+"pointer-events:auto;"+"transition:"+"opacity var(--x-modal-duration) var(--x-modal-easing),"+"transform var(--x-modal-duration) var(--x-modal-easing),"+"visibility 0s;"+"}"+":host([data-open=true][data-size=full]) [part=dialog]{"+"transform:scale(1);"+"}"+"[part=dialog]:focus{"+"outline:none;"+"}"+"[part=header]{"+"padding:var(--x-modal-header-padding);"+"border-bottom:1px solid var(--x-modal-border);"+"flex-shrink:0;"+"}"+"[part=body]{"+"padding:var(--x-modal-body-padding);"+"overflow-y:auto;"+"flex:1;"+"}"+"[part=footer]{"+"padding:var(--x-modal-footer-padding);"+"border-top:1px solid var(--x-modal-border);"+"flex-shrink:0;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=dialog],[part=backdrop]{transition:none;}"+"}");
app.components.x_modal.x_modal.make_el = (function app$components$x_modal$x_modal$make_el(tag){
return document.createElement(tag);
});
app.components.x_modal.x_modal.init_dom_BANG_ = (function app$components$x_modal$x_modal$init_dom_BANG_(el){
var root_21205 = el.attachShadow(({"mode": "open"}));
var style_21206 = app.components.x_modal.x_modal.make_el("style");
var backdrop_21207 = app.components.x_modal.x_modal.make_el("div");
var dialog_21208 = app.components.x_modal.x_modal.make_el("div");
var header_21209 = app.components.x_modal.x_modal.make_el("div");
var hslot_21210 = app.components.x_modal.x_modal.make_el("slot");
var body_21211 = app.components.x_modal.x_modal.make_el("div");
var bslot_21212 = app.components.x_modal.x_modal.make_el("slot");
var footer_21213 = app.components.x_modal.x_modal.make_el("div");
var fslot_21214 = app.components.x_modal.x_modal.make_el("slot");
(style_21206.textContent = app.components.x_modal.x_modal.style_text);

backdrop_21207.setAttribute("part",app.components.x_modal.model.part_backdrop);

dialog_21208.setAttribute("part",app.components.x_modal.model.part_dialog);

dialog_21208.setAttribute("role","dialog");

dialog_21208.setAttribute("aria-modal","true");

header_21209.setAttribute("part",app.components.x_modal.model.part_header);

hslot_21210.setAttribute("name","header");

body_21211.setAttribute("part",app.components.x_modal.model.part_body);

footer_21213.setAttribute("part",app.components.x_modal.model.part_footer);

fslot_21214.setAttribute("name","footer");

header_21209.appendChild(hslot_21210);

body_21211.appendChild(bslot_21212);

footer_21213.appendChild(fslot_21214);

dialog_21208.appendChild(header_21209);

dialog_21208.appendChild(body_21211);

dialog_21208.appendChild(footer_21213);

root_21205.appendChild(style_21206);

root_21205.appendChild(backdrop_21207);

root_21205.appendChild(dialog_21208);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_refs,({"root": root_21205, "backdrop": backdrop_21207, "dialog": dialog_21208, "header": header_21209, "body": body_21211, "footer": footer_21213}));

return null;
});
app.components.x_modal.x_modal.ensure_refs_BANG_ = (function app$components$x_modal$x_modal$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_modal.x_modal.init_dom_BANG_(el);

return app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
}
});
app.components.x_modal.x_modal.read_model = (function app$components$x_modal$x_modal$read_model(el){
return app.components.x_modal.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open-present?","open-present?",965047899),el.hasAttribute(app.components.x_modal.model.attr_open),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),el.getAttribute(app.components.x_modal.model.attr_size),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_modal.model.attr_label)], null));
});
app.components.x_modal.x_modal.dispatch_BANG_ = (function app$components$x_modal$x_modal$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_modal.x_modal.collect_tabbables = (function app$components$x_modal$x_modal$collect_tabbables(refs){
var sel = (""+"a[href],button:not([disabled]),input:not([disabled]),"+"select:not([disabled]),textarea:not([disabled]),"+"[tabindex]:not([tabindex='-1'])");
var root = app.components.x_modal.x_modal.goog$module$goog$object.get(refs,"root");
var dialog = app.components.x_modal.x_modal.goog$module$goog$object.get(refs,"dialog");
var visible_QMARK_ = (function (node){
var s = window.getComputedStyle(node);
return ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("none",s.display)) && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("hidden",s.visibility)) && ((((node.offsetWidth > (0))) && ((node.offsetHeight > (0))))))));
});
var shadow_els = cljs.core.filter.cljs$core$IFn$_invoke$arity$2(visible_QMARK_,cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(dialog.querySelectorAll(sel)));
var slots = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(root.querySelectorAll("slot"));
var slotted_els = cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic((function (slot){
return cljs.core.filter.cljs$core$IFn$_invoke$arity$2(visible_QMARK_,cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic((function (ae){
var children = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(ae.querySelectorAll(sel));
if(cljs.core.truth_(ae.matches(sel))){
return cljs.core.cons(ae,children);
} else {
return children;
}
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(slot.assignedElements(({"flatten": true})))], 0)));
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([slots], 0));
return cljs.core.vec(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(shadow_els,slotted_els));
});
app.components.x_modal.x_modal.activate_focus_trap_BANG_ = (function app$components$x_modal$x_modal$activate_focus_trap_BANG_(el){
var refs_21238 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_21239 = (cljs.core.truth_(refs_21238)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21238,"dialog"):null);
var tabbables_21240 = (cljs.core.truth_(refs_21238)?app.components.x_modal.x_modal.collect_tabbables(refs_21238):null);
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_restore,document.activeElement);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_tabbables,(cljs.core.truth_(tabbables_21240)?cljs.core.clj__GT_js(tabbables_21240):null));

if(cljs.core.seq(tabbables_21240)){
cljs.core.first(tabbables_21240).focus();
} else {
if(cljs.core.truth_(dialog_21239)){
if(cljs.core.truth_(dialog_21239.hasAttribute("tabindex"))){
} else {
dialog_21239.setAttribute("tabindex","-1");

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_dialog_tab,true);
}

dialog_21239.focus();
} else {
}
}

return null;
});
app.components.x_modal.x_modal.deactivate_focus_trap_BANG_ = (function app$components$x_modal$x_modal$deactivate_focus_trap_BANG_(el){
var refs_21252 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_21253 = (cljs.core.truth_(refs_21252)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21252,"dialog"):null);
var restore_21254 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_restore);
var dialog_tab_added_21255 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_dialog_tab) === true;
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_tabbables,null);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_restore,null);

if(cljs.core.truth_((function (){var and__5140__auto__ = dialog_21253;
if(cljs.core.truth_(and__5140__auto__)){
return dialog_tab_added_21255;
} else {
return and__5140__auto__;
}
})())){
dialog_21253.removeAttribute("tabindex");

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_dialog_tab,false);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = restore_21254;
if(cljs.core.truth_(and__5140__auto__)){
return restore_21254.isConnected;
} else {
return and__5140__auto__;
}
})())){
restore_21254.focus();
} else {
}

return null;
});
app.components.x_modal.x_modal.cycle_focus_BANG_ = (function app$components$x_modal$x_modal$cycle_focus_BANG_(el,e){
var tabbables_js_21258 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_tabbables);
var tabbables_21259 = (cljs.core.truth_(tabbables_js_21258)?cljs.core.vec(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(tabbables_js_21258)):cljs.core.PersistentVector.EMPTY);
var refs_21260 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_21261 = (cljs.core.truth_(refs_21260)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21260,"dialog"):null);
if(cljs.core.empty_QMARK_(tabbables_21259)){
e.preventDefault();

if(cljs.core.truth_(dialog_21261)){
dialog_21261.focus();
} else {
}
} else {
var active_21267 = document.activeElement;
var first_el_21268 = cljs.core.first(tabbables_21259);
var last_el_21269 = cljs.core.last(tabbables_21259);
var shift_QMARK__21270 = e.shiftKey;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK__21270;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_21267,first_el_21268);
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

last_el_21269.focus();
} else {
if(((cljs.core.not(shift_QMARK__21270)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_21267,last_el_21269)))){
e.preventDefault();

first_el_21268.focus();
} else {

}
}
}

return null;
});
app.components.x_modal.x_modal.do_dismiss_BANG_ = (function app$components$x_modal$x_modal$do_dismiss_BANG_(el,reason){
app.components.x_modal.x_modal.dispatch_BANG_(el,app.components.x_modal.model.event_dismiss,app.components.x_modal.model.dismiss_event_detail(reason));

el.removeAttribute(app.components.x_modal.model.attr_open);

return null;
});
app.components.x_modal.x_modal.do_show_BANG_ = (function app$components$x_modal$x_modal$do_show_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_modal.model.attr_open))){
} else {
el.setAttribute(app.components.x_modal.model.attr_open,"");
}

return null;
});
app.components.x_modal.x_modal.do_hide_BANG_ = (function app$components$x_modal$x_modal$do_hide_BANG_(el){
el.removeAttribute(app.components.x_modal.model.attr_open);

return null;
});
app.components.x_modal.x_modal.do_toggle_BANG_ = (function app$components$x_modal$x_modal$do_toggle_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_modal.model.attr_open))){
app.components.x_modal.x_modal.do_hide_BANG_(el);
} else {
app.components.x_modal.x_modal.do_show_BANG_(el);
}

return null;
});
app.components.x_modal.x_modal.render_BANG_ = (function app$components$x_modal$x_modal$render_BANG_(el){
var refs_21329 = app.components.x_modal.x_modal.ensure_refs_BANG_(el);
var dialog_21330 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21329,"dialog");
var m_21331 = app.components.x_modal.x_modal.read_model(el);
var open_QMARK__21332 = new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(m_21331);
var prev_open_21333 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_prev_open);
el.setAttribute("data-open",(cljs.core.truth_(open_QMARK__21332)?"true":"false"));

el.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(m_21331));

dialog_21330.setAttribute("aria-label",new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m_21331));

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(prev_open_21333,open_QMARK__21332)){
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_prev_open,open_QMARK__21332);

app.components.x_modal.x_modal.dispatch_BANG_(el,app.components.x_modal.model.event_toggle,app.components.x_modal.model.toggle_event_detail(open_QMARK__21332));

if(cljs.core.truth_(open_QMARK__21332)){
setTimeout((function (){
return app.components.x_modal.x_modal.activate_focus_trap_BANG_(el);
}),(0));
} else {
app.components.x_modal.x_modal.deactivate_focus_trap_BANG_(el);
}
} else {
}

return null;
});
app.components.x_modal.x_modal.on_keydown_BANG_ = (function app$components$x_modal$x_modal$on_keydown_BANG_(el,e){
var key_21344 = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_21344,"Escape")){
e.preventDefault();

app.components.x_modal.x_modal.do_dismiss_BANG_(el,app.components.x_modal.model.reason_escape);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_21344,"Tab")){
app.components.x_modal.x_modal.cycle_focus_BANG_(el,e);
} else {
}
}

return null;
});
app.components.x_modal.x_modal.add_listeners_BANG_ = (function app$components$x_modal$x_modal$add_listeners_BANG_(el){
var refs_21345 = app.components.x_modal.x_modal.ensure_refs_BANG_(el);
var backdrop_21346 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21345,"backdrop");
var dialog_21347 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21345,"dialog");
var backdrop_h_21348 = (function (_){
return app.components.x_modal.x_modal.do_dismiss_BANG_(el,app.components.x_modal.model.reason_backdrop);
});
var keydown_h_21349 = (function (e){
return app.components.x_modal.x_modal.on_keydown_BANG_(el,e);
});
backdrop_21346.addEventListener("click",backdrop_h_21348);

dialog_21347.addEventListener("keydown",keydown_h_21349);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_handlers,({"backdrop": backdrop_h_21348, "keydown": keydown_h_21349}));

return null;
});
app.components.x_modal.x_modal.remove_listeners_BANG_ = (function app$components$x_modal$x_modal$remove_listeners_BANG_(el){
var hs_21351 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_handlers);
var refs_21352 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_21351;
if(cljs.core.truth_(and__5140__auto__)){
return refs_21352;
} else {
return and__5140__auto__;
}
})())){
var backdrop_21353 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21352,"backdrop");
var dialog_21354 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_21352,"dialog");
var backdrop_h_21355 = app.components.x_modal.x_modal.goog$module$goog$object.get(hs_21351,"backdrop");
var keydown_h_21356 = app.components.x_modal.x_modal.goog$module$goog$object.get(hs_21351,"keydown");
if(cljs.core.truth_(backdrop_h_21355)){
backdrop_21353.removeEventListener("click",backdrop_h_21355);
} else {
}

if(cljs.core.truth_(keydown_h_21356)){
dialog_21354.removeEventListener("keydown",keydown_h_21356);
} else {
}
} else {
}

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_handlers,null);

return null;
});
app.components.x_modal.x_modal.connected_BANG_ = (function app$components$x_modal$x_modal$connected_BANG_(el){
app.components.x_modal.x_modal.ensure_refs_BANG_(el);

app.components.x_modal.x_modal.remove_listeners_BANG_(el);

app.components.x_modal.x_modal.add_listeners_BANG_(el);

app.components.x_modal.x_modal.render_BANG_(el);

return null;
});
app.components.x_modal.x_modal.disconnected_BANG_ = (function app$components$x_modal$x_modal$disconnected_BANG_(el){
app.components.x_modal.x_modal.remove_listeners_BANG_(el);

app.components.x_modal.x_modal.deactivate_focus_trap_BANG_(el);

return null;
});
app.components.x_modal.x_modal.attribute_changed_BANG_ = (function app$components$x_modal$x_modal$attribute_changed_BANG_(el,_attr_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_modal.x_modal.render_BANG_(el);
} else {
}

return null;
});
app.components.x_modal.x_modal.install_properties_BANG_ = (function app$components$x_modal$x_modal$install_properties_BANG_(proto){
Object.defineProperty(proto,"open",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_modal.model.attr_open);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return app.components.x_modal.x_modal.do_show_BANG_(this$);
} else {
return app.components.x_modal.x_modal.do_hide_BANG_(this$);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"size",({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_modal.model.attr_size);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_modal.model.default_size;
}
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_modal.model.attr_size);
} else {
return this$.setAttribute(app.components.x_modal.model.attr_size,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"label",({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_modal.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_modal.model.default_label;
}
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_modal.model.attr_label);
} else {
return this$.setAttribute(app.components.x_modal.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"show",({"value": (function (){
var this$ = this;
return app.components.x_modal.x_modal.do_show_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

Object.defineProperty(proto,"hide",({"value": (function (){
var this$ = this;
return app.components.x_modal.x_modal.do_hide_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

return Object.defineProperty(proto,"toggle",({"value": (function (){
var this$ = this;
return app.components.x_modal.x_modal.do_toggle_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));
});
app.components.x_modal.x_modal.define_element_BANG_ = (function app$components$x_modal$x_modal$define_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_modal.model.tag_name))){
} else {
var klass_21376 = (class extends HTMLElement {});
(klass_21376.observedAttributes = app.components.x_modal.model.observed_attributes);

(klass_21376.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_modal.x_modal.connected_BANG_(this$);
}));

(klass_21376.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_modal.x_modal.disconnected_BANG_(this$);
}));

(klass_21376.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
return app.components.x_modal.x_modal.attribute_changed_BANG_(this$,n,o,v);
}));

app.components.x_modal.x_modal.install_properties_BANG_(klass_21376.prototype);

customElements.define(app.components.x_modal.model.tag_name,klass_21376);
}

return null;
});
app.components.x_modal.x_modal.register_BANG_ = (function app$components$x_modal$x_modal$register_BANG_(){
return app.components.x_modal.x_modal.define_element_BANG_();
});
app.components.x_modal.x_modal.init_BANG_ = (function app$components$x_modal$x_modal$init_BANG_(){
return app.components.x_modal.x_modal.register_BANG_();
});

//# sourceMappingURL=app.components.x_modal.x_modal.js.map
