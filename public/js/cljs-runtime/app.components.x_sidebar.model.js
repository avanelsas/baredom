goog.provide('app.components.x_sidebar.model');
app.components.x_sidebar.model.tag_name = "x-sidebar";
app.components.x_sidebar.model.attr_open = "open";
app.components.x_sidebar.model.attr_collapsed = "collapsed";
app.components.x_sidebar.model.attr_placement = "placement";
app.components.x_sidebar.model.attr_variant = "variant";
app.components.x_sidebar.model.attr_breakpoint = "breakpoint";
app.components.x_sidebar.model.attr_label = "label";
app.components.x_sidebar.model.event_toggle = "toggle";
app.components.x_sidebar.model.event_dismiss = "dismiss";
app.components.x_sidebar.model.part_backdrop = "backdrop";
app.components.x_sidebar.model.part_sidebar = "sidebar";
app.components.x_sidebar.model.part_panel = "panel";
app.components.x_sidebar.model.placement_left = "left";
app.components.x_sidebar.model.placement_right = "right";
app.components.x_sidebar.model.variant_docked = "docked";
app.components.x_sidebar.model.variant_overlay = "overlay";
app.components.x_sidebar.model.variant_modal = "modal";
app.components.x_sidebar.model.reason_escape = "escape";
app.components.x_sidebar.model.reason_backdrop = "backdrop";
app.components.x_sidebar.model.observed_attributes = [app.components.x_sidebar.model.attr_open,app.components.x_sidebar.model.attr_collapsed,app.components.x_sidebar.model.attr_placement,app.components.x_sidebar.model.attr_variant,app.components.x_sidebar.model.attr_breakpoint,app.components.x_sidebar.model.attr_label];
app.components.x_sidebar.model.allowed_placement = cljs.core.PersistentHashSet.createAsIfByAssoc([app.components.x_sidebar.model.placement_right,app.components.x_sidebar.model.placement_left]);
app.components.x_sidebar.model.allowed_variant = cljs.core.PersistentHashSet.createAsIfByAssoc([app.components.x_sidebar.model.variant_overlay,app.components.x_sidebar.model.variant_docked,app.components.x_sidebar.model.variant_modal]);
app.components.x_sidebar.model.allowed_dismiss_reason = cljs.core.PersistentHashSet.createAsIfByAssoc([app.components.x_sidebar.model.reason_backdrop,app.components.x_sidebar.model.reason_escape]);
app.components.x_sidebar.model.defaults = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"placement","placement",768366651),app.components.x_sidebar.model.placement_left,new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_sidebar.model.variant_docked,new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440),(768),new cljs.core.Keyword(null,"label","label",1718410804),"Sidebar",new cljs.core.Keyword(null,"open","open",-1763596448),false,new cljs.core.Keyword(null,"collapsed","collapsed",-628494523),false], null);
app.components.x_sidebar.model.property_api = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_sidebar.model.attr_open], null),new cljs.core.Keyword(null,"collapsed","collapsed",-628494523),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),new cljs.core.Keyword(null,"reflects-attribute","reflects-attribute",-535418966),app.components.x_sidebar.model.attr_collapsed], null)], null);
app.components.x_sidebar.model.event_schema = cljs.core.PersistentArrayMap.createAsIfByAssoc([app.components.x_sidebar.model.event_toggle,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null)], null),app.components.x_sidebar.model.event_dismiss,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"detail","detail",-1545345025),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"reason","reason",-2070751759),new cljs.core.Symbol(null,"string","string",-349010059,null)], null)], null)]);
app.components.x_sidebar.model.trim_or_nil = (function app$components$x_sidebar$model$trim_or_nil(value){
var s = (((!((value == null))))?clojure.string.trim(value):null);
if(cljs.core.seq(s)){
return s;
} else {
return null;
}
});
app.components.x_sidebar.model.parse_number = (function app$components$x_sidebar$model$parse_number(value,fallback){
var raw = (function (){var G__20996 = value;
var G__20996__$1 = (((G__20996 == null))?null:(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__20996)));
if((G__20996__$1 == null)){
return null;
} else {
return clojure.string.trim(G__20996__$1);
}
})();
if(cljs.core.seq(raw)){
var n = Number(raw);
if(cljs.core.truth_(Number.isFinite(n))){
return n;
} else {
return fallback;
}
} else {
return fallback;
}
});
app.components.x_sidebar.model.normalize_placement = (function app$components$x_sidebar$model$normalize_placement(value){
if(cljs.core.contains_QMARK_(app.components.x_sidebar.model.allowed_placement,value)){
return value;
} else {
return new cljs.core.Keyword(null,"placement","placement",768366651).cljs$core$IFn$_invoke$arity$1(app.components.x_sidebar.model.defaults);
}
});
app.components.x_sidebar.model.normalize_variant = (function app$components$x_sidebar$model$normalize_variant(value){
if(cljs.core.contains_QMARK_(app.components.x_sidebar.model.allowed_variant,value)){
return value;
} else {
return new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(app.components.x_sidebar.model.defaults);
}
});
app.components.x_sidebar.model.normalize_breakpoint = (function app$components$x_sidebar$model$normalize_breakpoint(value){
return app.components.x_sidebar.model.parse_number(value,new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440).cljs$core$IFn$_invoke$arity$1(app.components.x_sidebar.model.defaults));
});
app.components.x_sidebar.model.normalize_label = (function app$components$x_sidebar$model$normalize_label(value){
var or__5142__auto__ = app.components.x_sidebar.model.trim_or_nil(value);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(app.components.x_sidebar.model.defaults);
}
});
app.components.x_sidebar.model.compute_open = (function app$components$x_sidebar$model$compute_open(p__21000){
var map__21002 = p__21000;
var map__21002__$1 = cljs.core.__destructure_map(map__21002);
var open_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21002__$1,new cljs.core.Keyword(null,"open-attr","open-attr",-1772289801));
return cljs.core.boolean$(open_attr);
});
app.components.x_sidebar.model.compute_state = (function app$components$x_sidebar$model$compute_state(p__21006){
var map__21007 = p__21006;
var map__21007__$1 = cljs.core.__destructure_map(map__21007);
var open_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"open-attr","open-attr",-1772289801));
var collapsed_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"collapsed-attr","collapsed-attr",-1535396832));
var placement_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"placement-attr","placement-attr",-1321967286));
var variant_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"variant-attr","variant-attr",-1143729279));
var breakpoint_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"breakpoint-attr","breakpoint-attr",-250275627));
var label_attr = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"label-attr","label-attr",-571749887));
var viewport_width = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"viewport-width","viewport-width",-1146235948));
var prefers_reduced_motion = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21007__$1,new cljs.core.Keyword(null,"prefers-reduced-motion","prefers-reduced-motion",-2023150975));
var placement = app.components.x_sidebar.model.normalize_placement(placement_attr);
var declared_variant = app.components.x_sidebar.model.normalize_variant(variant_attr);
var breakpoint = app.components.x_sidebar.model.normalize_breakpoint(breakpoint_attr);
var label = app.components.x_sidebar.model.normalize_label(label_attr);
var open = app.components.x_sidebar.model.compute_open(new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open-attr","open-attr",-1772289801),open_attr], null));
var collapsed_requested = cljs.core.boolean$(collapsed_attr);
var effective_variant = (((viewport_width < breakpoint))?app.components.x_sidebar.model.variant_modal:declared_variant);
var is_modal = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(effective_variant,app.components.x_sidebar.model.variant_modal);
var is_overlay = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(effective_variant,app.components.x_sidebar.model.variant_overlay);
var is_docked = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(effective_variant,app.components.x_sidebar.model.variant_docked);
var collapsed_applied = ((is_docked) && (collapsed_requested));
var show_backdrop = ((open) && (((is_modal) || (is_overlay))));
return cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"open","open",-1763596448),new cljs.core.Keyword(null,"effective-variant","effective-variant",-829495195),new cljs.core.Keyword(null,"aria-hidden","aria-hidden",399337029),new cljs.core.Keyword(null,"collapsed-applied","collapsed-applied",-1696231706),new cljs.core.Keyword(null,"breakpoint","breakpoint",1183378440),new cljs.core.Keyword(null,"is-docked","is-docked",875310728),new cljs.core.Keyword(null,"collapsed-requested","collapsed-requested",1573505800),new cljs.core.Keyword(null,"declared-variant","declared-variant",-2054964950),new cljs.core.Keyword(null,"show-backdrop","show-backdrop",-532209910),new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"placement","placement",768366651),new cljs.core.Keyword(null,"reduced-motion","reduced-motion",79621277),new cljs.core.Keyword(null,"is-modal","is-modal",1610761086),new cljs.core.Keyword(null,"is-overlay","is-overlay",1953921119)],[open,effective_variant,(!(open)),collapsed_applied,breakpoint,is_docked,collapsed_requested,declared_variant,show_backdrop,label,placement,cljs.core.boolean$(prefers_reduced_motion),is_modal,is_overlay]);
});
app.components.x_sidebar.model.toggle_event_detail = (function app$components$x_sidebar$model$toggle_event_detail(open){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"open","open",-1763596448),cljs.core.boolean$(open)], null);
});
app.components.x_sidebar.model.dismiss_event_detail = (function app$components$x_sidebar$model$dismiss_event_detail(reason){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"reason","reason",-2070751759),reason], null);
});
app.components.x_sidebar.model.dismiss_reason_QMARK_ = (function app$components$x_sidebar$model$dismiss_reason_QMARK_(reason){
return cljs.core.contains_QMARK_(app.components.x_sidebar.model.allowed_dismiss_reason,reason);
});
app.components.x_sidebar.model.toggle_should_fire_QMARK_ = (function app$components$x_sidebar$model$toggle_should_fire_QMARK_(prev_state,next_state){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(prev_state),new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(next_state));
});
app.components.x_sidebar.model.entered_modal_open_QMARK_ = (function app$components$x_sidebar$model$entered_modal_open_QMARK_(prev_state,next_state){
var and__5140__auto__ = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(next_state);
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(next_state);
if(cljs.core.truth_(and__5140__auto____$1)){
return cljs.core.not((function (){var and__5140__auto____$2 = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(prev_state);
if(cljs.core.truth_(and__5140__auto____$2)){
return new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(prev_state);
} else {
return and__5140__auto____$2;
}
})());
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
});
app.components.x_sidebar.model.left_modal_open_QMARK_ = (function app$components$x_sidebar$model$left_modal_open_QMARK_(prev_state,next_state){
var and__5140__auto__ = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(prev_state);
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(prev_state);
if(cljs.core.truth_(and__5140__auto____$1)){
return cljs.core.not((function (){var and__5140__auto____$2 = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(next_state);
if(cljs.core.truth_(and__5140__auto____$2)){
return new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(next_state);
} else {
return and__5140__auto____$2;
}
})());
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
});

//# sourceMappingURL=app.components.x_sidebar.model.js.map
