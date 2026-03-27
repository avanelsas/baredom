goog.provide('app.components.x_avatar.model');
app.components.x_avatar.model.tag_name = "x-avatar";
app.components.x_avatar.model.attr_src = "src";
app.components.x_avatar.model.attr_alt = "alt";
app.components.x_avatar.model.attr_name = "name";
app.components.x_avatar.model.attr_initials = "initials";
app.components.x_avatar.model.attr_size = "size";
app.components.x_avatar.model.attr_shape = "shape";
app.components.x_avatar.model.attr_variant = "variant";
app.components.x_avatar.model.attr_status = "status";
app.components.x_avatar.model.attr_disabled = "disabled";
app.components.x_avatar.model.observed_attributes = [app.components.x_avatar.model.attr_src,app.components.x_avatar.model.attr_alt,app.components.x_avatar.model.attr_name,app.components.x_avatar.model.attr_initials,app.components.x_avatar.model.attr_size,app.components.x_avatar.model.attr_shape,app.components.x_avatar.model.attr_variant,app.components.x_avatar.model.attr_status,app.components.x_avatar.model.attr_disabled];
app.components.x_avatar.model.valid_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["xs",null,"md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_avatar.model.valid_shapes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["rounded",null,"square",null,"circle",null], null), null);
app.components.x_avatar.model.valid_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["neutral",null,"brand",null,"subtle",null], null), null);
app.components.x_avatar.model.valid_statuses = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["online",null,"away",null,"offline",null,"busy",null], null), null);
app.components.x_avatar.model.default_size = "md";
app.components.x_avatar.model.default_shape = "circle";
app.components.x_avatar.model.default_variant = "neutral";
app.components.x_avatar.model.parse_enum = (function app$components$x_avatar$model$parse_enum(valid,default$,s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(valid,v)){
return v;
} else {
return default$;
}
});
app.components.x_avatar.model.parse_size = (function app$components$x_avatar$model$parse_size(s){
return app.components.x_avatar.model.parse_enum(app.components.x_avatar.model.valid_sizes,app.components.x_avatar.model.default_size,s);
});
app.components.x_avatar.model.parse_shape = (function app$components$x_avatar$model$parse_shape(s){
return app.components.x_avatar.model.parse_enum(app.components.x_avatar.model.valid_shapes,app.components.x_avatar.model.default_shape,s);
});
app.components.x_avatar.model.parse_variant = (function app$components$x_avatar$model$parse_variant(s){
return app.components.x_avatar.model.parse_enum(app.components.x_avatar.model.valid_variants,app.components.x_avatar.model.default_variant,s);
});
app.components.x_avatar.model.parse_status = (function app$components$x_avatar$model$parse_status(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_avatar.model.valid_statuses,v)){
return v;
} else {
return null;
}
});
app.components.x_avatar.model.normalize_text = (function app$components$x_avatar$model$normalize_text(s){
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
app.components.x_avatar.model.normalize_initials = (function app$components$x_avatar$model$normalize_initials(s){
var temp__5823__auto__ = app.components.x_avatar.model.normalize_text(s);
if(cljs.core.truth_(temp__5823__auto__)){
var v = temp__5823__auto__;
if((v.length > (3))){
return v.slice((0),(3));
} else {
return v;
}
} else {
return null;
}
});
app.components.x_avatar.model.derive_initials = (function app$components$x_avatar$model$derive_initials(name_str){
var temp__5823__auto__ = app.components.x_avatar.model.normalize_text(name_str);
if(cljs.core.truth_(temp__5823__auto__)){
var n = temp__5823__auto__;
var parts = cljs.core.vec(cljs.core.remove.cljs$core$IFn$_invoke$arity$2((function (p1__21311_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p1__21311_SHARP_,"");
}),n.split(/\s+/)));
if(cljs.core.seq(parts)){
var a = (cljs.core.first(parts)[(0)]);
var b = (((cljs.core.count(parts) >= (2)))?(cljs.core.second(parts)[(0)]):null);
var s = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(a)+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = b;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"")){
return null;
} else {
return s.toUpperCase();
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_avatar.model.normalize = (function app$components$x_avatar$model$normalize(p__21327){
var map__21328 = p__21327;
var map__21328__$1 = cljs.core.__destructure_map(map__21328);
var initials_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"initials-raw","initials-raw",1031756224));
var variant_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250));
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
var shape_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"shape-raw","shape-raw",1588938757));
var status_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"status-raw","status-raw",-2121061147));
var alt_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"alt-raw","alt-raw",-1423153400));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var src_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"src-raw","src-raw",-510472785));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21328__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var src = app.components.x_avatar.model.normalize_text(src_raw);
var alt = app.components.x_avatar.model.normalize_text(alt_raw);
var nm = app.components.x_avatar.model.normalize_text(name_raw);
var initials = app.components.x_avatar.model.normalize_initials(initials_raw);
var derived = (((initials == null))?app.components.x_avatar.model.derive_initials(nm):null);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"initials","initials",-2130377215),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"alt","alt",-3214426),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"src","src",-1651076051),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"derived","derived",1986040597),new cljs.core.Keyword(null,"shape","shape",1190694006)],[initials,cljs.core.boolean$(disabled_present_QMARK_),app.components.x_avatar.model.parse_variant(variant_raw),alt,nm,src,app.components.x_avatar.model.parse_size(size_raw),app.components.x_avatar.model.parse_status(status_raw),derived,app.components.x_avatar.model.parse_shape(shape_raw)]);
});
/**
 * Return the string to render in [part=initials], or nil (show fallback).
 */
app.components.x_avatar.model.display_text = (function app$components$x_avatar$model$display_text(p__21352){
var map__21353 = p__21352;
var map__21353__$1 = cljs.core.__destructure_map(map__21353);
var initials = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21353__$1,new cljs.core.Keyword(null,"initials","initials",-2130377215));
var derived = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21353__$1,new cljs.core.Keyword(null,"derived","derived",1986040597));
var or__5142__auto__ = initials;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return derived;
}
});
/**
 * Return the accessible label string, or nil (mark as aria-hidden).
 */
app.components.x_avatar.model.label = (function app$components$x_avatar$model$label(p__21354){
var map__21355 = p__21354;
var map__21355__$1 = cljs.core.__destructure_map(map__21355);
var alt = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21355__$1,new cljs.core.Keyword(null,"alt","alt",-3214426));
var name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21355__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var or__5142__auto__ = alt;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return name;
}
});
app.components.x_avatar.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"initials","initials",-2130377215),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"alt","alt",-3214426),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"src","src",-1651076051),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"shape","shape",1190694006)],[new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)]);

//# sourceMappingURL=app.components.x_avatar.model.js.map
