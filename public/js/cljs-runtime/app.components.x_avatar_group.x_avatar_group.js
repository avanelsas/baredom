goog.provide('app.components.x_avatar_group.x_avatar_group');
goog.scope(function(){
  app.components.x_avatar_group.x_avatar_group.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_avatar_group.x_avatar_group.k_refs = "__xAvatarGroupRefs";
app.components.x_avatar_group.x_avatar_group.k_model = "__xAvatarGroupModel";
app.components.x_avatar_group.x_avatar_group.k_handlers = "__xAvatarGroupHandlers";
app.components.x_avatar_group.x_avatar_group.style_text = (""+":host{"+"display:inline-flex;"+"align-items:center;"+"flex-direction:row;"+"color-scheme:light dark;"+"--x-avatar-group-overflow-bg:rgba(0,0,0,0.08);"+"--x-avatar-group-overflow-color:rgba(0,0,0,0.70);"+"--x-avatar-group-overflow-border:rgba(0,0,0,0.14);"+"--x-avatar-group-overflow-ring:#ffffff;"+"--x-avatar-group-size:32px;"+"--x-avatar-group-font-size:0.75rem;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-avatar-group-overflow-bg:rgba(255,255,255,0.10);"+"--x-avatar-group-overflow-color:rgba(255,255,255,0.75);"+"--x-avatar-group-overflow-border:rgba(255,255,255,0.18);"+"--x-avatar-group-overflow-ring:#0b0c0f;}}"+":host([data-size='xs']){--x-avatar-group-size:20px;--x-avatar-group-font-size:0.625rem;}"+":host([data-size='sm']){--x-avatar-group-size:24px;--x-avatar-group-font-size:0.6875rem;}"+":host([data-size='md']){--x-avatar-group-size:32px;--x-avatar-group-font-size:0.75rem;}"+":host([data-size='lg']){--x-avatar-group-size:40px;--x-avatar-group-font-size:0.875rem;}"+":host([data-size='xl']){--x-avatar-group-size:48px;--x-avatar-group-font-size:1rem;}"+":host([data-direction='rtl']){flex-direction:row-reverse;}"+"[part=overflow]{"+"display:none;"+"flex-shrink:0;"+"width:var(--x-avatar-group-size);"+"height:var(--x-avatar-group-size);"+"border-radius:999px;"+"background:var(--x-avatar-group-overflow-bg);"+"color:var(--x-avatar-group-overflow-color);"+"border:1px solid var(--x-avatar-group-overflow-border);"+"box-shadow:0 0 0 2px var(--x-avatar-group-overflow-ring);"+"font-size:var(--x-avatar-group-font-size);"+"font-weight:600;"+"line-height:1;"+"align-items:center;"+"justify-content:center;"+"user-select:none;"+"box-sizing:border-box;"+"white-space:nowrap;}"+"::slotted(x-avatar){flex-shrink:0;}"+"@media (prefers-reduced-motion:reduce){"+"::slotted(x-avatar){transition:none !important;}}");
app.components.x_avatar_group.x_avatar_group.init_dom_BANG_ = (function app$components$x_avatar_group$x_avatar_group$init_dom_BANG_(el){
var root_21828 = el.attachShadow(({"mode": "open"}));
var style_21829 = document.createElement("style");
var slot_el_21830 = document.createElement("slot");
var overflow_21831 = document.createElement("span");
(style_21829.textContent = app.components.x_avatar_group.x_avatar_group.style_text);

overflow_21831.setAttribute("part","overflow");

overflow_21831.setAttribute("aria-hidden","true");

root_21828.appendChild(style_21829);

root_21828.appendChild(slot_el_21830);

root_21828.appendChild(overflow_21831);

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_refs,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_21830,new cljs.core.Keyword(null,"overflow","overflow",2058931880),overflow_21831], null));

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
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_ = (function app$components$x_avatar_group$x_avatar_group$apply_layout_BANG_(el,p__21757){
var map__21758 = p__21757;
var map__21758__$1 = cljs.core.__destructure_map(map__21758);
var m = map__21758__$1;
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var overlap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"overlap","overlap",-1673335644));
var max = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"max","max",61366548));
var direction = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"direction","direction",-633359395));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21758__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var map__21759_21877 = app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_(el);
var map__21759_21878__$1 = cljs.core.__destructure_map(map__21759_21877);
var slot_el_21879 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21759_21878__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var overflow_21880 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21759_21878__$1,new cljs.core.Keyword(null,"overflow","overflow",2058931880));
var slot_el_21881__$1 = slot_el_21879;
var overflow_21882__$1 = overflow_21880;
var children_21883 = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(slot_el_21881__$1.assignedElements());
var total_21884 = cljs.core.count(children_21883);
var vec__21760_21885 = app.components.x_avatar_group.model.compute_visible_hidden(total_21884,max);
var vis_count_21886 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21760_21885,(0),null);
var hidden_count_21887 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21760_21885,(1),null);
var margin_21888 = cljs.core.get.cljs$core$IFn$_invoke$arity$3(app.components.x_avatar_group.model.overlap_margin,overlap,"0px");
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

var seq__21767_21890 = cljs.core.seq(cljs.core.map.cljs$core$IFn$_invoke$arity$3(cljs.core.vector,children_21883,cljs.core.range.cljs$core$IFn$_invoke$arity$0()));
var chunk__21768_21891 = null;
var count__21769_21892 = (0);
var i__21770_21893 = (0);
while(true){
if((i__21770_21893 < count__21769_21892)){
var vec__21785_21894 = chunk__21768_21891.cljs$core$IIndexed$_nth$arity$2(null,i__21770_21893);
var child_21895 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21785_21894,(0),null);
var idx_21896 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21785_21894,(1),null);
if((idx_21896 < vis_count_21886)){
(child_21895.style.display = "");

child_21895.setAttribute("size",size);

if(cljs.core.truth_(disabled)){
child_21895.setAttribute("disabled","");
} else {
child_21895.removeAttribute("disabled");
}

(child_21895.style.marginInlineStart = (((idx_21896 > (0)))?margin_21888:"0px"));
} else {
(child_21895.style.display = "none");
}


var G__21897 = seq__21767_21890;
var G__21898 = chunk__21768_21891;
var G__21899 = count__21769_21892;
var G__21900 = (i__21770_21893 + (1));
seq__21767_21890 = G__21897;
chunk__21768_21891 = G__21898;
count__21769_21892 = G__21899;
i__21770_21893 = G__21900;
continue;
} else {
var temp__5823__auto___21901 = cljs.core.seq(seq__21767_21890);
if(temp__5823__auto___21901){
var seq__21767_21902__$1 = temp__5823__auto___21901;
if(cljs.core.chunked_seq_QMARK_(seq__21767_21902__$1)){
var c__5673__auto___21903 = cljs.core.chunk_first(seq__21767_21902__$1);
var G__21905 = cljs.core.chunk_rest(seq__21767_21902__$1);
var G__21906 = c__5673__auto___21903;
var G__21907 = cljs.core.count(c__5673__auto___21903);
var G__21908 = (0);
seq__21767_21890 = G__21905;
chunk__21768_21891 = G__21906;
count__21769_21892 = G__21907;
i__21770_21893 = G__21908;
continue;
} else {
var vec__21795_21910 = cljs.core.first(seq__21767_21902__$1);
var child_21911 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21795_21910,(0),null);
var idx_21912 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21795_21910,(1),null);
if((idx_21912 < vis_count_21886)){
(child_21911.style.display = "");

child_21911.setAttribute("size",size);

if(cljs.core.truth_(disabled)){
child_21911.setAttribute("disabled","");
} else {
child_21911.removeAttribute("disabled");
}

(child_21911.style.marginInlineStart = (((idx_21912 > (0)))?margin_21888:"0px"));
} else {
(child_21911.style.display = "none");
}


var G__21913 = cljs.core.next(seq__21767_21902__$1);
var G__21914 = null;
var G__21915 = (0);
var G__21916 = (0);
seq__21767_21890 = G__21913;
chunk__21768_21891 = G__21914;
count__21769_21892 = G__21915;
i__21770_21893 = G__21916;
continue;
}
} else {
}
}
break;
}

if((hidden_count_21887 > (0))){
(overflow_21882__$1.textContent = (""+"+"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(hidden_count_21887)));

overflow_21882__$1.setAttribute("role","img");

overflow_21882__$1.setAttribute("aria-label",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(hidden_count_21887)+" more"));

(overflow_21882__$1.style.display = "inline-flex");

(overflow_21882__$1.style.marginInlineStart = margin_21888);
} else {
(overflow_21882__$1.textContent = "");

(overflow_21882__$1.style.display = "none");
}

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_model,m);

return null;
});
app.components.x_avatar_group.x_avatar_group.update_from_attrs_BANG_ = (function app$components$x_avatar_group$x_avatar_group$update_from_attrs_BANG_(el){
var new_m_21918 = app.components.x_avatar_group.x_avatar_group.read_model(el);
var old_m_21919 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_model);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_m_21919,new_m_21918)){
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_(el,new_m_21918);
} else {
}

return null;
});
app.components.x_avatar_group.x_avatar_group.refresh_layout_BANG_ = (function app$components$x_avatar_group$x_avatar_group$refresh_layout_BANG_(el){
app.components.x_avatar_group.x_avatar_group.apply_layout_BANG_(el,app.components.x_avatar_group.x_avatar_group.read_model(el));

return null;
});
app.components.x_avatar_group.x_avatar_group.add_listeners_BANG_ = (function app$components$x_avatar_group$x_avatar_group$add_listeners_BANG_(el){
var map__21807_21920 = app.components.x_avatar_group.x_avatar_group.ensure_refs_BANG_(el);
var map__21807_21921__$1 = cljs.core.__destructure_map(map__21807_21920);
var slot_el_21922 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21807_21921__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var slot_el_21923__$1 = slot_el_21922;
var on_slot_21924 = (function (_){
return app.components.x_avatar_group.x_avatar_group.refresh_layout_BANG_(el);
});
if(cljs.core.truth_(slot_el_21923__$1)){
slot_el_21923__$1.addEventListener("slotchange",on_slot_21924);
} else {
}

app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.set(el,app.components.x_avatar_group.x_avatar_group.k_handlers,({"slot": on_slot_21924}));

return null;
});
app.components.x_avatar_group.x_avatar_group.remove_listeners_BANG_ = (function app$components$x_avatar_group$x_avatar_group$remove_listeners_BANG_(el){
var temp__5823__auto___21925 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_handlers);
if(cljs.core.truth_(temp__5823__auto___21925)){
var hs_21926 = temp__5823__auto___21925;
var temp__5823__auto___21927__$1 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(el,app.components.x_avatar_group.x_avatar_group.k_refs);
if(cljs.core.truth_(temp__5823__auto___21927__$1)){
var refs_21928 = temp__5823__auto___21927__$1;
var slot_el_21929 = new cljs.core.Keyword(null,"slot-el","slot-el",1985374132).cljs$core$IFn$_invoke$arity$1(refs_21928);
var on_slot_21930 = app.components.x_avatar_group.x_avatar_group.goog$module$goog$object.get(hs_21926,"slot");
if(cljs.core.truth_((function (){var and__5140__auto__ = slot_el_21929;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_21930;
} else {
return and__5140__auto__;
}
})())){
slot_el_21929.removeEventListener("slotchange",on_slot_21930);
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
