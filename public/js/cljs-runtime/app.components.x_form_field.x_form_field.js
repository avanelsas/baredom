goog.provide('app.components.x_form_field.x_form_field');
goog.scope(function(){
  app.components.x_form_field.x_form_field.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_form_field.x_form_field.k_refs = "__xFormFieldRefs";
app.components.x_form_field.x_form_field.k_internals = "__xFormFieldInternals";
app.components.x_form_field.x_form_field.k_handlers = "__xFormFieldHandlers";
app.components.x_form_field.x_form_field.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-form-field-label-color:#374151;"+"--x-form-field-label-font-size:0.875rem;"+"--x-form-field-input-bg:#ffffff;"+"--x-form-field-input-color:#111827;"+"--x-form-field-input-border:1px solid #d1d5db;"+"--x-form-field-input-border-radius:6px;"+"--x-form-field-input-padding:0.5rem 0.75rem;"+"--x-form-field-focus-ring-color:#2563eb;"+"--x-form-field-error-color:#dc2626;"+"--x-form-field-hint-color:#6b7280;"+"--x-form-field-disabled-opacity:0.45;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-form-field-label-color:#d1d5db;"+"--x-form-field-input-bg:#1f2937;"+"--x-form-field-input-color:#f9fafb;"+"--x-form-field-input-border:1px solid #4b5563;"+"--x-form-field-focus-ring-color:#3b82f6;"+"--x-form-field-error-color:#f87171;"+"--x-form-field-hint-color:#9ca3af;"+"}"+"}"+"[part=field]{"+"display:flex;"+"flex-direction:column;"+"gap:0.25rem;"+"}"+"[part=label]{"+"display:block;"+"font-size:var(--x-form-field-label-font-size);"+"font-weight:500;"+"color:var(--x-form-field-label-color);"+"}"+".label-hidden{"+"display:none;"+"}"+"[part=input-wrapper]{"+"display:block;"+"}"+"[part=input]{"+"display:block;"+"width:100%;"+"box-sizing:border-box;"+"padding:var(--x-form-field-input-padding);"+"background:var(--x-form-field-input-bg);"+"color:var(--x-form-field-input-color);"+"border:var(--x-form-field-input-border);"+"border-radius:var(--x-form-field-input-border-radius);"+"font-size:1rem;"+"line-height:1.5;"+"font-family:inherit;"+"outline:none;"+"transition:border-color 120ms ease,box-shadow 120ms ease;"+"}"+"[part=input]:focus{"+"border-color:var(--x-form-field-focus-ring-color);"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-focus-ring-color) 20%,transparent);"+"}"+":host([data-invalid]) [part=input]{"+"border-color:var(--x-form-field-error-color);"+"}"+":host([data-invalid]) [part=input]:focus{"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-error-color) 20%,transparent);"+"}"+"[part=input]:disabled{"+"opacity:var(--x-form-field-disabled-opacity);"+"cursor:not-allowed;"+"}"+"[part=hint]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-form-field-hint-color);"+"}"+".hint-hidden{"+"display:none;"+"}"+"[part=error]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-form-field-error-color);"+"}"+".error-hidden{"+"display:none;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=input]{transition:none;}"+"}");
app.components.x_form_field.x_form_field.make_el = (function app$components$x_form_field$x_form_field$make_el(tag){
return document.createElement(tag);
});
app.components.x_form_field.x_form_field.set_attr_BANG_ = (function app$components$x_form_field$x_form_field$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_form_field.x_form_field.remove_attr_BANG_ = (function app$components$x_form_field$x_form_field$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_form_field.x_form_field.has_attr_QMARK_ = (function app$components$x_form_field$x_form_field$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_form_field.x_form_field.get_attr = (function app$components$x_form_field$x_form_field$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_form_field.x_form_field.set_bool_attr_BANG_ = (function app$components$x_form_field$x_form_field$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_form_field.x_form_field.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_form_field.x_form_field.remove_attr_BANG_(el,attr);
}
});
app.components.x_form_field.x_form_field.make_shadow_BANG_ = (function app$components$x_form_field$x_form_field$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_form_field.x_form_field.make_el("style");
var field_el = app.components.x_form_field.x_form_field.make_el("div");
var label_el = app.components.x_form_field.x_form_field.make_el("label");
var wrapper_el = app.components.x_form_field.x_form_field.make_el("div");
var input_el = app.components.x_form_field.x_form_field.make_el("input");
var hint_el = app.components.x_form_field.x_form_field.make_el("span");
var error_el = app.components.x_form_field.x_form_field.make_el("span");
(style_el.textContent = app.components.x_form_field.x_form_field.style_text);

app.components.x_form_field.x_form_field.set_attr_BANG_(field_el,"part","field");

app.components.x_form_field.x_form_field.set_attr_BANG_(label_el,"part","label");

app.components.x_form_field.x_form_field.set_attr_BANG_(label_el,"id","label");

app.components.x_form_field.x_form_field.set_attr_BANG_(label_el,"for","input");

app.components.x_form_field.x_form_field.set_attr_BANG_(wrapper_el,"part","input-wrapper");

app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"part","input");

app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"id","input");

app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"aria-labelledby","label");

app.components.x_form_field.x_form_field.set_attr_BANG_(hint_el,"part","hint");

app.components.x_form_field.x_form_field.set_attr_BANG_(hint_el,"id","hint");

app.components.x_form_field.x_form_field.set_attr_BANG_(hint_el,"aria-live","polite");

app.components.x_form_field.x_form_field.set_attr_BANG_(error_el,"part","error");

app.components.x_form_field.x_form_field.set_attr_BANG_(error_el,"id","error");

app.components.x_form_field.x_form_field.set_attr_BANG_(error_el,"role","alert");

app.components.x_form_field.x_form_field.set_attr_BANG_(error_el,"aria-live","assertive");

wrapper_el.appendChild(input_el);

field_el.appendChild(label_el);

field_el.appendChild(wrapper_el);

field_el.appendChild(hint_el);

field_el.appendChild(error_el);

root.appendChild(style_el);

root.appendChild(field_el);

var refs = ({"field": field_el, "label": label_el, "input": input_el, "hint": hint_el, "error": error_el});
app.components.x_form_field.x_form_field.goog$module$goog$object.set(el,app.components.x_form_field.x_form_field.k_refs,refs);

return refs;
});
app.components.x_form_field.x_form_field.sync_validity_BANG_ = (function app$components$x_form_field$x_form_field$sync_validity_BANG_(el,internals,input_el){
var has_error_QMARK_ = app.components.x_form_field.x_form_field.has_attr_QMARK_(el,app.components.x_form_field.model.attr_error);
var error_msg = (function (){var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_error);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var required_QMARK_ = app.components.x_form_field.x_form_field.has_attr_QMARK_(el,app.components.x_form_field.model.attr_required);
var value = input_el.value;
if(cljs.core.truth_(has_error_QMARK_)){
return internals.setValidity(({"customError": true}),error_msg,input_el);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = required_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(value,"");
} else {
return and__5140__auto__;
}
})())){
return internals.setValidity(({"valueMissing": true}),"Please fill in this field.",input_el);
} else {
return internals.setValidity(({}),"",input_el);

}
}
});
app.components.x_form_field.x_form_field.read_model = (function app$components$x_form_field$x_form_field$read_model(el){
return app.components.x_form_field.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"error-raw","error-raw",-1164358971),new cljs.core.Keyword(null,"hint-raw","hint-raw",-503443994),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710),new cljs.core.Keyword(null,"type-raw","type-raw",-967209994),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657)],[app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_label),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_name),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_error),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_hint),app.components.x_form_field.x_form_field.has_attr_QMARK_(el,app.components.x_form_field.model.attr_disabled),app.components.x_form_field.x_form_field.has_attr_QMARK_(el,app.components.x_form_field.model.attr_required),app.components.x_form_field.x_form_field.has_attr_QMARK_(el,app.components.x_form_field.model.attr_readonly),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_value),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_autocomplete),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_type),app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_placeholder)]));
});
app.components.x_form_field.x_form_field.render_BANG_ = (function app$components$x_form_field$x_form_field$render_BANG_(el){
var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var label_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"label");
var input_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
var hint_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"hint");
var error_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"error");
var m = app.components.x_form_field.x_form_field.read_model(el);
var has_error_QMARK_ = new cljs.core.Keyword(null,"has-error?","has-error?",1984278765).cljs$core$IFn$_invoke$arity$1(m);
var has_hint_QMARK_ = new cljs.core.Keyword(null,"has-hint?","has-hint?",-60700378).cljs$core$IFn$_invoke$arity$1(m);
var has_label_QMARK_ = new cljs.core.Keyword(null,"has-label?","has-label?",-1686997398).cljs$core$IFn$_invoke$arity$1(m);
(input_el.type = new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(m));

(input_el.name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m));

(input_el.placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m));

(input_el.disabled = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m));

(input_el.readOnly = new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m));

(input_el.required = new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m));

(input_el.autocomplete = new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"aria-invalid",(cljs.core.truth_(has_error_QMARK_)?"true":"false"));

var describedby_22696 = (cljs.core.truth_((function (){var and__5140__auto__ = has_hint_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return has_error_QMARK_;
} else {
return and__5140__auto__;
}
})())?"hint error":(cljs.core.truth_(has_hint_QMARK_)?"hint":(cljs.core.truth_(has_error_QMARK_)?"error":null
)));
if(cljs.core.truth_(describedby_22696)){
app.components.x_form_field.x_form_field.set_attr_BANG_(input_el,"aria-describedby",describedby_22696);
} else {
app.components.x_form_field.x_form_field.remove_attr_BANG_(input_el,"aria-describedby");
}

(label_el.textContent = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(has_label_QMARK_)){
label_el.classList.remove("label-hidden");
} else {
label_el.classList.add("label-hidden");
}

(hint_el.textContent = new cljs.core.Keyword(null,"hint","hint",439639918).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(has_hint_QMARK_)){
hint_el.classList.remove("hint-hidden");
} else {
hint_el.classList.add("hint-hidden");
}

(error_el.textContent = new cljs.core.Keyword(null,"error","error",-978969032).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(has_error_QMARK_)){
error_el.classList.remove("error-hidden");
} else {
error_el.classList.add("error-hidden");
}

app.components.x_form_field.x_form_field.set_bool_attr_BANG_(el,"data-invalid",has_error_QMARK_);

var temp__5823__auto____$1 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return app.components.x_form_field.x_form_field.sync_validity_BANG_(el,internals,input_el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_form_field.x_form_field.dispatch_BANG_ = (function app$components$x_form_field$x_form_field$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_form_field.x_form_field.make_input_handler = (function app$components$x_form_field$x_form_field$make_input_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___22697__$1 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22697__$1)){
var internals_22698 = temp__5823__auto___22697__$1;
internals_22698.setFormValue(value);

app.components.x_form_field.x_form_field.sync_validity_BANG_(el,internals_22698,input_el);
} else {
}

return app.components.x_form_field.x_form_field.dispatch_BANG_(el,app.components.x_form_field.model.event_input,({"name": name, "value": value}));
} else {
return null;
}
});
});
app.components.x_form_field.x_form_field.make_change_handler = (function app$components$x_form_field$x_form_field$make_change_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___22699__$1 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22699__$1)){
var internals_22700 = temp__5823__auto___22699__$1;
internals_22700.setFormValue(value);

app.components.x_form_field.x_form_field.sync_validity_BANG_(el,internals_22700,input_el);
} else {
}

return app.components.x_form_field.x_form_field.dispatch_BANG_(el,app.components.x_form_field.model.event_change,({"name": name, "value": value}));
} else {
return null;
}
});
});
app.components.x_form_field.x_form_field.add_listeners_BANG_ = (function app$components$x_form_field$x_form_field$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
var input_h = app.components.x_form_field.x_form_field.make_input_handler(el);
var change_h = app.components.x_form_field.x_form_field.make_change_handler(el);
input_el.addEventListener("input",input_h);

input_el.addEventListener("change",change_h);

return app.components.x_form_field.x_form_field.goog$module$goog$object.set(el,app.components.x_form_field.x_form_field.k_handlers,({"input": input_h, "change": change_h}));
} else {
return null;
}
});
app.components.x_form_field.x_form_field.remove_listeners_BANG_ = (function app$components$x_form_field$x_form_field$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var input_el_22701 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
input_el_22701.removeEventListener("input",app.components.x_form_field.x_form_field.goog$module$goog$object.get(handlers,"input"));

input_el_22701.removeEventListener("change",app.components.x_form_field.x_form_field.goog$module$goog$object.get(handlers,"change"));

return app.components.x_form_field.x_form_field.goog$module$goog$object.set(el,app.components.x_form_field.x_form_field.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_form_field.x_form_field.form_disabled_BANG_ = (function app$components$x_form_field$x_form_field$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_form_field.x_form_field.set_bool_attr_BANG_(el,app.components.x_form_field.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_form_field.x_form_field.render_BANG_(el);
});
app.components.x_form_field.x_form_field.form_reset_BANG_ = (function app$components$x_form_field$x_form_field$form_reset_BANG_(el){
app.components.x_form_field.x_form_field.remove_attr_BANG_(el,app.components.x_form_field.model.attr_value);

var temp__5823__auto___22704 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22704)){
var refs_22705 = temp__5823__auto___22704;
var input_el_22706 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs_22705,"input");
(input_el_22706.value = "");
} else {
}

var temp__5823__auto___22707 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22707)){
var internals_22708 = temp__5823__auto___22707;
internals_22708.setFormValue("");
} else {
}

return app.components.x_form_field.x_form_field.render_BANG_(el);
});
app.components.x_form_field.x_form_field.connected_BANG_ = (function app$components$x_form_field$x_form_field$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs))){
} else {
app.components.x_form_field.x_form_field.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_form_field.x_form_field.goog$module$goog$object.set(el,app.components.x_form_field.x_form_field.k_internals,el.attachInternals());
} else {
}
}

app.components.x_form_field.x_form_field.remove_listeners_BANG_(el);

var temp__5823__auto___22709 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22709)){
var refs_22710 = temp__5823__auto___22709;
var input_el_22711 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs_22710,"input");
var val_attr_22712 = app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_value);
if(cljs.core.truth_(val_attr_22712)){
(input_el_22711.value = val_attr_22712);
} else {
}
} else {
}

var temp__5823__auto___22713 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22713)){
var internals_22714 = temp__5823__auto___22713;
internals_22714.setFormValue((function (){var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(el,app.components.x_form_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
}

app.components.x_form_field.x_form_field.add_listeners_BANG_(el);

return app.components.x_form_field.x_form_field.render_BANG_(el);
});
app.components.x_form_field.x_form_field.disconnected_BANG_ = (function app$components$x_form_field$x_form_field$disconnected_BANG_(el){
return app.components.x_form_field.x_form_field.remove_listeners_BANG_(el);
});
app.components.x_form_field.x_form_field.attribute_changed_BANG_ = (function app$components$x_form_field$x_form_field$attribute_changed_BANG_(el,name,_old,new_val){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_form_field.model.attr_value)){
var temp__5823__auto___22715 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(el,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22715)){
var refs_22716 = temp__5823__auto___22715;
var input_el_22717 = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs_22716,"input");
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(input_el_22717.value,new_val)){
(input_el_22717.value = (function (){var or__5142__auto__ = new_val;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
}
} else {
}
} else {
}

return app.components.x_form_field.x_form_field.render_BANG_(el);
});
app.components.x_form_field.x_form_field.define_string_prop_BANG_ = (function app$components$x_form_field$x_form_field$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_form_field.x_form_field.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_form_field.x_form_field.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_form_field.x_form_field.define_bool_prop_BANG_ = (function app$components$x_form_field$x_form_field$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_form_field.x_form_field.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_form_field.x_form_field.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_form_field.x_form_field.define_value_prop_BANG_ = (function app$components$x_form_field$x_form_field$define_value_prop_BANG_(proto){
return Object.defineProperty(proto,"value",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(this$,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5821__auto__)){
var refs = temp__5821__auto__;
return app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input").value;
} else {
var or__5142__auto__ = app.components.x_form_field.x_form_field.get_attr(this$,app.components.x_form_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}
}), "set": (function (v){
var this$ = this;
var str_v = (((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined))))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)):"");
app.components.x_form_field.x_form_field.set_attr_BANG_(this$,app.components.x_form_field.model.attr_value,str_v);

var temp__5823__auto__ = app.components.x_form_field.x_form_field.goog$module$goog$object.get(this$,app.components.x_form_field.x_form_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_form_field.x_form_field.goog$module$goog$object.get(refs,"input");
return (input_el.value = str_v);
} else {
return null;
}
})}));
});
app.components.x_form_field.x_form_field.element_class = (function app$components$x_form_field$x_form_field$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_form_field.model.observed_attributes;
})}));

app.components.x_form_field.x_form_field.define_value_prop_BANG_(proto);

app.components.x_form_field.x_form_field.define_string_prop_BANG_(proto,"name",app.components.x_form_field.model.attr_name);

app.components.x_form_field.x_form_field.define_string_prop_BANG_(proto,"placeholder",app.components.x_form_field.model.attr_placeholder);

app.components.x_form_field.x_form_field.define_string_prop_BANG_(proto,"autocomplete",app.components.x_form_field.model.attr_autocomplete);

app.components.x_form_field.x_form_field.define_bool_prop_BANG_(proto,"disabled",app.components.x_form_field.model.attr_disabled);

app.components.x_form_field.x_form_field.define_bool_prop_BANG_(proto,"readOnly",app.components.x_form_field.model.attr_readonly);

app.components.x_form_field.x_form_field.define_bool_prop_BANG_(proto,"required",app.components.x_form_field.model.attr_required);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_form_field.x_form_field.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_form_field.x_form_field.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_form_field.x_form_field.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_form_field.x_form_field.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_form_field.x_form_field.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_form_field.x_form_field.init_BANG_ = (function app$components$x_form_field$x_form_field$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_form_field.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_form_field.model.tag_name,app.components.x_form_field.x_form_field.element_class());
}
});

//# sourceMappingURL=app.components.x_form_field.x_form_field.js.map
