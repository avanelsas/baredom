goog.provide('app.components.x_form.model');
app.components.x_form.model.tag_name = "x-form";
app.components.x_form.model.attr_loading = "loading";
app.components.x_form.model.attr_novalidate = "novalidate";
app.components.x_form.model.attr_autocomplete = "autocomplete";
app.components.x_form.model.event_submit = "x-form-submit";
app.components.x_form.model.event_reset = "x-form-reset";
app.components.x_form.model.observed_attributes = [app.components.x_form.model.attr_loading,app.components.x_form.model.attr_novalidate,app.components.x_form.model.attr_autocomplete];
app.components.x_form.model.allowed_autocomplete = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["off",null,"on",null], null), null);
app.components.x_form.model.normalize_autocomplete = (function app$components$x_form$model$normalize_autocomplete(raw){
if(cljs.core.contains_QMARK_(app.components.x_form.model.allowed_autocomplete,raw)){
return raw;
} else {
return "on";
}
});
/**
 * Returns true when attribute is present and not literally "false".
 */
app.components.x_form.model.bool_attr_QMARK_ = (function app$components$x_form$model$bool_attr_QMARK_(v){
return (((!((v == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(v,"false")));
});
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_form.model.normalize = (function app$components$x_form$model$normalize(p__22692){
var map__22693 = p__22692;
var map__22693__$1 = cljs.core.__destructure_map(map__22693);
var loading_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22693__$1,new cljs.core.Keyword(null,"loading-raw","loading-raw",-1707143099));
var novalidate_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22693__$1,new cljs.core.Keyword(null,"novalidate-raw","novalidate-raw",1783236714));
var autocomplete_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22693__$1,new cljs.core.Keyword(null,"autocomplete-raw","autocomplete-raw",1742330710));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"loading?","loading?",1905707049),app.components.x_form.model.bool_attr_QMARK_(loading_raw),new cljs.core.Keyword(null,"novalidate?","novalidate?",491529885),(!((novalidate_raw == null))),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),app.components.x_form.model.normalize_autocomplete(autocomplete_raw)], null);
});
app.components.x_form.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"loading","loading",-737050189),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_form.model.attr_loading], null),new cljs.core.Keyword(null,"novalidate","novalidate",-572717086),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_form.model.attr_novalidate], null),new cljs.core.Keyword(null,"autocomplete","autocomplete",1041133913),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_form.model.attr_autocomplete], null)], null);
app.components.x_form.model.event_schema = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"submit","submit",-49315317),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_form.model.event_submit,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"values","values",372645556),null], null), null)], null),new cljs.core.Keyword(null,"reset","reset",-800929946),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_form.model.event_reset,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentHashSet.EMPTY], null)], null);

//# sourceMappingURL=app.components.x_form.model.js.map
