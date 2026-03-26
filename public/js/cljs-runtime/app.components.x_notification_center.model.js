goog.provide('app.components.x_notification_center.model');
app.components.x_notification_center.model.tag_name = "x-notification-center";
app.components.x_notification_center.model.attr_position = "position";
app.components.x_notification_center.model.attr_max = "max";
app.components.x_notification_center.model.data_position = "data-position";
app.components.x_notification_center.model.event_push = "x-notification-center-push";
app.components.x_notification_center.model.event_dismiss = "x-notification-center-dismiss";
app.components.x_notification_center.model.event_empty = "x-notification-center-empty";
app.components.x_notification_center.model.alert_tag = "x-alert";
app.components.x_notification_center.model.data_notification_id = "data-notification-id";
app.components.x_notification_center.model.observed_attributes = [app.components.x_notification_center.model.attr_position,app.components.x_notification_center.model.attr_max];
app.components.x_notification_center.model.css_width = "--x-notification-center-width";
app.components.x_notification_center.model.css_gap = "--x-notification-center-gap";
app.components.x_notification_center.model.css_offset_x = "--x-notification-center-offset-x";
app.components.x_notification_center.model.css_offset_y = "--x-notification-center-offset-y";
app.components.x_notification_center.model.css_z_index = "--x-notification-center-z-index";
app.components.x_notification_center.model.valid_positions = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 6, ["top-center",null,"bottom-center",null,"top-right",null,"bottom-right",null,"top-left",null,"bottom-left",null], null), null);
app.components.x_notification_center.model.default_position = "top-right";
app.components.x_notification_center.model.default_max = (5);
/**
 * Normalise a position attribute string. Falls back to top-right for unknown values.
 */
app.components.x_notification_center.model.parse_position = (function app$components$x_notification_center$model$parse_position(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_notification_center.model.valid_positions,s.toLowerCase())))){
return s.toLowerCase();
} else {
return app.components.x_notification_center.model.default_position;
}
});
/**
 * Parse max attribute to a positive integer. Falls back to 5 for nil/invalid/non-positive.
 */
app.components.x_notification_center.model.parse_max = (function app$components$x_notification_center$model$parse_max(s){
if((s == null)){
return app.components.x_notification_center.model.default_max;
} else {
var n = parseInt((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)),(10));
if(((typeof n === 'number') && (((cljs.core.not(isNaN(n))) && ((n > (0))))))){
return n;
} else {
return app.components.x_notification_center.model.default_max;
}
}
});
/**
 * Returns true for bottom-* position strings.
 */
app.components.x_notification_center.model.bottom_position_QMARK_ = (function app$components$x_notification_center$model$bottom_position_QMARK_(pos){
var and__5140__auto__ = typeof pos === 'string';
if(and__5140__auto__){
return pos.startsWith("bottom");
} else {
return and__5140__auto__;
}
});
app.components.x_notification_center.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"count","count",2139924085),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"readonly","readonly",-1101398934),true], null)], null);
app.components.x_notification_center.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_notification_center.model.event_push,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"count","count",2139924085),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null),app.components.x_notification_center.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"text","text",-1790561697),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"count","count",2139924085),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null),app.components.x_notification_center.model.event_empty,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false], null)]);

//# sourceMappingURL=app.components.x_notification_center.model.js.map
