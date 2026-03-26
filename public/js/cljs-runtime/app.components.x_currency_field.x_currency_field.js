goog.provide('app.components.x_currency_field.x_currency_field');
goog.scope(function(){
  app.components.x_currency_field.x_currency_field.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_currency_field.x_currency_field.k_refs = "__xCurrencyFieldRefs";
app.components.x_currency_field.x_currency_field.k_internals = "__xCurrencyFieldInternals";
app.components.x_currency_field.x_currency_field.k_handlers = "__xCurrencyFieldHandlers";
app.components.x_currency_field.x_currency_field.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-currency-field-bg:#ffffff;"+"--x-currency-field-color:#111827;"+"--x-currency-field-border:1px solid #d1d5db;"+"--x-currency-field-border-radius:6px;"+"--x-currency-field-focus-ring-color:#2563eb;"+"--x-currency-field-symbol-color:#6b7280;"+"--x-currency-field-label-color:#374151;"+"--x-currency-field-hint-color:#6b7280;"+"--x-currency-field-error-color:#dc2626;"+"--x-currency-field-disabled-opacity:0.45;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-currency-field-bg:#1f2937;"+"--x-currency-field-color:#f9fafb;"+"--x-currency-field-border:1px solid #4b5563;"+"--x-currency-field-focus-ring-color:#3b82f6;"+"--x-currency-field-symbol-color:#9ca3af;"+"--x-currency-field-label-color:#d1d5db;"+"--x-currency-field-hint-color:#9ca3af;"+"--x-currency-field-error-color:#f87171;"+"}"+"}"+"[part=field]{"+"display:flex;"+"flex-direction:column;"+"gap:0.25rem;"+"}"+"[part=label]{"+"display:block;"+"font-size:0.875rem;"+"font-weight:500;"+"color:var(--x-currency-field-label-color);"+"}"+".label-hidden{"+"display:none;"+"}"+"[part=input-wrapper]{"+"display:flex;"+"align-items:center;"+"background:var(--x-currency-field-bg);"+"border:var(--x-currency-field-border);"+"border-radius:var(--x-currency-field-border-radius);"+"transition:border-color 120ms ease,box-shadow 120ms ease;"+"}"+"[part=input-wrapper]:focus-within{"+"border-color:var(--x-currency-field-focus-ring-color);"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-focus-ring-color) 20%,transparent);"+"}"+":host([data-invalid]) [part=input-wrapper]{"+"border-color:var(--x-currency-field-error-color);"+"}"+":host([data-invalid]) [part=input-wrapper]:focus-within{"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-error-color) 20%,transparent);"+"}"+"[part=symbol]{"+"flex-shrink:0;"+"padding:0.5rem 0 0.5rem 0.75rem;"+"font-size:1rem;"+"color:var(--x-currency-field-symbol-color);"+"user-select:none;"+"pointer-events:none;"+"}"+"[part=input]{"+"flex:1;"+"min-width:0;"+"padding:0.5rem 0.75rem;"+"background:transparent;"+"color:var(--x-currency-field-color);"+"border:none;"+"outline:none;"+"font-size:1rem;"+"line-height:1.5;"+"font-family:inherit;"+"}"+"[part=input]:disabled{"+"opacity:1;"+"cursor:not-allowed;"+"}"+":host([disabled]) [part=input-wrapper]{"+"opacity:var(--x-currency-field-disabled-opacity);"+"cursor:not-allowed;"+"}"+"[part=hint]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-currency-field-hint-color);"+"}"+".hint-hidden{"+"display:none;"+"}"+"[part=error]{"+"display:block;"+"font-size:0.8125rem;"+"color:var(--x-currency-field-error-color);"+"}"+".error-hidden{"+"display:none;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=input-wrapper]{transition:none;}"+"}");
app.components.x_currency_field.x_currency_field.make_el = (function app$components$x_currency_field$x_currency_field$make_el(tag){
return document.createElement(tag);
});
app.components.x_currency_field.x_currency_field.set_attr_BANG_ = (function app$components$x_currency_field$x_currency_field$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_currency_field.x_currency_field.remove_attr_BANG_ = (function app$components$x_currency_field$x_currency_field$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_currency_field.x_currency_field.has_attr_QMARK_ = (function app$components$x_currency_field$x_currency_field$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_currency_field.x_currency_field.get_attr = (function app$components$x_currency_field$x_currency_field$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_currency_field.x_currency_field.set_bool_attr_BANG_ = (function app$components$x_currency_field$x_currency_field$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_currency_field.x_currency_field.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_currency_field.x_currency_field.remove_attr_BANG_(el,attr);
}
});
app.components.x_currency_field.x_currency_field.currency_symbol = (function app$components$x_currency_field$x_currency_field$currency_symbol(currency,locale){
try{var fmt = (new Intl.NumberFormat(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(locale,""))?"en-US":locale),({"style": "currency", "currency": currency})));
var parts = fmt.formatToParts((0));
var sym = cljs.core.some((function (p1__22720_SHARP_){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("currency",p1__22720_SHARP_.type)){
return p1__22720_SHARP_.value;
} else {
return null;
}
}),cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(parts));
var or__5142__auto__ = sym;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return currency;
}
}catch (e22721){var _ = e22721;
return currency;
}});
app.components.x_currency_field.x_currency_field.format_display = (function app$components$x_currency_field$x_currency_field$format_display(value,currency,locale){
var num = parseFloat(value);
if(cljs.core.truth_(isNaN(num))){
return value;
} else {
try{return (new Intl.NumberFormat(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(locale,""))?undefined:locale),({"minimumFractionDigits": (2), "maximumFractionDigits": (2)}))).format(num);
}catch (e22722){var _ = e22722;
return value;
}}
});
app.components.x_currency_field.x_currency_field.is_focused_QMARK_ = (function app$components$x_currency_field$x_currency_field$is_focused_QMARK_(el,input_el){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(input_el,el.shadowRoot.activeElement);
});
app.components.x_currency_field.x_currency_field.make_shadow_BANG_ = (function app$components$x_currency_field$x_currency_field$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_currency_field.x_currency_field.make_el("style");
var field_el = app.components.x_currency_field.x_currency_field.make_el("div");
var label_el = app.components.x_currency_field.x_currency_field.make_el("label");
var wrapper_el = app.components.x_currency_field.x_currency_field.make_el("div");
var symbol_el = app.components.x_currency_field.x_currency_field.make_el("span");
var input_el = app.components.x_currency_field.x_currency_field.make_el("input");
var hint_el = app.components.x_currency_field.x_currency_field.make_el("span");
var error_el = app.components.x_currency_field.x_currency_field.make_el("span");
(style_el.textContent = app.components.x_currency_field.x_currency_field.style_text);

app.components.x_currency_field.x_currency_field.set_attr_BANG_(field_el,"part","field");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(label_el,"part","label");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(label_el,"id","label");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(label_el,"for","input");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(wrapper_el,"part","input-wrapper");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(symbol_el,"part","symbol");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(symbol_el,"aria-hidden","true");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"part","input");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"id","input");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"inputmode","decimal");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"aria-labelledby","label");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(hint_el,"part","hint");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(hint_el,"id","hint");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(hint_el,"aria-live","polite");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(error_el,"part","error");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(error_el,"id","error");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(error_el,"role","alert");

app.components.x_currency_field.x_currency_field.set_attr_BANG_(error_el,"aria-live","assertive");

wrapper_el.appendChild(symbol_el);

wrapper_el.appendChild(input_el);

field_el.appendChild(label_el);

field_el.appendChild(wrapper_el);

field_el.appendChild(hint_el);

field_el.appendChild(error_el);

root.appendChild(style_el);

root.appendChild(field_el);

var refs = ({"field": field_el, "label": label_el, "wrapper": wrapper_el, "symbol": symbol_el, "input": input_el, "hint": hint_el, "error": error_el});
app.components.x_currency_field.x_currency_field.goog$module$goog$object.set(el,app.components.x_currency_field.x_currency_field.k_refs,refs);

return refs;
});
app.components.x_currency_field.x_currency_field.sync_validity_BANG_ = (function app$components$x_currency_field$x_currency_field$sync_validity_BANG_(el,internals,input_el,value){
var has_error_QMARK_ = app.components.x_currency_field.x_currency_field.has_attr_QMARK_(el,app.components.x_currency_field.model.attr_error);
var error_msg = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_error);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var required_QMARK_ = app.components.x_currency_field.x_currency_field.has_attr_QMARK_(el,app.components.x_currency_field.model.attr_required);
var min_raw = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_min);
var max_raw = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_max);
var num = parseFloat(value);
var has_num_QMARK_ = cljs.core.not(isNaN(num));
var min_num = (cljs.core.truth_(min_raw)?parseFloat(min_raw):null);
var max_num = (cljs.core.truth_(max_raw)?parseFloat(max_raw):null);
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
if(((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(value,"")) && ((!(has_num_QMARK_))))){
return internals.setValidity(({"badInput": true}),"Please enter a valid number.",input_el);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = has_num_QMARK_;
if(and__5140__auto__){
var and__5140__auto____$1 = min_num;
if(cljs.core.truth_(and__5140__auto____$1)){
return ((cljs.core.not(isNaN(min_num))) && ((num < min_num)));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return internals.setValidity(({"rangeUnderflow": true}),(""+"Value must be at least "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(min_num)+"."),input_el);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = has_num_QMARK_;
if(and__5140__auto__){
var and__5140__auto____$1 = max_num;
if(cljs.core.truth_(and__5140__auto____$1)){
return ((cljs.core.not(isNaN(max_num))) && ((num > max_num)));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return internals.setValidity(({"rangeOverflow": true}),(""+"Value must be at most "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_num)+"."),input_el);
} else {
return internals.setValidity(({}),"",input_el);

}
}
}
}
}
});
app.components.x_currency_field.x_currency_field.read_model = (function app$components$x_currency_field$x_currency_field$read_model(el){
return app.components.x_currency_field.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"currency-raw","currency-raw",1169887491),new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"error-raw","error-raw",-1164358971),new cljs.core.Keyword(null,"hint-raw","hint-raw",-503443994),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"min-raw","min-raw",183137548),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"max-raw","max-raw",-434946611),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"locale-raw","locale-raw",385401846),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657)],[app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_label),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_currency),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_name),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_error),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_hint),app.components.x_currency_field.x_currency_field.has_attr_QMARK_(el,app.components.x_currency_field.model.attr_disabled),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_min),app.components.x_currency_field.x_currency_field.has_attr_QMARK_(el,app.components.x_currency_field.model.attr_required),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_max),app.components.x_currency_field.x_currency_field.has_attr_QMARK_(el,app.components.x_currency_field.model.attr_readonly),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_value),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_locale),app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_placeholder)]));
});
app.components.x_currency_field.x_currency_field.render_BANG_ = (function app$components$x_currency_field$x_currency_field$render_BANG_(el){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var label_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"label");
var symbol_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"symbol");
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var hint_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"hint");
var error_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"error");
var m = app.components.x_currency_field.x_currency_field.read_model(el);
var custom_error_QMARK_ = new cljs.core.Keyword(null,"has-error?","has-error?",1984278765).cljs$core$IFn$_invoke$arity$1(m);
var computed_msg = (cljs.core.truth_(custom_error_QMARK_)?null:app.components.x_currency_field.model.validation_message(m));
var has_error_QMARK_ = (function (){var or__5142__auto__ = custom_error_QMARK_;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((function (){var or__5142__auto____$1 = computed_msg;
if(cljs.core.truth_(or__5142__auto____$1)){
return or__5142__auto____$1;
} else {
return "";
}
})(),"");
}
})();
var display_error = (cljs.core.truth_(custom_error_QMARK_)?new cljs.core.Keyword(null,"error","error",-978969032).cljs$core$IFn$_invoke$arity$1(m):(function (){var or__5142__auto__ = computed_msg;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
var has_hint_QMARK_ = new cljs.core.Keyword(null,"has-hint?","has-hint?",-60700378).cljs$core$IFn$_invoke$arity$1(m);
var has_label_QMARK_ = new cljs.core.Keyword(null,"has-label?","has-label?",-1686997398).cljs$core$IFn$_invoke$arity$1(m);
var currency = new cljs.core.Keyword(null,"currency","currency",-898327568).cljs$core$IFn$_invoke$arity$1(m);
var locale = new cljs.core.Keyword(null,"locale","locale",-2115712697).cljs$core$IFn$_invoke$arity$1(m);
(symbol_el.textContent = app.components.x_currency_field.x_currency_field.currency_symbol(currency,locale));

(input_el.name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m));

(input_el.placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m));

(input_el.disabled = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m));

(input_el.readOnly = new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m));

(input_el.required = new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"aria-invalid",(cljs.core.truth_(has_error_QMARK_)?"true":"false"));

var describedby_22769 = (cljs.core.truth_((function (){var and__5140__auto__ = has_hint_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return has_error_QMARK_;
} else {
return and__5140__auto__;
}
})())?"hint error":(cljs.core.truth_(has_hint_QMARK_)?"hint":(cljs.core.truth_(has_error_QMARK_)?"error":null
)));
if(cljs.core.truth_(describedby_22769)){
app.components.x_currency_field.x_currency_field.set_attr_BANG_(input_el,"aria-describedby",describedby_22769);
} else {
app.components.x_currency_field.x_currency_field.remove_attr_BANG_(input_el,"aria-describedby");
}

if(app.components.x_currency_field.x_currency_field.is_focused_QMARK_(el,input_el)){
} else {
var raw_val_22770 = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m);
(input_el.value = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(raw_val_22770,""))?"":app.components.x_currency_field.x_currency_field.format_display(raw_val_22770,currency,locale)));
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

(error_el.textContent = display_error);

if(cljs.core.truth_(has_error_QMARK_)){
error_el.classList.remove("error-hidden");
} else {
error_el.classList.add("error-hidden");
}

app.components.x_currency_field.x_currency_field.set_bool_attr_BANG_(el,"data-invalid",has_error_QMARK_);

var temp__5823__auto____$1 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return app.components.x_currency_field.x_currency_field.sync_validity_BANG_(el,internals,input_el,(function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
return null;
}
} else {
return null;
}
});
app.components.x_currency_field.x_currency_field.dispatch_BANG_ = (function app$components$x_currency_field$x_currency_field$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_currency_field.x_currency_field.make_focus_handler = (function app$components$x_currency_field$x_currency_field$make_focus_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var raw_val = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
return (input_el.value = raw_val);
} else {
return null;
}
});
});
app.components.x_currency_field.x_currency_field.make_input_handler = (function app$components$x_currency_field$x_currency_field$make_input_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___22771__$1 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22771__$1)){
var internals_22772 = temp__5823__auto___22771__$1;
internals_22772.setFormValue(value);

app.components.x_currency_field.x_currency_field.sync_validity_BANG_(el,internals_22772,input_el,value);
} else {
}

return app.components.x_currency_field.x_currency_field.dispatch_BANG_(el,app.components.x_currency_field.model.event_input,({"name": name, "value": value}));
} else {
return null;
}
});
});
app.components.x_currency_field.x_currency_field.make_change_handler = (function app$components$x_currency_field$x_currency_field$make_change_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var raw = input_el.value;
var num = parseFloat(raw);
var canonical = (cljs.core.truth_(isNaN(num))?raw:(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(num)));
var name = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
app.components.x_currency_field.x_currency_field.set_attr_BANG_(el,app.components.x_currency_field.model.attr_value,canonical);

var temp__5823__auto___22773__$1 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22773__$1)){
var internals_22774 = temp__5823__auto___22773__$1;
internals_22774.setFormValue(canonical);

app.components.x_currency_field.x_currency_field.sync_validity_BANG_(el,internals_22774,input_el,canonical);
} else {
}

return app.components.x_currency_field.x_currency_field.dispatch_BANG_(el,app.components.x_currency_field.model.event_change,({"name": name, "value": canonical}));
} else {
return null;
}
});
});
app.components.x_currency_field.x_currency_field.add_listeners_BANG_ = (function app$components$x_currency_field$x_currency_field$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var focus_h = app.components.x_currency_field.x_currency_field.make_focus_handler(el);
var input_h = app.components.x_currency_field.x_currency_field.make_input_handler(el);
var change_h = app.components.x_currency_field.x_currency_field.make_change_handler(el);
input_el.addEventListener("focus",focus_h);

input_el.addEventListener("input",input_h);

input_el.addEventListener("change",change_h);

return app.components.x_currency_field.x_currency_field.goog$module$goog$object.set(el,app.components.x_currency_field.x_currency_field.k_handlers,({"focus": focus_h, "input": input_h, "change": change_h}));
} else {
return null;
}
});
app.components.x_currency_field.x_currency_field.remove_listeners_BANG_ = (function app$components$x_currency_field$x_currency_field$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var input_el_22775 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
input_el_22775.removeEventListener("focus",app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(handlers,"focus"));

input_el_22775.removeEventListener("input",app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(handlers,"input"));

input_el_22775.removeEventListener("change",app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(handlers,"change"));

return app.components.x_currency_field.x_currency_field.goog$module$goog$object.set(el,app.components.x_currency_field.x_currency_field.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_currency_field.x_currency_field.form_disabled_BANG_ = (function app$components$x_currency_field$x_currency_field$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_currency_field.x_currency_field.set_bool_attr_BANG_(el,app.components.x_currency_field.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_currency_field.x_currency_field.render_BANG_(el);
});
app.components.x_currency_field.x_currency_field.form_reset_BANG_ = (function app$components$x_currency_field$x_currency_field$form_reset_BANG_(el){
app.components.x_currency_field.x_currency_field.remove_attr_BANG_(el,app.components.x_currency_field.model.attr_value);

var temp__5823__auto___22776 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22776)){
var refs_22777 = temp__5823__auto___22776;
var input_el_22778 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs_22777,"input");
(input_el_22778.value = "");
} else {
}

var temp__5823__auto___22779 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22779)){
var internals_22780 = temp__5823__auto___22779;
internals_22780.setFormValue("");
} else {
}

return app.components.x_currency_field.x_currency_field.render_BANG_(el);
});
app.components.x_currency_field.x_currency_field.connected_BANG_ = (function app$components$x_currency_field$x_currency_field$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs))){
} else {
app.components.x_currency_field.x_currency_field.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_currency_field.x_currency_field.goog$module$goog$object.set(el,app.components.x_currency_field.x_currency_field.k_internals,el.attachInternals());
} else {
}
}

app.components.x_currency_field.x_currency_field.remove_listeners_BANG_(el);

var temp__5823__auto___22781 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22781)){
var refs_22782 = temp__5823__auto___22781;
var input_el_22783 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs_22782,"input");
var val_attr_22784 = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_value);
var currency_22785 = app.components.x_currency_field.model.normalize_currency(app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_currency));
var locale_22786 = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_locale);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
if(cljs.core.truth_(val_attr_22784)){
(input_el_22783.value = app.components.x_currency_field.x_currency_field.format_display(val_attr_22784,currency_22785,locale_22786));
} else {
}
} else {
}

var temp__5823__auto___22787 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22787)){
var internals_22788 = temp__5823__auto___22787;
internals_22788.setFormValue((function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
}

app.components.x_currency_field.x_currency_field.add_listeners_BANG_(el);

return app.components.x_currency_field.x_currency_field.render_BANG_(el);
});
app.components.x_currency_field.x_currency_field.disconnected_BANG_ = (function app$components$x_currency_field$x_currency_field$disconnected_BANG_(el){
return app.components.x_currency_field.x_currency_field.remove_listeners_BANG_(el);
});
app.components.x_currency_field.x_currency_field.attribute_changed_BANG_ = (function app$components$x_currency_field$x_currency_field$attribute_changed_BANG_(el,name,_old,new_val){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_currency_field.model.attr_value)){
var temp__5823__auto___22789 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(el,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22789)){
var refs_22790 = temp__5823__auto___22789;
var input_el_22791 = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs_22790,"input");
if(app.components.x_currency_field.x_currency_field.is_focused_QMARK_(el,input_el_22791)){
} else {
var currency_22792 = app.components.x_currency_field.model.normalize_currency(app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_currency));
var locale_22793 = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(el,app.components.x_currency_field.model.attr_locale);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
(input_el_22791.value = (((((new_val == null)) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new_val,""))))?"":app.components.x_currency_field.x_currency_field.format_display(new_val,currency_22792,locale_22793)));
}
} else {
}
} else {
}

return app.components.x_currency_field.x_currency_field.render_BANG_(el);
});
app.components.x_currency_field.x_currency_field.define_string_prop_BANG_ = (function app$components$x_currency_field$x_currency_field$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_currency_field.x_currency_field.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_currency_field.x_currency_field.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_currency_field.x_currency_field.define_bool_prop_BANG_ = (function app$components$x_currency_field$x_currency_field$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_currency_field.x_currency_field.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_currency_field.x_currency_field.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_currency_field.x_currency_field.define_value_prop_BANG_ = (function app$components$x_currency_field$x_currency_field$define_value_prop_BANG_(proto){
return Object.defineProperty(proto,"value",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(this$,app.components.x_currency_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
var str_v = (((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined))))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)):"");
app.components.x_currency_field.x_currency_field.set_attr_BANG_(this$,app.components.x_currency_field.model.attr_value,str_v);

var temp__5823__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(this$,app.components.x_currency_field.x_currency_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(refs,"input");
var currency = app.components.x_currency_field.model.normalize_currency(app.components.x_currency_field.x_currency_field.get_attr(this$,app.components.x_currency_field.model.attr_currency));
var locale = (function (){var or__5142__auto__ = app.components.x_currency_field.x_currency_field.get_attr(this$,app.components.x_currency_field.model.attr_locale);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
if(app.components.x_currency_field.x_currency_field.is_focused_QMARK_(this$,input_el)){
return null;
} else {
return (input_el.value = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(str_v,""))?"":app.components.x_currency_field.x_currency_field.format_display(str_v,currency,locale)));
}
} else {
return null;
}
})}));
});
app.components.x_currency_field.x_currency_field.element_class = (function app$components$x_currency_field$x_currency_field$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_currency_field.model.observed_attributes;
})}));

app.components.x_currency_field.x_currency_field.define_value_prop_BANG_(proto);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"name",app.components.x_currency_field.model.attr_name);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"currency",app.components.x_currency_field.model.attr_currency);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"locale",app.components.x_currency_field.model.attr_locale);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"min",app.components.x_currency_field.model.attr_min);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"max",app.components.x_currency_field.model.attr_max);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"placeholder",app.components.x_currency_field.model.attr_placeholder);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"label",app.components.x_currency_field.model.attr_label);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"hint",app.components.x_currency_field.model.attr_hint);

app.components.x_currency_field.x_currency_field.define_string_prop_BANG_(proto,"error",app.components.x_currency_field.model.attr_error);

app.components.x_currency_field.x_currency_field.define_bool_prop_BANG_(proto,"disabled",app.components.x_currency_field.model.attr_disabled);

app.components.x_currency_field.x_currency_field.define_bool_prop_BANG_(proto,"readOnly",app.components.x_currency_field.model.attr_readonly);

app.components.x_currency_field.x_currency_field.define_bool_prop_BANG_(proto,"required",app.components.x_currency_field.model.attr_required);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_currency_field.x_currency_field.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_currency_field.x_currency_field.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_currency_field.x_currency_field.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_currency_field.x_currency_field.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_currency_field.x_currency_field.form_reset_BANG_(this$);
}));

(proto["checkValidity"] = (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(this$,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5821__auto__)){
var internals = temp__5821__auto__;
return internals.checkValidity();
} else {
return true;
}
}));

(proto["reportValidity"] = (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_currency_field.x_currency_field.goog$module$goog$object.get(this$,app.components.x_currency_field.x_currency_field.k_internals);
if(cljs.core.truth_(temp__5821__auto__)){
var internals = temp__5821__auto__;
return internals.reportValidity();
} else {
return true;
}
}));

return cls;
});
app.components.x_currency_field.x_currency_field.init_BANG_ = (function app$components$x_currency_field$x_currency_field$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_currency_field.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_currency_field.model.tag_name,app.components.x_currency_field.x_currency_field.element_class());
}
});

//# sourceMappingURL=app.components.x_currency_field.x_currency_field.js.map
