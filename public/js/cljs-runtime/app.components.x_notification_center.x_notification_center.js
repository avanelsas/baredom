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
var root_22806 = el.attachShadow(({"mode": "open"}));
var style_22807 = document.createElement("style");
var container_22808 = document.createElement("div");
(style_22807.textContent = app.components.x_notification_center.x_notification_center.style_text);

container_22808.setAttribute("part","container");

root_22806.appendChild(style_22807);

root_22806.appendChild(container_22808);

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_refs,({"root": root_22806, "container": container_22808}));

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
var raw_22810 = el.getAttribute(app.components.x_notification_center.model.attr_position);
var pos_22811 = app.components.x_notification_center.model.parse_position(raw_22810);
el.setAttribute(app.components.x_notification_center.model.data_position,pos_22811);

return null;
});
app.components.x_notification_center.x_notification_center.dispatch_BANG_ = (function app$components$x_notification_center$x_notification_center$dispatch_BANG_(el,event_name,detail){
el.dispatchEvent((new CustomEvent(event_name,({"detail": cljs.core.clj__GT_js(detail), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_notification_center.x_notification_center.on_alert_dismiss = (function app$components$x_notification_center$x_notification_center$on_alert_dismiss(el,e){
var refs_22812 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22813 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22812,"container");
var path_22814 = e.composedPath();
var alert_22815__$1 = (path_22814[(0)]);
var id_22816 = alert_22815__$1.getAttribute(app.components.x_notification_center.model.data_notification_id);
var new_count_22817 = (container_22813.querySelectorAll(app.components.x_notification_center.model.alert_tag).length - (1));
var type_val_22818 = alert_22815__$1.getAttribute("type");
var reason_22819 = e.detail.reason;
var text_val_22820 = e.detail.text;
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"id","id",-1388402092),id_22816,new cljs.core.Keyword(null,"type","type",1174270348),(function (){var or__5142__auto__ = type_val_22818;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
})(),new cljs.core.Keyword(null,"reason","reason",-2070751759),(function (){var or__5142__auto__ = reason_22819;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"text","text",-1790561697),(function (){var or__5142__auto__ = text_val_22820;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"count","count",2139924085),new_count_22817], null));

if((new_count_22817 === (0))){
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_empty,cljs.core.PersistentArrayMap.EMPTY);
} else {
}

return null;
});
app.components.x_notification_center.x_notification_center.add_listeners_BANG_ = (function app$components$x_notification_center$x_notification_center$add_listeners_BANG_(el){
var refs_22821 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var root_22822 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22821,"root");
var dismiss_h_22823 = (function (e){
return app.components.x_notification_center.x_notification_center.on_alert_dismiss(el,e);
});
root_22822.addEventListener(app.components.x_alert.model.event_dismiss,dismiss_h_22823,({"capture": false}));

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_handlers,({"dismiss": dismiss_h_22823}));

return null;
});
app.components.x_notification_center.x_notification_center.remove_listeners_BANG_ = (function app$components$x_notification_center$x_notification_center$remove_listeners_BANG_(el){
var hs_22825 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_handlers);
var refs_22826 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_22825;
if(cljs.core.truth_(and__5140__auto__)){
return refs_22826;
} else {
return and__5140__auto__;
}
})())){
var root_22827 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22826,"root");
var dismiss_h_22828 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(hs_22825,"dismiss");
if(cljs.core.truth_(dismiss_h_22828)){
root_22827.removeEventListener(app.components.x_alert.model.event_dismiss,dismiss_h_22828,({"capture": false}));
} else {
}
} else {
}

app.components.x_notification_center.x_notification_center.goog$module$goog$object.set(el,app.components.x_notification_center.x_notification_center.k_handlers,null);

return null;
});
app.components.x_notification_center.x_notification_center.push_BANG_ = (function app$components$x_notification_center$x_notification_center$push_BANG_(el,opts){
var refs_22829 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22830 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22829,"container");
var current_max_22831 = (function (){var or__5142__auto__ = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(el,app.components.x_notification_center.x_notification_center.k_max);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_notification_center.model.default_max;
}
})();
var current_count_22832 = container_22830.querySelectorAll(app.components.x_notification_center.model.alert_tag).length;
if((current_count_22832 < current_max_22831)){
var alert_22834__$1 = document.createElement(app.components.x_notification_center.model.alert_tag);
var id_22835 = (function (){var or__5142__auto__ = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"id");
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_notification_center.x_notification_center.next_id_BANG_();
}
})();
var type_val_22836 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"type");
var text_val_22837 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"text");
var icon_val_22838 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"icon");
var timeout_val_22839 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"timeoutMs");
var dismissible_val_22840 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(opts,"dismissible");
alert_22834__$1.setAttribute(app.components.x_notification_center.model.data_notification_id,id_22835);

if(cljs.core.truth_(type_val_22836)){
alert_22834__$1.setAttribute("type",type_val_22836);
} else {
}

if(cljs.core.truth_(text_val_22837)){
alert_22834__$1.setAttribute("text",text_val_22837);
} else {
}

if(cljs.core.truth_(icon_val_22838)){
alert_22834__$1.setAttribute("icon",icon_val_22838);
} else {
}

if(cljs.core.truth_(timeout_val_22839)){
alert_22834__$1.setAttribute("timeout-ms",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((timeout_val_22839 | 0))));
} else {
}

if(dismissible_val_22840 === false){
alert_22834__$1.setAttribute("dismissible","false");
} else {
}

container_22830.appendChild(alert_22834__$1);

var new_count_22846 = container_22830.querySelectorAll(app.components.x_notification_center.model.alert_tag).length;
app.components.x_notification_center.x_notification_center.dispatch_BANG_(el,app.components.x_notification_center.model.event_push,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"id","id",-1388402092),id_22835,new cljs.core.Keyword(null,"count","count",2139924085),new_count_22846], null));
} else {
}

return null;
});
app.components.x_notification_center.x_notification_center.clear_BANG_ = (function app$components$x_notification_center$x_notification_center$clear_BANG_(el){
var refs_22857 = app.components.x_notification_center.x_notification_center.ensure_refs_BANG_(el);
var container_22858 = app.components.x_notification_center.x_notification_center.goog$module$goog$object.get(refs_22857,"container");
var alerts_22859 = container_22858.querySelectorAll(app.components.x_notification_center.model.alert_tag);
var seq__22800_22860 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(alerts_22859));
var chunk__22801_22861 = null;
var count__22802_22862 = (0);
var i__22803_22863 = (0);
while(true){
if((i__22803_22863 < count__22802_22862)){
var alert_22864__$1 = chunk__22801_22861.cljs$core$IIndexed$_nth$arity$2(null,i__22803_22863);
alert_22864__$1.remove();


var G__22865 = seq__22800_22860;
var G__22866 = chunk__22801_22861;
var G__22867 = count__22802_22862;
var G__22868 = (i__22803_22863 + (1));
seq__22800_22860 = G__22865;
chunk__22801_22861 = G__22866;
count__22802_22862 = G__22867;
i__22803_22863 = G__22868;
continue;
} else {
var temp__5823__auto___22869 = cljs.core.seq(seq__22800_22860);
if(temp__5823__auto___22869){
var seq__22800_22870__$1 = temp__5823__auto___22869;
if(cljs.core.chunked_seq_QMARK_(seq__22800_22870__$1)){
var c__5673__auto___22871 = cljs.core.chunk_first(seq__22800_22870__$1);
var G__22872 = cljs.core.chunk_rest(seq__22800_22870__$1);
var G__22873 = c__5673__auto___22871;
var G__22874 = cljs.core.count(c__5673__auto___22871);
var G__22875 = (0);
seq__22800_22860 = G__22872;
chunk__22801_22861 = G__22873;
count__22802_22862 = G__22874;
i__22803_22863 = G__22875;
continue;
} else {
var alert_22876__$1 = cljs.core.first(seq__22800_22870__$1);
alert_22876__$1.remove();


var G__22877 = cljs.core.next(seq__22800_22870__$1);
var G__22878 = null;
var G__22879 = (0);
var G__22880 = (0);
seq__22800_22860 = G__22877;
chunk__22801_22861 = G__22878;
count__22802_22862 = G__22879;
i__22803_22863 = G__22880;
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
