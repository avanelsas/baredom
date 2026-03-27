goog.provide('app.components.x_stepper.model');
goog.scope(function(){
  app.components.x_stepper.model.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_stepper.model.tag_name = "x-stepper";
app.components.x_stepper.model.attr_steps = "steps";
app.components.x_stepper.model.attr_current = "current";
app.components.x_stepper.model.attr_orientation = "orientation";
app.components.x_stepper.model.attr_size = "size";
app.components.x_stepper.model.attr_disabled = "disabled";
app.components.x_stepper.model.observed_attributes = [app.components.x_stepper.model.attr_steps,app.components.x_stepper.model.attr_current,app.components.x_stepper.model.attr_orientation,app.components.x_stepper.model.attr_size,app.components.x_stepper.model.attr_disabled];
app.components.x_stepper.model.event_change = "x-stepper-change";
app.components.x_stepper.model.property_api = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"steps","steps",-128433302),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"current","current",-1088038603),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"orientation","orientation",623557579),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"size","size",1098693007),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"string","string",-349010059,null)], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null);
app.components.x_stepper.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_stepper.model.event_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"from","from",1815293044),new cljs.core.Symbol(null,"number","number",-1084057331,null),new cljs.core.Keyword(null,"to","to",192099007),new cljs.core.Symbol(null,"number","number",-1084057331,null)], null),new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true], null)]);
app.components.x_stepper.model.default_orientation = new cljs.core.Keyword(null,"horizontal","horizontal",2062109475);
app.components.x_stepper.model.orientation__GT_kw = new cljs.core.PersistentArrayMap(null, 2, ["horizontal",new cljs.core.Keyword(null,"horizontal","horizontal",2062109475),"vertical",new cljs.core.Keyword(null,"vertical","vertical",718696748)], null);
app.components.x_stepper.model.kw__GT_orientation = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"horizontal","horizontal",2062109475),"horizontal",new cljs.core.Keyword(null,"vertical","vertical",718696748),"vertical"], null);
app.components.x_stepper.model.parse_orientation = (function app$components$x_stepper$model$parse_orientation(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_stepper.model.orientation__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_stepper.model.default_orientation;
}
});
app.components.x_stepper.model.orientation__GT_attr = (function app$components$x_stepper$model$orientation__GT_attr(o){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_stepper.model.kw__GT_orientation,o);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "horizontal";
}
});
app.components.x_stepper.model.default_size = new cljs.core.Keyword(null,"md","md",707286655);
app.components.x_stepper.model.size__GT_kw = new cljs.core.PersistentArrayMap(null, 3, ["sm",new cljs.core.Keyword(null,"sm","sm",-1402575065),"md",new cljs.core.Keyword(null,"md","md",707286655),"lg",new cljs.core.Keyword(null,"lg","lg",-80787836)], null);
app.components.x_stepper.model.kw__GT_size = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"sm","sm",-1402575065),"sm",new cljs.core.Keyword(null,"md","md",707286655),"md",new cljs.core.Keyword(null,"lg","lg",-80787836),"lg"], null);
app.components.x_stepper.model.parse_size = (function app$components$x_stepper$model$parse_size(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_stepper.model.size__GT_kw,((typeof s === 'string')?s.toLowerCase():null));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_stepper.model.default_size;
}
});
app.components.x_stepper.model.size__GT_attr = (function app$components$x_stepper$model$size__GT_attr(s){
var or__5142__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(app.components.x_stepper.model.kw__GT_size,s);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "md";
}
});
/**
 * Parse current-step attribute to a non-negative integer, defaulting to 0.
 */
app.components.x_stepper.model.parse_current = (function app$components$x_stepper$model$parse_current(s){
if(typeof s === 'string'){
var n = parseInt(s.trim(),(10));
if(((typeof n === 'number') && (((cljs.core.not(isNaN(n))) && ((n >= (0))))))){
return n;
} else {
return (0);
}
} else {
return (0);
}
});
/**
 * Clamp current to [0, steps-count-1], or 0 when there are no steps.
 */
app.components.x_stepper.model.clamp_current = (function app$components$x_stepper$model$clamp_current(current_idx,steps_count){
if((steps_count === (0))){
return (0);
} else {
return cljs.core.max.cljs$core$IFn$_invoke$arity$2((0),cljs.core.min.cljs$core$IFn$_invoke$arity$2(current_idx,(steps_count - (1))));
}
});
/**
 * Parse the steps attribute value into a vector of {:label string :description string|nil}.
 * 
 *   Accepted formats:
 *  - Positive integer string  "3"  → [{:label "Step 1"} {:label "Step 2"} {:label "Step 3"}]
 *  - JSON array string        "[{\"label\":\"One\"}]"
 *   Unrecognised / nil → [].
 */
app.components.x_stepper.model.parse_steps = (function app$components$x_stepper$model$parse_steps(s){
if(typeof s === 'string'){
var trimmed = s.trim();
if(cljs.core.truth_(cljs.core.re_matches(/^\d+$/,trimmed))){
var n = parseInt(trimmed,(10));
if((((n > (0))) && ((n <= (200))))){
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (i){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),(""+"Step "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(i)),new cljs.core.Keyword(null,"description","description",-1428560544),null], null);
}),cljs.core.range.cljs$core$IFn$_invoke$arity$2((1),(n + (1))));
} else {
return cljs.core.PersistentVector.EMPTY;
}
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",trimmed);
if(and__5140__auto__){
return trimmed.startsWith("[");
} else {
return and__5140__auto__;
}
})())){
try{var parsed = JSON.parse(trimmed);
if(cljs.core.truth_(Array.isArray(parsed))){
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2((function (item){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),(function (){var l = app.components.x_stepper.model.goog$module$goog$object.get(item,"label");
if(typeof l === 'string'){
return l;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"description","description",-1428560544),(function (){var d = app.components.x_stepper.model.goog$module$goog$object.get(item,"description");
if(typeof d === 'string'){
return d;
} else {
return null;
}
})()], null);
}),cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(parsed));
} else {
return cljs.core.PersistentVector.EMPTY;
}
}catch (e23114){var _ = e23114;
return cljs.core.PersistentVector.EMPTY;
}} else {
return cljs.core.PersistentVector.EMPTY;

}
}
} else {
return cljs.core.PersistentVector.EMPTY;
}
});
/**
 * Return :complete, :current, or :upcoming for the step at index i.
 */
app.components.x_stepper.model.step_state = (function app$components$x_stepper$model$step_state(i,current_idx){
if((i < current_idx)){
return new cljs.core.Keyword(null,"complete","complete",-500388775);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(i,current_idx)){
return new cljs.core.Keyword(null,"current","current",-1088038603);
} else {
return new cljs.core.Keyword(null,"upcoming","upcoming",1873315471);

}
}
});
app.components.x_stepper.model.state__GT_attr = (function app$components$x_stepper$model$state__GT_attr(state){
var G__23115 = state;
var G__23115__$1 = (((G__23115 instanceof cljs.core.Keyword))?G__23115.fqn:null);
switch (G__23115__$1) {
case "complete":
return "complete";

break;
case "current":
return "current";

break;
case "upcoming":
return "upcoming";

break;
default:
throw (new Error((""+"No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__23115__$1))));

}
});
/**
 * Normalize raw attribute inputs into a stable view-model map.
 * 
 *   Input keys:
 *  :steps-raw       string | nil
 *  :current-raw     string | nil
 *  :orientation-raw string | nil
 *  :size-raw        string | nil
 *  :disabled?       boolean
 * 
 *   Output keys:
 *  :steps       [{:label string :description string|nil} ...]
 *  :current     int  (clamped)
 *  :orientation :horizontal | :vertical
 *  :size        :sm | :md | :lg
 *  :disabled?   boolean
 */
app.components.x_stepper.model.normalize = (function app$components$x_stepper$model$normalize(p__23116){
var map__23117 = p__23116;
var map__23117__$1 = cljs.core.__destructure_map(map__23117);
var steps_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23117__$1,new cljs.core.Keyword(null,"steps-raw","steps-raw",1741812533));
var current_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23117__$1,new cljs.core.Keyword(null,"current-raw","current-raw",-1440665509));
var orientation_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23117__$1,new cljs.core.Keyword(null,"orientation-raw","orientation-raw",-471053928));
var size_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23117__$1,new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423));
var disabled_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23117__$1,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181));
var steps = app.components.x_stepper.model.parse_steps(steps_raw);
var n = cljs.core.count(steps);
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"steps","steps",-128433302),steps,new cljs.core.Keyword(null,"current","current",-1088038603),app.components.x_stepper.model.clamp_current(app.components.x_stepper.model.parse_current(current_raw),n),new cljs.core.Keyword(null,"orientation","orientation",623557579),app.components.x_stepper.model.parse_orientation(orientation_raw),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_stepper.model.parse_size(size_raw),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),cljs.core.boolean$(disabled_QMARK_)], null);
});
/**
 * Build event detail map for an x-stepper-change event.
 */
app.components.x_stepper.model.change_detail = (function app$components$x_stepper$model$change_detail(from,to){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"from","from",1815293044),from,new cljs.core.Keyword(null,"to","to",192099007),to], null);
});

//# sourceMappingURL=app.components.x_stepper.model.js.map
