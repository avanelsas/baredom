goog.provide('app.components.x_grid.model');
app.components.x_grid.model.tag_name = "x-grid";
app.components.x_grid.model.attr_columns = "columns";
app.components.x_grid.model.attr_min_column_size = "min-column-size";
app.components.x_grid.model.attr_gap = "gap";
app.components.x_grid.model.attr_row_gap = "row-gap";
app.components.x_grid.model.attr_column_gap = "column-gap";
app.components.x_grid.model.attr_align_items = "align-items";
app.components.x_grid.model.attr_justify_items = "justify-items";
app.components.x_grid.model.attr_auto_flow = "auto-flow";
app.components.x_grid.model.attr_inline = "inline";
app.components.x_grid.model.observed_attributes = [app.components.x_grid.model.attr_columns,app.components.x_grid.model.attr_min_column_size,app.components.x_grid.model.attr_gap,app.components.x_grid.model.attr_row_gap,app.components.x_grid.model.attr_column_gap,app.components.x_grid.model.attr_align_items,app.components.x_grid.model.attr_justify_items,app.components.x_grid.model.attr_auto_flow,app.components.x_grid.model.attr_inline];
app.components.x_grid.model.gap_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, ["none",null,"xs",null,"md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_grid.model.align_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["center",null,"start",null,"stretch",null,"end",null], null), null);
app.components.x_grid.model.flow_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["column-dense",null,"row-dense",null,"dense",null,"column",null,"row",null], null), null);
app.components.x_grid.model.default_gap = "md";
app.components.x_grid.model.default_min_column_size = "16rem";
app.components.x_grid.model.property_api = cljs.core.PersistentArrayMap.EMPTY;
app.components.x_grid.model.event_schema = cljs.core.PersistentArrayMap.EMPTY;
app.components.x_grid.model.gap__GT_css = (function app$components$x_grid$model$gap__GT_css(v){
var G__20844 = v;
switch (G__20844) {
case "none":
return "0";

break;
case "xs":
return "4px";

break;
case "sm":
return "8px";

break;
case "md":
return "16px";

break;
case "lg":
return "24px";

break;
case "xl":
return "32px";

break;
default:
return "16px";

}
});
app.components.x_grid.model.valid_enum = (function app$components$x_grid$model$valid_enum(v,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
app.components.x_grid.model.normalize_gap = (function app$components$x_grid$model$normalize_gap(v){
return app.components.x_grid.model.valid_enum(v,app.components.x_grid.model.gap_values,app.components.x_grid.model.default_gap);
});
app.components.x_grid.model.normalize_row_gap = (function app$components$x_grid$model$normalize_row_gap(v){
if(cljs.core.contains_QMARK_(app.components.x_grid.model.gap_values,v)){
return v;
} else {
return null;
}
});
app.components.x_grid.model.normalize_column_gap = (function app$components$x_grid$model$normalize_column_gap(v){
if(cljs.core.contains_QMARK_(app.components.x_grid.model.gap_values,v)){
return v;
} else {
return null;
}
});
app.components.x_grid.model.normalize_align = (function app$components$x_grid$model$normalize_align(v){
return app.components.x_grid.model.valid_enum(v,app.components.x_grid.model.align_values,"stretch");
});
app.components.x_grid.model.normalize_flow = (function app$components$x_grid$model$normalize_flow(v){
return app.components.x_grid.model.valid_enum(v,app.components.x_grid.model.flow_values,"row");
});
app.components.x_grid.model.flow__GT_css = (function app$components$x_grid$model$flow__GT_css(v){
var G__20851 = v;
switch (G__20851) {
case "row-dense":
return "row dense";

break;
case "column-dense":
return "column dense";

break;
default:
return v;

}
});
app.components.x_grid.model.normalize_columns = (function app$components$x_grid$model$normalize_columns(v){
if(cljs.core.truth_((function (){var and__5140__auto__ = v;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v);
} else {
return and__5140__auto__;
}
})())){
return v;
} else {
return null;
}
});
app.components.x_grid.model.normalize_min_size = (function app$components$x_grid$model$normalize_min_size(v){
if(cljs.core.truth_((function (){var and__5140__auto__ = v;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",v);
} else {
return and__5140__auto__;
}
})())){
return v;
} else {
return app.components.x_grid.model.default_min_column_size;
}
});
app.components.x_grid.model.derive_state = (function app$components$x_grid$model$derive_state(p__20854){
var map__20855 = p__20854;
var map__20855__$1 = cljs.core.__destructure_map(map__20855);
var inline = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"inline","inline",1399884222));
var justify_items = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"justify-items","justify-items",1638310783));
var row_gap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"row-gap","row-gap",-1809905537));
var auto_flow = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"auto-flow","auto-flow",-1524521728));
var align_items = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"align-items","align-items",-267946462));
var columns = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"columns","columns",1998437288));
var min_column_size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"min-column-size","min-column-size",-1066019378));
var column_gap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"column-gap","column-gap",384822863));
var gap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20855__$1,new cljs.core.Keyword(null,"gap","gap",80255254));
var columns_STAR_ = app.components.x_grid.model.normalize_columns(columns);
var min_STAR_ = app.components.x_grid.model.normalize_min_size(min_column_size);
var gap_STAR_ = app.components.x_grid.model.normalize_gap(gap);
var row_gap_STAR_ = (function (){var or__5142__auto__ = app.components.x_grid.model.normalize_row_gap(row_gap);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return gap_STAR_;
}
})();
var col_gap_STAR_ = (function (){var or__5142__auto__ = app.components.x_grid.model.normalize_column_gap(column_gap);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return gap_STAR_;
}
})();
var align_STAR_ = app.components.x_grid.model.normalize_align(align_items);
var justify_STAR_ = app.components.x_grid.model.normalize_align(justify_items);
var flow_STAR_ = app.components.x_grid.model.flow__GT_css(app.components.x_grid.model.normalize_flow(auto_flow));
var template = (cljs.core.truth_(columns_STAR_)?columns_STAR_:(""+"repeat(auto-fit,minmax("+cljs.core.str.cljs$core$IFn$_invoke$arity$1(min_STAR_)+",1fr))"));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"auto-flow","auto-flow",-1524521728),new cljs.core.Keyword(null,"align-items","align-items",-267946462),new cljs.core.Keyword(null,"columns","columns",1998437288),new cljs.core.Keyword(null,"column-gap","column-gap",384822863),new cljs.core.Keyword(null,"gap","gap",80255254),new cljs.core.Keyword(null,"row-gap-token","row-gap-token",-219143816),new cljs.core.Keyword(null,"inline","inline",1399884222),new cljs.core.Keyword(null,"column-gap-token","column-gap-token",-10192833),new cljs.core.Keyword(null,"row-gap","row-gap",-1809905537),new cljs.core.Keyword(null,"justify-items","justify-items",1638310783)],[flow_STAR_,align_STAR_,template,app.components.x_grid.model.gap__GT_css(col_gap_STAR_),gap_STAR_,row_gap_STAR_,cljs.core.boolean$(inline),col_gap_STAR_,app.components.x_grid.model.gap__GT_css(row_gap_STAR_),justify_STAR_]);
});

//# sourceMappingURL=app.components.x_grid.model.js.map
