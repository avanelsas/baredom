goog.provide('app.components.x_slider.model');
app.components.x_slider.model.tag_name = "x-slider";
app.components.x_slider.model.attr_value = "value";
app.components.x_slider.model.attr_min = "min";
app.components.x_slider.model.attr_max = "max";
app.components.x_slider.model.attr_step = "step";
app.components.x_slider.model.attr_disabled = "disabled";
app.components.x_slider.model.attr_readonly = "readonly";
app.components.x_slider.model.attr_name = "name";
app.components.x_slider.model.attr_label = "label";
app.components.x_slider.model.attr_show_value = "show-value";
app.components.x_slider.model.attr_size = "size";
app.components.x_slider.model.attr_aria_label = "aria-label";
app.components.x_slider.model.attr_aria_labelledby = "aria-labelledby";
app.components.x_slider.model.attr_aria_describedby = "aria-describedby";
app.components.x_slider.model.event_input = "x-slider-input";
app.components.x_slider.model.event_change = "x-slider-change";
app.components.x_slider.model.default_value = (0);
app.components.x_slider.model.default_min = (0);
app.components.x_slider.model.default_max = (100);
app.components.x_slider.model.default_step = "1";
app.components.x_slider.model.default_size = "md";
app.components.x_slider.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["md",null,"lg",null,"sm",null], null), null);
app.components.x_slider.model.observed_attributes = [app.components.x_slider.model.attr_value,app.components.x_slider.model.attr_min,app.components.x_slider.model.attr_max,app.components.x_slider.model.attr_step,app.components.x_slider.model.attr_disabled,app.components.x_slider.model.attr_readonly,app.components.x_slider.model.attr_name,app.components.x_slider.model.attr_label,app.components.x_slider.model.attr_show_value,app.components.x_slider.model.attr_size,app.components.x_slider.model.attr_aria_label,app.components.x_slider.model.attr_aria_labelledby,app.components.x_slider.model.attr_aria_describedby];
app.components.x_slider.model.property_api = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.Keyword(null,"showValue","showValue",-1618193945),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"readOnly","readOnly",-1749118317),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"step","step",1288888124)],[new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_min], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_disabled], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_show_value], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_name], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_value], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_size], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_readonly], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_max], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_label], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_slider.model.attr_step], null)]);
app.components.x_slider.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_slider.model.event_input,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null),app.components.x_slider.model.event_change,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null)], null)]);
app.components.x_slider.model.normalize_number = (function app$components$x_slider$model$normalize_number(s,default_val){
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
app.components.x_slider.model.normalize_min = (function app$components$x_slider$model$normalize_min(s){
return app.components.x_slider.model.normalize_number(s,app.components.x_slider.model.default_min);
});
app.components.x_slider.model.normalize_max = (function app$components$x_slider$model$normalize_max(s){
return app.components.x_slider.model.normalize_number(s,app.components.x_slider.model.default_max);
});
app.components.x_slider.model.normalize_value = (function app$components$x_slider$model$normalize_value(s,min_val,max_val){
var v = app.components.x_slider.model.normalize_number(s,app.components.x_slider.model.default_value);
return cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2(v,min_val),max_val);
});
app.components.x_slider.model.normalize_step = (function app$components$x_slider$model$normalize_step(s){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(s,"any")){
return "any";
} else {
var n = ((typeof s === 'string')?parseFloat(s):NaN);
if(((cljs.core.not(isNaN(n))) && ((n > (0))))){
return (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(n));
} else {
return app.components.x_slider.model.default_step;
}
}
});
app.components.x_slider.model.normalize_size = (function app$components$x_slider$model$normalize_size(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_slider.model.allowed_sizes,s)))){
return s;
} else {
return app.components.x_slider.model.default_size;
}
});
app.components.x_slider.model.fill_percent = (function app$components$x_slider$model$fill_percent(value,min_val,max_val){
if((max_val > min_val)){
return cljs.core.min.cljs$core$IFn$_invoke$arity$2(cljs.core.max.cljs$core$IFn$_invoke$arity$2((100.0 * ((value - min_val) / (max_val - min_val))),0.0),100.0);
} else {
return 0.0;
}
});
app.components.x_slider.model.derive_state = (function app$components$x_slider$model$derive_state(p__23064){
var map__23065 = p__23064;
var map__23065__$1 = cljs.core.__destructure_map(map__23065);
var aria_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var step = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"step","step",1288888124));
var min = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"min","min",444991522));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var show_value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"show-value","show-value",-1560941240));
var value = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"value","value",305978217));
var name = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var readonly = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"readonly","readonly",-1101398934));
var aria_labelledby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"max","max",61366548));
var aria_describedby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23065__$1,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471));
var norm_min = app.components.x_slider.model.normalize_min(min);
var norm_max = app.components.x_slider.model.normalize_max(max);
var norm_value = app.components.x_slider.model.normalize_value(value,norm_min,norm_max);
var norm_step = app.components.x_slider.model.normalize_step(step);
var norm_size = app.components.x_slider.model.normalize_size(size);
var disabled_QMARK_ = cljs.core.boolean$(disabled);
var readonly_QMARK_ = cljs.core.boolean$(readonly);
var show_val_QMARK_ = cljs.core.boolean$(show_value);
var pct = app.components.x_slider.model.fill_percent(norm_value,norm_min,norm_max);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"min","min",444991522),new cljs.core.Keyword(null,"readonly?","readonly?",988057827),new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"aria-labelledby","aria-labelledby",1817118667),new cljs.core.Keyword(null,"show-value?","show-value?",-1960833108),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.Keyword(null,"max","max",61366548),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),new cljs.core.Keyword(null,"step","step",1288888124),new cljs.core.Keyword(null,"fill-percent","fill-percent",-1813848228)],[norm_min,readonly_QMARK_,((((typeof name === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",name))))?name:null),norm_value,((((typeof aria_labelledby === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",aria_labelledby))))?aria_labelledby:null),show_val_QMARK_,norm_size,norm_max,((((typeof label === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",label))))?label:null),((((typeof aria_describedby === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",aria_describedby))))?aria_describedby:null),((((typeof aria_label === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",aria_label))))?aria_label:null),disabled_QMARK_,norm_step,pct]);
});

//# sourceMappingURL=app.components.x_slider.model.js.map
