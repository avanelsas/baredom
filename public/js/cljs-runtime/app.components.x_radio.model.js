goog.provide('app.components.x_radio.model');
app.components.x_radio.model.tag_name = "x-radio";
app.components.x_radio.model.attr_checked = "checked";
app.components.x_radio.model.attr_disabled = "disabled";
app.components.x_radio.model.attr_readonly = "readonly";
app.components.x_radio.model.attr_required = "required";
app.components.x_radio.model.attr_name = "name";
app.components.x_radio.model.attr_value = "value";
app.components.x_radio.model.attr_aria_label = "aria-label";
app.components.x_radio.model.attr_aria_describedby = "aria-describedby";
app.components.x_radio.model.attr_aria_labelledby = "aria-labelledby";
app.components.x_radio.model.event_change_request = "x-radio-change-request";
app.components.x_radio.model.event_change = "x-radio-change";
app.components.x_radio.model.observed_attributes = [app.components.x_radio.model.attr_checked,app.components.x_radio.model.attr_disabled,app.components.x_radio.model.attr_readonly,app.components.x_radio.model.attr_required,app.components.x_radio.model.attr_name,app.components.x_radio.model.attr_value,app.components.x_radio.model.attr_aria_label,app.components.x_radio.model.attr_aria_describedby,app.components.x_radio.model.attr_aria_labelledby];
app.components.x_radio.model.property_api = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_checked], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_disabled], null),new cljs.core.Keyword(null,"readOnly","readOnly",-1749118317),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_readonly], null),new cljs.core.Keyword(null,"required","required",1807647006),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_required], null),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_name], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_radio.model.attr_value], null)], null);
app.components.x_radio.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_radio.model.event_change_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"previousChecked","previousChecked",-1217692458),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"nextChecked","nextChecked",125748657),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null),app.components.x_radio.model.event_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null)]);
/**
 * Returns true if the attribute is present and not equal to "false".
 */
app.components.x_radio.model.parse_bool_attr = (function app$components$x_radio$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Returns the raw value string, or "on" if nil.
 */
app.components.x_radio.model.radio_value = (function app$components$x_radio$model$radio_value(raw){
if((!((raw == null)))){
return raw;
} else {
return "on";
}
});
/**
 * Returns true when the field satisfies its required constraint.
 */
app.components.x_radio.model.valid_QMARK_ = (function app$components$x_radio$model$valid_QMARK_(required_QMARK_,checked_QMARK_){
if(cljs.core.truth_(required_QMARK_)){
return checked_QMARK_;
} else {
return true;
}
});
/**
 * Derives a complete view-model map from raw attribute presence/values.
 */
app.components.x_radio.model.normalize = (function app$components$x_radio$model$normalize(p__23009){
var map__23010 = p__23009;
var map__23010__$1 = cljs.core.__destructure_map(map__23010);
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
var aria_describedby_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860));
var name_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"name-raw","name-raw",1493628068));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var required_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"required-present?","required-present?",-1253354196));
var aria_labelledby_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"aria-labelledby-raw","aria-labelledby-raw",-107265075));
var checked_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"checked-present?","checked-present?",-1155676496));
var readonly_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"readonly-present?","readonly-present?",793625394));
var value_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23010__$1,new cljs.core.Keyword(null,"value-raw","value-raw",-1649205133));
var checked_QMARK_ = cljs.core.boolean$(checked_present_QMARK_);
var disabled_QMARK_ = cljs.core.boolean$(disabled_present_QMARK_);
var readonly_QMARK_ = cljs.core.boolean$(readonly_present_QMARK_);
var required_QMARK_ = cljs.core.boolean$(required_present_QMARK_);
var value = app.components.x_radio.model.radio_value(value_raw);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"required?","required?",-872514462),new cljs.core.Keyword(null,"aria-checked","aria-checked",980530562),new cljs.core.Keyword(null,"readonly?","readonly?",988057827),new cljs.core.Keyword(null,"checked?","checked?",2024809091),new cljs.core.Keyword(null,"valid?","valid?",-212412379),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667),new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181)],[required_QMARK_,((checked_QMARK_)?"true":"false"),readonly_QMARK_,checked_QMARK_,app.components.x_radio.model.valid_QMARK_(required_QMARK_,checked_QMARK_),name_raw,value,aria_labelledby_raw,aria_describedby_raw,aria_label_raw,disabled_QMARK_]);
});

//# sourceMappingURL=app.components.x_radio.model.js.map
