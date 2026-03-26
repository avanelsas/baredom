goog.provide('app.components.x_table.x_table');
goog.scope(function(){
  app.components.x_table.x_table.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_table.x_table.k_refs = "__xTableRefs";
app.components.x_table.x_table.k_model = "__xTableModel";
app.components.x_table.x_table.k_handlers = "__xTableHandlers";
app.components.x_table.x_table.style_text = (""+":host{"+"display:grid;"+"box-sizing:border-box;"+"color-scheme:light dark;"+"--x-table-border-color:rgba(0,0,0,0.1);"+"--x-table-border-radius:8px;"+"--x-table-stripe-bg:rgba(0,0,0,0.025);"+"--x-table-caption-color:inherit;"+"--x-table-caption-font-size:0.875rem;"+"--x-table-caption-font-weight:600;"+"--x-table-caption-padding:0 0 0.5rem;"+"--x-table-compact-padding:0.25rem 0.5rem;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-table-border-color:rgba(255,255,255,0.1);"+"--x-table-stripe-bg:rgba(255,255,255,0.03);}}"+":host([data-bordered]){"+"border:1px solid var(--x-table-border-color);"+"border-radius:var(--x-table-border-radius);"+"overflow:hidden;}"+":host([data-full-width]){"+"width:100%;}"+":host([data-compact]){"+"--x-table-cell-padding:var(--x-table-compact-padding);}"+":host([data-striped]) ::slotted(x-table-row[data-stripe='even']){"+"--x-table-row-bg:var(--x-table-stripe-bg);}"+"[part=caption]{"+"grid-column:1 / -1;"+"color:var(--x-table-caption-color);"+"font-size:var(--x-table-caption-font-size);"+"font-weight:var(--x-table-caption-font-weight);"+"padding:var(--x-table-caption-padding);"+"box-sizing:border-box;}"+"[part=caption][hidden]{"+"display:none !important;}"+"slot{display:contents;}");
app.components.x_table.x_table.init_dom_BANG_ = (function app$components$x_table$x_table$init_dom_BANG_(el){
var root_23318 = el.attachShadow(({"mode": "open"}));
var style_23319 = document.createElement("style");
var caption_div_23320 = document.createElement("div");
var slot_el_23321 = document.createElement("slot");
(style_23319.textContent = app.components.x_table.x_table.style_text);

caption_div_23320.setAttribute("part","caption");

caption_div_23320.setAttribute("hidden","");

root_23318.appendChild(style_23319);

root_23318.appendChild(caption_div_23320);

root_23318.appendChild(slot_el_23321);

app.components.x_table.x_table.goog$module$goog$object.set(el,app.components.x_table.x_table.k_refs,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"root","root",-448657453),root_23318,new cljs.core.Keyword(null,"caption-div","caption-div",1485363333),caption_div_23320,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_23321], null));

return null;
});
app.components.x_table.x_table.ensure_refs_BANG_ = (function app$components$x_table$x_table$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_table.x_table.init_dom_BANG_(el);

return app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_refs);
}
});
app.components.x_table.x_table.read_model = (function app$components$x_table$x_table$read_model(el){
return app.components.x_table.model.normalize(new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"columns-raw","columns-raw",-2093994046),el.getAttribute(app.components.x_table.model.attr_columns),new cljs.core.Keyword(null,"caption-raw","caption-raw",-375935083),el.getAttribute(app.components.x_table.model.attr_caption),new cljs.core.Keyword(null,"selectable-raw","selectable-raw",-1276314763),el.getAttribute(app.components.x_table.model.attr_selectable),new cljs.core.Keyword(null,"striped?","striped?",-797214979),el.hasAttribute(app.components.x_table.model.attr_striped),new cljs.core.Keyword(null,"bordered?","bordered?",562358476),el.hasAttribute(app.components.x_table.model.attr_bordered),new cljs.core.Keyword(null,"full-width?","full-width?",1477288349),el.hasAttribute(app.components.x_table.model.attr_full_width),new cljs.core.Keyword(null,"compact?","compact?",1216893298),el.hasAttribute(app.components.x_table.model.attr_compact),new cljs.core.Keyword(null,"row-count-raw","row-count-raw",-1432781274),el.getAttribute(app.components.x_table.model.attr_row_count)], null));
});
app.components.x_table.x_table.update_stripe_attrs_BANG_ = (function app$components$x_table$x_table$update_stripe_attrs_BANG_(el){
var rows_23325 = el.querySelectorAll("x-table-row");
var len_23326 = rows_23325.length;
var i_23327 = (0);
while(true){
if((i_23327 < len_23326)){
var row_23328 = (rows_23325[i_23327]);
if((cljs.core.mod((i_23327 + (1)),(2)) === (0))){
row_23328.setAttribute("data-stripe","even");
} else {
row_23328.removeAttribute("data-stripe");
}

var G__23329 = (i_23327 + (1));
i_23327 = G__23329;
continue;
} else {
}
break;
}

return null;
});
app.components.x_table.x_table.apply_model_BANG_ = (function app$components$x_table$x_table$apply_model_BANG_(el,p__23272){
var map__23273 = p__23272;
var map__23273__$1 = cljs.core.__destructure_map(map__23273);
var m = map__23273__$1;
var columns = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"columns","columns",1998437288));
var caption = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"caption","caption",-855383902));
var selectable = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"selectable","selectable",370587038));
var striped_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"striped?","striped?",-797214979));
var bordered_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"bordered?","bordered?",562358476));
var full_width_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"full-width?","full-width?",1477288349));
var compact_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"compact?","compact?",1216893298));
var row_count = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23273__$1,new cljs.core.Keyword(null,"row-count","row-count",1060167988));
var map__23274_23330 = app.components.x_table.x_table.ensure_refs_BANG_(el);
var map__23274_23331__$1 = cljs.core.__destructure_map(map__23274_23330);
var caption_div_23332 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23274_23331__$1,new cljs.core.Keyword(null,"caption-div","caption-div",1485363333));
var caption_div_23333__$1 = caption_div_23332;
if(cljs.core.truth_(columns)){
(el.style.gridTemplateColumns = columns);
} else {
(el.style.gridTemplateColumns = "");
}

el.setAttribute("role",app.components.x_table.model.role_for_selectable(selectable));

var ms_23334 = app.components.x_table.model.aria_multiselectable(selectable);
if(cljs.core.truth_(ms_23334)){
el.setAttribute("aria-multiselectable",ms_23334);
} else {
el.removeAttribute("aria-multiselectable");
}

if((!((row_count == null)))){
el.setAttribute("aria-rowcount",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row_count)));
} else {
el.removeAttribute("aria-rowcount");
}

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(caption,"")){
el.setAttribute("aria-label",caption);
} else {
el.removeAttribute("aria-label");
}

if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(caption,"")){
(caption_div_23333__$1.textContent = caption);

caption_div_23333__$1.removeAttribute("hidden");
} else {
(caption_div_23333__$1.textContent = "");

caption_div_23333__$1.setAttribute("hidden","");
}

if(cljs.core.truth_(striped_QMARK_)){
el.setAttribute("data-striped","");
} else {
el.removeAttribute("data-striped");
}

if(cljs.core.truth_(bordered_QMARK_)){
el.setAttribute("data-bordered","");
} else {
el.removeAttribute("data-bordered");
}

if(cljs.core.truth_(full_width_QMARK_)){
el.setAttribute("data-full-width","");
} else {
el.removeAttribute("data-full-width");
}

if(cljs.core.truth_(compact_QMARK_)){
el.setAttribute("data-compact","");
} else {
el.removeAttribute("data-compact");
}

if(cljs.core.truth_(striped_QMARK_)){
app.components.x_table.x_table.update_stripe_attrs_BANG_(el);
} else {
}

app.components.x_table.x_table.goog$module$goog$object.set(el,app.components.x_table.x_table.k_model,m);

return null;
});
app.components.x_table.x_table.update_from_attrs_BANG_ = (function app$components$x_table$x_table$update_from_attrs_BANG_(el){
var new_m_23337 = app.components.x_table.x_table.read_model(el);
var old_m_23338 = app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_23338,new_m_23337)){
app.components.x_table.x_table.apply_model_BANG_(el,new_m_23337);
} else {
}

return null;
});
app.components.x_table.x_table.col_index_of = (function app$components$x_table$x_table$col_index_of(cell){
var row = cell.parentElement;
var cells = row.querySelectorAll("x-table-cell");
return Array.from(cells).indexOf(cell);
});
app.components.x_table.x_table.dispatch_sort_BANG_ = (function app$components$x_table$x_table$dispatch_sort_BANG_(el,col_index,direction,previous_direction){
var detail_23339 = cljs.core.clj__GT_js(app.components.x_table.model.sort_detail(col_index,direction,previous_direction));
el.dispatchEvent((new CustomEvent(app.components.x_table.model.event_sort,({"detail": detail_23339, "bubbles": true, "composed": true, "cancelable": true}))));

return null;
});
app.components.x_table.x_table.dispatch_row_select_BANG_ = (function app$components$x_table$x_table$dispatch_row_select_BANG_(el,row_index,selected_QMARK_,selectable){
var detail_23342 = cljs.core.clj__GT_js(app.components.x_table.model.row_select_detail(row_index,selected_QMARK_,selectable));
el.dispatchEvent((new CustomEvent(app.components.x_table.model.event_row_select,({"detail": detail_23342, "bubbles": true, "composed": true, "cancelable": true}))));

return null;
});
app.components.x_table.x_table.handle_single_select_BANG_ = (function app$components$x_table$x_table$handle_single_select_BANG_(el,target_row){
var selected_rows_23343 = el.querySelectorAll("x-table-row[selected]");
var len_23344 = selected_rows_23343.length;
var i_23345 = (0);
while(true){
if((i_23345 < len_23344)){
(selected_rows_23343[i_23345]).removeAttribute("selected");

var G__23346 = (i_23345 + (1));
i_23345 = G__23346;
continue;
} else {
}
break;
}

target_row.setAttribute("selected","");

return null;
});
app.components.x_table.x_table.handle_multi_select_BANG_ = (function app$components$x_table$x_table$handle_multi_select_BANG_(_el,target_row){
if(cljs.core.truth_(target_row.hasAttribute("selected"))){
target_row.removeAttribute("selected");
} else {
target_row.setAttribute("selected","");
}

return null;
});
app.components.x_table.x_table.on_cell_sort = (function app$components$x_table$x_table$on_cell_sort(el,e){
var cell_23347 = e.target;
var col_index_23348 = app.components.x_table.x_table.col_index_of(cell_23347);
var direction_23349 = app.components.x_table.x_table.goog$module$goog$object.get(e.detail,"direction");
var prev_dir_23350 = app.components.x_table.x_table.goog$module$goog$object.get(e.detail,"previousDirection");
if((col_index_23348 >= (0))){
e.stopPropagation();

app.components.x_table.x_table.dispatch_sort_BANG_(el,col_index_23348,direction_23349,prev_dir_23350);
} else {
}

return null;
});
app.components.x_table.x_table.on_row_click = (function app$components$x_table$x_table$on_row_click(el,e){
var m_23351 = (function (){var or__5142__auto__ = app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table.x_table.read_model(el);
}
})();
var selectable_23352 = new cljs.core.Keyword(null,"selectable","selectable",370587038).cljs$core$IFn$_invoke$arity$1(m_23351);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(selectable_23352,"none")){
var row_23353 = e.target;
var row_index_23354 = app.components.x_table.x_table.goog$module$goog$object.get(e.detail,"rowIndex");
var currently_QMARK__23355 = row_23353.hasAttribute("selected");
var will_select_QMARK__23356 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(selectable_23352,"single"))?cljs.core.not(currently_QMARK__23355):cljs.core.not(currently_QMARK__23355));
app.components.x_table.x_table.dispatch_row_select_BANG_(el,row_index_23354,will_select_QMARK__23356,selectable_23352);

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(selectable_23352,"single")){
app.components.x_table.x_table.handle_single_select_BANG_(el,row_23353);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(selectable_23352,"multi")){
app.components.x_table.x_table.handle_multi_select_BANG_(el,row_23353);
} else {
}
}
} else {
}

return null;
});
app.components.x_table.x_table.on_row_connected = (function app$components$x_table$x_table$on_row_connected(el,_e){
var m_23357 = (function (){var or__5142__auto__ = app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_model);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_table.x_table.read_model(el);
}
})();
if(cljs.core.truth_(new cljs.core.Keyword(null,"striped?","striped?",-797214979).cljs$core$IFn$_invoke$arity$1(m_23357))){
app.components.x_table.x_table.update_stripe_attrs_BANG_(el);
} else {
}

return null;
});
app.components.x_table.x_table.add_listeners_BANG_ = (function app$components$x_table$x_table$add_listeners_BANG_(el){
var sort_h_23358 = (function (e){
return app.components.x_table.x_table.on_cell_sort(el,e);
});
var row_click_h_23359 = (function (e){
return app.components.x_table.x_table.on_row_click(el,e);
});
var row_conn_h_23360 = (function (e){
return app.components.x_table.x_table.on_row_connected(el,e);
});
el.addEventListener("x-table-cell-sort",sort_h_23358);

el.addEventListener("x-table-row-click",row_click_h_23359);

el.addEventListener("x-table-row-connected",row_conn_h_23360);

app.components.x_table.x_table.goog$module$goog$object.set(el,app.components.x_table.x_table.k_handlers,({"sort": sort_h_23358, "row-click": row_click_h_23359, "row-conn": row_conn_h_23360}));

return null;
});
app.components.x_table.x_table.remove_listeners_BANG_ = (function app$components$x_table$x_table$remove_listeners_BANG_(el){
var hs_23361 = app.components.x_table.x_table.goog$module$goog$object.get(el,app.components.x_table.x_table.k_handlers);
if(cljs.core.truth_(hs_23361)){
var sort_h_23362 = app.components.x_table.x_table.goog$module$goog$object.get(hs_23361,"sort");
var row_click_h_23363 = app.components.x_table.x_table.goog$module$goog$object.get(hs_23361,"row-click");
var row_conn_h_23364 = app.components.x_table.x_table.goog$module$goog$object.get(hs_23361,"row-conn");
if(cljs.core.truth_(sort_h_23362)){
el.removeEventListener("x-table-cell-sort",sort_h_23362);
} else {
}

if(cljs.core.truth_(row_click_h_23363)){
el.removeEventListener("x-table-row-click",row_click_h_23363);
} else {
}

if(cljs.core.truth_(row_conn_h_23364)){
el.removeEventListener("x-table-row-connected",row_conn_h_23364);
} else {
}
} else {
}

app.components.x_table.x_table.goog$module$goog$object.set(el,app.components.x_table.x_table.k_handlers,null);

return null;
});
app.components.x_table.x_table.install_property_accessors_BANG_ = (function app$components$x_table$x_table$install_property_accessors_BANG_(proto){
Object.defineProperty(proto,app.components.x_table.model.attr_columns,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table.model.attr_columns);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_columns,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table.model.attr_columns);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table.model.attr_caption,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table.model.attr_caption);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_caption,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table.model.attr_caption);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table.model.attr_selectable,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(app.components.x_table.model.attr_selectable);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "none";
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_selectable,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(app.components.x_table.model.attr_selectable);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table.model.attr_striped,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table.model.attr_striped);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_striped,"");
} else {
return this$.removeAttribute(app.components.x_table.model.attr_striped);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table.model.attr_bordered,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table.model.attr_bordered);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_bordered,"");
} else {
return this$.removeAttribute(app.components.x_table.model.attr_bordered);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,app.components.x_table.model.attr_compact,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table.model.attr_compact);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_compact,"");
} else {
return this$.removeAttribute(app.components.x_table.model.attr_compact);
}
}), "enumerable": true, "configurable": true}));

Object.defineProperty(proto,"fullWidth",({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_table.model.attr_full_width);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_table.model.attr_full_width,"");
} else {
return this$.removeAttribute(app.components.x_table.model.attr_full_width);
}
}), "enumerable": true, "configurable": true}));

return Object.defineProperty(proto,"rowCount",({"get": (function (){
var this$ = this;
return app.components.x_table.model.parse_row_count(this$.getAttribute(app.components.x_table.model.attr_row_count));
}), "set": (function (v){
var this$ = this;
if((v == null)){
return this$.removeAttribute(app.components.x_table.model.attr_row_count);
} else {
return this$.setAttribute(app.components.x_table.model.attr_row_count,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.floor(v))));
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_table.x_table.element_class = (function app$components$x_table$x_table$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_table.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_table.x_table.ensure_refs_BANG_(this$);

app.components.x_table.x_table.remove_listeners_BANG_(this$);

app.components.x_table.x_table.add_listeners_BANG_(this$);

app.components.x_table.x_table.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_table.x_table.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_table.x_table.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_table.x_table.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_table.x_table.register_BANG_ = (function app$components$x_table$x_table$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_table.model.tag_name))){
} else {
customElements.define(app.components.x_table.model.tag_name,app.components.x_table.x_table.element_class());
}

return null;
});
app.components.x_table.x_table.init_BANG_ = (function app$components$x_table$x_table$init_BANG_(){
return app.components.x_table.x_table.register_BANG_();
});

//# sourceMappingURL=app.components.x_table.x_table.js.map
