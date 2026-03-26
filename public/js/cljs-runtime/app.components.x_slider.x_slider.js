goog.provide('app.components.x_slider.x_slider');
goog.scope(function(){
  app.components.x_slider.x_slider.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_slider.x_slider.k_refs = "__xSliderRefs";
app.components.x_slider.x_slider.k_internals = "__xSliderInternals";
app.components.x_slider.x_slider.k_handlers = "__xSliderHandlers";
app.components.x_slider.x_slider.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-slider-track-color:rgba(0,0,0,0.15);"+"--x-slider-fill-color:#3b82f6;"+"--x-slider-thumb-color:#ffffff;"+"--x-slider-thumb-border:2px solid #3b82f6;"+"--x-slider-thumb-shadow:0 1px 4px rgba(0,0,0,0.20);"+"--x-slider-focus-ring:#60a5fa;"+"--x-slider-disabled-opacity:0.45;"+"--x-slider-label-color:rgba(0,0,0,0.60);"+"--x-slider-value-color:rgba(0,0,0,0.50);"+"--x-slider-radius:9999px;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-slider-track-color:rgba(255,255,255,0.15);"+"--x-slider-fill-color:#3b82f6;"+"--x-slider-thumb-color:#ffffff;"+"--x-slider-thumb-border:2px solid #3b82f6;"+"--x-slider-focus-ring:#93c5fd;"+"--x-slider-label-color:rgba(255,255,255,0.60);"+"--x-slider-value-color:rgba(255,255,255,0.50);"+"}}"+"[part=base]{"+"width:100%;"+"box-sizing:border-box;}"+"[part=base][data-size=sm]{--_x-slider-track-h:4px;--_x-slider-thumb-sz:14px;}"+"[part=base][data-size=md]{--_x-slider-track-h:6px;--_x-slider-thumb-sz:18px;}"+"[part=base][data-size=lg]{--_x-slider-track-h:8px;--_x-slider-thumb-sz:22px;}"+"[part=header]{"+"display:flex;"+"justify-content:space-between;"+"align-items:baseline;"+"margin-bottom:4px;}"+"[part=label-text]{"+"font-size:0.875rem;"+"color:var(--x-slider-label-color);"+"font-weight:500;}"+"[part=value-text]{"+"font-size:0.8125rem;"+"color:var(--x-slider-value-color);}"+"[part=input]{"+"-webkit-appearance:none;"+"appearance:none;"+"display:block;"+"width:100%;"+"height:var(--_x-slider-thumb-sz,18px);"+"cursor:pointer;"+"margin:0;"+"padding:0;"+"background:transparent;"+"outline:none;"+"box-sizing:border-box;}"+"[part=input]::-webkit-slider-runnable-track{"+"height:var(--_x-slider-track-h,6px);"+"border-radius:var(--x-slider-radius);"+"background:linear-gradient("+"to right,"+"var(--x-slider-fill-color) var(--_x-slider-fill,0%),"+"var(--x-slider-track-color) var(--_x-slider-fill,0%)"+");}"+"[part=input]::-moz-range-track{"+"height:var(--_x-slider-track-h,6px);"+"border-radius:var(--x-slider-radius);"+"background:var(--x-slider-track-color);"+"border:none;}"+"[part=input]::-moz-range-progress{"+"height:var(--_x-slider-track-h,6px);"+"border-radius:var(--x-slider-radius);"+"background:var(--x-slider-fill-color);}"+"[part=input]::-webkit-slider-thumb{"+"-webkit-appearance:none;"+"width:var(--_x-slider-thumb-sz,18px);"+"height:var(--_x-slider-thumb-sz,18px);"+"border-radius:50%;"+"background:var(--x-slider-thumb-color);"+"border:var(--x-slider-thumb-border);"+"box-shadow:var(--x-slider-thumb-shadow);"+"margin-top:calc(var(--_x-slider-track-h,6px)/2 - var(--_x-slider-thumb-sz,18px)/2);"+"cursor:pointer;"+"transition:box-shadow 100ms ease;}"+"[part=input]::-moz-range-thumb{"+"width:var(--_x-slider-thumb-sz,18px);"+"height:var(--_x-slider-thumb-sz,18px);"+"border-radius:50%;"+"background:var(--x-slider-thumb-color);"+"border:var(--x-slider-thumb-border);"+"box-shadow:var(--x-slider-thumb-shadow);"+"cursor:pointer;"+"transition:box-shadow 100ms ease;}"+"[part=input]:focus-visible::-webkit-slider-thumb{"+"box-shadow:var(--x-slider-thumb-shadow),0 0 0 3px var(--x-slider-focus-ring);}"+"[part=input]:focus-visible::-moz-range-thumb{"+"box-shadow:var(--x-slider-thumb-shadow),0 0 0 3px var(--x-slider-focus-ring);}"+":host([data-disabled]) [part=input]{"+"opacity:var(--x-slider-disabled-opacity);"+"cursor:default;"+"pointer-events:none;}"+":host([data-readonly]) [part=input]{"+"pointer-events:none;}"+"@media (prefers-reduced-motion:reduce){"+"[part=input]::-webkit-slider-thumb{transition:none;}"+"[part=input]::-moz-range-thumb{transition:none;}"+"}");
app.components.x_slider.x_slider.make_el = (function app$components$x_slider$x_slider$make_el(tag){
return document.createElement(tag);
});
app.components.x_slider.x_slider.set_attr_BANG_ = (function app$components$x_slider$x_slider$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_slider.x_slider.remove_attr_BANG_ = (function app$components$x_slider$x_slider$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_slider.x_slider.has_attr_QMARK_ = (function app$components$x_slider$x_slider$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_slider.x_slider.get_attr = (function app$components$x_slider$x_slider$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_slider.x_slider.set_bool_attr_BANG_ = (function app$components$x_slider$x_slider$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_slider.x_slider.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_slider.x_slider.remove_attr_BANG_(el,attr);
}
});
app.components.x_slider.x_slider.make_shadow_BANG_ = (function app$components$x_slider$x_slider$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_slider.x_slider.make_el("style");
var base_el = app.components.x_slider.x_slider.make_el("div");
var header_el = app.components.x_slider.x_slider.make_el("div");
var label_el = app.components.x_slider.x_slider.make_el("span");
var value_el = app.components.x_slider.x_slider.make_el("span");
var input_el = app.components.x_slider.x_slider.make_el("input");
(style_el.textContent = app.components.x_slider.x_slider.style_text);

app.components.x_slider.x_slider.set_attr_BANG_(base_el,"part","base");

app.components.x_slider.x_slider.set_attr_BANG_(header_el,"part","header");

app.components.x_slider.x_slider.set_attr_BANG_(label_el,"part","label-text");

app.components.x_slider.x_slider.set_attr_BANG_(value_el,"part","value-text");

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"part","input");

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"type","range");

header_el.appendChild(label_el);

header_el.appendChild(value_el);

base_el.appendChild(header_el);

base_el.appendChild(input_el);

root.appendChild(style_el);

root.appendChild(base_el);

var refs = ({"base": base_el, "header": header_el, "label": label_el, "value": value_el, "input": input_el});
app.components.x_slider.x_slider.goog$module$goog$object.set(el,app.components.x_slider.x_slider.k_refs,refs);

return refs;
});
app.components.x_slider.x_slider.read_model = (function app$components$x_slider$x_slider$read_model(el){
return app.components.x_slider.model.derive_state(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"show-value","show-value",-1560941240),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"readonly","readonly",-1101398934),new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),new cljs.core.Keyword(null,"step","step",1288888124)],[app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_min),app.components.x_slider.x_slider.has_attr_QMARK_(el,app.components.x_slider.model.attr_disabled),app.components.x_slider.x_slider.has_attr_QMARK_(el,app.components.x_slider.model.attr_show_value),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_name),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_value),app.components.x_slider.x_slider.has_attr_QMARK_(el,app.components.x_slider.model.attr_readonly),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_aria_labelledby),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_size),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_max),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_label),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_aria_describedby),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_aria_label),app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_step)]));
});
app.components.x_slider.x_slider.render_BANG_ = (function app$components$x_slider$x_slider$render_BANG_(el){
var temp__5823__auto__ = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var base_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"base");
var header_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"header");
var label_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"label");
var value_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"value");
var input_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"input");
var m = app.components.x_slider.x_slider.read_model(el);
var map__23068 = m;
var map__23068__$1 = cljs.core.__destructure_map(map__23068);
var aria_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var step = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"step","step",1288888124));
var fill_percent = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"fill-percent","fill-percent",-1813848228));
var min = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"min","min",444991522));
var readonly_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"readonly?","readonly?",988057827));
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"value","value",305978217));
var aria_labelledby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667));
var show_value_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"show-value?","show-value?",-1960833108));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"max","max",61366548));
var aria_describedby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23068__$1,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471));
var show_header_QMARK_ = (function (){var or__5142__auto__ = (!((label == null)));
if(or__5142__auto__){
return or__5142__auto__;
} else {
return show_value_QMARK_;
}
})();
app.components.x_slider.x_slider.set_attr_BANG_(base_el,"data-size",size);

app.components.x_slider.x_slider.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);

app.components.x_slider.x_slider.set_bool_attr_BANG_(el,"data-readonly",readonly_QMARK_);

(header_el.style.display = (cljs.core.truth_(show_header_QMARK_)?"flex":"none"));

(label_el.textContent = (function (){var or__5142__auto__ = label;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

(label_el.style.display = (((!((label == null))))?"":"none"));

(value_el.textContent = (cljs.core.truth_(show_value_QMARK_)?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)):""));

(value_el.style.display = (cljs.core.truth_(show_value_QMARK_)?"":"none"));

el.style.setProperty("--_x-slider-fill",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(fill_percent.toFixed((2)))+"%"));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"min",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(min)));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"max",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max)));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"step",step);

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(input_el.value,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)))){
(input_el.value = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));
} else {
}

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-valuemin",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(min)));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-valuemax",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max)));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-valuenow",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));

app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-readonly",(cljs.core.truth_(readonly_QMARK_)?"true":"false"));

var temp__5821__auto___23106 = aria_label;
if(cljs.core.truth_(temp__5821__auto___23106)){
var v_23107 = temp__5821__auto___23106;
app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-label",v_23107);
} else {
if((!((label == null)))){
app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-label",label);
} else {
app.components.x_slider.x_slider.remove_attr_BANG_(input_el,"aria-label");
}
}

var temp__5821__auto___23108 = aria_labelledby;
if(cljs.core.truth_(temp__5821__auto___23108)){
var v_23109 = temp__5821__auto___23108;
app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-labelledby",v_23109);
} else {
app.components.x_slider.x_slider.remove_attr_BANG_(input_el,"aria-labelledby");
}

var temp__5821__auto___23110 = aria_describedby;
if(cljs.core.truth_(temp__5821__auto___23110)){
var v_23111 = temp__5821__auto___23110;
app.components.x_slider.x_slider.set_attr_BANG_(input_el,"aria-describedby",v_23111);
} else {
app.components.x_slider.x_slider.remove_attr_BANG_(input_el,"aria-describedby");
}

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_slider.x_slider.set_attr_BANG_(input_el,"disabled","");
} else {
app.components.x_slider.x_slider.remove_attr_BANG_(input_el,"disabled");
}

var temp__5823__auto____$1 = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return internals.setFormValue((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value)));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_slider.x_slider.dispatch_BANG_ = (function app$components$x_slider$x_slider$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_slider.x_slider.make_input_handler = (function app$components$x_slider$x_slider$make_input_handler(el){
return (function (_evt){
var refs = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs);
var input_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"input");
var raw_val = input_el.value;
var num_val = parseFloat(raw_val);
var min_num = parseFloat((function (){var or__5142__auto__ = app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_min);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "0";
}
})());
var max_num = parseFloat((function (){var or__5142__auto__ = app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "100";
}
})());
app.components.x_slider.x_slider.set_attr_BANG_(el,app.components.x_slider.model.attr_value,raw_val);

return app.components.x_slider.x_slider.dispatch_BANG_(el,app.components.x_slider.model.event_input,({"value": num_val, "min": min_num, "max": max_num}));
});
});
app.components.x_slider.x_slider.make_change_handler = (function app$components$x_slider$x_slider$make_change_handler(el){
return (function (_evt){
var refs = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs);
var input_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"input");
var raw_val = input_el.value;
var num_val = parseFloat(raw_val);
var min_num = parseFloat((function (){var or__5142__auto__ = app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_min);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "0";
}
})());
var max_num = parseFloat((function (){var or__5142__auto__ = app.components.x_slider.x_slider.get_attr(el,app.components.x_slider.model.attr_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "100";
}
})());
return app.components.x_slider.x_slider.dispatch_BANG_(el,app.components.x_slider.model.event_change,({"value": num_val, "min": min_num, "max": max_num}));
});
});
app.components.x_slider.x_slider.make_keydown_handler = (function app$components$x_slider$x_slider$make_keydown_handler(el){
return (function (evt){
if(cljs.core.truth_((function (){var or__5142__auto__ = app.components.x_slider.x_slider.has_attr_QMARK_(el,app.components.x_slider.model.attr_disabled);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_slider.x_slider.has_attr_QMARK_(el,app.components.x_slider.model.attr_readonly);
}
})())){
evt.preventDefault();

return evt.stopPropagation();
} else {
return null;
}
});
});
app.components.x_slider.x_slider.add_listeners_BANG_ = (function app$components$x_slider$x_slider$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"input");
var input_h = app.components.x_slider.x_slider.make_input_handler(el);
var change_h = app.components.x_slider.x_slider.make_change_handler(el);
var keydown_h = app.components.x_slider.x_slider.make_keydown_handler(el);
var handlers = ({"input": input_h, "change": change_h, "keydown": keydown_h});
input_el.addEventListener("input",input_h);

input_el.addEventListener("change",change_h);

input_el.addEventListener("keydown",keydown_h);

return app.components.x_slider.x_slider.goog$module$goog$object.set(el,app.components.x_slider.x_slider.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_slider.x_slider.remove_listeners_BANG_ = (function app$components$x_slider$x_slider$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var input_el_23112 = app.components.x_slider.x_slider.goog$module$goog$object.get(refs,"input");
input_el_23112.removeEventListener("input",app.components.x_slider.x_slider.goog$module$goog$object.get(handlers,"input"));

input_el_23112.removeEventListener("change",app.components.x_slider.x_slider.goog$module$goog$object.get(handlers,"change"));

input_el_23112.removeEventListener("keydown",app.components.x_slider.x_slider.goog$module$goog$object.get(handlers,"keydown"));

return app.components.x_slider.x_slider.goog$module$goog$object.set(el,app.components.x_slider.x_slider.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_slider.x_slider.form_disabled_BANG_ = (function app$components$x_slider$x_slider$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_slider.x_slider.set_bool_attr_BANG_(el,app.components.x_slider.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_slider.x_slider.render_BANG_(el);
});
app.components.x_slider.x_slider.form_reset_BANG_ = (function app$components$x_slider$x_slider$form_reset_BANG_(el){
app.components.x_slider.x_slider.set_attr_BANG_(el,app.components.x_slider.model.attr_value,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_slider.model.default_value)));

return app.components.x_slider.x_slider.render_BANG_(el);
});
app.components.x_slider.x_slider.connected_BANG_ = (function app$components$x_slider$x_slider$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_slider.x_slider.goog$module$goog$object.get(el,app.components.x_slider.x_slider.k_refs))){
} else {
app.components.x_slider.x_slider.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_slider.x_slider.goog$module$goog$object.set(el,app.components.x_slider.x_slider.k_internals,el.attachInternals());
} else {
}
}

app.components.x_slider.x_slider.remove_listeners_BANG_(el);

app.components.x_slider.x_slider.add_listeners_BANG_(el);

return app.components.x_slider.x_slider.render_BANG_(el);
});
app.components.x_slider.x_slider.disconnected_BANG_ = (function app$components$x_slider$x_slider$disconnected_BANG_(el){
return app.components.x_slider.x_slider.remove_listeners_BANG_(el);
});
app.components.x_slider.x_slider.attribute_changed_BANG_ = (function app$components$x_slider$x_slider$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_slider.x_slider.render_BANG_(el);
});
app.components.x_slider.x_slider.define_bool_prop_BANG_ = (function app$components$x_slider$x_slider$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_slider.x_slider.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_slider.x_slider.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_slider.x_slider.define_string_prop_BANG_ = (function app$components$x_slider$x_slider$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_slider.x_slider.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_slider.x_slider.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_slider.x_slider.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_slider.x_slider.element_class = (function app$components$x_slider$x_slider$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_slider.model.observed_attributes;
})}));

app.components.x_slider.x_slider.define_bool_prop_BANG_(proto,"disabled",app.components.x_slider.model.attr_disabled);

app.components.x_slider.x_slider.define_bool_prop_BANG_(proto,"readOnly",app.components.x_slider.model.attr_readonly);

app.components.x_slider.x_slider.define_bool_prop_BANG_(proto,"showValue",app.components.x_slider.model.attr_show_value);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"value",app.components.x_slider.model.attr_value);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"min",app.components.x_slider.model.attr_min);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"max",app.components.x_slider.model.attr_max);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"step",app.components.x_slider.model.attr_step);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"name",app.components.x_slider.model.attr_name);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"label",app.components.x_slider.model.attr_label);

app.components.x_slider.x_slider.define_string_prop_BANG_(proto,"size",app.components.x_slider.model.attr_size);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_slider.x_slider.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_slider.x_slider.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_slider.x_slider.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_slider.x_slider.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_slider.x_slider.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_slider.x_slider.init_BANG_ = (function app$components$x_slider$x_slider$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_slider.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_slider.model.tag_name,app.components.x_slider.x_slider.element_class());
}
});

//# sourceMappingURL=app.components.x_slider.x_slider.js.map
