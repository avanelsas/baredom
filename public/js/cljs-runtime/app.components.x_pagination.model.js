goog.provide('app.components.x_pagination.model');
app.components.x_pagination.model.tag_name = "x-pagination";
app.components.x_pagination.model.attr_page = "page";
app.components.x_pagination.model.attr_total_pages = "total-pages";
app.components.x_pagination.model.attr_sibling_count = "sibling-count";
app.components.x_pagination.model.attr_boundary_count = "boundary-count";
app.components.x_pagination.model.attr_size = "size";
app.components.x_pagination.model.attr_disabled = "disabled";
app.components.x_pagination.model.attr_label = "label";
app.components.x_pagination.model.event_page_change = "page-change";
app.components.x_pagination.model.slot_prev = "prev";
app.components.x_pagination.model.slot_next = "next";
app.components.x_pagination.model.observed_attributes = [app.components.x_pagination.model.attr_page,app.components.x_pagination.model.attr_total_pages,app.components.x_pagination.model.attr_sibling_count,app.components.x_pagination.model.attr_boundary_count,app.components.x_pagination.model.attr_size,app.components.x_pagination.model.attr_disabled,app.components.x_pagination.model.attr_label];
app.components.x_pagination.model.default_page = (1);
app.components.x_pagination.model.default_total_pages = (1);
app.components.x_pagination.model.default_sibling_count = (1);
app.components.x_pagination.model.default_boundary_count = (1);
app.components.x_pagination.model.default_size = "md";
app.components.x_pagination.model.default_label = "Pagination";
app.components.x_pagination.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
/**
 * Parse string to positive integer ≥ 1, returning fallback on failure.
 */
app.components.x_pagination.model.parse_pos_int = (function app$components$x_pagination$model$parse_pos_int(s,fallback){
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
app.components.x_pagination.model.parse_total_pages = (function app$components$x_pagination$model$parse_total_pages(s){
return app.components.x_pagination.model.parse_pos_int(s,app.components.x_pagination.model.default_total_pages);
});
/**
 * Parse current page, clamped to [1, total].
 */
app.components.x_pagination.model.parse_page = (function app$components$x_pagination$model$parse_page(s,total){
var n = app.components.x_pagination.model.parse_pos_int(s,app.components.x_pagination.model.default_page);
return cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),cljs.core.min.cljs$core$IFn$_invoke$arity$2(n,total));
});
app.components.x_pagination.model.parse_sibling_count = (function app$components$x_pagination$model$parse_sibling_count(s){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(cljs.core.truth_((function (){var and__5140__auto__ = isFinite(n);
if(cljs.core.truth_(and__5140__auto__)){
return (n >= (0));
} else {
return and__5140__auto__;
}
})())){
return n;
} else {
return app.components.x_pagination.model.default_sibling_count;
}
} else {
return app.components.x_pagination.model.default_sibling_count;
}
});
app.components.x_pagination.model.parse_boundary_count = (function app$components$x_pagination$model$parse_boundary_count(s){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(cljs.core.truth_((function (){var and__5140__auto__ = isFinite(n);
if(cljs.core.truth_(and__5140__auto__)){
return (n >= (0));
} else {
return and__5140__auto__;
}
})())){
return n;
} else {
return app.components.x_pagination.model.default_boundary_count;
}
} else {
return app.components.x_pagination.model.default_boundary_count;
}
});
app.components.x_pagination.model.parse_size = (function app$components$x_pagination$model$parse_size(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_pagination.model.allowed_sizes,v)){
return v;
} else {
return app.components.x_pagination.model.default_size;
}
});
app.components.x_pagination.model.parse_bool_attr = (function app$components$x_pagination$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
app.components.x_pagination.model.normalize = (function app$components$x_pagination$model$normalize(p__22804){
var map__22805 = p__22804;
var map__22805__$1 = cljs.core.__destructure_map(map__22805);
var page_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"page-raw","page-raw",-1198808870));
var total_pages_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"total-pages-raw","total-pages-raw",430880208));
var sibling_count_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"sibling-count-raw","sibling-count-raw",-620117200));
var boundary_count_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"boundary-count-raw","boundary-count-raw",410083250));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22805__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var total = app.components.x_pagination.model.parse_total_pages(total_pages_raw);
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"page","page",849072397),app.components.x_pagination.model.parse_page(page_raw,total),new cljs.core.Keyword(null,"total-pages","total-pages",685894112),total,new cljs.core.Keyword(null,"sibling-count","sibling-count",1756034573),app.components.x_pagination.model.parse_sibling_count(sibling_count_raw),new cljs.core.Keyword(null,"boundary-count","boundary-count",1637925987),app.components.x_pagination.model.parse_boundary_count(boundary_count_raw),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_pagination.model.parse_size(size_raw),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_pagination.model.default_label;
}
})()], null);
});
/**
 * Returns a sequence of {:type :page :n N} and {:type :ellipsis} maps.
 * current  — 1-indexed current page
 * total    — total page count
 * siblings — pages shown on each side of current
 * boundary — pages always shown at start and end
 */
app.components.x_pagination.model.build_page_items = (function app$components$x_pagination$model$build_page_items(current,total,siblings,boundary){
if((total <= (0))){
return cljs.core.PersistentVector.EMPTY;
} else {
var left_end = cljs.core.min.cljs$core$IFn$_invoke$arity$2(boundary,total);
var right_start = cljs.core.max.cljs$core$IFn$_invoke$arity$2(((total - boundary) - (-1)),(1));
var mid_start = cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),(current - siblings));
var mid_end = cljs.core.min.cljs$core$IFn$_invoke$arity$2(total,(current + siblings));
var pages = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.sorted_set(),cljs.core.range.cljs$core$IFn$_invoke$arity$2((1),(left_end + (1)))),cljs.core.range.cljs$core$IFn$_invoke$arity$2(right_start,(total + (1)))),cljs.core.range.cljs$core$IFn$_invoke$arity$2(mid_start,(mid_end + (1))));
var pages_vec = cljs.core.vec(pages);
var result = cljs.core.PersistentVector.EMPTY;
var i = (0);
while(true){
if((i >= cljs.core.count(pages_vec))){
return result;
} else {
var n = cljs.core.nth.cljs$core$IFn$_invoke$arity$2(pages_vec,i);
var prev = (((i > (0)))?cljs.core.nth.cljs$core$IFn$_invoke$arity$2(pages_vec,(i - (1))):null);
var G__22901 = (function (){var G__22824 = result;
var G__22824__$1 = (cljs.core.truth_((function (){var and__5140__auto__ = prev;
if(cljs.core.truth_(and__5140__auto__)){
return (n > (prev + (1)));
} else {
return and__5140__auto__;
}
})())?cljs.core.conj.cljs$core$IFn$_invoke$arity$2(G__22824,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"ellipsis","ellipsis",998505738)], null)):G__22824);
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(G__22824__$1,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"n","n",562130025),n], null));

})();
var G__22902 = (i + (1));
result = G__22901;
i = G__22902;
continue;
}
break;
}
}
});
app.components.x_pagination.model.prev_disabled_QMARK_ = (function app$components$x_pagination$model$prev_disabled_QMARK_(p__22833){
var map__22841 = p__22833;
var map__22841__$1 = cljs.core.__destructure_map(map__22841);
var page = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22841__$1,new cljs.core.Keyword(null,"page","page",849072397));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22841__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var or__5142__auto__ = disabled;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (page <= (1));
}
});
app.components.x_pagination.model.next_disabled_QMARK_ = (function app$components$x_pagination$model$next_disabled_QMARK_(p__22844){
var map__22845 = p__22844;
var map__22845__$1 = cljs.core.__destructure_map(map__22845);
var page = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22845__$1,new cljs.core.Keyword(null,"page","page",849072397));
var total_pages = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22845__$1,new cljs.core.Keyword(null,"total-pages","total-pages",685894112));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22845__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var or__5142__auto__ = disabled;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (page >= total_pages);
}
});
app.components.x_pagination.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"total-pages","total-pages",685894112),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"sibling-count","sibling-count",1756034573),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"boundary-count","boundary-count",1637925987),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_pagination.model.event_schema = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"page-change","page-change",-95987345),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null)], null);

//# sourceMappingURL=app.components.x_pagination.model.js.map
