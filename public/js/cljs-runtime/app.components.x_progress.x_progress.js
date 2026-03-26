goog.provide('app.components.x_progress.x_progress');
goog.scope(function(){
  app.components.x_progress.x_progress.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_progress.x_progress.k_initialized = "__xProgressInit";
app.components.x_progress.x_progress.k_base = "__xProgressBase";
app.components.x_progress.x_progress.k_header = "__xProgressHeader";
app.components.x_progress.x_progress.k_fill = "__xProgressFill";
app.components.x_progress.x_progress.k_label_node = "__xProgressLabel";
app.components.x_progress.x_progress.k_value_node = "__xProgressValue";
app.components.x_progress.x_progress.k_completed = "__xProgressCompleted";
app.components.x_progress.x_progress.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"box-sizing:border-box;"+"--x-progress-border-radius:9999px;"+"--x-progress-track-color:rgba(0,0,0,0.10);"+"--x-progress-fill-color:#3b82f6;"+"--x-progress-label-color:rgba(0,0,0,0.60);"+"--x-progress-value-color:rgba(0,0,0,0.50);}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-progress-track-color:rgba(255,255,255,0.12);"+"--x-progress-label-color:rgba(255,255,255,0.60);"+"--x-progress-value-color:rgba(255,255,255,0.50);}}"+"[part=base][data-size='sm']{--x-progress-height:4px;}"+"[part=base][data-size='md']{--x-progress-height:8px;}"+"[part=base][data-size='lg']{--x-progress-height:12px;}"+"[part=base][data-variant='success']{--x-progress-fill-color:#22c55e;}"+"[part=base][data-variant='warning']{--x-progress-fill-color:#f59e0b;}"+"[part=base][data-variant='danger']{--x-progress-fill-color:#ef4444;}"+"[part=base]{"+"width:100%;"+"box-sizing:border-box;}"+"[part=header]{"+"display:flex;"+"justify-content:space-between;"+"align-items:baseline;"+"margin-bottom:4px;}"+"[part=label-text]{"+"font-size:0.875rem;"+"color:var(--x-progress-label-color);"+"font-weight:500;}"+"[part=value-text]{"+"font-size:0.8125rem;"+"color:var(--x-progress-value-color);}"+"[part=track]{"+"height:var(--x-progress-height,8px);"+"border-radius:var(--x-progress-border-radius,9999px);"+"background:var(--x-progress-track-color);"+"overflow:hidden;"+"position:relative;}"+"[part=fill]{"+"height:100%;"+"background:var(--x-progress-fill-color);"+"border-radius:var(--x-progress-border-radius,9999px);"+"transition:width 0.3s ease;"+"width:0%;}"+"@keyframes x-progress-indeterminate{"+"0%{transform:translateX(-100%) scaleX(0.5);}"+"50%{transform:translateX(60%) scaleX(0.8);}"+"100%{transform:translateX(200%) scaleX(0.5);}}"+"[part=base][data-indeterminate='true'] [part=fill]{"+"animation:x-progress-indeterminate 1.5s ease infinite;"+"width:40%;"+"transform-origin:left center;}"+"@media (prefers-reduced-motion:reduce){"+"[part=fill]{transition:none;}"+"[part=base][data-indeterminate='true'] [part=fill]{"+"animation:none;"+"width:50%;}}");
app.components.x_progress.x_progress.init_dom_BANG_ = (function app$components$x_progress$x_progress$init_dom_BANG_(el){
var root_22981 = el.attachShadow(({"mode": "open"}));
var style_el_22982 = document.createElement("style");
var base_22983 = document.createElement("div");
var header_22984 = document.createElement("div");
var label_node_22985 = document.createElement("span");
var value_node_22986 = document.createElement("span");
var track_22987 = document.createElement("div");
var fill_22988 = document.createElement("div");
(style_el_22982.textContent = app.components.x_progress.x_progress.style_text);

base_22983.setAttribute("part","base");

header_22984.setAttribute("part","header");

label_node_22985.setAttribute("part","label-text");

value_node_22986.setAttribute("part","value-text");

track_22987.setAttribute("part","track");

fill_22988.setAttribute("part","fill");

header_22984.appendChild(label_node_22985);

header_22984.appendChild(value_node_22986);

track_22987.appendChild(fill_22988);

base_22983.appendChild(header_22984);

base_22983.appendChild(track_22987);

root_22981.appendChild(style_el_22982);

root_22981.appendChild(base_22983);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_base,base_22983);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_header,header_22984);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_fill,fill_22988);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_label_node,label_node_22985);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_value_node,value_node_22986);

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_initialized,true);

return null;
});
app.components.x_progress.x_progress.read_inputs = (function app$components$x_progress$x_progress$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"value","value",305978217),el.getAttribute(app.components.x_progress.model.attr_value),new cljs.core.Keyword(null,"max","max",61366548),el.getAttribute(app.components.x_progress.model.attr_max),new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_progress.model.attr_variant),new cljs.core.Keyword(null,"size","size",1098693007),el.getAttribute(app.components.x_progress.model.attr_size),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_progress.model.attr_label),new cljs.core.Keyword(null,"show-value","show-value",-1560941240),el.hasAttribute(app.components.x_progress.model.attr_show_value),new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976),el.hasAttribute(app.components.x_progress.model.attr_indeterminate)], null);
});
app.components.x_progress.x_progress.render_BANG_ = (function app$components$x_progress$x_progress$render_BANG_(el){
var map__22968_22989 = app.components.x_progress.model.derive_state(app.components.x_progress.x_progress.read_inputs(el));
var map__22968_22990__$1 = cljs.core.__destructure_map(map__22968_22989);
var percent_22991 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"percent","percent",2031453817));
var variant_22992 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var show_value_22993 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"show-value","show-value",-1560941240));
var value_22994 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"value","value",305978217));
var size_22995 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var aria_valuetext_22996 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"aria-valuetext","aria-valuetext",-1020629328));
var indeterminate_22997 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976));
var label_22998 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var max_22999 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22968_22990__$1,new cljs.core.Keyword(null,"max","max",61366548));
var base_23000 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_base);
var header_23001 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_header);
var fill_23002 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_fill);
var label_node_23003 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_label_node);
var value_node_23004 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_value_node);
var show_header_QMARK__23005 = (function (){var or__5142__auto__ = (!((label_22998 == null)));
if(or__5142__auto__){
return or__5142__auto__;
} else {
return show_value_22993;
}
})();
var was_completed_23006 = app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_completed);
var now_complete_23007 = ((cljs.core.not(indeterminate_22997)) && ((value_22994 >= max_22999)));
base_23000.setAttribute("data-variant",variant_22992);

base_23000.setAttribute("data-size",size_22995);

base_23000.setAttribute("data-indeterminate",(cljs.core.truth_(indeterminate_22997)?"true":"false"));

(fill_23002.style.width = (cljs.core.truth_(indeterminate_22997)?"40%":(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(percent_22991.toFixed((2)))+"%")));

(header_23001.style.display = (cljs.core.truth_(show_header_QMARK__23005)?"flex":"none"));

(label_node_23003.textContent = (function (){var or__5142__auto__ = label_22998;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

(label_node_23003.style.display = (((!((label_22998 == null))))?"":"none"));

(value_node_23004.textContent = (cljs.core.truth_((function (){var and__5140__auto__ = show_value_22993;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(indeterminate_22997);
} else {
return and__5140__auto__;
}
})())?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round(percent_22991))+"%"):""));

(value_node_23004.style.display = (cljs.core.truth_(show_value_22993)?"":"none"));

el.setAttribute("role","progressbar");

el.setAttribute("aria-valuemin","0");

if(cljs.core.truth_(indeterminate_22997)){
el.removeAttribute("aria-valuenow");

el.setAttribute("aria-busy","true");

el.setAttribute("aria-valuetext",aria_valuetext_22996);
} else {
el.setAttribute("aria-valuenow",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value_22994)));

el.setAttribute("aria-valuemax",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_22999)));

el.setAttribute("aria-valuetext",aria_valuetext_22996);

el.removeAttribute("aria-busy");
}

if((!((label_22998 == null)))){
el.setAttribute("aria-label",label_22998);
} else {
el.removeAttribute("aria-label");
}

if(((now_complete_23007) && (cljs.core.not(was_completed_23006)))){
el.dispatchEvent((new CustomEvent(app.components.x_progress.model.event_complete,({"bubbles": true, "composed": true, "detail": ({"value": value_22994, "max": max_22999})}))));
} else {
}

app.components.x_progress.x_progress.goog$module$goog$object.set(el,app.components.x_progress.x_progress.k_completed,cljs.core.boolean$(now_complete_23007));

return null;
});
app.components.x_progress.x_progress.connected_BANG_ = (function app$components$x_progress$x_progress$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_initialized))){
} else {
app.components.x_progress.x_progress.init_dom_BANG_(el);
}

app.components.x_progress.x_progress.render_BANG_(el);

return null;
});
app.components.x_progress.x_progress.attribute_changed_BANG_ = (function app$components$x_progress$x_progress$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_(app.components.x_progress.x_progress.goog$module$goog$object.get(el,app.components.x_progress.x_progress.k_initialized))){
app.components.x_progress.x_progress.render_BANG_(el);
} else {
}
} else {
}

return null;
});
app.components.x_progress.x_progress.install_property_accessors_BANG_ = (function app$components$x_progress$x_progress$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_progress.model.attr_value,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_progress.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "0";
}
}), "set": (function (v){
var this$ = this;
return this$.setAttribute(app.components.x_progress.model.attr_value,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_progress.model.attr_max,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_progress.model.attr_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "100";
}
}), "set": (function (v){
var this$ = this;
return this$.setAttribute(app.components.x_progress.model.attr_max,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"indeterminate",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_progress.model.attr_indeterminate);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_progress.model.attr_indeterminate,"");
} else {
return this$.removeAttribute(app.components.x_progress.model.attr_indeterminate);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"showValue",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_progress.model.attr_show_value);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_progress.model.attr_show_value,"");
} else {
return this$.removeAttribute(app.components.x_progress.model.attr_show_value);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_progress.x_progress.element_class = (function app$components$x_progress$x_progress$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_progress.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_progress.x_progress.connected_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
app.components.x_progress.x_progress.attribute_changed_BANG_(this$,n,o,v);

return null;
}));

app.components.x_progress.x_progress.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_progress.x_progress.register_BANG_ = (function app$components$x_progress$x_progress$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_progress.model.tag_name))){
} else {
customElements.define(app.components.x_progress.model.tag_name,app.components.x_progress.x_progress.element_class());
}

return null;
});
app.components.x_progress.x_progress.init_BANG_ = (function app$components$x_progress$x_progress$init_BANG_(){
return app.components.x_progress.x_progress.register_BANG_();
});

//# sourceMappingURL=app.components.x_progress.x_progress.js.map
