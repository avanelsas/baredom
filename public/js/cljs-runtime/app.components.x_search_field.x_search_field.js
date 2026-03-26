goog.provide('app.components.x_search_field.x_search_field');
goog.scope(function(){
  app.components.x_search_field.x_search_field.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_search_field.x_search_field.k_refs = "__xSearchFieldRefs";
app.components.x_search_field.x_search_field.k_internals = "__xSearchFieldInternals";
app.components.x_search_field.x_search_field.k_handlers = "__xSearchFieldHandlers";
app.components.x_search_field.x_search_field.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-search-field-bg:#ffffff;"+"--x-search-field-color:#111827;"+"--x-search-field-border:1px solid #d1d5db;"+"--x-search-field-border-radius:6px;"+"--x-search-field-focus-ring-color:#2563eb;"+"--x-search-field-icon-color:#9ca3af;"+"--x-search-field-clear-color:#9ca3af;"+"--x-search-field-disabled-opacity:0.45;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-search-field-bg:#1f2937;"+"--x-search-field-color:#f9fafb;"+"--x-search-field-border:1px solid #4b5563;"+"--x-search-field-focus-ring-color:#3b82f6;"+"--x-search-field-icon-color:#6b7280;"+"--x-search-field-clear-color:#6b7280;"+"}"+"}"+"[part=wrapper]{"+"display:flex;"+"align-items:center;"+"position:relative;"+"background:var(--x-search-field-bg);"+"border:var(--x-search-field-border);"+"border-radius:var(--x-search-field-border-radius);"+"transition:border-color 120ms ease,box-shadow 120ms ease;"+"}"+"[part=wrapper]:focus-within{"+"border-color:var(--x-search-field-focus-ring-color);"+"box-shadow:0 0 0 3px color-mix(in srgb,var(--x-search-field-focus-ring-color) 20%,transparent);"+"}"+"[part=icon]{"+"display:flex;"+"align-items:center;"+"justify-content:center;"+"flex-shrink:0;"+"width:36px;"+"color:var(--x-search-field-icon-color);"+"pointer-events:none;"+"}"+"[part=input]{"+"flex:1;"+"min-width:0;"+"padding:0.5rem 0.25rem;"+"background:transparent;"+"color:var(--x-search-field-color);"+"border:none;"+"outline:none;"+"font-size:1rem;"+"line-height:1.5;"+"font-family:inherit;"+"}"+"[part=input]::-webkit-search-cancel-button{"+"-webkit-appearance:none;"+"}"+"[part=input]::-webkit-search-decoration{"+"-webkit-appearance:none;"+"}"+"[part=clear]{"+"display:flex;"+"align-items:center;"+"justify-content:center;"+"flex-shrink:0;"+"width:32px;"+"height:32px;"+"margin-right:2px;"+"background:none;"+"border:none;"+"cursor:pointer;"+"color:var(--x-search-field-clear-color);"+"font-size:1.125rem;"+"line-height:1;"+"border-radius:4px;"+"padding:0;"+"}"+"[part=clear]:hover{"+"color:var(--x-search-field-color);"+"}"+".clear-hidden{"+"display:none;"+"}"+":host([disabled]) [part=wrapper]{"+"opacity:var(--x-search-field-disabled-opacity);"+"cursor:not-allowed;"+"}"+":host([disabled]) [part=input]{"+"cursor:not-allowed;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=wrapper]{transition:none;}"+"}");
app.components.x_search_field.x_search_field.make_el = (function app$components$x_search_field$x_search_field$make_el(tag){
return document.createElement(tag);
});
app.components.x_search_field.x_search_field.set_attr_BANG_ = (function app$components$x_search_field$x_search_field$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_search_field.x_search_field.remove_attr_BANG_ = (function app$components$x_search_field$x_search_field$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_search_field.x_search_field.has_attr_QMARK_ = (function app$components$x_search_field$x_search_field$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_search_field.x_search_field.get_attr = (function app$components$x_search_field$x_search_field$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_search_field.x_search_field.set_bool_attr_BANG_ = (function app$components$x_search_field$x_search_field$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_search_field.x_search_field.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_search_field.x_search_field.remove_attr_BANG_(el,attr);
}
});
app.components.x_search_field.x_search_field.search_icon_svg = (""+"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" "+"viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" "+"stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" "+"aria-hidden=\"true\">"+"<circle cx=\"11\" cy=\"11\" r=\"8\"/>"+"<line x1=\"21\" y1=\"21\" x2=\"16.65\" y2=\"16.65\"/>"+"</svg>");
app.components.x_search_field.x_search_field.make_shadow_BANG_ = (function app$components$x_search_field$x_search_field$make_shadow_BANG_(el){
if(cljs.core.truth_(el.shadowRoot)){
return null;
} else {
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_search_field.x_search_field.make_el("style");
var wrapper = app.components.x_search_field.x_search_field.make_el("div");
var icon_el = app.components.x_search_field.x_search_field.make_el("span");
var input_el = app.components.x_search_field.x_search_field.make_el("input");
var clear_el = app.components.x_search_field.x_search_field.make_el("button");
(style_el.textContent = app.components.x_search_field.x_search_field.style_text);

app.components.x_search_field.x_search_field.set_attr_BANG_(wrapper,"part","wrapper");

app.components.x_search_field.x_search_field.set_attr_BANG_(icon_el,"part","icon");

(icon_el.innerHTML = app.components.x_search_field.x_search_field.search_icon_svg);

app.components.x_search_field.x_search_field.set_attr_BANG_(input_el,"part","input");

app.components.x_search_field.x_search_field.set_attr_BANG_(input_el,"type","search");

app.components.x_search_field.x_search_field.set_attr_BANG_(input_el,"spellcheck","false");

app.components.x_search_field.x_search_field.set_attr_BANG_(clear_el,"part","clear");

app.components.x_search_field.x_search_field.set_attr_BANG_(clear_el,"type","button");

app.components.x_search_field.x_search_field.set_attr_BANG_(clear_el,"aria-label","Clear search");

(clear_el.textContent = "\u00D7");

clear_el.classList.add("clear-hidden");

wrapper.appendChild(icon_el);

wrapper.appendChild(input_el);

wrapper.appendChild(clear_el);

root.appendChild(style_el);

root.appendChild(wrapper);

var refs = ({"wrapper": wrapper, "icon": icon_el, "input": input_el, "clear": clear_el});
app.components.x_search_field.x_search_field.goog$module$goog$object.set(el,app.components.x_search_field.x_search_field.k_refs,refs);

return refs;
}
});
app.components.x_search_field.x_search_field.toggle_clear_visibility_BANG_ = (function app$components$x_search_field$x_search_field$toggle_clear_visibility_BANG_(input_el,clear_el,el){
var empty_QMARK_ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("",input_el.value);
var disabled_QMARK_ = app.components.x_search_field.x_search_field.has_attr_QMARK_(el,app.components.x_search_field.model.attr_disabled);
if(cljs.core.truth_((function (){var or__5142__auto__ = empty_QMARK_;
if(or__5142__auto__){
return or__5142__auto__;
} else {
return disabled_QMARK_;
}
})())){
return clear_el.classList.add("clear-hidden");
} else {
return clear_el.classList.remove("clear-hidden");
}
});
app.components.x_search_field.x_search_field.sync_validity_BANG_ = (function app$components$x_search_field$x_search_field$sync_validity_BANG_(el,internals,input_el){
var required_QMARK_ = app.components.x_search_field.x_search_field.has_attr_QMARK_(el,app.components.x_search_field.model.attr_required);
var value = input_el.value;
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
});
app.components.x_search_field.x_search_field.render_BANG_ = (function app$components$x_search_field$x_search_field$render_BANG_(el){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var clear_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"clear");
var m = app.components.x_search_field.model.normalize(new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_name),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_value),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657),app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_placeholder),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_label),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),app.components.x_search_field.x_search_field.has_attr_QMARK_(el,app.components.x_search_field.model.attr_disabled),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),app.components.x_search_field.x_search_field.has_attr_QMARK_(el,app.components.x_search_field.model.attr_required),new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710),app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_autocomplete)], null));
(input_el.name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m));

(input_el.placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m));

(input_el.disabled = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m));

(input_el.required = new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m));

(input_el.autocomplete = new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913).cljs$core$IFn$_invoke$arity$1(m));

var lbl_22740 = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var and__5140__auto__ = lbl_22740;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(lbl_22740,"");
} else {
return and__5140__auto__;
}
})())){
app.components.x_search_field.x_search_field.set_attr_BANG_(input_el,"aria-label",lbl_22740);
} else {
app.components.x_search_field.x_search_field.remove_attr_BANG_(input_el,"aria-label");
}

app.components.x_search_field.x_search_field.set_attr_BANG_(input_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_search_field.x_search_field.toggle_clear_visibility_BANG_(input_el,clear_el,el);

var temp__5823__auto____$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return app.components.x_search_field.x_search_field.sync_validity_BANG_(el,internals,input_el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_search_field.x_search_field.dispatch_BANG_ = (function app$components$x_search_field$x_search_field$dispatch_BANG_(el,event_name,detail,cancelable_QMARK_){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cljs.core.boolean$(cancelable_QMARK_)}))));
});
app.components.x_search_field.x_search_field.make_input_handler = (function app$components$x_search_field$x_search_field$make_input_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var clear_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"clear");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___22742__$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22742__$1)){
var internals_22743 = temp__5823__auto___22742__$1;
internals_22743.setFormValue(value);

app.components.x_search_field.x_search_field.sync_validity_BANG_(el,internals_22743,input_el);
} else {
}

app.components.x_search_field.x_search_field.toggle_clear_visibility_BANG_(input_el,clear_el,el);

return app.components.x_search_field.x_search_field.dispatch_BANG_(el,app.components.x_search_field.model.event_input,({"name": name, "value": value}),false);
} else {
return null;
}
});
});
app.components.x_search_field.x_search_field.make_change_handler = (function app$components$x_search_field$x_search_field$make_change_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var temp__5823__auto___22745__$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22745__$1)){
var internals_22746 = temp__5823__auto___22745__$1;
internals_22746.setFormValue(value);

app.components.x_search_field.x_search_field.sync_validity_BANG_(el,internals_22746,input_el);
} else {
}

return app.components.x_search_field.x_search_field.dispatch_BANG_(el,app.components.x_search_field.model.event_change,({"name": name, "value": value}),false);
} else {
return null;
}
});
});
app.components.x_search_field.x_search_field.make_keydown_handler = (function app$components$x_search_field$x_search_field$make_keydown_handler(el){
return (function (evt){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("Enter",evt.key)){
evt.preventDefault();

var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var value = input_el.value;
var name = (function (){var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_search_field.x_search_field.has_attr_QMARK_(el,app.components.x_search_field.model.attr_required);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(value,"");
} else {
return and__5140__auto__;
}
})())){
var temp__5823__auto____$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return internals.reportValidity();
} else {
return null;
}
} else {
return app.components.x_search_field.x_search_field.dispatch_BANG_(el,app.components.x_search_field.model.event_search,({"name": name, "value": value}),true);
}
} else {
return null;
}
} else {
return null;
}
});
});
app.components.x_search_field.x_search_field.make_click_handler = (function app$components$x_search_field$x_search_field$make_click_handler(el){
return (function (_evt){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var clear_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"clear");
var name = (function (){var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
(input_el.value = "");

var temp__5823__auto___22747__$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22747__$1)){
var internals_22748 = temp__5823__auto___22747__$1;
internals_22748.setFormValue("");

app.components.x_search_field.x_search_field.sync_validity_BANG_(el,internals_22748,input_el);
} else {
}

app.components.x_search_field.x_search_field.toggle_clear_visibility_BANG_(input_el,clear_el,el);

app.components.x_search_field.x_search_field.dispatch_BANG_(el,app.components.x_search_field.model.event_clear,({"name": name}),false);

return input_el.focus();
} else {
return null;
}
});
});
app.components.x_search_field.x_search_field.add_listeners_BANG_ = (function app$components$x_search_field$x_search_field$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var clear_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"clear");
var input_h = app.components.x_search_field.x_search_field.make_input_handler(el);
var change_h = app.components.x_search_field.x_search_field.make_change_handler(el);
var keydown_h = app.components.x_search_field.x_search_field.make_keydown_handler(el);
var click_h = app.components.x_search_field.x_search_field.make_click_handler(el);
input_el.addEventListener("input",input_h);

input_el.addEventListener("change",change_h);

input_el.addEventListener("keydown",keydown_h);

clear_el.addEventListener("click",click_h);

return app.components.x_search_field.x_search_field.goog$module$goog$object.set(el,app.components.x_search_field.x_search_field.k_handlers,({"input": input_h, "change": change_h, "keydown": keydown_h, "click": click_h}));
} else {
return null;
}
});
app.components.x_search_field.x_search_field.remove_listeners_BANG_ = (function app$components$x_search_field$x_search_field$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var input_el_22749 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
var clear_el_22750 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"clear");
input_el_22749.removeEventListener("input",app.components.x_search_field.x_search_field.goog$module$goog$object.get(handlers,"input"));

input_el_22749.removeEventListener("change",app.components.x_search_field.x_search_field.goog$module$goog$object.get(handlers,"change"));

input_el_22749.removeEventListener("keydown",app.components.x_search_field.x_search_field.goog$module$goog$object.get(handlers,"keydown"));

clear_el_22750.removeEventListener("click",app.components.x_search_field.x_search_field.goog$module$goog$object.get(handlers,"click"));

return app.components.x_search_field.x_search_field.goog$module$goog$object.set(el,app.components.x_search_field.x_search_field.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_search_field.x_search_field.form_disabled_BANG_ = (function app$components$x_search_field$x_search_field$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_search_field.x_search_field.set_bool_attr_BANG_(el,app.components.x_search_field.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_search_field.x_search_field.render_BANG_(el);
});
app.components.x_search_field.x_search_field.form_reset_BANG_ = (function app$components$x_search_field$x_search_field$form_reset_BANG_(el){
app.components.x_search_field.x_search_field.remove_attr_BANG_(el,app.components.x_search_field.model.attr_value);

var temp__5823__auto___22751 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22751)){
var refs_22752 = temp__5823__auto___22751;
var input_el_22753 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs_22752,"input");
(input_el_22753.value = "");
} else {
}

var temp__5823__auto___22754 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22754)){
var internals_22755 = temp__5823__auto___22754;
internals_22755.setFormValue("");
} else {
}

return app.components.x_search_field.x_search_field.render_BANG_(el);
});
app.components.x_search_field.x_search_field.connected_BANG_ = (function app$components$x_search_field$x_search_field$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs))){
} else {
app.components.x_search_field.x_search_field.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_search_field.x_search_field.goog$module$goog$object.set(el,app.components.x_search_field.x_search_field.k_internals,el.attachInternals());
} else {
}
}

app.components.x_search_field.x_search_field.remove_listeners_BANG_(el);

var temp__5823__auto___22756 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22756)){
var refs_22757 = temp__5823__auto___22756;
var input_el_22758 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs_22757,"input");
var val_attr_22759 = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_value);
if(cljs.core.truth_(val_attr_22759)){
(input_el_22758.value = val_attr_22759);
} else {
}
} else {
}

var temp__5823__auto___22760 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5823__auto___22760)){
var internals_22761 = temp__5823__auto___22760;
internals_22761.setFormValue((function (){var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(el,app.components.x_search_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
}

app.components.x_search_field.x_search_field.add_listeners_BANG_(el);

return app.components.x_search_field.x_search_field.render_BANG_(el);
});
app.components.x_search_field.x_search_field.disconnected_BANG_ = (function app$components$x_search_field$x_search_field$disconnected_BANG_(el){
return app.components.x_search_field.x_search_field.remove_listeners_BANG_(el);
});
app.components.x_search_field.x_search_field.attribute_changed_BANG_ = (function app$components$x_search_field$x_search_field$attribute_changed_BANG_(el,name,_old,new_val){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_search_field.model.attr_value)){
var temp__5823__auto___22762 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(el,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto___22762)){
var refs_22763 = temp__5823__auto___22762;
var input_el_22765 = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs_22763,"input");
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(input_el_22765.value,new_val)){
(input_el_22765.value = (function (){var or__5142__auto__ = new_val;
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

return app.components.x_search_field.x_search_field.render_BANG_(el);
});
app.components.x_search_field.x_search_field.define_string_prop_BANG_ = (function app$components$x_search_field$x_search_field$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_search_field.x_search_field.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_search_field.x_search_field.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_search_field.x_search_field.define_bool_prop_BANG_ = (function app$components$x_search_field$x_search_field$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_search_field.x_search_field.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_search_field.x_search_field.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_search_field.x_search_field.define_value_prop_BANG_ = (function app$components$x_search_field$x_search_field$define_value_prop_BANG_(proto){
return Object.defineProperty(proto,"value",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(this$,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5821__auto__)){
var refs = temp__5821__auto__;
return app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input").value;
} else {
var or__5142__auto__ = app.components.x_search_field.x_search_field.get_attr(this$,app.components.x_search_field.model.attr_value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}
}), "set": (function (v){
var this$ = this;
var str_v = (((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined))))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)):"");
app.components.x_search_field.x_search_field.set_attr_BANG_(this$,app.components.x_search_field.model.attr_value,str_v);

var temp__5823__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(this$,app.components.x_search_field.x_search_field.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var input_el = app.components.x_search_field.x_search_field.goog$module$goog$object.get(refs,"input");
return (input_el.value = str_v);
} else {
return null;
}
})}));
});
app.components.x_search_field.x_search_field.element_class = (function app$components$x_search_field$x_search_field$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_search_field.model.observed_attributes;
})}));

app.components.x_search_field.x_search_field.define_value_prop_BANG_(proto);

app.components.x_search_field.x_search_field.define_string_prop_BANG_(proto,"name",app.components.x_search_field.model.attr_name);

app.components.x_search_field.x_search_field.define_string_prop_BANG_(proto,"placeholder",app.components.x_search_field.model.attr_placeholder);

app.components.x_search_field.x_search_field.define_string_prop_BANG_(proto,"label",app.components.x_search_field.model.attr_label);

app.components.x_search_field.x_search_field.define_string_prop_BANG_(proto,"autocomplete",app.components.x_search_field.model.attr_autocomplete);

app.components.x_search_field.x_search_field.define_bool_prop_BANG_(proto,"disabled",app.components.x_search_field.model.attr_disabled);

app.components.x_search_field.x_search_field.define_bool_prop_BANG_(proto,"required",app.components.x_search_field.model.attr_required);

(proto["checkValidity"] = (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(this$,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5821__auto__)){
var internals = temp__5821__auto__;
return internals.checkValidity();
} else {
return true;
}
}));

(proto["reportValidity"] = (function (){
var this$ = this;
var temp__5821__auto__ = app.components.x_search_field.x_search_field.goog$module$goog$object.get(this$,app.components.x_search_field.x_search_field.k_internals);
if(cljs.core.truth_(temp__5821__auto__)){
var internals = temp__5821__auto__;
return internals.reportValidity();
} else {
return true;
}
}));

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_search_field.x_search_field.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_search_field.x_search_field.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_search_field.x_search_field.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_search_field.x_search_field.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_search_field.x_search_field.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_search_field.x_search_field.init_BANG_ = (function app$components$x_search_field$x_search_field$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_search_field.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_search_field.model.tag_name,app.components.x_search_field.x_search_field.element_class());
}
});

//# sourceMappingURL=app.components.x_search_field.x_search_field.js.map
