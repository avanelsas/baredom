goog.provide('app.components.x_form.x_form');
goog.scope(function(){
  app.components.x_form.x_form.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_form.x_form.k_refs = "__xFormRefs";
app.components.x_form.x_form.k_handlers = "__xFormHandlers";
app.components.x_form.x_form.style_text = (""+":host{"+"display:block;"+"}"+"[part=root]{"+"display:flex;"+"flex-direction:column;"+"gap:var(--x-form-gap,1rem);"+"width:var(--x-form-width,100%);"+"}"+":host([loading]){pointer-events:none;opacity:0.6;}");
app.components.x_form.x_form.make_el = (function app$components$x_form$x_form$make_el(tag){
return document.createElement(tag);
});
app.components.x_form.x_form.set_attr_BANG_ = (function app$components$x_form$x_form$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_form.x_form.remove_attr_BANG_ = (function app$components$x_form$x_form$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_form.x_form.has_attr_QMARK_ = (function app$components$x_form$x_form$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_form.x_form.get_attr = (function app$components$x_form$x_form$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_form.x_form.make_shadow_BANG_ = (function app$components$x_form$x_form$make_shadow_BANG_(el){
if(cljs.core.truth_(el.shadowRoot)){
return null;
} else {
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_form.x_form.make_el("style");
var form_el = app.components.x_form.x_form.make_el("form");
var slot_el = app.components.x_form.x_form.make_el("slot");
(style_el.textContent = app.components.x_form.x_form.style_text);

app.components.x_form.x_form.set_attr_BANG_(form_el,"part","root");

app.components.x_form.x_form.set_attr_BANG_(form_el,"novalidate","");

form_el.appendChild(slot_el);

root.appendChild(style_el);

root.appendChild(form_el);

return app.components.x_form.x_form.goog$module$goog$object.set(el,app.components.x_form.x_form.k_refs,({"form": form_el}));
}
});
app.components.x_form.x_form.read_model = (function app$components$x_form$x_form$read_model(el){
return app.components.x_form.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"loading-raw","loading-raw",-1707143099),app.components.x_form.x_form.get_attr(el,app.components.x_form.model.attr_loading),new cljs.core.Keyword(null,"novalidate-raw","novalidate-raw",1783236714),(cljs.core.truth_(app.components.x_form.x_form.has_attr_QMARK_(el,app.components.x_form.model.attr_novalidate))?"":null),new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710),app.components.x_form.x_form.get_attr(el,app.components.x_form.model.attr_autocomplete)], null));
});
app.components.x_form.x_form.render_BANG_ = (function app$components$x_form$x_form$render_BANG_(el){
var temp__5823__auto__ = app.components.x_form.x_form.goog$module$goog$object.get(el,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var form_el = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
var m = app.components.x_form.x_form.read_model(el);
app.components.x_form.x_form.set_attr_BANG_(form_el,"autocomplete",new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"loading?","loading?",1905707049).cljs$core$IFn$_invoke$arity$1(m))){
app.components.x_form.x_form.set_attr_BANG_(form_el,"aria-busy","true");

return app.components.x_form.x_form.set_attr_BANG_(form_el,"data-loading","");
} else {
app.components.x_form.x_form.remove_attr_BANG_(form_el,"aria-busy");

return app.components.x_form.x_form.remove_attr_BANG_(form_el,"data-loading");
}
} else {
return null;
}
});
app.components.x_form.x_form.report_fields_validity_BANG_ = (function app$components$x_form$x_form$report_fields_validity_BANG_(el){
var fields = el.querySelectorAll("[name]:not([disabled])");
var result = ({"ok": true});
var n__5741__auto___22720 = fields.length;
var i_22721 = (0);
while(true){
if((i_22721 < n__5741__auto___22720)){
var field_22722 = (fields[i_22721]);
if(cljs.core.truth_(field_22722.reportValidity)){
if(cljs.core.truth_(field_22722.reportValidity())){
} else {
(result["ok"] = false);
}
} else {
}

var G__22723 = (i_22721 + (1));
i_22721 = G__22723;
continue;
} else {
}
break;
}

return (result["ok"]);
});
app.components.x_form.x_form.collect_values = (function app$components$x_form$x_form$collect_values(el){
var fields = el.querySelectorAll("[name]:not([disabled])");
var result = (new Object());
var n__5741__auto___22724 = fields.length;
var i_22725 = (0);
while(true){
if((i_22725 < n__5741__auto___22724)){
var field_22726 = (fields[i_22725]);
var field_name_22727 = field_22726.name;
if(cljs.core.truth_((function (){var and__5140__auto__ = field_name_22727;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(field_name_22727,"");
} else {
return and__5140__auto__;
}
})())){
var tag_lower_22728 = field_22726.tagName.toLowerCase();
var type_attr_22729 = field_22726.getAttribute("type");
var checkbox_QMARK__22730 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(type_attr_22729,"checkbox")) || (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(type_attr_22729,"radio")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(tag_lower_22728,"x-checkbox")))));
if(checkbox_QMARK__22730){
if(cljs.core.truth_(field_22726.checked)){
(result[field_name_22727] = (function (){var or__5142__auto__ = field_22726.value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "on";
}
})());
} else {
}
} else {
(result[field_name_22727] = (function (){var or__5142__auto__ = field_22726.value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
}
} else {
}

var G__22731 = (i_22725 + (1));
i_22725 = G__22731;
continue;
} else {
}
break;
}

return result;
});
app.components.x_form.x_form.dispatch_BANG_ = (function app$components$x_form$x_form$dispatch_BANG_(el,event_name,detail,cancelable_QMARK_){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cancelable_QMARK_}))));
});
app.components.x_form.x_form.handle_submit_BANG_ = (function app$components$x_form$x_form$handle_submit_BANG_(el,e){
e.preventDefault();

var m = app.components.x_form.x_form.read_model(el);
if(cljs.core.truth_(new cljs.core.Keyword(null,"loading?","loading?",1905707049).cljs$core$IFn$_invoke$arity$1(m))){
return null;
} else {
var valid_QMARK_ = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"novalidate?","novalidate?",491529885).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_form.x_form.report_fields_validity_BANG_(el);
}
})();
if(cljs.core.truth_(valid_QMARK_)){
var values = app.components.x_form.x_form.collect_values(el);
return app.components.x_form.x_form.dispatch_BANG_(el,app.components.x_form.model.event_submit,({"values": values}),true);
} else {
return null;
}
}
});
app.components.x_form.x_form.handle_reset_BANG_ = (function app$components$x_form$x_form$handle_reset_BANG_(el,_e){
var fields_22732 = el.querySelectorAll("[name]");
var n__5741__auto___22733 = fields_22732.length;
var i_22734 = (0);
while(true){
if((i_22734 < n__5741__auto___22733)){
var field_22735 = (fields_22732[i_22734]);
if(cljs.core.truth_(field_22735.formResetCallback)){
field_22735.formResetCallback();
} else {
}

var G__22736 = (i_22734 + (1));
i_22734 = G__22736;
continue;
} else {
}
break;
}

return app.components.x_form.x_form.dispatch_BANG_(el,app.components.x_form.model.event_reset,({}),false);
});
app.components.x_form.x_form.handle_click_BANG_ = (function app$components$x_form$x_form$handle_click_BANG_(el,e){
var temp__5823__auto__ = e.target.closest("button,input[type=submit],input[type=reset]");
if(cljs.core.truth_(temp__5823__auto__)){
var btn = temp__5823__auto__;
var btn_type = btn.type;
var temp__5823__auto____$1 = app.components.x_form.x_form.goog$module$goog$object.get(el,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto____$1)){
var refs = temp__5823__auto____$1;
var form_el = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(btn_type,"submit")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(btn_type,"")))){
return form_el.requestSubmit();
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(btn_type,"reset")){
return form_el.reset();
} else {
return null;
}
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_form.x_form.add_listeners_BANG_ = (function app$components$x_form$x_form$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_form.x_form.goog$module$goog$object.get(el,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var form_el = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
var submit_h = (function (e){
return app.components.x_form.x_form.handle_submit_BANG_(el,e);
});
var reset_h = (function (e){
return app.components.x_form.x_form.handle_reset_BANG_(el,e);
});
var click_h = (function (e){
return app.components.x_form.x_form.handle_click_BANG_(el,e);
});
form_el.addEventListener("submit",submit_h);

form_el.addEventListener("reset",reset_h);

el.addEventListener("click",click_h);

return app.components.x_form.x_form.goog$module$goog$object.set(el,app.components.x_form.x_form.k_handlers,({"submit": submit_h, "reset": reset_h, "click": click_h}));
} else {
return null;
}
});
app.components.x_form.x_form.remove_listeners_BANG_ = (function app$components$x_form$x_form$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_form.x_form.goog$module$goog$object.get(el,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_form.x_form.goog$module$goog$object.get(el,app.components.x_form.x_form.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var form_el_22737 = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
form_el_22737.removeEventListener("submit",app.components.x_form.x_form.goog$module$goog$object.get(handlers,"submit"));

form_el_22737.removeEventListener("reset",app.components.x_form.x_form.goog$module$goog$object.get(handlers,"reset"));

el.removeEventListener("click",app.components.x_form.x_form.goog$module$goog$object.get(handlers,"click"));

return app.components.x_form.x_form.goog$module$goog$object.set(el,app.components.x_form.x_form.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_form.x_form.connected_BANG_ = (function app$components$x_form$x_form$connected_BANG_(el){
app.components.x_form.x_form.make_shadow_BANG_(el);

app.components.x_form.x_form.remove_listeners_BANG_(el);

app.components.x_form.x_form.add_listeners_BANG_(el);

return app.components.x_form.x_form.render_BANG_(el);
});
app.components.x_form.x_form.disconnected_BANG_ = (function app$components$x_form$x_form$disconnected_BANG_(el){
return app.components.x_form.x_form.remove_listeners_BANG_(el);
});
app.components.x_form.x_form.attribute_changed_BANG_ = (function app$components$x_form$x_form$attribute_changed_BANG_(el,_name,_old,_new_val){
return app.components.x_form.x_form.render_BANG_(el);
});
app.components.x_form.x_form.define_bool_prop_BANG_ = (function app$components$x_form$x_form$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_form.x_form.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
if(cljs.core.boolean$(v)){
return app.components.x_form.x_form.set_attr_BANG_(this$,attr_name,"");
} else {
return app.components.x_form.x_form.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_form.x_form.define_string_prop_BANG_ = (function app$components$x_form$x_form$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_form.x_form.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_form.x_form.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_form.x_form.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_form.x_form.element_class = (function app$components$x_form$x_form$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_form.model.observed_attributes;
})}));

app.components.x_form.x_form.define_bool_prop_BANG_(proto,"loading",app.components.x_form.model.attr_loading);

app.components.x_form.x_form.define_bool_prop_BANG_(proto,"novalidate",app.components.x_form.model.attr_novalidate);

app.components.x_form.x_form.define_string_prop_BANG_(proto,"autocomplete",app.components.x_form.model.attr_autocomplete);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_form.x_form.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_form.x_form.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_form.x_form.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["submit"] = (function (){
var this$ = this;
var temp__5823__auto__ = app.components.x_form.x_form.goog$module$goog$object.get(this$,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var form_el = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
return form_el.requestSubmit();
} else {
return null;
}
}));

(proto["reset"] = (function (){
var this$ = this;
var temp__5823__auto__ = app.components.x_form.x_form.goog$module$goog$object.get(this$,app.components.x_form.x_form.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var form_el = app.components.x_form.x_form.goog$module$goog$object.get(refs,"form");
return form_el.reset();
} else {
return null;
}
}));

(proto["setFieldError"] = (function (field_name,msg){
var this$ = this;
var temp__5823__auto__ = this$.querySelector((""+"[name=\""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(field_name)+"\"]"));
if(cljs.core.truth_(temp__5823__auto__)){
var field = temp__5823__auto__;
if((((msg == null)) || (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(msg,"")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(msg,undefined)))))){
return app.components.x_form.x_form.remove_attr_BANG_(field,"error");
} else {
return app.components.x_form.x_form.set_attr_BANG_(field,"error",msg);
}
} else {
return null;
}
}));

(proto["clearErrors"] = (function (){
var this$ = this;
var fields = this$.querySelectorAll("[error]");
var n__5741__auto__ = fields.length;
var i = (0);
while(true){
if((i < n__5741__auto__)){
app.components.x_form.x_form.remove_attr_BANG_((fields[i]),"error");

var G__22741 = (i + (1));
i = G__22741;
continue;
} else {
return null;
}
break;
}
}));

return cls;
});
app.components.x_form.x_form.init_BANG_ = (function app$components$x_form$x_form$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_form.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_form.model.tag_name,app.components.x_form.x_form.element_class());
}
});

//# sourceMappingURL=app.components.x_form.x_form.js.map
