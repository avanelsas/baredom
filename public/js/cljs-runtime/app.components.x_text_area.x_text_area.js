goog.provide('app.components.x_text_area.x_text_area');
goog.scope(function(){
  app.components.x_text_area.x_text_area.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_text_area.x_text_area.k_refs = "__xTextAreaRefs";
app.components.x_text_area.x_text_area.k_internals = "__xTextAreaInternals";
app.components.x_text_area.x_text_area.k_handlers = "__xTextAreaHandlers";
app.components.x_text_area.x_text_area.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-text-area-label-color:#374151;"+"--x-text-area-label-font-size:0.875rem;"+"--x-text-area-bg:#ffffff;"+"--x-text-area-color:#111827;"+"--x-text-area-border:1px solid #d1d5db;"+"--x-text-area-border-radius:6px;"+"--x-text-area-padding:0.5rem 0.75rem;"+"--x-text-area-focus-ring-color:#2563eb;"+"--x-text-area-error-color:#dc2626;"+"--x-text-area-hint-color:#6b7280;"+"--x-text-area-disabled-opacity:0.45;"+"--x-text-area-min-height:5rem;"+"--x-text-area-font-size:1rem;"+"--x-text-area-resize:vertical;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-text-area-label-color:#d1d5db;"+"--x-text-area-bg:#1f2937;"+"--x-text-area-color:#f9fafb;"+"--x-text-area-border:1px solid #4b5563;"+"--x-text-area-focus-ring-color:#3b82f6;"+"--x-text-area-error-color:#f87171;"+"--x-text-area-hint-color:#9ca3af;"+"}"+"}"+"[part=field]{"+"display:flex;"+"flex-direction:column;"+"gap:0.25rem;"+"}"+"[part=label]{"+"display:block;"+"font-size:var(--x-text-area-label-font-size);"+"font-weight:500;"+"color:var(--x-text-area-label-color);"+"}"+".label-hidden{"+"display:none;"+"}"+"[part=textarea-wrapper]{"+"display:block;"+"}"+"[part=textarea]{"+"display:block;"+"width:100%;"+"box-sizing:border-box;"+"min-height:var(--x-text-area-min-height);"+"padding:var(--x-text-area-padding);"+"background:var(--x-text-area-bg);"+"color:var(--x-text-area-color);"+"border:var(--x-text-area-border);"+"border-radius:var(--x-text-area-border-radius);"+"font-size:var(--x-text-area-font-size);"+"line-height:1.5;"+"font-family:inherit;"+"outline:none;"+"resize:var(--x-text-area-resize);"+"transition:border-color 120ms ease,box-shadow 120ms ease;"+"}"+"[part=textarea]:focus{"+"border-color:var(--x-text-area-focus-ring-color);"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-text-area-focus-ring-color) 20%,transparent);"+"}"+":host([data-invalid]) [part=textarea]{"+"border-color:var(--x-text-area-error-color);"+"}"+":host([data-invalid]) [part=textarea]:focus{"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-text-area-error-color) 20%,transparent);"+"}"+"[part=textarea]:disabled{"+"opacity:var(--x-text-area-disabled-opacity);"+"cursor:not-allowed;"+"}"+"[part=hint]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-text-area-hint-color);"+"}"+".hint-hidden{"+"display:none;"+"}"+"[part=error]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-text-area-error-color);"+"}"+".error-hidden{"+"display:none;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=textarea]{transition:none;}"+"}");
app.components.x_text_area.x_text_area.make_el = (function app$components$x_text_area$x_text_area$make_el(tag){
return document.createElement(tag);
});
app.components.x_text_area.x_text_area.set_attr_BANG_ = (function app$components$x_text_area$x_text_area$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_text_area.x_text_area.remove_attr_BANG_ = (function app$components$x_text_area$x_text_area$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_text_area.x_text_area.has_attr_QMARK_ = (function app$components$x_text_area$x_text_area$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_text_area.x_text_area.get_attr = (function app$components$x_text_area$x_text_area$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_text_area.x_text_area.set_bool_attr_BANG_ = (function app$components$x_text_area$x_text_area$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_text_area.x_text_area.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_text_area.x_text_area.remove_attr_BANG_(el,attr);
}
});
app.components.x_text_area.x_text_area.make_shadow_BANG_ = (function app$components$x_text_area$x_text_area$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_text_area.x_text_area.make_el("style");
var field_el = app.components.x_text_area.x_text_area.make_el("div");
var label_el = app.components.x_text_area.x_text_area.make_el("label");
var wrapper_el = app.components.x_text_area.x_text_area.make_el("div");
var textarea_el = app.components.x_text_area.x_text_area.make_el("textarea");
var hint_el = app.components.x_text_area.x_text_area.make_el("span");
var error_el = app.components.x_text_area.x_text_area.make_el("span");
(style_el.textContent = app.components.x_text_area.x_text_area.style_text);

app.components.x_text_area.x_text_area.set_attr_BANG_(field_el,"part","field");

app.components.x_text_area.x_text_area.set_attr_BANG_(label_el,"part","label");

app.components.x_text_area.x_text_area.set_attr_BANG_(label_el,"id","label");

app.components.x_text_area.x_text_area.set_attr_BANG_(label_el,"for","textarea");

app.components.x_text_area.x_text_area.set_attr_BANG_(wrapper_el,"part","textarea-wrapper");

app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"part","textarea");

app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"id","textarea");

app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"aria-labelledby","label");

app.components.x_text_area.x_text_area.set_attr_BANG_(hint_el,"part","hint");

app.components.x_text_area.x_text_area.set_attr_BANG_(hint_el,"id","hint");

app.components.x_text_area.x_text_area.set_attr_BANG_(hint_el,"aria-live","polite");

app.components.x_text_area.x_text_area.set_attr_BANG_(error_el,"part","error");

app.components.x_text_area.x_text_area.set_attr_BANG_(error_el,"id","error");

app.components.x_text_area.x_text_area.set_attr_BANG_(error_el,"role","alert");

app.components.x_text_area.x_text_area.set_attr_BANG_(error_el,"aria-live","assertive");

wrapper_el.appendChild(textarea_el);

field_el.appendChild(label_el);

field_el.appendChild(wrapper_el);

field_el.appendChild(hint_el);

field_el.appendChild(error_el);

root.appendChild(style_el);

root.appendChild(field_el);

var refs = ({"field": field_el, "label": label_el, "textarea": textarea_el, "hint": hint_el, "error": error_el});
app.components.x_text_area.x_text_area.goog$module$goog$object.set(el,app.components.x_text_area.x_text_area.k_refs,refs);

return refs;
});
app.components.x_text_area.x_text_area.sync_validity_BANG_ = (function app$components$x_text_area$x_text_area$sync_validity_BANG_(el,internals,textarea_el){
var has_error_QMARK_ = app.components.x_text_area.x_text_area.has_attr_QMARK_(el,app.components.x_text_area.model.attr_error);
var error_msg = (function (){var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_error);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var required_QMARK_ = app.components.x_text_area.x_text_area.has_attr_QMARK_(el,app.components.x_text_area.model.attr_required);
var value = textarea_el.value;
if(cljs.core.truth_(has_error_QMARK_)){
return internals.setValidity(({"customError": true}),error_msg,textarea_el);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = required_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(value,"");
} else {
return and__5140__auto__;
}
})())){
return internals.setValidity(({"valueMissing": true}),"Please fill in this field.",textarea_el);
} else {
return internals.setValidity(({}),"",textarea_el);

}
}
});
app.components.x_text_area.x_text_area.read_model = (function app$components$x_text_area$x_text_area$read_model(el){
return app.components.x_text_area.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"resize-raw","resize-raw",-287517888),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"error-raw","error-raw",-1164358971),new cljs.core.Keyword(null,"hint-raw","hint-raw",-503443994),new cljs.core.Keyword(null,"rows-raw","rows-raw",-1828357145),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"maxlength-raw","maxlength-raw",-886656470),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710),new cljs.core.Keyword(null,"minlength-raw","minlength-raw",-1245403779),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657)],[app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_resize),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_label),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_name),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_error),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_hint),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_rows),app.components.x_text_area.x_text_area.has_attr_QMARK_(el,app.components.x_text_area.model.attr_disabled),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_maxlength),app.components.x_text_area.x_text_area.has_attr_QMARK_(el,app.components.x_text_area.model.attr_required),app.components.x_text_area.x_text_area.has_attr_QMARK_(el,app.components.x_text_area.model.attr_readonly),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_value),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_autocomplete),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_minlength),app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_placeholder)]));
});
app.components.x_text_area.x_text_area.render_BANG_ = (function app$components$x_text_area$x_text_area$render_BANG_(el){
var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var textarea_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
var label_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"label");
var hint_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"hint");
var error_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"error");
var m = app.components.x_text_area.x_text_area.read_model(el);
var has_error_QMARK_ = new cljs.core.Keyword(null,"has-error?","has-error?",1984278765).cljs$core$IFn$_invoke$arity$1(m);
var has_hint_QMARK_ = new cljs.core.Keyword(null,"has-hint?","has-hint?",-60700378).cljs$core$IFn$_invoke$arity$1(m);
var has_label_QMARK_ = new cljs.core.Keyword(null,"has-label?","has-label?",-1686997398).cljs$core$IFn$_invoke$arity$1(m);
(textarea_el.name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.disabled = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.readOnly = new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.required = new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.rows = new cljs.core.Keyword(null,"rows","rows",850049680).cljs$core$IFn$_invoke$arity$1(m));

(textarea_el.autocomplete = new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913).cljs$core$IFn$_invoke$arity$1(m));

var temp__5821__auto___23408 = new cljs.core.Keyword(null,"maxlength","maxlength",-1163911985).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23408)){
var maxlen_23409 = temp__5821__auto___23408;
app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"maxlength",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(maxlen_23409)));
} else {
app.components.x_text_area.x_text_area.remove_attr_BANG_(textarea_el,"maxlength");
}

var temp__5821__auto___23416 = new cljs.core.Keyword(null,"minlength","minlength",259053545).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23416)){
var minlen_23417 = temp__5821__auto___23416;
app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"minlength",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(minlen_23417)));
} else {
app.components.x_text_area.x_text_area.remove_attr_BANG_(textarea_el,"minlength");
}

el.style.setProperty("--x-text-area-resize",new cljs.core.Keyword(null,"resize","resize",297367086).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"aria-invalid",(cljs.core.truth_(has_error_QMARK_)?"true":"false"));

var describedby_23427 = (cljs.core.truth_((function (){var and__5140__auto__ = has_hint_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return has_error_QMARK_;
} else {
return and__5140__auto__;
}
})())?"hint error":(cljs.core.truth_(has_hint_QMARK_)?"hint":(cljs.core.truth_(has_error_QMARK_)?"error":null
)));
if(cljs.core.truth_(describedby_23427)){
app.components.x_text_area.x_text_area.set_attr_BANG_(textarea_el,"aria-describedby",describedby_23427);
} else {
app.components.x_text_area.x_text_area.remove_attr_BANG_(textarea_el,"aria-describedby");
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

app.components.x_text_area.x_text_area.set_bool_attr_BANG_(el,"data-invalid",has_error_QMARK_);

var temp__5823__auto____$1 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return app.components.x_text_area.x_text_area.sync_validity_BANG_(el,internals,textarea_el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_text_area.x_text_area.dispatch_BANG_ = (function app$components$x_text_area$x_text_area$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_text_area.x_text_area.make_input_handler = (function app$components$x_text_area$x_text_area$make_input_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var textarea_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
var value = textarea_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___23446__$1 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_internals);
if(cljs.core.truth_(temp__5823__auto___23446__$1)){
var internals_23448 = temp__5823__auto___23446__$1;
internals_23448.setFormValue(value);

app.components.x_text_area.x_text_area.sync_validity_BANG_(el,internals_23448,textarea_el);
} else {
}

return app.components.x_text_area.x_text_area.dispatch_BANG_(el,app.components.x_text_area.model.event_input,({"name": name, "value": value}));
} else {
return null;
}
});
});
app.components.x_text_area.x_text_area.make_change_handler = (function app$components$x_text_area$x_text_area$make_change_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var textarea_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
var value = textarea_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___23449__$1 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_internals);
if(cljs.core.truth_(temp__5823__auto___23449__$1)){
var internals_23450 = temp__5823__auto___23449__$1;
internals_23450.setFormValue(value);

app.components.x_text_area.x_text_area.sync_validity_BANG_(el,internals_23450,textarea_el);
} else {
}

return app.components.x_text_area.x_text_area.dispatch_BANG_(el,app.components.x_text_area.model.event_change,({"name": name, "value": value}));
} else {
return null;
}
});
});
app.components.x_text_area.x_text_area.add_listeners_BANG_ = (function app$components$x_text_area$x_text_area$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var textarea_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
var input_h = app.components.x_text_area.x_text_area.make_input_handler(el);
var change_h = app.components.x_text_area.x_text_area.make_change_handler(el);
textarea_el.addEventListener("input",input_h);

textarea_el.addEventListener("change",change_h);

return app.components.x_text_area.x_text_area.goog$module$goog$object.set(el,app.components.x_text_area.x_text_area.k_handlers,({"input": input_h, "change": change_h}));
} else {
return null;
}
});
app.components.x_text_area.x_text_area.remove_listeners_BANG_ = (function app$components$x_text_area$x_text_area$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var textarea_el_23458 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
textarea_el_23458.removeEventListener("input",app.components.x_text_area.x_text_area.goog$module$goog$object.get(handlers,"input"));

textarea_el_23458.removeEventListener("change",app.components.x_text_area.x_text_area.goog$module$goog$object.get(handlers,"change"));

return app.components.x_text_area.x_text_area.goog$module$goog$object.set(el,app.components.x_text_area.x_text_area.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_text_area.x_text_area.form_disabled_BANG_ = (function app$components$x_text_area$x_text_area$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_text_area.x_text_area.set_bool_attr_BANG_(el,app.components.x_text_area.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_text_area.x_text_area.render_BANG_(el);
});
app.components.x_text_area.x_text_area.form_reset_BANG_ = (function app$components$x_text_area$x_text_area$form_reset_BANG_(el){
app.components.x_text_area.x_text_area.remove_attr_BANG_(el,app.components.x_text_area.model.attr_value);

var temp__5823__auto___23462 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto___23462)){
var refs_23463 = temp__5823__auto___23462;
var textarea_el_23464 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs_23463,"textarea");
(textarea_el_23464.value = "");
} else {
}

var temp__5823__auto___23465 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_internals);
if(cljs.core.truth_(temp__5823__auto___23465)){
var internals_23466 = temp__5823__auto___23465;
internals_23466.setFormValue("");
} else {
}

return app.components.x_text_area.x_text_area.render_BANG_(el);
});
app.components.x_text_area.x_text_area.connected_BANG_ = (function app$components$x_text_area$x_text_area$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs))){
} else {
app.components.x_text_area.x_text_area.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_text_area.x_text_area.goog$module$goog$object.set(el,app.components.x_text_area.x_text_area.k_internals,el.attachInternals());
} else {
}
}

app.components.x_text_area.x_text_area.remove_listeners_BANG_(el);

var temp__5823__auto___23467 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto___23467)){
var refs_23468 = temp__5823__auto___23467;
var textarea_el_23469 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs_23468,"textarea");
var val_attr_23470 = app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_value);
if(cljs.core.truth_(val_attr_23470)){
(textarea_el_23469.value = val_attr_23470);
} else {
}
} else {
}

var temp__5823__auto___23471 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_internals);
if(cljs.core.truth_(temp__5823__auto___23471)){
var internals_23472 = temp__5823__auto___23471;
internals_23472.setFormValue((function (){var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(el,app.components.x_text_area.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
}

app.components.x_text_area.x_text_area.add_listeners_BANG_(el);

return app.components.x_text_area.x_text_area.render_BANG_(el);
});
app.components.x_text_area.x_text_area.disconnected_BANG_ = (function app$components$x_text_area$x_text_area$disconnected_BANG_(el){
return app.components.x_text_area.x_text_area.remove_listeners_BANG_(el);
});
app.components.x_text_area.x_text_area.attribute_changed_BANG_ = (function app$components$x_text_area$x_text_area$attribute_changed_BANG_(el,name,_old,new_val){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_text_area.model.attr_value)){
var temp__5823__auto___23473 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(el,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto___23473)){
var refs_23474 = temp__5823__auto___23473;
var textarea_el_23475 = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs_23474,"textarea");
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(textarea_el_23475.value,new_val)){
(textarea_el_23475.value = (function (){var or__5142__auto__ = new_val;
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

return app.components.x_text_area.x_text_area.render_BANG_(el);
});
app.components.x_text_area.x_text_area.define_string_prop_BANG_ = (function app$components$x_text_area$x_text_area$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_text_area.x_text_area.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_text_area.x_text_area.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_text_area.x_text_area.define_bool_prop_BANG_ = (function app$components$x_text_area$x_text_area$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_text_area.x_text_area.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_text_area.x_text_area.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_text_area.x_text_area.define_int_prop_BANG_ = (function app$components$x_text_area$x_text_area$define_int_prop_BANG_(proto,prop_name,attr_name,default_val){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_text_area.model.parse_positive_int(app.components.x_text_area.x_text_area.get_attr(this$,attr_name));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default_val;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)) && ((v > (0))))))){
return app.components.x_text_area.x_text_area.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.floor(v))));
} else {
return app.components.x_text_area.x_text_area.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_text_area.x_text_area.define_value_prop_BANG_ = (function app$components$x_text_area$x_text_area$define_value_prop_BANG_(proto){
return Object.defineProperty(proto,"value",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(this$,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5821__auto__)){
var refs = temp__5821__auto__;
return app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea").value;
} else {
var or__5142__auto__ = app.components.x_text_area.x_text_area.get_attr(this$,app.components.x_text_area.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}
}), "set": (function (v){
var this$ = this;
var str_v = (((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined))))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)):"");
app.components.x_text_area.x_text_area.set_attr_BANG_(this$,app.components.x_text_area.model.attr_value,str_v);

var temp__5823__auto__ = app.components.x_text_area.x_text_area.goog$module$goog$object.get(this$,app.components.x_text_area.x_text_area.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var textarea_el = app.components.x_text_area.x_text_area.goog$module$goog$object.get(refs,"textarea");
return (textarea_el.value = str_v);
} else {
return null;
}
})}));
});
app.components.x_text_area.x_text_area.element_class = (function app$components$x_text_area$x_text_area$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_text_area.model.observed_attributes;
})}));

app.components.x_text_area.x_text_area.define_value_prop_BANG_(proto);

app.components.x_text_area.x_text_area.define_string_prop_BANG_(proto,"name",app.components.x_text_area.model.attr_name);

app.components.x_text_area.x_text_area.define_string_prop_BANG_(proto,"placeholder",app.components.x_text_area.model.attr_placeholder);

app.components.x_text_area.x_text_area.define_string_prop_BANG_(proto,"autocomplete",app.components.x_text_area.model.attr_autocomplete);

app.components.x_text_area.x_text_area.define_bool_prop_BANG_(proto,"disabled",app.components.x_text_area.model.attr_disabled);

app.components.x_text_area.x_text_area.define_bool_prop_BANG_(proto,"readOnly",app.components.x_text_area.model.attr_readonly);

app.components.x_text_area.x_text_area.define_bool_prop_BANG_(proto,"required",app.components.x_text_area.model.attr_required);

app.components.x_text_area.x_text_area.define_int_prop_BANG_(proto,"rows",app.components.x_text_area.model.attr_rows,(3));

app.components.x_text_area.x_text_area.define_int_prop_BANG_(proto,"maxLength",app.components.x_text_area.model.attr_maxlength,null);

app.components.x_text_area.x_text_area.define_int_prop_BANG_(proto,"minLength",app.components.x_text_area.model.attr_minlength,null);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_text_area.x_text_area.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_text_area.x_text_area.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_text_area.x_text_area.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_text_area.x_text_area.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_text_area.x_text_area.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_text_area.x_text_area.init_BANG_ = (function app$components$x_text_area$x_text_area$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_text_area.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_text_area.model.tag_name,app.components.x_text_area.x_text_area.element_class());
}
});

//# sourceMappingURL=app.components.x_text_area.x_text_area.js.map
