goog.provide('app.components.x_spacer.model');
app.components.x_spacer.model.tag_name = "x-spacer";
app.components.x_spacer.model.attr_size = "size";
app.components.x_spacer.model.attr_axis = "axis";
app.components.x_spacer.model.attr_grow = "grow";
app.components.x_spacer.model.observed_attributes = [app.components.x_spacer.model.attr_size,app.components.x_spacer.model.attr_axis,app.components.x_spacer.model.attr_grow];
app.components.x_spacer.model.valid_axes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["vertical",null,"horizontal",null], null), null);
app.components.x_spacer.model.default_axis = "vertical";
app.components.x_spacer.model.default_size = "1rem";
/**
 * Normalise axis attribute. Unknown / nil values fall back to 'vertical'.
 */
app.components.x_spacer.model.parse_axis = (function app$components$x_spacer$model$parse_axis(s){
var v = ((typeof s === 'string')?s.trim().toLowerCase():null);
if(cljs.core.contains_QMARK_(app.components.x_spacer.model.valid_axes,v)){
return v;
} else {
return app.components.x_spacer.model.default_axis;
}
});
/**
 * Returns true when the grow attribute is present and not explicitly 'false'.
 *   grow is false when absent (nil), true when present (empty string or any value
 *   other than 'false').
 */
app.components.x_spacer.model.parse_grow = (function app$components$x_spacer$model$parse_grow(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(s)).trim().toLowerCase(),"false")));
});
/**
 * Returns the size value, or the default when nil/blank.
 */
app.components.x_spacer.model.parse_size = (function app$components$x_spacer$model$parse_size(s){
if(((typeof s === 'string') && ((s.trim().length > (0))))){
return s.trim();
} else {
return app.components.x_spacer.model.default_size;
}
});
/**
 * Normalise raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :size-raw   string | nil
 *  :axis-raw   string | nil
 *  :grow-raw   string | nil
 * 
 *   Output keys:
 *  :size   string   — CSS length value
 *  :axis   string   — 'vertical' | 'horizontal'
 *  :grow?  boolean  — true when grow attribute is present
 */
app.components.x_spacer.model.normalize = (function app$components$x_spacer$model$normalize(p__23093){
var map__23094 = p__23093;
var map__23094__$1 = cljs.core.__destructure_map(map__23094);
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23094__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var axis_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23094__$1,new cljs.core.Keyword(null,"axis-raw","axis-raw",291213231));
var grow_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23094__$1,new cljs.core.Keyword(null,"grow-raw","grow-raw",441172903));
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_spacer.model.parse_size(size_raw),new cljs.core.Keyword(null,"axis","axis",-1215390822),app.components.x_spacer.model.parse_axis(axis_raw),new cljs.core.Keyword(null,"grow?","grow?",2124334580),app.components.x_spacer.model.parse_grow(grow_raw)], null);
});
app.components.x_spacer.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"axis","axis",-1215390822),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"grow","grow",-524118895),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);

//# sourceMappingURL=app.components.x_spacer.model.js.map
