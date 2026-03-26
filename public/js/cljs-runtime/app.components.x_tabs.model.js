goog.provide('app.components.x_tabs.model');
app.components.x_tabs.model.tag_name = "x-tabs";
app.components.x_tabs.model.attr_value = "value";
app.components.x_tabs.model.attr_orientation = "orientation";
app.components.x_tabs.model.attr_activation = "activation";
app.components.x_tabs.model.attr_label = "label";
app.components.x_tabs.model.attr_loop = "loop";
app.components.x_tabs.model.observed_attributes = [app.components.x_tabs.model.attr_value,app.components.x_tabs.model.attr_orientation,app.components.x_tabs.model.attr_activation,app.components.x_tabs.model.attr_label,app.components.x_tabs.model.attr_loop];
app.components.x_tabs.model.orientation_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["vertical",null,"horizontal",null], null), null);
app.components.x_tabs.model.activation_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["auto",null,"manual",null], null), null);
app.components.x_tabs.model.event_value_change = "value-change";
app.components.x_tabs.model.property_api = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_tabs.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_tabs.model.event_value_change,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_tabs.model.valid_enum = (function app$components$x_tabs$model$valid_enum(v,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
app.components.x_tabs.model.normalize_orientation = (function app$components$x_tabs$model$normalize_orientation(v){
return app.components.x_tabs.model.valid_enum(v,app.components.x_tabs.model.orientation_values,"horizontal");
});
app.components.x_tabs.model.normalize_activation = (function app$components$x_tabs$model$normalize_activation(v){
return app.components.x_tabs.model.valid_enum(v,app.components.x_tabs.model.activation_values,"auto");
});
app.components.x_tabs.model.derive_state = (function app$components$x_tabs$model$derive_state(p__21311){
var map__21312 = p__21311;
var map__21312__$1 = cljs.core.__destructure_map(map__21312);
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21312__$1,new cljs.core.Keyword(null,"value","value",305978217));
var orientation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21312__$1,new cljs.core.Keyword(null,"orientation","orientation",623557579));
var activation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21312__$1,new cljs.core.Keyword(null,"activation","activation",2128521072));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21312__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var loop = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21312__$1,new cljs.core.Keyword(null,"loop","loop",-395552849));
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"value","value",305978217),value,new cljs.core.Keyword(null,"orientation","orientation",623557579),app.components.x_tabs.model.normalize_orientation(orientation),new cljs.core.Keyword(null,"activation","activation",2128521072),app.components.x_tabs.model.normalize_activation(activation),new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"loop","loop",-395552849),cljs.core.boolean$(loop)], null);
});

//# sourceMappingURL=app.components.x_tabs.model.js.map
