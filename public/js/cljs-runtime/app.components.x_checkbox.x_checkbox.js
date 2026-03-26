goog.provide('app.components.x_checkbox.x_checkbox');
goog.scope(function(){
  app.components.x_checkbox.x_checkbox.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_checkbox.x_checkbox.k_refs = "__xCheckboxRefs";
app.components.x_checkbox.x_checkbox.k_internals = "__xCheckboxInternals";
app.components.x_checkbox.x_checkbox.k_handlers = "__xCheckboxHandlers";
app.components.x_checkbox.x_checkbox.style_text = (""+":host{"+"display:inline-block;"+"vertical-align:middle;"+"color-scheme:light dark;"+"--x-checkbox-size:16px;"+"--x-checkbox-border-width:2px;"+"--x-checkbox-border-color:#6b7280;"+"--x-checkbox-border-radius:4px;"+"--x-checkbox-bg:#ffffff;"+"--x-checkbox-bg-checked:#2563eb;"+"--x-checkbox-bg-indeterminate:#2563eb;"+"--x-checkbox-border-checked:#2563eb;"+"--x-checkbox-border-indeterminate:#2563eb;"+"--x-checkbox-check-color:#ffffff;"+"--x-checkbox-focus-ring:#60a5fa;"+"--x-checkbox-disabled-opacity:0.45;"+"--x-checkbox-transition:background 120ms ease,border-color 120ms ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-checkbox-border-color:#9ca3af;"+"--x-checkbox-bg:#1f2937;"+"--x-checkbox-bg-checked:#3b82f6;"+"--x-checkbox-bg-indeterminate:#3b82f6;"+"--x-checkbox-border-checked:#3b82f6;"+"--x-checkbox-border-indeterminate:#3b82f6;"+"--x-checkbox-focus-ring:#93c5fd;"+"}"+"}"+"[part=control]{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"padding:0;"+"cursor:pointer;"+"}"+"[part=control][disabled]{"+"cursor:default;"+"opacity:var(--x-checkbox-disabled-opacity);"+"}"+"[part=control]:focus-visible{"+"outline:none;"+"box-shadow:0 0 0 3px var(--x-checkbox-focus-ring);"+"border-radius:calc(var(--x-checkbox-border-radius) + 2px);"+"}"+"[part=box]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-checkbox-size);"+"height:var(--x-checkbox-size);"+"border:var(--x-checkbox-border-width) solid var(--x-checkbox-border-color);"+"border-radius:var(--x-checkbox-border-radius);"+"background:var(--x-checkbox-bg);"+"transition:var(--x-checkbox-transition);"+"position:relative;"+"flex-shrink:0;"+"}"+":host([data-checked]) [part=box]{"+"background:var(--x-checkbox-bg-checked);"+"border-color:var(--x-checkbox-border-checked);"+"}"+":host([data-indeterminate]) [part=box]{"+"background:var(--x-checkbox-bg-indeterminate);"+"border-color:var(--x-checkbox-border-indeterminate);"+"}"+"[part=check]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"position:relative;"+"width:100%;"+"height:100%;"+"}"+"[part=check]::before{"+"content:'\\2713';"+"color:var(--x-checkbox-check-color);"+"font-size:10px;"+"font-weight:700;"+"line-height:1;"+"opacity:0;"+"transition:opacity 100ms ease;"+"position:absolute;"+"}"+":host([data-checked]) [part=check]::before{"+"opacity:1;"+"}"+"[part=check]::after{"+"content:'';"+"display:block;"+"width:8px;"+"height:2px;"+"background:var(--x-checkbox-check-color);"+"border-radius:1px;"+"opacity:0;"+"transition:opacity 100ms ease;"+"position:absolute;"+"}"+":host([data-indeterminate]) [part=check]::after{"+"opacity:1;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=box]{transition:none;}"+"[part=check]::before{transition:none;}"+"[part=check]::after{transition:none;}"+"}");
app.components.x_checkbox.x_checkbox.make_el = (function app$components$x_checkbox$x_checkbox$make_el(tag){
return document.createElement(tag);
});
app.components.x_checkbox.x_checkbox.set_attr_BANG_ = (function app$components$x_checkbox$x_checkbox$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_checkbox.x_checkbox.remove_attr_BANG_ = (function app$components$x_checkbox$x_checkbox$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_checkbox.x_checkbox.has_attr_QMARK_ = (function app$components$x_checkbox$x_checkbox$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_checkbox.x_checkbox.get_attr = (function app$components$x_checkbox$x_checkbox$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_ = (function app$components$x_checkbox$x_checkbox$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_checkbox.x_checkbox.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_checkbox.x_checkbox.remove_attr_BANG_(el,attr);
}
});
app.components.x_checkbox.x_checkbox.make_shadow_BANG_ = (function app$components$x_checkbox$x_checkbox$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_checkbox.x_checkbox.make_el("style");
var control_el = app.components.x_checkbox.x_checkbox.make_el("button");
var box_el = app.components.x_checkbox.x_checkbox.make_el("span");
var check_el = app.components.x_checkbox.x_checkbox.make_el("span");
(style_el.textContent = app.components.x_checkbox.x_checkbox.style_text);

app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"part","control");

app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"type","button");

app.components.x_checkbox.x_checkbox.set_attr_BANG_(box_el,"part","box");

app.components.x_checkbox.x_checkbox.set_attr_BANG_(check_el,"part","check");

box_el.appendChild(check_el);

control_el.appendChild(box_el);

root.appendChild(style_el);

root.appendChild(control_el);

var refs = ({"root": root, "control": control_el, "box": box_el, "check": check_el});
app.components.x_checkbox.x_checkbox.goog$module$goog$object.set(el,app.components.x_checkbox.x_checkbox.k_refs,refs);

return refs;
});
app.components.x_checkbox.x_checkbox.read_model = (function app$components$x_checkbox$x_checkbox$read_model(el){
return app.components.x_checkbox.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"aria-labelledby-raw","aria-labelledby-raw",-107265075),new cljs.core.Keyword(null,"checked-present?","checked-present?",-1155676496),new cljs.core.Keyword(null,"indeterminate-present?","indeterminate-present?",-1714683438),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860)],[app.components.x_checkbox.x_checkbox.get_attr(el,app.components.x_checkbox.model.attr_name),app.components.x_checkbox.x_checkbox.has_attr_QMARK_(el,app.components.x_checkbox.model.attr_disabled),app.components.x_checkbox.x_checkbox.has_attr_QMARK_(el,app.components.x_checkbox.model.attr_required),app.components.x_checkbox.x_checkbox.get_attr(el,app.components.x_checkbox.model.attr_aria_labelledby),app.components.x_checkbox.x_checkbox.has_attr_QMARK_(el,app.components.x_checkbox.model.attr_checked),app.components.x_checkbox.x_checkbox.has_attr_QMARK_(el,app.components.x_checkbox.model.attr_indeterminate),app.components.x_checkbox.x_checkbox.has_attr_QMARK_(el,app.components.x_checkbox.model.attr_readonly),app.components.x_checkbox.x_checkbox.get_attr(el,app.components.x_checkbox.model.attr_value),app.components.x_checkbox.x_checkbox.get_attr(el,app.components.x_checkbox.model.attr_aria_label),app.components.x_checkbox.x_checkbox.get_attr(el,app.components.x_checkbox.model.attr_aria_describedby)]));
});
app.components.x_checkbox.x_checkbox.render_BANG_ = (function app$components$x_checkbox$x_checkbox$render_BANG_(el){
var temp__5823__auto__ = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(refs,"control");
var m = app.components.x_checkbox.x_checkbox.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var checked_QMARK_ = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(m);
var indeterminate_QMARK_ = new cljs.core.Keyword(null,"indeterminate?","indeterminate?",-1382048766).cljs$core$IFn$_invoke$arity$1(m);
app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-checked",new cljs.core.Keyword(null,"aria-checked","aria-checked",980530562).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-disabled",(cljs.core.truth_(disabled_QMARK_)?"true":"false"));

app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-readonly",(cljs.core.truth_(new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

var temp__5821__auto___22195 = new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___22195)){
var v_22196 = temp__5821__auto___22195;
app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-label",v_22196);
} else {
app.components.x_checkbox.x_checkbox.remove_attr_BANG_(control_el,"aria-label");
}

var temp__5821__auto___22197 = new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___22197)){
var v_22198 = temp__5821__auto___22197;
app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-labelledby",v_22198);
} else {
app.components.x_checkbox.x_checkbox.remove_attr_BANG_(control_el,"aria-labelledby");
}

var temp__5821__auto___22200 = new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___22200)){
var v_22202 = temp__5821__auto___22200;
app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"aria-describedby",v_22202);
} else {
app.components.x_checkbox.x_checkbox.remove_attr_BANG_(control_el,"aria-describedby");
}

(control_el.tabIndex = (cljs.core.truth_(disabled_QMARK_)?(-1):(0)));

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_checkbox.x_checkbox.set_attr_BANG_(control_el,"disabled","");
} else {
app.components.x_checkbox.x_checkbox.remove_attr_BANG_(control_el,"disabled");
}

app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,"data-checked",checked_QMARK_);

app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,"data-indeterminate",indeterminate_QMARK_);

app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);

var temp__5823__auto____$1 = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
var form_value = (cljs.core.truth_(checked_QMARK_)?new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m):null);
return internals.setFormValue((function (){var or__5142__auto__ = form_value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
})());
} else {
return null;
}
} else {
return null;
}
});
app.components.x_checkbox.x_checkbox.dispatch_cancelable_BANG_ = (function app$components$x_checkbox$x_checkbox$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_checkbox.x_checkbox.dispatch_BANG_ = (function app$components$x_checkbox$x_checkbox$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_checkbox.x_checkbox.try_toggle_BANG_ = (function app$components$x_checkbox$x_checkbox$try_toggle_BANG_(el){
var m = app.components.x_checkbox.x_checkbox.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var readonly_QMARK_ = new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__5142__auto__ = disabled_QMARK_;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return readonly_QMARK_;
}
})())){
return null;
} else {
var checked_QMARK_ = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(m);
var indeterminate_QMARK_ = new cljs.core.Keyword(null,"indeterminate?","indeterminate?",-1382048766).cljs$core$IFn$_invoke$arity$1(m);
var next_state = app.components.x_checkbox.model.next_toggle_state(checked_QMARK_,indeterminate_QMARK_);
var next_checked = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(next_state);
var value = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m);
var allowed_QMARK_ = app.components.x_checkbox.x_checkbox.dispatch_cancelable_BANG_(el,app.components.x_checkbox.model.event_change_request,({"value": value, "previousChecked": checked_QMARK_, "nextChecked": next_checked}));
if(allowed_QMARK_){
app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,app.components.x_checkbox.model.attr_checked,next_checked);

app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,app.components.x_checkbox.model.attr_indeterminate,new cljs.core.Keyword(null,"indeterminate?","indeterminate?",-1382048766).cljs$core$IFn$_invoke$arity$1(next_state));

app.components.x_checkbox.x_checkbox.render_BANG_(el);

return app.components.x_checkbox.x_checkbox.dispatch_BANG_(el,app.components.x_checkbox.model.event_change,({"value": value, "checked": next_checked}));
} else {
return null;
}
}
});
app.components.x_checkbox.x_checkbox.wire_external_label_BANG_ = (function app$components$x_checkbox$x_checkbox$wire_external_label_BANG_(el){
var id = app.components.x_checkbox.x_checkbox.get_attr(el,"id");
if(cljs.core.truth_(id)){
var lbl = document.querySelector((""+"label[for='"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(id)+"']"));
if(cljs.core.truth_(lbl)){
var lid = (function (){var or__5142__auto__ = app.components.x_checkbox.x_checkbox.get_attr(lbl,"id");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var gen_id = (""+"x-cb-lbl-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(id));
app.components.x_checkbox.x_checkbox.set_attr_BANG_(lbl,"id",gen_id);

return gen_id;
}
})();
var temp__5823__auto__ = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
return app.components.x_checkbox.x_checkbox.set_attr_BANG_(app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(refs,"control"),"aria-labelledby",lid);
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_checkbox.x_checkbox.make_click_handler = (function app$components$x_checkbox$x_checkbox$make_click_handler(el){
return (function (_evt){
return app.components.x_checkbox.x_checkbox.try_toggle_BANG_(el);
});
});
app.components.x_checkbox.x_checkbox.make_keydown_handler = (function app$components$x_checkbox$x_checkbox$make_keydown_handler(el){
return (function (evt){
var key = evt.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")))){
evt.preventDefault();

return app.components.x_checkbox.x_checkbox.try_toggle_BANG_(el);
} else {
return null;
}
});
});
app.components.x_checkbox.x_checkbox.add_listeners_BANG_ = (function app$components$x_checkbox$x_checkbox$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(refs,"control");
var click_h = app.components.x_checkbox.x_checkbox.make_click_handler(el);
var keydown_h = app.components.x_checkbox.x_checkbox.make_keydown_handler(el);
var handlers = ({"click": click_h, "keydown": keydown_h});
control_el.addEventListener("click",click_h);

control_el.addEventListener("keydown",keydown_h);

return app.components.x_checkbox.x_checkbox.goog$module$goog$object.set(el,app.components.x_checkbox.x_checkbox.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_checkbox.x_checkbox.remove_listeners_BANG_ = (function app$components$x_checkbox$x_checkbox$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var control_el_22209 = app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(refs,"control");
control_el_22209.removeEventListener("click",app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(handlers,"click"));

control_el_22209.removeEventListener("keydown",app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(handlers,"keydown"));

return app.components.x_checkbox.x_checkbox.goog$module$goog$object.set(el,app.components.x_checkbox.x_checkbox.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_checkbox.x_checkbox.form_disabled_BANG_ = (function app$components$x_checkbox$x_checkbox$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(el,app.components.x_checkbox.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_checkbox.x_checkbox.render_BANG_(el);
});
app.components.x_checkbox.x_checkbox.form_reset_BANG_ = (function app$components$x_checkbox$x_checkbox$form_reset_BANG_(el){
app.components.x_checkbox.x_checkbox.remove_attr_BANG_(el,app.components.x_checkbox.model.attr_checked);

app.components.x_checkbox.x_checkbox.remove_attr_BANG_(el,app.components.x_checkbox.model.attr_indeterminate);

return app.components.x_checkbox.x_checkbox.render_BANG_(el);
});
app.components.x_checkbox.x_checkbox.connected_BANG_ = (function app$components$x_checkbox$x_checkbox$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_checkbox.x_checkbox.goog$module$goog$object.get(el,app.components.x_checkbox.x_checkbox.k_refs))){
} else {
app.components.x_checkbox.x_checkbox.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_checkbox.x_checkbox.goog$module$goog$object.set(el,app.components.x_checkbox.x_checkbox.k_internals,el.attachInternals());
} else {
}
}

app.components.x_checkbox.x_checkbox.remove_listeners_BANG_(el);

app.components.x_checkbox.x_checkbox.add_listeners_BANG_(el);

app.components.x_checkbox.x_checkbox.wire_external_label_BANG_(el);

return app.components.x_checkbox.x_checkbox.render_BANG_(el);
});
app.components.x_checkbox.x_checkbox.disconnected_BANG_ = (function app$components$x_checkbox$x_checkbox$disconnected_BANG_(el){
return app.components.x_checkbox.x_checkbox.remove_listeners_BANG_(el);
});
app.components.x_checkbox.x_checkbox.attribute_changed_BANG_ = (function app$components$x_checkbox$x_checkbox$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_checkbox.x_checkbox.render_BANG_(el);
});
app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_ = (function app$components$x_checkbox$x_checkbox$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_checkbox.x_checkbox.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_checkbox.x_checkbox.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_checkbox.x_checkbox.define_string_prop_BANG_ = (function app$components$x_checkbox$x_checkbox$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_checkbox.x_checkbox.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_checkbox.x_checkbox.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_checkbox.x_checkbox.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_checkbox.x_checkbox.element_class = (function app$components$x_checkbox$x_checkbox$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_checkbox.model.observed_attributes;
})}));

app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_(proto,"checked",app.components.x_checkbox.model.attr_checked);

app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_(proto,"indeterminate",app.components.x_checkbox.model.attr_indeterminate);

app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_(proto,"disabled",app.components.x_checkbox.model.attr_disabled);

app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_(proto,"readOnly",app.components.x_checkbox.model.attr_readonly);

app.components.x_checkbox.x_checkbox.define_bool_prop_BANG_(proto,"required",app.components.x_checkbox.model.attr_required);

app.components.x_checkbox.x_checkbox.define_string_prop_BANG_(proto,"name",app.components.x_checkbox.model.attr_name);

app.components.x_checkbox.x_checkbox.define_string_prop_BANG_(proto,"value",app.components.x_checkbox.model.attr_value);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_checkbox.x_checkbox.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_checkbox.x_checkbox.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_checkbox.x_checkbox.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_checkbox.x_checkbox.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_checkbox.x_checkbox.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_checkbox.x_checkbox.init_BANG_ = (function app$components$x_checkbox$x_checkbox$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_checkbox.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_checkbox.model.tag_name,app.components.x_checkbox.x_checkbox.element_class());
}
});

//# sourceMappingURL=app.components.x_checkbox.x_checkbox.js.map
