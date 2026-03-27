goog.provide('app.components.x_table_cell.x_table_cell');
goog.scope(function(){
  app.components.x_table_cell.x_table_cell.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_table_cell.x_table_cell.k_refs = "__xTableCellRefs";
app.components.x_table_cell.x_table_cell.k_model = "__xTableCellModel";
app.components.x_table_cell.x_table_cell.k_handlers = "__xTableCellHandlers";
app.components.x_table_cell.x_table_cell.svg_sort_neutral = "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 3L5 7h6L8 3zm0 10l3-4H5l3 4z\"/></svg>";
app.components.x_table_cell.x_table_cell.svg_sort_asc = "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 4L4 10h8L8 4z\"/></svg>";
app.components.x_table_cell.x_table_cell.svg_sort_desc = "<svg width=\"16\" height=\"16\" viewBox=\"0 0 16 16\" fill=\"currentColor\" aria-hidden=\"true\"><path d=\"M8 12l4-6H4l4 6z\"/></svg>";
app.components.x_table_cell.x_table_cell.style_text = (""+":host{"+"display:block;"+"box-sizing:border-box;"+"color-scheme:light dark;"+"--x-table-cell-padding:0.5rem 0.75rem;"+"--x-table-cell-bg:transparent;"+"--x-table-cell-header-bg:rgba(0,0,0,0.04);"+"--x-table-cell-border-color:rgba(0,0,0,0.1);"+"--x-table-cell-border-width:1px;"+"--x-table-cell-color:inherit;"+"--x-table-cell-header-color:inherit;"+"--x-table-cell-font-size:inherit;"+"--x-table-cell-header-font-weight:600;"+"--x-table-cell-min-width:0;"+"--x-table-cell-max-width:none;"+"--x-table-cell-sort-color:rgba(0,0,0,0.4);"+"--x-table-cell-sort-hover-color:rgba(0,0,0,0.7);"+"--x-table-cell-focus-ring:rgba(59,130,246,0.5);"+"--x-table-cell-transition-duration:150ms;"+"--x-table-cell-sticky-bg:#ffffff;"+"--x-table-cell-disabled-opacity:0.45;"+"min-width:var(--x-table-cell-min-width);"+"max-width:var(--x-table-cell-max-width);}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-table-cell-header-bg:rgba(255,255,255,0.06);"+"--x-table-cell-border-color:rgba(255,255,255,0.1);"+"--x-table-cell-sort-color:rgba(255,255,255,0.4);"+"--x-table-cell-sort-hover-color:rgba(255,255,255,0.7);"+"--x-table-cell-sticky-bg:#1f2937;}}"+":host([data-sticky='start']){"+"position:sticky;"+"inset-inline-start:0;"+"background:var(--x-table-cell-sticky-bg);"+"z-index:1;}"+":host([data-sticky='end']){"+"position:sticky;"+"inset-inline-end:0;"+"background:var(--x-table-cell-sticky-bg);"+"z-index:1;}"+":host([disabled]){"+"opacity:var(--x-table-cell-disabled-opacity);}"+"[part=cell]{"+"display:flex;"+"align-items:center;"+"justify-content:flex-start;"+"padding:var(--x-table-cell-padding);"+"background:var(--x-table-cell-bg);"+"border-bottom:var(--x-table-cell-border-width) solid var(--x-table-cell-border-color);"+"color:var(--x-table-cell-color);"+"font-size:var(--x-table-cell-font-size);"+"min-width:0;"+"box-sizing:border-box;}"+":host([data-type='header']) [part=cell]{"+"background:var(--x-table-cell-header-bg);"+"color:var(--x-table-cell-header-color);"+"font-weight:var(--x-table-cell-header-font-weight);}"+":host([data-align='start']) [part=cell]{justify-content:flex-start;}"+":host([data-align='center']) [part=cell]{justify-content:center;}"+":host([data-align='end']) [part=cell]{justify-content:flex-end;}"+":host([data-valign='top']) [part=cell]{align-items:flex-start;}"+":host([data-valign='middle']) [part=cell]{align-items:center;}"+":host([data-valign='bottom']) [part=cell]{align-items:flex-end;}"+"[part=content]{"+"flex:1 1 auto;"+"min-width:0;}"+":host([data-truncate]) [part=content]{"+"overflow:hidden;"+"text-overflow:ellipsis;"+"white-space:nowrap;}"+"[part=sort-btn]{"+"flex:0 0 auto;"+"appearance:none;"+"border:0;"+"background:transparent;"+"padding:2px;"+"margin:0 0 0 4px;"+"cursor:pointer;"+"color:var(--x-table-cell-sort-color);"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"border-radius:4px;"+"transition:color var(--x-table-cell-transition-duration) ease;"+"line-height:1;}"+"[part=sort-btn]:hover{"+"color:var(--x-table-cell-sort-hover-color);}"+"[part=sort-btn]:focus{"+"outline:none;}"+"[part=sort-btn]:focus-visible{"+"outline:2px solid var(--x-table-cell-focus-ring);"+"outline-offset:2px;}"+"[part=sort-btn][disabled]{"+"cursor:default;"+"pointer-events:none;"+"opacity:0.4;}"+"[part=sort-btn][hidden]{"+"display:none !important;}"+"@media (prefers-reduced-motion:reduce){"+"[part=sort-btn]{transition:none !important;}}");
app.components.x_table_cell.x_table_cell.init_dom_BANG_ = (function app$components$x_table_cell$x_table_cell$init_dom_BANG_(el){
var root_23282 = el.attachShadow(({"mode": "open"}));
var style_23283 = document.createElement("style");
var cell_23284 = document.createElement("div");
var content_23285 = document.createElement("span");
var default_slot_23286 = document.createElement("slot");
var sort_btn_23287 = document.createElement("button");
var sort_icon_slot_23288 = document.createElement("slot");
var sort_icon_default_23289 = document.createElement("span");
(style_23283.textContent = app.components.x_table_cell.x_table_cell.style_text);

cell_23284.setAttribute("part","cell");

content_23285.setAttribute("part","content");

content_23285.appendChild(default_slot_23286);

sort_btn_23287.setAttribute("part","sort-btn");

sort_btn_23287.setAttribute("type","button");

sort_icon_slot_23288.setAttribute("name",app.components.x_table_cell.model.slot_sort_icon);

sort_icon_default_23289.setAttribute("part","sort-icon-default");

sort_icon_default_23289.setAttribute("aria-hidden","true");

(sort_icon_default_23289.innerHTML = app.components.x_table_cell.x_table_cell.svg_sort_neutral);

sort_icon_slot_23288.appendChild(sort_icon_default_23289);

sort_btn_23287.appendChild(sort_icon_slot_23288);

cell_23284.appendChild(content_23285);

cell_23284.appendChild(sort_btn_23287);

root_23282.appendChild(style_23283);

root_23282.appendChild(cell_23284);

app.components.x_table_cell.x_table_cell.goog$module$goog$object.set(el,app.components.x_table_cell.x_table_cell.k_refs,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"root","root",-448657453),root_23282,new cljs.core.Keyword(null,"cell","cell",764245084),cell_23284,new cljs.core.Keyword(null,"content","content",15833224),content_23285,new cljs.core.Keyword(null,"sort-btn","sort-btn",1913954560),sort_btn_23287,new cljs.core.Keyword(null,"sort-icon-default","sort-icon-default",-596209230),sort_icon_default_23289], null));

return null;
});
app.components.x_table_cell.x_table_cell.ensure_refs_BANG_ = (function app$components$x_table_cell$x_table_cell$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_table_cell.x_table_cell.init_dom_BANG_(el);

return app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_refs);
}
});
app.components.x_table_cell.x_table_cell.read_model = (function app$components$x_table_cell$x_table_cell$read_model(el){
return app.components.x_table_cell.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"align-raw","align-raw",-723387357),new cljs.core.Keyword(null,"row-span-raw","row-span-raw",-1130003707),new cljs.core.Keyword(null,"valign-raw","valign-raw",-1083375124),new cljs.core.Keyword(null,"sort-direction-raw","sort-direction-raw",-1822100883),new cljs.core.Keyword(null,"sortable?","sortable?",291547474),new cljs.core.Keyword(null,"truncate?","truncate?",135673334),new cljs.core.Keyword(null,"type-raw","type-raw",-967209994),new cljs.core.Keyword(null,"sticky-raw","sticky-raw",1857578519),new cljs.core.Keyword(null,"scope-raw","scope-raw",-773628648),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"col-span-raw","col-span-raw",1425293661)],[el.getAttribute(app.components.x_table_cell.model.attr_align),el.getAttribute(app.components.x_table_cell.model.attr_row_span),el.getAttribute(app.components.x_table_cell.model.attr_valign),el.getAttribute(app.components.x_table_cell.model.attr_sort_direction),el.hasAttribute(app.components.x_table_cell.model.attr_sortable),el.hasAttribute(app.components.x_table_cell.model.attr_truncate),el.getAttribute(app.components.x_table_cell.model.attr_type),el.getAttribute(app.components.x_table_cell.model.attr_sticky),el.getAttribute(app.components.x_table_cell.model.attr_scope),el.hasAttribute(app.components.x_table_cell.model.attr_disabled),el.getAttribute(app.components.x_table_cell.model.attr_col_span)]));
});
app.components.x_table_cell.x_table_cell.apply_span_BANG_ = (function app$components$x_table_cell$x_table_cell$apply_span_BANG_(el,col_span,row_span){
(el.style.gridColumn = (((col_span > (1)))?(""+"span "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(col_span)):""));

(el.style.gridRow = (((row_span > (1)))?(""+"span "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row_span)):""));

return null;
});
app.components.x_table_cell.x_table_cell.apply_host_attrs_BANG_ = (function app$components$x_table_cell$x_table_cell$apply_host_attrs_BANG_(el,p__23242){
var map__23245 = p__23242;
var map__23245__$1 = cljs.core.__destructure_map(map__23245);
var m = map__23245__$1;
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var align = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"align","align",1964212802));
var valign = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"valign","valign",1485197511));
var sticky = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"sticky","sticky",-2121213869));
var truncate_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"truncate?","truncate?",135673334));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var sortable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"sortable?","sortable?",291547474));
var sort_direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23245__$1,new cljs.core.Keyword(null,"sort-direction","sort-direction",-1635889628));
el.setAttribute("role",app.components.x_table_cell.model.role_for_cell(m));

var aria_sort_23300 = app.components.x_table_cell.model.aria_sort_value(m);
if(cljs.core.truth_(aria_sort_23300)){
el.setAttribute("aria-sort",aria_sort_23300);
} else {
el.removeAttribute("aria-sort");
}

if(cljs.core.truth_(disabled_QMARK_)){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

el.setAttribute("data-type",type);

el.setAttribute("data-align",align);

el.setAttribute("data-valign",valign);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(sticky,"none")){
el.removeAttribute("data-sticky");
} else {
el.setAttribute("data-sticky",sticky);
}

if(cljs.core.truth_(truncate_QMARK_)){
el.setAttribute("data-truncate","");
} else {
el.removeAttribute("data-truncate");
}

return null;
});
app.components.x_table_cell.x_table_cell.sort_icon_svg = (function app$components$x_table_cell$x_table_cell$sort_icon_svg(p__23256){
var map__23257 = p__23256;
var map__23257__$1 = cljs.core.__destructure_map(map__23257);
var sort_direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23257__$1,new cljs.core.Keyword(null,"sort-direction","sort-direction",-1635889628));
var G__23258 = sort_direction;
switch (G__23258) {
case "asc":
return app.components.x_table_cell.x_table_cell.svg_sort_asc;

break;
case "desc":
return app.components.x_table_cell.x_table_cell.svg_sort_desc;

break;
default:
return app.components.x_table_cell.x_table_cell.svg_sort_neutral;

}
});
app.components.x_table_cell.x_table_cell.apply_model_BANG_ = (function app$components$x_table_cell$x_table_cell$apply_model_BANG_(el,p__23259){
var map__23260 = p__23259;
var map__23260__$1 = cljs.core.__destructure_map(map__23260);
var m = map__23260__$1;
var col_span = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23260__$1,new cljs.core.Keyword(null,"col-span","col-span",-232603210));
var row_span = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23260__$1,new cljs.core.Keyword(null,"row-span","row-span",-365554241));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23260__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var sort_direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23260__$1,new cljs.core.Keyword(null,"sort-direction","sort-direction",-1635889628));
var map__23261_23305 = app.components.x_table_cell.x_table_cell.ensure_refs_BANG_(el);
var map__23261_23306__$1 = cljs.core.__destructure_map(map__23261_23305);
var sort_btn_23307 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23261_23306__$1,new cljs.core.Keyword(null,"sort-btn","sort-btn",1913954560));
var sort_icon_default_23308 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23261_23306__$1,new cljs.core.Keyword(null,"sort-icon-default","sort-icon-default",-596209230));
var sort_btn_23309__$1 = sort_btn_23307;
var sort_icon_default_23310__$1 = sort_icon_default_23308;
var visible_QMARK__23311 = app.components.x_table_cell.model.sort_btn_visible_QMARK_(m);
app.components.x_table_cell.x_table_cell.apply_host_attrs_BANG_(el,m);

app.components.x_table_cell.x_table_cell.apply_span_BANG_(el,col_span,row_span);

(sort_btn_23309__$1.hidden = cljs.core.not(visible_QMARK__23311));

(sort_btn_23309__$1.tabIndex = parseInt(app.components.x_table_cell.model.sort_btn_tabindex(m),(10)));

sort_btn_23309__$1.setAttribute("aria-label",app.components.x_table_cell.model.sort_btn_aria_label(sort_direction));

(sort_btn_23309__$1.disabled = cljs.core.boolean$(disabled_QMARK_));

(sort_icon_default_23310__$1.innerHTML = app.components.x_table_cell.x_table_cell.sort_icon_svg(m));

app.components.x_table_cell.x_table_cell.goog$module$goog$object.set(el,app.components.x_table_cell.x_table_cell.k_model,m);

return null;
});
app.components.x_table_cell.x_table_cell.update_from_attrs_BANG_ = (function app$components$x_table_cell$x_table_cell$update_from_attrs_BANG_(el){
var new_m_23317 = app.components.x_table_cell.x_table_cell.read_model(el);
var old_m_23318 = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23318,new_m_23317)){
app.components.x_table_cell.x_table_cell.apply_model_BANG_(el,new_m_23317);
} else {
}

return null;
});
app.components.x_table_cell.x_table_cell.dispatch_sort_BANG_ = (function app$components$x_table_cell$x_table_cell$dispatch_sort_BANG_(el){
var current_23323 = app.components.x_table_cell.model.parse_sort_direction(el.getAttribute(app.components.x_table_cell.model.attr_sort_direction));
var next_dir_23324 = app.components.x_table_cell.model.next_sort_direction(current_23323);
var ev_23325 = (new CustomEvent(app.components.x_table_cell.model.event_sort,({"detail": ({"direction": next_dir_23324, "previousDirection": current_23323}), "bubbles": true, "composed": true, "cancelable": true})));
el.dispatchEvent(ev_23325);

return null;
});
app.components.x_table_cell.x_table_cell.dispatch_connected_BANG_ = (function app$components$x_table_cell$x_table_cell$dispatch_connected_BANG_(el,m){
el.dispatchEvent((new CustomEvent(app.components.x_table_cell.model.event_connected,({"detail": cljs.core.clj__GT_js(app.components.x_table_cell.model.connected_detail(m)), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_table_cell.x_table_cell.dispatch_disconnected_BANG_ = (function app$components$x_table_cell$x_table_cell$dispatch_disconnected_BANG_(el){
el.dispatchEvent((new CustomEvent(app.components.x_table_cell.model.event_disconnected,({"detail": ({}), "bubbles": true, "composed": true, "cancelable": false}))));

return null;
});
app.components.x_table_cell.x_table_cell.on_sort_click = (function app$components$x_table_cell$x_table_cell$on_sort_click(el,_e){
var m_23326 = (function (){var or__5142__auto__ = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_cell.x_table_cell.read_model(el);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = app.components.x_table_cell.model.sort_btn_visible_QMARK_(m_23326);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(m_23326));
} else {
return and__5140__auto__;
}
})())){
app.components.x_table_cell.x_table_cell.dispatch_sort_BANG_(el);
} else {
}

return null;
});
app.components.x_table_cell.x_table_cell.add_listeners_BANG_ = (function app$components$x_table_cell$x_table_cell$add_listeners_BANG_(el){
var map__23270_23329 = app.components.x_table_cell.x_table_cell.ensure_refs_BANG_(el);
var map__23270_23330__$1 = cljs.core.__destructure_map(map__23270_23329);
var sort_btn_23331 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23270_23330__$1,new cljs.core.Keyword(null,"sort-btn","sort-btn",1913954560));
var sort_btn_23332__$1 = sort_btn_23331;
var sort_click_h_23333 = (function (e){
return app.components.x_table_cell.x_table_cell.on_sort_click(el,e);
});
sort_btn_23332__$1.addEventListener("click",sort_click_h_23333);

app.components.x_table_cell.x_table_cell.goog$module$goog$object.set(el,app.components.x_table_cell.x_table_cell.k_handlers,({"sort-click": sort_click_h_23333}));

return null;
});
app.components.x_table_cell.x_table_cell.remove_listeners_BANG_ = (function app$components$x_table_cell$x_table_cell$remove_listeners_BANG_(el){
var hs_23335 = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_handlers);
var refs_23336 = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(el,app.components.x_table_cell.x_table_cell.k_refs);
if(cljs.core.truth_((function (){var and__5140__auto__ = hs_23335;
if(cljs.core.truth_(and__5140__auto__)){
return refs_23336;
} else {
return and__5140__auto__;
}
})())){
var map__23272_23338 = refs_23336;
var map__23272_23339__$1 = cljs.core.__destructure_map(map__23272_23338);
var sort_btn_23340 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23272_23339__$1,new cljs.core.Keyword(null,"sort-btn","sort-btn",1913954560));
var sort_btn_23341__$1 = sort_btn_23340;
var h_23342 = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(hs_23335,"sort-click");
if(cljs.core.truth_(h_23342)){
sort_btn_23341__$1.removeEventListener("click",h_23342);
} else {
}
} else {
}

app.components.x_table_cell.x_table_cell.goog$module$goog$object.set(el,app.components.x_table_cell.x_table_cell.k_handlers,null);

return null;
});
app.components.x_table_cell.x_table_cell.install_property_accessors_BANG_ = (function app$components$x_table_cell$x_table_cell$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_table_cell.model.attr_type,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_type);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "data";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_type,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_type);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_scope,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_scope);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "col";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_scope,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_scope);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_align,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_align);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "start";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_align,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_align);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_valign,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_valign);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "middle";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_valign,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_valign);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_truncate,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_cell.model.attr_truncate);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_truncate,"");
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_truncate);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_sticky,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_sticky);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "none";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_sticky,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_sticky);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_sortable,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_cell.model.attr_sortable);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_sortable,"");
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_sortable);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table_cell.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table_cell.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"colSpan",({"get": (function (){
var this$ = this;
return app.components.x_table_cell.model.parse_span(this$.getAttribute(app.components.x_table_cell.model.attr_col_span));
}), "set": (function (v){
var this$ = this;
var n = parseInt(v,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return this$.setAttribute(app.components.x_table_cell.model.attr_col_span,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_col_span);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"rowSpan",({"get": (function (){
var this$ = this;
return app.components.x_table_cell.model.parse_span(this$.getAttribute(app.components.x_table_cell.model.attr_row_span));
}), "set": (function (v){
var this$ = this;
var n = parseInt(v,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return this$.setAttribute(app.components.x_table_cell.model.attr_row_span,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_row_span);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"sortDirection",({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table_cell.model.attr_sort_direction);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "none";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table_cell.model.attr_sort_direction,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table_cell.model.attr_sort_direction);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_table_cell.x_table_cell.element_class = (function app$components$x_table_cell$x_table_cell$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_table_cell.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_table_cell.x_table_cell.ensure_refs_BANG_(this$);

app.components.x_table_cell.x_table_cell.remove_listeners_BANG_(this$);

app.components.x_table_cell.x_table_cell.add_listeners_BANG_(this$);

app.components.x_table_cell.x_table_cell.update_from_attrs_BANG_(this$);

var m_23362 = (function (){var or__5142__auto__ = app.components.x_table_cell.x_table_cell.goog$module$goog$object.get(this$,app.components.x_table_cell.x_table_cell.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table_cell.x_table_cell.read_model(this$);
}
})();
app.components.x_table_cell.x_table_cell.dispatch_connected_BANG_(this$,m_23362);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_table_cell.x_table_cell.remove_listeners_BANG_(this$);

app.components.x_table_cell.x_table_cell.dispatch_disconnected_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_table_cell.x_table_cell.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_table_cell.x_table_cell.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_table_cell.x_table_cell.register_BANG_ = (function app$components$x_table_cell$x_table_cell$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_table_cell.model.tag_name))){
} else {
customElements.define(app.components.x_table_cell.model.tag_name,app.components.x_table_cell.x_table_cell.element_class());
}

return null;
});
app.components.x_table_cell.x_table_cell.init_BANG_ = (function app$components$x_table_cell$x_table_cell$init_BANG_(){
return app.components.x_table_cell.x_table_cell.register_BANG_();
});

//# sourceMappingURL=app.components.x_table_cell.x_table_cell.js.map
