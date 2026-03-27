goog.provide('app.components.x_radio.x_radio');
goog.scope(function(){
  app.components.x_radio.x_radio.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_radio.x_radio.k_refs = "__xRadioRefs";
app.components.x_radio.x_radio.k_internals = "__xRadioInternals";
app.components.x_radio.x_radio.k_handlers = "__xRadioHandlers";
app.components.x_radio.x_radio.style_text = (""+":host{"+"display:inline-block;"+"vertical-align:middle;"+"color-scheme:light dark;"+"--x-radio-size:16px;"+"--x-radio-border-width:2px;"+"--x-radio-border-color:#6b7280;"+"--x-radio-bg:#ffffff;"+"--x-radio-checked-color:#2563eb;"+"--x-radio-dot-size:6px;"+"--x-radio-focus-ring:#60a5fa;"+"--x-radio-disabled-opacity:0.45;"+"--x-radio-transition:background 120ms ease,border-color 120ms ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-radio-border-color:#9ca3af;"+"--x-radio-bg:#1f2937;"+"--x-radio-checked-color:#3b82f6;"+"--x-radio-focus-ring:#93c5fd;"+"}"+"}"+"[part=control]{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-radio-size);"+"height:var(--x-radio-size);"+"border:var(--x-radio-border-width) solid var(--x-radio-border-color);"+"border-radius:50%;"+"background:var(--x-radio-bg);"+"transition:var(--x-radio-transition);"+"cursor:pointer;"+"flex-shrink:0;"+"position:relative;"+"}"+"[part=control][disabled]{"+"cursor:default;"+"opacity:var(--x-radio-disabled-opacity);"+"}"+"[part=control]:focus-visible{"+"outline:none;"+"box-shadow:0 0 0 3px var(--x-radio-focus-ring);"+"}"+":host([data-checked]) [part=control]{"+"border-color:var(--x-radio-checked-color);"+"}"+"[part=dot]{"+"display:block;"+"width:var(--x-radio-dot-size);"+"height:var(--x-radio-dot-size);"+"border-radius:50%;"+"background:var(--x-radio-checked-color);"+"opacity:0;"+"transition:opacity 100ms ease;"+"}"+":host([data-checked]) [part=dot]{"+"opacity:1;"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=control]{transition:none;}"+"[part=dot]{transition:none;}"+"}");
app.components.x_radio.x_radio.make_el = (function app$components$x_radio$x_radio$make_el(tag){
return document.createElement(tag);
});
app.components.x_radio.x_radio.set_attr_BANG_ = (function app$components$x_radio$x_radio$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_radio.x_radio.remove_attr_BANG_ = (function app$components$x_radio$x_radio$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_radio.x_radio.has_attr_QMARK_ = (function app$components$x_radio$x_radio$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_radio.x_radio.get_attr = (function app$components$x_radio$x_radio$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_radio.x_radio.set_bool_attr_BANG_ = (function app$components$x_radio$x_radio$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_radio.x_radio.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_radio.x_radio.remove_attr_BANG_(el,attr);
}
});
app.components.x_radio.x_radio.make_shadow_BANG_ = (function app$components$x_radio$x_radio$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_radio.x_radio.make_el("style");
var control_el = app.components.x_radio.x_radio.make_el("button");
var dot_el = app.components.x_radio.x_radio.make_el("span");
(style_el.textContent = app.components.x_radio.x_radio.style_text);

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"part","control");

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"type","button");

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"role","radio");

app.components.x_radio.x_radio.set_attr_BANG_(dot_el,"part","dot");

control_el.appendChild(dot_el);

root.appendChild(style_el);

root.appendChild(control_el);

var refs = ({"root": root, "control": control_el, "dot": dot_el});
app.components.x_radio.x_radio.goog$module$goog$object.set(el,app.components.x_radio.x_radio.k_refs,refs);

return refs;
});
app.components.x_radio.x_radio.read_model = (function app$components$x_radio$x_radio$read_model(el){
return app.components.x_radio.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"aria-labelledby-raw","aria-labelledby-raw",-107265075),new cljs.core.Keyword(null,"checked-present?","checked-present?",-1155676496),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860)],[app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_name),app.components.x_radio.x_radio.has_attr_QMARK_(el,app.components.x_radio.model.attr_disabled),app.components.x_radio.x_radio.has_attr_QMARK_(el,app.components.x_radio.model.attr_required),app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_aria_labelledby),app.components.x_radio.x_radio.has_attr_QMARK_(el,app.components.x_radio.model.attr_checked),app.components.x_radio.x_radio.has_attr_QMARK_(el,app.components.x_radio.model.attr_readonly),app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_value),app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_aria_label),app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_aria_describedby)]));
});
app.components.x_radio.x_radio.render_BANG_ = (function app$components$x_radio$x_radio$render_BANG_(el){
var temp__5823__auto__ = app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_radio.x_radio.goog$module$goog$object.get(refs,"control");
var m = app.components.x_radio.x_radio.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var checked_QMARK_ = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(m);
app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-checked",new cljs.core.Keyword(null,"aria-checked","aria-checked",980530562).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-disabled",(cljs.core.truth_(disabled_QMARK_)?"true":"false"));

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-readonly",(cljs.core.truth_(new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

var temp__5821__auto___23045 = new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23045)){
var v_23046 = temp__5821__auto___23045;
app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-label",v_23046);
} else {
app.components.x_radio.x_radio.remove_attr_BANG_(control_el,"aria-label");
}

var temp__5821__auto___23047 = new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23047)){
var v_23048 = temp__5821__auto___23047;
app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-labelledby",v_23048);
} else {
app.components.x_radio.x_radio.remove_attr_BANG_(control_el,"aria-labelledby");
}

var temp__5821__auto___23049 = new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23049)){
var v_23050 = temp__5821__auto___23049;
app.components.x_radio.x_radio.set_attr_BANG_(control_el,"aria-describedby",v_23050);
} else {
app.components.x_radio.x_radio.remove_attr_BANG_(control_el,"aria-describedby");
}

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_radio.x_radio.set_attr_BANG_(control_el,"disabled","");
} else {
app.components.x_radio.x_radio.remove_attr_BANG_(control_el,"disabled");
}

app.components.x_radio.x_radio.set_bool_attr_BANG_(el,"data-checked",checked_QMARK_);

app.components.x_radio.x_radio.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);

var temp__5823__auto____$1 = app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_internals);
if(cljs.core.truth_(temp__5823__auto____$1)){
var internals = temp__5823__auto____$1;
return internals.setFormValue((cljs.core.truth_(checked_QMARK_)?new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m):null));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_radio.x_radio.group_radios = (function app$components$x_radio$x_radio$group_radios(el){
var name_val = app.components.x_radio.x_radio.get_attr(el,app.components.x_radio.model.attr_name);
var form = (cljs.core.truth_(el.form)?el.form:null);
var scope = (function (){var or__5142__auto__ = form;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return document;
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = name_val;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(name_val,"");
} else {
return and__5140__auto__;
}
})())){
var all = scope.querySelectorAll(app.components.x_radio.model.tag_name);
var result = [];
all.forEach((function (r){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(app.components.x_radio.x_radio.get_attr(r,app.components.x_radio.model.attr_name),name_val)){
return result.push(r);
} else {
return null;
}
}));

return result;
} else {
return [el];
}
});
app.components.x_radio.x_radio.sync_tabindexes_BANG_ = (function app$components$x_radio$x_radio$sync_tabindexes_BANG_(el){
var radios = app.components.x_radio.x_radio.group_radios(el);
var checked = radios.find((function (r){
return app.components.x_radio.x_radio.has_attr_QMARK_(r,app.components.x_radio.model.attr_checked);
}));
var first_r = (((radios.length > (0)))?(radios[(0)]):null);
return radios.forEach((function (r){
var temp__5823__auto__ = app.components.x_radio.x_radio.goog$module$goog$object.get(r,app.components.x_radio.x_radio.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var ctrl = app.components.x_radio.x_radio.goog$module$goog$object.get(refs,"control");
var tab = (cljs.core.truth_(checked)?((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(r,checked))?(0):(-1)):((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(r,first_r))?(0):(-1))
);
return (ctrl.tabIndex = tab);
} else {
return null;
}
}));
});
app.components.x_radio.x_radio.dispatch_cancelable_BANG_ = (function app$components$x_radio$x_radio$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_radio.x_radio.dispatch_BANG_ = (function app$components$x_radio$x_radio$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_radio.x_radio.try_select_BANG_ = (function app$components$x_radio$x_radio$try_select_BANG_(el){
var m = app.components.x_radio.x_radio.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var readonly_QMARK_ = new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m);
var checked_QMARK_ = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__5142__auto__ = disabled_QMARK_;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var or__5142__auto____$1 = readonly_QMARK_;
if(cljs.core.truth_(or__5142__auto____$1)){
return or__5142__auto____$1;
} else {
return checked_QMARK_;
}
}
})())){
return null;
} else {
var value = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m);
var allowed_QMARK_ = app.components.x_radio.x_radio.dispatch_cancelable_BANG_(el,app.components.x_radio.model.event_change_request,({"value": value, "previousChecked": false, "nextChecked": true}));
if(allowed_QMARK_){
app.components.x_radio.x_radio.set_attr_BANG_(el,app.components.x_radio.model.attr_checked,"");

app.components.x_radio.x_radio.render_BANG_(el);

var radios_23051 = app.components.x_radio.x_radio.group_radios(el);
radios_23051.forEach((function (r){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(r,el);
if(and__5140__auto__){
return app.components.x_radio.x_radio.has_attr_QMARK_(r,app.components.x_radio.model.attr_checked);
} else {
return and__5140__auto__;
}
})())){
app.components.x_radio.x_radio.remove_attr_BANG_(r,app.components.x_radio.model.attr_checked);

return app.components.x_radio.x_radio.render_BANG_(r);
} else {
return null;
}
}));

app.components.x_radio.x_radio.sync_tabindexes_BANG_(el);

return app.components.x_radio.x_radio.dispatch_BANG_(el,app.components.x_radio.model.event_change,({"value": value, "checked": true}));
} else {
return null;
}
}
});
app.components.x_radio.x_radio.focus_control_BANG_ = (function app$components$x_radio$x_radio$focus_control_BANG_(r){
var temp__5823__auto__ = app.components.x_radio.x_radio.goog$module$goog$object.get(r,app.components.x_radio.x_radio.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var ctrl = app.components.x_radio.x_radio.goog$module$goog$object.get(refs,"control");
return ctrl.focus();
} else {
return null;
}
});
app.components.x_radio.x_radio.make_keydown_handler = (function app$components$x_radio$x_radio$make_keydown_handler(el){
return (function (evt){
var key = evt.key;
var radios = app.components.x_radio.x_radio.group_radios(el);
var n = radios.length;
var idx = radios.indexOf(el);
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")))){
evt.preventDefault();

return app.components.x_radio.x_radio.try_select_BANG_(el);
} else {
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowDown")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowRight")))){
evt.preventDefault();

if((n > (0))){
var next_idx = cljs.core.mod((idx + (1)),n);
var next_r = (radios[next_idx]);
app.components.x_radio.x_radio.try_select_BANG_(next_r);

return app.components.x_radio.x_radio.focus_control_BANG_(next_r);
} else {
return null;
}
} else {
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowUp")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowLeft")))){
evt.preventDefault();

if((n > (0))){
var prev_idx = cljs.core.mod((idx - (1)),n);
var prev_r = (radios[prev_idx]);
app.components.x_radio.x_radio.try_select_BANG_(prev_r);

return app.components.x_radio.x_radio.focus_control_BANG_(prev_r);
} else {
return null;
}
} else {
return null;
}
}
}
});
});
app.components.x_radio.x_radio.make_click_handler = (function app$components$x_radio$x_radio$make_click_handler(el){
return (function (_evt){
return app.components.x_radio.x_radio.try_select_BANG_(el);
});
});
app.components.x_radio.x_radio.add_listeners_BANG_ = (function app$components$x_radio$x_radio$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_radio.x_radio.goog$module$goog$object.get(refs,"control");
var click_h = app.components.x_radio.x_radio.make_click_handler(el);
var keydown_h = app.components.x_radio.x_radio.make_keydown_handler(el);
var handlers = ({"click": click_h, "keydown": keydown_h});
el.addEventListener("click",click_h);

control_el.addEventListener("keydown",keydown_h);

return app.components.x_radio.x_radio.goog$module$goog$object.set(el,app.components.x_radio.x_radio.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_radio.x_radio.remove_listeners_BANG_ = (function app$components$x_radio$x_radio$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var control_el_23062 = app.components.x_radio.x_radio.goog$module$goog$object.get(refs,"control");
el.removeEventListener("click",app.components.x_radio.x_radio.goog$module$goog$object.get(handlers,"click"));

control_el_23062.removeEventListener("keydown",app.components.x_radio.x_radio.goog$module$goog$object.get(handlers,"keydown"));

return app.components.x_radio.x_radio.goog$module$goog$object.set(el,app.components.x_radio.x_radio.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_radio.x_radio.form_disabled_BANG_ = (function app$components$x_radio$x_radio$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_radio.x_radio.set_bool_attr_BANG_(el,app.components.x_radio.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_radio.x_radio.render_BANG_(el);
});
app.components.x_radio.x_radio.form_reset_BANG_ = (function app$components$x_radio$x_radio$form_reset_BANG_(el){
app.components.x_radio.x_radio.remove_attr_BANG_(el,app.components.x_radio.model.attr_checked);

return app.components.x_radio.x_radio.render_BANG_(el);
});
app.components.x_radio.x_radio.connected_BANG_ = (function app$components$x_radio$x_radio$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_radio.x_radio.goog$module$goog$object.get(el,app.components.x_radio.x_radio.k_refs))){
} else {
app.components.x_radio.x_radio.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_radio.x_radio.goog$module$goog$object.set(el,app.components.x_radio.x_radio.k_internals,el.attachInternals());
} else {
}
}

app.components.x_radio.x_radio.remove_listeners_BANG_(el);

app.components.x_radio.x_radio.add_listeners_BANG_(el);

app.components.x_radio.x_radio.render_BANG_(el);

return app.components.x_radio.x_radio.sync_tabindexes_BANG_(el);
});
app.components.x_radio.x_radio.disconnected_BANG_ = (function app$components$x_radio$x_radio$disconnected_BANG_(el){
return app.components.x_radio.x_radio.remove_listeners_BANG_(el);
});
app.components.x_radio.x_radio.attribute_changed_BANG_ = (function app$components$x_radio$x_radio$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_radio.x_radio.render_BANG_(el);
});
app.components.x_radio.x_radio.define_bool_prop_BANG_ = (function app$components$x_radio$x_radio$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_radio.x_radio.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_radio.x_radio.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_radio.x_radio.define_string_prop_BANG_ = (function app$components$x_radio$x_radio$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_radio.x_radio.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_radio.x_radio.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_radio.x_radio.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_radio.x_radio.element_class = (function app$components$x_radio$x_radio$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_radio.model.observed_attributes;
})}));

app.components.x_radio.x_radio.define_bool_prop_BANG_(proto,"checked",app.components.x_radio.model.attr_checked);

app.components.x_radio.x_radio.define_bool_prop_BANG_(proto,"disabled",app.components.x_radio.model.attr_disabled);

app.components.x_radio.x_radio.define_bool_prop_BANG_(proto,"readOnly",app.components.x_radio.model.attr_readonly);

app.components.x_radio.x_radio.define_bool_prop_BANG_(proto,"required",app.components.x_radio.model.attr_required);

app.components.x_radio.x_radio.define_string_prop_BANG_(proto,"name",app.components.x_radio.model.attr_name);

app.components.x_radio.x_radio.define_string_prop_BANG_(proto,"value",app.components.x_radio.model.attr_value);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_radio.x_radio.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_radio.x_radio.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_radio.x_radio.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_radio.x_radio.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_radio.x_radio.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_radio.x_radio.init_BANG_ = (function app$components$x_radio$x_radio$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_radio.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_radio.model.tag_name,app.components.x_radio.x_radio.element_class());
}
});

//# sourceMappingURL=app.components.x_radio.x_radio.js.map
