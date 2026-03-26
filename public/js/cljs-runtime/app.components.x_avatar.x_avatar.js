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
var root_21819 = el.attachShadow(({"mode": "open"}));
var style_21820 = document.createElement("style");
var root_el_21821 = document.createElement("div");
var img_21822 = document.createElement("img");
var initials_21823 = document.createElement("span");
var fallback_21824 = document.createElement("span");
var status_21825 = document.createElement("span");
var badge_21826 = document.createElement("span");
var badge_slot_21827 = document.createElement("slot");
(style_21820.textContent = app.components.x_avatar.x_avatar.style_text);

root_el_21821.setAttribute("part","root");

img_21822.setAttribute("part","image");

img_21822.setAttribute("alt","");

initials_21823.setAttribute("part","initials");

fallback_21824.setAttribute("part","fallback");

(fallback_21824.textContent = "?");

status_21825.setAttribute("part","status");

status_21825.setAttribute("aria-hidden","true");

badge_21826.setAttribute("part","badge");

badge_slot_21827.setAttribute("name","badge");

badge_21826.appendChild(badge_slot_21827);

root_el_21821.appendChild(img_21822);

root_el_21821.appendChild(initials_21823);

root_el_21821.appendChild(fallback_21824);

root_21819.appendChild(style_21820);

root_21819.appendChild(root_el_21821);

root_21819.appendChild(status_21825);

root_21819.appendChild(badge_21826);

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_refs,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"root-el","root-el",1068654895),root_el_21821,new cljs.core.Keyword(null,"img","img",1442687358),img_21822,new cljs.core.Keyword(null,"initials-el","initials-el",2044340724),initials_21823,new cljs.core.Keyword(null,"fallback-el","fallback-el",-208030239),fallback_21824,new cljs.core.Keyword(null,"status-el","status-el",2132204108),status_21825,new cljs.core.Keyword(null,"badge-el","badge-el",1341875403),badge_21826,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001),badge_slot_21827], null));

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
app.components.x_avatar.x_avatar.apply_model_BANG_ = (function app$components$x_avatar$x_avatar$apply_model_BANG_(el,p__21805){
var map__21806 = p__21805;
var map__21806__$1 = cljs.core.__destructure_map(map__21806);
var m = map__21806__$1;
var src = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"src","src",-1651076051));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var shape = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"shape","shape",1190694006));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var status = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"status","status",-1997798413));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21806__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var map__21808_21832 = app.components.x_avatar.x_avatar.ensure_refs_BANG_(el);
var map__21808_21833__$1 = cljs.core.__destructure_map(map__21808_21832);
var img_21834 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"img","img",1442687358));
var initials_el_21835 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"initials-el","initials-el",2044340724));
var fallback_el_21836 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"fallback-el","fallback-el",-208030239));
var status_el_21837 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"status-el","status-el",2132204108));
var badge_el_21838 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"badge-el","badge-el",1341875403));
var badge_slot_21839 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21808_21833__$1,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001));
var img_21840__$1 = img_21834;
var initials_el_21841__$1 = initials_el_21835;
var fallback_el_21842__$1 = fallback_el_21836;
var status_el_21843__$1 = status_el_21837;
var badge_el_21844__$1 = badge_el_21838;
var badge_slot_21845__$1 = badge_slot_21839;
var img_ok_QMARK__21846 = cljs.core.boolean$(app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_img_ok));
var has_src_QMARK__21847 = (!((src == null)));
var text_21848 = app.components.x_avatar.model.display_text(m);
var lbl_21849 = app.components.x_avatar.model.label(m);
var show_img_QMARK__21850 = ((has_src_QMARK__21847) && (img_ok_QMARK__21846));
var show_initials_QMARK__21851 = (((!(show_img_QMARK__21850))) && ((!((text_21848 == null)))));
var show_fallback_QMARK__21852 = (((!(show_img_QMARK__21850))) && ((!(show_initials_QMARK__21851))));
var has_badge_QMARK__21853 = app.components.x_avatar.x_avatar.slot_has_content_QMARK_(badge_slot_21845__$1);
el.setAttribute("data-size",size);

el.setAttribute("data-shape",shape);

el.setAttribute("data-variant",variant);

if(cljs.core.truth_(lbl_21849)){
el.setAttribute("role","img");

el.setAttribute("aria-label",lbl_21849);

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

var next_21854 = (function (){var or__5142__auto__ = src;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
var prev_21855 = (function (){var or__5142__auto__ = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_last_src);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})();
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(prev_21855,next_21854)){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_last_src,next_21854);

(img_21840__$1.src = next_21854);
} else {
}

app.components.x_avatar.x_avatar.set_display_BANG_(img_21840__$1,((show_img_QMARK__21850)?"block":"none"));

if(show_initials_QMARK__21851){
(initials_el_21841__$1.textContent = text_21848);
} else {
}

app.components.x_avatar.x_avatar.set_display_BANG_(initials_el_21841__$1,((show_initials_QMARK__21851)?"inline":"none"));

app.components.x_avatar.x_avatar.set_display_BANG_(fallback_el_21842__$1,((show_fallback_QMARK__21852)?"inline":"none"));

if(cljs.core.truth_(status)){
el.style.setProperty("--x-avatar-status-color",(function (){var G__21809 = status;
switch (G__21809) {
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

app.components.x_avatar.x_avatar.set_display_BANG_(status_el_21843__$1,"block");
} else {
app.components.x_avatar.x_avatar.set_display_BANG_(status_el_21843__$1,"none");
}

app.components.x_avatar.x_avatar.set_display_BANG_(badge_el_21844__$1,(cljs.core.truth_(has_badge_QMARK__21853)?"block":"none"));

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_model,m);

return null;
});
app.components.x_avatar.x_avatar.update_from_attrs_BANG_ = (function app$components$x_avatar$x_avatar$update_from_attrs_BANG_(el){
var new_m_21857 = app.components.x_avatar.x_avatar.read_model(el);
var old_m_21858 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21858,new_m_21857)){
app.components.x_avatar.x_avatar.apply_model_BANG_(el,new_m_21857);
} else {
}

return null;
});
app.components.x_avatar.x_avatar.add_listeners_BANG_ = (function app$components$x_avatar$x_avatar$add_listeners_BANG_(el){
var map__21810_21859 = app.components.x_avatar.x_avatar.ensure_refs_BANG_(el);
var map__21810_21860__$1 = cljs.core.__destructure_map(map__21810_21859);
var img_21861 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21810_21860__$1,new cljs.core.Keyword(null,"img","img",1442687358));
var badge_slot_21862 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21810_21860__$1,new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001));
var img_21863__$1 = img_21861;
var badge_slot_21864__$1 = badge_slot_21862;
var on_load_21865 = (function (_){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_img_ok,true);

return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
var on_error_21866 = (function (_){
app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_img_ok,false);

return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
var on_slot_21867 = (function (_){
return app.components.x_avatar.x_avatar.update_from_attrs_BANG_(el);
});
img_21863__$1.addEventListener("load",on_load_21865);

img_21863__$1.addEventListener("error",on_error_21866);

if(cljs.core.truth_(badge_slot_21864__$1)){
badge_slot_21864__$1.addEventListener("slotchange",on_slot_21867);
} else {
}

app.components.x_avatar.x_avatar.goog$module$goog$object.set(el,app.components.x_avatar.x_avatar.k_handlers,({"load": on_load_21865, "error": on_error_21866, "slot": on_slot_21867}));

return null;
});
app.components.x_avatar.x_avatar.remove_listeners_BANG_ = (function app$components$x_avatar$x_avatar$remove_listeners_BANG_(el){
var temp__5823__auto___21868 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_handlers);
if(cljs.core.truth_(temp__5823__auto___21868)){
var hs_21869 = temp__5823__auto___21868;
var temp__5823__auto___21870__$1 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(el,app.components.x_avatar.x_avatar.k_refs);
if(cljs.core.truth_(temp__5823__auto___21870__$1)){
var refs_21871 = temp__5823__auto___21870__$1;
var img_21872 = new cljs.core.Keyword(null,"img","img",1442687358).cljs$core$IFn$_invoke$arity$1(refs_21871);
var badge_slot_21873 = new cljs.core.Keyword(null,"badge-slot","badge-slot",2030670001).cljs$core$IFn$_invoke$arity$1(refs_21871);
var on_load_21874 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21869,"load");
var on_error_21875 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21869,"error");
var on_slot_21876 = app.components.x_avatar.x_avatar.goog$module$goog$object.get(hs_21869,"slot");
if(cljs.core.truth_(on_load_21874)){
img_21872.removeEventListener("load",on_load_21874);
} else {
}

if(cljs.core.truth_(on_error_21875)){
img_21872.removeEventListener("error",on_error_21875);
} else {
}

if(cljs.core.truth_((function (){var and__5140__auto__ = badge_slot_21873;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_21876;
} else {
return and__5140__auto__;
}
})())){
badge_slot_21873.removeEventListener("slotchange",on_slot_21876);
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
