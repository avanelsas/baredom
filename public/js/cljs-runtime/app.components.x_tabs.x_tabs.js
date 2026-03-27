goog.provide('app.components.x_tabs.x_tabs');
goog.scope(function(){
  app.components.x_tabs.x_tabs.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_tabs.x_tabs.key_root = "__x_tabs_root";
app.components.x_tabs.x_tabs.key_base = "__x_tabs_base";
app.components.x_tabs.x_tabs.key_slot = "__x_tabs_slot";
app.components.x_tabs.x_tabs.key_init = "__x_tabs_initialized";
app.components.x_tabs.x_tabs.key_on_tab_select = "__x_tabs_on_tab_select";
app.components.x_tabs.x_tabs.key_on_keydown = "__x_tabs_on_keydown";
app.components.x_tabs.x_tabs.key_observer = "__x_tabs_observer";
app.components.x_tabs.x_tabs.getv = (function app$components$x_tabs$x_tabs$getv(el,k){
return app.components.x_tabs.x_tabs.goog$module$goog$object.get(el,k);
});
app.components.x_tabs.x_tabs.setv_BANG_ = (function app$components$x_tabs$x_tabs$setv_BANG_(el,k,v){
return app.components.x_tabs.x_tabs.goog$module$goog$object.set(el,k,v);
});
app.components.x_tabs.x_tabs.initialized_QMARK_ = (function app$components$x_tabs$x_tabs$initialized_QMARK_(el){
return app.components.x_tabs.x_tabs.getv(el,app.components.x_tabs.x_tabs.key_init) === true;
});
app.components.x_tabs.x_tabs.mark_init_BANG_ = (function app$components$x_tabs$x_tabs$mark_init_BANG_(el){
return app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_init,true);
});
app.components.x_tabs.x_tabs.style_text = (""+":host{display:block;color-scheme:light dark;}"+".base{display:block;}"+"::slotted(x-tab){margin-inline-end:var(--x-tabs-gap,8px);}");
app.components.x_tabs.x_tabs.read_inputs = (function app$components$x_tabs$x_tabs$read_inputs(el){
return new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"value","value",305978217),el.getAttribute(app.components.x_tabs.model.attr_value),new cljs.core.Keyword(null,"orientation","orientation",623557579),el.getAttribute(app.components.x_tabs.model.attr_orientation),new cljs.core.Keyword(null,"activation","activation",2128521072),el.getAttribute(app.components.x_tabs.model.attr_activation),new cljs.core.Keyword(null,"label","label",1718410804),el.getAttribute(app.components.x_tabs.model.attr_label),new cljs.core.Keyword(null,"loop","loop",-395552849),el.hasAttribute(app.components.x_tabs.model.attr_loop)], null);
});
app.components.x_tabs.x_tabs.direct_children = (function app$components$x_tabs$x_tabs$direct_children(el){
return cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(el.children);
});
app.components.x_tabs.x_tabs.get_tabs = (function app$components$x_tabs$x_tabs$get_tabs(el){
return cljs.core.vec(cljs.core.filter.cljs$core$IFn$_invoke$arity$2((function (p1__21170_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("X-TAB",p1__21170_SHARP_.tagName);
}),app.components.x_tabs.x_tabs.direct_children(el)));
});
app.components.x_tabs.x_tabs.enabled_tab_QMARK_ = (function app$components$x_tabs$x_tabs$enabled_tab_QMARK_(tab){
return cljs.core.not(tab.hasAttribute("disabled"));
});
app.components.x_tabs.x_tabs.tab_value = (function app$components$x_tabs$x_tabs$tab_value(tab){
return tab.getAttribute("value");
});
app.components.x_tabs.x_tabs.first_enabled_tab = (function app$components$x_tabs$x_tabs$first_enabled_tab(tabs){
return cljs.core.some((function (tab){
if(app.components.x_tabs.x_tabs.enabled_tab_QMARK_(tab)){
return tab;
} else {
return null;
}
}),tabs);
});
app.components.x_tabs.x_tabs.derive_initial_selection = (function app$components$x_tabs$x_tabs$derive_initial_selection(tabs){
var temp__5823__auto__ = app.components.x_tabs.x_tabs.first_enabled_tab(tabs);
if(cljs.core.truth_(temp__5823__auto__)){
var tab = temp__5823__auto__;
return app.components.x_tabs.x_tabs.tab_value(tab);
} else {
return null;
}
});
app.components.x_tabs.x_tabs.set_host_value_if_needed_BANG_ = (function app$components$x_tabs$x_tabs$set_host_value_if_needed_BANG_(el,value){
var current = el.getAttribute(app.components.x_tabs.model.attr_value);
if(cljs.core.truth_((function (){var and__5140__auto__ = value;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(current,value);
} else {
return and__5140__auto__;
}
})())){
return el.setAttribute(app.components.x_tabs.model.attr_value,value);
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = (((value == null)) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(value,"")));
if(and__5140__auto__){
return current;
} else {
return and__5140__auto__;
}
})())){
return el.removeAttribute(app.components.x_tabs.model.attr_value);
} else {
return null;

}
}
});
app.components.x_tabs.x_tabs.set_tab_selected_if_needed_BANG_ = (function app$components$x_tabs$x_tabs$set_tab_selected_if_needed_BANG_(tab,selected_QMARK_){
var has_selected_QMARK_ = tab.hasAttribute("selected");
if(cljs.core.truth_((function (){var and__5140__auto__ = selected_QMARK_;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not(has_selected_QMARK_);
} else {
return and__5140__auto__;
}
})())){
return tab.setAttribute("selected","");
} else {
if(cljs.core.truth_((function (){var and__5140__auto__ = cljs.core.not(selected_QMARK_);
if(and__5140__auto__){
return has_selected_QMARK_;
} else {
return and__5140__auto__;
}
})())){
return tab.removeAttribute("selected");
} else {
return null;

}
}
});
app.components.x_tabs.x_tabs.set_tab_tabindex_if_needed_BANG_ = (function app$components$x_tabs$x_tabs$set_tab_tabindex_if_needed_BANG_(tab,tabindex){
var current = tab.getAttribute("tabindex");
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(current,tabindex)){
return tab.setAttribute("tabindex",tabindex);
} else {
return null;
}
});
app.components.x_tabs.x_tabs.select_tab_BANG_ = (function app$components$x_tabs$x_tabs$select_tab_BANG_(tabs,value){
var seq__21180 = cljs.core.seq(tabs);
var chunk__21181 = null;
var count__21182 = (0);
var i__21183 = (0);
while(true){
if((i__21183 < count__21182)){
var tab = chunk__21181.cljs$core$IIndexed$_nth$arity$2(null,i__21183);
var selected_QMARK__21407 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.tab_value(tab),value);
app.components.x_tabs.x_tabs.set_tab_selected_if_needed_BANG_(tab,selected_QMARK__21407);

if(((selected_QMARK__21407) && (app.components.x_tabs.x_tabs.enabled_tab_QMARK_(tab)))){
app.components.x_tabs.x_tabs.set_tab_tabindex_if_needed_BANG_(tab,"0");
} else {
app.components.x_tabs.x_tabs.set_tab_tabindex_if_needed_BANG_(tab,"-1");
}


var G__21410 = seq__21180;
var G__21411 = chunk__21181;
var G__21412 = count__21182;
var G__21413 = (i__21183 + (1));
seq__21180 = G__21410;
chunk__21181 = G__21411;
count__21182 = G__21412;
i__21183 = G__21413;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__21180);
if(temp__5823__auto__){
var seq__21180__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__21180__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__21180__$1);
var G__21418 = cljs.core.chunk_rest(seq__21180__$1);
var G__21419 = c__5673__auto__;
var G__21420 = cljs.core.count(c__5673__auto__);
var G__21421 = (0);
seq__21180 = G__21418;
chunk__21181 = G__21419;
count__21182 = G__21420;
i__21183 = G__21421;
continue;
} else {
var tab = cljs.core.first(seq__21180__$1);
var selected_QMARK__21426 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.tab_value(tab),value);
app.components.x_tabs.x_tabs.set_tab_selected_if_needed_BANG_(tab,selected_QMARK__21426);

if(((selected_QMARK__21426) && (app.components.x_tabs.x_tabs.enabled_tab_QMARK_(tab)))){
app.components.x_tabs.x_tabs.set_tab_tabindex_if_needed_BANG_(tab,"0");
} else {
app.components.x_tabs.x_tabs.set_tab_tabindex_if_needed_BANG_(tab,"-1");
}


var G__21427 = cljs.core.next(seq__21180__$1);
var G__21428 = null;
var G__21429 = (0);
var G__21430 = (0);
seq__21180 = G__21427;
chunk__21181 = G__21428;
count__21182 = G__21429;
i__21183 = G__21430;
continue;
}
} else {
return null;
}
}
break;
}
});
app.components.x_tabs.x_tabs.hide_panel_BANG_ = (function app$components$x_tabs$x_tabs$hide_panel_BANG_(tab,visible_QMARK_){
var id = tab.getAttribute("controls");
if(cljs.core.truth_((function (){var and__5140__auto__ = id;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(id,"");
} else {
return and__5140__auto__;
}
})())){
var temp__5823__auto__ = document.getElementById(id);
if(cljs.core.truth_(temp__5823__auto__)){
var panel = temp__5823__auto__;
if(cljs.core.truth_(visible_QMARK_)){
return panel.removeAttribute("hidden");
} else {
return panel.setAttribute("hidden","");
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_tabs.x_tabs.update_panels_BANG_ = (function app$components$x_tabs$x_tabs$update_panels_BANG_(tabs,selected_value){
var seq__21197 = cljs.core.seq(tabs);
var chunk__21198 = null;
var count__21199 = (0);
var i__21200 = (0);
while(true){
if((i__21200 < count__21199)){
var tab = chunk__21198.cljs$core$IIndexed$_nth$arity$2(null,i__21200);
app.components.x_tabs.x_tabs.hide_panel_BANG_(tab,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.tab_value(tab),selected_value));


var G__21436 = seq__21197;
var G__21437 = chunk__21198;
var G__21438 = count__21199;
var G__21439 = (i__21200 + (1));
seq__21197 = G__21436;
chunk__21198 = G__21437;
count__21199 = G__21438;
i__21200 = G__21439;
continue;
} else {
var temp__5823__auto__ = cljs.core.seq(seq__21197);
if(temp__5823__auto__){
var seq__21197__$1 = temp__5823__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__21197__$1)){
var c__5673__auto__ = cljs.core.chunk_first(seq__21197__$1);
var G__21440 = cljs.core.chunk_rest(seq__21197__$1);
var G__21441 = c__5673__auto__;
var G__21442 = cljs.core.count(c__5673__auto__);
var G__21443 = (0);
seq__21197 = G__21440;
chunk__21198 = G__21441;
count__21199 = G__21442;
i__21200 = G__21443;
continue;
} else {
var tab = cljs.core.first(seq__21197__$1);
app.components.x_tabs.x_tabs.hide_panel_BANG_(tab,cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.tab_value(tab),selected_value));


var G__21444 = cljs.core.next(seq__21197__$1);
var G__21445 = null;
var G__21446 = (0);
var G__21447 = (0);
seq__21197 = G__21444;
chunk__21198 = G__21445;
count__21199 = G__21446;
i__21200 = G__21447;
continue;
}
} else {
return null;
}
}
break;
}
});
app.components.x_tabs.x_tabs.effective_selected_value = (function app$components$x_tabs$x_tabs$effective_selected_value(tabs,host_value){
var values = cljs.core.set(cljs.core.keep.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.tab_value,tabs));
if(cljs.core.truth_((function (){var and__5140__auto__ = host_value;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.contains_QMARK_(values,host_value);
} else {
return and__5140__auto__;
}
})())){
return host_value;
} else {
return app.components.x_tabs.x_tabs.derive_initial_selection(tabs);

}
});
app.components.x_tabs.x_tabs.dispatch_value_change_BANG_ = (function app$components$x_tabs$x_tabs$dispatch_value_change_BANG_(el,value){
return el.dispatchEvent((new CustomEvent(app.components.x_tabs.model.event_value_change,({"detail": ({"value": (function (){var or__5142__auto__ = value;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})()}), "bubbles": true, "composed": true}))));
});
app.components.x_tabs.x_tabs.coordinate_tabs_BANG_ = (function app$components$x_tabs$x_tabs$coordinate_tabs_BANG_(el){
var tabs = app.components.x_tabs.x_tabs.get_tabs(el);
var host_value = el.getAttribute(app.components.x_tabs.model.attr_value);
var selected_value = app.components.x_tabs.x_tabs.effective_selected_value(tabs,host_value);
if(cljs.core.truth_(selected_value)){
app.components.x_tabs.x_tabs.select_tab_BANG_(tabs,selected_value);

app.components.x_tabs.x_tabs.update_panels_BANG_(tabs,selected_value);

app.components.x_tabs.x_tabs.set_host_value_if_needed_BANG_(el,selected_value);
} else {
}

return selected_value;
});
app.components.x_tabs.x_tabs.activate_tab_by_value_BANG_ = (function app$components$x_tabs$x_tabs$activate_tab_by_value_BANG_(el,value){
var current = el.getAttribute(app.components.x_tabs.model.attr_value);
if(cljs.core.truth_((function (){var and__5140__auto__ = value;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(current,value);
} else {
return and__5140__auto__;
}
})())){
app.components.x_tabs.x_tabs.set_host_value_if_needed_BANG_(el,value);

app.components.x_tabs.x_tabs.coordinate_tabs_BANG_(el);

return app.components.x_tabs.x_tabs.dispatch_value_change_BANG_(el,value);
} else {
return null;
}
});
app.components.x_tabs.x_tabs.on_tab_select = (function app$components$x_tabs$x_tabs$on_tab_select(el,e){
e.stopPropagation();

var value = e.detail.value;
return app.components.x_tabs.x_tabs.activate_tab_by_value_BANG_(el,value);
});
app.components.x_tabs.x_tabs.focus_tab_BANG_ = (function app$components$x_tabs$x_tabs$focus_tab_BANG_(tabs,idx){
var temp__5823__auto__ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(tabs,idx,null);
if(cljs.core.truth_(temp__5823__auto__)){
var tab = temp__5823__auto__;
tab.focus();

return tab;
} else {
return null;
}
});
app.components.x_tabs.x_tabs.next_index = (function app$components$x_tabs$x_tabs$next_index(idx,tabs,loop_QMARK_){
var last_idx = (cljs.core.count(tabs) - (1));
var candidate = (idx + (1));
if((candidate <= last_idx)){
return candidate;
} else {
if(cljs.core.truth_(loop_QMARK_)){
return (0);
} else {
return idx;

}
}
});
app.components.x_tabs.x_tabs.prev_index = (function app$components$x_tabs$x_tabs$prev_index(idx,tabs,loop_QMARK_){
var candidate = (idx - (1));
var last_idx = (cljs.core.count(tabs) - (1));
if((candidate >= (0))){
return candidate;
} else {
if(cljs.core.truth_(loop_QMARK_)){
return last_idx;
} else {
return idx;

}
}
});
app.components.x_tabs.x_tabs.handle_arrow = (function app$components$x_tabs$x_tabs$handle_arrow(el,e){
var tabs = cljs.core.vec(cljs.core.filter.cljs$core$IFn$_invoke$arity$2(app.components.x_tabs.x_tabs.enabled_tab_QMARK_,app.components.x_tabs.x_tabs.get_tabs(el)));
var state = app.components.x_tabs.model.derive_state(app.components.x_tabs.x_tabs.read_inputs(el));
var orientation = new cljs.core.Keyword(null,"orientation","orientation",623557579).cljs$core$IFn$_invoke$arity$1(state);
var activation = new cljs.core.Keyword(null,"activation","activation",2128521072).cljs$core$IFn$_invoke$arity$1(state);
var loop_QMARK_ = new cljs.core.Keyword(null,"loop","loop",-395552849).cljs$core$IFn$_invoke$arity$1(state);
var key = e.key;
var active = document.activeElement;
var idx = tabs.indexOf(active);
if((idx >= (0))){
var target_idx = ((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(orientation,"horizontal")) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowRight"))))?app.components.x_tabs.x_tabs.next_index(idx,tabs,loop_QMARK_):((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(orientation,"horizontal")) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowLeft"))))?app.components.x_tabs.x_tabs.prev_index(idx,tabs,loop_QMARK_):((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(orientation,"vertical")) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowDown"))))?app.components.x_tabs.x_tabs.next_index(idx,tabs,loop_QMARK_):((((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(orientation,"vertical")) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"ArrowUp"))))?app.components.x_tabs.x_tabs.prev_index(idx,tabs,loop_QMARK_):((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Home"))?(0):((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"End"))?(cljs.core.count(tabs) - (1)):null
))))));
if((!((target_idx == null)))){
e.preventDefault();

var temp__5823__auto__ = app.components.x_tabs.x_tabs.focus_tab_BANG_(tabs,target_idx);
if(cljs.core.truth_(temp__5823__auto__)){
var tab = temp__5823__auto__;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(activation,"auto")){
return app.components.x_tabs.x_tabs.activate_tab_by_value_BANG_(el,app.components.x_tabs.x_tabs.tab_value(tab));
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
} else {
return null;
}
});
app.components.x_tabs.x_tabs.render_BANG_ = (function app$components$x_tabs$x_tabs$render_BANG_(el){
var base = app.components.x_tabs.x_tabs.getv(el,app.components.x_tabs.x_tabs.key_base);
var state = app.components.x_tabs.model.derive_state(app.components.x_tabs.x_tabs.read_inputs(el));
if(cljs.core.truth_(base)){
base.setAttribute("role","tablist");

base.setAttribute("aria-orientation",new cljs.core.Keyword(null,"orientation","orientation",623557579).cljs$core$IFn$_invoke$arity$1(state));

var label_21465 = new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(state);
if(cljs.core.truth_((function (){var and__5140__auto__ = label_21465;
if(cljs.core.truth_(and__5140__auto__)){
return cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(label_21465,"");
} else {
return and__5140__auto__;
}
})())){
base.setAttribute("aria-label",label_21465);
} else {
base.removeAttribute("aria-label");
}
} else {
}

return app.components.x_tabs.x_tabs.coordinate_tabs_BANG_(el);
});
app.components.x_tabs.x_tabs.install_mutation_observer_BANG_ = (function app$components$x_tabs$x_tabs$install_mutation_observer_BANG_(el){
var observer = (new MutationObserver((function (_records,_observer){
return app.components.x_tabs.x_tabs.render_BANG_(el);
})));
observer.observe(el,({"childList": true, "subtree": false, "attributes": true, "attributeFilter": ["value","selected","disabled","controls"]}));

return app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_observer,observer);
});
app.components.x_tabs.x_tabs.disconnect_mutation_observer_BANG_ = (function app$components$x_tabs$x_tabs$disconnect_mutation_observer_BANG_(el){
var temp__5823__auto__ = app.components.x_tabs.x_tabs.getv(el,app.components.x_tabs.x_tabs.key_observer);
if(cljs.core.truth_(temp__5823__auto__)){
var observer = temp__5823__auto__;
return observer.disconnect();
} else {
return null;
}
});
app.components.x_tabs.x_tabs.install_listeners_BANG_ = (function app$components$x_tabs$x_tabs$install_listeners_BANG_(el){
var on_tab_select_STAR_ = (function (e){
return app.components.x_tabs.x_tabs.on_tab_select(el,e);
});
var on_keydown_STAR_ = (function (e){
return app.components.x_tabs.x_tabs.handle_arrow(el,e);
});
el.addEventListener("tab-select",on_tab_select_STAR_);

el.addEventListener("keydown",on_keydown_STAR_);

app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_on_tab_select,on_tab_select_STAR_);

app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_on_keydown,on_keydown_STAR_);

return app.components.x_tabs.x_tabs.install_mutation_observer_BANG_(el);
});
app.components.x_tabs.x_tabs.remove_listeners_BANG_ = (function app$components$x_tabs$x_tabs$remove_listeners_BANG_(el){
var temp__5823__auto___21466 = app.components.x_tabs.x_tabs.getv(el,app.components.x_tabs.x_tabs.key_on_tab_select);
if(cljs.core.truth_(temp__5823__auto___21466)){
var on_tab_select_STAR__21467 = temp__5823__auto___21466;
el.removeEventListener("tab-select",on_tab_select_STAR__21467);
} else {
}

var temp__5823__auto___21468 = app.components.x_tabs.x_tabs.getv(el,app.components.x_tabs.x_tabs.key_on_keydown);
if(cljs.core.truth_(temp__5823__auto___21468)){
var on_keydown_STAR__21469 = temp__5823__auto___21468;
el.removeEventListener("keydown",on_keydown_STAR__21469);
} else {
}

return app.components.x_tabs.x_tabs.disconnect_mutation_observer_BANG_(el);
});
app.components.x_tabs.x_tabs.init_dom_BANG_ = (function app$components$x_tabs$x_tabs$init_dom_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style = document.createElement("style");
var base = document.createElement("div");
var slot = document.createElement("slot");
(style.textContent = app.components.x_tabs.x_tabs.style_text);

base.setAttribute("part","base");

(base.className = "base");

base.appendChild(slot);

root.appendChild(style);

root.appendChild(base);

app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_root,root);

app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_base,base);

return app.components.x_tabs.x_tabs.setv_BANG_(el,app.components.x_tabs.x_tabs.key_slot,slot);
});
app.components.x_tabs.x_tabs.init_element_BANG_ = (function app$components$x_tabs$x_tabs$init_element_BANG_(el){
if(app.components.x_tabs.x_tabs.initialized_QMARK_(el)){
} else {
app.components.x_tabs.x_tabs.init_dom_BANG_(el);

app.components.x_tabs.x_tabs.install_listeners_BANG_(el);

app.components.x_tabs.x_tabs.mark_init_BANG_(el);
}

app.components.x_tabs.x_tabs.render_BANG_(el);

return el;
});
app.components.x_tabs.x_tabs.connected_callback = (function app$components$x_tabs$x_tabs$connected_callback(el){
if(app.components.x_tabs.x_tabs.initialized_QMARK_(el)){
app.components.x_tabs.x_tabs.install_listeners_BANG_(el);

return app.components.x_tabs.x_tabs.render_BANG_(el);
} else {
return app.components.x_tabs.x_tabs.init_element_BANG_(el);
}
});
app.components.x_tabs.x_tabs.disconnected_callback = (function app$components$x_tabs$x_tabs$disconnected_callback(el){
return app.components.x_tabs.x_tabs.remove_listeners_BANG_(el);
});
app.components.x_tabs.x_tabs.attribute_changed_callback = (function app$components$x_tabs$x_tabs$attribute_changed_callback(el,_name,_old,_new){
if(app.components.x_tabs.x_tabs.initialized_QMARK_(el)){
return app.components.x_tabs.x_tabs.render_BANG_(el);
} else {
return null;
}
});
app.components.x_tabs.x_tabs.install_property_accessors_BANG_ = (function app$components$x_tabs$x_tabs$install_property_accessors_BANG_(klass){
return Object.defineProperty(klass.prototype,"value",({"get": (function (){
var this$ = this;
return this$.getAttribute(app.components.x_tabs.model.attr_value);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_tabs.model.attr_value,v);
} else {
return this$.removeAttribute(app.components.x_tabs.model.attr_value);
}
}), "configurable": true, "enumerable": true}));
});
app.components.x_tabs.x_tabs.element_class = (function app$components$x_tabs$x_tabs$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_tabs.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
return app.components.x_tabs.x_tabs.connected_callback(this$);
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
return app.components.x_tabs.x_tabs.disconnected_callback(this$);
}));

(klass.prototype.attributeChangedCallback = (function (name,old,new$){
var this$ = this;
return app.components.x_tabs.x_tabs.attribute_changed_callback(this$,name,old,new$);
}));

app.components.x_tabs.x_tabs.install_property_accessors_BANG_(klass);

return klass;
});
app.components.x_tabs.x_tabs.register_BANG_ = (function app$components$x_tabs$x_tabs$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_tabs.model.tag_name))){
return null;
} else {
return customElements.define(app.components.x_tabs.model.tag_name,app.components.x_tabs.x_tabs.element_class());
}
});
app.components.x_tabs.x_tabs.init_BANG_ = (function app$components$x_tabs$x_tabs$init_BANG_(){
return app.components.x_tabs.x_tabs.register_BANG_();
});

//# sourceMappingURL=app.components.x_tabs.x_tabs.js.map
