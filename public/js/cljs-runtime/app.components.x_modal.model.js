goog.provide('app.components.x_modal.model');
app.components.x_modal.model.tag_name = "x-modal";
app.components.x_modal.model.attr_open = "open";
app.components.x_modal.model.attr_size = "size";
app.components.x_modal.model.attr_label = "label";
app.components.x_modal.model.event_toggle = "x-modal-toggle";
app.components.x_modal.model.event_dismiss = "x-modal-dismiss";
app.components.x_modal.model.part_backdrop = "backdrop";
app.components.x_modal.model.part_dialog = "dialog";
app.components.x_modal.model.part_header = "header";
app.components.x_modal.model.part_body = "body";
app.components.x_modal.model.part_footer = "footer";
app.components.x_modal.model.reason_escape = "escape";
app.components.x_modal.model.reason_backdrop = "backdrop";
app.components.x_modal.model.default_label = "Modal";
app.components.x_modal.model.default_size = "md";
app.components.x_modal.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["md",null,"lg",null,"full",null,"xl",null,"sm",null], null), null);
app.components.x_modal.model.observed_attributes = ["open","size","label"];
/**
 * Normalize raw size attribute value. Falls back to default-size.
 */
app.components.x_modal.model.normalize_size = (function app$components$x_modal$model$normalize_size(raw){
if(((typeof raw === 'string') && (cljs.core.contains_QMARK_(app.components.x_modal.model.allowed_sizes,raw)))){
return raw;
} else {
return app.components.x_modal.model.default_size;
}
});
/**
 * Normalize raw label attribute value. Falls back to default-label.
 */
app.components.x_modal.model.normalize_label = (function app$components$x_modal$model$normalize_label(raw){
if(((typeof raw === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(raw,"")))){
return raw;
} else {
return app.components.x_modal.model.default_label;
}
});
/**
 * Produce a normalized view-model map from raw attribute values.
 */
app.components.x_modal.model.normalize = (function app$components$x_modal$model$normalize(p__22794){
var map__22795 = p__22794;
var map__22795__$1 = cljs.core.__destructure_map(map__22795);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22795__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22795__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22795__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open?","open?",1238443125),cljs.core.boolean$(open_present_QMARK_),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_modal.model.normalize_size(size_raw),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_modal.model.normalize_label(label_raw)], null);
});
/**
 * Build the x-modal-toggle CustomEvent detail.
 */
app.components.x_modal.model.toggle_event_detail = (function app$components$x_modal$model$toggle_event_detail(open){
return ({"open": open});
});
/**
 * Build the x-modal-dismiss CustomEvent detail.
 */
app.components.x_modal.model.dismiss_event_detail = (function app$components$x_modal$model$dismiss_event_detail(reason){
return ({"reason": reason});
});
app.components.x_modal.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_modal.model.attr_open], null),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_modal.model.attr_size], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_modal.model.attr_label], null)], null);
app.components.x_modal.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_modal.model.event_toggle,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null),app.components.x_modal.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);

//# sourceMappingURL=app.components.x_modal.model.js.map
