goog.provide('app.components.x_context_menu.model');
app.components.x_context_menu.model.tag_name = "x-context-menu";
app.components.x_context_menu.model.attr_open = "open";
app.components.x_context_menu.model.attr_disabled = "disabled";
app.components.x_context_menu.model.attr_placement = "placement";
app.components.x_context_menu.model.attr_offset = "offset";
app.components.x_context_menu.model.attr_z_index = "z-index";
app.components.x_context_menu.model.observed_attributes = ["open","disabled","placement","offset","z-index"];
app.components.x_context_menu.model.valid_placements = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, ["right-start",null,"bottom-start",null,"bottom-end",null,"top-end",null,"top-start",null,"left-start",null], null), null);
app.components.x_context_menu.model.default_placement = "bottom-start";
app.components.x_context_menu.model.default_offset = (8);
app.components.x_context_menu.model.default_z_index = (1000);
app.components.x_context_menu.model.event_open_request = "x-context-menu-open-request";
app.components.x_context_menu.model.event_open = "x-context-menu-open";
app.components.x_context_menu.model.event_close_request = "x-context-menu-close-request";
app.components.x_context_menu.model.event_close = "x-context-menu-close";
app.components.x_context_menu.model.event_select = "x-context-menu-select";
app.components.x_context_menu.model.property_api = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_context_menu.model.attr_open], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_context_menu.model.attr_disabled], null),new cljs.core.Keyword(null,"placement","placement",768366651),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_context_menu.model.attr_placement], null),new cljs.core.Keyword(null,"offset","offset",296498311),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_context_menu.model.attr_offset], null),new cljs.core.Keyword(null,"z-index","z-index",1892827090),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_context_menu.model.attr_z_index], null)], null);
app.components.x_context_menu.model.parse_placement = (function app$components$x_context_menu$model$parse_placement(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_context_menu.model.valid_placements,s)))){
return s;
} else {
return app.components.x_context_menu.model.default_placement;
}
});
app.components.x_context_menu.model.parse_int_pos = (function app$components$x_context_menu$model$parse_int_pos(s,default_val){
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
app.components.x_context_menu.model.parse_bool_attr = (function app$components$x_context_menu$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
app.components.x_context_menu.model.normalize = (function app$components$x_context_menu$model$normalize(p__22284){
var map__22285 = p__22284;
var map__22285__$1 = cljs.core.__destructure_map(map__22285);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22285__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22285__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var placement_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22285__$1,new cljs.core.Keyword(null,"placement-raw","placement-raw",-1500957198));
var offset_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22285__$1,new cljs.core.Keyword(null,"offset-raw","offset-raw",-1418404095));
var z_index_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22285__$1,new cljs.core.Keyword(null,"z-index-raw","z-index-raw",-1179679257));
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"open?","open?",1238443125),app.components.x_context_menu.model.parse_bool_attr(open_present_QMARK_),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),app.components.x_context_menu.model.parse_bool_attr(disabled_present_QMARK_),new cljs.core.Keyword(null,"placement","placement",768366651),app.components.x_context_menu.model.parse_placement(placement_raw),new cljs.core.Keyword(null,"offset","offset",296498311),app.components.x_context_menu.model.parse_int_pos(offset_raw,app.components.x_context_menu.model.default_offset),new cljs.core.Keyword(null,"z-index","z-index",1892827090),app.components.x_context_menu.model.parse_int_pos(z_index_raw,app.components.x_context_menu.model.default_z_index)], null);
});
app.components.x_context_menu.model.fits_QMARK_ = (function app$components$x_context_menu$model$fits_QMARK_(edge,size,limit,margin){
return (((edge >= margin)) && (((edge + size) <= (limit - margin))));
});
app.components.x_context_menu.model.opposite_placement = (function app$components$x_context_menu$model$opposite_placement(placement){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"bottom-start")){
return "top-start";
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"bottom-end")){
return "top-end";
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"top-start")){
return "bottom-start";
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"top-end")){
return "bottom-end";
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"right-start")){
return "left-start";
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(placement,"left-start")){
return "right-start";
} else {
return placement;

}
}
}
}
}
}
});
app.components.x_context_menu.model.candidate_xy = (function app$components$x_context_menu$model$candidate_xy(placement,offset,anchor_rect,panel_size){
var map__22287 = anchor_rect;
var map__22287__$1 = cljs.core.__destructure_map(map__22287);
var ax = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22287__$1,new cljs.core.Keyword(null,"x","x",2099068185));
var ay = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22287__$1,new cljs.core.Keyword(null,"y","y",-1757859776));
var aw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22287__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var ah = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22287__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var map__22288 = panel_size;
var map__22288__$1 = cljs.core.__destructure_map(map__22288);
var pw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22288__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var ph = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22288__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var G__22289 = placement;
switch (G__22289) {
case "bottom-start":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),ax,new cljs.core.Keyword(null,"y","y",-1757859776),((ay + ah) + offset)], null);

break;
case "bottom-end":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),((ax + aw) - pw),new cljs.core.Keyword(null,"y","y",-1757859776),((ay + ah) + offset)], null);

break;
case "top-start":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),ax,new cljs.core.Keyword(null,"y","y",-1757859776),((ay - ph) - offset)], null);

break;
case "top-end":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),((ax + aw) - pw),new cljs.core.Keyword(null,"y","y",-1757859776),((ay - ph) - offset)], null);

break;
case "right-start":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),((ax + aw) + offset),new cljs.core.Keyword(null,"y","y",-1757859776),ay], null);

break;
case "left-start":
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),((ax - pw) - offset),new cljs.core.Keyword(null,"y","y",-1757859776),ay], null);

break;
default:
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"x","x",2099068185),ax,new cljs.core.Keyword(null,"y","y",-1757859776),((ay + ah) + offset)], null);

}
});
app.components.x_context_menu.model.compute_position = (function app$components$x_context_menu$model$compute_position(placement,offset,anchor_rect,panel_size,viewport_size,margin){
var map__22292 = viewport_size;
var map__22292__$1 = cljs.core.__destructure_map(map__22292);
var vw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22292__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var vh = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22292__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var map__22293 = panel_size;
var map__22293__$1 = cljs.core.__destructure_map(map__22293);
var pw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22293__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var ph = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22293__$1,new cljs.core.Keyword(null,"height","height",1025178622));
var try_placement = (function (p){
var map__22294 = app.components.x_context_menu.model.candidate_xy(p,offset,anchor_rect,panel_size);
var map__22294__$1 = cljs.core.__destructure_map(map__22294);
var cx = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22294__$1,new cljs.core.Keyword(null,"x","x",2099068185));
var cy = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22294__$1,new cljs.core.Keyword(null,"y","y",-1757859776));
var fits_x = app.components.x_context_menu.model.fits_QMARK_(cx,pw,vw,margin);
var fits_y = app.components.x_context_menu.model.fits_QMARK_(cy,ph,vh,margin);
var x = cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2(cx,margin),((vw - pw) - margin));
var y = cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2(cy,margin),((vh - ph) - margin));
var max_h = ((vh - y) - margin);
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"x","x",2099068185),x,new cljs.core.Keyword(null,"y","y",-1757859776),y,new cljs.core.Keyword(null,"final-placement","final-placement",-1586044424),p,new cljs.core.Keyword(null,"fits","fits",2008565808),((fits_x) && (fits_y)),new cljs.core.Keyword(null,"max-height","max-height",-612563804),cljs.core.max.cljs$core$IFn$_invoke$arity$2((40),max_h)], null);
});
var primary = try_placement(placement);
var flipped = try_placement(app.components.x_context_menu.model.opposite_placement(placement));
if(cljs.core.truth_((function (){var or__5142__auto__ = new cljs.core.Keyword(null,"fits","fits",2008565808).cljs$core$IFn$_invoke$arity$1(primary);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.not(new cljs.core.Keyword(null,"fits","fits",2008565808).cljs$core$IFn$_invoke$arity$1(flipped));
}
})())){
return primary;
} else {
return flipped;
}
});

//# sourceMappingURL=app.components.x_context_menu.model.js.map
