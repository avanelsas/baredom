goog.provide('app.components.x_avatar_group.x_avatar_group');
goog.scope(function(){
  app.components.x_avatar_group.x_avatar_group.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_avatar_group.x_avatar_group.k_refs = "__xAvatarGroupRefs";
app.components.x_avatar_group.x_avatar_group.k_model = "__xAvatarGroupModel";
app.components.x_avatar_group.x_avatar_group.k_handlers = "__xAvatarGroupHandlers";
app.components.x_avatar_group.x_avatar_group.style_text = (""+":host{"+"display:inline-flex;"+"align-items:center;"+"flex-direction:row;"+"color-scheme:light dark;"+"--x-avatar-group-overflow-bg:rgba(0,0,0,0.08);"+"--x-avatar-group-overflow-color:rgba(0,0,0,0.70);"+"--x-avatar-group-overflow-border:rgba(0,0,0,0.14);"+"--x-avatar-group-overflow-ring:#ffffff;"+"--x-avatar-group-size:32px;"+"--x-avatar-group-font-size:0.75rem;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-avatar-group-overflow-bg:rgba(255,255,255,0.10);"+"--x-avatar-group-overflow-color:rgba(255,255,255,0.75);"+"--x-avatar-group-overflow-border:rgba(255,255,255,0.18);"+"--x-avatar-group-overflow-ring:#0b0c0f;}}"+":host([data-size='xs']){--x-avatar-group-size:20px;--x-avatar-group-font-size:0.625rem;}"+":host([data-size='sm']){--x-avatar-group-size:24px;--x-avatar-group-font-size:0.6875rem;}"+":host([data-size='md']){--x-avatar-group-size:32px;--x-avatar-group-font-size:0.75rem;}"+":host([data-size='lg']){--x-avatar-group-size:40px;--x-avatar-group-font-size:0.875rem;}"+":host([data-size='xl']){--x-avatar-group-size:48px;--x-avatar-group-font-size:1rem;}"+":host([data-direction='rtl']){flex-direction:row-reverse;}"+"[part=overflow]{"+"display:none;"+"flex-shrink:0;"+"width:var(--x-avatar-group-size);"+"height:var(--x-avatar-group-size);"+"border-radius:999px;"+"background:var(--x-avatar-group-overflow-bg);"+"color:var(--x-avatar-group-overflow-color);"+"border:1px solid var(--x-avatar-group-overflow-border);"+"box-shadow:0 0 0 2px var(--x-avatar-group-overflow-ring);"+"font-size:var(--x-avatar-group-font-size);"+"font-weight:600;"+"line-height:1;"+"align-items:center;"+"justify-content:center;"+"user-select:none;"+"box-sizing:border-box;"+"white-space:nowrap;}"+"::slotted(x-avatar){flex-shrink:0;}"+"@media (prefers-reduced-motion:reduce){"+"::slotted(x-avatar){transition:none !important;}}");
app.components.x_avatar_group.x_avatar_group.init_dom_BANG_ = (function app$components$x_avatar_group$x_avatar_group$init_dom_BANG_(el){
var root_21872 = el.attachShadow(({"mode": "open"}));
var style_21873 = document.createElement("style");
var slot_el_21874 = document.createElement("slot");
var overflow_21875 = document.createElement("span");
(style_21873.textContent = app.components.x_avatar_group.x_avatar_group.style_text);

overflow_21875.setAttribute("part","overflow");

overflow_21875.setAttribute("aria-hidden","true");

root_21872.appendChild(style_21873);

root_21872.appendChild(slot_el_21874);

root_21872.appendChild(overflow_21875);

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_refs,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_21874,new cljs.core.Keyword(null,"overflow","overflow",2058931880),overflow_21875], null));

return null;
});
app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_ = (function app$components$x_avatar_group$x_avatar_group$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_avatar_group.x_avatar_group.init_dom_BANG_(el);

return app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_refs);
}
});
app.components.x_avatar_group.x_avatar_group.read_model = (function app$components$x_avatar_group$x_avatar_group$read_model(el){
return app.components.x_avatar_group.model.normalize(new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),el.getAttribute(app.components.x_avatar_group.model.attr_size),new cljs.core.Keyword(null,"overlap-raw","overlap-raw",1202229114),el.getAttribute(app.components.x_avatar_group.model.attr_overlap),new cljs.core.Keyword(null,"max-raw","max-raw",-434946611),el.getAttribute(app.components.x_avatar_group.model.attr_max),new cljs.core.Keyword(null,"direction-raw","direction-raw",560730236),el.getAttribute(app.components.x_avatar_group.model.attr_direction),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),el.hasAttribute(app.components.x_avatar_group.model.attr_disabled),new cljs.core.Keyword(null,"label-raw","label-raw",-83844350),el.getAttribute(app.components.x_avatar_group.model.attr_label)], null));
});
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_ = (function app$components$x_avatar_group$x_avatar_group$apply_layout_BANG_(el,p__21771){
var map__21783 = p__21771;
var map__21783__$1 = cljs.core.__destructure_map(map__21783);
var m = map__21783__$1;
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var overlap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"overlap","overlap",-1673335644));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"max","max",61366548));
var direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"direction","direction",-633359395));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21783__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var map__21814_21876 = app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_(el);
var map__21814_21877__$1 = cljs.core.__destructure_map(map__21814_21876);
var slot_el_21878 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21814_21877__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var overflow_21879 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21814_21877__$1,new cljs.core.Keyword(null,"overflow","overflow",2058931880));
var slot_el_21880__$1 = slot_el_21878;
var overflow_21881__$1 = overflow_21879;
var children_21882 = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(slot_el_21880__$1.assignedElements());
var total_21883 = cljs.core.count(children_21882);
var vec__21815_21884 = app.components.x_avatar_group.model.compute_visible_hidden(total_21883,max);
var vis_count_21885 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21815_21884,(0),null);
var hidden_count_21886 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21815_21884,(1),null);
var margin_21887 = cljs.core.get.cljs$core$IFn$_invoke$arity$3(app.components.x_avatar_group.model.overlap_margin,overlap,"0px");
el.setAttribute("data-size",size);

el.setAttribute("data-overlap",overlap);

el.setAttribute("data-direction",direction);

if(cljs.core.truth_(label)){
el.setAttribute("role","group");

el.setAttribute("aria-label",label);
} else {
el.setAttribute("role","group");

el.removeAttribute("aria-label");
}

var seq__21848_21888 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$3(cljs.core.vector,children_21882,cljs.core.range.cljs$core$IFn$_invoke$arity$0()));
var chunk__21849_21889 = null;
var count__21850_21890 = (0);
var i__21851_21891 = (0);
while(true){
if((i__21851_21891 < count__21850_21890)){
var vec__21858_21892 = chunk__21849_21889.cljs$core$IIndexed$_nth$arity$2(null,i__21851_21891);
var child_21893 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21858_21892,(0),null);
var idx_21894 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21858_21892,(1),null);
if((idx_21894 < vis_count_21885)){
(child_21893.style.display = "");

child_21893.setAttribute("size",size);

if(cljs.core.truth_(disabled)){
child_21893.setAttribute("disabled","");
} else {
child_21893.removeAttribute("disabled");
}

(child_21893.style.marginInlineStart = (((idx_21894 > (0)))?margin_21887:"0px"));
} else {
(child_21893.style.display = "none");
}


var G__21895 = seq__21848_21888;
var G__21896 = chunk__21849_21889;
var G__21897 = count__21850_21890;
var G__21898 = (i__21851_21891 + (1));
seq__21848_21888 = G__21895;
chunk__21849_21889 = G__21896;
count__21850_21890 = G__21897;
i__21851_21891 = G__21898;
continue;
} else {
var temp__5823__auto___21899 = cljs.core.seq(seq__21848_21888);
if(temp__5823__auto___21899){
var seq__21848_21900__$1 = temp__5823__auto___21899;
if(cljs.core.chunked_seq_QMARK_(seq__21848_21900__$1)){
var c__5673__auto___21901 = cljs.core.chunk_first(seq__21848_21900__$1);
var G__21902 = cljs.core.chunk_rest(seq__21848_21900__$1);
var G__21903 = c__5673__auto___21901;
var G__21904 = cljs.core.count(c__5673__auto___21901);
var G__21905 = (0);
seq__21848_21888 = G__21902;
chunk__21849_21889 = G__21903;
count__21850_21890 = G__21904;
i__21851_21891 = G__21905;
continue;
} else {
var vec__21863_21906 = cljs.core.first(seq__21848_21900__$1);
var child_21907 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21863_21906,(0),null);
var idx_21908 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21863_21906,(1),null);
if((idx_21908 < vis_count_21885)){
(child_21907.style.display = "");

child_21907.setAttribute("size",size);

if(cljs.core.truth_(disabled)){
child_21907.setAttribute("disabled","");
} else {
child_21907.removeAttribute("disabled");
}

(child_21907.style.marginInlineStart = (((idx_21908 > (0)))?margin_21887:"0px"));
} else {
(child_21907.style.display = "none");
}


var G__21909 = cljs.core.next(seq__21848_21900__$1);
var G__21910 = null;
var G__21911 = (0);
var G__21912 = (0);
seq__21848_21888 = G__21909;
chunk__21849_21889 = G__21910;
count__21850_21890 = G__21911;
i__21851_21891 = G__21912;
continue;
}
} else {
}
}
break;
}

if((hidden_count_21886 > (0))){
(overflow_21881__$1.textContent = (""+"+"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(hidden_count_21886)));

overflow_21881__$1.setAttribute("role","img");

overflow_21881__$1.setAttribute("aria-label",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(hidden_count_21886)+" more"));

(overflow_21881__$1.style.display = "inline-flex");

(overflow_21881__$1.style.marginInlineStart = margin_21887);
} else {
(overflow_21881__$1.textContent = "");

(overflow_21881__$1.style.display = "none");
}

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_model,m);

return null;
});
app.components.x_avatar_group.x_avatar_group.update_from_attrs_BANG_ = (function app$components$x_avatar_group$x_avatar_group$update_from_attrs_BANG_(el){
var new_m_21913 = app.components.x_avatar_group.x_avatar_group.read_model(el);
var old_m_21914 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21914,new_m_21913)){
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_(el,new_m_21913);
} else {
}

return null;
});
app.components.x_avatar_group.x_avatar_group.refresh_layout_BANG_ = (function app$components$x_avatar_group$x_avatar_group$refresh_layout_BANG_(el){
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_(el,app.components.x_avatar_group.x_avatar_group.read_model(el));

return null;
});
app.components.x_avatar_group.x_avatar_group.add_listeners_BANG_ = (function app$components$x_avatar_group$x_avatar_group$add_listeners_BANG_(el){
var map__21869_21915 = app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_(el);
var map__21869_21916__$1 = cljs.core.__destructure_map(map__21869_21915);
var slot_el_21917 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21869_21916__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var slot_el_21918__$1 = slot_el_21917;
var on_slot_21919 = (function (_){
return app.components.x_avatar_group.x_avatar_group.refresh_layout_BANG_(el);
});
if(cljs.core.truth_(slot_el_21918__$1)){
slot_el_21918__$1.addEventListener("slotchange",on_slot_21919);
} else {
}

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_handlers,({"slot": on_slot_21919}));

return null;
});
app.components.x_avatar_group.x_avatar_group.remove_listeners_BANG_ = (function app$components$x_avatar_group$x_avatar_group$remove_listeners_BANG_(el){
var temp__5823__auto___21921 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_handlers);
if(cljs.core.truth_(temp__5823__auto___21921)){
var hs_21922 = temp__5823__auto___21921;
var temp__5823__auto___21923__$1 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_refs);
if(cljs.core.truth_(temp__5823__auto___21923__$1)){
var refs_21924 = temp__5823__auto___21923__$1;
var slot_el_21925 = new cljs.core.Keyword(null,"slot-el","slot-el",1985374132).cljs$core$IFn$_invoke$arity$1(refs_21924);
var on_slot_21926 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(hs_21922,"slot");
if(cljs.core.truth_((function (){var and__5140__auto__ = slot_el_21925;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_21926;
} else {
return and__5140__auto__;
}
})())){
slot_el_21925.removeEventListener("slotchange",on_slot_21926);
} else {
}
} else {
}
} else {
}

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_handlers,null);

return null;
});
app.components.x_avatar_group.x_avatar_group.def_string_prop_BANG_ = (function app$components$x_avatar_group$x_avatar_group$def_string_prop_BANG_(proto,attr){
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
app.components.x_avatar_group.x_avatar_group.def_string_prop_default_BANG_ = (function app$components$x_avatar_group$x_avatar_group$def_string_prop_default_BANG_(proto,attr,default$){
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
app.components.x_avatar_group.x_avatar_group.def_bool_prop_BANG_ = (function app$components$x_avatar_group$x_avatar_group$def_bool_prop_BANG_(proto,attr){
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
app.components.x_avatar_group.x_avatar_group.def_int_prop_BANG_ = (function app$components$x_avatar_group$x_avatar_group$def_int_prop_BANG_(proto,attr){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return app.components.x_avatar_group.model.parse_max(this$.getAttribute(attr));
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_avatar_group.x_avatar_group.install_property_accessors_BANG_ = (function app$components$x_avatar_group$x_avatar_group$install_property_accessors_BANG_(proto){
app.components.x_avatar_group.x_avatar_group.def_string_prop_default_BANG_(proto,app.components.x_avatar_group.model.attr_size,app.components.x_avatar_group.model.default_size);

app.components.x_avatar_group.x_avatar_group.def_string_prop_default_BANG_(proto,app.components.x_avatar_group.model.attr_overlap,app.components.x_avatar_group.model.default_overlap);

app.components.x_avatar_group.x_avatar_group.def_string_prop_default_BANG_(proto,app.components.x_avatar_group.model.attr_direction,app.components.x_avatar_group.model.default_direction);

app.components.x_avatar_group.x_avatar_group.def_string_prop_BANG_(proto,app.components.x_avatar_group.model.attr_label);

app.components.x_avatar_group.x_avatar_group.def_bool_prop_BANG_(proto,app.components.x_avatar_group.model.attr_disabled);

return app.components.x_avatar_group.x_avatar_group.def_int_prop_BANG_(proto,app.components.x_avatar_group.model.attr_max);
});
app.components.x_avatar_group.x_avatar_group.element_class = (function app$components$x_avatar_group$x_avatar_group$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_avatar_group.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_(this$);

app.components.x_avatar_group.x_avatar_group.remove_listeners_BANG_(this$);

app.components.x_avatar_group.x_avatar_group.add_listeners_BANG_(this$);

app.components.x_avatar_group.x_avatar_group.refresh_layout_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_avatar_group.x_avatar_group.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (_attr,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_avatar_group.x_avatar_group.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_avatar_group.x_avatar_group.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_avatar_group.x_avatar_group.register_BANG_ = (function app$components$x_avatar_group$x_avatar_group$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_avatar_group.model.tag_name))){
} else {
customElements.define(app.components.x_avatar_group.model.tag_name,app.components.x_avatar_group.x_avatar_group.element_class());
}

return null;
});
app.components.x_avatar_group.x_avatar_group.init_BANG_ = (function app$components$x_avatar_group$x_avatar_group$init_BANG_(){
return app.components.x_avatar_group.x_avatar_group.register_BANG_();
});

//# sourceMappingURL=app.components.x_avatar_group.x_avatar_group.js.map
