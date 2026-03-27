goog.provide('app.components.x_tab.model');
app.components.x_tab.model.tag_name = "x-tab";
app.components.x_tab.model.attr_value = "value";
app.components.x_tab.model.attr_selected = "selected";
app.components.x_tab.model.attr_disabled = "disabled";
app.components.x_tab.model.attr_orientation = "orientation";
app.components.x_tab.model.attr_size = "size";
app.components.x_tab.model.attr_variant = "variant";
app.components.x_tab.model.attr_label = "label";
app.components.x_tab.model.attr_controls = "controls";
app.components.x_tab.model.observed_attributes = [app.components.x_tab.model.attr_value,app.components.x_tab.model.attr_selected,app.components.x_tab.model.attr_disabled,app.components.x_tab.model.attr_orientation,app.components.x_tab.model.attr_size,app.components.x_tab.model.attr_variant,app.components.x_tab.model.attr_label,app.components.x_tab.model.attr_controls];
app.components.x_tab.model.orientation_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["vertical",null,"horizontal",null], null), null);
app.components.x_tab.model.size_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_tab.model.variant_values = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["default",null,"underline",null,"pill",null], null), null);
app.components.x_tab.model.event_tab_select = "tab-select";
app.components.x_tab.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_tab.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_tab.model.event_tab_select,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_tab.model.valid_enum = (function app$components$x_tab$model$valid_enum(v,allowed,fallback){
if(cljs.core.contains_QMARK_(allowed,v)){
return v;
} else {
return fallback;
}
});
app.components.x_tab.model.normalize_orientation = (function app$components$x_tab$model$normalize_orientation(v){
return app.components.x_tab.model.valid_enum(v,app.components.x_tab.model.orientation_values,"horizontal");
});
app.components.x_tab.model.normalize_size = (function app$components$x_tab$model$normalize_size(v){
return app.components.x_tab.model.valid_enum(v,app.components.x_tab.model.size_values,"md");
});
app.components.x_tab.model.normalize_variant = (function app$components$x_tab$model$normalize_variant(v){
return app.components.x_tab.model.valid_enum(v,app.components.x_tab.model.variant_values,"default");
});
app.components.x_tab.model.derive_state = (function app$components$x_tab$model$derive_state(p__21019){
var map__21020 = p__21019;
var map__21020__$1 = cljs.core.__destructure_map(map__21020);
var selected = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"selected","selected",574897764));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var orientation = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"orientation","orientation",623557579));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var controls = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21020__$1,new cljs.core.Keyword(null,"controls","controls",1340701452));
var selected_STAR_ = cljs.core.boolean$(selected);
var disabled_STAR_ = cljs.core.boolean$(disabled);
var tabindex = ((disabled_STAR_)?"-1":((selected_STAR_)?"0":"-1"
));
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"selected","selected",574897764),selected_STAR_,new cljs.core.Keyword(null,"disabled","disabled",-1529784218),disabled_STAR_,new cljs.core.Keyword(null,"orientation","orientation",623557579),app.components.x_tab.model.normalize_orientation(orientation),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_tab.model.normalize_size(size),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_tab.model.normalize_variant(variant),new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"controls","controls",1340701452),controls,new cljs.core.Keyword(null,"tabindex","tabindex",338877510),tabindex], null);
});

//# sourceMappingURL=app.components.x_tab.model.js.map
