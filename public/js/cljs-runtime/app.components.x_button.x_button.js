goog.provide('app.components.x_button.x_button');
app.components.x_button.x_button.state_key = "__xButtonState";
app.components.x_button.x_button.hover_key = "__xButtonHover";
app.components.x_button.x_button.focus_visible_key = "__xButtonFocusVisible";
app.components.x_button.x_button.active_source_key = "__xButtonActiveSource";
app.components.x_button.x_button.last_activation_source_key = "__xButtonLastActivationSource";
app.components.x_button.x_button.get_prop = (function app$components$x_button$x_button$get_prop(obj,k){
return (obj[k]);
});
app.components.x_button.x_button.set_prop_BANG_ = (function app$components$x_button$x_button$set_prop_BANG_(obj,k,v){
return (obj[k] = v);
});
app.components.x_button.x_button.has_attr_QMARK_ = (function app$components$x_button$x_button$has_attr_QMARK_(el,attr_name){
return el.hasAttribute(attr_name);
});
app.components.x_button.x_button.get_attr = (function app$components$x_button$x_button$get_attr(el,attr_name){
return el.getAttribute(attr_name);
});
app.components.x_button.x_button.set_bool_attr_BANG_ = (function app$components$x_button$x_button$set_bool_attr_BANG_(el,attr_name,value){
if(cljs.core.truth_(value)){
return el.setAttribute(attr_name,"");
} else {
return el.removeAttribute(attr_name);
}
});
app.components.x_button.x_button.get_el_state = (function app$components$x_button$x_button$get_el_state(el){
return app.components.x_button.x_button.get_prop(el,app.components.x_button.x_button.state_key);
});
app.components.x_button.x_button.set_el_state_BANG_ = (function app$components$x_button$x_button$set_el_state_BANG_(el,state){
return app.components.x_button.x_button.set_prop_BANG_(el,app.components.x_button.x_button.state_key,state);
});
app.components.x_button.x_button.get_hover = (function app$components$x_button$x_button$get_hover(el){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(true,app.components.x_button.x_button.get_prop(el,app.components.x_button.x_button.hover_key));
});
app.components.x_button.x_button.set_hover_BANG_ = (function app$components$x_button$x_button$set_hover_BANG_(el,value){
return app.components.x_button.x_button.set_prop_BANG_(el,app.components.x_button.x_button.hover_key,cljs.core.boolean$(value));
});
app.components.x_button.x_button.get_focus_visible = (function app$components$x_button$x_button$get_focus_visible(el){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(true,app.components.x_button.x_button.get_prop(el,app.components.x_button.x_button.focus_visible_key));
});
app.components.x_button.x_button.set_focus_visible_BANG_ = (function app$components$x_button$x_button$set_focus_visible_BANG_(el,value){
return app.components.x_button.x_button.set_prop_BANG_(el,app.components.x_button.x_button.focus_visible_key,cljs.core.boolean$(value));
});
app.components.x_button.x_button.get_active_source = (function app$components$x_button$x_button$get_active_source(el){
return app.components.x_button.x_button.get_prop(el,app.components.x_button.x_button.active_source_key);
});
app.components.x_button.x_button.set_active_source_BANG_ = (function app$components$x_button$x_button$set_active_source_BANG_(el,value){
return app.components.x_button.x_button.set_prop_BANG_(el,app.components.x_button.x_button.active_source_key,value);
});
app.components.x_button.x_button.get_last_activation_source = (function app$components$x_button$x_button$get_last_activation_source(el){
return app.components.x_button.x_button.get_prop(el,app.components.x_button.x_button.last_activation_source_key);
});
app.components.x_button.x_button.set_last_activation_source_BANG_ = (function app$components$x_button$x_button$set_last_activation_source_BANG_(el,value){
return app.components.x_button.x_button.set_prop_BANG_(el,app.components.x_button.x_button.last_activation_source_key,value);
});
app.components.x_button.x_button.read_public_state = (function app$components$x_button$x_button$read_public_state(el){
return app.components.x_button.model.public_state(new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"disabled","disabled",-1529784218),app.components.x_button.x_button.has_attr_QMARK_(el,app.components.x_button.model.attr_disabled),new cljs.core.Keyword(null,"loading","loading",-737050189),app.components.x_button.x_button.has_attr_QMARK_(el,app.components.x_button.model.attr_loading),new cljs.core.Keyword(null,"pressed","pressed",1100937946),app.components.x_button.x_button.has_attr_QMARK_(el,app.components.x_button.model.attr_pressed),new cljs.core.Keyword(null,"type","type",1174270348),app.components.x_button.x_button.get_attr(el,app.components.x_button.model.attr_type),new cljs.core.Keyword(null,"variant","variant",-424354234),app.components.x_button.x_button.get_attr(el,app.components.x_button.model.attr_variant),new cljs.core.Keyword(null,"size","size",1098693007),app.components.x_button.x_button.get_attr(el,app.components.x_button.model.attr_size),new cljs.core.Keyword(null,"label","label",1718410804),app.components.x_button.x_button.get_attr(el,app.components.x_button.model.attr_label)], null));
});
app.components.x_button.x_button.interactive_el_QMARK_ = (function app$components$x_button$x_button$interactive_el_QMARK_(el){
return app.components.x_button.model.interactive_QMARK_(app.components.x_button.x_button.read_public_state(el));
});
app.components.x_button.x_button.dispatch_BANG_ = (function app$components$x_button$x_button$dispatch_BANG_(el,event_name,detail){
return el.dispatchEvent((new CustomEvent(event_name,({"detail": detail, "bubbles": true, "composed": true, "cancelable": false}))));
});
app.components.x_button.x_button.assigned_nodes = (function app$components$x_button$x_button$assigned_nodes(slot_el){
return slot_el.assignedNodes(({"flatten": true}));
});
app.components.x_button.x_button.slot_has_content_QMARK_ = (function app$components$x_button$x_button$slot_has_content_QMARK_(slot_el){
return (app.components.x_button.x_button.assigned_nodes(slot_el).length > (0));
});
app.components.x_button.x_button.meaningful_text_node_QMARK_ = (function app$components$x_button$x_button$meaningful_text_node_QMARK_(node){
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node.nodeType,Node.TEXT_NODE)) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",(function (){var or__5142__auto__ = node.textContent;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})().trim())));
});
app.components.x_button.x_button.meaningful_element_node_QMARK_ = (function app$components$x_button$x_button$meaningful_element_node_QMARK_(node){
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(node.nodeType,Node.ELEMENT_NODE)) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2("",(function (){var or__5142__auto__ = node.textContent;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})().trim())));
});
app.components.x_button.x_button.slot_has_meaningful_text_QMARK_ = (function app$components$x_button$x_button$slot_has_meaningful_text_QMARK_(slot_el){
var nodes = app.components.x_button.x_button.assigned_nodes(slot_el);
var idx = (0);
while(true){
if((idx < nodes.length)){
var node = (nodes[idx]);
if(((app.components.x_button.x_button.meaningful_text_node_QMARK_(node)) || (app.components.x_button.x_button.meaningful_element_node_QMARK_(node)))){
return true;
} else {
var G__20762 = (idx + (1));
idx = G__20762;
continue;
}
} else {
return false;
}
break;
}
});
app.components.x_button.x_button.style_text = (function app$components$x_button$x_button$style_text(){
return (""+":host{"+"display:inline-block;"+"vertical-align:middle;"+"color-scheme:light dark;"+"--x-button-radius:0.75rem;"+"--x-button-gap:0.5rem;"+"--x-button-padding-inline:0.95rem;"+"--x-button-height-sm:2rem;"+"--x-button-height-md:2.5rem;"+"--x-button-height-lg:3rem;"+"--x-button-font-size-sm:0.875rem;"+"--x-button-font-size-md:0.9375rem;"+"--x-button-font-size-lg:1rem;"+"--x-button-font-weight:600;"+"--x-button-icon-size-sm:0.875rem;"+"--x-button-icon-size-md:1rem;"+"--x-button-icon-size-lg:1.125rem;"+"--x-button-spinner-size:1em;"+"--x-button-spinner-stroke:2px;"+"--x-button-shadow:0 1px 2px rgba(15,23,42,0.08),0 1px 1px rgba(15,23,42,0.04);"+"--x-button-shadow-hover:0 4px 10px rgba(15,23,42,0.10),0 2px 4px rgba(15,23,42,0.06);"+"--x-button-shadow-active:0 1px 2px rgba(15,23,42,0.08);"+"--x-button-bg:#2563eb;"+"--x-button-bg-hover:#1d4ed8;"+"--x-button-bg-active:#1e40af;"+"--x-button-bg-disabled:#cbd5e1;"+"--x-button-fg:#ffffff;"+"--x-button-fg-disabled:#ffffff;"+"--x-button-border:transparent;"+"--x-button-border-hover:transparent;"+"--x-button-border-active:transparent;"+"--x-button-secondary-bg:#ffffff;"+"--x-button-secondary-bg-hover:#f8fafc;"+"--x-button-secondary-bg-active:#f1f5f9;"+"--x-button-secondary-fg:#0f172a;"+"--x-button-secondary-border:#cbd5e1;"+"--x-button-tertiary-bg:#f1f5f9;"+"--x-button-tertiary-bg-hover:#e2e8f0;"+"--x-button-tertiary-bg-active:#cbd5e1;"+"--x-button-tertiary-fg:#0f172a;"+"--x-button-ghost-bg:transparent;"+"--x-button-ghost-bg-hover:#f8fafc;"+"--x-button-ghost-bg-active:#f1f5f9;"+"--x-button-ghost-fg:#334155;"+"--x-button-danger-bg:#dc2626;"+"--x-button-danger-bg-hover:#b91c1c;"+"--x-button-danger-bg-active:#991b1b;"+"--x-button-danger-fg:#ffffff;"+"--x-button-focus-ring:#60a5fa;"+"--x-button-transition-duration:140ms;"+"--x-button-transition-easing:cubic-bezier(0.2,0,0,1);"+"}"+"@media (prefers-color-scheme: dark){"+":host{"+"--x-button-shadow:0 1px 2px rgba(0,0,0,0.35),0 1px 1px rgba(0,0,0,0.2);"+"--x-button-shadow-hover:0 6px 14px rgba(0,0,0,0.35),0 2px 6px rgba(0,0,0,0.24);"+"--x-button-shadow-active:0 1px 2px rgba(0,0,0,0.3);"+"--x-button-bg:#3b82f6;"+"--x-button-bg-hover:#2563eb;"+"--x-button-bg-active:#1d4ed8;"+"--x-button-bg-disabled:#334155;"+"--x-button-fg:#eff6ff;"+"--x-button-fg-disabled:#94a3b8;"+"--x-button-secondary-bg:#111827;"+"--x-button-secondary-bg-hover:#1f2937;"+"--x-button-secondary-bg-active:#273449;"+"--x-button-secondary-fg:#e5e7eb;"+"--x-button-secondary-border:#374151;"+"--x-button-tertiary-bg:#1e293b;"+"--x-button-tertiary-bg-hover:#273449;"+"--x-button-tertiary-bg-active:#334155;"+"--x-button-tertiary-fg:#e5e7eb;"+"--x-button-ghost-bg:transparent;"+"--x-button-ghost-bg-hover:#0f172a;"+"--x-button-ghost-bg-active:#172033;"+"--x-button-ghost-fg:#cbd5e1;"+"--x-button-danger-bg:#ef4444;"+"--x-button-danger-bg-hover:#dc2626;"+"--x-button-danger-bg-active:#b91c1c;"+"--x-button-danger-fg:#ffffff;"+"--x-button-focus-ring:#93c5fd;"+"}"+"}"+"@keyframes x-button-spin{"+"from{transform:rotate(0deg);}"+"to{transform:rotate(360deg);}"+"}"+"button{"+"all:unset;"+"box-sizing:border-box;"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"inline-size:100%;"+"min-inline-size:0;"+"min-block-size:var(--x-button-height-md);"+"padding-inline:var(--x-button-padding-inline);"+"border-radius:var(--x-button-radius);"+"border:1px solid var(--x-button-border);"+"background:var(--x-button-bg);"+"color:var(--x-button-fg);"+"font-size:var(--x-button-font-size-md);"+"font-weight:var(--x-button-font-weight);"+"line-height:1;"+"cursor:pointer;"+"user-select:none;"+"box-shadow:var(--x-button-shadow);"+"transition:"+"background var(--x-button-transition-duration) var(--x-button-transition-easing),"+"border-color var(--x-button-transition-duration) var(--x-button-transition-easing),"+"color var(--x-button-transition-duration) var(--x-button-transition-easing),"+"transform var(--x-button-transition-duration) var(--x-button-transition-easing),"+"box-shadow var(--x-button-transition-duration) var(--x-button-transition-easing),"+"opacity var(--x-button-transition-duration) var(--x-button-transition-easing);"+"}"+"button[disabled]{cursor:default;box-shadow:none;}"+"button:focus-visible{outline:none;box-shadow:0 0 0 3px var(--x-button-focus-ring),var(--x-button-shadow);}"+"button[data-size='sm']{min-block-size:var(--x-button-height-sm);font-size:var(--x-button-font-size-sm);}"+"button[data-size='md']{min-block-size:var(--x-button-height-md);font-size:var(--x-button-font-size-md);}"+"button[data-size='lg']{min-block-size:var(--x-button-height-lg);font-size:var(--x-button-font-size-lg);}"+"button[data-variant='primary']{"+"background:var(--x-button-bg);"+"color:var(--x-button-fg);"+"border-color:var(--x-button-border);"+"}"+"button[data-variant='primary'][data-hover='true']:not([disabled]){"+"background:var(--x-button-bg-hover);"+"box-shadow:var(--x-button-shadow-hover);"+"}"+"button[data-variant='primary'][data-active='true']:not([disabled]){"+"background:var(--x-button-bg-active);"+"transform:translateY(1px);"+"box-shadow:var(--x-button-shadow-active);"+"}"+"button[data-variant='secondary']{"+"background:var(--x-button-secondary-bg);"+"color:var(--x-button-secondary-fg);"+"border-color:var(--x-button-secondary-border);"+"}"+"button[data-variant='secondary'][data-hover='true']:not([disabled]){"+"background:var(--x-button-secondary-bg-hover);"+"border-color:var(--x-button-secondary-border);"+"box-shadow:var(--x-button-shadow-hover);"+"}"+"button[data-variant='secondary'][data-active='true']:not([disabled]){"+"background:var(--x-button-secondary-bg-active);"+"transform:translateY(1px);"+"box-shadow:var(--x-button-shadow-active);"+"}"+"button[data-variant='tertiary']{"+"background:var(--x-button-tertiary-bg);"+"color:var(--x-button-tertiary-fg);"+"border-color:transparent;"+"box-shadow:none;"+"}"+"button[data-variant='tertiary'][data-hover='true']:not([disabled]){"+"background:var(--x-button-tertiary-bg-hover);"+"}"+"button[data-variant='tertiary'][data-active='true']:not([disabled]){"+"background:var(--x-button-tertiary-bg-active);"+"transform:translateY(1px);"+"}"+"button[data-variant='ghost']{"+"background:var(--x-button-ghost-bg);"+"color:var(--x-button-ghost-fg);"+"border-color:transparent;"+"box-shadow:none;"+"}"+"button[data-variant='ghost'][data-hover='true']:not([disabled]){"+"background:var(--x-button-ghost-bg-hover);"+"}"+"button[data-variant='ghost'][data-active='true']:not([disabled]){"+"background:var(--x-button-ghost-bg-active);"+"transform:translateY(1px);"+"}"+"button[data-variant='danger']{"+"background:var(--x-button-danger-bg);"+"color:var(--x-button-danger-fg);"+"border-color:transparent;"+"}"+"button[data-variant='danger'][data-hover='true']:not([disabled]){"+"background:var(--x-button-danger-bg-hover);"+"box-shadow:var(--x-button-shadow-hover);"+"}"+"button[data-variant='danger'][data-active='true']:not([disabled]){"+"background:var(--x-button-danger-bg-active);"+"transform:translateY(1px);"+"box-shadow:var(--x-button-shadow-active);"+"}"+"button[data-loading='true']{cursor:progress;}"+"button[data-disabled='true']{"+"background:var(--x-button-bg-disabled);"+"color:var(--x-button-fg-disabled);"+"border-color:transparent;"+"opacity:0.72;"+"}"+"[part='inner']{display:inline-flex;align-items:center;justify-content:center;gap:var(--x-button-gap);min-inline-size:0;}"+"[part='label']{display:inline-flex;align-items:center;justify-content:center;min-inline-size:0;white-space:nowrap;}"+"[part='icon-start'],[part='icon-end'],[part='spinner']{display:inline-flex;align-items:center;justify-content:center;flex:none;}"+"button[data-size='sm'] [part='icon-start'],button[data-size='sm'] [part='icon-end']{inline-size:var(--x-button-icon-size-sm);block-size:var(--x-button-icon-size-sm);}"+"button[data-size='md'] [part='icon-start'],button[data-size='md'] [part='icon-end']{inline-size:var(--x-button-icon-size-md);block-size:var(--x-button-icon-size-md);}"+"button[data-size='lg'] [part='icon-start'],button[data-size='lg'] [part='icon-end']{inline-size:var(--x-button-icon-size-lg);block-size:var(--x-button-icon-size-lg);}"+"[part='spinner']{inline-size:var(--x-button-spinner-size);block-size:var(--x-button-spinner-size);position:relative;}"+"[part='spinner-slot']{display:inline-flex;align-items:center;justify-content:center;}"+"[part='spinner-fallback']{"+"display:none;"+"inline-size:100%;"+"block-size:100%;"+"box-sizing:border-box;"+"border-radius:999px;"+"border:var(--x-button-spinner-stroke) solid currentColor;"+"border-inline-end-color:transparent;"+"animation:x-button-spin 0.7s linear infinite;"+"opacity:0.9;"+"}"+"button[data-has-icon-start='false'] [part='icon-start']{display:none;}"+"button[data-has-icon-end='false'] [part='icon-end']{display:none;}"+"button[data-loading='false'] [part='spinner']{display:none;}"+"button[data-loading='true'][data-has-spinner='false'] [part='spinner-fallback']{display:inline-block;}"+"button[data-loading='true'][data-has-spinner='true'] [part='spinner-fallback']{display:none;}"+"@media (prefers-reduced-motion: reduce){"+"button{transition:none;}"+"[part='spinner-fallback']{animation:none;}"+"}");
});
app.components.x_button.x_button.create_el = (function app$components$x_button$x_button$create_el(tag){
return document.createElement(tag);
});
app.components.x_button.x_button.make_shadow_state = (function app$components$x_button$x_button$make_shadow_state(root,style_el,button_el,inner_el,label_slot_el,icon_start_slot_el,icon_end_slot_el,spinner_slot_el){
return ({"root": root, "style": style_el, "button": button_el, "inner": inner_el, "label-slot": label_slot_el, "icon-start-slot": icon_start_slot_el, "icon-end-slot": icon_end_slot_el, "spinner-slot": spinner_slot_el});
});
app.components.x_button.x_button.create_shadow_BANG_ = (function app$components$x_button$x_button$create_shadow_BANG_(el){
var root = el.attachShadow(({"mode": "open"}));
var style_el = app.components.x_button.x_button.create_el("style");
var button_el = app.components.x_button.x_button.create_el("button");
var inner_el = app.components.x_button.x_button.create_el("span");
var icon_start_el = app.components.x_button.x_button.create_el("span");
var label_el = app.components.x_button.x_button.create_el("span");
var spinner_el = app.components.x_button.x_button.create_el("span");
var spinner_fallback_el = app.components.x_button.x_button.create_el("span");
var icon_end_el = app.components.x_button.x_button.create_el("span");
var default_slot_el = app.components.x_button.x_button.create_el("slot");
var icon_start_slot_el = app.components.x_button.x_button.create_el("slot");
var icon_end_slot_el = app.components.x_button.x_button.create_el("slot");
var spinner_slot_el = app.components.x_button.x_button.create_el("slot");
(style_el.textContent = app.components.x_button.x_button.style_text());

button_el.setAttribute("part","button");

inner_el.setAttribute("part","inner");

icon_start_el.setAttribute("part","icon-start");

label_el.setAttribute("part","label");

spinner_el.setAttribute("part","spinner");

spinner_slot_el.setAttribute("part","spinner-slot");

spinner_fallback_el.setAttribute("part","spinner-fallback");

icon_end_el.setAttribute("part","icon-end");

icon_start_slot_el.setAttribute("name",app.components.x_button.model.slot_icon_start);

icon_end_slot_el.setAttribute("name",app.components.x_button.model.slot_icon_end);

spinner_slot_el.setAttribute("name",app.components.x_button.model.slot_spinner);

spinner_el.setAttribute("aria-hidden","true");

spinner_fallback_el.setAttribute("aria-hidden","true");

icon_start_el.appendChild(icon_start_slot_el);

label_el.appendChild(default_slot_el);

spinner_el.appendChild(spinner_slot_el);

spinner_el.appendChild(spinner_fallback_el);

icon_end_el.appendChild(icon_end_slot_el);

inner_el.appendChild(icon_start_el);

inner_el.appendChild(label_el);

inner_el.appendChild(spinner_el);

inner_el.appendChild(icon_end_el);

button_el.appendChild(inner_el);

root.appendChild(style_el);

root.appendChild(button_el);

return app.components.x_button.x_button.make_shadow_state(root,style_el,button_el,inner_el,default_slot_el,icon_start_slot_el,icon_end_slot_el,spinner_slot_el);
});
app.components.x_button.x_button.render_BANG_ = (function app$components$x_button$x_button$render_BANG_(el,state){
var button_el = (state["button"]);
var label_slot_el = (state["label-slot"]);
var icon_start_slot_el = (state["icon-start-slot"]);
var icon_end_slot_el = (state["icon-end-slot"]);
var spinner_slot_el = (state["spinner-slot"]);
var public_state = app.components.x_button.x_button.read_public_state(el);
var has_default_text_QMARK_ = app.components.x_button.x_button.slot_has_meaningful_text_QMARK_(label_slot_el);
var has_icon_start_QMARK_ = app.components.x_button.x_button.slot_has_content_QMARK_(icon_start_slot_el);
var has_icon_end_QMARK_ = app.components.x_button.x_button.slot_has_content_QMARK_(icon_end_slot_el);
var has_spinner_QMARK_ = app.components.x_button.x_button.slot_has_content_QMARK_(spinner_slot_el);
var hover_QMARK_ = app.components.x_button.x_button.get_hover(el);
var active_QMARK_ = (!((app.components.x_button.x_button.get_active_source(el) == null)));
var focus_visible_QMARK_ = app.components.x_button.x_button.get_focus_visible(el);
var aria_label_value = app.components.x_button.model.aria_label(cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(public_state,new cljs.core.Keyword(null,"has-default-text?","has-default-text?",2021837624),has_default_text_QMARK_));
button_el.setAttribute("type",new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(public_state));

(button_el.disabled = (function (){var or__5142__auto__ = new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(public_state);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return new cljs.core.Keyword(null,"loading","loading",-737050189).cljs$core$IFn$_invoke$arity$1(public_state);
}
})());

var temp__5821__auto___20796 = app.components.x_button.model.aria_busy(public_state);
if(cljs.core.truth_(temp__5821__auto___20796)){
var v_20798 = temp__5821__auto___20796;
button_el.setAttribute("aria-busy",v_20798);
} else {
button_el.removeAttribute("aria-busy");
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"pressed","pressed",1100937946).cljs$core$IFn$_invoke$arity$1(public_state))){
button_el.setAttribute("aria-pressed","true");
} else {
button_el.removeAttribute("aria-pressed");
}

if(cljs.core.truth_(aria_label_value)){
button_el.setAttribute("aria-label",aria_label_value);
} else {
button_el.removeAttribute("aria-label");
}

button_el.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(public_state));

button_el.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(public_state));

button_el.setAttribute("data-loading",(cljs.core.truth_(new cljs.core.Keyword(null,"loading","loading",-737050189).cljs$core$IFn$_invoke$arity$1(public_state))?"true":"false"));

button_el.setAttribute("data-disabled",(cljs.core.truth_(new cljs.core.Keyword(null,"disabled","disabled",-1529784218).cljs$core$IFn$_invoke$arity$1(public_state))?"true":"false"));

button_el.setAttribute("data-hover",((hover_QMARK_)?"true":"false"));

button_el.setAttribute("data-active",((active_QMARK_)?"true":"false"));

button_el.setAttribute("data-focus-visible",((focus_visible_QMARK_)?"true":"false"));

button_el.setAttribute("data-has-icon-start",((has_icon_start_QMARK_)?"true":"false"));

button_el.setAttribute("data-has-icon-end",((has_icon_end_QMARK_)?"true":"false"));

button_el.setAttribute("data-has-spinner",((has_spinner_QMARK_)?"true":"false"));

el.setAttribute("data-variant",new cljs.core.Keyword(null,"variant","variant",-424354234).cljs$core$IFn$_invoke$arity$1(public_state));

return el.setAttribute("data-size",new cljs.core.Keyword(null,"size","size",1098693007).cljs$core$IFn$_invoke$arity$1(public_state));
});
app.components.x_button.x_button.end_active_press_BANG_ = (function app$components$x_button$x_button$end_active_press_BANG_(el){
var source = app.components.x_button.x_button.get_active_source(el);
if(cljs.core.truth_(source)){
app.components.x_button.x_button.set_active_source_BANG_(el,null);

app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_press_end,({"source": source}));

var temp__5823__auto__ = app.components.x_button.x_button.get_el_state(el);
if(cljs.core.truth_(temp__5823__auto__)){
var state = temp__5823__auto__;
return app.components.x_button.x_button.render_BANG_(el,state);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_button.x_button.sync_noninteractive_state_BANG_ = (function app$components$x_button$x_button$sync_noninteractive_state_BANG_(el){
if(app.components.x_button.x_button.interactive_el_QMARK_(el)){
return null;
} else {
app.components.x_button.x_button.set_hover_BANG_(el,false);

return app.components.x_button.x_button.set_active_source_BANG_(el,null);
}
});
app.components.x_button.x_button.setup_hover_BANG_ = (function app$components$x_button$x_button$setup_hover_BANG_(el,button_el){
button_el.addEventListener("pointerenter",(function (_){
if(app.components.x_button.x_button.interactive_el_QMARK_(el)){
if(app.components.x_button.x_button.get_hover(el)){
return null;
} else {
app.components.x_button.x_button.set_hover_BANG_(el,true);

app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));

return app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_hover_start,({}));
}
} else {
return null;
}
}));

return button_el.addEventListener("pointerleave",(function (_){
if(app.components.x_button.x_button.get_hover(el)){
app.components.x_button.x_button.set_hover_BANG_(el,false);

app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));

if(app.components.x_button.x_button.interactive_el_QMARK_(el)){
app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_hover_end,({}));
} else {
}
} else {
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("pointer",app.components.x_button.x_button.get_active_source(el))){
return app.components.x_button.x_button.end_active_press_BANG_(el);
} else {
return null;
}
}));
});
app.components.x_button.x_button.setup_press_BANG_ = (function app$components$x_button$x_button$setup_press_BANG_(el,button_el){
button_el.addEventListener("pointerdown",(function (_){
if(app.components.x_button.x_button.interactive_el_QMARK_(el)){
if(cljs.core.truth_(app.components.x_button.x_button.get_active_source(el))){
return null;
} else {
app.components.x_button.x_button.set_last_activation_source_BANG_(el,"pointer");

app.components.x_button.x_button.set_active_source_BANG_(el,"pointer");

app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));

return app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_press_start,({"source": "pointer"}));
}
} else {
return null;
}
}));

button_el.addEventListener("pointerup",(function (_){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("pointer",app.components.x_button.x_button.get_active_source(el))){
return app.components.x_button.x_button.end_active_press_BANG_(el);
} else {
return null;
}
}));

button_el.addEventListener("pointercancel",(function (_){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("pointer",app.components.x_button.x_button.get_active_source(el))){
return app.components.x_button.x_button.end_active_press_BANG_(el);
} else {
return null;
}
}));

button_el.addEventListener("keydown",(function (event){
var key = event.key;
if(((app.components.x_button.x_button.interactive_el_QMARK_(el)) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")))))){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("keyboard",app.components.x_button.x_button.get_active_source(el))){
return null;
} else {
app.components.x_button.x_button.set_last_activation_source_BANG_(el,"keyboard");

app.components.x_button.x_button.set_active_source_BANG_(el,"keyboard");

app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));

return app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_press_start,({"source": "keyboard"}));
}
} else {
return null;
}
}));

button_el.addEventListener("keyup",(function (event){
var key = event.key;
if(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2("keyboard",app.components.x_button.x_button.get_active_source(el))) && (((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key,"Enter")) || (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(key," ")))))){
return app.components.x_button.x_button.end_active_press_BANG_(el);
} else {
return null;
}
}));

button_el.addEventListener("blur",(function (_){
if(cljs.core.truth_(app.components.x_button.x_button.get_active_source(el))){
return app.components.x_button.x_button.end_active_press_BANG_(el);
} else {
return null;
}
}));

return button_el.addEventListener("click",(function (_){
if(app.components.x_button.x_button.interactive_el_QMARK_(el)){
var source = (function (){var or__5142__auto__ = app.components.x_button.x_button.get_last_activation_source(el);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "programmatic";
}
})();
app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_press,({"source": source}));

return app.components.x_button.x_button.set_last_activation_source_BANG_(el,null);
} else {
return null;
}
}));
});
app.components.x_button.x_button.setup_focus_BANG_ = (function app$components$x_button$x_button$setup_focus_BANG_(el,button_el){
button_el.addEventListener("focus",(function (_){
var visible = button_el.matches(":focus-visible");
app.components.x_button.x_button.set_focus_visible_BANG_(el,visible);

app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));

if(cljs.core.truth_(visible)){
return app.components.x_button.x_button.dispatch_BANG_(el,app.components.x_button.model.event_focus_visible,({}));
} else {
return null;
}
}));

return button_el.addEventListener("blur",(function (_){
app.components.x_button.x_button.set_focus_visible_BANG_(el,false);

return app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));
}));
});
app.components.x_button.x_button.setup_slots_BANG_ = (function app$components$x_button$x_button$setup_slots_BANG_(el,state){
var rerender = (function (_){
return app.components.x_button.x_button.render_BANG_(el,state);
});
(state["label-slot"]).addEventListener("slotchange",rerender);

(state["icon-start-slot"]).addEventListener("slotchange",rerender);

(state["icon-end-slot"]).addEventListener("slotchange",rerender);

return (state["spinner-slot"]).addEventListener("slotchange",rerender);
});
app.components.x_button.x_button.connected_BANG_ = (function app$components$x_button$x_button$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_button.x_button.get_el_state(el))){
} else {
var state_20815 = app.components.x_button.x_button.create_shadow_BANG_(el);
var button_el_20816 = (state_20815["button"]);
app.components.x_button.x_button.set_hover_BANG_(el,false);

app.components.x_button.x_button.set_focus_visible_BANG_(el,false);

app.components.x_button.x_button.set_active_source_BANG_(el,null);

app.components.x_button.x_button.set_last_activation_source_BANG_(el,null);

app.components.x_button.x_button.setup_hover_BANG_(el,button_el_20816);

app.components.x_button.x_button.setup_press_BANG_(el,button_el_20816);

app.components.x_button.x_button.setup_focus_BANG_(el,button_el_20816);

app.components.x_button.x_button.setup_slots_BANG_(el,state_20815);

app.components.x_button.x_button.set_el_state_BANG_(el,state_20815);
}

app.components.x_button.x_button.sync_noninteractive_state_BANG_(el);

return app.components.x_button.x_button.render_BANG_(el,app.components.x_button.x_button.get_el_state(el));
});
app.components.x_button.x_button.disconnected_BANG_ = (function app$components$x_button$x_button$disconnected_BANG_(el){
app.components.x_button.x_button.set_hover_BANG_(el,false);

app.components.x_button.x_button.set_focus_visible_BANG_(el,false);

app.components.x_button.x_button.set_active_source_BANG_(el,null);

return app.components.x_button.x_button.set_last_activation_source_BANG_(el,null);
});
app.components.x_button.x_button.attribute_changed_BANG_ = (function app$components$x_button$x_button$attribute_changed_BANG_(el,_name,_old_value,_new_value){
var temp__5823__auto__ = app.components.x_button.x_button.get_el_state(el);
if(cljs.core.truth_(temp__5823__auto__)){
var state = temp__5823__auto__;
app.components.x_button.x_button.sync_noninteractive_state_BANG_(el);

return app.components.x_button.x_button.render_BANG_(el,state);
} else {
return null;
}
});
app.components.x_button.x_button.define_bool_prop_BANG_ = (function app$components$x_button$x_button$define_bool_prop_BANG_(proto,prop_name,attr_name){
return Object.defineProperty(proto,prop_name,({"configurable": true, "enumerable": true, "get": (function (){
var this$ = this;
return app.components.x_button.x_button.has_attr_QMARK_(this$,attr_name);
}), "set": (function (value){
var this$ = this;
return app.components.x_button.x_button.set_bool_attr_BANG_(this$,attr_name,cljs.core.boolean$(value));
})}));
});
app.components.x_button.x_button.make_constructor = (function app$components$x_button$x_button$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_button.x_button.define_element_BANG_ = (function app$components$x_button$x_button$define_element_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_button.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_button.x_button.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_button.x_button.define_bool_prop_BANG_(proto,"disabled",app.components.x_button.model.attr_disabled);

app.components.x_button.x_button.define_bool_prop_BANG_(proto,"loading",app.components.x_button.model.attr_loading);

app.components.x_button.x_button.define_bool_prop_BANG_(proto,"pressed",app.components.x_button.model.attr_pressed);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_button.x_button.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_button.x_button.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (name,old_value,new_value){
var this$ = this;
return app.components.x_button.x_button.attribute_changed_BANG_(this$,name,old_value,new_value);
}));

(ctor["observedAttributes"] = app.components.x_button.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_button.model.tag_name,ctor);
}
});
app.components.x_button.x_button.init_BANG_ = (function app$components$x_button$x_button$init_BANG_(){
return app.components.x_button.x_button.define_element_BANG_();
});

//# sourceMappingURL=app.components.x_button.x_button.js.map
