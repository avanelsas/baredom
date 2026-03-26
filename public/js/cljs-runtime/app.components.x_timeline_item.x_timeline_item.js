goog.provide('app.components.x_timeline_item.x_timeline_item');
goog.scope(function(){
  app.components.x_timeline_item.x_timeline_item.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_timeline_item.x_timeline_item.k_refs = "__xTimelineItemRefs";
app.components.x_timeline_item.x_timeline_item.k_model = "__xTimelineItemModel";
app.components.x_timeline_item.x_timeline_item.k_handlers = "__xTimelineItemHandlers";
app.components.x_timeline_item.x_timeline_item.k_entered = "__xTimelineItemEntered";
app.components.x_timeline_item.x_timeline_item.k_parent_pos = "__xTimelineItemParentPos";
app.components.x_timeline_item.x_timeline_item.k_self_pos = "__xTimelineItemSelfPos";
app.components.x_timeline_item.x_timeline_item.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-timeline-item-marker-size:2rem;"+"--x-timeline-item-marker-bg:rgba(0,0,0,0.06);"+"--x-timeline-item-marker-color:rgba(0,0,0,0.45);"+"--x-timeline-item-connector-color:rgba(0,0,0,0.12);"+"--x-timeline-item-connector-width:2px;"+"--x-timeline-item-gap:0.75rem;"+"--x-timeline-item-label-width:6rem;"+"--x-timeline-item-label-color:rgba(0,0,0,0.5);"+"--x-timeline-item-label-font-size:0.8125rem;"+"--x-timeline-item-title-font-size:0.9375rem;"+"--x-timeline-item-stripe-bg:rgba(0,0,0,0.025);"+"--x-timeline-item-motion:150ms;"+"--x-timeline-item-motion-ease:cubic-bezier(0.2,0,0,1);"+"--x-timeline-item-enter-duration:160ms;"+"--x-timeline-item-disabled-opacity:0.45;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-timeline-item-marker-bg:rgba(255,255,255,0.10);"+"--x-timeline-item-marker-color:rgba(255,255,255,0.55);"+"--x-timeline-item-connector-color:rgba(255,255,255,0.15);"+"--x-timeline-item-label-color:rgba(255,255,255,0.5);"+"--x-timeline-item-stripe-bg:rgba(255,255,255,0.03);}}"+":host([data-status='active']){"+"--x-timeline-item-marker-bg:rgba(0,102,204,1);"+"--x-timeline-item-marker-color:#fff;"+"--x-timeline-item-connector-color:rgba(0,102,204,0.4);}"+":host([data-status='complete']){"+"--x-timeline-item-marker-bg:rgba(16,140,72,1);"+"--x-timeline-item-marker-color:#fff;"+"--x-timeline-item-connector-color:rgba(16,140,72,0.5);}"+":host([data-status='error']){"+"--x-timeline-item-marker-bg:rgba(190,20,40,1);"+"--x-timeline-item-marker-color:#fff;"+"--x-timeline-item-connector-color:rgba(190,20,40,0.4);}"+":host([data-status='warning']){"+"--x-timeline-item-marker-bg:rgba(204,120,0,1);"+"--x-timeline-item-marker-color:#fff;"+"--x-timeline-item-connector-color:rgba(204,120,0,0.4);}"+"@media (prefers-color-scheme:dark){"+":host([data-status='active']){"+"--x-timeline-item-marker-bg:rgba(80,160,255,1);"+"--x-timeline-item-connector-color:rgba(80,160,255,0.4);}"+":host([data-status='complete']){"+"--x-timeline-item-marker-bg:rgba(60,210,120,1);"+"--x-timeline-item-connector-color:rgba(60,210,120,0.5);}"+":host([data-status='error']){"+"--x-timeline-item-marker-bg:rgba(255,90,110,1);"+"--x-timeline-item-connector-color:rgba(255,90,110,0.4);}"+":host([data-status='warning']){"+"--x-timeline-item-marker-bg:rgba(255,190,90,1);"+"--x-timeline-item-connector-color:rgba(255,190,90,0.4);}}"+":host([data-striped]){"+"background:var(--x-timeline-item-stripe-bg);"+"border-radius:4px;}"+":host([disabled]){"+"opacity:var(--x-timeline-item-disabled-opacity);"+"pointer-events:none;}"+":host(:not([disabled])){cursor:pointer;}"+":host(:not([disabled])):focus{outline:none;}"+":host(:not([disabled])):focus-visible{"+"outline:2px solid rgba(0,102,204,0.55);"+"outline-offset:2px;}"+"@media (prefers-color-scheme:dark){"+":host(:not([disabled])):focus-visible{"+"outline-color:rgba(80,160,255,0.7);}}"+".timeline-item{"+"display:grid;"+"grid-template-columns:var(--x-timeline-item-label-width) var(--x-timeline-item-marker-size) 1fr;"+"grid-template-areas:'label track content';"+"align-items:start;"+"gap:0 var(--x-timeline-item-gap);}"+":host([data-position='end']) .timeline-item{"+"grid-template-columns:1fr var(--x-timeline-item-marker-size) var(--x-timeline-item-label-width);"+"grid-template-areas:'content track label';}"+".label-col{"+"grid-area:label;"+"display:flex;"+"align-items:center;"+"justify-content:flex-end;"+"min-height:var(--x-timeline-item-marker-size);"+"font-size:var(--x-timeline-item-label-font-size);"+"color:var(--x-timeline-item-label-color);"+"overflow:hidden;"+"min-width:0;}"+":host([data-position='end']) .label-col{"+"justify-content:flex-start;}"+".track-col{"+"grid-area:track;"+"display:flex;"+"flex-direction:column;"+"align-items:center;}"+".marker{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"width:var(--x-timeline-item-marker-size);"+"height:var(--x-timeline-item-marker-size);"+"border-radius:999px;"+"background:var(--x-timeline-item-marker-bg);"+"color:var(--x-timeline-item-marker-color);"+"font-size:0.875em;"+"flex-shrink:0;"+"transition:"+"background var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease),"+"color var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease);}"+".connector{"+"flex:1;"+"width:var(--x-timeline-item-connector-width);"+"background:var(--x-timeline-item-connector-color);"+"min-height:1rem;"+"transition:background var(--x-timeline-item-motion) var(--x-timeline-item-motion-ease);}"+":host([data-connector='dashed']) .connector{"+"background:repeating-linear-gradient("+"to bottom,"+"var(--x-timeline-item-connector-color) 0,"+"var(--x-timeline-item-connector-color) 4px,"+"transparent 4px,"+"transparent 8px);}"+":host([data-connector='none']) .connector,"+":host([data-last]) .connector{"+"display:none;}"+".content-col{"+"grid-area:content;"+"display:flex;"+"flex-direction:column;"+"min-width:0;"+"padding-bottom:var(--x-timeline-item-gap);}"+".title{"+"font-weight:600;"+"font-size:var(--x-timeline-item-title-font-size);"+"line-height:1.25;"+"min-height:var(--x-timeline-item-marker-size);"+"display:flex;"+"align-items:center;"+"overflow-wrap:anywhere;"+"min-width:0;}"+".title[hidden]{display:none;}"+".body{margin-top:0.25rem;}"+".actions{margin-top:0.5rem;}"+":host([data-entering]){"+"animation:x-timeline-item-enter var(--x-timeline-item-enter-duration)"+" var(--x-timeline-item-motion-ease) both;}"+"@keyframes x-timeline-item-enter{"+"from{opacity:0;transform:translateX(-4px);}"+"to{opacity:1;transform:translateX(0);}}"+":host([data-position='end'][data-entering]){"+"animation-name:x-timeline-item-enter-end;}"+"@keyframes x-timeline-item-enter-end{"+"from{opacity:0;transform:translateX(4px);}"+"to{opacity:1;transform:translateX(0);}}"+"@media (prefers-reduced-motion:reduce){"+":host([data-entering]){animation:none !important;}"+".marker,.connector{transition:none !important;}}");
app.components.x_timeline_item.x_timeline_item.init_dom_BANG_ = (function app$components$x_timeline_item$x_timeline_item$init_dom_BANG_(el){
var root_23383 = el.attachShadow(({"mode": "open"}));
var style_23384 = document.createElement("style");
var item_div_23385 = document.createElement("div");
var label_col_23386 = document.createElement("div");
var label_slot_23387 = document.createElement("slot");
var label_text_23388 = document.createElement("span");
var track_col_23389 = document.createElement("div");
var marker_23390 = document.createElement("div");
var icon_slot_23391 = document.createElement("slot");
var default_icon_23392 = document.createElement("span");
var connector_23393 = document.createElement("div");
var content_col_23394 = document.createElement("div");
var title_el_23395 = document.createElement("div");
var body_el_23396 = document.createElement("div");
var default_slot_23397 = document.createElement("slot");
var actions_el_23398 = document.createElement("div");
var actions_slot_23399 = document.createElement("slot");
(style_23384.textContent = app.components.x_timeline_item.x_timeline_item.style_text);

item_div_23385.setAttribute("class","timeline-item");

label_col_23386.setAttribute("class","label-col");

label_slot_23387.setAttribute("name","label");

label_text_23388.setAttribute("class","label-text");

label_slot_23387.appendChild(label_text_23388);

label_col_23386.appendChild(label_slot_23387);

track_col_23389.setAttribute("class","track-col");

marker_23390.setAttribute("class","marker");

icon_slot_23391.setAttribute("name","icon");

default_icon_23392.setAttribute("class","default-icon");

default_icon_23392.setAttribute("aria-hidden","true");

icon_slot_23391.appendChild(default_icon_23392);

marker_23390.appendChild(icon_slot_23391);

connector_23393.setAttribute("class","connector");

track_col_23389.appendChild(marker_23390);

track_col_23389.appendChild(connector_23393);

content_col_23394.setAttribute("class","content-col");

title_el_23395.setAttribute("class","title");

body_el_23396.setAttribute("class","body");

body_el_23396.appendChild(default_slot_23397);

actions_el_23398.setAttribute("class","actions");

actions_slot_23399.setAttribute("name","actions");

actions_el_23398.appendChild(actions_slot_23399);

content_col_23394.appendChild(title_el_23395);

content_col_23394.appendChild(body_el_23396);

content_col_23394.appendChild(actions_el_23398);

item_div_23385.appendChild(label_col_23386);

item_div_23385.appendChild(track_col_23389);

item_div_23385.appendChild(content_col_23394);

root_23383.appendChild(style_23384);

root_23383.appendChild(item_div_23385);

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_refs,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"connector","connector",-1517293248),new cljs.core.Keyword(null,"label-text","label-text",-1566972381),new cljs.core.Keyword(null,"marker","marker",865118313),new cljs.core.Keyword(null,"default-icon","default-icon",491415788),new cljs.core.Keyword(null,"label-col","label-col",1883390160),new cljs.core.Keyword(null,"track-col","track-col",629248272),new cljs.core.Keyword(null,"body-el","body-el",-277974608),new cljs.core.Keyword(null,"default-slot","default-slot",-1145647151),new cljs.core.Keyword(null,"root","root",-448657453),new cljs.core.Keyword(null,"actions-slot","actions-slot",1424248629),new cljs.core.Keyword(null,"title-el","title-el",-401767816),new cljs.core.Keyword(null,"content-col","content-col",767367449),new cljs.core.Keyword(null,"item-div","item-div",1630512700),new cljs.core.Keyword(null,"actions-el","actions-el",1486248509),new cljs.core.Keyword(null,"icon-slot","icon-slot",1874301182),new cljs.core.Keyword(null,"label-slot","label-slot",-1755862561)],[connector_23393,label_text_23388,marker_23390,default_icon_23392,label_col_23386,track_col_23389,body_el_23396,default_slot_23397,root_23383,actions_slot_23399,title_el_23395,content_col_23394,item_div_23385,actions_el_23398,icon_slot_23391,label_slot_23387]));

return null;
});
app.components.x_timeline_item.x_timeline_item.ensure_refs_BANG_ = (function app$components$x_timeline_item$x_timeline_item$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_timeline_item.x_timeline_item.init_dom_BANG_(el);

return app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_refs);
}
});
app.components.x_timeline_item.x_timeline_item.read_model = (function app$components$x_timeline_item$x_timeline_item$read_model(el){
return app.components.x_timeline_item.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"position-raw","position-raw",248130625),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"data-striped?","data-striped?",906911587),new cljs.core.Keyword(null,"connector-raw","connector-raw",106333796),new cljs.core.Keyword(null,"data-position-raw","data-position-raw",256144356),new cljs.core.Keyword(null,"status-raw","status-raw",-2121061147),new cljs.core.Keyword(null,"data-last?","data-last?",-1110326839),new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778),new cljs.core.Keyword(null,"title-raw","title-raw",976469452),new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214),new cljs.core.Keyword(null,"data-index-raw","data-index-raw",491574874),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181)],[el.getAttribute(app.components.x_timeline_item.model.attr_position),el.getAttribute(app.components.x_timeline_item.model.attr_label),el.hasAttribute(app.components.x_timeline_item.model.data_attr_striped),el.getAttribute(app.components.x_timeline_item.model.attr_connector),app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_parent_pos),el.getAttribute(app.components.x_timeline_item.model.attr_status),el.hasAttribute(app.components.x_timeline_item.model.data_attr_last),el.hasAttribute(app.components.x_timeline_item.model.attr_icon),el.getAttribute(app.components.x_timeline_item.model.attr_title),el.getAttribute(app.components.x_timeline_item.model.attr_icon),el.getAttribute(app.components.x_timeline_item.model.data_attr_index),el.hasAttribute(app.components.x_timeline_item.model.attr_disabled)]));
});
app.components.x_timeline_item.x_timeline_item.apply_model_BANG_ = (function app$components$x_timeline_item$x_timeline_item$apply_model_BANG_(el,p__23366){
var map__23367 = p__23366;
var map__23367__$1 = cljs.core.__destructure_map(map__23367);
var m = map__23367__$1;
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var title = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"title","title",636505583));
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var effective_position = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"effective-position","effective-position",840072317));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var marker_icon = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"marker-icon","marker-icon",424963779));
var marker_aria = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23367__$1,new cljs.core.Keyword(null,"marker-aria","marker-aria",-353426855));
var map__23369_23409 = app.components.x_timeline_item.x_timeline_item.ensure_refs_BANG_(el);
var map__23369_23410__$1 = cljs.core.__destructure_map(map__23369_23409);
var label_text_23411 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23369_23410__$1,new cljs.core.Keyword(null,"label-text","label-text",-1566972381));
var marker_23412 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23369_23410__$1,new cljs.core.Keyword(null,"marker","marker",865118313));
var title_el_23413 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23369_23410__$1,new cljs.core.Keyword(null,"title-el","title-el",-401767816));
var default_icon_23414 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23369_23410__$1,new cljs.core.Keyword(null,"default-icon","default-icon",491415788));
var label_text_23415__$1 = label_text_23411;
var marker_23416__$1 = marker_23412;
var title_el_23417__$1 = title_el_23413;
var default_icon_23418__$1 = default_icon_23414;
el.setAttribute("data-status",app.components.x_timeline_item.model.status__GT_attr(status));

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_self_pos,true);

el.setAttribute("data-position",cljs.core.name(effective_position));

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_self_pos,null);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"connector","connector",-1517293248).cljs$core$IFn$_invoke$arity$1(m),new cljs.core.Keyword(null,"dashed","dashed",-1449249319))){
el.setAttribute("data-connector","dashed");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"connector","connector",-1517293248).cljs$core$IFn$_invoke$arity$1(m),new cljs.core.Keyword(null,"none","none",1333468478))){
el.setAttribute("data-connector","none");
} else {
el.removeAttribute("data-connector");
}
}

el.setAttribute("role","listitem");

if(cljs.core.truth_(disabled_QMARK_)){
el.setAttribute("aria-disabled","true");

(el.tabIndex = (-1));
} else {
el.removeAttribute("aria-disabled");

(el.tabIndex = (0));
}

var host_label_23419 = ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(title,""))?title:((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,""))?label:null
));
if(cljs.core.truth_(host_label_23419)){
el.setAttribute("aria-label",host_label_23419);
} else {
el.removeAttribute("aria-label");
}

marker_23416__$1.setAttribute("aria-label",marker_aria);

(label_text_23415__$1.textContent = label);

if((!((marker_icon == null)))){
(default_icon_23418__$1.textContent = marker_icon);
} else {
(default_icon_23418__$1.textContent = "");
}

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(title,"")){
title_el_23417__$1.removeAttribute("hidden");

(title_el_23417__$1.textContent = title);
} else {
title_el_23417__$1.setAttribute("hidden","");

(title_el_23417__$1.textContent = "");
}

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_model,m);

return null;
});
app.components.x_timeline_item.x_timeline_item.update_from_attrs_BANG_ = (function app$components$x_timeline_item$x_timeline_item$update_from_attrs_BANG_(el){
var new_m_23424 = app.components.x_timeline_item.x_timeline_item.read_model(el);
var old_m_23425 = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23425,new_m_23424)){
app.components.x_timeline_item.x_timeline_item.apply_model_BANG_(el,new_m_23424);
} else {
}

return null;
});
app.components.x_timeline_item.x_timeline_item.start_enter_BANG_ = (function app$components$x_timeline_item$x_timeline_item$start_enter_BANG_(el){
if(cljs.core.truth_(app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_entered))){
} else {
app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_entered,true);

el.setAttribute("data-entering","");

var on_end = (function app$components$x_timeline_item$x_timeline_item$start_enter_BANG__$_on_end(e){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(e.target,el)){
el.removeEventListener("animationend",app$components$x_timeline_item$x_timeline_item$start_enter_BANG__$_on_end);

return el.removeAttribute("data-entering");
} else {
return null;
}
});
el.addEventListener("animationend",on_end);
}

return null;
});
app.components.x_timeline_item.x_timeline_item.dispatch_click_BANG_ = (function app$components$x_timeline_item$x_timeline_item$dispatch_click_BANG_(el){
var m_23432 = (function (){var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.x_timeline_item.read_model(el);
}
})();
el.dispatchEvent((new CustomEvent(app.components.x_timeline_item.model.event_click,({"detail": cljs.core.clj__GT_js(app.components.x_timeline_item.model.click_detail(m_23432)), "bubbles": true, "composed": true, "cancelable": true}))));

return null;
});
app.components.x_timeline_item.x_timeline_item.dispatch_connected_BANG_ = (function app$components$x_timeline_item$x_timeline_item$dispatch_connected_BANG_(el,m){
el.dispatchEvent((new CustomEvent(app.components.x_timeline_item.model.event_connected,({"detail": cljs.core.clj__GT_js(app.components.x_timeline_item.model.connected_detail(m)), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_timeline_item.x_timeline_item.dispatch_disconnected_BANG_ = (function app$components$x_timeline_item$x_timeline_item$dispatch_disconnected_BANG_(_el,m){
document.dispatchEvent((new CustomEvent(app.components.x_timeline_item.model.event_disconnected,({"detail": cljs.core.clj__GT_js(app.components.x_timeline_item.model.disconnected_detail(m)), "bubbles": false, "composed": false, "cancelable": false}))));

return null;
});
app.components.x_timeline_item.x_timeline_item.on_click = (function app$components$x_timeline_item$x_timeline_item$on_click(el,_e){
var m_23435 = (function (){var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.x_timeline_item.read_model(el);
}
})();
if(app.components.x_timeline_item.model.interactive_eligible_QMARK_(m_23435)){
app.components.x_timeline_item.x_timeline_item.dispatch_click_BANG_(el);
} else {
}

return null;
});
app.components.x_timeline_item.x_timeline_item.on_keydown = (function app$components$x_timeline_item$x_timeline_item$on_keydown(el,e){
var key_23443 = e.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_23443,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key_23443," ")))){
var m_23445 = (function (){var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.x_timeline_item.read_model(el);
}
})();
if(app.components.x_timeline_item.model.interactive_eligible_QMARK_(m_23445)){
e.preventDefault();

app.components.x_timeline_item.x_timeline_item.dispatch_click_BANG_(el);
} else {
}
} else {
}

return null;
});
app.components.x_timeline_item.x_timeline_item.add_listeners_BANG_ = (function app$components$x_timeline_item$x_timeline_item$add_listeners_BANG_(el){
var click_h_23449 = (function (e){
return app.components.x_timeline_item.x_timeline_item.on_click(el,e);
});
var keydown_h_23450 = (function (e){
return app.components.x_timeline_item.x_timeline_item.on_keydown(el,e);
});
el.addEventListener("click",click_h_23449);

el.addEventListener("keydown",keydown_h_23450);

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_handlers,({"click": click_h_23449, "keydown": keydown_h_23450}));

return null;
});
app.components.x_timeline_item.x_timeline_item.remove_listeners_BANG_ = (function app$components$x_timeline_item$x_timeline_item$remove_listeners_BANG_(el){
var hs_23453 = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(el,app.components.x_timeline_item.x_timeline_item.k_handlers);
if(cljs.core.truth_(hs_23453)){
var click_h_23460 = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(hs_23453,"click");
var keydown_h_23461 = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(hs_23453,"keydown");
if(cljs.core.truth_(click_h_23460)){
el.removeEventListener("click",click_h_23460);
} else {
}

if(cljs.core.truth_(keydown_h_23461)){
el.removeEventListener("keydown",keydown_h_23461);
} else {
}
} else {
}

app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(el,app.components.x_timeline_item.x_timeline_item.k_handlers,null);

return null;
});
app.components.x_timeline_item.x_timeline_item.install_property_accessors_BANG_ = (function app$components$x_timeline_item$x_timeline_item$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_timeline_item.model.attr_label,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline_item.model.attr_label);
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
return this$.setAttribute(app.components.x_timeline_item.model.attr_label,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_label);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline_item.model.attr_title,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline_item.model.attr_title);
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
return this$.setAttribute(app.components.x_timeline_item.model.attr_title,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_title);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline_item.model.attr_status,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline_item.model.attr_status);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "pending";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline_item.model.attr_status,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_status);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline_item.model.attr_icon,({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_timeline_item.model.attr_icon);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline_item.model.attr_icon,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_icon);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline_item.model.attr_connector,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline_item.model.attr_connector);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "solid";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline_item.model.attr_connector,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_connector);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_timeline_item.model.attr_position,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_timeline_item.model.attr_position);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "start";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline_item.model.attr_position,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_position);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,app.components.x_timeline_item.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_timeline_item.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_timeline_item.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_timeline_item.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_timeline_item.x_timeline_item.element_class = (function app$components$x_timeline_item$x_timeline_item$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_timeline_item.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_timeline_item.x_timeline_item.ensure_refs_BANG_(this$);

app.components.x_timeline_item.x_timeline_item.remove_listeners_BANG_(this$);

app.components.x_timeline_item.x_timeline_item.add_listeners_BANG_(this$);

var dp_23484 = this$.getAttribute(app.components.x_timeline_item.model.data_attr_position);
if(cljs.core.truth_(dp_23484)){
app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(this$,app.components.x_timeline_item.x_timeline_item.k_parent_pos,dp_23484);
} else {
}

app.components.x_timeline_item.x_timeline_item.update_from_attrs_BANG_(this$);

app.components.x_timeline_item.x_timeline_item.start_enter_BANG_(this$);

var m_23485 = (function (){var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(this$,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.x_timeline_item.read_model(this$);
}
})();
app.components.x_timeline_item.x_timeline_item.dispatch_connected_BANG_(this$,m_23485);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
var m_23486 = (function (){var or__5142__auto__ = app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(this$,app.components.x_timeline_item.x_timeline_item.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.x_timeline_item.read_model(this$);
}
})();
app.components.x_timeline_item.x_timeline_item.remove_listeners_BANG_(this$);

app.components.x_timeline_item.x_timeline_item.dispatch_disconnected_BANG_(this$,m_23486);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (attr_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(attr_name,app.components.x_timeline_item.model.data_attr_position);
if(and__5140__auto__){
return app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.get(this$,app.components.x_timeline_item.x_timeline_item.k_self_pos);
} else {
return and__5140__auto__;
}
})())){
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(attr_name,app.components.x_timeline_item.model.data_attr_position)){
app.components.x_timeline_item.x_timeline_item.goog$module$goog$object.set(this$,app.components.x_timeline_item.x_timeline_item.k_parent_pos,new_val);
} else {
}

app.components.x_timeline_item.x_timeline_item.update_from_attrs_BANG_(this$);
}
} else {
}

return null;
}));

app.components.x_timeline_item.x_timeline_item.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_timeline_item.x_timeline_item.register_BANG_ = (function app$components$x_timeline_item$x_timeline_item$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_timeline_item.model.tag_name))){
} else {
customElements.define(app.components.x_timeline_item.model.tag_name,app.components.x_timeline_item.x_timeline_item.element_class());
}

return null;
});
app.components.x_timeline_item.x_timeline_item.init_BANG_ = (function app$components$x_timeline_item$x_timeline_item$init_BANG_(){
return app.components.x_timeline_item.x_timeline_item.register_BANG_();
});

//# sourceMappingURL=app.components.x_timeline_item.x_timeline_item.js.map
