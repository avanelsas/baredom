goog.provide('app.components.x_spinner.x_spinner');
goog.scope(function(){
  app.components.x_spinner.x_spinner.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_spinner.x_spinner.k_initialized = "__xSpinnerInit";
app.components.x_spinner.x_spinner.k_ring = "__xSpinnerRing";
app.components.x_spinner.x_spinner.style_text = (""+":host{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"color-scheme:light dark;"+"--x-spinner-size:24px;"+"--x-spinner-color:currentColor;"+"--x-spinner-track-color:rgba(0,0,0,0.12);"+"--x-spinner-thickness:2px;"+"--x-spinner-duration:0.75s;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-spinner-track-color:rgba(255,255,255,0.15);}}"+":host([data-size='xs']){--x-spinner-size:16px;}"+":host([data-size='sm']){--x-spinner-size:20px;}"+":host([data-size='md']){--x-spinner-size:24px;}"+":host([data-size='lg']){--x-spinner-size:32px;}"+":host([data-size='xl']){--x-spinner-size:48px;}"+":host([data-variant='primary']){--x-spinner-color:#3b82f6;}"+":host([data-variant='success']){--x-spinner-color:#22c55e;}"+":host([data-variant='warning']){--x-spinner-color:#f59e0b;}"+":host([data-variant='danger']){--x-spinner-color:#ef4444;}"+"[part=ring]{"+"display:block;"+"width:var(--x-spinner-size);"+"height:var(--x-spinner-size);"+"border-radius:50%;"+"border:var(--x-spinner-thickness) solid var(--x-spinner-track-color);"+"border-top-color:var(--x-spinner-color);"+"box-sizing:border-box;"+"animation:x-spinner-spin var(--x-spinner-duration) linear infinite;}"+"@keyframes x-spinner-spin{"+"to{transform:rotate(360deg);}}"+"@media (prefers-reduced-motion:reduce){"+"[part=ring]{animation-play-state:paused;}}");
app.components.x_spinner.x_spinner.init_dom_BANG_ = (function app$components$x_spinner$x_spinner$init_dom_BANG_(el){
var root_23114 = el.attachShadow(({"mode": "open"}));
var style_23115 = document.createElement("style");
var ring_23116 = document.createElement("span");
(style_23115.textContent = app.components.x_spinner.x_spinner.style_text);

ring_23116.setAttribute("part","ring");

ring_23116.setAttribute("aria-hidden","true");

root_23114.appendChild(style_23115);

root_23114.appendChild(ring_23116);

el.setAttribute("role","status");

el.setAttribute("aria-live","polite");

app.components.x_spinner.x_spinner.goog$module$goog$object.set(el,app.components.x_spinner.x_spinner.k_ring,ring_23116);

app.components.x_spinner.x_spinner.goog$module$goog$object.set(el,app.components.x_spinner.x_spinner.k_initialized,true);

return null;
});
app.components.x_spinner.x_spinner.read_attrs = (function app$components$x_spinner$x_spinner$read_attrs(el){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),el.getAttribute(app.components.x_spinner.model.attr_size),new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_spinner.model.attr_variant),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_spinner.model.attr_label)], null);
});
app.components.x_spinner.x_spinner.render_BANG_ = (function app$components$x_spinner$x_spinner$render_BANG_(el){
var map__23113_23117 = app.components.x_spinner.model.derive_state(app.components.x_spinner.x_spinner.read_attrs(el));
var map__23113_23118__$1 = cljs.core.__destructure_map(map__23113_23117);
var size_23119 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23113_23118__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var variant_23120 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23113_23118__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var label_23121 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23113_23118__$1,new cljs.core.Keyword(null,"label","label",1718410804));
el.setAttribute("data-size",size_23119);

el.setAttribute("data-variant",variant_23120);

el.setAttribute("aria-label",label_23121);

return null;
});
app.components.x_spinner.x_spinner.connected_BANG_ = (function app$components$x_spinner$x_spinner$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_spinner.x_spinner.goog$module$goog$object.get(el,app.components.x_spinner.x_spinner.k_initialized))){
} else {
app.components.x_spinner.x_spinner.init_dom_BANG_(el);
}

app.components.x_spinner.x_spinner.render_BANG_(el);

return null;
});
app.components.x_spinner.x_spinner.attribute_changed_BANG_ = (function app$components$x_spinner$x_spinner$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val);
if(and__5140__auto__){
return app.components.x_spinner.x_spinner.goog$module$goog$object.get(el,app.components.x_spinner.x_spinner.k_initialized);
} else {
return and__5140__auto__;
}
})())){
app.components.x_spinner.x_spinner.render_BANG_(el);
} else {
}

return null;
});
app.components.x_spinner.x_spinner.install_property_accessors_BANG_ = (function app$components$x_spinner$x_spinner$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_spinner.model.attr_size,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_spinner.model.attr_size);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_spinner.model.default_size;
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_spinner.model.attr_size,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_spinner.model.attr_size);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_spinner.model.attr_variant,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_spinner.model.attr_variant);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_spinner.model.default_variant;
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_spinner.model.attr_variant,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_spinner.model.attr_variant);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,app.components.x_spinner.model.attr_label,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_spinner.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_spinner.model.default_label;
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_spinner.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_spinner.model.attr_label);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_spinner.x_spinner.element_class = (function app$components$x_spinner$x_spinner$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_spinner.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_spinner.x_spinner.connected_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
app.components.x_spinner.x_spinner.attribute_changed_BANG_(this$,n,o,v);

return null;
}));

app.components.x_spinner.x_spinner.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_spinner.x_spinner.register_BANG_ = (function app$components$x_spinner$x_spinner$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_spinner.model.tag_name))){
} else {
customElements.define(app.components.x_spinner.model.tag_name,app.components.x_spinner.x_spinner.element_class());
}

return null;
});
app.components.x_spinner.x_spinner.init_BANG_ = (function app$components$x_spinner$x_spinner$init_BANG_(){
return app.components.x_spinner.x_spinner.register_BANG_();
});

//# sourceMappingURL=app.components.x_spinner.x_spinner.js.map
