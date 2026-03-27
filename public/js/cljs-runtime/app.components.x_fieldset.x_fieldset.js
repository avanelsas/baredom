goog.provide('app.components.x_fieldset.x_fieldset');
goog.scope(function(){
  app.components.x_fieldset.x_fieldset.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_fieldset.x_fieldset.k_refs = "__xFieldsetRefs";
app.components.x_fieldset.x_fieldset.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-fieldset-border-color:#d1d5db;"+"--x-fieldset-border-width:1px;"+"--x-fieldset-border-radius:8px;"+"--x-fieldset-padding:1rem;"+"--x-fieldset-gap:0.75rem;"+"--x-fieldset-bg:transparent;"+"--x-fieldset-legend-color:#374151;"+"--x-fieldset-legend-font-size:0.875rem;"+"--x-fieldset-legend-font-weight:600;"+"--x-fieldset-legend-padding:0 0.375rem;"+"--x-fieldset-disabled-opacity:0.45;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-fieldset-border-color:#374151;"+"--x-fieldset-legend-color:#d1d5db;"+"}"+"}"+"[part=root]{"+"display:block;"+"border:var(--x-fieldset-border-width) solid var(--x-fieldset-border-color);"+"border-radius:var(--x-fieldset-border-radius);"+"padding:var(--x-fieldset-padding);"+"background:var(--x-fieldset-bg);"+"position:relative;"+"}"+":host([data-disabled]) [part=root]{"+"opacity:var(--x-fieldset-disabled-opacity);"+"}"+"[part=legend]{"+"display:block;"+"position:absolute;"+"top:calc(-0.5em);"+"left:calc(var(--x-fieldset-border-radius) - 0.375rem);"+"padding:var(--x-fieldset-legend-padding);"+"color:var(--x-fieldset-legend-color);"+"font-size:var(--x-fieldset-legend-font-size);"+"font-weight:var(--x-fieldset-legend-font-weight);"+"background:inherit;"+"line-height:1;"+"white-space:nowrap;"+"}"+"[part=legend][hidden]{"+"display:none;"+"}"+"[part=content]{"+"display:flex;"+"flex-direction:column;"+"gap:var(--x-fieldset-gap);"+"}"+"@media (prefers-reduced-motion:reduce){"+"}");
app.components.x_fieldset.x_fieldset.make_el = (function app$components$x_fieldset$x_fieldset$make_el(tag){
return document.createElement(tag);
});
app.components.x_fieldset.x_fieldset.set_attr_BANG_ = (function app$components$x_fieldset$x_fieldset$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_fieldset.x_fieldset.remove_attr_BANG_ = (function app$components$x_fieldset$x_fieldset$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_fieldset.x_fieldset.has_attr_QMARK_ = (function app$components$x_fieldset$x_fieldset$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_fieldset.x_fieldset.get_attr = (function app$components$x_fieldset$x_fieldset$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_fieldset.x_fieldset.set_bool_attr_BANG_ = (function app$components$x_fieldset$x_fieldset$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_fieldset.x_fieldset.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_fieldset.x_fieldset.remove_attr_BANG_(el,attr);
}
});
app.components.x_fieldset.x_fieldset.make_shadow_BANG_ = (function app$components$x_fieldset$x_fieldset$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_fieldset.x_fieldset.make_el("style");
var root_el = app.components.x_fieldset.x_fieldset.make_el("div");
var legend_el = app.components.x_fieldset.x_fieldset.make_el("div");
var content_el = app.components.x_fieldset.x_fieldset.make_el("div");
var slot_el = app.components.x_fieldset.x_fieldset.make_el("slot");
(style_el.textContent = app.components.x_fieldset.x_fieldset.style_text);

app.components.x_fieldset.x_fieldset.set_attr_BANG_(root_el,"part","root");

app.components.x_fieldset.x_fieldset.set_attr_BANG_(root_el,"role","group");

app.components.x_fieldset.x_fieldset.set_attr_BANG_(legend_el,"part","legend");

app.components.x_fieldset.x_fieldset.set_attr_BANG_(legend_el,"id","x-fieldset-legend");

app.components.x_fieldset.x_fieldset.set_attr_BANG_(content_el,"part","content");

content_el.appendChild(slot_el);

root_el.appendChild(legend_el);

root_el.appendChild(content_el);

root.appendChild(style_el);

root.appendChild(root_el);

var refs = ({"root": root, "root-el": root_el, "legend-el": legend_el, "content-el": content_el});
app.components.x_fieldset.x_fieldset.goog$module$goog$object.set(el,app.components.x_fieldset.x_fieldset.k_refs,refs);

return refs;
});
app.components.x_fieldset.x_fieldset.read_model = (function app$components$x_fieldset$x_fieldset$read_model(el){
return app.components.x_fieldset.model.normalize(new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"legend-raw","legend-raw",907427195),app.components.x_fieldset.x_fieldset.get_attr(el,app.components.x_fieldset.model.attr_legend),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),app.components.x_fieldset.x_fieldset.has_attr_QMARK_(el,app.components.x_fieldset.model.attr_disabled),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),app.components.x_fieldset.x_fieldset.get_attr(el,app.components.x_fieldset.model.attr_aria_label),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860),app.components.x_fieldset.x_fieldset.get_attr(el,app.components.x_fieldset.model.attr_aria_describedby)], null));
});
app.components.x_fieldset.x_fieldset.render_BANG_ = (function app$components$x_fieldset$x_fieldset$render_BANG_(el){
var temp__5823__auto__ = app.components.x_fieldset.x_fieldset.goog$module$goog$object.get(el,app.components.x_fieldset.x_fieldset.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var root_el = app.components.x_fieldset.x_fieldset.goog$module$goog$object.get(refs,"root-el");
var legend_el = app.components.x_fieldset.x_fieldset.goog$module$goog$object.get(refs,"legend-el");
var m = app.components.x_fieldset.x_fieldset.read_model(el);
var legend = new cljs.core.Keyword(null,"legend","legend",-1027192245).cljs$core$IFn$_invoke$arity$1(m);
var visible_QMARK_ = new cljs.core.Keyword(null,"legend-visible?","legend-visible?",-281107501).cljs$core$IFn$_invoke$arity$1(m);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
(legend_el.textContent = legend);

if(cljs.core.truth_(visible_QMARK_)){
app.components.x_fieldset.x_fieldset.remove_attr_BANG_(legend_el,"hidden");
} else {
app.components.x_fieldset.x_fieldset.set_attr_BANG_(legend_el,"hidden","");
}

var temp__5821__auto___22653 = new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___22653)){
var aria_lbl_22654 = temp__5821__auto___22653;
app.components.x_fieldset.x_fieldset.set_attr_BANG_(root_el,"aria-label",aria_lbl_22654);

app.components.x_fieldset.x_fieldset.remove_attr_BANG_(root_el,"aria-labelledby");
} else {
app.components.x_fieldset.x_fieldset.remove_attr_BANG_(root_el,"aria-label");

if(cljs.core.truth_(visible_QMARK_)){
app.components.x_fieldset.x_fieldset.set_attr_BANG_(root_el,"aria-labelledby","x-fieldset-legend");
} else {
app.components.x_fieldset.x_fieldset.remove_attr_BANG_(root_el,"aria-labelledby");
}
}

var temp__5821__auto___22655 = new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___22655)){
var v_22656 = temp__5821__auto___22655;
app.components.x_fieldset.x_fieldset.set_attr_BANG_(root_el,"aria-describedby",v_22656);
} else {
app.components.x_fieldset.x_fieldset.remove_attr_BANG_(root_el,"aria-describedby");
}

return app.components.x_fieldset.x_fieldset.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);
} else {
return null;
}
});
app.components.x_fieldset.x_fieldset.connected_BANG_ = (function app$components$x_fieldset$x_fieldset$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_fieldset.x_fieldset.goog$module$goog$object.get(el,app.components.x_fieldset.x_fieldset.k_refs))){
} else {
app.components.x_fieldset.x_fieldset.make_shadow_BANG_(el);
}

return app.components.x_fieldset.x_fieldset.render_BANG_(el);
});
app.components.x_fieldset.x_fieldset.disconnected_BANG_ = (function app$components$x_fieldset$x_fieldset$disconnected_BANG_(_el){
return null;
});
app.components.x_fieldset.x_fieldset.attribute_changed_BANG_ = (function app$components$x_fieldset$x_fieldset$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_fieldset.x_fieldset.render_BANG_(el);
});
app.components.x_fieldset.x_fieldset.define_bool_prop_BANG_ = (function app$components$x_fieldset$x_fieldset$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_fieldset.x_fieldset.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_fieldset.x_fieldset.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_fieldset.x_fieldset.define_string_prop_BANG_ = (function app$components$x_fieldset$x_fieldset$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_fieldset.x_fieldset.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_fieldset.x_fieldset.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_fieldset.x_fieldset.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_fieldset.x_fieldset.element_class = (function app$components$x_fieldset$x_fieldset$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_fieldset.model.observed_attributes;
})}));

app.components.x_fieldset.x_fieldset.define_string_prop_BANG_(proto,"legend",app.components.x_fieldset.model.attr_legend);

app.components.x_fieldset.x_fieldset.define_bool_prop_BANG_(proto,"disabled",app.components.x_fieldset.model.attr_disabled);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_fieldset.x_fieldset.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_fieldset.x_fieldset.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_fieldset.x_fieldset.attribute_changed_BANG_(this$,n,o,v);
}));

return cls;
});
app.components.x_fieldset.x_fieldset.init_BANG_ = (function app$components$x_fieldset$x_fieldset$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_fieldset.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_fieldset.model.tag_name,app.components.x_fieldset.x_fieldset.element_class());
}
});

//# sourceMappingURL=app.components.x_fieldset.x_fieldset.js.map
