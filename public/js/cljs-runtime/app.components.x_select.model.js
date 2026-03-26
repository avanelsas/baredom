goog.provide('app.components.x_select.model');
app.components.x_select.model.tag_name = "x-select";
app.components.x_select.model.attr_disabled = "disabled";
app.components.x_select.model.attr_required = "required";
app.components.x_select.model.attr_size = "size";
app.components.x_select.model.attr_placeholder = "placeholder";
app.components.x_select.model.attr_value = "value";
app.components.x_select.model.attr_name = "name";
app.components.x_select.model.event_select_change = "select-change";
app.components.x_select.model.default_size = "md";
app.components.x_select.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_select.model.observed_attributes = [app.components.x_select.model.attr_disabled,app.components.x_select.model.attr_required,app.components.x_select.model.attr_size,app.components.x_select.model.attr_placeholder,app.components.x_select.model.attr_value,app.components.x_select.model.attr_name];
app.components.x_select.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_select.model.attr_disabled], null),new cljs.core.Keyword(null,"required","required",1807647006),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_select.model.attr_required], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_select.model.attr_value], null)], null);
app.components.x_select.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_select.model.event_select_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
/**
 * Returns value if it is in allowed, otherwise default-value.
 */
app.components.x_select.model.normalize_enum = (function app$components$x_select$model$normalize_enum(value,allowed,default_value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(allowed,value)))){
return value;
} else {
return default_value;
}
});
app.components.x_select.model.normalize_size = (function app$components$x_select$model$normalize_size(v){
return app.components.x_select.model.normalize_enum(v,app.components.x_select.model.allowed_sizes,app.components.x_select.model.default_size);
});
/**
 * Derives a complete view-model map from raw attribute presence/values.
 */
app.components.x_select.model.normalize = (function app$components$x_select$model$normalize(p__23036){
var map__23037 = p__23036;
var map__23037__$1 = cljs.core.__destructure_map(map__23037);
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var required_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var placeholder_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23037__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"required?","required?",-872514462),cljs.core.boolean$(required_present_QMARK_),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_select.model.normalize_size(size_raw),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),placeholder_raw,new cljs.core.Keyword(null,"value","value",305978217),value_raw,new cljs.core.Keyword(null,"name","name",1843675177),name_raw], null);
});

//# sourceMappingURL=app.components.x_select.model.js.map
