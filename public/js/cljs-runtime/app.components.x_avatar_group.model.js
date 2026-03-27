goog.provide('app.components.x_avatar_group.model');
app.components.x_avatar_group.model.tag_name = "x-avatar-group";
app.components.x_avatar_group.model.attr_size = "size";
app.components.x_avatar_group.model.attr_overlap = "overlap";
app.components.x_avatar_group.model.attr_max = "max";
app.components.x_avatar_group.model.attr_direction = "direction";
app.components.x_avatar_group.model.attr_disabled = "disabled";
app.components.x_avatar_group.model.attr_label = "label";
app.components.x_avatar_group.model.observed_attributes = [app.components.x_avatar_group.model.attr_size,app.components.x_avatar_group.model.attr_overlap,app.components.x_avatar_group.model.attr_max,app.components.x_avatar_group.model.attr_direction,app.components.x_avatar_group.model.attr_disabled,app.components.x_avatar_group.model.attr_label];
app.components.x_avatar_group.model.valid_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["xs",null,"md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_avatar_group.model.valid_overlaps = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["none",null,"md",null,"lg",null,"sm",null], null), null);
app.components.x_avatar_group.model.valid_directions = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["ltr",null,"rtl",null], null), null);
app.components.x_avatar_group.model.default_size = "md";
app.components.x_avatar_group.model.default_overlap = "md";
app.components.x_avatar_group.model.default_direction = "ltr";
app.components.x_avatar_group.model.overlap_margin = new cljs.core.PersistentArrayMap(null, 4, ["none","0px","sm","-4px","md","-8px","lg","-12px"], null);
app.components.x_avatar_group.model.parse_enum = (function app$components$x_avatar_group$model$parse_enum(valid,default$,s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(valid,v)){
return v;
} else {
return default$;
}
});
app.components.x_avatar_group.model.parse_size = (function app$components$x_avatar_group$model$parse_size(s){
return app.components.x_avatar_group.model.parse_enum(app.components.x_avatar_group.model.valid_sizes,app.components.x_avatar_group.model.default_size,s);
});
app.components.x_avatar_group.model.parse_overlap = (function app$components$x_avatar_group$model$parse_overlap(s){
return app.components.x_avatar_group.model.parse_enum(app.components.x_avatar_group.model.valid_overlaps,app.components.x_avatar_group.model.default_overlap,s);
});
app.components.x_avatar_group.model.parse_direction = (function app$components$x_avatar_group$model$parse_direction(s){
return app.components.x_avatar_group.model.parse_enum(app.components.x_avatar_group.model.valid_directions,app.components.x_avatar_group.model.default_direction,s);
});
app.components.x_avatar_group.model.parse_max = (function app$components$x_avatar_group$model$parse_max(s){
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
app.components.x_avatar_group.model.parse_bool_attr = (function app$components$x_avatar_group$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
app.components.x_avatar_group.model.normalize_label = (function app$components$x_avatar_group$model$normalize_label(s){
if(typeof s === 'string'){
var v = s.trim();
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(v,"")){
return null;
} else {
return v;
}
} else {
return null;
}
});
/**
 * Given total item count and optional max, return [visible-count hidden-count].
 */
app.components.x_avatar_group.model.compute_visible_hidden = (function app$components$x_avatar_group$model$compute_visible_hidden(total,max){
if(cljs.core.truth_((function (){var and__5140__auto__ = max;
if(cljs.core.truth_(and__5140__auto__)){
return (total > max);
} else {
return and__5140__auto__;
}
})())){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [max,(total - max)], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [total,(0)], null);
}
});
app.components.x_avatar_group.model.normalize = (function app$components$x_avatar_group$model$normalize(p__21463){
var map__21464 = p__21463;
var map__21464__$1 = cljs.core.__destructure_map(map__21464);
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var overlap_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"overlap-raw","overlap-raw",1202229114));
var max_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"max-raw","max-raw",-434946611));
var direction_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"direction-raw","direction-raw",560730236));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21464__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_avatar_group.model.parse_size(size_raw),new cljs.core.Keyword(null,"overlap","overlap",-1673335644),app.components.x_avatar_group.model.parse_overlap(overlap_raw),new cljs.core.Keyword(null,"max","max",61366548),app.components.x_avatar_group.model.parse_max(max_raw),new cljs.core.Keyword(null,"direction","direction",-633359395),app.components.x_avatar_group.model.parse_direction(direction_raw),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_avatar_group.model.normalize_label(label_raw)], null);
});
app.components.x_avatar_group.model.property_api = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"overlap","overlap",-1673335644),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"direction","direction",-633359395),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);

//# sourceMappingURL=app.components.x_avatar_group.model.js.map
