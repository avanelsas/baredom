goog.provide('app.components.x_chip.x_chip');
goog.scope(function(){
  app.components.x_chip.x_chip.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_chip.x_chip.k_refs = "__xChipRefs";
app.components.x_chip.x_chip.k_handlers = "__xChipHandlers";
app.components.x_chip.x_chip.style_text = (""+":host{"+"display:inline-flex;"+"align-items:center;"+"vertical-align:middle;"+"color-scheme:light dark;"+"font-family:inherit;"+"font-size:0.875rem;"+"line-height:1;"+"border-radius:999px;"+"background:rgba(0,0,0,0.08);"+"border:1px solid rgba(0,0,0,0.12);"+"color:rgba(0,0,0,0.80);"+"padding:0.25rem 0.625rem;"+"gap:0.25rem;"+"box-sizing:border-box;"+"cursor:default;"+"user-select:none;"+"outline:none;}"+"@media (prefers-color-scheme:dark){"+":host{"+"background:rgba(255,255,255,0.10);"+"border-color:rgba(255,255,255,0.16);"+"color:rgba(255,255,255,0.88);}}"+":host(:focus-visible){"+"outline:2px solid currentColor;"+"outline-offset:2px;}"+":host([disabled]){"+"opacity:0.55;"+"pointer-events:none;}"+"[part=container]{"+"display:inline-flex;"+"align-items:center;"+"gap:0.25rem;}"+"[part=label]{"+"display:inline;"+"font-size:inherit;"+"color:inherit;}"+"[part=remove]{"+"display:none;"+"align-items:center;"+"justify-content:center;"+"width:1rem;"+"height:1rem;"+"border:none;"+"background:none;"+"color:inherit;"+"cursor:pointer;"+"padding:0;"+"border-radius:50%;"+"font-size:1rem;"+"line-height:1;"+"opacity:0.7;"+"flex-shrink:0;}"+"[part=remove]:hover{opacity:1;}"+"[part=remove]:focus-visible{"+"outline:2px solid currentColor;"+"outline-offset:1px;}"+":host([data-removable]) [part=remove]{"+"display:inline-flex;}"+"@keyframes x-chip-exit{"+"to{opacity:0;transform:scale(0.85);}}"+":host([data-exiting]){"+"animation:x-chip-exit 300ms forwards;}"+"@media (prefers-reduced-motion:reduce){"+":host([data-exiting]){"+"animation-duration:0ms;}}");
app.components.x_chip.x_chip.make_el = (function app$components$x_chip$x_chip$make_el(tag){
return document.createElement(tag);
});
app.components.x_chip.x_chip.set_attr_BANG_ = (function app$components$x_chip$x_chip$set_attr_BANG_(el,k,v){
return el.setAttribute(k,v);
});
app.components.x_chip.x_chip.make_shadow_BANG_ = (function app$components$x_chip$x_chip$make_shadow_BANG_(el){
var root_22167 = el.attachShadow(({"mode": "open"}));
var style_22168 = app.components.x_chip.x_chip.make_el("style");
var container_22169 = app.components.x_chip.x_chip.make_el("span");
var label_el_22170 = app.components.x_chip.x_chip.make_el("span");
var remove_btn_22171 = app.components.x_chip.x_chip.make_el("button");
var remove_x_22172 = app.components.x_chip.x_chip.make_el("span");
(style_22168.textContent = app.components.x_chip.x_chip.style_text);

app.components.x_chip.x_chip.set_attr_BANG_(container_22169,"part","container");

app.components.x_chip.x_chip.set_attr_BANG_(label_el_22170,"part","label");

app.components.x_chip.x_chip.set_attr_BANG_(remove_btn_22171,"part","remove");

app.components.x_chip.x_chip.set_attr_BANG_(remove_btn_22171,"type","button");

app.components.x_chip.x_chip.set_attr_BANG_(remove_btn_22171,"aria-label","Remove");

app.components.x_chip.x_chip.set_attr_BANG_(remove_x_22172,"aria-hidden","true");

(remove_x_22172.textContent = "\u00D7");

remove_btn_22171.appendChild(remove_x_22172);

container_22169.appendChild(label_el_22170);

container_22169.appendChild(remove_btn_22171);

root_22167.appendChild(style_22168);

root_22167.appendChild(container_22169);

app.components.x_chip.x_chip.goog$module$goog$object.set(el,app.components.x_chip.x_chip.k_refs,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"root","root",-448657453),root_22167,new cljs.core.Keyword(null,"container","container",-1736937707),container_22169,new cljs.core.Keyword(null,"label-el","label-el",-356062007),label_el_22170,new cljs.core.Keyword(null,"remove-btn","remove-btn",843430290),remove_btn_22171], null));

return null;
});
app.components.x_chip.x_chip.dispatch_remove_BANG_ = (function app$components$x_chip$x_chip$dispatch_remove_BANG_(el,m){

var detail = app.components.x_chip.model.remove_detail(m);
var ev = (new CustomEvent(app.components.x_chip.model.event_remove,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_chip.x_chip.do_exit_BANG_ = (function app$components$x_chip$x_chip$do_exit_BANG_(el){

var timeout_id = setTimeout((function (){
return el.remove();
}),(400));
var on_end = (function app$components$x_chip$x_chip$do_exit_BANG__$_on_end(){
clearTimeout(timeout_id);

el.removeEventListener("animationend",app$components$x_chip$x_chip$do_exit_BANG__$_on_end);

return el.remove();
});
el.addEventListener("animationend",on_end);

return el.setAttribute("data-exiting","");
});
app.components.x_chip.x_chip.try_remove_BANG_ = (function app$components$x_chip$x_chip$try_remove_BANG_(el){

var m = app.components.x_chip.model.normalize(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_chip.model.attr_label),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),el.getAttribute(app.components.x_chip.model.attr_value),new cljs.core.Keyword(null,"removable-raw","removable-raw",1718453796),el.getAttribute(app.components.x_chip.model.attr_removable),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_chip.model.attr_disabled)], null));
if(cljs.core.truth_(app.components.x_chip.model.removal_eligible_QMARK_(m))){
if(app.components.x_chip.x_chip.dispatch_remove_BANG_(el,m)){
return app.components.x_chip.x_chip.do_exit_BANG_(el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_chip.x_chip.render_BANG_ = (function app$components$x_chip$x_chip$render_BANG_(el){
var m_22173 = app.components.x_chip.model.normalize(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_chip.model.attr_label),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),el.getAttribute(app.components.x_chip.model.attr_value),new cljs.core.Keyword(null,"removable-raw","removable-raw",1718453796),el.getAttribute(app.components.x_chip.model.attr_removable),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_chip.model.attr_disabled)], null));
var map__22143_22174 = m_22173;
var map__22143_22175__$1 = cljs.core.__destructure_map(map__22143_22174);
var label_22176 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22143_22175__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var removable_QMARK__22177 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22143_22175__$1,new cljs.core.Keyword(null,"removable?","removable?",1886800649));
var disabled_QMARK__22178 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22143_22175__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var refs_22179 = app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_refs);
var label_el_22180 = new cljs.core.Keyword(null,"label-el","label-el",-356062007).cljs$core$IFn$_invoke$arity$1(refs_22179);
var remove_btn_22181 = new cljs.core.Keyword(null,"remove-btn","remove-btn",843430290).cljs$core$IFn$_invoke$arity$1(refs_22179);
var eligible_22182 = app.components.x_chip.model.removal_eligible_QMARK_(m_22173);
if(cljs.core.truth_(label_el_22180)){
(label_el_22180.textContent = label_22176);
} else {
}

if(cljs.core.truth_(removable_QMARK__22177)){
el.setAttribute("data-removable","");
} else {
el.removeAttribute("data-removable");
}

(el.tabIndex = (cljs.core.truth_(eligible_22182)?(0):(-1)));

if(cljs.core.truth_(eligible_22182)){
el.setAttribute("aria-keyshortcuts","Backspace Delete");
} else {
el.removeAttribute("aria-keyshortcuts");
}

if(cljs.core.truth_(remove_btn_22181)){
if(cljs.core.truth_(disabled_QMARK__22178)){
remove_btn_22181.setAttribute("disabled","");
} else {
remove_btn_22181.removeAttribute("disabled");
}
} else {
}

return null;
});
app.components.x_chip.x_chip.add_listeners_BANG_ = (function app$components$x_chip$x_chip$add_listeners_BANG_(el){
var refs_22183 = app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_refs);
var remove_btn_22184 = new cljs.core.Keyword(null,"remove-btn","remove-btn",843430290).cljs$core$IFn$_invoke$arity$1(refs_22183);
var on_click_22185 = (function (_ev){
return app.components.x_chip.x_chip.try_remove_BANG_(el);
});
var on_keydown_22186 = (function (ev){
var key = ev.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Backspace")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Delete")))){
ev.preventDefault();

return app.components.x_chip.x_chip.try_remove_BANG_(el);
} else {
return null;
}
});
if(cljs.core.truth_(remove_btn_22184)){
remove_btn_22184.addEventListener("click",on_click_22185);
} else {
}

el.addEventListener("keydown",on_keydown_22186);

app.components.x_chip.x_chip.goog$module$goog$object.set(el,app.components.x_chip.x_chip.k_handlers,({"click": on_click_22185, "keydown": on_keydown_22186}));

return null;
});
app.components.x_chip.x_chip.remove_listeners_BANG_ = (function app$components$x_chip$x_chip$remove_listeners_BANG_(el){
var temp__5823__auto___22187 = app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_handlers);
if(cljs.core.truth_(temp__5823__auto___22187)){
var hs_22188 = temp__5823__auto___22187;
var refs_22189 = app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_refs);
var on_click_22190 = app.components.x_chip.x_chip.goog$module$goog$object.get(hs_22188,"click");
var on_keydown_22191 = app.components.x_chip.x_chip.goog$module$goog$object.get(hs_22188,"keydown");
if(cljs.core.truth_((function (){var and__5140__auto__ = refs_22189;
if(cljs.core.truth_(and__5140__auto__)){
return on_click_22190;
} else {
return and__5140__auto__;
}
})())){
var remove_btn_22192 = new cljs.core.Keyword(null,"remove-btn","remove-btn",843430290).cljs$core$IFn$_invoke$arity$1(refs_22189);
if(cljs.core.truth_(remove_btn_22192)){
remove_btn_22192.removeEventListener("click",on_click_22190);
} else {
}
} else {
}

if(cljs.core.truth_(on_keydown_22191)){
el.removeEventListener("keydown",on_keydown_22191);
} else {
}
} else {
}

app.components.x_chip.x_chip.goog$module$goog$object.set(el,app.components.x_chip.x_chip.k_handlers,null);

return null;
});
app.components.x_chip.x_chip.connected_BANG_ = (function app$components$x_chip$x_chip$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_refs))){
} else {
app.components.x_chip.x_chip.make_shadow_BANG_(el);
}

app.components.x_chip.x_chip.remove_listeners_BANG_(el);

app.components.x_chip.x_chip.add_listeners_BANG_(el);

return app.components.x_chip.x_chip.render_BANG_(el);
});
app.components.x_chip.x_chip.disconnected_BANG_ = (function app$components$x_chip$x_chip$disconnected_BANG_(el){
return app.components.x_chip.x_chip.remove_listeners_BANG_(el);
});
app.components.x_chip.x_chip.attribute_changed_BANG_ = (function app$components$x_chip$x_chip$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_(app.components.x_chip.x_chip.goog$module$goog$object.get(el,app.components.x_chip.x_chip.k_refs))){
return app.components.x_chip.x_chip.render_BANG_(el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_chip.x_chip.def_string_prop_BANG_ = (function app$components$x_chip$x_chip$def_string_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.getAttribute(attr);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_chip.x_chip.def_bool_prop_BANG_ = (function app$components$x_chip$x_chip$def_bool_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.hasAttribute(attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,"");
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_chip.x_chip.install_property_accessors_BANG_ = (function app$components$x_chip$x_chip$install_property_accessors_BANG_(proto){
app.components.x_chip.x_chip.def_string_prop_BANG_(proto,app.components.x_chip.model.attr_label);

app.components.x_chip.x_chip.def_string_prop_BANG_(proto,app.components.x_chip.model.attr_value);

app.components.x_chip.x_chip.def_bool_prop_BANG_(proto,app.components.x_chip.model.attr_removable);

return app.components.x_chip.x_chip.def_bool_prop_BANG_(proto,app.components.x_chip.model.attr_disabled);
});
app.components.x_chip.x_chip.element_class = (function app$components$x_chip$x_chip$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_chip.model.observed_attributes;
})}));

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_chip.x_chip.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_chip.x_chip.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_chip.x_chip.attribute_changed_BANG_(this$,n,o,v);
}));

app.components.x_chip.x_chip.install_property_accessors_BANG_(proto);

return cls;
});
app.components.x_chip.x_chip.init_BANG_ = (function app$components$x_chip$x_chip$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_chip.model.tag_name))){
} else {
customElements.define(app.components.x_chip.model.tag_name,app.components.x_chip.x_chip.element_class());
}

return null;
});

//# sourceMappingURL=app.components.x_chip.x_chip.js.map
