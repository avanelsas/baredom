goog.provide('app.components.x_breadcrumbs.x_breadcrumbs');
goog.scope(function(){
  app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object = goog.module.get('goog.object');
});
app.components.x_breadcrumbs.x_breadcrumbs.k_refs = "__xBreadcrumbsRefs";
app.components.x_breadcrumbs.x_breadcrumbs.k_model = "__xBreadcrumbsModel";
app.components.x_breadcrumbs.x_breadcrumbs.k_handlers = "__xBreadcrumbsHandlers";
app.components.x_breadcrumbs.x_breadcrumbs.style_text = (""+":host{"+"display:block;"+"color-scheme:light dark;"+"--x-breadcrumbs-color:rgba(0,0,0,0.55);"+"--x-breadcrumbs-color-current:rgba(0,0,0,0.88);"+"--x-breadcrumbs-color-hover:rgba(0,0,0,0.80);"+"--x-breadcrumbs-separator-color:rgba(0,0,0,0.35);"+"--x-breadcrumbs-font-size:0.875rem;"+"--x-breadcrumbs-gap:0.25rem;"+"--x-breadcrumbs-disabled-opacity:0.55;}"+"@media (prefers-color-scheme:dark){"+":host{"+"--x-breadcrumbs-color:rgba(255,255,255,0.50);"+"--x-breadcrumbs-color-current:rgba(255,255,255,0.90);"+"--x-breadcrumbs-color-hover:rgba(255,255,255,0.75);"+"--x-breadcrumbs-separator-color:rgba(255,255,255,0.30);}}"+":host([data-size='sm']){--x-breadcrumbs-font-size:0.75rem;}"+":host([data-size='lg']){--x-breadcrumbs-font-size:1rem;}"+":host([disabled]){opacity:var(--x-breadcrumbs-disabled-opacity);pointer-events:none;}"+"[part=root]{"+"display:block;"+"font-size:var(--x-breadcrumbs-font-size);}"+"[part=list]{"+"display:flex;"+"flex-wrap:wrap;"+"align-items:center;"+"list-style:none;"+"margin:0;"+"padding:0;"+"gap:var(--x-breadcrumbs-gap);}"+":host([data-wrap='false']) [part=list]{flex-wrap:nowrap;}"+".crumb{"+"display:inline-flex;"+"align-items:center;"+"gap:var(--x-breadcrumbs-gap);}"+".crumb-content{"+"color:var(--x-breadcrumbs-color);"+"text-decoration:none;"+"cursor:pointer;}"+".crumb-content:hover{"+"color:var(--x-breadcrumbs-color-hover);}"+".crumb[data-current] .crumb-content{"+"color:var(--x-breadcrumbs-color-current);"+"font-weight:500;}"+":host([data-variant='subtle']) .crumb-content{"+"opacity:0.75;}"+":host([data-variant='text']) .crumb-content{"+"text-decoration:none;"+"border-bottom:none;}"+".separator{"+"color:var(--x-breadcrumbs-separator-color);"+"user-select:none;"+"aria-hidden:true;"+"flex-shrink:0;}"+".ellipsis{"+"display:inline-flex;"+"align-items:center;"+"gap:var(--x-breadcrumbs-gap);}"+".ellipsis-btn{"+"color:var(--x-breadcrumbs-color);"+"background:none;"+"border:none;"+"padding:0;"+"cursor:pointer;"+"font:inherit;"+"line-height:1;}"+".ellipsis-btn:hover{"+"color:var(--x-breadcrumbs-color-hover);}"+"slot{display:none;}"+"@media (prefers-reduced-motion:reduce){"+"[part=list]{transition:none !important;}}");
app.components.x_breadcrumbs.x_breadcrumbs.init_dom_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$init_dom_BANG_(el){
var root_21998 = el.attachShadow(({"mode": "open"}));
var style_21999 = document.createElement("style");
var nav_22000 = document.createElement("nav");
var ol_22001 = document.createElement("ol");
var slot_el_22002 = document.createElement("slot");
(style_21999.textContent = app.components.x_breadcrumbs.x_breadcrumbs.style_text);

nav_22000.setAttribute("part","root");

ol_22001.setAttribute("part","list");

nav_22000.appendChild(ol_22001);

root_21998.appendChild(style_21999);

root_21998.appendChild(nav_22000);

root_21998.appendChild(slot_el_22002);

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"nav","nav",719540477),nav_22000,new cljs.core.Keyword(null,"ol","ol",932524051),ol_22001,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132),slot_el_22002], null));

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$ensure_refs_BANG_(el){
var or__5142__auto__ = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
if(cljs.core.truth_(or__5142__auto__)){
return or__5142__auto__;
} else {
app.components.x_breadcrumbs.x_breadcrumbs.init_dom_BANG_(el);

return app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
}
});
app.components.x_breadcrumbs.x_breadcrumbs.read_model = (function app$components$x_breadcrumbs$x_breadcrumbs$read_model(el){
return app.components.x_breadcrumbs.model.normalize(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"variant-raw","variant-raw",1353213250),new cljs.core.Keyword(null,"max-items-raw","max-items-raw",-340487390),new cljs.core.Keyword(null,"disabled-present?","disabled-present?",-1165473496),new cljs.core.Keyword(null,"wrap-raw","wrap-raw",-1468030804),new cljs.core.Keyword(null,"items-after-raw","items-after-raw",-1677187220),new cljs.core.Keyword(null,"size-raw","size-raw",-2036217423),new cljs.core.Keyword(null,"preserve-aria-current-present?","preserve-aria-current-present?",729931506),new cljs.core.Keyword(null,"items-before-raw","items-before-raw",1580997624),new cljs.core.Keyword(null,"aria-label-raw","aria-label-raw",-412828103),new cljs.core.Keyword(null,"separator-raw","separator-raw",1358337914),new cljs.core.Keyword(null,"aria-describedby-raw","aria-describedby-raw",-1672877860)],[el.getAttribute(app.components.x_breadcrumbs.model.attr_variant),el.getAttribute(app.components.x_breadcrumbs.model.attr_max_items),el.hasAttribute(app.components.x_breadcrumbs.model.attr_disabled),el.getAttribute(app.components.x_breadcrumbs.model.attr_wrap),el.getAttribute(app.components.x_breadcrumbs.model.attr_items_after),el.getAttribute(app.components.x_breadcrumbs.model.attr_size),el.hasAttribute(app.components.x_breadcrumbs.model.attr_preserve_aria_current),el.getAttribute(app.components.x_breadcrumbs.model.attr_items_before),el.getAttribute(app.components.x_breadcrumbs.model.attr_aria_label),el.getAttribute(app.components.x_breadcrumbs.model.attr_separator),el.getAttribute(app.components.x_breadcrumbs.model.attr_aria_describedby)]));
});
app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_separator_el(_el,sep_text){
var span = document.createElement("span");
span.setAttribute("class","separator");

span.setAttribute("aria-hidden","true");

(span.textContent = sep_text);

return span;
});
app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_ellipsis_el(_el,sep_text){
var li = document.createElement("li");
var sep = document.createElement("span");
var btn = document.createElement("button");
li.setAttribute("class","ellipsis");

sep.setAttribute("class","separator");

sep.setAttribute("aria-hidden","true");

(sep.textContent = sep_text);

btn.setAttribute("class","ellipsis-btn");

btn.setAttribute("aria-label","Show hidden breadcrumbs");

(btn.textContent = "\u2026");

li.appendChild(sep);

li.appendChild(btn);

return li;
});
app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el = (function app$components$x_breadcrumbs$x_breadcrumbs$make_crumb_el(_el,source_child,current_QMARK_){
var li = document.createElement("li");
var wrapper = document.createElement("span");
var clone = source_child.cloneNode(true);
li.setAttribute("class","crumb");

wrapper.setAttribute("class","crumb-content");

wrapper.appendChild(clone);

li.appendChild(wrapper);

if(cljs.core.truth_(current_QMARK_)){
li.setAttribute("data-current","");

wrapper.setAttribute("aria-current","page");
} else {
}

return li;
});
app.components.x_breadcrumbs.x_breadcrumbs.rebuild_list_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$rebuild_list_BANG_(el,p__21962){
var map__21963 = p__21962;
var map__21963__$1 = cljs.core.__destructure_map(map__21963);
var _m = map__21963__$1;
var separator = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21963__$1,new cljs.core.Keyword(null,"separator","separator",-1628749125));
var max_items = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21963__$1,new cljs.core.Keyword(null,"max-items","max-items",-1969244147));
var items_before = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21963__$1,new cljs.core.Keyword(null,"items-before","items-before",-1151895719));
var items_after = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21963__$1,new cljs.core.Keyword(null,"items-after","items-after",358736705));
var preserve_aria_current = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21963__$1,new cljs.core.Keyword(null,"preserve-aria-current","preserve-aria-current",682773185));
var map__21964_22003 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21964_22004__$1 = cljs.core.__destructure_map(map__21964_22003);
var slot_el_22005 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21964_22004__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var ol_22006 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21964_22004__$1,new cljs.core.Keyword(null,"ol","ol",932524051));
var slot_el_22007__$1 = slot_el_22005;
var ol_22008__$1 = ol_22006;
var children_22009 = cljs.core.array_seq.cljs$core$IFn$_invoke$arity$1(slot_el_22007__$1.assignedElements());
var total_22010 = cljs.core.count(children_22009);
var plan_22011 = app.components.x_breadcrumbs.model.build_plan(total_22010,max_items,items_before,items_after);
var visible_22012 = new cljs.core.Keyword(null,"visible","visible",-1024216805).cljs$core$IFn$_invoke$arity$1(plan_22011);
var ellipsis_at_22013 = new cljs.core.Keyword(null,"ellipsis-at","ellipsis-at",-628372349).cljs$core$IFn$_invoke$arity$1(plan_22011);
(ol_22008__$1.innerHTML = "");

var seq__21965_22021 = cljs.core.seq(cljs.core.map_indexed.cljs$core$IFn$_invoke$arity$2(cljs.core.vector,visible_22012));
var chunk__21966_22022 = null;
var count__21967_22023 = (0);
var i__21968_22024 = (0);
while(true){
if((i__21968_22024 < count__21967_22023)){
var vec__21975_22025 = chunk__21966_22022.cljs$core$IIndexed$_nth$arity$2(null,i__21968_22024);
var pos_22026 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21975_22025,(0),null);
var idx_22027 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21975_22025,(1),null);
var child_22031 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(children_22009,idx_22027,null);
var last_QMARK__22032 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22026,(cljs.core.count(visible_22012) - (1)));
var current_QMARK__22033 = ((last_QMARK__22032) && (cljs.core.not(preserve_aria_current)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22026,ellipsis_at_22013)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

if((((pos_22026 > (0))) && ((!(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22026,ellipsis_at_22013)))))){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22026,ellipsis_at_22013)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core.truth_(child_22031)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el(el,child_22031,current_QMARK__22033));
} else {
}


var G__22034 = seq__21965_22021;
var G__22035 = chunk__21966_22022;
var G__22036 = count__21967_22023;
var G__22037 = (i__21968_22024 + (1));
seq__21965_22021 = G__22034;
chunk__21966_22022 = G__22035;
count__21967_22023 = G__22036;
i__21968_22024 = G__22037;
continue;
} else {
var temp__5823__auto___22038 = cljs.core.seq(seq__21965_22021);
if(temp__5823__auto___22038){
var seq__21965_22039__$1 = temp__5823__auto___22038;
if(cljs.core.chunked_seq_QMARK_(seq__21965_22039__$1)){
var c__5673__auto___22040 = cljs.core.chunk_first(seq__21965_22039__$1);
var G__22041 = cljs.core.chunk_rest(seq__21965_22039__$1);
var G__22042 = c__5673__auto___22040;
var G__22043 = cljs.core.count(c__5673__auto___22040);
var G__22044 = (0);
seq__21965_22021 = G__22041;
chunk__21966_22022 = G__22042;
count__21967_22023 = G__22043;
i__21968_22024 = G__22044;
continue;
} else {
var vec__21980_22045 = cljs.core.first(seq__21965_22039__$1);
var pos_22046 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21980_22045,(0),null);
var idx_22047 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__21980_22045,(1),null);
var child_22048 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(children_22009,idx_22047,null);
var last_QMARK__22049 = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22046,(cljs.core.count(visible_22012) - (1)));
var current_QMARK__22050 = ((last_QMARK__22049) && (cljs.core.not(preserve_aria_current)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22046,ellipsis_at_22013)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

if((((pos_22046 > (0))) && ((!(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22046,ellipsis_at_22013)))))){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(pos_22046,ellipsis_at_22013)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_separator_el(el,separator));
} else {
}

if(cljs.core.truth_(child_22048)){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_crumb_el(el,child_22048,current_QMARK__22050));
} else {
}


var G__22051 = cljs.core.next(seq__21965_22039__$1);
var G__22052 = null;
var G__22053 = (0);
var G__22054 = (0);
seq__21965_22021 = G__22051;
chunk__21966_22022 = G__22052;
count__21967_22023 = G__22053;
i__21968_22024 = G__22054;
continue;
}
} else {
}
}
break;
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(ellipsis_at_22013,cljs.core.count(visible_22012))){
ol_22008__$1.appendChild(app.components.x_breadcrumbs.x_breadcrumbs.make_ellipsis_el(el,separator));
} else {
}

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.apply_model_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$apply_model_BANG_(el,p__21988){
var map__21989 = p__21988;
var map__21989__$1 = cljs.core.__destructure_map(map__21989);
var m = map__21989__$1;
var size = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var variant = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"variant","variant",-424354234));
var wrap = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"wrap","wrap",851669987));
var disabled = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"disabled","disabled",-1529784218));
var aria_label = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"aria-label","aria-label",455891514));
var aria_describedby = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21989__$1,new cljs.core.Keyword(null,"aria-describedby","aria-describedby",1826540471));
var map__21990_22055 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21990_22056__$1 = cljs.core.__destructure_map(map__21990_22055);
var nav_22057 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21990_22056__$1,new cljs.core.Keyword(null,"nav","nav",719540477));
var nav_22058__$1 = nav_22057;
el.setAttribute("data-size",size);

el.setAttribute("data-variant",variant);

el.setAttribute("data-wrap",(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.boolean$(wrap))));

if(cljs.core.truth_(aria_label)){
nav_22058__$1.setAttribute("aria-label",aria_label);
} else {
nav_22058__$1.setAttribute("aria-label","Breadcrumb");
}

if(cljs.core.truth_(aria_describedby)){
nav_22058__$1.setAttribute("aria-describedby",aria_describedby);
} else {
nav_22058__$1.removeAttribute("aria-describedby");
}

app.components.x_breadcrumbs.x_breadcrumbs.rebuild_list_BANG_(el,m);

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_model,m);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$update_from_attrs_BANG_(el){
var new_m_22059 = app.components.x_breadcrumbs.x_breadcrumbs.read_model(el);
app.components.x_breadcrumbs.x_breadcrumbs.apply_model_BANG_(el,new_m_22059);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.add_listeners_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$add_listeners_BANG_(el){
var map__21991_22060 = app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(el);
var map__21991_22061__$1 = cljs.core.__destructure_map(map__21991_22060);
var slot_el_22062 = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__21991_22061__$1,new cljs.core.Keyword(null,"slot-el","slot-el",1985374132));
var slot_el_22063__$1 = slot_el_22062;
var on_slot_22064 = (function (_){
return app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(el);
});
if(cljs.core.truth_(slot_el_22063__$1)){
slot_el_22063__$1.addEventListener("slotchange",on_slot_22064);
} else {
}

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers,({"slot": on_slot_22064}));

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$remove_listeners_BANG_(el){
var temp__5823__auto___22065 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers);
if(cljs.core.truth_(temp__5823__auto___22065)){
var hs_22066 = temp__5823__auto___22065;
var temp__5823__auto___22067__$1 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(el,app.components.x_breadcrumbs.x_breadcrumbs.k_refs);
if(cljs.core.truth_(temp__5823__auto___22067__$1)){
var refs_22068 = temp__5823__auto___22067__$1;
var slot_el_22069 = new cljs.core.Keyword(null,"slot-el","slot-el",1985374132).cljs$core$IFn$_invoke$arity$1(refs_22068);
var on_slot_22070 = app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.get(hs_22066,"slot");
if(cljs.core.truth_((function (){var and__5140__auto__ = slot_el_22069;
if(cljs.core.truth_(and__5140__auto__)){
return on_slot_22070;
} else {
return and__5140__auto__;
}
})())){
slot_el_22069.removeEventListener("slotchange",on_slot_22070);
} else {
}
} else {
}
} else {
}

app.components.x_breadcrumbs.x_breadcrumbs.goog$module$goog$object.set(el,app.components.x_breadcrumbs.x_breadcrumbs.k_handlers,null);

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_string_prop_BANG_(proto,attr){
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
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_string_prop_default_BANG_(proto,attr,default$){
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
app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_bool_prop_BANG_(proto,attr){
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
app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$def_int_prop_BANG_(proto,attr,default$){
return Object.defineProperty(proto,attr,({"get": (function (){
var this$ = this;
return app.components.x_breadcrumbs.model.parse_pos_int(this$.getAttribute(attr),default$);
}), "set": (function (v){
var this$ = this;
if((!((v == null)))){
return this$.setAttribute(attr,(""+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v)));
} else {
return this$.removeAttribute(attr);
}
}), "enumerable": true, "configurable": true}));
});
app.components.x_breadcrumbs.x_breadcrumbs.install_property_accessors_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$install_property_accessors_BANG_(proto){
app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_separator,app.components.x_breadcrumbs.model.default_separator);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_size,app.components.x_breadcrumbs.model.default_size);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_default_BANG_(proto,app.components.x_breadcrumbs.model.attr_variant,app.components.x_breadcrumbs.model.default_variant);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_wrap);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_disabled);

app.components.x_breadcrumbs.x_breadcrumbs.def_bool_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_preserve_aria_current);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_max_items,null);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_items_before,app.components.x_breadcrumbs.model.default_items_before);

app.components.x_breadcrumbs.x_breadcrumbs.def_int_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_items_after,app.components.x_breadcrumbs.model.default_items_after);

app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_aria_label);

return app.components.x_breadcrumbs.x_breadcrumbs.def_string_prop_BANG_(proto,app.components.x_breadcrumbs.model.attr_aria_describedby);
});
app.components.x_breadcrumbs.x_breadcrumbs.element_class = (function app$components$x_breadcrumbs$x_breadcrumbs$element_class(){
var klass = (class extends HTMLElement {});
(klass.observedAttributes = app.components.x_breadcrumbs.model.observed_attributes);

(klass.prototype.connectedCallback = (function (){
var this$ = this;
app.components.x_breadcrumbs.x_breadcrumbs.ensure_refs_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.add_listeners_BANG_(this$);

app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(this$);

return null;
}));

(klass.prototype.disconnectedCallback = (function (){
var this$ = this;
app.components.x_breadcrumbs.x_breadcrumbs.remove_listeners_BANG_(this$);

return null;
}));

(klass.prototype.attributeChangedCallback = (function (attr_name,old_val,new_val){
var this$ = this;
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(old_val,new_val)){
app.components.x_breadcrumbs.x_breadcrumbs.update_from_attrs_BANG_(this$);
} else {
}

return null;
}));

app.components.x_breadcrumbs.x_breadcrumbs.install_property_accessors_BANG_(klass.prototype);

return klass;
});
app.components.x_breadcrumbs.x_breadcrumbs.register_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$register_BANG_(){
if(cljs.core.truth_(customElements.get(app.components.x_breadcrumbs.model.tag_name))){
} else {
customElements.define(app.components.x_breadcrumbs.model.tag_name,app.components.x_breadcrumbs.x_breadcrumbs.element_class());
}

return null;
});
app.components.x_breadcrumbs.x_breadcrumbs.init_BANG_ = (function app$components$x_breadcrumbs$x_breadcrumbs$init_BANG_(){
return app.components.x_breadcrumbs.x_breadcrumbs.register_BANG_();
});

//# sourceMappingURL=app.components.x_breadcrumbs.x_breadcrumbs.js.map
