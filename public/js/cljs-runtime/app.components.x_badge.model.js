goog.provide('app.components.x_badge.model');
app.components.x_badge.model.tag_name = "x-badge";
app.components.x_badge.model.attr_variant = "variant";
app.components.x_badge.model.attr_size = "size";
app.components.x_badge.model.attr_pill = "pill";
app.components.x_badge.model.attr_dot = "dot";
app.components.x_badge.model.attr_count = "count";
app.components.x_badge.model.attr_max = "max";
app.components.x_badge.model.attr_text = "text";
app.components.x_badge.model.attr_aria_label = "aria-label";
app.components.x_badge.model.attr_aria_describedby = "aria-describedby";
app.components.x_badge.model.observed_attributes = [app.components.x_badge.model.attr_variant,app.components.x_badge.model.attr_size,app.components.x_badge.model.attr_pill,app.components.x_badge.model.attr_dot,app.components.x_badge.model.attr_count,app.components.x_badge.model.attr_max,app.components.x_badge.model.attr_text,app.components.x_badge.model.attr_aria_label,app.components.x_badge.model.attr_aria_describedby];
app.components.x_badge.model.valid_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["success",null,"neutral",null,"warning",null,"info",null,"error",null], null), null);
app.components.x_badge.model.valid_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["md",null,"sm",null], null), null);
app.components.x_badge.model.default_variant = "neutral";
app.components.x_badge.model.default_size = "md";
app.components.x_badge.model.default_max = (99);
app.components.x_badge.model.parse_enum = (function app$components$x_badge$model$parse_enum(valid,default$,s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(valid,v)){
return v;
} else {
return default$;
}
});
app.components.x_badge.model.parse_variant = (function app$components$x_badge$model$parse_variant(s){
return app.components.x_badge.model.parse_enum(app.components.x_badge.model.valid_variants,app.components.x_badge.model.default_variant,s);
});
app.components.x_badge.model.parse_size = (function app$components$x_badge$model$parse_size(s){
return app.components.x_badge.model.parse_enum(app.components.x_badge.model.valid_sizes,app.components.x_badge.model.default_size,s);
});
/**
 * Parse string to non-negative integer, returning `fallback` on failure.
 */
app.components.x_badge.model.parse_int_attr = (function app$components$x_badge$model$parse_int_attr(s,fallback){
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
return fallback;
}
} else {
return fallback;
}
});
app.components.x_badge.model.parse_bool_attr = (function app$components$x_badge$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Determine badge display mode.
 * :slot  — custom slotted content (overrides everything)
 * :count — numeric count display
 * :text  — string text display
 * :dot   — dot-only (no text)
 * :empty — hidden / no content
 */
app.components.x_badge.model.compute_mode = (function app$components$x_badge$model$compute_mode(p__21811){
var map__21812 = p__21811;
var map__21812__$1 = cljs.core.__destructure_map(map__21812);
var has_slot_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21812__$1,new cljs.core.Keyword(null,"has-slot?","has-slot?",181244965));
var count = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21812__$1,new cljs.core.Keyword(null,"count","count",2139924085));
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21812__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
var dot = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21812__$1,new cljs.core.Keyword(null,"dot","dot",1442709401));
if(cljs.core.truth_(has_slot_QMARK_)){
return new cljs.core.Keyword(null,"slot","slot",240229571);
} else {
if((!((count == null)))){
return new cljs.core.Keyword(null,"count","count",2139924085);
} else {
if((!((text == null)))){
return new cljs.core.Keyword(null,"text","text",-1790561697);
} else {
if(cljs.core.truth_(dot)){
return new cljs.core.Keyword(null,"dot","dot",1442709401);
} else {
return new cljs.core.Keyword(null,"empty","empty",767870958);

}
}
}
}
});
/**
 * Return the string to render in [part=label], or nil.
 */
app.components.x_badge.model.display_text = (function app$components$x_badge$model$display_text(p__21813){
var map__21814 = p__21813;
var map__21814__$1 = cljs.core.__destructure_map(map__21814);
var m = map__21814__$1;
var count = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21814__$1,new cljs.core.Keyword(null,"count","count",2139924085));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21814__$1,new cljs.core.Keyword(null,"max","max",61366548));
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21814__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
var G__21815 = app.components.x_badge.model.compute_mode(m);
var G__21815__$1 = (((G__21815 instanceof cljs.core.Keyword))?G__21815.fqn:null);
switch (G__21815__$1) {
case "count":
if((count > max)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max)+"+");
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(count));
}

break;
case "text":
return text;

break;
default:
return null;

}
});
app.components.x_badge.model.normalize = (function app$components$x_badge$model$normalize(p__21816){
var map__21817 = p__21816;
var map__21817__$1 = cljs.core.__destructure_map(map__21817);
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
var dot_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"dot-raw","dot-raw",1754908187));
var aria_describedby_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860));
var pill_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"pill-raw","pill-raw",-557846051));
var variant_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250));
var text_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"text-raw","text-raw",-124751068));
var has_slot_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"has-slot?","has-slot?",181244965));
var max_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"max-raw","max-raw",-434946611));
var count_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"count-raw","count-raw",-952861839));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21817__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var max_val = app.components.x_badge.model.parse_int_attr(max_raw,app.components.x_badge.model.default_max);
var count_val = (cljs.core.truth_(count_raw)?(function (){var n = app.components.x_badge.model.parse_int_attr(count_raw,null);
if((!((n == null)))){
return n;
} else {
return null;
}
})():null);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"has-slot?","has-slot?",181244965),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"pill","pill",-37707000),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"count","count",2139924085),new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),new cljs.core.Keyword(null,"dot","dot",1442709401),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),new cljs.core.Keyword(null,"text","text",-1790561697)],[cljs.core.boolean$(has_slot_QMARK_),app.components.x_badge.model.parse_variant(variant_raw),app.components.x_badge.model.parse_bool_attr(pill_raw),app.components.x_badge.model.parse_size(size_raw),max_val,count_val,aria_describedby_raw,app.components.x_badge.model.parse_bool_attr(dot_raw),aria_label_raw,((typeof text_raw === 'string')?(function (){var v = text_raw.trim();
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(v,"")){
return null;
} else {
return v;
}
})():null)]);
});
app.components.x_badge.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"pill","pill",-37707000),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"dot","dot",1442709401),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"count","count",2139924085),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);

//# sourceMappingURL=app.components.x_badge.model.js.map
