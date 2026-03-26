goog.provide('app.components.x_toaster.model');
app.components.x_toaster.model.tag_name = "x-toaster";
app.components.x_toaster.model.attr_position = "position";
app.components.x_toaster.model.attr_max_toasts = "max-toasts";
app.components.x_toaster.model.attr_label = "label";
app.components.x_toaster.model.observed_attributes = [app.components.x_toaster.model.attr_position,app.components.x_toaster.model.attr_max_toasts,app.components.x_toaster.model.attr_label];
app.components.x_toaster.model.child_tag = "x-toast";
app.components.x_toaster.model.child_event_dismiss = "x-toast-dismiss";
app.components.x_toaster.model.child_dismiss_reason_toaster = "toaster-remove";
app.components.x_toaster.model.event_dismiss = "x-toaster-dismiss";
app.components.x_toaster.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"maxToasts","maxToasts",-755771830),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null);
app.components.x_toaster.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_toaster.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"heading","heading",-1312171873),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_toaster.model.valid_positions = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, ["top-center",null,"bottom-start",null,"bottom-end",null,"bottom-center",null,"top-end",null,"top-start",null], null), null);
/**
 * Normalise position attribute to a valid enum value; fallback to "top-end".
 */
app.components.x_toaster.model.parse_position = (function app$components$x_toaster$model$parse_position(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_toaster.model.valid_positions,v)){
return v;
} else {
return "top-end";
}
});
/**
 * Parse max-toasts to a positive integer; fallback to 5.
 */
app.components.x_toaster.model.parse_max_toasts = (function app$components$x_toaster$model$parse_max_toasts(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return Math.floor(n);
} else {
return null;
}
} else {
return null;
}
});
/**
 * Produce a stable view-model map from raw attribute inputs.
 * 
 *   Input keys:
 *  :position-raw    string | nil
 *  :max-toasts-raw  string | nil
 *  :label-raw       string | nil
 * 
 *   Output keys:
 *  :position   string — one of the six valid position values
 *  :max-toasts number — positive integer (default 5)
 *  :label      string — aria-label for the region (default "Notifications")
 */
app.components.x_toaster.model.normalize = (function app$components$x_toaster$model$normalize(p__23407){
var map__23408 = p__23407;
var map__23408__$1 = cljs.core.__destructure_map(map__23408);
var position_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23408__$1,new cljs.core.Keyword(null,"position-raw","position-raw",248130625));
var max_toasts_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23408__$1,new cljs.core.Keyword(null,"max-toasts-raw","max-toasts-raw",-815087069));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23408__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"position","position",-2011731912),app.components.x_toaster.model.parse_position(position_raw),new cljs.core.Keyword(null,"max-toasts","max-toasts",-1982289139),(function (){var or__5142__auto__ = app.components.x_toaster.model.parse_max_toasts(max_toasts_raw);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (5);
}
})(),new cljs.core.Keyword(null,"label","label",1718410804),((((typeof label_raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label_raw.trim(),""))))?label_raw:"Notifications")], null);
});
/**
 * Build event detail map for x-toaster-dismiss from the child x-toast-dismiss detail.
 */
app.components.x_toaster.model.dismiss_detail = (function app$components$x_toaster$model$dismiss_detail(type,reason,heading,message){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),(function (){var or__5142__auto__ = type;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
})(),new cljs.core.Keyword(null,"reason","reason",-2070751759),(function (){var or__5142__auto__ = reason;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"heading","heading",-1312171873),(function (){var or__5142__auto__ = heading;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"message","message",-406056002),(function (){var or__5142__auto__ = message;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()], null);
});
/**
 * Returns true when position places the toaster at the bottom of the viewport.
 */
app.components.x_toaster.model.bottom_position_QMARK_ = (function app$components$x_toaster$model$bottom_position_QMARK_(position){
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(position,"bottom-start")) || (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(position,"bottom-center")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(position,"bottom-end")))));
});
/**
 * Returns true when position uses horizontal centering.
 */
app.components.x_toaster.model.center_position_QMARK_ = (function app$components$x_toaster$model$center_position_QMARK_(position){
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(position,"top-center")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(position,"bottom-center")));
});

//# sourceMappingURL=app.components.x_toaster.model.js.map
