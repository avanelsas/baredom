goog.provide('app.components.x_sidebar.x_sidebar');
goog.scope(function(){
  app.components.x_sidebar.x_sidebar.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_sidebar.x_sidebar.css_text = (""+":host {"+"  --x-sidebar-bg: Canvas;"+"  --x-sidebar-fg: CanvasText;"+"  --x-sidebar-border: color-mix(in srgb, currentColor 12%, transparent);"+"  --x-sidebar-backdrop: rgb(0 0 0 / 0.4);"+"  --x-sidebar-shadow: 0 8px 24px rgb(0 0 0 / 0.18);"+"  --x-sidebar-width: 18rem;"+"  --x-sidebar-collapsed-width: 4rem;"+"  --x-sidebar-duration: 180ms;"+"  --x-sidebar-easing: ease;"+"  display: block;"+"  position: relative;"+"}"+":host([hidden]) { display: none; }"+".backdrop[hidden] { display: none; }"+".backdrop {"+"  position: fixed;"+"  inset: 0;"+"  background: var(--x-sidebar-backdrop);"+"  opacity: 0;"+"  z-index: 999;"+"  pointer-events: none;"+"  transition: opacity var(--x-sidebar-duration) var(--x-sidebar-easing);"+"}"+".backdrop[data-active='true'] {"+"  opacity: 1;"+"  pointer-events: auto;"+"}"+".sidebar {"+"  position: relative;"+"  color: var(--x-sidebar-fg);"+"  block-size: 100%;"+"}"+".panel {"+"  background: var(--x-sidebar-bg);"+"  color: var(--x-sidebar-fg);"+"  inline-size: var(--x-sidebar-width);"+"  min-block-size: 100%;"+"  overflow: hidden;"+"  box-shadow: var(--x-sidebar-shadow);"+"  will-change: transform, inline-size;"+"  transition: transform var(--x-sidebar-duration) var(--x-sidebar-easing),"+"              inline-size var(--x-sidebar-duration) var(--x-sidebar-easing),"+"              opacity var(--x-sidebar-duration) var(--x-sidebar-easing);"+"}"+":host([data-effective-variant='docked']) .panel[data-collapsed='true'] {"+"  inline-size: var(--x-sidebar-collapsed-width);"+"}"+":host([data-effective-variant='overlay']) .sidebar,"+":host([data-effective-variant='modal']) .sidebar {"+"  position: fixed;"+"  inset-block: 0;"+"  z-index: 1000;"+"  pointer-events: none;"+"}"+":host([data-effective-variant='overlay'][data-open='true']) .sidebar,"+":host([data-effective-variant='modal'][data-open='true']) .sidebar {"+"  pointer-events: auto;"+"}"+":host([data-effective-variant='overlay'][data-placement='left']) .sidebar,"+":host([data-effective-variant='modal'][data-placement='left']) .sidebar {"+"  inset-inline-start: 0;"+"}"+":host([data-effective-variant='overlay'][data-placement='right']) .sidebar,"+":host([data-effective-variant='modal'][data-placement='right']) .sidebar {"+"  inset-inline-end: 0;"+"}"+":host([data-effective-variant='overlay'][data-placement='left']) .panel[data-open='false'],"+":host([data-effective-variant='modal'][data-placement='left']) .panel[data-open='false'] {"+"  transform: translateX(-100%);"+"  pointer-events: none;"+"}"+":host([data-effective-variant='overlay'][data-placement='right']) .panel[data-open='false'],"+":host([data-effective-variant='modal'][data-placement='right']) .panel[data-open='false'] {"+"  transform: translateX(100%);"+"  pointer-events: none;"+"}"+":host([data-effective-variant='overlay']) .panel[data-open='true'],"+":host([data-effective-variant='modal']) .panel[data-open='true'] {"+"  transform: translateX(0);"+"  pointer-events: auto;"+"}"+":host([data-reduced-motion='true']) .panel,"+":host([data-reduced-motion='true']) .backdrop {"+"  transition: none;"+"}"+"@media (prefers-color-scheme: dark) {"+"  :host {"+"    --x-sidebar-border: color-mix(in srgb, currentColor 20%, transparent);"+"    --x-sidebar-shadow: 0 8px 24px rgb(0 0 0 / 0.4);"+"  }"+"}");
app.components.x_sidebar.x_sidebar.has_attr_QMARK_ = (function app$components$x_sidebar$x_sidebar$has_attr_QMARK_(el,name){
return el.hasAttribute(name);
});
app.components.x_sidebar.x_sidebar.get_attr = (function app$components$x_sidebar$x_sidebar$get_attr(el,name){
return el.getAttribute(name);
});
app.components.x_sidebar.x_sidebar.set_bool_attr_BANG_ = (function app$components$x_sidebar$x_sidebar$set_bool_attr_BANG_(el,name,value){
if(cljs.core.truth_(value)){
return el.setAttribute(name,"");
} else {
return el.removeAttribute(name);
}
});
app.components.x_sidebar.x_sidebar.set_data_attr_BANG_ = (function app$components$x_sidebar$x_sidebar$set_data_attr_BANG_(el,name,value){
return el.setAttribute(name,value);
});
app.components.x_sidebar.x_sidebar.prefers_reduced_motion_QMARK_ = (function app$components$x_sidebar$x_sidebar$prefers_reduced_motion_QMARK_(){
var mq = window.matchMedia("(prefers-reduced-motion: reduce)");
return cljs.core.boolean$(mq.matches);
});
app.components.x_sidebar.x_sidebar.viewport_width = (function app$components$x_sidebar$x_sidebar$viewport_width(){
return window.innerWidth;
});
app.components.x_sidebar.x_sidebar.read_inputs = (function app$components$x_sidebar$x_sidebar$read_inputs(instance){
return new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"open-attr","open-attr",-1772289801),app.components.x_sidebar.x_sidebar.has_attr_QMARK_(instance,app.components.x_sidebar.model.attr_open),new cljs.core.Keyword(null,"collapsed-attr","collapsed-attr",-1535396832),app.components.x_sidebar.x_sidebar.has_attr_QMARK_(instance,app.components.x_sidebar.model.attr_collapsed),new cljs.core.Keyword(null,"placement-attr","placement-attr",-1321967286),app.components.x_sidebar.x_sidebar.get_attr(instance,app.components.x_sidebar.model.attr_placement),new cljs.core.Keyword(null,"variant-attr","variant-attr",-1143729279),app.components.x_sidebar.x_sidebar.get_attr(instance,app.components.x_sidebar.model.attr_variant),new cljs.core.Keyword(null,"breakpoint-attr","breakpoint-attr",-250275627),app.components.x_sidebar.x_sidebar.get_attr(instance,app.components.x_sidebar.model.attr_breakpoint),new cljs.core.Keyword(null,"label-attr","label-attr",-571749887),app.components.x_sidebar.x_sidebar.get_attr(instance,app.components.x_sidebar.model.attr_label),new cljs.core.Keyword(null,"viewport-width","viewport-width",-1146235948),app.components.x_sidebar.x_sidebar.viewport_width(),new cljs.core.Keyword(null,"prefers-reduced-motion","prefers-reduced-motion",-2023150975),app.components.x_sidebar.x_sidebar.prefers_reduced_motion_QMARK_()], null);
});
app.components.x_sidebar.x_sidebar.dispatch_custom_event_BANG_ = (function app$components$x_sidebar$x_sidebar$dispatch_custom_event_BANG_(instance,event_name,detail){
return instance.dispatchEvent((new CustomEvent(event_name,cljs.core.clj__GT_js(new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"detail","detail",-1545345025),detail,new cljs.core.Keyword(null,"bubbles","bubbles",1049634589),true,new cljs.core.Keyword(null,"composed","composed",-1510693384),true], null)))));
});
app.components.x_sidebar.x_sidebar.emit_toggle_BANG_ = (function app$components$x_sidebar$x_sidebar$emit_toggle_BANG_(instance,open){
return app.components.x_sidebar.x_sidebar.dispatch_custom_event_BANG_(instance,app.components.x_sidebar.model.event_toggle,app.components.x_sidebar.model.toggle_event_detail(open));
});
app.components.x_sidebar.x_sidebar.emit_dismiss_BANG_ = (function app$components$x_sidebar$x_sidebar$emit_dismiss_BANG_(instance,reason){
if(app.components.x_sidebar.model.dismiss_reason_QMARK_(reason)){
return app.components.x_sidebar.x_sidebar.dispatch_custom_event_BANG_(instance,app.components.x_sidebar.model.event_dismiss,app.components.x_sidebar.model.dismiss_event_detail(reason));
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.visible_element_QMARK_ = (function app$components$x_sidebar$x_sidebar$visible_element_QMARK_(el){
var style = window.getComputedStyle(el);
return ((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("none",style.display)) && (((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("hidden",style.visibility)) && ((((el.offsetWidth > (0))) && ((el.offsetHeight > (0))))))));
});
app.components.x_sidebar.x_sidebar.collect_tabbables = (function app$components$x_sidebar$x_sidebar$collect_tabbables(root){
var selector = (""+"a[href], button:not([disabled]), input:not([disabled]), "+"select:not([disabled]), textarea:not([disabled]), "+"[tabindex]:not([tabindex='-1'])");
var node_list = root.querySelectorAll(selector);
return cljs.core.vec(cljs.core.filter.cljs$core$IFn$_invoke$arity$2(app.components.x_sidebar.x_sidebar.visible_element_QMARK_,cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(node_list)));
});
app.components.x_sidebar.x_sidebar.focused_sidebar_descendant = (function app$components$x_sidebar$x_sidebar$focused_sidebar_descendant(instance){
var root = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_root");
var sidebar = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_sidebar");
var shadow_active = (cljs.core.truth_(root)?root.activeElement:null);
var doc_active = document.activeElement;
if(cljs.core.truth_((function (){var and__5140__auto__ = shadow_active;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = sidebar;
if(cljs.core.truth_(and__5140__auto____$1)){
return sidebar.contains(shadow_active);
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return shadow_active;
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = doc_active;
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = sidebar;
if(cljs.core.truth_(and__5140__auto____$1)){
return sidebar.contains(doc_active);
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
return doc_active;
} else {
return null;

}
}
});
app.components.x_sidebar.x_sidebar.release_sidebar_focus_BANG_ = (function app$components$x_sidebar$x_sidebar$release_sidebar_focus_BANG_(instance){
var temp__5823__auto__ = app.components.x_sidebar.x_sidebar.focused_sidebar_descendant(instance);
if(cljs.core.truth_(temp__5823__auto__)){
var focused = temp__5823__auto__;
return focused.blur();
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.focus_panel_BANG_ = (function app$components$x_sidebar$x_sidebar$focus_panel_BANG_(instance){
var panel = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panel");
if(cljs.core.truth_(panel.hasAttribute("tabindex"))){
} else {
panel.setAttribute("tabindex","-1");

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_panelTabindexAdded",true);
}

return panel.focus();
});
app.components.x_sidebar.x_sidebar.activate_focus_trap_BANG_ = (function app$components$x_sidebar$x_sidebar$activate_focus_trap_BANG_(instance){
var panel = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panel");
var tabbables = app.components.x_sidebar.x_sidebar.collect_tabbables(panel);
app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_restoreFocusTarget",document.activeElement);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_tabbables",cljs.core.clj__GT_js(tabbables));

if(cljs.core.seq(tabbables)){
return cljs.core.first(tabbables).focus();
} else {
return app.components.x_sidebar.x_sidebar.focus_panel_BANG_(instance);
}
});
app.components.x_sidebar.x_sidebar.deactivate_focus_trap_BANG_ = (function app$components$x_sidebar$x_sidebar$deactivate_focus_trap_BANG_(instance){
var restore_target = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_restoreFocusTarget");
var panel = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panel");
var panel_tabindex_added = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panelTabindexAdded") === true;
app.components.x_sidebar.x_sidebar.release_sidebar_focus_BANG_(instance);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_tabbables",null);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_restoreFocusTarget",null);

if(cljs.core.truth_((function (){var and__5140__auto__ = panel;
if(cljs.core.truth_(and__5140__auto__)){
return panel_tabindex_added;
} else {
return and__5140__auto__;
}
})())){
panel.removeAttribute("tabindex");

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_panelTabindexAdded",false);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = restore_target;
if(cljs.core.truth_(and__5140__auto__)){
return restore_target.isConnected;
} else {
return and__5140__auto__;
}
})())){
return restore_target.focus();
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.cycle_focus_BANG_ = (function app$components$x_sidebar$x_sidebar$cycle_focus_BANG_(instance,event){
var tabbables_js = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_tabbables");
var tabbables = (cljs.core.truth_(tabbables_js)?cljs.core.vec(cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(tabbables_js)):cljs.core.PersistentVector.EMPTY);
var panel = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panel");
if(cljs.core.empty_QMARK_(tabbables)){
event.preventDefault();

if(cljs.core.truth_(panel)){
return app.components.x_sidebar.x_sidebar.focus_panel_BANG_(instance);
} else {
return null;
}
} else {
var active = document.activeElement;
var first_el = cljs.core.first(tabbables);
var last_el = cljs.core.last(tabbables);
var shift_QMARK_ = event.shiftKey;
if(cljs.core.truth_((function (){var and__5140__auto__ = shift_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active,first_el);
} else {
return and__5140__auto__;
}
})())){
event.preventDefault();

return last_el.focus();
} else {
if(((cljs.core.not(shift_QMARK_)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(active,last_el)))){
event.preventDefault();

return first_el.focus();
} else {
return null;

}
}
}
});
app.components.x_sidebar.x_sidebar.apply_host_state_BANG_ = (function app$components$x_sidebar$x_sidebar$apply_host_state_BANG_(instance,p__20998){
var map__20999 = p__20998;
var map__20999__$1 = cljs.core.__destructure_map(map__20999);
var effective_variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20999__$1,new cljs.core.Keyword(null,"effective-variant","effective-variant",-829495195));
var placement = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20999__$1,new cljs.core.Keyword(null,"placement","placement",768366651));
var open = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20999__$1,new cljs.core.Keyword(null,"open","open",-1763596448));
var collapsed_applied = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20999__$1,new cljs.core.Keyword(null,"collapsed-applied","collapsed-applied",-1696231706));
var reduced_motion = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__20999__$1,new cljs.core.Keyword(null,"reduced-motion","reduced-motion",79621277));
app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(instance,"data-effective-variant",effective_variant);

app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(instance,"data-placement",placement);

app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(instance,"data-open",(cljs.core.truth_(open)?"true":"false"));

app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(instance,"data-collapsed",(cljs.core.truth_(collapsed_applied)?"true":"false"));

return app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(instance,"data-reduced-motion",(cljs.core.truth_(reduced_motion)?"true":"false"));
});
app.components.x_sidebar.x_sidebar.apply_a11y_BANG_ = (function app$components$x_sidebar$x_sidebar$apply_a11y_BANG_(instance,p__21008){
var map__21015 = p__21008;
var map__21015__$1 = cljs.core.__destructure_map(map__21015);
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21015__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var aria_hidden = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21015__$1,new cljs.core.Keyword(null,"aria-hidden","aria-hidden",399337029));
var temp__5823__auto__ = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_sidebar");
if(cljs.core.truth_(temp__5823__auto__)){
var sidebar = temp__5823__auto__;
sidebar.setAttribute("role","navigation");

sidebar.setAttribute("aria-label",label);

return sidebar.setAttribute("aria-hidden",(cljs.core.truth_(aria_hidden)?"true":"false"));
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.apply_backdrop_BANG_ = (function app$components$x_sidebar$x_sidebar$apply_backdrop_BANG_(instance,p__21024){
var map__21025 = p__21024;
var map__21025__$1 = cljs.core.__destructure_map(map__21025);
var show_backdrop = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21025__$1,new cljs.core.Keyword(null,"show-backdrop","show-backdrop",-532209910));
var temp__5823__auto__ = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_backdrop");
if(cljs.core.truth_(temp__5823__auto__)){
var backdrop = temp__5823__auto__;
app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(backdrop,"data-active",(cljs.core.truth_(show_backdrop)?"true":"false"));

(backdrop.hidden = cljs.core.not(show_backdrop));

if((!((backdrop.inert == null)))){
return (backdrop.inert = cljs.core.not(show_backdrop));
} else {
return null;
}
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.apply_panel_state_BANG_ = (function app$components$x_sidebar$x_sidebar$apply_panel_state_BANG_(instance,p__21026){
var map__21027 = p__21026;
var map__21027__$1 = cljs.core.__destructure_map(map__21027);
var open = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21027__$1,new cljs.core.Keyword(null,"open","open",-1763596448));
var placement = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21027__$1,new cljs.core.Keyword(null,"placement","placement",768366651));
var effective_variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21027__$1,new cljs.core.Keyword(null,"effective-variant","effective-variant",-829495195));
var collapsed_applied = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21027__$1,new cljs.core.Keyword(null,"collapsed-applied","collapsed-applied",-1696231706));
var temp__5823__auto__ = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_panel");
if(cljs.core.truth_(temp__5823__auto__)){
var panel = temp__5823__auto__;
app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(panel,"data-open",(cljs.core.truth_(open)?"true":"false"));

app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(panel,"data-placement",placement);

app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(panel,"data-variant",effective_variant);

return app.components.x_sidebar.x_sidebar.set_data_attr_BANG_(panel,"data-collapsed",(cljs.core.truth_(collapsed_applied)?"true":"false"));
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.sync_focus_trap_BANG_ = (function app$components$x_sidebar$x_sidebar$sync_focus_trap_BANG_(instance,prev_state,next_state){
if(cljs.core.truth_(app.components.x_sidebar.model.entered_modal_open_QMARK_(prev_state,next_state))){
return app.components.x_sidebar.x_sidebar.activate_focus_trap_BANG_(instance);
} else {
if(cljs.core.truth_(app.components.x_sidebar.model.left_modal_open_QMARK_(prev_state,next_state))){
return app.components.x_sidebar.x_sidebar.deactivate_focus_trap_BANG_(instance);
} else {
return null;

}
}
});
app.components.x_sidebar.x_sidebar.dispatch_state_events_BANG_ = (function app$components$x_sidebar$x_sidebar$dispatch_state_events_BANG_(instance,prev_state,next_state){
if(app.components.x_sidebar.model.toggle_should_fire_QMARK_(prev_state,next_state)){
return app.components.x_sidebar.x_sidebar.emit_toggle_BANG_(instance,new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(next_state));
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.ensure_shadow_BANG_ = (function app$components$x_sidebar$x_sidebar$ensure_shadow_BANG_(instance){
if(cljs.core.truth_(app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_root"))){
return null;
} else {
var root = instance.attachShadow(({"mode": "open"}));
var style_el = document.createElement("style");
var backdrop_el = document.createElement("div");
var sidebar_el = document.createElement("aside");
var panel_el = document.createElement("div");
var slot_el = document.createElement("slot");
(style_el.textContent = app.components.x_sidebar.x_sidebar.css_text);

(backdrop_el.className = "backdrop");

backdrop_el.setAttribute("part",app.components.x_sidebar.model.part_backdrop);

(backdrop_el.hidden = true);

(sidebar_el.className = "sidebar");

sidebar_el.setAttribute("part",app.components.x_sidebar.model.part_sidebar);

(panel_el.className = "panel");

panel_el.setAttribute("part",app.components.x_sidebar.model.part_panel);

panel_el.appendChild(slot_el);

sidebar_el.appendChild(panel_el);

root.appendChild(style_el);

root.appendChild(backdrop_el);

root.appendChild(sidebar_el);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_root",root);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_backdrop",backdrop_el);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_sidebar",sidebar_el);

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_panel",panel_el);

return app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_slot",slot_el);
}
});
app.components.x_sidebar.x_sidebar.render_BANG_ = (function app$components$x_sidebar$x_sidebar$render_BANG_(instance){
app.components.x_sidebar.x_sidebar.ensure_shadow_BANG_(instance);

var prev_state = (function (){var or__5142__auto__ = (function (){var G__21064 = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_prevState");
if((G__21064 == null)){
return null;
} else {
return cljs.core.js__GT_clj.cljs$core$IFn$_invoke$arity$variadic(G__21064,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"keywordize-keys","keywordize-keys",1310784252),true], 0));
}
})();
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return cljs.core.PersistentArrayMap.EMPTY;
}
})();
var next_state = app.components.x_sidebar.model.compute_state(app.components.x_sidebar.x_sidebar.read_inputs(instance));
app.components.x_sidebar.x_sidebar.apply_host_state_BANG_(instance,next_state);

app.components.x_sidebar.x_sidebar.sync_focus_trap_BANG_(instance,prev_state,next_state);

app.components.x_sidebar.x_sidebar.apply_a11y_BANG_(instance,next_state);

app.components.x_sidebar.x_sidebar.apply_backdrop_BANG_(instance,next_state);

app.components.x_sidebar.x_sidebar.apply_panel_state_BANG_(instance,next_state);

app.components.x_sidebar.x_sidebar.dispatch_state_events_BANG_(instance,prev_state,next_state);

return app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_prevState",cljs.core.clj__GT_js(next_state));
});
app.components.x_sidebar.x_sidebar.on_backdrop_click = (function app$components$x_sidebar$x_sidebar$on_backdrop_click(instance,event){
var state = (function (){var G__21075 = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_prevState");
if((G__21075 == null)){
return null;
} else {
return cljs.core.js__GT_clj.cljs$core$IFn$_invoke$arity$variadic(G__21075,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"keywordize-keys","keywordize-keys",1310784252),true], 0));
}
})();
var backdrop = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_backdrop");
if(cljs.core.truth_((function (){var and__5140__auto__ = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"is-overlay","is-overlay",1953921119).cljs$core$IFn$_invoke$arity$1(state);
}
})();
if(cljs.core.truth_(and__5140__auto__)){
var and__5140__auto____$1 = new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(and__5140__auto____$1)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(event.target,backdrop);
} else {
return and__5140__auto____$1;
}
} else {
return and__5140__auto__;
}
})())){
app.components.x_sidebar.x_sidebar.emit_dismiss_BANG_(instance,app.components.x_sidebar.model.reason_backdrop);

return app.components.x_sidebar.x_sidebar.set_bool_attr_BANG_(instance,app.components.x_sidebar.model.attr_open,false);
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.on_keydown = (function app$components$x_sidebar$x_sidebar$on_keydown(instance,event){
var state = (function (){var G__21077 = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_prevState");
if((G__21077 == null)){
return null;
} else {
return cljs.core.js__GT_clj.cljs$core$IFn$_invoke$arity$variadic(G__21077,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"keywordize-keys","keywordize-keys",1310784252),true], 0));
}
})();
var key = event.key;
if(cljs.core.truth_((function (){var and__5140__auto__ = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"is-overlay","is-overlay",1953921119).cljs$core$IFn$_invoke$arity$1(state);
}
})();
if(cljs.core.truth_(and__5140__auto__)){
return new cljs.core.Keyword(null,"open","open",-1763596448).cljs$core$IFn$_invoke$arity$1(state);
} else {
return and__5140__auto__;
}
})())){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Escape")){
event.preventDefault();

app.components.x_sidebar.x_sidebar.emit_dismiss_BANG_(instance,app.components.x_sidebar.model.reason_escape);

return app.components.x_sidebar.x_sidebar.set_bool_attr_BANG_(instance,app.components.x_sidebar.model.attr_open,false);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = new cljs.core.Keyword(null,"is-modal","is-modal",1610761086).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Tab");
} else {
return and__5140__auto__;
}
})())){
return app.components.x_sidebar.x_sidebar.cycle_focus_BANG_(instance,event);
} else {
return null;

}
}
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.on_resize = (function app$components$x_sidebar$x_sidebar$on_resize(instance,_event){
return app.components.x_sidebar.x_sidebar.render_BANG_(instance);
});
app.components.x_sidebar.x_sidebar.bind_handlers_BANG_ = (function app$components$x_sidebar$x_sidebar$bind_handlers_BANG_(instance){
if(cljs.core.truth_(app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_handlers"))){
return null;
} else {
return app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_handlers",({"onBackdropClick": (function (event){
return app.components.x_sidebar.x_sidebar.on_backdrop_click(instance,event);
}), "onKeydown": (function (event){
return app.components.x_sidebar.x_sidebar.on_keydown(instance,event);
}), "onResize": (function (event){
return app.components.x_sidebar.x_sidebar.on_resize(instance,event);
})}));
}
});
app.components.x_sidebar.x_sidebar.add_listeners_BANG_ = (function app$components$x_sidebar$x_sidebar$add_listeners_BANG_(instance){
if(app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_listening") === true){
return null;
} else {
var handlers = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_handlers");
var backdrop = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_backdrop");
var root = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_root");
if(cljs.core.truth_((function (){var and__5140__auto__ = backdrop;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
backdrop.addEventListener("click",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onBackdropClick"));
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = root;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
root.addEventListener("keydown",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onKeydown"));
} else {
}

if(cljs.core.truth_(handlers)){
window.addEventListener("resize",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onResize"));
} else {
}

return app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_listening",true);
}
});
app.components.x_sidebar.x_sidebar.remove_listeners_BANG_ = (function app$components$x_sidebar$x_sidebar$remove_listeners_BANG_(instance){
if(app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_listening") === true){
var handlers = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_handlers");
var backdrop = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_backdrop");
var root = app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(instance,"_root");
if(cljs.core.truth_((function (){var and__5140__auto__ = backdrop;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
backdrop.removeEventListener("click",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onBackdropClick"));
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = root;
if(cljs.core.truth_(and__5140__auto__)){
return handlers;
} else {
return and__5140__auto__;
}
})())){
root.removeEventListener("keydown",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onKeydown"));
} else {
}

if(cljs.core.truth_(handlers)){
window.removeEventListener("resize",app.components.x_sidebar.x_sidebar.goog$module$goog$object.get(handlers,"onResize"));
} else {
}

return app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(instance,"_listening",false);
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.connected_callback = (function app$components$x_sidebar$x_sidebar$connected_callback(instance){
app.components.x_sidebar.x_sidebar.ensure_shadow_BANG_(instance);

app.components.x_sidebar.x_sidebar.bind_handlers_BANG_(instance);

app.components.x_sidebar.x_sidebar.add_listeners_BANG_(instance);

return app.components.x_sidebar.x_sidebar.render_BANG_(instance);
});
app.components.x_sidebar.x_sidebar.disconnected_callback = (function app$components$x_sidebar$x_sidebar$disconnected_callback(instance){
app.components.x_sidebar.x_sidebar.remove_listeners_BANG_(instance);

return app.components.x_sidebar.x_sidebar.deactivate_focus_trap_BANG_(instance);
});
app.components.x_sidebar.x_sidebar.attribute_changed_callback = (function app$components$x_sidebar$x_sidebar$attribute_changed_callback(instance,_name,old_value,new_value){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_value,new_value)){
return app.components.x_sidebar.x_sidebar.render_BANG_(instance);
} else {
return null;
}
});
app.components.x_sidebar.x_sidebar.define_open_property_BANG_ = (function app$components$x_sidebar$x_sidebar$define_open_property_BANG_(proto){
return Object.defineProperty(proto,app.components.x_sidebar.model.attr_open,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_sidebar.model.attr_open);
}), "set": (function (value){
var this$ = this;
return app.components.x_sidebar.x_sidebar.set_bool_attr_BANG_(this$,app.components.x_sidebar.model.attr_open,cljs.core.boolean$(value));
}), "enumerable": true, "configurable": true}));
});
app.components.x_sidebar.x_sidebar.define_collapsed_property_BANG_ = (function app$components$x_sidebar$x_sidebar$define_collapsed_property_BANG_(proto){
return Object.defineProperty(proto,app.components.x_sidebar.model.attr_collapsed,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_sidebar.model.attr_collapsed);
}), "set": (function (value){
var this$ = this;
return app.components.x_sidebar.x_sidebar.set_bool_attr_BANG_(this$,app.components.x_sidebar.model.attr_collapsed,cljs.core.boolean$(value));
}), "enumerable": true, "configurable": true}));
});
app.components.x_sidebar.x_sidebar.install_prototype_BANG_ = (function app$components$x_sidebar$x_sidebar$install_prototype_BANG_(proto){
app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(proto,"connectedCallback",(function (){
var this$ = this;
return app.components.x_sidebar.x_sidebar.connected_callback(this$);
}));

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(proto,"disconnectedCallback",(function (){
var this$ = this;
return app.components.x_sidebar.x_sidebar.disconnected_callback(this$);
}));

app.components.x_sidebar.x_sidebar.goog$module$goog$object.set(proto,"attributeChangedCallback",(function (name,old_value,new_value){
var this$ = this;
return app.components.x_sidebar.x_sidebar.attribute_changed_callback(this$,name,old_value,new_value);
}));

app.components.x_sidebar.x_sidebar.define_open_property_BANG_(proto);

app.components.x_sidebar.x_sidebar.define_collapsed_property_BANG_(proto);

return proto;
});
app.components.x_sidebar.x_sidebar.define_custom_element_BANG_ = (function app$components$x_sidebar$x_sidebar$define_custom_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_sidebar.model.tag_name))){
return null;
} else {
var base_proto = HTMLElement.prototype;
var proto = app.components.x_sidebar.x_sidebar.install_prototype_BANG_(Object.create(base_proto));
var ctor = (function () { return Reflect.construct(HTMLElement, [], this.constructor); });
(ctor.prototype = proto);

Object.defineProperty(proto,"constructor",({"value": ctor}));

Object.setPrototypeOf(ctor,HTMLElement);

(ctor["observedAttributes"] = app.components.x_sidebar.model.observed_attributes);

return customElements.define(app.components.x_sidebar.model.tag_name,ctor);
}
});
app.components.x_sidebar.x_sidebar.init_BANG_ = (function app$components$x_sidebar$x_sidebar$init_BANG_(){
return app.components.x_sidebar.x_sidebar.define_custom_element_BANG_();
});

//# sourceMappingURL=app.components.x_sidebar.x_sidebar.js.map
