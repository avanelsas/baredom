goog.provide('app.components.x_spinner.model');
app.components.x_spinner.model.tag_name = "x-spinner";
app.components.x_spinner.model.attr_size = "size";
app.components.x_spinner.model.attr_variant = "variant";
app.components.x_spinner.model.attr_label = "label";
app.components.x_spinner.model.observed_attributes = [app.components.x_spinner.model.attr_size,app.components.x_spinner.model.attr_variant,app.components.x_spinner.model.attr_label];
app.components.x_spinner.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_spinner.model.attr_size], null),new cljs.core.Keyword(null,"variant","variant",-424354234),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_spinner.model.attr_variant], null),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_spinner.model.attr_label], null)], null);
app.components.x_spinner.model.allowed_sizes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["xs",null,"md",null,"lg",null,"xl",null,"sm",null], null), null);
app.components.x_spinner.model.allowed_variants = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["success",null,"warning",null,"primary",null,"danger",null,"default",null], null), null);
app.components.x_spinner.model.default_size = "md";
app.components.x_spinner.model.default_variant = "default";
app.components.x_spinner.model.default_label = "Loading";
/**
 * Return size string if valid, otherwise default-size.
 */
app.components.x_spinner.model.normalize_size = (function app$components$x_spinner$model$normalize_size(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_spinner.model.allowed_sizes,s)))){
return s;
} else {
return app.components.x_spinner.model.default_size;
}
});
/**
 * Return variant string if valid, otherwise default-variant.
 */
app.components.x_spinner.model.normalize_variant = (function app$components$x_spinner$model$normalize_variant(s){
if(((typeof s === 'string') && (cljs.core.contains_QMARK_(app.components.x_spinner.model.allowed_variants,s)))){
return s;
} else {
return app.components.x_spinner.model.default_variant;
}
});
/**
 * Return trimmed label string or default-label when absent/blank.
 */
app.components.x_spinner.model.normalize_label = (function app$components$x_spinner$model$normalize_label(s){
if(((typeof s === 'string') && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",s.trim())))){
return s.trim();
} else {
return app.components.x_spinner.model.default_label;
}
});
/**
 * Produce a stable view-model map from raw attribute strings.
 * 
 *   Input keys:
 *  :size    string | nil
 *  :variant string | nil
 *  :label   string | nil
 * 
 *   Output keys:
 *  :size    string  — valid size enum value
 *  :variant string  — valid variant enum value
 *  :label   string  — accessible label text
 */
app.components.x_spinner.model.derive_state = (function app$components$x_spinner$model$derive_state(p__23096){
var map__23097 = p__23096;
var map__23097__$1 = cljs.core.__destructure_map(map__23097);
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23097__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23097__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23097__$1,new cljs.core.Keyword(null,"label","label",1718410804));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_spinner.model.normalize_size(size),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_spinner.model.normalize_variant(variant),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_spinner.model.normalize_label(label)], null);
});

//# sourceMappingURL=app.components.x_spinner.model.js.map
