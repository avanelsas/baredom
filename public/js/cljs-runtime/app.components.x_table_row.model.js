goog.provide('app.components.x_table_row.model');
app.components.x_table_row.model.tag_name = "x-table-row";
app.components.x_table_row.model.attr_selected = "selected";
app.components.x_table_row.model.attr_disabled = "disabled";
app.components.x_table_row.model.attr_interactive = "interactive";
app.components.x_table_row.model.attr_row_index = "row-index";
app.components.x_table_row.model.observed_attributes = [app.components.x_table_row.model.attr_selected,app.components.x_table_row.model.attr_disabled,app.components.x_table_row.model.attr_interactive,app.components.x_table_row.model.attr_row_index];
app.components.x_table_row.model.event_click = "x-table-row-click";
app.components.x_table_row.model.event_connected = "x-table-row-connected";
app.components.x_table_row.model.event_disconnected = "x-table-row-disconnected";
app.components.x_table_row.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"interactive","interactive",-2024078362),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null);
app.components.x_table_row.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_table_row.model.event_click,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null),app.components.x_table_row.model.event_connected,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"interactive","interactive",-2024078362),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null),app.components.x_table_row.model.event_disconnected,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null)]);
/**
 * Parse a row-index attribute string to a positive integer.
 *   Returns nil for absent, zero, negative, or non-numeric input.
 */
app.components.x_table_row.model.parse_row_index = (function app$components$x_table_row$model$parse_row_index(s){
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
 *  :selected?      boolean
 *  :disabled?      boolean
 *  :interactive?   boolean
 *  :row-index-raw  string | nil
 * 
 *   Output keys mirror the attribute semantics with parsed/normalised values.
 */
app.components.x_table_row.model.normalize = (function app$components$x_table_row$model$normalize(p__23168){
var map__23169 = p__23168;
var map__23169__$1 = cljs.core.__destructure_map(map__23169);
var selected_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23169__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23169__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var interactive_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23169__$1,new cljs.core.Keyword(null,"interactive?","interactive?",367617676));
var row_index_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23169__$1,new cljs.core.Keyword(null,"row-index-raw","row-index-raw",1490649093));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"selected?","selected?",-742502788),cljs.core.boolean$(selected_QMARK_),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_QMARK_),new cljs.core.Keyword(null,"interactive?","interactive?",367617676),cljs.core.boolean$(interactive_QMARK_),new cljs.core.Keyword(null,"row-index","row-index",-828710296),app.components.x_table_row.model.parse_row_index(row_index_raw)], null);
});
/**
 * Returns true when the row should respond to user interaction
 *   (interactive attribute set and not disabled).
 */
app.components.x_table_row.model.interactive_eligible_QMARK_ = (function app$components$x_table_row$model$interactive_eligible_QMARK_(p__23170){
var map__23171 = p__23170;
var map__23171__$1 = cljs.core.__destructure_map(map__23171);
var interactive_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23171__$1,new cljs.core.Keyword(null,"interactive?","interactive?",367617676));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23171__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var and__5140__auto__ = interactive_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto__;
}
});
/**
 * Returns the aria-selected string value or nil (to remove the attribute).
 * 
 *   Rules:
 *   - selected → "true"
 *   - interactive but not selected → "false" (row is in a selection context)
 *   - neither interactive nor selected → nil (row is not selectable, omit attribute)
 */
app.components.x_table_row.model.aria_selected_value = (function app$components$x_table_row$model$aria_selected_value(p__23172){
var map__23173 = p__23172;
var map__23173__$1 = cljs.core.__destructure_map(map__23173);
var selected_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23173__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var interactive_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23173__$1,new cljs.core.Keyword(null,"interactive?","interactive?",367617676));
if(cljs.core.truth_(selected_QMARK_)){
return "true";
} else {
if(cljs.core.truth_(interactive_QMARK_)){
return "false";
} else {
return null;

}
}
});
/**
 * Build the event detail map for x-table-row-connected.
 */
app.components.x_table_row.model.connected_detail = (function app$components$x_table_row$model$connected_detail(p__23174){
var map__23175 = p__23174;
var map__23175__$1 = cljs.core.__destructure_map(map__23175);
var selected_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23175__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23175__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var interactive_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23175__$1,new cljs.core.Keyword(null,"interactive?","interactive?",367617676));
var row_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23175__$1,new cljs.core.Keyword(null,"row-index","row-index",-828710296));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),(function (){var or__5142__auto__ = row_index;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})(),new cljs.core.Keyword(null,"selected","selected",574897764),selected_QMARK_,new cljs.core.Keyword(null,"disabled","disabled",-1529784218),disabled_QMARK_,new cljs.core.Keyword(null,"interactive","interactive",-2024078362),interactive_QMARK_], null);
});
/**
 * Build the event detail map for x-table-row-click.
 */
app.components.x_table_row.model.click_detail = (function app$components$x_table_row$model$click_detail(p__23176){
var map__23177 = p__23176;
var map__23177__$1 = cljs.core.__destructure_map(map__23177);
var selected_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23177__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23177__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var row_index = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23177__$1,new cljs.core.Keyword(null,"row-index","row-index",-828710296));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"rowIndex","rowIndex",-821650233),(function (){var or__5142__auto__ = row_index;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})(),new cljs.core.Keyword(null,"selected","selected",574897764),selected_QMARK_,new cljs.core.Keyword(null,"disabled","disabled",-1529784218),disabled_QMARK_], null);
});

//# sourceMappingURL=app.components.x_table_row.model.js.map
