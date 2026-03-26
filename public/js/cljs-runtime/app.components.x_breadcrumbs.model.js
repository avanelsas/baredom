goog.provide('app.components.x_breadcrumbs.model');
app.components.x_breadcrumbs.model.tag_name = "x-breadcrumbs";
app.components.x_breadcrumbs.model.attr_separator = "separator";
app.components.x_breadcrumbs.model.attr_size = "size";
app.components.x_breadcrumbs.model.attr_variant = "variant";
app.components.x_breadcrumbs.model.attr_wrap = "wrap";
app.components.x_breadcrumbs.model.attr_max_items = "max-items";
app.components.x_breadcrumbs.model.attr_items_before = "items-before";
app.components.x_breadcrumbs.model.attr_items_after = "items-after";
app.components.x_breadcrumbs.model.attr_disabled = "disabled";
app.components.x_breadcrumbs.model.attr_preserve_aria_current = "preserve-aria-current";
app.components.x_breadcrumbs.model.attr_aria_label = "aria-label";
app.components.x_breadcrumbs.model.attr_aria_describedby = "aria-describedby";
app.components.x_breadcrumbs.model.observed_attributes = [app.components.x_breadcrumbs.model.attr_separator,app.components.x_breadcrumbs.model.attr_size,app.components.x_breadcrumbs.model.attr_variant,app.components.x_breadcrumbs.model.attr_wrap,app.components.x_breadcrumbs.model.attr_max_items,app.components.x_breadcrumbs.model.attr_items_before,app.components.x_breadcrumbs.model.attr_items_after,app.components.x_breadcrumbs.model.attr_disabled,app.components.x_breadcrumbs.model.attr_preserve_aria_current,app.components.x_breadcrumbs.model.attr_aria_label,app.components.x_breadcrumbs.model.attr_aria_describedby];
app.components.x_breadcrumbs.model.valid_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_breadcrumbs.model.valid_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["subtle",null,"text",null,"default",null], null), null);
app.components.x_breadcrumbs.model.default_size = "md";
app.components.x_breadcrumbs.model.default_variant = "default";
app.components.x_breadcrumbs.model.default_separator = "/";
app.components.x_breadcrumbs.model.default_items_before = (1);
app.components.x_breadcrumbs.model.default_items_after = (2);
app.components.x_breadcrumbs.model.parse_enum = (function app$components$x_breadcrumbs$model$parse_enum(valid,default$,s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(valid,v)){
return v;
} else {
return default$;
}
});
app.components.x_breadcrumbs.model.parse_size = (function app$components$x_breadcrumbs$model$parse_size(s){
return app.components.x_breadcrumbs.model.parse_enum(app.components.x_breadcrumbs.model.valid_sizes,app.components.x_breadcrumbs.model.default_size,s);
});
app.components.x_breadcrumbs.model.parse_variant = (function app$components$x_breadcrumbs$model$parse_variant(s){
return app.components.x_breadcrumbs.model.parse_enum(app.components.x_breadcrumbs.model.valid_variants,app.components.x_breadcrumbs.model.default_variant,s);
});
app.components.x_breadcrumbs.model.parse_bool_attr = (function app$components$x_breadcrumbs$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Parse string to positive integer ≥ 1, returning `fallback` on failure.
 */
app.components.x_breadcrumbs.model.parse_pos_int = (function app$components$x_breadcrumbs$model$parse_pos_int(s,fallback){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(cljs.core.truth_((function (){var and__5140__auto__ = isFinite(n);
if(cljs.core.truth_(and__5140__auto__)){
return (n > (0));
} else {
return and__5140__auto__;
}
})())){
return n;
} else {
return fallback;
}
} else {
return fallback;
}
});
/**
 * Parse string to positive integer ≥ 1, returning nil on failure.
 */
app.components.x_breadcrumbs.model.parse_pos_int_or_nil = (function app$components$x_breadcrumbs$model$parse_pos_int_or_nil(s){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(cljs.core.truth_((function (){var and__5140__auto__ = isFinite(n);
if(cljs.core.truth_(and__5140__auto__)){
return (n > (0));
} else {
return and__5140__auto__;
}
})())){
return n;
} else {
return null;
}
} else {
return null;
}
});
app.components.x_breadcrumbs.model.normalize_separator = (function app$components$x_breadcrumbs$model$normalize_separator(s){
if(typeof s === 'string'){
return s;
} else {
return app.components.x_breadcrumbs.model.default_separator;
}
});
/**
 * Return a map describing which items to display and where the ellipsis goes.
 * 
 * Returns:
 * {:visible     [0 1 4 5]  ; indices of visible items in order
 *  :ellipsis-at 2          ; insert ellipsis before this position in :visible (-1 = none)
 *  :total       6}
 */
app.components.x_breadcrumbs.model.build_plan = (function app$components$x_breadcrumbs$model$build_plan(total,max_items,items_before,items_after){
if((((max_items == null)) || ((total <= max_items)))){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"visible","visible",-1024216805),cljs.core.vec(cljs.core.range.cljs$core$IFn$_invoke$arity$1(total)),new cljs.core.Keyword(null,"ellipsis-at","ellipsis-at",-628372349),(-1),new cljs.core.Keyword(null,"total","total",1916810418),total], null);
} else {
var before = cljs.core.min.cljs$core$IFn$_invoke$arity$2(items_before,cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),(total - items_after)));
var after = cljs.core.min.cljs$core$IFn$_invoke$arity$2(items_after,cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),(total - before)));
var before__$1 = cljs.core.min.cljs$core$IFn$_invoke$arity$2(before,(total - after));
var before__$2 = cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),before__$1);
var before_idxs = cljs.core.vec(cljs.core.range.cljs$core$IFn$_invoke$arity$1(before__$2));
var after_idxs = cljs.core.vec(cljs.core.range.cljs$core$IFn$_invoke$arity$2((total - after),total));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"visible","visible",-1024216805),cljs.core.into.cljs$core$IFn$_invoke$arity$2(before_idxs,after_idxs),new cljs.core.Keyword(null,"ellipsis-at","ellipsis-at",-628372349),cljs.core.count(before_idxs),new cljs.core.Keyword(null,"total","total",1916810418),total], null);
}
});
app.components.x_breadcrumbs.model.normalize = (function app$components$x_breadcrumbs$model$normalize(p__21932){
var map__21933 = p__21932;
var map__21933__$1 = cljs.core.__destructure_map(map__21933);
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
var separator_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"separator-raw","separator-raw",1358337914));
var aria_describedby_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860));
var max_items_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"max-items-raw","max-items-raw",-340487390));
var variant_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var items_after_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"items-after-raw","items-after-raw",-1677187220));
var wrap_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"wrap-raw","wrap-raw",-1468030804));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var preserve_aria_current_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"preserve-aria-current-present?","preserve-aria-current-present?",729931506));
var items_before_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21933__$1,new cljs.core.Keyword(null,"items-before-raw","items-before-raw",1580997624));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"preserve-aria-current","preserve-aria-current",682773185),new cljs.core.Keyword(null,"items-after","items-after",358736705),new cljs.core.Keyword(null,"wrap","wrap",851669987),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"max-items","max-items",-1969244147),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),new cljs.core.Keyword(null,"items-before","items-before",-1151895719),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),new cljs.core.Keyword(null,"separator","separator",-1628749125)],[cljs.core.boolean$(preserve_aria_current_present_QMARK_),app.components.x_breadcrumbs.model.parse_pos_int(items_after_raw,app.components.x_breadcrumbs.model.default_items_after),app.components.x_breadcrumbs.model.parse_bool_attr(wrap_raw),cljs.core.boolean$(disabled_present_QMARK_),app.components.x_breadcrumbs.model.parse_variant(variant_raw),app.components.x_breadcrumbs.model.parse_pos_int_or_nil(max_items_raw),app.components.x_breadcrumbs.model.parse_size(size_raw),aria_describedby_raw,app.components.x_breadcrumbs.model.parse_pos_int(items_before_raw,app.components.x_breadcrumbs.model.default_items_before),aria_label_raw,app.components.x_breadcrumbs.model.normalize_separator(separator_raw)]);
});
app.components.x_breadcrumbs.model.property_api = new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"separator","separator",-1628749125),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"wrap","wrap",851669987),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"max-items","max-items",-1969244147),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"items-before","items-before",-1151895719),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"items-after","items-after",358736705),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);

//# sourceMappingURL=app.components.x_breadcrumbs.model.js.map
