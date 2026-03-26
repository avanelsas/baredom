goog.provide('app.components.x_command_palette.model');
app.components.x_command_palette.model.tag_name = "x-command-palette";
app.components.x_command_palette.model.attr_open = "open";
app.components.x_command_palette.model.attr_modal = "modal";
app.components.x_command_palette.model.attr_dismissible = "dismissible";
app.components.x_command_palette.model.attr_disabled = "disabled";
app.components.x_command_palette.model.attr_no_scrim = "no-scrim";
app.components.x_command_palette.model.attr_close_on_scrim = "close-on-scrim";
app.components.x_command_palette.model.attr_close_on_escape = "close-on-escape";
app.components.x_command_palette.model.attr_label = "label";
app.components.x_command_palette.model.attr_placeholder = "placeholder";
app.components.x_command_palette.model.attr_empty_text = "empty-text";
app.components.x_command_palette.model.attr_portal = "portal";
app.components.x_command_palette.model.event_open_request = "x-command-palette-open-request";
app.components.x_command_palette.model.event_open = "x-command-palette-open";
app.components.x_command_palette.model.event_close_request = "x-command-palette-close-request";
app.components.x_command_palette.model.event_close = "x-command-palette-close";
app.components.x_command_palette.model.event_select_request = "x-command-palette-select-request";
app.components.x_command_palette.model.event_select = "x-command-palette-select";
app.components.x_command_palette.model.event_query_change = "x-command-palette-query-change";
app.components.x_command_palette.model.default_placeholder = "Search\u2026";
app.components.x_command_palette.model.default_empty_text = "No results";
app.components.x_command_palette.model.observed_attributes = ["open","modal","dismissible","disabled","no-scrim","close-on-scrim","close-on-escape","label","placeholder","empty-text","portal"];
app.components.x_command_palette.model.property_api = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"items","items",1031954938),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"array","array",-440182315,null),new cljs.core.Keyword(null,"description","description",-1428560544),"Array of command items with shape {id, label, keywords?, group?, value?, icon?, disabled?}"], null),new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_command_palette.model.attr_open], null),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_command_palette.model.attr_disabled], null)], null);
app.components.x_command_palette.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_command_palette.model.event_open_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_command_palette.model.event_open,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_command_palette.model.event_close_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_command_palette.model.event_close,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),cljs.core.PersistentArrayMap.EMPTY], null),app.components.x_command_palette.model.event_select_request,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),true,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"item","item",249373802),new cljs.core.Symbol(null,"object","object",-1179821820,null)], null)], null),app.components.x_command_palette.model.event_select,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"item","item",249373802),new cljs.core.Symbol(null,"object","object",-1179821820,null)], null)], null),app.components.x_command_palette.model.event_query_change,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"cancelable","cancelable",2083112403),false,new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"query","query",-1288509510),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
/**
 * Returns true if attr string is non-nil and not "false".
 */
app.components.x_command_palette.model.parse_bool_attr = (function app$components$x_command_palette$model$parse_bool_attr(s){
return (((!((s == null)))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false")));
});
/**
 * nil → true, "false" → false, anything else → true.
 */
app.components.x_command_palette.model.parse_bool_default_true = (function app$components$x_command_palette$model$parse_bool_default_true(s){
if((s == null)){
return true;
} else {
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(s,"false");
}
});
/**
 * Trim and return nil if empty.
 */
app.components.x_command_palette.model.normalize_str = (function app$components$x_command_palette$model$normalize_str(s){
if(typeof s === 'string'){
var t = s.trim();
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(t,"")){
return t;
} else {
return null;
}
} else {
return null;
}
});
/**
 * Takes a JS array of item objects and returns a ClojureScript vector of
 *   normalized maps. Each item receives: :id :label :group :value :icon
 *   :disabled? :search-str.
 */
app.components.x_command_palette.model.normalize_items = (function app$components$x_command_palette$model$normalize_items(items){
if((((items == null)) || (cljs.core.not(cljs.core.array_QMARK_(items))))){
return cljs.core.PersistentVector.EMPTY;
} else {
var len = items.length;
var i = (0);
var acc = cljs.core.transient$(cljs.core.PersistentVector.EMPTY);
while(true){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(i,len)){
return cljs.core.persistent_BANG_(acc);
} else {
var raw = (items[i]);
var id = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = raw.id;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return i;
}
})()));
var label = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__5142__auto__ = raw.label;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()));
var group = app.components.x_command_palette.model.normalize_str(raw.group);
var value = raw.value;
var icon = raw.icon;
var disabled_QMARK_ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(true,raw.disabled);
var keywords = raw.keywords;
var kw_str = (cljs.core.truth_(cljs.core.array_QMARK_(keywords))?keywords.join(" "):((typeof keywords === 'string')?keywords:""));
var group_str = (function (){var or__5142__auto__ = group;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var value_str = ((typeof value === 'string')?value:"");
var search_str = (""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(label)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(kw_str)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(group_str)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(value_str)).toLowerCase();
var G__23589 = (i + (1));
var G__23590 = cljs.core.conj_BANG_.cljs$core$IFn$_invoke$arity$2(acc,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"id","id",-1388402092),id,new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"group","group",582596132),group,new cljs.core.Keyword(null,"value","value",305978217),value,new cljs.core.Keyword(null,"icon","icon",1679606541),icon,new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181),disabled_QMARK_,new cljs.core.Keyword(null,"search-str","search-str",-821246171),search_str], null));
i = G__23589;
acc = G__23590;
continue;
}
break;
}
}
});
/**
 * Filter items by query string. Each space-separated token must appear
 *   in the item's search-str. Returns {:visible [...] :groups #{...}}.
 */
app.components.x_command_palette.model.filter_items = (function app$components$x_command_palette$model$filter_items(items,query){
var q = ((typeof query === 'string')?query.trim():"");
var tokens = ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(q,""))?cljs.core.keep.cljs$core$IFn$_invoke$arity$2(app.components.x_command_palette.model.normalize_str,clojure.string.split.cljs$core$IFn$_invoke$arity$2(q,/\s+/)):null);
if(cljs.core.empty_QMARK_(tokens)){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"visible","visible",-1024216805),items,new cljs.core.Keyword(null,"groups","groups",-136896102),cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,cljs.core.keep.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"group","group",582596132),items))], null);
} else {
var visible = cljs.core.filterv((function (item){
return cljs.core.every_QMARK_((function (tok){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2((-1),new cljs.core.Keyword(null,"search-str","search-str",-821246171).cljs$core$IFn$_invoke$arity$1(item).indexOf(tok.toLowerCase()));
}),tokens);
}),items);
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"visible","visible",-1024216805),visible,new cljs.core.Keyword(null,"groups","groups",-136896102),cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,cljs.core.keep.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"group","group",582596132),visible))], null);
}
});
/**
 * Walk forward (or backward) from start-idx in visible, wrapping,
 *   returning the index of the next non-disabled item. Returns nil if all disabled.
 */
app.components.x_command_palette.model.find_next_enabled = (function app$components$x_command_palette$model$find_next_enabled(visible,start_idx,direction){
var n = cljs.core.count(visible);
if((n > (0))){
var i = (1);
while(true){
if((i <= n)){
var idx = cljs.core.mod((start_idx + (i * direction)),n);
var item = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(visible,idx,null);
if(cljs.core.truth_((function (){var and__5140__auto__ = item;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181).cljs$core$IFn$_invoke$arity$1(item));
} else {
return and__5140__auto__;
}
})())){
return idx;
} else {
var G__23591 = (i + (1));
i = G__23591;
continue;
}
} else {
return null;
}
break;
}
} else {
return null;
}
});
/**
 * Advance active-idx forward, wrapping, skipping disabled items.
 */
app.components.x_command_palette.model.next_active_idx = (function app$components$x_command_palette$model$next_active_idx(visible,active_idx){
var or__5142__auto__ = app.components.x_command_palette.model.find_next_enabled(visible,(function (){var or__5142__auto__ = active_idx;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (-1);
}
})(),(1));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
});
/**
 * Advance active-idx backward, wrapping, skipping disabled items.
 */
app.components.x_command_palette.model.prev_active_idx = (function app$components$x_command_palette$model$prev_active_idx(visible,active_idx){
var n = cljs.core.count(visible);
var or__5142__auto__ = app.components.x_command_palette.model.find_next_enabled(visible,(function (){var or__5142__auto__ = active_idx;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return n;
}
})(),(-1));
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return (0);
}
});
/**
 * Canonicalize raw attr strings to a stable model map.
 */
app.components.x_command_palette.model.normalize = (function app$components$x_command_palette$model$normalize(p__23587){
var map__23588 = p__23587;
var map__23588__$1 = cljs.core.__destructure_map(map__23588);
var open_present_QMARK_ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"open-present?","open-present?",965047899));
var placeholder_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"placeholder-raw","placeholder-raw",-986138657));
var label_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"label-raw","label-raw",-83844350));
var empty_text_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"empty-text-raw","empty-text-raw",1477406757));
var portal_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"portal-raw","portal-raw",2293766));
var close_on_scrim_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"close-on-scrim-raw","close-on-scrim-raw",159828234));
var disabled_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"disabled-raw","disabled-raw",498098381));
var close_on_escape_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"close-on-escape-raw","close-on-escape-raw",-1942646255));
var no_scrim_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"no-scrim-raw","no-scrim-raw",-1344365743));
var dismissible_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"dismissible-raw","dismissible-raw",947680658));
var modal_raw = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__23588__$1,new cljs.core.Keyword(null,"modal-raw","modal-raw",-263722282));
var modal_QMARK_ = app.components.x_command_palette.model.parse_bool_default_true(modal_raw);
var dismissible_QMARK_ = app.components.x_command_palette.model.parse_bool_default_true(dismissible_raw);
var no_scrim_QMARK_ = app.components.x_command_palette.model.parse_bool_attr(no_scrim_raw);
var scrim_QMARK_ = ((modal_QMARK_) && ((!(no_scrim_QMARK_))));
var close_on_scrim_QMARK_ = (((!((close_on_scrim_raw == null))))?app.components.x_command_palette.model.parse_bool_attr(close_on_scrim_raw):scrim_QMARK_);
var close_on_escape_QMARK_ = app.components.x_command_palette.model.parse_bool_default_true(close_on_escape_raw);
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"portal","portal",2002989957),new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),new cljs.core.Keyword(null,"dismissible?","dismissible?",1270419178),new cljs.core.Keyword(null,"empty-text","empty-text",1554384594),new cljs.core.Keyword(null,"close-on-scrim?","close-on-scrim?",-367113965),new cljs.core.Keyword(null,"close-on-escape?","close-on-escape?",-1559868909),new cljs.core.Keyword(null,"scrim?","scrim?",744210580),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"open?","open?",1238443125),new cljs.core.Keyword(null,"modal?","modal?",2146094679),new cljs.core.Keyword(null,"disabled?","disabled?",-1523234181)],[app.components.x_command_palette.model.normalize_str(portal_raw),(function (){var or__5142__auto__ = app.components.x_command_palette.model.normalize_str(placeholder_raw);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_command_palette.model.default_placeholder;
}
})(),dismissible_QMARK_,(function (){var or__5142__auto__ = app.components.x_command_palette.model.normalize_str(empty_text_raw);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return app.components.x_command_palette.model.default_empty_text;
}
})(),close_on_scrim_QMARK_,close_on_escape_QMARK_,scrim_QMARK_,app.components.x_command_palette.model.normalize_str(label_raw),cljs.core.boolean$(open_present_QMARK_),modal_QMARK_,app.components.x_command_palette.model.parse_bool_attr(disabled_raw)]);
});

//# sourceMappingURL=app.components.x_command_palette.model.js.map
