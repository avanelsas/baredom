goog.provide('app.components.x_menu.model');
app.components.x_menu.model.tag_name = "x-menu";
app.components.x_menu.model.attr_open = "open";
app.components.x_menu.model.attr_placement = "placement";
app.components.x_menu.model.attr_label = "label";
app.components.x_menu.model.observed_attributes = [app.components.x_menu.model.attr_open,app.components.x_menu.model.attr_placement,app.components.x_menu.model.attr_label];
app.components.x_menu.model.placement_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["bottom-start",null,"bottom-end",null,"top-end",null,"top-start",null], null), null);
app.components.x_menu.model.event_open = "x-menu-open";
app.components.x_menu.model.event_close = "x-menu-close";
app.components.x_menu.model.event_select = "x-menu-select";
app.components.x_menu.model.event_item_select = "x-menu-item-select";
app.components.x_menu.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"placement","placement",768366651),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_menu.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_menu.model.event_open,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_menu.model.event_close,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_menu.model.event_select,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_menu.model.valid_enum = (function app$components$x_menu$model$valid_enum(v,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
app.components.x_menu.model.normalize_placement = (function app$components$x_menu$model$normalize_placement(v){
return app.components.x_menu.model.valid_enum((function (){var or__5142__auto__ = v;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),app.components.x_menu.model.placement_values,"bottom-start");
});
app.components.x_menu.model.derive_state = (function app$components$x_menu$model$derive_state(p__22742){
var map__22743 = p__22742;
var map__22743__$1 = cljs.core.__destructure_map(map__22743);
var open = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22743__$1,new cljs.core.Keyword(null,"open","open",-1763596448));
var placement = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22743__$1,new cljs.core.Keyword(null,"placement","placement",768366651));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22743__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open","open",-1763596448),cljs.core.boolean$(open),new cljs.core.Keyword(null,"placement","placement",768366651),app.components.x_menu.model.normalize_placement(placement),new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__5142__auto__ = label;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()], null);
});

//# sourceMappingURL=app.components.x_menu.model.js.map
