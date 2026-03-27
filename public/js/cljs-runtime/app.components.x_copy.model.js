goog.provide('app.components.x_copy.model');
app.components.x_copy.model.tag_name = "x-copy";
app.components.x_copy.model.attr_text = "text";
app.components.x_copy.model.attr_from = "from";
app.components.x_copy.model.attr_from_attr = "from-attr";
app.components.x_copy.model.attr_mode = "mode";
app.components.x_copy.model.attr_disabled = "disabled";
app.components.x_copy.model.attr_show_tooltip = "show-tooltip";
app.components.x_copy.model.attr_tooltip_ms = "tooltip-ms";
app.components.x_copy.model.attr_success_message = "success-message";
app.components.x_copy.model.attr_error_message = "error-message";
app.components.x_copy.model.attr_hotkey = "hotkey";
app.components.x_copy.model.event_copy_request = "x-copy-request";
app.components.x_copy.model.event_copy_success = "x-copy-success";
app.components.x_copy.model.event_copy_error = "x-copy-error";
app.components.x_copy.model.slot_default = "default";
app.components.x_copy.model.slot_tooltip = "tooltip";
app.components.x_copy.model.css_prefix = "--x-copy-";
app.components.x_copy.model.observed_attributes = ["text","from","from-attr","mode","disabled","show-tooltip","tooltip-ms","success-message","error-message","hotkey"];
/**
 * Returns "text" or "html", defaulting to "text".
 */
app.components.x_copy.model.parse_mode = (function app$components$x_copy$model$parse_mode(s){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"html")){
return "html";
} else {
return "text";
}
});
/**
 * nil/absent → true, "false"/"0" → false, anything else → true.
 */
app.components.x_copy.model.parse_bool_default_true = (function app$components$x_copy$model$parse_bool_default_true(s){
if((s == null)){
return true;
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")){
return false;
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"0")){
return false;
} else {
return true;

}
}
}
});
/**
 * Parse a positive integer from s, returning default-v if not valid or not positive.
 */
app.components.x_copy.model.parse_int_pos = (function app$components$x_copy$model$parse_int_pos(s,default_v){
if(((typeof s === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"")))){
var n = parseInt(s,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return n;
} else {
return default_v;
}
} else {
return default_v;
}
});
/**
 * Clamp integer n to [lo, hi].
 */
app.components.x_copy.model.clamp = (function app$components$x_copy$model$clamp(n,lo,hi){
return Math.min(hi,Math.max(lo,n));
});
/**
 * Produce a normalized view-model map from raw attribute values.
 */
app.components.x_copy.model.normalize = (function app$components$x_copy$model$normalize(p__22410){
var map__22411 = p__22410;
var map__22411__$1 = cljs.core.__destructure_map(map__22411);
var hotkey_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"hotkey-raw","hotkey-raw",1972404222));
var tooltip_ms_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"tooltip-ms-raw","tooltip-ms-raw",-1973326401));
var from_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"from-raw","from-raw",-44462428));
var text_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"text-raw","text-raw",-124751068));
var mode_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"mode-raw","mode-raw",-112711707));
var error_message_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"error-message-raw","error-message-raw",1910536231));
var from_attr_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"from-attr-raw","from-attr-raw",761657416));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var success_message_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"success-message-raw","success-message-raw",388691116));
var show_tooltip_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22411__$1,new cljs.core.Keyword(null,"show-tooltip-raw","show-tooltip-raw",453600717));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"show-tooltip?","show-tooltip?",-1214081087),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Keyword(null,"from-attr","from-attr",1217901743),new cljs.core.Keyword(null,"hotkey","hotkey",-329017295),new cljs.core.Keyword(null,"tooltip-ms","tooltip-ms",1062450417),new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.Keyword(null,"error-message","error-message",1756021561),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"success-message","success-message",-96541601),new cljs.core.Keyword(null,"text","text",-1790561697)],[app.components.x_copy.model.parse_bool_default_true(show_tooltip_raw),app.components.x_copy.model.parse_mode(mode_raw),((((typeof from_attr_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(from_attr_raw.trim(),""))))?from_attr_raw.trim():null),((((typeof hotkey_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(hotkey_raw.trim(),""))))?hotkey_raw.trim():null),app.components.x_copy.model.clamp(app.components.x_copy.model.parse_int_pos(tooltip_ms_raw,(1200)),(100),(10000)),((((typeof from_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(from_raw.trim(),""))))?from_raw.trim():null),(function (){var or__5142__auto__ = (function (){var and__5140__auto__ = typeof error_message_raw === 'string';
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(error_message_raw,"");
if(and__5140__auto____$1){
return error_message_raw;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "Copy failed";
}
})(),cljs.core.boolean$(disabled_present_QMARK_),(function (){var or__5142__auto__ = (function (){var and__5140__auto__ = typeof success_message_raw === 'string';
if(and__5140__auto__){
var and__5140__auto____$1 = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(success_message_raw,"");
if(and__5140__auto____$1){
return success_message_raw;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "Copied";
}
})(),((((typeof text_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(text_raw.trim(),""))))?text_raw.trim():null)]);
});
/**
 * Build the x-copy-request CustomEvent detail object.
 */
app.components.x_copy.model.request_detail = (function app$components$x_copy$model$request_detail(p__22412){
var map__22413 = p__22412;
var map__22413__$1 = cljs.core.__destructure_map(map__22413);
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22413__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
var from = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22413__$1,new cljs.core.Keyword(null,"from","from",1815293044));
var from_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22413__$1,new cljs.core.Keyword(null,"from-attr","from-attr",1217901743));
var mode = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22413__$1,new cljs.core.Keyword(null,"mode","mode",654403691));
return ({"text": (function (){var or__5142__auto__ = text;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(), "mode": (function (){var or__5142__auto__ = mode;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "text";
}
})(), "from": (function (){var or__5142__auto__ = from;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
})(), "fromAttr": (function (){var or__5142__auto__ = from_attr;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
})()});
});
/**
 * Build the x-copy-success CustomEvent detail object.
 */
app.components.x_copy.model.success_detail = (function app$components$x_copy$model$success_detail(text){
return ({"text": (function (){var or__5142__auto__ = text;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()});
});
/**
 * Build the x-copy-error CustomEvent detail object.
 */
app.components.x_copy.model.error_detail = (function app$components$x_copy$model$error_detail(err){
return ({"error": (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(err))});
});
app.components.x_copy.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"errorMessage","errorMessage",982744869),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"textValue","textValue",-598870870),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Keyword(null,"fromAttr","fromAttr",-1768252533),new cljs.core.Keyword(null,"tooltipMs","tooltipMs",1056463375),new cljs.core.Keyword(null,"hotkey","hotkey",-329017295),new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.Keyword(null,"showTooltip","showTooltip",482349691),new cljs.core.Keyword(null,"successMessage","successMessage",-789572803),new cljs.core.Keyword(null,"text","text",-1790561697)],[new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_error_message], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_disabled], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),null,new cljs.core.Keyword(null,"note","note",1426297904),"property-only override"], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_mode], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_from_attr], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_tooltip_ms], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_hotkey], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_from], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_show_tooltip], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_success_message], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_copy.model.attr_text], null)]);
app.components.x_copy.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_copy.model.event_copy_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"fromAttr","fromAttr",-1768252533),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_copy.model.event_copy_success,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null),app.components.x_copy.model.event_copy_error,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"error","error",-978969032),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);

//# sourceMappingURL=app.components.x_copy.model.js.map
