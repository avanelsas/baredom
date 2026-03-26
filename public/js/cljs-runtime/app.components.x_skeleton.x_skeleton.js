goog.provide('app.components.x_skeleton.x_skeleton');
goog.scope(function(){
  app.components.x_skeleton.x_skeleton.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_skeleton.x_skeleton.k_initialized = "__xSkeletonInit";
app.components.x_skeleton.x_skeleton.k_base = "__xSkeletonBase";
app.components.x_skeleton.x_skeleton.style_text = (""+":host{"+"display:block;"+"box-sizing:border-box;"+"color-scheme:light dark;"+"--x-skeleton-color:rgba(0,0,0,0.08);"+"--x-skeleton-highlight:rgba(255,255,255,0.65);"+"--x-skeleton-border-radius:4px;"+"--x-skeleton-duration:1.5s;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-skeleton-color:rgba(255,255,255,0.10);"+"--x-skeleton-highlight:rgba(255,255,255,0.18);}}"+"[part=base]{"+"width:100%;"+"height:1rem;"+"position:relative;"+"overflow:hidden;"+"background:var(--x-skeleton-color);"+"border-radius:var(--x-skeleton-border-radius);}"+"[part=base][data-variant='text']{"+"height:1em;"+"border-radius:3px;}"+"[part=base][data-variant='circle']{"+"width:2.5rem;"+"height:2.5rem;"+"border-radius:50%;}"+"[part=shimmer]{"+"position:absolute;"+"inset:0;"+"background:linear-gradient("+"90deg,"+"transparent 0%,"+"var(--x-skeleton-highlight) 50%,"+"transparent 100%);"+"transform:translateX(-100%);"+"display:none;"+"pointer-events:none;}"+"@keyframes x-skeleton-pulse{"+"0%,100%{opacity:1;}"+"50%{opacity:0.4;}}"+"@keyframes x-skeleton-wave{"+"0%{transform:translateX(-100%);}"+"100%{transform:translateX(200%);}}"+"[part=base][data-animation='pulse']{"+"animation:x-skeleton-pulse var(--x-skeleton-duration,1.5s) ease-in-out infinite;}"+"[part=base][data-animation='wave'] [part=shimmer]{"+"display:block;"+"animation:x-skeleton-wave var(--x-skeleton-duration,1.5s) linear infinite;}"+"@media (prefers-reduced-motion:reduce){"+"[part=base][data-animation='pulse']{"+"animation:none;}"+"[part=base][data-animation='wave'] [part=shimmer]{"+"animation:none;}}");
app.components.x_skeleton.x_skeleton.init_dom_BANG_ = (function app$components$x_skeleton$x_skeleton$init_dom_BANG_(el){
var root_23041 = el.attachShadow(({"mode": "open"}));
var style_el_23042 = document.createElement("style");
var base_23043 = document.createElement("div");
var shimmer_23044 = document.createElement("div");
(style_el_23042.textContent = app.components.x_skeleton.x_skeleton.style_text);

base_23043.setAttribute("part","base");

shimmer_23044.setAttribute("part","shimmer");

base_23043.appendChild(shimmer_23044);

root_23041.appendChild(style_el_23042);

root_23041.appendChild(base_23043);

app.components.x_skeleton.x_skeleton.goog$module$goog$object.set(el,app.components.x_skeleton.x_skeleton.k_base,base_23043);

app.components.x_skeleton.x_skeleton.goog$module$goog$object.set(el,app.components.x_skeleton.x_skeleton.k_initialized,true);

return null;
});
app.components.x_skeleton.x_skeleton.read_inputs = (function app$components$x_skeleton$x_skeleton$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_skeleton.model.attr_variant),new cljs.core.Keyword(null,"animation","animation",-1248293244),el.getAttribute(app.components.x_skeleton.model.attr_animation),new cljs.core.Keyword(null,"width","width",-384071477),el.getAttribute(app.components.x_skeleton.model.attr_width),new cljs.core.Keyword(null,"height","height",1025178622),el.getAttribute(app.components.x_skeleton.model.attr_height)], null);
});
app.components.x_skeleton.x_skeleton.render_BANG_ = (function app$components$x_skeleton$x_skeleton$render_BANG_(el){
var map__23040_23045 = app.components.x_skeleton.model.derive_state(app.components.x_skeleton.x_skeleton.read_inputs(el));
var map__23040_23046__$1 = cljs.core.__destructure_map(map__23040_23045);
var variant_23047 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23040_23046__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var animation_23048 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23040_23046__$1,new cljs.core.Keyword(null,"animation","animation",-1248293244));
var width_23049 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23040_23046__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var height_23050 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23040_23046__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var base_23051 = app.components.x_skeleton.x_skeleton.goog$module$goog$object.get(el,app.components.x_skeleton.x_skeleton.k_base);
base_23051.setAttribute("data-variant",variant_23047);

base_23051.setAttribute("data-animation",animation_23048);

if(cljs.core.truth_(width_23049)){
base_23051.style.setProperty("width",width_23049);
} else {
base_23051.style.removeProperty("width");
}

if(cljs.core.truth_(height_23050)){
base_23051.style.setProperty("height",height_23050);
} else {
base_23051.style.removeProperty("height");
}

el.setAttribute("aria-hidden","true");

return null;
});
app.components.x_skeleton.x_skeleton.connected_BANG_ = (function app$components$x_skeleton$x_skeleton$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_skeleton.x_skeleton.goog$module$goog$object.get(el,app.components.x_skeleton.x_skeleton.k_initialized))){
} else {
app.components.x_skeleton.x_skeleton.init_dom_BANG_(el);
}

app.components.x_skeleton.x_skeleton.render_BANG_(el);

return null;
});
app.components.x_skeleton.x_skeleton.attribute_changed_BANG_ = (function app$components$x_skeleton$x_skeleton$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_(app.components.x_skeleton.x_skeleton.goog$module$goog$object.get(el,app.components.x_skeleton.x_skeleton.k_initialized))){
app.components.x_skeleton.x_skeleton.render_BANG_(el);
} else {
}
} else {
}

return null;
});
app.components.x_skeleton.x_skeleton.install_property_accessors_BANG_ = (function app$components$x_skeleton$x_skeleton$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_skeleton.model.attr_variant,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_skeleton.model.attr_variant);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_skeleton.model.default_variant;
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v)))){
return this$.setAttribute(app.components.x_skeleton.model.attr_variant,v);
} else {
return this$.removeAttribute(app.components.x_skeleton.model.attr_variant);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_skeleton.model.attr_animation,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_skeleton.model.attr_animation);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_skeleton.model.default_animation;
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v)))){
return this$.setAttribute(app.components.x_skeleton.model.attr_animation,v);
} else {
return this$.removeAttribute(app.components.x_skeleton.model.attr_animation);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_skeleton.model.attr_width,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_skeleton.model.attr_width);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v)))){
return this$.setAttribute(app.components.x_skeleton.model.attr_width,v);
} else {
return this$.removeAttribute(app.components.x_skeleton.model.attr_width);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,app.components.x_skeleton.model.attr_height,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_skeleton.model.attr_height);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v)))){
return this$.setAttribute(app.components.x_skeleton.model.attr_height,v);
} else {
return this$.removeAttribute(app.components.x_skeleton.model.attr_height);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_skeleton.x_skeleton.element_class = (function app$components$x_skeleton$x_skeleton$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_skeleton.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_skeleton.x_skeleton.connected_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
app.components.x_skeleton.x_skeleton.attribute_changed_BANG_(this$,n,o,v);

return null;
}));

app.components.x_skeleton.x_skeleton.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_skeleton.x_skeleton.register_BANG_ = (function app$components$x_skeleton$x_skeleton$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_skeleton.model.tag_name))){
} else {
customElements.define(app.components.x_skeleton.model.tag_name,app.components.x_skeleton.x_skeleton.element_class());
}

return null;
});
app.components.x_skeleton.x_skeleton.init_BANG_ = (function app$components$x_skeleton$x_skeleton$init_BANG_(){
return app.components.x_skeleton.x_skeleton.register_BANG_();
});

//# sourceMappingURL=app.components.x_skeleton.x_skeleton.js.map
