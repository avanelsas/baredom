goog.provide('app.components.x_stat.model');
app.components.x_stat.model.tag_name = "x-stat";
app.components.x_stat.model.attr_variant = "variant";
app.components.x_stat.model.attr_align = "align";
app.components.x_stat.model.attr_size = "size";
app.components.x_stat.model.attr_emphasis = "emphasis";
app.components.x_stat.model.attr_trend = "trend";
app.components.x_stat.model.attr_loading = "loading";
app.components.x_stat.model.attr_label = "label";
app.components.x_stat.model.attr_value = "value";
app.components.x_stat.model.attr_hint = "hint";
app.components.x_stat.model.observed_attributes = [app.components.x_stat.model.attr_variant,app.components.x_stat.model.attr_align,app.components.x_stat.model.attr_size,app.components.x_stat.model.attr_emphasis,app.components.x_stat.model.attr_trend,app.components.x_stat.model.attr_loading,app.components.x_stat.model.attr_label,app.components.x_stat.model.attr_value,app.components.x_stat.model.attr_hint];
app.components.x_stat.model.variant_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["warning",null,"positive",null,"subtle",null,"danger",null,"default",null], null), null);
app.components.x_stat.model.align_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["center",null,"start",null,"end",null], null), null);
app.components.x_stat.model.size_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_stat.model.emphasis_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["high",null,"normal",null], null), null);
app.components.x_stat.model.trend_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["neutral",null,"up",null,"down",null], null), null);
app.components.x_stat.model.default_variant = "default";
app.components.x_stat.model.default_align = "start";
app.components.x_stat.model.default_size = "md";
app.components.x_stat.model.default_emphasis = "normal";
app.components.x_stat.model.default_trend = "neutral";
app.components.x_stat.model.property_api = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"attribute","attribute",-2074029119),app.components.x_stat.model.attr_loading,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"default","default",-1987822328),false,new cljs.core.Keyword(null,"reflects-to-attribute","reflects-to-attribute",1726736839),true], null)], null);
app.components.x_stat.model.event_schema = cljs.core.PersistentArrayMap.EMPTY;
app.components.x_stat.model.valid_enum = (function app$components$x_stat$model$valid_enum(value,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,value)){
return value;
} else {
return fallback;
}
});
app.components.x_stat.model.normalize_variant = (function app$components$x_stat$model$normalize_variant(v){
return app.components.x_stat.model.valid_enum(v,app.components.x_stat.model.variant_values,app.components.x_stat.model.default_variant);
});
app.components.x_stat.model.normalize_align = (function app$components$x_stat$model$normalize_align(v){
return app.components.x_stat.model.valid_enum(v,app.components.x_stat.model.align_values,app.components.x_stat.model.default_align);
});
app.components.x_stat.model.normalize_size = (function app$components$x_stat$model$normalize_size(v){
return app.components.x_stat.model.valid_enum(v,app.components.x_stat.model.size_values,app.components.x_stat.model.default_size);
});
app.components.x_stat.model.normalize_emphasis = (function app$components$x_stat$model$normalize_emphasis(v){
return app.components.x_stat.model.valid_enum(v,app.components.x_stat.model.emphasis_values,app.components.x_stat.model.default_emphasis);
});
app.components.x_stat.model.normalize_trend = (function app$components$x_stat$model$normalize_trend(v){
return app.components.x_stat.model.valid_enum(v,app.components.x_stat.model.trend_values,app.components.x_stat.model.default_trend);
});
app.components.x_stat.model.normalize_bool = (function app$components$x_stat$model$normalize_bool(v){
return cljs.core.boolean$(v);
});
app.components.x_stat.model.normalize_text = (function app$components$x_stat$model$normalize_text(v){
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
app.components.x_stat.model.derive_state = (function app$components$x_stat$model$derive_state(p__21108){
var map__21109 = p__21108;
var map__21109__$1 = cljs.core.__destructure_map(map__21109);
var emphasis = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"emphasis","emphasis",293543451));
var trend = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"trend","trend",54563841));
var align = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"align","align",1964212802));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"value","value",305978217));
var hint = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"hint","hint",439639918));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var loading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"loading","loading",-737050189));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21109__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var variant_STAR_ = app.components.x_stat.model.normalize_variant(variant);
var align_STAR_ = app.components.x_stat.model.normalize_align(align);
var size_STAR_ = app.components.x_stat.model.normalize_size(size);
var emphasis_STAR_ = app.components.x_stat.model.normalize_emphasis(emphasis);
var trend_STAR_ = app.components.x_stat.model.normalize_trend(trend);
var loading_STAR_ = app.components.x_stat.model.normalize_bool(loading);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"trend","trend",54563841),new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"hint","hint",439639918),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"aria-busy","aria-busy",793864247),new cljs.core.Keyword(null,"emphasis","emphasis",293543451)],[trend_STAR_,align_STAR_,variant_STAR_,app.components.x_stat.model.normalize_text(value),app.components.x_stat.model.normalize_text(hint),size_STAR_,loading_STAR_,app.components.x_stat.model.normalize_text(label),((loading_STAR_)?"true":null),emphasis_STAR_]);
});

//# sourceMappingURL=app.components.x_stat.model.js.map
