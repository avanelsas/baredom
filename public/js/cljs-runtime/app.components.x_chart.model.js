goog.provide('app.components.x_chart.model');
app.components.x_chart.model.tag_name = "x-chart";
app.components.x_chart.model.attr_type = "type";
app.components.x_chart.model.attr_data = "data";
app.components.x_chart.model.attr_height = "height";
app.components.x_chart.model.attr_padding = "padding";
app.components.x_chart.model.attr_x_format = "x-format";
app.components.x_chart.model.attr_y_format = "y-format";
app.components.x_chart.model.attr_grid = "grid";
app.components.x_chart.model.attr_axes = "axes";
app.components.x_chart.model.attr_tooltip = "tooltip";
app.components.x_chart.model.attr_cursor = "cursor";
app.components.x_chart.model.attr_disabled = "disabled";
app.components.x_chart.model.attr_loading = "loading";
app.components.x_chart.model.attr_selected = "selected";
app.components.x_chart.model.observed_attributes = ["type","data","height","padding","x-format","y-format","grid","axes","tooltip","cursor","disabled","loading","selected"];
app.components.x_chart.model.default_height = (180);
app.components.x_chart.model.default_padding = (12);
app.components.x_chart.model.allowed_types = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["bar",null,"area",null,"line",null], null), null);
app.components.x_chart.model.allowed_cursors = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["none",null,"x",null,"nearest",null], null), null);
app.components.x_chart.model.max_tooltip_rows = (8);
app.components.x_chart.model.dot_r = (4);
app.components.x_chart.model.tooltip_edge_pad = (8);
app.components.x_chart.model.tooltip_offset = (12);
app.components.x_chart.model.event_select = "x-chart-select";
app.components.x_chart.model.event_hover = "x-chart-hover";
app.components.x_chart.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"axes","axes",1970866440),new cljs.core.Keyword(null,"grid","grid",402978600),new cljs.core.Keyword(null,"cursor","cursor",1011937484),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.Keyword(null,"padding","padding",1660304693),new cljs.core.Keyword(null,"tooltip","tooltip",-1809677058),new cljs.core.Keyword(null,"height","height",1025178622),new cljs.core.Keyword(null,"data","data",-232669377)],[new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_selected], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_disabled], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_axes], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_grid], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_cursor], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_type], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_loading], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_padding], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_tooltip], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_chart.model.attr_height], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"array","array",-440182315,null)], null)]);
app.components.x_chart.model.parse_type = (function app$components$x_chart$model$parse_type(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_chart.model.allowed_types,s)))){
return s;
} else {
return "line";
}
});
app.components.x_chart.model.parse_cursor = (function app$components$x_chart$model$parse_cursor(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_chart.model.allowed_cursors,s)))){
return s;
} else {
return "nearest";
}
});
/**
 * Returns true if attr is present (non-nil) and not literally "false".
 */
app.components.x_chart.model.parse_bool_attr = (function app$components$x_chart$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Parse a positive integer string, returning default-val on failure.
 */
app.components.x_chart.model.parse_int_pos = (function app$components$x_chart$model$parse_int_pos(s,default_val){
if(typeof s === 'string'){
var n = parseInt(s,(10));
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return n;
} else {
return default_val;
}
} else {
return default_val;
}
});
app.components.x_chart.model.parse_point = (function app$components$x_chart$model$parse_point(raw_pt){
if(cljs.core.truth_((function (){var and__5140__auto__ = raw_pt;
if(cljs.core.truth_(and__5140__auto__)){
return (((!((raw_pt.x == null)))) && ((!((raw_pt.y == null)))));
} else {
return and__5140__auto__;
}
})())){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),raw_pt.x,new cljs.core.Keyword(null,"y","y",-1757859776),raw_pt.y], null);
} else {
return null;
}
});
app.components.x_chart.model.parse_one_series = (function app$components$x_chart$model$parse_one_series(raw_s,i){
var id = (function (){var or__5142__auto__ = (function (){var and__5140__auto__ = raw_s.id;
if(cljs.core.truth_(and__5140__auto__)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(raw_s.id));
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (""+"s"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(i));
}
})();
var label = (function (){var or__5142__auto__ = (function (){var and__5140__auto__ = raw_s.label;
if(cljs.core.truth_(and__5140__auto__)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(raw_s.label));
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return id;
}
})();
var color = (function (){var and__5140__auto__ = raw_s.color;
if(cljs.core.truth_(and__5140__auto__)){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(raw_s.color));
} else {
return and__5140__auto__;
}
})();
var data = raw_s.data;
if(cljs.core.truth_(cljs.core.array_QMARK_(data))){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"id","id",-1388402092),id,new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"color","color",1011675173),color,new cljs.core.Keyword(null,"data","data",-232669377),cljs.core.keep_indexed.cljs$core$IFn$_invoke$arity$2((function (_j,pt){
return app.components.x_chart.model.parse_point(pt);
}),cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(data))], null);
} else {
return null;
}
});
/**
 * Parse JSON string into a vector of series maps. Returns [] on error.
 */
app.components.x_chart.model.parse_series_data = (function app$components$x_chart$model$parse_series_data(json_str){
if((!(typeof json_str === 'string'))){
return cljs.core.PersistentVector.EMPTY;
} else {
try{var parsed = JSON.parse(json_str);
if(cljs.core.truth_(cljs.core.array_QMARK_(parsed))){
return cljs.core.vec(cljs.core.keep_indexed.cljs$core$IFn$_invoke$arity$2((function (i,s){
return app.components.x_chart.model.parse_one_series(s,i);
}),cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(parsed)));
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.object_QMARK_(parsed);
if(and__5140__auto__){
return cljs.core.array_QMARK_(parsed.data);
} else {
return and__5140__auto__;
}
})())){
var s = app.components.x_chart.model.parse_one_series(parsed,(0));
if(cljs.core.truth_(s)){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [s], null);
} else {
return cljs.core.PersistentVector.EMPTY;
}
} else {
return cljs.core.PersistentVector.EMPTY;

}
}
}catch (e21978){var _e = e21978;
return cljs.core.PersistentVector.EMPTY;
}}
});
/**
 * Returns "category" when x values are strings, otherwise "numeric".
 */
app.components.x_chart.model.x_kind = (function app$components$x_chart$model$x_kind(series){
var first_pt = (function (){var G__21979 = series;
var G__21979__$1 = (((G__21979 == null))?null:cljs.core.first(G__21979));
var G__21979__$2 = (((G__21979__$1 == null))?null:new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__21979__$1));
if((G__21979__$2 == null)){
return null;
} else {
return cljs.core.first(G__21979__$2);
}
})();
if(cljs.core.truth_((function (){var and__5140__auto__ = first_pt;
if(cljs.core.truth_(and__5140__auto__)){
return typeof new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(first_pt) === 'string';
} else {
return and__5140__auto__;
}
})())){
return "category";
} else {
return "numeric";
}
});
/**
 * Returns [min-y max-y] across all series data points, or [0 1] if empty.
 */
app.components.x_chart.model.domain_y = (function app$components$x_chart$model$domain_y(series){
var ys = cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic((function (s){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"y","y",-1757859776),new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s));
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([series], 0));
if(cljs.core.empty_QMARK_(ys)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(1)], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.min,ys),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.max,ys)], null);
}
});
/**
 * Returns [min-x max-x] for numeric x, or category count for category x.
 */
app.components.x_chart.model.domain_x = (function app$components$x_chart$model$domain_x(series){
var kind = app.components.x_chart.model.x_kind(series);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(kind,"category")){
var n = cljs.core.apply.cljs$core$IFn$_invoke$arity$3(cljs.core.max,(0),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (s){
return cljs.core.count(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s));
}),series));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),n) - (1))], null);
} else {
var xs = cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic((function (s){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"x","x",2099068185),new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s));
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([series], 0));
if(cljs.core.empty_QMARK_(xs)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(1)], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.min,xs),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.max,xs)], null);
}
}
});
/**
 * Compute a "nice" step size for approximately n ticks over span.
 */
app.components.x_chart.model.nice_step = (function app$components$x_chart$model$nice_step(span,n){
if((((span === (0))) || ((n === (0))))){
return (1);
} else {
var raw = (span / n);
var mag = Math.pow((10),Math.floor(Math.log10(raw)));
var norm = (raw / mag);
return (mag * (((norm <= (1)))?(1):(((norm <= (2)))?(2):(((norm <= 2.5))?2.5:(((norm <= (5)))?(5):(10)
)))));
}
});
/**
 * Returns a vector of ~n nice tick values between mn and mx.
 */
app.components.x_chart.model.ticks_y = (function app$components$x_chart$model$ticks_y(mn,mx,n){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mn,mx)){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [mn], null);
} else {
var span = (mx - mn);
var step = app.components.x_chart.model.nice_step(span,n);
var start = (Math.ceil((mn / step)) * step);
var end = (Math.floor((mx / step)) * step);
var v = start;
var acc = cljs.core.PersistentVector.EMPTY;
while(true){
if((v > (end + (step * 1.0E-4)))){
return acc;
} else {
var G__22090 = (v + step);
var G__22091 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(acc,v);
v = G__22090;
acc = G__22091;
continue;
}
break;
}
}
});
/**
 * Parse selected attr string "seriesId:index" into map, or nil.
 */
app.components.x_chart.model.parse_selected = (function app$components$x_chart$model$parse_selected(s){
if(typeof s === 'string'){
var parts = s.split(":");
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),parts.length)){
var idx = parseInt((parts[(1)]),(10));
if(cljs.core.truth_(isNaN(idx))){
return null;
} else {
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"series-id","series-id",226875035),(parts[(0)]),new cljs.core.Keyword(null,"index","index",-1531685915),idx], null);
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_chart.model.normalize = (function app$components$x_chart$model$normalize(p__21983){
var map__21984 = p__21983;
var map__21984__$1 = cljs.core.__destructure_map(map__21984);
var x_format_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"x-format-raw","x-format-raw",-1683657190));
var selected_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"selected-raw","selected-raw",-1471626848));
var padding_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"padding-raw","padding-raw",-847772255));
var tooltip_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"tooltip-present?","tooltip-present?",-2098824029));
var y_format_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"y-format-raw","y-format-raw",-1098541692));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var grid_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"grid-present?","grid-present?",-468948562));
var axes_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"axes-raw","axes-raw",-839946224));
var height_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"height-raw","height-raw",-867301006));
var cursor_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"cursor-raw","cursor-raw",-1169819084));
var loading_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"loading-present?","loading-present?",-1827648748));
var type_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"type-raw","type-raw",-967209994));
var data_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21984__$1,new cljs.core.Keyword(null,"data-raw","data-raw",822066711));
var series = app.components.x_chart.model.parse_series_data(data_raw);
var vec__21985 = app.components.x_chart.model.domain_y(series);
var ymn = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21985,(0),null);
var ymx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21985,(1),null);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"y-domain","y-domain",-969203007),new cljs.core.Keyword(null,"selected","selected",574897764),new cljs.core.Keyword(null,"axes?","axes?",1786285669),new cljs.core.Keyword(null,"series","series",600710694),new cljs.core.Keyword(null,"loading?","loading?",1905707049),new cljs.core.Keyword(null,"y-fmt","y-fmt",2092412811),new cljs.core.Keyword(null,"cursor","cursor",1011937484),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"padding","padding",1660304693),new cljs.core.Keyword(null,"x-fmt","x-fmt",2073358008),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"tooltip?","tooltip?",-642753154),new cljs.core.Keyword(null,"y-ticks","y-ticks",-843622722),new cljs.core.Keyword(null,"height","height",1025178622),new cljs.core.Keyword(null,"x-kind","x-kind",693167455),new cljs.core.Keyword(null,"grid?","grid?",-288406689)],[new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ymn,ymx], null),app.components.x_chart.model.parse_selected(selected_raw),(((axes_raw == null))?true:app.components.x_chart.model.parse_bool_attr(axes_raw)),series,app.components.x_chart.model.parse_bool_attr(loading_present_QMARK_),y_format_raw,app.components.x_chart.model.parse_cursor(cursor_raw),app.components.x_chart.model.parse_type(type_raw),app.components.x_chart.model.parse_int_pos(padding_raw,app.components.x_chart.model.default_padding),x_format_raw,app.components.x_chart.model.parse_bool_attr(disabled_present_QMARK_),app.components.x_chart.model.parse_bool_attr(tooltip_present_QMARK_),app.components.x_chart.model.ticks_y(ymn,ymx,(5)),app.components.x_chart.model.parse_int_pos(height_raw,app.components.x_chart.model.default_height),app.components.x_chart.model.x_kind(series),(((grid_present_QMARK_ == null))?true:app.components.x_chart.model.parse_bool_attr(grid_present_QMARK_))]);
});
/**
 * Format a numeric value v using fmt-str spec.
 * fmt-str: nil | "int" | "pct" | "abbr" | "fixed:N" | "raw"
 */
app.components.x_chart.model.format_value = (function app$components$x_chart$model$format_value(v,fmt_str){
if((fmt_str == null)){
var abs = Math.abs(v);
if((abs >= (1000000))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round((v / (1000000))))+"M");
} else {
if((abs >= (1000))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round((v / (1000))))+"K");
} else {
if(cljs.core.truth_(Number.isInteger(v))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));
} else {
return v.toFixed((1));

}
}
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(fmt_str,"int")){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round(v)));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(fmt_str,"pct")){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)+"%");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(fmt_str,"abbr")){
var abs = Math.abs(v);
if((abs >= (1000000000))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v / (1000000000)).toFixed((1)))+"B");
} else {
if((abs >= (1000000))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v / (1000000)).toFixed((1)))+"M");
} else {
if((abs >= (1000))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((v / (1000)).toFixed((1)))+"K");
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));

}
}
}
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(fmt_str,"raw")){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));
} else {
if(cljs.core.truth_(fmt_str.startsWith("fixed:"))){
var n = parseInt(fmt_str.slice((6)),(10));
if(cljs.core.truth_(isNaN(n))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));
} else {
return v.toFixed(n);
}
} else {
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));

}
}
}
}
}
}
});
/**
 * Compute tooltip position with edge-safe flip logic.
 * px/py = cursor position; tw/th = tooltip size; W/H = container size.
 * Returns {:left l :top t}.
 */
app.components.x_chart.model.tooltip_position = (function app$components$x_chart$model$tooltip_position(px,py,tw,th,W,H,edge_pad,offset){
var left = ((((((px + tw) + offset) + edge_pad) > W))?((px - tw) - offset):(px + offset));
var top = (((((py + th) + edge_pad) > H))?((py - th) - edge_pad):(py - (4)));
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"left","left",-399115937),cljs.core.max.cljs$core$IFn$_invoke$arity$2(edge_pad,cljs.core.min.cljs$core$IFn$_invoke$arity$2(left,((W - tw) - edge_pad))),new cljs.core.Keyword(null,"top","top",-1856271961),cljs.core.max.cljs$core$IFn$_invoke$arity$2(edge_pad,cljs.core.min.cljs$core$IFn$_invoke$arity$2(top,((H - th) - edge_pad)))], null);
});
/**
 * For cursor='x': find one point per series closest to mx.
 * all-pts is a flat seq of points with :id and :px keys.
 */
app.components.x_chart.model.find_pts_at_x = (function app$components$x_chart$model$find_pts_at_x(all_pts,mx){
var by_id = cljs.core.group_by(new cljs.core.Keyword(null,"id","id",-1388402092),all_pts);
return cljs.core.vec(cljs.core.keep.cljs$core$IFn$_invoke$arity$2((function (p__21994){
var vec__21995 = p__21994;
var _id = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21995,(0),null);
var pts = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21995,(1),null);
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (best,pt){
var d = Math.abs((new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt) - mx));
if((((best == null)) || ((d < new cljs.core.Keyword(null,"d","d",1972142424).cljs$core$IFn$_invoke$arity$1(best))))){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pt,new cljs.core.Keyword(null,"d","d",1972142424),d);
} else {
return best;
}
}),null,pts);
}),by_id));
});

//# sourceMappingURL=app.components.x_chart.model.js.map
