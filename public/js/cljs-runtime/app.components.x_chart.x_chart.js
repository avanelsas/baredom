goog.provide('app.components.x_chart.x_chart');
goog.scope(function(){
  app.components.x_chart.x_chart.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_chart.x_chart.k_refs = "__xChartRefs";
app.components.x_chart.x_chart.k_handlers = "__xChartHandlers";
app.components.x_chart.x_chart.k_data = "__xChartData";
app.components.x_chart.x_chart.svg_ns = "http://www.w3.org/2000/svg";
app.components.x_chart.x_chart.make_el = (function app$components$x_chart$x_chart$make_el(tag){
return document.createElement(tag);
});
app.components.x_chart.x_chart.make_svg_el = (function app$components$x_chart$x_chart$make_svg_el(tag){
return document.createElementNS(app.components.x_chart.x_chart.svg_ns,tag);
});
app.components.x_chart.x_chart.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"contain:content;"+"--x-chart-series-1:rgba(0,102,204,0.95);"+"--x-chart-series-2:rgba(16,140,72,0.95);"+"--x-chart-series-3:rgba(190,20,40,0.95);"+"--x-chart-series-4:rgba(204,120,0,0.95);"+"--x-chart-border:rgba(0,0,0,0.1);"+"--x-chart-grid:rgba(0,0,0,0.08);"+"--x-chart-axis-label:rgba(0,0,0,0.5);"+"--x-chart-skeleton:linear-gradient(90deg,rgba(0,0,0,0.06) 25%,rgba(0,0,0,0.03) 50%,rgba(0,0,0,0.06) 75%);"+"--x-chart-tooltip-bg:rgba(255,255,255,0.96);"+"--x-chart-tooltip-border:rgba(0,0,0,0.12);"+"--x-chart-tooltip-shadow:0 4px 16px rgba(0,0,0,0.14);"+"--x-chart-focus-ring:rgba(0,102,204,0.55);"+"--x-chart-radius:0.75rem;"+"--x-chart-tooltip-radius:0.5rem;"+"--x-chart-tooltip-padding:0.45rem 0.7rem;"+"--x-chart-tooltip-font-size:0.8125rem;"+"--x-chart-tooltip-header-color:rgba(0,0,0,0.5);"+"--x-chart-tooltip-label-color:rgba(0,0,0,0.65);"+"--x-chart-tooltip-value-color:rgba(0,0,0,0.9);"+"--x-chart-tooltip-swatch-size:8px;"+"--x-chart-tooltip-gap:0.35rem;"+"--x-chart-crosshair-color:rgba(0,0,0,0.18);"+"--x-chart-crosshair-width:1;"+"}"+"@media(prefers-color-scheme:dark){"+":host{"+"--x-chart-series-1:rgba(120,190,255,0.95);"+"--x-chart-series-2:rgba(80,230,150,0.92);"+"--x-chart-series-3:rgba(255,100,110,0.93);"+"--x-chart-series-4:rgba(255,190,60,0.93);"+"--x-chart-border:rgba(255,255,255,0.1);"+"--x-chart-grid:rgba(255,255,255,0.08);"+"--x-chart-axis-label:rgba(255,255,255,0.45);"+"--x-chart-skeleton:linear-gradient(90deg,rgba(255,255,255,0.06) 25%,rgba(255,255,255,0.03) 50%,rgba(255,255,255,0.06) 75%);"+"--x-chart-tooltip-bg:rgba(30,30,35,0.97);"+"--x-chart-tooltip-border:rgba(255,255,255,0.12);"+"--x-chart-tooltip-shadow:0 4px 20px rgba(0,0,0,0.45);"+"--x-chart-focus-ring:rgba(120,190,255,0.55);"+"--x-chart-tooltip-header-color:rgba(255,255,255,0.45);"+"--x-chart-tooltip-label-color:rgba(255,255,255,0.6);"+"--x-chart-tooltip-value-color:rgba(255,255,255,0.9);"+"--x-chart-crosshair-color:rgba(255,255,255,0.2);"+"}"+"}"+"[part=container]{"+"position:relative;"+"border:1px solid var(--x-chart-border);"+"border-radius:var(--x-chart-radius);"+"overflow:hidden;"+"outline:none;"+"}"+"[part=container]:focus-visible{"+"box-shadow:0 0 0 3px var(--x-chart-focus-ring);"+"}"+"[part=svg]{display:block;width:100%;height:auto;overflow:visible;}"+":host([loading]) [part=container]{"+"background:var(--x-chart-skeleton);"+"background-size:200% 100%;"+"animation:x-chart-shimmer 1.4s ease infinite;"+"}"+"@keyframes x-chart-shimmer{"+"0%{background-position:200% 0;}"+"100%{background-position:-200% 0;}"+"}"+"@media(prefers-reduced-motion:reduce){"+":host([loading]) [part=container]{animation:none;}"+"}"+"[part=sr-only]{"+"position:absolute;width:1px;height:1px;padding:0;"+"margin:-1px;overflow:hidden;clip:rect(0,0,0,0);"+"white-space:nowrap;border:0;"+"}"+"[part=tooltip]{"+"visibility:hidden;"+"opacity:0;"+"position:absolute;"+"pointer-events:none;"+"background:var(--x-chart-tooltip-bg);"+"border:1px solid var(--x-chart-tooltip-border);"+"box-shadow:var(--x-chart-tooltip-shadow);"+"border-radius:var(--x-chart-tooltip-radius);"+"padding:var(--x-chart-tooltip-padding);"+"font-size:var(--x-chart-tooltip-font-size);"+"line-height:1.5;"+"white-space:nowrap;"+"z-index:10;"+"}"+"[part=tooltip][data-visible=true]{visibility:visible;opacity:1;}"+"@media(prefers-reduced-motion:no-preference){"+"[part=tooltip]{transition:opacity 0.1s ease,left 0.08s ease,top 0.08s ease;}"+"}"+"[part=tooltip-header]{"+"font-size:0.75rem;"+"color:var(--x-chart-tooltip-header-color);"+"margin-bottom:0.25rem;"+"}"+"[part=tooltip-body]{display:flex;flex-direction:column;gap:var(--x-chart-tooltip-gap);}"+"[part=tooltip-row]{display:flex;align-items:center;gap:0.4rem;}"+"[part=tooltip-row][data-hidden=true]{display:none;}"+"[part=tooltip-swatch]{"+"display:inline-block;"+"width:var(--x-chart-tooltip-swatch-size);"+"height:var(--x-chart-tooltip-swatch-size);"+"border-radius:50%;"+"flex-shrink:0;"+"}"+"[part=tooltip-label]{color:var(--x-chart-tooltip-label-color);}"+"[part=tooltip-value]{color:var(--x-chart-tooltip-value-color);font-weight:600;margin-left:auto;padding-left:0.5rem;}");
app.components.x_chart.x_chart.has_attr_QMARK_ = (function app$components$x_chart$x_chart$has_attr_QMARK_(el,attr){
return el.hasAttribute(attr);
});
app.components.x_chart.x_chart.get_attr = (function app$components$x_chart$x_chart$get_attr(el,attr){
return el.getAttribute(attr);
});
app.components.x_chart.x_chart.read_model = (function app$components$x_chart$x_chart$read_model(el){
return app.components.x_chart.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"selected-raw","selected-raw",-1471626848),new cljs.core.Keyword(null,"padding-raw","padding-raw",-847772255),new cljs.core.Keyword(null,"tooltip-present?","tooltip-present?",-2098824029),new cljs.core.Keyword(null,"y-format-raw","y-format-raw",-1098541692),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"grid-present?","grid-present?",-468948562),new cljs.core.Keyword(null,"axes-raw","axes-raw",-839946224),new cljs.core.Keyword(null,"height-raw","height-raw",-867301006),new cljs.core.Keyword(null,"cursor-raw","cursor-raw",-1169819084),new cljs.core.Keyword(null,"loading-present?","loading-present?",-1827648748),new cljs.core.Keyword(null,"type-raw","type-raw",-967209994),new cljs.core.Keyword(null,"data-raw","data-raw",822066711),new cljs.core.Keyword(null,"x-format-raw","x-format-raw",-1683657190)],[app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_selected),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_padding),(cljs.core.truth_(app.components.x_chart.x_chart.has_attr_QMARK_(el,app.components.x_chart.model.attr_tooltip))?el.getAttribute(app.components.x_chart.model.attr_tooltip):null),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_y_format),(cljs.core.truth_(app.components.x_chart.x_chart.has_attr_QMARK_(el,app.components.x_chart.model.attr_disabled))?el.getAttribute(app.components.x_chart.model.attr_disabled):null),(cljs.core.truth_(app.components.x_chart.x_chart.has_attr_QMARK_(el,app.components.x_chart.model.attr_grid))?el.getAttribute(app.components.x_chart.model.attr_grid):null),(cljs.core.truth_(app.components.x_chart.x_chart.has_attr_QMARK_(el,app.components.x_chart.model.attr_axes))?el.getAttribute(app.components.x_chart.model.attr_axes):null),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_height),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_cursor),(cljs.core.truth_(app.components.x_chart.x_chart.has_attr_QMARK_(el,app.components.x_chart.model.attr_loading))?el.getAttribute(app.components.x_chart.model.attr_loading):null),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_type),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_data),app.components.x_chart.x_chart.get_attr(el,app.components.x_chart.model.attr_x_format)]));
});
app.components.x_chart.x_chart.dispatch_BANG_ = (function app$components$x_chart$x_chart$dispatch_BANG_(el,event_name,cancelable_QMARK_,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": cancelable_QMARK_}))));
});
app.components.x_chart.x_chart.label_width = (function app$components$x_chart$x_chart$label_width(){
return (36);
});
app.components.x_chart.x_chart.label_height = (function app$components$x_chart$x_chart$label_height(){
return (18);
});
app.components.x_chart.x_chart.plot_bounds = (function app$components$x_chart$x_chart$plot_bounds(W,H,padding){
var lw = app.components.x_chart.x_chart.label_width();
var lh = app.components.x_chart.x_chart.label_height();
var x0 = (padding + lw);
var y0 = padding;
var x1 = (W - padding);
var y1 = ((H - padding) - lh);
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"x0","x0",410843387),x0,new cljs.core.Keyword(null,"y0","y0",111454807),y0,new cljs.core.Keyword(null,"x1","x1",-1863922247),x1,new cljs.core.Keyword(null,"y1","y1",589123466),y1], null);
});
app.components.x_chart.x_chart.scale_y = (function app$components$x_chart$x_chart$scale_y(y,p__22095,y0,y1){
var vec__22096 = p__22095;
var mn = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22096,(0),null);
var mx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22096,(1),null);
var span = (mx - mn);
if((span === (0))){
return ((y0 + y1) / (2));
} else {
return (y0 + ((y1 - y0) * ((mx - y) / span)));
}
});
app.components.x_chart.x_chart.scale_x_numeric = (function app$components$x_chart$x_chart$scale_x_numeric(x,p__22099,x0,x1){
var vec__22100 = p__22099;
var mn = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22100,(0),null);
var mx = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__22100,(1),null);
var span = (mx - mn);
if((span === (0))){
return ((x0 + x1) / (2));
} else {
return (x0 + (((x - mn) / span) * (x1 - x0)));
}
});
app.components.x_chart.x_chart.scale_x_category = (function app$components$x_chart$x_chart$scale_x_category(i,n,x0,x1){
if((n <= (1))){
return ((x0 + x1) / (2));
} else {
return (x0 + ((i / (n - (1))) * (x1 - x0)));
}
});
app.components.x_chart.x_chart.data_pt_x = (function app$components$x_chart$x_chart$data_pt_x(pt,i,kind,x_dom,x0,x1){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(kind,"category")){
return app.components.x_chart.x_chart.scale_x_category(i,(cljs.core.second(x_dom) - cljs.core.first(x_dom)),x0,x1);
} else {
return app.components.x_chart.x_chart.scale_x_numeric(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt),x_dom,x0,x1);
}
});
app.components.x_chart.x_chart.set_attr_BANG_ = (function app$components$x_chart$x_chart$set_attr_BANG_(el,attr,val){
return el.setAttribute(attr,val);
});
app.components.x_chart.x_chart.svg_line_BANG_ = (function app$components$x_chart$x_chart$svg_line_BANG_(x1,y1,x2,y2,stroke,opacity){
var el = app.components.x_chart.x_chart.make_svg_el("line");
app.components.x_chart.x_chart.set_attr_BANG_(el,"x1",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x1)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"y1",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y1)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"x2",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x2)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"y2",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y2)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"stroke",stroke);

app.components.x_chart.x_chart.set_attr_BANG_(el,"stroke-opacity",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(opacity)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"stroke-width","1");

return el;
});
app.components.x_chart.x_chart.svg_text_BANG_ = (function app$components$x_chart$x_chart$svg_text_BANG_(x,y,text,anchor,font_size,fill){
var el = app.components.x_chart.x_chart.make_svg_el("text");
app.components.x_chart.x_chart.set_attr_BANG_(el,"x",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"y",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"text-anchor",anchor);

app.components.x_chart.x_chart.set_attr_BANG_(el,"font-size",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(font_size)));

app.components.x_chart.x_chart.set_attr_BANG_(el,"fill",fill);

(el.textContent = text);

return el;
});
app.components.x_chart.x_chart.series_color_var = (function app$components$x_chart$x_chart$series_color_var(idx){
return (""+"var(--x-chart-series-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.mod(idx,(4)) + (1)))+")");
});
app.components.x_chart.x_chart.build_line_d = (function app$components$x_chart$x_chart$build_line_d(pts){
if(cljs.core.seq(pts)){
var vec__22103 = pts;
var seq__22104 = cljs.core.seq(vec__22103);
var first__22105 = cljs.core.first(seq__22104);
var seq__22104__$1 = cljs.core.next(seq__22104);
var f = first__22105;
var rest_pts = seq__22104__$1;
return (""+"M "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(f))+","+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(f))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p){
return (""+" L "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(p))+","+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(p)));
}),rest_pts))));
} else {
return null;
}
});
app.components.x_chart.x_chart.build_area_d = (function app$components$x_chart$x_chart$build_area_d(pts,baseline_y){
if(cljs.core.seq(pts)){
var line_d = app.components.x_chart.x_chart.build_line_d(pts);
var last_p = cljs.core.last(pts);
var first_p = cljs.core.first(pts);
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line_d)+" L "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(last_p))+","+cljs.core.str.cljs$core$IFn$_invoke$arity$1(baseline_y)+" L "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(first_p))+","+cljs.core.str.cljs$core$IFn$_invoke$arity$1(baseline_y)+" Z");
} else {
return null;
}
});
app.components.x_chart.x_chart.remove_children_BANG_ = (function app$components$x_chart$x_chart$remove_children_BANG_(el){
while(true){
var temp__5823__auto__ = el.lastChild;
if(cljs.core.truth_(temp__5823__auto__)){
var c = temp__5823__auto__;
el.removeChild(c);

continue;
} else {
return null;
}
break;
}
});
app.components.x_chart.x_chart.draw_grid_BANG_ = (function app$components$x_chart$x_chart$draw_grid_BANG_(svg,p__22106,p__22107){
var map__22108 = p__22106;
var map__22108__$1 = cljs.core.__destructure_map(map__22108);
var y_ticks = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22108__$1,new cljs.core.Keyword(null,"y-ticks","y-ticks",-843622722));
var y_domain = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22108__$1,new cljs.core.Keyword(null,"y-domain","y-domain",-969203007));
var map__22109 = p__22107;
var map__22109__$1 = cljs.core.__destructure_map(map__22109);
var x0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22109__$1,new cljs.core.Keyword(null,"x0","x0",410843387));
var y0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22109__$1,new cljs.core.Keyword(null,"y0","y0",111454807));
var x1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22109__$1,new cljs.core.Keyword(null,"x1","x1",-1863922247));
var y1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22109__$1,new cljs.core.Keyword(null,"y1","y1",589123466));
var seq__22110 = cljs.core.seq(y_ticks);
var chunk__22111 = null;
var count__22112 = (0);
var i__22113 = (0);
while(true){
if((i__22113 < count__22112)){
var tick = chunk__22111.cljs$core$IIndexed$_nth$arity$2(null,i__22113);
var py_22305 = app.components.x_chart.x_chart.scale_y(tick,y_domain,y0,y1);
var line_22306 = app.components.x_chart.x_chart.svg_line_BANG_(x0,py_22305,x1,py_22305,"var(--x-chart-grid)",(1));
svg.appendChild(line_22306);


var G__22307 = seq__22110;
var G__22308 = chunk__22111;
var G__22309 = count__22112;
var G__22310 = (i__22113 + (1));
seq__22110 = G__22307;
chunk__22111 = G__22308;
count__22112 = G__22309;
i__22113 = G__22310;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__22110);
if(temp__5823__auto__){
var seq__22110__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__22110__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__22110__$1);
var G__22311 = cljs.core.chunk_rest(seq__22110__$1);
var G__22312 = c__5673__auto__;
var G__22313 = cljs.core.count(c__5673__auto__);
var G__22314 = (0);
seq__22110 = G__22311;
chunk__22111 = G__22312;
count__22112 = G__22313;
i__22113 = G__22314;
continue;
} else {
var tick = cljs.core.first(seq__22110__$1);
var py_22315 = app.components.x_chart.x_chart.scale_y(tick,y_domain,y0,y1);
var line_22316 = app.components.x_chart.x_chart.svg_line_BANG_(x0,py_22315,x1,py_22315,"var(--x-chart-grid)",(1));
svg.appendChild(line_22316);


var G__22317 = cljs.core.next(seq__22110__$1);
var G__22318 = null;
var G__22319 = (0);
var G__22320 = (0);
seq__22110 = G__22317;
chunk__22111 = G__22318;
count__22112 = G__22319;
i__22113 = G__22320;
continue;
}
} else {
return null;
}
}
break;
}
});
app.components.x_chart.x_chart.draw_axes_BANG_ = (function app$components$x_chart$x_chart$draw_axes_BANG_(svg,p__22114,p__22115,padding){
var map__22116 = p__22114;
var map__22116__$1 = cljs.core.__destructure_map(map__22116);
var y_ticks = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"y-ticks","y-ticks",-843622722));
var y_domain = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"y-domain","y-domain",-969203007));
var series = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"series","series",600710694));
var x_kind = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"x-kind","x-kind",693167455));
var y_fmt = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"y-fmt","y-fmt",2092412811));
var x_fmt = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22116__$1,new cljs.core.Keyword(null,"x-fmt","x-fmt",2073358008));
var map__22117 = p__22115;
var map__22117__$1 = cljs.core.__destructure_map(map__22117);
var x0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22117__$1,new cljs.core.Keyword(null,"x0","x0",410843387));
var y0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22117__$1,new cljs.core.Keyword(null,"y0","y0",111454807));
var x1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22117__$1,new cljs.core.Keyword(null,"x1","x1",-1863922247));
var y1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22117__$1,new cljs.core.Keyword(null,"y1","y1",589123466));
var seq__22124_22321 = cljs.core.seq(y_ticks);
var chunk__22125_22322 = null;
var count__22126_22323 = (0);
var i__22127_22324 = (0);
while(true){
if((i__22127_22324 < count__22126_22323)){
var tick_22325 = chunk__22125_22322.cljs$core$IIndexed$_nth$arity$2(null,i__22127_22324);
var py_22326 = app.components.x_chart.x_chart.scale_y(tick_22325,y_domain,y0,y1);
var lbl_22327 = app.components.x_chart.model.format_value(tick_22325,y_fmt);
var txt_22328 = app.components.x_chart.x_chart.svg_text_BANG_((x0 - (4)),(py_22326 + (4)),lbl_22327,"end",(10),"var(--x-chart-axis-label)");
svg.appendChild(txt_22328);


var G__22329 = seq__22124_22321;
var G__22330 = chunk__22125_22322;
var G__22331 = count__22126_22323;
var G__22332 = (i__22127_22324 + (1));
seq__22124_22321 = G__22329;
chunk__22125_22322 = G__22330;
count__22126_22323 = G__22331;
i__22127_22324 = G__22332;
continue;
} else {
var temp__5823__auto___22333 = cljs.core.seq(seq__22124_22321);
if(temp__5823__auto___22333){
var seq__22124_22334__$1 = temp__5823__auto___22333;
if(cljs.core.chunked_seq_QMARK_(seq__22124_22334__$1)){
var c__5673__auto___22335 = cljs.core.chunk_first(seq__22124_22334__$1);
var G__22336 = cljs.core.chunk_rest(seq__22124_22334__$1);
var G__22337 = c__5673__auto___22335;
var G__22338 = cljs.core.count(c__5673__auto___22335);
var G__22339 = (0);
seq__22124_22321 = G__22336;
chunk__22125_22322 = G__22337;
count__22126_22323 = G__22338;
i__22127_22324 = G__22339;
continue;
} else {
var tick_22340 = cljs.core.first(seq__22124_22334__$1);
var py_22341 = app.components.x_chart.x_chart.scale_y(tick_22340,y_domain,y0,y1);
var lbl_22342 = app.components.x_chart.model.format_value(tick_22340,y_fmt);
var txt_22343 = app.components.x_chart.x_chart.svg_text_BANG_((x0 - (4)),(py_22341 + (4)),lbl_22342,"end",(10),"var(--x-chart-axis-label)");
svg.appendChild(txt_22343);


var G__22344 = cljs.core.next(seq__22124_22334__$1);
var G__22345 = null;
var G__22346 = (0);
var G__22347 = (0);
seq__22124_22321 = G__22344;
chunk__22125_22322 = G__22345;
count__22126_22323 = G__22346;
i__22127_22324 = G__22347;
continue;
}
} else {
}
}
break;
}

var temp__5823__auto__ = cljs.core.first(series);
if(cljs.core.truth_(temp__5823__auto__)){
var s = temp__5823__auto__;
var pts = new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s);
var n = cljs.core.count(pts);
var xdom = app.components.x_chart.model.domain_x(series);
var step = cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),(Math.ceil((n / (5))) | 0));
return cljs.core.doall.cljs$core$IFn$_invoke$arity$1(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2((function (i,pt){
if((cljs.core.mod(i,step) === (0))){
var px = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(x_kind,"category"))?app.components.x_chart.x_chart.scale_x_category(i,(cljs.core.second(xdom) - cljs.core.first(xdom)),x0,x1):app.components.x_chart.x_chart.scale_x_numeric(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt),xdom,x0,x1));
var label = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(x_kind,"category"))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt))):app.components.x_chart.model.format_value(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt),x_fmt));
var txt = app.components.x_chart.x_chart.svg_text_BANG_(px,((y1 + padding) + (4)),label,"middle",(10),"var(--x-chart-axis-label)");
return svg.appendChild(txt);
} else {
return null;
}
}),pts));
} else {
return null;
}
});
app.components.x_chart.x_chart.compute_series_pts = (function app$components$x_chart$x_chart$compute_series_pts(s,series_idx,kind,x_dom,p__22135,y_domain){
var map__22136 = p__22135;
var map__22136__$1 = cljs.core.__destructure_map(map__22136);
var x0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22136__$1,new cljs.core.Keyword(null,"x0","x0",410843387));
var y0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22136__$1,new cljs.core.Keyword(null,"y0","y0",111454807));
var x1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22136__$1,new cljs.core.Keyword(null,"x1","x1",-1863922247));
var y1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22136__$1,new cljs.core.Keyword(null,"y1","y1",589123466));
return cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2((function (i,pt){
var px = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(kind,"category"))?(function (){var n = cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),cljs.core.count(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s)));
return app.components.x_chart.x_chart.scale_x_category(i,(n - (1)),x0,x1);
})():app.components.x_chart.x_chart.scale_x_numeric(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt),x_dom,x0,x1));
var py = app.components.x_chart.x_chart.scale_y(new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt),y_domain,y0,y1);
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(pt,new cljs.core.Keyword(null,"px","px",281329899),px,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"py","py",1397985916),py,new cljs.core.Keyword(null,"si","si",793409822),series_idx,new cljs.core.Keyword(null,"i","i",-1386841315),i], 0));
}),new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(s));
});
app.components.x_chart.x_chart.draw_line_series_BANG_ = (function app$components$x_chart$x_chart$draw_line_series_BANG_(svg,series_idx,pts,color){
var temp__5823__auto__ = app.components.x_chart.x_chart.build_line_d(pts);
if(cljs.core.truth_(temp__5823__auto__)){
var d = temp__5823__auto__;
var path = app.components.x_chart.x_chart.make_svg_el("path");
app.components.x_chart.x_chart.set_attr_BANG_(path,"d",d);

app.components.x_chart.x_chart.set_attr_BANG_(path,"fill","none");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke",color);

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-width","2");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-linejoin","round");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-linecap","round");

return svg.appendChild(path);
} else {
return null;
}
});
app.components.x_chart.x_chart.draw_area_series_BANG_ = (function app$components$x_chart$x_chart$draw_area_series_BANG_(svg,series_idx,pts,color,baseline_y){
var temp__5823__auto__ = app.components.x_chart.x_chart.build_area_d(pts,baseline_y);
if(cljs.core.truth_(temp__5823__auto__)){
var d = temp__5823__auto__;
var path = app.components.x_chart.x_chart.make_svg_el("path");
app.components.x_chart.x_chart.set_attr_BANG_(path,"d",d);

app.components.x_chart.x_chart.set_attr_BANG_(path,"fill",color);

app.components.x_chart.x_chart.set_attr_BANG_(path,"fill-opacity","0.18");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke",color);

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-width","2");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-linejoin","round");

app.components.x_chart.x_chart.set_attr_BANG_(path,"stroke-linecap","round");

return svg.appendChild(path);
} else {
return null;
}
});
app.components.x_chart.x_chart.draw_bar_series_BANG_ = (function app$components$x_chart$x_chart$draw_bar_series_BANG_(svg,series_idx,pts,color,p__22137,total_series){
var map__22138 = p__22137;
var map__22138__$1 = cljs.core.__destructure_map(map__22138);
var x0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22138__$1,new cljs.core.Keyword(null,"x0","x0",410843387));
var x1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22138__$1,new cljs.core.Keyword(null,"x1","x1",-1863922247));
var n = cljs.core.count(pts);
var bw_raw = (((n === (0)))?(0):((x1 - x0) / n));
var gap = (bw_raw * 0.08);
var sw = ((bw_raw - (gap * (2))) / total_series);
var offset = (series_idx * sw);
var seq__22139 = cljs.core.seq(pts);
var chunk__22140 = null;
var count__22141 = (0);
var i__22142 = (0);
while(true){
if((i__22142 < count__22141)){
var pt = chunk__22140.cljs$core$IIndexed$_nth$arity$2(null,i__22142);
var bx_22348 = (((new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt) + (- (bw_raw / (2)))) + gap) + offset);
var by_22349 = cljs.core.min.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt),new cljs.core.Keyword(null,"y0-baseline","y0-baseline",613579610).cljs$core$IFn$_invoke$arity$1(pt));
var bh_22350 = Math.abs((new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt) - new cljs.core.Keyword(null,"y0-baseline","y0-baseline",613579610).cljs$core$IFn$_invoke$arity$1(pt)));
var rect_22351 = app.components.x_chart.x_chart.make_svg_el("rect");
app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"x",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(bx_22348)));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"y",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(by_22349)));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"width",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),sw))));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"height",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),bh_22350))));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"fill",color);

app.components.x_chart.x_chart.set_attr_BANG_(rect_22351,"rx","2");

svg.appendChild(rect_22351);


var G__22353 = seq__22139;
var G__22354 = chunk__22140;
var G__22355 = count__22141;
var G__22356 = (i__22142 + (1));
seq__22139 = G__22353;
chunk__22140 = G__22354;
count__22141 = G__22355;
i__22142 = G__22356;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__22139);
if(temp__5823__auto__){
var seq__22139__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__22139__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__22139__$1);
var G__22357 = cljs.core.chunk_rest(seq__22139__$1);
var G__22358 = c__5673__auto__;
var G__22359 = cljs.core.count(c__5673__auto__);
var G__22360 = (0);
seq__22139 = G__22357;
chunk__22140 = G__22358;
count__22141 = G__22359;
i__22142 = G__22360;
continue;
} else {
var pt = cljs.core.first(seq__22139__$1);
var bx_22361 = (((new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt) + (- (bw_raw / (2)))) + gap) + offset);
var by_22362 = cljs.core.min.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt),new cljs.core.Keyword(null,"y0-baseline","y0-baseline",613579610).cljs$core$IFn$_invoke$arity$1(pt));
var bh_22363 = Math.abs((new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt) - new cljs.core.Keyword(null,"y0-baseline","y0-baseline",613579610).cljs$core$IFn$_invoke$arity$1(pt)));
var rect_22364 = app.components.x_chart.x_chart.make_svg_el("rect");
app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"x",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(bx_22361)));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"y",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(by_22362)));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"width",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((1),sw))));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"height",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),bh_22363))));

app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"fill",color);

app.components.x_chart.x_chart.set_attr_BANG_(rect_22364,"rx","2");

svg.appendChild(rect_22364);


var G__22365 = cljs.core.next(seq__22139__$1);
var G__22366 = null;
var G__22367 = (0);
var G__22368 = (0);
seq__22139 = G__22365;
chunk__22140 = G__22366;
count__22141 = G__22367;
i__22142 = G__22368;
continue;
}
} else {
return null;
}
}
break;
}
});
/**
 * Build hover-data map from a seq of hit points, x/y format specs, and series vec.
 */
app.components.x_chart.x_chart.build_hover_data = (function app$components$x_chart$x_chart$build_hover_data(pts,x_fmt,y_fmt,series){
if(cljs.core.seq(pts)){
var first_pt = cljs.core.first(pts);
var x_label = ((typeof new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(first_pt) === 'string')?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(first_pt))):app.components.x_chart.model.format_value(new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(first_pt),x_fmt));
var color_map = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2((function (i,s){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(s),(function (){var or__5142__auto__ = new cljs.core.Keyword(null,"color","color",1011675173).cljs$core$IFn$_invoke$arity$1(s);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_chart.x_chart.series_color_var(i);
}
})()], null);
}),series));
var rows = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (pt){
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"color","color",1011675173),cljs.core.get.cljs$core$IFn$_invoke$arity$3(color_map,new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(pt),app.components.x_chart.x_chart.series_color_var(new cljs.core.Keyword(null,"si","si",793409822).cljs$core$IFn$_invoke$arity$2(pt,(0)))),new cljs.core.Keyword(null,"label","label",1718410804),(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(pt))),new cljs.core.Keyword(null,"value","value",305978217),app.components.x_chart.model.format_value(new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt),y_fmt),new cljs.core.Keyword(null,"px","px",281329899),new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt),new cljs.core.Keyword(null,"py","py",1397985916),new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt)], null);
}),pts);
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"x-label","x-label",802517907),x_label,new cljs.core.Keyword(null,"rows","rows",850049680),rows,new cljs.core.Keyword(null,"px","px",281329899),new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(first_pt),new cljs.core.Keyword(null,"py","py",1397985916),new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(first_pt),new cljs.core.Keyword(null,"dot-pts","dot-pts",1732240900),cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (pt){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"px","px",281329899),new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt),new cljs.core.Keyword(null,"py","py",1397985916),new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt),new cljs.core.Keyword(null,"color","color",1011675173),cljs.core.get.cljs$core$IFn$_invoke$arity$3(color_map,new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(pt),app.components.x_chart.x_chart.series_color_var(new cljs.core.Keyword(null,"si","si",793409822).cljs$core$IFn$_invoke$arity$2(pt,(0))))], null);
}),pts)], null);
} else {
return null;
}
});
app.components.x_chart.x_chart.show_tooltip_BANG_ = (function app$components$x_chart$x_chart$show_tooltip_BANG_(refs,hover_data,W,H){
var tooltip_el = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"tooltip");
if(cljs.core.truth_((function (){var and__5140__auto__ = tooltip_el;
if(cljs.core.truth_(and__5140__auto__)){
return hover_data;
} else {
return and__5140__auto__;
}
})())){
var map__22163 = hover_data;
var map__22163__$1 = cljs.core.__destructure_map(map__22163);
var x_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22163__$1,new cljs.core.Keyword(null,"x-label","x-label",802517907));
var rows = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22163__$1,new cljs.core.Keyword(null,"rows","rows",850049680));
var px = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22163__$1,new cljs.core.Keyword(null,"px","px",281329899));
var py = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22163__$1,new cljs.core.Keyword(null,"py","py",1397985916));
var dot_pts = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22163__$1,new cljs.core.Keyword(null,"dot-pts","dot-pts",1732240900));
var header = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"tooltip-header");
var body = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"tooltip-body");
var row_els = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"tooltip-rows");
var dot_els = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"dots");
var xhair = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"crosshair");
if(cljs.core.truth_(header)){
(header.textContent = x_label);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = body;
if(cljs.core.truth_(and__5140__auto__)){
return row_els;
} else {
return and__5140__auto__;
}
})())){
var n__5741__auto___22369 = app.components.x_chart.model.max_tooltip_rows;
var i_22370 = (0);
while(true){
if((i_22370 < n__5741__auto___22369)){
var row_el_22371 = (row_els[i_22370]);
if(cljs.core.truth_(row_el_22371)){
var temp__5821__auto___22372 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(rows,i_22370,null);
if(cljs.core.truth_(temp__5821__auto___22372)){
var row_22373 = temp__5821__auto___22372;
app.components.x_chart.x_chart.set_attr_BANG_(row_el_22371,"data-hidden","false");

var swatch_22374 = row_el_22371.firstChild;
var label_22375 = (cljs.core.truth_(swatch_22374)?swatch_22374.nextSibling:null);
var value_22376 = (cljs.core.truth_(label_22375)?label_22375.nextSibling:null);
if(cljs.core.truth_(swatch_22374)){
(swatch_22374.style.backgroundColor = new cljs.core.Keyword(null,"color","color",1011675173).cljs$core$IFn$_invoke$arity$1(row_22373));
} else {
}

if(cljs.core.truth_(label_22375)){
(label_22375.textContent = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(row_22373));
} else {
}

if(cljs.core.truth_(value_22376)){
(value_22376.textContent = new cljs.core.Keyword(null,"value","value",305978217).cljs$core$IFn$_invoke$arity$1(row_22373));
} else {
}
} else {
app.components.x_chart.x_chart.set_attr_BANG_(row_el_22371,"data-hidden","true");
}
} else {
}

var G__22377 = (i_22370 + (1));
i_22370 = G__22377;
continue;
} else {
}
break;
}
} else {
}

if(cljs.core.truth_(dot_els)){
var n__5741__auto___22378 = app.components.x_chart.model.max_tooltip_rows;
var i_22379 = (0);
while(true){
if((i_22379 < n__5741__auto___22378)){
var dot_22380 = (dot_els[i_22379]);
if(cljs.core.truth_(dot_22380)){
var temp__5821__auto___22381 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(dot_pts,i_22379,null);
if(cljs.core.truth_(temp__5821__auto___22381)){
var dp_22382 = temp__5821__auto___22381;
app.components.x_chart.x_chart.set_attr_BANG_(dot_22380,"cx",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(dp_22382))));

app.components.x_chart.x_chart.set_attr_BANG_(dot_22380,"cy",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(dp_22382))));

app.components.x_chart.x_chart.set_attr_BANG_(dot_22380,"fill",new cljs.core.Keyword(null,"color","color",1011675173).cljs$core$IFn$_invoke$arity$1(dp_22382));

app.components.x_chart.x_chart.set_attr_BANG_(dot_22380,"visibility","visible");
} else {
app.components.x_chart.x_chart.set_attr_BANG_(dot_22380,"visibility","hidden");
}
} else {
}

var G__22383 = (i_22379 + (1));
i_22379 = G__22383;
continue;
} else {
}
break;
}
} else {
}

if(cljs.core.truth_(xhair)){
app.components.x_chart.x_chart.set_attr_BANG_(xhair,"visibility","visible");
} else {
}

if(cljs.core.truth_(xhair)){
app.components.x_chart.x_chart.set_attr_BANG_(xhair,"x1",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(px)));
} else {
}

if(cljs.core.truth_(xhair)){
app.components.x_chart.x_chart.set_attr_BANG_(xhair,"x2",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(px)));
} else {
}

app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"data-visible","true");

var tw = tooltip_el.offsetWidth;
var th = tooltip_el.offsetHeight;
var map__22166 = app.components.x_chart.model.tooltip_position(px,py,tw,th,W,H,app.components.x_chart.model.tooltip_edge_pad,app.components.x_chart.model.tooltip_offset);
var map__22166__$1 = cljs.core.__destructure_map(map__22166);
var left = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22166__$1,new cljs.core.Keyword(null,"left","left",-399115937));
var top = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22166__$1,new cljs.core.Keyword(null,"top","top",-1856271961));
(tooltip_el.style.left = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(left)+"px"));

return (tooltip_el.style.top = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(top)+"px"));
} else {
return null;
}
});
app.components.x_chart.x_chart.hide_tooltip_BANG_ = (function app$components$x_chart$x_chart$hide_tooltip_BANG_(refs){
var tooltip_el = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"tooltip");
var xhair = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"crosshair");
var dot_els = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"dots");
if(cljs.core.truth_(tooltip_el)){
app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"data-visible","false");
} else {
}

if(cljs.core.truth_(xhair)){
app.components.x_chart.x_chart.set_attr_BANG_(xhair,"visibility","hidden");
} else {
}

if(cljs.core.truth_(dot_els)){
var n__5741__auto__ = app.components.x_chart.model.max_tooltip_rows;
var i = (0);
while(true){
if((i < n__5741__auto__)){
var dot_22384 = (dot_els[i]);
if(cljs.core.truth_(dot_22384)){
app.components.x_chart.x_chart.set_attr_BANG_(dot_22384,"visibility","hidden");
} else {
}

var G__22385 = (i + (1));
i = G__22385;
continue;
} else {
return null;
}
break;
}
} else {
return null;
}
});
app.components.x_chart.x_chart.find_nearest_pt = (function app$components$x_chart$x_chart$find_nearest_pt(all_pts,mx,my){
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (best,pt){
var dx = (new cljs.core.Keyword(null,"px","px",281329899).cljs$core$IFn$_invoke$arity$1(pt) - mx);
var dy = (new cljs.core.Keyword(null,"py","py",1397985916).cljs$core$IFn$_invoke$arity$1(pt) - my);
var d = ((dx * dx) + (dy * dy));
if((((best == null)) || ((d < new cljs.core.Keyword(null,"d","d",1972142424).cljs$core$IFn$_invoke$arity$1(best))))){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(pt,new cljs.core.Keyword(null,"d","d",1972142424),d);
} else {
return best;
}
}),null,all_pts);
});
app.components.x_chart.x_chart.add_mouse_listeners_BANG_ = (function app$components$x_chart$x_chart$add_mouse_listeners_BANG_(el,hit_el,all_pts,refs,W,H,cursor,show_tooltip_QMARK_,x_fmt,y_fmt,series){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cursor,"none")){
return null;
} else {
hit_el.addEventListener("mousemove",(function (ev){
var rect = hit_el.getBoundingClientRect();
var mx = (ev.clientX - rect.x);
var my = (ev.clientY - rect.y);
var pts = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cursor,"x"))?app.components.x_chart.model.find_pts_at_x(all_pts,mx):(function (){var p = app.components.x_chart.x_chart.find_nearest_pt(all_pts,mx,my);
if(cljs.core.truth_(p)){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p], null);
} else {
return null;
}
})());
var pt = cljs.core.first(pts);
if(cljs.core.truth_(pt)){
if(cljs.core.truth_(show_tooltip_QMARK_)){
var hd_22386 = app.components.x_chart.x_chart.build_hover_data(pts,x_fmt,y_fmt,series);
app.components.x_chart.x_chart.show_tooltip_BANG_(refs,hd_22386,W,H);
} else {
}

return app.components.x_chart.x_chart.dispatch_BANG_(el,app.components.x_chart.model.event_hover,false,({"seriesId": new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(pt), "index": new cljs.core.Keyword(null,"i","i",-1386841315).cljs$core$IFn$_invoke$arity$1(pt), "x": new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt), "y": new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt), "value": new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt)}));
} else {
return null;
}
}));

hit_el.addEventListener("mouseleave",(function (_){
if(cljs.core.truth_(show_tooltip_QMARK_)){
return app.components.x_chart.x_chart.hide_tooltip_BANG_(refs);
} else {
return null;
}
}));

return hit_el.addEventListener("click",(function (ev){
var rect = hit_el.getBoundingClientRect();
var mx = (ev.clientX - rect.x);
var my = (ev.clientY - rect.y);
var pt = app.components.x_chart.x_chart.find_nearest_pt(all_pts,mx,my);
if(cljs.core.truth_(pt)){
return app.components.x_chart.x_chart.dispatch_BANG_(el,app.components.x_chart.model.event_select,false,({"seriesId": new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(pt), "index": new cljs.core.Keyword(null,"i","i",-1386841315).cljs$core$IFn$_invoke$arity$1(pt), "x": new cljs.core.Keyword(null,"x","x",2099068185).cljs$core$IFn$_invoke$arity$1(pt), "y": new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt), "value": new cljs.core.Keyword(null,"y","y",-1757859776).cljs$core$IFn$_invoke$arity$1(pt)}));
} else {
return null;
}
}));
}
});
app.components.x_chart.x_chart.chart_width = (function app$components$x_chart$x_chart$chart_width(el,refs){
var container = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"container");
var w = (cljs.core.truth_(container)?container.clientWidth:(0));
if((w > (0))){
return w;
} else {
return (400);
}
});
app.components.x_chart.x_chart.render_BANG_ = (function app$components$x_chart$x_chart$render_BANG_(el){
var refs = app.components.x_chart.x_chart.goog$module$goog$object.get(el,app.components.x_chart.x_chart.k_refs);
if(cljs.core.truth_(refs)){
var svg = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"svg");
var sr_el = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"sr");
var m = app.components.x_chart.x_chart.read_model(el);
var map__22225 = m;
var map__22225__$1 = cljs.core.__destructure_map(map__22225);
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var y_ticks = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"y-ticks","y-ticks",-843622722));
var height = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var tooltip_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"tooltip?","tooltip?",-642753154));
var grid_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"grid?","grid?",-288406689));
var x_kind = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"x-kind","x-kind",693167455));
var y_domain = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"y-domain","y-domain",-969203007));
var axes_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"axes?","axes?",1786285669));
var series = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"series","series",600710694));
var loading_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"loading?","loading?",1905707049));
var y_fmt = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"y-fmt","y-fmt",2092412811));
var cursor = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"cursor","cursor",1011937484));
var type = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var padding = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"padding","padding",1660304693));
var x_fmt = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22225__$1,new cljs.core.Keyword(null,"x-fmt","x-fmt",2073358008));
var W = app.components.x_chart.x_chart.chart_width(el,refs);
var H = height;
var bounds = app.components.x_chart.x_chart.plot_bounds(W,H,padding);
var map__22226 = bounds;
var map__22226__$1 = cljs.core.__destructure_map(map__22226);
var x0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22226__$1,new cljs.core.Keyword(null,"x0","x0",410843387));
var y0 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22226__$1,new cljs.core.Keyword(null,"y0","y0",111454807));
var x1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22226__$1,new cljs.core.Keyword(null,"x1","x1",-1863922247));
var y1 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22226__$1,new cljs.core.Keyword(null,"y1","y1",589123466));
var x_dom = app.components.x_chart.model.domain_x(series);
var baseline_y = app.components.x_chart.x_chart.scale_y((0),y_domain,y0,y1);
app.components.x_chart.x_chart.set_attr_BANG_(svg,"viewBox",(""+"0 0 "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(W)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(H)));

app.components.x_chart.x_chart.set_attr_BANG_(svg,"width",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(W)));

app.components.x_chart.x_chart.set_attr_BANG_(svg,"height",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(H)));

app.components.x_chart.x_chart.remove_children_BANG_(svg);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs,"crosshair",null);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs,"dots",null);

if(cljs.core.truth_(loading_QMARK_)){
} else {
if(cljs.core.truth_(grid_QMARK_)){
app.components.x_chart.x_chart.draw_grid_BANG_(svg,m,bounds);
} else {
}

var all_pts_22389 = cljs.core.vec(cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic((function (s,i){
var pts = app.components.x_chart.x_chart.compute_series_pts(s,i,x_kind,x_dom,bounds,y_domain);
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (pt){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$variadic(pt,new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(s),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(s),new cljs.core.Keyword(null,"y0-baseline","y0-baseline",613579610),baseline_y], 0));
}),pts);
}),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([series,cljs.core.range.cljs$core$IFn$_invoke$arity$0()], 0)));
cljs.core.doall.cljs$core$IFn$_invoke$arity$1(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2((function (i,s){
var color = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"color","color",1011675173).cljs$core$IFn$_invoke$arity$1(s);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_chart.x_chart.series_color_var(i);
}
})();
var pts = cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__22205_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(p1__22205_SHARP_),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(s));
}),all_pts_22389);
var G__22250 = type;
switch (G__22250) {
case "bar":
return app.components.x_chart.x_chart.draw_bar_series_BANG_(svg,i,pts,color,bounds,cljs.core.count(series));

break;
case "area":
app.components.x_chart.x_chart.draw_area_series_BANG_(svg,i,pts,color,baseline_y);

return app.components.x_chart.x_chart.draw_line_series_BANG_(svg,i,pts,color);

break;
default:
return app.components.x_chart.x_chart.draw_line_series_BANG_(svg,i,pts,color);

}
}),series));

if(cljs.core.truth_(axes_QMARK_)){
app.components.x_chart.x_chart.draw_axes_BANG_(svg,m,bounds,padding);
} else {
}

var xhair_22391 = app.components.x_chart.x_chart.make_svg_el("line");
app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"x1",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x0)));

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"y1",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y0)));

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"x2",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x0)));

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"y2",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y1)));

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"stroke","var(--x-chart-crosshair-color)");

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"stroke-width","var(--x-chart-crosshair-width)");

app.components.x_chart.x_chart.set_attr_BANG_(xhair_22391,"visibility","hidden");

svg.appendChild(xhair_22391);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs,"crosshair",xhair_22391);

var dot_arr_22394 = (new Array(app.components.x_chart.model.max_tooltip_rows));
var n__5741__auto___22395 = app.components.x_chart.model.max_tooltip_rows;
var i_22396 = (0);
while(true){
if((i_22396 < n__5741__auto___22395)){
var dot_22398 = app.components.x_chart.x_chart.make_svg_el("circle");
app.components.x_chart.x_chart.set_attr_BANG_(dot_22398,"r",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(app.components.x_chart.model.dot_r)));

app.components.x_chart.x_chart.set_attr_BANG_(dot_22398,"stroke","var(--x-chart-tooltip-bg)");

app.components.x_chart.x_chart.set_attr_BANG_(dot_22398,"stroke-width","2");

app.components.x_chart.x_chart.set_attr_BANG_(dot_22398,"visibility","hidden");

svg.appendChild(dot_22398);

(dot_arr_22394[i_22396] = dot_22398);

var G__22399 = (i_22396 + (1));
i_22396 = G__22399;
continue;
} else {
}
break;
}

app.components.x_chart.x_chart.goog$module$goog$object.set(refs,"dots",dot_arr_22394);

var hit_22400 = app.components.x_chart.x_chart.make_svg_el("rect");
app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"x",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x0)));

app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"y",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y0)));

app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"width",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),(x1 - x0)))));

app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"height",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),(y1 - y0)))));

app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"fill","transparent");

app.components.x_chart.x_chart.set_attr_BANG_(hit_22400,"style","cursor:crosshair;");

if(cljs.core.truth_(disabled_QMARK_)){
} else {
app.components.x_chart.x_chart.add_mouse_listeners_BANG_(el,hit_22400,all_pts_22389,refs,W,H,cursor,tooltip_QMARK_,x_fmt,y_fmt,series);
}

svg.appendChild(hit_22400);

(sr_el.textContent = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.count(series))+" series chart"));
}

if(cljs.core.truth_(loading_QMARK_)){
return app.components.x_chart.x_chart.hide_tooltip_BANG_(refs);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_chart.x_chart.make_tooltip_row_BANG_ = (function app$components$x_chart$x_chart$make_tooltip_row_BANG_(){
var row = app.components.x_chart.x_chart.make_el("div");
var swatch = app.components.x_chart.x_chart.make_el("span");
var label = app.components.x_chart.x_chart.make_el("span");
var value = app.components.x_chart.x_chart.make_el("span");
app.components.x_chart.x_chart.set_attr_BANG_(row,"part","tooltip-row");

app.components.x_chart.x_chart.set_attr_BANG_(row,"data-hidden","true");

app.components.x_chart.x_chart.set_attr_BANG_(swatch,"part","tooltip-swatch");

app.components.x_chart.x_chart.set_attr_BANG_(label,"part","tooltip-label");

app.components.x_chart.x_chart.set_attr_BANG_(value,"part","tooltip-value");

row.appendChild(swatch);

row.appendChild(label);

row.appendChild(value);

return row;
});
app.components.x_chart.x_chart.make_shadow_BANG_ = (function app$components$x_chart$x_chart$make_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_chart.x_chart.make_el("style");
var container = app.components.x_chart.x_chart.make_el("div");
var svg = app.components.x_chart.x_chart.make_svg_el("svg");
var sr_el = app.components.x_chart.x_chart.make_el("div");
var tooltip_el = app.components.x_chart.x_chart.make_el("div");
var header_el = app.components.x_chart.x_chart.make_el("div");
var body_el = app.components.x_chart.x_chart.make_el("div");
var row_arr = (new Array(app.components.x_chart.model.max_tooltip_rows));
var m = app.components.x_chart.x_chart.read_model(el);
(style_el.textContent = app.components.x_chart.x_chart.style_text);

app.components.x_chart.x_chart.set_attr_BANG_(container,"part","container");

app.components.x_chart.x_chart.set_attr_BANG_(container,"tabindex","0");

app.components.x_chart.x_chart.set_attr_BANG_(svg,"part","svg");

app.components.x_chart.x_chart.set_attr_BANG_(svg,"aria-hidden","true");

app.components.x_chart.x_chart.set_attr_BANG_(svg,"role","img");

app.components.x_chart.x_chart.set_attr_BANG_(sr_el,"part","sr-only");

app.components.x_chart.x_chart.set_attr_BANG_(sr_el,"aria-live","polite");

app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"part","tooltip");

app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"role","tooltip");

app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"aria-hidden","true");

app.components.x_chart.x_chart.set_attr_BANG_(tooltip_el,"data-visible","false");

app.components.x_chart.x_chart.set_attr_BANG_(header_el,"part","tooltip-header");

app.components.x_chart.x_chart.set_attr_BANG_(body_el,"part","tooltip-body");

var n__5741__auto___22404 = app.components.x_chart.model.max_tooltip_rows;
var i_22405 = (0);
while(true){
if((i_22405 < n__5741__auto___22404)){
var row_22406 = app.components.x_chart.x_chart.make_tooltip_row_BANG_();
body_el.appendChild(row_22406);

(row_arr[i_22405] = row_22406);

var G__22407 = (i_22405 + (1));
i_22405 = G__22407;
continue;
} else {
}
break;
}

tooltip_el.appendChild(header_el);

tooltip_el.appendChild(body_el);

container.appendChild(svg);

container.appendChild(sr_el);

container.appendChild(tooltip_el);

root.appendChild(style_el);

root.appendChild(container);

var refs_22408 = ({});
app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"root",root);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"container",container);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"svg",svg);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"sr",sr_el);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"tooltip",tooltip_el);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"tooltip-header",header_el);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"tooltip-body",body_el);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"tooltip-rows",row_arr);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"crosshair",null);

app.components.x_chart.x_chart.goog$module$goog$object.set(refs_22408,"dots",null);

app.components.x_chart.x_chart.goog$module$goog$object.set(el,app.components.x_chart.x_chart.k_refs,refs_22408);

var ro = (new ResizeObserver((function (_){
return app.components.x_chart.x_chart.render_BANG_(el);
})));
ro.observe(container);

return app.components.x_chart.x_chart.goog$module$goog$object.set(app.components.x_chart.x_chart.goog$module$goog$object.get(el,app.components.x_chart.x_chart.k_refs),"ro",ro);
});
app.components.x_chart.x_chart.connected_BANG_ = (function app$components$x_chart$x_chart$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_chart.x_chart.goog$module$goog$object.get(el,app.components.x_chart.x_chart.k_refs))){
} else {
app.components.x_chart.x_chart.make_shadow_BANG_(el);
}

return app.components.x_chart.x_chart.render_BANG_(el);
});
app.components.x_chart.x_chart.disconnected_BANG_ = (function app$components$x_chart$x_chart$disconnected_BANG_(el){
var refs = app.components.x_chart.x_chart.goog$module$goog$object.get(el,app.components.x_chart.x_chart.k_refs);
if(cljs.core.truth_(refs)){
var ro = app.components.x_chart.x_chart.goog$module$goog$object.get(refs,"ro");
if(cljs.core.truth_(ro)){
return ro.disconnect();
} else {
return null;
}
} else {
return null;
}
});
app.components.x_chart.x_chart.attribute_changed_BANG_ = (function app$components$x_chart$x_chart$attribute_changed_BANG_(el,_name,_old,_new){
if(cljs.core.truth_(app.components.x_chart.x_chart.goog$module$goog$object.get(el,app.components.x_chart.x_chart.k_refs))){
return app.components.x_chart.x_chart.render_BANG_(el);
} else {
return null;
}
});
app.components.x_chart.x_chart.define_str_prop_BANG_ = (function app$components$x_chart$x_chart$define_str_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return this$.getAttribute(attr_name);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr_name,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr_name);
}
})}));
});
app.components.x_chart.x_chart.define_bool_prop_BANG_ = (function app$components$x_chart$x_chart$define_bool_prop_BANG_(proto,prop_name,attr_name){
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
app.components.x_chart.x_chart.define_data_prop_BANG_ = (function app$components$x_chart$x_chart$define_data_prop_BANG_(proto){
return Object.defineProperty(proto,"data",({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
var cached = app.components.x_chart.x_chart.goog$module$goog$object.get(this$,app.components.x_chart.x_chart.k_data);
var or__5142__auto__ = cached;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
var raw = this$.getAttribute(app.components.x_chart.model.attr_data);
return app.components.x_chart.model.parse_series_data(raw);
}
}), "set": (function (v){
var this$ = this;
app.components.x_chart.x_chart.goog$module$goog$object.set(this$,app.components.x_chart.x_chart.k_data,v);

if(cljs.core.truth_(cljs.core.array_QMARK_(v))){
return this$.setAttribute(app.components.x_chart.model.attr_data,JSON.stringify(v));
} else {
return this$.removeAttribute(app.components.x_chart.model.attr_data);
}
})}));
});
app.components.x_chart.x_chart.make_constructor = (function app$components$x_chart$x_chart$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_chart.x_chart.init_BANG_ = (function app$components$x_chart$x_chart$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_chart.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_chart.x_chart.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_chart.x_chart.define_str_prop_BANG_(proto,"type",app.components.x_chart.model.attr_type);

app.components.x_chart.x_chart.define_str_prop_BANG_(proto,"cursor",app.components.x_chart.model.attr_cursor);

app.components.x_chart.x_chart.define_str_prop_BANG_(proto,"selected",app.components.x_chart.model.attr_selected);

app.components.x_chart.x_chart.define_bool_prop_BANG_(proto,"grid",app.components.x_chart.model.attr_grid);

app.components.x_chart.x_chart.define_bool_prop_BANG_(proto,"axes",app.components.x_chart.model.attr_axes);

app.components.x_chart.x_chart.define_bool_prop_BANG_(proto,"tooltip",app.components.x_chart.model.attr_tooltip);

app.components.x_chart.x_chart.define_bool_prop_BANG_(proto,"disabled",app.components.x_chart.model.attr_disabled);

app.components.x_chart.x_chart.define_bool_prop_BANG_(proto,"loading",app.components.x_chart.model.attr_loading);

app.components.x_chart.x_chart.define_data_prop_BANG_(proto);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_chart.x_chart.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_chart.x_chart.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_chart.x_chart.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor["observedAttributes"] = app.components.x_chart.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_chart.model.tag_name,ctor);
}
});

//# sourceMappingURL=app.components.x_chart.x_chart.js.map
