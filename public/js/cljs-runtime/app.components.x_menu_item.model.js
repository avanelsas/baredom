goog.provide('app.components.x_menu_item.model');
app.components.x_menu_item.model.tag_name = "x-menu-item";
app.components.x_menu_item.model.attr_value = "value";
app.components.x_menu_item.model.attr_disabled = "disabled";
app.components.x_menu_item.model.attr_variant = "variant";
app.components.x_menu_item.model.attr_type = "type";
app.components.x_menu_item.model.observed_attributes = [app.components.x_menu_item.model.attr_value,app.components.x_menu_item.model.attr_disabled,app.components.x_menu_item.model.attr_variant,app.components.x_menu_item.model.attr_type];
app.components.x_menu_item.model.variant_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["",null,"danger",null], null), null);
app.components.x_menu_item.model.type_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["",null,"divider",null], null), null);
app.components.x_menu_item.model.event_item_select = "x-menu-item-select";
app.components.x_menu_item.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_menu_item.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_menu_item.model.event_item_select,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_menu_item.model.valid_enum = (function app$components$x_menu_item$model$valid_enum(v,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
app.components.x_menu_item.model.normalize_variant = (function app$components$x_menu_item$model$normalize_variant(v){
return app.components.x_menu_item.model.valid_enum((function (){var or__5142__auto__ = v;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),app.components.x_menu_item.model.variant_values,"");
});
app.components.x_menu_item.model.normalize_type = (function app$components$x_menu_item$model$normalize_type(v){
return app.components.x_menu_item.model.valid_enum((function (){var or__5142__auto__ = v;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),app.components.x_menu_item.model.type_values,"");
});
app.components.x_menu_item.model.derive_state = (function app$components$x_menu_item$model$derive_state(p__22755){
var map__22756 = p__22755;
var map__22756__$1 = cljs.core.__destructure_map(map__22756);
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22756__$1,new cljs.core.Keyword(null,"value","value",305978217));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22756__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22756__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22756__$1,new cljs.core.Keyword(null,"type","type",1174270348));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),(function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.boolean$(disabled),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_menu_item.model.normalize_variant(variant),new cljs.core.Keyword(null,"type","type",1174270348),app.components.x_menu_item.model.normalize_type(type)], null);
});

//# sourceMappingURL=app.components.x_menu_item.model.js.map
