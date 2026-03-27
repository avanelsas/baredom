goog.provide('app.components.x_table_row.x_table_row');
goog.scope(function(){
  app.components.x_table_row.x_table_row.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_table_row.x_table_row.k_refs = "__xTableRowRefs";
app.components.x_table_row.x_table_row.k_model = "__xTableRowModel";
app.components.x_table_row.x_table_row.k_handlers = "__xTableRowHandlers";
app.components.x_table_row.x_table_row.style_text = (""+":host{"+"display:grid;"+"grid-template-columns:subgrid;"+"grid-column:1 / -1;"+"box-sizing:border-box;"+"color-scheme:light dark;"+"--x-table-row-bg:transparent;"+"--x-table-row-hover-bg:rgba(0,0,0,0.03);"+"--x-table-row-selected-bg:rgba(59,130,246,0.08);"+"--x-table-row-selected-hover-bg:rgba(59,130,246,0.12);"+"--x-table-row-focus-ring:rgba(59,130,246,0.5);"+"--x-table-row-transition-duration:150ms;"+"--x-table-row-disabled-opacity:0.45;"+"--x-table-row-cursor:pointer;"+"background:var(--x-table-row-bg);"+"transition:background var(--x-table-row-transition-duration) ease;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-table-row-hover-bg:rgba(255,255,255,0.04);"+"--x-table-row-selected-bg:rgba(99,160,255,0.12);"+"--x-table-row-selected-hover-bg:rgba(99,160,255,0.16);}}"+":host([data-selected]){"+"background:var(--x-table-row-selected-bg);}"+":host([data-interactive]){"+"cursor:var(--x-table-row-cursor);}"+":host([data-interactive]:hover){"+"background:var(--x-table-row-hover-bg);}"+":host([data-interactive][data-selected]:hover){"+"background:var(--x-table-row-selected-hover-bg);}"+":host([data-interactive]:focus){"+"outline:none;}"+":host([data-interactive]:focus-visible){"+"outline:2px solid var(--x-table-row-focus-ring);"+"outline-offset:-2px;}"+":host([disabled]){"+"opacity:var(--x-table-row-disabled-opacity);"+"pointer-events:none;}"+"slot{display:contents;}"+"@media (prefers-reduced-motion:reduce){"+":host{transition:none !important;}}");
app.components.x_table_row.x_table_row.init_dom_BANG_ = (function app$components$x_table_row$x_table_row$init_dom_BANG_(el){
var root_23238 = el.attachShadow(({"mode": "open"}));
var style_23239 = document.createElement("style");
var slot_el_23240 = document.createElement("slot");
(style_23239.textContent = app.components.x_table_row.x_table_row.style_text);

root_23238.appendChild(style_23239);

root_23238.appendChild(slot_el_23240);

app.components.x_table_row.x_table_row.goog$module$goog$object.set(el,app.components.x_table_row.x_table_row.k_refs,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"root","root",-448657453),root_23238,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_23240], null));

return null;
});
app.components.x_table_row.x_table_row.ensure_refs_BANG_ = (function app$components$x_table_row$x_table_row$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_table_row.x_table_row.init_dom_BANG_(el);

return app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_refs);
}
});
app.components.x_table_row.x_table_row.read_model = (function app$components$x_table_row$x_table_row$read_model(el){
return app.components.x_table_row.model.normalize(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"selected?","selected?",-742502788),el.hasAttribute(app.components.x_table_row.model.attr_selected),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),el.hasAttribute(app.components.x_table_row.model.attr_disabled),new cljs.core.Keyword(null,"interactive?","interactive?",367617676),el.hasAttribute(app.components.x_table_row.model.attr_interactive),new cljs.core.Keyword(null,"row-index-raw","row-index-raw",1490649093),el.getAttribute(app.components.x_table_row.model.attr_row_index)], null));
});
app.components.x_table_row.x_table_row.apply_host_attrs_BANG_ = (function app$components$x_table_row$x_table_row$apply_host_attrs_BANG_(el,p__23183){
var map__23184 = p__23183;
var map__23184__$1 = cljs.core.__destructure_map(map__23184);
var m = map__23184__$1;
var selected_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23184__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23184__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var interactive_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23184__$1,new cljs.core.Keyword(null,"interactive?","interactive?",367617676));
var row_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23184__$1,new cljs.core.Keyword(null,"row-index","row-index",-828710296));
el.setAttribute("role","row");

var aria_sel_23241 = app.components.x_table_row.model.aria_selected_value(m);
if(cljs.core.truth_(aria_sel_23241)){
el.setAttribute("aria-selected",aria_sel_23241);
} else {
el.removeAttribute("aria-selected");
}

if(cljs.core.truth_(disabled_QMARK_)){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

if((!((row_index == null)))){
el.setAttribute("aria-rowindex",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row_index)));
} else {
el.removeAttribute("aria-rowindex");
}

if(cljs.core.truth_(selected_QMARK_)){
el.setAttribute("data-selected","");
} else {
el.removeAttribute("data-selected");
}

if(cljs.core.truth_(interactive_QMARK_)){
el.setAttribute("data-interactive","");
} else {
el.removeAttribute("data-interactive");
}

(el.tabIndex = (cljs.core.truth_(app.components.x_table_row.model.interactive_eligible_QMARK_(m))?(0):(-1)));

return null;
});
app.components.x_table_row.x_table_row.apply_model_BANG_ = (function app$components$x_table_row$x_table_row$apply_model_BANG_(el,m){
app.components.x_table_row.x_table_row.ensure_refs_BANG_(el);

app.components.x_table_row.x_table_row.apply_host_attrs_BANG_(el,m);

app.components.x_table_row.x_table_row.goog$module$goog$object.set(el,app.components.x_table_row.x_table_row.k_model,m);

return null;
});
app.components.x_table_row.x_table_row.update_from_attrs_BANG_ = (function app$components$x_table_row$x_table_row$update_from_attrs_BANG_(el){
var new_m_23243 = app.components.x_table_row.x_table_row.read_model(el);
var old_m_23244 = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23244,new_m_23243)){
app.components.x_table_row.x_table_row.apply_model_BANG_(el,new_m_23243);
} else {
}

return null;
});
app.components.x_table_row.x_table_row.dispatch_click_BANG_ = (function app$components$x_table_row$x_table_row$dispatch_click_BANG_(el){
var m_23246 = (function (){var or__5142__auto__ = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_row.x_table_row.read_model(el);
}
})();
el.dispatchEvent((new CustomEvent(app.components.x_table_row.model.event_click,({"detail": cljs.core.clj__GT_js(app.components.x_table_row.model.click_detail(m_23246)), "bubbles": true, "composed": true, "cancelable": true}))));

return null;
});
app.components.x_table_row.x_table_row.dispatch_connected_BANG_ = (function app$components$x_table_row$x_table_row$dispatch_connected_BANG_(el,m){
el.dispatchEvent((new CustomEvent(app.components.x_table_row.model.event_connected,({"detail": cljs.core.clj__GT_js(app.components.x_table_row.model.connected_detail(m)), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_table_row.x_table_row.dispatch_disconnected_BANG_ = (function app$components$x_table_row$x_table_row$dispatch_disconnected_BANG_(_el){
document.dispatchEvent((new CustomEvent(app.components.x_table_row.model.event_disconnected,({"detail": ({}), "bubbles": false, "composed": false, "cancelable": false}))));

return null;
});
app.components.x_table_row.x_table_row.on_click = (function app$components$x_table_row$x_table_row$on_click(el,_e){
var m_23247 = (function (){var or__5142__auto__ = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_row.x_table_row.read_model(el);
}
})();
if(cljs.core.truth_(app.components.x_table_row.model.interactive_eligible_QMARK_(m_23247))){
app.components.x_table_row.x_table_row.dispatch_click_BANG_(el);
} else {
}

return null;
});
app.components.x_table_row.x_table_row.on_keydown = (function app$components$x_table_row$x_table_row$on_keydown(el,e){
var key_23248 = e.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_23248,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_23248," ")))){
var m_23249 = (function (){var or__5142__auto__ = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_row.x_table_row.read_model(el);
}
})();
if(cljs.core.truth_(app.components.x_table_row.model.interactive_eligible_QMARK_(m_23249))){
e.preventDefault();

app.components.x_table_row.x_table_row.dispatch_click_BANG_(el);
} else {
}
} else {
}

return null;
});
app.components.x_table_row.x_table_row.add_listeners_BANG_ = (function app$components$x_table_row$x_table_row$add_listeners_BANG_(el){
var click_h_23250 = (function (e){
return app.components.x_table_row.x_table_row.on_click(el,e);
});
var keydown_h_23251 = (function (e){
return app.components.x_table_row.x_table_row.on_keydown(el,e);
});
el.addEventListener("click",click_h_23250);

el.addEventListener("keydown",keydown_h_23251);

app.components.x_table_row.x_table_row.goog$module$goog$object.set(el,app.components.x_table_row.x_table_row.k_handlers,({"click": click_h_23250, "keydown": keydown_h_23251}));

return null;
});
app.components.x_table_row.x_table_row.remove_listeners_BANG_ = (function app$components$x_table_row$x_table_row$remove_listeners_BANG_(el){
var hs_23252 = app.components.x_table_row.x_table_row.goog$module$goog$object.get(el,app.components.x_table_row.x_table_row.k_handlers);
if(cljs.core.truth_(hs_23252)){
var click_h_23253 = app.components.x_table_row.x_table_row.goog$module$goog$object.get(hs_23252,"click");
var keydown_h_23254 = app.components.x_table_row.x_table_row.goog$module$goog$object.get(hs_23252,"keydown");
if(cljs.core.truth_(click_h_23253)){
el.removeEventListener("click",click_h_23253);
} else {
}

if(cljs.core.truth_(keydown_h_23254)){
el.removeEventListener("keydown",keydown_h_23254);
} else {
}
} else {
}

app.components.x_table_row.x_table_row.goog$module$goog$object.set(el,app.components.x_table_row.x_table_row.k_handlers,null);

return null;
});
app.components.x_table_row.x_table_row.install_property_accessors_BANG_ = (function app$components$x_table_row$x_table_row$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_table_row.model.attr_selected,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_row.model.attr_selected);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_row.model.attr_selected,"");
} else {
return this$.removeAttribute(app.components.x_table_row.model.attr_selected);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_row.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_row.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_row.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_table_row.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_row.model.attr_interactive,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_row.model.attr_interactive);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_row.model.attr_interactive,"");
} else {
return this$.removeAttribute(app.components.x_table_row.model.attr_interactive);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"rowIndex",({"get": (function (){
var this$ = this;
return app.components.x_table_row.model.parse_row_index(this$.getAttribute(app.components.x_table_row.model.attr_row_index));
}), "set": (function (v){
var this$ = this;
var n = parseInt(v,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return this$.setAttribute(app.components.x_table_row.model.attr_row_index,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));
} else {
return this$.removeAttribute(app.components.x_table_row.model.attr_row_index);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_table_row.x_table_row.element_class = (function app$components$x_table_row$x_table_row$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_table_row.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_table_row.x_table_row.ensure_refs_BANG_(this$);

app.components.x_table_row.x_table_row.remove_listeners_BANG_(this$);

app.components.x_table_row.x_table_row.add_listeners_BANG_(this$);

app.components.x_table_row.x_table_row.update_from_attrs_BANG_(this$);

var m_23255 = (function (){var or__5142__auto__ = app.components.x_table_row.x_table_row.goog$module$goog$object.get(this$,app.components.x_table_row.x_table_row.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_row.x_table_row.read_model(this$);
}
})();
app.components.x_table_row.x_table_row.dispatch_connected_BANG_(this$,m_23255);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_table_row.x_table_row.remove_listeners_BANG_(this$);

app.components.x_table_row.x_table_row.dispatch_disconnected_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_table_row.x_table_row.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_table_row.x_table_row.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_table_row.x_table_row.register_BANG_ = (function app$components$x_table_row$x_table_row$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_table_row.model.tag_name))){
} else {
customElements.define(app.components.x_table_row.model.tag_name,app.components.x_table_row.x_table_row.element_class());
}

return null;
});
app.components.x_table_row.x_table_row.init_BANG_ = (function app$components$x_table_row$x_table_row$init_BANG_(){
return app.components.x_table_row.x_table_row.register_BANG_();
});

//# sourceMappingURL=app.components.x_table_row.x_table_row.js.map
