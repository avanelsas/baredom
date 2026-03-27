goog.provide('app.components.x_button.model');
app.components.x_button.model.tag_name = "x-button";
app.components.x_button.model.attr_disabled = "disabled";
app.components.x_button.model.attr_loading = "loading";
app.components.x_button.model.attr_pressed = "pressed";
app.components.x_button.model.attr_type = "type";
app.components.x_button.model.attr_variant = "variant";
app.components.x_button.model.attr_size = "size";
app.components.x_button.model.attr_label = "label";
app.components.x_button.model.event_press = "press";
app.components.x_button.model.event_press_start = "press-start";
app.components.x_button.model.event_press_end = "press-end";
app.components.x_button.model.event_hover_start = "hover-start";
app.components.x_button.model.event_hover_end = "hover-end";
app.components.x_button.model.event_focus_visible = "focus-visible";
app.components.x_button.model.slot_default = "default";
app.components.x_button.model.slot_icon_start = "icon-start";
app.components.x_button.model.slot_icon_end = "icon-end";
app.components.x_button.model.slot_spinner = "spinner";
app.components.x_button.model.default_type = "button";
app.components.x_button.model.default_variant = "primary";
app.components.x_button.model.default_size = "md";
app.components.x_button.model.allowed_types = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["reset",null,"submit",null,"button",null], null), null);
app.components.x_button.model.allowed_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["secondary",null,"tertiary",null,"primary",null,"danger",null,"ghost",null], null), null);
app.components.x_button.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_button.model.observed_attributes = [app.components.x_button.model.attr_disabled,app.components.x_button.model.attr_loading,app.components.x_button.model.attr_pressed,app.components.x_button.model.attr_type,app.components.x_button.model.attr_variant,app.components.x_button.model.attr_size,app.components.x_button.model.attr_label];
app.components.x_button.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_button.model.attr_disabled], null),new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_button.model.attr_loading], null),new cljs.core.Keyword(null,"pressed","pressed",1100937946),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_button.model.attr_pressed], null)], null);
app.components.x_button.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_button.model.event_press,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_button.model.event_press_start,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_button.model.event_press_end,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"source","source",-433931539),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_button.model.event_hover_start,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_button.model.event_hover_end,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_button.model.event_focus_visible,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null)]);
app.components.x_button.model.normalize_enum = (function app$components$x_button$model$normalize_enum(value,allowed,default_value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(allowed,value)))){
return value;
} else {
return default_value;
}
});
app.components.x_button.model.normalize_type = (function app$components$x_button$model$normalize_type(value){
return app.components.x_button.model.normalize_enum(value,app.components.x_button.model.allowed_types,app.components.x_button.model.default_type);
});
app.components.x_button.model.normalize_variant = (function app$components$x_button$model$normalize_variant(value){
return app.components.x_button.model.normalize_enum(value,app.components.x_button.model.allowed_variants,app.components.x_button.model.default_variant);
});
app.components.x_button.model.normalize_size = (function app$components$x_button$model$normalize_size(value){
return app.components.x_button.model.normalize_enum(value,app.components.x_button.model.allowed_sizes,app.components.x_button.model.default_size);
});
app.components.x_button.model.non_empty_string_QMARK_ = (function app$components$x_button$model$non_empty_string_QMARK_(value){
return ((typeof value === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",value)));
});
app.components.x_button.model.public_state = (function app$components$x_button$model$public_state(p__20450){
var map__20451 = p__20450;
var map__20451__$1 = cljs.core.__destructure_map(map__20451);
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var loading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"loading","loading",-737050189));
var pressed = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"pressed","pressed",1100937946));
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20451__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.boolean$(disabled),new cljs.core.Keyword(null,"loading","loading",-737050189),cljs.core.boolean$(loading),new cljs.core.Keyword(null,"pressed","pressed",1100937946),cljs.core.boolean$(pressed),new cljs.core.Keyword(null,"type","type",1174270348),app.components.x_button.model.normalize_type(type),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_button.model.normalize_variant(variant),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_button.model.normalize_size(size),new cljs.core.Keyword(null,"label","label",1718410804),((typeof label === 'string')?label:null)], null);
});
app.components.x_button.model.interactive_QMARK_ = (function app$components$x_button$model$interactive_QMARK_(p__20469){
var map__20474 = p__20469;
var map__20474__$1 = cljs.core.__destructure_map(map__20474);
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20474__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var loading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20474__$1,new cljs.core.Keyword(null,"loading","loading",-737050189));
return cljs.core.not((function (){var or__5142__auto__ = disabled;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return loading;
}
})());
});
app.components.x_button.model.aria_busy = (function app$components$x_button$model$aria_busy(p__20476){
var map__20477 = p__20476;
var map__20477__$1 = cljs.core.__destructure_map(map__20477);
var loading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20477__$1,new cljs.core.Keyword(null,"loading","loading",-737050189));
if(cljs.core.truth_(loading)){
return "true";
} else {
return null;
}
});
app.components.x_button.model.aria_label = (function app$components$x_button$model$aria_label(p__20482){
var map__20483 = p__20482;
var map__20483__$1 = cljs.core.__destructure_map(map__20483);
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20483__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var has_default_text_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20483__$1,new cljs.core.Keyword(null,"has-default-text?","has-default-text?",2021837624));
if(((cljs.core.not(has_default_text_QMARK_)) && (app.components.x_button.model.non_empty_string_QMARK_(label)))){
return label;
} else {
return null;
}
});

//# sourceMappingURL=app.components.x_button.model.js.map
