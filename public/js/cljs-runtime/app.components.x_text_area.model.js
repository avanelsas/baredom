goog.provide('app.components.x_text_area.model');
app.components.x_text_area.model.tag_name = "x-text-area";
app.components.x_text_area.model.attr_label = "label";
app.components.x_text_area.model.attr_name = "name";
app.components.x_text_area.model.attr_value = "value";
app.components.x_text_area.model.attr_placeholder = "placeholder";
app.components.x_text_area.model.attr_hint = "hint";
app.components.x_text_area.model.attr_error = "error";
app.components.x_text_area.model.attr_disabled = "disabled";
app.components.x_text_area.model.attr_readonly = "readonly";
app.components.x_text_area.model.attr_required = "required";
app.components.x_text_area.model.attr_rows = "rows";
app.components.x_text_area.model.attr_maxlength = "maxlength";
app.components.x_text_area.model.attr_minlength = "minlength";
app.components.x_text_area.model.attr_autocomplete = "autocomplete";
app.components.x_text_area.model.attr_resize = "resize";
app.components.x_text_area.model.event_input = "x-text-area-input";
app.components.x_text_area.model.event_change = "x-text-area-change";
app.components.x_text_area.model.observed_attributes = [app.components.x_text_area.model.attr_label,app.components.x_text_area.model.attr_name,app.components.x_text_area.model.attr_value,app.components.x_text_area.model.attr_placeholder,app.components.x_text_area.model.attr_hint,app.components.x_text_area.model.attr_error,app.components.x_text_area.model.attr_disabled,app.components.x_text_area.model.attr_readonly,app.components.x_text_area.model.attr_required,app.components.x_text_area.model.attr_rows,app.components.x_text_area.model.attr_maxlength,app.components.x_text_area.model.attr_minlength,app.components.x_text_area.model.attr_autocomplete,app.components.x_text_area.model.attr_resize];
app.components.x_text_area.model.allowed_resize = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["none",null,"vertical",null,"both",null,"horizontal",null], null), null);
app.components.x_text_area.model.normalize_resize = (function app$components$x_text_area$model$normalize_resize(raw){
if(((typeof raw === 'string') && (cljs.core.contains_QMARK_(app.components.x_text_area.model.allowed_resize,raw)))){
return raw;
} else {
return "vertical";
}
});
app.components.x_text_area.model.parse_positive_int = (function app$components$x_text_area$model$parse_positive_int(raw){
if(typeof raw === 'string'){
var n = parseInt(raw,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return n;
} else {
return null;
}
} else {
return null;
}
});
app.components.x_text_area.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"minLength","minLength",-1538722770),new cljs.core.Keyword(null,"rows","rows",850049680),new cljs.core.Keyword(null,"readOnly","readOnly",-1749118317),new cljs.core.Keyword(null,"maxLength","maxLength",-1633020073),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),new cljs.core.Keyword(null,"required","required",1807647006)],[new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_disabled], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_name], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_value], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_minlength], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_rows], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_readonly], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_maxlength], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_autocomplete], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_text_area.model.attr_required], null)]);
app.components.x_text_area.model.event_schema = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_text_area.model.event_input,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null),new cljs.core.Keyword(null,"change","change",-1163046502),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_text_area.model.event_change,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null)], null);
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_text_area.model.normalize = (function app$components$x_text_area$model$normalize(p__23275){
var map__23277 = p__23275;
var map__23277__$1 = cljs.core.__destructure_map(map__23277);
var minlength_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"minlength-raw","minlength-raw",-1245403779));
var placeholder_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657));
var resize_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"resize-raw","resize-raw",-287517888));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
var error_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"error-raw","error-raw",-1164358971));
var hint_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"hint-raw","hint-raw",-503443994));
var rows_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"rows-raw","rows-raw",-1828357145));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var maxlength_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"maxlength-raw","maxlength-raw",-886656470));
var required_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196));
var readonly_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var autocomplete_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23277__$1,new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710));
var error = (function (){var or__5142__auto__ = error_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var hint = (function (){var or__5142__auto__ = hint_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var label = (function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"required?","required?",-872514462),new cljs.core.Keyword(null,"readonly?","readonly?",988057827),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),new cljs.core.Keyword(null,"has-hint?","has-hint?",-60700378),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"minlength","minlength",259053545),new cljs.core.Keyword(null,"has-label?","has-label?",-1686997398),new cljs.core.Keyword(null,"has-error?","has-error?",1984278765),new cljs.core.Keyword(null,"resize","resize",297367086),new cljs.core.Keyword(null,"hint","hint",439639918),new cljs.core.Keyword(null,"maxlength","maxlength",-1163911985),new cljs.core.Keyword(null,"rows","rows",850049680),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"error","error",-978969032),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181)],[cljs.core.boolean$(required_present_QMARK_),cljs.core.boolean$(readonly_present_QMARK_),(function (){var or__5142__auto__ = placeholder_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),((typeof hint_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(hint_raw,"")) && ((!((hint_raw == null))))))),(function (){var or__5142__auto__ = name_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),(function (){var or__5142__auto__ = value_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),app.components.x_text_area.model.parse_positive_int(minlength_raw),((typeof label_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label_raw,"")) && ((!((label_raw == null))))))),((typeof error_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(error_raw,"")) && ((!((error_raw == null))))))),app.components.x_text_area.model.normalize_resize(resize_raw),hint,app.components.x_text_area.model.parse_positive_int(maxlength_raw),(function (){var or__5142__auto__ = app.components.x_text_area.model.parse_positive_int(rows_raw);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (3);
}
})(),label,error,(function (){var or__5142__auto__ = autocomplete_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),cljs.core.boolean$(disabled_present_QMARK_)]);
});

//# sourceMappingURL=app.components.x_text_area.model.js.map
