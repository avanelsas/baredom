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
var root_22804 = el.attachShadow(({"mode": "open"}));
var style_22805 = app.components.x_modal.x_modal.make_el("style");
var backdrop_22806 = app.components.x_modal.x_modal.make_el("div");
var dialog_22807 = app.components.x_modal.x_modal.make_el("div");
var header_22808 = app.components.x_modal.x_modal.make_el("div");
var hslot_22809 = app.components.x_modal.x_modal.make_el("slot");
var body_22810 = app.components.x_modal.x_modal.make_el("div");
var bslot_22811 = app.components.x_modal.x_modal.make_el("slot");
var footer_22812 = app.components.x_modal.x_modal.make_el("div");
var fslot_22813 = app.components.x_modal.x_modal.make_el("slot");
(style_22805.textContent = app.components.x_modal.x_modal.style_text);

backdrop_22806.setAttribute("part",app.components.x_modal.model.part_backdrop);

dialog_22807.setAttribute("part",app.components.x_modal.model.part_dialog);

dialog_22807.setAttribute("role","dialog");

dialog_22807.setAttribute("aria-modal","true");

header_22808.setAttribute("part",app.components.x_modal.model.part_header);

hslot_22809.setAttribute("name","header");

body_22810.setAttribute("part",app.components.x_modal.model.part_body);

footer_22812.setAttribute("part",app.components.x_modal.model.part_footer);

fslot_22813.setAttribute("name","footer");

header_22808.appendChild(hslot_22809);

body_22810.appendChild(bslot_22811);

footer_22812.appendChild(fslot_22813);

dialog_22807.appendChild(header_22808);

dialog_22807.appendChild(body_22810);

dialog_22807.appendChild(footer_22812);

root_22804.appendChild(style_22805);

root_22804.appendChild(backdrop_22806);

root_22804.appendChild(dialog_22807);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_refs,({"root": root_22804, "backdrop": backdrop_22806, "dialog": dialog_22807, "header": header_22808, "body": body_22810, "footer": footer_22812}));

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
var refs_22814 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_22815 = (cljs.core.truth_(refs_22814)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22814,"dialog"):null);
var tabbables_22816 = (cljs.core.truth_(refs_22814)?app.components.x_modal.x_modal.collect_tabbables(refs_22814):null);
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_restore,document.activeElement);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_tabbables,(cljs.core.truth_(tabbables_22816)?cljs.core.clj__GT_js(tabbables_22816):null));

if(cljs.core.seq(tabbables_22816)){
cljs.core.first(tabbables_22816).focus();
} else {
if(cljs.core.truth_(dialog_22815)){
if(cljs.core.truth_(dialog_22815.hasAttribute("tabindex"))){
} else {
dialog_22815.setAttribute("tabindex","-1");

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_dialog_tab,true);
}

dialog_22815.focus();
} else {
}
}

return null;
});
app.components.x_modal.x_modal.deactivate_focus_trap_BANG_ = (function app$components$x_modal$x_modal$deactivate_focus_trap_BANG_(el){
var refs_22817 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_22818 = (cljs.core.truth_(refs_22817)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22817,"dialog"):null);
var restore_22819 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_restore);
var dialog_tab_added_22820 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_dialog_tab) === true;
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_tabbables,null);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_restore,null);

if(cljs.core.truth_((function (){var and__5140__auto__ = dialog_22818;
if(cljs.core.truth_(and__5140__auto__)){
return dialog_tab_added_22820;
} else {
return and__5140__auto__;
}
})())){
dialog_22818.removeAttribute("tabindex");

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_dialog_tab,false);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = restore_22819;
if(cljs.core.truth_(and__5140__auto__)){
return restore_22819.isConnected;
} else {
return and__5140__auto__;
}
})())){
restore_22819.focus();
} else {
}

return null;
});
app.components.x_modal.x_modal.cycle_focus_BANG_ = (function app$components$x_modal$x_modal$cycle_focus_BANG_(el,e){
var tabbables_js_22821 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_tabbables);
var tabbables_22822 = (cljs.core.truth_(tabbables_js_22821)?cljs.core.vec(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(tabbables_js_22821)):cljs.core.PersistentVector.EMPTY);
var refs_22823 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
var dialog_22824 = (cljs.core.truth_(refs_22823)?app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22823,"dialog"):null);
if(cljs.core.empty_QMARK_(tabbables_22822)){
e.preventDefault();

if(cljs.core.truth_(dialog_22824)){
dialog_22824.focus();
} else {
}
} else {
var active_22825 = document.activeElement;
var first_el_22826 = cljs.core.first(tabbables_22822);
var last_el_22827 = cljs.core.last(tabbables_22822);
var shift_QMARK__22828 = e.shiftKey;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK__22828;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22825,first_el_22826);
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

last_el_22827.focus();
} else {
if(((cljs.core.not(shift_QMARK__22828)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22825,last_el_22827)))){
e.preventDefault();

first_el_22826.focus();
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
var refs_22829 = app.components.x_modal.x_modal.ensure_refs_BANG_(el);
var dialog_22830 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22829,"dialog");
var m_22831 = app.components.x_modal.x_modal.read_model(el);
var open_QMARK__22832 = new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(m_22831);
var prev_open_22833 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_prev_open);
el.setAttribute("data-open",(cljs.core.truth_(open_QMARK__22832)?"true":"false"));

el.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(m_22831));

dialog_22830.setAttribute("aria-label",new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m_22831));

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(prev_open_22833,open_QMARK__22832)){
app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_prev_open,open_QMARK__22832);

app.components.x_modal.x_modal.dispatch_BANG_(el,app.components.x_modal.model.event_toggle,app.components.x_modal.model.toggle_event_detail(open_QMARK__22832));

if(cljs.core.truth_(open_QMARK__22832)){
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
var key_22837 = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22837,"Escape")){
e.preventDefault();

app.components.x_modal.x_modal.do_dismiss_BANG_(el,app.components.x_modal.model.reason_escape);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22837,"Tab")){
app.components.x_modal.x_modal.cycle_focus_BANG_(el,e);
} else {
}
}

return null;
});
app.components.x_modal.x_modal.add_listeners_BANG_ = (function app$components$x_modal$x_modal$add_listeners_BANG_(el){
var refs_22840 = app.components.x_modal.x_modal.ensure_refs_BANG_(el);
var backdrop_22841 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22840,"backdrop");
var dialog_22842 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22840,"dialog");
var backdrop_h_22843 = (function (_){
return app.components.x_modal.x_modal.do_dismiss_BANG_(el,app.components.x_modal.model.reason_backdrop);
});
var keydown_h_22844 = (function (e){
return app.components.x_modal.x_modal.on_keydown_BANG_(el,e);
});
backdrop_22841.addEventListener("click",backdrop_h_22843);

dialog_22842.addEventListener("keydown",keydown_h_22844);

app.components.x_modal.x_modal.goog$module$goog$object.set(el,app.components.x_modal.x_modal.k_handlers,({"backdrop": backdrop_h_22843, "keydown": keydown_h_22844}));

return null;
});
app.components.x_modal.x_modal.remove_listeners_BANG_ = (function app$components$x_modal$x_modal$remove_listeners_BANG_(el){
var hs_22854 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_handlers);
var refs_22855 = app.components.x_modal.x_modal.goog$module$goog$object.get(el,app.components.x_modal.x_modal.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22854;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22855;
} else {
return and__5140__auto__;
}
})())){
var backdrop_22856 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22855,"backdrop");
var dialog_22857 = app.components.x_modal.x_modal.goog$module$goog$object.get(refs_22855,"dialog");
var backdrop_h_22858 = app.components.x_modal.x_modal.goog$module$goog$object.get(hs_22854,"backdrop");
var keydown_h_22859 = app.components.x_modal.x_modal.goog$module$goog$object.get(hs_22854,"keydown");
if(cljs.core.truth_(backdrop_h_22858)){
backdrop_22856.removeEventListener("click",backdrop_h_22858);
} else {
}

if(cljs.core.truth_(keydown_h_22859)){
dialog_22857.removeEventListener("keydown",keydown_h_22859);
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
var klass_22867 = (class extends HTMLElement {});
(klass_22867.observedAttributes = app.components.x_modal.model.observed_attributes);

(klass_22867.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_modal.x_modal.connected_BANG_(this$);
}));

(klass_22867.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_modal.x_modal.disconnected_BANG_(this$);
}));

(klass_22867.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
return app.components.x_modal.x_modal.attribute_changed_BANG_(this$,n,o,v);
}));

app.components.x_modal.x_modal.install_properties_BANG_(klass_22867.prototype);

customElements.define(app.components.x_modal.model.tag_name,klass_22867);
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
