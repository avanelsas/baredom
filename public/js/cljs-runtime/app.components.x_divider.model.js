goog.provide('app.components.x_divider.model');
app.components.x_divider.model.tag_name = "x-divider";
app.components.x_divider.model.attr_orientation = "orientation";
app.components.x_divider.model.attr_variant = "variant";
app.components.x_divider.model.attr_thickness = "thickness";
app.components.x_divider.model.attr_color = "color";
app.components.x_divider.model.attr_inset = "inset";
app.components.x_divider.model.attr_length = "length";
app.components.x_divider.model.attr_label = "label";
app.components.x_divider.model.attr_align = "align";
app.components.x_divider.model.attr_role = "role";
app.components.x_divider.model.attr_aria_label = "aria-label";
app.components.x_divider.model.observed_attributes = [app.components.x_divider.model.attr_orientation,app.components.x_divider.model.attr_variant,app.components.x_divider.model.attr_thickness,app.components.x_divider.model.attr_color,app.components.x_divider.model.attr_inset,app.components.x_divider.model.attr_length,app.components.x_divider.model.attr_label,app.components.x_divider.model.attr_align,app.components.x_divider.model.attr_role,app.components.x_divider.model.attr_aria_label];
app.components.x_divider.model.valid_orientations = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["vertical",null,"horizontal",null], null), null);
app.components.x_divider.model.valid_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["dashed",null,"dotted",null,"solid",null], null), null);
app.components.x_divider.model.valid_aligns = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["center",null,"start",null,"end",null], null), null);
app.components.x_divider.model.default_orientation = "horizontal";
app.components.x_divider.model.default_variant = "solid";
app.components.x_divider.model.default_align = "center";
app.components.x_divider.model.parse_enum = (function app$components$x_divider$model$parse_enum(valid,default$,s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(valid,v)){
return v;
} else {
return default$;
}
});
app.components.x_divider.model.parse_orientation = (function app$components$x_divider$model$parse_orientation(s){
return app.components.x_divider.model.parse_enum(app.components.x_divider.model.valid_orientations,app.components.x_divider.model.default_orientation,s);
});
app.components.x_divider.model.parse_variant = (function app$components$x_divider$model$parse_variant(s){
return app.components.x_divider.model.parse_enum(app.components.x_divider.model.valid_variants,app.components.x_divider.model.default_variant,s);
});
app.components.x_divider.model.parse_align = (function app$components$x_divider$model$parse_align(s){
return app.components.x_divider.model.parse_enum(app.components.x_divider.model.valid_aligns,app.components.x_divider.model.default_align,s);
});
/**
 * Returns true when the divider should carry role=separator.
 * False when role is 'presentation' or 'none'.
 */
app.components.x_divider.model.separator_role_QMARK_ = (function app$components$x_divider$model$separator_role_QMARK_(role){
return (!(cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["none",null,"presentation",null], null), null),role)));
});
/**
 * Returns true when label is a non-empty string.
 */
app.components.x_divider.model.has_label_QMARK_ = (function app$components$x_divider$model$has_label_QMARK_(label){
return ((typeof label === 'string') && ((label.trim().length > (0))));
});
app.components.x_divider.model.normalize = (function app$components$x_divider$model$normalize(p__22505){
var map__22506 = p__22505;
var map__22506__$1 = cljs.core.__destructure_map(map__22506);
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
var role_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"role-raw","role-raw",777951354));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var variant_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250));
var align_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"align-raw","align-raw",-723387357));
var length_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"length-raw","length-raw",-1832244116));
var thickness_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"thickness-raw","thickness-raw",1843812655));
var color_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"color-raw","color-raw",1277647215));
var inset_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"inset-raw","inset-raw",89351124));
var orientation_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22506__$1,new cljs.core.Keyword(null,"orientation-raw","orientation-raw",-471053928));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"role","role",-736691072),new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.Keyword(null,"thickness","thickness",-940175454),new cljs.core.Keyword(null,"inset","inset",-396367740),new cljs.core.Keyword(null,"color","color",1011675173),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"orientation","orientation",623557579),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"length","length",588987862),new cljs.core.Keyword(null,"aria-label","aria-label",455891514)],[role_raw,app.components.x_divider.model.parse_align(align_raw),thickness_raw,inset_raw,color_raw,app.components.x_divider.model.parse_variant(variant_raw),app.components.x_divider.model.parse_orientation(orientation_raw),label_raw,length_raw,aria_label_raw]);
});
app.components.x_divider.model.property_api = new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"orientation","orientation",623557579),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"align","align",1964212802),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"thickness","thickness",-940175454),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"color","color",1011675173),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"inset","inset",-396367740),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"length","length",588987862),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);

//# sourceMappingURL=app.components.x_divider.model.js.map
