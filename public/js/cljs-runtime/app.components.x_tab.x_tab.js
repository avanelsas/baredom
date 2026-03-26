goog.provide('app.components.x_tab.x_tab');
goog.scope(function(){
  app.components.x_tab.x_tab.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_tab.x_tab.key_root = "__x_tab_root";
app.components.x_tab.x_tab.key_base = "__x_tab_base";
app.components.x_tab.x_tab.key_initialized = "__x_tab_initialized";
app.components.x_tab.x_tab.getv = (function app$components$x_tab$x_tab$getv(el,k){
return app.components.x_tab.x_tab.goog$module$goog$object.get(el,k);
});
app.components.x_tab.x_tab.setv_BANG_ = (function app$components$x_tab$x_tab$setv_BANG_(el,k,v){
return app.components.x_tab.x_tab.goog$module$goog$object.set(el,k,v);
});
app.components.x_tab.x_tab.initialized_QMARK_ = (function app$components$x_tab$x_tab$initialized_QMARK_(el){
return app.components.x_tab.x_tab.getv(el,app.components.x_tab.x_tab.key_initialized) === true;
});
app.components.x_tab.x_tab.mark_initialized_BANG_ = (function app$components$x_tab$x_tab$mark_initialized_BANG_(el){
return app.components.x_tab.x_tab.setv_BANG_(el,app.components.x_tab.x_tab.key_initialized,true);
});
app.components.x_tab.x_tab.read_inputs = (function app$components$x_tab$x_tab$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"value","value",305978217),el.getAttribute(app.components.x_tab.model.attr_value),new cljs.core.Keyword(null,"selected","selected",574897764),el.hasAttribute(app.components.x_tab.model.attr_selected),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),el.hasAttribute(app.components.x_tab.model.attr_disabled),new cljs.core.Keyword(null,"orientation","orientation",623557579),el.getAttribute(app.components.x_tab.model.attr_orientation),new cljs.core.Keyword(null,"size","size",1098693007),el.getAttribute(app.components.x_tab.model.attr_size),new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_tab.model.attr_variant),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_tab.model.attr_label),new cljs.core.Keyword(null,"controls","controls",1340701452),el.getAttribute(app.components.x_tab.model.attr_controls)], null);
});
app.components.x_tab.x_tab.style_text = (""+":host{"+"display:inline-block;color-scheme:light dark;outline:none;"+"--x-tab-color:#0f172a;"+"--x-tab-hover-background:rgba(15,23,42,0.05);"+"--x-tab-selected-color:#1d4ed8;"+"--x-tab-selected-background:rgba(59,130,246,0.12);"+"--x-tab-selected-border-color:transparent;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-tab-color:#94a3b8;"+"--x-tab-hover-background:rgba(248,250,252,0.06);"+"--x-tab-selected-color:#60a5fa;"+"--x-tab-selected-background:rgba(59,130,246,0.18);"+"--x-tab-selected-border-color:#60a5fa;}}"+".base{box-sizing:border-box;display:inline-flex;align-items:center;justify-content:center;"+"cursor:pointer;user-select:none;"+"padding:var(--x-tab-padding-md,10px 16px);border-radius:var(--x-tab-radius,8px);"+"color:var(--x-tab-color);background:var(--x-tab-background,transparent);"+"border:1px solid var(--x-tab-border-color,transparent);"+"transition:background var(--x-tab-transition-duration,120ms) ease,"+"color var(--x-tab-transition-duration,120ms) ease,"+"border-color var(--x-tab-transition-duration,120ms) ease;}"+".base[data-size='sm']{padding:var(--x-tab-padding-sm,6px 12px);}"+".base[data-size='md']{padding:var(--x-tab-padding-md,10px 16px);}"+".base[data-size='lg']{padding:var(--x-tab-padding-lg,14px 20px);}"+".base:hover{background:var(--x-tab-hover-background);}"+".base[data-selected='true']{color:var(--x-tab-selected-color);"+"background:var(--x-tab-selected-background);"+"border-color:var(--x-tab-selected-border-color);}"+".base[data-variant='underline'][data-selected='true']{border-bottom:2px solid var(--x-tab-selected-color);background:transparent;}"+".base[data-variant='pill'][data-selected='true']{background:var(--x-tab-selected-background);}"+".base[data-disabled='true']{opacity:var(--x-tab-disabled-opacity,0.5);cursor:default;}"+":host(:focus-visible) .base{box-shadow:0 0 0 3px var(--x-tab-focus-ring,rgba(59,130,246,0.4));}"+"@media (prefers-reduced-motion: reduce){.base{transition:none;}}");
app.components.x_tab.x_tab.apply_host_a11y_BANG_ = (function app$components$x_tab$x_tab$apply_host_a11y_BANG_(el,state){
el.setAttribute("role","tab");

el.setAttribute("aria-selected",(cljs.core.truth_(new cljs.core.Keyword(null,"selected","selected",574897764).cljs$core$IFn$_invoke$arity$1(state))?"true":"false"));

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

el.setAttribute("tabindex",new cljs.core.Keyword(null,"tabindex","tabindex",338877510).cljs$core$IFn$_invoke$arity$1(state));

var controls = new cljs.core.Keyword(null,"controls","controls",1340701452).cljs$core$IFn$_invoke$arity$1(state);
var label = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_((function (){var and__5140__auto__ = controls;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(controls,"");
} else {
return and__5140__auto__;
}
})())){
el.setAttribute("aria-controls",controls);
} else {
el.removeAttribute("aria-controls");
}

if(cljs.core.truth_((function (){var and__5140__auto__ = label;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,"");
} else {
return and__5140__auto__;
}
})())){
return el.setAttribute("aria-label",label);
} else {
return el.removeAttribute("aria-label");
}
});
app.components.x_tab.x_tab.apply_state_BANG_ = (function app$components$x_tab$x_tab$apply_state_BANG_(base,state){
base.setAttribute("data-selected",(cljs.core.truth_(new cljs.core.Keyword(null,"selected","selected",574897764).cljs$core$IFn$_invoke$arity$1(state))?"true":"false"));

base.setAttribute("data-disabled",(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(state))?"true":"false"));

base.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(state));

base.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(state));

return base.setAttribute("data-orientation",new cljs.core.Keyword(null,"orientation","orientation",623557579).cljs$core$IFn$_invoke$arity$1(state));
});
app.components.x_tab.x_tab.render_BANG_ = (function app$components$x_tab$x_tab$render_BANG_(el){
var state = app.components.x_tab.model.derive_state(app.components.x_tab.x_tab.read_inputs(el));
var base = app.components.x_tab.x_tab.getv(el,app.components.x_tab.x_tab.key_base);
if(cljs.core.truth_(base)){
app.components.x_tab.x_tab.apply_host_a11y_BANG_(el,state);

return app.components.x_tab.x_tab.apply_state_BANG_(base,state);
} else {
return null;
}
});
app.components.x_tab.x_tab.dispatch_select_BANG_ = (function app$components$x_tab$x_tab$dispatch_select_BANG_(el){
var value = el.getAttribute(app.components.x_tab.model.attr_value);
return el.dispatchEvent((new CustomEvent(app.components.x_tab.model.event_tab_select,({"detail": ({"value": (function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()}), "bubbles": true, "composed": true}))));
});
app.components.x_tab.x_tab.activate_BANG_ = (function app$components$x_tab$x_tab$activate_BANG_(el){
var map__21315 = app.components.x_tab.model.derive_state(app.components.x_tab.x_tab.read_inputs(el));
var map__21315__$1 = cljs.core.__destructure_map(map__21315);
var selected = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21315__$1,new cljs.core.Keyword(null,"selected","selected",574897764));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21315__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
if(((cljs.core.not(disabled)) && (cljs.core.not(selected)))){
return app.components.x_tab.x_tab.dispatch_select_BANG_(el);
} else {
return null;
}
});
app.components.x_tab.x_tab.on_click = (function app$components$x_tab$x_tab$on_click(el,_){
return app.components.x_tab.x_tab.activate_BANG_(el);
});
app.components.x_tab.x_tab.on_keydown = (function app$components$x_tab$x_tab$on_keydown(el,e){
var k = e.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k," ")))){
e.preventDefault();

return app.components.x_tab.x_tab.activate_BANG_(el);
} else {
return null;
}
});
app.components.x_tab.x_tab.install_listeners_BANG_ = (function app$components$x_tab$x_tab$install_listeners_BANG_(el){
el.addEventListener("click",(function (e){
return app.components.x_tab.x_tab.on_click(el,e);
}));

return el.addEventListener("keydown",(function (e){
return app.components.x_tab.x_tab.on_keydown(el,e);
}));
});
app.components.x_tab.x_tab.init_dom_BANG_ = (function app$components$x_tab$x_tab$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var slot = document.createElement("slot");
(style.textContent = app.components.x_tab.x_tab.style_text);

base.setAttribute("part","base");

(base.className = "base");

base.appendChild(slot);

root.appendChild(style);

root.appendChild(base);

app.components.x_tab.x_tab.setv_BANG_(el,app.components.x_tab.x_tab.key_root,root);

return app.components.x_tab.x_tab.setv_BANG_(el,app.components.x_tab.x_tab.key_base,base);
});
app.components.x_tab.x_tab.init_element_BANG_ = (function app$components$x_tab$x_tab$init_element_BANG_(el){
if(app.components.x_tab.x_tab.initialized_QMARK_(el)){
} else {
app.components.x_tab.x_tab.init_dom_BANG_(el);

app.components.x_tab.x_tab.install_listeners_BANG_(el);

app.components.x_tab.x_tab.mark_initialized_BANG_(el);
}

app.components.x_tab.x_tab.render_BANG_(el);

return el;
});
app.components.x_tab.x_tab.connected_callback = (function app$components$x_tab$x_tab$connected_callback(el){
return app.components.x_tab.x_tab.init_element_BANG_(el);
});
app.components.x_tab.x_tab.attribute_changed_callback = (function app$components$x_tab$x_tab$attribute_changed_callback(el,_,___$1,___$2){
if(app.components.x_tab.x_tab.initialized_QMARK_(el)){
return app.components.x_tab.x_tab.render_BANG_(el);
} else {
return app.components.x_tab.x_tab.init_element_BANG_(el);
}
});
app.components.x_tab.x_tab.install_property_accessors_BANG_ = (function app$components$x_tab$x_tab$install_property_accessors_BANG_(klass){
Object.defineProperty(klass.prototype,"selected",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_tab.model.attr_selected);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_tab.model.attr_selected,"");
} else {
return this$.removeAttribute(app.components.x_tab.model.attr_selected);
}
}), "configurable": true, "enumerable": true}));

Object.defineProperty(klass.prototype,"disabled",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_tab.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_tab.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_tab.model.attr_disabled);
}
}), "configurable": true, "enumerable": true}));

return Object.defineProperty(klass.prototype,"value",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_tab.model.attr_value);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_tab.model.attr_value,v);
} else {
return this$.removeAttribute(app.components.x_tab.model.attr_value);
}
}), "configurable": true, "enumerable": true}));
});
app.components.x_tab.x_tab.element_class = (function app$components$x_tab$x_tab$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_tab.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_tab.x_tab.connected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_tab.x_tab.attribute_changed_callback(this$,name,old_value,new_value);
}));

app.components.x_tab.x_tab.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_tab.x_tab.register_BANG_ = (function app$components$x_tab$x_tab$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_tab.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_tab.model.tag_name,app.components.x_tab.x_tab.element_class());
}
});
app.components.x_tab.x_tab.init_BANG_ = (function app$components$x_tab$x_tab$init_BANG_(){
return app.components.x_tab.x_tab.register_BANG_();
});

//# sourceMappingURL=app.components.x_tab.x_tab.js.map
