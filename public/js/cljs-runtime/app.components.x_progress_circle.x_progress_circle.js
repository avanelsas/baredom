goog.provide('app.components.x_progress_circle.x_progress_circle');
goog.scope(function(){
  app.components.x_progress_circle.x_progress_circle.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_progress_circle.x_progress_circle.svg_ns = "http://www.w3.org/2000/svg";
app.components.x_progress_circle.x_progress_circle.circumference = 100.0;
app.components.x_progress_circle.x_progress_circle.k_initialized = "__xProgressCircleInit";
app.components.x_progress_circle.x_progress_circle.k_base = "__xProgressCircleBase";
app.components.x_progress_circle.x_progress_circle.k_fill = "__xProgressCircleFill";
app.components.x_progress_circle.x_progress_circle.k_value_node = "__xProgressCircleValueNode";
app.components.x_progress_circle.x_progress_circle.k_completed = "__xProgressCircleCompleted";
app.components.x_progress_circle.x_progress_circle.style_text = (""+":host{"+"display:inline-flex;"+"color-scheme:light dark;"+"box-sizing:border-box;"+"--x-progress-circle-size:64px;"+"--x-progress-circle-track-color:rgba(0,0,0,0.10);"+"--x-progress-circle-fill-color:#3b82f6;"+"--x-progress-circle-stroke-width:2.8;"+"--x-progress-circle-value-color:rgba(0,0,0,0.50);"+"--x-progress-circle-transition-duration:0.3s;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-progress-circle-track-color:rgba(255,255,255,0.12);"+"--x-progress-circle-value-color:rgba(255,255,255,0.50);}}"+"[part=base][data-size='sm']{--x-progress-circle-size:40px;}"+"[part=base][data-size='md']{--x-progress-circle-size:64px;}"+"[part=base][data-size='lg']{--x-progress-circle-size:96px;}"+"[part=base][data-variant='success']{--x-progress-circle-fill-color:#22c55e;}"+"[part=base][data-variant='warning']{--x-progress-circle-fill-color:#f59e0b;}"+"[part=base][data-variant='danger']{--x-progress-circle-fill-color:#ef4444;}"+"[part=base]{"+"position:relative;"+"display:inline-flex;"+"width:var(--x-progress-circle-size);"+"height:var(--x-progress-circle-size);}"+"[part=svg]{"+"width:100%;"+"height:100%;}"+"[part=track]{"+"fill:none;"+"stroke:var(--x-progress-circle-track-color);"+"stroke-width:var(--x-progress-circle-stroke-width);}"+"[part=fill]{"+"fill:none;"+"stroke:var(--x-progress-circle-fill-color);"+"stroke-width:var(--x-progress-circle-stroke-width);"+"stroke-linecap:round;"+"transform:rotate(-90deg);"+"transform-origin:center;"+"transition:stroke-dashoffset var(--x-progress-circle-transition-duration) ease;}"+"[part=center]{"+"position:absolute;"+"inset:0;"+"display:flex;"+"align-items:center;"+"justify-content:center;}"+"[part=value-text]{"+"font-size:calc(var(--x-progress-circle-size) * 0.22);"+"color:var(--x-progress-circle-value-color);"+"font-weight:500;"+"line-height:1;}"+"@keyframes x-progress-circle-spin{"+"from{transform:rotate(-90deg);}"+"to{transform:rotate(270deg);}}"+"[part=base][data-indeterminate='true'] [part=fill]{"+"animation:x-progress-circle-spin 1.2s linear infinite;}"+"@media (prefers-reduced-motion:reduce){"+"[part=fill]{transition:none;}"+"[part=base][data-indeterminate='true'] [part=fill]{"+"animation:none;}}");
app.components.x_progress_circle.x_progress_circle.init_dom_BANG_ = (function app$components$x_progress_circle$x_progress_circle$init_dom_BANG_(el){
var root_23011 = el.attachShadow(({"mode": "open"}));
var style_el_23012 = document.createElement("style");
var base_23013 = document.createElement("div");
var svg_23014 = document.createElementNS(app.components.x_progress_circle.x_progress_circle.svg_ns,"svg");
var track_23015 = document.createElementNS(app.components.x_progress_circle.x_progress_circle.svg_ns,"circle");
var fill_23016 = document.createElementNS(app.components.x_progress_circle.x_progress_circle.svg_ns,"circle");
var center_23017 = document.createElement("div");
var value_node_23018 = document.createElement("span");
(style_el_23012.textContent = app.components.x_progress_circle.x_progress_circle.style_text);

base_23013.setAttribute("part","base");

svg_23014.setAttribute("part","svg");

svg_23014.setAttribute("viewBox","0 0 36 36");

svg_23014.setAttribute("aria-hidden","true");

svg_23014.setAttribute("focusable","false");

track_23015.setAttribute("part","track");

track_23015.setAttribute("cx","18");

track_23015.setAttribute("cy","18");

track_23015.setAttribute("r","15.9155");

fill_23016.setAttribute("part","fill");

fill_23016.setAttribute("cx","18");

fill_23016.setAttribute("cy","18");

fill_23016.setAttribute("r","15.9155");

center_23017.setAttribute("part","center");

value_node_23018.setAttribute("part","value-text");

svg_23014.appendChild(track_23015);

svg_23014.appendChild(fill_23016);

center_23017.appendChild(value_node_23018);

base_23013.appendChild(svg_23014);

base_23013.appendChild(center_23017);

root_23011.appendChild(style_el_23012);

root_23011.appendChild(base_23013);

app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.set(el,app.components.x_progress_circle.x_progress_circle.k_base,base_23013);

app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.set(el,app.components.x_progress_circle.x_progress_circle.k_fill,fill_23016);

app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.set(el,app.components.x_progress_circle.x_progress_circle.k_value_node,value_node_23018);

app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.set(el,app.components.x_progress_circle.x_progress_circle.k_initialized,true);

return null;
});
app.components.x_progress_circle.x_progress_circle.read_inputs = (function app$components$x_progress_circle$x_progress_circle$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"value","value",305978217),el.getAttribute(app.components.x_progress_circle.model.attr_value),new cljs.core.Keyword(null,"max","max",61366548),el.getAttribute(app.components.x_progress_circle.model.attr_max),new cljs.core.Keyword(null,"variant","variant",-424354234),el.getAttribute(app.components.x_progress_circle.model.attr_variant),new cljs.core.Keyword(null,"size","size",1098693007),el.getAttribute(app.components.x_progress_circle.model.attr_size),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_progress_circle.model.attr_label),new cljs.core.Keyword(null,"show-value","show-value",-1560941240),el.hasAttribute(app.components.x_progress_circle.model.attr_show_value),new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976),el.hasAttribute(app.components.x_progress_circle.model.attr_indeterminate)], null);
});
app.components.x_progress_circle.x_progress_circle.render_BANG_ = (function app$components$x_progress_circle$x_progress_circle$render_BANG_(el){
var map__23010_23019 = app.components.x_progress_circle.model.derive_state(app.components.x_progress_circle.x_progress_circle.read_inputs(el));
var map__23010_23020__$1 = cljs.core.__destructure_map(map__23010_23019);
var percent_23021 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"percent","percent",2031453817));
var variant_23022 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var show_value_23023 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"show-value","show-value",-1560941240));
var value_23024 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"value","value",305978217));
var size_23025 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var aria_valuetext_23026 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"aria-valuetext","aria-valuetext",-1020629328));
var indeterminate_23027 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976));
var label_23028 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var max_23029 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010_23020__$1,new cljs.core.Keyword(null,"max","max",61366548));
var base_23030 = app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_base);
var fill_23031 = app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_fill);
var value_node_23032 = app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_value_node);
var was_completed_23033 = app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_completed);
var now_complete_23034 = ((cljs.core.not(indeterminate_23027)) && ((value_23024 >= max_23029)));
var offset_23035 = (cljs.core.truth_(indeterminate_23027)?(app.components.x_progress_circle.x_progress_circle.circumference * 0.75):(app.components.x_progress_circle.x_progress_circle.circumference * ((1) - (percent_23021 / (100)))));
base_23030.setAttribute("data-variant",variant_23022);

base_23030.setAttribute("data-size",size_23025);

base_23030.setAttribute("data-indeterminate",(cljs.core.truth_(indeterminate_23027)?"true":"false"));

fill_23031.setAttribute("stroke-dasharray",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_progress_circle.x_progress_circle.circumference)));

fill_23031.setAttribute("stroke-dashoffset",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(offset_23035)));

(value_node_23032.textContent = (cljs.core.truth_((function (){var and__5140__auto__ = show_value_23023;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(indeterminate_23027);
} else {
return and__5140__auto__;
}
})())?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round(percent_23021))+"%"):""));

(value_node_23032.style.display = (cljs.core.truth_((function (){var and__5140__auto__ = show_value_23023;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(indeterminate_23027);
} else {
return and__5140__auto__;
}
})())?"":"none"));

el.setAttribute("role","progressbar");

el.setAttribute("aria-valuemin","0");

if(cljs.core.truth_(indeterminate_23027)){
el.removeAttribute("aria-valuenow");

el.setAttribute("aria-busy","true");

el.setAttribute("aria-valuetext",aria_valuetext_23026);
} else {
el.setAttribute("aria-valuenow",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value_23024)));

el.setAttribute("aria-valuemax",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_23029)));

el.setAttribute("aria-valuetext",aria_valuetext_23026);

el.removeAttribute("aria-busy");
}

if((!((label_23028 == null)))){
el.setAttribute("aria-label",label_23028);
} else {
el.removeAttribute("aria-label");
}

if(((now_complete_23034) && (cljs.core.not(was_completed_23033)))){
el.dispatchEvent((new CustomEvent(app.components.x_progress_circle.model.event_complete,({"bubbles": true, "composed": true, "detail": ({"value": value_23024, "max": max_23029})}))));
} else {
}

app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.set(el,app.components.x_progress_circle.x_progress_circle.k_completed,cljs.core.boolean$(now_complete_23034));

return null;
});
app.components.x_progress_circle.x_progress_circle.connected_BANG_ = (function app$components$x_progress_circle$x_progress_circle$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_initialized))){
} else {
app.components.x_progress_circle.x_progress_circle.init_dom_BANG_(el);
}

app.components.x_progress_circle.x_progress_circle.render_BANG_(el);

return null;
});
app.components.x_progress_circle.x_progress_circle.attribute_changed_BANG_ = (function app$components$x_progress_circle$x_progress_circle$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_(app.components.x_progress_circle.x_progress_circle.goog$module$goog$object.get(el,app.components.x_progress_circle.x_progress_circle.k_initialized))){
app.components.x_progress_circle.x_progress_circle.render_BANG_(el);
} else {
}
} else {
}

return null;
});
app.components.x_progress_circle.x_progress_circle.install_property_accessors_BANG_ = (function app$components$x_progress_circle$x_progress_circle$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_progress_circle.model.attr_value,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_progress_circle.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "0";
}
}), "set": (function (v){
var this$ = this;
return this$.setAttribute(app.components.x_progress_circle.model.attr_value,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_progress_circle.model.attr_max,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_progress_circle.model.attr_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "100";
}
}), "set": (function (v){
var this$ = this;
return this$.setAttribute(app.components.x_progress_circle.model.attr_max,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"indeterminate",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_progress_circle.model.attr_indeterminate);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_progress_circle.model.attr_indeterminate,"");
} else {
return this$.removeAttribute(app.components.x_progress_circle.model.attr_indeterminate);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"showValue",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_progress_circle.model.attr_show_value);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_progress_circle.model.attr_show_value,"");
} else {
return this$.removeAttribute(app.components.x_progress_circle.model.attr_show_value);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_progress_circle.x_progress_circle.element_class = (function app$components$x_progress_circle$x_progress_circle$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_progress_circle.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_progress_circle.x_progress_circle.connected_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,o,v){
var this$ = this;
app.components.x_progress_circle.x_progress_circle.attribute_changed_BANG_(this$,n,o,v);

return null;
}));

app.components.x_progress_circle.x_progress_circle.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_progress_circle.x_progress_circle.register_BANG_ = (function app$components$x_progress_circle$x_progress_circle$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_progress_circle.model.tag_name))){
} else {
customElements.define(app.components.x_progress_circle.model.tag_name,app.components.x_progress_circle.x_progress_circle.element_class());
}

return null;
});
app.components.x_progress_circle.x_progress_circle.init_BANG_ = (function app$components$x_progress_circle$x_progress_circle$init_BANG_(){
return app.components.x_progress_circle.x_progress_circle.register_BANG_();
});

//# sourceMappingURL=app.components.x_progress_circle.x_progress_circle.js.map
