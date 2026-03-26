goog.provide('app.components.x_currency_field.model');
app.components.x_currency_field.model.tag_name = "x-currency-field";
app.components.x_currency_field.model.attr_name = "name";
app.components.x_currency_field.model.attr_value = "value";
app.components.x_currency_field.model.attr_currency = "currency";
app.components.x_currency_field.model.attr_locale = "locale";
app.components.x_currency_field.model.attr_min = "min";
app.components.x_currency_field.model.attr_max = "max";
app.components.x_currency_field.model.attr_placeholder = "placeholder";
app.components.x_currency_field.model.attr_label = "label";
app.components.x_currency_field.model.attr_hint = "hint";
app.components.x_currency_field.model.attr_error = "error";
app.components.x_currency_field.model.attr_disabled = "disabled";
app.components.x_currency_field.model.attr_required = "required";
app.components.x_currency_field.model.attr_readonly = "readonly";
app.components.x_currency_field.model.event_input = "x-currency-field-input";
app.components.x_currency_field.model.event_change = "x-currency-field-change";
app.components.x_currency_field.model.observed_attributes = [app.components.x_currency_field.model.attr_name,app.components.x_currency_field.model.attr_value,app.components.x_currency_field.model.attr_currency,app.components.x_currency_field.model.attr_locale,app.components.x_currency_field.model.attr_min,app.components.x_currency_field.model.attr_max,app.components.x_currency_field.model.attr_placeholder,app.components.x_currency_field.model.attr_label,app.components.x_currency_field.model.attr_hint,app.components.x_currency_field.model.attr_error,app.components.x_currency_field.model.attr_disabled,app.components.x_currency_field.model.attr_required,app.components.x_currency_field.model.attr_readonly];
app.components.x_currency_field.model.normalize_currency = (function app$components$x_currency_field$model$normalize_currency(raw){
var s = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})())).trim().toUpperCase();
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"")){
return "USD";
} else {
return s;
}
});
app.components.x_currency_field.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_value], null),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_name], null),new cljs.core.Keyword(null,"currency","currency",-898327568),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_currency], null),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_locale], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_disabled], null),new cljs.core.Keyword(null,"readonly","readonly",-1101398934),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_readonly], null),new cljs.core.Keyword(null,"required","required",1807647006),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_currency_field.model.attr_required], null)], null);
app.components.x_currency_field.model.event_schema = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_currency_field.model.event_input,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null),new cljs.core.Keyword(null,"change","change",-1163046502),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_currency_field.model.event_change,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"value","value",305978217),null], null), null)], null)], null);
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_currency_field.model.normalize = (function app$components$x_currency_field$model$normalize(p__22696){
var map__22697 = p__22696;
var map__22697__$1 = cljs.core.__destructure_map(map__22697);
var placeholder_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var currency_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"currency-raw","currency-raw",1169887491));
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
var error_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"error-raw","error-raw",-1164358971));
var hint_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"hint-raw","hint-raw",-503443994));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var min_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"min-raw","min-raw",183137548));
var required_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196));
var max_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"max-raw","max-raw",-434946611));
var readonly_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var locale_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22697__$1,new cljs.core.Keyword(null,"locale-raw","locale-raw",385401846));
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
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Keyword(null,"required?","required?",-872514462),new cljs.core.Keyword(null,"readonly?","readonly?",988057827),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),new cljs.core.Keyword(null,"has-hint?","has-hint?",-60700378),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"has-label?","has-label?",-1686997398),new cljs.core.Keyword(null,"has-error?","has-error?",1984278765),new cljs.core.Keyword(null,"hint","hint",439639918),new cljs.core.Keyword(null,"currency","currency",-898327568),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"error","error",-978969032),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181)],[(function (){var or__5142__auto__ = min_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),cljs.core.boolean$(required_present_QMARK_),cljs.core.boolean$(readonly_present_QMARK_),(function (){var or__5142__auto__ = placeholder_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),((typeof hint_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(hint_raw,"")) && ((!((hint_raw == null))))))),(function (){var or__5142__auto__ = locale_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),(function (){var or__5142__auto__ = name_raw;
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
})(),((typeof label_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label_raw,"")) && ((!((label_raw == null))))))),((typeof error_raw === 'string') && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(error_raw,"")) && ((!((error_raw == null))))))),hint,app.components.x_currency_field.model.normalize_currency(currency_raw),(function (){var or__5142__auto__ = max_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),label,error,cljs.core.boolean$(disabled_present_QMARK_)]);
});
/**
 * Returns a computed validation error string or empty string.
 * Priority: badInput > rangeUnderflow > rangeOverflow.
 */
app.components.x_currency_field.model.validation_message = (function app$components$x_currency_field$model$validation_message(p__22698){
var map__22699 = p__22698;
var map__22699__$1 = cljs.core.__destructure_map(map__22699);
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22699__$1,new cljs.core.Keyword(null,"value","value",305978217));
var min = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22699__$1,new cljs.core.Keyword(null,"min","min",444991522));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22699__$1,new cljs.core.Keyword(null,"max","max",61366548));
var num = parseFloat(value);
var has_num_QMARK_ = ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(value,"")) && (cljs.core.not(isNaN(num))));
var min_num = ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(min,""))?parseFloat(min):null);
var max_num = ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(max,""))?parseFloat(max):null);
if(((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(value,"")) && ((!(has_num_QMARK_))))){
return "Please enter a valid number.";
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = has_num_QMARK_;
if(and__5140__auto__){
var and__5140__auto____$1 = min_num;
if(cljs.core.truth_(and__5140__auto____$1)){
return ((cljs.core.not(isNaN(min_num))) && ((num < min_num)));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return (""+"Value must be at least "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(min)+".");
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = has_num_QMARK_;
if(and__5140__auto__){
var and__5140__auto____$1 = max_num;
if(cljs.core.truth_(and__5140__auto____$1)){
return ((cljs.core.not(isNaN(max_num))) && ((num > max_num)));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return (""+"Value must be at most "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(max)+".");
} else {
return "";

}
}
}
});

//# sourceMappingURL=app.components.x_currency_field.model.js.map
