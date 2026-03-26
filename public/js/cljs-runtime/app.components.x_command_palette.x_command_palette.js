goog.provide('app.components.x_command_palette.x_command_palette');
goog.scope(function(){
  app.components.x_command_palette.x_command_palette.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_command_palette.x_command_palette.k_refs = "__xCommandPaletteRefs";
app.components.x_command_palette.x_command_palette.k_handlers = "__xCommandPaletteHandlers";
app.components.x_command_palette.x_command_palette.k_items = "__xCommandPaletteItems";
app.components.x_command_palette.x_command_palette.k_query = "__xCommandPaletteQuery";
app.components.x_command_palette.x_command_palette.k_active_idx = "__xCommandPaletteActiveIdx";
app.components.x_command_palette.x_command_palette.make_el = (function app$components$x_command_palette$x_command_palette$make_el(tag){
return document.createElement(tag);
});
app.components.x_command_palette.x_command_palette.get_ref = (function app$components$x_command_palette$x_command_palette$get_ref(el,k){
return app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs),cljs.core.name(k));
});
/**
 * Read all observed attrs from the element and normalize.
 */
app.components.x_command_palette.x_command_palette.read_model = (function app$components$x_command_palette$x_command_palette$read_model(el){
return app.components.x_command_palette.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"empty-text-raw","empty-text-raw",1477406757),new cljs.core.Keyword(null,"portal-raw","portal-raw",2293766),new cljs.core.Keyword(null,"close-on-scrim-raw","close-on-scrim-raw",159828234),new cljs.core.Keyword(null,"disabled-raw","disabled-raw",498098381),new cljs.core.Keyword(null,"close-on-escape-raw","close-on-escape-raw",-1942646255),new cljs.core.Keyword(null,"no-scrim-raw","no-scrim-raw",-1344365743),new cljs.core.Keyword(null,"dismissible-raw","dismissible-raw",947680658),new cljs.core.Keyword(null,"modal-raw","modal-raw",-263722282),new cljs.core.Keyword(null,"open-present?","open-present?",965047899),new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657)],[el.getAttribute(app.components.x_command_palette.model.attr_label),el.getAttribute(app.components.x_command_palette.model.attr_empty_text),el.getAttribute(app.components.x_command_palette.model.attr_portal),el.getAttribute(app.components.x_command_palette.model.attr_close_on_scrim),el.getAttribute(app.components.x_command_palette.model.attr_disabled),el.getAttribute(app.components.x_command_palette.model.attr_close_on_escape),el.getAttribute(app.components.x_command_palette.model.attr_no_scrim),el.getAttribute(app.components.x_command_palette.model.attr_dismissible),el.getAttribute(app.components.x_command_palette.model.attr_modal),el.hasAttribute(app.components.x_command_palette.model.attr_open),el.getAttribute(app.components.x_command_palette.model.attr_placeholder)]));
});
app.components.x_command_palette.x_command_palette.dispatch_BANG_ = (function app$components$x_command_palette$x_command_palette$dispatch_BANG_(el,event_name,cancelable,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cljs.core.boolean$(cancelable)})));
el.dispatchEvent(ev);

return ev;
});
app.components.x_command_palette.x_command_palette.style_text = (""+":host{display:contents;}"+"[part=overlay]{"+"display:none;position:fixed;inset:0;"+"background:rgba(0,0,0,0.45);"+"z-index:var(--x-command-palette-z,800);"+"}"+"[part=panel]{"+"display:none;position:fixed;"+"top:20vh;left:50%;transform:translateX(-50%);"+"width:var(--x-command-palette-width,560px);"+"max-height:60vh;"+"z-index:calc(var(--x-command-palette-z,800) + 1);"+"flex-direction:column;"+"border-radius:var(--x-command-palette-radius,12px);"+"background:var(--x-command-palette-bg,#fff);"+"box-shadow:0 20px 60px rgba(0,0,0,0.25),0 4px 16px rgba(0,0,0,0.12);"+"overflow:hidden;"+"}"+":host([open]) [part=overlay]{display:block;}"+":host([open]) [part=panel]{display:flex;}"+"[part=search-wrap]{"+"display:flex;align-items:center;gap:8px;"+"padding:12px 16px;"+"border-bottom:1px solid var(--x-command-palette-divider,rgba(0,0,0,0.08));"+"}"+"[part=search-icon]{"+"flex:none;display:inline-flex;align-items:center;justify-content:center;"+"width:18px;height:18px;"+"color:var(--x-command-palette-icon-color,#64748b);"+"}"+"[part=input]{"+"flex:1;border:none;outline:none;background:transparent;"+"font-size:1rem;line-height:1.5;"+"color:var(--x-command-palette-text,inherit);"+"}"+"[part=input]::placeholder{"+"color:var(--x-command-palette-placeholder,#94a3b8);"+"}"+"[part=clear-btn]{"+"all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"+"width:24px;height:24px;border-radius:50%;"+"color:var(--x-command-palette-icon-color,#64748b);"+"}"+"[part=clear-btn]:focus-visible{outline:2px solid var(--x-command-palette-focus-ring,#60a5fa);}"+"[part=clear-btn][hidden]{display:none;}"+"[part=list-wrap]{"+"flex:1;overflow-y:auto;overscroll-behavior:contain;"+"padding:8px 0;"+"}"+"[part=list]{display:flex;flex-direction:column;}"+"[part=item]{"+"padding:10px 16px;cursor:default;"+"font-size:0.9375rem;line-height:1.4;"+"color:var(--x-command-palette-item-text,inherit);"+"border-radius:0;"+"outline:none;"+"}"+"[part=item]:hover,[part=item]:focus{"+"background:var(--x-command-palette-item-hover,#f1f5f9);"+"}"+"[part=item][aria-selected=true]{"+"background:var(--x-command-palette-item-active,#e0e7ff);"+"color:var(--x-command-palette-item-active-text,#3730a3);"+"}"+"[part=item][aria-disabled=true]{opacity:0.45;cursor:default;pointer-events:none;}"+"[part=group-header]{"+"padding:8px 16px 4px;"+"font-size:0.75rem;font-weight:600;letter-spacing:0.05em;text-transform:uppercase;"+"color:var(--x-command-palette-group-text,#94a3b8);"+"}"+"[part=empty]{"+"padding:32px 16px;text-align:center;"+"color:var(--x-command-palette-empty-text,#94a3b8);"+"font-size:0.9375rem;"+"}"+"[part=empty][hidden]{display:none;}"+"@media (prefers-color-scheme:dark){"+"[part=panel]{background:var(--x-command-palette-bg,#1e293b);box-shadow:0 20px 60px rgba(0,0,0,0.6),0 4px 16px rgba(0,0,0,0.4);}"+"[part=search-wrap]{border-bottom-color:var(--x-command-palette-divider,rgba(255,255,255,0.08));}"+"[part=item]:hover,[part=item]:focus{background:var(--x-command-palette-item-hover,#334155);}"+"[part=item][aria-selected=true]{background:var(--x-command-palette-item-active,#312e81);color:var(--x-command-palette-item-active-text,#c7d2fe);}"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=panel],[part=overlay]{transition:none;}"+"}");
/**
 * Attach shadow root, build DOM, store refs. Returns refs map.
 */
app.components.x_command_palette.x_command_palette.make_shadow_BANG_ = (function app$components$x_command_palette$x_command_palette$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = app.components.x_command_palette.x_command_palette.make_el("style");
var overlay = app.components.x_command_palette.x_command_palette.make_el("div");
var panel = app.components.x_command_palette.x_command_palette.make_el("div");
var search_wrap = app.components.x_command_palette.x_command_palette.make_el("div");
var search_icon = app.components.x_command_palette.x_command_palette.make_el("span");
var input_el = app.components.x_command_palette.x_command_palette.make_el("input");
var clear_btn = app.components.x_command_palette.x_command_palette.make_el("button");
var list_wrap = app.components.x_command_palette.x_command_palette.make_el("div");
var list_el = app.components.x_command_palette.x_command_palette.make_el("div");
var empty_el = app.components.x_command_palette.x_command_palette.make_el("div");
(style.textContent = app.components.x_command_palette.x_command_palette.style_text);

overlay.setAttribute("part","overlay");

panel.setAttribute("part","panel");

panel.setAttribute("role","dialog");

panel.setAttribute("aria-modal","true");

search_wrap.setAttribute("part","search-wrap");

search_icon.setAttribute("part","search-icon");

search_icon.setAttribute("aria-hidden","true");

(search_icon.textContent = "\uD83D\uDD0D");

input_el.setAttribute("part","input");

input_el.setAttribute("type","search");

input_el.setAttribute("role","combobox");

input_el.setAttribute("autocomplete","off");

input_el.setAttribute("autocorrect","off");

input_el.setAttribute("spellcheck","false");

input_el.setAttribute("aria-autocomplete","list");

input_el.setAttribute("aria-expanded","false");

clear_btn.setAttribute("part","clear-btn");

clear_btn.setAttribute("type","button");

clear_btn.setAttribute("aria-label","Clear");

clear_btn.setAttribute("hidden","");

list_wrap.setAttribute("part","list-wrap");

list_el.setAttribute("part","list");

list_el.setAttribute("role","listbox");

list_el.setAttribute("aria-label","Results");

empty_el.setAttribute("part","empty");

empty_el.setAttribute("hidden","");

search_wrap.appendChild(search_icon);

search_wrap.appendChild(input_el);

search_wrap.appendChild(clear_btn);

list_wrap.appendChild(list_el);

list_wrap.appendChild(empty_el);

panel.appendChild(search_wrap);

panel.appendChild(list_wrap);

root.appendChild(style);

root.appendChild(overlay);

root.appendChild(panel);

var refs = ({"overlay": overlay, "panel": panel, "input": input_el, "clear-btn": clear_btn, "list": list_el, "empty": empty_el});
app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_refs,refs);

return refs;
});
/**
 * Clear list and re-render items filtered by current query.
 */
app.components.x_command_palette.x_command_palette.render_items_BANG_ = (function app$components$x_command_palette$x_command_palette$render_items_BANG_(el){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
if(cljs.core.truth_(refs)){
var list_el = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"list");
var empty_el = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"empty");
var items_js = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_items);
var query = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_query);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var items = app.components.x_command_palette.model.normalize_items(items_js);
var map__23592 = app.components.x_command_palette.model.filter_items(items,query);
var map__23592__$1 = cljs.core.__destructure_map(map__23592);
var visible = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23592__$1,new cljs.core.Keyword(null,"visible","visible",-1024216805));
var active_idx = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_active_idx);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})();
(list_el.textContent = "");

if(cljs.core.empty_QMARK_(visible)){
return empty_el.removeAttribute("hidden");
} else {
empty_el.setAttribute("hidden","");

var seq__23593 = cljs.core.seq(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,visible));
var chunk__23594 = null;
var count__23595 = (0);
var i__23596 = (0);
while(true){
if((i__23596 < count__23595)){
var vec__23603 = chunk__23594.cljs$core$IIndexed$_nth$arity$2(null,i__23596);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23603,(0),null);
var item = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23603,(1),null);
var div_23610 = app.components.x_command_palette.x_command_palette.make_el("div");
div_23610.setAttribute("part","item");

div_23610.setAttribute("role","option");

div_23610.setAttribute("data-id",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(item))));

div_23610.setAttribute("data-idx",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(idx)));

div_23610.setAttribute("tabindex","-1");

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(item))){
div_23610.setAttribute("aria-disabled","true");
} else {
}

(div_23610.textContent = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(item));

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,active_idx)){
div_23610.setAttribute("aria-selected","true");
} else {
}

list_el.appendChild(div_23610);


var G__23611 = seq__23593;
var G__23612 = chunk__23594;
var G__23613 = count__23595;
var G__23614 = (i__23596 + (1));
seq__23593 = G__23611;
chunk__23594 = G__23612;
count__23595 = G__23613;
i__23596 = G__23614;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__23593);
if(temp__5823__auto__){
var seq__23593__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__23593__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__23593__$1);
var G__23615 = cljs.core.chunk_rest(seq__23593__$1);
var G__23616 = c__5673__auto__;
var G__23617 = cljs.core.count(c__5673__auto__);
var G__23618 = (0);
seq__23593 = G__23615;
chunk__23594 = G__23616;
count__23595 = G__23617;
i__23596 = G__23618;
continue;
} else {
var vec__23606 = cljs.core.first(seq__23593__$1);
var idx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23606,(0),null);
var item = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__23606,(1),null);
var div_23619 = app.components.x_command_palette.x_command_palette.make_el("div");
div_23619.setAttribute("part","item");

div_23619.setAttribute("role","option");

div_23619.setAttribute("data-id",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(item))));

div_23619.setAttribute("data-idx",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(idx)));

div_23619.setAttribute("tabindex","-1");

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(item))){
div_23619.setAttribute("aria-disabled","true");
} else {
}

(div_23619.textContent = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(item));

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(idx,active_idx)){
div_23619.setAttribute("aria-selected","true");
} else {
}

list_el.appendChild(div_23619);


var G__23620 = cljs.core.next(seq__23593__$1);
var G__23621 = null;
var G__23622 = (0);
var G__23623 = (0);
seq__23593 = G__23620;
chunk__23594 = G__23621;
count__23595 = G__23622;
i__23596 = G__23623;
continue;
}
} else {
return null;
}
}
break;
}
}
} else {
return null;
}
});
/**
 * Sync all shadow DOM parts with current element state.
 */
app.components.x_command_palette.x_command_palette.render_BANG_ = (function app$components$x_command_palette$x_command_palette$render_BANG_(el){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
if(cljs.core.truth_(refs)){
var m = app.components.x_command_palette.x_command_palette.read_model(el);
var panel = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"panel");
var input = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"input");
var overlay = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"overlay");
var empty_el = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"empty");
var query = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_query);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var label = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(m);
var placeholder = new cljs.core.Keyword(null,"placeholder","placeholder",-104873083).cljs$core$IFn$_invoke$arity$1(m);
var empty_text = new cljs.core.Keyword(null,"empty-text","empty-text",1554384594).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(label)){
panel.setAttribute("aria-label",label);
} else {
panel.setAttribute("aria-label","Command palette");
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"scrim?","scrim?",744210580).cljs$core$IFn$_invoke$arity$1(m))){
overlay.removeAttribute("hidden");
} else {
overlay.setAttribute("hidden","");
}

input.setAttribute("placeholder",placeholder);

input.setAttribute("aria-expanded",(cljs.core.truth_(new cljs.core.Keyword(null,"open?","open?",1238443125).cljs$core$IFn$_invoke$arity$1(m))?"true":"false"));

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m))){
input.setAttribute("disabled","");

input.setAttribute("aria-disabled","true");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m))){
} else {
input.removeAttribute("disabled");

input.removeAttribute("aria-disabled");
}

(empty_el.textContent = empty_text);

return app.components.x_command_palette.x_command_palette.render_items_BANG_(el);
} else {
return null;
}
});
/**
 * Actually open: set attr, focus input, dispatch open event.
 */
app.components.x_command_palette.x_command_palette.do_open_BANG_ = (function app$components$x_command_palette$x_command_palette$do_open_BANG_(el){
el.setAttribute(app.components.x_command_palette.model.attr_open,"");

var refs_23624 = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var input_23625 = (cljs.core.truth_(refs_23624)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs_23624,"input"):null);
if(cljs.core.truth_(input_23625)){
input_23625.focus();
} else {
}

app.components.x_command_palette.x_command_palette.render_BANG_(el);

return app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_open,false,({}));
});
/**
 * Actually close: remove attr, dispatch close event.
 */
app.components.x_command_palette.x_command_palette.do_close_BANG_ = (function app$components$x_command_palette$x_command_palette$do_close_BANG_(el){
el.removeAttribute(app.components.x_command_palette.model.attr_open);

app.components.x_command_palette.x_command_palette.render_BANG_(el);

return app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_close,false,({}));
});
app.components.x_command_palette.x_command_palette.request_open_BANG_ = (function app$components$x_command_palette$x_command_palette$request_open_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_command_palette.model.attr_open))){
return null;
} else {
var ev = app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_open_request,true,({}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
return app.components.x_command_palette.x_command_palette.do_open_BANG_(el);
}
}
});
app.components.x_command_palette.x_command_palette.request_close_BANG_ = (function app$components$x_command_palette$x_command_palette$request_close_BANG_(el){
if(cljs.core.truth_(el.hasAttribute(app.components.x_command_palette.model.attr_open))){
var ev = app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_close_request,true,({}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
return app.components.x_command_palette.x_command_palette.do_close_BANG_(el);
}
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.active_visible = (function app$components$x_command_palette$x_command_palette$active_visible(el){
var items_js = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_items);
var query = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_query);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var items = app.components.x_command_palette.model.normalize_items(items_js);
var map__23609 = app.components.x_command_palette.model.filter_items(items,query);
var map__23609__$1 = cljs.core.__destructure_map(map__23609);
var visible = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23609__$1,new cljs.core.Keyword(null,"visible","visible",-1024216805));
return visible;
});
app.components.x_command_palette.x_command_palette.move_active_BANG_ = (function app$components$x_command_palette$x_command_palette$move_active_BANG_(el,direction){
var visible = app.components.x_command_palette.x_command_palette.active_visible(el);
var cur_idx = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_active_idx);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})();
var new_idx = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(direction,new cljs.core.Keyword(null,"down","down",1565245570)))?app.components.x_command_palette.model.next_active_idx(visible,cur_idx):app.components.x_command_palette.model.prev_active_idx(visible,cur_idx));
app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_active_idx,new_idx);

app.components.x_command_palette.x_command_palette.render_items_BANG_(el);

var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var list_el = (cljs.core.truth_(refs)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"list"):null);
var item_el = (cljs.core.truth_(list_el)?list_el.querySelector((""+"[data-idx='"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new_idx)+"']")):null);
if(cljs.core.truth_(item_el)){
return item_el.scrollIntoView(({"block": "nearest"}));
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.select_active_BANG_ = (function app$components$x_command_palette$x_command_palette$select_active_BANG_(el){
var visible = app.components.x_command_palette.x_command_palette.active_visible(el);
var cur_idx = (function (){var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_active_idx);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})();
var item = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(visible,cur_idx,null);
if(cljs.core.truth_((function (){var and__5140__auto__ = item;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(item));
} else {
return and__5140__auto__;
}
})())){
var ev = app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_select_request,true,({"item": cljs.core.clj__GT_js(item)}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_select,false,({"item": cljs.core.clj__GT_js(item)}));

return app.components.x_command_palette.x_command_palette.request_close_BANG_(el);
}
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.on_input_input_BANG_ = (function app$components$x_command_palette$x_command_palette$on_input_input_BANG_(el,_e){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"input"):null);
var clr = (cljs.core.truth_(refs)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"clear-btn"):null);
var q = (cljs.core.truth_(inp)?inp.value:null);
app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_query,(function (){var or__5142__auto__ = q;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_active_idx,(0));

if(cljs.core.truth_(clr)){
if(cljs.core.truth_((function (){var and__5140__auto__ = q;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(q,"");
} else {
return and__5140__auto__;
}
})())){
clr.removeAttribute("hidden");
} else {
clr.setAttribute("hidden","");
}
} else {
}

app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_query_change,false,({"query": (function (){var or__5142__auto__ = q;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()}));

return app.components.x_command_palette.x_command_palette.render_items_BANG_(el);
});
app.components.x_command_palette.x_command_palette.on_input_keydown_BANG_ = (function app$components$x_command_palette$x_command_palette$on_input_keydown_BANG_(el,e){
var key = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowDown")){
e.preventDefault();

return app.components.x_command_palette.x_command_palette.move_active_BANG_(el,new cljs.core.Keyword(null,"down","down",1565245570));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowUp")){
e.preventDefault();

return app.components.x_command_palette.x_command_palette.move_active_BANG_(el,new cljs.core.Keyword(null,"up","up",-269712113));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")){
e.preventDefault();

return app.components.x_command_palette.x_command_palette.select_active_BANG_(el);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Escape")){
var m = app.components.x_command_palette.x_command_palette.read_model(el);
if(cljs.core.truth_(new cljs.core.Keyword(null,"close-on-escape?","close-on-escape?",-1559868909).cljs$core$IFn$_invoke$arity$1(m))){
return app.components.x_command_palette.x_command_palette.request_close_BANG_(el);
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
app.components.x_command_palette.x_command_palette.on_scrim_click_BANG_ = (function app$components$x_command_palette$x_command_palette$on_scrim_click_BANG_(el,_e){
var m = app.components.x_command_palette.x_command_palette.read_model(el);
if(cljs.core.truth_(new cljs.core.Keyword(null,"close-on-scrim?","close-on-scrim?",-367113965).cljs$core$IFn$_invoke$arity$1(m))){
return app.components.x_command_palette.x_command_palette.request_close_BANG_(el);
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.on_list_click_BANG_ = (function app$components$x_command_palette$x_command_palette$on_list_click_BANG_(el,e){
var target = e.target;
var item_el = target.closest("[part=item]");
if(cljs.core.truth_(item_el)){
var idx = parseInt(item_el.getAttribute("data-idx"),(10));
var visible = app.components.x_command_palette.x_command_palette.active_visible(el);
var item = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(visible,idx,null);
if(cljs.core.truth_((function (){var and__5140__auto__ = item;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(item));
} else {
return and__5140__auto__;
}
})())){
var ev = app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_select_request,true,({"item": cljs.core.clj__GT_js(item)}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_select,false,({"item": cljs.core.clj__GT_js(item)}));

return app.components.x_command_palette.x_command_palette.request_close_BANG_(el);
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.on_clear_click_BANG_ = (function app$components$x_command_palette$x_command_palette$on_clear_click_BANG_(el,_e){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"input"):null);
var clr = (cljs.core.truth_(refs)?app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"clear-btn"):null);
if(cljs.core.truth_(inp)){
(inp.value = "");
} else {
}

if(cljs.core.truth_(clr)){
clr.setAttribute("hidden","");
} else {
}

app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_query,"");

app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_active_idx,(0));

app.components.x_command_palette.x_command_palette.dispatch_BANG_(el,app.components.x_command_palette.model.event_query_change,false,({"query": ""}));

app.components.x_command_palette.x_command_palette.render_items_BANG_(el);

if(cljs.core.truth_(inp)){
return inp.focus();
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.add_listeners_BANG_ = (function app$components$x_command_palette$x_command_palette$add_listeners_BANG_(el){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var input = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"input");
var overlay = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"overlay");
var list_el = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"list");
var clr = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"clear-btn");
var h_input = (function (e){
return app.components.x_command_palette.x_command_palette.on_input_input_BANG_(el,e);
});
var h_keydown = (function (e){
return app.components.x_command_palette.x_command_palette.on_input_keydown_BANG_(el,e);
});
var h_scrim = (function (e){
return app.components.x_command_palette.x_command_palette.on_scrim_click_BANG_(el,e);
});
var h_list = (function (e){
return app.components.x_command_palette.x_command_palette.on_list_click_BANG_(el,e);
});
var h_clear = (function (e){
return app.components.x_command_palette.x_command_palette.on_clear_click_BANG_(el,e);
});
input.addEventListener("input",h_input);

input.addEventListener("keydown",h_keydown);

overlay.addEventListener("click",h_scrim);

list_el.addEventListener("click",h_list);

clr.addEventListener("click",h_clear);

return app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(el,app.components.x_command_palette.x_command_palette.k_handlers,({"input": h_input, "keydown": h_keydown, "scrim": h_scrim, "list": h_list, "clear": h_clear}));
});
app.components.x_command_palette.x_command_palette.remove_listeners_BANG_ = (function app$components$x_command_palette$x_command_palette$remove_listeners_BANG_(el){
var refs = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs);
var handlers = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_handlers);
if(cljs.core.truth_((function (){var and__5140__auto__ = refs;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
var input = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"input");
var overlay = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"overlay");
var list_el = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"list");
var clr = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(refs,"clear-btn");
input.removeEventListener("input",app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(handlers,"input"));

input.removeEventListener("keydown",app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(handlers,"keydown"));

overlay.removeEventListener("click",app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(handlers,"scrim"));

list_el.removeEventListener("click",app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(handlers,"list"));

return clr.removeEventListener("click",app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(handlers,"clear"));
} else {
return null;
}
});
app.components.x_command_palette.x_command_palette.connected_BANG_ = (function app$components$x_command_palette$x_command_palette$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(el,app.components.x_command_palette.x_command_palette.k_refs))){
} else {
app.components.x_command_palette.x_command_palette.make_shadow_BANG_(el);
}

app.components.x_command_palette.x_command_palette.remove_listeners_BANG_(el);

app.components.x_command_palette.x_command_palette.add_listeners_BANG_(el);

return app.components.x_command_palette.x_command_palette.render_BANG_(el);
});
app.components.x_command_palette.x_command_palette.disconnected_BANG_ = (function app$components$x_command_palette$x_command_palette$disconnected_BANG_(el){
return app.components.x_command_palette.x_command_palette.remove_listeners_BANG_(el);
});
app.components.x_command_palette.x_command_palette.attribute_changed_BANG_ = (function app$components$x_command_palette$x_command_palette$attribute_changed_BANG_(el,_name,_old,_new){
return app.components.x_command_palette.x_command_palette.render_BANG_(el);
});
app.components.x_command_palette.x_command_palette.define_bool_prop_BANG_ = (function app$components$x_command_palette$x_command_palette$define_bool_prop_BANG_(proto,prop_name,attr_name){
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
app.components.x_command_palette.x_command_palette.define_items_prop_BANG_ = (function app$components$x_command_palette$x_command_palette$define_items_prop_BANG_(proto){
return Object.defineProperty(proto,"items",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = app.components.x_command_palette.x_command_palette.goog$module$goog$object.get(this$,app.components.x_command_palette.x_command_palette.k_items);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return [];
}
}), "set": (function (v){
var this$ = this;
app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(this$,app.components.x_command_palette.x_command_palette.k_items,v);

app.components.x_command_palette.x_command_palette.goog$module$goog$object.set(this$,app.components.x_command_palette.x_command_palette.k_active_idx,(0));

return app.components.x_command_palette.x_command_palette.render_items_BANG_(this$);
})}));
});
app.components.x_command_palette.x_command_palette.define_methods_BANG_ = (function app$components$x_command_palette$x_command_palette$define_methods_BANG_(proto){
Object.defineProperty(proto,"open",({"configurable": true, "writable": true, "value": (function (){
var this$ = this;
return app.components.x_command_palette.x_command_palette.request_open_BANG_(this$);
})}));

(proto["close"] = (function (){
var this$ = this;
return app.components.x_command_palette.x_command_palette.request_close_BANG_(this$);
}));

return (proto["toggle"] = (function (){
var this$ = this;
if(cljs.core.truth_(this$.hasAttribute(app.components.x_command_palette.model.attr_open))){
return app.components.x_command_palette.x_command_palette.request_close_BANG_(this$);
} else {
return app.components.x_command_palette.x_command_palette.request_open_BANG_(this$);
}
}));
});
app.components.x_command_palette.x_command_palette.make_constructor = (function app$components$x_command_palette$x_command_palette$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_command_palette.x_command_palette.init_BANG_ = (function app$components$x_command_palette$x_command_palette$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_command_palette.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_command_palette.x_command_palette.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_command_palette.x_command_palette.define_bool_prop_BANG_(proto,"open",app.components.x_command_palette.model.attr_open);

app.components.x_command_palette.x_command_palette.define_bool_prop_BANG_(proto,"disabled",app.components.x_command_palette.model.attr_disabled);

app.components.x_command_palette.x_command_palette.define_items_prop_BANG_(proto);

app.components.x_command_palette.x_command_palette.define_methods_BANG_(proto);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_command_palette.x_command_palette.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_command_palette.x_command_palette.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_command_palette.x_command_palette.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor["observedAttributes"] = app.components.x_command_palette.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_command_palette.model.tag_name,ctor);
}
});

//# sourceMappingURL=app.components.x_command_palette.x_command_palette.js.map
