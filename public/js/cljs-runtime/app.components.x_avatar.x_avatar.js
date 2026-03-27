goog.provide('app.components.x_avatar.x_avatar');
goog.scope(function(){
  app.components.x_avatar.x_avatar.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_avatar.x_avatar.k_refs = "__xAvatarRefs";
app.components.x_avatar.x_avatar.k_model = "__xAvatarModel";
app.components.x_avatar.x_avatar.k_handlers = "__xAvatarHandlers";
app.components.x_avatar.x_avatar.k_img_ok = "__xAvatarImgOk";
app.components.x_avatar.x_avatar.k_last_src = "__xAvatarLastSrc";
app.components.x_avatar.x_avatar.style_text = (""+":host{"+"display:inline-block;"+"vertical-align:middle;"+"position:relative;"+"color-scheme:light dark;"+"--x-avatar-size-xs:20px;"+"--x-avatar-size-sm:24px;"+"--x-avatar-size-md:32px;"+"--x-avatar-size-lg:40px;"+"--x-avatar-size-xl:48px;"+"--x-avatar-radius:10px;"+"--x-avatar-disabled-opacity:0.55;"+"--x-avatar-bg:rgba(0,0,0,0.06);"+"--x-avatar-border:rgba(0,0,0,0.14);"+"--x-avatar-color:rgba(0,0,0,0.86);"+"--x-avatar-ring:#ffffff;"+"--x-avatar-status-online:rgba(16,140,72,0.95);"+"--x-avatar-status-offline:rgba(0,0,0,0.45);"+"--x-avatar-status-busy:rgba(190,20,40,0.95);"+"--x-avatar-status-away:rgba(204,120,0,0.95);"+"--x-avatar-status-color:transparent;"+"--x-avatar-font-size:0.875rem;"+"--x-avatar-size:var(--x-avatar-size-md);}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-avatar-bg:rgba(255,255,255,0.10);"+"--x-avatar-border:rgba(255,255,255,0.18);"+"--x-avatar-color:rgba(255,255,255,0.88);"+"--x-avatar-ring:#0b0c0f;"+"--x-avatar-status-online:rgba(60,210,120,0.95);"+"--x-avatar-status-offline:rgba(255,255,255,0.45);"+"--x-avatar-status-busy:rgba(255,90,110,0.95);"+"--x-avatar-status-away:rgba(255,190,90,0.95);}}"+":host([data-size='xs']){--x-avatar-size:var(--x-avatar-size-xs);--x-avatar-font-size:0.625rem;}"+":host([data-size='sm']){--x-avatar-size:var(--x-avatar-size-sm);--x-avatar-font-size:0.75rem;}"+":host([data-size='md']){--x-avatar-size:var(--x-avatar-size-md);}"+":host([data-size='lg']){--x-avatar-size:var(--x-avatar-size-lg);--x-avatar-font-size:1rem;}"+":host([data-size='xl']){--x-avatar-size:var(--x-avatar-size-xl);--x-avatar-font-size:1.125rem;}"+":host([data-variant='brand']){"+"--x-avatar-bg:rgba(0,102,204,0.10);"+"--x-avatar-border:rgba(0,102,204,0.30);"+"--x-avatar-color:rgba(0,60,120,0.90);}"+":host([data-variant='subtle']){"+"--x-avatar-bg:rgba(0,0,0,0.03);"+"--x-avatar-border:rgba(0,0,0,0.08);"+"--x-avatar-color:rgba(0,0,0,0.55);}"+"@media (prefers-color-scheme:dark){"+":host([data-variant='brand']){"+"--x-avatar-bg:rgba(80,160,255,0.18);"+"--x-avatar-border:rgba(80,160,255,0.40);"+"--x-avatar-color:rgba(210,235,255,0.92);}"+":host([data-variant='subtle']){"+"--x-avatar-bg:rgba(255,255,255,0.04);"+"--x-avatar-border:rgba(255,255,255,0.08);"+"--x-avatar-color:rgba(255,255,255,0.50);}}"+"[part=root]{"+"width:var(--x-avatar-size);"+"height:var(--x-avatar-size);"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"overflow:hidden;"+"background:var(--x-avatar-bg);"+"color:var(--x-avatar-color);"+"border:1px solid var(--x-avatar-border);"+"box-shadow:0 0 0 2px var(--x-avatar-ring);"+"font-size:var(--x-avatar-font-size);"+"font-weight:600;"+"line-height:1;"+"user-select:none;"+"border-radius:999px;}"+":host([data-shape='square']) [part=root]{border-radius:0;}"+":host([data-shape='rounded']) [part=root]{border-radius:var(--x-avatar-radius);}"+":host([disabled]){opacity:var(--x-avatar-disabled-opacity);}"+"[part=image]{width:100%;height:100%;object-fit:cover;display:none;}"+"[part=initials],[part=fallback]{display:none;padding:0 0.25em;}"+"[part=status]{"+"position:absolute;"+"right:-1px;"+"bottom:-1px;"+"width:0.34em;"+"height:0.34em;"+"min-width:10px;"+"min-height:10px;"+"border-radius:999px;"+"border:2px solid var(--x-avatar-ring);"+"background:var(--x-avatar-status-color);"+"display:none;}"+"[part=badge]{"+"position:absolute;"+"top:-4px;"+"right:-4px;"+"display:none;}"+"slot[name=badge]::slotted(*){display:block;}"+"@media (prefers-reduced-motion:reduce){"+"[part=root]{transition:none !important;}}");
app.components.x_avatar.x_avatar.init_dom_BANG_ = (function app$components$x_avatar$x_avatar$init_dom_BANG_(el){
var root_21615 = el.attachShadow(({"mode": "open"}));
var style_21616 = document.createElement("style");
var root_el_21617 = document.createElement("div");
var img_21618 = document.createElement("img");
var initials_21619 = document.createElement("span");
var fallback_21620 = document.createElement("span");
var status_21621 = document.createElement("span");
var badge_21622 = document.createElement("span");
var badge_slot_21623 = document.createElement("slot");
(style_21616.textContent = app.components.x_avatar.x_avatar.style_text);

root_el_21617.setAttribute("part","root");

img_21618.setAttribute("part","image");

img_21618.setAttribute("alt","");

initials_21619.setAttribute("part","initials");

fallback_21620.setAttribute("part","fallback");

(fallback_21620.textContent = "?");

status_21621.setAttribute("part","status");

status_21621.setAttribute("aria-hidden","true");

badge_21622.setAttribute("part","badge");

badge_slot_21623.setAttribute("name","badge");

badge_21622.appendChild(badge_slot_21623);

root_el_21617.appendChild(img_21618);

root_el_21617.appendChild(initials_21619);

root_el_21617.appendChild(fallback_21620);

root_21615.appendChild(style_21616);

root_21615.appendChild(root_el_21617);

root_21615.appendChild(status_21621);

root_21615.appendChild(badge_21622);

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_refs,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"root-el","root-el",1068654895),root_el_21617,new cljs.core.Keyword(null,"img","img",1442687358),img_21618,new cljs.core.Keyword(null,"initials-el","initials-el",2044340724),initials_21619,new cljs.core.Keyword(null,"fallback-el","fallback-el",-208030239),fallback_21620,new cljs.core.Keyword(null,"status-el","status-el",2132204108),status_21621,new cljs.core.Keyword(null,"badge-el","badge-el",1341875403),badge_21622,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001),badge_slot_21623], null));

return null;
});
app.components.x_avatar.x_avatar.ensure_refs_BANG_ = (function app$components$x_avatar$x_avatar$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_avatar.x_avatar.init_dom_BANG_(el);

return app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_refs);
}
});
app.components.x_avatar.x_avatar.read_model = (function app$components$x_avatar$x_avatar$read_model(el){
return app.components.x_avatar.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"initials-raw","initials-raw",1031756224),new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250),new cljs.core.Keyword(null,"name-raw","name-raw",1493628068),new cljs.core.Keyword(null,"shape-raw","shape-raw",1588938757),new cljs.core.Keyword(null,"status-raw","status-raw",-2121061147),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"alt-raw","alt-raw",-1423153400),new cljs.core.Keyword(null,"src-raw","src-raw",-510472785),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423)],[el.getAttribute(app.components.x_avatar.model.attr_initials),el.getAttribute(app.components.x_avatar.model.attr_variant),el.getAttribute(app.components.x_avatar.model.attr_name),el.getAttribute(app.components.x_avatar.model.attr_shape),el.getAttribute(app.components.x_avatar.model.attr_status),el.hasAttribute(app.components.x_avatar.model.attr_disabled),el.getAttribute(app.components.x_avatar.model.attr_alt),el.getAttribute(app.components.x_avatar.model.attr_src),el.getAttribute(app.components.x_avatar.model.attr_size)]));
});
app.components.x_avatar.x_avatar.slot_has_content_QMARK_ = (function app$components$x_avatar$x_avatar$slot_has_content_QMARK_(slot_el){
if(cljs.core.truth_(slot_el)){
return (slot_el.assignedNodes().length > (0));
} else {
return null;
}
});
app.components.x_avatar.x_avatar.set_display_BANG_ = (function app$components$x_avatar$x_avatar$set_display_BANG_(node,v){
(node.style.display = v);

return null;
});
app.components.x_avatar.x_avatar.apply_model_BANG_ = (function app$components$x_avatar$x_avatar$apply_model_BANG_(el,p__21396){
var map__21397 = p__21396;
var map__21397__$1 = cljs.core.__destructure_map(map__21397);
var m = map__21397__$1;
var src = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"src","src",-1651076051));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var shape = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"shape","shape",1190694006));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21397__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var map__21398_21692 = app.components.x_avatar.x_avatar.ensure_refs_BANG_(el);
var map__21398_21693__$1 = cljs.core.__destructure_map(map__21398_21692);
var img_21695 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"img","img",1442687358));
var initials_el_21696 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"initials-el","initials-el",2044340724));
var fallback_el_21697 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"fallback-el","fallback-el",-208030239));
var status_el_21698 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"status-el","status-el",2132204108));
var badge_el_21699 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"badge-el","badge-el",1341875403));
var badge_slot_21700 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21398_21693__$1,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001));
var img_21701__$1 = img_21695;
var initials_el_21702__$1 = initials_el_21696;
var fallback_el_21703__$1 = fallback_el_21697;
var status_el_21704__$1 = status_el_21698;
var badge_el_21705__$1 = badge_el_21699;
var badge_slot_21706__$1 = badge_slot_21700;
var img_ok_QMARK__21707 = cljs.core.boolean$(app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_img_ok));
var has_src_QMARK__21708 = (!((src == null)));
var text_21709 = app.components.x_avatar.model.display_text(m);
var lbl_21710 = app.components.x_avatar.model.label(m);
var show_img_QMARK__21711 = ((has_src_QMARK__21708) && (img_ok_QMARK__21707));
var show_initials_QMARK__21712 = (((!(show_img_QMARK__21711))) && ((!((text_21709 == null)))));
var show_fallback_QMARK__21713 = (((!(show_img_QMARK__21711))) && ((!(show_initials_QMARK__21712))));
var has_badge_QMARK__21714 = app.components.x_avatar.x_avatar.slot_has_content_QMARK_(badge_slot_21706__$1);
el.setAttribute("data-size",size);

el.setAttribute("data-shape",shape);

el.setAttribute("data-variant",variant);

if(cljs.core.truth_(lbl_21710)){
el.setAttribute("role","img");

el.setAttribute("aria-label",lbl_21710);

el.removeAttribute("aria-hidden");
} else {
el.removeAttribute("role");

el.removeAttribute("aria-label");

el.setAttribute("aria-hidden","true");
}

if(cljs.core.truth_(disabled)){
el.setAttribute("aria-disabled","true");
} else {
el.removeAttribute("aria-disabled");
}

var next_21736 = (function (){var or__5142__auto__ = src;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var prev_21737 = (function (){var or__5142__auto__ = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_last_src);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(prev_21737,next_21736)){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_last_src,next_21736);

(img_21701__$1.src = next_21736);
} else {
}

app.components.x_avatar.x_avatar.set_display_BANG_(img_21701__$1,((show_img_QMARK__21711)?"block":"none"));

if(show_initials_QMARK__21712){
(initials_el_21702__$1.textContent = text_21709);
} else {
}

app.components.x_avatar.x_avatar.set_display_BANG_(initials_el_21702__$1,((show_initials_QMARK__21712)?"inline":"none"));

app.components.x_avatar.x_avatar.set_display_BANG_(fallback_el_21703__$1,((show_fallback_QMARK__21713)?"inline":"none"));

if(cljs.core.truth_(status)){
el.style.setProperty("--x-avatar-status-color",(function (){var G__21401 = status;
switch (G__21401) {
case "online":
return "var(--x-avatar-status-online)";

break;
case "busy":
return "var(--x-avatar-status-busy)";

break;
case "away":
return "var(--x-avatar-status-away)";

break;
default:
return "var(--x-avatar-status-offline)";

}
})());

app.components.x_avatar.x_avatar.set_display_BANG_(status_el_21704__$1,"block");
} else {
app.components.x_avatar.x_avatar.set_display_BANG_(status_el_21704__$1,"none");
}

app.components.x_avatar.x_avatar.set_display_BANG_(badge_el_21705__$1,(cljs.core.truth_(has_badge_QMARK__21714)?"block":"none"));

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_model,m);

return null;
});
app.components.x_avatar.x_avatar.update_from_attrs_BANG_ = (function app$components$x_avatar$x_avatar$update_from_attrs_BANG_(el){
var new_m_21763 = app.components.x_avatar.x_avatar.read_model(el);
var old_m_21764 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21764,new_m_21763)){
app.components.x_avatar.x_avatar.apply_model_BANG_(el,new_m_21763);
} else {
}

return null;
});
app.components.x_avatar.x_avatar.add_listeners_BANG_ = (function app$components$x_avatar$x_avatar$add_listeners_BANG_(el){
var map__21408_21772 = app.components.x_avatar.x_avatar.ensure_refs_BANG_(el);
var map__21408_21774__$1 = cljs.core.__destructure_map(map__21408_21772);
var img_21775 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21408_21774__$1,new cljs.core.Keyword(null,"img","img",1442687358));
var badge_slot_21776 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21408_21774__$1,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001));
var img_21777__$1 = img_21775;
var badge_slot_21778__$1 = badge_slot_21776;
var on_load_21779 = (function (_){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_img_ok,true);

return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
var on_error_21780 = (function (_){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_img_ok,false);

return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
var on_slot_21781 = (function (_){
return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
img_21777__$1.addEventListener("load",on_load_21779);

img_21777__$1.addEventListener("error",on_error_21780);

if(cljs.core.truth_(badge_slot_21778__$1)){
badge_slot_21778__$1.addEventListener("slotchange",on_slot_21781);
} else {
}

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_handlers,({"load": on_load_21779, "error": on_error_21780, "slot": on_slot_21781}));

return null;
});
app.components.x_avatar.x_avatar.remove_listeners_BANG_ = (function app$components$x_avatar$x_avatar$remove_listeners_BANG_(el){
var temp__5823__auto___21793 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_handlers);
if(cljs.core.truth_(temp__5823__auto___21793)){
var hs_21794 = temp__5823__auto___21793;
var temp__5823__auto___21796__$1 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_refs);
if(cljs.core.truth_(temp__5823__auto___21796__$1)){
var refs_21797 = temp__5823__auto___21796__$1;
var img_21798 = new cljs.core.Keyword(null,"img","img",1442687358).cljs$core$IFn$_invoke$arity$1(refs_21797);
var badge_slot_21799 = new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001).cljs$core$IFn$_invoke$arity$1(refs_21797);
var on_load_21800 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21794,"load");
var on_error_21801 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21794,"error");
var on_slot_21802 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21794,"slot");
if(cljs.core.truth_(on_load_21800)){
img_21798.removeEventListener("load",on_load_21800);
} else {
}

if(cljs.core.truth_(on_error_21801)){
img_21798.removeEventListener("error",on_error_21801);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = badge_slot_21799;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_21802;
} else {
return and__5140__auto__;
}
})())){
badge_slot_21799.removeEventListener("slotchange",on_slot_21802);
} else {
}
} else {
}
} else {
}

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_handlers,null);

return null;
});
app.components.x_avatar.x_avatar.def_string_prop_BANG_ = (function app$components$x_avatar$x_avatar$def_string_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.getAttribute(attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_avatar.x_avatar.def_string_prop_default_BANG_ = (function app$components$x_avatar$x_avatar$def_string_prop_default_BANG_(proto,attr,default$){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(attr);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default$;
}
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_avatar.x_avatar.install_property_accessors_BANG_ = (function app$components$x_avatar$x_avatar$install_property_accessors_BANG_(proto){
app.components.x_avatar.x_avatar.def_string_prop_BANG_(proto,app.components.x_avatar.model.attr_src);

app.components.x_avatar.x_avatar.def_string_prop_BANG_(proto,app.components.x_avatar.model.attr_alt);

app.components.x_avatar.x_avatar.def_string_prop_BANG_(proto,app.components.x_avatar.model.attr_name);

app.components.x_avatar.x_avatar.def_string_prop_BANG_(proto,app.components.x_avatar.model.attr_initials);

app.components.x_avatar.x_avatar.def_string_prop_default_BANG_(proto,app.components.x_avatar.model.attr_size,app.components.x_avatar.model.default_size);

app.components.x_avatar.x_avatar.def_string_prop_default_BANG_(proto,app.components.x_avatar.model.attr_shape,app.components.x_avatar.model.default_shape);

app.components.x_avatar.x_avatar.def_string_prop_default_BANG_(proto,app.components.x_avatar.model.attr_variant,app.components.x_avatar.model.default_variant);

app.components.x_avatar.x_avatar.def_string_prop_BANG_(proto,app.components.x_avatar.model.attr_status);

return Object.defineProperty(proto,app.components.x_avatar.model.attr_disabled,({"get": (function (){
var this$ = this;
return this$.hasAttribute(app.components.x_avatar.model.attr_disabled);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(app.components.x_avatar.model.attr_disabled,"");
} else {
return this$.removeAttribute(app.components.x_avatar.model.attr_disabled);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_avatar.x_avatar.element_class = (function app$components$x_avatar$x_avatar$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_avatar.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_avatar.x_avatar.ensure_refs_BANG_(this$);

app.components.x_avatar.x_avatar.remove_listeners_BANG_(this$);

app.components.x_avatar.x_avatar.add_listeners_BANG_(this$);

app.components.x_avatar.x_avatar.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_avatar.x_avatar.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (attr_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(attr_name,app.components.x_avatar.model.attr_src)){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(this$,app.components.x_avatar.x_avatar.k_img_ok,false);
} else {
}

app.components.x_avatar.x_avatar.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_avatar.x_avatar.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_avatar.x_avatar.register_BANG_ = (function app$components$x_avatar$x_avatar$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_avatar.model.tag_name))){
} else {
customElements.define(app.components.x_avatar.model.tag_name,app.components.x_avatar.x_avatar.element_class());
}

return null;
});
app.components.x_avatar.x_avatar.init_BANG_ = (function app$components$x_avatar$x_avatar$init_BANG_(){
return app.components.x_avatar.x_avatar.register_BANG_();
});

//# sourceMappingURL=app.components.x_avatar.x_avatar.js.map
