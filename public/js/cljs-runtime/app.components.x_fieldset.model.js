goog.provide('app.components.x_fieldset.model');
app.components.x_fieldset.model.tag_name = "x-fieldset";
app.components.x_fieldset.model.attr_legend = "legend";
app.components.x_fieldset.model.attr_disabled = "disabled";
app.components.x_fieldset.model.attr_aria_label = "aria-label";
app.components.x_fieldset.model.attr_aria_describedby = "aria-describedby";
app.components.x_fieldset.model.observed_attributes = [app.components.x_fieldset.model.attr_legend,app.components.x_fieldset.model.attr_disabled,app.components.x_fieldset.model.attr_aria_label,app.components.x_fieldset.model.attr_aria_describedby];
app.components.x_fieldset.model.property_api = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"legend","legend",-1027192245),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_fieldset.model.attr_legend], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_fieldset.model.attr_disabled], null)], null);
/**
 * Returns true if attribute is present and not equal to "false".
 */
app.components.x_fieldset.model.parse_bool_attr = (function app$components$x_fieldset$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_fieldset.model.normalize = (function app$components$x_fieldset$model$normalize(p__22594){
var map__22595 = p__22594;
var map__22595__$1 = cljs.core.__destructure_map(map__22595);
var legend_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22595__$1,new cljs.core.Keyword(null,"legend-raw","legend-raw",907427195));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22595__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22595__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
var aria_describedby_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22595__$1,new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860));
var legend = (function (){var or__5142__auto__ = legend_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var disabled_QMARK_ = cljs.core.boolean$(disabled_present_QMARK_);
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"legend","legend",-1027192245),legend,new cljs.core.Keyword(null,"legend-visible?","legend-visible?",-281107501),cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(legend,""),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),disabled_QMARK_,new cljs.core.Keyword(null,"aria-label","aria-label",455891514),aria_label_raw,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471),aria_describedby_raw], null);
});

//# sourceMappingURL=app.components.x_fieldset.model.js.map
