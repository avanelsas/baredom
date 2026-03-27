goog.provide('app.components.x_toaster.x_toaster');
goog.scope(function(){
  app.components.x_toaster.x_toaster.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_toaster.x_toaster.k_refs = "__xToasterRefs";
app.components.x_toaster.x_toaster.k_model = "__xToasterModel";
app.components.x_toaster.x_toaster.k_handlers = "__xToasterHandlers";
app.components.x_toaster.x_toaster.style_text = (""+":host{"+"display:flex;"+"position:fixed;"+"z-index:var(--x-toaster-z-index,9000);"+"gap:var(--x-toaster-gap,8px);"+"width:max-content;"+"max-width:var(--x-toaster-max-width,480px);"+"box-sizing:border-box;"+"pointer-events:none;"+"color-scheme:light dark;}"+"::slotted("+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_toaster.model.child_tag)+"){"+"pointer-events:auto;}"+"slot{display:contents;}"+":host([data-position='top-start']){"+"top:var(--x-toaster-inset,16px);"+"inset-inline-start:var(--x-toaster-inset,16px);"+"flex-direction:column-reverse;}"+":host([data-position='top-center']){"+"top:var(--x-toaster-inset,16px);"+"left:50%;"+"transform:translateX(-50%);"+"flex-direction:column-reverse;"+"align-items:center;}"+":host([data-position='top-end']){"+"top:var(--x-toaster-inset,16px);"+"inset-inline-end:var(--x-toaster-inset,16px);"+"flex-direction:column-reverse;}"+":host([data-position='bottom-start']){"+"bottom:var(--x-toaster-inset,16px);"+"inset-inline-start:var(--x-toaster-inset,16px);"+"flex-direction:column;}"+":host([data-position='bottom-center']){"+"bottom:var(--x-toaster-inset,16px);"+"left:50%;"+"transform:translateX(-50%);"+"flex-direction:column;"+"align-items:center;}"+":host([data-position='bottom-end']){"+"bottom:var(--x-toaster-inset,16px);"+"inset-inline-end:var(--x-toaster-inset,16px);"+"flex-direction:column;}");
app.components.x_toaster.x_toaster.init_dom_BANG_ = (function app$components$x_toaster$x_toaster$init_dom_BANG_(el){
var root_23494 = el.attachShadow(({"mode": "open"}));
var style_23495 = document.createElement("style");
var slot_el_23496 = document.createElement("slot");
(style_23495.textContent = app.components.x_toaster.x_toaster.style_text);

root_23494.appendChild(style_23495);

root_23494.appendChild(slot_el_23496);

app.components.x_toaster.x_toaster.goog$module$goog$object.set(el,app.components.x_toaster.x_toaster.k_refs,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"root","root",-448657453),root_23494,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_23496], null));

return null;
});
app.components.x_toaster.x_toaster.ensure_refs_BANG_ = (function app$components$x_toaster$x_toaster$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_toaster.x_toaster.goog$module$goog$object.get(el,app.components.x_toaster.x_toaster.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_toaster.x_toaster.init_dom_BANG_(el);

return app.components.x_toaster.x_toaster.goog$module$goog$object.get(el,app.components.x_toaster.x_toaster.k_refs);
}
});
app.components.x_toaster.x_toaster.read_model = (function app$components$x_toaster$x_toaster$read_model(el){
return app.components.x_toaster.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"position-raw","position-raw",248130625),el.getAttribute(app.components.x_toaster.model.attr_position),new cljs.core.Keyword(null,"max-toasts-raw","max-toasts-raw",-815087069),el.getAttribute(app.components.x_toaster.model.attr_max_toasts),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_toaster.model.attr_label)], null));
});
app.components.x_toaster.x_toaster.apply_model_BANG_ = (function app$components$x_toaster$x_toaster$apply_model_BANG_(el,p__23490){
var map__23491 = p__23490;
var map__23491__$1 = cljs.core.__destructure_map(map__23491);
var m = map__23491__$1;
var position = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23491__$1,new cljs.core.Keyword(null,"position","position",-2011731912));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23491__$1,new cljs.core.Keyword(null,"label","label",1718410804));
app.components.x_toaster.x_toaster.ensure_refs_BANG_(el);

el.setAttribute("role","region");

el.setAttribute("aria-label",label);

el.setAttribute("data-position",position);

app.components.x_toaster.x_toaster.goog$module$goog$object.set(el,app.components.x_toaster.x_toaster.k_model,m);

return null;
});
app.components.x_toaster.x_toaster.update_from_attrs_BANG_ = (function app$components$x_toaster$x_toaster$update_from_attrs_BANG_(el){
var new_m_23498 = app.components.x_toaster.x_toaster.read_model(el);
var old_m_23499 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(el,app.components.x_toaster.x_toaster.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23499,new_m_23498)){
app.components.x_toaster.x_toaster.apply_model_BANG_(el,new_m_23498);
} else {
}

return null;
});
app.components.x_toaster.x_toaster.dispatch_toaster_dismiss_BANG_ = (function app$components$x_toaster$x_toaster$dispatch_toaster_dismiss_BANG_(el,child_detail){
var type = app.components.x_toaster.x_toaster.goog$module$goog$object.get(child_detail,"type");
var reason = app.components.x_toaster.x_toaster.goog$module$goog$object.get(child_detail,"reason");
var heading = app.components.x_toaster.x_toaster.goog$module$goog$object.get(child_detail,"heading");
var message = app.components.x_toaster.x_toaster.goog$module$goog$object.get(child_detail,"message");
var detail = cljs.core.clj__GT_js(app.components.x_toaster.model.dismiss_detail(type,reason,heading,message));
var event = (new CustomEvent(app.components.x_toaster.model.event_dismiss,({"detail": detail, "bubbles": true, "composed": true, "cancelable": true})));
return el.dispatchEvent(event);
});
app.components.x_toaster.x_toaster.on_toast_dismiss = (function app$components$x_toaster$x_toaster$on_toast_dismiss(el,e){
var detail_23500 = e.detail;
var reason_23501 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(detail_23500,"reason");
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(reason_23501,app.components.x_toaster.model.child_dismiss_reason_toaster)){
e.preventDefault();

var not_prevented_23502 = app.components.x_toaster.x_toaster.dispatch_toaster_dismiss_BANG_(el,detail_23500);
if(cljs.core.truth_(not_prevented_23502)){
e.target.dismiss(app.components.x_toaster.model.child_dismiss_reason_toaster);
} else {
}
} else {
}

return null;
});
app.components.x_toaster.x_toaster.evict_oldest_BANG_ = (function app$components$x_toaster$x_toaster$evict_oldest_BANG_(el,max_toasts){
var children = el.querySelectorAll(app.components.x_toaster.model.child_tag);
var count = children.length;
if((count >= max_toasts)){
var oldest_count_23503 = ((count - max_toasts) - (-1));
var i_23504 = (0);
while(true){
if((i_23504 < oldest_count_23503)){
var toast_23505 = (children[i_23504]);
toast_23505.dismiss(app.components.x_toaster.model.child_dismiss_reason_toaster);

var G__23506 = (i_23504 + (1));
i_23504 = G__23506;
continue;
} else {
}
break;
}
} else {
}

return null;
});
app.components.x_toaster.x_toaster.do_toast_BANG_ = (function app$components$x_toaster$x_toaster$do_toast_BANG_(el,opts){
var m = (function (){var or__5142__auto__ = app.components.x_toaster.x_toaster.goog$module$goog$object.get(el,app.components.x_toaster.x_toaster.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toaster.x_toaster.read_model(el);
}
})();
var max_toasts = new cljs.core.Keyword(null,"max-toasts","max-toasts",-1982289139).cljs$core$IFn$_invoke$arity$1(m);
app.components.x_toaster.x_toaster.evict_oldest_BANG_(el,max_toasts);

var toast = document.createElement(app.components.x_toaster.model.child_tag);
var t_23507 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"type");
if(cljs.core.truth_(t_23507)){
toast.setAttribute("type",t_23507);
} else {
}

var h_23508 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"heading");
if(cljs.core.truth_(h_23508)){
toast.setAttribute("heading",h_23508);
} else {
}

var msg_23509 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"message");
if(cljs.core.truth_(msg_23509)){
toast.setAttribute("message",msg_23509);
} else {
}

var icon_23510 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"icon");
if(cljs.core.truth_(icon_23510)){
toast.setAttribute("icon",icon_23510);
} else {
}

var d_23511 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"dismissible");
if(d_23511 === false){
toast.setAttribute("dismissible","false");
} else {
}

var tms_23512 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"timeoutMs");
if(cljs.core.truth_(tms_23512)){
toast.setAttribute("timeout-ms",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(tms_23512)));
} else {
}

var sp_23513 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(opts,"showProgress");
if(cljs.core.truth_(sp_23513)){
toast.setAttribute("show-progress","");
} else {
}

el.appendChild(toast);

return toast;
});
app.components.x_toaster.x_toaster.add_listeners_BANG_ = (function app$components$x_toaster$x_toaster$add_listeners_BANG_(el){
var dismiss_h_23514 = (function (e){
return app.components.x_toaster.x_toaster.on_toast_dismiss(el,e);
});
el.addEventListener(app.components.x_toaster.model.child_event_dismiss,dismiss_h_23514);

app.components.x_toaster.x_toaster.goog$module$goog$object.set(el,app.components.x_toaster.x_toaster.k_handlers,({"dismiss": dismiss_h_23514}));

return null;
});
app.components.x_toaster.x_toaster.remove_listeners_BANG_ = (function app$components$x_toaster$x_toaster$remove_listeners_BANG_(el){
var hs_23515 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(el,app.components.x_toaster.x_toaster.k_handlers);
if(cljs.core.truth_(hs_23515)){
var dismiss_h_23516 = app.components.x_toaster.x_toaster.goog$module$goog$object.get(hs_23515,"dismiss");
if(cljs.core.truth_(dismiss_h_23516)){
el.removeEventListener(app.components.x_toaster.model.child_event_dismiss,dismiss_h_23516);
} else {
}
} else {
}

app.components.x_toaster.x_toaster.goog$module$goog$object.set(el,app.components.x_toaster.x_toaster.k_handlers,null);

return null;
});
app.components.x_toaster.x_toaster.install_property_accessors_BANG_ = (function app$components$x_toaster$x_toaster$install_property_accessors_BANG_(klass){
var proto = klass.prototype;
Object.defineProperty(proto,app.components.x_toaster.model.attr_position,({"get": (function (){
var this$ = this;
return app.components.x_toaster.model.parse_position(this$.getAttribute(app.components.x_toaster.model.attr_position));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_toaster.model.attr_position,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toaster.model.attr_position);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_toaster.model.attr_label,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_toaster.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "Notifications";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_((function (){var and__5140__auto__ = v;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,"");
} else {
return and__5140__auto__;
}
})())){
return this$.setAttribute(app.components.x_toaster.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_toaster.model.attr_label);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"maxToasts",({"get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_toaster.model.parse_max_toasts(this$.getAttribute(app.components.x_toaster.model.attr_max_toasts));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (5);
}
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_toaster.model.attr_max_toasts);
} else {
return this$.setAttribute(app.components.x_toaster.model.attr_max_toasts,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.floor(v))));
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"toast",({"value": (function (opts){
var this$ = this;
return app.components.x_toaster.x_toaster.do_toast_BANG_(this$,(function (){var or__5142__auto__ = opts;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return ({});
}
})());
}), "writable": true, "configurable": true}));
});
app.components.x_toaster.x_toaster.element_class = (function app$components$x_toaster$x_toaster$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_toaster.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_toaster.x_toaster.ensure_refs_BANG_(this$);

app.components.x_toaster.x_toaster.remove_listeners_BANG_(this$);

app.components.x_toaster.x_toaster.add_listeners_BANG_(this$);

app.components.x_toaster.x_toaster.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_toaster.x_toaster.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_toaster.x_toaster.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_toaster.x_toaster.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_toaster.x_toaster.register_BANG_ = (function app$components$x_toaster$x_toaster$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_toaster.model.tag_name))){
} else {
customElements.define(app.components.x_toaster.model.tag_name,app.components.x_toaster.x_toaster.element_class());
}

return null;
});
app.components.x_toaster.x_toaster.init_BANG_ = (function app$components$x_toaster$x_toaster$init_BANG_(){
return app.components.x_toaster.x_toaster.register_BANG_();
});

//# sourceMappingURL=app.components.x_toaster.x_toaster.js.map
