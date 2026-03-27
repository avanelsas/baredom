goog.provide('app.components.x_drawer.x_drawer');
goog.scope(function(){
  app.components.x_drawer.x_drawer.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_drawer.x_drawer.k_refs = "__xDrawerRefs";
app.components.x_drawer.x_drawer.k_handlers = "__xDrawerHandlers";
app.components.x_drawer.x_drawer.k_prev_open = "__xDrawerPrevOpen";
app.components.x_drawer.x_drawer.k_restore = "__xDrawerRestore";
app.components.x_drawer.x_drawer.k_tabbables = "__xDrawerTabbables";
app.components.x_drawer.x_drawer.k_panel_tab = "__xDrawerPanelTab";
app.components.x_drawer.x_drawer.style_text = (""+":host{"+"display:contents;"+"color-scheme:light dark;"+"--x-drawer-size:20rem;"+"--x-drawer-bg:Canvas;"+"--x-drawer-fg:CanvasText;"+"--x-drawer-backdrop:rgb(0 0 0/0.4);"+"--x-drawer-shadow:0 8px 24px rgb(0 0 0/0.18);"+"--x-drawer-duration:200ms;"+"--x-drawer-easing:ease;"+"--x-drawer-z:1000;"+"--x-drawer-header-padding:1rem 1.25rem;"+"--x-drawer-body-padding:1rem 1.25rem;"+"--x-drawer-footer-padding:0.75rem 1.25rem;"+"--x-drawer-border:color-mix(in srgb,currentColor 12%,transparent);"+"}"+"[part=backdrop]{"+"display:none;"+"position:fixed;"+"inset:0;"+"background:var(--x-drawer-backdrop);"+"z-index:var(--x-drawer-z);"+"}"+":host([data-open=true]) [part=backdrop]{"+"display:block;"+"}"+"[part=panel]{"+"position:fixed;"+"background:var(--x-drawer-bg);"+"color:var(--x-drawer-fg);"+"box-shadow:var(--x-drawer-shadow);"+"display:flex;"+"flex-direction:column;"+"overflow:hidden;"+"z-index:calc(var(--x-drawer-z) + 1);"+"transition:transform var(--x-drawer-duration) var(--x-drawer-easing);"+"}"+":host([data-placement=left]) [part=panel],"+":host([data-placement=right]) [part=panel]{"+"top:0;bottom:0;"+"width:var(--x-drawer-size);"+"max-width:100vw;"+"}"+":host([data-placement=left]) [part=panel]{"+"left:0;"+"transform:translateX(-100%);"+"}"+":host([data-placement=right]) [part=panel]{"+"right:0;"+"transform:translateX(100%);"+"}"+":host([data-placement=top]) [part=panel],"+":host([data-placement=bottom]) [part=panel]{"+"left:0;right:0;"+"height:var(--x-drawer-size);"+"max-height:100vh;"+"}"+":host([data-placement=top]) [part=panel]{"+"top:0;"+"transform:translateY(-100%);"+"}"+":host([data-placement=bottom]) [part=panel]{"+"bottom:0;"+"transform:translateY(100%);"+"}"+":host([data-open=true]) [part=panel]{"+"transform:none;"+"}"+"[part=panel]:focus{"+"outline:none;"+"}"+"[part=header]{"+"padding:var(--x-drawer-header-padding);"+"border-bottom:1px solid var(--x-drawer-border);"+"flex-shrink:0;"+"}"+"[part=body]{"+"padding:var(--x-drawer-body-padding);"+"overflow-y:auto;"+"flex:1;"+"}"+"[part=footer]{"+"padding:var(--x-drawer-footer-padding);"+"border-top:1px solid var(--x-drawer-border);"+"flex-shrink:0;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=panel]{transition:none;}"+"}");
app.components.x_drawer.x_drawer.make_el = (function app$components$x_drawer$x_drawer$make_el(tag){
return document.createElement(tag);
});
app.components.x_drawer.x_drawer.init_dom_BANG_ = (function app$components$x_drawer$x_drawer$init_dom_BANG_(el){
var root_22629 = el.attachShadow(({"mode": "open"}));
var style_22630 = app.components.x_drawer.x_drawer.make_el("style");
var backdrop_22631 = app.components.x_drawer.x_drawer.make_el("div");
var panel_22632 = app.components.x_drawer.x_drawer.make_el("div");
var header_22633 = app.components.x_drawer.x_drawer.make_el("div");
var hslot_22634 = app.components.x_drawer.x_drawer.make_el("slot");
var body_22635 = app.components.x_drawer.x_drawer.make_el("div");
var bslot_22636 = app.components.x_drawer.x_drawer.make_el("slot");
var footer_22637 = app.components.x_drawer.x_drawer.make_el("div");
var fslot_22638 = app.components.x_drawer.x_drawer.make_el("slot");
(style_22630.textContent = app.components.x_drawer.x_drawer.style_text);

backdrop_22631.setAttribute("part",app.components.x_drawer.model.part_backdrop);

panel_22632.setAttribute("part",app.components.x_drawer.model.part_panel);

panel_22632.setAttribute("role","dialog");

panel_22632.setAttribute("aria-modal","true");

header_22633.setAttribute("part",app.components.x_drawer.model.part_header);

hslot_22634.setAttribute("name","header");

body_22635.setAttribute("part",app.components.x_drawer.model.part_body);

footer_22637.setAttribute("part",app.components.x_drawer.model.part_footer);

fslot_22638.setAttribute("name","footer");

header_22633.appendChild(hslot_22634);

body_22635.appendChild(bslot_22636);

footer_22637.appendChild(fslot_22638);

panel_22632.appendChild(header_22633);

panel_22632.appendChild(body_22635);

panel_22632.appendChild(footer_22637);

root_22629.appendChild(style_22630);

root_22629.appendChild(backdrop_22631);

root_22629.appendChild(panel_22632);

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_refs,({"root": root_22629, "backdrop": backdrop_22631, "panel": panel_22632, "header": header_22633, "body": body_22635, "footer": footer_22637}));

return null;
});
app.components.x_drawer.x_drawer.ensure_refs_BANG_ = (function app$components$x_drawer$x_drawer$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_drawer.x_drawer.init_dom_BANG_(el);

return app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
}
});
app.components.x_drawer.x_drawer.read_model = (function app$components$x_drawer$x_drawer$read_model(el){
return app.components.x_drawer.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open-present?","open-present?",965047899),el.hasAttribute(app.components.x_drawer.model.attr_open),new cljs.core.Keyword(null,"placement-raw","placement-raw",-1500957198),el.getAttribute(app.components.x_drawer.model.attr_placement),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_drawer.model.attr_label)], null));
});
app.components.x_drawer.x_drawer.dispatch_BANG_ = (function app$components$x_drawer$x_drawer$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_drawer.x_drawer.collect_tabbables = (function app$components$x_drawer$x_drawer$collect_tabbables(panel){
var sel = (""+"a[href],button:not([disabled]),input:not([disabled]),"+"select:not([disabled]),textarea:not([disabled]),"+"[tabindex]:not([tabindex='-1'])");
return cljs.core.vec(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (node){
var s = window.getComputedStyle(node);
return ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("none",s.display)) && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("hidden",s.visibility)) && ((((node.offsetWidth > (0))) && ((node.offsetHeight > (0))))))));
}),cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(panel.querySelectorAll(sel))));
});
app.components.x_drawer.x_drawer.activate_focus_trap_BANG_ = (function app$components$x_drawer$x_drawer$activate_focus_trap_BANG_(el){
var refs_22646 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
var panel_22647 = (cljs.core.truth_(refs_22646)?app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22646,"panel"):null);
var tabbables_22648 = (cljs.core.truth_(panel_22647)?app.components.x_drawer.x_drawer.collect_tabbables(panel_22647):null);
app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_restore,document.activeElement);

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_tabbables,(cljs.core.truth_(tabbables_22648)?cljs.core.clj__GT_js(tabbables_22648):null));

if(cljs.core.seq(tabbables_22648)){
cljs.core.first(tabbables_22648).focus();
} else {
if(cljs.core.truth_(panel_22647)){
if(cljs.core.truth_(panel_22647.hasAttribute("tabindex"))){
} else {
panel_22647.setAttribute("tabindex","-1");

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_panel_tab,true);
}

panel_22647.focus();
} else {
}
}

return null;
});
app.components.x_drawer.x_drawer.deactivate_focus_trap_BANG_ = (function app$components$x_drawer$x_drawer$deactivate_focus_trap_BANG_(el){
var refs_22649 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
var panel_22650 = (cljs.core.truth_(refs_22649)?app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22649,"panel"):null);
var restore_22651 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_restore);
var panel_tab_added_22652 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_panel_tab) === true;
app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_tabbables,null);

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_restore,null);

if(cljs.core.truth_((function (){var and__5140__auto__ = panel_22650;
if(cljs.core.truth_(and__5140__auto__)){
return panel_tab_added_22652;
} else {
return and__5140__auto__;
}
})())){
panel_22650.removeAttribute("tabindex");

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_panel_tab,false);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = restore_22651;
if(cljs.core.truth_(and__5140__auto__)){
return restore_22651.isConnected;
} else {
return and__5140__auto__;
}
})())){
restore_22651.focus();
} else {
}

return null;
});
app.components.x_drawer.x_drawer.cycle_focus_BANG_ = (function app$components$x_drawer$x_drawer$cycle_focus_BANG_(el,e){
var tabbables_js_22666 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_tabbables);
var tabbables_22667 = (cljs.core.truth_(tabbables_js_22666)?cljs.core.vec(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(tabbables_js_22666)):cljs.core.PersistentVector.EMPTY);
var refs_22668 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
var panel_22669 = (cljs.core.truth_(refs_22668)?app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22668,"panel"):null);
if(cljs.core.empty_QMARK_(tabbables_22667)){
e.preventDefault();

if(cljs.core.truth_(panel_22669)){
panel_22669.focus();
} else {
}
} else {
var active_22670 = document.activeElement;
var first_el_22671 = cljs.core.first(tabbables_22667);
var last_el_22672 = cljs.core.last(tabbables_22667);
var shift_QMARK__22673 = e.shiftKey;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK__22673;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22670,first_el_22671);
} else {
return and__5140__auto__;
}
})())){
e.preventDefault();

last_el_22672.focus();
} else {
if(((cljs.core.not(shift_QMARK__22673)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active_22670,last_el_22672)))){
e.preventDefault();

first_el_22671.focus();
} else {

}
}
}

return null;
});
app.components.x_drawer.x_drawer.do_dismiss_BANG_ = (function app$components$x_drawer$x_drawer$do_dismiss_BANG_(el,reason){
app.components.x_drawer.x_drawer.dispatch_BANG_(el,app.components.x_drawer.model.event_dismiss,app.components.x_drawer.model.dismiss_event_detail(reason));

el.removeAttribute(app.components.x_drawer.model.attr_open);

return null;
});
app.components.x_drawer.x_drawer.do_show_BANG_ = (function app$components$x_drawer$x_drawer$do_show_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_drawer.model.attr_open))){
} else {
el.setAttribute(app.components.x_drawer.model.attr_open,"");
}

return null;
});
app.components.x_drawer.x_drawer.do_hide_BANG_ = (function app$components$x_drawer$x_drawer$do_hide_BANG_(el){
el.removeAttribute(app.components.x_drawer.model.attr_open);

return null;
});
app.components.x_drawer.x_drawer.do_toggle_BANG_ = (function app$components$x_drawer$x_drawer$do_toggle_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_drawer.model.attr_open))){
app.components.x_drawer.x_drawer.do_hide_BANG_(el);
} else {
app.components.x_drawer.x_drawer.do_show_BANG_(el);
}

return null;
});
app.components.x_drawer.x_drawer.render_BANG_ = (function app$components$x_drawer$x_drawer$render_BANG_(el){
var refs_22674 = app.components.x_drawer.x_drawer.ensure_refs_BANG_(el);
var panel_22675 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22674,"panel");
var m_22676 = app.components.x_drawer.x_drawer.read_model(el);
var open_QMARK__22677 = new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(m_22676);
var prev_open_22678 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_prev_open);
el.setAttribute("data-open",(cljs.core.truth_(open_QMARK__22677)?"true":"false"));

el.setAttribute("data-placement",new cljs.core.Keyword(null,"placement","placement",768366651).cljs$core$IFn$_invoke$arity$1(m_22676));

panel_22675.setAttribute("aria-label",new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m_22676));

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(prev_open_22678,open_QMARK__22677)){
app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_prev_open,open_QMARK__22677);

app.components.x_drawer.x_drawer.dispatch_BANG_(el,app.components.x_drawer.model.event_toggle,app.components.x_drawer.model.toggle_event_detail(open_QMARK__22677));

if(cljs.core.truth_(open_QMARK__22677)){
setTimeout((function (){
return app.components.x_drawer.x_drawer.activate_focus_trap_BANG_(el);
}),(0));
} else {
app.components.x_drawer.x_drawer.deactivate_focus_trap_BANG_(el);
}
} else {
}

return null;
});
app.components.x_drawer.x_drawer.on_keydown_BANG_ = (function app$components$x_drawer$x_drawer$on_keydown_BANG_(el,e){
var key_22679 = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22679,"Escape")){
e.preventDefault();

app.components.x_drawer.x_drawer.do_dismiss_BANG_(el,app.components.x_drawer.model.reason_escape);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_22679,"Tab")){
app.components.x_drawer.x_drawer.cycle_focus_BANG_(el,e);
} else {
}
}

return null;
});
app.components.x_drawer.x_drawer.add_listeners_BANG_ = (function app$components$x_drawer$x_drawer$add_listeners_BANG_(el){
var refs_22680 = app.components.x_drawer.x_drawer.ensure_refs_BANG_(el);
var backdrop_22681 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22680,"backdrop");
var panel_22682 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22680,"panel");
var backdrop_h_22683 = (function (_){
return app.components.x_drawer.x_drawer.do_dismiss_BANG_(el,app.components.x_drawer.model.reason_backdrop);
});
var keydown_h_22684 = (function (e){
return app.components.x_drawer.x_drawer.on_keydown_BANG_(el,e);
});
backdrop_22681.addEventListener("click",backdrop_h_22683);

panel_22682.addEventListener("keydown",keydown_h_22684);

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_handlers,({"backdrop": backdrop_h_22683, "keydown": keydown_h_22684}));

return null;
});
app.components.x_drawer.x_drawer.remove_listeners_BANG_ = (function app$components$x_drawer$x_drawer$remove_listeners_BANG_(el){
var hs_22685 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_handlers);
var refs_22686 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(el,app.components.x_drawer.x_drawer.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22685;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22686;
} else {
return and__5140__auto__;
}
})())){
var backdrop_22687 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22686,"backdrop");
var panel_22688 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(refs_22686,"panel");
var backdrop_h_22689 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(hs_22685,"backdrop");
var keydown_h_22690 = app.components.x_drawer.x_drawer.goog$module$goog$object.get(hs_22685,"keydown");
if(cljs.core.truth_(backdrop_h_22689)){
backdrop_22687.removeEventListener("click",backdrop_h_22689);
} else {
}

if(cljs.core.truth_(keydown_h_22690)){
panel_22688.removeEventListener("keydown",keydown_h_22690);
} else {
}
} else {
}

app.components.x_drawer.x_drawer.goog$module$goog$object.set(el,app.components.x_drawer.x_drawer.k_handlers,null);

return null;
});
app.components.x_drawer.x_drawer.connected_BANG_ = (function app$components$x_drawer$x_drawer$connected_BANG_(el){
app.components.x_drawer.x_drawer.ensure_refs_BANG_(el);

app.components.x_drawer.x_drawer.remove_listeners_BANG_(el);

app.components.x_drawer.x_drawer.add_listeners_BANG_(el);

app.components.x_drawer.x_drawer.render_BANG_(el);

return null;
});
app.components.x_drawer.x_drawer.disconnected_BANG_ = (function app$components$x_drawer$x_drawer$disconnected_BANG_(el){
app.components.x_drawer.x_drawer.remove_listeners_BANG_(el);

app.components.x_drawer.x_drawer.deactivate_focus_trap_BANG_(el);

return null;
});
app.components.x_drawer.x_drawer.attribute_changed_BANG_ = (function app$components$x_drawer$x_drawer$attribute_changed_BANG_(el,_attr_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_drawer.x_drawer.render_BANG_(el);
} else {
}

return null;
});
app.components.x_drawer.x_drawer.install_properties_BANG_ = (function app$components$x_drawer$x_drawer$install_properties_BANG_(proto){
Object.defineProperty(proto,"open",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_drawer.model.attr_open);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return app.components.x_drawer.x_drawer.do_show_BANG_(this$);
} else {
return app.components.x_drawer.x_drawer.do_hide_BANG_(this$);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"placement",({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_drawer.model.attr_placement);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_drawer.model.default_placement;
}
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_drawer.model.attr_placement);
} else {
return this$.setAttribute(app.components.x_drawer.model.attr_placement,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"label",({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_drawer.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_drawer.model.default_label;
}
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_drawer.model.attr_label);
} else {
return this$.setAttribute(app.components.x_drawer.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"show",({"value": (function (){
var this$ = this;
return app.components.x_drawer.x_drawer.do_show_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

Object.defineProperty(proto,"hide",({"value": (function (){
var this$ = this;
return app.components.x_drawer.x_drawer.do_hide_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));

return Object.defineProperty(proto,"toggle",({"value": (function (){
var this$ = this;
return app.components.x_drawer.x_drawer.do_toggle_BANG_(this$);
}), "enumerable": true, "configurable": true, "writable": true}));
});
app.components.x_drawer.x_drawer.define_element_BANG_ = (function app$components$x_drawer$x_drawer$define_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_drawer.model.tag_name))){
} else {
var klass_22691 = (class extends HTMLElement {});
(klass_22691.observedAttributes = app.components.x_drawer.model.observed_attributes);

(klass_22691.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_drawer.x_drawer.connected_BANG_(this$);
}));

(klass_22691.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_drawer.x_drawer.disconnected_BANG_(this$);
}));

(klass_22691.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
return app.components.x_drawer.x_drawer.attribute_changed_BANG_(this$,n,o,v);
}));

app.components.x_drawer.x_drawer.install_properties_BANG_(klass_22691.prototype);

customElements.define(app.components.x_drawer.model.tag_name,klass_22691);
}

return null;
});
app.components.x_drawer.x_drawer.register_BANG_ = (function app$components$x_drawer$x_drawer$register_BANG_(){
return app.components.x_drawer.x_drawer.define_element_BANG_();
});
app.components.x_drawer.x_drawer.init_BANG_ = (function app$components$x_drawer$x_drawer$init_BANG_(){
return app.components.x_drawer.x_drawer.register_BANG_();
});

//# sourceMappingURL=app.components.x_drawer.x_drawer.js.map
