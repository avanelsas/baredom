goog.provide('app.components.x_file_download.model');
app.components.x_file_download.model.tag_name = "x-file-download";
app.components.x_file_download.model.attr_href = "href";
app.components.x_file_download.model.attr_filename = "filename";
app.components.x_file_download.model.attr_disabled = "disabled";
app.components.x_file_download.model.attr_aria_label = "aria-label";
app.components.x_file_download.model.event_click = "x-file-download-click";
app.components.x_file_download.model.observed_attributes = [app.components.x_file_download.model.attr_href,app.components.x_file_download.model.attr_filename,app.components.x_file_download.model.attr_disabled,app.components.x_file_download.model.attr_aria_label];
app.components.x_file_download.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"href","href",-793805698),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_file_download.model.attr_href], null),new cljs.core.Keyword(null,"filename","filename",-1428840783),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_file_download.model.attr_filename], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_file_download.model.attr_disabled], null)], null);
app.components.x_file_download.model.event_schema = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"click","click",1912301393),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),app.components.x_file_download.model.event_click,new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"filename","filename",-1428840783),null,new cljs.core.Keyword(null,"href","href",-793805698),null], null), null)], null)], null);
/**
 * Derives a complete view-model map from raw attribute values.
 */
app.components.x_file_download.model.normalize = (function app$components$x_file_download$model$normalize(p__22644){
var map__22645 = p__22644;
var map__22645__$1 = cljs.core.__destructure_map(map__22645);
var href_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22645__$1,new cljs.core.Keyword(null,"href-raw","href-raw",-1680949453));
var filename_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22645__$1,new cljs.core.Keyword(null,"filename-raw","filename-raw",-706184256));
var disabled_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22645__$1,new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496));
var aria_label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22645__$1,new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103));
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"href","href",-793805698),(function (){var or__5142__auto__ = href_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"filename","filename",-1428840783),(function (){var or__5142__auto__ = filename_raw;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_present_QMARK_),new cljs.core.Keyword(null,"aria-label","aria-label",455891514),aria_label_raw], null);
});

//# sourceMappingURL=app.components.x_file_download.model.js.map
