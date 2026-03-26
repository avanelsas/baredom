goog.provide('app.components.x_table.model');
app.components.x_table.model.tag_name = "x-table";
app.components.x_table.model.attr_columns = "columns";
app.components.x_table.model.attr_caption = "caption";
app.components.x_table.model.attr_selectable = "selectable";
app.components.x_table.model.attr_striped = "striped";
app.components.x_table.model.attr_bordered = "bordered";
app.components.x_table.model.attr_full_width = "full-width";
app.components.x_table.model.attr_compact = "compact";
app.components.x_table.model.attr_row_count = "row-count";
app.components.x_table.model.observed_attributes = [app.components.x_table.model.attr_columns,app.components.x_table.model.attr_caption,app.components.x_table.model.attr_selectable,app.components.x_table.model.attr_striped,app.components.x_table.model.attr_bordered,app.components.x_table.model.attr_full_width,app.components.x_table.model.attr_compact,app.components.x_table.model.attr_row_count];
app.components.x_table.model.event_sort = "x-table-sort";
app.components.x_table.model.event_row_select = "x-table-row-select";
app.components.x_table.model.property_api = new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"columns","columns",1998437288),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"caption","caption",-855383902),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"selectable","selectable",370587038),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"striped","striped",-628686784),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"bordered","bordered",-832486681),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"fullWidth","fullWidth",-1436357554),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"compact","compact",-348732150),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"rowCount","rowCount",917416504),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null);
app.components.x_table.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_table.model.event_sort,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"colIndex","colIndex",171239635),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"direction","direction",-633359395),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"previousDirection","previousDirection",900518581),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null),app.components.x_table.model.event_row_select,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"selectionMode","selectionMode",1264773367),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_table.model.selectable_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["none",null,"single",null,"multi",null], null), null);
/**
 * Parse `columns` attribute to a CSS grid-template-columns string.
 *   A positive integer string (e.g. "4") becomes "repeat(4,1fr)".
 *   Any other non-empty string is returned as-is.
 *   Nil or blank → nil (no explicit template).
 */
app.components.x_table.model.parse_columns = (function app$components$x_table$model$parse_columns(s){
if(typeof s === 'string'){
var t = s.trim();
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(t,"")){
var n = parseInt(t,(10));
if(((cljs.core.not(isNaN(n))) && ((((n > (0))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(t,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)))))))){
return (""+"repeat("+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)+",1fr)");
} else {
return t;
}
} else {
return null;
}
} else {
return null;
}
});
/**
 * Normalise selectable attribute. Unknown/nil → "none".
 */
app.components.x_table.model.parse_selectable = (function app$components$x_table$model$parse_selectable(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_table.model.selectable_values,v)){
return v;
} else {
return "none";
}
});
/**
 * Parse row-count to a positive integer, or nil if absent/invalid.
 */
app.components.x_table.model.parse_row_count = (function app$components$x_table$model$parse_row_count(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return Math.floor(n);
} else {
return null;
}
} else {
return null;
}
});
/**
 * Produce a canonical view-model map from raw attribute inputs.
 * 
 *   Input keys:
 *  :columns-raw     string | nil
 *  :caption-raw     string | nil
 *  :selectable-raw  string | nil
 *  :striped?        boolean
 *  :bordered?       boolean
 *  :full-width?     boolean
 *  :compact?        boolean
 *  :row-count-raw   string | nil
 * 
 *   Output keys mirror the attribute semantics with parsed/normalised values.
 */
app.components.x_table.model.normalize = (function app$components$x_table$model$normalize(p__23253){
var map__23254 = p__23253;
var map__23254__$1 = cljs.core.__destructure_map(map__23254);
var columns_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"columns-raw","columns-raw",-2093994046));
var caption_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"caption-raw","caption-raw",-375935083));
var selectable_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"selectable-raw","selectable-raw",-1276314763));
var striped_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"striped?","striped?",-797214979));
var bordered_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"bordered?","bordered?",562358476));
var full_width_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"full-width?","full-width?",1477288349));
var compact_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"compact?","compact?",1216893298));
var row_count_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23254__$1,new cljs.core.Keyword(null,"row-count-raw","row-count-raw",-1432781274));
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"columns","columns",1998437288),app.components.x_table.model.parse_columns(columns_raw),new cljs.core.Keyword(null,"caption","caption",-855383902),(function (){var or__5142__auto__ = caption_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"selectable","selectable",370587038),app.components.x_table.model.parse_selectable(selectable_raw),new cljs.core.Keyword(null,"striped?","striped?",-797214979),cljs.core.boolean$(striped_QMARK_),new cljs.core.Keyword(null,"bordered?","bordered?",562358476),cljs.core.boolean$(bordered_QMARK_),new cljs.core.Keyword(null,"full-width?","full-width?",1477288349),cljs.core.boolean$(full_width_QMARK_),new cljs.core.Keyword(null,"compact?","compact?",1216893298),cljs.core.boolean$(compact_QMARK_),new cljs.core.Keyword(null,"row-count","row-count",1060167988),app.components.x_table.model.parse_row_count(row_count_raw)], null);
});
/**
 * Return ARIA role string. "none" → "table"; any selection mode → "grid".
 */
app.components.x_table.model.role_for_selectable = (function app$components$x_table$model$role_for_selectable(selectable){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(selectable,"none")){
return "table";
} else {
return "grid";
}
});
/**
 * Return "true" for multi-select, nil otherwise (attribute should be removed).
 */
app.components.x_table.model.aria_multiselectable = (function app$components$x_table$model$aria_multiselectable(selectable){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(selectable,"multi")){
return "true";
} else {
return null;
}
});
/**
 * Build event detail for x-table-sort.
 */
app.components.x_table.model.sort_detail = (function app$components$x_table$model$sort_detail(col_index,direction,previous_direction){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"colIndex","colIndex",171239635),col_index,new cljs.core.Keyword(null,"direction","direction",-633359395),direction,new cljs.core.Keyword(null,"previousDirection","previousDirection",900518581),previous_direction], null);
});
/**
 * Build event detail for x-table-row-select.
 */
app.components.x_table.model.row_select_detail = (function app$components$x_table$model$row_select_detail(row_index,selected_QMARK_,selectable){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),(function (){var or__5142__auto__ = row_index;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})(),new cljs.core.Keyword(null,"selected","selected",574897764),selected_QMARK_,new cljs.core.Keyword(null,"selectionMode","selectionMode",1264773367),selectable], null);
});

//# sourceMappingURL=app.components.x_table.model.js.map
