goog.provide('app.components.x_alert.model');
app.components.x_alert.model.tag_name = "x-alert";
app.components.x_alert.model.attr_type = "type";
app.components.x_alert.model.attr_text = "text";
app.components.x_alert.model.attr_icon = "icon";
app.components.x_alert.model.attr_dismissible = "dismissible";
app.components.x_alert.model.attr_disabled = "disabled";
app.components.x_alert.model.attr_timeout_ms = "timeout-ms";
app.components.x_alert.model.observed_attributes = [app.components.x_alert.model.attr_type,app.components.x_alert.model.attr_text,app.components.x_alert.model.attr_icon,app.components.x_alert.model.attr_dismissible,app.components.x_alert.model.attr_disabled,app.components.x_alert.model.attr_timeout_ms];
app.components.x_alert.model.event_dismiss = "x-alert-dismiss";
app.components.x_alert.model.property_api = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"icon","icon",1679606541),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"dismissible","dismissible",80759338),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"timeoutMs","timeoutMs",-716622575),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null);
app.components.x_alert.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_alert.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_alert.model.default_type = new cljs.core.Keyword(null,"info","info",-317069002);
app.components.x_alert.model.type__GT_kw = new cljs.core.PersistentArrayMap(null, 4, ["info",new cljs.core.Keyword(null,"info","info",-317069002),"success",new cljs.core.Keyword(null,"success","success",1890645906),"warning",new cljs.core.Keyword(null,"warning","warning",-1685650671),"error",new cljs.core.Keyword(null,"error","error",-978969032)], null);
app.components.x_alert.model.kw__GT_type = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"info","info",-317069002),"info",new cljs.core.Keyword(null,"success","success",1890645906),"success",new cljs.core.Keyword(null,"warning","warning",-1685650671),"warning",new cljs.core.Keyword(null,"error","error",-978969032),"error"], null);
app.components.x_alert.model.type__GT_icon = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"info","info",-317069002),"\u2139",new cljs.core.Keyword(null,"success","success",1890645906),"\u2713",new cljs.core.Keyword(null,"warning","warning",-1685650671),"!",new cljs.core.Keyword(null,"error","error",-978969032),"\u2A2F"], null);
/**
 * Normalise a raw type attribute string to a keyword.
 *   Unknown / nil values fall back to :info.
 */
app.components.x_alert.model.parse_type = (function app$components$x_alert$model$parse_type(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_alert.model.type__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_alert.model.default_type;
}
});
/**
 * Convert an internal type keyword back to its attribute string.
 */
app.components.x_alert.model.type__GT_attr = (function app$components$x_alert$model$type__GT_attr(t){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_alert.model.kw__GT_type,t);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
});
/**
 * Parse an attribute that is true when absent or empty, false only when "false".
 */
app.components.x_alert.model.parse_bool_default_true = (function app$components$x_alert$model$parse_bool_default_true(s){
if((s == null)){
return true;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)).trim().toLowerCase(),"false");
}
});
/**
 * Parse timeout-ms attribute to a positive integer, or nil if absent/invalid.
 */
app.components.x_alert.model.parse_timeout_ms = (function app$components$x_alert$model$parse_timeout_ms(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((typeof n === 'number') && (((cljs.core.not(isNaN(n))) && ((n > (0))))))){
return n;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Normalise icon attribute: empty strings and "none" become nil.
 */
app.components.x_alert.model.normalize_icon = (function app$components$x_alert$model$normalize_icon(s){
if(typeof s === 'string'){
var v = s.trim();
if(((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,"")) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v.toLowerCase(),"none")))){
return v;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Return the ARIA live-region role for a given alert type.
 *   warning/error use role=alert (assertive); info/success use role=status (polite).
 */
app.components.x_alert.model.role_for_type = (function app$components$x_alert$model$role_for_type(t){
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(t,new cljs.core.Keyword(null,"warning","warning",-1685650671))) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(t,new cljs.core.Keyword(null,"error","error",-978969032))))){
return "alert";
} else {
return "status";
}
});
/**
 * Return the default icon glyph for a type keyword.
 */
app.components.x_alert.model.default_icon_for_type = (function app$components$x_alert$model$default_icon_for_type(t){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_alert.model.type__GT_icon,t);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_alert.model.type__GT_icon,app.components.x_alert.model.default_type);
}
});
/**
 * Normalise raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :type-raw          string | nil
 *  :text              string | nil
 *  :icon-present?     boolean
 *  :icon-raw          string | nil
 *  :dismissible-attr  string | nil
 *  :disabled-present? boolean
 *  :timeout-ms-raw    string | nil
 * 
 *   Output keys:
 *  :type         keyword  — :info | :success | :warning | :error
 *  :text         string
 *  :icon-mode    keyword  — :default | :custom | :hidden
 *  :icon         string | nil  (only meaningful when :custom)
 *  :dismissible? boolean
 *  :disabled?    boolean
 *  :timeout-ms   int | nil
 */
app.components.x_alert.model.normalize = (function app$components$x_alert$model$normalize(p__21270){
var map__21271 = p__21270;
var map__21271__$1 = cljs.core.__destructure_map(map__21271);
var type_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"type-raw","type-raw",-967209994));
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
var icon_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778));
var icon_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214));
var dismissible_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"dismissible-attr","dismissible-attr",-2012325753));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var timeout_ms_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21271__$1,new cljs.core.Keyword(null,"timeout-ms-raw","timeout-ms-raw",-1969949623));
var t = app.components.x_alert.model.parse_type(type_raw);
var icon_STAR_ = app.components.x_alert.model.normalize_icon(icon_raw);
var icon_mode = (cljs.core.truth_((function (){var and__5140__auto__ = icon_present_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return (icon_STAR_ == null);
} else {
return and__5140__auto__;
}
})())?new cljs.core.Keyword(null,"hidden","hidden",-312506092):(((!((icon_STAR_ == null))))?new cljs.core.Keyword(null,"custom","custom",340151948):new cljs.core.Keyword(null,"default","default",-1987822328)
));
return new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"type","type",1174270348),t,new cljs.core.Keyword(null,"text","text",-1790561697),(function (){var or__5142__auto__ = text;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"icon-mode","icon-mode",-2015012071),icon_mode,new cljs.core.Keyword(null,"icon","icon",1679606541),((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"custom","custom",340151948)))?icon_STAR_:null),new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178),app.components.x_alert.model.parse_bool_default_true(dismissible_attr),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406),app.components.x_alert.model.parse_timeout_ms(timeout_ms_raw)], null);
});
/**
 * Return true when the alert can currently be dismissed (dismissible and not disabled).
 */
app.components.x_alert.model.dismiss_eligible_QMARK_ = (function app$components$x_alert$model$dismiss_eligible_QMARK_(p__21278){
var map__21279 = p__21278;
var map__21279__$1 = cljs.core.__destructure_map(map__21279);
var dismissible_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21279__$1,new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21279__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var and__5140__auto__ = dismissible_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto__;
}
});
/**
 * Build the event detail map for an x-alert-dismiss event.
 */
app.components.x_alert.model.dismiss_detail = (function app$components$x_alert$model$dismiss_detail(p__21288,reason){
var map__21289 = p__21288;
var map__21289__$1 = cljs.core.__destructure_map(map__21289);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21289__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var text = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21289__$1,new cljs.core.Keyword(null,"text","text",-1790561697));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),app.components.x_alert.model.type__GT_attr(type),new cljs.core.Keyword(null,"reason","reason",-2070751759),reason,new cljs.core.Keyword(null,"text","text",-1790561697),text], null);
});

//# sourceMappingURL=app.components.x_alert.model.js.map
