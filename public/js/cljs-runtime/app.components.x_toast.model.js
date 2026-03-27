goog.provide('app.components.x_toast.model');
app.components.x_toast.model.tag_name = "x-toast";
app.components.x_toast.model.attr_type = "type";
app.components.x_toast.model.attr_heading = "heading";
app.components.x_toast.model.attr_message = "message";
app.components.x_toast.model.attr_icon = "icon";
app.components.x_toast.model.attr_dismissible = "dismissible";
app.components.x_toast.model.attr_disabled = "disabled";
app.components.x_toast.model.attr_timeout_ms = "timeout-ms";
app.components.x_toast.model.attr_show_progress = "show-progress";
app.components.x_toast.model.observed_attributes = [app.components.x_toast.model.attr_type,app.components.x_toast.model.attr_heading,app.components.x_toast.model.attr_message,app.components.x_toast.model.attr_icon,app.components.x_toast.model.attr_dismissible,app.components.x_toast.model.attr_disabled,app.components.x_toast.model.attr_timeout_ms,app.components.x_toast.model.attr_show_progress];
app.components.x_toast.model.event_dismiss = "x-toast-dismiss";
app.components.x_toast.model.property_api = new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"heading","heading",-1312171873),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"icon","icon",1679606541),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"dismissible","dismissible",80759338),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"timeoutMs","timeoutMs",-716622575),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"showProgress","showProgress",861509378),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);
app.components.x_toast.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_toast.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"heading","heading",-1312171873),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_toast.model.default_type = new cljs.core.Keyword(null,"info","info",-317069002);
app.components.x_toast.model.type__GT_kw = new cljs.core.PersistentArrayMap(null, 4, ["info",new cljs.core.Keyword(null,"info","info",-317069002),"success",new cljs.core.Keyword(null,"success","success",1890645906),"warning",new cljs.core.Keyword(null,"warning","warning",-1685650671),"error",new cljs.core.Keyword(null,"error","error",-978969032)], null);
app.components.x_toast.model.kw__GT_type = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"info","info",-317069002),"info",new cljs.core.Keyword(null,"success","success",1890645906),"success",new cljs.core.Keyword(null,"warning","warning",-1685650671),"warning",new cljs.core.Keyword(null,"error","error",-978969032),"error"], null);
app.components.x_toast.model.type__GT_icon = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"info","info",-317069002),"\u2139",new cljs.core.Keyword(null,"success","success",1890645906),"\u2713",new cljs.core.Keyword(null,"warning","warning",-1685650671),"!",new cljs.core.Keyword(null,"error","error",-978969032),"\u2A2F"], null);
/**
 * Normalise a raw type attribute string to a keyword.
 *   Unknown / nil values fall back to :info.
 */
app.components.x_toast.model.parse_type = (function app$components$x_toast$model$parse_type(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_toast.model.type__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_toast.model.default_type;
}
});
/**
 * Convert an internal type keyword back to its attribute string.
 */
app.components.x_toast.model.type__GT_attr = (function app$components$x_toast$model$type__GT_attr(t){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_toast.model.kw__GT_type,t);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "info";
}
});
/**
 * Parse an attribute that is true when absent or empty, false only when "false".
 */
app.components.x_toast.model.parse_bool_default_true = (function app$components$x_toast$model$parse_bool_default_true(s){
if((s == null)){
return true;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)).trim().toLowerCase(),"false");
}
});
/**
 * Parse an attribute that is false when absent, true when present (any value except "false").
 */
app.components.x_toast.model.parse_bool_default_false = (function app$components$x_toast$model$parse_bool_default_false(s){
if((s == null)){
return false;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)).trim().toLowerCase(),"false");
}
});
/**
 * Parse timeout-ms attribute to a positive integer, or nil if absent/invalid.
 */
app.components.x_toast.model.parse_timeout_ms = (function app$components$x_toast$model$parse_timeout_ms(s){
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
app.components.x_toast.model.normalize_icon = (function app$components$x_toast$model$normalize_icon(s){
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
 * Return the ARIA live-region role for a given toast type.
 *   warning/error use role=alert (assertive); info/success use role=status (polite).
 */
app.components.x_toast.model.role_for_type = (function app$components$x_toast$model$role_for_type(t){
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(t,new cljs.core.Keyword(null,"warning","warning",-1685650671))) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(t,new cljs.core.Keyword(null,"error","error",-978969032))))){
return "alert";
} else {
return "status";
}
});
/**
 * Return the default icon glyph for a type keyword.
 */
app.components.x_toast.model.default_icon_for_type = (function app$components$x_toast$model$default_icon_for_type(t){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_toast.model.type__GT_icon,t);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_toast.model.type__GT_icon,app.components.x_toast.model.default_type);
}
});
/**
 * Normalise raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :type-raw            string | nil
 *  :heading             string | nil
 *  :message             string | nil
 *  :icon-present?       boolean
 *  :icon-raw            string | nil
 *  :dismissible-attr    string | nil
 *  :disabled-present?   boolean
 *  :timeout-ms-raw      string | nil
 *  :show-progress-attr  string | nil
 * 
 *   Output keys:
 *  :type           keyword  — :info | :success | :warning | :error
 *  :heading        string   — trimmed, may be ""
 *  :message        string   — trimmed, may be ""
 *  :icon-mode      keyword  — :default | :custom | :hidden
 *  :icon           string | nil  (only meaningful when :custom)
 *  :dismissible?   boolean
 *  :disabled?      boolean
 *  :timeout-ms     int | nil
 *  :show-progress? boolean
 */
app.components.x_toast.model.normalize = (function app$components$x_toast$model$normalize(p__23451){
var map__23453 = p__23451;
var map__23453__$1 = cljs.core.__destructure_map(map__23453);
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"message","message",-406056002));
var heading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"heading","heading",-1312171873));
var dismissible_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"dismissible-attr","dismissible-attr",-2012325753));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var timeout_ms_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"timeout-ms-raw","timeout-ms-raw",-1969949623));
var icon_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778));
var show_progress_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"show-progress-attr","show-progress-attr",1680262066));
var type_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"type-raw","type-raw",-967209994));
var icon_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23453__$1,new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214));
var t = app.components.x_toast.model.parse_type(type_raw);
var icon_STAR_ = app.components.x_toast.model.normalize_icon(icon_raw);
var icon_mode = (cljs.core.truth_((function (){var and__5140__auto__ = icon_present_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return (icon_STAR_ == null);
} else {
return and__5140__auto__;
}
})())?new cljs.core.Keyword(null,"hidden","hidden",-312506092):(((!((icon_STAR_ == null))))?new cljs.core.Keyword(null,"custom","custom",340151948):new cljs.core.Keyword(null,"default","default",-1987822328)
));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"show-progress?","show-progress?",-1681518912),new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"icon","icon",1679606541),new cljs.core.Keyword(null,"icon-mode","icon-mode",-2015012071),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406),new cljs.core.Keyword(null,"message","message",-406056002),new cljs.core.Keyword(null,"heading","heading",-1312171873)],[app.components.x_toast.model.parse_bool_default_false(show_progress_attr),app.components.x_toast.model.parse_bool_default_true(dismissible_attr),t,((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"custom","custom",340151948)))?icon_STAR_:null),icon_mode,cljs.core.boolean$(disabled_present_QMARK_),app.components.x_toast.model.parse_timeout_ms(timeout_ms_raw),(function (){var or__5142__auto__ = ((typeof message === 'string')?message.trim():null);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),(function (){var or__5142__auto__ = ((typeof heading === 'string')?heading.trim():null);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()]);
});
/**
 * Return true when the toast can currently be dismissed (dismissible and not disabled).
 */
app.components.x_toast.model.dismiss_eligible_QMARK_ = (function app$components$x_toast$model$dismiss_eligible_QMARK_(p__23476){
var map__23477 = p__23476;
var map__23477__$1 = cljs.core.__destructure_map(map__23477);
var dismissible_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23477__$1,new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23477__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var and__5140__auto__ = dismissible_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(disabled_QMARK_);
} else {
return and__5140__auto__;
}
});
/**
 * Build the event detail map for an x-toast-dismiss event.
 */
app.components.x_toast.model.dismiss_detail = (function app$components$x_toast$model$dismiss_detail(p__23480,reason){
var map__23481 = p__23480;
var map__23481__$1 = cljs.core.__destructure_map(map__23481);
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23481__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var heading = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23481__$1,new cljs.core.Keyword(null,"heading","heading",-1312171873));
var message = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23481__$1,new cljs.core.Keyword(null,"message","message",-406056002));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),app.components.x_toast.model.type__GT_attr(type),new cljs.core.Keyword(null,"reason","reason",-2070751759),reason,new cljs.core.Keyword(null,"heading","heading",-1312171873),heading,new cljs.core.Keyword(null,"message","message",-406056002),message], null);
});
/**
 * Return true when both show-progress is set and timeout-ms is a valid positive integer.
 */
app.components.x_toast.model.progress_eligible_QMARK_ = (function app$components$x_toast$model$progress_eligible_QMARK_(p__23484){
var map__23485 = p__23484;
var map__23485__$1 = cljs.core.__destructure_map(map__23485);
var show_progress_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23485__$1,new cljs.core.Keyword(null,"show-progress?","show-progress?",-1681518912));
var timeout_ms = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23485__$1,new cljs.core.Keyword(null,"timeout-ms","timeout-ms",754221406));
return cljs.core.boolean$((function (){var and__5140__auto__ = show_progress_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return (((!((timeout_ms == null)))) && ((timeout_ms > (0))));
} else {
return and__5140__auto__;
}
})());
});

//# sourceMappingURL=app.components.x_toast.model.js.map
