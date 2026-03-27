goog.provide('app.components.x_timeline.model');
app.components.x_timeline.model.tag_name = "x-timeline";
app.components.x_timeline.model.attr_label = "label";
app.components.x_timeline.model.attr_position = "position";
app.components.x_timeline.model.attr_striped = "striped";
app.components.x_timeline.model.observed_attributes = [app.components.x_timeline.model.attr_label,app.components.x_timeline.model.attr_position,app.components.x_timeline.model.attr_striped];
app.components.x_timeline.model.child_tag = "x-timeline-item";
app.components.x_timeline.model.child_event_connected = "x-timeline-item-connected";
app.components.x_timeline.model.child_event_disconnected = "x-timeline-item-disconnected";
app.components.x_timeline.model.child_event_click = "x-timeline-item-click";
app.components.x_timeline.model.event_select = "x-timeline-select";
app.components.x_timeline.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"striped","striped",-628686784),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);
app.components.x_timeline.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_timeline.model.event_select,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"index","index",-1531685915),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null)]);
app.components.x_timeline.model.valid_positions = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["start",null,"end",null,"alternating",null], null), null);
/**
 * Normalise a raw position attribute string to "start", "end", or "alternating".
 *   Unknown/nil values fall back to "start".
 */
app.components.x_timeline.model.parse_position = (function app$components$x_timeline$model$parse_position(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_timeline.model.valid_positions,v)){
return v;
} else {
return "start";
}
});
/**
 * Return the data-position string to assign to a child item.
 *   position — the coordinator's resolved position string
 *   index    — the child's 0-based index
 * 
 *   :start       → always "start"
 *   :end         → always "end"
 *   :alternating → even index → "start", odd index → "end"
 */
app.components.x_timeline.model.item_position = (function app$components$x_timeline$model$item_position(position,index){
var G__23366 = position;
switch (G__23366) {
case "start":
return "start";

break;
case "end":
return "end";

break;
case "alternating":
if((cljs.core.mod(index,(2)) === (0))){
return "start";
} else {
return "end";
}

break;
default:
return "start";

}
});
/**
 * Normalise raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :label-raw     string | nil
 *  :position-raw  string | nil
 *  :striped?      boolean
 * 
 *   Output keys:
 *  :label     string
 *  :position  string  — "start" | "end" | "alternating"
 *  :striped?  boolean
 */
app.components.x_timeline.model.normalize = (function app$components$x_timeline$model$normalize(p__23367){
var map__23368 = p__23367;
var map__23368__$1 = cljs.core.__destructure_map(map__23368);
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23368__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var position_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23368__$1,new cljs.core.Keyword(null,"position-raw","position-raw",248130625));
var striped_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23368__$1,new cljs.core.Keyword(null,"striped?","striped?",-797214979));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"position","position",-2011731912),app.components.x_timeline.model.parse_position(position_raw),new cljs.core.Keyword(null,"striped?","striped?",-797214979),cljs.core.boolean$(striped_QMARK_)], null);
});
/**
 * Build the event detail map for an x-timeline-select event.
 */
app.components.x_timeline.model.select_detail = (function app$components$x_timeline$model$select_detail(index,status,label){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"index","index",-1531685915),(function (){var or__5142__auto__ = index;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
})(),new cljs.core.Keyword(null,"status","status",-1997798413),(function (){var or__5142__auto__ = status;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__5142__auto__ = label;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()], null);
});

//# sourceMappingURL=app.components.x_timeline.model.js.map
