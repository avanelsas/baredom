goog.provide('app.components.x_date_picker.model');
app.components.x_date_picker.model.tag_name = "x-date-picker";
app.components.x_date_picker.model.attr_mode = "mode";
app.components.x_date_picker.model.attr_value = "value";
app.components.x_date_picker.model.attr_start = "start";
app.components.x_date_picker.model.attr_end = "end";
app.components.x_date_picker.model.attr_min = "min";
app.components.x_date_picker.model.attr_max = "max";
app.components.x_date_picker.model.attr_format = "format";
app.components.x_date_picker.model.attr_locale = "locale";
app.components.x_date_picker.model.attr_separator = "separator";
app.components.x_date_picker.model.attr_auto_swap = "auto-swap";
app.components.x_date_picker.model.attr_range_allow_same = "range-allow-same-day";
app.components.x_date_picker.model.attr_close_on_select = "close-on-select";
app.components.x_date_picker.model.attr_placeholder = "placeholder";
app.components.x_date_picker.model.attr_disabled = "disabled";
app.components.x_date_picker.model.attr_readonly = "readonly";
app.components.x_date_picker.model.attr_required = "required";
app.components.x_date_picker.model.attr_name = "name";
app.components.x_date_picker.model.attr_autocomplete = "autocomplete";
app.components.x_date_picker.model.attr_aria_label = "aria-label";
app.components.x_date_picker.model.attr_aria_describedby = "aria-describedby";
app.components.x_date_picker.model.event_input = "x-date-picker-input";
app.components.x_date_picker.model.event_change_request = "x-date-picker-change-request";
app.components.x_date_picker.model.event_change = "x-date-picker-change";
app.components.x_date_picker.model.observed_attributes = ["mode","value","start","end","min","max","format","locale","separator","auto-swap","range-allow-same-day","close-on-select","placeholder","disabled","readonly","required","name","autocomplete","aria-label","aria-describedby"];
app.components.x_date_picker.model.property_api = new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_mode], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_value], null),new cljs.core.Keyword(null,"start","start",-355208981),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_start], null),new cljs.core.Keyword(null,"end","end",-268185958),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_end], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_disabled], null),new cljs.core.Keyword(null,"readOnly","readOnly",-1749118317),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_readonly], null),new cljs.core.Keyword(null,"required","required",1807647006),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_date_picker.model.attr_required], null),new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),"open"], null)], null);
/**
 * Trim and return nil if empty.
 */
app.components.x_date_picker.model.normalize_str = (function app$components$x_date_picker$model$normalize_str(s){
if(typeof s === 'string'){
var t = s.trim();
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(t,"")){
return t;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Returns true if attr string is non-nil and not "false".
 */
app.components.x_date_picker.model.parse_bool_attr = (function app$components$x_date_picker$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Returns :iso or :localized.  Default is :iso.
 */
app.components.x_date_picker.model.parse_format = (function app$components$x_date_picker$model$parse_format(s){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"localized")){
return new cljs.core.Keyword(null,"localized","localized",1722109655);
} else {
return new cljs.core.Keyword(null,"iso","iso",-1366207543);
}
});
/**
 * Returns :single or :range.  Default is :single.
 */
app.components.x_date_picker.model.parse_mode = (function app$components$x_date_picker$model$parse_mode(s){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"range")){
return new cljs.core.Keyword(null,"range","range",1639692286);
} else {
return new cljs.core.Keyword(null,"single","single",1551466437);
}
});
/**
 * Parse integer string, returning default-v on failure.
 */
app.components.x_date_picker.model.parse_int = (function app$components$x_date_picker$model$parse_int(s,default_v){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(cljs.core.truth_(isNaN(n))){
return default_v;
} else {
return n;
}
} else {
return default_v;
}
});
/**
 * Zero-pad integer n to 2 digits.
 */
app.components.x_date_picker.model.pad2 = (function app$components$x_date_picker$model$pad2(n){
if((n < (10))){
return (""+"0"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n));
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n));
}
});
/**
 * JS Date → "YYYY-MM-DD" using UTC fields.
 */
app.components.x_date_picker.model.date__GT_iso = (function app$components$x_date_picker$model$date__GT_iso(d){
if(cljs.core.truth_(d)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(d.getUTCFullYear())+"-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_date_picker.model.pad2((d.getUTCMonth() + (1))))+"-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_date_picker.model.pad2(d.getUTCDate())));
} else {
return null;
}
});
/**
 * "YYYY-MM-DD" → JS Date at UTC midnight, or nil on parse failure.
 */
app.components.x_date_picker.model.iso__GT_date = (function app$components$x_date_picker$model$iso__GT_date(s){
if(cljs.core.truth_((function (){var and__5140__auto__ = typeof s === 'string';
if(and__5140__auto__){
return cljs.core.re_matches(/\d{4}-\d{2}-\d{2}/,s);
} else {
return and__5140__auto__;
}
})())){
var d = (new Date((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)+"T00:00:00.000Z")));
if(cljs.core.truth_(isNaN(d.getTime()))){
return null;
} else {
return d;
}
} else {
return null;
}
});
/**
 * Returns -1, 0, or 1 comparing two UTC dates by day only.
 */
app.components.x_date_picker.model.compare_date = (function app$components$x_date_picker$model$compare_date(a,b){
if((((a == null)) && ((b == null)))){
return (0);
} else {
if((a == null)){
return (-1);
} else {
if((b == null)){
return (1);
} else {
var at = a.getTime();
var bt = b.getTime();
if((at < bt)){
return (-1);
} else {
if((at > bt)){
return (1);
} else {
return (0);

}
}

}
}
}
});
app.components.x_date_picker.model.min_date = (function app$components$x_date_picker$model$min_date(a,b){
if((app.components.x_date_picker.model.compare_date(a,b) < (0))){
return a;
} else {
return b;
}
});
app.components.x_date_picker.model.max_date = (function app$components$x_date_picker$model$max_date(a,b){
if((app.components.x_date_picker.model.compare_date(a,b) > (0))){
return a;
} else {
return b;
}
});
/**
 * Return a new Date that is d + n days (UTC).
 */
app.components.x_date_picker.model.add_days = (function app$components$x_date_picker$model$add_days(d,n){
if(cljs.core.truth_(d)){
var t = d.getTime();
return (new Date((t + (n * (86400000)))));
} else {
return null;
}
});
/**
 * Return UTC midnight on the 1st of d's month.
 */
app.components.x_date_picker.model.start_of_month = (function app$components$x_date_picker$model$start_of_month(d){
if(cljs.core.truth_(d)){
return (new Date(Date.UTC(d.getUTCFullYear(),d.getUTCMonth(),(1))));
} else {
return null;
}
});
/**
 * Number of days in d's UTC month.
 */
app.components.x_date_picker.model.days_in_month = (function app$components$x_date_picker$model$days_in_month(d){
if(cljs.core.truth_(d)){
return (new Date(Date.UTC(d.getUTCFullYear(),(d.getUTCMonth() + (1)),(0)))).getUTCDate();
} else {
return null;
}
});
/**
 * Day of week 0=Sunday..6=Saturday for d's UTC date.
 */
app.components.x_date_picker.model.weekday_0_sun = (function app$components$x_date_picker$model$weekday_0_sun(d){
if(cljs.core.truth_(d)){
return d.getUTCDay();
} else {
return null;
}
});
/**
 * Clamp d to [min-d, max-d]. Either bound may be nil (unbounded).
 */
app.components.x_date_picker.model.clamp_to_range = (function app$components$x_date_picker$model$clamp_to_range(d,min_d,max_d){
var d1 = (cljs.core.truth_((function (){var and__5140__auto__ = min_d;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = d;
if(cljs.core.truth_(and__5140__auto____$1)){
return (app.components.x_date_picker.model.compare_date(d,min_d) < (0));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())?min_d:d);
var d2 = (cljs.core.truth_((function (){var and__5140__auto__ = max_d;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = d1;
if(cljs.core.truth_(and__5140__auto____$1)){
return (app.components.x_date_picker.model.compare_date(d1,max_d) > (0));
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())?max_d:d1);
return d2;
});
/**
 * True if d is between a and b inclusive (either order).
 */
app.components.x_date_picker.model.in_range_QMARK_ = (function app$components$x_date_picker$model$in_range_QMARK_(d,a,b){
if(cljs.core.truth_((function (){var and__5140__auto__ = d;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = a;
if(cljs.core.truth_(and__5140__auto____$1)){
return b;
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
var lo = (((app.components.x_date_picker.model.compare_date(a,b) < (0)))?a:b);
var hi = (((app.components.x_date_picker.model.compare_date(a,b) < (0)))?b:a);
return (((!((app.components.x_date_picker.model.compare_date(d,lo) < (0))))) && ((!((app.components.x_date_picker.model.compare_date(d,hi) > (0))))));
} else {
return null;
}
});
/**
 * Format a JS Date for display.  Uses Intl.DateTimeFormat when available.
 */
app.components.x_date_picker.model.format_date = (function app$components$x_date_picker$model$format_date(d,p__22445){
var map__22446 = p__22445;
var map__22446__$1 = cljs.core.__destructure_map(map__22446);
var format = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22446__$1,new cljs.core.Keyword(null,"format","format",-1306924766));
var locale = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22446__$1,new cljs.core.Keyword(null,"locale","locale",-2115712697));
if(cljs.core.truth_(d)){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(format,new cljs.core.Keyword(null,"localized","localized",1722109655))){
var opts = ({"year": "numeric", "month": "long", "day": "numeric", "timeZone": "UTC"});
var loc = (function (){var or__5142__auto__ = locale;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "default";
}
})();
return (new Intl.DateTimeFormat(loc,opts)).format(d);
} else {
return app.components.x_date_picker.model.date__GT_iso(d);
}
} else {
return null;
}
});
/**
 * Try to parse a display string to a single date.
 * Accepts ISO "YYYY-MM-DD". Returns {:ok? boolean :date js/Date|nil}.
 */
app.components.x_date_picker.model.parse_display__GT_single = (function app$components$x_date_picker$model$parse_display__GT_single(display){
var s = app.components.x_date_picker.model.normalize_str(display);
var date = (cljs.core.truth_(s)?app.components.x_date_picker.model.iso__GT_date(s):null);
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"ok?","ok?",447310304),(!((date == null))),new cljs.core.Keyword(null,"date","date",-1463434462),date], null);
});
/**
 * Try to parse a display string that may contain a separator.
 * Returns {:ok? boolean :start js/Date|nil :end js/Date|nil}.
 */
app.components.x_date_picker.model.parse_display__GT_range = (function app$components$x_date_picker$model$parse_display__GT_range(display,p__22447){
var map__22448 = p__22447;
var map__22448__$1 = cljs.core.__destructure_map(map__22448);
var separator = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22448__$1,new cljs.core.Keyword(null,"separator","separator",-1628749125));
var sep = ((((typeof separator === 'string') && ((separator.length > (0)))))?separator:" - ");
var s = app.components.x_date_picker.model.normalize_str(display);
if((s == null)){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ok?","ok?",447310304),false,new cljs.core.Keyword(null,"start","start",-355208981),null,new cljs.core.Keyword(null,"end","end",-268185958),null], null);
} else {
var idx = s.indexOf(sep);
if((idx < (0))){
var map__22449 = app.components.x_date_picker.model.parse_display__GT_single(s);
var map__22449__$1 = cljs.core.__destructure_map(map__22449);
var ok_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22449__$1,new cljs.core.Keyword(null,"ok?","ok?",447310304));
var date = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22449__$1,new cljs.core.Keyword(null,"date","date",-1463434462));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ok?","ok?",447310304),ok_QMARK_,new cljs.core.Keyword(null,"start","start",-355208981),date,new cljs.core.Keyword(null,"end","end",-268185958),null], null);
} else {
var s1 = s.substring((0),idx).trim();
var s2 = s.substring((idx + sep.length)).trim();
var d1 = app.components.x_date_picker.model.iso__GT_date(s1);
var d2 = app.components.x_date_picker.model.iso__GT_date(s2);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ok?","ok?",447310304),(((!((d1 == null)))) && ((!((d2 == null))))),new cljs.core.Keyword(null,"start","start",-355208981),d1,new cljs.core.Keyword(null,"end","end",-268185958),d2], null);
}
}
});
/**
 * Build stable model from raw attr strings.
 */
app.components.x_date_picker.model.canonicalize = (function app$components$x_date_picker$model$canonicalize(p__22463){
var map__22464 = p__22463;
var map__22464__$1 = cljs.core.__destructure_map(map__22464);
var end = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var separator = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"separator","separator",-1628749125));
var min = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"min","min",444991522));
var format = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"format","format",-1306924766));
var locale = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"locale","locale",-2115712697));
var range_allow_same_day_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"range-allow-same-day?","range-allow-same-day?",-714516856));
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"value","value",305978217));
var mode = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"mode","mode",654403691));
var start = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"max","max",61366548));
var auto_swap_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22464__$1,new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411));
var m = app.components.x_date_picker.model.parse_mode(mode);
var fmt = app.components.x_date_picker.model.parse_format(format);
var loc = app.components.x_date_picker.model.normalize_str(locale);
var sep = (function (){var or__5142__auto__ = app.components.x_date_picker.model.normalize_str(separator);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return " - ";
}
})();
var min_d = app.components.x_date_picker.model.iso__GT_date(min);
var max_d = app.components.x_date_picker.model.iso__GT_date(max);
var auto_swap_QMARK___$1 = cljs.core.boolean$(auto_swap_QMARK_);
var allow_same_QMARK_ = cljs.core.boolean$(range_allow_same_day_QMARK_);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(m,new cljs.core.Keyword(null,"single","single",1551466437))){
var value_d = app.components.x_date_picker.model.iso__GT_date(value);
var complete_QMARK_ = (!((value_d == null)));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"format","format",-1306924766),new cljs.core.Keyword(null,"value-d","value-d",-1768534876),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.Keyword(null,"allow-same-day?","allow-same-day?",-1971558934),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Keyword(null,"max-d","max-d",-814680979),new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411),new cljs.core.Keyword(null,"complete?","complete?",-892991879),new cljs.core.Keyword(null,"separator","separator",-1628749125),new cljs.core.Keyword(null,"min-d","min-d",-1118792932),new cljs.core.Keyword(null,"value-str","value-str",989198845)],[fmt,value_d,loc,allow_same_QMARK_,new cljs.core.Keyword(null,"single","single",1551466437),max_d,auto_swap_QMARK___$1,complete_QMARK_,sep,min_d,(function (){var or__5142__auto__ = app.components.x_date_picker.model.normalize_str(value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()]);
} else {
var start_d = app.components.x_date_picker.model.iso__GT_date(start);
var end_d = app.components.x_date_picker.model.iso__GT_date(end);
var complete_QMARK_ = (((!((start_d == null)))) && ((!((end_d == null)))));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"end-str","end-str",-558112319),new cljs.core.Keyword(null,"format","format",-1306924766),new cljs.core.Keyword(null,"start-d","start-d",-1546785820),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.Keyword(null,"allow-same-day?","allow-same-day?",-1971558934),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Keyword(null,"max-d","max-d",-814680979),new cljs.core.Keyword(null,"start-str","start-str",1077409677),new cljs.core.Keyword(null,"end-d","end-d",-1316370898),new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411),new cljs.core.Keyword(null,"complete?","complete?",-892991879),new cljs.core.Keyword(null,"separator","separator",-1628749125),new cljs.core.Keyword(null,"min-d","min-d",-1118792932)],[(function (){var or__5142__auto__ = app.components.x_date_picker.model.normalize_str(end);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),fmt,start_d,loc,allow_same_QMARK_,new cljs.core.Keyword(null,"range","range",1639692286),max_d,(function (){var or__5142__auto__ = app.components.x_date_picker.model.normalize_str(start);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),end_d,auto_swap_QMARK___$1,complete_QMARK_,sep,min_d]);
}
});
/**
 * Return a 42-item vector for a calendar view of the month containing d.
 * Each entry is {:date jsDate :in-month? boolean}.
 */
app.components.x_date_picker.model.month_grid = (function app$components$x_date_picker$model$month_grid(d){
if(cljs.core.truth_(d)){
var som = app.components.x_date_picker.model.start_of_month(d);
var dow = app.components.x_date_picker.model.weekday_0_sun(som);
var grid_start = app.components.x_date_picker.model.add_days(som,(- dow));
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (i){
var cell_date = app.components.x_date_picker.model.add_days(grid_start,i);
var in_month_QMARK_ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cell_date.getUTCMonth(),d.getUTCMonth());
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"date","date",-1463434462),cell_date,new cljs.core.Keyword(null,"in-month?","in-month?",-1630426251),in_month_QMARK_], null);
}),cljs.core.range.cljs$core$IFn$_invoke$arity$1((42)));
} else {
return null;
}
});
/**
 * Compute the input display string from canonicalized state.
 */
app.components.x_date_picker.model.display_value = (function app$components$x_date_picker$model$display_value(canon,p__22484){
var map__22485 = p__22484;
var map__22485__$1 = cljs.core.__destructure_map(map__22485);
var format = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22485__$1,new cljs.core.Keyword(null,"format","format",-1306924766));
var locale = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22485__$1,new cljs.core.Keyword(null,"locale","locale",-2115712697));
var config = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"format","format",-1306924766),format,new cljs.core.Keyword(null,"locale","locale",-2115712697),locale], null);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"mode","mode",654403691).cljs$core$IFn$_invoke$arity$1(canon),new cljs.core.Keyword(null,"single","single",1551466437))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"value-d","value-d",-1768534876).cljs$core$IFn$_invoke$arity$1(canon))){
return app.components.x_date_picker.model.format_date(new cljs.core.Keyword(null,"value-d","value-d",-1768534876).cljs$core$IFn$_invoke$arity$1(canon),config);
} else {
return "";
}
} else {
var sep = new cljs.core.Keyword(null,"separator","separator",-1628749125).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_((function (){var and__5140__auto__ = new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(and__5140__auto__)){
return new cljs.core.Keyword(null,"end-d","end-d",-1316370898).cljs$core$IFn$_invoke$arity$1(canon);
} else {
return and__5140__auto__;
}
})())){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_date_picker.model.format_date(new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon),config))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(sep)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_date_picker.model.format_date(new cljs.core.Keyword(null,"end-d","end-d",-1316370898).cljs$core$IFn$_invoke$arity$1(canon),config)));
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon))){
return app.components.x_date_picker.model.format_date(new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon),config);
} else {
return "";

}
}
}
});

//# sourceMappingURL=app.components.x_date_picker.model.js.map
