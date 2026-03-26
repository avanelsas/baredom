goog.provide('app.components.x_navbar.model');
app.components.x_navbar.model.tag_name = "x-navbar";
app.components.x_navbar.model.attr_label = "label";
app.components.x_navbar.model.attr_orientation = "orientation";
app.components.x_navbar.model.attr_variant = "variant";
app.components.x_navbar.model.attr_sticky = "sticky";
app.components.x_navbar.model.attr_elevated = "elevated";
app.components.x_navbar.model.attr_breakpoint = "breakpoint";
app.components.x_navbar.model.attr_alignment = "alignment";
app.components.x_navbar.model.event_focus_visible = "focus-visible";
app.components.x_navbar.model.event_navigate = "navigate";
app.components.x_navbar.model.event_brand_activate = "brand-activate";
app.components.x_navbar.model.slot_brand = "brand";
app.components.x_navbar.model.slot_actions = "actions";
app.components.x_navbar.model.slot_toggle = "toggle";
app.components.x_navbar.model.slot_start = "start";
app.components.x_navbar.model.slot_end = "end";
app.components.x_navbar.model.default_orientation = "horizontal";
app.components.x_navbar.model.default_variant = "default";
app.components.x_navbar.model.default_breakpoint = "md";
app.components.x_navbar.model.default_alignment = "space-between";
app.components.x_navbar.model.allowed_orientations = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["vertical",null,"horizontal",null], null), null);
app.components.x_navbar.model.allowed_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["transparent",null,"inverted",null,"subtle",null,"default",null], null), null);
app.components.x_navbar.model.allowed_breakpoints = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_navbar.model.allowed_alignments = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["center",null,"start",null,"space-between",null], null), null);
app.components.x_navbar.model.observed_attributes = [app.components.x_navbar.model.attr_label,app.components.x_navbar.model.attr_orientation,app.components.x_navbar.model.attr_variant,app.components.x_navbar.model.attr_sticky,app.components.x_navbar.model.attr_elevated,app.components.x_navbar.model.attr_breakpoint,app.components.x_navbar.model.attr_alignment];
app.components.x_navbar.model.property_api = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"sticky","sticky",-2121213869),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_navbar.model.attr_sticky], null),new cljs.core.Keyword(null,"elevated","elevated",-7323953),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_navbar.model.attr_elevated], null)], null);
app.components.x_navbar.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_navbar.model.event_focus_visible,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_navbar.model.event_navigate,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"href","href",-793805698),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_navbar.model.event_brand_activate,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_navbar.model.slot_names = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"brand","brand",557863343),app.components.x_navbar.model.slot_brand,new cljs.core.Keyword(null,"actions","actions",-812656882),app.components.x_navbar.model.slot_actions,new cljs.core.Keyword(null,"toggle","toggle",1291842030),app.components.x_navbar.model.slot_toggle,new cljs.core.Keyword(null,"start","start",-355208981),app.components.x_navbar.model.slot_start,new cljs.core.Keyword(null,"end","end",-268185958),app.components.x_navbar.model.slot_end,new cljs.core.Keyword(null,"default","default",-1987822328),"default"], null);
app.components.x_navbar.model.normalize_enum = (function app$components$x_navbar$model$normalize_enum(value,allowed,default_value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(allowed,value)))){
return value;
} else {
return default_value;
}
});
app.components.x_navbar.model.normalize_orientation = (function app$components$x_navbar$model$normalize_orientation(value){
return app.components.x_navbar.model.normalize_enum(value,app.components.x_navbar.model.allowed_orientations,app.components.x_navbar.model.default_orientation);
});
app.components.x_navbar.model.normalize_variant = (function app$components$x_navbar$model$normalize_variant(value){
return app.components.x_navbar.model.normalize_enum(value,app.components.x_navbar.model.allowed_variants,app.components.x_navbar.model.default_variant);
});
app.components.x_navbar.model.normalize_breakpoint = (function app$components$x_navbar$model$normalize_breakpoint(value){
return app.components.x_navbar.model.normalize_enum(value,app.components.x_navbar.model.allowed_breakpoints,app.components.x_navbar.model.default_breakpoint);
});
app.components.x_navbar.model.normalize_alignment = (function app$components$x_navbar$model$normalize_alignment(value){
return app.components.x_navbar.model.normalize_enum(value,app.components.x_navbar.model.allowed_alignments,app.components.x_navbar.model.default_alignment);
});
app.components.x_navbar.model.non_empty_string_QMARK_ = (function app$components$x_navbar$model$non_empty_string_QMARK_(value){
return ((typeof value === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",value)));
});
app.components.x_navbar.model.public_state = (function app$components$x_navbar$model$public_state(p__20958){
var map__20959 = p__20958;
var map__20959__$1 = cljs.core.__destructure_map(map__20959);
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var orientation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"orientation","orientation",623557579));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var sticky = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"sticky","sticky",-2121213869));
var elevated = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"elevated","elevated",-7323953));
var breakpoint = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440));
var alignment = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20959__$1,new cljs.core.Keyword(null,"alignment","alignment",1040093386));
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"label","label",1718410804),((typeof label === 'string')?label:null),new cljs.core.Keyword(null,"orientation","orientation",623557579),app.components.x_navbar.model.normalize_orientation(orientation),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_navbar.model.normalize_variant(variant),new cljs.core.Keyword(null,"sticky","sticky",-2121213869),cljs.core.boolean$(sticky),new cljs.core.Keyword(null,"elevated","elevated",-7323953),cljs.core.boolean$(elevated),new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440),app.components.x_navbar.model.normalize_breakpoint(breakpoint),new cljs.core.Keyword(null,"alignment","alignment",1040093386),app.components.x_navbar.model.normalize_alignment(alignment)], null);
});
app.components.x_navbar.model.landmark_label = (function app$components$x_navbar$model$landmark_label(p__20961){
var map__20968 = p__20961;
var map__20968__$1 = cljs.core.__destructure_map(map__20968);
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20968__$1,new cljs.core.Keyword(null,"label","label",1718410804));
if(app.components.x_navbar.model.non_empty_string_QMARK_(label)){
return label;
} else {
return null;
}
});

//# sourceMappingURL=app.components.x_navbar.model.js.map
