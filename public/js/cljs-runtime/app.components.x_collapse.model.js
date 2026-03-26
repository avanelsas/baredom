goog.provide('app.components.x_collapse.model');
app.components.x_collapse.model.tag_name = "x-collapse";
app.components.x_collapse.model.attr_open = "open";
app.components.x_collapse.model.attr_disabled = "disabled";
app.components.x_collapse.model.attr_header = "header";
app.components.x_collapse.model.attr_duration_ms = "duration-ms";
app.components.x_collapse.model.event_toggle = "x-collapse-toggle";
app.components.x_collapse.model.event_change = "x-collapse-change";
app.components.x_collapse.model.slot_content = "content";
app.components.x_collapse.model.default_duration_ms = (300);
app.components.x_collapse.model.observed_attributes = [app.components.x_collapse.model.attr_open,app.components.x_collapse.model.attr_disabled,app.components.x_collapse.model.attr_header,app.components.x_collapse.model.attr_duration_ms];
app.components.x_collapse.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_collapse.model.attr_open], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_collapse.model.attr_disabled], null),new cljs.core.Keyword(null,"header","header",119441134),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_collapse.model.attr_header], null),new cljs.core.Keyword(null,"durationMs","durationMs",1439579766),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_collapse.model.attr_duration_ms], null)], null);
app.components.x_collapse.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_collapse.model.event_toggle,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_collapse.model.event_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null)]);
/**
 * Parses s as a positive integer. Returns nil if s is nil, not a number, or <= 0.
 */
app.components.x_collapse.model.parse_int_pos = (function app$components$x_collapse$model$parse_int_pos(s){
if(cljs.core.truth_(s)){
var n = parseInt(s,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return n;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Clamps n to the range [lo hi].
 */
app.components.x_collapse.model.clamp = (function app$components$x_collapse$model$clamp(n,lo,hi){
return cljs.core.max.cljs$core$IFn$_invoke$arity$2(lo,cljs.core.min.cljs$core$IFn$_invoke$arity$2(hi,n));
});
/**
 * Derives a complete view-model map from raw attribute presence/values.
 */
app.components.x_collapse.model.normalize = (function app$components$x_collapse$model$normalize(p__22221){
var map__22222 = p__22221;
var map__22222__$1 = cljs.core.__destructure_map(map__22222);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22222__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22222__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var header_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22222__$1,new cljs.core.Keyword(null,"header-raw","header-raw",41484));
var duration_ms_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22222__$1,new cljs.core.Keyword(null,"duration-ms-raw","duration-ms-raw",-2138819526));
var open_QMARK_ = cljs.core.boolean$(open_present_QMARK_);
var disabled_QMARK_ = cljs.core.boolean$(disabled_present_QMARK_);
var header = (function (){var or__5142__auto__ = header_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var dur_raw = app.components.x_collapse.model.parse_int_pos(duration_ms_raw);
var duration = (cljs.core.truth_(dur_raw)?app.components.x_collapse.model.clamp(dur_raw,(0),(2000)):app.components.x_collapse.model.default_duration_ms);
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"open?","open?",1238443125),open_QMARK_,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),disabled_QMARK_,new cljs.core.Keyword(null,"header","header",119441134),header,new cljs.core.Keyword(null,"duration-ms","duration-ms",1993555055),duration], null);
});
/**
 * Produces the CustomEvent detail object for toggle/change events.
 */
app.components.x_collapse.model.toggle_detail = (function app$components$x_collapse$model$toggle_detail(open_QMARK_,source){
return ({"open": open_QMARK_, "source": source});
});

//# sourceMappingURL=app.components.x_collapse.model.js.map
