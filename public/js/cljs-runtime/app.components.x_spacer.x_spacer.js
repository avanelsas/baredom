goog.provide('app.components.x_spacer.x_spacer');
goog.scope(function(){
  app.components.x_spacer.x_spacer.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_spacer.x_spacer.k_refs = "__xSpacerRefs";
app.components.x_spacer.x_spacer.style_text = (""+":host{"+"display:block;"+"flex-shrink:0;"+"height:var(--x-spacer-size,1rem);"+"width:0;"+"pointer-events:none;}"+":host([data-axis='horizontal']){"+"height:0;"+"width:var(--x-spacer-size,1rem);}"+":host([data-grow='true']){"+"flex:1 0 var(--x-spacer-size,0px);"+"height:0;"+"width:0;}");
app.components.x_spacer.x_spacer.make_el = (function app$components$x_spacer$x_spacer$make_el(tag){
return document.createElement(tag);
});
app.components.x_spacer.x_spacer.make_shadow_BANG_ = (function app$components$x_spacer$x_spacer$make_shadow_BANG_(el){
var root_23096 = el.attachShadow(({"mode": "open"}));
var style_23097 = app.components.x_spacer.x_spacer.make_el("style");
(style_23097.textContent = app.components.x_spacer.x_spacer.style_text);

root_23096.appendChild(style_23097);

app.components.x_spacer.x_spacer.goog$module$goog$object.set(el,app.components.x_spacer.x_spacer.k_refs,({"root": root_23096}));

return null;
});
app.components.x_spacer.x_spacer.render_BANG_ = (function app$components$x_spacer$x_spacer$render_BANG_(el){
var map__23095_23098 = app.components.x_spacer.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),el.getAttribute(app.components.x_spacer.model.attr_size),new cljs.core.Keyword(null,"axis-raw","axis-raw",291213231),el.getAttribute(app.components.x_spacer.model.attr_axis),new cljs.core.Keyword(null,"grow-raw","grow-raw",441172903),el.getAttribute(app.components.x_spacer.model.attr_grow)], null));
var map__23095_23099__$1 = cljs.core.__destructure_map(map__23095_23098);
var size_23100 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23095_23099__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var axis_23101 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23095_23099__$1,new cljs.core.Keyword(null,"axis","axis",-1215390822));
var grow_QMARK__23102 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23095_23099__$1,new cljs.core.Keyword(null,"grow?","grow?",2124334580));
var style_23103 = el.style;
el.setAttribute("data-axis",axis_23101);

el.setAttribute("data-grow",(cljs.core.truth_(grow_QMARK__23102)?"true":"false"));

style_23103.setProperty("--x-spacer-size",size_23100);

el.setAttribute("role","none");

el.setAttribute("aria-hidden","true");

return null;
});
app.components.x_spacer.x_spacer.connected_BANG_ = (function app$components$x_spacer$x_spacer$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_spacer.x_spacer.goog$module$goog$object.get(el,app.components.x_spacer.x_spacer.k_refs))){
} else {
app.components.x_spacer.x_spacer.make_shadow_BANG_(el);
}

return app.components.x_spacer.x_spacer.render_BANG_(el);
});
app.components.x_spacer.x_spacer.disconnected_BANG_ = (function app$components$x_spacer$x_spacer$disconnected_BANG_(_el){
return null;
});
app.components.x_spacer.x_spacer.attribute_changed_BANG_ = (function app$components$x_spacer$x_spacer$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val);
if(and__5140__auto__){
return app.components.x_spacer.x_spacer.goog$module$goog$object.get(el,app.components.x_spacer.x_spacer.k_refs);
} else {
return and__5140__auto__;
}
})())){
return app.components.x_spacer.x_spacer.render_BANG_(el);
} else {
return null;
}
});
app.components.x_spacer.x_spacer.def_string_prop_BANG_ = (function app$components$x_spacer$x_spacer$def_string_prop_BANG_(proto,attr){
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
app.components.x_spacer.x_spacer.def_string_prop_default_BANG_ = (function app$components$x_spacer$x_spacer$def_string_prop_default_BANG_(proto,attr,default_val){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(attr);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default_val;
}
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_spacer.x_spacer.def_bool_presence_prop_BANG_ = (function app$components$x_spacer$x_spacer$def_bool_presence_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return app.components.x_spacer.model.parse_grow(this$.getAttribute(attr));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,"");
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_spacer.x_spacer.install_property_accessors_BANG_ = (function app$components$x_spacer$x_spacer$install_property_accessors_BANG_(proto){
app.components.x_spacer.x_spacer.def_string_prop_default_BANG_(proto,app.components.x_spacer.model.attr_size,app.components.x_spacer.model.default_size);

app.components.x_spacer.x_spacer.def_string_prop_default_BANG_(proto,app.components.x_spacer.model.attr_axis,app.components.x_spacer.model.default_axis);

return app.components.x_spacer.x_spacer.def_bool_presence_prop_BANG_(proto,app.components.x_spacer.model.attr_grow);
});
app.components.x_spacer.x_spacer.element_class = (function app$components$x_spacer$x_spacer$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_spacer.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_spacer.x_spacer.connected_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_spacer.x_spacer.disconnected_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
app.components.x_spacer.x_spacer.attribute_changed_BANG_(this$,n,o,v);

return null;
}));

app.components.x_spacer.x_spacer.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_spacer.x_spacer.register_BANG_ = (function app$components$x_spacer$x_spacer$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_spacer.model.tag_name))){
} else {
customElements.define(app.components.x_spacer.model.tag_name,app.components.x_spacer.x_spacer.element_class());
}

return null;
});
app.components.x_spacer.x_spacer.init_BANG_ = (function app$components$x_spacer$x_spacer$init_BANG_(){
return app.components.x_spacer.x_spacer.register_BANG_();
});

//# sourceMappingURL=app.components.x_spacer.x_spacer.js.map
