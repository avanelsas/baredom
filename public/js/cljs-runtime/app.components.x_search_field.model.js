goog.provide('app.components.x_search_field.model');
app.components.x_search_field.model.tag_name = "x-search-field";
app.components.x_search_field.model.attr_name = "name";
app.components.x_search_field.model.attr_value = "value";
app.components.x_search_field.model.attr_placeholder = "placeholder";
app.components.x_search_field.model.attr_label = "label";
app.components.x_search_field.model.attr_disabled = "disabled";
app.components.x_search_field.model.attr_required = "required";
app.components.x_search_field.model.attr_autocomplete = "autocomplete";
app.components.x_search_field.model.event_input = "x-search-field-input";
app.components.x_search_field.model.event_change = "x-search-field-change";
app.components.x_search_field.model.event_search = "x-search-field-search";
app.components.x_search_field.model.event_clear = "x-search-field-clear";
app.components.x_search_field.model.observed_attributes = [app.components.x_search_field.model.attr_name,app.components.x_search_field.model.attr_value,app.components.x_search_field.model.attr_placeholder,app.components.x_search_field.model.attr_label,app.components.x_search_field.model.attr_disabled,app.components.x_search_field.model.attr_required,app.components.x_search_field.model.attr_autocomplete];
app.components.x_search_field.model.allowed_autocomplete = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["off",null,"on",null], null), null);
app.components.x_search_field.model.normalize_autocomplete = (function app$components$x_search_field$model$normalize_autocomplete(raw){
if(((typeof raw === 'string') && (cljs.core.contains_QMARK_(app.components.x_search_field.model.allowed_autocomplete,raw)))){
return raw;
} else {
return "off";
}
});
app.components.x_search_field.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_value], null),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_name], null),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_placeholder], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_label], null),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_autocomplete], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_disabled], null),new cljs.core.Keyword(null,"required","required",1807647006),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_search_field.model.attr_required], null)], null);
app.components.x_search_field.model.event_schema = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_search_field.model.event_input,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null),new cljs.core.Keyword(null,"change","change",-1163046502),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_search_field.model.event_change,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null),new cljs.core.Keyword(null,"search","search",1564939822),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_search_field.model.event_search,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null),new cljs.core.Keyword(null,"clear","clear",1877104959),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_search_field.model.event_clear,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"name","name",1843675177),null], null), null)], null)], null);
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_search_field.model.normalize = (function app$components$x_search_field$model$normalize(p__22694){
var map__22695 = p__22694;
var map__22695__$1 = cljs.core.__destructure_map(map__22695);
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var placeholder_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var required_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196));
var autocomplete_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22695__$1,new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710));
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"name","name",1843675177),(function (){var or__5142__auto__ = name_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"value","value",305978217),(function (){var or__5142__auto__ = value_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),(function (){var or__5142__auto__ = placeholder_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"required?","required?",-872514462),cljs.core.boolean$(required_present_QMARK_),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),app.components.x_search_field.model.normalize_autocomplete(autocomplete_raw)], null);
});

//# sourceMappingURL=app.components.x_search_field.model.js.map
