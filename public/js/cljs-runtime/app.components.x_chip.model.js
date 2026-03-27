goog.provide('app.components.x_chip.model');
app.components.x_chip.model.tag_name = "x-chip";
app.components.x_chip.model.attr_label = "label";
app.components.x_chip.model.attr_value = "value";
app.components.x_chip.model.attr_removable = "removable";
app.components.x_chip.model.attr_disabled = "disabled";
app.components.x_chip.model.observed_attributes = [app.components.x_chip.model.attr_label,app.components.x_chip.model.attr_value,app.components.x_chip.model.attr_removable,app.components.x_chip.model.attr_disabled];
app.components.x_chip.model.event_remove = "x-chip-remove";
/**
 * nil (attribute absent) → true.
 * 'false' or '0' → false.
 * Anything else → true.
 */
app.components.x_chip.model.parse_bool_default_true = (function app$components$x_chip$model$parse_bool_default_true(s){
if((s == null)){
return true;
} else {
return (!(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"0")))));
}
});
/**
 * Returns value-raw when it is a non-nil, non-empty string; else falls back to label.
 */
app.components.x_chip.model.effective_value = (function app$components$x_chip$model$effective_value(label,value_raw){
if(((typeof value_raw === 'string') && ((value_raw.length > (0))))){
return value_raw;
} else {
return label;
}
});
/**
 * Converts raw attribute strings to a clean view model.
 * disabled-present? should be boolean: (.hasAttribute el attr-disabled)
 */
app.components.x_chip.model.normalize = (function app$components$x_chip$model$normalize(p__22118){
var map__22119 = p__22118;
var map__22119__$1 = cljs.core.__destructure_map(map__22119);
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22119__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22119__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var removable_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22119__$1,new cljs.core.Keyword(null,"removable-raw","removable-raw",1718453796));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22119__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var label = (function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var removable_QMARK_ = app.components.x_chip.model.parse_bool_default_true(removable_raw);
var disabled_QMARK_ = cljs.core.boolean$(disabled_present_QMARK_);
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"value","value",305978217),app.components.x_chip.model.effective_value(label,value_raw),new cljs.core.Keyword(null,"removable?","removable?",1886800649),removable_QMARK_,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),disabled_QMARK_], null);
});
/**
 * True when the chip can be removed by the user: removable and not disabled.
 */
app.components.x_chip.model.removal_eligible_QMARK_ = (function app$components$x_chip$model$removal_eligible_QMARK_(p__22120){
var map__22121 = p__22120;
var map__22121__$1 = cljs.core.__destructure_map(map__22121);
var removable_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22121__$1,new cljs.core.Keyword(null,"removable?","removable?",1886800649));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22121__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var and__5140__auto__ = removable_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto__;
}
});
/**
 * Builds the JS detail object for x-chip-remove events.
 */
app.components.x_chip.model.remove_detail = (function app$components$x_chip$model$remove_detail(p__22122){
var map__22123 = p__22122;
var map__22123__$1 = cljs.core.__destructure_map(map__22123);
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22123__$1,new cljs.core.Keyword(null,"value","value",305978217));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22123__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return ({"value": value, "label": label});
});
app.components.x_chip.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"removable","removable",1005575226),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);

//# sourceMappingURL=app.components.x_chip.model.js.map
