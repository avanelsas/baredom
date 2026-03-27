goog.provide('app.components.x_timeline.x_timeline');
goog.scope(function(){
  app.components.x_timeline.x_timeline.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_timeline.x_timeline.k_refs = "__xTimelineRefs";
app.components.x_timeline.x_timeline.k_model = "__xTimelineModel";
app.components.x_timeline.x_timeline.k_handlers = "__xTimelineHandlers";
app.components.x_timeline.x_timeline.style_text = (""+":host{"+"display:flex;"+"flex-direction:column;"+"color-scheme:light dark;"+"--x-timeline-gap:0;"+"--x-timeline-label-color:inherit;"+"--x-timeline-label-font-size:0.875rem;"+"--x-timeline-label-font-weight:600;"+"--x-timeline-label-padding:0 0 0.5rem;}"+"slot{display:contents;}"+"[part=label]{"+"color:var(--x-timeline-label-color);"+"font-size:var(--x-timeline-label-font-size);"+"font-weight:var(--x-timeline-label-font-weight);"+"padding:var(--x-timeline-label-padding);"+"box-sizing:border-box;}"+"[part=label][hidden]{"+"display:none !important;}");
app.components.x_timeline.x_timeline.init_dom_BANG_ = (function app$components$x_timeline$x_timeline$init_dom_BANG_(el){
var root_23394 = el.attachShadow(({"mode": "open"}));
var style_23395 = document.createElement("style");
var label_div_23396 = document.createElement("div");
var slot_el_23397 = document.createElement("slot");
(style_23395.textContent = app.components.x_timeline.x_timeline.style_text);

label_div_23396.setAttribute("part","label");

label_div_23396.setAttribute("hidden","");

root_23394.appendChild(style_23395);

root_23394.appendChild(label_div_23396);

root_23394.appendChild(slot_el_23397);

app.components.x_timeline.x_timeline.goog$module$goog$object.set(el,app.components.x_timeline.x_timeline.k_refs,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"root","root",-448657453),root_23394,new cljs.core.Keyword(null,"label-div","label-div",1505730697),label_div_23396,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_23397], null));

return null;
});
app.components.x_timeline.x_timeline.ensure_refs_BANG_ = (function app$components$x_timeline$x_timeline$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_timeline.x_timeline.goog$module$goog$object.get(el,app.components.x_timeline.x_timeline.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_timeline.x_timeline.init_dom_BANG_(el);

return app.components.x_timeline.x_timeline.goog$module$goog$object.get(el,app.components.x_timeline.x_timeline.k_refs);
}
});
app.components.x_timeline.x_timeline.read_model = (function app$components$x_timeline$x_timeline$read_model(el){
return app.components.x_timeline.model.normalize(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_timeline.model.attr_label),new cljs.core.Keyword(null,"position-raw","position-raw",248130625),el.getAttribute(app.components.x_timeline.model.attr_position),new cljs.core.Keyword(null,"striped?","striped?",-797214979),el.hasAttribute(app.components.x_timeline.model.attr_striped)], null));
});
app.components.x_timeline.x_timeline.apply_model_BANG_ = (function app$components$x_timeline$x_timeline$apply_model_BANG_(el,p__23373){
var map__23374 = p__23373;
var map__23374__$1 = cljs.core.__destructure_map(map__23374);
var m = map__23374__$1;
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23374__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var map__23375_23410 = app.components.x_timeline.x_timeline.ensure_refs_BANG_(el);
var map__23375_23411__$1 = cljs.core.__destructure_map(map__23375_23410);
var label_div_23412 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23375_23411__$1,new cljs.core.Keyword(null,"label-div","label-div",1505730697));
var label_div_23413__$1 = label_div_23412;
el.setAttribute("role","list");

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,"")){
el.setAttribute("aria-label",label);
} else {
el.removeAttribute("aria-label");
}

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,"")){
(label_div_23413__$1.textContent = label);

label_div_23413__$1.removeAttribute("hidden");
} else {
(label_div_23413__$1.textContent = "");

label_div_23413__$1.setAttribute("hidden","");
}

app.components.x_timeline.x_timeline.goog$module$goog$object.set(el,app.components.x_timeline.x_timeline.k_model,m);

return null;
});
app.components.x_timeline.x_timeline.update_from_attrs_BANG_ = (function app$components$x_timeline$x_timeline$update_from_attrs_BANG_(el){
var new_m_23414 = app.components.x_timeline.x_timeline.read_model(el);
var old_m_23415 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(el,app.components.x_timeline.x_timeline.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23415,new_m_23414)){
app.components.x_timeline.x_timeline.apply_model_BANG_(el,new_m_23414);
} else {
}

return null;
});
app.components.x_timeline.x_timeline.update_items_BANG_ = (function app$components$x_timeline$x_timeline$update_items_BANG_(el){
var map__23376_23418 = (function (){var or__5142__auto__ = app.components.x_timeline.x_timeline.goog$module$goog$object.get(el,app.components.x_timeline.x_timeline.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline.x_timeline.read_model(el);
}
})();
var map__23376_23419__$1 = cljs.core.__destructure_map(map__23376_23418);
var position_23420 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23376_23419__$1,new cljs.core.Keyword(null,"position","position",-2011731912));
var striped_QMARK__23421 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23376_23419__$1,new cljs.core.Keyword(null,"striped?","striped?",-797214979));
var items_23422 = el.querySelectorAll(app.components.x_timeline.model.child_tag);
var len_23423 = items_23422.length;
var i_23424 = (0);
while(true){
if((i_23424 < len_23423)){
var item_23425 = (items_23422[i_23424]);
var last_QMARK__23426 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(i_23424,(len_23423 - (1)));
item_23425.setAttribute("data-index",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(i_23424)));

if(last_QMARK__23426){
item_23425.setAttribute("data-last","");
} else {
item_23425.removeAttribute("data-last");
}

item_23425.setAttribute("data-position",app.components.x_timeline.model.item_position(position_23420,i_23424));

if(cljs.core.truth_(striped_QMARK__23421)){
item_23425.setAttribute("data-striped","");
} else {
item_23425.removeAttribute("data-striped");
}

var G__23429 = (i_23424 + (1));
i_23424 = G__23429;
continue;
} else {
}
break;
}

return null;
});
app.components.x_timeline.x_timeline.dispatch_select_BANG_ = (function app$components$x_timeline$x_timeline$dispatch_select_BANG_(el,index,status,label){
var detail_23430 = cljs.core.clj__GT_js(app.components.x_timeline.model.select_detail(index,status,label));
el.dispatchEvent((new CustomEvent(app.components.x_timeline.model.event_select,({"detail": detail_23430, "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_timeline.x_timeline.on_item_connected = (function app$components$x_timeline$x_timeline$on_item_connected(el,_e){
app.components.x_timeline.x_timeline.update_items_BANG_(el);

return null;
});
app.components.x_timeline.x_timeline.on_item_disconnected = (function app$components$x_timeline$x_timeline$on_item_disconnected(el,_e){
if(cljs.core.truth_(el.isConnected)){
app.components.x_timeline.x_timeline.update_items_BANG_(el);
} else {
}

return null;
});
app.components.x_timeline.x_timeline.on_item_click = (function app$components$x_timeline$x_timeline$on_item_click(el,e){
e.stopPropagation();

var target_23431 = e.target;
var index_str_23432 = target_23431.getAttribute("data-index");
var index_23433 = ((typeof index_str_23432 === 'string')?(function (){var n = parseInt(index_str_23432,(10));
if(cljs.core.truth_(isNaN(n))){
return null;
} else {
return n;
}
})():null);
var detail_23434 = e.detail;
var status_23435 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(detail_23434,"status");
var label_23436 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(detail_23434,"label");
app.components.x_timeline.x_timeline.dispatch_select_BANG_(el,index_23433,status_23435,label_23436);

return null;
});
app.components.x_timeline.x_timeline.add_listeners_BANG_ = (function app$components$x_timeline$x_timeline$add_listeners_BANG_(el){
var conn_h_23439 = (function (e){
return app.components.x_timeline.x_timeline.on_item_connected(el,e);
});
var click_h_23440 = (function (e){
return app.components.x_timeline.x_timeline.on_item_click(el,e);
});
var doc_h_23441 = (function (e){
return app.components.x_timeline.x_timeline.on_item_disconnected(el,e);
});
el.addEventListener(app.components.x_timeline.model.child_event_connected,conn_h_23439);

el.addEventListener(app.components.x_timeline.model.child_event_click,click_h_23440);

document.addEventListener(app.components.x_timeline.model.child_event_disconnected,doc_h_23441);

app.components.x_timeline.x_timeline.goog$module$goog$object.set(el,app.components.x_timeline.x_timeline.k_handlers,({"item-connected": conn_h_23439, "item-click": click_h_23440, "item-disconnected-doc": doc_h_23441}));

return null;
});
app.components.x_timeline.x_timeline.remove_listeners_BANG_ = (function app$components$x_timeline$x_timeline$remove_listeners_BANG_(el){
var hs_23442 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(el,app.components.x_timeline.x_timeline.k_handlers);
if(cljs.core.truth_(hs_23442)){
var conn_h_23443 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(hs_23442,"item-connected");
var click_h_23444 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(hs_23442,"item-click");
var doc_h_23445 = app.components.x_timeline.x_timeline.goog$module$goog$object.get(hs_23442,"item-disconnected-doc");
if(cljs.core.truth_(conn_h_23443)){
el.removeEventListener(app.components.x_timeline.model.child_event_connected,conn_h_23443);
} else {
}

if(cljs.core.truth_(click_h_23444)){
el.removeEventListener(app.components.x_timeline.model.child_event_click,click_h_23444);
} else {
}

if(cljs.core.truth_(doc_h_23445)){
document.removeEventListener(app.components.x_timeline.model.child_event_disconnected,doc_h_23445);
} else {
}
} else {
}

app.components.x_timeline.x_timeline.goog$module$goog$object.set(el,app.components.x_timeline.x_timeline.k_handlers,null);

return null;
});
app.components.x_timeline.x_timeline.install_property_accessors_BANG_ = (function app$components$x_timeline$x_timeline$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_timeline.model.attr_label,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline.model.attr_label);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
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
return this$.setAttribute(app.components.x_timeline.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline.model.attr_label);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline.model.attr_position,({"get": (function (){
var this$ = this;
return app.components.x_timeline.model.parse_position(this$.getAttribute(app.components.x_timeline.model.attr_position));
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline.model.attr_position,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline.model.attr_position);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,app.components.x_timeline.model.attr_striped,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_timeline.model.attr_striped);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline.model.attr_striped,"");
} else {
return this$.removeAttribute(app.components.x_timeline.model.attr_striped);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_timeline.x_timeline.element_class = (function app$components$x_timeline$x_timeline$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_timeline.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_timeline.x_timeline.ensure_refs_BANG_(this$);

app.components.x_timeline.x_timeline.update_from_attrs_BANG_(this$);

app.components.x_timeline.x_timeline.remove_listeners_BANG_(this$);

app.components.x_timeline.x_timeline.add_listeners_BANG_(this$);

app.components.x_timeline.x_timeline.update_items_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_timeline.x_timeline.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_timeline.x_timeline.update_from_attrs_BANG_(this$);

app.components.x_timeline.x_timeline.update_items_BANG_(this$);
} else {
}

return null;
}));

app.components.x_timeline.x_timeline.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_timeline.x_timeline.register_BANG_ = (function app$components$x_timeline$x_timeline$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_timeline.model.tag_name))){
} else {
customElements.define(app.components.x_timeline.model.tag_name,app.components.x_timeline.x_timeline.element_class());
}

return null;
});
app.components.x_timeline.x_timeline.init_BANG_ = (function app$components$x_timeline$x_timeline$init_BANG_(){
return app.components.x_timeline.x_timeline.register_BANG_();
});

//# sourceMappingURL=app.components.x_timeline.x_timeline.js.map
