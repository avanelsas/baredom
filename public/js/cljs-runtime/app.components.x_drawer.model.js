goog.provide('app.components.x_drawer.model');
app.components.x_drawer.model.tag_name = "x-drawer";
app.components.x_drawer.model.attr_open = "open";
app.components.x_drawer.model.attr_placement = "placement";
app.components.x_drawer.model.attr_label = "label";
app.components.x_drawer.model.event_toggle = "x-drawer-toggle";
app.components.x_drawer.model.event_dismiss = "x-drawer-dismiss";
app.components.x_drawer.model.part_backdrop = "backdrop";
app.components.x_drawer.model.part_panel = "panel";
app.components.x_drawer.model.part_header = "header";
app.components.x_drawer.model.part_body = "body";
app.components.x_drawer.model.part_footer = "footer";
app.components.x_drawer.model.reason_escape = "escape";
app.components.x_drawer.model.reason_backdrop = "backdrop";
app.components.x_drawer.model.default_label = "Drawer";
app.components.x_drawer.model.default_placement = "right";
app.components.x_drawer.model.allowed_placements = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["right",null,"top",null,"bottom",null,"left",null], null), null);
app.components.x_drawer.model.observed_attributes = ["open","placement","label"];
/**
 * Normalize raw placement attribute value. Falls back to default-placement.
 */
app.components.x_drawer.model.normalize_placement = (function app$components$x_drawer$model$normalize_placement(raw){
if(((typeof raw === 'string') && (cljs.core.contains_QMARK_(app.components.x_drawer.model.allowed_placements,raw)))){
return raw;
} else {
return app.components.x_drawer.model.default_placement;
}
});
/**
 * Normalize raw label attribute value. Falls back to default-label.
 */
app.components.x_drawer.model.normalize_label = (function app$components$x_drawer$model$normalize_label(raw){
if(((typeof raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(raw,"")))){
return raw;
} else {
return app.components.x_drawer.model.default_label;
}
});
/**
 * Produce a normalized view-model map from raw attribute values.
 */
app.components.x_drawer.model.normalize = (function app$components$x_drawer$model$normalize(p__22514){
var map__22515 = p__22514;
var map__22515__$1 = cljs.core.__destructure_map(map__22515);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22515__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var placement_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22515__$1,new cljs.core.Keyword(null,"placement-raw","placement-raw",-1500957198));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22515__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open?","open?",1238443125),cljs.core.boolean$(open_present_QMARK_),new cljs.core.Keyword(null,"placement","placement",768366651),app.components.x_drawer.model.normalize_placement(placement_raw),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_drawer.model.normalize_label(label_raw)], null);
});
/**
 * Build the x-drawer-toggle CustomEvent detail.
 */
app.components.x_drawer.model.toggle_event_detail = (function app$components$x_drawer$model$toggle_event_detail(open){
return ({"open": open});
});
/**
 * Build the x-drawer-dismiss CustomEvent detail.
 */
app.components.x_drawer.model.dismiss_event_detail = (function app$components$x_drawer$model$dismiss_event_detail(reason){
return ({"reason": reason});
});
app.components.x_drawer.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_drawer.model.attr_open], null),new cljs.core.Keyword(null,"placement","placement",768366651),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_drawer.model.attr_placement], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_drawer.model.attr_label], null)], null);
app.components.x_drawer.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_drawer.model.event_toggle,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null),app.components.x_drawer.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);

//# sourceMappingURL=app.components.x_drawer.model.js.map
