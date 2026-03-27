goog.provide('app.components.x_switch.x_switch');
goog.scope(function(){
  app.components.x_switch.x_switch.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_switch.x_switch.k_refs = "__xSwitchRefs";
app.components.x_switch.x_switch.k_internals = "__xSwitchInternals";
app.components.x_switch.x_switch.k_handlers = "__xSwitchHandlers";
app.components.x_switch.x_switch.style_text = (""+":host{"+"display:inline-block;"+"vertical-align:middle;"+"color-scheme:light dark;"+"--x-switch-track-width:44px;"+"--x-switch-track-height:24px;"+"--x-switch-thumb-size:18px;"+"--x-switch-thumb-offset:3px;"+"--x-switch-track-radius:999px;"+"--x-switch-track-bg:#d1d5db;"+"--x-switch-track-bg-checked:#2563eb;"+"--x-switch-thumb-bg:#ffffff;"+"--x-switch-focus-ring:#60a5fa;"+"--x-switch-disabled-opacity:0.45;"+"--x-switch-transition:background 150ms ease,transform 150ms ease;"+"}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-switch-track-bg:#4b5563;"+"--x-switch-track-bg-checked:#3b82f6;"+"--x-switch-thumb-bg:#f9fafb;"+"--x-switch-focus-ring:#93c5fd;"+"}"+"}"+"[part=control]{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"padding:0;"+"cursor:pointer;"+"}"+"[part=control][disabled]{"+"cursor:default;"+"opacity:var(--x-switch-disabled-opacity);"+"}"+"[part=control]:focus-visible{"+"outline:none;"+"box-shadow:0 0 0 3px var(--x-switch-focus-ring);"+"border-radius:calc(var(--x-switch-track-radius) + 2px);"+"}"+"[part=track]{"+"position:relative;"+"display:inline-block;"+"width:var(--x-switch-track-width);"+"height:var(--x-switch-track-height);"+"border-radius:var(--x-switch-track-radius);"+"background:var(--x-switch-track-bg);"+"transition:background 150ms ease;"+"flex-shrink:0;"+"}"+":host([data-checked]) [part=track]{"+"background:var(--x-switch-track-bg-checked);"+"}"+"[part=thumb]{"+"position:absolute;"+"top:var(--x-switch-thumb-offset);"+"left:var(--x-switch-thumb-offset);"+"width:var(--x-switch-thumb-size);"+"height:var(--x-switch-thumb-size);"+"border-radius:50%;"+"background:var(--x-switch-thumb-bg);"+"box-shadow:0 1px 3px rgba(0,0,0,0.25);"+"transition:transform 150ms ease;"+"}"+":host([data-checked]) [part=thumb]{"+"transform:translateX(calc(var(--x-switch-track-width) - var(--x-switch-thumb-size) - calc(var(--x-switch-thumb-offset) * 2)));"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=track]{transition:none;}"+"[part=thumb]{transition:none;}"+"}");
app.components.x_switch.x_switch.make_el = (function app$components$x_switch$x_switch$make_el(tag){
return document.createElement(tag);
});
app.components.x_switch.x_switch.set_attr_BANG_ = (function app$components$x_switch$x_switch$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_switch.x_switch.remove_attr_BANG_ = (function app$components$x_switch$x_switch$remove_attr_BANG_(el,attr){
return el.removeAttribute(attr);
});
app.components.x_switch.x_switch.has_attr_QMARK_ = (function app$components$x_switch$x_switch$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_switch.x_switch.get_attr = (function app$components$x_switch$x_switch$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_switch.x_switch.set_bool_attr_BANG_ = (function app$components$x_switch$x_switch$set_bool_attr_BANG_(el,attr,value){
if(cljs.core.truth_(value)){
return app.components.x_switch.x_switch.set_attr_BANG_(el,attr,"");
} else {
return app.components.x_switch.x_switch.remove_attr_BANG_(el,attr);
}
});
app.components.x_switch.x_switch.make_shadow_BANG_ = (function app$components$x_switch$x_switch$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_switch.x_switch.make_el("style");
var control_el = app.components.x_switch.x_switch.make_el("button");
var track_el = app.components.x_switch.x_switch.make_el("span");
var thumb_el = app.components.x_switch.x_switch.make_el("span");
(style_el.textContent = app.components.x_switch.x_switch.style_text);

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"part","control");

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"type","button");

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"role","switch");

app.components.x_switch.x_switch.set_attr_BANG_(track_el,"part","track");

app.components.x_switch.x_switch.set_attr_BANG_(thumb_el,"part","thumb");

track_el.appendChild(thumb_el);

control_el.appendChild(track_el);

root.appendChild(style_el);

root.appendChild(control_el);

var refs = ({"root": root, "control": control_el, "track": track_el, "thumb": thumb_el});
app.components.x_switch.x_switch.goog$module$goog$object.set(el,app.components.x_switch.x_switch.k_refs,refs);

return refs;
});
app.components.x_switch.x_switch.read_model = (function app$components$x_switch$x_switch$read_model(el){
return app.components.x_switch.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196),new cljs.core.Keyword(null,"aria-labelledby-raw","aria-labelledby-raw",-107265075),new cljs.core.Keyword(null,"checked-present?","checked-present?",-1155676496),new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394),new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860)],[app.components.x_switch.x_switch.get_attr(el,app.components.x_switch.model.attr_name),app.components.x_switch.x_switch.has_attr_QMARK_(el,app.components.x_switch.model.attr_disabled),app.components.x_switch.x_switch.has_attr_QMARK_(el,app.components.x_switch.model.attr_required),app.components.x_switch.x_switch.get_attr(el,app.components.x_switch.model.attr_aria_labelledby),app.components.x_switch.x_switch.has_attr_QMARK_(el,app.components.x_switch.model.attr_checked),app.components.x_switch.x_switch.has_attr_QMARK_(el,app.components.x_switch.model.attr_readonly),app.components.x_switch.x_switch.get_attr(el,app.components.x_switch.model.attr_value),app.components.x_switch.x_switch.get_attr(el,app.components.x_switch.model.attr_aria_label),app.components.x_switch.x_switch.get_attr(el,app.components.x_switch.model.attr_aria_describedby)]));
});
app.components.x_switch.x_switch.render_BANG_ = (function app$components$x_switch$x_switch$render_BANG_(el){
var temp__5823__auto__ = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_switch.x_switch.goog$module$goog$object.get(refs,"control");
var m = app.components.x_switch.x_switch.read_model(el);
var disabled_QMARK_ = new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m);
var checked_QMARK_ = new cljs.core.Keyword(null,"checked?","checked?",2024809091).cljs$core$IFn$_invoke$arity$1(m);
app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-checked",new cljs.core.Keyword(null,"aria-checked","aria-checked",980530562).cljs$core$IFn$_invoke$arity$1(m));

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-disabled",(cljs.core.truth_(disabled_QMARK_)?"true":"false"));

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-required",(cljs.core.truth_(new cljs.core.Keyword(null,"required?","required?",-872514462).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-readonly",(cljs.core.truth_(new cljs.core.Keyword(null,"readonly?","readonly?",988057827).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

var temp__5821__auto___23264 = new cljs.core.Keyword(null,"aria-label","aria-label",455891514).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23264)){
var v_23265 = temp__5821__auto___23264;
app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-label",v_23265);
} else {
app.components.x_switch.x_switch.remove_attr_BANG_(control_el,"aria-label");
}

var temp__5821__auto___23266 = new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23266)){
var v_23267 = temp__5821__auto___23266;
app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-labelledby",v_23267);
} else {
app.components.x_switch.x_switch.remove_attr_BANG_(control_el,"aria-labelledby");
}

var temp__5821__auto___23268 = new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5821__auto___23268)){
var v_23269 = temp__5821__auto___23268;
app.components.x_switch.x_switch.set_attr_BANG_(control_el,"aria-describedby",v_23269);
} else {
app.components.x_switch.x_switch.remove_attr_BANG_(control_el,"aria-describedby");
}

(control_el.tabIndex = (cljs.core.truth_(disabled_QMARK_)?(-1):(0)));

if(cljs.core.truth_(disabled_QMARK_)){
app.components.x_switch.x_switch.set_attr_BANG_(control_el,"disabled","");
} else {
app.components.x_switch.x_switch.remove_attr_BANG_(control_el,"disabled");
}

app.components.x_switch.x_switch.set_bool_attr_BANG_(el,"data-checked",checked_QMARK_);

app.components.x_switch.x_switch.set_bool_attr_BANG_(el,"data-disabled",disabled_QMARK_);

var temp__5823__auto____$1 = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_internals);
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
app.components.x_switch.x_switch.dispatch_cancelable_BANG_ = (function app$components$x_switch$x_switch$dispatch_cancelable_BANG_(el,event_name,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_switch.x_switch.dispatch_BANG_ = (function app$components$x_switch$x_switch$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_switch.x_switch.try_toggle_BANG_ = (function app$components$x_switch$x_switch$try_toggle_BANG_(el){
var m = app.components.x_switch.x_switch.read_model(el);
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
var next_checked = cljs.core.not(checked_QMARK_);
var value = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(m);
var allowed_QMARK_ = app.components.x_switch.x_switch.dispatch_cancelable_BANG_(el,app.components.x_switch.model.event_change_request,({"value": value, "previousChecked": checked_QMARK_, "nextChecked": next_checked}));
if(allowed_QMARK_){
app.components.x_switch.x_switch.set_bool_attr_BANG_(el,app.components.x_switch.model.attr_checked,next_checked);

app.components.x_switch.x_switch.render_BANG_(el);

return app.components.x_switch.x_switch.dispatch_BANG_(el,app.components.x_switch.model.event_change,({"value": value, "checked": next_checked}));
} else {
return null;
}
}
});
app.components.x_switch.x_switch.wire_external_label_BANG_ = (function app$components$x_switch$x_switch$wire_external_label_BANG_(el){
var id = app.components.x_switch.x_switch.get_attr(el,"id");
if(cljs.core.truth_(id)){
var lbl = document.querySelector((""+"label[for='"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(id)+"']"));
if(cljs.core.truth_(lbl)){
var lid = (function (){var or__5142__auto__ = app.components.x_switch.x_switch.get_attr(lbl,"id");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var gen_id = (""+"x-sw-lbl-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(id));
app.components.x_switch.x_switch.set_attr_BANG_(lbl,"id",gen_id);

return gen_id;
}
})();
var temp__5823__auto__ = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
return app.components.x_switch.x_switch.set_attr_BANG_(app.components.x_switch.x_switch.goog$module$goog$object.get(refs,"control"),"aria-labelledby",lid);
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
app.components.x_switch.x_switch.make_click_handler = (function app$components$x_switch$x_switch$make_click_handler(el){
return (function (_evt){
return app.components.x_switch.x_switch.try_toggle_BANG_(el);
});
});
app.components.x_switch.x_switch.make_keydown_handler = (function app$components$x_switch$x_switch$make_keydown_handler(el){
return (function (evt){
var key = evt.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")))){
evt.preventDefault();

return app.components.x_switch.x_switch.try_toggle_BANG_(el);
} else {
return null;
}
});
});
app.components.x_switch.x_switch.add_listeners_BANG_ = (function app$components$x_switch$x_switch$add_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var control_el = app.components.x_switch.x_switch.goog$module$goog$object.get(refs,"control");
var click_h = app.components.x_switch.x_switch.make_click_handler(el);
var keydown_h = app.components.x_switch.x_switch.make_keydown_handler(el);
var handlers = ({"click": click_h, "keydown": keydown_h});
control_el.addEventListener("click",click_h);

control_el.addEventListener("keydown",keydown_h);

return app.components.x_switch.x_switch.goog$module$goog$object.set(el,app.components.x_switch.x_switch.k_handlers,handlers);
} else {
return null;
}
});
app.components.x_switch.x_switch.remove_listeners_BANG_ = (function app$components$x_switch$x_switch$remove_listeners_BANG_(el){
var temp__5823__auto__ = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_refs);
if(cljs.core.truth_(temp__5823__auto__)){
var refs = temp__5823__auto__;
var temp__5823__auto____$1 = app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_handlers);
if(cljs.core.truth_(temp__5823__auto____$1)){
var handlers = temp__5823__auto____$1;
var control_el_23271 = app.components.x_switch.x_switch.goog$module$goog$object.get(refs,"control");
control_el_23271.removeEventListener("click",app.components.x_switch.x_switch.goog$module$goog$object.get(handlers,"click"));

control_el_23271.removeEventListener("keydown",app.components.x_switch.x_switch.goog$module$goog$object.get(handlers,"keydown"));

return app.components.x_switch.x_switch.goog$module$goog$object.set(el,app.components.x_switch.x_switch.k_handlers,null);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_switch.x_switch.form_disabled_BANG_ = (function app$components$x_switch$x_switch$form_disabled_BANG_(el,disabled_QMARK_){
app.components.x_switch.x_switch.set_bool_attr_BANG_(el,app.components.x_switch.model.attr_disabled,cljs.core.boolean$(disabled_QMARK_));

return app.components.x_switch.x_switch.render_BANG_(el);
});
app.components.x_switch.x_switch.form_reset_BANG_ = (function app$components$x_switch$x_switch$form_reset_BANG_(el){
app.components.x_switch.x_switch.remove_attr_BANG_(el,app.components.x_switch.model.attr_checked);

return app.components.x_switch.x_switch.render_BANG_(el);
});
app.components.x_switch.x_switch.connected_BANG_ = (function app$components$x_switch$x_switch$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_switch.x_switch.goog$module$goog$object.get(el,app.components.x_switch.x_switch.k_refs))){
} else {
app.components.x_switch.x_switch.make_shadow_BANG_(el);

if(cljs.core.truth_(el.attachInternals)){
app.components.x_switch.x_switch.goog$module$goog$object.set(el,app.components.x_switch.x_switch.k_internals,el.attachInternals());
} else {
}
}

app.components.x_switch.x_switch.remove_listeners_BANG_(el);

app.components.x_switch.x_switch.add_listeners_BANG_(el);

app.components.x_switch.x_switch.wire_external_label_BANG_(el);

return app.components.x_switch.x_switch.render_BANG_(el);
});
app.components.x_switch.x_switch.disconnected_BANG_ = (function app$components$x_switch$x_switch$disconnected_BANG_(el){
return app.components.x_switch.x_switch.remove_listeners_BANG_(el);
});
app.components.x_switch.x_switch.attribute_changed_BANG_ = (function app$components$x_switch$x_switch$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_switch.x_switch.render_BANG_(el);
});
app.components.x_switch.x_switch.define_bool_prop_BANG_ = (function app$components$x_switch$x_switch$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_switch.x_switch.has_attr_QMARK_(this$,attr_name);
}), "set": (function (v){
var this$ = this;
return app.components.x_switch.x_switch.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(v));
})}));
});
app.components.x_switch.x_switch.define_string_prop_BANG_ = (function app$components$x_switch$x_switch$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_switch.x_switch.get_attr(this$,attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if((((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,undefined)))){
return app.components.x_switch.x_switch.set_attr_BANG_(this$,attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return app.components.x_switch.x_switch.remove_attr_BANG_(this$,attr_name);
}
})}));
});
app.components.x_switch.x_switch.element_class = (function app$components$x_switch$x_switch$element_class(){
var cls = (class extends HTMLElement {});
var proto = cls.prototype;
(cls.formAssociated = true);

Object.defineProperty(cls,"observedAttributes",({"get": (function (){
return app.components.x_switch.model.observed_attributes;
})}));

app.components.x_switch.x_switch.define_bool_prop_BANG_(proto,"checked",app.components.x_switch.model.attr_checked);

app.components.x_switch.x_switch.define_bool_prop_BANG_(proto,"disabled",app.components.x_switch.model.attr_disabled);

app.components.x_switch.x_switch.define_bool_prop_BANG_(proto,"readOnly",app.components.x_switch.model.attr_readonly);

app.components.x_switch.x_switch.define_bool_prop_BANG_(proto,"required",app.components.x_switch.model.attr_required);

app.components.x_switch.x_switch.define_string_prop_BANG_(proto,"name",app.components.x_switch.model.attr_name);

app.components.x_switch.x_switch.define_string_prop_BANG_(proto,"value",app.components.x_switch.model.attr_value);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_switch.x_switch.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_switch.x_switch.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_switch.x_switch.attribute_changed_BANG_(this$,n,o,v);
}));

(proto["formDisabledCallback"] = (function (d){
var this$ = this;
return app.components.x_switch.x_switch.form_disabled_BANG_(this$,d);
}));

(proto["formResetCallback"] = (function (){
var this$ = this;
return app.components.x_switch.x_switch.form_reset_BANG_(this$);
}));

return cls;
});
app.components.x_switch.x_switch.init_BANG_ = (function app$components$x_switch$x_switch$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_switch.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_switch.model.tag_name,app.components.x_switch.x_switch.element_class());
}
});

//# sourceMappingURL=app.components.x_switch.x_switch.js.map
