goog.provide('app.components.x_divider.x_divider');
goog.scope(function(){
  app.components.x_divider.x_divider.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_divider.x_divider.k_refs = "__xDividerRefs";
app.components.x_divider.x_divider.k_handlers = "__xDividerHandlers";
app.components.x_divider.x_divider.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-divider-color:rgba(0,0,0,0.12);"+"--x-divider-thickness:1px;"+"--x-divider-inset:0px;"+"--x-divider-length:auto;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-divider-color:rgba(255,255,255,0.15);}}"+"[part=container]{"+"display:flex;"+"align-items:center;"+"width:var(--x-divider-length,auto);"+"box-sizing:border-box;}"+":host([data-orientation='vertical']) [part=container]{"+"flex-direction:column;"+"height:var(--x-divider-length,auto);"+"width:auto;}"+"[part=line],"+"[part=line-left],"+"[part=line-right]{"+"flex:1;"+"border:none;"+"background:var(--x-divider-color,rgba(0,0,0,0.12));"+"height:var(--x-divider-thickness,1px);"+"margin:var(--x-divider-inset,0px) 0;}"+":host([data-orientation='vertical']) [part=line],"+":host([data-orientation='vertical']) [part=line-left],"+":host([data-orientation='vertical']) [part=line-right]{"+"width:var(--x-divider-thickness,1px);"+"height:auto;"+"margin:0 var(--x-divider-inset,0px);}"+":host([data-variant='dashed']) [part=line],"+":host([data-variant='dashed']) [part=line-left],"+":host([data-variant='dashed']) [part=line-right]{"+"background:none;"+"height:0;"+"border-top:var(--x-divider-thickness,1px) dashed var(--x-divider-color,rgba(0,0,0,0.12));}"+":host([data-orientation='vertical'][data-variant='dashed']) [part=line],"+":host([data-orientation='vertical'][data-variant='dashed']) [part=line-left],"+":host([data-orientation='vertical'][data-variant='dashed']) [part=line-right]{"+"border-top:none;"+"border-left:var(--x-divider-thickness,1px) dashed var(--x-divider-color,rgba(0,0,0,0.12));"+"width:0;"+"height:auto;}"+":host([data-variant='dotted']) [part=line],"+":host([data-variant='dotted']) [part=line-left],"+":host([data-variant='dotted']) [part=line-right]{"+"background:none;"+"height:0;"+"border-top:var(--x-divider-thickness,1px) dotted var(--x-divider-color,rgba(0,0,0,0.12));}"+":host([data-orientation='vertical'][data-variant='dotted']) [part=line],"+":host([data-orientation='vertical'][data-variant='dotted']) [part=line-left],"+":host([data-orientation='vertical'][data-variant='dotted']) [part=line-right]{"+"border-top:none;"+"border-left:var(--x-divider-thickness,1px) dotted var(--x-divider-color,rgba(0,0,0,0.12));"+"width:0;"+"height:auto;}"+"[part=label]{"+"display:flex;"+"align-items:center;"+"flex-shrink:0;"+"padding:0 0.75em;"+"font-size:0.75rem;"+"font-weight:500;"+"color:var(--x-divider-color);"+"white-space:nowrap;}"+":host([data-orientation='vertical']) [part=label]{"+"padding:0.75em 0;"+"writing-mode:horizontal-tb;}"+"[part=label-text]{"+"display:inline;}"+":host([data-align='start']) [part=line-left]{"+"flex:0 0 1rem;}"+":host([data-align='end']) [part=line-right]{"+"flex:0 0 1rem;}"+"@media (prefers-reduced-motion:reduce){"+"*{transition:none !important;animation:none !important;}}");
app.components.x_divider.x_divider.make_el = (function app$components$x_divider$x_divider$make_el(tag){
return document.createElement(tag);
});
app.components.x_divider.x_divider.set_attr_BANG_ = (function app$components$x_divider$x_divider$set_attr_BANG_(el,k,v){
return el.setAttribute(k,v);
});
app.components.x_divider.x_divider.make_shadow_BANG_ = (function app$components$x_divider$x_divider$make_shadow_BANG_(el){
var root_22564 = el.attachShadow(({"mode": "open"}));
var style_22565 = app.components.x_divider.x_divider.make_el("style");
var container_22566 = app.components.x_divider.x_divider.make_el("div");
(style_22565.textContent = app.components.x_divider.x_divider.style_text);

app.components.x_divider.x_divider.set_attr_BANG_(container_22566,"part","container");

var line_22567 = app.components.x_divider.x_divider.make_el("div");
app.components.x_divider.x_divider.set_attr_BANG_(line_22567,"part","line");

container_22566.appendChild(line_22567);

root_22564.appendChild(style_22565);

root_22564.appendChild(container_22566);

app.components.x_divider.x_divider.goog$module$goog$object.set(el,app.components.x_divider.x_divider.k_refs,({"root": root_22564, "container": container_22566, "mode": "no-label"}));

return null;
});
app.components.x_divider.x_divider.remove_all_children_BANG_ = (function app$components$x_divider$x_divider$remove_all_children_BANG_(parent){
while(true){
var temp__5823__auto__ = parent.firstChild;
if(cljs.core.truth_(temp__5823__auto__)){
var child = temp__5823__auto__;
parent.removeChild(child);

continue;
} else {
return null;
}
break;
}
});
app.components.x_divider.x_divider.ensure_label_dom_BANG_ = (function app$components$x_divider$x_divider$ensure_label_dom_BANG_(el){

var refs = app.components.x_divider.x_divider.goog$module$goog$object.get(el,app.components.x_divider.x_divider.k_refs);
var cur_mode = app.components.x_divider.x_divider.goog$module$goog$object.get(refs,"mode");
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cur_mode,"no-label")){
var container_22568 = app.components.x_divider.x_divider.goog$module$goog$object.get(refs,"container");
app.components.x_divider.x_divider.remove_all_children_BANG_(container_22568);

var line_left_22569 = app.components.x_divider.x_divider.make_el("div");
var label_wrap_22570 = app.components.x_divider.x_divider.make_el("span");
var slot_el_22571 = app.components.x_divider.x_divider.make_el("slot");
var label_text_22572 = app.components.x_divider.x_divider.make_el("span");
var line_right_22573 = app.components.x_divider.x_divider.make_el("div");
app.components.x_divider.x_divider.set_attr_BANG_(line_left_22569,"part","line-left");

app.components.x_divider.x_divider.set_attr_BANG_(label_wrap_22570,"part","label");

app.components.x_divider.x_divider.set_attr_BANG_(slot_el_22571,"name","label");

app.components.x_divider.x_divider.set_attr_BANG_(label_text_22572,"part","label-text");

app.components.x_divider.x_divider.set_attr_BANG_(line_right_22573,"part","line-right");

label_wrap_22570.appendChild(slot_el_22571);

label_wrap_22570.appendChild(label_text_22572);

container_22568.appendChild(line_left_22569);

container_22568.appendChild(label_wrap_22570);

container_22568.appendChild(line_right_22573);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"line-left",line_left_22569);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"label-wrap",label_wrap_22570);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"label-text",label_text_22572);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"line-right",line_right_22573);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"mode","label");
} else {
}

return null;
});
app.components.x_divider.x_divider.ensure_no_label_dom_BANG_ = (function app$components$x_divider$x_divider$ensure_no_label_dom_BANG_(el){

var refs = app.components.x_divider.x_divider.goog$module$goog$object.get(el,app.components.x_divider.x_divider.k_refs);
var cur_mode = app.components.x_divider.x_divider.goog$module$goog$object.get(refs,"mode");
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cur_mode,"label")){
var container_22574 = app.components.x_divider.x_divider.goog$module$goog$object.get(refs,"container");
app.components.x_divider.x_divider.remove_all_children_BANG_(container_22574);

var line_22575 = app.components.x_divider.x_divider.make_el("div");
app.components.x_divider.x_divider.set_attr_BANG_(line_22575,"part","line");

container_22574.appendChild(line_22575);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"line-left",null);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"label-wrap",null);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"label-text",null);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"line-right",null);

app.components.x_divider.x_divider.goog$module$goog$object.set(refs,"mode","no-label");
} else {
}

return null;
});
app.components.x_divider.x_divider.render_BANG_ = (function app$components$x_divider$x_divider$render_BANG_(el){
var m_22576 = app.components.x_divider.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),new cljs.core.Keyword(null,"align-raw","align-raw",-723387357),new cljs.core.Keyword(null,"length-raw","length-raw",-1832244116),new cljs.core.Keyword(null,"thickness-raw","thickness-raw",1843812655),new cljs.core.Keyword(null,"color-raw","color-raw",1277647215),new cljs.core.Keyword(null,"inset-raw","inset-raw",89351124),new cljs.core.Keyword(null,"orientation-raw","orientation-raw",-471053928),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"role-raw","role-raw",777951354)],[el.getAttribute(app.components.x_divider.model.attr_variant),el.getAttribute(app.components.x_divider.model.attr_label),el.getAttribute(app.components.x_divider.model.attr_align),el.getAttribute(app.components.x_divider.model.attr_length),el.getAttribute(app.components.x_divider.model.attr_thickness),el.getAttribute(app.components.x_divider.model.attr_color),el.getAttribute(app.components.x_divider.model.attr_inset),el.getAttribute(app.components.x_divider.model.attr_orientation),el.getAttribute(app.components.x_divider.model.attr_aria_label),el.getAttribute(app.components.x_divider.model.attr_role)]));
var map__22559_22577 = m_22576;
var map__22559_22578__$1 = cljs.core.__destructure_map(map__22559_22577);
var aria_label_22579 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var align_22580 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"align","align",1964212802));
var thickness_22581 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"thickness","thickness",-940175454));
var inset_22582 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"inset","inset",-396367740));
var color_22583 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"color","color",1011675173));
var variant_22584 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var orientation_22585 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"orientation","orientation",623557579));
var label_22586 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var length_22587 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__22559_22578__$1,new cljs.core.Keyword(null,"length","length",588987862));
var has_lbl_22588 = app.components.x_divider.model.has_label_QMARK_(label_22586);
var style_22589 = el.style;
el.setAttribute("data-orientation",orientation_22585);

el.setAttribute("data-variant",variant_22584);

el.setAttribute("data-align",align_22580);

style_22589.setProperty("--x-divider-thickness",(function (){var or__5142__auto__ = thickness_22581;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "1px";
}
})());

style_22589.setProperty("--x-divider-inset",(function (){var or__5142__auto__ = inset_22582;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "0px";
}
})());

style_22589.setProperty("--x-divider-length",(function (){var or__5142__auto__ = length_22587;
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return "auto";
}
})());

if(cljs.core.truth_(color_22583)){
style_22589.setProperty("--x-divider-color",color_22583);
} else {
style_22589.removeProperty("--x-divider-color");
}

if(app.components.x_divider.model.separator_role_QMARK_(new cljs.core.Keyword(null,"role","role",-736691072).cljs$core$IFn$_invoke$arity$1(m_22576))){
el.setAttribute("role","separator");
} else {
el.setAttribute("role","presentation");
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(orientation_22585,"vertical")){
el.setAttribute("aria-orientation","vertical");
} else {
el.removeAttribute("aria-orientation");
}

if(cljs.core.truth_((function (){var and__5140__auto__ = aria_label_22579;
if(cljs.core.truth_(and__5140__auto__)){
return (aria_label_22579.length > (0));
} else {
return and__5140__auto__;
}
})())){
el.setAttribute("aria-label",aria_label_22579);
} else {
el.removeAttribute("aria-label");
}

if(has_lbl_22588){
app.components.x_divider.x_divider.ensure_label_dom_BANG_(el);

var refs_22590 = app.components.x_divider.x_divider.goog$module$goog$object.get(el,app.components.x_divider.x_divider.k_refs);
var label_text_22591 = app.components.x_divider.x_divider.goog$module$goog$object.get(refs_22590,"label-text");
if(cljs.core.truth_(label_text_22591)){
(label_text_22591.textContent = label_22586);
} else {
}
} else {
app.components.x_divider.x_divider.ensure_no_label_dom_BANG_(el);
}

return null;
});
app.components.x_divider.x_divider.add_listeners_BANG_ = (function app$components$x_divider$x_divider$add_listeners_BANG_(_el){
return null;
});
app.components.x_divider.x_divider.remove_listeners_BANG_ = (function app$components$x_divider$x_divider$remove_listeners_BANG_(_el){
return null;
});
app.components.x_divider.x_divider.connected_BANG_ = (function app$components$x_divider$x_divider$connected_BANG_(el){
if(cljs.core.truth_(app.components.x_divider.x_divider.goog$module$goog$object.get(el,app.components.x_divider.x_divider.k_refs))){
} else {
app.components.x_divider.x_divider.make_shadow_BANG_(el);
}

app.components.x_divider.x_divider.remove_listeners_BANG_(el);

app.components.x_divider.x_divider.add_listeners_BANG_(el);

return app.components.x_divider.x_divider.render_BANG_(el);
});
app.components.x_divider.x_divider.disconnected_BANG_ = (function app$components$x_divider$x_divider$disconnected_BANG_(el){
return app.components.x_divider.x_divider.remove_listeners_BANG_(el);
});
app.components.x_divider.x_divider.attribute_changed_BANG_ = (function app$components$x_divider$x_divider$attribute_changed_BANG_(el,_name,old_val,new_val){
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
if(cljs.core.truth_(app.components.x_divider.x_divider.goog$module$goog$object.get(el,app.components.x_divider.x_divider.k_refs))){
return app.components.x_divider.x_divider.render_BANG_(el);
} else {
return null;
}
} else {
return null;
}
});
app.components.x_divider.x_divider.def_string_prop_BANG_ = (function app$components$x_divider$x_divider$def_string_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return this$.getAttribute(attr);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_divider.x_divider.def_string_prop_default_BANG_ = (function app$components$x_divider$x_divider$def_string_prop_default_BANG_(proto,attr,default_val){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
var or__5142__auto__ = this$.getAttribute(attr);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
return default_val;
}
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_divider.x_divider.install_property_accessors_BANG_ = (function app$components$x_divider$x_divider$install_property_accessors_BANG_(proto){
app.components.x_divider.x_divider.def_string_prop_default_BANG_(proto,app.components.x_divider.model.attr_orientation,app.components.x_divider.model.default_orientation);

app.components.x_divider.x_divider.def_string_prop_default_BANG_(proto,app.components.x_divider.model.attr_variant,app.components.x_divider.model.default_variant);

app.components.x_divider.x_divider.def_string_prop_default_BANG_(proto,app.components.x_divider.model.attr_align,app.components.x_divider.model.default_align);

app.components.x_divider.x_divider.def_string_prop_BANG_(proto,app.components.x_divider.model.attr_label);

app.components.x_divider.x_divider.def_string_prop_BANG_(proto,app.components.x_divider.model.attr_thickness);

app.components.x_divider.x_divider.def_string_prop_BANG_(proto,app.components.x_divider.model.attr_color);

app.components.x_divider.x_divider.def_string_prop_BANG_(proto,app.components.x_divider.model.attr_inset);

return app.components.x_divider.x_divider.def_string_prop_BANG_(proto,app.components.x_divider.model.attr_length);
});
app.components.x_divider.x_divider.make_constructor = (function app$components$x_divider$x_divider$make_constructor(){
var ctor_ref = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(null);
var ctor = (function (){
return Reflect.construct(HTMLElement,[],cljs.core.deref(ctor_ref));
});
cljs.core.reset_BANG_(ctor_ref,ctor);

return ctor;
});
app.components.x_divider.x_divider.init_BANG_ = (function app$components$x_divider$x_divider$init_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_divider.model.tag_name))){
return null;
} else {
var proto = Object.create(HTMLElement.prototype);
var ctor = app.components.x_divider.x_divider.make_constructor();
Object.setPrototypeOf(ctor,HTMLElement);

(proto["constructor"] = ctor);

app.components.x_divider.x_divider.install_property_accessors_BANG_(proto);

(proto["connectedCallback"] = (function (){
var this$ = this;
return app.components.x_divider.x_divider.connected_BANG_(this$);
}));

(proto["disconnectedCallback"] = (function (){
var this$ = this;
return app.components.x_divider.x_divider.disconnected_BANG_(this$);
}));

(proto["attributeChangedCallback"] = (function (n,o,v){
var this$ = this;
return app.components.x_divider.x_divider.attribute_changed_BANG_(this$,n,o,v);
}));

(ctor["observedAttributes"] = app.components.x_divider.model.observed_attributes);

(ctor["prototype"] = proto);

return customElements.define(app.components.x_divider.model.tag_name,ctor);
}
});

//# sourceMappingURL=app.components.x_divider.x_divider.js.map
