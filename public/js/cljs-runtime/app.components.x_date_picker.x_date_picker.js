goog.provide('app.components.x_date_picker.x_date_picker');
goog.scope(function(){
  app.components.x_date_picker.x_date_picker.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_date_picker.x_date_picker.k_refs = "__xDatePickerRefs";
app.components.x_date_picker.x_date_picker.k_state = "__xDatePickerState";
app.components.x_date_picker.x_date_picker.k_focused = "__xDatePickerFocused";
app.components.x_date_picker.x_date_picker.k_display = "__xDatePickerDisplay";
app.components.x_date_picker.x_date_picker.k_pending = "__xDatePickerPendingDisplay";
app.components.x_date_picker.x_date_picker.k_wd_done = "__xDatePickerWeekdaysDone";
app.components.x_date_picker.x_date_picker.k_handlers = "__xDatePickerHandlers";
app.components.x_date_picker.x_date_picker.k_range_step = "__xDatePickerRangeStep";
app.components.x_date_picker.x_date_picker.make_el = (function app$components$x_date_picker$x_date_picker$make_el(tag){
return document.createElement(tag);
});
app.components.x_date_picker.x_date_picker.get_ref = (function app$components$x_date_picker$x_date_picker$get_ref(el,k){
return app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs),cljs.core.name(k));
});
app.components.x_date_picker.x_date_picker.dispatch_BANG_ = (function app$components$x_date_picker$x_date_picker$dispatch_BANG_(el,event_name,cancelable,detail){
var ev = (new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cljs.core.boolean$(cancelable)})));
el.dispatchEvent(ev);

return ev;
});
/**
 * Read all observed attrs, canonicalize, cache result on element.
 */
app.components.x_date_picker.x_date_picker.read_state_BANG_ = (function app$components$x_date_picker$x_date_picker$read_state_BANG_(el){
var mode_raw = el.getAttribute(app.components.x_date_picker.model.attr_mode);
var mode = app.components.x_date_picker.model.parse_mode(mode_raw);
var canon = app.components.x_date_picker.model.canonicalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Keyword(null,"format","format",-1306924766),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.Keyword(null,"range-allow-same-day?","range-allow-same-day?",-714516856),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"mode","mode",654403691),new cljs.core.Keyword(null,"start","start",-355208981),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411),new cljs.core.Keyword(null,"end","end",-268185958),new cljs.core.Keyword(null,"separator","separator",-1628749125)],[el.getAttribute(app.components.x_date_picker.model.attr_min),el.getAttribute(app.components.x_date_picker.model.attr_format),el.getAttribute(app.components.x_date_picker.model.attr_locale),app.components.x_date_picker.model.parse_bool_attr(el.getAttribute(app.components.x_date_picker.model.attr_range_allow_same)),el.getAttribute(app.components.x_date_picker.model.attr_value),mode_raw,el.getAttribute(app.components.x_date_picker.model.attr_start),el.getAttribute(app.components.x_date_picker.model.attr_max),app.components.x_date_picker.model.parse_bool_attr(el.getAttribute(app.components.x_date_picker.model.attr_auto_swap)),el.getAttribute(app.components.x_date_picker.model.attr_end),el.getAttribute(app.components.x_date_picker.model.attr_separator)]));
var cfg = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"format","format",-1306924766),new cljs.core.Keyword(null,"format","format",-1306924766).cljs$core$IFn$_invoke$arity$1(canon),new cljs.core.Keyword(null,"locale","locale",-2115712697),new cljs.core.Keyword(null,"locale","locale",-2115712697).cljs$core$IFn$_invoke$arity$1(canon)], null);
var disp = app.components.x_date_picker.model.display_value(canon,cfg);
var existing = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var cur_month = (cljs.core.truth_(existing)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(existing,"month"):null);
var anchor = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"value-d","value-d",-1768534876).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var or__5142__auto____$1 = new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(or__5142__auto____$1)){
return or__5142__auto____$1;
} else {
return (new Date(Date.now()));
}
}
})();
var month = (function (){var or__5142__auto__ = cur_month;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_date_picker.model.start_of_month(anchor);
}
})();
var state = ({"canon": canon, "cfg": cfg, "disp": disp, "month": month});
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_state,state);

return state;
});
app.components.x_date_picker.x_date_picker.day_out_of_range_QMARK_ = (function app$components$x_date_picker$x_date_picker$day_out_of_range_QMARK_(d,canon){
var or__5142__auto__ = (function (){var and__5140__auto__ = new cljs.core.Keyword(null,"min-d","min-d",-1118792932).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(and__5140__auto__)){
return (app.components.x_date_picker.model.compare_date(d,new cljs.core.Keyword(null,"min-d","min-d",-1118792932).cljs$core$IFn$_invoke$arity$1(canon)) < (0));
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var and__5140__auto__ = new cljs.core.Keyword(null,"max-d","max-d",-814680979).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(and__5140__auto__)){
return (app.components.x_date_picker.model.compare_date(d,new cljs.core.Keyword(null,"max-d","max-d",-814680979).cljs$core$IFn$_invoke$arity$1(canon)) > (0));
} else {
return and__5140__auto__;
}
}
});
app.components.x_date_picker.x_date_picker.compute_flags = (function app$components$x_date_picker$x_date_picker$compute_flags(d,canon){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"mode","mode",654403691).cljs$core$IFn$_invoke$arity$1(canon),new cljs.core.Keyword(null,"single","single",1551466437))){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"selected?","selected?",-742502788),(function (){var and__5140__auto__ = new cljs.core.Keyword(null,"value-d","value-d",-1768534876).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),app.components.x_date_picker.model.compare_date(d,new cljs.core.Keyword(null,"value-d","value-d",-1768534876).cljs$core$IFn$_invoke$arity$1(canon)));
} else {
return and__5140__auto__;
}
})(),new cljs.core.Keyword(null,"in-range?","in-range?",-291779262),false,new cljs.core.Keyword(null,"edge?","edge?",203916303),false], null);
} else {
var start = new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon);
var end = new cljs.core.Keyword(null,"end-d","end-d",-1316370898).cljs$core$IFn$_invoke$arity$1(canon);
var sel_QMARK_ = (function (){var or__5142__auto__ = (function (){var and__5140__auto__ = start;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),app.components.x_date_picker.model.compare_date(d,start));
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var and__5140__auto__ = end;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),app.components.x_date_picker.model.compare_date(d,end));
} else {
return and__5140__auto__;
}
}
})();
var in_r_QMARK_ = (function (){var and__5140__auto__ = start;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = end;
if(cljs.core.truth_(and__5140__auto____$1)){
return app.components.x_date_picker.model.in_range_QMARK_(d,start,end);
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})();
var edge_QMARK_ = (function (){var or__5142__auto__ = (function (){var and__5140__auto__ = start;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),app.components.x_date_picker.model.compare_date(d,start));
} else {
return and__5140__auto__;
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var and__5140__auto__ = end;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),app.components.x_date_picker.model.compare_date(d,end));
} else {
return and__5140__auto__;
}
}
})();
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"selected?","selected?",-742502788),sel_QMARK_,new cljs.core.Keyword(null,"in-range?","in-range?",-291779262),in_r_QMARK_,new cljs.core.Keyword(null,"edge?","edge?",203916303),edge_QMARK_], null);
}
});
app.components.x_date_picker.x_date_picker.render_grid_BANG_ = (function app$components$x_date_picker$x_date_picker$render_grid_BANG_(el,grid,canon){
(grid.textContent = "");

var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var month = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"month"):null);
var items = app.components.x_date_picker.model.month_grid(month);
var seq__12310 = cljs.core.seq(items);
var chunk__12311 = null;
var count__12312 = (0);
var i__12313 = (0);
while(true){
if((i__12313 < count__12312)){
var map__12318 = chunk__12311.cljs$core$IIndexed$_nth$arity$2(null,i__12313);
var map__12318__$1 = cljs.core.__destructure_map(map__12318);
var date = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12318__$1,new cljs.core.Keyword(null,"date","date",-1463434462));
var in_month_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12318__$1,new cljs.core.Keyword(null,"in-month?","in-month?",-1630426251));
var btn_12324 = app.components.x_date_picker.x_date_picker.make_el("button");
var iso_12325 = app.components.x_date_picker.model.date__GT_iso(date);
var day_12326 = date.getUTCDate();
var disabled_QMARK__12327 = app.components.x_date_picker.x_date_picker.day_out_of_range_QMARK_(date,canon);
var map__12319_12328 = app.components.x_date_picker.x_date_picker.compute_flags(date,canon);
var map__12319_12329__$1 = cljs.core.__destructure_map(map__12319_12328);
var selected_QMARK__12330 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12319_12329__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var in_range_QMARK__12331 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12319_12329__$1,new cljs.core.Keyword(null,"in-range?","in-range?",-291779262));
var edge_QMARK__12332 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12319_12329__$1,new cljs.core.Keyword(null,"edge?","edge?",203916303));
btn_12324.setAttribute("part","day");

btn_12324.setAttribute("type","button");

btn_12324.setAttribute("data-iso",iso_12325);

btn_12324.setAttribute("data-outside",(cljs.core.truth_(in_month_QMARK_)?"false":"true"));

btn_12324.setAttribute("data-disabled",(cljs.core.truth_(disabled_QMARK__12327)?"true":"false"));

btn_12324.setAttribute("data-selected",(cljs.core.truth_(selected_QMARK__12330)?"true":"false"));

btn_12324.setAttribute("data-in-range",(cljs.core.truth_(in_range_QMARK__12331)?"true":"false"));

btn_12324.setAttribute("data-range-edge",(cljs.core.truth_(edge_QMARK__12332)?"true":"false"));

if(cljs.core.truth_(disabled_QMARK__12327)){
btn_12324.setAttribute("disabled","");
} else {
}

(btn_12324.textContent = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(day_12326)));

grid.appendChild(btn_12324);


var G__12333 = seq__12310;
var G__12334 = chunk__12311;
var G__12335 = count__12312;
var G__12336 = (i__12313 + (1));
seq__12310 = G__12333;
chunk__12311 = G__12334;
count__12312 = G__12335;
i__12313 = G__12336;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__12310);
if(temp__5823__auto__){
var seq__12310__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__12310__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__12310__$1);
var G__12337 = cljs.core.chunk_rest(seq__12310__$1);
var G__12338 = c__5673__auto__;
var G__12339 = cljs.core.count(c__5673__auto__);
var G__12340 = (0);
seq__12310 = G__12337;
chunk__12311 = G__12338;
count__12312 = G__12339;
i__12313 = G__12340;
continue;
} else {
var map__12320 = cljs.core.first(seq__12310__$1);
var map__12320__$1 = cljs.core.__destructure_map(map__12320);
var date = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12320__$1,new cljs.core.Keyword(null,"date","date",-1463434462));
var in_month_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12320__$1,new cljs.core.Keyword(null,"in-month?","in-month?",-1630426251));
var btn_12341 = app.components.x_date_picker.x_date_picker.make_el("button");
var iso_12342 = app.components.x_date_picker.model.date__GT_iso(date);
var day_12343 = date.getUTCDate();
var disabled_QMARK__12344 = app.components.x_date_picker.x_date_picker.day_out_of_range_QMARK_(date,canon);
var map__12321_12345 = app.components.x_date_picker.x_date_picker.compute_flags(date,canon);
var map__12321_12346__$1 = cljs.core.__destructure_map(map__12321_12345);
var selected_QMARK__12347 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12321_12346__$1,new cljs.core.Keyword(null,"selected?","selected?",-742502788));
var in_range_QMARK__12348 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12321_12346__$1,new cljs.core.Keyword(null,"in-range?","in-range?",-291779262));
var edge_QMARK__12349 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12321_12346__$1,new cljs.core.Keyword(null,"edge?","edge?",203916303));
btn_12341.setAttribute("part","day");

btn_12341.setAttribute("type","button");

btn_12341.setAttribute("data-iso",iso_12342);

btn_12341.setAttribute("data-outside",(cljs.core.truth_(in_month_QMARK_)?"false":"true"));

btn_12341.setAttribute("data-disabled",(cljs.core.truth_(disabled_QMARK__12344)?"true":"false"));

btn_12341.setAttribute("data-selected",(cljs.core.truth_(selected_QMARK__12347)?"true":"false"));

btn_12341.setAttribute("data-in-range",(cljs.core.truth_(in_range_QMARK__12348)?"true":"false"));

btn_12341.setAttribute("data-range-edge",(cljs.core.truth_(edge_QMARK__12349)?"true":"false"));

if(cljs.core.truth_(disabled_QMARK__12344)){
btn_12341.setAttribute("disabled","");
} else {
}

(btn_12341.textContent = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(day_12343)));

grid.appendChild(btn_12341);


var G__12350 = cljs.core.next(seq__12310__$1);
var G__12351 = null;
var G__12352 = (0);
var G__12353 = (0);
seq__12310 = G__12350;
chunk__12311 = G__12351;
count__12312 = G__12352;
i__12313 = G__12353;
continue;
}
} else {
return null;
}
}
break;
}
});
app.components.x_date_picker.x_date_picker.render_weekdays_BANG_ = (function app$components$x_date_picker$x_date_picker$render_weekdays_BANG_(el,weekdays_el){
if(cljs.core.truth_(app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_wd_done))){
return null;
} else {
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_wd_done,true);

var labels = ["Su","Mo","Tu","We","Th","Fr","Sa"];
var n__5741__auto__ = (7);
var i = (0);
while(true){
if((i < n__5741__auto__)){
var div_12354 = app.components.x_date_picker.x_date_picker.make_el("div");
div_12354.setAttribute("part","weekday");

div_12354.setAttribute("aria-hidden","true");

(div_12354.textContent = (labels[i]));

weekdays_el.appendChild(div_12354);

var G__12355 = (i + (1));
i = G__12355;
continue;
} else {
return null;
}
break;
}
}
});
app.components.x_date_picker.x_date_picker.month_name = (function app$components$x_date_picker$x_date_picker$month_name(month_date,locale){
var opts = ({"year": "numeric", "month": "long", "timeZone": "UTC"});
var loc = (function (){var or__5142__auto__ = locale;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "default";
}
})();
return (new Intl.DateTimeFormat(loc,opts)).format(month_date);
});
app.components.x_date_picker.x_date_picker.render_calendar_BANG_ = (function app$components$x_date_picker$x_date_picker$render_calendar_BANG_(el){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
if(cljs.core.truth_(refs)){
var month_label = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"month-label");
var grid = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"grid");
var weekdays_el = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"weekdays");
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var canon = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"canon"):null);
var month = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"month"):null);
var cfg = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"cfg"):null);
var locale = (cljs.core.truth_(cfg)?new cljs.core.Keyword(null,"locale","locale",-2115712697).cljs$core$IFn$_invoke$arity$1(cfg):null);
if(cljs.core.truth_(month_label)){
(month_label.textContent = app.components.x_date_picker.x_date_picker.month_name(month,locale));
} else {
}

app.components.x_date_picker.x_date_picker.render_weekdays_BANG_(el,weekdays_el);

if(cljs.core.truth_((function (){var and__5140__auto__ = grid;
if(cljs.core.truth_(and__5140__auto__)){
return canon;
} else {
return and__5140__auto__;
}
})())){
return app.components.x_date_picker.x_date_picker.render_grid_BANG_(el,grid,canon);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.sync_input_display_BANG_ = (function app$components$x_date_picker$x_date_picker$sync_input_display_BANG_(el){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input"):null);
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var disp = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"disp"):null);
var focused = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_focused);
if(cljs.core.truth_((function (){var and__5140__auto__ = inp;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(focused);
} else {
return and__5140__auto__;
}
})())){
return (inp.value = (function (){var or__5142__auto__ = disp;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.render_BANG_ = (function app$components$x_date_picker$x_date_picker$render_BANG_(el){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
if(cljs.core.truth_(refs)){
var inp = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input");
var btn = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"btn");
var sr = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"sr");
var disabled_QMARK_ = el.hasAttribute(app.components.x_date_picker.model.attr_disabled);
var readonly_QMARK_ = el.hasAttribute(app.components.x_date_picker.model.attr_readonly);
var placeholder = el.getAttribute(app.components.x_date_picker.model.attr_placeholder);
var aria_label = el.getAttribute(app.components.x_date_picker.model.attr_aria_label);
var aria_desc = el.getAttribute(app.components.x_date_picker.model.attr_aria_describedby);
var open_QMARK_ = el.hasAttribute("open");
if(cljs.core.truth_(btn)){
if(cljs.core.truth_(disabled_QMARK_)){
btn.setAttribute("disabled","");
} else {
btn.removeAttribute("disabled");
}
} else {
}

if(cljs.core.truth_(inp)){
if(cljs.core.truth_(disabled_QMARK_)){
inp.setAttribute("disabled","");

inp.setAttribute("aria-disabled","true");
} else {
inp.removeAttribute("disabled");

inp.removeAttribute("aria-disabled");
}

if(cljs.core.truth_(readonly_QMARK_)){
inp.setAttribute("readonly","");
} else {
}

if(cljs.core.truth_(readonly_QMARK_)){
} else {
inp.removeAttribute("readonly");
}

if(cljs.core.truth_(placeholder)){
inp.setAttribute("placeholder",placeholder);
} else {
}

if(cljs.core.truth_(aria_label)){
inp.setAttribute("aria-label",aria_label);
} else {
}

if(cljs.core.truth_(aria_desc)){
inp.setAttribute("aria-describedby",aria_desc);
} else {
}
} else {
}

app.components.x_date_picker.x_date_picker.sync_input_display_BANG_(el);

return app.components.x_date_picker.x_date_picker.render_calendar_BANG_(el);
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.set_single_value_BANG_ = (function app$components$x_date_picker$x_date_picker$set_single_value_BANG_(el,d){
if(cljs.core.truth_(d)){
return el.setAttribute(app.components.x_date_picker.model.attr_value,app.components.x_date_picker.model.date__GT_iso(d));
} else {
return el.removeAttribute(app.components.x_date_picker.model.attr_value);
}
});
/**
 * Apply date selection. For single: set value. For range: smart first/second click.
 */
app.components.x_date_picker.x_date_picker.do_select_date_BANG_ = (function app$components$x_date_picker$x_date_picker$do_select_date_BANG_(el,d,reason){
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var canon = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"canon"):null);
var mode = (cljs.core.truth_(canon)?new cljs.core.Keyword(null,"mode","mode",654403691).cljs$core$IFn$_invoke$arity$1(canon):null);
var detail = ({"iso": app.components.x_date_picker.model.date__GT_iso(d), "reason": reason});
var ev = app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change_request,true,detail);
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mode,new cljs.core.Keyword(null,"single","single",1551466437))){
app.components.x_date_picker.x_date_picker.set_single_value_BANG_(el,d);

app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

app.components.x_date_picker.x_date_picker.render_BANG_(el);

app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change,false,detail);

if(cljs.core.truth_(el.hasAttribute(app.components.x_date_picker.model.attr_close_on_select))){
el.removeAttribute("open");

return app.components.x_date_picker.x_date_picker.render_BANG_(el);
} else {
return null;
}
} else {
var step = (function (){var or__5142__auto__ = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_range_step);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})();
var cur_start = new cljs.core.Keyword(null,"start-d","start-d",-1546785820).cljs$core$IFn$_invoke$arity$1(canon);
var cur_end = new cljs.core.Keyword(null,"end-d","end-d",-1316370898).cljs$core$IFn$_invoke$arity$1(canon);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(step,(0))){
el.setAttribute(app.components.x_date_picker.model.attr_start,app.components.x_date_picker.model.date__GT_iso(d));

el.removeAttribute(app.components.x_date_picker.model.attr_end);

app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_range_step,(1));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(step,(1))){
var allow_same_QMARK__12356 = new cljs.core.Keyword(null,"allow-same-day?","allow-same-day?",-1971558934).cljs$core$IFn$_invoke$arity$1(canon);
var cmp_12357 = app.components.x_date_picker.model.compare_date(d,cur_start);
var valid_QMARK__12358 = (function (){var or__5142__auto__ = (cmp_12357 > (0));
if(or__5142__auto__){
return or__5142__auto__;
} else {
var and__5140__auto__ = allow_same_QMARK__12356;
if(cljs.core.truth_(and__5140__auto__)){
return (cmp_12357 === (0));
} else {
return and__5140__auto__;
}
}
})();
if(cljs.core.truth_(valid_QMARK__12358)){
var final_start_12359 = (cljs.core.truth_(new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411).cljs$core$IFn$_invoke$arity$1(canon))?app.components.x_date_picker.model.min_date(cur_start,d):cur_start);
var final_end_12360 = (cljs.core.truth_(new cljs.core.Keyword(null,"auto-swap?","auto-swap?",-1675454411).cljs$core$IFn$_invoke$arity$1(canon))?app.components.x_date_picker.model.max_date(cur_start,d):d);
el.setAttribute(app.components.x_date_picker.model.attr_start,app.components.x_date_picker.model.date__GT_iso(final_start_12359));

el.setAttribute(app.components.x_date_picker.model.attr_end,app.components.x_date_picker.model.date__GT_iso(final_end_12360));

app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_range_step,(0));

if(cljs.core.truth_((function (){var and__5140__auto__ = el.hasAttribute(app.components.x_date_picker.model.attr_close_on_select);
if(cljs.core.truth_(and__5140__auto__)){
return (((!((final_start_12359 == null)))) && ((!((final_end_12360 == null)))));
} else {
return and__5140__auto__;
}
})())){
el.removeAttribute("open");
} else {
}
} else {
el.setAttribute(app.components.x_date_picker.model.attr_start,app.components.x_date_picker.model.date__GT_iso(d));

el.removeAttribute(app.components.x_date_picker.model.attr_end);

app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_range_step,(1));
}
} else {
el.setAttribute(app.components.x_date_picker.model.attr_start,app.components.x_date_picker.model.date__GT_iso(d));

el.removeAttribute(app.components.x_date_picker.model.attr_end);

app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_range_step,(1));

}
}

app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

app.components.x_date_picker.x_date_picker.render_BANG_(el);

return app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change,false,detail);
}
}
});
app.components.x_date_picker.x_date_picker.commit_display_BANG_ = (function app$components$x_date_picker$x_date_picker$commit_display_BANG_(el,reason){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input"):null);
var val = (cljs.core.truth_(inp)?inp.value:null);
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var canon = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"canon"):null);
var mode = new cljs.core.Keyword(null,"mode","mode",654403691).cljs$core$IFn$_invoke$arity$1(canon);
app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_input,false,({"value": val}));

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mode,new cljs.core.Keyword(null,"single","single",1551466437))){
var map__12322 = app.components.x_date_picker.model.parse_display__GT_single(val);
var map__12322__$1 = cljs.core.__destructure_map(map__12322);
var ok_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12322__$1,new cljs.core.Keyword(null,"ok?","ok?",447310304));
var date = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12322__$1,new cljs.core.Keyword(null,"date","date",-1463434462));
if(cljs.core.truth_(ok_QMARK_)){
var ev = app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change_request,true,({"iso": app.components.x_date_picker.model.date__GT_iso(date), "reason": reason}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
app.components.x_date_picker.x_date_picker.set_single_value_BANG_(el,date);

app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

app.components.x_date_picker.x_date_picker.render_BANG_(el);

return app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change,false,({"iso": app.components.x_date_picker.model.date__GT_iso(date), "reason": reason}));
}
} else {
return null;
}
} else {
var map__12323 = app.components.x_date_picker.model.parse_display__GT_range(val,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"separator","separator",-1628749125),new cljs.core.Keyword(null,"separator","separator",-1628749125).cljs$core$IFn$_invoke$arity$1(canon)], null));
var map__12323__$1 = cljs.core.__destructure_map(map__12323);
var ok_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12323__$1,new cljs.core.Keyword(null,"ok?","ok?",447310304));
var start = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12323__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__12323__$1,new cljs.core.Keyword(null,"end","end",-268185958));
if(cljs.core.truth_(ok_QMARK_)){
var ev = app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change_request,true,({"start-iso": app.components.x_date_picker.model.date__GT_iso(start), "end-iso": app.components.x_date_picker.model.date__GT_iso(end), "reason": reason}));
if(cljs.core.truth_(ev.defaultPrevented)){
return null;
} else {
el.setAttribute(app.components.x_date_picker.model.attr_start,app.components.x_date_picker.model.date__GT_iso(start));

el.setAttribute(app.components.x_date_picker.model.attr_end,app.components.x_date_picker.model.date__GT_iso(end));

app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_range_step,(0));

app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

app.components.x_date_picker.x_date_picker.render_BANG_(el);

return app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_change,false,({"start-iso": app.components.x_date_picker.model.date__GT_iso(start), "end-iso": app.components.x_date_picker.model.date__GT_iso(end), "reason": reason}));
}
} else {
return null;
}
}
});
app.components.x_date_picker.x_date_picker.navigate_month_BANG_ = (function app$components$x_date_picker$x_date_picker$navigate_month_BANG_(el,direction){
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_state);
var cur = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"month"):null);
if(cljs.core.truth_(cur)){
var y = cur.getUTCFullYear();
var mo = cur.getUTCMonth();
var next_mo = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(direction,new cljs.core.Keyword(null,"next","next",-117701485)))?(new Date(Date.UTC(y,(mo + (1)),(1)))):(new Date(Date.UTC(y,(mo - (1)),(1)))));
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(state,"month",next_mo);

return app.components.x_date_picker.x_date_picker.render_calendar_BANG_(el);
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.open_popover_BANG_ = (function app$components$x_date_picker$x_date_picker$open_popover_BANG_(el){
if(cljs.core.truth_(el.hasAttribute("open"))){
return null;
} else {
el.setAttribute("open","");

return app.components.x_date_picker.x_date_picker.render_calendar_BANG_(el);
}
});
app.components.x_date_picker.x_date_picker.close_popover_BANG_ = (function app$components$x_date_picker$x_date_picker$close_popover_BANG_(el){
if(cljs.core.truth_(el.hasAttribute("open"))){
return el.removeAttribute("open");
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.style_text = (""+":host{display:block;color-scheme:light dark;}"+"[part=container]{position:relative;display:flex;align-items:stretch;gap:8px;width:100%;}"+"[part=input]{"+"flex:1;min-width:0;appearance:none;box-sizing:border-box;"+"border:1px solid var(--x-date-picker-border,#cbd5e1);"+"border-radius:var(--x-date-picker-radius,8px);"+"padding:8px 12px;font-size:0.9375rem;"+"background:var(--x-date-picker-input-bg,#fff);"+"color:var(--x-date-picker-text,inherit);"+"outline:none;"+"}"+"[part=input]:focus{border-color:var(--x-date-picker-focus,#60a5fa);box-shadow:0 0 0 3px rgba(96,165,250,0.25);}"+"[part=input][disabled]{opacity:0.5;cursor:default;}"+"[part=btn][disabled]{opacity:0.5;cursor:default;pointer-events:none;}"+"[part=input]::placeholder{color:var(--x-date-picker-placeholder,#94a3b8);}"+"[part=btn]{"+"all:unset;cursor:pointer;flex:0 0 auto;width:42px;height:42px;"+"display:inline-flex;align-items:center;justify-content:center;"+"border:1px solid var(--x-date-picker-border,#cbd5e1);"+"border-radius:var(--x-date-picker-radius,8px);"+"background:var(--x-date-picker-btn-bg,#f8fafc);"+"color:var(--x-date-picker-text,inherit);"+"box-sizing:border-box;"+"}"+"[part=btn]:hover{background:var(--x-date-picker-btn-hover,#f1f5f9);}"+"[part=btn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"+"[part=popover]{"+"display:none;position:absolute;z-index:1000;"+"top:calc(100% + 8px);left:0;"+"background:var(--x-date-picker-popover-bg,#fff);"+"border:1px solid var(--x-date-picker-border,#e2e8f0);"+"border-radius:var(--x-date-picker-popover-radius,12px);"+"box-shadow:0 8px 32px rgba(0,0,0,0.12),0 2px 8px rgba(0,0,0,0.06);"+"padding:16px;"+"width:var(--x-date-picker-popover-width,304px);"+"box-sizing:border-box;"+"}"+":host([open]) [part=popover]{display:block;}"+"[part=nav]{"+"display:flex;align-items:center;justify-content:space-between;"+"margin-bottom:12px;"+"}"+"[part=navbtn]{"+"all:unset;cursor:pointer;display:inline-flex;align-items:center;justify-content:center;"+"width:32px;height:32px;border-radius:6px;"+"color:var(--x-date-picker-text,inherit);"+"}"+"[part=navbtn]:hover{background:var(--x-date-picker-nav-hover,#f1f5f9);}"+"[part=navbtn]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"+"[part=monthlabel]{font-weight:600;font-size:0.9375rem;}"+"[part=weekdays]{"+"display:grid;grid-template-columns:repeat(7,1fr);gap:4px;margin-bottom:4px;"+"}"+"[part=weekday]{"+"text-align:center;font-size:0.75rem;font-weight:600;"+"color:var(--x-date-picker-weekday,#94a3b8);padding:4px 0;"+"}"+"[part=grid]{display:grid;grid-template-columns:repeat(7,1fr);gap:4px;}"+"[part=day]{"+"all:unset;cursor:pointer;box-sizing:border-box;"+"display:flex;align-items:center;justify-content:center;"+"height:36px;border-radius:6px;"+"font-size:0.875rem;"+"color:var(--x-date-picker-day-text,inherit);"+"}"+"[part=day]:hover:not([disabled]){background:var(--x-date-picker-day-hover,#f1f5f9);}"+"[part=day]:focus-visible{outline:2px solid var(--x-date-picker-focus,#60a5fa);}"+"[part=day][data-outside=true]{opacity:0.4;}"+"[part=day][data-disabled=true]{opacity:0.3;cursor:default;pointer-events:none;}"+"[part=day][data-selected=true]{"+"background:var(--x-date-picker-selected-bg,#2563eb);"+"color:var(--x-date-picker-selected-text,#fff);"+"font-weight:600;"+"}"+"[part=day][data-in-range=true]:not([data-range-edge=true]){"+"background:var(--x-date-picker-range-bg,#dbeafe);"+"color:var(--x-date-picker-range-text,#1e40af);"+"border-radius:0;"+"}"+"[part=day][data-range-edge=true]{"+"background:var(--x-date-picker-selected-bg,#2563eb);"+"color:var(--x-date-picker-selected-text,#fff);"+"font-weight:600;"+"}"+"[part=sr-status]{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0,0,0,0);white-space:nowrap;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-date-picker-border:#334155;"+"--x-date-picker-input-bg:#0f172a;"+"--x-date-picker-btn-bg:#1e293b;"+"--x-date-picker-btn-hover:#334155;"+"--x-date-picker-popover-bg:#1e293b;"+"--x-date-picker-day-hover:#334155;"+"--x-date-picker-selected-bg:#3b82f6;"+"--x-date-picker-range-bg:#1e3a8a;"+"--x-date-picker-range-text:#93c5fd;"+"--x-date-picker-nav-hover:#334155;"+"}"+"}"+"@media (prefers-reduced-motion:reduce){"+"[part=popover]{transition:none;}"+"}");
app.components.x_date_picker.x_date_picker.make_shadow_BANG_ = (function app$components$x_date_picker$x_date_picker$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = app.components.x_date_picker.x_date_picker.make_el("style");
var container = app.components.x_date_picker.x_date_picker.make_el("div");
var inp = app.components.x_date_picker.x_date_picker.make_el("input");
var btn = app.components.x_date_picker.x_date_picker.make_el("button");
var popover = app.components.x_date_picker.x_date_picker.make_el("div");
var nav = app.components.x_date_picker.x_date_picker.make_el("div");
var nav_prev = app.components.x_date_picker.x_date_picker.make_el("button");
var month_lbl = app.components.x_date_picker.x_date_picker.make_el("div");
var nav_next = app.components.x_date_picker.x_date_picker.make_el("button");
var weekdays = app.components.x_date_picker.x_date_picker.make_el("div");
var grid = app.components.x_date_picker.x_date_picker.make_el("div");
var sr = app.components.x_date_picker.x_date_picker.make_el("div");
(style.textContent = app.components.x_date_picker.x_date_picker.style_text);

container.setAttribute("part","container");

inp.setAttribute("part","input");

inp.setAttribute("type","text");

inp.setAttribute("inputmode","none");

inp.setAttribute("autocomplete","off");

btn.setAttribute("part","btn");

btn.setAttribute("type","button");

btn.setAttribute("aria-label","Open calendar");

(btn.textContent = "\uD83D\uDCC5");

popover.setAttribute("part","popover");

popover.setAttribute("role","dialog");

popover.setAttribute("aria-modal","true");

popover.setAttribute("aria-label","Calendar");

nav.setAttribute("part","nav");

nav_prev.setAttribute("part","navbtn");

nav_prev.setAttribute("data-nav","prev");

nav_prev.setAttribute("type","button");

nav_prev.setAttribute("aria-label","Previous month");

(nav_prev.textContent = "\u2039");

month_lbl.setAttribute("part","monthlabel");

month_lbl.setAttribute("aria-live","polite");

nav_next.setAttribute("part","navbtn");

nav_next.setAttribute("data-nav","next");

nav_next.setAttribute("type","button");

nav_next.setAttribute("aria-label","Next month");

(nav_next.textContent = "\u203A");

weekdays.setAttribute("part","weekdays");

weekdays.setAttribute("aria-hidden","true");

grid.setAttribute("part","grid");

grid.setAttribute("role","grid");

grid.setAttribute("aria-label","Calendar grid");

sr.setAttribute("part","sr-status");

sr.setAttribute("aria-live","polite");

sr.setAttribute("aria-atomic","true");

nav.appendChild(nav_prev);

nav.appendChild(month_lbl);

nav.appendChild(nav_next);

popover.appendChild(nav);

popover.appendChild(weekdays);

popover.appendChild(grid);

popover.appendChild(sr);

container.appendChild(inp);

container.appendChild(btn);

container.appendChild(popover);

root.appendChild(style);

root.appendChild(container);

var refs = ({"weekdays": weekdays, "grid": grid, "popover": popover, "nav-next": nav_next, "month-label": month_lbl, "input": inp, "btn": btn, "sr": sr, "nav-prev": nav_prev});
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_refs,refs);

return refs;
});
app.components.x_date_picker.x_date_picker.on_input_event_BANG_ = (function app$components$x_date_picker$x_date_picker$on_input_event_BANG_(el,_e){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input"):null);
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_display,(cljs.core.truth_(inp)?inp.value:null));

return app.components.x_date_picker.x_date_picker.dispatch_BANG_(el,app.components.x_date_picker.model.event_input,false,({"value": (cljs.core.truth_(inp)?inp.value:null)}));
});
app.components.x_date_picker.x_date_picker.on_input_keydown_BANG_ = (function app$components$x_date_picker$x_date_picker$on_input_keydown_BANG_(el,e){
var key = e.key;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")){
e.preventDefault();

return app.components.x_date_picker.x_date_picker.commit_display_BANG_(el,"keyboard");
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Escape")){
return app.components.x_date_picker.x_date_picker.close_popover_BANG_(el);
} else {
return null;
}
}
});
app.components.x_date_picker.x_date_picker.on_input_focus_BANG_ = (function app$components$x_date_picker$x_date_picker$on_input_focus_BANG_(el,_e){
return app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_focused,true);
});
app.components.x_date_picker.x_date_picker.on_input_blur_BANG_ = (function app$components$x_date_picker$x_date_picker$on_input_blur_BANG_(el,_e){
app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_focused,false);

app.components.x_date_picker.x_date_picker.commit_display_BANG_(el,"blur");

return app.components.x_date_picker.x_date_picker.sync_input_display_BANG_(el);
});
app.components.x_date_picker.x_date_picker.on_btn_click_BANG_ = (function app$components$x_date_picker$x_date_picker$on_btn_click_BANG_(el,_e){
if(cljs.core.truth_(el.hasAttribute("open"))){
return app.components.x_date_picker.x_date_picker.close_popover_BANG_(el);
} else {
return app.components.x_date_picker.x_date_picker.open_popover_BANG_(el);
}
});
app.components.x_date_picker.x_date_picker.on_nav_click_BANG_ = (function app$components$x_date_picker$x_date_picker$on_nav_click_BANG_(el,e){
var target = e.currentTarget;
var dir = target.getAttribute("data-nav");
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(dir,"prev")){
return app.components.x_date_picker.x_date_picker.navigate_month_BANG_(el,new cljs.core.Keyword(null,"prev","prev",-1597069226));
} else {
return app.components.x_date_picker.x_date_picker.navigate_month_BANG_(el,new cljs.core.Keyword(null,"next","next",-117701485));
}
});
app.components.x_date_picker.x_date_picker.on_grid_click_BANG_ = (function app$components$x_date_picker$x_date_picker$on_grid_click_BANG_(el,e){
var target = e.target;
var btn = target.closest("button[data-iso]");
if(cljs.core.truth_((function (){var and__5140__auto__ = btn;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(btn.hasAttribute("disabled"));
} else {
return and__5140__auto__;
}
})())){
var iso = btn.getAttribute("data-iso");
var d = app.components.x_date_picker.model.iso__GT_date(iso);
if(cljs.core.truth_(d)){
return app.components.x_date_picker.x_date_picker.do_select_date_BANG_(el,d,"click");
} else {
return null;
}
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.on_popover_mousedown_BANG_ = (function app$components$x_date_picker$x_date_picker$on_popover_mousedown_BANG_(_el,e){
return e.preventDefault();
});
app.components.x_date_picker.x_date_picker.add_listeners_BANG_ = (function app$components$x_date_picker$x_date_picker$add_listeners_BANG_(el){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
var inp = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input");
var btn = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"btn");
var prev = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"nav-prev");
var next = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"nav-next");
var grid = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"grid");
var pop = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"popover");
var h_input = (function (e){
return app.components.x_date_picker.x_date_picker.on_input_event_BANG_(el,e);
});
var h_keydown = (function (e){
return app.components.x_date_picker.x_date_picker.on_input_keydown_BANG_(el,e);
});
var h_focus = (function (e){
return app.components.x_date_picker.x_date_picker.on_input_focus_BANG_(el,e);
});
var h_blur = (function (e){
return app.components.x_date_picker.x_date_picker.on_input_blur_BANG_(el,e);
});
var h_btn = (function (e){
return app.components.x_date_picker.x_date_picker.on_btn_click_BANG_(el,e);
});
var h_prev = (function (e){
return app.components.x_date_picker.x_date_picker.on_nav_click_BANG_(el,e);
});
var h_next = (function (e){
return app.components.x_date_picker.x_date_picker.on_nav_click_BANG_(el,e);
});
var h_grid = (function (e){
return app.components.x_date_picker.x_date_picker.on_grid_click_BANG_(el,e);
});
var h_pop_md = (function (e){
return app.components.x_date_picker.x_date_picker.on_popover_mousedown_BANG_(el,e);
});
var h_doc_click = (function (e){
if(cljs.core.truth_(el.hasAttribute("open"))){
if(cljs.core.truth_(e.composedPath().some((function (n){
return (n === el);
})))){
return null;
} else {
return app.components.x_date_picker.x_date_picker.close_popover_BANG_(el);
}
} else {
return null;
}
});
inp.addEventListener("input",h_input);

inp.addEventListener("keydown",h_keydown);

inp.addEventListener("focus",h_focus);

inp.addEventListener("blur",h_blur);

btn.addEventListener("click",h_btn);

prev.addEventListener("click",h_prev);

next.addEventListener("click",h_next);

grid.addEventListener("click",h_grid);

pop.addEventListener("mousedown",h_pop_md);

document.addEventListener("pointerdown",h_doc_click,true);

return app.components.x_date_picker.x_date_picker.goog$module$goog$object.set(el,app.components.x_date_picker.x_date_picker.k_handlers,({"pop-md": h_pop_md, "keydown": h_keydown, "grid": h_grid, "next": h_next, "blur": h_blur, "prev": h_prev, "focus": h_focus, "input": h_input, "btn": h_btn, "doc-click": h_doc_click}));
});
app.components.x_date_picker.x_date_picker.remove_listeners_BANG_ = (function app$components$x_date_picker$x_date_picker$remove_listeners_BANG_(el){
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs);
var handlers = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_handlers);
if(cljs.core.truth_((function (){var and__5140__auto__ = refs;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
var inp = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input");
var btn = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"btn");
var prev = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"nav-prev");
var next = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"nav-next");
var grid = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"grid");
var pop = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"popover");
inp.removeEventListener("input",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"input"));

inp.removeEventListener("keydown",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"keydown"));

inp.removeEventListener("focus",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"focus"));

inp.removeEventListener("blur",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"blur"));

btn.removeEventListener("click",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"btn"));

prev.removeEventListener("click",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"prev"));

next.removeEventListener("click",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"next"));

grid.removeEventListener("click",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"grid"));

pop.removeEventListener("mousedown",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"pop-md"));

return document.removeEventListener("pointerdown",app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(handlers,"doc-click"),true);
} else {
return null;
}
});
app.components.x_date_picker.x_date_picker.connected_BANG_ = (function app$components$x_date_picker$x_date_picker$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(el,app.components.x_date_picker.x_date_picker.k_refs))){
} else {
app.components.x_date_picker.x_date_picker.make_shadow_BANG_(el);
}

app.components.x_date_picker.x_date_picker.remove_listeners_BANG_(el);

app.components.x_date_picker.x_date_picker.add_listeners_BANG_(el);

app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

return app.components.x_date_picker.x_date_picker.render_BANG_(el);
});
app.components.x_date_picker.x_date_picker.disconnected_BANG_ = (function app$components$x_date_picker$x_date_picker$disconnected_BANG_(el){
return app.components.x_date_picker.x_date_picker.remove_listeners_BANG_(el);
});
app.components.x_date_picker.x_date_picker.attribute_changed_BANG_ = (function app$components$x_date_picker$x_date_picker$attribute_changed_BANG_(el,_n,_o,_v){
app.components.x_date_picker.x_date_picker.read_state_BANG_(el);

return app.components.x_date_picker.x_date_picker.render_BANG_(el);
});
app.components.x_date_picker.x_date_picker.define_string_prop_BANG_ = (function app$components$x_date_picker$x_date_picker$define_string_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(attr_name);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return null;
}
}), "set": (function (v){
var this$ = this;
if(((typeof v === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,"")))){
return this$.setAttribute(attr_name,v);
} else {
return this$.removeAttribute(attr_name);
}
})}));
});
app.components.x_date_picker.x_date_picker.define_bool_prop_BANG_ = (function app$components$x_date_picker$x_date_picker$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.hasAttribute(attr_name);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr_name,"");
} else {
return this$.removeAttribute(attr_name);
}
})}));
});
app.components.x_date_picker.x_date_picker.define_methods_BANG_ = (function app$components$x_date_picker$x_date_picker$define_methods_BANG_(proto){
(proto["focus"] = (function (){
var this$ = this;
var refs = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(this$,app.components.x_date_picker.x_date_picker.k_refs);
var inp = (cljs.core.truth_(refs)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(refs,"input"):null);
if(cljs.core.truth_(inp)){
return inp.focus();
} else {
return null;
}
}));

(proto["commit"] = (function (){
var this$ = this;
return app.components.x_date_picker.x_date_picker.commit_display_BANG_(this$,"programmatic");
}));

return (proto["clear"] = (function (){
var this$ = this;
var state = app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(this$,app.components.x_date_picker.x_date_picker.k_state);
var canon = (cljs.core.truth_(state)?app.components.x_date_picker.x_date_picker.goog$module$goog$object.get(state,"canon"):null);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"mode","mode",654403691).cljs$core$IFn$_invoke$arity$1(canon),new cljs.core.Keyword(null,"single","single",1551466437))){
this$.removeAttribute(app.components.x_date_picker.model.attr_value);
} else {
this$.removeAttribute(app.components.x_date_picker.model.attr_start);

this$.removeAttribute(app.components.x_date_picker.model.attr_end);
}

app.components.x_date_picker.x_date_picker.read_state_BANG_(this$);

return app.components.x_date_picker.x_date_picker.render_BANG_(this$);
}));
});
app.components.x_date_picker.x_date_picker.element_class = (function app$components$x_date_picker$x_date_picker$element_class(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
var _ = cljs.core.reset_BANG_(ctor_ref,ctor);
var proto = Object.create(HTMLElement.prototype);
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

(ctor["observedAttributes"] = app.components.x_date_picker.model.observed_attributes);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_date_picker.x_date_picker.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_date_picker.x_date_picker.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_date_picker.x_date_picker.attribute_changed_BANG_(this$,n,o,v);
}));

app.components.x_date_picker.x_date_picker.define_string_prop_BANG_(proto,"mode",app.components.x_date_picker.model.attr_mode);

app.components.x_date_picker.x_date_picker.define_string_prop_BANG_(proto,"value",app.components.x_date_picker.model.attr_value);

app.components.x_date_picker.x_date_picker.define_string_prop_BANG_(proto,"start",app.components.x_date_picker.model.attr_start);

app.components.x_date_picker.x_date_picker.define_string_prop_BANG_(proto,"end",app.components.x_date_picker.model.attr_end);

app.components.x_date_picker.x_date_picker.define_bool_prop_BANG_(proto,"open","open");

app.components.x_date_picker.x_date_picker.define_bool_prop_BANG_(proto,"disabled",app.components.x_date_picker.model.attr_disabled);

app.components.x_date_picker.x_date_picker.define_bool_prop_BANG_(proto,"readOnly",app.components.x_date_picker.model.attr_readonly);

app.components.x_date_picker.x_date_picker.define_bool_prop_BANG_(proto,"required",app.components.x_date_picker.model.attr_required);

app.components.x_date_picker.x_date_picker.define_methods_BANG_(proto);

(ctor["prototype"] = proto);

return ctor;
});
app.components.x_date_picker.x_date_picker.init_BANG_ = (function app$components$x_date_picker$x_date_picker$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_date_picker.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_date_picker.model.tag_name,app.components.x_date_picker.x_date_picker.element_class());
}
});

//# sourceMappingURL=app.components.x_date_picker.x_date_picker.js.map
