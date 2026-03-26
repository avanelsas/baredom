goog.provide('app.components.x_progress_circle.model');
app.components.x_progress_circle.model.tag_name = "x-progress-circle";
app.components.x_progress_circle.model.attr_value = "value";
app.components.x_progress_circle.model.attr_max = "max";
app.components.x_progress_circle.model.attr_variant = "variant";
app.components.x_progress_circle.model.attr_size = "size";
app.components.x_progress_circle.model.attr_label = "label";
app.components.x_progress_circle.model.attr_show_value = "show-value";
app.components.x_progress_circle.model.attr_indeterminate = "indeterminate";
app.components.x_progress_circle.model.event_complete = "x-progress-circle-complete";
app.components.x_progress_circle.model.default_variant = "default";
app.components.x_progress_circle.model.default_size = "md";
app.components.x_progress_circle.model.default_value = (0);
app.components.x_progress_circle.model.default_max = (100);
app.components.x_progress_circle.model.allowed_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, ["success",null,"warning",null,"danger",null,"default",null], null), null);
app.components.x_progress_circle.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_progress_circle.model.observed_attributes = [app.components.x_progress_circle.model.attr_value,app.components.x_progress_circle.model.attr_max,app.components.x_progress_circle.model.attr_variant,app.components.x_progress_circle.model.attr_size,app.components.x_progress_circle.model.attr_label,app.components.x_progress_circle.model.attr_show_value,app.components.x_progress_circle.model.attr_indeterminate];
app.components.x_progress_circle.model.property_api = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_progress_circle.model.attr_value], null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_progress_circle.model.attr_max], null),new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_progress_circle.model.attr_indeterminate], null),new cljs.core.Keyword(null,"showValue","showValue",-1618193945),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_progress_circle.model.attr_show_value], null)], null);
app.components.x_progress_circle.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_progress_circle.model.event_complete,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null)]);
app.components.x_progress_circle.model.normalize_number = (function app$components$x_progress_circle$model$normalize_number(s,default_val){
if(typeof s === 'string'){
var n = parseFloat(s);
if(cljs.core.truth_(isNaN(n))){
return default_val;
} else {
return n;
}
} else {
return default_val;
}
});
app.components.x_progress_circle.model.normalize_value = (function app$components$x_progress_circle$model$normalize_value(value_str,max_val){
var v = app.components.x_progress_circle.model.normalize_number(value_str,app.components.x_progress_circle.model.default_value);
return cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2(v,(0)),max_val);
});
app.components.x_progress_circle.model.normalize_max = (function app$components$x_progress_circle$model$normalize_max(max_str){
var m = app.components.x_progress_circle.model.normalize_number(max_str,app.components.x_progress_circle.model.default_max);
if((m > (0))){
return m;
} else {
return app.components.x_progress_circle.model.default_max;
}
});
app.components.x_progress_circle.model.normalize_variant = (function app$components$x_progress_circle$model$normalize_variant(value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(app.components.x_progress_circle.model.allowed_variants,value)))){
return value;
} else {
return app.components.x_progress_circle.model.default_variant;
}
});
app.components.x_progress_circle.model.normalize_size = (function app$components$x_progress_circle$model$normalize_size(value){
if(((typeof value === 'string') && (cljs.core.contains_QMARK_(app.components.x_progress_circle.model.allowed_sizes,value)))){
return value;
} else {
return app.components.x_progress_circle.model.default_size;
}
});
app.components.x_progress_circle.model.derive_state = (function app$components$x_progress_circle$model$derive_state(p__22979){
var map__22980 = p__22979;
var map__22980__$1 = cljs.core.__destructure_map(map__22980);
var max_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"max","max",61366548));
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"value","value",305978217));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var show_value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"show-value","show-value",-1560941240));
var indeterminate = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22980__$1,new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976));
var norm_max = app.components.x_progress_circle.model.normalize_max(max_attr);
var norm_value = app.components.x_progress_circle.model.normalize_value(value,norm_max);
var norm_variant = app.components.x_progress_circle.model.normalize_variant(variant);
var norm_size = app.components.x_progress_circle.model.normalize_size(size);
var indet_QMARK_ = cljs.core.boolean$(indeterminate);
var show_val_QMARK_ = cljs.core.boolean$(show_value);
var percent = ((indet_QMARK_)?(50):cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2((100.0 * (norm_value / norm_max)),(0)),(100)));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.Keyword(null,"show-value","show-value",-1560941240),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"indeterminate","indeterminate",-513040976),new cljs.core.Keyword(null,"aria-valuetext","aria-valuetext",-1020629328),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"percent","percent",2031453817)],[norm_variant,show_val_QMARK_,norm_value,norm_size,indet_QMARK_,((indet_QMARK_)?"Loading\u2026":(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(Math.round(percent))+"%")),norm_max,((((typeof label === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",label))))?label:null),percent]);
});

//# sourceMappingURL=app.components.x_progress_circle.model.js.map
