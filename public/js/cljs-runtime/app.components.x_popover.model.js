goog.provide('app.components.x_popover.model');
app.components.x_popover.model.tag_name = "x-popover";
app.components.x_popover.model.attr_open = "open";
app.components.x_popover.model.attr_placement = "placement";
app.components.x_popover.model.attr_heading = "heading";
app.components.x_popover.model.attr_close_label = "close-label";
app.components.x_popover.model.attr_no_close = "no-close";
app.components.x_popover.model.attr_disabled = "disabled";
app.components.x_popover.model.event_toggle = "x-popover-toggle";
app.components.x_popover.model.event_change = "x-popover-change";
app.components.x_popover.model.allowed_placements = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["bottom-start",null,"bottom-end",null,"top-end",null,"top-start",null], null), null);
app.components.x_popover.model.default_placement = "bottom-start";
app.components.x_popover.model.default_close_label = "Close";
app.components.x_popover.model.observed_attributes = [app.components.x_popover.model.attr_open,app.components.x_popover.model.attr_placement,app.components.x_popover.model.attr_heading,app.components.x_popover.model.attr_close_label,app.components.x_popover.model.attr_no_close,app.components.x_popover.model.attr_disabled];
app.components.x_popover.model.property_api = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_open], null),new cljs.core.Keyword(null,"placement","placement",768366651),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_placement], null),new cljs.core.Keyword(null,"heading","heading",-1312171873),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_heading], null),new cljs.core.Keyword(null,"closeLabel","closeLabel",557288558),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_close_label], null),new cljs.core.Keyword(null,"noClose","noClose",1115851533),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_no_close], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_popover.model.attr_disabled], null)], null);
app.components.x_popover.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_popover.model.event_toggle,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_popover.model.event_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null)]);
/**
 * Normalizes raw placement string. Falls back to default-placement if invalid or nil.
 */
app.components.x_popover.model.normalize_placement = (function app$components$x_popover$model$normalize_placement(s){
if(cljs.core.contains_QMARK_(app.components.x_popover.model.allowed_placements,s)){
return s;
} else {
return app.components.x_popover.model.default_placement;
}
});
/**
 * Derives a complete view-model map from raw attribute presence/values.
 */
app.components.x_popover.model.normalize = (function app$components$x_popover$model$normalize(p__22842){
var map__22843 = p__22842;
var map__22843__$1 = cljs.core.__destructure_map(map__22843);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var no_close_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"no-close-present?","no-close-present?",-154717274));
var heading_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"heading-raw","heading-raw",116296555));
var placement_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"placement-raw","placement-raw",-1500957198));
var close_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22843__$1,new cljs.core.Keyword(null,"close-label-raw","close-label-raw",798572883));
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"open?","open?",1238443125),cljs.core.boolean$(open_present_QMARK_),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"no-close?","no-close?",1842227643),cljs.core.boolean$(no_close_present_QMARK_),new cljs.core.Keyword(null,"heading","heading",-1312171873),(function (){var or__5142__auto__ = heading_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"close-label","close-label",-1088385889),(function (){var or__5142__auto__ = close_label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_popover.model.default_close_label;
}
})(),new cljs.core.Keyword(null,"placement","placement",768366651),app.components.x_popover.model.normalize_placement(placement_raw)], null);
});
/**
 * Produces the CustomEvent detail object for toggle/change events.
 */
app.components.x_popover.model.toggle_detail = (function app$components$x_popover$model$toggle_detail(open_QMARK_,source){
return ({"open": open_QMARK_, "source": source});
});

//# sourceMappingURL=app.components.x_popover.model.js.map
