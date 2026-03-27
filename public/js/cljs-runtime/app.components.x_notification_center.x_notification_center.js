goog.provide('app.components.x_notification_center.x_notification_center');
goog.scope(function(){
  app.components.x_notification_center.x_notification_center.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_notification_center.x_notification_center.k_refs = "__xNcRefs";
app.components.x_notification_center.x_notification_center.k_handlers = "__xNcHandlers";
app.components.x_notification_center.x_notification_center.k_max = "__xNcMax";
app.components.x_notification_center.x_notification_center.next_id_counter = ({"v": (0)});
app.components.x_notification_center.x_notification_center.next_id_BANG_ = (function app$components$x_notification_center$x_notification_center$next_id_BANG_(){
var id = (app.components.x_notification_center.x_notification_center.next_id_counter["v"]);
(app.components.x_notification_center.x_notification_center.next_id_counter["v"] = (id + (1)));

return (""+"xnc-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(id));
});
app.components.x_notification_center.x_notification_center.style_text = (""+":host{"+"display:block;"+"position:fixed;"+"z-index:var(--x-notification-center-z-index,9999);"+"--x-notification-center-width:360px;"+"--x-notification-center-gap:8px;"+"--x-notification-center-offset-x:16px;"+"--x-notification-center-offset-y:16px;"+"--x-notification-center-z-index:9999;"+"pointer-events:none;"+"}"+":host([data-position='top-right']){"+"top:var(--x-notification-center-offset-y);"+"right:var(--x-notification-center-offset-x);"+"}"+":host([data-position='top-left']){"+"top:var(--x-notification-center-offset-y);"+"left:var(--x-notification-center-offset-x);"+"}"+":host([data-position='bottom-right']){"+"bottom:var(--x-notification-center-offset-y);"+"right:var(--x-notification-center-offset-x);"+"}"+":host([data-position='bottom-left']){"+"bottom:var(--x-notification-center-offset-y);"+"left:var(--x-notification-center-offset-x);"+"}"+":host([data-position='top-center']){"+"top:var(--x-notification-center-offset-y);"+"left:50%;"+"transform:translateX(-50%);"+"}"+":host([data-position='bottom-center']){"+"bottom:var(--x-notification-center-offset-y);"+"left:50%;"+"transform:translateX(-50%);"+"}"+"[part=container]{"+"display:flex;"+"flex-direction:column;"+"gap:var(--x-notification-center-gap);"+"width:var(--x-notification-center-width);"+"pointer-events:auto;"+"}"+":host([data-position='bottom-right']) [part=container],"+":host([data-position='bottom-left']) [part=container],"+":host([data-position='bottom-center']) [part=container]{"+"flex-direction:column-reverse;"+"}");
app.components.x_notification_center.x_notification_center.init_dom_BANG_ = (function app$components$x_notification_center$x_notification_center$init_dom_BANG_(el){
var root_22834 = el.attachShadow(({"mode": "open"}));
var style_22835 = document.createElement("style");
var container_22836 = document.createElement("div");
(style_22835.textContent = app.components.x_notification_center.x_notification_center.style_text);

container_22836.setAttribute("part","container");

root_22834.appendChild(style_22835);

root_22834.appendChild(container_22836);

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_refs,({"root": root_22834, "container": container_22836}));

return null;
});
app.components.x_notification_center.x_notification_center.ensure_refs_BANG_ = (function app$components$x_notification_center$x_notification_center$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_notification_center.x_notification_center.init_dom_BANG_(el);

return app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_refs);
}
});
app.components.x_notification_center.x_notification_center.apply_position_BANG_ = (function app$components$x_notification_center$x_notification_center$apply_position_BANG_(el){
var raw_22838 = el.getAttribute(app.components.x_notification_center.model.attr_position);
var pos_22839 = app.components.x_notification_center.model.parse_position(raw_22838);
el.setAttribute(app.components.x_notification_center.model.data_position,pos_22839);

return null;
});
app.components.x_notification_center.x_notification_center.dispatch_BANG_ = (function app$components$x_notification_center$x_notification_center$dispatch_BANG_(el,event_name,detail){
el.dispatchEvent((new CustomEvent(event_name,({"detail": cljs.core.clj__GT_js(detail), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_notification_center.x_notification_center.on_alert_dismiss = (function app$components$x_notification_center$x_notification_center$on_alert_dismiss(el,e){
var refs_22845 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22846 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22845,"container");
var path_22847 = e.composedPath();
var alert_22848__$1 = (path_22847[(0)]);
var id_22849 = alert_22848__$1.getAttribute(app.components.x_notification_center.model.data_notification_id);
var new_count_22850 = (container_22846.querySelectorAll(app.components.x_notification_center.model.alert_tag).length - (1));
var type_val_22851 = alert_22848__$1.getAttribute("type");
var reason_22852 = e.detail.reason;
var text_val_22853 = e.detail.text;
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"id","id",-1388402092),id_22849,new cljs.core.Keyword(null,"type","type",1174270348),(function (){var or__5142__auto__ = type_val_22851;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
})(),new cljs.core.Keyword(null,"reason","reason",-2070751759),(function (){var or__5142__auto__ = reason_22852;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"text","text",-1790561697),(function (){var or__5142__auto__ = text_val_22853;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"count","count",2139924085),new_count_22850], null));

if((new_count_22850 === (0))){
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_empty,cljs.core.PersistentArrayMap.EMPTY);
} else {
}

return null;
});
app.components.x_notification_center.x_notification_center.add_listeners_BANG_ = (function app$components$x_notification_center$x_notification_center$add_listeners_BANG_(el){
var refs_22860 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var root_22861 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22860,"root");
var dismiss_h_22862 = (function (e){
return app.components.x_notification_center.x_notification_center.on_alert_dismiss(el,e);
});
root_22861.addEventListener(app.components.x_alert.model.event_dismiss,dismiss_h_22862,({"capture": false}));

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_handlers,({"dismiss": dismiss_h_22862}));

return null;
});
app.components.x_notification_center.x_notification_center.remove_listeners_BANG_ = (function app$components$x_notification_center$x_notification_center$remove_listeners_BANG_(el){
var hs_22863 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_handlers);
var refs_22864 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22863;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22864;
} else {
return and__5140__auto__;
}
})())){
var root_22865 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22864,"root");
var dismiss_h_22866 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(hs_22863,"dismiss");
if(cljs.core.truth_(dismiss_h_22866)){
root_22865.removeEventListener(app.components.x_alert.model.event_dismiss,dismiss_h_22866,({"capture": false}));
} else {
}
} else {
}

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_handlers,null);

return null;
});
app.components.x_notification_center.x_notification_center.push_BANG_ = (function app$components$x_notification_center$x_notification_center$push_BANG_(el,opts){
var refs_22868 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22869 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22868,"container");
var current_max_22870 = (function (){var or__5142__auto__ = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_notification_center.model.default_max;
}
})();
var current_count_22871 = container_22869.querySelectorAll(app.components.x_notification_center.model.alert_tag).length;
if((current_count_22871 < current_max_22870)){
var alert_22872__$1 = document.createElement(app.components.x_notification_center.model.alert_tag);
var id_22873 = (function (){var or__5142__auto__ = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"id");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_notification_center.x_notification_center.next_id_BANG_();
}
})();
var type_val_22874 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"type");
var text_val_22875 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"text");
var icon_val_22876 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"icon");
var timeout_val_22877 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"timeoutMs");
var dismissible_val_22878 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"dismissible");
alert_22872__$1.setAttribute(app.components.x_notification_center.model.data_notification_id,id_22873);

if(cljs.core.truth_(type_val_22874)){
alert_22872__$1.setAttribute("type",type_val_22874);
} else {
}

if(cljs.core.truth_(text_val_22875)){
alert_22872__$1.setAttribute("text",text_val_22875);
} else {
}

if(cljs.core.truth_(icon_val_22876)){
alert_22872__$1.setAttribute("icon",icon_val_22876);
} else {
}

if(cljs.core.truth_(timeout_val_22877)){
alert_22872__$1.setAttribute("timeout-ms",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((timeout_val_22877 | 0))));
} else {
}

if(dismissible_val_22878 === false){
alert_22872__$1.setAttribute("dismissible","false");
} else {
}

container_22869.appendChild(alert_22872__$1);

var new_count_22879 = container_22869.querySelectorAll(app.components.x_notification_center.model.alert_tag).length;
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_push,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"id","id",-1388402092),id_22873,new cljs.core.Keyword(null,"count","count",2139924085),new_count_22879], null));
} else {
}

return null;
});
app.components.x_notification_center.x_notification_center.clear_BANG_ = (function app$components$x_notification_center$x_notification_center$clear_BANG_(el){
var refs_22880 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22881 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22880,"container");
var alerts_22882 = container_22881.querySelectorAll(app.components.x_notification_center.model.alert_tag);
var seq__22800_22883 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(alerts_22882));
var chunk__22801_22884 = null;
var count__22802_22885 = (0);
var i__22803_22886 = (0);
while(true){
if((i__22803_22886 < count__22802_22885)){
var alert_22887__$1 = chunk__22801_22884.cljs$core$IIndexed$_nth$arity$2(null,i__22803_22886);
alert_22887__$1.remove();


var G__22888 = seq__22800_22883;
var G__22889 = chunk__22801_22884;
var G__22890 = count__22802_22885;
var G__22891 = (i__22803_22886 + (1));
seq__22800_22883 = G__22888;
chunk__22801_22884 = G__22889;
count__22802_22885 = G__22890;
i__22803_22886 = G__22891;
continue;
} else {
var temp__5823__auto___22892 = cljs.core.seq(seq__22800_22883);
if(temp__5823__auto___22892){
var seq__22800_22893__$1 = temp__5823__auto___22892;
if(cljs.core.chunked_seq_QMARK_(seq__22800_22893__$1)){
var c__5673__auto___22894 = cljs.core.chunk_first(seq__22800_22893__$1);
var G__22895 = cljs.core.chunk_rest(seq__22800_22893__$1);
var G__22896 = c__5673__auto___22894;
var G__22897 = cljs.core.count(c__5673__auto___22894);
var G__22898 = (0);
seq__22800_22883 = G__22895;
chunk__22801_22884 = G__22896;
count__22802_22885 = G__22897;
i__22803_22886 = G__22898;
continue;
} else {
var alert_22899__$1 = cljs.core.first(seq__22800_22893__$1);
alert_22899__$1.remove();


var G__22900 = cljs.core.next(seq__22800_22893__$1);
var G__22901 = null;
var G__22902 = (0);
var G__22903 = (0);
seq__22800_22883 = G__22900;
chunk__22801_22884 = G__22901;
count__22802_22885 = G__22902;
i__22803_22886 = G__22903;
continue;
}
} else {
}
}
break;
}

return null;
});
app.components.x_notification_center.x_notification_center.install_property_accessors_BANG_ = (function app$components$x_notification_center$x_notification_center$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_notification_center.model.attr_position,({"get": (function (){
var this$ = this;
return app.components.x_notification_center.model.parse_position(this$.getAttribute(app.components.x_notification_center.model.attr_position));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_notification_center.model.attr_position,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_notification_center.model.attr_position);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_notification_center.model.attr_max,({"get": (function (){
var this$ = this;
return app.components.x_notification_center.model.parse_max(this$.getAttribute(app.components.x_notification_center.model.attr_max));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_notification_center.model.attr_max,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v | 0))));
} else {
return this$.removeAttribute(app.components.x_notification_center.model.attr_max);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"count",({"get": (function (){
var this$ = this;
var refs = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(this$,app.components.x_notification_center.x_notification_center.k_refs);
if(cljs.core.truth_(refs)){
var container = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs,"container");
return container.querySelectorAll(app.components.x_notification_center.model.alert_tag).length;
} else {
return (0);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_notification_center.x_notification_center.element_class = (function app$components$x_notification_center$x_notification_center$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_notification_center.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(this$);

app.components.x_notification_center.x_notification_center.remove_listeners_BANG_(this$);

app.components.x_notification_center.x_notification_center.add_listeners_BANG_(this$);

app.components.x_notification_center.x_notification_center.apply_position_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_notification_center.x_notification_center.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (n,_old_val,_new_val){
var this$ = this;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(n,app.components.x_notification_center.model.attr_position)){
app.components.x_notification_center.x_notification_center.apply_position_BANG_(this$);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(n,app.components.x_notification_center.model.attr_max)){
app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(this$,app.components.x_notification_center.x_notification_center.k_max,app.components.x_notification_center.model.parse_max(this$.getAttribute(app.components.x_notification_center.model.attr_max)));
} else {
}
}

return null;
}));

app.components.x_notification_center.x_notification_center.install_property_accessors_BANG_(klass.prototype);

(klass.prototype.push = (function (opts){
var this$ = this;
return app.components.x_notification_center.x_notification_center.push_BANG_(this$,opts);
}));

(klass.prototype.clear = (function (){
var this$ = this;
return app.components.x_notification_center.x_notification_center.clear_BANG_(this$);
}));

return klass;
});
app.components.x_notification_center.x_notification_center.register_BANG_ = (function app$components$x_notification_center$x_notification_center$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_notification_center.model.tag_name))){
} else {
customElements.define(app.components.x_notification_center.model.tag_name,app.components.x_notification_center.x_notification_center.element_class());
}

return null;
});
app.components.x_notification_center.x_notification_center.init_BANG_ = (function app$components$x_notification_center$x_notification_center$init_BANG_(){
return app.components.x_notification_center.x_notification_center.register_BANG_();
});

//# sourceMappingURL=app.components.x_notification_center.x_notification_center.js.map
