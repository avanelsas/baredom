goog.provide('app.components.x_timeline_item.model');
app.components.x_timeline_item.model.tag_name = "x-timeline-item";
app.components.x_timeline_item.model.attr_label = "label";
app.components.x_timeline_item.model.attr_title = "title";
app.components.x_timeline_item.model.attr_status = "status";
app.components.x_timeline_item.model.attr_icon = "icon";
app.components.x_timeline_item.model.attr_connector = "connector";
app.components.x_timeline_item.model.attr_position = "position";
app.components.x_timeline_item.model.attr_disabled = "disabled";
app.components.x_timeline_item.model.data_attr_last = "data-last";
app.components.x_timeline_item.model.data_attr_index = "data-index";
app.components.x_timeline_item.model.data_attr_position = "data-position";
app.components.x_timeline_item.model.data_attr_striped = "data-striped";
app.components.x_timeline_item.model.observed_attributes = ["label","title","status","icon","connector","position","disabled","data-last","data-index","data-position","data-striped"];
app.components.x_timeline_item.model.event_connected = "x-timeline-item-connected";
app.components.x_timeline_item.model.event_disconnected = "x-timeline-item-disconnected";
app.components.x_timeline_item.model.event_click = "x-timeline-item-click";
app.components.x_timeline_item.model.property_api = new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"title","title",636505583),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"icon","icon",1679606541),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"connector","connector",-1517293248),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);
app.components.x_timeline_item.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_timeline_item.model.event_connected,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null),app.components.x_timeline_item.model.event_disconnected,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null),app.components.x_timeline_item.model.event_click,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_timeline_item.model.status__GT_kw = new cljs.core.PersistentArrayMap(null, 5, ["pending",new cljs.core.Keyword(null,"pending","pending",-220036727),"active",new cljs.core.Keyword(null,"active","active",1895962068),"complete",new cljs.core.Keyword(null,"complete","complete",-500388775),"error",new cljs.core.Keyword(null,"error","error",-978969032),"warning",new cljs.core.Keyword(null,"warning","warning",-1685650671)], null);
app.components.x_timeline_item.model.kw__GT_status = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"pending","pending",-220036727),"pending",new cljs.core.Keyword(null,"active","active",1895962068),"active",new cljs.core.Keyword(null,"complete","complete",-500388775),"complete",new cljs.core.Keyword(null,"error","error",-978969032),"error",new cljs.core.Keyword(null,"warning","warning",-1685650671),"warning"], null);
app.components.x_timeline_item.model.status__GT_icon = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"pending","pending",-220036727),"\u25CB",new cljs.core.Keyword(null,"active","active",1895962068),"\u25CF",new cljs.core.Keyword(null,"complete","complete",-500388775),"\u2713",new cljs.core.Keyword(null,"error","error",-978969032),"\u2715",new cljs.core.Keyword(null,"warning","warning",-1685650671),"!"], null);
app.components.x_timeline_item.model.connector__GT_kw = new cljs.core.PersistentArrayMap(null, 3, ["solid",new cljs.core.Keyword(null,"solid","solid",-2023773691),"dashed",new cljs.core.Keyword(null,"dashed","dashed",-1449249319),"none",new cljs.core.Keyword(null,"none","none",1333468478)], null);
app.components.x_timeline_item.model.position__GT_kw = new cljs.core.PersistentArrayMap(null, 2, ["start",new cljs.core.Keyword(null,"start","start",-355208981),"end",new cljs.core.Keyword(null,"end","end",-268185958)], null);
/**
 * Normalise a raw status attribute string to a keyword. Defaults to :pending.
 */
app.components.x_timeline_item.model.parse_status = (function app$components$x_timeline_item$model$parse_status(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.status__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"pending","pending",-220036727);
}
});
/**
 * Convert an internal status keyword back to its attribute string.
 */
app.components.x_timeline_item.model.status__GT_attr = (function app$components$x_timeline_item$model$status__GT_attr(k){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.kw__GT_status,k);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "pending";
}
});
/**
 * Normalise a raw connector attribute string. Defaults to :solid.
 */
app.components.x_timeline_item.model.parse_connector = (function app$components$x_timeline_item$model$parse_connector(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.connector__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"solid","solid",-2023773691);
}
});
/**
 * Normalise a raw position attribute string to :start or :end. Defaults to :start.
 */
app.components.x_timeline_item.model.parse_position = (function app$components$x_timeline_item$model$parse_position(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.position__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"start","start",-355208981);
}
});
/**
 * Normalise icon attribute: absent/nil/empty/"none" become nil.
 */
app.components.x_timeline_item.model.normalize_icon = (function app$components$x_timeline_item$model$normalize_icon(s){
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
 * Parse data-index attribute to a non-negative integer, or nil if absent/invalid.
 */
app.components.x_timeline_item.model.parse_data_index = (function app$components$x_timeline_item$model$parse_data_index(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((typeof n === 'number') && (((cljs.core.not(isNaN(n))) && ((n >= (0))))))){
return n;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Returns the effective position keyword for rendering.
 *   Reads data-position first (parent override, accepts only start/end).
 *   Falls back to position attribute. Defaults to :start.
 */
app.components.x_timeline_item.model.effective_position = (function app$components$x_timeline_item$model$effective_position(data_pos_raw,pos_raw){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.position__GT_kw,((typeof data_pos_raw === 'string')?data_pos_raw.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_timeline_item.model.parse_position(pos_raw);
}
});
/**
 * Return the default icon glyph for a status keyword.
 */
app.components.x_timeline_item.model.icon_for_status = (function app$components$x_timeline_item$model$icon_for_status(k){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.status__GT_icon,k);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_timeline_item.model.status__GT_icon,new cljs.core.Keyword(null,"pending","pending",-220036727));
}
});
/**
 * Build the ARIA label string for the marker element.
 */
app.components.x_timeline_item.model.marker_aria_label = (function app$components$x_timeline_item$model$marker_aria_label(status,label,data_index){
var status_str = app.components.x_timeline_item.model.status__GT_attr(status);
var index_str = (((!((data_index == null))))?(""+"Step "+cljs.core.str.cljs$core$IFn$_invoke$arity$1((data_index + (1)))+": "):null);
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(index_str)+cljs.core.str.cljs$core$IFn$_invoke$arity$1(((((typeof label === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label,""))))?(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(label)+" "):null))+"("+cljs.core.str.cljs$core$IFn$_invoke$arity$1(status_str)+")");
});
/**
 * Normalise raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :label-raw        string | nil
 *  :title-raw        string | nil
 *  :status-raw       string | nil
 *  :icon-present?    boolean
 *  :icon-raw         string | nil
 *  :connector-raw    string | nil
 *  :position-raw     string | nil
 *  :disabled?        boolean
 *  :data-last?       boolean
 *  :data-index-raw   string | nil
 *  :data-position-raw string | nil
 *  :data-striped?    boolean
 * 
 *   Output keys:
 *  :label             string
 *  :title             string
 *  :status            keyword  — :pending | :active | :complete | :error | :warning
 *  :icon-mode         keyword  — :default | :custom | :hidden
 *  :icon              string | nil
 *  :connector         keyword  — :solid | :dashed | :none
 *  :effective-position keyword — :start | :end
 *  :disabled?         boolean
 *  :data-last?        boolean
 *  :data-index        int | nil
 *  :data-striped?     boolean
 *  :marker-icon       string | nil
 *  :marker-aria       string
 */
app.components.x_timeline_item.model.normalize = (function app$components$x_timeline_item$model$normalize(p__23287){
var map__23289 = p__23287;
var map__23289__$1 = cljs.core.__destructure_map(map__23289);
var data_index_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"data-index-raw","data-index-raw",491574874));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var position_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"position-raw","position-raw",248130625));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var data_striped_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"data-striped?","data-striped?",906911587));
var connector_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"connector-raw","connector-raw",106333796));
var data_position_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"data-position-raw","data-position-raw",256144356));
var status_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"status-raw","status-raw",-2121061147));
var data_last_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"data-last?","data-last?",-1110326839));
var icon_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"icon-present?","icon-present?",2040576778));
var title_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"title-raw","title-raw",976469452));
var icon_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23289__$1,new cljs.core.Keyword(null,"icon-raw","icon-raw",480816214));
var status = app.components.x_timeline_item.model.parse_status(status_raw);
var icon_STAR_ = app.components.x_timeline_item.model.normalize_icon(icon_raw);
var icon_mode = (cljs.core.truth_((function (){var and__5140__auto__ = icon_present_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return (icon_STAR_ == null);
} else {
return and__5140__auto__;
}
})())?new cljs.core.Keyword(null,"hidden","hidden",-312506092):(((!((icon_STAR_ == null))))?new cljs.core.Keyword(null,"custom","custom",340151948):new cljs.core.Keyword(null,"default","default",-1987822328)
));
var marker_icon = (function (){var G__23302 = icon_mode;
var G__23302__$1 = (((G__23302 instanceof cljs.core.Keyword))?G__23302.fqn:null);
switch (G__23302__$1) {
case "default":
return app.components.x_timeline_item.model.icon_for_status(status);

break;
case "custom":
return icon_STAR_;

break;
case "hidden":
return null;

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__23302__$1))));

}
})();
var data_index = app.components.x_timeline_item.model.parse_data_index(data_index_raw);
var eff_pos = app.components.x_timeline_item.model.effective_position(data_position_raw,position_raw);
var label = (function (){var or__5142__auto__ = label_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var title = (function (){var or__5142__auto__ = title_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"connector","connector",-1517293248),new cljs.core.Keyword(null,"data-index","data-index",1161508385),new cljs.core.Keyword(null,"marker-icon","marker-icon",424963779),new cljs.core.Keyword(null,"data-striped?","data-striped?",906911587),new cljs.core.Keyword(null,"data-last?","data-last?",-1110326839),new cljs.core.Keyword(null,"icon","icon",1679606541),new cljs.core.Keyword(null,"title","title",636505583),new cljs.core.Keyword(null,"status","status",-1997798413),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"marker-aria","marker-aria",-353426855),new cljs.core.Keyword(null,"icon-mode","icon-mode",-2015012071),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"effective-position","effective-position",840072317)],[app.components.x_timeline_item.model.parse_connector(connector_raw),data_index,marker_icon,cljs.core.boolean$(data_striped_QMARK_),cljs.core.boolean$(data_last_QMARK_),((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(icon_mode,new cljs.core.Keyword(null,"custom","custom",340151948)))?icon_STAR_:null),title,status,label,app.components.x_timeline_item.model.marker_aria_label(status,label,data_index),icon_mode,cljs.core.boolean$(disabled_QMARK_),eff_pos]);
});
/**
 * Return true when the item can receive click/keyboard interactions.
 */
app.components.x_timeline_item.model.interactive_eligible_QMARK_ = (function app$components$x_timeline_item$model$interactive_eligible_QMARK_(p__23316){
var map__23317 = p__23316;
var map__23317__$1 = cljs.core.__destructure_map(map__23317);
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23317__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
return cljs.core.not(disabled_QMARK_);
});
/**
 * Build the event detail map for an x-timeline-item-connected event.
 */
app.components.x_timeline_item.model.connected_detail = (function app$components$x_timeline_item$model$connected_detail(p__23323){
var map__23324 = p__23323;
var map__23324__$1 = cljs.core.__destructure_map(map__23324);
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23324__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23324__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var effective_position = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23324__$1,new cljs.core.Keyword(null,"effective-position","effective-position",840072317));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23324__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"status","status",-1997798413),app.components.x_timeline_item.model.status__GT_attr(status),new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"position","position",-2011731912),cljs.core.name(effective_position),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.boolean$(disabled_QMARK_)], null);
});
/**
 * Build the event detail map for an x-timeline-item-disconnected event.
 */
app.components.x_timeline_item.model.disconnected_detail = (function app$components$x_timeline_item$model$disconnected_detail(p__23335){
var map__23336 = p__23335;
var map__23336__$1 = cljs.core.__destructure_map(map__23336);
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23336__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23336__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"status","status",-1997798413),app.components.x_timeline_item.model.status__GT_attr(status),new cljs.core.Keyword(null,"label","label",1718410804),label], null);
});
/**
 * Build the event detail map for an x-timeline-item-click event.
 */
app.components.x_timeline_item.model.click_detail = (function app$components$x_timeline_item$model$click_detail(p__23340){
var map__23341 = p__23340;
var map__23341__$1 = cljs.core.__destructure_map(map__23341);
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23341__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23341__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"status","status",-1997798413),app.components.x_timeline_item.model.status__GT_attr(status),new cljs.core.Keyword(null,"label","label",1718410804),label], null);
});

//# sourceMappingURL=app.components.x_timeline_item.model.js.map
