goog.provide('app.components.x_table_cell.model');
app.components.x_table_cell.model.tag_name = "x-table-cell";
app.components.x_table_cell.model.attr_type = "type";
app.components.x_table_cell.model.attr_scope = "scope";
app.components.x_table_cell.model.attr_align = "align";
app.components.x_table_cell.model.attr_valign = "valign";
app.components.x_table_cell.model.attr_col_span = "col-span";
app.components.x_table_cell.model.attr_row_span = "row-span";
app.components.x_table_cell.model.attr_truncate = "truncate";
app.components.x_table_cell.model.attr_sticky = "sticky";
app.components.x_table_cell.model.attr_sortable = "sortable";
app.components.x_table_cell.model.attr_sort_direction = "sort-direction";
app.components.x_table_cell.model.attr_disabled = "disabled";
app.components.x_table_cell.model.observed_attributes = [app.components.x_table_cell.model.attr_type,app.components.x_table_cell.model.attr_scope,app.components.x_table_cell.model.attr_align,app.components.x_table_cell.model.attr_valign,app.components.x_table_cell.model.attr_col_span,app.components.x_table_cell.model.attr_row_span,app.components.x_table_cell.model.attr_truncate,app.components.x_table_cell.model.attr_sticky,app.components.x_table_cell.model.attr_sortable,app.components.x_table_cell.model.attr_sort_direction,app.components.x_table_cell.model.attr_disabled];
app.components.x_table_cell.model.event_sort = "x-table-cell-sort";
app.components.x_table_cell.model.event_connected = "x-table-cell-connected";
app.components.x_table_cell.model.event_disconnected = "x-table-cell-disconnected";
app.components.x_table_cell.model.slot_sort_icon = "sort-icon";
app.components.x_table_cell.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Keyword(null,"rowSpan","rowSpan",826884002),new cljs.core.Keyword(null,"truncate","truncate",-1327322939),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"valign","valign",1485197511),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"scope","scope",-439358418),new cljs.core.Keyword(null,"colSpan","colSpan",872137394),new cljs.core.Keyword(null,"sticky","sticky",-2121213869),new cljs.core.Keyword(null,"sortable","sortable",2109633621),new cljs.core.Keyword(null,"sortDirection","sortDirection",-45816999)],[new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)]);
app.components.x_table_cell.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_table_cell.model.event_sort,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"direction","direction",-633359395),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"previousDirection","previousDirection",900518581),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null),app.components.x_table_cell.model.event_connected,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"scope","scope",-439358418),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"colSpan","colSpan",872137394),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"rowSpan","rowSpan",826884002),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_table_cell.model.event_disconnected,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null)]);
app.components.x_table_cell.model.type_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["data",null,"header",null], null), null);
app.components.x_table_cell.model.scope_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["rowgroup",null,"col",null,"row",null,"colgroup",null], null), null);
app.components.x_table_cell.model.align_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["center",null,"start",null,"end",null], null), null);
app.components.x_table_cell.model.valign_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["top",null,"middle",null,"bottom",null], null), null);
app.components.x_table_cell.model.sticky_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["none",null,"start",null,"end",null], null), null);
app.components.x_table_cell.model.sort_dir_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["none",null,"desc",null,"asc",null], null), null);
/**
 * Return s (lower-cased) if it is in allowed, otherwise fallback.
 */
app.components.x_table_cell.model.normalize_enum = (function app$components$x_table_cell$model$normalize_enum(s,allowed,fallback){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
/**
 * Normalise type attribute. Unknown/nil → "data".
 */
app.components.x_table_cell.model.parse_type = (function app$components$x_table_cell$model$parse_type(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.type_values,"data");
});
/**
 * Normalise scope attribute. Unknown/nil → "col".
 */
app.components.x_table_cell.model.parse_scope = (function app$components$x_table_cell$model$parse_scope(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.scope_values,"col");
});
/**
 * Normalise align attribute. Unknown/nil → "start".
 */
app.components.x_table_cell.model.parse_align = (function app$components$x_table_cell$model$parse_align(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.align_values,"start");
});
/**
 * Normalise valign attribute. Unknown/nil → "middle".
 */
app.components.x_table_cell.model.parse_valign = (function app$components$x_table_cell$model$parse_valign(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.valign_values,"middle");
});
/**
 * Parse a col-span or row-span attribute to a positive integer. Invalid/nil → 1.
 */
app.components.x_table_cell.model.parse_span = (function app$components$x_table_cell$model$parse_span(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return Math.floor(n);
} else {
return (1);
}
} else {
return (1);
}
});
/**
 * Normalise sticky attribute. Unknown/nil → "none".
 */
app.components.x_table_cell.model.parse_sticky = (function app$components$x_table_cell$model$parse_sticky(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.sticky_values,"none");
});
/**
 * Normalise sort-direction attribute. Unknown/nil → "none".
 */
app.components.x_table_cell.model.parse_sort_direction = (function app$components$x_table_cell$model$parse_sort_direction(s){
return app.components.x_table_cell.model.normalize_enum(s,app.components.x_table_cell.model.sort_dir_values,"none");
});
/**
 * Return the ARIA role string for the host element based on type and scope.
 */
app.components.x_table_cell.model.role_for_cell = (function app$components$x_table_cell$model$role_for_cell(p__23130){
var map__23131 = p__23130;
var map__23131__$1 = cljs.core.__destructure_map(map__23131);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23131__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var scope = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23131__$1,new cljs.core.Keyword(null,"scope","scope",-439358418));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(type,"header")){
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(scope,"row")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(scope,"rowgroup")))){
return "rowheader";
} else {
return "columnheader";
}
} else {
return "cell";
}
});
/**
 * Return aria-sort string or nil if not applicable.
 *   Set only when sortable is true or sort-direction is not "none".
 */
app.components.x_table_cell.model.aria_sort_value = (function app$components$x_table_cell$model$aria_sort_value(p__23133){
var map__23134 = p__23133;
var map__23134__$1 = cljs.core.__destructure_map(map__23134);
var sortable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23134__$1,new cljs.core.Keyword(null,"sortable?","sortable?",291547474));
var sort_direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23134__$1,new cljs.core.Keyword(null,"sort-direction","sort-direction",-1635889628));
if(cljs.core.truth_((function (){var or__5142__auto__ = sortable_QMARK_;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(sort_direction,"none");
}
})())){
var G__23135 = sort_direction;
switch (G__23135) {
case "asc":
return "ascending";

break;
case "desc":
return "descending";

break;
default:
return "none";

}
} else {
return null;
}
});
/**
 * Sort button is visible only for header cells with sortable=true.
 */
app.components.x_table_cell.model.sort_btn_visible_QMARK_ = (function app$components$x_table_cell$model$sort_btn_visible_QMARK_(p__23137){
var map__23138 = p__23137;
var map__23138__$1 = cljs.core.__destructure_map(map__23138);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23138__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var sortable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23138__$1,new cljs.core.Keyword(null,"sortable?","sortable?",291547474));
var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(type,"header");
if(and__5140__auto__){
return sortable_QMARK_;
} else {
return and__5140__auto__;
}
});
/**
 * Returns "0" when the sort button should be keyboard-focusable, else "-1".
 */
app.components.x_table_cell.model.sort_btn_tabindex = (function app$components$x_table_cell$model$sort_btn_tabindex(p__23140){
var map__23141 = p__23140;
var map__23141__$1 = cljs.core.__destructure_map(map__23141);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23141__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var sortable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23141__$1,new cljs.core.Keyword(null,"sortable?","sortable?",291547474));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23141__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(type,"header");
if(and__5140__auto__){
var and__5140__auto____$1 = sortable_QMARK_;
if(cljs.core.truth_(and__5140__auto____$1)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return "0";
} else {
return "-1";
}
});
/**
 * Cycle sort direction: none → asc → desc → none.
 */
app.components.x_table_cell.model.next_sort_direction = (function app$components$x_table_cell$model$next_sort_direction(current){
var G__23142 = current;
switch (G__23142) {
case "none":
return "asc";

break;
case "asc":
return "desc";

break;
case "desc":
return "none";

break;
default:
return "asc";

}
});
/**
 * Return the aria-label for the sort button describing the action that will occur.
 */
app.components.x_table_cell.model.sort_btn_aria_label = (function app$components$x_table_cell$model$sort_btn_aria_label(current_direction){
var G__23143 = current_direction;
switch (G__23143) {
case "none":
return "Sort ascending";

break;
case "asc":
return "Sort descending";

break;
case "desc":
return "Remove sort";

break;
default:
return "Sort ascending";

}
});
/**
 * Produce a canonical view-model map from raw attribute inputs.
 * 
 *   Input keys:
 *  :type-raw           string | nil
 *  :scope-raw          string | nil
 *  :align-raw          string | nil
 *  :valign-raw         string | nil
 *  :col-span-raw       string | nil
 *  :row-span-raw       string | nil
 *  :truncate?          boolean
 *  :sticky-raw         string | nil
 *  :sortable?          boolean
 *  :sort-direction-raw string | nil
 *  :disabled?          boolean
 * 
 *   Output keys mirror the attribute semantics with parsed/normalised values.
 */
app.components.x_table_cell.model.normalize = (function app$components$x_table_cell$model$normalize(p__23145){
var map__23147 = p__23145;
var map__23147__$1 = cljs.core.__destructure_map(map__23147);
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var col_span_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"col-span-raw","col-span-raw",1425293661));
var align_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"align-raw","align-raw",-723387357));
var row_span_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"row-span-raw","row-span-raw",-1130003707));
var valign_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"valign-raw","valign-raw",-1083375124));
var sort_direction_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"sort-direction-raw","sort-direction-raw",-1822100883));
var sortable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"sortable?","sortable?",291547474));
var truncate_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"truncate?","truncate?",135673334));
var type_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"type-raw","type-raw",-967209994));
var sticky_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"sticky-raw","sticky-raw",1857578519));
var scope_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23147__$1,new cljs.core.Keyword(null,"scope-raw","scope-raw",-773628648));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Keyword(null,"sort-direction","sort-direction",-1635889628),new cljs.core.Keyword(null,"valign","valign",1485197511),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"scope","scope",-439358418),new cljs.core.Keyword(null,"sortable?","sortable?",291547474),new cljs.core.Keyword(null,"sticky","sticky",-2121213869),new cljs.core.Keyword(null,"col-span","col-span",-232603210),new cljs.core.Keyword(null,"truncate?","truncate?",135673334),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"row-span","row-span",-365554241)],[app.components.x_table_cell.model.parse_align(align_raw),app.components.x_table_cell.model.parse_sort_direction(sort_direction_raw),app.components.x_table_cell.model.parse_valign(valign_raw),app.components.x_table_cell.model.parse_type(type_raw),app.components.x_table_cell.model.parse_scope(scope_raw),cljs.core.boolean$(sortable_QMARK_),app.components.x_table_cell.model.parse_sticky(sticky_raw),app.components.x_table_cell.model.parse_span(col_span_raw),cljs.core.boolean$(truncate_QMARK_),cljs.core.boolean$(disabled_QMARK_),app.components.x_table_cell.model.parse_span(row_span_raw)]);
});
/**
 * Build the event detail map for x-table-cell-connected.
 */
app.components.x_table_cell.model.connected_detail = (function app$components$x_table_cell$model$connected_detail(p__23156){
var map__23157 = p__23156;
var map__23157__$1 = cljs.core.__destructure_map(map__23157);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23157__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var scope = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23157__$1,new cljs.core.Keyword(null,"scope","scope",-439358418));
var col_span = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23157__$1,new cljs.core.Keyword(null,"col-span","col-span",-232603210));
var row_span = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23157__$1,new cljs.core.Keyword(null,"row-span","row-span",-365554241));
var align = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23157__$1,new cljs.core.Keyword(null,"align","align",1964212802));
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),type,new cljs.core.Keyword(null,"scope","scope",-439358418),scope,new cljs.core.Keyword(null,"colSpan","colSpan",872137394),col_span,new cljs.core.Keyword(null,"rowSpan","rowSpan",826884002),row_span,new cljs.core.Keyword(null,"align","align",1964212802),align], null);
});

//# sourceMappingURL=app.components.x_table_cell.model.js.map
