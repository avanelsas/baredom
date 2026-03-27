goog.provide('app.components.x_badge.x_badge');
goog.scope(function(){
  app.components.x_badge.x_badge.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_badge.x_badge.k_refs = "__xBadgeRefs";
app.components.x_badge.x_badge.k_model = "__xBadgeModel";
app.components.x_badge.x_badge.k_handlers = "__xBadgeHandlers";
app.components.x_badge.x_badge.style_text = (""+":host{"+"display:inline-flex;"+"vertical-align:middle;"+"align-items:center;"+"justify-content:center;"+"color-scheme:light dark;"+"--x-badge-bg:rgba(0,0,0,0.08);"+"--x-badge-color:rgba(0,0,0,0.80);"+"--x-badge-border:rgba(0,0,0,0.12);"+"--x-badge-font-size:0.75rem;"+"--x-badge-height:1.25rem;"+"--x-badge-padding:0 0.375rem;"+"--x-badge-radius:0.25rem;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-badge-bg:rgba(255,255,255,0.12);"+"--x-badge-color:rgba(255,255,255,0.88);"+"--x-badge-border:rgba(255,255,255,0.14);}}"+":host([data-variant='info']){"+"--x-badge-bg:rgba(0,102,204,0.12);"+"--x-badge-color:rgba(0,60,140,0.90);"+"--x-badge-border:rgba(0,102,204,0.25);}"+":host([data-variant='success']){"+"--x-badge-bg:rgba(16,140,72,0.12);"+"--x-badge-color:rgba(0,90,45,0.90);"+"--x-badge-border:rgba(16,140,72,0.25);}"+":host([data-variant='warning']){"+"--x-badge-bg:rgba(204,120,0,0.12);"+"--x-badge-color:rgba(140,70,0,0.90);"+"--x-badge-border:rgba(204,120,0,0.25);}"+":host([data-variant='error']){"+"--x-badge-bg:rgba(190,20,40,0.12);"+"--x-badge-color:rgba(140,0,20,0.90);"+"--x-badge-border:rgba(190,20,40,0.25);}"+"@media (prefers-color-scheme:dark){"+":host([data-variant='info']){"+"--x-badge-bg:rgba(80,160,255,0.18);"+"--x-badge-color:rgba(210,235,255,0.95);"+"--x-badge-border:rgba(80,160,255,0.35);}"+":host([data-variant='success']){"+"--x-badge-bg:rgba(60,210,120,0.18);"+"--x-badge-color:rgba(200,255,230,0.95);"+"--x-badge-border:rgba(60,210,120,0.35);}"+":host([data-variant='warning']){"+"--x-badge-bg:rgba(255,190,90,0.18);"+"--x-badge-color:rgba(255,235,180,0.95);"+"--x-badge-border:rgba(255,190,90,0.35);}"+":host([data-variant='error']){"+"--x-badge-bg:rgba(255,90,110,0.18);"+"--x-badge-color:rgba(255,210,215,0.95);"+"--x-badge-border:rgba(255,90,110,0.35);}}"+":host([data-size='sm']){"+"--x-badge-font-size:0.6875rem;"+"--x-badge-height:1rem;"+"--x-badge-padding:0 0.25rem;"+"--x-badge-radius:0.1875rem;}"+"[part=base]{"+"display:inline-flex;"+"align-items:center;"+"justify-content:center;"+"min-height:var(--x-badge-height);"+"padding:var(--x-badge-padding);"+"border-radius:var(--x-badge-radius);"+"border:1px solid var(--x-badge-border);"+"background:var(--x-badge-bg);"+"color:var(--x-badge-color);"+"font-size:var(--x-badge-font-size);"+"font-weight:600;"+"line-height:1;"+"white-space:nowrap;"+"user-select:none;"+"box-sizing:border-box;}"+":host([data-pill]) [part=base]{border-radius:999px;}"+":host([data-dot]) [part=base]{"+"min-width:var(--x-badge-height);"+"padding:0;}"+":host([data-mode='dot']) [part=label],"+":host([data-mode='empty']) [part=base]{"+"display:none;}"+"slot{display:none;}"+":host([data-mode='slot']) slot{display:contents;}"+":host([data-mode='slot']) [part=label]{display:none;}"+"@media (prefers-reduced-motion:reduce){"+"[part=base]{transition:none !important;}}");
app.components.x_badge.x_badge.init_dom_BANG_ = (function app$components$x_badge$x_badge$init_dom_BANG_(el){
var root_21934 = el.attachShadow(({"mode": "open"}));
var style_21935 = document.createElement("style");
var base_21936 = document.createElement("div");
var slot_el_21937 = document.createElement("slot");
var label_el_21938 = document.createElement("span");
(style_21935.textContent = app.components.x_badge.x_badge.style_text);

base_21936.setAttribute("part","base");

base_21936.setAttribute("role","status");

label_el_21938.setAttribute("part","label");

base_21936.appendChild(slot_el_21937);

base_21936.appendChild(label_el_21938);

root_21934.appendChild(style_21935);

root_21934.appendChild(base_21936);

app.components.x_badge.x_badge.goog$module$goog$object.set(el,app.components.x_badge.x_badge.k_refs,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"base","base",185279322),base_21936,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_21937,new cljs.core.Keyword(null,"label-el","label-el",-356062007),label_el_21938], null));

return null;
});
app.components.x_badge.x_badge.ensure_refs_BANG_ = (function app$components$x_badge$x_badge$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_badge.x_badge.goog$module$goog$object.get(el,app.components.x_badge.x_badge.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_badge.x_badge.init_dom_BANG_(el);

return app.components.x_badge.x_badge.goog$module$goog$object.get(el,app.components.x_badge.x_badge.k_refs);
}
});
app.components.x_badge.x_badge.slot_has_content_QMARK_ = (function app$components$x_badge$x_badge$slot_has_content_QMARK_(slot_el){
if(cljs.core.truth_(slot_el)){
return (slot_el.assignedNodes().length > (0));
} else {
return null;
}
});
app.components.x_badge.x_badge.read_model = (function app$components$x_badge$x_badge$read_model(el){
var map__21927 = app.components.x_badge.x_badge.ensure_refs_BANG_(el);
var map__21927__$1 = cljs.core.__destructure_map(map__21927);
var slot_el = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21927__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
return app.components.x_badge.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250),new cljs.core.Keyword(null,"text-raw","text-raw",-124751068),new cljs.core.Keyword(null,"has-slot?","has-slot?",181244965),new cljs.core.Keyword(null,"max-raw","max-raw",-434946611),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),new cljs.core.Keyword(null,"count-raw","count-raw",-952861839),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"dot-raw","dot-raw",1754908187),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860),new cljs.core.Keyword(null,"pill-raw","pill-raw",-557846051)],[el.getAttribute(app.components.x_badge.model.attr_variant),el.getAttribute(app.components.x_badge.model.attr_text),app.components.x_badge.x_badge.slot_has_content_QMARK_(slot_el),el.getAttribute(app.components.x_badge.model.attr_max),el.getAttribute(app.components.x_badge.model.attr_size),el.getAttribute(app.components.x_badge.model.attr_count),el.getAttribute(app.components.x_badge.model.attr_aria_label),el.getAttribute(app.components.x_badge.model.attr_dot),el.getAttribute(app.components.x_badge.model.attr_aria_describedby),el.getAttribute(app.components.x_badge.model.attr_pill)]));
});
app.components.x_badge.x_badge.apply_model_BANG_ = (function app$components$x_badge$x_badge$apply_model_BANG_(el,p__21928){
var map__21929 = p__21928;
var map__21929__$1 = cljs.core.__destructure_map(map__21929);
var m = map__21929__$1;
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var pill = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"pill","pill",-37707000));
var dot = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"dot","dot",1442709401));
var aria_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var aria_describedby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21929__$1,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471));
var map__21930_21939 = app.components.x_badge.x_badge.ensure_refs_BANG_(el);
var map__21930_21940__$1 = cljs.core.__destructure_map(map__21930_21939);
var base_21941 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21930_21940__$1,new cljs.core.Keyword(null,"base","base",185279322));
var label_el_21942 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21930_21940__$1,new cljs.core.Keyword(null,"label-el","label-el",-356062007));
var base_21943__$1 = base_21941;
var label_el_21944__$1 = label_el_21942;
var mode_21945 = app.components.x_badge.model.compute_mode(m);
var txt_21946 = app.components.x_badge.model.display_text(m);
el.setAttribute("data-variant",variant);

el.setAttribute("data-size",size);

el.setAttribute("data-mode",cljs.core.name(mode_21945));

if(cljs.core.truth_(pill)){
el.setAttribute("data-pill","");
} else {
el.removeAttribute("data-pill");
}

if(cljs.core.truth_(dot)){
el.setAttribute("data-dot","");
} else {
el.removeAttribute("data-dot");
}

(label_el_21944__$1.textContent = (function (){var or__5142__auto__ = txt_21946;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "";
}
})());

if(cljs.core.truth_(aria_label)){
base_21943__$1.setAttribute("aria-label",aria_label);
} else {
base_21943__$1.removeAttribute("aria-label");
}

if(cljs.core.truth_(aria_describedby)){
base_21943__$1.setAttribute("aria-describedby",aria_describedby);
} else {
base_21943__$1.removeAttribute("aria-describedby");
}

app.components.x_badge.x_badge.goog$module$goog$object.set(el,app.components.x_badge.x_badge.k_model,m);

return null;
});
app.components.x_badge.x_badge.update_from_attrs_BANG_ = (function app$components$x_badge$x_badge$update_from_attrs_BANG_(el){
var new_m_21947 = app.components.x_badge.x_badge.read_model(el);
var old_m_21948 = app.components.x_badge.x_badge.goog$module$goog$object.get(el,app.components.x_badge.x_badge.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21948,new_m_21947)){
app.components.x_badge.x_badge.apply_model_BANG_(el,new_m_21947);
} else {
}

return null;
});
app.components.x_badge.x_badge.add_listeners_BANG_ = (function app$components$x_badge$x_badge$add_listeners_BANG_(el){
var map__21931_21949 = app.components.x_badge.x_badge.ensure_refs_BANG_(el);
var map__21931_21950__$1 = cljs.core.__destructure_map(map__21931_21949);
var slot_el_21951 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21931_21950__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var slot_el_21952__$1 = slot_el_21951;
var on_slot_21953 = (function (_){
return app.components.x_badge.x_badge.update_from_attrs_BANG_(el);
});
if(cljs.core.truth_(slot_el_21952__$1)){
slot_el_21952__$1.addEventListener("slotchange",on_slot_21953);
} else {
}

app.components.x_badge.x_badge.goog$module$goog$object.set(el,app.components.x_badge.x_badge.k_handlers,({"slot": on_slot_21953}));

return null;
});
app.components.x_badge.x_badge.remove_listeners_BANG_ = (function app$components$x_badge$x_badge$remove_listeners_BANG_(el){
var temp__5823__auto___21954 = app.components.x_badge.x_badge.goog$module$goog$object.get(el,app.components.x_badge.x_badge.k_handlers);
if(cljs.core.truth_(temp__5823__auto___21954)){
var hs_21955 = temp__5823__auto___21954;
var temp__5823__auto___21956__$1 = app.components.x_badge.x_badge.goog$module$goog$object.get(el,app.components.x_badge.x_badge.k_refs);
if(cljs.core.truth_(temp__5823__auto___21956__$1)){
var refs_21957 = temp__5823__auto___21956__$1;
var slot_el_21958 = new cljs.core.Keyword(null,"slot-el","slot-el",1985374132).cljs$core$IFn$_invoke$arity$1(refs_21957);
var on_slot_21959 = app.components.x_badge.x_badge.goog$module$goog$object.get(hs_21955,"slot");
if(cljs.core.truth_((function (){var and__5140__auto__ = slot_el_21958;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_21959;
} else {
return and__5140__auto__;
}
})())){
slot_el_21958.removeEventListener("slotchange",on_slot_21959);
} else {
}
} else {
}
} else {
}

app.components.x_badge.x_badge.goog$module$goog$object.set(el,app.components.x_badge.x_badge.k_handlers,null);

return null;
});
app.components.x_badge.x_badge.def_string_prop_BANG_ = (function app$components$x_badge$x_badge$def_string_prop_BANG_(proto,attr){
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
app.components.x_badge.x_badge.def_string_prop_default_BANG_ = (function app$components$x_badge$x_badge$def_string_prop_default_BANG_(proto,attr,default$){
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
app.components.x_badge.x_badge.def_bool_prop_BANG_ = (function app$components$x_badge$x_badge$def_bool_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.hasAttribute(attr);
}), "set": (function (v){
var this$ = this;
if(cljs.core.truth_(v)){
return this$.setAttribute(attr,"");
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_badge.x_badge.def_int_prop_BANG_ = (function app$components$x_badge$x_badge$def_int_prop_BANG_(proto,attr,default$){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return app.components.x_badge.model.parse_int_attr(this$.getAttribute(attr),default$);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_badge.x_badge.install_property_accessors_BANG_ = (function app$components$x_badge$x_badge$install_property_accessors_BANG_(proto){
app.components.x_badge.x_badge.def_string_prop_default_BANG_(proto,app.components.x_badge.model.attr_variant,app.components.x_badge.model.default_variant);

app.components.x_badge.x_badge.def_string_prop_default_BANG_(proto,app.components.x_badge.model.attr_size,app.components.x_badge.model.default_size);

app.components.x_badge.x_badge.def_bool_prop_BANG_(proto,app.components.x_badge.model.attr_pill);

app.components.x_badge.x_badge.def_bool_prop_BANG_(proto,app.components.x_badge.model.attr_dot);

app.components.x_badge.x_badge.def_string_prop_BANG_(proto,app.components.x_badge.model.attr_text);

app.components.x_badge.x_badge.def_string_prop_BANG_(proto,app.components.x_badge.model.attr_aria_label);

app.components.x_badge.x_badge.def_string_prop_BANG_(proto,app.components.x_badge.model.attr_aria_describedby);

app.components.x_badge.x_badge.def_int_prop_BANG_(proto,app.components.x_badge.model.attr_count,null);

app.components.x_badge.x_badge.def_int_prop_BANG_(proto,app.components.x_badge.model.attr_max,app.components.x_badge.model.default_max);

return Object.defineProperty(proto,"displayText",({"get": (function (){
var this$ = this;
return app.components.x_badge.model.display_text(app.components.x_badge.x_badge.read_model(this$));
}), "enumerable": true, "configurable": true}));
});
app.components.x_badge.x_badge.element_class = (function app$components$x_badge$x_badge$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_badge.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_badge.x_badge.ensure_refs_BANG_(this$);

app.components.x_badge.x_badge.remove_listeners_BANG_(this$);

app.components.x_badge.x_badge.add_listeners_BANG_(this$);

app.components.x_badge.x_badge.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_badge.x_badge.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_attr,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_badge.x_badge.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_badge.x_badge.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_badge.x_badge.register_BANG_ = (function app$components$x_badge$x_badge$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_badge.model.tag_name))){
} else {
customElements.define(app.components.x_badge.model.tag_name,app.components.x_badge.x_badge.element_class());
}

return null;
});
app.components.x_badge.x_badge.init_BANG_ = (function app$components$x_badge$x_badge$init_BANG_(){
return app.components.x_badge.x_badge.register_BANG_();
});

//# sourceMappingURL=app.components.x_badge.x_badge.js.map
