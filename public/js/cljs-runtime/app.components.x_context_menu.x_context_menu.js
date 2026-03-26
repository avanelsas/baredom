goog.provide('app.components.x_context_menu.x_context_menu');
goog.scope(function(){
  app.components.x_context_menu.x_context_menu.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_context_menu.x_context_menu.k_refs = "__xContextMenuRefs";
app.components.x_context_menu.x_context_menu.k_handlers = "__xContextMenuHandlers";
app.components.x_context_menu.x_context_menu.k_layer = "__xContextMenuLayer";
app.components.x_context_menu.x_context_menu.make_el = (function app$components$x_context_menu$x_context_menu$make_el(tag){
return document.createElement(tag);
});
app.components.x_context_menu.x_context_menu.host_style_text = ":host{display:contents;}slot{display:none;}";
app.components.x_context_menu.x_context_menu.panel_style_text = (""+":host{color-scheme:light dark;"+"--x-context-menu-bg:rgba(255,255,255,0.97);"+"--x-context-menu-border:rgba(0,0,0,0.1);"+"--x-context-menu-shadow:0 8px 32px rgba(0,0,0,0.14),0 2px 8px rgba(0,0,0,0.08);"+"--x-context-menu-radius:10px;"+"--x-context-menu-item-hover:rgba(0,0,0,0.05);"+"--x-context-menu-item-active:rgba(0,0,0,0.09);"+"--x-context-menu-item-fg:rgba(0,0,0,0.87);"+"--x-context-menu-separator:rgba(0,0,0,0.1);"+"}"+"@media(prefers-color-scheme:dark){"+":host{"+"--x-context-menu-bg:rgba(30,30,35,0.97);"+"--x-context-menu-border:rgba(255,255,255,0.1);"+"--x-context-menu-shadow:0 8px 32px rgba(0,0,0,0.55),0 2px 8px rgba(0,0,0,0.35);"+"--x-context-menu-item-hover:rgba(255,255,255,0.07);"+"--x-context-menu-item-active:rgba(255,255,255,0.12);"+"--x-context-menu-item-fg:rgba(255,255,255,0.87);"+"--x-context-menu-separator:rgba(255,255,255,0.1);"+"}"+"}"+"[part=panel]{"+"position:absolute;"+"min-width:160px;"+"border-radius:var(--x-context-menu-radius);"+"background:var(--x-context-menu-bg);"+"border:1px solid var(--x-context-menu-border);"+"box-shadow:var(--x-context-menu-shadow);"+"padding:4px 0;"+"overflow:auto;"+"pointer-events:auto;"+"outline:none;"+"@media(prefers-reduced-motion:no-preference){"+"transition:opacity 120ms ease,transform 120ms ease;"+"}"+"}");
app.components.x_context_menu.x_context_menu.has_attr_QMARK_ = (function app$components$x_context_menu$x_context_menu$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_context_menu.x_context_menu.get_attr = (function app$components$x_context_menu$x_context_menu$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_context_menu.x_context_menu.read_model = (function app$components$x_context_menu$x_context_menu$read_model(el){
return app.components.x_context_menu.model.normalize(new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"open-present?","open-present?",965047899),(cljs.core.truth_(app.components.x_context_menu.x_context_menu.has_attr_QMARK_(el,app.components.x_context_menu.model.attr_open))?app.components.x_context_menu.x_context_menu.get_attr(el,app.components.x_context_menu.model.attr_open):null),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),(cljs.core.truth_(app.components.x_context_menu.x_context_menu.has_attr_QMARK_(el,app.components.x_context_menu.model.attr_disabled))?app.components.x_context_menu.x_context_menu.get_attr(el,app.components.x_context_menu.model.attr_disabled):null),new cljs.core.Keyword(null,"placement-raw","placement-raw",-1500957198),app.components.x_context_menu.x_context_menu.get_attr(el,app.components.x_context_menu.model.attr_placement),new cljs.core.Keyword(null,"offset-raw","offset-raw",-1418404095),app.components.x_context_menu.x_context_menu.get_attr(el,app.components.x_context_menu.model.attr_offset),new cljs.core.Keyword(null,"z-index-raw","z-index-raw",-1179679257),app.components.x_context_menu.x_context_menu.get_attr(el,app.components.x_context_menu.model.attr_z_index)], null));
});
app.components.x_context_menu.x_context_menu.dispatch_BANG_ = (function app$components$x_context_menu$x_context_menu$dispatch_BANG_(el,event_name,cancelable_QMARK_,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cancelable_QMARK_})));
el.dispatchEvent(ev);

return cljs.core.not(ev.defaultPrevented);
});
app.components.x_context_menu.x_context_menu.overlay_root_id = "__xOverlayRoot";
app.components.x_context_menu.x_context_menu.ensure_overlay_root_BANG_ = (function app$components$x_context_menu$x_context_menu$ensure_overlay_root_BANG_(){
var or__5142__auto__ = document.getElementById(app.components.x_context_menu.x_context_menu.overlay_root_id);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var div = app.components.x_context_menu.x_context_menu.make_el("div");
div.setAttribute("id",app.components.x_context_menu.x_context_menu.overlay_root_id);

(div.style.position = "fixed");

(div.style.inset = "0");

(div.style.pointerEvents = "none");

(div.style.zIndex = "0");

document.body.appendChild(div);

return div;
}
});
app.components.x_context_menu.x_context_menu.menu_items = (function app$components$x_context_menu$x_context_menu$menu_items(panel){
return cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(panel.querySelectorAll("[role=menuitem]:not([disabled]):not([aria-disabled=true])"));
});
app.components.x_context_menu.x_context_menu.focus_item_BANG_ = (function app$components$x_context_menu$x_context_menu$focus_item_BANG_(item){
if(cljs.core.truth_(item)){
return item.focus();
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.focus_next_BANG_ = (function app$components$x_context_menu$x_context_menu$focus_next_BANG_(panel,current){
var items = app.components.x_context_menu.x_context_menu.menu_items(panel);
var idx = cljs.core.to_array(items).indexOf(current);
var next = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(items,cljs.core.mod((idx + (1)),cljs.core.count(items)),null);
return app.components.x_context_menu.x_context_menu.focus_item_BANG_(next);
});
app.components.x_context_menu.x_context_menu.focus_prev_BANG_ = (function app$components$x_context_menu$x_context_menu$focus_prev_BANG_(panel,current){
var items = app.components.x_context_menu.x_context_menu.menu_items(panel);
var n = cljs.core.count(items);
var idx = cljs.core.to_array(items).indexOf(current);
var prev = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(items,cljs.core.mod((idx + (n - (1))),n),null);
return app.components.x_context_menu.x_context_menu.focus_item_BANG_(prev);
});
app.components.x_context_menu.x_context_menu.focus_first_BANG_ = (function app$components$x_context_menu$x_context_menu$focus_first_BANG_(panel){
return app.components.x_context_menu.x_context_menu.focus_item_BANG_(cljs.core.first(app.components.x_context_menu.x_context_menu.menu_items(panel)));
});
app.components.x_context_menu.x_context_menu.make_layer_BANG_ = (function app$components$x_context_menu$x_context_menu$make_layer_BANG_(el,z_index){
var overlay = app.components.x_context_menu.x_context_menu.ensure_overlay_root_BANG_();
var layer = app.components.x_context_menu.x_context_menu.make_el("div");
var shadow__$1 = layer.attachShadow(({"mode": "open"}));
var style = app.components.x_context_menu.x_context_menu.make_el("style");
var panel = app.components.x_context_menu.x_context_menu.make_el("div");
(layer.style.position = "fixed");

(layer.style.inset = "0");

(layer.style.pointerEvents = "none");

(layer.style.zIndex = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(z_index)));

(style.textContent = app.components.x_context_menu.x_context_menu.panel_style_text);

panel.setAttribute("part","panel");

panel.setAttribute("role","menu");

panel.setAttribute("tabindex","-1");

shadow__$1.appendChild(style);

shadow__$1.appendChild(panel);

overlay.appendChild(layer);

return layer;
});
app.components.x_context_menu.x_context_menu.get_panel = (function app$components$x_context_menu$x_context_menu$get_panel(layer){
if(cljs.core.truth_(layer)){
return layer.shadowRoot.querySelector("[part=panel]");
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.position_layer_BANG_ = (function app$components$x_context_menu$x_context_menu$position_layer_BANG_(layer,x,y,p__22315){
var map__22316 = p__22315;
var map__22316__$1 = cljs.core.__destructure_map(map__22316);
var z_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22316__$1,new cljs.core.Keyword(null,"z-index","z-index",1892827090));
var max_height = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22316__$1,new cljs.core.Keyword(null,"max-height","max-height",-612563804));
(layer.style.zIndex = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(z_index)));

var panel = app.components.x_context_menu.x_context_menu.get_panel(layer);
if(cljs.core.truth_(panel)){
(panel.style.left = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x)+"px"));

(panel.style.top = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y)+"px"));

return (panel.style.maxHeight = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_height)+"px"));
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.clone_children_to_panel_BANG_ = (function app$components$x_context_menu$x_context_menu$clone_children_to_panel_BANG_(el,panel){
var seq__22317 = cljs.core.seq(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(el.children));
var chunk__22318 = null;
var count__22319 = (0);
var i__22320 = (0);
while(true){
if((i__22320 < count__22319)){
var child = chunk__22318.cljs$core$IIndexed$_nth$arity$2(null,i__22320);
panel.appendChild(child.cloneNode(true));


var G__22447 = seq__22317;
var G__22448 = chunk__22318;
var G__22449 = count__22319;
var G__22450 = (i__22320 + (1));
seq__22317 = G__22447;
chunk__22318 = G__22448;
count__22319 = G__22449;
i__22320 = G__22450;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__22317);
if(temp__5823__auto__){
var seq__22317__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__22317__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__22317__$1);
var G__22451 = cljs.core.chunk_rest(seq__22317__$1);
var G__22452 = c__5673__auto__;
var G__22453 = cljs.core.count(c__5673__auto__);
var G__22454 = (0);
seq__22317 = G__22451;
chunk__22318 = G__22452;
count__22319 = G__22453;
i__22320 = G__22454;
continue;
} else {
var child = cljs.core.first(seq__22317__$1);
panel.appendChild(child.cloneNode(true));


var G__22455 = cljs.core.next(seq__22317__$1);
var G__22456 = null;
var G__22457 = (0);
var G__22458 = (0);
seq__22317 = G__22455;
chunk__22318 = G__22456;
count__22319 = G__22457;
i__22320 = G__22458;
continue;
}
} else {
return null;
}
}
break;
}
});
app.components.x_context_menu.x_context_menu.add_layer_listeners_BANG_ = (function app$components$x_context_menu$x_context_menu$add_layer_listeners_BANG_(el,layer){
var panel = app.components.x_context_menu.x_context_menu.get_panel(layer);
var on_key = (function (ev){
var key = ev.key;
var focused = layer.shadowRoot.activeElement;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Escape")){
ev.preventDefault();

ev.stopPropagation();

return (app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2 ? app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2(el,"keyboard") : app.components.x_context_menu.x_context_menu.close_BANG_.call(null,el,"keyboard"));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowDown")){
ev.preventDefault();

if(cljs.core.truth_(focused)){
return app.components.x_context_menu.x_context_menu.focus_next_BANG_(panel,focused);
} else {
return app.components.x_context_menu.x_context_menu.focus_first_BANG_(panel);
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowUp")){
ev.preventDefault();

if(cljs.core.truth_(focused)){
return app.components.x_context_menu.x_context_menu.focus_prev_BANG_(panel,focused);
} else {
return app.components.x_context_menu.x_context_menu.focus_first_BANG_(panel);
}
} else {
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")))){
if(cljs.core.truth_(focused)){
ev.preventDefault();

return focused.click();
} else {
return null;
}
} else {
return null;
}
}
}
}
});
var on_click_backdrop = (function (ev){
var panel__$1 = app.components.x_context_menu.x_context_menu.get_panel(layer);
if(cljs.core.truth_((function (){var and__5140__auto__ = panel__$1;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(panel__$1.contains(ev.target));
} else {
return and__5140__auto__;
}
})())){
return (app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2 ? app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2(el,"backdrop") : app.components.x_context_menu.x_context_menu.close_BANG_.call(null,el,"backdrop"));
} else {
return null;
}
});
var on_item_click = (function (ev){
ev.stopPropagation();

var target = ev.target;
var item = target.closest("[role=menuitem]");
if(cljs.core.truth_(item)){
app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_select,false,({"item": item}));

return (app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2 ? app.components.x_context_menu.x_context_menu.close_BANG_.cljs$core$IFn$_invoke$arity$2(el,"select") : app.components.x_context_menu.x_context_menu.close_BANG_.call(null,el,"select"));
} else {
return null;
}
});
layer.addEventListener("keydown",on_key,true);

layer.addEventListener("click",on_click_backdrop);

if(cljs.core.truth_(panel)){
panel.addEventListener("click",on_item_click);
} else {
}

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(layer,"__onKey",on_key);

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(layer,"__onClickBackdrop",on_click_backdrop);

return app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(layer,"__onItemClick",on_item_click);
});
app.components.x_context_menu.x_context_menu.remove_layer_BANG_ = (function app$components$x_context_menu$x_context_menu$remove_layer_BANG_(layer){
if(cljs.core.truth_(layer)){
var on_key = app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(layer,"__onKey");
var on_click_backdrop = app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(layer,"__onClickBackdrop");
var panel = app.components.x_context_menu.x_context_menu.get_panel(layer);
if(cljs.core.truth_(on_key)){
layer.removeEventListener("keydown",on_key,true);
} else {
}

if(cljs.core.truth_(on_click_backdrop)){
layer.removeEventListener("click",on_click_backdrop);
} else {
}

if(cljs.core.truth_(layer.parentNode)){
return layer.parentNode.removeChild(layer);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.close_BANG_ = (function app$components$x_context_menu$x_context_menu$close_BANG_(el,reason){
if(cljs.core.truth_(el.hasAttribute(app.components.x_context_menu.model.attr_open))){
var proceed_QMARK_ = app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_close_request,true,({"reason": reason}));
if(proceed_QMARK_){
el.removeAttribute(app.components.x_context_menu.model.attr_open);

var layer_22459 = app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(el,app.components.x_context_menu.x_context_menu.k_layer);
app.components.x_context_menu.x_context_menu.remove_layer_BANG_(layer_22459);

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_layer,null);

return app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_close,false,({"reason": reason}));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.open_at_coords_BANG_ = (function app$components$x_context_menu$x_context_menu$open_at_coords_BANG_(el,x,y,reason){
if(cljs.core.truth_(el.hasAttribute(app.components.x_context_menu.model.attr_disabled))){
return null;
} else {
var proceed_QMARK_ = app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_open_request,true,({"reason": reason}));
if(proceed_QMARK_){
var m = app.components.x_context_menu.x_context_menu.read_model(el);
var map__22364 = m;
var map__22364__$1 = cljs.core.__destructure_map(map__22364);
var placement = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22364__$1,new cljs.core.Keyword(null,"placement","placement",768366651));
var offset = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22364__$1,new cljs.core.Keyword(null,"offset","offset",296498311));
var z_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22364__$1,new cljs.core.Keyword(null,"z-index","z-index",1892827090));
var vw = window.innerWidth;
var vh = window.innerHeight;
var anchor_rect = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"x","x",2099068185),x,new cljs.core.Keyword(null,"y","y",-1757859776),y,new cljs.core.Keyword(null,"width","width",-384071477),(1),new cljs.core.Keyword(null,"height","height",1025178622),(1)], null);
var panel_est = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),(200),new cljs.core.Keyword(null,"height","height",1025178622),(300)], null);
var pos = app.components.x_context_menu.model.compute_position(placement,offset,anchor_rect,panel_est,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),vw,new cljs.core.Keyword(null,"height","height",1025178622),vh], null),(8));
var layer = app.components.x_context_menu.x_context_menu.make_layer_BANG_(el,z_index);
app.components.x_context_menu.x_context_menu.clone_children_to_panel_BANG_(el,app.components.x_context_menu.x_context_menu.get_panel(layer));

app.components.x_context_menu.x_context_menu.add_layer_listeners_BANG_(el,layer);

app.components.x_context_menu.x_context_menu.position_layer_BANG_(layer,new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pos),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pos),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pos,new cljs.core.Keyword(null,"z-index","z-index",1892827090),z_index));

el.setAttribute(app.components.x_context_menu.model.attr_open,"");

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_layer,layer);

requestAnimationFrame((function (){
var panel = app.components.x_context_menu.x_context_menu.get_panel(layer);
if(cljs.core.truth_(panel)){
var pw_22460 = panel.offsetWidth;
var ph_22461 = panel.offsetHeight;
var pos2_22462 = app.components.x_context_menu.model.compute_position(placement,offset,anchor_rect,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),pw_22460,new cljs.core.Keyword(null,"height","height",1025178622),ph_22461], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),vw,new cljs.core.Keyword(null,"height","height",1025178622),vh], null),(8));
app.components.x_context_menu.x_context_menu.position_layer_BANG_(layer,new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pos2_22462),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pos2_22462),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pos2_22462,new cljs.core.Keyword(null,"z-index","z-index",1892827090),z_index));

return app.components.x_context_menu.x_context_menu.focus_first_BANG_(panel);
} else {
return null;
}
}));

return app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_open,false,({"reason": reason}));
} else {
return null;
}
}
});
app.components.x_context_menu.x_context_menu.open_for_element_BANG_ = (function app$components$x_context_menu$x_context_menu$open_for_element_BANG_(el,anchor_el,reason){
var rect = anchor_el.getBoundingClientRect();
var m = app.components.x_context_menu.x_context_menu.read_model(el);
var map__22395 = m;
var map__22395__$1 = cljs.core.__destructure_map(map__22395);
var placement = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22395__$1,new cljs.core.Keyword(null,"placement","placement",768366651));
var offset = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22395__$1,new cljs.core.Keyword(null,"offset","offset",296498311));
var z_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22395__$1,new cljs.core.Keyword(null,"z-index","z-index",1892827090));
var vw = window.innerWidth;
var vh = window.innerHeight;
var anchor = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"x","x",2099068185),rect.left,new cljs.core.Keyword(null,"y","y",-1757859776),rect.top,new cljs.core.Keyword(null,"width","width",-384071477),rect.width,new cljs.core.Keyword(null,"height","height",1025178622),rect.height], null);
var proceed_QMARK_ = app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_open_request,true,({"reason": reason}));
if(proceed_QMARK_){
var panel_est = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),(200),new cljs.core.Keyword(null,"height","height",1025178622),(300)], null);
var pos = app.components.x_context_menu.model.compute_position(placement,offset,anchor,panel_est,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),vw,new cljs.core.Keyword(null,"height","height",1025178622),vh], null),(8));
var layer = app.components.x_context_menu.x_context_menu.make_layer_BANG_(el,z_index);
app.components.x_context_menu.x_context_menu.clone_children_to_panel_BANG_(el,app.components.x_context_menu.x_context_menu.get_panel(layer));

app.components.x_context_menu.x_context_menu.add_layer_listeners_BANG_(el,layer);

app.components.x_context_menu.x_context_menu.position_layer_BANG_(layer,new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pos),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pos),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pos,new cljs.core.Keyword(null,"z-index","z-index",1892827090),z_index));

el.setAttribute(app.components.x_context_menu.model.attr_open,"");

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_layer,layer);

requestAnimationFrame((function (){
var panel = app.components.x_context_menu.x_context_menu.get_panel(layer);
if(cljs.core.truth_(panel)){
var pw_22466 = panel.offsetWidth;
var ph_22467 = panel.offsetHeight;
var pos2_22468 = app.components.x_context_menu.model.compute_position(placement,offset,anchor,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),pw_22466,new cljs.core.Keyword(null,"height","height",1025178622),ph_22467], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),vw,new cljs.core.Keyword(null,"height","height",1025178622),vh], null),(8));
app.components.x_context_menu.x_context_menu.position_layer_BANG_(layer,new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pos2_22468),new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pos2_22468),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pos2_22468,new cljs.core.Keyword(null,"z-index","z-index",1892827090),z_index));

return app.components.x_context_menu.x_context_menu.focus_first_BANG_(panel);
} else {
return null;
}
}));

return app.components.x_context_menu.x_context_menu.dispatch_BANG_(el,app.components.x_context_menu.model.event_open,false,({"reason": reason}));
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.make_shadow_BANG_ = (function app$components$x_context_menu$x_context_menu$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = app.components.x_context_menu.x_context_menu.make_el("style");
var slot = app.components.x_context_menu.x_context_menu.make_el("slot");
(style.textContent = app.components.x_context_menu.x_context_menu.host_style_text);

root.appendChild(style);

root.appendChild(slot);

var refs = ({});
app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(refs,"root",root);

app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(refs,"slot",slot);

return app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_refs,refs);
});
app.components.x_context_menu.x_context_menu.connected_BANG_ = (function app$components$x_context_menu$x_context_menu$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(el,app.components.x_context_menu.x_context_menu.k_refs))){
} else {
app.components.x_context_menu.x_context_menu.make_shadow_BANG_(el);
}

if(cljs.core.truth_(el.hasAttribute(app.components.x_context_menu.model.attr_open))){
return el.removeAttribute(app.components.x_context_menu.model.attr_open);
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.disconnected_BANG_ = (function app$components$x_context_menu$x_context_menu$disconnected_BANG_(el){
var layer = app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(el,app.components.x_context_menu.x_context_menu.k_layer);
if(cljs.core.truth_(layer)){
app.components.x_context_menu.x_context_menu.remove_layer_BANG_(layer);

return app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_layer,null);
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.attribute_changed_BANG_ = (function app$components$x_context_menu$x_context_menu$attribute_changed_BANG_(el,name,_old,_new){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(name,app.components.x_context_menu.model.attr_open)){
if(cljs.core.truth_(el.hasAttribute(app.components.x_context_menu.model.attr_open))){
return null;
} else {
var layer = app.components.x_context_menu.x_context_menu.goog$module$goog$object.get(el,app.components.x_context_menu.x_context_menu.k_layer);
if(cljs.core.truth_(layer)){
app.components.x_context_menu.x_context_menu.remove_layer_BANG_(layer);

return app.components.x_context_menu.x_context_menu.goog$module$goog$object.set(el,app.components.x_context_menu.x_context_menu.k_layer,null);
} else {
return null;
}
}
} else {
return null;
}
});
app.components.x_context_menu.x_context_menu.define_bool_prop_BANG_ = (function app$components$x_context_menu$x_context_menu$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.hasAttribute(attr_name);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name,"");
} else {
return this$.removeAttribute(attr_name);
}
})}));
});
app.components.x_context_menu.x_context_menu.define_str_prop_BANG_ = (function app$components$x_context_menu$x_context_menu$define_str_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.getAttribute(attr_name);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr_name);
}
})}));
});
app.components.x_context_menu.x_context_menu.define_methods_BANG_ = (function app$components$x_context_menu$x_context_menu$define_methods_BANG_(proto){
(proto["openAt"] = (function (x,y){
var this$ = this;
return app.components.x_context_menu.x_context_menu.open_at_coords_BANG_(this$,x,y,"programmatic");
}));

(proto["toggleAt"] = (function (x,y){
var this$ = this;
if(cljs.core.truth_(this$.hasAttribute(app.components.x_context_menu.model.attr_open))){
return app.components.x_context_menu.x_context_menu.close_BANG_(this$,"toggle");
} else {
return app.components.x_context_menu.x_context_menu.open_at_coords_BANG_(this$,x,y,"toggle");
}
}));

(proto["openForElement"] = (function (anchor_el){
var this$ = this;
return app.components.x_context_menu.x_context_menu.open_for_element_BANG_(this$,anchor_el,"programmatic");
}));

return (proto["close"] = (function (){
var this$ = this;
return app.components.x_context_menu.x_context_menu.close_BANG_(this$,"programmatic");
}));
});
app.components.x_context_menu.x_context_menu.make_constructor = (function app$components$x_context_menu$x_context_menu$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_context_menu.x_context_menu.init_BANG_ = (function app$components$x_context_menu$x_context_menu$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_context_menu.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_context_menu.x_context_menu.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_context_menu.x_context_menu.define_bool_prop_BANG_(proto,"open",app.components.x_context_menu.model.attr_open);

app.components.x_context_menu.x_context_menu.define_bool_prop_BANG_(proto,"disabled",app.components.x_context_menu.model.attr_disabled);

app.components.x_context_menu.x_context_menu.define_str_prop_BANG_(proto,"placement",app.components.x_context_menu.model.attr_placement);

app.components.x_context_menu.x_context_menu.define_methods_BANG_(proto);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_context_menu.x_context_menu.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_context_menu.x_context_menu.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_context_menu.x_context_menu.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor["observedAttributes"] = app.components.x_context_menu.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_context_menu.model.tag_name,ctor);
}
});

//# sourceMappingURL=app.components.x_context_menu.x_context_menu.js.map
